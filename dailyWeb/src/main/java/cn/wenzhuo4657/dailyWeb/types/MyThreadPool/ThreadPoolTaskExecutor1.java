package cn.wenzhuo4657.dailyWeb.types.MyThreadPool;

import cn.wenzhuo4657.dailyWeb.types.utils.ThreadMdcUtils;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池： 父子线程继承mdc变量
 */
public class ThreadPoolTaskExecutor1 extends ThreadPoolTaskExecutor {

    public ThreadPoolTaskExecutor1() {
        super();
        this.setCorePoolSize(10);
        this.setMaxPoolSize(10);
        this.setQueueCapacity(200);
        this.setKeepAliveSeconds(60);
        this.setThreadNamePrefix("custom-pool-");
        this.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//拒绝策略：直接在调用者线程中执行被拒绝的任务
    }

    @Override
    public void execute(Runnable task) {
        super.execute(ThreadMdcUtils.wrap(task, MDC.getCopyOfContextMap()));
    }


    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadMdcUtils.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadMdcUtils.wrap(task, MDC.getCopyOfContextMap()));
    }
}
