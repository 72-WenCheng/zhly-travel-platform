<template>
  <div class="system-config-modern">
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Setting /></el-icon>
        </div>
        <div class="header-title">
          <h1>系统设置</h1>
          <p>管理系统各项配置参数</p>
        </div>
      </div>
      <div class="header-actions">
        <el-button
          type="success"
          class="action-btn"
          @click="handleSaveAll"
          :loading="saving"
        >
          保存设置
        </el-button>
      </div>
    </div>

    <!-- 配置标签页 -->
    <el-card class="config-card-wrapper">
      <el-tabs v-model="activeTab" class="config-tabs">
        <!-- 基本设置 -->
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="150px">
            <el-divider content-position="left">系统信息</el-divider>
            <el-form-item label="管理平台名称">
              <el-input v-model="basicSettings.adminPlatformName" placeholder="请输入管理平台名称" />
            </el-form-item>
            <el-form-item label="用户平台名称">
              <el-input v-model="basicSettings.userPlatformName" placeholder="请输入用户平台名称" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="basicSettings.systemDescription" type="textarea" :rows="3" placeholder="请输入系统描述" />
            </el-form-item>
            <el-form-item label="系统版本">
              <el-input v-model="basicSettings.systemVersion" disabled />
            </el-form-item>
            <el-form-item label="管理员邮箱">
              <el-input v-model="basicSettings.adminEmail" placeholder="请输入管理员邮箱" />
            </el-form-item>
            <el-form-item label="客服电话">
              <el-input v-model="basicSettings.servicePhone" placeholder="请输入客服电话" />
            </el-form-item>
            <el-form-item label="客服时间">
              <el-input v-model="basicSettings.contactTime" placeholder="例如：工作日 9:00-18:00" />
            </el-form-item>
            
            <el-form-item label="维护模式">
              <div style="display: flex; flex-direction: column;">
                <el-switch v-model="basicSettings.maintenanceMode" active-text="开启" inactive-text="关闭" />
                <div class="form-tip maintenance-tip">开启后，系统将进入维护模式，用户无法访问</div>
              </div>
            </el-form-item>
            <el-form-item label="维护提示">
              <el-input v-model="basicSettings.maintenanceMessage" type="textarea" :rows="2" placeholder="维护模式提示信息" />
            </el-form-item>
            
          </el-form>
        </el-tab-pane>
        
        <!-- 缓存配置 -->
        <el-tab-pane label="缓存配置" name="cache">
          <el-form :model="cacheSettings" label-width="150px">
            <el-divider content-position="left">缓存类型</el-divider>
            <el-form-item label="缓存类型">
              <el-radio-group v-model="cacheSettings.type">
                <el-radio label="redis">Redis</el-radio>
                <el-radio label="memory">内存</el-radio>
                <el-radio label="none">不使用</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-divider content-position="left" v-if="cacheSettings.type === 'redis'">Redis配置</el-divider>
            <el-form-item label="Redis地址" v-if="cacheSettings.type === 'redis'">
              <el-input v-model="cacheSettings.host" placeholder="例如：localhost" />
            </el-form-item>
            <el-form-item label="Redis端口" v-if="cacheSettings.type === 'redis'">
              <el-input-number v-model="cacheSettings.port" :min="1" :max="65535" />
            </el-form-item>
            <el-form-item label="Redis密码" v-if="cacheSettings.type === 'redis'">
              <el-input v-model="cacheSettings.password" type="password" show-password placeholder="留空表示无密码" />
            </el-form-item>
            <el-form-item label="数据库索引" v-if="cacheSettings.type === 'redis'">
              <el-input-number v-model="cacheSettings.database" :min="0" :max="15" />
            </el-form-item>
            
            <el-divider content-position="left">缓存设置</el-divider>
            <el-form-item label="默认过期时间">
              <el-input-number v-model="cacheSettings.expireTime" :min="60" :max="86400" :step="60" />
              <span style="margin-left: 8px;">秒</span>
            </el-form-item>
            
          </el-form>
        </el-tab-pane>
        
        <!-- 安全配置 -->
        <el-tab-pane label="安全配置" name="security">
          <el-form :model="securitySettings" label-width="150px">
            <el-divider content-position="left">登录安全</el-divider>
            <el-form-item label="密码最小长度">
              <el-input-number v-model="securitySettings.passwordMinLength" :min="6" :max="20" />
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-switch v-model="securitySettings.enableLoginLock" active-text="启用" inactive-text="禁用" />
            </el-form-item>
            <el-form-item label="最大失败次数">
              <el-input-number
                v-model="securitySettings.maxLoginAttempts"
                :min="3"
                :max="10"
                :disabled="!securitySettings.enableLoginLock"
              />
            </el-form-item>
            <el-form-item label="锁定时间">
              <el-input-number
                v-model="securitySettings.lockTime"
                :min="1"
                :max="120"
                :disabled="!securitySettings.enableLoginLock"
              />
              <span style="margin-left: 8px;">分钟</span>
            </el-form-item>
            
            <el-divider content-position="left">会话管理</el-divider>
            <el-form-item label="会话超时时间">
              <el-input-number v-model="securitySettings.sessionTimeout" :min="10" :max="1440" />
              <span style="margin-left: 8px;">分钟</span>
            </el-form-item>
            <el-form-item label="允许同时登录">
              <el-switch v-model="securitySettings.allowMultiLogin" active-text="允许" inactive-text="禁止" />
            </el-form-item>
            
            <el-divider content-position="left">API安全</el-divider>
            <el-form-item label="启用API限流">
              <el-switch v-model="securitySettings.enableApiRateLimit" active-text="启用" inactive-text="禁用" />
            </el-form-item>
            <el-form-item label="每分钟请求数" v-if="securitySettings.enableApiRateLimit">
              <el-input-number v-model="securitySettings.apiRateLimit" :min="10" :max="1000" />
            </el-form-item>
            
          </el-form>
        </el-tab-pane>
        
        <!-- 操作日志 -->
        <el-tab-pane label="操作日志" name="logs">
          <div class="logs-section">
            <el-card shadow="never">
              <template #header>
                <div class="logs-header">
                  <div class="logs-title">
                    <span>系统操作日志</span>
                    <span class="auto-refresh-tip">30 秒自动刷新</span>
                  </div>
                </div>
              </template>
              
              <el-table :data="logList" v-loading="loadingLogs" style="width: 100%">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="operator" label="操作人" width="120" />
                <el-table-column prop="operation" label="操作类型" width="150" />
                <el-table-column prop="module" label="操作模块" width="150" />
                <el-table-column prop="content" label="操作内容" min-width="200" show-overflow-tooltip />
                <el-table-column prop="ip" label="IP地址" width="130" />
                <el-table-column prop="createTime" label="操作时间" width="180">
                  <template #default="{ row }">
                    {{ formatDateTime(row.createTime) }}
                  </template>
                </el-table-column>
              </el-table>
              
              <div class="logs-pagination" style="margin-top: 20px;">
                <div class="logs-pagination-pages">
                  <el-button
                    class="page-btn"
                    :disabled="logPagination.page <= 1"
                    @click="handleLogPageChange(logPagination.page - 1)"
                  >
                    <el-icon><ArrowLeft /></el-icon>
                  </el-button>
                  <span class="page-info">
                    {{ logPagination.page }} / {{ Math.max(1, Math.ceil((logPagination.total || 1) / (logPagination.size || 10))) }}
                  </span>
                  <el-button
                    class="page-btn"
                    :disabled="logPagination.page >= Math.ceil((logPagination.total || 1) / (logPagination.size || 10))"
                    @click="handleLogPageChange(logPagination.page + 1)"
                  >
                    <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </div>
                <div class="logs-pagination-jump">
                  <span>前往</span>
                  <el-input
                    v-model.number="logPageJump"
                    size="small"
                    class="page-jump-input"
                    @keyup.enter="handleLogPageJump"
                    @blur="handleLogPageJump"
                  />
                  <span>页</span>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Check, Refresh, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getCacheConfig, updateCacheConfig, getSecurityConfig, updateSecurityConfig } from '@/api/systemConfig'
import BackButton from '@/components/BackButton.vue'
import dayjs from 'dayjs'

const activeTab = ref('basic')
const saving = ref(false)
const testingCache = ref(false)
const loadingLogs = ref(false)

// 基本设置
const basicSettings = reactive({
  systemName: '',
  adminPlatformName: '',
  userPlatformName: '',
  systemDescription: '',
  systemVersion: '1.0.0',
  adminEmail: '',
  servicePhone: '',
  contactTime: '',
  maintenanceMode: false,
  maintenanceMessage: '系统维护中，请稍后再试'
})

// 缓存设置
const cacheSettings = reactive({
  type: 'redis',
  host: 'localhost',
  port: 6379,
  password: '',
  database: 0,
  expireTime: 3600
})

// 安全设置
const securitySettings = reactive({
  passwordMinLength: 8,
  enableLoginLock: true,
  maxLoginAttempts: 5,
  lockTime: 30,
  sessionTimeout: 120,
  allowMultiLogin: false,
  enableApiRateLimit: true,
  apiRateLimit: 100
})

// 日志列表
const logList = ref([])
const logPagination = reactive({
  page: 1,
  size: 10,
  total: 0
})
const logPageJump = ref<number | null>(null)
const LOGS_AUTO_REFRESH_INTERVAL = 30000
const logsAutoRefreshTimer = ref<number | null>(null)

const parseBoolean = (value?: boolean | string) => {
  if (typeof value === 'boolean') return value
  if (typeof value === 'string') {
    return value === 'true' || value === '1'
  }
  return false
}

const applyBasicSettings = (data?: Record<string, any>) => {
  if (!data) return
  basicSettings.systemName = data.systemName || data.adminPlatformName || ''
  basicSettings.adminPlatformName = data.adminPlatformName || data.systemName || ''
  basicSettings.userPlatformName = data.userPlatformName || data.systemName || ''
  basicSettings.systemDescription = data.systemDescription || ''
  basicSettings.systemVersion = data.systemVersion || basicSettings.systemVersion
  basicSettings.adminEmail = data.adminEmail || data.contactEmail || ''
  basicSettings.servicePhone = data.servicePhone || data.contactPhone || ''
  basicSettings.contactTime = data.contactTime || data.serviceHours || ''
  basicSettings.maintenanceMode = parseBoolean(data.maintenanceMode)
  basicSettings.maintenanceMessage = data.maintenanceMessage || basicSettings.maintenanceMessage
}

// 加载系统配置
const loadSystemConfig = async () => {
  try {
    const res = await request.get('/admin/system-config/system')
    if (res.code === 200 && res.data) {
      applyBasicSettings(res.data)
    }
  } catch (error) {
    console.error('加载系统配置失败:', error)
  }
}

const applyCacheSettings = (data?: Record<string, any>) => {
  if (!data) return
  cacheSettings.type = data.type || 'redis'
  cacheSettings.host = data.host || 'localhost'
  cacheSettings.port = Number(data.port ?? 6379)
  cacheSettings.password = data.password || ''
  cacheSettings.database = Number(data.database ?? 0)
  cacheSettings.expireTime = Number(data.expireTime ?? 3600)
}

// 加载缓存配置
const loadCacheConfig = async () => {
  try {
    const res = await getCacheConfig()
    if (res.code === 200 && res.data) {
      applyCacheSettings(res.data)
    }
  } catch (error) {
    console.error('加载缓存配置失败:', error)
  }
}

const applySecuritySettings = (data?: Record<string, any>) => {
  if (!data) return
  const toNumber = (value: unknown, fallback: number) => {
    const n = Number(value)
    return Number.isFinite(n) ? n : fallback
  }
  securitySettings.passwordMinLength = toNumber(data.passwordMinLength, 6)
  securitySettings.enableLoginLock = parseBoolean(data.enableLoginLock ?? false)
  securitySettings.maxLoginAttempts = toNumber(data.maxLoginAttempts, 5)
  securitySettings.lockTime = toNumber(data.lockTime, 30)
  securitySettings.sessionTimeout = toNumber(data.sessionTimeout, 120)
  securitySettings.allowMultiLogin = parseBoolean(data.allowMultiLogin ?? false)
  securitySettings.enableApiRateLimit = parseBoolean(data.enableApiRateLimit ?? true)
  securitySettings.apiRateLimit = toNumber(data.apiRateLimit, 100)
}

// 加载安全配置
const loadSecurityConfig = async () => {
  try {
    const res = await getSecurityConfig()
    if (res.code === 200 && res.data) {
      applySecuritySettings(res.data)
    }
  } catch (error) {
    console.error('加载安全配置失败:', error)
  }
}

// 加载所有配置
const loadAllConfigs = async () => {
  await Promise.all([loadSystemConfig(), loadCacheConfig(), loadSecurityConfig()])
}

// 保存基本设置
const saveBasicSettings = async () => {
  saving.value = true
  try {
    const payload = {
      systemName: basicSettings.adminPlatformName || basicSettings.systemName,
      adminPlatformName: basicSettings.adminPlatformName,
      userPlatformName: basicSettings.userPlatformName,
      systemDescription: basicSettings.systemDescription,
      adminEmail: basicSettings.adminEmail,
      servicePhone: basicSettings.servicePhone,
      contactTime: basicSettings.contactTime,
      maintenanceMode: basicSettings.maintenanceMode,
      maintenanceMessage: basicSettings.maintenanceMessage
    }
    const res = await request.post('/admin/system-config/system', payload)
    if (res.code === 200) {
      ElMessage.success('基本设置保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 保存缓存设置
const saveCacheSettings = async () => {
  saving.value = true
  try {
    const payload = {
      type: cacheSettings.type,
      host: cacheSettings.host,
      port: cacheSettings.port,
      password: cacheSettings.password,
      database: cacheSettings.database,
      expireTime: cacheSettings.expireTime
    }
    const res = await updateCacheConfig(payload)
    if (res.code === 200) {
      ElMessage.success('缓存设置保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 保存安全设置
const saveSecuritySettings = async () => {
  saving.value = true
  try {
    const payload = {
      passwordMinLength: securitySettings.passwordMinLength,
      enableLoginLock: securitySettings.enableLoginLock,
      maxLoginAttempts: securitySettings.maxLoginAttempts,
      lockTime: securitySettings.lockTime,
      sessionTimeout: securitySettings.sessionTimeout,
      allowMultiLogin: securitySettings.allowMultiLogin,
      enableApiRateLimit: securitySettings.enableApiRateLimit,
      apiRateLimit: securitySettings.apiRateLimit
    }
    const res = await updateSecurityConfig(payload)
    if (res.code === 200) {
      ElMessage.success('安全设置保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 保存所有配置
const handleSaveAll = async () => {
  saving.value = true
  try {
    await Promise.all([
      saveBasicSettings(),
      saveCacheSettings(),
      saveSecuritySettings()
    ])
    ElMessage.success('所有配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 重置配置
const handleReset = () => {
  loadAllConfigs()
  ElMessage.info('配置已重置')
}

// 测试缓存连接
const testCacheConnection = async () => {
  testingCache.value = true
  try {
    const res = await request.post('/admin/system-config/cache/test')
    if (res.code === 200) {
      ElMessage.success('缓存连接测试成功')
    } else {
      ElMessage.error(res.message || '缓存连接测试失败')
    }
  } catch (error) {
    ElMessage.error('缓存连接测试失败')
  } finally {
    testingCache.value = false
  }
}

// 加载日志
const loadLogs = async () => {
  loadingLogs.value = true
  try {
    const res = await request.get('/admin/system-config/logs', {
      params: {
        page: logPagination.page,
        size: logPagination.size
      }
    })
    if (res.code === 200) {
      logList.value = (res.data.list || []).map((item: any) => ({
        id: item.id,
        operator: item.operator || '系统',
        operation: item.operationType || '',
        module: item.operationModule || '',
        content: item.operationContent || '',
        ip: item.operationIp || '',
        createTime: item.createTime || item.operationTime
      }))
      logPagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载日志失败')
  } finally {
    loadingLogs.value = false
  }
}

const startLogsAutoRefresh = () => {
  stopLogsAutoRefresh()
  logsAutoRefreshTimer.value = window.setInterval(() => {
    if (activeTab.value === 'logs') {
      loadLogs()
    }
  }, LOGS_AUTO_REFRESH_INTERVAL)
}

const stopLogsAutoRefresh = () => {
  if (logsAutoRefreshTimer.value) {
    clearInterval(logsAutoRefreshTimer.value)
    logsAutoRefreshTimer.value = null
  }
}

// 日志分页变化
const handleLogSizeChange = (size: number) => {
  logPagination.size = size
  logPagination.page = 1
  loadLogs()
}

const handleLogPageChange = (page: number) => {
  logPagination.page = page
  loadLogs()
}

const handleLogPageJump = () => {
  const totalPages = Math.max(1, Math.ceil((logPagination.total || 1) / (logPagination.size || 10)))
  let target = Number(logPageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === logPagination.page) return
  handleLogPageChange(target)
}

// 格式化时间
const formatDateTime = (time: string) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'logs') {
    loadLogs()
    startLogsAutoRefresh()
  } else {
    stopLogsAutoRefresh()
  }
})

// 页面加载
onMounted(() => {
  loadAllConfigs()
  // 如果当前在日志标签页，加载日志
  if (activeTab.value === 'logs') {
    loadLogs()
    startLogsAutoRefresh()
  }
})

onUnmounted(() => {
  stopLogsAutoRefresh()
})
</script>

<style lang="scss" scoped>
.system-config-modern {
  padding: 24px;
  background: #f5f6f8;
  min-height: calc(100vh - 60px);

  .page-header-modern {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 24px 28px;
    background: #fff;
    border-radius: 16px;
    border: 1px solid #e5e7eb;

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
      align-items: center;
      gap: 12px;
      flex-shrink: 0;

      .action-btn {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        border-radius: 6px;
        border: 1px solid #dcdfe6;
        background: #fff;
        color: #606266;
        font-weight: 400;
        font-size: 14px;
        box-shadow: none;
        transition: all 0.2s ease;

        .el-icon {
          font-size: 16px;
          color: #606266;
        }

        &:hover {
          border-color: #c0c4cc;
          color: #606266;
          background: #fff;
        }

        &[type="success"] {
          border-color: #dcdfe6;
          color: #606266;
          background: #fff;

          .el-icon {
            color: #606266;
          }

          &:hover {
            background: #fff;
            border-color: #c0c4cc;
            color: #606266;
          }
        }
      }
    }
  }

  .config-card-wrapper {
    border-radius: 16px;
    border: 1px solid #e5e7eb;
    box-shadow: none;
    background: #fff;

    :deep(.el-card__body) {
      padding: 0;
    }
  }

  .config-tabs {
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
          bottom: 8px;
          height: 2px;
          background: #d0d4db;
          border-radius: 2px;
        }
      }
    }

    :deep(.el-tabs__content) {
      padding: 28px 36px;
      background: #fff;
    }
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

    :deep(.el-input__wrapper),
    :deep(.el-select .el-input__wrapper),
    :deep(.el-input-number .el-input__wrapper),
    :deep(.el-textarea__inner) {
      border-radius: 10px;
      border: 1px solid #e1e4ea;
      box-shadow: none;
      background: #f3f5fa;

      &.is-focus,
      &:focus {
        border-color: #cfd4dd;
        box-shadow: none;
        background: #fff;
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
    font-size: 12px;
    color: #9aa0ad;
    margin-top: 4px;
    line-height: 1.5;
  }

  .maintenance-tip {
    margin-top: 12px;
    margin-left: 0;
  }

  .logs-section {
    .logs-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .logs-title {
      display: flex;
      align-items: center;
      gap: 12px;

      span:first-child {
        font-weight: 600;
        color: #2f3542;
      }

      .auto-refresh-tip {
        font-size: 12px;
        color: #909399;
      }
    }

    :deep(.el-card) {
      border: 1px solid #e5e7eb;
      box-shadow: none;
      .el-card__body {
        padding-bottom: 12px;
      }
    }

    :deep(.el-table) {
      border-radius: 12px;
      overflow: hidden;
    }

    :deep(.el-pagination) {
      display: flex;
      justify-content: center;
      .el-pagination__editor {
        .el-input__inner {
          background-color: transparent;
          box-shadow: none;
          border: none;
        }
      }
    }

    .logs-pagination {
      margin-bottom: 0 !important;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 16px;
      flex-wrap: wrap;
    }
    
    .logs-pagination-pages {
      display: inline-flex;
      align-items: center;
      gap: 12px;
    }
    
    .logs-pagination-jump {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: #909399;

      // 去掉“前往第几页”输入框的灰色背景，统一为白色扁平样式
      :deep(.el-input__inner) {
        background-color: #fff;
        box-shadow: none;
      }

      // 缩小“前往第几页”输入框的宽度
      .page-jump-input {
        width: 60px;
      }
    }
  }
}
</style>

<style lang="scss">
// 简化按钮样式
.system-config-modern {
  .simple-btn {
    padding: 8px 20px !important;
    border-radius: 6px !important;
    font-size: 14px !important;
    border: 1px solid #d7dae0 !important;
    background: #fff !important;
    color: #5f6673 !important;
    box-shadow: none !important;
    min-width: auto !important;
    height: auto !important;

    &.el-button--primary,
    &.el-button--default,
    &.el-button--success,
    &.el-button--info,
    &.el-button--warning,
    &.el-button--danger {
      background: #fff !important;
      border: 1px solid #d7dae0 !important;
      color: #5f6673 !important;
    }

    .el-icon {
      display: none !important;
    }

    &::before {
      display: none !important;
    }

    &:hover {
      border-color: #caced6 !important;
    }

    &:active {
      border-color: #caced6 !important;
    }
  }

  .header-actions {
    .el-button:not(.simple-btn) {
      padding: 8px 20px !important;
      border-radius: 6px !important;
      font-size: 14px !important;
      border: 1px solid #d7dae0 !important;
      background: #fff !important;
      color: #5f6673 !important;
      box-shadow: none !important;

      &.el-button--primary {
        background: #fff !important;
        border: 1px solid #d7dae0 !important;
        color: #5f6673 !important;
      }

      .el-icon {
        display: none !important;
      }

      &:hover {
        background: #f6f7f9 !important;
        border-color: #caced6 !important;
      }
    }
  }
}
</style>





























