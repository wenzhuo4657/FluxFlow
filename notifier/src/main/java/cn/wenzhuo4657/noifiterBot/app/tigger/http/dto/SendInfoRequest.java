package cn.wenzhuo4657.noifiterBot.app.tigger.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;


/**
 * 发送信息请求DTO
 */
public class SendInfoRequest {


    private Long communicatorIndex;


    private String paramsJson;

    private String type;

    private MultipartFile file;

    public SendInfoRequest() {}


    public SendInfoRequest(Long communicatorIndex, String paramsJson, String type, MultipartFile file) {
        this.communicatorIndex = communicatorIndex;
        this.paramsJson = paramsJson;
        this.type = type;
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getCommunicatorIndex() {
        return communicatorIndex;
    }

    public void setCommunicatorIndex(Long communicatorIndex) {
        this.communicatorIndex = communicatorIndex;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "SendInfoRequest{" +
                "communicatorIndex=" + communicatorIndex +
                ", paramsJson='" + paramsJson + '\'' +
                ", type='" + type + '\'' +
                ", file=" + file +
                '}';
    }
}