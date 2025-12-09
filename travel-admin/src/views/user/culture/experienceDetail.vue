<template>
  <div class="experience-detail-page">
    <BackButton />

    <div class="hero" v-if="experience.coverImage">
      <img :src="experience.coverImage" :alt="experience.name" />
      <div class="hero-mask"></div>
      <div class="hero-content">
        <div class="badge-row">
          <div class="rating-chip">
            <el-icon><StarFilled /></el-icon>
            <span>{{ experience.rating.toFixed ? experience.rating.toFixed(1) : experience.rating }}</span>
          </div>
        </div>
        <h1 class="hero-title">{{ experience.name }}</h1>
        <p class="hero-subtitle">{{ experience.slogan }}</p>
        <div class="hero-meta">
          <span>
            <el-icon><Location /></el-icon>
            {{ experience.location }}
          </span>
          <span>
            <el-icon><Clock /></el-icon>
            {{ experience.duration }}
          </span>
          <span class="meta-item">
            <el-icon><User /></el-icon>
            适合 {{ experience.suitableFor }}
          </span>
        </div>
        <div class="hero-bottom">
          <div class="hero-price">
            <span class="currency">¥</span>
            <span class="number">{{ experience.price }}</span>
            <span class="unit">/人</span>
          </div>
          <el-tag :type="experience.status === 'hot' ? 'danger' : 'success'" class="status-tag" size="large">
            {{ experience.statusText }}
          </el-tag>
        </div>
      </div>
    </div>

    <el-row :gutter="24">
      <el-col :span="16">
        <el-card class="section-card">
          <div class="section-header">
            <div>
              <p class="eyebrow">体验亮点</p>
              <h2 class="section-title">{{ experience.name }}</h2>
              <p class="section-desc">{{ experience.description }}</p>
            </div>
          </div>

          <div class="highlight-grid">
            <div v-for="(item, index) in experience.highlights" :key="item.title" class="highlight-card">
              <div class="highlight-icon">
                <el-icon :size="20">
                  <component :is="getHighlightIcon(index)" />
                </el-icon>
              </div>
              <div>
                <h4>{{ item.title }}</h4>
                <p>{{ item.description }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="section-card">
          <div class="section-header">
            <p class="eyebrow">行程安排</p>
            <h3 class="section-title">沉浸式体验流程</h3>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in experience.schedule"
              :key="index"
              :timestamp="step.time"
              placement="top"
            >
              <h4>{{ step.title }}</h4>
              <p>{{ step.description }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <el-card class="section-card">
          <div class="two-column">
            <div>
              <p class="eyebrow">费用包含</p>
              <ul class="info-list">
                <li v-for="item in experience.includes" :key="item">
                  <el-icon color="#67c23a"><CircleCheck /></el-icon>
                  <span>{{ item }}</span>
                </li>
              </ul>
            </div>
            <div>
              <p class="eyebrow">体验提示</p>
              <ul class="info-list">
                <li v-for="item in experience.preparations" :key="item">
                  <el-icon color="#e6a23c"><WarningFilled /></el-icon>
                  <span>{{ item }}</span>
                </li>
              </ul>
            </div>
          </div>
        </el-card>

        <el-card class="section-card" v-if="experience.gallery.length">
          <div class="section-header">
            <p class="eyebrow">现场一瞥</p>
            <h3 class="section-title">体验场景</h3>
          </div>
          <div class="gallery-grid">
            <el-image v-for="(img, index) in experience.gallery" :key="index" :src="img" fit="cover" />
          </div>
        </el-card>

      </el-col>

      <el-col :span="8">
        <div class="booking-sticky">
          <el-card class="booking-card">
            <div class="booking-header">
              <div>
                <p class="eyebrow">预约体验</p>
                <h3>{{ experience.name }}</h3>
              </div>
              <div class="price-block">
                <span class="currency">¥</span>
                <span class="number">{{ experience.price }}</span>
                <span class="unit">/人</span>
              </div>
            </div>

            <el-form :model="bookingForm" label-position="top">
              <el-form-item label="体验日期">
                <el-date-picker
                  v-model="bookingForm.date"
                  type="date"
                  placeholder="选择日期"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="体验场次">
                <el-select v-model="bookingForm.timeSlot" placeholder="选择场次" style="width: 100%">
                  <el-option label="上午场 (9:00-12:00)" value="morning" />
                  <el-option label="下午场 (14:00-17:00)" value="afternoon" />
                  <el-option label="夜场 (18:00-21:00)" value="night" />
                </el-select>
              </el-form-item>

              <el-form-item label="参与人数">
                <el-input-number v-model="bookingForm.participants" :min="1" :max="30" style="width: 100%" />
              </el-form-item>

              <el-form-item label="联系人">
                <el-input v-model="bookingForm.contactName" placeholder="请输入姓名" />
              </el-form-item>

              <el-form-item label="联系电话">
                <el-input v-model="bookingForm.contactPhone" placeholder="请输入手机号" />
              </el-form-item>

              <el-form-item label="备注">
                <el-input
                  v-model="bookingForm.notes"
                  type="textarea"
                  :rows="2"
                  placeholder="如有定制需求请备注"
                />
              </el-form-item>
            </el-form>

            <div class="total-box">
              <div class="total-row">
                <span>合计</span>
                <span class="amount">¥{{ totalPrice }}</span>
              </div>
              <p class="tips">下单后客服将与您确认具体时间与人数</p>
            </div>

            <el-button type="primary" size="large" class="booking-btn" @click="handleBooking">
              <el-icon><Calendar /></el-icon>
              立即预约
            </el-button>

            <div class="contact-box">
              <el-icon><Phone /></el-icon>
              <span>咨询：{{ experience.contactPhone }}</span>
            </div>
          </el-card>

          <el-card class="host-card">
            <div class="host-head">
              <el-avatar :size="50" :src="experience.host.avatar">{{ experience.host.name?.charAt(0) }}</el-avatar>
              <div>
                <h4>{{ experience.host.name }}</h4>
                <p>{{ experience.host.title }}</p>
              </div>
            </div>
            <p class="host-bio">{{ experience.host.bio }}</p>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import * as cultureExperienceApi from '@/api/cultureExperience'
import {
  Calendar,
  CircleCheck,
  Clock,
  Location,
  Phone,
  StarFilled,
  User,
  WarningFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const experience = ref({
  id: null,
  name: '蜀绣体验工坊',
  categoryName: '文化体验',
  coverImage: 'https://picsum.photos/1200/640?random=888',
  location: '成都 · 锦里',
  duration: '约2小时',
  price: 168,
  rating: 4.8,
  reviewCount: 86,
  status: 'hot',
  statusText: '热门抢订中',
  suitableFor: '亲子/团建/非遗爱好者',
  slogan: '跟着非遗老师亲手完成一件蜀绣作品，收藏一段东方美学',
  tags: ['非遗工坊', '手作体验', '沉浸式'],
  highlights: [
    { emoji: '🧵', title: '师傅一对一指导', description: '非遗传承人现场教学，零基础也能上手' },
    { emoji: '🎨', title: '精品材料包', description: '提供配色丝线与绣框，可带走成品' },
    { emoji: '🏮', title: '沉浸式空间', description: '古风场景+茶点，沉浸式感受蜀绣美学' }
  ],
  description: '通过专业老师的带领，从纹样认识、针法练习到完成成品，全流程沉浸式体验蜀绣魅力。',
  schedule: [
    { time: '00:00', title: '集合签到', description: '老师介绍当日体验内容与注意事项' },
    { time: '00:20', title: '文化讲解', description: '了解蜀绣历史、纹样寓意与色彩故事' },
    { time: '00:50', title: '针法练习', description: '手把手示范基础针法，练习手感' },
    { time: '01:20', title: '创作成品', description: '完成主题绣片，可选择装框或做徽章' }
  ],
  includes: ['全套材料工具', '茶歇/软饮', '场地与讲解'],
  preparations: ['请提前15分钟到场', '穿着舒适便于活动的衣物', '如需团建可提前沟通定制'],
  gallery: [
    'https://picsum.photos/500/340?random=901',
    'https://picsum.photos/500/340?random=902',
    'https://picsum.photos/500/340?random=903'
  ],
  reviews: [
    {
      id: 1,
      userName: '山城织女',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=su1',
      rating: 5,
      date: '2024-10-02',
      content: '老师很专业，流程也安排得很细致，成品带回家很有纪念意义。'
    }
  ],
  contactPhone: '023-6688-9900',
  host: {
    name: '林老师',
    title: '省级非遗传承人 · 蜀绣导师',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=master',
    bio: '从业20年，擅长将传统纹样与当代设计结合，带领学员完成富有东方意境的绣品。'
  }
})

const bookingForm = ref({
  date: '',
  timeSlot: '',
  participants: 2,
  contactName: '',
  contactPhone: '',
  notes: ''
})

const totalPrice = computed(() => Math.max(bookingForm.value.participants || 1, 1) * (experience.value.price || 0))

const disabledDate = (date) => date < new Date(new Date().setHours(0, 0, 0, 0))

const highlightIcons = [StarFilled, CircleCheck, Clock, WarningFilled]
const getHighlightIcon = (index) => highlightIcons[index % highlightIcons.length]

const mapTimeSlot = (slot) => {
  const map = {
    morning: '上午场 (9:00-12:00)',
    afternoon: '下午场 (14:00-17:00)',
    night: '夜场 (18:00-21:00)'
  }
  return map[slot] || ''
}

const handleBooking = async () => {
  if (!bookingForm.value.date) return ElMessage.warning('请选择体验日期')
  if (!bookingForm.value.timeSlot) return ElMessage.warning('请选择体验场次')
  if (!bookingForm.value.contactName) return ElMessage.warning('请输入联系人')
  if (!bookingForm.value.contactPhone) return ElMessage.warning('请输入联系电话')

  try {
    await ElMessageBox.confirm(
      `确认预约「${experience.value.name}」？\n日期：${bookingForm.value.date}\n场次：${mapTimeSlot(bookingForm.value.timeSlot)}\n人数：${bookingForm.value.participants}人\n合计：¥${totalPrice.value}`,
      '确认预约',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'info' }
    )

    const payload = {
      experienceId: experience.value.id,
      experienceName: experience.value.name,
      date: bookingForm.value.date,
      timeSlot: bookingForm.value.timeSlot,
      participants: bookingForm.value.participants,
      contactName: bookingForm.value.contactName,
      contactPhone: bookingForm.value.contactPhone,
      notes: bookingForm.value.notes,
      totalAmount: totalPrice.value,
      type: 'experience'
    }

    console.log('提交文化体验预约', payload)
    ElMessage.success('预约提交成功，我们将尽快与您确认')
    setTimeout(() => router.push('/home/user/culture/bookings'), 1200)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '预约失败，请稍后再试')
    }
  }
}


const normalizeImages = (images) => {
  if (!images) return []
  try {
    const parsed = Array.isArray(images) ? images : JSON.parse(images)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return typeof images === 'string' ? [images] : []
  }
}

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return

  try {
    const res = await cultureExperienceApi.getUserExperienceById(Number(id))
    if (res.code === 200) {
      const data = res.data
      const images = normalizeImages(data.images)
      const flow = normalizeImages(data.flow).map((item, index) => {
        const entry = typeof item === 'string' ? { title: item, description: '' } : item
        return {
          time: `${9 + index}:00`,
          title: entry.title || `步骤${index + 1}`,
          description: entry.description || ''
        }
      })

      experience.value = {
        ...experience.value,
        id: data.id,
        name: data.name || experience.value.name,
        categoryName: data.categoryName || '文化体验',
        coverImage: images[0] || experience.value.coverImage,
        location: data.location || experience.value.location,
        duration: data.duration || experience.value.duration,
        price: Number(data.price) || experience.value.price,
        rating: Number(data.rating) || experience.value.rating,
        reviewCount: data.reviewCount || experience.value.reviewCount,
        status: data.status || 'featured',
        statusText: data.status === 'hot' ? '热门抢订中' : '精选推荐',
        suitableFor: data.suitableFor || experience.value.suitableFor,
        slogan: data.slogan || experience.value.slogan,
        tags: data.tags ? normalizeImages(data.tags) : experience.value.tags,
        description: data.description || experience.value.description,
        highlights: data.features
          ? normalizeImages(data.features).map((item) =>
              typeof item === 'string'
                ? { emoji: '✨', title: item, description: '' }
                : { emoji: item.emoji || '✨', title: item.title, description: item.description || '' }
            )
          : experience.value.highlights,
        schedule: flow.length ? flow : experience.value.schedule,
        includes: data.includes ? normalizeImages(data.includes) : experience.value.includes,
        preparations: data.notes ? normalizeImages(data.notes) : experience.value.preparations,
        gallery: images.length ? images : experience.value.gallery,
        reviews: data.reviews ? normalizeImages(data.reviews) : experience.value.reviews,
        contactPhone: data.contactPhone || experience.value.contactPhone,
        host: experience.value.host
      }
    } else {
      ElMessage.error(res.message || '加载体验详情失败')
    }
  } catch (error) {
    console.error('加载文化体验详情失败', error)
    ElMessage.error('加载体验详情失败')
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.experience-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.hero {
  position: relative;
  height: 420px;
  border-radius: 18px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(0, 0, 0, 0.55), rgba(0, 0, 0, 0.25));
}

.hero-content {
  position: absolute;
  inset: 0;
  padding: 32px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 12px;
}

.badge-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.25);
  font-weight: 600;
}

.hero-title {
  margin: 0;
  font-size: 32px;
  font-weight: 800;
  letter-spacing: 1px;
}

.hero-subtitle {
  margin: 0;
  font-size: 15px;
  opacity: 0.92;
}

.hero-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;

  .meta-item {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
  }
}

.hero-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.hero-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
  font-weight: 800;
  color: #fff;

  .currency {
    font-size: 18px;
  }

  .number {
    font-size: 42px;
  }

  .unit {
    font-size: 14px;
    opacity: 0.8;
  }
}

.status-tag {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.section-card {
  margin-bottom: 18px;

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.eyebrow {
  margin: 0 0 6px 0;
  font-size: 12px;
  letter-spacing: 1px;
  color: #909399;
  text-transform: uppercase;
}

.section-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.section-desc {
  margin: 8px 0 0;
  color: #606266;
  line-height: 1.7;
}

.pill-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.pill {
  background: #f0f2ff;
  color: #667eea;
  padding: 6px 12px;
  border-radius: 14px;
  font-weight: 600;
  font-size: 12px;
}

.highlight-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.highlight-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 14px;
  background: #f7f9fc;
}

.highlight-icon {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: linear-gradient(135deg, #ecf2ff, #f3f7ff);
  font-size: 20px;
}

.highlight-card h4 {
  margin: 0 0 6px;
  font-size: 16px;
  color: #303133;
}

.highlight-card p {
  margin: 0;
  color: #606266;
}

.two-column {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 12px 0 0;
  display: flex;
  flex-direction: column;
  gap: 10px;

  li {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #606266;
    line-height: 1.5;

    .el-icon {
      line-height: 1;
      display: inline-flex;
      align-items: center;
    }
  }
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 14px;
  margin-top: 12px;

  :deep(.el-image) {
    width: 100%;
    height: 160px;
    border-radius: 12px;
    overflow: hidden;
  }
}

.booking-sticky {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.booking-card :deep(.el-card__body) {
  padding: 20px;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;

  h3 {
    margin: 0;
    font-size: 20px;
    color: #303133;
  }
}

.price-block {
  display: flex;
  align-items: baseline;
  gap: 4px;
  color: #f56c6c;
  font-weight: 800;

  .currency {
    font-size: 14px;
  }

  .number {
    font-size: 28px;
  }

  .unit {
    font-size: 12px;
    color: #909399;
    font-weight: 500;
  }
}

.total-box {
  padding: 12px;
  background: #f7f9fc;
  border-radius: 10px;
  margin-top: 12px;
}

.total-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 700;
  color: #303133;
}

.amount {
  font-size: 22px;
  color: #f56c6c;
}

.tips {
  margin: 6px 0 0;
  font-size: 12px;
  color: #909399;
}

.booking-btn {
  width: 100%;
  margin-top: 12px;
}

.contact-box {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #ecf5ff;
  border-radius: 10px;
  color: #409eff;
  font-size: 13px;
}

.host-card :deep(.el-card__body) {
  padding: 16px;
}

.host-head {
  display: flex;
  align-items: center;
  gap: 12px;

  h4 {
    margin: 0;
    font-size: 16px;
    color: #303133;
  }

  p {
    margin: 4px 0 0;
    color: #909399;
    font-size: 13px;
  }
}

.host-bio {
  margin: 10px 0 0;
  color: #606266;
  line-height: 1.6;
}

@media (max-width: 960px) {
  .experience-detail-page {
    padding: 12px;
  }

  .hero {
    height: 320px;
  }

  .hero-title {
    font-size: 26px;
  }

  .hero-content {
    padding: 20px;
  }
}
</style>

