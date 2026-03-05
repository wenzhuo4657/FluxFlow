<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addDocs, type AddDocsRequest } from '@/services/request'
import { Plus } from '@element-plus/icons-vue'

/**
 * 组件属性
 */
interface Props {
  /** 类型ID */
  typeId: string
  /** 文档名称 */
  docsName: string
}

const props = defineProps<Props>()

/**
 * 组件事件
 */
const emit = defineEmits<{
  /** 添加成功事件 */
  (event: 'success'): void
  /** 添加失败事件 */
  (event: 'error', error: Error): void
}>()

/** 加载状态 */
const loading = ref(false)

/**
 * 添加文档
 */
async function handleAddDocs() {
  // 参数校验
  if (!props.typeId) {
    ElMessage.warning('类型ID不能为空')
    return
  }
  if (!props.docsName || props.docsName.trim() === '') {
    ElMessage.warning('文档名称不能为空')
    return
  }

  loading.value = true

  try {
    const request: AddDocsRequest = {
      typeId: props.typeId,
      docsName: props.docsName.trim()
    }

    const success = await addDocs(request)

    if (success) {
      ElMessage.success('添加文档成功')
      emit('success')
    } else {
      ElMessage.error('添加文档失败')
      emit('error', new Error('添加文档失败'))
    }
  } catch (error) {
    console.error('添加文档错误:', error)
    ElMessage.error('添加文档时发生错误')
    emit('error', error as Error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-button
    type="primary"
    :icon="Plus"
    :loading="loading"
    @click="handleAddDocs"
  >
    添加文档
  </el-button>
</template>

<style scoped>
</style>
