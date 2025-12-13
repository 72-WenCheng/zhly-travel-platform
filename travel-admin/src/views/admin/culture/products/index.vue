<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><House /></el-icon>
        </div>
        <div class="header-title">
          <h1>农特产品管理</h1>
          <p>管理农特产品信息与状态</p>
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
        <el-button type="success" class="action-btn" @click="exportServices">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
          <el-button type="primary" class="action-btn" @click="createService">
          <el-icon><Plus /></el-icon>
          添加产品
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><House /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总产品数</div>
          <div class="stat-value">{{ serviceStats.total }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">正常产品</div>
          <div class="stat-value">{{ serviceStats.active }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">维护中</div>
          <div class="stat-value">{{ serviceStats.maintenance }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总销量</div>
          <div class="stat-value">{{ serviceStats.totalSales }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
          <el-icon :size="24"><Lock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已关闭</div>
          <div class="stat-value">{{ serviceStats.closed }}</div>
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
          <el-form-item label="产品名称">
            <el-input v-model="searchForm.keyword" placeholder="请输入产品名称" clearable />
          </el-form-item>
          <el-form-item label="产地">
            <el-input v-model="searchForm.origin" placeholder="请输入产地" clearable />
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
      <el-card class="batch-card-modern" v-if="selectedServices.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedServices.length }}</strong> 个产品</span>
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

    <!-- 服务列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="serviceList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="images" label="图片" width="120" align="center">
            <template #default="{ row }">
              <el-image 
                v-if="row.images && row.images.length > 0"
                :src="row.images[0]" 
                :preview-src-list="row.images"
                style="width: 80px; height: 50px; border-radius: 8px;"
                fit="cover"
              />
              <span v-else style="color: #c0c4cc;">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="产品名称" width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span style="color: #303133; font-weight: 500;">{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="origin" label="产地" width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <span style="color: #303133;">{{ row.origin }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="badge" label="标签" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.badge" type="danger" size="small">{{ row.badge }}</el-tag>
              <span v-else style="color: #c0c4cc;">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="120" align="center">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600;">¥{{ row.price }}{{ row.unit ? '/' + row.unit : '' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="100" align="center">
            <template #default="{ row }">
              <span v-if="row.stock === -1" style="color: #909399;">无限</span>
              <span v-else style="color: #303133;">{{ row.stock || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="sales" label="销量" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #606266;">{{ row.sales || 0 }}</span>
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
          <el-table-column prop="views" label="浏览量" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #606266;">{{ row.views || 0 }}</span>
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
                <el-button type="info" size="small" text @click="editService(row)">
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
                  @click="deleteService(row)"
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
  House, Download, Plus, Search, Refresh, Check, Warning, 
  Edit, Lock, Unlock, Delete, Select, CloseBold, Close, Tools, View, ArrowLeft, ArrowRight
} from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils'
import { 
  getProductList, 
  deleteProduct as deleteServiceApi, 
  toggleProductStatus as toggleServiceStatus,
  getProductStatistics as getServiceStatistics
} from '@/api/agriculturalProduct'
import type { AgriculturalProduct, ProductListParams } from '@/api/agriculturalProduct'
import { useRouter } from 'vue-router'

const router = useRouter()

// 搜索表单
const searchForm = reactive<ProductListParams>({
  keyword: '',
  origin: '',
  status: undefined
})

// 产品列表
const serviceList = ref<AgriculturalProduct[]>([])
const loading = ref(false)

// 选中的产品
const selectedServices = ref<AgriculturalProduct[]>([])

// 产品统计
const serviceStats = ref({
  total: 0,
  active: 0,
  maintenance: 0,
  closed: 0,
  totalSales: 0
})

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
  loadServiceList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    origin: '',
    status: undefined
  })
  pagination.current = 1
  loadServiceList()
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [searchForm.keyword, searchForm.origin, searchForm.status],
  () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
      pagination.current = 1
      loadServiceList()
    }, 500)
  },
  { deep: true }
)

// 加载产品列表
const loadServiceList = async () => {
  loading.value = true
  try {
    const params: ProductListParams = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    
    if (searchForm.origin) {
      params.origin = searchForm.origin
    }
    
    if (searchForm.status !== undefined) {
      params.status = searchForm.status
    }
    
    const result = await getProductList(params)
    
    if (result.code === 200 && result.data) {
      serviceList.value = result.data.list || []
      pagination.total = result.data.total || 0
      
      // 更新统计数据
      updateStats(serviceList.value)
    } else {
      ElMessage.error(result.message || '加载服务列表失败')
    }
    
    // 更新最后刷新时间
    lastUpdateTime.value = formatCurrentTime()
  } catch (error: any) {
    console.error('加载服务列表失败:', error)
    ElMessage.error('加载服务列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = (data: AgriculturalProduct[]) => {
  serviceStats.value = {
    total: data.length,
    active: data.filter(s => s.status === 1).length,
    maintenance: data.filter(s => s.status === 2).length,
    closed: data.filter(s => s.status === 0).length,
    totalSales: data.reduce((sum, s) => sum + (s.sales || 0), 0)
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const result = await getServiceStatistics()
    if (result.code === 200 && result.data) {
      serviceStats.value = {
        total: result.data.total || 0,
        active: result.data.active || 0,
        maintenance: result.data.maintenance || 0,
        closed: result.data.closed || 0,
        totalViews: result.data.totalViews || 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 切换状态（开启/关闭）
const toggleStatus = async (row: AgriculturalProduct) => {
  try {
    const action = row.status === 0 ? '开启' : '关闭'
    await ElMessageBox.confirm(
      `确定要${action}产品 ${row.name} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === 0 ? 1 : 0
    const result = await toggleServiceStatus(row.id!, newStatus)
    
    if (result.code === 200) {
      row.status = newStatus
      updateStats(serviceList.value)
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
const setMaintenance = async (row: AgriculturalProduct) => {
  try {
    await ElMessageBox.confirm(
      `确定要将产品 ${row.name} 设置为维护中吗？维护期间用户将无法购买此产品。`,
      '设为维护中',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await toggleServiceStatus(row.id!, 2)
    
    if (result.code === 200) {
      row.status = 2
      updateStats(serviceList.value)
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
const setNormal = async (row: AgriculturalProduct) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复产品 ${row.name} 的开放吗？恢复后用户可以正常购买。`,
      '恢复开放',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    const result = await toggleServiceStatus(row.id!, 1)
    
    if (result.code === 200) {
      row.status = 1
      updateStats(serviceList.value)
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

// 删除产品
const deleteService = async (row: AgriculturalProduct) => {
  try {
    if (row.status !== 0) {
      ElMessage.warning('只有已关闭状态的产品才能删除！请先关闭产品。')
      return
    }
    
    await ElMessageBox.confirm('确定要删除这个产品吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deleteServiceApi(row.id!)
    
    if (result.code === 200) {
      const index = serviceList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        serviceList.value.splice(index, 1)
        pagination.total--
      }
      updateStats(serviceList.value)
      ElMessage.success('产品删除成功')
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除服务失败:', error)
      ElMessage.error('删除服务失败: ' + error.message)
    }
  }
}

// 添加产品
const createService = () => {
  router.push('/home/admin/culture/products/create')
}

// 编辑产品
const editService = (row: AgriculturalProduct) => {
  router.push(`/home/admin/culture/products/edit/${row.id}`)
}

// 导出服务
const exportServices = async () => {
  try {
    const totalCount = pagination.total || serviceStats.value.total || 0
    
    await ElMessageBox.confirm(
      `确定要导出全部服务数据吗？预计导出 ${totalCount} 条记录`,
      '导出确认',
      {
        confirmButtonText: '确定导出',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.info('正在导出数据，请稍候...')
    
    let dataToExport: CultureService[] = []
    
    try {
      const params: ProductListParams = {
        page: 1,
        size: 9999
      }
      
      if (searchForm.keyword) params.keyword = searchForm.keyword
      if (searchForm.origin) params.origin = searchForm.origin
      if (searchForm.status !== undefined) params.status = searchForm.status
      
      const result = await getProductList(params)
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
    const exportData = dataToExport.map((product: AgriculturalProduct) => ({
      'ID': product.id || '',
      '产品名称': product.name || '',
      '产地': product.origin || '',
      '价格': product.price || '',
      '单位': product.unit || '',
      '库存': product.stock === -1 ? '无限' : (product.stock || 0),
      '销量': product.sales || 0,
      '评分': product.rating || '',
      '浏览量': product.views || 0,
      '状态': getStatusName(product.status || 0),
      '标签': product.badge || '',
      '描述': product.description || '',
      '特色': product.features ? product.features.join(',') : '',
      '亮点': product.highlights ? product.highlights.join(',') : '',
      '创建时间': product.createTime || '',
      '更新时间': product.updateTime || ''
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
    const fileName = `农特产品数据_${dateStr}_${timeStr}_共${dataToExport.length}条.csv`
    
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
const handleSelectionChange = (selection: AgriculturalProduct[]) => {
  selectedServices.value = selection
}

// 批量启用
const batchEnable = async () => {
  try {
    await ElMessageBox.confirm('确定要启用选中的产品吗？', '提示', {
      type: 'success'
    })
    
    // TODO: 调用API批量启用服务
    selectedServices.value.forEach(service => {
      service.status = 1
    })
    ElMessage.success('批量启用成功')
    clearSelection()
    loadServiceList()
  } catch {
    // 用户取消
  }
}

// 批量禁用
const batchDisable = async () => {
  try {
    await ElMessageBox.confirm('确定要禁用选中的产品吗？', '提示', {
      type: 'warning'
    })
    
    // TODO: 调用API批量禁用服务
    selectedServices.value.forEach(service => {
      service.status = 0
    })
    ElMessage.success('批量禁用成功')
    clearSelection()
    loadServiceList()
  } catch {
    // 用户取消
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    const notClosed = selectedServices.value.filter(s => s.status !== 0)
    if (notClosed.length > 0) {
      ElMessage.warning(`有 ${notClosed.length} 个产品不是已关闭状态，无法删除！请先关闭这些产品。`)
      return
    }
    
    await ElMessageBox.confirm('确定要删除选中的产品吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    
    // TODO: 调用API批量删除服务
    selectedServices.value.forEach(service => {
      const index = serviceList.value.findIndex(item => item.id === service.id)
      if (index > -1) {
        serviceList.value.splice(index, 1)
        pagination.total--
      }
    })
    ElMessage.success('批量删除成功')
    clearSelection()
    loadServiceList()
  } catch {
    // 用户取消
  }
}

// 清除选择
const clearSelection = () => {
  selectedServices.value = []
}

// 获取行类名
const getRowClassName = ({ row, rowIndex }: { row: AgriculturalProduct; rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadServiceList()
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
    console.log('自动刷新产品列表...')
    loadServiceList()
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
  loadServiceList()
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
// 农家乐管理特定样式 - 保持与景点管理一致的样式
</style>

