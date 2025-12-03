<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="header-title">
          <h1>攻略管理</h1>
          <p>管理系统攻略内容与审核</p>
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
        <el-button type="success" class="action-btn" @click="exportPlans">
          <el-icon><Download /></el-icon>
          导出攻略
        </el-button>
      </div>
    </div>

    <!-- 攻略统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总攻略数</div>
          <div class="stat-value">{{ planStats.total }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待审核</div>
          <div class="stat-value">{{ planStats.pending }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已发布</div>
          <div class="stat-value">{{ planStats.published }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #a8a8a8 0%, #7f7f7f 100%)">
          <el-icon :size="24"><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已下架</div>
          <div class="stat-value">{{ planStats.unpublished }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
          <el-icon :size="24"><Close /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已驳回</div>
          <div class="stat-value">{{ planStats.rejected }}</div>
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
          <el-form-item label="攻略标题">
            <el-input v-model="searchForm.title" placeholder="请输入攻略标题" clearable />
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
          </el-form-item>
          <el-form-item label="目的地">
            <el-input v-model="searchForm.destination" placeholder="请输入目的地" clearable />
          </el-form-item>
          <el-form-item label="发布状态">
            <el-select v-model="searchForm.status" placeholder="请选择发布状态" clearable>
              <el-option label="已发布" :value="1" />
              <el-option label="已下架" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.auditStatus" placeholder="请选择审核状态" clearable>
              <el-option label="待审核" :value="0" />
              <el-option label="审核通过" :value="1" />
              <el-option label="审核拒绝" :value="2" />
            </el-select>
          </el-form-item>
        </div>
        <div class="filter-row">
          <el-form-item label="创建时间" style="flex: 0 0 320px;">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%;"
            />
          </el-form-item>
          <el-form-item label="排序方式" class="filter-actions">
            <el-select v-model="searchForm.sortBy" placeholder="请选择排序方式" clearable style="width: 200px;">
              <el-option label="浏览数（高到低）" value="viewCount" />
              <el-option label="点赞数（高到低）" value="likeCount" />
              <el-option label="评论数（高到低）" value="commentCount" />
            </el-select>
            <el-button class="reset-btn" @click="handleReset" style="margin-left: 12px;">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedPlans.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedPlans.length }}</strong> 个攻略</span>
          </div>
          <div class="batch-buttons">
            <!-- 如果选中的都是待审核状态，显示审核按钮 -->
            <template v-if="allSelectedArePending">
              <el-button type="success" size="small" @click="batchApprove">
                <el-icon><Check /></el-icon>
                批量通过
              </el-button>
              <el-button type="warning" size="small" @click="batchReject">
                <el-icon><Close /></el-icon>
                批量拒绝
              </el-button>
            </template>
            <!-- 如果选中的都是已发布状态，只显示下架按钮 -->
            <template v-else-if="allSelectedArePublished">
              <el-button type="warning" size="small" @click="batchUnpublish">
                <el-icon><ArrowDown /></el-icon>
                批量下架
              </el-button>
            </template>
            <!-- 如果选中的都是已下架状态，显示上架和删除按钮 -->
            <template v-else-if="allSelectedAreUnpublished">
              <el-button type="success" size="small" @click="batchPublish">
                <el-icon><ArrowUp /></el-icon>
                批量上架
              </el-button>
              <el-button type="danger" size="small" @click="batchDelete">
                <el-icon><Delete /></el-icon>
                批量删除
              </el-button>
            </template>
            <!-- 如果选中的都是已驳回状态，显示提示 -->
            <template v-else-if="allSelectedAreRejected">
              <span style="color: #999; margin-right: 12px;">已驳回状态无批量操作</span>
            </template>
            <!-- 混合状态 -->
            <template v-else>
              <span style="color: #999; margin-right: 12px;">已选择 {{ selectedPlans.length }} 个不同状态的攻略</span>
            </template>
            
            <!-- 取消选择按钮：始终显示 -->
            <el-button size="small" @click="clearSelection">
              <el-icon><CloseBold /></el-icon>
              取消选择
            </el-button>
          </div>
        </div>
      </el-card>
    </transition>

    <!-- 攻略列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="planList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="title" label="攻略标题" width="220" show-overflow-tooltip />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="destination" label="目的地" width="120" />
          <el-table-column prop="days" label="天数" width="80" align="center" />
          <el-table-column prop="personCount" label="人数" width="80" align="center">
            <template #default="{ row }">
              <span>{{ row.personCount || '-' }}人</span>
            </template>
          </el-table-column>
          <el-table-column prop="budget" label="预算" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600;">¥{{ row.budget || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="travelType" label="类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.travelType || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="bestSeason" label="季节" width="100" align="center">
            <template #default="{ row }">
              <span>{{ row.bestSeason || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="suitableFor" label="适合人群" width="110" align="center">
            <template #default="{ row }">
              <span>{{ row.suitableFor || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="difficultyLevel" label="难度" width="100" align="center">
            <template #default="{ row }">
              <el-rate 
                v-model="row.difficultyLevel" 
                disabled 
                size="small"
                :max="5"
                v-if="row.difficultyLevel"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="发布状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="审核状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getAuditStatusTag(row.auditStatus)">
                {{ getAuditStatusName(row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览数" width="90" align="center">
            <template #default="{ row }">
              <span style="color: #409eff;">{{ row.viewCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="likeCount" label="点赞数" width="90" align="center">
            <template #default="{ row }">
              <span style="color: #f56c6c;">{{ row.likeCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="commentCount" label="评论数" width="90" align="center">
            <template #default="{ row }">
              <span style="color: #67c23a;">{{ row.commentCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="tags" label="标签" width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <template v-if="row.tags">
                <el-tag 
                  v-for="(tag, index) in row.tags.split(',').slice(0, 2)" 
                  :key="index"
                  size="small"
                  style="margin-right: 4px;"
                >
                  {{ tag }}
                </el-tag>
              </template>
              <span v-else style="color: #999;">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170">
            <template #default="{ row }">
              <span>{{ formatDateTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="170">
            <template #default="{ row }">
              <span>{{ formatDateTime(row.publishTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <!-- 草稿状态 (status=0 且 auditStatus=0 但不是待审核)：无需操作 -->
                <template v-if="row.status === 0 && row.auditStatus !== 0 && row.auditStatus !== 2">
                  <span style="color: #999;">草稿状态</span>
                </template>
                <!-- 待审核状态 (status=0 且 auditStatus=0)：显示详情按钮 -->
                <template v-else-if="row.status === 0 && row.auditStatus === 0">
                  <el-button type="primary" size="small" text @click="viewDetail(row)">
                    <el-icon><View /></el-icon>
                    详情
                  </el-button>
                </template>
                <!-- 已驳回状态 (status=0 且 auditStatus=2)：等待用户重新提交 -->
                <template v-else-if="row.status === 0 && row.auditStatus === 2">
                  <span style="color: #999;">等待用户修改</span>
                </template>
                <!-- 已发布状态 (status=1)：详情 + 下架按钮 -->
                <template v-else-if="row.status === 1">
                  <el-button type="primary" size="small" text @click="viewDetail(row)">
                    <el-icon><View /></el-icon>
                    详情
                  </el-button>
                  <el-button type="warning" size="small" text @click="toggleStatus(row)">
                    <el-icon><ArrowDown /></el-icon>
                    下架
                  </el-button>
                </template>
                <!-- 已下架状态 (status=2)：详情 + 上架 + 删除按钮 -->
                <template v-else-if="row.status === 2">
                  <el-button type="primary" size="small" text @click="viewDetail(row)">
                    <el-icon><View /></el-icon>
                    详情
                  </el-button>
                  <el-button type="success" size="small" text @click="toggleStatus(row)">
                    <el-icon><ArrowUp /></el-icon>
                    上架
                  </el-button>
                  <el-button type="danger" size="small" text @click="deletePlan(row)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
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

    <!-- 审核拒绝对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="审核拒绝" width="500px">
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="攻略标题">
          <el-input :value="currentPlan.title" readonly />
        </el-form-item>
        <el-form-item label="作者">
          <el-input :value="currentPlan.author" readonly />
        </el-form-item>
        <el-form-item label="拒绝类型" prop="reasonType">
          <el-select v-model="rejectForm.reasonType" placeholder="请选择拒绝原因类型" style="width: 100%">
            <el-option label="内容违规" value="内容违规" />
            <el-option label="信息虚假" value="信息虚假" />
            <el-option label="图片不符" value="图片不符" />
            <el-option label="质量不达标" value="质量不达标" />
            <el-option label="涉及敏感信息" value="涉及敏感信息" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" prop="reasonDetail">
          <el-input 
            v-model="rejectForm.reasonDetail" 
            type="textarea" 
            :rows="4"
            placeholder="请输入详细的拒绝原因（必填，至少10个字）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejectLoading">
          <el-icon><Close /></el-icon>
          确定拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import BackButton from '@/components/BackButton.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Download, Search, Refresh, Check, Clock, 
  Close, ArrowDown, ArrowUp, Delete, Select, CloseBold, Upload, RefreshRight,
  Edit, Box, View
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { formatDateTime } from '@/utils'

const router = useRouter()

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref<NodeJS.Timeout | null>(null)
const lastUpdateTime = ref<string>('')

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
  destination: '',
  status: null,
  auditStatus: null,  // 新增：审核状态筛选
  dateRange: null,
  sortBy: null  // 排序方式：viewCount-浏览数, likeCount-点赞数, commentCount-评论数
})

// 审核拒绝对话框
const rejectDialogVisible = ref(false)
const rejectLoading = ref(false)
const rejectFormRef = ref()
const currentPlan = ref<any>({})
const rejectForm = reactive({
  reasonType: '',
  reasonDetail: ''
})
const rejectRules = {
  reasonType: [
    { required: true, message: '请选择拒绝原因类型', trigger: 'change' }
  ],
  reasonDetail: [
    { required: true, message: '请输入详细说明', trigger: 'blur' },
    { min: 10, message: '详细说明至少10个字', trigger: 'blur' }
  ]
}

// 攻略列表
const planList = ref([])
const loading = ref(false)

// 选中的攻略
const selectedPlans = ref([])

// 攻略统计（与用户端统计字段保持一致）
const planStats = ref({
  total: 0,
  pending: 0,       // 待审核
  published: 0,     // 已发布
  unpublished: 0,   // 已下架
  rejected: 0       // 已驳回
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

// 计算属性：检查选中的是否都是待审核状态（基于新的状态逻辑）
const allSelectedArePending = computed(() => {
  if (selectedPlans.value.length === 0) return false
  return selectedPlans.value.every((plan: any) => plan.status === 0 && plan.auditStatus === 0)
})

// 计算属性：检查选中的是否都是已发布状态
const allSelectedArePublished = computed(() => {
  if (selectedPlans.value.length === 0) return false
  return selectedPlans.value.every((plan: any) => plan.status === 1)
})

// 计算属性：检查选中的是否都是已下架状态
const allSelectedAreUnpublished = computed(() => {
  if (selectedPlans.value.length === 0) return false
  return selectedPlans.value.every((plan: any) => plan.status === 2)
})

// 计算属性：检查选中的是否都是已驳回状态（基于新的状态逻辑）
const allSelectedAreRejected = computed(() => {
  if (selectedPlans.value.length === 0) return false
  return selectedPlans.value.every((plan: any) => plan.status === 0 && plan.auditStatus === 2)
})

// 获取发布状态名称
const getStatusName = (status: number) => {
  const statusMap = {
    0: '草稿',
    1: '已发布',
    2: '已下架'
  }
  return statusMap[status] || '未知'
}

// 获取发布状态标签
const getStatusTag = (status: number) => {
  const tagMap = {
    0: '',        // 草稿：默认色
    1: 'success', // 已发布：绿色
    2: 'warning'  // 已下架：橙色
  }
  return tagMap[status] || ''
}

// 获取审核状态名称
const getAuditStatusName = (auditStatus: number) => {
  const statusMap = {
    0: '待审核',
    1: '审核通过',
    2: '审核拒绝'
  }
  return statusMap[auditStatus] || '未知'
}

// 获取审核状态标签
const getAuditStatusTag = (auditStatus: number) => {
  const tagMap = {
    0: 'info',    // 待审核：蓝色
    1: 'success', // 审核通过：绿色
    2: 'danger'   // 审核拒绝：红色
  }
  return tagMap[auditStatus] || ''
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadPlanList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    author: '',
    destination: '',
    status: null,
    auditStatus: null,
    dateRange: null,
    sortBy: null
  })
  handleSearch()
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [
    searchForm.title,
    searchForm.author,
    searchForm.destination,
    searchForm.status,
    searchForm.auditStatus,
    searchForm.dateRange,
    searchForm.sortBy
  ],
  () => {
    // 清除之前的定时器
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    // 设置新的定时器（防抖：500ms）
    searchTimeout = setTimeout(() => {
      handleSearch()
    }, 500)
  },
  { deep: true }
)

// 格式化当前时间
const formatCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

// 加载统计数据（获取全部数据，不分页）
const loadStats = async () => {
  try {
    console.log('开始加载统计数据...')
    
    // 获取全部数据来计算统计（不分页）
    const params: any = {
      page: 1,
      size: 9999  // 获取所有数据
    }
    
    const result = await request.get('/travel-plan/list', { params })
    console.log('统计数据结果:', result)
    
    if (result.code === 200 && result.data) {
      const allData = result.data.list || result.data
      // 基于全部数据更新统计
      updateStats(allData)
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载攻略列表
const loadPlanList = async () => {
  loading.value = true
  try {
    console.log('开始加载攻略列表...')
    
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.title) {
      params.keyword = searchForm.title
    }
    
    if (searchForm.destination) {
      params.destination = searchForm.destination
    }
    
    if (searchForm.status !== null && searchForm.status !== undefined) {
      params.status = searchForm.status
    }
    
    if (searchForm.auditStatus !== null && searchForm.auditStatus !== undefined) {
      params.auditStatus = searchForm.auditStatus
    }
    
    if (searchForm.author) {
      params.authorName = searchForm.author
    }
    
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      // 格式化日期为 yyyy-MM-dd HH:mm:ss 格式
      const formatDate = (date: Date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day} 00:00:00`
      }
      
      const formatEndDate = (date: Date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day} 23:59:59`
      }
      
      params.startTime = formatDate(new Date(searchForm.dateRange[0]))
      params.endTime = formatEndDate(new Date(searchForm.dateRange[1]))
    }
    
    const result = await request.get('/travel-plan/list', { params })
    console.log('攻略列表结果:', result)
    
    if (result.code === 200 && result.data) {
      // 直接使用后端返回的数据，不需要前端二次筛选
      let data = result.data.list || result.data
      
      // 如果选择了排序方式，进行前端排序（从高到低）
      if (searchForm.sortBy) {
        data = [...data].sort((a: any, b: any) => {
          const aValue = a[searchForm.sortBy] || 0
          const bValue = b[searchForm.sortBy] || 0
          return bValue - aValue  // 从高到低排序
        })
      }
      
      planList.value = data
      pagination.total = result.data.total || planList.value.length
    }
    
    // 更新最后刷新时间
    lastUpdateTime.value = formatCurrentTime()
    console.log('攻略列表加载完成，最后更新时间:', lastUpdateTime.value)
  } catch (error: any) {
    console.error('加载攻略列表失败:', error)
    ElMessage.error('加载攻略列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 更新统计数据（与用户端逻辑完全一致）
const updateStats = (data: any[]) => {
  console.log('========== 管理端统计数据 ==========')
  console.log('总数:', data.length)
  
  // 管理端不统计草稿（audit_status = NULL），草稿对管理员不可见
  // 使用Number()转换，确保类型一致性
  const pending = data.filter((p: any) => Number(p.auditStatus) === 0)
  const published = data.filter((p: any) => Number(p.auditStatus) === 1 && Number(p.status) === 1)
  const unpublished = data.filter((p: any) => Number(p.auditStatus) === 1 && (Number(p.status) === 0 || Number(p.status) === 2))
  const rejected = data.filter((p: any) => Number(p.auditStatus) === 2)
  
  console.log('待审核:', pending.length, pending.map(p => `ID:${p.id}`))
  console.log('已发布:', published.length, published.map(p => `ID:${p.id}`))
  console.log('已下架:', unpublished.length, unpublished.map(p => `ID:${p.id}`))
  console.log('已驳回:', rejected.length, rejected.map(p => `ID:${p.id}`))
  console.log('=====================================')
  
  planStats.value = {
    total: data.length,
    pending: pending.length,
    published: published.length,
    unpublished: unpublished.length,
    rejected: rejected.length
  }
}

// 查看详情
const viewDetail = (row: any) => {
  router.push(`/home/admin/plans/detail/${row.id}`)
}

// 审核通过
const approvePlan = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要审核通过攻略《${row.title}》吗？`,
      '审核通过确认',
      {
        confirmButtonText: '确定通过',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    // 调用新的审核API（带日志记录）
    const result = await request.post(`/travel-plan/${row.id}/audit-with-log`, null, {
      params: { auditStatus: 1 }
    })
    
    if (result.code === 200) {
      // 更新状态：审核通过后 status=1(已发布), auditStatus=1(审核通过)
      row.status = 1
      row.auditStatus = 1
      // 重新加载准确的统计数据
      await loadStats()
      ElMessage.success('审核通过成功！攻略已发布')
    } else {
      ElMessage.error(result.message || '审核失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('审核通过失败:', error)
      ElMessage.error('审核通过失败: ' + error.message)
    }
  }
}

// 审核拒绝 - 打开拒绝对话框
const rejectPlan = (row: any) => {
  currentPlan.value = row
  rejectForm.reasonType = ''
  rejectForm.reasonDetail = ''
  rejectDialogVisible.value = true
}

// 确认拒绝 - 提交拒绝原因
const confirmReject = async () => {
  try {
    // 验证表单
    await rejectFormRef.value.validate()
    
    rejectLoading.value = true
    
    // 组合拒绝原因
    const rejectReason = `${rejectForm.reasonType}：${rejectForm.reasonDetail}`
    
    // 调用新的审核API（带日志记录）
    const result = await request.post(`/travel-plan/${currentPlan.value.id}/audit-with-log`, null, {
      params: { 
        auditStatus: 2,
        rejectReason: rejectReason
      }
    })
    
    if (result.code === 200) {
      // 更新状态：审核拒绝后 status=0(草稿), auditStatus=2(审核拒绝)
      currentPlan.value.status = 0
      currentPlan.value.auditStatus = 2
      // 重新加载准确的统计数据
      await loadStats()
      ElMessage.success('审核拒绝成功！已通知用户修改')
      rejectDialogVisible.value = false
    } else {
      ElMessage.error(result.message || '审核拒绝失败')
    }
  } catch (error: any) {
    if (error !== 'validate') {
      console.error('审核拒绝失败:', error)
      ElMessage.error('审核拒绝失败: ' + error.message)
    }
  } finally {
    rejectLoading.value = false
  }
}

// 切换状态
const toggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '下架' : '上架'}攻略 ${row.title} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API切换状态
    const newStatus = row.status === 1 ? 2 : 1
    const result = await request.post(`/travel-plan/${row.id}/audit`, null, {
      params: { status: newStatus }
    })
    
    if (result.code === 200) {
      row.status = newStatus
      ElMessage.success(`${row.status === 1 ? '上架' : '下架'}成功`)
      // 刷新统计数据
      updateStats(planList.value)
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

// 删除攻略（物理删除）
const deletePlan = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除攻略《' + row.title + '》吗？删除后无法恢复！',
      '删除确认', 
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API删除攻略（物理删除）
    const result = await request.delete(`/travel-plan/${row.id}`)
    
    if (result.code === 200) {
      // 从列表中移除
      const index = planList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        planList.value.splice(index, 1)
        pagination.total--
      }
      // 重新加载准确的统计数据
      await loadStats()
      ElMessage.success('攻略删除成功')
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除攻略失败:', error)
      ElMessage.error('删除攻略失败: ' + error.message)
    }
  }
}

// 导出攻略（导出所有数据）
const exportPlans = async () => {
  try {
    // 获取全部数据统计
    const statsResult = await request.get('/travel-plan/list', { 
      params: { page: 1, size: 1 } 
    })
    const totalCount = statsResult.data?.total || planStats.value.total || 0
    
    // 确认导出
    await ElMessageBox.confirm(
      `确定要导出全部攻略数据吗？预计导出 ${totalCount} 条记录`,
      '导出确认',
      {
        confirmButtonText: '确定导出',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.info('正在导出数据，请稍候...')
    
    let dataToExport: any[] = []
    
    // 导出所有数据（不分页）
    try {
      const params: any = {
        page: 1,
        size: 9999
      }
      const result = await request.get('/travel-plan/list', { params })
      if (result.code === 200 && result.data) {
        dataToExport = result.data.list || result.data
      }
    } catch (error) {
      console.error('获取全部数据失败:', error)
      throw new Error('获取数据失败')
    }
    
    if (dataToExport.length === 0) {
      ElMessage.warning('没有数据可导出')
      return
    }
    
    // 准备导出数据（包含所有字段）
    const exportData = dataToExport.map((plan: any) => ({
      'ID': plan.id || '',
      '标题': plan.title || '',
      '作者': plan.author || '',
      '目的地': plan.destination || '',
      '天数': plan.days || 0,
      '人数': plan.personCount || '',
      '预算': plan.budget || 0,
      '类型': plan.travelType || '',
      '最佳季节': plan.bestSeason || '',
      '适合人群': plan.suitableFor || '',
      '难度等级': plan.difficultyLevel ? `${plan.difficultyLevel}星` : '',
      '发布状态': getStatusName(plan.status),
      '审核状态': getAuditStatusName(plan.auditStatus),
      '浏览数': plan.viewCount || 0,
      '点赞数': plan.likeCount || 0,
      '评论数': plan.commentCount || 0,
      '标签': plan.tags || '',
      '创建时间': plan.createTime || '',
      '发布时间': plan.publishTime || '',
      '拒绝原因': plan.rejectReason || ''
    }))
    
    // 转换为CSV格式
    const headers = Object.keys(exportData[0])
    const csvContent = [
      headers.join(','),
      ...exportData.map(row => headers.map(header => {
        let value = row[header]
        if (value === null || value === undefined) {
          value = ''
        }
        // 转换为字符串
        value = String(value)
        // 处理包含逗号、引号、换行符的值
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
    const fileName = `攻略数据_${dateStr}_${timeStr}_共${dataToExport.length}条.csv`
    
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
const handleSelectionChange = (selection: any[]) => {
  selectedPlans.value = selection
}

// 批量发布
const batchPublish = async () => {
  try {
    await ElMessageBox.confirm('确定要发布选中的攻略吗？', '提示', {
      type: 'success'
    })
    
    // 调用API批量发布攻略（使用Promise.allSettled避免一个失败全部失败）
    const publishPromises = selectedPlans.value.map(async (plan) => {
      try {
        const result = await request.post(`/travel-plan/${plan.id}/audit`, null, {
          params: { status: 1 }
        })
        return { success: true, plan, result }
      } catch (error) {
        console.error(`发布攻略 ${plan.id} 失败:`, error)
        return { success: false, plan, error }
      }
    })
    
    const results = await Promise.allSettled(publishPromises)
    
    let successCount = 0
    let failCount = 0
    
    results.forEach((result) => {
      if (result.status === 'fulfilled' && result.value.success) {
        successCount++
        // 更新前端状态
        result.value.plan.status = 1
      } else {
        failCount++
      }
    })
    
    if (successCount > 0) {
      // 刷新统计数据
      updateStats(planList.value)
      
      if (failCount > 0) {
        ElMessage.warning(`成功发布 ${successCount} 个攻略，${failCount} 个失败`)
      } else {
        ElMessage.success(`成功发布 ${successCount} 个攻略`)
      }
    } else {
      ElMessage.error('批量发布失败')
    }
    
    clearSelection()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量发布失败:', error)
      ElMessage.error('批量发布失败: ' + error.message)
    }
  }
}

// 批量下架
const batchUnpublish = async () => {
  try {
    await ElMessageBox.confirm('确定要下架选中的攻略吗？', '提示', {
      type: 'warning'
    })
    
    // 调用API批量下架攻略（使用Promise.allSettled避免一个失败全部失败）
    const unpublishPromises = selectedPlans.value.map(async (plan) => {
      try {
        const result = await request.post(`/travel-plan/${plan.id}/audit`, null, {
          params: { status: 2 }
        })
        return { success: true, plan, result }
      } catch (error) {
        console.error(`下架攻略 ${plan.id} 失败:`, error)
        return { success: false, plan, error }
      }
    })
    
    const results = await Promise.allSettled(unpublishPromises)
    
    let successCount = 0
    let failCount = 0
    
    results.forEach((result) => {
      if (result.status === 'fulfilled' && result.value.success) {
        successCount++
        // 更新前端状态
        result.value.plan.status = 2
      } else {
        failCount++
      }
    })
    
    if (successCount > 0) {
      // 刷新统计数据
      updateStats(planList.value)
      
      if (failCount > 0) {
        ElMessage.warning(`成功下架 ${successCount} 个攻略，${failCount} 个失败`)
      } else {
        ElMessage.success(`成功下架 ${successCount} 个攻略`)
      }
    } else {
      ElMessage.error('批量下架失败')
    }
    
    clearSelection()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量下架失败:', error)
      ElMessage.error('批量下架失败: ' + error.message)
    }
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的攻略吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    
    // 调用API批量删除攻略（使用Promise.allSettled避免一个失败全部失败）
    const deletePromises = selectedPlans.value.map(async (plan) => {
      try {
        const result = await request.delete(`/travel-plan/${plan.id}`)
        return { success: true, plan, result }
      } catch (error) {
        console.error(`删除攻略 ${plan.id} 失败:`, error)
        return { success: false, plan, error }
      }
    })
    
    const results = await Promise.allSettled(deletePromises)
    
    let successCount = 0
    let failCount = 0
    
    results.forEach((result, index) => {
      if (result.status === 'fulfilled' && result.value.success) {
        successCount++
      } else {
        failCount++
      }
    })
    
    if (successCount > 0) {
      // 从列表中移除已删除的攻略
      selectedPlans.value.forEach(plan => {
        const index = planList.value.findIndex(item => item.id === plan.id)
        if (index > -1) {
          planList.value.splice(index, 1)
          pagination.total--
        }
      })
      // 刷新统计数据
      updateStats(planList.value)
      
      if (failCount > 0) {
        ElMessage.warning(`成功删除 ${successCount} 个攻略，${failCount} 个失败`)
      } else {
        ElMessage.success(`成功删除 ${successCount} 个攻略`)
      }
    } else {
      ElMessage.error('批量删除失败')
    }
    
    clearSelection()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败: ' + error.message)
    }
  }
}

// 批量审核通过（使用新的API）
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要审核通过选中的 ${selectedPlans.value.length} 个攻略吗？`,
      '批量审核通过', 
      {
        confirmButtonText: '确定通过',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    // 调用新的审核API批量审核通过
    const approvePromises = selectedPlans.value.map(async (plan) => {
      try {
        const result = await request.post(`/travel-plan/${plan.id}/audit-with-log`, null, {
          params: { auditStatus: 1 }
        })
        return { success: true, plan, result }
      } catch (error) {
        console.error(`审核通过攻略 ${plan.id} 失败:`, error)
        return { success: false, plan, error }
      }
    })
    
    const results = await Promise.allSettled(approvePromises)
    
    let successCount = 0
    let failCount = 0
    
    results.forEach((result) => {
      if (result.status === 'fulfilled' && result.value.success) {
        successCount++
        // 更新状态
        result.value.plan.status = 1
        result.value.plan.auditStatus = 1
      } else {
        failCount++
      }
    })
    
    if (successCount > 0) {
      updateStats(planList.value)
      if (failCount > 0) {
        ElMessage.warning(`成功审核通过 ${successCount} 个攻略，${failCount} 个失败`)
      } else {
        ElMessage.success(`成功审核通过 ${successCount} 个攻略！`)
      }
    } else {
      ElMessage.error('批量审核通过失败')
    }
    
    clearSelection()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量审核通过失败:', error)
      ElMessage.error('批量审核通过失败: ' + error.message)
    }
  }
}

// 批量审核拒绝
const batchReject = async () => {
  try {
    // 批量拒绝需要输入原因
    const { value: reason } = await ElMessageBox.prompt(
      `请输入批量拒绝的原因（将应用于${selectedPlans.value.length}个攻略）：`,
      '批量审核拒绝',
      {
        confirmButtonText: '确定拒绝',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因（至少10个字）',
        inputValidator: (value) => {
          if (!value || value.trim().length < 10) {
            return '拒绝原因至少10个字'
          }
          return true
        },
        inputType: 'textarea'
      }
    )
    
    // 调用新的审核API批量审核拒绝
    const rejectPromises = selectedPlans.value.map(async (plan) => {
      try {
        const result = await request.post(`/travel-plan/${plan.id}/audit-with-log`, null, {
          params: { 
            auditStatus: 2,
            rejectReason: `批量拒绝：${reason}`
          }
        })
        return { success: true, plan, result }
      } catch (error) {
        console.error(`审核拒绝攻略 ${plan.id} 失败:`, error)
        return { success: false, plan, error }
      }
    })
    
    const results = await Promise.allSettled(rejectPromises)
    
    let successCount = 0
    let failCount = 0
    
    results.forEach((result) => {
      if (result.status === 'fulfilled' && result.value.success) {
        successCount++
        // 更新状态
        result.value.plan.status = 0
        result.value.plan.auditStatus = 2
      } else {
        failCount++
      }
    })
    
    if (successCount > 0) {
      updateStats(planList.value)
      if (failCount > 0) {
        ElMessage.warning(`成功拒绝 ${successCount} 个攻略，${failCount} 个失败`)
      } else {
        ElMessage.success(`成功拒绝 ${successCount} 个攻略！`)
      }
    } else {
      ElMessage.error('批量审核拒绝失败')
    }
    
    clearSelection()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量审核拒绝失败:', error)
      ElMessage.error('批量审核拒绝失败: ' + error.message)
    }
  }
}

// 清除选择
const clearSelection = () => {
  selectedPlans.value = []
}

// 获取行类名
const getRowClassName = ({ row, rowIndex }: { row: any; rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadPlanList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadPlanList()
}

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新攻略列表和统计数据...')
    loadStats()  // 刷新统计数据
    loadPlanList()  // 刷新列表数据
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
  loadStats()  // 先加载统计数据
  loadPlanList()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>
