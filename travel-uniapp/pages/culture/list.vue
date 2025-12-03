<template>
  <view class="list-page">
    <view class="header">
      <text class="title">文旅列表</text>
    </view>
    
    <view class="content">
      <view class="card">
        <text class="card-title">文旅项目</text>
        <text class="card-desc">浏览所有文旅项目</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const cultureList = ref([])
</script>

<style scoped>
.list-page {
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
}
</style>



















