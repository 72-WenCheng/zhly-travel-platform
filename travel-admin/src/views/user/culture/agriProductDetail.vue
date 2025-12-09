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
                <span class="unit">{{ product.unit }}</span>
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

              <el-form-item label="收货地址">
                <el-input
                  v-model="purchaseForm.address"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入详细收货地址"
                />
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

            <div class="total-box">
              <div class="total-row">
                <span>商品总价</span>
                <span class="price">¥{{ totalPrice }}</span>
              </div>
              <div class="total-row">
                <span>运费</span>
                <span class="shipping">{{ shippingFee === 0 ? '免运费' : `¥${shippingFee}` }}</span>
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

const route = useRoute()
const router = useRouter()

const product = ref({
  id: 1,
  name: '云雾绿茶礼盒',
  category: '农特产品 / 茶叶',
  price: 168,
  unit: '/盒',
  sales: 1560,
  stock: 320,
  viewCount: 3086,
  rating: 4.8,
  reviewCount: 86,
  certified: true,
  origin: '重庆市忠县',
  shelfLife: '18个月',
  shippingDesc: '全场满99免邮',
  badges: ['手工制茶', '当季新茶'],
  tags: ['高山茶园', '原产地发货', '可定制伴手礼'],
  mainImage: 'https://picsum.photos/800/600?random=501',
  images: [
    'https://picsum.photos/800/600?random=501',
    'https://picsum.photos/800/600?random=502',
    'https://picsum.photos/800/600?random=503',
    'https://picsum.photos/800/600?random=504',
    'https://picsum.photos/800/600?random=505'
  ],
  description:
    '精选重庆高山云雾茶园春茶嫩芽，经传统手工工艺杀青、揉捻、干燥而成。茶汤清透翠绿，香气清扬持久，滋味鲜爽回甘。礼盒包装大方雅致，送礼自饮皆宜。',
  features: [
    { icon: Medal, title: '高山云雾', description: '海拔900米云雾茶园，昼夜温差大，香气更足' },
    { icon: Brush, title: '手工采摘', description: '一芽一叶，人工精选，确保芽叶完整' },
    { icon: Discount, title: '地理标志', description: '获得地理标志认证，品质有保障' },
    { icon: Present, title: '礼盒定制', description: '支持企业/团体定制专属包装与贺卡' }
  ],
  specifications: [
    { label: '产品名称', value: '云雾绿茶礼盒' },
    { label: '净含量', value: '250g/盒' },
    { label: '等级', value: '特级' },
    { label: '产地', value: '重庆忠县' },
    { label: '采摘时间', value: '清明前' },
    { label: '加工工艺', value: '手工杀青/低温烘焙' },
    { label: '包装', value: '礼盒装（含礼袋）' },
    { label: '贮存方式', value: '密封、避光、干燥、低温' }
  ],
  traceability: [
    { time: '2024-03-18', title: '春茶开采', detail: '早春头采，人工采摘单芽' },
    { time: '2024-03-19', title: '工艺制作', detail: '杀青、揉捻、理条、烘焙，全程低温锁香' },
    { time: '2024-03-21', title: '质检入库', detail: '通过农残、重金属检测，批次编号CQ-0321' },
    { time: '2024-03-22', title: '包装发货', detail: '恒温仓储，48小时内发出' }
  ],
  availableSpecs: [
    { label: '250g 礼盒装', value: '250g' },
    { label: '500g 礼盒装', value: '500g' },
    { label: '1000g 商务礼盒', value: '1000g' }
  ],
  reviews: [
    {
      id: 1,
      userName: '茶友A',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=tea1',
      rating: 5,
      date: '2024-10-12',
      content: '茶香很纯正，回甘快，礼盒质感也很高级，客户很喜欢。'
    },
    {
      id: 2,
      userName: '山城旅人',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=tea2',
      rating: 4.5,
      date: '2024-10-05',
      content: '包装完好，茶叶很新鲜，冲泡三泡后依然有香气。'
    },
    {
      id: 3,
      userName: '茶语者',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=tea3',
      rating: 5,
      date: '2024-09-21',
      content: '下单第二天就收到，客服还帮忙印了定制贺卡，体验不错。'
    }
  ]
})

const purchaseForm = ref({
  specification: '250g',
  quantity: 1,
  address: '',
  notes: ''
})

const totalPrice = computed(() => {
  let base = product.value.price
  const spec = purchaseForm.value.specification

  if (spec === '500g') base = product.value.price * 1.9
  if (spec === '1000g') base = product.value.price * 3.6

  return Math.round(base * purchaseForm.value.quantity)
})

const shippingFee = computed(() => (totalPrice.value >= 99 ? 0 : 12))
const finalPrice = computed(() => totalPrice.value + shippingFee.value)

const addToCart = () => {
  const cartItem = {
    productId: product.value.id,
    name: product.value.name,
    image: product.value.mainImage,
    price: product.value.price,
    specification: purchaseForm.value.specification,
    quantity: purchaseForm.value.quantity
  }

  console.log('加入购物车', cartItem)
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  if (!validateForm()) return

  const orderItem = {
    productId: product.value.id,
    name: product.value.name,
    image: product.value.mainImage,
    price: product.value.price,
    specification: purchaseForm.value.specification,
    quantity: purchaseForm.value.quantity,
    address: purchaseForm.value.address,
    notes: purchaseForm.value.notes
  }

  router.push({
    path: '/home/user/culture/order',
    query: { items: JSON.stringify([orderItem]) }
  })
}

const validateForm = () => {
  if (!purchaseForm.value.address) {
    ElMessage.warning('请填写收货地址')
    return false
  }
  return true
}

onMounted(() => {
  const id = route.params.id
  console.log('加载农特产品详情，ID:', id)
  // TODO: 通过ID请求后端获取真实数据
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
</style>

