package cn.wenzhuo4657.dailyWeb.types.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.UserDto;

/**
 *  登录身份工具类：隔绝框架层影响，如果后续更换权限框架，只改它就好了，
 *
 */
public class AuthUtils {

    /**
     * 获取登录用户的id
     * @return
     */
    public static Long getLoginId(){

//        替换为使用access token解析出登录id
        return Long.valueOf(StpUtil.getLoginId().toString());
    }

    /**
     * 存储用户信息
     */
    public static void setUserInfo(UserDto userInfo){
        StpUtil.getTokenSession().set("userInfo", userInfo);
    }

    /**
     * 获取用户信息
     */
    public static UserDto getUserInfo(){
        return (UserDto) StpUtil.getTokenSession().get("userInfo");
    }

}
