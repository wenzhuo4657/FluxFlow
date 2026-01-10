package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache;

import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.factory.IMultiCacheFactory;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy.CacheStrategy;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 全局缓存管理器
 * 基于策略模式实现，支持动态切换不同的缓存实现（Redis、Valkey等）
 */
@Component
public class GlobalCache  implements IGlobalCache,CacheStrategy {


    @Autowired
    private IMultiCacheFactory multiCacheFactory;

    /**
     * 获取当前缓存策略
     * @return 当前活跃的缓存策略
     */
    public CacheStrategy getCurrentCacheStrategy() {
        return multiCacheFactory.getCurrentCacheStrategy();
    }



    @Override
    public Boolean switchCacheStrategy(CacheType cacheType) {
//        todo 内部应当支持缓存数据的迁移，但目前仅仅是强制切换
        return multiCacheFactory.switchCacheStrategy(cacheType);
    }


//    以下方法均为代理api层实现

    @Override
    public void set(String key, Object value) {
        multiCacheFactory.getCurrentCacheStrategy().set(key,value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        multiCacheFactory.getCurrentCacheStrategy().set(key, value, timeout, timeUnit);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return multiCacheFactory.getCurrentCacheStrategy().get(key, clazz);
    }

    @Override
    public Boolean delete(String key) {
        return multiCacheFactory.getCurrentCacheStrategy().delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        return multiCacheFactory.getCurrentCacheStrategy().hasKey(key);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return multiCacheFactory.getCurrentCacheStrategy().expire(key, timeout, timeUnit);
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        return multiCacheFactory.getCurrentCacheStrategy().getExpire(key, timeUnit);
    }

    @Override
    public void flushAll() {
        multiCacheFactory.getCurrentCacheStrategy().flushAll();
    }

    @Override
    public void initialize() {
        multiCacheFactory.getCurrentCacheStrategy().initialize();
    }

    @Override
    public void shutdown() {
        multiCacheFactory.getCurrentCacheStrategy().shutdown();
    }

    @Override
    public boolean isAvailable() {
        return multiCacheFactory.getCurrentCacheStrategy().isAvailable();
    }

    @Override
    public <T> T executeLuaScript(String luaScript, List<String> keys, List<Object> values, Class<T> resultType) {
        return multiCacheFactory.getCurrentCacheStrategy().executeLuaScript(luaScript, keys, values, resultType);
    }

    @Override
    public <T> T executeLuaScriptSha1(String scriptSha1, List<String> keys, List<Object> values, Class<T> resultType) {
        return multiCacheFactory.getCurrentCacheStrategy().executeLuaScriptSha1(scriptSha1, keys, values, resultType);
    }

    @Override
    public String loadLuaScript(String luaScript) {
        return multiCacheFactory.getCurrentCacheStrategy().loadLuaScript(luaScript);
    }
}
