package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy;

import cn.wenzhuo4657.noifiterBot.app.config.CacheConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 缓存抽象层，用于实现一些公用的方法，如key前缀构建。
 */
public abstract class abstractCacheStrategy implements CacheStrategy {
    CacheConfiguration cacheConfiguration;


    public abstractCacheStrategy(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
        initialize();
    }


    /**
     * 构建key
     */
    public String buildKey(String key){

        if (StringUtils.isEmpty(key)){//避免构造无效key
            return null;
        }
        return cacheConfiguration.getKeyPrefix() + ":" +name()+ ":"+key;
    }

    public abstract String name();


    /**
     * 默认的Lua脚本执行实现 - 不支持
     */
    @Override
    public <T> T executeLuaScript(String luaScript, List<String> keys, List<Object> values, Class<T> resultType) {
        throw new UnsupportedOperationException("当前缓存策略不支持Lua脚本执行");
    }

    /**
     * 默认的Lua脚本SHA1执行实现 - 不支持
     */
    @Override
    public <T> T executeLuaScriptSha1(String scriptSha1, List<String> keys, List<Object> values, Class<T> resultType) {
        throw new UnsupportedOperationException("当前缓存策略不支持Lua脚本执行");
    }

    /**
     * 默认的Lua脚本加载实现 - 不支持
     */
    @Override
    public String loadLuaScript(String luaScript) {
        throw new UnsupportedOperationException("当前缓存策略不支持Lua脚本执行");
    }
}
