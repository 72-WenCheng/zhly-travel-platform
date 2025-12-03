<template>
  <view class="plan-page">
    <!-- 顶部搜索栏 -->
    <view class="search-bar">
      <view class="search-box" @click="goToSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索攻略、景点、美食...</text>
      </view>
      <view class="filter-btn" @click="showFilter = true">
        <text class="filter-icon">筛选</text>
      </view>
    </view>

    <!-- 快速入口 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToCreatePlan">
        <text class="action-icon">✍️</text>
        <text class="action-text">创建攻略</text>
      </view>
      <view class="action-item" @click="goToAIGenerate">
        <text class="action-icon">🤖</text>
        <text class="action-text">AI生成</text>
      </view>
      <view class="action-item" @click="goToMyPlans">
        <text class="action-icon">📚</text>
        <text class="action-text">我的攻略</text>
      </view>
      <view class="action-item" @click="goToMyCollection">
        <text class="action-icon">⭐</text>
        <text class="action-text">我的收藏</text>
      </view>
    </view>

    <!-- 分类标签 -->
    <view class="category-tabs">
      <scroll-view class="tabs-scroll" scroll-x="true" show-scrollbar="false">
        <view class="tabs-list">
          <view 
            v-for="tab in categoryTabs" 
            :key="tab.id"
            class="tab-item"
            :class="{ active: currentTab === tab.id }"
            @click="switchTab(tab.id)"
          >
            <text class="tab-text">{{ tab.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 攻略列表 -->
    <view class="plan-list">
      <view 
        v-for="plan in filteredPlans" 
        :key="plan.id"
        class="plan-card"
        @click="goToPlanDetail(plan.id)"
      >
        <image :src="plan.coverImage" class="plan-cover" mode="aspectFill" />
        <view class="plan-content">
          <view class="plan-header">
            <text class="plan-title">{{ plan.title }}</text>
            <view class="plan-status" :class="getStatusClass(plan.status)">
              <text class="status-text">{{ getStatusText(plan.status) }}</text>
            </view>
          </view>
          <text class="plan-desc">{{ plan.description }}</text>
          <view class="plan-meta">
            <view class="meta-item">
              <text class="meta-icon">👤</text>
              <text class="meta-text">{{ plan.author }}</text>
            </view>
            <view class="meta-item">
              <text class="meta-icon">👁️</text>
              <text class="meta-text">{{ plan.viewCount }}</text>
            </view>
            <view class="meta-item">
              <text class="meta-icon">⭐</text>
              <text class="meta-text">{{ plan.collectCount }}</text>
            </view>
            <view class="meta-item">
              <text class="meta-icon">❤️</text>
              <text class="meta-text">{{ plan.likeCount }}</text>
            </view>
          </view>
          <view class="plan-tags">
            <text 
              v-for="tag in plan.tags" 
              :key="tag"
              class="tag-item"
            >{{ tag }}</text>
          </view>
          <view class="plan-footer">
            <text class="plan-date">{{ formatDate(plan.createTime) }}</text>
            <view class="plan-actions">
              <view class="action-btn" @click.stop="toggleCollect(plan)">
                <text class="action-icon">{{ plan.isCollected ? '⭐' : '☆' }}</text>
              </view>
              <view class="action-btn" @click.stop="toggleLike(plan)">
                <text class="action-icon">{{ plan.isLiked ? '❤️' : '🤍' }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="filteredPlans.length === 0" class="empty-state">
      <text class="empty-icon">📝</text>
      <text class="empty-text">暂无攻略</text>
      <text class="empty-desc">快来创建第一条攻略吧！</text>
    </view>

    <!-- 筛选弹窗 -->
    <view v-if="showFilter" class="filter-modal" @click="showFilter = false">
      <view class="filter-content" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选</text>
          <text class="filter-close" @click="showFilter = false">✕</text>
        </view>
        <view class="filter-body">
          <view class="filter-section">
            <text class="section-title">攻略类型</text>
            <view class="filter-options">
              <view 
                v-for="type in planTypes" 
                :key="type.id"
                class="filter-option"
                :class="{ active: selectedType === type.id }"
                @click="selectedType = type.id"
              >
                <text class="option-text">{{ type.name }}</text>
              </view>
            </view>
          </view>
          <view class="filter-section">
            <text class="section-title">出行方式</text>
            <view class="filter-options">
              <view 
                v-for="travel in travelTypes" 
                :key="travel.id"
                class="filter-option"
                :class="{ active: selectedTravel === travel.id }"
                @click="selectedTravel = travel.id"
              >
                <text class="option-text">{{ travel.name }}</text>
              </view>
            </view>
          </view>
          <view class="filter-section">
            <text class="section-title">预算范围</text>
            <view class="filter-options">
              <view 
                v-for="budget in budgetRanges" 
                :key="budget.id"
                class="filter-option"
                :class="{ active: selectedBudget === budget.id }"
                @click="selectedBudget = budget.id"
              >
                <text class="option-text">{{ budget.name }}</text>
              </view>
            </view>
          </view>
        </view>
        <view class="filter-footer">
          <view class="filter-btn reset" @click="resetFilter">重置</view>
          <view class="filter-btn confirm" @click="applyFilter">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

// 当前选中的标签
const currentTab = ref(0)

// 筛选相关
const showFilter = ref(false)
const selectedType = ref(0)
const selectedTravel = ref(0)
const selectedBudget = ref(0)

// 分类标签
const categoryTabs = ref([
  { id: 0, name: '全部' },
  { id: 1, name: '热门' },
  { id: 2, name: '最新' },
  { id: 3, name: '情侣' },
  { id: 4, name: '家庭' },
  { id: 5, name: '团队' },
  { id: 6, name: '穷游' },
  { id: 7, name: '富游' }
])

// 攻略类型
const planTypes = ref([
  { id: 0, name: '全部' },
  { id: 1, name: '景点攻略' },
  { id: 2, name: '美食攻略' },
  { id: 3, name: '住宿攻略' },
  { id: 4, name: '交通攻略' },
  { id: 5, name: '购物攻略' }
])

// 出行方式
const travelTypes = ref([
  { id: 0, name: '全部' },
  { id: 1, name: '自驾游' },
  { id: 2, name: '自由行' },
  { id: 3, name: '跟团游' },
  { id: 4, name: '背包客' }
])

// 预算范围
const budgetRanges = ref([
  { id: 0, name: '全部' },
  { id: 1, name: '500以下' },
  { id: 2, name: '500-1000' },
  { id: 3, name: '1000-3000' },
  { id: 4, name: '3000以上' }
])

// 攻略列表
const plans = ref([
  {
    id: 1,
    title: '重庆三日游超详细攻略',
    description: '探索山城魅力，品味巴渝文化，从洪崖洞到解放碑，完整路线规划',
    coverImage: 'https://via.placeholder.com/750x400?text=Chongqing+Guide',
    author: '旅行达人张',
    viewCount: 12580,
    collectCount: 856,
    likeCount: 1204,
    status: 1,
    tags: ['重庆', '三日游', '穷游', '情侣'],
    createTime: '2024-01-15',
    isCollected: false,
    isLiked: false
  },
  {
    id: 2,
    title: '成都美食之旅完整攻略',
    description: '舌尖上的成都，川菜魅力尽显，火锅串串一个不落',
    coverImage: 'https://via.placeholder.com/750x400?text=Chengdu+Food',
    author: '美食博主李',
    viewCount: 9680,
    collectCount: 642,
    likeCount: 892,
    status: 1,
    tags: ['成都', '美食', '富游', '个人'],
    createTime: '2024-01-14',
    isCollected: true,
    isLiked: true
  },
  {
    id: 3,
    title: '西安古都文化深度游',
    description: '千年古都，历史文化魅力，兵马俑大雁塔古城墙全攻略',
    coverImage: 'https://via.placeholder.com/750x400?text=XiAn+Culture',
    author: '文化探索者王',
    viewCount: 8560,
    collectCount: 534,
    likeCount: 768,
    status: 1,
    tags: ['西安', '文化', '家庭', '中等消费'],
    createTime: '2024-01-13',
    isCollected: false,
    isLiked: false
  }
])

// 筛选后的攻略列表
const filteredPlans = computed(() => {
  let result = plans.value

  // 根据标签筛选
  if (currentTab.value === 1) {
    // 热门
    result = result.sort((a, b) => b.viewCount - a.viewCount)
  } else if (currentTab.value === 2) {
    // 最新
    result = result.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
  } else if (currentTab.value === 3) {
    // 情侣
    result = result.filter(p => p.tags.includes('情侣'))
  } else if (currentTab.value === 4) {
    // 家庭
    result = result.filter(p => p.tags.includes('家庭'))
  } else if (currentTab.value === 5) {
    // 团队
    result = result.filter(p => p.tags.includes('团队'))
  } else if (currentTab.value === 6) {
    // 穷游
    result = result.filter(p => p.tags.includes('穷游'))
  } else if (currentTab.value === 7) {
    // 富游
    result = result.filter(p => p.tags.includes('富游'))
  }

  return result
})

// 切换标签
const switchTab = (id: number) => {
  currentTab.value = id
}

// 重置筛选
const resetFilter = () => {
  selectedType.value = 0
  selectedTravel.value = 0
  selectedBudget.value = 0
}

// 应用筛选
const applyFilter = () => {
  showFilter.value = false
  // TODO: 应用筛选逻辑
}

// 切换收藏
const toggleCollect = (plan: any) => {
  plan.isCollected = !plan.isCollected
  if (plan.isCollected) {
    plan.collectCount++
    uni.showToast({ title: '已收藏', icon: 'success' })
  } else {
    plan.collectCount--
    uni.showToast({ title: '已取消收藏', icon: 'none' })
  }
}

// 切换点赞
const toggleLike = (plan: any) => {
  plan.isLiked = !plan.isLiked
  if (plan.isLiked) {
    plan.likeCount++
    uni.showToast({ title: '已点赞', icon: 'success' })
  } else {
    plan.likeCount--
    uni.showToast({ title: '已取消点赞', icon: 'none' })
  }
}

// 获取状态文字
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待审核',
    1: '已发布',
    2: '已下架'
  }
  return statusMap[status] || '未知'
}

// 获取状态样式类
const getStatusClass = (status: number) => {
  const classMap: Record<number, string> = {
    0: 'pending',
    1: 'published',
    2: 'offline'
  }
  return classMap[status] || ''
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return dateStr.split('T')[0]
}

// 跳转到搜索
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/plan/search'
  })
}

// 跳转到创建攻略
const goToCreatePlan = () => {
  uni.navigateTo({
    url: '/pages/plan/create'
  })
}

// 跳转到AI生成
const goToAIGenerate = () => {
  uni.navigateTo({
    url: '/pages/ai/generate'
  })
}

// 跳转到我的攻略
const goToMyPlans = () => {
  uni.navigateTo({
    url: '/pages/user/profile'
  })
}

// 跳转到我的收藏
const goToMyCollection = () => {
  uni.navigateTo({
    url: '/pages/user/collect'
  })
}

// 跳转到攻略详情
const goToPlanDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/plan/detail?id=${id}`
  })
}

// 加载攻略列表
const loadPlans = async () => {
  try {
    // TODO: 调用API获取攻略列表
    // const result = await request.get('/plan/list')
    console.log('加载攻略列表')
  } catch (error) {
    console.error('加载攻略列表失败:', error)
  }
}

onMounted(() => {
  loadPlans()
})
</script>

<style lang="scss" scoped>
.plan-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.search-bar {
  background: #fff;
  padding: 20rpx;
  display: flex;
  gap: 20rpx;
  align-items: center;
  
  .search-box {
    flex: 1;
    background: #f5f5f5;
    border-radius: 50rpx;
    padding: 20rpx 30rpx;
    display: flex;
    align-items: center;
    gap: 16rpx;
    
    .search-icon {
      font-size: 32rpx;
      color: #999;
    }
    
    .search-placeholder {
      font-size: 28rpx;
      color: #999;
    }
  }
  
  .filter-btn {
    padding: 20rpx 24rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50rpx;
    
    .filter-icon {
      font-size: 26rpx;
      color: #fff;
    }
  }
}

.quick-actions {
  background: #fff;
  padding: 20rpx;
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
  
  .action-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx;
    background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
    border-radius: 16rpx;
    
    .action-icon {
      font-size: 40rpx;
      margin-bottom: 8rpx;
    }
    
    .action-text {
      font-size: 24rpx;
      color: #333;
    }
  }
}

.category-tabs {
  background: #fff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
  
  .tabs-scroll {
    white-space: nowrap;
    
    .tabs-list {
      display: flex;
      gap: 20rpx;
      padding: 0 20rpx;
      
      .tab-item {
        padding: 12rpx 32rpx;
        background: #f5f5f5;
        border-radius: 50rpx;
        white-space: nowrap;
        
        .tab-text {
          font-size: 26rpx;
          color: #666;
        }
        
        &.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          
          .tab-text {
            color: #fff;
            font-weight: bold;
          }
        }
      }
    }
  }
}

.plan-list {
  padding: 0 20rpx;
}

.plan-card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  
  .plan-cover {
    width: 100%;
    height: 400rpx;
  }
  
  .plan-content {
    padding: 30rpx;
    
    .plan-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16rpx;
      
      .plan-title {
        flex: 1;
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-right: 16rpx;
      }
      
      .plan-status {
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        font-size: 22rpx;
        
        &.pending {
          background: #fff3cd;
          color: #856404;
        }
        
        &.published {
          background: #d4edda;
          color: #155724;
        }
        
        &.offline {
          background: #f8d7da;
          color: #721c24;
        }
        
        .status-text {
          font-size: 22rpx;
        }
      }
    }
    
    .plan-desc {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
      margin-bottom: 20rpx;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
    
    .plan-meta {
      display: flex;
      gap: 24rpx;
      margin-bottom: 20rpx;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 8rpx;
        
        .meta-icon {
          font-size: 24rpx;
        }
        
        .meta-text {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
    
    .plan-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-bottom: 20rpx;
      
      .tag-item {
        padding: 8rpx 20rpx;
        background: rgba(102, 126, 234, 0.1);
        color: #667eea;
        border-radius: 20rpx;
        font-size: 22rpx;
      }
    }
    
    .plan-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .plan-date {
        font-size: 24rpx;
        color: #999;
      }
      
      .plan-actions {
        display: flex;
        gap: 20rpx;
        
        .action-btn {
          padding: 12rpx;
          
          .action-icon {
            font-size: 32rpx;
          }
        }
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 100rpx 40rpx;
  
  .empty-icon {
    font-size: 120rpx;
    display: block;
    margin-bottom: 24rpx;
  }
  
  .empty-text {
    font-size: 32rpx;
    color: #333;
    display: block;
    margin-bottom: 12rpx;
  }
  
  .empty-desc {
    font-size: 26rpx;
    color: #999;
  }
}

.filter-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  
  .filter-content {
    background: #fff;
    border-radius: 32rpx 32rpx 0 0;
    width: 100%;
    max-height: 80vh;
    overflow-y: auto;
    
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx;
      border-bottom: 1px solid #f0f0f0;
      
      .filter-title {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
      }
      
      .filter-close {
        font-size: 40rpx;
        color: #999;
      }
    }
    
    .filter-body {
      padding: 30rpx;
      
      .filter-section {
        margin-bottom: 40rpx;
        
        .section-title {
          font-size: 30rpx;
          font-weight: bold;
          color: #333;
          display: block;
          margin-bottom: 20rpx;
        }
        
        .filter-options {
          display: flex;
          flex-wrap: wrap;
          gap: 16rpx;
          
          .filter-option {
            padding: 16rpx 32rpx;
            background: #f5f5f5;
            border-radius: 50rpx;
            
            .option-text {
              font-size: 26rpx;
              color: #666;
            }
            
            &.active {
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              
              .option-text {
                color: #fff;
              }
            }
          }
        }
      }
    }
    
    .filter-footer {
      display: flex;
      gap: 20rpx;
      padding: 30rpx;
      border-top: 1px solid #f0f0f0;
      
      .filter-btn {
        flex: 1;
        padding: 24rpx;
        text-align: center;
        border-radius: 50rpx;
        font-size: 30rpx;
        
        &.reset {
          background: #f5f5f5;
          color: #666;
        }
        
        &.confirm {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;
        }
      }
    }
  }
}
</style>
