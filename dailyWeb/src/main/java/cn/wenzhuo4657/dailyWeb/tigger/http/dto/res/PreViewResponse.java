package cn.wenzhuo4657.dailyWeb.tigger.http.dto.res;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.TodayItemDto;

import java.util.List;

public class PreViewResponse {


    private List<TodayItemDto> baseItem;

    private List<ItemDto> planItem;

    public PreViewResponse(List<TodayItemDto> baseItem, List<ItemDto> planItem) {
        this.baseItem = baseItem;
        this.planItem = planItem;
    }

    public PreViewResponse() {
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
}
