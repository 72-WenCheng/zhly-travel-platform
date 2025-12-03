<template>
  <view class="test-page">
    <text class="title">旅游系统</text>
    <text class="subtitle">UniApp测试页面</text>
    <button @click="showToast" class="btn">点击测试</button>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const showToast = () => {
  uni.showToast({
    title: 'UniApp运行正常！',
    icon: 'success'
  })
}
</script>

<style>
.test-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.subtitle {
  font-size: 28rpx;
  margin-bottom: 40rpx;
  opacity: 0.9;
}

.btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2rpx solid white;
  border-radius: 50rpx;
  padding: 20rpx 40rpx;
  font-size: 28rpx;
}
</style>