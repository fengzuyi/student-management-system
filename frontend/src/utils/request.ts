import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 检查 token 是否过期
const isTokenExpired = (token: string) => {
  try {
    // 解析 JWT token
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    const { exp } = JSON.parse(jsonPayload)
    
    // 检查是否过期（提前 5 分钟判断过期）
    return exp * 1000 < Date.now() + 5 * 60 * 1000
  } catch (e) {
    return true
  }
}

// 获取 token 剩余有效期（小时）
const getTokenRemainingTime = (token: string): number => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    const { exp } = JSON.parse(jsonPayload)
    const remainingTime = exp * 1000 - Date.now()
    return Math.floor(remainingTime / (60 * 60 * 1000))
  } catch (e) {
    return 0
  }
}

const service = axios.create({
  baseURL: '',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      // 检查 token 是否过期
      if (isTokenExpired(token)) {
        console.log('Token已过期，清除登录信息')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        return Promise.reject(new Error('Token expired'))
      }
      
      // 在请求头中添加 token
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('请求头设置完成:', config.headers)
    } else {
      console.log('未找到token，请求头未设置Authorization')
    }
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是二进制数据，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    console.log('收到响应:', response)
    const res = response.data
    if (res.code !== 200) {
      console.error('请求失败:', res)
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    console.error('响应拦截器错误:', error)
    if (error.response?.status === 401) {
      console.log('401未授权，清除登录信息')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      console.error('请求错误:', error.response?.data || error.message)
      ElMessage.error(error.response?.data?.message || error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default service 