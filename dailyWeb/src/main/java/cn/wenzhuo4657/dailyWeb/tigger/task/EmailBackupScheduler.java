package cn.wenzhuo4657.dailyWeb.tigger.task;



import cn.wenzhuo4657.dailyWeb.Main;
import cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;





@Component
@ConditionalOnProperty(name = "email.enable", havingValue = "true")
public class EmailBackupScheduler {



    private final static Logger log= LoggerFactory.getLogger(EmailBackupScheduler.class);
    @Autowired
    private ApiService apiService;

    @Value("${email.config.to}")
    private  String to;

    @Value("${email.config.from}")
    private  String from;
    @Value("${email.config.password}")
    private  String password;

    private  static  long index=0;

    /**
     * 定时邮件备份，每天0（UTC+8）点执行
     */
    @Scheduled(cron = "0 0 0 * * ?",zone = "Asia/Shanghai")
    public void backup() {
      log.info("定时任务: 备份   -start");

      try {
          if (index == 0){
              index = apiService.registerGmailCommunicator(from, password, to, new String[]{});
          }
          // 直接发送文件
          boolean b = apiService.sendGmailWithFile(index, "dailyWeb", "备份", Main.getDbfilePath().toFile());

          if (!b){
              log.error("定时任务: 备份邮件发送失败");
          }else{
              log.info("定时任务: 备份邮件发送成功");
          }
      } catch (Exception e) {
        log.error("定时任务: 邮件备份异常",e);
      }
      log.info("定时任务: 邮件备份   -end");

    }

}
