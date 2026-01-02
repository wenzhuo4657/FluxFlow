package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy;

import cn.wenzhuo4657.noifiterBot.app.config.CacheConfiguration;
import cn.wenzhuo4657.noifiterBot.app.types.Exception.AppException;
import cn.wenzhuo4657.noifiterBot.app.types.Exception.ResponseCode;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LocalStrategy extends abstractCacheStrategy {

    Map<String, Object> cacheMap = new ConcurrentHashMap<>();

    public LocalStrategy(CacheConfiguration cacheConfiguration) {
        super(cacheConfiguration);
    }


    @Override
    public String name() {
        return CacheType.LOCAL.getName();
    }

    @Override
    public Boolean delete(String key) {
        String fullKey = buildKey(key);
        return cacheMap.remove(fullKey) != null;
    }

    @Override
    public void set(String key, Object value) {
        String fullKey = buildKey(key);
        cacheMap.put(fullKey, value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        // 本地缓存不支持过期时间功能
        throw new AppException(ResponseCode.NOT_PERMISSIONS);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String fullKey = buildKey(key);
        Object value = cacheMap.get(fullKey);
        if (value == null) {
            return null;
        }

        try {
            return clazz.cast(value);
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public Boolean hasKey(String key) {
        String fullKey = buildKey(key);
        return cacheMap.containsKey(fullKey);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        // 本地缓存不支持过期时间功能
        throw new AppException(ResponseCode.NOT_PERMISSIONS);
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        // 本地缓存不支持过期时间功能
        throw new AppException(ResponseCode.NOT_PERMISSIONS);
    }

    @Override
    public void flushAll() {
        cacheMap.clear();
    }

    @Override
    public void initialize() {
        // 本地缓存无需额外初始化
    }

    @Override
    public void shutdown() {
        cacheMap.clear();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

}
