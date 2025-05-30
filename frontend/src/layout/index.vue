<template>
  <el-container class="app-wrapper">
    <el-header>
      <div class="header-content">
        <div class="header-left">
          <img src="@/assets/header.png" alt="logo" class="header-logo">
          <h2 class="header-title">学生管理系统</h2>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="showChangePasswordDialog" :icon="Key">修改密码</el-button>
          <el-button type="danger" @click="handleLogout" :icon="SwitchButton">退出登录</el-button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          router
          :default-active="$route.path"
          class="el-menu-vertical"
        >
          <el-sub-menu index="/student-management">
            <template #title>
              <el-icon><School /></el-icon>
              <span>学生管理系统</span>
            </template>
            <el-menu-item index="/student-management/student-list">
              <el-icon><User /></el-icon>
              <span>学生管理</span>
            </el-menu-item>
            <el-menu-item index="/student-management/class-list">
              <el-icon><House /></el-icon>
              <span>班级管理</span>
            </el-menu-item>
            <el-menu-item index="/student-management/grade-management">
              <el-icon><Document /></el-icon>
              <span>成绩管理</span>
            </el-menu-item>
            <el-menu-item 
              v-if="userInfo.role === 'ADMIN'"
              index="/student-management/user-list"
            >
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="changePasswordDialogVisible"
      title="修改密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
            确认修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { User, School, Document, House, SwitchButton, Key, UserFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout, changePassword } from '@/api/auth'
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const changePasswordDialogVisible = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref<FormInstance>()
const userInfo = ref<any>({})

onMounted(() => {
  // 获取用户信息
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
  }
})

// 密码表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 显示修改密码对话框
const showChangePasswordDialog = () => {
  changePasswordDialogVisible.value = true
  // 重置表单
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 处理修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        await changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        changePasswordDialogVisible.value = false
        // 退出登录
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.replace('/login')
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || '密码修改失败')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

const handleLogout = async () => {
  try {
    // 先清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 然后调用退出登录接口
    await logout()
    
    ElMessage.success('退出登录成功')
    // 强制跳转到登录页面
    router.replace('/login')
  } catch (error: any) {
    console.error('退出登录失败:', error)
    // 即使接口调用失败，也清除本地存储并跳转到登录页
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.replace('/login')
    ElMessage.error('退出登录失败，请重新登录')
  }
}
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 70px;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #dcdfe6;
}

.el-menu {
  border-right: none;
}

.el-main {
  background-color: #f0f2f5;
  padding: 0;
  height: calc(100vh - 70px);
  overflow: auto;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;  /* 添加按钮之间的间距 */
}

.header-logo {
  height: 68px;
  width: auto;
  margin: 5px 0;
}

.header-title {
  color: #0054A3;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
  font-family: "STXingkai", "Xingkai SC", "华文行楷", "楷体", sans-serif;
}
</style> 