<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { getTypesWithItems, ContentData } from '@/services/request'
import { useCounterStore } from '@/storage/DocsView'
import { HomeModels } from '@/storage/DocsView'

import { ArrowDown } from '@element-plus/icons-vue'
import { SessionStorage } from '@/constants/storage'
import AddDocsButtonVue  from  '@/page/home/DocsListView/button/AddDocsButton.vue'
import DeleteDocsButtonVue from '@/page/home/DocsListView/button/DeleteDocsButton.vue'

// 功能内容
const items = ref<ContentData[]>([])
const currentTypeId = ref<string>('')
const newDocsName = ref<string>('')

const store = useCounterStore()

//处理文档id变更之后的行为
function changedDocsID(target:ContentData){
  store.setCurrentDocsId(target.id)
}


  // 调用api获取当前用户在当前类型下的文档集合,并变更响应变量
async function loadByTypeId(typeId: string) {
  // 验证 typeId 是否有效
  if (!typeId || typeId === 'null' || typeId === 'undefined') {
    console.log('Invalid typeId, skipping API call:', typeId)
    return
  }

  console.log('Loading content for typeId:', typeId)

  try {
      const list:ContentData[] = await getTypesWithItems({ id: typeId })
      items.value = list
 
  } catch (e) {
    console.error('Failed to load content names:', e)
    items.value = []
  }
}




// 监听 currentTypeId 变化，自动加载内容
watch(currentTypeId, (newTypeId, oldTypeId) => {
  if (newTypeId && newTypeId !== oldTypeId) {
    console.log('currentTypeId changed from', oldTypeId, 'to', newTypeId)
    loadByTypeId(newTypeId)
  }
})

// 监听 store 中的 currentTypeId 变化
watch(() => store.getCurrentTypeId, (newTypeId) => {
  if (newTypeId) {
    currentTypeId.value = newTypeId
  }
}, { immediate: true })

// 接收type变更的函数
function onTypeChanged(payload:  string) {
  const typeId = typeof payload === 'string' ? payload : 'null'

  // 验证 typeId 是否有效
  if (!typeId || typeId === 'null' || typeId === 'undefined') {
    console.log('Invalid typeId in onTypeChanged:', typeId)
    return
  }

  console.log('Type changed, new typeId:', typeId)

  // 只需要修改 currentTypeId，watch 会自动调用 loadByTypeId
  currentTypeId.value = typeId
}

onMounted(() => {
  // 初始化时，从store获取当前的typeId
  if (store.getCurrentTypeId) {
    currentTypeId.value = store.getCurrentTypeId
  }
})

onBeforeUnmount(() => {
  
})


// 下拉列表的点击函数
function onCommand(cmd: number | string) {
  let target: ContentData | undefined
  const idStr = String(cmd)
  target = items.value.find(it => it.id === idStr)
  if (!target && typeof cmd === 'string') {
    target = items.value.find(it => it.name === cmd)
  }
  if (target) {
    changedDocsID(target)
    // 用户手动点击时，才切换到 Banner 视图
    store.setHomeModel(HomeModels.DOC_CONTENT)
  }
}

/** 操作成功后刷新列表并清空输入 */
function handleSuccess() {
  if (currentTypeId.value) {
    loadByTypeId(currentTypeId.value)
  }
  newDocsName.value = ''
}
</script>

<template>

      <el-scrollbar class="button-view">
            <div class="container">
            <div v-for="it in items" :key="it.id" class="item-wrapper">
 
                <el-button
                class="item"
                @click="onCommand(it.id)"
              >
                {{ it.name }}
              </el-button>
              <delete-docs-button-vue
                :docs-id="it.id"
                class="item_button"
                @success="handleSuccess"
              />
            
            </div>
          </div>
          <div class="add-section">
            <el-input
              v-model="newDocsName"
              placeholder="新文档名称"
              class="docs-name-input"
              clearable
            />
            <add-docs-button-vue
              :type-id="currentTypeId"
              :docs-name="newDocsName"
              @success="handleSuccess"
            />
          </div>
  </el-scrollbar>

</template>

<style scoped>

.button-view {
  height: 100%;
  width: 100%;
}

.container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 10px;
}

.item-wrapper {
  display: flex;
  flex-direction: column;
  gap: 5px;
  align-items: center;
}

.item {
  width: 80px;
  height: 120px;
}


.add-section {
  display: flex;
  gap: 10px;
  padding: 10px;
  align-items: center;
}

.docs-name-input {
  width: 150px;
}
</style>
