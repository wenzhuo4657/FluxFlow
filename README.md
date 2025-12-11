# dailyWeb-back
日报程序后端


# 介绍

这是一个用于记录日报的后端程序，使用Spring Boot和MyBatis作为框架。它支持GitHub OAuth登录，并且可以通过邮件每日定时备份

![](https://cdn.wenzhuo4657.org/img/2025/12/d31f1295c8020ea40099b3ad93797d81.png)

# 使用

## 配置
所有的个人配置都放置在`application-prod.yml`当中，请根据实际内容为主
```
domain:
  url:  ${domain:https://test.wenzhuo4657.org}
  home:  ${domain.url}/md-web

github:
  client-id:  ${GITHUB_CLIENT_ID}
  client-secret:  ${GITHUB_CLIENT_SECRET}
  redirect-uri: ${domain.url}/api/oauth/callback/github

email:
  enable: true
  config:
    from: wenzhuo4657@gmail.com
    to: wenzhuo4657@gmail.com
    password: ${GMAIL_PASSWORD}


```


## 部署


###  先决条件

##### jdk

  jdk版本 >= 17

##### github oauth 

homepage:   https://your.domain.com:8081/
redirect-uri: https://your.domain.com:8081/api/oauth/callback/github

ps: 请根据实际部署情况替换路径

##### 域名

a记录解析至目标主机ip

##### 谷歌邮箱

获取邮箱的应用密码

默认关闭，可选

### jar部署

下载项目自行编译得到jar，

```
nohup java \
  -Dserver.port=8081 \
  -Ddir.beifen=/root/snap/daily/beifen \
  -Demail.enable=false \
  -Dspring.profiles.active=prod \
  -Ddomain.url="xxx" \
  -Dgithub.client-id="xxx" \
  -Dgithub.client-secret="xxx"  \
  -jar dailyWeb-1.0-SNAPSHOT.jar > nohup.out 2>&1 &               
```

ps: jar配置定时备份有些麻烦，默认关闭，手动备份



###   docker
暂无



