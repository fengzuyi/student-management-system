<template>
  <div class="course-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <div class="header-operations">
            <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedIds.length">批量删除</el-button>
            <el-button type="primary" @click="handleAdd">新增课程</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="课程代码">
          <el-input v-model="queryParams.courseCode" placeholder="请输入课程代码" clearable />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="courseList"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="课程代码" prop="courseCode" />
        <el-table-column label="课程名称" prop="courseName" />
        <el-table-column label="学分" prop="credits" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="courseForm.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseForm.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学分" prop="credits">
          <el-input-number 
            v-model="courseForm.credits" 
            :min="0.5" 
            :max="10" 
            :step="0.5"
            :precision="1"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getCourseList, addCourse, updateCourse, deleteCourse, type Course, type CourseQuery } from '@/api/course'

// 查询参数
const queryParams = reactive<CourseQuery>({
  courseCode: '',
  courseName: '',
  pageNum: 1,
  pageSize: 10
})

// 数据列表
const courseList = ref<Course[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const courseFormRef = ref<FormInstance>()
const courseForm = reactive<Course>({
  courseCode: '',
  courseName: '',
  credits: 1.0
})

// 表单校验规则
const rules = reactive<FormRules>({
  courseCode: [
    { required: true, message: '请输入课程代码', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  credits: [
    { required: true, message: '请输入学分', trigger: 'blur' },
    { type: 'number', min: 0.5, max: 10, message: '学分必须在 0.5 到 10 之间', trigger: 'blur' }
  ]
})

// 获取课程列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCourseList(queryParams)
    courseList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.courseCode = ''
  queryParams.courseName = ''
  handleQuery()
}

// 多选框选中数据
const handleSelectionChange = (selection: Course[]) => {
  selectedIds.value = selection.map(item => item.id!)
}

// 新增按钮操作
const handleAdd = () => {
  dialogTitle.value = '添加课程'
  courseForm.id = undefined
  courseForm.courseCode = ''
  courseForm.courseName = ''
  courseForm.credits = 1.0
  dialogVisible.value = true
}

// 修改按钮操作
const handleEdit = (row: Course) => {
  dialogTitle.value = '修改课程'
  Object.assign(courseForm, row)
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!courseFormRef.value) return
  await courseFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (courseForm.id) {
          await updateCourse(courseForm)
          ElMessage.success('修改成功')
        } else {
          await addCourse(courseForm)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交表单失败:', error)
      }
    }
  })
}

// 删除按钮操作
const handleDelete = (row: Course) => {
  ElMessageBox.confirm('确认要删除该课程吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse([row.id!])
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 批量删除操作
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  ElMessageBox.confirm(`确认要删除选中的 ${selectedIds.value.length} 条数据吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse(selectedIds.value)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  })
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

onMounted(() => {
  getList()
})
</script>

<style scoped>
.course-container {
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