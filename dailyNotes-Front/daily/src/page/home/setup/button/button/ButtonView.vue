<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAllTypes, TypeData } from '@/services/request'
import { ArrowDown } from '@element-plus/icons-vue'
import { SessionStorage } from '@/constants/storage'
import { useI18n } from 'vue-i18n'
import { useCounterStore } from '@/storage/DocsView'
import { HomeModels } from '@/storage/DocsView'
const { t, locale } = useI18n()

const store = useCounterStore()

// 组件映射键类型

const types = ref<TypeData[]>([])
 


 function  changedTypeId(target:TypeData){
 
     store.$patch((state) => {
      state.currentTypeId = target.id
      state.currentView = target.name
})
}

async function fetchTypes() {

    const list: TypeData[] = await getAllTypes();

    if (Array.isArray(list)) {
      types.value = list;
    }
}



function onCommand(cmd: number | string) {
  let target: TypeData | undefined
  const idNum = Number(cmd)
  if (!Number.isNaN(idNum)) {
    target = types.value.find(t => t.id === String(idNum))
  }
  if (!target && typeof cmd === 'string') {
    target = types.value.find(t => t.name === cmd)
  }
  if (target) {
    changedTypeId(target)
  }
}

onMounted(()=>{
 
  fetchTypes() 
})
</script>

<template>

  <el-scrollbar class="button-view">


            <el-button
            v-for="it in types"
            :key="it.id"
            class="item"
            @click="onCommand(it.id)"
          >
            {{ it.name }}
          </el-button>
  </el-scrollbar>




</template>

<style scoped>
.button-view { 
    height: 100%;
    width: 100%;
}

.item{
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 5%;
  text-align: center;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

</style>
