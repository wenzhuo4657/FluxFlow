<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
import { DownLoadFile} from '@/services/request';
</script>

<template>

        <el-button type="success" plain @click="DownLoadFile()">
                {{t('export')}}
              </el-button>
</template>

<style>

        
</style>