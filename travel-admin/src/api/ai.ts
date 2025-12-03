import request from '@/utils/request'

// AI API接口

export interface AiGenerateLog {
  id?: number
  userId: number
  requestContent: string
  responseContent?: string
  generateType?: number
  costTokens?: number
  status: number
  createTime?: string
}

export interface AiLogListParams {
  page?: number
  size?: number
  userId?: number
  status?: number
}

export interface AiLogListResponse {
  list: AiGenerateLog[]
  total: number
  page: number
  size: number
}

export interface AiStatistics {
  totalRequests: number
  successRequests: number
  failedRequests: number
  successRate: number
  totalTokensUsed: number
  activeUsers: number
}

// 获取AI日志列表
export function getAiLogList(params: AiLogListParams) {
  return request.get<AiLogListResponse>('/ai/logs', { params })
}

// 获取AI统计信息
export function getAiStatistics() {
  return request.get<AiStatistics>('/ai/statistics')
}

// 生成旅游攻略
export function generateTravelPlan(params: {
  destination: string
  days: number
  budget: string
  interests: string
  travelStyle: string
}) {
  return request.post<any>('/ai/generate-plan', params)
}

// AI智能问答
export function aiChat(question: string) {
  return request.post<any>('/ai/chat', { question })
}

// 获取用户AI统计
export function getUserAiStats(userId: number) {
  return request.get<any>(`/ai/user/${userId}/stats`)
}

// 生成AI内容
export function generateContent(userId: number, prompt: string, modelName: string = 'gpt-3.5-turbo') {
  return request.post<string>('/ai/generate', null, {
    params: { userId, request: prompt, modelName }
  })
}

// 清理过期日志
export function cleanExpiredLogs(days: number = 30) {
  return request.post<string>('/ai/clean', null, {
    params: { days }
  })
}

// 删除AI日志
export function deleteAiLog(id: number) {
  return request.delete<string>(`/ai/logs/${id}`)
}

// 管理员专用API

// 获取所有AI日志列表（管理员）
export function getAdminAiLogList(params: AiLogListParams) {
  return request.get<AiLogListResponse>('/ai/admin/logs', { params })
}

// 获取所有AI统计信息（管理员）
export function getAdminAiStatistics() {
  return request.get<AiStatistics>('/ai/admin/statistics')
}









