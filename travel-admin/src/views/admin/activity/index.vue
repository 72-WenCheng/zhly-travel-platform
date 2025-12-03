<template>
  <div class="admin-list-modern">
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Star /></el-icon>
        </div>
        <div class="header-title">
          <h1>活动管理</h1>
          <p>管理系统活动信息与状态</p>
          <div class="status-info">
            <el-icon class="status-icon"><Refresh /></el-icon>
            <span class="status-text">数据每30秒自动刷新</span>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="success" class="action-btn" @click="exportActivities">
          <el-icon><Download /></el-icon>
          导出活动
        </el-button>
        <el-button type="primary" class="action-btn" @click="createActivity">
          <el-icon><Plus /></el-icon>
          添加活动
        </el-button>
      </div>
    </div>

    <!-- 活动统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Star /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总活动数</div>
          <div class="stat-value">{{ activityStats.total }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">进行中</div>
          <div class="stat-value">{{ activityStats.active }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总浏览量</div>
          <div class="stat-value">{{ activityStats.totalViews }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总参与人数</div>
          <div class="stat-value">{{ activityStats.totalParticipants }}</div>
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
        <div class="filter-row">
          <el-form-item label="活动标题">
            <el-input v-model="searchForm.title" placeholder="请输入活动标题" clearable />
          </el-form-item>
          <el-form-item label="活动类型">
            <el-select v-model="searchForm.activityType" placeholder="请选择活动类型" clearable>
              <el-option label="限时优惠" :value="1" />
              <el-option label="新品推荐" :value="2" />
              <el-option label="节日活动" :value="3" />
              <el-option label="特色路线" :value="4" />
              <el-option label="打卡挑战" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="草稿" :value="0" />
              <el-option label="进行中" :value="1" />
              <el-option label="已结束" :value="2" />
              <el-option label="已下架" :value="3" />
            </el-select>
          </el-form-item>
        </div>
        <div class="filter-row">
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="searchForm.createTimeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="活动时间" style="flex: 0 0 380px;">
            <el-date-picker
              v-model="searchForm.activityTimeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 100%;"
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

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedRows.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedRows.length }}</strong> 个活动</span>
          </div>
          <div class="batch-buttons">
            <el-button type="danger" size="small" @click="handleBatchDelete">
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

    <!-- 数据表格 -->
    <el-card class="table-card-modern" shadow="never">
      <el-table 
        :data="activityList" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        class="modern-table"
        ref="tableRef"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <el-image 
              :src="row.coverImage" 
              fit="cover"
              class="table-image"
              :preview-src-list="[row.coverImage]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="活动标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="活动类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getActivityTypeTag(row.activityType)">
              {{ getActivityTypeText(row.activityType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动时间" width="200">
          <template #default="{ row }">
            <div class="time-range">
              <div>{{ formatDate(row.startTime) }}</div>
              <div>{{ formatDate(row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="优惠信息" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag v-if="row.discountInfo" type="danger" effect="dark">
              {{ row.discountInfo }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览数" width="100" sortable />
        <el-table-column prop="participantCount" label="参与人数" width="100" sortable />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="warning" 
              size="small" 
              @click="handleEndActivity(row)"
            >
              <el-icon><CircleClose /></el-icon>
              结束
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              size="small" 
              @click="handleStartActivity(row)"
            >
              <el-icon><CircleCheck /></el-icon>
              开始
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      class="modern-dialog"
      @close="resetForm"
    >
      <div class="dialog-header-decoration"></div>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" class="edit-form">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动副标题" prop="subtitle">
          <el-input v-model="formData.subtitle" placeholder="请输入活动副标题" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="封面图片" prop="coverImage">
              <el-input v-model="formData.coverImage" placeholder="请输入图片URL" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动类型" prop="activityType">
              <el-select v-model="formData.activityType" placeholder="请选择活动类型">
                <el-option label="限时优惠" :value="1" />
                <el-option label="新品推荐" :value="2" />
                <el-option label="节日活动" :value="3" />
                <el-option label="特色路线" :value="4" />
                <el-option label="打卡挑战" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动地点">
              <el-input v-model="formData.location" placeholder="请输入活动地点" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠信息">
              <el-input v-model="formData.discountInfo" placeholder="如：门票5折优惠" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动规则">
          <el-input 
            v-model="formData.rules" 
            type="textarea" 
            :rows="3"
            placeholder="请输入活动规则"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">进行中</el-radio>
            <el-radio :label="2">已结束</el-radio>
            <el-radio :label="3">已下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            <el-icon v-if="!submitting"><Check /></el-icon>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Star, Plus, Download, Refresh, Search, Check,
  View, User, Edit, Delete, CircleClose, CircleCheck, Select, CloseBold
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加活动')
const submitting = ref(false)
const formRef = ref()
const lastUpdateTime = ref('')
const selectedRows = ref<any[]>([])
let refreshTimer: any = null

const activityList = ref<any[]>([])

const activityStats = reactive({
  total: 0,
  active: 0,
  totalViews: 0,
  totalParticipants: 0
})

const searchForm = reactive({
  title: '',
  activityType: null,
  status: null,
  createTimeRange: [],
  activityTimeRange: []
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive<any>({
  id: null,
  title: '',
  subtitle: '',
  description: '',
  coverImage: '',
  activityType: null,
  startTime: null,
  endTime: null,
  location: '',
  discountInfo: '',
  rules: '',
  status: 1
})

const formRules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  activityType: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    if (searchForm.title) params.title = searchForm.title
    if (searchForm.activityType !== null && searchForm.activityType !== undefined) params.activityType = searchForm.activityType
    if (searchForm.status !== null && searchForm.status !== undefined) params.status = searchForm.status
    
    const response = await request.get('/admin/activity/page', { params })
    if (response.code === 200) {
      activityList.value = response.data.records
      pagination.total = response.data.total
      lastUpdateTime.value = dayjs().format('HH:mm:ss')
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载统计
const loadStats = async () => {
  try {
    const response = await request.get('/admin/activity/stats')
    if (response.code === 200) {
      Object.assign(activityStats, response.data)
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 创建活动
const createActivity = () => {
  dialogTitle.value = '添加活动'
  dialogVisible.value = true
}

// 编辑活动
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑活动'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    let response
    if (formData.id) {
      response = await request.put(`/admin/activity/${formData.id}`, formData)
    } else {
      response = await request.post('/admin/activity', formData)
    }
    
    if (response.code === 200) {
      ElMessage.success(formData.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadData()
      loadStats()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 结束活动
const handleEndActivity = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定结束此活动吗？', '提示', { type: 'warning' })
    const response = await request.put(`/admin/activity/${row.id}/status`, null, {
      params: { status: 2 }
    })
    if (response.code === 200) {
      ElMessage.success('活动已结束')
      loadData()
      loadStats()
    }
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

// 开始活动
const handleStartActivity = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定开始此活动吗？', '提示', { type: 'info' })
    const response = await request.put(`/admin/activity/${row.id}/status`, null, {
      params: { status: 1 }
    })
    if (response.code === 200) {
      ElMessage.success('活动已开始')
      loadData()
      loadStats()
    }
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

// 删除活动
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除此活动吗？', '警告', { type: 'error' })
    const response = await request.delete(`/admin/activity/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    }
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 导出
const exportActivities = () => {
  ElMessage.info('导出功能开发中...')
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    activityType: null,
    status: null,
    createTimeRange: [],
    activityTimeRange: []
  })
  pagination.current = 1
  loadData()
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: ReturnType<typeof setTimeout> | null = null
watch(
  () => [
    searchForm.title,
    searchForm.activityType,
    searchForm.status,
    searchForm.createTimeRange,
    searchForm.activityTimeRange
  ],
  () => {
    // 清除之前的定时器
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    // 设置新的定时器（防抖：500ms）
    searchTimeout = setTimeout(() => {
      pagination.current = 1
      loadData()
    }, 500)
  },
  { deep: true }
)

// 分页
const handleSizeChange = () => {
  pagination.current = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

// 多选
const handleSelectionChange = (rows: any[]) => {
  selectedRows.value = rows
}

const tableRef = ref()

// 清除选择
const clearSelection = () => {
  selectedRows.value = []
  tableRef.value?.clearSelection()
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的活动')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个活动吗？`,
      '批量删除',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    const response = await request.delete('/admin/activity/batch', { data: ids })
    
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
      ElMessage.error('批量删除失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  formData.id = null
}

// 工具方法
const getActivityTypeText = (type: number) => {
  const types: Record<number, string> = {
    1: '限时优惠', 2: '新品推荐', 3: '节日活动', 4: '特色路线', 5: '打卡挑战'
  }
  return types[type] || '未知'
}

const getActivityTypeTag = (type: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const tags: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined> = {
    1: 'danger', 2: 'success', 3: 'warning', 4: 'info', 5: undefined
  }
  return tags[type] || undefined
}

const getStatusText = (status: number) => {
  const statuses: Record<number, string> = {
    0: '草稿', 1: '进行中', 2: '已结束', 3: '已下架'
  }
  return statuses[status] || '未知'
}

const getStatusType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const types: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined> = {
    0: 'info', 1: 'success', 2: 'warning', 3: 'danger'
  }
  return types[status] || undefined
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const formatDateTime = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 自动刷新
const startAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  refreshTimer = setInterval(() => {
    loadData()
    loadStats()
    lastUpdateTime.value = dayjs().format('HH:mm:ss')
  }, 30000)
}

onMounted(() => {
  loadData()
  loadStats()
  lastUpdateTime.value = dayjs().format('HH:mm:ss')
  startAutoRefresh()
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style lang="scss" scoped>
.time-range {
  font-size: 12px;
  line-height: 1.6;
  
  div:first-child {
    color: #67C23A;
  }
  
  div:last-child {
    color: #F56C6C;
  }
}
</style>

