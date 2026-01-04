/**
 * 图片URL处理工具函数
 * 统一处理图片URL，确保能正确访问
 */

/**
 * 处理图片URL，确保能正确访问
 * @param url 图片URL（可能是相对路径或绝对路径）
 * @returns 处理后的图片URL
 */
export function processImageUrl(url: string | null | undefined): string {
  if (!url || url === 'null' || url === 'undefined' || url.trim() === '') {
    return ''
  }

  let trimmedUrl = url.trim()
  console.log('processImageUrl 原始URL:', trimmedUrl)

  // 如果已经是完整的URL（http:// 或 https://），直接返回
  if (/^https?:\/\//i.test(trimmedUrl)) {
    return trimmedUrl
  }

  // 处理路径中的空格，替换为下划线（文件名中的空格可能导致路径错误）
  // 只替换文件名部分（最后一个斜杠之后的部分）的空格
  const lastSlashIndex = trimmedUrl.lastIndexOf('/')
  if (lastSlashIndex >= 0) {
    const pathPart = trimmedUrl.substring(0, lastSlashIndex + 1)
    const fileNamePart = trimmedUrl.substring(lastSlashIndex + 1)
    trimmedUrl = pathPart + fileNamePart.replace(/\s+/g, '_')
  } else {
    // 如果没有斜杠，说明只有文件名，直接替换空格
    trimmedUrl = trimmedUrl.replace(/\s+/g, '_')
  }

  // 统一处理大小写问题：将 /upLoad/ 或 /UpLoad/ 等转换为 /upload/
  // 使用正则匹配并替换，不区分大小写
  trimmedUrl = trimmedUrl.replace(/^\/up[lL]oad(\/)/i, '/upload$1')

  // 将 /images/ 路径转换为 /upload/ 路径（后端可能没有 /images/ 映射）
  if (trimmedUrl.startsWith('/images/')) {
    trimmedUrl = '/upload' + trimmedUrl.substring(7) // 7 = '/images'.length
  }

  // 处理可能缺少 /upload/ 前缀的路径
  // 如果路径以 culture/、homestay/、service/ 等开头，添加 /upload/ 前缀
  if (trimmedUrl.match(/^(culture|homestay|service|product|attraction|experience)\//i)) {
    trimmedUrl = '/upload/' + trimmedUrl
  }

  // 如果已经是 /api/ 或 /upload/ 开头的，直接返回（vite代理会处理）
  if (trimmedUrl.startsWith('/api/') || trimmedUrl.startsWith('/upload/')) {
    return trimmedUrl
  }

  // 如果是其他相对路径（以 / 开头），保持原样（vite代理会处理）
  if (trimmedUrl.startsWith('/')) {
    return trimmedUrl
  }

  // 如果是不以 / 开头的相对路径，添加 /upload/ 前缀
  const finalUrl = '/upload/' + trimmedUrl
  console.log('processImageUrl 处理后URL:', finalUrl)
  return finalUrl
}

/**
 * 处理图片URL数组
 * @param urls 图片URL数组或字符串
 * @returns 处理后的图片URL数组
 */
export function processImageUrls(urls: string[] | string | null | undefined): string[] {
  if (!urls) return []
  
  let urlArray: string[] = []
  
  if (Array.isArray(urls)) {
    urlArray = urls
  } else if (typeof urls === 'string') {
    try {
      const parsed = JSON.parse(urls)
      if (Array.isArray(parsed)) {
        urlArray = parsed
      } else {
        urlArray = [urls]
      }
    } catch {
      // 如果不是JSON，尝试按逗号分割
      urlArray = urls.split(',').map(url => url.trim()).filter(Boolean)
    }
  }
  
  return urlArray.map(processImageUrl).filter(Boolean)
}

