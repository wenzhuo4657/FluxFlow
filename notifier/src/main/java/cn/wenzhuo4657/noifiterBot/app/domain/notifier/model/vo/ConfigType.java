package cn.wenzhuo4657.noifiterBot.app.domain.notifier.model.vo;


import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsMaxDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps.QpsResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.EmailNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email.GmailConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot.TgBotNotifierMessage;




public class ConfigType {

    /**
     * 描述通知器策略
     */
    public  static enum  Strategy {

        Gmail("gmail邮件通知器",EmailNotifier.class,GmailConfig.class, NotifierMessage.class, NotifierResult.class,"gmail",
                "配置格式: GmailConfig{from: '发件人邮箱', password: '应用专用密码', to: '收件人邮箱'}\n" +
                "消息格式: NotifierMessage{title: '邮件标题', content: '邮件内容', file: '附件url(可选)'}\n" +
                "响应格式: NotifierResult{isSuccess: 'boolean - 发送是否成功'}\n" +
                "说明: 通过SMTP协议发送Gmail邮件，需要开启两步验证并使用应用专用密码"),
        TgBot("tgbot消息通知器", TgBotNotifier.class,TgBotConfig.class, TgBotNotifierMessage.class,NotifierResult.class,"tgBot",
                "配置格式: TgBotConfig{botToken: 'Telegram Bot Token'}\n" +
                "消息格式: TgBotNotifierMessage{title: '标题', content: '消息内容', chatId: 'Telegram Chat ID'}\n" +
                "响应格式: NotifierResult{isSuccess: 'boolean - 发送是否成功'}\n" +
                "说明: 通过Telegram Bot API发送消息，需要创建Bot并获取Token，chatId为目标聊天ID");

        private String name;
        private Class<? extends INotifier> clazz;
        private Class<?> configClass;
        private Class<? extends NotifierMessage> messageClass;
        private Class<? extends NotifierResult> resultClass;
        private String code;
        private String description;

        Strategy(String name, Class<? extends INotifier> clazz, Class<?> configClass, Class<? extends NotifierMessage> messageClass, Class<? extends NotifierResult> resultClass, String code, String description) {
            this.name = name;
            this.clazz = clazz;
            this.configClass = configClass;
            this.messageClass = messageClass;
            this.resultClass = resultClass;
            this.code = code;
            this.description = description;
        }


        public static Strategy find(String type) {
                for (Strategy strategy : Strategy.values()) {
                    if (strategy.getCode().equals(type)) {
                        return strategy;
                    }
                }
                throw new IllegalArgumentException("未找到对应的策略类型: " + type);
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Class<? extends INotifier> getClazz() {
            return clazz;
        }

        public void setClazz(Class<? extends INotifier> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getConfigClass() {
            return configClass;
        }

        public void setConfigClass(Class<?> configClass) {
            this.configClass = configClass;
        }

        public Class<? extends NotifierMessage> getMessageClass() {
            return messageClass;
        }

        public void setMessageClass(Class<? extends NotifierMessage> messageClass) {
            this.messageClass = messageClass;
        }

        public Class<? extends NotifierResult> getResultClass() {
            return resultClass;
        }

        public void setResultClass(Class<? extends NotifierResult> resultClass) {
            this.resultClass = resultClass;
        }
    }


    /**
     * 描述通知器装饰器
     */
    public  static  enum Decorator {
        Qps("qps限流", QpsMaxDecorator.class, QpsResult.class,"qps","qps限流");

        private String name;
        private Class<? extends INotifier> clazz;
        private Class<? extends NotifierResult> resultClass;
        private String code;
        private String description;


        Decorator(String name, Class<? extends INotifier> clazz, Class<? extends NotifierResult> resultClass, String code, String description) {
            this.name = name;
            this.clazz = clazz;
            this.resultClass = resultClass;
            this.code = code;
            this.description = description;
        }

        public static Decorator find(String s) {
            for (Decorator decorator : Decorator.values()) {
                if (decorator.getCode().equals(s)) {
                    return decorator;
                }
            }
            throw new IllegalArgumentException("未找到对应的装饰器");
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setClazz(Class<? extends INotifier> clazz) {
            this.clazz = clazz;
        }

        public void setResultClass(Class<? extends NotifierResult> resultClass) {
            this.resultClass = resultClass;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public Class<? extends INotifier> getClazz() {
            return clazz;
        }

        public Class<? extends NotifierResult> getResultClass() {
            return resultClass;
        }
    }
}
