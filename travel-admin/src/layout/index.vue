<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo">
          <div class="logo-icon" v-if="!isCollapse">🏖️</div>
          <span v-if="!isCollapse">{{ adminPlatformName }}</span>
      </div>
      <!-- 三角形装饰 -->
      <div class="triangle-decoration" style="position: absolute; right: -20px; top: 60px; width: 30px; height: 30px; background: #667eea; clip-path: polygon(0 0, 100% 0, 0 100%); z-index: 100;"></div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/travel-plan">
          <el-icon><Document /></el-icon>
          <span>攻略管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/attraction">
          <el-icon><Location /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/ai">
          <el-icon><MagicStick /></el-icon>
          <span>AI智能</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/culture">
          <el-icon><Shop /></el-icon>
          <span>文旅对接</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/system">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <!-- 左侧 -->
        <div class="header-left">
          <!-- 折叠按钮 -->
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </div>
          
          <!-- 系统标题 -->
          <div class="system-title">
            <el-icon class="title-icon"><Location /></el-icon>
            <span class="title-text">{{ adminPlatformName }}</span>
          </div>
        </div>
        
        <!-- 右侧 -->
        <div class="header-right">
          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-dropdown-trigger">
              <el-avatar :size="32" :src="userStore.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.nickname || '管理员' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  <span>系统设置</span>
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

      <!-- 内容区域 -->
      <el-main class="main-content">
        <!-- 三角形装饰 -->
        <div class="content-triangle" style="position: absolute; left: -20px; top: 85px; width: 30px; height: 30px; background: #ffffff; clip-path: polygon(100% 0, 0 0, 100% 100%); z-index: 100; box-shadow: -2px 2px 4px rgba(0, 0, 0, 0.1);"></div>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'
import { ElMessageBox } from 'element-plus'
import { 
  Fold, Expand, Setting, Odometer, User, Document, Location, 
  MagicStick, Shop, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const systemStore = useSystemStore()
const { adminPlatformName } = storeToRefs(systemStore)

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理用户下拉菜单命令
const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      // 跳转到个人中心
      router.push('/dashboard/user/profile')
      break
    case 'settings':
      // 跳转到系统设置
      router.push('/dashboard/system')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        // 调用退出登录（会先调用后端API，再清除本地存储）
        await userStore.logout()
        // 使用 replace 跳转到登录页
        await nextTick()
        try {
          await router.replace('/')
          // 等待路由组件加载完成
          await new Promise(resolve => setTimeout(resolve, 200))
          // 检查登录页面是否已加载
          const loginPage = document.querySelector('.login-page')
          const loginForm = document.querySelector('.login-form')
          if (!loginPage && !loginForm) {
            // 如果页面没有加载，强制刷新
            window.location.href = '/'
          }
        } catch {
          window.location.href = '/'
        }
      } catch {
        // 用户取消
      }
      break
  }
}

onMounted(() => {
  // 组件挂载时的初始化逻辑
})

watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    const main = document.querySelector('.main-content') as HTMLElement | null
    if (main) {
      main.scrollTo({ top: 0, left: 0, behavior: 'auto' })
    }
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' })
  }
)
</script>

<style lang="scss">
/* 全局样式 - 用于覆盖Element Plus默认样式 */

/* 优化Element Plus菜单展开动画性能 - 禁用所有复杂的过渡 */
.el-menu--inline .el-menu-item,
.el-menu--inline .el-sub-menu__title {
  transition: background-color 0.1s ease !important;
  will-change: background-color !important;
}

.el-menu--inline .el-menu {
  transition: none !important;
}

/* 禁用Element Plus的默认过渡，使用更快的动画 */
.el-menu-vertical,
.el-menu-vertical-demo,
.el-menu {
  * {
    transition: background-color 0.1s ease, color 0.1s ease !important;
  }
}

/* 子菜单展开优化 */
.el-sub-menu .el-menu {
  transition: none !important;
}

.el-sub-menu__title {
  transition: background-color 0.1s ease !important;
}

.header .admin-title {
  display: flex !important;
  align-items: center !important;
  gap: 10px !important;
  padding: 8px 20px !important;
  background: rgba(102, 126, 234, 0.08) !important;
  border-radius: 8px !important;
  border: 1px solid rgba(102, 126, 234, 0.2) !important;
  transition: all 0.3s ease !important;
  margin-right: 16px !important;
  
  &:hover {
    background: rgba(102, 126, 234, 0.12) !important;
    border-color: rgba(102, 126, 234, 0.3) !important;
  }
  
  .title-icon {
    font-size: 18px !important;
    color: #667eea !important;
  }
  
  .title-text {
    font-size: 16px !important;
    font-weight: 600 !important;
    color: #667eea !important;
    letter-spacing: 0.5px !important;
  }
}
</style>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  background: #f5f7fa;
  position: relative;
  
  // 添加整体阴影效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(
      ellipse at top,
      rgba(102, 126, 234, 0.05) 0%,
      transparent 50%
    );
    pointer-events: none;
    z-index: 0;
  }
  
  > * {
    position: relative;
    z-index: 1;
  }
}

.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  transition: width 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.15);
  overflow: visible !important;
  
  // 添加右侧边框渐变效果
  &::after {
    content: '';
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    width: 1px;
    background: linear-gradient(180deg, 
      rgba(255, 255, 255, 0.1) 0%, 
      rgba(255, 255, 255, 0.05) 50%, 
      rgba(255, 255, 255, 0.1) 100%
    );
    pointer-events: none;
  }
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.3));
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    position: relative;
    
    // Logo下方添加三角形装饰 - 向右下延伸
    &::after {
      content: '';
      position: absolute;
      bottom: -15px;
      right: 0;
      width: 0;
      height: 0;
      border-left: 15px solid rgba(102, 126, 234, 0.3);
      border-top: 15px solid transparent;
      z-index: 100;
    }
    
    .logo-icon {
      font-size: 24px;
      margin-right: 8px;
    }
  }
  
  // 侧边栏右侧三角形装饰
  .triangle-decoration {
    position: absolute;
    right: -20px;
    top: 60px;
    width: 30px;
    height: 30px;
    background: #667eea;
    clip-path: polygon(0 0, 100% 0, 0 100%);
    z-index: 100;
  }
}

// 深度选择器 - 确保Element Plus的样式被覆盖
:deep(.el-aside) {
  overflow: visible !important;
}

:deep(.el-main) {
  overflow-x: visible !important;
}

.sidebar-menu {
  border: none;
  background: transparent;
  
  :deep(.el-menu-item) {
    color: #bfcbd9;
    transition: background-color 0.15s ease, color 0.15s ease !important;
    position: relative;
    margin: 6px 10px;
    border-radius: 10px;
    font-weight: 500;
    height: 50px;
    line-height: 50px;
    
    &:hover {
      background: transparent !important;
      color: inherit !important;
      transform: none !important;
    }
    
    &.is-active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
      color: #fff !important;
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.5) !important;
      font-weight: 700 !important;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 5px;
        background: linear-gradient(180deg, #fff, rgba(255, 255, 255, 0.3));
        border-radius: 0 5px 5px 0;
        box-shadow: 2px 0 8px rgba(255, 255, 255, 0.3);
      }
      
      .el-icon {
        color: #fff !important;
        font-size: 20px !important;
      }
    }
    
    .el-icon {
      font-size: 18px;
      margin-right: 8px;
      transition: all 0.15s ease;
    }
  }
  
  // 优化子菜单展开动画
  :deep(.el-sub-menu) {
    .el-sub-menu__title {
      color: #bfcbd9;
      transition: background-color 0.15s ease, color 0.15s ease !important;
      margin: 6px 10px;
      border-radius: 10px;
      height: 50px;
      line-height: 50px;
      font-weight: 500;
      
      &:hover {
        background: transparent !important;
        color: inherit !important;
        transform: none !important;
      }
      
      .el-icon {
        font-size: 18px;
        margin-right: 8px;
      }
    }
    
    .el-menu {
      transition: none !important;
      
      // 修复子菜单项抖动问题
      .el-menu-item {
        height: 44px !important;
        line-height: 44px !important;
        padding-left: 40px !important;
        margin: 2px 10px !important;
        border-radius: 8px !important;
        color: #a0a5ad !important;
        font-size: 14px !important;
        transition: background-color 0.15s ease, color 0.15s ease !important;
        
        &:hover {
          background: transparent !important;
          color: inherit !important;
        }
        
        &.is-active {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.3)) !important;
          color: #fff !important;
          font-weight: 600 !important;
        }
      }
    }
    
    &.is-opened .el-sub-menu__title {
      color: #fff;
    }
  }
}

.header {
  background: #ffffff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .collapse-btn {
      cursor: pointer;
      padding: 8px;
      border-radius: 6px;
      color: #606266;
      transition: all 0.2s;
      font-size: 20px;
      
      &:hover {
        background: #f5f7fa;
        color: #667eea;
      }
    }
    
    .system-title {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .title-icon {
        font-size: 24px;
        color: #667eea;
      }
      
      .title-text {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    
    .user-dropdown-trigger {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 6px 12px;
      border-radius: 20px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        background: #f5f7fa;
      }
      
      .user-name {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
      }
      
      .dropdown-icon {
        font-size: 12px;
        color: #909399;
        transition: transform 0.2s;
      }
    }
    
    .el-dropdown:hover .dropdown-icon {
      transform: rotate(180deg);
    }
  }
}

.main-content {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  padding: 24px;
  position: relative;
  overflow-y: auto;
  overflow-x: visible !important;
  
  // 主内容区域三角形装饰
  .content-triangle {
    position: absolute;
    left: -20px;
    top: 85px;
    width: 30px;
    height: 30px;
    background: #ffffff;
    clip-path: polygon(100% 0, 0 0, 100% 100%);
    z-index: 100;
    box-shadow: -2px 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  // 添加左侧分割线效果
  &::after {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background: linear-gradient(180deg, 
      rgba(102, 126, 234, 0.2) 0%, 
      rgba(118, 75, 162, 0.2) 50%, 
      rgba(102, 126, 234, 0.2) 100%
    );
    border-radius: 0 3px 3px 0;
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  
  &:hover::after {
    opacity: 1;
  }
  
  // 滚动条美化
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 4px;
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
    }
  }
}
</style>
