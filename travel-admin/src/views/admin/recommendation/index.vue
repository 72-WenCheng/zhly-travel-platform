<template>
  <div class="admin-list-modern">
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><TrendCharts /></el-icon>
        </div>
        <div class="header-title">
          <h1>热门推荐管理</h1>
          <p>管理系统热门推荐内容</p>
          <div class="status-info">
            <el-icon class="status-icon"><Refresh /></el-icon>
            <span class="status-text">数据每30秒自动刷新</span>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="success" class="action-btn" @click="exportRecommendations">
          <el-icon><Download /></el-icon>
          导出推荐
        </el-button>
        <el-button type="primary" class="action-btn" @click="createRecommendation">
          <el-icon><Plus /></el-icon>
          添加推荐
        </el-button>
      </div>
    </div>

    <!-- 推荐统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总推荐数</div>
          <div class="stat-value">{{ recommendStats.total }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">启用中</div>
          <div class="stat-value">{{ recommendStats.active }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总点击量</div>
          <div class="stat-value">{{ recommendStats.totalClicks }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><Star /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">平均评分</div>
          <div class="stat-value">{{ recommendStats.avgScore }}</div>
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
          <el-form-item label="推荐标题">
            <el-input v-model="searchForm.title" placeholder="请输入推荐标题" clearable />
          </el-form-item>
          <el-form-item label="推荐类型">
            <el-select v-model="searchForm.recommendType" placeholder="请选择推荐类型" clearable>
              <el-option label="热门景点" :value="1" />
              <el-option label="热门攻略" :value="2" />
              <el-option label="新品推荐" :value="3" />
              <el-option label="编辑推荐" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="项目类型">
            <el-select v-model="searchForm.itemType" placeholder="请选择项目类型" clearable>
              <el-option label="景点" :value="1" />
              <el-option label="攻略" :value="2" />
              <el-option label="文旅项目" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="禁用" :value="0" />
              <el-option label="启用" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <div class="filter-row">
          <el-form-item label="创建时间" style="flex: 0 0 320px;">
            <el-date-picker
              v-model="searchForm.createTimeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
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
            <span>已选择 <strong>{{ selectedRows.length }}</strong> 个推荐</span>
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
        :data="recommendList" 
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
        <el-table-column prop="title" label="推荐标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="subtitle" label="副标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="推荐类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getRecommendTypeTag(row.recommendType)">
              {{ getRecommendTypeText(row.recommendType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="项目类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getItemTypeTag(row.itemType)">
              {{ getItemTypeText(row.itemType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag 
              v-for="tag in getTags(row.tags)" 
              :key="tag"
              size="small"
              style="margin-right: 4px"
            >
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.score" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="displayOrder" label="顺序" width="80" sortable />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              :active-value="1" 
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="clickCount" label="点击量" width="100" sortable />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
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
      width="800px"
      class="modern-dialog"
      @close="resetForm"
    >
      <div class="dialog-header-decoration"></div>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" class="edit-form">
        <el-form-item label="推荐标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入推荐标题" />
        </el-form-item>
        <el-form-item label="推荐副标题">
          <el-input v-model="formData.subtitle" placeholder="请输入推荐副标题" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="推荐类型" prop="recommendType">
              <el-select v-model="formData.recommendType" placeholder="请选择推荐类型">
                <el-option label="热门景点" :value="1" />
                <el-option label="热门攻略" :value="2" />
                <el-option label="新品推荐" :value="3" />
                <el-option label="编辑推荐" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="itemType">
              <el-select v-model="formData.itemType" placeholder="请选择项目类型">
                <el-option label="景点" :value="1" />
                <el-option label="攻略" :value="2" />
                <el-option label="文旅项目" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目ID" prop="itemId">
              <el-input-number v-model="formData.itemId" :min="1" placeholder="请输入项目ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分">
              <el-rate v-model="formData.score" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="formData.coverImage" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="formData.tags" placeholder="多个标签用逗号分隔，如：美食,夜景,文化" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="显示顺序">
              <el-input-number v-model="formData.displayOrder" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="formData.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
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
  TrendCharts, Plus, Download, Refresh, Search, Check, 
  View, Star, Edit, Delete, Select, CloseBold
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加推荐')
const submitting = ref(false)
const formRef = ref()
const lastUpdateTime = ref('')
const selectedRows = ref<any[]>([])
let refreshTimer: any = null

const recommendList = ref<any[]>([])

const recommendStats = reactive({
  total: 0,
  active: 0,
  totalClicks: 0,
  avgScore: '0.0'
})

const searchForm = reactive({
  title: '',
  recommendType: null,
  itemType: null,
  status: null,
  createTimeRange: []
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
  recommendType: null,
  itemType: null,
  itemId: null,
  coverImage: '',
  tags: '',
  score: 0,
  displayOrder: 0,
  startTime: null,
  endTime: null,
  status: 1
})

const formRules = {
  title: [{ required: true, message: '请输入推荐标题', trigger: 'blur' }],
  recommendType: [{ required: true, message: '请选择推荐类型', trigger: 'change' }],
  itemType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  itemId: [{ required: true, message: '请输入项目ID', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    if (searchForm.recommendType !== null && searchForm.recommendType !== undefined) params.recommendType = searchForm.recommendType
    if (searchForm.itemType !== null && searchForm.itemType !== undefined) params.itemType = searchForm.itemType
    if (searchForm.status !== null && searchForm.status !== undefined) params.status = searchForm.status
    
    const response = await request.get('/admin/recommendation/page', { params })
    if (response.code === 200) {
      recommendList.value = response.data.records
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
    const response = await request.get('/admin/recommendation/stats')
    if (response.code === 200) {
      Object.assign(recommendStats, response.data)
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 创建推荐
const createRecommendation = () => {
  dialogTitle.value = '添加推荐'
  dialogVisible.value = true
}

// 编辑推荐
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑推荐'
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
      response = await request.put(`/admin/recommendation/${formData.id}`, formData)
    } else {
      response = await request.post('/admin/recommendation', formData)
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

// 状态切换
const handleStatusChange = async (row: any) => {
  try {
    await request.put(`/admin/recommendation/${row.id}/status`, null, {
      params: { status: row.status }
    })
    ElMessage.success('状态更新成功')
    loadStats()
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

// 删除推荐
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除此推荐吗？', '警告', { type: 'error' })
    const response = await request.delete(`/admin/recommendation/${row.id}`)
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
const exportRecommendations = () => {
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
    recommendType: null,
    itemType: null,
    status: null,
    createTimeRange: []
  })
  pagination.current = 1
  loadData()
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [
    searchForm.title,
    searchForm.recommendType,
    searchForm.itemType,
    searchForm.status,
    searchForm.createTimeRange
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
    ElMessage.warning('请选择要删除的推荐')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个推荐吗？`,
      '批量删除',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    const response = await request.delete('/admin/recommendation/batch', { data: ids })
    
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
const getRecommendTypeText = (type: number) => {
  const types: Record<number, string> = {
    1: '热门景点', 2: '热门攻略', 3: '新品推荐', 4: '编辑推荐'
  }
  return types[type] || '未知'
}

const getRecommendTypeTag = (type: number) => {
  const tags: Record<number, string> = {
    1: 'success', 2: 'warning', 3: 'info', 4: 'danger'
  }
  return tags[type] || ''
}

const getItemTypeText = (type: number) => {
  const types: Record<number, string> = {
    1: '景点', 2: '攻略', 3: '文旅项目'
  }
  return types[type] || '未知'
}

const getItemTypeTag = (type: number) => {
  const tags: Record<number, string> = {
    1: '', 2: 'success', 3: 'warning'
  }
  return tags[type] || ''
}

const getTags = (tagsStr: string) => {
  if (!tagsStr) return []
  return tagsStr.split(',').filter(tag => tag.trim())
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

