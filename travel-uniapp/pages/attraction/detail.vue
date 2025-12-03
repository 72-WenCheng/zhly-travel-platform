<template>
  <view class="detail-page">
    <view class="banner" v-if="detailLoaded">
      <image :src="detailInfo.coverImage || defaultCover" class="banner-image" mode="aspectFill" />
      <view class="banner-mask"></view>
      <view class="banner-info">
        <text class="banner-name">{{ detailInfo.name || '景点详情' }}</text>
        <view class="banner-rating">
          <text class="rating-score">{{ (ratingSummary.averageScore || 0).toFixed(1) }}</text>
          <text class="rating-star">★</text>
          <text class="rating-count">（{{ ratingSummary.ratingCount }} 人评分）</text>
        </view>
      </view>
    </view>

    <view class="card">
      <view class="card-header">
        <text class="card-title">基础信息</text>
      </view>
      <view class="info-row">
        <text class="info-label">📍 位置</text>
        <text class="info-value">{{ detailInfo.city || '-' }} · {{ detailInfo.address || '暂无地址信息' }}</text>
      </view>
      <view class="info-row" v-if="detailInfo.openTime">
        <text class="info-label">🕒 开放时间</text>
        <text class="info-value">{{ detailInfo.openTime }}</text>
      </view>
      <view class="info-row" v-if="detailInfo.ticketPrice !== undefined">
        <text class="info-label">🎫 门票</text>
        <text class="info-value">{{ detailInfo.ticketPrice > 0 ? `¥${detailInfo.ticketPrice}` : '免费开放' }}</text>
      </view>
      <view class="info-row" v-if="detailInfo.description">
        <text class="info-label">📝 简介</text>
        <text class="info-desc">{{ detailInfo.description }}</text>
      </view>
    </view>

    <view class="card rating-card">
      <view class="card-header">
        <text class="card-title">景点评分</text>
        <text class="card-subtitle">点击星星即可评分，可随时调整</text>
      </view>

      <view class="rating-summary">
        <view class="average">
          <text class="average-score">{{ (ratingSummary.averageScore || 0).toFixed(1) }}</text>
          <text class="average-text">平均分</text>
        </view>
        <view class="rating-count-text">{{ ratingSummary.ratingCount }} 人已评分</view>
      </view>

      <view class="rating-stars">
        <text
          v-for="star in 5"
          :key="star"
          class="star"
          :class="{ active: star <= displayedUserScore }"
          @click="handleRate(star)"
        >★</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app'
import request from '@/utils/request'

const detail = ref<any>(null)
const ratingSummary = ref({
  averageScore: 0,
  ratingCount: 0,
  userScore: 0
})
const attractionId = ref<number | null>(null)
const submitting = ref(false)
const tempScore = ref<number | null>(null)
const defaultCover = 'https://via.placeholder.com/750x400?text=Attraction'

const detailInfo = computed(() => detail.value || {})
const detailLoaded = computed(() => !!detail.value)

const displayedUserScore = computed(() => {
  if (tempScore.value !== null) {
    return tempScore.value
  }
  return ratingSummary.value.userScore || 0
})

const fetchDetail = async () => {
  if (!attractionId.value) return
  const res = await request.get(`/attraction/${attractionId.value}`)
  if (res.code === 200) {
    detail.value = res.data
  } else {
    throw new Error(res.message || '获取景点详情失败')
  }
}

const fetchRatingSummary = async () => {
  if (!attractionId.value) return
  const token = uni.getStorageSync('token')
  const res = await request.get(
    `/attraction/${attractionId.value}/rating`,
    null,
    token ? { header: { Authorization: `Bearer ${token}` } } : {}
  )
  if (res.code === 200) {
    ratingSummary.value = {
      averageScore: res.data?.averageScore || 0,
      ratingCount: res.data?.ratingCount || 0,
      userScore: res.data?.userScore || 0
    }
  } else {
    throw new Error(res.message || '获取评分信息失败')
  }
}

const refreshPage = async () => {
  await Promise.all([fetchDetail(), fetchRatingSummary()])
}

const handleRate = async (score: number) => {
  if (!attractionId.value) return
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.showToast({
      title: '请先登录后再评分',
      icon: 'none'
    })
    uni.navigateTo({
      url: '/pages/user/login'
    })
    return
  }

  if (submitting.value) return
  submitting.value = true
  tempScore.value = score

  try {
    const res = await request.post(
      `/attraction/${attractionId.value}/rating`,
      { score },
      { header: { Authorization: `Bearer ${token}` } }
    )
    if (res.code === 200) {
      ratingSummary.value = {
        averageScore: res.data?.averageScore || 0,
        ratingCount: res.data?.ratingCount || 0,
        userScore: res.data?.userScore || score
      }
      uni.showToast({
        title: '感谢您的评分',
        icon: 'success'
      })
    } else {
      throw new Error(res.message || '评分失败')
    }
  } catch (error: any) {
    tempScore.value = null
    uni.showToast({
      title: error.message || '评分失败',
      icon: 'none'
    })
  } finally {
    submitting.value = false
  }
}

onLoad(async (options) => {
  if (options?.id) {
    attractionId.value = Number(options.id)
    try {
      await refreshPage()
    } catch (error: any) {
      uni.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  } else {
    uni.showToast({
      title: '未找到景点信息',
      icon: 'none'
    })
  }
})

onPullDownRefresh(async () => {
  try {
    await refreshPage()
  } finally {
    uni.stopPullDownRefresh()
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f7fb;
  padding-bottom: 40rpx;
}

.banner {
  position: relative;
  height: 360rpx;
}

.banner-image {
  width: 100%;
  height: 360rpx;
}

.banner-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0.2), rgba(0,0,0,0.6));
}

.banner-info {
  position: absolute;
  bottom: 40rpx;
  left: 40rpx;
  right: 40rpx;
  color: #fff;
}

.banner-name {
  font-size: 42rpx;
  font-weight: 600;
  margin-bottom: 12rpx;
  display: block;
}

.banner-rating {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  font-size: 26rpx;
}

.rating-score {
  font-size: 48rpx;
  font-weight: 700;
}

.rating-star {
  color: #ffd700;
  font-size: 32rpx;
}

.card {
  background: #fff;
  margin: 20rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 12rpx 30rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 20rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1f2937;
}

.card-subtitle {
  display: block;
  font-size: 24rpx;
  color: #94a3b8;
  margin-top: 6rpx;
}

.info-row {
  margin-top: 20rpx;
}

.info-label {
  font-size: 26rpx;
  color: #64748b;
}

.info-value {
  display: block;
  margin-top: 8rpx;
  font-size: 30rpx;
  color: #1e293b;
}

.info-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  color: #475569;
  line-height: 1.6;
}

.rating-card {
  margin-top: 24rpx;
}

.rating-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.average {
  display: flex;
  align-items: baseline;
  gap: 10rpx;
}

.average-score {
  font-size: 56rpx;
  font-weight: 700;
  color: #f97316;
}

.average-text {
  font-size: 26rpx;
  color: #94a3b8;
}

.rating-count-text {
  font-size: 26rpx;
  color: #94a3b8;
}

.rating-stars {
  display: flex;
  justify-content: space-between;
  padding: 0 20rpx;
}

.star {
  font-size: 48rpx;
  color: #e2e8f0;
  transition: color 0.2s ease;
}

.star.active {
  color: #fbbf24;
}
</style>




  return ratingSummary.value.userScore || 0
})

const fetchDetail = async () => {
  if (!attractionId.value) return
  const res = await request.get(`/attraction/${attractionId.value}`)
  if (res.code === 200) {
    detail.value = res.data
  } else {
    throw new Error(res.message || '获取景点详情失败')
  }
}

const fetchRatingSummary = async () => {
  if (!attractionId.value) return
  const token = uni.getStorageSync('token')
  const res = await request.get(
    `/attraction/${attractionId.value}/rating`,
    null,
    token ? { header: { Authorization: `Bearer ${token}` } } : {}
  )
  if (res.code === 200) {
    ratingSummary.value = {
      averageScore: res.data?.averageScore || 0,
      ratingCount: res.data?.ratingCount || 0,
      userScore: res.data?.userScore || 0
    }
  } else {
    throw new Error(res.message || '获取评分信息失败')
  }
}

const refreshPage = async () => {
  await Promise.all([fetchDetail(), fetchRatingSummary()])
}

const handleRate = async (score: number) => {
  if (!attractionId.value) return
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.showToast({
      title: '请先登录后再评分',
      icon: 'none'
    })
    uni.navigateTo({
      url: '/pages/user/login'
    })
    return
  }

  if (submitting.value) return
  submitting.value = true
  tempScore.value = score

  try {
    const res = await request.post(
      `/attraction/${attractionId.value}/rating`,
      { score },
      { header: { Authorization: `Bearer ${token}` } }
    )
    if (res.code === 200) {
      ratingSummary.value = {
        averageScore: res.data?.averageScore || 0,
        ratingCount: res.data?.ratingCount || 0,
        userScore: res.data?.userScore || score
      }
      uni.showToast({
        title: '感谢您的评分',
        icon: 'success'
      })
    } else {
      throw new Error(res.message || '评分失败')
    }
  } catch (error: any) {
    tempScore.value = null
    uni.showToast({
      title: error.message || '评分失败',
      icon: 'none'
    })
  } finally {
    submitting.value = false
  }
}

onLoad(async (options) => {
  if (options?.id) {
    attractionId.value = Number(options.id)
    try {
      await refreshPage()
    } catch (error: any) {
      uni.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  } else {
    uni.showToast({
      title: '未找到景点信息',
      icon: 'none'
    })
  }
})

onPullDownRefresh(async () => {
  try {
    await refreshPage()
  } finally {
    uni.stopPullDownRefresh()
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f7fb;
  padding-bottom: 40rpx;
}

.banner {
  position: relative;
  height: 360rpx;
}

.banner-image {
  width: 100%;
  height: 360rpx;
}

.banner-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0.2), rgba(0,0,0,0.6));
}

.banner-info {
  position: absolute;
  bottom: 40rpx;
  left: 40rpx;
  right: 40rpx;
  color: #fff;
}

.banner-name {
  font-size: 42rpx;
  font-weight: 600;
  margin-bottom: 12rpx;
  display: block;
}

.banner-rating {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  font-size: 26rpx;
}

.rating-score {
  font-size: 48rpx;
  font-weight: 700;
}

.rating-star {
  color: #ffd700;
  font-size: 32rpx;
}

.card {
  background: #fff;
  margin: 20rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 12rpx 30rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 20rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1f2937;
}

.card-subtitle {
  display: block;
  font-size: 24rpx;
  color: #94a3b8;
  margin-top: 6rpx;
}

.info-row {
  margin-top: 20rpx;
}

.info-label {
  font-size: 26rpx;
  color: #64748b;
}

.info-value {
  display: block;
  margin-top: 8rpx;
  font-size: 30rpx;
  color: #1e293b;
}

.info-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  color: #475569;
  line-height: 1.6;
}

.rating-card {
  margin-top: 24rpx;
}

.rating-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.average {
  display: flex;
  align-items: baseline;
  gap: 10rpx;
}

.average-score {
  font-size: 56rpx;
  font-weight: 700;
  color: #f97316;
}

.average-text {
  font-size: 26rpx;
  color: #94a3b8;
}

.rating-count-text {
  font-size: 26rpx;
  color: #94a3b8;
}

.rating-stars {
  display: flex;
  justify-content: space-between;
  padding: 0 20rpx;
}

.star {
  font-size: 48rpx;
  color: #e2e8f0;
  transition: color 0.2s ease;
}

.star.active {
  color: #fbbf24;
}
</style>



