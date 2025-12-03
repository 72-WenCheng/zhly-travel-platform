<template>
  <div class="user-management">
    <!-- 状态栏 -->
    <div class="status-bar">
      <div class="status-info">
        <el-icon class="status-icon"><Refresh /></el-icon>
        <span class="status-text">数据每30秒自动刷新</span>
        <span v-if="lastUpdateTime" class="update-time">最后更新: {{ lastUpdateTime }}</span>
      </div>
    </div>

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="请选择用户类型" clearable>
            <el-option label="个人" :value="1" />
            <el-option label="情侣" :value="2" />
            <el-option label="家庭" :value="3" />
            <el-option label="团队" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </template>

      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTag(row.userType)">
              {{ getUserTypeName(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="travelPreference" label="旅游偏好" width="100">
          <template #default="{ row }">
            <el-tag :type="getPreferenceTag(row.travelPreference)">
              {{ getPreferenceName(row.travelPreference) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handleView(row)">查看</el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ currentUser.gender === 1 ? '男' : currentUser.gender === 2 ? '女' : '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentUser.age }}</el-descriptions-item>
          <el-descriptions-item label="用户类型">{{ getUserTypeName(currentUser.userType) }}</el-descriptions-item>
          <el-descriptions-item label="旅游偏好">{{ getPreferenceName(currentUser.travelPreference) }}</el-descriptions-item>
          <el-descriptions-item label="兴趣爱好" :span="2">{{ currentUser.interestTags }}</el-descriptions-item>
          <el-descriptions-item label="常去城市" :span="2">{{ currentUser.frequentCities }}</el-descriptions-item>
          <el-descriptions-item label="最后登录时间">{{ currentUser.lastLoginTime }}</el-descriptions-item>
          <el-descriptions-item label="最后登录IP">{{ currentUser.lastLoginIp }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentUser.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentUser.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getUserList, getUserDetail, updateUserStatus, deleteUser } from '@/api/user'
import { formatDateTime } from '@/utils'

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref<NodeJS.Timeout | null>(null)
const lastUpdateTime = ref<string>('')

// 搜索表单
const searchForm = reactive({
  username: '',
  userType: null,
  status: null
})

// 用户列表
const userList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 用户详情
const detailVisible = ref(false)
const currentUser = ref(null)

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

// 获取旅游偏好名称
const getPreferenceName = (preference: number) => {
  const preferenceMap = {
    1: '穷游',
    2: '富游',
    3: '中等消费'
  }
  return preferenceMap[preference] || '未知'
}

// 获取旅游偏好标签
const getPreferenceTag = (preference: number) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return tagMap[preference] || ''
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
    userType: null,
    status: null
  })
  handleSearch()
}

// 格式化当前时间
const formatCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

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
    
    const result = await getUserList(params)
    console.log('用户列表结果:', result)
    
    if (result.code === 200 && result.data) {
      userList.value = result.data
      // 注意：如果API返回总数，使用API的总数；否则使用列表长度
      pagination.total = Array.isArray(result.data) ? result.data.length : 0
    }
    
    // 更新最后刷新时间
    lastUpdateTime.value = formatCurrentTime()
    console.log('用户列表加载完成，最后更新时间:', lastUpdateTime.value)
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 添加用户
const handleAdd = () => {
  ElMessage.info('添加用户功能开发中...')
}

// 编辑用户
const handleEdit = (row: any) => {
  ElMessage.info('编辑用户功能开发中...')
}

// 查看用户
const handleView = (row: any) => {
  currentUser.value = row
  detailVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row: any) => {
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
    const result = await updateUserStatus(row.id, newStatus)
    
    if (result.code === 200) {
      row.status = newStatus
      ElMessage.success('状态更新成功')
      // 刷新列表
      loadUserList()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('更新用户状态失败:', error)
      ElMessage.error('更新用户状态失败')
    }
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

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新用户列表...')
    loadUserList()
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

onMounted(() => {
  loadUserList()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss" scoped>
.user-management {
  // 状态栏样式
  .status-bar {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 16px 24px;
    margin-bottom: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.2);
    
    .status-info {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .status-icon {
        font-size: 18px;
        color: #667eea;
        animation: rotate 2s linear infinite;
      }
      
      .status-text {
        font-size: 14px;
        color: #606266;
        font-weight: 500;
      }
      
      .update-time {
        font-size: 13px;
        color: #909399;
        font-weight: 500;
      }
    }
  }
  
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  
  .user-detail {
    .el-descriptions {
      margin-top: 20px;
    }
  }
}
</style>











