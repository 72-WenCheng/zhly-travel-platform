// API 响应类型定义
export interface ApiResponse<T = any> {
  code: number
  message?: string
  data: T
}

// 分页响应类型
export interface PageResponse<T = any> {
  records: T[]
  total: number
  current: number
  size: number
}












