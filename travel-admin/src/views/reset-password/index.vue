<template>
  <div class="reset-password-page">
    <div class="reset-password-container">
      <div class="reset-password-header">
        <div class="logo-icon">
          <el-icon><Location /></el-icon>
        </div>
        <h1>智慧生态旅游系统</h1>
        <p>智能旅游攻略平台 · 生态旅游新体验</p>
      </div>
      
      <div class="reset-password-form">
        <h2>重置密码</h2>
        <p class="form-description">请输入您的新密码</p>
        
        <el-form :model="resetPasswordForm" :rules="resetPasswordRules" ref="resetPasswordFormRef">
          <el-form-item prop="newPassword">
            <el-input 
              v-model="resetPasswordForm.newPassword" 
              type="password"
              placeholder="请输入新密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="resetPasswordForm.confirmPassword" 
              type="password"
              placeholder="请再次输入新密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleResetPassword" 
              :loading="loading"
              size="large"
              style="width: 100%;"
            >
              {{ loading ? '重置中...' : '重置密码' }}
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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api/auth'

const router = useRouter()
const route = useRoute()

// 表单数据
const resetPasswordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 重置密码token
const resetToken = ref('')

// 表单验证规则
const resetPasswordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { 
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== resetPasswordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 表单引用
const resetPasswordFormRef = ref()
const loading = ref(false)

// 页面加载时获取token
onMounted(() => {
  const token = route.query.token as string
  if (token) {
    resetToken.value = token
    console.log('重置密码token:', token)
  } else {
    ElMessage.error('重置链接无效')
    setTimeout(() => {
      router.push('/')
    }, 2000)
  }
})

// 重置密码处理
const handleResetPassword = async () => {
  // 使用 validate 的回调形式来捕获验证错误
  resetPasswordFormRef.value.validate((valid: boolean, fields: any) => {
    if (!valid) {
      // 获取第一个错误信息并显示为弹出提示
      const firstError = Object.keys(fields || {})[0]
      if (firstError && fields[firstError] && fields[firstError].length > 0) {
        ElMessage.error(fields[firstError][0].message)
      } else {
        ElMessage.error('请检查表单输入')
      }
      return
    }
    
    // 验证通过，执行重置密码逻辑
    executeResetPassword()
  })
}

// 执行重置密码
const executeResetPassword = async () => {
  try {
    loading.value = true
    
    console.log('发送重置密码请求 - token:', resetToken.value)
    
    // 调用重置密码API
    const response = await authAPI.resetPassword({
      token: resetToken.value,
      newPassword: resetPasswordForm.newPassword
    })
    
    console.log('重置密码响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('密码重置成功，请使用新密码登录')
      
      // 跳转到登录页面
      setTimeout(() => {
        router.push('/')
      }, 2000)
    } else {
      ElMessage.error(response.message || '重置失败')
    }
  } catch (error: any) {
    console.error('重置密码错误:', error)
    console.error('错误response:', error.response)
    console.error('错误response.data:', error.response?.data)
    
    // 提取用户友好的错误信息
    let errorMessage = '重置失败，请重试'
    
    // 优先使用后端返回的错误信息
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
      console.log('使用后端返回的错误信息:', errorMessage)
    } else if (error.response?.data?.data) {
      errorMessage = error.response.data.data
      console.log('使用后端返回的数据信息:', errorMessage)
    } else if (error.message) {
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
.reset-password-page {
  height: 100vh;
  background: #000000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.reset-password-container {
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 50px 40px;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  position: relative;
  z-index: 1;
}

.reset-password-header {
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
    margin-bottom: 12px;
    line-height: 1.2;
  }
  
  p {
    font-size: 16px;
    color: #888;
    font-weight: 400;
  }
}

.reset-password-form {
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
      background: rgba(255, 255, 255, 0.3);
      border-radius: 2px;
    }
  }
  
  :deep(.el-form-item) {
    margin-bottom: 24px;
    
    // 隐藏默认的表单错误提示
    .el-form-item__error {
      display: none !important;
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
      height: 48px;
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
        font-weight: 400;
      }
    }
    
    .el-input__prefix {
      padding-left: 12px;
      
      .el-icon {
        font-size: 16px;
        color: #ffffff;
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
  
  // 移除所有可能的阴影效果
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
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.15);
      border-color: rgba(255, 255, 255, 0.4);
    }
    
    &:active {
      background: rgba(255, 255, 255, 0.12);
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
      font-weight: 500;
      text-decoration: none;
      color: #ffffff !important;
      
      &:hover {
        color: #e0e0e0 !important;
        text-decoration: underline;
      }
    }
  }
}

// 全局移除所有输入框的焦点外边框
.reset-password-page {
  :deep(.el-input__wrapper) {
    box-shadow: none !important;
    
    &.is-focus,
    &.is-focus:hover {
      box-shadow: none !important;
      outline: none !important;
      border-color: rgba(255, 255, 255, 0.3) !important;
    }
  }
  
  :deep(.el-input.is-focus .el-input__wrapper),
  :deep(.el-input .el-input__wrapper.is-focus),
  :deep(.el-form-item .el-input__wrapper.is-focus),
  :deep(.el-form-item .el-input.is-focus .el-input__wrapper),
  :deep(.reset-password-form .el-input__wrapper.is-focus),
  :deep(.reset-password-form .el-input.is-focus .el-input__wrapper) {
    box-shadow: none !important;
    outline: none !important;
    border-color: rgba(255, 255, 255, 0.3) !important;
  }
  
  :deep(.el-input__inner) {
    &:focus,
    &:focus-visible,
    &:focus-within {
      outline: none !important;
      box-shadow: none !important;
      border: none !important;
    }
  }
  
  // 移除所有可能的伪元素阴影
  :deep(.el-input__wrapper::before),
  :deep(.el-input__wrapper::after) {
    box-shadow: none !important;
    outline: none !important;
  }
  
  // 覆盖 CSS 变量
  :deep(.el-input__wrapper.is-focus) {
    --el-input-focus-border-color: rgba(255, 255, 255, 0.3) !important;
    --el-color-primary: rgba(255, 255, 255, 0.3) !important;
  }
}

@media (max-width: 480px) {
  .reset-password-page {
    padding: 10px;
  }
  
  .reset-password-container {
    padding: 40px 25px;
    border-radius: 20px;
  }
  
  .reset-password-header {
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
  
  .reset-password-form {
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
        border-radius: 12px;
      }
      
      .el-input__inner {
        height: 44px;
        font-size: 14px;
      }
    }
    
    :deep(.el-button) {
      height: 48px;
      font-size: 15px;
      border-radius: 12px;
    }
  }
}
</style>

