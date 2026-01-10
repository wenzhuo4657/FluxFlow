package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.decorator;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;

/**
 * 装饰器工厂接口
 * 用于创建装饰后的通知器实例
 *
 * 通过工厂模式，实现装饰器的可扩展性，符合开闭原则：
 * - 添加新装饰器时无需修改现有代码
 * - 只需新增对应的工厂实现类即可
 */
public interface DecoratorFactory {

    /**
     * 创建装饰后的通知器实例
     *
     * @param notifier 被装饰的通知器
     * @param globalCache 全局缓存（部分装饰器可能需要）
     * @return 装饰后的通知器
     */
    INotifier create(INotifier notifier, GlobalCache globalCache);

    /**
     * 获取装饰器代码
     * 用于标识装饰器类型
     *
     * @return 装饰器代码，对应 ConfigType.Decorator.getCode()
     */
    String getDecoratorCode();

    /**
     * 获取装饰器名称
     *
     * @return 装饰器名称
     */
    default String getDecoratorName() {
        return getDecoratorCode();
    }
}
