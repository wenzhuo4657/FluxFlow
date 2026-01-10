package cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.strategy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存策略接口
 * 定义缓存操作的核心方法，支持动态切换不同的缓存实现
 */
public interface CacheStrategy {

    /**
     * 设置缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 设置缓存并指定过期时间
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 获取缓存值
     * @param key 缓存键
     * @param clazz 返回值类型
     * @return 缓存值，如果不存在或已过期返回null
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     * @param key 缓存键
     * @return 是否删除成功
     */
    Boolean delete(String key);

    /**
     * 检查缓存是否存在
     * @param key 缓存键
     * @return 是否存在
     */
    Boolean hasKey(String key);

    /**
     * 设置过期时间
     * @param key 缓存键
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout, TimeUnit timeUnit);

    /**
     * 获取剩余过期时间
     * @param key 缓存键
     * @param timeUnit 时间单位
     * @return 剩余时间，如果不存在或永不过期返回-1
     */
    Long getExpire(String key, TimeUnit timeUnit);




    /**
     * 清空所有缓存
     */
    void flushAll();



    /**
     * 初始化缓存连接
     */
    void initialize();

    /**
     * 关闭缓存连接
     */
    void shutdown();

    /**
     * 检查缓存连接是否可用
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 执行Lua脚本
     * @param luaScript Lua脚本内容
     * @param keys 脚本中使用的键列表
     * @param values 脚本中使用的值列表
     * @return 脚本执行结果
     */
    <T> T executeLuaScript(String luaScript, List<String> keys, List<Object> values, Class<T> resultType);

    /**
     * 执行Lua脚本（SHA1哈希，用于预加载的脚本）
     * @param scriptSha1 脚本的SHA1哈希值
     * @param keys 脚本中使用的键列表
     * @param values 脚本中使用的值列表
     * @return 脚本执行结果
     */
    <T> T executeLuaScriptSha1(String scriptSha1, List<String> keys, List<Object> values, Class<T> resultType);

    /**
     * 加载Lua脚本到Redis服务器并返回SHA1哈希值
     * @param luaScript Lua脚本内容
     * @return 脚本的SHA1哈希值
     */
    String loadLuaScript(String luaScript);
}