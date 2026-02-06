package cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.TodayItemDto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TodayDataRes implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TodayItemDto> baseItem;

    private List<ItemDto> planItem;

    public TodayDataRes(List<TodayItemDto> baseItem, List<ItemDto> planItem) {
        this.baseItem = baseItem;
        this.planItem = planItem;
    }
    public List<TodayItemDto> getBaseItem() {
        return baseItem;
    }

    public void setBaseItem(List<TodayItemDto> baseItem) {
        this.baseItem = baseItem;
    }

    public List<ItemDto> getPlanItem() {
        return planItem;
    }

    public void setPlanItem(List<ItemDto> planItem) {
        this.planItem = planItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodayDataRes that = (TodayDataRes) o;
        return Objects.equals(baseItem, that.baseItem) && Objects.equals(planItem, that.planItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseItem, planItem);
    }

    @Override
    public String toString() {
        return "TodayDataRes{" +
                "baseItem=" + baseItem +
                ", planItem=" + planItem +
                '}';
    }
}
