package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator;


import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.*;

public abstract class NotifierDecorator<H extends NotifierResult> extends IAbstractNotifier<NotifierConfig, NotifierMessage, H> {

    /**
     * 被装饰器的引用
     */
    protected INotifier notifier;

    public NotifierDecorator(INotifier notifier) {
        if (notifier == null || !notifier.isAvailable()){
            throw new IllegalArgumentException("Notifier is not available");
        }
        this.notifier = notifier;
    }


    /**
     * 装饰器无需配置验证，直接返回true
     *
     * @return
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {
//        销毁被装饰器的资源
        if (notifier != null) {
            notifier.destroy();
        }
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Decorator does not support getName method");
    }
}
