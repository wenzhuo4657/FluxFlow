package cn.wenzhuo4657.dailyWeb.types.utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadMdcUtils {


    private  final static  String MDC_TRACE_ID="traceId";

    /**
     * 在当前线程的MDC中设置traceId（如果不存在的话）
     * 用于确保每个请求线程都有一个唯一的日志追踪标识
     * 如果当前线程已经存在traceId，则不会覆盖现有值
     */
    public static void setTraceIdIfAbsent(){
        if (MDC.get(MDC_TRACE_ID)==null){
            MDC.put(MDC_TRACE_ID,generateTraceId());
        }
    }

    /**
     * 接收外部透传的traceID，绑定到当前线程的MDC中
     * 用于分布式系统中跨服务传递追踪ID
     *
     * @param traceId 外部传入的追踪ID
     */
    public static  void setTraceId(String traceId){
        MDC.put(MDC_TRACE_ID,String.join(":",MDC_TRACE_ID,traceId));
    }

    /**
     * 将MDC上下文信息传递到新线程中（Callable版本）
     * 用于异步任务中保持日志追踪链路的连续性
     *
     * @param callable 需要在新线程中执行的任务
     * @param context 父线程的MDC上下文信息，如果为null则清空MDC
     * @param <T> 返回值类型
     * @return 包装后的Callable，执行时会自动设置和清理MDC上下文
     */
    public static  <T> Callable<T> wrap(final  Callable<T> callable, final Map<String,String> context){
        return ()->{
            if (context==null){
                MDC.clear();
            }else {
                MDC.setContextMap(context);
            }

            setTraceIdIfAbsent();

            try {
                return callable.call();
            }finally {
                MDC.clear();
            }
        };




    }


    /**
     * 将MDC上下文信息传递到新线程中（Runnable版本）
     * 用于异步任务中保持日志追踪链路的连续性
     *
     * @param runnable 需要在新线程中执行的任务
     * @param context 父线程的MDC上下文信息，如果为null则清空MDC
     * @return 包装后的Runnable，执行时会自动设置和清理MDC上下文
     */
    public static  Runnable wrap(final  Runnable runnable, final Map<String,String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }

            setTraceIdIfAbsent();

            try {
                 runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 生成完整的traceId
     * 格式: "traceId:{具体的追踪标识}"
     *
     * @return 完整的traceId字符串
     */
    private  static  String generateTraceId(){
        return String.join(":",MDC_TRACE_ID,generateMdcTraceId());
    }

    /**
     * 生成MDC专用Trace ID的工具方法
     * 格式: {时间戳后8位}-{机器标识后2位}-{随机数4位}
     * 示例: 2a8f1c3e-A1-7f92
     *
     * 推荐原因:
     * 1. 包含时间信息，便于日志时间线分析
     * 2. 适中的长度，不会过长影响日志可读性
     * 3. 生成速度快，不影响业务性能
     * 4. 包含机器标识，分布式环境下可追踪来源
     * 5. 随机部分保证唯一性，避免冲突
     */
    private static String generateMdcTraceId() {
        // 获取时间戳后8位的16进制表示
        long timestamp = System.currentTimeMillis();
        String timeHex = Long.toHexString(timestamp).substring(Math.max(0, Long.toHexString(timestamp).length() - 8));

        // 获取机器标识（可以用IP地址、主机名等的hash后两位）
        String machineId = getMachineIdentifier();

        // 生成4位随机数
        String random = Integer.toHexString(ThreadLocalRandom.current().nextInt(0x10000, 0xFFFFF));

        return String.format("%s-%s-%s", timeHex, machineId, random);
    }

    /**
     * 获取机器标识
     * 可以根据实际情况修改这个方法
     */
    private static String getMachineIdentifier() {
        try {
            // 方案1: 使用本地IP地址的hash
            String localHost = java.net.InetAddress.getLocalHost().getHostAddress();
            int hash = Math.abs(localHost.hashCode());
            return Integer.toHexString(hash).substring(Math.max(0, Integer.toHexString(hash).length() - 2));

            // 方案2: 使用主机名（取消下面的注释并注释上面的IP方案）
            // String hostName = java.net.InetAddress.getLocalHost().getHostName();
            // int hostHash = Math.abs(hostName.hashCode());
            // return Integer.toHexString(hostHash).substring(Math.max(0, Integer.toHexString(hostHash).length() - 2));

            // 方案3: 使用环境变量或配置文件中的机器标识
            // return System.getProperty("machine.id", "A1");

        } catch (Exception e) {
            // 如果获取失败，使用默认值
            return "A1";
        }
    }


}
