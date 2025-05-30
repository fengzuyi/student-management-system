<template>
  <div class="grade-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
        </div>
      </template>
      
      <!-- 搜索和操作区域 -->
      <div class="table-operations">
        <el-space>
          <el-select
            v-model="searchForm.semester"
            style="width: 200px"
            placeholder="选择学期"
            @change="handleSearch"
          >
            <el-option value="2021-2022-1" label="2021-2022学年第一学期" />
            <el-option value="2021-2022-2" label="2021-2022学年第二学期" />
            <el-option value="2022-2023-1" label="2022-2023学年第一学期" />
            <el-option value="2022-2023-2" label="2022-2023学年第二学期" />
          </el-select>
          <el-select
            v-model="searchForm.courseId"
            style="width: 200px"
            placeholder="选择课程"
            @change="handleSearch"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
          <el-button type="primary" @click="showAddModal">录入成绩</el-button>
        </el-space>
      </div>

      <!-- 成绩统计卡片 -->
      <el-row :gutter="16" style="margin: 16px 0">
        <el-col :span="8">
          <el-card>
            <template #header>平均分</template>
            <div class="statistic-value">{{ statistics.average.toFixed(2) }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>最高分</template>
            <div class="statistic-value">{{ statistics.max.toFixed(2) }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>最低分</template>
            <div class="statistic-value">{{ statistics.min.toFixed(2) }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 成绩表格 -->
      <el-table
        :data="grades"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="score" label="成绩" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditModal(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

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
              :loading="studentsLoading"
              style="width: 100%"
            >
              <el-option
                v-for="student in students"
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
              :loading="coursesLoading"
              style="width: 100%"
            >
              <el-option
                v-for="course in courses"
                :key="course.id"
                :label="course.courseName"
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
              <el-option value="2021-2022-1" label="2021-2022学年第一学期" />
              <el-option value="2021-2022-2" label="2021-2022学年第二学期" />
              <el-option value="2022-2023-1" label="2022-2023学年第一学期" />
              <el-option value="2022-2023-2" label="2022-2023学年第二学期" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

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
}

interface FormState {
  id?: number
  studentId?: number
  courseId?: number
  semester?: string
  score?: number
}

interface Statistics {
  average: number
  max: number
  min: number
}

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
  courseId: undefined
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
const statistics = reactive<Statistics>({
  average: 0,
  max: 0,
  min: 0
})

const fetchGrades = async () => {
  if (!searchForm.courseId || !searchForm.semester) return
  
  loading.value = true
  try {
    const response = await axios.get(`/api/grades/course/${searchForm.courseId}`)
    grades.value = response.data
    await fetchStatistics()
  } catch (error) {
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  if (!searchForm.courseId || !searchForm.semester) return
  
  try {
    const response = await axios.get(
      `/api/grades/course/${searchForm.courseId}/semester/${searchForm.semester}/statistics`
    )
    Object.assign(statistics, response.data)
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

const fetchStudents = async () => {
  studentsLoading.value = true
  try {
    const response = await axios.get('/api/students')
    students.value = response.data
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    studentsLoading.value = false
  }
}

const fetchCourses = async () => {
  coursesLoading.value = true
  try {
    const response = await axios.get('/api/courses')
    courses.value = response.data
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    coursesLoading.value = false
  }
}

const handleSearch = () => {
  fetchGrades()
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
    if (formState.id) {
      await axios.put(`/api/grades/${formState.id}`, formState)
      ElMessage.success('更新成绩成功')
    } else {
      await axios.post('/api/grades', formState)
      ElMessage.success('录入成绩成功')
    }
    modalVisible.value = false
    fetchGrades()
  } catch (error: any) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('操作失败')
    }
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => {
  fetchStudents()
  fetchCourses()
})
</script>

<style scoped>
.grade-management {
  padding: 24px;
}

.table-operations {
  margin-bottom: 16px;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  color: var(--el-color-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style> 