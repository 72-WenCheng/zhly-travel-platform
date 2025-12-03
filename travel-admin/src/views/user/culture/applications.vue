<template>
  <div class="applications-page">
    <!-- 返回按钮 -->
    <BackButton />

    <el-card class="page-header">
      <h1>
        <el-icon><Document /></el-icon>
        我的申请
      </h1>
    </el-card>

    <!-- 申请筛选 -->
    <el-card class="filter-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部申请" name="all" />
        <el-tab-pane label="审核中" name="pending">
          <template #label>
            审核中
            <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane label="已通过" name="approved" />
        <el-tab-pane label="已拒绝" name="rejected" />
      </el-tabs>
    </el-card>

    <!-- 申请列表 -->
    <div v-if="filteredApplications.length === 0" class="empty-applications">
      <el-empty description="暂无申请记录">
        <el-button type="primary" @click="goBrowse">去浏览项目</el-button>
      </el-empty>
    </div>

    <div v-else class="applications-list">
      <el-card v-for="application in filteredApplications" :key="application.id" class="application-card">
        <!-- 申请头部 -->
        <div class="application-header">
          <div class="header-left">
            <span class="application-time">{{ formatDateTime(application.createTime) }}</span>
            <span class="application-no">申请编号：{{ application.applicationNo }}</span>
          </div>
          <el-tag :type="getStatusType(application.status)" size="large">
            {{ getStatusText(application.status) }}
          </el-tag>
        </div>

        <el-divider />

        <!-- 项目信息 -->
        <div class="project-info" @click="goToProject(application.projectId)">
          <el-image :src="application.projectImage" fit="cover" class="project-image" />
          <div class="project-details">
            <h3 class="project-title">{{ application.projectTitle }}</h3>
            <div class="project-meta">
              <div class="meta-item">
                <el-icon><Location /></el-icon>
                <span>{{ application.location }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Coin /></el-icon>
                <span>投资额：{{ application.investmentAmount }}万元</span>
              </div>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 申请信息 -->
        <div class="application-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="企业/组织">{{ application.organizationName }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{ application.applicantName }}</el-descriptions-item>
            <el-descriptions-item label="职务">{{ application.position }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ application.phone }}</el-descriptions-item>
            <el-descriptions-item label="合作意向" :span="2">{{ getCooperationType(application.cooperationType) }}</el-descriptions-item>
            <el-descriptions-item v-if="application.description" label="申请说明" :span="2">
              {{ application.description }}
            </el-descriptions-item>
            <el-descriptions-item v-if="application.rejectReason && application.status === 'rejected'" label="拒绝原因" :span="2">
              <span style="color: #f56c6c;">{{ application.rejectReason }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 申请操作 -->
        <div class="application-actions">
          <el-button v-if="application.status === 'pending'" @click="handleWithdraw(application)">
            撤回申请
          </el-button>
          <el-button v-if="application.status === 'approved'" type="primary" @click="handleContact(application)">
            联系对接
          </el-button>
          <el-button @click="handleViewProject(application.projectId)">
            查看项目
          </el-button>
          <el-button v-if="application.status === 'rejected'" type="danger" link @click="handleDelete(application)">
            删除记录
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div v-if="filteredApplications.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import { Document, Location, Coin } from '@element-plus/icons-vue'

const router = useRouter()

// 当前标签页
const activeTab = ref('all')

// 申请列表（模拟数据）
const applications = ref([
  {
    id: 1,
    applicationNo: 'AP202410290001',
    createTime: '2024-10-29 14:30:25',
    status: 'pending',
    projectId: 1,
    projectTitle: '巴南区乡村振兴示范项目',
    projectImage: 'https://picsum.photos/120/120?random=40',
    location: '重庆市巴南区',
    organizationName: '重庆文旅发展有限公司',
    applicantName: '张三',
    position: '总经理',
    phone: '138****8888',
    email: 'zhang@example.com',
    cooperationType: 'investment',
    investmentAmount: 500,
    description: '我司专注于乡村旅游开发，拥有丰富的运营经验和专业团队，希望能与贵方合作开发此项目。'
  },
  {
    id: 2,
    applicationNo: 'AP202410280002',
    createTime: '2024-10-28 10:15:30',
    status: 'approved',
    projectId: 2,
    projectTitle: '长江三峡文化旅游综合体',
    projectImage: 'https://picsum.photos/120/120?random=41',
    location: '重庆市万州区',
    organizationName: '万州旅游集团',
    applicantName: '李四',
    position: '副总经理',
    phone: '139****9999',
    email: 'li@example.com',
    cooperationType: 'operation',
    investmentAmount: 1000,
    description: '我们在三峡地区有多年的旅游运营经验，希望能参与本项目的运营管理工作。'
  },
  {
    id: 3,
    applicationNo: 'AP202410270003',
    createTime: '2024-10-27 16:45:12',
    status: 'rejected',
    projectId: 1,
    projectTitle: '巴南区乡村振兴示范项目',
    projectImage: 'https://picsum.photos/120/120?random=40',
    location: '重庆市巴南区',
    organizationName: '某投资公司',
    applicantName: '王五',
    position: '项目经理',
    phone: '137****7777',
    email: 'wang@example.com',
    cooperationType: 'resource',
    investmentAmount: 200,
    description: '希望提供资源对接服务。',
    rejectReason: '投资金额不符合项目要求，建议调整后重新申请。'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredApplications.value.length)

// 审核中数量
const pendingCount = computed(() => applications.value.filter(a => a.status === 'pending').length)

// 过滤后的申请
const filteredApplications = computed(() => {
  if (activeTab.value === 'all') {
    return applications.value
  }
  return applications.value.filter(application => application.status === activeTab.value)
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '审核中',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取合作类型文本
const getCooperationType = (type) => {
  const typeMap = {
    investment: '资金投资',
    technology: '技术支持',
    operation: '运营管理',
    resource: '资源对接',
    other: '其他合作'
  }
  return typeMap[type] || type
}

// 标签页切换
const handleTabChange = (tab) => {
  console.log('切换到标签:', tab)
  currentPage.value = 1
}

// 跳转到项目详情
const goToProject = (projectId) => {
  router.push(`/home/user/culture/project/${projectId}`)
}

// 撤回申请
const handleWithdraw = async (application) => {
  try {
    await ElMessageBox.confirm('确定要撤回这个申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // TODO: 调用撤回申请接口
    console.log('撤回申请:', application.id)
    
    ElMessage.success('申请已撤回')
    const index = applications.value.findIndex(a => a.id === application.id)
    if (index > -1) {
      applications.value.splice(index, 1)
    }
  } catch {
    // 用户取消
  }
}

// 联系对接
const handleContact = (application) => {
  ElMessage.success('项目对接人员会在1个工作日内与您联系')
}

// 查看项目
const handleViewProject = (projectId) => {
  router.push(`/home/user/culture/project/${projectId}`)
}

// 删除记录
const handleDelete = async (application) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？删除后无法恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const index = applications.value.findIndex(a => a.id === application.id)
    if (index > -1) {
      applications.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 去浏览项目
const goBrowse = () => {
  router.push('/home/user/culture')
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
}
</script>

<style scoped lang="scss">
.applications-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;

  h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    margin: 0;

    .el-icon {
      font-size: 32px;
      color: #409eff;
    }
  }
}

.filter-card {
  margin-bottom: 24px;

  :deep(.el-tabs__item) {
    font-size: 16px;
  }

  .tab-badge {
    margin-left: 8px;
  }
}

.empty-applications {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  .application-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      gap: 24px;
      align-items: center;

      .application-time {
        font-size: 14px;
        color: #606266;
      }

      .application-no {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .project-info {
    display: flex;
    gap: 20px;
    cursor: pointer;
    padding: 16px;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    .project-image {
      width: 120px;
      height: 120px;
      border-radius: 8px;
      flex-shrink: 0;
    }

    .project-details {
      flex: 1;

      .project-title {
        font-size: 18px;
        font-weight: 700;
        color: #303133;
        margin: 0 0 16px 0;
      }

      .project-meta {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: #606266;

          .el-icon {
            color: #909399;
          }
        }
      }
    }
  }

  .application-info {
    :deep(.el-descriptions__label) {
      font-weight: 600;
      background: #fafafa;
    }
  }

  .application-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>



