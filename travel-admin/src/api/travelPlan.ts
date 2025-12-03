import request from '@/utils/request'

// 旅游攻略API接口

export interface TravelPlan {
  id?: number
  title: string
  destination: string
  days: number
  budget: number
  people: number
  type: number
  description: string
  coverImage?: string
  content?: string
  schedule?: any
  costs?: any
  notes?: string
  tags?: string
  status?: number
  authorId?: number
  authorName?: string
  viewCount?: number
  likeCount?: number
  commentCount?: number
  collectCount?: number
  createTime?: string
  updateTime?: string
}

export interface PlanListParams {
  page?: number
  size?: number
  keyword?: string
  destination?: string
  status?: number
}

export interface PlanListResponse {
  list: TravelPlan[]
  total: number
  page: number
  size: number
}

// 获取攻略列表
export function getPlanList(params: PlanListParams) {
  return request.get<PlanListResponse>('/travel-plan/list', { params })
}

// 获取攻略详情
export function getPlanById(id: number) {
  return request.get<TravelPlan>(`/travel-plan/${id}`)
}

// 创建攻略
export function createPlan(data: TravelPlan) {
  return request.post<string>('/travel-plan', data)
}

// 更新攻略
export function updatePlan(id: number, data: TravelPlan) {
  return request.put<string>(`/travel-plan/${id}`, data)
}

// 删除攻略
export function deletePlan(id: number) {
  return request.delete<string>(`/travel-plan/${id}`)
}

// 获取热门攻略
export function getHotPlans(limit: number = 10) {
  return request.get<TravelPlan[]>(`/travel-plan/hot?limit=${limit}`)
}

// 获取用户攻略列表
export function getUserPlans(userId: number, page: number = 1, size: number = 10) {
  return request.get<PlanListResponse>(`/travel-plan/user/${userId}?page=${page}&size=${size}`)
}

// 获取当前用户的攻略列表
export function getMyPlans(params: PlanListParams) {
  return request.get<PlanListResponse>('/travel-plan/my-plans', { params })
}

// 更新攻略统计
export function updatePlanStats(id: number, type: string) {
  return request.post<string>(`/travel-plan/${id}/stats?type=${type}`)
}

// 审核攻略
export function auditPlan(id: number, status: number) {
  return request.post<string>(`/travel-plan/${id}/audit?status=${status}`)
}









