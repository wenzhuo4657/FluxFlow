package cn.wenzhuo4657.dailyWeb.config;

import cn.wenzhuo4657.dailyWeb.types.MyThreadPool.ThreadPoolTaskExecutor1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {


    @Bean("MyExecutor")
    public Executor myExecutor(){
        ThreadPoolTaskExecutor1 executor1 = new ThreadPoolTaskExecutor1();
        executor1.setThreadNamePrefix("myExecutor-");
        return executor1;
    }
}
