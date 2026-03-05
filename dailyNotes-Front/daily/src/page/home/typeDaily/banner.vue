<script setup lang="ts">
import dailyBanner from '@/page/home/typeDaily/daily/dailyBanner.vue'
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { SessionStorage } from '@/constants/storage';
import { useI18n } from 'vue-i18n'
import { getBackgroundUrl } from '@/services/request';
import { watch } from 'vue';
import { useCounterStore } from '@/storage/DocsView';
import Plan_I from './Plan_I/Plan_I.vue';


const store = useCounterStore();

// 定义索引值
type ComponentMapKey = 'dailyBase' | 'Plan_I';

// 类型保护函数：检查值是否为有效的 ComponentMapKey
function isValidComponentKey(value: string): value is ComponentMapKey {
  const validKeys: ComponentMapKey[] = ['dailyBase', 'Plan_I'];
  return validKeys.includes(value as ComponentMapKey);
}

// 从字符串中提取有效的 ComponentMapKey
function extractComponentKey(value: string): ComponentMapKey {
  const validKeys: ComponentMapKey[] = ['dailyBase', 'Plan_I'];

  for (const key of validKeys) {
    if (value.includes(key)) {
      return key;
    }
  }

  throw new Error(`Invalid component key: "${value}". Expected one of: ${validKeys.join(', ')}`);
}

const current = ref<ComponentMapKey>("dailyBase"); // 控制显示哪个

const compMap = {
  dailyBase: dailyBanner,
  Plan_I: Plan_I
} as const;

const handleEditorToggle = (nextState: ComponentMapKey) => {
  current.value = nextState
}

// 监听 store 中的 currentView 变化，根据类型名称切换视图
watch(() => store.currentView, (newTypeName) => {
  console.log('Current Type Name:', newTypeName);
  if (newTypeName) {
    try {
      const newView = extractComponentKey(newTypeName);
      handleEditorToggle(newView);
    } catch (error) {
      console.error(error);
      // 使用默认值，或根据需要处理错误
    }
  }
}, { immediate: true });


// vue组件生命周期：组件挂载完成后执
onMounted(() => {
    
    // 刷新背景图片
    getBackgroundUrl();
});

// vue组件生命周期：在组件实例被卸载之前调用
onBeforeUnmount(() => {
  
});
</script>
<template>
  <div class=" banner-image  banner-size">
          <component :is="compMap[current]" />  
  </div>
</template>

<style>
.banner-image{
  background-image: var(--cdn-url);  

  background-repeat: no-repeat;
  background-size: cover;
  background-position: 0 0;
}

.banner-size{
  width: 100%;
  height: 100%;
}


</style>
