import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 是否已经弹出了登录过期提示，避免重复弹窗
let isSessionExpired = false

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 20000,  // 增加到20秒，与后端保持一致
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 优先从sessionStorage获取token（当前标签页的用户身份）
    // 如果sessionStorage没有，再从localStorage获取（向后兼容）
    let token = sessionStorage.getItem('travel_token')
    if (!token) {
      token = localStorage.getItem('travel_token')
    }
    
    // 添加token到请求头
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  async (response: AxiosResponse) => {
    const { data } = response
    
    // 如果返回的是Result格式
    if (data && typeof data === 'object' && 'code' in data) {
      // 业务级别的未登录 / 会话超时
      if (data.code === 401 || data.code === 403) {
        if (!isSessionExpired) {
          isSessionExpired = true
          try {
            await ElMessageBox.confirm(
              data.message || '登录状态已失效，请重新登录',
              '提示',
              {
                type: 'warning',
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                closeOnClickModal: false,
                closeOnPressEscape: false,
              }
            )
          } catch {
            // 用户点了取消或关闭，对行为不做强制
          }
          const userStore = useUserStore()
          userStore.logout()
          window.location.href = '/'
          isSessionExpired = false
        }
        return Promise.reject(new Error(data.message || '登录状态已失效'))
      }

      if (data.code === 200) {
        // 返回data字段的内容，保持原有的Result结构
        return data
      } else {
        ElMessage.error(data.message || '请求失败')
        return Promise.reject(new Error(data.message || '请求失败'))
      }
    }
    
    return data
  },
  (error) => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401: {
          // HTTP 级别未授权，也按会话超时处理
          if (!isSessionExpired) {
            isSessionExpired = true
            ElMessageBox.confirm(
              (data && (data.message || data.msg)) || '登录状态已失效，请重新登录',
              '提示',
              {
                type: 'warning',
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                closeOnClickModal: false,
                closeOnPressEscape: false,
              }
            ).finally(() => {
              const userStore = useUserStore()
              userStore.logout()
              window.location.href = '/'
              isSessionExpired = false
            })
          }
          break
        }
        case 403:
          ElMessage.error('禁止访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default request









