<template>
  <div class="user-list-container">
    <div class="header">
      <h2>用户管理</h2>
      <div class="header-buttons">
        <el-button 
          type="danger" 
          :disabled="!selectedUsers.length"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
        <el-button type="primary" @click="handleAdd" :icon="Plus">添加用户</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="userList"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="password" label="密码" width="150" />
      <el-table-column prop="realName" label="真实姓名" width="150" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'">
            {{ row.role === 'ADMIN' ? '管理员' : '教师' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button
            type="danger"
            link
            @click="handleDelete(row)"
            :disabled="row.id === currentUserId"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item
          label="密码"
          prop="password"
          v-if="dialogType === 'add'"
        >
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（不填则使用默认密码：123456）"
            show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { listUsers, createUser, updateUser, deleteUser, type UserDTO } from '@/api/user'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const userList = ref<UserDTO[]>([])
const currentUserId = ref<number>()
const selectedUsers = ref<UserDTO[]>([])

// 表单数据
const form = reactive<UserDTO>({
  username: '',
  password: '',
  realName: '',
  role: 'TEACHER'
})

// 表单验证规则
const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 50, message: '真实姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await listUsers()
    console.log('获取到的用户列表数据:', res.data)
    userList.value = res.data
    // 获取当前用户ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    currentUserId.value = userInfo.id
  } catch (error: any) {
    console.error('获取用户列表失败:', error)
    ElMessage.error(error.response?.data?.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.username = ''
  form.password = ''
  form.realName = ''
  form.role = 'TEACHER'
}

// 编辑用户
const handleEdit = (row: UserDTO) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 删除用户
const handleDelete = async (row: UserDTO) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await deleteUser(row.id!)
    ElMessage.success('删除成功')
    fetchUserList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (dialogType.value === 'add') {
          await createUser(form)
          ElMessage.success('添加成功')
        } else {
          await updateUser(form.id!, form)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchUserList()
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 表格多选
const handleSelectionChange = (selection: UserDTO[]) => {
  selectedUsers.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  // 检查是否包含当前登录用户
  const hasCurrentUser = selectedUsers.value.some(user => user.id === currentUserId.value)
  if (hasCurrentUser) {
    ElMessage.warning('不能删除当前登录用户')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    
    // 执行批量删除
    const deletePromises = selectedUsers.value.map(user => deleteUser(user.id!))
    await Promise.all(deletePromises)
    
    ElMessage.success('批量删除成功')
    fetchUserList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '批量删除失败')
    }
  }
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-list-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.header-buttons {
  display: flex;
  gap: 10px;
}
</style> 