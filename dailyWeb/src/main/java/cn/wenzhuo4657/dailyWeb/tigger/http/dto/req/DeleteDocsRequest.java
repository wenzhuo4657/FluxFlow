package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DeleteDocsRequest {

    @NotNull
    @Min(0)
    private String docsId;

    public DeleteDocsRequest() {
    }

    public DeleteDocsRequest(String docsId) {
        this.docsId = docsId;
    }

    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }

    @Override
    public String toString() {
        return "DeleteDocsRequest{" +
                "docsId='" + docsId + '\'' +
                '}';
    }
}
