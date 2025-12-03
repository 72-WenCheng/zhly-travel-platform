import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export interface User {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  gender: number
  age: number
  userType: number
  travelPreference: number
  interestTags: string
  frequentCities: string
  status: number
  lastLoginTime: string
  lastLoginIp: string
  createTime: string
  updateTime: string
  role?: number // 用户角色：1=管理员, 2=普通用户
}

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<User | null>(null)
  const isLoggedIn = ref<boolean>(false)
  const isManualLogout = ref<boolean>(false) // 标记是否为手动退出登录

  // 计算属性
  const nickname = computed(() => userInfo.value?.nickname || '游客')
  const avatar = computed(() => userInfo.value?.avatar || '/default-avatar.png')
  const userId = computed(() => userInfo.value?.id || 0)

  // 初始化用户信息
  const initUser = () => {
    try {
      // 优先从sessionStorage恢复（刷新时恢复）
      const sessionToken = sessionStorage.getItem('travel_token')
      const sessionUserInfo = sessionStorage.getItem('travel_user_info')
      
      if (sessionToken && sessionUserInfo) {
        try {
          token.value = sessionToken
          userInfo.value = JSON.parse(sessionUserInfo)
          isLoggedIn.value = true
          return
        } catch (e) {
          console.warn('从sessionStorage恢复用户信息失败:', e)
        }
      }
      
      // 如果sessionStorage没有，从localStorage恢复（兼容旧版本）
      const savedToken = localStorage.getItem('travel_token')
      const savedUserInfo = localStorage.getItem('travel_user_info')
      
      if (savedToken && savedUserInfo) {
        try {
          token.value = savedToken
          userInfo.value = JSON.parse(savedUserInfo)
          isLoggedIn.value = true
          
          // 同步到sessionStorage
          sessionStorage.setItem('travel_token', savedToken)
          sessionStorage.setItem('travel_user_info', savedUserInfo)
        } catch (e) {
          console.warn('从localStorage恢复用户信息失败:', e)
          clearUser()
        }
      }
    } catch (error) {
      console.error('初始化用户信息失败:', error)
      clearUser()
    }
  }

  // 设置用户信息
  const setUserInfo = (user: User) => {
    userInfo.value = user
    
    // 保存到sessionStorage（刷新时恢复）
    sessionStorage.setItem('travel_user_info', JSON.stringify(user))
    
    // 保存到localStorage（兼容旧版本）
    localStorage.setItem('travel_user_info', JSON.stringify(user))
  }

  // 设置token
  const setToken = (newToken: string) => {
    token.value = newToken
    
    // 保存到sessionStorage（刷新时恢复）
    sessionStorage.setItem('travel_token', newToken)
    
    // 保存到localStorage（兼容旧版本）
    localStorage.setItem('travel_token', newToken)
    isLoggedIn.value = true
  }
  
  // 登录（设置用户信息和token）
  const loginUser = (user: User, newToken: string) => {
    setUserInfo(user)
    setToken(newToken)
  }

  // 登录
  const login = async (username: string, password: string) => {
    try {
      const result = await request.post('/user/login', { username, password })
      
      setToken(result.data)
      
      // 获取用户信息
      const userResult = await request.get('/user/info')
      setUserInfo(userResult.data)
      
      return { success: true, message: '登录成功' }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, message: '网络错误，请稍后重试' }
    }
  }

  // 注册
  const register = async (userData: Partial<User> & { password: string }) => {
    try {
      await request.post('/user/register', userData)
      return { success: true, message: '注册成功' }
    } catch (error) {
      console.error('注册失败:', error)
      return { success: false, message: '网络错误，请稍后重试' }
    }
  }

  // 更新用户信息
  const updateUser = async (userData: Partial<User>) => {
    try {
      await request.put('/user/update', userData)
      
      if (userInfo.value) {
        Object.assign(userInfo.value, userData)
        setUserInfo(userInfo.value)
      }
      return { success: true, message: '更新成功' }
    } catch (error) {
      console.error('更新用户信息失败:', error)
      return { success: false, message: '网络错误，请稍后重试' }
    }
  }

  // 更新用户标签
  const updateUserTags = async (tags: string[]) => {
    try {
      await request.put('/user/tags', { userId: userId.value, tags })
      return { success: true, message: '标签更新成功' }
    } catch (error) {
      console.error('更新用户标签失败:', error)
      return { success: false, message: '网络错误，请稍后重试' }
    }
  }

  // 获取用户推荐
  const getUserRecommend = async () => {
    try {
      const result = await request.get(`/user/recommend/${userId.value}`)
      return { success: true, data: result.data }
    } catch (error) {
      console.error('获取用户推荐失败:', error)
      return { success: false, message: '网络错误，请稍后重试' }
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      // 标记为手动退出，避免窗口关闭时重复发送请求
      isManualLogout.value = true
      
      // 先调用后端退出登录API，标记用户下线
      if (token.value) {
        try {
          await request.post('/user/logout')
        } catch (error) {
          console.error('调用退出登录API失败:', error)
          // 即使API调用失败，也继续清除本地存储
        }
      }
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      // 无论API调用成功与否，都清除本地存储
      clearUser()
      
      // 强制清除路由缓存，确保退出后能正确跳转
      // 使用 nextTick 确保清除操作完成后再进行路由跳转
      await new Promise(resolve => setTimeout(resolve, 50))
    }
  }

  // 清除用户信息
  const clearUser = () => {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    
    // 清除sessionStorage
    sessionStorage.removeItem('travel_token')
    sessionStorage.removeItem('travel_user_info')
    
    // 清除localStorage
    localStorage.removeItem('travel_token')
    localStorage.removeItem('travel_user_info')
    
    // 清除多用户相关的旧数据（兼容旧版本）
    localStorage.removeItem('travel_user_sessions')
    localStorage.removeItem('travel_current_user_id')
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (key.startsWith('travel_user_full_')) {
        localStorage.removeItem(key)
      }
    })
    
    // 重置手动退出标记（延迟重置，确保窗口关闭事件能检测到）
    setTimeout(() => {
      isManualLogout.value = false
    }, 1000)
  }

  // 检查登录状态
  const checkLoginStatus = () => {
    return isLoggedIn.value && token.value
  }

  return {
    // 状态
    token,
    userInfo,
    isLoggedIn,
    isManualLogout,
    
    // 计算属性
    nickname,
    avatar,
    userId,
    
    // 方法
    initUser,
    setUserInfo,
    setToken,
    loginUser,
    login,
    register,
    updateUser,
    updateUserTags,
    getUserRecommend,
    logout,
    clearUser,
    checkLoginStatus
  }
})









