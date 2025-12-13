import request from '@/utils/request'

// 特色民宿API接口

export interface Homestay {
  id?: number
  title: string
  location: string
  rating?: number
  views?: number
  price: number
  roomType?: string
  capacity?: number
  description?: string
  images?: string[]
  features?: string[]
  amenities?: string[]
  highlightTags?: string[]
  highlights?: string
  contactPhone?: string
  cover?: string
  status?: number // 0-已关闭 1-正常 2-维护中
  createTime?: string
  updateTime?: string
}

export interface HomestayListParams {
  page?: number
  size?: number
  keyword?: string
  location?: string
  status?: number
}

export interface HomestayListResponse {
  list: Homestay[]
  total: number
  page: number
  size: number
}

// 获取特色民宿列表（管理端）
export function getHomestayList(params: HomestayListParams) {
  return request.get<HomestayListResponse>('/admin/culture/homestays/page', { params })
}

// 获取特色民宿详情（管理端）
export function getHomestayById(id: number) {
  return request.get<Homestay>(`/admin/culture/homestays/${id}`)
}

// 创建特色民宿（管理端）
export function createHomestay(data: Homestay) {
  return request.post<string>('/admin/culture/homestays', data)
}

// 更新特色民宿（管理端）
export function updateHomestay(id: number, data: Homestay) {
  return request.put<string>(`/admin/culture/homestays/${id}`, data)
}

// 删除特色民宿（管理端）
export function deleteHomestay(id: number) {
  return request.delete<string>(`/admin/culture/homestays/${id}`)
}

// 切换特色民宿状态（管理端）
export function toggleHomestayStatus(id: number, status: number) {
  return request.put<string>(`/admin/culture/homestays/${id}/status`, { status })
}

// 获取特色民宿统计（管理端）
export function getHomestayStatistics() {
  return request.get<any>('/admin/culture/homestays/stats')
}








