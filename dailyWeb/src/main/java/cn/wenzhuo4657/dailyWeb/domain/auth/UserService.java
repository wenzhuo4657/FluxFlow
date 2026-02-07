package cn.wenzhuo4657.dailyWeb.domain.auth;

import cn.wenzhuo4657.dailyWeb.domain.auth.model.aggregate.CheckUserByOauthAggregate;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.aggregate.RegisterAggregate;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.RegisterByOauthDto;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.UserDto;
import cn.wenzhuo4657.dailyWeb.domain.auth.repository.IAuthRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.User;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import cn.wenzhuo4657.dailyWeb.types.utils.SnowflakeUtils;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public  class UserService  implements IUserService {


    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private IAuthRepository authRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public UserDto registerByOauth(RegisterByOauthDto registerByOauthDto) {
        CheckUserByOauthAggregate check = new CheckUserByOauthAggregate(registerByOauthDto.getOauth_provider(), registerByOauthDto.getOauth_provider_user_id());


        User userEntity = authRepository.checkUserExists(check);
        if (userEntity ==null){
            RegisterAggregate aggregate=new RegisterAggregate();
            User userDto=new User();
            userDto.setName(registerByOauthDto.getOauth_provider_username());
            userDto.setUserId(SnowflakeUtils.getSnowflakeId());
            userDto.setAvatarUrl(registerByOauthDto.getOauth_provider_avatar());
            userDto.setOauthUserid(registerByOauthDto.getOauth_provider_user_id());
            userDto.setOauthProvider(registerByOauthDto.getOauth_provider());
            aggregate.setUser(userDto);
            userEntity=authRepository.initUser(aggregate);
        }

        UserDto dto=new UserDto();
        dto.setUsername(userEntity.getName());
        dto.setAvatar(userEntity.getAvatarUrl());
        dto.setId(userEntity.getUserId());
        return dto;
    }

    @Override
    public void refreshUserHeartbeat(long userId) {
        RScoredSortedSet<Long> set =
                redissonClient.getScoredSortedSet("online:users");

        set.add(System.currentTimeMillis(), userId);

    }

    @Override
    public boolean logoutUser(long userId) {
        try {
            AuthUtils.logoutUser(userId);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }

    }

    @Override
    public List<UserDto> getOnlineUsers() {
        List<String> userList = AuthUtils.getUserList();
        List<User> users = authRepository.queryUser(userList);
        List<UserDto> userDtos=new ArrayList<>();
        for (User user:users){
            UserDto dto = new UserDto();
            dto.setUsername(user.getName());
            dto.setAvatar(user.getAvatarUrl());
            dto.setId(user.getUserId());
            userDtos.add(dto);
        }
        return userDtos;
    }
}
