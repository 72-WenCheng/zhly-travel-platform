<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Bell /></el-icon>
        </div>
        <div class="header-title">
          <h1>公告管理</h1>
          <p>管理系统公告与通知内容</p>
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
        <el-button type="primary" class="action-btn" @click="createAnnouncement">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :style="{ background: stat.color }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">{{ stat.value }}</div>
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
        <!-- 第一行：三个筛选条件 -->
        <div class="filter-row">
          <el-form-item label="公告类型">
            <el-select v-model="searchForm.type" placeholder="请选择公告类型" clearable>
              <el-option label="系统公告" :value="1" />
              <el-option label="活动公告" :value="2" />
              <el-option label="功能更新" :value="3" />
              <el-option label="维护通知" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布状态">
            <el-select v-model="searchForm.status" placeholder="请选择发布状态" clearable>
              <el-option label="草稿" :value="0" />
              <el-option label="已发布" :value="1" />
              <el-option label="已下架" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="请输入标题或内容关键词" 
              clearable
            />
          </el-form-item>
        </div>
        <!-- 第二行：右侧重置按钮，结构与攻略管理一致 -->
        <div class="filter-row">
          <el-form-item label=" " class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedAnnouncements.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedAnnouncements.length }}</strong> 条公告</span>
          </div>
          <div class="batch-buttons">
            <el-button type="danger" size="small" @click="batchDelete">
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

    <!-- 公告列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="announcementList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="title" label="公告标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="type" label="类型" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.type)" size="small">
                {{ getTypeName(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)" size="small">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="isTop" label="置顶" width="80" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.isTop === 1" type="warning" size="small">置顶</el-tag>
              <span v-else style="color: #999;">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="170">
            <template #default="{ row }">
              <span v-if="row.publishTime">{{ formatDateTime(row.publishTime) }}</span>
              <span v-else style="color: #999;">未发布</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" size="small" text @click="viewDetail(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button type="info" size="small" text @click="editAnnouncement(row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button 
                  :type="row.isTop === 1 ? 'warning' : 'default'" 
                  size="small" 
                  text
                  @click="toggleTop(row)"
                >
                  <el-icon><Top /></el-icon>
                  {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
                </el-button>
                <el-button 
                  :type="row.status === 1 ? 'warning' : 'success'" 
                  size="small" 
                  text
                  @click="toggleStatus(row)"
                >
                  <el-icon><component :is="row.status === 1 ? 'Lock' : 'Unlock'" /></el-icon>
                  {{ row.status === 1 ? '下架' : '发布' }}
                </el-button>
                <el-button type="danger" size="small" text @click="deleteAnnouncement(row)">
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
          :disabled="pagination.current <= 1"
          @click="handleCurrentChange(pagination.current - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="page-info">
          {{ pagination.current }} / {{ Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10))) }}
        </span>
        <el-button
          class="page-btn"
          :disabled="pagination.current >= Math.ceil((pagination.total || 1) / (pagination.size || 10))"
          @click="handleCurrentChange(pagination.current + 1)"
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

    <!-- 创建/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px" class="announcement-form">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="系统公告" :value="1" />
            <el-option label="活动公告" :value="2" />
            <el-option label="功能更新" :value="3" />
            <el-option label="维护通知" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input 
            v-model="formData.content" 
            type="textarea" 
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="置顶设置">
          <div
            class="toggle-control"
            :class="{ active: formData.isTop === 1 }"
            @click="toggleFormTop"
          >
            <div class="toggle-thumb"></div>
          </div>
          <span class="toggle-tip">开启后，公告将显示在列表最前面</span>
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" style="width: 200px" />
          <span style="margin-left: 10px; color: #999;">数字越大越靠前，默认0</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="secondary-btn" @click="dialogVisible = false">取消</el-button>
          <el-button class="primary-btn" @click="submitForm" :loading="submitLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="公告详情"
      width="700px"
    >
      <div v-if="currentAnnouncement" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公告ID">{{ currentAnnouncement.id }}</el-descriptions-item>
          <el-descriptions-item label="公告类型">
            <el-tag :type="getTypeTag(currentAnnouncement.type)" size="small">
              {{ getTypeName(currentAnnouncement.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布状态">
            <el-tag :type="getStatusTag(currentAnnouncement.status)" size="small">
              {{ getStatusName(currentAnnouncement.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否置顶">
            <el-tag v-if="currentAnnouncement.isTop === 1" type="warning" size="small">是</el-tag>
            <span v-else>否</span>
          </el-descriptions-item>
          <el-descriptions-item label="标题" :span="2">{{ currentAnnouncement.title }}</el-descriptions-item>
          <el-descriptions-item label="内容" :span="2">
            <div style="white-space: pre-wrap; line-height: 1.6;">{{ currentAnnouncement.content }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="发布时间" :span="2">
            <span v-if="currentAnnouncement.publishTime">{{ formatDateTime(currentAnnouncement.publishTime) }}</span>
            <span v-else style="color: #999;">未发布</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDateTime(currentAnnouncement.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Refresh, Plus, Search, Select, CloseBold, View, Edit, Delete, Top, Lock, Unlock } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { resolvePaginationTotal } from '@/utils/pagination'
import BackButton from '@/components/BackButton.vue'
import type { FormInstance, FormRules } from 'element-plus'

// 数据
const loading = ref(false)
const submitLoading = ref(false)
const announcementList = ref<any[]>([])
const selectedAnnouncements = ref<any[]>([])
const detailDialogVisible = ref(false)
const dialogVisible = ref(false)
const currentAnnouncement = ref<any>(null)
const lastUpdateTime = ref('')
const formRef = ref<FormInstance>()

// 统计信息
const stats = ref([
  { label: '总公告数', value: 0, icon: 'Bell', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '已发布', value: 0, icon: 'Check', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '草稿', value: 0, icon: 'Document', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '置顶公告', value: 0, icon: 'Top', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
])

// 搜索表单
const searchForm = reactive({
  type: null as number | null,
  status: null as number | null,
  keyword: ''
})

// 表单数据
const formData = reactive({
  id: null as number | null,
  title: '',
  content: '',
  type: 1,
  status: 0,
  isTop: 0,
  sortOrder: 0
})

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度在 10 到 2000 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ]
}

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 翻页跳转
const pageJump = ref(null)

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10)))
  let target = Number(pageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === pagination.current) return
  handleCurrentChange(target)
}

// 自动刷新定时器
let refreshTimer: any = null

// 计算对话框标题
const dialogTitle = computed(() => {
  return formData.id ? '编辑公告' : '发布公告'
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.type !== null) {
      params.type = searchForm.type
    }
    
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }
    
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    
    const response = await request.get('/admin/announcement/page', { params })
    
    if (response.code === 200 && response.data) {
      const data = response.data
      const list = data.list || data.records || []
      const resolvedTotal = resolvePaginationTotal(data, list)

      if (list.length > pagination.size && resolvedTotal <= list.length) {
        const start = (pagination.current - 1) * pagination.size
        announcementList.value = list.slice(start, start + pagination.size)
        pagination.total = list.length
      } else {
        announcementList.value = list
        pagination.total = resolvedTotal
      }

      lastUpdateTime.value = new Date().toLocaleString('zh-CN')
      updateStats()
    } else {
      ElMessage.error(response.message || '获取公告列表失败')
    }
  } catch (error: any) {
    console.error('加载公告列表失败:', error)
    ElMessage.error('加载公告列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 更新统计信息
const updateStats = () => {
  const total = pagination.total || announcementList.value.length
  const published = announcementList.value.filter(item => item.status === 1).length
  const draft = announcementList.value.filter(item => item.status === 0).length
  const top = announcementList.value.filter(item => item.isTop === 1).length
  
  // 需要从总数统计，这里先用当前页数据
  stats.value[0].value = pagination.total
  stats.value[1].value = published
  stats.value[2].value = draft
  stats.value[3].value = top
}

// 重置筛选
const handleReset = () => {
  searchForm.type = null
  searchForm.status = null
  searchForm.keyword = ''
  pagination.current = 1
  loadData()
}

// 自动检索（防抖）
let searchTimer: any = null
watch(
  () => [searchForm.type, searchForm.status, searchForm.keyword],
  () => {
    if (searchTimer) {
      clearTimeout(searchTimer)
    }
    searchTimer = setTimeout(() => {
      pagination.current = 1
      loadData()
    }, 500)
  },
  { deep: true }
)

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadData()
}

// 覆盖本页表格行悬停效果（去掉放大和阴影）
// 放在脚本末尾，仅影响本组件

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedAnnouncements.value = selection
}

// 清除选择
const clearSelection = () => {
  selectedAnnouncements.value = []
}

// 创建公告
const createAnnouncement = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑公告
const editAnnouncement = (row: any) => {
  formData.id = row.id
  formData.title = row.title
  formData.content = row.content
  formData.type = row.type
  formData.status = row.status
  formData.isTop = row.isTop || 0
  formData.sortOrder = row.sortOrder || 0
  dialogVisible.value = true
}

// 查看详情
const viewDetail = (row: any) => {
  currentAnnouncement.value = row
  detailDialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        let response
        if (formData.id) {
          response = await request.put(`/admin/announcement/${formData.id}`, formData)
        } else {
          response = await request.post('/admin/announcement', formData)
        }
        
        if (response.code === 200) {
          ElMessage.success(formData.id ? '更新公告成功' : '创建公告成功')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error: any) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败: ' + (error.message || '未知错误'))
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.title = ''
  formData.content = ''
  formData.type = 1
  formData.status = 0
  formData.isTop = 0
  formData.sortOrder = 0
  formRef.value?.resetFields()
}

// 切换状态
const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 2 : 1
  const action = newStatus === 1 ? '发布' : '下架'
  
  try {
    const response = await request.put(`/admin/announcement/${row.id}/status`, null, {
      params: { status: newStatus }
    })
    
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error: any) {
    console.error(`${action}失败:`, error)
    ElMessage.error(`${action}失败: ` + (error.message || '未知错误'))
  }
}

// 切换置顶
const toggleTop = async (row: any) => {
  const newTop = row.isTop === 1 ? 0 : 1
  const action = newTop === 1 ? '置顶' : '取消置顶'
  
  try {
    const response = await request.put(`/admin/announcement/${row.id}/top`, null, {
      params: { isTop: newTop }
    })
    
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error: any) {
    console.error(`${action}失败:`, error)
    ElMessage.error(`${action}失败: ` + (error.message || '未知错误'))
  }
}

// 删除
const deleteAnnouncement = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告"${row.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await request.delete(`/admin/announcement/${row.id}`)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 批量删除
const batchDelete = async () => {
  if (selectedAnnouncements.value.length === 0) {
    ElMessage.warning('请先选择要删除的公告')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedAnnouncements.value.length} 条公告吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedAnnouncements.value.map(item => item.id)
    const response = await request.delete('/admin/announcement/batch', { data: ids })
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      clearSelection()
      loadData()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败: ' + (error.message || '未知错误'))
    }
  }
}

const toggleFormTop = () => {
  formData.isTop = formData.isTop === 1 ? 0 : 1
}

// 格式化方法
const getTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '系统公告',
    2: '活动公告',
    3: '功能更新',
    4: '维护通知'
  }
  return typeMap[type] || '未知'
}

const getTypeTag = (type: number) => {
  const tagMap: Record<number, string> = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return tagMap[type] || ''
}

const getStatusName = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '草稿',
    1: '已发布',
    2: '已下架'
  }
  return statusMap[status] || '未知'
}

const getStatusTag = (status: number) => {
  const tagMap: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'warning'
  }
  return tagMap[status] || ''
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '--'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 自动刷新
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    loadData()
  }, 30000) // 30秒刷新一次
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 生命周期
onMounted(() => {
  loadData()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>


<style lang="scss">
@import '@/styles/admin-list.scss';

.detail-content {
  padding: 20px 0;
}

// 表单字段与输入框对齐
.announcement-form {
  :deep(.el-form-item) {
    align-items: center;
    margin-bottom: 18px;

    .el-form-item__label {
      padding-top: 0;
    }

    .el-form-item__content {
      margin-top: 0 !important;
    }

    // 多行文本域单独处理
    &.is-required .el-textarea {
      margin-top: 6px;
    }
  }
}

.toggle-control {
  width: 84px;
  height: 32px;
  border-radius: 999px;
  background: #f0f2f5;
  border: 1px solid #e4e7ed;
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 6px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;

  .toggle-thumb {
    width: 26px;
    height: 26px;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
    transition: transform 0.2s ease;
  }

  &.active {
    background: rgba(103, 194, 58, 0.18);
    border-color: rgba(103, 194, 58, 0.4);

    .toggle-thumb {
      transform: translateX(46px);
      background: #67c23a;
      box-shadow: 0 2px 8px rgba(103, 194, 58, 0.35);
    }
  }
}

.toggle-tip {
  margin-left: 12px;
  color: #999;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  .secondary-btn {
    border-radius: 6px;
    border: 1px solid #dcdfe6;
    color: #606266;
    background: #fff;

    &:hover {
      background: #f5f7fa;
      border-color: #c0c4cc;
      color: #303133;
    }
  }

  .primary-btn {
    border-radius: 6px;
    border: 1px solid #dcdfe6;
    background: #f5f7fa;
    color: #303133;
    font-weight: 600;

    &:hover:not([disabled]) {
      background: #e9ebf0;
    }

    &[disabled] {
      color: #c0c4cc;
      border-color: #e4e7ed;
    }
  }
}

// 覆盖本页表格行悬停效果（去掉放大和阴影，仅影响公告管理这张表）
.table-card-modern .modern-table {
  :deep(.el-table__body tr:hover) {
    background: #f5f7fa !important;
    transform: none !important;
    box-shadow: none !important;
  }
}
</style>



