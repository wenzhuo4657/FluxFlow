<script setup>
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()


const onChange = () => {
  locale.value = locale.value === 'en' ? 'zh' : 'en'
  localStorage.setItem('locale', locale.value) // 持久化
}
</script>

<template>
  <el-button type="success" plain @click="onChange">
    <strong>{{ $t('switch') }}</strong>
  </el-button>
</template>
<style>
</style>