package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class ItemDto implements Serializable {

    private static final long serialVersionUID = 1L;




    @NotNull
    @Min(value = 0)
    private String index;

    @NotNull
    private String title;
    @NotNull
    private String content;

    private String expand; // 额外字段数据，随类型动态变化

    public ItemDto() {
    }

    public ItemDto(String index, String title, String content, String expand) {
        this.index = index;
        this.title = title;
        this.content = content;
        this.expand = expand;
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
        return "ItemDto{" +
                "index='" + index + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", expand='" + expand + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(index, itemDto.index)
                && Objects.equals(title, itemDto.title)
                && Objects.equals(content, itemDto.content)
                && Objects.equals(expand, itemDto.expand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, title, content, expand);
    }
}

