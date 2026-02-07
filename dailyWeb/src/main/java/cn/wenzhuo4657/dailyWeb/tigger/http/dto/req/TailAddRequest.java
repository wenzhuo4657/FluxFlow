package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TailAddRequest {

    @NotNull
    private String docsId;

    @NotBlank
    private String content;

    public TailAddRequest() {
    }

    public TailAddRequest(String docsId, String content) {
        this.docsId = docsId;
        this.content = content;
    }

    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TailAddRequest{" +
                "docsId=" + docsId +
                ", content='" + content + '\'' +
                '}';
    }
}
