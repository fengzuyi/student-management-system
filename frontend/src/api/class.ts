import request from '@/utils/request'

export interface ClassInfo {
  id?: number
  className: string
  grade: string
  major: string
  studentCount?: number
}

export interface ClassQuery {
  className?: string
  grade?: string
  major?: string
  pageNum: number
  pageSize: number
}

// 获取班级列表
export function getClassList(params: ClassQuery) {
  return request({
    url: '/api/class/list',
    method: 'get',
    params
  })
}

// 创建班级
export function createClass(data: ClassInfo) {
  return request({
    url: '/api/class',
    method: 'post',
    data
  })
}

// 更新班级
export function updateClass(data: ClassInfo) {
  return request({
    url: '/api/class',
    method: 'put',
    data
  })
}

// 删除班级
export function deleteClass(ids: number[]) {
  return request({
    url: `/api/class/${ids.join(',')}`,
    method: 'delete'
  })
}

// 导出班级信息
export function exportClass() {
  return request({
    url: '/api/class/export',
    method: 'get',
    responseType: 'blob'
  })
} 