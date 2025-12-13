<template>
  <div class="admin-list-modern">
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Tickets /></el-icon>
        </div>
        <div class="header-title">
          <h1>预约管理</h1>
          <p>管理文化体验的预约信息</p>
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
        <el-button type="success" class="action-btn" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 筛选卡片 -->
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row">
          <el-form-item label="预约编号">
            <el-input
              v-model="filters.bookingNo"
              placeholder="请输入预约编号"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="体验名称">
            <el-input
              v-model="filters.experienceName"
              placeholder="请输入体验名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="预约状态">
            <el-select v-model="filters.status" placeholder="请选择预约状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待确认" :value="1" />
              <el-option label="已确认" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付状态">
            <el-select v-model="filters.paymentStatus" placeholder="请选择支付状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="未支付" :value="1" />
              <el-option label="已支付" :value="2" />
              <el-option label="已退款" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="体验日期">
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="体验场次">
            <el-select v-model="filters.timeSlot" placeholder="请选择体验场次" clearable>
              <el-option label="全部" value="" />
              <el-option label="上午场" value="morning" />
              <el-option label="下午场" value="afternoon" />
              <el-option label="夜场" value="night" />
            </el-select>
          </el-form-item>
          <el-form-item label="联系人">
            <el-input
              v-model="filters.contactName"
              placeholder="请输入联系人姓名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input
              v-model="filters.contactPhone"
              placeholder="请输入联系电话"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待确认</div>
          <div class="stat-value">{{ stats.pending }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已确认</div>
          <div class="stat-value">{{ stats.confirmed }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已完成</div>
          <div class="stat-value">{{ stats.completed }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总金额</div>
          <div class="stat-value">¥{{ stats.totalAmount }}</div>
        </div>
      </div>
    </div>

    <!-- 预约列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table
          :data="appointmentList"
          v-loading="loading"
          class="modern-table"
          :row-class-name="getRowClassName"
        >
        <el-table-column prop="bookingNo" label="预约编号" width="180" fixed="left" />
        <el-table-column label="体验信息" width="280">
          <template #default="{ row }">
            <div class="experience-info">
              <el-image
                :src="row.experienceImage"
                fit="cover"
                class="experience-image"
              />
              <div class="experience-details">
                <div class="experience-name">{{ row.experienceName }}</div>
                <el-tag type="primary" size="small">文化体验</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预约信息" width="200">
          <template #default="{ row }">
            <div class="appointment-info">
              <div>日期：{{ row.bookingDate }}</div>
              <div>场次：{{ getTimeSlotName(row.bookingTime) }}</div>
              <div>人数：{{ row.peopleCount }}人</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            <div class="amount">¥{{ row.totalAmount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
              {{ getPaymentStatusName(row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预约状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            <span>{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="info" size="small" text @click="handleViewDetail(row)">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="success"
                size="small"
                text
                @click="handleConfirm(row)"
              >
                <el-icon><Check /></el-icon>
                确认
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="warning"
                size="small"
                text
                @click="handleComplete(row)"
              >
                <el-icon><CircleCheck /></el-icon>
                完成
              </el-button>
              <el-button
                v-if="row.status === 1 || row.status === 2"
                type="danger"
                size="small"
                text
                @click="handleCancel(row)"
              >
                <el-icon><Close /></el-icon>
                取消
              </el-button>
            </div>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="800px">
      <div v-if="currentAppointment" class="appointment-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预约编号">{{ currentAppointment.bookingNo }}</el-descriptions-item>
          <el-descriptions-item label="预约类型">
            <el-tag type="primary" size="small">文化体验</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="体验名称" :span="2">{{ currentAppointment.experienceName }}</el-descriptions-item>
          <el-descriptions-item label="体验日期">{{ currentAppointment.bookingDate }}</el-descriptions-item>
          <el-descriptions-item label="体验场次">{{ getTimeSlotName(currentAppointment.bookingTime) }}</el-descriptions-item>
          <el-descriptions-item label="参与人数">{{ currentAppointment.peopleCount }}人</el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ currentAppointment.price }}</el-descriptions-item>
          <el-descriptions-item label="总金额" :span="2">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
              ¥{{ currentAppointment.totalAmount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentAppointment.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentAppointment.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="特殊需求" :span="2" v-if="currentAppointment.specialRequirements">
            {{ currentAppointment.specialRequirements }}
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPaymentStatusType(currentAppointment.paymentStatus)" size="small">
              {{ getPaymentStatusName(currentAppointment.paymentStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="currentAppointment.paymentTime">
            {{ formatDateTime(currentAppointment.paymentTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag :type="getStatusType(currentAppointment.status)" size="small">
              {{ getStatusName(currentAppointment.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="确认时间" v-if="currentAppointment.confirmTime">
            {{ formatDateTime(currentAppointment.confirmTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="完成时间" v-if="currentAppointment.completeTime">
            {{ formatDateTime(currentAppointment.completeTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="取消时间" v-if="currentAppointment.cancelTime">
            {{ formatDateTime(currentAppointment.cancelTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因" :span="2" v-if="currentAppointment.cancelReason">
            <span style="color: #f56c6c;">{{ currentAppointment.cancelReason }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentAppointment.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentAppointment.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Download,
  Tickets,
  Clock,
  CircleCheck,
  Trophy,
  Money,
  Refresh,
  View,
  Check,
  Close,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'

// 筛选条件
const filters = reactive({
  bookingNo: '',
  experienceName: '',
  status: '',
  paymentStatus: '',
  dateRange: null as any,
  timeSlot: '',
  contactName: '',
  contactPhone: ''
})

// 预约列表
const appointmentList = ref([])
const loading = ref(false)

// 统计数据
const stats = reactive({
  pending: 0,
  confirmed: 0,
  completed: 0,
  totalAmount: 0
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentAppointment = ref<any>(null)

// 翻页跳转
const pageJump = ref<number | null>(null)

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10)))
  let target = Number(pageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === pagination.page) return
  handlePageChange(target)
}

// 最后更新时间
const lastUpdateTime = ref('')

// 格式化当前时间
const formatCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

// 获取行类名
const getRowClassName = ({ row, rowIndex }: { row: any; rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [filters.bookingNo, filters.experienceName, filters.status, filters.paymentStatus, filters.dateRange, filters.timeSlot, filters.contactName, filters.contactPhone],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.page = 1
      loadAppointments()
    }, 500)
  },
  { deep: true }
)

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref<NodeJS.Timeout | null>(null)

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新预约列表...')
    loadAppointments()
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

// 获取场次名称
const getTimeSlotName = (slot: string) => {
  const map: Record<string, string> = {
    morning: '上午场 (9:00-12:00)',
    afternoon: '下午场 (14:00-17:00)',
    night: '夜场 (18:00-21:00)'
  }
  return map[slot] || slot
}

// 获取支付状态名称
const getPaymentStatusName = (status: number) => {
  const map: Record<number, string> = {
    1: '未支付',
    2: '已支付',
    3: '已退款'
  }
  return map[status] || '未知'
}

// 获取支付状态类型
const getPaymentStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'info'
  }
  return map[status] || 'info'
}

// 获取预约状态名称
const getStatusName = (status: number) => {
  const map: Record<number, string> = {
    1: '待确认',
    2: '已确认',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

// 获取状态类型
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'info',
    4: 'danger'
  }
  return map[status] || 'info'
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadAppointments()
}

// 重置
const handleReset = () => {
  Object.assign(filters, {
    bookingNo: '',
    experienceName: '',
    status: '',
    paymentStatus: '',
    dateRange: null,
    timeSlot: '',
    contactName: '',
    contactPhone: ''
  })
  pagination.page = 1
  loadAppointments()
}

// 加载预约列表
const loadAppointments = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size,
      bookingType: 1 // 只查询文化体验的预约
    }
    if (filters.bookingNo) params.bookingNo = filters.bookingNo
    if (filters.experienceName) params.itemName = filters.experienceName
    if (filters.status) params.status = filters.status
    if (filters.paymentStatus) params.paymentStatus = filters.paymentStatus
    if (filters.timeSlot) params.bookingTime = filters.timeSlot
    if (filters.contactName) params.contactName = filters.contactName
    if (filters.contactPhone) params.contactPhone = filters.contactPhone
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    
    const res = await request.get('/admin/culture/booking/page', { params })
    if (res.code === 200) {
      appointmentList.value = res.data.records || []
      pagination.total = res.data.total || 0
      
      // 更新统计数据
      updateStats()
      
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    } else {
      ElMessage.error(res.message || '加载预约列表失败')
    }
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.pending = appointmentList.value.filter((item: any) => item.status === 1).length
  stats.confirmed = appointmentList.value.filter((item: any) => item.status === 2).length
  stats.completed = appointmentList.value.filter((item: any) => item.status === 3).length
  stats.totalAmount = appointmentList.value.reduce((sum: number, item: any) => {
    return sum + (parseFloat(item.totalAmount) || 0)
  }, 0)
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentAppointment.value = {
    ...row,
    experienceName: row.itemName,
    experienceImage: row.itemImage
  }
  detailDialogVisible.value = true
}

// 确认预约
const handleConfirm = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确认接受该预约？',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request.put(`/admin/culture/booking/${row.id}/confirm`)
    if (res.code === 200) {
      ElMessage.success('确认成功')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '确认失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

// 完成预约
const handleComplete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确认该预约已完成？',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request.put(`/admin/culture/booking/${row.id}/complete`)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 取消预约
const handleCancel = async (row: any) => {
  try {
    const { value: cancelReason } = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    })

    const res = await request.put(`/admin/culture/booking/${row.id}/cancel`, {
      cancelReason
    })
    if (res.code === 200) {
      ElMessage.success('预约已取消')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 导出数据
const handleExport = async () => {
  try {
    const params: any = {
      bookingType: 1 // 只导出文化体验的预约
    }
    if (filters.bookingNo) params.bookingNo = filters.bookingNo
    if (filters.experienceName) params.itemName = filters.experienceName
    if (filters.status) params.status = filters.status
    if (filters.paymentStatus) params.paymentStatus = filters.paymentStatus
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    
    const response = await request.get('/admin/culture/booking/export', {
      params,
      responseType: 'blob'
    })
    
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `预约列表_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadAppointments()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadAppointments()
}

// 页面加载
onMounted(() => {
  loadAppointments()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style scoped lang="scss">

.experience-info {
  display: flex;
  gap: 12px;
  align-items: center;

  .experience-image {
    width: 60px;
    height: 60px;
    border-radius: 8px;
  }

  .experience-details {
    flex: 1;
    min-width: 0;

    .experience-name {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.appointment-info {
  font-size: 13px;
  line-height: 1.8;
  color: #606266;
}

.amount {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
}

.appointment-detail {
  padding: 10px 0;
}
</style>







