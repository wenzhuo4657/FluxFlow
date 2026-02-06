package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.ExpandFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.FieldFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.TitleFn;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;

import java.util.List;
import java.util.Map;


/**
 * 策略定义，typeName用于区分不同策略，此处只是用于提供方法的统一入口
 */
public interface TypeStrategy {

    /**
     * 初始化field属性
     */
    String toFiled(Long type)throws ClassNotFoundException;
    String toFiled(Long type, Map<String,String> fieldMap)throws ClassNotFoundException;


    List<ItemDto>  apply(Long type  , List<DocsItem> item) throws ClassNotFoundException;




    FieldFn getFieldFn();

    TitleFn getTitleFn();

    ExpandFn getExpandFn();



}
