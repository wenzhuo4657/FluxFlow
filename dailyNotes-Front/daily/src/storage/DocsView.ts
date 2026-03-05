import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { SessionStorage } from '@/constants/storage'

export type HomeModel = number;

export const HomeModels = Object.freeze({
  
  DEFAULT: 1 as HomeModel,
  DOCS_LIST: 2 as HomeModel,
  DOC_CONTENT: 3 as HomeModel,
  CONFIGURATION: 5 as HomeModel,

});

// 从 sessionStorage 恢复状态
const savedState = sessionStorage.getItem(SessionStorage.DOCS_VIEW_STATE)
const initialState = savedState ? JSON.parse(savedState) : {
  homeModel: HomeModels.DEFAULT,
  currentTypeId: '',
  currentDocsId: '',
  currentItemIndex: '',
  refreshTrigger: 0,
  currentView: ''
}

/**
 * 该pinia用于管理home页面的状态
 */
export const useCounterStore = defineStore('DocsView', () => {
  // State
  const homeModel = ref<number>(initialState.homeModel)
  const currentTypeId = ref<string>(initialState.currentTypeId)
  const currentDocsId = ref<string>(initialState.currentDocsId)
  const currentItemIndex = ref<string>(initialState.currentItemIndex)
  const refreshTrigger = ref<number>(initialState.refreshTrigger)
  const currentView = ref<string>(initialState.currentView)

  // Getters
  const getCurrentModel = computed(() => homeModel.value)
  const getCurrentTypeId = computed(() => currentTypeId.value)
  const getCurrentDocsId = computed(() => currentDocsId.value)
  const getCurrentItemIndex = computed(() => currentItemIndex.value)
  const getRefreshTrigger = computed(() => refreshTrigger.value)
  const getCurrentView = computed(() => currentView.value)

  // Actions
  const setHomeModel = (model: number) => {
    homeModel.value = model
    persistState()
  }
  
  const setCurrentTypeId = (typeId: string) => {
    currentTypeId.value = typeId
    persistState()
  }
  
  const setCurrentDocsId = (docsId: string) => {
    currentDocsId.value = docsId
    persistState()
  }
  
  const setCurrentItemIndex = (itemIndex: string) => {
    currentItemIndex.value = itemIndex
    persistState()
  }
  
  const triggerRefresh = () => {
    refreshTrigger.value++
    persistState()
  }
  
  const setCurrentView = (view: string) => {
    currentView.value = view
    persistState()
  }
  
  // 持久化状态到 sessionStorage
  const persistState = () => {
    const state = {
      homeModel: homeModel.value,
      currentTypeId: currentTypeId.value,
      currentDocsId: currentDocsId.value,
      currentItemIndex: currentItemIndex.value,
      currentView: currentView.value
    }
    sessionStorage.setItem(SessionStorage.DOCS_VIEW_STATE, JSON.stringify(state))
  }
  
  // 清除状态到初始值
  const clearState = () => {
    homeModel.value = HomeModels.DEFAULT;
    currentTypeId.value = '';
    currentDocsId.value = '';
    currentItemIndex.value = '';
    currentView.value = '';
    
    // 同时清除 sessionStorage 中的持久化数据
    sessionStorage.removeItem(SessionStorage.DOCS_VIEW_STATE);
  }

  return {
    // State
    homeModel,
    currentTypeId,
    currentDocsId,
    currentItemIndex,
    refreshTrigger,
    currentView,
    
    // Getters
    getCurrentModel,
    getCurrentTypeId,
    getCurrentDocsId,
    getCurrentItemIndex,
    getRefreshTrigger,
    getCurrentView,
    
    // Actions
    setHomeModel,
    setCurrentTypeId,
    setCurrentDocsId,
    setCurrentItemIndex,
    triggerRefresh,
    setCurrentView,
    clearState,
  }
})



