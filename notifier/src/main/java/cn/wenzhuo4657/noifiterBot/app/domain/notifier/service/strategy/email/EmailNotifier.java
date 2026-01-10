package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email;




import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.*;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import java.io.InputStream;
import java.util.Properties;

public class EmailNotifier extends IAbstractNotifier<GmailConfig, NotifierMessage,NotifierResult> {

    Logger log= LoggerFactory.getLogger(EmailNotifier.class);

    public EmailNotifier(GmailConfig config) {
        super(config);
    }
    Session session;

  @Override
    public NotifierResult send(NotifierMessage message) {

        GmailConfig config = getConfig();
        String from = config.getFrom();
        String to = config.getTo();
        String title = message.getTitle();
        String content = message.getContent();

        String url = message.getFile1();

        File in = message.getFile2();




      try {
            Session session = getSession(config.getFrom(), config.getPassword());

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress( from, "DailyWeb"));
            msg.setReplyTo(new Address[]{new InternetAddress(to)});
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to,  false));
            msg.setSubject( title);


            MimeBodyPart html = new MimeBodyPart();
            html.setContent("<p>"+ content+"<br><a href='"+url+"'>附件</a>"+"</p>", "text/html; charset=UTF-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(html);

            if (in != null){
                MimeBodyPart attach = new MimeBodyPart();
                attach.attachFile(in);
                multipart.addBodyPart(attach);
            }






            msg.setContent(multipart);


            Transport.send(msg);

        }catch (Exception e){
            e.printStackTrace();
            return NotifierResult.fail();
        }


        return NotifierResult.ok();
    }



    @Override
    public boolean isAvailable() {
        try {
            NotifierMessage message = new NotifierMessage();
            message.setTitle("Test Email");
            message.setContent("This is a test email sent from the EmailNotifier.");
            send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private Session getSession(String from, String password) {
        try {
            // 修复：先检查session是否存在且连接有效
            if (this.session != null) {
                try {
                    // 使用getTransport而不是getStore，因为我们要发送邮件
//                    Transport transport = this.session.getTransport();
//                    if (transport.isConnected()) {
//                        return this.session;
//                    }
//                    todo 分别管理seesion和Transport,前置用于配置，后者用于发送邮件
                    return this.session;
                } catch (Exception e) {
                    log.warn("检查Session连接状态失败，重新创建", e);
                    this.session = null;
                }
            }
        } catch (Exception e) {
            log.error("Session检查异常", e);
            this.session = null;
        }
        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");



            // 修复：不要创建局部变量session，直接赋值给成员变量
            this.session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            log.info("新的邮件Session创建成功");
            return this.session;

        } catch (Exception e) {
            log.error("创建邮件Session失败", e);
            throw new RuntimeException("邮件Session创建失败: " + e.getMessage(), e);
        }
    }


    @Override
    public void destroy() {
        try {
//            todo 资源释放，需要详细了解session和Transport的关系
            if (session != null&&session.getTransport().isConnected()) {
                session.getTransport().close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            session=null;
        }


    }

    @Override
    public String getName() {
        String s=getConfig().getFrom()+getConfig().getTo()+getConfig().getPassword();
        return  "enail:"+s.hashCode();
    }
}
