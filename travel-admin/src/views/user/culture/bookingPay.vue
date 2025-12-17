<template>
  <div class="booking-pay-page">
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Calendar /></el-icon>
        预订支付
      </h1>
    </el-card>

    <el-card v-if="booking" class="booking-card">
      <div class="booking-header">
        <div class="header-left">
          <span class="booking-no">预订编号：{{ booking.bookingNo }}</span>
          <span class="booking-time">
            预定时间：{{ booking.bookingDate }}
            <span v-if="booking.bookingTime"> {{ booking.bookingTime }}</span>
          </span>
        </div>
        <el-tag type="warning" size="large" v-if="booking.paymentStatus === 1">
          待支付
        </el-tag>
        <el-tag type="success" size="large" v-else>
          已支付
        </el-tag>
      </div>

      <el-divider />

      <div class="booking-body">
        <div class="info-left">
          <el-image :src="booking.image" fit="cover" class="booking-image" />
          <div class="project-info">
            <h3 class="project-name">{{ booking.itemName }}</h3>
            <p class="project-type">
              {{ booking.bookingType === 1 ? '文化体验' : booking.bookingType === 2 ? '农家乐' : '民宿' }}
            </p>
          </div>
        </div>
        <div class="info-right">
          <div class="info-row">
            <span class="label">联系人：</span>
            <span>{{ booking.contactName }} {{ booking.contactPhone }}</span>
          </div>
          <div class="info-row">
            <span class="label">人数：</span>
            <span>{{ booking.peopleCount }}人</span>
          </div>
          <div class="info-row" v-if="booking.specialRequirements">
            <span class="label">特殊需求：</span>
            <span>{{ booking.specialRequirements }}</span>
          </div>
          <div class="info-row amount-row">
            <span class="label">应付金额：</span>
            <span class="amount">¥{{ Number(booking.totalAmount || 0).toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="actions">
          <el-button @click="goBack">返回</el-button>
          <el-button
            type="primary"
            :disabled="booking.paymentStatus === 2"
            @click="handlePay"
          >
            {{ booking.paymentStatus === 2 ? '已支付' : '确认支付' }}
          </el-button>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar } from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const booking = ref(null)

// 本地模拟“已支付”记录，用于多页面之间同步状态（仅开发联调阶段使用）
const MOCK_PAID_KEY = 'mock_paid_bookings'

const addMockPaidBooking = (id) => {
  try {
    const raw = localStorage.getItem(MOCK_PAID_KEY)
    const list = raw ? JSON.parse(raw) : []
    if (!list.includes(id)) {
      list.push(id)
      localStorage.setItem(MOCK_PAID_KEY, JSON.stringify(list))
    }
  } catch (e) {
    console.warn('保存模拟支付记录失败', e)
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

const loadBooking = async () => {
  const id = route.params.id
  if (!id) return

  try {
    // TODO: 如果后端有单条预订详情接口，替换为对应接口
    const res = await request.get('/culture/booking/my')
    if (res.code === 200 && Array.isArray(res.data)) {
      const found = res.data.find((b) => String(b.id) === String(id))
      if (!found) {
        ElMessage.error('未找到预订记录')
        return
      }

      // 处理字符串 'null' 的时间段，避免显示在页面上
      const normalizedTime =
        found.bookingTime && found.bookingTime !== 'null' ? found.bookingTime : ''
      const images = safeParseImages(found.itemImage)

      booking.value = {
        ...found,
        bookingTime: normalizedTime,
        image: images[0] || 'https://picsum.photos/120/120?random=21'
      }
    } else {
      ElMessage.error(res.message || '加载预订详情失败')
    }
  } catch (error) {
    console.error('加载预订详情失败:', error)
    ElMessage.error('加载预订详情失败')
  }
}

const handlePay = async () => {
  if (!booking.value) return

  try {
    await ElMessageBox.confirm(
      `确认支付预订？\n\n预订编号：${booking.value.bookingNo}\n金额：¥${Number(
        booking.value.totalAmount || 0
      ).toFixed(2)}`,
      '确认支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // TODO: 调用预订支付接口，例如：await request.post(`/culture/booking/${booking.value.id}/pay`)
    console.log('支付预订:', booking.value.id)

    // 模拟支付成功（本地更新为已支付，并写入 localStorage，供列表页读取）
    ElMessage.success('支付成功')
    booking.value.paymentStatus = 2
    addMockPaidBooking(booking.value.id)

    // 支付成功后返回我的预定列表
    router.push('/home/user/culture/bookings?mode=stay')
  } catch {
    // 用户取消
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadBooking()
})
</script>

<style scoped lang="scss">
.booking-pay-page {
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
  }
}

.booking-card {
  max-width: 960px;
  margin: 0 auto;

  .booking-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .booking-no {
      font-size: 14px;
      color: #909399;
      margin-right: 16px;
    }

    .booking-time {
      font-size: 14px;
      color: #606266;
    }
  }

  .booking-body {
    display: flex;
    gap: 24px;
    margin-top: 16px;

    .info-left {
      display: flex;
      gap: 16px;
      align-items: center;

      .booking-image {
        width: 120px;
        height: 120px;
        border-radius: 8px;
      }

      .project-info {
        .project-name {
          margin: 0 0 8px;
          font-size: 18px;
          font-weight: 600;
        }

        .project-type {
          margin: 0;
          font-size: 14px;
          color: #909399;
        }
      }
    }

    .info-right {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;

      .info-row {
        font-size: 14px;
        color: #606266;

        .label {
          color: #909399;
          margin-right: 6px;
        }
      }

      .amount-row {
        margin-top: 8px;

        .amount {
          font-size: 22px;
          font-weight: 700;
          color: #f56c6c;
        }
      }
    }
  }

  .actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 12px;
  }
}
</style>


