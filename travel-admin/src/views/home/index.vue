<template>
  <el-container class="home-layout">
    <!-- 左侧菜单栏（仅管理员显示） -->
    <el-aside v-if="isAdminRoute" :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="sidebar-header">
        <div class="collapse-btn-sidebar" @click="toggleCollapse">
          <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        router
        class="sidebar-menu"
        @select="handleAdminMenuSelect"
      >
        <el-menu-item index="/home/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>监测面板</span>
        </el-menu-item>
        
        <!-- 内容管理分组 -->
        <div class="menu-group-title" v-if="!isCollapse">内容管理</div>
        <el-menu-item index="/home/admin/plans">
          <el-icon><Document /></el-icon>
          <span>攻略管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/attractions">
          <el-icon><Location /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/comment">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        
        <!-- 文旅管理分组 -->
        <div class="menu-group-title" v-if="!isCollapse">文旅管理</div>
        <el-menu-item index="/home/admin/culture/services">
          <el-icon><House /></el-icon>
          <span>农家乐管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/homestays">
          <el-icon><HomeFilled /></el-icon>
          <span>特色民宿管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/products">
          <el-icon><ShoppingBag /></el-icon>
          <span>农特产品管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/projects">
          <el-icon><Document /></el-icon>
          <span>政策对接管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/experiences">
          <el-icon><Tickets /></el-icon>
          <span>文化体验管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/souvenirs">
          <el-icon><ShoppingBag /></el-icon>
          <span>特色周边管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/orders">
          <el-icon><ShoppingCart /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/appointments">
          <el-icon><Tickets /></el-icon>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/bookings">
          <el-icon><Calendar /></el-icon>
          <span>预订管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/applications">
          <el-icon><Files /></el-icon>
          <span>申请管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/culture/coupons">
          <el-icon><Tickets /></el-icon>
          <span>优惠券管理</span>
        </el-menu-item>
        
        <!-- 运营管理分组 -->
        <div class="menu-group-title" v-if="!isCollapse">运营管理</div>
        <el-menu-item index="/home/admin/banner">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/announcement">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/ai">
          <el-icon><MagicStick /></el-icon>
          <span>AI管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/report">
          <el-icon><Warning /></el-icon>
          <span>举报审核</span>
        </el-menu-item>
        
        <!-- 用户管理分组 -->
        <div class="menu-group-title" v-if="!isCollapse">用户管理</div>
        <el-menu-item index="/home/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/points">
          <el-icon><Coin /></el-icon>
          <span>积分管理</span>
        </el-menu-item>
        <el-menu-item index="/home/admin/browse-history">
          <el-icon><Clock /></el-icon>
          <span>浏览历史</span>
        </el-menu-item>
        
        <!-- 其他管理分组 -->
        <div class="menu-group-title" v-if="!isCollapse">其他管理</div>
        <el-menu-item index="/home/admin/system">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主要内容 -->
    <el-container class="main-container-wrapper">
      <!-- 顶部导航 -->
      <el-header class="header">
        <!-- 左侧 -->
        <div class="header-left">
          <!-- 系统标题 -->
          <div class="system-title" :class="{ 'admin-title': isAdmin, 'user-title': !isAdmin }">
            <el-icon class="title-icon"><Location /></el-icon>
            <div class="title-with-badge">
              <div class="title-wrapper">
                <span class="title-text">{{ isAdmin ? adminPlatformName : userPlatformName }}</span>
                <span class="title-english">ZHIHUISHENGTAILUYOU</span>
              </div>
              <span class="platform-badge" :class="{ 'admin-platform': isAdmin, 'user-platform': !isAdmin }">
                {{ isAdmin ? '管理平台' : '用户平台' }}
              </span>
            </div>
          </div>
          <div v-if="!isAdmin" class="header-nav">
            <div
              v-for="menu in userNavMenus"
              :key="menu.path"
              class="nav-item"
              :class="{ active: isNavActive(menu.path) }"
              @click="handleNavClick(menu.path)"
            >
              <el-icon class="nav-icon">
                <component :is="menu.icon" />
              </el-icon>
              <span>{{ menu.label }}</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧 -->
        <div class="header-right">
          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand" trigger="hover" class="user-dropdown-wrapper">
            <div class="user-dropdown-trigger">
              <!-- 装饰性背景 -->
              <div class="trigger-background">
                <div class="gradient-circle circle-1"></div>
                <div class="gradient-circle circle-2"></div>
              </div>
              
              <!-- 用户信息 -->
              <div class="user-info-content">
                <div class="avatar-wrapper">
                  <el-avatar :size="42" :src="userAvatar" class="user-avatar">
                    {{ userName?.charAt(0) }}
                  </el-avatar>
                  <div class="avatar-status" :class="{ 'status-active': isUserActive, 'status-inactive': !isUserActive }"></div>
                </div>
                <div class="user-text">
                  <span class="user-name">{{ userName }}</span>
                  <span class="user-role">{{ isAdmin ? '管理员' : '普通用户' }}</span>
                </div>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <el-dropdown-item divided command="user-profile" v-if="!isAdmin && !isAdminRoute">
                  <el-icon><User /></el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="culture-center" v-if="!isAdmin && !isAdminRoute">
                  <el-icon><Shop /></el-icon>
                  <span>我的文旅</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="user-addresses" v-if="!isAdmin && !isAdminRoute">
                  <el-icon><Location /></el-icon>
                  <span>我的地址</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="change-password">
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="deactivate" v-if="!isAdmin && !isAdminRoute">
                  <el-icon><CircleClose /></el-icon>
                  <span>注销账号</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <!-- 修改密码对话框 -->
  <el-dialog
    v-model="changePasswordDialogVisible"
    title="修改密码"
    width="520px"
    :close-on-click-modal="false"
    class="change-password-dialog"
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      label-position="left"
      class="password-form-minimal"
      :hide-required-asterisk="false"
    >
      <el-form-item label="原密码" required>
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          placeholder="请输入原密码"
          show-password
          clearable
          size="large"
          @blur="validateOldPassword"
        />
      </el-form-item>
      <el-form-item label="新密码" required>
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          :placeholder="passwordPlaceholder"
          show-password
          clearable
          size="large"
          @blur="validateNewPasswordInput"
        />
      </el-form-item>
      <el-form-item label="确认新密码" required>
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
          clearable
          size="large"
          @blur="validateConfirmPasswordInput"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="password-dialog-footer-minimal">
        <el-button @click="changePasswordDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleChangePassword"
          :loading="changePasswordLoading"
        >
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 注销账号 -->
  <el-dialog
    v-model="deactivateDialogVisible"
    title="注销账号"
    width="640px"
    :close-on-click-modal="false"
    class="deactivate-dialog"
  >
    <div class="deactivate-warning">
      <p class="deactivate-warning-title">注销前请先了解：</p>
      <ul class="deactivate-warning-list">
        <li>提交注销后会立即退出登录，并进入 7 天冷静期，期间账号数据会暂存，登录一次即可取消注销。</li>
        <li>若 7 天内没有再次登录，系统会在到期时彻底删除账号及其数据。</li>
      </ul>
    </div>
    <el-form
      ref="deactivateFormRef"
      :model="deactivateForm"
      label-position="top"
      :hide-required-asterisk="true"
    >
      <el-form-item label="确认关键词">
        <el-input
          v-model="deactivateForm.confirmText"
          placeholder='请输入"注销"以确认操作'
          size="large"
          @blur="validateDeactivateConfirmInput"
        />
      </el-form-item>
      <el-form-item label="注销原因（选填）">
        <el-input
          v-model="deactivateForm.reason"
          type="textarea"
          :rows="4"
          placeholder="可简单描述注销原因，帮助我们优化体验"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="deactivate-dialog-footer">
        <el-button @click="deactivateDialogVisible = false">取消</el-button>
        <el-button :loading="deactivateLoading" @click="handleDeactivateAccount">
          确认注销
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'
import { getCurrentUserInfo } from '@/utils/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { 
  Location, SwitchButton, ArrowDown, Fold, Expand,
  Odometer, User, Document, MagicStick, Shop,
  ChatDotRound, Picture, Star, TrendCharts,
  Coin, DataAnalysis, Warning, Setting,
  TrophyBase, Lock, CircleClose,
  Clock, Bell, Message, House, HomeFilled,
  ShoppingBag, Tickets, Files, Calendar, ShoppingCart
} from '@element-plus/icons-vue'
import { changePassword, deactivateAccount } from '@/api/user'
import { getPublicSecurityConfig } from '@/api/systemConfig'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const systemStore = useSystemStore()
const { adminPlatformName, userPlatformName } = storeToRefs(systemStore)

// 侧边栏折叠状态
const isCollapse = ref(false)

// 响应式数据
const userName = ref('')
const userAvatar = ref('')
const systemTitle = ref('')

// 用户活跃状态监控
const isUserActive = ref(true) // 默认活跃（绿色）
let inactivityTimer: number | null = null
const INACTIVITY_TIMEOUT = 5 * 60 * 1000 // 5分钟 = 300000毫秒

// 重置不活跃定时器
const resetInactivityTimer = () => {
  // 清除现有定时器
  if (inactivityTimer) {
    clearTimeout(inactivityTimer)
    inactivityTimer = null
  }
  
  // 设置为活跃状态
  isUserActive.value = true
  
  // 设置新的定时器，5分钟后设置为不活跃
  inactivityTimer = setTimeout(() => {
    isUserActive.value = false
    inactivityTimer = null
  }, INACTIVITY_TIMEOUT)
}

// 用户活动事件处理函数
const handleUserActivity = () => {
  resetInactivityTimer()
}

// 修改密码相关
const changePasswordDialogVisible = ref(false)
const changePasswordLoading = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordMinLength = ref(6)
const passwordPlaceholder = computed(() => `请输入新密码（至少${passwordMinLength.value}位）`)

// 密码验证函数（使用 ElMessage 弹出提示）
const validateOldPassword = () => {
  if (!passwordForm.value.oldPassword) {
    ElMessage.warning('请输入原密码')
    return false
  }
  return true
}

const validateNewPasswordInput = () => {
  if (!passwordForm.value.newPassword) {
    ElMessage.warning('请输入新密码')
    return false
  } else if (passwordForm.value.newPassword.length < passwordMinLength.value) {
    ElMessage.warning(`密码长度至少${passwordMinLength.value}位`)
    return false
  }
  return true
}

const validateConfirmPasswordInput = () => {
  if (!passwordForm.value.confirmPassword) {
    ElMessage.warning('请再次输入新密码')
    return false
  } else if (passwordForm.value.confirmPassword !== passwordForm.value.newPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return false
  }
  return true
}

// 验证所有字段
const validateAllFields = (): boolean => {
  if (!validateOldPassword()) return false
  if (!validateNewPasswordInput()) return false
  if (!validateConfirmPasswordInput()) return false
  return true
}

// 注销账号相关
const deactivateDialogVisible = ref(false)
const deactivateLoading = ref(false)
const deactivateFormRef = ref<FormInstance>()
const deactivateForm = ref({
  confirmText: '',
  reason: ''
})

// 注销账号验证函数（使用 ElMessage 弹出提示）
const validateDeactivateConfirmInput = (): boolean => {
  if (!deactivateForm.value.confirmText) {
    ElMessage.warning('请输入确认关键词')
    return false
  } else if (deactivateForm.value.confirmText !== '注销') {
    ElMessage.warning('请输入"注销"两个字以确认')
    return false
  }
  return true
}

const userNavMenus = [
  { label: '首页', path: '/home/user/dashboard', icon: Odometer },
  { label: '攻略社区', path: '/home/user/community', icon: ChatDotRound },
  { label: '私信', path: '/home/user/messages', icon: Message },
  { label: '我的攻略', path: '/home/user/plans', icon: Document },
  { label: '景点社区', path: '/home/user/recommendations', icon: TrendCharts },
  { label: '文旅体验', path: '/home/user/culture', icon: Shop },
  { label: 'AI助手', path: '/home/user/ai', icon: MagicStick },
  { label: '我的收藏', path: '/home/user/collect', icon: Star },
  { label: '用户画像', path: '/home/user/portrait', icon: DataAnalysis },
  { label: '升级指南', path: '/home/user/level-guide', icon: TrophyBase }
]

type MenuUsageMeta = {
  actionCode: string
  actionName: string
  actionDesc: string
  actionIcon: string
  actionPath: string
}

const adminMenuActionMap: Record<string, MenuUsageMeta> = {
  '/home/admin/dashboard': {
    actionCode: 'adminDashboard',
    actionName: '监测面板',
    actionDesc: '查看运营监测总览',
    actionIcon: 'Odometer',
    actionPath: '/home/admin/dashboard'
  },
  '/home/admin/plans': {
    actionCode: 'adminPlans',
    actionName: '攻略管理',
    actionDesc: '管理攻略内容',
    actionIcon: 'Document',
    actionPath: '/home/admin/plans'
  },
  '/home/admin/attractions': {
    actionCode: 'adminAttractions',
    actionName: '景点管理',
    actionDesc: '维护景点资源',
    actionIcon: 'Location',
    actionPath: '/home/admin/attractions'
  },
  '/home/admin/comment': {
    actionCode: 'adminComments',
    actionName: '评论管理',
    actionDesc: '审核互动内容',
    actionIcon: 'ChatDotRound',
    actionPath: '/home/admin/comment'
  },
  '/home/admin/culture/services': {
    actionCode: 'adminCultureServices',
    actionName: '农家乐管理',
    actionDesc: '管理农家乐服务',
    actionIcon: 'House',
    actionPath: '/home/admin/culture/services'
  },
  '/home/admin/culture/homestays': {
    actionCode: 'adminCultureHomestays',
    actionName: '特色民宿管理',
    actionDesc: '管理特色民宿',
    actionIcon: 'HomeFilled',
    actionPath: '/home/admin/culture/homestays'
  },
  '/home/admin/culture/products': {
    actionCode: 'adminCultureProducts',
    actionName: '农特产品管理',
    actionDesc: '管理农特产品',
    actionIcon: 'ShoppingBag',
    actionPath: '/home/admin/culture/products'
  },
  '/home/admin/culture/projects': {
    actionCode: 'adminCultureProjects',
    actionName: '政策对接管理',
    actionDesc: '管理政策对接',
    actionIcon: 'Document',
    actionPath: '/home/admin/culture/projects'
  },
  '/home/admin/culture/experiences': {
    actionCode: 'adminCultureExperiences',
    actionName: '文化体验管理',
    actionDesc: '管理文化体验',
    actionIcon: 'Star',
    actionPath: '/home/admin/culture/experiences'
  },
  '/home/admin/culture/souvenirs': {
    actionCode: 'adminCultureSouvenirs',
    actionName: '特色周边管理',
    actionDesc: '管理特色周边产品',
    actionIcon: 'ShoppingBag',
    actionPath: '/home/admin/culture/souvenirs'
  },
  '/home/admin/culture/orders': {
    actionCode: 'adminCultureOrders',
    actionName: '订单管理',
    actionDesc: '管理文旅订单',
    actionIcon: 'ShoppingCart',
    actionPath: '/home/admin/culture/orders'
  },
  '/home/admin/culture/bookings': {
    actionCode: 'adminCultureBookings',
    actionName: '预订管理',
    actionDesc: '管理预订信息',
    actionIcon: 'Calendar',
    actionPath: '/home/admin/culture/bookings'
  },
  '/home/admin/culture/applications': {
    actionCode: 'adminCultureApplications',
    actionName: '申请管理',
    actionDesc: '管理项目申请',
    actionIcon: 'Files',
    actionPath: '/home/admin/culture/applications'
  },
  '/home/admin/culture/coupons': {
    actionCode: 'adminCultureCoupons',
    actionName: '优惠券管理',
    actionDesc: '管理优惠券',
    actionIcon: 'Tickets',
    actionPath: '/home/admin/culture/coupons'
  },
  '/home/admin/banner': {
    actionCode: 'adminBanner',
    actionName: '轮播图管理',
    actionDesc: '配置首页轮播',
    actionIcon: 'Picture',
    actionPath: '/home/admin/banner'
  },
  '/home/admin/users': {
    actionCode: 'adminUsers',
    actionName: '用户管理',
    actionDesc: '维护用户信息',
    actionIcon: 'User',
    actionPath: '/home/admin/users'
  },
  '/home/admin/points': {
    actionCode: 'adminPoints',
    actionName: '积分管理',
    actionDesc: '调整积分等级',
    actionIcon: 'Coin',
    actionPath: '/home/admin/points'
  },
  '/home/admin/browse-history': {
    actionCode: 'adminBrowseHistory',
    actionName: '浏览历史',
    actionDesc: '查看访问记录',
    actionIcon: 'Clock',
    actionPath: '/home/admin/browse-history'
  },
  '/home/admin/announcement': {
    actionCode: 'adminAnnouncement',
    actionName: '公告管理',
    actionDesc: '发布系统公告',
    actionIcon: 'Bell',
    actionPath: '/home/admin/announcement'
  },
  '/home/admin/ai': {
    actionCode: 'adminAi',
    actionName: 'AI管理',
    actionDesc: '维护AI功能',
    actionIcon: 'MagicStick',
    actionPath: '/home/admin/ai'
  },
  '/home/admin/report': {
    actionCode: 'adminReport',
    actionName: '举报审核',
    actionDesc: '处理举报记录',
    actionIcon: 'Warning',
    actionPath: '/home/admin/report'
  },
  '/home/admin/system': {
    actionCode: 'adminSystem',
    actionName: '系统设置',
    actionDesc: '配置系统参数',
    actionIcon: 'Setting',
    actionPath: '/home/admin/system'
  }
}

// 计算属性
const isAdmin = computed(() => {
  // 优先从 userStore 获取
  if (userStore.userInfo && userStore.userInfo.role === 1) {
    return true
  }
  
  // 其次从 getCurrentUserInfo 获取
  const currentUserInfo = getCurrentUserInfo()
  if (currentUserInfo && currentUserInfo.role === 1) {
    return true
  }
  
  // 最后从 sessionStorage/localStorage 获取
  let savedUserInfo = sessionStorage.getItem('travel_user_info')
  if (!savedUserInfo) {
    savedUserInfo = localStorage.getItem('travel_user_info')
  }
  
  if (savedUserInfo) {
    try {
      const userInfo = JSON.parse(savedUserInfo)
      // 使用 role 字段判断：1=管理员, 2=普通用户
      return userInfo.role === 1
    } catch (error) {
      return false
    }
  }
  return false
})


// 判断当前路由是否为管理路由
const isAdminRoute = computed(() => {
  return route.path.startsWith('/home/admin')
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

const handleNavClick = (path: string) => {
  if (route.path !== path) {
    router.push(path)
  }
}

const isNavActive = (path: string) => {
  return route.path.startsWith(path)
}

// 方法

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const recordMenuUsage = async (path: string) => {
  if (!isAdmin.value) return
  const meta = adminMenuActionMap[path]
  if (!meta) return
  try {
    await request.post('/admin/usage/common-actions', {
      actionCode: meta.actionCode,
      actionName: meta.actionName,
      actionDesc: meta.actionDesc,
      actionPath: meta.actionPath,
      actionIcon: meta.actionIcon
    })
  } catch (error) {
    console.error('记录常用操作失败:', error)
  }
}

const handleAdminMenuSelect = (path: string) => {
  recordMenuUsage(path)
}

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'user-profile') {
    // 跳转到用户资料页面
    viewUserProfile()
  } else if (command === 'level-guide') {
    router.push('/home/user/level-guide')
  } else if (command === 'change-password') {
    changePasswordDialogVisible.value = true
    // 重置表单
    if (passwordFormRef.value) {
      passwordFormRef.value.resetFields()
    }
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } else if (command === 'deactivate') {
    openDeactivateDialog()
  } else if (command === 'culture-center') {
    router.push('/home/user/culture/center')
  } else if (command === 'user-addresses') {
    router.push('/home/user/addresses')
  }
}

// 处理修改密码
const handleChangePassword = async () => {
  // 使用自定义验证函数
  if (!validateAllFields()) {
    return
  }
  
  changePasswordLoading.value = true
  try {
    const response = await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    if (response.code === 200) {
      changePasswordDialogVisible.value = false
      ElMessage.success('密码修改成功，请重新登录')
      // 直接退出登录
      handleLogout()
    }
  } catch (error: any) {
    // 错误提示已在响应拦截器中显示，这里只记录日志
    console.error('修改密码失败:', error)
  } finally {
    changePasswordLoading.value = false
  }
}

// 查看用户详情
const viewUserProfile = () => {
  try {
    // 优先从 userStore 获取当前标签页的用户信息
    const userInfo = userStore.userInfo || getCurrentUserInfo()
    if (userInfo) {
      const userId = userInfo.id || userInfo.userId
      if (userId) {
        router.push(`/home/user/profile/${userId}`)
      } else {
        ElMessage.warning('无法获取用户ID')
      }
    } else {
      ElMessage.warning('请先登录')
    }
  } catch (error) {
    console.error('查看用户详情失败:', error)
    ElMessage.error('查看用户详情失败')
  }
}


const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用退出登录（会先调用后端API，再清除本地存储）
    await userStore.logout()
    
    // 使用 replace 而不是 push，避免历史记录问题
    // 添加延迟确保状态已清除
    await nextTick()
    
    // 先尝试路由跳转
    try {
      await router.replace('/')
      // 等待路由组件加载完成
      await new Promise(resolve => setTimeout(resolve, 200))
      // 检查登录页面是否已加载（通过检查登录页面的关键元素）
      const loginPage = document.querySelector('.login-page')
      const loginForm = document.querySelector('.login-form')
      if (!loginPage && !loginForm) {
        // 如果页面没有加载，强制刷新
        window.location.href = '/'
      }
    } catch (error) {
      // 如果路由跳转失败，直接跳转
      window.location.href = '/'
    }
  } catch (error) {
    // 用户取消退出
  }
}

const openDeactivateDialog = () => {
  deactivateDialogVisible.value = true
  deactivateForm.value = {
    confirmText: '',
    reason: ''
  }
  nextTick(() => {
    deactivateFormRef.value?.clearValidate?.()
  })
}

const handleDeactivateAccount = async () => {
  // 使用自定义验证函数
  if (!validateDeactivateConfirmInput()) {
    return
  }
  
  deactivateLoading.value = true
  try {
    const response = await deactivateAccount({
      reason: deactivateForm.value.reason?.trim() || undefined
    })
    if (response.code === 200) {
      ElMessage.success('注销申请已提交，7天内登录可取消')
      deactivateDialogVisible.value = false
      await userStore.logout()
      router.push('/')
    }
  } catch (error) {
    console.error('注销账号失败:', error)
  } finally {
    deactivateLoading.value = false
  }
}

onMounted(() => {
  console.log('Home页面初始化')
  console.log('当前路径:', route.path)

  getPublicSecurityConfig()
    .then((res) => {
      if (res.code === 200 && res.data?.passwordMinLength) {
        const value = Number(res.data.passwordMinLength)
        if (!Number.isNaN(value)) {
          passwordMinLength.value = value
        }
      }
    })
    .catch((error) => {
      console.error('加载安全配置失败:', error)
    })
  
  // 确保用户信息已初始化（刷新时恢复当前标签页的用户身份）
  if (!userStore.isLoggedIn || !userStore.token) {
    userStore.initUser()
  }
  
  // 优先从sessionStorage获取当前标签页的用户信息
  let savedUserInfo = sessionStorage.getItem('travel_user_info')
  if (!savedUserInfo) {
    savedUserInfo = localStorage.getItem('travel_user_info')
  }
  console.log('当前标签页的用户信息:', savedUserInfo)
  
  // 初始化用户活跃状态监控
  resetInactivityTimer()
  
  // 监听用户活动事件
  const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']
  events.forEach(event => {
    document.addEventListener(event, handleUserActivity, { passive: true })
  })
  
  // 如果userStore中有用户信息，优先使用
  if (userStore.userInfo) {
    const userInfo = userStore.userInfo
    userName.value = userInfo.nickname || userInfo.username
    userAvatar.value = userInfo.avatar || ''
    
    // 根据用户角色设置系统标题（重定向逻辑已在路由守卫中处理，避免重复重定向）
    if (userInfo.role === 1) {
      systemTitle.value = '管理后台'
    } else if (userInfo.role === 2) {
      systemTitle.value = '用户中心'
    }
  } else if (savedUserInfo) {
    // 如果userStore没有，但从存储中有，解析并设置
    try {
      const userInfo = JSON.parse(savedUserInfo)
      userName.value = userInfo.nickname || userInfo.username
      userAvatar.value = userInfo.avatar || ''
      
      if (userInfo.role === 1) {
        systemTitle.value = '管理后台'
      } else if (userInfo.role === 2) {
        systemTitle.value = '用户中心'
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  } else {
    console.log('没有用户信息，跳转到登录页')
    router.push('/')
  }
})

// 清理用户活跃状态监控
onUnmounted(() => {
  // 清除定时器
  if (inactivityTimer) {
    clearTimeout(inactivityTimer)
    inactivityTimer = null
  }
  
  // 移除事件监听器
  const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']
  events.forEach(event => {
    document.removeEventListener(event, handleUserActivity)
  })
})

// 路由变化时，将当前布局内的滚动容器滚动到顶部
watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    const main = document.querySelector('.home-layout .main-content') as HTMLElement | null
    if (main) {
      main.scrollTo({ top: 0, left: 0, behavior: 'auto' })
    }
    // 兜底：同时把窗口本身也滚动到顶部
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' })
  }
)
</script>

<style lang="scss" scoped>
.home-layout {
  height: 100vh;
  background: #f5f7fa;
  overflow: hidden;
}

.main-container-wrapper {
  transition: margin-left 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  will-change: margin-left;
}

// 侧边栏样式（仅管理员显示）
.sidebar {
  background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
  transition: width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  will-change: width;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100vh;
  
  .sidebar-header {
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    background: rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    flex-shrink: 0;
    
    .collapse-btn-sidebar {
      cursor: pointer;
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      color: rgba(255, 255, 255, 0.9);
      background: rgba(255, 255, 255, 0.1);
      transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
      flex-shrink: 0;
      border: 1px solid rgba(255, 255, 255, 0.15);
      will-change: transform, background-color;
      
      .el-icon {
        font-size: 20px;
        transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
        display: inline-block;
      }
      
      &:hover {
        background: rgba(255, 255, 255, 0.2);
        color: #fff;
        transform: scale(1.1);
        border-color: rgba(255, 255, 255, 0.3);
      }
      
      &:active {
        transform: scale(0.95);
      }
    }
  }
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: 10px 0;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  
  // 隐藏滚动条但保持滚动功能
  scrollbar-width: none; // Firefox
  -ms-overflow-style: none; // IE 和 Edge
  
  &::-webkit-scrollbar {
    display: none; // Chrome, Safari, Opera
  }
  
  .menu-group-title {
    color: rgba(255, 255, 255, 0.6);
    font-size: 12px;
    padding: 12px 15px;
    margin-top: 8px;
    margin-left: 8px;
    margin-right: 8px;
    font-weight: 600;
    letter-spacing: 0.5px;
    text-transform: uppercase;
    transition: opacity 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    white-space: nowrap;
    overflow: hidden;
    
    &:first-child {
      margin-top: 0;
    }
  }
  
  :deep(.el-menu-item) {
    color: rgba(255, 255, 255, 0.8);
    transition: background-color 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                color 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                transform 0.2s cubic-bezier(0.4, 0, 0.2, 1),
                padding 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    margin: 2px 10px;
    border-radius: 8px;
    height: 46px;
    line-height: 46px;
    font-weight: 500;
    font-size: 14px;
    will-change: background-color, transform;
    white-space: nowrap;
    overflow: hidden;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1) !important;
      color: #fff !important;
      transform: none !important;
    }
    
    &.is-active {
      background: linear-gradient(135deg, #34495e 0%, #466078 100%) !important;
      color: #fff !important;
      box-shadow: 0 2px 8px rgba(52, 73, 94, 0.4);
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: #fff;
        border-radius: 0 4px 4px 0;
        transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
      }
    }
    
    .el-icon {
      font-size: 18px;
      margin-right: 8px;
      transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
                  margin 0.35s cubic-bezier(0.4, 0, 0.2, 1);
      flex-shrink: 0;
      display: inline-block;
    }
    
    span {
      transition: opacity 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
                  width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
      display: inline-block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  
  // 折叠状态下的样式优化
  :deep(.el-menu--collapse) {
    .el-menu-item {
      padding: 0 20px !important;
      
      .el-icon {
        margin-right: 0;
      }
      
      span {
        opacity: 0;
        width: 0;
        transform: translateX(-10px);
      }
    }
    
    .menu-group-title {
      opacity: 0;
      transform: translateX(-10px);
      height: 0;
      padding: 0;
      margin: 0;
      border: none;
    }
  }
}

.header {
  background: linear-gradient(135deg, #ffffff 0%, #f7f9fc 55%, #edf1f7 100%);
  border-bottom: 1px solid rgba(79, 110, 251, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  height: 78px;
  position: relative;
  box-shadow:
    0 15px 45px rgba(15, 23, 42, 0.08),
    0 4px 12px rgba(15, 23, 42, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  
  // 顶部高光效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, 
      transparent 0%,
      rgba(79, 110, 251, 0.25) 15%,
      rgba(99, 179, 237, 0.35) 45%,
      rgba(79, 110, 251, 0.25) 75%,
      transparent 100%
    );
    animation: shine 3s ease-in-out infinite;
  }
  
  @keyframes shine {
    0%, 100% { opacity: 0.5; }
    50% { opacity: 1; }
  }
  
  // 增强底部模糊过渡效果
  &::after {
    content: '';
    position: absolute;
    bottom: -18px;
    left: 0;
    right: 0;
    height: 18px;
    background: linear-gradient(
      to bottom,
      rgba(79, 110, 251, 0.12) 0%,
      rgba(138, 180, 248, 0.08) 40%,
      transparent 100%
    );
    filter: blur(6px);
    pointer-events: none;
    z-index: 10;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    position: relative;
    z-index: 2;
    
    .collapse-btn {
      cursor: pointer;
      padding: 9px;
      border-radius: 12px;
      color: #4c5d7a;
      transition: background-color 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  transform 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  box-shadow 0.25s cubic-bezier(0.4, 0, 0.2, 1);
      font-size: 22px;
      background: #f2f4fb;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 
        0 4px 10px rgba(15, 23, 42, 0.08),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      border: 1px solid rgba(79, 110, 251, 0.18);
      will-change: transform, background-color;
      
      .el-icon {
        transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
        display: inline-block;
      }
      
      &:hover {
        background: #e7edfb;
        transform: translateY(-2px) scale(1.05);
        box-shadow: 
          0 8px 18px rgba(79, 110, 251, 0.15),
          inset 0 1px 0 rgba(255, 255, 255, 0.9);
        color: #3451d1;
      }
      
      &:active {
        transform: translateY(0) scale(0.98);
        background: #dfe7fb;
      }
    }
    
    .system-title {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 24px;
      border-radius: 18px;
      position: relative;
      overflow: hidden;
      background: linear-gradient(135deg, #ffffff 0%, #eef2ff 100%);
      backdrop-filter: blur(12px) saturate(140%);
      border: 1.5px solid rgba(79, 110, 251, 0.15);
      box-shadow: 
        0 8px 24px rgba(79, 110, 251, 0.12),
        inset 0 1px 0 rgba(255, 255, 255, 0.9),
        inset 0 -1px 0 rgba(79, 110, 251, 0.08);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      
      // 装饰性光效
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, 
          transparent 0%, 
          rgba(255, 255, 255, 0.2) 50%, 
          transparent 100%
        );
        animation: slideLight 3s ease-in-out infinite;
      }
      
      @keyframes slideLight {
        0%, 100% { left: -100%; }
        50% { left: 100%; }
      }
      
      &:hover,
      &:active {
        background: linear-gradient(135deg, #ffffff 0%, #eef2ff 100%);
        transform: none;
        box-shadow: 
          0 8px 24px rgba(79, 110, 251, 0.12),
          inset 0 1px 0 rgba(255, 255, 255, 0.9),
          inset 0 -1px 0 rgba(79, 110, 251, 0.08);
        border-color: rgba(79, 110, 251, 0.15);
      }
      
      .title-icon {
        font-size: 30px;
        color: #465be9;
        filter:
          drop-shadow(0 4px 10px rgba(79, 110, 251, 0.25))
          drop-shadow(0 2px 6px rgba(255, 255, 255, 0.6));
        animation: iconFloat 3s ease-in-out infinite;
        position: relative;
        z-index: 1;
      }
      
      @keyframes iconFloat {
        0%, 100% {
          transform: translateY(0) rotate(0deg);
        }
        25% {
          transform: translateY(-3px) rotate(-2deg);
        }
        75% {
          transform: translateY(-3px) rotate(2deg);
        }
      }
      
      .title-with-badge {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .title-wrapper {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          gap: 2px;
        }
        
        .title-text {
          font-size: 22px;
          font-weight: 900;
          background: linear-gradient(
            90deg,
            #1f2d3d 0%,
            #2b3d63 40%,
            #3a4c8f 80%,
            #1f2d3d 100%
          );
          background-size: 200% 100%;
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          text-shadow: none;
          letter-spacing: 1.5px;
          animation: shimmer 3s ease-in-out infinite;
          position: relative;
          filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.6))
                  drop-shadow(0 0 12px rgba(186, 230, 253, 0.4))
                  drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
          font-family: 'Arial Black', 'Helvetica Neue', 'Microsoft YaHei Bold', 'PingFang SC', 'SimHei', sans-serif;
          font-style: italic;
          line-height: 1.2;
        }
        
        .title-english {
          font-size: 9px;
          font-weight: 600;
          color: rgba(31, 45, 61, 0.6);
          letter-spacing: 3px;
          text-transform: uppercase;
          display: block;
          opacity: 0.8;
          line-height: 1;
        }
        
        @keyframes shimmer {
          0%, 100% {
            background-position: 0% center;
          }
          50% {
            background-position: 200% center;
          }
        }
        
        .platform-badge {
          display: inline-block;
          padding: 5px 16px;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 800;
          position: relative;
          overflow: hidden;
          box-shadow: 
            0 3px 12px rgba(0, 0, 0, 0.25),
            inset 0 1px 0 rgba(255, 255, 255, 0.3);
          transition: all 0.3s ease;
          
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, 
              rgba(255, 255, 255, 0.4) 0%, 
              rgba(255, 255, 255, 0.15) 50%, 
              transparent 100%
            );
            pointer-events: none;
          }
          
          &::after {
            content: '';
            position: absolute;
            top: -50%;
            right: -50%;
            width: 100%;
            height: 100%;
            background: radial-gradient(circle, 
              rgba(255, 255, 255, 0.4) 0%, 
              transparent 70%
            );
            animation: badgePulse 2s ease-in-out infinite;
          }
          
          @keyframes badgePulse {
            0%, 100% { 
              opacity: 0.5;
              transform: translate(0, 0) scale(1);
            }
            50% { 
              opacity: 1;
              transform: translate(-10px, 10px) scale(1.1);
            }
          }
          
          &.admin-platform {
            background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
            color: #fff;
            border: 1px solid rgba(255, 255, 255, 0.6);
            text-shadow: 0 1px 2px rgba(15, 23, 42, 0.25);
          }
          
          &.user-platform {
            background: linear-gradient(135deg, #faf5e6 0%, #fef9e7 100%);
            color: #374151;
            border: 1px solid rgba(0, 0, 0, 0.1);
            text-shadow: none;
          }
        }
      }
    }
    
    .header-nav {
      display: flex;
      align-items: center;
      gap: 8px;
      padding-left: 8px;
      border-left: 1px solid rgba(15, 23, 42, 0.08);
      flex-wrap: wrap;
      
      .nav-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 14px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: 600;
        color: #64748b;
        cursor: pointer;
        transition: all 0.2s ease;
        background: #ffffff;
        border: 1px solid #e2e8f0;
        
        .nav-icon {
          font-size: 16px;
          color: #64748b;
        }
        
        &:hover {
          background: #f1f5f9;
          color: #475569;
          border-color: #cbd5e1;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
          
          .nav-icon {
            color: #475569;
          }
        }
        
        &.active {
          background: linear-gradient(135deg, #64748b, #94a3b8);
          color: #ffffff;
          border-color: transparent;
          box-shadow: 0 4px 12px rgba(100, 116, 139, 0.25);
          
          .nav-icon {
            color: #ffffff;
          }
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    position: relative;
    z-index: 2;
    
    .user-dropdown-wrapper {
      position: relative;
      
      &:hover .dropdown-icon {
        transform: rotate(180deg);
      }
    }
    
    .user-dropdown-trigger {
      position: relative;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 8px 12px;
      border-radius: 10px;
      cursor: pointer;
      transition: all 0.2s ease;
      background: rgba(79, 110, 251, 0.06);
      border: 1px solid rgba(79, 110, 251, 0.18);
      outline: none;  // ✅ 移除默认焦点边框
      
      &:hover {
        background: rgba(79, 110, 251, 0.12);
        border-color: rgba(79, 110, 251, 0.35);
        
        .user-name {
          color: #1f2d3d;
        }
      }
      
      &:focus {
        outline: none;
        background: rgba(79, 110, 251, 0.14);
      }
      
      &:active {
        background: rgba(79, 110, 251, 0.2);
      }
      
      // 装饰性背景（简化）
      .trigger-background {
        display: none;
      }
      
      // 用户信息内容
      .user-info-content {
        display: flex;
        align-items: center;
        gap: 10px;
      }
      
      // 头像包装器
      .avatar-wrapper {
        position: relative;
        
        .user-avatar {
          width: 36px;
          height: 36px;
          background: white;
          color: #34495e;
          font-weight: 700;
          font-size: 16px;
          border: 2px solid rgba(255, 255, 255, 0.5);
          transition: all 0.2s ease;
        }
        
        // 在线状态指示器
        .avatar-status {
          position: absolute;
          bottom: -2px;
          right: -2px;
          width: 12px;
          height: 12px;
          border-radius: 50%;
          border: 2px solid rgba(52, 73, 94, 0.9);
          transition: background-color 0.3s ease;
          
          &.status-active {
            background: #10b981; // 绿色 - 活跃
          }
          
          &.status-inactive {
            background: #ef4444; // 红色 - 不活跃
          }
        }
      }
      
      // 用户文本
      .user-text {
        display: flex;
        flex-direction: column;
        gap: 2px;
        
        .user-name {
          font-size: 14px;
          color: #1f2d3d;
          font-weight: 600;
          line-height: 1.2;
          transition: all 0.2s ease;
        }
        
        .user-role {
          font-size: 12px;
          color: rgba(31, 45, 61, 0.6);
          font-weight: 400;
          line-height: 1;
        }
      }
      
      // 下拉图标
      .dropdown-icon {
        font-size: 12px;
        color: rgba(31, 45, 61, 0.6);
        transition: all 0.2s ease;
      }
    }
  }
  
  // 浮动动画
  @keyframes float1 {
    0%, 100% {
      transform: translate(0, 0);
    }
    50% {
      transform: translate(10px, -10px);
    }
  }
  
  @keyframes float2 {
    0%, 100% {
      transform: translate(0, 0);
    }
    50% {
      transform: translate(-8px, 8px);
    }
  }
  
  // 脉冲动画
  @keyframes pulse {
    0%, 100% {
      opacity: 1;
      transform: scale(1);
    }
    50% {
      opacity: 0.8;
      transform: scale(1.1);
    }
  }
}

// 用户下拉菜单样式
:deep(.el-dropdown-menu) {
    border-radius: 16px;
    box-shadow: 
      0 12px 40px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255, 255, 255, 0.1);
    padding: 12px;
    border: 1.5px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(30px);
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.95) 0%, 
      rgba(255, 255, 255, 0.9) 100%);
    margin-top: 8px;
    transform: translateX(60px) !important;
    left: auto !important;
    right: -60px !important;
  }
  
  :deep(.el-dropdown-menu__item) {
      border-radius: 8px;
      margin: 0;
      padding: 12px 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      transition: all 0.2s ease;
      border-top: 1px solid rgba(0, 0, 0, 0.06);
      
      &:first-child {
        border-top: none;
      }
      
      .el-icon {
        font-size: 18px;
        color: #606266;
        transition: all 0.2s ease;
      }
      
      span {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
        transition: all 0.2s ease;
      }
      
      &:hover {
        background: #f3f4f6;
        
        .el-icon {
          color: #34495e;
        }
        
        span {
          color: #34495e;
        }
      }
      
      &:active {
        background: #e5e7eb;
      }
  }

.main-container {
  flex: 1;
  height: calc(100vh - 80px);
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f4ff 50%, #f5f7fa 100%);
  background-size: 200% 200%;
  animation: bgGradientShift 10s ease infinite;
  position: relative;
  
  // 添加顶部模糊过渡效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 20px;
    background: linear-gradient(to bottom, 
      rgba(245, 247, 250, 0.6) 0%,
      rgba(245, 247, 250, 0.3) 30%,
      transparent 100%
    );
    backdrop-filter: blur(6px);
    pointer-events: none;
    z-index: 5;
  }
  
  @keyframes bgGradientShift {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
  }
}

.main-content {
  background: linear-gradient(
    180deg,
    rgba(240, 244, 255, 0.8) 0%,
    rgba(245, 247, 250, 1) 5%,
    #f5f7fa 10%
  );
  padding: 30px 20px 20px;
  overflow-y: auto;
  width: 100%;
  position: relative;
  
  // 增强顶部过渡效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 40px;
    background: 
      radial-gradient(ellipse at top, 
        rgba(52, 73, 94, 0.12) 0%,
        rgba(70, 96, 120, 0.06) 30%,
        transparent 70%
      );
    pointer-events: none;
    z-index: 0;
  }
}

// 修改密码对话框样式
:deep(.change-password-dialog) {
  .el-dialog {
    border-radius: 12px !important;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;
  }
  
  .el-dialog__header {
    padding: 24px 24px 20px !important;
    background: linear-gradient(to bottom, #fafbfc 0%, #f5f7fa 100%) !important;
    border-bottom: 1px solid #ebeef5 !important;
    margin: 0 !important;
    
    .el-dialog__title {
      font-size: 20px !important;
      font-weight: 600 !important;
      color: #303133 !important;
      letter-spacing: 0.3px;
    }
    
    .el-dialog__headerbtn {
      top: 20px !important;
      right: 20px !important;
      
      .el-dialog__close {
        color: #909399 !important;
        font-size: 18px !important;
        transition: color 0.2s ease;
        
        &:hover {
          color: #606266 !important;
        }
      }
    }
  }
  
  .el-dialog__body {
    padding: 32px 28px !important;
    background: #fff !important;
  }
  
  .password-info-strip {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 18px;
    border-radius: 10px;
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.08), rgba(59, 130, 246, 0.08));
    border: 1px solid rgba(99, 102, 241, 0.15);
    margin-bottom: 20px;
    
    .password-info-icon {
      font-size: 24px;
      color: #4f46e5;
    }
    
    .password-info-text {
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: 13px;
      color: #475569;
      
      strong {
        font-size: 14px;
        color: #1f2937;
      }
    }
  }
  
  .el-dialog__footer {
    padding: 20px 24px 24px !important;
    background: linear-gradient(to top, #fafbfc 0%, #f5f7fa 100%) !important;
    border-top: 1px solid #ebeef5 !important;
    margin: 0 !important;
  }
  
  // 表单样式优化
  .el-form {
    .el-form-item {
      margin-bottom: 32px !important;
      
      &:last-child {
        margin-bottom: 0 !important;
      }
      
      .el-form-item__label {
        font-size: 18px !important;
        color: #606266 !important;
        font-weight: 500 !important;
        padding-bottom: 12px !important;
        line-height: 1.5;
      }
      
      .el-form-item__content {
        .el-input {
          // 覆盖 Element Plus CSS 变量
          --el-input-focus-border-color: #e4e7ed !important;
          --el-border-color: #e4e7ed !important;
          --el-color-primary: #e4e7ed !important;
          
          .el-input__wrapper {
            border-radius: 8px !important;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
            transition: none !important;
            background: white !important;
            border: 1px solid #e4e7ed !important;
            border-color: #e4e7ed !important;
            padding: 10px 16px !important;
            min-height: 36px !important;
            outline: none !important;
            
            &:hover {
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              border-color: #e4e7ed !important;
              border: 1px solid #e4e7ed !important;
              background: white !important;
              --el-input-focus-border-color: #e4e7ed !important;
              --el-border-color: #e4e7ed !important;
            }
            
            &.is-focus,
            &:focus,
            &:focus-visible,
            &:focus-within {
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              border-color: #e4e7ed !important;
              border: 1px solid #e4e7ed !important;
              background: white !important;
              --el-input-focus-border-color: #e4e7ed !important;
              --el-border-color: #e4e7ed !important;
              --el-color-primary: #e4e7ed !important;
              outline: none !important;
            }
          }
          
          .el-input__inner {
            font-size: 17px !important;
            color: #303133 !important;
            line-height: 1.6;
            
            &::placeholder {
              font-size: 17px !important;
              color: #909399 !important;
            }
          }
        }
      }
    }
  }
}

.password-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .password-dialog-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 6px;
    border: 1px solid #dcdfe6 !important;
    background: #fff !important;
    color: #606266 !important;
    font-weight: 400;
    font-size: 14px;
    box-shadow: none;
    transition: all 0.2s ease;
    min-width: 80px;

    &:hover {
      border-color: #c0c4cc !important;
      color: #606266 !important;
      background: #fff !important;
    }

    &:focus {
      border-color: #c0c4cc !important;
      color: #606266 !important;
      background: #fff !important;
    }

    &:active {
      border-color: #c0c4cc !important;
      color: #606266 !important;
      background: #fff !important;
    }

    &.is-loading {
      border-color: #dcdfe6 !important;
      background: #fff !important;
      color: #606266 !important;
    }
  }
}

</style>

<style lang="scss">
// 修改密码对话框全局样式（非 scoped，因为 dialog 渲染在 body 下）
.el-overlay .change-password-dialog {
  .el-dialog {
    border-radius: 10px !important;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06) !important;
  }
  
  .el-dialog__header {
    padding: 16px 20px !important;
    background: #ffffff !important;
    border-bottom: 1px solid #f0f0f0 !important;
    margin: 0 !important;
    
    .el-dialog__title {
      font-size: 20px !important;
      font-weight: 500 !important;
      color: #262626 !important;
    }
  }
  
  .el-dialog__body {
    padding: 18px 20px 10px !important;
    background: #ffffff !important;
    
    // 强制覆盖修改密码对话框的输入框样式
    .el-form {
      .el-form-item {
        // 隐藏错误提示（使用 ElMessage 弹出提示代替）
        .el-form-item__error {
          display: none !important;
        }
        
        // 标签和输入框垂直居中对齐
        display: flex !important;
        align-items: center !important;
        
        .el-form-item__label {
          font-size: 16px !important;
          color: #606266 !important;
          font-weight: 500 !important;
          padding-bottom: 0 !important;
          line-height: 1.5;
          display: flex !important;
          align-items: center !important;
        }
        
        &.is-required .el-form-item__label::before {
          content: '*' !important;
          color: #f56c6c !important;
          margin-right: 4px;
        }
        
        .el-form-item__content {
          .el-input {
            // 覆盖 Element Plus CSS 变量
            --el-input-focus-border-color: #e4e7ed !important;
            --el-border-color: #e4e7ed !important;
            --el-color-primary: #e4e7ed !important;
            
            .el-input__wrapper {
              border-radius: 6px !important;
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              transition: none !important;
              background: white !important;
              border: 1px solid #e4e7ed !important;
              border-color: #e4e7ed !important;
              padding: 6px 12px !important;
              min-height: 36px !important;
              outline: none !important;
              
              &:hover {
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                border-color: #e4e7ed !important;
                border: 1px solid #e4e7ed !important;
                background: white !important;
                --el-input-focus-border-color: #e4e7ed !important;
                --el-border-color: #e4e7ed !important;
              }
              
              &.is-focus,
              &:focus,
              &:focus-visible,
              &:focus-within {
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                border-color: #e4e7ed !important;
                border: 1px solid #e4e7ed !important;
                background: white !important;
                --el-input-focus-border-color: #e4e7ed !important;
                --el-border-color: #e4e7ed !important;
                --el-color-primary: #e4e7ed !important;
                outline: none !important;
              }
            }
            
            .el-input__inner {
              font-size: 14px !important;
              color: #303133 !important;
              line-height: 1.5;
              
              &::placeholder {
                font-size: 14px !important;
                color: #909399 !important;
              }
            }
          }
        }
      }
    }
  }
  
  .el-dialog__footer {
    padding: 10px 20px 16px !important;
    background: #ffffff !important;
    border-top: 1px solid #f0f0f0 !important;
    margin: 0 !important;
    
    .password-dialog-footer-minimal {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      gap: 14px;
      
      .el-button {
        min-width: 100px;
        min-height: 36px;
        padding: 8px 20px;
        border-radius: 6px;
        font-weight: 500;
        font-size: 14px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        transition: none;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      
      // 取消按钮：灰色系
      .el-button:first-child {
        border: 1px solid #dcdfe6;
        background-color: #f5f7fa;
        color: #909399;
        
        &:hover {
          border-color: #c0c4cc;
          color: #909399;
          background-color: #ebeef5;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:active {
          border-color: #c0c4cc;
          color: #909399;
          background-color: #e4e7ed;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:focus {
          border-color: #c0c4cc;
          color: #909399;
          background-color: #f5f7fa;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
      }
      
      // 确定按钮：灰色系
      .el-button--primary {
        background-color: #f5f7fa;
        border: 1px solid #dcdfe6;
        color: #909399;
        
        &:hover {
          background-color: #ebeef5;
          border-color: #c0c4cc;
          color: #909399;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:active {
          background-color: #e4e7ed;
          border-color: #c0c4cc;
          color: #909399;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:focus {
          background-color: #f5f7fa;
          border-color: #c0c4cc;
          color: #909399;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
      }
    }
  }
}

// 注销对话框全局样式
.el-overlay .deactivate-dialog {
  .el-dialog {
    border-radius: 10px !important;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06) !important;
  }
  
  .el-dialog__header {
    padding: 16px 20px !important;
    background: #ffffff !important;
    border-bottom: 1px solid #f0f0f0 !important;
    margin: 0 !important;
    
    .el-dialog__title {
      font-size: 20px !important;
      font-weight: 500 !important;
      color: #262626 !important;
    }
  }
  
  .deactivate-warning {
    background: #fff5f5;
    border: 1px solid #fecaca;
    color: #c2410c;
    padding: 16px 20px;
    border-radius: 8px;
    font-size: 15px;
    line-height: 1.7;
    margin-bottom: 20px;

    .deactivate-warning-title {
      font-weight: 600;
      font-size: 16px;
      margin: 0 0 8px 0;
    }

    .deactivate-warning-list {
      margin: 0;
      padding-left: 20px;

      li {
        margin: 4px 0;
      }
    }
  }

  .el-dialog__body {
    padding: 20px 24px 16px !important;
    
    .el-form {
      .el-form-item {
        margin-bottom: 24px !important;
        
        // 隐藏错误提示（使用 ElMessage 弹出提示代替）
        .el-form-item__error {
          display: none !important;
        }
        
        .el-form-item__label {
          font-size: 16px !important;
          color: #606266 !important;
          font-weight: 500 !important;
          padding-bottom: 10px !important;
          line-height: 1.5;
        }
        
        .el-form-item__content {
          .el-input {
            --el-input-focus-border-color: #e4e7ed !important;
            --el-border-color: #e4e7ed !important;
            --el-color-primary: #e4e7ed !important;
            
            .el-input__wrapper {
              border-radius: 8px !important;
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              transition: none !important;
              background: white !important;
              border: 1px solid #e4e7ed !important;
              border-color: #e4e7ed !important;
              padding: 12px 16px !important;
              min-height: 48px !important;
              outline: none !important;
              
              &:hover {
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                border-color: #e4e7ed !important;
                border: 1px solid #e4e7ed !important;
                background: white !important;
                --el-input-focus-border-color: #e4e7ed !important;
                --el-border-color: #e4e7ed !important;
              }
              
              &.is-focus,
              &:focus,
              &:focus-visible,
              &:focus-within {
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                border-color: #e4e7ed !important;
                border: 1px solid #e4e7ed !important;
                background: white !important;
                --el-input-focus-border-color: #e4e7ed !important;
                --el-border-color: #e4e7ed !important;
                --el-color-primary: #e4e7ed !important;
                outline: none !important;
              }
            }
            
            .el-input__inner {
              font-size: 15px !important;
              color: #303133 !important;
              line-height: 1.6;
              
              &::placeholder {
                font-size: 15px !important;
                color: #909399 !important;
              }
            }
          }
          
          .el-textarea {
            .el-textarea__inner {
              border-radius: 8px !important;
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              transition: none !important;
              background: white !important;
              border: 1px solid #e4e7ed !important;
              border-color: #e4e7ed !important;
              padding: 12px 16px !important;
              font-size: 15px !important;
              color: #303133 !important;
              line-height: 1.6;
              outline: none !important;
              
              &::placeholder {
                font-size: 15px !important;
                color: #909399 !important;
              }
              
              &:hover {
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                border-color: #e4e7ed !important;
                background: white !important;
              }
              
              &:focus {
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                border-color: #e4e7ed !important;
                background: white !important;
                outline: none !important;
              }
            }
          }
        }
      }
    }
  }
  
  .el-dialog__footer {
    padding: 10px 20px 16px !important;
    background: #ffffff !important;
    border-top: 1px solid #f0f0f0 !important;
    margin: 0 !important;
  }

  .deactivate-dialog-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 14px;
    
    .el-button {
      min-width: 100px;
      min-height: 36px;
      padding: 8px 20px;
      border-radius: 6px;
      font-weight: 500;
      font-size: 14px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      transition: none;
      display: flex;
      align-items: center;
      justify-content: center;
      
      // 取消按钮：灰色系
      &:first-child {
        border: 1px solid #dcdfe6;
        background-color: #f5f7fa;
        color: #909399;
        
        &:hover {
          border-color: #c0c4cc;
          color: #909399;
          background-color: #ebeef5;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:active {
          border-color: #c0c4cc;
          color: #909399;
          background-color: #e4e7ed;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:focus {
          border-color: #c0c4cc;
          color: #909399;
          background-color: #f5f7fa;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
      }
      
      // 确认注销按钮：灰色系
      &:last-child {
        border: 1px solid #dcdfe6;
        background-color: #f5f7fa;
        color: #909399;
        
        &:hover {
          background-color: #ebeef5;
          border-color: #c0c4cc;
          color: #909399;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:active {
          background-color: #e4e7ed;
          border-color: #c0c4cc;
          color: #909399;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
        
        &:focus {
          background-color: #f5f7fa;
          border-color: #c0c4cc;
          color: #909399;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        }
      }
    }
  }
}
</style>
