<template>
  <div class="attraction-create-page">
    <BackButton />
    
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-icon-wrapper" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
        <el-icon :size="28"><Location /></el-icon>
      </div>
      <div class="header-title">
        <h1>{{ formTitle }}</h1>
        <p>填写景点详细信息</p>
      </div>
    </div>
    
    <div class="page-header-decoration"></div>
    
    <div class="page-content">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="0"
      label-position="top"
      class="edit-form"
    >
      <!-- 基本信息卡片 -->
      <div class="form-section-card">
        <div class="section-header">
          <div class="section-icon-wrapper" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <h3 class="section-title">基本信息</h3>
        </div>
        <div class="section-content">
          <el-row :gutter="24">
        <el-col :span="12">
              <el-form-item prop="name">
                <div class="form-label-with-icon">
                  <el-icon><Star /></el-icon>
                  <span>景点名称 <span class="required-star">*</span></span>
                </div>
                <el-input v-model="formData.name" placeholder="请输入景点名称" size="large" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
              <el-form-item prop="city">
                <div class="form-label-with-icon">
                  <el-icon><Location /></el-icon>
                  <span>所在城市 <span class="required-star">*</span></span>
                </div>
                <el-input v-model="formData.city" placeholder="请输入城市" size="large" />
          </el-form-item>
        </el-col>
      </el-row>
          <el-row :gutter="24">
        <el-col :span="12">
              <el-form-item prop="type">
                <div class="form-label-with-icon">
                  <el-icon><Grid /></el-icon>
                  <span>景点类型 <span class="required-star">*</span></span>
                </div>
                <el-select v-model="formData.type" placeholder="请选择景点类型" size="large" style="width: 100%">
              <el-option label="自然景观" :value="1" />
              <el-option label="人文景观" :value="2" />
              <el-option label="主题公园" :value="3" />
              <el-option label="博物馆" :value="4" />
              <el-option label="历史遗迹" :value="5" />
              <el-option label="美食街" :value="6" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
              <el-form-item prop="ticketPrice">
                <div class="form-label-with-icon">
                  <el-icon><Wallet /></el-icon>
                  <span>门票价格 <span class="required-star">*</span></span>
                </div>
            <el-input-number
              v-model="formData.ticketPrice"
              :min="0"
              :precision="2"
              placeholder="请输入门票价格"
                  size="large"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
          <el-row :gutter="24">
        <el-col :span="12">
              <el-form-item prop="phone">
                <div class="form-label-with-icon">
                  <el-icon><Phone /></el-icon>
                  <span>联系电话</span>
                </div>
                <el-input v-model="formData.phone" placeholder="请输入联系电话" size="large" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
              <el-form-item prop="website">
                <div class="form-label-with-icon">
                  <el-icon><Link /></el-icon>
                  <span>官网</span>
                </div>
                <el-input v-model="formData.website" placeholder="请输入官网地址" size="large" />
          </el-form-item>
        </el-col>
      </el-row>
        </div>
      </div>

      <!-- 位置信息卡片 -->
      <div class="form-section-card">
        <div class="section-header">
          <div class="section-icon-wrapper" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><Location /></el-icon>
          </div>
          <h3 class="section-title">位置信息</h3>
        </div>
        <div class="section-content">
          <el-form-item prop="address">
            <div class="form-label-with-icon">
              <el-icon><MapLocation /></el-icon>
              <span>景点地址 <span class="required-star">*</span></span>
            </div>
            <div class="address-input-wrapper">
              <el-autocomplete
                v-model="formData.address"
                :fetch-suggestions="searchAddress"
                placeholder="请输入地址或在地图上选择位置"
                :trigger-on-focus="false"
                @select="handleAddressSelect"
                clearable
                size="large"
                style="width: 100%"
                popper-class="address-autocomplete"
              >
                <template #default="{ item }">
                  <div class="address-suggestion">
                    <el-icon class="suggestion-icon"><Location /></el-icon>
                    <div class="suggestion-content">
                      <div class="suggestion-name">{{ item.name }}</div>
                      <div class="suggestion-address">{{ item.district }}{{ item.address }}</div>
                    </div>
                  </div>
                </template>
                <template #suffix>
                  <el-button 
                    v-if="formData.address && !gettingLocation" 
                    text 
                    type="primary" 
                    size="small"
                    @click.stop="getLocationFromAddress"
                    title="获取坐标"
                  >
                    <el-icon><Location /></el-icon>
                    获取坐标
                  </el-button>
                  <el-icon v-if="gettingLocation" class="is-loading"><Loading /></el-icon>
                </template>
              </el-autocomplete>
              <el-button 
                type="primary" 
                @click="openMapPicker"
                class="map-picker-btn"
                title="在地图上选择位置"
                size="large"
              >
                <el-icon><Location /></el-icon>
                地图选择
              </el-button>
            </div>
            <div v-if="formData.longitude && formData.latitude" class="coordinate-info">
              <el-icon><Location /></el-icon>
              <span>经度: {{ formData.longitude }} | 纬度: {{ formData.latitude }}</span>
            </div>
      </el-form-item>
        </div>
      </div>

      <!-- 时间与评分卡片 -->
      <div class="form-section-card">
        <div class="section-header">
          <div class="section-icon-wrapper" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><Clock /></el-icon>
          </div>
          <h3 class="section-title">时间与评分</h3>
        </div>
        <div class="section-content">
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item prop="openTime">
                <div class="form-label-with-icon">
                  <el-icon><Clock /></el-icon>
                  <span>开放时间</span>
                </div>
                <el-time-picker
                  v-model="timeRange"
                  is-range
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  @change="handleTimeRangeChange"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item prop="suggestedDuration">
                <div class="form-label-with-icon">
                  <el-icon><Clock /></el-icon>
                  <span>建议游玩时长</span>
                </div>
            <el-input-number
                  v-model="formData.suggestedDuration"
                  :min="1"
                  :max="24"
                  placeholder="小时"
                  size="large"
              style="width: 100%"
            />
                <span class="form-tip">小时</span>
          </el-form-item>
        </el-col>
            <el-col :span="8">
              <el-form-item prop="rating">
                <div class="form-label-with-icon">
                  <el-icon><Trophy /></el-icon>
                  <span>景点等级</span>
                </div>
                <el-select v-model="formData.rating" placeholder="请选择景点等级" size="large" style="width: 100%">
                  <el-option label="1A级景区" :value="1" />
                  <el-option label="2A级景区" :value="2" />
                  <el-option label="3A级景区" :value="3" />
                  <el-option label="4A级景区" :value="4" />
                  <el-option label="5A级景区" :value="5" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="score">
                <div class="form-label-with-icon">
                  <el-icon><Star /></el-icon>
                  <span>评分</span>
                </div>
            <el-input-number
                  v-model="formData.score"
                  :min="0"
                  :max="5"
                  :precision="1"
                  placeholder="评分0-5"
                  size="large"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item prop="bestSeason">
                <div class="form-label-with-icon">
                  <el-icon><Sunny /></el-icon>
                  <span>最佳游览季节</span>
                </div>
                <el-select v-model="formData.bestSeason" placeholder="请选择最佳季节" size="large" style="width: 100%">
                  <el-option label="春季" value="春季" />
                  <el-option label="夏季" value="夏季" />
                  <el-option label="秋季" value="秋季" />
                  <el-option label="冬季" value="冬季" />
                  <el-option label="全年" value="全年" />
                  <el-option label="四季皆宜" value="四季皆宜" />
                </el-select>
      </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 图片与标签卡片 -->
      <div class="form-section-card">
        <div class="section-header">
          <div class="section-icon-wrapper" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon><Picture /></el-icon>
          </div>
          <h3 class="section-title">图片与标签</h3>
        </div>
        <div class="section-content">
          <el-form-item prop="images">
            <div class="form-label-with-icon">
              <el-icon><Picture /></el-icon>
              <span>景点图片 <span class="required-star">*</span></span>
            </div>
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
              class="image-upload-wrapper"
        >
              <el-icon><Plus /></el-icon>
        </el-upload>
            <el-dialog v-model="dialogVisible" width="50%">
              <img w-full :src="dialogImageUrl" alt="预览" />
            </el-dialog>
            <div class="upload-tip">支持jpg/png格式，最多9张，建议尺寸800x600，第一张将作为封面图</div>
      </el-form-item>
          <el-form-item prop="tags">
            <div class="form-label-with-icon">
              <el-icon><CollectionTag /></el-icon>
              <span>景点标签</span>
            </div>
            <el-select
              v-model="formData.tags"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="选择或输入标签，多个标签用逗号分隔"
              size="large"
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
        </div>
      </div>

      <!-- 详细信息卡片 -->
      <div class="form-section-card">
        <div class="section-header">
          <div class="section-icon-wrapper" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
            <el-icon><Document /></el-icon>
          </div>
          <h3 class="section-title">详细信息</h3>
        </div>
        <div class="section-content">
          <el-form-item prop="description">
            <div class="form-label-with-icon">
              <el-icon><EditPen /></el-icon>
              <span>景点描述 <span class="required-star">*</span></span>
            </div>
        <el-input
          v-model="formData.description"
              type="textarea"
              :rows="5"
              placeholder="请输入景点描述，让游客了解景点的特色和魅力"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="features">
            <div class="form-label-with-icon">
              <el-icon><MagicStick /></el-icon>
              <span>特色介绍</span>
            </div>
            <el-input
              v-model="formData.features"
          type="textarea"
          :rows="4"
              placeholder="请输入景点特色介绍，突出景点的独特之处"
              size="large"
        />
      </el-form-item>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item prop="transportation">
                <div class="form-label-with-icon">
                  <el-icon><Bicycle /></el-icon>
                  <span>交通信息</span>
                </div>
                <el-input
                  v-model="formData.transportation"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入交通信息，如：轻轨2号线临江门站，公交多路可达"
                  size="large"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="notes">
                <div class="form-label-with-icon">
                  <el-icon><Warning /></el-icon>
                  <span>注意事项</span>
                </div>
                <el-input
                  v-model="formData.notes"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入注意事项，帮助游客做好出行准备"
                  size="large"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 状态设置卡片 -->
      <div class="form-section-card">
        <div class="section-header">
          <div class="section-icon-wrapper" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%)">
            <el-icon><Setting /></el-icon>
          </div>
          <h3 class="section-title">状态设置</h3>
        </div>
        <div class="section-content">
          <el-form-item prop="status">
            <div class="form-label-with-icon">
              <el-icon><CircleCheck /></el-icon>
              <span>景点状态</span>
            </div>
            <el-radio-group v-model="formData.status" size="large">
              <el-radio-button :label="1">
                <el-icon><CircleCheck /></el-icon>
                正常
              </el-radio-button>
              <el-radio-button :label="2">
                <el-icon><Tools /></el-icon>
                维护中
              </el-radio-button>
              <el-radio-button :label="0">
                <el-icon><CircleClose /></el-icon>
                已关闭
              </el-radio-button>
        </el-radio-group>
      </el-form-item>
        </div>
      </div>
    </el-form>

    <!-- 页面底部按钮 -->
    <div class="page-footer">
      <el-button class="white-btn" @click="handleCancel" :disabled="loading" size="large">取消</el-button>
        <el-button class="white-btn" @click="handleSubmit" :loading="loading" size="large">
          <el-icon v-if="!loading"><Check /></el-icon>
        {{ loading ? '提交中...' : '确定' }}
        </el-button>
    </div>
    </div>
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
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

const commonTags = [
  '自然风光', '人文历史', '主题公园', '博物馆', '历史遗迹', '美食',
  '夜景', '摄影', '亲子', '情侣', '徒步', '登山', '休闲', '文化'
]

const formData = reactive<Attraction>({
  name: '',
  city: '',
  type: 1,
  ticketPrice: 0,
  phone: '',
  website: '',
  address: '',
  longitude: undefined,
  latitude: undefined,
  openTime: '',
  suggestedDuration: 2,
  rating: 5,
  score: 4.5,
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
  address: [{ required: true, message: '请输入景点地址', trigger: 'blur' }]
})

const formTitle = computed(() => {
  return isEdit.value ? '编辑景点' : '添加景点'
})

const uploadUrl = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL}/api/upload/avatar`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
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
  if (response.code === 200 && response.data) {
    const imageUrl = response.data.url || response.data
    // 如果是第一张图片，设为封面
    if (imageList.value.length === 1) {
      formData.coverImage = imageUrl
    }
    // 更新图片列表字符串
    updateImagesString()
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
    // 移除失败的文件
    const index = imageList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      imageList.value.splice(index, 1)
    }
  }
}

// 移除图片
const handleRemove = (file: UploadFile) => {
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
const openMapPicker = () => {
  ElMessage.info('地图选择功能开发中，请使用地址搜索或手动输入地址')
  // TODO: 可以集成高德地图的拾取坐标组件
  // 这里可以打开一个对话框，嵌入高德地图，让用户点击地图选择位置
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    name: '',
    city: '',
    type: 1,
    ticketPrice: 0,
    phone: '',
    website: '',
    address: '',
    longitude: undefined,
    latitude: undefined,
    openTime: '',
    suggestedDuration: 2,
    rating: 5,
    score: 4.5,
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
          : formData.tags
        
        const submitData: any = { 
          ...formData,
          tags: tagsStr
        }
        
        // 确保images和coverImage正确
        if (!submitData.coverImage && imageList.value.length > 0) {
          submitData.coverImage = imageList.value[0].url || ''
        }
        
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
        ElMessage.error('提交失败: ' + (error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
:deep(.modern-edit-dialog) {
  .el-dialog {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(102, 126, 234, 0.1);
    border: none;
    
    .el-dialog__header {
      padding: 24px 32px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-bottom: none;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 1px;
        background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
      }
      
      .el-dialog__title {
        font-size: 20px;
        font-weight: 700;
        color: #ffffff;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        letter-spacing: 0.5px;
      }
      
      .el-dialog__headerbtn {
        top: 24px;
        right: 24px;
        
        .el-dialog__close {
          font-size: 22px;
          color: rgba(255, 255, 255, 0.9);
          transition: all 0.3s;
          
          &:hover {
            color: #ffffff;
            transform: rotate(90deg);
            background: rgba(255, 255, 255, 0.2);
            border-radius: 50%;
          }
        }
      }
    }
    
    .el-dialog__body {
      padding: 36px 44px;
      max-height: 75vh;
      overflow-y: auto;
      background: linear-gradient(to bottom, #f8f9fa 0%, #ffffff 100%);
      position: relative;
      font-size: 15px;
      line-height: 1.6;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
        background-size: 200% 100%;
        animation: gradient-flow 3s ease infinite;
      }
      
      &::-webkit-scrollbar {
        width: 8px;
      }
      
      &::-webkit-scrollbar-track {
        background: #f1f3f5;
        border-radius: 4px;
      }
      
      &::-webkit-scrollbar-thumb {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 4px;
        
        &:hover {
          background: linear-gradient(135deg, #5568d3 0%, #6a4192 100%);
        }
      }
    }
    
    .el-dialog__footer {
      padding: 20px 32px 28px;
      border-top: 1px solid rgba(102, 126, 234, 0.1);
      background: linear-gradient(to bottom, #ffffff 0%, #f8f9fa 100%);
    }
  }
}

@keyframes gradient-flow {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.dialog-header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  background-size: 200% 100%;
  border-radius: 2px;
  margin-bottom: 24px;
  animation: gradient-flow 3s ease infinite;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.edit-form {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  
  // 表单卡片样式
  .form-section-card {
    margin-bottom: 28px;
    background: #ffffff;
    border-radius: 16px;
    border: 1px solid #e5e7eb;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
      transform: translateY(-2px);
    }
    
    .section-header {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 20px 24px;
      background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
      border-bottom: 2px solid #f1f3f5;
      
      .section-icon-wrapper {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        
        .el-icon {
          font-size: 22px;
          color: #ffffff;
        }
      }
      
      .section-title {
        font-size: 18px;
        font-weight: 700;
        color: #1f2937;
        margin: 0;
        letter-spacing: 0.3px;
      }
    }
    
    .section-content {
      padding: 28px 32px;
      
  .el-form-item {
        margin-bottom: 24px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
  
  // 带图标的标签样式
  .form-label-with-icon {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;
    font-size: 14px;
    font-weight: 600;
    color: #374151;
    letter-spacing: 0.2px;
    
    .el-icon {
      font-size: 16px;
      color: #667eea;
    }
    
    .required-star {
      color: #ef4444;
      margin-left: 2px;
      font-weight: 700;
    }
  }
  
  .el-form-item {
    margin-bottom: 28px;
    transition: all 0.3s;
    
    &:hover {
    .el-form-item__label {
        color: #667eea;
      }
    }
    
    .el-form-item__label {
      font-weight: 600;
      color: #303133;
      font-size: 15px;
      line-height: 1.6;
      padding-right: 18px;
      letter-spacing: 0.3px;
      transition: color 0.3s;
      position: relative;
      
      &::before {
        content: '';
        position: absolute;
        left: -10px;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 16px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 2px;
        opacity: 0;
        transition: opacity 0.3s;
      }
    }
    
    &.is-required {
      .el-form-item__label {
        &::before {
          opacity: 1;
        }
      }
    }
    
    .el-input,
    .el-select,
    .el-textarea,
    .el-input-number {
      font-size: 15px;
      
      .el-input__wrapper,
      .el-select__wrapper {
        border-radius: 10px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 0 0 1px #e1e5e9 inset, 0 1px 3px rgba(0, 0, 0, 0.05);
        background: #ffffff;
        min-height: 42px;
        
        .el-input__inner {
          font-size: 15px;
          line-height: 1.5;
          color: #303133;
          font-weight: 400;
          padding: 0 14px;
          
          &::placeholder {
            color: #9ca3af;
            font-size: 14px;
            font-weight: 400;
            opacity: 0.8;
          }
        }
        
        &:hover {
          box-shadow: 0 0 0 1px #c0c4cc inset, 0 2px 4px rgba(0, 0, 0, 0.08);
          transform: translateY(-1px);
        }
        
        &:focus-within {
          box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) inset, 0 0 0 1px #667eea inset, 0 4px 12px rgba(102, 126, 234, 0.15);
          transform: translateY(-1px);
          
          .el-input__inner {
            color: #303133;
          }
        }
      }
    }
    
    .el-select {
      .el-select__wrapper {
        .el-select__placeholder {
          color: #9ca3af;
          font-size: 14px;
          font-weight: 400;
          opacity: 0.8;
        }
        
        .el-select__selected-item {
          font-size: 15px;
          color: #303133;
          font-weight: 400;
          line-height: 1.5;
        }
      }
    }
    
    .el-textarea {
      .el-textarea__inner {
        border-radius: 10px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 0 0 1px #e1e5e9 inset, 0 1px 3px rgba(0, 0, 0, 0.05);
        font-size: 15px;
        line-height: 1.6;
        color: #303133;
        font-weight: 400;
        padding: 12px 14px;
        font-family: inherit;
        
        &::placeholder {
          color: #9ca3af;
          font-size: 14px;
          font-weight: 400;
          opacity: 0.8;
        }
        
        &:hover {
          box-shadow: 0 0 0 1px #c0c4cc inset, 0 2px 4px rgba(0, 0, 0, 0.08);
        }
        
        &:focus {
          box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) inset, 0 0 0 1px #667eea inset, 0 4px 12px rgba(102, 126, 234, 0.15);
          color: #303133;
        }
      }
    }
    
    .el-input-number {
      .el-input__wrapper {
        .el-input__inner {
          text-align: left;
          font-weight: 500;
        }
      }
      
      .el-input-number__increase,
      .el-input-number__decrease {
        width: 28px;
        font-size: 14px;
        
        &:hover {
          color: #667eea;
        }
      }
    }
  }
  
  // 美化行间距
  .el-row {
    margin-bottom: 0;
  }
  
  // 美化列间距
  .el-col {
    padding: 0 10px;
  }
  
  // 美化选择器下拉选项
  :deep(.el-select-dropdown) {
    border-radius: 10px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    border: 1px solid rgba(102, 126, 234, 0.1);
    
    .el-select-dropdown__item {
      font-size: 15px;
      line-height: 1.6;
      padding: 12px 16px;
      color: #303133;
      font-weight: 400;
      transition: all 0.2s;
      
      &:hover {
        background: linear-gradient(90deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
        color: #667eea;
        font-weight: 500;
      }
      
      &.selected {
        background: linear-gradient(90deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
        color: #667eea;
        font-weight: 600;
      }
    }
  }
  
  // 美化时间选择器
  .el-time-picker {
    width: 100%;
    
    .el-input__wrapper {
      border-radius: 10px;
      min-height: 42px;
      
      .el-input__inner {
        font-size: 15px;
        color: #303133;
        font-weight: 400;
        
        &::placeholder {
          color: #9ca3af;
          font-size: 14px;
        }
      }
    }
  }
  
  // 美化数字输入框
  :deep(.el-input-number) {
    .el-input__wrapper {
      border-radius: 10px;
      min-height: 42px;
      
      .el-input__inner {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
      }
    }
  }
  
  // 美化多选标签
  :deep(.el-select__tags) {
    .el-tag {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.08) 100%);
      border-color: rgba(102, 126, 234, 0.3);
      color: #667eea;
      font-size: 13px;
      font-weight: 500;
      padding: 4px 10px;
      border-radius: 6px;
      line-height: 1.5;
      
      .el-tag__close {
        color: #667eea;
        
        &:hover {
          background-color: rgba(102, 126, 234, 0.2);
          color: #5568d3;
        }
      }
    }
  }
  
  // 美化多图片上传
  .image-upload-wrapper {
    :deep(.el-upload--picture-card) {
      border: 2px dashed #d0d7de;
      border-radius: 14px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
      width: 120px;
      height: 120px;
      
      &:hover {
        border-color: #667eea;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.08) 100%);
        transform: scale(1.05);
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.25);
        
        .el-icon {
          color: #667eea;
          transform: scale(1.15) rotate(90deg);
        }
      }
      
      .el-icon {
        font-size: 36px;
        color: #909399;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      }
    }
    
    :deep(.el-upload-list--picture-card) {
      .el-upload-list__item {
        border-radius: 14px;
        overflow: hidden;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border: 2px solid transparent;
        width: 120px;
        height: 120px;
        
        &:hover {
          transform: translateY(-6px) scale(1.05);
          box-shadow: 0 12px 28px rgba(0, 0, 0, 0.2);
          border-color: #667eea;
          z-index: 10;
        }
      }
    }
  }
  
  // 美化单选按钮组（改为按钮样式）
  :deep(.el-radio-group) {
    display: flex;
    gap: 16px;
    
    .el-radio-button {
      flex: 1;
      margin-right: 0;
      
      .el-radio-button__inner {
        width: 100%;
        padding: 14px 20px;
        border-radius: 10px;
        font-size: 15px;
        font-weight: 600;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border: 2px solid #e5e7eb;
        background: #ffffff;
        color: #6b7280;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        
        .el-icon {
          font-size: 18px;
        }
        
        &:hover {
          border-color: #667eea;
          color: #667eea;
          background: rgba(102, 126, 234, 0.05);
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
        }
      }
      
      &.is-active .el-radio-button__inner {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-color: #667eea;
        color: #ffffff;
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        
        .el-icon {
          color: #ffffff;
        }
      }
    }
  }
  
  // 美化按钮中的图标
  :deep(.el-button) {
    .el-icon {
      font-size: 16px;
      margin-right: 6px;
    }
  }
  
  // 美化地址输入框中的获取坐标按钮
  .el-input__suffix {
    .el-button {
      font-size: 13px;
      font-weight: 500;
      padding: 6px 12px;
      border-radius: 6px;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.25);
      }
    }
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 2px dashed #dcdfe6;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: #667eea;
      background: rgba(102, 126, 234, 0.05);
    }
  }

  .avatar {
    width: 178px;
    height: 178px;
    display: block;
    border-radius: 6px;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: color 0.3s;
  }
  
  &:hover .avatar-uploader-icon {
    color: #667eea;
  }
}

.upload-tip {
  font-size: 13px;
  color: #606266;
  margin-top: 14px;
  line-height: 1.7;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 10px;
  border-left: 4px solid #667eea;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  font-weight: 400;
  letter-spacing: 0.2px;
  
  &::before {
    content: '💡';
    margin-right: 8px;
    font-size: 14px;
  }
}

.form-tip {
  font-size: 13px;
  color: #909399;
  margin-left: 10px;
  font-weight: 500;
  letter-spacing: 0.2px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 24px 32px;
  background: linear-gradient(to bottom, #ffffff 0%, #f8f9fa 100%);
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  margin: 0 -40px -36px;
  
  // 确保 white-btn 样式优先级最高
  .white-btn {
    // 样式已在 .page-footer .white-btn 中定义，这里不需要重复
  }
  
  .el-button:not(.white-btn) {
    padding: 14px 40px;
    border-radius: 12px;
    font-weight: 600;
    font-size: 16px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    min-width: 140px;
    height: 52px;
    
    &.el-button--primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        transition: left 0.6s;
      }
      
      &:hover {
        background: linear-gradient(135deg, #5568d3 0%, #6a4192 100%);
        transform: translateY(-4px);
        box-shadow: 0 10px 28px rgba(102, 126, 234, 0.45);
        
        &::before {
          left: 100%;
        }
      }
      
      &:active {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
      }
      
      .el-icon {
        margin-right: 6px;
        font-size: 18px;
      }
    }
    
    &:not(.el-button--primary) {
      border: 2px solid #e5e7eb;
      background: #ffffff;
      color: #6b7280;
      
      &:hover {
        border-color: #667eea;
        color: #667eea;
        background: rgba(102, 126, 234, 0.05);
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(102, 126, 234, 0.2);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
  }
}

// 地址输入框样式
.address-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  
  .el-autocomplete {
    flex: 1;
  }
  
  .map-picker-btn {
    flex-shrink: 0;
    white-space: nowrap;
    border-radius: 10px;
    transition: border-color 0.3s ease;
    font-weight: 600;
    padding: 0 20px;
    
    &:hover {
      border-color: #909399 !important;
      transform: none;
      box-shadow: none;
    }
    
    &:active {
      transform: none;
    }
  }
}

.coordinate-info {
  margin-top: 10px;
  padding: 10px 14px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 8px;
  border-left: 3px solid #667eea;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  
  .el-icon {
    color: #667eea;
    font-size: 16px;
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

.address-suggestion {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  
  .suggestion-icon {
    color: #667eea;
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

// 页面容器样式
.attraction-create-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 24px 32px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  .header-icon-wrapper {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
    
    .el-icon {
      color: #ffffff;
    }
  }
  
  .header-title {
    flex: 1;
    
    h1 {
      margin: 0 0 8px 0;
      font-size: 28px;
      font-weight: 700;
      color: #1f2937;
      letter-spacing: 0.5px;
    }
    
    p {
      margin: 0;
      font-size: 14px;
      color: #6b7280;
    }
  }
}

.page-header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  background-size: 200% 100%;
  border-radius: 2px;
  margin-bottom: 24px;
  animation: gradient-flow 3s ease infinite;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.page-content {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.page-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding: 24px 0;
  
  :deep(.white-btn) {
    padding: 14px 40px !important;
    border-radius: 4px !important;
    font-weight: 500 !important;
    font-size: 16px !important;
    transition: all 0.3s !important;
    min-width: 140px !important;
    height: 52px !important;
    background: #ffffff !important;
    background-color: #ffffff !important;
    border: 1px solid #dcdfe6 !important;
    border-color: #dcdfe6 !important;
    color: #606266 !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    vertical-align: middle !important;
    line-height: 1.5 !important;
    box-shadow: none !important;
    outline: none !important;
    
    // 覆盖 Element Plus 的所有可能类型和状态
    &,
    &.el-button--primary,
    &.el-button--default,
    &.el-button--success,
    &.el-button--info,
    &.el-button--warning,
    &.el-button--danger {
      background: #ffffff !important;
      background-color: #ffffff !important;
      border: 1px solid #dcdfe6 !important;
      border-color: #dcdfe6 !important;
      color: #606266 !important;
      box-shadow: none !important;
    }
    
    // 覆盖所有 hover 状态
    &:hover:not([disabled]),
    &.el-button--primary:hover:not([disabled]),
    &.el-button--default:hover:not([disabled]) {
      background: #f5f7fa !important;
      background-color: #f5f7fa !important;
      border-color: #c0c4cc !important;
      color: #303133 !important;
      box-shadow: none !important;
      transform: none !important;
    }
    
    // 覆盖所有 active 状态
    &:active:not([disabled]),
    &.el-button--primary:active:not([disabled]),
    &.el-button--default:active:not([disabled]) {
      background: #f0f2f5 !important;
      background-color: #f0f2f5 !important;
      transform: none !important;
      box-shadow: none !important;
    }
    
    // 覆盖所有 focus 状态
    &:focus:not([disabled]),
    &:focus-visible:not([disabled]),
    &.el-button--primary:focus:not([disabled]),
    &.el-button--default:focus:not([disabled]) {
      background: #ffffff !important;
      background-color: #ffffff !important;
      border-color: #dcdfe6 !important;
      color: #606266 !important;
      box-shadow: none !important;
      outline: none !important;
    }
    
    // 覆盖 disabled 状态
    &[disabled],
    &.is-disabled {
      color: #c0c4cc !important;
      border-color: #e4e7ed !important;
      background: #ffffff !important;
      background-color: #ffffff !important;
    }
    
    // 覆盖 loading 状态
    &.is-loading {
      background: #ffffff !important;
      background-color: #ffffff !important;
      border-color: #dcdfe6 !important;
      color: #606266 !important;
    }
    
    .el-icon {
      margin-right: 6px;
      font-size: 18px;
    }
  }
}
</style>


