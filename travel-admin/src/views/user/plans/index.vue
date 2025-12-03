<template>
  <div class="user-plans-new">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><Document /></el-icon>
        </div>
        <div class="header-text">
          <h2>我的攻略</h2>
          <p class="page-desc">管理你创建的所有旅游攻略</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" size="large" @click="createPlan">
          <el-icon><Plus /></el-icon>
          创建新攻略
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><Files /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.total }}</div>
              <div class="stats-label">总攻略数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><EditPen /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.draft }}</div>
              <div class="stats-label">草稿</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><Loading /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pending }}</div>
              <div class="stats-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><Check /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.published }}</div>
              <div class="stats-label">已发布</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><Remove /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.unpublished }}</div>
              <div class="stats-label">已下架</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><CloseBold /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.rejected }}</div>
              <div class="stats-label">已驳回</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline class="filter-form">
        <el-form-item>
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="输入标题自动检索..." 
            clearable 
            size="large"
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="searchForm.destination" 
            placeholder="输入目的地自动检索..." 
            clearable 
            size="large"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-select 
            v-model="searchForm.auditStatus" 
            placeholder="选择审核状态" 
            clearable 
            size="large"
            style="width: 220px"
            popper-class="audit-status-select"
          >
            <el-option :value="null">
              <div class="status-option-item all-status">
                <div class="status-text">
                  <div class="status-label">全部状态</div>
                  <div class="status-desc">显示所有攻略</div>
                </div>
              </div>
            </el-option>
            <el-option label="待审核" :value="0">
              <div class="status-option-item pending-status">
                <div class="status-text">
                  <div class="status-label">待审核</div>
                  <div class="status-desc">等待管理员审核</div>
                </div>
              </div>
            </el-option>
            <el-option label="审核通过" :value="1">
              <div class="status-option-item approved-status">
                <div class="status-text">
                  <div class="status-label">审核通过</div>
                  <div class="status-desc">已发布或已下架</div>
                </div>
              </div>
            </el-option>
            <el-option label="审核拒绝" :value="2">
              <div class="status-option-item rejected-status">
                <div class="status-text">
                  <div class="status-label">审核拒绝</div>
                  <div class="status-desc">需要修改内容</div>
                </div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 攻略列表 -->
    <div class="plans-grid">
      <el-row :gutter="20">
        <el-col :span="8" v-for="plan in planList" :key="`${plan.id}-${refreshKey}`">
          <el-card class="plan-card" @click="handleView(plan)">
            <div class="plan-cover">
              <img :src="plan.coverImage || defaultCover" :alt="plan.title" />
              <div class="plan-status-badge" :class="getAuditStatusClass(plan)">
                {{ getAuditStatusText(plan) }}
              </div>
            </div>
            <div class="plan-content">
              <h3 class="plan-title">{{ plan.title }}</h3>
              <p class="plan-destination">
                <el-icon><Location /></el-icon>
                {{ plan.destination }}
              </p>
              <p class="plan-desc">{{ plan.description || '暂无描述' }}</p>
              
              <!-- 拒绝原因提示 -->
              <div v-if="plan.auditStatus === 2" class="reject-hint" @click.stop="viewRejectReason(plan)">
                <el-icon color="#F56C6C"><Warning /></el-icon>
                <span>审核未通过，点击查看原因</span>
              </div>
              
              <div class="plan-meta">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ plan.days || 0 }}天
                </span>
                <span class="meta-item">
                  <el-icon><Money /></el-icon>
                  ¥{{ plan.budget || 0 }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ plan.viewCount || 0 }}
                </span>
              </div>
              
              <div class="plan-actions">
                <!-- 草稿状态：编辑、提交审核、删除 -->
                <template v-if="getAuditStatusText(plan) === '草稿'">
                  <el-button class="action-btn" type="primary" size="small" @click.stop="handleEdit(plan)">
                    <el-icon><Edit /></el-icon> 编辑
                  </el-button>
                  <el-button class="action-btn" type="success" size="small" @click.stop="submitForAudit(plan)">
                    <el-icon><Upload /></el-icon> 提交审核
                  </el-button>
                  <el-button class="action-btn" type="danger" size="small" @click.stop="handleDelete(plan)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
                
                <!-- 待审核状态：撤回按钮 -->
                <template v-else-if="getAuditStatusText(plan) === '待审核'">
                  <el-button class="action-btn" type="warning" size="small" @click.stop="handleWithdraw(plan)">
                    <el-icon><RefreshLeft /></el-icon> 撤回
                  </el-button>
                  <span style="color: #E6A23C; font-size: 13px; margin-left: 10px;">
                    <el-icon style="vertical-align: middle;"><Clock /></el-icon>
                    审核中，可撤回修改
                  </span>
                </template>
                
                <!-- 审核通过且已发布：只显示删除按钮 -->
                <template v-else-if="getAuditStatusText(plan) === '已发布'">
                  <el-button class="action-btn" type="danger" size="small" @click.stop="handleDelete(plan)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
                
                <!-- 审核通过但被下架：只显示状态 -->
                <template v-else-if="getAuditStatusText(plan) === '已下架'">
                  <div class="status-info unpublished">
                    <el-icon><Close /></el-icon>
                    <span>已下架（仅管理员可删除）</span>
                  </div>
                </template>
                
                <!-- 审核拒绝：编辑、重新提交、删除 -->
                <template v-else-if="getAuditStatusText(plan) === '已驳回'">
                  <el-button class="action-btn" type="primary" size="small" @click.stop="handleEdit(plan)">
                    <el-icon><Edit /></el-icon> 编辑
                  </el-button>
                  <el-button class="action-btn" type="success" size="small" @click.stop="resubmitForAudit(plan)">
                    <el-icon><RefreshRight /></el-icon> 重新提交
                  </el-button>
                  <el-button class="action-btn" type="danger" size="small" @click.stop="handleDelete(plan)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty 
        v-if="planList.length === 0 && !loading" 
        description="还没有创建攻略哦"
        :image-size="200"
      >
        <el-button type="primary" @click="createPlan">创建第一篇攻略</el-button>
      </el-empty>
    </div>

    <!-- 加载更多提示 -->
    <div class="load-more-wrapper" v-if="planList.length > 0">
      <div v-if="loadingMore" class="loading-more">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <div v-else-if="hasMore" class="load-more-tip">
        <el-icon><ArrowDown /></el-icon>
        <span>继续下拉加载更多</span>
      </div>
      <div v-else-if="planList.length > 0" class="no-more">
        <el-icon><Check /></el-icon>
        <span>已加载全部内容</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Files, 
  Plus, 
  View, 
  Star, 
  Collection, 
  Location, 
  Calendar, 
  Money, 
  EditPen, 
  Delete,
  Upload,
  Check,
  Loading,
  CloseBold,
  Warning,
  RefreshRight,
  RefreshLeft,
  Remove,
  Search,
  ArrowLeft,
  ArrowRight,
  ArrowDown
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const defaultCover = 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=600&h=400&fit=crop'

// 是否使用模拟数据
const useMockData = ref(false)

// 统计数据
const stats = reactive({
  total: 0,
  draft: 0,        // 草稿
  pending: 0,      // 待审核
  published: 0,    // 已发布
  unpublished: 0,  // 已下架
  rejected: 0      // 已驳回
})

// 搜索表单
const searchForm = reactive<{
  keyword: string
  destination: string
  auditStatus: number | null
}>({
  keyword: '',
  destination: '',
  auditStatus: null
})

// 攻略列表
const planList = ref<any[]>([])
// 所有模拟数据（用于筛选）
const allMockPlans = ref<any[]>([])
// 刷新键，用于强制重新渲染
const refreshKey = ref(0)

// 无限滚动相关
const loadingMore = ref(false)
const hasMore = ref(true)
const pageSize = ref(6) // 每次加载的数量（减小以便验证滚动加载）
const currentPage = ref(1)
const totalCount = ref(0) // 总数量

// 加载攻略列表
const loadPlans = async (reset: boolean = false) => {
  if (reset) {
    loading.value = true
    currentPage.value = 1
    planList.value = []
    hasMore.value = true
  } else {
    if (loadingMore.value || !hasMore.value) return
    loadingMore.value = true
  }
  
  // 使用模拟数据
  if (useMockData.value) {
    setTimeout(() => {
      loadMockData()
      filterMockData(reset)
      if (reset) {
        loading.value = false
      } else {
        loadingMore.value = false
      }
    }, 300) // 模拟加载延迟
    return
  }
  
  // 使用真实API
  try {
    // 优先从userStore获取当前标签页的用户信息
    let userInfo = userStore.userInfo
    if (!userInfo) {
      // 如果userStore没有，从sessionStorage获取（当前标签页）
      const sessionUserInfo = sessionStorage.getItem('travel_user_info')
      if (sessionUserInfo) {
        userInfo = JSON.parse(sessionUserInfo)
      } else {
        // 最后从localStorage获取（向后兼容）
        const localUserInfo = localStorage.getItem('travel_user_info')
        if (localUserInfo) {
          userInfo = JSON.parse(localUserInfo)
        }
      }
    }
    
    if (!userInfo || !userInfo.id) {
      ElMessage.error('无法获取用户信息，请重新登录')
      if (reset) loading.value = false
      else loadingMore.value = false
      return
    }
    
    const userId = userInfo.id

    const params = {
      page: currentPage.value,
      size: pageSize.value,
      authorId: userId,
      ...searchForm
    }

    console.log('🔍 开始加载攻略列表，用户ID:', userId, '页码:', currentPage.value, '每页数量:', pageSize.value, '重置:', reset, '请求参数:', params)

    const result = await request.get('/travel-plan/list', { params })
    
    console.log('📡 API响应:', result)
    
    if (result.code === 200 && result.data) {
      const newPlans = result.data.list || []
      totalCount.value = result.data.total || 0
      
      // 追加或替换数据
      if (reset) {
        planList.value = newPlans
        // 重置时，如果加载的数据等于pageSize，说明可能还有更多
        hasMore.value = newPlans.length >= pageSize.value
        // 更新页码（只有在还有更多数据时才更新）
        if (hasMore.value && newPlans.length > 0) {
          currentPage.value = 2 // 下次加载第2页
        } else {
          currentPage.value = 1
        }
      } else {
        planList.value = [...planList.value, ...newPlans]
        // 判断是否还有更多数据
        hasMore.value = planList.value.length < totalCount.value
        
        // 如果本次加载的数据少于pageSize，说明没有更多了
        if (newPlans.length < pageSize.value) {
          hasMore.value = false
        }
        
        // 更新页码（只有在还有更多数据时才更新）
        if (hasMore.value && newPlans.length > 0) {
          currentPage.value++
        }
      }
      
      console.log('✅ 攻略列表加载成功，当前', planList.value.length, '条，总共', totalCount.value, '条，还有更多:', hasMore.value, '当前页码:', currentPage.value)
    }
  } catch (error) {
    console.error('加载攻略列表失败，切换到模拟数据:', error)
    useMockData.value = true
    loadMockData()
    filterMockData(reset)
  } finally {
    if (reset) {
      loading.value = false
    } else {
      loadingMore.value = false
    }
    // 强制刷新列表，确保状态标签更新
    refreshKey.value++
  }
}

// 加载统计数据
const loadStats = async () => {
  // 使用模拟数据时直接返回
  if (useMockData.value) {
    return
  }
  
  try {
    // 优先从userStore获取当前标签页的用户信息
    let userInfo = userStore.userInfo
    if (!userInfo) {
      // 如果userStore没有，从sessionStorage获取（当前标签页）
      const sessionUserInfo = sessionStorage.getItem('travel_user_info')
      if (sessionUserInfo) {
        userInfo = JSON.parse(sessionUserInfo)
      } else {
        // 最后从localStorage获取（向后兼容）
        const localUserInfo = localStorage.getItem('travel_user_info')
        if (localUserInfo) {
          userInfo = JSON.parse(localUserInfo)
        }
      }
    }
    
    if (!userInfo || !userInfo.id) {
      console.error('无法获取用户信息')
      return
    }
    
    const userId = userInfo.id

    // 获取所有攻略数据以统计（不分页）
    const result = await request.get('/travel-plan/list', {
      params: {
        page: 1,
        size: 9999,
        authorId: userId
      }
    })

    if (result.code === 200 && result.data) {
      const allPlans = result.data.list || []
      
      stats.total = allPlans.length
      
      // 使用getAuditStatusText统一判断
      stats.draft = allPlans.filter((p: any) => getAuditStatusText(p) === '草稿').length
      stats.pending = allPlans.filter((p: any) => getAuditStatusText(p) === '待审核').length
      stats.published = allPlans.filter((p: any) => getAuditStatusText(p) === '已发布').length
      stats.unpublished = allPlans.filter((p: any) => getAuditStatusText(p) === '已下架').length
      stats.rejected = allPlans.filter((p: any) => getAuditStatusText(p) === '已驳回').length
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载模拟数据
const loadMockData = () => {
  // 只在第一次加载时初始化数据
  if (allMockPlans.value.length === 0) {
    allMockPlans.value = [
      {
        id: 1,
        title: '重庆三日游攻略',
        destination: '重庆',
        description: '探索山城魅力，品尝正宗火锅，欣赏绝美夜景',
        coverImage: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&h=400&fit=crop',
        days: 3,
        budget: 1500,
        status: 1,
        viewCount: 1256,
        likeCount: 89,
        collectCount: 45,
        createTime: '2025-10-20 10:30:00'
      },
      {
        id: 2,
        title: '成都休闲游',
        destination: '成都',
        description: '慢生活体验，熊猫基地，宽窄巷子，春熙路',
        coverImage: 'https://images.unsplash.com/photo-1548013146-72479768bada?w=600&h=400&fit=crop',
        days: 2,
        budget: 800,
        status: 0,
        viewCount: 0,
        likeCount: 0,
        collectCount: 0,
        createTime: '2025-10-22 14:20:00'
      },
      {
        id: 3,
        title: '西安历史文化之旅',
        destination: '西安',
        description: '探索古都文化，兵马俑，大雁塔，回民街美食',
        coverImage: 'https://images.unsplash.com/photo-1564415637254-92c66292cd4e?w=600&h=400&fit=crop',
        days: 4,
        budget: 2000,
        status: 2,
        viewCount: 0,
        likeCount: 0,
        collectCount: 0,
        createTime: '2025-10-23 09:15:00'
      }
    ]
    
    stats.total = 3
    // 模拟数据统计
    stats.draft = allMockPlans.value.filter((p: any) => !p.auditStatus || p.auditStatus === null).length
    stats.pending = allMockPlans.value.filter((p: any) => Number(p.auditStatus) === 0).length
    stats.published = allMockPlans.value.filter((p: any) => Number(p.auditStatus) === 1 && Number(p.status) === 1).length
    stats.unpublished = allMockPlans.value.filter((p: any) => Number(p.auditStatus) === 1 && Number(p.status) === 0).length
    stats.rejected = allMockPlans.value.filter((p: any) => Number(p.auditStatus) === 2).length
  }
}

// 筛选模拟数据
const filterMockData = (reset: boolean = false) => {
  let filtered = [...allMockPlans.value]
  
  // 按标题筛选
  if (searchForm.keyword) {
    filtered = filtered.filter(plan => 
      plan.title.toLowerCase().includes(searchForm.keyword.toLowerCase())
    )
  }
  
  // 按目的地筛选
  if (searchForm.destination) {
    filtered = filtered.filter(plan => 
      plan.destination.toLowerCase().includes(searchForm.destination.toLowerCase())
    )
  }
  
  // 按审核状态筛选
  if (searchForm.auditStatus !== null && searchForm.auditStatus !== undefined) {
    filtered = filtered.filter(plan => plan.auditStatus === searchForm.auditStatus)
  }
  
  // 更新总数
  totalCount.value = filtered.length
  
  // 分页处理
  if (reset) {
    const start = 0
    const end = pageSize.value
    planList.value = filtered.slice(start, end)
    hasMore.value = filtered.length > pageSize.value
    // 更新页码（只有在还有更多数据时才更新）
    if (hasMore.value && planList.value.length > 0) {
      currentPage.value = 2 // 下次加载第2页
    } else {
      currentPage.value = 1
    }
  } else {
    const start = planList.value.length
    const end = start + pageSize.value
    const newPlans = filtered.slice(start, end)
    planList.value = [...planList.value, ...newPlans]
    hasMore.value = planList.value.length < filtered.length
    
    // 更新页码（只有在还有更多数据时才更新）
    if (hasMore.value && newPlans.length > 0) {
      currentPage.value++
    }
  }
  
  console.log('✅ 模拟数据筛选完成，当前', planList.value.length, '条，总共', totalCount.value, '条，还有更多:', hasMore.value, '当前页码:', currentPage.value)
}

// 创建攻略
const createPlan = () => {
  router.push('/home/user/plans/create')
}

// 查看攻略
const handleView = (plan: any) => {
  router.push(`/home/user/plans/detail/${plan.id}`)
}

// 编辑攻略
const handleEdit = (plan: any) => {
  router.push(`/home/user/plans/edit/${plan.id}`)
}

// 发布攻略
// 删除攻略
const handleDelete = async (plan: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇攻略吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })

    const result = await request.delete(`/travel-plan/${plan.id}`)

    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadPlans(true)
      await loadStats()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交审核
const submitForAudit = async (plan: any) => {
  try {
    // 验证必要字段
    if (!plan.title || !plan.destination) {
      ElMessage.warning('请先完善攻略的标题和目的地信息')
      return
    }
    
    await ElMessageBox.confirm(
      '提交后将进入审核流程，审核通过后自动发布。确定要提交吗？',
      '提交审核',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 优先从userStore获取当前标签页的用户信息
    let userInfo = userStore.userInfo
    if (!userInfo) {
      const sessionUserInfo = sessionStorage.getItem('travel_user_info')
      if (sessionUserInfo) {
        userInfo = JSON.parse(sessionUserInfo)
      } else {
        const localUserInfo = localStorage.getItem('travel_user_info')
        if (localUserInfo) {
          userInfo = JSON.parse(localUserInfo)
        }
      }
    }
    
    if (!userInfo || !userInfo.id) {
      ElMessage.error('无法获取用户信息')
      return
    }
    
    const result = await request.post(`/travel-plan/${plan.id}/submit-audit`, null, {
      params: { userId: userInfo.id }
    })

    if (result.code === 200) {
      ElMessage.success('提交成功，请耐心等待管理员审核')
      
      // 立即重新加载数据
      await loadPlans(true)
      await loadStats()
    } else {
      ElMessage.error(result.message || '提交失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败: ' + (error.message || '未知错误'))
    }
  }
}

// 重新提交审核（审核被拒后）
const resubmitForAudit = async (plan: any) => {
  try {
    await ElMessageBox.confirm(
      '请确保已根据拒绝原因修改了攻略内容。确定要重新提交审核吗？',
      '重新提交',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 优先从userStore获取当前标签页的用户信息
    let userInfo = userStore.userInfo
    if (!userInfo) {
      const sessionUserInfo = sessionStorage.getItem('travel_user_info')
      if (sessionUserInfo) {
        userInfo = JSON.parse(sessionUserInfo)
      } else {
        const localUserInfo = localStorage.getItem('travel_user_info')
        if (localUserInfo) {
          userInfo = JSON.parse(localUserInfo)
        }
      }
    }
    
    if (!userInfo || !userInfo.id) {
      ElMessage.error('无法获取用户信息')
      return
    }
    
    const result = await request.post(`/travel-plan/${plan.id}/resubmit`, null, {
      params: { userId: userInfo.id }
    })

    if (result.code === 200) {
      ElMessage.success('重新提交成功，请耐心等待管理员审核')
      await loadPlans(true)
      await loadStats()
    } else {
      ElMessage.error(result.message || '重新提交失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('重新提交失败: ' + (error.message || '未知错误'))
    }
  }
}

// 撤回审核
const handleWithdraw = async (plan: any) => {
  try {
    await ElMessageBox.confirm(
      '撤回后攻略将恢复为草稿状态，管理员将无法继续审核。确定要撤回吗？',
      '撤回审核',
      {
        confirmButtonText: '确定撤回',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const result = await request.put(`/travel-plan/${plan.id}/withdraw`)

    if (result.code === 200) {
      ElMessage.success('撤回成功，攻略已恢复为草稿状态')
      await loadPlans(true)
      await loadStats()
    } else {
      ElMessage.error(result.message || '撤回失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('撤回失败: ' + (error.message || '未知错误'))
    }
  }
}

// 查看拒绝原因
const viewRejectReason = (plan: any) => {
  const reason = plan.rejectReason || '管理员未提供详细说明'
  ElMessageBox.alert(
    `<div style="line-height: 1.8; padding: 10px;">
      <div style="margin-bottom: 20px;">
        <div style="background: linear-gradient(135deg, #FEF0F0 0%, #FFF5F5 100%); padding: 16px; border-radius: 8px; border-left: 4px solid #F56C6C; box-shadow: 0 2px 8px rgba(245, 108, 108, 0.1);">
          <p style="color: #F56C6C; font-size: 14px; line-height: 1.8; margin: 0; white-space: pre-wrap;">
            ${reason}
          </p>
        </div>
      </div>
      
      <div style="background: #F4F4F5; padding: 12px 16px; border-radius: 6px; display: flex; align-items: center; gap: 8px;">
        <span style="font-size: 18px;">💡</span>
        <p style="color: #606266; font-size: 13px; margin: 0; line-height: 1.6;">
          请根据以上原因修改攻略内容后，点击<strong style="color: #67C23A;">「重新提交」</strong>按钮重新提交审核
        </p>
      </div>
    </div>`,
    '',
    {
      confirmButtonText: '知道了',
      dangerouslyUseHTMLString: true,
      customClass: 'reject-reason-dialog'
    }
  )
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  planList.value = []
  hasMore.value = true
  loadPlans(true)
}

// 获取滚动容器
const getScrollContainer = () => {
  // 查找 el-main 容器（Element Plus 布局的滚动容器）
  return document.querySelector('.el-main') || document.querySelector('.main-content') || window
}

// 滚动监听
const handleScroll = (event?: Event) => {
  // 获取滚动容器
  const scrollContainer = getScrollContainer()
  
  let scrollTop = 0
  let containerHeight = 0
  let scrollHeight = 0
  
  if (scrollContainer === window) {
    // window 滚动
    scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0
    containerHeight = window.innerHeight || document.documentElement.clientHeight
    scrollHeight = Math.max(
      document.body.scrollHeight,
      document.body.offsetHeight,
      document.documentElement.clientHeight,
      document.documentElement.scrollHeight,
      document.documentElement.offsetHeight
    )
  } else {
    // 容器滚动
    const container = scrollContainer as HTMLElement
    scrollTop = container.scrollTop || 0
    containerHeight = container.clientHeight || 0
    scrollHeight = container.scrollHeight || 0
  }
  
  // 当滚动到距离底部50px时加载更多（减小阈值，更容易触发）
  const threshold = 50
  const isNearBottom = scrollTop + containerHeight >= scrollHeight - threshold
  
  console.log('滚动监听:', {
    scrollTop,
    containerHeight,
    scrollHeight,
    isNearBottom,
    hasMore: hasMore.value,
    loadingMore: loadingMore.value,
    loading: loading.value,
    currentCount: planList.value.length
  })
  
  if (isNearBottom) {
    if (hasMore.value && !loadingMore.value && !loading.value) {
      console.log('✅ 触发加载更多')
      loadPlans(false)
    } else {
      console.log('❌ 不满足加载条件:', {
        hasMore: hasMore.value,
        loadingMore: loadingMore.value,
        loading: loading.value
      })
    }
  }
}

// 获取审核状态值（兼容驼峰和下划线）
const getAuditStatus = (plan: any) => {
  return plan.auditStatus !== undefined ? plan.auditStatus : plan.audit_status
}

// 获取发布状态值
const getStatus = (plan: any) => {
  return plan.status
}

// 获取审核状态文本
const getAuditStatusText = (plan: any) => {
  const auditStatus = getAuditStatus(plan)
  const status = getStatus(plan)
  
  // 草稿：audit_status = NULL
  if (auditStatus === null || auditStatus === undefined || auditStatus === '') {
    return '草稿'
  }
  
  // 转换为数字
  const auditStatusNum = Number(auditStatus)
  const statusNum = Number(status)
  
  // 待审核：audit_status = 0
  if (auditStatusNum === 0) {
    return '待审核'
  }
  
  // 审核通过：audit_status = 1
  if (auditStatusNum === 1) {
    // 已发布：status = 1
    if (statusNum === 1) {
      return '已发布'
    }
    // 已下架：status = 0 或 2
    if (statusNum === 0 || statusNum === 2) {
      return '已下架'
    }
  }
  
  // 审核拒绝：audit_status = 2
  if (auditStatusNum === 2) {
    return '已驳回'
  }
  
  return '未知'
}

// 获取审核状态样式类
const getAuditStatusClass = (plan: any) => {
  const auditStatus = getAuditStatus(plan)
  const status = getStatus(plan)
  
  // 草稿
  if (auditStatus === null || auditStatus === undefined || auditStatus === '') {
    return 'status-draft'
  }
  
  const auditStatusNum = Number(auditStatus)
  const statusNum = Number(status)
  
  // 待审核
  if (auditStatusNum === 0) {
    return 'status-pending'
  }
  
  // 审核通过
  if (auditStatusNum === 1) {
    if (statusNum === 1) {
      return 'status-published'
    }
    if (statusNum === 0 || statusNum === 2) {
      return 'status-unpublished'
    }
  }
  
  // 审核拒绝
  if (auditStatusNum === 2) {
    return 'status-rejected'
  }
  
  return 'status-draft'
}

// 自动筛选 - 使用防抖
let searchDebounceTimer: number | null = null
watch(
  () => [searchForm.keyword, searchForm.destination, searchForm.auditStatus],
  () => {
    // 清除之前的定时器
    if (searchDebounceTimer) {
      clearTimeout(searchDebounceTimer)
    }
    
    // 设置新的定时器
    searchDebounceTimer = setTimeout(() => {
      currentPage.value = 1
      planList.value = []
      hasMore.value = true
      loadPlans(true)
    }, 500) // 500ms 防抖
  },
  { deep: true }
)

// 组件挂载时添加滚动监听
onMounted(() => {
  loadPlans(true)
  loadStats()
  
  // 延迟添加滚动监听，确保 DOM 已渲染
  setTimeout(() => {
    const scrollContainer = getScrollContainer()
    if (scrollContainer === window) {
      window.addEventListener('scroll', handleScroll, { passive: true })
    } else {
      (scrollContainer as HTMLElement).addEventListener('scroll', handleScroll, { passive: true })
    }
  }, 100)
})

// 组件卸载时移除滚动监听
onUnmounted(() => {
  const scrollContainer = getScrollContainer()
  if (scrollContainer === window) {
    window.removeEventListener('scroll', handleScroll)
  } else {
    (scrollContainer as HTMLElement).removeEventListener('scroll', handleScroll)
  }
})
</script>

<style lang="scss" scoped>
.user-plans-new {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;

  // 页面头部
  .page-header {
    margin-bottom: 24px;
    padding: 32px;
    background: white;
    border-radius: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-content {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .header-icon {
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #606266;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #e4e7ed;
      }
      
      .header-text {
        h2 {
          margin: 0 0 8px 0;
          font-size: 32px;
          font-weight: 700;
          color: #303133;
        }
        
        .page-desc {
          margin: 0;
          color: #909399;
          font-size: 16px;
          line-height: 1.6;
        }
      }
    }
    
    .header-right {
      :deep(.el-button) {
        padding: 12px 24px;
        border-radius: 12px;
        background: white;
        color: #606266;
        border: 1px solid #e4e7ed;
        font-weight: 600;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        
        &:hover {
          transform: none;
          color: #606266;
          border-color: #e4e7ed;
          background-color: white;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }
      }
    }
  }

  // 统计卡片
  .stats-row {
    margin-bottom: 20px;

    .stats-card {
      border-radius: 12px !important;
      border: 1px solid #ebeef5 !important;
      background: white !important;
      background-color: white !important;
      background-image: none !important;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06) !important;
      transition: background-color 0.3s, border-color 0.3s, box-shadow 0.3s !important;
      transform: none !important;
      position: relative;
      overflow: visible;
      cursor: default !important;
      height: 96px !important;
      box-sizing: border-box !important;
      display: flex !important;
      align-items: center !important;

      // 移除全局样式中定义的紫色渐变顶部边框
      &::before {
        display: none !important;
        content: none !important;
        height: 0 !important;
        width: 0 !important;
        background: none !important;
        background-color: transparent !important;
        background-image: none !important;
        position: static !important;
        top: auto !important;
        left: auto !important;
        right: auto !important;
        z-index: -1 !important;
        animation: none !important;
        background-size: auto !important;
      }

      &:hover {
        background: #fafafa !important;
        background-color: #fafafa !important;
        background-image: none !important;
        border-color: #dcdfe6 !important;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
        transform: none !important;
        scale: 1 !important;
        
        .stats-icon {
          transform: none !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          
          :deep(.el-icon) {
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            text-shadow: none !important;
            opacity: 1 !important;
          }
        }
      }

      :deep(.el-card__header) {
        display: none !important;
        padding: 0 !important;
        height: 0 !important;
        margin: 0 !important;
        border-bottom: none !important;
      }

      :deep(.el-card__body) {
        padding: 20px !important;
        background: transparent !important;
        background-color: transparent !important;
        transition: background-color 0.3s !important;
        height: 100% !important;
        box-sizing: border-box !important;
        display: flex !important;
        align-items: center !important;
      }

      &:hover :deep(.el-card__body) {
        background-color: transparent !important;
      }

      .stats-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stats-icon {
          width: auto;
          height: auto;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          transition: none !important;
          transform: none !important;
          pointer-events: none !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          background: transparent !important;
          border-radius: 0 !important;
          
          :deep(.el-icon) {
            transition: none !important;
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            text-shadow: none !important;
            opacity: 1 !important;
          }
          
          &:hover {
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            opacity: 1 !important;
            
            :deep(.el-icon) {
              transform: none !important;
              box-shadow: none !important;
              filter: none !important;
              text-shadow: none !important;
              opacity: 1 !important;
            }
          }
        }

        .stats-info {
          flex: 1;

          .stats-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
            transition: all 0.3s;
          }

          .stats-label {
            font-size: 14px;
            color: #909399;
            margin-bottom: 2px;
          }
        }
      }
    }
  }

  // 筛选卡片
  .filter-card {
    margin-bottom: 20px;
    border-radius: 16px;
    border: none;
    background: white;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    :deep(.el-card__body) {
      padding: 24px;
    }
    
    // 全局覆盖选择框的focus样式
    :deep(.el-select .el-input__wrapper.is-focus),
    :deep(.el-select.is-focus .el-input__wrapper),
    :deep(.el-select .el-input.is-focus .el-input__wrapper) {
      border-color: #303133 !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
    }
    
    .filter-form {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
      
      .el-form-item {
        margin-bottom: 0;
      }
      
      :deep(.el-input) {
        .el-input__wrapper {
          border-radius: 10px;
          box-shadow: 0 0 0 1px #e4e7ed inset;
          transition: all 0.3s;
          
          &:hover {
            box-shadow: 0 0 0 1px #c0c4cc inset;
          }
          
          &.is-focus {
            box-shadow: 0 0 0 1px #909399 inset !important;
            border-color: #909399 !important;
          }
        }
        
      }
      
      :deep(.el-select) {
        // 禁用所有默认的 focus 效果
        .el-select__wrapper,
        .el-input__wrapper {
          border-radius: 10px;
          box-shadow: 0 0 0 1px #e4e7ed inset;
          transition: border-color 0.3s, box-shadow 0.3s;
          border: none !important;
          
          &:hover {
            box-shadow: 0 0 0 1px #c0c4cc inset;
          }
          
          // 禁用所有默认的 focus 样式
          &:focus,
          &:focus-visible,
          &.is-focus {
            outline: none !important;
            box-shadow: 0 0 0 1px #303133 inset !important;
            border: none !important;
          }
        }
        
        // 当 el-select 获得焦点时
        &.is-focus {
          .el-select__wrapper,
          .el-input__wrapper {
            box-shadow: 0 0 0 1px #303133 inset !important;
            border: none !important;
            outline: none !important;
          }
        }
      }
    }
    
    .status-option {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .el-icon {
        font-size: 14px;
      }
    }
  }

  // 攻略网格
  .plans-grid {
    margin-bottom: 20px;

    .plan-card {
      cursor: pointer;
      border-radius: 12px;
      border: 1px solid #ebeef5;
      background: white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: transform 0.3s, box-shadow 0.3s;
      margin-bottom: 20px;
      overflow: hidden;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      :deep(.el-card__body) {
        padding: 0;
      }

      .plan-cover {
        position: relative;
        height: 200px;
        overflow: hidden;
        background: #f5f7fa;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .plan-status-badge {
          position: absolute !important;
          top: 12px !important;
          right: 12px !important;
          padding: 6px 12px !important;
          border-radius: 4px !important;
          font-size: 12px !important;
          font-weight: 500 !important;
          background: white !important;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
          z-index: 10 !important;
          display: inline-block !important;
          min-width: 60px !important;
          text-align: center !important;

          &.status-draft {
            color: #909399 !important;
            background: #F4F4F5 !important;
          }

          &.status-published {
            color: #67C23A !important;
            background: #F0F9FF !important;
          }

          &.status-pending {
            color: #E6A23C !important;
            background: #FDF6EC !important;
          }
          
          &.status-rejected {
            color: #F56C6C !important;
            background: #FEF0F0 !important;
          }
          
          &.status-unpublished {
            color: #FFFFFF !important;
            background: #909399 !important;
            border: 2px solid #606266 !important;
          }
        }
      }
      
      .reject-hint {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 12px;
        margin: 8px 0;
        background: #FEF0F0;
        border: 1px solid #FBC4C4;
        border-radius: 4px;
        color: #F56C6C;
        font-size: 13px;
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          background: #FDE2E2;
          border-color: #F56C6C;
        }
      }


      .plan-content {
        padding: 16px;

        .plan-title {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .plan-destination {
          margin: 0 0 8px 0;
          font-size: 14px;
          color: #409eff;
          display: flex;
          align-items: center;
          gap: 4px;
          
          .el-icon {
            font-size: 14px;
          }
        }

        .plan-desc {
          margin: 0 0 12px 0;
          font-size: 13px;
          color: #606266;
          line-height: 1.6;
          height: 41px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .plan-meta {
          display: flex;
          gap: 12px;
          margin-bottom: 12px;
          padding-bottom: 12px;
          border-bottom: 1px solid #ebeef5;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: #909399;
            
            .el-icon {
              font-size: 14px;
            }
          }
        }

        .plan-actions {
          display: flex;
          gap: 8px;
          align-items: center;
          justify-content: flex-end;
          
          // 状态流转按钮：统一的阳光暖色系样式，简单但不压抑
          .action-btn {
            border-radius: 8px;          // 更接近“正方形”的大块按钮
            padding: 10px 22px;
            font-weight: 500;
            font-size: 13px;
            border: none;
            background: #FFF7E6; // 柔和暖黄色
            color: #8A6116;
            box-shadow: none !important;
            transform: none !important;
            display: inline-flex;
            align-items: center;
            gap: 4px;
            min-width: 92px;
            min-height: 36px;
            justify-content: center;
            
            .el-icon {
              font-size: 14px;
            }
            
            &:hover {
              background: #FFE8BF;
              color: #70480F;
            }
            
            // 覆盖 Element Plus 默认 type 颜色，统一风格
            &.el-button--primary {
              color: #8A6116;
              
              // 编辑按钮：悬停时不要出现额外背景变化，保持静止
              &:hover {
                background: #FFF7E6;
                color: #8A6116;
              }
            }
            
            &.el-button--success {
              color: #3F7F32;
              background: #F1FCEB;
            }
            
            &.el-button--warning {
              color: #B96B0D;
              background: #FFF3D6;
            }
            
            &.el-button--danger {
              color: #F56C6C;
              background: #FFECEC;
            }
          }
          
          .status-info {
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 8px 16px;
            border-radius: 4px;
            font-size: 14px;
            font-weight: 500;
            margin-right: auto;
            
            &.success {
              color: #67C23A;
              background: #f0f9ff;
              border: 1px solid #b3e19d;
            }
            
            &.unpublished {
              color: #909399;
              background: #F4F4F5;
              border: 1px solid #DCDFE6;
            }
            
            .el-icon {
              font-size: 16px;
            }
          }
        }
      }
    }
    
    // 空状态
    :deep(.el-empty) {
      padding: 60px 20px;
      
      .el-empty__description p {
        color: #909399;
        font-size: 14px;
      }
    }
  }

  // 加载更多提示
  .load-more-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 30px;
    padding: 20px 0;
    min-height: 60px;

    .loading-more {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #409eff;
      font-size: 14px;

      .loading-icon {
        font-size: 18px;
        animation: rotate 1s linear infinite;
      }
    }

    .load-more-tip {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #909399;
      font-size: 14px;

      .el-icon {
        font-size: 16px;
        animation: bounce 2s ease-in-out infinite;
      }
    }

    .no-more {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #c0c4cc;
      font-size: 13px;

      .el-icon {
        font-size: 16px;
        color: #67c23a;
      }
    }
  }

  // 旋转动画
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  // 上下跳动动画
  @keyframes bounce {
    0%, 100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-4px);
    }
  }

  // 旧的分页样式（已移除，保留以防需要）
  .pagination-wrapper {
    display: none; // 隐藏分页组件
    
    .pagination-info {
      display: flex;
      align-items: center;
      gap: 16px;
      color: #909399;
      font-size: 14px;
      
      .total-text {
        font-weight: 500;
      }
      
      .page-info {
        padding: 4px 12px;
        background: #f5f7fa;
        border-radius: 12px;
        color: #606266;
      }
    }
    
    .pagination-controls {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
      justify-content: center;
      
      .page-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 10px 20px;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
        background: white;
        color: #606266;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.3s;
        height: 40px;
        
        &:hover:not(:disabled) {
          border-color: #303133;
          color: #303133;
          background: #fafafa;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        &:disabled {
          opacity: 0.4;
          cursor: not-allowed;
        }
        
        .el-icon {
          font-size: 16px;
        }
      }
      
      .page-numbers {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .page-number {
          min-width: 40px;
          height: 40px;
          padding: 0 12px;
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          background: white;
          color: #606266;
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.3s;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &:hover:not(.active):not(.ellipsis) {
            border-color: #303133;
            color: #303133;
            background: #fafafa;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }
          
          &.active {
            background: white;
            border: 2px solid #303133;
            color: #303133;
            font-weight: 600;
            box-shadow: 0 2px 8px rgba(48, 49, 51, 0.1);
          }
          
          &.ellipsis {
            border: none;
            background: transparent;
            cursor: default;
            color: #909399;
            min-width: auto;
            padding: 0 4px;
            
            &:hover {
              transform: none;
              box-shadow: none;
            }
          }
          
          &:disabled {
            cursor: default;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .user-plans-new {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      padding: 24px;
      
      .header-content {
        .header-icon {
          width: 64px;
          height: 64px;
          
          .el-icon {
            font-size: 36px;
          }
        }
        
        .header-text h2 {
          font-size: 28px;
        }
      }
      
      .header-right {
        width: 100%;
        
        :deep(.el-button) {
          width: 100%;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .user-plans-new {
    padding: 16px;
    
    .page-header {
      padding: 20px;
      
      .header-content {
        gap: 12px;
        
        .header-icon {
          width: 56px;
          height: 56px;
          
          .el-icon {
            font-size: 32px;
          }
        }
        
        .header-text {
          h2 {
            font-size: 24px;
          }
          
          .page-desc {
            font-size: 13px;
          }
        }
      }
    }
    
    .filter-card {
      :deep(.el-card__body) {
        padding: 16px;
      }
      
      .filter-form {
        .el-form-item {
          width: 100%;
          
          .el-input,
          .el-select {
            width: 100% !important;
          }
        }
      }
    }
  }
}
</style>

<style lang="scss">
// 审核拒绝原因对话框样式
.reject-reason-dialog {
  border-radius: 12px;
  overflow: hidden;
  
  .el-message-box__header {
    padding: 20px 20px 15px;
    background: linear-gradient(135deg, #FEF0F0 0%, #FFF 100%);
    border-bottom: none;
    
    .el-message-box__title {
      color: #303133;
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .el-message-box__content {
    padding: 0 20px 20px;
  }
  
  .el-message-box__btns {
    padding: 15px 20px 20px;
    
    .el-button--primary {
      background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
      border: none;
      padding: 10px 30px;
      border-radius: 6px;
      font-weight: 500;
      
      &:hover {
        // 去掉“知道了”按钮的悬停动效，保持静止
        background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
        transform: none;
        box-shadow: none;
      }
    }
  }
}

// 审核状态下拉框样式
.audit-status-select {
  .el-select-dropdown__item {
    height: auto !important;
    padding: 0 !important;
    line-height: normal !important;
    
    &:hover {
      background: transparent !important;
    }
    
    .status-option-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      transition: all 0.3s;
      border-radius: 6px;
      margin: 4px 8px;
      
      .status-text {
        flex: 1;
        width: 100%;
        
        .status-label {
          font-size: 14px;
          font-weight: 600;
          margin-bottom: 2px;
          color: #303133;
        }
        
        .status-desc {
          font-size: 12px;
          color: #909399;
        }
      }
      
      &:hover {
        background-color: #f5f7fa;
      }
      
      // 全部状态
      &.all-status {
        .status-label {
          color: #303133;
        }
        
        &:hover {
          background-color: #f5f7fa;
        }
      }
      
      // 待审核
      &.pending-status {
        .status-label {
          color: #303133;
        }
        
        &:hover {
          background-color: #f5f7fa;
        }
      }
      
      // 审核通过
      &.approved-status {
        .status-label {
          color: #303133;
        }
        
        &:hover {
          background-color: #f5f7fa;
        }
      }
      
      // 审核拒绝
      &.rejected-status {
        .status-label {
          color: #303133;
        }
        
        &:hover {
          background-color: #f5f7fa;
        }
      }
    }
  }
  
  .el-select-dropdown__item.selected {
    .status-option-item {
      background-color: #f5f5f5 !important;
      
      &::after {
        content: '✓';
        position: absolute;
        right: 16px;
        font-size: 18px;
        font-weight: bold;
        color: #606266;
      }
    }
  }
}
</style>

<style lang="scss">
// 翻页组件下拉菜单样式 - 移除蓝色（因为下拉菜单挂载在body上，需要非scoped样式）
.el-pagination .el-select-dropdown__item,
.el-select-dropdown.el-popper[data-popper-placement] .el-select-dropdown__item {
  color: #606266 !important;
  
  &:hover {
    background-color: #f5f7fa !important;
    color: #303133 !important;
  }
  
  &.selected {
    color: #303133 !important;
    background-color: #f5f5f5 !important;
    font-weight: 600;
  }
}

// 完全禁用 Element Plus 默认的蓝色 focus 样式，重新写为黑色
.user-plans-new .filter-card .el-select {
  // 禁用所有默认的 focus 效果
  .el-select__wrapper,
  .el-input__wrapper {
    &:focus,
    &:focus-visible,
    &.is-focus {
      --el-input-focus-border-color: #303133 !important;
      --el-border-color: #303133 !important;
      border: none !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
      outline: none !important;
    }
  }
  
  // 当 el-select 获得焦点时
  &.is-focus {
    .el-select__wrapper,
    .el-input__wrapper {
      --el-input-focus-border-color: #303133 !important;
      --el-border-color: #303133 !important;
      border: none !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
      outline: none !important;
    }
  }
}
</style>



