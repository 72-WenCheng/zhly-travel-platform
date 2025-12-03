import request from '@/utils/request'

export interface ConversationItem {
  conversationId: string
  targetUserId: number
  targetNickname: string
  targetAvatar: string
  latestMessage: string
  latestMessageTime: string
  latestSenderId: number
  unreadCount: number
  messageCount: number
}

export interface ChatMessage {
  id: number
  senderId: number
  receiverId: number
  content: string
  status: number
  readTime?: string
  createTime: string
  updateTime: string
  senderNickname?: string
  senderAvatar?: string
  receiverNickname?: string
  receiverAvatar?: string
  isOutgoing?: boolean
}

export const fetchConversationList = (userId: number) => {
  return request({
    url: '/user/message/conversations',
    method: 'get',
    params: { userId }
  })
}

export const fetchChatHistory = (userId: number, targetUserId: number, page = 1, size = 20) => {
  return request({
    url: '/user/message/history',
    method: 'get',
    params: { userId, targetUserId, page, limit: size }
  })
}

export const sendPrivateMessage = (payload: { senderId: number; receiverId: number; content: string }) => {
  return request({
    url: '/user/message/send',
    method: 'post',
    data: payload
  })
}

export const markConversationRead = (payload: { userId: number; targetUserId: number }) => {
  return request({
    url: '/user/message/mark-read',
    method: 'post',
    data: payload
  })
}

export const getUnreadMessageCount = (userId: number) => {
  return request({
    url: '/user/message/unread-count',
    method: 'get',
    params: { userId }
  })
}

export const recallMessage = (payload: { messageId: number; userId: number }) => {
  return request({
    url: '/user/message/recall',
    method: 'post',
    data: payload
  })
}

