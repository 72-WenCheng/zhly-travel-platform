<template>
  <view class="create-page">
    <!-- 顶部说明 -->
    <view class="header">
      <text class="title">创建攻略</text>
      <text class="subtitle">分享你的旅游经验，帮助更多旅友</text>
    </view>

    <!-- 表单 -->
    <view class="form-section">
      <!-- 基本信息 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">ℹ️</text>
          <text class="group-title">基本信息</text>
        </view>
        
        <view class="form-item">
          <text class="item-label">攻略标题 <text class="required">*</text></text>
          <input 
            v-model="formData.title" 
            class="form-input" 
            placeholder="请输入攻略标题（0/50）"
            maxlength="50"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label">目的地 <text class="required">*</text></text>
          <input 
            v-model="formData.destination" 
            class="form-input" 
            placeholder="请输入目的地"
          />
        </view>
        
        <view class="form-row">
          <view class="form-item-half">
            <text class="item-label">出行天数</text>
            <picker mode="selector" :range="daysOptions" @change="onDaysChange">
              <view class="picker-display">
                <text class="picker-text">{{ formData.days || '请选择' }}天</text>
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
          
          <view class="form-item-half">
            <text class="item-label">出行人数</text>
            <picker mode="selector" :range="personOptions" @change="onPersonChange">
              <view class="picker-display">
                <text class="picker-text">{{ formData.personCount || '请选择' }}人</text>
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
        </view>
        
        <view class="form-item">
          <text class="item-label">预算（元）</text>
          <input 
            v-model="formData.budget" 
            class="form-input" 
            type="digit"
            placeholder="请输入预算"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label">攻略类型</text>
          <view class="radio-group">
            <view 
              v-for="type in travelTypes" 
              :key="type"
              class="radio-option"
              :class="{ active: formData.travelType === type }"
              @click="formData.travelType = type"
            >
              <text class="radio-text">{{ type }}</text>
            </view>
          </view>
        </view>
        
        <view class="form-item">
          <text class="item-label">攻略描述</text>
          <textarea 
            v-model="formData.description" 
            class="form-textarea" 
            placeholder="请输入攻略描述，让更多人了解这份攻略的特色（0/500）"
            maxlength="500"
          />
        </view>
      </view>

      <!-- 快速信息 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">⚡</text>
          <text class="group-title">快速信息</text>
        </view>
        
        <view class="form-item">
          <text class="item-label">最佳季节</text>
          <view class="radio-group">
            <view 
              v-for="season in seasons" 
              :key="season"
              class="radio-option"
              :class="{ active: formData.bestSeason === season }"
              @click="formData.bestSeason = season"
            >
              <text class="radio-text">{{ season }}</text>
            </view>
          </view>
        </view>
        
        <view class="form-item">
          <text class="item-label">适合人群</text>
          <view class="radio-group">
            <view 
              v-for="crowd in suitableFor" 
              :key="crowd"
              class="radio-option"
              :class="{ active: formData.suitableFor === crowd }"
              @click="formData.suitableFor = crowd"
            >
              <text class="radio-text">{{ crowd }}</text>
            </view>
          </view>
        </view>
        
        <view class="form-item">
          <text class="item-label">难度等级</text>
          <view class="difficulty-selector">
            <view 
              v-for="level in 5" 
              :key="level"
              class="star-item"
              @click="formData.difficultyLevel = level"
            >
              <text class="star" :class="{ active: level <= formData.difficultyLevel }">★</text>
            </view>
            <text class="difficulty-text">{{ difficultyText }}</text>
          </view>
        </view>
      </view>

      <!-- 行程安排 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">📅</text>
          <text class="group-title">行程安排</text>
        </view>
        
        <view 
          v-for="(day, index) in formData.itinerary" 
          :key="index"
          class="day-plan"
        >
          <view class="day-header">
            <text class="day-number">第{{ index + 1 }}天</text>
            <text class="day-date" v-if="day.date">{{ day.date }}</text>
            <text class="day-remove" @click="removeDay(index)" v-if="formData.itinerary.length > 1">删除</text>
          </view>
          
          <view class="day-content">
            <view class="plan-item">
              <text class="item-label">时间</text>
              <picker mode="date" @change="(e) => day.date = e.detail.value">
                <view class="picker-display">
                  <text class="picker-text">{{ day.date || '选择日期' }}</text>
                  <text class="picker-arrow">📅</text>
                </view>
              </picker>
            </view>
            
            <view class="plan-item">
              <text class="item-label">活动安排 <text class="required">*</text></text>
              <textarea 
                v-model="day.activities" 
                class="form-textarea-small" 
                placeholder="例如：上午游览故宫，下午逛南锣鼓巷"
              />
            </view>
            
            <view class="plan-item">
              <text class="item-label">地点</text>
              <input 
                v-model="day.location" 
                class="form-input" 
                placeholder="例如：北京市东城区"
              />
            </view>
            
            <view class="plan-item">
              <text class="item-label">当日费用（元）</text>
              <input 
                v-model="day.cost" 
                class="form-input" 
                type="digit"
                placeholder="例如：500"
              />
            </view>
          </view>
        </view>
        
        <view class="add-day-btn" @click="addDay">
          <text class="add-icon">+</text>
          <text class="add-text">添加一天</text>
        </view>
      </view>

      <!-- 费用明细 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">💰</text>
          <text class="group-title">费用明细</text>
        </view>
        
        <view 
          v-for="(cost, index) in formData.costDetail" 
          :key="index"
          class="cost-item"
        >
          <view class="cost-header">
            <text class="cost-type-label">类型：</text>
            <picker mode="selector" :range="costTypes" @change="(e) => cost.type = costTypes[e.detail.value]">
              <view class="picker-display-inline">
                <text class="picker-text">{{ cost.type || '请选择' }}</text>
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
            <text class="cost-remove" @click="removeCost(index)" v-if="formData.costDetail.length > 1">删除</text>
          </view>
          
          <view class="cost-row">
            <view class="cost-amount">
              <text class="item-label-small">金额（元）</text>
              <input 
                v-model="cost.amount" 
                class="form-input-small" 
                type="digit"
                placeholder="0"
              />
            </view>
            <view class="cost-description">
              <text class="item-label-small">说明</text>
              <input 
                v-model="cost.description" 
                class="form-input-small" 
                placeholder="例如：往返机票"
              />
            </view>
          </view>
        </view>
        
        <view class="add-cost-btn" @click="addCost">
          <text class="add-icon">+</text>
          <text class="add-text">添加费用项</text>
        </view>
        
        <view class="total-cost">
          <text class="total-label">总计：</text>
          <text class="total-amount">¥{{ totalCost }}</text>
        </view>
      </view>

      <!-- 注意事项 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">⚠️</text>
          <text class="group-title">注意事项</text>
        </view>
        
        <view class="form-item">
          <textarea 
            v-model="formData.notice" 
            class="form-textarea-large" 
            placeholder="请输入旅行中需要注意的事项，如天气、交通、安全等（0/300）"
            maxlength="300"
          />
        </view>
      </view>

      <!-- 标签 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">🏷️</text>
          <text class="group-title">标签</text>
        </view>
        
        <view class="form-item">
          <view class="tag-selector">
            <text 
              v-for="tag in planTags" 
              :key="tag"
              class="tag-item"
              :class="{ active: selectedTags.includes(tag) }"
              @click="toggleTag(tag)"
            >
              #{{ tag }}
            </text>
          </view>
        </view>
      </view>

      <!-- 封面图片 -->
      <view class="form-group">
        <view class="group-header">
          <text class="group-icon">🖼️</text>
          <text class="group-title">封面图片</text>
        </view>
        
        <view class="form-item">
          <view class="upload-area" @click="chooseCoverImage">
            <image v-if="formData.coverImage" :src="formData.coverImage" class="cover-image" mode="aspectFill" />
            <view v-else class="upload-placeholder">
              <text class="upload-icon">📷</text>
              <text class="upload-text">点击上传封面图片</text>
              <text class="upload-hint">建议尺寸：750x500</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="footer">
      <view class="footer-actions">
        <view class="action-btn save" @click="saveDraft">
          <text class="btn-icon">💾</text>
          <text class="btn-text">保存草稿</text>
        </view>
        <view class="action-btn publish" @click="publishPlan">
          <text class="btn-icon">🚀</text>
          <text class="btn-text">发布攻略</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import request from '@/utils/request'

// 表单数据
const formData = ref({
  // 基本信息
  title: '',
  destination: '',
  days: 1,
  personCount: 1,
  budget: '',
  travelType: '自由行',
  description: '',
  
  // 快速信息
  bestSeason: '全年',
  suitableFor: '所有人',
  difficultyLevel: 1,
  
  // 行程安排
  itinerary: [
    {
      day: 1,
      date: '',
      activities: '',
      location: '',
      cost: ''
    }
  ],
  
  // 费用明细
  costDetail: [
    {
      type: '交通',
      amount: '',
      description: ''
    }
  ],
  
  // 注意事项
  notice: '',
  
  // 图片
  coverImage: ''
})

// 选中的标签
const selectedTags = ref<string[]>([])

// 出行天数选项
const daysOptions = ref(Array.from({ length: 30 }, (_, i) => i + 1))

// 出行人数选项
const personOptions = ref(Array.from({ length: 20 }, (_, i) => i + 1))

// 旅行类型
const travelTypes = ref(['自由行', '跟团游', '自驾游'])

// 季节选项
const seasons = ref(['全年', '春季', '夏季', '秋季', '冬季', '春秋'])

// 适合人群
const suitableFor = ref(['所有人', '情侣', '亲子', '独自', '朋友', '老人', '学生'])

// 费用类型
const costTypes = ref(['交通', '住宿', '餐饮', '门票', '购物', '娱乐', '其他'])

// 攻略标签
const planTags = ref([
  '穷游', '富游', '美食', '摄影', '文化', '自然', 
  '历史', '购物', '亲子', '浪漫', '冒险', '放松'
])

// 难度等级文本
const difficultyText = computed(() => {
  const texts = ['', '非常简单', '简单', '一般', '有挑战', '困难']
  return texts[formData.value.difficultyLevel]
})

// 总费用
const totalCost = computed(() => {
  return formData.value.costDetail.reduce((sum, item) => {
    return sum + (Number(item.amount) || 0)
  }, 0)
})

// 出行天数改变
const onDaysChange = (e: any) => {
  formData.value.days = daysOptions.value[e.detail.value]
}

// 出行人数改变
const onPersonChange = (e: any) => {
  formData.value.personCount = personOptions.value[e.detail.value]
}

// 添加一天行程
const addDay = () => {
  formData.value.itinerary.push({
    day: formData.value.itinerary.length + 1,
    date: '',
    activities: '',
    location: '',
    cost: ''
  })
}

// 删除一天行程
const removeDay = (index: number) => {
  if (formData.value.itinerary.length > 1) {
    formData.value.itinerary.splice(index, 1)
    // 重新编号
    formData.value.itinerary.forEach((day, i) => {
      day.day = i + 1
    })
  }
}

// 添加费用项
const addCost = () => {
  formData.value.costDetail.push({
    type: '',
    amount: '',
    description: ''
  })
}

// 删除费用项
const removeCost = (index: number) => {
  if (formData.value.costDetail.length > 1) {
    formData.value.costDetail.splice(index, 1)
  }
}

// 切换标签
const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

// 选择封面图片
const chooseCoverImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      formData.value.coverImage = res.tempFilePaths[0]
      // TODO: 上传图片到服务器
    }
  })
}

// 保存草稿
const saveDraft = async () => {
  try {
    const token = uni.getStorageSync('token')
    if (!token) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      uni.navigateTo({
        url: '/pages/user/login'
      })
      return
    }
    
    // 准备提交数据
    const submitData = {
      ...formData.value,
      tags: selectedTags.value.join(','),
      itinerary: JSON.stringify(formData.value.itinerary),
      costDetail: JSON.stringify(formData.value.costDetail),
      images: JSON.stringify([formData.value.coverImage]),
      status: 0, // 草稿
      auditStatus: 0
    }
    
    const response = await request.post('/travel-plan', submitData)
    
    uni.showToast({
      title: '草稿已保存',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    console.error('保存草稿失败:', error)
    uni.showToast({
      title: error.message || '保存失败',
      icon: 'none'
    })
  }
}

// 发布攻略
const publishPlan = async () => {
  // 验证必填项
  if (!formData.value.title) {
    uni.showToast({
      title: '请输入攻略标题',
      icon: 'none'
    })
    return
  }
  
  if (!formData.value.destination) {
    uni.showToast({
      title: '请输入目的地',
      icon: 'none'
    })
    return
  }
  
  const hasActivities = formData.value.itinerary.some(day => day.activities)
  if (!hasActivities) {
    uni.showToast({
      title: '请至少添加一天的活动安排',
      icon: 'none'
    })
    return
  }
  
  try {
    const token = uni.getStorageSync('token')
    if (!token) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      uni.navigateTo({
        url: '/pages/user/login'
      })
      return
    }
    
    // 准备提交数据
    const submitData = {
      ...formData.value,
      tags: selectedTags.value.join(','),
      itinerary: JSON.stringify(formData.value.itinerary),
      costDetail: JSON.stringify(formData.value.costDetail),
      images: JSON.stringify([formData.value.coverImage]),
      status: 0, // 草稿状态，等待审核
      auditStatus: 0 // 待审核
    }
    
    const response = await request.post('/travel-plan', submitData)
    
    uni.showToast({
      title: '提交成功，等待审核',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/plan/plan'
      })
    }, 1500)
  } catch (error: any) {
    console.error('发布攻略失败:', error)
    uni.showToast({
      title: error.message || '发布失败',
      icon: 'none'
    })
  }
}
</script>

<style lang="scss" scoped>
.create-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 160rpx;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
  color: #fff;
  
  .title {
    font-size: 44rpx;
    font-weight: bold;
    display: block;
    margin-bottom: 12rpx;
  }
  
  .subtitle {
    font-size: 26rpx;
    opacity: 0.95;
  }
}

.form-section {
  padding: 20rpx;
}

.form-group {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .group-header {
    display: flex;
    align-items: center;
    margin-bottom: 24rpx;
    
    .group-icon {
      font-size: 36rpx;
      margin-right: 12rpx;
    }
    
    .group-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
}

.form-item {
  margin-bottom: 24rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .item-label {
    font-size: 28rpx;
    color: #333;
    display: block;
    margin-bottom: 12rpx;
    
    .required {
      color: #ff6b6b;
    }
  }
  
  .form-input {
    width: 100%;
    padding: 20rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    font-size: 28rpx;
  }
  
  .form-textarea {
    width: 100%;
    padding: 20rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    font-size: 28rpx;
    min-height: 160rpx;
  }
  
  .form-textarea-small {
    width: 100%;
    padding: 20rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    font-size: 28rpx;
    min-height: 120rpx;
  }
  
  .form-textarea-large {
    width: 100%;
    padding: 20rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    font-size: 28rpx;
    min-height: 200rpx;
  }
}

.form-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 24rpx;
  
  .form-item-half {
    flex: 1;
    
    .item-label {
      font-size: 28rpx;
      color: #333;
      display: block;
      margin-bottom: 12rpx;
    }
  }
}

.picker-display {
  padding: 20rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .picker-text {
    font-size: 28rpx;
    color: #333;
  }
  
  .picker-arrow {
    font-size: 20rpx;
    color: #999;
  }
}

.picker-display-inline {
  padding: 8rpx 16rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  
  .picker-text {
    font-size: 26rpx;
    color: #333;
  }
  
  .picker-arrow {
    font-size: 18rpx;
    color: #999;
  }
}

.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  
  .radio-option {
    padding: 12rpx 24rpx;
    background: #f5f5f5;
    border-radius: 50rpx;
    border: 2px solid transparent;
    
    .radio-text {
      font-size: 26rpx;
      color: #666;
    }
    
    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-color: #667eea;
      
      .radio-text {
        color: #fff;
        font-weight: bold;
      }
    }
  }
}

.difficulty-selector {
  display: flex;
  align-items: center;
  gap: 8rpx;
  
  .star-item {
    .star {
      font-size: 48rpx;
      color: #ddd;
      
      &.active {
        color: #FFD700;
      }
    }
  }
  
  .difficulty-text {
    margin-left: 16rpx;
    font-size: 26rpx;
    color: #667eea;
    font-weight: bold;
  }
}

.day-plan {
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  
  .day-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .day-number {
      font-size: 30rpx;
      font-weight: bold;
      color: #667eea;
    }
    
    .day-date {
      font-size: 24rpx;
      color: #999;
    }
    
    .day-remove {
      font-size: 26rpx;
      color: #ff6b6b;
    }
  }
  
  .day-content {
    .plan-item {
      margin-bottom: 16rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.add-day-btn, .add-cost-btn {
  padding: 24rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-top: 20rpx;
  
  .add-icon {
    font-size: 32rpx;
    color: #667eea;
    font-weight: bold;
  }
  
  .add-text {
    font-size: 28rpx;
    color: #667eea;
  }
}

.cost-item {
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  
  .cost-header {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 16rpx;
    
    .cost-type-label {
      font-size: 26rpx;
      color: #666;
    }
    
    .cost-remove {
      margin-left: auto;
      font-size: 26rpx;
      color: #ff6b6b;
    }
  }
  
  .cost-row {
    display: flex;
    gap: 16rpx;
    
    .cost-amount {
      flex: 1;
    }
    
    .cost-description {
      flex: 2;
    }
    
    .item-label-small {
      font-size: 24rpx;
      color: #666;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .form-input-small {
      width: 100%;
      padding: 16rpx;
      background: #fff;
      border-radius: 8rpx;
      font-size: 26rpx;
    }
  }
}

.total-cost {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 2px dashed #e0e0e0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  
  .total-label {
    font-size: 28rpx;
    color: #666;
    margin-right: 16rpx;
  }
  
  .total-amount {
    font-size: 36rpx;
    font-weight: bold;
    color: #ff6b6b;
  }
}

.tag-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  
  .tag-item {
    display: inline-block;
    padding: 8rpx 20rpx;
    font-size: 26rpx;
    color: #909399;
    background: #f5f7fa;
    border: none;
    border-radius: 8rpx;
    cursor: pointer;
    transition: all 0.2s;
    
    &.active {
      color: #606266;
      background: #e4e7ed;
    }
    
    &:active {
      background: #dcdfe6;
      color: #303133;
    }
  }
}

.upload-area {
  width: 100%;
  height: 300rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 2px dashed #ddd;
  
  .cover-image {
    width: 100%;
    height: 100%;
  }
  
  .upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12rpx;
    
    .upload-icon {
      font-size: 64rpx;
    }
    
    .upload-text {
      font-size: 26rpx;
      color: #666;
    }
    
    .upload-hint {
      font-size: 22rpx;
      color: #999;
    }
  }
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .footer-actions {
    display: flex;
    gap: 20rpx;
    
    .action-btn {
      flex: 1;
      padding: 24rpx;
      border-radius: 50rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12rpx;
      
      .btn-icon {
        font-size: 32rpx;
      }
      
      .btn-text {
        font-size: 28rpx;
        font-weight: bold;
      }
      
      &.save {
        background: linear-gradient(135deg, #4ECDC4 0%, #6BD9D1 100%);
        
        .btn-text, .btn-icon {
          color: #fff;
        }
      }
      
      &.publish {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        
        .btn-text, .btn-icon {
          color: #fff;
        }
      }
    }
  }
}
</style>
