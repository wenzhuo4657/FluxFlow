<script setup lang="ts">
import DataDownLoad from './button/DataDownLoad.vue';
import DataImport from './button/DataImport.vue';
import Language from './button/Language.vue'
import { useI18n } from 'vue-i18n'
import { Download, Upload, Operation } from '@element-plus/icons-vue'

const { t } = useI18n()

// 配置项定义，便于后续扩展
const configItems = [
  {
    id: 'dataExport',
    component: 'DataDownLoad',
    icon: Download,
  },
  {
    id: 'dataImport',
    component: 'DataImport',
    icon: Upload,
  },
  {
    id: 'language',
    component: 'Language',
    icon: Operation,
  }
]

// 响应式获取配置项标题和描述
const getConfigItemTitle = (id: string) => {
  switch (id) {
    case 'dataExport':
      return t('dataExport')
    case 'dataImport':
      return t('dataImport')
    case 'language':
      return t('language')
    default:
      return ''
  }
}

const getConfigItemDescription = (id: string) => {
  switch (id) {
    case 'dataExport':
      return t('dataExportDesc') || t('export')
    case 'dataImport':
      return t('dataImportDesc') || t('import')
    case 'language':
      return t('switchLanguage') || t('switch')
    default:
      return ''
  }
}
</script>

<template>
  <div class="config-container">
    <div class="config-header">
      <h2 class="config-title">{{ t('configuration') || t('settings') }}</h2>
      <p class="config-subtitle">{{ t('configSubtitle') || t('manageSettings') }}</p>
    </div>
    
    <el-divider class="config-divider" />
    
    <div class="config-content">
      <div class="config-section">
        <div class="config-item" v-for="item in configItems" :key="item.id">
          <el-card class="config-card" :header="getConfigItemTitle(item.id)" shadow="hover">
            <div class="config-item-content">
              <div class="config-item-icon">
                <el-icon :size="24">
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <div class="config-item-info">
                <p class="config-item-desc">{{ getConfigItemDescription(item.id) }}</p>
              </div>
              <div class="config-item-action">
                <DataDownLoad v-if="item.component === 'DataDownLoad'" />
                <DataImport v-else-if="item.component === 'DataImport'" />
                <Language v-else-if="item.component === 'Language'" />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.config-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.config-header {
  text-align: center;
  margin-bottom: 30px;
}

.config-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px 0;
}

.config-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.config-divider {
  margin: 20px 0;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.config-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.config-item {
  width: 100%;
}

.config-card {
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.config-card :deep(.el-card__header) {
  font-weight: 600;
  color: #303133;
  padding: 12px 20px;
  background-color: #fafafa;
  border-radius: 8px 8px 0 0;
}

.config-item-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 0;
}

.config-item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: #f0f9ff;
  border-radius: 6px;
  color: #409eff;
}

.config-item-info {
  flex: 1;
}

.config-item-desc {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.config-item-action {
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .config-container {
    padding: 15px 10px;
  }
  
  .config-item-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .config-item-action {
    width: 100%;
    justify-content: center;
  }
}
</style>
