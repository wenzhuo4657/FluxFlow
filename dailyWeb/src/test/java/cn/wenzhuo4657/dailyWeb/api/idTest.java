package cn.wenzhuo4657.dailyWeb.api;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadLocalRandom;

/**
 * MDC Trace ID生成测试类
 * 推荐用于MDC日志追踪的唯一标识生成
 */
@SpringBootTest
public class idTest {

    private static final Logger log = LoggerFactory.getLogger(idTest.class);

    /**
     * 生成MDC专用Trace ID的工具方法
     * 格式: {时间戳后8位}-{机器标识后2位}-{随机数4位}
     * 示例: 2a8f1c3e-A1-7f92
     *
     * 推荐原因:
     * 1. 包含时间信息，便于日志时间线分析
     * 2. 适中的长度，不会过长影响日志可读性
     * 3. 生成速度快，不影响业务性能
     * 4. 包含机器标识，分布式环境下可追踪来源
     * 5. 随机部分保证唯一性，避免冲突
     */
    public static String generateMdcTraceId() {
        // 获取时间戳后8位的16进制表示
        long timestamp = System.currentTimeMillis();
        String timeHex = Long.toHexString(timestamp).substring(Math.max(0, Long.toHexString(timestamp).length() - 8));

        // 获取机器标识（可以用IP地址、主机名等的hash后两位）
        String machineId = getMachineIdentifier();

        // 生成4位随机数
        String random = Integer.toHexString(ThreadLocalRandom.current().nextInt(0x10000, 0xFFFFF));

        return String.format("%s-%s-%s", timeHex, machineId, random);
    }

    /**
     * 获取机器标识
     * 可以根据实际情况修改这个方法
     */
    private static String getMachineIdentifier() {
        try {
            // 方案1: 使用本地IP地址的hash
            String localHost = java.net.InetAddress.getLocalHost().getHostAddress();
            int hash = Math.abs(localHost.hashCode());
            return Integer.toHexString(hash).substring(Math.max(0, Integer.toHexString(hash).length() - 2));

            // 方案2: 使用主机名（取消下面的注释并注释上面的IP方案）
            // String hostName = java.net.InetAddress.getLocalHost().getHostName();
            // int hostHash = Math.abs(hostName.hashCode());
            // return Integer.toHexString(hostHash).substring(Math.max(0, Integer.toHexString(hostHash).length() - 2));

            // 方案3: 使用环境变量或配置文件中的机器标识
            // return System.getProperty("machine.id", "A1");

        } catch (Exception e) {
            // 如果获取失败，使用默认值
            return "A1";
        }
    }

    /**
     * 测试MDC Trace ID生成
     */
    @Test
    public void testMdcTraceId() {
        String traceId = generateMdcTraceId();
        System.out.println("MDC Trace ID: " + traceId);
        System.out.println("长度: " + traceId.length());
        log.info("MDC Trace ID: {}", traceId);
    }

    /**
     * 演示MDC使用的测试方法
     */
    @Test
    public void demonstrateMdcUsage() {
        // 模拟在Controller中设置MDC
        String traceId = generateMdcTraceId();

        // 在实际应用中，你会这样使用：
        // MDC.put("traceId", traceId);

        System.out.println("=== MDC使用演示 ===");
        System.out.println("生成的Trace ID: " + traceId);
        System.out.println();
        System.out.println("日志输出示例:");
        System.out.println("[INFO] " + traceId + " - 用户请求开始");
        System.out.println("[DEBUG] " + traceId + " - 执行业务逻辑");
        System.out.println("[INFO] " + traceId + " - 数据库查询完成");
        System.out.println("[INFO] " + traceId + " - 响应返回成功");
        System.out.println();

        // 在请求结束时清理MDC
        // MDC.remove("traceId");

        System.out.println("Trace ID构成分析:");
        String[] parts = traceId.split("-");
        System.out.println("- 时间部分: " + parts[0] + " (便于时间排序)");
        System.out.println("- 机器标识: " + parts[1] + " (便于分布式追踪)");
        System.out.println("- 随机部分: " + parts[2] + " (保证唯一性)");
    }


}
