/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_API_BASE: string
  // 在这里添加其他环境变量类型
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}