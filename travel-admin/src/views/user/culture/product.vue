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
                <el-icon><Checked /></el-icon>
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

        <!-- 用户评价 -->
        <el-card class="reviews-card">
          <div class="reviews-header">
            <h3>用户评价 ({{ product.reviews.length }})</h3>
            <div class="rating-summary">
              <el-rate v-model="product.rating" disabled show-score />
            </div>
          </div>
          
          <div class="reviews-list">
            <div v-for="review in product.reviews" :key="review.id" class="review-item">
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
                <el-input
                  v-model="purchaseForm.address"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入详细地址"
                />
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

            <div class="total-section">
              <div class="total-row">
                <span>商品总价</span>
                <span class="total-price">¥{{ totalPrice }}</span>
              </div>
              <div class="total-row">
                <span>运费</span>
                <span class="shipping-fee">{{ shippingFee === 0 ? '免运费' : `¥${shippingFee}` }}</span>
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

// 产品详情数据
const product = ref({
  id: 1,
  name: '巴南银针茶',
  badge: '地理标志',
  certified: true,
  price: 128,
  unit: '/盒',
  sales: 2356,
  rating: 4.9,
  origin: '重庆市巴南区',
  mainImage: 'https://picsum.photos/600/600?random=20',
  images: [
    'https://picsum.photos/600/600?random=20',
    'https://picsum.photos/600/600?random=21',
    'https://picsum.photos/600/600?random=22',
    'https://picsum.photos/600/600?random=23'
  ],
  description: '巴南银针茶产自重庆市巴南区海拔800-1200米的高山茶园，常年云雾缭绕，气候温和，土壤肥沃，是茶树生长的理想环境。采用传统手工采摘，只选取清明前的嫩芽，经过杀青、揉捻、干燥等十几道工序精心制作而成。茶叶外形挺直如针，色泽翠绿，香气清雅，滋味鲜爽，回甘持久，富含茶多酚、氨基酸等营养成分，具有提神醒脑、降脂减肥等功效。',
  features: [
    '国家地理标志产品',
    '有机种植，无农药残留',
    '手工采摘，品质保证',
    '传统工艺，匠心制作',
    '清香持久，回甘明显',
    '富含营养，健康养生'
  ],
  specifications: [
    { label: '产品名称', value: '巴南银针茶' },
    { label: '产地', value: '重庆巴南' },
    { label: '等级', value: '特级' },
    { label: '净含量', value: '250g' },
    { label: '保质期', value: '18个月' },
    { label: '存储方式', value: '密封、阴凉、干燥' },
    { label: '采摘时间', value: '清明前' },
    { label: '包装', value: '礼盒装' }
  ],
  availableSpecs: [
    { label: '250g 礼盒装', value: '250g' },
    { label: '500g 礼盒装', value: '500g' },
    { label: '1000g 礼盒装', value: '1000g' }
  ],
  reviews: [
    {
      id: 1,
      userName: '茶叶爱好者',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=31',
      rating: 5,
      date: '2024-10-22',
      content: '茶叶品质非常好，香气浓郁，口感清爽。包装精美，送礼自用都很合适。会回购！'
    },
    {
      id: 2,
      userName: '健康生活',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=32',
      rating: 5,
      date: '2024-10-20',
      content: '朋友推荐的，确实不错。茶叶很新鲜，泡出来颜色翠绿，喝了几天感觉很舒服。'
    },
    {
      id: 3,
      userName: '重庆本地人',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=33',
      rating: 5,
      date: '2024-10-18',
      content: '支持家乡特产！巴南银针茶确实好，以前只能在当地买到，现在网上也能买了，方便！'
    }
  ]
})

// 购买表单
const purchaseForm = ref({
  specification: '250g',
  quantity: 1,
  receiverName: '',
  receiverPhone: '',
  address: '',
  notes: ''
})

// 计算总价
const totalPrice = computed(() => {
  let price = product.value.price
  const spec = purchaseForm.value.specification
  
  // 根据规格调整价格
  if (spec === '500g') {
    price = product.value.price * 1.9  // 优惠价
  } else if (spec === '1000g') {
    price = product.value.price * 3.6  // 更优惠
  }
  
  return Math.round(price * purchaseForm.value.quantity)
})

// 运费（满100免运费）
const shippingFee = computed(() => {
  return totalPrice.value >= 100 ? 0 : 10
})

// 最终价格
const finalPrice = computed(() => {
  return totalPrice.value + shippingFee.value
})

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
const buyNow = () => {
  // 跳转到订单确认页面
  const orderItem = {
    productId: product.value.id,
    name: product.value.name,
    image: product.value.mainImage,
    price: product.value.price,
    specification: purchaseForm.value.specification,
    quantity: purchaseForm.value.quantity
  }
  
  router.push({
    path: '/home/user/culture/order',
    query: {
      items: JSON.stringify([orderItem])
    }
  })
}

// 表单验证
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
    ElMessage.warning('请输入收货地址')
    return false
  }
  return true
}

// 页面加载
onMounted(() => {
  const id = route.params.id
  console.log('加载产品详情，ID:', id)
  // TODO: 根据ID从后端加载实际数据
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
  display: flex;
  flex-direction: column;
  gap: 12px;

  :deep(.el-radio) {
    margin: 0;
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


