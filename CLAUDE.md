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