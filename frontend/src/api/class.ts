import request from '@/utils/request'

/**
 * 班级信息接口
 */
export interface ClassInfo {
  /** 班级ID */
  id?: number
  /** 班级名称 */
  className: string
  /** 年级 */
  grade: string
  /** 专业 */
  major: string
  /** 学生数量 */
  studentCount?: number
}

/**
 * 班级查询参数接口
 */
export interface ClassQuery {
  /** 班级名称 */
  className?: string
  /** 年级 */
  grade?: string
  /** 专业 */
  major?: string
  /** 页码 */
  pageNum: number
  /** 每页数量 */
  pageSize: number
}

/**
 * 获取班级列表
 * @param params 查询参数
 * @returns 班级列表数据
 */
export function getClassList(params: ClassQuery) {
  return request({
    url: '/api/class/list',
    method: 'get',
    params
  })
}

/**
 * 创建班级
 * @param data 班级信息
 * @returns 创建结果
 */
export function createClass(data: ClassInfo) {
  return request({
    url: '/api/class',
    method: 'post',
    data
  })
}

/**
 * 更新班级
 * @param data 班级信息
 * @returns 更新结果
 */
export function updateClass(data: ClassInfo) {
  return request({
    url: '/api/class',
    method: 'put',
    data
  })
}

/**
 * 删除班级
 * @param ids 班级ID数组
 * @returns 删除结果
 */
export function deleteClass(ids: number[]) {
  return request({
    url: `/api/class/${ids.join(',')}`,
    method: 'delete'
  })
}

/**
 * 导出班级信息
 * @returns Excel文件
 */
export function exportClass() {
  return request({
    url: '/api/class/export',
    method: 'get',
    responseType: 'blob'
  })
} 