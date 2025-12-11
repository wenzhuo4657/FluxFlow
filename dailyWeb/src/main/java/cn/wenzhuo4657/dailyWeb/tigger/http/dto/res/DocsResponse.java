package cn.wenzhuo4657.dailyWeb.tigger.http.dto.res;

import jakarta.validation.constraints.NotNull;

public class DocsResponse {

    @NotNull
    private String id;

    @NotNull
    private String name;

    public DocsResponse() {
    }

    public DocsResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DocsResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
