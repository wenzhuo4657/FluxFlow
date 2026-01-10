package cn.wenzhuo4657.noifiterBot.app.apiTest.scripts;

import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.CacheStrategy;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.RedisStrategy;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.InputStreamReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LuaTest {

    @Autowired
    private GlobalCache globalCache;

    private CacheStrategy cacheStrategy;

    @BeforeEach
    void setUp() {
        // 确保使用Redis策略
        if (globalCache.getCurrentCacheStrategy() instanceof RedisStrategy) {
            cacheStrategy = globalCache.getCurrentCacheStrategy();
        } else {
            // 切换到Redis策略
            globalCache.switchCacheStrategy(CacheType.REDIS);
            cacheStrategy = globalCache.getCurrentCacheStrategy();
        }

        // 清理测试环境
        if (cacheStrategy != null && cacheStrategy.isAvailable()) {
            cacheStrategy.flushAll();
        }
    }





    /**
     * 参数有效性测试
     * @throws IOException
     */
    @Test
    void testQpsMaxScriptWithInvalidParameters() throws IOException {
        if (!cacheStrategy.isAvailable()) {
            System.out.println("Redis不可用，跳过测试");
            return;
        }

        // 从文件中读取QPS限制脚本
        ClassPathResource resource = new ClassPathResource("scripts/qps_MAX.lua");
        String qpsScript = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

        // 测试无效参数：max_qps为0或负数
        assertThrows(RuntimeException.class, () -> {
            cacheStrategy.executeLuaScript(qpsScript,
                    Arrays.asList("test:invalid"), Arrays.asList("0"), List.class);
        });

        assertThrows(RuntimeException.class, () -> {
            cacheStrategy.executeLuaScript(qpsScript,
                    Arrays.asList("test:invalid"), Arrays.asList("-1"), List.class);
        });

        // 测试正确参数 - 使用新的JSON返回格式
        assertDoesNotThrow(() -> {
            QpsResponse result = cacheStrategy.executeLuaScript(qpsScript,
                    Arrays.asList("test:valid"), Arrays.asList(100), QpsResponse.class);

            assertNotNull(result);
            assertEquals(1, result.getStatus()); // 应该成功
            assertEquals("new_created", result.getMessage()); // 应该是新建键
            System.out.println("✅ QPS限制脚本测试成功: " + result);
        });
    }

    /**
     * qps最大值功能性测试
     * @throws IOException
     */

    @Test
    void testQpsMaxScriptWithSha1() throws IOException {
        if (!cacheStrategy.isAvailable()) {
            System.out.println("Redis不可用，跳过测试");
            return;
        }

        // 从文件中读取QPS限制脚本
        ClassPathResource resource = new ClassPathResource("scripts/qps_MAX.lua");
        String qpsScript = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

        // 预加载脚本并获取SHA1
        String scriptSha1 = cacheStrategy.loadLuaScript(qpsScript);
        assertNotNull(scriptSha1);
        assertEquals(40, scriptSha1.length()); // SHA1哈希长度

        System.out.println("✅ QPS限制脚本已预加载，SHA1: " + scriptSha1);

        // 使用SHA1执行脚本
        String qpsKey = "test:qps:sha1";
        int maxQps = 3;

        // 清理可能存在的键
        cacheStrategy.delete(qpsKey);

        // 使用SHA1执行测试
        QpsResponse result = cacheStrategy.executeLuaScript(qpsScript,
                Arrays.asList("test:valid"), Arrays.asList(maxQps), QpsResponse.class);

        assertNotNull(result);
        assertNotNull(result);
        assertEquals(1, result.getStatus()); // 应该成功
        assertEquals("new_created", result.getMessage()); // 应该是新建键
        System.out.println("✅ QPS限制脚本测试成功: " + result);

        System.out.println("✅ 使用SHA1执行QPS限制脚本成功: " + result);
    }

    @Test
    void testQpsMaxScriptPerformance() throws IOException {
        if (!cacheStrategy.isAvailable()) {
            System.out.println("Redis不可用，跳过测试");
            return;
        }

        // 从文件中读取QPS限制脚本
        ClassPathResource resource = new ClassPathResource("scripts/qps_MAX.lua");
        String qpsScript = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        String scriptSha1 = cacheStrategy.loadLuaScript(qpsScript);

        String qpsKey = "test:qps:performance";
        int maxQps = 30;

        // 清理可能存在的键
        cacheStrategy.delete(qpsKey);

        long startTime = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        int maxqps=0;
        // 快速发送100个请求

        for (int i = 1; i <= 100; i++) {
            try {
                QpsResponse result = cacheStrategy.executeLuaScriptSha1(scriptSha1,
                        Arrays.asList("test:valid"), Arrays.asList(maxQps), QpsResponse.class);

                if (result.getStatus() == 1) {
                    successCount++;
                } else {
                    System.out.println("❌ 请求失败，错误信息: " + result.getMessage());
                    failCount++;
                }
                if (result.getCurrent_qps()>=maxqps){
                    maxqps=result.getCurrent_qps();
                }
            } catch (Exception e) {
                System.out.println("❌ 请求失败，异常信息: " + e.getMessage());
                failCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✅ QPS限制脚本性能测试完成:");
        System.out.println("   总请求数: 100");
        System.out.println("   实际最大qps："+maxqps);
        System.out.println("   成功请求: " + successCount);
        System.out.println("   失败请求: " + failCount);
        System.out.println("   执行时间: " + duration + "ms");
        System.out.println("   平均响应时间: " + (duration / 100.0) + "ms/请求");

        // 验证成功请求不超过QPS限制
        assertTrue(failCount > 0); // 应该有失败的请求
    }
}