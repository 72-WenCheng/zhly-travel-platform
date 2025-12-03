<template>
  <div class="shopping-cart">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="cart-header">
      <div class="header-content">
        <h1>
          <el-icon><ShoppingCart /></el-icon>
          购物车
        </h1>
        <span class="item-count">共 {{ cartItems.length }} 件商品</span>
      </div>
    </el-card>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="goShopping">去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else class="cart-content">
      <el-row :gutter="24">
        <!-- 左侧：商品列表 -->
        <el-col :span="16">
          <el-card class="cart-items">
            <div class="select-all">
              <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
              <el-button link type="danger" @click="handleDeleteSelected" :disabled="selectedItems.length === 0">
                删除选中商品
              </el-button>
            </div>

            <el-divider />

            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <el-checkbox v-model="item.selected" @change="handleSelectItem"></el-checkbox>
              
              <div class="item-image" @click="goToProduct(item.productId)">
                <el-image :src="item.image" fit="cover" />
              </div>

              <div class="item-info">
                <h3 class="item-name" @click="goToProduct(item.productId)">{{ item.name }}</h3>
                <p class="item-spec">规格：{{ item.specification }}</p>
                <p class="item-origin">产地：{{ item.origin }}</p>
              </div>

              <div class="item-price">
                <span class="current-price">¥{{ item.price }}</span>
                <span class="unit">{{ item.unit }}</span>
              </div>

              <div class="item-quantity">
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  :max="999"
                  @change="handleQuantityChange(item)"
                />
              </div>

              <div class="item-subtotal">
                <span class="subtotal-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>

              <div class="item-actions">
                <el-button link type="primary" @click="handleMoveToFavorites(item)">移入收藏</el-button>
                <el-button link type="danger" @click="handleDelete(item)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：结算信息 -->
        <el-col :span="8">
          <div class="settlement-sticky">
            <el-card class="settlement-card">
              <h3 class="settlement-title">
                <el-icon><Wallet /></el-icon>
                结算信息
              </h3>

              <div class="settlement-detail">
                <div class="detail-row">
                  <span>商品件数</span>
                  <span>{{ selectedCount }} 件</span>
                </div>
                <div class="detail-row">
                  <span>商品总价</span>
                  <span>¥{{ totalPrice.toFixed(2) }}</span>
                </div>
                <div class="detail-row">
                  <span>运费</span>
                  <span class="free-shipping">{{ shippingFee === 0 ? '免运费' : `¥${shippingFee}` }}</span>
                </div>
                <div class="detail-row discount">
                  <span>优惠</span>
                  <span>-¥{{ discount.toFixed(2) }}</span>
                </div>
                <el-divider />
                <div class="detail-row total">
                  <span>应付总额</span>
                  <span class="total-amount">¥{{ finalAmount.toFixed(2) }}</span>
                </div>
              </div>

              <el-button
                type="danger"
                size="large"
                class="checkout-btn"
                :disabled="selectedItems.length === 0"
                @click="handleCheckout"
              >
                <el-icon><Checked /></el-icon>
                结算 ({{ selectedCount }})
              </el-button>

              <div class="service-promise">
                <div class="promise-item">
                  <el-icon color="#67c23a"><CircleCheck /></el-icon>
                  <span>7天无理由退货</span>
                </div>
                <div class="promise-item">
                  <el-icon color="#409eff"><Van /></el-icon>
                  <span>满99元包邮</span>
                </div>
                <div class="promise-item">
                  <el-icon color="#f56c6c"><Medal /></el-icon>
                  <span>正品保证</span>
                </div>
              </div>
            </el-card>

            <!-- 推荐商品 -->
            <el-card class="recommend-card">
              <h3 class="recommend-title">猜你喜欢</h3>
              <div v-for="item in recommendItems" :key="item.id" class="recommend-item" @click="goToProduct(item.id)">
                <el-image :src="item.image" fit="cover" class="recommend-image" />
                <div class="recommend-info">
                  <p class="recommend-name">{{ item.name }}</p>
                  <span class="recommend-price">¥{{ item.price }}</span>
                </div>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import {
  ShoppingCart,
  Wallet,
  Checked,
  CircleCheck,
  Van,
  Medal
} from '@element-plus/icons-vue'

const router = useRouter()

// 购物车商品列表（模拟数据）
const cartItems = ref([
  {
    id: 1,
    productId: 1,
    name: '巴南银针茶',
    image: 'https://picsum.photos/100/100?random=20',
    origin: '重庆巴南',
    price: 128,
    unit: '/盒',
    specification: '250g/盒',
    quantity: 2,
    selected: true
  },
  {
    id: 2,
    productId: 2,
    name: '江津花椒',
    image: 'https://picsum.photos/100/100?random=21',
    origin: '重庆江津',
    price: 68,
    unit: '/袋',
    specification: '500g/袋',
    quantity: 1,
    selected: true
  },
  {
    id: 3,
    productId: 3,
    name: '手工竹编',
    image: 'https://picsum.photos/100/100?random=22',
    origin: '重庆梁平',
    price: 158,
    unit: '/个',
    specification: '中号',
    quantity: 1,
    selected: false
  }
])

// 推荐商品
const recommendItems = ref([
  {
    id: 4,
    name: '涪陵榨菜',
    image: 'https://picsum.photos/80/80?random=23',
    price: 45
  },
  {
    id: 5,
    name: '石柱蜂蜜',
    image: 'https://picsum.photos/80/80?random=24',
    price: 98
  },
  {
    id: 6,
    name: '土家织锦',
    image: 'https://picsum.photos/80/80?random=25',
    price: 368
  }
])

// 全选状态
const selectAll = computed({
  get: () => cartItems.value.every(item => item.selected),
  set: (val) => {
    cartItems.value.forEach(item => item.selected = val)
  }
})

// 已选中商品
const selectedItems = computed(() => cartItems.value.filter(item => item.selected))

// 已选中数量
const selectedCount = computed(() => selectedItems.value.reduce((sum, item) => sum + item.quantity, 0))

// 商品总价
const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 运费（满99免运费）
const shippingFee = computed(() => {
  return totalPrice.value >= 99 ? 0 : 10
})

// 优惠金额
const discount = computed(() => {
  // 满200减20
  if (totalPrice.value >= 200) return 20
  return 0
})

// 应付总额
const finalAmount = computed(() => {
  return totalPrice.value + shippingFee.value - discount.value
})

// 全选/取消全选
const handleSelectAll = () => {
  cartItems.value.forEach(item => item.selected = selectAll.value)
}

// 单选
const handleSelectItem = () => {
  // 更新全选状态
}

// 数量变化
const handleQuantityChange = (item) => {
  console.log('数量变化:', item)
  // TODO: 调用后端API更新数量
}

// 删除商品
const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = cartItems.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      cartItems.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 删除选中商品
const handleDeleteSelected = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    cartItems.value = cartItems.value.filter(item => !item.selected)
    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

// 移入收藏
const handleMoveToFavorites = (item) => {
  ElMessage.success('已移入收藏')
  handleDelete(item)
}

// 去逛逛
const goShopping = () => {
  router.push('/home/user/culture')
}

// 跳转到商品详情
const goToProduct = (id) => {
  router.push(`/home/user/culture/product/${id}`)
}

// 结算
const handleCheckout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  // 跳转到订单确认页面
  router.push({
    path: '/home/user/culture/order',
    query: {
      items: JSON.stringify(selectedItems.value.map(item => ({
        productId: item.productId,
        name: item.name,
        image: item.image,
        price: item.price,
        quantity: item.quantity,
        specification: item.specification
      })))
    }
  })
}
</script>

<style scoped lang="scss">
.shopping-cart {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.cart-header {
  margin-bottom: 24px;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;

    h1 {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 28px;
      font-weight: 700;
      color: #303133;
      margin: 0;

      .el-icon {
        font-size: 32px;
        color: #409eff;
      }
    }

    .item-count {
      font-size: 16px;
      color: #909399;
    }
  }
}

.empty-cart {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;
}

.cart-content {
  .cart-items {
    margin-bottom: 24px;

    .select-all {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .cart-item {
      display: flex;
      align-items: center;
      gap: 20px;
      padding: 20px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .item-image {
        width: 100px;
        height: 100px;
        cursor: pointer;
        border-radius: 8px;
        overflow: hidden;

        :deep(.el-image) {
          width: 100%;
          height: 100%;
        }

        &:hover {
          opacity: 0.8;
        }
      }

      .item-info {
        flex: 1;
        min-width: 200px;

        .item-name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 8px 0;
          cursor: pointer;

          &:hover {
            color: #409eff;
          }
        }

        .item-spec,
        .item-origin {
          font-size: 14px;
          color: #909399;
          margin: 4px 0;
        }
      }

      .item-price {
        width: 100px;
        text-align: center;

        .current-price {
          font-size: 18px;
          font-weight: 600;
          color: #f56c6c;
        }

        .unit {
          font-size: 12px;
          color: #909399;
          margin-left: 4px;
        }
      }

      .item-quantity {
        width: 120px;
      }

      .item-subtotal {
        width: 100px;
        text-align: center;

        .subtotal-price {
          font-size: 18px;
          font-weight: 700;
          color: #f56c6c;
        }
      }

      .item-actions {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
    }
  }
}

.settlement-sticky {
  position: sticky;
  top: 24px;
}

.settlement-card {
  margin-bottom: 24px;

  .settlement-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 20px 0;
  }

  .settlement-detail {
    .detail-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      font-size: 14px;

      &.discount span:last-child {
        color: #f56c6c;
      }

      &.total {
        font-size: 16px;
        font-weight: 700;

        .total-amount {
          font-size: 24px;
          color: #f56c6c;
        }
      }
    }

    .free-shipping {
      color: #67c23a;
    }
  }

  .checkout-btn {
    width: 100%;
    margin-top: 20px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
  }

  .service-promise {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;

    .promise-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #606266;
    }
  }
}

.recommend-card {
  .recommend-title {
    font-size: 16px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 16px 0;
  }

  .recommend-item {
    display: flex;
    gap: 12px;
    padding: 12px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    .recommend-image {
      width: 60px;
      height: 60px;
      border-radius: 6px;
    }

    .recommend-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .recommend-name {
        font-size: 14px;
        color: #303133;
        margin: 0 0 8px 0;
      }

      .recommend-price {
        font-size: 16px;
        font-weight: 600;
        color: #f56c6c;
      }
    }
  }
}
</style>



