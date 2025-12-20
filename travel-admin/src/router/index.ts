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
        component: () => import('@/views/admin/culture/index.vue'),
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
        path: 'user/culture/experience/:id',
        name: 'CultureExperienceDetail',
        component: () => import('@/views/user/culture/experienceDetail.vue'),
        meta: { title: '文化体验详情', icon: 'Document' }
      },
      {
        path: 'user/culture/service/:id',
        name: 'CultureService',
        component: () => import('@/views/user/culture/service.vue'),
        meta: { title: '服务详情', icon: 'Document' }
      },
      {
        path: 'user/culture/homestay/:id',
        name: 'CultureHomestay',
        component: () => import('@/views/user/culture/homestay.vue'),
        meta: { title: '民宿详情', icon: 'HomeFilled' }
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
        path: 'user/culture/agri-product/:id',
        name: 'CultureAgriProduct',
        component: () => import('@/views/user/culture/agriProductDetail.vue'),
        meta: { title: '农特产品详情', icon: 'Document' }
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
        path: 'user/culture/center',
        name: 'CultureCenter',
        component: () => import('@/views/user/culture/center.vue'),
        meta: { title: '我的文旅', icon: 'Shop' }
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
        path: 'user/culture/booking-pay/:id',
        name: 'CultureBookingPay',
        component: () => import('@/views/user/culture/bookingPay.vue'),
        meta: { title: '预订支付', icon: 'Wallet' }
      },
      {
        path: 'user/culture/booking-detail/:id',
        name: 'CultureBookingDetail',
        component: () => import('@/views/user/culture/bookingDetail.vue'),
        meta: { title: '预约详情', icon: 'Document' }
      },
      {
        path: 'user/culture/applications',
        name: 'CultureApplications',
        component: () => import('@/views/user/culture/applications.vue'),
        meta: { title: '我的申请', icon: 'Document' }
      },
      {
        path: 'user/coupons',
        name: 'UserCoupons',
        component: () => import('@/views/user/coupons/index.vue'),
        meta: { title: '我的优惠券', icon: 'Ticket' }
      },
      {
        path: 'user/addresses',
        name: 'UserAddresses',
        component: () => import('@/views/user/address/index.vue'),
        meta: { title: '我的地址', icon: 'Location' }
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
      // 农家乐管理
      {
        path: 'admin/culture/services',
        name: 'AdminCultureServices',
        component: () => import('@/views/admin/culture/services/index.vue'),
        meta: { title: '农家乐管理', icon: 'House' }
      },
      {
        path: 'admin/culture/services/create',
        name: 'AdminCultureServicesCreate',
        component: () => import('@/views/admin/culture/services/create.vue'),
        meta: { title: '添加农家乐', icon: 'House' }
      },
      {
        path: 'admin/culture/services/edit/:id',
        name: 'AdminCultureServicesEdit',
        component: () => import('@/views/admin/culture/services/create.vue'),
        meta: { title: '编辑农家乐', icon: 'House' }
      },
      // 特色民宿管理
      {
        path: 'admin/culture/homestays',
        name: 'AdminCultureHomestays',
        component: () => import('@/views/admin/culture/homestays/index.vue'),
        meta: { title: '特色民宿管理', icon: 'HomeFilled' }
      },
      {
        path: 'admin/culture/homestays/create',
        name: 'AdminCultureHomestaysCreate',
        component: () => import('@/views/admin/culture/homestays/create.vue'),
        meta: { title: '添加民宿', icon: 'HomeFilled' }
      },
      {
        path: 'admin/culture/homestays/edit/:id',
        name: 'AdminCultureHomestaysEdit',
        component: () => import('@/views/admin/culture/homestays/create.vue'),
        meta: { title: '编辑民宿', icon: 'HomeFilled' }
      },
      // 农特产品管理
      {
        path: 'admin/culture/products',
        name: 'AdminCultureProducts',
        component: () => import('@/views/admin/culture/products/index.vue'),
        meta: { title: '农特产品管理', icon: 'ShoppingBag' }
      },
      {
        path: 'admin/culture/products/create',
        name: 'AdminCultureProductsCreate',
        component: () => import('@/views/admin/culture/products/create.vue'),
        meta: { title: '添加产品', icon: 'ShoppingBag' }
      },
      {
        path: 'admin/culture/products/edit/:id',
        name: 'AdminCultureProductsEdit',
        component: () => import('@/views/admin/culture/products/create.vue'),
        meta: { title: '编辑产品', icon: 'ShoppingBag' }
      },
      // 政策对接管理
      {
        path: 'admin/culture/projects',
        name: 'AdminCultureProjects',
        component: () => import('@/views/admin/culture/projects/index.vue'),
        meta: { title: '政策对接管理', icon: 'Document' }
      },
      {
        path: 'admin/culture/projects/create',
        name: 'AdminCultureProjectsCreate',
        component: () => import('@/views/admin/culture/projects/create.vue'),
        meta: { title: '添加项目', icon: 'Document' }
      },
      {
        path: 'admin/culture/projects/edit/:id',
        name: 'AdminCultureProjectsEdit',
        component: () => import('@/views/admin/culture/projects/create.vue'),
        meta: { title: '编辑项目', icon: 'Document' }
      },
      // 文化体验管理
      {
        path: 'admin/culture/experiences',
        name: 'AdminCultureExperiences',
        component: () => import('@/views/admin/culture/experiences/index.vue'),
        meta: { title: '文化体验管理', icon: 'Star' }
      },
      {
        path: 'admin/culture/experiences/create',
        name: 'AdminCultureExperiencesCreate',
        component: () => import('@/views/admin/culture/experiences/create.vue'),
        meta: { title: '添加体验', icon: 'Star' }
      },
      {
        path: 'admin/culture/experiences/edit/:id',
        name: 'AdminCultureExperiencesEdit',
        component: () => import('@/views/admin/culture/experiences/create.vue'),
        meta: { title: '编辑体验', icon: 'Star' }
      },
      {
        path: 'admin/culture/souvenirs',
        name: 'AdminCultureSouvenirs',
        component: () => import('@/views/admin/culture/souvenirs/index.vue'),
        meta: { title: '特色周边管理', icon: 'ShoppingBag' }
      },
      {
        path: 'admin/culture/souvenirs/create',
        name: 'AdminCultureSouvenirsCreate',
        component: () => import('@/views/admin/culture/souvenirs/create.vue'),
        meta: { title: '创建特色周边', icon: 'ShoppingBag' }
      },
      {
        path: 'admin/culture/souvenirs/edit/:id',
        name: 'AdminCultureSouvenirsEdit',
        component: () => import('@/views/admin/culture/souvenirs/create.vue'),
        meta: { title: '编辑特色周边', icon: 'ShoppingBag' }
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
        path: 'admin/culture/appointments',
        name: 'AdminCultureAppointments',
        component: () => import('@/views/admin/culture/appointments.vue'),
        meta: { title: '预约管理', icon: 'Tickets' }
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

// 防止重复导航的标记
let isNavigating = false

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 如果正在导航中，直接放行，避免重复导航
  if (isNavigating) {
    next()
    return
  }
  
  NProgress.start()
  
  const userStore = useUserStore()
  const systemStore = useSystemStore()

  // 优化：对于公开页面，不等待系统配置加载，直接放行
  const isPublicPage = to.path === '/' || to.path === '/register' || to.path === '/forgot-password' || to.path === '/reset-password' || to.path === '/maintenance'
  
  if (isPublicPage) {
    // 后台异步加载配置，不阻塞路由
    if (!systemStore.loaded && !systemStore.loading) {
      systemStore.fetchConfig().catch((error) => {
        console.error('加载系统配置失败:', error)
      })
    }
    next()
    return
  }

  // 对于需要权限的页面，异步加载配置但不阻塞
  if (!systemStore.loaded && !systemStore.loading) {
    // 不等待配置加载完成，先放行路由
    systemStore.fetchConfig().catch((error) => {
      console.error('加载系统配置失败:', error)
    })
  }

  const isUserRoute = to.path.startsWith('/home/user')
  const isMaintenanceRoute = to.name === 'UserMaintenance'

  // 如果配置已加载，检查维护模式
  if (systemStore.loaded) {
    if (systemStore.maintenanceMode && isUserRoute && !isMaintenanceRoute) {
      isNavigating = true
      next({ name: 'UserMaintenance' })
      setTimeout(() => { isNavigating = false }, 100)
      return
    }

    if (!systemStore.maintenanceMode && isMaintenanceRoute) {
      isNavigating = true
      next('/home/user/dashboard')
      setTimeout(() => { isNavigating = false }, 100)
      return
    }
  }
  
  // 初始化用户信息（确保刷新时能正确恢复当前标签页的用户身份）
  // 对于公开页面，不需要初始化用户信息
  if (!isPublicPage) {
    if (!userStore.isLoggedIn || !userStore.token) {
      userStore.initUser()
    }
    
    // 检查是否已登录
    if (!userStore.token) {
      // 尝试从sessionStorage获取token
      const sessionToken = sessionStorage.getItem('travel_token')
      if (!sessionToken) {
        // 如果是从其他页面跳转到登录页，直接放行
        if (to.path === '/' || to.path === '/register' || to.path === '/forgot-password' || to.path === '/reset-password') {
          next()
          return
        }
        isNavigating = true
        next('/')
        setTimeout(() => { isNavigating = false }, 100)
        return
      }
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
        console.log('管理员账号无法访问用户平台')
        isNavigating = true
        next('/home/admin/dashboard')
        setTimeout(() => { isNavigating = false }, 100)
        return
      }
      
      // 普通用户访问 /dashboard 或 /home/admin 路径时，重定向到用户平台
      if (role === 2 && (to.path.startsWith('/dashboard') || to.path.startsWith('/home/admin'))) {
        // 避免重复重定向：如果目标路径已经是正确的，直接放行
        if (from.path === '/home/user/dashboard' || to.path === '/home/user/dashboard') {
          next()
          return
        }
        console.log('普通用户尝试访问管理平台，重定向到用户平台')
        isNavigating = true
        next('/home/user/dashboard')
        setTimeout(() => { isNavigating = false }, 100)
        return
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
  
  next()
})

router.afterEach((to) => {
  NProgress.done()
  
  // 路由预加载：在空闲时预加载可能访问的页面
  if ('requestIdleCallback' in window) {
    requestIdleCallback(() => {
      preloadNextRoutes(to)
      prefetchResources(to)
    }, { timeout: 1000 })
  } else {
    // 降级方案：延迟预加载
    setTimeout(() => {
      preloadNextRoutes(to)
      prefetchResources(to)
    }, 1500)
  }
})

// 路由预加载函数
const preloadedRoutes = new Set<string>()

function preloadNextRoutes(currentRoute: any) {
  // 避免重复预加载
  if (preloadedRoutes.has(currentRoute.path)) {
    return
  }
  preloadedRoutes.add(currentRoute.path)
  
  const routeMap: Record<string, string[]> = {
    '/': ['/dashboard', '/home/user/dashboard'],
    '/dashboard': ['/dashboard/user', '/dashboard/travel-plan'],
    '/home/user/dashboard': ['/home/user/plans', '/home/user/attractions', '/home/user/collect'],
    '/home/admin/dashboard': ['/home/admin/users', '/home/admin/plans', '/home/admin/attractions']
  }
  
  const routesToPreload = routeMap[currentRoute.path] || []
  
  // 限制同时预加载的路由数量，避免影响当前页面性能
  const maxPreload = 3
  routesToPreload.slice(0, maxPreload).forEach((path, index) => {
    // 错开预加载时间，避免同时发起多个请求
    setTimeout(() => {
      const route = router.resolve(path)
      if (route && route.matched.length > 0) {
        // 预加载路由组件
        const matched = route.matched[route.matched.length - 1]
        if (matched && typeof matched.components?.default === 'function') {
          matched.components.default().catch(() => {
            // 静默处理预加载失败
          })
        }
      }
    }, index * 200) // 每个路由间隔 200ms
  })
}

// 资源预加载函数（prefetch）
function prefetchResources(currentRoute: any) {
  // 预加载可能需要的 API 资源
  const apiPrefetchMap: Record<string, string[]> = {
    '/home/user/dashboard': ['/api/user/info', '/api/statistics/dashboard'],
    '/home/admin/dashboard': ['/api/admin/statistics', '/api/user/list']
  }
  
  const apisToPrefetch = apiPrefetchMap[currentRoute.path] || []
  
  // 使用 link prefetch 预加载 API（如果浏览器支持）
  apisToPrefetch.forEach(api => {
    const link = document.createElement('link')
    link.rel = 'prefetch'
    link.href = api
    link.as = 'fetch'
    link.crossOrigin = 'anonymous'
    document.head.appendChild(link)
    
    // 5秒后移除，避免占用资源
    setTimeout(() => {
      if (link.parentNode) {
        link.parentNode.removeChild(link)
      }
    }, 5000)
  })
}

export default router
