package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo;


import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DocsItemFiled {

    private static final Logger log = LoggerFactory.getLogger(DocsItemFiled.class);

    public static final String FILED_SPLIT_1=":";
    public static final String FILED_SPLIT_2=" ";



    /**
     * 合法属性记录
     * ps（1）： 关于属性值的解析和变更不去控制，仅仅保证所有field属性都为合法属性，且设置默认值
     * ps（2） 所有属性值都为字符串形式
     * ps(3) 属性值含义唯一，避免意义混淆，尽管他们处于不同文档类型，他每个属性的含义都是一样的
     * ps(4)  默认是为null的属性表示需要动态初始化（有的不需要参数，有的需要参数），动态初始化逻辑在ItemFiled#toFiled
     */
    public  enum ItemFiled{
        title("title","摸鱼~~~~~","文档项的标题"),
        status("status","false","表示该文档项是否完成，主要用于任务类型"),
        data("data","null","文档项定位的日期,"),
//        data_start/data_end的时间精度一般为日
        data_start("data_start","null","时间起点，与data_end成对表示，用于确定文档项的时间范围起点"),
        data_end("data_end","null","时间终点，与data_start成对表示，用于确定文档项的时间范围终点,null表示没有设置终点，即一直执行"),
//         time_point的时间精度为分钟
        time_point("time_point","null","时间点，用于确定文档项的时间范围起点或终点，精度为分钟"),
        parent_id("parent_id","null","父任务ID，用于建立任务间的父子关系，仅任务集类型有效"),
        connection("connection","null","连接的文档项ID，用于建立文档项之间的连接关系"),
        task_status("task_status","2","任务状态：1-完成，2-待做，3-销毁（父任务完成或取消任务），仅任务集类型有效"),
        score("score","null","任务评分，整数类型（1-5分或1-10分），仅任务集类型有效")
        ;


        private String filed;
        private String Default;
        private  String description;


        ItemFiled(String filed, String aDefault, String description) {
            this.filed = filed;
            Default = aDefault;
            this.description = description;
        }

        public String getFiled() {
            return filed;
        }

        public String getDefault() {
            return Default;
        }

        public static ItemFiled toItemFiled(String filed) {
            for (ItemFiled itemFiled : ItemFiled.values()) {
                if (itemFiled.getFiled().equals(filed)){
                    return itemFiled;
                }
            }
            log.error("不支持的属性");
            return null;
        }



    }

    public static Map<String,String> toFiled(ItemFiled[] itemFileds){
        Map<String,String> map =new HashMap<>();
        for (ItemFiled itemFiled : itemFileds) {
            map.put(itemFiled.getFiled(),itemFiled.getDefault());
        }
        return map;
    }

    public static Map<String,String> toMap(String  filed) throws ClassNotFoundException {
        HashMap<String, String> map=new HashMap<>();
        String[] split = filed.split(FILED_SPLIT_2);
        for (String s : split) {
            String[] split1 = s.split(FILED_SPLIT_1);
            if (ItemFiled.toItemFiled(split1[0])==null){
                throw new ClassNotFoundException("不支持的属性");
            }
            map.put(split1[0],split1[1]);
        }
        return map;
    }


    /**
     * 将初始化完成的属性集变成string
     *
     * 目前校验规则
     * 1，不允许value为null
     */
    public static String  toFiled(Map<String,String> map){
        StringBuilder filed=new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.isEmpty(entry.getValue())){
                throw new AppException(ResponseCode.programmingError);
            }
            filed.append(entry.getKey()).append(FILED_SPLIT_1).append(entry.getValue()).append(FILED_SPLIT_2);
        }
        return filed.toString();
    }
}
