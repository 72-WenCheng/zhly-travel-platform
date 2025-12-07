<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Clock /></el-icon>
        </div>
        <div class="header-title">
          <h1>浏览历史管理</h1>
          <p>管理系统用户浏览记录与行为数据</p>
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
        <el-button type="primary" class="action-btn" @click="exportHistory">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :style="{ background: stat.color }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">{{ stat.value }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="searchForm" class="filter-form">
        <!-- 第一行：三个筛选条件 + 重置按钮 -->
        <div class="filter-row">
          <el-form-item label="用户ID">
            <el-input 
              v-model.number="searchForm.userId" 
              placeholder="请输入用户ID" 
              clearable
            />
          </el-form-item>
          <el-form-item label="浏览类型">
            <el-select v-model="searchForm.browseType" placeholder="请选择浏览类型" clearable>
              <el-option label="景点" :value="1" />
              <el-option label="攻略" :value="2" />
              <el-option label="文旅项目" :value="3" />
              <el-option label="活动" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="请输入标题或图片关键词" 
              clearable
            />
          </el-form-item>
          <el-form-item label=" " class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedHistory.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedHistory.length }}</strong> 条记录</span>
          </div>
          <div class="batch-buttons">
            <el-button type="danger" size="small" @click="batchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button size="small" @click="clearSelection">
              <el-icon><CloseBold /></el-icon>
              取消选择
            </el-button>
          </div>
        </div>
      </el-card>
    </transition>

    <!-- 浏览历史列表 -->
    <el-card class="table-card-modern history-table-card" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="historyList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="userId" label="用户ID" width="100" align="center" />
          <el-table-column prop="browseType" label="浏览类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getBrowseTypeTag(row.browseType)" size="small">
                {{ getBrowseTypeName(row.browseType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="browseId" label="对象ID" width="100" align="center" />
          <el-table-column prop="browseTitle" label="标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="browseImage" label="图片" width="100" align="center">
            <template #default="{ row }">
              <el-image
                v-if="row.browseImage"
                :src="row.browseImage"
                :preview-src-list="[row.browseImage]"
                fit="cover"
                style="width: 60px; height: 60px; border-radius: 4px;"
                :preview-teleported="true"
              />
              <span v-else style="color: #999;">无图片</span>
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="停留时长" width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.duration && row.duration > 0">
                {{ formatDuration(row.duration) }}
              </span>
              <span v-else style="color: #999;">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="浏览时间" width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" size="small" text @click="viewDetail(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button type="danger" size="small" text @click="deleteHistory(row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container-modern simple-pagination">
        <el-button
          class="page-btn"
          :disabled="pagination.current <= 1"
          @click="handleCurrentChange(pagination.current - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="page-info">
          {{ pagination.current }} / {{ Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10))) }}
        </span>
        <el-button
          class="page-btn"
          :disabled="pagination.current >= Math.ceil((pagination.total || 1) / (pagination.size || 10))"
          @click="handleCurrentChange(pagination.current + 1)"
        >
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <div class="page-jump">
          <span>前往</span>
          <el-input
            v-model.number="pageJump"
            size="small"
            class="page-jump-input"
            @input="handlePageJump"
          />
          <span>页</span>
        </div>
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="浏览历史详情"
      width="600px"
    >
      <div v-if="currentHistory" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ currentHistory.id }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentHistory.userId }}</el-descriptions-item>
          <el-descriptions-item label="浏览类型">
            <el-tag :type="getBrowseTypeTag(currentHistory.browseType)" size="small">
              {{ getBrowseTypeName(currentHistory.browseType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="对象ID">{{ currentHistory.browseId }}</el-descriptions-item>
          <el-descriptions-item label="标题" :span="2">{{ currentHistory.browseTitle || '--' }}</el-descriptions-item>
          <el-descriptions-item label="停留时长" :span="2">
            <span v-if="currentHistory.duration && currentHistory.duration > 0">
              {{ formatDuration(currentHistory.duration) }}
            </span>
            <span v-else>--</span>
          </el-descriptions-item>
          <el-descriptions-item label="浏览时间" :span="2">
            {{ formatDateTime(currentHistory.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="图片" :span="2" v-if="currentHistory.browseImage">
            <el-image
              :src="currentHistory.browseImage"
              :preview-src-list="[currentHistory.browseImage]"
              fit="cover"
              style="width: 200px; height: 200px; border-radius: 4px;"
              :preview-teleported="true"
            />
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button class="white-btn" @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Refresh, Download, Search, Select, CloseBold, View, Delete, User } from '@element-plus/icons-vue'
import request from '@/utils/request'
import BackButton from '@/components/BackButton.vue'
import { resolvePaginationTotal } from '@/utils/pagination'

// 数据
const loading = ref(false)
const historyList = ref<any[]>([])
const selectedHistory = ref<any[]>([])
const detailDialogVisible = ref(false)
const currentHistory = ref<any>(null)
const lastUpdateTime = ref('')

// 统计信息
const stats = ref([
  { label: '总浏览记录', value: 0, icon: 'Clock', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '今日浏览', value: 0, icon: 'Calendar', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '平均停留时长', value: '0秒', icon: 'Timer', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '活跃用户', value: 0, icon: 'User', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
])

// 搜索表单
const searchForm = reactive({
  userId: null as number | null,
  browseType: null as number | null,
  keyword: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 翻页跳转
const pageJump = ref(null)

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10)))
  let target = Number(pageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === pagination.current) return
  handleCurrentChange(target)
}

// 自动刷新定时器
let refreshTimer: any = null

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.userId) {
      params.userId = searchForm.userId
    }
    
    if (searchForm.browseType !== null) {
      params.browseType = searchForm.browseType
    }
    
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    
    const response = await request.get('/admin/browse-history/page', { params })
    
    if (response.code === 200 && response.data) {
      const data = response.data
      const list = data.records || data.list || []
      const resolvedTotal = resolvePaginationTotal(data, list)

      if (list.length > pagination.size && resolvedTotal <= list.length) {
        const start = (pagination.current - 1) * pagination.size
        historyList.value = list.slice(start, start + pagination.size)
        pagination.total = list.length
      } else {
        historyList.value = list
        pagination.total = resolvedTotal
      }

      lastUpdateTime.value = new Date().toLocaleString('zh-CN')
    } else {
      ElMessage.error(response.message || '获取浏览历史失败')
    }
  } catch (error: any) {
    console.error('加载浏览历史失败:', error)
    ElMessage.error('加载浏览历史失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStats = async () => {
  try {
    const response = await request.get('/admin/browse-history/stats')
    
    if (response.code === 200 && response.data) {
      const data = response.data
      stats.value[0].value = data.totalCount || 0
      stats.value[1].value = data.todayCount || 0
      
      // 格式化平均停留时长
      if (data.avgDuration) {
        const avgSeconds = Math.round(data.avgDuration)
        stats.value[2].value = formatDuration(avgSeconds)
      } else {
        stats.value[2].value = '0秒'
      }
      
      // 活跃用户数（从类型统计中计算）
      if (data.typeStats && Array.isArray(data.typeStats)) {
        // 这里可以根据实际需求计算活跃用户数
        stats.value[3].value = data.totalCount || 0
      }
    }
  } catch (error: any) {
    console.error('加载统计信息失败:', error)
  }
}

// 重置筛选
const handleReset = () => {
  searchForm.userId = null
  searchForm.browseType = null
  searchForm.keyword = ''
  pagination.current = 1
  loadData()
}

// 自动检索（防抖）
let searchTimer: any = null
watch(
  () => [searchForm.userId, searchForm.browseType, searchForm.keyword],
  () => {
    if (searchTimer) {
      clearTimeout(searchTimer)
    }
    searchTimer = setTimeout(() => {
      pagination.current = 1
      loadData()
    }, 500)
  },
  { deep: true }
)

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadData()
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedHistory.value = selection
}

// 清除选择
const clearSelection = () => {
  selectedHistory.value = []
}

// 查看详情
const viewDetail = (row: any) => {
  currentHistory.value = row
  detailDialogVisible.value = true
}

// 删除
const deleteHistory = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条浏览记录吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await request.delete(`/admin/browse-history/${row.id}`)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 批量删除
const batchDelete = async () => {
  if (selectedHistory.value.length === 0) {
    ElMessage.warning('请先选择要删除的记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedHistory.value.length} 条记录吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedHistory.value.map(item => item.id)
    const response = await request.delete('/admin/browse-history/batch', { data: ids })
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      clearSelection()
      loadData()
      loadStats()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 导出数据
const exportHistory = () => {
  ElMessage.info('导出功能开发中...')
}

// 格式化方法
const getBrowseTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '景点',
    2: '攻略',
    3: '文旅项目',
    4: '活动'
  }
  return typeMap[type] || '未知'
}

const getBrowseTypeTag = (type: number) => {
  const tagMap: Record<number, string> = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'info'
  }
  return tagMap[type] || ''
}

const formatDuration = (seconds: number) => {
  if (seconds < 60) {
    return `${seconds}秒`
  } else if (seconds < 3600) {
    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    return secs > 0 ? `${minutes}分${secs}秒` : `${minutes}分钟`
  } else {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return minutes > 0 ? `${hours}小时${minutes}分钟` : `${hours}小时`
  }
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '--'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 行样式，和用户管理保持一致的奇偶行背景 + 悬停效果
const getRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 自动刷新
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    loadData()
    loadStats()
  }, 30000) // 30秒刷新一次
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 生命周期
onMounted(() => {
  loadData()
  loadStats()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';

.detail-content {
  padding: 20px 0;
}

// 仅关闭本页表格的行悬停效果：不改变背景、不放大、不加阴影
.history-table-card .modern-table {
  :deep(.el-table__body tr:hover) {
    background: inherit !important;
    transform: none !important;
    box-shadow: none !important;
  }
}

// 白色系按钮样式
.white-btn {
  background: #ffffff !important;
  border: 1px solid #dcdfe6 !important;
  color: #606266 !important;
  padding: 6px 16px !important;
  min-width: 80px !important;
  font-size: 13px !important;
  border-radius: 4px !important;
  transition: all 0.3s !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  vertical-align: middle !important;
  line-height: 1.5 !important;
  
  &:hover:not([disabled]) {
    background: #f5f7fa !important;
    border-color: #c0c4cc !important;
    color: #303133 !important;
  }
  
  &:active:not([disabled]) {
    background: #f0f2f5 !important;
  }
  
  &[disabled] {
    color: #c0c4cc !important;
    border-color: #e4e7ed !important;
    background: #ffffff !important;
  }
}

</style>



