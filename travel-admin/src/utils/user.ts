import { useUserStore } from '@/stores/user'

/**
 * 获取当前标签页的用户信息
 * 优先级：userStore > sessionStorage > localStorage
 * 确保多标签页环境下每个标签页使用正确的用户身份
 */
export function getCurrentUserInfo() {
  const userStore = useUserStore()
  
  // 1. 优先从 userStore 获取（最快）
  if (userStore.userInfo) {
    return userStore.userInfo
  }
  
  // 2. 从 sessionStorage 获取（当前标签页独立）
  try {
    const sessionUserInfo = sessionStorage.getItem('travel_user_info')
    if (sessionUserInfo) {
      return JSON.parse(sessionUserInfo)
    }
  } catch (e) {
    console.warn('从 sessionStorage 获取用户信息失败:', e)
  }
  
  // 3. 从 localStorage 获取（向后兼容）
  try {
    const localUserInfo = localStorage.getItem('travel_user_info')
    if (localUserInfo) {
      return JSON.parse(localUserInfo)
    }
  } catch (e) {
    console.warn('从 localStorage 获取用户信息失败:', e)
  }
  
  return null
}

/**
 * 获取当前标签页的用户ID
 */
export function getCurrentUserId(): number | null {
  const userInfo = getCurrentUserInfo()
  return userInfo?.id || userInfo?.userId || null
}











































































