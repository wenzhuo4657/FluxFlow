package cn.wenzhuo4657.noifiterBot.app.apiTest.scripts;

public class QpsResponse {
    private int status;
    private int current_value;
    private int current_qps;
    private String message;

    // 默认构造函数
    public QpsResponse() {}

    // Getter和Setter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(int current_value) {
        this.current_value = current_value;
    }

    public int getCurrent_qps() {
        return current_qps;
    }

    public void setCurrent_qps(int current_qps) {
        this.current_qps = current_qps;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "QpsResponse{" +
                "status=" + status +
                ", current_value=" + current_value +
                ", current_qps=" + current_qps +
                ", message='" + message + '\'' +
                '}';
    }
}