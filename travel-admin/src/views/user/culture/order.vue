<template>
  <div class="order-confirm">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Document /></el-icon>
        确认订单
      </h1>
    </el-card>

    <el-row :gutter="24">
      <!-- 左侧：订单信息 -->
      <el-col :span="16">
        <!-- 收货地址 -->
        <el-card class="address-card">
          <div class="card-header">
            <h3>
              <el-icon><Location /></el-icon>
              收货地址
            </h3>
            <el-button link type="primary" @click="addressDialogVisible = true">
              <el-icon><Plus /></el-icon>
              添加新地址
            </el-button>
          </div>

          <div v-if="addresses.length === 0" class="no-address">
            <el-empty description="暂无收货地址">
              <el-button type="primary" @click="addressDialogVisible = true">添加地址</el-button>
            </el-empty>
          </div>

          <el-radio-group v-else v-model="selectedAddressId" class="address-list">
            <div
              v-for="address in addresses"
              :key="address.id"
              class="address-item"
              :class="{ 'active': selectedAddressId === address.id }"
              @click="selectedAddressId = address.id"
            >
              <div class="address-radio-wrapper">
                <el-radio :label="address.id" />
              </div>
              
              <div class="address-info">
                <!-- 第一行：姓名、电话、默认标签 -->
                <div class="info-row">
                  <span class="name">{{ address.name }}</span>
                  <span class="phone">{{ address.phone }}</span>
                  <el-tag v-if="address.isDefault" type="danger" size="small">默认</el-tag>
                </div>
                
                <!-- 第二行：详细地址 -->
                <div class="address-row">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
                </div>
                
                <!-- 第三行：操作按钮 -->
                <div class="actions-row">
                  <el-button link size="small" type="primary" @click.stop="editAddress(address)">编辑</el-button>
                  <span class="divider">|</span>
                  <el-button link size="small" type="danger" @click.stop="deleteAddress(address)">删除</el-button>
                  <template v-if="!address.isDefault">
                    <span class="divider">|</span>
                    <el-button link size="small" type="primary" @click.stop="setDefaultAddress(address)">设为默认</el-button>
                  </template>
                </div>
              </div>
            </div>
          </el-radio-group>
        </el-card>

        <!-- 商品列表 -->
        <el-card class="goods-card">
          <h3>
            <el-icon><ShoppingBag /></el-icon>
            商品清单
          </h3>

          <div class="goods-header">
            <span class="col-info">商品信息</span>
            <span class="col-price">单价</span>
            <span class="col-quantity">数量</span>
            <span class="col-subtotal">小计</span>
          </div>

          <div v-for="item in orderItems" :key="item.productId" class="goods-item">
            <div class="col-info">
              <el-image :src="item.image" fit="cover" class="goods-image" />
              <div class="goods-info">
                <p class="goods-name">{{ item.name }}</p>
                <p class="goods-spec">规格：{{ item.specification }}</p>
              </div>
            </div>
            <div class="col-price">¥{{ item.price }}</div>
            <div class="col-quantity">x {{ item.quantity }}</div>
            <div class="col-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </el-card>

        <!-- 发票信息 -->
        <el-card class="invoice-card">
          <div class="card-header">
            <h3>
              <el-icon><Tickets /></el-icon>
              发票信息
            </h3>
          </div>

          <div class="invoice-options">
            <div 
              class="invoice-item"
              :class="{ 'active': invoiceType === 'none' }"
              @click="invoiceType = 'none'"
            >
              <div class="invoice-radio-wrapper">
                <el-radio v-model="invoiceType" label="none" />
              </div>
              <div class="invoice-label">不需要发票</div>
            </div>

            <div 
              class="invoice-item"
              :class="{ 'active': invoiceType === 'personal' }"
              @click="invoiceType = 'personal'"
            >
              <div class="invoice-radio-wrapper">
                <el-radio v-model="invoiceType" label="personal" />
              </div>
              <div class="invoice-label">个人发票</div>
            </div>

            <div 
              class="invoice-item"
              :class="{ 'active': invoiceType === 'company' }"
              @click="invoiceType = 'company'"
            >
              <div class="invoice-radio-wrapper">
                <el-radio v-model="invoiceType" label="company" />
              </div>
              <div class="invoice-label">企业发票</div>
            </div>
          </div>

          <div v-if="invoiceType !== 'none'" class="invoice-form">
            <el-form :model="invoiceForm" label-width="100px">
              <el-form-item v-if="invoiceType === 'company'" label="公司名称">
                <el-input v-model="invoiceForm.companyName" placeholder="请输入公司名称" />
              </el-form-item>
              <el-form-item v-if="invoiceType === 'company'" label="税号">
                <el-input v-model="invoiceForm.taxNumber" placeholder="请输入税号" />
              </el-form-item>
              <el-form-item label="发票抬头">
                <el-input v-model="invoiceForm.title" placeholder="请输入发票抬头" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="invoiceForm.email" placeholder="电子发票将发送到此邮箱" />
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- 订单备注 -->
        <el-card class="notes-card">
          <h3>
            <el-icon><Edit /></el-icon>
            订单备注
          </h3>
          <el-input
            v-model="orderNotes"
            type="textarea"
            :rows="3"
            placeholder="选填，如有特殊要求请在此处备注"
            maxlength="200"
            show-word-limit
          />
        </el-card>
      </el-col>

      <!-- 右侧：订单结算 -->
      <el-col :span="8">
        <div class="settlement-sticky">
          <el-card class="settlement-card">
            <h3 class="settlement-title">
              <el-icon><Wallet /></el-icon>
              订单总额
            </h3>

            <div class="settlement-details">
              <div class="detail-row">
                <span class="label">商品总价</span>
                <span class="value">¥{{ goodsTotal.toFixed(2) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">运费</span>
                <span class="value shipping">{{ shippingFee === 0 ? '免运费' : `¥${shippingFee}` }}</span>
              </div>
              <div class="detail-row">
                <span class="label">优惠券</span>
                <span class="value discount" @click="showCouponDialog">
                  {{ couponDiscount > 0 ? `-¥${couponDiscount.toFixed(2)}` : '选择优惠券' }}
                  <el-icon><ArrowRight /></el-icon>
                </span>
              </div>
              <el-divider />
              <div class="detail-row total">
                <span class="label">应付总额</span>
                <span class="final-price">¥{{ finalTotal.toFixed(2) }}</span>
              </div>
            </div>

            <el-button
              type="danger"
              size="large"
              class="submit-btn"
              :loading="submitting"
              :disabled="!selectedAddressId"
              @click="submitOrder"
            >
              <template v-if="!submitting">
                <el-icon><Checked /></el-icon>
                确认提交订单
              </template>
              <template v-else>
                提交中...
              </template>
            </el-button>

            <div v-if="!selectedAddressId" class="submit-warning">
              <el-icon><InfoFilled /></el-icon>
              <span>请先选择收货地址</span>
            </div>

            <div class="submit-tips">
              <el-icon><InfoFilled /></el-icon>
              <span>提交订单即表示同意<el-link type="primary" :underline="false" @click="agreementDialogVisible = true">《购买协议》</el-link></span>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>

    <!-- 地址编辑对话框 -->
    <el-dialog
      v-model="addressDialogVisible"
      :title="editingAddress ? '编辑地址' : '添加地址'"
      width="600px"
    >
      <el-form :model="addressForm" label-width="100px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择省市区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>

    <!-- 优惠券选择对话框 -->
    <el-dialog
      v-model="couponDialogVisible"
      title="选择优惠券"
      width="600px"
      class="coupon-dialog"
    >
      <div class="coupon-list">
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
          v-for="coupon in availableCouponsComputed" 
          :key="coupon.id"
          class="coupon-item"
          :class="{ 'active': selectedCouponId === coupon.id, 'disabled': !coupon.available }"
          @click="selectCoupon(coupon)"
        >
          <div class="coupon-left">
            <div class="coupon-amount">
              <span class="symbol">¥</span>
              <span class="value">{{ coupon.amount }}</span>
            </div>
            <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.name }}</div>
            <div class="coupon-desc">{{ coupon.description }}</div>
            <div class="coupon-expire">有效期至：{{ coupon.expireDate }}</div>
          </div>
          <div v-if="selectedCouponId === coupon.id" class="coupon-check">
            <el-icon><Check /></el-icon>
          </div>
          <div v-if="!coupon.available" class="coupon-unavailable">不可用</div>
        </div>

        <div v-if="availableCouponsComputed.length === 0" class="empty-coupon">
          <el-empty description="暂无可用优惠券">
            <el-button type="primary" @click="router.push('/home/user/level-guide')">
              查看升级指南
            </el-button>
          </el-empty>
        </div>
      </div>

      <template #footer>
        <el-button @click="couponDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCoupon">确定</el-button>
      </template>
    </el-dialog>

    <!-- 购买协议对话框 -->
    <el-dialog
      v-model="agreementDialogVisible"
      title="购买协议"
      width="700px"
      class="agreement-dialog"
    >
      <div class="agreement-content">
        <h3>文旅产品购买协议</h3>
        
        <section>
          <h4>一、协议总则</h4>
          <p>1.1 本协议是您与文旅对接平台（以下简称"本平台"）之间关于文旅产品购买的法律协议。</p>
          <p>1.2 在您点击"确认提交订单"按钮前，请您务必仔细阅读本协议的全部内容。如果您不同意本协议的任何内容，请不要进行后续操作。</p>
          <p>1.3 您点击"确认提交订单"按钮即视为您已充分阅读、理解并接受本协议的全部内容。</p>
        </section>

        <section>
          <h4>二、产品与服务</h4>
          <p>2.1 本平台提供的文旅产品包括但不限于：文化体验、旅游服务、特色周边商品等。</p>
          <p>2.2 产品信息以页面展示为准，包括产品名称、价格、规格、产地、生产日期等。</p>
          <p>2.3 本平台保留对产品信息进行修改的权利，但会提前通知已下单用户。</p>
        </section>

        <section>
          <h4>三、订单与支付</h4>
          <p>3.1 您提交订单后，本平台将生成订单确认信息，请您仔细核对。</p>
          <p>3.2 订单提交后，请在规定时间内完成支付，逾期订单将自动取消。</p>
          <p>3.3 支付方式包括：支付宝、微信支付、银行卡支付等。</p>
          <p>3.4 发票将在订单完成后的7个工作日内开具并发送至您预留的邮箱。</p>
        </section>

        <section>
          <h4>四、配送与签收</h4>
          <p>4.1 本平台将根据您选择的收货地址安排配送。</p>
          <p>4.2 配送时间因地区而异，一般为3-7个工作日。</p>
          <p>4.3 签收前请仔细检查商品包装及内容，如有破损请拒收并及时联系客服。</p>
          <p>4.4 运费标准：订单金额满99元免运费，不满99元收取10元运费。</p>
        </section>

        <section>
          <h4>五、退换货政策</h4>
          <p>5.1 商品自签收之日起7天内，如无质量问题，支持7天无理由退货（特殊商品除外）。</p>
          <p>5.2 如商品存在质量问题，自签收之日起30天内可申请退换货。</p>
          <p>5.3 退换货商品需保持原包装完整，不影响二次销售。</p>
          <p>5.4 定制商品、体验类产品等特殊商品不支持退换货，购买前请仔细阅读产品说明。</p>
        </section>

        <section>
          <h4>六、用户权利与义务</h4>
          <p>6.1 您有权了解所购买产品的真实情况，包括但不限于产地、规格、质量等。</p>
          <p>6.2 您应当提供真实、准确、完整的个人信息和收货信息。</p>
          <p>6.3 您应当妥善保管账号和密码，因您保管不善造成的损失由您自行承担。</p>
          <p>6.4 您不得利用本平台从事任何违法违规活动。</p>
        </section>

        <section>
          <h4>七、平台责任</h4>
          <p>7.1 本平台承诺所售产品均为正品，如发现假冒伪劣产品，将承担相应法律责任。</p>
          <p>7.2 本平台将保护您的个人信息安全，未经您同意不会向第三方披露。</p>
          <p>7.3 本平台将及时处理您的投诉和建议，保障您的合法权益。</p>
          <p>7.4 因不可抗力导致的服务中断或延迟，本平台不承担责任。</p>
        </section>

        <section>
          <h4>八、争议解决</h4>
          <p>8.1 因本协议产生的争议，双方应友好协商解决。</p>
          <p>8.2 协商不成的，任何一方可向本平台所在地人民法院提起诉讼。</p>
        </section>

        <section>
          <h4>九、其他条款</h4>
          <p>9.1 本协议的解释权归本平台所有。</p>
          <p>9.2 本平台有权根据需要修改本协议，修改后的协议将在平台上公示。</p>
          <p>9.3 如本协议的任何条款被认定为无效，不影响其他条款的效力。</p>
        </section>

        <section class="agreement-footer">
          <p><strong>客服电话：</strong>13977531577</p>
          <p><strong>客服邮箱：</strong>service@wenl.com</p>
          <p><strong>工作时间：</strong>周一至周日 9:00-21:00</p>
        </section>
      </div>

      <template #footer>
        <el-button type="primary" @click="agreementDialogVisible = false">我已阅读</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { regionData } from '@/utils/regionData'
import {
  Document,
  Location,
  ShoppingBag,
  Tickets,
  Edit,
  Wallet,
  Checked,
  InfoFilled,
  Plus,
  ArrowRight,
  Close,
  Check
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 订单商品
const orderItems = ref([])

// 收货地址列表
const addresses = ref([
  {
    id: 1,
    name: '张三',
    phone: '138****8888',
    province: '重庆市',
    city: '重庆市',
    district: '渝中区',
    detail: '解放碑步行街100号',
    isDefault: true
  },
  {
    id: 2,
    name: '李四',
    phone: '139****9999',
    province: '重庆市',
    city: '重庆市',
    district: '江北区',
    detail: '观音桥步行街200号',
    isDefault: false
  }
])

// 选中的地址ID
const selectedAddressId = ref(1)

// 地址对话框
const addressDialogVisible = ref(false)
const editingAddress = ref(null)
const addressForm = ref({
  name: '',
  phone: '',
  region: [],
  detail: '',
  isDefault: false
})

// 地区选项（使用全国省市区数据）
const regionOptions = ref(regionData)

// 发票类型
const invoiceType = ref('none')
const invoiceForm = ref({
  companyName: '',
  taxNumber: '',
  title: '',
  email: ''
})

// 订单备注
const orderNotes = ref('')

// 提交中
const submitting = ref(false)

// 优惠券相关
const couponDialogVisible = ref(false)
const selectedCouponId = ref(null)
const selectedCoupon = ref(null)
const couponDiscount = computed(() => {
  return selectedCoupon.value ? selectedCoupon.value.amount : 0
})

// 可用优惠券列表
// 可用优惠券列表（从后端获取）
const availableCoupons = ref([])

// 加载可用优惠券
const loadAvailableCoupons = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id || userInfo.userId
    
    if (!userId) {
      return
    }
    
    const response = await request.get('/user/coupon/available', {
      params: {
        userId,
        orderAmount: goodsTotal.value
      }
    })
    
    if (response.code === 200 && response.data) {
      const coupons = response.data.list || []
      availableCoupons.value = coupons.map((coupon: any) => ({
        id: coupon.id,
        name: coupon.couponName || '优惠券',
        description: coupon.sourceDesc || '等级权益优惠券',
        amount: coupon.discountValue ? parseFloat(coupon.discountValue) : 0,
        minAmount: coupon.minAmount ? parseFloat(coupon.minAmount) : 0,
        expireDate: coupon.validEndTime ? new Date(coupon.validEndTime).toLocaleDateString('zh-CN') : '',
        available: goodsTotal.value >= (coupon.minAmount ? parseFloat(coupon.minAmount) : 0)
      }))
    }
  } catch (error: any) {
    console.error('加载可用优惠券失败:', error)
    // 失败时使用空数组，不影响订单流程
    availableCoupons.value = []
  }
}

// 计算可用优惠券（兼容旧代码）
const availableCouponsComputed = computed(() => {
  const total = goodsTotal.value
  return availableCoupons.value.map(coupon => ({
    ...coupon,
    available: total >= coupon.minAmount
  }))
})
      amount: 50,
      minAmount: 300,
      expireDate: '2025-11-30',
      available: total >= 300
    },
    {
      id: 3,
      name: '文化体验券',
      description: '限文化体验类产品',
      amount: 30,
      minAmount: 150,
      expireDate: '2025-10-31',
      available: total >= 150
    },
    {
      id: 4,
      name: '周边商品券',
      description: '限特色周边商品',
      amount: 15,
      minAmount: 80,
      expireDate: '2025-12-15',
      available: total >= 80
    }
  ]
})

// 购买协议
const agreementDialogVisible = ref(false)

// 商品总价
const goodsTotal = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 运费
const shippingFee = computed(() => {
  return goodsTotal.value >= 99 ? 0 : 10
})

// 最终总价
const finalTotal = computed(() => {
  return goodsTotal.value + shippingFee.value - couponDiscount.value
})

// 编辑地址
const editAddress = (address) => {
  editingAddress.value = address
  addressForm.value = {
    name: address.name,
    phone: address.phone,
    region: [address.province, address.city, address.district],
    detail: address.detail,
    isDefault: address.isDefault
  }
  addressDialogVisible.value = true
}

// 删除地址
const deleteAddress = async (address) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = addresses.value.findIndex(a => a.id === address.id)
    if (index > -1) {
      addresses.value.splice(index, 1)
      ElMessage.success('删除成功')
      
      // 如果删除的是当前选中的地址，选择第一个地址
      if (selectedAddressId.value === address.id && addresses.value.length > 0) {
        selectedAddressId.value = addresses.value[0].id
      }
    }
  } catch {
    // 用户取消
  }
}

// 设为默认地址
const setDefaultAddress = (address) => {
  addresses.value.forEach(a => {
    a.isDefault = a.id === address.id
  })
  ElMessage.success('已设为默认地址')
}

// 保存地址
const saveAddress = () => {
  if (!addressForm.value.name) {
    ElMessage.warning('请输入收货人姓名')
    return
  }
  if (!addressForm.value.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  if (addressForm.value.region.length === 0) {
    ElMessage.warning('请选择所在地区')
    return
  }
  if (!addressForm.value.detail) {
    ElMessage.warning('请输入详细地址')
    return
  }

  if (editingAddress.value) {
    // 编辑地址
    Object.assign(editingAddress.value, {
      name: addressForm.value.name,
      phone: addressForm.value.phone,
      province: addressForm.value.region[0],
      city: addressForm.value.region[1],
      district: addressForm.value.region[2],
      detail: addressForm.value.detail,
      isDefault: addressForm.value.isDefault
    })
    ElMessage.success('修改成功')
  } else {
    // 新增地址
    const newAddress = {
      id: Date.now(),
      name: addressForm.value.name,
      phone: addressForm.value.phone,
      province: addressForm.value.region[0],
      city: addressForm.value.region[1],
      district: addressForm.value.region[2],
      detail: addressForm.value.detail,
      isDefault: addressForm.value.isDefault
    }
    
    if (newAddress.isDefault) {
      addresses.value.forEach(a => a.isDefault = false)
    }
    
    addresses.value.push(newAddress)
    selectedAddressId.value = newAddress.id
    ElMessage.success('添加成功')
  }

  addressDialogVisible.value = false
  editingAddress.value = null
  addressForm.value = {
    name: '',
    phone: '',
    region: [],
    detail: '',
    isDefault: false
  }
}

// 显示优惠券对话框
const showCouponDialog = () => {
  couponDialogVisible.value = true
}

// 选择优惠券
const selectCoupon = (coupon) => {
  if (coupon === null) {
    selectedCouponId.value = null
    return
  }
  
  if (!coupon.available) {
    ElMessage.warning(`该优惠券需订单满${coupon.minAmount}元才可使用`)
    return
  }
  
  selectedCouponId.value = coupon.id
}

// 确认选择优惠券
const confirmCoupon = () => {
  if (selectedCouponId.value === null) {
    selectedCoupon.value = null
    ElMessage.success('已取消使用优惠券')
  } else {
    const coupon = availableCouponsComputed.value.find(c => c.id === selectedCouponId.value)
    if (coupon) {
      selectedCoupon.value = coupon
      ElMessage.success(`已使用优惠券，优惠¥${coupon.amount}`)
    }
  }
  couponDialogVisible.value = false
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认提交订单？\n\n应付总额：¥${finalTotal.value.toFixed(2)}`,
      '确认订单',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    submitting.value = true

    // TODO: 调用后端API提交订单
    const selectedAddress = addresses.value.find(a => a.id === selectedAddressId.value)
    const orderData = {
      items: orderItems.value,
      address: selectedAddress,
      invoice: invoiceType.value !== 'none' ? invoiceForm.value : null,
      notes: orderNotes.value,
      goodsTotal: goodsTotal.value,
      shippingFee: shippingFee.value,
      couponDiscount: couponDiscount.value,
      finalTotal: finalTotal.value
    }

    console.log('订单数据:', orderData)

    // 模拟提交延迟
    await new Promise(resolve => setTimeout(resolve, 2000))

    ElMessage.success('订单提交成功！')
    
    // 跳转到订单列表或支付页面
    setTimeout(() => {
      router.push('/home/user/culture/orders')
    }, 1500)
  } catch {
    // 用户取消
  } finally {
    submitting.value = false
  }
}

// 页面加载
onMounted(() => {
  // 从路由参数获取订单商品
  const itemsParam = route.query.items
  if (itemsParam) {
    try {
      orderItems.value = JSON.parse(itemsParam)
    } catch (e) {
      console.error('解析订单商品失败:', e)
      ElMessage.error('订单数据错误')
      router.back()
      return
    }
  } else {
    ElMessage.error('缺少订单商品')
    router.back()
    return
  }
  
  // 加载可用优惠券
  loadAvailableCoupons()
})
</script>

<style scoped lang="scss">
.order-confirm {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;

  :deep(.el-card__body) {
    padding: 20px 24px;
  }

  h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin: 0;

    .el-icon {
      font-size: 28px;
      color: #409eff;
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  h3 {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    margin: 0;
  }
}

.address-card {
  margin-bottom: 24px;

  .address-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    width: 100%;

    .address-item {
      display: flex;
      gap: 12px;
      padding: 20px;
      border: 2px solid #dcdfe6;
      border-radius: 8px;
      background: white;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #c0c4cc;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      &.active {
        border-color: #409eff;
        background: linear-gradient(135deg, #f0f9ff 0%, #ecf5ff 100%);
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);

        .info-row .name {
          color: #409eff;
        }
      }

      .address-radio-wrapper {
        flex-shrink: 0;
        padding-top: 2px;

        :deep(.el-radio) {
          .el-radio__input {
            .el-radio__inner {
              width: 16px;
              height: 16px;
            }
          }

          .el-radio__label {
            display: none;
          }
        }
      }

      .address-info {
        flex: 1;
        min-width: 0;

        .info-row {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 10px;

          .name {
            font-size: 16px;
            font-weight: 700;
            color: #303133;
          }

          .phone {
            font-size: 14px;
            color: #909399;
          }

          .el-tag {
            margin-left: auto;
          }
        }

        .address-row {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          margin-bottom: 12px;
        }

        .actions-row {
          display: flex;
          align-items: center;
          gap: 8px;
          padding-top: 12px;
          border-top: 1px solid #f0f0f0;

          .divider {
            color: #dcdfe6;
            user-select: none;
          }

          :deep(.el-button) {
            padding: 0;
            height: auto;
            font-size: 13px;
            
            &.is-link {
              &[type="primary"] {
                --el-button-text-color: #409eff;
                --el-button-hover-text-color: #66b1ff;
              }
              
              &[type="danger"] {
                --el-button-text-color: #f56c6c;
                --el-button-hover-text-color: #f78989;
              }
            }
          }
        }
      }
    }
  }
}

.goods-card {
  margin-bottom: 24px;

  h3 {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 20px 0;
  }

  .goods-header {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 16px;
    padding: 16px 12px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
    border-radius: 8px;
    font-size: 15px;
    font-weight: 700;
    color: #606266;
    margin-bottom: 16px;
    text-align: center;

    .col-info {
      text-align: left;
    }
  }

  .goods-item {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 16px;
    padding: 16px 12px;
    align-items: center;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }

    .col-info {
      display: flex;
      gap: 12px;

      .goods-image {
        width: 80px;
        height: 80px;
        border-radius: 8px;
      }

      .goods-info {
        flex: 1;

        .goods-name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 8px 0;
        }

        .goods-spec {
          font-size: 14px;
          color: #909399;
          margin: 0;
        }
      }
    }

    .col-price,
    .col-quantity {
      font-size: 16px;
      color: #606266;
      text-align: center;
    }

    .col-subtotal {
      font-size: 18px;
      font-weight: 600;
      color: #f56c6c;
      text-align: right;
    }
  }
}

.invoice-card,
.notes-card {
  margin-bottom: 24px;

  h3 {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 20px 0;
  }

  .invoice-options {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 24px;

    .invoice-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px 20px;
      border: 2px solid #dcdfe6;
      border-radius: 8px;
      background: white;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #c0c4cc;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      &.active {
        border-color: #409eff;
        background: linear-gradient(135deg, #f0f9ff 0%, #ecf5ff 100%);
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);

        .invoice-label {
          color: #409eff;
          font-weight: 700;
        }
      }

      .invoice-radio-wrapper {
        flex-shrink: 0;

        :deep(.el-radio) {
          .el-radio__input {
            .el-radio__inner {
              width: 16px;
              height: 16px;
            }
          }

          .el-radio__label {
            display: none;
          }
        }
      }

      .invoice-label {
        flex: 1;
        font-size: 15px;
        color: #606266;
        font-weight: 500;
        user-select: none;
      }
    }
  }

  .invoice-form {
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
  }
}

.settlement-sticky {
  position: sticky;
  top: 24px;
}

.settlement-card {
  .settlement-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 20px 0;
  }

  .settlement-details {
    .detail-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      font-size: 15px;

      .label {
        color: #606266;
        font-weight: 500;
      }

      .value {
        color: #303133;
        font-weight: 600;
      }

      .shipping {
        color: #67c23a;
        font-weight: 600;
      }

      .discount {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #409eff;
        cursor: pointer;
        font-weight: 600;

        &:hover {
          text-decoration: underline;
        }
      }

      &.total {
        margin-top: 16px;
        padding-top: 4px;

        .label {
          font-size: 18px;
          font-weight: 700;
          color: #303133;
        }

        .final-price {
          font-size: 28px;
          font-weight: 700;
          color: #f56c6c;
        }
      }
    }
  }

  .submit-btn {
    width: 100%;
    height: 50px;
    font-size: 17px;
    font-weight: 700;
    margin-top: 24px;
    letter-spacing: 1px;
  }

  .submit-warning {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    margin-top: 12px;
    padding: 8px;
    background: #fef0f0;
    border-radius: 6px;
    font-size: 13px;
    color: #f56c6c;

    .el-icon {
      font-size: 16px;
    }
  }

  .submit-tips {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 12px;
    font-size: 12px;
    color: #909399;
    text-align: center;
    justify-content: center;
    line-height: 1.6;

    .el-icon {
      flex-shrink: 0;
    }

    :deep(.el-link) {
      font-size: 12px;
    }
  }
}

/* 优惠券对话框样式 */
.coupon-dialog {
  .coupon-list {
    max-height: 500px;
    overflow-y: auto;

    .coupon-item {
      position: relative;
      display: flex;
      gap: 16px;
      padding: 20px;
      margin-bottom: 16px;
      border: 2px solid #e4e7ed;
      border-radius: 12px;
      background: white;
      cursor: pointer;
      transition: all 0.3s ease;

      &:last-child {
        margin-bottom: 0;
      }

      &:hover:not(.disabled) {
        border-color: #409eff;
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
      }

      &.active {
        border-color: #409eff;
        background: linear-gradient(135deg, #f0f9ff 0%, #ecf5ff 100%);
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
      }

      &.disabled {
        opacity: 0.5;
        cursor: not-allowed;
        background: #f5f5f5;
      }

      .no-coupon {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        width: 100%;
        font-size: 16px;
        color: #606266;

        .el-icon {
          font-size: 20px;
        }
      }

      .coupon-left {
        flex-shrink: 0;
        width: 120px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
        border-radius: 8px;
        padding: 16px 0;

        .coupon-amount {
          display: flex;
          align-items: flex-start;
          color: white;

          .symbol {
            font-size: 18px;
            margin-top: 4px;
          }

          .value {
            font-size: 36px;
            font-weight: 700;
          }
        }

        .coupon-condition {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.9);
          margin-top: 4px;
        }
      }

      .coupon-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 6px;

        .coupon-name {
          font-size: 16px;
          font-weight: 700;
          color: #303133;
        }

        .coupon-desc {
          font-size: 13px;
          color: #909399;
        }

        .coupon-expire {
          font-size: 12px;
          color: #c0c4cc;
        }
      }

      .coupon-check {
        position: absolute;
        right: 16px;
        top: 16px;
        width: 24px;
        height: 24px;
        background: #409eff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;

        .el-icon {
          font-size: 16px;
        }
      }

      .coupon-unavailable {
        position: absolute;
        right: 16px;
        top: 16px;
        padding: 4px 12px;
        background: #f56c6c;
        color: white;
        font-size: 12px;
        border-radius: 4px;
      }
    }

    .empty-coupon {
      padding: 40px 0;
    }
  }
}

/* 购买协议对话框样式 */
.agreement-dialog {
  .agreement-content {
    max-height: 60vh;
    overflow-y: auto;
    padding: 0 20px;

    h3 {
      font-size: 22px;
      font-weight: 700;
      color: #303133;
      text-align: center;
      margin-bottom: 24px;
      padding-bottom: 12px;
      border-bottom: 2px solid #409eff;
    }

    section {
      margin-bottom: 24px;

      h4 {
        font-size: 16px;
        font-weight: 700;
        color: #409eff;
        margin-bottom: 12px;
      }

      p {
        font-size: 14px;
        line-height: 1.8;
        color: #606266;
        margin-bottom: 8px;
        text-indent: 0;

        strong {
          color: #303133;
          font-weight: 600;
        }
      }

      &.agreement-footer {
        margin-top: 32px;
        padding: 20px;
        background: linear-gradient(135deg, #f0f9ff 0%, #ecf5ff 100%);
        border-radius: 8px;
        border-left: 4px solid #409eff;

        p {
          margin-bottom: 8px;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }

    /* 美化滚动条 */
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f0f0f0;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #409eff;
      border-radius: 3px;

      &:hover {
        background: #66b1ff;
      }
    }
  }
}
</style>

