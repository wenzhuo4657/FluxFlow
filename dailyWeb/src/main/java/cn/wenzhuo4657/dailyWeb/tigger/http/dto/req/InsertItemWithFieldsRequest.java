package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * 创建带字段的文档项请求
 */
public class InsertItemWithFieldsRequest {

    @NotNull
    private String docsId;

    @NotNull
    @Min(value = 0)
    private long type;

    /**
     * 动态字段映射
     * key: 字段名（如 data_start, data_end, parent_id, task_status, score, title）
     * value: 字段值
     */
    private Map<String, String> fields;

    public InsertItemWithFieldsRequest() {
    }

    public InsertItemWithFieldsRequest(String docsId, long type, Map<String, String> fields) {
        this.docsId = docsId;
        this.type = type;
        this.fields = fields;
    }

    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }


    public long getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "InsertItemWithFieldsRequest{" +
                "docsId=" + docsId +
                ", type=" + type +
                ", fields=" + fields +
                '}';
    }
}
