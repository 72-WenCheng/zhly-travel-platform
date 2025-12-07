<template>
  <div class="order-management">
    <BackButton />
    
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row">
          <el-form-item label="订单编号">
            <el-input v-model="filters.orderNo" placeholder="请输入订单编号" clearable />
          </el-form-item>
          <el-form-item label="商品类型">
            <el-select v-model="filters.productType" placeholder="请选择商品类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="特色产品" :value="1" />
              <el-option label="文化体验" :value="2" />
              <el-option label="农家乐/民宿" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="filters.orderStatus" placeholder="请选择订单状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待支付" :value="1" />
              <el-option label="已支付" :value="2" />
              <el-option label="已发货" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已取消" :value="5" />
              <el-option label="已退款" :value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="下单时间">
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%;"
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

    <el-card class="table-card">
      <div class="table-header">
        <h3>订单列表</h3>
        <div class="actions">
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出订单
          </el-button>
        </div>
      </div>

      <el-table :data="orderList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="180" fixed />
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
                <div class="product-type">
                  <el-tag size="small">{{ getProductTypeName(row.productType) }}</el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            <div class="amount-info">
              <div class="total">¥{{ row.totalAmount }}</div>
              <div class="final" v-if="row.couponDiscount > 0">
                实付: ¥{{ row.finalAmount }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)">
              {{ getStatusName(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template #default="{ row }">
            <span>{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button
              v-if="row.orderStatus === 2"
              link
              type="success"
              size="small"
              @click="handleShip(row)"
            >
              <el-icon><Van /></el-icon>
              发货
            </el-button>
            <el-button
              v-if="row.orderStatus === 1"
              link
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              <el-icon><Close /></el-icon>
              取消订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" required>
          <el-select v-model="shipForm.shipCompany" placeholder="请选择物流公司">
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
            <el-tag :type="getStatusType(currentOrder.orderStatus)">
              {{ getStatusName(currentOrder.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ currentOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="商品类型">
            {{ getProductTypeName(currentOrder.productType) }}
          </el-descriptions-item>
          <el-descriptions-item label="购买数量">{{ currentOrder.quantity }}</el-descriptions-item>
          <el-descriptions-item label="商品单价">¥{{ currentOrder.productPrice }}</el-descriptions-item>
          <el-descriptions-item label="商品总额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额">¥{{ currentOrder.couponDiscount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额" :span="2">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
              ¥{{ currentOrder.finalAmount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentOrder.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ currentOrder.province }} {{ currentOrder.city }} {{ currentOrder.district }} {{ currentOrder.detailedAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="发票类型">
            {{ getInvoiceTypeName(currentOrder.invoiceType) }}
          </el-descriptions-item>
          <el-descriptions-item label="发票抬头" v-if="currentOrder.invoiceType > 1">
            {{ currentOrder.invoiceTitle }}
          </el-descriptions-item>
          <el-descriptions-item label="买家留言" :span="2" v-if="currentOrder.buyerMessage">
            {{ currentOrder.buyerMessage }}
          </el-descriptions-item>
          <el-descriptions-item label="支付方式" v-if="currentOrder.paymentMethod">
            {{ currentOrder.paymentMethod }}
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="currentOrder.paymentTime">
            {{ currentOrder.paymentTime }}
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  View,
  Van,
  Close
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'

// 筛选条件
const filters = reactive({
  orderNo: '',
  productType: '',
  orderStatus: '',
  dateRange: null as any
})

// 订单列表
const orderList = ref([])

const loading = ref(false)

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

// 获取商品类型名称
const getProductTypeName = (type: number) => {
  const map: Record<number, string> = {
    1: '特色产品',
    2: '文化体验',
    3: '农家乐/民宿'
  }
  return map[type] || '未知'
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

// 获取状态类型（Element Plus Tag类型）
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

// 获取发票类型名称
const getInvoiceTypeName = (type: number) => {
  const map: Record<number, string> = {
    1: '不开发票',
    2: '个人',
    3: '企业'
  }
  return map[type] || '未知'
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadOrders()
}

// 重置
const handleReset = () => {
  filters.orderNo = ''
  filters.productType = ''
  filters.orderStatus = ''
  filters.dateRange = null
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
    if (filters.productType) params.productType = filters.productType
    if (filters.orderStatus) params.orderStatus = filters.orderStatus
    
    const res = await request.get('/admin/culture/order/page', { params })
    if (res.code === 200) {
      orderList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
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

// 导出订单
const handleExport = async () => {
  try {
    const params: any = {}
    if (filters.orderNo) params.orderNo = filters.orderNo
    if (filters.productType) params.productType = filters.productType
    if (filters.orderStatus) params.orderStatus = filters.orderStatus
    
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
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style scoped lang="scss">
.order-management {
  padding: 20px;

  .table-card {
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
      }

      .actions {
        display: flex;
        gap: 10px;
      }
    }

    .product-info {
      display: flex;
      gap: 12px;
      align-items: center;

      .product-image {
        width: 60px;
        height: 60px;
        border-radius: 4px;
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

        .product-type {
          font-size: 12px;
          color: #909399;
        }
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

    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }

  .order-detail {
    padding: 10px 0;
  }
}
</style>



