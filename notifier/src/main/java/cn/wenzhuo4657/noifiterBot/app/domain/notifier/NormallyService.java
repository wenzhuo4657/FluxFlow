package cn.wenzhuo4657.noifiterBot.app.domain.notifier;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.IPondFactory;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NormallyService implements  INotifierService{

    @Autowired
    private IPondFactory pondFactory;
    @Override
    public long registerCommunicator(String paramsJson, String type,String [] decorator) {
        return  pondFactory.init(paramsJson,type,decorator);
    }

    @Override
    public boolean sendInfo(long communicatorIndex, String paramsJson,String type) {
        ConfigType.Strategy strategy = ConfigType.Strategy.find(type);
        Class<? extends NotifierMessage> messageClass = strategy.getMessageClass();
        NotifierMessage message = JSON.parseObject(paramsJson, messageClass);
        INotifier notifier = pondFactory.get(communicatorIndex);
        NotifierResult send = notifier.send(message);
        return send.isSuccess();
    }

    @Override
    public boolean sendInfo(long communicatorIndex, String paramsJson, String type, File file) {
        ConfigType.Strategy strategy = ConfigType.Strategy.find(type);
        Class<? extends NotifierMessage> messageClass = strategy.getMessageClass();
        NotifierMessage message = JSON.parseObject(paramsJson, messageClass);
        message.setFile2(file);

//        todo 有没有可能将消息作为外部，然后装饰器作为内部装配？
//        实现的好处是不用处理json，接口增加但语义明确
        INotifier notifier = pondFactory.get(communicatorIndex);



        NotifierResult send = notifier.send(message);
        return send.isSuccess();
    }

    @Override
    public boolean checkCommunicatorStatus(long communicatorIndex) {
        INotifier iNotifier = pondFactory.get(communicatorIndex);
        if (iNotifier != null&&iNotifier.isAvailable()){
            return true;
        }
        return false;
    }

    @Override
    public Map<String,String> querySupportNotifier() {
        ConfigType.Strategy[] strategies = ConfigType.Strategy.values();
        Map<String,String> map = new HashMap<>();
        for (ConfigType.Strategy strategy : strategies) {
            map.put(strategy.getCode(),strategy.getDescription());
        }
        return map;
    }

    @Override
    public Map<String,String> querySupportDecorator() {
        ConfigType.Decorator[] decorators = ConfigType.Decorator.values();
        Map<String,String> map = new HashMap<>();
        for (ConfigType.Decorator decorator : decorators) {
            map.put(decorator.getCode(), decorator.getDescription());
        }
        return map;
    }
}
