package cn.wenzhuo4657.dailyWeb.domain.agent;

import java.util.concurrent.Future;

public interface AgentService {

    /**
     * 根据用户id分析用户日志，然后动态提醒第三方通知器
     * @param userId 用户ID
     * @return Future<Boolean> 异步执行结果，true-成功，false-失败
     */
    Future<Boolean> analyzeAndNotifyUserLogs(Long userId);
}
