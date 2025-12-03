import axios from 'axios'

const API_BASE_URL = 'http://localhost:8070/api'

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 添加token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      // 清除本地存储的token
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 跳转到登录页面
      window.location.href = '/'
    }
    return Promise.reject(error)
  }
)

// 认证相关API
export const authAPI = {
  // 用户登录
  login: (data: { username: string; password: string; loginType?: string }) => {
    return api.post('/auth/login', data)
  },

  // 用户注册
  register: (data: {
    username: string
    email: string
    password: string
    phone?: string
    inviteCode?: string
  }) => {
    return api.post('/auth/register', data)
  },

  // 发送验证码
  sendCaptcha: (email: string) => {
    return api.post('/auth/send-captcha', { email })
  },

  // 忘记密码
  forgotPassword: (data: { email: string; captcha: string }) => {
    return api.post('/auth/forgot-password', data)
  },

  // 重置密码
  resetPassword: (data: { token: string; newPassword: string }) => {
    return api.post('/auth/reset-password', data)
  },

  // 修改密码
  changePassword: (data: {
    userId: number
    oldPassword: string
    newPassword: string
  }) => {
    return api.post('/auth/change-password', data)
  },

  // 用户登出
  logout: (userId: number) => {
    return api.post('/auth/logout', { userId })
  },

  // 发送手机验证码
  sendPhoneCaptcha: (data: { phone: string }) => {
    return api.post('/auth/send-phone-captcha', data)
  },

  // 手机号注册
  registerByPhone: (data: {
    phone: string
    captcha: string
    password: string
    inviteCode?: string
  }) => {
    return api.post('/auth/register/phone', data)
  },

  // 手机号登录
  loginByPhone: (data: {
    phone: string
    captcha: string
    loginType?: string
  }) => {
    return api.post('/auth/login/phone', data)
  }
}

export default api

