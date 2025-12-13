import request from '@/utils/request'

// 农家乐服务API接口

export interface CultureService {
  id?: number
  title: string
  location: string
  rating?: number
  views?: number
  description?: string
  images?: string[]
  features?: string[]
  facilities?: Array<{ name: string; icon?: string }>
  packages?: Array<{
    id?: number
    name: string
    price: number
    unit: string
    description?: string
    includes?: string[]
  }>
  contactPhone?: string
  status?: number // 0-已关闭 1-正常 2-维护中
  createTime?: string
  updateTime?: string
}

export interface ServiceListParams {
  page?: number
  size?: number
  keyword?: string
  location?: string
  status?: number
}

export interface ServiceListResponse {
  list: CultureService[]
  total: number
  page: number
  size: number
}

// 获取农家乐服务列表（管理端）
export function getServiceList(params: ServiceListParams) {
  return request.get<ServiceListResponse>('/admin/culture/services/page', { params })
}

// 获取农家乐服务详情（管理端）
export function getServiceById(id: number) {
  return request.get<CultureService>(`/admin/culture/services/${id}`)
}

// 创建农家乐服务（管理端）
export function createService(data: CultureService) {
  return request.post<string>('/admin/culture/services', data)
}

// 更新农家乐服务（管理端）
export function updateService(id: number, data: CultureService) {
  return request.put<string>(`/admin/culture/services/${id}`, data)
}

// 删除农家乐服务（管理端）
export function deleteService(id: number) {
  return request.delete<string>(`/admin/culture/services/${id}`)
}

// 切换农家乐服务状态（管理端）
export function toggleServiceStatus(id: number, status: number) {
  return request.put<string>(`/admin/culture/services/${id}/status`, { status })
}

// 获取农家乐服务统计（管理端）
export function getServiceStatistics() {
  return request.get<any>('/admin/culture/services/stats')
}











