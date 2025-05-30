<template>
  <div class="not-found">
    <h1>404</h1>
    <h2>页面不存在</h2>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
// 404 页面逻辑
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

h1 {
  font-size: 120px;
  color: #409EFF;
  margin: 0;
}

h2 {
  font-size: 30px;
  color: #606266;
  margin: 20px 0 40px;
}
</style> 