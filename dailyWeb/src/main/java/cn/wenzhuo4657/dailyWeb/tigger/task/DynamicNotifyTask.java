package cn.wenzhuo4657.dailyWeb.tigger.task;

import cn.wenzhuo4657.dailyWeb.domain.agent.AgentService;
import cn.wenzhuo4657.dailyWeb.domain.auth.UserService;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "ai.enable", havingValue = "true")
public class DynamicNotifyTask {

    private static final Logger log = LoggerFactory.getLogger(DynamicNotifyTask.class);

    private static final String ONLINE_USERS_KEY = "online:users";
    private static final long ONE_HOUR_MS = 3600 * 1000;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AgentService agentService;
    /**
     * 定时获取离线用户（最后一次登录时间超过一小时前的用户）
     * 每次执行后清除已获取的用户，避免重复获取
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void getInactiveUsers() {
        try {
            RScoredSortedSet<Long> onlineUsers =
                    redissonClient.getScoredSortedSet(ONLINE_USERS_KEY);

            long oneHourAgo = System.currentTimeMillis() - ONE_HOUR_MS;

            Collection<Long> inactiveUsers =
                    onlineUsers.valueRange(0, true, oneHourAgo, true);

            if (inactiveUsers == null || inactiveUsers.isEmpty()) {
                log.debug("No inactive users found");
                return;
            }

            log.info("Found {} inactive users: {}", inactiveUsers.size(), inactiveUsers);


            inactiveUsers.stream().forEach(
                    user->{
                        // 清除已处理的离线用户，避免重复获取
                        onlineUsers.remove(user);

                        agentService.analyzeAndNotifyUserLogs(user);
                    }
            );

        } catch (Exception e) {
            log.error("Error while getting inactive users", e);
        }
    }


}
