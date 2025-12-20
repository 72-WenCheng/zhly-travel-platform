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
            <el-form-item label="项目名称" prop="name" required>
              <el-input v-model="formData.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="type" required>
              <el-select v-model="formData.type" placeholder="请选择项目类型" style="width: 100%">
                <el-option label="农家乐" :value="0" />
                <el-option label="特色产品" :value="1" />
                <el-option label="文化体验" :value="2" />
                <el-option label="政府项目" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目地区" prop="region" required>
              <el-input v-model="formData.region" placeholder="请输入项目地区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投资规模（万元）" prop="price" required>
              <el-input-number
                v-model="formData.price"
                :min="0"
                :precision="2"
                placeholder="投资规模"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="month"
                placeholder="选择开始时间（年月）"
                format="YYYY-MM"
                value-format="YYYY-MM"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="惠及农户（户）" prop="beneficiaries">
              <el-input-number
                v-model="formData.beneficiaries"
                :min="0"
                :precision="0"
                placeholder="惠及农户数量"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="项目描述" prop="description" required>
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>

        <!-- 项目标签 -->
        <el-divider content-position="left">
          <el-icon><Collection /></el-icon>
          项目标签
        </el-divider>
        
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="formData.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入项目标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in commonTags"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
          <div class="form-tip">可输入多个标签，如：乡村振兴、产业融合、就业扶持等</div>
        </el-form-item>

        <!-- 项目目标 -->
        <el-divider content-position="left">
          <el-icon><Aim /></el-icon>
          项目目标
        </el-divider>
        
        <el-form-item label="目标列表">
          <div class="goals-list">
            <div
              v-for="(goal, index) in formData.goals"
              :key="index"
              class="goal-item"
            >
              <el-card shadow="never" style="margin-bottom: 16px;">
                <div class="goal-header">
                  <h4>目标 {{ index + 1 }}</h4>
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removeGoal(index)"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
                <el-form-item :label="`目标${index + 1}标题`" :prop="`goals.${index}.title`">
                  <el-input v-model="goal.title" placeholder="如：产业发展、就业创收等" />
                </el-form-item>
                <el-form-item :label="`目标${index + 1}描述`" :prop="`goals.${index}.description`">
                  <el-input
                    v-model="goal.description"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入目标描述"
                  />
                </el-form-item>
              </el-card>
            </div>
            <el-button
              type="primary"
              size="small"
              text
              @click="addGoal"
            >
              <el-icon><Plus /></el-icon>
              添加目标
            </el-button>
          </div>
        </el-form-item>

        <!-- 合作方式 -->
        <el-divider content-position="left">
          <el-icon><Connection /></el-icon>
          合作方式
        </el-divider>
        
        <el-form-item label="合作方式列表">
          <div class="cooperation-list">
            <div
              v-for="(item, index) in formData.cooperation"
              :key="index"
              class="cooperation-item"
            >
              <el-input
                v-model="formData.cooperation[index]"
                placeholder="请输入合作方式"
                style="width: calc(100% - 80px); margin-right: 10px;"
              />
              <el-button
                type="danger"
                size="small"
                text
                @click="removeCooperation(index)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
            <el-button
              type="primary"
              size="small"
              text
              @click="addCooperation"
            >
              <el-icon><Plus /></el-icon>
              添加合作方式
            </el-button>
          </div>
        </el-form-item>

        <!-- 政策支持 -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          政策支持
        </el-divider>
        
        <el-form-item label="政策列表">
          <div class="policies-list">
            <div
              v-for="(policy, index) in formData.policies"
              :key="index"
              class="policy-item"
            >
              <el-card shadow="never" style="margin-bottom: 16px;">
                <div class="policy-header">
                  <h4>政策 {{ index + 1 }}</h4>
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removePolicy(index)"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
                <el-form-item :label="`政策${index + 1}标题`" :prop="`policies.${index}.title`">
                  <el-input v-model="policy.title" placeholder="如：资金支持、土地政策等" />
                </el-form-item>
                <el-form-item :label="`政策${index + 1}内容`" :prop="`policies.${index}.content`">
                  <el-input
                    v-model="policy.content"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入政策内容"
                  />
                </el-form-item>
              </el-card>
            </div>
            <el-button
              type="primary"
              size="small"
              text
              @click="addPolicy"
            >
              <el-icon><Plus /></el-icon>
              添加政策
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
  Plus, InfoFilled, Picture, Setting, Delete, Collection, Aim, Connection, Document
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import { createAdminProject, updateAdminProject, getAdminProjectById } from '@/api/cultureProject'
import type { CultureProject } from '@/api/cultureProject'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = ref(false)
const serviceId = ref<number | null>(null)
const imageList = ref<UploadFiles>([])
const dialogVisible = ref(false)
const dialogImageUrl = ref('')


const formData = reactive<any>({
  name: '',
  type: 3,
  region: '',
  price: 0,
  contactPerson: '',
  contactPhone: '',
  email: '',
  address: '',
  description: '',
  image: '',
  status: 1,
  startDate: '',
  beneficiaries: 0,
  tags: [],
  goals: [],
  cooperation: [],
  policies: []
})

// 常用标签
const commonTags = [
  '乡村振兴', '产业融合', '就业扶持', '文化传承', '生态保护',
  '农旅融合', '产业升级', '农民增收', '美丽乡村', '可持续发展'
]

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  region: [{ required: true, message: '请输入项目地区', trigger: 'blur' }],
  price: [{ required: true, message: '请输入投资规模', trigger: 'blur' }],
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
    const result = await getAdminProjectById(id)
    console.log('加载项目数据，API返回:', result)
    
    if (result.code === 200 && result.data) {
      const service: any = result.data
      console.log('项目原始数据:', service)
      
      // 解析JSON字段 - 这些字段可能以字符串形式存储
      let tags: any[] = []
      let goals: any[] = []
      let cooperation: string[] = []
      let policies: any[] = []
      
      try {
        // 尝试从各个可能的字段解析
        if (service.tags) {
          if (typeof service.tags === 'string') {
            try {
              tags = JSON.parse(service.tags)
            } catch {
              // 如果不是JSON，尝试按逗号分割
              tags = service.tags.split(',').filter((t: string) => t.trim())
            }
          } else if (Array.isArray(service.tags)) {
            tags = service.tags
          }
        }
        
        if (service.goals) {
          if (typeof service.goals === 'string') {
            try {
              goals = JSON.parse(service.goals)
            } catch {
              goals = []
            }
          } else if (Array.isArray(service.goals)) {
            goals = service.goals
          }
        }
        
        if (service.cooperation) {
          if (typeof service.cooperation === 'string') {
            try {
              cooperation = JSON.parse(service.cooperation)
            } catch {
              // 如果不是JSON，尝试按换行或逗号分割
              cooperation = service.cooperation.split(/[,\n]/).filter((c: string) => c.trim())
            }
          } else if (Array.isArray(service.cooperation)) {
            cooperation = service.cooperation
          }
        }
        
        if (service.policies) {
          if (typeof service.policies === 'string') {
            try {
              policies = JSON.parse(service.policies)
            } catch {
              policies = []
            }
          } else if (Array.isArray(service.policies)) {
            policies = service.policies
          }
        }
      } catch (e) {
        console.warn('解析JSON字段失败:', e)
      }
      
      // 格式化开始时间 - 从createTime或startDate字段获取
      let startDate = ''
      if (service.startDate) {
        startDate = service.startDate
      } else if (service.createTime) {
        // 从createTime转换为YYYY-MM格式
        try {
          const date = new Date(service.createTime)
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          startDate = `${year}-${month}`
        } catch {
          startDate = ''
        }
      }
      
      // 处理图片 - 可能存储在image字段中
      let images: string[] = []
      if (service.image) {
        if (typeof service.image === 'string') {
          try {
            // 尝试解析为JSON数组
            const parsed = JSON.parse(service.image)
            if (Array.isArray(parsed)) {
              images = parsed
            } else {
              images = [service.image]
            }
          } catch {
            // 如果不是JSON，就是单个图片URL
            images = [service.image]
          }
        } else if (Array.isArray(service.image)) {
          images = service.image
        }
      }
      
      // 如果images字段存在，优先使用
      if (service.images && Array.isArray(service.images)) {
        images = service.images
      }
      
      // 更新表单数据
      Object.assign(formData, {
        name: service.name || '',
        type: service.type !== undefined && service.type !== null ? service.type : 3,
        region: service.region || '',
        price: service.price ? Number(service.price) : 0,
        contactPerson: service.contactPerson || '',
        contactPhone: service.contactPhone || '',
        email: service.email || '',
        address: service.address || '',
        description: service.description || '',
        status: service.status !== undefined && service.status !== null ? service.status : 1,
        startDate: startDate,
        beneficiaries: service.beneficiaries ? Number(service.beneficiaries) : 0,
        tags: Array.isArray(tags) ? tags : [],
        goals: Array.isArray(goals) ? goals : [],
        cooperation: Array.isArray(cooperation) ? cooperation : [],
        policies: Array.isArray(policies) ? policies : []
      })
      
      // 处理图片列表
      if (images.length > 0) {
        imageList.value = images.map((url: string, index: number) => ({
          uid: index + 1,
          name: `image-${index + 1}`,
          url: url,
          status: 'success' as const
        }))
        formData.images = images
      } else {
        imageList.value = []
        formData.images = []
      }
      
      // 确保数组字段不为null
      if (!formData.goals || !Array.isArray(formData.goals)) {
        formData.goals = []
      }
      if (!formData.cooperation || !Array.isArray(formData.cooperation)) {
        formData.cooperation = []
      }
      if (!formData.policies || !Array.isArray(formData.policies)) {
        formData.policies = []
      }
      if (!formData.tags || !Array.isArray(formData.tags)) {
        formData.tags = []
      }
      
      console.log('加载后的表单数据:', formData)
      console.log('加载后的图片列表:', imageList.value)
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

// 添加目标
const addGoal = () => {
  formData.goals = formData.goals || []
  formData.goals.push({
    title: '',
    description: ''
  })
}

// 删除目标
const removeGoal = (index: number) => {
  formData.goals.splice(index, 1)
}

// 添加合作方式
const addCooperation = () => {
  formData.cooperation = formData.cooperation || []
  formData.cooperation.push('')
}

// 删除合作方式
const removeCooperation = (index: number) => {
  formData.cooperation.splice(index, 1)
}

// 添加政策
const addPolicy = () => {
  formData.policies = formData.policies || []
  formData.policies.push({
    title: '',
    content: ''
  })
}

// 删除政策
const removePolicy = (index: number) => {
  formData.policies.splice(index, 1)
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    name: '',
    type: 3,
    region: '',
    price: 0,
    contactPerson: '',
    contactPhone: '',
    email: '',
    address: '',
    description: '',
    image: '',
    status: 1,
    startDate: '',
    beneficiaries: 0,
    tags: [],
    goals: [],
    cooperation: [],
    policies: []
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
        const imageUrls = formData.images && Array.isArray(formData.images) ? formData.images : []
        const submitData: any = { 
          name: formData.name,
          type: formData.type,
          region: formData.region,
          price: formData.price || 0,
          contactPerson: formData.contactPerson || '',
          contactPhone: formData.contactPhone || '',
          email: formData.email || '',
          address: formData.address || '',
          description: formData.description || '',
          image: imageUrls.length > 0 ? imageUrls[0] : '', // 封面图（第一张）
          images: imageUrls.length > 0 ? JSON.stringify(imageUrls) : '', // 图片列表（JSON数组）
          status: formData.status || 1,
          startDate: formData.startDate || '', // 格式：YYYY-MM
          beneficiaries: formData.beneficiaries || 0,
          tags: formData.tags && formData.tags.length > 0 ? JSON.stringify(formData.tags) : '',
          goals: formData.goals && formData.goals.length > 0 ? JSON.stringify(formData.goals) : '',
          cooperation: formData.cooperation && formData.cooperation.length > 0 ? JSON.stringify(formData.cooperation) : '',
          policies: formData.policies && formData.policies.length > 0 ? JSON.stringify(formData.policies) : ''
        }
        
        let result
        if (isEdit.value && serviceId.value) {
          // 编辑模式
          result = await updateAdminProject(serviceId.value, submitData)
        } else {
          // 新增模式
          result = await createAdminProject(submitData)
        }
        
        if (result.code === 200) {
          ElMessage.success(isEdit.value ? '更新项目成功' : '创建项目成功')
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

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.goals-list,
.cooperation-list,
.policies-list {
  .goal-item,
  .cooperation-item,
  .policy-item {
    margin-bottom: 16px;
    
    .goal-header,
    .policy-header {
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

