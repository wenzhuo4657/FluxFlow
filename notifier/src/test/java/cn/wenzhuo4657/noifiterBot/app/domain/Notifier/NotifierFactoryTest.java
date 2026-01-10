package cn.wenzhuo4657.noifiterBot.app.domain.Notifier;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsMaxDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.IPondFactory;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.factory.NormallyPondFactory;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifierMessage;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotifierFactoryTest {

    @Autowired
    private IPondFactory pondFactory;

    @Test
    public   void test(){
        TgBotConfig tgBotConfig=new TgBotConfig();
        tgBotConfig.setBotToken(System.getenv("tgBot"));
        

        long index = pondFactory.createTgBotNotifier(tgBotConfig.getBotToken(),new String[]{"qps"});

        INotifier notifier = pondFactory.get(index);
        TgBotNotifierMessage message=new TgBotNotifierMessage();
        message.setChatId("6550266873");
        message.setTitle("test");
        message.setContent("test");
        NotifierResult send = notifier.send(message);
        System.out.println("send: "+JSON.toJSONString(send));

    }



}
