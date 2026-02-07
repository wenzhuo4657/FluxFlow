package cn.wenzhuo4657.dailyWeb.domain.auth.repository;

import cn.wenzhuo4657.dailyWeb.domain.auth.model.aggregate.CheckUserByOauthAggregate;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.aggregate.RegisterAggregate;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.User;

import java.util.List;

public interface IAuthRepository {
    /**
     * 检查Oauth用户是否存在
     * @return  null,表示不存在。非null，即对应的用户实体
     */
    public User checkUserExists(CheckUserByOauthAggregate user);


    /**
     * 初始化用户
     * @return  用户实体
     * @Exception 初始化失败
     */
    public User  initUser(RegisterAggregate user);

    List<User> queryUser(List<String> userList);
}
