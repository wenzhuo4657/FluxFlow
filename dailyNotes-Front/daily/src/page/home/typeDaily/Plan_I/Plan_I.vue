<script setup lang="ts">
import CreatePlanForm from './CreatePlanForm.vue';
import ItemViewAndEdit from './ItemViewAndEdit.vue';
</script>

<template>
  <div class="plan-container">
    <!-- 左侧：创建计划表单 -->
    <div class="form-wrapper">
      <CreatePlanForm />
    </div>

    <!-- 右侧：计划列表（由你实现） -->
    <div class="content-wrapper">
      <ItemViewAndEdit></ItemViewAndEdit>
    </div>
  </div>
</template>

<style scoped>
.plan-container {
  display: flex;
  height: 100%;
  width: 100%;
  gap: 20px;
  padding: 20px;
}

.form-wrapper {
  width: 35%;
  min-width: 320px;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}
</style>
