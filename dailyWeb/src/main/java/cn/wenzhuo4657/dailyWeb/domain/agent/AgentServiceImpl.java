package cn.wenzhuo4657.dailyWeb.domain.agent;


import cn.wenzhuo4657.dailyWeb.domain.agent.graph.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AgentServiceImpl implements AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentServiceImpl.class);

    @Autowired
    private Factory factory;




    @Async("MyExecutor")
    @Override
    public Future<Boolean> analyzeAndNotifyUserLogs(Long userId) {
        try {
            Boolean result = factory.execute(userId).join();
            log.info("代理执行完成, userId: {}, result: {}", userId, result);
            return java.util.concurrent.CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("代理执行异常, userId: {}, error: {}", userId, e.getMessage(), e);
            return java.util.concurrent.CompletableFuture.completedFuture(false);
        }
    }
}
