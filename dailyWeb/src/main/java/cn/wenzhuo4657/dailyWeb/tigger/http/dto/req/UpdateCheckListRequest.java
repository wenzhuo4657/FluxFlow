package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCheckListRequest {

    @NotNull
    @Min(value = 0)
    private String index;

    @NotNull
    private String title;

    public UpdateCheckListRequest() {
    }

    public UpdateCheckListRequest(String index, String title) {
        this.index = index;
        this.title = title;
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

    @Override
    public String toString() {
        return "UpdateCheckListRequest{" +
                "index='" + index + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
