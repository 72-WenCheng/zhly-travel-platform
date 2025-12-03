<template>
  <div class="plan-detail-page">
    <!-- 返回按钮 -->
    <BackButton />
    

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="50"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <!-- 攻略详情 -->
    <div v-else-if="plan" class="detail-content">
      <!-- 图片预览区块（用于审核）- 始终显示 -->
      <el-card class="info-card image-preview-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Picture /></el-icon>
            <span>图片预览{{ images && images.length > 0 ? `（${images.length}张）` : '' }} - 审核参考</span>
          </div>
        </template>
        
        <!-- 有图片时显示 -->
        <div v-if="images && images.length > 0" class="preview-images-grid">
          <el-image
            v-for="(img, index) in images"
            :key="index"
            :src="img"
            fit="cover"
            class="preview-image"
            :preview-src-list="images"
            :initial-index="index"
            :preview-teleported="true"
            :z-index="3000"
          >
            <template #error>
              <div class="image-error">
                <el-icon><PictureFilled /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
        </div>
        
        <!-- 无图片时显示提示 -->
        <div v-else class="no-images">
          <el-icon :size="60"><PictureFilled /></el-icon>
          <p class="tip-title">该攻略暂无图片</p>
          <p class="tip-desc">建议作者补充图片以提升攻略质量</p>
        </div>
      </el-card>

      <!-- 基本信息卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><InfoFilled /></el-icon>
            <span>基本信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="攻略标题" :span="2">
            <div class="title-text">{{ plan.title }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="作者">
            {{ plan.authorName || plan.author || '未知作者' }}
          </el-descriptions-item>
          <el-descriptions-item label="目的地">
            <el-tag type="primary">{{ plan.destination }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="旅行天数">
            {{ plan.days }} 天
          </el-descriptions-item>
          <el-descriptions-item label="预算">
            ¥{{ plan.budget }}
          </el-descriptions-item>
          <el-descriptions-item label="出行人数">
            {{ plan.personCount || '-' }} 人
          </el-descriptions-item>
          <el-descriptions-item label="旅行类型">
            {{ getTravelTypeText(plan.travelType) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(plan.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDate(plan.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 快速信息卡片 -->
      <el-card class="info-card" shadow="hover" v-if="plan.bestSeason || plan.suitableFor || plan.difficultyLevel">
        <template #header>
          <div class="card-header">
            <el-icon><Lightning /></el-icon>
            <span>快速信息</span>
          </div>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="最佳季节" v-if="plan.bestSeason">
            {{ plan.bestSeason }}
          </el-descriptions-item>
          <el-descriptions-item label="适合人群" v-if="plan.suitableFor">
            {{ plan.suitableFor }}
          </el-descriptions-item>
          <el-descriptions-item label="难度等级" v-if="plan.difficultyLevel">
            <el-rate v-model="plan.difficultyLevel" disabled show-score text-color="#ff9900" />
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 攻略描述 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Memo /></el-icon>
            <span>攻略描述</span>
          </div>
        </template>
        <div v-if="plan.description" class="description-content">
          {{ plan.description }}
        </div>
        <div v-else class="empty-content">
          <el-icon><InfoFilled /></el-icon>
          <span>暂无攻略描述</span>
        </div>
      </el-card>

      <!-- 行程安排 -->
      <el-card class="content-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-icon><Calendar /></el-icon>
            <span>行程安排</span>
          </div>
        </template>
        <div v-if="itinerary && itinerary.length > 0" class="itinerary-list">
          <div
            v-for="(day, index) in itinerary"
            :key="index"
            class="itinerary-day">
            <div class="day-header">
              <div class="day-number">Day {{ index + 1 }}</div>
              <div class="day-title">{{ day.title || day.day || `第${index + 1}天` }}</div>
            </div>
            <div class="day-content">
              <!-- 支持 items 格式（创建/编辑页面使用的格式） -->
              <div v-if="day.items && Array.isArray(day.items) && day.items.length > 0" class="activities-list">
                <div v-for="(item, idx) in day.items" :key="idx" class="activity-item">
                  <div class="activity-time">{{ formatTime(item.time) }}</div>
                  <div class="activity-content">
                    <div class="activity-name">{{ item.activity || item.name || '未命名活动' }}</div>
                    <div v-if="item.location" class="activity-location">
                      <el-icon><Location /></el-icon>
                      {{ item.location }}
                    </div>
                    <div v-if="item.description" class="activity-desc">{{ item.description }}</div>
                  </div>
                </div>
              </div>
              <!-- 支持 activities 格式（旧数据格式） -->
              <div v-else-if="day.activities && Array.isArray(day.activities) && day.activities.length > 0" class="activities-list">
                <div v-for="(activity, idx) in day.activities" :key="idx" class="activity-item">
                  <div class="activity-time">{{ formatTime(activity.time) }}</div>
                  <div class="activity-content">
                    <div class="activity-name">{{ activity.activity || activity.name || '未命名活动' }}</div>
                    <div v-if="activity.location" class="activity-location">
                      <el-icon><Location /></el-icon>
                      {{ activity.location }}
                    </div>
                    <div v-if="activity.description" class="activity-desc">{{ activity.description }}</div>
                  </div>
                </div>
              </div>
              <!-- 支持 spots 格式 -->
              <div v-else-if="day.spots && day.spots.length > 0" class="spots-list">
                <div v-for="(spot, idx) in day.spots" :key="idx" class="spot-item">
                  <div class="spot-icon">
                    <el-icon><Location /></el-icon>
                  </div>
                  <div class="spot-info">
                    <div class="spot-name">{{ spot.name }}</div>
                    <div v-if="spot.description" class="spot-desc">{{ spot.description }}</div>
                    <div class="spot-meta">
                      <span v-if="spot.duration">
                        <el-icon><Clock /></el-icon>
                        {{ spot.duration }}
                      </span>
                      <span v-if="spot.cost">
                        <el-icon><Wallet /></el-icon>
                        ¥{{ spot.cost }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="day-description">
                {{ day.description || day.content || '暂无行程安排' }}
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-content">
          <el-icon><InfoFilled /></el-icon>
          <span>暂无行程安排</span>
        </div>
      </el-card>

      <!-- 费用预算 -->
      <el-card class="content-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-icon><Wallet /></el-icon>
            <span>费用预算</span>
          </div>
        </template>
        <div class="budget-section">
          <div class="budget-summary">
            <div class="budget-total">
              <span class="label">预计总费用：</span>
              <span class="value">¥{{ totalCost }}</span>
          </div>
            <div class="budget-per-person">
              <span class="label">人均费用：</span>
              <span class="value">¥{{ perPersonCost }}</span>
              <span class="person-count">（{{ plan.personCount || 1 }}人）</span>
        </div>
          </div>
          <div v-if="costDetail && costDetail.length > 0" class="cost-breakdown">
            <div
              v-for="(item, index) in costDetail"
              :key="index"
              class="cost-item">
              <span class="cost-name">{{ item.category || item.name || item.item || '未命名' }}</span>
              <span class="cost-value">¥{{ item.amount || item.value || 0 }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 注意事项 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><WarnTriangleFilled /></el-icon>
            <span>注意事项</span>
          </div>
        </template>
        <div v-if="plan.notice" class="notice-content">
          {{ plan.notice }}
        </div>
        <div v-else class="empty-content">
          <el-icon><InfoFilled /></el-icon>
          <span>暂无注意事项</span>
        </div>
      </el-card>

      <!-- 标签 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><PriceTag /></el-icon>
            <span>标签</span>
          </div>
        </template>
        <div v-if="tags && tags.length > 0" class="tags-container">
          <el-tag v-for="tag in tags" :key="tag" type="info" effect="plain">
            {{ tag }}
          </el-tag>
        </div>
        <div v-else class="empty-content">
          <el-icon><InfoFilled /></el-icon>
          <span>暂无标签</span>
        </div>
      </el-card>

      <!-- 审核信息（如果有拒绝原因） -->
      <el-card class="info-card reject-card" shadow="hover" v-if="plan.auditStatus === 2 && plan.rejectReason">
        <template #header>
          <div class="card-header">
            <el-icon><WarningFilled /></el-icon>
            <span>拒绝原因</span>
          </div>
        </template>
        <el-alert
          type="error"
          :closable="false"
          show-icon
        >
          <template #title>
            <div class="reject-reason">{{ plan.rejectReason }}</div>
          </template>
        </el-alert>
      </el-card>

      <!-- 底部操作按钮 - 待审核状态显示审核按钮 -->
      <div class="bottom-actions" v-if="plan && plan.auditStatus === 0">
        <el-button type="success" size="large" @click="handleApprove">
          <el-icon><Check /></el-icon>
          审核通过
        </el-button>
        <el-button type="danger" size="large" @click="handleReject">
          <el-icon><Close /></el-icon>
          审核拒绝
        </el-button>
      </div>
    </div>

    <!-- 审核拒绝对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="审核拒绝"
      width="700px"
      :close-on-click-modal="false"
      class="reject-dialog"
    >
      <div class="reject-dialog-content">
        <!-- 攻略信息提示 -->
        <el-alert
          :title="`正在拒绝攻略：${plan?.title || ''}`"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />

        <el-form :model="rejectForm" label-width="110px" label-position="top">
          <!-- 拒绝类型 -->
          <el-form-item label="拒绝类型" required>
            <el-select 
              v-model="rejectForm.type" 
              placeholder="请选择拒绝类型" 
              style="width: 100%"
              size="large"
              @change="onRejectTypeChange"
            >
              <el-option label="📝 内容质量问题" value="content" />
              <el-option label="🚫 内容违规" value="violation" />
              <el-option label="❌ 信息虚假" value="fake" />
              <el-option label="🖼️ 图片问题" value="image" />
              <el-option label="💰 费用信息不准确" value="cost" />
              <el-option label="📅 行程安排不合理" value="schedule" />
              <el-option label="⚠️ 涉及敏感信息" value="sensitive" />
              <el-option label="🔧 格式错误" value="format" />
              <el-option label="📋 其他原因" value="other" />
            </el-select>
          </el-form-item>

          <!-- 常用理由模板 -->
          <el-form-item label="快速选择常用理由（可选）" v-if="commonReasons.length > 0">
            <div class="reason-templates">
              <el-tag
                v-for="(reason, index) in commonReasons"
                :key="index"
                class="reason-tag"
                type="info"
                effect="plain"
                @click="selectReasonTemplate(reason)"
              >
                {{ reason }}
              </el-tag>
            </div>
          </el-form-item>

          <!-- 详细说明 -->
          <el-form-item label="详细说明" required>
            <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="8"
              placeholder="请详细说明拒绝的原因，帮助作者了解问题所在并进行修改...&#10;&#10;建议包含：&#10;1. 具体问题描述&#10;2. 修改建议&#10;3. 参考标准"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <!-- 修改建议 -->
          <el-form-item label="修改建议（可选）">
            <el-input
              v-model="rejectForm.suggestion"
              type="textarea"
              :rows="3"
              placeholder="给出具体的修改建议，帮助作者更好地完善攻略..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>

        <!-- 拒绝提示 -->
        <el-alert
          title="温馨提示"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <div style="font-size: 13px; line-height: 1.6;">
              • 请详细说明拒绝原因，帮助作者改进<br>
              • 拒绝后作者可以修改并重新提交<br>
              • 建议提供具体的修改方向
            </div>
          </template>
        </el-alert>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="rejectDialogVisible = false">取消</el-button>
          <el-button 
            type="danger" 
            size="large" 
            @click="confirmReject" 
            :loading="submitting"
            :disabled="!rejectForm.type || !rejectForm.reason || rejectForm.reason.length < 20"
          >
            <el-icon><Close /></el-icon>
            确定拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Check,
  Close,
  InfoFilled,
  Lightning,
  Memo,
  Reading,
  Calendar,
  Money,
  WarnTriangleFilled,
  PriceTag,
  Picture,
  DataLine,
  View,
  StarFilled,
  ChatDotRound,
  WarningFilled,
  Loading,
  PictureFilled,
  Location,
  Clock,
  Wallet
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { formatDateTime } from '@/utils'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const plan = ref<any>(null)
const rejectDialogVisible = ref(false)
const submitting = ref(false)
const rejectForm = ref({
  type: '',
  reason: '',
  suggestion: ''
})

// 常用理由模板（根据类型动态显示）
const commonReasons = ref<string[]>([])

// 拒绝理由模板库
const reasonTemplates: Record<string, string[]> = {
  content: [
    '内容过于简单，缺少详细的行程安排',
    '攻略描述不够清晰，缺少实用信息',
    '缺少必要的旅行提示和注意事项',
    '内容质量较低，建议补充更多细节'
  ],
  violation: [
    '包含违法违规内容',
    '含有政治敏感信息',
    '含有不当言论或歧视性内容',
    '包含广告或营销信息'
  ],
  fake: [
    '费用预算与实际严重不符',
    '景点信息描述不准确',
    '行程时间安排不合理',
    '包含虚假宣传内容'
  ],
  image: [
    '图片质量不佳，建议更换高清图片',
    '图片与内容不符',
    '缺少关键景点的实拍图片',
    '图片含有水印或广告信息'
  ],
  cost: [
    '费用明细不完整',
    '预算金额明显偏离实际',
    '缺少必要的费用说明',
    '费用项目分类不清晰'
  ],
  schedule: [
    '行程安排过于紧凑',
    '行程规划不合理，建议调整',
    '天数与行程内容不匹配',
    '缺少休息时间安排'
  ],
  sensitive: [
    '涉及敏感地区或话题',
    '包含不当的政治观点',
    '可能引起争议的内容',
    '违反平台规范的表述'
  ],
  format: [
    '格式混乱，建议重新排版',
    '缺少段落分隔，不易阅读',
    '标点符号使用不规范',
    '存在大量错别字'
  ],
  other: [
    '攻略重复，已有类似内容',
    '标题与内容不符',
    '信息过时，需要更新',
    '不符合攻略发布规范'
  ]
}

// 选择拒绝类型时更新常用理由
const onRejectTypeChange = (type: string) => {
  commonReasons.value = reasonTemplates[type] || []
  // 清空之前的理由（如果有）
  if (rejectForm.value.reason === '') {
    // 不清空，允许用户保留已输入的内容
  }
}

// 选择理由模板
const selectReasonTemplate = (reason: string) => {
  if (rejectForm.value.reason) {
    // 如果已有内容，追加
    rejectForm.value.reason += '\n' + reason
  } else {
    // 如果没有内容，直接设置
    rejectForm.value.reason = reason
  }
}

// 解析行程
const itinerary = computed(() => {
  if (!plan.value?.itinerary) return []
  try {
    return typeof plan.value.itinerary === 'string'
      ? JSON.parse(plan.value.itinerary)
      : plan.value.itinerary
  } catch {
    return []
  }
})

// 解析费用明细
const costDetail = computed(() => {
  if (!plan.value?.costDetail) return []
  try {
    return typeof plan.value.costDetail === 'string'
      ? JSON.parse(plan.value.costDetail)
      : plan.value.costDetail
  } catch {
    return []
  }
})

// 计算总费用
const totalCost = computed(() => {
  if (!costDetail.value || costDetail.value.length === 0) {
    return plan.value?.budget || 0
  }
  return costDetail.value.reduce((sum, item) => {
    return sum + (Number(item.amount || item.value || 0))
  }, 0)
})

// 计算人均费用
const perPersonCost = computed(() => {
  const total = totalCost.value
  const personCount = plan.value?.personCount || 1
  return personCount > 0 ? Math.round(total / personCount) : total
})

// 解析标签
const tags = computed(() => {
  if (!plan.value?.tags) return []
  return typeof plan.value.tags === 'string'
    ? plan.value.tags.split(',').filter(Boolean)
    : plan.value.tags
})

// 解析图片
const images = computed(() => {
  if (!plan.value?.images) return []
  return typeof plan.value.images === 'string'
    ? plan.value.images.split(',').filter(Boolean)
    : plan.value.images
})

// 获取状态文本
const getStatusText = (plan: any) => {
  const auditStatus = Number(plan.auditStatus)
  const status = Number(plan.status)
  
  if (auditStatus === 0) return '待审核'
  if (auditStatus === 1 && status === 1) return '已发布'
  if (auditStatus === 1 && (status === 0 || status === 2)) return '已下架'
  if (auditStatus === 2) return '已驳回'
  return '草稿'
}

// 获取状态类型
const getStatusType = (plan: any) => {
  const auditStatus = Number(plan.auditStatus)
  const status = Number(plan.status)
  
  if (auditStatus === 0) return 'warning'
  if (auditStatus === 1 && status === 1) return 'success'
  if (auditStatus === 1 && (status === 0 || status === 2)) return 'info'
  if (auditStatus === 2) return 'danger'
  return ''
}

// 获取旅行类型文本
const getTravelTypeText = (type: number) => {
  const types: Record<number, string> = {
    1: '自由行',
    2: '跟团游',
    3: '自驾游',
    4: '户外徒步'
  }
  return types[type] || '未知'
}

// 格式化日期（使用统一的格式化函数）
const formatDate = (dateStr: string) => {
  return formatDateTime(dateStr)
}

// 格式化内容
const formatContent = (content: string) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 格式化时间显示（行程安排中的时间）
const formatTime = (time: any) => {
  if (!time) return '待定'
  
  // 如果是字符串，使用 formatDateTime 处理（移除 T 字母等）
  if (typeof time === 'string') {
    // 如果是完整的日期时间格式（包含日期和时间），格式化后提取时间部分
    const formatted = formatDateTime(time)
    // 如果包含空格，说明是日期时间格式，提取时间部分（HH:mm:ss）
    if (formatted.includes(' ')) {
      const timePart = formatted.split(' ')[1]
      // 如果时间部分包含秒，只取时分，否则返回完整时间部分
      if (timePart && timePart.includes(':')) {
        const parts = timePart.split(':')
        // 返回 HH:mm 格式（不包含秒）
        return `${parts[0]}:${parts[1]}`
      }
      return timePart
    }
    // 如果已经是时间格式（HH:mm），直接返回
    return formatted
  }
  
  return time.toString()
}

// 加载攻略详情
const loadPlanDetail = async () => {
  loading.value = true
  try {
    const planId = route.params.id
    const result = await request.get(`/travel-plan/${planId}`)
    
    if (result.code === 200) {
      const data = result.data
      
      // 完整数据映射，确保所有字段正确显示
      plan.value = {
        id: data.id,
        title: data.title || '未命名攻略',
        description: data.description || '',
        content: data.content || '',
        destination: data.destination || '',
        days: data.days || 1,
        budget: data.budget || 0,
        personCount: data.personCount || 1,
        travelType: data.travelType || data.type || 1,
        bestSeason: data.bestSeason || '',
        suitableFor: data.suitableFor || '',
        difficultyLevel: data.difficultyLevel || 1,
        notice: data.notice || '',
        tags: data.tags || '',
        images: data.images || '',
        itinerary: data.itinerary || '',
        costDetail: data.costDetail || '',
        authorId: data.authorId,
        authorName: data.authorName || data.author || '匿名用户', // 支持author和authorName
        authorAvatar: data.authorAvatar || '',
        viewCount: data.viewCount || 0,
        likeCount: data.likeCount || 0,
        commentCount: data.commentCount || 0,
        createTime: data.createTime || '',
        updateTime: data.updateTime || '',
        publishTime: data.publishTime || '',
        status: data.status,
        auditStatus: data.auditStatus,
        rejectReason: data.rejectReason || ''
      }
      
      console.log('=== 管理端攻略详情加载完成 ===')
      console.log('完整数据:', plan.value)
      console.log('审核状态信息:', {
        auditStatus: plan.value.auditStatus,
        auditStatusType: typeof plan.value.auditStatus,
        auditStatusValue: data.auditStatus,
        shouldShowButtons: plan.value.auditStatus === 0
      })
      console.log('作者信息:', {
        authorId: plan.value.authorId,
        authorName: plan.value.authorName,
        author: data.author
      })
      console.log('图片数据:', {
        images: plan.value.images,
        imagesType: typeof plan.value.images,
        parsedImages: images.value
      })
    } else {
      ElMessage.error(result.message || '加载失败')
      router.back()
    }
  } catch (error: any) {
    ElMessage.error('加载失败: ' + (error.message || '未知错误'))
    router.back()
  } finally {
    loading.value = false
  }
}

// 审核通过
const handleApprove = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要通过此攻略的审核吗？通过后将自动发布。',
      '审核通过',
      {
        confirmButtonText: '确定通过',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    submitting.value = true
    const result = await request.post(`/travel-plan/${plan.value.id}/audit-with-log`, null, {
      params: {
        auditStatus: 1,
        rejectReason: null
      }
    })

    if (result.code === 200) {
      ElMessage.success('审核通过成功')
      router.push('/home/admin/plans')
    } else {
      ElMessage.error(result.message || '审核失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败: ' + (error.message || '未知错误'))
    }
  } finally {
    submitting.value = false
  }
}

// 打开拒绝对话框
const handleReject = () => {
  // 重置表单
  rejectForm.value = {
    type: '',
    reason: '',
    suggestion: ''
  }
  commonReasons.value = []
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  // 验证
  if (!rejectForm.value.type) {
    ElMessage.warning('请选择拒绝类型')
    return
  }
  
  if (!rejectForm.value.reason.trim()) {
    ElMessage.warning('请填写详细说明')
    return
  }
  
  if (rejectForm.value.reason.trim().length < 20) {
    ElMessage.warning('详细说明至少需要20个字符')
    return
  }

  try {
    submitting.value = true
    
    // 组装完整的拒绝原因
    let fullReason = `【拒绝类型】${getRejectTypeLabel(rejectForm.value.type)}\n\n`
    fullReason += `【详细说明】\n${rejectForm.value.reason}`
    
    if (rejectForm.value.suggestion) {
      fullReason += `\n\n【修改建议】\n${rejectForm.value.suggestion}`
    }
    
    const result = await request.post(`/travel-plan/${plan.value.id}/audit-with-log`, null, {
      params: {
        auditStatus: 2,
        rejectReason: fullReason
      }
    })

    if (result.code === 200) {
      ElMessage.success('审核拒绝成功')
      rejectDialogVisible.value = false
      router.push('/home/admin/plans')
    } else {
      ElMessage.error(result.message || '审核失败')
    }
  } catch (error: any) {
    ElMessage.error('审核失败: ' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// 获取拒绝类型标签
const getRejectTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    content: '内容质量问题',
    violation: '内容违规',
    fake: '信息虚假',
    image: '图片问题',
    cost: '费用信息不准确',
    schedule: '行程安排不合理',
    sensitive: '涉及敏感信息',
    format: '格式错误',
    other: '其他原因'
  }
  return labels[type] || type
}

onMounted(() => {
  loadPlanDetail()
})
</script>

<style scoped lang="scss">
.plan-detail-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid #e5e7eb;
  box-shadow: none;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .header-icon {
      width: 60px;
      height: 60px;
      background: #f2f3f5;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #5f6673;
      border: 1px solid #e4e6eb;
    }

    .header-title {
      h1 {
        margin: 0;
        font-size: 28px;
        font-weight: 600;
        color: #2f3542;
        margin-bottom: 4px;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: #8a8f99;
      }
    }
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  
  p {
    margin-top: 16px;
    font-size: 16px;
    color: #909399;
  }
}

.detail-content {
  max-width: 1200px;
  margin: 0 auto;
}

.status-badge-container {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.info-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.title-text {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.description-content,
.notice-content {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.content-text {
  font-size: 15px;
  line-height: 2;
  color: #606266;
  padding: 12px;
}

.content-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

// 行程列表 - 平衡设计
.itinerary-list {
  .itinerary-day {
    margin-bottom: 32px;
    padding: 24px;
    background: #ffffff;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    &:last-child {
      margin-bottom: 0;
    }

    .day-header {
      display: flex;
      align-items: center;
      gap: 14px;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 2px solid #f3f4f6;

      .day-number {
        padding: 8px 16px;
        background: #f2f3f5;
        color: #2f3542;
        border-radius: 8px;
        font-weight: 600;
        font-size: 13px;
        border: 1px solid #e1e4ea;
      }

      .day-title {
        font-size: 19px;
        font-weight: 600;
        color: #1f2937;
      }
    }

      .day-content {
      .day-description {
        font-size: 15px;
        line-height: 1.8;
        color: #4b5563;
        padding: 18px;
        background: #f9fafb;
        border-radius: 10px;
          border-left: 4px solid #e1e4ea;
      }

      .spots-list {
        .spot-item {
          display: flex;
          gap: 16px;
          padding: 18px;
          background: #ffffff;
          border-radius: 10px;
          margin-bottom: 14px;
          border: 1px solid #e5e7eb;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
          transition: all 0.2s ease;

          &:hover {
            border-color: #d8dce3;
            box-shadow: 0 2px 8px rgba(15, 23, 42, 0.06);
          }

          &:last-child {
            margin-bottom: 0;
          }

          .spot-icon {
            width: 44px;
            height: 44px;
            background: #f5f5f7;
            color: #5f6673;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            border: 1px solid #e1e4ea;

            .el-icon {
              font-size: 20px;
            }
          }

          .spot-info {
            flex: 1;

            .spot-name {
              font-size: 16px;
              font-weight: 600;
              color: #111827;
              margin-bottom: 8px;
            }

            .spot-desc {
              font-size: 14px;
              color: #6b7280;
              line-height: 1.6;
              margin-bottom: 12px;
            }

            .spot-meta {
              display: flex;
              gap: 16px;
              font-size: 13px;
              color: #9ca3af;

              span {
                display: flex;
                align-items: center;
                gap: 5px;
                padding: 4px 10px;
                background: #f9fafb;
                border-radius: 6px;

                .el-icon {
                  font-size: 14px;
                  color: #9096a1;
                }
              }
            }
          }
        }
      }

      .activities-list {
        position: relative;
        padding-left: 36px;

        // 时间线
        &::before {
          content: '';
          position: absolute;
          left: 14px;
          top: 0;
          bottom: 0;
          width: 2px;
          background: #e1e4ea;
        }

        .activity-item {
          position: relative;
          display: flex;
          gap: 16px;
          padding: 18px;
          background: #ffffff;
          border: 1px solid #e5e7eb;
          border-radius: 10px;
          margin-bottom: 18px;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
          transition: all 0.2s ease;

          &:hover {
            border-color: #d8dce3;
            box-shadow: 0 2px 8px rgba(15, 23, 42, 0.06);
          }

          &:last-child {
            margin-bottom: 0;
          }

          // 时间线节点
          &::before {
            content: '';
            position: absolute;
            left: -30px;
            top: 22px;
            width: 12px;
            height: 12px;
            background: #d0d4db;
            border: 3px solid white;
            border-radius: 50%;
            box-shadow: 0 0 0 2px #e5e7eb;
            z-index: 1;
          }

          .activity-time {
            min-width: 80px;
            font-size: 13px;
            font-weight: 600;
            color: #2f3542;
            padding: 8px 14px;
            background: #f2f3f5;
            border-radius: 8px;
            text-align: center;
            flex-shrink: 0;
            height: fit-content;
            border: 1px solid #e1e4ea;
          }

          .activity-content {
            flex: 1;

            .activity-name {
              font-size: 16px;
              font-weight: 600;
              color: #111827;
              margin-bottom: 10px;
              line-height: 1.5;
            }

            .activity-location {
              display: inline-flex;
              align-items: center;
              gap: 6px;
              font-size: 14px;
              color: #6b7280;
              padding: 6px 12px;
              background: #f3f4f6;
              border-radius: 8px;
              margin-bottom: 10px;
              border: 1px solid #e5e7eb;

              .el-icon {
                font-size: 14px;
                color: #9096a1;
              }
            }

            .activity-desc {
              font-size: 14px;
              color: #6b7280;
              line-height: 1.7;
              padding-top: 12px;
              border-top: 1px solid #f3f4f6;
              margin-top: 10px;
            }
          }
        }
      }
    }
  }
}

// 费用预算 - 平衡设计
.budget-section {
  .budget-summary {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 28px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .budget-total,
    .budget-per-person {
      padding: 28px;
      background: #ffffff;
      border-radius: 12px;
      text-align: center;
      border: 1px solid #e5e7eb;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.03);

      .label {
        display: block;
        font-size: 14px;
        color: #6b7280;
        margin-bottom: 14px;
        font-weight: 500;
      }

      .value {
        display: block;
        font-size: 38px;
        font-weight: 700;
        margin-bottom: 10px;
        letter-spacing: -0.5px;
      }

      .person-count {
        font-size: 13px;
        color: #9ca3af;
        font-weight: 400;
      }
    }

    .budget-total {
      border-top: 4px solid #dcdfe6;

      .value {
        color: #2f3542;
      }
    }

    .budget-per-person {
      border-top: 4px solid #dcdfe6;

      .value {
        color: #2f3542;
      }
    }
  }

  .cost-breakdown {
    background: #ffffff;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
    overflow: hidden;

    .cost-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 18px 22px;
      border-bottom: 1px solid #f3f4f6;

      &:last-child {
        border-bottom: none;
      }

      .cost-name {
        font-size: 15px;
        font-weight: 500;
        color: #374151;
        display: flex;
        align-items: center;
        gap: 12px;

        &::before {
          content: '';
          width: 8px;
          height: 8px;
          background: #d0d4db;
          border-radius: 50%;
        }
      }

      .cost-value {
        font-size: 17px;
        font-weight: 600;
        color: #2f3542;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      }
    }
  }
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.cover-image {
  width: 100%;
  max-height: 500px;
  border-radius: 8px;
}

/* 顶部图片预览区块 */
.image-preview-card {
  background: #fff;
  color: #303133;
  margin-bottom: 24px;

  :deep(.el-card__header) {
    background: #fff;
    border-bottom: 1px solid #f0f0f0;
    padding: 16px 20px;

    .card-header {
      color: #2f3542;
      font-weight: 600;
      font-size: 16px;
    }
  }

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.preview-images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;

  .preview-image {
    width: 100%;
    height: 160px;
    border-radius: 12px;
    cursor: pointer;
    box-shadow: none;

    :deep(img) {
      border-radius: 12px;
    }
  }

.image-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    background: #f5f6f8;
    border-radius: 12px;
    color: #909399;
    font-size: 13px;

    .el-icon {
      font-size: 32px;
      margin-bottom: 8px;
    }
  }
}

/* 图片预览窗口样式优化 */
:deep(.el-image-viewer__wrapper) {
  z-index: 3000 !important;
  
  .el-image-viewer__mask {
    background: rgba(0, 0, 0, 0.8);
    backdrop-filter: blur(4px);
  }
  
  .el-image-viewer__close {
    color: #fff;
    font-size: 28px;
    width: 50px;
    height: 50px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transition: all 0.3s;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: scale(1.1);
    }
  }
  
  .el-image-viewer__canvas {
    img {
      max-width: 90vw;
      max-height: 90vh;
      border-radius: 8px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
    }
  }
  
  .el-image-viewer__actions {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 24px;
    padding: 8px 16px;
    
    .el-image-viewer__actions__inner {
      .el-image-viewer__actions__item {
        color: #fff;
        font-size: 20px;
        width: 40px;
        height: 40px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        transition: all 0.3s;
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
          transform: scale(1.1);
        }
      }
    }
  }
  
  .el-image-viewer__next,
  .el-image-viewer__prev {
    width: 50px;
    height: 50px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transition: all 0.3s;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: scale(1.1);
    }
    
    .el-icon {
      color: #fff;
      font-size: 24px;
    }
  }
}

/* 无图片提示 */
.no-images {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
    color: #606266;
  text-align: center;

  .el-icon {
    color: #c0c4cc;
    margin-bottom: 16px;
  }

  .tip-title {
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 8px 0;
      color: #303133;
  }

  .tip-desc {
    font-size: 14px;
    margin: 0;
      color: #909399;
  }
}

/* 空内容提示 */
.empty-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  color: #909399;
  font-size: 14px;

  .el-icon {
    font-size: 20px;
  }
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;

  .grid-image {
    width: 100%;
    height: 200px;
    border-radius: 8px;
    cursor: pointer;
    transition: transform 0.3s;

    &:hover {
      transform: scale(1.05);
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #fff;
    border-radius: 12px;
    border: 1px solid #e5e7eb;

    .stat-info {
      flex: 1;

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 4px;
      }

      .stat-number {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
}

.reject-card {
  border: 2px solid #f56c6c;

  .reject-reason {
    font-size: 15px;
    line-height: 1.6;
  }
}

.bottom-actions {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding: 32px 0;
  margin-top: 32px;
  margin-bottom: 32px;
  border-top: 2px dashed #dcdfe6;
  background: #fafafa;
  border-radius: 8px;
  position: sticky;
  bottom: 0;
  z-index: 100;
  
  .el-button {
    min-width: 160px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border: 1px solid #dcdfe6;
    background: #fff;
    color: #4b5563;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    
    &:hover {
      border-color: #c0c4cc;
      transform: translateY(-2px);
      box-shadow: 0 4px 10px rgba(4, 9, 20, 0.08);
    }
    
    &.el-button--success {
      color: #2f9b46;
    }
    
    &.el-button--danger {
      color: #c0392b;
    }
  }
}

/* 审核拒绝对话框样式 */
.reject-dialog {
  :deep(.el-dialog__header) {
    background: #fff;
    border-bottom: 1px solid #f0f0f0;
    padding: 20px;
    
    .el-dialog__title {
      color: #303133;
      font-size: 20px;
      font-weight: bold;
    }
    
    .el-dialog__headerbtn .el-dialog__close {
      color: #909399;
      font-size: 20px;
      
      &:hover {
        color: #606266;
      }
    }
  }
  
  :deep(.el-dialog__body) {
    padding: 24px;
    max-height: 70vh;
    overflow-y: auto;
  }
  
  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid #f0f0f0;
  }
}

.reject-dialog-content {
  .reason-templates {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    
    .reason-tag {
      cursor: pointer;
      transition: all 0.3s;
      padding: 8px 16px;
      font-size: 13px;
      
      &:hover {
        background: #f2f3f5;
        color: #303133;
        border-color: #e1e4ea;
        transform: translateY(-2px);
        box-shadow: none;
      }
    }
  }
  
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #303133;
    font-size: 14px;
  }
  
  :deep(.el-select) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 0 0 1px #c0c4cc inset;
      }
      
      &.is-focus {
        box-shadow: 0 0 0 1px #409eff inset;
      }
    }
  }
  
  :deep(.el-textarea__inner) {
    font-family: inherit;
    line-height: 1.6;
    
    &:focus {
      border-color: #409eff;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

