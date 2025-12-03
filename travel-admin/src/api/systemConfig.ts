import request from '@/utils/request'

// 系统配置API接口

export interface SystemConfig {
  id?: number
  configKey: string
  configValue: string
  configType?: string
  description?: string
  createTime?: string
  updateTime?: string
}

export interface AiConfig {
  modelName: string
  apiUrl: string
  apiKey: string
  maxTokens: number
  temperature: number
  enabled: boolean
}

export interface SystemConfigData {
  systemName: string
  adminPlatformName: string
  userPlatformName: string
  systemDescription: string
  systemVersion: string
  adminEmail: string
  contactEmail?: string
  servicePhone: string
  contactPhone?: string
  contactTime?: string
  serviceHours?: string
  maintenanceMode: boolean | string
  maintenanceMessage?: string
}

// 获取配置值
export function getConfigValue(configKey: string) {
  return request.get<string>(`/system-config/value/${configKey}`)
}

// 设置配置值
export function setConfigValue(configKey: string, configValue: string) {
  return request.post<string>('/system-config/set', null, {
    params: { configKey, configValue }
  })
}

// 获取配置列表
export function getConfigList(configType?: string) {
  return request.get<SystemConfig[]>('/system-config/list', {
    params: { configType }
  })
}

// 批量更新配置
export function batchUpdateConfigs(configs: Record<string, string>) {
  return request.post<string>('/system-config/batch-update', configs)
}

// 获取AI配置
export function getAiConfig() {
  return request.get<AiConfig>('/system-config/ai')
}

// 更新AI配置
export function updateAiConfig(aiConfig: AiConfig) {
  return request.post<string>('/system-config/ai', aiConfig)
}

// 获取系统配置
export function getSystemConfig() {
  return request.get<SystemConfigData>('/admin/system-config/system')
}

// 获取公开系统配置（用户端展示使用）
export function getPublicSystemConfig() {
  return request.get<SystemConfigData>('/system-config/system')
}

// 更新系统配置
export function updateSystemConfig(systemConfig: SystemConfigData) {
  return request.post<string>('/admin/system-config/system', systemConfig)
}

// 获取缓存配置
export function getCacheConfig() {
  return request.get<Record<string, any>>('/admin/system-config/cache')
}

// 更新缓存配置
export function updateCacheConfig(cacheConfig: Record<string, any>) {
  return request.post<string>('/admin/system-config/cache', cacheConfig)
}

// 获取安全配置
export function getSecurityConfig() {
  return request.get<Record<string, any>>('/admin/system-config/security')
}

// 更新安全配置
export function updateSecurityConfig(securityConfig: Record<string, any>) {
  return request.post<string>('/admin/system-config/security', securityConfig)
}

// 获取公开安全配置（前台提示用）
export function getPublicSecurityConfig() {
  return request.get<Record<string, any>>('/system-config/security')
}













