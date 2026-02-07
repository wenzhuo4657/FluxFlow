package cn.wenzhuo4657.dailyWeb.domain.auth;


import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.RegisterByOauthDto;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;


public  interface IUserService {

    /**
     * 初始化用户
     * 如果已存在，则直接返回相应的用户信息
     */
    public UserDto registerByOauth(RegisterByOauthDto registerByOauthDto);


    /**
     * 刷新用户心跳，内部使用redis的zet数据结构维护一组用户：最后登录时间
     */
    public void refreshUserHeartbeat(long userId);


    /**
     * 手动下线用户
     */
    public  boolean logoutUser(long userId);

    /**
     * 获取所有在线用户
     */
    public List<UserDto> getOnlineUsers();



}
