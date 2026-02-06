package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.impl;


import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemFiled;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemType;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.ExpandFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.FieldFn;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.function.TitleFn;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsType;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TypeFunction {





    public  static TitleFn toTitle = new TitleFn() {
        @Override
        public String apply(DocsItemType.ItemType itemType, DocsItem item) throws ClassNotFoundException {
            if (itemType.getTypeName().equals(DocsItemType.ItemType.dailyBase.getTypeName())){
                Map<String, String> map = DocsItemFiled.toMap(item.getItemField());
                return  map.get(DocsItemFiled.ItemFiled.data.getFiled());
            }

            if (itemType.getTypeName().equals(DocsItemType.ItemType.Plan_I.getTypeName())){
                Map<String, String> map = DocsItemFiled.toMap(item.getItemField());
                return  map.get(DocsItemFiled.ItemFiled.title.getFiled());
            }
            if (itemType.getTypeName().equals(DocsItemType.ItemType.StickyNote.getTypeName())){
                return  "stickyNote";
            }

            throw  new ClassNotFoundException("不支持的ItemType");

        }
    };

    public static FieldFn toField = new FieldFn() {
        @Override
        public String toField(DocsItemType.ItemType itemType,Map<String,String> map) throws ClassNotFoundException {
            Map<String,String> target =DocsItemFiled.toFiled(itemType.getFiled());
            if (DocsItemType.ItemType.dailyBase.equals(itemType)){
                if (map!=null&& !map.isEmpty()){
                    for (DocsItemFiled.ItemFiled itemFiled : DocsItemType.Daily_Base_Field) {
                        if (map.containsKey(itemFiled.getFiled())){
                            target.put(itemFiled.getFiled(),map.get(itemFiled.getFiled()));
                        }
                    }
                }else {
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                    target.put(DocsItemFiled.ItemFiled.data.getFiled(), format.format(new Date()));
                }


            } else if (DocsItemType.ItemType.Plan_I.equals(itemType)){
                if (map!=null&& !map.isEmpty()){
                    for (DocsItemFiled.ItemFiled itemFiled : DocsItemType.Plan_I_Field) {
                        if (map.containsKey(itemFiled.getFiled())){
                            target.put(itemFiled.getFiled(),map.get(itemFiled.getFiled()));
                        }
                    }
                }
            } else if (DocsItemType.ItemType.StickyNote.equals(itemType)){
//                什么都不做
                if (map!=null&& !map.isEmpty()){
                    for (DocsItemFiled.ItemFiled itemFiled : DocsItemType.StickyNote_Field) {
                        if (map.containsKey(itemFiled.getFiled())){
                            target.put(itemFiled.getFiled(),map.get(itemFiled.getFiled()));
                        }
                    }
                }
            }
            else {
                throw new AppException(ResponseCode.UnsupportedType);
            }


            return DocsItemFiled.toFiled(target);

        }

    };


    public static ExpandFn toExpand = new ExpandFn() {
        @Override
        public String apply(DocsItemType.ItemType itemType, DocsItem item) throws ClassNotFoundException {
            if (itemType.getTypeName().equals(DocsItemType.ItemType.Plan_I.getTypeName())){
                return item.getItemField();
            }
            return "";
        }
    };
}
