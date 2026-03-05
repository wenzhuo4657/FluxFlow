/**
 * Storage Key Constants
 * 统一管理所有 localStorage 和 sessionStorage 的键名
 */

/**
 * Session Storage Keys (会话存储)
 * 浏览器会话期间保持的数据，页面关闭后清除
 */
export const SessionStorage = Object.freeze({
  /** 主页视图状态 */
  DOCS_VIEW_STATE: 'docsViewState',
  ACCESS_TOKEN: 'AccessToken'
} as const);

/**
 * Local Storage Keys (本地存储)
 * 持久化存储在浏览器本地，页面关闭后依然保留
 */
export const LocalStorage = Object.freeze({
  /** 用户认证令牌 */
  TOKEN: 'token',

  /** 用户信息 (JSON字符串) */
  USER_INFO: 'userInfo',

  /** 国际化语言设置 (如: zh-CN, en-US) */
  LOCALE: 'locale',
} as const);

/**
 * Type alias for better IDE support
 */
export type SessionStorageKey = typeof SessionStorage[keyof typeof SessionStorage];
export type LocalStorageKey = typeof LocalStorage[keyof typeof LocalStorage];
