package cn.wenzhuo4657.dailyWeb.tigger.task;



import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;





@Component
@ConditionalOnProperty(name = "email.enable", havingValue = "true")
public class EmailBackupScheduler {



    private final static Logger log= org.slf4j.LoggerFactory.getLogger(EmailBackupScheduler.class);


    /**
     * 定时邮件备份，每天0（UTC+8）点执行
     */
    @Scheduled(cron = "0 0 0 * * ?",zone = "Asia/Shanghai")
    public void backup() {
      log.info("定时任务: 备份   -start");

      try {

      } catch (Exception e) {
        log.error("定时任务: 邮件备份失败",e);
      }
      log.info("定时任务: 邮件备份   -end");

    }

}
