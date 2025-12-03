<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><MagicStick /></el-icon>
        </div>
        <div class="header-title">
          <h1>AI管理</h1>
          <p>管理AI配置与日志</p>
          <div class="status-info">
            <el-icon class="status-icon"><Refresh /></el-icon>
            <span class="status-text">数据每30秒自动刷新</span>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="success" class="action-btn" @click="exportAILogs">
          <el-icon><Download /></el-icon>
          导出日志
        </el-button>
        <el-button type="primary" class="action-btn" @click="testAI">
          <el-icon><MagicStick /></el-icon>
          测试AI
        </el-button>
      </div>
    </div>

    <!-- AI状态概览 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><MagicStick /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总请求数</div>
          <div class="stat-value">{{ aiStats.totalRequests }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">成功率</div>
          <div class="stat-value">{{ aiStats.successRate }}%</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">平均响应时间</div>
          <div class="stat-value">{{ aiStats.avgResponseTime }}ms</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">活跃用户</div>
          <div class="stat-value">{{ aiStats.activeUsers }}</div>
        </div>
      </div>
    </div>

    <!-- AI配置 -->
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>AI配置</span>
          <el-button type="primary" @click="saveConfig">保存配置</el-button>
        </div>
      </template>
      
      <el-form :model="aiConfig" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模型名称">
              <el-input v-model="aiConfig.modelName" placeholder="请输入模型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API地址">
              <el-input v-model="aiConfig.apiUrl" placeholder="请输入API地址" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="API密钥">
              <el-input v-model="aiConfig.apiKey" type="password" placeholder="请输入API密钥" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大Token">
              <el-input-number v-model="aiConfig.maxTokens" :min="100" :max="4000" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="温度参数">
              <el-slider v-model="aiConfig.temperature" :min="0" :max="2" :step="0.1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用状态">
              <el-switch v-model="aiConfig.enabled" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="系统提示词">
          <el-input 
            v-model="aiConfig.systemPrompt" 
            type="textarea" 
            :rows="4"
            placeholder="请输入系统提示词"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedLogs.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedLogs.length }}</strong> 条日志</span>
          </div>
          <div class="batch-buttons">
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

    <!-- AI日志 -->
    <el-card class="table-card-modern" shadow="never">
      <template #header>
        <div class="card-header-modern">
          <span>AI日志</span>
        </div>
      </template>
      
      <div class="table-wrapper">
        <el-table 
          :data="aiLogs" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="userId" label="用户ID" width="80" align="center" />
          <el-table-column prop="request" label="请求内容" width="200" show-overflow-tooltip />
          <el-table-column prop="response" label="响应内容" width="200" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="responseTime" label="响应时间" width="100" align="center">
            <template #default="{ row }">
              {{ row.responseTime }}ms
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" size="small" text @click="viewLog(row)">
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button type="danger" size="small" text @click="deleteLog(row)">
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
          {{ pagination.current }} / {{ Math.max(1, Math.ceil((pagination.total || 1) / (pagination.size || 10))) }}
        </span>
        <el-button
          class="page-btn"
          :disabled="pagination.current >= Math.ceil((pagination.total || 1) / (pagination.size || 10))"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import BackButton from '@/components/BackButton.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  View, Delete, MagicStick, Download, Check, Clock, User, 
  Select, CloseBold
} from '@element-plus/icons-vue'
import { getAdminAiLogList, getAdminAiStatistics, deleteAiLog, generateContent } from '@/api/ai'
import { getAiConfig, updateAiConfig } from '@/api/systemConfig'

// AI统计信息
const aiStats = ref({
  totalRequests: 0,
  successRate: 0,
  avgResponseTime: 0,
  activeUsers: 0
})

// 自动刷新定时器
let refreshTimer: any = null

// AI配置
const aiConfig = reactive({
  modelName: 'gpt-3.5-turbo',
  apiUrl: 'https://api.openai.com/v1/chat/completions',
  apiKey: '',
  maxTokens: 2000,
  temperature: 0.7,
  enabled: true,
  systemPrompt: '你是一个专业的旅游攻略生成助手，请根据用户的需求生成详细的旅游攻略。'
})

// 加载AI配置
const loadAIConfig = async () => {
  try {
    const response = await getAiConfig()
    if (response.code === 200 && response.data) {
      Object.assign(aiConfig, response.data)
    }
  } catch (error) {
    console.error('加载AI配置失败:', error)
  }
}

// 自动刷新
const lastUpdateTime = ref<string>('')

// 格式化当前时间
const formatCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

// AI日志
interface AiLogItem {
  id: number
  userId: number
  request: string
  response: string
  status: number
  responseTime: number
  createTime: string
}

const aiLogs = ref<AiLogItem[]>([])
const loading = ref(false)

// 选中的日志
const selectedLogs = ref<AiLogItem[]>([])

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

// 获取状态名称
const getStatusName = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '失败',
    1: '成功',
    2: '处理中'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签
const getStatusTag = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const tagMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined> = {
    0: 'danger',
    1: 'success',
    2: 'warning'
  }
  return tagMap[status] || undefined
}

// 保存配置
const saveConfig = async () => {
  try {
    const response = await updateAiConfig(aiConfig)
    if (response.code === 200) {
      ElMessage.success('配置保存成功')
      // 重新加载配置
      loadAIConfig()
    } else {
      ElMessage.error(response.message || '配置保存失败')
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('配置保存失败')
  }
}

// 测试AI
const testAI = async () => {
  try {
    ElMessage.info('正在测试AI...')
    
    // 调用AI生成功能进行测试
    const testPrompt = '请简单介绍一下重庆这个城市，约50字左右。'
    
    // 获取当前用户ID（使用管理员ID 1）
    const userId = 1
    
    // 调用真实AI API
    const response = await generateContent(userId, testPrompt, 'gpt-3.5-turbo')
    
    if (response && response.data && response.data.length > 0) {
      ElMessage.success({
        message: 'AI测试成功！\n回复：' + response.data.substring(0, 100) + '...',
        duration: 5000
      })
    } else {
      ElMessage.warning('AI测试完成，但未返回有效内容')
    }
  } catch (error) {
    console.error('AI测试失败:', error)
    ElMessage.error('AI测试失败：无法连接到AI服务，请检查配置')
  }
}

// 导出日志
const exportAILogs = () => {
  try {
    if (aiLogs.value.length === 0) {
      ElMessage.warning('没有可导出的日志数据')
      return
    }
    
    // 构建CSV内容
    const headers = ['ID', '用户ID', '请求内容', '响应内容', '状态', '响应时间(ms)', '创建时间']
    const csvRows = [headers.join(',')]
    
    aiLogs.value.forEach((log: any) => {
      const row = [
        log.id || '',
        log.userId || '',
        `"${(log.request || '').replace(/"/g, '""')}"`,
        `"${(log.response || '').replace(/"/g, '""')}"`,
        getStatusName(log.status),
        log.responseTime || 0,
        log.createTime || ''
      ]
      csvRows.push(row.join(','))
    })
    
    // 创建下载链接
    const csvContent = csvRows.join('\n')
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    link.setAttribute('href', url)
    link.setAttribute('download', `AI日志_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`成功导出 ${aiLogs.value.length} 条日志`)
  } catch (error) {
    ElMessage.error('导出日志失败：' + (error as Error).message)
  }
}

// 查看日志
const viewLog = (row: any) => {
  // 构建显示内容
  const content = `
    <div style="text-align: left; max-width: 800px;">
      <div style="margin-bottom: 20px;">
        <h3 style="color: #409EFF; margin-bottom: 10px;">📝 请求内容</h3>
        <div style="background: #f5f7fa; padding: 15px; border-radius: 5px; white-space: pre-wrap; word-wrap: break-word;">
          ${row.request || '无请求内容'}
        </div>
      </div>
      
      <div style="margin-bottom: 20px;">
        <h3 style="color: #67C23A; margin-bottom: 10px;">🤖 AI响应</h3>
        <div style="background: #f0f9ff; padding: 15px; border-radius: 5px; white-space: pre-wrap; word-wrap: break-word;">
          ${row.response || '无响应内容'}
        </div>
      </div>
      
      <div style="margin-top: 20px; padding-top: 15px; border-top: 1px solid #e4e7ed;">
        <div style="display: flex; justify-content: space-between; flex-wrap: wrap; gap: 10px;">
          <div><strong>请求ID:</strong> ${row.id}</div>
          <div><strong>用户ID:</strong> ${row.userId}</div>
          <div><strong>状态:</strong> <span style="color: ${row.status === 1 ? '#67C23A' : '#F56C6C'}">${getStatusName(row.status)}</span></div>
          <div><strong>响应时间:</strong> ${row.responseTime}ms</div>
          <div><strong>创建时间:</strong> ${row.createTime}</div>
        </div>
      </div>
    </div>
  `
  
  ElMessageBox.alert(content, 'AI日志详情', {
    confirmButtonText: '关闭',
    dangerouslyUseHTMLString: true,
    customClass: 'ai-log-detail-dialog'
  })
}

// 删除日志
const deleteLog = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这条日志吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteAiLog(row.id)
    if (response.code === 200) {
      ElMessage.success('日志删除成功')
      // 重新加载日志
      loadAILogs()
      // 重新加载统计
      loadAIStats()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 加载AI统计
const loadAIStats = async () => {
  try {
    const response = await getAdminAiStatistics()
    if (response.code === 200 && response.data) {
      const data = response.data
      aiStats.value = {
        totalRequests: data.totalRequests || 0,
        successRate: data.successRate || 0,
        avgResponseTime: data.avgResponseTime || 0,
        activeUsers: data.activeUsers || 0
      }
    }
  } catch (error) {
    console.error('加载AI统计失败:', error)
  }
}

// 加载AI日志
const loadAILogs = async () => {
  loading.value = true
  try {
    const response = await getAdminAiLogList({
      page: pagination.current,
      size: pagination.size
    })
    
    if (response.code === 200 && response.data) {
      const data = response.data
      // 转换数据格式 - MyBatis Plus会自动处理驼峰和下划线转换
      aiLogs.value = (data.list || []).map((log: any) => ({
        id: log.id,
        userId: log.userId || log.user_id,
        request: log.requestContent || log.request_content || '',
        response: log.responseContent || log.response_content || '',
        status: log.status || 0,
        responseTime: log.responseTime || log.response_time || 0,
        createTime: log.createTime || log.create_time || ''
      }))
      
      pagination.total = data.total || 0
    }
  } catch (error) {
    console.error('加载AI日志失败:', error)
    ElMessage.error('加载AI日志失败')
  } finally {
    loading.value = false
  }
}

// 选择改变
const handleSelectionChange = (selection: any[]) => {
  selectedLogs.value = selection
}

// 批量删除
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的日志吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    
    // 逐个删除选中的日志
    let successCount = 0
    for (const log of selectedLogs.value) {
      try {
        const response = await deleteAiLog(log.id)
        if (response.code === 200) {
          successCount++
        }
      } catch (error) {
        console.error('删除日志失败:', error)
      }
    }
    
    if (successCount > 0) {
      ElMessage.success(`成功删除 ${successCount} 条日志`)
      clearSelection()
      // 重新加载日志
      loadAILogs()
      // 重新加载统计
      loadAIStats()
    } else {
      ElMessage.error('批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 清除选择
const clearSelection = () => {
  selectedLogs.value = []
}

// 获取行类名
const getRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadAILogs()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadAILogs()
}

// 刷新所有数据
const refreshAll = () => {
  loadAIStats()
  loadAILogs()
  // 更新最后刷新时间
  lastUpdateTime.value = formatCurrentTime()
}

onMounted(() => {
  loadAIConfig()  // 加载AI配置
  refreshAll()    // 刷新统计数据
  
  // 设置自动刷新，每30秒刷新一次
  refreshTimer = setInterval(() => {
    refreshAll()
  }, 30000)
})

onUnmounted(() => {
  // 清除定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style lang="scss" scoped>
// 使用全局样式 admin-list.scss

// AI配置卡片特殊样式
.config-card {
    margin-bottom: 24px;
    border-radius: 20px;
    border: none;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
      background-size: 200% 100%;
      animation: gradientFlow 3s ease infinite;
    }
    
    :deep(.el-card__header) {
      padding: 24px 28px;
      border-bottom: 2px solid rgba(102, 126, 234, 0.1);
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.03), rgba(118, 75, 162, 0.03));
    }
    
    :deep(.el-card__body) {
      padding: 28px;
    }
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 18px;
      font-weight: 700;
      color: #303133;
      letter-spacing: 0.5px;
      
      .el-button {
        border-radius: 10px;
        font-weight: 700;
        font-size: 15px;
        padding: 10px 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        transition: all 0.2s ease;
        color: #ffffff !important;
        
        &:hover {
          background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
          color: #ffffff !important;
        }
      }
    }
    
    :deep(.el-form-item__label) {
      font-weight: 600;
      color: #606266;
      font-size: 14px;
    }
    
    :deep(.el-input__wrapper) {
      border-radius: 10px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: all 0.2s ease;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
      }
      
      &.is-focus {
        box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
      }
    }
    
    :deep(.el-slider__runway) {
      background-color: #e4e7ed;
      border-radius: 4px;
    }
    
    :deep(.el-slider__bar) {
      background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
      border-radius: 4px;
    }
    
    :deep(.el-slider__button) {
      border: 2px solid #667eea;
      background: #fff;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      
      &:hover {
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      }
    }
    
    :deep(.el-switch) {
      .el-switch__core {
        background-color: #dcdfe6;
        
        &::after {
          background-color: #fff;
        }
      }
      
      &.is-checked .el-switch__core {
        background-color: #667eea;
        box-shadow: 0 0 0 1px #667eea;
      }
    }
    
    :deep(.el-textarea__inner) {
      border-radius: 10px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: all 0.2s ease;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
      }
      
      &:focus {
        box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
      }
    }
  }
  
  // 卡片头部样式
  .card-header-modern {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: 700;
    color: #303133;
    letter-spacing: 0.5px;
    
    :deep(.el-button--text) {
      color: #4c5ef5 !important;
      font-weight: 600;
      font-size: 14px;
      padding: 6px 12px;
      border-radius: 6px;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(102, 126, 234, 0.1);
        color: #3b4df2 !important;
      }
    }
  }
  
  @keyframes gradientFlow {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
</style>

<style lang="scss">
// AI日志详情弹窗样式
.ai-log-detail-dialog {
  .el-message-box__message {
    max-height: 70vh;
    overflow-y: auto;
    
    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 10px;
    }
    
    div[style*="background"] {
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      font-size: 14px;
      line-height: 1.6;
      max-height: 400px;
      overflow-y: auto;
    }
  }
  
  .el-message-box__title {
    font-size: 18px;
    font-weight: 700;
    color: #303133;
  }
  
  .el-message-box__btns {
    padding-top: 20px;
    
    .el-button {
      padding: 10px 30px;
      font-size: 14px;
      border-radius: 6px;
    }
  }
}

</style>









