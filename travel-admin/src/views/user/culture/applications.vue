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
    <div v-loading="loading">
      <div v-if="!loading && filteredApplications.length === 0" class="empty-applications">
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
          <el-image 
            :src="application.projectImage || '/default-project.jpg'" 
            fit="cover" 
            class="project-image"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
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
            <el-descriptions-item v-if="application.rejectReason && (application.status === 4 || application.status === 'rejected')" label="拒绝原因" :span="2">
              <span style="color: #f56c6c;">{{ application.rejectReason }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 申请操作 -->
        <div class="application-actions">
          <el-button type="primary" link @click="handleViewDetail(application)">
            查看详情
          </el-button>
          <el-button v-if="application.status === 1 || application.status === 2" @click="handleWithdraw(application)">
            撤回申请
          </el-button>
          <el-button v-if="application.status === 3" type="primary" @click="handleContact(application)">
            联系对接
          </el-button>
          <el-button @click="handleViewProject(application.projectId)">
            查看项目
          </el-button>
          <el-button v-if="application.status === 4" type="danger" link @click="handleDelete(application)">
            删除记录
          </el-button>
        </div>
      </el-card>
      </div>
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

  <!-- 申请详情弹窗 -->
  <el-dialog v-model="detailDialogVisible" title="申请详情" width="900px">
    <div v-if="currentApplication" class="application-detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentApplication.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusType(currentApplication.status)" size="small">
            {{ getStatusText(currentApplication.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="项目名称" :span="2">
          <strong>{{ currentApplication.projectTitle }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="申请人/企业">{{ currentApplication.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentApplication.contactName || currentApplication.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="职务">{{ currentApplication.position || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentApplication.phone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱" v-if="currentApplication.email" :span="2">
          {{ currentApplication.email }}
        </el-descriptions-item>
        <el-descriptions-item label="所在地区" v-if="currentApplication.region" :span="2">
          {{ currentApplication.region }}
        </el-descriptions-item>
        <el-descriptions-item label="详细地址" v-if="currentApplication.detailedAddress" :span="2">
          {{ currentApplication.detailedAddress }}
        </el-descriptions-item>
        <el-descriptions-item label="申请金额" :span="2">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
            ¥{{ currentApplication.investmentAmount || 0 }} 万元
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="批准金额" v-if="currentApplication.approvalAmount" :span="2">
          <span style="color: #67c23a; font-size: 18px; font-weight: bold;">
            ¥{{ currentApplication.approvalAmount }} 万元
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="合作意向" :span="2">
          {{ getCooperationType(currentApplication.cooperationType) }}
        </el-descriptions-item>
        <el-descriptions-item label="申请说明" v-if="currentApplication.description" :span="2">
          {{ currentApplication.description }}
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="currentApplication.reviewOpinion" :span="2">
          {{ currentApplication.reviewOpinion }}
        </el-descriptions-item>
        <el-descriptions-item label="拒绝原因" v-if="currentApplication.rejectReason && (currentApplication.status === 4 || currentApplication.status === 'rejected')" :span="2">
          <span style="color: #f56c6c;">{{ currentApplication.rejectReason }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="合同编号" v-if="currentApplication.contractNo">{{ currentApplication.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同签订时间" v-if="currentApplication.contractSignTime">
          {{ formatDateTime(currentApplication.contractSignTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="项目开始时间" v-if="currentApplication.projectStartTime">
          {{ formatDateTime(currentApplication.projectStartTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="项目结束时间" v-if="currentApplication.projectEndTime">
          {{ formatDateTime(currentApplication.projectEndTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ formatDateTime(currentApplication.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" v-if="currentApplication.updateTime">
          {{ formatDateTime(currentApplication.updateTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" v-if="currentApplication.remark" :span="2">
          {{ currentApplication.remark }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="detailDialogVisible = false">我知道了</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import { Document, Location, Coin, Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getProjectById } from '@/api/cultureProject'

const router = useRouter()

// 加载状态
const loading = ref(false)

// 当前标签页
const activeTab = ref('all')

// 申请列表
const applications = ref([])
// 详情弹窗
const detailDialogVisible = ref(false)
const currentApplication = ref(null)

// 项目信息缓存（用于存储项目详情）
const projectCache = ref(new Map())

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 审核中数量
const pendingCount = computed(() => {
  return applications.value.filter(a => {
    const status = typeof a.status === 'number' ? a.status : parseInt(a.status)
    return status === 1 || status === 2 // 1-待审核, 2-审核中
  }).length
})

// 过滤后的申请
const filteredApplications = computed(() => {
  let filtered = applications.value
  
  if (activeTab.value === 'all') {
    return filtered
  }
  
  // 根据状态过滤
  return filtered.filter(application => {
    const status = typeof application.status === 'number' ? application.status : parseInt(application.status)
    if (activeTab.value === 'pending') {
      return status === 1 || status === 2 // 待审核或审核中
    } else if (activeTab.value === 'approved') {
      return status === 3 // 已通过
    } else if (activeTab.value === 'rejected') {
      return status === 4 // 已拒绝
    }
    return true
  })
})

// 状态映射：后端数字 -> 前端字符串
const mapStatusToFrontend = (status) => {
  // 后端状态：1-待审核, 2-审核中, 3-已通过, 4-已拒绝, 5-已撤回
  const statusMap = {
    1: 'pending',
    2: 'pending',
    3: 'approved',
    4: 'rejected',
    5: 'withdrawn'
  }
  return statusMap[status] || 'unknown'
}

// 获取状态文本
const getStatusText = (status) => {
  const numStatus = typeof status === 'number' ? status : parseInt(status)
  const statusMap = {
    1: '待审核',
    2: '审核中',
    3: '已通过',
    4: '已拒绝',
    5: '已撤回'
  }
  return statusMap[numStatus] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const numStatus = typeof status === 'number' ? status : parseInt(status)
  const typeMap = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'info'
  }
  return typeMap[numStatus] || 'info'
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
  return typeMap[type] || type || '未指定'
}

// 加载申请列表
const loadApplications = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    // 根据标签页设置状态过滤
    if (activeTab.value === 'pending') {
      // 审核中包括待审核(1)和审核中(2)，这里需要后端支持多状态查询
      // 暂时先不传status，前端过滤
    } else if (activeTab.value === 'approved') {
      params.status = 3
    } else if (activeTab.value === 'rejected') {
      params.status = 4
    }
    
    const res = await request.get('/user/culture/application/my', { params })
    console.log('申请列表API响应:', res)
    
    if (res.code === 200 && res.data) {
      // 处理分页数据格式
      let records = []
      if (res.data.records) {
        records = res.data.records
      } else if (res.data.list) {
        records = res.data.list
      } else if (Array.isArray(res.data)) {
        records = res.data
      }
      
      console.log('解析后的申请记录:', records, '数量:', records.length)
      
      // 加载项目信息并映射数据
      const mappedApplications = await Promise.all(
        records.map(async (item) => {
          // 加载项目信息
          let projectInfo = null
          if (item.projectId) {
            if (projectCache.value.has(item.projectId)) {
              projectInfo = projectCache.value.get(item.projectId)
            } else {
              try {
                const projectRes = await getProjectById(item.projectId)
                if (projectRes.code === 200 && projectRes.data) {
                  projectInfo = projectRes.data
                  projectCache.value.set(item.projectId, projectInfo)
                }
              } catch (e) {
                console.warn('加载项目信息失败:', e)
              }
            }
          }
          
          // 解析图片
          let projectImage = ''
          if (projectInfo) {
            if (projectInfo.images) {
              try {
                const images = typeof projectInfo.images === 'string' 
                  ? JSON.parse(projectInfo.images) 
                  : projectInfo.images
                projectImage = Array.isArray(images) && images.length > 0 ? images[0] : projectInfo.image || ''
              } catch {
                projectImage = projectInfo.image || ''
              }
            } else {
              projectImage = projectInfo.image || ''
            }
          }
          
          return {
            id: item.id,
            applicationNo: item.applicationNo || `AP${item.id}`,
            createTime: item.submitTime || item.createTime,
            updateTime: item.updateTime || null,
            status: item.status,
            projectId: item.projectId,
            projectTitle: item.projectTitle || projectInfo?.name || '未知项目',
            projectImage: projectImage,
            location: projectInfo?.location || projectInfo?.region || item.region || '未知地区',
            region: item.region || projectInfo?.region || '',
            detailedAddress: item.detailedAddress || '',
            organizationName: item.applicantName || '',
            applicantName: item.contactName || item.applicantName || '',
            contactName: item.contactName || '',
            position: item.position || '',
            phone: item.contactPhone || '',
            email: item.contactEmail || '',
            cooperationType: item.cooperationType || '',
            investmentAmount: item.applyAmount ? Number(item.applyAmount) : 0,
            approvalAmount: item.approvalAmount ? Number(item.approvalAmount) : null,
            description: item.projectDescription || '',
            reviewOpinion: item.reviewOpinion || '',
            rejectReason: item.rejectReason || '',
            projectStartTime: item.projectStartTime || '',
            projectEndTime: item.projectEndTime || '',
            contractNo: item.contractNo || '',
            contractSignTime: item.contractSignTime || '',
            remark: item.remark || ''
          }
        })
      )
      
      applications.value = mappedApplications
      total.value = res.data.total || mappedApplications.length
    } else {
      ElMessage.error(res.message || '加载申请列表失败')
      applications.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载申请列表失败:', error)
    ElMessage.error('加载申请列表失败: ' + (error.message || '未知错误'))
    applications.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tab) => {
  console.log('切换到标签:', tab)
  currentPage.value = 1
  loadApplications()
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

    const res = await request.put(`/user/culture/application/${application.id}/withdraw`)
    
    if (res.code === 200) {
      ElMessage.success('申请已撤回')
      // 重新加载列表
      loadApplications()
    } else {
      ElMessage.error(res.message || '撤回申请失败')
    }
  } catch (error) {
    if (error.message && !error.message.includes('取消')) {
      ElMessage.error('撤回申请失败: ' + error.message)
    }
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

// 查看详情
const handleViewDetail = (application) => {
  currentApplication.value = application
  detailDialogVisible.value = true
}

// 删除记录
const handleDelete = async (application) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？删除后无法恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 注意：后端可能没有删除接口，这里只是前端移除
    // 如果需要真正的删除，需要后端提供删除接口
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
  loadApplications()
}

// 页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadApplications()
}

// 页面加载时获取数据
onMounted(() => {
  loadApplications()
})
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



