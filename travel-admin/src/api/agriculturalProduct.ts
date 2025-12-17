import request from '@/utils/request'

// 农特产品API接口

export interface AgriculturalProduct {
  id?: number
  name: string
  // 分类，如：茶叶、粮油等
  category?: string
  origin: string
  price: number
  originalPrice?: number
  unit?: string
  stock?: number // -1表示无限
  description?: string
  // 保质期
  shelfLife?: string
  images?: string[]
  features?: string[]
  highlights?: string[]
  // 规格选择，数组形式，元素可为 { label, value }
  availableSpecs?: any[]
  summary?: string
  badge?: string
  rating?: number
  sales?: number
  views?: number
  contactPhone?: string
  status?: number // 0-已关闭 1-正常 2-维护中
  createTime?: string
  updateTime?: string
}

export interface ProductListParams {
  page?: number
  size?: number
  keyword?: string
  origin?: string
  status?: number
}

export interface ProductListResponse {
  list: AgriculturalProduct[]
  total: number
  page: number
  size: number
}

// 获取农特产品列表（管理端）
export function getProductList(params: ProductListParams) {
  return request.get<ProductListResponse>('/admin/culture/products/page', { params })
}

// 获取农特产品详情（管理端）
export function getProductById(id: number) {
  return request.get<AgriculturalProduct>(`/admin/culture/products/${id}`)
}

// 创建农特产品（管理端）
export function createProduct(data: AgriculturalProduct) {
  return request.post<string>('/admin/culture/products', data)
}

// 更新农特产品（管理端）
export function updateProduct(id: number, data: AgriculturalProduct) {
  return request.put<string>(`/admin/culture/products/${id}`, data)
}

// 删除农特产品（管理端）
export function deleteProduct(id: number) {
  return request.delete<string>(`/admin/culture/products/${id}`)
}

// 切换农特产品状态（管理端）
export function toggleProductStatus(id: number, status: number) {
  return request.put<string>(`/admin/culture/products/${id}/status`, { status })
}

// 获取农特产品统计（管理端）
export function getProductStatistics() {
  return request.get<any>('/admin/culture/products/stats')
}








