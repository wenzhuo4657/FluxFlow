package cn.wenzhuo4657.noifiterBot.app.tigger.http.dto;



/**
 * 通信器状态检查请求DTO
 */
public class CommunicatorStatusRequest {


    private Long communicatorIndex;

    public CommunicatorStatusRequest() {}

    public CommunicatorStatusRequest(Long communicatorIndex) {
        this.communicatorIndex = communicatorIndex;
    }

    public Long getCommunicatorIndex() {
        return communicatorIndex;
    }

    public void setCommunicatorIndex(Long communicatorIndex) {
        this.communicatorIndex = communicatorIndex;
    }

    @Override
    public String toString() {
        return "CommunicatorStatusRequest{" +
                "communicatorIndex=" + communicatorIndex +
                '}';
    }
}