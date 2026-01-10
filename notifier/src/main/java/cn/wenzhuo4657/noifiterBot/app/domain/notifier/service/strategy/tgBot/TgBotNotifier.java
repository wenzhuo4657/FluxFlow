package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.IAbstractNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import okhttp3.OkHttpClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

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
        
        TgBotConfig config = getConfig();
        
        // 构建 OkHttpClient，使用配置的超时时间
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS);
        
        // 如果配置了代理，则使用代理
        if (config.getProxyHost() != null && config.getProxyPort() != null) {
            Proxy.Type proxyType = "SOCKS".equalsIgnoreCase(config.getProxyType()) 
                    ? Proxy.Type.SOCKS 
                    : Proxy.Type.HTTP;
            
            Proxy proxy = new Proxy(proxyType, 
                    new InetSocketAddress(config.getProxyHost(), config.getProxyPort()));
            okHttpBuilder.proxy(proxy);
        }
        
        OkHttpClient okHttpClient = okHttpBuilder.build();
        telegramClient = new OkHttpTelegramClient(okHttpClient, botToken);
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
