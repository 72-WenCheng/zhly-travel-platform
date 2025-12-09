<template>
  <div class="login-page">
    <!-- 左侧数据仪表板 -->
    <div class="dashboard-section">
      <div class="dashboard-content">
        <!-- 顶部标签 -->
        <div class="dashboard-tag">ZHLY • SMART JOURNEY OS</div>
        
        <!-- 主标题 -->
        <h1 class="dashboard-title">Monochrome Mobility Intelligence</h1>
        
        <!-- 描述文字 -->
        <p class="dashboard-description">
          单色灰调情绪板，展示实时旅服态势与关键指标。纯粹、克制，带有安全感，让旅服数据在此沉淀并做出决策。
        </p>
        
        <!-- 关键指标卡片 -->
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-label">Active Travelers</div>
            <div class="metric-value">12.8K</div>
            <div class="metric-change positive">+18%</div>
          </div>
          
          <div class="metric-card">
            <div class="metric-label">AI 行程生成</div>
            <div class="metric-value">3.1s</div>
            <div class="metric-change negative">-0.8s</div>
          </div>
          
          <div class="metric-card">
            <div class="metric-label">实时告警</div>
            <div class="metric-value">0</div>
            <div class="metric-status">Secured</div>
          </div>
          
          <div class="metric-card">
            <div class="metric-label">系统可用率</div>
            <div class="metric-value">99.97%</div>
            <div class="metric-change positive">+0.2%</div>
          </div>
        </div>
        
        <!-- 实时旅服态势 -->
        <div class="status-card">
          <div class="status-header">
            <span class="status-title">实时旅服态势</span>
            <span class="live-badge">LIVE</span>
          </div>
          <div class="status-list">
            <div class="status-item">重庆 • 山城夜航308条直播路线</div>
            <div class="status-item">北京 • 胡同漫游 98条特色线路</div>
            <div class="status-item">成都 • 美食巡航142家签约商户</div>
          </div>
        </div>
        
        <!-- 系统运行状态 -->
        <div class="status-card">
          <div class="status-header">
            <span class="status-title">系统运行状态</span>
            <span class="online-badge">ONLINE</span>
          </div>
          <div class="status-list">
            <div class="status-item">API 响应时间: <span class="status-value">12ms</span></div>
            <div class="status-item">数据库连接: <span class="status-value">正常</span></div>
            <div class="status-item">缓存命中率: <span class="status-value">94.2%</span></div>
          </div>
        </div>
        
        <!-- 底部标签 -->
        <div class="dashboard-footer">
          <span class="footer-tag">Zero Downtime</span>
          <span class="footer-tag">AI Copilot</span>
          <span class="footer-tag">SOC2 Ready</span>
        </div>
      </div>
    </div>
    
    <!-- 右侧登录表单 -->
    <div class="login-section">
      <div class="login-content">
        <!-- 顶部图标和标题 -->
        <div class="login-header">
          <div class="logo-cube">
            <div class="cube-face front"></div>
            <div class="cube-face back"></div>
            <div class="cube-face right"></div>
            <div class="cube-face left"></div>
            <div class="cube-face top"></div>
            <div class="cube-face bottom"></div>
          </div>
          <div class="header-tag">UNIFIED CONTROL • MISSION SAFE</div>
          <h1 class="login-title">智慧生态旅游平台</h1>
        </div>
        
        <!-- 用户/管理端切换 -->
        <div class="login-type-selector">
          <div class="selector-wrapper">
            <div 
              class="selector-option" 
              :class="{ active: loginType === 'user' }"
              @click="loginType = 'user'; handleLoginTypeChange('user')"
            >
              <el-icon class="option-icon"><User /></el-icon>
              <span class="option-text">用户端</span>
            </div>
            <div 
              class="selector-option" 
              :class="{ active: loginType === 'admin' }"
              @click="loginType = 'admin'; handleLoginTypeChange('admin')"
            >
              <el-icon class="option-icon"><Setting /></el-icon>
              <span class="option-text">管理端</span>
            </div>
            <div class="selector-slider" :class="{ 'slide-right': loginType === 'admin' }"></div>
          </div>
        </div>
        
        <!-- 登录方式切换 -->
        <div class="login-method-tabs">
          <div 
            class="tab-item" 
            :class="{ active: loginMethod === 'account' }"
            @click="loginMethod = 'account'"
          >
            账号登录
          </div>
          <div 
            class="tab-item" 
            :class="{ active: loginMethod === 'phone' }"
            @click="loginMethod = 'phone'"
          >
            手机登录
          </div>
        </div>

        <!-- 账号密码登录表单 -->
        <el-form 
          v-if="loginMethod === 'account'"
          :model="loginForm" 
          :rules="loginRules" 
          ref="loginFormRef" 
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="用户名/邮箱"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="密码"
              prefix-icon="Lock"
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item class="login-options">
            <div class="options-container">
              <el-checkbox v-model="rememberMe">保持会话</el-checkbox>
              <el-link type="primary" class="forgot-password" @click="handleForgotPassword">忘记密码?</el-link>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleLogin" 
              :loading="loading"
              size="large"
              class="login-button"
            >
              {{ loading ? '登录中...' : '进入控制台' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 手机号验证码登录表单 -->
        <el-form 
          v-if="loginMethod === 'phone'"
          :model="phoneLoginForm" 
          :rules="phoneLoginRules" 
          ref="phoneLoginFormRef" 
          class="login-form phone-login-form"
        >
          <el-form-item prop="phone">
            <el-input 
              v-model="phoneLoginForm.phone" 
              placeholder="请输入手机号"
              :prefix-icon="Phone"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="captcha">
            <div class="captcha-input-wrapper">
              <el-input 
                v-model="phoneLoginForm.captcha" 
                placeholder="请输入验证码"
                :prefix-icon="Message"
                size="large"
                @keyup.enter="handlePhoneLogin"
              />
              <el-button 
                :disabled="countdown > 0"
                @click="handleSendCaptcha"
                class="captcha-button"
              >
                {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handlePhoneLogin" 
              :loading="loading"
              size="large"
              class="login-button"
            >
              {{ loading ? '登录中...' : '进入控制台' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <!-- 注册链接 -->
        <div class="register-link">
          <el-link type="primary" @click="handleRegister" class="register-link-text">
            尚未加入? 创建旅服身份
          </el-link>
        </div>
        
        <!-- 底部信息 -->
        <div class="login-footer">
          <div class="footer-info">
            <span class="info-label">安全等级:</span>
            <span class="info-value">Zero-Trust • MFA</span>
          </div>
          <div class="footer-info">
            <span class="info-label">系统状态:</span>
            <span class="info-value status-green">All Systems Green</span>
          </div>
          <div class="footer-links">
            <el-link type="primary" @click="handleTerms">服务条款</el-link>
            <el-link type="primary" @click="handlePrivacy">隐私声明</el-link>
            <el-link type="primary" @click="handleAboutUs">关于我们</el-link>
            <el-link type="primary" @click="handleContact">联系支持</el-link>
          </div>
        </div>
        
        <!-- 对话框组件 -->
        <AgreementDialog v-model="showUserAgreementDialog" type="user" />
        <AgreementDialog v-model="showPrivacyDialog" type="privacy" />
        
        <!-- 关于我们对话框 -->
        <el-dialog
          v-model="showAboutDialog"
          title="关于我们"
          width="500px"
          :close-on-click-modal="true"
        >
          <div class="about-content">
            <h3>{{ systemStore.userPlatformName || '智慧生态旅游系统' }}</h3>
            <p>{{ systemStore.footerDescription || '智慧生态旅游系统是一个集成了人工智能、大数据分析和生态旅游理念的综合性旅游服务平台。' }}</p>
            <h4>我们的使命</h4>
            <p>通过科技赋能，为游客提供个性化、智能化的旅游服务，同时促进生态旅游的可持续发展。</p>
            <h4>核心功能</h4>
            <ul>
              <li>智能旅游路线规划</li>
              <li>个性化景点推荐</li>
              <li>生态旅游文化体验</li>
              <li>实时旅游数据监控</li>
            </ul>
            <h4>技术特色</h4>
            <p>采用前沿的AI技术、大数据分析和云计算架构，为用户提供流畅、安全、智能的旅游服务体验。</p>
          </div>
        </el-dialog>
        
        <!-- 联系支持对话框 -->
        <el-dialog
          v-model="showContactDialog"
          title="联系支持"
          width="500px"
          :close-on-click-modal="true"
        >
          <div class="contact-content">
            <h3>获取帮助</h3>
            <p>我们随时为您提供支持服务，如有任何问题或建议，请通过以下方式联系我们：</p>
            <div class="contact-item">
              <strong>📧 邮箱支持：</strong>
              <a :href="`mailto:${systemStore.contactEmail}`">{{ systemStore.contactEmail }}</a>
            </div>
            <div class="contact-item">
              <strong>📞 客服热线：</strong>
              <span>{{ systemStore.contactPhone }}</span>
            </div>
            <div class="contact-item" v-if="systemStore.contactTime">
              <strong>🕐 服务时间：</strong>
              <span>{{ systemStore.contactTime }}</span>
            </div>
            <div class="contact-item">
              <strong>💬 在线客服：</strong>
              <span>登录后可在系统内联系在线客服</span>
            </div>
            <div class="contact-item">
              <strong>📍 公司地址：</strong>
              <span>广西壮族自治区南宁市</span>
            </div>
          </div>
        </el-dialog>
      </div>
      
      <!-- 背景装饰 -->
      <div class="login-bg-decoration">
        <div class="bg-circle circle-1"></div>
        <div class="bg-circle circle-2"></div>
        <div class="bg-circle circle-3"></div>
        <div class="bg-grid"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'
import { ElMessage } from 'element-plus'
import { User, Setting, Phone, Message } from '@element-plus/icons-vue'
import AgreementDialog from '@/components/AgreementDialog.vue'

const router = useRouter()
const systemStore = useSystemStore()

// 登录类型选择（用户端/管理端）
const loginType = ref('user')

// 登录方式选择（账号登录/手机登录）
const loginMethod = ref('account')

// 账号登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 手机号登录表单数据
const phoneLoginForm = reactive({
  phone: '',
  captcha: ''
})

// 保持会话
const rememberMe = ref(false)

// 验证码倒计时
const countdown = ref(0)
let countdownTimer: NodeJS.Timeout | null = null

// 页面加载时，从localStorage读取保存的账号和密码，并加载系统配置
onMounted(async () => {
  const savedUsername = localStorage.getItem('remembered_username')
  const savedPassword = localStorage.getItem('remembered_password')
  const savedRememberMe = localStorage.getItem('remember_me')
  
  if (savedUsername) {
    loginForm.username = savedUsername
  }
  if (savedPassword) {
    loginForm.password = savedPassword
  }
  if (savedRememberMe === 'true') {
    rememberMe.value = true
  }
  
  // 加载系统配置
  await systemStore.fetchConfig()
})

// 表单验证规则
const loginRules = {
  username: [],
  password: []
}

// 手机号登录验证规则
const phoneLoginRules = {
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
  ]
}

// 表单引用
const loginFormRef = ref()
const phoneLoginFormRef = ref()
const loading = ref(false)

// 对话框状态
const showUserAgreementDialog = ref(false)
const showPrivacyDialog = ref(false)
const showAboutDialog = ref(false)
const showContactDialog = ref(false)

// 登录类型切换
const handleLoginTypeChange = (value: string) => {
  loginForm.username = ''
  loginForm.password = ''
  phoneLoginForm.phone = ''
  phoneLoginForm.captcha = ''
  rememberMe.value = false
}

// 发送手机验证码
const handleSendCaptcha = async () => {
  if (!phoneLoginFormRef.value) return
  
  await phoneLoginFormRef.value.validateField('phone', async (valid: boolean) => {
    if (!valid) {
      return
    }
    
    try {
      loading.value = true
      const response = await authAPI.sendPhoneCaptcha({ phone: phoneLoginForm.phone })
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

// 手机号登录
const handlePhoneLogin = async () => {
  if (!phoneLoginFormRef.value) return
  
  await phoneLoginFormRef.value.validate(async (valid: boolean, fields: any) => {
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
    
    try {
      loading.value = true
      const response = await authAPI.loginByPhone({
        phone: phoneLoginForm.phone,
        captcha: phoneLoginForm.captcha,
        loginType: loginType.value
      })
      
      if (response.code === 200) {
        localStorage.setItem('travel_token', response.data.token)
        localStorage.setItem('travel_user_info', JSON.stringify(response.data.user))
        
        const userStore = useUserStore()
        userStore.setToken(response.data.token)
        userStore.setUserInfo(response.data.user)
        
        ElMessage.success('登录成功')
        
        const user = response.data.user
        const role = user.role
        
        if (role === 1) {
          router.push('/home/admin/dashboard')
        } else if (role === 2) {
          router.push('/home/user/dashboard')
        } else {
          router.push('/home/user/dashboard')
        }
      } else {
        ElMessage.error(response.message || '登录失败')
      }
    } catch (error: any) {
      let errorMessage = '登录失败，请重试'
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message
      } else if (error.message) {
        if (error.message.includes('未注册')) {
          errorMessage = '该手机号未注册，请先注册'
        } else if (error.message.includes('验证码')) {
          errorMessage = '验证码错误或已过期，请重新获取'
        } else {
          errorMessage = error.message
        }
      }
      
      ElMessage.error(errorMessage)
    } finally {
      loading.value = false
    }
  })
}

// 组件卸载时清理定时器
onMounted(() => {
  // ... existing code
})

watch(() => loginMethod.value, () => {
  // 切换登录方式时清空表单
  phoneLoginForm.phone = ''
  phoneLoginForm.captcha = ''
  loginForm.username = ''
  loginForm.password = ''
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  countdown.value = 0
})

// 登录处理
const handleLogin = async () => {
  try {
    loading.value = true
    
    const response = await authAPI.login({
      username: loginForm.username,
      password: loginForm.password,
      loginType: loginType.value
    })
    
    if (response.code === 200) {
      localStorage.setItem('travel_token', response.data.token)
      localStorage.setItem('travel_user_info', JSON.stringify(response.data.user))
      
      if (rememberMe.value) {
        localStorage.setItem('remembered_username', loginForm.username)
        localStorage.setItem('remembered_password', loginForm.password)
        localStorage.setItem('remember_me', 'true')
      } else {
        localStorage.removeItem('remembered_username')
        localStorage.removeItem('remembered_password')
        localStorage.removeItem('remember_me')
      }
      
      const userStore = useUserStore()
      userStore.setToken(response.data.token)
      userStore.setUserInfo(response.data.user)
      
      ElMessage.success('登录成功')
      
      const user = response.data.user
      const role = user.role
      
      if (role === 1) {
        router.push('/home/admin/dashboard')
      } else if (role === 2) {
        router.push('/home/user/dashboard')
      } else {
        router.push('/home/user/dashboard')
      }
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error: any) {
    let errorMessage = '登录失败，请重试'
    
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.response?.data?.data) {
      errorMessage = error.response.data.data
    } else if (error.message) {
      if (error.message.includes('selectOne') || 
          error.message.includes('TooManyResults') ||
          error.message.includes('Expected one result')) {
        errorMessage = '账号信息异常，请联系管理员'
      } else if (error.message.includes('Connection') || 
                 error.message.includes('timeout')) {
        errorMessage = '网络连接异常，请检查网络后重试'
      } else if (error.message.includes('500') || 
                 error.message.includes('Internal Server Error')) {
        errorMessage = '服务器异常，请稍后重试'
      } else {
        errorMessage = error.message
      }
    }
    
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

// 注册处理
const handleRegister = () => {
  router.push('/register')
}

// 忘记密码处理
const handleForgotPassword = () => {
  router.push('/forgot-password')
}

// 底部链接处理
const handleTerms = () => {
  showUserAgreementDialog.value = true
}

const handlePrivacy = () => {
  showPrivacyDialog.value = true
}

const handleAboutUs = () => {
  showAboutDialog.value = true
}

const handleContact = () => {
  showContactDialog.value = true
}

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})

// 监听登录方式切换，清空表单
watch(() => loginMethod.value, () => {
  phoneLoginForm.phone = ''
  phoneLoginForm.captcha = ''
  loginForm.username = ''
  loginForm.password = ''
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  countdown.value = 0
})
</script>

<style lang="scss" scoped>
.login-page {
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: #000000;
  position: relative;
}

// 左侧数据仪表板
.dashboard-section {
  flex: 0 0 50%;
  background: linear-gradient(to right, 
    #0a0a0a 0%, 
    #0a0a0a 70%, 
    rgba(10, 10, 10, 0.8) 85%,
    rgba(0, 0, 0, 0.4) 95%,
    transparent 100%);
  padding: 0 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
  position: relative;
  overflow: hidden;
  
  .dashboard-content {
    max-width: 600px;
    margin: 0 auto;
    position: relative;
    z-index: 2;
    overflow-y: auto;
    max-height: 100vh;
    padding: 40px 0;
    
    // 隐藏滚动条但保持滚动功能
    &::-webkit-scrollbar {
      width: 0;
      background: transparent;
    }
  }
  
  .dashboard-tag {
    font-size: 12px;
    color: #888;
    letter-spacing: 2px;
    margin-bottom: 20px;
    text-transform: uppercase;
  }
  
  .dashboard-title {
    font-size: 48px;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 20px;
    line-height: 1.2;
  }
  
  .dashboard-description {
    font-size: 14px;
    color: #aaa;
    line-height: 1.8;
    margin-bottom: 40px;
  }
  
  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 30px;
    
    .metric-card {
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      padding: 20px;
      
      .metric-label {
        font-size: 12px;
        color: #888;
        margin-bottom: 8px;
      }
      
      .metric-value {
        font-size: 32px;
        font-weight: 700;
        color: #ffffff;
        margin-bottom: 8px;
      }
      
      .metric-change {
        font-size: 12px;
        font-weight: 600;
        
        &.positive {
          color: #4ade80;
        }
        
        &.negative {
          color: #f87171;
        }
      }
      
      .metric-status {
        font-size: 12px;
        color: #4ade80;
      }
    }
  }
  
  .status-card {
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    
    .status-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      .status-title {
        font-size: 14px;
        font-weight: 600;
        color: #ffffff;
      }
      
      .live-badge {
        background: #ef4444;
        color: #ffffff;
        font-size: 10px;
        font-weight: 700;
        padding: 4px 8px;
        border-radius: 4px;
        text-transform: uppercase;
        letter-spacing: 1px;
      }
      
      .online-badge {
        background: #4ade80;
        color: #000000;
        font-size: 10px;
        font-weight: 700;
        padding: 4px 8px;
        border-radius: 4px;
        text-transform: uppercase;
        letter-spacing: 1px;
      }
    }
    
    .status-list {
      .status-item {
        font-size: 13px;
        color: #aaa;
        margin-bottom: 8px;
        line-height: 1.6;
        
        .status-value {
          color: #ffffff;
          font-weight: 600;
        }
      }
    }
  }
  
  .dashboard-footer {
    display: flex;
    gap: 12px;
    margin-top: auto;
    padding-top: 40px;
    
    .footer-tag {
      font-size: 11px;
      color: #666;
      padding: 6px 12px;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 6px;
    }
  }
}

// 右侧登录表单
.login-section {
  flex: 0 0 50%;
  background: #000000;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 40px;
  overflow: hidden;
  
  .login-content {
    width: 100%;
    max-width: 420px;
    position: relative;
    z-index: 2;
  }
  
  .login-header {
    text-align: center;
    margin-bottom: 40px;
    
    .logo-cube {
      width: 60px;
      height: 60px;
      margin: 0 auto 20px;
      position: relative;
      transform-style: preserve-3d;
      animation: rotateCube 10s infinite linear;
      
      .cube-face {
        position: absolute;
        width: 60px;
        height: 60px;
        border: 2px solid rgba(255, 255, 255, 0.3);
        background: rgba(255, 255, 255, 0.05);
      }
      
      .front { transform: rotateY(0deg) translateZ(30px); }
      .back { transform: rotateY(180deg) translateZ(30px); }
      .right { transform: rotateY(90deg) translateZ(30px); }
      .left { transform: rotateY(-90deg) translateZ(30px); }
      .top { transform: rotateX(90deg) translateZ(30px); }
      .bottom { transform: rotateX(-90deg) translateZ(30px); }
    }
    
    .header-tag {
      font-size: 11px;
      color: #888;
      letter-spacing: 2px;
      margin-bottom: 12px;
      text-transform: uppercase;
    }
    
    .login-title {
      font-size: 32px;
      font-weight: 700;
      color: #ffffff;
    }
  }
  
  // 登录方式切换标签页
  .login-method-tabs {
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

  .login-type-selector {
    margin-bottom: 40px;
    display: flex;
    justify-content: center;
    
    .selector-wrapper {
      position: relative;
      display: flex;
      background: rgba(0, 0, 0, 0.4);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 14px;
      padding: 4px;
      box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.3);
      gap: 4px;
      min-width: 280px;
      
      .selector-option {
        position: relative;
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 12px 20px;
        border-radius: 10px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        z-index: 2;
        color: #666;
        font-size: 14px;
        font-weight: 500;
        
        .option-icon {
          font-size: 18px;
          transition: transform 0.3s ease;
          
          :deep(svg) {
            width: 18px;
            height: 18px;
          }
        }
        
        .option-text {
          transition: color 0.3s ease;
        }
        
        &.active {
          color: #ffffff;
          
        .option-icon {
          transform: scale(1.15);
        }
        
        .option-text {
          font-weight: 600;
        }
        }
      }
      
      .selector-slider {
        position: absolute;
        top: 4px;
        left: 4px;
        width: calc(50% - 4px);
        height: calc(100% - 8px);
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 10px;
        transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        z-index: 1;
        
        &::before {
          display: none;
        }
        
        &.slide-right {
          transform: translateX(calc(100% + 4px));
        }
      }
    }
  }
  
  .login-form {
    // 手机号登录表单：隐藏错误提示
    &.phone-login-form {
      :deep(.el-form-item__error) {
        display: none !important;
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
        margin-top: 3px; // 稍微下移一点
        
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
    
    // 全局移除输入框焦点外边框 - 使用更具体的选择器覆盖所有可能的情况
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
    
    // 移除所有可能的阴影效果，包括 inset box-shadow
    :deep(.el-input__wrapper) {
      box-shadow: none !important;
      
      &::before,
      &::after {
        box-shadow: none !important;
        outline: none !important;
      }
    }
    
    .login-options {
      margin-bottom: 24px;
      
      .options-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 16px;
        width: 100%;
      }
    }
    
    :deep(.el-checkbox) {
      margin: 0;
      display: flex;
      align-items: center;
      
      .el-checkbox__input {
        display: flex;
        align-items: center;
        vertical-align: middle;
        
        .el-checkbox__inner {
          width: 18px;
          height: 18px;
          border: 2px solid rgba(255, 255, 255, 0.3);
          background: rgba(0, 0, 0, 0.2);
          border-radius: 4px;
          transition: all 0.3s ease;
          vertical-align: middle;
          
          &:hover {
            border-color: rgba(255, 255, 255, 0.5);
            background: rgba(255, 255, 255, 0.05);
          }
          
          &::after {
            border: 2px solid #ffffff;
            border-left: 0;
            border-top: 0;
            height: 10px;
              left: 50%;
              top: 50%;
            width: 5px;
              transition: all 0.2s ease;
              transform: translate(-50%, -50%) rotate(45deg) scale(0);
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
          
          &.is-checked .el-checkbox__inner::after {
            transform: translate(-50%, -50%) rotate(45deg) scale(1);
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
        line-height: 1.5;
        padding-left: 8px;
        display: flex;
        align-items: center;
        vertical-align: middle;
      }
    }
    
    :deep(.forgot-password) {
      color: #ffffff !important;
      font-size: 14px;
      text-decoration: none;
      line-height: 1.5;
      white-space: nowrap;
      
      &:hover {
        color: #e0e0e0 !important;
        text-decoration: underline;
      }
    }
    
    .login-button {
      width: 100%;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: #ffffff;
      font-weight: 600;
      height: 50px;
      border-radius: 12px;
      
      &:hover {
        background: rgba(255, 255, 255, 0.15);
      }
    }
  }
  
  .register-link {
    text-align: center;
    margin: 20px 0;
    
    .register-link-text {
      color: #ffffff;
      font-size: 14px;
    }
  }
  
  .login-footer {
    margin-top: 40px;
    padding-top: 30px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    
    .footer-info {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      font-size: 12px;
      
      .info-label {
        color: #888;
      }
      
      .info-value {
        color: #ffffff;
        
        &.status-green {
          color: #4ade80;
        }
      }
    }
    
    .footer-links {
      display: flex;
      gap: 16px;
      margin-top: 16px;
      flex-wrap: wrap;
      
      :deep(.el-link) {
        font-size: 11px;
        color: #666;
        
        &:hover {
          color: #ffffff;
        }
      }
    }
  }
  
  .login-bg-decoration {
    display: none;
  }
}

@keyframes rotateCube {
  0% { transform: rotateX(0deg) rotateY(0deg); }
  100% { transform: rotateX(360deg) rotateY(360deg); }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

// 响应式设计
@media (max-width: 1024px) {
  .login-page {
    flex-direction: column;
  }
  
  .dashboard-section,
  .login-section {
    flex: 0 0 50%;
  }
}

@media (max-width: 768px) {
  .dashboard-section {
    display: none;
  }
  
  .login-section {
    flex: 0 0 100%;
  }
}

// 对话框样式
.about-content,
.contact-content {
  color: #e0e0e0;
  line-height: 1.8;
  
  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 16px;
    text-align: center;
  }
  
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #ffffff;
    margin-top: 20px;
    margin-bottom: 12px;
    
    &:first-of-type {
      margin-top: 0;
    }
  }
  
  p {
    font-size: 14px;
    color: #aaa;
    margin-bottom: 12px;
    text-align: justify;
  }
  
  ul {
    margin: 12px 0;
    padding-left: 24px;
    
    li {
      font-size: 14px;
      color: #aaa;
      margin-bottom: 8px;
    }
  }
  
  .contact-item {
    margin-bottom: 16px;
    padding: 12px;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    
    strong {
      color: #ffffff;
      display: block;
      margin-bottom: 6px;
    }
    
    span, a {
      color: #aaa;
      font-size: 14px;
    }
    
    a {
      color: #ffffff;
      text-decoration: none;
      
      &:hover {
        color: #e0e0e0;
        text-shadow: none;
      }
    }
  }
}

// 对话框深色主题
:deep(.el-dialog) {
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  
  .el-dialog__header {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding: 20px;
    background: transparent !important;
    
    .el-dialog__title {
      color: #ffffff;
      font-weight: 600;
    }
    
    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #888;
        
        &:hover {
          color: #ffffff;
        }
      }
    }
  }
  
  .el-dialog__body {
    color: #e0e0e0;
    padding: 20px;
    background: transparent !important;
  }
  
  .el-dialog__footer {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding: 20px;
    background: transparent !important;
    
    .el-button {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #ffffff;
        
        &:hover {
          background: rgba(255, 255, 255, 0.15);
        }
    }
  }
}

// 全局移除所有输入框的焦点外边框 - 使用最高优先级的选择器
.login-page {
  // 覆盖所有可能的 Element Plus 输入框焦点样式
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
  :deep(.el-input.is-focus .el-input__wrapper:hover),
  :deep(.el-form-item .el-input__wrapper.is-focus),
  :deep(.el-form-item .el-input.is-focus .el-input__wrapper),
  :deep(.login-form .el-input__wrapper.is-focus),
  :deep(.login-form .el-input.is-focus .el-input__wrapper) {
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
  
  // 覆盖 CSS 变量（Element Plus 可能使用变量来控制焦点颜色）
  :deep(.el-input__wrapper.is-focus) {
    --el-input-focus-border-color: rgba(255, 255, 255, 0.3) !important;
    --el-color-primary: rgba(255, 255, 255, 0.3) !important;
  }
  
  // 最激进的覆盖 - 针对所有可能的组合
  :deep(*) {
    &.el-input__wrapper.is-focus,
    &.el-input.is-focus .el-input__wrapper {
      box-shadow: none !important;
      outline: none !important;
    }
  }
}
</style>





















