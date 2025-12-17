<template>
  <view class="order-page">
    <view class="page-header">
      <text class="title">我的订单</text>
      <text class="subtitle">查看文旅商品的下单记录与状态</text>
    </view>

    <view v-if="!isLoggedIn" class="empty-state">
      <text class="empty-title">请先登录后查看订单</text>
      <button class="primary-btn" @click="goLogin">前往登录</button>
    </view>

    <view v-else>
      <view v-if="loading" class="loading">加载中...</view>
      <view v-else-if="orders.length === 0" class="empty-state">
        <text class="empty-title">暂无订单</text>
        <text class="empty-desc">下单后可在此查看进度与支付状态</text>
      </view>

      <scroll-view v-else class="order-list" scroll-y>
        <view v-for="order in orders" :key="order.orderNo" class="order-card" @click="goDetail(order)">
          <view class="card-header">
            <text class="order-no">订单号：{{ order.orderNo }}</text>
            <text :class="['status', statusClass(order.orderStatus)]">{{ statusText(order.orderStatus) }}</text>
          </view>
          <view class="card-body">
            <view class="row">
              <text class="label">商品</text>
              <text class="value">{{ order.productName || '-' }}</text>
            </view>
            <view class="row">
              <text class="label">金额</text>
              <text class="value price">¥{{ (order.finalAmount || order.totalAmount || 0).toFixed(2) }}</text>
            </view>
            <view class="row">
              <text class="label">下单时间</text>
              <text class="value">{{ formatDate(order.createTime) }}</text>
            </view>
            <view class="row" v-if="order.couponDiscount">
              <text class="label">优惠</text>
              <text class="value">-¥{{ order.couponDiscount.toFixed(2) }}</text>
            </view>
          </view>
          <view class="card-footer">
            <button
              v-if="order.orderStatus === 1"
              class="primary-btn small"
              @click.stop="payOrder(order)"
            >
              去支付
            </button>
            <button class="ghost-btn small" @click.stop="goDetail(order)">查看详情</button>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onShow } from 'vue'
import request from '@/utils/request'

const orders = ref<any[]>([])
const loading = ref(false)
const isLoggedIn = ref(false)

const page = ref(1)
const size = ref(20)

const formatDate = (val?: string) => {
  if (!val) return '-'
  const d = new Date(val)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const statusText = (status?: number) => {
  switch (status) {
    case 1:
      return '待支付'
    case 2:
      return '已支付'
    case 3:
      return '已完成'
    case 4:
      return '已取消'
    default:
      return '未知'
  }
}

const statusClass = (status?: number) => {
  return {
    pending: status === 1,
    success: status === 2 || status === 3,
    danger: status === 4
  }
}

const ensureLogin = (): string | null => {
  const token = uni.getStorageSync('token')
  isLoggedIn.value = !!token
  if (!token) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return null
  }
  return token
}

const loadOrders = async () => {
  const token = ensureLogin()
  if (!token) {
    orders.value = []
    return
  }
  loading.value = true
  try {
    const res = await request.get(
      '/culture/order/my',
      { page: page.value, size: size.value },
      { header: { Authorization: `Bearer ${token}` } }
    )
    if (res.code === 200) {
      orders.value = res.data?.records || res.data?.records || res.data || []
    } else {
      uni.showToast({ title: res.message || '获取订单失败', icon: 'none' })
    }
  } catch (err: any) {
    uni.showToast({ title: err.message || '获取订单失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const payOrder = async (order: any) => {
  const token = ensureLogin()
  if (!token) return
  uni.showLoading({ title: '支付中...' })
  try {
    const res = await request.put(
      `/culture/order/${order.orderNo}/pay`,
      {},
      { header: { Authorization: `Bearer ${token}` } }
    )
    if (res.code === 200) {
      uni.showToast({ title: '支付成功', icon: 'success' })
      await loadOrders()
    } else {
      uni.showToast({ title: res.message || '支付失败', icon: 'none' })
    }
  } catch (err: any) {
    uni.showToast({ title: err.message || '支付失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

const goDetail = (order: any) => {
  const encoded = encodeURIComponent(JSON.stringify(order))
  uni.navigateTo({
    url: `/pages/order/detail?order=${encoded}`
  })
}

const goLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' })
}

onShow(async () => {
  await loadOrders()
})
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding: 20rpx;
}

.page-header {
  margin-bottom: 20rpx;
}

.title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1f2937;
}

.subtitle {
  display: block;
  margin-top: 6rpx;
  color: #6b7280;
  font-size: 26rpx;
}

.order-list {
  max-height: calc(100vh - 200rpx);
}

.order-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.order-no {
  font-size: 26rpx;
  color: #374151;
}

.status {
  font-size: 24rpx;
  padding: 6rpx 12rpx;
  border-radius: 12rpx;
}

.status.pending {
  background: #fff7ed;
  color: #f97316;
}

.status.success {
  background: #ecfdf3;
  color: #16a34a;
}

.status.danger {
  background: #fef2f2;
  color: #dc2626;
}

.card-body .row {
  display: flex;
  justify-content: space-between;
  margin: 8rpx 0;
}

.label {
  color: #6b7280;
  font-size: 26rpx;
}

.value {
  color: #111827;
  font-size: 28rpx;
}

.value.price {
  color: #ef4444;
  font-weight: 600;
}

.card-footer {
  margin-top: 16rpx;
  display: flex;
  gap: 12rpx;
}

.primary-btn {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 18rpx;
  border-radius: 12rpx;
  text-align: center;
  font-size: 28rpx;
}

.primary-btn.small {
  padding: 12rpx;
  font-size: 26rpx;
}

.ghost-btn {
  flex: 1;
  border: 1rpx solid #e5e7eb;
  color: #374151;
  padding: 18rpx;
  border-radius: 12rpx;
  background: #fff;
  font-size: 28rpx;
}

.ghost-btn.small {
  padding: 12rpx;
  font-size: 26rpx;
}

.empty-state {
  margin-top: 80rpx;
  text-align: center;
  color: #6b7280;
}

.empty-title {
  font-size: 30rpx;
  font-weight: 600;
}

.empty-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 26rpx;
}

.loading {
  text-align: center;
  color: #6b7280;
  margin-top: 40rpx;
}
</style>
















