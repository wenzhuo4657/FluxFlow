package cn.wenzhuo4657.noifiterBot.app.tigger.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 注册通信器请求DTO
 */
public class RegisterCommunicatorRequest {

    private String paramsJson;

    private String type;


    private String[] decorators;

    public RegisterCommunicatorRequest() {}

    public RegisterCommunicatorRequest(String paramsJson, String type, String[] decorators) {
        this.paramsJson = paramsJson;
        this.type = type;
        this.decorators = decorators;
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

    public String[] getDecorators() {
        return decorators;
    }

    public void setDecorators(String[] decorators) {
        this.decorators = decorators;
    }

    @Override
    public String toString() {
        return "RegisterCommunicatorRequest{" +
                "paramsJson='" + paramsJson + '\'' +
                ", type='" + type + '\'' +
                ", decorators=" + java.util.Arrays.toString(decorators) +
                '}';
    }
}