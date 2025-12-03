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
          <el-icon :size="48"><Medal /></el-icon>
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
        <div class="progress-percentage">{{ Math.min(100, Math.round(levelProgress)) }}%</div>
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
          <el-icon :size="32"><Medal /></el-icon>
          <div class="level-badge-name">{{ level.name }}</div>
        </div>
        <div class="level-details">
          <div class="level-points">
            <el-icon><Coin /></el-icon>
            <span>{{ level.points }} 积分</span>
          </div>
          <el-divider />
          <div class="level-perks">
            <div class="perk-item">
              <el-icon :color="level.canPost ? '#67c23a' : '#909399'">
                <CircleCheck v-if="level.canPost" />
                <CircleClose v-else />
              </el-icon>
              <span>{{ level.canPost ? `发布攻略 ${level.postLimit}篇/天` : '无发布权限' }}</span>
            </div>
            <div class="perk-item">
              <el-icon color="#67c23a"><ChatDotRound /></el-icon>
              <span>评论 {{ level.commentLimit }}条/天</span>
            </div>
            <div class="perk-item">
              <el-icon :color="level.couponBenefit ? '#67c23a' : '#909399'">
                <Ticket v-if="level.couponBenefit" />
                <CircleClose v-else />
              </el-icon>
              <span>{{ level.couponBenefit ? level.couponText : '无优惠券权益' }}</span>
            </div>
          </div>
        </div>
        <div class="current-badge" v-if="level.code === currentLevelCode">
          <el-icon><CircleCheck /></el-icon>
          当前等级
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
          <div class="rule-icon" :style="{ background: rule.gradient }">
            {{ rule.icon }}
          </div>
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
            size="small" 
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
      
      <el-empty v-if="pointsLog.length === 0" description="暂无积分记录" />
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
  User, UserFilled, Setting, Right, InfoFilled, Ticket
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
    color: '#CD7F32',
    gradient: 'linear-gradient(135deg, #CD7F32 0%, #8B5A00 100%)',
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
    color: '#C0C0C0',
    gradient: 'linear-gradient(135deg, #C0C0C0 0%, #A8A8A8 100%)',
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
    color: '#FFD700',
    gradient: 'linear-gradient(135deg, #FFD700 0%, #FFA500 100%)',
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
    color: '#E5E4E2',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
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
    color: '#B9F2FF',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
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
    color: '#FF4500',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
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
])

// 积分记录
const pointsLog = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

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

// 获取日志详情
const getLogDetail = (log) => {
  if (log.relatedType && log.relatedId) {
    return `关联${log.relatedType} #${log.relatedId}`
  }
  return '-'
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
    } catch (error) {
      console.warn('从后端获取用户信息失败，使用localStorage数据:', error)
      // 降级方案：使用localStorage数据并根据积分计算等级
      try {
        const savedUserInfo = localStorage.getItem('travel_user_info') || sessionStorage.getItem('travel_user_info')
        if (savedUserInfo) {
          const userData = JSON.parse(savedUserInfo)
          currentPoints.value = userData.points || 0
          const levelInfo = getLevelByPoints(currentPoints.value)
          currentLevelCode.value = levelInfo.code
          currentLevelName.value = levelInfo.name
          currentLevelColor.value = levelInfo.color
          if (levelInfo.gradient) {
            currentLevelGradient.value = `linear-gradient(135deg, ${levelInfo.gradient.start}, ${levelInfo.gradient.end})`
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
      // 处理分页数据
      if (response.data.records || response.data.list) {
        const records = response.data.records || response.data.list || []
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
      } else if (Array.isArray(response.data)) {
        // 直接是数组
        pointsLog.value = response.data.map((log) => ({
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
  total.value = pointsLog.value.length
}

// 处理技巧跳转
const handleTipAction = (tip) => {
  const actionMap = {
    'profile': () => {
      router.push('/home/user/profile-edit')
    },
    'checkin': () => {
      checkinDialogVisible.value = true
      checkTodayCheckin()
    },
    'create-plan': () => {
      router.push('/home/user/plans/create')
    },
    'community': () => {
      router.push('/home/user/community')
    },
    'recommendations': () => {
      router.push('/home/user/recommendations')
    },
    'my-plans': () => {
      router.push('/home/user/plans')
    },
  }
  
  const action = actionMap[tip.action]
  if (action) {
    action()
  }
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
  border-radius: 12px;
  transition: all 0.3s;
  position: relative;
  overflow: visible;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  &.current {
    border: 2px solid #ffd700;
    box-shadow: 0 4px 20px rgba(255, 215, 0, 0.25);
  }
  
  &.locked {
    opacity: 0.6;
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
  
  .level-badge {
    padding: 24px;
    color: white;
    text-align: center;
    border-radius: 12px 12px 0 0;
    
    .level-badge-name {
      margin-top: 12px;
      font-size: 18px;
      font-weight: 700;
    }
  }
  
  .level-details {
    padding: 20px;
    
    .level-points {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
    
    .level-perks {
      margin-top: 12px;
      
      .perk-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 0;
        font-size: 14px;
        color: #606266;
      }
    }
  }
  
  .current-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: linear-gradient(135deg, #ffd700 0%, #ffb800 100%);
    color: #8B4513;
    padding: 5px 14px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 4px;
    box-shadow: 0 3px 12px rgba(255, 215, 0, 0.4);
    border: 1.5px solid rgba(255, 255, 255, 0.5);
    animation: badge-glow 2s ease-in-out infinite;
  }
  
  @keyframes badge-glow {
    0%, 100% {
      box-shadow: 0 3px 12px rgba(255, 215, 0, 0.4);
    }
    50% {
      box-shadow: 0 3px 16px rgba(255, 215, 0, 0.6);
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
    gap: 16px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
    transition: all 0.3s;
    
    &:hover {
      background: #ecf5ff;
      transform: translateX(4px);
    }
  }
  
  .rule-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
  }
  
  .rule-info {
    flex: 1;
    
    .rule-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .rule-desc {
      font-size: 13px;
      color: #909399;
    }
  }
  
  .rule-points {
    font-size: 24px;
    font-weight: 700;
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
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
      padding: 8px 20px;
      font-weight: 500;
      background: #f5f7fa;
      border-color: #dcdfe6;
      color: #606266;
      
      &:hover {
        background: #e4e7ed;
        border-color: #c0c4cc;
        color: #303133;
      }
      
      .el-icon {
        margin-right: 0;
        margin-left: 4px;
      }
    }
  }
}

.points-log-card {
  border-radius: 12px;
  
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
</style>

