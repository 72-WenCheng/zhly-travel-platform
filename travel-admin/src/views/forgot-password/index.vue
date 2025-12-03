<template>
  <div class="forgot-password-page">
    <div class="forgot-password-container">
      <div class="forgot-password-header">
        <div class="logo-icon">
          <el-icon><Location /></el-icon>
        </div>
        <h1>{{ userPlatformName }}</h1>
        <p>智能旅游攻略平台 · 生态旅游新体验</p>
      </div>
      
      <div class="forgot-password-form">
        <h2>忘记密码</h2>
        <p class="form-description">请输入您的邮箱地址，我们将发送重置密码的链接给您</p>
        
        <el-form :model="forgotPasswordForm" :rules="forgotPasswordRules" ref="forgotPasswordFormRef">
          <el-form-item prop="email">
            <el-input 
              v-model="forgotPasswordForm.email" 
              placeholder="请输入注册邮箱"
              prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="captcha">
            <div class="captcha-container">
              <el-input 
                v-model="forgotPasswordForm.captcha" 
                placeholder="请输入验证码"
                prefix-icon="Key"
                size="large"
                style="flex: 1; margin-right: 12px;"
              />
              <el-button 
                type="primary" 
                :disabled="captchaDisabled"
                @click="sendCaptcha"
                class="captcha-button"
              >
                {{ captchaText }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleForgotPassword" 
              :loading="loading"
              size="large"
              style="width: 100%;"
            >
              {{ loading ? '发送中...' : '发送重置链接' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="back-login">
          <div class="link-container">
            <span>想起密码了？</span>
            <el-link type="primary" @click="handleBackLogin" class="back-login-text">返回登录</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api/auth'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'

const router = useRouter()
const systemStore = useSystemStore()
const { userPlatformName } = storeToRefs(systemStore)

// 表单数据
const forgotPasswordForm = reactive({
  email: '',
  captcha: ''
})

// 验证码相关
const captchaDisabled = ref(false)
const captchaText = ref('获取验证码')
const countdown = ref(0)

// 表单验证规则 - 移除前端验证，交由后端处理
const forgotPasswordRules = {
  email: [],
  captcha: []
}

// 表单引用
const forgotPasswordFormRef = ref()
const loading = ref(false)

onMounted(() => {
  systemStore.fetchConfig().catch((error) => {
    console.error('加载系统配置失败:', error)
  })
})

// 发送验证码
const sendCaptcha = async () => {
  // 移除前端验证，交由后端处理
  console.log('发送验证码请求 - 邮箱:', forgotPasswordForm.email)
  
  try {
    // 调用发送验证码API
    const response = await authAPI.sendCaptcha(forgotPasswordForm.email)
    console.log('发送验证码响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('验证码已发送到您的邮箱')
      startCountdown()
    } else {
      ElMessage.error(response.message || '发送验证码失败')
    }
  } catch (error: any) {
    console.error('发送验证码错误:', error)
    console.error('错误response:', error.response)
    console.error('错误response.data:', error.response?.data)
    
    // 提取用户友好的错误信息
    let errorMessage = '发送验证码失败，请重试'
    
    // 优先使用后端返回的错误信息
    // 后端返回格式: { code: 400, message: "错误信息", data: null }
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
      console.log('使用后端返回的错误信息:', errorMessage)
    } else if (error.response?.data?.data) {
      errorMessage = error.response.data.data
      console.log('使用后端返回的数据信息:', errorMessage)
    } else if (error.message) {
      // 过滤掉技术性的错误信息
      if (error.message.includes('email not found') || 
          error.message.includes('邮箱未注册')) {
        errorMessage = '该邮箱未注册，请先注册账号'
      } else if (error.message.includes('Connection') || 
                 error.message.includes('timeout')) {
        errorMessage = '网络连接异常，请检查网络后重试'
      } else if (error.message.includes('500') || 
                 error.message.includes('Internal Server Error')) {
        errorMessage = '服务器异常，请稍后重试'
      } else {
        errorMessage = error.message
      }
      console.log('使用错误message:', errorMessage)
    }
    
    console.log('最终错误信息:', errorMessage)
    ElMessage.error(errorMessage)
  }
}

// 开始倒计时
const startCountdown = () => {
  captchaDisabled.value = true
  countdown.value = 60
  
  const timer = setInterval(() => {
    countdown.value--
    captchaText.value = `${countdown.value}秒后重发`
    
    if (countdown.value <= 0) {
      clearInterval(timer)
      captchaDisabled.value = false
      captchaText.value = '获取验证码'
    }
  }, 1000)
}

// 忘记密码处理
const handleForgotPassword = async () => {
  try {
    // 移除前端验证，交由后端处理
    // await forgotPasswordFormRef.value.validate()
    loading.value = true
    
    console.log('发送忘记密码请求 - 邮箱:', forgotPasswordForm.email, '验证码:', forgotPasswordForm.captcha)
    
    // 调用忘记密码API
    const response = await authAPI.forgotPassword({
      email: forgotPasswordForm.email,
      captcha: forgotPasswordForm.captcha
    })
    
    console.log('忘记密码响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('重置密码链接已发送到您的邮箱，请查收邮件')
      
      // 跳转到登录页面
      setTimeout(() => {
        router.push('/')
      }, 2000)
    } else {
      ElMessage.error(response.message || '发送失败')
    }
  } catch (error: any) {
    console.error('忘记密码错误:', error)
    console.error('错误response:', error.response)
    console.error('错误response.data:', error.response?.data)
    
    // 提取用户友好的错误信息
    let errorMessage = '发送失败，请重试'
    
    // 优先使用后端返回的错误信息
    // 后端返回格式: { code: 400, message: "错误信息", data: null }
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
      console.log('使用后端返回的错误信息:', errorMessage)
    } else if (error.response?.data?.data) {
      errorMessage = error.response.data.data
      console.log('使用后端返回的数据信息:', errorMessage)
    } else if (error.message) {
      // 过滤掉技术性的错误信息
      if (error.message.includes('captcha') && 
          (error.message.includes('invalid') || error.message.includes('错误'))) {
        errorMessage = '验证码错误或已过期，请重新获取'
      } else if (error.message.includes('email not found') || 
                 error.message.includes('邮箱未注册')) {
        errorMessage = '该邮箱未注册，请先注册账号'
      } else if (error.message.includes('Connection') || 
                 error.message.includes('timeout')) {
        errorMessage = '网络连接异常，请检查网络后重试'
      } else if (error.message.includes('500') || 
                 error.message.includes('Internal Server Error')) {
        errorMessage = '服务器异常，请稍后重试'
      } else {
        errorMessage = error.message
      }
      console.log('使用错误message:', errorMessage)
    }
    
    console.log('最终错误信息:', errorMessage)
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

// 返回登录
const handleBackLogin = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
.forgot-password-page {
  height: 100vh;
  background: #000000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
  
  background: #000000;
  
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.forgot-password-container {
  width: 100%;
  max-width: 450px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 50px 40px;
      border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 
    inset 0 0 20px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 2;
}

.forgot-password-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo-icon {
    margin-bottom: 20px;
    
    .el-icon {
      font-size: 48px;
      color: #ffffff;
    }
  }
  
  h1 {
    font-size: 28px;
    font-weight: 700;
    color: #ffffff;
    color: #ffffff;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 12px;
    line-height: 1.2;
  }
  
  p {
    font-size: 16px;
    color: #888;
    font-weight: 400;
  }
}

.forgot-password-form {
  .form-description {
    text-align: center;
    color: #aaa;
    font-size: 14px;
    margin-bottom: 30px;
    line-height: 1.5;
  }
  
  h2 {
    text-align: center;
    font-size: 22px;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 20px;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 50%;
      transform: translateX(-50%);
      width: 40px;
      height: 3px;
      background: #ffffff;
      border-radius: 2px;
    }
  }
  
  :deep(.el-form-item) {
    margin-bottom: 24px;
    
    .el-form-item__error {
      font-size: 12px;
      margin-top: 6px;
      color: #f87171;
    }
  }
  
  .captcha-container {
    display: flex;
    align-items: center;
    width: 100%;
    
    .captcha-button {
      border-radius: 8px;
      font-weight: 500;
      min-width: 120px;
      height: 48px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: #ffffff;
      
      &:hover:not(:disabled) {
        background: rgba(255, 255, 255, 0.15);
        box-shadow: none;
      }
      
      &:disabled {
        background: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.1);
        color: #666;
        cursor: not-allowed;
      }
    }
  }
  
  :deep(.el-input) {
    .el-input__wrapper {
      background: rgba(0, 0, 0, 0.3);
        border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 8px;
      transition: all 0.2s ease;
      outline: none !important;
      box-shadow: none !important;
      
      &:hover {
          border-color: rgba(255, 255, 255, 0.3);
        box-shadow: none !important;
      }
      
      &.is-focus {
        border-color: rgba(255, 255, 255, 0.3) !important;
        box-shadow: none !important;
        outline: none !important;
      }
      
      &:focus,
      &:focus-within,
      &:focus-visible {
        outline: none !important;
        box-shadow: none !important;
        border-color: rgba(255, 255, 255, 0.3) !important;
      }
    }
    
    .el-input__inner {
      color: #e0e0e0;
      font-size: 14px;
      padding: 0 12px;
      outline: none !important;
      border: none !important;
      box-shadow: none !important;
      
      &:focus,
      &:focus-visible,
      &:focus-within {
        outline: none !important;
        border: none !important;
        box-shadow: none !important;
      }
      
      &::placeholder {
        color: #666;
      }
    }
    
    .el-input__prefix {
      padding-left: 12px;
      
      .el-icon {
          color: #ffffff;
        font-size: 16px;
      }
    }
    
    // 覆盖所有可能的焦点状态组合
    &.is-focus .el-input__wrapper,
    &.is-focus .el-input__wrapper:hover {
      border-color: rgba(255, 255, 255, 0.3) !important;
      box-shadow: none !important;
      outline: none !important;
    }
  }
  
  // 全局移除输入框焦点外边框
  :deep(.el-input__wrapper.is-focus),
  :deep(.el-input.is-focus .el-input__wrapper),
  :deep(.el-input .el-input__wrapper.is-focus),
  :deep(.el-form-item .el-input__wrapper.is-focus),
  :deep(.el-form-item .el-input.is-focus .el-input__wrapper),
  :deep(.el-input__wrapper.is-focus:hover),
  :deep(.el-input.is-focus .el-input__wrapper:hover) {
    box-shadow: none !important;
    outline: none !important;
    border-color: rgba(255, 255, 255, 0.3) !important;
  }
  
  :deep(input:focus),
  :deep(input:focus-visible),
  :deep(.el-input__inner:focus),
  :deep(.el-input__inner:focus-visible) {
    outline: none !important;
    box-shadow: none !important;
    border: none !important;
  }
  
  :deep(.el-input__wrapper) {
    box-shadow: none !important;
    
    &::before,
    &::after {
      box-shadow: none !important;
      outline: none !important;
    }
  }
  
  :deep(.el-button) {
    border-radius: 12px;
    font-weight: 600;
    font-size: 16px;
    height: 50px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: #ffffff;
      
      &:hover {
        background: rgba(255, 255, 255, 0.15);
        transform: none;
      }
    
    &:active {
      transform: none;
    }
    
    &:disabled {
      background: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.1);
      color: #666;
      cursor: not-allowed;
      
      &:hover {
        transform: none;
        box-shadow: none;
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
  
  .back-login {
    text-align: center;
    margin-top: 25px;
    font-size: 14px;
    color: #888;
    
    .link-container {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px;
    }
    
    .back-login-text {
      color: #ffffff !important;
      font-weight: 500;
      text-decoration: none;
      
      &:hover {
        color: #e0e0e0 !important;
        text-shadow: 0 0 12px rgba(255, 255, 255, 0.3);
        text-decoration: underline;
      }
    }
  }
}

@media (max-width: 480px) {
  .forgot-password-page {
    padding: 10px;
  }
  
  .forgot-password-container {
    padding: 40px 25px;
    border-radius: 20px;
  }
  
  .forgot-password-header {
    margin-bottom: 30px;
    
    .logo-icon .el-icon {
      font-size: 40px;
    }
    
    h1 {
      font-size: 24px;
    }
    
    p {
      font-size: 14px;
    }
  }
  
  .forgot-password-form {
    h2 {
      font-size: 20px;
      margin-bottom: 15px;
    }
    
    .form-description {
      font-size: 13px;
      margin-bottom: 25px;
    }
    
    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: 8px;
      }
      
      .el-input__inner {
        font-size: 14px;
      }
    }
    
    :deep(.el-button) {
      height: 48px;
      font-size: 15px;
      border-radius: 12px;
    }
    
    .captcha-container {
      .captcha-button {
        min-width: 100px;
        height: 44px;
        font-size: 13px;
      }
    }
  }
}

// 全局移除所有输入框的焦点外边框
.forgot-password-page {
  :deep(.el-input__wrapper.is-focus) {
    --el-input-focus-border-color: rgba(255, 255, 255, 0.3) !important;
    --el-color-primary: rgba(255, 255, 255, 0.3) !important;
    box-shadow: none !important;
    outline: none !important;
    border-color: rgba(255, 255, 255, 0.3) !important;
  }
}
</style>
