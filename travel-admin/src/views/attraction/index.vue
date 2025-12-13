<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Location /></el-icon>
        </div>
        <div class="header-title">
          <h1>景点管理</h1>
          <p>管理景点信息与状态</p>
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
        <el-button type="success" class="action-btn" @click="exportAttractions">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
        <el-button type="primary" class="action-btn" @click="createAttraction">
          <el-icon><Plus /></el-icon>
          添加景点
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Location /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总景点数</div>
          <div class="stat-value">{{ attractionStats.total }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">正常景点</div>
          <div class="stat-value">{{ attractionStats.active }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">维护中</div>
          <div class="stat-value">{{ attractionStats.maintenance }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总浏览量</div>
          <div class="stat-value">{{ attractionStats.totalViews }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
          <el-icon :size="24"><Lock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已关闭</div>
          <div class="stat-value">{{ attractionStats.closed }}</div>
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
          <el-form-item label="景点名称">
            <el-input v-model="searchForm.keyword" placeholder="请输入景点名称" clearable />
          </el-form-item>
          <el-form-item label="城市">
            <el-input v-model="searchForm.city" placeholder="请输入城市" clearable />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
              <el-option label="自然景观" :value="1" />
              <el-option label="人文景观" :value="2" />
              <el-option label="主题公园" :value="3" />
              <el-option label="博物馆" :value="4" />
              <el-option label="历史遗迹" :value="5" />
              <el-option label="美食街" :value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="维护中" :value="2" />
              <el-option label="已关闭" :value="0" />
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

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedAttractions.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedAttractions.length }}</strong> 个景点</span>
          </div>
          <div class="batch-buttons">
            <el-button type="success" size="small" @click="batchEnable">
              <el-icon><Check /></el-icon>
              批量启用
            </el-button>
            <el-button type="warning" size="small" @click="batchDisable">
              <el-icon><Close /></el-icon>
              批量禁用
            </el-button>
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

    <!-- 景点列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="attractionList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="coverImage" label="图片" width="120" align="center">
            <template #default="{ row }">
              <el-image 
                v-if="row.coverImage || row.image"
                :src="row.coverImage || row.image" 
                :preview-src-list="getImageList(row)"
                style="width: 80px; height: 50px; border-radius: 8px;"
                fit="cover"
              />
              <span v-else style="color: #c0c4cc;">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="景点名称" width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span style="color: #303133; font-weight: 500;">{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="city" label="城市" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              <span style="color: #303133;">{{ row.city }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="120" align="center">
            <template #default="{ row }">
              <el-tag>{{ getTypeName(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="地址" width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span style="color: #303133;">{{ row.address || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="ticketPrice" label="门票价格" width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.ticketPrice" style="color: #f56c6c; font-weight: 600;">
                ¥{{ row.ticketPrice }}
              </span>
              <span v-else style="color: #67c23a;">免费</span>
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="120" align="center">
            <template #default="{ row }">
              <el-rate 
                v-if="row.rating" 
                :model-value="row.rating" 
                disabled 
                show-score 
                text-color="#ff9900"
                :max="5"
              />
              <span v-else style="color: #c0c4cc;">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #606266;">{{ row.viewCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170">
            <template #default="{ row }">
              <span style="color: #909399;">{{ formatDateTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="320" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="info" size="small" text @click="editAttraction(row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <!-- 正常状态：显示设置为维护中和关闭按钮 -->
                <template v-if="row.status === 1">
                  <el-button type="warning" size="small" text @click="setMaintenance(row)">
                    <el-icon><Tools /></el-icon>
                    设为维护中
                  </el-button>
                  <el-button type="warning" size="small" text @click="toggleStatus(row)">
                    <el-icon><Lock /></el-icon>
                    关闭
                  </el-button>
                </template>
                <!-- 维护中状态：显示恢复开放和关闭按钮 -->
                <template v-else-if="row.status === 2">
                  <el-button type="success" size="small" text @click="setNormal(row)">
                    <el-icon><Check /></el-icon>
                    恢复开放
                  </el-button>
                  <el-button type="warning" size="small" text @click="toggleStatus(row)">
                    <el-icon><Lock /></el-icon>
                    关闭
                  </el-button>
                </template>
                <!-- 已关闭状态：显示开启按钮 -->
                <template v-else-if="row.status === 0">
                  <el-button type="success" size="small" text @click="toggleStatus(row)">
                    <el-icon><Unlock /></el-icon>
                    开启
                  </el-button>
                </template>
                <!-- 只有已关闭状态才显示删除按钮 -->
                <el-button 
                  v-if="row.status === 0"
                  type="danger" 
                  size="small" 
                  text 
                  @click="handleDeleteAttraction(row)"
                >
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

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import BackButton from '@/components/BackButton.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Location, Download, Plus, Search, Refresh, Check, Warning, 
  Edit, Lock, Unlock, Delete, Select, CloseBold, Close, Tools, View, ArrowLeft, ArrowRight
} from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils'
import { 
  getAttractionList, 
  deleteAttraction, 
  toggleAttractionStatus
} from '@/api/attraction'
import type { Attraction, AttractionListParams } from '@/api/attraction'
import { useRouter } from 'vue-router'

const router = useRouter()

// 搜索表单
const searchForm = reactive<AttractionListParams>({
  keyword: '',
  city: '',
  type: undefined,
  status: undefined
})

// 景点列表
const attractionList = ref<Attraction[]>([])
const loading = ref(false)

// 选中的景点
const selectedAttractions = ref<Attraction[]>([])

// 景点统计
const attractionStats = ref({
  total: 0,
  active: 0,
  maintenance: 0,
  closed: 0,
  totalViews: 0
})

// 获取类型名称
const getTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '自然景观',
    2: '人文景观',
    3: '主题公园',
    4: '博物馆',
    5: '历史遗迹',
    6: '美食街'
  }
  return typeMap[type] || '其他'
}

// 获取图片列表
const getImageList = (row: Attraction): string[] => {
  if (row.coverImage) return [row.coverImage]
  if (row.image) return [row.image]
  if (row.images) {
    if (typeof row.images === 'string') {
      try {
        return JSON.parse(row.images)
      } catch {
        return row.images.split(',').filter((url: string) => url.trim())
      }
    }
    if (Array.isArray(row.images)) {
      return row.images
    }
  }
  return []
}

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 翻页跳转
const pageJump = ref<number | null>(null)

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10)))
  let target = Number(pageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === pagination.current) return
  handleCurrentChange(target)
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

// 获取状态名称
const getStatusName = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '已关闭',
    1: '正常',
    2: '维护中'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签
const getStatusTag = (status: number) => {
  const tagMap: Record<number, string> = {
    0: 'danger',
    1: 'success',
    2: 'warning'
  }
  return tagMap[status] || ''
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadAttractionList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    city: '',
    type: undefined,
    status: undefined
  })
  pagination.current = 1
  loadAttractionList()
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [searchForm.keyword, searchForm.city, searchForm.type, searchForm.status],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.current = 1
      loadAttractionList()
    }, 500)
  },
  { deep: true }
)

// 加载景点列表
const loadAttractionList = async () => {
  loading.value = true
  try {
    const params: AttractionListParams = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    
    if (searchForm.city) {
      params.city = searchForm.city
    }
    
    if (searchForm.type !== undefined) {
      params.type = searchForm.type
    }
    
    if (searchForm.status !== undefined) {
      params.status = searchForm.status
    }
    
    const result = await getAttractionList(params)
    
    if (result.code === 200 && result.data) {
      attractionList.value = result.data.list || []
      pagination.total = result.data.total || 0
      
      // 更新统计数据
      updateStats(attractionList.value)
    } else {
      ElMessage.error(result.message || '加载景点列表失败')
    }
    
    // 更新最后刷新时间
    lastUpdateTime.value = formatCurrentTime()
  } catch (error: any) {
    console.error('加载景点列表失败:', error)
    ElMessage.error('加载景点列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = (data: Attraction[]) => {
  attractionStats.value = {
    total: data.length,
    active: data.filter(a => a.status === 1).length,
    maintenance: data.filter(a => a.status === 2).length,
    closed: data.filter(a => a.status === 0).length,
    totalViews: data.reduce((sum, a) => sum + (a.viewCount || 0), 0)
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 景点统计可以通过列表数据计算
    await loadAttractionList()
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 切换状态（开启/关闭）
const toggleStatus = async (row: Attraction) => {
  try {
    const action = row.status === 0 ? '开启' : '关闭'
    await ElMessageBox.confirm(
      `确定要${action}景点 ${row.name} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === 0 ? 1 : 0
    const result = await toggleAttractionStatus(row.id!, newStatus)
    
    if (result.code === 200) {
      row.status = newStatus
      updateStats(attractionList.value)
      ElMessage.success(`${action}成功`)
    } else {
      ElMessage.error(result.message || '状态更新失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('状态更新失败:', error)
      ElMessage.error('状态更新失败: ' + error.message)
    }
  }
}

// 设置为维护中
const setMaintenance = async (row: Attraction) => {
  try {
    await ElMessageBox.confirm(
      `确定要将景点 ${row.name} 设置为维护中吗？维护期间用户将无法访问此景点。`,
      '设为维护中',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await toggleAttractionStatus(row.id!, 2)
    
    if (result.code === 200) {
      row.status = 2
      updateStats(attractionList.value)
      ElMessage.success('已设置为维护中状态')
    } else {
      ElMessage.error(result.message || '设置失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('设置维护中失败:', error)
      ElMessage.error('设置失败: ' + error.message)
    }
  }
}

// 恢复开放（从维护中恢复到正常）
const setNormal = async (row: Attraction) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复景点 ${row.name} 的开放吗？恢复后用户可以正常访问。`,
      '恢复开放',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    const result = await toggleAttractionStatus(row.id!, 1)
    
    if (result.code === 200) {
      row.status = 1
      updateStats(attractionList.value)
      ElMessage.success('已恢复开放')
    } else {
      ElMessage.error(result.message || '恢复失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('恢复开放失败:', error)
      ElMessage.error('恢复失败: ' + error.message)
    }
  }
}

// 删除景点
const handleDeleteAttraction = async (row: Attraction) => {
  try {
    if (row.status !== 0) {
      ElMessage.warning('只有已关闭状态的景点才能删除！请先关闭景点。')
      return
    }
    
    await ElMessageBox.confirm('确定要删除这个景点吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deleteAttraction(row.id!)
    
    if (result.code === 200) {
      const index = attractionList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        attractionList.value.splice(index, 1)
        pagination.total--
      }
      updateStats(attractionList.value)
      ElMessage.success('景点删除成功')
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除景点失败:', error)
      ElMessage.error('删除景点失败: ' + error.message)
    }
  }
}

// 添加景点
const createAttraction = () => {
  router.push('/home/admin/attractions/create')
}

// 编辑景点
const editAttraction = (row: Attraction) => {
  router.push(`/home/admin/attractions/edit/${row.id}`)
}

// 导出景点
const exportAttractions = async () => {
  try {
    const totalCount = pagination.total || attractionStats.value.total || 0
    
    await ElMessageBox.confirm(
      `确定要导出全部景点数据吗？预计导出 ${totalCount} 条记录`,
      '导出确认',
      {
        confirmButtonText: '确定导出',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.info('正在导出数据，请稍候...')
    
    let dataToExport: Attraction[] = []
    
    try {
      const params: AttractionListParams = {
        page: 1,
        size: 9999
      }
      
      if (searchForm.keyword) params.keyword = searchForm.keyword
      if (searchForm.city) params.city = searchForm.city
      if (searchForm.type !== undefined) params.type = searchForm.type
      if (searchForm.status !== undefined) params.status = searchForm.status
      
      const result = await getAttractionList(params)
      if (result.code === 200 && result.data) {
        dataToExport = result.data.list || []
      }
    } catch (error) {
      console.error('获取全部数据失败:', error)
      throw new Error('获取数据失败')
    }
    
    if (dataToExport.length === 0) {
      ElMessage.warning('没有数据可导出')
      return
    }
    
    // 准备导出数据
    const exportData = dataToExport.map((attraction: Attraction) => ({
      'ID': attraction.id || '',
      '景点名称': attraction.name || '',
      '城市': attraction.city || '',
      '省份': attraction.province || '',
      '类型': getTypeName(attraction.type || 0),
      '地址': attraction.address || '',
      '门票价格': attraction.ticketPrice || '免费',
      '评分': attraction.rating || '',
      '浏览量': attraction.viewCount || 0,
      '收藏数': attraction.collectCount || 0,
      '状态': getStatusName(attraction.status || 0),
      '描述': attraction.description || '',
      '创建时间': attraction.createTime || '',
      '更新时间': attraction.updateTime || ''
    }))
    
    // 转换为CSV格式
    const headers = Object.keys(exportData[0])
    const csvContent = [
      headers.join(','),
      ...exportData.map(row => headers.map(header => {
        let value = row[header as keyof typeof row]
        if (value === null || value === undefined) {
          value = ''
        }
        value = String(value)
        if (value.includes(',') || value.includes('"') || value.includes('\n')) {
          value = `"${value.replace(/"/g, '""')}"`
        }
        return value
      }).join(','))
    ].join('\n')
    
    // 添加BOM以支持中文
    const BOM = '\uFEFF'
    const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    // 生成文件名
    const now = new Date()
    const dateStr = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
    const timeStr = `${String(now.getHours()).padStart(2, '0')}${String(now.getMinutes()).padStart(2, '0')}${String(now.getSeconds()).padStart(2, '0')}`
    const fileName = `景点数据_${dateStr}_${timeStr}_共${dataToExport.length}条.csv`
    
    link.setAttribute('href', url)
    link.setAttribute('download', fileName)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    ElMessage.success(`导出成功！共导出 ${dataToExport.length} 条数据`)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('导出失败:', error)
      ElMessage.error('导出失败: ' + (error.message || '未知错误'))
    }
  }
}

// 选择改变
const handleSelectionChange = (selection: Attraction[]) => {
  selectedAttractions.value = selection
}

// 批量启用
const batchEnable = async () => {
  try {
    await ElMessageBox.confirm('确定要启用选中的景点吗？', '提示', {
      type: 'success'
    })
    
    // TODO: 调用API批量启用景点
    selectedAttractions.value.forEach(attraction => {
      attraction.status = 1
    })
    ElMessage.success('批量启用成功')
    clearSelection()
    loadAttractionList()
  } catch {
    // 用户取消
  }
}

// 批量禁用
const batchDisable = async () => {
  try {
    await ElMessageBox.confirm('确定要禁用选中的景点吗？', '提示', {
      type: 'warning'
    })
    
    // TODO: 调用API批量禁用景点
    selectedAttractions.value.forEach(attraction => {
      attraction.status = 0
    })
    ElMessage.success('批量禁用成功')
    clearSelection()
    loadAttractionList()
  } catch {
    // 用户取消
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    const notClosed = selectedAttractions.value.filter(a => a.status !== 0)
    if (notClosed.length > 0) {
      ElMessage.warning(`有 ${notClosed.length} 个景点不是已关闭状态，无法删除！请先关闭这些景点。`)
      return
    }
    
    await ElMessageBox.confirm('确定要删除选中的景点吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    
    // TODO: 调用API批量删除景点
    selectedAttractions.value.forEach(attraction => {
      const index = attractionList.value.findIndex(item => item.id === attraction.id)
      if (index > -1) {
        attractionList.value.splice(index, 1)
        pagination.total--
      }
    })
    ElMessage.success('批量删除成功')
    clearSelection()
    loadAttractionList()
  } catch {
    // 用户取消
  }
}

// 清除选择
const clearSelection = () => {
  selectedAttractions.value = []
}

// 获取行类名
const getRowClassName = ({ row, rowIndex }: { row: Attraction; rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadAttractionList()
}

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref<NodeJS.Timeout | null>(null)

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新景点列表...')
    loadAttractionList()
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

onMounted(() => {
  loadAttractionList()
  loadStats()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style lang="scss" scoped>
// 景点管理特定样式
</style>
