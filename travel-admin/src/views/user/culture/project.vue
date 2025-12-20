<template>
  <div class="project-detail" v-loading="loading">
    <!-- 返回按钮 -->
    <BackButton />

    <el-row :gutter="24">
      <!-- 左侧：项目详情 -->
      <el-col :span="16">
        <!-- 项目头部 -->
        <el-card class="header-card">
          <div class="project-header">
            <div class="header-left">
              <h1 class="project-title">{{ project.title }}</h1>
              <div class="project-status">
                <el-tag :type="project.statusType" size="large">{{ project.statusText }}</el-tag>
              </div>
            </div>
            <div class="header-right"></div>
          </div>

          <div class="project-meta">
            <div class="meta-item">
              <span class="label">项目地区</span>
              <span class="value">
                <el-icon><Location /></el-icon>
                {{ project.location }}
              </span>
            </div>
            <div class="meta-item">
              <span class="label">开始时间</span>
              <span class="value">
                <el-icon><Calendar /></el-icon>
                {{ project.startDate }}
              </span>
            </div>
            <div class="meta-item">
              <span class="label">投资规模</span>
              <span class="value">
                <el-icon><Coin /></el-icon>
                {{ project.investment }}万元
              </span>
            </div>
            <div class="meta-item">
              <span class="label">惠及农户</span>
              <span class="value">
                <el-icon><User /></el-icon>
                {{ project.beneficiaries }}户
              </span>
            </div>
          </div>
        </el-card>

        <!-- 项目图片 -->
        <el-card v-if="projectImages && projectImages.length > 0" class="images-card">
          <h3>项目图片</h3>
          <div class="project-images">
            <el-image
              v-for="(img, index) in projectImages"
              :key="index"
              :src="img"
              :preview-src-list="projectImages"
              :initial-index="index"
              fit="cover"
              class="project-image"
              :preview-teleported="true"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
        </el-card>

        <!-- 项目介绍 -->
        <el-card class="info-card">
          <h3>项目简介</h3>
          <p>{{ project.description }}</p>

          <el-divider />

          <h3>项目标签</h3>
          <div class="project-tags">
            <el-tag v-for="tag in project.tags" :key="tag" size="large" type="info">
              #{{ tag }}
            </el-tag>
          </div>

          <el-divider />

          <h3>项目目标</h3>
          <div class="project-goals">
            <div v-for="goal in project.goals" :key="goal.title" class="goal-item">
              <div class="goal-icon">
                <el-icon><component :is="goal.icon" /></el-icon>
              </div>
              <div class="goal-content">
                <h4>{{ goal.title }}</h4>
                <p>{{ goal.description }}</p>
              </div>
            </div>
          </div>

          <el-divider />

          <h3>合作方式</h3>
          <ul class="cooperation-list">
            <li v-for="item in project.cooperation" :key="item">
              <el-icon color="#67c23a"><SuccessFilled /></el-icon>
              <span>{{ item }}</span>
            </li>
          </ul>

          <el-divider />

          <h3>政策支持</h3>
          <div class="policy-support">
            <div v-for="policy in project.policies" :key="policy.title" class="policy-item">
              <h4>{{ policy.title }}</h4>
              <p>{{ policy.content }}</p>
            </div>
          </div>

          <el-divider />

          <h3>联系方式</h3>
          <div class="contact-info-detail">
            <div class="contact-item">
              <el-icon><User /></el-icon>
              <span>负责人：{{ project.contact.name }}</span>
            </div>
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <span>联系电话：{{ project.contact.phone }}</span>
            </div>
            <div class="contact-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：{{ project.contact.email }}</span>
            </div>
            <div class="contact-item">
              <el-icon><Location /></el-icon>
              <span>地址：{{ project.contact.address }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：申请表单 -->
      <el-col :span="8">
        <div class="apply-card-sticky">
          <el-card class="apply-card">
            <h3 class="apply-title">
              <el-icon><Document /></el-icon>
              项目申请
            </h3>

            <el-alert
              v-if="project.statusText === '招募中'"
              title="该项目正在招募合作伙伴，欢迎申请！"
              type="success"
              :closable="false"
              show-icon
              style="margin-bottom: 20px"
            />

            <el-alert
              v-else-if="project.statusText === '进行中'"
              title="该项目正在进行中，如需加入请联系负责人。"
              type="info"
              :closable="false"
              show-icon
              style="margin-bottom: 20px"
            />

            <el-form :model="applyForm" label-position="top">
              <el-form-item label="企业/组织名称">
                <el-input v-model="applyForm.organizationName" placeholder="请输入企业/组织名称" />
              </el-form-item>

              <el-form-item label="申请人姓名">
                <el-input v-model="applyForm.applicantName" placeholder="请输入申请人姓名" />
              </el-form-item>

              <el-form-item label="职务">
                <el-input v-model="applyForm.position" placeholder="请输入职务" />
              </el-form-item>

              <el-form-item label="联系电话">
                <el-input v-model="applyForm.phone" placeholder="请输入联系电话" />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="applyForm.email" placeholder="请输入邮箱" />
              </el-form-item>

              <el-form-item label="合作意向">
                <el-select v-model="applyForm.cooperationType" placeholder="请选择合作意向" style="width: 100%">
                  <el-option label="资金投资" value="investment" />
                  <el-option label="技术支持" value="technology" />
                  <el-option label="运营管理" value="operation" />
                  <el-option label="资源对接" value="resource" />
                  <el-option label="其他合作" value="other" />
                </el-select>
              </el-form-item>

              <el-form-item label="预计投资金额（万元）">
                <el-input-number
                  v-model="applyForm.investmentAmount"
                  :min="0"
                  :step="10"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="申请说明">
                <el-input
                  v-model="applyForm.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请简要说明您的合作意向、优势资源等"
                />
              </el-form-item>
            </el-form>

            <el-button type="primary" size="large" class="apply-button" @click="handleApply">
              提交申请
            </el-button>

            <div class="tips">
              <el-icon><InfoFilled /></el-icon>
              <span>提交后我们会在3个工作日内与您联系</span>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import {
  ArrowLeft,
  Location,
  Calendar,
  Coin,
  User,
  SuccessFilled,
  Phone,
  Message,
  Document,
  Checked,
  InfoFilled,
  TrendCharts,
  Briefcase,
  Sunny,
  Brush,
  Picture
} from '@element-plus/icons-vue'
import { getProjectById } from '@/api/cultureProject'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

// 加载状态
const loading = ref(false)

// 项目详情数据
const project = ref({
  id: null,
  title: '',
  statusText: '',
  statusType: 'info',
  location: '',
  startDate: '',
  investment: 0,
  beneficiaries: 0,
  description: '',
  tags: [],
  goals: [],
  cooperation: [],
  policies: [],
  images: [],
  contact: {
    name: '',
    phone: '',
    email: '',
    address: ''
  }
})

// 项目图片列表（用于预览）
const projectImages = ref([])

// 状态映射
const getStatusInfo = (status) => {
  // status: 0-下架, 1-上架/进行中, 2-已完成, 3-招募中
  const statusMap = {
    0: { text: '已下架', type: 'info' },
    1: { text: '进行中', type: 'success' },
    2: { text: '已完成', type: 'success' },
    3: { text: '招募中', type: 'warning' }
  }
  return statusMap[status] || { text: '未知', type: 'info' }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    return `${year}-${month}`
  } catch {
    return dateStr
  }
}

// 加载项目详情
const loadProjectDetail = async (id) => {
  if (!id) {
    ElMessage.error('项目ID不能为空')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await getProjectById(Number(id))
    const data = res.data

    if (!data) {
      ElMessage.error('项目不存在')
      router.back()
      return
    }

    // 状态信息
    const statusInfo = getStatusInfo(data.status || 1)

    // 解析tags
    let tags = []
    try {
      if (data.tags) {
        if (typeof data.tags === 'string') {
          tags = JSON.parse(data.tags)
        } else if (Array.isArray(data.tags)) {
          tags = data.tags
        }
      }
    } catch (e) {
      console.warn('解析tags失败:', e)
    }
    // 如果没有tags，使用默认值
    if (!tags || tags.length === 0) {
      tags = ['乡村振兴', '产业融合', '就业扶持', '文化传承', '生态保护']
    }

    // 解析goals
    let goals = []
    try {
      if (data.goals) {
        if (typeof data.goals === 'string') {
          goals = JSON.parse(data.goals)
        } else if (Array.isArray(data.goals)) {
          goals = data.goals
        }
      }
    } catch (e) {
      console.warn('解析goals失败:', e)
    }
    // 如果没有goals，使用默认值
    if (!goals || goals.length === 0) {
      goals = [
        {
          icon: TrendCharts,
          title: '产业发展',
          description: '建立完善的农旅融合产业链，推动当地经济发展'
        },
        {
          icon: Briefcase,
          title: '就业创收',
          description: '创造就业岗位，带动农户增收致富'
        },
        {
          icon: Sunny,
          title: '生态保护',
          description: '保护和改善当地生态环境，打造美丽乡村'
        },
        {
          icon: Brush,
          title: '文化传承',
          description: '挖掘和传承当地特色文化，提升文化软实力'
        }
      ]
    } else {
      // 为goals添加图标映射
      const iconMap = {
        '产业发展': TrendCharts,
        '就业创收': Briefcase,
        '生态保护': Sunny,
        '文化传承': Brush
      }
      goals = goals.map(goal => ({
        ...goal,
        icon: iconMap[goal.title] || TrendCharts
      }))
    }

    // 解析cooperation
    let cooperation = []
    try {
      if (data.cooperation) {
        if (typeof data.cooperation === 'string') {
          cooperation = JSON.parse(data.cooperation)
        } else if (Array.isArray(data.cooperation)) {
          cooperation = data.cooperation
        }
      }
    } catch (e) {
      console.warn('解析cooperation失败:', e)
    }
    // 如果没有cooperation，使用默认值
    if (!cooperation || cooperation.length === 0) {
      cooperation = [
        '政府提供政策支持和土地资源',
        '企业负责项目投资和运营管理',
        '农户提供劳动力和农产品',
        '共同分享项目收益',
        '建立长期合作机制'
      ]
    }

    // 解析policies
    let policies = []
    try {
      if (data.policies) {
        if (typeof data.policies === 'string') {
          policies = JSON.parse(data.policies)
        } else if (Array.isArray(data.policies)) {
          policies = data.policies
        }
      }
    } catch (e) {
      console.warn('解析policies失败:', e)
    }
    // 如果没有policies，使用默认值
    if (!policies || policies.length === 0) {
      policies = [
        {
          title: '资金支持',
          content: '政府提供专项扶持资金，对符合条件的企业给予投资补贴。'
        },
        {
          title: '土地政策',
          content: '优先保障项目用地，简化审批流程。'
        },
        {
          title: '税收优惠',
          content: '对符合条件的项目给予税收优惠政策支持。'
        },
        {
          title: '人才引进',
          content: '对引进的高层次人才提供住房补贴、子女入学等配套服务。'
        }
      ]
    }

    // 处理开始时间：优先使用startDate，如果没有则从createTime转换
    let startDate = ''
    if (data.startDate) {
      startDate = data.startDate
    } else if (data.createTime) {
      startDate = formatDate(data.createTime)
    } else {
      startDate = '未知'
    }

    // 解析图片列表
    let images = []
    try {
      if (data.images) {
        if (typeof data.images === 'string') {
          images = JSON.parse(data.images)
        } else if (Array.isArray(data.images)) {
          images = data.images
        }
      }
      // 如果没有images字段，尝试使用image字段（单张图片）
      if (images.length === 0 && data.image) {
        images = [data.image]
      }
    } catch (e) {
      console.warn('解析images失败:', e)
      // 如果解析失败，尝试使用image字段
      if (data.image) {
        images = [data.image]
      }
    }
    projectImages.value = images

    // 映射数据
    project.value = {
      id: data.id,
      title: data.name || '项目名称',
      statusText: statusInfo.text,
      statusType: statusInfo.type,
      location: data.location || data.region || '未知地区',
      startDate: startDate,
      investment: data.price ? Number(data.price) : 0,
      beneficiaries: data.beneficiaries ? Number(data.beneficiaries) : 0,
      description: data.description || '暂无项目介绍',
      tags: tags,
      goals: goals,
      cooperation: cooperation,
      policies: policies,
      images: images,
      contact: {
        name: data.contactPerson || '项目负责人',
        phone: data.contactPhone || '暂无',
        email: data.email || '',
        address: data.address || '暂无'
      }
    }
  } catch (error) {
    console.error('加载项目详情失败:', error)
    ElMessage.error(error.message || '加载项目详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 申请表单
const applyForm = ref({
  organizationName: '',
  applicantName: '',
  position: '',
  phone: '',
  email: '',
  cooperationType: '',
  investmentAmount: 0,
  description: ''
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 处理申请
const handleApply = async () => {
  if (!applyForm.value.organizationName) {
    ElMessage.warning('请输入企业/组织名称')
    return
  }
  if (!applyForm.value.applicantName) {
    ElMessage.warning('请输入申请人姓名')
    return
  }
  if (!applyForm.value.phone) {
    ElMessage.warning('请输入联系电话')
    return
  }
  if (!applyForm.value.cooperationType) {
    ElMessage.warning('请选择合作意向')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认提交申请？\n\n项目：${project.value.title}\n企业：${applyForm.value.organizationName}\n申请人：${applyForm.value.applicantName}\n\n我们会在3个工作日内与您联系。`,
      '确认申请',
      {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 调用后端API提交申请
    const submitData = {
      projectId: project.value.id,
      projectTitle: project.value.title,
      organizationName: applyForm.value.organizationName,
      applicantName: applyForm.value.applicantName,
      position: applyForm.value.position || '',
      phone: applyForm.value.phone,
      email: applyForm.value.email || '',
      cooperationType: applyForm.value.cooperationType,
      investmentAmount: applyForm.value.investmentAmount || 0,
      description: applyForm.value.description || ''
    }

    const res = await request.post('/user/culture/application/submit', submitData)
    
    if (res.code === 200) {
      ElMessage.success('申请提交成功！我们会尽快与您联系')
      
      // 清空表单
      applyForm.value = {
        organizationName: '',
        applicantName: '',
        position: '',
        phone: '',
        email: '',
        cooperationType: '',
        investmentAmount: 0,
        description: ''
      }
      
      // 2秒后跳转到申请列表
      setTimeout(() => {
        router.push('/home/user/culture/applications')
      }, 2000)
    } else {
      ElMessage.error(res.message || '申请提交失败')
    }
  } catch (error) {
    if (error.message && !error.message.includes('取消')) {
      console.error('提交申请失败:', error)
      ElMessage.error('申请提交失败: ' + (error.message || '未知错误'))
    }
  }
}

// 页面加载
onMounted(() => {
  const id = route.params.id
  console.log('加载政府项目详情，ID:', id)
  loadProjectDetail(id)
})
</script>

<style scoped lang="scss">
.project-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.header-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }
}

.images-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 20px 0;
  }
}

.project-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.project-image {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }

  :deep(.el-image__inner) {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  flex: 1;
}

.project-title {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px 0;
}

.project-status {
  display: inline-block;
}

.header-right {
}

.project-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;

  .label {
    font-size: 13px;
    color: #909399;
  }

  .value {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.info-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 32px;
  }

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    margin: 24px 0 16px 0;

    &:first-child {
      margin-top: 0;
    }
  }

  p {
    font-size: 15px;
    line-height: 1.8;
    color: #606266;
    margin: 0;
  }
}

.project-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.project-goals {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.goal-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;

  .goal-icon {
    font-size: 40px;
  }

  .goal-content {
    flex: 1;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 14px;
      line-height: 1.6;
      color: #606266;
      margin: 0;
    }
  }
}

.cooperation-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 0;
    font-size: 15px;
    color: #606266;
    line-height: 1.6;
  }
}

.policy-support {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.policy-item {
  padding: 20px;
  background: #fff1f0;
  border-left: none;
  border-radius: 8px;

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #f56c6c;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    line-height: 1.6;
    color: #606266;
    margin: 0;
  }
}

.contact-info-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.apply-card-sticky {
  position: sticky;
  top: 24px;
}

.apply-card {
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.apply-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
}

.apply-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 16px;
}

.tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  background: #f4f4f5;
  border-radius: 8px;
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .project-detail {
    padding: 12px;
  }

  .project-title {
    font-size: 24px;
  }

  .project-meta,
  .project-goals {
    grid-template-columns: 1fr;
  }

  .project-images {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .project-image {
    height: 150px;
  }

  .apply-card-sticky {
    position: static;
  }
}
</style>


