package cn.wenzhuo4657.dailyWeb.tigger.http.dto.res;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GetItemsResponse {





    @NotNull
    private String index;

    @NotNull
    private String title;
    @NotNull
    private String content;

    private String expand; // 额外字段数据，随类型动态变化


    public GetItemsResponse(String index, String title, String content, String expand) {
        this.index = index;
        this.title = title;
        this.content = content;
        this.expand = expand;
    }


    public GetItemsResponse() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    @Override
    public String toString() {
        return "GetItemsResponse{" +
                "index='" + index + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", expand='" + expand + '\'' +
                '}';
    }
}
