package cn.wenzhuo4657.noifiterBot.app.domain.Notifier;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.INotifierService;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.NormallyService;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo.ConfigType;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifierMessage;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class NormallyServiceTest {


    @Autowired
    private INotifierService normallyService;




    @Test
    public  void  test(){
        System.out.println("tgBot测试");
        TgBotConfig tgBotConfig=new TgBotConfig();
        tgBotConfig.setBotToken(System.getenv("tgBot"));
        String json= JSON.toJSONString(tgBotConfig);
        String  type= ConfigType.Strategy.TgBot.getCode();
        String[]  decorator={ConfigType.Decorator.Qps.getCode()};
        long index = normallyService.registerCommunicator(json, type, decorator);

        TgBotNotifierMessage message=new TgBotNotifierMessage();
        message.setChatId("6550266873");
        message.setTitle("test");
        message.setContent("test");
        String  jsonMessage  = JSON.toJSONString(message);
        boolean send = normallyService.sendInfo(index,jsonMessage,type);
        System.out.println("send: "+send);


        System.out.println("email测试");
        GmailConfig gmailConfig=new GmailConfig();
        gmailConfig.setFrom("wenzhuo4657@gmail.com");
        gmailConfig.setTo("wenzhuo4657@gmail.com");
        gmailConfig.setPassword(System.getenv("GMAIL_PASSWORD"));
        json = JSON.toJSONString(gmailConfig);
        type= ConfigType.Strategy.Gmail.getCode();
        index = normallyService.registerCommunicator(json, type, decorator);

        NotifierMessage notifierMessage = new NotifierMessage();
        notifierMessage.setTitle("test");
        notifierMessage.setContent("test");
        File file = new File("C:\\working\\my-project\\NotifierBot\\devops.md");
        boolean b = normallyService.sendInfo(index, JSON.toJSONString(notifierMessage), type,file);
        System.out.println("send: "+b);


    }

    public static void main(String[] args) {

        NotifierMessage notifierMessage = new NotifierMessage();
        notifierMessage.setTitle("test");
        notifierMessage.setContent("test");
        System.out.println(JSON.toJSONString(notifierMessage));
    }





}
