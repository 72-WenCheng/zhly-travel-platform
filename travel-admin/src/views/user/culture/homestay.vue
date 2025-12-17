<template>
  <div class="homestay-detail">
    <BackButton />

    <div class="layout">
      <div class="left">
        <section class="hero-card">
          <div class="hero-left">
            <el-image
              v-if="heroCover"
              :src="heroCover"
              :preview-src-list="previewImages"
              fit="cover"
              class="hero-img"
            />
            <div v-else class="hero-img placeholder">暂无图片</div>
            <div v-if="homestay.images && homestay.images.length > 1" class="thumbs">
              <el-image
                v-for="(img, idx) in homestay.images"
                :key="img + idx"
                :src="img"
                :preview-src-list="previewImages"
                fit="cover"
                class="thumb"
              />
            </div>
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

          <el-divider />

          <CouponSelector
            v-model="selectedCoupon"
            :order-amount="totalPrice"
            @change="handleCouponChange"
          />

          <div class="total">
            <div class="total-row">
              <span>总计</span>
              <span class="amount">¥{{ finalPrice }}</span>
            </div>
            <div v-if="selectedCoupon" class="coupon-discount">
              <span>已优惠</span>
              <span class="discount-amount">-¥{{ couponDiscount }}</span>
            </div>
          </div>

          <el-button class="book-btn" type="primary" size="large" @click="handleBook">
            立即预订
          </el-button>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import CouponSelector from '@/components/CouponSelector.vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const homestay = ref({
  id: null,
  title: '',
  location: '',
  rating: 0,
  views: 0,
  price: 0,
  roomType: '',
  capacity: 0,
  contactPhone: '',
  summary: '',
  features: [] as string[],
  amenities: [] as string[],
  highlightTags: [] as string[],
  highlights: '',
  cover: '',
  images: [] as string[]
})

const heroCover = computed(() => {
  if (homestay.value.cover) return homestay.value.cover
  if (homestay.value.images && homestay.value.images.length) return homestay.value.images[0]
  return ''
})

// 预览列表：封面优先，其次是服务图片（去重）
const previewImages = computed(() => {
  const imgs = homestay.value.images || []
  const cover = heroCover.value
  if (!cover && !imgs.length) return []
  const result: string[] = []
  if (cover) result.push(cover)
  for (const url of imgs) {
    if (url && !result.includes(url)) result.push(url)
  }
  return result
})

const form = ref({
  date: '',
  nights: 1,
  name: '',
  phone: '',
  notes: ''
})

const disablePast = (time: Date) => time.getTime() < Date.now() - 24 * 60 * 60 * 1000

// 选中的优惠券
const selectedCoupon = ref(null)

const totalPrice = computed(() => {
  const nights = form.value.nights || 1
  return homestay.value.price * nights
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

// 将日期转成 yyyy-MM-dd（使用本地时间，避免 ISO 时区导致少一天）
const formatDateOnly = (val: any): string | null => {
  if (!val) return null
  const d = new Date(val)
  if (Number.isNaN(d.getTime())) return null
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

// 处理优惠券变化
const handleCouponChange = (coupon: any) => {
  selectedCoupon.value = coupon
}

const handleBook = async () => {
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

  try {
    await ElMessageBox.confirm(
      `确认预订「${homestay.value.title}」？\n入住日期：${form.value.date}\n晚数：${form.value.nights || 1}晚\n${selectedCoupon.value ? `优惠券：${selectedCoupon.value.name || selectedCoupon.value.couponName}\n优惠：-¥${couponDiscount.value}\n` : ''}总计：¥${finalPrice.value}`,
      '确认预订',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const nights = form.value.nights || 1

    // 后端 bookingType=3 为民宿预定，需要 homestayId，并且 date 只接受 yyyy-MM-dd
    const payload = {
      bookingType: 3,
      homestayId: homestay.value.id,
      itemName: homestay.value.title,
      date: formatDateOnly(form.value.date),
      timeSlot: `${nights}晚`,
      quantity: nights,
      contactName: form.value.name,
      contactPhone: form.value.phone,
      notes: form.value.notes,
      totalAmount: finalPrice.value,
      unitPrice: homestay.value.price,
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

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await request.get(`/culture/homestays/${id}`)
    if (res.code === 200 && res.data) {
      const data = res.data as any
      homestay.value = {
        id: data.id,
        title: data.title,
        location: data.location,
        rating: data.rating || 0,
        views: data.views || 0,
        price: Number(data.price || 0),
        roomType: data.roomType || '',
        capacity: data.capacity || 0,
        contactPhone: data.contactPhone || '',
        summary: data.description || '',
        features: normalizeArray(data.features),
        amenities: normalizeArray(data.amenities),
        highlightTags: normalizeArray(data.highlightTags),
        highlights: data.highlights || '',
        cover: data.cover || '',
        images: normalizeImages(data.images || data.cover || [])
      }
    } else {
      ElMessage.error(res.message || '加载民宿详情失败')
    }
  } catch (error) {
    console.error('加载民宿详情失败', error)
    ElMessage.error('加载民宿详情失败')
  }
}

const normalizeArray = (val: any) => {
  if (!val) return []
  if (Array.isArray(val)) return val
  try {
    const parsed = JSON.parse(val)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

const normalizeImages = (images: any): string[] => {
  if (!images) return []
  if (Array.isArray(images)) return images
  if (typeof images === 'string') {
    try {
      const parsed = JSON.parse(images)
      if (Array.isArray(parsed)) return parsed
      return [images]
    } catch {
      return [images]
    }
  }
  return []
}

onMounted(() => {
  loadDetail()
})
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
  background: #f5f7fa;
}

.title {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 800;
  color: #111827;
}

.thumbs {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(72px, 1fr));
  gap: 8px;
  margin-top: 10px;
}

.thumb {
  width: 100%;
  height: 72px;
  object-fit: cover;
  border-radius: 8px;
}

.hero-img.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a3a3a3;
  background: #f5f7fa;
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
  flex-direction: column;
  gap: 8px;

  .total-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

