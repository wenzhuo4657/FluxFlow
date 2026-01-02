package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.localCache;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 通用本地缓存实现
 * 使用 Caffeine 作为本地缓存引擎，提供高性能的通知器缓存功能
 */

public class NormallyCacheNotifier implements CacheNotifier {

    private static final Logger logger = LoggerFactory.getLogger(NormallyCacheNotifier.class);

    /**
     * 缓存键前缀
     */
    private static final String CACHE_KEY_PREFIX = "notifier:";

    /**
     * Caffeine 缓存实例
     * 配置：
     * - 最大容量：10,000 个通知器实例
     * - 无活跃后过期时间：60 分钟
     * - 初始容量：100
     */
    private final Cache<String, INotifier> cache;

    /**
     * 构造函数，初始化 Caffeine 缓存
     */
    public NormallyCacheNotifier() {
        this.cache = Caffeine.newBuilder()
                // 最大缓存数量
                .maximumSize(10_000)
                // 写入后30分钟过期
                .expireAfterAccess(60, TimeUnit.MINUTES)
                // 初始容量
                .initialCapacity(100)
                // 启用缓存统计
                .recordStats()
                // 缓存移除监听器
                .removalListener((key, value, cause) -> {
                    logger.debug("通知器从缓存中移除: key={}, cause={}", key, cause);
                    // 如果是通知器实例，尝试销毁资源
                    if (value instanceof INotifier) {
                        try {
                            ((INotifier) value).destroy();
                        } catch (Exception e) {
                            logger.error("销毁缓存中的通知器失败: key={}, error={}", key, e.getMessage(), e);
                        }
                    }
                })
                .build();

        logger.info("NormallyCacheNotifier 初始化完成，缓存配置: 最大容量={}, 过期时间={}分钟",
                   10_000, 30);
    }

    /**
     * 缓存通知器实例
     *
     * @param index   索引key
     * @param notifier 通知器对象
     */
    @Override
    public void notifierCache(long index, INotifier notifier) {
        if (notifier == null) {
            logger.warn("尝试缓存 null 通知器: index={}", index);
            return;
        }

        if (!notifier.isAvailable()) {
            logger.warn("尝试缓存不可用的通知器: index={}, notifier={}", index, notifier.getClass().getSimpleName());
            return;
        }

        String cacheKey = buildCacheKey(index);
        cache.put(cacheKey, notifier);

        logger.debug("通知器已缓存: index={}, notifier={}", index, notifier.getClass().getSimpleName());
    }

    /**
     * 获取缓存的通知器
     *
     * @param index 索引key
     * @return 通知器对象，如果不存在或不可用则返回 null
     */
    @Override
    public INotifier find(long index) {
        String cacheKey = buildCacheKey(index);
        INotifier notifier = cache.getIfPresent(cacheKey);

        if (notifier == null) {
            logger.debug("缓存中未找到通知器: index={}", index);
            return null;
        }

        if (!notifier.isAvailable()) {
            logger.warn("缓存中的通知器不可用，将移除: index={}, notifier={}",
                       index, notifier.getClass().getSimpleName());
            cache.invalidate(cacheKey);
            return null;
        }

        logger.debug("从缓存中获取通知器成功: index={}, notifier={}",
                    index, notifier.getClass().getSimpleName());
        return notifier;
    }

    /**
     * 移除缓存的通知器
     *
     * @param index 索引key
     * @return 被移除的通知器，如果不存在则返回 null
     */
    @Override
    public INotifier remove(long index) {
        String cacheKey = buildCacheKey(index);
        INotifier notifier = cache.getIfPresent(cacheKey);

        if (notifier != null) {
            cache.invalidate(cacheKey);
            logger.info("通知器已从缓存中移除: index={}, notifier={}",
                       index, notifier.getClass().getSimpleName());
        }

        return notifier;
    }

    /**
     * 清空所有缓存
     */
    @Override
    public void clear() {
        cache.invalidateAll();
        logger.info("所有通知器缓存已清空");
    }






    /**
     * 构建缓存键
     *
     * @param index 索引
     * @return 缓存键
     */
    private String buildCacheKey(long index) {
        return CACHE_KEY_PREFIX + index;
    }


}
