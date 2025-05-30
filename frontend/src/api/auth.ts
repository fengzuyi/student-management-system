import request from '@/utils/request'

interface ChangePasswordDTO {
  oldPassword: string
  newPassword: string
}

// 退出登录
export const logout = () => {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 修改密码
export const changePassword = (data: ChangePasswordDTO) => {
  return request({
    url: '/api/auth/change-password',
    method: 'post',
    data
  })
} 