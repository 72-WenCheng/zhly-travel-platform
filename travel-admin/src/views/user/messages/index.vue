<template>
  <div class="message-page">
    <BackButton />

    <div class="message-layout">
      <div class="conversation-panel">
        <div class="panel-header">
          <div class="title">
            <el-icon :size="20"><MessageBox /></el-icon>
            <span>会话列表</span>
          </div>
        </div>

        <el-scrollbar class="conversation-scroll">
          <div
            v-for="conversation in conversations"
            :key="conversation.targetUserId"
            class="conversation-item"
            :class="{ active: selectedConversation?.targetUserId === conversation.targetUserId }"
            @click="selectConversation(conversation)"
          >
            <div class="conversation-main">
              <el-avatar
                :size="52"
                :src="conversation.targetAvatar || '/default-avatar.png'"
                class="conversation-avatar"
              >
                {{ conversation.targetNickname?.charAt(0) }}
              </el-avatar>
              <div class="conversation-info">
                <div class="conversation-top">
                  <span class="conversation-name">{{ conversation.targetNickname }}</span>
                  <span class="conversation-time">
                    {{ formatRelativeTime(conversation.latestMessageTime) }}
                  </span>
                </div>
                <div class="conversation-bottom">
                  <span class="conversation-preview">{{ conversation.latestMessage }}</span>
                  <el-badge
                    v-if="conversation.unreadCount"
                    :value="conversation.unreadCount"
                    type="danger"
                    class="unread-badge"
                  />
                </div>
              </div>
            </div>
            <el-button
              text
              size="small"
              class="conversation-delete"
              @click.stop="confirmDeleteConversation(conversation)"
            >
              删除
            </el-button>
          </div>

          <el-empty
            v-if="!conversations.length"
            description="暂无会话，快去和旅行达人打个招呼吧"
            :image-size="120"
          />
        </el-scrollbar>
      </div>

      <div class="chat-panel">
        <div v-if="!selectedConversation" class="chat-empty">
          <el-empty description="选择一位联系人开始聊天" />
        </div>
        <template v-else>
          <div class="chat-header">
            <div class="chat-user">
              <el-avatar
                :size="48"
                :src="selectedConversation.targetAvatar || '/default-avatar.png'"
              >
                {{ selectedConversation.targetNickname?.charAt(0) }}
              </el-avatar>
              <div class="chat-user-info">
                <h3>{{ selectedConversation.targetNickname }}</h3>
                <p>保持联系，随时掌握行程变化</p>
              </div>
            </div>
            <div class="chat-header-actions">
              <el-button
                class="chat-btn"
                type="default"
                size="small"
                :icon="User"
                @click="viewProfile(selectedConversation.targetUserId)"
              >
                查看资料
              </el-button>
            </div>
          </div>

          <div class="chat-body">
            <div class="load-more" v-if="hasMoreMessage">
              <el-button text size="small" :loading="messageLoading" @click="loadMoreHistory">
                加载更多历史记录
              </el-button>
            </div>
            <el-scrollbar ref="chatScrollbarRef" class="chat-scrollbar">
              <div class="chat-content">
                <div
                  v-for="message in messages"
                  :key="message.id"
                  class="message-item"
                  :class="{ outgoing: message.isOutgoing, recalled: message.content === '[已撤回]' }"
                >
                  <el-avatar
                    :size="40"
                    :src="message.isOutgoing ? currentUserAvatar : selectedConversation.targetAvatar"
                  >
                    {{ message.isOutgoing ? currentUserName?.charAt(0) : selectedConversation.targetNickname?.charAt(0) }}
                  </el-avatar>
                  <div class="message-bubble">
                    <div class="message-text">{{ message.content === '[已撤回]' ? '[已撤回]' : message.content }}</div>
                    <div class="message-footer">
                      <div class="message-time">
                        {{ formatRelativeTime(message.createTime) }}
                        <span v-if="message.isOutgoing && message.content !== '[已撤回]'">
                          {{ message.status === 1 ? ' · 已读' : ' · 待查阅' }}
                        </span>
                      </div>
                      <el-button
                        v-if="message.isOutgoing && message.content !== '[已撤回]' && canRecallMessage(message)"
                        text
                        size="small"
                        class="message-recall-btn"
                        @click.stop="handleRecallMessage(message)"
                      >
                        撤回
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </div>

          <div class="chat-input">
            <el-input
              v-model="messageContent"
              type="textarea"
              :rows="3"
              resize="none"
              maxlength="500"
              placeholder="输入想说的话，Enter发送，Shift+Enter换行"
              @keyup.enter.exact.prevent="handleSend"
              @keyup.enter.shift.stop
              :disabled="sendLoading"
            />
            <div class="chat-input-actions">
              <div>
                <el-button class="chat-btn" @click="messageContent = ''" :disabled="sendLoading">清空</el-button>
                <el-button class="chat-btn" :loading="sendLoading" @click="handleSend">发送</el-button>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatLineSquare, MessageBox, User } from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { useUserStore } from '@/stores/user'
import {
  fetchConversationList,
  fetchChatHistory,
  sendPrivateMessage,
  markConversationRead,
  recallMessage,
  type ConversationItem,
  type ChatMessage
} from '@/api/message'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

type TargetInfo = {
  id: number
  nickname?: string
  avatar?: string
}

interface IncomingMessagePayload {
  message: ChatMessage
  conversation: ConversationItem
}

interface ReadUpdatePayload {
  readerId: number
}

const conversations = ref<ConversationItem[]>([])
const selectedConversation = ref<ConversationItem | null>(null)
const messages = ref<ChatMessage[]>([])
const messagePagination = ref({
  page: 1,
  size: 20,
  total: 0
})
const messageLoading = ref(false)
const sendLoading = ref(false)
const messageContent = ref('')
const unreadTotal = ref(0)
const chatScrollbarRef = ref()
const messageIdSet = ref<Set<number>>(new Set())

const ws = ref<WebSocket | null>(null)
const wsConnected = ref(false)
const reconnectTimer = ref<number | null>(null)
const reconnectAttempts = ref(0)

const currentUserId = computed(() => userStore.userInfo?.id || 0)
const currentUserName = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '我')
const currentUserAvatar = computed(() => userStore.userInfo?.avatar || '/default-avatar.png')

const hasMoreMessage = computed(() => messages.value.length < messagePagination.value.total)

const parseTargetInfoFromRoute = (): TargetInfo | null => {
  const { targetId } = route.query
  if (!targetId) return null
  const id = Number(targetId)
  if (!id || Number.isNaN(id)) return null
  return {
    id,
    nickname: (route.query.targetName as string) || '',
    avatar: (route.query.targetAvatar as string) || ''
  }
}

const buildPlaceholderConversation = (info: TargetInfo): ConversationItem => {
  const myId = currentUserId.value
  const conversationKey = myId
    ? `${Math.min(myId, info.id)}_${Math.max(myId, info.id)}`
    : `${info.id}`
  return {
    conversationId: conversationKey,
    targetUserId: info.id,
    targetNickname: info.nickname || '旅行达人',
    targetAvatar: info.avatar || '/default-avatar.png',
    latestMessage: '',
    latestMessageTime: '',
    latestSenderId: info.id,
    unreadCount: 0,
    messageCount: 0
  }
}

const initConversationFromQuery = async () => {
  const info = parseTargetInfoFromRoute()
  if (!info) return
  if (!currentUserId.value) {
    userStore.initUser()
  }
  if (!currentUserId.value || info.id === currentUserId.value) {
    return
  }
  if (!conversations.value.length) {
    await loadConversations()
  }
  const existing = conversations.value.find((item) => item.targetUserId === info.id)
  if (existing) {
    await selectConversation(existing, { force: true })
    return
  }
  const placeholder = buildPlaceholderConversation(info)
  conversations.value = [
    placeholder,
    ...conversations.value.filter((item) => item.targetUserId !== info.id)
  ]
  selectedConversation.value = placeholder
  messages.value = []
  messagePagination.value.page = 1
  messagePagination.value.total = 0
  messageIdSet.value = new Set()
}

const formatRelativeTime = (time?: string) => {
  if (!time) return ''
  return dayjs(time).fromNow()
}

const scrollToBottom = async () => {
  await nextTick()
  const scrollbar = chatScrollbarRef.value as any
  if (scrollbar?.setScrollTop) {
    scrollbar.setScrollTop(Number.MAX_SAFE_INTEGER)
  }
}

const loadConversations = async () => {
  if (!currentUserId.value) return
  try {
    const res = await fetchConversationList(currentUserId.value)
    conversations.value = res.data || []
    unreadTotal.value = conversations.value.reduce((sum, item) => sum + (item.unreadCount || 0), 0)

    if (selectedConversation.value) {
      const updated = conversations.value.find(
        (item) => item.targetUserId === selectedConversation.value?.targetUserId
      )
      if (updated) {
        selectedConversation.value = { ...selectedConversation.value, ...updated }
      }
    }
  } catch (error) {
    console.error('获取会话列表失败', error)
  }
}

const loadMessages = async (reset = false) => {
  if (!currentUserId.value || !selectedConversation.value) return
  try {
    messageLoading.value = true
    if (reset) {
      messagePagination.value.page = 1
      messages.value = []
      messageIdSet.value = new Set()
    }
    const res = await fetchChatHistory(
      currentUserId.value,
      selectedConversation.value.targetUserId,
      messagePagination.value.page,
      messagePagination.value.size
    )
    const records: ChatMessage[] = res.data?.records || []
    messagePagination.value.total = res.data?.total || 0
    const normalized = [...records].reverse()

    if (messagePagination.value.page === 1) {
      messages.value = normalized
      messageIdSet.value = new Set(
        normalized.filter((item) => !!item.id).map((item) => item.id as number)
      )
      scrollToBottom()
    } else {
      const uniqueRecords = normalized.filter((item) => {
        if (!item.id) return false
        if (messageIdSet.value.has(item.id)) return false
        messageIdSet.value.add(item.id)
        return true
      })
      messages.value = [...uniqueRecords, ...messages.value]
    }
  } catch (error) {
    console.error('获取聊天记录失败', error)
    ElMessage.error('加载私信失败，请稍后重试')
  } finally {
    messageLoading.value = false
  }
}

const selectConversation = async (conversation: ConversationItem, options: { force?: boolean } = {}) => {
  if (!options.force && selectedConversation.value?.targetUserId === conversation.targetUserId) {
    return
  }
  selectedConversation.value = conversation
  messagePagination.value.page = 1
  await loadMessages(true)
  await markAsRead(conversation.targetUserId)
}

const loadMoreHistory = async () => {
  if (messageLoading.value) return
  messagePagination.value.page += 1
  await loadMessages()
}

const markAsRead = async (targetUserId: number) => {
  try {
    await markConversationRead({
      userId: currentUserId.value,
      targetUserId
    })
    const target = conversations.value.find((item) => item.targetUserId === targetUserId)
    if (target) {
      const unread = target.unreadCount || 0
      unreadTotal.value = Math.max(0, unreadTotal.value - unread)
      target.unreadCount = 0
    }
  } catch (error) {
    console.warn('标记已读失败', error)
  }
}

const handleSend = async () => {
  if (!selectedConversation.value) {
    ElMessage.warning('请选择一个会话')
    return
  }
  if (!messageContent.value.trim()) {
    ElMessage.warning('请输入要发送的内容')
    return
  }
  try {
    sendLoading.value = true
    await sendPrivateMessage({
      senderId: currentUserId.value,
      receiverId: selectedConversation.value.targetUserId,
      content: messageContent.value.trim()
    })
    messageContent.value = ''
    if (!wsConnected.value) {
      await loadMessages(true)
      await loadConversations()
    }
    ElMessage.success('发送成功')
  } catch (error) {
    console.error('发送消息失败', error)
    ElMessage.error('发送失败，请稍后再试')
  } finally {
    sendLoading.value = false
  }
}

const confirmDeleteConversation = async (conversation: ConversationItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除与 ${conversation.targetNickname || '该用户'} 的会话吗？此操作不会通知对方。`,
      '删除会话',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'delete-conversation-dialog'
      }
    )
    conversations.value = conversations.value.filter(
      (item) => item.targetUserId !== conversation.targetUserId
    )
    if (selectedConversation.value?.targetUserId === conversation.targetUserId) {
      selectedConversation.value = null
      messages.value = []
      messagePagination.value.page = 1
      messagePagination.value.total = 0
      messageIdSet.value = new Set()
    }
    unreadTotal.value = conversations.value.reduce((sum, item) => sum + (item.unreadCount || 0), 0)
    ElMessage.success('会话已删除')
  } catch (error) {
    // canceled
  }
}

const viewProfile = (userId: number) => {
  router.push(`/home/user/profile/${userId}`)
}

const getStoredToken = () => {
  return sessionStorage.getItem('travel_token') || localStorage.getItem('travel_token') || ''
}

const resolveWsBaseUrl = () => {
  // 优先使用环境变量配置
  if (import.meta.env.VITE_WS_BASE_URL) {
    return import.meta.env.VITE_WS_BASE_URL
  }
  
  // 开发环境：使用Vite代理（通过相对路径）
  if (import.meta.env.DEV) {
    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
    const host = window.location.host
    return `${protocol}://${host}`
  }
  
  // 生产环境：使用当前域名
  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
  return `${protocol}://${window.location.host}`
}

const connectWebSocket = () => {
  const token = getStoredToken()
  if (!token) {
    console.warn('❌ WebSocket连接失败: 未找到token')
    return
  }

  cleanupWebSocket()

  const base = resolveWsBaseUrl()
  const wsUrl = `${base}/api/ws/chat?token=${token}`
  console.log('🔌 正在连接WebSocket:', wsUrl)
  
  try {
    ws.value = new WebSocket(wsUrl)
    ws.value.onopen = () => {
      wsConnected.value = true
      reconnectAttempts.value = 0
      console.log('✅ WebSocket连接成功')
      ElMessage.success('实时消息已连接')
    }
    ws.value.onmessage = (event) => {
      console.log('📨 收到WebSocket消息:', event.data)
      handleSocketMessage(event.data)
    }
    ws.value.onclose = (event) => {
      wsConnected.value = false
      console.warn('⚠️ WebSocket连接关闭:', event.code, event.reason)
      if (event.code !== 1000) {
        scheduleReconnect()
      }
    }
    ws.value.onerror = (error) => {
      console.error('❌ WebSocket错误:', error)
      ElMessage.error('WebSocket连接失败，将使用轮询模式')
      ws.value?.close()
    }
  } catch (error) {
    console.error('❌ 建立WebSocket连接失败', error)
    scheduleReconnect()
  }
}

const scheduleReconnect = () => {
  if (reconnectTimer.value) return
  reconnectTimer.value = window.setTimeout(() => {
    reconnectAttempts.value += 1
    connectWebSocket()
    reconnectTimer.value = null
  }, Math.min(1000 * Math.max(reconnectAttempts.value, 1), 10000))
}

const cleanupWebSocket = () => {
  if (ws.value) {
    ws.value.onopen = null
    ws.value.onclose = null
    ws.value.onmessage = null
    ws.value.onerror = null
    ws.value.close()
    ws.value = null
  }
  if (reconnectTimer.value) {
    clearTimeout(reconnectTimer.value)
    reconnectTimer.value = null
  }
}

const handleSocketMessage = (raw: string) => {
  try {
    const payload = JSON.parse(raw) as { type: string; data: any }
    console.log('📦 解析消息类型:', payload.type, payload.data)
    switch (payload.type) {
      case 'NEW_MESSAGE':
        console.log('💬 处理新消息')
        handleIncomingMessage(payload.data as IncomingMessagePayload)
        break
      case 'READ_UPDATE':
        console.log('👁️ 处理已读更新')
        handleReadUpdate(payload.data as ReadUpdatePayload)
        break
      case 'MESSAGE_RECALLED':
        console.log('🔄 处理消息撤回')
        handleMessageRecalled(payload.data as { messageId: number })
        break
      case 'SYSTEM':
        console.log('ℹ️ 系统消息:', payload.data)
        break
      default:
        console.log('⚠️ 未知消息类型:', payload.type)
        break
    }
  } catch (error) {
    console.error('❌ 解析WebSocket消息失败', error, raw)
  }
}

const handleIncomingMessage = async (payload: IncomingMessagePayload) => {
  console.log('📥 收到新消息推送:', payload)
  if (!payload?.message || !payload?.conversation) {
    console.warn('⚠️ 消息格式不完整:', payload)
    return
  }
  
  const message = payload.message as ChatMessage
  const conversation = payload.conversation as ConversationItem
  
  console.log('💬 消息内容:', message.content, '来自:', conversation.targetUserId)
  
  updateConversationList(conversation)

  if (
    selectedConversation.value &&
    selectedConversation.value.targetUserId === conversation.targetUserId
  ) {
    console.log('✅ 当前正在查看此会话，直接添加消息')
    await appendMessage(message)
    if (!message.isOutgoing) {
      await markAsRead(conversation.targetUserId)
    }
  } else {
    console.log('ℹ️ 当前未查看此会话，更新未读数')
    unreadTotal.value = conversations.value.reduce(
      (sum, item) => sum + (item.unreadCount || 0),
      0
    )
  }
}

const appendMessage = async (message: ChatMessage) => {
  if (!message.id || messageIdSet.value.has(message.id)) {
    return
  }
  messageIdSet.value.add(message.id)
  messages.value = [...messages.value, message]
  messagePagination.value.total = Math.max(messagePagination.value.total + 1, messages.value.length)
  await scrollToBottom()
}

const updateConversationList = (conversation: ConversationItem) => {
  const existingIndex = conversations.value.findIndex(
    (item) => item.targetUserId === conversation.targetUserId
  )
  if (existingIndex !== -1) {
    conversations.value.splice(existingIndex, 1)
  }
  conversations.value.unshift(conversation)
  unreadTotal.value = conversations.value.reduce((sum, item) => sum + (item.unreadCount || 0), 0)
  if (
    selectedConversation.value &&
    selectedConversation.value.targetUserId === conversation.targetUserId
  ) {
    selectedConversation.value = { ...selectedConversation.value, ...conversation }
  }
}

const handleReadUpdate = (payload: ReadUpdatePayload) => {
  if (!payload?.readerId) return
  const target = conversations.value.find((item) => item.targetUserId === payload.readerId)
  if (target) {
    target.unreadCount = 0
  }
  if (selectedConversation.value?.targetUserId === payload.readerId) {
    messages.value = messages.value.map((msg) =>
      msg.senderId === currentUserId.value ? { ...msg, status: 1 } : msg
    )
  }
}

const canRecallMessage = (message: ChatMessage): boolean => {
  if (!message.createTime) return false
  const createTime = dayjs(message.createTime)
  const now = dayjs()
  const minutesDiff = now.diff(createTime, 'minute')
  return minutesDiff <= 2
}

const handleRecallMessage = async (message: ChatMessage) => {
  // 再次检查是否在2分钟内
  if (!canRecallMessage(message)) {
    return
  }

  try {
    await ElMessageBox.confirm('确定要撤回这条消息吗？', '撤回消息', {
      confirmButtonText: '确定撤回',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 确认后再次检查时间（防止确认对话框期间时间超限）
    if (!canRecallMessage(message)) {
      return
    }

    await recallMessage({
      messageId: message.id!,
      userId: currentUserId.value
    })

    // 更新本地消息状态
    const index = messages.value.findIndex((msg) => msg.id === message.id)
    if (index !== -1) {
      messages.value[index].content = '[已撤回]'
    }

    ElMessage.success('消息已撤回')
  } catch (error: any) {
    if (error !== 'cancel') {
      // 静默处理错误，不显示错误提示
      console.error('撤回消息失败:', error)
    }
  }
}

const handleMessageRecalled = (payload: { messageId: number }) => {
  if (!payload?.messageId) return
  const index = messages.value.findIndex((msg) => msg.id === payload.messageId)
  if (index !== -1) {
    messages.value[index].content = '[已撤回]'
  }
}

onMounted(async () => {
  if (!userStore.userInfo) {
    userStore.initUser()
  }
  await loadConversations()
  await initConversationFromQuery()
  connectWebSocket()
})

watch(
  () => route.query.targetId,
  async () => {
    await initConversationFromQuery()
  }
)

watch(
  () => userStore.token,
  () => {
    connectWebSocket()
  }
)

onBeforeUnmount(() => {
  cleanupWebSocket()
})
</script>

<style scoped lang="scss">
.message-page {
  padding: 24px;
  background: #f5f7fb;
}

.message-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
}

.conversation-panel {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 6px 18px rgba(15, 76, 129, 0.04);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 240px);
  max-height: calc(100vh - 240px);

  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    .title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-weight: 600;
      color: #0f2b46;
      font-size: 16px;
      
      .el-icon {
        font-size: 20px;
      }
    }
  }

  .conversation-scroll {
    flex: 1;
    min-height: 0;
    margin-top: 8px;
  }

  .conversation-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 14px 14px 14px 10px;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 1px solid transparent;

    &:hover {
      background: rgba(64, 158, 255, 0.08);
      border-color: rgba(64, 158, 255, 0.1);
    }

    &.active {
      background: rgba(64, 158, 255, 0.12);
      box-shadow: inset 0 0 0 1px rgba(64, 158, 255, 0.2);
    }
  }

  .conversation-main {
    display: flex;
    gap: 12px;
    flex: 1;
    align-items: center;
    min-width: 0; // 确保flex子元素可以收缩
    overflow: hidden; // 防止内容溢出
  }

  .conversation-delete {
    opacity: 0;
    color: #a0aec0;
    transition: opacity 0.2s ease;
  }

  .conversation-item:hover .conversation-delete {
    opacity: 1;
  }

  .conversation-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;
    min-width: 0; // 确保flex子元素可以收缩
    overflow: hidden; // 防止内容溢出
  }

  .conversation-top,
  .conversation-bottom {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    min-width: 0; // 确保flex子元素可以收缩
    overflow: hidden; // 防止内容溢出
  }

  .conversation-name {
    font-weight: 600;
    color: #0f2b46;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex-shrink: 1;
    min-width: 0;
  }

  .conversation-time {
    font-size: 12px;
    color: #8b99ad;
    flex-shrink: 0; // 时间不收缩
    white-space: nowrap;
  }

  .conversation-preview {
    flex: 1;
    color: #5c6678;
    font-size: 13px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    min-width: 0; // 确保可以收缩
  }
}

.chat-panel {
  background: #fff;
  border-radius: 16px;
  padding: 16px 20px;
  min-height: 500px;
  box-shadow: 0 8px 24px rgba(10, 37, 64, 0.06);
  display: flex;
  flex-direction: column;

  .chat-empty {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f7;

  .chat-user {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .chat-user-info h3 {
    margin: 0;
    font-size: 20px;
    color: #0f2b46;
  }

  .chat-user-info p {
    margin: 4px 0 0;
    color: #6d7685;
    font-size: 13px;
  }
}

.chat-body {
  flex: 1;
  margin: 12px 0 16px;
  background: #f7f9fc;
  border-radius: 12px;
  padding: 12px 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
  max-height: calc(100vh - 610px);

  .load-more {
    text-align: center;
    margin-bottom: 8px;
    flex-shrink: 0;
  }

  .chat-scrollbar {
    flex: 1;
    min-height: 0;
    overflow: hidden;
  }

  .chat-content {
    padding: 0 20px 12px;
  }
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;

  .message-bubble {
    background: #fff;
    padding: 10px 14px;
    border-radius: 12px;
    max-width: 70%;
    box-shadow: 0 2px 8px rgba(15, 76, 129, 0.06);
    position: relative;
  }

  .message-text {
    white-space: pre-wrap;
    line-height: 1.6;
    color: #0f2b46;
  }

  .message-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 6px;
    gap: 8px;
  }

  .message-time {
    font-size: 12px;
    color: #8b99ad;
    flex: 1;
  }

  .message-recall-btn {
    padding: 2px 8px;
    font-size: 11px;
    color: #8b99ad;
    opacity: 0;
    transition: opacity 0.2s ease;
    flex-shrink: 0;

    :deep(.el-button__inner) {
      background: transparent !important;
    }

    &:hover {
      background: transparent !important;
    }

    &:focus {
      background: transparent !important;
    }
  }

  .message-bubble:hover .message-recall-btn {
    opacity: 1;
  }

  &.outgoing {
    flex-direction: row-reverse;

    .message-bubble {
      background: linear-gradient(135deg, #5aa9ff, #2f7bff);
      color: #fff;
      box-shadow: 0 4px 14px rgba(64, 158, 255, 0.28);
    }

    .message-text,
    .message-time {
      color: rgba(255, 255, 255, 0.9);
    }

    .message-footer {
      .message-time {
        text-align: left;
      }
    }

    .message-recall-btn {
      color: rgba(255, 255, 255, 0.8);

      :deep(.el-button__inner) {
        background: transparent !important;
      }

      &:hover {
        background: transparent !important;
      }

      &:focus {
        background: transparent !important;
      }
    }
  }

  &.recalled {
    .message-bubble {
      background: #f5f5f5;
      opacity: 0.7;
    }

    .message-text {
      color: #999;
      font-style: italic;
    }

    .message-time {
      color: #999;
    }
  }
}

.chat-input {
  border-top: 1px solid #f0f2f7;
  padding-top: 12px;
  :deep(.el-textarea__inner) {
    border-color: #dfe3eb;
    box-shadow: none;
    transition: border-color 0.2s ease;
  }
  :deep(.el-textarea__inner:focus),
  :deep(.el-textarea__inner:focus-visible) {
    border-color: #000;
    box-shadow: none;
  }

  .chat-input-actions {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-top: 8px;
  }
}

:deep(.chat-btn.el-button) {
  background-color: #ffffff;
  border: 1px solid #dfe3eb;
  color: #4a5568;
  transition: all 0.2s ease;
  font-weight: 500;
  padding: 10px 22px !important; // 提高高度并增加左右留白
  font-size: 14px;
  border-radius: 18px;
  height: 38px !important; // 强制统一按钮高度，避免被 size 覆盖
}

:deep(.chat-btn.el-button:hover),
:deep(.chat-btn.el-button:focus) {
  background-color: #f5f7fa;
  border-color: #cfd5dd;
  color: #1f2d3d;
}

:deep(.chat-btn.el-button.is-disabled) {
  background-color: #f7f8fb;
  border-color: #e4e7ed;
  color: #c0c4cc;
}

@media (max-width: 1200px) {
  .message-layout {
    grid-template-columns: 1fr;
  }

  .conversation-panel {
    max-height: 320px;
  }
}

// 删除会话对话框按钮样式 - 白灰色系
:deep(.delete-conversation-dialog) {
  .el-message-box__btns {
    .el-button {
      border-radius: 8px;
      font-weight: 500;
      padding: 10px 20px;
    }

    // 取消按钮 - 白色背景，灰色边框和文字
    .el-button--default {
      background-color: #ffffff;
      border-color: #d1d5db;
      color: #6b7280;

      &:hover {
        background-color: #f9fafb;
        border-color: #9ca3af;
        color: #4b5563;
      }

      &:focus {
        background-color: #f9fafb;
        border-color: #9ca3af;
        color: #4b5563;
      }
    }

    // 删除按钮 - 灰色背景，白色文字
    .el-button--primary {
      background-color: #9ca3af;
      border-color: #9ca3af;
      color: #ffffff;

      &:hover {
        background-color: #6b7280;
        border-color: #6b7280;
      }

      &:focus {
        background-color: #6b7280;
        border-color: #6b7280;
      }

      &:active {
        background-color: #4b5563;
        border-color: #4b5563;
      }
    }
  }
}
</style>

