package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.IAbstractNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TgBotNotifier extends IAbstractNotifier<TgBotConfig, TgBotNotifierMessage,NotifierResult> {


    public TgBotNotifier(TgBotConfig config) {
        super(config);
    }

    private TelegramClient telegramClient=null;


    @Override
    public NotifierResult send(TgBotNotifierMessage message) {
        try {
            TgBotConfig config = getConfig();
            TelegramClient telegramClient = getTelegramClient(config.getBotToken());



            SendMessage sendMessage =
                    new SendMessage(message.getChatId(), message.getTitle() + "\n" + message.getContent());
            telegramClient.execute(sendMessage);

            return NotifierResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return NotifierResult.fail();
        }
    }

    private TelegramClient getTelegramClient(String botToken){
        if (telegramClient!=null){
            return  telegramClient;
        }
        telegramClient=new OkHttpTelegramClient(botToken);
        return telegramClient;
    }


    @Override
    public boolean isAvailable() {
        if (telegramClient!=null){
            return true;
        }
        return true;
    }

    @Override
    public void destroy() {
        telegramClient=null;

    }

    @Override
    public String getName() {
        TgBotConfig config = getConfig();
        return "Tgbot"+config.getBotToken().hashCode();
    }
}
