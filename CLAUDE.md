# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个名为 **FluxFlow** 的个人日报管理系统，采用微服务架构，包含两个核心服务：
1. **dailyWeb**: 主应用服务，负责日报管理、文档编辑、用户认证等核心业务
2. **notifier**: 通知服务，负责邮件、Telegram Bot 等多渠道消息通知

技术栈: Java 17 + Spring Boot 3.4.5 + SQLite + MyBatis + Telegram Bot API

## 常用开发命令

### 启动 dailyWeb 主服务
```bash
cd dailyWeb && mvn spring-boot:run
```

### 启动 notifier 通知服务
```bash
cd notifier && mvn spring-boot:run
```

### 构建 dailyWeb
```bash
cd dailyWeb && mvn clean package
```

### 构建 notifier
```bash
cd notifier && mvn clean package
```

### Docker 部署
```bash
# 构建 notifier 镜像
docker build -t notifier:1.0-SNAPSHOT notifier/

# 使用 docker-compose 启动所有服务
cd devops && docker-compose up -d
```

### 生产环境部署
```bash
# dailyWeb 服务
nohup java \
  -Dserver.port=8081 \
  -Ddir.beifen=/root/snap/daily/beifen \
  -Demail.enable=false \
  -Dspring.profiles.active=prod \
  -Ddomain.url="xxx" \
  -Dgithub.client-id="xxx" \
  -Dgithub.client-secret="xxx" \
  -jar dailyWeb/dailyWeb-1.0-SNAPSHOT.jar > nohup.out 2>&1 &

# notifier 服务 (默认端口 8089)
java -jar notifier/target/app-1.0-SNAPSHOT.jar
```

## 架构设计

项目采用 **DDD (领域驱动设计)** 分层架构和微服务架构。

### 微服务架构
```
dailyWeb-back/
├── dailyWeb/          # 主应用服务 (端口: 8080/8081)
├── notifier/          # 通知服务 (端口: 8089)
└── devops/           # 部署配置
```

### dailyWeb 服务结构
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
│   ├── database/                 # 数据库相关 (MyBatis)
│   │   ├── dao/                  # MyBatis数据访问对象
│   │   ├── entity/               # 数据库实体
│   │   └── repository/           # 数据仓库实现
│   └── adapter/                  # 适配器层
│       └── notifier/             # Notifier服务适配器 (ApiServiceHttpImpl)
├── tigger/                       # 触发器层
│   ├── http/                     # HTTP控制器
│   ├── task/                     # 定时任务 (邮件备份)
│   └── mcp/                      # MCP协议相关
└── types/                        # 通用类型和工具类
```

### notifier 服务结构
```
notifier/src/main/java/cn/wenzhuo4657/noifiterBot/app/
├── App.java                      # 应用程序入口
├── config/                       # 配置类 (CORS、缓存、Thymeleaf)
├── domain/                       # 领域层
│   ├── auth/                     # 认证领域
│   └── notifier/                 # 通知核心领域
│       ├── service/              # 通知策略 (Email、TelegramBot)
│       ├── factory/              # 通知工厂
│       └── decorator/            # 装饰器 (QPS限流)
├── infrastructure/               # 基础设施层
│   ├── cache/                    # 缓存策略 (本地缓存、Redis、Valkey)
│   └── database/                 # 数据库相关
├── tigger/                       # 触发器层
│   └── http/                     # HTTP控制器 (通知API)
└── types/                        # 通用类型
```

### 核心设计模式
1. **策略模式**: 文档编辑策略 (dailyWeb)、通知渠道策略 (notifier)
2. **工厂模式**: 通知器工厂 (NormallyPondFactory)
3. **装饰器模式**: QPS限流装饰器 (QpsMaxDecorator)
4. **适配器模式**: Notifier服务适配器 (ApiServiceHttpImpl)

### 服务间通信
- **dailyWeb → notifier**: HTTP REST API (通过 RestTemplate)
- **配置项**: `dailyWeb.application.yml` 中 `notifierbot.base-url` 指定 notifier 服务地址
- **API示例**: `POST /api/v1/notifier/register` 注册通知器

## 数据库配置

使用 SQLite 数据库，支持自动结构初始化：
- `dailyWeb/src/main/resources/schema.sql` - 数据库表结构
- `dailyWeb/src/main/resources/data.sql` - 初始化数据
- `dataBaseVersion` 表用于版本控制

主要数据表：`docs`、`docs_item`、`docs_type`、`user`、`user_auth`

## 环境配置

### dailyWeb 配置

#### 开发环境 (dev)
- 端口: 8080
- 启用邮件功能
- 支持前端开发模式 (Vite dev server on localhost:5173)
- 配置文件: `dailyWeb/src/main/resources/application-dev.yml`

#### 生产环境 (prod)
- 端口: 8081 (可通过命令行参数修改)
- 使用环境变量配置敏感信息
- 可选启用邮件功能
- 配置文件: `dailyWeb/src/main/resources/application-prod.yml`

#### 关键配置项
- `dir.beifen`: 备份目录路径 `${daily.home:${user.home}/daily/beifen}`
- `notifierbot.base-url`: 通知器服务地址 `http://localhost:8089`
- `domain.url`: 应用域名，用于 OAuth 回调
- `github.client-id/client-secret`: GitHub OAuth 配置
- `sa-token`: JWT 配置，有效期30天
- `spring.datasource.url`: SQLite 数据库路径

### notifier 配置

#### 默认配置
- 端口: 8089
- 支持多种缓存策略: 本地缓存 (Caffeine)、Redis、Valkey
- 配置文件: `notifier/src/main/resources/application.yml` 和 `application-cache.yml`

#### 支持的通知渠道
- **Email**: Gmail SMTP
- **Telegram Bot**: 通过 Telegram Bot API
- 可扩展其他通知渠道

## 开发注意事项

### dailyWeb 开发
1. **工作目录**: 开发时需要在 `dailyWeb` 子目录下执行 Maven 命令
2. **测试配置**: 项目默认跳过测试 (`<skipTests>true</skipTests>`)
3. **前端集成**: 前端构建产物需放在 `src/main/resources/static/` 目录
4. **认证机制**: 使用 Sa-Token 进行权限管理，GitHub OAuth 作为用户认证
5. **日志系统**: 使用 SLF4J + MyBatis 日志实现
6. **全局异常处理**: `GlobalRestExceptionHandler` 统一处理异常
7. **跨域配置**: `CorsConfig` 处理前后端分离的跨域问题

### notifier 开发
1. **服务依赖**: notifier 可独立运行，但 dailyWeb 需要调用 notifier 的 API
2. **缓存配置**: 默认使用本地缓存 (Caffeine)，可在 `application-cache.yml` 中切换到 Redis/Valkey
3. **通知器注册**: 使用装饰器模式扩展功能 (如 QPS 限流)
4. **Lua脚本**: 使用 Redis Lua 脚本实现 QPS 限流逻辑 (`scripts/qps_MAX.lua`)

### 通用注意事项
1. **多服务协调**: 开发时需要同时启动 dailyWeb 和 notifier 服务
2. **API版本管理**: notifier API 使用 `/api/v1/` 前缀
3. **错误处理**: 两个服务都使用统一的异常处理和响应格式
4. **配置外部化**: 敏感信息通过环境变量注入

## 专业 Agents 使用指南

项目配置了多个专业化 AI Agents 来提升代码质量和开发效率。Claude Code 应该在以下场景主动使用这些 agents：

### 可用 Agents 列表

#### 1. **code-reviewer-pro** - 代码审查专家
- **用途**: 对代码进行全面的质量、安全性和可维护性审查
- **触发时机**:
  - ✅ **每次完成代码修改或编写新功能后**（必须自动触发）
  - 提交 PR 之前
  - 重大重构后
- **使用方式**: `Task tool with subagent_type=code-reviewer-pro`
- **检查重点**: 安全漏洞、代码质量、性能问题、最佳实践、测试覆盖

#### 2. **test-automator** - 测试自动化专家
- **用途**: 设计、实现和维护自动化测试策略
- **触发时机**:
  - ✅ **需要为新功能编写测试时主动使用**
  - 提升测试覆盖率
  - 设置 CI/CD 测试管道
  - 优化测试流程
- **使用方式**: `Task tool with subagent_type=test-automator`
- **专业领域**: 单元测试、集成测试、E2E 测试、测试策略、CI/CD 集成

#### 3. **debugger** - 调试专家
- **用途**: 专门处理错误、测试失败和异常行为
- **触发时机**:
  - ✅ **遇到任何错误或测试失败时主动使用**
  - 性能问题排查
  - 意外行为调查
- **使用方式**: `Task tool with subagent_type=debugger`
- **能力**: 系统化问题诊断、根本原因分析、修复验证

#### 4. **backend-architect** - 后端架构师
- **用途**: 设计健壮、可扩展、可维护的后端系统
- **触发时机**:
  - 设计新功能或服务时
  - 架构决策前
  - API 设计评审
- **使用方式**: `Task tool with subagent_type=backend-architect`
- **流程**: 需求收集 → 架构设计 → 方案提案

#### 5. **architect-reviewer** - 架构审查专家
- **用途**: 审查架构一致性、模式遵守和可维护性
- **触发时机**:
  - ✅ **结构性变更后主动审查**
  - 引入新服务后
  - API 修改后
- **使用方式**: `Task tool with subagent_type=architect-reviewer`
- **检查重点**: 系统完整性、架构一致性、可维护性

#### 6. **qa-expert** - 质量保证专家
- **用途**: 设计和实施全面的 QA 流程
- **触发时机**:
  - ✅ **需要开发测试策略时主动使用**
  - 执行详细测试计划
  - 质量改进建议
- **使用方式**: `Task tool with subagent_type=qa-expert`
- **能力**: QA 流程设计、测试计划执行、数据驱动反馈

### Agent 使用工作流

#### 标准开发流程
1. **编写/修改代码** → 自动使用 `code-reviewer-pro` 审查
2. **发现 bug/错误** → 自动使用 `debugger` 诊断
3. **需要添加测试** → 自动使用 `test-automator` 实现
4. **架构变更** → 自动使用 `architect-reviewer` 审查

#### 并行使用策略
当需要多个 agents 时，在单个响应中并行启动多个 agents：
```xml
<Task tool call for code-reviewer-pro>
<Task tool call for architect-reviewer>
```

### 重要规则

- ✅ **主动使用**: 标记了 "主动使用" 的 agents 必须在相应场景自动触发，无需用户请求
- ⚠️ **优先级**: 修复 bug 和错误时，debugger 应优先于其他 agents
- 📋 **上下文**: Agents 可以看到完整对话历史，无需重复上下文
- 🔄 **串行 vs 并行**: 独立任务使用并行，依赖任务使用串行

## 核心功能模块

### dailyWeb 核心功能
1. **文档管理系统**
   - 支持多种文档类型 (日报、规划、手帐等)
   - 文档项 (Item) 的增删改查
   - 版本号机制处理多端并发修改
   - 使用策略模式实现不同类型文档的编辑逻辑

2. **用户认证与授权**
   - GitHub OAuth 登录 (JustAuth)
   - Sa-Token JWT 会话管理
   - 多端登录支持

3. **系统管理**
   - 数据库版本管理
   - 数据导入导出
   - 自动邮件备份 (定时任务)

4. **通知集成**
   - 通过适配器模式调用 notifier 服务
   - 支持邮件通知、Telegram Bot 通知

### notifier 核心功能
1. **多渠道通知**
   - Email 通知器 (Gmail SMTP)
   - Telegram Bot 通知器
   - 可扩展的通知器接口

2. **通知器管理**
   - 通知器注册与配置
   - 通知器状态查询
   - QPS 限流装饰器

3. **缓存策略**
   - 本地缓存 (Caffeine)
   - Redis/Valkey 分布式缓存
   - 使用策略模式支持多种缓存后端

4. **API 接口**
   - RESTful API 设计
   - 统一的响应格式 (ApiResponse)
   - 支持通知器注册、状态查询、消息发送

## 部署要求

### 系统要求
- **JDK**: 17+
- **Maven**: 3.6+
- **Docker** (可选): 用于容器化部署

### 外部服务依赖
- **GitHub OAuth**: 需要配置 Client ID 和 Secret
- **Gmail SMTP** (可选): 用于邮件通知和备份
- **Redis/Valkey** (可选): 用于 notifier 分布式缓存
- **Telegram Bot** (可选): 用于 Telegram 通知

### 端口配置
- **dailyWeb**: 8080 (dev) / 8081 (prod)
- **notifier**: 8089

### 文件系统
- **备份目录**: 确保有写入权限 (`dir.beifen`)
- **数据库文件**: SQLite 文件路径

## API 接口说明

### dailyWeb 主要 API
- `POST /api/oauth/login/github` - GitHub OAuth 登录
- `GET /api/docs/*` - 文档相关接口
- `GET /api/item/*` - 文档项相关接口
- `POST /api/system/backup` - 数据库备份

### notifier 主要 API
- `POST /api/v1/notifier/register` - 注册通知器
- `GET /api/v1/notifier/index` - 查询通知器状态
- `POST /api/v1/notifier/send` - 发送通知消息
- `GET /api/v1/notifier/status` - 查询通知器在线状态

## 参考文档

- **项目计划**: `devops.md` - 包含详细的项目规划和开发进度
- **项目README**: `README.md` - 快速开始指南
- **GitHub Issues**: 项目开发任务和问题追踪