<template>
  <div class="homestay-detail">
    <BackButton />

    <div class="layout">
      <div class="left">
        <section class="hero-card">
          <div class="hero-left">
            <img :src="homestay.cover" :alt="homestay.title" class="hero-img" />
          </div>
          <div class="hero-right">
            <h1 class="title">{{ homestay.title }}</h1>
            <p class="sub-title">{{ homestay.location }}</p>
            <div class="meta">
              <span>⭐ {{ homestay.rating }}</span>
              <span>·</span>
              <span>{{ homestay.views }}次浏览</span>
            </div>
            <p class="summary">{{ homestay.summary }}</p>
            <div class="price-block">
              <span class="price">¥{{ homestay.price }}</span>
              <span class="unit">/晚</span>
            </div>
          </div>
        </section>

        <section class="info-grid">
          <div class="info-card">
            <h3>基础信息</h3>
            <ul>
              <li><strong>地址：</strong>{{ homestay.location }}</li>
              <li><strong>房型：</strong>{{ homestay.roomType }}</li>
              <li><strong>可住人数：</strong>{{ homestay.capacity }}人</li>
              <li><strong>联系电话：</strong>{{ homestay.contactPhone }}</li>
              <li><strong>配套：</strong>{{ homestay.amenities.join(' / ') }}</li>
            </ul>
          </div>
          <div class="info-card">
            <h3>亮点</h3>
            <p>{{ homestay.highlights }}</p>
            <div class="chips">
              <span v-for="item in homestay.highlightTags" :key="item" class="chip">#{{ item }}</span>
            </div>
          </div>
        </section>
      </div>

      <div class="right">
        <section class="booking-card">
          <h3>预订信息</h3>
          <el-form :model="form" label-position="top">
            <el-form-item label="入住日期">
              <el-date-picker
                v-model="form.date"
                type="date"
                placeholder="选择日期"
                :disabled-date="disablePast"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="入住晚数">
              <el-input-number v-model="form.nights" :min="1" :max="30" style="width: 100%" />
            </el-form-item>
            <el-form-item label="联系人姓名">
              <el-input v-model="form.name" placeholder="请输入联系人姓名" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input
                v-model="form.notes"
                type="textarea"
                :rows="3"
                placeholder="如有特殊需求请备注"
              />
            </el-form-item>
          </el-form>

          <div class="total">
            <span>总计</span>
            <span class="amount">¥{{ totalPrice }}</span>
          </div>

          <el-button class="book-btn" type="primary" size="large" @click="handleBook">
            立即预订
          </el-button>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import BackButton from '@/components/BackButton.vue'

const homestay = ref({
  id: 1,
  title: '山水间·云舍',
  location: '重庆市武隆区',
  rating: 4.9,
  views: 236,
  price: 368,
  roomType: '景观大床房 · 1室1厅1卫',
  capacity: 2,
  contactPhone: '023-6688-1122',
  summary: '峡谷景观房与贴心管家服务，适合度假小憩。',
  features: ['景观房', '管家服务', '双早套餐', '私人影院'],
  amenities: ['空调', 'WiFi', '观景阳台', '早餐', '停车位'],
  highlightTags: ['峡谷观景', '管家服务', '私密小院'],
  highlights: '位于武隆峡谷旁，拥有观景露台与私密小院，提供管家式服务与双早餐。',
  cover: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80'
})

const form = ref({
  date: '',
  nights: 1,
  name: '',
  phone: '',
  notes: ''
})

const disablePast = (time) => time.getTime() < Date.now() - 24 * 60 * 60 * 1000

const totalPrice = computed(() => {
  const nights = form.value.nights || 1
  return homestay.value.price * nights
})

const handleBook = () => {
  if (!form.value.date) {
    ElMessage.warning('请选择入住日期')
    return
  }
  if (!form.value.name) {
    ElMessage.warning('请输入联系人姓名')
    return
  }
  if (!form.value.phone) {
    ElMessage.warning('请输入联系电话')
    return
  }
  ElMessage.success(`已提交预订：${homestay.value.title}，共${form.value.nights || 1}晚`)
}
</script>

<style scoped lang="scss">
.homestay-detail {
  padding: 24px 32px 60px;
  background: #f5f7fa;
}

.layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 18px;
  align-items: start;
}

.left {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.right {
  position: sticky;
  top: 16px;
}

.hero-card {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 24px;
  background: #fff;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
  margin-bottom: 24px;
}

.hero-img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-radius: 12px;
}

.title {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 800;
  color: #111827;
}

.sub-title {
  margin: 0 0 6px 0;
  color: #6b7280;
  font-size: 14px;
}

.meta {
  display: flex;
  gap: 8px;
  color: #475569;
  font-size: 14px;
  margin-bottom: 10px;
}

.summary {
  color: #374151;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.chip {
  padding: 6px 10px;
  background: #f3f4f6;
  border-radius: 12px;
  font-size: 12px;
  color: #374151;
  font-weight: 600;
}

.price-block {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.price {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
}

.unit {
  color: #9ca3af;
  font-size: 13px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.info-card {
  background: #fff;
  border-radius: 14px;
  padding: 16px 18px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);

  h3 {
    margin: 0 0 10px 0;
    font-size: 16px;
    font-weight: 700;
    color: #111827;
  }

  ul {
    margin: 0;
    padding-left: 18px;
    color: #4b5563;
    line-height: 1.8;
    font-size: 14px;
  }

  p {
    margin: 0 0 10px 0;
    color: #4b5563;
    line-height: 1.6;
  }
}

.booking-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);

  h3 {
    margin: 0 0 12px 0;
    font-size: 18px;
    font-weight: 700;
    color: #111827;
  }
}

.total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.amount {
  font-size: 22px;
  color: #ef4444;
}

.book-btn {
  width: 100%;
  height: 46px;
  font-weight: 700;
}

@media (max-width: 1024px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .right {
    position: static;
  }

  .hero-card {
    grid-template-columns: 1fr;
  }

  .hero-img {
    height: 260px;
  }
}
</style>

