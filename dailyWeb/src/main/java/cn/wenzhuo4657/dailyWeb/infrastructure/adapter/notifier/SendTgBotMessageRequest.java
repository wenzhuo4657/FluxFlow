package cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 发送Telegram Bot消息请求DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendTgBotMessageRequest {

    /**
     * 通信器索引
     */
    private Long communicatorIndex;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * Telegram聊天ID
     */
    private String chatId;

    public SendTgBotMessageRequest() {
    }

    public SendTgBotMessageRequest(Long communicatorIndex, String title, String content, String chatId) {
        this.communicatorIndex = communicatorIndex;
        this.title = title;
        this.content = content;
        this.chatId = chatId;
    }

    public Long getCommunicatorIndex() {
        return communicatorIndex;
    }

    public void setCommunicatorIndex(Long communicatorIndex) {
        this.communicatorIndex = communicatorIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "SendTgBotMessageRequest{" +
                "communicatorIndex=" + communicatorIndex +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
