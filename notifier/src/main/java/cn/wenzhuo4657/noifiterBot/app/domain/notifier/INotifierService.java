package cn.wenzhuo4657.noifiterBot.app.domain.notifier;

import java.io.File;
import java.util.Map;

public interface INotifierService {

    /**
     *  注册通信器
     *
     * @param paramsJson  通信器校验参数-json格式
     * @param type  通知器类型
     * @return  通信器索引（雪花算法实现）
     */
    long registerCommunicator(String paramsJson, String type,String [] decorator);



    /**
     * 发送通信
     *
     * @param communicatorIndex  通信器索引
     * @param paramsJson  发送参数-json格式
     * @param type  通知器类型
     * @return true-成功，false-失败
     */
    boolean sendInfo(long communicatorIndex , String paramsJson,String type);

    /**
     * 发送通信（支持传递InputStream file）
     * @param communicatorIndex 通信器索引
     * @param paramsJson 发送参数-json格式
     * @param type 通知器类型
     * @param file 文件对象 （null不报错，但如果为null，应该调用他的重载方法）
     * @return true-成功，false-失败
     */
    boolean sendInfo(long communicatorIndex , String paramsJson, String type, File file);


    /**
     * 检查通信器状态
     *
     * @param communicatorIndex 通信器索引
     * @return true-正常，false-异常
     */
    boolean checkCommunicatorStatus(long communicatorIndex);


    /**
     * 查询支持的通知器
     */
    Map<String,String> querySupportNotifier();


    /**
     * 查询支持的增强功能
     */
    Map<String,String> querySupportDecorator();



}
