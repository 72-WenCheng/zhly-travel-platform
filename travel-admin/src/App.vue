<template>
  <div id="app">
    <router-view />

    <el-dialog
      v-model="maintenanceModalVisible"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      width="420px"
      class="maintenance-countdown-dialog"
    >
      <div class="maintenance-dialog-body">
        <el-icon class="maintenance-dialog-icon"><WarningFilled /></el-icon>
        <p class="maintenance-dialog-title">系统维护即将开始</p>
        <p class="maintenance-dialog-message">{{ maintenanceMessage }}</p>
        <p class="maintenance-dialog-countdown">将在 {{ maintenanceCountdown }} 秒后自动退出</p>
        <el-button type="primary" @click="forceLogoutForMaintenance">立即退出</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'

const userStore = useUserStore()
const systemStore = useSystemStore()
const router = useRouter()
const { maintenanceMode, maintenanceMessage } = storeToRefs(systemStore)

const maintenanceModalVisible = ref(false)
const maintenanceCountdown = ref(10)
const maintenanceLogoutDelay = 10
let maintenanceCountdownTimer: number | null = null
let maintenancePromptShown = false

const startMaintenanceCountdown = () => {
  if (!userStore.isLoggedIn || userStore.userInfo?.role !== 2) return
  if (maintenancePromptShown) return
  maintenancePromptShown = true
  maintenanceCountdown.value = maintenanceLogoutDelay
  maintenanceModalVisible.value = true
  if (maintenanceCountdownTimer) {
    clearInterval(maintenanceCountdownTimer)
    maintenanceCountdownTimer = null
  }
  maintenanceCountdownTimer = window.setInterval(() => {
    maintenanceCountdown.value -= 1
    if (maintenanceCountdown.value <= 0) {
      forceLogoutForMaintenance()
    }
  }, 1000)
}

const clearMaintenanceCountdown = () => {
  if (maintenanceCountdownTimer) {
    clearInterval(maintenanceCountdownTimer)
    maintenanceCountdownTimer = null
  }
  maintenanceModalVisible.value = false
  maintenancePromptShown = false
}

const forceLogoutForMaintenance = async () => {
  clearMaintenanceCountdown()
  if (userStore.isLoggedIn) {
    await userStore.logout()
  }
  ElMessage.warning('系统维护中，已自动退出')
  router.push('/maintenance')
}

// 标记是否已经发送过退出请求（避免重复发送）
let logoutRequestSent = false

// 发送退出登录请求（使用 fetch with keepalive，适合在页面卸载时使用）
const sendLogoutRequest = () => {
  // 如果已经发送过，或者没有token，直接返回
  if (logoutRequestSent || !userStore.token) {
    return
  }
  
  try {
    const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
    const url = `${baseURL}/user/logout`
    const token = userStore.token
    
    // 使用 fetch with keepalive，确保请求在页面关闭后也能完成
    // keepalive 选项告诉浏览器在页面关闭后也要完成这个请求
    fetch(url, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      keepalive: true, // 关键：确保请求在页面关闭后也能完成
      body: JSON.stringify({})
    }).catch(() => {
      // 静默处理错误，因为页面可能已经关闭
      // 即使失败，浏览器也会尝试完成请求
    })
    
    logoutRequestSent = true
    console.log('✅ 窗口关闭时已发送退出登录请求')
  } catch (error) {
    console.error('发送退出登录请求失败:', error)
  }
}

// 处理窗口关闭事件
const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  // 只有在用户已登录且不是手动退出时才发送退出请求
  // 如果是手动退出，userStore.logout() 已经发送过请求了
  if (userStore.isLoggedIn && userStore.token && !userStore.isManualLogout) {
    sendLogoutRequest()
  }
}

// 处理页面可见性变化（当用户切换到其他标签页或最小化窗口时）
const handleVisibilityChange = () => {
  // 当页面隐藏时，可以考虑发送心跳或标记离线
  // 但这里我们只在窗口关闭时处理，因为切换标签页不应该算退出
  if (document.hidden) {
    // 页面隐藏，但不发送退出请求（因为用户可能只是切换标签页）
    // 如果需要，可以在这里实现心跳机制
  }
}

onMounted(() => {
  // 初始化用户信息
  userStore.initUser()
  // 预加载系统配置，供全局使用
  systemStore.fetchConfig().catch((error) => {
    console.error('预加载系统配置失败:', error)
  })
  systemStore.startAutoRefresh()
  
  // 监听窗口关闭事件
  window.addEventListener('beforeunload', handleBeforeUnload)
  
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onBeforeUnmount(() => {
  // 移除事件监听器
  window.removeEventListener('beforeunload', handleBeforeUnload)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  clearMaintenanceCountdown()
  systemStore.stopAutoRefresh()
})

watch(maintenanceMode, (value, oldValue) => {
  if (value && !oldValue) {
    startMaintenanceCountdown()
  } else if (!value && oldValue) {
    clearMaintenanceCountdown()
  }
})

watch(
  () => userStore.isLoggedIn,
  (loggedIn) => {
    if (!loggedIn) {
      clearMaintenanceCountdown()
    }
  }
)
</script>

<style>
#app {
  height: 100vh;
  width: 100vw;
}
.maintenance-countdown-dialog .el-dialog__header {
  display: none;
}
.maintenance-dialog-body {
  text-align: center;
  padding: 12px 8px 4px;
}
.maintenance-dialog-icon {
  font-size: 48px;
  color: #f97316;
}
.maintenance-dialog-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 12px 0 8px;
}
.maintenance-dialog-message {
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 12px;
}
.maintenance-dialog-countdown {
  color: #7c3aed;
  font-weight: 600;
  margin-bottom: 18px;
}
.maintenance-dialog-body .el-button {
  padding: 10px 32px;
}
</style>

