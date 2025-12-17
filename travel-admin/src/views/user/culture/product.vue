<template>
  <div class="product-detail">
    <!-- 返回按钮 -->
    <BackButton />

    <el-row :gutter="24">
      <!-- 左侧：产品详情 -->
      <el-col :span="16">
        <!-- 产品图片 -->
        <div class="product-images">
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
              v-for="(img, index) in product.images.slice(0, 4)"
              :key="index"
              :src="img"
              fit="cover"
              class="thumbnail"
              @click="product.mainImage = img"
            />
          </div>
        </div>

        <!-- 产品信息 -->
        <el-card class="info-card">
          <div class="product-header">
            <h1 class="product-name">{{ product.name }}</h1>
            <div class="product-badges">
              <el-tag type="danger" size="large">{{ product.badge }}</el-tag>
              <el-tag v-if="product.certified" type="success" size="large">
                官方认证
              </el-tag>
            </div>
          </div>

          <div class="product-price-section">
            <div class="price-main">
              <span class="price-label">价格</span>
              <div class="price-value">
                <span class="currency">¥</span>
                <span class="price-number">{{ product.price }}</span>
                <span class="price-unit">{{ product.unit }}</span>
              </div>
            </div>
            <div class="sales-info">
              <span>已售 {{ product.sales }}</span>
              <el-divider direction="vertical" />
              <span>
                <el-rate v-model="product.rating" disabled show-score />
              </span>
            </div>
          </div>

          <el-divider />

          <div class="product-origin">
            <h3>产地信息</h3>
            <div class="origin-info">
              <el-icon><Location /></el-icon>
              <span>{{ product.origin }}</span>
            </div>
          </div>

          <el-divider />

          <div class="product-intro">
            <h3>产品介绍</h3>
            <p>{{ product.description }}</p>
          </div>

          <el-divider />

          <div class="product-features">
            <h3>产品特点</h3>
            <div class="features-grid">
              <div v-for="feature in product.features" :key="feature" class="feature-item">
                <el-icon color="#67c23a"><SuccessFilled /></el-icon>
                <span>{{ feature }}</span>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="product-specs">
            <h3>规格参数</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item
                v-for="spec in product.specifications"
                :key="spec.label"
                :label="spec.label"
              >
                {{ spec.value }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

      </el-col>

      <!-- 右侧：购买信息 -->
      <el-col :span="8">
        <div class="purchase-card-sticky">
          <el-card class="purchase-card">
            <h3 class="purchase-title">
              <el-icon><ShoppingCart /></el-icon>
              购买信息
            </h3>

            <el-form :model="purchaseForm" label-position="top">
              <el-form-item label="购买规格">
                <el-radio-group v-model="purchaseForm.specification" class="spec-radio-group">
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
                <el-input v-model="purchaseForm.receiverName" placeholder="请输入姓名" />
              </el-form-item>

              <el-form-item label="联系电话">
                <el-input v-model="purchaseForm.receiverPhone" placeholder="请输入手机号" />
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

              <el-form-item label="备注">
                <el-input
                  v-model="purchaseForm.notes"
                  type="textarea"
                  :rows="2"
                  placeholder="选填，如有特殊需求请备注"
                />
              </el-form-item>
            </el-form>

            <el-divider />

            <CouponSelector
              v-model="selectedCoupon"
              :order-amount="goodsTotal"
              @change="handleCouponChange"
            />

            <div class="total-section">
              <div class="total-row">
                <span>商品总价</span>
                <span class="total-price">¥{{ goodsTotal }}</span>
              </div>
              <div class="total-row">
                <span>运费</span>
                <span class="shipping-fee">{{ shippingFee === 0 ? '免运费' : `¥${shippingFee}` }}</span>
              </div>
              <div v-if="selectedCoupon" class="total-row">
                <span>优惠券</span>
                <span class="coupon-discount">-¥{{ couponDiscount }}</span>
              </div>
              <el-divider />
              <div class="total-row final">
                <span>合计</span>
                <span class="final-price">¥{{ finalPrice }}</span>
              </div>
            </div>

            <div class="action-buttons">
              <el-button type="warning" size="large" class="action-btn" @click="addToCart">
                <el-icon><ShoppingCart /></el-icon> 加入购物车
              </el-button>
              <el-button type="danger" size="large" class="action-btn" @click="buyNow">
                <el-icon><Checked /></el-icon> 立即购买
              </el-button>
            </div>

            <div class="service-info">
              <div class="service-item">
                <el-icon><Van /></el-icon>
                <span>全国包邮</span>
              </div>
              <div class="service-item">
                <el-icon><CircleCheck /></el-icon>
                <span>7天无理由退货</span>
              </div>
              <div class="service-item">
                <el-icon><Medal /></el-icon>
                <span>正品保证</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>

    <!-- 地址选择弹窗 -->
    <el-dialog
      v-model="addressDialogVisible"
      title="选择收货地址"
      width="600px"
    >
      <div v-if="addresses.length === 0" class="empty-address">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="goToAddressPage">前往"我的地址"添加</el-button>
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
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import CouponSelector from '@/components/CouponSelector.vue'
import request from '@/utils/request'
import {
  ArrowLeft,
  Location,
  SuccessFilled,
  ShoppingCart,
  Checked,
  Van,
  CircleCheck,
  Medal,
  Picture
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 产品详情数据（从特色周边接口加载）
const product = ref<any>({
  id: undefined,
  name: '',
  badge: '',
  certified: false,
  price: 0,
  unit: '/件',
  sales: 0,
  rating: 4.8,
  origin: '',
  mainImage: '',
  images: [] as string[],
  description: '',
  features: [] as string[],
  specifications: [] as { label: string; value: string }[],
  availableSpecs: [] as { label: string; value: string }[],
  reviews: [] as any[],
  // 购买规格配置：[{ label, value, price }]
  sellSpecs: [] as any[]
})

// 购买表单
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

// 计算商品总价
const goodsTotal = computed(() => {
  let price = product.value.price
  const specValue = purchaseForm.value.specification

  // 如果配置了购买规格，则优先使用每个规格自己的 price
  if (product.value.availableSpecs && product.value.availableSpecs.length > 0) {
    const matched = product.value.availableSpecs.find(
      (item: any) => item.value === specValue || item.label === specValue
    )
    if (matched && matched.price != null) {
      price = Number(matched.price)
    }
  } else {
    // 兼容旧逻辑：根据规格字符串简单调整倍数
    if (specValue === '500g') {
      price = product.value.price * 1.9  // 优惠价
    } else if (specValue === '1000g') {
      price = product.value.price * 3.6  // 更优惠
    }
  }

  return Math.round(price * purchaseForm.value.quantity)
})

// 计算总价（兼容旧代码）
const totalPrice = computed(() => goodsTotal.value)

// 运费（满100免运费）
const shippingFee = computed(() => {
  return goodsTotal.value >= 100 ? 0 : 10
})

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

// 返回上一页
const goBack = () => {
  router.back()
}

// 加入购物车
const addToCart = () => {
  // TODO: 调用后端API加入购物车
  const cartItem = {
    productId: product.value.id,
    name: product.value.name,
    image: product.value.mainImage,
    price: product.value.price,
    specification: purchaseForm.value.specification,
    quantity: purchaseForm.value.quantity,
    origin: product.value.origin
  }
  
  console.log('加入购物车:', cartItem)
  
  ElMessage.success({
    message: '已加入购物车',
    duration: 2000,
    showClose: true,
    appendTo: document.body
  })
  
  // 询问是否前往购物车
  setTimeout(() => {
    ElMessageBox.confirm('是否前往购物车查看？', '提示', {
      confirmButtonText: '去购物车',
      cancelButtonText: '继续购物',
      type: 'info'
    }).then(() => {
      router.push('/home/user/culture/cart')
    }).catch(() => {
      // 继续购物
    })
  }, 500)
}

// 立即购买
const buyNow = async () => {
  if (!validateForm()) return

  try {
    await ElMessageBox.confirm(
      `确认下单「${product.value.name}」？\n规格：${purchaseForm.value.specification}\n数量：${purchaseForm.value.quantity}\n${selectedCoupon.value ? `优惠券：${selectedCoupon.value.name || selectedCoupon.value.couponName}\n优惠：-¥${couponDiscount.value}\n` : ''}总计：¥${finalPrice.value}`,
      '确认下单',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'info' }
    )

    // 根据选择的规格获取对应价格
    let unitPrice = product.value.price || 0
    if (product.value.availableSpecs && product.value.availableSpecs.length > 0) {
      const selectedSpec = product.value.availableSpecs.find(
        (s: any) => s.value === purchaseForm.value.specification
      )
      if (selectedSpec && selectedSpec.price != null) {
        unitPrice = Number(selectedSpec.price)
      }
    }

    const payload = {
      productId: product.value.id,
      productType: 8, // 8=特色周边
      productName: product.value.name,
      productImage: product.value.mainImage,
      productPrice: unitPrice,
      quantity: purchaseForm.value.quantity,
      specification: purchaseForm.value.specification,
      contactName: purchaseForm.value.receiverName,
      contactPhone: purchaseForm.value.receiverPhone,
      address: purchaseForm.value.address,
      buyerMessage: purchaseForm.value.notes,
      couponId: selectedCoupon.value?.id || null,
      couponDiscount: couponDiscount.value,
      totalAmount: goodsTotal.value,
      finalAmount: finalPrice.value,
      shippingFee: shippingFee.value
    }

    const res = await request.post('/culture/order', payload)
    if (res.code === 200) {
      ElMessage.success('下单成功，即将跳转到我的订单')
      // 跳转到用户端"我的订单"页面
      router.push('/home/user/culture/orders')
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch {
    // 用户取消
  }
}

// 表单验证
const validateForm = () => {
  if (!purchaseForm.value.specification) {
    ElMessage.warning('请选择规格')
    return false
  }
  if (!purchaseForm.value.receiverName) {
    ElMessage.warning('请输入收货人姓名')
    return false
  }
  if (!purchaseForm.value.receiverPhone) {
    ElMessage.warning('请输入联系电话')
    return false
  }
  if (!purchaseForm.value.address) {
    ElMessage.warning('请输入收货地址')
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
    ElMessage.info('暂未添加收货地址，请先前往"我的地址"页面添加')
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

// 解析 images/规格/特点等字段
const parseImages = (val: any): string[] => {
  if (!val) return []
  if (Array.isArray(val)) return val
  try {
    const parsed = JSON.parse(val)
    if (Array.isArray(parsed)) return parsed
    return []
  } catch {
    return typeof val === 'string' ? [val] : []
  }
}

const parseLines = (val?: string): string[] => {
  if (!val) return []
  return val
    .split(/\r?\n/)
    .map(s => s.trim())
    .filter(Boolean)
}

const parseSpecs = (val?: string): { label: string; value: string }[] => {
  if (!val) return []
  try {
    const parsed = JSON.parse(val)
    if (Array.isArray(parsed)) {
      return parsed
        .map((it: any) => ({
          label: it.label || it.name || '',
          value: it.value || it.content || ''
        }))
        .filter(it => it.label && it.value)
    }
  } catch {
    // fall through
  }
  // 兼容“名称：值”按行存储
  return val
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(Boolean)
    .map(line => {
      const [label, value] = line.split(/[:：]/)
      return { label: label?.trim() || '', value: (value || '').trim() }
    })
    .filter(it => it.label && it.value)
}

// 从后端加载特色周边详情
const loadProductDetail = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await request.get(`/culture/attractions/${id}`)
    if (res.code !== 200 || !res.data) {
      ElMessage.error('获取特色周边详情失败')
      return
    }
    const data = res.data
    const images = parseImages(data.images || data.image || data.coverImage)
    const features = parseLines(data.features)
    const specs = parseSpecs(data.specifications)

    // 解析购买规格配置
    let sellSpecs: any[] = []
    if (data.sellSpecs) {
      try {
        const parsed = JSON.parse(data.sellSpecs)
        if (Array.isArray(parsed)) {
          sellSpecs = parsed
        }
      } catch {
        sellSpecs = []
      }
    }

    const province = data.province || ''
    const city = data.city || ''
    const origin = [province, city].filter(Boolean).join(' · ') || data.address || '待定'

    const tags = Array.isArray(data.tags)
      ? data.tags
      : (typeof data.tags === 'string' ? data.tags.split(/[,，、\s]+/).filter(Boolean) : [])

    const certified = data.certified === 1 || data.certified === true

    product.value = {
      id: data.id,
      name: data.name,
      // 标签优先使用后台 badge，其次 tags
      badge: data.badge || tags[0] || '特色周边',
      certified,
      price: data.ticketPrice != null ? Number(data.ticketPrice) : 0,
      unit: '/件',
      sales: data.viewCount || 0,
      rating: data.rating != null ? Number(data.rating) : 4.8,
      origin,
      mainImage: images[0] || '',
      images: images.length ? images : ['https://picsum.photos/600/600?random=20'],
      description: data.description || '',
      features,
      specifications: specs,
      // 如果配置了购买规格，则用它作为右侧规格+价格来源；否则从规格参数中智能生成
      availableSpecs: sellSpecs.length
        ? sellSpecs.map((s, idx) => ({
            label: s.label || s.value || `规格${idx + 1}`,
            value: s.value || s.label || `spec_${idx + 1}`,
            price: s.price != null ? Number(s.price) : (data.ticketPrice != null ? Number(data.ticketPrice) : 0)
          }))
        : (specs.length
            ? specs
                .filter(s => /规格|净含量|容量|重量/.test(s.label))
                .map(s => ({ label: s.value, value: s.value, price: data.ticketPrice != null ? Number(data.ticketPrice) : 0 }))
            : []),
      reviews: [],
      sellSpecs
    }

    // 默认选第一条规格
    if (product.value.availableSpecs.length) {
      purchaseForm.value.specification = product.value.availableSpecs[0].value
    }
  } catch (error) {
    console.error('加载特色周边详情异常', error)
    ElMessage.error('加载特色周边详情出错')
  }
}

// 页面加载
onMounted(() => {
  loadAddressesFromStorage() // 自动加载默认地址
  loadProductDetail()
})
</script>

<style scoped lang="scss">
.product-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.product-images {
  margin-bottom: 20px;
}

.main-image {
  width: 100%;
  height: 500px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;

  :deep(.el-image__inner) {
    object-fit: cover;
  }
}

.thumbnail-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.thumbnail {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;

  &:hover {
    border-color: #409eff;
  }

  :deep(.el-image__inner) {
    object-fit: cover;
  }
}

.info-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }
}

.product-header {
  margin-bottom: 20px;
}

.product-name {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px 0;
}

.product-badges {
  display: flex;
  gap: 12px;
}

.product-price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe5e5 100%);
  border-radius: 12px;
}

.price-main {
  display: flex;
  flex-direction: column;
  gap: 8px;

  .price-label {
    font-size: 14px;
    color: #909399;
  }

  .price-value {
    display: flex;
    align-items: baseline;
    gap: 4px;

    .currency {
      font-size: 20px;
      color: #f56c6c;
      font-weight: 600;
    }

    .price-number {
      font-size: 40px;
      font-weight: 800;
      color: #f56c6c;
    }

    .price-unit {
      font-size: 16px;
      color: #909399;
    }
  }
}

.sales-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.product-origin,
.product-intro,
.product-features,
.product-specs {
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

.origin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #606266;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.purchase-card-sticky {
  position: sticky;
  top: 24px;
}

.purchase-card {
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.purchase-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
}

.spec-radio-group {
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

  :deep(.el-radio__label) {
    width: 100%;
    text-align: center;
  }
}

.total-section {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  margin-top: 20px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 15px;
  color: #606266;

  .coupon-discount {
    color: #67c23a;
    font-weight: 600;
  }

  &.final {
    font-size: 18px;
    font-weight: 700;
    color: #303133;

    .final-price {
      font-size: 24px;
      color: #f56c6c;
    }
  }
}

.shipping-fee {
  color: #67c23a;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  flex-direction: row;
  gap: 12px;
  margin-top: 20px;
}

.action-btn {
  width: 100%;
  height: 48px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  
  .el-icon {
    margin-right: 6px;
    font-size: 18px;
  }
}

.service-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 20px;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;

  .el-icon {
    font-size: 24px;
    color: #67c23a;
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
        color: #606266;
        font-size: 14px;
        line-height: 1.5;
      }
    }
  }
}

.empty-address {
  padding: 40px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .product-detail {
    padding: 12px;
  }

  .product-name {
    font-size: 24px;
  }

  .main-image {
    height: 300px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .purchase-card-sticky {
    position: static;
  }
}
</style>


