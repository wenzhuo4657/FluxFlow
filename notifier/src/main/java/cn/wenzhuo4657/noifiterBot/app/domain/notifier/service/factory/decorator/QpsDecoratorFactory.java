package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.decorator;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsMaxDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * QPS限流装饰器工厂
 * 负责创建QPS限流装饰器实例
 */
@Component
public class QpsDecoratorFactory implements DecoratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(QpsDecoratorFactory.class);

    @Override
    public INotifier create(INotifier notifier, GlobalCache globalCache) {
        logger.debug("创建QPS限流装饰器: targetNotifier={}",
                    notifier.getClass().getSimpleName());

        return new QpsMaxDecorator(notifier, globalCache);
    }

    @Override
    public String getDecoratorCode() {
        return ConfigType.Decorator.Qps.getCode();
    }

    @Override
    public String getDecoratorName() {
        return "QPS限流装饰器";
    }
}
