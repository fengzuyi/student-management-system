import request from '@/utils/request'

export interface Course {
  id?: number
  courseCode: string
  courseName: string
  credits: number
  createTime?: string
  updateTime?: string
}

export interface CourseQuery {
  courseCode?: string
  courseName?: string
  pageNum: number
  pageSize: number
}

export function getCourseList(params: CourseQuery) {
  return request({
    url: '/api/course/list',
    method: 'get',
    params
  })
}

export function getCourseById(id: number) {
  return request({
    url: `/api/course/${id}`,
    method: 'get'
  })
}

export function addCourse(data: Course) {
  return request({
    url: '/api/course',
    method: 'post',
    data
  })
}

export function updateCourse(data: Course) {
  return request({
    url: '/api/course',
    method: 'put',
    data
  })
}

export function deleteCourse(ids: number[]) {
  return request({
    url: `/api/course/${ids.join(',')}`,
    method: 'delete'
  })
} 