<script setup lang="ts">
import { connectionBase, GetItemsRequest, getMdByType, ItemData, updateItemByType, UpdateItemRequest, updateTaskRequest as updateTaskApi, UpdateTaskRequest } from '@/services/request';
import { useCounterStore } from '@/storage/DocsView';
import { ref, watch, computed, onMounted } from 'vue';
import MarkdownView from '@/page/home/components/content/markdownView.vue';

// 任务状态枚举
enum TaskStatus {
  COMPLETED = '1',    // 完成
  TODO = '2',         // 待做
  DESTROYED = '3'     // 销毁
}

// 任务状态显示顺序：2(待做) -> 1(完成) -> 3(销毁)
const STATUS_ORDER: TaskStatus[] = [TaskStatus.TODO, TaskStatus.COMPLETED, TaskStatus.DESTROYED];

// Plan 数据接口
interface Plan {
  task_ID: string;
  title: string;
  data_start: string;
  data_end: string | null;
  task_status: TaskStatus;
  score: string | null;
  parent_id: string | null;
  content: string;
  connection:string|null;
}

const store = useCounterStore();
const res = ref<ItemData[]>([]);
const plans = ref<Plan[]>([]);

onMounted(() => {
    loadData(store.getCurrentDocsId)
  
})

// 监听文档 ID 变化
watch(() => store.getCurrentDocsId, (newDocsId) => {
  if (newDocsId) {
    loadData(newDocsId);
  }
}, { immediate: true });

// 监听刷新触发器
watch(() => store.getRefreshTrigger, () => {
  const docsId = store.getCurrentDocsId;
  if (docsId) {
    loadData(docsId);
  }
}, { immediate: false });

/**
 * 解析 expand 字符串，提取字段信息
 * 格式: "score:5 data_end:2026-01-18 task_status:2 parent_id:null data_start:2026-01-17 title:摸鱼~~~~~"
 */
function parseExpand(expand: string): Partial<Plan> {
  const fields: Partial<Plan> = {};

  if (!expand) return fields;

  // 按空格分割，然后解析每个字段
  const parts = expand.split(' ');
  for (const part of parts) {
    const colonIndex = part.indexOf(':');
    if (colonIndex === -1) continue;

    const key = part.substring(0, colonIndex);
    const value = part.substring(colonIndex + 1);

    switch (key) {
      case 'title':
        fields.title = value;
        break;
      case 'data_start':
        fields.data_start = value;
        break;
      case 'data_end':
        fields.data_end = value === 'null' ? null : value;
        break;
      case 'task_status':
        fields.task_status = value as TaskStatus;
        break;
      case 'score':
        fields.score = value === 'null' ? null : value;
        break;
      case 'parent_id':
        fields.parent_id = value === 'null' ? null : value;
        break;
      case 'connection':
        fields.connection= value== 'null'?null :value
    }
  }

  return fields;
}

/**
 * 将 ItemData 转换为 Plan
 */
function convertToPlan(item: ItemData): Plan {
  const parsed = parseExpand(item.expand || '');

  return {
    task_ID: item.index,
    title: parsed.title || item.title || '无标题',
    data_start: parsed.data_start || '',
    data_end: parsed.data_end || null,
    task_status: parsed.task_status || TaskStatus.TODO,
    score: parsed.score || null,
    parent_id: parsed.parent_id || null,
    content: item.content || '',
    connection:parsed.connection||''
  };
}

/**
 * 加载数据
 */
async function loadData(id: string) {
  if (id === '') {
    return;
  }

  const typeId = store.getCurrentTypeId || '';
  const data: GetItemsRequest = { docsId: id, type: typeId };

  const result = await getMdByType(data);
  res.value = result;

  // 转换为 Plan 对象
  plans.value = result.map(convertToPlan);
}

/**
 * 排序和分组后的计划列表
 * 规则：
 * 1. 按 task_status 分组，顺序为 2(待做) -> 1(完成) -> 3(销毁)
 * 2. 每组内按 data_start 降序排序（最近的在前）
 */
const groupedPlans = computed(() => {
  // 按状态分组
  const grouped: Record<TaskStatus, Plan[]> = {
    [TaskStatus.TODO]: [],
    [TaskStatus.COMPLETED]: [],
    [TaskStatus.DESTROYED]: []
  };

  // 分组
  for (const plan of plans.value) {
    grouped[plan.task_status].push(plan);
  }

  // 每组内按 data_start 降序排序（最近的在前）
  for (const status of STATUS_ORDER) {
    grouped[status].sort((a, b) => {
      return b.data_start.localeCompare(a.data_start);
    });
  }

  return grouped;
});

/**
 * 获取状态显示文本
 */
function getStatusText(status: TaskStatus): string {
  switch (status) {
    case TaskStatus.COMPLETED:
      return '已完成';
    case TaskStatus.TODO:
      return '待做';
    case TaskStatus.DESTROYED:
      return '已销毁';
    default:
      return '未知';
  }
}

/**
 * 完成任务（携带 score 分数）
 */
async function completeTask(plan: Plan,taskStatus:TaskStatus) {
  // 弹出输入框让用户评分
  const inputScore = prompt('请为此任务评分 (1-10):', plan.score || '5');

  // 用户取消输入
  if (inputScore === null) {
    return;
  }

  // 验证评分
  const scoreNum = parseInt(inputScore);
  if (isNaN(scoreNum) || scoreNum < 1 || scoreNum > 10) {
    alert('请输入 1-10 之间的数字');
    return;
  }

  const data: UpdateTaskRequest = {
    taskId: plan.task_ID,
    taskStatus: taskStatus,
    score: inputScore
  };

  const success = await updateTaskApi(data);
}


/**
 * 选择任务列表
 */
const tasklist = ref<TaskStatus>(TaskStatus.TODO);

/**
 * 切换任务列表
 */
function switchTaskList(status: TaskStatus) {
  tasklist.value = status;
}

/**
 * 获取每个状态的任务数量
 */
const taskCounts = computed(() => ({
  [TaskStatus.TODO]: groupedPlans.value[TaskStatus.TODO].length,
  [TaskStatus.COMPLETED]: groupedPlans.value[TaskStatus.COMPLETED].length,
  [TaskStatus.DESTROYED]: groupedPlans.value[TaskStatus.DESTROYED].length
}));

// 编辑状态
const editingPlanId = ref<string | null>(null);
const editingContent = ref('');

/**
 * 开始编辑
 */
function startEdit(plan: Plan) {
  editingPlanId.value = plan.task_ID;
  editingContent.value = plan.content;
}

/**
 * 取消编辑
 */
function cancelEdit() {
  editingPlanId.value = null;
  editingContent.value = '';
}

/**
 * 保存编辑
 */
async function saveEdit(plan: Plan) {
  const data: UpdateItemRequest = {
    index: plan.task_ID,
    content: editingContent.value
  };

  const success = await updateItemByType(data);

  if (success) {
    // 更新本地数据
    plan.content = editingContent.value;
    // 同时更新 res 中的数据
    const item = res.value.find(i => i.index === plan.task_ID);
    if (item) {
      item.content = editingContent.value;
    }
    cancelEdit();
    alert('保存成功');
  } else {
    alert('保存失败');
  }
}


/**
 * 跳转到目标文档
 */
function route(docsId:string){
  store.setCurrentTypeId("0")
  store.setCurrentDocsId(docsId)
  store.setCurrentView('dailyBase')
}

/**
 * 关联基本文档
 */
function connection(taskId:string){
  connectionBase(taskId)
}

</script>

<template>
  <!-- 选择任务列表 -->
  <div class="task-selector">
    <button
      v-for="status in STATUS_ORDER"
      :key="status"
      :class="['task-btn', { active: tasklist === status }]"
      @click="switchTaskList(status)"
    >
      {{ getStatusText(status) }} ({{ taskCounts[status] }})
    </button>
  </div>

  <!-- 任务列表 -->
  <div v-for="plan in groupedPlans[tasklist]" :key="plan.task_ID" class="plan-card">
    <div class="plan-header">
      <strong>{{ plan.title }}</strong>
      <span class="plan-id">ID: {{ plan.task_ID }}</span>
    </div>

    <!-- 元信息 -->
    <div class="plan-meta">
      <div v-if="plan.data_start"><span>开始: {{ plan.data_start }}</span></div>
      <div v-if="plan.data_end"><span>结束: {{ plan.data_end }}</span></div>
      <div v-if="plan.parent_id"><span>父任务: {{ plan.parent_id }}</span></div>
      <!-- <div><span>状态: {{ getStatusText(plan.task_status) }}</span></div> -->
       <div  v-if="tasklist==TaskStatus.COMPLETED">score:{{ plan.score }}</div>
    </div>

    <!-- 内容 -->
    <div class="plan-content">
      <!-- 编辑模式 -->
      <div v-if="tasklist==TaskStatus.TODO&&editingPlanId === plan.task_ID" class="edit-mode">
        <textarea
          v-model="editingContent"
          class="edit-textarea"
          rows="6"
          placeholder="输入 Markdown 内容..."
        ></textarea>
        <div class="edit-actions">
          <button @click="saveEdit(plan)" class="btn-save">保存</button>
          <button @click="cancelEdit" class="btn-cancel">取消</button>
        </div>
      </div>
      <!-- 查看模式 -->
      <div v-else>
        <div v-if="plan.content" class="content-text" @click="startEdit(plan)">
          <MarkdownView v-model="plan.content" />
        </div>
        <div v-else class="content-empty" @click="startEdit(plan)">
          点击添加内容...
        </div>
      </div>
    </div>

    <!--  按钮 -->
    <button v-if="tasklist==TaskStatus.TODO&&editingPlanId !== plan.task_ID" @click="completeTask(plan,TaskStatus.COMPLETED)">✓ 完成</button>
    <button v-if="tasklist==TaskStatus.COMPLETED&&editingPlanId !== plan.task_ID" @click="completeTask(plan,TaskStatus.DESTROYED)">✓ 销毁</button>
    <button v-if="plan.connection==''"  @click="connection(plan.task_ID)">创建关联文档</button>
    <button  v-if="plan.connection!=null&&plan.connection!=''"  @click="route(plan.connection)">执行状况</button>
  </div>



</template>

<style scoped>
/* 任务选择器 */
.task-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
}

.task-btn {
  flex: 1;
  padding: 10px 16px;
  background: white;
  color: #666;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.task-btn:hover {
  color: #409eff;
  border-color: #409eff;
}

.task-btn.active {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

/* 任务卡片 */
.plan-card {
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.plan-id {
  font-size: 12px;
  color: #999;
  font-family: monospace;
}

.plan-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  padding: 8px;
  background: #f9f9f9;
  border-radius: 4px;
  font-size: 13px;
  color: #666;
}

.plan-content {
  margin-bottom: 12px;
}

.edit-mode {
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.edit-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  box-sizing: border-box;
}

.edit-textarea:focus {
  outline: none;
  border-color: #409eff;
}

.edit-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  justify-content: flex-end;
}

.btn-save {
  padding: 8px 16px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-save:hover {
  background: #73d13d;
}

.btn-cancel {
  padding: 8px 16px;
  background: #999;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #777;
}

.content-text {
  padding: 10px;
  background: #fafafa;
  border-radius: 4px;
  white-space: pre-wrap;
  cursor: pointer;
  transition: background 0.3s;
}

.content-text:hover {
  background: #f0f0f0;
}

.content-empty {
  padding: 10px;
  background: #fafafa;
  border-radius: 4px;
  color: #999;
  cursor: pointer;
  transition: background 0.3s;
}

.content-empty:hover {
  background: #f0f0f0;
}

.plan-card button {
  padding: 8px 16px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.plan-card button:hover {
  background: #73d13d;
}
</style>
