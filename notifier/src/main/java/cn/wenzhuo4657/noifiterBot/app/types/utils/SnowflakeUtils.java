package cn.wenzhuo4657.noifiterBot.app.types.utils;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowflakeUtils {

    static Logger log= LoggerFactory.getLogger(SnowflakeUtils.class);

    public  static  long getSnowflakeId(){
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return  id;

    }




}
