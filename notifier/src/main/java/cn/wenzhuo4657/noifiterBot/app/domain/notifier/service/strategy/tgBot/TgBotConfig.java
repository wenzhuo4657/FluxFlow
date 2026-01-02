package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;

public class TgBotConfig implements NotifierConfig {

    private String botToken;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
