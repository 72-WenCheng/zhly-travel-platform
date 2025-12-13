<template>
  <div class="admin-list-modern">
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Calendar /></el-icon>
        </div>
        <div class="header-title">
          <h1>预订管理</h1>
          <p>管理农家乐和民宿的预订信息</p>
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
          <el-form-item label="预订编号">
            <el-input
              v-model="filters.bookingNo"
              placeholder="请输入预订编号"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="服务类型">
            <el-select v-model="filters.serviceType" placeholder="请选择服务类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="农家乐" value="farmstay" />
              <el-option label="民宿" value="homestay" />
            </el-select>
          </el-form-item>
          <el-form-item label="预订状态">
            <el-select v-model="filters.status" placeholder="请选择预订状态" clearable>
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
          <el-form-item label="预订日期">
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            />
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
          <el-form-item label=" " class="filter-actions">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">重置</el-button>
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

    <!-- 筛选和搜索 -->
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row">
          <el-form-item label="预订编号">
            <el-input
              v-model="filters.bookingNo"
              placeholder="请输入预订编号"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="服务类型">
            <el-select v-model="filters.serviceType" placeholder="请选择服务类型" clearable>
              <el-option label="农家乐" value="farmstay" />
              <el-option label="民宿" value="homestay" />
            </el-select>
          </el-form-item>
          <el-form-item label="预订状态">
            <el-select v-model="filters.status" placeholder="请选择预订状态" clearable>
              <el-option label="待确认" :value="1" />
              <el-option label="已确认" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付状态">
            <el-select v-model="filters.paymentStatus" placeholder="请选择支付状态" clearable>
              <el-option label="未支付" :value="1" />
              <el-option label="已支付" :value="2" />
              <el-option label="已退款" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
        <div class="filter-row">
          <el-form-item label="预订日期">
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            />
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
        </div>
      </el-form>
    </el-card>

    <!-- 预订列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table
          :data="bookingList"
          v-loading="loading"
          class="modern-table"
          :row-class-name="getRowClassName"
        >
        <el-table-column prop="bookingNo" label="预订编号" width="180" fixed="left" />
        <el-table-column label="服务信息" width="280">
          <template #default="{ row }">
            <div class="service-info">
              <el-image
                :src="row.serviceImage"
                fit="cover"
                class="service-image"
              />
              <div class="service-details">
                <div class="service-name">{{ row.serviceName }}</div>
                <el-tag :type="row.serviceType === 'farmstay' ? 'success' : 'primary'" size="small">
                  {{ row.serviceType === 'farmstay' ? '农家乐' : '民宿' }}
                </el-tag>
                <div class="package-name" v-if="row.packageName">
                  套餐：{{ row.packageName }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预订信息" width="200">
          <template #default="{ row }">
            <div class="booking-info">
              <div v-if="row.serviceType === 'homestay'">
                <div>入住：{{ row.checkInDate }}</div>
                <div>晚数：{{ row.nights }}晚</div>
              </div>
              <div v-else>
                <div>日期：{{ row.bookingDate }}</div>
                <div>人数：{{ row.peopleCount }}人</div>
              </div>
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
        <el-table-column label="预订状态" width="100">
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
    <el-dialog v-model="detailDialogVisible" title="预订详情" width="800px">
      <div v-if="currentBooking" class="booking-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预订编号">{{ currentBooking.bookingNo }}</el-descriptions-item>
          <el-descriptions-item label="服务类型">
            <el-tag :type="currentBooking.serviceType === 'farmstay' ? 'success' : 'primary'" size="small">
              {{ currentBooking.serviceType === 'farmstay' ? '农家乐' : '民宿' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="服务名称" :span="2">{{ currentBooking.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="套餐名称" :span="2" v-if="currentBooking.packageName">
            {{ currentBooking.packageName }}
          </el-descriptions-item>
          <el-descriptions-item label="预订日期" v-if="currentBooking.serviceType === 'farmstay'">
            {{ currentBooking.bookingDate }}
          </el-descriptions-item>
          <el-descriptions-item label="入住日期" v-if="currentBooking.serviceType === 'homestay'">
            {{ currentBooking.checkInDate }}
          </el-descriptions-item>
          <el-descriptions-item label="入住晚数" v-if="currentBooking.serviceType === 'homestay'">
            {{ currentBooking.nights }}晚
          </el-descriptions-item>
          <el-descriptions-item label="人数" v-if="currentBooking.serviceType === 'farmstay'">
            {{ currentBooking.peopleCount }}人
          </el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ currentBooking.price }}</el-descriptions-item>
          <el-descriptions-item label="总金额" :span="2">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
              ¥{{ currentBooking.totalAmount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentBooking.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentBooking.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="特殊需求" :span="2" v-if="currentBooking.specialRequirements">
            {{ currentBooking.specialRequirements }}
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPaymentStatusType(currentBooking.paymentStatus)" size="small">
              {{ getPaymentStatusName(currentBooking.paymentStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="currentBooking.paymentTime">
            {{ formatDateTime(currentBooking.paymentTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="预订状态">
            <el-tag :type="getStatusType(currentBooking.status)" size="small">
              {{ getStatusName(currentBooking.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="确认时间" v-if="currentBooking.confirmTime">
            {{ formatDateTime(currentBooking.confirmTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="完成时间" v-if="currentBooking.completeTime">
            {{ formatDateTime(currentBooking.completeTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="取消时间" v-if="currentBooking.cancelTime">
            {{ formatDateTime(currentBooking.cancelTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因" :span="2" v-if="currentBooking.cancelReason">
            <span style="color: #f56c6c;">{{ currentBooking.cancelReason }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentBooking.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentBooking.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Download,
  Calendar,
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
  serviceType: '',
  status: '',
  paymentStatus: '',
  dateRange: null as any,
  contactName: '',
  contactPhone: ''
})

// 预订列表
const bookingList = ref([])
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
const currentBooking = ref<any>(null)

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
  () => [filters.bookingNo, filters.serviceType, filters.status, filters.paymentStatus, filters.dateRange, filters.contactName, filters.contactPhone],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.page = 1
      loadBookings()
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
    console.log('自动刷新预订列表...')
    loadBookings()
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

// 获取预订状态名称
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
  loadBookings()
}

// 重置
const handleReset = () => {
  Object.assign(filters, {
    bookingNo: '',
    serviceType: '',
    status: '',
    paymentStatus: '',
    dateRange: null,
    contactName: '',
    contactPhone: ''
  })
  pagination.page = 1
  loadBookings()
}

// 加载预订列表
const loadBookings = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size,
      bookingType: 2 // 只查询农家乐和民宿的预订
    }
    if (filters.bookingNo) params.bookingNo = filters.bookingNo
    if (filters.serviceType) params.serviceType = filters.serviceType
    if (filters.status) params.status = filters.status
    if (filters.paymentStatus) params.paymentStatus = filters.paymentStatus
    if (filters.contactName) params.contactName = filters.contactName
    if (filters.contactPhone) params.contactPhone = filters.contactPhone
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    
    const res = await request.get('/admin/culture/booking/page', { params })
    if (res.code === 200) {
      bookingList.value = res.data.records || []
      pagination.total = res.data.total || 0
      
      // 更新统计数据
      updateStats()
      
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    } else {
      ElMessage.error(res.message || '加载预订列表失败')
    }
  } catch (error) {
    ElMessage.error('加载预订列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.pending = bookingList.value.filter((item: any) => item.status === 1).length
  stats.confirmed = bookingList.value.filter((item: any) => item.status === 2).length
  stats.completed = bookingList.value.filter((item: any) => item.status === 3).length
  stats.totalAmount = bookingList.value.reduce((sum: number, item: any) => {
    return sum + (parseFloat(item.totalAmount) || 0)
  }, 0)
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentBooking.value = row
  detailDialogVisible.value = true
}

// 确认预订
const handleConfirm = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确认接受该预订？',
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
      loadBookings()
    } else {
      ElMessage.error(res.message || '确认失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

// 完成预订
const handleComplete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确认该预订已完成？',
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
      loadBookings()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 取消预订
const handleCancel = async (row: any) => {
  try {
    const { value: cancelReason } = await ElMessageBox.prompt('请输入取消原因', '取消预订', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    })

    const res = await request.put(`/admin/culture/booking/${row.id}/cancel`, {
      cancelReason
    })
    if (res.code === 200) {
      ElMessage.success('预订已取消')
      loadBookings()
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
      bookingType: 2 // 只导出农家乐和民宿的预订
    }
    if (filters.bookingNo) params.bookingNo = filters.bookingNo
    if (filters.serviceType) params.serviceType = filters.serviceType
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
    link.download = `预订列表_${new Date().toISOString().slice(0, 10)}.xlsx`
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
  loadBookings()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadBookings()
}

// 页面加载
onMounted(() => {
  loadBookings()
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

.service-info {
  display: flex;
  gap: 12px;
  align-items: center;

  .service-image {
    width: 60px;
    height: 60px;
    border-radius: 8px;
  }

  .service-details {
    flex: 1;
    min-width: 0;

    .service-name {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .package-name {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.booking-info {
  font-size: 13px;
  line-height: 1.8;
  color: #606266;
}

.amount {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
}

.booking-detail {
  padding: 10px 0;
}
</style>
