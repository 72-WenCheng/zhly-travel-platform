<template>
  <div class="agri-product-detail">
    <BackButton />

    <el-row :gutter="24">
      <!-- 左侧：商品详情 -->
      <el-col :span="16">
        <el-card class="gallery-card">
          <div class="gallery">
            <el-image
              :src="product.mainImage"
              :preview-src-list="product.images"
              fit="cover"
              class="main-image"
              :preview-teleported="true"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>

            <div class="thumbnail-list">
              <el-image
                v-for="(img, index) in product.images.slice(0, 5)"
                :key="index"
                :src="img"
                fit="cover"
                class="thumbnail"
                @click="product.mainImage = img"
              />
            </div>
          </div>
        </el-card>

        <el-card class="info-card">
          <div class="title-row">
            <div>
              <h1 class="product-name">{{ product.name }}</h1>
              <div class="meta-line">
                <el-tag v-for="badge in product.badges" :key="badge" type="danger" effect="dark">
                  {{ badge }}
                </el-tag>
                <el-tag v-if="product.certified" type="success">
                  官方认证
                </el-tag>
              </div>
            </div>
            <div class="rating">
              <el-rate v-model="product.rating" disabled show-score />
              <span class="rating-count">{{ product.viewCount }} 次浏览</span>
            </div>
          </div>

          <div class="price-panel">
            <div class="price-box">
              <span class="label">价格</span>
              <div class="price">
                <span class="currency">¥</span>
                <span class="number">{{ product.price }}</span>
                <span class="unit">/{{ product.unit }}</span>
              </div>
              <div class="sub-meta">月销 {{ product.sales }} · 库存 {{ product.stock }}</div>
            </div>
            <div class="benefit-box">
              <div class="benefit-item">
                <el-icon><Van /></el-icon>
                <span>{{ product.shippingDesc }}</span>
              </div>
              <div class="benefit-item">
                <el-icon><Medal /></el-icon>
                <span>产地直供</span>
              </div>
              <div class="benefit-item">
                <el-icon><Switch /></el-icon>
                <span>可开票</span>
              </div>
            </div>
          </div>

          <el-divider />

          <!-- 规格参数（基础信息） -->
          <section class="section">
            <h3>规格参数</h3>
            <div class="base-info">
              <div class="info-item">
                <el-icon><Location /></el-icon>
                <span>产地：{{ product.origin }}</span>
              </div>
              <div class="info-item">
                <el-icon><Discount /></el-icon>
                <span>分类：{{ product.category }}</span>
              </div>
              <div class="info-item">
                <el-icon><Timer /></el-icon>
                <span>保质期：{{ product.shelfLife }}</span>
              </div>
            </div>
          </section>

          <el-divider />

          <div class="tags">
            <el-tag v-for="tag in product.tags" :key="tag" type="info" effect="light">
              #{{ tag }}
            </el-tag>
          </div>

          <el-divider />

          <section class="section">
            <h3>产品亮点</h3>
            <div class="feature-grid">
              <div v-for="item in product.features" :key="item.title" class="feature-card">
                <el-icon class="feature-icon" :size="20">
                  <component :is="item.icon" />
                </el-icon>
                <div class="feature-body">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.description }}</p>
                </div>
              </div>
            </div>
          </section>

          <el-divider />

          <section class="section">
            <h3>产品介绍</h3>
            <p class="description">{{ product.description }}</p>
          </section>

          <el-divider />

          <section class="section">
            <el-descriptions :column="2" border>
              <el-descriptions-item
                v-for="spec in product.specifications"
                :key="spec.label"
                :label="spec.label"
              >
                {{ spec.value }}
              </el-descriptions-item>
            </el-descriptions>
          </section>

          <el-divider />

          <section class="section trace-section">
            <div class="trace-header">
              <div>
                <h3>溯源信息</h3>
                <p class="sub-text">从田间到餐桌，全程可追溯</p>
              </div>
              <el-tag class="trace-status" effect="dark">
                已完成溯源
              </el-tag>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in product.traceability"
                :key="index"
                :timestamp="item.time"
              >
                <h4>{{ item.title }}</h4>
                <p>{{ item.detail }}</p>
              </el-timeline-item>
            </el-timeline>
          </section>
        </el-card>
      </el-col>

      <!-- 右侧：购买信息 -->
      <el-col :span="8">
        <div class="purchase-sticky">
          <el-card class="purchase-card">
            <h3 class="purchase-title">
              <el-icon><ShoppingCart /></el-icon>
              购买信息
            </h3>

            <el-form :model="purchaseForm" label-position="top">
              <el-form-item label="规格选择">
                <el-radio-group v-model="purchaseForm.specification" class="spec-group">
                  <el-radio
                    v-for="spec in product.availableSpecs"
                    :key="spec.value"
                    :label="spec.value"
                    border
                  >
                    {{ spec.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="购买数量">
                <el-input-number
                  v-model="purchaseForm.quantity"
                  :min="1"
                  :max="999"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="收货人姓名">
                <el-input
                  v-model="purchaseForm.receiverName"
                  placeholder="请输入收货人姓名"
                />
              </el-form-item>

              <el-form-item label="联系电话">
                <el-input
                  v-model="purchaseForm.receiverPhone"
                  placeholder="请输入联系电话"
                />
              </el-form-item>

              <el-form-item label="收货地址">
                <div class="address-field">
                  <el-input
                    v-model="purchaseForm.address"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入详细收货地址"
                  />
                  <el-button
                    class="address-select-btn"
                    type="primary"
                    text
                    @click="openAddressDialog"
                  >
                    从我的地址选择
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item label="买家备注">
                <el-input
                  v-model="purchaseForm.notes"
                  type="textarea"
                  :rows="2"
                  placeholder="选填：口味、包装等"
                />
              </el-form-item>
            </el-form>

            <el-divider />

            <CouponSelector
              v-model="selectedCoupon"
              :order-amount="goodsTotal"
              @change="handleCouponChange"
            />

            <div class="total-box">
              <div class="total-row">
                <span>商品总价</span>
                <span class="price">¥{{ goodsTotal }}</span>
              </div>
              <div class="total-row">
                <span>运费</span>
                <span class="shipping">{{ shippingFee === 0 ? '免运费' : `¥${shippingFee}` }}</span>
              </div>
              <div v-if="selectedCoupon" class="total-row">
                <span>优惠券</span>
                <span class="coupon-discount">-¥{{ couponDiscount }}</span>
              </div>
              <el-divider />
              <div class="total-row final">
                <span>应付合计</span>
                <span class="final-price">¥{{ finalPrice }}</span>
              </div>
            </div>

            <div class="actions">
              <el-button type="warning" size="large" class="action" @click="addToCart">
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button type="danger" size="large" class="action" @click="buyNow">
                <el-icon><Checked /></el-icon>
                立即购买
              </el-button>
            </div>

            <div class="service-list">
              <div class="service-item">
                <el-icon><CircleCheck /></el-icon>
                <span>正品保证</span>
              </div>
              <div class="service-item">
                <el-icon><Van /></el-icon>
                <span>48小时内发货</span>
              </div>
              <div class="service-item">
                <el-icon><Switch /></el-icon>
                <span>7天无理由退货</span>
              </div>
            </div>
          </el-card>

          <!-- 地址选择弹窗 -->
          <el-dialog
            v-model="addressDialogVisible"
            title="选择收货地址"
            width="600px"
          >
            <div v-if="addresses.length === 0" class="empty-address">
              <el-empty description="暂无收货地址">
                <el-button type="primary" @click="goToAddressPage">前往“我的地址”添加</el-button>
              </el-empty>
            </div>
            <div v-else class="address-dialog-content">
              <el-radio-group v-model="selectedAddressId" class="address-list">
                <div
                  v-for="address in addresses"
                  :key="address.id"
                  class="address-item"
                  :class="{ active: selectedAddressId === address.id }"
                  @click="selectedAddressId = address.id"
                >
                  <div class="address-radio-wrapper">
                    <el-radio :label="address.id" />
                  </div>
                  <div class="address-info">
                    <div class="info-row">
                      <span class="name">{{ address.name }}</span>
                      <span class="phone">{{ address.phone }}</span>
                      <el-tag v-if="address.isDefault" type="danger" size="small">默认</el-tag>
                    </div>
                    <div class="address-row">
                      {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
                    </div>
                  </div>
                </div>
              </el-radio-group>
            </div>
            <template #footer>
              <el-button @click="addressDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="confirmSelectAddress">确定</el-button>
            </template>
          </el-dialog>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import CouponSelector from '@/components/CouponSelector.vue'
import {
  Location,
  Picture,
  ShoppingCart,
  Checked,
  Van,
  Medal,
  Switch,
  Discount,
  Timer,
  Brush,
  Present
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const product = ref({
  id: null,
  name: '',
  category: '',
  price: 0,
  unit: '',
  sales: 0,
  stock: 0,
  viewCount: 0,
  rating: 0,
  reviewCount: 0,
  certified: false,
  origin: '',
  shelfLife: '',
  shippingDesc: '',
  badges: [] as string[],
  tags: [] as string[],
  mainImage: '',
  images: [] as string[],
  description: '',
  features: [] as any[],
  specifications: [] as any[],
  traceability: [] as any[],
  availableSpecs: [] as any[],
  reviews: [] as any[],
  productType: 4 // 4=特产/商品
})

const purchaseForm = ref({
  specification: '',
  quantity: 1,
  receiverName: '',
  receiverPhone: '',
  address: '',
  notes: ''
})

// 地址相关
interface Address {
  id: number
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
}

const ADDRESS_STORAGE_KEY = 'travel_user_addresses'

const addresses = ref<Address[]>([])
const selectedAddressId = ref<number | null>(null)
const addressDialogVisible = ref(false)

// 选中的优惠券
const selectedCoupon = ref(null)

// 计算商品总价（根据选择的规格动态计算）
const goodsTotal = computed(() => {
  let price = product.value.price || 0
  const spec = purchaseForm.value.specification || ''

  // 根据克数/规格调整单价：
  // 约定：基础价格为最小规格（如 250g），更大规格按优惠系数计算
  if (spec.includes('500')) {
    price = price * 1.9 // 500g 礼盒，稍微优惠
  } else if (spec.includes('1000') || spec.includes('1kg') || spec.includes('1KG')) {
    price = price * 3.6 // 1000g / 1kg 礼盒，更优惠
  }

  return Math.round(price * (purchaseForm.value.quantity || 1))
})

// 计算总价（兼容旧代码）
const totalPrice = computed(() => goodsTotal.value)

const shippingFee = computed(() => (goodsTotal.value >= 99 ? 0 : 12))

// 计算优惠金额
const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  const discountValue = selectedCoupon.value.discountValue || selectedCoupon.value.amount || 0
  const subtotal = goodsTotal.value + shippingFee.value
  return Math.min(discountValue, subtotal)
})

// 最终价格
const finalPrice = computed(() => {
  return Math.max(0, goodsTotal.value + shippingFee.value - couponDiscount.value)
})

// 处理优惠券变化
const handleCouponChange = (coupon: any) => {
  selectedCoupon.value = coupon
}

const addToCart = () => {
  ElMessage.success('已加入购物车（示例，未落库）')
}

const buyNow = async () => {
  if (!validateForm()) return

  try {
    await ElMessageBox.confirm(
      `确认下单「${product.value.name}」？\n规格：${purchaseForm.value.specification}\n数量：${purchaseForm.value.quantity}\n${selectedCoupon.value ? `优惠券：${selectedCoupon.value.name || selectedCoupon.value.couponName}\n优惠：-¥${couponDiscount.value}\n` : ''}总计：¥${finalPrice.value}`,
      '确认下单',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'info' }
    )

    const payload = {
      productId: product.value.id,
      productType: product.value.productType || 4,
      productName: product.value.name,
      productImage: product.value.mainImage,
      productPrice: product.value.price,
      quantity: purchaseForm.value.quantity,
      specification: purchaseForm.value.specification,
      contactName: purchaseForm.value.receiverName,
      contactPhone: purchaseForm.value.receiverPhone,
      address: purchaseForm.value.address,
      buyerMessage: purchaseForm.value.notes,
      couponId: selectedCoupon.value?.id || null,
      couponDiscount: couponDiscount.value,
      totalAmount: totalPrice.value,
      finalAmount: finalPrice.value,
      shippingFee: shippingFee.value
    }

    const res = await request.post('/culture/order', payload)
    if (res.code === 200) {
      ElMessage.success('下单成功，即将跳转到我的订单')
      // 跳转到用户端“我的订单”页面
      router.push('/home/user/culture/orders')
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch {
    // 用户取消
  }
}

const validateForm = () => {
  if (!purchaseForm.value.receiverName) {
    ElMessage.warning('请输入收货人姓名')
    return false
  }
  if (!purchaseForm.value.receiverPhone) {
    ElMessage.warning('请输入联系电话')
    return false
  }
  if (!purchaseForm.value.address) {
    ElMessage.warning('请填写收货地址')
    return false
  }
  if (!purchaseForm.value.specification) {
    ElMessage.warning('请选择规格')
    return false
  }
  return true
}

// 从本地存储加载地址，并自动应用默认地址
const loadAddressesFromStorage = () => {
  try {
    const raw = localStorage.getItem(ADDRESS_STORAGE_KEY)
    if (!raw) return
    const list = JSON.parse(raw) as Address[]
    if (!Array.isArray(list)) return
    addresses.value = list

    // 找到默认地址或第一个地址
    const defaultAddress = list.find(a => a.isDefault) || list[0]
    if (defaultAddress) {
      selectedAddressId.value = defaultAddress.id
      applyAddressToForm(defaultAddress)
    }
  } catch (error) {
    console.error('加载本地地址失败:', error)
  }
}

const applyAddressToForm = (addr: Address) => {
  purchaseForm.value.receiverName = addr.name
  purchaseForm.value.receiverPhone = addr.phone
  purchaseForm.value.address = `${addr.province} ${addr.city} ${addr.district} ${addr.detail}`
}

const openAddressDialog = () => {
  if (addresses.value.length === 0) {
    ElMessage.info('暂未添加收货地址，请先前往“我的地址”页面添加')
  }
  addressDialogVisible.value = true
}

const confirmSelectAddress = () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择一个地址')
    return
  }
  const addr = addresses.value.find(a => a.id === selectedAddressId.value)
  if (!addr) {
    ElMessage.warning('未找到选中的地址')
    return
  }
  applyAddressToForm(addr)
  addressDialogVisible.value = false
}

const goToAddressPage = () => {
  addressDialogVisible.value = false
  router.push('/home/user/addresses')
}

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await request.get(`/culture/products/${id}`)
    if (res.code === 200 && res.data) {
      const data = res.data as any
      const imgs = normalizeArray(data.images)
      const highlightList = normalizeArray(data.highlights || data.features || [])
      product.value = {
        id: data.id,
        name: data.productName || data.name || '',
        category: data.category || '',
        price: Number(data.price || 0),
        unit: data.unit || '',
        sales: data.salesCount || 0,
        stock: data.stock ?? 0,
        viewCount: data.viewCount || data.reviewCount || 0,
        rating: data.rating || 0,
        reviewCount: data.reviewCount || 0,
        certified: false,
        origin: data.origin || '',
        shelfLife: data.shelfLife || '',
        shippingDesc: data.shippingDesc || '',
        badges: data.badge ? [data.badge] : [],
        tags: normalizeArray(data.tags || data.features || []),
        mainImage: imgs[0] || '',
        images: imgs,
        description: data.description || data.summary || '',
        // 产品亮点：使用高亮/亮点字段映射到「产品亮点」卡片
        features: highlightList.map((text: any) => ({
          title: typeof text === 'string' ? text : String(text),
          description: '',
          icon: Brush
        })),
        specifications: normalizeArray(data.specifications).map((s: any) => {
          if (typeof s === 'string') return { label: s, value: '' }
          return s
        }),
        traceability: [],
        availableSpecs: normalizeArray(data.availableSpecs).length
          ? normalizeArray(data.availableSpecs)
          : [{ label: data.unit || '默认规格', value: data.unit || 'default' }],
        reviews: [],
        productType: data.productType || 4
      }
      if (!purchaseForm.value.specification && product.value.availableSpecs.length > 0) {
        purchaseForm.value.specification = product.value.availableSpecs[0].value
      }
    } else {
      ElMessage.error(res.message || '加载商品详情失败')
    }
  } catch (error) {
    console.error('加载商品详情失败', error)
    ElMessage.error('加载商品详情失败')
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

onMounted(() => {
  loadDetail()
  loadAddressesFromStorage()
})
</script>

<style scoped lang="scss">
.agri-product-detail {
  min-height: 100vh;
  padding: 24px;
  background: #f5f7fa;
}

.gallery-card,
.info-card,
.reviews-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.gallery {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.main-image {
  width: 100%;
  height: 420px;
  border-radius: 12px;
  overflow: hidden;

  :deep(img) {
    object-fit: cover;
  }
}

.thumbnail-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.thumbnail {
  width: 100%;
  height: 92px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
  }
}

.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  background: #f2f6fc;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 30px;
  font-weight: 800;
  color: #303133;
}

.meta-line {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.rating {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.rating-count {
  font-size: 12px;
  color: #909399;
}

.price-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffecec 100%);
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.price-box .label {
  font-size: 12px;
  color: #909399;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 4px;

  .currency {
    font-size: 18px;
    color: #f56c6c;
  }

  .number {
    font-size: 42px;
    font-weight: 800;
    color: #f56c6c;
  }

  .unit {
    color: #909399;
  }
}

.sub-meta {
  font-size: 13px;
  color: #909399;
}

.benefit-box {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.base-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #606266;
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.section h3 {
  margin: 0 0 12px 0;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.description {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  margin: 0;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.feature-card {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 10px;
  display: flex;
  gap: 10px;
}

.feature-icon {
  font-size: 22px;
}

.feature-card h4 {
  margin: 0 0 6px 0;
  font-size: 16px;
  color: #303133;
}

.feature-card p {
  margin: 0;
  color: #606266;
}

.trace-section .trace-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.trace-status {
  background: #4caf50;
  border-color: #4caf50;
  color: #fff;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.sub-text {
  margin: 4px 0 0 0;
  color: #909399;
}

.purchase-sticky {
  position: sticky;
  top: 24px;
}

.purchase-card :deep(.el-card__body) {
  padding: 20px;
}

.purchase-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 16px 0;
}

.spec-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;

  :deep(.el-radio) {
    margin: 0;
    width: 100%;
    justify-content: center;
    align-items: center;
    text-align: center;
  }

  :deep(.el-radio.is-checked .el-radio__inner) {
    background-color: #d3d5d8;
    border-color: #d3d5d8;
  }

  :deep(.el-radio.is-checked .el-radio__label) {
    color: #303133;
  }

  :deep(.el-radio__label) {
    width: 100%;
    text-align: center;
  }
}

.total-box {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 10px;
  margin-top: 12px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  color: #606266;

  .coupon-discount {
    color: #67c23a;
    font-weight: 600;
  }

  &.final {
    font-weight: 700;
    color: #303133;

    .final-price {
      font-size: 22px;
      color: #f56c6c;
    }
  }
}

.price {
  color: #f56c6c;
  font-weight: 700;
}

.shipping {
  color: #67c23a;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.action {
  width: 100%;
}

.service-list {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  color: #909399;
  font-size: 12px;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;

  .el-icon {
    color: #67c23a;
    font-size: 20px;
  }
}

@media (max-width: 900px) {
  .agri-product-detail {
    padding: 12px;
  }

  .title-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .price-panel {
    flex-direction: column;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .benefit-box {
    grid-template-columns: repeat(2, 1fr);
  }
}

.address-field {
  display: flex;
  flex-direction: column;
  gap: 6px;

  .address-select-btn {
    align-self: flex-end;
    padding: 0;
  }
}

.address-dialog-content {
  .address-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .address-item {
    display: flex;
    gap: 12px;
    padding: 14px 16px;
    border-radius: 8px;
    border: 2px solid #dcdfe6;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      border-color: #c0c4cc;
    }

    &.active {
      border-color: #409eff;
      background: #ecf5ff;
    }

    .address-radio-wrapper {
      flex-shrink: 0;

      :deep(.el-radio__label) {
        display: none;
      }
    }

    .address-info {
      flex: 1;

      .info-row {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 6px;

        .name {
          font-weight: 600;
        }

        .phone {
          color: #909399;
          font-size: 13px;
        }
      }

      .address-row {
        font-size: 13px;
        color: #606266;
      }
    }
  }
}

.empty-address {
  padding: 30px 0;
}
</style>

