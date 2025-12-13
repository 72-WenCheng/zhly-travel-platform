import request from '@/utils/request'

// 文旅项目API接口

export interface CultureProject {
  id?: number
  name: string
  type: number
  region: string
  location?: string
  image?: string
  description?: string
  price: number
  rating?: number
  contactPhone?: string
  contactPerson?: string
  address?: string
  status?: number
  viewCount?: number
  orderCount?: number
  revenue?: number
  createTime?: string
  updateTime?: string
}

export interface ProjectListParams {
  page?: number
  size?: number
  keyword?: string
  region?: string
  type?: number
  status?: number
}

export interface ProjectListResponse {
  list: CultureProject[]
  total: number
  page: number
  size: number
}

// 获取文旅项目列表
export function getProjectList(params: ProjectListParams) {
  return request.get<ProjectListResponse>('/culture-project/list', { params })
}

// 获取文旅项目详情
export function getProjectById(id: number) {
  return request.get<CultureProject>(`/culture-project/${id}`)
}

// 创建文旅项目
export function createProject(data: CultureProject) {
  return request.post<string>('/culture-project', data)
}

// 更新文旅项目
export function updateProject(id: number, data: CultureProject) {
  return request.put<string>(`/culture-project/${id}`, data)
}

// 删除文旅项目
export function deleteProject(id: number) {
  return request.delete<string>(`/culture-project/${id}`)
}

// 获取热门文旅项目
export function getHotProjects(limit: number = 10) {
  return request.get<CultureProject[]>(`/culture-project/hot?limit=${limit}`)
}

// 获取地区文旅项目列表
export function getRegionProjects(region: string, limit: number = 10) {
  return request.get<CultureProject[]>(`/culture-project/region/${region}?limit=${limit}`)
}

// 更新文旅项目统计
export function updateProjectStats(id: number, type: string) {
  return request.post<string>(`/culture-project/${id}/stats?type=${type}`)
}

// 切换文旅项目状态
export function toggleProjectStatus(id: number, status: number) {
  return request.post<string>(`/culture-project/${id}/status?status=${status}`)
}

// 获取文旅项目统计
export function getProjectStatistics() {
  return request.get<any>('/culture-project/statistics')
}

// ========== 管理端API ==========

// 获取政策对接项目列表（管理端）
export function getAdminProjectList(params: ProjectListParams) {
  return request.get<ProjectListResponse>('/admin/culture/project/page', { params })
}

// 获取政策对接项目详情（管理端）
export function getAdminProjectById(id: number) {
  return request.get<CultureProject>(`/admin/culture/project/${id}`)
}

// 创建政策对接项目（管理端）
export function createAdminProject(data: CultureProject) {
  return request.post<string>('/admin/culture/project', data)
}

// 更新政策对接项目（管理端）
export function updateAdminProject(id: number, data: CultureProject) {
  return request.put<string>(`/admin/culture/project/${id}`, data)
}

// 删除政策对接项目（管理端）
export function deleteAdminProject(id: number) {
  return request.delete<string>(`/admin/culture/project/${id}`)
}

// 切换政策对接项目状态（管理端）
export function toggleAdminProjectStatus(id: number, status: number) {
  return request.put<string>(`/admin/culture/project/${id}/status`, { status })
}

// 获取政策对接项目统计（管理端）
export function getAdminProjectStatistics() {
  return request.get<any>('/admin/culture/project/stats')
}









