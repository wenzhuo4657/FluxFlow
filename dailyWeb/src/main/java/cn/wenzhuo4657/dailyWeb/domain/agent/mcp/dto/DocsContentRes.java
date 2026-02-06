package cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DocsContentRes implements Serializable {

    private static final long serialVersionUID = 1L;

    List<ItemDto> itemDtos;

    public List<ItemDto> getItemDtos() {
        return itemDtos;
    }
    public void setItemDtos(List<ItemDto> itemDtos) {
        this.itemDtos = itemDtos;
    }


    @Override
    public String toString() {
        return "DocsContentRes{" +
                "itemDtos=" + itemDtos +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocsContentRes that = (DocsContentRes) o;
        return Objects.equals(itemDtos, that.itemDtos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemDtos);
    }

    public DocsContentRes() {
    }
    public DocsContentRes(List<ItemDto> itemDtos) {
        this.itemDtos = itemDtos;
    }

}
