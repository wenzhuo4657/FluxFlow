package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy;

public abstract class IAbstractNotifier<T extends NotifierConfig, R extends NotifierMessage,H extends  NotifierResult> implements INotifier<T, R, H> {

    T config;


    public IAbstractNotifier(T config) {
        this.config = config;
    }


    public IAbstractNotifier() {
    }


    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public int getQps() {
        return 20;
//        todo 目前qps是在抽象层统一，但后续需要做到在配置中读取，便于更改
    }

}
