<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <div class="logo-icon">
          <el-icon><Location /></el-icon>
        </div>
        <h1>{{ userPlatformName }}</h1>
        <p>智能旅游攻略平台 · 生态旅游新体验</p>
      </div>
      
      <div class="register-form">
        <h2>用户注册</h2>
        
        <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="请输入邮箱"
              prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="请确认密码"
              prefix-icon="Lock"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="请输入手机号"
              prefix-icon="Phone"
              size="large"
            />
          </el-form-item>
          <el-form-item class="agreement-item">
            <div class="agreement-wrapper">
              <el-checkbox v-model="agreeTerms" class="agreement-checkbox" />
              <div class="agreement-text" @click.stop>
                <span>我已阅读并同意</span>
                <el-link type="primary" @click.stop.prevent="showUserAgreement">《用户协议》</el-link>
                <span>和</span>
                <el-link type="primary" @click.stop.prevent="showPrivacyPolicy">《隐私政策》</el-link>
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleRegister" 
              :loading="loading"
              size="large"
              style="width: 100%;"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-link">
          <div class="link-container">
            <span>已有账号？</span>
            <el-link type="primary" @click="handleLogin" class="login-link-text">立即登录</el-link>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 用户协议和隐私政策对话框 -->
    <AgreementDialog 
      v-model="showUserAgreementDialog" 
      type="user" 
      @confirmed="handleAgreementConfirmed('user')" 
    />
    <AgreementDialog 
      v-model="showPrivacyDialog" 
      type="privacy" 
      @confirmed="handleAgreementConfirmed('privacy')" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api/auth'
import { getPublicSecurityConfig } from '@/api/systemConfig'
import AgreementDialog from '@/components/AgreementDialog.vue'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const systemStore = useSystemStore()
const { userPlatformName } = storeToRefs(systemStore)

// 表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

// 同意条款
const agreeTerms = ref(false)
const userAgreementRead = ref(false)
const privacyAgreementRead = ref(false)

// 表单验证规则
const passwordMinLength = ref(6)

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (!value) {
          callback(new Error('请输入密码'))
        } else if (value.length < passwordMinLength.value || value.length > 20) {
          callback(new Error(`密码长度在 ${passwordMinLength.value} 到 20 个字符`))
        } else if (!/^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]+$/.test(value)) {
          callback(new Error('密码必须包含字母和数字'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

// 表单引用
const registerFormRef = ref()
const loading = ref(false)

// 对话框显示状态
const showUserAgreementDialog = ref(false)
const showPrivacyDialog = ref(false)

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
})

// 注册处理
const handleRegister = async () => {
  try {
    // 手动验证每个字段
    if (!registerForm.username || registerForm.username.trim() === '') {
      ElMessage.error('请输入用户名')
      return
    }
    
    if (registerForm.username.length < 3 || registerForm.username.length > 20) {
      ElMessage.error('用户名长度应在3到20个字符之间')
      return
    }
    
    if (!registerForm.email || registerForm.email.trim() === '') {
      ElMessage.error('请输入邮箱')
      return
    }
    
    // 验证邮箱格式
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
    if (!emailRegex.test(registerForm.email)) {
      ElMessage.error('请输入正确的邮箱格式')
      return
    }
    
    if (!registerForm.password || registerForm.password.trim() === '') {
      ElMessage.error('请输入密码')
      return
    }
    
    if (registerForm.password.length < passwordMinLength.value || registerForm.password.length > 20) {
      ElMessage.error(`密码长度应在${passwordMinLength.value}到20个字符之间`)
      return
    }
    
    // 验证密码包含字母和数字
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)/
    if (!passwordRegex.test(registerForm.password)) {
      ElMessage.error('密码必须包含字母和数字')
      return
    }
    
    if (registerForm.password !== registerForm.confirmPassword) {
      ElMessage.error('两次输入密码不一致')
      return
    }
    
    if (!registerForm.phone || registerForm.phone.trim() === '') {
      ElMessage.error('请输入手机号')
      return
    }
    
    // 验证手机号格式
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(registerForm.phone)) {
      ElMessage.error('请输入正确的手机号格式')
      return
    }
    
    if (!agreeTerms.value) {
      ElMessage.warning('请先同意用户协议和隐私政策')
      return
    }
    
    console.log('开始注册，表单数据:', {
      username: registerForm.username,
      email: registerForm.email,
      phone: registerForm.phone
    })
    
    loading.value = true
    
    // 从URL参数获取邀请码
    const inviteCode = route.query.invite as string || ''
    
    // 调用注册API
    const response = await authAPI.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      phone: registerForm.phone,
      inviteCode: inviteCode  // 传递邀请码
    })
    
    console.log('注册响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('注册成功！请登录')
      setTimeout(() => {
        router.push('/')
      }, 1500)
    } else {
      console.error('注册失败，返回码:', response.code, '错误信息:', response.message)
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error: any) {
    console.error('注册错误:', error)
    console.error('错误response:', error.response)
    console.error('错误response.data:', error.response?.data)
    
    // 提取用户友好的错误信息
    let errorMessage = '注册失败，请重试'
    
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
      if (error.message.includes('username') && error.message.includes('already exists')) {
        errorMessage = '用户名已存在，请选择其他用户名'
      } else if (error.message.includes('email') && error.message.includes('already exists')) {
        errorMessage = '邮箱已被注册，请使用其他邮箱'
      } else if (error.message.includes('phone') && error.message.includes('already exists')) {
        errorMessage = '手机号已被使用，请使用其他手机号'
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

// 登录处理
const handleLogin = () => {
  router.push('/')
}

// 显示用户协议
const showUserAgreement = () => {
  showUserAgreementDialog.value = true
}

// 显示隐私政策
const showPrivacyPolicy = () => {
  showPrivacyDialog.value = true
}

const handleAgreementConfirmed = (type: 'user' | 'privacy') => {
  if (type === 'user') {
    userAgreementRead.value = true
  } else {
    privacyAgreementRead.value = true
  }
  if (!agreeTerms.value && userAgreementRead.value && privacyAgreementRead.value) {
    agreeTerms.value = true
  }
}

watch(agreeTerms, (val) => {
  if (!val) {
    userAgreementRead.value = false
    privacyAgreementRead.value = false
  }
})
</script>

<style lang="scss" scoped>
.register-page {
  height: 100vh;
  background: #000000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.register-container {
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

.register-header {
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

.register-form {
  h2 {
    text-align: center;
    font-size: 22px;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 35px;
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
  
  .agreement-item {
    margin-bottom: 20px !important;
    .agreement-wrapper {
      display: flex;
      align-items: center;
      gap: 6px;
    }
    
    :deep(.agreement-checkbox .el-checkbox__input) {
      margin-top: 0;
    }
    
    :deep(.el-checkbox) {
      margin-right: 0;
      
      .el-checkbox__input.is-checked .el-checkbox__inner {
        background: rgba(255, 255, 255, 0.2);
        border-color: #ffffff;
        box-shadow: 0 0 10px rgba(255, 255, 255, 0.2);
      }
    }
    
    .agreement-text {
      color: #888;
      font-size: 14px;
      line-height: 1.4;
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 0;
      
      span {
        display: flex;
        align-items: center;
        margin-right: 2px;
      }
      
      .el-link {
        color: #ffffff !important;
        font-size: 14px;
        margin: 0 1px;
        text-decoration: none;
        font-weight: 500;
        
        &:hover {
          color: #e0e0e0 !important;
          text-decoration: underline;
        }
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
  
  :deep(.el-link) {
    color: #ffffff !important;
    font-weight: 500;
    
    &:hover {
        color: #e0e0e0 !important;
      text-decoration: underline;
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
  }
  
  .login-link {
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
    
    .login-link-text {
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
  .register-page {
    padding: 10px;
  }
  
  .register-container {
    padding: 40px 25px;
    border-radius: 20px;
  }
  
  .register-header {
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
  
  .register-form {
    h2 {
      font-size: 20px;
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
  }
}

// 全局移除所有输入框的焦点外边框
.register-page {
  :deep(.el-input__wrapper.is-focus) {
    --el-input-focus-border-color: rgba(255, 255, 255, 0.3) !important;
    --el-color-primary: rgba(255, 255, 255, 0.3) !important;
    box-shadow: none !important;
    outline: none !important;
    border-color: rgba(255, 255, 255, 0.3) !important;
  }
}
</style>

