// 工具函数集合

/**
 * 格式化日期
 */
export function formatDate(date: string | Date, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化日期时间字符串，将 ISO 格式中的 T 替换为空格
 * @param dateTimeStr 日期时间字符串，如 "2025-11-01T15:13:37" 或 "2025-11-01T15:13:37.000Z"
 * @returns 格式化后的字符串，如 "2025-11-01 15:13:37"
 */
export function formatDateTime(dateTimeStr: string | null | undefined): string {
  if (!dateTimeStr) return '-'
  
  // 如果是 ISO 格式，将 T 替换为空格，并移除毫秒和时区信息
  if (typeof dateTimeStr === 'string') {
    // 处理 ISO 格式：2025-11-01T15:13:37 或 2025-11-01T15:13:37.000Z
    let formatted = dateTimeStr.replace('T', ' ')
    
    // 移除毫秒部分（.000 或 .123）
    formatted = formatted.replace(/\.\d{3}Z?$/, '')
    
    // 移除 Z 时区标识（如果还存在）
    formatted = formatted.replace(/Z$/, '')
    
    // 移除末尾的时区信息（如 +08:00）
    formatted = formatted.replace(/[+-]\d{2}:\d{2}$/, '')
    
    return formatted.trim()
  }
  
  return dateTimeStr.toString()
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 格式化数字
 */
export function formatNumber(num: number): string {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

/**
 * 格式化价格
 */
export function formatPrice(price: number): string {
  return '¥' + price.toFixed(2)
}

/**
 * 获取景点类型名称
 */
export function getAttractionTypeName(type: number): string {
  const typeMap: Record<number, string> = {
    1: '自然景观',
    2: '人文景观',
    3: '主题公园',
    4: '博物馆',
    5: '历史遗迹',
    6: '美食街'
  }
  return typeMap[type] || '未知'
}

/**
 * 获取攻略类型名称
 */
export function getPlanTypeName(type: number): string {
  const typeMap: Record<number, string> = {
    1: '自由行',
    2: '跟团游',
    3: '自驾游',
    4: '背包客'
  }
  return typeMap[type] || '未知'
}

/**
 * 获取状态名称
 */
export function getStatusName(status: number): string {
  const statusMap: Record<number, string> = {
    0: '禁用',
    1: '正常',
    2: '维护中',
    3: '待审核'
  }
  return statusMap[status] || '未知'
}

/**
 * 获取状态标签类型
 */
export function getStatusTagType(status: number): string {
  const tagMap: Record<number, string> = {
    0: 'danger',
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return tagMap[status] || ''
}

/**
 * 生成随机ID
 */
export function generateId(): string {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout | null = null
  
  return (...args: Parameters<T>) => {
    if (timeout) clearTimeout(timeout)
    timeout = setTimeout(() => func(...args), wait)
  }
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let lastTime = 0
  
  return (...args: Parameters<T>) => {
    const now = Date.now()
    if (now - lastTime >= wait) {
      lastTime = now
      func(...args)
    }
  }
}

/**
 * 深拷贝
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) as any
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as any
  if (typeof obj === 'object') {
    const clonedObj = {} as any
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
  return obj
}

/**
 * 验证邮箱
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证手机号
 */
export function isValidPhone(phone: string): boolean {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证身份证号
 */
export function isValidIdCard(idCard: string): boolean {
  const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return idCardRegex.test(idCard)
}



















