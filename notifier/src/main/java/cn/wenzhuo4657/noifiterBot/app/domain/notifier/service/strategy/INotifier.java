package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy;


/**
 * 通知器策略接口定义
 *
 */
public  interface INotifier<T extends  NotifierConfig, R extends NotifierMessage,H extends  NotifierResult> {



    /**
     * 发送通知
     */
    public abstract H send(R message);


    /**
     * 通知器是否可用
     */
    public abstract boolean isAvailable();

    /**
     * 销毁资源
     */
    public abstract void destroy();


    public T getConfig();


    /**
     * 获取通知起允许的最大qps
     */
    public  int getQps();


    /**
     * 通知器唯一性保证
     * todo 目前底层均用拼接配置的字符串的hashcode，但仍有hash冲突的风险
     */
    public  String getName();
}
