<template>
  <div class="admin-list-modern">
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Coin /></el-icon>
        </div>
        <div class="header-title">
          <h1>积分管理</h1>
          <p>管理用户积分和配置积分规则</p>
          <div class="status-info">
            <div class="status-text">
              <el-icon class="status-icon"><Refresh /></el-icon>
              <span>数据每30秒自动刷新</span>
            </div>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="action-btn" @click="showAdjustDialog">
          <el-icon><Edit /></el-icon>
          手动调整积分
        </el-button>
      </div>
    </div>

    <!-- 积分统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总用户数</div>
          <div class="stat-value">{{ stats.totalUsers }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Coin /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总积分发放</div>
          <div class="stat-value">{{ formatNumber(stats.totalPointsIssued) }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">今日发放</div>
          <div class="stat-value">{{ stats.todayPointsIssued }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><DataLine /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">平均积分</div>
          <div class="stat-value">{{ stats.averagePoints || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" class="modern-tabs">
      <!-- 用户积分列表 -->
      <el-tab-pane label="用户积分" name="users">
        <!-- 筛选 -->
        <el-card class="filter-card-modern">
          <div class="filter-header">
            <el-icon><Filter /></el-icon>
            筛选条件
          </div>
          <el-form inline class="filter-form">
            <div class="filter-row">
              <el-form-item label="用户ID">
                <el-input v-model="userFilter.userId" placeholder="输入用户ID" clearable />
              </el-form-item>
              <el-form-item label="等级">
                <el-select v-model="userFilter.levelCode" placeholder="选择等级" clearable>
                  <el-option label="全部等级" :value="null" />
                  <el-option label="青铜旅行者" :value="1" />
                  <el-option label="白银探索者" :value="2" />
                  <el-option label="黄金游侠" :value="3" />
                  <el-option label="铂金旅者" :value="4" />
                  <el-option label="钻石达人" :value="5" />
                  <el-option label="王者导师" :value="6" />
                </el-select>
              </el-form-item>
              <el-form-item class="filter-actions">
                <el-button class="reset-btn" @click="resetUserFilter">
                  重置筛选
                </el-button>
              </el-form-item>
            </div>
          </el-form>
        </el-card>

        <!-- 批量操作 -->
        <transition name="slide-down">
          <el-card class="batch-card-modern" v-if="selectedUserRows.length > 0" shadow="never">
            <div class="batch-actions-modern">
              <div class="batch-info-modern">
                <el-icon :size="20"><Select /></el-icon>
                <span>已选择 <strong>{{ selectedUserRows.length }}</strong> 个用户</span>
              </div>
              <div class="batch-buttons">
                <el-button type="danger" size="small" @click="handleBatchDeleteUsers">
                  <el-icon><Delete /></el-icon>
                  批量删除
                </el-button>
                <el-button size="small" @click="clearUserSelection">
                  <el-icon><CloseBold /></el-icon>
                  取消选择
                </el-button>
              </div>
            </div>
          </el-card>
        </transition>

        <!-- 用户积分表格 -->
        <el-card class="table-card-modern">
          <el-table 
            :data="userPointsList" 
            class="modern-table"
            @selection-change="handleUserSelectionChange"
            ref="userTableRef"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column label="用户ID" prop="userId" width="100" />
            <el-table-column label="等级" width="200">
              <template #default="{ row }">
                <div class="level-cell">
                  <div class="level-icon" :style="{ background: getLevelColor(row.levelCode) }">
                    <el-icon><Trophy /></el-icon>
                  </div>
                  <div>
                    <div class="level-name" :style="{ color: getLevelColor(row.levelCode) }">
                      {{ row.levelName }}
                    </div>
                    <div class="level-code">LV.{{ row.levelCode }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="总积分" prop="totalPoints" width="120" sortable />
            <el-table-column label="可用积分" prop="availablePoints" width="120" sortable />
            <el-table-column label="冻结积分" prop="frozenPoints" width="120" />
            <el-table-column label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="更新时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.updateTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button link type="primary" @click="viewUserLogs(row.userId)">
                    <el-icon><Document /></el-icon>
                    明细
                  </el-button>
                  <el-button link type="warning" @click="adjustPoints(row)">
                    <el-icon><Edit /></el-icon>
                    调整
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container-modern simple-pagination">
            <el-button
              class="page-btn"
              :disabled="userPagination.page <= 1"
              @click="handleUserPageChange(userPagination.page - 1)"
            >
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
            <span class="page-info">
              {{ userPagination.page }} / {{ Math.max(1, Math.ceil((userPagination.total || 1) / (userPagination.limit || 20))) }}
            </span>
            <el-button
              class="page-btn"
              :disabled="userPagination.page >= Math.ceil((userPagination.total || 1) / (userPagination.limit || 20))"
              @click="handleUserPageChange(userPagination.page + 1)"
            >
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <div class="page-jump">
              <span>前往</span>
              <el-input
                v-model.number="userPageJump"
                size="small"
                class="page-jump-input"
                @input="handleUserPageJump"
              />
              <span>页</span>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 积分明细 -->
      <el-tab-pane label="积分明细" name="logs">
        <!-- 筛选 -->
        <el-card class="filter-card-modern">
          <div class="filter-header">
            <el-icon><Filter /></el-icon>
            筛选条件
          </div>
          <el-form inline class="filter-form">
            <div class="filter-row">
              <el-form-item label="用户ID">
                <el-input v-model="logFilter.userId" placeholder="输入用户ID" clearable />
              </el-form-item>
              <el-form-item label="行为类型">
                <el-select v-model="logFilter.actionType" placeholder="选择行为" clearable>
                  <el-option label="全部类型" :value="null" />
                  <el-option label="登录" :value="1" />
                  <el-option label="发布攻略" :value="2" />
                  <el-option label="评论" :value="3" />
                  <el-option label="点赞" :value="4" />
                  <el-option label="收藏" :value="5" />
                  <el-option label="分享" :value="6" />
                  <el-option label="完善资料" :value="7" />
                  <el-option label="邀请好友" :value="8" />
                  <el-option label="消费" :value="9" />
                  <el-option label="管理员调整" :value="10" />
                </el-select>
              </el-form-item>
              <el-form-item class="filter-actions">
                <el-button class="reset-btn" @click="resetLogFilter">
                  重置筛选
                </el-button>
              </el-form-item>
            </div>
          </el-form>
        </el-card>

        <!-- 积分明细表格 -->
        <el-card class="table-card-modern">
          <el-table :data="pointsLogsList" class="modern-table">
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="用户ID" prop="userId" width="100" />
            <el-table-column label="积分变动" width="120">
              <template #default="{ row }">
                <span :class="row.points > 0 ? 'points-add' : 'points-minus'">
                  {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="行为类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getActionTypeTag(row.actionType)">
                  {{ getActionTypeName(row.actionType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="行为描述" prop="actionDesc" min-width="200" />
            <el-table-column label="关联类型" prop="relatedType" width="100" />
            <el-table-column label="关联ID" prop="relatedId" width="100" />
            <el-table-column label="变动后余额" prop="balanceAfter" width="120" />
            <el-table-column label="时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container-modern simple-pagination">
            <el-button
              class="page-btn"
              :disabled="logPagination.page <= 1"
              @click="handleLogPageChange(logPagination.page - 1)"
            >
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
            <span class="page-info">
              {{ logPagination.page }} / {{ Math.max(1, Math.ceil((logPagination.total || 1) / (logPagination.limit || 20))) }}
            </span>
            <el-button
              class="page-btn"
              :disabled="logPagination.page >= Math.ceil((logPagination.total || 1) / (logPagination.limit || 20))"
              @click="handleLogPageChange(logPagination.page + 1)"
            >
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <div class="page-jump">
              <span>前往</span>
              <el-input
                v-model.number="logPageJump"
                size="small"
                class="page-jump-input"
                @input="handleLogPageJump"
              />
              <span>页</span>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 调整积分对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      width="500px"
      class="points-adjust-dialog"
    >
      <template #header>
        <span style="font-size: 18px; font-weight: 600;">调整用户积分</span>
      </template>
      <el-form :model="adjustForm" label-width="100px" class="edit-form">
        <el-form-item label="用户ID">
          <el-input v-model="adjustForm.userId" placeholder="输入用户ID" />
        </el-form-item>
        <el-form-item label="积分变动">
          <el-input-number 
            v-model="adjustForm.points" 
            :min="-10000" 
            :max="10000"
            placeholder="正数增加，负数减少"
          />
          <span class="form-tip">正数为增加，负数为扣除</span>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input 
            v-model="adjustForm.reason" 
            type="textarea" 
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="white-btn" @click="adjustDialogVisible = false">取消</el-button>
        <el-button class="white-btn" @click="submitAdjust">
          确认调整
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Coin, Edit, User, TrendCharts, DataLine, Filter, Search, 
  Refresh, Trophy, Document, Check, Select, Delete, CloseBold,
  ArrowLeft, ArrowRight
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { resolvePaginationTotal } from '@/utils/pagination'

const activeTab = ref('users')

// 统计数据
const stats = ref({
  totalUsers: 0,
  totalPointsIssued: 0,
  todayPointsIssued: 0
})

// 用户积分列表
const userPointsList = ref([])
const selectedUserRows = ref([])
const userTableRef = ref()
const userFilter = ref({
  userId: null,
  levelCode: null
})
const userPagination = ref({
  page: 1,
  limit: 10,
  total: 0
})

const userPageJump = ref(null)

const handleUserPageChange = page => {
  userPagination.value.page = page
  loadUserPoints()
}

const handleUserPageJump = () => {
  const totalPages = Math.max(1, Math.ceil((userPagination.value.total || 1) / (userPagination.value.limit || 20)))
  let target = Number(userPageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === userPagination.value.page) return
  handleUserPageChange(target)
}

// 积分明细列表
const pointsLogsList = ref([])
const logFilter = ref({
  userId: null,
  actionType: null
})
const logPagination = ref({
  page: 1,
  limit: 10,
  total: 0
})

// 日志翻页跳转
const logPageJump = ref(null)

const handleLogPageChange = page => {
  logPagination.value.page = page
  loadPointsLogs()
}

const handleLogPageJump = () => {
  const totalPages = Math.max(1, Math.ceil((logPagination.value.total || 1) / (logPagination.value.limit || 20)))
  let target = Number(logPageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === logPagination.value.page) return
  handleLogPageChange(target)
}

// 调整积分
const adjustDialogVisible = ref(false)
const adjustForm = ref({
  userId: null,
  points: 0,
  reason: ''
})

// 等级颜色
const levelColors = {
  1: '#CD7F32',
  2: '#C0C0C0',
  3: '#FFD700',
  4: '#E5E4E2',
  5: '#B9F2FF',
  6: '#FF4500'
}

const getLevelColor = (levelCode) => {
  return levelColors[levelCode] || '#909399'
}

// 行为类型
const actionTypeNames = {
  1: '登录',
  2: '发布攻略',
  3: '评论',
  4: '点赞',
  5: '收藏',
  6: '分享',
  7: '完善资料',
  8: '邀请好友',
  9: '消费',
  10: '管理员调整'
}

const getActionTypeName = (type) => {
  return actionTypeNames[type] || '未知'
}

const getActionTypeTag = (type) => {
  const tagMap = {
    1: '',
    2: 'success',
    3: 'primary',
    4: 'warning',
    5: 'info',
    6: 'success',
    7: 'primary',
    8: 'danger',
    9: 'warning',
    10: 'danger'
  }
  return tagMap[type] || ''
}

// 加载统计
const loadStats = async () => {
  try {
    const res = await request.get('/admin/points/stats')
    if (res.code === 200) {
      stats.value = {
        totalUsers: res.data.totalUsers || 0,
        totalPointsIssued: res.data.totalPointsIssued || 0,
        todayPointsIssued: res.data.todayPointsIssued || 0,
        averagePoints: res.data.averagePoints || 0
      }
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    }
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

// 加载用户积分列表
const loadUserPoints = async () => {
  try {
    const res = await request.get('/admin/points/list', {
      params: {
        page: userPagination.value.page,
        limit: userPagination.value.limit,
        ...userFilter.value
      }
    })
    if (res.code === 200) {
      const list = res.data.records || res.data.list || []
      const resolvedTotal = resolvePaginationTotal(res.data, list)
      if (list.length > userPagination.value.limit && resolvedTotal <= list.length) {
        const start = (userPagination.value.page - 1) * userPagination.value.limit
        userPointsList.value = list.slice(start, start + userPagination.value.limit)
        userPagination.value.total = list.length
      } else {
        userPointsList.value = list
        userPagination.value.total = resolvedTotal
      }
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    }
  } catch (error) {
    ElMessage.error('加载用户积分列表失败')
  }
}

// 加载积分明细
const loadPointsLogs = async () => {
  try {
    const res = await request.get('/admin/points/log', {
      params: {
        page: logPagination.value.page,
        limit: logPagination.value.limit,
        ...logFilter.value
      }
    })
    if (res.code === 200) {
      const list = res.data.records || res.data.list || []
      const resolvedTotal = resolvePaginationTotal(res.data, list)
      if (list.length > logPagination.value.limit && resolvedTotal <= list.length) {
        const start = (logPagination.value.page - 1) * logPagination.value.limit
        pointsLogsList.value = list.slice(start, start + logPagination.value.limit)
        logPagination.value.total = list.length
      } else {
        pointsLogsList.value = list
        logPagination.value.total = resolvedTotal
      }
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    }
  } catch (error) {
    ElMessage.error('加载积分明细失败')
  }
}

// 重置筛选
const resetUserFilter = () => {
  userFilter.value = {
    userId: null,
    levelCode: null
  }
  loadUserPoints()
}

const resetLogFilter = () => {
  logFilter.value = {
    userId: null,
    actionType: null
  }
  loadPointsLogs()
}

// 查看用户明细
const viewUserLogs = (userId) => {
  activeTab.value = 'logs'
  logFilter.value.userId = userId
  loadPointsLogs()
}

// 用户选择
const handleUserSelectionChange = (rows) => {
  selectedUserRows.value = rows
}

// 清除用户选择
const clearUserSelection = () => {
  selectedUserRows.value = []
  userTableRef.value?.clearSelection()
}

// 批量删除用户积分
const handleBatchDeleteUsers = async () => {
  if (selectedUserRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUserRows.value.length} 个用户的积分记录吗？`,
      '批量删除',
      { type: 'warning' }
    )
    
    const userIds = selectedUserRows.value.map(row => row.userId)
    const response = await request.delete('/admin/points/batch', { data: userIds })
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      clearUserSelection()
      loadUserPoints()
      loadStats()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 调整积分
const showAdjustDialog = () => {
  adjustForm.value = {
    userId: null,
    points: 0,
    reason: ''
  }
  adjustDialogVisible.value = true
}

const adjustPoints = (row) => {
  adjustForm.value = {
    userId: row.userId,
    points: 0,
    reason: ''
  }
  adjustDialogVisible.value = true
}

const submitAdjust = async () => {
  if (!adjustForm.value.userId) {
    ElMessage.warning('请输入用户ID')
    return
  }
  if (adjustForm.value.points === 0) {
    ElMessage.warning('请输入积分变动数量')
    return
  }
  if (!adjustForm.value.reason) {
    ElMessage.warning('请输入调整原因')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认${adjustForm.value.points > 0 ? '增加' : '扣除'} ${Math.abs(adjustForm.value.points)} 积分？`,
      '确认操作',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await request.post('/admin/points/adjust', adjustForm.value)
    if (res.code === 200) {
      ElMessage.success('积分调整成功')
      adjustDialogVisible.value = false
      loadUserPoints()
      loadStats()
      loadPointsLogs()
    } else {
      ElMessage.error(res.message || '积分调整失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('积分调整失败')
    }
  }
}

// 格式化
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

// 格式化当前时间（用于显示最后刷新时间）
const formatCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

// 防抖函数
let userFilterTimer = null
let logFilterTimer = null

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref(null)
const lastUpdateTime = ref('')

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新积分管理数据...')
    loadStats()  // 刷新统计数据
    loadUserPoints()  // 刷新用户积分列表
    loadPointsLogs()  // 刷新积分明细列表
  }, refreshInterval.value)
  console.log(`自动刷新已启动，间隔: ${refreshInterval.value / 1000}秒`)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
    autoRefreshTimer.value = null
    console.log('自动刷新已停止')
  }
}

// 监听用户积分筛选条件变化，自动触发搜索
watch(
  () => [userFilter.value.userId, userFilter.value.levelCode],
  () => {
    if (userFilterTimer) {
      clearTimeout(userFilterTimer)
    }
    userFilterTimer = setTimeout(() => {
      userPagination.value.page = 1
      loadUserPoints()
    }, 500)
  },
  { deep: true }
)

// 监听积分明细筛选条件变化，自动触发搜索
watch(
  () => [logFilter.value.userId, logFilter.value.actionType],
  () => {
    if (logFilterTimer) {
      clearTimeout(logFilterTimer)
    }
    logFilterTimer = setTimeout(() => {
      logPagination.value.page = 1
      loadPointsLogs()
    }, 500)
  },
  { deep: true }
)

onMounted(() => {
  loadStats()
  loadUserPoints()
  loadPointsLogs()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';

// 积分调整对话框样式 - 移除标题下方的线和按钮图标
.points-adjust-dialog {
  .el-dialog__header {
    padding: 20px 20px 0 !important;
    border-bottom: none !important;
    border: none !important;
    border-bottom-width: 0 !important;
    margin-bottom: 0 !important;
    
    &::after,
    &::before {
      content: none !important;
      display: none !important;
      height: 0 !important;
      width: 0 !important;
      border: none !important;
      background: none !important;
    }
    
    .el-dialog__title {
      border-bottom: none !important;
      padding-bottom: 0 !important;
      margin-bottom: 0 !important;
    }
  }
  
  .el-dialog__footer {
    // 白色系按钮样式
    .white-btn {
      background: #ffffff;
      border: 1px solid #dcdfe6;
      color: #606266;
      padding: 6px 16px;
      min-width: 80px;
      font-size: 13px;
      border-radius: 4px;
      transition: all 0.3s;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      vertical-align: middle;
      line-height: 1.5;
      
      &:hover {
        background: #f5f7fa;
        border-color: #c0c4cc;
        color: #303133;
      }
      
      &:active {
        background: #f0f2f5;
      }
      
      .el-icon,
      i,
      svg {
        display: none !important;
        visibility: hidden !important;
        width: 0 !important;
        height: 0 !important;
      }
      
      // 确保文字垂直居中
      span {
        display: inline-block;
        line-height: 1.5;
        vertical-align: middle;
      }
    }
    
    .el-button {
      padding: 6px 16px;
      min-width: 80px;
      font-size: 13px;
      border-radius: 4px;
    }
  }
  
  .el-dialog__body {
    padding-top: 12px !important;
  }
}

// 全局覆盖
.el-overlay .points-adjust-dialog .el-dialog .el-dialog__header {
  border-bottom: none !important;
  border-bottom-width: 0 !important;
  border-bottom-style: none !important;
  
  &::after,
  &::before {
    display: none !important;
    content: none !important;
  }
}
</style>

<style lang="scss" scoped>
.modern-tabs {
  margin-top: 20px;
  
  :deep(.el-tabs__header) {
    background: white;
    padding: 20px 20px 0;
    border-radius: 16px 16px 0 0;
    margin-bottom: 0;
  }
  
  :deep(.el-tabs__item) {
    font-size: 16px;
    font-weight: 600;
    padding: 0 30px;
    height: 50px;
    line-height: 50px;
  }
  
  :deep(.el-tabs__content) {
    padding: 0;
    margin-top: 0;
  }
  
  // tab-pane 与筛选卡片之间的间距
  :deep(.el-tab-pane) {
    .filter-card-modern {
      margin-top: 24px;
      margin-bottom: 24px;
      border-radius: 20px;
    }
  }
}

.level-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .level-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;
  }
  
  .level-name {
    font-size: 14px;
    font-weight: 600;
  }
  
  .level-code {
    font-size: 12px;
    color: #909399;
  }
}

.points-add {
  color: #67c23a;
  font-weight: 700;
  font-size: 16px;
}

.points-minus {
  color: #f56c6c;
  font-weight: 700;
  font-size: 16px;
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

.dialog-header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  margin-bottom: 20px;
}

/* 取消积分管理列表“明细 / 调整”操作按钮的悬停模糊、放大等效果 */
:deep(.action-buttons .el-button) {
  transition: none !important;
}

:deep(.action-buttons .el-button:hover),
:deep(.action-buttons .el-button:focus) {
  transform: none !important;
  box-shadow: none !important;
  background-color: transparent !important;
}
</style>







