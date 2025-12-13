package cn.wenzhuo4657.dailyWeb.types.MyThreadPool;

import cn.wenzhuo4657.dailyWeb.types.utils.ThreadMdcUtils;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 自定义线程池： 父子线程继承mdc变量
 */
public class ThreadPoolTaskExecutor1 extends ThreadPoolTaskExecutor {

    public ThreadPoolTaskExecutor1() {
        super();
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
