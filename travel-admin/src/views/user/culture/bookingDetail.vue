<template>
  <div class="booking-detail-page">
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Document /></el-icon>
        预约详情
      </h1>
    </el-card>

    <div v-loading="loading" class="detail-container">
      <el-card v-if="booking" class="detail-card">
        <!-- 预订头部信息 -->
        <div class="booking-header">
          <div class="header-left">
            <div class="booking-no">
              <el-icon><Document /></el-icon>
              <span>预订编号：{{ booking.bookingNo }}</span>
            </div>
            <div class="booking-time">
              <el-icon><Calendar /></el-icon>
              <span>创建时间：{{ formatDateTime(booking.createTime) }}</span>
            </div>
          </div>
          <div class="header-right">
            <el-tag :type="getStatusType(booking.status)" size="large">
              {{ getStatusText(booking.status) }}
            </el-tag>
            <el-tag 
              :type="booking.paymentStatus === 2 ? 'success' : 'warning'" 
              size="large"
              style="margin-left: 12px;"
            >
              {{ booking.paymentStatus === 2 ? '已支付' : '未支付' }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <!-- 服务信息 -->
        <div class="section">
          <h3 class="section-title">
            <el-icon><Shop /></el-icon>
            服务信息
          </h3>
          <div class="service-info">
            <el-image 
              :src="booking.image" 
              fit="cover" 
              class="service-image"
              :preview-src-list="booking.image ? [booking.image] : []"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="service-details">
              <h4 class="service-name">{{ booking.itemName }}</h4>
              <el-tag 
                :type="getBookingTypeTagType(booking.bookingType)" 
                size="default"
                style="margin-top: 8px;"
              >
                {{ getBookingTypeText(booking.bookingType) }}
              </el-tag>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 预订信息 -->
        <div class="section">
          <h3 class="section-title">
            <el-icon><Calendar /></el-icon>
            预订信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">预订日期：</span>
              <span class="value">{{ booking.bookingDate || '-' }}</span>
            </div>
            <div class="info-item" v-if="booking.bookingTime && booking.bookingTime !== 'null'">
              <span class="label">时间段：</span>
              <span class="value">{{ formatTimeSlot(booking.bookingTime) }}</span>
            </div>
            <div class="info-item" v-if="booking.bookingType === 3">
              <span class="label">入住日期：</span>
              <span class="value">{{ booking.bookingDate || '-' }}</span>
            </div>
            <div class="info-item" v-if="booking.bookingType === 3 && booking.nights">
              <span class="label">晚数：</span>
              <span class="value">{{ booking.nights }}晚</span>
            </div>
            <div class="info-item">
              <span class="label">人数：</span>
              <span class="value">{{ booking.peopleCount || 1 }}人</span>
            </div>
            <div class="info-item">
              <span class="label">总金额：</span>
              <span class="value amount">¥{{ Number(booking.totalAmount || 0).toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 联系信息 -->
        <div class="section">
          <h3 class="section-title">
            <el-icon><User /></el-icon>
            联系信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">联系人：</span>
              <span class="value">{{ booking.contactName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ booking.contactPhone || '-' }}</span>
            </div>
            <div class="info-item" v-if="booking.specialRequirements && booking.specialRequirements !== 'null'">
              <span class="label">特殊需求：</span>
              <span class="value">{{ booking.specialRequirements }}</span>
            </div>
          </div>
        </div>

        <!-- 取消信息（如果已取消） -->
        <div v-if="booking.status === 4 && booking.cancelReason" class="section cancel-section">
          <el-divider />
          <h3 class="section-title cancel-title">
            <el-icon><Warning /></el-icon>
            取消信息
          </h3>
          <div class="cancel-info">
            <div class="info-item">
              <span class="label">取消原因：</span>
              <span class="value cancel-text">{{ booking.cancelReason }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <el-divider />
        <div class="actions">
          <el-button @click="goBack">返回</el-button>
          <el-button
            v-if="booking.paymentStatus === 1 && booking.status === 2"
            type="primary"
            @click="handlePay"
          >
            <el-icon><Wallet /></el-icon>
            去支付
          </el-button>
          <el-button
            v-if="booking.status === 1"
            type="warning"
            @click="handleCancel"
          >
            <el-icon><Close /></el-icon>
            取消预订
          </el-button>
          <el-button
            v-if="booking.status === 3 && booking.paymentStatus === 2"
            type="primary"
            @click="handleComment"
          >
            <el-icon><ChatDotRound /></el-icon>
            评价
          </el-button>
          <el-button @click="handleContact">
            <el-icon><Phone /></el-icon>
            联系客服
          </el-button>
        </div>
      </el-card>

      <el-empty v-else-if="!loading" description="未找到预订记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Calendar,
  Shop,
  Picture,
  User,
  Warning,
  Wallet,
  Close,
  ChatDotRound,
  Phone
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const booking = ref(null)

// 读取本地模拟"已支付"的预订 ID 列表（仅开发联调阶段使用）
const MOCK_PAID_KEY = 'mock_paid_bookings'
const getMockPaidIds = () => {
  try {
    const raw = localStorage.getItem(MOCK_PAID_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
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

// 加载预订详情
const loadBooking = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('预订ID不存在')
    router.back()
    return
  }

  try {
    loading.value = true
    const res = await request.get(`/culture/booking/${id}`)
    if (res.code === 200 && res.data) {
      const data = res.data
      const images = safeParseImages(data.itemImage)
      
      // 处理时间段显示
      let normalizedTime = ''
      if (data.bookingTime && data.bookingTime !== 'null') {
        normalizedTime = data.bookingTime
      }

      // 处理特殊需求
      const normalizedSpecialRequirements =
        data.specialRequirements && data.specialRequirements !== 'null'
          ? data.specialRequirements
          : ''

      // 检查 mock 支付数据
      const mockPaidIds = getMockPaidIds()
      const isMockPaid = mockPaidIds.includes(data.id)

      booking.value = {
        ...data,
        bookingTime: normalizedTime,
        specialRequirements: normalizedSpecialRequirements,
        image: images[0] || 'https://picsum.photos/300/200?random=22',
        // 如果是民宿，计算晚数（如果有入住和退房日期）
        nights: data.nights || null,
        // 处理支付状态（与列表页保持一致）
        paymentStatus: isMockPaid ? 2 : (data.paymentStatus ?? 1)
      }
    } else {
      ElMessage.error(res.message || '加载预订详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载预订详情失败:', error)
    ElMessage.error('加载预订详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '待确认',
    2: '已确认',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    1: 'warning',
    2: 'success',
    3: 'info',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取预订类型文本
const getBookingTypeText = (type) => {
  const typeMap = {
    1: '文化体验',
    2: '农家乐',
    3: '民宿'
  }
  return typeMap[type] || '未知'
}

// 获取预订类型标签类型
const getBookingTypeTagType = (type) => {
  const typeMap = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return typeMap[type] || 'info'
}

// 格式化时间段
const formatTimeSlot = (timeSlot) => {
  const timeSlotMap = {
    morning: '上午场 (9:00-12:00)',
    afternoon: '下午场 (14:00-17:00)',
    night: '夜场 (18:00-21:00)'
  }
  return timeSlotMap[timeSlot] || timeSlot
}

// 去支付
const handlePay = () => {
  if (!booking.value) return
  router.push(`/home/user/culture/booking-pay/${booking.value.id}`)
}

// 取消预订
const handleCancel = async () => {
  if (!booking.value) return

  try {
    await ElMessageBox.prompt('请输入取消原因', '取消预订', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请输入取消原因',
      inputValidator: (value) => {
        if (!value || value.trim().length === 0) {
          return '请输入取消原因'
        }
        return true
      }
    }).then(async ({ value }) => {
      try {
        await request.put(`/culture/booking/${booking.value.id}/cancel`, {
          reason: value
        })
        ElMessage.success('预订已取消')
        // 重新加载详情
        await loadBooking()
      } catch (error) {
        ElMessage.error('取消预订失败')
      }
    })
  } catch {
    // 用户取消
  }
}

// 评价
const handleComment = () => {
  ElMessage.info('评价功能开发中...')
}

// 联系客服
const handleContact = () => {
  ElMessage.info('客服电话：13977531577')
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadBooking()
})
</script>

<style scoped lang="scss">
.booking-detail-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;

  h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 26px;
    font-weight: 700;
    margin: 0;

    .el-icon {
      font-size: 32px;
      color: #409eff;
    }
  }
}

.detail-container {
  max-width: 1000px;
  margin: 0 auto;
}

.detail-card {
  .booking-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 16px;

    .header-left {
      display: flex;
      flex-direction: column;
      gap: 12px;
      flex: 1;

      .booking-no,
      .booking-time {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        color: #606266;

        .el-icon {
          color: #909399;
        }
      }

      .booking-no {
        font-weight: 600;
        color: #303133;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
    }
  }

  .section {
    margin: 24px 0;

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 16px 0;

      .el-icon {
        color: #409eff;
      }

      &.cancel-title {
        color: #f56c6c;

        .el-icon {
          color: #f56c6c;
        }
      }
    }

    .service-info {
      display: flex;
      gap: 20px;
      align-items: flex-start;

      .service-image {
        width: 200px;
        height: 150px;
        border-radius: 8px;
        flex-shrink: 0;

        :deep(.image-slot) {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 100%;
          background: #f5f7fa;
          color: #909399;
          font-size: 48px;
        }
      }

      .service-details {
        flex: 1;

        .service-name {
          font-size: 20px;
          font-weight: 700;
          color: #303133;
          margin: 0 0 12px 0;
        }
      }
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 16px;

      .info-item {
        display: flex;
        align-items: flex-start;
        font-size: 14px;
        line-height: 1.6;

        .label {
          color: #909399;
          min-width: 100px;
          flex-shrink: 0;
        }

        .value {
          color: #606266;
          flex: 1;
          word-break: break-word;

          &.amount {
            font-size: 18px;
            font-weight: 700;
            color: #f56c6c;
          }

          &.cancel-text {
            color: #f56c6c;
            font-weight: 500;
          }
        }
      }
    }

    &.cancel-section {
      .cancel-info {
        background: #fef0f0;
        border-left: 4px solid #f56c6c;
        padding: 16px;
        border-radius: 4px;
      }
    }
  }

  .actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    flex-wrap: wrap;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;

    .el-button {
      min-width: 100px;
    }
  }
}
</style>

