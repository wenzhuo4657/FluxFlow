package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory;

import cn.hutool.core.util.StrUtil;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.decorator.DecoratorFactory;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.EmailNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifier;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通知器工厂实现类
 * 负责创建具体的通知器实例并应用装饰器
 *
 * 重构说明：
 * - 使用装饰器工厂注册表替代switch-case
 * - 符合开闭原则：添加新装饰器无需修改此类
 * - 新增装饰器只需创建新的DecoratorFactory实现类
 */
@Component
public class NormallyPondFactory extends AbstractPondFactory {

    private static final Logger logger = LoggerFactory.getLogger(NormallyPondFactory.class);

    /**
     * 装饰器工厂注册表
     * Key: 装饰器代码
     * Value: 装饰器工厂实例
     */
    private final Map<String, DecoratorFactory> decoratorFactories;

    /**
     * 构造函数，自动注册所有装饰器工厂
     *
     * @param globalCache 全局缓存
     * @param decoratorFactories Spring自动注入的所有装饰器工厂
     */
    public NormallyPondFactory(
            GlobalCache globalCache,
            List<DecoratorFactory> decoratorFactories) {
        super(globalCache);

        // 注册所有装饰器工厂到Map中
        this.decoratorFactories = decoratorFactories.stream()
            .collect(Collectors.toMap(
                DecoratorFactory::getDecoratorCode,
                Function.identity()
            ));

        logger.info("装饰器工厂注册完成: 注册数量={}, 装饰器类型={}",
                   this.decoratorFactories.size(),
                   this.decoratorFactories.keySet());
    }

    // ============ 创建Gmail通知器 ============

    @Override
    protected INotifier createGmailNotifierInternal(String from, String password, String to) {
        try {
            // 验证参数
            if (StrUtil.isBlank(from)) {
                throw new IllegalArgumentException("发件人邮箱不能为空");
            }
            if (StrUtil.isBlank(password)) {
                throw new IllegalArgumentException("发件人密码不能为空");
            }
            if (StrUtil.isBlank(to)) {
                throw new IllegalArgumentException("收件人邮箱不能为空");
            }

            // 创建配置对象
            GmailConfig config = new GmailConfig();
            config.setFrom(from);
            config.setPassword(password);
            config.setTo(to);

            // 创建通知器实例
            EmailNotifier notifier = new EmailNotifier(config);

            logger.debug("Gmail通知器创建成功: from={}, to={}", from, to);
            return notifier;

        } catch (Exception e) {
            logger.error("创建Gmail通知器失败: from={}, to={}, error={}", from, to, e.getMessage(), e);
            throw new RuntimeException("创建Gmail通知器失败: " + e.getMessage(), e);
        }
    }

    // ============ 创建Telegram Bot通知器 ============

    @Override
    protected INotifier createTgBotNotifierInternal(String botToken) {
        try {
            // 验证参数
            if (StrUtil.isBlank(botToken)) {
                throw new IllegalArgumentException("Telegram Bot Token不能为空");
            }

            // 创建配置对象
            TgBotConfig config = new TgBotConfig();
            config.setBotToken(botToken);

            // 创建通知器实例
            TgBotNotifier notifier = new TgBotNotifier(config);

            logger.debug("Telegram Bot通知器创建成功: botToken={}", maskToken(botToken));
            return notifier;

        } catch (Exception e) {
            logger.error("创建Telegram Bot通知器失败: error={}", e.getMessage(), e);
            throw new RuntimeException("创建Telegram Bot通知器失败: " + e.getMessage(), e);
        }
    }

    // ============ 应用装饰器 ============

    @Override
    protected INotifier applyDecorators(INotifier notifier, String[] decoratorCodes) {
        try {
            if (notifier == null) {
                throw new IllegalArgumentException("通知器实例不能为空");
            }

            if (decoratorCodes == null || decoratorCodes.length == 0) {
                logger.debug("无需应用装饰器，返回原始通知器");
                return notifier;
            }

            logger.debug("应用装饰器: decoratorCount={}, decoratorTypes={}",
                       decoratorCodes.length, String.join(", ", decoratorCodes));

            // 解析装饰器代码
            ConfigType.Decorator[] decorators = parseDecoratorCodes(decoratorCodes);

            INotifier decoratedNotifier = notifier;

            // 应用装饰器链
            for (ConfigType.Decorator decoratorType : decorators) {
                if (decoratorType == null) {
                    logger.warn("装饰器类型为空，跳过");
                    continue;
                }

                logger.debug("应用装饰器: {}", decoratorType.getName());
                decoratedNotifier = applySingleDecorator(decoratedNotifier, decoratorType);
            }

            logger.debug("装饰器应用完成: originalNotifier={}, decoratedNotifier={}",
                        notifier.getClass().getSimpleName(),
                        decoratedNotifier.getClass().getSimpleName());

            return decoratedNotifier;

        } catch (Exception e) {
            logger.error("应用装饰器失败: error={}", e.getMessage(), e);
            throw new RuntimeException("应用装饰器失败: " + e.getMessage(), e);
        }
    }

    /**
     * 应用单个装饰器
     *
     * 重构说明：
     * - 使用装饰器工厂注册表替代switch-case
     * - 符合开闭原则：添加新装饰器无需修改此方法
     * - 通过查找Map获取对应的工厂实例
     *
     * @param notifier 被装饰的通知器
     * @param decoratorType 装饰器类型
     * @return 装饰后的通知器
     */
    private INotifier applySingleDecorator(INotifier notifier, ConfigType.Decorator decoratorType) {
        String code = decoratorType.getCode();

        // 从工厂注册表中查找对应的装饰器工厂
        DecoratorFactory factory = decoratorFactories.get(code);

        if (factory == null) {
            logger.warn("不支持的装饰器类型: {}, 返回原始通知器（可能未实现对应的DecoratorFactory）",
                       decoratorType.getName());
            return notifier;
        }

        try {
            // 使用工厂创建装饰后的通知器
            return factory.create(notifier, getGlobalCache());
        } catch (Exception e) {
            logger.error("应用装饰器失败: decorator={}, error={}",
                        decoratorType.getName(), e.getMessage(), e);
            throw new RuntimeException("应用装饰器失败: " + e.getMessage(), e);
        }
    }
}
