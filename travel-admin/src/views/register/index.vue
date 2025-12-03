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
        
        <!-- 注册方式切换 -->
        <div class="register-method-tabs">
          <div 
            class="tab-item" 
            :class="{ active: registerMethod === 'email' }"
            @click="registerMethod = 'email'"
          >
            邮箱注册
          </div>
          <div 
            class="tab-item" 
            :class="{ active: registerMethod === 'phone' }"
            @click="registerMethod = 'phone'"
          >
            手机注册
          </div>
        </div>
        
        <!-- 邮箱注册表单 -->
        <el-form 
          v-if="registerMethod === 'email'"
          :model="registerForm" 
          :rules="registerRules" 
          ref="registerFormRef"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="请输入邮箱"
              :prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="请确认密码"
              :prefix-icon="Lock"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="请输入手机号"
              :prefix-icon="Phone"
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

        <!-- 手机号注册表单 -->
        <el-form 
          v-if="registerMethod === 'phone'"
          :model="phoneRegisterForm" 
          :rules="phoneRegisterRules" 
          ref="phoneRegisterFormRef"
        >
          <el-form-item prop="phone">
            <el-input 
              v-model="phoneRegisterForm.phone" 
              placeholder="请输入手机号"
              :prefix-icon="Phone"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="captcha">
            <div class="captcha-input-wrapper">
              <el-input 
                v-model="phoneRegisterForm.captcha" 
                placeholder="请输入验证码"
                :prefix-icon="Message"
                size="large"
              />
              <el-button 
                :disabled="countdown > 0"
                @click="handleSendPhoneCaptcha"
                class="captcha-button"
              >
                {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="phoneRegisterForm.password" 
              type="password" 
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="phoneRegisterForm.confirmPassword" 
              type="password" 
              placeholder="请确认密码"
              :prefix-icon="Lock"
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
              @click="handlePhoneRegister" 
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
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Message, Lock, Phone } from '@element-plus/icons-vue'
import { authAPI } from '@/api/auth'
import { getPublicSecurityConfig } from '@/api/systemConfig'
import AgreementDialog from '@/components/AgreementDialog.vue'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const systemStore = useSystemStore()
const { userPlatformName } = storeToRefs(systemStore)

// 注册方式选择（邮箱注册/手机注册）
const registerMethod = ref('email')

// 邮箱注册表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

// 手机号注册表单数据
const phoneRegisterForm = reactive({
  phone: '',
  captcha: '',
  password: '',
  confirmPassword: ''
})

// 同意条款
const agreeTerms = ref(false)
const userAgreementRead = ref(false)
const privacyAgreementRead = ref(false)

// 验证码倒计时
const countdown = ref(0)
let countdownTimer: NodeJS.Timeout | null = null

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
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (!value) {
          callback(new Error('请输入邮箱'))
        } else {
          // 基本的邮箱格式验证
          const emailRegex = /^[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9.-]*[a-zA-Z0-9])?\.[a-zA-Z]{2,6}$/
          
          if (!emailRegex.test(value)) {
            callback(new Error('请输入正确的邮箱格式'))
            return
          }
          
          // 提取用户名和域名部分
          const parts = value.split('@')
          const username = parts[0]
          const domain = parts[1].toLowerCase()
          
          // 验证用户名部分：不能全是数字，必须包含至少一个字母
          if (/^\d+$/.test(username)) {
            callback(new Error('邮箱用户名不能全是数字，必须包含字母'))
            return
          }
          
          // 验证域名部分：不能全是数字
          const domainParts = domain.split('.')
          const domainName = domainParts[0]
          if (/^\d+$/.test(domainName)) {
            callback(new Error('请输入正确的邮箱格式'))
            return
          }
          
          // 验证必须是各大邮箱服务商的后缀
          const validEmailDomains = [
            // 国内邮箱
            'qq.com', 'foxmail.com', 'vip.qq.com',
            '163.com', '126.com', 'yeah.net', 'vip.163.com',
            'sina.com', 'sina.cn', 'vip.sina.com',
            'sohu.com', 'vip.sohu.com',
            '139.com',
            'tom.com',
            '21cn.com',
            '188.com',
            'aliyun.com',
            '189.cn',
            'wo.cn',
            // 国际邮箱
            'gmail.com',
            'outlook.com', 'hotmail.com', 'live.com', 'msn.com',
            'yahoo.com', 'yahoo.cn', 'yahoo.com.cn',
            'icloud.com', 'me.com', 'mac.com',
            'aol.com',
            'mail.com',
            'protonmail.com', 'proton.me',
            'zoho.com',
            'yandex.com',
            'mail.ru',
            'gmx.com',
            'rediffmail.com',
            // 企业邮箱常见后缀
            'qq.com', '163.com', 'sina.com', 'sohu.com',
            'gmail.com', 'outlook.com', 'yahoo.com'
          ]
          
          // 检查是否是有效的邮箱服务商域名
          let isValidDomain = false
          for (const validDomain of validEmailDomains) {
            if (domain === validDomain || domain.endsWith('.' + validDomain)) {
              isValidDomain = true
              break
            }
          }
          
          // 如果不在列表中，检查是否是常见的通用域名格式（允许但不推荐）
          if (!isValidDomain) {
            // 允许常见的通用顶级域名，但要求域名部分不能全是数字
            const commonTlds = ['com', 'cn', 'net', 'org', 'edu', 'gov', 'co', 'io', 'me', 'tv', 'cc', 'info', 'biz', 'name', 'mobi', 'asia', 'xyz', 'top', 'wang', 'tech', 'online', 'site', 'store', 'shop', 'app', 'dev', 'cloud', 'email', 'live', 'work', 'fun', 'love', 'life', 'world', 'today', 'city', 'group', 'team', 'company']
            const tld = domainParts[domainParts.length - 1].toLowerCase()
            
            if (commonTlds.includes(tld) && domainParts.length >= 2) {
              // 允许通用域名，但给出提示
              isValidDomain = true
            } else {
              callback(new Error('请输入正确的邮箱格式，支持QQ、163、Gmail、Outlook等主流邮箱服务商'))
              return
            }
          }
          
          // 验证域名长度：域名部分不能过长
          if (domain.length > 253) {
            callback(new Error('邮箱域名过长，请输入正确的邮箱格式'))
            return
          }
          
          // 验证用户名长度：不能过长
          if (username.length > 64) {
            callback(new Error('邮箱用户名过长，请输入正确的邮箱格式'))
            return
          }
          
          callback()
        }
      },
      trigger: 'blur'
    }
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
const phoneRegisterFormRef = ref()
const loading = ref(false)

// 手机号注册验证规则
const phoneRegisterRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (!value) {
          callback(new Error('请输入手机号'))
        } else {
          // 简单的手机号格式验证（支持国际格式）
          const phoneRegex = /^(\+?[1-9]\d{1,14}|1[3-9]\d{9})$/
          if (!phoneRegex.test(value.replace(/[\s-]/g, ''))) {
            callback(new Error('请输入正确的手机号格式'))
          } else {
            callback()
          }
        }
      },
      trigger: 'blur'
    }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
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
        if (value !== phoneRegisterForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

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
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid, fields) => {
    if (!valid) {
      // 验证失败，使用 ElMessage 显示第一个错误
      const firstError = Object.keys(fields || {})[0]
      if (firstError && fields[firstError] && fields[firstError].length > 0) {
        ElMessage.error(fields[firstError][0].message)
      } else {
        ElMessage.error('请检查输入信息')
      }
      return
    }
    
    if (!agreeTerms.value) {
      ElMessage.warning('请先同意用户协议和隐私政策')
      return
    }
    
    try {
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
  })
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

// 发送手机验证码
const handleSendPhoneCaptcha = async () => {
  if (!phoneRegisterFormRef.value) return
  
  await phoneRegisterFormRef.value.validateField('phone', async (valid: boolean) => {
    if (!valid) {
      return
    }
    
    try {
      loading.value = true
      const response = await authAPI.sendPhoneCaptcha({ phone: phoneRegisterForm.phone })
      if (response.code === 200) {
        ElMessage.success('验证码已发送，请查收')
        // 开始倒计时
        startCountdown()
      } else {
        ElMessage.error(response.message || '发送验证码失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '发送验证码失败')
    } finally {
      loading.value = false
    }
  })
}

// 开始倒计时
const startCountdown = () => {
  countdown.value = 60
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }
  }, 1000)
}

// 手机号注册处理
const handlePhoneRegister = async () => {
  if (!phoneRegisterFormRef.value) return
  
  await phoneRegisterFormRef.value.validate(async (valid, fields) => {
    if (!valid) {
      // 验证失败，使用 ElMessage 显示第一个错误
      const firstError = Object.keys(fields || {})[0]
      if (firstError && fields[firstError] && fields[firstError].length > 0) {
        ElMessage.error(fields[firstError][0].message)
      } else {
        ElMessage.error('请检查输入信息')
      }
      return
    }
    
    if (!agreeTerms.value) {
      ElMessage.warning('请先同意用户协议和隐私政策')
      return
    }
    
    try {
      loading.value = true
      
      // 从URL参数获取邀请码
      const inviteCode = route.query.invite as string || ''
      
      // 调用手机号注册API
      const response = await authAPI.registerByPhone({
        phone: phoneRegisterForm.phone,
        captcha: phoneRegisterForm.captcha,
        password: phoneRegisterForm.password,
        inviteCode: inviteCode
      })
      
      if (response.code === 200) {
        ElMessage.success('注册成功！请登录')
        setTimeout(() => {
          router.push('/')
        }, 1500)
      } else {
        ElMessage.error(response.message || '注册失败')
      }
    } catch (error: any) {
      let errorMessage = '注册失败，请重试'
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message
      } else if (error.message) {
        errorMessage = error.message
      }
      
      ElMessage.error(errorMessage)
    } finally {
      loading.value = false
    }
  })
}

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})

// 监听注册方式切换，清空表单
watch(() => registerMethod.value, () => {
  phoneRegisterForm.phone = ''
  phoneRegisterForm.captcha = ''
  phoneRegisterForm.password = ''
  phoneRegisterForm.confirmPassword = ''
  registerForm.username = ''
  registerForm.email = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  countdown.value = 0
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
    margin-bottom: 25px;
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

  // 注册方式切换标签页
  .register-method-tabs {
    display: flex;
    gap: 12px;
    margin-bottom: 30px;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 4px;
    
    .tab-item {
      flex: 1;
      text-align: center;
      padding: 10px 20px;
      color: #888;
      font-size: 14px;
      cursor: pointer;
      border-radius: 6px;
      transition: all 0.3s ease;
      
      &:hover {
        color: #fff;
        background: rgba(255, 255, 255, 0.05);
      }
      
      &.active {
        color: #fff;
        background: rgba(255, 255, 255, 0.1);
        font-weight: 600;
      }
    }
  }

  // 验证码输入框容器
  .captcha-input-wrapper {
    display: flex;
    gap: 12px;
    align-items: flex-start;
    
    .el-input {
      flex: 1;
    }
    
    .captcha-button {
      white-space: nowrap;
      min-width: 120px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #fff;
      
      &:hover:not(:disabled) {
        background: rgba(255, 255, 255, 0.15);
        border-color: rgba(255, 255, 255, 0.3);
      }
      
      &:disabled {
        color: #666;
        cursor: not-allowed;
      }
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
    
    .el-form-item__error {
      display: none; // 隐藏表单下方的错误提示
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
      
      .el-checkbox__input {
        .el-checkbox__inner {
          width: 18px;
          height: 18px;
          border: 2px solid rgba(255, 255, 255, 0.3);
          background: rgba(0, 0, 0, 0.2);
          border-radius: 4px;
          transition: all 0.3s ease;
          
          &:hover {
            border-color: rgba(255, 255, 255, 0.5);
            background: rgba(255, 255, 255, 0.05);
          }
          
          &::after {
            border: 2px solid #ffffff;
            border-left: 0;
            border-top: 0;
            height: 10px;
            left: 5px;
            top: 4px;
            width: 5px;
            transition: all 0.2s ease;
          }
        }
        
        &.is-checked {
          .el-checkbox__inner {
            background: rgba(255, 255, 255, 0.15);
            border-color: rgba(255, 255, 255, 0.8);
            box-shadow: 
              0 0 8px rgba(255, 255, 255, 0.3),
              inset 0 0 10px rgba(255, 255, 255, 0.1);
            
            &:hover {
              border-color: #ffffff;
              background: rgba(255, 255, 255, 0.2);
              box-shadow: 
                0 0 12px rgba(255, 255, 255, 0.4),
                inset 0 0 12px rgba(255, 255, 255, 0.15);
            }
          }
        }
        
        &.is-focus {
          .el-checkbox__inner {
            border-color: rgba(255, 255, 255, 0.6);
            box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
          }
        }
      }
      
      .el-checkbox__label {
        color: #888;
        font-size: 14px;
        padding-left: 8px;
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

