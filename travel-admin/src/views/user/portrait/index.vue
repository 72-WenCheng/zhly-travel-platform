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
              :style="{ 
                fontSize: tag.weight * 2 + 'px',
                opacity: 0.5 + tag.weight / 20
              }"
            >
              {{ tag.icon }} {{ tag.name }}
            </div>
          </div>
          <div v-else class="interest-tags-empty">
            <span>----</span>
          </div>
          
          <div v-if="portraitData.interestTags && portraitData.interestTags.length > 0" class="tag-legend">
            <span class="legend-item">
              <span class="legend-dot" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"></span>
              字体越大表示兴趣度越高
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
              <div v-if="portraitData.consumptionLevel && portraitData.consumptionLevel.level" class="level-badge" :style="{ 
                background: portraitData.consumptionLevel.color 
              }">
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
        { type: '自然风光', icon: '🏔️', percentage: 45, color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
        { type: '人文历史', icon: '🏛️', percentage: 25, color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
        { type: '美食体验', icon: '🍽️', percentage: 20, color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
        { type: '休闲度假', icon: '🏖️', percentage: 10, color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' }
      ],
      interestTags: [
        { name: '摄影', icon: '📷', weight: 10 },
        { name: '美食', icon: '🍜', weight: 9 },
        { name: '文化', icon: '🎭', weight: 8 }
      ],
      consumptionLevel: {
        level: '品质舒适型',
        icon: '💳',
        color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
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
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    
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
    
    // 旅游偏好分析
    .preference-analysis {
      .primary-preference {
        text-align: center;
        padding: 20px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        border-radius: 12px;
        margin-bottom: 24px;
        
        .preference-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }
        
        .preference-value {
          font-size: 28px;
          font-weight: 700;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
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
            gap: 6px;
            min-width: 120px;
            
            .label-icon {
              font-size: 20px;
            }
            
            .label-text {
              font-size: 14px;
              color: #606266;
              font-weight: 500;
            }
          }
          
          .chart-bar-wrapper {
            flex: 1;
            height: 24px;
            background: #f5f7fa;
            border-radius: 12px;
            overflow: hidden;
            
            .chart-bar {
              height: 100%;
              border-radius: 12px;
              transition: all 0.3s ease;
            }
          }
          
          .chart-value {
            min-width: 48px;
            text-align: right;
            font-size: 14px;
            font-weight: 600;
            color: #667eea;
          }
        }
      }
      
      .preference-chart-empty {
        text-align: center;
        padding: 40px 0;
        color: #909399;
        font-size: 14px;
      }
    }
    
    // 兴趣标签云
    .interest-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      justify-content: center;
      padding: 20px 0;
      min-height: 200px;
      align-items: center;
      
      .interest-tag {
        color: #667eea;
        font-weight: 600;
        cursor: default;
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
      text-align: center;
      padding-top: 16px;
      border-top: 1px solid #ebeef5;
      
      .legend-item {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;
        color: #909399;
        
        .legend-dot {
          width: 12px;
          height: 12px;
          border-radius: 50%;
        }
      }
    }
    
    // 消费行为分析
    .consumption-analysis {
      .consumption-level {
        text-align: center;
        margin-bottom: 24px;
        
        .level-badge {
          display: inline-flex;
          align-items: center;
          gap: 12px;
          padding: 16px 32px;
          border-radius: 50px;
          color: white;
          font-size: 20px;
          font-weight: 700;
          box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
          
          .badge-icon {
            font-size: 28px;
          }
        }
        
        .level-badge-empty {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          padding: 16px 32px;
          border-radius: 50px;
          background: #f5f7fa;
          border: 1px solid #dcdfe6;
          color: #909399;
          font-size: 20px;
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
          padding: 16px 20px;
          background: #f5f7fa;
          border-radius: 12px;
          margin-bottom: 12px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .stat-label {
            font-size: 14px;
            color: #606266;
          }
          
          .stat-value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
          }
        }
      }
    }
    
    // 出行特征
    .travel-characteristics {
      .characteristic-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
        border-radius: 12px;
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .char-icon {
          font-size: 32px;
          width: 56px;
          height: 56px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: white;
          border-radius: 12px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }
        
        .char-content {
          flex: 1;
          
          .char-label {
            font-size: 13px;
            color: #909399;
            margin-bottom: 4px;
          }
          
          .char-value {
            font-size: 15px;
            font-weight: 600;
            color: #303133;
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


