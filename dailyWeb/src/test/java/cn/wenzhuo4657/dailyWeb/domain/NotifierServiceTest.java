package cn.wenzhuo4657.dailyWeb.domain;


import cn.wenzhuo4657.dailyWeb.domain.notifier.NotifierService;
import cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto.NotifierBody;
import cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto.NotifierMessage;
import cn.wenzhuo4657.dailyWeb.domain.notifier.model.vo.NotifierType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotifierServiceTest {

    @Autowired
    private NotifierService notifierService;

    @Test
    public void testSendNotification() {
        NotifierMessage message = new NotifierMessage();
        message.setUserId(1L);
        message.setNotifierType(NotifierType.TG_BOT.getCode());
        NotifierBody body = new NotifierBody();
        body.setTitle("Test Notification");
        body.setContent("This is a test notification.");
        message.setNotifierBody(body);
        notifierService.send(message);
    }


}
