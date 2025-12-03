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









