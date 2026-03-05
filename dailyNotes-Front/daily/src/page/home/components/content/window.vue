<template>
  <div
    class="note-window"
    :class="{ 'is-collapsed': isCollapsed, 'is-dragging': isDragging }"
    :style="windowStyle"
  >
    <!-- 展开状态的窗口 -->
    <div v-if="!isCollapsed" class="window-content">
      <!-- 标题栏 - 点击可折叠 -->
      <div
        class="window-header"
        @mousedown="startDrag"
        @click="handleHeaderClick"
      >
        <div class="window-title">
          <el-icon class="title-icon"><Document /></el-icon>
          <span>{{ title }}</span>
        </div>
        <!-- 编辑/保存按钮 -->
        <div class="window-actions">
          <el-button
            v-if="!isEditing"
            type="text"
            size="small"
            @click.stop="startEditing"
            class="action-button"
          >
            <el-icon><Edit /></el-icon>
          </el-button>
          <template v-else>
            <el-button
              type="text"
              size="small"
              @click.stop="cancelEditing"
              class="action-button cancel-button"
            >
              <el-icon><Close /></el-icon>
            </el-button>
            <el-button
              type="text"
              size="small"
              @click.stop="saveEditing"
              class="action-button save-button"
            >
              <el-icon><Check /></el-icon>
            </el-button>
          </template>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="window-body">
        <div class="content-wrapper">
          <!-- 查看模式 -->
          <div v-if="!isEditing">
            <!-- 使用原生渲染，避免 markdown-view 组件可能的问题 -->
            <div
              v-if="content && content.trim()"
              class="markdown-content"
              v-html="renderedContent"
            ></div>
            <div v-else class="empty-content">
              <el-icon class="empty-icon"><EditPen /></el-icon>
              <p>点击标题栏可折叠便签，点击编辑按钮开始编辑内容</p>
            </div>
          </div>

          <!-- 编辑模式 -->
          <div v-else class="editing-container">
            <el-input
              v-model="editingContent"
              type="textarea"
              :rows="15"
              resize="none"
              placeholder="请输入 Markdown 内容..."
              class="content-editor"
              @keydown.ctrl.enter="saveEditing"
            />
            <div class="editing-hint">
              <small>提示：支持 Markdown 语法，按 Ctrl+Enter 快速保存</small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 折叠状态的按钮 -->
    <div
      v-else
      class="collapsed-button"
      @mousedown="startDrag"
      @click="handleCollapsedClick"
    >
      <el-icon class="collapsed-icon"><Document /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import { Document, EditPen, Edit, Check, Close } from '@element-plus/icons-vue'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'

// Props 定义
interface Props {
  content?: string
  initialContent?: string
  title?: string
  icon?: string
  initialPosition?: { x: number; y: number }
  width?: number
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  content: '',
  initialContent: '',
  title: '便签',
  icon: 'Document',
  initialPosition: () => ({ x: window.innerWidth - 320, y: 100 }),
  width: 300,
  height: 400
})

// 响应式状态
const isCollapsed = ref(true)
const isDragging = ref(false)
const isEditing = ref(false)
// 优先使用 content prop（v-model），其次使用 initialContent
const internalContent = ref(props.content || props.initialContent || '')
const editingContent = ref('')
const position = ref({ ...props.initialPosition })
const dragOffset = ref({ x: 0, y: 0 })

// 拖拽相关状态
let dragStartTime = 0
let dragStartPos = { x: 0, y: 0 }
let hasDragged = false

// Markdown 渲染器
const md = new MarkdownIt({ breaks: true })

// 暴露给 v-model 的内容属性
const content = computed({
  get: () => internalContent.value,
  set: (value) => {
    internalContent.value = value
  }
})

// 渲染 Markdown 内容
const renderedContent = computed(() => {
  if (!internalContent.value) return ''
  try {
    return DOMPurify.sanitize(md.render(internalContent.value))
  } catch (error) {
    console.error('Markdown渲染错误:', error)
    return internalContent.value
  }
})

// 计算窗口样式
const windowStyle = computed(() => {
  if (isCollapsed.value) {
    return {
      left: `${position.value.x}px`,
      top: `${position.value.y}px`,
      width: '48px',
      height: '48px'
    }
  } else {
    return {
      left: `${position.value.x}px`,
      top: `${position.value.y}px`,
      width: `${props.width}px`,
      height: `${props.height}px`
    }
  }
})

// 处理标题栏点击
const handleHeaderClick = (event: MouseEvent) => {
  // 阻止事件冒泡
  event.stopPropagation()

  // 如果正在拖拽或刚刚拖拽过，不处理点击
  if (isDragging.value || hasDragged) {
    return
  }

  // 延迟处理点击，避免与拖拽冲突
  setTimeout(() => {
    if (!isDragging.value && !hasDragged) {
      isCollapsed.value = true
    }
  }, 50)
}

// 处理折叠按钮点击
const handleCollapsedClick = (event: MouseEvent) => {
  // 阻止事件冒泡
  event.stopPropagation()

  // 如果正在拖拽，不处理点击
  if (isDragging.value || hasDragged) {
    return
  }

  // 延迟处理点击
  setTimeout(() => {
    if (!isDragging.value && !hasDragged) {
      isCollapsed.value = false
    }
  }, 50)
}

// 开始拖拽
const startDrag = (event: MouseEvent) => {
  event.preventDefault()
  event.stopPropagation()

  dragStartTime = Date.now()
  dragStartPos = { x: event.clientX, y: event.clientY }
  hasDragged = false
  isDragging.value = true

  const rect = (event.currentTarget as HTMLElement).getBoundingClientRect()
  dragOffset.value = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top
  }

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

// 拖拽中
const onDrag = (event: MouseEvent) => {
  if (!isDragging.value) return

  // 标记已经发生了拖拽
  const dragDistance = Math.sqrt(
    Math.pow(event.clientX - dragStartPos.x, 2) +
    Math.pow(event.clientY - dragStartPos.y, 2)
  )

  if (dragDistance > 3) {
    hasDragged = true
  }

  let newX = event.clientX - dragOffset.value.x
  let newY = event.clientY - dragOffset.value.y

  // 限制窗口在视口内
  const maxX = window.innerWidth - (isCollapsed.value ? 48 : props.width)
  const maxY = window.innerHeight - (isCollapsed.value ? 48 : props.height)

  newX = Math.max(0, Math.min(newX, maxX))
  newY = Math.max(0, Math.min(newY, maxY))

  position.value = { x: newX, y: newY }
}

// 停止拖拽
const stopDrag = () => {
  isDragging.value = false

  // 延迟重置状态
  setTimeout(() => {
    hasDragged = false
  }, 200)

  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 编辑相关方法
const startEditing = () => {
  isEditing.value = true
  editingContent.value = internalContent.value
}

const cancelEditing = () => {
  isEditing.value = false
  editingContent.value = ''
}

const saveEditing = () => {
  internalContent.value = editingContent.value
  isEditing.value = false
  editingContent.value = ''
}

// 事件发射
const emit = defineEmits<{
  'update:content': [value: string]
  'position-change': [position: { x: number; y: number }]
}>()

// 监听 props.content 变化（v-model 绑定的内容）
watch(() => props.content, (newContent) => {
  if (newContent !== internalContent.value) {
    internalContent.value = newContent || ''
  }
})

// 监听 props.initialContent 变化，确保外部内容能正确传递
watch(() => props.initialContent, (newContent) => {
  if (newContent !== internalContent.value && !props.content) {
    internalContent.value = newContent || ''
  }
})

// 监听内部内容变化，通知外部
watch(internalContent, (newContent) => {
  emit('update:content', newContent || '')
})

// 监听位置变化
watch(position, (newPosition) => {
  emit('position-change', newPosition)
}, { deep: true })

// 清理事件监听器
onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
})
</script>

<style scoped>
.note-window {
  position: fixed;
  z-index: 1000;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}

.note-window.is-collapsed {
  cursor: move;
}

.note-window.is-dragging {
  cursor: move;
  transition: none;
}

/* 展开状态样式 */
.window-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 8px;
}

.window-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  cursor: pointer;
  border-radius: 8px 8px 0 0;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.window-header:hover {
  opacity: 0.9;
}

.window-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  font-size: 14px;
  flex: 1;
}

.window-actions {
  display: flex;
  gap: 4px;
  margin-left: auto;
}

.action-button {
  color: white !important;
  padding: 4px !important;
  min-height: 24px !important;
  width: 24px !important;
  border-radius: 4px !important;
  transition: all 0.2s !important;
}

.action-button:hover {
  background-color: rgba(255, 255, 255, 0.2) !important;
  transform: scale(1.1);
}

.cancel-button:hover {
  background-color: rgba(245, 108, 108, 0.8) !important;
}

.save-button:hover {
  background-color: rgba(103, 194, 58, 0.8) !important;
}

.title-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.window-body {
  flex: 1;
  overflow: hidden;
  background: #fafafa;
  border: 1px solid #e0e0e0;
  border-top: none;
  border-radius: 0 0 8px 8px;
  position: relative;
}

.content-wrapper {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px;
}

/* 自定义滚动条样式 */
.content-wrapper::-webkit-scrollbar {
  width: 6px;
}

.content-wrapper::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.content-wrapper::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
  transition: background-color 0.2s;
}

.content-wrapper::-webkit-scrollbar-thumb:hover {
  background: #666;
}

.content-wrapper::-webkit-scrollbar-corner {
  background: #f1f1f1;
}

.markdown-content {
  width: 100%;
  min-height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
}

.markdown-content :deep(h1) {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #2c3e50;
  border-bottom: 2px solid #667eea;
  padding-bottom: 8px;
}

.markdown-content :deep(h2) {
  font-size: 18px;
  font-weight: 600;
  margin: 20px 0 12px 0;
  color: #34495e;
}

.markdown-content :deep(h3) {
  font-size: 16px;
  font-weight: 600;
  margin: 16px 0 8px 0;
  color: #34495e;
}

.markdown-content :deep(p) {
  margin: 12px 0;
  line-height: 1.7;
}

.markdown-content :deep(ul), .markdown-content :deep(ol) {
  margin: 12px 0;
  padding-left: 24px;
}

.markdown-content :deep(li) {
  margin: 6px 0;
  line-height: 1.6;
}

.markdown-content :deep(code) {
  background: #f1f2f6;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #e74c3c;
}

.markdown-content :deep(pre) {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 16px 0;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.4;
}

.markdown-content :deep(pre)::-webkit-scrollbar {
  height: 6px;
}

.markdown-content :deep(pre)::-webkit-scrollbar-track {
  background: #34495e;
  border-radius: 3px;
}

.markdown-content :deep(pre)::-webkit-scrollbar-thumb {
  background: #7f8c8d;
  border-radius: 3px;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #667eea;
  margin: 16px 0;
  padding-left: 16px;
  color: #7f8c8d;
  font-style: italic;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  text-align: center;
  padding: 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

/* 编辑模式样式 */
.editing-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-editor {
  flex: 1;
  height: 100% !important;
}

.content-editor :deep(.el-textarea__inner) {
  height: 100% !important;
  resize: none;
  border: none;
  border-radius: 0;
  box-shadow: none;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  padding: 16px;
  background: #fafafa;
  color: #333;
}

.content-editor :deep(.el-textarea__inner):focus {
  box-shadow: none;
  background: white;
}

.editing-hint {
  padding: 8px 16px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  color: #6c757d;
  text-align: center;
  font-size: 12px;
  flex-shrink: 0;
}

/* 折叠状态样式 */
.collapsed-button {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s;
}

.collapsed-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.collapsed-button:active {
  transform: scale(0.95);
}

.collapsed-icon {
  color: white;
  font-size: 20px;
  pointer-events: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .note-window:not(.is-collapsed) {
    width: 280px !important;
    height: 350px !important;
  }

  .window-header {
    padding: 10px 12px;
  }

  .content-wrapper {
    padding: 12px;
  }

  .window-title {
    font-size: 13px;
  }

  .markdown-content {
    font-size: 13px;
  }
}
</style>