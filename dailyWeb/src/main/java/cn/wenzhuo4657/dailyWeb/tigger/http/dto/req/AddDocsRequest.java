package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddDocsRequest {

    @NotNull
    @Min(0)
    private String typeId;

    @NotBlank
    private String docsName;

    public AddDocsRequest() {
    }

    public AddDocsRequest(String typeId, String docsName) {
        this.typeId = typeId;
        this.docsName = docsName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getDocsName() {
        return docsName;
    }

    public void setDocsName(String docsName) {
        this.docsName = docsName;
    }

    @Override
    public String toString() {
        return "AddDocsRequest{" +
                "typeId='" + typeId + '\'' +
                ", docsName='" + docsName + '\'' +
                '}';
    }
}
