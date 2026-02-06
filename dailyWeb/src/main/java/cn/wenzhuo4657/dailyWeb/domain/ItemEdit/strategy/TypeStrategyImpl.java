package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemFiled;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemType;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.ExpandFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.FieldFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.TitleFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.impl.TypeFunction;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.DocsDto;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;
import org.springframework.stereotype.Component;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public    class TypeStrategyImpl implements TypeStrategy {


    protected DocsItemType.ItemType router(Long type) throws ClassNotFoundException {
        return DocsItemType.ItemType.valueOfByCode(type);
    }



    @Override
    public String toFiled(Long type) throws ClassNotFoundException {
        DocsItemType.ItemType itemType  = router(type);
        return TypeFunction.toField.toField(itemType,new HashMap<>());
    }

    @Override
    public String toFiled(Long type, Map<String, String> fieldMap) throws ClassNotFoundException {
        DocsItemType.ItemType itemType  = router(type);
        return TypeFunction.toField.toField(itemType,fieldMap);
    }

    @Override
    public List<ItemDto> apply(Long type, List<DocsItem> items) throws ClassNotFoundException {
        DocsItemType.ItemType itemType  = router(type);

        List<ItemDto> list=new ArrayList<>(items.size());

        for (DocsItem item:items) {
            ItemDto itemDto = new ItemDto();
            itemDto.setIndex(item.getIndex().toString());
            itemDto.setTitle(getTitleFn().apply(itemType,item));
            itemDto.setContent(item.getItemContent());
            itemDto.setExpand(getExpandFn().apply(itemType,item));
            list.add(itemDto);
        }


        return list;
    }

    @Override
    public FieldFn getFieldFn() {
        return TypeFunction.toField;
    }

    @Override
    public TitleFn getTitleFn() {
        return TypeFunction.toTitle;
    }

    @Override
    public ExpandFn getExpandFn() {
        return TypeFunction.toExpand;
    }
}
