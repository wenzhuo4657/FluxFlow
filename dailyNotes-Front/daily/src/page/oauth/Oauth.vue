

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/storage/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  const {  token,accessToken, userInfo } = route.query

  if ( token && userInfo) {
    try {
  
  
      // 保存 token 和用户信息
      authStore.setToken(token)
      authStore.setAccessToken(accessToken)
      authStore.setUser(JSON.parse(decodeURIComponent(userInfo)))

      // 登录成功，跳转到主页
      router.replace({ name: 'Home' })
    } catch (error) {
      console.error('处理登录回调时出错:', error)
      // 出错时跳转到登录页
      router.push({ name: 'Login', query: { error: 'callback_failed' } })
    }
  } else {
    // URL 中没有 token 或 userInfo，可能是登录失败或直接访问
    console.error('登录回调缺少必要参数')
    router.push({ name: 'Login', query: { error: 'missing_params' } })
  }
})
</script>

<template>
  <div class="callback-container">
    <p>正在登录中，请稍候...</p>
  </div>
</template>

<style scoped>
.callback-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 18px;
}
</style>
