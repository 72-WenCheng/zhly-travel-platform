/**
 * API请求工具
 * 统一的HTTP请求封装，提供错误处理和用户友好的提示
 */

const BASE_URL = 'http://localhost:8070/api'

/**
 * 发起HTTP请求
 * @param {Object} options 请求配置
 * @returns {Promise}
 */
function request(options) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success: (res) => {
        // 处理响应
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          // HTTP状态码错误
          reject(new Error(getErrorMessage(res.statusCode)))
        }
      },
      fail: (err) => {
        // 网络错误或请求失败
        console.error('请求失败:', err)
        reject(new Error('网络连接异常，请检查网络后重试'))
      }
    })
  })
}

/**
 * 获取用户友好的错误信息
 * @param {number} statusCode HTTP状态码
 * @returns {string} 错误信息
 */
function getErrorMessage(statusCode) {
  const errorMap = {
    400: '请求参数错误',
    401: '未授权，请重新登录',
    403: '拒绝访问',
    404: '请求的资源不存在',
    500: '服务器异常，请稍后重试',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时'
  }
  return errorMap[statusCode] || '请求失败，请重试'
}

/**
 * 处理业务错误，提取用户友好的错误信息
 * @param {Error} error 错误对象
 * @param {string} defaultMessage 默认错误信息
 * @returns {string} 用户友好的错误信息
 */
function getBusinessErrorMessage(error, defaultMessage = '操作失败，请重试') {
  // 优先使用后端返回的错误信息
  if (error.response?.data?.message) {
    return error.response.data.message
  }
  
  if (error.response?.data?.data) {
    return error.response.data.data
  }
  
  // 处理网络错误
  if (error.message) {
    if (error.message.includes('Connection') || error.message.includes('timeout')) {
      return '网络连接异常，请检查网络后重试'
    }
    
    if (error.message.includes('500') || error.message.includes('Internal Server Error')) {
      return '服务器异常，请稍后重试'
    }
    
    // 检查常见的技术性错误
    if (error.message.includes('username') && error.message.includes('already exists')) {
      return '用户名已存在，请选择其他用户名'
    }
    
    if (error.message.includes('email') && error.message.includes('already exists')) {
      return '邮箱已被注册，请使用其他邮箱'
    }
    
    if (error.message.includes('phone') && error.message.includes('already exists')) {
      return '手机号已被使用，请使用其他手机号'
    }
    
    // 返回原始错误信息
    return error.message
  }
  
  return defaultMessage
}

/**
 * POST请求
 */
function post(url, data, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * GET请求
 */
function get(url, params, options = {}) {
  // 拼接URL参数
  let fullUrl = url
  if (params) {
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    fullUrl += '?' + queryString
  }
  
  return request({
    url: fullUrl,
    method: 'GET',
    ...options
  })
}

/**
 * PUT请求
 */
function put(url, data, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * DELETE请求
 */
function del(url, data, options = {}) {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

export default {
  request,
  post,
  get,
  put,
  delete: del,
  getBusinessErrorMessage
}

