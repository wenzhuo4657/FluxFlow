import { defineStore } from 'pinia'
import { ref, computed, Ref } from 'vue'
import { LocalStorage, SessionStorage } from '@/constants/storage'
import { logout as apiLogout } from '@/services/login'
import router from '@/router'
import { useCounterStore } from '@/storage/DocsView'
import { heartbeat } from '@/services/request'

// 用户信息类型定义
export interface UserInfo {
  id?: string
  username?: string
  email?: string
  avatar?: string
  name?: string
  [key: string]: any
}

// 心跳间隔配置（毫秒）
const HEARTBEAT_INTERVAL = 30 * 1000 // 30秒

export const useAuthStore = defineStore('auth', () => {
  // State
  const token: Ref<string> = ref(localStorage.getItem(LocalStorage.TOKEN) || '')
  const accessToken:Ref<String>=ref(sessionStorage.getItem(SessionStorage.ACCESS_TOKEN)||'')
  const user: Ref<UserInfo | null> = ref(JSON.parse(localStorage.getItem(LocalStorage.USER_INFO) || 'null'))

  // 心跳定时器
  let heartbeatTimer: ReturnType<typeof setInterval> | null = null

  // Getters
  const isAuthenticated = computed(() => !!token.value)

  // ==================== 心跳管理 ====================

  /**
   * 启动心跳定时器
   */
  const startHeartbeat = () => {
    // 如果已有定时器，先清除
    stopHeartbeat()

    // 立即执行一次心跳
    sendHeartbeat()

    // 设置定时心跳
    heartbeatTimer = setInterval(() => {
      sendHeartbeat()
    }, HEARTBEAT_INTERVAL)
  }

  /**
   * 停止心跳定时器
   */
  const stopHeartbeat = () => {
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer)
      heartbeatTimer = null
    }
  }

  /**
   * 发送心跳请求（静默处理，不影响用户体验）
   */
  const sendHeartbeat = async () => {
    if (!accessToken.value) {
      stopHeartbeat()
      return
    }

    try {
      await heartbeat()
    } catch (error) {
      // 心跳失败时静默处理，避免打扰用户
      console.debug('Heartbeat failed:', error)
      // 如果是 401 错误，HTTP 拦截器会自动处理登出
    }
  }

  // Actions
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem(LocalStorage.TOKEN, newToken)
  }
  const setAccessToken=(newAccessToken: string)=> {
    accessToken.value=newAccessToken
    sessionStorage.setItem(SessionStorage.ACCESS_TOKEN,newAccessToken)
        // 设置 accessToken 后启动心跳
    if (newAccessToken) {
      startHeartbeat()
    } else {
      stopHeartbeat()
    }
    
  }

  const setUser = (userInfo: UserInfo | null) => {
    user.value = userInfo
    localStorage.setItem(LocalStorage.USER_INFO, JSON.stringify(userInfo))
  }
  

  const logout = async () => {
    // 停止心跳
    stopHeartbeat()

    try {
      // 调用后端登出接口
      await apiLogout()
    } catch (error) {
      console.error('Logout API call failed:', error)
      // 即使后端调用失败，也要清理本地状态
    }

    // 清理本地状态
    token.value = ''
    user.value = null

    localStorage.removeItem(LocalStorage.TOKEN)
    localStorage.removeItem(LocalStorage.USER_INFO)

    // 清除 DocsView 状态
    const docsViewStore = useCounterStore()
    docsViewStore.clearState()

    router.push('/')
  }


  // 初始化：如果已有 token，启动心跳
  if (token.value) {
    startHeartbeat()
  }

  // 返回对外暴露的属性和方法
  return {
    token,
    accessToken,
    user,
    isAuthenticated,
    setToken,
    setUser,
    setAccessToken,
    logout,
    // 暴露心跳控制方法（可选，用于调试或特殊场景）
    startHeartbeat,
    stopHeartbeat
  }
})