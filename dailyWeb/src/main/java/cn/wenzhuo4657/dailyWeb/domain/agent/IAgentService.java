package cn.wenzhuo4657.dailyWeb.domain.agent;


import org.springframework.stereotype.Service;

@Service
public class IAgentService implements  AgentService {






    @Override
    public boolean analyzeAndNotifyUserLogs(Long userId) {
        return false;
    }
}
