<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="760px"
    align-center
    :close-on-click-modal="false"
    @close="handleClose"
    custom-class="agreement-dialog-dark"
  >
    <div class="agreement-content">
      <div v-if="type === 'user'" class="content-section">
        <h3>用户协议</h3>
        <p>欢迎使用智慧生态旅游系统！在注册使用本系统前，请您仔细阅读并同意以下用户协议。</p>
        
        <h4>1. 服务条款</h4>
        <p>本协议是您与智慧生态旅游系统之间关于使用本系统服务所订立的协议。注册即表示您完全理解并同意遵守本协议所有条款。</p>
        
        <h4>2. 账户注册</h4>
        <p>2.1 您需要提供真实、准确、完整的个人信息进行注册。</p>
        <p>2.2 您有责任维护账户的安全，对账户下的所有活动负责。</p>
        <p>2.3 您不得将账户转让、出售或以其他方式让与他人使用。</p>
        
        <h4>3. 用户行为规范</h4>
        <p>3.1 您应当遵守国家法律法规，不得利用本系统从事违法违规活动。</p>
        <p>3.2 您不得发布虚假、误导性或不当内容。</p>
        <p>3.3 您不得干扰或破坏本系统的正常运行。</p>
        
        <h4>4. 隐私保护</h4>
        <p>我们将严格保护您的个人信息安全，详见《隐私政策》。</p>
        
        <h4>5. 知识产权</h4>
        <p>本系统所有内容的知识产权归本公司所有，未经授权不得复制、传播或使用。</p>
        
        <h4>6. 免责声明</h4>
        <p>6.1 本系统提供的信息仅供参考，不对信息的准确性、完整性承担责任。</p>
        <p>6.2 因不可抗力导致的服务中断或数据丢失，本系统不承担责任。</p>
        
        <h4>7. 协议修改</h4>
        <p>我们有权随时修改本协议，修改后的协议将在系统中公布。继续使用本系统即视为您接受修改后的协议。</p>
        
        <h4>8. 联系方式</h4>
<p>如有疑问，请联系我们：{{ contactEmail }}</p>
      </div>
      
      <div v-if="type === 'privacy'" class="content-section">
        <h3>隐私政策</h3>
        <p>智慧生态旅游系统非常重视您的隐私保护。本隐私政策说明了我们如何收集、使用和保护您的个人信息。</p>
        
        <h4>1. 信息收集</h4>
        <p>1.1 注册信息：用户名、邮箱、手机号等基本信息。</p>
        <p>1.2 使用信息：系统使用记录、浏览历史、操作日志等。</p>
        <p>1.3 设备信息：设备型号、操作系统、IP地址等。</p>
        
        <h4>2. 信息使用</h4>
        <p>2.1 为您提供旅游攻略、景点推荐等核心服务。</p>
        <p>2.2 优化系统功能，提升用户体验。</p>
        <p>2.3 发送重要通知和服务更新。</p>
        <p>2.4 进行数据分析和研究。</p>
        
        <h4>3. 信息保护</h4>
        <p>3.1 我们采用行业标准的安全措施保护您的信息安全。</p>
        <p>3.2 您的密码经过加密存储，我们无法直接查看。</p>
        <p>3.3 我们不会将您的个人信息出售给第三方。</p>
        
        <h4>4. 信息共享</h4>
        <p>4.1 仅在法律法规要求或获得您同意的情况下共享信息。</p>
        <p>4.2 与我们的合作伙伴共享必要的服务相关数据（经过脱敏处理）。</p>
        
        <h4>5. 您的权利</h4>
        <p>5.1 查看、修改或删除您的个人信息。</p>
        <p>5.2 随时注销账户。</p>
        <p>5.3 拒绝接收营销信息。</p>
        
        <h4>6. Cookie使用</h4>
        <p>我们使用Cookie来改善用户体验、分析流量、个性化内容。</p>
        
        <h4>7. 未成年人保护</h4>
        <p>我们特别重视未成年人的信息保护，未满18岁需在监护人同意下使用本系统。</p>
        
        <h4>8. 隐私政策更新</h4>
        <p>我们可能不定期更新本隐私政策，重大变更会通知您。</p>
        
        <h4>9. 联系我们</h4>
<p>如对本隐私政策有疑问，请联系：{{ contactEmail }}</p>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleConfirm">我已阅读</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, watch, nextTick, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useSystemStore } from '@/stores/system'

const props = defineProps<{
  modelValue: boolean
  type: 'user' | 'privacy'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirmed: []
}>()

const systemStore = useSystemStore()
const { contactEmail } = storeToRefs(systemStore)

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const dialogTitle = computed(() => {
  return props.type === 'user' ? '用户协议' : '隐私政策'
})

const handleClose = () => {
  visible.value = false
}

const handleConfirm = () => {
  emit('confirmed')
  handleClose()
}

// 监听对话框显示，动态添加样式类
watch(visible, async (newVal) => {
  if (newVal) {
    await nextTick()
    // 查找对话框元素并添加样式类
    setTimeout(() => {
      const dialogs = document.querySelectorAll('.el-dialog')
      dialogs.forEach((dialog: any) => {
        const title = dialog.querySelector('.el-dialog__title')
        if (title && (title.textContent === '用户协议' || title.textContent === '隐私政策')) {
          dialog.classList.add('agreement-dialog-dark')
          // 直接设置样式
          dialog.style.background = 'linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%)'
          dialog.style.border = '1px solid rgba(255, 255, 255, 0.1)'
          
          const header = dialog.querySelector('.el-dialog__header')
          if (header) {
            header.style.background = 'transparent'
            header.style.borderBottom = '1px solid rgba(255, 255, 255, 0.1)'
            const headerTitle = header.querySelector('.el-dialog__title')
            if (headerTitle) {
              headerTitle.style.color = '#ffffff'
            }
          }
          
          const body = dialog.querySelector('.el-dialog__body')
          if (body) {
            body.style.background = 'transparent'
            body.style.color = '#e0e0e0'
          }
          
          const footer = dialog.querySelector('.el-dialog__footer')
          if (footer) {
            footer.style.background = 'transparent'
            footer.style.borderTop = '1px solid rgba(255, 255, 255, 0.1)'
          }
        }
      })
    }, 100)
  }
})

onMounted(() => {
  systemStore.fetchConfig()
})
</script>

<style lang="scss" scoped>
.agreement-content {
  max-height: 70vh;
  overflow-y: auto;
  padding: 24px 0;
  scrollbar-width: none; // Firefox
  -ms-overflow-style: none; // IE/Edge
  
  &::-webkit-scrollbar {
    width: 0;
    height: 0;
  }
  
  .content-section {
    h3 {
      font-size: 20px;
      font-weight: 600;
      color: #ffffff;
      margin-bottom: 20px;
      text-align: center;
    }
    
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #ffffff;
      margin-top: 24px;
      margin-bottom: 12px;
      
      &:first-of-type {
        margin-top: 0;
      }
    }
    
    p {
      font-size: 14px;
      line-height: 1.8;
      color: #aaa;
      margin-bottom: 12px;
      text-align: justify;
    }
  }
}

.dialog-footer {
  text-align: center;
  
  .el-button {
    min-width: 120px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #ffffff;
    
    &:hover {
      background: rgba(255, 255, 255, 0.15);
    }
  }
}
</style>

<!-- 对话框全局样式（对话框渲染在body下，需要使用全局样式） -->
<style lang="scss">
// 使用多种选择器确保覆盖所有情况
.el-overlay .agreement-dialog-dark,
.el-dialog__wrapper .agreement-dialog-dark,
body .el-overlay .agreement-dialog-dark,
body .el-dialog__wrapper .agreement-dialog-dark,
.agreement-dialog-dark {
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  
  .el-dialog__header {
    background: transparent !important;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
    padding: 20px !important;
    margin: 0 !important;
    
    .el-dialog__title {
      color: #ffffff !important;
      font-weight: 600 !important;
    }
    
    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #888 !important;
        
        &:hover {
          color: #ffffff !important;
        }
      }
    }
  }

  .el-dialog__body {
    background: transparent !important;
    color: #e0e0e0 !important;
    padding: 20px !important;
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
    
    &::-webkit-scrollbar {
      width: 0 !important;
      height: 0 !important;
    }
  }

  .el-dialog__footer {
    background: transparent !important;
    border-top: 1px solid rgba(255, 255, 255, 0.1) !important;
    padding: 20px !important;
    margin: 0 !important;
    
    .el-button {
      background: rgba(255, 255, 255, 0.1) !important;
      border: 1px solid rgba(255, 255, 255, 0.2) !important;
      color: #ffffff !important;
      
      &:hover {
        background: rgba(255, 255, 255, 0.15) !important;
      }
    }
  }
}

// 如果 custom-class 不生效，通过内容匹配（使用 :has 选择器，现代浏览器支持）
body .el-overlay .el-dialog:has(.agreement-content),
body .el-dialog__wrapper .el-dialog:has(.agreement-content),
body .el-overlay .el-dialog:has([class*="agreement"]),
body .el-dialog__wrapper .el-dialog:has([class*="agreement"]) {
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  
  .el-dialog__header {
    background: transparent !important;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
    
    .el-dialog__title {
      color: #ffffff !important;
    }
    
    .el-dialog__headerbtn .el-dialog__close {
      color: #888 !important;
      
      &:hover {
        color: #ffffff !important;
      }
    }
  }
  
  .el-dialog__body {
    background: transparent !important;
    color: #e0e0e0 !important;
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
    
    &::-webkit-scrollbar {
      width: 0 !important;
      height: 0 !important;
    }
  }
  
  .el-dialog__footer {
    background: transparent !important;
    border-top: 1px solid rgba(255, 255, 255, 0.1) !important;
  }
}

</style>

