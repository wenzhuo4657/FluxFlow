package cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 注册Telegram Bot通信器请求DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterTgBotCommunicatorRequest {

    /**
     * Telegram Bot Token
     */
    private String botToken;

    /**
     * 装饰器数组（如QPS限流等）
     */
    private String[] decorators;

    public RegisterTgBotCommunicatorRequest() {
    }

    public RegisterTgBotCommunicatorRequest(String botToken, String[] decorators) {
        this.botToken = botToken;
        this.decorators = decorators;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String[] getDecorators() {
        return decorators;
    }

    public void setDecorators(String[] decorators) {
        this.decorators = decorators;
    }

    @Override
    public String toString() {
        return "RegisterTgBotCommunicatorRequest{" +
                "botToken='***'" +
                ", decorators=" + (decorators != null ? String.join(",", decorators) : "null") +
                '}';
    }
}
