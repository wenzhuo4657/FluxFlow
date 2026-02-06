package cn.wenzhuo4657.dailyWeb.domain;

import cn.wenzhuo4657.dailyWeb.domain.agent.AgentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Future;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AgentServiceTest {

    @Autowired
    private AgentService agentService;

    @Test
    public void test() throws Exception {
        Future<Boolean> future = agentService.analyzeAndNotifyUserLogs(2014584316373372928L);
        boolean result = future.get(); // 阻塞等待异步结果
        System.out.println("result: " + result);
    }
}
