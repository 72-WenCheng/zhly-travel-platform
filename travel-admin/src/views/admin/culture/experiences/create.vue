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

    <!-- 体验表单 -->
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
            <el-form-item label="体验名称" prop="name" required>
              <el-input v-model="formData.name" placeholder="请输入体验名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryName" required>
              <el-input v-model="formData.categoryName" placeholder="请输入分类" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="位置" prop="location" required>
              <el-input v-model="formData.location" placeholder="请输入位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
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
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="时长" prop="duration">
              <el-input v-model="formData.duration" placeholder="如：约2小时" />
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

        <el-form-item label="简介" prop="slogan">
          <el-input
            v-model="formData.slogan"
            type="textarea"
            :rows="2"
            placeholder="请输入简介，如：跟着非遗老师亲手完成一件蜀绣作品，收藏一段东方美学"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="体验描述" prop="description" required>
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入服务描述"
          />
        </el-form-item>

        <el-form-item label="适合人群" prop="suitableFor">
          <el-input
            v-model="formData.suitableFor"
            placeholder="如：亲子/团建/非遗爱好者"
            maxlength="100"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 精选标签（体验亮点） -->
        <el-divider content-position="left">
          <el-icon><Star /></el-icon>
          精选标签（体验亮点）
        </el-divider>
        
        <el-form-item label="体验亮点" prop="features">
          <div class="dynamic-form-list">
            <div
              v-for="(item, index) in featuresList"
              :key="index"
              class="dynamic-form-item"
            >
              <el-row :gutter="10" style="width: 100%">
                <el-col :span="3">
                  <el-input
                    v-model="item.emoji"
                    placeholder="图标"
                    maxlength="2"
                    style="text-align: center"
                  >
                    <template #prefix>
                      <span style="font-size: 18px">{{ item.emoji || '✨' }}</span>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="7">
                  <el-input
                    v-model="item.title"
                    placeholder="标题（如：师傅一对一指导）"
                  />
                </el-col>
                <el-col :span="12">
                  <el-input
                    v-model="item.description"
                    placeholder="描述（如：非遗传承人现场教学，零基础也能上手）"
                  />
                </el-col>
                <el-col :span="2">
                  <el-button
                    type="danger"
                    :icon="Delete"
                    circle
                    @click="removeFeature(index)"
                  />
                </el-col>
              </el-row>
            </div>
            <el-button
              type="primary"
              :icon="Plus"
              @click="addFeature"
              style="margin-top: 10px"
            >
              添加亮点
            </el-button>
          </div>
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

        <!-- 体验流程（时间线） -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          体验流程（行程安排）
        </el-divider>
        
        <el-form-item label="行程安排" prop="flow">
          <div class="timeline-form-list">
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in flowList"
                :key="index"
                :timestamp="item.time"
                placement="top"
              >
                <div class="timeline-form-item">
                  <el-row :gutter="10">
                    <el-col :span="6">
                      <el-time-picker
                        v-model="item.time"
                        format="HH:mm"
                        value-format="HH:mm"
                        placeholder="选择时间"
                        style="width: 100%"
                      />
                    </el-col>
                    <el-col :span="8">
                      <el-input
                        v-model="item.title"
                        placeholder="标题（如：集合签到）"
                      />
                    </el-col>
                    <el-col :span="8">
                      <el-input
                        v-model="item.description"
                        placeholder="描述（如：老师介绍当日体验内容与注意事项）"
                      />
                    </el-col>
                    <el-col :span="2">
                      <el-button
                        type="danger"
                        :icon="Delete"
                        circle
                        @click="removeFlow(index)"
                      />
                    </el-col>
                  </el-row>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-button
              type="primary"
              :icon="Plus"
              @click="addFlow"
              style="margin-top: 10px"
            >
              添加时间点
            </el-button>
          </div>
        </el-form-item>

        <!-- 费用包含和体验提醒 -->
        <el-divider content-position="left">
          <el-icon><CircleCheck /></el-icon>
          费用包含与体验提醒
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="费用包含" prop="includes">
              <div class="simple-list-form">
                <div
                  v-for="(item, index) in includesList"
                  :key="index"
                  class="simple-list-item"
                >
                  <el-input
                    v-model="includesList[index]"
                    placeholder="如：全套材料工具"
                    style="flex: 1"
                  >
                    <template #prefix>
                      <el-icon color="#67c23a"><CircleCheck /></el-icon>
                    </template>
                  </el-input>
                  <el-button
                    type="danger"
                    :icon="Delete"
                    circle
                    style="margin-left: 10px"
                    @click="removeInclude(index)"
                  />
                </div>
                <el-button
                  type="primary"
                  :icon="Plus"
                  @click="addInclude"
                  style="margin-top: 10px; width: 100%"
                >
                  添加费用包含项
                </el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体验提醒" prop="notes">
              <div class="simple-list-form">
                <div
                  v-for="(item, index) in notesList"
                  :key="index"
                  class="simple-list-item"
                >
                  <el-input
                    v-model="notesList[index]"
                    placeholder="如：请提前15分钟到场"
                    style="flex: 1"
                  >
                    <template #prefix>
                      <el-icon color="#e6a23c"><WarningFilled /></el-icon>
                    </template>
                  </el-input>
                  <el-button
                    type="danger"
                    :icon="Delete"
                    circle
                    style="margin-left: 10px"
                    @click="removeNote(index)"
                  />
                </div>
                <el-button
                  type="primary"
                  :icon="Plus"
                  @click="addNote"
                  style="margin-top: 10px; width: 100%"
                >
                  添加体验提醒
                </el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 联系信息 -->
        <el-divider content-position="left">
          <el-icon><Phone /></el-icon>
          联系信息
        </el-divider>
        
        <el-form-item label="咨询电话" prop="contactPhone">
          <el-input
            v-model="formData.contactPhone"
            placeholder="请输入咨询电话，如：023-6688-9900"
            maxlength="20"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 教师介绍 -->
        <el-divider content-position="left">
          <el-icon><User /></el-icon>
          教师介绍
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教师姓名" prop="hostName">
              <el-input
                v-model="hostInfo.name"
                placeholder="如：林老师"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教师职称" prop="hostTitle">
              <el-input
                v-model="hostInfo.title"
                placeholder="如：省级非遗传承人 · 蜀绣导师"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="教师头像" prop="hostAvatar">
          <el-input
            v-model="hostInfo.avatar"
            placeholder="请输入头像URL，或使用默认头像"
          >
            <template #append>
              <el-button @click="handlePreviewAvatar">预览</el-button>
            </template>
          </el-input>
          <div v-if="hostInfo.avatar" class="avatar-preview" style="margin-top: 10px">
            <el-avatar :size="60" :src="hostInfo.avatar">
              {{ hostInfo.name?.charAt(0) || '师' }}
            </el-avatar>
          </div>
        </el-form-item>
        
        <el-form-item label="教师简介" prop="hostBio">
          <el-input
            v-model="hostInfo.bio"
            type="textarea"
            :rows="3"
            placeholder="如：从业20年，擅长将传统纹样与当代设计结合，带领学员完成富有东方意境的绣品。"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 状态设置 -->
        <el-divider content-position="left">
          <el-icon><Setting /></el-icon>
          状态设置
        </el-divider>
        
        <el-form-item label="体验状态" prop="status" required>
          <el-radio-group v-model="formData.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="maintenance">维护中</el-radio>
            <el-radio label="inactive">已关闭</el-radio>
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
  Plus, InfoFilled, Picture, Star, Document, Setting, Delete, CircleCheck, WarningFilled, Phone, User
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import { createExperience, updateExperience, getExperienceById } from '@/api/cultureExperience'
import type { CultureExperience } from '@/api/cultureExperience'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = ref(false)
const serviceId = ref<number | null>(null)
const imageList = ref<UploadFiles>([])
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

// 体验亮点列表
interface FeatureItem {
  emoji: string
  title: string
  description: string
}
const featuresList = ref<FeatureItem[]>([])

// 体验流程（时间线）列表
interface FlowItem {
  time: string
  title: string
  description: string
}
const flowList = ref<FlowItem[]>([])

// 费用包含列表
const includesList = ref<string[]>([])

// 体验提醒列表
const notesList = ref<string[]>([])

// 教师介绍信息
interface HostInfo {
  name: string
  title: string
  avatar: string
  bio: string
}
const hostInfo = ref<HostInfo>({
  name: '',
  title: '',
  avatar: '',
  bio: ''
})

const commonFeatures = [
  '采摘体验', '农家餐饮', '垂钓', '烧烤', '儿童游乐区', 
  '停车场', '免费WiFi', '宠物友好', '住宿', '温泉'
]

const commonIncludes = [
  '农家午餐', '农家晚餐', '早餐', '采摘体验', '垂钓体验', 
  '烧烤食材', '住宿', '茶水饮料', '导游服务'
]

const formData = reactive<CultureExperience>({
  name: '',
  categoryName: '',
  location: '',
  price: 0,
  duration: '',
  rating: 0,
  slogan: '',
  description: '',
  suitableFor: '',
  flow: '',
  notes: '',
  images: '',
  contactPhone: '',
  host: '',
  status: 'active'
})

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入体验名称', trigger: 'blur' }],
  categoryName: [{ required: true, message: '请输入分类', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  description: [{ required: true, message: '请输入体验描述', trigger: 'blur' }],
  images: [
    { 
      required: true, 
      message: '请上传体验图片', 
      trigger: 'change',
      validator: (_rule: any, _value: any, callback: any) => {
        if (!imageList.value || imageList.value.length === 0) {
          callback(new Error('请上传体验图片'))
        } else {
          callback()
        }
      }
    }
  ],
  status: [{ required: true, message: '请选择体验状态', trigger: 'change' }]
})

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseUrl}/upload/image?folder=experience`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('travel_token') || localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 加载服务数据（编辑模式）
const loadServiceData = async (id: number) => {
  try {
    loading.value = true
    const result = await getExperienceById(id)
    if (result.code === 200 && result.data) {
      const service = result.data
      Object.assign(formData, {
        name: service.name || '',
        categoryName: service.categoryName || '',
        location: service.location || '',
        price: service.price ? Number(service.price) : 0,
        duration: service.duration || '',
        rating: service.rating ? Number(service.rating) : 0,
        slogan: service.slogan || '',
        description: service.description || '',
        suitableFor: service.suitableFor || '',
        flow: service.flow || '',
        notes: service.notes || '',
        contactPhone: service.contactPhone || '',
        host: service.host || '',
        status: service.status || 'active'
      })
      
      // 安全地处理图片列表 - images 字段是 JSON 字符串
      imageList.value = []
      if (service.images) {
        try {
          let imagesArray: string[] = []
          
          if (typeof service.images === 'string') {
            // 尝试解析 JSON 字符串
            try {
              const parsed = JSON.parse(service.images)
              if (Array.isArray(parsed)) {
                imagesArray = parsed
              } else if (typeof parsed === 'string') {
                imagesArray = [parsed]
              }
            } catch {
              // 如果不是有效的 JSON，直接作为字符串使用
              imagesArray = [service.images]
            }
          } else if (Array.isArray(service.images)) {
            // 如果已经是数组，直接使用
            imagesArray = service.images
          }
          
          // 转换为上传组件需要的格式
          if (imagesArray.length > 0) {
            imageList.value = imagesArray.map((url: string, index: number) => ({
              uid: index,
              name: `image-${index}`,
              url: url,
              status: 'success'
            }))
          }
        } catch (error) {
          console.error('处理图片数据失败:', error)
          imageList.value = []
        }
      }
      
      // 处理体验亮点（features）字段
      featuresList.value = []
      if (service.features) {
        try {
          let featuresArray: FeatureItem[] = []
          if (typeof service.features === 'string') {
            try {
              const parsed = JSON.parse(service.features)
              if (Array.isArray(parsed)) {
                featuresArray = parsed.map((item: any) => ({
                  emoji: item.emoji || '✨',
                  title: item.title || '',
                  description: item.description || ''
                }))
              }
            } catch {
              // 如果不是有效的 JSON，忽略
            }
          } else if (Array.isArray(service.features)) {
            featuresArray = service.features
          }
          featuresList.value = featuresArray
        } catch (error) {
          console.error('处理体验亮点数据失败:', error)
          featuresList.value = []
        }
      }
      
      // 处理体验流程（flow）字段
      flowList.value = []
      if (service.flow) {
        try {
          let flowArray: FlowItem[] = []
          if (typeof service.flow === 'string') {
            try {
              const parsed = JSON.parse(service.flow)
              if (Array.isArray(parsed)) {
                flowArray = parsed.map((item: any, index: number) => {
                  if (typeof item === 'string') {
                    // 如果是字符串，转换为对象格式
                    const [hours, minutes] = [9 + Math.floor(index * 0.5), (index * 20) % 60]
                    return {
                      time: `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`,
                      title: item,
                      description: ''
                    }
                  } else {
                    return {
                      time: item.time || `${String(9 + index).padStart(2, '0')}:00`,
                      title: item.title || '',
                      description: item.description || ''
                    }
                  }
                })
              }
            } catch {
              // 如果不是有效的 JSON，忽略
            }
          } else if (Array.isArray(service.flow)) {
            flowArray = service.flow.map((item: any, index: number) => ({
              time: item.time || `${String(9 + index).padStart(2, '0')}:00`,
              title: item.title || '',
              description: item.description || ''
            }))
          }
          flowList.value = flowArray
        } catch (error) {
          console.error('处理体验流程数据失败:', error)
          flowList.value = []
        }
      }
      
      // 处理费用包含（includes）字段
      includesList.value = []
      if (service.includes) {
        try {
          let includesArray: string[] = []
          if (typeof service.includes === 'string') {
            try {
              const parsed = JSON.parse(service.includes)
              if (Array.isArray(parsed)) {
                includesArray = parsed.filter((item: any) => item && typeof item === 'string')
              }
            } catch {
              // 如果不是有效的 JSON，忽略
            }
          } else if (Array.isArray(service.includes)) {
            includesArray = service.includes.filter((item: any) => item && typeof item === 'string')
          }
          includesList.value = includesArray
        } catch (error) {
          console.error('处理费用包含数据失败:', error)
          includesList.value = []
        }
      }
      
      // 处理体验提醒（notes）字段
      notesList.value = []
      if (service.notes) {
        try {
          let notesArray: string[] = []
          if (typeof service.notes === 'string') {
            try {
              const parsed = JSON.parse(service.notes)
              if (Array.isArray(parsed)) {
                notesArray = parsed.filter((item: any) => item && typeof item === 'string')
              }
            } catch {
              // 如果不是有效的 JSON，忽略
            }
          } else if (Array.isArray(service.notes)) {
            notesArray = service.notes.filter((item: any) => item && typeof item === 'string')
          }
          notesList.value = notesArray
        } catch (error) {
          console.error('处理体验提醒数据失败:', error)
          notesList.value = []
        }
      }
      
      // 处理教师介绍（host）字段
      hostInfo.value = {
        name: '',
        title: '',
        avatar: '',
        bio: ''
      }
      if (service.host) {
        try {
          let hostData: HostInfo | null = null
          if (typeof service.host === 'string') {
            try {
              const parsed = JSON.parse(service.host)
              if (parsed && typeof parsed === 'object') {
                hostData = {
                  name: parsed.name || '',
                  title: parsed.title || '',
                  avatar: parsed.avatar || '',
                  bio: parsed.bio || ''
                }
              }
            } catch {
              // 如果不是有效的 JSON，忽略
            }
          } else if (typeof service.host === 'object') {
            hostData = {
              name: service.host.name || '',
              title: service.host.title || '',
              avatar: service.host.avatar || '',
              bio: service.host.bio || ''
            }
          }
          if (hostData) {
            hostInfo.value = hostData
          }
        } catch (error) {
          console.error('处理教师介绍数据失败:', error)
        }
      }
    } else {
      ElMessage.error(result.message || '加载服务数据失败')
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
  formData.images = imageUrls.length > 0 ? JSON.stringify(imageUrls) : ''
}

// 体验亮点管理
const addFeature = () => {
  featuresList.value.push({
    emoji: '✨',
    title: '',
    description: ''
  })
}

const removeFeature = (index: number) => {
  featuresList.value.splice(index, 1)
}

// 体验流程管理
const addFlow = () => {
  const lastTime = flowList.value.length > 0 
    ? flowList.value[flowList.value.length - 1].time 
    : '00:00'
  const [hours, minutes] = lastTime.split(':').map(Number)
  const nextMinutes = minutes + 20
  const nextHours = hours + Math.floor(nextMinutes / 60)
  const finalMinutes = nextMinutes % 60
  const nextTime = `${String(nextHours).padStart(2, '0')}:${String(finalMinutes).padStart(2, '0')}`
  
  flowList.value.push({
    time: nextTime,
    title: '',
    description: ''
  })
}

const removeFlow = (index: number) => {
  flowList.value.splice(index, 1)
}

// 费用包含管理
const addInclude = () => {
  includesList.value.push('')
}

const removeInclude = (index: number) => {
  includesList.value.splice(index, 1)
}

// 体验提醒管理
const addNote = () => {
  notesList.value.push('')
}

const removeNote = (index: number) => {
  notesList.value.splice(index, 1)
}

// 预览头像
const handlePreviewAvatar = () => {
  if (hostInfo.value.avatar) {
    window.open(hostInfo.value.avatar, '_blank')
  } else {
    ElMessage.warning('请先输入头像URL')
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
    categoryName: '',
    location: '',
    price: 0,
    duration: '',
    rating: 0,
    slogan: '',
    description: '',
    suitableFor: '',
    flow: '',
    notes: '',
    images: '',
    contactPhone: '',
    host: '',
    status: 'active'
  })
  imageList.value = []
  featuresList.value = []
  flowList.value = []
  includesList.value = []
  notesList.value = []
  hostInfo.value = {
    name: '',
    title: '',
    avatar: '',
    bio: ''
  }
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
        
        // 将体验亮点转换为 JSON 字符串
        const featuresJson = featuresList.value.length > 0 
          ? JSON.stringify(featuresList.value.filter(item => item.title || item.description))
          : ''
        
        // 将体验流程转换为 JSON 字符串
        const flowJson = flowList.value.length > 0
          ? JSON.stringify(flowList.value.filter(item => item.title || item.description))
          : ''
        
        // 将费用包含转换为 JSON 字符串
        const includesJson = includesList.value.length > 0
          ? JSON.stringify(includesList.value.filter(item => item && item.trim()))
          : ''
        
        // 将体验提醒转换为 JSON 字符串
        const notesJson = notesList.value.length > 0
          ? JSON.stringify(notesList.value.filter(item => item && item.trim()))
          : ''
        
        // 将教师介绍转换为 JSON 字符串
        const hostJson = (hostInfo.value.name || hostInfo.value.title || hostInfo.value.bio)
          ? JSON.stringify({
              name: hostInfo.value.name || '',
              title: hostInfo.value.title || '',
              avatar: hostInfo.value.avatar || '',
              bio: hostInfo.value.bio || ''
            })
          : ''
        
        // 构建提交数据
        const submitData: CultureExperience = { 
          name: formData.name,
          categoryName: formData.categoryName || '',
          location: formData.location,
          price: formData.price || 0,
          duration: formData.duration || '',
          rating: formData.rating || 0,
          slogan: formData.slogan || '',
          description: formData.description || '',
          suitableFor: formData.suitableFor || '',
          features: featuresJson,
          flow: flowJson,
          includes: includesJson,
          notes: notesJson,
          contactPhone: formData.contactPhone || '',
          host: hostJson,
          images: formData.images || '',
          status: formData.status || 'active'
        }
        
        let result
        if (isEdit.value && serviceId.value) {
          // 编辑模式
          result = await updateExperience(serviceId.value, submitData)
        } else {
          // 新增模式
          result = await createExperience(submitData)
        }
        
        if (result.code === 200) {
          ElMessage.success(isEdit.value ? '更新体验成功' : '创建体验成功')
          router.back()
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新体验失败' : '创建体验失败'))
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
  .dynamic-form-list {
    width: 100%;
    
    .dynamic-form-item {
      margin-bottom: 15px;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 8px;
      border: 1px solid #e4e7ed;
      
      &:hover {
        border-color: #409eff;
      }
    }
  }
  
  .timeline-form-list {
    width: 100%;
    
    .timeline-form-item {
      padding: 10px 0;
    }
    
    :deep(.el-timeline-item__timestamp) {
      font-size: 14px;
      color: #409eff;
      font-weight: 500;
    }
  }
  
  .simple-list-form {
    width: 100%;
    
    .simple-list-item {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 6px;
      border: 1px solid #e4e7ed;
      
      &:hover {
        border-color: #409eff;
      }
    }
  }
}

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

