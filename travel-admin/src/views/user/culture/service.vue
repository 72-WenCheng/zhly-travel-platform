<template>
  <div class="service-detail">
    <!-- 返回按钮 -->
    <BackButton />

    <el-row :gutter="0">
      <!-- 左侧：服务详情 -->
      <el-col :span="16" class="left-content">
        <!-- 服务头图 -->
        <div class="detail-header">
          <el-carousel :interval="4000" arrow="never" height="450px">
            <el-carousel-item v-for="(img, index) in service.images" :key="index">
              <img :src="img" :alt="service.title" />
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 服务信息 -->
        <el-card class="info-card">
          <h1 class="service-title">{{ service.title }}</h1>
          <div class="service-meta">
            <span class="meta-item">
              <el-icon><Location /></el-icon>
              {{ service.location }}
            </span>
            <span class="meta-item">
              <el-icon><Star /></el-icon>
              {{ service.rating }} 分
            </span>
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ service.views }} 次浏览
            </span>
          </div>

          <el-divider />

          <div class="service-intro">
            <h3>服务介绍</h3>
            <p>{{ service.description }}</p>
          </div>

          <el-divider />

          <div class="service-features">
            <h3>服务特色</h3>
            <div class="features-grid">
              <div v-for="feature in service.features" :key="feature" class="feature-tag">
                ✓ {{ feature }}
              </div>
            </div>
          </div>

          <el-divider />

          <div class="service-facilities">
            <h3>配套设施</h3>
            <div class="facilities-grid">
              <div v-for="facility in service.facilities" :key="facility.name" class="facility-item">
              <img class="facility-icon" :src="facility.icon" :alt="facility.name" />
                <span class="facility-name">{{ facility.name }}</span>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="service-packages">
            <h3>套餐选择</h3>
            <div class="packages-list">
              <div
                v-for="pkg in service.packages"
                :key="pkg.id"
                :class="['package-item', { 'active': selectedPackage?.id === pkg.id }]"
                @click="selectedPackage = pkg"
              >
                <div class="package-header">
                  <h4>{{ pkg.name }}</h4>
                  <div class="package-price">
                    <span class="price-number">¥{{ pkg.price }}</span>
                    <span class="price-unit">/{{ pkg.unit }}</span>
                  </div>
                </div>
                <p class="package-desc">{{ pkg.description }}</p>
                <div class="package-includes">
                <span v-for="item in pkg.includes" :key="item" class="include-item">
                  #{{ item }}
                </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

      </el-col>

      <!-- 右侧：预订信息 -->
      <el-col :span="8" class="right-content">
        <div class="booking-card-sticky">
          <el-card class="booking-card">
            <h3 class="booking-title">预订信息</h3>
            
            <div v-if="selectedPackage" class="selected-package-info">
              <h4>{{ selectedPackage.name }}</h4>
              <div class="package-price">
                <span class="price-number">¥{{ selectedPackage.price }}</span>
                <span class="price-unit">/{{ selectedPackage.unit }}</span>
              </div>
            </div>
            <div v-else class="no-package-selected">
              <el-text type="warning">请先选择套餐</el-text>
            </div>

            <el-divider />

            <el-form :model="bookingForm" label-position="top" :disabled="!selectedPackage">

              <el-form-item label="人数">
                <el-input-number
                  v-model="bookingForm.quantity"
                  :min="1"
                  :max="20"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="联系人姓名">
                <el-input v-model="bookingForm.contactName" placeholder="请输入姓名" />
              </el-form-item>

              <el-form-item label="联系电话">
                <el-input v-model="bookingForm.contactPhone" placeholder="请输入手机号" />
              </el-form-item>

              <el-form-item label="特殊需求">
                <el-input
                  v-model="bookingForm.notes"
                  type="textarea"
                  :rows="3"
                  placeholder="如有特殊需求请备注"
                />
              </el-form-item>
            </el-form>

            <el-divider v-if="selectedPackage" />

            <CouponSelector
              v-if="selectedPackage"
              v-model="selectedCoupon"
              :order-amount="totalPrice"
              @change="handleCouponChange"
            />

            <div v-if="selectedPackage" class="total-price">
              <span>总计</span>
              <span class="total-amount">¥{{ finalPrice }}</span>
              <div v-if="selectedCoupon" class="coupon-discount">
                <span>已优惠</span>
                <span class="discount-amount">-¥{{ couponDiscount }}</span>
              </div>
            </div>

            <el-button
              type="default"
              size="large"
              class="booking-button ghost"
              :disabled="!selectedPackage"
              @click="handleBooking"
            >
              <el-icon><Calendar /></el-icon>
              立即预订
            </el-button>

            <div class="contact-info">
              <el-icon><Phone /></el-icon>
              <span class="contact-text">咨询电话：{{ service.contactPhone }}</span>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import BackButton from '@/components/BackButton.vue'
import CouponSelector from '@/components/CouponSelector.vue'
import {
  ArrowLeft,
  Location,
  Star,
  View,
  Calendar,
  Phone
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 服务详情数据
const service = ref({
  id: null,
  title: '',
  badge: '',
  location: '',
  rating: 0,
  views: 0,
  contactPhone: '',
  description: '',
  images: [],
  features: [],
  facilities: [],
  packages: [],
  reviews: []
})

// 选中的套餐
const selectedPackage = ref(null)

// 预订表单
const bookingForm = ref({
  quantity: 1,
  contactName: '',
  contactPhone: '',
  notes: ''
})

// 选中的优惠券
const selectedCoupon = ref(null)

// 计算总价
const totalPrice = computed(() => {
  if (!selectedPackage.value) return 0
  const qty = bookingForm.value.quantity || 1
  return selectedPackage.value.price * qty
})

// 计算优惠金额
const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  const discountValue = selectedCoupon.value.discountValue || selectedCoupon.value.amount || 0
  return Math.min(discountValue, totalPrice.value)
})

// 计算最终价格
const finalPrice = computed(() => {
  return Math.max(0, totalPrice.value - couponDiscount.value)
})

// 处理优惠券变化
const handleCouponChange = (coupon: any) => {
  selectedCoupon.value = coupon
}

// 禁用过去的日期
const disabledDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 处理预订
const handleBooking = async () => {
  if (!selectedPackage.value) {
    ElMessage.warning('请先选择套餐')
    return
  }
  if (!bookingForm.value.contactName) {
    ElMessage.warning('请输入联系人姓名')
    return
  }
  if (!bookingForm.value.contactPhone) {
    ElMessage.warning('请输入联系电话')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认预订 ${service.value.title}？\n套餐：${selectedPackage.value.name}\n人数：${bookingForm.value.quantity || 1}人\n${selectedCoupon.value ? `优惠券：${selectedCoupon.value.name || selectedCoupon.value.couponName}\n优惠：-¥${couponDiscount.value}\n` : ''}总计：¥${finalPrice.value}`,
      '确认预订',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const payload = {
      bookingType: 2,
      serviceId: service.value.id,
      serviceName: service.value.title,
      packageId: selectedPackage.value.id,
      packageName: selectedPackage.value.name,
      quantity: bookingForm.value.quantity || 1,
      contactName: bookingForm.value.contactName,
      contactPhone: bookingForm.value.contactPhone,
      notes: bookingForm.value.notes,
      totalAmount: finalPrice.value,
      unitPrice: selectedPackage.value.price,
      couponId: selectedCoupon.value?.id || null,
      couponDiscount: couponDiscount.value
    }

    const res = await request.post('/culture/booking', payload)
    if (res.code === 200) {
      ElMessage.success('预订成功！我们会尽快与您联系确认详情')
      setTimeout(() => {
        router.push('/home/user/culture/bookings')
      }, 1200)
    } else {
      ElMessage.error(res.message || '预订失败')
    }
  } catch {
    // 用户取消
  }
}

// 页面加载
onMounted(() => {
  const id = route.params.id
  loadDetail(id)
})

const loadDetail = async (id) => {
  try {
    const res = await request.get(`/culture/services/${id}`)
    if (res.code === 200 && res.data) {
      const data = res.data
      service.value = {
        id: data.id,
        title: data.title,
        badge: data.badge || '',
        location: data.location,
        rating: data.rating || 0,
        views: data.views || 0,
        contactPhone: data.contactPhone || '',
        description: data.description || '',
        images: normalizeArray(data.images),
        features: normalizeArray(data.features),
        facilities: normalizeArray(data.facilities),
        packages: Array.isArray(data.packages) ? data.packages : []
      }
      if (service.value.packages.length > 0) {
        selectedPackage.value = service.value.packages[0]
      }
    } else {
      ElMessage.error(res.message || '加载服务详情失败')
    }
  } catch (error) {
    console.error('加载服务详情失败', error)
    ElMessage.error('加载服务详情失败')
  }
}

const normalizeArray = (val) => {
  if (!val) return []
  if (Array.isArray(val)) return val
  try {
    const parsed = JSON.parse(val)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}
</script>

<style scoped lang="scss">
.service-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 0;
}

.left-content {
  padding: 24px 24px 24px 0;
}

.right-content {
  padding: 24px 0 24px 24px;
}

.detail-header {
  position: relative;
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;

  .el-carousel {
    border-radius: 12px;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.info-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }
}

.service-title {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
}

.service-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.service-intro,
.service-features,
.service-facilities,
.service-packages {
  margin-top: 24px;

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 16px 0;
  }

  p {
    font-size: 15px;
    line-height: 1.8;
    color: #606266;
    margin: 0;
  }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.feature-tag {
  padding: 10px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
  text-align: center;
}

.facilities-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;

  .facility-icon {
    width: 32px;
    height: 32px;
  }

  .facility-name {
    font-size: 13px;
    color: #606266;
  }
}

.packages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.package-item {
  padding: 20px;
  background: #f5f7fa;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #d1d5db;
    background: #f6f7fb;
  }

  &.active {
    border-color: #d1d5db;
    background: #f6f7fb;
  }
}

.package-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  h4 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
}

.package-price {
  display: flex;
  align-items: baseline;
  gap: 4px;

  .price-number {
    font-size: 24px;
    font-weight: 700;
    color: #f56c6c;
  }

  .price-unit {
    font-size: 14px;
    color: #909399;
  }
}

.package-desc {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
}

.package-includes {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.include-item {
  padding: 0;
  background: transparent;
  border-radius: 0;
  font-size: 13px;
  color: #374151;
  border: none;
  font-weight: 600;
}

.reviews-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    margin: 0;
  }
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-user {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .user-name {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }
}

.review-date {
  font-size: 12px;
  color: #909399;
}

.review-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin: 0;
}

.booking-card-sticky {
  position: sticky;
  top: 24px;
}

.booking-card {
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.booking-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
}

.selected-package-info {
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
  }
}

.no-package-selected {
  padding: 20px;
  text-align: center;
}

.total-price {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;

  > div:first-child {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .total-amount {
    font-size: 24px;
    color: #f56c6c;
  }

  .coupon-discount {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    color: #909399;
    padding-top: 8px;
    border-top: 1px solid #e4e7ed;

    .discount-amount {
      color: #67c23a;
      font-weight: 600;
    }
  }
}

.booking-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.booking-button.ghost {
  background: #ffffff;
  border: 1px solid #d1d5db;
  color: #374151;
  box-shadow: none;

  &:hover {
    background: #f3f4f6;
    border-color: #cbd5e1;
    color: #111827;
  }
}

.contact-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  background: #ecfdf3;
  border-radius: 8px;
  font-size: 13px;
  color: #16a34a;

  .contact-text {
    color: #15803d;
    font-weight: 600;
  }
}

@media (max-width: 768px) {
  .service-detail {
    padding: 12px;
  }

  .service-title {
    font-size: 24px;
  }

  .features-grid,
  .facilities-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .booking-card-sticky {
    position: static;
  }
}
</style>


