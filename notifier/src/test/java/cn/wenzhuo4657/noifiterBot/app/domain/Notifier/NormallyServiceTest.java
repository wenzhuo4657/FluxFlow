package cn.wenzhuo4657.noifiterBot.app.domain.Notifier;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.INotifierService;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通知器服务测试类
 * 测试通知器的注册、发送、查询等功能
 */
@SpringBootTest
public class NormallyServiceTest {

    @Autowired
    private INotifierService normallyService;

    private String[] decorators;

    @BeforeEach
    void setUp() {
        decorators = new String[]{};
    }

    // ============ 注册通信器测试 ============

    @Test
    @DisplayName("测试注册Telegram Bot通信器")
    @EnabledIfEnvironmentVariable(named = "tgBot", matches = ".+")
    void testRegisterTgBotCommunicator() {
        // Given
        String botToken = System.getenv("tgBot");

        // When
        long index = normallyService.registerTgBotCommunicator(botToken, decorators);

        // Then
        assertTrue(index > 0, "通信器索引应该大于0");
        System.out.println("Telegram Bot通信器注册成功，索引: " + index);
    }

    @Test
    @DisplayName("测试注册Gmail通信器")
    @EnabledIfEnvironmentVariable(named = "GMAIL_PASSWORD", matches = ".+")
    void testRegisterGmailCommunicator() {
        // Given
        String from = "wenzhuo4657@gmail.com";
        String password = System.getenv("GMAIL_PASSWORD");
        String to = "wenzhuo4657@gmail.com";

        // When
        long index = normallyService.registerGmailCommunicator(from, password, to, decorators);

        // Then
        assertTrue(index > 0, "通信器索引应该大于0");
        System.out.println("Gmail通信器注册成功，索引: " + index);
    }

    // ============ 发送信息测试 ============

    @Test
    @DisplayName("测试发送Telegram Bot消息")
    @EnabledIfEnvironmentVariable(named = "tgBot", matches = ".+")
    void testSendTgBotMessage() {
        // Given
        String botToken = System.getenv("tgBot");
        long index = normallyService.registerTgBotCommunicator(botToken, decorators);
        String title = "测试标题";
        String content = "这是一条测试消息";
        String chatId = "6550266873";

        // When
        boolean result = normallyService.sendTgBotMessage(index, title, content, chatId);

        // Then
        assertTrue(result, "消息应该发送成功");
        System.out.println("Telegram Bot消息发送成功");
    }

    @Test
    @DisplayName("测试发送Gmail邮件")
    @EnabledIfEnvironmentVariable(named = "GMAIL_PASSWORD", matches = ".+")
    void testSendGmail() {
        // Given
        String from = "wenzhuo4657@gmail.com";
        String password = System.getenv("GMAIL_PASSWORD");
        String to = "wenzhuo4657@gmail.com";
        long index = normallyService.registerGmailCommunicator(from, password, to, decorators);
        String title = "测试邮件";
        String content = "这是一封测试邮件";

        // When
        boolean result = normallyService.sendGmail(index, title, content, null);

        // Then
        assertTrue(result, "邮件应该发送成功");
        System.out.println("Gmail邮件发送成功");
    }

    @Test
    @DisplayName("测试发送带附件的Gmail邮件")
    @EnabledIfEnvironmentVariable(named = "GMAIL_PASSWORD", matches = ".+")
    void testSendGmailWithFile() {
        // Given
        String from = "wenzhuo4657@gmail.com";
        String password = System.getenv("GMAIL_PASSWORD");
        String to = "wenzhuo4657@gmail.com";
        long index = normallyService.registerGmailCommunicator(from, password, to, decorators);
        String title = "测试带附件的邮件";
        String content = "这是一封带附件的测试邮件";
        File file = new File("C:\\working\\my-project\\dailyWeb-back\\README.md");

        // When
        boolean result = normallyService.sendGmailWithFile(index, title, content, file);

        // Then
        assertTrue(result, "带附件的邮件应该发送成功");
        System.out.println("带附件的Gmail邮件发送成功");
    }

    // ============ 查询接口测试 ============

    @Test
    @DisplayName("测试检查通信器状态")
    @EnabledIfEnvironmentVariable(named = "tgBot", matches = ".+")
    void testCheckCommunicatorStatus() {
        // Given
        String botToken = System.getenv("tgBot");
        long index = normallyService.registerTgBotCommunicator(botToken, decorators);

        // When
        boolean status = normallyService.checkCommunicatorStatus(index);

        // Then
        assertTrue(status, "通信器状态应该正常");
        System.out.println("通信器状态检查: " + (status ? "正常" : "异常"));
    }

    @Test
    @DisplayName("测试查询支持的通知器")
    void testQuerySupportNotifier() {
        // When
        Map<String, String> notifiers = normallyService.querySupportNotifier();

        // Then
        assertNotNull(notifiers, "返回的通知器列表不应为null");
        assertFalse(notifiers.isEmpty(), "应该至少有一个支持的通知器");
        assertTrue(notifiers.containsKey("gmail"), "应该包含Gmail通知器");
        assertTrue(notifiers.containsKey("tgBot"), "应该包含Telegram Bot通知器");

        System.out.println("支持的通知器:");
        notifiers.forEach((key, value) -> System.out.println("  - " + key + ": " + value));
    }

    @Test
    @DisplayName("测试查询支持的装饰器")
    void testQuerySupportDecorator() {
        // When
        Map<String, String> decorators = normallyService.querySupportDecorator();

        // Then
        assertNotNull(decorators, "返回的装饰器列表不应为null");
        assertFalse(decorators.isEmpty(), "应该至少有一个支持的装饰器");
        assertTrue(decorators.containsKey("qps"), "应该包含QPS限流装饰器");

        System.out.println("支持的装饰器:");
        decorators.forEach((key, value) -> System.out.println("  - " + key + ": " + value));
    }

    // ============ 综合测试 ============

    @Test
    @DisplayName("综合测试：完整流程")
    @EnabledIfEnvironmentVariable(named = "tgBot", matches = ".+")
    void testCompleteWorkflow() {
        System.out.println("=== 开始综合测试 ===");

        // 1. 查询支持的通知器
        System.out.println("\n1. 查询支持的通知器");
        Map<String, String> notifiers = normallyService.querySupportNotifier();
        assertNotNull(notifiers);
        System.out.println("支持的通知器数量: " + notifiers.size());

        // 2. 查询支持的装饰器
        System.out.println("\n2. 查询支持的装饰器");
        Map<String, String> decorators = normallyService.querySupportDecorator();
        assertNotNull(decorators);
        System.out.println("支持的装饰器数量: " + decorators.size());

        // 3. 注册Telegram Bot通信器
        System.out.println("\n3. 注册Telegram Bot通信器");
        String botToken = System.getenv("tgBot");
        long tgBotIndex = normallyService.registerTgBotCommunicator(botToken, new String[]{});
        assertTrue(tgBotIndex > 0);
        System.out.println("注册成功，索引: " + tgBotIndex);

        // 4. 检查通信器状态
        System.out.println("\n4. 检查通信器状态");
        boolean status = normallyService.checkCommunicatorStatus(tgBotIndex);
        assertTrue(status);
        System.out.println("通信器状态: " + (status ? "正常" : "异常"));

        // 5. 发送消息
        System.out.println("\n5. 发送Telegram Bot消息");
        boolean sendResult = normallyService.sendTgBotMessage(
            tgBotIndex,
            "综合测试标题",
            "这是一条来自综合测试的消息",
            "6550266873"
        );
        assertTrue(sendResult);
        System.out.println("消息发送: " + (sendResult ? "成功" : "失败"));

        System.out.println("\n=== 综合测试完成 ===");
    }
}
