<template>
  <div class="edit-plan">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>编辑攻略</h2>
      </div>
      <div class="header-actions">
        <el-button class="square-btn" @click="saveDraft">保存草稿</el-button>
        <el-button class="square-btn" @click="publishPlan">提交审核</el-button>
      </div>
    </div>

    <!-- 攻略表单 -->
    <el-card class="form-card" v-loading="loading">
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="100px">
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <el-icon><InfoFilled /></el-icon>
          基本信息
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="攻略标题" prop="title">
              <el-input v-model="planForm.title" placeholder="请输入攻略标题" maxlength="50" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的地" prop="destination">
              <el-input v-model="planForm.destination" placeholder="请输入目的地" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="出行天数">
              <el-input-number 
                :model-value="computedDays" 
                :min="1" 
                :max="30" 
                style="width: 100%" 
                disabled
                placeholder="自动计算"
              />
              <div class="field-tip">根据行程安排自动计算</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="预算">
              <el-input-number 
                :model-value="computedBudget" 
                :min="0" 
                style="width: 100%" 
                disabled
                placeholder="自动计算"
              />
              <div class="field-tip">根据费用明细自动计算</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="出行人数" prop="people" required>
              <el-input-number v-model="planForm.people" :min="1" :max="20" style="width: 100%" placeholder="请输入人数" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="攻略类型" prop="type">
              <el-select v-model="planForm.type" placeholder="请选择类型" style="width: 100%">
                <el-option label="自由行" :value="1" />
                <el-option label="跟团游" :value="2" />
                <el-option label="自驾游" :value="3" />
                <el-option label="背包客" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="攻略描述" prop="description">
          <el-input 
            v-model="planForm.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入攻略描述，让其他人了解这次旅行的特色..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 快速信息 -->
        <el-divider content-position="left">
          <el-icon><Lightning /></el-icon>
          快速信息
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最佳季节" prop="bestSeason">
              <el-select v-model="planForm.bestSeason" placeholder="请选择最佳季节" style="width: 100%">
                <el-option 
                  v-for="season in seasonOptions" 
                  :key="season.value"
                  :label="season.label" 
                  :value="season.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="适合人群" prop="suitableFor">
              <el-select v-model="planForm.suitableFor" placeholder="请选择适合人群" style="width: 100%">
                <el-option label="所有人" value="所有人" />
                <el-option label="情侣" value="情侣" />
                <el-option label="亲子" value="亲子" />
                <el-option label="独自" value="独自" />
                <el-option label="朋友" value="朋友" />
                <el-option label="老人" value="老人" />
                <el-option label="学生" value="学生" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="难度等级" prop="difficultyLevel">
              <el-rate 
                v-model="planForm.difficultyLevel" 
                :max="5"
                show-text
                :texts="['非常简单', '简单', '一般', '有挑战', '困难']"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 行程安排 -->
        <el-divider content-position="left">
          <el-icon><Calendar /></el-icon>
          行程安排
        </el-divider>
        
        <div class="schedule-section">
          <div v-for="(day, dayIndex) in planForm.schedule" :key="dayIndex" class="day-schedule">
            <div class="day-header">
              <h4>
                <el-icon><Sunny /></el-icon>
                第{{ dayIndex + 1 }}天
              </h4>
              <el-tooltip content="删除这一天" placement="top" v-if="planForm.schedule.length > 1">
                <el-button class="icon-btn delete-btn" text circle @click="removeDay(dayIndex)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
            
            <div class="day-content">
              <div v-for="(item, itemIndex) in day.items" :key="itemIndex" class="schedule-item">
                <el-row :gutter="12">
                  <el-col :span="6">
                    <el-time-picker v-model="item.time" placeholder="时间" style="width: 100%" />
                  </el-col>
                  <el-col :span="10">
                    <el-input v-model="item.activity" placeholder="活动内容" />
                  </el-col>
                  <el-col :span="6">
                    <el-input v-model="item.location" placeholder="地点" />
                  </el-col>
                  <el-col :span="2">
                    <el-tooltip content="删除活动" placement="top" v-if="day.items.length > 1">
                      <el-button class="icon-btn delete-btn" text circle @click="removeItem(dayIndex, itemIndex)">
                        <el-icon><Close /></el-icon>
                      </el-button>
                    </el-tooltip>
                  </el-col>
                </el-row>
              </div>
              
              <el-tooltip content="添加活动" placement="top">
                <el-button class="icon-btn" text circle @click="addItem(dayIndex)">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>
          
          <el-tooltip content="添加一天" placement="top">
            <el-button class="icon-btn" text circle @click="addDay">
              <el-icon><Plus /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
        
        <!-- 费用明细 -->
        <el-divider content-position="left">
          <el-icon><Wallet /></el-icon>
          费用明细
        </el-divider>
        
        <div class="cost-section">
          <div v-for="(cost, costIndex) in planForm.costs" :key="costIndex" class="cost-item">
            <el-row :gutter="12">
              <el-col :span="10">
                <el-input v-model="cost.category" placeholder="费用类别（如：交通、住宿、餐饮）" />
              </el-col>
              <el-col :span="12">
                <el-input-number v-model="cost.amount" :min="0" :max="100000" placeholder="金额" style="width: 100%" />
              </el-col>
              <el-col :span="2">
                <el-tooltip content="删除费用" placement="top" v-if="planForm.costs.length > 1">
                  <el-button class="icon-btn delete-btn" text circle @click="removeCost(costIndex)">
                    <el-icon><Close /></el-icon>
                  </el-button>
                </el-tooltip>
              </el-col>
            </el-row>
          </div>
          
          <el-tooltip content="添加费用" placement="top">
            <el-button class="icon-btn" text circle @click="addCost">
              <el-icon><Plus /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
        
        <!-- 注意事项 -->
        <el-divider content-position="left">
          <el-icon><Warning /></el-icon>
          注意事项
        </el-divider>
        
        <el-form-item label="注意事项" prop="notes">
          <el-input 
            v-model="planForm.notes" 
            type="textarea" 
            :rows="5"
            placeholder="请输入注意事项，如天气、交通、安全提示等..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 标签 -->
        <el-divider content-position="left">
          <el-icon><PriceTag /></el-icon>
          攻略标签
        </el-divider>
        
        <el-form-item label="攻略标签">
          <div class="tag-container">
            <!-- 已选标签 -->
            <el-tag
              v-for="tag in planForm.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              type="primary"
              effect="plain"
              style="margin-right: 10px; margin-bottom: 10px;"
            >
              {{ tag }}
            </el-tag>
            <!-- 可选标签列表 -->
            <div class="tag-selector">
              <el-tag
                v-for="tag in availableTags"
                :key="tag"
                v-if="!planForm.tags.includes(tag)"
                @click="addTag(tag)"
                effect="plain"
                style="margin-right: 10px; margin-bottom: 10px; cursor: pointer;"
                :class="{ 'tag-available': true }"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
        
        <!-- 封面图片 -->
        <el-divider content-position="left">
          <el-icon><Picture /></el-icon>
          封面图片
        </el-divider>
        
        <el-form-item label="上传封面">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :file-list="coverFileList"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
            :on-error="handleCoverError"
            :on-preview="handlePicturePreview"
            :on-remove="handleCoverRemove"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸：800x600，支持 JPG、PNG 格式，大小不超过 2MB；可上传多张图片</div>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图片大图预览（与创建攻略复用同一风格） -->
    <el-dialog
      v-model="previewVisible"
      width="720px"
      :show-close="true"
      :close-on-click-modal="true"
      class="image-preview-dialog"
    >
      <div class="image-preview-wrapper" v-if="previewImageUrl">
        <img :src="previewImageUrl" alt="预览图片" class="image-preview-img" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, InfoFilled, Calendar, 
  Wallet, Warning, PriceTag, Picture, Delete, Close, Sunny, Lightning
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const planId = ref(route.params.id as string)

const loading = ref(false)
const planFormRef = ref()

// 最佳季节选项列表
const seasonOptions = [
  { label: '全年', value: '全年' },
  { label: '春季', value: '春季' },
  { label: '夏季', value: '夏季' },
  { label: '秋季', value: '秋季' },
  { label: '冬季', value: '冬季' },
  { label: '春秋', value: '春秋' }
]

// 攻略表单
const planForm = reactive({
  title: '',
  destination: '',
  days: 1,  // 保留字段用于提交，但由计算属性自动更新
  budget: 0,  // 保留字段用于提交，但由计算属性自动更新
  people: 1,
  type: 1,
  description: '',
  bestSeason: '',
  suitableFor: '',
  difficultyLevel: 0,
  schedule: [
    {
      items: [
        { time: '', activity: '', location: '' }
      ]
    }
  ],
  costs: [
    { category: '', amount: 0 }
  ],
  notes: '',
  tags: [] as string[],
  coverImage: ''
})

// 计算属性：自动计算出行天数（根据行程安排）
const computedDays = computed(() => {
  return planForm.schedule ? planForm.schedule.length : 1
})

// 计算属性：自动计算总预算（根据费用明细）
const computedBudget = computed(() => {
  if (!planForm.costs || planForm.costs.length === 0) return 0
  return planForm.costs.reduce((sum, cost) => {
    return sum + (Number(cost.amount) || 0)
  }, 0)
})

// 监听计算属性的变化，自动更新表单字段（用于提交）
watch(computedDays, (newDays) => {
  planForm.days = newDays
}, { immediate: true })

watch(computedBudget, (newBudget) => {
  planForm.budget = newBudget
}, { immediate: true })

// 表单验证规则
// 自定义验证器 - 行程安排验证
const validateSchedule = (rule: any, value: any, callback: any) => {
  if (!planForm.schedule || planForm.schedule.length === 0) {
    callback(new Error('请至少添加一天行程'))
  } else {
    // 检查每天是否至少有一个活动
    const hasEmptyDay = planForm.schedule.some((day: any) => {
      return !day.items || day.items.length === 0 || !day.items.some((item: any) => item.activity)
    })
    if (hasEmptyDay) {
      callback(new Error('每天至少需要添加一个活动内容'))
    } else {
      callback()
    }
  }
}

// days 和 budget 已改为自动计算，移除验证器

const planRules = {
  title: [
    { required: true, message: '请输入攻略标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度应在5-50个字符之间', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' },
    { min: 2, max: 20, message: '目的地长度应在2-20个字符之间', trigger: 'blur' }
  ],
  // days 和 budget 已改为自动计算，移除验证规则
  people: [
    { required: true, message: '请输入出行人数', trigger: 'change' },
    { type: 'number', min: 1, max: 20, message: '出行人数应在1-20人之间', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择攻略类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入攻略描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度应在10-500个字符之间', trigger: 'blur' }
  ]
}

// 标签相关 - 标准标签列表（与后端一致）
const availableTags = ref([
  '摄影', '美食', '文化', '自然风光', '历史', '建筑',
  '徒步', '温泉', '博物馆', '夜生活', '夜景', '购物',
  '度假', '情侣', '亲子', '休闲'
])

// 上传相关
const uploadUrl = ref('/api/upload/image')
const coverFileList = ref<any[]>([])
const previewVisible = ref(false)
const previewImageUrl = ref('')

// 上传请求头（需要token）
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('travel_token') || localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 加载攻略详情
const loadPlanDetail = async () => {
  loading.value = true
  try {
    const result = await request.get(`/travel-plan/${planId.value}`)
    if (result.code === 200) {
      const data = result.data
      
      // 解析标签
      let tagsArray = []
      if (data.tags) {
        if (typeof data.tags === 'string') {
          tagsArray = data.tags.split(',').filter((t: string) => t.trim())
        } else if (Array.isArray(data.tags)) {
          tagsArray = data.tags
        }
      }
      
      // 解析行程安排
      let scheduleData = [{ items: [{ time: '', activity: '', location: '' }] }]
      if (data.itinerary) {
        try {
          scheduleData = JSON.parse(data.itinerary)
        } catch (e) {
          console.error('解析行程数据失败:', e)
        }
      }
      
      // 解析费用明细
      let costsData = [{ category: '', amount: 0 }]
      if (data.costDetail) {
        try {
          costsData = JSON.parse(data.costDetail)
        } catch (e) {
          console.error('解析费用数据失败:', e)
        }
      }
      
      // 解析图片：优先使用coverImage，如果没有则从images字段提取第一张
      let coverImageValue = ''
      if (data.coverImage) {
        coverImageValue = data.coverImage
      } else if (data.images) {
        const imageList = typeof data.images === 'string' 
          ? data.images.split(',').filter((img: string) => img.trim())
          : data.images
        if (imageList.length > 0) {
          coverImageValue = imageList[0]
        }
      }
      
      Object.assign(planForm, {
        title: data.title || '',
        destination: data.destination || '',
        days: data.days || 1,
        budget: data.budget || 0,
        people: data.personCount || data.people || 1,
        type: data.travelType || data.type || 1,
        description: data.description || '',
        bestSeason: data.bestSeason || '全年',
        suitableFor: data.suitableFor || '所有人',
        difficultyLevel: data.difficultyLevel || 1,
        schedule: scheduleData,
        costs: costsData,
        notes: data.notice || data.notes || '',
        tags: tagsArray,
        coverImage: coverImageValue
      })

      // 初始化封面缩略图列表
      coverFileList.value = coverImageValue
        ? [{ name: '封面', url: coverImageValue }]
        : []
      
      ElMessage.success('加载成功')
    } else {
      ElMessage.error(result.message || '加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载攻略详情失败:', error)
    ElMessage.error('加载失败，请稍后重试')
    setTimeout(() => {
      router.back()
    }, 1500)
  } finally {
    loading.value = false
  }
}

// 添加一天
const addDay = () => {
  planForm.schedule.push({
    items: [
      { time: '', activity: '', location: '', cost: '' }
    ]
  })
}

// 删除一天
const removeDay = (dayIndex: number) => {
  if (planForm.schedule.length > 1) {
    planForm.schedule.splice(dayIndex, 1)
  }
}

// 添加活动
const addItem = (dayIndex: number) => {
  planForm.schedule[dayIndex].items.push({
    time: '',
    activity: '',
    location: ''
  })
}

// 删除活动
const removeItem = (dayIndex: number, itemIndex: number) => {
  if (planForm.schedule[dayIndex].items.length > 1) {
    planForm.schedule[dayIndex].items.splice(itemIndex, 1)
  }
}

// 添加费用
const addCost = () => {
  planForm.costs.push({
    category: '',
    amount: 0
  })
}

// 删除费用
const removeCost = (costIndex: number) => {
  if (planForm.costs.length > 1) {
    planForm.costs.splice(costIndex, 1)
  }
}

// 添加标签
const addTag = (tag: string) => {
  if (tag && !planForm.tags.includes(tag)) {
    planForm.tags.push(tag)
  }
}

// 删除标签
const removeTag = (tag: string) => {
  const index = planForm.tags.indexOf(tag)
  if (index > -1) {
    planForm.tags.splice(index, 1)
  }
}

// 上传封面前验证
const beforeCoverUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 从上传响应中提取图片地址（与创建攻略保持一致）
const extractImageUrl = (response: any) => {
  if (!response) return ''
  if (response.code === 200 && response.data) {
    const data = response.data
    if (typeof data === 'string') return data
    if (data.fileUrl) return data.fileUrl
    if (data.url) return data.url
    if (data.filePath) {
      return data.filePath.startsWith('/') ? data.filePath : '/' + data.filePath
    }
    return String(data)
  }
  if (response.url) return response.url
  if (response.fileUrl) return response.fileUrl
  return ''
}

// 上传成功（维护缩略图列表和封面字段）
const handleCoverSuccess = (response: any, file: any, fileList: any[]) => {
  console.log('上传响应:', response)
  const imageUrl = extractImageUrl(response)

  if (!imageUrl) {
    ElMessage.error('上传成功但无法获取图片地址')
    return
  }

  // 更新缩略图列表
  coverFileList.value = fileList.map((item) => {
    const url = item.url || extractImageUrl(item.response) || imageUrl
    return { ...item, url }
  })

  // 更新封面地址
  planForm.coverImage = imageUrl
  ElMessage.success('封面上传成功')
}

// 上传失败
const handleCoverError = (error: any) => {
  console.error('封面上传失败:', error)
  let errorMessage = '封面上传失败，请重试'
  if (error?.message) {
    errorMessage = error.message
  } else if (error?.response?.data?.message) {
    errorMessage = error.response.data.message
  }
  ElMessage.error(errorMessage)
}

// 预览图片（缩略图点击）
const handlePicturePreview = (file: any) => {
  const url = file.url || extractImageUrl(file.response)
  if (!url) return
  previewImageUrl.value = url
  previewVisible.value = true
}

// 删除图片：清理缩略图和封面
const handleCoverRemove = (file: any, fileList: any[]) => {
  const url = file.url || extractImageUrl(file.response)
  if (url && planForm.coverImage === url) {
    planForm.coverImage = ''
  }
  coverFileList.value = fileList
}

// 保存草稿
const saveDraft = async () => {
  try {
    loading.value = true
    
    // 准备提交数据（草稿状态不更新 auditStatus，保持原有的 NULL）
    // 将 coverImage 转换为 images 字段（数据库使用 images 字段存储）
    let imagesValue = ''
    if (planForm.coverImage) {
      imagesValue = planForm.coverImage
    }
    
    const submitData = {
      title: planForm.title,
      destination: planForm.destination,
      days: planForm.days,
      budget: planForm.budget,
      description: planForm.description,
      personCount: planForm.people,
      travelType: planForm.type,
      bestSeason: planForm.bestSeason,
      suitableFor: planForm.suitableFor,
      difficultyLevel: planForm.difficultyLevel,
      itinerary: JSON.stringify(planForm.schedule),
      costDetail: JSON.stringify(planForm.costs),
      notice: planForm.notes,
      tags: planForm.tags.join(','),
      images: imagesValue,  // 使用 images 字段而不是 coverImage
      status: 0
      // 不发送 auditStatus，保持原有状态（草稿=NULL）
    }
    
    const result = await request.put(`/travel-plan/${planId.value}`, submitData)
    
    if (result.code === 200) {
      ElMessage.success('草稿保存成功')
      setTimeout(() => {
        router.push('/home/user/plans')
      }, 1500)
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error: any) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 发布攻略
const publishPlan = async () => {
  try {
    // 验证表单
    await planFormRef.value.validate()
    
    // 确认发布
    await ElMessageBox.confirm(
      '确定要提交审核吗？审核通过后将自动发布。',
      '提交审核',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    loading.value = true
    
    // 准备提交数据
    // 将 coverImage 转换为 images 字段（数据库使用 images 字段存储）
    let imagesValue = ''
    if (planForm.coverImage) {
      imagesValue = planForm.coverImage
    }
    
    const submitData = {
      title: planForm.title,
      destination: planForm.destination,
      days: planForm.days,
      budget: planForm.budget,
      description: planForm.description,
      personCount: planForm.people,
      travelType: planForm.type,
      bestSeason: planForm.bestSeason,
      suitableFor: planForm.suitableFor,
      difficultyLevel: planForm.difficultyLevel,
      itinerary: JSON.stringify(planForm.schedule),
      costDetail: JSON.stringify(planForm.costs),
      notice: planForm.notes,
      tags: planForm.tags.join(','),
      images: imagesValue,  // 使用 images 字段而不是 coverImage
      auditStatus: 0,  // 待审核状态
      status: 0
    }
    
    const result = await request.put(`/travel-plan/${planId.value}`, submitData)
    
    if (result.code === 200) {
      ElMessage.success('提交成功，等待审核')
      setTimeout(() => {
        router.push('/home/user/plans')
      }, 1500)
    } else {
      ElMessage.error(result.message || '提交失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('提交审核失败:', error)
      ElMessage.error('提交失败: ' + (error.message || '请检查表单填写'))
    }
  } finally {
    loading.value = false
  }
}

// 天数已改为自动计算，移除手动调整逻辑

onMounted(() => {
  loadPlanDetail()
})
</script>

<style lang="scss" scoped>
// 让编辑页的整体视觉和“创建攻略”保持完全一致
.edit-plan {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px 24px;
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    h2 {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .header-actions {
      display: flex;
      gap: 12px;

      .square-btn {
        border-radius: 4px;
        padding: 8px 20px;
        font-weight: 500;
        transition: none;
        background-color: #ffffff;
        border: 1px solid #dcdfe6;
        color: #606266;

        &:hover {
          transform: none !important;
          box-shadow: none !important;
          background-color: #f5f7fa;
          border-color: #c0c4cc;
          color: #606266;
        }
      }
    }
  }

  .icon-btn {
    transition: none;
    color: #909399;
    border: 1px dashed #dcdfe6;

    &:hover {
      transform: none !important;
      box-shadow: none !important;
      color: #409eff;
      border-color: #409eff;
      background: #ecf5ff;
    }

    &.delete-btn {
      &:hover {
        color: #f56c6c;
        border-color: #f56c6c;
        background: #fef0f0;
      }
    }
  }

  .form-card {
    position: relative;
    z-index: 10;
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

      :deep(.el-input__inner),
      :deep(.el-textarea__inner) {
        font-size: 16px !important;
        padding-top: 12px;
        padding-bottom: 12px;
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
        }
      }

      .el-tag {
        border-radius: 16px;
        padding: 6px 14px;
        font-weight: 600;
      }

      .el-button {
        border-radius: 12px;
        font-weight: 600;
        transition: all 0.3s;
      }
    }

    .tag-container {
      .tag-selector {
        margin-top: 12px;
        padding-top: 12px;
        border-top: 1px dashed #e4e7ed;
        
        .tag-available {
          transition: all 0.2s;
          
          &:hover {
            background-color: #ecf5ff;
            border-color: #b3d8ff;
            color: #409eff;
          }
        }
      }
    }

    // 行程安排：与“创建攻略”页面统一的白色扁平样式，去掉紫色渐变
    .schedule-section {
      .day-schedule {
        border: 1px solid #e4e7ed;
        background: #ffffff;
        border-radius: 12px;
        padding: 20px;
        margin-bottom: 16px;

        .day-header {
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

        .day-content {
          .schedule-item {
            margin-bottom: 12px;
            padding: 12px;
            background: #fafafa;
            border-radius: 8px;
            box-shadow: none;
          }
        }
      }
    }

    // 费用明细：和创建页一致
    .cost-section {
      .cost-item {
        margin-bottom: 12px;
        padding: 12px;
        background: #fafafa;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
      }
    }

    // 封面上传：完全复用创建页的白色上传卡片风格
    .cover-uploader {
      :deep(.el-upload--picture-card) {
        border-radius: 12px;
        background-color: #fafafa;
        border: 1px dashed #dcdfe6;
        transition: none;
      }

      :deep(.el-upload--picture-card:hover) {
        border-color: #c0c4cc;
        background-color: #f5f7fa;
        transform: none;
        box-shadow: none;
      }

      :deep(.el-upload-list__item.is-success) {
        border-radius: 12px;
      }

      // 隐藏 Element Plus 默认的“按 Delete 键可删除”文字提示，只保留删除按钮
      :deep(.el-upload-list__item-status-label) {
        display: none;
      }
    }

    .upload-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #909399;
    }
    
    .field-tip {
      margin-top: 4px;
      font-size: 12px;
      color: #909399;
      font-style: italic;
    }

    // 其余表单内提示样式 END
  }

  // 图片预览弹窗样式（与创建页保持一致）
  .image-preview-dialog {
    :deep(.el-dialog__body) {
      padding: 0;
    }
  }

  .image-preview-wrapper {
    text-align: center;
    background: #000;
  }

  .image-preview-img {
    max-width: 100%;
    max-height: 80vh;
    object-fit: contain;
    display: block;
    margin: 0 auto;
  }
}

@keyframes backgroundShift {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(-5%, -5%) rotate(5deg);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>


















