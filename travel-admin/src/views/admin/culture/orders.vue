<template>
  <div class="admin-list-modern">
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><ShoppingCart /></el-icon>
        </div>
        <div class="header-title">
          <h1>订单管理</h1>
          <p>管理农特产品和特色周边的购买订单</p>
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

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待支付</div>
          <div class="stat-value">{{ stats.pending }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待发货</div>
          <div class="stat-value">{{ stats.shipped }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><CircleCheck /></el-icon>
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
          <el-form-item label="订单编号">
            <el-input
              v-model="filters.orderNo"
              placeholder="请输入订单编号"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="商品名称">
            <el-input
              v-model="filters.productName"
              placeholder="请输入商品名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="filters.orderStatus" placeholder="请选择订单状态" clearable>
              <el-option label="待支付" :value="1" />
              <el-option label="已支付" :value="2" />
              <el-option label="已发货" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已取消" :value="5" />
              <el-option label="已退款" :value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式">
            <el-select v-model="filters.paymentMethod" placeholder="请选择支付方式" clearable>
              <el-option label="微信支付" value="wechat" />
              <el-option label="支付宝" value="alipay" />
              <el-option label="银行卡" value="bank" />
            </el-select>
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
        <div class="filter-row">
          <el-form-item label="下单时间">
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="收货人">
            <el-input
              v-model="filters.receiverName"
              placeholder="请输入收货人姓名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input
              v-model="filters.receiverPhone"
              placeholder="请输入联系电话"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table
          :data="orderList"
          v-loading="loading"
          class="modern-table"
          :row-class-name="getRowClassName"
        >
        <el-table-column prop="orderNo" label="订单编号" width="180" fixed="left" />
        <el-table-column label="商品信息" width="300">
          <template #default="{ row }">
            <div class="product-info">
              <el-image
                :src="row.productImage"
                fit="cover"
                class="product-image"
              />
              <div class="product-details">
                <div class="product-name">{{ row.productName }}</div>
                <div class="product-spec" v-if="row.specification">
                  规格：{{ row.specification }}
                </div>
                <div class="product-quantity">数量：{{ row.quantity }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货信息" width="200">
          <template #default="{ row }">
            <div class="receiver-info">
              <div>{{ row.receiverName }}</div>
              <div>{{ row.receiverPhone }}</div>
              <div class="address" v-if="row.address">{{ row.address }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="150">
          <template #default="{ row }">
            <div class="amount-info">
              <div class="total">¥{{ row.totalAmount }}</div>
              <div class="final" v-if="row.couponDiscount > 0">
                实付：¥{{ row.finalAmount }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="{ row }">
            <el-tag size="small" v-if="row.paymentMethod">
              {{ getPaymentMethodName(row.paymentMethod) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)" size="small">
              {{ getStatusName(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="物流信息" width="180">
          <template #default="{ row }">
            <div v-if="row.shipCompany && row.shipNo" class="logistics-info">
              <div>{{ row.shipCompany }}</div>
              <div class="ship-no">{{ row.shipNo }}</div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template #default="{ row }">
            <span>{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="info" size="small" text @click="handleViewDetail(row)">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                v-if="row.orderStatus === 2"
                type="success"
                size="small"
                text
                @click="handleShip(row)"
              >
                <el-icon><Box /></el-icon>
                发货
              </el-button>
              <el-button
                v-if="row.orderStatus === 1"
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

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" required>
          <el-select v-model="shipForm.shipCompany" placeholder="请选择物流公司" style="width: 100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通快递" value="圆通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="京东物流" value="京东物流" />
            <el-option label="邮政EMS" value="邮政EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="shipForm.shipNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="shipForm.remark"
            type="textarea"
            :rows="3"
            placeholder="选填，发货备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="800px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.orderStatus)" size="small">
              {{ getStatusName(currentOrder.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="商品名称" :span="2">{{ currentOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="商品规格" v-if="currentOrder.specification">
            {{ currentOrder.specification }}
          </el-descriptions-item>
          <el-descriptions-item label="购买数量">{{ currentOrder.quantity }}</el-descriptions-item>
          <el-descriptions-item label="商品单价">¥{{ currentOrder.productPrice }}</el-descriptions-item>
          <el-descriptions-item label="商品总额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额" v-if="currentOrder.couponDiscount > 0">
            ¥{{ currentOrder.couponDiscount }}
          </el-descriptions-item>
          <el-descriptions-item label="实付金额" :span="2">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
              ¥{{ currentOrder.finalAmount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ currentOrder.province }} {{ currentOrder.city }} {{ currentOrder.district }} {{ currentOrder.detailedAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="买家留言" :span="2" v-if="currentOrder.buyerMessage">
            {{ currentOrder.buyerMessage }}
          </el-descriptions-item>
          <el-descriptions-item label="支付方式" v-if="currentOrder.paymentMethod">
            {{ getPaymentMethodName(currentOrder.paymentMethod) }}
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="currentOrder.paymentTime">
            {{ formatDateTime(currentOrder.paymentTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="物流公司" v-if="currentOrder.shipCompany">
            {{ currentOrder.shipCompany }}
          </el-descriptions-item>
          <el-descriptions-item label="物流单号" v-if="currentOrder.shipNo">
            {{ currentOrder.shipNo }}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatDateTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentOrder.updateTime) }}</el-descriptions-item>
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
  ShoppingCart,
  Clock,
  Box,
  CircleCheck,
  Money,
  Refresh,
  View,
  Close,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'

// 筛选条件
const filters = reactive({
  orderNo: '',
  productName: '',
  orderStatus: '',
  paymentMethod: '',
  dateRange: null as any,
  receiverName: '',
  receiverPhone: ''
})

// 订单列表
const orderList = ref([])
const loading = ref(false)

// 统计数据
const stats = reactive({
  pending: 0,
  shipped: 0,
  completed: 0,
  totalAmount: 0
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 发货对话框
const shipDialogVisible = ref(false)
const shipForm = reactive({
  orderId: null as number | null,
  shipCompany: '',
  shipNo: '',
  remark: ''
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentOrder = ref<any>(null)

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
  () => [filters.orderNo, filters.productName, filters.orderStatus, filters.paymentMethod, filters.dateRange, filters.receiverName, filters.receiverPhone],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.page = 1
      loadOrders()
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
    console.log('自动刷新订单列表...')
    loadOrders()
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

// 获取支付方式名称
const getPaymentMethodName = (method: string) => {
  const map: Record<string, string> = {
    wechat: '微信支付',
    alipay: '支付宝',
    bank: '银行卡'
  }
  return map[method] || method
}

// 获取订单状态名称
const getStatusName = (status: number) => {
  const map: Record<number, string> = {
    1: '待支付',
    2: '已支付',
    3: '已发货',
    4: '已完成',
    5: '已取消',
    6: '已退款'
  }
  return map[status] || '未知'
}

// 获取状态类型
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'primary',
    4: 'info',
    5: 'info',
    6: 'danger'
  }
  return map[status] || 'info'
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadOrders()
}

// 重置
const handleReset = () => {
  Object.assign(filters, {
    orderNo: '',
    productName: '',
    orderStatus: '',
    paymentMethod: '',
    dateRange: null,
    receiverName: '',
    receiverPhone: ''
  })
  pagination.page = 1
  loadOrders()
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size
    }
    if (filters.orderNo) params.orderNo = filters.orderNo
    if (filters.productName) params.productName = filters.productName
    if (filters.orderStatus) params.orderStatus = filters.orderStatus
    if (filters.paymentMethod) params.paymentMethod = filters.paymentMethod
    if (filters.receiverName) params.receiverName = filters.receiverName
    if (filters.receiverPhone) params.receiverPhone = filters.receiverPhone
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    
    const res = await request.get('/admin/culture/order/page', { params })
    if (res.code === 200) {
      orderList.value = res.data.records || []
      pagination.total = res.data.total || 0
      
      // 更新统计数据
      updateStats()
      
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    } else {
      ElMessage.error(res.message || '加载订单列表失败')
    }
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.pending = orderList.value.filter((item: any) => item.orderStatus === 1).length
  stats.shipped = orderList.value.filter((item: any) => item.orderStatus === 2).length
  stats.completed = orderList.value.filter((item: any) => item.orderStatus === 4).length
  stats.totalAmount = orderList.value.reduce((sum: number, item: any) => {
    return sum + (parseFloat(item.finalAmount || item.totalAmount) || 0)
  }, 0)
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

// 发货
const handleShip = (row: any) => {
  shipForm.orderId = row.id
  shipForm.shipCompany = ''
  shipForm.shipNo = ''
  shipForm.remark = ''
  shipDialogVisible.value = true
}

// 确认发货
const confirmShip = async () => {
  if (!shipForm.shipCompany || !shipForm.shipNo) {
    ElMessage.warning('请填写物流公司和物流单号')
    return
  }

  try {
    const res = await request.put(`/admin/culture/order/${shipForm.orderId}/ship`, {
      shipCompany: shipForm.shipCompany,
      shipNo: shipForm.shipNo,
      remark: shipForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('发货成功')
      shipDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } catch (error) {
    ElMessage.error('发货失败')
  }
}

// 取消订单
const handleCancel = async (row: any) => {
  try {
    const { value: cancelReason } = await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    })

    const res = await request.put(`/admin/culture/order/${row.id}/cancel`, {
      cancelReason
    })
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    } else {
      ElMessage.error(res.message || '取消订单失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 导出数据
const handleExport = async () => {
  try {
    const params: any = {}
    if (filters.orderNo) params.orderNo = filters.orderNo
    if (filters.productName) params.productName = filters.productName
    if (filters.orderStatus) params.orderStatus = filters.orderStatus
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    
    const response = await request.get('/admin/culture/order/export', {
      params,
      responseType: 'blob'
    })
    
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `订单列表_${new Date().toISOString().slice(0, 10)}.xlsx`
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
  loadOrders()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadOrders()
}

// 页面加载
onMounted(() => {
  loadOrders()
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

.product-info {
  display: flex;
  gap: 12px;
  align-items: center;

  .product-image {
    width: 60px;
    height: 60px;
    border-radius: 8px;
  }

  .product-details {
    flex: 1;
    min-width: 0;

    .product-name {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .product-spec,
    .product-quantity {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }
}

.receiver-info {
  font-size: 13px;
  line-height: 1.8;
  color: #606266;

  .address {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}

.amount-info {
  .total {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 2px;
  }

  .final {
    font-size: 12px;
    color: #f56c6c;
  }
}

.logistics-info {
  font-size: 13px;
  line-height: 1.6;

  .ship-no {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}

.order-detail {
  padding: 10px 0;
}
</style>
