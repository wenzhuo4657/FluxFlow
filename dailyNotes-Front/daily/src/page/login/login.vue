<template>
  <div class="login-container">
    <p>请选择以下方式登录</p>
    <button @click="loginWithGithub" class="github-login-btn">
      <img src="https://github.com/favicon.ico" alt="GitHub Logo" />
      使用 GitHub 登录
    </button>
  </div>
</template>

<script setup>


const loginWithGithub = () => {

  const url = (import.meta.env.VITE_API_BASE || '') + '/api/oauth/render/github';
  console.log(url)
  window.location.href = url
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}
.github-login-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.3s;
}
.github-login-btn:hover {
  background-color: #f0f0f0;
}
.github-login-btn img {
  width: 20px;
  height: 20px;
}
</style>