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

            if (itemType.getTypeName().equals(DocsItemType.ItemType.Plan_I.getTypeName())
                ||
                    itemType.getTypeName().equals(DocsItemType.ItemType.Plan_II.getTypeName())
            ){
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


//            使用if else保证只有经过处理的类型才能够返回，最后一个else抛出错误
//            该if else处理的是每个类型属性中的动态属性，即默认值为null的属性，为了方便编写，不去单独校验他们，而是统一在DocsItemFiled.toFiled(map)方法中统一校验是否null的值来判断是否初始化完成
            if (DocsItemType.ItemType.dailyBase.equals(itemType)){
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                target.put(DocsItemFiled.ItemFiled.data.getFiled(), format.format(new Date()));

            } else if (DocsItemType.ItemType.Plan_I.equals(itemType)){
                if (map!=null&& !map.isEmpty()){
                    if (map.containsKey(DocsItemFiled.ItemFiled.time_point)){
                        map.put(DocsItemFiled.ItemFiled.time_point.getFiled(),map.get(DocsItemFiled.ItemFiled.time_point));
                    }

                }else {
                    throw new AppException(ResponseCode.programmingError);
                }
            } else if (DocsItemType.ItemType.Plan_II.equals(itemType)){
                if (map!=null&& !map.isEmpty()){
                    if (map.containsKey(DocsItemFiled.ItemFiled.data_start)){
                        map.put(DocsItemFiled.ItemFiled.data_start.getFiled(),map.get(DocsItemFiled.ItemFiled.data_start));
                    }
                    if (map.containsKey(DocsItemFiled.ItemFiled.data_end)){
                        map.put(DocsItemFiled.ItemFiled.data_end.getFiled(),map.get(DocsItemFiled.ItemFiled.data_end));
                    }

                }else {
                    throw new AppException(ResponseCode.programmingError);
                }
            } else if (DocsItemType.ItemType.StickyNote.equals(itemType)){
//                什么都不做
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
            if (itemType.getTypeName().equals(DocsItemType.ItemType.Plan_I.getTypeName())
                    ||
                       itemType.getTypeName().equals(DocsItemType.ItemType.Plan_II.getTypeName())){
                Map<String, String> map = DocsItemFiled.toMap(item.getItemField());
                return  map.get(DocsItemFiled.ItemFiled.status.getFiled());
            }
            return "";
        }
    };
}
