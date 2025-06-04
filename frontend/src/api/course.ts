import request from '@/utils/request'

/**
 * 课程信息接口
 */
export interface Course {
  /** 课程ID */
  id?: number
  /** 课程代码 */
  courseCode: string
  /** 课程名称 */
  courseName: string
  /** 学分 */
  credits: number
  /** 创建时间 */
  createTime?: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 课程查询参数接口
 */
export interface CourseQuery {
  /** 课程代码 */
  courseCode?: string
  /** 课程名称 */
  courseName?: string
  /** 页码 */
  pageNum: number
  /** 每页数量 */
  pageSize: number
}

/**
 * 获取课程列表
 * @param params 查询参数
 * @returns 课程列表数据
 */
export function getCourseList(params: CourseQuery) {
  return request({
    url: '/api/course/list',
    method: 'get',
    params
  })
}

/**
 * 创建课程
 * @param data 课程信息
 * @returns 创建结果
 */
export function createCourse(data: Course) {
  return request({
    url: '/api/course',
    method: 'post',
    data
  })
}

/**
 * 更新课程
 * @param data 课程信息
 * @returns 更新结果
 */
export function updateCourse(data: Course) {
  return request({
    url: '/api/course',
    method: 'put',
    data
  })
}

/**
 * 删除课程
 * @param ids 课程ID数组
 * @returns 删除结果
 */
export function deleteCourse(ids: number[]) {
  return request({
    url: `/api/course/${ids.join(',')}`,
    method: 'delete'
  })
}

/**
 * 根据课程代码查询课程
 * @param courseCode 课程代码
 * @returns 课程信息
 */
export function getCourseByCode(courseCode: string) {
  return request({
    url: `/api/course/code/${courseCode}`,
    method: 'get'
  })
} 