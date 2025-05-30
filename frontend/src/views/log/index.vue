<template>
  <div class="log-container">
    <div class="search-wrapper">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作类型" class="search-item">
          <el-input
            v-model="searchForm.operationType"
            placeholder="请输入操作类型"
            clearable
            style="width: 120px"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户名" class="search-item">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 120px"
          ></el-input>
        </el-form-item>
        <el-form-item label="时间范围" class="search-item">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
            @change="handleDateRangeChange"
          ></el-date-picker>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
        <el-form-item class="search-item batch-delete">
          <el-button type="danger" :disabled="!selectedLogs.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-wrapper">
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="logList"
          @selection-change="handleSelectionChange"
          border
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="username" label="用户名" width="120"></el-table-column>
          <el-table-column prop="realName" label="真实姓名" width="120"></el-table-column>
          <el-table-column prop="operationType" label="操作类型" width="150"></el-table-column>
          <el-table-column prop="operationContent" label="操作内容"></el-table-column>
          <el-table-column prop="createTime" label="操作时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/format'

interface LogItem {
  id: number
  username: string
  realName: string
  operationType: string
  operationContent: string
  createTime: string
}

interface SearchForm {
  operationType: string
  username: string
  startTime: string
  endTime: string
}

const loading = ref(false)
const logList = ref<LogItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedLogs = ref<LogItem[]>([])
const dateRange = ref<string[]>([])

const searchForm = reactive<SearchForm>({
  operationType: '',
  username: '',
  startTime: '',
  endTime: ''
})

const fetchLogs = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    const response = await request.get('/api/logs/list', { params })
    const { list, total: totalCount } = response.data
    logList.value = list
    total.value = totalCount
  } catch (error) {
    ElMessage.error('获取日志列表失败')
    console.error('获取日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchLogs()
}

const resetSearch = () => {
  (Object.keys(searchForm) as Array<keyof SearchForm>).forEach(key => {
    searchForm[key] = ''
  })
  dateRange.value = []
  handleSearch()
}

const handleDateRangeChange = (val: string[] | null) => {
  if (val) {
    searchForm.startTime = val[0]
    searchForm.endTime = val[1]
  } else {
    searchForm.startTime = ''
    searchForm.endTime = ''
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchLogs()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchLogs()
}

const handleSelectionChange = (selection: LogItem[]) => {
  selectedLogs.value = selection
}

const handleDelete = async (row: LogItem) => {
  try {
    await ElMessageBox.confirm('确定要删除该日志记录吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/api/logs/${row.id}`)
    ElMessage.success('删除成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除日志失败:', error)
    }
  }
}

const handleBatchDelete = async () => {
  if (!selectedLogs.value.length) {
    ElMessage.warning('请选择要删除的日志')
    return
  }

  try {
    await ElMessageBox.confirm('确定要删除选中的日志记录吗？', '提示', {
      type: 'warning'
    })
    const ids = selectedLogs.value.map(log => log.id)
    await request.delete('/api/logs/batch', { data: { ids } })
    ElMessage.success('批量删除成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('批量删除日志失败:', error)
    }
  }
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.log-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.search-wrapper {
  background-color: #fff;
  padding: 16px;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  margin: 0;
}

.search-item {
  margin-right: 20px;
  margin-bottom: 0;
}

.search-item:last-child {
  margin-right: 0;
  margin-left: 0;
}

.search-item.batch-delete {
  margin-left: auto;
  margin-right: 0;
}

.search-form :deep(.el-form-item__label) {
  padding-right: 8px;
}

.search-form :deep(.el-form-item:last-child .el-button) {
  margin-left: 10px;
}

.search-form :deep(.el-form-item:last-child .el-button:first-child) {
  margin-left: 0;
}

.table-wrapper {
  flex: 1;
  background-color: #fff;
  margin-top: 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-container {
  flex: 1;
  padding: 16px 16px 0;
  overflow: auto;
}

.pagination-container {
  padding: 16px;
  background-color: #fff;
  border-top: 1px solid #f1f3f7;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

:deep(.el-table) {
  height: 100%;
}

:deep(.el-table__body-wrapper) {
  overflow-y: auto;
}
</style> 