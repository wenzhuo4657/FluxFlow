package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;

public class TgBotNotifierMessage extends NotifierMessage {

    private  String chatId;


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
