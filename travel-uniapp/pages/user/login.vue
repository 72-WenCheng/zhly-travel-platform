<template>
  <view class="login-page">
    <view class="header">
      <text class="title">用户登录</text>
      <text class="subtitle">输入账号密码获取访问令牌</text>
    </view>

    <view class="form-card">
      <view class="form-item">
        <input
          v-model="loginData.username"
          class="input"
          placeholder="用户名"
          placeholder-style="color: #9ca3af"
        />
      </view>
      <view class="form-item">
        <input
          v-model="loginData.password"
          class="input"
          placeholder="密码"
          placeholder-style="color: #9ca3af"
          password
        />
      </view>
      <button class="primary-btn" :loading="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      <view class="register-tip">
        <text>还没有账号？</text>
        <text class="link" @click="goRegister">前往注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import request from '@/utils/request'

const loginData = ref({
  username: '',
  password: ''
})
const loading = ref(false)

const validate = () => {
  if (!loginData.value.username.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return false
  }
  if (!loginData.value.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return false
  }
  return true
}

const handleLogin = async () => {
  if (!validate() || loading.value) return
  loading.value = true
  try {
    const res = await request.post('/auth/login', {
      username: loginData.value.username,
      password: loginData.value.password,
      loginType: 'user'
    })
    if (res.code === 200) {
      const token = res.data?.token
      if (token) {
        uni.setStorageSync('token', token)
        uni.setStorageSync('userId', res.data?.userId)
      }
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 500)
    } else {
      uni.showToast({ title: res.message || '登录失败', icon: 'none' })
    }
  } catch (err: any) {
    uni.showToast({ title: err.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  uni.navigateTo({ url: '/pages/user/register' })
}
</script>

<style scoped>
.login-page {
  padding: 40rpx 30rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: #fff;
}

.header {
  text-align: left;
  margin-bottom: 40rpx;
}

.title {
  font-size: 48rpx;
  font-weight: 700;
  display: block;
}

.subtitle {
  margin-top: 10rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
}

.form-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
  color: #111827;
}

.form-item {
  margin-bottom: 20rpx;
}

.input {
  width: 100%;
  padding: 24rpx;
  background: #f3f4f6;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #111827;
}

.primary-btn {
  width: 100%;
  margin-top: 10rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 22rpx;
  border-radius: 14rpx;
  font-size: 30rpx;
}

.register-tip {
  margin-top: 20rpx;
  text-align: center;
  color: #6b7280;
  font-size: 26rpx;
}

.link {
  color: #667eea;
  margin-left: 8rpx;
}
</style>



















