package cn.wenzhuo4657.noifiterBot.app.domain.notifier;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.IPondFactory;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifierMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知器服务实现类
 * 提供通知器的注册、消息发送、状态查询等功能的实现
 */
@Service
public class NormallyService implements INotifierService {

    @Autowired
    private IPondFactory pondFactory;

    // ============ 注册通信器实现 ============

    @Override
    public long registerGmailCommunicator(String from, String password, String to, String[] decorator) {
        return pondFactory.createGmailNotifier(from, password, to, decorator);
    }

    @Override
    public long registerTgBotCommunicator(String botToken, String[] decorator) {
        return pondFactory.createTgBotNotifier(botToken, decorator);
    }

    // ============ 发送信息实现 ============

    @Override
    public boolean sendGmail(long communicatorIndex, String title, String content, String fileUrl) {
        NotifierMessage message = new NotifierMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setFile1(fileUrl);

        INotifier notifier = pondFactory.get(communicatorIndex);
        NotifierResult send = notifier.send(message);
        return send.isSuccess();
    }

    @Override
    public boolean sendGmailWithFile(long communicatorIndex, String title, String content, File file) {
        NotifierMessage message = new NotifierMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setFile2(file);

        INotifier notifier = pondFactory.get(communicatorIndex);
        NotifierResult send = notifier.send(message);
        return send.isSuccess();
    }

    @Override
    public boolean sendTgBotMessage(long communicatorIndex, String title, String content, String chatId) {
        TgBotNotifierMessage message = new TgBotNotifierMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setChatId(chatId);

        INotifier notifier = pondFactory.get(communicatorIndex);
        NotifierResult send = notifier.send(message);
        return send.isSuccess();
    }

    // ============ 查询接口实现 ============

    @Override
    public boolean checkCommunicatorStatus(long communicatorIndex) {
        INotifier iNotifier = pondFactory.get(communicatorIndex);
        return iNotifier != null && iNotifier.isAvailable();
    }

    @Override
    public Map<String, String> querySupportNotifier() {
        ConfigType.Strategy[] strategies = ConfigType.Strategy.values();
        Map<String, String> map = new HashMap<>();
        for (ConfigType.Strategy strategy : strategies) {
            map.put(strategy.getCode(), strategy.getDescription());
        }
        return map;
    }

    @Override
    public Map<String, String> querySupportDecorator() {
        ConfigType.Decorator[] decorators = ConfigType.Decorator.values();
        Map<String, String> map = new HashMap<>();
        for (ConfigType.Decorator decorator : decorators) {
            map.put(decorator.getCode(), decorator.getDescription());
        }
        return map;
    }
}
