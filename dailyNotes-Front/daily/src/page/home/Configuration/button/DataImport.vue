<script setup lang="ts">
import { ref } from 'vue';
import {  upload } from '@/services/request';
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()


function onClickImport() {
  fileInput.value?.click();
}
const fileInput = ref<HTMLInputElement | null>(null);
const uploading = ref(false);
const progress = ref(0);

async function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;



  const fd = new FormData();

  fd.append('file', file, file.name);

  try {
    uploading.value = true;
    progress.value = 0;

    await upload(fd)

  } finally {
    uploading.value = false;
    input.value = ''; // 允许再次选择同名文件触发 change
  }
}

</script>

<template>
  <el-button type="success" plain @click="onClickImport" :disabled="uploading">
    <strong>{{t('import')}}</strong>
  </el-button>
         <input
    ref="fileInput"
    type="file"
    style="display: none;"
    accept=".db"  
    @change="onFileChange"
  />
</template>
