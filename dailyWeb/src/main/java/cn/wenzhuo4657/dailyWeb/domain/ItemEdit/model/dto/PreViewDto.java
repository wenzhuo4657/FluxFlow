package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto;

import java.util.List;
import java.util.Objects;

public class PreViewDto {

    private List<TodayItemDto> baseItem;

    private List<ItemDto> planItem;


    public PreViewDto(List<TodayItemDto> baseItem, List<ItemDto> planItem) {
        this.baseItem = baseItem;
        this.planItem = planItem;
    }

    public PreViewDto() {
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

    @Override
    public String toString() {
        return "PreViewDto{" +
                "baseItem=" + baseItem +
                ", planItem=" + planItem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PreViewDto that = (PreViewDto) o;
        return Objects.equals(baseItem, that.baseItem) && Objects.equals(planItem, that.planItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseItem, planItem);
    }

    public void setPlanItem(List<ItemDto> planItem) {
        this.planItem = planItem;
    }
}
