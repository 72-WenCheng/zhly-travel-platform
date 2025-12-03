<template>
  <view class="culture-page">
    <!-- 顶部说明 -->
    <view class="header">
      <text class="title">文旅对接</text>
      <text class="subtitle">助力家乡发展，体验特色文化</text>
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

    <!-- 文旅项目列表 -->
    <view class="culture-list">
      <view 
        v-for="item in filteredItems" 
        :key="item.id"
        class="culture-card"
        @click="goToCultureDetail(item.id)"
      >
        <image :src="item.image" class="culture-image" mode="aspectFill" />
        <view class="culture-overlay">
          <view class="culture-type-badge" :style="{ background: getTypeColor(item.type) }">
            <text class="type-text">{{ getTypeName(item.type) }}</text>
          </view>
        </view>
        <view class="culture-content">
          <view class="culture-header">
            <text class="culture-name">{{ item.name }}</text>
            <view class="culture-rating">
              <text class="rating-star">⭐</text>
              <text class="rating-score">{{ item.rating }}</text>
            </view>
          </view>
          <text class="culture-region">📍 {{ item.region }}</text>
          <text class="culture-desc">{{ item.description }}</text>
          <view class="culture-info">
            <view class="info-item">
              <text class="info-icon">💰</text>
              <text class="info-text">¥{{ item.price }}/人</text>
            </view>
            <view class="info-item">
              <text class="info-icon">📞</text>
              <text class="info-text">{{ item.contactPhone }}</text>
            </view>
            <view class="info-item">
              <text class="info-icon">👤</text>
              <text class="info-text">{{ item.contactPerson }}</text>
            </view>
          </view>
          <view class="culture-footer">
            <text class="culture-address">{{ item.address }}</text>
            <view class="culture-tags">
              <text 
                v-for="tag in item.tags" 
                :key="tag"
                class="tag-item"
              >{{ tag }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="filteredItems.length === 0" class="empty-state">
      <text class="empty-icon">🏛️</text>
      <text class="empty-text">暂无文旅项目</text>
      <text class="empty-desc">敬请期待更多精彩内容</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

// 当前选中的分类
const currentTab = ref(0)

// 分类标签
const categoryTabs = ref([
  { id: 0, name: '全部', icon: '🏛️' },
  { id: 1, name: '农家乐', icon: '🏡' },
  { id: 2, name: '特色产品', icon: '🛍️' },
  { id: 3, name: '文化体验', icon: '🎭' },
  { id: 4, name: '团队建设', icon: '👥' }
])

// 文旅项目列表
const cultureItems = ref([
  {
    id: 1,
    name: '重庆巴南农家乐',
    region: '重庆·巴南区',
    description: '体验田园生活，品尝地道农家菜，感受乡村慢生活',
    image: 'https://via.placeholder.com/750x400?text=Farm+House',
    rating: 4.5,
    price: 120,
    contactPerson: '张经理',
    contactPhone: '13800138000',
    address: '重庆市巴南区南泉街道',
    type: 0,
    tags: ['农家乐', '美食', '亲子']
  },
  {
    id: 2,
    name: '重庆土特产专卖',
    region: '重庆·渝中区',
    description: '精选重庆本土特色产品，花椒、火锅底料、牛肉干等',
    image: 'https://via.placeholder.com/750x400?text=Local+Products',
    rating: 4.6,
    price: 0,
    contactPerson: '李经理',
    contactPhone: '13800138001',
    address: '重庆市渝中区解放碑步行街',
    type: 1,
    tags: ['特产', '购物', '伴手礼']
  },
  {
    id: 3,
    name: '重庆非物质文化遗产体验馆',
    region: '重庆·沙坪坝区',
    description: '深入了解巴渝文化，体验传统手工艺，制作糖画、剪纸等',
    image: 'https://via.placeholder.com/750x400?text=Culture+Experience',
    rating: 4.7,
    price: 80,
    contactPerson: '王老师',
    contactPhone: '13800138002',
    address: '重庆市沙坪坝区磁器口古镇',
    type: 2,
    tags: ['文化', '体验', '教育']
  },
  {
    id: 4,
    name: '重庆南山团建基地',
    region: '重庆·南岸区',
    description: '专业团建服务，拓展训练、团队游戏、烧烤露营',
    image: 'https://via.placeholder.com/750x400?text=Team+Building',
    rating: 4.4,
    price: 200,
    contactPerson: '赵经理',
    contactPhone: '13800138003',
    address: '重庆市南岸区南山风景区',
    type: 3,
    tags: ['团建', '拓展', '户外']
  },
  {
    id: 5,
    name: '重庆永川农家乐度假村',
    region: '重庆·永川区',
    description: '大型度假村，住宿餐饮一体化，适合周末休闲度假',
    image: 'https://via.placeholder.com/750x400?text=Resort',
    rating: 4.5,
    price: 150,
    contactPerson: '陈经理',
    contactPhone: '13800138004',
    address: '重庆市永川区茶山竹海',
    type: 0,
    tags: ['度假', '住宿', '休闲']
  },
  {
    id: 6,
    name: '重庆手工食品作坊',
    region: '重庆·江北区',
    description: '传承工艺，手工制作豆腐脑、米花糖等传统小吃',
    image: 'https://via.placeholder.com/750x400?text=Handmade+Food',
    rating: 4.6,
    price: 0,
    contactPerson: '刘师傅',
    contactPhone: '13800138005',
    address: '重庆市江北区观音桥步行街',
    type: 1,
    tags: ['手工', '传统', '美食']
  }
])

// 筛选后的项目列表
const filteredItems = computed(() => {
  if (currentTab.value === 0) {
    return cultureItems.value
  }
  return cultureItems.value.filter(item => item.type === currentTab.value - 1)
})

// 切换分类
const switchTab = (id: number) => {
  currentTab.value = id
}

// 获取类型名称
const getTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    0: '农家乐',
    1: '特色产品',
    2: '文化体验',
    3: '团队建设'
  }
  return typeMap[type] || '未知'
}

// 获取类型颜色
const getTypeColor = (type: number) => {
  const colorMap: Record<number, string> = {
    0: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    1: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    2: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    3: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
  return colorMap[type] || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
}

// 跳转到文旅详情
const goToCultureDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/culture/detail?id=${id}`
  })
}

// 加载文旅项目列表
const loadCultureItems = async () => {
  try {
    // TODO: 调用API获取文旅项目列表
    // const result = await request.get('/culture/list')
    console.log('加载文旅项目列表')
  } catch (error) {
    console.error('加载文旅项目列表失败:', error)
  }
}

onMounted(() => {
  loadCultureItems()
})
</script>

<style lang="scss" scoped>
.culture-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
  color: #fff;
  
  .title {
    font-size: 44rpx;
    font-weight: bold;
    display: block;
    margin-bottom: 12rpx;
  }
  
  .subtitle {
    font-size: 26rpx;
    opacity: 0.95;
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

.culture-list {
  padding: 0 20rpx;
}

.culture-card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  
  .culture-image {
    width: 100%;
    height: 400rpx;
  }
  
  .culture-overlay {
    position: relative;
    margin-top: -80rpx;
    height: 80rpx;
    display: flex;
    align-items: flex-end;
    padding: 0 20rpx 20rpx;
    
    .culture-type-badge {
      padding: 8rpx 20rpx;
      border-radius: 20rpx;
      
      .type-text {
        font-size: 22rpx;
        color: #fff;
        font-weight: bold;
      }
    }
  }
  
  .culture-content {
    padding: 30rpx;
    
    .culture-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12rpx;
      
      .culture-name {
        flex: 1;
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-right: 16rpx;
      }
      
      .culture-rating {
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
    
    .culture-region {
      font-size: 24rpx;
      color: #666;
      display: block;
      margin-bottom: 12rpx;
    }
    
    .culture-desc {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
      margin-bottom: 20rpx;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
    
    .culture-info {
      display: flex;
      flex-wrap: wrap;
      gap: 20rpx;
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
    
    .culture-footer {
      .culture-address {
        font-size: 24rpx;
        color: #999;
        display: block;
        margin-bottom: 12rpx;
      }
      
      .culture-tags {
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
