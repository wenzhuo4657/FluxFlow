<script setup lang="ts">
import Banner from '@/page/home/typeDaily/banner.vue'
import { useI18n } from 'vue-i18n'
import StickyNote from './StickyNote/StickyNote.vue'
import DocsList from './DocsListView/DocsList.vue'
import Configuration from './Configuration/Configuration.vue'
import { useCounterStore } from '@/storage/DocsView'
import { HomeModels } from '@/storage/DocsView'
import PreView from './PreView/PreView.vue'



const { t } = useI18n()

const store = useCounterStore()



</script>

<template>
      <StickyNote></StickyNote>
    <div class="any">

    
      <!-- 默认 -->
      <div  v-if="store.homeModel === HomeModels.DEFAULT">
         <PreView></PreView>
      </div>

      <!-- 渲染文档列表 -->
      <div  v-if="store.homeModel === HomeModels.DOCS_LIST">
        <DocsList></DocsList>
      </div>

      <!-- 渲染指定文档内容 -->
       <div  v-if="store.homeModel === HomeModels.DOC_CONTENT">
            <Banner></Banner>
       </div>
       <!-- 渲染配置 -->
        <div v-if="store.homeModel === HomeModels.CONFIGURATION">
          <Configuration></Configuration>
        </div>

  </div>

</template>

<style scoped>


.any{
  height: 100%;
  width: 100%;
}

.any > div{
  height: 100%;
  width: 100%;
}

</style>

