<template>
  <div class="order-detail-page">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Document /></el-icon>
        订单详情
      </h1>
    </el-card>

    <!-- 加载中 -->
    <el-empty v-if="loading && !order" description="加载中..." />

    <!-- 主体内容 -->
    <template v-if="order">
      <!-- 订单状态（仅非待付款时展示） -->
      <el-card v-if="order.status !== 'pending'" class="status-card">
        <div class="status-content">
          <div class="status-icon">
            <el-icon v-if="order.status === 'pending'" class="pending-icon"><Clock /></el-icon>
            <el-icon v-else-if="order.status === 'paid'" class="paid-icon"><Ticket /></el-icon>
            <el-icon v-else-if="order.status === 'shipped'" class="shipped-icon"><Van /></el-icon>
            <el-icon v-else-if="order.status === 'completed'" class="completed-icon"><CircleCheck /></el-icon>
            <el-icon v-else class="cancelled-icon"><CircleClose /></el-icon>
          </div>
          <div class="status-text">
            <h2>{{ getStatusText(order.status) }}</h2>
            <p v-if="order.status === 'shipped'" class="status-desc">您的订单正在配送中，请耐心等待</p>
            <p v-else-if="order.status === 'completed'" class="status-desc">交易成功，感谢您的支持</p>
          </div>
        </div>
      </el-card>

      <!-- 物流信息 -->
      <el-card v-if="order.status === 'shipped' || order.status === 'completed'" class="logistics-card">
      <h3>
        <el-icon><Van /></el-icon>
        物流信息
      </h3>

      <div class="logistics-info">
        <div class="info-row">
          <span class="label">物流公司：</span>
          <span>{{ order.logistics?.company || '顺丰速运' }}</span>
        </div>
        <div class="info-row">
          <span class="label">运单号：</span>
          <span>{{ order.logistics?.trackingNo || 'SF1234567890' }}</span>
          <el-button link type="primary" size="small" @click="copyTrackingNo">复制</el-button>
        </div>
      </div>

      <el-timeline class="logistics-timeline">
        <el-timeline-item
          v-for="(track, index) in logisticsTrack"
          :key="index"
          :timestamp="track.time"
          :type="index === 0 ? 'primary' : 'info'"
        >
          {{ track.desc }}
        </el-timeline-item>
      </el-timeline>
      </el-card>

      <!-- 收货信息 -->
      <el-card class="address-card">
      <h3>
        <el-icon><Location /></el-icon>
        收货信息
      </h3>

      <div class="address-info">
        <div class="info-row">
          <span class="label">收货人：</span>
          <span>{{ order.receiver }}</span>
        </div>
        <div class="info-row">
          <span class="label">手机号：</span>
          <span>{{ order.phone }}</span>
        </div>
        <div class="info-row">
          <span class="label">收货地址：</span>
          <span>{{ order.address }}</span>
        </div>
      </div>
      </el-card>

      <!-- 商品信息 -->
      <el-card class="goods-card">
      <h3>
        <el-icon><ShoppingBag /></el-icon>
        商品信息
      </h3>

      <div class="goods-list">
        <div
          v-for="item in order.items"
          :key="item.id"
          class="goods-item"
          @click="goToProduct(item.productId)"
        >
          <el-image :src="item.image" fit="cover" class="goods-image" />
          <div class="goods-info">
            <p class="goods-name">{{ item.name }}</p>
            <p class="goods-spec">规格：{{ item.specification }}</p>
          </div>
          <div class="goods-price">
            <span class="price">¥{{ item.price }}</span>
            <span class="quantity">x {{ item.quantity }}</span>
          </div>
          <div class="goods-subtotal">
            ¥{{ (item.price * item.quantity).toFixed(2) }}
          </div>
        </div>
      </div>
      </el-card>

      <!-- 订单信息 -->
      <el-card class="order-info-card">
      <h3>
        <el-icon><Tickets /></el-icon>
        订单信息
      </h3>

      <div class="info-grid">
        <div class="info-item">
          <span class="label">订单编号：</span>
          <span>{{ order.orderNo }}</span>
          <el-button link type="primary" size="small" @click="copyOrderNo">复制</el-button>
        </div>
        <div class="info-item">
          <span class="label">下单时间：</span>
          <span>{{ formatDateTime(order.createTime) }}</span>
        </div>
        <div class="info-item">
          <span class="label">支付方式：</span>
          <span>{{ order.payMethod || '在线支付' }}</span>
        </div>
        <div class="info-item">
          <span class="label">支付时间：</span>
          <span>{{ order.payTime || '-' }}</span>
        </div>
        <div v-if="order.invoice" class="info-item">
          <span class="label">发票信息：</span>
          <span>{{ order.invoice }}</span>
        </div>
        <div v-if="order.remark" class="info-item full-width">
          <span class="label">订单备注：</span>
          <span>{{ order.remark }}</span>
        </div>
      </div>
      </el-card>

      <!-- 费用明细 -->
      <el-card class="price-card">
      <h3>
        <el-icon><Wallet /></el-icon>
        费用明细
      </h3>

      <div class="price-list">
        <div class="price-item">
          <span class="label">商品总价：</span>
          <span>¥{{ order.goodsAmount.toFixed(2) }}</span>
        </div>
        <div class="price-item">
          <span class="label">运费：</span>
          <span class="shipping">{{ order.shippingFee > 0 ? `¥${order.shippingFee.toFixed(2)}` : '免运费' }}</span>
        </div>
        <div v-if="order.couponAmount > 0" class="price-item discount">
          <span class="label">优惠券：</span>
          <span>-¥{{ order.couponAmount.toFixed(2) }}</span>
        </div>
        <el-divider />
        <div class="price-item total">
          <span class="label">实付款：</span>
          <span class="amount">¥{{ order.totalAmount.toFixed(2) }}</span>
        </div>
      </div>
      </el-card>

      <!-- 底部操作栏：待付款展示支付/取消，其他状态展示对应操作 -->
      <div v-if="order.status === 'pending'" class="bottom-actions">
        <el-button type="primary" size="large" @click="handlePay">
          立即支付
        </el-button>
        <el-button size="large" @click="handleCancel">
          取消订单
        </el-button>
      </div>
      <div v-else class="bottom-actions">
        <el-button v-if="order.status === 'shipped'" type="primary" size="large" @click="handleConfirm">
          确认收货
        </el-button>
        <el-button v-if="order.status === 'completed'" type="primary" size="large" @click="handleComment">
          评价晒单
        </el-button>
        <el-button v-if="order.status === 'completed'" size="large" @click="handleRefund">
          申请退款
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'
import {
  Document,
  Clock,
  Ticket,
  Van,
  CircleCheck,
  CircleClose,
  Location,
  ShoppingBag,
  Tickets,
  Wallet
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 订单ID（路由参数中存的是数据库主键ID）
const orderId = route.params.id as string

type OrderStatus = 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'

interface OrderItem {
  id: number
  productId: number
  name: string
  image: string
  specification: string
  price: number
  quantity: number
}

interface OrderDetailView {
  id: number
  orderNo: string
  createTime: string | null
  payTime: string | null
  status: OrderStatus
  payMethod: string
  items: OrderItem[]
  receiver: string
  phone: string
  address: string
  goodsAmount: number
  shippingFee: number
  couponAmount: number
  totalAmount: number
  remark: string
  invoice: string | null
  logistics: {
    company: string | null
    trackingNo: string | null
  } | null
}

// 订单详情（从后端加载）
const order = ref<OrderDetailView | null>(null)
const loading = ref(false)

// 倒计时
const countdown = ref('29:59')
let timer = null

// 物流轨迹
const logisticsTrack = ref([
  {
    time: '2024-10-29 16:00:00',
    desc: '【重庆市】快件已到达【重庆渝中营业点】，正在派送中，派送员：张师傅，电话：138****8888'
  },
  {
    time: '2024-10-29 14:30:00',
    desc: '【重庆市】快件已到达【重庆分拨中心】'
  },
  {
    time: '2024-10-29 10:00:00',
    desc: '【重庆市】快件已从【重庆江北营业点】发出'
  },
  {
    time: '2024-10-29 09:00:00',
    desc: '【重庆市】您的订单已由卖家发货，快递公司：顺丰速运'
  }
])

// 获取状态文本
const getStatusText = (status: OrderStatus) => {
  const statusMap = {
    pending: '等待买家付款',
    paid: '等待卖家发货',
    shipped: '卖家已发货',
    completed: '交易成功',
    cancelled: '交易关闭'
  }
  return statusMap[status] || '未知状态'
}

// 启动倒计时
const startCountdown = () => {
  if (order.value.status !== 'pending') return

  let seconds = 30 * 60 // 30分钟
  timer = setInterval(() => {
    seconds--
    if (seconds <= 0) {
      clearInterval(timer)
      countdown.value = '00:00'
      ElMessage.warning('订单已超时，自动取消')
      order.value.status = 'cancelled'
      return
    }

    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    countdown.value = `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
  }, 1000)
}

// 复制运单号
const copyTrackingNo = () => {
  const trackingNo = order.value?.logistics?.trackingNo || 'SF1234567890'
  navigator.clipboard.writeText(trackingNo).then(() => {
    ElMessage.success('运单号已复制')
  })
}

// 复制订单号
const copyOrderNo = () => {
  if (!order.value) return
  navigator.clipboard.writeText(order.value.orderNo).then(() => {
    ElMessage.success('订单号已复制')
  })
}

// 跳转到商品详情
const goToProduct = (id) => {
  router.push(`/home/user/culture/product/${id}`)
}

// 立即支付
const handlePay = async () => {
  try {
    await ElMessageBox.confirm(
      `确认支付订单？\n\n订单号：${order.value.orderNo}\n金额：¥${order.value.totalAmount.toFixed(2)}`,
      '确认支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 调用支付成功接口（模拟）
    const res = await request.put(`/culture/order/${order.value.orderNo}/pay`)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      await loadOrderDetail()
      if (timer) {
        clearInterval(timer)
      }
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch {
    // 用户取消
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.put(`/culture/order/${order.value.id}/cancel`)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      if (timer) {
        clearInterval(timer)
      }
      await loadOrderDetail()
    } else {
      ElMessage.error(res.message || '取消订单失败')
    }
  } catch {
    // 用户取消
  }
}

// 确认收货
const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm('确认已收到货物？', '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })

    const res = await request.put(`/culture/order/${order.value.id}/confirm`)
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      await loadOrderDetail()
    } else {
      ElMessage.error(res.message || '确认收货失败')
    }
  } catch {
    // 用户取消
  }
}

// 评价
const handleComment = () => {
  ElMessage.info('评价功能开发中...')
}

// 申请退款
const handleRefund = () => {
  ElMessage.info('退款功能开发中...')
}

// 映射后端订单状态为前端状态字符串
const mapStatus = (status?: number | null): OrderStatus => {
  switch (status) {
    case 1:
      return 'pending'
    case 2:
      return 'paid'
    case 3:
      return 'shipped'
    case 4:
      return 'completed'
    case 5:
      return 'cancelled'
    default:
      return 'pending'
  }
}

// 从后端加载订单详情
const loadOrderDetail = async () => {
  if (!orderId) {
    ElMessage.error('缺少订单ID')
    router.back()
    return
  }
  try {
    loading.value = true
    const res = await request.get(`/culture/order/${orderId}`)
    if (res.code !== 200 || !res.data) {
      ElMessage.error(res.message || '加载订单详情失败')
      router.back()
      return
    }
    const data = res.data
    const goodsAmount = Number(data.totalAmount || 0)
    const couponAmount = Number(data.couponDiscount || 0)
    const finalAmount = Number(data.finalAmount || goodsAmount - couponAmount)
    // 根据总价和优惠反推运费（final = goods + shipping - coupon）
    const shippingFee = Math.max(0, finalAmount - goodsAmount + couponAmount)

    order.value = {
      id: data.id,
      orderNo: data.orderNo,
      createTime: data.createTime,
      payTime: data.paymentTime,
      status: mapStatus(data.orderStatus),
      payMethod: data.paymentMethod || '在线支付',
      items: [
        {
          id: data.productId,
          productId: data.productId,
          name: data.productName,
          image: data.productImage,
          specification: data.specification || data.productSpec || '',
          price: Number(data.productPrice || 0),
          quantity: data.quantity || 1
        }
      ],
      receiver: data.contactName || '',
      phone: data.contactPhone || '',
      address:
        (data.province || '') +
        (data.city ? ` ${data.city}` : '') +
        (data.district ? ` ${data.district}` : '') +
        (data.detailedAddress ? ` ${data.detailedAddress}` : ''),
      goodsAmount,
      shippingFee,
      couponAmount,
      totalAmount: finalAmount,
      remark: data.remark || data.buyerMessage || '',
      invoice: data.invoiceTitle || null,
      logistics: data.shipCompany || data.shipNo
        ? {
            company: data.shipCompany,
            trackingNo: data.shipNo
          }
        : null
    }

    // 待支付订单启动倒计时
    if (order.value.status === 'pending') {
      startCountdown()
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrderDetail()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped lang="scss">
.order-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
  padding-bottom: 100px;
}

.page-header {
  margin-bottom: 24px;

  h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin: 0;

    .el-icon {
      font-size: 28px;
      color: #409eff;
    }
  }
}

.status-card {
  margin-bottom: 24px;

  .status-content {
    display: flex;
    align-items: center;
    gap: 24px;

    .status-icon {
      width: 80px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;

      .el-icon {
        font-size: 48px;
      }

      .pending-icon {
        color: #e6a23c;
        background: rgba(230, 162, 60, 0.1);
        padding: 20px;
        border-radius: 50%;
      }

      .paid-icon {
        color: #409eff;
        background: rgba(64, 158, 255, 0.1);
        padding: 20px;
        border-radius: 50%;
      }

      .shipped-icon {
        color: #409eff;
        background: rgba(64, 158, 255, 0.1);
        padding: 20px;
        border-radius: 50%;
      }

      .completed-icon {
        color: #67c23a;
        background: rgba(103, 194, 58, 0.1);
        padding: 20px;
        border-radius: 50%;
      }

      .cancelled-icon {
        color: #909399;
        background: rgba(144, 147, 153, 0.1);
        padding: 20px;
        border-radius: 50%;
      }
    }

    .status-text {
      flex: 1;

      h2 {
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        margin: 0 0 8px 0;
      }

      .status-desc {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }
  }

  .countdown {
    margin-top: 16px;
    padding: 12px 20px;
    background: #fef0f0;
    border-radius: 8px;
    color: #f56c6c;
    font-size: 16px;
    font-weight: 600;

    .time {
      margin-left: 8px;
      font-size: 20px;
      font-family: monospace;
    }
  }
}

.logistics-card,
.address-card,
.goods-card,
.order-info-card,
.price-card {
  margin-bottom: 24px;

  h3 {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 20px 0;

    .el-icon {
      font-size: 20px;
      color: #409eff;
    }
  }
}

.logistics-info,
.address-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;

  .info-row {
    display: flex;
    align-items: center;
    font-size: 15px;
    color: #606266;

    .label {
      min-width: 90px;
      color: #909399;
      font-weight: 500;
    }
  }
}

.logistics-timeline {
  margin-top: 20px;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 16px;

  .goods-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 8px;
    background: #f5f7fa;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #e8f4ff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
    }

    .goods-image {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      flex-shrink: 0;
    }

    .goods-info {
      flex: 1;

      .goods-name {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 8px 0;
      }

      .goods-spec {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }

    .goods-price {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 4px;

      .price {
        font-size: 16px;
        color: #f56c6c;
        font-weight: 600;
      }

      .quantity {
        font-size: 14px;
        color: #909399;
      }
    }

    .goods-subtotal {
      min-width: 100px;
      text-align: right;
      font-size: 18px;
      font-weight: 700;
      color: #303133;
    }
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .info-item {
    display: flex;
    align-items: center;
    font-size: 15px;
    color: #606266;

    .label {
      min-width: 90px;
      color: #909399;
      font-weight: 500;
    }

    &.full-width {
      grid-column: 1 / -1;
    }
  }
}

.price-list {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .price-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 15px;
    color: #606266;

    .label {
      color: #909399;
      font-weight: 500;
    }

    .shipping {
      color: #67c23a;
      font-weight: 600;
    }

    &.discount {
      span:last-child {
        color: #f56c6c;
        font-weight: 600;
      }
    }

    &.total {
      margin-top: 8px;
      font-size: 18px;
      font-weight: 700;

      .amount {
        font-size: 24px;
        color: #f56c6c;
      }
    }
  }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px;
  background: white;
  border-top: 1px solid #ebeef5;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  z-index: 1000;
}
</style>

