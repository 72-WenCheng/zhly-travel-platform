import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'
import './styles/index.scss'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 统一的启动函数，避免首次渲染后又因为配置/用户初始化导致二次重绘
async function bootstrap() {
  const app = createApp(App)
  const pinia = createPinia()
  app.use(pinia)
  app.use(router)
  app.use(ElementPlus, {
    locale: zhCn,
  })

  // 预先恢复用户与系统配置，避免组件挂载后再次重绘
  const userStore = useUserStore(pinia)
  const systemStore = useSystemStore(pinia)
  userStore.initUser()
  await systemStore.fetchConfig().catch(() => {
    // 配置拉取失败不阻塞启动
    console.warn('系统配置预加载失败，继续启动')
  })

  // 常用图标列表（根据实际使用情况调整）
  const commonIcons = [
    'User', 'Setting', 'House', 'Document', 'Location', 'Search', 
    'Edit', 'Delete', 'View', 'Refresh', 'Plus', 'WarningFilled',
    'ChatDotRound', 'ChatLineRound', 'Star', 'ShoppingCart', 'Bell'
  ]

  // 只注册常用图标，减少首次加载体积
  const iconMap = new Map(Object.entries(ElementPlusIconsVue))
  commonIcons.forEach(iconName => {
    const component = iconMap.get(iconName)
    if (component) {
      app.component(iconName, component)
    }
  })

  // 其他图标延迟注册，在空闲时注册
  if ('requestIdleCallback' in window) {
    requestIdleCallback(() => {
      Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
        if (!commonIcons.includes(key)) {
          app.component(key, component)
        }
      })
    }, { timeout: 2000 })
  } else {
    setTimeout(() => {
      Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
        if (!commonIcons.includes(key)) {
          app.component(key, component)
        }
      })
    }, 500)
  }

  await router.isReady()
  app.mount('#app')
}

bootstrap()



















