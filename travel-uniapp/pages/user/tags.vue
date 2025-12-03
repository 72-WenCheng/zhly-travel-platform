<template>
  <view class="tags-page">
    <!-- 顶部说明 -->
    <view class="header">
      <text class="title">个人信息化标签</text>
      <text class="subtitle">完善标签，获取更精准的个性化推荐</text>
    </view>

    <!-- 用户关系标签 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">出行方式</text>
        <text class="section-desc">请选择您的出行方式</text>
      </view>
      <view class="tags-container">
        <view 
          v-for="tag in userTypeTags" 
          :key="tag.id"
          class="tag-item"
          :class="{ active: selectedUserType === tag.id }"
          @click="selectUserType(tag.id)"
        >
          <text class="tag-icon">{{ tag.icon }}</text>
          <text class="tag-text">{{ tag.name }}</text>
          <text class="tag-desc">{{ tag.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 旅游偏好 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">旅游偏好</text>
        <text class="section-desc">选择您的消费水平偏好</text>
      </view>
      <view class="tags-container">
        <view 
          v-for="tag in travelPreferenceTags" 
          :key="tag.id"
          class="tag-item"
          :class="{ active: selectedPreference === tag.id }"
          @click="selectPreference(tag.id)"
        >
          <text class="tag-icon">{{ tag.icon }}</text>
          <text class="tag-text">{{ tag.name }}</text>
          <text class="tag-desc">{{ tag.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 兴趣爱好（多选） -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">兴趣爱好</text>
        <text class="section-desc">选择您感兴趣的标签（可多选）</text>
      </view>
      <view class="tags-grid">
        <view 
          v-for="tag in interestTags" 
          :key="tag.id"
          class="tag-chip"
          :class="{ active: selectedInterests.includes(tag.id) }"
          @click="toggleInterest(tag.id)"
        >
          <text class="chip-text">{{ tag.name }}</text>
        </view>
      </view>
    </view>

    <!-- 常去城市 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">常去城市</text>
        <text class="section-desc">选择您常去的城市（可多选）</text>
      </view>
      <view class="tags-grid">
        <view 
          v-for="city in popularCities" 
          :key="city.id"
          class="tag-chip"
          :class="{ active: selectedCities.includes(city.id) }"
          @click="toggleCity(city.id)"
        >
          <text class="chip-text">{{ city.name }}</text>
        </view>
      </view>
      <view class="add-city" @click="showAddCity = true">
        <text class="add-icon">+</text>
        <text class="add-text">添加自定义城市</text>
      </view>
    </view>

    <!-- 当前标签展示 -->
    <view class="section" v-if="hasSelectedTags">
      <view class="section-header">
        <text class="section-title">我的标签</text>
        <text class="section-desc">基于这些标签为您推荐内容</text>
      </view>
      <view class="my-tags">
        <view 
          v-for="(tag, index) in myTags" 
          :key="index"
          class="my-tag"
        >
          <text class="my-tag-text">{{ tag }}</text>
        </view>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="footer">
      <view class="save-btn" @click="saveTags">
        <text class="save-text">保存标签</text>
      </view>
    </view>

    <!-- 添加城市弹窗 -->
    <view v-if="showAddCity" class="modal" @click="showAddCity = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">添加城市</text>
          <text class="modal-close" @click="showAddCity = false">✕</text>
        </view>
        <view class="modal-body">
          <input 
            v-model="customCity" 
            class="city-input" 
            placeholder="请输入城市名称"
            maxlength="10"
          />
        </view>
        <view class="modal-footer">
          <view class="modal-btn cancel" @click="showAddCity = false">取消</view>
          <view class="modal-btn confirm" @click="addCustomCity">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import request from '@/utils/request'

// 添加城市
const showAddCity = ref(false)
const customCity = ref('')

// 选中的标签
const selectedUserType = ref(0)
const selectedPreference = ref(0)
const selectedInterests = ref<number[]>([])
const selectedCities = ref<number[]>([])

// 用户关系标签
const userTypeTags = ref([
  {
    id: 1,
    name: '个人',
    icon: '👤',
    desc: '独自旅行'
  },
  {
    id: 2,
    name: '情侣',
    icon: '💑',
    desc: '情侣出行'
  },
  {
    id: 3,
    name: '家庭',
    icon: '👨‍👩‍👧‍👦',
    desc: '家庭出游'
  },
  {
    id: 4,
    name: '团队',
    icon: '👥',
    desc: '团队建设'
  }
])

// 旅游偏好标签
const travelPreferenceTags = ref([
  {
    id: 1,
    name: '穷游',
    icon: '💰',
    desc: '经济实惠'
  },
  {
    id: 2,
    name: '富游',
    icon: '💎',
    desc: '高端享受'
  },
  {
    id: 3,
    name: '中等消费',
    icon: '💳',
    desc: '性价比'
  }
])

// 兴趣爱好标签
const interestTags = ref([
  { id: 1, name: '摄影' },
  { id: 2, name: '美食' },
  { id: 3, name: '自然风光' },
  { id: 4, name: '历史文化' },
  { id: 5, name: '音乐' },
  { id: 6, name: '艺术' },
  { id: 7, name: '运动' },
  { id: 8, name: '购物' },
  { id: 9, name: '夜生活' },
  { id: 10, name: '博物馆' },
  { id: 11, name: '户外探险' },
  { id: 12, name: '温泉SPA' }
])

// 热门城市
const popularCities = ref([
  { id: 1, name: '重庆' },
  { id: 2, name: '成都' },
  { id: 3, name: '西安' },
  { id: 4, name: '北京' },
  { id: 5, name: '上海' },
  { id: 6, name: '广州' },
  { id: 7, name: '深圳' },
  { id: 8, name: '杭州' },
  { id: 9, name: '南京' },
  { id: 10, name: '厦门' },
  { id: 11, name: '三亚' },
  { id: 12, name: '丽江' }
])

// 是否选择了标签
const hasSelectedTags = computed(() => {
  return selectedUserType > 0 || selectedPreference > 0 || selectedInterests.length > 0 || selectedCities.length > 0
})

// 我的标签汇总
const myTags = computed(() => {
  const tags: string[] = []
  
  if (selectedUserType > 0) {
    const userType = userTypeTags.value.find(t => t.id === selectedUserType)
    if (userType) tags.push(userType.name)
  }
  
  if (selectedPreference > 0) {
    const preference = travelPreferenceTags.value.find(t => t.id === selectedPreference)
    if (preference) tags.push(preference.name)
  }
  
  selectedInterests.value.forEach(id => {
    const interest = interestTags.value.find(t => t.id === id)
    if (interest) tags.push(interest.name)
  })
  
  selectedCities.value.forEach(id => {
    const city = popularCities.value.find(t => t.id === id)
    if (city) tags.push(city.name)
  })
  
  return tags
})

// 选择用户类型
const selectUserType = (id: number) => {
  selectedUserType.value = id
}

// 选择旅游偏好
const selectPreference = (id: number) => {
  selectedPreference.value = id
}

// 切换兴趣爱好
const toggleInterest = (id: number) => {
  const index = selectedInterests.value.indexOf(id)
  if (index > -1) {
    selectedInterests.value.splice(index, 1)
  } else {
    selectedInterests.value.push(id)
  }
}

// 切换城市
const toggleCity = (id: number) => {
  const index = selectedCities.value.indexOf(id)
  if (index > -1) {
    selectedCities.value.splice(index, 1)
  } else {
    selectedCities.value.push(id)
  }
}

// 添加自定义城市
const addCustomCity = () => {
  if (customCity.value.trim()) {
    // TODO: 添加到自定义城市列表
    uni.showToast({
      title: '城市已添加',
      icon: 'success'
    })
    customCity.value = ''
    showAddCity.value = false
  } else {
    uni.showToast({
      title: '请输入城市名称',
      icon: 'none'
    })
  }
}

// 保存标签
const saveTags = async () => {
  if (!hasSelectedTags.value) {
    uni.showToast({
      title: '请至少选择一个标签',
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
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/user/login'
        })
      }, 1500)
      return
    }
    
    const tagsData = {
      userType: selectedUserType.value,
      travelPreference: selectedPreference.value,
      interestTags: selectedInterests.value,
      frequentCities: selectedCities.value.map(id => {
        const city = popularCities.value.find(c => c.id === id)
        return city?.name || ''
      }).filter(Boolean)
    }
    
    // TODO: 调用API保存标签
    // await request.put('/user/tags', tagsData)
    
    uni.showToast({
      title: '标签保存成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存标签失败:', error)
    uni.showToast({
      title: '保存失败，请重试',
      icon: 'none'
    })
  }
}

// 加载用户标签
const loadUserTags = async () => {
  try {
    const token = uni.getStorageSync('token')
    if (!token) return
    
    // TODO: 调用API获取用户标签
    // const result = await request.get('/user/tags')
    // if (result.code === 200 && result.data) {
    //   selectedUserType.value = result.data.userType || 0
    //   selectedPreference.value = result.data.travelPreference || 0
    //   selectedInterests.value = result.data.interestTags || []
    //   selectedCities.value = result.data.frequentCities || []
    // }
  } catch (error) {
    console.error('加载用户标签失败:', error)
  }
}

// 页面加载时获取用户标签
uni.use((params: any) => {
  loadUserTags()
})
</script>

<style lang="scss" scoped>
.tags-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding-bottom: 120rpx;
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

.section {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .section-header {
    margin-bottom: 24rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .section-desc {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.tags-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  
  .tag-item {
    padding: 30rpx 20rpx;
    background: #f5f5f5;
    border-radius: 16rpx;
    text-align: center;
    border: 2px solid transparent;
    transition: all 0.3s;
    
    .tag-icon {
      font-size: 48rpx;
      display: block;
      margin-bottom: 12rpx;
    }
    
    .tag-text {
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .tag-desc {
      font-size: 22rpx;
      color: #999;
    }
    
    &.active {
      background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
      border-color: #667eea;
      
      .tag-text {
        color: #667eea;
      }
    }
  }
}

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  
  .tag-chip {
    padding: 16rpx 32rpx;
    background: #f5f5f5;
    border-radius: 50rpx;
    border: 2px solid transparent;
    
    .chip-text {
      font-size: 26rpx;
      color: #666;
    }
    
    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-color: #667eea;
      
      .chip-text {
        color: #fff;
        font-weight: bold;
      }
    }
  }
}

.add-city {
  margin-top: 20rpx;
  padding: 24rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  
  .add-icon {
    font-size: 32rpx;
    color: #667eea;
    font-weight: bold;
  }
  
  .add-text {
    font-size: 26rpx;
    color: #667eea;
  }
}

.my-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  
  .my-tag {
    padding: 12rpx 24rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50rpx;
    
    .my-tag-text {
      font-size: 24rpx;
      color: #fff;
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
  
  .save-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50rpx;
    padding: 28rpx;
    text-align: center;
    
    .save-text {
      font-size: 32rpx;
      font-weight: bold;
      color: #fff;
    }
  }
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .modal-content {
    background: #fff;
    border-radius: 24rpx;
    width: 80%;
    max-width: 600rpx;
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx;
      border-bottom: 1px solid #f0f0f0;
      
      .modal-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
      
      .modal-close {
        font-size: 40rpx;
        color: #999;
      }
    }
    
    .modal-body {
      padding: 30rpx;
      
      .city-input {
        width: 100%;
        padding: 20rpx;
        background: #f5f5f5;
        border-radius: 12rpx;
        font-size: 28rpx;
      }
    }
    
    .modal-footer {
      display: flex;
      gap: 20rpx;
      padding: 30rpx;
      border-top: 1px solid #f0f0f0;
      
      .modal-btn {
        flex: 1;
        padding: 24rpx;
        text-align: center;
        border-radius: 50rpx;
        font-size: 28rpx;
        
        &.cancel {
          background: #f5f5f5;
          color: #666;
        }
        
        &.confirm {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;
        }
      }
    }
  }
}
</style>
