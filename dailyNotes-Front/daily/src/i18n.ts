import { createI18n } from 'vue-i18n'
import { LocalStorage } from '@/constants/storage'

const messages = {
  en: {
    switch: 'Switch to Chinese',
    export:'export',
    import:'import',
    logout: "sign out",
    addItem: "add item",
    editItem: "edit item",
    deleteItem: "delete item",
    saveItem: "save item",
    index:"index",
    configuration: "Configuration",
    dataExport: "Data Export",
    dataImport: "Data Import",
    language: "Language",
    switchLanguage: "Switch Language",
    dataExportDesc: "Export your data to a file",
    dataImportDesc: "Import data from a file",
    switchLanguageDesc: "Switch between Chinese and English",
    configSubtitle: "Manage your application settings",
    manageSettings: "Manage Settings",
    settings: "Settings",
  },
  zh: {
    switch: '切换到英文',
    export:'导出',
    import:'导入',
    logout: "登出",
    addItem: "新增",
    editItem: "编辑",
    deleteItem: "删除",
    saveItem: "保存",
    index: "首页",
    configuration: "配置",
    dataExport: "数据导出",
    dataImport: "数据导入",
    language: "语言",
    switchLanguage: "切换语言",
    dataExportDesc: "将您的数据导出到文件",
    dataImportDesc: "从文件导入数据",
    switchLanguageDesc: "在中文和英文之间切换",
    configSubtitle: "管理您的应用设置",
    manageSettings: "管理设置",
    settings: "设置",
  },
}

// 从本地存储或浏览器语言恢复
const saved = localStorage.getItem(LocalStorage.LOCALE)
const guess = navigator.language.toLowerCase().startsWith('zh') ? 'zh' : 'en'
const locale = saved || guess

export const i18n = createI18n({
  legacy: false,           // 用 Composition API
  locale,
  fallbackLocale: 'en',
  messages,
})
