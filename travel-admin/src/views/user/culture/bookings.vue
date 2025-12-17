<template>
  <div class="bookings-page">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Calendar /></el-icon>
        {{ pageTitle }}
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
            <!-- 以预定时间为准：预定日期 + 时间段（晚数） -->
            <span class="booking-time">
              {{ booking.date }}
              <span v-if="booking.timeSlot" class="booking-time-slot">{{ booking.timeSlot }}</span>
            </span>
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
            <div class="price-status" v-if="booking.paymentStatusText">
              <el-tag :type="booking.paymentStatusTagType" size="small">
                {{ booking.paymentStatusText }}
              </el-tag>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 联系信息 -->
        <div class="contact-info">
          <div class="info-row">
            <span class="label">联系人：</span>
            <span>{{ booking.contactName }} {{ booking.contactPhone }}</span>
          </div>
          <div class="info-row">
            <span class="label">特殊需求：</span>
            <span>{{ booking.specialRequirements || '无' }}</span>
          </div>
          <div v-if="booking.status === 'cancelled' && booking.cancelReason" class="info-row cancel-row">
            <span class="label">取消原因：</span>
            <span class="cancel-text">{{ booking.cancelReason }}</span>
          </div>
        </div>

        <!-- 预订操作 -->
        <div class="booking-actions">
          <el-button
            v-if="booking.paymentStatus === 1 && booking.status === 'confirmed'"
            type="danger"
            @click="handlePay(booking)"
          >
            去支付
          </el-button>
          <el-button v-if="booking.status === 'pending'" @click="handleCancel(booking)">
            取消预订
          </el-button>
          <el-button
            v-if="booking.status === 'completed' && booking.paymentStatus === 2"
            type="primary"
            @click="handleComment(booking)"
          >
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import { Calendar, Clock, User } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

// 页面模式：experience（文化体验预约）/ stay（农家乐&民宿预定）/ all（全部）
const mode = computed(() => {
  const m = route.query.mode
  if (m === 'experience' || m === 'stay') return m
  return 'all'
})

const pageTitle = computed(() => {
  if (mode.value === 'experience') return '我的预约'
  if (mode.value === 'stay') return '我的预定'
  return '我的预订'
})

// 当前标签页
const activeTab = ref('all')

// 预订列表（接口数据）
const bookings = ref([])

// 将后端 status 数字映射为前端状态字符串
const mapStatus = (status) => {
  switch (status) {
    case 1:
      return 'pending'
    case 2:
      return 'confirmed'
    case 3:
      return 'completed'
    case 4:
      return 'cancelled'
    default:
      return 'pending'
  }
}

// 将 bookingType 映射为前端类型
const mapType = (bookingType) => {
  switch (bookingType) {
    case 1:
      return 'experience'
    case 2:
      return 'service' // 农家乐
    case 3:
      return 'homestay' // 民宿
    default:
      return 'service'
  }
}

// 解析图片字段（可能是 JSON 数组 / 字符串）
const safeParseImages = (val) => {
  if (!val) return []
  if (Array.isArray(val)) return val
  if (typeof val === 'string') {
    try {
      const parsed = JSON.parse(val)
      if (Array.isArray(parsed)) return parsed
      return [val]
    } catch {
      return [val]
    }
  }
  return []
}

// 读取本地模拟“已支付”的预订 ID 列表（仅开发联调阶段使用）
const MOCK_PAID_KEY = 'mock_paid_bookings'
const getMockPaidIds = () => {
  try {
    const raw = localStorage.getItem(MOCK_PAID_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

// 加载预订列表
const loadBookings = async () => {
  try {
    const res = await request.get('/culture/booking/my')
    if (res.code === 200 && Array.isArray(res.data)) {
      const mockPaidIds = getMockPaidIds()
      bookings.value = res.data.map((b) => {
        const images = safeParseImages(b.itemImage)

        // 处理后端返回字符串 'null' 的情况，避免在页面展示出来
        const normalizedTimeSlot =
          b.bookingTime && b.bookingTime !== 'null' ? b.bookingTime : ''
        const normalizedSpecialRequirements =
          b.specialRequirements && b.specialRequirements !== 'null'
            ? b.specialRequirements
            : ''

        const isMockPaid = mockPaidIds.includes(b.id)

        return {
          id: b.id,
          bookingNo: b.bookingNo,
          createTime: b.createTime,
          status: mapStatus(b.status),
          type: mapType(b.bookingType),
          projectId: b.itemId,
          projectName: b.itemName,
          image: images[0] || 'https://picsum.photos/120/120?random=20',
          date: b.bookingDate,
          timeSlot: normalizedTimeSlot,
          participants: b.peopleCount,
          contactName: b.contactName,
          contactPhone: b.contactPhone,
          totalAmount: Number(b.totalAmount || 0),
          paymentStatus: isMockPaid ? 2 : (b.paymentStatus ?? 1),
          paymentStatusText: isMockPaid || b.paymentStatus === 2 ? '已支付' : '未支付',
          paymentStatusTagType: isMockPaid || b.paymentStatus === 2 ? 'success' : 'warning',
          cancelReason: b.cancelReason,
          notes: normalizedSpecialRequirements,
          specialRequirements: normalizedSpecialRequirements
        }
      })
    } else {
      bookings.value = []
    }
  } catch (error) {
    console.error('加载预订列表失败:', error)
    ElMessage.error('加载预订列表失败')
    bookings.value = []
  }
}

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredBookings.value.length)

// 待确认数量
const pendingCount = computed(() => bookings.value.filter(b => b.status === 'pending').length)

// 过滤后的预订
const filteredBookings = computed(() => {
  // 先按模块类型过滤
  let list = bookings.value
  if (mode.value === 'experience') {
    list = list.filter(b => b.type === 'experience')
  } else if (mode.value === 'stay') {
    // 农家乐(service) + 民宿(homestay)
    list = list.filter(b => b.type === 'service' || b.type === 'homestay')
  }

  // 再按状态过滤
  if (activeTab.value === 'all') {
    return list
  }
  return list.filter(booking => booking.status === activeTab.value)
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
  currentPage.value = 1
}

// 跳转到详情
const goToDetail = (projectId, type) => {
  if (type === 'experience') {
    router.push(`/home/user/culture/detail/${projectId}`)
  } else if (type === 'service') {
    router.push(`/home/user/culture/service/${projectId}`)
  } else if (type === 'homestay') {
    router.push(`/home/user/culture/homestay/${projectId}`)
  }
}

// 去支付
const handlePay = (booking) => {
  router.push(`/home/user/culture/booking-pay/${booking.id}`)
}

// 取消预订
const handleCancel = async (booking) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预订吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 调用取消预订接口
    await request.put(`/culture/booking/${booking.id}/cancel`, {
      reason: '用户主动取消'
    })

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

onMounted(() => {
  loadBookings()
})

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

          .booking-time-slot {
            margin-left: 12px; // 日期与“1晚”之间留出明显间隔
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
        gap: 4px; // 日期和「1晚」之间间距稍微紧凑一点

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
        min-width: 56px; // 标签和内容靠近一些
        color: #909399;
      }

      &.cancel-row {
        border-left: 3px solid #f56c6c;
        padding-left: 8px;
        color: #f56c6c;

        .label {
          color: #f56c6c;
          font-weight: 600;
        }

        .cancel-text {
          font-weight: 500;
        }
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



