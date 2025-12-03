<template>
  <div class="ai-management">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon ai-icon">
              <el-icon><MagicStick /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.totalGenerations }}</div>
              <div class="stats-label">总生成次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon success-icon">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.successRate }}%</div>
              <div class="stats-label">成功率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon time-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.avgTime }}s</div>
              <div class="stats-label">平均耗时</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.activeUsers }}</div>
              <div class="stats-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>AI生成趋势</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="generationTrendOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>生成类型分布</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="typeDistributionOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>AI生成记录</span>
              <div>
                <el-button type="primary" @click="handleTestAI">测试AI</el-button>
                <el-button @click="handleRefresh">刷新</el-button>
              </div>
            </div>
          </template>

          <el-table :data="generationList" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="80" />
            <el-table-column prop="generateType" label="生成类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTypeTag(row.generateType)">
                  {{ getTypeName(row.generateType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="requestContent" label="请求内容" width="250" show-overflow-tooltip />
            <el-table-column prop="costTokens" label="消耗Token" width="100">
              <template #default="{ row }">
                {{ row.costTokens || 0 }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAiLogList, getAiStatistics, deleteAiLog, generateTravelPlan, aiChat } from '@/api/ai'
import type { AiGenerateLog } from '@/api/ai'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 统计数据
const stats = ref({
  totalGenerations: 0,
  successRate: 0,
  avgTime: 0,
  activeUsers: 0
})

// 生成记录列表
const generationList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// AI生成趋势图表配置
const generationTrendOption = ref({
  title: {
    text: '近7天AI生成趋势'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    data: [120, 200, 150, 80, 70, 110, 130],
    type: 'line',
    smooth: true,
    areaStyle: {}
  }]
})

// 生成类型分布图表配置
const typeDistributionOption = ref({
  title: {
    text: '生成类型分布'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [{
    name: '生成类型',
    type: 'pie',
    radius: '50%',
    data: [
      { value: 1048, name: '攻略生成' },
      { value: 735, name: '景点推荐' },
      { value: 580, name: '路线规划' },
      { value: 484, name: '智能问答' },
      { value: 300, name: '个性化推荐' }
    ],
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
})

// 获取生成类型名称
const getTypeName = (type?: number) => {
  if (type === undefined || type === null) return '未知'
  const typeMap: Record<number, string> = {
    0: '攻略生成',
    1: '智能问答',
    2: '景点推荐',
    3: '路线规划',
    4: '个性化推荐'
  }
  return typeMap[type] || '未知'
}

// 获取生成类型标签
const getTypeTag = (type?: number) => {
  if (type === undefined || type === null) return ''
  const tagMap: Record<number, string> = {
    0: 'success',
    1: 'info',
    2: 'warning',
    3: 'danger',
    4: ''
  }
  return tagMap[type] || ''
}

// 加载生成记录列表
const loadGenerationList = async () => {
  loading.value = true
  try {
    const result = await getAiLogList({
      page: pagination.current,
      size: pagination.size
    })
    
    if (result.code === 200 && result.data) {
      generationList.value = result.data.list || []
      pagination.total = result.data.total || 0
    } else {
      ElMessage.error(result.message || '加载生成记录失败')
    }
  } catch (error: any) {
    console.error('加载生成记录失败:', error)
    ElMessage.error(error.message || '加载生成记录失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const result = await getAiStatistics()
    if (result.code === 200 && result.data) {
      const data = result.data
      stats.value = {
        totalGenerations: data.totalRequests || 0,
        successRate: data.successRate || 0,
        avgTime: 0, // avgResponseTime not available in current schema
        activeUsers: data.activeUsers || 0
      }
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 测试AI
const handleTestAI = async () => {
  try {
    const { value: question } = await ElMessageBox.prompt('请输入测试问题', 'AI测试', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：介绍一下北京'
    })
    
    const loading = ElMessage.loading('AI正在思考中...', 0)
    const result = await aiChat(question)
    loading.close()
    
    if (result.code === 200) {
      ElMessage.success('AI回复成功')
      loadGenerationList()
    } else {
      ElMessage.error(result.message || 'AI回复失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('AI测试失败:', error)
      ElMessage.error(error.message || 'AI测试失败')
    }
  }
}

// 刷新
const handleRefresh = () => {
  loadGenerationList()
  loadStatistics()
}

// 查看记录
const handleView = (row: any) => {
  ElMessageBox.alert(row.responseContent || '暂无响应内容', 'AI生成详情', {
    confirmButtonText: '确定',
    dangerouslyUseHTMLString: true
  })
}

// 删除记录
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deleteAiLog(row.id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      loadGenerationList()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadGenerationList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadGenerationList()
}

onMounted(() => {
  loadGenerationList()
  loadStatistics()
})
</script>

<style lang="scss" scoped>
.ai-management {
  .stats-card {
    .stats-content {
      display: flex;
      align-items: center;
      
      .stats-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;
        color: #fff;
        
        &.ai-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.success-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        &.time-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
        
        &.user-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
      }
      
      .stats-info {
        .stats-number {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          line-height: 1;
        }
        
        .stats-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .chart-container {
    margin-top: 20px;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>









