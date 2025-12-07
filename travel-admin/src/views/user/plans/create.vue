<template>
  <div class="create-plan">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>创建攻略</h2>
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
            :rows="4"
            placeholder="请输入旅行中需要注意的事项，如天气、交通、安全等..."
            maxlength="300"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 标签 -->
        <el-divider content-position="left">
          <el-icon><PriceTag /></el-icon>
          标签
        </el-divider>
        
        <el-form-item label="攻略标签">
          <div class="tag-container">
            <!-- 已选标签 -->
            <span
              v-for="tag in planForm.tags"
              :key="tag"
              class="tag-item tag-selected"
              @click="removeTag(tag)"
            >
              #{{ tag }}
            </span>
            <!-- 可选标签列表 -->
            <div class="tag-selector">
              <template v-for="tag in availableTags" :key="tag">
                <span
                  v-if="!planForm.tags.includes(tag)"
                  @click="addTag(tag)"
                  class="tag-item tag-available"
                >
                  #{{ tag }}
                </span>
              </template>
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

    <!-- 图片大图预览 -->
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Delete, Close, InfoFilled, Calendar, 
  Wallet, Warning, PriceTag, Picture, Sunny, Lightning
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { getCurrentUserInfo, getCurrentUserId } from '@/utils/user'
import { planTagOptions } from '@/constants/tags'

const router = useRouter()
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
  bestSeason: '全年',
  suitableFor: '所有人',
  difficultyLevel: 1,
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
  tags: [],
  // 第一张作为封面，其余为补充图片
  coverImage: '',
  images: [] as string[]
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

// 标签相关 - 标准标签列表（与热门话题共用）
const availableTags = ref([...planTagOptions])

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

// 从上传响应中提取图片地址
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

// 上传成功
const handleCoverSuccess = (response: any, file: any, fileList: any[]) => {
  console.log('上传响应:', response)
  const imageUrl = extractImageUrl(response)

  if (!imageUrl) {
    ElMessage.error('上传成功但无法获取图片地址')
    return
  }

  // 维护本地文件列表（缩略图预览）
  coverFileList.value = fileList.map((item) => {
    const url = item.url || extractImageUrl(item.response) || imageUrl
    return {
      ...item,
      url
    }
  })

  // 维护表单中的图片数组和封面
  if (!planForm.images.includes(imageUrl)) {
    planForm.images.push(imageUrl)
  }
  if (!planForm.coverImage) {
    planForm.coverImage = imageUrl
  }

  ElMessage.success('图片上传成功')
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

// 预览图片
const handlePicturePreview = (file: any) => {
  const url = file.url || extractImageUrl(file.response)
  if (!url) return
  previewImageUrl.value = url
  previewVisible.value = true
}

// 删除图片
const handleCoverRemove = (file: any, fileList: any[]) => {
  const url = file.url || extractImageUrl(file.response)
  if (url) {
    const index = planForm.images.indexOf(url)
    if (index !== -1) {
      planForm.images.splice(index, 1)
    }
    // 如果删除的是封面，则重置为剩余第一张
    if (planForm.coverImage === url) {
      planForm.coverImage = planForm.images[0] || ''
    }
  }
  coverFileList.value = fileList
}

// 保存草稿
const saveDraft = async () => {
  try {
    loading.value = true
    
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    if (!userInfo || !userInfo.id) {
      ElMessage.error('请先登录')
      return
    }
    
    // 准备提交数据（草稿状态不发送 auditStatus，保持数据库 NULL）
    // 将 coverImage 转换为 images 字段（数据库使用 images 字段存储）
    let imagesValue = ''
    if (planForm.images && planForm.images.length > 0) {
      imagesValue = planForm.images.join(',')
    } else if (planForm.coverImage) {
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
      authorId: userInfo.id,
      authorName: userInfo.username,
      status: 0,
      viewCount: 0,
      likeCount: 0,
      commentCount: 0
      // 不发送 auditStatus，让数据库保持 NULL（草稿状态）
    }
    
    const result = await request.post('/travel-plan', submitData)
    
    if (result.code === 200) {
      ElMessage.success('草稿保存成功')
      // 延迟跳转到我的攻略页面
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

// 检查发布权限和限制
const checkPublishPermission = async (userId: number) => {
  try {
    // 检查是否有发布权限
    const permissionRes = await request.get('/user/points/check-permission', {
      params: {
        userId,
        permissionType: 'POST_PLAN'
      }
    })
    
    if (permissionRes.code !== 200 || !permissionRes.data?.hasPermission) {
      ElMessage.warning('您当前等级不支持发布攻略，请先完成任务升级到白银探索者')
      return false
    }
    
    // 检查今日发布次数
    const pointsRes = await request.get('/user/points/my', {
      params: { userId }
    })
    
    if (pointsRes.code === 200 && pointsRes.data) {
      const todayPosts = pointsRes.data.todayPosts || 0
      const dailyPostLimit = pointsRes.data.dailyPostLimit || 0
      
      if (dailyPostLimit > 0 && todayPosts >= dailyPostLimit) {
        ElMessage.warning(`今日发布已达上限（${dailyPostLimit}篇），请明天再试`)
        return false
      }
    }
    
    return true
  } catch (error: any) {
    console.error('检查发布权限失败:', error)
    // 如果检查失败，允许继续发布（降级处理）
    return true
  }
}

// 发布攻略
const publishPlan = async () => {
  try {
    // 验证表单
    if (!planFormRef.value) {
      ElMessage.error('表单未初始化')
      return
    }
    
    await planFormRef.value.validate()
    
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    if (!userInfo || !userInfo.id) {
      ElMessage.error('请先登录')
      return
    }
    
    // 检查发布权限和限制
    const canPublish = await checkPublishPermission(userInfo.id)
    if (!canPublish) {
      return
    }
    
    // 确认发布
    await ElMessageBox.confirm(
      '确定要发布这篇攻略吗？发布后将提交审核。',
      '确认发布',
      {
        confirmButtonText: '确定发布',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    loading.value = true
    
    // 准备提交数据
    // 将图片数组转换为 images 字段（数据库使用 images 字段存储）
    let imagesValue = ''
    if (planForm.images && planForm.images.length > 0) {
      imagesValue = planForm.images.join(',')
    } else if (planForm.coverImage) {
      imagesValue = planForm.coverImage
    }
    
    const submitData: any = {
      ...planForm,
      authorId: userInfo.id,
      authorName: userInfo.username,
      personCount: planForm.people,
      travelType: planForm.type,
      itinerary: JSON.stringify(planForm.schedule),
      costDetail: JSON.stringify(planForm.costs),
      notice: planForm.notes,
      tags: planForm.tags.join(','),
      images: imagesValue,  // 使用 images 字段而不是 coverImage
      auditStatus: 0,  // 待审核状态
      status: 0,
      viewCount: 0,
      likeCount: 0,
      commentCount: 0
    }
    // 移除 coverImage 字段，避免混淆
    delete submitData.coverImage
    
    const result = await request.post('/travel-plan', submitData)
    
    if (result.code === 200) {
      ElMessage.success('发布成功，等待审核')
      // 延迟跳转到我的攻略页面
      setTimeout(() => {
        router.push('/home/user/plans')
      }, 1500)
    } else {
      ElMessage.error(result.message || '发布失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('发布攻略失败:', error)
      ElMessage.error('发布失败: ' + (error.message || '请检查表单填写'))
    }
  } finally {
    loading.value = false
  }
}

// 天数已改为自动计算，移除手动调整逻辑

onMounted(() => {
  // 初始化
})
</script>

<style lang="scss" scoped>
.create-plan {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px 24px;
    background: white;
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
        height: 44px; // 与输入框高度保持一致，垂直居中
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

          .el-icon {
            color: #303133;
          }
        }
      }
      
      .el-tag {
        border-radius: 16px;
        padding: 6px 14px;
        font-weight: 600;
      }
      
      .tag-container {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        align-items: center;
        
        .tag-item {
          display: inline-block;
          padding: 4px 12px;
          font-size: 14px;
          color: #909399;
          background: #f5f7fa;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          transition: all 0.2s;
          user-select: none;
          
          &.tag-selected {
            color: #606266;
            background: #e4e7ed;
            
            &:hover {
              background: #dcdfe6;
              color: #303133;
            }
          }
          
          &.tag-available {
            &:hover {
              background: #ebeef5;
              color: #606266;
            }
          }
        }
        
        .tag-selector {
          margin-top: 0;
          padding-top: 0;
          border-top: none;
        }
      }
      
      .el-button {
        border-radius: 8px;
        font-weight: 500;
        transition: none;

        &--primary,
        &--success,
        &--danger {
          background: #ffffff;
          border: 1px solid #dcdfe6;
          color: #606266;

          &:hover {
            transform: none;
            box-shadow: none;
            background: #f5f7fa;
            border-color: #c0c4cc;
            color: #606266;
          }
        }
      }
    }
    
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

    .cost-section {
      .cost-item {
        margin-bottom: 12px;
        padding: 12px;
        background: #fafafa;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
      }
    }
    
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
  }

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

// 动画定义
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






















