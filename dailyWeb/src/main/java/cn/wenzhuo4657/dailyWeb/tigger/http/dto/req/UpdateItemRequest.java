package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateItemRequest {

    @NotNull
    @Min(value = 0)
    private String index;

    @NotNull
    private String content;

    public UpdateItemRequest() {
    }

    public UpdateItemRequest(String index, String content) {
        this.index = index;
        this.content = content;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "UpdateItemRequest{" +
                "index='" + index + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
