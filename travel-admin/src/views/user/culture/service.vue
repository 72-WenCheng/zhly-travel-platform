<template>
  <div class="service-detail">
    <!-- 返回按钮 -->
    <BackButton />

    <el-row :gutter="24">
      <!-- 左侧：服务详情 -->
      <el-col :span="16">
        <!-- 服务头图 -->
        <div class="detail-header">
          <el-carousel :interval="4000" arrow="hover" height="450px">
            <el-carousel-item v-for="(img, index) in service.images" :key="index">
              <img :src="img" :alt="service.title" />
            </el-carousel-item>
          </el-carousel>
          <div class="service-badge">{{ service.badge }}</div>
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
                <span class="facility-icon">{{ facility.icon }}</span>
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
                    {{ item }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 评论区 -->
        <el-card class="reviews-card">
          <div class="reviews-header">
            <h3>用户评价 ({{ service.reviews.length }})</h3>
            <div class="rating-summary">
              <el-rate v-model="service.rating" disabled show-score />
            </div>
          </div>
          
          <div class="reviews-list">
            <div v-for="review in service.reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <el-avatar :src="review.userAvatar">{{ review.userName.charAt(0) }}</el-avatar>
                <div class="review-user">
                  <span class="user-name">{{ review.userName }}</span>
                  <el-rate v-model="review.rating" disabled size="small" />
                </div>
                <span class="review-date">{{ review.date }}</span>
              </div>
              <p class="review-content">{{ review.content }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：预订信息 -->
      <el-col :span="8">
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
              <el-form-item label="入住日期">
                <el-date-picker
                  v-model="bookingForm.checkInDate"
                  type="date"
                  placeholder="选择日期"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="入住天数">
                <el-input-number
                  v-model="bookingForm.days"
                  :min="1"
                  :max="30"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="房间数/人数">
                <el-input-number
                  v-model="bookingForm.quantity"
                  :min="1"
                  :max="10"
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

            <div v-if="selectedPackage" class="total-price">
              <span>总计</span>
              <span class="total-amount">¥{{ totalPrice }}</span>
            </div>

            <el-button
              type="primary"
              size="large"
              class="booking-button"
              :disabled="!selectedPackage"
              @click="handleBooking"
            >
              <el-icon><Calendar /></el-icon>
              立即预订
            </el-button>

            <div class="contact-info">
              <el-icon><Phone /></el-icon>
              <span>咨询电话：{{ service.contactPhone }}</span>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
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
  id: 1,
  title: '云雾山庄农家乐',
  badge: '政府推荐',
  location: '重庆市巴南区云雾山',
  rating: 4.8,
  views: 1234,
  contactPhone: '023-6688-1122',
  description: '云雾山庄位于巴南区云雾山脚下，环境优美，空气清新。我们提供正宗的农家菜、采摘体验、垂钓娱乐等服务。这里远离城市喧嚣，是周末休闲、家庭聚会的理想之地。我们的食材全部来自自家农场，绿色健康，让您享受真正的田园生活。',
  images: [
    'https://picsum.photos/800/450?random=10',
    'https://picsum.photos/800/450?random=11',
    'https://picsum.photos/800/450?random=12',
    'https://picsum.photos/800/450?random=13'
  ],
  features: [
    '采摘体验',
    '农家餐饮',
    '垂钓',
    '烧烤',
    '儿童游乐区',
    '停车场',
    '免费WiFi',
    '宠物友好'
  ],
  facilities: [
    { icon: '🅿️', name: '免费停车' },
    { icon: '📶', name: '免费WiFi' },
    { icon: '🍽️', name: '农家餐厅' },
    { icon: '🐟', name: '垂钓池塘' },
    { icon: '🌳', name: '采摘果园' },
    { icon: '🏡', name: '休息凉亭' },
    { icon: '🎮', name: '儿童乐园' },
    { icon: '🐶', name: '宠物友好' }
  ],
  packages: [
    {
      id: 1,
      name: '基础套餐',
      price: 88,
      unit: '人',
      description: '包含农家午餐，采摘体验1小时',
      includes: ['农家午餐', '采摘体验1小时', '茶水饮料']
    },
    {
      id: 2,
      name: '标准套餐',
      price: 128,
      unit: '人',
      description: '包含午餐+晚餐，采摘+垂钓体验',
      includes: ['农家午餐', '农家晚餐', '采摘体验2小时', '垂钓体验2小时', '烧烤食材']
    },
    {
      id: 3,
      name: '豪华套餐',
      price: 198,
      unit: '人',
      description: '包含三餐，全天体验，住宿一晚',
      includes: ['三餐', '采摘不限时', '垂钓不限时', '烧烤套餐', '住宿一晚', '早餐']
    }
  ],
  reviews: [
    {
      id: 1,
      userName: '周末游玩',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=11',
      rating: 5,
      date: '2024-10-21',
      content: '非常棒的农家乐！环境优美，空气清新。农家菜味道正宗，孩子们在果园玩得很开心。老板热情好客，下次还会再来！'
    },
    {
      id: 2,
      userName: '家庭聚会',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=12',
      rating: 5,
      date: '2024-10-19',
      content: '带着全家老小来的，大家都很满意。停车方便，设施齐全，食材新鲜。特别推荐烤全羊，太美味了！'
    },
    {
      id: 3,
      userName: '公司团建',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=13',
      rating: 4,
      date: '2024-10-15',
      content: '公司团建选的这里，同事们都玩得很开心。采摘、垂钓、烧烤一条龙，很适合团建活动。性价比高！'
    }
  ]
})

// 选中的套餐
const selectedPackage = ref(null)

// 预订表单
const bookingForm = ref({
  checkInDate: '',
  days: 1,
  quantity: 1,
  contactName: '',
  contactPhone: '',
  notes: ''
})

// 计算总价
const totalPrice = computed(() => {
  if (!selectedPackage.value) return 0
  return selectedPackage.value.price * bookingForm.value.quantity * bookingForm.value.days
})

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
  if (!bookingForm.value.checkInDate) {
    ElMessage.warning('请选择入住日期')
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
      `确认预订 ${service.value.title}？\n套餐：${selectedPackage.value.name}\n日期：${bookingForm.value.checkInDate}\n天数：${bookingForm.value.days}天\n数量：${bookingForm.value.quantity}\n总计：¥${totalPrice.value}`,
      '确认预订',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // TODO: 调用后端API提交预订
    console.log('预订信息:', {
      serviceId: service.value.id,
      serviceName: service.value.title,
      package: selectedPackage.value,
      ...bookingForm.value,
      totalPrice: totalPrice.value
    })

    ElMessage.success('预订成功！我们会尽快与您联系确认详情')
    
    // 2秒后跳转到预订列表
    setTimeout(() => {
      router.push('/home/user/culture/bookings')
    }, 2000)
  } catch {
    // 用户取消
  }
}

// 页面加载
onMounted(() => {
  const id = route.params.id
  console.log('加载服务详情，ID:', id)
  // TODO: 根据ID从后端加载实际数据
  
  // 默认选中第一个套餐
  if (service.value.packages.length > 0) {
    selectedPackage.value = service.value.packages[0]
  }
})
</script>

<style scoped lang="scss">
.service-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
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

  .service-badge {
    position: absolute;
    top: 20px;
    left: 20px;
    padding: 8px 16px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    font-size: 14px;
    font-weight: 600;
    color: #667eea;
    z-index: 10;
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
    font-size: 32px;
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
    border-color: #409eff;
    background: #ecf5ff;
  }

  &.active {
    border-color: #409eff;
    background: #ecf5ff;
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
  padding: 4px 12px;
  background: white;
  border-radius: 12px;
  font-size: 12px;
  color: #67c23a;
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
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;

  .total-amount {
    font-size: 24px;
    color: #f56c6c;
  }
}

.booking-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.contact-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  background: #ecf5ff;
  border-radius: 8px;
  font-size: 13px;
  color: #409eff;
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


