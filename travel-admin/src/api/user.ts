import request from '@/utils/request'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  gender: number
  age: number
  userType: number
  travelPreference: number
  interestTags: string
  frequentCities: string
  status: number
  lastLoginTime: string
  lastLoginIp: string
  createTime: string
  updateTime: string
}

// 上传头像
export const uploadAvatar = (formData: FormData) => {
  return request({
    url: '/user/upload-avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新用户信息
export const updateUserInfo = (data: Partial<UserInfo>) => {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 修改密码
export const changePassword = (data: { oldPassword: string; newPassword: string }) => {
  return request({
    url: '/user/change-password',
    method: 'put',
    data
  })
}

// 获取用户信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 退出登录
export const logout = () => {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

// 注销账号
export const deactivateAccount = (data: { reason?: string }) => {
  return request({
    url: '/user/deactivate',
    method: 'post',
    data
  })
}

// ========== 用户管理 ==========

// 获取用户列表
export const getUserList = (params: { page: number; size: number; keyword?: string }) => {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export const getUserDetail = (id: number) => {
  return request({
    url: `/admin/user/${id}`,
    method: 'get'
  })
}

// 更新用户状态
export const updateUserStatus = (id: number, status: number) => {
  return request({
    url: `/admin/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 删除用户
export const deleteUser = (id: number) => {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

// ========== 用户画像 ==========

// 获取用户画像分析数据
export const getUserPortrait = () => {
  return request({
    url: '/user/portrait',
    method: 'get'
  })
}

// 根据用户ID获取用户画像（管理员接口）
export const getUserPortraitById = (userId: number) => {
  return request({
    url: `/user/portrait/${userId}`,
    method: 'get'
  })
}

