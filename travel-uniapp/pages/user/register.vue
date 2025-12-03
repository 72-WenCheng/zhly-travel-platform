<template>
  <view class="register-page">
    <view class="register-container">
      <view class="register-header">
        <text class="header-title">用户注册</text>
        <text class="header-subtitle">创建新账户</text>
      </view>
      
      <view class="register-form">
        <view class="form-item">
          <input 
            v-model="registerData.username" 
            class="form-input"
            placeholder="请输入用户名"
            placeholder-style="color: #999"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerData.email" 
            class="form-input"
            placeholder="请输入邮箱"
            placeholder-style="color: #999"
            type="email"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerData.password" 
            class="form-input"
            placeholder="请输入密码"
            placeholder-style="color: #999"
            password
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerData.confirmPassword" 
            class="form-input"
            placeholder="请确认密码"
            placeholder-style="color: #999"
            password
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerData.phone" 
            class="form-input"
            placeholder="请输入手机号"
            placeholder-style="color: #999"
            type="number"
          />
        </view>
        
        <view class="agreement-box">
          <checkbox-group @change="onAgreementChange">
            <label class="agreement-label">
              <checkbox value="agree" :checked="agreeTerms" />
              <text class="agreement-text">
                我已阅读并同意
                <text class="link-text" @click="showUserAgreement">《用户协议》</text>
                和
                <text class="link-text" @click="showPrivacyPolicy">《隐私政策》</text>
              </text>
            </label>
          </checkbox-group>
        </view>
        
        <button 
          class="register-btn" 
          @click="handleRegister"
          :disabled="loading"
        >
          {{ loading ? '注册中...' : '立即注册' }}
        </button>
        
        <view class="login-link">
          <text class="link-text">已有账号？</text>
          <text class="link-text login-action" @click="goToLogin">立即登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '../../utils/request.js'

// 表单数据
const registerData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const DEFAULT_CONTACT_EMAIL = '2401554207@qq.com'
const contactEmail = ref(DEFAULT_CONTACT_EMAIL)

// 同意条款
const agreeTerms = ref(false)

// 加载状态
const loading = ref(false)

// 页面加载
const loadSystemConfig = async () => {
  try {
    const response = await request.get('/system-config/system')
    if (response?.code === 200 && response.data) {
      const emailFromConfig = response.data.contactEmail || response.data.adminEmail
      if (emailFromConfig) {
        contactEmail.value = emailFromConfig
      }
    } else {
      console.warn('获取系统配置失败，使用默认邮箱', response?.message)
    }
  } catch (error) {
    console.error('获取系统配置异常，使用默认邮箱', error)
  }
}

onLoad(async () => {
  console.log('注册页面加载')
  await loadSystemConfig()
})

// 同意条款改变
const onAgreementChange = (e) => {
  agreeTerms.value = e.detail.value.includes('agree')
}

// 注册处理
const handleRegister = async () => {
  try {
    // 验证表单
    if (!registerData.value.username || registerData.value.username.trim() === '') {
      uni.showToast({
        title: '请输入用户名',
        icon: 'none'
      })
      return
    }
    
    if (!registerData.value.email || registerData.value.email.trim() === '') {
      uni.showToast({
        title: '请输入邮箱',
        icon: 'none'
      })
      return
    }
    
    // 验证邮箱格式
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
    if (!emailRegex.test(registerData.value.email)) {
      uni.showToast({
        title: '邮箱格式不正确',
        icon: 'none'
      })
      return
    }
    
    if (!registerData.value.password || registerData.value.password.trim() === '') {
      uni.showToast({
        title: '请输入密码',
        icon: 'none'
      })
      return
    }
    
    if (registerData.value.password.length < 6) {
      uni.showToast({
        title: '密码长度至少6位',
        icon: 'none'
      })
      return
    }
    
    if (registerData.value.password !== registerData.value.confirmPassword) {
      uni.showToast({
        title: '两次输入密码不一致',
        icon: 'none'
      })
      return
    }
    
    if (!registerData.value.phone || registerData.value.phone.trim() === '') {
      uni.showToast({
        title: '请输入手机号',
        icon: 'none'
      })
      return
    }
    
    // 验证手机号格式
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(registerData.value.phone)) {
      uni.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }
    
    if (!agreeTerms.value) {
      uni.showToast({
        title: '请先同意用户协议和隐私政策',
        icon: 'none'
      })
      return
    }
    
    console.log('开始注册，表单数据:', {
      username: registerData.value.username,
      email: registerData.value.email,
      phone: registerData.value.phone
    })
    
    loading.value = true
    
    // 调用注册API
    const response = await request.post('/auth/register', {
      username: registerData.value.username,
      email: registerData.value.email,
      password: registerData.value.password,
      phone: registerData.value.phone
    })
    
    console.log('注册响应:', response)
    
    if (response.code === 200) {
      uni.showToast({
        title: '注册成功！请登录',
        icon: 'success'
      })
      
      // 延迟跳转到登录页
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/user/login'
        })
      }, 1500)
    } else {
      console.error('注册失败，返回码:', response.code, '错误信息:', response.message)
      uni.showToast({
        title: response.message || '注册失败',
        icon: 'none',
        duration: 2000
      })
    }
  } catch (error) {
    console.error('注册错误:', error)
    console.error('错误response:', error.response)
    console.error('错误response.data:', error.response?.data)
    
    // 提取用户友好的错误信息
    let errorMessage = '注册失败，请重试'
    
    // 优先使用后端返回的错误信息
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
    uni.showToast({
      title: errorMessage,
      icon: 'none',
      duration: 2000
    })
  } finally {
    loading.value = false
  }
}

// 跳转到登录页
const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/user/login'
  })
}

// 显示用户协议
const showUserAgreement = () => {
  uni.showModal({
    title: '用户服务协议',
    content: `欢迎使用智慧生态旅游系统！

【服务内容】
本系统提供旅游攻略、景点推荐、文旅产品预订、文化体验、政府项目申请等一站式生态旅游服务。

【账户注册】
1. 您需提供真实、准确、完整的个人信息进行注册
2. 您有责任维护账户安全，对账户下的所有活动负责
3. 您不得将账户转让、出售或以其他方式让与他人使用

【用户行为规范】
1. 您应当遵守国家法律法规，不得利用本系统从事违法违规活动
2. 您不得发布虚假、误导性或不当内容
3. 您不得干扰或破坏本系统的正常运行

【服务使用】
1. 旅游攻略与内容：您可浏览、发布旅游攻略，内容应真实客观
2. 景点信息：信息仅供参考，建议出行前通过官方渠道核实
3. 文旅产品预订：订单提交后与产品提供方形成服务合同关系
4. 支付与退款：按照产品提供方的退改政策执行

【隐私保护】
我们将严格按照《个人信息保护法》等法律法规保护您的个人信息安全，详见《隐私政策》。

【知识产权】
本系统所有内容的知识产权归我们或相关权利人所有，未经授权不得复制、传播或使用。

【免责声明】
1. 本系统提供的信息仅供参考，不对信息的准确性、完整性作绝对保证
2. 因系统维护、升级、故障、不可抗力等导致的服务中断，我们不承担责任
3. 因您违反本协议导致的一切后果，由您自行承担

【协议修改】
我们有权随时修改本协议，修改后的协议将在系统中公布。继续使用即视为您接受修改后的协议。

【联系我们】
如有疑问，请联系：${contactEmail.value || DEFAULT_CONTACT_EMAIL}`,
    showCancel: false,
    confirmText: '我已阅读'
  })
}

// 显示隐私政策
const showPrivacyPolicy = () => {
  uni.showModal({
    title: '隐私政策',
    content: `智慧生态旅游系统深知个人信息对您的重要性，我们将严格按照《个人信息保护法》《网络安全法》《数据安全法》等相关法律法规的要求，保护您的个人信息安全。

【适用范围】
本隐私政策适用于智慧生态旅游系统提供的所有服务，包括旅游攻略、景点推荐、文旅产品预订、文化体验、政府项目申请、AI智能旅游规划等。

【信息收集】
1. 账户信息：用户名、手机号、邮箱、密码等注册信息
2. 个人资料：昵称、头像、性别、生日、地区、旅游偏好等
3. 使用信息：浏览记录、搜索记录、发布内容、订单信息、支付信息等
4. 位置信息：基于位置的服务（如附近景点推荐）所需的位置信息
5. 设备信息：设备型号、操作系统、设备标识符、IP地址等
6. 日志信息：访问时间、访问页面、请求信息等

【信息使用】
1. 向您提供服务：处理注册、订单、预订、支付等交易请求
2. 改善用户体验：分析用户行为、优化功能、个性化推荐
3. 安全保障：保障账户安全、防范风险、识别异常行为
4. 发送通知：服务通知、重要更新、安全提醒等
5. 数据分析：去标识化处理后的数据用于统计研究

【信息共享】
我们不会向第三方共享您的个人信息，除非：
1. 获得您的明确同意
2. 法律法规规定或司法机关、行政机关依法要求
3. 与授权合作伙伴共享（仅共享提供服务所必需的信息）
4. 紧急情况下为保护用户或公众的重大合法权益

【信息保护】
1. 技术措施：数据加密传输和存储、访问控制、安全审计等
2. 管理措施：员工安全培训、权限分级管理、定期安全评估等
3. 数据保存：仅在实现目的所必需的期间内保留您的个人信息
4. 跨境传输：个人信息存储在中华人民共和国境内

【您的权利】
1. 访问权：查看您的个人信息
2. 更正权：更正错误的个人信息
3. 删除权：在特定情形下删除个人信息
4. 撤回同意权：撤回此前给予的授权同意
5. 注销权：随时注销账户
6. 拒绝权：拒绝接收营销信息
7. 获取副本权：获取个人信息副本

【Cookie使用】
我们使用Cookie来记住您的身份、分析使用情况、提供个性化内容、保障服务安全。您可以通过浏览器设置管理Cookie。

【未成年人保护】
未满18岁的未成年人需在父母或监护人同意下使用本系统。我们非常重视未成年人信息保护，仅在法律法规允许、监护人明确同意或保护未成年人所必要的情况下使用相关信息。

【隐私政策更新】
我们可能不定期更新本隐私政策，重大变更会通过显著方式通知您。未经您明确同意，我们不会削减您按照本隐私政策所应享有的权利。

【联系我们】
如有疑问、意见或建议，或需要行使相关权利，请联系：
电子邮箱：${contactEmail.value || DEFAULT_CONTACT_EMAIL}
我们将在15个工作日内予以回复。`,
    showCancel: false,
    confirmText: '我已阅读'
  })
}
</script>

<style scoped>
.register-page {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.register-container {
  max-width: 680rpx;
  margin: 0 auto;
  padding-top: 80rpx;
}

.register-header {
  text-align: center;
  margin-bottom: 60rpx;
}

.header-title {
  font-size: 56rpx;
  font-weight: bold;
  color: white;
  display: block;
  margin-bottom: 24rpx;
}

.header-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
}

.register-form {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.form-item {
  margin-bottom: 32rpx;
}

.form-input {
  width: 100%;
  height: 88rpx;
  padding: 0 32rpx;
  background: rgba(255, 255, 255, 0.8);
  border: 2rpx solid transparent;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #333;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #667eea;
  background: white;
}

.agreement-box {
  margin: 40rpx 0 32rpx;
}

.agreement-label {
  display: flex;
  align-items: flex-start;
}

.agreement-text {
  font-size: 24rpx;
  color: #666;
  line-height: 1.6;
  margin-left: 16rpx;
}

.link-text {
  color: #667eea;
}

.register-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 16rpx;
  color: white;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.register-btn:active {
  transform: scale(0.98);
}

.register-btn[disabled] {
  opacity: 0.6;
}

.login-link {
  text-align: center;
  margin-top: 40rpx;
  font-size: 28rpx;
  color: #666;
}

.login-action {
  margin-left: 16rpx;
  font-weight: 500;
}
</style>
