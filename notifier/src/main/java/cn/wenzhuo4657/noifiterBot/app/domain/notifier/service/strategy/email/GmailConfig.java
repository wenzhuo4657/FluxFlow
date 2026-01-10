package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.email;


import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;

public class GmailConfig implements NotifierConfig {


    /**
     * 通知发起方邮箱账号
     */
    private String from;
    /**
     * 通知发起方的应用密码
     */
    private String password;

    /**
     * 通知接收方邮箱账号
     */
    private String to;


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "GmailConfig{" +
                "from='" + from + '\'' +
                ", password='" + password + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
