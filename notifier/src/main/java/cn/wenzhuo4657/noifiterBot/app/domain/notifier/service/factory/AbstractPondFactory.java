package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.localCache.CacheNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.localCache.NormallyCacheNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import cn.wenzhuo4657.noifiterBot.app.types.utils.SnowflakeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通知器工厂抽象类
 * 实现通知器的创建、缓存和装饰器应用流程
 */
public abstract class AbstractPondFactory implements IPondFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPondFactory.class);

    private final CacheNotifier cacheNotifier;
    private final GlobalCache globalCache;

    public AbstractPondFactory(GlobalCache globalCache) {
        this.globalCache = globalCache;
        this.cacheNotifier = new NormallyCacheNotifier();
    }

    // ============ Gmail 通信器创建 ============

    @Override
    public long createGmailNotifier(String from, String password, String to, String[] decorator) {
        logger.info("创建Gmail通信器: from={}, to={}", from, to);

        try {
            // 1. 创建Gmail通知器实例
            INotifier notifier = createGmailNotifierInternal(from, password, to);

            // 2. 应用装饰器
            if (decorator != null && decorator.length > 0) {
                notifier = applyDecorators(notifier, decorator);
            }

            // 3. 生成唯一索引
            long index = generateIndex();

            // 4. 缓存通知器实例
            cacheNotifier.notifierCache(index, notifier);

            logger.info("Gmail通信器创建成功: index={}", index);
            return index;

        } catch (Exception e) {
            logger.error("创建Gmail通信器失败: from={}, to={}, error={}", from, to, e.getMessage(), e);
            throw new RuntimeException("创建Gmail通信器失败: " + e.getMessage(), e);
        }
    }

    // ============ Telegram Bot 通信器创建 ============

    @Override
    public long createTgBotNotifier(String botToken, String[] decorator) {
        logger.info("创建Telegram Bot通信器: botToken={}", maskToken(botToken));

        try {
            // 1. 创建Telegram Bot通知器实例
            INotifier notifier = createTgBotNotifierInternal(botToken);

            // 2. 应用装饰器
            if (decorator != null && decorator.length > 0) {
                notifier = applyDecorators(notifier, decorator);
            }

            // 3. 生成唯一索引
            long index = generateIndex();

            // 4. 缓存通知器实例
            cacheNotifier.notifierCache(index, notifier);

            logger.info("Telegram Bot通信器创建成功: index={}", index);
            return index;

        } catch (Exception e) {
            logger.error("创建Telegram Bot通信器失败: error={}", e.getMessage(), e);
            throw new RuntimeException("创建Telegram Bot通信器失败: " + e.getMessage(), e);
        }
    }

    // ============ 获取通知器 ============

    @Override
    public INotifier get(long key) {
        logger.debug("获取通知器: key={}", key);

        try {
            INotifier notifier = cacheNotifier.find(key);

            if (notifier != null && notifier.isAvailable()) {
                return notifier;
            }

            logger.warn("通知器不存在或已过期: key={}", key);
            return null;

        } catch (Exception e) {
            logger.error("获取通知器失败: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    // ============ 抽象方法 ============

    /**
     * 创建Gmail通知器实例
     */
    protected abstract INotifier createGmailNotifierInternal(String from, String password, String to);

    /**
     * 创建Telegram Bot通知器实例
     */
    protected abstract INotifier createTgBotNotifierInternal(String botToken);

    /**
     * 应用装饰器到通知器
     */
    protected abstract INotifier applyDecorators(INotifier notifier, String[] decoratorCodes);

    // ============ 工具方法 ============

    /**
     * 生成唯一索引
     */
    protected long generateIndex() {
        return SnowflakeUtils.getSnowflakeId();
    }

    /**
     * 获取GlobalCache实例（供子类使用）
     */
    protected GlobalCache getGlobalCache() {
        return globalCache;
    }

    /**
     * 掩码Token（用于日志输出）
     */
    protected String maskToken(String token) {
        if (token == null || token.isEmpty()) {
            return "***";
        }
        int visibleLength = Math.min(token.length(), 10);
        return token.substring(0, visibleLength) + "...";
    }

    /**
     * 解析装饰器代码
     */
    protected ConfigType.Decorator[] parseDecoratorCodes(String[] decoratorCodes) {
        if (decoratorCodes == null || decoratorCodes.length == 0) {
            return new ConfigType.Decorator[0];
        }

        ConfigType.Decorator[] decorators = new ConfigType.Decorator[decoratorCodes.length];
        for (int i = 0; i < decoratorCodes.length; i++) {
            decorators[i] = ConfigType.Decorator.find(decoratorCodes[i]);
        }
        return decorators;
    }
}