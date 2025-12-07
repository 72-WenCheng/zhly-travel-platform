<template>
  <div class="user-portrait">
    <!-- 返回按钮 -->
    <BackButton />

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><DataAnalysis /></el-icon>
        </div>
        <div class="header-text">
          <h2>用户画像分析</h2>
          <p class="page-desc">基于您的浏览行为、搜索记录、收藏偏好等大数据智能分析</p>
        </div>
      </div>
      <div class="last-update">
        <el-icon><Clock /></el-icon>
        <span>上次更新：{{ lastUpdateTime }}</span>
      </div>
    </div>

    <!-- 核心画像数据 -->
    <el-row :gutter="20" class="portrait-stats">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon :size="32" style="color: #374151;"><Monitor /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ portraitData.browseCount }}</div>
            <div class="stat-label">浏览次数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon :size="32" style="color: #374151;"><StarFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ portraitData.favoriteCount }}</div>
            <div class="stat-label">收藏数量</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon :size="32" style="color: #374151;"><Search /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ portraitData.searchCount }}</div>
            <div class="stat-label">搜索次数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon :size="32" style="color: #374151;"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ portraitData.avgStayTime }}分钟</div>
            <div class="stat-label">平均停留</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- AI 分析结果 -->
    <el-row :gutter="20">
      <!-- 旅游偏好分析 -->
      <el-col :xs="24" :md="12">
        <el-card class="portrait-card">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><LocationFilled /></el-icon>
              <span>旅游偏好分析</span>
            </div>
          </template>
          
          <div class="preference-analysis">
            <div class="primary-preference">
              <div class="preference-label">主要偏好</div>
              <div class="preference-value">{{ portraitData.primaryPreference || '----' }}</div>
            </div>
            
            <div v-if="portraitData.preferenceDistribution && portraitData.preferenceDistribution.length > 0" class="preference-chart">
              <div 
                v-for="item in portraitData.preferenceDistribution" 
                :key="item.type"
                class="chart-item"
              >
                <div class="chart-label">
                  <span class="label-icon">{{ item.icon }}</span>
                  <span class="label-text">{{ item.type }}</span>
                </div>
                <div class="chart-bar-wrapper">
                  <div class="chart-bar" :style="{ width: item.percentage + '%', background: item.color }"></div>
                </div>
                <div class="chart-value">{{ item.percentage }}%</div>
              </div>
            </div>
            <div v-else class="preference-chart-empty">
              <span>----</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 兴趣标签云 -->
      <el-col :xs="24" :md="12">
        <el-card class="portrait-card">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><CollectionTag /></el-icon>
              <span>兴趣标签云</span>
            </div>
          </template>
          
          <div v-if="portraitData.interestTags && portraitData.interestTags.length > 0" class="interest-tags">
            <div 
              v-for="tag in portraitData.interestTags" 
              :key="tag.name"
              class="interest-tag"
              :class="getTagWeightClass(tag.weight)"
              :style="getTagStyle(tag.weight)"
            >
              <span class="tag-icon">{{ tag.icon }}</span>
              <span class="tag-name">{{ tag.name }}</span>
            </div>
          </div>
          <div v-else class="interest-tags-empty">
            <span>----</span>
          </div>
          
          <div v-if="portraitData.interestTags && portraitData.interestTags.length > 0" class="tag-legend">
            <span class="legend-item">
              <span class="legend-dot legend-high"></span>
              <span>兴趣度高</span>
            </span>
            <span class="legend-item">
              <span class="legend-dot legend-medium"></span>
              <span>兴趣度中</span>
            </span>
            <span class="legend-item">
              <span class="legend-dot legend-low"></span>
              <span>兴趣度低</span>
            </span>
          </div>
        </el-card>
      </el-col>

      <!-- 消费行为分析 -->
      <el-col :xs="24" :md="12">
        <el-card class="portrait-card">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Money /></el-icon>
              <span>消费行为分析</span>
            </div>
          </template>
          
          <div class="consumption-analysis">
            <div class="consumption-level">
              <div v-if="portraitData.consumptionLevel && portraitData.consumptionLevel.level" class="level-badge" :class="getConsumptionLevelClass(portraitData.consumptionLevel.level)">
                <span class="badge-icon">{{ portraitData.consumptionLevel.icon }}</span>
                <span class="badge-text">{{ portraitData.consumptionLevel.level }}</span>
              </div>
              <div v-else class="level-badge-empty">
                <span class="badge-text">----</span>
              </div>
            </div>
            
            <div class="consumption-stats">
              <div class="stat-row">
                <span class="stat-label">平均消费</span>
                <span class="stat-value">{{ portraitData.avgConsumption > 0 ? `¥${portraitData.avgConsumption}/天` : '----' }}</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">消费频次</span>
                <span class="stat-value">{{ portraitData.consumptionFrequency || '----' }}</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">价格敏感度</span>
                <span class="stat-value">{{ portraitData.priceSensitivity || '----' }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 出行特征 -->
      <el-col :xs="24" :md="12">
        <el-card class="portrait-card">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Promotion /></el-icon>
              <span>出行特征</span>
            </div>
          </template>
          
          <div class="travel-characteristics">
            <div class="characteristic-item">
              <div class="char-icon">🎒</div>
              <div class="char-content">
                <div class="char-label">出行方式</div>
                <div class="char-value">{{ portraitData.travelMode || '----' }}</div>
              </div>
            </div>
            
            <div class="characteristic-item">
              <div class="char-icon">📅</div>
              <div class="char-content">
                <div class="char-label">旅游时长偏好</div>
                <div class="char-value">{{ portraitData.tripDuration || '----' }}</div>
              </div>
            </div>
            
            <div class="characteristic-item">
              <div class="char-icon">🌸</div>
              <div class="char-content">
                <div class="char-label">季节偏好</div>
                <div class="char-value">{{ portraitData.seasonPreference || '----' }}</div>
              </div>
            </div>
            
            <div class="characteristic-item">
              <div class="char-icon">🏙️</div>
              <div class="char-content">
                <div class="char-label">常去目的地</div>
                <div class="char-value">{{ portraitData.frequentDestinations || '----' }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI 推荐说明 -->
    <el-card class="recommendation-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><MagicStick /></el-icon>
          <span>个性化推荐说明</span>
        </div>
      </template>
      
      <div class="recommendation-content">
        <div class="recommendation-item">
          <el-icon class="item-icon" color="#67C23A"><SuccessFilled /></el-icon>
          <div class="item-text">
            <strong>智能景点推荐：</strong>系统会根据您的用户画像，推荐符合您偏好的景点和旅游线路
          </div>
        </div>
        <div class="recommendation-item">
          <el-icon class="item-icon" color="#409EFF"><SuccessFilled /></el-icon>
          <div class="item-text">
            <strong>个性化内容：</strong>首页展示的内容会优先显示您感兴趣的类型
          </div>
        </div>
        <div class="recommendation-item">
          <el-icon class="item-icon" color="#E6A23C"><SuccessFilled /></el-icon>
          <div class="item-text">
            <strong>精准价格匹配：</strong>系统会根据您的消费水平，推荐相应价位的产品
          </div>
        </div>
        <div class="recommendation-item">
          <el-icon class="item-icon" color="#F56C6C"><SuccessFilled /></el-icon>
          <div class="item-text">
            <strong>持续学习优化：</strong>随着您的使用，系统会不断学习和优化推荐算法，让推荐更精准
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import BackButton from '@/components/BackButton.vue'
import { getUserPortrait } from '@/api/user'
import { ElMessage } from 'element-plus'
import { 
  DataAnalysis, Clock, Monitor, StarFilled, Search, Timer,
  LocationFilled, CollectionTag, Money, Promotion, MagicStick,
  SuccessFilled
} from '@element-plus/icons-vue'

// 用户画像数据
const portraitData = ref({
  browseCount: 0,
  favoriteCount: 0,
  searchCount: 0,
  avgStayTime: 0,
  primaryPreference: '',
  preferenceDistribution: [],
  interestTags: [],
  consumptionLevel: {
    level: '',
    icon: '',
    color: ''
  },
  avgConsumption: 0,
  consumptionFrequency: '',
  priceSensitivity: '',
  travelMode: '',
  tripDuration: '',
  seasonPreference: '',
  frequentDestinations: ''
})

const lastUpdateTime = ref('')

// 获取消费等级样式类
const getConsumptionLevelClass = (level: string) => {
  if (level.includes('豪华')) {
    return 'level-luxury'
  } else if (level.includes('品质') || level.includes('舒适')) {
    return 'level-comfort'
  } else if (level.includes('经济') || level.includes('实惠')) {
    return 'level-economy'
  }
  return ''
}

// 获取标签权重样式类
const getTagWeightClass = (weight: number) => {
  if (weight >= 10) {
    return 'tag-weight-high'
  } else if (weight >= 5) {
    return 'tag-weight-medium'
  } else {
    return 'tag-weight-low'
  }
}

// 获取标签样式
const getTagStyle = (weight: number) => {
  // 根据权重计算边框粗细和背景色深度
  const borderWidth = Math.max(1, Math.min(3, Math.floor(weight / 3) + 1))
  const bgOpacity = Math.min(0.15, weight / 30)
  
  return {
    borderWidth: `${borderWidth}px`,
    backgroundColor: `rgba(144, 147, 153, ${bgOpacity})`
  }
}

// 加载用户画像数据
const loadPortraitData = async () => {
  try {
    const response = await getUserPortrait()
    
    if (response.code === 200 && response.data) {
      const data = response.data
      
      // 设置用户画像数据
      portraitData.value = {
        browseCount: data.browseCount || 0,
        favoriteCount: data.favoriteCount || 0,
        searchCount: data.searchCount || 0,
        avgStayTime: data.avgStayTime || 0,
        primaryPreference: data.primaryPreference || '',
        preferenceDistribution: data.preferenceDistribution || [],
        interestTags: data.interestTags || [],
        consumptionLevel: (data.consumptionLevel && data.consumptionLevel.level) ? data.consumptionLevel : {
          level: '',
          icon: '',
          color: ''
        },
        avgConsumption: data.avgConsumption !== undefined && data.avgConsumption !== null ? data.avgConsumption : 0,
        consumptionFrequency: data.consumptionFrequency || '',
        priceSensitivity: data.priceSensitivity || '',
        travelMode: data.travelMode || '',
        tripDuration: data.tripDuration || '',
        seasonPreference: data.seasonPreference || '',
        frequentDestinations: data.frequentDestinations || ''
      }
      
      // 更新时间
      if (data.lastUpdateTime) {
        lastUpdateTime.value = data.lastUpdateTime
      } else {
        const now = new Date()
        lastUpdateTime.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
      }
      
      console.log('用户画像数据加载完成:', portraitData.value)
    } else {
      throw new Error(response.message || '获取用户画像失败')
    }
  } catch (error: any) {
    console.error('加载用户画像失败:', error)
    ElMessage.error('加载用户画像失败: ' + (error.message || '未知错误'))
    
    // 如果加载失败，使用默认数据
    portraitData.value = {
      browseCount: 0,
      favoriteCount: 0,
      searchCount: 0,
      avgStayTime: 0,
      primaryPreference: '自然风光',
      preferenceDistribution: [
        { type: '自然风光', icon: '🏔️', percentage: 45, color: '#909399' },
        { type: '人文历史', icon: '🏛️', percentage: 25, color: '#909399' },
        { type: '美食体验', icon: '🍽️', percentage: 20, color: '#909399' },
        { type: '休闲度假', icon: '🏖️', percentage: 10, color: '#909399' }
      ],
      interestTags: [
        { name: '摄影', icon: '📷', weight: 10 },
        { name: '美食', icon: '🍜', weight: 9 },
        { name: '文化', icon: '🎭', weight: 8 }
      ],
      consumptionLevel: {
        level: '品质舒适型',
        icon: '💳',
        color: '#909399'
      },
      avgConsumption: 800,
      consumptionFrequency: '每月2-3次',
      priceSensitivity: '中等',
      travelMode: '自由行 + 自驾游',
      tripDuration: '3-5天',
      seasonPreference: '春季、秋季',
      frequentDestinations: '重庆、成都、西安'
    }
    
    const now = new Date()
    lastUpdateTime.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
  }
}

onMounted(() => {
  loadPortraitData()
})
</script>

<style lang="scss" scoped>
.user-portrait {
  .page-header {
    margin-bottom: 24px;
    padding: 32px;
    background: linear-gradient(to bottom, #fafbfc 0%, #f5f7fa 100%);
    border-radius: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-content {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .header-icon {
        width: 80px;
        height: 80px;
        background: #f0f2f5;
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #606266;
      }
      
      .header-text {
        h2 {
          margin: 0 0 8px 0;
          color: #303133;
          font-size: 32px;
          font-weight: 700;
        }
        
        .page-desc {
          margin: 0;
          color: #606266;
          font-size: 15px;
          line-height: 1.6;
        }
      }
    }
    
    .last-update {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 20px;
      background: #f0f2f5;
      border-radius: 30px;
      color: #606266;
      font-size: 14px;
      
      .el-icon {
        color: #909399;
      }
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
  
  .portrait-stats {
    margin-bottom: 24px;
    
    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 12px;
      border: 1px solid #ebeef5;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: background-color 0.3s, border-color 0.3s, box-shadow 0.3s;
      cursor: default;
      height: 96px;
      box-sizing: border-box;
      
      &:hover {
        background-color: #fafafa;
        border-color: #dcdfe6;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transform: none;
        
        .stat-icon {
          transform: none !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          
          :deep(.el-icon) {
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            text-shadow: none !important;
            opacity: 1 !important;
          }
        }
      }
      
      .stat-icon {
        width: auto;
        height: auto;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        flex-shrink: 0;
        transition: none !important;
        transform: none !important;
        pointer-events: none !important;
        box-shadow: none !important;
        filter: none !important;
        opacity: 1 !important;
        background: transparent !important;
        border-radius: 0 !important;
        
        :deep(.el-icon) {
          transition: none !important;
          transform: none !important;
          box-shadow: none !important;
          filter: none !important;
          text-shadow: none !important;
          opacity: 1 !important;
        }
        
        &:hover {
          transform: none !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          
          :deep(.el-icon) {
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            text-shadow: none !important;
            opacity: 1 !important;
          }
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 2px;
        }
      }
    }
  }
  
  .portrait-card {
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: visible;
    border: 1px solid #e4e7ed;
    box-shadow: none;
    background: white;
    
    :deep(.el-card__header) {
      padding: 20px 24px;
      border-bottom: 2px solid #f0f2f5;
      background: #fafbfc;
    }
    
    :deep(.el-card__body) {
      padding: 24px;
      background: white;
    }
    
    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      
      .header-icon {
        font-size: 20px;
        color: #606266;
      }
    }
    
    // 旅游偏好分析 - 清晰数据展示
    .preference-analysis {
      .primary-preference {
        text-align: center;
        padding: 32px 24px;
        background: #f8f9fa;
        border: 1px solid #e4e7ed;
        border-radius: 6px;
        margin-bottom: 24px;
        
        .preference-label {
          font-size: 13px;
          color: #909399;
          margin-bottom: 12px;
          font-weight: 500;
        }
        
        .preference-value {
          font-size: 32px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
        }
      }
      
      .preference-chart {
        .chart-item {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .chart-label {
            display: flex;
            align-items: center;
            gap: 8px;
            min-width: 100px;
            flex-shrink: 0;
            
            .label-icon {
              font-size: 18px;
              width: 20px;
              text-align: center;
            }
            
            .label-text {
              font-size: 14px;
              color: #606266;
              font-weight: 500;
            }
          }
          
          .chart-bar-wrapper {
            flex: 1;
            height: 28px;
            background: #f0f2f5;
            border-radius: 4px;
            overflow: hidden;
            position: relative;
            
            .chart-bar {
              height: 100%;
              background: #909399 !important;
              transition: width 0.5s ease;
              display: flex;
              align-items: center;
              padding-right: 8px;
              justify-content: flex-end;
            }
          }
          
          .chart-value {
            min-width: 50px;
            text-align: right;
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            flex-shrink: 0;
          }
        }
      }
      
      .preference-chart-empty {
        text-align: center;
        padding: 60px 0;
        color: #909399;
        font-size: 14px;
      }
    }
    
    // 兴趣标签云 - 清晰标签展示（优化版）
    .interest-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      padding: 20px 0;
      min-height: 200px;
      align-items: flex-start;
      
      .interest-tag {
        padding: 10px 16px;
        background: white;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        color: #606266;
        font-weight: 500;
        font-size: 14px;
        cursor: default;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        transition: all 0.2s ease;
        
        .tag-icon {
          font-size: 16px;
          flex-shrink: 0;
        }
        
        .tag-name {
          font-size: 14px;
          line-height: 1.4;
        }
        
        // 兴趣度高（权重 >= 10）
        &.tag-weight-high {
          border-color: #909399;
          font-weight: 600;
          background: rgba(144, 147, 153, 0.08);
          
          .tag-name {
            color: #303133;
          }
        }
        
        // 兴趣度中（权重 5-9）
        &.tag-weight-medium {
          border-color: #c0c4cc;
          font-weight: 500;
          background: rgba(144, 147, 153, 0.05);
          
          .tag-name {
            color: #606266;
          }
        }
        
        // 兴趣度低（权重 < 5）
        &.tag-weight-low {
          border-color: #e4e7ed;
          font-weight: 500;
          background: white;
          
          .tag-name {
            color: #909399;
          }
        }
      }
    }
    
    .interest-tags-empty {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 200px;
      color: #909399;
      font-size: 14px;
    }
    
    .tag-legend {
      text-align: left;
      padding-top: 16px;
      border-top: 1px solid #f0f2f5;
      margin-top: 12px;
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: 16px;
      
      .legend-item {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: #606266;
        
        .legend-dot {
          width: 10px;
          height: 10px;
          border-radius: 50%;
          flex-shrink: 0;
          
          &.legend-high {
            background: #909399;
          }
          
          &.legend-medium {
            background: #c0c4cc;
          }
          
          &.legend-low {
            background: #e4e7ed;
          }
        }
      }
      
      .legend-note {
        font-size: 11px;
        color: #909399;
        margin-left: auto;
      }
    }
    
    // 消费行为分析 - 清晰数据展示
    .consumption-analysis {
      .consumption-level {
        text-align: center;
        margin-bottom: 24px;
        padding-bottom: 24px;
        border-bottom: 1px solid #f0f2f5;
        
        .level-badge {
          display: inline-flex;
          align-items: center;
          gap: 10px;
          padding: 10px 20px;
          border-radius: 4px;
          background: white;
          border: 1px solid #e4e7ed;
          color: #303133;
          font-size: 15px;
          font-weight: 600;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
          
          .badge-icon {
            font-size: 18px;
          }
          
          // 豪华型
          &.level-luxury {
            background: white;
            border-color: #e4e7ed;
            color: #303133;
            
            .badge-icon {
              color: #606266;
            }
          }
          
          // 品质舒适型
          &.level-comfort {
            background: white;
            border-color: #e4e7ed;
            color: #303133;
            
            .badge-icon {
              color: #606266;
            }
          }
          
          // 经济实惠型
          &.level-economy {
            background: white;
            border-color: #e4e7ed;
            color: #303133;
            
            .badge-icon {
              color: #606266;
            }
          }
        }
        
        .level-badge-empty {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          padding: 10px 20px;
          border-radius: 4px;
          background: #f8f9fa;
          border: 1px solid #e4e7ed;
          color: #909399;
          font-size: 15px;
          font-weight: 500;
          
          .badge-text {
            color: #909399;
          }
        }
      }
      
      .consumption-stats {
        .stat-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 14px 0;
          border-bottom: 1px solid #f0f2f5;
          
          &:last-child {
            margin-bottom: 0;
            border-bottom: none;
          }
          
          .stat-label {
            font-size: 14px;
            color: #909399;
            font-weight: 500;
          }
          
          .stat-value {
            font-size: 15px;
            font-weight: 600;
            color: #303133;
          }
        }
      }
    }
    
    // 出行特征 - 清晰列表展示
    .travel-characteristics {
      .characteristic-item {
        display: flex;
        align-items: flex-start;
        gap: 14px;
        padding: 16px 0;
        border-bottom: 1px solid #f0f2f5;
        
        &:last-child {
          margin-bottom: 0;
          border-bottom: none;
        }
        
        .char-icon {
          font-size: 20px;
          width: 36px;
          height: 36px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #f8f9fa;
          border: 1px solid #e4e7ed;
          border-radius: 4px;
          flex-shrink: 0;
        }
        
        .char-content {
          flex: 1;
          min-width: 0;
          
          .char-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 6px;
            font-weight: 500;
          }
          
          .char-value {
            font-size: 15px;
            font-weight: 600;
            color: #303133;
            line-height: 1.5;
            word-break: break-all;
          }
        }
      }
    }
  }
  
  // 推荐说明
  .recommendation-card {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    border: 1px solid rgba(102, 126, 234, 0.1);
    
    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: 600;
      
      .header-icon {
        font-size: 22px;
        color: #667eea;
      }
    }
    
    .recommendation-content {
      .recommendation-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 16px;
        background: white;
        border-radius: 12px;
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .item-icon {
          font-size: 20px;
          margin-top: 2px;
          flex-shrink: 0;
        }
        
        .item-text {
          flex: 1;
          font-size: 14px;
          line-height: 1.8;
          color: #606266;
          
          strong {
            color: #303133;
            font-weight: 600;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 992px) {
  .user-portrait {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      padding: 24px;
      
      .header-content {
        .header-icon {
          width: 64px;
          height: 64px;
        }
        
        .header-text h2 {
          font-size: 28px;
        }
      }
      
      .last-update {
        width: 100%;
        justify-content: center;
      }
    }
    
    .portrait-stats {
      .stat-card {
        padding: 20px;
        
        .stat-icon {
          width: 56px;
          height: 56px;
        }
        
        .stat-content .stat-value {
          font-size: 24px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .user-portrait {
    .page-header {
      padding: 20px;
      
      .header-content {
        gap: 12px;
        
        .header-icon {
          width: 56px;
          height: 56px;
          
          .el-icon {
            font-size: 32px;
          }
        }
        
        .header-text {
          h2 {
            font-size: 24px;
          }
          
          .page-desc {
            font-size: 13px;
          }
        }
      }
      
      .last-update {
        font-size: 13px;
        padding: 10px 16px;
      }
    }
    
    .portrait-card {
      .card-header {
        font-size: 16px;
        
        .header-icon {
          font-size: 20px;
        }
      }
      
      .preference-analysis {
        .preference-chart {
          .chart-item {
            flex-wrap: wrap;
            
            .chart-label {
              width: 100%;
              margin-bottom: 8px;
            }
            
            .chart-bar-wrapper {
              width: calc(100% - 60px);
            }
          }
        }
      }
    }
  }
}
</style>


