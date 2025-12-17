package cn.wenzhuo4657.dailyWeb.infrastructure.database.repository;


import cn.wenzhuo4657.dailyWeb.domain.auth.model.aggregate.CheckUserByOauthAggregate;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.aggregate.RegisterAggregate;
import cn.wenzhuo4657.dailyWeb.domain.auth.repository.IAuthRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsTypeDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.UserAuthDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.UserDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsType;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.User;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.UserAuth;
import cn.wenzhuo4657.dailyWeb.types.utils.SnowflakeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class AuthRepository  implements IAuthRepository {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private DocsDao docsDao;

    @Autowired
    private DocsTypeDao docsTypeDao;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public User checkUserExists(CheckUserByOauthAggregate user) {
        return userDao.queryByOauthId(user.getOauth_provider(),user.getOauth_provider_user_id());

    }

    @Override
    public User initUser(RegisterAggregate user) {
        User userEntity = user.getUser();
        userEntity.setCreatedAt(simpleDateFormat.format(System.currentTimeMillis()));

        int insert = userDao.insert(userEntity);

        if (insert==1){
            initUser(userEntity);
            return userEntity;
        }
        throw  new RuntimeException("用户初始化失败");
    }


    private void initUser(User user){

//        todo 用户初始化的同时初始化文档，待优化，不同文档的初始化
        List<DocsType> all = docsTypeDao.getAll();
        for (DocsType docsType : all){
            Docs docs = new Docs();
            docs.setName("default");
            docs.setTypeId(docsType.getTypeId());
            docs.setDocsId(SnowflakeUtils.getSnowflakeId());
            docs.setCreateTime(simpleDateFormat.format(new Date()));
            docs.setUpdateTime(simpleDateFormat.format(new Date()));
            docs.setUserId(user.getUserId());
            docsDao.insert(docs);



            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(user.getUserId());
            userAuth.setDocsTypeId(docsType.getTypeId());
            userAuthDao.insert(userAuth);
        }


    }




}
