package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.NotNull;

public class InsertItemRequest {

    @NotNull
    private String docsId;

    @NotNull
    private String type;

    public InsertItemRequest() {
    }

    public InsertItemRequest(String docsId, String type) {
        this.docsId = docsId;
        this.type = type;
    }

    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InsertItemRequest{" +
                "docsId='" + docsId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
