<template>
  <div class="reset-password-page">
    <div class="reset-password-container">
      <div class="reset-password-header">
        <div class="logo-icon">
          <el-icon><Location /></el-icon>
        </div>
        <h1>{{ userPlatformName }}</h1>
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
              :placeholder="passwordPlaceholder"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Lock } from '@element-plus/icons-vue'
import { authAPI } from '@/api/auth'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'
import { getPublicSecurityConfig } from '@/api/systemConfig'

const router = useRouter()
const route = useRoute()
const systemStore = useSystemStore()
const { userPlatformName } = storeToRefs(systemStore)

// 表单数据
const resetPasswordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const passwordMinLength = ref(6)
const passwordPlaceholder = computed(() => `请输入新密码（至少${passwordMinLength.value}位）`)

// 表单验证规则
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== resetPasswordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const resetPasswordRules = {
  newPassword: [
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (!value) {
          callback(new Error('请输入新密码'))
        } else if (value.length < passwordMinLength.value) {
          callback(new Error(`密码长度至少${passwordMinLength.value}位`))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 表单引用
const resetPasswordFormRef = ref()
const loading = ref(false)
const resetToken = ref('')

// 从URL参数获取token
onMounted(async () => {
  systemStore.fetchConfig().catch((error) => {
    console.error('加载系统配置失败:', error)
  })
  try {
    const res = await getPublicSecurityConfig()
    if (res.code === 200 && res.data?.passwordMinLength) {
      const value = Number(res.data.passwordMinLength)
      if (!Number.isNaN(value)) {
        passwordMinLength.value = value
      }
    }
  } catch (error) {
    console.error('加载安全配置失败:', error)
  }
  
  const token = route.query.token as string
  if (!token) {
    ElMessage.error('重置链接无效，请重新申请')
    setTimeout(() => {
      router.push('/forgot-password')
    }, 2000)
    return
  }
  resetToken.value = token
})

// 重置密码处理
const handleResetPassword = async () => {
  if (!resetPasswordFormRef.value) return
  
  await resetPasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!resetToken.value) {
        ElMessage.error('重置链接无效，请重新申请')
        router.push('/forgot-password')
        return
      }
      
      loading.value = true
      try {
        const response = await authAPI.resetPassword({
          token: resetToken.value,
          newPassword: resetPasswordForm.newPassword
        })
        
        if (response.code === 200) {
          ElMessage.success('密码重置成功，请使用新密码登录')
          
          // 跳转到登录页面
          setTimeout(() => {
            router.push('/')
          }, 2000)
        } else {
          ElMessage.error(response.message || '重置密码失败')
        }
      } catch (error: any) {
        console.error('重置密码错误:', error)
        
        // 提取用户友好的错误信息
        let errorMessage = '重置密码失败，请重试'
        
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.message) {
          if (error.message.includes('无效') || error.message.includes('失效')) {
            errorMessage = '重置链接已失效，请重新申请'
          } else if (error.message.includes('Connection') || error.message.includes('timeout')) {
            errorMessage = '网络连接异常，请检查网络后重试'
          } else {
            errorMessage = error.message
          }
        }
        
        ElMessage.error(errorMessage)
      } finally {
        loading.value = false
      }
    }
  })
}

// 返回登录
const handleBackLogin = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
.reset-password-page {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
  
  // 添加装饰性背景元素
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
    background-size: 20px 20px;
    animation: float 20s infinite linear;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 20%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: linear-gradient(45deg, rgba(255,255,255,0.1), rgba(255,255,255,0.05));
    border-radius: 50%;
    animation: float 15s infinite ease-in-out;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.reset-password-container {
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 50px 40px;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
  
  // 添加顶部装饰线
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 4px;
    background: linear-gradient(90deg, #667eea, #764ba2);
    border-radius: 0 0 4px 4px;
  }
}

.reset-password-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo-icon {
    margin-bottom: 20px;
    
    .el-icon {
      font-size: 48px;
      color: #667eea;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
  
  h1 {
    font-size: 28px;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 12px;
    line-height: 1.2;
  }
  
  p {
    font-size: 16px;
    color: #666;
    font-weight: 400;
    opacity: 0.8;
  }
}

.reset-password-form {
  .form-description {
    text-align: center;
    color: #666;
    font-size: 14px;
    margin-bottom: 30px;
    line-height: 1.5;
  }
  
  h2 {
    text-align: center;
    font-size: 22px;
    font-weight: 600;
    color: #333;
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
      background: linear-gradient(90deg, #667eea, #764ba2);
      border-radius: 2px;
    }
  }
  
  :deep(.el-form-item) {
    margin-bottom: 24px;
    
    .el-form-item__error {
      font-size: 12px;
      margin-top: 6px;
    }
  }
  
  :deep(.el-input) {
    .el-input__wrapper {
      border-radius: 16px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      border: 2px solid transparent;
      transition: all 0.3s ease;
      background: rgba(255, 255, 255, 0.8);
      
      &:hover {
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
        border-color: rgba(102, 126, 234, 0.3);
      }
      
      &.is-focus {
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
        border-color: #667eea;
      }
    }
    
    .el-input__inner {
      font-size: 15px;
      padding: 0 16px;
      height: 48px;
      
      &::placeholder {
        color: #999;
        font-weight: 400;
      }
    }
    
    .el-input__prefix {
      left: 16px;
      
      .el-icon {
        font-size: 18px;
        color: #667eea;
      }
    }
  }
  
  :deep(.el-button) {
    border-radius: 16px;
    font-weight: 600;
    font-size: 16px;
    height: 52px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
      transition: left 0.5s;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
      
      &::before {
        left: 100%;
      }
    }
    
    &:active {
      transform: translateY(0);
    }
    
    &:disabled {
      background: #ccc;
      cursor: not-allowed;
      
      &:hover {
        transform: none;
        box-shadow: none;
      }
    }
  }
  
  .back-login {
    text-align: center;
    margin-top: 25px;
    font-size: 14px;
    color: #666;
    
    .link-container {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px;
    }
    
    .back-login-text {
      font-weight: 500;
      text-decoration: none;
      
      &:hover {
        text-decoration: underline;
      }
    }
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
