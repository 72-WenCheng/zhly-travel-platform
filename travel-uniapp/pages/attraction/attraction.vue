<template>
  <view class="attraction-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-box" @click="goToSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索景点...</text>
      </view>
    </view>

    <!-- 个性化推荐标签提示 -->
    <view class="recommend-banner" v-if="userTags.length > 0">
      <text class="banner-icon">🎯</text>
      <view class="banner-content">
        <text class="banner-title">为您推荐</text>
        <text class="banner-desc">基于您的标签：{{ userTags.join('、') }}</text>
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
            <text class="tab-icon">{{ tab.icon }}</text>
            <text class="tab-text">{{ tab.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 景点列表 -->
    <view class="attraction-list">
      <view 
        v-for="attraction in filteredAttractions" 
        :key="attraction.id"
        class="attraction-card"
        @click="goToAttractionDetail(attraction.id)"
      >
        <image :src="attraction.image" class="attraction-image" mode="aspectFill" />
        <view class="attraction-overlay">
          <view class="attraction-status" :class="getStatusClass(attraction.status)">
            <text class="status-text">{{ getStatusText(attraction.status) }}</text>
          </view>
        </view>
        <view class="attraction-content">
          <view class="attraction-header">
            <text class="attraction-name">{{ attraction.name }}</text>
            <view class="attraction-rating">
              <text class="rating-star">⭐</text>
              <text class="rating-score">{{ attraction.rating }}</text>
            </view>
          </view>
          <text class="attraction-location">📍 {{ attraction.city }} · {{ attraction.address }}</text>
          <text class="attraction-desc">{{ attraction.description }}</text>
          <view class="attraction-info">
            <view class="info-item">
              <text class="info-icon">🎫</text>
              <text class="info-text">{{ attraction.ticketPrice > 0 ? `¥${attraction.ticketPrice}` : '免费' }}</text>
            </view>
            <view class="info-item">
              <text class="info-icon">🕐</text>
              <text class="info-text">{{ attraction.openingHours || '全天开放' }}</text>
            </view>
            <view class="info-item">
              <text class="info-icon">👁️</text>
              <text class="info-text">{{ attraction.viewCount }}次浏览</text>
            </view>
          </view>
          <view class="attraction-tags">
            <text 
              v-for="tag in attraction.tags" 
              :key="tag"
              class="tag-item"
            >{{ tag }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="filteredAttractions.length === 0" class="empty-state">
      <text class="empty-icon">🏞️</text>
      <text class="empty-text">暂无景点</text>
      <text class="empty-desc">请尝试调整筛选条件</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

// 用户标签
const userTags = ref<string[]>([])

// 当前选中的分类
const currentTab = ref(0)

// 分类标签
const categoryTabs = ref([
  { id: 0, name: '推荐', icon: '🎯' },
  { id: 1, name: '热门', icon: '🔥' },
  { id: 2, name: '自然景观', icon: '🏔️' },
  { id: 3, name: '人文景观', icon: '🏛️' },
  { id: 4, name: '主题公园', icon: '🎢' },
  { id: 5, name: '博物馆', icon: '🏛️' },
  { id: 6, name: '历史遗迹', icon: '🗿' },
  { id: 7, name: '美食街', icon: '🍜' }
])

// 景点列表
const attractions = ref([
  {
    id: 1,
    name: '洪崖洞',
    city: '重庆',
    address: '渝中区嘉陵江滨江路',
    description: '重庆网红打卡地，夜幕降临，灯火通明，宛如童话世界',
    image: 'https://via.placeholder.com/750x400?text=Hongya+Cave',
    rating: 4.5,
    ticketPrice: 0,
    openingHours: '09:00-22:00',
    viewCount: 12580,
    status: 1,
    type: 2,
    tags: ['网红打卡', '夜景', '免费']
  },
  {
    id: 2,
    name: '解放碑步行街',
    city: '重庆',
    address: '渝中区解放碑',
    description: '重庆最繁华的商业中心，购物美食一站式体验',
    image: 'https://via.placeholder.com/750x400?text=Jiefangbei',
    rating: 4.3,
    ticketPrice: 0,
    openingHours: '全天开放',
    viewCount: 9680,
    status: 1,
    type: 3,
    tags: ['购物', '美食', '免费']
  },
  {
    id: 3,
    name: '磁器口古镇',
    city: '重庆',
    address: '沙坪坝区磁器口古镇',
    description: '千年古镇，体验巴渝文化，品味传统小吃',
    image: 'https://via.placeholder.com/750x400?text=Ciqikou',
    rating: 4.2,
    ticketPrice: 0,
    openingHours: '08:00-22:00',
    viewCount: 8560,
    status: 1,
    type: 4,
    tags: ['古镇', '文化', '小吃']
  },
  {
    id: 4,
    name: '长江索道',
    city: '重庆',
    address: '渝中区新华路151号',
    description: '重庆独特的交通工具，俯瞰两江交汇美景',
    image: 'https://via.placeholder.com/750x400?text=Cableway',
    rating: 4.4,
    ticketPrice: 20,
    openingHours: '07:30-22:30',
    viewCount: 7560,
    status: 1,
    type: 2,
    tags: ['交通工具', '夜景', '特色']
  },
  {
    id: 5,
    name: '重庆科技馆',
    city: '重庆',
    address: '江北区江北城西大街',
    description: '现代化科技展示，寓教于乐的科学体验',
    image: 'https://via.placeholder.com/750x400?text=Science+Museum',
    rating: 4.1,
    ticketPrice: 40,
    openingHours: '09:00-17:00',
    viewCount: 5430,
    status: 1,
    type: 5,
    tags: ['博物馆', '教育', '亲子']
  },
  {
    id: 6,
    name: '南山一棵树观景台',
    city: '重庆',
    address: '南岸区南山路',
    description: '重庆最佳夜景观赏地，俯瞰全城夜景',
    image: 'https://via.placeholder.com/750x400?text=NanShan',
    rating: 4.6,
    ticketPrice: 30,
    openingHours: '09:00-22:00',
    viewCount: 6420,
    status: 1,
    type: 2,
    tags: ['夜景', '摄影', '观光']
  }
])

// 筛选后的景点列表
const filteredAttractions = computed(() => {
  let result = attractions.value

  // 根据分类筛选
  if (currentTab.value === 0) {
    // 推荐 - 基于用户标签推荐
    // TODO: 根据用户标签智能推荐
  } else if (currentTab.value === 1) {
    // 热门 - 按浏览量排序
    result = result.sort((a, b) => b.viewCount - a.viewCount)
  } else if (currentTab.value === 2) {
    // 自然景观
    result = result.filter(a => a.type === 1)
  } else if (currentTab.value === 3) {
    // 人文景观
    result = result.filter(a => a.type === 2)
  } else if (currentTab.value === 4) {
    // 主题公园
    result = result.filter(a => a.type === 3)
  } else if (currentTab.value === 5) {
    // 博物馆
    result = result.filter(a => a.type === 4)
  } else if (currentTab.value === 6) {
    // 历史遗迹
    result = result.filter(a => a.type === 5)
  } else if (currentTab.value === 7) {
    // 美食街
    result = result.filter(a => a.type === 6)
  }

  return result
})

// 切换分类
const switchTab = (id: number) => {
  currentTab.value = id
}

// 获取状态文字
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '已关闭',
    1: '正常开放',
    2: '维护中'
  }
  return statusMap[status] || '未知'
}

// 获取状态样式类
const getStatusClass = (status: number) => {
  const classMap: Record<number, string> = {
    0: 'closed',
    1: 'open',
    2: 'maintenance'
  }
  return classMap[status] || ''
}

// 跳转到搜索
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/attraction/search'
  })
}

// 跳转到景点详情
const goToAttractionDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/attraction/detail?id=${id}`
  })
}

// 获取用户标签
const getUserTags = async () => {
  try {
    const token = uni.getStorageSync('token')
    if (!token) return
    
    // TODO: 调用API获取用户标签
    // const result = await request.get('/user/tags')
    // 临时模拟数据
    userTags.value = ['情侣出行', '摄影', '夜景']
  } catch (error) {
    console.error('获取用户标签失败:', error)
  }
}

// 加载景点列表
const loadAttractions = async () => {
  try {
    // TODO: 调用API获取景点列表
    // const result = await request.get('/attraction/list')
    console.log('加载景点列表')
  } catch (error) {
    console.error('加载景点列表失败:', error)
  }
}

onMounted(() => {
  getUserTags()
  loadAttractions()
})
</script>

<style lang="scss" scoped>
.attraction-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.search-bar {
  background: #fff;
  padding: 20rpx;
  
  .search-box {
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
}

.recommend-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 20rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  color: #fff;
  
  .banner-icon {
    font-size: 48rpx;
  }
  
  .banner-content {
    flex: 1;
    
    .banner-title {
      font-size: 28rpx;
      font-weight: bold;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .banner-desc {
      font-size: 24rpx;
      opacity: 0.9;
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
        padding: 16rpx 32rpx;
        background: #f5f5f5;
        border-radius: 50rpx;
        white-space: nowrap;
        display: flex;
        align-items: center;
        gap: 8rpx;
        
        .tab-icon {
          font-size: 28rpx;
        }
        
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

.attraction-list {
  padding: 0 20rpx;
}

.attraction-card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  
  .attraction-image {
    width: 100%;
    height: 400rpx;
  }
  
  .attraction-overlay {
    position: relative;
    margin-top: -80rpx;
    height: 80rpx;
    display: flex;
    align-items: flex-end;
    padding: 0 20rpx 20rpx;
    
    .attraction-status {
      padding: 8rpx 20rpx;
      border-radius: 20rpx;
      font-size: 22rpx;
      
      &.open {
        background: rgba(76, 175, 80, 0.9);
        color: #fff;
      }
      
      &.closed {
        background: rgba(244, 67, 54, 0.9);
        color: #fff;
      }
      
      &.maintenance {
        background: rgba(255, 152, 0, 0.9);
        color: #fff;
      }
      
      .status-text {
        font-size: 22rpx;
      }
    }
  }
  
  .attraction-content {
    padding: 30rpx;
    
    .attraction-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12rpx;
      
      .attraction-name {
        flex: 1;
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-right: 16rpx;
      }
      
      .attraction-rating {
        display: flex;
        align-items: center;
        gap: 4rpx;
        
        .rating-star {
          font-size: 24rpx;
        }
        
        .rating-score {
          font-size: 26rpx;
          color: #ff9900;
          font-weight: bold;
        }
      }
    }
    
    .attraction-location {
      font-size: 24rpx;
      color: #666;
      display: block;
      margin-bottom: 12rpx;
    }
    
    .attraction-desc {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
      margin-bottom: 20rpx;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
    
    .attraction-info {
      display: flex;
      gap: 24rpx;
      margin-bottom: 20rpx;
      
      .info-item {
        display: flex;
        align-items: center;
        gap: 8rpx;
        
        .info-icon {
          font-size: 24rpx;
        }
        
        .info-text {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
    
    .attraction-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      
      .tag-item {
        padding: 8rpx 20rpx;
        background: rgba(102, 126, 234, 0.1);
        color: #667eea;
        border-radius: 20rpx;
        font-size: 22rpx;
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
</style>
