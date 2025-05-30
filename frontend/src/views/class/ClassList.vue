<template>
  <div class="class-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
          <div>
            <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedClasses.length" style="margin-right: 10px">批量删除</el-button>
            <el-button type="success" @click="handleExport" style="margin-right: 10px">导出Excel</el-button>
            <el-button type="primary" @click="handleAdd">新增班级</el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="classList" 
        border 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="className" label="班级名称" width="150" />
        <el-table-column prop="grade" label="年级" width="120" />
        <el-table-column prop="major" label="专业" width="200" />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="classForm"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
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

interface ClassInfo {
  id: number
  className: string
  grade: string
  major: string
  studentCount: number
}

// 数据列表
const classList = ref<ClassInfo[]>([])
const selectedClasses = ref<ClassInfo[]>([])

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const classForm = ref<FormInstance>()
const form = reactive({
  id: undefined,
  className: '',
  grade: '',
  major: ''
})

// 表单校验规则
const rules = reactive<FormRules>({
  className: [
    { required: true, message: '请输入班级名称', trigger: 'submit' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'submit' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'submit' }
  ]
})

// 获取班级列表
const getList = async () => {
  try {
    const res = await request.get('/api/class/list')
    classList.value = res.data
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

// 新增班级
const handleAdd = () => {
  dialogTitle.value = '新增班级'
  dialogVisible.value = true
  // 重置表单数据
  Object.assign(form, {
    id: undefined,
    className: '',
    grade: '',
    major: ''
  })
  // 重置表单验证状态
  nextTick(() => {
    classForm.value?.clearValidate()
  })
}

// 编辑班级
const handleEdit = (row: ClassInfo) => {
  dialogTitle.value = '编辑班级'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 导出Excel
const handleExport = async () => {
  try {
    ElMessage.info('正在导出Excel，请稍候...')
    const response = await axios.get('/api/class/export', {
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
    link.setAttribute('download', '班级信息.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功，文件已开始下载')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '导出失败，请稍后重试')
  }
}

// 删除班级
const handleDelete = (row: ClassInfo) => {
  ElMessageBox.confirm(
    `确认删除该班级吗？\n注意：删除班级将同时删除该班级下的所有学生信息！`,
    '警告',
    {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      await request.delete(`/class/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const submitForm = async () => {
  if (!classForm.value) return
  await classForm.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await request.put('/class', form)
          ElMessage.success('修改成功')
        } else {
          await request.post('/class', form)
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

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单验证状态
  classForm.value?.clearValidate()
}

// 处理表格选择变化
const handleSelectionChange = (selection: ClassInfo[]) => {
  selectedClasses.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedClasses.value.length === 0) {
    ElMessage.warning('请选择要删除的班级')
    return
  }

  const classNames = selectedClasses.value.map(item => item.className).join('、')
  ElMessageBox.confirm(
    `确认删除以下班级吗？\n${classNames}\n注意：删除班级将同时删除该班级下的所有学生信息！`,
    '警告',
    {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      const ids = selectedClasses.value.map(item => item.id)
      await request.delete('/class/batch', { data: ids })
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.class-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 