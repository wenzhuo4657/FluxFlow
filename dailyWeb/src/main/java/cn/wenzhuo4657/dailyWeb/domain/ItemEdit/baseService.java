package cn.wenzhuo4657.dailyWeb.domain.ItemEdit;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.InsertItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.QueryItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.UpdateItemDto;

import java.util.List;
import java.util.Map;

/**
 * 基本接口： 查询、初始化
 */
public interface baseService {

    /**
     * 增加item
     */
    @Deprecated
    boolean insertItem(InsertItemDto dto,Long userId);

    /**
     * 增加item, 允许覆盖默认属性
     */
    boolean insertItem_II(InsertItemDto dto, Long userId, Map<String ,String> fieldMap);

    /**
     * 修改item
     */
    boolean updateItem(UpdateItemDto dto);


    /**
     * 获取item
     */
    List<ItemDto> getItem(QueryItemDto dto);

    boolean deleteItem(long index);
}
