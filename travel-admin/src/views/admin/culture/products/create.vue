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

    <!-- 产品表单 -->
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
            <el-form-item label="产品名称" prop="name" required>
              <el-input v-model="formData.name" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产地" prop="origin" required>
              <el-input v-model="formData.origin" placeholder="请输入产地" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select
                v-model="formData.category"
                placeholder="请选择分类"
                style="width: 100%"
              >
                <el-option
                  v-for="item in categoryOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保质期" prop="shelfLife">
              <el-select
                v-model="formData.shelfLife"
                placeholder="请选择保质期"
                style="width: 100%"
              >
                <el-option label="6个月" value="6个月" />
                <el-option label="12个月" value="12个月" />
                <el-option label="18个月" value="18个月" />
                <el-option label="24个月" value="24个月" />
                <el-option label="36个月" value="36个月" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="价格" prop="price" required>
              <el-input-number
                v-model="formData.price"
                :min="0"
                :precision="2"
                placeholder="价格"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="formData.unit" placeholder="如：斤、盒、袋" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number
                v-model="formData.originalPrice"
                :min="0"
                :precision="2"
                placeholder="原价（可选）"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number
                v-model="formData.stock"
                :min="-1"
                placeholder="库存（-1表示无限）"
                style="width: 100%"
              />
              <div class="form-tip">输入-1表示库存无限</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签" prop="badge">
              <el-input v-model="formData.badge" placeholder="如：政府扶持、特色产品" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产品描述" prop="description" required>
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

        <!-- 产品特色 -->
        <el-divider content-position="left">
          <el-icon><Star /></el-icon>
          产品特色
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
          <div class="form-tip">可输入多个特色标签，如：有机、绿色、无公害等</div>
        </el-form-item>

        <el-form-item label="亮点介绍" prop="highlights">
          <el-select
            v-model="formData.highlights"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入亮点"
            style="width: 100%"
          >
            <el-option
              v-for="highlight in commonHighlights"
              :key="highlight"
              :label="highlight"
              :value="highlight"
            />
          </el-select>
        </el-form-item>

        <!-- 规格选择（用于用户端购买时的规格切换） -->
        <el-divider content-position="left">
          <el-icon><Setting /></el-icon>
          规格选择
        </el-divider>

        <el-form-item label="规格选项" prop="availableSpecs">
          <div v-for="(spec, index) in formData.availableSpecs" :key="index" class="spec-item">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-select
                  v-model="formData.availableSpecs[index].value"
                  placeholder="请选择规格"
                  style="width: 100%"
                  @change="(val) => handleSpecPresetChange(index, val)"
                >
                  <el-option
                    v-for="item in specPresetOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-col>
              <el-col :span="10">
                <el-input
                  v-model="formData.availableSpecs[index].label"
                  placeholder="规格名称"
                  disabled
                />
              </el-col>
              <el-col :span="4">
                <el-button @click="removeSpecOption(index)" :icon="Delete" />
              </el-col>
            </el-row>
          </div>
          <el-button type="primary" text @click="addSpecOption" style="width: 100%; margin-top: 8px;">
            <el-icon><Plus /></el-icon>
            添加规格选项
          </el-button>
          <div class="form-tip">用于用户端“规格选择”单选按钮，已预置常见规格，如：250g、500g 等</div>
        </el-form-item>

        <el-form-item label="产品摘要" prop="summary">
          <el-input
            v-model="formData.summary"
            type="textarea"
            :rows="2"
            placeholder="简短的产品摘要"
          />
        </el-form-item>

        <!-- 状态设置 -->
        <el-divider content-position="left">
          <el-icon><Setting /></el-icon>
          状态设置
        </el-divider>
        
        <el-form-item label="产品状态" prop="status" required>
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
  Plus, InfoFilled, Picture, Star, Setting
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import { createProduct, updateProduct, getProductById } from '@/api/agriculturalProduct'
import type { AgriculturalProduct } from '@/api/agriculturalProduct'

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
  '有机', '绿色', '无公害', '原生态', '新鲜', 
  '纯天然', '手工制作', '传统工艺', '地方特色', '优质'
]

const commonHighlights = [
  '稻田观光', '农事体验', '土鸡美食', '稻田露营', '土鸡柴火饭',
  '亲子插秧', '新鲜采摘', '产地直供', '品质保证'
]

// 预置分类选项
const categoryOptions = [
  '茶叶',
  '粮油',
  '干货',
  '水果',
  '蔬菜',
  '禽畜产品',
  '休闲零食',
  '腊味/腌制',
  '蜂蜜/特产',
  '其他'
]

// 预置规格选项
const specPresetOptions = [
  { label: '250g 礼盒装', value: '250g' },
  { label: '500g 礼盒装', value: '500g' },
  { label: '750g 礼盒装', value: '750g' },
  { label: '1000g 礼盒装', value: '1000g' }
]

const formData = reactive<AgriculturalProduct>({
  name: '',
  category: '',
  origin: '',
  price: 0,
  originalPrice: 0,
  unit: '斤',
  stock: -1,
  description: '',
  summary: '',
  shelfLife: '',
  images: [],
  features: [],
  highlights: [],
  badge: '',
  rating: 0,
  contactPhone: '',
  status: 1,
  availableSpecs: []
})

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  origin: [{ required: true, message: '请输入产地', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  description: [{ required: true, message: '请输入产品描述', trigger: 'blur' }],
  images: [
    { 
      required: true, 
      message: '请上传产品图片', 
      trigger: 'change',
      validator: (_rule: any, _value: any, callback: any) => {
        if (!imageList.value || imageList.value.length === 0) {
          callback(new Error('请上传产品图片'))
        } else {
          callback()
        }
      }
    }
  ],
  status: [{ required: true, message: '请选择产品状态', trigger: 'change' }]
})

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseUrl}/upload/image?folder=product`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('travel_token') || localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 添加规格选项
const addSpecOption = () => {
  if (!Array.isArray(formData.availableSpecs)) {
    formData.availableSpecs = []
  }
  formData.availableSpecs.push({ label: '', value: '' })
}

// 删除规格选项
const removeSpecOption = (index: number) => {
  if (!Array.isArray(formData.availableSpecs)) return
  formData.availableSpecs.splice(index, 1)
}

// 选择预置规格后，同步名称
const handleSpecPresetChange = (index: number, value: string) => {
  const preset = specPresetOptions.find(item => item.value === value)
  if (!formData.availableSpecs[index]) {
    formData.availableSpecs[index] = { label: '', value: '' }
  }
  formData.availableSpecs[index].value = value
  formData.availableSpecs[index].label = preset?.label || value
}

// 加载产品数据（编辑模式）
const loadServiceData = async (id: number) => {
  try {
    loading.value = true
    const result = await getProductById(id)
    if (result.code === 200 && result.data) {
      const product: any = result.data

      // 解析图片（后端存的是字符串，通常是 JSON 数组）
      let images: string[] = []
      if (product.images) {
        if (Array.isArray(product.images)) {
          images = product.images
        } else if (typeof product.images === 'string') {
          try {
            const parsed = JSON.parse(product.images)
            if (Array.isArray(parsed)) {
              images = parsed
            } else {
              images = product.images
                .split(',')
                .map((i: string) => i.trim())
                .filter((i: string) => i)
            }
          } catch {
            images = product.images
              .split(',')
              .map((i: string) => i.trim())
              .filter((i: string) => i)
          }
        }
      }

      // 解析特色标签 / 亮点（后端是 JSON 字符串或逗号分隔字符串，前端用数组）
      const parseTagString = (val: any): string[] => {
        if (!val) return []
        if (Array.isArray(val)) return val
        if (typeof val === 'string') {
          // 优先尝试按 JSON 解析
          const trimmed = val.trim()
          if (trimmed.startsWith('[') || trimmed.startsWith('{')) {
            try {
              const parsed = JSON.parse(trimmed)
              if (Array.isArray(parsed)) {
                return parsed
                  .map((s: any) => String(s).trim())
                  .filter((s: string) => s)
              }
            } catch {
              // ignore and fallback to split
            }
          }
          // 兼容逗号 / 换行分隔的旧数据
          return trimmed
            .split(/[,\n]/)
            .map((s: string) => s.trim())
            .filter((s: string) => s)
        }
        return []
      }

      Object.assign(formData, {
        name: product.productName || product.name || '',
        category: product.category || '',
        origin: product.origin || '',
        price: Number(product.price || 0),
        originalPrice: Number(product.originalPrice || 0),
        unit: product.unit || '斤',
        stock: product.stock !== undefined ? product.stock : -1,
        description: product.description || '',
        summary: product.summary || '',
        shelfLife: product.shelfLife || '',
        images,
        features: parseTagString(product.features),
        highlights: parseTagString(product.highlights),
        badge: product.badge || '',
        rating: Number(product.rating || 0),
        contactPhone: product.contactPhone || '',
        status: product.status !== undefined ? product.status : 1,
        availableSpecs: (() => {
          if (!product.availableSpecs) return []
          if (Array.isArray(product.availableSpecs)) return product.availableSpecs
          try {
            const parsed = JSON.parse(product.availableSpecs)
            return Array.isArray(parsed) ? parsed : []
          } catch {
            return []
          }
        })()
      })
      
      // 处理图片列表（用于上传组件展示）
      if (images.length > 0) {
        imageList.value = images.map((url: string, index: number) => ({
          uid: index,
          name: `image-${index}`,
          url,
          status: 'success'
        }))
      }
    } else {
      ElMessage.error('加载产品数据失败')
      router.back()
    }
  } catch (error: any) {
    console.error('加载产品数据失败:', error)
    ElMessage.error('加载产品数据失败: ' + (error.message || '未知错误'))
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


// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    name: '',
    category: '',
    origin: '',
    price: 0,
    originalPrice: 0,
    unit: '斤',
    stock: -1,
    description: '',
    summary: '',
    shelfLife: '',
    images: [],
    features: [],
    highlights: [],
    badge: '',
    rating: 0,
    contactPhone: '',
    status: 1,
    availableSpecs: []
  })
  imageList.value = []
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

        const imageArray = (formData.images || []) as string[]
        const featuresArray = (formData.features || []) as string[]
        const highlightsArray = (formData.highlights || []) as string[]
        const specOptions = Array.isArray(formData.availableSpecs)
          ? formData.availableSpecs.filter(opt => opt && (opt.label || opt.value))
          : []

        // 后端实体字段是 CultureProduct，部分字段为字符串，这里做适配转换
        const submitData: any = {
          // 基本信息
          productName: formData.name,
          productType: 4, // 4-特产/农特产品
          category: formData.category || '',
          origin: formData.origin,
          price: formData.price || 0,
          originalPrice: formData.originalPrice || 0,
          unit: formData.unit || '斤',
          stock: formData.stock !== undefined ? formData.stock : -1,
          description: formData.description || '',
          summary: formData.summary || '',
          shelfLife: formData.shelfLife || '',

          // 图片相关：后端字段是 String，存 JSON 数组
          images: imageArray.length ? JSON.stringify(imageArray) : '[]',
          coverImage: imageArray[0] || undefined,

          // 标签/亮点：后端字段是 JSON 字符串
          features: featuresArray.length ? JSON.stringify(featuresArray) : '[]',
          highlights: highlightsArray.length ? JSON.stringify(highlightsArray) : '[]',

          // 规格选择：JSON 字符串
          availableSpecs: specOptions.length ? JSON.stringify(specOptions) : '[]',

          // 其他
          badge: formData.badge || '',
          rating: formData.rating || 0,
          contactPhone: formData.contactPhone || '',
          status: formData.status || 1
        }

        let result
        if (isEdit.value && serviceId.value) {
          // 编辑模式
          result = await updateProduct(serviceId.value, submitData)
        } else {
          // 新增模式
          result = await createProduct(submitData)
        }
        
        if (result.code === 200) {
          ElMessage.success(isEdit.value ? '更新产品成功' : '创建产品成功')
          router.back()
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新产品失败' : '创建产品失败'))
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

