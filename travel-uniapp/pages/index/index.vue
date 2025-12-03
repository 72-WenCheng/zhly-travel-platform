<template>
  <view class="index-page">
    <!-- 顶部天气信息 - 人性化操作 -->
    <view class="weather-bar">
      <view class="weather-info">
        <text class="weather-icon">{{ weatherIcon }}</text>
        <text class="weather-temp">{{ weather.temperature }}°C</text>
        <text class="weather-desc">{{ weather.weather }}</text>
        <text class="weather-location">{{ weather.location }}</text>
      </view>
      <view class="weather-advice" :class="{ 'warning': weatherAdvice.type === 'warning' }">
        <text class="advice-text">{{ weatherAdvice.text }}</text>
      </view>
    </view>

    <!-- 轮播图 - 展示五大核心功能 -->
    <swiper 
      class="banner-swiper" 
      :indicator-dots="true" 
      :autoplay="true" 
      :interval="4000" 
      :duration="500"
      :circular="true"
    >
      <swiper-item v-for="(banner, index) in banners" :key="index">
        <image 
          :src="banner.image" 
          class="banner-image" 
          mode="aspectFill"
          @click="onBannerClick(banner)"
        />
        <view class="banner-content">
          <text class="banner-title">{{ banner.title }}</text>
          <text class="banner-desc">{{ banner.description }}</text>
        </view>
      </swiper-item>
    </swiper>

    <!-- 五大核心功能 -->
    <view class="core-functions">
      <view class="function-title">
        <text class="title-text">核心功能</text>
        <text class="title-desc">智能旅游，一键规划</text>
      </view>
      <view class="function-grid">
        <view 
          v-for="(func, index) in coreFunctions" 
          :key="index"
          class="function-item"
          @click="onFunctionClick(func)"
        >
          <view class="function-icon" :style="{ background: func.color }">
            <text class="icon-text">{{ func.icon }}</text>
          </view>
          <text class="function-name">{{ func.name }}</text>
          <text class="function-desc">{{ func.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 个性化标签展示 -->
    <view class="personal-tags-section" v-if="userTags.length > 0">
      <view class="section-header">
        <text class="section-title">为您推荐</text>
        <text class="section-desc">基于您的个性化标签</text>
      </view>
      <view class="tags-display">
        <view class="tag-item" v-for="(tag, index) in userTags" :key="index">
          <text class="tag-text">{{ tag }}</text>
        </view>
      </view>
    </view>

    <!-- 热门攻略推荐 -->
    <view class="recommend-section">
      <view class="section-header">
        <text class="section-title">热门攻略</text>
        <text class="section-more" @click="goToPlanPage">更多 ></text>
      </view>
      <scroll-view class="plan-scroll" scroll-x="true" show-scrollbar="false">
        <view class="plan-list">
          <view 
            v-for="plan in recommendPlans" 
            :key="plan.id"
            class="plan-item"
            @click="goToPlanDetail(plan.id)"
          >
            <image :src="plan.coverImage" class="plan-image" mode="aspectFill" />
            <view class="plan-content">
              <text class="plan-title">{{ plan.title }}</text>
              <text class="plan-desc">{{ plan.description }}</text>
              <view class="plan-meta">
                <text class="plan-author">{{ plan.author }}</text>
                <text class="plan-views">{{ plan.viewCount }}次浏览</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 景点推荐 - 基于个性化标签 -->
    <view class="attraction-section">
      <view class="section-header">
        <text class="section-title">推荐景点</text>
        <text class="section-more" @click="goToAttractionPage">更多 ></text>
      </view>
      <view class="attraction-grid">
        <view 
          v-for="attraction in recommendAttractions" 
          :key="attraction.id"
          class="attraction-item"
          @click="goToAttractionDetail(attraction.id)"
        >
          <image :src="attraction.image" class="attraction-image" mode="aspectFill" />
          <view class="attraction-overlay">
            <text class="attraction-name">{{ attraction.name }}</text>
            <text class="attraction-location">{{ attraction.location }}</text>
            <view class="attraction-rating">
              <text class="rating-text">⭐ {{ attraction.rating }}</text>
              <text class="rating-count">{{ attraction.reviewCount }}条评价</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- AI智能推荐 -->
    <view class="ai-section">
      <view class="section-header">
        <text class="section-title">AI智能助手</text>
        <text class="section-more" @click="goToAIPage">体验 ></text>
      </view>
      <view class="ai-card" @click="goToAIPage">
        <view class="ai-content">
          <text class="ai-title">🤖 智能生成专属攻略</text>
          <text class="ai-desc">输入目的地、预算、时间，AI为您量身定制旅游方案</text>
          <view class="ai-features">
            <text class="feature-item">🎯 个性化推荐</text>
            <text class="feature-item">🗺️ 智能路线规划</text>
            <text class="feature-item">💰 预算智能分配</text>
            <text class="feature-item">📸 一键导出攻略</text>
          </view>
        </view>
        <view class="ai-button">
          <text class="button-text">立即体验</text>
        </view>
      </view>
    </view>

    <!-- 文旅对接 -->
    <view class="culture-section">
      <view class="section-header">
        <text class="section-title">文旅对接</text>
        <text class="section-more" @click="goToCulturePage">更多 ></text>
      </view>
      <view class="culture-grid">
        <view 
          v-for="item in cultureItems" 
          :key="item.id"
          class="culture-item"
          @click="goToCultureDetail(item.id)"
        >
          <image :src="item.image" class="culture-image" mode="aspectFill" />
          <view class="culture-content">
            <text class="culture-name">{{ item.name }}</text>
            <text class="culture-desc">{{ item.description }}</text>
            <text class="culture-type">{{ item.type }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'

// 天气信息
const weather = ref({
  temperature: 25,
  weather: '晴天',
  location: '重庆'
})

const weatherIcon = ref('☀️')
const weatherAdvice = ref({
  text: '适合出行，建议户外活动',
  type: 'normal'
})

// 用户标签
const userTags = ref<string[]>([])

// 轮播图数据
const banners = ref([
  {
    id: 1,
    title: '攻略社区中心',
    description: '分享旅游攻略，探索精彩世界',
    image: 'https://via.placeholder.com/750x400?text=Guide+Community',
    link: '/pages/plan/plan'
  },
  {
    id: 2,
    title: 'AI智能生成',
    description: '让AI为您规划完美旅程',
    image: 'https://via.placeholder.com/750x400?text=AI+Generation',
    link: '/pages/ai/ai'
  },
  {
    id: 3,
    title: '文旅对接发展',
    description: '助力家乡发展，体验特色文化',
    image: 'https://via.placeholder.com/750x400?text=Culture+Tourism',
    link: '/pages/culture/culture'
  },
  {
    id: 4,
    title: '个性化推荐',
    description: '基于您的标签智能推荐',
    image: 'https://via.placeholder.com/750x400?text=Personalized',
    link: '/pages/user/tags'
  }
])

// 五大核心功能
const coreFunctions = ref([
  {
    name: '攻略社区',
    icon: '📝',
    desc: '分享攻略',
    color: 'linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%)',
    path: '/pages/plan/plan'
  },
  {
    name: '个性标签',
    icon: '🏷️',
    desc: '个性推荐',
    color: 'linear-gradient(135deg, #4ECDC4 0%, #6BD9D1 100%)',
       path: '/pages/user/tags'
  },
  {
    name: '全资源旅游',
    icon: '🌍',
    desc: '景点大全',
    color: 'linear-gradient(135deg, #45B7D1 0%, #6BC5DB 100%)',
    path: '/pages/attraction/attraction'
  },
  {
    name: 'AI生成',
    icon: '🤖',
    desc: '智能规划',
    color: 'linear-gradient(135deg, #96CEB4 0%, #B4DCC9 100%)',
    path: '/pages/ai/ai'
  },
  {
    name: '文旅对接',
    icon: '🏛️',
    desc: '助力发展',
    color: 'linear-gradient(135deg, #FFEAA7 0%, #FFF3C7 100%)',
    path: '/pages/culture/culture'
  }
])

// 推荐攻略数据
const recommendPlans = ref([
  {
    id: 1,
    title: '重庆三日游攻略',
    description: '探索山城魅力，品味巴渝文化',
    coverImage: 'https://via.placeholder.com/300x200?text=Chongqing',
    author: '张三',
    viewCount: 12580
  },
  {
    id: 2,
    title: '成都美食之旅',
    description: '舌尖上的成都，品味川菜魅力',
    coverImage: 'https://via.placeholder.com/300x200?text=Chengdu',
    author: '李四',
    viewCount: 9680
  },
  {
    id: 3,
    title: '西安古都游',
    description: '千年古都，历史文化的魅力',
    coverImage: 'https://via.placeholder.com/300x200?text=XiAn',
    author: '王五',
    viewCount: 8560
  }
])

// 推荐景点数据
const recommendAttractions = ref([
  {
    id: 1,
    name: '洪崖洞',
    location: '重庆·渝中区',
    image: 'https://via.placeholder.com/340x200?text=Hongya',
    rating: 4.5,
    reviewCount: 12580
  },
  {
    id: 2,
    name: '解放碑',
    location: '重庆·渝中区',
    image: 'https://via.placeholder.com/340x200?text=Jiefangbei',
    rating: 4.3,
    reviewCount: 9680
  },
  {
    id: 3,
    name: '磁器口古镇',
    location: '重庆·沙坪坝区',
    image: 'https://via.placeholder.com/340x200?text=Ciqikou',
    rating: 4.2,
    reviewCount: 8560
  },
  {
    id: 4,
    name: '长江索道',
    location: '重庆·渝中区',
    image: 'https://via.placeholder.com/340x200?text=Cableway',
    rating: 4.4,
    reviewCount: 7560
  }
])

// 文旅项目数据
const cultureItems = ref([
  {
    id: 1,
    name: '重庆农家乐',
    description: '体验田园生活，享受乡村美食',
    type: '农家乐',
    image: 'https://via.placeholder.com/160x160?text=Farm'
  },
  {
    id: 2,
    name: '特色农产品',
    description: '新鲜有机农产品，健康生活首选',
    type: '特色产品',
    image: 'https://via.placeholder.com/160x160?text=Product'
  },
  {
    id: 3,
    name: '文化体验',
    description: '深入了解巴渝文化，感受历史魅力',
    type: '文化体验',
    image: 'https://via.placeholder.com/160x160?text=Culture'
  },
  {
    id: 4,
    name: '团队建设',
    description: '增进团队凝聚力，提升团队合作',
    type: '团队建设',
    image: 'https://via.placeholder.com/160x160?text=Team'
  }
])

// 轮播图点击事件
const onBannerClick = (banner: any) => {
  uni.navigateTo({
    url: banner.link
  })
}

// 核心功能点击事件
const onFunctionClick = (func: any) => {
  uni.navigateTo({
    url: func.path
  })
}

// 跳转到攻略页面
const goToPlanPage = () => {
  uni.switchTab({
    url: '/pages/plan/plan'
  })
}

// 跳转到攻略详情
const goToPlanDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/plan/detail?id=${id}`
  })
}

// 跳转到景点页面
const goToAttractionPage = () => {
  uni.navigateTo({
    url: '/pages/attraction/attraction'
  })
}

// 跳转到景点详情
const goToAttractionDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/attraction/detail?id=${id}`
  })
}

// 跳转到AI页面
const goToAIPage = () => {
  uni.switchTab({
    url: '/pages/ai/ai'
  })
}

// 跳转到文旅页面
const goToCulturePage = () => {
  uni.switchTab({
    url: '/pages/culture/culture'
  })
}

// 跳转到文旅详情
const goToCultureDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/culture/detail?id=${id}`
  })
}

// 获取天气信息
const getWeatherInfo = async () => {
  try {
    // TODO: 调用天气API
    // const result = await request.get('/weather/info')
    console.log('获取天气信息')
  } catch (error) {
    console.error('获取天气信息失败:', error)
  }
}

// 获取用户标签
const getUserTags = async () => {
  try {
    const token = uni.getStorageSync('token')
    if (!token) return
    
    // TODO: 调用API获取用户标签
    // const result = await request.get('/user/tags')
    // 临时模拟数据
    userTags.value = ['情侣出行', '富游', '摄影', '美食', '自然风光']
  } catch (error) {
    console.error('获取用户标签失败:', error)
  }
}

// 加载推荐数据
const loadRecommendData = async () => {
  try {
    // TODO: 调用API获取推荐数据
    console.log('加载推荐数据')
  } catch (error) {
    console.error('加载推荐数据失败:', error)
  }
}

onMounted(() => {
  getWeatherInfo()
  getUserTags()
  loadRecommendData()
})
</script>

<style lang="scss" scoped>
.index-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.weather-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30rpx 30rpx 20rpx;
  color: #fff;
  
  .weather-info {
    display: flex;
    align-items: center;
    gap: 20rpx;
    margin-bottom: 12rpx;
    
    .weather-icon {
      font-size: 40rpx;
    }
    
    .weather-temp {
      font-size: 44rpx;
      font-weight: bold;
    }
    
    .weather-desc {
      font-size: 28rpx;
    }
    
    .weather-location {
      font-size: 24rpx;
      opacity: 0.9;
    }
  }
  
  .weather-advice {
    .advice-text {
      font-size: 24rpx;
      opacity: 0.95;
    }
    
    &.warning {
      .advice-text {
        color: #FFE5E5;
      }
    }
  }
}

.banner-swiper {
  height: 400rpx;
  margin: 20rpx;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  
  .banner-image {
    width: 100%;
    height: 100%;
  }
  
  .banner-content {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0,0,0,0.8));
    padding: 50rpx 30rpx 30rpx;
    color: #fff;
    
    .banner-title {
      font-size: 36rpx;
      font-weight: bold;
      display: block;
      margin-bottom: 12rpx;
    }
    
    .banner-desc {
      font-size: 26rpx;
      opacity: 0.95;
    }
  }
}

.core-functions {
  background: #fff;
  margin: 20rpx;
  padding: 40rpx 30rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .function-title {
    text-align: center;
    margin-bottom: 40rpx;
    
    .title-text {
      font-size: 40rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 12rpx;
    }
    
    .title-desc {
      font-size: 26rpx;
      color: #999;
    }
  }
  
  .function-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 30rpx;
    
    .function-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .function-icon {
        width: 90rpx;
        height: 90rpx;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16rpx;
        box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
        
        .icon-text {
          font-size: 44rpx;
        }
      }
      
      .function-name {
        font-size: 24rpx;
        color: #333;
        text-align: center;
        font-weight: 500;
        margin-bottom: 4rpx;
      }
      
      .function-desc {
        font-size: 20rpx;
        color: #999;
        text-align: center;
      }
    }
  }
}

.personal-tags-section {
  margin: 20rpx;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  padding: 30rpx;
  border-radius: 20rpx;
  
  .section-header {
    margin-bottom: 20rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .section-desc {
      font-size: 24rpx;
      color: #666;
    }
  }
  
  .tags-display {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
    
    .tag-item {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      padding: 12rpx 24rpx;
      border-radius: 40rpx;
      font-size: 24rpx;
      box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
      
      .tag-text {
        color: #fff;
      }
    }
  }
}

.recommend-section,
.attraction-section,
.ai-section,
.culture-section {
  margin: 20rpx;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
    
    .section-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
    }
    
    .section-more {
      font-size: 26rpx;
      color: #999;
    }
  }
}

.plan-scroll {
  white-space: nowrap;
  
  .plan-list {
    display: flex;
    gap: 20rpx;
    
    .plan-item {
      width: 320rpx;
      background: #fff;
      border-radius: 20rpx;
      overflow: hidden;
      flex-shrink: 0;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
      
      .plan-image {
        width: 100%;
        height: 220rpx;
      }
      
      .plan-content {
        padding: 24rpx;
        
        .plan-title {
          font-size: 30rpx;
          font-weight: bold;
          color: #333;
          display: block;
          margin-bottom: 12rpx;
        }
        
        .plan-desc {
          font-size: 24rpx;
          color: #666;
          display: block;
          margin-bottom: 16rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .plan-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .plan-author {
            font-size: 22rpx;
            color: #999;
          }
          
          .plan-views {
            font-size: 22rpx;
            color: #999;
          }
        }
      }
    }
  }
}

.attraction-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  
  .attraction-item {
    background: #fff;
    border-radius: 20rpx;
    overflow: hidden;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
    position: relative;
    
    .attraction-image {
      width: 100%;
      height: 240rpx;
    }
    
    .attraction-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0,0,0,0.8));
      padding: 30rpx 20rpx 20rpx;
      color: #fff;
      
      .attraction-name {
        font-size: 30rpx;
        font-weight: bold;
        display: block;
        margin-bottom: 8rpx;
      }
      
      .attraction-location {
        font-size: 24rpx;
        opacity: 0.9;
        display: block;
        margin-bottom: 12rpx;
      }
      
      .attraction-rating {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .rating-text {
          font-size: 26rpx;
          color: #FFD700;
          font-weight: bold;
        }
        
        .rating-count {
          font-size: 22rpx;
          opacity: 0.9;
        }
      }
    }
  }
}

.ai-section {
  .ai-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20rpx;
    padding: 40rpx;
    color: #fff;
    box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
    
    .ai-content {
      .ai-title {
        font-size: 36rpx;
        font-weight: bold;
        display: block;
        margin-bottom: 20rpx;
      }
      
      .ai-desc {
        font-size: 26rpx;
        opacity: 0.95;
        display: block;
        margin-bottom: 24rpx;
        line-height: 1.6;
      }
      
      .ai-features {
        display: flex;
        flex-wrap: wrap;
        gap: 20rpx;
        
        .feature-item {
          font-size: 24rpx;
          opacity: 0.9;
        }
      }
    }
    
    .ai-button {
      background: rgba(255,255,255,0.25);
      padding: 20rpx 40rpx;
      border-radius: 50rpx;
      margin-top: 24rpx;
      text-align: center;
      backdrop-filter: blur(10rpx);
      
      .button-text {
        font-size: 28rpx;
        font-weight: bold;
        color: #fff;
      }
    }
  }
}

.culture-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  
  .culture-item {
    background: #fff;
    border-radius: 20rpx;
    overflow: hidden;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
    
    .culture-image {
      width: 100%;
      height: 200rpx;
    }
    
    .culture-content {
      padding: 24rpx;
      
      .culture-name {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        display: block;
        margin-bottom: 8rpx;
      }
      
      .culture-desc {
        font-size: 24rpx;
        color: #666;
        display: block;
        margin-bottom: 12rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .culture-type {
        font-size: 22rpx;
        color: #667eea;
        background: rgba(102, 126, 234, 0.1);
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        display: inline-block;
      }
    }
  }
}
</style>
