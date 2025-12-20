import request from '@/utils/request'

// 文化体验API接口

export interface CultureExperience {
  id?: number
  name: string
  categoryName: string
  location: string
  price: number
  duration: string
  rating?: number
  status?: string
  images?: string
  slogan?: string
  features?: string
  description?: string
  suitableFor?: string
  flow?: string
  includes?: string
  notes?: string
  contactPhone?: string
  host?: string
  reviews?: string
  viewCount?: number
  orderCount?: number
  createTime?: string
  updateTime?: string
}

export interface ExperienceListParams {
  page?: number
  size?: number
  keyword?: string
  categoryName?: string
  status?: string
}

export interface ExperienceListResponse {
  list: CultureExperience[]
  total: number
  page: number
  size: number
}

// 获取文化体验列表（管理端）
export function getExperienceList(params: ExperienceListParams) {
  return request.get<ExperienceListResponse>('/admin/culture/experiences/page', { params })
}

// 获取文化体验详情（管理端）
export function getExperienceById(id: number) {
  return request.get<CultureExperience>(`/admin/culture/experiences/${id}`)
}

// 创建文化体验（管理端）
export function createExperience(data: CultureExperience) {
  return request.post<string>('/admin/culture/experiences', data)
}

// 更新文化体验（管理端）
export function updateExperience(id: number, data: CultureExperience) {
  return request.put<string>(`/admin/culture/experiences/${id}`, data)
}

// 删除文化体验（管理端）
export function deleteExperience(id: number) {
  return request.delete<string>(`/admin/culture/experiences/${id}`)
}

// 切换文化体验状态（管理端）
export function toggleExperienceStatus(id: number, status: string) {
  return request.put<string>(`/admin/culture/experiences/${id}/status`, { status })
}

// 获取文化体验统计（管理端）
export function getExperienceStatistics() {
  return request.get<any>('/admin/culture/experiences/stats')
}

// 获取文化体验列表（用户端）
export function getUserExperienceList(params: ExperienceListParams) {
  return request.get<ExperienceListResponse>('/culture/experiences/page', { params })
}

// 获取文化体验详情（用户端）
export function getUserExperienceById(id: number) {
  return request.get<CultureExperience>(`/culture/experiences/${id}`)
}

// 获取热门文化体验（用户端）
export function getHotExperiences(limit: number = 10) {
  return request.get<CultureExperience[]>(`/culture/experiences/hot?limit=${limit}`)
}

// 根据分类获取文化体验（用户端）
export function getExperiencesByCategory(categoryName: string, limit: number = 10) {
  return request.get<CultureExperience[]>(`/culture/experiences/category/${categoryName}?limit=${limit}`)
}

// 获取所有分类（用户端）
export function getCategories() {
  return request.get<string[]>('/culture/experiences/categories')
}











































































