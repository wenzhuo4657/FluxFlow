<script setup lang="ts">
import ButtonView from './button/button/ButtonView.vue';
import ButtonBar from './button/ButtonBar.vue';
import UserBar from './button/UserBar.vue';
import { useI18n } from 'vue-i18n'
import { useCounterStore,HomeModels } from '@/storage/DocsView'

const { t } = useI18n()
const store = useCounterStore()

function changedModel(type:number){
      store.setHomeModel(type)
}


// 是否隐藏当前元素  true: 不隐藏  ， false： 隐藏
const sidebarOpen=defineModel('sidebarOpen')


</script>
<template>
    <div class="setup-container" v-if="sidebarOpen">
        <!-- 菜单栏分为两类
             一：实际内容： 文档预览（充当默认）、配置、文档类型列表、
             二: 用户信息： 展示用户信息、登出按钮
              -->

        <div class="main-content">
            <!-- 这里放置主内容：文档预览、文档类型列表、配置等 -->
             <!-- <ButtonBar></ButtonBar> -->
            <!--  1， 预览组件，默认路由选项 -->
            <el-button type="success" plain class="sidebar-item" v-on:click="changedModel(HomeModels.DEFAULT)">
                   {{t('index')}}
            </el-button>

             <!-- 2,配置组件 -->
            <el-button type="success" plain class="sidebar-item" v-on:click="changedModel(HomeModels.CONFIGURATION)">
                    {{t('configuration')}}
            </el-button>
    
             <!-- 3， 文档类型组件，展示文档类型 -->
             <div  class="viewType-banner"  v-on:click="changedModel(HomeModels.DOCS_LIST)">
                <ButtonView></ButtonView>
             </div>

        </div>
        <UserBar  class="bottom"></UserBar>
    </div>

</template>
<style>

.setup-container {
    height: 100%;  /*  高度100% ，绝对不允许溢出！*/
    width: 100%;
    display: flex;
    flex-direction: column;
 
}

.main-content {
    height: 70%;
    background-color: rgb(248, 243, 243);
    display: flex;
    flex-direction: column;
}

.sidebar-item{
    height: 10%;
    width:  100%;   
    margin-left: 0 !important; /* 覆盖 Element Plus 的默认样式 */
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);    /* 阴影*/
    text-align: center;

}

.viewType-banner{
    margin-top: 5%;
    height: 70%;
}


.bottom {
    height: 30%;
    width: 100%;
}


</style>