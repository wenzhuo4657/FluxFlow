<script setup>
import { useAuthStore } from '@/storage/auth'
import { useRouter } from 'vue-router'
import { logout as apiLogout } from '@/services/login'
import { useI18n } from 'vue-i18n'
const { t, locale } = useI18n()

const router = useRouter()
const auth = useAuthStore()

async function onLogout() {
  try {
    await apiLogout() // 调用后端登出，尽力使服务端失效会话
  } catch (_) {
    // 忽略后端错误，继续清理本地状态
  } finally {
    auth.logout()
    router.replace({ name: 'Login' })
  }
}
</script>

<template>
  <el-button type="success" plain @click="onLogout">
    <strong>{{ $t('logout') }}</strong>
  </el-button>
  
</template>

<style>
</style>
