package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;


public class InsertItemDto {


    @NotNull
    @Min(0)
    private Long docsId;

    @NotNull
    private int type;

    public InsertItemDto() {
    }


    public InsertItemDto(Long docsId, int type) {
        this.docsId = docsId;
        this.type = type;
    }

    public Long getDocsId() {
        return docsId;
    }


    public void setDocsId(Long docsId) {
        this.docsId = docsId;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

