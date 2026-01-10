package cn.wenzhuo4657.noifiterBot.app.domain.notifier;

import java.io.File;
import java.util.Map;

/**
 * 通知器服务接口
 * 提供通知器的注册、消息发送、状态查询等功能
 */
public interface INotifierService {

    // ============ 注册通信器 ============

    /**
     * 注册Gmail通信器
     *
     * @param from 发件人邮箱
     * @param password 应用专用密码
     * @param to 收件人邮箱
     * @param decorator 装饰器数组（可选）
     * @return 通信器索引（雪花算法实现）
     */
    long registerGmailCommunicator(String from, String password, String to, String[] decorator);

    /**
     * 注册Telegram Bot通信器
     *
     * @param botToken Telegram Bot Token
     * @param decorator 装饰器数组（可选）
     * @return 通信器索引（雪花算法实现）
     */
    long registerTgBotCommunicator(String botToken, String[] decorator);

    // ============ 发送信息 ============

    /**
     * 发送Gmail邮件
     *
     * @param communicatorIndex 通信器索引
     * @param title 邮件标题
     * @param content 邮件内容
     * @param fileUrl 附件URL（可选）
     * @return true-成功，false-失败
     */
    boolean sendGmail(long communicatorIndex, String title, String content, String fileUrl);

    /**
     * 发送Gmail邮件（带文件附件）
     *
     * @param communicatorIndex 通信器索引
     * @param title 邮件标题
     * @param content 邮件内容
     * @param file 文件对象
     * @return true-成功，false-失败
     */
    boolean sendGmailWithFile(long communicatorIndex, String title, String content, File file);

    /**
     * 发送Telegram Bot消息
     *
     * @param communicatorIndex 通信器索引
     * @param title 消息标题
     * @param content 消息内容
     * @param chatId Telegram Chat ID
     * @return true-成功，false-失败
     */
    boolean sendTgBotMessage(long communicatorIndex, String title, String content, String chatId);

    // ============ 查询接口 ============

    /**
     * 检查通信器状态
     *
     * @param communicatorIndex 通信器索引
     * @return true-正常，false-异常
     */
    boolean checkCommunicatorStatus(long communicatorIndex);

    /**
     * 查询支持的通知器类型
     *
     * @return 通知器类型映射表（code -> description）
     */
    Map<String, String> querySupportNotifier();

    /**
     * 查询支持的装饰器类型
     *
     * @return 装饰器类型映射表（code -> description）
     */
    Map<String, String> querySupportDecorator();

}
