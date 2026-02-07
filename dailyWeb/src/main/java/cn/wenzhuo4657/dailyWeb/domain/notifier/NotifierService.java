package cn.wenzhuo4657.dailyWeb.domain.notifier;

import cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto.NotifierMessage;
import cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotifierService implements INotifierService {

    private static final Logger log = LoggerFactory.getLogger(NotifierService.class);
    @Value("${notifierbot.botToken}")
    String botToken;
    @Value("${notifierbot.chatId}")
    String chatId;

    @Autowired
    private ApiService apiService;

    @Override
    public boolean send(NotifierMessage message) {
        log.info("Sending notification: {}", message);
        long l = apiService.registerTgBotCommunicator(botToken, new String[]{});
        apiService.sendTgBotMessage(l, message.getNotifierBody().getTitle(), message.getNotifierBody().getContent(), chatId);
        return true;
    }
}
