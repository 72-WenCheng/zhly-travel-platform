<template>
  <div class="project-detail">
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
  Brush
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 项目详情数据
const project = ref({
  id: 1,
  title: '巴南区乡村振兴示范项目',
  statusText: '进行中',
  statusType: 'success',
  location: '重庆市巴南区',
  startDate: '2024-03',
  investment: 5000,
  beneficiaries: 320,
  description: '以农旅融合为核心，打造集观光、体验、度假为一体的乡村旅游综合体，带动当地农民增收致富。通过整合当地特色农业资源、文化资源和旅游资源，建立完善的产业链条，实现经济效益和社会效益双赢。项目将采用"政府引导+企业运营+农户参与"的模式，确保项目可持续发展。',
  tags: ['乡村振兴', '产业融合', '就业扶持', '文化传承', '生态保护'],
  goals: [
    {
      icon: TrendCharts,
      title: '产业发展',
      description: '建立完善的农旅融合产业链，年产值达到8000万元以上'
    },
    {
      icon: Briefcase,
      title: '就业创收',
      description: '创造就业岗位500个以上，带动农户年均增收2万元'
    },
    {
      icon: Sunny,
      title: '生态保护',
      description: '保护和改善当地生态环境，打造美丽乡村示范点'
    },
    {
      icon: Brush,
      title: '文化传承',
      description: '挖掘和传承当地特色文化，提升文化软实力'
    }
  ],
  cooperation: [
    '政府提供政策支持和土地资源',
    '企业负责项目投资和运营管理',
    '农户提供劳动力和农产品',
    '共同分享项目收益',
    '建立长期合作机制'
  ],
  policies: [
    {
      title: '资金支持',
      content: '政府提供专项扶持资金，对符合条件的企业给予投资额30%的补贴，最高不超过500万元。'
    },
    {
      title: '土地政策',
      content: '优先保障项目用地，简化审批流程。对农业设施用地给予政策倾斜，租金优惠50%。'
    },
    {
      title: '税收优惠',
      content: '前三年免征企业所得税，后三年减半征收。农产品加工企业享受增值税即征即退政策。'
    },
    {
      title: '人才引进',
      content: '对引进的高层次人才提供住房补贴、子女入学等配套服务，简化落户手续。'
    }
  ],
  contact: {
    name: '张经理',
    phone: '023-6688-3344',
    email: 'banan_gov@cq.gov.cn',
    address: '重庆市巴南区政府大楼3楼'
  }
})

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

    // TODO: 调用后端API提交申请
    console.log('申请信息:', {
      projectId: project.value.id,
      projectTitle: project.value.title,
      ...applyForm.value
    })

    ElMessage.success('申请提交成功！我们会尽快与您联系')
    
    // 2秒后跳转到申请列表
    setTimeout(() => {
      router.push('/home/user/culture/applications')
    }, 2000)
  } catch {
    // 用户取消
  }
}

// 页面加载
onMounted(() => {
  const id = route.params.id
  console.log('加载政府项目详情，ID:', id)
  // TODO: 根据ID从后端加载实际数据
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

  .apply-card-sticky {
    position: static;
  }
}
</style>


