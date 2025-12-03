<template>
  <view class="login-page">
    <view class="header">
      <text class="title">用户登录</text>
      <text class="subtitle">欢迎使用旅游系统</text>
    </view>
    
    <view class="content">
      <view class="card">
        <text class="card-title">登录信息</text>
        <text class="card-desc">请输入用户名和密码</text>
      </view>
      
      <view class="card">
        <text class="card-title">快速登录</text>
        <text class="card-desc">支持手机号、邮箱登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const loginData = ref({
  username: '',
  password: ''
})

const handleLogin = () => {
  uni.showToast({
    title: '登录功能开发中',
    icon: 'none'
  })
}
</script>

<style scoped>
.login-page {
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
}
</style>



















