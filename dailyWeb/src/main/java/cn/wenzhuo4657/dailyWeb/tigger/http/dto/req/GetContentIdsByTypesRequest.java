package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GetContentIdsByTypesRequest {

    @NotNull
    @Min(0)
    private String id;

    public GetContentIdsByTypesRequest() {
    }

    public GetContentIdsByTypesRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GetContentIdsByTypesRequest{" +
                "id='" + id + '\'' +
                '}';
    }
}
