package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsMaxDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.EmailNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifier;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import cn.wenzhuo4657.noifiterBot.app.types.utils.SnowflakeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NormallyPondFactory extends  AbstractPondFactory{

    private static final Logger logger = LoggerFactory.getLogger(NormallyPondFactory.class);

    private GlobalCache globalCache;

    @Autowired
    public NormallyPondFactory(GlobalCache globalCache) {
        super(globalCache);
        this.globalCache=globalCache;
    }

    @Override
    protected INotifier createNotifier(ConfigType.Strategy strategyType, String json) {
        try {
            logger.debug("创建通知器: strategyType={}, json={}", strategyType.getName(), json);

            // 1. 验证输入参数
            if (strategyType == null) {
                throw new IllegalArgumentException("策略类型不能为空");
            }
            if (StrUtil.isBlank(json)) {
                throw new IllegalArgumentException("配置JSON不能为空");
            }

            // 2. 解析JSON配置
            JSONObject configJson = JSONUtil.parseObj(json);

            // 3. 根据策略类型创建对应的通知器实例
            switch (strategyType) {
                case TgBot:
                    return createTgBotNotifier(configJson);
                case Gmail:
                    return createEmailNotifier(configJson);
                default:
                    throw new IllegalArgumentException("不支持的通知器类型: " + strategyType.getName());
            }

        } catch (Exception e) {
            logger.error("创建通知器失败: strategyType={}, json={}, error={}",
                        strategyType != null ? strategyType.getName() : "null",
                        json,
                        e.getMessage(), e);
            throw new RuntimeException("创建通知器失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 Telegram Bot 通知器
     *
     * @param configJson 配置JSON
     * @return TgBotNotifier 实例
     */
    private INotifier createTgBotNotifier(JSONObject configJson) {
        try {
            // 解析 Telegram Bot 配置
            TgBotConfig config = new TgBotConfig();

            String botToken = configJson.getStr("botToken");
            if (StrUtil.isBlank(botToken)) {
                throw new IllegalArgumentException("Telegram Bot Token 不能为空");
            }
            config.setBotToken(botToken);

            // 创建通知器实例
            TgBotNotifier notifier = new TgBotNotifier(config);

            logger.debug("Telegram Bot 通知器创建成功: botToken={}",
                        botToken.substring(0, Math.min(botToken.length(), 10)) + "...");

            return notifier;

        } catch (Exception e) {
            logger.error("创建 Telegram Bot 通知器失败: config={}, error={}",
                        configJson, e.getMessage(), e);
            throw new RuntimeException("创建 Telegram Bot 通知器失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建邮件通知器
     *
     * @param configJson 配置JSON
     * @return EmailNotifier 实例
     */
    private INotifier createEmailNotifier(JSONObject configJson) {
        try {
            // 解析邮件配置
            GmailConfig config = new GmailConfig();

            String from = configJson.getStr("from");
            String password = configJson.getStr("password");
            String to = configJson.getStr("to");

            if (StrUtil.isBlank(from)) {
                throw new IllegalArgumentException("发件人邮箱不能为空");
            }
            if (StrUtil.isBlank(password)) {
                throw new IllegalArgumentException("发件人密码不能为空");
            }
            if (StrUtil.isBlank(to)) {
                throw new IllegalArgumentException("收件人邮箱不能为空");
            }

            config.setFrom(from);
            config.setPassword(password);
            config.setTo(to);

            // 创建通知器实例
            EmailNotifier notifier = new EmailNotifier(config);

            logger.debug("邮件通知器创建成功: from={}, to={}", from, to);

            return notifier;

        } catch (Exception e) {
            logger.error("创建邮件通知器失败: config={}, error={}",
                        configJson, e.getMessage(), e);
            throw new RuntimeException("创建邮件通知器失败: " + e.getMessage(), e);
        }
    }


    @Override
    protected INotifier applyDecorators(INotifier notifier, ConfigType.Decorator[] decorator) {
        try {
            logger.debug("应用装饰器: decoratorCount={}", decorator != null ? decorator.length : 0);

            if (notifier == null) {
                throw new IllegalArgumentException("通知器实例不能为空");
            }

            if (decorator == null || decorator.length == 0) {
                logger.debug("无需应用装饰器，返回原始通知器");
                return notifier;
            }

            INotifier decoratedNotifier = notifier;

            // 应用装饰器链
            for (ConfigType.Decorator decoratorType : decorator) {
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
     * @param notifier 被装饰的通知器
     * @param decoratorType 装饰器类型
     * @return 装饰后的通知器
     */
    private INotifier applySingleDecorator(INotifier notifier, ConfigType.Decorator decoratorType) {
        try {
            switch (decoratorType) {
                case Qps:
                    return new QpsMaxDecorator(notifier, globalCache);
                default:
                    logger.warn("不支持的装饰器类型: {}, 返回原始通知器", decoratorType.getName());
                    return notifier;
            }

        } catch (Exception e) {
            logger.error("应用装饰器失败: decorator={}, error={}",
                        decoratorType.getName(), e.getMessage(), e);
            throw new RuntimeException("应用装饰器失败: " + e.getMessage(), e);
        }
    }

    @Override
    protected long createIndex() {
        return SnowflakeUtils.getSnowflakeId();
    }
}
