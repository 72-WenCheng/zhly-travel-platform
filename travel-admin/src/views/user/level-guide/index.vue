<template>
  <div class="level-guide-page">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><TrophyBase /></el-icon>
        </div>
        <div class="header-text">
          <h2>升级指南</h2>
          <p class="page-desc">了解等级体系，快速提升你的旅行者等级</p>
        </div>
      </div>
    </div>

    <!-- 当前等级卡片 -->
    <el-card class="current-level-card">
      <div class="level-status">
        <div class="level-icon" :style="{ background: currentLevelGradient }">
          <el-icon :size="48">
            <User v-if="currentLevelIcon === 'User'" />
            <Aim v-else-if="currentLevelIcon === 'Aim'" />
            <Medal v-else-if="currentLevelIcon === 'Medal'" />
            <Trophy v-else-if="currentLevelIcon === 'Trophy'" />
            <StarFilled v-else-if="currentLevelIcon === 'StarFilled'" />
            <TrophyBase v-else-if="currentLevelIcon === 'TrophyBase'" />
            <Medal v-else />
          </el-icon>
        </div>
        <div class="level-info">
          <div class="level-name" :style="{ color: currentLevelColor }">{{ currentLevelName }}</div>
          <div class="level-desc">当前等级</div>
        </div>
        <div class="points-info">
          <div class="points-value">{{ currentPoints }}</div>
          <div class="points-label">总积分</div>
        </div>
      </div>
      
      <!-- 升级进度 -->
      <div class="level-progress" v-if="nextLevel">
        <div class="progress-info">
          <span>距离下一级（{{ nextLevel.name }}）</span>
          <span class="progress-text" :class="{ 'already-reached': levelProgress >= 100 }">
            {{ levelProgress >= 100 ? '已达成' : `${nextLevel.points - currentPoints} 积分` }}
          </span>
        </div>
        <el-progress 
          :percentage="Math.min(100, levelProgress)" 
          :color="progressColor"
          :stroke-width="12"
        />
        <div class="progress-percentage">{{ Math.min(100, Math.round(levelProgress * 10) / 10) }}%</div>
      </div>
      <div class="max-level-tip" v-else>
        <el-icon><Trophy /></el-icon>
        <span>恭喜！您已达到最高等级</span>
      </div>
    </el-card>

    <!-- 等级列表 -->
    <div class="section-title">
      <el-icon><TrendCharts /></el-icon>
      <span>等级体系</span>
    </div>
    <div class="levels-grid">
      <el-card 
        v-for="level in levels" 
        :key="level.code"
        class="level-card"
        :class="{ 'current': level.code === currentLevelCode, 'locked': level.code > currentLevelCode }"
      >
        <div class="level-badge" :style="{ background: level.gradient }">
          <el-icon :size="40">
            <User v-if="level.icon === 'User'" />
            <Aim v-else-if="level.icon === 'Aim'" />
            <Medal v-else-if="level.icon === 'Medal'" />
            <Trophy v-else-if="level.icon === 'Trophy'" />
            <StarFilled v-else-if="level.icon === 'StarFilled'" />
            <TrophyBase v-else-if="level.icon === 'TrophyBase'" />
            <Medal v-else />
          </el-icon>
          <div class="level-badge-name">{{ level.name }}</div>
        </div>
        <div class="level-details">
          <div class="level-points">{{ level.points }} 积分</div>
          <el-divider />
          <div class="level-perks">
            <div class="perk-item">
              <span>{{ level.canPost ? `发布攻略 ${level.postLimit}篇/天` : '无发布权限' }}</span>
            </div>
            <div class="perk-item">
              <span>评论 {{ level.commentLimit }}条/天</span>
            </div>
            <div class="perk-item" :class="{ 'clickable': level.couponBenefit }" @click="level.couponBenefit && goToCoupons()">
              <span>{{ level.couponBenefit ? level.couponText : '无优惠券权益' }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 积分获取规则 -->
    <div class="section-title">
      <el-icon><Coin /></el-icon>
      <span>积分获取规则</span>
    </div>
    <el-card class="points-rules-card">
      <div class="rules-grid">
        <div v-for="rule in pointsRules" :key="rule.action" class="rule-item">
          <div class="rule-info">
            <div class="rule-name">{{ rule.name }}</div>
            <div class="rule-desc">{{ rule.desc }}</div>
          </div>
          <div class="rule-points">+{{ rule.points }}</div>
        </div>
      </div>
    </el-card>

    <!-- 升级技巧 -->
    <div class="section-title">
      <el-icon><Promotion /></el-icon>
      <span>快速升级技巧</span>
    </div>
    <el-card class="tips-card">
      <div class="tips-list">
        <div v-for="(tip, index) in tips" :key="index" class="tip-item" :class="{ 'clickable': tip.action }">
          <div class="tip-number">{{ index + 1 }}</div>
          <div class="tip-content">
            <div class="tip-title">{{ tip.title }}</div>
            <div class="tip-desc">{{ tip.desc }}</div>
          </div>
          <el-button 
            v-if="tip.action" 
            size="default" 
            @click="handleTipAction(tip)"
            class="tip-action-btn"
          >
            <el-icon><Right /></el-icon>
            {{ tip.actionText }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 积分记录 -->
    <div class="section-title">
      <el-icon><List /></el-icon>
      <span>我的积分记录</span>
    </div>
    <el-card class="points-log-card">
      <el-table :data="pointsLog" style="width: 100%">
        <el-table-column prop="actionDesc" label="行为" width="200">
          <template #default="{ row }">
            <div class="action-cell">
              <el-icon>
                <Calendar v-if="row.actionType === 1" />
                <Document v-else-if="row.actionType === 2" />
                <ChatDotRound v-else-if="row.actionType === 3" />
                <Star v-else-if="row.actionType === 4" />
                <Star v-else-if="row.actionType === 5" />
                <Share v-else-if="row.actionType === 6" />
                <User v-else-if="row.actionType === 7" />
                <UserFilled v-else-if="row.actionType === 8" />
                <Coin v-else-if="row.actionType === 9" />
                <Setting v-else-if="row.actionType === 10" />
                <Coin v-else />
              </el-icon>
              <span>{{ row.actionDesc }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分变动" width="120" align="center">
          <template #default="{ row }">
            <span :class="row.points > 0 ? 'points-plus' : 'points-minus'">
              {{ row.points > 0 ? '+' : '' }}{{ row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="积分余额" width="120" align="center" />
        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="说明" min-width="200">
          <template #default="{ row }">
            <span class="log-detail">{{ getLogDetail(row) }}</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="pointsLog.length === 0 && total === 0" description="暂无积分记录" />
      
      <!-- 分页组件 -->
      <div v-if="total > pageSize" class="points-log-pagination">
        <el-button 
          :disabled="pageNum <= 1"
          class="page-btn prev-btn"
          @click="handlePointsLogPageChange(pageNum - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="page-info">{{ pageNum }} / {{ totalPages }}</span>
        <el-button 
          :disabled="pageNum >= totalPages"
          class="page-btn next-btn"
          @click="handlePointsLogPageChange(pageNum + 1)"
        >
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
    </el-card>

    <!-- 签到对话框 -->
    <el-dialog
      v-model="checkinDialogVisible"
      title="每日签到"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="checkin-content">
        <div class="checkin-icon">
          <el-icon :size="80" color="#ffd700"><Calendar /></el-icon>
        </div>
        <div class="checkin-title">{{ checkinMessage }}</div>
        <div class="checkin-points" v-if="!alreadyCheckedIn">
          <span class="points-label">今日可获得</span>
          <span class="points-value">+5</span>
          <span class="points-unit">积分</span>
        </div>
        <div class="checkin-tips" v-if="!alreadyCheckedIn">
          <el-icon><InfoFilled /></el-icon>
          <span>连续签到可获得额外奖励</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="checkinDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleCheckin"
          :disabled="alreadyCheckedIn"
        >
          {{ alreadyCheckedIn ? '今日已签到' : '立即签到' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { 
  TrophyBase, Medal, Trophy, TrendCharts, Coin, 
  CircleCheck, CircleClose, ChatDotRound, View, Hide,
  Promotion, List, Calendar, Document, Star, Share, 
  User, UserFilled, Setting, Right, InfoFilled, Ticket, ArrowRight, ArrowLeft,
  Location, StarFilled, Aim, Rank, MagicStick
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { getLevelByCode, getLevelByPoints, getLevelByName } from '@/utils/level'
import { getCurrentUserInfo, getCurrentUserId } from '@/utils/user'

const router = useRouter()

// 用户当前信息
const currentPoints = ref(0)
const currentLevelCode = ref(1)
const currentLevelName = ref('青铜旅行者')
const currentLevelColor = ref('#CD7F32')
const currentLevelGradient = ref('linear-gradient(135deg, #CD7F32 0%, #8B5A00 100%)')

// 等级列表
const levels = ref([
  {
    code: 1,
    name: '青铜旅行者',
    points: 0,
    color: '#8B7355',
    gradient: 'linear-gradient(135deg, #8B7355 0%, #6B5B4F 100%)',
    icon: 'User',
    canPost: false,
    postLimit: 0,
    canComment: true,
    commentLimit: 10,
    couponBenefit: false,
    couponText: ''
  },
  {
    code: 2,
    name: '白银探索者',
    points: 100,
    color: '#9CA3AF',
    gradient: 'linear-gradient(135deg, #9CA3AF 0%, #6B7280 100%)',
    icon: 'Aim',
    canPost: true,
    postLimit: 2,
    canComment: true,
    commentLimit: 15,
    couponBenefit: false,
    couponText: ''
  },
  {
    code: 3,
    name: '黄金游侠',
    points: 500,
    color: '#F59E0B',
    gradient: 'linear-gradient(135deg, #F59E0B 0%, #D97706 100%)',
    icon: 'Medal',
    canPost: true,
    postLimit: 5,
    canComment: true,
    commentLimit: 30,
    couponBenefit: true,
    couponText: '每月1张满50减10优惠券'
  },
  {
    code: 4,
    name: '铂金旅者',
    points: 2000,
    color: '#6366F1',
    gradient: 'linear-gradient(135deg, #6366F1 0%, #4F46E5 100%)',
    icon: 'Trophy',
    canPost: true,
    postLimit: 10,
    canComment: true,
    commentLimit: 50,
    couponBenefit: true,
    couponText: '每月2张满100减20优惠券'
  },
  {
    code: 5,
    name: '钻石达人',
    points: 5000,
    color: '#EC4899',
    gradient: 'linear-gradient(135deg, #EC4899 0%, #DB2777 100%)',
    icon: 'StarFilled',
    canPost: true,
    postLimit: 20,
    canComment: true,
    commentLimit: 100,
    couponBenefit: true,
    couponText: '每月3张满200减50优惠券'
  },
  {
    code: 6,
    name: '王者导师',
    points: 10000,
    color: '#F97316',
    gradient: 'linear-gradient(135deg, #F97316 0%, #EA580C 100%)',
    icon: 'TrophyBase',
    canPost: true,
    postLimit: 999,
    canComment: true,
    commentLimit: 999,
    couponBenefit: true,
    couponText: '每月5张满300减100优惠券'
  }
])

// 积分规则
const pointsRules = ref([
  { icon: '📅', name: '每日登录', desc: '每天登录系统', points: 5, gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { icon: '✍️', name: '发布攻略', desc: '发布旅行攻略（审核通过）', points: 20, gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { icon: '💬', name: '发表评论', desc: '有效评论（>10字）', points: 2, gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { icon: '👍', name: '收到点赞', desc: '攻略或评论被点赞', points: 1, gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { icon: '⭐', name: '收藏内容', desc: '收藏景点或攻略', points: 1, gradient: 'linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%)' },
  { icon: '🔗', name: '分享内容', desc: '分享攻略或景点', points: 3, gradient: 'linear-gradient(135deg, #f8b500 0%, #fceabb 100%)' },
  { icon: '👤', name: '完善资料', desc: '完善个人信息', points: 10, gradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)' },
  { icon: '🎁', name: '邀请好友', desc: '好友完成注册', points: 50, gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)' },
  { icon: '🛒', name: '完成订单', desc: '文旅产品订单（消费金额1%）', points: '1%', gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)' },
  { icon: '🏆', name: '攻略加精', desc: '攻略被设为精华', points: 30, gradient: 'linear-gradient(135deg, #ffd700 0%, #ffb800 100%)' },
  { icon: '🔥', name: '热门攻略', desc: '攻略进入热门榜', points: 50, gradient: 'linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%)' },
  { icon: '💎', name: '连续签到', desc: '连续签到额外奖励', points: '+5', gradient: 'linear-gradient(135deg, #c471ed 0%, #f64f59 100%)' }
])

// 升级技巧
const tips = ref([
  { 
    title: '完善个人资料', 
    desc: '首次完善头像、昵称、个性签名等信息，一次性获得10积分',
    action: 'profile',
    actionText: '立即完善'
  },
  { 
    title: '每日登录签到', 
    desc: '养成每天登录的习惯，每天可获得5积分，连续登录还有额外奖励',
    action: 'checkin',
    actionText: '立即签到'
  },
  { 
    title: '发布优质攻略', 
    desc: '分享你的旅行经历和攻略，每篇通过审核的攻略可获得20积分',
    action: 'create-plan',
    actionText: '去发布'
  },
  { 
    title: '积极互动交流', 
    desc: '在他人攻略下发表有价值的评论，每条有效评论可获得2积分',
    action: 'community',
    actionText: '去评论'
  },
  { 
    title: '收藏和分享', 
    desc: '收藏感兴趣的内容获得1积分，分享给好友可获得3积分',
    action: 'recommendations',
    actionText: '去发现'
  },
  { 
    title: '创作受欢迎内容', 
    desc: '你的攻略和评论被点赞时，每个点赞可获得1积分',
    action: 'my-plans',
    actionText: '查看我的'
  },
  { 
    title: '邀请好友注册', 
    desc: '邀请好友完成注册，每成功邀请一位好友可获得50积分',
    action: 'invite',
    actionText: '去邀请'
  },
  { 
    title: '完成文旅订单', 
    desc: '购买文旅产品并完成订单，可获得消费金额1%的积分奖励',
    action: 'culture',
    actionText: '去购买'
  },
  { 
    title: '攻略获得加精', 
    desc: '你的攻略被设为精华时，可获得30积分奖励',
    action: 'my-plans',
    actionText: '查看我的'
  },
  { 
    title: '攻略进入热门', 
    desc: '你的攻略进入热门榜时，可获得50积分奖励',
    action: 'my-plans',
    actionText: '查看我的'
  },
])

// 积分记录
const pointsLog = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// 签到相关
const checkinDialogVisible = ref(false)
const alreadyCheckedIn = ref(false)
const checkinMessage = ref('坚持每日签到，获得更多积分奖励！')


// 计算下一级
const nextLevel = computed(() => {
  const current = levels.value.find(l => l.code === currentLevelCode.value)
  if (!current) return null
  const nextIndex = levels.value.findIndex(l => l.code === current.code) + 1
  return nextIndex < levels.value.length ? {
    name: levels.value[nextIndex].name,
    points: levels.value[nextIndex].points
  } : null
})

// 计算升级进度
const levelProgress = computed(() => {
  if (!nextLevel.value) return 100
  const current = levels.value.find(l => l.code === currentLevelCode.value)
  if (!current) return 0
  
  const currentPointsValue = current.points
  const nextPoints = nextLevel.value.points
  const userPoints = currentPoints.value
  
  // 如果积分已经达到或超过下一级，返回100%
  if (userPoints >= nextPoints) return 100
  
  // 如果下一级积分小于等于当前等级积分，返回100%（不应该发生，但做保护）
  if (nextPoints <= currentPointsValue) return 100
  
  // 计算进度百分比
  const progress = ((userPoints - currentPointsValue) / (nextPoints - currentPointsValue)) * 100
  return Math.min(Math.max(progress, 0), 100)
})

// 进度条颜色
const progressColor = computed(() => {
  if (levelProgress.value < 30) return '#f56c6c'
  if (levelProgress.value < 60) return '#e6a23c'
  if (levelProgress.value < 90) return '#409eff'
  return '#67c23a'
})

// 获取当前等级的图标
const currentLevelIcon = computed(() => {
  const level = levels.value.find(l => l.code === currentLevelCode.value)
  return level?.icon || 'Medal'
})

// 获取日志详情
// 获取友好的说明文本
const getLogDetail = (log) => {
  // 如果已经有详细的actionDesc，直接使用（如"首次完善个人资料"、"连续7天奖励"等）
  if (log.actionDesc && !log.actionDesc.includes('关联') && !log.actionDesc.includes('#')) {
    return log.actionDesc
  }
  
  // 根据行为类型和关联类型生成友好说明
  const actionType = log.actionType
  const relatedType = log.relatedType
  const relatedId = log.relatedId
  
  // 如果没有关联信息，返回默认说明
  if (!relatedType || !relatedId) {
    // 根据行为类型返回默认说明
    const defaultDesc = {
      1: '每日登录奖励',
      2: '攻略审核通过',
      3: '评论已发布',
      4: '收到点赞',
      5: '收藏成功',
      6: '分享成功',
      7: '资料已完善',
      8: '好友注册成功',
      9: '订单已完成',
      10: '管理员调整',
      11: '攻略进入热门',
      12: '攻略获得加精'
    }
    return defaultDesc[actionType] || '积分操作'
  }
  
  // 根据关联类型生成友好说明
  const typeMap = {
    'plan': '攻略',
    'attraction': '景点',
    'comment': '评论',
    'order': '订单',
    'user': '用户'
  }
  
  const typeName = typeMap[relatedType] || relatedType
  
  // 根据行为类型组合说明
  const actionDescMap = {
    1: '每日登录',
    2: '发布攻略',
    3: '发表评论',
    4: `收到${typeName}的点赞`,
    5: `收藏了${typeName}`,
    6: `分享了${typeName}`,
    7: '完善资料',
    8: '邀请好友',
    9: '完成订单',
    10: '管理员调整',
    11: '攻略进入热门',
    12: '攻略获得加精'
  }
  
  return actionDescMap[actionType] || `${typeName}相关操作`
}

// 积分记录分页改变
const handlePointsLogPageChange = (page) => {
  pageNum.value = page
  loadPointsLog()
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 优先从当前标签页获取用户信息
    const userData = getCurrentUserInfo()
    if (userData) {
      currentPoints.value = userData.points || 0
    }
    
    // 从后端获取真实的用户积分和等级信息
    try {
      const userResponse = await request.get('/user/info')
      if (userResponse.code === 200 && userResponse.data) {
        const user = userResponse.data
        const userId = user.id
        
        if (userId) {
          // 获取积分和等级信息
          const pointsResponse = await request.get('/user/points/my', {
            params: {
              userId: userId
            }
          })
          
          if (pointsResponse && pointsResponse.code === 200 && pointsResponse.data) {
            const pointsData = pointsResponse.data
            const userPoints = pointsData.userPoints
            
            // 更新积分
            currentPoints.value = userPoints?.totalPoints || 0
            
            // 优先根据积分实时计算等级（确保准确性）
            const levelInfo = getLevelByPoints(currentPoints.value)
            if (levelInfo) {
              currentLevelCode.value = levelInfo.code
              // 从 levels 数组中获取完整的等级信息（包括颜色和渐变）
              const levelData = levels.value.find(l => l.code === levelInfo.code)
              if (levelData) {
                currentLevelName.value = levelData.name
                currentLevelColor.value = levelData.color
                currentLevelGradient.value = levelData.gradient
              } else {
                currentLevelName.value = levelInfo.name
                currentLevelColor.value = levelInfo.color
                if (levelInfo.gradient) {
                  currentLevelGradient.value = `linear-gradient(135deg, ${levelInfo.gradient.start}, ${levelInfo.gradient.end})`
                } else {
                  currentLevelGradient.value = `linear-gradient(135deg, ${levelInfo.color}, ${levelInfo.color})`
                }
              }
            }
          }
        }
      }
    } catch (error) {
      console.warn('从后端获取用户信息失败，使用localStorage数据:', error)
      // 降级方案：使用localStorage数据并根据积分计算等级
      try {
        const savedUserInfo = localStorage.getItem('travel_user_info') || sessionStorage.getItem('travel_user_info')
        if (savedUserInfo) {
          const userData = JSON.parse(savedUserInfo)
          currentPoints.value = userData.points || 0
          const levelInfo = getLevelByPoints(currentPoints.value)
          if (levelInfo) {
            currentLevelCode.value = levelInfo.code
            // 从 levels 数组中获取完整的等级信息（包括颜色和渐变）
            const levelData = levels.value.find(l => l.code === levelInfo.code)
            if (levelData) {
              currentLevelName.value = levelData.name
              currentLevelColor.value = levelData.color
              currentLevelGradient.value = levelData.gradient
            } else {
              currentLevelName.value = levelInfo.name
              currentLevelColor.value = levelInfo.color
              if (levelInfo.gradient) {
                currentLevelGradient.value = `linear-gradient(135deg, ${levelInfo.gradient.start}, ${levelInfo.gradient.end})`
              } else {
                currentLevelGradient.value = `linear-gradient(135deg, ${levelInfo.color}, ${levelInfo.color})`
              }
            }
          }
        }
      } catch (parseError) {
        console.error('解析用户信息失败:', parseError)
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 加载积分记录
const loadPointsLog = async () => {
  try {
    // 获取当前标签页的用户信息
    const userData = getCurrentUserInfo()
    if (!userData) {
      return
    }
    
    const userId = userData.id
    
    if (!userId) {
      // 尝试从后端获取用户ID
      try {
        const userResponse = await request.get('/user/info')
        if (userResponse.code === 200 && userResponse.data && userResponse.data.id) {
          await loadPointsLogForUser(userResponse.data.id)
        }
      } catch (error) {
        console.warn('获取用户ID失败，使用模拟数据:', error)
        // 降级方案：使用模拟数据
        useMockPointsLog()
      }
    } else {
      await loadPointsLogForUser(userId)
    }
  } catch (error) {
    console.error('加载积分记录失败:', error)
    // 降级方案：使用模拟数据
    useMockPointsLog()
  }
}

// 为指定用户加载积分记录
const loadPointsLogForUser = async (userId) => {
  try {
    const response = await request.get('/user/points/my-log', {
      params: {
        userId,
        page: pageNum.value,
        limit: pageSize.value
      }
    })
    
    // 兼容R和Result两种返回格式
    if ((response.code === 200 || response.code === 0) && response.data) {
      console.log('📦 后端返回的完整数据:', JSON.stringify(response.data, null, 2))
      
      // 处理分页数据 - MyBatis Plus的Page对象包含records和total
      if (response.data.records) {
        // MyBatis Plus分页返回格式
        const records = response.data.records || []
        pointsLog.value = records.map((log) => ({
          actionType: log.actionType,
          actionDesc: log.actionDesc || log.description || '积分操作',
          points: log.points || log.pointChange || 0,
          balanceAfter: log.balanceAfter || log.balance || 0,
          createTime: log.createTime || log.createdTime,
          relatedType: log.relatedType,
          relatedId: log.relatedId
        }))
        // 修复：如果total为0但records有数据，说明后端分页可能有问题
        // 如果records长度等于pageSize，说明可能还有更多数据，需要设置一个合理的total
        if (response.data.total > 0) {
          total.value = response.data.total
        } else if (records.length > 0) {
          // 如果total为0但有数据，说明后端可能没有正确返回total
          // 如果当前页数据量等于pageSize，说明可能还有更多数据，设置一个较大的total
          // 否则使用records.length作为total
          if (records.length >= pageSize.value) {
            // 当前页数据满了，可能还有更多数据，设置一个合理的total（至少是当前页数 * pageSize）
            total.value = pageNum.value * pageSize.value + 1 // 至少比当前页多1，确保有下一页
          } else {
            // 当前页数据不满，说明这是最后一页，使用records.length作为total
            total.value = (pageNum.value - 1) * pageSize.value + records.length
          }
        } else {
          total.value = 0
        }
        console.log('✅ 积分记录加载成功 - 总数:', total.value, '当前页数据:', records.length, '总页数:', Math.ceil(total.value / pageSize.value), '后端返回total:', response.data.total)
      } else if (response.data.list) {
        // 自定义分页格式
        const records = response.data.list || []
        pointsLog.value = records.map((log) => ({
          actionType: log.actionType,
          actionDesc: log.actionDesc || log.description || '积分操作',
          points: log.points || log.pointChange || 0,
          balanceAfter: log.balanceAfter || log.balance || 0,
          createTime: log.createTime || log.createdTime,
          relatedType: log.relatedType,
          relatedId: log.relatedId
        }))
        total.value = response.data.total || records.length
        console.log('✅ 积分记录加载成功(list格式) - 总数:', total.value, '当前页数据:', records.length)
      } else if (Array.isArray(response.data)) {
        // 直接是数组（无分页）
        pointsLog.value = response.data.slice((pageNum.value - 1) * pageSize.value, pageNum.value * pageSize.value).map((log) => ({
          actionType: log.actionType,
          actionDesc: log.actionDesc || log.description || '积分操作',
          points: log.points || log.pointChange || 0,
          balanceAfter: log.balanceAfter || log.balance || 0,
          createTime: log.createTime || log.createdTime,
          relatedType: log.relatedType,
          relatedId: log.relatedId
        }))
        total.value = response.data.length
      }
    }
  } catch (error) {
    console.error('加载积分记录失败:', error)
    useMockPointsLog()
  }
}

// 使用模拟数据（降级方案）
const useMockPointsLog = () => {
  pointsLog.value = [
    {
      actionType: 1,
      actionDesc: '每日登录',
      points: 5,
      balanceAfter: currentPoints.value,
      createTime: new Date().toISOString(),
      relatedType: null,
      relatedId: null
    },
    {
      actionType: 2,
      actionDesc: '发布攻略',
      points: 20,
      balanceAfter: currentPoints.value - 5,
      createTime: new Date(Date.now() - 86400000).toISOString(),
      relatedType: 'plan',
      relatedId: 1
    },
    {
      actionType: 3,
      actionDesc: '发表评论',
      points: 2,
      balanceAfter: currentPoints.value - 25,
      createTime: new Date(Date.now() - 172800000).toISOString(),
      relatedType: 'plan',
      relatedId: 2
    }
  ]
  // 模拟数据时，设置total为实际数据长度，用于分页显示
  total.value = pointsLog.value.length
  // 如果数据超过一页，需要分页显示
  if (pointsLog.value.length > pageSize.value) {
    pointsLog.value = pointsLog.value.slice(0, pageSize.value)
  }
}

// 处理技巧跳转
const handleTipAction = (tip) => {
  const actionMap = {
    'profile': () => {
      // 跳转到个人资料编辑页面
      router.push('/home/user/profile-edit')
    },
    'checkin': () => {
      // 打开签到对话框
      checkinDialogVisible.value = true
      checkTodayCheckin()
    },
    'create-plan': () => {
      // 跳转到创建攻略页面
      router.push('/home/user/plans/create')
    },
    'community': () => {
      // 跳转到攻略社区页面（可以发表评论）
      router.push('/home/user/community')
    },
    'recommendations': () => {
      // 跳转到推荐页面（可以收藏和分享）
      router.push('/home/user/recommendations')
    },
    'my-plans': () => {
      // 跳转到我的攻略页面（查看自己的攻略、点赞情况、加精和热门状态）
      router.push('/home/user/plans')
    },
    'invite': () => {
      // 跳转到邀请好友页面（如果存在）或用户中心
      // TODO: 如果有专门的邀请页面，可以跳转过去
      ElMessage.info('邀请好友功能开发中，敬请期待！')
      // router.push('/home/user/invite')
    },
    'culture': () => {
      // 跳转到文旅体验页面（可以购买产品）
      router.push('/home/user/culture')
    },
  }
  
  const action = actionMap[tip.action]
  if (action) {
    action()
  } else {
    ElMessage.warning('功能暂未开放')
  }
}

// 跳转到我的优惠券页面
const goToCoupons = () => {
  router.push('/home/user/coupons')
}

// 检查今日是否已签到
const checkTodayCheckin = async () => {
  try {
    // 从localStorage快速检查
    const today = new Date().toDateString()
    const lastCheckin = localStorage.getItem('last_checkin_date')
    
    if (lastCheckin === today) {
      alreadyCheckedIn.value = true
      checkinMessage.value = '今日已签到，明天再来吧！'
    } else {
      alreadyCheckedIn.value = false
      checkinMessage.value = '坚持每日签到，获得更多积分奖励！'
    }
    
    // 可以调用后端接口验证今日是否已签到（可选）
    // TODO: 如果有检查签到的接口，可以调用
  } catch (error) {
    console.warn('检查签到状态失败:', error)
  }
}

// 处理签到
const handleCheckin = async () => {
  if (alreadyCheckedIn.value) return
  
  try {
    // 获取当前标签页的用户信息
    const userData = getCurrentUserInfo()
    let userId = null
    
    if (userData) {
      userId = userData.id
    }
    
    // 如果localStorage没有用户ID，尝试从后端获取
    if (!userId) {
      try {
        const userResponse = await request.get('/user/info')
        if (userResponse.code === 200 && userResponse.data && userResponse.data.id) {
          userId = userResponse.data.id
        }
      } catch (error) {
        console.error('获取用户ID失败:', error)
        ElMessage.error('无法获取用户信息，请重新登录')
        return
      }
    }
    
    if (!userId) {
      ElMessage.error('无法获取用户信息，请重新登录')
      return
    }
    
    // 调用后端签到接口
    const response = await request.post('/user/points/daily-checkin', {}, {
      params: { userId }
    })
    
    // 兼容R和Result两种返回格式
    if (response.code === 200 || response.code === 0 || response.success) {
      // 签到成功
      const today = new Date().toDateString()
      localStorage.setItem('last_checkin_date', today)
      
      // 刷新用户信息（重新加载积分）
      await loadUserInfo()
      
      alreadyCheckedIn.value = true
      checkinMessage.value = '签到成功！获得5积分'
      
      ElMessage.success(response.message || response.data || '签到成功，获得5积分！')
      
      // 刷新积分记录
      await loadPointsLog()
      
      // 1.5秒后关闭对话框
      setTimeout(() => {
        checkinDialogVisible.value = false
      }, 1500)
    } else {
      ElMessage.error(response.message || '签到失败，请稍后重试')
    }
  } catch (error) {
    console.error('签到失败:', error)
    // 如果后端返回错误，可能是今日已签到
    if (error.response?.data?.message?.includes('已签到') || error.message?.includes('已签到')) {
      const today = new Date().toDateString()
      localStorage.setItem('last_checkin_date', today)
      alreadyCheckedIn.value = true
      checkinMessage.value = '今日已签到，明天再来吧！'
      ElMessage.warning('今日已签到，明天再来吧！')
    } else {
      ElMessage.error('签到失败，请稍后重试')
    }
  }
}


// 分页
const handlePageChange = (page) => {
  pageNum.value = page
  loadPointsLog()
}

onMounted(() => {
  loadUserInfo()
  loadPointsLog()
})
</script>

<style lang="scss" scoped>
.level-guide-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
  
  .header-content {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  
  .header-icon {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #606266;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #e4e7ed;
  }
  
  .header-text {
    flex: 1;
    
    h2 {
      margin: 0;
      font-size: 32px;
      font-weight: 700;
      color: #303133;
      margin-bottom: 8px;
    }
    
    .page-desc {
      margin: 0;
      font-size: 16px;
      color: #909399;
    }
  }
}

.current-level-card {
  margin-bottom: 24px;
  border-radius: 12px;
  
  :deep(.el-card__body) {
    padding: 24px;
  }
  
  .level-status {
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 24px;
  }
  
  .level-icon {
    width: 80px;
    height: 80px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }
  
  .level-info {
    flex: 1;
    
    .level-name {
      font-size: 28px;
      font-weight: 700;
      margin-bottom: 4px;
    }
    
    .level-desc {
      font-size: 14px;
      color: #909399;
    }
  }
  
  .points-info {
    text-align: right;
    
    .points-value {
      font-size: 32px;
      font-weight: 700;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .points-label {
      font-size: 14px;
      color: #909399;
    }
  }
  
  .level-progress {
    .progress-info {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: 14px;
      color: #606266;
      
      .progress-text {
        font-weight: 600;
        color: #409eff;
        
        &.already-reached {
          color: #67c23a;
          font-weight: 700;
        }
      }
      
      .progress-percentage {
        text-align: right;
        margin-top: 8px;
        font-size: 14px;
        color: #909399;
        font-weight: 600;
      }
    }
  }
  
  .max-level-tip {
    text-align: center;
    padding: 16px;
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
    border-radius: 8px;
    color: #606266;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 32px 0 16px;
}

.levels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.level-card {
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  border: 2px solid #e5e7eb;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  background: #ffffff;
  
  &.current {
    border: 3px solid #F59E0B;
    box-shadow: 0 0 0 2px rgba(245, 158, 11, 0.1), 0 8px 24px rgba(245, 158, 11, 0.3);
  }
  
  &.locked {
    opacity: 0.75;
    filter: grayscale(20%);
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
  
  .level-badge {
    padding: 32px 24px;
    color: white;
    text-align: center;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
      pointer-events: none;
    }
    
    .el-icon {
      margin-bottom: 12px;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
    }
    
    .level-badge-name {
      font-size: 19px;
      font-weight: 700;
      letter-spacing: 1px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
    }
  }
  
  .level-details {
    padding: 28px 24px;
    background: linear-gradient(to bottom, #ffffff 0%, #fafafa 100%);
    
    .level-points {
      text-align: center;
      font-size: 20px;
      font-weight: 700;
      color: #1f2937;
      margin-bottom: 8px;
      padding: 12px 0;
      background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
      border-radius: 10px;
    }
    
    .level-perks {
      margin-top: 20px;
      
      .perk-item {
        padding: 12px 16px;
        font-size: 14px;
        color: #4b5563;
        line-height: 1.7;
        background: #ffffff;
        border-radius: 8px;
        margin-bottom: 8px;
        border-left: 3px solid #e5e7eb;
        transition: all 0.2s ease;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        &:hover {
          background: #f9fafb;
          border-left-color: #6366f1;
          transform: translateX(4px);
        }
        
        &.clickable {
          cursor: pointer;
          color: #6366f1;
          border-left-color: #6366f1;
          
          &:hover {
            background: #eef2ff;
            border-left-color: #4f46e5;
          }
          
          &:active {
            transform: translateX(2px);
          }
        }
      }
    }
  }
}

.points-rules-card {
  border-radius: 12px;
  margin-bottom: 24px;
  
  .rules-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
  }
  
  .rule-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 24px;
    background: #ffffff;
    border: 1px solid #e4e7ed;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  }
  
  .rule-info {
    flex: 1;
    
    .rule-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 6px;
      line-height: 1.4;
    }
    
    .rule-desc {
      font-size: 14px;
      color: #606266;
      line-height: 1.5;
    }
  }
  
  .rule-points {
    font-size: 28px;
    font-weight: 700;
    color: #ff6b35;
    margin-left: 20px;
    white-space: nowrap;
  }
}

.tips-card {
  border-radius: 12px;
  margin-bottom: 24px;
  
  .tips-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .tip-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 20px;
    background: #f5f7fa;
    border-radius: 8px;
    
    .tip-number {
      width: 32px;
      height: 32px;
      background: linear-gradient(135deg, #e4e7ed 0%, #c0c4cc 100%);
      color: #606266;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      flex-shrink: 0;
    }
    
    .tip-content {
      flex: 1;
      
      .tip-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .tip-desc {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
      }
    }
    
    .tip-action-btn {
      flex-shrink: 0;
      border-radius: 20px;
      padding: 12px 28px;
      font-weight: 500;
      font-size: 15px;
      background: #f5f7fa;
      border-color: #dcdfe6;
      color: #606266;
      min-height: 44px;
      
      &:hover {
        background: #e4e7ed;
        border-color: #c0c4cc;
        color: #303133;
      }
      
      .el-icon {
        margin-right: 0;
        margin-left: 6px;
        font-size: 16px;
      }
    }
  }
}

.points-log-card {
  border-radius: 12px;
  
  :deep(.el-card__body) {
    padding: 20px;
  }
  
  .action-cell {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .points-plus {
    color: #67c23a;
    font-weight: 600;
  }
  
  .points-minus {
    color: #f56c6c;
    font-weight: 600;
  }
  
  .log-detail {
    color: #909399;
    font-size: 13px;
  }
}

// 签到对话框样式
.checkin-content {
  text-align: center;
  padding: 20px 0;
  
  .checkin-icon {
    margin-bottom: 20px;
    animation: bounce 2s ease-in-out infinite;
  }
  
  .checkin-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
  }
  
  .checkin-points {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin-bottom: 16px;
    
    .points-label {
      font-size: 14px;
      color: #909399;
    }
    
    .points-value {
      font-size: 32px;
      font-weight: 700;
      background: linear-gradient(135deg, #ffd700 0%, #ffb800 100%);
      background-clip: text;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    
    .points-unit {
      font-size: 14px;
      color: #909399;
    }
  }
  
  .checkin-tips {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: #ecf5ff;
    border-radius: 16px;
    font-size: 13px;
    color: #409eff;
    
    .el-icon {
      font-size: 16px;
    }
  }
}


@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@media (max-width: 768px) {
  .levels-grid {
    grid-template-columns: 1fr;
  }
  
  .rules-grid {
    grid-template-columns: 1fr !important;
  }
  
  .level-status {
    flex-direction: column;
    text-align: center;
  }
}

// 积分记录分页样式
.points-log-pagination {
  margin-top: 24px;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
  display: flex !important;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
  min-height: 40px;

  .page-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    width: 32px;
    height: 32px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    background: #fff;
    color: #606266;
    font-weight: 400;
    font-size: 14px;
    box-shadow: none;
    transition: all 0.2s ease;
    cursor: pointer;

    .el-icon {
      font-size: 14px;
      color: #606266;
    }

    &:hover:not(.is-disabled) {
      border-color: #409eff;
      color: #409eff;
      background: #ecf5ff;

      .el-icon {
        color: #409eff;
      }
    }

    &:active:not(.is-disabled) {
      background: #b3d8ff;
    }

    &.is-disabled {
      border-color: #e4e7ed;
      background: #f5f7fa;
      color: #c0c4cc;
      cursor: not-allowed;

      .el-icon {
        color: #c0c4cc;
      }
    }
  }

  .page-info {
    font-size: 14px;
    color: #606266;
    min-width: 50px;
    text-align: center;
    font-weight: 500;
  }
}
</style>

