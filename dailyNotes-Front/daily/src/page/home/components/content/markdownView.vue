 <script setup lang="ts">
 import { ref, computed, watch } from 'vue'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'

 const model = defineModel<string>({ default: '' })

// 解析器
const md = new MarkdownIt({ breaks: true })

const   raw=computed(() =>{
    return md.render(model.value);
})

</script>

<template>
    <span v-html="raw"></span>

</template>

<style>
</style>