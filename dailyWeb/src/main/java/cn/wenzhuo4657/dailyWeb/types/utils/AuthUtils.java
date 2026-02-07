package cn.wenzhuo4657.dailyWeb.types.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.UserDto;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *  登录身份工具类：隔绝框架层影响，如果后续更换权限框架，只改它就好了，
 *
 */
public class AuthUtils {


    /**
     * 获取登录用户的id
     * @param httpRequest HTTP请求对象
     * @return 用户ID
     */
    public static Long getLoginId(HttpServletRequest httpRequest){

        try {
            try {
                String accessToken = httpRequest.getHeader("access_token").replace("Bearer ", "").trim();
                Claims claims = JwtUtils.parsePayload(accessToken);
                return  claims.get("username", Long.class);
            }catch (Exception e){
                return Long.valueOf(StpUtil.getLoginId().toString());
            }

        }catch (Exception e){
            throw  new AppException(ResponseCode.NOT_LOGIN,e.getMessage());
        }






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

    /**
     * 下线用户
     */
    public static void logoutUser(Long userId){
        StpUtil.logout(userId);
    }

    /**
     * 低并发方法，严禁在c端使用
     */
    public static List<String> getUserList() {
        List<String> sessionIdList = StpUtil.searchSessionId("", 0, -1, false);

        List<String> userIds = new ArrayList<>();
        for (String sessionId : sessionIdList) {
            // Sa-Token 的 SessionId 默认格式为：Authorization:login:session:{userId}
            // 我们需要解析出末尾的 userId
            // 也可以使用 StpUtil.stpLogic.splicingKeySession() 获取前缀长度来截取
            String userId = sessionId.substring(sessionId.lastIndexOf(":") + 1);
            userIds.add(userId);
        }
        return userIds;
    }
}
