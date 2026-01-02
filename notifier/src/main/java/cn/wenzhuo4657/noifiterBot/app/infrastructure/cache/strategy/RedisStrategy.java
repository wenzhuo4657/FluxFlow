package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.wenzhuo4657.noifiterBot.app.config.CacheConfiguration;
import cn.wenzhuo4657.noifiterBot.app.config.CacheConfiguration.Redis;
import cn.wenzhuo4657.noifiterBot.app.types.cache.CacheType;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisStrategy  extends   abstractCacheStrategy{

    RedissonClient redissonClient;


    public RedisStrategy(CacheConfiguration cacheConfiguration) {
        super(cacheConfiguration);
    }


    @Override
    public String name() {
        return CacheType.REDIS.getName();
    }


    @Override
    public void initialize() {
        Redis redis = cacheConfiguration.getRedis();
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+redis.getHost()+":"+redis.getPort())
                .setDatabase(redis.getDatabase())
                .setConnectionPoolSize(redis.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redis.getConnectionMinimumIdleSize())
                .setTimeout(redis.getTimeout())
                .setRetryAttempts(redis.getRetryAttempts())
                .setRetryInterval(redis.getRetryInterval());
        if(!StringUtils.isEmpty(redis.getPassword()))
            config.useSingleServer().setPassword(redis.getPassword());

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        // 禁用多态类型处理，避免需要@class字段
        mapper.disable(com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS);
        mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonJacksonCodec codec = new JsonJacksonCodec(mapper);
        config.setCodec(codec);

        redissonClient = Redisson.create(config);

    }


    @Override
    public void set(String key, Object value) {
        key=buildKey(key);
        if (value instanceof String){
            redissonClient.getBucket(key).set(value);
        }else if (value instanceof Map<?,?>){
            RMap<Object, Object> map = redissonClient.getMap(key);
            map.putAll((Map<?, ?>) value);
        }else {
//            其他类型，通过序列化转为json
            redissonClient.getBucket(key).set(value);
        }

    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        key=buildKey(key);
        Duration duration = Duration.of(timeout, timeUnit.toChronoUnit());
        if (value instanceof String){
            redissonClient.getBucket(key).set(value, duration);
        }else if (value instanceof Map<?,?>){
            RMap<Object, Object> map = redissonClient.getMap(key);
            map.expire(duration);
            map.putAll((Map<?, ?>) value);
        }else {
//            其他类型，通过默认序列化转为json
            redissonClient.getBucket(key).set(value, duration);
        }

    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (!hasKey(key)||clazz == null) {
            return null;
        }
        key=buildKey(key);

        try {

            if (clazz.isAssignableFrom(String.class)){
                return clazz.cast(redissonClient.getBucket(key).get());
            } else if (clazz.isAssignableFrom(Map.class)){
                RMap<Object, Object> map = redissonClient.getMap(key);
                return clazz.cast(map);
            }else {
                return clazz.cast(redissonClient.getBucket(key).get());
            }

        } catch (Exception e) {
            // 发生异常时返回null，避免影响业务流程
            return null;
        }
    }

    @Override
    public Boolean delete(String key) {
        key=buildKey(key);
        if (key == null) {
            return false;
        }

        try {
            // 尝试删除所有可能的数据结构类型的key
            // Redisson的delete会删除指定key，无论其类型
            return redissonClient.getKeys().delete(key) > 0;
        } catch (Exception e) {
            // 删除过程中发生异常
            return false;
        }
    }

    @Override
    public Boolean hasKey(String key) {
        key=buildKey(key);
        if (key == null) {
            return false;
        }

        try {
            // 使用Redisson的keys API检查key是否存在
            return redissonClient.getKeys().countExists(key) > 0;
        } catch (Exception e) {
            // 检查过程中发生异常
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        if (redissonClient == null || redissonClient.isShutdown()) {
            return false;
        }

        try {
            // 轻量级连接检测：执行一个简单的操作来验证连接
            // 使用Redisson的getNodesGroup().pingAll()来检测所有节点连接状态
            return redissonClient.getNodesGroup().pingAll();
        } catch (Exception e) {
            // 连接异常或超时，返回false
            return false;
        }
    }
    @Override
    public void flushAll() {
        if (!isAvailable()) {
            throw new RuntimeException("Redis连接不可用，无法执行flushAll操作");
        }

        try {

            // 执行清空当前数据库的命令
            redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,
                "return redis.call('FLUSHDB')",
                RScript.ReturnType.STATUS
            );
        } catch (Exception e) {
            throw new RuntimeException("执行flushAll操作失败: " + e.getMessage(), e);
        }
    }


    @Override
    public void shutdown() {
        if (redissonClient == null) {
            return; // 已经关闭或未初始化
        }

        try {
            // 检查连接状态
            if (!redissonClient.isShutdown()) {
                // 优雅关闭：等待正在执行的操作完成
                redissonClient.shutdown(3, 5, java.util.concurrent.TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            // 强制关闭
            try {
                redissonClient.shutdown();
            } catch (Exception shutdownException) {
                // 记录错误但不抛出异常，确保关闭过程能够完成
                System.err.println("Redis强制关闭时发生错误: " + shutdownException.getMessage());
            }
        } finally {
            // 清理引用
            redissonClient = null;
        }
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        if (!hasKey(key)) {
            return null;
        }
        key=buildKey(key);

        try {


            // 尝试从不同类型的数据结构获取过期时间
            // 先尝试bucket
            if (redissonClient.getBucket(key).isExists()) {
                return redissonClient.getBucket(key).remainTimeToLive();
            }

            // 尝试set
            if (redissonClient.getSet(key).isExists()) {
                return redissonClient.getSet(key).remainTimeToLive();
            }

            // 尝试map
            if (redissonClient.getMap(key).isExists()) {
                return redissonClient.getMap(key).remainTimeToLive();
            }

            // 如果以上都不匹配，使用通用的key过期时间查询
            // Redisson没有直接的getKeysTimeout方法，我们使用TTL命令
            Long ttlResult = redissonClient.getScript().eval(
                RScript.Mode.READ_ONLY,
                "return redis.call('TTL', KEYS[1])",
                RScript.ReturnType.INTEGER,
                java.util.Collections.singletonList(key)
            );

            // 转换时间单位
            if (ttlResult != null && ttlResult > 0) {
                long seconds = ttlResult;
                return timeUnit.convert(seconds, TimeUnit.SECONDS);
            }
            return ttlResult;

        } catch (Exception e) {
            // 获取过期时间过程中发生异常
            return -1L;
        }
    }



    @Override
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        if (!hasKey(key)) {
            return null;
        }
        key=buildKey(key);

        try {
            // 检查key是否存在
            if (!hasKey(key)) {
                return false;
            }

            // 尝试为不同类型的数据结构设置过期时间
            // 先尝试bucket
            if (redissonClient.getBucket(key).isExists()) {
                return redissonClient.getBucket(key).expire(timeout, timeUnit);
            }

            // 尝试set
            if (redissonClient.getSet(key).isExists()) {
                return redissonClient.getSet(key).expire(timeout, timeUnit);
            }

            // 尝试map
            if (redissonClient.getMap(key).isExists()) {
                return redissonClient.getMap(key).expire(timeout, timeUnit);
            }

            // 如果以上都不匹配，使用通用的key过期时间设置
            return redissonClient.getKeys().expire(key, timeout, timeUnit);

        } catch (Exception e) {
            // 设置过期时间过程中发生异常
            return false;
        }
    }

    @Override
    public <T> T executeLuaScript(String luaScript, List<String> keys, List<Object> values, Class<T> resultType) {
        if (!isAvailable()) {
            throw new RuntimeException("Redis连接不可用，无法执行Lua脚本");
        }
        keys=keys.stream().map(key->buildKey(key)).collect(Collectors.toList());

        try {
            RScript script = redissonClient.getScript();

            // 使用Redisson的标准API格式：keys和values分别传递
            List<Object> keysObjects = keys != null ?
                keys.stream().map(key -> (Object) key).collect(Collectors.toList()) : Collections.emptyList();

            Object[] valuesArray = values != null ? values.toArray() : new Object[0];

            // 使用STATUS返回类型，让Redisson返回原始字符串而不尝试JSON解析
            Object result = script.eval(RScript.Mode.READ_WRITE,
                                       luaScript,
                                       RScript.ReturnType.STATUS,
                                       keysObjects,
                                       valuesArray);

            // 如果期望的返回类型是String，直接返回
            if (resultType == String.class) {
                return resultType.cast(result);
            }

            // 对于复杂对象，需要手动处理JSON反序列化
            try {
                if (result instanceof String && resultType != String.class) {
                    // 如果结果是字符串，但期望的是其他类型，说明是JSON字符串
                    String jsonResult = (String) result;
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    return mapper.readValue(jsonResult, resultType);
                } else {
                    // 直接类型转换
                    return resultType.cast(result);
                }
            } catch (Exception e) {
                throw new RuntimeException("JSON反序列化失败: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("执行Lua脚本失败: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T executeLuaScriptSha1(String scriptSha1, List<String> keys, List<Object> values, Class<T> resultType) {
        if (!isAvailable()) {
            throw new RuntimeException("Redis连接不可用，无法执行Lua脚本");
        }
        keys=keys.stream().map(key->buildKey(key)).collect(Collectors.toList());

        try {
            RScript script = redissonClient.getScript();

            // 使用Redisson的标准API格式：keys和values分别传递
            List<Object> keysObjects = keys != null ?
                keys.stream().map(key -> (Object) key).collect(Collectors.toList()) : Collections.emptyList();

            Object[] valuesArray = values != null ? values.stream().map(v -> v.toString()).toArray() : new Object[0];

            // 使用STATUS返回类型，让Redisson返回原始字符串而不尝试JSON解析
            Object result = script.evalSha(RScript.Mode.READ_WRITE,
                                         scriptSha1,
                                         RScript.ReturnType.STATUS,
                                         keysObjects,
                                         valuesArray);

            // 如果期望的返回类型是String，直接返回
            if (resultType == String.class) {
                return resultType.cast(result);
            }

            // 对于复杂对象，需要手动处理JSON反序列化
            try {
                if (result instanceof String && resultType != String.class) {
                    // 如果结果是字符串，但期望的是其他类型，说明是JSON字符串
                    String jsonResult = (String) result;
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    return mapper.readValue(jsonResult, resultType);
                } else {
                    // 直接类型转换
                    return resultType.cast(result);
                }
            } catch (Exception e) {
                throw new RuntimeException("JSON反序列化失败: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("执行Lua脚本(SHA1)失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String loadLuaScript(String luaScript) {
        if (!isAvailable()) {
            throw new RuntimeException("Redis连接不可用，无法加载Lua脚本");
        }

        try {
            RScript script = redissonClient.getScript();
            return script.scriptLoad(luaScript);
        } catch (Exception e) {
            throw new RuntimeException("加载Lua脚本失败: " + e.getMessage(), e);
        }
    }
}
