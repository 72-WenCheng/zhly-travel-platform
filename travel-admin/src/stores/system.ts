import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { SystemConfigData } from '@/api/systemConfig'
import { getPublicSystemConfig } from '@/api/systemConfig'

const DEFAULT_NAME = '智慧生态旅游系统'
const DEFAULT_DESCRIPTION = '智慧生态旅游系统 © 2024 版权所有 | 技术支持：智慧生态科技有限公司'
const DEFAULT_EMAIL = 'admin@example.com'
const DEFAULT_PHONE = '400-123-4567'
const DEFAULT_TIME = '工作日 9:00-18:00'
const DEFAULT_MAINTENANCE_MESSAGE = '系统维护中，请稍后再试'

export const useSystemStore = defineStore('system', () => {
  const config = ref<SystemConfigData | null>(null)
  const loading = ref(false)
  const loaded = ref(false)
  let refreshTimer: number | null = null

  const normalizeConfig = (data: SystemConfigData | null): SystemConfigData | null => {
    if (!data) return null
    return {
      ...data,
      systemName: data.systemName || data.adminPlatformName || DEFAULT_NAME,
      adminPlatformName: data.adminPlatformName || data.systemName || DEFAULT_NAME,
      userPlatformName: data.userPlatformName || DEFAULT_NAME,
      systemDescription: data.systemDescription || DEFAULT_DESCRIPTION,
      adminEmail: data.adminEmail || data.contactEmail || DEFAULT_EMAIL,
      contactEmail: data.contactEmail || data.adminEmail || DEFAULT_EMAIL,
      servicePhone: data.servicePhone || data.contactPhone || DEFAULT_PHONE,
      contactPhone: data.contactPhone || data.servicePhone || DEFAULT_PHONE,
      contactTime: data.contactTime || data.serviceHours || DEFAULT_TIME,
      serviceHours: data.serviceHours || data.contactTime || DEFAULT_TIME,
      maintenanceMode: data.maintenanceMode === true || data.maintenanceMode === 'true',
      maintenanceMessage: data.maintenanceMessage || DEFAULT_MAINTENANCE_MESSAGE
    }
  }

  const fetchConfig = async (force = false) => {
    if (loading.value) return config.value
    if (!force && loaded.value && config.value) return config.value
    loading.value = true
    try {
      const result = await getPublicSystemConfig()
      config.value = normalizeConfig(result.data)
      loaded.value = true
      return config.value
    } catch (error) {
      console.error('获取系统配置失败:', error)
      return config.value
    } finally {
      loading.value = false
    }
  }

  const adminPlatformName = computed(() => config.value?.adminPlatformName || DEFAULT_NAME)
  const userPlatformName = computed(() => config.value?.userPlatformName || DEFAULT_NAME)
  const footerDescription = computed(() => config.value?.systemDescription || DEFAULT_DESCRIPTION)
  const systemVersion = computed(() => config.value?.systemVersion || '1.0.0')
  const contactEmail = computed(() => config.value?.contactEmail || DEFAULT_EMAIL)
  const contactPhone = computed(() => config.value?.contactPhone || DEFAULT_PHONE)
  const contactTime = computed(() => config.value?.contactTime || DEFAULT_TIME)

  const contactMessage = computed(() => {
    return `联系我们：电子邮箱 ${contactEmail.value} | 客服热线：${contactPhone.value}${contactTime.value ? `（${contactTime.value}）` : ''}`
  })

  const aboutMessage = computed(() => {
    return `关于我们：${footerDescription.value}`
  })

  const maintenanceMode = computed(() => config.value?.maintenanceMode === true || config.value?.maintenanceMode === 'true')
  const maintenanceMessage = computed(() => config.value?.maintenanceMessage || DEFAULT_MAINTENANCE_MESSAGE)

  const startAutoRefresh = (interval = 2000) => {
    if (refreshTimer) return
    refreshTimer = window.setInterval(() => {
      fetchConfig(true)
    }, interval)
  }

  const stopAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  }

  return {
    config,
    loading,
    loaded,
    fetchConfig,
    adminPlatformName,
    userPlatformName,
    footerDescription,
    systemVersion,
    contactEmail,
    contactPhone,
    contactTime,
    contactMessage,
    aboutMessage,
    maintenanceMode,
    maintenanceMessage,
    startAutoRefresh,
    stopAutoRefresh
  }
})

