<template>
  <div class="user-detail-page">
    <!-- 返回按钮 -->
    <BackButton />
    
    <div v-loading="loading" class="user-detail-content">
      <!-- 用户基本信息卡片 -->
      <div class="user-info-card">
        <div class="user-header">
          <el-avatar :size="120" :src="userInfo.avatar">
            {{ userInfo.nickname?.charAt(0) || '用' }}
          </el-avatar>
          <div class="user-basic-info">
            <h2 class="user-name">{{ userInfo.nickname || '未设置昵称' }}</h2>
            <div class="user-level">
              <span 
                v-if="userInfo.levelName"
                class="level-badge" 
                :style="{ 
                  '--level-color-start': userInfo.levelGradient?.start || userInfo.levelColor, 
                  '--level-color-end': userInfo.levelGradient?.end || userInfo.levelColor 
                }"
              >
                {{ userInfo.levelName }}
              </span>
              <span class="user-points">积分: {{ userInfo.points || 0 }}</span>
            </div>
            <div class="user-stats">
              <div class="stat-item" @click="showFollowersDialog = true">
                <div class="stat-value">{{ userStats.followers || 0 }}</div>
                <div class="stat-label">粉丝</div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item" @click="showFollowingDialog = true">
                <div class="stat-value">{{ userStats.following || 0 }}</div>
                <div class="stat-label">关注</div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.plans || 0 }}</div>
                <div class="stat-label">攻略</div>
              </div>
            </div>
          </div>
          <div class="user-actions">
            <template v-if="!isOwnProfile">
              <el-button
                class="message-btn"
                type="primary"
                plain
                :icon="ChatLineSquare"
                @click="handleSendMessage"
              >
                私信Ta
              </el-button>
              <el-button
                class="follow-btn"
                @click="toggleFollow"
                :loading="followLoading"
              >
                {{ isFollowing ? '取消关注' : '关注' }}
              </el-button>
            </template>
            <el-button v-else class="edit-profile-btn" @click="$router.push('/home/user/profile-edit')">
              编辑个人信息
            </el-button>
          </div>
        </div>
      </div>

      <!-- 用户画像和发布的攻略容器 -->
      <div class="portrait-plans-container">
        <!-- 用户画像卡片 -->
        <div class="portrait-card">
          <h3 class="card-title">
            <el-icon><User /></el-icon>
            用户画像
          </h3>
        <div v-if="(portraitData && Object.keys(portraitData).length > 0) || basicInfo" class="portrait-content">
          <!-- 画像总结 -->
          <div v-if="portraitSummary" class="portrait-summary">
            <p class="summary-text">{{ portraitSummary }}</p>
          </div>
          
          <!-- 用户画像信息网格布局 -->
          <div class="portrait-info-grid">
            <!-- 基本资料 -->
            <div v-if="basicInfo" class="portrait-info-card basic-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><User /></el-icon>
                <h4 class="card-title">基本资料</h4>
              </div>
              <div class="card-content">
                <div class="info-row">
                  <span class="info-label">用户名</span>
                  <span class="info-value">{{ getSafeFieldValue(basicInfo.username) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">性别</span>
                  <span class="info-value">{{ basicInfo.gender !== undefined && basicInfo.gender !== null ? getGenderText(basicInfo.gender) : '----' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">年龄</span>
                  <span class="info-value">{{ basicInfo.age !== undefined && basicInfo.age !== null ? `${basicInfo.age}岁` : '----' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">手机号</span>
                  <span class="info-value">{{ getSafeFieldValue(basicInfo.phone) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">邮箱</span>
                  <span class="info-value">{{ getSafeFieldValue(basicInfo.email) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">出行偏好</span>
                  <span class="info-value">{{ basicInfo.travelPreference !== undefined && basicInfo.travelPreference !== null ? getTravelPreferenceTypeText(basicInfo.travelPreference) : '----' }}</span>
                </div>
              </div>
            </div>
            
            <!-- 兴趣标签 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><CollectionTag /></el-icon>
                <h4 class="card-title">兴趣标签</h4>
              </div>
              <div class="card-content">
                <div v-if="portraitData.interestTags && Array.isArray(portraitData.interestTags) && portraitData.interestTags.length > 0" class="tags-container">
                  <el-tag 
                    v-for="(tag, index) in portraitData.interestTags" 
                    :key="index" 
                    class="interest-tag"
                  >
                    {{ getSafeFieldValue(tag) }}
                  </el-tag>
                </div>
                <div v-else class="tags-container">
                  <span class="empty-tag">----</span>
                </div>
              </div>
            </div>

            <!-- 旅游偏好 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><Location /></el-icon>
                <h4 class="card-title">旅游偏好</h4>
              </div>
              <div class="card-content">
                <div class="preference-badge">
                  {{ getSafeFieldValue(portraitData.primaryPreference || getTravelPreferenceText(portraitData.travelPreference)) }}
                </div>
              </div>
            </div>

            <!-- 消费水平 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><Money /></el-icon>
                <h4 class="card-title">消费水平</h4>
              </div>
              <div class="card-content">
                <div class="consumption-badge">
                  {{ getConsumptionLevelText(portraitData.consumptionLevel) }}
                </div>
              </div>
            </div>

            <!-- 出行偏好 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><Compass /></el-icon>
                <h4 class="card-title">出行偏好</h4>
              </div>
              <div class="card-content">
                <div class="preference-badge">
                  {{ (basicInfo.travelPreference !== undefined && basicInfo.travelPreference !== null) 
                      ? getTravelPreferenceTypeText(basicInfo.travelPreference) 
                      : '----' }}
                </div>
              </div>
            </div>

            <!-- 旅游时长偏好 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><Clock /></el-icon>
                <h4 class="card-title">旅游时长偏好</h4>
              </div>
              <div class="card-content">
                <div class="preference-badge">
                  {{ getSafeFieldValue(portraitData.tripDuration) || '----' }}
                </div>
              </div>
            </div>

            <!-- 季节偏好 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><Sunny /></el-icon>
                <h4 class="card-title">季节偏好</h4>
              </div>
              <div class="card-content">
                <div class="preference-badge">
                  {{ getSafeFieldValue(portraitData.seasonPreference) || '----' }}
                </div>
              </div>
            </div>

            <!-- 平均消费 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><Wallet /></el-icon>
                <h4 class="card-title">平均消费</h4>
              </div>
              <div class="card-content">
                <div class="consumption-badge">
                  {{ portraitData.avgConsumption ? `¥${portraitData.avgConsumption}/天` : '----' }}
                </div>
              </div>
            </div>

            <!-- 消费频次 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><TrendCharts /></el-icon>
                <h4 class="card-title">消费频次</h4>
              </div>
              <div class="card-content">
                <div class="preference-badge">
                  {{ getSafeFieldValue(portraitData.consumptionFrequency) || '----' }}
                </div>
              </div>
            </div>

            <!-- 价格敏感度 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><DataAnalysis /></el-icon>
                <h4 class="card-title">价格敏感度</h4>
              </div>
              <div class="card-content">
                <div class="preference-badge">
                  {{ getSafeFieldValue(portraitData.priceSensitivity) || '----' }}
                </div>
              </div>
            </div>

            <!-- 常去城市 -->
            <div class="portrait-info-card">
              <div class="card-header">
                <el-icon class="card-icon"><LocationFilled /></el-icon>
                <h4 class="card-title">常去城市</h4>
              </div>
              <div class="card-content">
                <div v-if="portraitData.frequentDestinations && getFrequentCities(portraitData.frequentDestinations).length > 0" class="cities-container">
                  <el-tag 
                    v-for="(city, index) in getFrequentCities(portraitData.frequentDestinations)" 
                    :key="index" 
                    class="city-tag"
                  >
                    {{ getSafeFieldValue(city) }}
                  </el-tag>
                </div>
                <div v-else class="cities-container">
                  <span class="empty-tag">----</span>
                </div>
              </div>
            </div>
          </div>
        </div>
          <el-empty v-else description="暂无画像信息" />
        </div>

        <!-- 发布的攻略 -->
        <div class="plans-card">
        <h3 class="card-title">
          <el-icon><Document /></el-icon>
          发布的攻略
        </h3>
        <div v-if="plans.length > 0" class="plans-list">
          <div 
            v-for="plan in plans" 
            :key="plan.id" 
            class="plan-item"
            @click="viewPlan(plan.id)"
          >
            <div class="plan-cover">
              <img :src="plan.coverImage || '/default-cover.jpg'" :alt="plan.title" />
            </div>
            <div class="plan-info">
              <h4 class="plan-title">{{ plan.title }}</h4>
              <p class="plan-desc">{{ plan.description || '暂无描述' }}</p>
              <div class="plan-meta">
                <span class="plan-destination">
                  <el-icon><Location /></el-icon>
                  {{ plan.destination }}
                </span>
                <span class="plan-stats">
                  <span><el-icon><View /></el-icon> {{ plan.viewCount || 0 }}</span>
                  <span><el-icon><Star /></el-icon> {{ plan.likeCount || 0 }}</span>
                  <span><el-icon><ChatDotRound /></el-icon> {{ plan.commentCount || 0 }}</span>
                </span>
                <span class="plan-time">{{ formatTime(plan.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无发布的攻略" />
        
        <!-- 分页器 -->
        <div v-if="planTotal > 0" class="plan-pagination-simple">
          <el-button 
            :disabled="planCurrentPage <= 1"
            class="page-btn"
            @click="handlePlanPageChange(planCurrentPage - 1)"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <span class="page-info">{{ planCurrentPage }} / {{ Math.ceil(planTotal / planPageSize) }}</span>
          <el-button 
            :disabled="planCurrentPage >= Math.ceil(planTotal / planPageSize)"
            class="page-btn"
            @click="handlePlanPageChange(planCurrentPage + 1)"
          >
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        </div>
      </div>
    </div>

    <!-- 粉丝列表对话框 -->
    <el-dialog v-model="showFollowersDialog" title="粉丝列表" width="600px">
      <div v-if="followersList.length > 0" class="follow-list">
        <div 
          v-for="follower in followersList" 
          :key="follower.id" 
          class="follow-item"
          @click="viewUser(follower.followerId)"
        >
          <el-avatar :size="50" :src="follower.followerAvatar">
            {{ follower.followerName?.charAt(0) }}
          </el-avatar>
          <div class="follow-user-info">
            <span class="follow-user-name">{{ follower.followerName }}</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无粉丝" />
    </el-dialog>

    <!-- 关注列表对话框 -->
    <el-dialog v-model="showFollowingDialog" title="关注列表" width="600px">
      <div v-if="followingList.length > 0" class="follow-list">
        <div 
          v-for="following in followingList" 
          :key="following.id" 
          class="follow-item"
          @click="viewUser(following.followeeId)"
        >
          <el-avatar :size="50" :src="following.followeeAvatar">
            {{ following.followeeName?.charAt(0) }}
          </el-avatar>
          <div class="follow-user-info">
            <span class="follow-user-name">{{ following.followeeName }}</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无关注" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  User, Document, Location, View, Star, ChatDotRound, Plus, Edit, ChatLineSquare,
  CollectionTag, Money, Calendar, LocationFilled, ArrowLeft, ArrowRight,
  Compass, Clock, Sunny, Wallet, TrendCharts, DataAnalysis
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { getLevelByCode, getLevelByName, getLevelByPoints } from '@/utils/level'
import { formatDateTime } from '@/utils'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const followLoading = ref(false)
// 初始化userId：如果有路由参数则使用，否则使用当前登录用户的ID
const getInitialUserId = () => {
  const routeId = route.params.id
  if (routeId && !isNaN(Number(routeId))) {
    return Number(routeId)
  }
  const currentUser = userStore.userInfo || userStore.user
  return currentUser?.id || 0
}
const userId = ref(getInitialUserId())

// 用户信息
const userInfo = ref({
  id: 0,
  nickname: '',
  avatar: '',
  levelName: '',
  levelColor: '',
  levelGradient: null,
  points: 0
})

// 统计数据
const userStats = ref({
  followers: 0,
  following: 0,
  plans: 0
})

// 是否关注
const isFollowing = ref(false)

// 用户画像
const portraitData = ref<any>({})

// 基本资料
const basicInfo = ref<any>({})

// 画像总结词（计算属性，实时更新，更自然、更具体的描述）
const portraitSummary = computed(() => {
  const hasPortraitData = portraitData.value && Object.keys(portraitData.value).length > 0
  const hasBasicInfo = basicInfo.value && Object.keys(basicInfo.value).length > 0
  
  if (!hasPortraitData && !hasBasicInfo) {
    return ''
  }
  
  const traits: string[] = []
  const behaviors: string[] = []
  const preferences: string[] = []
  
  // 年龄段特征
  let ageNum: number | null = null
  if (portraitData.value?.ageGroup) {
    const age = portraitData.value.ageGroup.replace('岁', '')
    ageNum = parseInt(age)
  } else if (basicInfo.value?.age !== undefined && basicInfo.value?.age !== null) {
    ageNum = parseInt(String(basicInfo.value.age))
  }
  
  if (ageNum !== null && !isNaN(ageNum)) {
    if (ageNum < 25) {
      traits.push('年轻')
    } else if (ageNum < 30) {
      traits.push('充满活力')
    } else if (ageNum < 35) {
      traits.push('成熟')
    } else if (ageNum < 45) {
      traits.push('阅历丰富')
    } else if (ageNum < 55) {
      traits.push('资深')
    } else {
      traits.push('经验深厚')
    }
  }
  
  // 兴趣标签 - 用更自然的语言描述
  if (portraitData.value?.interestTags && Array.isArray(portraitData.value.interestTags) && portraitData.value.interestTags.length > 0) {
    const tags = portraitData.value.interestTags
    const tagNames = tags.map((tag: any) => {
      const tagValue = getSafeFieldValue(tag)
      return tagValue !== '----' ? tagValue : null
    }).filter((name: string | null): name is string => name !== null && name.trim().length > 0 && !name.includes('[object'))
    
    if (tagNames.length > 0) {
      // 根据标签组合生成更自然的描述
      const tagDescriptions: Record<string, string> = {
        '摄影': '喜欢用镜头记录旅途中的美好瞬间',
        '美食': '热衷于探索各地特色美食',
        '文化': '对历史文化有着浓厚兴趣',
        '自然风光': '偏爱大自然的壮美景色',
        '夜景': '享受城市夜晚的繁华与宁静',
        '情侣': '喜欢与伴侣一起创造浪漫回忆',
        '家庭': '注重家庭旅行的温馨体验',
        '购物': '享受在旅途中发现独特商品的乐趣',
        '休闲': '追求轻松惬意的旅行方式'
      }
      
      // 优先使用特定组合的描述
      if (tagNames.includes('情侣') && tagNames.includes('夜景')) {
        behaviors.push('喜欢与伴侣一起欣赏城市夜景，享受浪漫时光')
      } else if (tagNames.includes('美食') && tagNames.length > 1) {
        behaviors.push(`热爱美食探索，同时${tagNames.filter(t => t !== '美食').slice(0, 2).join('和')}也是其旅行中的重要元素`)
      } else if (tagNames.length === 1) {
        const desc = tagDescriptions[tagNames[0]] || `对${tagNames[0]}情有独钟`
        behaviors.push(desc)
      } else if (tagNames.length <= 3) {
        behaviors.push(`在旅行中，${tagNames.join('、')}都是其关注的重点`)
      } else {
        behaviors.push(`旅行兴趣广泛，尤其关注${tagNames.slice(0, 3).join('、')}等方面`)
      }
    }
  }
  
  // 旅游偏好 - 更具体的描述
  let prefText = ''
  if (portraitData.value?.primaryPreference) {
    const prefValue = getSafeFieldValue(portraitData.value.primaryPreference)
    if (prefValue && prefValue !== '----') {
      prefText = prefValue
    }
  }
  
  if (prefText && prefText !== '----' && !prefText.includes('[object')) {
    const preferenceDescriptions: Record<string, string> = {
      '自然风光': '更愿意选择山川湖海等自然景观作为目的地',
      '人文历史': '对古迹、博物馆、历史建筑等文化场所格外感兴趣',
      '美食体验': '旅行计划往往围绕当地特色美食展开',
      '购物娱乐': '喜欢在旅途中购物和体验娱乐项目',
      '休闲度假': '倾向于选择轻松舒适的度假方式',
      '冒险探索': '追求刺激和新奇的旅行体验',
      '温泉度假': '偏好温泉、SPA等放松身心的旅行方式',
      '宗教场所': '对宗教文化和建筑艺术有独特兴趣',
      '主题公园': '喜欢在主题公园中寻找乐趣'
    }
    preferences.push(preferenceDescriptions[prefText] || `更偏好${prefText}类型的旅行目的地`)
  }
  
  // 消费水平 - 更具体的描述
  let consumptionLevelText = getConsumptionLevelText(portraitData.value?.consumptionLevel)
  if (consumptionLevelText && consumptionLevelText !== '----') {
    const consumptionDescriptions: Record<string, string> = {
      '经济实惠型': '在旅行中注重性价比，善于寻找物美价廉的体验',
      '品质舒适型': '愿意为更好的住宿和体验支付合理费用',
      '豪华型': '追求高品质的旅行体验，不吝于为舒适和享受投入',
      '高端奢华型': '偏好高端酒店和精致服务，追求极致旅行体验',
      '性价比型': '在预算范围内追求最佳体验'
    }
    behaviors.push(consumptionDescriptions[consumptionLevelText] || `消费水平属于${consumptionLevelText}`)
  }
  
  // 出行方式 - 更具体的描述
  if (portraitData.value?.travelMode) {
    const travelMode = String(portraitData.value.travelMode)
    if (travelMode.includes('自由行')) {
      behaviors.push('更倾向于自由行，享受自主规划行程的乐趣')
    } else if (travelMode.includes('自驾')) {
      behaviors.push('喜欢自驾出行，享受路上的自由与灵活')
    } else if (travelMode.includes('跟团')) {
      behaviors.push('偏好跟团旅行，享受省心省力的旅行方式')
    }
  }
  
  // 旅游时长 - 更具体的描述
  if (portraitData.value?.tripDuration) {
    const duration = String(portraitData.value.tripDuration)
    if (duration.includes('1-3') || duration.includes('短途')) {
      behaviors.push('通常选择短途旅行，适合周末或小长假出行')
    } else if (duration.includes('3-5')) {
      behaviors.push('偏好中短途旅行，既能深度体验又不会过于疲惫')
    } else if (duration.includes('5-7') || duration.includes('7')) {
      behaviors.push('喜欢安排较长的旅行时间，追求深度体验')
    }
  }
  
  // 季节偏好
  if (portraitData.value?.seasonPreference) {
    const season = String(portraitData.value.seasonPreference)
    if (season.includes('春季') || season.includes('春天')) {
      preferences.push('偏爱春季出行，享受万物复苏的美好')
    } else if (season.includes('秋季') || season.includes('秋天')) {
      preferences.push('喜欢在秋高气爽的季节踏上旅程')
    } else if (season.includes('夏季') || season.includes('夏天')) {
      preferences.push('更愿意在夏季出行，享受阳光与活力')
    } else if (season.includes('冬季') || season.includes('冬天')) {
      preferences.push('偏好冬季旅行，体验不同季节的独特魅力')
    }
  }
  
  // 常去目的地
  if (portraitData.value?.frequentDestinations) {
    const destinations = getFrequentCities(portraitData.value.frequentDestinations)
    if (destinations.length > 0) {
      if (destinations.length === 1) {
        preferences.push(`经常前往${destinations[0]}，对这座城市有着特殊的情感`)
      } else if (destinations.length <= 3) {
        preferences.push(`常去的目的地包括${destinations.join('、')}等地`)
      } else {
        preferences.push(`足迹遍布${destinations.slice(0, 3).join('、')}等多个城市`)
      }
    }
  }
  
  // 统计数据补充
  if (portraitData.value?.browseCount > 0 || portraitData.value?.favoriteCount > 0) {
    const browseCount = portraitData.value.browseCount || 0
    const favoriteCount = portraitData.value.favoriteCount || 0
    
    if (browseCount > 100 || favoriteCount > 20) {
      behaviors.push('是一位活跃的旅行爱好者，经常浏览和收藏感兴趣的旅行内容')
    } else if (browseCount > 50 || favoriteCount > 10) {
      behaviors.push('对旅行充满热情，会主动探索和收集旅行信息')
    }
  }
  
  // 构建最终描述 - 更自然、更丰富的表达
  if (traits.length === 0 && behaviors.length === 0 && preferences.length === 0) {
    return ''
  }
  
  let summary = ''
  
  // 开头：年龄特征 + 身份定位
  if (traits.length > 0) {
    summary = `这是一位${traits[0]}的旅行者`
  } else {
    summary = '这是一位旅行者'
  }
  
  // 中间部分：行为特征和偏好，用更自然的语言连接
  const allDescriptions = [...behaviors, ...preferences]
  
  if (allDescriptions.length > 0) {
    if (allDescriptions.length === 1) {
      summary += `，${allDescriptions[0]}`
    } else if (allDescriptions.length === 2) {
      summary += `，${allDescriptions[0]}，同时${allDescriptions[1]}`
    } else {
      // 3个或更多描述，用更自然的连接
      const firstPart = allDescriptions.slice(0, allDescriptions.length - 1).join('，')
      const lastPart = allDescriptions[allDescriptions.length - 1]
      summary += `，${firstPart}，此外${lastPart}`
    }
  }
  
  summary += '。'
  
  // 添加互动性文字
  const interactiveMessages = [
    '希望多多关照，期待与志同道合的旅友一起分享旅行心得！',
    '如果有和我一样兴趣的旅友，欢迎一起交流旅行经验！',
    '期待认识更多热爱旅行的朋友，一起探索美好的世界！',
    '希望在这里遇到志同道合的旅友，一起分享旅途中的精彩！',
    '欢迎有相同旅行偏好的朋友多多交流，一起发现更多美好！',
    '期待与同样热爱旅行的你相遇，一起分享旅途中的点点滴滴！',
    '如果有和我一样喜欢探索的朋友，欢迎一起交流互动！',
    '希望在这里找到志同道合的旅友，一起开启更多精彩的旅程！'
  ]
  
  // 根据用户特征选择更合适的互动文字
  let interactiveText = ''
  if (portraitData.value?.interestTags && Array.isArray(portraitData.value.interestTags) && portraitData.value.interestTags.length > 0) {
    const tagCount = portraitData.value.interestTags.length
    if (tagCount >= 3) {
      interactiveText = '希望多多关照，期待与志同道合的旅友一起分享旅行心得！'
    } else {
      interactiveText = '如果有和我一样兴趣的旅友，欢迎一起交流旅行经验！'
    }
  } else if (portraitData.value?.primaryPreference) {
    interactiveText = '期待认识更多热爱旅行的朋友，一起探索美好的世界！'
  } else {
    interactiveText = '希望在这里遇到志同道合的旅友，一起分享旅途中的精彩！'
  }
  
  summary += ` ${interactiveText}`
  
  return summary
})

// 旅游偏好映射
const travelPreferenceMap: Record<number | string, string> = {
  1: '自然风光',
  2: '人文历史',
  3: '美食体验',
  4: '购物娱乐',
  5: '休闲度假',
  6: '冒险探索'
}

// 安全获取字段值，避免显示 [object Object]
const getSafeFieldValue = (value: any): string => {
  if (value === null || value === undefined || value === '') {
    return '----'
  }
  
  // 如果是对象，尝试获取常见属性
  if (typeof value === 'object') {
    if (value.level) return String(value.level)
    if (value.name) return String(value.name)
    if (value.label) return String(value.label)
    if (value.value) return String(value.value)
    if (value.text) return String(value.text)
    // 如果对象没有可用的属性，返回 ----
    return '----'
  }
  
  // 如果是字符串，检查是否包含 [object Object]
  const strValue = String(value)
  if (strValue.includes('[object Object]') || strValue.includes('[object')) {
    return '----'
  }
  
  return strValue.trim() || '----'
}

// 获取消费水平文本
const getConsumptionLevelText = (value: any): string => {
  if (value === null || value === undefined || value === '') {
    return '----'
  }
  
  // 如果是对象，尝试获取 level 属性
  if (typeof value === 'object') {
    if (value.level) {
      return String(value.level)
    }
    // 如果对象没有 level 属性，返回 ----
    return '----'
  }
  
  // 如果是字符串，检查是否包含 [object Object]
  const strValue = String(value)
  if (strValue.includes('[object Object]') || strValue.includes('[object')) {
    return '----'
  }
  
  return strValue.trim() || '----'
}

// 获取旅游偏好文本
const getTravelPreferenceText = (value: number | string | null | undefined): string => {
  if (value === null || value === undefined) {
    return ''
  }
  
  // 如果是数字，直接查找
  if (typeof value === 'number') {
    return travelPreferenceMap[value] || `未知类型(${value})`
  }
  
  // 如果是字符串，尝试转换为数字后查找
  if (typeof value === 'string') {
    const numValue = parseInt(value)
    if (!isNaN(numValue)) {
      return travelPreferenceMap[numValue] || value
    }
    // 如果不是数字，可能是已经转换好的文本，直接返回
    return value
  }
  
  return String(value)
}

// 获取性别文本
const getGenderText = (gender: number | null | undefined): string => {
  if (gender === null || gender === undefined) {
    return '保密'
  }
  switch (gender) {
    case 0:
      return '保密'
    case 1:
      return '男'
    case 2:
      return '女'
    default:
      return '未知'
  }
}

// 获取出行偏好类型文本（travelPreference字段：1-个人, 2-情侣, 3-家庭, 4-团队, 5-商务, 6-其他）
const getTravelPreferenceTypeText = (travelPreference: number | null | undefined): string => {
  if (travelPreference === null || travelPreference === undefined) {
    return '----'
  }
  switch (travelPreference) {
    case 1:
      return '个人'
    case 2:
      return '情侣'
    case 3:
      return '家庭'
    case 4:
      return '团队'
    case 5:
      return '商务'
    case 6:
      return '其他'
    default:
      return '----'
  }
}

// 获取出行类型文本（userType字段，保留用于兼容）
const getUserTypeText = (userType: number | null | undefined): string => {
  if (userType === null || userType === undefined) {
    return '-'
  }
  switch (userType) {
    case 1:
      return '个人'
    case 2:
      return '情侣'
    case 3:
      return '家庭'
    case 4:
      return '团队'
    default:
      return '未知'
  }
}

// 攻略列表
const plans = ref<any[]>([])
const planCurrentPage = ref(1)
const planPageSize = ref(7)
const planTotal = ref(0)

// 粉丝/关注列表
const showFollowersDialog = ref(false)
const showFollowingDialog = ref(false)
const followersList = ref<any[]>([])
const followingList = ref<any[]>([])

// 是否是自己的主页
const isOwnProfile = computed(() => {
  const currentUser = userStore.userInfo || userStore.user
  const currentUserId = currentUser?.id
  const targetUserId = userId.value
  // 确保两个ID都存在且相等，或者都是0（未登录状态）
  if (!currentUserId || !targetUserId) {
    return false
  }
  return currentUserId === targetUserId
})

// 加载用户信息
const loadUserInfo = async () => {
  loading.value = true
  try {
    // 获取用户详情（包含画像）
    const userResponse = await request.get(`/user/detail/${userId.value}`)
    if (userResponse.code === 200 && userResponse.data) {
      const data = userResponse.data
      
      // 根据升级指南计算等级（统一使用积分）
      const points = data.points !== undefined && data.points !== null ? data.points : 0
      const authorLevelInfo = getLevelByPoints(points)
      
      userInfo.value = {
        id: data.id,
        nickname: data.nickname || data.username || '未设置昵称',
        avatar: data.avatar || '',
        levelName: authorLevelInfo.name,
        levelColor: authorLevelInfo.color,
        levelGradient: authorLevelInfo.gradient || { start: authorLevelInfo.color, end: authorLevelInfo.color },
        points: data.points || 0
      }
      
      // 设置基本资料（仅基本信息，画像数据从用户画像API获取）
      basicInfo.value = {
        username: data.username || '',
        gender: data.gender,
        phone: data.phone || '',
        email: data.email || '',
        travelPreference: data.travelPreference, // 使用出行偏好字段
        age: data.age // 保存年龄，用于画像总结
      }
    }

    // 获取关注统计
    const statsResponse = await request.get('/user/follow/stats', {
      params: { userId: userId.value }
    })
    if (statsResponse.code === 200 && statsResponse.data) {
      userStats.value.followers = statsResponse.data.followers || 0
      userStats.value.following = statsResponse.data.following || 0
    }

    // 检查是否关注
    const currentUser = userStore.userInfo || userStore.user
    if (currentUser?.id && !isOwnProfile.value) {
      const followResponse = await request.get('/user/follow/is-following', {
        params: {
          followerId: currentUser.id,
          followeeId: userId.value
        }
      })
      if (followResponse.code === 200 && followResponse.data) {
        isFollowing.value = followResponse.data.isFollowing || false
      }
    }

    // 加载用户画像（从用户画像API获取完整数据）
    await loadPortraitData()

    // 加载攻略列表
    await loadPlans()

  } catch (error: any) {
    console.error('加载用户信息失败:', error)
    ElMessage.error(error?.message || '加载用户信息失败')
  } finally {
    loading.value = false
  }
}

// 加载用户画像（从用户画像API实时获取完整数据）
const loadPortraitData = async () => {
  try {
    // 从用户画像API获取完整数据（与用户画像分析页面使用相同的数据源）
    // 注意：如果查看的是其他用户，需要传递userId参数
    const targetUserId = userId.value
    const currentUser = userStore.userInfo || userStore.user
    
    let response
    if (targetUserId && currentUser?.id && targetUserId !== currentUser.id) {
      // 查看其他用户的画像，使用带userId的接口
      response = await request.get(`/user/portrait/${targetUserId}`)
    } else {
      // 查看自己的画像
      response = await request.get('/user/portrait')
    }
    
    if (response.code === 200 && response.data) {
      const data = response.data
      
      // 完整更新画像数据，确保所有字段都从用户画像API获取
      portraitData.value = {
        // 旅游偏好（景点类型偏好，如"自然风光"、"宗教场所"等）
        primaryPreference: data.primaryPreference || '',
        preferenceDistribution: data.preferenceDistribution || [],
        
        // 出行偏好（出行类型，如"个人"、"情侣"、"家庭"等，从basicInfo获取）
        // travelPreference 字段在 basicInfo 中，不在 portraitData 中
        
        // 兴趣标签（处理可能是对象数组的情况）
        interestTags: (() => {
          if (data.interestTags && Array.isArray(data.interestTags)) {
            return data.interestTags.map((tag: any) => {
              if (typeof tag === 'string') {
                return tag
              } else if (tag && typeof tag === 'object') {
                return tag.name || tag.tag || tag.label || String(tag)
              }
              return String(tag)
            }).filter((name: string) => name && name.trim().length > 0)
          }
          return []
        })(),
        
        // 消费水平（处理可能是对象的情况）
        consumptionLevel: (() => {
          if (data.consumptionLevel) {
            if (typeof data.consumptionLevel === 'object' && data.consumptionLevel.level) {
              return data.consumptionLevel.level
            }
            return String(data.consumptionLevel)
          }
          return ''
        })(),
        
        // 消费行为详细数据
        avgConsumption: data.avgConsumption || 0,
        consumptionFrequency: data.consumptionFrequency || '',
        priceSensitivity: data.priceSensitivity || '',
        
        // 常去城市
        frequentDestinations: data.frequentDestinations || '',
        
        // 出行特征
        travelMode: data.travelMode || '',
        tripDuration: data.tripDuration || '',
        seasonPreference: data.seasonPreference || '',
        
        // 统计数据（用于画像总结）
        browseCount: data.browseCount || 0,
        favoriteCount: data.favoriteCount || 0,
        searchCount: data.searchCount || 0,
        avgStayTime: data.avgStayTime || 0
      }
      
      console.log('✅ 用户画像数据已从API实时获取:', portraitData.value)
    } else {
      console.warn('⚠️ 获取用户画像数据失败:', response.message)
    }
  } catch (error) {
    console.error('❌ 加载用户画像数据失败:', error)
    // 如果加载失败，清空画像数据，避免显示错误数据
    portraitData.value = {}
  }
}

// 加载攻略列表
const loadPlans = async () => {
  try {
    const response = await request.get(`/travel-plan/author/${userId.value}`, {
      params: {
        page: planCurrentPage.value,
        limit: planPageSize.value,
        status: 1 // 只显示已发布的
      }
    })
    if (response.code === 200 && response.data) {
      // 使用后端返回的总数，更准确
      if (response.data.total !== undefined) {
        planTotal.value = response.data.total
        userStats.value.plans = response.data.total
      }
      
      // 处理攻略列表数据
      if (response.data.list) {
        plans.value = response.data.list.map((plan: any) => {
          let coverImage = plan.coverImage || ''
          if (!coverImage && plan.images) {
            if (typeof plan.images === 'string') {
              const imageArray = plan.images.split(',').filter((img: string) => img.trim())
              if (imageArray.length > 0) {
                coverImage = imageArray[0].trim().replace(/["']/g, '')
              }
            } else if (Array.isArray(plan.images) && plan.images.length > 0) {
              coverImage = plan.images[0]
            }
          }
          return {
            ...plan,
            coverImage: coverImage || '/default-cover.jpg'
          }
        })
        
        // 如果后端没有返回total，使用列表长度
        if (response.data.total === undefined) {
          planTotal.value = plans.value.length
          userStats.value.plans = plans.value.length
        }
      } else {
        plans.value = []
        planTotal.value = 0
        userStats.value.plans = 0
      }
    }
  } catch (error) {
    console.error('加载攻略列表失败:', error)
    plans.value = []
    planTotal.value = 0
    userStats.value.plans = 0
  }
}

// 分页大小改变
const handlePlanSizeChange = (size: number) => {
  planPageSize.value = size
  planCurrentPage.value = 1
  loadPlans()
}

// 当前页改变
const handlePlanPageChange = (page: number) => {
  planCurrentPage.value = page
  loadPlans()
}

// 关注/取消关注
const toggleFollow = async () => {
  if (isOwnProfile.value) return
  
  followLoading.value = true
  try {
    const currentUser = userStore.userInfo || userStore.user
    if (!currentUser?.id) {
      ElMessage.warning('请先登录')
      return
    }

    if (isFollowing.value) {
      // 取消关注
      const response = await request.post('/user/follow/unfollow', null, {
        params: {
          followerId: currentUser.id,
          followeeId: userId.value
        }
      })
      if (response.code === 200) {
        isFollowing.value = false
        userStats.value.followers--
        ElMessage.success('已取消关注')
      }
    } else {
      // 关注
      const response = await request.post('/user/follow/follow', null, {
        params: {
          followerId: currentUser.id,
          followeeId: userId.value
        }
      })
      if (response.code === 200) {
        isFollowing.value = true
        userStats.value.followers++
        ElMessage.success('关注成功')
      }
    }
  } catch (error: any) {
    console.error('操作失败:', error)
    ElMessage.error(error?.message || '操作失败')
  } finally {
    followLoading.value = false
  }
}

const handleSendMessage = () => {
  if (isOwnProfile.value) return
  const targetId = userInfo.value.id
  if (!targetId) {
    ElMessage.warning('用户信息加载中，请稍后再试')
    return
  }
  const currentUser = userStore.userInfo || userStore.user
  if (!currentUser?.id) {
    ElMessage.warning('请先登录')
    router.push('/')
    return
  }
  router.push({
    path: '/home/user/messages',
    query: {
      targetId: String(targetId),
      targetName: userInfo.value.nickname || basicInfo.value.username || '旅行达人',
      targetAvatar: userInfo.value.avatar || ''
    }
  })
}

// 查看攻略
const viewPlan = (planId: number) => {
  router.push(`/home/user/plans/detail/${planId}`)
}

// 查看用户
const viewUser = (targetUserId: number) => {
  // 关闭对话框
  showFollowersDialog.value = false
  showFollowingDialog.value = false
  // 跳转到用户页面
  router.push(`/home/user/profile/${targetUserId}`)
}

// 加载粉丝列表
const loadFollowers = async () => {
  try {
    const response = await request.get('/user/follow/followers-list', {
      params: {
        userId: userId.value,
        page: 1,
        limit: 100
      }
    })
    if (response.code === 200 && response.data) {
      followersList.value = response.data
    }
  } catch (error) {
    console.error('加载粉丝列表失败:', error)
  }
}

// 加载关注列表
const loadFollowing = async () => {
  try {
    const response = await request.get('/user/follow/following-list', {
      params: {
        userId: userId.value,
        page: 1,
        limit: 100
      }
    })
    if (response.code === 200 && response.data) {
      followingList.value = response.data
    }
  } catch (error) {
    console.error('加载关注列表失败:', error)
  }
}

// 格式化时间
const formatTime = (timeStr: string) => {
  return formatDateTime(timeStr)
}

// 处理常去城市数据（可能是字符串或数组）
const getFrequentCities = (destinations: any): string[] => {
  if (!destinations) {
    return []
  }
  
  if (Array.isArray(destinations)) {
    return destinations.map((city: any) => {
      if (typeof city === 'string') {
        return city
      } else if (city && typeof city === 'object') {
        return city.name || city.city || String(city)
      }
      return String(city)
    }).filter((city: string) => city && city.trim().length > 0)
  }
  
  if (typeof destinations === 'string') {
    // 处理字符串格式，可能是逗号、顿号、空格分隔
    return destinations.split(/[,，、\s]+/)
      .map((city: string) => city.trim())
      .filter((city: string) => city.length > 0)
  }
  
  return []
}

// 监听对话框显示
watch(showFollowersDialog, (val) => {
  if (val) {
    loadFollowers()
  }
})

watch(showFollowingDialog, (val) => {
  if (val) {
    loadFollowing()
  }
})

// 监听路由参数变化，更新userId并重新加载用户信息
watch(() => route.params.id, (newId) => {
  if (newId && !isNaN(Number(newId))) {
    userId.value = Number(newId)
    // 重置关注状态
    isFollowing.value = false
    // 重置分页状态
    planCurrentPage.value = 1
    planPageSize.value = 7
    planTotal.value = 0
    // 重新加载用户信息
    loadUserInfo()
  } else {
    // 如果没有路由参数，使用当前登录用户的ID
    const currentUser = userStore.userInfo || userStore.user
    userId.value = currentUser?.id || 0
    isFollowing.value = false
    // 重置分页状态
    planCurrentPage.value = 1
    planPageSize.value = 7
    planTotal.value = 0
    loadUserInfo()
  }
}, { immediate: false })

onMounted(() => {
  loadUserInfo()
})
</script>

<script lang="ts">
import { watch } from 'vue'
export default {}
</script>

<style scoped lang="scss">
.user-detail-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 60px);

  .user-detail-content {
    max-width: 1400px;
    margin: 0 auto;
  }

  .user-info-card {
    background: white;
    border-radius: 16px;
    padding: 30px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

    .user-header {
      display: flex;
      align-items: flex-start;
      gap: 24px;

      .user-basic-info {
        flex: 1;

        .user-name {
          font-size: 28px;
          font-weight: 700;
          margin: 0 0 8px 0;
          color: #303133;
        }

        .user-id {
          font-size: 14px;
          color: #909399;
          margin: 0 0 16px 0;
        }

        .user-level {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 20px;

          .level-badge {
            font-size: 12px;
            font-weight: 600;
            padding: 4px 12px;
            border-radius: 12px;
            background: linear-gradient(135deg, var(--level-color-start, #409eff), var(--level-color-end, #66b1ff));
            color: white;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
          }

          .user-points {
            font-size: 14px;
            color: #606266;
          }
        }

        .user-stats {
          display: flex;
          align-items: center;
          gap: 32px;

          .stat-item {
            cursor: pointer;
            text-align: center;

            .stat-value {
              font-size: 24px;
              font-weight: 700;
              color: #303133;
            }

            .stat-label {
              font-size: 14px;
              color: #909399;
              margin-top: 4px;
            }
          }

          .stat-divider {
            width: 1px;
            height: 40px;
            background: #e4e7ed;
          }
        }
      }

      .user-actions {
        flex-shrink: 0;
        display: flex;
        gap: 12px;

        :deep(.message-btn) {
          min-width: 130px;
          min-height: 44px;
          padding: 12px 20px;
          font-size: 15px;
          
          .el-icon {
            font-size: 18px;
          }
        }

        :deep(.follow-btn),
        :deep(.edit-profile-btn) {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 12px 20px;
          min-height: 44px;
          min-width: 110px;
          border-radius: 6px;
          border: 1px solid #dcdfe6 !important;
          background: #fff !important;
          color: #606266 !important;
          font-weight: 400;
          font-size: 15px;
          box-shadow: none;
          transition: all 0.2s ease;

          .el-icon {
            font-size: 18px;
            color: #606266 !important;
          }

          &:hover {
            border-color: #c0c4cc !important;
            color: #606266 !important;
            background: #fff !important;
          }

          &:focus {
            border-color: #c0c4cc !important;
            color: #606266 !important;
            background: #fff !important;
          }

          &:active {
            border-color: #c0c4cc !important;
            color: #606266 !important;
            background: #fff !important;
          }
        }
      }
    }
  }

    .portrait-plans-container {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 20px;

      @media (max-width: 992px) {
        grid-template-columns: 1fr;
      }
    }

    .portrait-card,
    .plans-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
      display: flex;
      flex-direction: column;

      .card-title {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0 0 20px 0;
        padding: 0 0 16px 0;
        border-bottom: 1px solid #ebeef5;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        width: 100%;
        box-sizing: border-box;

        .el-icon {
          color: #409eff;
        }
      }

      .portrait-content {
        flex: 1;
        display: flex;
        flex-direction: column;
      }

      .portrait-summary {
        background: #f5f7fa;
        padding: 16px 20px;
        border-radius: 8px;
        border: 1px solid #dcdfe6;
        margin-bottom: 24px;
        
        .summary-text {
          color: #606266;
          font-size: 14px;
          line-height: 1.6;
          margin: 0;
          font-weight: 400;
        }
      }

      // 用户画像信息网格布局
      .portrait-info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 20px;
        margin-top: 20px;
      }

      .portrait-info-card {
        background: #fafafa;
        border-radius: 12px;
        padding: 20px;
        border: 1px solid #f0f0f0;
        transition: none;
        
        &:hover {
          // 移除悬停效果
          background: #fafafa;
          border-color: #f0f0f0;
          transform: none;
          box-shadow: none;
        }

        .card-header {
          display: flex;
          align-items: baseline;
          gap: 8px;
          margin-bottom: 16px;
          padding-bottom: 12px;
          border-bottom: 1px solid #e8e8e8;
          line-height: 1.5;

          .card-icon {
            font-size: 18px;
            color: #667eea;
            flex-shrink: 0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            line-height: 1.5;
            width: 18px;
            height: 18px;
            margin: 0;
            padding: 0;
            vertical-align: baseline;
            
            :deep(.el-icon) {
              display: inline-flex;
              align-items: center;
              justify-content: center;
              width: 18px;
              height: 18px;
              line-height: 1.5;
              vertical-align: baseline;
            }
            
            :deep(svg) {
              width: 18px;
              height: 18px;
              vertical-align: baseline;
            }
          }

          .card-title {
            font-size: 16px;
            font-weight: 600;
            margin: 0;
            padding: 0;
            color: #303133;
            line-height: 1.5;
            display: inline-block;
            vertical-align: baseline;
            
            // 确保 h4 标签没有默认样式影响
            &, h4 {
              margin: 0;
              padding: 0;
              line-height: 1.5;
              vertical-align: baseline;
            }
          }
        }

        .card-content {
          .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #f5f5f5;
            
            &:last-child {
              border-bottom: none;
            }

            .info-label {
              font-size: 14px;
              color: #909399;
              font-weight: 500;
            }

            .info-value {
              font-size: 14px;
              color: #303133;
              font-weight: 500;
            }
          }

          .tags-container {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .empty-tag {
              color: #909399;
              font-size: 14px;
            }

            .interest-tag {
              font-size: 14px;
              padding: 6px 12px;
              border-radius: 4px;
              background: #f5f7fa !important;
              color: #606266 !important;
              border: 1px solid #dcdfe6 !important;
              font-weight: 400;
            }
          }

          .preference-badge,
          .consumption-badge,
          .age-badge {
            display: inline-block;
            padding: 6px 12px;
            background: #f5f7fa;
            color: #606266;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            font-size: 14px;
            font-weight: 400;
          }

          .cities-container {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .empty-tag {
              color: #909399;
              font-size: 14px;
            }

            .city-tag {
              font-size: 14px;
              padding: 6px 12px;
              border-radius: 4px;
              background: #f5f7fa !important;
              color: #606266 !important;
              border: 1px solid #dcdfe6 !important;
              font-weight: 400;
            }
          }
        }

        &.basic-info-card {
          grid-column: span 2;
          
          @media (max-width: 768px) {
            grid-column: span 1;
          }
        }
      }
  }

  .plans-list {
    flex: 1;
    margin-bottom: 20px;
    
    .plan-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s;
      border: 1px solid #f0f0f0;
      margin-bottom: 16px;

      &:hover {
        background: #f5f7fa;
        border-color: #409eff;
      }

      .plan-cover {
        width: 200px;
        height: 120px;
        flex-shrink: 0;
        border-radius: 8px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .plan-info {
        flex: 1;

        .plan-title {
          font-size: 18px;
          font-weight: 600;
          margin: 0 0 8px 0;
          color: #303133;
        }

        .plan-desc {
          font-size: 14px;
          color: #606266;
          margin: 0 0 12px 0;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .plan-meta {
          display: flex;
          align-items: center;
          gap: 16px;
          font-size: 13px;
          color: #909399;

          .plan-destination {
            display: flex;
            align-items: center;
            gap: 4px;
          }

          .plan-stats {
            display: flex;
            gap: 12px;

            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }

  .plan-pagination-simple {
    margin-top: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;

    .page-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 8px 12px;
      border-radius: 6px;
      border: 1px solid #dcdfe6;
      background: #fff;
      color: #606266;
      font-weight: 400;
      font-size: 14px;
      box-shadow: none;
      transition: all 0.2s ease;
      min-width: 40px;
      height: 36px;

      .el-icon {
        font-size: 16px;
        color: #606266;
      }

      &:hover:not(.is-disabled) {
        border-color: #dcdfe6;
        color: #303133;
        background: #f5f7fa;
      }

      &:active:not(.is-disabled) {
        background: #e4e7ed;
        border-color: #c0c4cc;
      }

      &:focus {
        border-color: #dcdfe6;
        color: #303133;
        background: #f5f7fa;
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
      min-width: 60px;
      text-align: center;
    }
  }

  .follow-list {
    .follow-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #f5f7fa;
      }

      .follow-user-info {
        flex: 1;

        .follow-user-name {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
        }
      }
    }
  }
}
</style>























