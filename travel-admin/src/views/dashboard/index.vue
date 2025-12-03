<template>
  <div class="dashboard">
    <!-- 测试卡片 - 验证样式是否生效 -->
    <div style="width: 300px; height: 120px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 20px; margin: 20px; padding: 20px; color: white; font-size: 24px; font-weight: bold; box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);">
      如果你看到这个紫色卡片，说明样式可以生效！
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card" style="border-radius: 20px !important; background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%) !important;">
          <div class="stats-content" style="display: flex; align-items: center; padding: 20px;">
            <div class="stats-icon user-icon" style="width: 80px; height: 80px; border-radius: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; margin-right: 20px; font-size: 40px; color: #fff; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info" style="flex: 1;">
              <div class="stats-number" style="font-size: 42px; font-weight: 800; background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));">{{ stats.totalUsers }}</div>
              <div class="stats-label" style="font-size: 16px; color: #606266; font-weight: 600;">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" style="border-radius: 20px !important; background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%) !important; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;">
          <div class="stats-content" style="display: flex; align-items: center; padding: 20px;">
            <div class="stats-icon plan-icon" style="width: 80px; height: 80px; border-radius: 20px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); display: flex; align-items: center; justify-content: center; margin-right: 20px; font-size: 40px; color: #fff; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stats-info" style="flex: 1;">
              <div class="stats-number" style="font-size: 42px; font-weight: 800; background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));">{{ stats.totalPlans }}</div>
              <div class="stats-label" style="font-size: 16px; color: #606266; font-weight: 600;">攻略总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" style="border-radius: 20px !important; background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%) !important; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;">
          <div class="stats-content" style="display: flex; align-items: center; padding: 20px;">
            <div class="stats-icon attraction-icon" style="width: 80px; height: 80px; border-radius: 20px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); display: flex; align-items: center; justify-content: center; margin-right: 20px; font-size: 40px; color: #fff; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stats-info" style="flex: 1;">
              <div class="stats-number" style="font-size: 42px; font-weight: 800; background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));">{{ stats.totalAttractions }}</div>
              <div class="stats-label" style="font-size: 16px; color: #606266; font-weight: 600;">景点总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" style="border-radius: 20px !important; background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%) !important; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;">
          <div class="stats-content" style="display: flex; align-items: center; padding: 20px;">
            <div class="stats-icon ai-icon" style="width: 80px; height: 80px; border-radius: 20px; background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); display: flex; align-items: center; justify-content: center; margin-right: 20px; font-size: 40px; color: #fff; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);">
              <el-icon><MagicStick /></el-icon>
            </div>
            <div class="stats-info" style="flex: 1;">
              <div class="stats-number" style="font-size: 42px; font-weight: 800; background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));">{{ stats.aiGenerations }}</div>
              <div class="stats-label" style="font-size: 16px; color: #606266; font-weight: 600;">AI生成次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <v-chart :option="userGrowthOption" style="height: 300px;" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>攻略类型分布</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <v-chart :option="planTypeOption" style="height: 300px;" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新攻略</span>
              <el-button type="text">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div 
              v-for="plan in latestPlans" 
              :key="plan.id" 
              class="activity-item"
            >
              <el-avatar :src="plan.userAvatar" />
              <div class="activity-content">
                <div class="activity-title">{{ plan.title }}</div>
                <div class="activity-meta">
                  <span>{{ plan.userName }}</span>
                  <span>{{ formatDateTime(plan.createTime) }}</span>
                </div>
              </div>
              <el-tag :type="getPlanTypeTag(plan.type)">{{ plan.typeName }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门景点</span>
              <el-button type="text">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div 
              v-for="attraction in hotAttractions" 
              :key="attraction.id" 
              class="activity-item"
            >
              <el-image 
                :src="attraction.image" 
                class="attraction-image"
                fit="cover"
              />
              <div class="activity-content">
                <div class="activity-title">{{ attraction.name }}</div>
                <div class="activity-meta">
                  <span>{{ attraction.city }}</span>
                  <span>{{ attraction.viewCount }}次浏览</span>
                </div>
              </div>
              <el-rate 
                v-model="attraction.rating" 
                disabled 
                show-score 
                text-color="#ff9900"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { formatDateTime } from '@/utils'
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
  totalUsers: 0,
  totalPlans: 0,
  totalAttractions: 0,
  aiGenerations: 0
})

// 用户增长趋势图表配置
const userGrowthOption = ref({
  title: {
    text: '近7天用户增长'
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

// 攻略类型分布图表配置
const planTypeOption = ref({
  title: {
    text: '攻略类型分布'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [{
    name: '攻略类型',
    type: 'pie',
    radius: '50%',
    data: [
      { value: 1048, name: '国内游' },
      { value: 735, name: '国外游' },
      { value: 580, name: '游轮游' },
      { value: 484, name: '节假日游' },
      { value: 300, name: '非节假日游' }
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

// 最新攻略数据
const latestPlans = ref([
  {
    id: 1,
    title: '重庆三日游攻略',
    userName: '张三',
    userAvatar: '',
    createTime: '2小时前',
    type: 1,
    typeName: '国内游'
  },
  {
    id: 2,
    title: '日本樱花季攻略',
    userName: '李四',
    userAvatar: '',
    createTime: '4小时前',
    type: 2,
    typeName: '国外游'
  },
  {
    id: 3,
    title: '三亚海岛游攻略',
    userName: '王五',
    userAvatar: '',
    createTime: '6小时前',
    type: 1,
    typeName: '国内游'
  }
])

// 热门景点数据
const hotAttractions = ref([
  {
    id: 1,
    name: '洪崖洞',
    city: '重庆',
    image: '',
    viewCount: 12580,
    rating: 4.5
  },
  {
    id: 2,
    name: '解放碑',
    city: '重庆',
    image: '',
    viewCount: 9680,
    rating: 4.3
  },
  {
    id: 3,
    name: '磁器口古镇',
    city: '重庆',
    image: '',
    viewCount: 8560,
    rating: 4.2
  }
])

// 获取攻略类型标签
const getPlanTypeTag = (type: number) => {
  const typeMap = {
    1: 'success',
    2: 'warning',
    3: 'info',
    4: 'danger',
    5: ''
  }
  return typeMap[type] || ''
}

// 加载数据
const loadData = async () => {
  try {
    console.log('开始加载Dashboard数据...')
    const result = await request.get('/statistics/overview')
    console.log('API返回结果:', result)
    if (result.code === 200 && result.data) {
      console.log('数据加载成功:', result.data)
      stats.value = {
        totalUsers: result.data.totalUsers || 0,
        totalPlans: result.data.totalPlans || 0,
        totalAttractions: result.data.totalAttractions || 0,
        aiGenerations: result.data.aiGenerations || 0
      }
      console.log('更新后的统计数据:', stats.value)
    } else {
      console.error('API返回失败:', result)
      ElMessage.error('获取统计数据失败')
    }
  } catch (error: any) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载仪表盘数据失败: ' + error.message)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.dashboard {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  min-height: calc(100vh - 60px);
  padding: 24px;
  
  // 统一的卡片样式
  :deep(.el-card) {
    border-radius: 20px;
    border: none;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: #fff;
    position: relative;
    overflow: hidden;
    
    // 添加卡片顶部渐变边框
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
    
    &:hover {
      box-shadow: 0 12px 40px rgba(102, 126, 234, 0.15);
      transform: translateY(-4px);
    }
  }
  
  @keyframes gradientFlow {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
  
  .stats-row {
    margin-bottom: 24px;
    
    .stats-card {
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
      position: relative;
      overflow: hidden;
      
      // 添加背景光晕效果
      &::after {
        content: '';
        position: absolute;
        top: -50%;
        right: -50%;
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba(102, 126, 234, 0.1) 0%, transparent 70%);
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &:hover {
        transform: translateY(-8px) scale(1.02);
        box-shadow: 0 16px 48px rgba(102, 126, 234, 0.2);
        
        &::after {
          opacity: 1;
        }
      }
      
      .stats-content {
        display: flex;
        align-items: center;
        padding: 20px;
        position: relative;
        z-index: 1;
        
        .stats-icon {
          width: 80px;
          height: 80px;
          border-radius: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 20px;
          font-size: 40px;
          color: #fff;
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          overflow: hidden;
          
          // 添加图标背景动画
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), transparent);
            opacity: 0;
            transition: opacity 0.3s ease;
          }
          
          &.user-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }
          
          &.plan-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }
          
          &.attraction-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }
          
          &.ai-icon {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          }
        }
        
        .stats-info {
          flex: 1;
          
          .stats-number {
            font-size: 42px;
            font-weight: 800;
            line-height: 1;
            margin-bottom: 10px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
            background-size: 200% 200%;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            animation: gradientShift 3s ease infinite;
            filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));
          }
          
          .stats-label {
            font-size: 16px;
            color: #606266;
            font-weight: 600;
            letter-spacing: 0.5px;
          }
        }
      }
      
      &:hover .stats-icon {
        transform: scale(1.15) rotate(5deg);
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
        
        &::before {
          opacity: 1;
        }
      }
    }
    
    @keyframes gradientShift {
      0% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
      100% { background-position: 0% 50%; }
    }
  }
  
  .chart-row {
    margin-bottom: 24px;
    
    :deep(.el-card__header) {
      padding: 24px 28px;
      border-bottom: 2px solid rgba(102, 126, 234, 0.1);
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.03), rgba(118, 75, 162, 0.03));
    }
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 700;
      font-size: 20px;
      color: #303133;
      letter-spacing: 0.5px;
      
      .el-button {
        color: #667eea;
        font-weight: 600;
        padding: 8px 16px;
        border-radius: 8px;
        transition: all 0.2s ease;
        
        &:hover {
          color: #764ba2;
          background: rgba(102, 126, 234, 0.1);
        }
      }
    }
  }
  
  .activity-row {
    :deep(.el-card__header) {
      padding: 24px 28px;
      border-bottom: 2px solid rgba(102, 126, 234, 0.1);
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.03), rgba(118, 75, 162, 0.03));
    }
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 700;
      font-size: 20px;
      color: #303133;
      letter-spacing: 0.5px;
      
      .el-button {
        color: #667eea;
        font-weight: 600;
        padding: 8px 16px;
        border-radius: 8px;
        transition: all 0.2s ease;
        
        &:hover {
          color: #764ba2;
          background: rgba(102, 126, 234, 0.1);
        }
      }
    }
    
    .activity-list {
      padding: 12px 0;
      
      .activity-item {
        display: flex;
        align-items: center;
        padding: 18px 20px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
        transition: all 0.2s ease;
        border-radius: 12px;
        margin: 4px 8px;
        
        &:last-child {
          border-bottom: none;
        }
        
        &:hover {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
          transform: translateX(8px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
        }
        
        .el-avatar {
          width: 52px;
          height: 52px;
          margin-right: 18px;
          border: 3px solid rgba(102, 126, 234, 0.2);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }
        
        .attraction-image {
          width: 60px;
          height: 60px;
          border-radius: 14px;
          margin-right: 18px;
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
        }
        
        .activity-content {
          flex: 1;
          
          .activity-title {
            font-size: 17px;
            font-weight: 700;
            color: #303133;
            margin-bottom: 8px;
            letter-spacing: 0.3px;
          }
          
          .activity-meta {
            font-size: 14px;
            color: #909399;
            font-weight: 500;
            
            span {
              margin-right: 20px;
            }
          }
        }
        
        .el-tag {
          border-radius: 20px;
          padding: 6px 18px;
          font-weight: 600;
          font-size: 13px;
          border: none;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .el-rate {
          font-size: 18px;
        }
      }
    }
  }
}
</style>

<style lang="scss">
/* 全局样式 - 强制覆盖Element Plus默认样式 */
.dashboard {
  .el-card {
    border-radius: 20px !important;
    border: none !important;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    background: #fff !important;
    position: relative !important;
    overflow: hidden !important;
    
    &::before {
      content: '' !important;
      position: absolute !important;
      top: 0 !important;
      left: 0 !important;
      right: 0 !important;
      height: 4px !important;
      background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%) !important;
      background-size: 200% 100% !important;
      animation: gradientFlow 3s ease infinite !important;
      z-index: 10 !important;
    }
    
    &:hover {
      box-shadow: 0 12px 40px rgba(102, 126, 234, 0.15) !important;
      transform: translateY(-4px) !important;
    }
  }
  
  @keyframes gradientFlow {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
  
  .stats-card {
    cursor: pointer !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%) !important;
    position: relative !important;
    overflow: hidden !important;
    
    &::after {
      content: '' !important;
      position: absolute !important;
      top: -50% !important;
      right: -50% !important;
      width: 200% !important;
      height: 200% !important;
      background: radial-gradient(circle, rgba(102, 126, 234, 0.1) 0%, transparent 70%) !important;
      opacity: 0 !important;
      transition: opacity 0.3s ease !important;
    }
    
    &:hover {
      transform: translateY(-8px) scale(1.02) !important;
      box-shadow: 0 16px 48px rgba(102, 126, 234, 0.2) !important;
      
      &::after {
        opacity: 1 !important;
      }
    }
    
    .stats-content {
      display: flex !important;
      align-items: center !important;
      padding: 20px !important;
      position: relative !important;
      z-index: 1 !important;
      
      .stats-icon {
        width: 80px !important;
        height: 80px !important;
        border-radius: 20px !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        margin-right: 20px !important;
        font-size: 40px !important;
        color: #fff !important;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2) !important;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
        position: relative !important;
        overflow: hidden !important;
        
        &.user-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
        }
        
        &.plan-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
        }
        
        &.attraction-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%) !important;
        }
        
        &.ai-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%) !important;
        }
      }
      
      .stats-info {
        flex: 1 !important;
        
        .stats-number {
          font-size: 42px !important;
          font-weight: 800 !important;
          line-height: 1 !important;
          margin-bottom: 10px !important;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%) !important;
          background-size: 200% 200% !important;
          -webkit-background-clip: text !important;
          -webkit-text-fill-color: transparent !important;
          background-clip: text !important;
          animation: gradientShift 3s ease infinite !important;
          filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2)) !important;
        }
        
        .stats-label {
          font-size: 16px !important;
          color: #606266 !important;
          font-weight: 600 !important;
          letter-spacing: 0.5px !important;
        }
      }
    }
    
    &:hover .stats-icon {
      transform: scale(1.15) rotate(5deg) !important;
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3) !important;
    }
  }
  
  @keyframes gradientShift {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
}
</style>








