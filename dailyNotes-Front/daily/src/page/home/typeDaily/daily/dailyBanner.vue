<script setup lang="ts">

import { addItemByType, addItemByTypeWithFields, GetItemsRequest, getMdByType, InsertItemRequest, InsertItemWithFieldsRequest, ItemData } from '@/services/request';
import { ref, watch, onMounted, onUnmounted, onBeforeMount, computed } from 'vue';
import ItemViewAndEdit from './ItemViewAndEdit.vue';
import { useI18n } from 'vue-i18n'
import { useCounterStore } from '@/storage/DocsView'
const { t, locale } = useI18n()

const store = useCounterStore()



// 文档id
const docsId = ref<string>(store.getCurrentDocsId || '')
// 文档内容
const res = ref<ItemData[]>([])
// 当前渲染的文档item
const current=ref<ItemData>()

onMounted(() => {
  // 设置初始docsId值
  docsId.value = store.getCurrentDocsId || '';
  loadDocsId(docsId.value)


  
})

// 监听 store 中的 docsId 变化
watch(() => store.getCurrentDocsId, (newDocsId) => {
  if (newDocsId) {
    dynamicDocsId(newDocsId)
  }
}, { immediate: true })

// 监听刷新触发器
watch(() => store.getRefreshTrigger, () => {
  if (docsId.value) {
    loadDocsId(docsId.value);
  }
}, { immediate: false })

onUnmounted(() => {
  
})

/**
 *  使用Pinia状态管理,动态更新docsId，
 */
function dynamicDocsId(newDocsId: string) {
  docsId.value = newDocsId
}


/**
 * 监听docsId变化，调用渲染函数
 */
watch(docsId, async (newDocsId, oldDocsId) => {
  loadDocsId(newDocsId)


})


// 根据文档id加载渲染内容res，并根据缓存来决定选定item
async function loadDocsId(id:string){
  if(id===''){
    return 
  }

  const typeId = store.getCurrentTypeId || '';
  const data: GetItemsRequest = { docsId: id, type: typeId };

  const result = await getMdByType(data);
  
  res.value = result.sort((a, b) => a.title.localeCompare(b.title));

  const itemID:string=store.getCurrentItemIndex|| '';

  current.value=result.find( it => it.index==itemID)
  if(!current.value){
    current.value=res.value[res.value.length-1]
  }
}



/**
 * 变更current的函数,同时缓存item选择
 */
function chooseItem(item:ItemData){
  current.value=item
  store.setCurrentItemIndex(item.index)
}

async function InsertItem(){
  
  const typeId = store.getCurrentTypeId || '';
   const fieldsObj: Record<string, string> = {};
   fieldsObj.data=reportDate.value
  const data:InsertItemWithFieldsRequest={ docsId:docsId.value,type:typeId,fields:fieldsObj as any}
  const res=await addItemByTypeWithFields(data);
  if(res==true){
     // 重新加载文档列表而不是刷新整个页面
     store.triggerRefresh();
  }else{
     alert("异常错误")
  }

}


/**
 * 计算逆序的res，用于排序
 */
const reversedRes = computed(() => {
  return [...res.value].reverse()
})


const reportDate = ref(new Intl.DateTimeFormat('sv-SE').format(new Date()));

const shortcuts = [
  {
    text: '今天',
    value: new Date(),
  },
  {
    text: '昨天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      return date
    },
  },
  {
    text: '明天',
    value: () => {
    const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24)
      return date
    },
  },
]



</script>

<template>
  <div  class="split">
    <!-- 左右分栏，左侧功能按钮，右侧渲染内容 -->
      <div class="split_title">
        <!-- 按扭区 -->
        <!-- 1，新增 -->
      <button 
          @click="InsertItem"
          class="button-reset">
        {{ $t('addItem') }}</button>
        <!-- 2,选择日期添加 -->
    <el-date-picker
  v-model="reportDate"
  type="date"
  format="YYYY/MM/DD"
  value-format="YYYY-MM-DD"
  :shortcuts="shortcuts"
  />

    
        <!-- title区：选择item，交给编辑区域，默认为最新 -->
      <button
        v-for="it in reversedRes"
        :key="it.index"
        @click="chooseItem(it)"
        :class="{ 'selector': current && current.index === it.index }"
      >
      
        <strong >{{ it.title }}</strong>
      </button>
    


      </div>
      <div   class="split_content">
        <!-- 引入外部组件进行编辑和渲染，将item交给他 -->
        <div v-if="current"  class="editor-wrap">
          <ItemViewAndEdit  v-model:item="current"></ItemViewAndEdit>
       </div>
      <div v-else>
        <em>加载中...</em>
      </div>
      </div>

  </div>


</template>

<style scoped>
.split {
  height:  100%;
  width: 100%;
  display: flex;
  flex-direction: row;
}

.split_title{
  width: 20%;
  height: 100%;
  display: flex;        
  flex-direction: column; 
  gap: 8px;
  padding-right: 5%;

  overflow-y: auto;           /* 只在这里显示垂直滚动条 */
  overflow-x: hidden;          /* 隐藏水平滚动条 */

}
.split_title > div{
  display: flex;
  flex-direction: row;
}

.split_content {
  display: flex;
  width: 75%;
  height: 100%;
  overflow-y: auto;
}


.editor-wrap {
  flex: 1;   
      
}

.selector{
  background-color: bisque;

}








</style>
