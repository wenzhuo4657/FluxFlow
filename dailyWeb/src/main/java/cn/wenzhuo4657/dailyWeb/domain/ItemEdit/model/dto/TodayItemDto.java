package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class TodayItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(value = 0)
    private String docsId;

    private Long index;

    @NotNull
    private String name;
    @NotNull
    private String content;


    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodayItemDto that = (TodayItemDto) o;
        return Objects.equals(docsId, that.docsId)
                && Objects.equals(index, that.index)
                && Objects.equals(name, that.name)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docsId, index, name, content);
    }

    @Override
    public String toString() {
        return "TodayItemDto{" +
                "docsId='" + docsId + '\'' +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
