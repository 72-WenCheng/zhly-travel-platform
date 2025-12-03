<template>
  <view class="vue3-page">
    <view class="header">
      <text class="title">旅游系统</text>
      <text class="subtitle">Vue3 + UniApp</text>
    </view>
    
    <view class="content">
      <view class="card">
        <text class="card-title">系统状态</text>
        <text class="card-desc">Vue3运行正常</text>
        <text class="card-info">当前时间: {{ currentTime }}</text>
      </view>
      
      <view class="card">
        <text class="card-title">功能测试</text>
        <button @click="showToast" class="test-btn">点击测试</button>
        <button @click="showModal" class="test-btn">显示弹窗</button>
      </view>
      
      <view class="card">
        <text class="card-title">数据展示</text>
        <text class="data-text">计数器: {{ count }}</text>
        <button @click="increment" class="count-btn">+1</button>
        <button @click="decrement" class="count-btn">-1</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// 响应式数据
const currentTime = ref('')
const count = ref(0)

// 定时器
let timer = null

// 生命周期
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

// 方法
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString()
}

const showToast = () => {
  uni.showToast({
    title: 'Vue3运行正常！',
    icon: 'success'
  })
}

const showModal = () => {
  uni.showModal({
    title: '提示',
    content: '这是Vue3 + UniApp的弹窗',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({
          title: '用户点击确定',
          icon: 'success'
        })
      }
    }
  })
}

const increment = () => {
  count.value++
}

const decrement = () => {
  count.value--
}
</script>

<style scoped>
.vue3-page {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
  color: white;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 20rpx;
}

.subtitle {
  font-size: 28rpx;
  opacity: 0.9;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.card-desc {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.card-info {
  font-size: 24rpx;
  color: #333;
  display: block;
}

.test-btn {
  background: #007aff;
  color: white;
  border: none;
  border-radius: 8rpx;
  padding: 16rpx 32rpx;
  font-size: 24rpx;
  margin-right: 16rpx;
  margin-top: 16rpx;
}

.count-btn {
  background: #34c759;
  color: white;
  border: none;
  border-radius: 8rpx;
  padding: 12rpx 24rpx;
  font-size: 24rpx;
  margin-right: 16rpx;
  margin-top: 16rpx;
}

.data-text {
  font-size: 24rpx;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}
</style>



















