<script setup lang="ts">

import { deleteItemByTypes, ItemData, updateItemByType, UpdateItemRequest } from '@/services/request'
import { ref } from 'vue';
import { useI18n } from 'vue-i18n'
import MarkdownView from '../../components/content/markdownView.vue';
import { useCounterStore } from '@/storage/DocsView'
import { ElMessageBox } from 'element-plus';
const { t, locale } = useI18n()

const store = useCounterStore()

// 使用默认值选项
const item = defineModel<ItemData>("item",{
  default: () => ({
    index: "0",
    content:"待写入"
  })
})

const edit_val=ref(false);

const tempContent=ref<String>('')

function editStatus(){
  // 不可重复编辑
  if(edit_val.value==true){
    return
  }
    edit_val.value=true
    tempContent.value=item.value.content
}

function save(){
   // 不可重复保存
  if(edit_val.value==false){
    return
  }
    edit_val.value=false
    // 如果tempContent为空，说明没有进入过编辑模式，直接使用item.content
    if (tempContent.value === '') {
        tempContent.value = item.value.content || ''
    }
    item.value.content = String(tempContent.value);
    // 保存api调用
    const  data:UpdateItemRequest={index:item.value.index,content:item.value.content}
    updateItemByType(data)

}

function deleteItem(item: ItemData) {

  
  // 二次确认
  try {
    ElMessageBox.confirm(
      '确定要删除该文档吗？删除后无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
  } catch {
    // 用户取消
    return
  }

  deleteItemByTypes(item.index)

  // 发送刷新事件，重新加载文档组件
  store.triggerRefresh();
}







</script>

<template>


    <div  class="sqlit">
        <!--按扭区 -->
        <div>
            <button  @click="editStatus">{{ $t('editItem') }}</button>
            <button  @click="save">{{ $t('saveItem') }}</button>
            <el-button   type="danger"  @click="deleteItem(item)">{{ $t('deleteItem') }}</el-button>
        </div>
      
        <!-- 编辑与预览区 -->
        <div>
                <div v-if="!edit_val">
              
                 <MarkdownView   v-model="item.content"></MarkdownView>
                    </div>
                <div v-else  class="editor-wrap">
                     <el-input
                        v-model="tempContent"
                        type="textarea"
                        :autosize="{ minRows: 16, maxRows: 40 }"
                        class="edit_input"
                        spellcheck="false"
                        placeholder="Please input"
                        />
                </div>
        </div>
      
       

            
    </div>


</template>

<style>
.sqlit{
    width:  100%;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.editor-wrap {
  flex: 1;
  display: flex;
  align-items: stretch;
  width: 100%;
}
.edit_input { width: 100%; }
.edit_input textarea {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  line-height: 1.5;
}

</style>