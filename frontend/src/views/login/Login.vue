<template>
  <div class="login-container">
    <div class="login-box">
      <div class="help-button">
        <el-tooltip
          content="如需登录账号，请联系管理员ariesplus@qq.com"
          placement="right"
          effect="light"
        >
          <el-button
            type="primary"
            :icon="QuestionFilled"
            circle
            class="help-icon"
          />
        </el-tooltip>
      </div>
      <h2>学生管理系统</h2>
      <el-form
        ref="loginForm"
        :model="form"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, QuestionFilled } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loginForm = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  if (!loginForm.value) return
  
  await loginForm.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/api/auth/login', form)
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/login.jpg');
  background-size: cover;
  background-position: center;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #f0f1f3;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}

.help-button {
  position: absolute;
  top: 15px;
  left: 15px;
}

.help-icon {
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  color: #409EFF;
  width: 32px;
  height: 32px;
  padding: 0;
}

.help-icon:hover {
  background-color: #409EFF;
  color: white;
}
</style> 