<template>
  <div class="grade-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
          <div class="right">
            <el-button type="danger" :disabled="!selectedGrades.length" @click="handleBatchDelete">
              批量删除
            </el-button>
            <el-button type="primary" @click="showAddModal">录入成绩</el-button>
            <el-upload
              class="upload-demo"
              action="/api/grades/import"
              :headers="uploadHeaders"
              :on-success="handleImportSuccess"
              :on-error="handleImportError"
              :show-file-list="false"
              accept=".xlsx,.xls"
            >
              <el-button type="primary">导入成绩</el-button>
            </el-upload>
            <el-button type="success" @click="handleExport">导出成绩</el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索和操作区域 -->
      <div class="table-operations">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item>
            <el-input
              v-model="searchForm.studentNo"
              placeholder="请输入学号"
              clearable
              @keyup.enter="handleSearch"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="searchForm.semester"
              style="width: 200px"
              placeholder="选择学期"
              clearable
              @change="handleSearch"
            >
              <el-option
                v-for="semester in semesters"
                :key="semester"
                :label="formatSemester(semester)"
                :value="semester"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="searchForm.courseId"
              style="width: 200px"
              placeholder="选择课程"
              clearable
              @change="handleSearch"
            >
              <el-option
                v-for="course in courses"
                :key="course.id"
                :label="course.courseName"
                :value="course.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 成绩表格 -->
      <el-table
        :data="grades"
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="score" label="成绩" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditModal(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 添加/编辑成绩弹窗 -->
      <el-dialog
        :title="modalTitle"
        v-model="modalVisible"
        width="500px"
      >
        <el-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          label-width="80px"
        >
          <el-form-item label="学生" prop="studentId">
            <el-select
              v-model="formState.studentId"
              placeholder="选择学生"
              style="width: 100%"
              filterable
              remote
              :remote-method="filterStudents"
              :loading="studentsLoading"
            >
              <el-option
                v-for="student in filteredStudents"
                :key="student.id"
                :label="`${student.name} (${student.studentNo})`"
                :value="student.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="课程" prop="courseId">
            <el-select
              v-model="formState.courseId"
              placeholder="选择课程"
              style="width: 100%"
              filterable
              remote
              :remote-method="filterCourses"
              :loading="coursesLoading"
            >
              <el-option
                v-for="course in filteredCourses"
                :key="course.id"
                :label="`${course.courseName} (${course.courseCode})`"
                :value="course.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="学期" prop="semester">
            <el-select
              v-model="formState.semester"
              placeholder="选择学期"
              style="width: 100%"
            >
              <el-option
                v-for="semester in semesters"
                :key="semester"
                :label="formatSemester(semester)"
                :value="semester"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="成绩" prop="score">
            <el-input-number
              v-model="formState.score"
              :min="0"
              :max="100"
              :precision="2"
              style="width: 100%"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="handleModalCancel">取消</el-button>
            <el-button type="primary" @click="handleModalOk">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

interface Student {
  id: number
  name: string
  studentNo: string
}

interface Course {
  id: number
  courseName: string
  courseCode: string
}

interface Grade {
  id: number
  studentId: number
  studentName: string
  studentNo: string
  courseId: number
  courseName: string
  courseCode: string
  score: number
  semester: string
}

interface SearchForm {
  semester?: string
  courseId?: number
  studentNo?: string
}

interface FormState {
  id?: number
  studentId?: number
  courseId?: number
  semester?: string
  score?: number
}

const router = useRouter()

const columns = [
  {
    prop: 'studentNo',
    label: '学号'
  },
  {
    prop: 'studentName',
    label: '姓名'
  },
  {
    prop: 'courseName',
    label: '课程'
  },
  {
    prop: 'semester',
    label: '学期'
  },
  {
    prop: 'score',
    label: '成绩'
  }
]

const searchForm = reactive<SearchForm>({
  semester: undefined,
  courseId: undefined,
  studentNo: undefined
})

const formState = reactive<FormState>({
  id: undefined,
  studentId: undefined,
  courseId: undefined,
  semester: undefined,
  score: undefined
})

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }],
  score: [{ required: true, message: '请输入成绩', trigger: 'blur' }]
}

const grades = ref<Grade[]>([])
const students = ref<Student[]>([])
const courses = ref<Course[]>([])
const loading = ref(false)
const studentsLoading = ref(false)
const coursesLoading = ref(false)
const modalVisible = ref(false)
const modalTitle = ref('录入成绩')
const formRef = ref()

// 添加分页相关变量
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 添加选中的成绩列表
const selectedGrades = ref<Grade[]>([])

// 添加学期列表状态
const semesters = ref<string[]>([])

// 添加上传请求头
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// 添加新的状态变量
const filteredStudents = ref<Student[]>([])
const filteredCourses = ref<Course[]>([])

const fetchGrades = async () => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token) {
    console.error('未找到token，请先登录')
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    let response
    if (searchForm.studentNo) {
      // 如果输入了学号，使用学号查询接口
      response = await request.get(`/api/grades/studentNo/${searchForm.studentNo}`)
      grades.value = response.data
      // 根据其他搜索条件过滤
      if (searchForm.courseId) {
        grades.value = grades.value.filter((grade: Grade) => grade.courseId === searchForm.courseId)
      }
      if (searchForm.semester) {
        grades.value = grades.value.filter((grade: Grade) => grade.semester === searchForm.semester)
      }
      pagination.total = grades.value.length
    } else {
      // 使用分页查询接口，同时传递所有搜索条件
      response = await request.get('/api/grades', {
        params: {
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize,
          semester: searchForm.semester,
          courseId: searchForm.courseId
        }
      })
      pagination.total = response.data.total
      grades.value = response.data.list
    }
  } catch (error: any) {
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取成绩列表失败')
    }
  } finally {
    loading.value = false
  }
}

const fetchStudents = async () => {
  studentsLoading.value = true
  try {
    const response = await request.get('/api/student/list', {
      params: {
        pageNum: 1,
        pageSize: 1000  // 获取足够多的学生数据
      }
    })
    students.value = response.data.list
  } catch (error: any) {
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取学生列表失败')
    }
  } finally {
    studentsLoading.value = false
  }
}

const fetchCourses = async () => {
  coursesLoading.value = true
  try {
    const response = await request.get('/api/course/list', {
      params: {
        pageNum: 1,
        pageSize: 1000  // 获取足够多的课程数据
      }
    })
    courses.value = response.data.list
  } catch (error: any) {
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取课程列表失败')
    }
  } finally {
    coursesLoading.value = false
  }
}

const handleSearch = async () => {
  pagination.currentPage = 1 // 重置页码
  await fetchGrades()
}

const showAddModal = () => {
  modalTitle.value = '录入成绩'
  Object.assign(formState, {
    id: undefined,
    studentId: undefined,
    courseId: searchForm.courseId,
    semester: searchForm.semester,
    score: undefined
  })
  modalVisible.value = true
}

const showEditModal = (record: Grade) => {
  modalTitle.value = '编辑成绩'
  Object.assign(formState, record)
  modalVisible.value = true
}

const handleModalOk = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = {
      id: formState.id,
      studentId: formState.studentId,
      courseId: formState.courseId,
      semester: formState.semester,
      score: formState.score
    }
    
    if (formState.id) {
      await request.put(`/api/grades/${formState.id}`, submitData)
      ElMessage.success('更新成绩成功')
    } else {
      await request.post('/api/grades', submitData)
      ElMessage.success('录入成绩成功')
    }
    modalVisible.value = false
    fetchGrades()
  } catch (error: any) {
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

// 添加分页处理函数
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  fetchGrades()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchGrades()
}

// 处理表格选择变化
const handleSelectionChange = (selection: Grade[]) => {
  selectedGrades.value = selection
}

// 重置搜索
const resetSearch = () => {
  searchForm.semester = undefined
  searchForm.courseId = undefined
  searchForm.studentNo = undefined
  pagination.currentPage = 1
  handleSearch()
}

// 处理删除
const handleDelete = async (row: Grade) => {
  try {
    await ElMessageBox.confirm('确定要删除这条成绩记录吗？', '提示', {
      type: 'warning'
    })
    
    // 使用批量删除接口，传入单个ID
    await request.delete('/api/grades/batch', { 
      data: { ids: [row.id] }
    })
    ElMessage.success('删除成功')
    fetchGrades()
  } catch (error: any) {
    if (error === 'cancel') return
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 处理批量删除
const handleBatchDelete = async () => {
  if (!selectedGrades.value.length) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedGrades.value.length} 条成绩记录吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const ids = selectedGrades.value.map(grade => grade.id)
    await request.delete('/api/grades/batch', { data: { ids } })
    ElMessage.success('批量删除成功')
    fetchGrades()
  } catch (error: any) {
    if (error === 'cancel') return
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '批量删除失败')
    }
  }
}

// 处理导出
const handleExport = async () => {
  const loadingInstance = ElMessage({
    message: '正在导出数据，请稍候...',
    duration: 0,
    type: 'info'
  })
  
  try {
    const response = await request.get('/api/grades/export', {
      responseType: 'blob',
      params: {
        semester: searchForm.semester,
        courseId: searchForm.courseId
      },
      timeout: 30000 // 设置30秒超时
    })
    
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '成绩列表.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    loadingInstance.close()
    ElMessage.success('导出成功')
  } catch (error: any) {
    loadingInstance.close()
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('导出超时，请稍后重试或减少导出数据量')
    } else if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '导出失败')
    }
  }
}

// 处理导入成功
const handleImportSuccess = (response: any) => {
  if (response.code === 200) {
    ElMessage.success('导入成功')
    fetchGrades()
  } else {
    ElMessage.error(response.message || '导入失败')
  }
}

// 处理导入失败
const handleImportError = (error: any) => {
  if (error.response?.status === 401 || error.response?.status === 403) {
    ElMessage.error('登录已过期，请重新登录')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  } else {
    ElMessage.error(error.response?.data?.message || '导入失败')
  }
}

// 添加获取学期列表的方法
const fetchSemesters = async () => {
  try {
    const response = await request.get('/api/grades/semesters')
    semesters.value = response.data
  } catch (error: any) {
    if (error.response?.status === 401 || error.response?.status === 403) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取学期列表失败')
    }
  }
}

// 添加学期格式化方法
const formatSemester = (semester: string) => {
  const [year, term] = semester.split('-')
  const termText = term === '1' ? '第一学期' : '第二学期'
  return `${year}学年${termText}`
}

// 添加过滤方法
const filterStudents = async (query: string) => {
  if (query) {
    filteredStudents.value = students.value.filter(student => 
      student.name.includes(query) || student.studentNo.includes(query)
    )
  } else {
    filteredStudents.value = students.value.slice(0, 100) // 默认显示前100条
  }
}

const filterCourses = async (query: string) => {
  if (query) {
    filteredCourses.value = courses.value.filter(course => 
      course.courseName.includes(query) || course.courseCode.includes(query)
    )
  } else {
    filteredCourses.value = courses.value.slice(0, 100) // 默认显示前100条
  }
}

onMounted(() => {
  fetchStudents()
  fetchCourses()
  fetchGrades()
  fetchSemesters()
})
</script>

<style scoped>
.grade-management {
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

.card-header .right {
  display: flex;
  gap: 10px;
}

.table-operations {
  padding: 10px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-operations .right {
  display: flex;
  gap: 10px;
}

.search-form {
  display: flex;
  align-items: center;
  margin-bottom: 0;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.upload-demo {
  display: inline-block;
}
</style> 