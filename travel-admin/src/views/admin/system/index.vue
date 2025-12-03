<template>
  <div class="system-config-modern">
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Setting /></el-icon>
        </div>
        <div class="header-title">
          <h1>系统配置管理</h1>
          <p>管理系统各项配置参数</p>
        </div>
      </div>
      <div class="header-actions">
        <el-button class="simple-btn" @click="handleSave">保存设置</el-button>
      </div>
    </div>

    <!-- 配置标签页 -->
    <el-tabs v-model="activeTab" class="config-tabs">
      <!-- 系统配置 -->
      <el-tab-pane label="系统配置" name="system">
        <el-card class="config-card-modern">
          <el-form :model="systemConfig" label-width="120px">
            <el-form-item label="管理平台名称">
              <el-input v-model="systemConfig.adminPlatformName" placeholder="请输入管理平台名称" />
            </el-form-item>
            <el-form-item label="用户平台名称">
              <el-input v-model="systemConfig.userPlatformName" placeholder="请输入用户平台名称" />
            </el-form-item>
            <el-form-item label="系统版本">
              <el-input v-model="systemConfig.systemVersion" placeholder="系统版本来自后端配置" disabled />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input
                v-model="systemConfig.systemDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>
            <el-form-item label="管理员邮箱">
              <el-input v-model="systemConfig.adminEmail" placeholder="请输入管理员邮箱" />
            </el-form-item>
            <el-form-item label="客服电话">
              <el-input v-model="systemConfig.servicePhone" placeholder="请输入客服电话" />
            </el-form-item>
            <el-form-item label="客服时间">
              <el-input v-model="systemConfig.contactTime" placeholder="请输入客服服务时间，例如：工作日 9:00-18:00" />
            </el-form-item>
            <el-form-item label="维护模式">
              <el-switch v-model="systemConfig.maintenanceMode" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="维护提示">
              <el-input
                v-model="systemConfig.maintenanceMessage"
                type="textarea"
                :rows="2"
                placeholder="请输入维护模式提示信息"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 数据库配置 -->
      <el-tab-pane label="数据库配置" name="database">
        <el-card class="config-card-modern">
          <el-form :model="databaseConfig" label-width="120px">
            <el-form-item label="数据库类型">
              <el-select v-model="databaseConfig.type" placeholder="请选择数据库类型">
                <el-option label="MySQL" value="mysql" />
                <el-option label="PostgreSQL" value="postgresql" />
                <el-option label="SQLite" value="sqlite" />
              </el-select>
            </el-form-item>
            <el-form-item label="数据库地址">
              <el-input v-model="databaseConfig.host" placeholder="请输入数据库地址" />
            </el-form-item>
            <el-form-item label="端口号">
              <el-input-number v-model="databaseConfig.port" :min="1" :max="65535" />
            </el-form-item>
            <el-form-item label="数据库名">
              <el-input v-model="databaseConfig.database" placeholder="请输入数据库名" />
            </el-form-item>
            <el-form-item label="用户名">
              <el-input v-model="databaseConfig.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="databaseConfig.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item label="连接池大小">
              <el-input-number v-model="databaseConfig.poolSize" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="连接超时">
              <el-input-number v-model="databaseConfig.timeout" :min="1000" :max="60000" />
              <span class="form-tip">毫秒</span>
            </el-form-item>
            <el-form-item label="启用连接池">
              <el-switch v-model="databaseConfig.enablePool" active-text="启用" inactive-text="禁用" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 缓存配置 -->
      <el-tab-pane label="缓存配置" name="cache">
        <el-card class="config-card-modern">
          <el-form :model="cacheConfig" label-width="120px">
            <el-form-item label="缓存类型">
              <el-select v-model="cacheConfig.type" placeholder="请选择缓存类型">
                <el-option label="Redis" value="redis" />
                <el-option label="内存" value="memory" />
                <el-option label="文件" value="file" />
              </el-select>
            </el-form-item>
            <el-form-item label="Redis地址">
              <el-input v-model="cacheConfig.host" placeholder="请输入Redis地址" />
            </el-form-item>
            <el-form-item label="Redis端口">
              <el-input-number v-model="cacheConfig.port" :min="1" :max="65535" />
            </el-form-item>
            <el-form-item label="Redis密码">
              <el-input v-model="cacheConfig.password" type="password" placeholder="请输入Redis密码" show-password />
            </el-form-item>
            <el-form-item label="数据库索引">
              <el-input-number v-model="cacheConfig.database" :min="0" :max="15" />
            </el-form-item>
            <el-form-item label="过期时间(秒)">
              <el-input-number v-model="cacheConfig.expireTime" :min="60" :max="86400" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import { getSystemConfig, updateSystemConfig } from '@/api/systemConfig'
import type { SystemConfigData } from '@/api/systemConfig'

// 当前标签页
const activeTab = ref('system')

// 系统配置
const systemConfig = reactive<SystemConfigData>({
  systemName: '',
  adminPlatformName: '',
  userPlatformName: '',
  systemDescription: '',
  systemVersion: '',
  adminEmail: '',
  servicePhone: '',
  contactTime: '',
  contactEmail: '',
  contactPhone: '',
  maintenanceMode: false,
  maintenanceMessage: ''
})

// 数据库配置
const databaseConfig = reactive({
  type: 'mysql',
  host: 'localhost',
  port: 3306,
  database: 'travel_system',
  username: 'root',
  password: '',
  poolSize: 10,
  timeout: 30000,
  enablePool: true
})

// 缓存配置
const cacheConfig = reactive({
  type: 'redis',
  host: 'localhost',
  port: 6379,
  password: '',
  database: 0,
  expireTime: 3600
})

const parseBoolean = (value?: boolean | string) => {
  if (typeof value === 'boolean') return value
  if (typeof value === 'string') {
    return value === 'true' || value === '1'
  }
  return false
}

const applySystemConfig = (data?: Partial<SystemConfigData>) => {
  if (!data) return
  systemConfig.systemName = data.systemName || data.adminPlatformName || ''
  systemConfig.adminPlatformName = data.adminPlatformName || data.systemName || ''
  systemConfig.userPlatformName = data.userPlatformName || data.systemName || ''
  systemConfig.systemDescription = data.systemDescription || ''
  systemConfig.systemVersion = data.systemVersion || ''
  systemConfig.adminEmail = data.adminEmail || data.contactEmail || ''
  systemConfig.servicePhone = data.servicePhone || data.contactPhone || ''
  systemConfig.contactTime = data.contactTime || data.serviceHours || ''
  systemConfig.contactEmail = data.contactEmail || data.adminEmail || ''
  systemConfig.contactPhone = data.contactPhone || data.servicePhone || ''
  systemConfig.maintenanceMode = parseBoolean(data.maintenanceMode)
  systemConfig.maintenanceMessage = data.maintenanceMessage || ''
}

// 加载系统配置
const loadSystemConfig = async () => {
  try {
    const result = await getSystemConfig()
    applySystemConfig(result.data)
  } catch (error) {
    ElMessage.error('加载系统配置失败')
  }
}

// 保存配置
const handleSave = async () => {
  try {
    if (activeTab.value === 'system') {
      const payload = {
        ...systemConfig,
        systemName: systemConfig.adminPlatformName || systemConfig.systemName
      }
      await updateSystemConfig(payload)
      ElMessage.success('系统配置保存成功')
    } else {
      ElMessage.info('其他配置保存功能开发中...')
    }
  } catch (error) {
    ElMessage.error('保存配置失败')
  }
}

// 重置配置
const handleReset = () => {
  if (activeTab.value === 'system') {
    loadSystemConfig()
  }
  ElMessage.info('配置已重置')
}

onMounted(() => {
  loadSystemConfig()
})
</script>

<style lang="scss" scoped>
.system-config-modern {
  padding: 24px;
  background: #f5f6f8;
  min-height: calc(100vh - 60px);

  // 页面头部
  .page-header-modern {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 24px 28px;
    background: #fff;
    border-radius: 16px;
    border: 1px solid #e5e7eb;
    box-shadow: none;

    .header-left {
      display: flex;
      align-items: center;
      gap: 18px;

      .header-icon {
        width: 60px;
        height: 60px;
        border-radius: 16px;
        background: #f1f2f4;
        border: 1px solid #e3e5e8;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #5f6673;
      }

      .header-title {
        h1 {
          margin: 0 0 6px 0;
          font-size: 24px;
          font-weight: 600;
          color: #2f3542;
        }

        p {
          margin: 0;
          color: #8a8f99;
          font-size: 14px;
        }
      }
    }

    .header-actions {
      display: flex;
      gap: 12px;

      :deep(.el-button) {
        padding: 8px 20px !important;
        border-radius: 6px !important;
        font-size: 14px !important;
        border: 1px solid #d7dae0 !important;
        background: #fff !important;
        color: #5f6673 !important;

        &:hover {
          background: #f6f7f9 !important;
          border-color: #caced6 !important;
        }
      }
    }
  }

  // 配置标签页
  .config-tabs {
    border-radius: 16px;
    overflow: hidden;
    border: 1px solid #e5e7eb;
    background: #fff;

    // 去掉切换标签时底部默认的蓝色指示条
    :deep(.el-tabs__active-bar) {
      display: none !important;
      height: 0 !important;
      background: transparent !important;
      transform: none !important;
    }

    :deep(.el-tabs__nav-wrap::after) {
      display: none !important;
    }

    :deep(.el-tabs__header) {
      background: #f8f9fb;
      margin: 0;
      padding: 0 16px;
      border-bottom: 1px solid #e5e7eb;
    }

    :deep(.el-tabs__item) {
      height: 56px;
      line-height: 56px;
      font-size: 15px;
      font-weight: 600;
      color: #7b818c;
      padding: 0 28px;
      position: relative;

      &:hover {
        color: #4d5562;
      }

      &.is-active {
        color: #2f3542;

        &::after {
          content: '';
          position: absolute;
          left: 24px;
          right: 24px;
          bottom: 10px;
          height: 2px;
          background: #d0d4db;
          border-radius: 2px;
        }
      }
    }

    :deep(.el-tabs__content) {
      padding: 0;
      background: #fff;
    }
  }

  // 配置卡片
  .config-card-modern {
    margin: 0;
    border-radius: 0;
    border: none;
    background: #fff;

    :deep(.el-card__body) {
      padding: 28px 36px;
    }

    .el-form-item {
      margin-bottom: 24px;

      :deep(.el-form-item__label) {
        font-weight: 500;
        color: #4d5562;
        font-size: 14px;
        pointer-events: none;
        user-select: none;
      }

      :deep(.el-button) {
        padding: 8px 20px !important;
        border-radius: 6px !important;
        border: 1px solid #d7dae0 !important;
        background: #fff !important;
        color: #5f6673 !important;

        &:hover {
          background: #f6f7f9 !important;
          border-color: #caced6 !important;
        }
      }

      :deep(.el-input__wrapper),
      :deep(.el-select .el-input__wrapper),
      :deep(.el-input-number .el-input__wrapper) {
        border-radius: 10px;
        border: 1px solid #e1e4ea;
        box-shadow: none;
        background: #f3f5fa;

        &.is-focus {
          border-color: #cfd4dd;
          box-shadow: none;
          background: #fff;
        }
      }

      :deep(.el-slider) {
        .el-slider__runway {
          background-color: #e2e6ed;
          border-radius: 4px;
        }

        .el-slider__bar {
          background: #bfc5cf;
        }

        .el-slider__button {
          border: 2px solid #d5dae3;
          background: #fff;
          box-shadow: none;
        }
      }

      :deep(.el-switch) {
        .el-switch__core {
          width: 48px;
          height: 24px;
          border-radius: 999px;
          border: 1px solid #dcdfe6;
          background-color: #f0f1f4;

          &::after {
            width: 20px;
            height: 20px;
            background: #fff;
            box-shadow: none;
          }
        }

        &.is-checked .el-switch__core {
          border-color: #cfd4dd;
          background-color: #dfe2e8;
        }
      }

      :deep(.el-checkbox) {
        margin-right: 24px;

        .el-checkbox__inner {
          border-radius: 4px;
          border: 1px solid #dfe2e8;
          background: #fff;
        }

        &.is-checked .el-checkbox__inner {
          border-color: #c5cad4;
          background: #eceff3;
        }

        .el-checkbox__label {
          color: #4d5562;
        }
      }
    }

    .form-tip {
      margin-left: 12px;
      color: #a0a5b0;
      font-size: 13px;
    }
  }
}
</style>

<style lang="scss">
// 覆盖全局按钮样式，使用更高优先级和自定义类名
.system-config-modern {
  // 使用自定义类名，确保优先级最高
  .simple-btn {
    padding: 8px 16px !important;
    border-radius: 4px !important;
    font-weight: 400 !important;
    font-size: 14px !important;
    border: 1px solid #dcdfe6 !important;
    background: #fff !important;
    color: #606266 !important;
    box-shadow: none !important;
    min-width: auto !important;
    height: auto !important;
    
    // 覆盖所有可能的类型
    &.el-button--primary,
    &.el-button--default,
    &.el-button--success,
    &.el-button--info,
    &.el-button--warning,
    &.el-button--danger {
      background: #fff !important;
      border: 1px solid #dcdfe6 !important;
      color: #606266 !important;
    }
    
    // 移除所有图标
    .el-icon {
      display: none !important;
    }
    
    // 移除图标后的间距
    &::before {
      display: none !important;
    }
    
    &:hover {
      background: #f5f7fa !important;
      border-color: #c0c4cc !important;
      transform: none !important;
      box-shadow: none !important;
    }
    
    &:active {
      background: #f5f7fa !important;
      border-color: #c0c4cc !important;
    }
    
    &:focus {
      background: #fff !important;
      border-color: #dcdfe6 !important;
    }
  }
  
  // 也覆盖没有类名的按钮（备用）
  .page-header-modern {
    .header-actions {
      .el-button:not(.simple-btn) {
        padding: 8px 16px !important;
        border-radius: 4px !important;
        font-weight: 400 !important;
        font-size: 14px !important;
        border: 1px solid #dcdfe6 !important;
        background: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        min-width: auto !important;
        height: auto !important;
        
        &.el-button--primary {
          background: #fff !important;
          border: 1px solid #dcdfe6 !important;
          color: #606266 !important;
        }
        
        &:hover {
          background: #f5f7fa !important;
          border-color: #c0c4cc !important;
          transform: none !important;
          box-shadow: none !important;
        }
      }
    }
  }
  
  .config-card-modern {
    .el-form-item {
      .el-button:not(.simple-btn) {
        padding: 8px 16px !important;
        border-radius: 4px !important;
        font-weight: 400 !important;
        font-size: 14px !important;
        border: 1px solid #dcdfe6 !important;
        background: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        min-width: auto !important;
        height: auto !important;
        
        &:hover {
          background: #f5f7fa !important;
          border-color: #c0c4cc !important;
          transform: none !important;
          box-shadow: none !important;
        }
      }
    }
  }
}

.system-config-modern {
  .config-tabs {
    .el-tabs__active-bar {
      display: none !important;
      height: 0 !important;
      background: transparent !important;
      transform: none !important;
    }

    .el-tabs__nav-wrap::after {
      display: none !important;
    }
  }
}
</style>