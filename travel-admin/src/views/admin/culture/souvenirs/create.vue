<template>
  <div class="create-souvenir">
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
            <el-form-item label="标签" prop="badge">
              <el-input v-model="formData.badge" placeholder="如：地理标志、特色产品" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产地" prop="origin" required>
              <el-input v-model="formData.origin" placeholder="请输入产地，如：重庆市巴南区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="官方认证" prop="certified">
              <el-switch v-model="formData.certified" />
              <div class="form-tip">开启后显示"官方认证"标签</div>
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
              <el-input v-model="formData.unit" placeholder="如：/盒、/斤、/袋" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="评分" prop="rating">
              <el-rate v-model="formData.rating" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产品描述" prop="description" required>
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入产品描述"
          />
        </el-form-item>

        <!-- 图片上传 -->
        <el-divider content-position="left">
          <el-icon><Picture /></el-icon>
          图片上传
        </el-divider>
        
        <el-form-item label="产品图片" prop="images" required>
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

        <!-- 产品特点 -->
        <el-divider content-position="left">
          <el-icon><Star /></el-icon>
          产品特点
        </el-divider>
        
        <el-form-item label="产品特点" prop="features">
          <el-select
            v-model="formData.features"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入产品特点"
            style="width: 100%;"
          >
            <el-option
              v-for="item in featureOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
          <div class="form-tip">支持多选，也可手动输入新的特点。如：国家地理标志产品、有机种植等</div>
        </el-form-item>

        <!-- 规格参数 -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          规格参数
        </el-divider>
        
        <el-form-item label="规格参数" prop="specifications">
          <div v-for="(spec, index) in formData.specifications" :key="index" class="spec-item">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-select
                  v-model="formData.specifications[index].label"
                  placeholder="请选择参数名称"
                  filterable
                  allow-create
                  default-first-option
                  style="width: 100%;"
                >
                  <el-option
                    v-for="item in specLabelOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </el-col>
              <el-col :span="12">
                <el-input
                  v-model="formData.specifications[index].value"
                  placeholder="参数值，如：巴南银针茶"
                />
              </el-col>
              <el-col :span="2">
                <el-button @click="removeSpec(index)" :icon="Delete" />
              </el-col>
            </el-row>
          </div>
          <el-button type="primary" text @click="addSpec" style="width: 100%; margin-top: 8px;">
            <el-icon><Plus /></el-icon>
            添加规格参数
          </el-button>
          <div class="form-tip">参数名称支持下拉选择或自定义，如：产品名称、产地、净含量、保质期等</div>
        </el-form-item>

        <!-- 购买规格配置 -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          购买规格（右侧购买表单用）
        </el-divider>
        
        <el-form-item label="购买规格" prop="purchaseSpecs">
          <div v-for="(spec, index) in formData.purchaseSpecs" :key="index" class="spec-item">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-select
                  v-model="formData.purchaseSpecs[index].label"
                  placeholder="请选择规格名称"
                  filterable
                  allow-create
                  default-first-option
                  style="width: 100%;"
                >
                  <el-option
                    v-for="item in purchaseSpecOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-input
                  v-model="formData.purchaseSpecs[index].value"
                  placeholder="规格值，如：250g，或与名称一致"
                />
              </el-col>
              <el-col :span="4">
                <el-input-number
                  v-model="formData.purchaseSpecs[index].price"
                  :min="0"
                  :precision="2"
                  placeholder="单价"
                  style="width: 100%;"
                />
              </el-col>
              <el-col :span="2">
                <el-button @click="formData.purchaseSpecs.splice(index, 1)" :icon="Delete" />
              </el-col>
            </el-row>
          </div>
          <el-button type="primary" text @click="formData.purchaseSpecs.push({ label: '', value: '', price: formData.price || 0 })" style="width: 100%; margin-top: 8px;">
            <el-icon><Plus /></el-icon>
            添加购买规格
          </el-button>
          <div class="form-tip">
            这里配置前台右侧购买区域的规格和对应单价；如果不配置，则默认只有一个规格，使用上面的基础价格。
          </div>
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
  Plus, InfoFilled, Picture, Document, Setting, Star, Delete
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import { 
  createAdminAttraction, 
  updateAdminAttraction, 
  getAdminAttractionById 
} from '@/api/cultureAttraction'
import type { CultureAttraction } from '@/api/cultureAttraction'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = ref(false)
const souvenirId = ref<number | null>(null)
const imageList = ref<UploadFiles>([])
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

// 产品特点预设选项
const featureOptions = [
  '国家地理标志产品',
  '官方认证',
  '地方特色产品',
  '传统手工制作',
  '有机种植',
  '绿色食品',
  '无添加防腐剂',
  '原生态原产地直供',
  '限量发售',
  '非遗工艺',
  '获奖产品',
  '品质保证'
]

// 规格参数名称预设选项
const specLabelOptions = [
  '产品名称',
  '产地',
  '净含量',
  '包装规格',
  '保质期',
  '生产日期',
  '贮存条件',
  '执行标准',
  '配料',
  '等级',
  '生产许可证号',
  '生产厂家'
]

// 购买规格预设选项
const purchaseSpecOptions = [
  '250g 礼盒装',
  '500g 礼盒装',
  '1000g 礼盒装',
  '单件装',
  '两件装',
  '家庭装',
  '礼盒装'
]

const formData = reactive<any>({
  name: '',
  badge: '',
  certified: false,
  price: 0,
  unit: '/盒',
  origin: '',
  description: '',
  images: [],
  mainImage: '',
  features: [],
  specifications: [],
  // 购买规格配置：[{ label, value, price }]
  purchaseSpecs: [],
  rating: 0,
  status: 1
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

// 添加特点
const addFeature = () => {
  formData.features.push('')
}

// 删除特点
const removeFeature = (index: number) => {
  formData.features.splice(index, 1)
}

// 添加规格参数
const addSpec = () => {
  formData.specifications.push({ label: '', value: '' })
}

// 删除规格参数
const removeSpec = (index: number) => {
  formData.specifications.splice(index, 1)
}

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseUrl}/upload/image?folder=attraction`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('travel_token') || localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 加载产品数据（编辑模式）
const loadSouvenirData = async (id: number) => {
  try {
    loading.value = true
    const result = await getAdminAttractionById(id)
    if (result.code === 200 && result.data) {
      const souvenir = result.data
      Object.assign(formData, {
        name: souvenir.name || '',
        // 标签与官方认证从后端字段直接回显
        badge: souvenir.badge || '',
        certified: !!souvenir.certified,
        // 价格：统一读取后端 ticketPrice
        price: souvenir.ticketPrice || 0,
        unit: souvenir.unit || '/盒',
        // 产地：优先 city，其次 province + city，最后 address
        origin: souvenir.city || [souvenir.province, souvenir.city].filter(Boolean).join('') || souvenir.address || '',
        description: souvenir.description || '',
        rating: souvenir.rating || 0,
        features: Array.isArray(souvenir.features)
          ? souvenir.features
          : (souvenir.features ? souvenir.features.split('\n').filter((f: string) => f.trim()) : []),
        specifications: Array.isArray(souvenir.specifications)
          ? souvenir.specifications
          : (souvenir.specifications ? JSON.parse(souvenir.specifications) : []),
        // 购买规格配置
        purchaseSpecs: (() => {
          if (!souvenir.sellSpecs) return []
          try {
            const parsed = JSON.parse(souvenir.sellSpecs)
            return Array.isArray(parsed) ? parsed : []
          } catch {
            return []
          }
        })(),
        status: souvenir.status !== undefined ? souvenir.status : 1
      })
      
      // 处理图片列表，去重防止每次编辑叠加一张
      const images: string[] = []
      if (souvenir.coverImage) images.push(souvenir.coverImage)
      if (souvenir.mainImage) images.push(souvenir.mainImage)
      if (souvenir.image) images.push(souvenir.image)
      if (souvenir.images) {
        if (Array.isArray(souvenir.images)) {
          images.push(...souvenir.images)
        } else if (typeof souvenir.images === 'string') {
          try {
            const parsed = JSON.parse(souvenir.images)
            if (Array.isArray(parsed)) {
              images.push(...parsed)
            } else {
              images.push(...souvenir.images.split(','))
            }
          } catch {
            images.push(...souvenir.images.split(','))
          }
        }
      }

      const uniqueImages = Array.from(new Set(images.filter((u: string) => !!u)))
      
      if (uniqueImages.length > 0) {
        imageList.value = uniqueImages.map((url: string, index: number) => ({
          uid: index,
          name: `image-${index}`,
          url: url,
          status: 'success'
        }))
        formData.mainImage = uniqueImages[0]
        formData.images = uniqueImages
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
      souvenirId.value = numId
      loadSouvenirData(numId)
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
  if (imageUrls.length > 0) {
    formData.mainImage = imageUrls[0]
  }
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
    badge: '',
    certified: false,
    price: 0,
    unit: '/盒',
    origin: '',
    description: '',
    images: [],
    mainImage: '',
    features: [],
    specifications: [],
    rating: 0,
    status: 1
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
        
        // 构建提交数据
        const submitData: any = { 
          name: formData.name,
          // 标签与官方认证直接对应后端字段
          badge: formData.badge || null,
          certified: formData.certified ? 1 : 0,
          unit: formData.unit || '/盒',
          description: formData.description || '',
          // 售价映射到后端 ticketPrice 字段
          ticketPrice: formData.price || 0,
          // 产地映射到 city / address，便于用户端展示
          city: formData.origin || '',
          address: formData.origin || '',
          // 后端 images 字段是 String，这里统一存成 JSON 字符串
          images: formData.images && formData.images.length > 0 ? JSON.stringify(formData.images) : null,
          coverImage: formData.mainImage || (formData.images && formData.images.length > 0 ? formData.images[0] : undefined),
          // features 字段后端是 String，用换行符拼接
          features: Array.isArray(formData.features) ? formData.features.join('\n') : (formData.features || ''),
          // 规格参数同样存成 JSON 字符串，保存到新增的 specifications 字段
          specifications: Array.isArray(formData.specifications) && formData.specifications.length > 0
            ? JSON.stringify(formData.specifications)
            : null,
          // 购买规格配置存入 sellSpecs 字段
          sellSpecs: Array.isArray(formData.purchaseSpecs) && formData.purchaseSpecs.length > 0
            ? JSON.stringify(formData.purchaseSpecs)
            : null,
          // 类型：为区分景点与特色周边，这里固定使用一个类型值（如 8 = 购物娱乐/周边）
          type: 8,
          rating: formData.rating || 0,
          status: formData.status || 1
        }
        
        let result
        if (isEdit.value && souvenirId.value) {
          // 编辑模式
          result = await updateAdminAttraction(souvenirId.value, submitData)
        } else {
          // 新增模式
          result = await createAdminAttraction(submitData)
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
.create-souvenir {
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
      
      .action-btn {
        border-radius: 4px;
        padding: 8px 20px;
        font-weight: 500;
        
        &.confirm-btn {
          background: #409eff;
          border-color: #409eff;
          color: #ffffff;
          
          &:hover {
            background: #66b1ff;
            border-color: #66b1ff;
          }
        }
      }
    }
  }
  
  .form-card {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    
    .form-tip {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
    
    .upload-tip {
      font-size: 12px;
      color: #909399;
      margin-top: 8px;
    }
  }
  
  .image-preview-dialog {
    .image-preview-wrapper {
      text-align: center;
      
      img {
        max-width: 100%;
        max-height: 70vh;
      }
    }
  }
  
  .feature-item {
    margin-bottom: 8px;
  }
  
  .spec-item {
    margin-bottom: 8px;
  }
}
</style>

