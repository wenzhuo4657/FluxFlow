package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.factory;

import cn.wenzhuo4657.noifiterBot.app.config.CacheConfiguration;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.CacheStrategy;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.RedisStrategy;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.ValkeyStrategy;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@EnableConfigurationProperties(CacheConfiguration.class)
public class MultiCacheFactory implements IMultiCacheFactory {

    private final static Map<String, CacheStrategy> factory = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(MultiCacheFactory.class);

    @Autowired
    private CacheConfiguration cacheConfig;

    private String currentCacheType;

    // spring注入完成后初始化
    @PostConstruct
    public void init() {
        currentCacheType = cacheConfig.isEnabled() ? cacheConfig.getActiveType() : CacheType.LOCAL.getName();
        factory.put(currentCacheType, createStrategy(currentCacheType.toLowerCase()));

        log.info("初始化完成，当前使用的缓存策略为: {}", currentCacheType);
    }

    @Override
    public CacheStrategy createStrategy(String activeType) {
        switch (activeType) {
            case "redis":
                return new RedisStrategy(cacheConfig);
            case "valkey":
                return new ValkeyStrategy(cacheConfig);
            default:
                log.error("配置失效，未找到对应的缓存类型，使用本地缓存");
                throw new RuntimeException("未找到对应的缓存类型");
        }
    }

    @Override
    public CacheStrategy getCurrentCacheStrategy() {
        if (factory.containsKey(currentCacheType)) {
            return factory.get(currentCacheType);
        } else {
            log.error("当前缓存类型失效，请检查配置文件");
            return null;
        }
    }

    @Override
    public boolean switchCacheStrategy(CacheType cacheType) {
        String oldCacheType = currentCacheType;

        try {
            // 0,存放当前活跃缓存至库中
            CacheStrategy currentStrategy = getCurrentCacheStrategy();
            if (currentStrategy != null) {
                factory.put(currentCacheType, currentStrategy);
            }

            // 1,旧连接可用，直接返回
            if (factory.containsKey(cacheType.getName())) {
                CacheStrategy cacheStrategy = factory.get(cacheType.getName());
                if (cacheStrategy.isAvailable()) {
                    currentCacheType = cacheType.getName();
                    return true;
                }
            }

            // 2，旧连接不可用，关闭相应资源
            CacheStrategy remove = factory.remove(cacheType.getName());
            if (remove != null) {
                remove.shutdown();
            }

            // 3，重建连接
            factory.put(cacheType.getName(), createStrategy(cacheType.getName()));
            currentCacheType = cacheType.getName();

            return true;

        } catch (Exception e) {
            currentCacheType = oldCacheType;
            log.error("切换缓存策略失败: {}", e.getMessage());
            return false;
        }
    }
}
