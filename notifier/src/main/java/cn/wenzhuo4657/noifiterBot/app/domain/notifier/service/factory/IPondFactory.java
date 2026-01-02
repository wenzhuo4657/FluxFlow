package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.NotifierDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;

public interface IPondFactory {

    /**
     * 初始化通知器，返回索引key
     *
     * @param json   配置json字符串：通知器元信息，用于通知器与第三方的身份验证，参考ConfigType.Strategy#messageClass
     * @param type   通知器策略类型;参照ConfigType.Strategy#code
     * @param decorator 装饰器数组  参照ConfigType.Decorator#code
     * @return   索引key
     */
    public long init(String json, String type, String[] decorator);


    /**
     * 获取通知器
     */
    public  INotifier get(long key);
}
