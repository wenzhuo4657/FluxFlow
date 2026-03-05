<script setup>
import { useAuthStore } from '@/storage/auth'
import { useRouter } from 'vue-router'
const authStore = useAuthStore()
const router = useRouter()
const logout = () => {
  authStore.logout()
 router.push({ name: 'Login', query: { info: 'logout' } })
}
</script>

<template>


  <!-- 
  <p><strong>Current route path:</strong> {{ $route.fullPath }}</p>
  <nav>
    <RouterLink to="/home">Go to Home</RouterLink>
    <br></br>
    <RouterLink to="/login">Go to login</RouterLink>
  </nav>
  <button @click="logout">logout</button>
  -->
  <main>
    <RouterView />
  </main>
</template>

<style scoped>
</style>

