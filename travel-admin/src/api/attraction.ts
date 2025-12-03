import request from '@/utils/request'

// 景点API接口

export interface Attraction {
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
  list: Attraction[]
  total: number
  page: number
  size: number
}

// 获取景点列表（管理端使用 /admin/attraction/list，用户端使用 /user/attraction/list）
export function getAttractionList(params: AttractionListParams) {
  // 检查是否在管理端页面，如果是则使用管理端接口
  const isAdminPage = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/dashboard/')
  const url = isAdminPage ? '/admin/attraction/list' : '/user/attraction/list'
  return request.get<AttractionListResponse>(url, { params })
}

// 获取景点详情（管理端使用 /admin/attraction/{id}，用户端使用 /user/attraction/detail/{id}）
export function getAttractionById(id: number) {
  const isAdminPage = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/dashboard/')
  const url = isAdminPage ? `/admin/attraction/${id}` : `/user/attraction/detail/${id}`
  return request.get<Attraction>(url)
}

// 创建景点（管理端）
export function createAttraction(data: Attraction) {
  const isAdminPage = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/dashboard/')
  const url = isAdminPage ? '/admin/attraction' : '/attraction'
  return request.post<string>(url, data)
}

// 更新景点（管理端）
export function updateAttraction(id: number, data: Attraction) {
  const isAdminPage = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/dashboard/')
  const url = isAdminPage ? `/admin/attraction/${id}` : `/attraction/${id}`
  return request.put<string>(url, data)
}

// 删除景点（管理端）
export function deleteAttraction(id: number) {
  const isAdminPage = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/dashboard/')
  const url = isAdminPage ? `/admin/attraction/${id}` : `/attraction/${id}`
  return request.delete<string>(url)
}

// 获取热门景点
export function getHotAttractions(limit: number = 10) {
  return request.get<Attraction[]>(`/attraction/hot?limit=${limit}`)
}

// 获取城市景点列表
export function getCityAttractions(city: string, limit: number = 10) {
  return request.get<Attraction[]>(`/attraction/city/${city}?limit=${limit}`)
}

// 更新景点统计
export function updateAttractionStats(id: number, type: string) {
  return request.post<string>(`/attraction/${id}/stats?type=${type}`)
}

// 切换景点状态（管理端）
export function toggleAttractionStatus(id: number, status: number) {
  const isAdminPage = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/dashboard/')
  const url = isAdminPage ? `/admin/attraction/${id}/status?status=${status}` : `/attraction/${id}/status?status=${status}`
  return request.post<string>(url)
}











