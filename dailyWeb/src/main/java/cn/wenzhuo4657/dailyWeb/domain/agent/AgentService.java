package cn.wenzhuo4657.dailyWeb.domain.agent;

public interface AgentService {

    /**
     * 根据用户id分析用户日志，然后动态提醒第三方通知器，
     */
    boolean analyzeAndNotifyUserLogs(Long userId);
}
