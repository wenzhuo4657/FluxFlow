# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个日报管理系统的后端项目，包含两个独立的Spring Boot模块：
- **dailyWeb**：日报管理核心业务（端口8080）
- **notifier**：消息通知服务（端口8089）

项目采用领域驱动设计（DDD）架构，使用SQLite作为嵌入式数据库，支持GitHub OAuth登录和邮件定时备份。

## 开发环境配置

### 前置要求
- JDK 17+
- Maven 3.6+
- Git

### 环境配置
1. 克隆仓库：`git clone <repository-url>`
2. 配置GitHub OAuth：
   - 在GitHub创建OAuth App
   - 设置环境变量：
     ```bash
     export GITHUB_CLIENT_ID="your-client-id"
     export GITHUB_CLIENT_SECRET="your-client-secret"
     ```

## 构建和运行命令

### 构建项目
```bash
# 构建dailyWeb模块
cd dailyWeb
mvn clean package

# 构建notifier模块
cd ../notifier
mvn clean package
```

### 运行项目
```bash
# 开发环境运行dailyWeb
cd dailyWeb
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境运行
java -jar target/dailyWeb-1.0-SNAPSHOT.jar --spring.profiles.active=prod

# 运行notifier模块
cd notifier
mvn spring-boot:run
```

### 测试命令
```bash
# 跳过测试构建（默认配置）
mvn clean package -DskipTests

# 运行测试
mvn test
```

## 项目架构

### 模块结构
```
dailyWeb-back/
├── dailyWeb/          # 日报管理主模块
│   ├── src/main/java/cn/wenzhuo4657/dailyWeb/
│   │   ├── config/           # 配置类（Cors、过滤器、异常处理等）
│   │   ├── domain/           # 领域层（DDD核心）
│   │   │   ├── auth/         # 认证领域
│   │   │   ├── ItemEdit/     # 日报编辑领域
│   │   │   ├── system/       # 系统领域
│   │   │   └── Types/        # 类型管理领域
│   │   ├── infrastructure/   # 基础设施层
│   │   │   ├── adapter/      # 外部服务适配器
│   │   │   └── database/     # 数据库访问
│   │   ├── tigger/           # 应用层（控制器、定时任务）
│   │   │   ├── http/         # REST API控制器
│   │   │   └── task/         # 定时任务调度
│   │   └── types/            # 类型定义和工具类
│   └── src/main/resources/
│       ├── application.yml   # 主配置文件
│       ├── schema.sql        # 数据库初始化脚本
│       └── mapper/           # MyBatis XML映射文件
│
└── notifier/          # 通知器模块
    └── src/main/java/cn/wenzhuo4657/noifiterBot/app/
        ├── config/           # 配置类
        ├── domain/           # 通知领域
        └── App.java          # 主启动类
```

### 技术栈
- **框架**：Spring Boot 3.4.5, MyBatis 3.0.4
- **认证**：Sa-Token 1.44.0, JustAuth 1.4.0 (GitHub OAuth)
- **数据库**：SQLite（嵌入式）
- **工具库**：Hutool 5.8.41, Fastjson2 2.0.43
- **通知**：Telegram Bots 9.2.0, Redisson 3.38.1（notifier模块）

### 数据库设计
使用SQLite数据库，核心表包括：
- `docs` - 文档表
- `docs_item` - 文档项表（日报内容）
- `docs_type` - 文档类型表
- `user` - 用户表
- `user_auth` - 用户权限表

数据库初始化通过`schema.sql`和`data.sql`自动执行。

## 配置管理

### 配置文件
- `application.yml` - 基础配置
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

### 关键配置项
```yaml
# 数据库配置
dir:
  beifen: ${daily.home:${user.home}/daily/beifen}  # 备份目录
  db: beifen.db                                    # 数据库文件

# GitHub OAuth配置（通过环境变量设置）
github:
  client-id: ${GITHUB_CLIENT_ID}
  client-secret: ${GITHUB_CLIENT_SECRET}
  redirect-uri: ${domain.url}/api/oauth/callback/github

# 通知器服务地址
notifierbot:
  base-url: ${NOTIFIERBOT_BASE_URL:http://localhost:8089}
```

### 环境变量
```bash
# 必需的环境变量
export GITHUB_CLIENT_ID="your-client-id"
export GITHUB_CLIENT_SECRET="your-client-secret"

# 可选的环境变量
export NOTIFIERBOT_BASE_URL="http://localhost:8089"  # notifier服务地址
export DAILY_HOME="/path/to/backup/dir"              # 备份目录
```

## API设计

### 认证流程
1. 前端重定向到GitHub OAuth授权页面
2. GitHub回调到`/api/oauth/callback/github`
3. 后端处理回调，创建/获取用户，生成Sa-Token
4. 重定向回前端携带token

### 主要API端点
- `GET /api/oauth/callback/github` - GitHub OAuth回调
- `POST /api/item` - 创建日报项
- `GET /api/item/{id}` - 获取日报项
- `GET /api/types` - 获取文档类型
- `GET /api/docs` - 获取文档列表

### 认证头
```http
Authorization: Bearer {satoken}
```

## 定时任务

### 邮件备份任务
配置在`EmailBackupScheduler.java`中，每天0点执行：
```java
@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
public void backup() {
    // 备份数据库并发送邮件
}
```

### 启用条件
需要在配置中设置`email.enable: true`并配置Gmail SMTP信息。

## 模块间通信

### dailyWeb → notifier
通过HTTP API调用notifier服务：
```java
// 配置notifier服务地址
notifierbot:
  base-url: ${NOTIFIERBOT_BASE_URL:http://localhost:8089}

// 调用示例
String url = baseUrl + "/api/notifier/send/gmail";
```

### 通知类型
- 邮件通知（Gmail）
- Telegram机器人消息
- 系统内部通知

## 开发指南

### 代码组织原则
1. **领域驱动设计**：业务逻辑放在domain包中
2. **依赖倒置**：领域层定义接口，基础设施层实现
3. **单一职责**：每个类/方法只做一件事
4. **接口隔离**：通过接口定义契约

### 添加新功能步骤
1. 在domain包中定义领域模型和接口
2. 在infrastructure包中实现技术细节
3. 在tigger/http包中添加控制器
4. 在resources/mapper中添加MyBatis映射

### 调试技巧
1. **查看SQL日志**：在application-dev.yml中设置：
   ```yaml
   mybatis:
     configuration:
       log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
   ```
2. **启用详细日志**：设置日志级别为DEBUG
3. **使用开发环境**：`spring.profiles.active=dev`

## 部署指南

### JAR部署
```bash
java \
  -Dserver.port=8081 \
  -Ddir.beifen=/path/to/backup \
  -Demail.enable=false \
  -Dspring.profiles.active=prod \
  -Ddomain.url="https://your-domain.com" \
  -Dgithub.client-id="your-client-id" \
  -Dgithub.client-secret="your-client-secret" \
  -jar dailyWeb-1.0-SNAPSHOT.jar
```

### 注意事项
1. **SQLite并发**：配置`maximum-pool-size: 1`避免写锁
2. **文件权限**：确保备份目录有读写权限
3. **环境变量**：生产环境务必使用环境变量而非硬编码
4. **防火墙**：开放对应端口（8080/8089）

## 常见问题

### 数据库连接问题
- 检查`dir.beifen`目录是否存在且有写权限
- 确认SQLite JDBC驱动在classpath中

### OAuth认证失败
- 检查GitHub OAuth App的配置是否正确
- 确认回调URL与部署地址匹配
- 检查环境变量`GITHUB_CLIENT_ID`和`GITHUB_CLIENT_SECRET`

### 邮件发送失败
- 检查Gmail应用专用密码是否正确
- 确认`email.enable`设置为true
- 检查SMTP配置和网络连接

### 模块间通信失败
- 确认notifier服务正在运行（端口8089）
- 检查`notifierbot.base-url`配置
- 查看网络防火墙设置