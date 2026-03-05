# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个日报程序的前端项目，使用 Vue 3 + Vite 构建，主要用于编辑和管理日报内容。项目支持富文本编辑、Markdown 渲染、多语言和用户认证等功能。

## 项目结构

主要代码在 `daily/` 目录下：

```
daily/
├── src/
│   ├── page/              # 页面组件
│   │   ├── home/         # 主页面
│   │   │   ├── components/    # 通用组件（内容组件、按钮组件等）
│   │   │   ├── typeDaily/     # 日报相关组件
│   │   │   ├── StickyNote/    # 便签组件
│   │   │   ├── setup/         # 设置页面（包含导入导出、语言切换等）
│   │   │   ├── layout.vue     # 布局组件
│   │   │   └── login/         # 登录组件
│   │   └── oauth/         # OAuth 认证回调页面
│   ├── services/          # API 服务层
│   │   ├── login.ts       # 登录/OAuth相关API
│   │   └── request.ts     # 日报条目CRUD API
│   ├── storage/           # Pinia状态管理
│   │   └── auth.ts        # 认证状态store
│   ├── lib/               # 工具库
│   │   └── http.ts        # Axios封装，含拦截器和认证处理
│   ├── envBus/            # 环境变量总线
│   ├── constants/         # 常量定义（如localStorage keys）
│   ├── router/            # 路由配置
│   ├── i18n.ts            # 国际化配置（支持中英文）
│   ├── main.js            # 应用入口
│   └── App.vue            # 根组件
├── public/                # 静态资源
├── package.json
└── vite.config.js
```

## 开发命令

所有命令需在 `daily/` 目录下执行：

```bash
cd daily
npm install              # 安装依赖
npm run dev              # 启动开发服务器
npm run build            # 构建生产版本
npm run buildx           # 构建生产版本（带 /md-web/ 基础路径）
npm run preview          # 预览构建结果
```

**Node.js版本要求**: 20.19.0 或 >=22.12.0

## 技术栈

- **前端框架**: Vue 3.5.18 (Composition API)
- **构建工具**: Vite 7.0.6
- **UI 组件库**: Element Plus 2.11.5
- **状态管理**: Pinia 3.0.3
- **路由**: Vue Router 4.6.3
- **富文本编辑**: Tiptap 3.7.1
- **Markdown**: markdown-it 14.1.0 + markdown-it-anchor + markdown-it-toc-done-right
- **HTTP 客户端**: Axios 1.12.2
- **国际化**: Vue i18n 11.1.12 (支持中英文)
- **代码高亮**: highlight.js 11.11.1
- **XSS防护**: DOMPurify 3.2.7

## 核心架构

### HTTP层与认证

- HTTP客户端在 `src/lib/http.ts` 中通过 `initHttp(userStore)` 初始化
- 请求拦截器自动添加 `satoken: Bearer <token>` 头
- 响应拦截器统一处理错误码（4xx/5xx）和401自动登出
- 错误提示有防重复机制（5秒内相同错误只显示一次）
- 认证token通过Pinia store (`src/storage/auth.ts`) 和localStorage双重存储

### 路由与鉴权

- 路由配置在 `src/router/index.ts`
- 使用 `meta.requiresAuth` 标记需要认证的路由
- 路由守卫自动检查token，未认证用户重定向到登录页
- 根路径 `/` 根据token状态自动重定向到 `/home` 或 `/login`

### 状态管理

- 认证状态使用Pinia store (`src/storage/auth.ts`)
- 状态自动从localStorage恢复，并在变更时同步到localStorage
- 提供 `setToken`, `setUser`, `logout` 等action

### API服务层

- 所有API调用在 `src/services/` 目录
- `request.ts`: 日报条目CRUD（getMdByType, addItemByType, updateItemByType等）
- `login.ts`: OAuth登录/登出
- 使用TypeScript接口定义请求/响应类型
- 所有API返回统一的 `ApiResponse<T>` 结构 `{ code, message, data }`

### 国际化

- 配置在 `src/i18n.ts`
- 支持中英文切换，语言偏好保存在localStorage
- 使用 `$t('key')` 或 `t('key')` (Composition API) 获取翻译文本

### 富文本编辑

- 基于Tiptap编辑器，支持Markdown语法
- 组件位置: `src/page/home/typeDaily/daily/ItemViewAndEdit.vue`
- 支持Markdown渲染和预览（`markdownView.vue`）

## 环境变量配置

```env
VITE_API_BASE=${DAILY_WEB_API_URL}      # 后端API地址（不含/api后缀）
VITE_BACKGROUD=${DAILY_WEB_BACKGROUND_URL}  # 背景图片URL
```

背景图片在应用启动时通过 `getBackgroundUrl()` API获取并设置CSS变量 `--cdn-url`。

## 部署说明

- 生产环境部署路径: `/md-web/`
- 使用 `npm run buildx` 构建带基础路径的版本
- 需要配置Nginx反向代理将 `/md-web/api/*` 转发到后端API

## AI 助手能力说明

本项目环境配置了专业的 AI Subagents 和 MCP 工具，可以根据任务类型自动或手动调用。

### 🤖 可用的 Subagents（专业代理）

以下代理会在你提出相关请求时**自动触发**：

#### 1. **frontend-developer** - 前端开发专家 ⭐️ Vue 3 专用
- **触发关键词**: "实现组件"、"开发功能"、"重构组件"、"Vue组件"、"响应式布局"、"性能优化"
- **专长**: Vue 3 Composition API、TypeScript、Element Plus、Tiptap 编辑器
- **适用任务**:
  - 实现新的 Vue 组件或页面
  - 重构现有前端代码
  - 状态管理（Pinia）和路由优化
  - UI/UX 实现
- **模型**: sonnet（平衡速度和质量）

**示例触发词**:
```
"帮我实现一个日报编辑组件"
"优化登录表单的响应式布局"
"重构 ItemViewAndEdit.vue 组件"
```

#### 2. **code-reviewer-pro** - 代码审查专家
- **触发关键词**: "审查代码"、"代码质量"、"检查bug"、"安全性检查"、"性能问题"
- **专长**: 代码质量、安全性漏洞、可维护性、最佳实践
- **适用任务**:
  - 编写代码后自动触发审查
  - 提交前的质量检查
  - 发现潜在的安全漏洞和性能问题
- **模型**: haiku（快速审查）

**示例触发词**:
```
"审查 http.ts 的代码质量"
"检查登录组件是否有安全漏洞"
"review 这个文件"
```

#### 3. **typescript-pro** - TypeScript 架构专家
- **触发关键词**: "类型定义"、"TypeScript类型"、"架构设计"、"类型安全"
- **专长**: 高级类型系统、泛型、工具类型、类型推导
- **适用任务**:
  - 设计复杂的类型定义
  - 解决 TypeScript 类型错误
  - 重构以提高类型安全性
  - API 接口类型设计
- **模型**: sonnet

**示例触发词**:
```
"为 API 响应定义 TypeScript 类型"
"优化 auth store 的类型定义"
"解决这个 TypeScript 类型错误"
```

#### 4. **ui-designer** - UI 设计师（通过 21st.dev MCP）
- **触发关键词**: "/ui"、"/21"、设计界面、UI 组件
- **专长**: UI 组件生成、视觉设计、交互设计
- **适用任务**:
  - 快速生成 UI 组件代码
  - 改进现有 UI 设计
  - 获取 UI 设计灵感

**示例触发词**:
```
"/ui 创建一个登录表单组件"
"/21 日报编辑器工具栏"
```

### 🔧 可用的 MCP 工具

以下 MCP 工具会在相应场景中**自动使用**：

#### 1. **21st.dev UI 组件构建器** ⭐️
- **工具**: `mcp__magic__21st_magic_component_builder`
- **用途**: 根据描述生成 Vue/React UI 组件代码
- **触发**: 用户请求创建 UI 组件时（如 "/ui" 或 "/21" 命令）
- **示例**: "创建一个带验证的表单组件"

#### 2. **21st.dev UI 组件优化器**
- **工具**: `mcp__magic__21st_magic_component_refiner`
- **用途**: 改进和优化现有组件的 UI 设计
- **触发**: 用户请求改进组件 UI 时
- **示例**: "优化这个按钮的样式"

#### 3. **21st.dev UI 灵感库**
- **工具**: `mcp__magic__21st_magic_component_inspiration`
- **用途**: 获取 UI 组件设计灵感和预览
- **触发**: 用户请求查看 UI 示例时

#### 4. **Logo 搜索**
- **工具**: `mcp__magic__logo_search`
- **用途**: 搜索并生成公司/品牌的 Logo 组件（JSX/TSX/SVG）
- **触发**: 用户请求添加 logo 时
- **示例**: "添加 GitHub logo"

#### 5. **网页阅读器**
- **工具**: `mcp__web_reader__webReader`
- **用途**: 抓取并转换网页内容为 Markdown 格式
- **触发**: 用户请求读取网页内容时
- **示例**: "读取这个文档的内容"

#### 6. **图片分析**
- **工具**: `mcp__4_5v_mcp__analyze_image`
- **用途**: 分析远程图片 URL 的内容
- **触发**: 用户提供图片 URL 并要求分析时

#### 7. **Chrome DevTools 自动化**
- **工具**: `mcp__chrome-devtools__*` 系列
- **用途**: 浏览器自动化操作（点击、填充表单、截图、性能测试等）
- **触发**: 前端测试和浏览器交互任务

### 📋 如何调用这些能力

#### 方式 1：自然语言触发（推荐）⭐️
直接描述你的需求，AI 会自动选择合适的 agent：

```markdown
# 前端开发 - 自动触发 frontend-developer
"帮我实现一个富文本编辑器组件，支持 Markdown 预览"

# 代码审查 - 自动触发 code-reviewer-pro
"我刚修改了 http.ts，帮我检查一下代码质量"

# TypeScript - 自动触发 typescript-pro
"为 API 响应定义完整的 TypeScript 类型"
```

#### 方式 2：明确指定 Agent
使用明确的指令：

```markdown
"请使用 frontend-developer 代理实现一个日报编辑组件"
"请使用 code-reviewer-pro 审查 src/services/request.ts"
"请使用 typescript-pro 优化类型定义"
```

#### 方式 3：使用 UI 快捷命令
```markdown
"/ui 创建一个登录表单"        # 触发 UI 组件生成器
"/21 日报列表卡片"            # 触发 UI 组件生成器
"/logo GitHub"                # 触发 Logo 搜索
```

### 🎯 任务类型与 Agent 映射表

| 任务类型 | 自动触发 Agent | 触发关键词示例 |
|---------|---------------|---------------|
| 实现 Vue 组件/页面 | frontend-developer | "实现组件"、"开发功能" |
| 优化前端代码 | frontend-developer | "优化性能"、"重构代码" |
| 代码审查/质量检查 | code-reviewer-pro | "审查代码"、"检查bug" |
| TypeScript 类型设计 | typescript-pro | "类型定义"、"解决类型错误" |
| 快速生成 UI 组件 | ui-designer (MCP) | "/ui"、"/21"、"设计界面" |
| 架构设计 | typescript-pro | "架构设计"、"技术方案" |

### 💡 最佳实践

1. **优先使用自然语言**：直接描述需求，让 AI 自动选择最合适的 agent
2. **明确任务目标**：具体说明要实现什么功能，而不是只说"优化"
3. **提供上下文**：说明相关文件路径、技术栈要求和约束条件
4. **分阶段执行**：复杂任务可以分多个步骤，让不同 agent 各司其职

**示例工作流**:
```markdown
# 第一步：使用 frontend-developer 实现功能
"实现一个日报编辑组件，使用 Tiptap 编辑器"

# 第二步：使用 code-reviewer-pro 审查代码（自动触发）
"帮我审查刚才实现的组件"

# 第三步：使用 ui-designer 优化 UI（如需要）
"/ui 优化编辑器的工具栏设计"
```

## 开发注意事项

### 代码规范
1. **路径别名**: `@` 指向 `src` 目录，使用 `@/` 导入模块
2. **Vue 组件**: 必须使用 Composition API（`<script setup>`）风格
3. **TypeScript**:
   - API 响应的大数字 ID 字段使用 `string` 类型避免精度损失
   - 优先使用接口（interface）定义数据结构
   - 使用泛型提高代码复用性
4. **API 通信**:
   - 所有请求/响应使用 JSON 格式（文件上传除外）
   - 401 错误会自动清除登录状态并跳转登录页
   - 使用 `src/lib/http.ts` 中的 http 实例发起请求

### AI 辅助开发工作流

#### 情景 1：实现新功能
```markdown
# 步骤 1: 使用 frontend-developer 实现功能
"实现一个日报导出功能，支持导出为 Markdown 文件"

# 步骤 2: AI 会自动使用 code-reviewer-pro 审查代码
#（无需手动触发）

# 步骤 3: 如有类型问题，使用 typescript-pro
"优化导出功能的 TypeScript 类型定义"
```

#### 情景 2：快速 UI 开发
```markdown
# 使用 /ui 或 /21 命令快速生成 UI
"/ui 创建一个设置页面的表单，包含语言切换和主题选择"

# 然后使用 frontend-developer 集成到项目
"将上面的 UI 组件集成到 setup/index.vue 中"
```

#### 情景 3：代码优化
```markdown
# 直接描述优化需求
"优化 ItemViewAndEdit.vue 的性能，减少不必要的重渲染"

# AI 会自动：
# 1. 使用 frontend-developer 分析并优化代码
# 2. 使用 code-reviewer-pro 检查优化后的质量
```

### 常见任务快速触发词

| 需求 | 快速触发词 |
|------|-----------|
| 创建新组件 | "实现一个 [组件名] 组件" |
| 修复 Bug | "修复 [文件/功能] 的 bug" |
| 代码审查 | "审查 [文件名]" / "检查代码质量" |
| 类型优化 | "优化类型定义" / "添加 TypeScript 类型" |
| UI 生成 | "/ui [组件描述]" / "/21 [组件描述]" |
| 性能优化 | "优化 [组件/页面] 性能" |
| 架构设计 | "设计 [功能] 的架构" |

### ⚠️ 重要提醒

1. **Agent 自动触发**: 不需要手动调用 Task 工具，只需描述需求，AI 会自动选择合适的 agent
2. **MCP 工具**: MCP 工具（如 21st.dev、Logo 搜索）会在相关任务中自动使用
3. **上下文提供**: 描述需求时，尽量提供：
   - 目标文件路径（如 `src/page/home/typeDaily/daily/ItemViewAndEdit.vue`）
   - 技术要求（如"使用 Element Plus 组件"）
   - 功能约束（如"需要支持 Markdown 渲染"）
4. **分步执行**: 复杂任务可以分多个步骤，让不同 agent 各司其职