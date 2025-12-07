<template>
  <div class="admin-statistics">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>数据统计</h2>
      <div class="header-actions">
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="success" @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 时间范围选择 -->
    <el-card class="filter-card">
      <el-form :model="timeRange" inline>
        <el-form-item label="统计时间">
          <el-date-picker
            v-model="timeRange.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleTimeRangeChange"
          />
        </el-form-item>
        <el-form-item label="统计类型">
          <el-select v-model="timeRange.type" placeholder="请选择统计类型" @change="handleTypeChange">
            <el-option label="日统计" value="day" />
            <el-option label="周统计" value="week" />
            <el-option label="月统计" value="month" />
            <el-option label="年统计" value="year" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadStatistics">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 核心指标 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ metrics.totalUsers }}</div>
              <div class="metric-label">总用户数</div>
              <div class="metric-change" :class="metrics.userChange >= 0 ? 'positive' : 'negative'">
                <el-icon><ArrowUp v-if="metrics.userChange >= 0" /><ArrowDown v-else /></el-icon>
                {{ Math.abs(metrics.userChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon plan-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ metrics.totalPlans }}</div>
              <div class="metric-label">总攻略数</div>
              <div class="metric-change" :class="metrics.planChange >= 0 ? 'positive' : 'negative'">
                <el-icon><ArrowUp v-if="metrics.planChange >= 0" /><ArrowDown v-else /></el-icon>
                {{ Math.abs(metrics.planChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon attraction-icon">
              <el-icon><Location /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ metrics.totalAttractions }}</div>
              <div class="metric-label">总景点数</div>
              <div class="metric-change" :class="metrics.attractionChange >= 0 ? 'positive' : 'negative'">
                <el-icon><ArrowUp v-if="metrics.attractionChange >= 0" /><ArrowDown v-else /></el-icon>
                {{ Math.abs(metrics.attractionChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon ai-icon">
              <el-icon><MagicStick /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ metrics.totalAI }}</div>
              <div class="metric-label">AI生成次数</div>
              <div class="metric-change" :class="metrics.aiChange >= 0 ? 'positive' : 'negative'">
                <el-icon><ArrowUp v-if="metrics.aiChange >= 0" /><ArrowDown v-else /></el-icon>
                {{ Math.abs(metrics.aiChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-button type="text" @click="refreshUserTrend">刷新</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="userTrendOption" style="height: 400px;" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>功能使用分布</span>
              <el-button type="text" @click="refreshFunctionUsage">刷新</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="functionUsageOption" style="height: 400px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>AI使用统计</span>
              <el-button type="text" @click="refreshAiStats">刷新</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="aiStatsOption" style="height: 400px;" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>地域分布</span>
              <el-button type="text" @click="refreshRegionStats">刷新</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="regionOption" style="height: 400px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>详细统计数据</span>
          <el-button type="primary" size="small" @click="exportDetailData">导出详细数据</el-button>
        </div>
      </template>
      <el-table :data="detailData" v-loading="loading" stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="newUsers" label="新增用户" width="100" />
        <el-table-column prop="newPlans" label="新增攻略" width="100" />
        <el-table-column prop="newAttractions" label="新增景点" width="100" />
        <el-table-column prop="aiRequests" label="AI请求数" width="100" />
        <el-table-column prop="pageViews" label="页面浏览量" width="120" />
        <el-table-column prop="activeUsers" label="活跃用户" width="100" />
        <el-table-column prop="conversionRate" label="转化率" width="100">
          <template #default="{ row }">
            {{ row.conversionRate }}%
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
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
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 时间范围
const timeRange = reactive({
  dateRange: null,
  type: 'day'
})

// 核心指标
const metrics = ref({
  totalUsers: 0,
  totalPlans: 0,
  totalAttractions: 0,
  totalAI: 0,
  userChange: 12.5,
  planChange: 8.3,
  attractionChange: 15.2,
  aiChange: 25.8
})

// 详细数据
const detailData = ref([])
const loading = ref(false)

// 用户增长趋势图表
const userTrendOption = ref({
  title: {
    text: '用户增长趋势'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    data: [120, 200, 150, 80, 70, 110, 130, 180, 220, 190, 250, 280],
    type: 'line',
    smooth: true,
    areaStyle: {}
  }]
})

// 功能使用分布图表
const functionUsageOption = ref({
  title: {
    text: '功能使用分布'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [{
    name: '功能使用',
    type: 'pie',
    radius: '50%',
    data: [
      { value: 35, name: 'AI生成' },
      { value: 25, name: '攻略浏览' },
      { value: 20, name: '景点查看' },
      { value: 15, name: '用户注册' },
      { value: 5, name: '其他' }
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

// AI使用统计图表
const aiStatsOption = ref({
  title: {
    text: 'AI使用统计'
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
    name: 'AI请求数',
    type: 'bar',
    data: [120, 200, 150, 80, 70, 110, 130]
  }]
})

// 地域分布图表
const regionOption = ref({
  title: {
    text: '用户地域分布'
  },
  tooltip: {
    trigger: 'item'
  },
  series: [{
    name: '地域分布',
    type: 'pie',
    radius: '50%',
    data: [
      { value: 25, name: '北京' },
      { value: 20, name: '上海' },
      { value: 15, name: '广州' },
      { value: 12, name: '深圳' },
      { value: 10, name: '杭州' },
      { value: 8, name: '成都' },
      { value: 10, name: '其他' }
    ]
  }]
})

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取统计数据
    console.log('加载统计数据')
    // 移除模拟数据，等待后端API实现
    detailData.value = []
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 时间范围改变
const handleTimeRangeChange = () => {
  loadStatistics()
}

// 统计类型改变
const handleTypeChange = () => {
  loadStatistics()
}

// 刷新数据
const refreshData = () => {
  loadStatistics()
}

// 导出数据
const exportData = () => {
  console.log('导出数据')
}

// 刷新图表
const refreshUserTrend = () => {
  console.log('刷新用户增长趋势')
}

const refreshFunctionUsage = () => {
  console.log('刷新功能使用分布')
}

const refreshAiStats = () => {
  console.log('刷新AI使用统计')
}

const refreshRegionStats = () => {
  console.log('刷新地域分布')
}

// 导出详细数据
const exportDetailData = () => {
  console.log('导出详细数据')
}

onMounted(() => {
  loadStatistics()
})
</script>

<style lang="scss" scoped>
.admin-statistics {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: #303133;
    }
  }

  .filter-card {
    margin-bottom: 20px;
  }

  .metrics-row {
    margin-bottom: 20px;
  }

  .metric-card {
    .metric-content {
      display: flex;
      align-items: center;
      
      .metric-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;
        color: #fff;
        
        &.user-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.plan-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        &.attraction-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
        
        &.ai-icon {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
      }
      
      .metric-info {
        flex: 1;
        
        .metric-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .metric-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 4px;
        }
        
        .metric-change {
          font-size: 12px;
          display: flex;
          align-items: center;
          
          &.positive {
            color: #67c23a;
          }
          
          &.negative {
            color: #f56c6c;
          }
        }
      }
    }
  }

  .charts-row {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: bold;
  }

  .chart-container {
    margin-top: 20px;
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>






