<template>
  <div class="orders-page">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><List /></el-icon>
        我的订单
      </h1>
    </el-card>

    <!-- 订单筛选 -->
    <el-card class="filter-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane label="待付款" name="pending">
          <template #label>
            待付款
            <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane label="待发货" name="paid" />
        <el-tab-pane label="待收货" name="shipped" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>
    </el-card>

    <!-- 订单列表 -->
    <div v-if="filteredOrders.length === 0" class="empty-orders">
      <el-empty description="暂无订单">
        <el-button type="primary" @click="goShopping">去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else class="orders-list">
      <el-card v-for="order in filteredOrders" :key="order.id" class="order-card">
        <!-- 订单头部 -->
        <div class="order-header">
          <div class="header-left">
            <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
            <span class="order-no">订单号：{{ order.orderNo }}</span>
          </div>
          <el-tag :type="getStatusType(order.status)" size="large">
            {{ getStatusText(order.status) }}
          </el-tag>
        </div>

        <el-divider />

        <!-- 订单商品 -->
        <div class="order-goods">
          <div
            v-for="item in order.items"
            :key="item.id"
            class="goods-item"
            @click="goToProduct(item.productId)"
          >
            <el-image :src="item.image" fit="cover" class="goods-image" />
            <div class="goods-info">
              <p class="goods-name">{{ item.name }}</p>
              <p class="goods-spec">规格：{{ item.specification }}</p>
              <p class="goods-price">¥{{ item.price }} x {{ item.quantity }}</p>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 订单信息 -->
        <div class="order-info">
          <div class="info-row">
            <span class="label">收货人：</span>
            <span>{{ order.receiver }} {{ order.phone }}</span>
          </div>
          <div class="info-row">
            <span class="label">收货地址：</span>
            <span>{{ order.address }}</span>
          </div>
          <div class="info-row total">
            <span class="label">订单金额：</span>
            <span class="amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 订单操作 -->
        <div class="order-actions">
          <el-button v-if="order.status === 'pending'" type="danger" @click="handlePay(order)">
            立即支付
          </el-button>
          <el-button v-if="order.status === 'pending'" @click="handleCancel(order)">
            取消订单
          </el-button>
          <el-button v-if="order.status === 'shipped'" type="primary" @click="handleConfirm(order)">
            确认收货
          </el-button>
          <el-button v-if="order.status === 'completed'" @click="handleComment(order)">
            评价
          </el-button>
          <el-button @click="handleViewDetail(order)">查看详情</el-button>
          <el-button v-if="order.status === 'completed'" type="danger" link @click="handleDelete(order)">
            删除订单
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div v-if="filteredOrders.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import { List } from '@element-plus/icons-vue'

const router = useRouter()

// 当前标签页
const activeTab = ref('all')

// 订单列表（模拟数据）
const orders = ref([
  {
    id: 1,
    orderNo: '202410290001',
    createTime: '2024-10-29 14:30:25',
    status: 'pending',
    items: [
      {
        id: 1,
        productId: 1,
        name: '巴南银针茶',
        image: 'https://picsum.photos/80/80?random=20',
        specification: '250g/盒',
        price: 128,
        quantity: 2
      }
    ],
    receiver: '张三',
    phone: '138****8888',
    address: '重庆市渝中区解放碑步行街100号',
    totalAmount: 256
  },
  {
    id: 2,
    orderNo: '202410280002',
    createTime: '2024-10-28 10:15:30',
    status: 'shipped',
    items: [
      {
        id: 2,
        productId: 2,
        name: '江津花椒',
        image: 'https://picsum.photos/80/80?random=21',
        specification: '500g/袋',
        price: 68,
        quantity: 3
      },
      {
        id: 3,
        productId: 3,
        name: '手工竹编',
        image: 'https://picsum.photos/80/80?random=22',
        specification: '中号',
        price: 158,
        quantity: 1
      }
    ],
    receiver: '李四',
    phone: '139****9999',
    address: '重庆市江北区观音桥步行街200号',
    totalAmount: 362
  },
  {
    id: 3,
    orderNo: '202410270003',
    createTime: '2024-10-27 16:45:12',
    status: 'completed',
    items: [
      {
        id: 4,
        productId: 4,
        name: '涪陵榨菜',
        image: 'https://picsum.photos/80/80?random=23',
        specification: '300g/袋',
        price: 45,
        quantity: 5
      }
    ],
    receiver: '张三',
    phone: '138****8888',
    address: '重庆市渝中区解放碑步行街100号',
    totalAmount: 225
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredOrders.value.length)

// 待付款数量
const pendingCount = computed(() => orders.value.filter(o => o.status === 'pending').length)

// 过滤后的订单
const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeTab.value)
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    paid: 'info',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

// 标签页切换
const handleTabChange = (tab) => {
  console.log('切换到标签:', tab)
  currentPage.value = 1
}

// 支付订单
const handlePay = (order) => {
  console.log('点击立即支付，订单ID:', order.id)
  // 跳转到订单详情页进行支付
  router.push(`/home/user/culture/order-detail/${order.id}`)
    .then(() => {
      console.log('路由跳转成功')
    })
    .catch(err => {
      console.error('路由跳转失败:', err)
    })
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // TODO: 调用取消订单接口
    console.log('取消订单:', order.id)
    
    ElMessage.success('订单已取消')
    order.status = 'cancelled'
  } catch {
    // 用户取消
  }
}

// 确认收货
const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到货物？', '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })

    // TODO: 调用确认收货接口
    console.log('确认收货:', order.id)
    
    ElMessage.success('确认收货成功')
    order.status = 'completed'
  } catch {
    // 用户取消
  }
}

// 评价订单
const handleComment = (order) => {
  ElMessage.info('评价功能开发中...')
}

// 查看详情
const handleViewDetail = (order) => {
  console.log('点击查看详情，订单ID:', order.id)
  router.push(`/home/user/culture/order-detail/${order.id}`)
    .then(() => {
      console.log('路由跳转成功')
    })
    .catch(err => {
      console.error('路由跳转失败:', err)
    })
}

// 删除订单
const handleDelete = async (order) => {
  try {
    await ElMessageBox.confirm('确定要删除这个订单吗？删除后无法恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const index = orders.value.findIndex(o => o.id === order.id)
    if (index > -1) {
      orders.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 跳转到商品详情
const goToProduct = (id) => {
  router.push(`/home/user/culture/product/${id}`)
}

// 去逛逛
const goShopping = () => {
  router.push('/home/user/culture')
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
}
</script>

<style scoped lang="scss">
.orders-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;

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
}

.filter-card {
  margin-bottom: 24px;

  :deep(.el-tabs__item) {
    font-size: 16px;
  }

  .tab-badge {
    margin-left: 8px;
  }
}

.empty-orders {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      gap: 24px;
      align-items: center;

      .order-time {
        font-size: 14px;
        color: #606266;
      }

      .order-no {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .order-goods {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .goods-item {
      display: flex;
      gap: 16px;
      cursor: pointer;
      padding: 12px;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        background: #f5f7fa;
      }

      .goods-image {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        flex-shrink: 0;
      }

      .goods-info {
        flex: 1;

        .goods-name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 8px 0;
        }

        .goods-spec,
        .goods-price {
          font-size: 14px;
          color: #909399;
          margin: 4px 0;
        }

        .goods-price {
          color: #f56c6c;
          font-weight: 600;
        }
      }
    }
  }

  .order-info {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .info-row {
      display: flex;
      font-size: 14px;
      color: #606266;

      .label {
        min-width: 80px;
        color: #909399;
      }

      &.total {
        margin-top: 8px;
        font-size: 16px;
        font-weight: 700;

        .amount {
          color: #f56c6c;
          font-size: 20px;
        }
      }
    }
  }

  .order-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>

