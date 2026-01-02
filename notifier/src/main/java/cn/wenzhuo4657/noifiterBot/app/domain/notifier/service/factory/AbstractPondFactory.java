package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.localCache.NormallyCacheNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通知器工厂流程抽象
 */
public abstract class AbstractPondFactory extends NormallyCacheNotifier implements IPondFactory  {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPondFactory.class);



    private  GlobalCache globalCache;


    public AbstractPondFactory(GlobalCache globalCache) {
        this.globalCache = globalCache;
    }

    /**
     * 初始化通知器，返回索引key
     *
     * @param json      配置json字符串：通知器元信息，用于通知器与第三方的身份验证
     * @param type      通知器策略类型;参照ConfigType.Strategy#code
     * @param decorator 装饰器数组  参照ConfigType.Decorator#code
     * @return 索引key
     */
    @Override
    public long init(String json, String type, String[] decorator) {
        logger.info("初始化通知器: type={}, decoratorCount={}", type, decorator != null ? decorator.length : 0);

        try {
            // 1. 解析配置类型，创建对应的通知器实例
            ConfigType.Strategy strategyType = ConfigType.Strategy.find(type);
            INotifier notifier = createNotifier(strategyType, json);

            // 2. 装配装饰器
            if (decorator != null && decorator.length > 0) {
                ConfigType.Decorator[] arr=new ConfigType.Decorator[decorator.length];
                for (int i = 0; i < decorator.length; i++)
                       arr[i]=ConfigType.Decorator.find(decorator[i]);

                notifier = applyDecorators(notifier, arr);
            }

            // 3. 生成唯一索引
            long index = createIndex();

            // 4. 本地缓存通知器实例
            notifierCache(index, notifier);


            // 5. 缓存初始化信息，用于后续恢复
//            todo 暂不考虑，分布式缓存恢复机制

            logger.info("通知器初始化成功: index={}", index);
            return index;

        } catch (Exception e) {
            logger.error("通知器初始化失败: type={}, error={}", type, e.getMessage(), e);
            throw new RuntimeException("通知器初始化失败: " + e.getMessage(), e);
        }
    }




    /**
     * 获取通知器
     */
    @Override
    public INotifier get(long key) {
        logger.debug("获取通知器: key={}", key);

        try {
            // 1. 尝试从本地缓存获取通知器实例
            INotifier notifier = find(key);

            if (notifier != null && notifier.isAvailable()) {
                return notifier;
            }

            // 2. 如果缓存不存在或不可用，尝试从配置信息恢复
//            todo 暂不考虑，分布式缓存恢复机制

            logger.warn("通知器不存在或已过期: key={}", key);
            return null;

        } catch (Exception e) {
            logger.error("获取通知器失败: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }


    /**
     * 创建通知器实例
     */
    protected abstract INotifier createNotifier(ConfigType.Strategy strategyType, String json);


    /**
     * 应用装饰器到通知器实例上
     */

    protected abstract INotifier applyDecorators(INotifier notifier, ConfigType.Decorator[] decorator);


    /**
     * 生成唯一索引
     * @return
     */
    protected abstract long createIndex();





}