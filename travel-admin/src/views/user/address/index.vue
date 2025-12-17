<template>
  <div class="address-page">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Location /></el-icon>
        我的收货地址
      </h1>
    </el-card>

    <el-card class="address-card">
      <div class="card-header">
        <div class="title-left">
          <h3>
            <el-icon><Location /></el-icon>
            收货地址列表
          </h3>
          <p class="subtitle">管理你的常用收货地址，确认订单时将优先使用默认地址</p>
        </div>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          新增地址
        </el-button>
      </div>

      <div v-if="addresses.length === 0" class="empty-wrap">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="openCreate">添加地址</el-button>
        </el-empty>
      </div>

      <el-radio-group v-else v-model="selectedAddressId" class="address-list">
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

    <!-- 地址编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingAddress ? '编辑地址' : '新增地址'"
      width="600px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="收货人">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-cascader
            v-model="form.region"
            :options="regionOptions"
            placeholder="请选择省市区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input
            v-model="form.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { regionData } from '@/utils/regionData'
import { Location, Plus } from '@element-plus/icons-vue'

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

const STORAGE_KEY = 'travel_user_addresses'

const addresses = ref<Address[]>([])
const selectedAddressId = ref<number | null>(null)

const dialogVisible = ref(false)
const editingAddress = ref<Address | null>(null)
const form = ref({
  name: '',
  phone: '',
  region: [] as string[],
  detail: '',
  isDefault: false
})

const regionOptions = ref(regionData)

const loadFromStorage = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return
    const data = JSON.parse(raw) as Address[]
    addresses.value = data
    const defaultAddr = data.find(a => a.isDefault)
    selectedAddressId.value = defaultAddr?.id ?? (data[0]?.id ?? null)
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

const saveToStorage = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(addresses.value))
}

const resetForm = () => {
  form.value = {
    name: '',
    phone: '',
    region: [],
    detail: '',
    isDefault: false
  }
}

const openCreate = () => {
  editingAddress.value = null
  resetForm()
  dialogVisible.value = true
}

const editAddress = (address: Address) => {
  editingAddress.value = address
  form.value = {
    name: address.name,
    phone: address.phone,
    region: [address.province, address.city, address.district],
    detail: address.detail,
    isDefault: address.isDefault
  }
  dialogVisible.value = true
}

const deleteAddress = async (address: Address) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const index = addresses.value.findIndex(a => a.id === address.id)
    if (index > -1) {
      addresses.value.splice(index, 1)
      if (selectedAddressId.value === address.id) {
        selectedAddressId.value = addresses.value[0]?.id ?? null
      }
      saveToStorage()
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

const setDefaultAddress = (address: Address) => {
  addresses.value.forEach(a => {
    a.isDefault = a.id === address.id
  })
  selectedAddressId.value = address.id
  saveToStorage()
  ElMessage.success('已设为默认地址')
}

const handleSave = () => {
  if (!form.value.name) {
    ElMessage.warning('请输入收货人姓名')
    return
  }
  if (!form.value.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  if (form.value.region.length === 0) {
    ElMessage.warning('请选择所在地区')
    return
  }
  if (!form.value.detail) {
    ElMessage.warning('请输入详细地址')
    return
  }

  if (editingAddress.value) {
    Object.assign(editingAddress.value, {
      name: form.value.name,
      phone: form.value.phone,
      province: form.value.region[0],
      city: form.value.region[1],
      district: form.value.region[2],
      detail: form.value.detail,
      isDefault: form.value.isDefault
    })
    ElMessage.success('修改成功')
  } else {
    const newAddress: Address = {
      id: Date.now(),
      name: form.value.name,
      phone: form.value.phone,
      province: form.value.region[0],
      city: form.value.region[1],
      district: form.value.region[2],
      detail: form.value.detail,
      isDefault: form.value.isDefault
    }

    if (newAddress.isDefault) {
      addresses.value.forEach(a => (a.isDefault = false))
    }

    addresses.value.push(newAddress)
    selectedAddressId.value = newAddress.id
    ElMessage.success('添加成功')
  }

  saveToStorage()
  dialogVisible.value = false
  editingAddress.value = null
  resetForm()
}

onMounted(() => {
  loadFromStorage()
})
</script>

<style scoped lang="scss">
.address-page {
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

.address-card {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    .title-left {
      h3 {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 18px;
        font-weight: 700;
        color: #303133;
        margin: 0;
      }

      .subtitle {
        margin: 4px 0 0;
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .empty-wrap {
    padding: 40px 0;
  }

  .address-list {
    display: flex;
    flex-direction: column;
    gap: 16px;

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
          .el-radio__input .el-radio__inner {
            width: 16px;
            height: 16px;
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
          }
        }
      }
    }
  }
}
</style>


