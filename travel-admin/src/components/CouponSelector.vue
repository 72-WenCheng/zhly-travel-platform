<template>
  <div class="coupon-selector">
    <el-form-item label="优惠券" class="coupon-form-item">
      <div class="coupon-display" @click="showCouponDialog = true">
        <div v-if="selectedCoupon" class="selected-coupon coupon-item">
          <div class="coupon-left">
            <div class="coupon-amount">
              <span class="symbol">¥</span>
              <span class="value">{{ selectedCoupon.discountValue || selectedCoupon.amount }}</span>
            </div>
            <div class="coupon-condition">满{{ selectedCoupon.minAmount }}元可用</div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ selectedCoupon.name || selectedCoupon.couponName }}</div>
            <div class="coupon-desc">{{ selectedCoupon.description || selectedCoupon.sourceDesc || '等级权益优惠券' }}</div>
          </div>
          <el-icon class="coupon-remove" @click.stop="clearCoupon">
            <Close />
          </el-icon>
        </div>
        <div v-else class="select-coupon-btn">
          <el-icon><Tickets /></el-icon>
          <span>选择优惠券</span>
        </div>
      </div>
    </el-form-item>

    <!-- 优惠券选择弹层（自定义遮罩，确保盖住顶部导航） -->
    <teleport to="body">
      <div
        v-if="showCouponDialog"
        class="coupon-mask"
        @click.self="showCouponDialog = false"
      >
        <div class="coupon-panel coupon-dialog">
          <div class="panel-header">
            <span class="panel-title">选择优惠券</span>
            <el-icon class="panel-close" @click="showCouponDialog = false"><Close /></el-icon>
          </div>

          <div class="coupon-list">
            <div
              v-if="availableCoupons.length > 0 && !hasAvailableCoupon"
              class="coupon-tip-unavailable"
            >
              当前订单金额不足，以下优惠券暂不可用，提升消费金额后即可使用。
            </div>
            <!-- 不使用优惠券 -->
            <div
              class="coupon-item"
              :class="{ 'active': selectedCouponId === null }"
              @click="selectCoupon(null)"
            >
              <div class="coupon-content no-coupon">
                <el-icon><Close /></el-icon>
                <span>不使用优惠券</span>
              </div>
            </div>

            <!-- 优惠券列表 -->
            <div
              v-for="coupon in availableCoupons"
              :key="coupon.id"
              class="coupon-item"
              :class="{ 'active': selectedCouponId === coupon.id, 'disabled': !coupon.available }"
              @click="selectCoupon(coupon)"
            >
              <div class="coupon-left">
                <div class="coupon-amount">
                  <span class="symbol">¥</span>
                  <span class="value">{{ coupon.discountValue || coupon.amount }}</span>
                </div>
                <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
              </div>
              <div class="coupon-right">
                <div class="coupon-name">{{ coupon.name || coupon.couponName }}</div>
                <div class="coupon-desc">{{ coupon.description || coupon.sourceDesc || '等级权益优惠券' }}</div>
                <div class="coupon-expire">有效期至：{{ formatDate(coupon.validEndTime || coupon.expireDate) }}</div>
              </div>
              <div v-if="selectedCouponId === coupon.id" class="coupon-check">
                <el-icon><Check /></el-icon>
              </div>
              <div v-if="!coupon.available" class="coupon-unavailable">不可用</div>
            </div>

            <div v-if="availableCoupons.length === 0" class="empty-coupon">
              <el-empty description="暂无可用优惠券">
                <el-button type="primary" @click="goToLevelGuide">
                  查看升级指南
                </el-button>
              </el-empty>
            </div>
          </div>

          <div class="panel-footer">
            <el-button @click="showCouponDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmSelect">确定</el-button>
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Tickets, Close, Check } from '@element-plus/icons-vue'
import request from '@/utils/request'

interface Props {
  modelValue?: any
  orderAmount?: number
}

interface Emits {
  (e: 'update:modelValue', value: any): void
  (e: 'change', value: any): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: null,
  orderAmount: 0
})

const emit = defineEmits<Emits>()

const router = useRouter()
const showCouponDialog = ref(false)
const availableCoupons = ref<any[]>([])
const hasAvailableCoupon = computed(() => {
  return availableCoupons.value.some(coupon => coupon.available)
})
const selectedCouponId = ref<number | null>(null)
const selectedCoupon = computed(() => {
  if (!selectedCouponId.value) return null
  return availableCoupons.value.find(c => c.id === selectedCouponId.value) || null
})

// 格式化日期
const formatDate = (date: string | Date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

// 加载可用优惠券
const loadAvailableCoupons = async () => {
  try {
    const response = await request.get('/user/coupon/available', {
      params: {
        orderAmount: props.orderAmount || 0
      }
    })
    
    if (response.code === 200 && response.data) {
      const coupons = response.data.list || []
      availableCoupons.value = coupons.map((coupon: any) => {
        const rawSource = coupon.sourceDesc || ''
        let prettySource = rawSource || '系统发放'
        if (rawSource.includes('管理端')) {
          prettySource = '系统发放'
        } else if (rawSource.includes('升级') || rawSource.includes('等级')) {
          prettySource = '等级发放'
        }

        return {
          id: coupon.id,
          name: coupon.couponName || '优惠券',
          couponName: coupon.couponName,
          description: prettySource,
          sourceDesc: rawSource,
          amount: coupon.discountValue ? parseFloat(coupon.discountValue) : 0,
          discountValue: coupon.discountValue ? parseFloat(coupon.discountValue) : 0,
          minAmount: coupon.minAmount ? parseFloat(coupon.minAmount) : 0,
          expireDate: coupon.validEndTime ? new Date(coupon.validEndTime).toLocaleDateString('zh-CN') : '',
          validEndTime: coupon.validEndTime,
          available: (props.orderAmount || 0) >= (coupon.minAmount ? parseFloat(coupon.minAmount) : 0)
        }
      })
    }
  } catch (error: any) {
    console.error('加载可用优惠券失败:', error)
    availableCoupons.value = []
  }
}

// 选择优惠券
const selectCoupon = (coupon: any) => {
  if (coupon && !coupon.available) return
  selectedCouponId.value = coupon ? coupon.id : null
}

// 确认选择
const confirmSelect = () => {
  const coupon = selectedCoupon.value
  emit('update:modelValue', coupon)
  emit('change', coupon)
  showCouponDialog.value = false
}

// 清除优惠券
const clearCoupon = () => {
  selectedCouponId.value = null
  emit('update:modelValue', null)
  emit('change', null)
}

// 跳转到升级指南
const goToLevelGuide = () => {
  router.push('/home/user/level-guide')
  showCouponDialog.value = false
}

// 监听订单金额变化，更新可用状态
watch(() => props.orderAmount, (newAmount) => {
  availableCoupons.value = availableCoupons.value.map(coupon => ({
    ...coupon,
    available: newAmount >= coupon.minAmount
  }))
  
  // 如果当前选中的优惠券不可用，清除选择
  if (selectedCoupon.value && !selectedCoupon.value.available) {
    clearCoupon()
  }
})

// 监听外部传入的选中值
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    selectedCouponId.value = newValue.id
  } else {
    selectedCouponId.value = null
  }
}, { immediate: true })

// 监听对话框打开，加载优惠券 + 控制全局样式
watch(showCouponDialog, (isOpen) => {
  if (isOpen) {
    loadAvailableCoupons()
    document.body.classList.add('coupon-dialog-open')
  } else {
    document.body.classList.remove('coupon-dialog-open')
  }
})

onMounted(() => {
  if (props.modelValue) {
    selectedCouponId.value = props.modelValue.id
  }
})
</script>

<style scoped lang="scss">
.coupon-selector {
  .coupon-form-item {
    margin-bottom: 0;
  }

  .coupon-display {
    cursor: pointer;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 8px 12px;
    transition: all 0.3s;
    background-color: #fafbff;
    max-width: 380px;
    margin-bottom: 8px;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.15);
      background-color: #ffffff;
    }

    .selected-coupon {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      position: relative;

      .coupon-left {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-width: 110px;

        .coupon-amount {
          display: flex;
          align-items: baseline;
          color: #f56c6c;
          font-weight: 700;

          .symbol {
            font-size: 14px;
          }

          .value {
            font-size: 24px;
          }
        }

        .coupon-condition {
          font-size: 12px;
          color: #909399;
          margin-top: 2px;
        }
      }

      .coupon-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 2px;

        .coupon-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          line-height: 1.2;
        }

        .coupon-desc {
          font-size: 12px;
          color: #909399;
        }
      }

      .coupon-remove {
        color: #909399;
        cursor: pointer;
        padding: 4px;
        transition: color 0.3s;

        &:hover {
          color: #f56c6c;
        }
      }
    }

    .select-coupon-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #606266;
      font-size: 14px;

      .el-icon {
        font-size: 18px;
      }
    }
  }
}

.coupon-dialog {
  .coupon-list {
    max-height: 500px;
    overflow-y: auto;
    padding: 16px 20px 12px;

    .coupon-item {
      position: relative;
      margin: 0 0 12px;
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      padding: 12px 14px;
      cursor: pointer;
      transition: all 0.3s;
      background: #fff;
      display: flex;
      align-items: center;
      gap: 16px;

      &:hover:not(.disabled) {
        border-color: #409eff;
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
      }

      &.active {
        border-color: #409eff;
        background: #ecf5ff;
      }

      &.disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }

      .no-coupon {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #909399;
        font-size: 14px;
      }

      .coupon-left {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-width: 110px;
        margin-right: 4px;

        .coupon-amount {
          display: flex;
          align-items: baseline;
          color: #f56c6c;
          font-weight: 700;

          .symbol {
            font-size: 16px;
          }

          .value {
            font-size: 32px;
          }
        }

        .coupon-condition {
          font-size: 12px;
          color: #909399;
          margin-top: 4px;
        }
      }

      .coupon-right {
        flex: 1;

        .coupon-name {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 6px;
        }

        .coupon-desc {
          font-size: 13px;
          color: #606266;
          margin-bottom: 4px;
        }

        .coupon-expire {
          font-size: 12px;
          color: #909399;
        }
      }

      .coupon-check {
        position: absolute;
        top: 16px;
        right: 16px;
        color: #409eff;
        font-size: 20px;
      }

      .coupon-unavailable {
        position: absolute;
        top: 16px;
        right: 16px;
        background: #f56c6c;
        color: #fff;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
      }
    }

    .empty-coupon {
      padding: 40px 0;
    }
  }
}

:global(.coupon-overlay) {
  z-index: 4000 !important;
}

.coupon-mask {
  position: fixed;
  inset: 0;
  z-index: 5000;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.coupon-panel {
  width: 640px;
  max-width: 95vw;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.22);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;

  .panel-title {
    font-weight: 600;
    font-size: 16px;
    color: #303133;
  }

  .panel-close {
    cursor: pointer;
    color: #909399;
    &:hover { color: #f56c6c; }
  }
}

.panel-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 12px 20px 16px;
  border-top: 1px solid #ebeef5;
}

.coupon-tip-unavailable {
  margin: 4px 4px 12px;
  padding: 6px 10px;
  border-radius: 6px;
  background: #fff7e6;
  color: #d48806;
  font-size: 12px;
}
</style>








