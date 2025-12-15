<template>
  <view class="detail-page" v-if="order">
    <view class="section">
      <view class="section-header">
        <text class="title">订单详情</text>
        <text :class="['status', statusClass(order.orderStatus)]">{{ statusText(order.orderStatus) }}</text>
      </view>
      <view class="row">
        <text class="label">订单号</text>
        <text class="value">{{ order.orderNo }}</text>
      </view>
      <view class="row">
        <text class="label">下单时间</text>
        <text class="value">{{ formatDate(order.createTime) }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="title">商品信息</text>
      </view>
      <view class="row">
        <text class="label">名称</text>
        <text class="value">{{ order.productName || '-' }}</text>
      </view>
      <view class="row">
        <text class="label">数量</text>
        <text class="value">{{ order.quantity || 1 }}</text>
      </view>
      <view class="row">
        <text class="label">原价</text>
        <text class="value">¥{{ (order.totalAmount || 0).toFixed(2) }}</text>
      </view>
      <view class="row" v-if="order.couponDiscount">
        <text class="label">优惠</text>
        <text class="value">-¥{{ order.couponDiscount.toFixed(2) }}</text>
      </view>
      <view class="row total">
        <text class="label">应付金额</text>
        <text class="value highlight">¥{{ (order.finalAmount || order.totalAmount || 0).toFixed(2) }}</text>
      </view>
    </view>

    <view class="section" v-if="order.contactName || order.contactPhone || order.detailedAddress">
      <view class="section-header">
        <text class="title">联系人信息</text>
      </view>
      <view class="row" v-if="order.contactName">
        <text class="label">联系人</text>
        <text class="value">{{ order.contactName }}</text>
      </view>
      <view class="row" v-if="order.contactPhone">
        <text class="label">联系电话</text>
        <text class="value">{{ order.contactPhone }}</text>
      </view>
      <view class="row" v-if="order.detailedAddress">
        <text class="label">地址</text>
        <text class="value">{{ order.detailedAddress }}</text>
      </view>
    </view>

    <view class="footer-actions">
      <button
        v-if="order.orderStatus === 1"
        class="primary-btn"
        @click="payOrder"
      >
        立即支付
      </button>
      <button class="ghost-btn" @click="back">返回</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from 'vue'
import request from '@/utils/request'

const order = ref<any | null>(null)

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

const back = () => {
  uni.navigateBack()
}

const payOrder = async () => {
  if (!order.value) return
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    uni.navigateTo({ url: '/pages/user/login' })
    return
  }
  uni.showLoading({ title: '支付中...' })
  try {
    const res = await request.put(
      `/culture/order/${order.value.orderNo}/pay`,
      {},
      { header: { Authorization: `Bearer ${token}` } }
    )
    if (res.code === 200) {
      uni.showToast({ title: '支付成功', icon: 'success' })
      order.value.orderStatus = 2
    } else {
      uni.showToast({ title: res.message || '支付失败', icon: 'none' })
    }
  } catch (err: any) {
    uni.showToast({ title: err.message || '支付失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

onLoad((options) => {
  if (options?.order) {
    try {
      order.value = JSON.parse(decodeURIComponent(options.order))
    } catch (e) {
      uni.showToast({ title: '订单信息解析失败', icon: 'none' })
    }
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding: 20rpx;
}

.section {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1f2937;
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

.row {
  display: flex;
  justify-content: space-between;
  margin: 10rpx 0;
}

.label {
  color: #6b7280;
  font-size: 26rpx;
}

.value {
  color: #111827;
  font-size: 28rpx;
  text-align: right;
}

.total .value {
  font-weight: 700;
}

.highlight {
  color: #ef4444;
}

.footer-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 12rpx;
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

.ghost-btn {
  flex: 1;
  border: 1rpx solid #e5e7eb;
  color: #374151;
  padding: 18rpx;
  border-radius: 12rpx;
  background: #fff;
  font-size: 28rpx;
}
</style>


