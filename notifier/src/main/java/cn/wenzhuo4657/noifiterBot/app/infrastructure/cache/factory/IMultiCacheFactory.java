package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.factory;

import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.CacheStrategy;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;

public interface IMultiCacheFactory {

    /**
     * spring注入完成后初始化
     */
    void init();

    /**
     * 创建缓存策略
     * @param activeType 激活的缓存类型
     * @return 缓存策略实例
     */
    CacheStrategy createStrategy(String activeType);

    /**
     * 获取当前缓存策略
     * @return 当前使用的缓存策略，如果不存在返回null
     */
    CacheStrategy getCurrentCacheStrategy();

    /**
     * 切换缓存策略
     * @param cacheType 缓存类型枚举
     * @return 是否切换成功
     */
    boolean switchCacheStrategy(CacheType cacheType);
}
