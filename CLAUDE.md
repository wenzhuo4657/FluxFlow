# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个名为 **dailyWeb** 的日报管理系统后端，采用前后端分离架构但前端资源被打包到后端一起部署。使用 Java 17 + Spring Boot 3.4.5 + SQLite + MyBatis 技术栈。

## 常用开发命令

### 本地开发启动
```bash
cd dailyWeb && mvn spring-boot:run
```

### 构建项目
```bash
cd dailyWeb && mvn clean package
```

### 跳过测试构建
```bash
cd dailyWeb && mvn clean package -DskipTests
```

### 生产环境部署
```bash
nohup java \
  -Dserver.port=8081 \
  -Ddir.beifen=/root/snap/daily/beifen \
  -Demail.enable=false \
  -Dspring.profiles.active=prod \
  -Ddomain.url="xxx" \
  -Dgithub.client-id="xxx" \
  -Dgithub.client-secret="xxx" \
  -jar dailyWeb-1.0-SNAPSHOT.jar > nohup.out 2>&1 &
```

## 架构设计

项目采用 **DDD (领域驱动设计)** 分层架构：

### 目录结构
```
dailyWeb/src/main/java/cn/wenzhuo4657/dailyWeb/
├── Main.java                     # 应用程序入口
├── config/                       # 配置类 (CORS、异常处理、OAuth等)
├── domain/                       # 领域层 - 核心业务逻辑
│   ├── auth/                     # 认证领域 (GitHub OAuth + Sa-Token)
│   ├── ItemEdit/                 # 文档编辑领域
│   ├── system/                   # 系统管理领域
│   └── Types/                    # 类型管理领域
├── infrastructure/               # 基础设施层
│   ├── database/                 # 数据库相关
│   │   ├── dao/                  # MyBatis数据访问对象
│   │   ├── entity/               # 数据库实体
│   │   └── repository/           # 数据仓库实现
│   └── adapter/                  # 适配器模式 (通知等)
├── tigger/                       # 触发器层
│   ├── http/                     # HTTP控制器
│   └── task/                     # 定时任务
└── types/                        # 通用类型和工具类
```

### 核心模块
- **认证模块**: 使用 Sa-Token + JustAuth 实现 GitHub OAuth 登录
- **文档管理**: 支持文档创建、编辑、分类管理，使用策略模式实现文档编辑
- **系统管理**: 数据库版本管理、备份恢复、系统配置
- **定时任务**: 自动备份邮件发送

## 数据库配置

使用 SQLite 数据库，支持自动结构初始化：
- `dailyWeb/src/main/resources/schema.sql` - 数据库表结构
- `dailyWeb/src/main/resources/data.sql` - 初始化数据
- `dataBaseVersion` 表用于版本控制

主要数据表：`docs`、`docs_item`、`docs_type`、`user`、`user_auth`

## 环境配置

### 开发环境 (dev)
- 端口: 8080
- 启用邮件功能
- 本地 GitHub 配置

### 生产环境 (prod)
- 使用环境变量配置敏感信息
- 可选启用邮件功能
- 支持命令行参数覆盖配置

### 关键配置项
- `dir.beifen`: 备份目录路径 `${daily.home:${user.home}/daily/beifen}`
- `notifierbot.base-url`: 通知器服务地址 `http://localhost:8089`
- `sa-token`: JWT 配置，有效期30天
- `spring.datasource.url`: SQLite 数据库路径

## 开发注意事项

1. **工作目录**: 开发时需要在 `dailyWeb` 子目录下执行 Maven 命令
2. **测试配置**: 项目默认跳过测试 (`<skipTests>true</skipTests>`)
3. **前端集成**: 前端构建产物需放在 `src/main/resources/static/` 目录
4. **认证机制**: 使用 Sa-Token 进行权限管理，GitHub OAuth 作为用户认证
5. **日志系统**: 使用 SLF4J + MyBatis 日志实现
6. **全局异常处理**: `GlobalRestExceptionHandler` 统一处理异常
7. **跨域配置**: `CorsConfig` 处理前后端分离的跨域问题

## 特殊功能

### 定时备份
- 支持邮件自动备份数据库到指定目录
- 可配置备份邮箱和 SMTP 配置

### 通知集成
- 与 NotifierBot 服务集成
- 支持外部通知服务调用

### 数据库版本管理
- 自动检测和应用数据库结构更新
- 支持版本回滚机制

## 部署要求

- **JDK**: 17+
- **Maven**: 3.6+
- **GitHub OAuth**: 需要配置 Client ID 和 Secret
- **备份目录**: 确保有写入权限
- **端口**: 默认8080，生产环境可通过命令行参数修改