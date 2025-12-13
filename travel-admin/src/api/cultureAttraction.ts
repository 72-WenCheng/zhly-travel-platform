import request from '@/utils/request'

// 特色周边管理API接口

export interface CultureAttraction {
  id?: number
  name: string
  city: string
  province?: string
  type: number
  image?: string
  images?: string | any
  coverImage?: string
  description?: string
  rating?: number
  score?: number
  ticketPrice?: number | string
  address?: string
  phone?: string
  website?: string
  openTime?: string
  openingHours?: string
  suggestedDuration?: number
  bestSeason?: string
  tags?: string | string[]
  features?: string
  transportation?: string
  nearbyFood?: string
  nearbyHotel?: string
  notes?: string
  longitude?: number
  latitude?: number
  status?: number
  viewCount?: number
  collectCount?: number
  createTime?: string
  updateTime?: string
  deleted?: number
}

export interface AttractionListParams {
  page?: number
  size?: number
  keyword?: string
  city?: string
  type?: number
  status?: number
}

export interface AttractionListResponse {
  list: CultureAttraction[]
  total: number
  page: number
  size: number
}

// ========== 管理端API ==========

// 获取特色周边列表（管理端）
export function getAdminAttractionList(params: AttractionListParams) {
  return request.get<AttractionListResponse>('/admin/culture/attractions/page', { params })
}

// 获取特色周边详情（管理端）
export function getAdminAttractionById(id: number) {
  return request.get<CultureAttraction>(`/admin/culture/attractions/${id}`)
}

// 创建特色周边（管理端）
export function createAdminAttraction(data: CultureAttraction) {
  return request.post<string>('/admin/culture/attractions', data)
}

// 更新特色周边（管理端）
export function updateAdminAttraction(id: number, data: CultureAttraction) {
  return request.put<string>(`/admin/culture/attractions/${id}`, data)
}

// 删除特色周边（管理端）
export function deleteAdminAttraction(id: number) {
  return request.delete<string>(`/admin/culture/attractions/${id}`)
}

// 切换特色周边状态（管理端）
export function toggleAdminAttractionStatus(id: number, status: number) {
  return request.put<string>(`/admin/culture/attractions/${id}/status`, { status })
}

// 获取特色周边统计（管理端）
export function getAdminAttractionStatistics() {
  return request.get<any>('/admin/culture/attractions/stats')
}







