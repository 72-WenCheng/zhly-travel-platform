<template>
  <div class="create-service">
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-actions">
        <el-button class="action-btn" @click="handleCancel" :disabled="loading">取消</el-button>
        <el-button class="action-btn confirm-btn" @click="handleSubmit" :loading="loading">
          {{ loading ? '提交中...' : '确定' }}
        </el-button>
      </div>
    </div>

    <!-- 服务表单 -->
    <el-card class="form-card" v-loading="loading">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
      >
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <el-icon><InfoFilled /></el-icon>
          基本信息
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服务名称" prop="title" required>
              <el-input v-model="formData.title" placeholder="请输入服务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位置" prop="location" required>
              <el-input v-model="formData.location" placeholder="请输入位置" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone" required>
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分" prop="rating">
              <el-input-number
                v-model="formData.rating"
                :min="0"
                :max="5"
                :precision="1"
                placeholder="评分0-5"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="服务描述" prop="description" required>
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入服务描述"
          />
        </el-form-item>

        <!-- 图片上传 -->
        <el-divider content-position="left">
          <el-icon><Picture /></el-icon>
          图片上传
        </el-divider>
        
        <el-form-item label="服务图片" prop="images" required>
          <el-upload
            v-model:file-list="imageList"
            :action="uploadUrl"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            :headers="uploadHeaders"
            :limit="9"
            :on-exceed="handleExceed"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-dialog
            v-model="dialogVisible"
            width="60%"
            class="image-preview-dialog"
          >
            <div class="image-preview-wrapper">
              <img :src="dialogImageUrl" alt="预览" />
            </div>
          </el-dialog>
          <div class="upload-tip">支持jpg/png格式，最多9张，建议尺寸800x600，第一张将作为封面图</div>
        </el-form-item>

        <!-- 服务特色 -->
        <el-divider content-position="left">
          <el-icon><Star /></el-icon>
          服务特色
        </el-divider>
        
        <el-form-item label="特色标签" prop="features">
          <el-select
            v-model="formData.features"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入特色标签"
            style="width: 100%"
          >
            <el-option
              v-for="feature in commonFeatures"
              :key="feature"
              :label="feature"
              :value="feature"
            />
          </el-select>
          <div class="form-tip">可输入多个特色标签，如：采摘体验、农家餐饮、垂钓等</div>
        </el-form-item>

        <!-- 配套设施 -->
        <el-divider content-position="left">
          <el-icon><Grid /></el-icon>
          配套设施
        </el-divider>
        
        <el-form-item label="设施列表">
          <el-select
            v-model="selectedFacilityValues"
            multiple
            filterable
            placeholder="选择配套设施（自动附带图标）"
            style="width: 100%"
            @change="syncFacilitiesFromSelection"
          >
            <el-option
              v-for="opt in facilityOptions"
              :key="opt.value"
              :label="opt.name"
              :value="opt.value"
            >
              <div class="facility-option">
                <img class="facility-icon" :src="opt.icon" :alt="opt.name" />
                <span>{{ opt.name }}</span>
              </div>
            </el-option>
          </el-select>
          <div class="selected-facilities" v-if="formData.facilities && formData.facilities.length">
            <div class="facility-chip" v-for="(fac, idx) in formData.facilities" :key="idx">
              <img v-if="fac.icon" class="facility-chip-icon" :src="fac.icon" :alt="fac.name" />
              <span>{{ fac.name }}</span>
            </div>
          </div>
          <div class="form-tip">
            直接选择即可，系统自动附带图标；如需自定义图标，可后续补充到预置列表。
          </div>
        </el-form-item>

        <!-- 套餐信息 -->
        <el-divider content-position="left">
          <el-icon><Wallet /></el-icon>
          套餐信息
        </el-divider>
        
        <el-form-item label="套餐列表">
          <div class="packages-list">
            <div
              v-for="(pkg, index) in formData.packages"
              :key="index"
              class="package-item"
            >
              <el-card shadow="never" style="margin-bottom: 16px;">
                <div class="package-header">
                  <h4>套餐 {{ index + 1 }}</h4>
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removePackage(index)"
                  >
                    <el-icon><Delete /></el-icon>
                    删除套餐
                  </el-button>
                </div>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="套餐名称" :prop="`packages.${index}.name`">
                      <el-input v-model="pkg.name" placeholder="请输入套餐名称" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="价格" :prop="`packages.${index}.price`">
                      <el-input-number
                        v-model="pkg.price"
                        :min="0"
                        :precision="2"
                        placeholder="价格"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="单位" :prop="`packages.${index}.unit`">
                      <el-input v-model="pkg.unit" placeholder="如：人、天、次" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="描述" :prop="`packages.${index}.description`">
                      <el-input v-model="pkg.description" placeholder="套餐描述" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="包含内容">
                  <el-select
                    v-model="pkg.includes"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="选择或输入包含内容"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in commonIncludes"
                      :key="item"
                      :label="item"
                      :value="item"
                    />
                  </el-select>
                </el-form-item>
              </el-card>
            </div>
            <el-button
              type="primary"
              size="small"
              text
              @click="addPackage"
            >
              <el-icon><Plus /></el-icon>
              添加套餐
            </el-button>
          </div>
        </el-form-item>

        <!-- 状态设置 -->
        <el-divider content-position="left">
          <el-icon><Setting /></el-icon>
          状态设置
        </el-divider>
        
        <el-form-item label="服务状态" prop="status" required>
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="2">维护中</el-radio>
            <el-radio :label="0">已关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, InfoFilled, Picture, Star, Grid, Wallet, Setting, Delete
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import { createService, updateService, getServiceById } from '@/api/cultureService'
import type { CultureService } from '@/api/cultureService'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = ref(false)
const serviceId = ref<number | null>(null)
const imageList = ref<UploadFiles>([])
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

const commonFeatures = [
  '采摘体验', '农家餐饮', '垂钓', '烧烤', '儿童游乐区', 
  '停车场', '免费WiFi', '宠物友好', '住宿', '温泉'
]

const commonIncludes = [
  '农家午餐', '农家晚餐', '早餐', '采摘体验', '垂钓体验', 
  '烧烤食材', '住宿', '茶水饮料', '导游服务'
]

// 预置的配套设施选项（带图标），用户直接选择即可
// 配套设施预置（本地图标，便于离线）
const facilityOptions = [
  { value: 'parking', name: '停车场', icon: '/icons/facility/parking.svg' },
  { value: 'wifi', name: '免费WiFi', icon: '/icons/facility/wifi.svg' },
  { value: 'children', name: '儿童游乐区', icon: '/icons/facility/children.svg' },
  { value: 'restaurant', name: '农家餐饮', icon: '/icons/facility/restaurant.svg' },
  { value: 'accommodation', name: '住宿/客房', icon: '/icons/facility/accommodation.svg' },
  { value: 'hotspring', name: '温泉', icon: '/icons/facility/hotspring.svg' },
  { value: 'bbq', name: '烧烤区', icon: '/icons/facility/bbq.svg' },
  { value: 'pet', name: '宠物友好', icon: '/icons/facility/pet.svg' }
]

const selectedFacilityValues = ref<string[]>([])

// 后端有些字段在数据库里是 JSON 字符串，这里统一做一次安全转换
const parseJsonArray = <T = any>(value: any, fallback: T[] = []): T[] => {
  if (!value) return fallback
  if (Array.isArray(value)) return value
  if (typeof value === 'string') {
    try {
      const parsed = JSON.parse(value)
      return Array.isArray(parsed) ? parsed : fallback
    } catch (e) {
      console.warn('解析 JSON 字段失败:', value, e)
      return fallback
    }
  }
  return fallback
}

const formData = reactive<CultureService>({
  title: '',
  location: '',
  contactPhone: '',
  rating: 0,
  description: '',
  images: [],
  features: [],
  facilities: [],
  packages: [],
  status: 1
})

const rules = reactive<FormRules>({
  title: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  description: [{ required: true, message: '请输入服务描述', trigger: 'blur' }],
  images: [
    { 
      required: true, 
      message: '请上传服务图片', 
      trigger: 'change',
      validator: (_rule: any, _value: any, callback: any) => {
        if (!imageList.value || imageList.value.length === 0) {
          callback(new Error('请上传服务图片'))
        } else {
          callback()
        }
      }
    }
  ],
  status: [{ required: true, message: '请选择服务状态', trigger: 'change' }]
})

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseUrl}/upload/image?folder=service`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('travel_token') || localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 加载服务数据（编辑模式）
const loadServiceData = async (id: number) => {
  try {
    loading.value = true
    const result = await getServiceById(id)
    if (result.code === 200 && result.data) {
      const service = result.data

      // 把可能是 JSON 字符串的字段转成前端需要的数组结构
      const imagesArray = parseJsonArray<string>(service.images)
      const featuresArray = parseJsonArray<string>(service.features)
      const facilitiesArray = parseJsonArray<any>(service.facilities)
      const packagesArray = Array.isArray(service.packages) ? service.packages : []

      Object.assign(formData, {
        title: service.title || '',
        location: service.location || '',
        contactPhone: service.contactPhone || '',
        rating: service.rating || 0,
        description: service.description || '',
        images: imagesArray,
        features: featuresArray,
        facilities: facilitiesArray,
        packages: packagesArray,
        status: service.status !== undefined ? service.status : 1
      })
      
      // 同步设施选择值，用于下拉多选展示
      selectedFacilityValues.value = (facilitiesArray || []).map(fac => {
        const matched = facilityOptions.find(opt => opt.name === fac.name)
        return matched ? matched.value : fac.name
      })
      
      // 处理图片列表
      if (imagesArray && imagesArray.length > 0) {
        imageList.value = imagesArray.map((url: string, index: number) => ({
          uid: index,
          name: `image-${index}`,
          url: url,
          status: 'success'
        }))
      }
    } else {
      ElMessage.error('加载服务数据失败')
      router.back()
    }
  } catch (error: any) {
    console.error('加载服务数据失败:', error)
    ElMessage.error('加载服务数据失败: ' + (error.message || '未知错误'))
    router.back()
  } finally {
    loading.value = false
  }
}

// 页面初始化
onMounted(() => {
  const id = route.params.id as string
  if (id && id !== 'create') {
    const numId = parseInt(id)
    if (!isNaN(numId)) {
      isEdit.value = true
      serviceId.value = numId
      loadServiceData(numId)
    } else {
      resetForm()
    }
  } else {
    resetForm()
  }
})

// 图片上传成功
const handleImageSuccess = (response: any, file: UploadFile) => {
  setTimeout(() => {
    formRef.value?.validateField('images')
  }, 100)
  
  let imageUrl = ''
  if (response && response.code === 200) {
    if (response.data) {
      imageUrl = response.data.fileUrl || response.data.url || response.data.avatar || response.data
    }
  } else if (response && typeof response === 'string') {
    imageUrl = response
  } else if (response && response.url) {
    imageUrl = response.url
  }
  
  if (imageUrl) {
    file.url = imageUrl
    file.response = response
    updateImagesArray()
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response?.message || '图片上传失败，无法获取图片地址')
    const index = imageList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      imageList.value.splice(index, 1)
    }
  }
}

// 移除图片
const handleRemove = (file: UploadFile) => {
  setTimeout(() => {
    formRef.value?.validateField('images')
  }, 100)
  updateImagesArray()
}

// 预览图片
const handlePictureCardPreview = (file: UploadFile) => {
  dialogImageUrl.value = file.url || ''
  dialogVisible.value = true
}

// 超出限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传9张图片')
}

// 更新图片数组
const updateImagesArray = () => {
  const imageUrls = imageList.value
    .map(file => file.url || file.response?.data?.url || file.response?.data)
    .filter(url => url)
  formData.images = imageUrls
}

// 图片上传前校验
const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 根据设施名称自动生成一个简单的图标 URL（可以按需调整为自己服务器上的图标）
const getFacilityIconByName = (name: string): string => {
  if (!name) return ''
  const key = name.trim()
  // 这里用非常简单的关键词匹配，你后面如果有专门的图标地址，可以直接改成固定 URL
  if (key.includes('停车')) return 'https://img.icons8.com/fluency/48/parking.png'
  if (key.includes('WiFi') || key.includes('wifi') || key.includes('网络')) return 'https://img.icons8.com/fluency/48/wifi.png'
  if (key.includes('儿童') || key.includes('小孩')) return 'https://img.icons8.com/fluency/48/children.png'
  if (key.includes('餐') || key.includes('餐饮') || key.includes('饭')) return 'https://img.icons8.com/fluency/48/restaurant.png'
  if (key.includes('住宿') || key.includes('客房') || key.includes('民宿')) return 'https://img.icons8.com/fluency/48/bed.png'
  if (key.includes('温泉')) return 'https://img.icons8.com/fluency/48/hot-springs.png'
  if (key.includes('烧烤')) return 'https://img.icons8.com/fluency/48/barbecue.png'
  if (key.includes('宠物')) return 'https://img.icons8.com/fluency/48/dog.png'
  return ''
}

// 根据选择的 value 列表，同步生成带图标的设施数组
const syncFacilitiesFromSelection = () => {
  formData.facilities = selectedFacilityValues.value.map(val => {
    const found = facilityOptions.find(opt => opt.value === val)
    if (found) {
      return { name: found.name, icon: found.icon }
    }
    // 兜底：如果某项不在预置列表，用名称=值，图标按名称匹配（基本不会走到）
    return { name: val, icon: getFacilityIconByName(val) }
  })
}

// 添加套餐
const addPackage = () => {
  formData.packages = formData.packages || []
  formData.packages.push({
    name: '',
    price: 0,
    unit: '人',
    description: '',
    includes: []
  })
}

// 删除套餐
const removePackage = (index: number) => {
  formData.packages.splice(index, 1)
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    title: '',
    location: '',
    contactPhone: '',
    rating: 0,
    description: '',
    images: [],
    features: [],
    facilities: [],
    packages: [],
    status: 1
  })
  imageList.value = []
  selectedFacilityValues.value = []
  formRef.value?.clearValidate()
}

// 取消并返回
const handleCancel = () => {
  router.back()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        // 确保图片数据正确
        updateImagesArray()
        
        // 确保至少有一个套餐
        if (!formData.packages || formData.packages.length === 0) {
          ElMessage.warning('请至少添加一个套餐')
          loading.value = false
          return
        }
        
        // 构建提交数据
        const submitData: CultureService = { 
          title: formData.title,
          location: formData.location,
          contactPhone: formData.contactPhone,
          rating: formData.rating || 0,
          description: formData.description || '',
          images: formData.images || [],
          features: formData.features || [],
          facilities: formData.facilities || [],
          packages: formData.packages || [],
          status: formData.status || 1
        }
        
        let result
        if (isEdit.value && serviceId.value) {
          // 编辑模式
          result = await updateService(serviceId.value, submitData)
        } else {
          // 新增模式
          result = await createService(submitData)
        }
        
        if (result.code === 200) {
          ElMessage.success(isEdit.value ? '更新服务成功' : '创建服务成功')
          router.back()
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新服务失败' : '创建服务失败'))
        }
      } catch (error: any) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败: ' + (error.message || error.response?.data?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请填写完整信息')
    }
  })
}
</script>

<style lang="scss" scoped>
.create-service {
  padding: 24px;
  min-height: 100vh;
  background: #f5f7fa;
  
  .page-header {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    
    .header-actions {
      display: flex;
      gap: 12px;
      
      :deep(.action-btn) {
        border-radius: 4px !important;
        padding: 8px 20px !important;
        font-weight: 500 !important;
        background-color: #ffffff !important;
        background: #ffffff !important;
        border: 1px solid #dcdfe6 !important;
        border-color: #dcdfe6 !important;
        color: #606266 !important;
        transition: all 0.3s !important;
        display: inline-flex !important;
        align-items: center !important;
        justify-content: center !important;
        vertical-align: middle !important;
        line-height: 1.5 !important;
        box-shadow: none !important;
        outline: none !important;
        
        &,
        &.el-button--primary,
        &.el-button--default {
          background: #ffffff !important;
          background-color: #ffffff !important;
          border: 1px solid #dcdfe6 !important;
          border-color: #dcdfe6 !important;
          color: #606266 !important;
          box-shadow: none !important;
        }
        
        &:hover:not([disabled]) {
          background: #f5f7fa !important;
          background-color: #f5f7fa !important;
          border-color: #c0c4cc !important;
          color: #303133 !important;
          box-shadow: none !important;
          transform: none !important;
        }
        
        &:active:not([disabled]) {
          background: #f0f2f5 !important;
          background-color: #f0f2f5 !important;
          transform: none !important;
          box-shadow: none !important;
        }
        
        &:focus:not([disabled]) {
          background: #ffffff !important;
          background-color: #ffffff !important;
          border-color: #dcdfe6 !important;
          color: #606266 !important;
          box-shadow: none !important;
          outline: none !important;
        }
        
        &[disabled],
        &.is-disabled {
          color: #c0c4cc !important;
          border-color: #e4e7ed !important;
          background: #ffffff !important;
          background-color: #ffffff !important;
        }
        
        &.is-loading {
          background: #ffffff !important;
          background-color: #ffffff !important;
          border-color: #dcdfe6 !important;
          color: #606266 !important;
        }
        
        &.confirm-btn {
          font-weight: 500 !important;
        }
      }
    }
  }
  
  .form-card {
    border-radius: 12px;
    border: 1px solid #e4e7ed;
    background: #ffffff;
    box-shadow: none;
    
    :deep(.el-card__body) {
      padding: 32px;
    }
    
    :deep(.el-form) {
      .el-form-item__label {
        font-weight: 600;
        color: #606266;
        white-space: nowrap;
        word-break: keep-all;
        font-size: 16px;
        display: flex;
        align-items: center;
        height: 44px;
        padding-bottom: 0;
      }
      
      .el-input__wrapper,
      .el-textarea__inner {
        border-radius: 4px;
        transition: none;
        min-height: 44px;
        border: 1px solid #dcdfe6 !important;
        box-shadow: none !important;
        background-color: #ffffff !important;
        
        &:hover {
          border: 1px solid #dcdfe6 !important;
          box-shadow: none !important;
          background-color: #ffffff !important;
        }
        
        &.is-focus {
          border: 1px solid #dcdfe6 !important;
          box-shadow: none !important;
          background-color: #ffffff !important;
        }
      }
      
      .el-divider {
        margin: 32px 0 24px;
        
        .el-divider__text {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          
          .el-icon {
            margin-right: 8px;
            vertical-align: middle;
            color: #303133;
          }
        }
      }
    }
  }
}

.facilities-list {
  .facility-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
  }
}

.packages-list {
  .package-item {
    margin-bottom: 16px;
    
    .package-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.facility-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.facility-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.selected-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.facility-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 12px;
  background: #f0f4ff;
  color: #3a6ff7;
  font-size: 13px;
}

.facility-chip-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

/* 去除图片卡片上"按 Delete 键可删除"的文字提示 */
:deep(.el-upload-list__item::after) {
  display: none !important;
}

/* 图片预览对话框样式 */
.image-preview-dialog {
  :deep(.el-dialog__body) {
    padding: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.image-preview-wrapper {
  max-width: 100%;
  max-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: center;

  img {
    max-width: 100%;
    max-height: 80vh;
    object-fit: contain;
    display: block;
    border-radius: 4px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
    background-color: #ffffff;
  }
}
</style>








