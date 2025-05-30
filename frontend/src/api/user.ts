import request from '@/utils/request'

export interface UserDTO {
  id?: number
  username: string
  password?: string
  realName: string
  role: 'ADMIN' | 'TEACHER'
  createTime?: string
}

export interface UserQuery {
  username?: string
  realName?: string
  role?: 'ADMIN' | 'TEACHER'
  pageNum: number
  pageSize: number
}

// 获取用户列表
export function listUsers(params: UserQuery) {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data: UserDTO) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id: number, data: UserDTO) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id: number) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

// 获取用户详情
export function getUserById(id: number) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
} 