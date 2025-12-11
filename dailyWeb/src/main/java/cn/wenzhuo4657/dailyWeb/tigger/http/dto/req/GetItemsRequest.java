package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.NotNull;

public class GetItemsRequest {


    @NotNull
    private String docsId;

    @NotNull
    private int type;


    public GetItemsRequest(String docsId, int type) {
        this.docsId = docsId;
        this.type = type;
    }

    public GetItemsRequest() {
    }


    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GetItemsRequest{" +
                "docsId='" + docsId + '\'' +
                ", type=" + type +
                '}';
    }
}
