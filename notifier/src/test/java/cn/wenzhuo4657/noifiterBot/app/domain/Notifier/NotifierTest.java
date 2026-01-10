package cn.wenzhuo4657.noifiterBot.app.domain.Notifier;

import cn.wenzhuo4657.noifiterBot.app.apiTest.scripts.QpsResponse;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsMaxDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.EmailNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NotifierTest {

    private static final Logger log = LoggerFactory.getLogger(NotifierTest.class);


    @Autowired
    private GlobalCache globalCache;

    /**
     * 通知器核心功能测试
     * 通知功能
     */
    @Test
    public  void testNotifyCore() throws Exception {
        log.info("邮箱通知器测试");
        GmailConfig gmailConfig=new GmailConfig();
        gmailConfig.setFrom("wenzhuo4657@gmail.com");
        gmailConfig.setTo("wenzhuo4657@gmail.com");
        gmailConfig.setPassword(System.getenv("GMAIL_PASSWORD"));
        EmailNotifier emailNotifier = new EmailNotifier(gmailConfig);

        NotifierMessage notifierMessage = new NotifierMessage();
        notifierMessage.setTitle("test");
        notifierMessage.setContent("test");
        long startTime = System.currentTimeMillis();
        NotifierResult send = emailNotifier.send(notifierMessage);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("   执行时间: " + duration + "ms");
        log.info("send result:{}", JSONObject.toJSONString(send));

        log.info("tgBot通知器测试");
        TgBotConfig tgBotConfig=new TgBotConfig();
        tgBotConfig.setBotToken(System.getenv("tgBot"));
        TgBotNotifier tgBotNotifier = new TgBotNotifier(tgBotConfig);
        TgBotNotifierMessage message=new TgBotNotifierMessage();
        message.setChatId("6550266873");
        message.setTitle("test");
        message.setContent("test");
        startTime = System.currentTimeMillis();
        tgBotNotifier.send(message);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("   执行时间: " + duration + "ms");
        log.info("send result:{}", JSONObject.toJSONString(send));
    }


    /**
     * 通知器功能测试
     * qps(包装器）+通知功能
     * 使用线程池进行并行测试
     */
    @Test
    public  void testNotify() throws InterruptedException {
        int sum =1000;//并发测试数量
//        1，初始化实例
        TgBotConfig tgBotConfig=new TgBotConfig();
        tgBotConfig.setBotToken(System.getenv("tgBot"));
        TgBotNotifier tgBotNotifier = new TgBotNotifier(tgBotConfig);
        QpsMaxDecorator qpsMaxDecorator=new QpsMaxDecorator(tgBotNotifier,globalCache);

        // 2，设置线程池和计数器
        ExecutorService executorService = Executors.newFixedThreadPool(sum);
        CountDownLatch latch = new CountDownLatch(sum);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

//        3，qps并行测试
        long startTime = System.currentTimeMillis();
        log.info("开始QPS限制脚本并行性能测试，100个线程同时发送请求");

        for (int i = 1; i <= sum;i++ ) {
            final int requestId = i;
            executorService.submit(() -> {
                try {
                    // 为每个线程创建独立的消息对象
                    TgBotNotifierMessage message = new TgBotNotifierMessage();
                    message.setChatId("6550266873");
                    message.setTitle("并行测试");
                    message.setContent("第"+requestId+"封并发请求");

                    QpsResult result = qpsMaxDecorator.send(message);

                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                        log.info("✅ 线程{}请求成功，当前qps： {}", requestId, result.getQpsResponse().getCurrent_qps());
                    } else {
                        failCount.incrementAndGet();
                        log.error("❌ 线程{}请求失败，错误信息： {}", requestId, result.getQpsResponse().getMessage());
                    }

                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("❌ 线程{}请求失败，异常信息: {}", requestId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            Thread.sleep(10);
        }

        // 等待所有线程完成
        latch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✅ QPS限制脚本并行性能测试完成:");
        System.out.println("   总请求数: "+sum);
        System.out.println("   成功请求: " + successCount.get());
        System.out.println("   失败请求: " + failCount.get());
        System.out.println("   执行时间: " + duration + "ms");
        System.out.println("   平均响应时间: " + (duration / 100.0) + "ms/请求");
        System.out.println("   实际QPS: " + (successCount.get() * 1000.0 / duration));

        // 验证成功请求不超过QPS限制
        assertTrue(failCount.get() > 0, "应该有失败的请求以验证QPS限制生效");
    }
}
