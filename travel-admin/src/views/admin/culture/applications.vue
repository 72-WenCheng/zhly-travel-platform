<template>
  <div class="application-management">
    <BackButton />
    
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row">
          <el-form-item label="申请编号">
            <el-input v-model="filters.applicationNo" placeholder="请输入申请编号" clearable />
          </el-form-item>
          <el-form-item label="申请人类型">
            <el-select v-model="filters.applicantType" placeholder="请选择申请人类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="个人" :value="1" />
              <el-option label="企业" :value="2" />
              <el-option label="合作社" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="申请状态">
            <el-select v-model="filters.status" placeholder="请选择申请状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待审核" :value="1" />
              <el-option label="审核中" :value="2" />
              <el-option label="已通过" :value="3" />
              <el-option label="已拒绝" :value="4" />
              <el-option label="已撤回" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item label="提交时间">
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%;"
            />
          </el-form-item>
          <el-form-item label=" " class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <h3>项目申请列表</h3>
        <div class="actions">
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出申请
          </el-button>
          <el-badge :value="pendingCount" :max="99" class="badge-item">
            <el-button type="warning">
              <el-icon><Bell /></el-icon>
              待审核
            </el-button>
          </el-badge>
        </div>
      </div>

      <el-table :data="applicationList" style="width: 100%" v-loading="loading">
        <el-table-column prop="applicationNo" label="申请编号" width="180" fixed />
        <el-table-column prop="projectTitle" label="项目名称" width="220" show-overflow-tooltip />
        <el-table-column label="申请人类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getApplicantTypeTag(row.applicantType)" size="small">
              {{ getApplicantTypeName(row.applicantType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人/企业" width="150" show-overflow-tooltip />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="region" label="所在地区" width="150" show-overflow-tooltip />
        <el-table-column label="申请金额" width="120">
          <template #default="{ row }">
            <div class="amount">¥{{ row.applyAmount }}万</div>
          </template>
        </el-table-column>
        <el-table-column label="申请状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              link
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              <el-icon><Check /></el-icon>
              通过
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              link
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              <el-icon><Close /></el-icon>
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 3"
              link
              type="warning"
              size="small"
              @click="handleContract(row)"
            >
              <el-icon><Document /></el-icon>
              合同管理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 申请详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="申请详情" width="900px">
      <div v-if="currentApplication" class="application-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请编号">{{ currentApplication.applicationNo }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(currentApplication.status)">
              {{ getStatusName(currentApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目名称" :span="2">
            <strong>{{ currentApplication.projectTitle }}</strong>
          </el-descriptions-item>
          <el-descriptions-item label="申请人类型">
            {{ getApplicantTypeName(currentApplication.applicantType) }}
          </el-descriptions-item>
          <el-descriptions-item label="申请人/企业">{{ currentApplication.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentApplication.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentApplication.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="联系邮箱" :span="2" v-if="currentApplication.contactEmail">
            {{ currentApplication.contactEmail }}
          </el-descriptions-item>
          <el-descriptions-item label="身份证号" v-if="currentApplication.idCard">
            {{ currentApplication.idCard }}
          </el-descriptions-item>
          <el-descriptions-item label="营业执照号" v-if="currentApplication.businessLicense">
            {{ currentApplication.businessLicense }}
          </el-descriptions-item>
          <el-descriptions-item label="所在地区" :span="2">{{ currentApplication.region }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ currentApplication.detailedAddress }}</el-descriptions-item>
          <el-descriptions-item label="土地面积" v-if="currentApplication.landArea">
            {{ currentApplication.landArea }}亩
          </el-descriptions-item>
          <el-descriptions-item label="农户数量" v-if="currentApplication.householdCount">
            {{ currentApplication.householdCount }}户
          </el-descriptions-item>
          <el-descriptions-item label="申请金额" :span="2">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">
              ¥{{ currentApplication.applyAmount }}万元
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="项目描述" :span="2">
            <div class="description-text">{{ currentApplication.projectDescription }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="实施方案" :span="2" v-if="currentApplication.implementationPlan">
            <div class="description-text">{{ currentApplication.implementationPlan }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="预期效益" :span="2" v-if="currentApplication.expectedBenefits">
            <div class="description-text">{{ currentApplication.expectedBenefits }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="审核意见" :span="2" v-if="currentApplication.reviewOpinion">
            {{ currentApplication.reviewOpinion }}
          </el-descriptions-item>
          <el-descriptions-item label="拒绝原因" :span="2" v-if="currentApplication.rejectReason">
            <span style="color: #f56c6c;">{{ currentApplication.rejectReason }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="批准金额" v-if="currentApplication.approvalAmount">
            ¥{{ currentApplication.approvalAmount }}万元
          </el-descriptions-item>
          <el-descriptions-item label="审核时间" v-if="currentApplication.reviewTime">
            {{ currentApplication.reviewTime }}
          </el-descriptions-item>
          <el-descriptions-item label="合同编号" v-if="currentApplication.contractNo">
            {{ currentApplication.contractNo }}
          </el-descriptions-item>
          <el-descriptions-item label="合同签订时间" v-if="currentApplication.contractSignTime">
            {{ currentApplication.contractSignTime }}
          </el-descriptions-item>
          <el-descriptions-item label="项目开始时间" v-if="currentApplication.projectStartTime">
            {{ currentApplication.projectStartTime }}
          </el-descriptions-item>
          <el-descriptions-item label="项目结束时间" v-if="currentApplication.projectEndTime">
            {{ currentApplication.projectEndTime }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentApplication.submitTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentApplication.updateTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="currentApplication.remark">
            {{ currentApplication.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 审核通过对话框 -->
    <el-dialog v-model="approveDialogVisible" title="审核通过" width="600px">
      <el-form :model="approveForm" label-width="120px">
        <el-form-item label="批准金额" required>
          <el-input-number v-model="approveForm.approvalAmount" :min="0" :precision="2" />
          <span style="margin-left: 8px;">万元</span>
        </el-form-item>
        <el-form-item label="审核意见" required>
          <el-input
            v-model="approveForm.reviewOpinion"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
        <el-form-item label="项目时间">
          <el-date-picker
            v-model="projectTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="项目开始时间"
            end-placeholder="项目结束时间"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="approveForm.remark"
            type="textarea"
            :rows="3"
            placeholder="选填"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApprove">确认通过</el-button>
      </template>
    </el-dialog>

    <!-- 审核拒绝对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="审核拒绝" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因" required>
          <el-input
            v-model="rejectForm.rejectReason"
            type="textarea"
            :rows="5"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 合同管理对话框 -->
    <el-dialog v-model="contractDialogVisible" title="合同管理" width="600px">
      <el-form :model="contractForm" label-width="120px">
        <el-form-item label="合同编号" required>
          <el-input v-model="contractForm.contractNo" placeholder="请输入合同编号" />
        </el-form-item>
        <el-form-item label="合同签订时间" required>
          <el-date-picker
            v-model="contractForm.contractSignTime"
            type="datetime"
            placeholder="选择签订时间"
          />
        </el-form-item>
        <el-form-item label="项目时间" required>
          <el-date-picker
            v-model="contractTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="项目开始时间"
            end-placeholder="项目结束时间"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="contractForm.remark"
            type="textarea"
            :rows="3"
            placeholder="选填"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contractDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmContract">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Bell,
  View,
  Check,
  Close,
  Document
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import { formatDateTime } from '@/utils'
import request from '@/utils/request'

// 筛选条件
const filters = reactive({
  applicationNo: '',
  applicantType: '',
  status: '',
  dateRange: null as any
})

// 申请列表
const applicationList = ref([])

const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 待审核数量
const pendingCount = computed(() => {
  return applicationList.value.filter(item => item.status === 1 || item.status === 2).length
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentApplication = ref<any>(null)

// 审核通过对话框
const approveDialogVisible = ref(false)
const approveForm = reactive({
  applicationId: null as number | null,
  approvalAmount: 0,
  reviewOpinion: '',
  remark: ''
})
const projectTimeRange = ref<any>(null)

// 审核拒绝对话框
const rejectDialogVisible = ref(false)
const rejectForm = reactive({
  applicationId: null as number | null,
  rejectReason: ''
})

// 合同管理对话框
const contractDialogVisible = ref(false)
const contractForm = reactive({
  applicationId: null as number | null,
  contractNo: '',
  contractSignTime: null as any,
  remark: ''
})
const contractTimeRange = ref<any>(null)

// 获取申请人类型名称
const getApplicantTypeName = (type: number) => {
  const map: Record<number, string> = {
    1: '个人',
    2: '企业',
    3: '合作社'
  }
  return map[type] || '未知'
}

// 获取申请人类型标签
const getApplicantTypeTag = (type: number) => {
  const map: Record<number, string> = {
    1: 'success',
    2: 'primary',
    3: 'warning'
  }
  return map[type] || 'info'
}

// 获取申请状态名称
const getStatusName = (status: number) => {
  const map: Record<number, string> = {
    1: '待审核',
    2: '审核中',
    3: '已通过',
    4: '已拒绝',
    5: '已撤回'
  }
  return map[status] || '未知'
}

// 获取状态类型
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger',
    5: 'info'
  }
  return map[status] || 'info'
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadApplications()
}

// 重置
const handleReset = () => {
  filters.applicationNo = ''
  filters.applicantType = ''
  filters.status = ''
  filters.dateRange = null
  pagination.page = 1
  loadApplications()
}

const route = useRoute()

// 加载申请列表
const loadApplications = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size
    }
    if (filters.applicationNo) params.applicationNo = filters.applicationNo
    if (filters.applicantType) params.applicantType = filters.applicantType
    if (filters.status) params.status = filters.status
    
    // 从路由参数中获取项目ID（用于从内容管理页面跳转过来时筛选）
    const projectId = route.query.projectId as string
    if (projectId) {
      params.projectId = projectId
    }
    
    const res = await request.get('/admin/culture/application/page', { params })
    if (res.code === 200) {
      applicationList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentApplication.value = row
  detailDialogVisible.value = true
}

// 审核通过
const handleApprove = (row: any) => {
  approveForm.applicationId = row.id
  approveForm.approvalAmount = row.applyAmount
  approveForm.reviewOpinion = ''
  approveForm.remark = ''
  projectTimeRange.value = null
  approveDialogVisible.value = true
}

// 确认审核通过
const confirmApprove = async () => {
  if (!approveForm.approvalAmount || !approveForm.reviewOpinion) {
    ElMessage.warning('请填写批准金额和审核意见')
    return
  }

  try {
    const approveInfo: any = {
      approvalAmount: approveForm.approvalAmount,
      reviewOpinion: approveForm.reviewOpinion
    }
    if (projectTimeRange.value && projectTimeRange.value.length === 2) {
      approveInfo.projectStartTime = projectTimeRange.value[0]
      approveInfo.projectEndTime = projectTimeRange.value[1]
    }
    if (approveForm.remark) {
      approveInfo.remark = approveForm.remark
    }
    
    const res = await request.put(`/admin/culture/application/${approveForm.applicationId}/approve`, approveInfo)
    if (res.code === 200) {
      ElMessage.success('审核通过')
      approveDialogVisible.value = false
      loadApplications()
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 审核拒绝
const handleReject = (row: any) => {
  rejectForm.applicationId = row.id
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

// 确认审核拒绝
const confirmReject = async () => {
  if (!rejectForm.rejectReason) {
    ElMessage.warning('请填写拒绝原因')
    return
  }

  try {
    const res = await request.put(`/admin/culture/application/${rejectForm.applicationId}/reject`, {
      rejectReason: rejectForm.rejectReason
    })
    if (res.code === 200) {
      ElMessage.success('已拒绝申请')
      rejectDialogVisible.value = false
      loadApplications()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 合同管理
const handleContract = (row: any) => {
  contractForm.applicationId = row.id
  contractForm.contractNo = row.contractNo || ''
  contractForm.contractSignTime = row.contractSignTime ? new Date(row.contractSignTime) : null
  contractForm.remark = row.remark || ''
  contractTimeRange.value = row.projectStartTime && row.projectEndTime
    ? [row.projectStartTime, row.projectEndTime]
    : null
  contractDialogVisible.value = true
}

// 确认合同信息
const confirmContract = async () => {
  if (!contractForm.contractNo || !contractForm.contractSignTime || !contractTimeRange.value) {
    ElMessage.warning('请填写完整的合同信息')
    return
  }

  try {
    const contractInfo: any = {
      contractNo: contractForm.contractNo,
      contractSignTime: contractForm.contractSignTime.toISOString(),
      projectStartTime: contractTimeRange.value[0],
      projectEndTime: contractTimeRange.value[1]
    }
    if (contractForm.remark) {
      contractInfo.remark = contractForm.remark
    }
    
    const res = await request.put(`/admin/culture/application/${contractForm.applicationId}/contract`, contractInfo)
    if (res.code === 200) {
      ElMessage.success('合同信息已保存')
      contractDialogVisible.value = false
      loadApplications()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 导出申请
const handleExport = async () => {
  try {
    const params: any = {}
    if (filters.applicationNo) params.applicationNo = filters.applicationNo
    if (filters.applicantType) params.applicantType = filters.applicantType
    if (filters.status) params.status = filters.status
    
    const projectId = route.query.projectId as string
    if (projectId) params.projectId = projectId
    
    const response = await request.get('/admin/culture/application/export', {
      params,
      responseType: 'blob'
    })
    
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `项目申请_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadApplications()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadApplications()
}

// 页面加载
onMounted(() => {
  loadApplications()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style scoped lang="scss">
.application-management {
  padding: 20px;

  .table-card {
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
      }

      .actions {
        display: flex;
        gap: 10px;
        align-items: center;

        .badge-item {
          :deep(.el-badge__content) {
            top: 8px;
            right: 20px;
          }
        }
      }
    }

    .amount {
      font-size: 14px;
      font-weight: 500;
      color: #f56c6c;
    }

    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }

  .application-detail {
    padding: 10px 0;

    .description-text {
      line-height: 1.8;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }
}
</style>



