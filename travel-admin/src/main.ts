import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'
import './styles/index.scss'

const app = createApp(App)

// 优化：图标按需注册（unplugin-vue-components 已处理组件按需导入）
// 只注册常用的图标，其他图标在使用时动态注册
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

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

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

app.mount('#app')



















