<template>
  <div class="admin-list-modern">
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Picture /></el-icon>
        </div>
        <div class="header-title">
          <h1>轮播图管理</h1>
          <p>管理用户端首页轮播图内容</p>
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
        <el-button type="success" class="action-btn" @click="exportBanners">
          <el-icon><Download /></el-icon>
          导出轮播图
        </el-button>
        <el-button type="primary" class="action-btn" @click="createBanner">
          <el-icon><Plus /></el-icon>
          添加轮播图
        </el-button>
      </div>
    </div>

    <!-- 轮播图统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><Picture /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总轮播图</div>
          <div class="stat-value">{{ bannerStats.total }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">启用中</div>
          <div class="stat-value">{{ bannerStats.active }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总点击量</div>
          <div class="stat-value">{{ bannerStats.totalClicks }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">平均点击率</div>
          <div class="stat-value">{{ bannerStats.avgClickRate }}%</div>
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
          <el-form-item label="轮播图标题">
            <el-input v-model="searchForm.title" placeholder="请输入轮播图标题" clearable />
          </el-form-item>
          <el-form-item label="链接类型">
            <el-select v-model="searchForm.linkType" placeholder="请选择链接类型" clearable>
              <el-option label="无链接" :value="0" />
              <el-option label="内部页面" :value="1" />
              <el-option label="外部链接" :value="2" />
              <el-option label="景点详情" :value="3" />
              <el-option label="攻略详情" :value="4" />
              <el-option label="活动详情" :value="5" />
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
            <span>已选择 <strong>{{ selectedRows.length }}</strong> 个轮播图</span>
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
        :data="bannerList" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        class="modern-table"
        :row-class-name="getRowClassName"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="220">
          <template #default="{ row }">
            <img
              :src="row.imageUrl"
              class="table-image-large"
              alt="banner"
            />
          </template>
        </el-table-column>
        <el-table-column prop="label" label="标签" width="120" show-overflow-tooltip />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="subtitle" label="副标题" min-width="220" show-overflow-tooltip />
        <el-table-column label="显示位置" width="140">
          <template #default="{ row }">
            <el-tag :type="getPositionTag(row.position)" size="small">
              {{ getPositionText(row.position) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="链接类型" width="130">
          <template #default="{ row }">
            <div style="display: flex; flex-direction: column; gap: 4px;">
              <el-tag :type="getLinkTypeTag(row.linkType)" size="small">
                {{ getLinkTypeText(row.linkType) }}
              </el-tag>
              <span v-if="row.linkType === 3 && row.linkValue" style="font-size: 12px; color: #909399;">
                景点: {{ row.linkValue }}
              </span>
              <span v-else-if="row.linkType === 4 && row.linkValue" style="font-size: 12px; color: #909399;">
                攻略: {{ row.linkValue }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="链接值/位置" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div v-if="row.linkValue" style="display: flex; align-items: center; gap: 4px;">
              <el-icon v-if="row.linkType === 2" style="color: #409eff;"><Link /></el-icon>
              <el-icon v-else-if="row.linkType === 1" style="color: #67c23a;"><Document /></el-icon>
              <el-icon v-else style="color: #e6a23c;"><Position /></el-icon>
              <span :style="{ 
                color: row.linkType === 2 ? '#409eff' : (row.linkType === 1 ? '#67c23a' : '#e6a23c'),
                fontWeight: 500
              }">
                {{ row.linkValue }}
              </span>
            </div>
            <span v-else style="color: #909399;">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="displayOrder" label="顺序" width="80" sortable />
        <el-table-column label="状态" width="130">
          <template #default="{ row }">
            <div
              class="status-toggle"
              :class="{ active: row.status === 1 }"
              @click="toggleBannerStatus(row)"
            >
              <div class="status-thumb"></div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="clickCount" label="点击量" width="100" sortable />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" text @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" text @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 编辑 / 添加轮播图对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="920px"
      class="modern-dialog"
      @close="resetForm"
    >
      <div class="dialog-header-decoration"></div>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" class="edit-form">
        <el-form-item label="轮播图标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input 
            v-model="formData.label" 
            placeholder="请输入轮播图标签（显示在小风车处）"
            maxlength="12"
            show-word-limit
          />
          <div class="form-item-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>用于与主副标题区分，显示在轮播图左上角的小风车标签位置</span>
          </div>
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="formData.subtitle" placeholder="请输入副标题" />
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <div class="image-url-input-wrapper">
            <el-input 
              v-model="formData.imageUrl" 
              placeholder="请输入图片URL（建议尺寸：1200x400）或点击右侧按钮上传图片"
              clearable
            >
              <template #append>
                <el-button @click="showUploadDialog = true" :icon="Upload">
                  上传图片
                </el-button>
              </template>
            </el-input>
            <div v-if="formData.imageUrl" class="image-preview-small">
              <el-image 
                :src="formData.imageUrl" 
                fit="cover"
                style="width: 120px; height: 60px; border-radius: 4px;"
                :preview-src-list="[formData.imageUrl]"
              />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="显示位置" prop="position">
          <el-select 
            v-model="formData.position" 
            placeholder="请选择显示位置"
            class="position-select"
          >
            <el-option label="用户平台首页" value="HOME">
              <div class="position-option-item">
                <span class="position-option-label">用户平台首页</span>
                <span class="position-option-desc">显示在用户平台首页轮播图位置</span>
              </div>
            </el-option>
            <el-option label="用户平台文旅首页" value="CULTURE">
              <div class="position-option-item">
                <span class="position-option-label">用户平台文旅首页</span>
                <span class="position-option-desc">显示在用户平台文旅首页轮播图位置</span>
              </div>
            </el-option>
          </el-select>
          <div class="form-item-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>选择轮播图要显示的具体位置，确保图片在正确的页面展示</span>
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="链接类型" prop="linkType">
              <el-select 
                v-model="formData.linkType" 
                placeholder="请选择链接类型"
                @change="handleLinkTypeChange"
                class="link-type-select"
              >
                <el-option label="无链接" :value="0">
                  <div class="link-option-item">
                    <span class="link-option-label">无链接</span>
                    <span class="link-option-desc">点击无跳转</span>
                  </div>
                </el-option>
                <el-option label="内部页面" :value="1">
                  <div class="link-option-item">
                    <span class="link-option-label">内部页面</span>
                    <span class="link-option-desc">跳转到系统内页面</span>
                  </div>
                </el-option>
                <el-option label="外部链接" :value="2">
                  <div class="link-option-item">
                    <span class="link-option-label">外部链接</span>
                    <span class="link-option-desc">跳转到外部网站</span>
                  </div>
                </el-option>
                <el-option label="景点详情" :value="3">
                  <div class="link-option-item">
                    <span class="link-option-label">景点详情</span>
                    <span class="link-option-desc">根据景点标题跳转到详情页</span>
                  </div>
                </el-option>
                <el-option label="攻略详情" :value="4">
                  <div class="link-option-item">
                    <span class="link-option-label">攻略详情</span>
                    <span class="link-option-desc">根据攻略标题跳转到详情页</span>
                  </div>
                </el-option>
              </el-select>
              <div class="form-item-tip" v-if="formData.linkType !== null && formData.linkType !== undefined">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ getLinkTypeDescription(formData.linkType) }}</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="链接值" :required="formData.linkType !== null && formData.linkType !== 0">
              <el-input 
                v-model="formData.linkValue" 
                :placeholder="getLinkValuePlaceholder(formData.linkType)"
                :disabled="formData.linkType === 0 || formData.linkType === null"
              >
                <template #prefix v-if="formData.linkType === 2">
                  <el-icon><Link /></el-icon>
                </template>
              </el-input>
              <div class="form-item-tip" v-if="formData.linkType !== null && formData.linkType !== 0">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ getLinkValueDescription(formData.linkType) }}</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="显示顺序">
              <el-input-number v-model="formData.displayOrder" :min="0" placeholder="数字越小越靠前" />
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
          <el-button class="secondary-btn" @click="dialogVisible = false">取消</el-button>
          <el-button class="primary-btn" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传轮播图"
      width="600px"
      @close="uploadImageUrl = ''"
    >
      <div class="upload-dialog-content">
        <ImageUpload 
          v-model="uploadImageUrl"
          alt="轮播图"
        />
        <div class="upload-tip">
          <el-icon><InfoFilled /></el-icon>
          <span>建议图片尺寸：1200x400 像素，支持 JPG、PNG、GIF 格式，大小不超过 10MB</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="secondary-btn" @click="showUploadDialog = false">取消</el-button>
          <el-button 
            class="primary-btn" 
            @click="handleUploadConfirm"
            :disabled="!uploadImageUrl"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Picture, Plus, Download, Refresh, Search, Check, 
  View, TrendCharts, Edit, Delete, Select, CloseBold, InfoFilled, Link, Document, Position, Upload
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'
import ImageUpload from '@/components/ImageUpload.vue'
import { useUserStore } from '@/stores/user'
import { resolvePaginationTotal } from '@/utils/pagination'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加轮播图')
const submitting = ref(false)
const formRef = ref()
const lastUpdateTime = ref('')
const selectedRows = ref<any[]>([])
const showUploadDialog = ref(false)
const uploadImageUrl = ref('')
let refreshTimer: any = null

const userStore = useUserStore()

const bannerList = ref<any[]>([])

const bannerStats = reactive({
  total: 0,
  active: 0,
  totalClicks: 0,
  avgClickRate: '0.0'
})

const searchForm = reactive({
  title: '',
  linkType: null,
  status: null,
  createTimeRange: []
})

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

const formData = reactive<any>({
  id: null,
  title: '',
  label: '',
  subtitle: '',
  imageUrl: '',
  position: 'HOME',
  linkType: 0,
  linkValue: '',
  displayOrder: 0,
  startTime: null,
  endTime: null,
  status: 1
})

const formRules = {
  title: [{ required: true, message: '请输入轮播图标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片URL', trigger: 'blur' }],
  position: [{ required: true, message: '请选择显示位置', trigger: 'change' }],
  linkType: [{ required: true, message: '请选择链接类型', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    if (searchForm.title) {
      params.title = searchForm.title
    }
    if (searchForm.linkType !== null && searchForm.linkType !== undefined) {
      params.linkType = searchForm.linkType
    }
    if (searchForm.status !== null && searchForm.status !== undefined) {
      params.status = searchForm.status
    }
    
    const response = await request.get('/admin/banner/page', { params })
    if (response.code === 200 && response.data) {
      const list = response.data.records || response.data.list || []
      const resolvedTotal = resolvePaginationTotal(response.data, list)
      const shouldSlice =
        list.length > pagination.size &&
        resolvedTotal <= list.length

      if (shouldSlice) {
        const start = (pagination.current - 1) * pagination.size
        bannerList.value = list.slice(start, start + pagination.size)
        pagination.total = list.length
      } else {
        bannerList.value = list
        pagination.total = resolvedTotal
      }
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
    const response = await request.get('/admin/banner/stats')
    if (response.code === 200 && response.data) {
      Object.assign(bannerStats, response.data)
    }
  } catch (error) {
    console.error('加载统计失败:', error)
    // 失败时使用默认值
    bannerStats.total = 0
    bannerStats.active = 0
    bannerStats.totalClicks = 0
    bannerStats.avgClickRate = '0.0'
  }
}

// 创建轮播图
const createBanner = () => {
  dialogTitle.value = '添加轮播图'
  dialogVisible.value = true
}

// 编辑轮播图
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑轮播图'
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    label: row.label || row.tag || '',
    subtitle: row.subtitle || '',
    imageUrl: row.imageUrl,
    position: row.position || 'HOME',
    linkType: row.linkType || 0,
    linkValue: row.linkValue || '',
    displayOrder: row.displayOrder || 0,
    startTime: row.startTime ? new Date(row.startTime) : null,
    endTime: row.endTime ? new Date(row.endTime) : null,
    status: row.status !== undefined ? row.status : 1
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    let response
    if (formData.id) {
      response = await request.put(`/admin/banner/${formData.id}`, formData)
    } else {
      response = await request.post('/admin/banner', formData)
    }
    
    if (response.code === 200) {
      ElMessage.success(formData.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      loadData()
      loadStats()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error: any) {
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// 状态切换
const handleStatusChange = async (row: any) => {
  try {
    const response = await request.put(`/admin/banner/${row.id}/status`, null, {
      params: { status: row.status }
    })
    if (response.code === 200) {
      ElMessage.success('状态更新成功')
      loadStats()
    } else {
      ElMessage.error(response.message || '状态更新失败')
      row.status = row.status === 1 ? 0 : 1
    }
  } catch (error: any) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

const toggleBannerStatus = (row: any) => {
  row.status = row.status === 1 ? 0 : 1
  handleStatusChange(row)
}

// 删除轮播图
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除此轮播图吗？', '警告', { type: 'error' })
    const response = await request.delete(`/admin/banner/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 导出
const exportBanners = () => {
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
    linkType: null,
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
    searchForm.linkType,
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

// 清除选择
const clearSelection = () => {
  selectedRows.value = []
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的轮播图')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个轮播图吗？`,
      '批量删除',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    const response = await request.delete('/admin/banner/batch', { data: ids })
    
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
  formData.title = ''
  formData.label = ''
  formData.subtitle = ''
  formData.imageUrl = ''
  formData.position = 'HOME'
  formData.linkType = 0
  formData.linkValue = ''
  formData.displayOrder = 0
  formData.startTime = null
  formData.endTime = null
  formData.status = 1
  uploadImageUrl.value = ''
  showUploadDialog.value = false
}

// 确认上传图片
const handleUploadConfirm = () => {
  if (!uploadImageUrl.value) return
  
  formData.imageUrl = uploadImageUrl.value
  showUploadDialog.value = false
  ElMessage.success('图片URL已填充')
  
  nextTick(() => {
    formRef.value?.validateField?.('imageUrl')
  })
}

// 获取行类名
const getRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 链接类型变化处理
const handleLinkTypeChange = (value: number) => {
  // 如果选择无链接，清空链接值
  if (value === 0) {
    formData.linkValue = ''
  }
}

// 获取链接类型描述
const getLinkTypeDescription = (type: number) => {
  const descriptions: Record<number, string> = {
    0: '选择此选项后，轮播图点击不会跳转',
    1: '例如：/home/user/plans 或 /home/user/attractions',
    2: '例如：https://www.example.com',
    3: '请输入景点标题，系统会自动匹配对应景点',
    4: '请输入攻略标题，系统会自动匹配对应攻略'
  }
  return descriptions[type] || ''
}

// 获取链接值输入框的placeholder
const getLinkValuePlaceholder = (type: number) => {
  if (type === 0 || type === null) {
    return '无链接时无需填写'
  }
  const placeholders: Record<number, string> = {
    1: '例如：/home/user/plans 或 /home/user/attractions',
    2: '例如：https://www.example.com',
    3: '请输入景点标题，如：武隆天生三桥',
    4: '请输入攻略标题，如：重庆三日游指南'
  }
  return placeholders[type] || '根据链接类型填写对应的值'
}

// 获取链接值描述
const getLinkValueDescription = (type: number) => {
  const descriptions: Record<number, string> = {
    1: '填写系统内部页面路径，以 / 开头',
    2: '填写完整的URL地址，以 http:// 或 https:// 开头',
    3: '输入景点标题，系统会智能匹配并校验',
    4: '输入攻略标题，系统会智能匹配并校验'
  }
  return descriptions[type] || ''
}

// 获取位置文本
const getPositionText = (position: string) => {
  const positions: Record<string, string> = {
    'HOME': '用户平台首页',
    'CULTURE': '用户平台文旅首页'
  }
  return positions[position] || position || '未知'
}

// 获取位置标签类型
const getPositionTag = (position: string) => {
  const tags: Record<string, string> = {
    'HOME': 'success',
    'CULTURE': 'warning'
  }
  return tags[position] || ''
}

// 工具方法
const getLinkTypeText = (type: number) => {
  const types: Record<number, string> = {
    0: '无链接', 1: '内部页面', 2: '外部链接', 
    3: '景点详情', 4: '攻略详情'
  }
  return types[type] || '未知'
}

const getLinkTypeTag = (type: number) => {
  const tags: Record<number, string> = {
    0: 'info', 1: '', 2: 'warning', 3: 'success', 4: 'success'
  }
  return tags[type] || ''
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
// 图片预览样式
.banner-image {
  width: 130px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
}

:deep(.image-preview-dialog),
.image-preview-wrapper,
.preview-image,
.preview-close-btn {
  display: none;
}

.table-image-large {
  width: 120px;
  height: 68px;
  border-radius: 8px;
  object-fit: cover;
  display: block;
}

/* 添加 / 编辑轮播图对话框整体放大 + 表单内容放大 */
.modern-dialog {
  :deep(.el-dialog__header) {
    padding: 18px 24px 8px;
  }

  :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
  }

  :deep(.el-dialog__body) {
    padding: 20px 24px 10px;
  }

  :deep(.edit-form) {
    .el-form-item {
      margin-bottom: 18px;
    }

    .el-form-item__label {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
    }

    .el-input__wrapper,
    .el-textarea__inner,
    .el-select__wrapper,
    .el-date-editor.el-input__wrapper,
    .el-input-number .el-input__wrapper {
      min-height: 42px;
      border-radius: 4px;
      font-size: 14px;
    }

    .el-input__inner,
    .el-textarea__inner {
      font-size: 14px;
    }

    .el-radio,
    .el-radio__label {
      font-size: 14px;
    }
  }
}

// 图片预览对话框
:deep(.image-preview-dialog) {
  .el-dialog__body {
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #000;
  }

  .preview-image {
    max-width: 100%;
    max-height: 70vh;
    object-fit: contain;
  }
}

// 表单项提示样式
.form-item-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  padding: 8px 12px;
  background: #f0f9ff;
  border-left: 3px solid #409eff;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;

  .el-icon {
    color: #409eff;
    font-size: 14px;
    flex-shrink: 0;
  }

  span {
    flex: 1;
  }
}

// 链接类型选择器样式
:deep(.link-type-select) {
  .el-select__selected-item {
    font-weight: 500;
  }
}

// 位置选择器样式
:deep(.position-select) {
  .el-select__selected-item {
    font-weight: 500;
  }
}

// 链接选项样式
.link-option-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px 0;

  .link-option-label {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }

  .link-option-desc {
    font-size: 12px;
    color: #909399;
  }
}

:deep(.el-select-dropdown__item) {
  .link-option-item {
    .link-option-label {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }

    .link-option-desc {
      font-size: 12px;
      color: #909399;
    }
  }

  &.selected {
    .link-option-item .link-option-label {
      color: #409eff;
      font-weight: 600;
    }
  }
}

// 位置选项样式
.position-option-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px 0;

  .position-option-label {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }

  .position-option-desc {
    font-size: 12px;
    color: #909399;
  }
}

:deep(.el-select-dropdown__item) {
  .position-option-item {
    .position-option-label {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }

    .position-option-desc {
      font-size: 12px;
      color: #909399;
    }
  }

  &.selected {
    .position-option-item .position-option-label {
      color: #409eff;
      font-weight: 600;
    }
  }
}

// 图片URL输入框样式
.image-url-input-wrapper {
  width: 100%;
  
  .image-preview-small {
    margin-top: 12px;
    padding: 8px;
    background: #f5f7fa;
    border-radius: 4px;
    display: inline-block;
  }
}

// 上传对话框样式
.upload-dialog-content {
  .upload-tip {
    margin-top: 16px;
    padding: 12px;
    background: #f0f9ff;
    border-left: 3px solid #409eff;
    border-radius: 4px;
    display: flex;
    align-items: flex-start;
    gap: 8px;
    font-size: 13px;
    color: #606266;
    line-height: 1.6;
    
    .el-icon {
      color: #409eff;
      font-size: 16px;
      flex-shrink: 0;
      margin-top: 2px;
    }
  }
}

.status-toggle {
  width: 84px;
  height: 32px;
  border-radius: 999px;
  background: #f0f2f5;
  border: 1px solid #e4e7ed;
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 10px;
  font-size: 12px;
  color: #909399;
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;

  .status-thumb {
    position: absolute;
    width: 26px;
    height: 26px;
    border-radius: 50%;
    background: #fff;
    top: 2px;
    left: 2px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    transition: all 0.2s ease;
  }

  &.active {
    background: rgba(103, 194, 58, 0.18);
    border-color: rgba(103, 194, 58, 0.4);
    color: #2f855a;

    .status-thumb {
      transform: translateX(50px);
      background: #67c23a;
      box-shadow: 0 2px 8px rgba(103, 194, 58, 0.4);
    }
  }
}

:deep(.image-preview-dialog) {
  background: transparent;
  box-shadow: none;
  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 0;
  }
}

.image-preview-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.85);
  border-radius: 12px;
  padding: 20px;
}

.preview-image {
  max-width: 80vw;
  max-height: 80vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.45);
}

.preview-close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(0, 0, 0, 0.4);
  transition: all 0.2s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  .secondary-btn,
  .primary-btn {
    border-radius: 6px;
    background: #fff;
    border: 1px solid #dcdfe6;
    color: #606266 !important;
    font-weight: 500;
    box-shadow: none;

    &:hover,
    &:focus,
    &:active {
      background: #f5f7fa !important;
      border-color: #c0c4cc !important;
      color: #303133 !important;
      box-shadow: none !important;
    }
  }

  .secondary-btn {

    &:hover {
      background: #f5f7fa;
      border-color: #c0c4cc;
      color: #303133;
    }
  }

  .primary-btn {
    border-radius: 6px;
    &[disabled] {
      background: #f5f7fa;
      color: #c0c4cc;
      border-color: #e4e7ed;
      cursor: not-allowed;
    }
  }
}
</style>
