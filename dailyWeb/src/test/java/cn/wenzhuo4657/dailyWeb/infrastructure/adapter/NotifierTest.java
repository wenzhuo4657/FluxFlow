package cn.wenzhuo4657.dailyWeb.infrastructure.adapter;

import cn.wenzhuo4657.dailyWeb.Main;
import cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier.ApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotifierTest {


    @Autowired
    private ApiService apiService;

    @Value("${email.config.to}")
    private  String to;

    @Value("${email.config.from}")
    private  String from;
    @Value("${email.config.password}")
    private  String password;



    @Test
    public  void test(){
        long l = apiService.registerGmailCommunicator(from, password, to,new String[]{});
        apiService.sendGmail(l,"Hello World", "This is a test message from NotifierBot.", "");
        apiService.sendGmailWithFile(l,"Hello World", "This is a test message from NotifierBot.", Main.getDbfilePath().toFile());

        l=apiService.registerTgBotCommunicator(System.getenv("tgBot"),new String[]{});
        apiService.sendTgBotMessage(l,"Hello World", "This is a test message from NotifierBot.", "6550266873");
    }






}
