<template>
  <div class="culture-detail">
    <!-- 返回按钮 -->
    <BackButton />

    <el-row :gutter="24">
      <!-- 左侧：项目详情 -->
      <el-col :span="16">
        <!-- 项目头图 -->
        <div class="detail-header">
          <div class="header-image">
            <img :src="project.coverImage" :alt="project.name" />
            <div class="header-badge">{{ project.categoryName }}</div>
          </div>
        </div>

        <!-- 项目信息 -->
        <el-card class="info-card">
          <h1 class="project-title">{{ project.name }}</h1>
          <div class="project-meta">
            <span class="meta-item">
              <el-icon><Location /></el-icon>
              {{ project.location }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ project.duration }}
            </span>
            <span class="meta-item">
              <el-icon><User /></el-icon>
              适合{{ project.suitableFor }}
            </span>
          </div>

          <el-divider />

          <div class="project-intro">
            <h3>项目介绍</h3>
            <p>{{ project.description }}</p>
          </div>

          <el-divider />

          <div class="project-features">
            <h3>体验特色</h3>
            <div class="features-grid">
              <div v-for="feature in project.features" :key="feature.title" class="feature-item">
                <div class="feature-icon">{{ feature.emoji }}</div>
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.description }}</p>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="project-schedule">
            <h3>活动流程</h3>
            <el-timeline>
              <el-timeline-item
                v-for="(step, index) in project.schedule"
                :key="index"
                :timestamp="step.time"
                placement="top"
              >
                <h4>{{ step.title }}</h4>
                <p>{{ step.description }}</p>
              </el-timeline-item>
            </el-timeline>
          </div>

          <el-divider />

          <div class="project-includes">
            <h3>费用包含</h3>
            <ul class="includes-list">
              <li v-for="item in project.includes" :key="item">
                <el-icon color="#67c23a"><SuccessFilled /></el-icon>
                <span>{{ item }}</span>
              </li>
            </ul>
          </div>

          <el-divider />

          <div class="project-notes">
            <h3>注意事项</h3>
            <ul class="notes-list">
              <li v-for="note in project.notes" :key="note">
                <el-icon color="#e6a23c"><WarningFilled /></el-icon>
                <span>{{ note }}</span>
              </li>
            </ul>
          </div>
        </el-card>

        <!-- 评论区 -->
        <el-card class="reviews-card">
          <div class="reviews-header">
            <h3>用户评价 ({{ project.reviewCount }})</h3>
            <div class="rating-summary">
              <el-rate v-model="project.rating" disabled show-score />
            </div>
          </div>
          
          <div class="reviews-list">
            <div v-for="review in project.reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <el-avatar :src="review.userAvatar">{{ review.userName.charAt(0) }}</el-avatar>
                <div class="review-user">
                  <span class="user-name">{{ review.userName }}</span>
                  <el-rate v-model="review.rating" disabled size="small" />
                </div>
                <span class="review-date">{{ review.date }}</span>
              </div>
              <p class="review-content">{{ review.content }}</p>
              <div v-if="review.images && review.images.length" class="review-images">
                <img v-for="(img, idx) in review.images" :key="idx" :src="img" alt="评价图片" />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：预订信息 -->
      <el-col :span="8">
        <div class="booking-card-sticky">
          <el-card class="booking-card">
            <h3 class="booking-title">预订信息</h3>
            
            <div class="price-section">
              <span class="price-label">价格</span>
              <div class="price-value">
                <span class="price-number">¥{{ project.price }}</span>
                <span class="price-unit">/人</span>
              </div>
            </div>

            <el-divider />

            <el-form :model="bookingForm" label-position="top">
              <el-form-item label="预订日期">
                <el-date-picker
                  v-model="bookingForm.date"
                  type="date"
                  placeholder="选择日期"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="预订时段">
                <el-select v-model="bookingForm.timeSlot" placeholder="选择时段" style="width: 100%">
                  <el-option label="上午场 (9:00-12:00)" value="morning" />
                  <el-option label="下午场 (14:00-17:00)" value="afternoon" />
                  <el-option label="全天 (9:00-17:00)" value="fullday" />
                </el-select>
              </el-form-item>

              <el-form-item label="参与人数">
                <el-input-number
                  v-model="bookingForm.participants"
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

            <div class="total-price">
              <span>总计</span>
              <span class="total-amount">¥{{ totalPrice }}</span>
            </div>

            <el-button type="primary" size="large" class="booking-button" @click="handleBooking">
              <el-icon><Calendar /></el-icon>
              立即预订
            </el-button>

            <div class="contact-info">
              <el-icon><Phone /></el-icon>
              <span>咨询电话：{{ project.contactPhone }}</span>
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
import * as cultureExperienceApi from '@/api/cultureExperience'
import request from '@/utils/request'
import {
  ArrowLeft,
  Location,
  Clock,
  User,
  SuccessFilled,
  WarningFilled,
  Calendar,
  Phone
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 项目详情数据
const project = ref({
  id: null,
  name: '',
  categoryName: '',
  coverImage: '',
  location: '',
  duration: '',
  suitableFor: '亲子、团建、文化爱好者',
  price: 0,
  rating: 5.0,
  reviewCount: 0,
  contactPhone: '023-6688-9900',
  description: '',
  features: [],
  schedule: [],
  includes: [],
  notes: [],
  reviews: []
})

// 加载状态
const loading = ref(true)

// 预订表单
const bookingForm = ref({
  date: '',
  timeSlot: '',
  participants: 1,
  contactName: '',
  contactPhone: '',
  notes: ''
})

// 计算总价
const totalPrice = computed(() => {
  return project.value.price * bookingForm.value.participants
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
  if (!bookingForm.value.date) {
    ElMessage.warning('请选择预订日期')
    return
  }
  if (!bookingForm.value.timeSlot) {
    ElMessage.warning('请选择预订时段')
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
      `确认预订 ${project.value.name}？\n日期：${bookingForm.value.date}\n时段：${getTimeSlotText(bookingForm.value.timeSlot)}\n人数：${bookingForm.value.participants}人\n总计：¥${totalPrice.value}`,
      '确认预订',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 调用后端API提交预订
    const bookingData = {
      experienceId: project.value.id,
      experienceName: project.value.name,
      date: bookingForm.value.date,
      timeSlot: bookingForm.value.timeSlot,
      participants: bookingForm.value.participants,
      contactName: bookingForm.value.contactName,
      contactPhone: bookingForm.value.contactPhone,
      notes: bookingForm.value.notes,
      totalAmount: totalPrice.value,
      type: 'experience'
    }

    const res = await request.post('/culture/booking', bookingData)
    if (res.code === 200) {
      ElMessage.success('预订成功！我们会尽快与您联系确认详情')
      
      // 更新预订量
      await cultureExperienceApi.getUserExperienceById(project.value.id)
      
      // 2秒后跳转到预订列表
      setTimeout(() => {
        router.push('/home/user/culture/bookings')
      }, 2000)
    } else {
      ElMessage.error(res.message || '预订失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('预订失败: ' + (error.message || '未知错误'))
    }
  }
}

// 获取时段文本
const getTimeSlotText = (slot) => {
  const map = {
    morning: '上午场 (9:00-12:00)',
    afternoon: '下午场 (14:00-17:00)',
    fullday: '全天 (9:00-17:00)'
  }
  return map[slot] || ''
}

// 加载体验详情
const loadExperienceDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('体验ID不存在')
    router.back()
    return
  }

  try {
    loading.value = true
    const res = await cultureExperienceApi.getUserExperienceById(Number(id))
    if (res.code === 200) {
      const data = res.data
      project.value = {
        id: data.id,
        name: data.name,
        categoryName: data.categoryName || '文化体验',
        coverImage: data.images ? (Array.isArray(JSON.parse(data.images)) ? JSON.parse(data.images)[0] : data.images) : '',
        location: data.location,
        duration: data.duration || '约2小时',
        suitableFor: '亲子、团建、文化爱好者',
        price: data.price ? Number(data.price) : 0,
        rating: data.rating ? Number(data.rating) : 5.0,
        reviewCount: 0,
        contactPhone: '023-6688-9900',
        description: data.description || '',
        features: [
          {
            emoji: '🎨',
            title: '文化体验',
            description: '深度体验传统文化魅力'
          },
          {
            emoji: '📚',
            title: '专业指导',
            description: '专业老师全程指导'
          },
          {
            emoji: '🎁',
            title: '精美纪念',
            description: '完成作品可带走'
          }
        ],
        schedule: data.flow ? (Array.isArray(JSON.parse(data.flow)) ? JSON.parse(data.flow).map((item, index) => ({
          time: `${9 + index}:00`,
          title: typeof item === 'string' ? item : item.title || `步骤${index + 1}`,
          description: typeof item === 'string' ? item : item.description || ''
        })) : []) : [],
        includes: [
          '专业老师全程指导',
          '所有体验材料和工具',
          '完成作品可带走'
        ],
        notes: data.notes ? (Array.isArray(JSON.parse(data.notes)) ? JSON.parse(data.notes) : []) : [
          '请提前15分钟到达集合地点',
          '体验过程中需保持安静',
          '儿童需在家长陪同下参与'
        ],
        reviews: data.reviews ? (Array.isArray(JSON.parse(data.reviews)) ? JSON.parse(data.reviews) : []) : []
      }
    } else {
      ElMessage.error(res.message || '加载体验详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载体验详情失败:', error)
    ElMessage.error('加载体验详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 页面加载
onMounted(() => {
  loadExperienceDetail()
})
</script>

<style scoped lang="scss">
.culture-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.detail-header {
  margin-bottom: 20px;
}

.header-image {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .header-badge {
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
  }
}

.info-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }
}

.project-title {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
}

.project-meta {
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

.project-intro,
.project-features,
.project-schedule,
.project-includes,
.project-notes {
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
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.feature-item {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
  text-align: center;

  .feature-icon {
    font-size: 40px;
    margin-bottom: 12px;
  }

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 13px;
    color: #909399;
    margin: 0;
  }
}

.includes-list,
.notes-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    padding: 8px 0;
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }
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
  margin: 0 0 12px 0;
}

.review-images {
  display: flex;
  gap: 8px;

  img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
  }
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

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .price-label {
    font-size: 14px;
    color: #909399;
  }

  .price-value {
    display: flex;
    align-items: baseline;
    gap: 4px;

    .price-number {
      font-size: 32px;
      font-weight: 700;
      color: #f56c6c;
    }

    .price-unit {
      font-size: 14px;
      color: #909399;
    }
  }
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
  .culture-detail {
    padding: 12px;
  }

  .header-image {
    height: 250px;
  }

  .project-title {
    font-size: 24px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .booking-card-sticky {
    position: static;
  }
}
</style>
