package cn.wenzhuo4657.dailyWeb.domain.ItemEdit;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.*;

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
     *
     */
    List<ItemDto> getItem(QueryItemDto dto);

    /**
     * 获取item
     */
    List<ItemDto> getItem(Long DocsId);

    boolean deleteItem(long index);


    /**
     * 获取预览item
     */
    PreViewDto queryUserByToday(Long userId);


    /**
     * 追加今日日报
     * type限定为0
     */
    boolean tailAdd(Long docsId,String content) throws ClassNotFoundException;


}
