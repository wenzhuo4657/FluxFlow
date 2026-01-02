package cn.wenzhuo4657.noifiterBot.app.tigger.http.dto;

/**
 * 通信器索引响应DTO
 */
public class CommunicatorIndexResponse {

    private Long communicatorIndex;

    private String message;

    public CommunicatorIndexResponse() {}

    public CommunicatorIndexResponse(Long communicatorIndex, String message) {
        this.communicatorIndex = communicatorIndex;
        this.message = message;
    }

    public static CommunicatorIndexResponse success(Long communicatorIndex) {
        return new CommunicatorIndexResponse(communicatorIndex, "通信器注册成功");
    }

    public Long getCommunicatorIndex() {
        return communicatorIndex;
    }

    public void setCommunicatorIndex(Long communicatorIndex) {
        this.communicatorIndex = communicatorIndex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CommunicatorIndexResponse{" +
                "communicatorIndex=" + communicatorIndex +
                ", message='" + message + '\'' +
                '}';
    }
}