import request from '@/utils/request'

// 用户收藏API接口

export interface UserCollect {
  id?: number
  userId: number
  collectType: number // 1-攻略, 2-景点, 3-文旅项目
  collectId: number
  createTime?: string
}

export interface CollectListParams {
  userId: number
  collectType?: number
  page?: number
  size?: number
}

export interface CollectListResponse {
  list: UserCollect[]
  total: number
  page: number
  size: number
}

export interface CollectStats {
  totalCount: number
  typeCounts: {
    type1: number
    type2: number
    type3: number
  }
}

// 添加收藏
export function addCollect(collectType: number, collectId: number) {
  return request.post<string>('/user-collect/add', null, {
    params: { collectType, collectId }
  })
}

// 取消收藏
export function removeCollect(collectType: number, collectId: number) {
  return request.post<string>('/user-collect/remove', null, {
    params: { collectType, collectId }
  })
}

// 检查是否已收藏
export function checkCollected(collectType: number, collectId: number) {
  return request.get<boolean>('/user-collect/check', {
    params: { collectType, collectId }
  })
}

// 获取用户收藏列表
export function getUserCollects(params: CollectListParams) {
  return request.get<CollectListResponse>('/user-collect/list', { params })
}

// 获取用户收藏统计
export function getUserCollectStats(userId: number) {
  return request.get<CollectStats>(`/user-collect/stats/${userId}`)
}

// 批量删除收藏
export function batchRemoveCollects(userId: number, collectIds: number[]) {
  return request.post<string>('/user-collect/batch-remove', { collectIds }, {
    params: { userId }
  })
}



















