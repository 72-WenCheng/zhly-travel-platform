<template>
  <div class="create-attraction">
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

    <!-- 景点表单 -->
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
            <el-form-item label="景点名称" prop="name" required>
              <el-input v-model="formData.name" placeholder="请输入景点名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在城市" prop="city" required>
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="景点类型" prop="type" required>
              <el-select v-model="formData.type" placeholder="请选择景点类型" style="width: 100%">
                <el-option label="自然风光" :value="1" />
                <el-option label="人文历史" :value="2" />
                <el-option label="主题公园" :value="3" />
                <el-option label="美食街" :value="4" />
                <el-option label="古镇古村" :value="5" />
                <el-option label="温泉度假" :value="6" />
                <el-option label="宗教场所" :value="7" />
                <el-option label="购物娱乐" :value="8" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门票价格" prop="ticketPrice" required>
              <el-input-number
                v-model="formData.ticketPrice"
                :min="0"
                :precision="2"
                placeholder="请输入门票价格"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone" required>
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="官网" prop="website" required>
              <el-input v-model="formData.website" placeholder="请输入官网地址" />
            </el-form-item>
          </el-col>
        </el-row>
        

        <!-- 位置信息 -->
        <el-divider content-position="left">
          <el-icon><Location /></el-icon>
          位置信息
        </el-divider>
        
        <el-form-item label="景点地址" prop="address" required>
          <div class="address-select-wrapper">
            <el-button 
              @click="openMapPicker"
              size="large"
              class="map-select-btn"
            >
              <el-icon><MapLocation /></el-icon>
              <span>地图选择</span>
            </el-button>
            <div v-if="formData.address" class="selected-address">
              <el-icon><Location /></el-icon>
              <span>{{ formData.address }}</span>
              <el-button 
                text 
                type="danger" 
                size="small"
                @click="clearAddress"
              >
                <el-icon><CircleClose /></el-icon>
              </el-button>
            </div>
            <div v-if="formData.longitude && formData.latitude" class="coordinate-info">
              <span>经度: {{ formData.longitude }} | 纬度: {{ formData.latitude }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 时间与评分 -->
        <el-divider content-position="left">
          <el-icon><Clock /></el-icon>
          时间与评分
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="开放时间" prop="openTime" required>
            <el-time-picker
              v-model="timeRange"
              is-range
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="HH:mm"
              value-format="HH:mm"
              @change="handleTimeRangeChange"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="建议游玩时长" prop="suggestedDuration" required>
            <el-input-number
              v-model="formData.suggestedDuration"
              :min="1"
              :max="24"
              placeholder="小时"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最佳游览季节" prop="bestSeason" required>
            <el-select v-model="formData.bestSeason" placeholder="请选择最佳季节" style="width: 100%">
              <el-option label="春季" value="春季" />
              <el-option label="夏季" value="夏季" />
              <el-option label="秋季" value="秋季" />
              <el-option label="冬季" value="冬季" />
              <el-option label="全年" value="全年" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="景点等级" prop="rating" required>
            <el-select v-model="formData.rating" placeholder="请选择景点等级" style="width: 100%">
              <el-option label="1A级景区" :value="1" />
              <el-option label="2A级景区" :value="2" />
              <el-option label="3A级景区" :value="3" />
              <el-option label="4A级景区" :value="4" />
              <el-option label="5A级景区" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="评分" prop="score" required>
            <el-input-number
              v-model="formData.score"
              :min="0"
              :max="5"
              :precision="1"
              placeholder="评分0-5"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="景点状态" prop="status" required>
            <el-radio-group v-model="formData.status">
              <el-radio :label="1">正常</el-radio>
              <el-radio :label="2">维护中</el-radio>
              <el-radio :label="0">已关闭</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

        <!-- 图片与标签 -->
        <el-divider content-position="left">
          <el-icon><Picture /></el-icon>
          图片与标签
        </el-divider>
        
        <el-form-item label="景点图片" prop="images" required>
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

      <el-form-item label="景点标签" prop="tags" required>
        <el-select
          v-model="formData.tags"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="选择或输入标签"
          style="width: 100%"
        >
          <el-option
            v-for="tag in commonTags"
            :key="tag"
            :label="tag"
            :value="tag"
          />
        </el-select>
      </el-form-item>

        <!-- 详细信息 -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          详细信息
        </el-divider>
        
        <el-form-item label="景点描述" prop="description" required>
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="5"
          placeholder="请输入景点描述"
        />
      </el-form-item>

      <el-form-item label="特色介绍" prop="features" required>
        <el-input
          v-model="formData.features"
          type="textarea"
          :rows="4"
          placeholder="请输入景点特色介绍"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="交通信息" prop="transportation" required>
            <el-input
              v-model="formData.transportation"
              type="textarea"
              :rows="3"
              placeholder="请输入交通信息"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="注意事项" prop="notes" required>
            <el-input
              v-model="formData.notes"
              type="textarea"
              :rows="3"
              placeholder="请输入注意事项"
            />
          </el-form-item>
        </el-col>
      </el-row>
      </el-form>
    </el-card>
    
    <!-- 地图选择器 -->
    <MapPicker
      v-model="mapPickerVisible"
      :default-city="formData.city"
      :default-location="formData.longitude && formData.latitude ? { lng: formData.longitude, lat: formData.latitude } : undefined"
      @confirm="handleMapPickerConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Check, Location, Loading, InfoFilled, Star, Grid, Wallet, Phone, Link,
  Clock, Trophy, Sunny, Picture, CollectionTag, Document, EditPen, 
  MagicStick, Bicycle, Warning, Setting, CircleCheck, Tools, CircleClose, MapLocation
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import MapPicker from '@/components/MapPicker.vue'
import { uploadAvatar } from '@/api/user'
import type { Attraction } from '@/api/attraction'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import { createAttraction, updateAttraction, getAttractionById } from '@/api/attraction'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = ref(false)
const attractionId = ref<number | null>(null)
const gettingLocation = ref(false)
const timeRange = ref<[string, string] | null>(null)
const imageList = ref<UploadFiles>([])
const mapPickerVisible = ref(false)
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

const commonTags = [
  '美食', '夜景', '摄影', '亲子', '情侣', '徒步', '登山', '休闲', '文化'
]

const formData = reactive<Attraction>({
  name: '',
  city: '',
  province: '',
  type: 1,
  ticketPrice: 0,
  phone: '',
  website: '',
  address: '',
  longitude: undefined,
  latitude: undefined,
  openTime: '',
  suggestedDuration: undefined,
  rating: 5,
  score: undefined,
  coverImage: '',
  images: '',
  tags: '',
  features: '',
  transportation: '',
  bestSeason: '全年',
  notes: '',
  description: '',
  status: 1
})

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  type: [{ required: true, message: '请选择景点类型', trigger: 'change' }],
  ticketPrice: [{ required: true, message: '请输入门票价格', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  website: [{ required: true, message: '请输入官网地址', trigger: 'blur' }],
  address: [{ required: true, message: '请选择景点地址', trigger: 'blur' }],
  openTime: [{ required: true, message: '请选择开放时间', trigger: 'blur' }],
  suggestedDuration: [{ required: true, message: '请输入建议游玩时长', trigger: 'blur' }],
  bestSeason: [{ required: true, message: '请选择最佳游览季节', trigger: 'change' }],
  rating: [{ required: true, message: '请选择景点等级', trigger: 'change' }],
  score: [{ required: true, message: '请输入评分', trigger: 'blur' }],
  status: [{ required: true, message: '请选择景点状态', trigger: 'change' }],
  images: [
    { 
      required: true, 
      message: '请上传景点图片', 
      trigger: 'change',
      validator: (_rule: any, _value: any, callback: any) => {
        if (!imageList.value || imageList.value.length === 0) {
          callback(new Error('请上传景点图片'))
        } else {
          callback()
        }
      }
    }
  ],
  tags: [
    { 
      required: true, 
      message: '请选择或输入景点标签', 
      trigger: 'change',
      validator: (_rule: any, value: any, callback: any) => {
        if (!value || (Array.isArray(value) && value.length === 0) || (typeof value === 'string' && !value.trim())) {
          callback(new Error('请选择或输入景点标签'))
        } else {
          callback()
        }
      }
    }
  ],
  description: [{ required: true, message: '请输入景点描述', trigger: 'blur' }],
  features: [{ required: true, message: '请输入特色介绍', trigger: 'blur' }],
  transportation: [{ required: true, message: '请输入交通信息', trigger: 'blur' }],
  notes: [{ required: true, message: '请输入注意事项', trigger: 'blur' }]
})

const formTitle = computed(() => {
  return isEdit.value ? '编辑景点' : '添加景点'
})

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseUrl}/upload/image?folder=attraction`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('travel_token') || localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 加载景点数据（编辑模式）
const loadAttractionData = async (id: number) => {
  try {
    loading.value = true
    const result = await getAttractionById(id)
    if (result.code === 200 && result.data) {
      const attraction = result.data
      Object.assign(formData, attraction)
      
      // 处理开放时间
      if (formData.openTime && formData.openTime.includes('-')) {
        const times = formData.openTime.split('-')
        if (times.length === 2) {
          timeRange.value = [times[0].trim(), times[1].trim()]
        }
      }
      
      // 处理图片列表
      if (formData.images) {
        const imageUrls = typeof formData.images === 'string' 
          ? formData.images.split(',').filter(url => url.trim())
          : formData.images
        imageList.value = imageUrls.map((url: string, index: number) => ({
          uid: index,
          name: `image-${index}`,
          url: url.trim(),
          status: 'success'
        }))
        updateImagesString()
      }
      
      // 处理标签
      if (formData.tags && typeof formData.tags === 'string') {
        formData.tags = formData.tags.split(',').filter(tag => tag.trim())
      }
    } else {
      ElMessage.error('加载景点数据失败')
      router.back()
    }
  } catch (error: any) {
    console.error('加载景点数据失败:', error)
    ElMessage.error('加载景点数据失败: ' + (error.message || '未知错误'))
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
      attractionId.value = numId
      loadAttractionData(numId)
    } else {
      resetForm()
    }
  } else {
    resetForm()
  }
})

// 处理时间范围变化
const handleTimeRangeChange = (val: [string, string] | null) => {
  // 触发开放时间验证
  setTimeout(() => {
    formRef.value?.validateField('openTime')
  }, 100)
  if (val && val.length === 2) {
    formData.openTime = `${val[0]}-${val[1]}`
  } else {
    formData.openTime = ''
  }
}

// 地址失焦时自动获取经纬度
const handleAddressBlur = () => {
  if (formData.address && formData.address.trim().length > 5) {
    // 延迟一点获取，避免频繁调用
    setTimeout(() => {
      getLocationFromAddress()
    }, 500)
  }
}

// 根据地址获取经纬度
const getLocationFromAddress = async () => {
  if (!formData.address || !formData.address.trim()) {
    ElMessage.warning('请先输入地址')
    return
  }

  gettingLocation.value = true
  try {
    // 调用后端API进行地理编码
    const response = await request.get('/third-party/geocode', {
      params: {
        address: `${formData.city || ''}${formData.address}`.trim()
      }
    })

    if (response.code === 200 && response.data) {
      formData.longitude = response.data.longitude
      formData.latitude = response.data.latitude
      ElMessage.success('坐标获取成功')
    } else {
      // 如果API不可用，尝试使用高德地图API（前端直接调用）
      await getLocationFromAmap()
    }
  } catch (error: any) {
    console.error('获取坐标失败:', error)
    // 降级方案：尝试使用高德地图API
    await getLocationFromAmap()
  } finally {
    gettingLocation.value = false
  }
}

// 使用高德地图API获取坐标（备用方案）
const getLocationFromAmap = async () => {
  try {
    // 注意：这里需要在后端配置高德地图API Key，或者前端使用自己的Key
    const address = encodeURIComponent(`${formData.city || ''}${formData.address}`.trim())
    // 这里可以调用一个代理接口，避免在前端暴露API Key
    const response = await request.get(`/api/third-party/amap/geocode`, {
      params: { address }
    })

    if (response.code === 200 && response.data) {
      formData.longitude = response.data.longitude
      formData.latitude = response.data.latitude
      ElMessage.success('坐标获取成功')
    } else {
      ElMessage.warning('无法自动获取坐标，请手动在地图上选择位置')
    }
  } catch (error) {
    ElMessage.warning('无法自动获取坐标，请手动在地图上选择位置')
  }
}

// 图片上传成功
const handleImageSuccess = (response: any, file: UploadFile) => {
  // 触发图片验证
  setTimeout(() => {
    formRef.value?.validateField('images')
  }, 100)
  
  // 处理不同的响应格式
  let imageUrl = ''
  if (response && response.code === 200) {
    if (response.data) {
      // 标准格式：{ fileUrl: "...", fileName: "..." }
      imageUrl = response.data.fileUrl || response.data.url || response.data.avatar || response.data
    }
  } else if (response && typeof response === 'string') {
    // 直接返回URL字符串
    imageUrl = response
  } else if (response && response.url) {
    // { url: "..." }
    imageUrl = response.url
  }
  
  if (imageUrl) {
    // 更新文件对象的url
    file.url = imageUrl
    file.response = response
    
    // 如果是第一张图片，设为封面
    if (imageList.value.length === 1 || !formData.coverImage) {
      formData.coverImage = imageUrl
    }
    
    // 更新图片列表字符串
    updateImagesString()
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response?.message || '图片上传失败，无法获取图片地址')
    // 移除失败的文件
    const index = imageList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      imageList.value.splice(index, 1)
    }
  }
}

// 移除图片
const handleRemove = (file: UploadFile) => {
  // 触发图片验证
  setTimeout(() => {
    formRef.value?.validateField('images')
  }, 100)
  updateImagesString()
  // 如果删除的是第一张，更新封面图
  if (imageList.value.length > 0) {
    formData.coverImage = imageList.value[0].url || ''
  } else {
    formData.coverImage = ''
  }
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

// 更新图片字符串
const updateImagesString = () => {
  const imageUrls = imageList.value
    .map(file => file.url || file.response?.data?.url || file.response?.data)
    .filter(url => url)
  formData.images = imageUrls.join(',')
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

// 高德地图地址搜索
const searchAddress = async (queryString: string, cb: (suggestions: any[]) => void) => {
  if (!queryString || queryString.length < 2) {
    cb([])
    return
  }

  try {
    // 调用后端API获取高德地图地址建议
    const response = await request.get('/third-party/amap/inputtips', {
      params: {
        keywords: queryString,
        city: formData.city || '',
        citylimit: formData.city ? true : false
      }
    })

    if (response.code === 200 && response.data && Array.isArray(response.data)) {
      const suggestions = response.data.map((item: any) => ({
        value: item.name + (item.district ? ` - ${item.district}` : ''),
        name: item.name,
        district: item.district || '',
        address: item.address || '',
        location: item.location,
        adcode: item.adcode
      }))
      cb(suggestions)
    } else {
      cb([])
    }
  } catch (error) {
    console.error('地址搜索失败:', error)
    cb([])
  }
}

// 选择地址建议
const handleAddressSelect = async (item: any) => {
  formData.address = item.name + (item.address ? ` - ${item.address}` : '')
  
  // 从地区信息中提取省份（adcode前两位对应省份）
  if (item.adcode && !formData.province) {
    // adcode格式：110000（前两位是省级代码）
    // 可以通过adcode或者district字段提取省份
    const district = item.district || ''
    if (district) {
      // 尝试从district中提取省份（例如："北京市朝阳区" -> "北京市"）
      const provinceMatch = district.match(/^(.+?[省市区县])/)
      if (provinceMatch) {
        const fullDistrict = provinceMatch[1]
        // 如果包含"市"、"区"、"县"，去掉这些后缀保留省份部分
        if (fullDistrict.includes('市') && !fullDistrict.includes('省')) {
          // 直辖市：北京市 -> 北京市
          formData.province = fullDistrict.split('市')[0] + '市'
        } else if (fullDistrict.includes('省')) {
          // 省份：广东省 -> 广东省
          formData.province = fullDistrict.split('省')[0] + '省'
        } else {
          // 其他情况，使用district的前部分
          formData.province = fullDistrict
        }
      }
    }
  }
  
  // 如果有位置信息，直接设置经纬度
  if (item.location) {
    const [lng, lat] = item.location.split(',')
    formData.longitude = parseFloat(lng)
    formData.latitude = parseFloat(lat)
    ElMessage.success('地址已选择，坐标已自动获取')
  } else {
    // 否则调用地理编码API获取坐标
    await getLocationFromAddress()
  }
}

// 打开地图选择器
const clearAddress = () => {
  formData.address = ''
  formData.longitude = undefined
  formData.latitude = undefined
}

const openMapPicker = () => {
  mapPickerVisible.value = true
}

// 地图选择确认
const handleMapPickerConfirm = (data: {
  location: { lng: number; lat: number }
  address: string
  province?: string
  city?: string
}) => {
  formData.address = data.address
  formData.longitude = data.location.lng
  formData.latitude = data.location.lat
  
  // 自动填充省份和城市
  if (data.province && !formData.province) {
    formData.province = data.province
  }
  if (data.city && !formData.city) {
    formData.city = data.city
  }
  
  // 清除验证错误
  formRef.value?.clearValidate('address')
  
  ElMessage.success('位置选择成功')
}

// 重置表单
const resetForm = () => {
    Object.assign(formData, {
    name: '',
    city: '',
    province: '',
    type: 1,
    ticketPrice: 0,
    phone: '',
    website: '',
    address: '',
    longitude: undefined,
    latitude: undefined,
    openTime: '',
    suggestedDuration: undefined,
    rating: 5,
    score: undefined,
    coverImage: '',
    images: '',
    tags: [],
    features: '',
    transportation: '',
    bestSeason: '全年',
    notes: '',
    description: '',
    status: 1
  })
  timeRange.value = null
  imageList.value = []
  gettingLocation.value = false
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
        // 处理标签：如果是数组，转为逗号分隔的字符串
        const tagsStr = Array.isArray(formData.tags) 
          ? formData.tags.join(',') 
          : (formData.tags || '')
        
        // 确保图片数据正确
        updateImagesString()
        
        // 构建提交数据
        const submitData: any = { 
          name: formData.name,
          city: formData.city,
          province: formData.province || '',
          type: formData.type,
          ticketPrice: formData.ticketPrice ? Number(formData.ticketPrice) : 0,
          phone: formData.phone || '',
          website: formData.website || '',
          address: formData.address || '',
          longitude: formData.longitude,
          latitude: formData.latitude,
          openTime: formData.openTime || '',
          suggestedDuration: formData.suggestedDuration ? Number(formData.suggestedDuration) : null,
          bestSeason: formData.bestSeason || '',
          rating: formData.rating ? Number(formData.rating) : 5,
          score: formData.score ? Number(formData.score) : null,
          coverImage: formData.coverImage || (imageList.value.length > 0 ? (imageList.value[0].url || '') : ''),
          images: formData.images || '',
          tags: tagsStr,
          description: formData.description || '',
          features: formData.features || '',
          transportation: formData.transportation || '',
          notes: formData.notes || '',
          status: formData.status || 1
        }
        
        // 移除空字段（可选，根据后端要求）
        Object.keys(submitData).forEach(key => {
          if (submitData[key] === null || submitData[key] === undefined || submitData[key] === '') {
            // 对于某些字段，保留空字符串或null
            if (!['province', 'phone', 'website', 'address', 'openTime', 'description', 'features', 'transportation', 'notes'].includes(key)) {
              delete submitData[key]
            }
          }
        })
        
        let result
        if (isEdit.value && attractionId.value) {
          // 编辑模式
          result = await updateAttraction(attractionId.value, submitData)
        } else {
          // 新增模式
          result = await createAttraction(submitData)
        }
        
        if (result.code === 200) {
          ElMessage.success(isEdit.value ? '更新景点成功' : '创建景点成功')
          router.back()
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新景点失败' : '创建景点失败'))
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
.create-attraction {
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
        background-color: #ffffff;
        border: 1px solid #dcdfe6;
        color: #606266;
        transition: none;
        
        &:hover {
          background-color: #f5f7fa;
          border-color: #c0c4cc;
          color: #606266;
          box-shadow: none;
          transform: none;
        }
        
        &.confirm-btn {
          font-weight: 600;
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
      
      .el-input-number {
        .el-input__wrapper {
          border-radius: 4px;
        }
      }
      
      .el-select {
        .el-input__wrapper {
          border-radius: 4px;
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


// 地址自动完成下拉框样式
:deep(.address-autocomplete) {
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(102, 126, 234, 0.1);
  
  .el-autocomplete-suggestion__list {
    padding: 8px 0;
    
    .el-autocomplete-suggestion__item {
      padding: 12px 16px;
      transition: all 0.2s;
      
      &:hover {
        background: linear-gradient(90deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
      }
      
      &.highlighted {
        background: linear-gradient(90deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
        color: #667eea;
      }
    }
  }
}

// 覆盖地址自动完成下拉框为白灰系风格
::deep(.address-autocomplete) {
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.08);
  border: 1px solid #e4e7ed;
  
  .el-autocomplete-suggestion__list {
    padding: 8px 0;
    
    .el-autocomplete-suggestion__item {
      padding: 12px 16px;
      transition: background-color 0.15s ease;
      
      &:hover {
        background: #f5f7fa;
      }
      
      &.highlighted {
        background: #f0f0f0;
        color: #303133;
      }
    }
  }
}

.address-suggestion {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  
  .suggestion-icon {
    color: #909399;
    font-size: 18px;
    margin-top: 2px;
    flex-shrink: 0;
  }
  
  .suggestion-content {
    flex: 1;
    min-width: 0;
    
    .suggestion-name {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
      line-height: 1.4;
    }
    
    .suggestion-address {
      font-size: 13px;
      color: #909399;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.address-select-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  
  .map-select-btn {
    width: 100%;
    padding: 14px 20px;
    border-radius: 8px;
    font-weight: 500;
    border: 2px dashed #409eff;
    background: #ecf5ff;
    color: #409eff;
    transition: all 0.3s;
    
    .el-icon {
      margin-right: 8px;
      font-size: 18px;
    }
    
    &:hover {
      border-color: #66b1ff;
      background: #d9ecff;
      color: #66b1ff;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
  
  .selected-address {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: #f5f7fa;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
    
    .el-icon {
      color: #667eea;
      font-size: 16px;
    }
    
    span {
      flex: 1;
      color: #303133;
      font-size: 14px;
    }
  }
}

.coordinate-info {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
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

/* 去除图片卡片上“按 Delete 键可删除”的文字提示，但保留删除按钮 */
:deep(.el-upload-list__item::after) {
  display: none !important;
}

/* 放大图片预览对话框样式优化为自适应白灰系 */
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


