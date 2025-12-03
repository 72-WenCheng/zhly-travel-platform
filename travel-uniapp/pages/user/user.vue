<template>
  <view class="user-page">
    <!-- 用户信息卡片 -->
    <view class="user-header">
      <view class="user-info">
        <image :src="userInfo.avatar || '/static/default-avatar.png'" class="user-avatar" @click="goToProfile" />
        <view class="user-details">
          <text class="user-name">{{ userInfo.nickname || '未登录' }}</text>
          <text class="user-desc">{{ userInfo.email || '完善信息获取更精准推荐' }}</text>
        </view>
        <view class="user-settings" @click="goToSettings">
          <text class="settings-icon">⚙️</text>
        </view>
      </view>
      
      <!-- 用户标签展示 -->
      <view class="user-tags" v-if="userTags.length > 0">
        <text 
          v-for="(tag, index) in userTags" 
          :key="index"
          class="tag-item"
        >{{ tag }}</text>
      </view>
      
      <!-- 统计数据 -->
      <view class="user-stats">
        <view class="stat-item" @click="goToMyPlans">
          <text class="stat-number">{{ userStats.myPlans }}</text>
          <text class="stat-label">我的攻略</text>
        </view>
        <view class="stat-item" @click="goToMyCollection">
          <text class="stat-number">{{ userStats.myCollections }}</text>
          <text class="stat-label">我的收藏</text>
        </view>
        <view class="stat-item" @click="goToMyHistory">
          <text class="stat-number">{{ userStats.myHistory }}</text>
          <text class="stat-label">浏览历史</text>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-functions">
      <view class="function-item" @click="goToTags">
        <text class="function-icon">🏷️</text>
        <text class="function-text">个性标签</text>
      </view>
      <view class="function-item" @click="goToMyPlans">
        <text class="function-icon">📝</text>
        <text class="function-text">我的攻略</text>
      </view>
      <view class="function-item" @click="goToMyCollection">
        <text class="function-icon">⭐</text>
        <text class="function-text">我的收藏</text>
      </view>
      <view class="function-item" @click="goToMyHistory">
        <text class="function-icon">🕐</text>
        <text class="function-text">浏览历史</text>
      </view>
    </view>

    <!-- 我的攻略 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">我的攻略</text>
        <text class="section-more" @click="goToMyPlans">查看全部 ></text>
      </view>
      <view class="plan-list">
        <view 
          v-for="plan in myPlans" 
          :key="plan.id"
          class="plan-item"
          @click="goToPlanDetail(plan.id)"
        >
          <image :src="plan.coverImage" class="plan-image" mode="aspectFill" />
          <view class="plan-content">
            <text class="plan-title">{{ plan.title }}</text>
            <view class="plan-meta">
              <text class="plan-views">{{ plan.viewCount }}次浏览</text>
              <text class="plan-date">{{ formatDate(plan.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>
      <view v-if="myPlans.length === 0" class="empty-placeholder">
        <text class="empty-text">暂无攻略，快去创建吧！</text>
      </view>
    </view>

    <!-- 我的收藏 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">我的收藏</text>
        <text class="section-more" @click="goToMyCollection">查看全部 ></text>
      </view>
      <view class="collection-list">
        <view 
          v-for="item in myCollections" 
          :key="item.id"
          class="collection-item"
          @click="goToCollectionDetail(item)"
        >
          <image :src="item.image" class="collection-image" mode="aspectFill" />
          <view class="collection-content">
            <text class="collection-title">{{ item.title }}</text>
            <text class="collection-type">{{ item.type }}</text>
          </view>
        </view>
      </view>
      <view v-if="myCollections.length === 0" class="empty-placeholder">
        <text class="empty-text">暂无收藏</text>
      </view>
    </view>

    <!-- 其他功能 -->
    <view class="other-functions">
      <view class="function-row" @click="goToProfile">
        <text class="row-icon">👤</text>
        <text class="row-text">个人信息</text>
        <text class="row-arrow">></text>
      </view>
      <view class="function-row" @click="goToTags">
        <text class="row-icon">🏷️</text>
        <text class="row-text">个性标签</text>
        <text class="row-arrow">></text>
      </view>
      <view class="function-row" @click="goToSettings">
        <text class="row-icon">⚙️</text>
        <text class="row-text">设置</text>
        <text class="row-arrow">></text>
      </view>
    </view>

    <!-- 登录提示 -->
    <view v-if="!isLoggedIn" class="login-prompt">
      <text class="prompt-text">登录后享受更多功能</text>
      <view class="login-btn" @click="goToLogin">
        <text class="btn-text">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

// 登录状态
const isLoggedIn = ref(false)

// 用户信息
const userInfo = ref({
  nickname: '',
  email: '',
  avatar: ''
})

// 用户标签
const userTags = ref<string[]>([])

// 统计数据
const userStats = ref({
  myPlans: 0,
  myCollections: 0,
  myHistory: 0
})

// 我的攻略
const myPlans = ref([
  {
    id: 1,
    title: '重庆三日游攻略',
    coverImage: 'https://via.placeholder.com/200x150?text=My+Plan',
    viewCount: 125,
    createTime: '2024-01-15'
  }
])

// 我的收藏
const myCollections = ref([
  {
    id: 1,
    title: '洪崖洞',
    type: '景点',
    image: 'https://via.placeholder.com/120x120?text=Collection'
  },
  {
    id: 2,
    title: '重庆三日游攻略',
    type: '攻略',
    image: 'https://via.placeholder.com/120x120?text=Collection'
  }
])

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

// 跳转到个人信息
const goToProfile = () => {
  if (!isLoggedIn.value) {
    goToLogin()
    return
  }
  uni.navigateTo({
    url: '/pages/user/profile'
  })
}

// 跳转到设置
const goToSettings = () => {
  uni.navigateTo({
    url: '/pages/user/settings'
  })
}

// 跳转到个性标签
const goToTags = () => {
  uni.navigateTo({
    url: '/pages/user/tags'
  })
}

// 跳转到我的攻略
const goToMyPlans = () => {
  if (!isLoggedIn.value) {
    goToLogin()
    return
  }
  uni.navigateTo({
    url: '/pages/user/profile'
  })
}

// 跳转到我的收藏
const goToMyCollection = () => {
  if (!isLoggedIn.value) {
    goToLogin()
    return
  }
  uni.navigateTo({
    url: '/pages/user/collect'
  })
}

// 跳转到浏览历史
const goToMyHistory = () => {
  if (!isLoggedIn.value) {
    goToLogin()
    return
  }
  uni.navigateTo({
    url: '/pages/user/history'
  })
}

// 跳转到攻略详情
const goToPlanDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/plan/detail?id=${id}`
  })
}

// 跳转到收藏详情
const goToCollectionDetail = (item: any) => {
  if (item.type === '景点') {
    uni.navigateTo({
      url: `/pages/attraction/detail?id=${item.id}`
    })
  } else if (item.type === '攻略') {
    uni.navigateTo({
      url: `/pages/plan/detail?id=${item.id}`
    })
  }
}

// 跳转到登录
const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/user/login'
  })
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const token = uni.getStorageSync('token')
    if (!token) {
      isLoggedIn.value = false
      return
    }
    
    isLoggedIn.value = true
    
    // TODO: 调用API获取用户信息
    // const result = await request.get('/user/info')
    // if (result.code === 200 && result.data) {
    //   userInfo.value = result.data
    // }
    
    // 临时模拟数据
    userInfo.value = {
      nickname: '旅行达人',
      email: 'user@example.com',
      avatar: 'https://via.placeholder.com/100x100?text=Avatar'
    }
    
    userTags.value = ['情侣出行', '摄影', '夜景']
    
    userStats.value = {
      myPlans: 3,
      myCollections: 12,
      myHistory: 28
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    isLoggedIn.value = false
  }
}

// 加载我的攻略
const loadMyPlans = async () => {
  try {
    // TODO: 调用API获取我的攻略
    // const result = await request.get('/user/plans')
  } catch (error) {
    console.error('加载我的攻略失败:', error)
  }
}

// 加载我的收藏
const loadMyCollections = async () => {
  try {
    // TODO: 调用API获取我的收藏
    // const result = await request.get('/user/collections')
  } catch (error) {
    console.error('加载我的收藏失败:', error)
  }
}

onMounted(() => {
  loadUserInfo()
  loadMyPlans()
  loadMyCollections()
})
</script>

<style lang="scss" scoped>
.user-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
  color: #fff;
  
  .user-info {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;
    
    .user-avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      border: 4rpx solid rgba(255, 255, 255, 0.3);
      margin-right: 24rpx;
    }
    
    .user-details {
      flex: 1;
      
      .user-name {
        font-size: 36rpx;
        font-weight: bold;
        display: block;
        margin-bottom: 8rpx;
      }
      
      .user-desc {
        font-size: 24rpx;
        opacity: 0.9;
      }
    }
    
    .user-settings {
      padding: 12rpx;
      
      .settings-icon {
        font-size: 40rpx;
      }
    }
  }
  
  .user-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: 24rpx;
    
    .tag-item {
      padding: 8rpx 20rpx;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50rpx;
      font-size: 22rpx;
      backdrop-filter: blur(10rpx);
    }
  }
  
  .user-stats {
    display: flex;
    gap: 40rpx;
    
    .stat-item {
      text-align: center;
      
      .stat-number {
        font-size: 40rpx;
        font-weight: bold;
        display: block;
        margin-bottom: 8rpx;
      }
      
      .stat-label {
        font-size: 24rpx;
        opacity: 0.9;
      }
    }
  }
}

.quick-functions {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: 20rpx;
  display: flex;
  gap: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .function-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .function-icon {
      font-size: 48rpx;
      margin-bottom: 12rpx;
    }
    
    .function-text {
      font-size: 24rpx;
      color: #333;
    }
  }
}

.section {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .section-more {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.plan-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  
  .plan-item {
    display: flex;
    gap: 20rpx;
    
    .plan-image {
      width: 160rpx;
      height: 120rpx;
      border-radius: 12rpx;
    }
    
    .plan-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .plan-title {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        display: block;
        margin-bottom: 12rpx;
      }
      
      .plan-meta {
        display: flex;
        gap: 20rpx;
        
        .plan-views {
          font-size: 22rpx;
          color: #999;
        }
        
        .plan-date {
          font-size: 22rpx;
          color: #999;
        }
      }
    }
  }
}

.collection-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  
  .collection-item {
    display: flex;
    gap: 20rpx;
    
    .collection-image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 12rpx;
    }
    
    .collection-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .collection-title {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        display: block;
        margin-bottom: 8rpx;
      }
      
      .collection-type {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

.empty-placeholder {
  text-align: center;
  padding: 40rpx;
  
  .empty-text {
    font-size: 26rpx;
    color: #999;
  }
}

.other-functions {
  background: #fff;
  margin: 20rpx;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .function-row {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .row-icon {
      font-size: 36rpx;
      margin-right: 20rpx;
    }
    
    .row-text {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .row-arrow {
      font-size: 28rpx;
      color: #999;
    }
  }
}

.login-prompt {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 20rpx;
  padding: 40rpx;
  border-radius: 20rpx;
  text-align: center;
  color: #fff;
  
  .prompt-text {
    font-size: 28rpx;
    display: block;
    margin-bottom: 24rpx;
  }
  
  .login-btn {
    background: rgba(255, 255, 255, 0.25);
    padding: 20rpx 60rpx;
    border-radius: 50rpx;
    display: inline-block;
    backdrop-filter: blur(10rpx);
    
    .btn-text {
      font-size: 28rpx;
      font-weight: bold;
      color: #fff;
    }
  }
}
</style>
