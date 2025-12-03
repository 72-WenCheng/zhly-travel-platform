<template>
  <div class="booking-management">
    <BackButton />
    
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="预订编号">
          <el-input v-model="filters.bookingNo" placeholder="请输入预订编号" clearable />
        </el-form-item>
        <el-form-item label="预订类型">
          <el-select v-model="filters.bookingType" placeholder="请选择" clearable>
            <el-option label="全部" value="" />
            <el-option label="文化体验" :value="1" />
            <el-option label="农家乐/民宿" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="预订状态">
          <el-select v-model="filters.status" placeholder="请选择" clearable>
            <el-option label="全部" value="" />
            <el-option label="待确认" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="已退款" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="预订日期">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <h3>预订列表</h3>
        <div class="actions">
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出预订
          </el-button>
        </div>
      </div>

      <el-table :data="bookingList" style="width: 100%" v-loading="loading">
        <el-table-column prop="bookingNo" label="预订编号" width="180" fixed />
        <el-table-column label="预订类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.bookingType === 1 ? 'primary' : 'success'" size="small">
              {{ row.bookingType === 1 ? '文化体验' : '农家乐/民宿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="项目信息" width="280">
          <template #default="{ row }">
            <div class="item-info">
              <el-image
                :src="row.itemImage"
                fit="cover"
                class="item-image"
              />
              <div class="item-details">
                <div class="item-name">{{ row.itemName }}</div>
                <div class="package-name" v-if="row.packageName">
                  套餐: {{ row.packageName }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预订信息" width="200">
          <template #default="{ row }">
            <div class="booking-info">
              <div>日期: {{ row.bookingDate }}</div>
              <div v-if="row.bookingTime">时段: {{ row.bookingTime }}</div>
              <div>人数: {{ row.peopleCount }}人</div>
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
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
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
              v-if="row.status === 1"
              link
              type="success"
              size="small"
              @click="handleConfirm(row)"
            >
              <el-icon><Check /></el-icon>
              确认
            </el-button>
            <el-button
              v-if="row.status === 2"
              link
              type="warning"
              size="small"
              @click="handleComplete(row)"
            >
              <el-icon><CircleCheck /></el-icon>
              完成
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              link
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              <el-icon><Close /></el-icon>
              取消
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

    <!-- 预订详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预订详情" width="800px">
      <div v-if="currentBooking" class="booking-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预订编号">{{ currentBooking.bookingNo }}</el-descriptions-item>
          <el-descriptions-item label="预订类型">
            <el-tag :type="currentBooking.bookingType === 1 ? 'primary' : 'success'" size="small">
              {{ currentBooking.bookingType === 1 ? '文化体验' : '农家乐/民宿' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目名称" :span="2">{{ currentBooking.itemName }}</el-descriptions-item>
          <el-descriptions-item label="套餐名称" :span="2" v-if="currentBooking.packageName">
            {{ currentBooking.packageName }}
          </el-descriptions-item>
          <el-descriptions-item label="预订日期">{{ currentBooking.bookingDate }}</el-descriptions-item>
          <el-descriptions-item label="预订时段" v-if="currentBooking.bookingTime">
            {{ currentBooking.bookingTime }}
          </el-descriptions-item>
          <el-descriptions-item label="预订人数">{{ currentBooking.peopleCount }}人</el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ currentBooking.price }}</el-descriptions-item>
          <el-descriptions-item label="总金额" :span="2">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
              ¥{{ currentBooking.totalAmount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentBooking.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentBooking.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2" v-if="currentBooking.contactIdCard">
            {{ currentBooking.contactIdCard }}
          </el-descriptions-item>
          <el-descriptions-item label="特殊要求" :span="2" v-if="currentBooking.specialRequirements">
            {{ currentBooking.specialRequirements }}
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPaymentStatusType(currentBooking.paymentStatus)" size="small">
              {{ getPaymentStatusName(currentBooking.paymentStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="currentBooking.paymentTime">
            {{ currentBooking.paymentTime }}
          </el-descriptions-item>
          <el-descriptions-item label="预订状态">
            <el-tag :type="getStatusType(currentBooking.status)">
              {{ getStatusName(currentBooking.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="确认时间" v-if="currentBooking.confirmTime">
            {{ currentBooking.confirmTime }}
          </el-descriptions-item>
          <el-descriptions-item label="完成时间" v-if="currentBooking.completeTime">
            {{ currentBooking.completeTime }}
          </el-descriptions-item>
          <el-descriptions-item label="取消时间" v-if="currentBooking.cancelTime">
            {{ currentBooking.cancelTime }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因" :span="2" v-if="currentBooking.cancelReason">
            {{ currentBooking.cancelReason }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="currentBooking.remark">
            {{ currentBooking.remark }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentBooking.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentBooking.updateTime) }}</el-descriptions-item>
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
  Check,
  CircleCheck,
  Close
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'

// 筛选条件
const filters = reactive({
  bookingNo: '',
  bookingType: '',
  status: '',
  dateRange: null as any
})

// 预订列表
const bookingList = ref([])

const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentBooking = ref<any>(null)

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
    4: '已取消',
    5: '已退款'
  }
  return map[status] || '未知'
}

// 获取状态类型
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'info',
    4: 'info',
    5: 'danger'
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
  filters.bookingNo = ''
  filters.bookingType = ''
  filters.status = ''
  filters.dateRange = null
  pagination.page = 1
  loadBookings()
}

// 加载预订列表
const loadBookings = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size
    }
    if (filters.bookingNo) params.bookingNo = filters.bookingNo
    if (filters.bookingType) params.bookingType = filters.bookingType
    if (filters.status) params.status = filters.status
    
    const res = await request.get('/admin/culture/booking/page', { params })
    if (res.code === 200) {
      bookingList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载预订列表失败')
  } finally {
    loading.value = false
  }
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

// 导出预订
const handleExport = async () => {
  try {
    const params: any = {}
    if (filters.bookingNo) params.bookingNo = filters.bookingNo
    if (filters.bookingType) params.bookingType = filters.bookingType
    if (filters.status) params.status = filters.status
    
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
})
</script>

<style scoped lang="scss">
.booking-management {
  padding: 20px;

  .filter-card {
    margin-bottom: 20px;
  }

  .filter-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }

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

    .item-info {
      display: flex;
      gap: 12px;
      align-items: center;

      .item-image {
        width: 60px;
        height: 60px;
        border-radius: 4px;
      }

      .item-details {
        flex: 1;
        min-width: 0;

        .item-name {
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
        }
      }
    }

    .booking-info {
      font-size: 12px;
      line-height: 1.8;
    }

    .amount {
      font-size: 14px;
      font-weight: 500;
      color: #f56c6c;
    }

    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }

  .booking-detail {
    padding: 10px 0;
  }
}
</style>



