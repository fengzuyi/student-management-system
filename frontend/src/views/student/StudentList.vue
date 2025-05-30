<template>
  <div class="student-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <div class="header-operations">
            <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedStudents.length">批量删除</el-button>
            <el-button type="primary" @click="handleAdd">新增学生</el-button>
            <el-upload
              class="upload-demo"
              action="/api/student/import"
              :on-success="handleImportSuccess"
              :on-error="handleImportError"
              :show-file-list="false"
              :headers="uploadHeaders"
            >
              <el-button type="success">导入Excel</el-button>
            </el-upload>
            <el-button type="info" @click="downloadTemplate">下载模板</el-button>
            <el-button type="warning" @click="handleExport">导出Excel</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" style="width: 200px">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table 
        :data="studentList" 
        border 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="classInfo.className" label="班级" width="150" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="studentForm"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import request from '@/utils/request'
import axios from 'axios'
import { useRouter } from 'vue-router'

interface Student {
  id: number
  studentNo: string
  name: string
  classId: number
  className?: string
  gender: string
  phone?: string
  email?: string
}

interface ClassInfo {
  id: number
  className: string
}

interface ApiResponse<T> {
  code: number
  message?: string
  data: T
}

interface ClassListResponse {
  list: ClassInfo[]
  total: number
}

interface ImportResponse {
  code: number
  message?: string
  data?: any
}

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  studentNo: '',
  name: '',
  classId: ''
})

// 数据列表
const studentList = ref<Student[]>([])
const total = ref(0)
const classOptions = ref<ClassInfo[]>([])
const selectedStudents = ref<Student[]>([])

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const studentForm = ref<FormInstance>()
const form = reactive({
  id: undefined,
  studentNo: '',
  name: '',
  classId: undefined,
  gender: '',
  phone: '',
  email: ''
})

// 表单校验规则
const rules = reactive<FormRules>({
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'submit' },
    { pattern: /^\d{10}$/, message: '学号必须为10位数字', trigger: 'submit' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'submit' }
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'submit' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'submit' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'submit' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'submit' }
  ]
})

// 获取班级列表
const getClassList = async () => {
  try {
    // 检查登录状态
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('未找到token，请先登录')
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    console.log('开始获取班级列表...')
    console.log('当前token:', token)
    
    const res = await request.get('/api/class/list', {
      params: {
        pageNum: 1,
        pageSize: 1000  // 获取足够多的班级数据
      }
    })
    console.log('班级列表响应数据:', res)
    
    // 处理直接返回的列表数据
    const classList = Array.isArray(res.data) ? res.data : 
                     (res.data?.list || [])
    console.log('获取到的班级列表:', classList)
    
    if (classList.length > 0) {
      classOptions.value = classList.map((item: any) => ({
        id: item.id,
        className: item.className
      }))
      console.log('设置后的班级选项:', classOptions.value)
    } else {
      console.warn('班级列表为空')
      ElMessage.warning('暂无班级数据')
    }
  } catch (error: any) {
    console.error('获取班级列表失败，详细错误:', error)
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取班级列表失败')
    }
  }
}

// 获取学生列表
const getList = async () => {
  try {
    // 检查登录状态
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('未找到token，请先登录')
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const params = {
      ...queryParams,
      classId: queryParams.classId || undefined
    }
    const res = await request.get('/api/student/list', { params })
    studentList.value = res.data.list
    total.value = res.data.total
  } catch (error: any) {
    console.error('获取学生列表失败:', error)
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取学生列表失败')
    }
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.studentNo = ''
  queryParams.name = ''
  queryParams.classId = ''
  handleQuery()
}

// 新增学生
const handleAdd = () => {
  dialogTitle.value = '新增学生'
  dialogVisible.value = true
  // 重置表单数据
  Object.assign(form, {
    id: undefined,
    studentNo: '',
    name: '',
    classId: undefined,
    gender: '',
    phone: '',
    email: ''
  })
  // 重置表单验证状态
  nextTick(() => {
    studentForm.value?.clearValidate()
  })
}

// 编辑学生
const handleEdit = (row: Student) => {
  dialogTitle.value = '编辑学生'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 删除学生
const handleDelete = (row: Student) => {
  ElMessageBox.confirm('确认删除该学生信息吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete('/api/student/delete', { data: { ids: [row.id] } })
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const submitForm = async () => {
  if (!studentForm.value) return
  await studentForm.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await request.put('/api/student', form)
          ElMessage.success('修改成功')
        } else {
          await request.post('/api/student', form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        ElMessage.error(form.id ? '修改失败' : '新增失败')
      }
    }
  })
}

// 导入成功
const handleImportSuccess = (response: ImportResponse) => {
  if (response.code === 200) {
    ElMessage.success('导入成功')
    getList()
  } else {
    // 检查是否包含跳过的学生信息
    const message = response.message || '导入失败'
    if (message.includes('导入完成，但以下学生因学号重复被跳过')) {
      ElMessage({
        type: 'warning',
        message: message,
        duration: 10000, // 显示10秒
        showClose: true
      })
      getList() // 即使有跳过的学生，也刷新列表
    } else {
      ElMessage.error(message)
    }
  }
}

// 导入失败
const handleImportError = (error: any) => {
  const message = error.response?.data?.message || '导入失败'
  if (message.includes('导入完成，但以下学生因学号重复被跳过')) {
    ElMessage({
      type: 'warning',
      message: message,
      duration: 10000, // 显示10秒
      showClose: true
    })
    getList() // 即使有跳过的学生，也刷新列表
  } else {
    ElMessage.error(message)
  }
}

// 导出Excel
const handleExport = async () => {
  try {
    ElMessage.info('正在导出Excel，请稍候...')
    const response = await axios.get('/api/student/export', {
      responseType: 'blob',
      headers: {
        'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    // 创建Blob对象并下载
    const blob = new Blob([response.data], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '学生信息.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功，文件已开始下载')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '导出失败，请稍后重试')
  }
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单验证状态
  studentForm.value?.clearValidate()
}

// 上传请求头
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// 下载模板
const downloadTemplate = async () => {
  try {
    const response = await axios.get('/api/student/template', {
      responseType: 'blob',
      headers: {
        'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    const blob = new Blob([response.data], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '学生信息导入模板.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('模板下载成功')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '模板下载失败，请稍后重试')
  }
}

// 处理表格多选
const handleSelectionChange = (selection: Student[]) => {
  selectedStudents.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedStudents.value.length === 0) {
    ElMessage.warning('请选择要删除的学生')
    return
  }

  ElMessageBox.confirm(
    `确认删除选中的 ${selectedStudents.value.length} 名学生吗？`,
    '警告',
    {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      const ids = selectedStudents.value.map(student => student.id)
      await request.delete('/api/student/delete', { data: { ids } })
      ElMessage.success('批量删除成功')
      getList()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '批量删除失败')
    }
  })
}

const router = useRouter()

onMounted(() => {
  getClassList()
  getList()
})
</script>

<style scoped>
.student-list {
  height: 100%;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
}

.el-card {
  background-color: #fff;
  height: 100%;
  border-radius: 0;
  border: none;
  box-shadow: none;
  display: flex;
  flex-direction: column;
}

.el-card :deep(.el-card__body) {
  flex: 1;
  padding: 10px;
  overflow: auto;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.header-operations {
  display: flex;
  gap: 10px;
}

.demo-form-inline {
  padding: 10px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.pagination-container {
  padding: 10px 20px;
  display: flex;
  justify-content: flex-end;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
  margin-top: auto;
}

.el-table {
  margin: 10px 0;
  flex: 1;
  overflow: auto;
}
</style> 