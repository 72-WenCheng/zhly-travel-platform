<template>
  <div class="coupon-management">
    <BackButton />
    
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row">
          <el-form-item label="优惠券名称">
            <el-input v-model="filters.name" placeholder="请输入优惠券名称" clearable />
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
        <h3>优惠券列表</h3>
        <div class="actions">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增优惠券
          </el-button>
        </div>
      </div>

      <el-table :data="couponList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" width="200" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="150">
          <template #default="{ row }">
            <span v-if="row.discountType === 1" class="discount-value">
              满{{ row.minAmount }}减{{ row.discountValue }}
            </span>
            <span v-else class="discount-value">
              {{ row.discountValue * 10 }}折
              <span v-if="row.maxDiscount" class="max-discount">
                (最高减{{ row.maxDiscount }}元)
              </span>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="发行数量" width="120">
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
              <div>{{ row.startTime }}</div>
              <div>至 {{ row.endTime }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="row.status === 2"
              link
              type="warning"
              size="small"
              @click="handleToggleStatus(row, 4)"
            >
              <el-icon><VideoPlay /></el-icon>
              下架
            </el-button>
            <el-button
              v-if="row.status === 4"
              link
              type="success"
              size="small"
              @click="handleToggleStatus(row, 2)"
            >
              <el-icon><VideoPause /></el-icon>
              上架
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
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
              <el-select v-model="couponForm.type" placeholder="请选择">
                <el-option label="满减券" :value="1" />
                <el-option label="折扣券" :value="2" />
                <el-option label="兑换券" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠方式" prop="discountType">
              <el-select v-model="couponForm.discountType" placeholder="请选择">
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
                :precision="couponForm.discountType === 1 ? 2 : 2"
              />
              <span v-if="couponForm.discountType === 1" style="margin-left: 8px;">元</span>
              <span v-else style="margin-left: 8px;">折</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低使用金额" prop="minAmount">
              <el-input-number v-model="couponForm.minAmount" :min="0" :precision="2" />
              <span style="margin-left: 8px;">元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item
          v-if="couponForm.discountType === 2"
          label="最高优惠金额"
          prop="maxDiscount"
        >
          <el-input-number v-model="couponForm.maxDiscount" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元（选填）</span>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发行总量" prop="totalCount">
              <el-input-number v-model="couponForm.totalCount" :min="1" :step="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每人限领" prop="perUserLimit">
              <el-input-number v-model="couponForm.perUserLimit" :min="1" :step="1" />
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
          <el-input-number v-model="couponForm.validDays" :min="1" :step="1" />
          <span style="margin-left: 8px;">天（从领取之日起）</span>
        </el-form-item>

        <el-form-item v-else label="有效期范围" prop="timeRange">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  VideoPlay,
  VideoPause
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'

// 筛选条件
const filters = reactive({
  name: '',
  type: '',
  status: ''
})

// 优惠券列表
const couponList = ref([])

const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (couponForm.id ? '编辑优惠券' : '新增优惠券'))
const formRef = ref()
const validType = ref('days')
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
  filters.name = ''
  filters.type = ''
  filters.status = ''
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
    
    const res = await request.get('/admin/culture/coupon/page', { params })
    if (res.code === 200) {
      couponList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载优惠券列表失败')
  } finally {
    loading.value = false
  }
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
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style scoped lang="scss">
.coupon-management {
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

    .discount-value {
      font-weight: 500;
      color: #f56c6c;
    }

    .max-discount {
      font-size: 12px;
      color: #909399;
    }

    .count-info {
      font-size: 12px;

      .low-stock {
        color: #f56c6c;
        font-weight: 500;
      }
    }

    .valid-info {
      font-size: 12px;
      line-height: 1.5;
    }

    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
}
</style>



