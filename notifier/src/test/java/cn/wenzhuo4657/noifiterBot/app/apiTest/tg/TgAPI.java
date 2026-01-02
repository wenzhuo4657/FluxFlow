package cn.wenzhuo4657.noifiterBot.app.apiTest.tg;


import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifierMessage;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TgAPI {

    private final TelegramClient telegramClient=new OkHttpTelegramClient(System.getenv("tgBot"));



    /**
     * 主动通信1：个人通信
     * 1)需要提前得知对话id
     * 通过： https://api.telegram.org/bot<TOKEN>/getUpdates
     */
    @Test
    public  void test1() throws TelegramApiException {
        SendMessage message=
                new SendMessage("6550266873","hello world!");
        telegramClient.execute(message);

    }

    /**
     * 主动通信2： 频道通信
     * 1） 需要提前得知对话id
     * 通过： https://api.telegram.org/bot<TOKEN>/getUpdates
     */
    @Test
    public  void test2() throws TelegramApiException {
        SendMessage message=
                new SendMessage("-5015631841","hello world!");
        telegramClient.execute(message);
    }


}
