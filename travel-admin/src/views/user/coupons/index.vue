<template>
  <div class="coupons-page">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <el-card class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><Ticket /></el-icon>
        </div>
        <div class="header-text">
          <h2>我的优惠券</h2>
          <p class="page-desc">查看和管理您的优惠券</p>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon available">
            <el-icon :size="32"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.available }}</div>
            <div class="stat-label">可用优惠券</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon used">
            <el-icon :size="32"><DocumentChecked /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.used }}</div>
            <div class="stat-label">已使用</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon expired">
            <el-icon :size="32"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.expired }}</div>
            <div class="stat-label">已过期</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 筛选标签 -->
    <el-card class="filter-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane name="available">
          <template #label>
            <span style="display: inline-flex; align-items: center; gap: 8px;">
              可用
              <el-badge v-if="stats.available > 0" :value="stats.available" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已使用" name="used" />
        <el-tab-pane label="已过期" name="expired" />
      </el-tabs>
    </el-card>

    <!-- 优惠券列表 -->
    <div v-if="filteredCoupons.length === 0" class="empty-coupons">
      <el-empty description="暂无优惠券">
        <el-button type="primary" @click="goToLevelGuide">查看升级指南</el-button>
      </el-empty>
    </div>

    <div v-else class="coupons-list">
      <el-card
        v-for="coupon in filteredCoupons"
        :key="coupon.id"
        class="coupon-card"
        :class="getCouponCardClass(coupon)"
      >
        <div class="coupon-content">
          <div class="coupon-left">
            <div class="coupon-amount">
              <span class="symbol">¥</span>
              <span class="value">{{ coupon.discountValue }}</span>
            </div>
            <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
            <div v-if="coupon.sourceDesc" class="coupon-source">
              <el-icon><Medal /></el-icon>
              {{ coupon.sourceDesc }}
            </div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.couponName }}</div>
            <div class="coupon-desc">{{ getCouponTypeText(coupon.couponType) }}</div>
            <div class="coupon-expire">
              <el-icon><Clock /></el-icon>
              有效期至：{{ formatDate(coupon.validEndTime) }}
            </div>
            <div v-if="coupon.usedTime" class="coupon-used">
              <el-icon><DocumentChecked /></el-icon>
              使用时间：{{ formatDate(coupon.usedTime) }}
            </div>
          </div>
          <div class="coupon-status">
            <el-tag :type="getStatusTagType(coupon.status)" size="large">
              {{ getStatusText(coupon.status) }}
            </el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div v-if="pagination.total > 0" class="pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { getCurrentUserId } from '@/utils/user'
import {
  Ticket,
  CircleCheck,
  DocumentChecked,
  Clock,
  Medal
} from '@element-plus/icons-vue'

const router = useRouter()

// 当前标签
const activeTab = ref('all')

// 统计数据
const stats = reactive({
  available: 0,
  used: 0,
  expired: 0
})

// 优惠券列表
const coupons = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loading = ref(false)

// 筛选后的优惠券列表
const filteredCoupons = computed(() => {
  let list = coupons.value
  
  if (activeTab.value === 'available') {
    list = list.filter(c => c.status === 1 && new Date(c.validEndTime) > new Date())
  } else if (activeTab.value === 'used') {
    list = list.filter(c => c.status === 2)
  } else if (activeTab.value === 'expired') {
    list = list.filter(c => c.status === 3 || new Date(c.validEndTime) < new Date())
  }
  
  return list
})

// 加载优惠券列表
const loadCoupons = async () => {
  loading.value = true
  try {
    const userId = getCurrentUserId()
    
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    const response = await request.get('/user/coupon/list', {
      params: {
        userId,
        page: pagination.page,
        limit: pagination.size
      }
    })
    
    if (response.code === 200 && response.data) {
      // 后端返回的是 Page<UserCoupon>，包含 records 和 total
      coupons.value = response.data.records || response.data.list || []
      pagination.total = response.data.total || 0
      
      // 更新统计
      updateStats()
    } else {
      ElMessage.error(response.message || '加载优惠券列表失败')
    }
  } catch (error) {
    console.error('加载优惠券列表失败:', error)
    const errorMessage = error instanceof Error ? error.message : '未知错误'
    ElMessage.error('加载优惠券列表失败: ' + errorMessage)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadCouponStats = async () => {
  try {
    const userId = getCurrentUserId()
    if (!userId) return
    
    const response = await request.get('/user/coupon/stats', {
      params: { userId }
    })
    
    if (response.code === 200 && response.data) {
      stats.available = response.data.available || 0
      stats.used = response.data.used || 0
      stats.expired = response.data.expired || 0
    }
  } catch (error) {
    console.error('加载优惠券统计失败:', error)
  }
}

// 更新统计数据（从本地列表计算）
const updateStats = () => {
  const now = new Date()
  stats.available = coupons.value.filter(c => 
    c.status === 1 && new Date(c.validEndTime) > now
  ).length
  stats.used = coupons.value.filter(c => c.status === 2).length
  stats.expired = coupons.value.filter(c => 
    c.status === 3 || new Date(c.validEndTime) < now
  ).length
}

// 标签切换
const handleTabChange = () => {
  pagination.page = 1
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadCoupons()
}

// 页码改变
const handlePageChange = (page) => {
  pagination.page = page
  loadCoupons()
}

// 获取优惠券卡片样式类
const getCouponCardClass = (coupon) => {
  const now = new Date()
  const endTime = new Date(coupon.validEndTime)
  
  if (coupon.status === 2) {
    return 'used'
  } else if (coupon.status === 3 || endTime < now) {
    return 'expired'
  } else {
    return 'available'
  }
}

// 获取优惠券类型文本
const getCouponTypeText = (type) => {
  const map = {
    1: '满减券',
    2: '折扣券',
    3: '兑换券'
  }
  return map[type] || '未知类型'
}

// 获取状态文本
const getStatusText = (status) => {
  const now = new Date()
  // 如果状态是1但已过期，显示已过期
  if (status === 1) {
    return '可用'
  }
  const map = {
    1: '可用',
    2: '已使用',
    3: '已过期',
    4: '已作废'
  }
  return map[status] || '未知'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const map = {
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'danger'
  }
  return map[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 跳转到升级指南
const goToLevelGuide = () => {
  router.push('/home/user/level-guide')
}

onMounted(() => {
  loadCoupons()
  loadCouponStats()
})
</script>

<style scoped lang="scss">
.coupons-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  
  .header-content {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .header-icon {
      color: #409eff;
    }
    
    .header-text {
      h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
      }
      
      .page-desc {
        margin: 8px 0 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
  
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .stat-icon {
        width: 64px;
        height: 64px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &.available {
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
          color: white;
        }
        
        &.used {
          background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
          color: white;
        }
        
        &.expired {
          background: linear-gradient(135deg, #e6a23c 0%, #f0a020 100%);
          color: white;
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: #303133;
          line-height: 1;
        }
        
        .stat-label {
          margin-top: 8px;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }
}

.filter-card {
  margin-bottom: 20px;
  
  .tab-badge {
    margin-left: 8px;
  }
}

.empty-coupons {
  padding: 60px 0;
  text-align: center;
}

.coupons-list {
  display: grid;
  gap: 20px;
  margin-bottom: 20px;
}

.coupon-card {
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  &.available {
    border-left: 4px solid #67c23a;
  }
  
  &.used {
    border-left: 4px solid #909399;
    opacity: 0.7;
  }
  
  &.expired {
    border-left: 4px solid #e6a23c;
    opacity: 0.6;
  }
  
  .coupon-content {
    display: flex;
    gap: 24px;
    align-items: center;
    
    .coupon-left {
      flex-shrink: 0;
      width: 140px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
      border-radius: 12px;
      padding: 24px 0;
      
      .coupon-amount {
        display: flex;
        align-items: flex-start;
        color: white;
        
        .symbol {
          font-size: 20px;
          margin-top: 6px;
        }
        
        .value {
          font-size: 42px;
          font-weight: 700;
          line-height: 1;
        }
      }
      
      .coupon-condition {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.9);
        margin-top: 8px;
      }
      
      .coupon-source {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: rgba(255, 255, 255, 0.8);
        margin-top: 8px;
      }
    }
    
    .coupon-right {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .coupon-name {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
      
      .coupon-desc {
        font-size: 14px;
        color: #909399;
      }
      
      .coupon-expire,
      .coupon-used {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #606266;
        margin-top: 4px;
      }
    }
    
    .coupon-status {
      flex-shrink: 0;
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .coupon-card .coupon-content {
    flex-direction: column;
    
    .coupon-left {
      width: 100%;
    }
  }
}
</style>

