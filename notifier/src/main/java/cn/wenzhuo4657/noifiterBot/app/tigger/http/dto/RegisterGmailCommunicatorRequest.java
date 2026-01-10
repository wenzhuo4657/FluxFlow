package cn.wenzhuo4657.noifiterBot.app.tigger.http.dto;

/**
 * 注册Gmail通信器请求DTO
 */
public class RegisterGmailCommunicatorRequest {

    /**
     * 发件人邮箱
     */
    private String from;

    /**
     * 应用专用密码
     */
    private String password;

    /**
     * 收件人邮箱
     */
    private String to;

    /**
     * 装饰器数组
     */
    private String[] decorators;

    public RegisterGmailCommunicatorRequest() {}

    public RegisterGmailCommunicatorRequest(String from, String password, String to, String[] decorators) {
        this.from = from;
        this.password = password;
        this.to = to;
        this.decorators = decorators;
    }

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

    public String[] getDecorators() {
        return decorators;
    }

    public void setDecorators(String[] decorators) {
        this.decorators = decorators;
    }

    @Override
    public String toString() {
        return "RegisterGmailCommunicatorRequest{" +
                "from='" + from + '\'' +
                ", password='***'" +
                ", to='" + to + '\'' +
                ", decorators=" + java.util.Arrays.toString(decorators) +
                '}';
    }
}
