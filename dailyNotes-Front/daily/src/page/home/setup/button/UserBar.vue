<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/storage/auth'
import { storeToRefs } from 'pinia'
import Logout from './Button/Logout.vue'

// 用户信息（从 Pinia 取出保持响应性）
const auth = useAuthStore()
const { user } = storeToRefs(auth)

const resolveName = (obj) => {
  if (!obj || typeof obj !== 'object') return ''
  const names = [
    obj.username,
    obj.name,
    obj.nickname,
    obj.nickName,
    obj.login,
    obj.user_name,
    obj.userName,
    obj.displayName,
  ]
  const found = names.find(v => typeof v === 'string' && v.trim())
  if (found) return found
  if (obj.user && typeof obj.user === 'object') return resolveName(obj.user)
  if (obj.profile && typeof obj.profile === 'object') return resolveName(obj.profile)
  return ''
}

const displayName = computed(() => {
  const val = user.value
  const name = resolveName(val)
  return name || '未登录'
})

const resolveAvatar = (obj) => {
  if (!obj || typeof obj !== 'object') return ''
  const pics = [
    obj.avatar,
    obj.avatarUrl,
    obj.avatar_url,
    obj.picture,
    obj.photo,
    obj.image,
  ]
  const found = pics.find(v => typeof v === 'string' && v.trim())
  if (found) return found
  if (obj.user && typeof obj.user === 'object') return resolveAvatar(obj.user)
  if (obj.profile && typeof obj.profile === 'object') return resolveAvatar(obj.profile)
  return ''
}

const avatarUrl = computed(() => {
  const val = user.value
  return resolveAvatar(val) || 'https://avatars.githubusercontent.com/u/117806849?v=4'
})
const onAvatarError = (e) => {
  e.target.src = 'https://avatars.githubusercontent.com/u/117806849?v=4'
}
</script>

<template>
    <div class="system-button-bar">
        <!-- 用户头像 -->
        <div class="user-avatar">
            <img :src="avatarUrl" alt="用户头像" @error="onAvatarError" />
        </div>

        <!-- 用户名 -->
        <div class="user-name" :title="displayName">
            {{ displayName }}
        </div>

        <!-- 登出按钮 -->
        <div class="logout-section">
            <Logout></Logout>
        </div>
    </div>
</template>
<style>
/* ========== 主容器 ========== */
.system-button-bar {
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 10px 16px;
    margin: 0;
    box-sizing: border-box;
    /* 透明背景，无颜色 */
    background: transparent;
    /* 细腻边框 */
    border-radius: 12px;
    border: none;
    /* 柔和阴影 */
    box-shadow: none;
    /* 平滑过渡 */
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 悬停状态：去除所有阴影和变换效果 */
.system-button-bar:hover {
    box-shadow: none;
    transform: none;
}

/* ========== 用户头像 ========== */
.user-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    overflow: hidden;
    /* 透明背景，无颜色 */
    background: transparent;
    /* 柔和阴影 */
    box-shadow:
        0 4px 12px rgba(0, 0, 0, 0.15),
        0 2px 4px rgba(0, 0, 0, 0.1);
    /* 双层边框效果 */
    border: 3px solid rgba(255, 255, 255, 0.95);
    outline: none;
    flex-shrink: 0;
    /* 平滑过渡动画 */
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
}

/* 头像光晕效果 - 已移除 */
.user-avatar::before {
    display: none;
}

/* 悬停状态：简化效果 */
.user-avatar:hover {
    transform: scale(1.08);
    box-shadow:
        0 6px 20px rgba(0, 0, 0, 0.2),
        0 3px 6px rgba(0, 0, 0, 0.15);
    border-color: rgba(255, 255, 255, 1);
}

.user-avatar:hover::before {
    opacity: 0;
}

/* 焦点状态：键盘导航的可访问性 */
.user-avatar:focus-within {
    outline: 2px solid #667eea;
    outline-offset: 2px;
}

.user-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.3s ease;
}

.user-avatar:hover img {
    transform: scale(1.05);
}

/* ========== 用户名 ========== */
.user-name {
    flex: 1;
    font-size: 15px;
    font-weight: 600;
    /* 普通文字颜色 */
    color: #2c3e50;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    padding: 0 12px;
    line-height: 1.5;
    /* 文字阴影增强可读性 */
    filter: drop-shadow(0 1px 1px rgba(255, 255, 255, 0.8));
    transition: all 0.2s ease;
}

/* 悬停状态：文字稍微变亮 */
.user-name:hover {
    color: #1a2a3a;
    transform: translateX(2px);
}

/* ========== 登出按钮区域 ========== */
.logout-section {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    padding-left: 8px;
}

/* Element Plus 按钮自定义样式 */
.logout-section .el-button {
    margin: 0;
    padding: 8px 14px;
    /* 透明背景，无颜色 */
    background: transparent;
    border: 1px solid rgba(0, 0, 0, 0.15);
    border-radius: 8px;
    /* 文字样式 */
    color: #495057;
    font-weight: 500;
    font-size: 13px;
    /* 柔和阴影 */
    box-shadow: none;
    /* 平滑过渡 */
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
}

/* 按钮悬停状态 */
.logout-section .el-button:hover {
    background: rgba(0, 0, 0, 0.05);
    border-color: rgba(0, 0, 0, 0.25);
    color: #2d3436;
    transform: translateY(-1px);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 按钮点击状态 */
.logout-section .el-button:active {
    transform: translateY(0);
    box-shadow: none;
    background: rgba(0, 0, 0, 0.08);
}

/* 按钮焦点状态：可访问性 */
.logout-section .el-button:focus {
    outline: 2px solid #667eea;
    outline-offset: 2px;
}

/* ========== 响应式设计 ========== */
/* 小屏幕适配 */
@media (max-width: 768px) {
    .system-button-bar {
        padding: 8px 12px;
    }

    .user-avatar {
        width: 40px;
        height: 40px;
        border-width: 2px;
    }

    .user-name {
        font-size: 13px;
        padding: 0 8px;
    }

    .logout-section .el-button {
        padding: 6px 10px;
        font-size: 12px;
    }
}

/* 超小屏幕适配 */
@media (max-width: 480px) {
    .user-name {
        display: none; /* 极小屏幕隐藏用户名 */
    }

    .logout-section .el-button {
        padding: 6px 8px;
    }
}

/* ========== 暗色模式支持 ========== */
@media (prefers-color-scheme: dark) {
    .system-button-bar {
        background: transparent;
        border-color: transparent;
    }

    .user-name {
        color: #e2e8f0;
        filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.5));
    }

    .user-name:hover {
        color: #ffffff;
    }

    .logout-section .el-button {
        background: transparent;
        border-color: rgba(255, 255, 255, 0.2);
        color: #e5e7eb;
    }

    .logout-section .el-button:hover {
        background: rgba(255, 255, 255, 0.1);
        color: #ffffff;
        border-color: rgba(255, 255, 255, 0.3);
    }
}

/* ========== 动画增强 ========== */
/* 加载动画 */
@keyframes shimmer {
    0% {
        background-position: -200% 0;
    }
    100% {
        background-position: 200% 0;
    }
}

/* 头像加载状态（可选） - 已去除背景 */
.user-avatar.loading img {
    animation: shimmer 1.5s infinite;
    background: transparent;
}

/* ========== 高对比度模式（可访问性） ========== */
@media (prefers-contrast: high) {
    .system-button-bar {
        border-width: 2px;
        border-color: #000;
    }

    .user-avatar {
        border-width: 4px;
        outline-width: 3px;
    }

    .logout-section .el-button {
        border-width: 2px;
        font-weight: 700;
    }
}

/* ========== 减少动画模式（可访问性） ========== */
@media (prefers-reduced-motion: reduce) {
    .system-button-bar,
    .user-avatar,
    .user-name,
    .logout-section .el-button,
    .user-avatar img {
        transition: none;
        animation: none;
    }

    .user-avatar:hover {
        transform: none;
    }

    .logout-section .el-button:hover {
        transform: none;
    }
}
</style>
