<template>
  <div class="admin-list-modern">
    <BackButton />

    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><WarningFilled /></el-icon>
        </div>
        <div class="header-title">
          <h1>举报审核</h1>
          <p>处理用户举报，维护社区环境</p>
          <div class="status-info">
            <div class="status-text">
              <el-icon class="status-icon"><Refresh /></el-icon>
              <span>数据每30秒自动刷新</span>
            </div>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon pending">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待处理</div>
          <div class="stat-value">{{ stats.pending || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon resolved">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已处理</div>
          <div class="stat-value">{{ stats.resolved || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rejected">
          <el-icon :size="24"><CircleClose /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已驳回</div>
          <div class="stat-value">{{ stats.rejected || 0 }}</div>
        </div>
      </div>
    </div>

    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Filter /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row single">
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="选择状态" clearable>
              <el-option label="全部状态" :value="null" />
              <el-option label="待处理" :value="0" />
              <el-option label="已处理" :value="2" />
              <el-option label="已驳回" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table
          :data="reportList"
          v-loading="loading"
          class="modern-table"
          :row-class-name="getRowClassName"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="reporterId" label="举报人ID" width="120" align="center" />
          <el-table-column label="举报类型" width="120" align="center">
            <template #default>
              <el-tag :type="getReportTypeColor()" size="small">
                {{ getReportTypeLabel() }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="被举报评论" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">
              评论ID：{{ row.reportedId }}
            </template>
          </el-table-column>
          <el-table-column label="举报原因" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.reason || '未填写' }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="举报时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleViewDetail(row)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container-modern simple-pagination">
        <el-button
          class="page-btn"
          :disabled="pagination.page <= 1"
          @click="handlePageChange(pagination.page - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="page-info">
          {{ pagination.page }} / {{ Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10))) }}
        </span>
        <el-button
          class="page-btn"
          :disabled="pagination.page >= Math.ceil((pagination.total || 1) / (pagination.size || 10))"
          @click="handlePageChange(pagination.page + 1)"
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

    <el-dialog
      v-model="detailDialogVisible"
      title="举报详情"
      width="820px"
    >
      <div v-if="detailData.report" class="detail-grid">
        <el-card shadow="never" class="detail-card">
          <div class="detail-card-title">举报信息</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="举报ID">{{ detailData.report.id }}</el-descriptions-item>
            <el-descriptions-item label="举报人ID">{{ detailData.report.reporterId }}</el-descriptions-item>
            <el-descriptions-item label="举报原因">
              {{ detailData.report.reason || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="举报时间">
              {{ formatDateTime(detailData.report.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(detailData.report.status)">
                {{ getStatusLabel(detailData.report.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理说明" v-if="detailData.report.handleResult">
              {{ detailData.report.handleResult }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="detail-card">
          <div class="detail-card-title">被举报评论</div>
          <div v-if="detailData.comment" class="comment-preview">
            <div class="comment-meta">
              <span>评论ID：{{ detailData.comment.id }}</span>
              <span>用户ID：{{ detailData.comment.userId }}</span>
              <span>时间：{{ formatDateTime(detailData.comment.createdTime) }}</span>
            </div>
            <el-input
              type="textarea"
              :rows="5"
              :model-value="detailData.comment.content"
              disabled
            />
          </div>
          <el-empty v-else description="评论已删除或不存在" />
        </el-card>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="white-btn" @click="detailDialogVisible = false">取消</el-button>
          <el-button
            class="white-btn"
            :disabled="!canOperate"
            @click="confirmProcess('REJECT')"
          >
            驳回
          </el-button>
          <el-button
            class="white-btn"
            :disabled="!canOperate"
            @click="confirmProcess('RESOLVE')"
          >
            处理并删除评论
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  WarningFilled,
  Refresh,
  Filter,
  Clock,
  CircleCheck,
  CircleClose,
  View
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import BackButton from '@/components/BackButton.vue'
import dayjs from 'dayjs'
import { resolvePaginationTotal } from '@/utils/pagination'

const REPORT_TYPE_COMMENT = 3

const loading = ref(false)
const reportList = ref<any[]>([])
const lastUpdateTime = ref('')
let autoRefreshTimer: any = null

const stats = reactive({
  pending: 0,
  resolved: 0,
  rejected: 0
})

const filters = reactive({
  status: 0 as number | null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 翻页跳转
const pageJump = ref(null)

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 20)))
  let target = Number(pageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === pagination.page) return
  handlePageChange(target)
}

const detailDialogVisible = ref(false)
const detailData = reactive<{ report: any; comment: any }>({
  report: null,
  comment: null
})

const canOperate = computed(() => detailData.report && detailData.report.status === 0)

const loadStats = async () => {
  try {
    const res = await request.get('/admin/report/stats')
    if (res.code === 200) {
      stats.pending = res.data.pending || 0
      stats.resolved = res.data.resolved || 0
      stats.rejected = res.data.rejected || 0
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const loadReports = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size,
      reportedType: REPORT_TYPE_COMMENT
    }
    if (filters.status !== null) {
      params.status = filters.status
    }

    const res = await request.get('/admin/report/page', { params })
    if (res.code === 200 && res.data) {
      const list = res.data.list || res.data.records || []
      const resolvedTotal = resolvePaginationTotal(res.data, list)

      if (list.length > pagination.size && resolvedTotal <= list.length) {
        const start = (pagination.page - 1) * pagination.size
        reportList.value = list.slice(start, start + pagination.size)
        pagination.total = list.length
      } else {
        reportList.value = list
        pagination.total = resolvedTotal
      }
      lastUpdateTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
    }
  } catch (error) {
    ElMessage.error('加载举报列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  filters.status = 0
  pagination.page = 1
  loadReports()
}

let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [filters.status],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.page = 1
      loadReports()
    }, 400)
  }
)

const handleViewDetail = async (row: any) => {
  try {
    const res = await request.get(`/admin/report/${row.id}`)
    if (res.code === 200) {
      detailData.report = res.data.report
      detailData.comment = res.data.comment
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const confirmProcess = async (action: 'RESOLVE' | 'REJECT') => {
  if (!detailData.report) {
    ElMessage.error('未获取举报详情')
    return
  }
  if (!canOperate.value) {
    ElMessage.warning('该举报已处理')
    return
  }

  const confirmText = action === 'RESOLVE'
    ? '确认处理该举报并删除评论？'
    : '确认驳回该举报？'

  try {
    await ElMessageBox.confirm(confirmText, '提示', {
      type: action === 'RESOLVE' ? 'warning' : 'info'
    })
    const res = await request.post(`/admin/report/${detailData.report.id}/handle`, {
      handlerId: 1,
      action
    })
    if (res.code === 200) {
      ElMessage.success(action === 'RESOLVE' ? '已处理并删除评论' : '已驳回举报')
      detailDialogVisible.value = false
      loadReports()
      loadStats()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadReports()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadReports()
}

const formatDateTime = (time: string) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

const getReportTypeLabel = () => '评论'
const getReportTypeColor = () => 'success'

const getStatusLabel = (status: number) => {
  const map: Record<number, string> = {
    0: '待处理',
    2: '已处理',
    3: '已驳回'
  }
  return map[status] || '未知'
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    2: 'success',
    3: 'info'
  }
  return map[status] || 'info'
}

const getRowClassName = ({ row }: any) => (row.status === 0 ? 'warning-row' : '')

onMounted(() => {
  loadStats()
  loadReports()
  lastUpdateTime.value = dayjs().format('HH:mm:ss')
  autoRefreshTimer = setInterval(() => {
    loadStats()
    loadReports()
    lastUpdateTime.value = dayjs().format('HH:mm:ss')
  }, 30000)
})

onUnmounted(() => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
  }
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';

.filter-row.single {
  gap: 24px;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.resolved {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.detail-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-card-title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 12px;
  color: #334155;
}

.comment-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #94a3b8;
}

// 覆盖本页表格悬停效果（去掉放大和阴影）
:deep(.modern-table .el-table__body tr:hover) {
  background: #f5f7fa !important;
  transform: none !important;
  box-shadow: none !important;
}

// 举报详情对话框按钮白色系样式
:deep(.dialog-footer .white-btn) {
  padding: 10px 20px !important;
  border-radius: 4px !important;
  font-weight: 500 !important;
  font-size: 14px !important;
  transition: border-color 0.3s ease !important;
  min-width: 100px !important;
  background: #ffffff !important;
  background-color: #ffffff !important;
  border: 1px solid #dcdfe6 !important;
  border-color: #dcdfe6 !important;
  color: #606266 !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: none !important;
  outline: none !important;
  
  // 覆盖 Element Plus 的所有可能类型和状态
  &,
  &.el-button--primary,
  &.el-button--default,
  &.el-button--success,
  &.el-button--info,
  &.el-button--warning,
  &.el-button--danger {
    background: #ffffff !important;
    background-color: #ffffff !important;
    border: 1px solid #dcdfe6 !important;
    border-color: #dcdfe6 !important;
    color: #606266 !important;
    box-shadow: none !important;
  }
  
  // 覆盖所有 hover 状态 - 灰色边框加深
  &:hover:not([disabled]),
  &.el-button--primary:hover:not([disabled]),
  &.el-button--default:hover:not([disabled]),
  &.el-button--warning:hover:not([disabled]),
  &.el-button--danger:hover:not([disabled]) {
    background: #ffffff !important;
    background-color: #ffffff !important;
    border-color: #909399 !important;
    color: #606266 !important;
    box-shadow: none !important;
    transform: none !important;
  }
  
  // 覆盖所有 active 状态
  &:active:not([disabled]),
  &.el-button--primary:active:not([disabled]),
  &.el-button--default:active:not([disabled]),
  &.el-button--warning:active:not([disabled]),
  &.el-button--danger:active:not([disabled]) {
    background: #ffffff !important;
    background-color: #ffffff !important;
    border-color: #909399 !important;
    color: #606266 !important;
    transform: none !important;
    box-shadow: none !important;
  }
  
  // 覆盖所有 focus 状态
  &:focus:not([disabled]),
  &:focus-visible:not([disabled]),
  &.el-button--primary:focus:not([disabled]),
  &.el-button--default:focus:not([disabled]),
  &.el-button--warning:focus:not([disabled]),
  &.el-button--danger:focus:not([disabled]) {
    background: #ffffff !important;
    background-color: #ffffff !important;
    border-color: #909399 !important;
    color: #606266 !important;
    box-shadow: none !important;
    outline: none !important;
  }
  
  // 覆盖 disabled 状态
  &[disabled],
  &.is-disabled {
    color: #c0c4cc !important;
    border-color: #e4e7ed !important;
    background: #ffffff !important;
    background-color: #ffffff !important;
    cursor: not-allowed;
    
    &:hover {
      border-color: #e4e7ed !important;
      background: #ffffff !important;
      background-color: #ffffff !important;
    }
  }
}
</style>