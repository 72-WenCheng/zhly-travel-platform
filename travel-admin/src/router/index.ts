import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ showSpinner: false })

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/forgot-password/index.vue'),
    meta: { title: '忘记密码' }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/reset-password/index.vue'),
    meta: { title: '重置密码' }
  },
  {
    path: '/maintenance',
    name: 'UserMaintenance',
    component: () => import('@/views/user/maintenance.vue'),
    meta: { title: '系统维护' }
  },
  {
    path: '/dashboard',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/admin/users/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'travel-plan',
        name: 'TravelPlanManagement',
        component: () => import('@/views/travel-plan/index.vue'),
        meta: { title: '攻略管理' }
      },
      {
        path: 'attraction',
        name: 'AttractionManagement',
        component: () => import('@/views/attraction/index.vue'),
        meta: { title: '景点管理' }
      },
      {
        path: 'ai',
        name: 'AIManagement',
        component: () => import('@/views/ai/index.vue'),
        meta: { title: 'AI智能' }
      },
      {
        path: 'culture',
        name: 'CultureManagement',
        component: () => import('@/views/culture/index.vue'),
        meta: { title: '文旅对接' }
      },
      {
        path: 'system',
        name: 'SystemSettings',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统设置' }
      },
      {
        path: 'user/profile/:id?',
        name: 'UserProfileDetail',
        component: () => import('@/views/user/profile/detail.vue'),
        meta: { title: '用户主页', icon: 'User' }
      },
      {
        path: 'user/profile-edit',
        name: 'UserProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/home',
    component: () => import('@/views/home/index.vue'),
    children: [
      // 用户端路由
      {
        path: 'user/dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/user/dashboard/index.vue'),
        meta: { title: '用户首页', icon: 'House' }
      },
      {
        path: 'user/community',
        name: 'UserCommunity',
        component: () => import('@/views/user/community/index.vue'),
        meta: { title: '攻略社区', icon: 'ChatDotRound' }
      },
      {
        path: 'user/messages',
        name: 'UserMessages',
        component: () => import('@/views/user/messages/index.vue'),
        meta: { title: '私信中心', icon: 'ChatLineRound' }
      },
      {
        path: 'user/plans',
        name: 'UserPlans',
        component: () => import('@/views/user/plans/index.vue'),
        meta: { title: '我的攻略', icon: 'Document' }
      },
      {
        path: 'user/plans/create',
        name: 'UserPlanCreate',
        component: () => import('@/views/user/plans/create.vue'),
        meta: { title: '创建攻略', icon: 'DocumentAdd' }
      },
      {
        path: 'user/plans/edit/:id',
        name: 'UserPlanEdit',
        component: () => import('@/views/user/plans/edit.vue'),
        meta: { title: '编辑攻略', icon: 'Edit' }
      },
      {
        path: 'user/recommendations',
        name: 'UserRecommendations',
        component: () => import('@/views/user/recommendations/index.vue'),
        meta: { title: '景点推荐', icon: 'Location' }
      },
      {
        path: 'user/collect',
        name: 'UserCollect',
        component: () => import('@/views/user/collect/index.vue'),
        meta: { title: '我的收藏', icon: 'Star' }
      },
      {
        path: 'user/attractions',
        name: 'UserAttractions',
        component: () => import('@/views/user/attractions/index.vue'),
        meta: { title: '景点列表', icon: 'Location' }
      },
      {
        path: 'user/ai',
        name: 'UserAI',
        component: () => import('@/views/user/ai/index.vue'),
        meta: { title: 'AI生成', icon: 'MagicStick' }
      },
      {
        path: 'user/portrait',
        name: 'UserPortrait',
        component: () => import('@/views/user/portrait/index.vue'),
        meta: { title: '用户画像', icon: 'DataAnalysis' }
      },
      {
        path: 'user/level-guide',
        name: 'UserLevelGuide',
        component: () => import('@/views/user/level-guide/index.vue'),
        meta: { title: '升级指南', icon: 'TrophyBase' }
      },
      {
        path: 'user/profile-edit',
        name: 'UserProfileEdit',
        component: () => import('@/views/user/profile-edit/index.vue'),
        meta: { title: '完善资料', icon: 'User' }
      },
      {
        path: 'user/culture',
        name: 'UserCulture',
        component: () => import('@/views/user/culture/medium.vue'),
        meta: { title: '文旅对接', icon: 'Shop' }
      },
      {
        path: 'user/culture/detail/:id',
        name: 'CultureDetail',
        component: () => import('@/views/user/culture/detail.vue'),
        meta: { title: '文化体验详情', icon: 'Document' }
      },
      {
        path: 'user/culture/service/:id',
        name: 'CultureService',
        component: () => import('@/views/user/culture/service.vue'),
        meta: { title: '服务详情', icon: 'Document' }
      },
      {
        path: 'user/culture/project/:id',
        name: 'CultureProject',
        component: () => import('@/views/user/culture/project.vue'),
        meta: { title: '政府项目详情', icon: 'Document' }
      },
      {
        path: 'user/culture/product/:id',
        name: 'CultureProduct',
        component: () => import('@/views/user/culture/product.vue'),
        meta: { title: '产品详情', icon: 'Document' }
      },
      {
        path: 'user/culture/cart',
        name: 'CultureCart',
        component: () => import('@/views/user/culture/cart.vue'),
        meta: { title: '购物车', icon: 'ShoppingCart' }
      },
      {
        path: 'user/culture/order',
        name: 'CultureOrder',
        component: () => import('@/views/user/culture/order.vue'),
        meta: { title: '确认订单', icon: 'Document' }
      },
      {
        path: 'user/culture/orders',
        name: 'CultureOrders',
        component: () => import('@/views/user/culture/orders.vue'),
        meta: { title: '我的订单', icon: 'List' }
      },
      {
        path: 'user/culture/order-detail/:id',
        name: 'CultureOrderDetail',
        component: () => import('@/views/user/culture/orderDetail.vue'),
        meta: { title: '订单详情', icon: 'Document' }
      },
      {
        path: 'user/culture/bookings',
        name: 'CultureBookings',
        component: () => import('@/views/user/culture/bookings.vue'),
        meta: { title: '我的预订', icon: 'Calendar' }
      },
      {
        path: 'user/culture/applications',
        name: 'CultureApplications',
        component: () => import('@/views/user/culture/applications.vue'),
        meta: { title: '我的申请', icon: 'Document' }
      },
      // 用户端详情页路由
      {
        path: 'user/profile/:id',
        name: 'UserProfileDetail',
        component: () => import('@/views/user/profile/detail.vue'),
        meta: { title: '用户主页', icon: 'User' }
      },
      {
        path: 'user/plans/detail/:id',
        name: 'UserPlanDetail',
        component: () => import('@/views/user/plans/detail.vue'),
        meta: { title: '攻略详情', icon: 'Document' }
      },
      {
        path: 'user/attractions/detail/:id',
        name: 'UserAttractionDetail',
        component: () => import('@/views/user/attractions/detail.vue'),
        meta: { title: '景点详情', icon: 'Location' }
      },
      {
        path: 'user/culture/detail/:id',
        name: 'UserCultureDetail',
        component: () => import('@/views/user/culture/detail.vue'),
        meta: { title: '文旅详情', icon: 'Shop' }
      },
      // 管理端路由
      {
        path: 'admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '管理首页', icon: 'House' }
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'admin/plans',
        name: 'AdminPlans',
        component: () => import('@/views/admin/plans/index.vue'),
        meta: { title: '攻略管理', icon: 'Document' }
      },
      {
        path: 'admin/plans/detail/:id',
        name: 'AdminPlanDetail',
        component: () => import('@/views/admin/plans/detail.vue'),
        meta: { title: '攻略详情', icon: 'Document' }
      },
      {
        path: 'admin/attractions',
        name: 'AdminAttractions',
        component: () => import('@/views/admin/attractions/index.vue'),
        meta: { title: '景点管理', icon: 'Location' }
      },
      {
        path: 'admin/attractions/create',
        name: 'AdminAttractionsCreate',
        component: () => import('@/views/admin/attractions/create.vue'),
        meta: { title: '添加景点', icon: 'Location' }
      },
      {
        path: 'admin/attractions/edit/:id',
        name: 'AdminAttractionsEdit',
        component: () => import('@/views/admin/attractions/create.vue'),
        meta: { title: '编辑景点', icon: 'Location' }
      },
      {
        path: 'admin/ai',
        name: 'AdminAI',
        component: () => import('@/views/admin/ai/index.vue'),
        meta: { title: 'AI管理', icon: 'MagicStick' }
      },
      {
        path: 'admin/culture',
        name: 'AdminCulture',
        component: () => import('@/views/admin/culture/index.vue'),
        meta: { title: '文旅管理', icon: 'Shop' }
      },
      {
        path: 'admin/banner',
        name: 'AdminBanner',
        component: () => import('@/views/admin/banner/index.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/statistics/index.vue'),
        meta: { title: '数据统计', icon: 'TrendCharts' }
      },
      {
        path: 'admin/user-level',
        name: 'AdminUserLevel',
        component: () => import('@/views/admin/user-level/index.vue'),
        meta: { title: '用户等级', icon: 'Trophy' }
      },
      {
        path: 'admin/points',
        name: 'AdminPoints',
        component: () => import('@/views/admin/points/index.vue'),
        meta: { title: '积分管理', icon: 'Coin' }
      },
      {
        path: 'admin/report',
        name: 'AdminReport',
        component: () => import('@/views/admin/report/index.vue'),
        meta: { title: '举报审核', icon: 'Warning' }
      },
      {
        path: 'admin/browse-history',
        name: 'AdminBrowseHistory',
        component: () => import('@/views/admin/browse-history/index.vue'),
        meta: { title: '浏览历史', icon: 'Clock' }
      },
      {
        path: 'admin/announcement',
        name: 'AdminAnnouncement',
        component: () => import('@/views/admin/announcement/index.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      },
      {
        path: 'admin/comment',
        name: 'AdminComment',
        component: () => import('@/views/admin/comment/index.vue'),
        meta: { title: '评论管理', icon: 'ChatDotRound' }
      },
      {
        path: 'admin/culture/orders',
        name: 'AdminCultureOrders',
        component: () => import('@/views/admin/culture/orders.vue'),
        meta: { title: '订单管理', icon: 'ShoppingCart' }
      },
      {
        path: 'admin/culture/coupons',
        name: 'AdminCultureCoupons',
        component: () => import('@/views/admin/culture/coupons.vue'),
        meta: { title: '优惠券管理', icon: 'Tickets' }
      },
      {
        path: 'admin/culture/bookings',
        name: 'AdminCultureBookings',
        component: () => import('@/views/admin/culture/bookings.vue'),
        meta: { title: '预订管理', icon: 'Calendar' }
      },
      {
        path: 'admin/culture/applications',
        name: 'AdminCultureApplications',
        component: () => import('@/views/admin/culture/applications.vue'),
        meta: { title: '申请管理', icon: 'Files' }
      },
      {
        path: 'admin/system',
        name: 'AdminSystem',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统设置', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的位置（浏览器前进/后退），则使用保存的位置
    if (savedPosition) {
      return savedPosition
    }
    // 默认每次进入新页面时滚动到顶部
    return { left: 0, top: 0 }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  const systemStore = useSystemStore()

  if (!systemStore.loaded) {
    try {
      await systemStore.fetchConfig()
    } catch (error) {
      console.error('加载系统配置失败:', error)
    }
  }

  const isUserRoute = to.path.startsWith('/home/user')
  const isMaintenanceRoute = to.name === 'UserMaintenance'

  if (systemStore.maintenanceMode && isUserRoute && !isMaintenanceRoute) {
    next({ name: 'UserMaintenance' })
    return
  }

  if (!systemStore.maintenanceMode && isMaintenanceRoute) {
    next('/home/user/dashboard')
    return
  }
  
  // 如果是登录页面、注册页面、忘记密码页面或重置密码页面，直接放行
  if (to.path === '/' || to.path === '/register' || to.path === '/forgot-password' || to.path === '/reset-password' || to.path === '/maintenance') {
    next()
    return
  }
  
  // 初始化用户信息（确保刷新时能正确恢复当前标签页的用户身份）
  if (!userStore.isLoggedIn || !userStore.token) {
    userStore.initUser()
  }
  
  // 检查是否已登录
  if (!userStore.token) {
    // 尝试从sessionStorage获取token
    const sessionToken = sessionStorage.getItem('travel_token')
    if (!sessionToken) {
      next('/')
      return
    }
  }
  
  // 根据用户角色检查权限
  // 优先从sessionStorage获取当前标签页的用户信息
  let savedUserInfo = sessionStorage.getItem('travel_user_info')
  if (!savedUserInfo) {
    savedUserInfo = localStorage.getItem('travel_user_info')
  }
  
  if (savedUserInfo) {
    try {
      const userInfo = JSON.parse(savedUserInfo)
      const role = userInfo.role
      
      // 权限检查：如果用户访问不符合其角色的路由，进行重定向
      
      // 管理员访问 /home/user/* 路径时，重定向到管理平台
      if (role === 1 && to.path.startsWith('/home/user')) {
        console.log('管理员尝试访问用户平台，重定向到管理平台')
        next('/home/admin/dashboard')
        return
      }
      
      // 普通用户访问 /dashboard 或 /home/admin 路径时，重定向到用户平台
      if (role === 2 && (to.path.startsWith('/dashboard') || to.path.startsWith('/home/admin'))) {
        console.log('普通用户尝试访问管理平台，重定向到用户平台')
        next('/home/user/dashboard')
        return
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
