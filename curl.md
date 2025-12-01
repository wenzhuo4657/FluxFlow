# NotifierBot HTTP API 接口文档

## 📋 基础信息

| 项目 | 值 |
|------|-----|
| **Base URL** | `http://localhost:8089/api/v1/notifier` |
| **Content-Type** | `application/json` |
| **请求头** | `X-Request-Id` (可选，用于请求追踪) |

---

## 📌 目录

1. [注册通信器](#1-注册通信器)
2. [发送通知消息](#2-发送通知消息)
3. [检查通信器状态](#3-检查通信器状态)
4. [查询支持的通知器类型](#4-查询支持的通知器类型)
5. [查询支持的装饰器类型](#5-查询支持的装饰器类型)
6. [完整使用流程](#完整使用流程示例)
7. [错误码说明](#错误码说明)
8. [注意事项](#注意事项)

---

## 1. 注册通信器

> **接口地址**: `POST /api/v1/notifier/register`
>
> **描述**: 注册一个通知器实例，返回通信器索引（雪花算法生成）
>
> **重要**: 通信器注册后长期有效，建议缓存索引避免重复注册

### 📥 请求示例

#### Telegram 通信器注册

```bash
curl -X POST http://localhost:8089/api/v1/notifier/register \
  -H "Content-Type: application/json" \
  -H "X-Request-Id: req_1234567890_001" \
  -d '{
    "type": "tgBot",
    "paramsJson": "{\"botToken\":\"YOUR_TELEGRAM_BOT_TOKEN\",\"chatId\":\"6550266873\"}",
    "decorators": ["qps"]
  }'
```

#### 邮件通信器注册

```bash
curl -X POST http://localhost:8089/api/v1/notifier/register \
  -H "Content-Type: application/json" \
  -d '{
    "type": "email",
    "paramsJson": "{\"from\":\"your-email@gmail.com\",\"to\":\"recipient@example.com\",\"password\":\"YOUR_APP_PASSWORD\"}",
    "decorators": ["qps"]
  }'
```

### 📊 请求参数

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| `type` | String | ✅ 是 | 通知器类型：`"tgBot"` 或 `"email"` |
| `paramsJson` | String | ✅ 是 | 通知器配置参数（JSON 格式） |
| `decorators` | Array[String] | ❌ 否 | 装饰器列表，可选 `["qps"]` |

#### 📝 paramsJson 格式

**Telegram 配置示例:**

```json
{
  "botToken": "YOUR_BOT_TOKEN",
  "chatId": "TARGET_CHAT_ID"
}
```

**邮件配置示例:**

```json
{
  "from": "your-email@gmail.com",
  "to": "recipient@example.com",
  "password": "YOUR_APP_PASSWORD"
}
```

### 📤 响应示例

```json
{
  "code": 200,
  "message": "通信器注册成功",
  "data": {
    "communicatorIndex": 1735712345678
  },
  "requestId": "req_1234567890_001"
}
```

---

## 2. 发送通知消息

> **接口地址**: `POST /api/v1/notifier/send`
>
> **描述**: 使用已注册的通信器索引发送通知消息

### 📥 请求示例

#### 📱 Telegram 消息发送

```bash
curl -X POST http://localhost:8089/api/v1/notifier/send \
  -H "Content-Type: application/json" \
  -H "X-Request-Id: req_1234567890_002" \
  -d '{
    "communicatorIndex": 1735712345678,
    "type": "tgBot",
    "paramsJson": "{\"title\":\"通知标题\",\"content\":\"通知内容\",\"chatId\":\"6550266873\"}"
  }'
```

#### 📧 邮件发送

```bash
curl -X POST http://localhost:8089/api/v1/notifier/send \
  -H "Content-Type: application/json" \
  -d '{
    "communicatorIndex": 1735712345679,
    "type": "email",
    "paramsJson": "{\"title\":\"邮件标题\",\"content\":\"邮件内容\"}"
  }'
```

### 📊 请求参数

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| `communicatorIndex` | Long | ✅ 是 | 通信器索引（注册时返回） |
| `type` | String | ✅ 是 | 通知器类型（应与注册时一致） |
| `paramsJson` | String | ✅ 是 | 消息内容（JSON 格式） |

#### 📝 paramsJson 格式

**Telegram 消息格式:**

```json
{
  "title": "消息标题",
  "content": "消息内容",
  "chatId": "目标聊天ID"  // 可选，如果不指定则使用注册时的chatId
}
```

**邮件格式:**

```json
{
  "title": "邮件主题",
  "content": "邮件正文",
  "html": false  // 可选，是否为HTML格式
}
```

### 📤 响应示例

#### ✅ 成功响应

```json
{
  "code": 200,
  "message": "信息发送成功",
  "data": "发送成功",
  "requestId": "req_1234567890_002"
}
```

#### ❌ 失败响应

```json
{
  "code": 500,
  "message": "信息发送失败",
  "data": null,
  "requestId": "req_1234567890_002"
}
```

---

## 3. 检查通信器状态

> **接口地址**: `POST /api/v1/notifier/status`
>
> **描述**: 检查通信器是否可用（连接状态、配置有效性等）

### 📥 请求示例

```bash
curl -X POST http://localhost:8089/api/v1/notifier/status \
  -H "Content-Type: application/json" \
  -H "X-Request-Id: req_1234567890_004" \
  -d '{
    "communicatorIndex": 1735712345678
  }'
```

### 📊 请求参数

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| `communicatorIndex` | Long | ✅ 是 | 通信器索引 |

### 📤 响应示例

#### ✅ 正常状态

```json
{
  "code": 200,
  "message": "通信器正常",
  "data": true,
  "requestId": "req_1234567890_004"
}
```

#### ❌ 异常状态

```json
{
  "code": 200,
  "message": "通信器异常",
  "data": false,
  "requestId": "req_1234567890_004"
}
```

---

## 4. 查询支持的通知器类型

> **接口地址**: `GET /api/v1/notifier/support/notifiers`
>
> **描述**: 查询系统支持的通知器类型列表

### 📥 请求示例

```bash
curl -X GET http://localhost:8089/api/v1/notifier/support/notifiers \
  -H "X-Request-Id: req_1234567890_005"
```

### 📤 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "tgBot": "Telegram Bot 通知器",
    "email": "邮件通知器"
  },
  "requestId": "req_1234567890_005"
}
```

---

## 5. 查询支持的装饰器类型

> **接口地址**: `GET /api/v1/notifier/support/decorators`
>
> **描述**: 查询系统支持的装饰器类型列表

### 📥 请求示例

```bash
curl -X GET http://localhost:8089/api/v1/notifier/support/decorators \
  -H "X-Request-Id: req_1234567890_006"
```

### 📤 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "qps": "QPS 限制装饰器"
  },
  "requestId": "req_1234567890_006"
}
```

---

## 完整使用流程示例

### 🚀 步骤 1: 查询支持的通知器类型

```bash
curl -X GET http://localhost:8089/api/v1/notifier/support/notifiers
```

### 🆕 步骤 2: 注册通信器（以 Telegram 为例）

```bash
curl -X POST http://localhost:8089/api/v1/notifier/register \
  -H "Content-Type: application/json" \
  -d '{
    "type": "tgBot",
    "paramsJson": "{\"botToken\":\"YOUR_BOT_TOKEN\",\"chatId\":\"YOUR_CHAT_ID\"}",
    "decorators": ["qps"]
  }'
```

**响应示例:**
```json
{"data":{"communicatorIndex": 1735712345678}}
```

### 📤 步骤 3: 发送通知消息

```bash
curl -X POST http://localhost:8089/api/v1/notifier/send \
  -H "Content-Type: application/json" \
  -d '{
    "communicatorIndex": 1735712345678,
    "type": "tgBot",
    "paramsJson": "{\"title\":\"测试\",\"content\":\"这是一条测试消息\"}"
  }'
```

### 🔍 步骤 4: 检查通信器状态

```bash
curl -X POST http://localhost:8089/api/v1/notifier/status \
  -H "Content-Type: application/json" \
  -d '{"communicatorIndex": 1735712345678}'
```

---

## 错误码说明

| 状态码 | 描述 |
|--------|------|
| `200` | 请求成功 |
| `400` | 请求参数错误 |
| `500` | 服务器内部错误 |

> **注意**: 所有响应都包含 `requestId` 字段，可用于问题追踪和日志查询。

---

## 注意事项

### ⚠️ 重要提示

1. **通信器索引** - 使用雪花算法生成，全局唯一且具有时序性
2. **长期有效** - 通信器注册后长期有效，无需重复注册
3. **QPS 限制** - QPS 装饰器限制默认使用 3 秒滑动窗口进行计数
4. **Telegram Bot** - 需要通过 [@BotFather](https://t.me/BotFather) 创建并获取 Token
5. **Gmail 配置** - 邮件发送需要使用应用专用密码（非登录密码）
6. **请求追踪** - 所有请求建议携带 `X-Request-Id` 头用于问题追踪
7. **JSON 转义** - 消息内容通过 JSON 字符串传递，注意转义特殊字符

### 🔧 环境配置

| 环境变量 | 用途 | 示例 |
|----------|------|------|
| `HOST1` | Redis 服务器地址 | `redis://localhost:6379` |
| `tgBot` | Telegram Bot Token | `123456:ABC-DEF...` |
| `GMAIL_PASSWORD` | Gmail 应用专用密码 | `xxxx xxxx xxxx xxxx` |

### 📚 相关资源

- **项目仓库**: https://github.com/your-repo/notifierbot
- **技术文档**: [CLAUDE.md](./CLAUDE.md)
- **项目规划**: [devops.md](./devops.md)

---

## 🎯 快速测试脚本

### 完整测试流程 (Bash)

```bash
#!/bin/bash

BASE_URL="http://localhost:8089/api/v1/notifier"

# 1. 注册通信器
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/register \
  -H "Content-Type: application/json" \
  -d '{
    "type": "tgBot",
    "paramsJson": "{\"botToken\":\"YOUR_BOT_TOKEN\",\"chatId\":\"YOUR_CHAT_ID\"}",
    "decorators": ["qps"]
  }')

# 提取通信器索引
COMM_INDEX=$(echo $REGISTER_RESPONSE | jq -r '.data.communicatorIndex')

# 2. 发送消息
curl -X POST $BASE_URL/send \
  -H "Content-Type: application/json" \
  -d "{
    \"communicatorIndex\": $COMM_INDEX,
    \"type\": \"tgBot\",
    \"paramsJson\": \"{\\\"title\\\":\\\"测试消息\\\",\\\"content\\\":\\\"这是一条测试消息\\\"}\"
  }"

# 3. 检查状态
curl -X POST $BASE_URL/status \
  -H "Content-Type: application/json" \
  -d "{\"communicatorIndex\": $COMM_INDEX}"
```

---

## 📞 支持

如有问题请提交 [Issue](https://github.com/your-repo/notifierbot/issues) 或联系开发团队。

---

*最后更新: 2025-12-01*
