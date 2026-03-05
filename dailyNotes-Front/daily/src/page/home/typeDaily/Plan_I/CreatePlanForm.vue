<script setup lang="ts">
import { ref } from 'vue';
import { useCounterStore } from '@/storage/DocsView';
import {
  addItemByTypeWithFields,
  InsertItemWithFieldsRequest
} from '@/services/request';

// ==================== 字段常量定义 ====================
/**
 * Plan_I 类型支持的字段（对应后端 Plan_I_Field）
 * - title: 标题（默认 "摸鱼~~~~~"）
 * - data_start: 时间起点
 * - data_end: 时间终点（null表示没有设置终点）
 * - task_status: 任务状态（1-完成，2-待做，3-销毁）
 * - parent_id: 父任务ID（用于建立任务间的父子关系）
 */

// 任务状态枚举（对应后端 ItemFiled.task_status）
enum TaskStatus {
  COMPLETED = '1',    // 完成
  TODO = '2',         // 待做（默认）
  DESTROYED = '3'     // 销毁（父任务完成或取消任务）
}

const store = useCounterStore();

// 表单字段（对应 Plan_I_Field）
interface PlanFields {
  title: string;
  data_start: string;
  data_end: string;
  task_status: TaskStatus;
  // parent_id: string;
}

/**
 * 获取今天的日期，格式为 YYYY-MM-DD
 */
function getTodayDate(): string {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

const formData = ref<PlanFields>({
  title: '',
  data_start: getTodayDate(),
  data_end: '',
  task_status: TaskStatus.TODO,  // 默认待做
  // parent_id: ''
});

// 定义事件
const emit = defineEmits<{
  success: []
}>();

/**
 * 插入带字段的计划条目
 */
async function insertItemWithFields() {
  // 验证表单
  if (!formData.value.title.trim()) {
    alert('请输入标题');
    return;
  }
  if (!formData.value.data_start) {
    alert('请选择开始日期');
    return;
  }

  const docsId = store.getCurrentDocsId || '';
  const typeId = store.getCurrentTypeId || '';

  // 构建请求参数（使用普通对象，Map 会被序列化为空对象）
  const fieldsObj: Record<string, string> = {};

  // 必填字段
  fieldsObj.title = formData.value.title;
  fieldsObj.data_start = formData.value.data_start;

  // 可选字段（仅在有值时设置）
  if (formData.value.data_end) {
    fieldsObj.data_end = formData.value.data_end;
  }
  if (formData.value.task_status) {
    fieldsObj.task_status = formData.value.task_status;
  }
  // if (formData.value.parent_id) {
  //   fieldsObj.parent_id = formData.value.parent_id;
  // }

  const data: InsertItemWithFieldsRequest = {
    docsId: docsId,
    type: typeId,
    fields: fieldsObj as any
  };

  console.log('创建计划，字段:', fieldsObj);

  const success = await addItemByTypeWithFields(data);

  if (success) {
    // 重新加载文档列表
    store.triggerRefresh();
    // 重置表单
    resetForm();
    // 触发成功事件
    emit('success');
    alert('创建成功');
  } else {
    alert('创建失败');
  }
}

/**
 * 重置表单
 */
function resetForm() {
  formData.value = {
    title: '',
    data_start: getTodayDate(),
    data_end: '',
    task_status: TaskStatus.TODO,
    // parent_id: ''
  };
}
</script>

<template>
  <div class="form-section">
    <h3>创建新计划</h3>

    <!-- 标题（必填，默认 "摸鱼~~~~~"） -->
    <div class="form-group">
      <label>标题 *</label>
      <input
        v-model="formData.title"
        type="text"
        placeholder="例如：完成项目开发"
        class="form-input"
      />
    </div>

    <!-- 时间范围 -->
    <div class="form-row">
      <!-- 开始日期（必填） -->
      <div class="form-group">
        <label>开始日期 * (data_start)</label>
        <input
          v-model="formData.data_start"
          type="date"
          class="form-input"
        />
      </div>

      <!-- 结束日期（可选，null表示一直执行） -->
      <div class="form-group">
        <label>结束日期 (data_end)</label>
        <input
          v-model="formData.data_end"
          type="date"
          class="form-input"
        />
        <small class="field-hint">留空表示没有终点，即一直执行</small>
      </div>
    </div>

    <!-- 任务状态 -->
    <div class="form-group">
      <label>任务状态 (task_status)</label>
      <select v-model="formData.task_status" class="form-select">
        <option :value="TaskStatus.TODO">待做（默认）</option>
        <option :value="TaskStatus.COMPLETED">已完成</option>
        <option :value="TaskStatus.DESTROYED">已销毁</option>
      </select>
    </div>

    <!-- 父任务ID（可选，用于建立父子关系） -->
    <!-- <div class="form-group">
      <label>父任务ID (parent_id)</label>
      <input
        v-model="formData.parent_id"
        type="text"
        placeholder="留空表示顶级任务"
        class="form-input"
      />
      <small class="field-hint">用于建立任务间的父子关系</small>
    </div> -->

    <button @click="insertItemWithFields" class="btn-submit">
      创建计划
    </button>
  </div>
</template>

<style scoped>
.form-section {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.field-hint {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

.form-input,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-row .form-group {
  flex: 1;
}

.btn-submit {
  width: 100%;
  padding: 12px;
  margin-top: 10px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover {
  background: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
}

.btn-submit:active {
  background: #3a8ee6;
  transform: translateY(0);
}
</style>
