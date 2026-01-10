package cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Notifier服务HTTP客户端接口
 * 使用Spring的RestTemplate作为http客户端，对接通知服务的四个核心方法
 */
public interface ApiService {

    // ============ 注册通信器方法 ============

    /**
     * 注册Gmail邮件通信器
     *
     * @param from Gmail邮件发送方
     * @param password Gmail应用密码
     * @param to 邮件接收方
     * @param decorators 装饰器数组（如QPS限流等）
     * @return 通信器索引
     */
    long registerGmailCommunicator(String from, String password, String to, String[] decorators);

    /**
     * 注册Telegram Bot通信器
     *
     * @param botToken Telegram Bot Token
     * @param decorators 装饰器数组（如QPS限流等）
     * @return 通信器索引
     */
    long registerTgBotCommunicator(String botToken, String[] decorators);

    // ============ 发送消息方法 ============

    /**
     * 发送Gmail邮件（通过文件URL）
     *
     * @param communicatorIndex 通信器索引
     * @param title 邮件标题
     * @param content 邮件内容
     * @param fileUrl 邮件附件URL（可选）
     * @return 发送结果
     */
    boolean sendGmail(long communicatorIndex, String title, String content, String fileUrl);

    /**
     * 发送Gmail邮件（直接上传文件）
     * 使用multipart/form-data格式上传文件附件
     *
     * @param communicatorIndex 通信器索引
     * @param title 邮件标题
     * @param content 邮件内容
     * @param file 邮件附件文件（可选）
     * @return 发送结果
     */
    boolean sendGmailWithFile(long communicatorIndex, String title, String content, File file);

    /**
     * 发送Telegram Bot消息
     *
     * @param communicatorIndex 通信器索引
     * @param title 消息标题
     * @param content 消息内容
     * @param chatId Telegram聊天ID
     * @return 发送结果
     */
    boolean sendTgBotMessage(long communicatorIndex, String title, String content, String chatId);
}
