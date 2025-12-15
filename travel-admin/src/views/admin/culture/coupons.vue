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
          <h1>优惠券管理</h1>
          <p>管理文旅平台的优惠券配置</p>
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
        <el-button type="primary" class="action-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增优惠券
        </el-button>
        <el-button type="success" class="action-btn" @click="openGrantDialog">
          <el-icon><Tickets /></el-icon>
          发放给用户
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
          <el-form-item label="优惠券名称">
            <el-input
              v-model="filters.name"
              placeholder="请输入优惠券名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="优惠券类型">
            <el-select v-model="filters.type" placeholder="请选择优惠券类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="满减券" :value="1" />
              <el-option label="折扣券" :value="2" />
              <el-option label="兑换券" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="未开始" :value="1" />
              <el-option label="进行中" :value="2" />
              <el-option label="已结束" :value="3" />
              <el-option label="已下架" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="使用范围">
            <el-select v-model="filters.scope" placeholder="请选择使用范围" clearable>
              <el-option label="全部" value="" />
              <el-option label="全场通用" :value="1" />
              <el-option label="指定分类" :value="2" />
              <el-option label="指定商品" :value="3" />
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

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">未开始</div>
          <div class="stat-value">{{ stats.notStarted }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><VideoPlay /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">进行中</div>
          <div class="stat-value">{{ stats.active }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已发放</div>
          <div class="stat-value">{{ stats.totalIssued }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已使用</div>
          <div class="stat-value">{{ stats.totalUsed }}</div>
        </div>
      </div>
    </div>

    <!-- 优惠券列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table
          :data="couponList"
          v-loading="loading"
          class="modern-table"
          :row-class-name="getRowClassName"
        >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="180">
          <template #default="{ row }">
            <div class="discount-content">
              <span v-if="row.discountType === 1" class="discount-value">
                满{{ row.minAmount }}减{{ row.discountValue }}元
              </span>
              <span v-else class="discount-value">
                {{ (row.discountValue * 10).toFixed(1) }}折
                <span v-if="row.maxDiscount" class="max-discount">
                  (最高减{{ row.maxDiscount }}元)
                </span>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发行数量" width="140">
          <template #default="{ row }">
            <div class="count-info">
              <div>总量: {{ row.totalCount }}</div>
              <div :class="{ 'low-stock': row.remainingCount < row.totalCount * 0.2 }">
                剩余: {{ row.remainingCount }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="使用范围" width="120">
          <template #default="{ row }">
            {{ getScopeName(row.scope) }}
          </template>
        </el-table-column>
        <el-table-column label="有效期" width="200">
          <template #default="{ row }">
            <div v-if="row.validDays" class="valid-info">
              领取后{{ row.validDays }}天内有效
            </div>
            <div v-else class="valid-info">
              <div>{{ formatDateTime(row.startTime) }}</div>
              <div>至 {{ formatDateTime(row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
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
        <el-table-column label="操作" fixed="right" width="280" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="info" size="small" text @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="warning"
                size="small"
                text
                @click="handleToggleStatus(row, 4)"
              >
                <el-icon><VideoPause /></el-icon>
                下架
              </el-button>
              <el-button
                v-if="row.status === 4"
                type="success"
                size="small"
                text
                @click="handleToggleStatus(row, 2)"
              >
                <el-icon><VideoPlay /></el-icon>
                上架
              </el-button>
              <el-button type="danger" size="small" text @click="handleDelete(row)">
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

    <!-- 新增/编辑优惠券对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="resetForm"
    >
      <el-form :model="couponForm" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="couponForm.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优惠券类型" prop="type">
              <el-select v-model="couponForm.type" placeholder="请选择" style="width: 100%">
                <el-option label="满减券" :value="1" />
                <el-option label="折扣券" :value="2" />
                <el-option label="兑换券" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠方式" prop="discountType">
              <el-select v-model="couponForm.discountType" placeholder="请选择" style="width: 100%">
                <el-option label="固定金额" :value="1" />
                <el-option label="折扣比例" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              :label="couponForm.discountType === 1 ? '优惠金额' : '折扣'"
              prop="discountValue"
            >
              <el-input-number
                v-model="couponForm.discountValue"
                :min="0"
                :max="couponForm.discountType === 1 ? 9999 : 1"
                :step="couponForm.discountType === 1 ? 1 : 0.1"
                :precision="couponForm.discountType === 1 ? 0 : 1"
                style="width: 100%"
              />
              <span v-if="couponForm.discountType === 1" style="margin-left: 8px;">元</span>
              <span v-else style="margin-left: 8px;">折</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低使用金额" prop="minAmount">
              <el-input-number
                v-model="couponForm.minAmount"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
              <span style="margin-left: 8px;">元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item
          v-if="couponForm.discountType === 2"
          label="最高优惠金额"
          prop="maxDiscount"
        >
          <el-input-number
            v-model="couponForm.maxDiscount"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
          <span style="margin-left: 8px;">元（选填）</span>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发行总量" prop="totalCount">
              <el-input-number
                v-model="couponForm.totalCount"
                :min="1"
                :step="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每人限领" prop="perUserLimit">
              <el-input-number
                v-model="couponForm.perUserLimit"
                :min="1"
                :step="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="使用范围" prop="scope">
          <el-radio-group v-model="couponForm.scope">
            <el-radio :value="1">全场通用</el-radio>
            <el-radio :value="2">指定分类</el-radio>
            <el-radio :value="3">指定商品</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="有效期类型">
          <el-radio-group v-model="validType">
            <el-radio value="days">固定天数</el-radio>
            <el-radio value="range">时间范围</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="validType === 'days'" label="有效天数" prop="validDays">
          <el-input-number
            v-model="couponForm.validDays"
            :min="1"
            :step="1"
            style="width: 100%"
          />
          <span style="margin-left: 8px;">天（从领取之日起）</span>
        </el-form-item>

        <el-form-item v-else label="有效期范围" prop="timeRange">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="使用说明">
          <el-input
            v-model="couponForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入使用说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 发放优惠券对话框 -->
    <el-dialog
      v-model="grantDialogVisible"
      title="发放优惠券给用户"
      width="520px"
    >
      <el-form :model="grantForm" label-width="120px">
        <el-form-item label="目标用户ID">
          <el-input
            v-model="grantForm.userId"
            placeholder="请输入用户ID"
            type="number"
          />
        </el-form-item>
        <el-form-item label="选择优惠券">
          <el-select
            v-model="grantForm.couponId"
            placeholder="请选择优惠券"
            filterable
            style="width: 100%;"
          >
            <el-option
              v-for="item in couponList"
              :key="item.id"
              :label="`${item.name}（满${item.minAmount}减${item.discountValue}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="有效天数(可选)">
          <el-input
            v-model="grantForm.validDays"
            placeholder="留空使用券默认有效期"
            type="number"
          />
        </el-form-item>
        <el-form-item label="来源说明">
          <el-input
            v-model="grantForm.sourceDesc"
            placeholder="如：客服补偿、活动奖励"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="grantDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="grantLoading" @click="submitGrant">确认发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Edit,
  Delete,
  VideoPlay,
  VideoPause,
  Tickets,
  Clock,
  Trophy,
  Money,
  Refresh,
  ArrowLeft,
  ArrowRight,
  Tickets as Present
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'
import type { FormInstance } from 'element-plus'

// 筛选条件
const filters = reactive({
  name: '',
  type: '',
  status: '',
  scope: ''
})

// 优惠券列表
const couponList = ref([])
const loading = ref(false)

// 统计数据
const stats = reactive({
  notStarted: 0,
  active: 0,
  totalIssued: 0,
  totalUsed: 0
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (couponForm.id ? '编辑优惠券' : '新增优惠券'))
const formRef = ref<FormInstance>()
const validType = ref('days')

// 发放对话框
const grantDialogVisible = ref(false)
const grantLoading = ref(false)
const grantForm = reactive({
  userId: '',
  couponId: null as number | null,
  validDays: '',
  sourceDesc: ''
})

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
  () => [filters.name, filters.type, filters.status, filters.scope],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.page = 1
      loadCoupons()
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
    console.log('自动刷新优惠券列表...')
    loadCoupons()
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
const timeRange = ref<any>(null)

// 表单数据
const couponForm = reactive({
  id: null as number | null,
  name: '',
  type: 1,
  discountType: 1,
  discountValue: 0,
  minAmount: 0,
  maxDiscount: null as number | null,
  totalCount: 100,
  perUserLimit: 1,
  scope: 1,
  validDays: 30,
  startTime: null as string | null,
  endTime: null as string | null,
  description: ''
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择优惠券类型', trigger: 'change' }],
  discountType: [{ required: true, message: '请选择优惠方式', trigger: 'change' }],
  discountValue: [{ required: true, message: '请输入优惠值', trigger: 'blur' }],
  minAmount: [{ required: true, message: '请输入最低使用金额', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入发行总量', trigger: 'blur' }],
  perUserLimit: [{ required: true, message: '请输入每人限领数量', trigger: 'blur' }],
  scope: [{ required: true, message: '请选择使用范围', trigger: 'change' }]
}

// 获取类型名称
const getTypeName = (type: number) => {
  const map: Record<number, string> = {
    1: '满减券',
    2: '折扣券',
    3: '兑换券'
  }
  return map[type] || '未知'
}

// 获取类型标签类型
const getTypeTagType = (type: number) => {
  const map: Record<number, string> = {
    1: 'danger',
    2: 'warning',
    3: 'success'
  }
  return map[type] || 'info'
}

// 获取使用范围名称
const getScopeName = (scope: number) => {
  const map: Record<number, string> = {
    1: '全场通用',
    2: '指定分类',
    3: '指定商品'
  }
  return map[scope] || '未知'
}

// 获取状态名称
const getStatusName = (status: number) => {
  const map: Record<number, string> = {
    1: '未开始',
    2: '进行中',
    3: '已结束',
    4: '已下架'
  }
  return map[status] || '未知'
}

// 获取状态类型
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'info',
    2: 'success',
    3: 'info',
    4: 'danger'
  }
  return map[status] || 'info'
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadCoupons()
}

// 重置
const handleReset = () => {
  Object.assign(filters, {
    name: '',
    type: '',
    status: '',
    scope: ''
  })
  pagination.page = 1
  loadCoupons()
}

// 加载优惠券列表
const loadCoupons = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size
    }
    if (filters.name) params.name = filters.name
    if (filters.type) params.type = filters.type
    if (filters.status) params.status = filters.status
    if (filters.scope) params.scope = filters.scope
    
    const res = await request.get('/admin/culture/coupon/page', { params })
    if (res.code === 200) {
      couponList.value = res.data.records || []
      pagination.total = res.data.total || 0
      
      // 更新统计数据
      updateStats()
      
      // 更新最后刷新时间
      lastUpdateTime.value = formatCurrentTime()
    } else {
      ElMessage.error(res.message || '加载优惠券列表失败')
    }
  } catch (error) {
    ElMessage.error('加载优惠券列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.notStarted = couponList.value.filter((item: any) => item.status === 1).length
  stats.active = couponList.value.filter((item: any) => item.status === 2).length
  stats.totalIssued = couponList.value.reduce((sum: number, item: any) => {
    return sum + ((item.totalCount || 0) - (item.remainingCount || 0))
  }, 0)
  // 已使用数量需要从后端获取统计数据
  stats.totalUsed = 0
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  Object.assign(couponForm, row)
  if (row.startTime && row.endTime) {
    validType.value = 'range'
    timeRange.value = [new Date(row.startTime), new Date(row.endTime)]
  } else {
    validType.value = 'days'
  }
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row: any, newStatus: number) => {
  const action = newStatus === 2 ? '上架' : '下架'
  
  try {
    await ElMessageBox.confirm(
      `确认${action}该优惠券？`,
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request.put(`/admin/culture/coupon/${row.id}/status`, {
      status: newStatus
    })
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadCoupons()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确认删除该优惠券？此操作不可恢复！',
      '警告',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    const res = await request.delete(`/admin/culture/coupon/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadCoupons()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 确认提交
const confirmSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 处理有效期
    if (validType.value === 'range') {
      if (!timeRange.value || timeRange.value.length !== 2) {
        ElMessage.warning('请选择有效期范围')
        return
      }
      couponForm.startTime = timeRange.value[0].toISOString()
      couponForm.endTime = timeRange.value[1].toISOString()
      couponForm.validDays = null as any
    } else {
      couponForm.startTime = null
      couponForm.endTime = null
    }

    if (couponForm.id) {
      // 编辑
      const res = await request.put(`/admin/culture/coupon/${couponForm.id}`, couponForm)
      if (res.code === 200) {
        ElMessage.success('编辑成功')
        dialogVisible.value = false
        loadCoupons()
      } else {
        ElMessage.error(res.message || '编辑失败')
      }
    } else {
      // 新增
      const res = await request.post('/admin/culture/coupon', couponForm)
      if (res.code === 200) {
        ElMessage.success('新增成功')
        dialogVisible.value = false
        loadCoupons()
      } else {
        ElMessage.error(res.message || '新增失败')
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  couponForm.id = null
  couponForm.name = ''
  couponForm.type = 1
  couponForm.discountType = 1
  couponForm.discountValue = 0
  couponForm.minAmount = 0
  couponForm.maxDiscount = null
  couponForm.totalCount = 100
  couponForm.perUserLimit = 1
  couponForm.scope = 1
  couponForm.validDays = 30
  couponForm.startTime = null
  couponForm.endTime = null
  couponForm.description = ''
  validType.value = 'days'
  timeRange.value = null
}

// 打开发放弹窗
const openGrantDialog = () => {
  grantDialogVisible.value = true
}

// 提交发放
const submitGrant = async () => {
  if (!grantForm.userId || !grantForm.couponId) {
    ElMessage.warning('请填写用户ID并选择优惠券')
    return
  }
  grantLoading.value = true
  try {
    const res = await request.post('/admin/user/coupon/grant', {
      userId: grantForm.userId,
      couponId: grantForm.couponId,
      validDays: grantForm.validDays || undefined,
      sourceDesc: grantForm.sourceDesc || '管理端发放'
    })
    if (res.code === 200) {
      ElMessage.success('发放成功')
      grantDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '发放失败')
    }
  } catch (error) {
    ElMessage.error('发放失败，请重试')
  } finally {
    grantLoading.value = false
  }
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadCoupons()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadCoupons()
}

// 页面加载
onMounted(() => {
  loadCoupons()
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

.discount-content {
  .discount-value {
    font-weight: 500;
    color: #f56c6c;
  }

  .max-discount {
    font-size: 12px;
    color: #909399;
    margin-left: 4px;
  }
}

.count-info {
  font-size: 13px;
  line-height: 1.6;

  .low-stock {
    color: #f56c6c;
    font-weight: 500;
  }
}

.valid-info {
  font-size: 12px;
  line-height: 1.5;
  color: #606266;
}
</style>
