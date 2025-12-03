import request from '@/utils/request'

// 分享API接口

/**
 * 记录分享并奖励积分
 * @param contentType 内容类型：plan/attraction/culture
 * @param contentId 内容ID
 */
export function recordShare(contentType: string, contentId: number) {
  return request.post<string>('/share/record', null, {
    params: { contentType, contentId }
  })
}







































































