package cn.wenzhuo4657.dailyWeb.tigger.http.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InsertItemRequest {

    @NotNull
    private String docsId;

    @NotNull
    private int type;

    public InsertItemRequest() {
    }

    public InsertItemRequest(String docsId, int type) {
        this.docsId = docsId;
        this.type = type;
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
}
