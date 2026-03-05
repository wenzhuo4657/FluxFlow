<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteDocs, type DeleteDocsRequest } from '@/services/request'
import { Delete } from '@element-plus/icons-vue'

/**
 * 组件属性
 */
interface Props {
  /** 文档ID */
  docsId: string
}

const props = defineProps<Props>()

/**
 * 组件事件
 */
const emit = defineEmits<{
  /** 删除成功事件 */
  (event: 'success'): void
  /** 删除失败事件 */
  (event: 'error', error: Error): void
}>()

/** 加载状态 */
const loading = ref(false)

/**
 * 删除文档（带确认）
 */
async function handleDeleteDocs() {
  // 参数校验
  if (!props.docsId) {
    ElMessage.warning('文档ID不能为空')
    return
  }

  // 二次确认
  try {
    await ElMessageBox.confirm(
      '确定要删除该文档吗？删除后无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
  } catch {
    // 用户取消
    return
  }

  loading.value = true

  try {
    const request: DeleteDocsRequest = {
      docsId: props.docsId
    }

    const success = await deleteDocs(request)

    if (success) {
      ElMessage.success('删除文档成功')
      emit('success')
    } else {
      ElMessage.error('删除文档失败')
      emit('error', new Error('删除文档失败'))
    }
  } catch (error) {
    console.error('删除文档错误:', error)
    ElMessage.error('删除文档时发生错误')
    emit('error', error as Error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-button
    type="danger"
    :icon="Delete"
    :loading="loading"
    @click="handleDeleteDocs"
  >
    删除文档
  </el-button>
</template>

<style scoped>
</style>
