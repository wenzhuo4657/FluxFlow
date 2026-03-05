<script setup lang="ts">
import { ref } from 'vue';
import SystemButtonBar from './components/button/SystemButtonBar/SystemButtonBar.vue';
import Home from './Home.vue';
import setup from './setup/setup.vue';
import { computed } from 'vue';
import setupIcon from '/images/setup.svg';


const sidebarOpen= ref(false);//侧边栏显示/隐藏开关

function toggleSidebar(){
  sidebarOpen.value=!sidebarOpen.value
}



</script>

<!-- 
编写要点
1，每个page只有layout.vue组件，能够使用vw、vh，其余子组件尽量根据父容器、父组件的高度决定自身大小
%： 该单位的使用需要依赖明确的参考对象，
父元素要有明确高度，或者同样适用百分号，但最终一定能够链式追溯到一个明确的高度

-->






<template>

   
<div  class="any">


       <div
       class="sidebar"
       :class="{ 'sidebar-open': sidebarOpen,'sidebar-close': !sidebarOpen}"
       >
          <div >
            <img
            v-on:click="toggleSidebar"
            :src="setupIcon"
            style="width: 2vw;height: 2vw; "
            alt="出错了">
          </div>
          <setup  v-model:sidebarOpen=sidebarOpen></setup>
      </div>

      <div class="body">
   
        <Home></Home>
      </div>
</div>
 
      
</template>

<style>

.any{
  display: flex;
  background: rgb(222, 215, 171);
  flex-direction: row;
  overflow: hidden;
}



.body{
  flex: 1;
  min-width: 80vw;
  height: 100vh;
}
.sidebar{
  transition: width 0.3s ease;
  padding: 2vh 0 3vh 0.5vw;
  top: 0px;
  left: 0px;

}

.sidebar-open{
  width: 20vw;
  height: 95vh;
  background-color: rgb(147, 190, 227);

  border-radius: 0 12px 12px 0;/*圆角 */
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);/*阴影*/

}

.sidebar-close{
  width: 2vw;
  height: 2vw;
}

</style>
