package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;

/**
 * 通知器工厂接口
 * 负责创建、缓存和管理通知器实例
 */
public interface IPondFactory {

    // ============ 注册通信器方法 ============

    /**
     * 创建并注册Gmail通信器
     *
     * @param from 发件人邮箱
     * @param password 应用专用密码
     * @param to 收件人邮箱
     * @param decorator 装饰器数组（可选）
     * @return 通信器索引
     */
    long createGmailNotifier(String from, String password, String to, String[] decorator);

    /**
     * 创建并注册Telegram Bot通信器
     *
     * @param botToken Telegram Bot Token
     * @param decorator 装饰器数组（可选）
     * @return 通信器索引
     */
    long createTgBotNotifier(String botToken, String[] decorator);

    // ============ 通用方法 ============

    /**
     * 获取通知器实例
     *
     * @param key 通信器索引
     * @return 通知器实例，如果不存在或不可用则返回null
     */
    INotifier get(long key);
}
