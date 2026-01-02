package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy;

import cn.wenzhuo4657.noifiterBot.app.config.CacheConfiguration;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;

import java.util.concurrent.TimeUnit;

public class ValkeyStrategy  extends   abstractCacheStrategy {

    public ValkeyStrategy(CacheConfiguration cacheConfiguration) {
        super(cacheConfiguration);
    }



    @Override
    public String name() {
        return CacheType.VALKEY.getName();
    }

    @Override
    public void set(String key, Object value) {

    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {

    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public Boolean delete(String key) {
        return null;
    }

    @Override
    public Boolean hasKey(String key) {
        return null;
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        return 0L;
    }

    @Override
    public void flushAll() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
