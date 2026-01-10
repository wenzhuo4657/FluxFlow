package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.localCache;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;


/**
 * 本地缓存通知器
 *
 * 核心功能： 通知器在本地的缓存具有时效
 */
public interface CacheNotifier {


    /**
     * 通知器缓存
     *
     * @param index   索引key
     * @param notifier 通知器对象
     */
    void notifierCache(long index, INotifier notifier);

    /**
     * 获取通知器
     *
     * @param index 索引key
     * @return 通知器对象
     */
    INotifier find(long index);


    /**
     * 清除缓存
     */
    public void clear();

    /**
     * 移除通知器
     */
    public INotifier remove(long index);
}
