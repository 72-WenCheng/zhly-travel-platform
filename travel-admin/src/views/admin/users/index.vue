<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><User /></el-icon>
        </div>
        <div class="header-title">
          <h1>用户管理</h1>
          <p>管理系统用户信息与权限</p>
          <div class="status-info">
            <div class="status-text">
              <el-icon class="status-icon"><Refresh /></el-icon>
              <span>数据每30秒自动刷新</span>
            </div>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="action-btn" @click="exportUsers">
          <el-icon><Download /></el-icon>
          导出用户
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :style="{ background: stat.color }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">{{ stat.value }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="searchForm" class="filter-form">
        <div class="filter-row">
          <el-form-item label="用户名">
            <el-input 
              v-model="searchForm.username" 
              placeholder="请输入用户名" 
              clearable 
              prefix-icon="Search"
            />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input 
              v-model="searchForm.email" 
              placeholder="请输入邮箱" 
              clearable 
              prefix-icon="Message"
            />
          </el-form-item>
          <el-form-item label="个性化标签">
            <el-select v-model="searchForm.userType" placeholder="请选择个性化标签" clearable>
              <el-option label="个人" :value="1" />
              <el-option label="情侣" :value="2" />
              <el-option label="家庭" :value="3" />
              <el-option label="团队" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="用户角色">
            <el-select v-model="searchForm.role" placeholder="请选择用户角色" clearable>
              <el-option label="管理员" :value="1" />
              <el-option label="普通用户" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
        </div>
        <div class="filter-row">
          <el-form-item label="注册时间" style="flex: 0 0 320px;">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%;"
            />
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedUsers.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedUsers.length }}</strong> 个用户</span>
          </div>
          <div class="batch-buttons">
            <el-button type="success" size="small" @click="batchEnable">
              <el-icon><Check /></el-icon>
              批量启用
            </el-button>
            <el-button type="warning" size="small" @click="batchDisable">
              <el-icon><Close /></el-icon>
              批量禁用
            </el-button>
            <el-dropdown @command="handleBatchRoleChange" size="small">
              <el-button type="primary" size="small">
                <el-icon><User /></el-icon>
                批量修改角色
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="1">设为管理员</el-dropdown-item>
                  <el-dropdown-item command="2">设为普通用户</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" size="small" @click="batchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button size="small" @click="clearSelection">
              <el-icon><CloseBold /></el-icon>
              取消选择
            </el-button>
          </div>
        </div>
      </el-card>
    </transition>

    <!-- 用户列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="userList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="avatar" label="头像" width="90" align="center">
          <template #default="{ row }">
            <div class="avatar-cell">
              <el-avatar :src="row.avatar" :size="48" class="user-avatar">
                {{ row.nickname?.charAt(0) }}
              </el-avatar>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="130" />
        <el-table-column prop="email" label="邮箱" width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="userType" label="个性化标签" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTag(row.userType)" class="user-type-tag">
              {{ getUserTypeName(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="用户角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTag(row.role)" class="role-tag">
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="travelPreference" label="旅游偏好" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="primary" size="small" class="preference-tag">
              {{ getTravelPreferenceName(row.travelPreference) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100" align="center">
          <template #default="{ row }">
            <span class="points-cell">{{ row.points }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="warning" size="small" class="level-tag">Lv.{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" class="status-tag">
              <el-icon><component :is="row.status === 1 ? 'Check' : 'Close'" /></el-icon>
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170">
          <template #default="{ row }">
            <span v-if="row.lastLoginTime">{{ formatDateTime(row.lastLoginTime) }}</span>
            <span v-else style="color: #999;">未登录</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" text @click="viewUserDetail(row)">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button type="info" size="small" text @click="editUser(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-dropdown @command="(cmd) => handleUserAction(cmd, row)" trigger="click" size="small">
                <el-button type="warning" size="small" text>
                  <el-icon><Tools /></el-icon>
                  管理
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="points">
                      <el-icon><Medal /></el-icon>
                      调整积分
                    </el-dropdown-item>
                    <el-dropdown-item command="level">
                      <el-icon><Trophy /></el-icon>
                      调整等级
                    </el-dropdown-item>
                    <el-dropdown-item command="loginHistory" divided>
                      <el-icon><Clock /></el-icon>
                      登录历史
                    </el-dropdown-item>
                    <el-dropdown-item command="operationLogs">
                      <el-icon><Document /></el-icon>
                      操作日志
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                size="small" 
                text
                @click="toggleStatus(row)"
              >
                <el-icon><component :is="row.status === 1 ? 'Lock' : 'Unlock'" /></el-icon>
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" size="small" text @click="deleteUser(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      </div>

      <div class="pagination-container-modern simple-pagination">
        <el-button
          class="page-btn"
          :disabled="pagination.current <= 1"
          @click="handleCurrentChange(pagination.current - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="page-info">
          {{ pagination.current }} / {{ Math.max(1, Math.ceil(pagination.total / pagination.size || 1)) }}
        </span>
        <el-button
          class="page-btn"
          :disabled="pagination.current >= Math.ceil(pagination.total / pagination.size || 1)"
          @click="handleCurrentChange(pagination.current + 1)"
        >
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <div class="page-jump">
          <span>前往</span>
          <el-input
            v-model.number="pageJump"
            size="small"
            class="page-jump-input"
            @input="handlePageJump"
          />
          <span>页</span>
        </div>
      </div>
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      width="650px"
      :close-on-click-modal="false"
      class="edit-user-dialog"
    >
      <template #header>
        <span style="font-size: 18px; font-weight: 600;">编辑用户信息</span>
      </template>
      <el-form :model="editForm" label-width="110px" :rules="editRules" ref="editFormRef" class="edit-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled />
          <span class="form-tip">用户名不可修改</span>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="个性化标签" prop="userType">
          <el-select v-model="editForm.userType" placeholder="请选择个性化标签">
            <el-option label="个人" :value="1" />
            <el-option label="情侣" :value="2" />
            <el-option label="家庭" :value="3" />
            <el-option label="团队" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="用户角色" prop="role">
          <el-select v-model="editForm.role" placeholder="请选择用户角色">
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="旅游偏好" prop="travelPreference">
          <el-select v-model="editForm.travelPreference" placeholder="请选择旅游偏好">
            <el-option label="穷游" :value="1" />
            <el-option label="富游" :value="2" />
            <el-option label="中等消费" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="兴趣爱好" prop="interestTags">
          <el-input 
            v-model="editForm.interestTags" 
            type="textarea" 
            :rows="3"
            placeholder="请输入兴趣爱好，多个用逗号分隔"
          />
        </el-form-item>
        
        <el-form-item label="常去城市" prop="frequentCities">
          <el-input 
            v-model="editForm.frequentCities" 
            type="textarea" 
            :rows="3"
            placeholder="请输入常去城市，多个用逗号分隔"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button class="simple-btn" @click="editDialogVisible = false">取消</el-button>
          <el-button class="simple-btn" @click="submitEdit" :loading="editLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="用户详情" 
      width="900px"
      :close-on-click-modal="false"
      class="modern-detail-dialog"
    >
      <div class="dialog-header-decoration"></div>
      <div v-loading="detailLoading" class="user-detail-content">
        <div v-if="userDetail.basicInfo" class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">{{ userDetail.basicInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userDetail.basicInfo.nickname }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userDetail.basicInfo.email }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userDetail.basicInfo.phone }}</el-descriptions-item>
            <el-descriptions-item label="积分">{{ userDetail.basicInfo.points || 0 }}</el-descriptions-item>
            <el-descriptions-item label="等级">Lv.{{ userDetail.basicInfo.level || 1 }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userDetail.basicInfo.status === 1 ? 'success' : 'danger'">
                {{ userDetail.basicInfo.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDateTime(userDetail.basicInfo.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div v-if="userDetail.browseStats" class="detail-section">
          <h3 class="section-title">行为数据</h3>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="总浏览数">{{ userDetail.browseStats.totalBrowses || 0 }}</el-descriptions-item>
            <el-descriptions-item label="景点浏览">{{ userDetail.browseStats.attractionBrowses || 0 }}</el-descriptions-item>
            <el-descriptions-item label="攻略浏览">{{ userDetail.browseStats.planBrowses || 0 }}</el-descriptions-item>
            <el-descriptions-item label="收藏数">{{ userDetail.collectCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="评论数">{{ userDetail.commentCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="订单数">{{ userDetail.orderCount || 0 }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>

    <!-- 积分调整对话框 -->
    <el-dialog 
      v-model="pointsDialogVisible" 
      width="500px"
      :close-on-click-modal="false"
      class="points-adjust-dialog"
      :show-close="true"
    >
      <template #header>
        <div style="border-bottom: none !important; padding-bottom: 0;">
          <span style="font-size: 18px; font-weight: 600;">调整用户积分</span>
        </div>
      </template>
      <el-form :model="pointsForm" label-width="100px">
        <el-form-item label="当前积分">
          <el-input :value="currentUser?.points || 0" disabled />
        </el-form-item>
        <el-form-item label="积分变动" required>
          <el-input-number 
            v-model="pointsForm.points" 
            :min="-999999" 
            :max="999999"
            placeholder="正数为增加，负数为扣除"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="变动原因">
          <el-input 
            v-model="pointsForm.reason" 
            type="textarea" 
            :rows="3"
            placeholder="请输入积分变动原因"
          />
        </el-form-item>
        <el-form-item>
          <div class="form-tip">调整后积分：{{ (currentUser?.points || 0) + (pointsForm.points || 0) }}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitPointsAdjust" 
          :loading="pointsLoading"
          style="--el-button-icon-left: none;"
        >
          <span>确认调整</span>
        </el-button>
      </template>
    </el-dialog>

    <!-- 等级调整对话框 -->
    <el-dialog 
      v-model="levelDialogVisible" 
      title="调整等级" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="levelForm" label-width="100px">
        <el-form-item label="当前等级">
          <el-input :value="`Lv.${currentUser?.level || 1}`" disabled />
        </el-form-item>
        <el-form-item label="新等级" required>
          <el-input-number 
            v-model="levelForm.level" 
            :min="1" 
            :max="10"
            placeholder="请输入等级（1-10）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input 
            v-model="levelForm.reason" 
            type="textarea" 
            :rows="3"
            placeholder="请输入等级调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="levelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLevelAdjust" :loading="levelLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 登录历史对话框 -->
    <el-dialog 
      v-model="loginHistoryDialogVisible" 
      title="登录历史" 
      width="900px"
      :close-on-click-modal="false"
    >
      <el-table :data="loginHistoryList" v-loading="loginHistoryLoading" style="width: 100%">
        <el-table-column prop="loginTime" label="登录时间" width="180" />
        <el-table-column prop="loginIp" label="登录IP" width="150" />
        <el-table-column prop="loginDevice" label="设备" width="150" />
        <el-table-column prop="loginLocation" label="地点" width="150" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="failReason" label="失败原因" show-overflow-tooltip />
      </el-table>
      <div class="pagination-container-modern" style="margin-top: 20px;">
        <el-pagination
          v-model:current-page="loginHistoryPagination.current"
          v-model:page-size="loginHistoryPagination.size"
          :page-sizes="[10, 20, 50]"
          :total="loginHistoryPagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadLoginHistory"
          @current-change="loadLoginHistory"
        />
      </div>
    </el-dialog>

    <!-- 操作日志对话框 -->
    <el-dialog 
      v-model="operationLogsDialogVisible" 
      title="操作日志" 
      width="900px"
      :close-on-click-modal="false"
    >
      <el-table :data="operationLogsList" v-loading="operationLogsLoading" style="width: 100%">
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column prop="operationType" label="操作类型" width="120" />
        <el-table-column prop="operationModule" label="操作模块" width="120" />
        <el-table-column prop="operationContent" label="操作内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operationIp" label="操作IP" width="150" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
      <div class="pagination-container-modern" style="margin-top: 20px;">
        <el-pagination
          v-model:current-page="operationLogsPagination.current"
          v-model:page-size="operationLogsPagination.size"
          :page-sizes="[10, 20, 50]"
          :total="operationLogsPagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadOperationLogs"
          @current-change="loadOperationLogs"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import BackButton from '@/components/BackButton.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, Download, Search, Refresh, Select, Check, Close, Delete, CloseBold,
  Edit, Lock, Unlock, UserFilled, Medal, Trophy, DataAnalysis, View, Tools,
  ArrowDown, Clock, Document
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref<NodeJS.Timeout | null>(null)
const lastUpdateTime = ref<string>('')

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  userType: null,
  role: null,
  status: null,
  dateRange: null
})

// 用户列表
const userList = ref([])
const loading = ref(false)

// 选中的用户
const selectedUsers = ref([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 翻页跳转
const pageJump = ref<number | null>(null)

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10)))
  let target = Number(pageJump.value || 1)
  if (!Number.isFinite(target)) return
  if (target < 1) target = 1
  if (target > totalPages) target = totalPages
  if (target === pagination.current) return
  handleCurrentChange(target)
}

// 统计数据
const userStats = ref({
  totalUsers: 0,
  activeUsers: 0,
  vipUsers: 0,
  todayNew: 0
})

// 编辑对话框
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref()
const editForm = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  userType: null,
  role: null,
  travelPreference: null,
  interestTags: '',
  frequentCities: ''
})

// 编辑表单验证规则
const editRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 当前操作的用户
const currentUser = ref<any>(null)

// 用户详情对话框
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const userDetail = ref<any>({})

// 积分调整对话框
const pointsDialogVisible = ref(false)
const pointsLoading = ref(false)
const pointsForm = reactive({
  points: 0,
  reason: ''
})

// 等级调整对话框
const levelDialogVisible = ref(false)
const levelLoading = ref(false)
const levelForm = reactive({
  level: 1,
  reason: ''
})

// 登录历史对话框
const loginHistoryDialogVisible = ref(false)
const loginHistoryLoading = ref(false)
const loginHistoryList = ref([])
const loginHistoryPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 操作日志对话框
const operationLogsDialogVisible = ref(false)
const operationLogsLoading = ref(false)
const operationLogsList = ref([])
const operationLogsPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const stats = computed(() => [
  {
    label: '总用户数',
    value: userStats.value.totalUsers,
    icon: 'UserFilled',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    label: '活跃用户',
    value: userStats.value.activeUsers,
    icon: 'Medal',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    label: 'VIP用户',
    value: userStats.value.vipUsers,
    icon: 'Trophy',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    label: '今日新增',
    value: userStats.value.todayNew,
    icon: 'DataAnalysis',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 获取用户类型名称（个性化标签）
const getUserTypeName = (type: number) => {
  const typeMap = {
    1: '个人',
    2: '情侣',
    3: '家庭',
    4: '团队'
  }
  return typeMap[type] || '未知'
}

// 获取用户类型标签
const getUserTypeTag = (type: number) => {
  const tagMap = {
    1: 'info',     // 个人用蓝色
    2: 'success',  // 情侣用绿色
    3: 'warning',  // 家庭用橙色
    4: 'danger'    // 团队用红色
  }
  return tagMap[type] || ''
}

// 获取用户角色名称
const getRoleName = (role: number) => {
  const roleMap = {
    1: '管理员',
    2: '普通用户'
  }
  return roleMap[role] || '未知'
}

// 获取用户角色标签
const getRoleTag = (role: number) => {
  const tagMap = {
    1: 'danger',  // 管理员用红色
    2: 'info'     // 普通用户用蓝色
  }
  return tagMap[role] || ''
}

// 获取旅游偏好名称
const getTravelPreferenceName = (preference: number) => {
  const preferenceMap = {
    1: '自然风光',
    2: '人文历史',
    3: '美食体验',
    4: '购物娱乐',
    5: '休闲度假'
  }
  return preferenceMap[preference] || '未设置'
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    email: '',
    userType: null,
    role: null,
    status: null,
    dateRange: null
  })
  pagination.current = 1
  loadUserList()
}

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout: NodeJS.Timeout | null = null
watch(
  () => [
    searchForm.username,
    searchForm.email,
    searchForm.userType,
    searchForm.role,
    searchForm.status,
    searchForm.dateRange
  ],
  () => {
    // 清除之前的定时器
    if (searchTimeout) {
      clearTimeout(searchTimeout)
    }
    // 设置新的定时器（防抖：500ms）
    searchTimeout = setTimeout(() => {
      pagination.current = 1
      loadUserList()
    }, 500)
  },
  { deep: true }
)

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    console.log('开始加载用户列表...')
    
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.username) {
      params.keyword = searchForm.username
    }
    
    const result = await request.get('/admin/user/list', { params })
    console.log('用户列表结果:', result)
    
    if (result.code === 200 && result.data) {
      let filteredData = result.data
      
      // 前端筛选：邮箱
      if (searchForm.email) {
        filteredData = filteredData.filter((user: any) => 
          user.email && user.email.includes(searchForm.email)
        )
      }
      
      // 前端筛选：个性化标签
      if (searchForm.userType !== null && searchForm.userType !== undefined) {
        filteredData = filteredData.filter((user: any) => 
          user.userType === searchForm.userType
        )
      }
      
      // 前端筛选：用户角色
      if (searchForm.role !== null && searchForm.role !== undefined) {
        filteredData = filteredData.filter((user: any) => 
          user.role === searchForm.role
        )
      }
      
      // 前端筛选：状态
      if (searchForm.status !== null && searchForm.status !== undefined) {
        filteredData = filteredData.filter((user: any) => 
          user.status === searchForm.status
        )
      }
      
      userList.value = filteredData
      // 更新总数
      pagination.total = filteredData.length
    }
    
    console.log('用户列表加载完成')
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 编辑用户
const editUser = (row: any) => {
  // 填充表单数据
  editForm.id = row.id
  editForm.username = row.username
  editForm.nickname = row.nickname || ''
  editForm.email = row.email || ''
  editForm.phone = row.phone || ''
  editForm.userType = row.userType
  editForm.role = row.role
  editForm.travelPreference = row.travelPreference
  editForm.interestTags = row.interestTags || ''
  editForm.frequentCities = row.frequentCities || ''
  
  editDialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    await editFormRef.value.validate()
    
    editLoading.value = true
    
    const result = await request.put(`/admin/user/${editForm.id}`, editForm)
    
    if (result.code === 200) {
      ElMessage.success('用户信息更新成功')
      editDialogVisible.value = false
      // 刷新列表
      loadUserList()
    } else {
      ElMessage.error(result.message || '更新失败')
    }
  } catch (error: any) {
    if (error !== false) { // validate会返回false
      console.error('更新用户信息失败:', error)
      ElMessage.error('更新用户信息失败: ' + (error.message || '未知错误'))
    }
  } finally {
    editLoading.value = false
  }
}

// 切换状态
const toggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}用户 ${row.username} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === 1 ? 0 : 1
    const result = await request.put(`/admin/user/${row.id}/status`, null, {
      params: { status: newStatus }
    })
    
    if (result.code === 200) {
      row.status = newStatus
      ElMessage.success('状态更新成功')
      // 刷新列表和统计
      loadUserList()
      loadUserStats()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('更新用户状态失败:', error)
      ElMessage.error('更新用户状态失败')
    }
  }
}

// 删除用户
const deleteUser = async (row: any) => {
  // 检查用户状态：只有禁用状态的用户才能删除
  if (row.status === 1) {
    ElMessage.warning('只能删除禁用状态的用户，请先禁用该用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要永久删除用户 "${row.username}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    const result = await request.delete(`/admin/user/${row.id}`)
    
    if (result.code === 200) {
      ElMessage.success('用户已永久删除')
      // 刷新列表和统计
      loadUserList()
      loadUserStats()
    } else {
      ElMessage.error(result.message || '删除用户失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败: ' + (error.message || '未知错误'))
    }
  }
}

// 导出用户
const exportUsers = async () => {
  try {
    // 构建导出参数
    const params: any = {
      format: 'excel' // 默认导出Excel
    }
    
    // 添加筛选条件
    if (searchForm.username) {
      params.keyword = searchForm.username
    }
    if (searchForm.userType !== null && searchForm.userType !== undefined) {
      params.userType = searchForm.userType
    }
    if (searchForm.role !== null && searchForm.role !== undefined) {
      params.role = searchForm.role
    }
    if (searchForm.status !== null && searchForm.status !== undefined) {
      params.status = searchForm.status
    }
    
    // 打开新窗口下载
    const queryString = new URLSearchParams(params).toString()
    window.open(`/api/admin/user/export?${queryString}`, '_blank')
    
    ElMessage.success('导出任务已启动，请稍候...')
  } catch (error: any) {
    console.error('导出用户失败:', error)
    ElMessage.error('导出用户失败: ' + (error.message || '未知错误'))
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadUserList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadUserList()
}

// 选择改变
const handleSelectionChange = (selection: any[]) => {
  selectedUsers.value = selection
}

// 批量启用
const batchEnable = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要启用的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要启用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    
    const ids = selectedUsers.value.map(user => user.id)
    const result = await request.put('/admin/user/batch/status', ids, {
      params: { status: 1 }
    })
    
    if (result.code === 200) {
      ElMessage.success('批量启用成功')
      loadUserList()
      loadUserStats()
      clearSelection()
    } else {
      ElMessage.error(result.message || '批量启用失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量启用失败:', error)
      ElMessage.error('批量启用失败: ' + (error.message || '未知错误'))
    }
  }
}

// 批量禁用
const batchDisable = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要禁用的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    
    const ids = selectedUsers.value.map(user => user.id)
    const result = await request.put('/admin/user/batch/status', ids, {
      params: { status: 0 }
    })
    
    if (result.code === 200) {
      ElMessage.success('批量禁用成功')
      loadUserList()
      loadUserStats()
      clearSelection()
    } else {
      ElMessage.error(result.message || '批量禁用失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量禁用失败:', error)
      ElMessage.error('批量禁用失败: ' + (error.message || '未知错误'))
    }
  }
}

// 批量修改角色
const handleBatchRoleChange = async (role: string) => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要修改角色的用户')
    return
  }
  
  try {
    const roleName = role === '1' ? '管理员' : '普通用户'
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedUsers.value.length} 个用户设置为"${roleName}"吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    
    const ids = selectedUsers.value.map(user => user.id)
    const result = await request.put('/admin/user/batch/role', ids, {
      params: { role: parseInt(role) }
    })
    
    if (result.code === 200) {
      ElMessage.success(`批量修改角色成功，已设置为"${roleName}"`)
      loadUserList()
      clearSelection()
    } else {
      ElMessage.error(result.message || '批量修改角色失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量修改角色失败:', error)
      ElMessage.error('批量修改角色失败: ' + (error.message || '未知错误'))
    }
  }
}

// 批量删除
const batchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }
  
  // 检查是否所有用户都是禁用状态
  const activeUsers = selectedUsers.value.filter(user => user.status === 1)
  if (activeUsers.length > 0) {
    ElMessage.warning(`只能删除禁用状态的用户，其中 ${activeUsers.length} 个用户仍在启用状态`)
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要永久删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    // 提取用户ID列表
    const ids = selectedUsers.value.map(user => user.id)
    
    // 调用API批量删除用户
    const result = await request.delete('/admin/user/batch', {
      data: ids
    })
    
    if (result.code === 200) {
      ElMessage.success('批量删除成功')
      // 刷新列表和统计
      loadUserList()
      loadUserStats()
      clearSelection()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除用户失败:', error)
      ElMessage.error('批量删除用户失败: ' + (error.message || '未知错误'))
    }
  }
}

// 清除选择
const clearSelection = () => {
  selectedUsers.value = []
}

// 获取行类名
const getRowClassName = ({ row, rowIndex }: { row: any; rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 加载统计数据
const loadUserStats = async () => {
  try {
    console.log('开始加载用户统计数据...')
    const result = await request.get('/admin/user/stats')
    console.log('用户统计结果:', result)
    
    if (result.code === 200 && result.data) {
      userStats.value = {
        totalUsers: result.data.total || 0,
        activeUsers: result.data.active || 0,
        vipUsers: result.data.vipUsers || 0,
        todayNew: result.data.todayNew || 0
      }
    }
  } catch (error: any) {
    console.error('加载用户统计失败:', error)
  }
}

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新用户统计数据...')
    loadUserStats()
    lastUpdateTime.value = dayjs().format('HH:mm:ss')
  }, refreshInterval.value)
  console.log(`自动刷新已启动，间隔: ${refreshInterval.value / 1000}秒`)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
    autoRefreshTimer.value = null
    console.log('自动刷新已停止')
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string | null) => {
  if (!dateTime) return '-'
  
  try {
    const date = new Date(dateTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (e) {
    return dateTime
  }
}

onMounted(() => {
  loadUserList()
  loadUserStats()
  lastUpdateTime.value = dayjs().format('HH:mm:ss')
  startAutoRefresh()
})

// 查看用户详情
const viewUserDetail = async (row: any) => {
  currentUser.value = row
  detailDialogVisible.value = true
  detailLoading.value = true
  
  try {
    const result = await request.get(`/admin/user/${row.id}/detail`)
    if (result.code === 200) {
      userDetail.value = result.data || {}
    } else {
      ElMessage.error(result.message || '获取用户详情失败')
    }
  } catch (error: any) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败: ' + (error.message || '未知错误'))
  } finally {
    detailLoading.value = false
  }
}

// 处理用户操作
const handleUserAction = (command: string, row: any) => {
  currentUser.value = row
  
  switch (command) {
    case 'points':
      pointsDialogVisible.value = true
      pointsForm.points = 0
      pointsForm.reason = ''
      break
    case 'level':
      levelDialogVisible.value = true
      levelForm.level = row.level || 1
      levelForm.reason = ''
      break
    case 'loginHistory':
      loginHistoryDialogVisible.value = true
      loginHistoryPagination.current = 1
      loadLoginHistory()
      break
    case 'operationLogs':
      operationLogsDialogVisible.value = true
      operationLogsPagination.current = 1
      loadOperationLogs()
      break
  }
}

// 提交积分调整
const submitPointsAdjust = async () => {
  if (!pointsForm.points || pointsForm.points === 0) {
    ElMessage.warning('请输入积分变动值')
    return
  }
  
  pointsLoading.value = true
  try {
    const result = await request.put(`/admin/user/${currentUser.value.id}/points`, null, {
      params: {
        points: pointsForm.points,
        reason: pointsForm.reason || '管理员调整积分'
      }
    })
    
    if (result.code === 200) {
      ElMessage.success('积分调整成功')
      pointsDialogVisible.value = false
      loadUserList()
      loadUserStats()
    } else {
      ElMessage.error(result.message || '积分调整失败')
    }
  } catch (error: any) {
    console.error('积分调整失败:', error)
    ElMessage.error('积分调整失败: ' + (error.message || '未知错误'))
  } finally {
    pointsLoading.value = false
  }
}

// 提交等级调整
const submitLevelAdjust = async () => {
  if (!levelForm.level || levelForm.level < 1 || levelForm.level > 10) {
    ElMessage.warning('请输入有效的等级（1-10）')
    return
  }
  
  levelLoading.value = true
  try {
    const result = await request.put(`/admin/user/${currentUser.value.id}/level`, null, {
      params: {
        level: levelForm.level,
        reason: levelForm.reason || '管理员调整等级'
      }
    })
    
    if (result.code === 200) {
      ElMessage.success('等级调整成功')
      levelDialogVisible.value = false
      loadUserList()
    } else {
      ElMessage.error(result.message || '等级调整失败')
    }
  } catch (error: any) {
    console.error('等级调整失败:', error)
    ElMessage.error('等级调整失败: ' + (error.message || '未知错误'))
  } finally {
    levelLoading.value = false
  }
}

// 加载登录历史
const loadLoginHistory = async () => {
  if (!currentUser.value) return
  
  loginHistoryLoading.value = true
  try {
    const result = await request.get(`/admin/user/${currentUser.value.id}/login-history`, {
      params: {
        page: loginHistoryPagination.current,
        size: loginHistoryPagination.size
      }
    })
    
    if (result.code === 200) {
      loginHistoryList.value = result.data || []
      loginHistoryPagination.total = result.data?.length || 0
    } else {
      ElMessage.error(result.message || '获取登录历史失败')
    }
  } catch (error: any) {
    console.error('获取登录历史失败:', error)
    ElMessage.error('获取登录历史失败: ' + (error.message || '未知错误'))
  } finally {
    loginHistoryLoading.value = false
  }
}

// 加载操作日志
const loadOperationLogs = async () => {
  if (!currentUser.value) return
  
  operationLogsLoading.value = true
  try {
    const result = await request.get(`/admin/user/${currentUser.value.id}/operation-logs`, {
      params: {
        page: operationLogsPagination.current,
        size: operationLogsPagination.size
      }
    })
    
    if (result.code === 200) {
      operationLogsList.value = result.data || []
      operationLogsPagination.total = result.data?.length || 0
    } else {
      ElMessage.error(result.message || '获取操作日志失败')
    }
  } catch (error: any) {
    console.error('获取操作日志失败:', error)
    ElMessage.error('获取操作日志失败: ' + (error.message || '未知错误'))
  } finally {
    operationLogsLoading.value = false
  }
}

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';

// 积分调整对话框样式 - 移除标题下方的线和按钮图标（非scoped，确保样式生效）
.points-adjust-dialog {
  // 移除标题下方的所有边框和装饰线
  .el-dialog__header {
    padding: 20px 20px 0 !important;
    border-bottom: none !important;
    border: none !important;
    border-bottom-width: 0 !important;
    margin-bottom: 0 !important;
    
    // 移除所有伪元素
    &::after {
      content: none !important;
      display: none !important;
      height: 0 !important;
      width: 0 !important;
      border: none !important;
      background: none !important;
    }
    
    &::before {
      content: none !important;
      display: none !important;
      height: 0 !important;
      width: 0 !important;
      border: none !important;
      background: none !important;
    }
    
    // 移除标题内的所有边框
    .el-dialog__title {
      border-bottom: none !important;
      padding-bottom: 0 !important;
      margin-bottom: 0 !important;
      
      &::after {
        display: none !important;
        content: none !important;
      }
    }
    
    // 移除 header 内的所有子元素的边框
    > * {
      border-bottom: none !important;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
  }
  
  // 移除按钮图标
  .el-dialog__footer {
    .el-button--primary {
      // 隐藏所有图标
      .el-icon,
      i,
      svg {
        display: none !important;
        visibility: hidden !important;
        width: 0 !important;
        height: 0 !important;
        margin: 0 !important;
        padding: 0 !important;
      }
      
      // 移除按钮内的所有图标元素
      > .el-icon,
      span .el-icon,
      i.el-icon,
      .el-button__icon {
        display: none !important;
        visibility: hidden !important;
        width: 0 !important;
        height: 0 !important;
      }
      
      // 确保按钮文字正常显示
      span:not(.el-icon):not(.el-button__icon) {
        display: inline-block !important;
      }
    }
  }
}

// 全局覆盖 - 确保积分调整对话框没有标题下方的线
.el-overlay .points-adjust-dialog .el-dialog .el-dialog__header {
  border-bottom: none !important;
  border-bottom-width: 0 !important;
  border-bottom-style: none !important;
  padding-bottom: 0 !important;
  margin-bottom: 0 !important;
  
  &::after,
  &::before {
    display: none !important;
    content: none !important;
    height: 0 !important;
    width: 0 !important;
    border: none !important;
    background: none !important;
  }
  
  .el-dialog__title {
    border-bottom: none !important;
  }
}
</style>

<style lang="scss" scoped>
.form-tip {
  color: #999;
  font-size: 12px;
  margin-left: 8px;
}

// 编辑对话框美化样式
:deep(.edit-user-dialog) {
  .el-dialog {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
      padding: 24px 24px 0;
      border-bottom: none !important;
      
      &::after {
        display: none !important;
      }
      
      &::before {
        display: none !important;
      }
      
      .el-dialog__title {
        border-bottom: none !important;
        padding-bottom: 0 !important;
        margin-bottom: 0 !important;
        
        &::after {
          display: none !important;
        }
        
        &::before {
          display: none !important;
        }
      }
    }
    
    .el-dialog__body {
      padding: 24px;
      max-height: 70vh;
      overflow-y: auto;
    }
    
    .el-dialog__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #f0f0f0;
      background: #fafafa;
    }
  }
}

.dialog-header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  margin-bottom: 20px;
}


.edit-form {
  .el-form-item {
    margin-bottom: 22px;
    
    .el-form-item__label {
      font-weight: 500;
      color: #606266;
    }
    
    .el-input,
    .el-select,
    .el-textarea {
      .el-input__wrapper,
      .el-select__wrapper {
        border-radius: 8px;
        transition: all 0.3s;
        
        &:hover {
          box-shadow: 0 0 0 1px #c0c4cc inset;
        }
      }
    }
  }
}

.edit-user-dialog {
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .simple-btn {
      padding: 8px 16px !important;
      border-radius: 4px !important;
      font-weight: 400 !important;
      font-size: 14px !important;
      border: 1px solid #dcdfe6 !important;
      background: #fff !important;
      color: #606266 !important;
      box-shadow: none !important;
      min-width: auto !important;
      height: auto !important;
      
      &.el-button--primary {
        background: #fff !important;
        border: 1px solid #dcdfe6 !important;
        color: #606266 !important;
      }
      
      .el-icon {
        display: none !important;
      }
      
      &:hover {
        background: #f5f7fa !important;
        border-color: #c0c4cc !important;
        transform: none !important;
        box-shadow: none !important;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .el-button {
    padding: 12px 32px;
    border-radius: 8px;
    font-weight: 500;
    
    &.el-button--primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      
      &:hover {
        background: linear-gradient(135deg, #5568d3 0%, #6a4192 100%);
      }
    }
  }
}

// 用户详情对话框样式
:deep(.modern-detail-dialog) {
  .el-dialog__body {
    max-height: 70vh;
    overflow-y: auto;
  }
}

.user-detail-content {
  .detail-section {
    margin-bottom: 24px;
    
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid #f0f0f0;
    }
  }
}
</style>


