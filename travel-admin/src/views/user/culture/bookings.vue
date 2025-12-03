<template>
  <div class="bookings-page">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Calendar /></el-icon>
        我的预订
      </h1>
    </el-card>

    <!-- 预订筛选 -->
    <el-card class="filter-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部预订" name="all" />
        <el-tab-pane label="待确认" name="pending">
          <template #label>
            待确认
            <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane label="已确认" name="confirmed" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>
    </el-card>

    <!-- 预订列表 -->
    <div v-if="filteredBookings.length === 0" class="empty-bookings">
      <el-empty description="暂无预订记录">
        <el-button type="primary" @click="goBrowse">去浏览</el-button>
      </el-empty>
    </div>

    <div v-else class="bookings-list">
      <el-card v-for="booking in filteredBookings" :key="booking.id" class="booking-card">
        <!-- 预订头部 -->
        <div class="booking-header">
          <div class="header-left">
            <span class="booking-time">{{ formatDateTime(booking.createTime) }}</span>
            <span class="booking-no">预订编号：{{ booking.bookingNo }}</span>
          </div>
          <el-tag :type="getStatusType(booking.status)" size="large">
            {{ getStatusText(booking.status) }}
          </el-tag>
        </div>

        <el-divider />

        <!-- 预订内容 -->
        <div class="booking-content" @click="goToDetail(booking.projectId, booking.type)">
          <el-image :src="booking.image" fit="cover" class="booking-image" />
          <div class="booking-info">
            <h3 class="project-name">{{ booking.projectName }}</h3>
            <div class="booking-details">
              <div class="detail-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ booking.date }}</span>
              </div>
              <div v-if="booking.timeSlot" class="detail-item">
                <el-icon><Clock /></el-icon>
                <span>{{ booking.timeSlot }}</span>
              </div>
              <div class="detail-item">
                <el-icon><User /></el-icon>
                <span>{{ booking.participants }}人</span>
              </div>
            </div>
          </div>
          <div class="booking-price">
            <div class="price-label">总计</div>
            <div class="price-value">¥{{ booking.totalAmount.toFixed(2) }}</div>
          </div>
        </div>

        <el-divider />

        <!-- 联系信息 -->
        <div class="contact-info">
          <div class="info-row">
            <span class="label">联系人：</span>
            <span>{{ booking.contactName }} {{ booking.contactPhone }}</span>
          </div>
          <div v-if="booking.notes" class="info-row">
            <span class="label">备注：</span>
            <span>{{ booking.notes }}</span>
          </div>
        </div>

        <!-- 预订操作 -->
        <div class="booking-actions">
          <el-button v-if="booking.status === 'pending'" @click="handleCancel(booking)">
            取消预订
          </el-button>
          <el-button v-if="booking.status === 'confirmed'" type="primary" @click="handleComment(booking)">
            评价
          </el-button>
          <el-button @click="handleContact(booking)">
            联系客服
          </el-button>
          <el-button v-if="booking.status === 'completed'" type="danger" link @click="handleDelete(booking)">
            删除记录
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div v-if="filteredBookings.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import { Calendar, Clock, User } from '@element-plus/icons-vue'

const router = useRouter()

// 当前标签页
const activeTab = ref('all')

// 预订列表（模拟数据）
const bookings = ref([
  {
    id: 1,
    bookingNo: 'BK202410290001',
    createTime: '2024-10-29 14:30:25',
    status: 'pending',
    type: 'experience',
    projectId: 3,
    projectName: '蜀绣体验工坊',
    image: 'https://picsum.photos/120/120?random=30',
    date: '2024-11-05',
    timeSlot: '上午场 (9:00-12:00)',
    participants: 4,
    contactName: '张三',
    contactPhone: '138****8888',
    totalAmount: 800,
    notes: '希望能有专业老师指导'
  },
  {
    id: 2,
    bookingNo: 'BK202410280002',
    createTime: '2024-10-28 10:15:30',
    status: 'confirmed',
    type: 'service',
    projectId: 1,
    projectName: '云雾山庄农家乐',
    image: 'https://picsum.photos/120/120?random=31',
    date: '2024-11-10',
    timeSlot: null,
    participants: 6,
    contactName: '李四',
    contactPhone: '139****9999',
    totalAmount: 1200,
    notes: ''
  },
  {
    id: 3,
    bookingNo: 'BK202410270003',
    createTime: '2024-10-27 16:45:12',
    status: 'completed',
    type: 'experience',
    projectId: 3,
    projectName: '传统竹编工艺',
    image: 'https://picsum.photos/120/120?random=32',
    date: '2024-10-28',
    timeSlot: '全天 (9:00-17:00)',
    participants: 2,
    contactName: '张三',
    contactPhone: '138****8888',
    totalAmount: 400,
    notes: ''
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredBookings.value.length)

// 待确认数量
const pendingCount = computed(() => bookings.value.filter(b => b.status === 'pending').length)

// 过滤后的预订
const filteredBookings = computed(() => {
  if (activeTab.value === 'all') {
    return bookings.value
  }
  return bookings.value.filter(booking => booking.status === activeTab.value)
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    confirmed: 'success',
    completed: 'info',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

// 标签页切换
const handleTabChange = (tab) => {
  console.log('切换到标签:', tab)
  currentPage.value = 1
}

// 跳转到详情
const goToDetail = (projectId, type) => {
  if (type === 'experience') {
    router.push(`/home/user/culture/detail/${projectId}`)
  } else if (type === 'service') {
    router.push(`/home/user/culture/service/${projectId}`)
  }
}

// 取消预订
const handleCancel = async (booking) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预订吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // TODO: 调用取消预订接口
    console.log('取消预订:', booking.id)
    
    ElMessage.success('预订已取消')
    booking.status = 'cancelled'
  } catch {
    // 用户取消
  }
}

// 评价
const handleComment = (booking) => {
  ElMessage.info('评价功能开发中...')
}

// 联系客服
const handleContact = (booking) => {
  ElMessage.info('客服电话：13977531577')
}

// 删除记录
const handleDelete = async (booking) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？删除后无法恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const index = bookings.value.findIndex(b => b.id === booking.id)
    if (index > -1) {
      bookings.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 去浏览
const goBrowse = () => {
  router.push('/home/user/culture')
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
}
</script>

<style scoped lang="scss">
.bookings-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;

  h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    margin: 0;

    .el-icon {
      font-size: 32px;
      color: #409eff;
    }
  }
}

.filter-card {
  margin-bottom: 24px;

  :deep(.el-tabs__item) {
    font-size: 16px;
  }

  .tab-badge {
    margin-left: 8px;
  }
}

.empty-bookings {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.booking-card {
  .booking-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      gap: 24px;
      align-items: center;

      .booking-time {
        font-size: 14px;
        color: #606266;
      }

      .booking-no {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .booking-content {
    display: flex;
    gap: 20px;
    cursor: pointer;
    padding: 16px;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    .booking-image {
      width: 120px;
      height: 120px;
      border-radius: 8px;
      flex-shrink: 0;
    }

    .booking-info {
      flex: 1;

      .project-name {
        font-size: 18px;
        font-weight: 700;
        color: #303133;
        margin: 0 0 16px 0;
      }

      .booking-details {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .detail-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: #606266;

          .el-icon {
            color: #909399;
          }
        }
      }
    }

    .booking-price {
      text-align: right;

      .price-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .price-value {
        font-size: 24px;
        font-weight: 700;
        color: #f56c6c;
      }
    }
  }

  .contact-info {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .info-row {
      display: flex;
      font-size: 14px;
      color: #606266;

      .label {
        min-width: 70px;
        color: #909399;
      }
    }
  }

  .booking-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>



