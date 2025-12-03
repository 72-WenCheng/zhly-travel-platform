<template>
  <view class="ai-page">
    <!-- AI智能推荐标题 -->
    <view class="ai-header">
      <text class="ai-title">AI智能推荐</text>
      <text class="ai-subtitle">让AI为您规划完美旅程</text>
    </view>

    <!-- AI生成攻略表单 -->
    <view class="ai-form">
      <view class="form-title">
        <text class="title-text">智能生成专属攻略</text>
        <text class="title-desc">输入您的需求，AI为您量身定制旅游方案</text>
      </view>
      
      <view class="form-content">
        <!-- 目的地选择 -->
        <view class="form-item">
          <text class="item-label">目的地</text>
          <input 
            v-model="formData.destination" 
            class="item-input" 
            placeholder="请输入目的地，如：重庆、成都、北京"
            @input="onDestinationInput"
          />
        </view>
        
        <!-- 出发地选择 -->
        <view class="form-item">
          <text class="item-label">出发地</text>
          <input 
            v-model="formData.departure" 
            class="item-input" 
            placeholder="请输入出发地"
          />
        </view>
        
        <!-- 旅游类型 -->
        <view class="form-item">
          <text class="item-label">旅游类型</text>
          <view class="type-options">
            <view 
              v-for="type in travelTypes" 
              :key="type.value"
              class="type-option"
              :class="{ active: formData.travelType === type.value }"
              @click="selectTravelType(type.value)"
            >
              <text class="type-icon">{{ type.icon }}</text>
              <text class="type-name">{{ type.name }}</text>
            </view>
          </view>
        </view>
        
        <!-- 出行时间 -->
        <view class="form-item">
          <text class="item-label">出行时间</text>
          <view class="time-inputs">
            <picker 
              mode="date" 
              :value="formData.departureTime" 
              @change="onDepartureTimeChange"
            >
              <view class="time-picker">
                <text class="time-label">出发时间</text>
                <text class="time-value">{{ formData.departureTime || '请选择' }}</text>
              </view>
            </picker>
            <picker 
              mode="date" 
              :value="formData.returnTime" 
              @change="onReturnTimeChange"
            >
              <view class="time-picker">
                <text class="time-label">返回时间</text>
                <text class="time-value">{{ formData.returnTime || '请选择' }}</text>
              </view>
            </picker>
          </view>
        </view>
        
        <!-- 人员数量 -->
        <view class="form-item">
          <text class="item-label">人员数量</text>
          <view class="person-count">
            <view class="count-control">
              <text class="count-btn" @click="decreasePersonCount">-</text>
              <text class="count-value">{{ formData.personCount }}</text>
              <text class="count-btn" @click="increasePersonCount">+</text>
            </view>
            <text class="count-desc">人</text>
          </view>
        </view>
        
        <!-- 预算范围 -->
        <view class="form-item">
          <text class="item-label">预算范围</text>
          <view class="budget-options">
            <view 
              v-for="budget in budgetOptions" 
              :key="budget.value"
              class="budget-option"
              :class="{ active: formData.budget === budget.value }"
              @click="selectBudget(budget.value)"
            >
              <text class="budget-name">{{ budget.name }}</text>
              <text class="budget-desc">{{ budget.desc }}</text>
            </view>
          </view>
        </view>
        
        <!-- 特殊需求 -->
        <view class="form-item">
          <text class="item-label">特殊需求</text>
          <textarea 
            v-model="formData.specialRequirements" 
            class="textarea-input" 
            placeholder="请输入特殊需求，如：带老人、带小孩、美食偏好等"
            maxlength="200"
          />
        </view>
      </view>
      
      <!-- 生成按钮 -->
      <view class="generate-section">
        <button 
          class="generate-btn" 
          :class="{ loading: isGenerating }"
          @click="generatePlan"
          :disabled="isGenerating"
        >
          <text v-if="!isGenerating" class="btn-text">🤖 AI生成攻略</text>
          <text v-else class="btn-text">生成中...</text>
        </button>
      </view>
    </view>

    <!-- 生成结果 -->
    <view class="result-section" v-if="generatedPlan">
      <view class="result-header">
        <text class="result-title">AI为您生成的攻略</text>
        <text class="result-subtitle">基于您的需求智能定制</text>
      </view>
      
      <view class="plan-card">
        <view class="plan-header">
          <text class="plan-title">{{ generatedPlan.title }}</text>
          <view class="plan-tags">
            <text 
              v-for="tag in generatedPlan.tags" 
              :key="tag"
              class="plan-tag"
            >
              {{ tag }}
            </text>
          </view>
        </view>
        
        <view class="plan-content">
          <text class="plan-desc">{{ generatedPlan.description }}</text>
          
          <view class="plan-details">
            <view class="detail-item">
              <text class="detail-label">目的地</text>
              <text class="detail-value">{{ generatedPlan.destination }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">行程天数</text>
              <text class="detail-value">{{ generatedPlan.days }}天</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">预算范围</text>
              <text class="detail-value">{{ generatedPlan.budgetRange }}</text>
            </view>
          </view>
        </view>
        
        <view class="plan-actions">
          <button class="action-btn primary" @click="savePlan">保存攻略</button>
          <button class="action-btn secondary" @click="sharePlan">分享攻略</button>
          <button class="action-btn secondary" @click="exportPlan">导出PDF</button>
        </view>
      </view>
    </view>

    <!-- 历史生成记录 -->
    <view class="history-section" v-if="historyPlans.length > 0">
      <view class="section-header">
        <text class="section-title">历史生成记录</text>
        <text class="section-more" @click="viewAllHistory">查看全部</text>
      </view>
      
      <view class="history-list">
        <view 
          v-for="plan in historyPlans" 
          :key="plan.id"
          class="history-item"
          @click="viewHistoryPlan(plan)"
        >
          <view class="history-content">
            <text class="history-title">{{ plan.title }}</text>
            <text class="history-desc">{{ plan.description }}</text>
            <text class="history-time">{{ plan.createTime }}</text>
          </view>
          <view class="history-actions">
            <text class="action-text">查看</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 表单数据
const formData = ref({
  destination: '',
  departure: '',
  travelType: 1,
  departureTime: '',
  returnTime: '',
  personCount: 2,
  budget: 1,
  specialRequirements: ''
})

// 旅游类型选项
const travelTypes = ref([
  { value: 1, name: '国内游', icon: '🏠' },
  { value: 2, name: '国外游', icon: '✈️' },
  { value: 3, name: '游轮游', icon: '🚢' },
  { value: 4, name: '节假日游', icon: '🎉' },
  { value: 5, name: '非节假日游', icon: '📅' }
])

// 预算选项
const budgetOptions = ref([
  { value: 1, name: '经济型', desc: '500-1000元/人' },
  { value: 2, name: '舒适型', desc: '1000-3000元/人' },
  { value: 3, name: '豪华型', desc: '3000-5000元/人' },
  { value: 4, name: '奢华型', desc: '5000元以上/人' }
])

// 生成状态
const isGenerating = ref(false)

// 生成的攻略
const generatedPlan = ref(null)

// 历史记录
const historyPlans = ref([
  {
    id: 1,
    title: '重庆三日游攻略',
    description: '探索山城魅力，品味巴渝文化',
    createTime: '2024-01-15 14:30',
    destination: '重庆',
    days: 3,
    budgetRange: '1000-2000元/人'
  },
  {
    id: 2,
    title: '成都美食之旅',
    description: '舌尖上的成都，品味川菜魅力',
    createTime: '2024-01-14 10:20',
    destination: '成都',
    days: 2,
    budgetRange: '800-1500元/人'
  }
])

// 目的地输入事件
const onDestinationInput = (e: any) => {
  // TODO: 实现目的地自动补全
  console.log('目的地输入:', e.detail.value)
}

// 选择旅游类型
const selectTravelType = (type: number) => {
  formData.value.travelType = type
}

// 出发时间变化
const onDepartureTimeChange = (e: any) => {
  formData.value.departureTime = e.detail.value
}

// 返回时间变化
const onReturnTimeChange = (e: any) => {
  formData.value.returnTime = e.detail.value
}

// 减少人员数量
const decreasePersonCount = () => {
  if (formData.value.personCount > 1) {
    formData.value.personCount--
  }
}

// 增加人员数量
const increasePersonCount = () => {
  if (formData.value.personCount < 10) {
    formData.value.personCount++
  }
}

// 选择预算
const selectBudget = (budget: number) => {
  formData.value.budget = budget
}

// 生成攻略
const generatePlan = async () => {
  if (!formData.value.destination) {
    uni.showToast({
      title: '请输入目的地',
      icon: 'none'
    })
    return
  }
  
  isGenerating.value = true
  
  try {
    // TODO: 调用AI生成API
    await new Promise(resolve => setTimeout(resolve, 3000)) // 模拟生成时间
    
    // 模拟生成结果
    generatedPlan.value = {
      title: `${formData.value.destination}${formData.value.personCount}人游攻略`,
      description: `基于您的需求，AI为您精心规划了${formData.value.destination}的完美旅程，包含景点推荐、美食攻略、住宿建议等全方位信息。`,
      destination: formData.value.destination,
      days: 3,
      budgetRange: budgetOptions.value.find(b => b.value === formData.value.budget)?.desc || '',
      tags: ['AI生成', '个性化', '智能推荐']
    }
    
    uni.showToast({
      title: '攻略生成成功',
      icon: 'success'
    })
  } catch (error) {
    uni.showToast({
      title: '生成失败，请重试',
      icon: 'error'
    })
  } finally {
    isGenerating.value = false
  }
}

// 保存攻略
const savePlan = () => {
  uni.showToast({
    title: '攻略已保存',
    icon: 'success'
  })
}

// 分享攻略
const sharePlan = () => {
  uni.showActionSheet({
    itemList: ['微信好友', '朋友圈', 'QQ', '微博'],
    success: (res) => {
      uni.showToast({
        title: '分享成功',
        icon: 'success'
      })
    }
  })
}

// 导出PDF
const exportPlan = () => {
  uni.showToast({
    title: 'PDF导出中...',
    icon: 'loading'
  })
}

// 查看全部历史
const viewAllHistory = () => {
  uni.navigateTo({
    url: '/pages/ai/history'
  })
}

// 查看历史攻略
const viewHistoryPlan = (plan: any) => {
  uni.navigateTo({
    url: `/pages/plan/detail?id=${plan.id}`
  })
}

// 加载历史记录
const loadHistoryPlans = async () => {
  try {
    // TODO: 调用API获取历史记录
    console.log('加载历史记录')
  } catch (error) {
    console.error('加载历史记录失败:', error)
  }
}

onMounted(() => {
  loadHistoryPlans()
})
</script>

<style lang="scss" scoped>
.ai-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding: 20rpx;
}

.ai-header {
  text-align: center;
  margin-bottom: 40rpx;
  
  .ai-title {
    font-size: 48rpx;
    font-weight: bold;
    color: #333;
    display: block;
    margin-bottom: 16rpx;
  }
  
  .ai-subtitle {
    font-size: 28rpx;
    color: #666;
  }
}

.ai-form {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
  margin-bottom: 40rpx;
  
  .form-title {
    text-align: center;
    margin-bottom: 40rpx;
    
    .title-text {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 16rpx;
    }
    
    .title-desc {
      font-size: 24rpx;
      color: #666;
    }
  }
  
  .form-content {
    .form-item {
      margin-bottom: 40rpx;
      
      .item-label {
        font-size: 28rpx;
        color: #333;
        display: block;
        margin-bottom: 16rpx;
        font-weight: bold;
      }
      
      .item-input {
        width: 100%;
        height: 80rpx;
        border: 2rpx solid #e4e7ed;
        border-radius: 8rpx;
        padding: 0 20rpx;
        font-size: 28rpx;
        background: #f8f9fa;
      }
      
      .type-options {
        display: flex;
        flex-wrap: wrap;
        gap: 16rpx;
        
        .type-option {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 20rpx;
          border: 2rpx solid #e4e7ed;
          border-radius: 12rpx;
          background: #f8f9fa;
          min-width: 120rpx;
          
          &.active {
            border-color: #409eff;
            background: #ecf5ff;
          }
          
          .type-icon {
            font-size: 32rpx;
            margin-bottom: 8rpx;
          }
          
          .type-name {
            font-size: 24rpx;
            color: #333;
          }
        }
      }
      
      .time-inputs {
        display: flex;
        gap: 20rpx;
        
        .time-picker {
          flex: 1;
          height: 80rpx;
          border: 2rpx solid #e4e7ed;
          border-radius: 8rpx;
          padding: 0 20rpx;
          display: flex;
          flex-direction: column;
          justify-content: center;
          background: #f8f9fa;
          
          .time-label {
            font-size: 22rpx;
            color: #666;
          }
          
          .time-value {
            font-size: 26rpx;
            color: #333;
          }
        }
      }
      
      .person-count {
        display: flex;
        align-items: center;
        gap: 20rpx;
        
        .count-control {
          display: flex;
          align-items: center;
          border: 2rpx solid #e4e7ed;
          border-radius: 8rpx;
          background: #f8f9fa;
          
          .count-btn {
            width: 60rpx;
            height: 60rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32rpx;
            color: #333;
            background: #fff;
            border-radius: 6rpx;
          }
          
          .count-value {
            width: 80rpx;
            text-align: center;
            font-size: 28rpx;
            color: #333;
          }
        }
        
        .count-desc {
          font-size: 28rpx;
          color: #333;
        }
      }
      
      .budget-options {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16rpx;
        
        .budget-option {
          padding: 20rpx;
          border: 2rpx solid #e4e7ed;
          border-radius: 12rpx;
          background: #f8f9fa;
          
          &.active {
            border-color: #409eff;
            background: #ecf5ff;
          }
          
          .budget-name {
            font-size: 26rpx;
            color: #333;
            font-weight: bold;
            display: block;
            margin-bottom: 8rpx;
          }
          
          .budget-desc {
            font-size: 22rpx;
            color: #666;
          }
        }
      }
      
      .textarea-input {
        width: 100%;
        height: 120rpx;
        border: 2rpx solid #e4e7ed;
        border-radius: 8rpx;
        padding: 20rpx;
        font-size: 28rpx;
        background: #f8f9fa;
        resize: none;
      }
    }
  }
  
  .generate-section {
    text-align: center;
    margin-top: 40rpx;
    
    .generate-btn {
      width: 100%;
      height: 88rpx;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 44rpx;
      color: #fff;
      font-size: 32rpx;
      font-weight: bold;
      
      &.loading {
        opacity: 0.7;
      }
      
      .btn-text {
        color: #fff;
      }
    }
  }
}

.result-section {
  margin-bottom: 40rpx;
  
  .result-header {
    text-align: center;
    margin-bottom: 30rpx;
    
    .result-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 16rpx;
    }
    
    .result-subtitle {
      font-size: 24rpx;
      color: #666;
    }
  }
  
  .plan-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    
    .plan-header {
      margin-bottom: 20rpx;
      
      .plan-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        display: block;
        margin-bottom: 16rpx;
      }
      
      .plan-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 12rpx;
        
        .plan-tag {
          padding: 8rpx 16rpx;
          background: #ecf5ff;
          color: #409eff;
          border-radius: 20rpx;
          font-size: 22rpx;
        }
      }
    }
    
    .plan-content {
      .plan-desc {
        font-size: 28rpx;
        color: #666;
        line-height: 1.6;
        display: block;
        margin-bottom: 30rpx;
      }
      
      .plan-details {
        .detail-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16rpx 0;
          border-bottom: 1rpx solid #f0f0f0;
          
          &:last-child {
            border-bottom: none;
          }
          
          .detail-label {
            font-size: 26rpx;
            color: #666;
          }
          
          .detail-value {
            font-size: 26rpx;
            color: #333;
            font-weight: bold;
          }
        }
      }
    }
    
    .plan-actions {
      display: flex;
      gap: 20rpx;
      margin-top: 30rpx;
      
      .action-btn {
        flex: 1;
        height: 72rpx;
        border-radius: 36rpx;
        font-size: 26rpx;
        font-weight: bold;
        
        &.primary {
          background: #409eff;
          color: #fff;
          border: none;
        }
        
        &.secondary {
          background: #f8f9fa;
          color: #333;
          border: 2rpx solid #e4e7ed;
        }
      }
    }
  }
}

.history-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .section-more {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .history-list {
    .history-item {
      background: #fff;
      border-radius: 16rpx;
      padding: 30rpx;
      margin-bottom: 20rpx;
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .history-content {
        flex: 1;
        
        .history-title {
          font-size: 28rpx;
          font-weight: bold;
          color: #333;
          display: block;
          margin-bottom: 8rpx;
        }
        
        .history-desc {
          font-size: 24rpx;
          color: #666;
          display: block;
          margin-bottom: 8rpx;
        }
        
        .history-time {
          font-size: 22rpx;
          color: #999;
        }
      }
      
      .history-actions {
        .action-text {
          font-size: 24rpx;
          color: #409eff;
        }
      }
    }
  }
}
</style>



















