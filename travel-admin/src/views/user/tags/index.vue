<template>
  <div class="user-tags">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>个性标签</h2>
      <p class="page-desc">设置您的个性化标签，获得更精准的推荐</p>
    </div>

    <!-- 兴趣标签 -->
    <el-card class="tags-card">
      <template #header>
        <div class="card-header">
          <span>兴趣标签</span>
          <el-tag type="info">多选</el-tag>
        </div>
      </template>
      
      <div class="tags-container">
        <el-tag
          v-for="tag in interestTags"
          :key="tag.value"
          :type="selectedInterests.includes(tag.value) ? 'success' : ''"
          :effect="selectedInterests.includes(tag.value) ? 'dark' : 'plain'"
          class="tag-item"
          @click="toggleInterest(tag.value)"
        >
          {{ tag.label }}
        </el-tag>
      </div>
    </el-card>

    <!-- 旅游偏好 -->
    <el-card class="tags-card">
      <template #header>
        <div class="card-header">
          <span>旅游偏好</span>
          <el-tag type="warning">单选</el-tag>
        </div>
      </template>
      
      <div class="tags-container preference-grid">
        <div
          v-for="preference in travelPreferences"
          :key="preference.value"
          :class="['preference-card', { 'active': selectedTravelPreference === preference.value }]"
          @click="selectTravelPreference(preference.value)"
        >
          <div class="preference-icon">{{ preference.label.split(' ')[0] }}</div>
          <div class="preference-label">{{ preference.label.split(' ').slice(1).join(' ') }}</div>
          <div class="preference-desc">{{ preference.desc }}</div>
        </div>
      </div>
    </el-card>

    <!-- 消费水平 -->
    <el-card class="tags-card">
      <template #header>
        <div class="card-header">
          <span>消费水平</span>
          <el-tag type="danger">单选</el-tag>
        </div>
      </template>
      
      <div class="tags-container budget-grid">
        <div
          v-for="budget in budgetLevels"
          :key="budget.value"
          :class="['budget-card', { 'active': selectedBudget === budget.value }]"
          @click="selectBudget(budget.value)"
        >
          <div class="budget-icon">{{ budget.label.split(' ')[0] }}</div>
          <div class="budget-label">{{ budget.label.split(' ').slice(1).join(' ') }}</div>
          <div class="budget-desc">{{ budget.desc }}</div>
        </div>
      </div>
    </el-card>

    <!-- 出行方式 -->
    <el-card class="tags-card">
      <template #header>
        <div class="card-header">
          <span>出行方式</span>
          <el-tag type="info">多选</el-tag>
        </div>
      </template>
      
      <div class="tags-container method-grid">
        <div
          v-for="method in travelMethods"
          :key="method.value"
          :class="['method-card', { 'active': selectedTravelMethods.includes(method.value) }]"
          @click="toggleTravelMethod(method.value)"
        >
          <div class="method-icon">{{ method.label.split(' ')[0] }}</div>
          <div class="method-label">{{ method.label.split(' ').slice(1).join(' ') }}</div>
          <div class="method-desc">{{ method.desc }}</div>
        </div>
      </div>
    </el-card>

    <!-- 保存按钮 -->
    <div class="save-container">
      <el-button type="primary" size="large" @click="saveTags" :loading="saving">
        <el-icon><Check /></el-icon>
        保存标签
      </el-button>
      <el-button size="large" @click="resetTags">重置</el-button>
    </div>

    <!-- 标签说明 -->
    <el-card class="info-card">
      <template #header>
        <span>标签说明</span>
      </template>
      <div class="info-content">
        <p><strong>兴趣标签：</strong>选择您感兴趣的内容类型，系统会根据您的兴趣推荐相关内容。</p>
        <p><strong>旅游偏好：</strong>选择您最喜欢的旅游类型，影响推荐的目的地和活动。</p>
        <p><strong>消费水平：</strong>选择您的消费能力，系统会推荐相应价位的产品和服务。</p>
        <p><strong>出行方式：</strong>选择您常用的出行方式，影响交通和住宿推荐。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'

// 兴趣标签
const interestTags = ref([
  { label: '🍜 美食', value: 'food', icon: '🍜' },
  { label: '🏛️ 文化', value: 'culture', icon: '🏛️' },
  { label: '🏔️ 自然风光', value: 'nature', icon: '🏔️' },
  { label: '📜 历史古迹', value: 'history', icon: '📜' },
  { label: '🎨 艺术', value: 'art', icon: '🎨' },
  { label: '⚽ 运动健身', value: 'sports', icon: '⚽' },
  { label: '🛍️ 购物', value: 'shopping', icon: '🛍️' },
  { label: '🎭 娱乐', value: 'entertainment', icon: '🎭' },
  { label: '📷 摄影', value: 'photography', icon: '📷' },
  { label: '🎵 音乐', value: 'music', icon: '🎵' },
  { label: '🎪 民俗体验', value: 'folklore', icon: '🎪' },
  { label: '🏖️ 海滩度假', value: 'beach', icon: '🏖️' },
  { label: '⛰️ 登山徒步', value: 'hiking', icon: '⛰️' },
  { label: '🏛️ 博物馆', value: 'museum', icon: '🏛️' },
  { label: '🎢 主题乐园', value: 'theme-park', icon: '🎢' },
  { label: '🌸 赏花', value: 'flower', icon: '🌸' },
  { label: '🍷 品酒', value: 'wine', icon: '🍷' },
  { label: '🧘 养生', value: 'wellness', icon: '🧘' }
])

// 旅游偏好
const travelPreferences = ref([
  { label: '🌿 自然风光', value: 'nature', desc: '山川、湖泊、森林等自然景观' },
  { label: '🏛️ 人文历史', value: 'culture', desc: '古建筑、历史遗迹、文化景点' },
  { label: '🍽️ 美食体验', value: 'food', desc: '品尝当地特色美食' },
  { label: '🎭 文旅对接', value: 'cultural-tourism', desc: '非遗体验、民俗活动' },
  { label: '🏖️ 休闲度假', value: 'relax', desc: '海滨、温泉、度假村' },
  { label: '🧗 冒险探索', value: 'adventure', desc: '户外运动、探险活动' },
  { label: '📸 网红打卡', value: 'popular', desc: '热门景点、拍照圣地' },
  { label: '🏙️ 城市观光', value: 'city', desc: '都市风光、现代建筑' }
])

// 消费水平
const budgetLevels = ref([
  { label: '💰 经济实惠', value: 'budget', desc: '人均 ¥200-500/天' },
  { label: '💳 品质舒适', value: 'comfort', desc: '人均 ¥500-1500/天' },
  { label: '💎 豪华尊享', value: 'luxury', desc: '人均 ¥1500+/天' }
])

// 出行方式
const travelMethods = ref([
  { label: '🎒 自由行', value: 'independent', desc: '自己规划行程' },
  { label: '👥 跟团游', value: 'group', desc: '跟随旅行团' },
  { label: '🚗 自驾游', value: 'self-drive', desc: '自己开车出行' },
  { label: '🎒 背包客', value: 'backpacker', desc: '穷游、青旅' },
  { label: '👨‍👩‍👧‍👦 家庭出游', value: 'family', desc: '带家人旅游' },
  { label: '💑 情侣出行', value: 'couple', desc: '两人浪漫之旅' },
  { label: '🤝 朋友结伴', value: 'friends', desc: '多人一起旅行' },
  { label: '💼 商务出行', value: 'business', desc: '出差兼旅游' }
])

// 选中的标签
const selectedInterests = ref(['food', 'culture'])
const selectedTravelPreference = ref('nature')
const selectedBudget = ref('comfort')
const selectedTravelMethods = ref(['independent', 'self-drive'])

// 保存状态
const saving = ref(false)

// 切换兴趣标签
const toggleInterest = (value: string) => {
  const index = selectedInterests.value.indexOf(value)
  if (index > -1) {
    selectedInterests.value.splice(index, 1)
  } else {
    selectedInterests.value.push(value)
  }
}

// 选择旅游偏好
const selectTravelPreference = (value: string) => {
  selectedTravelPreference.value = value
}

// 选择消费水平
const selectBudget = (value: string) => {
  selectedBudget.value = value
}

// 切换出行方式
const toggleTravelMethod = (value: string) => {
  const index = selectedTravelMethods.value.indexOf(value)
  if (index > -1) {
    selectedTravelMethods.value.splice(index, 1)
  } else {
    selectedTravelMethods.value.push(value)
  }
}

// 保存标签
const saveTags = async () => {
  try {
    saving.value = true
    
    // TODO: 调用API保存标签
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('标签保存成功！')
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 重置标签
const resetTags = () => {
  selectedInterests.value = []
  selectedTravelPreference.value = ''
  selectedBudget.value = ''
  selectedTravelMethods.value = []
  ElMessage.info('标签已重置')
}

onMounted(() => {
  loadUserTags()
})

const loadUserTags = async () => {
  // TODO: 加载用户标签
  console.log('加载用户标签')
}
</script>

<style lang="scss" scoped>
.user-tags {
  .page-header {
    margin-bottom: 30px;
    padding: 30px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.2);
    
    h2 {
      margin: 0 0 10px 0;
      color: white;
      font-size: 32px;
      font-weight: 700;
    }
    
    .page-desc {
      margin: 0;
      color: rgba(255, 255, 255, 0.9);
      font-size: 16px;
      line-height: 1.6;
    }
  }
  
  .tags-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
    }
    
    .tags-container {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      
      .tag-item {
        cursor: pointer;
        transition: all 0.3s;
        padding: 10px 20px;
        font-size: 15px;
        border-radius: 8px;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
      }
    }
    
    // 旅游偏好卡片样式
    .preference-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      
      .preference-card {
        padding: 20px;
        background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
        border-radius: 16px;
        border: 2px solid #e4e7ed;
        cursor: pointer;
        transition: all 0.3s ease;
        text-align: center;
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
          border-color: #667eea;
        }
        
        &.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-color: #667eea;
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
          
          .preference-icon {
            transform: scale(1.2);
          }
          
          .preference-label {
            color: white;
          }
          
          .preference-desc {
            color: rgba(255, 255, 255, 0.9);
          }
        }
        
        .preference-icon {
          font-size: 36px;
          margin-bottom: 12px;
          transition: all 0.3s ease;
        }
        
        .preference-label {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 8px;
          transition: all 0.3s ease;
        }
        
        .preference-desc {
          font-size: 13px;
          color: #909399;
          line-height: 1.4;
          transition: all 0.3s ease;
        }
      }
    }
    
    // 消费水平卡片样式
    .budget-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
      
      .budget-card {
        padding: 24px;
        background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
        border-radius: 16px;
        border: 2px solid #e4e7ed;
        cursor: pointer;
        transition: all 0.3s ease;
        text-align: center;
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
          border-color: #667eea;
        }
        
        &.active {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
          border-color: #fa709a;
          box-shadow: 0 8px 24px rgba(250, 112, 154, 0.3);
          
          .budget-icon {
            transform: scale(1.2);
          }
          
          .budget-label,
          .budget-desc {
            color: white;
          }
        }
        
        .budget-icon {
          font-size: 48px;
          margin-bottom: 16px;
          transition: all 0.3s ease;
        }
        
        .budget-label {
          font-size: 18px;
          font-weight: 700;
          color: #303133;
          margin-bottom: 12px;
          transition: all 0.3s ease;
        }
        
        .budget-desc {
          font-size: 14px;
          color: #909399;
          line-height: 1.4;
          transition: all 0.3s ease;
        }
      }
    }
    
    // 出行方式卡片样式
    .method-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      
      .method-card {
        padding: 20px;
        background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
        border-radius: 16px;
        border: 2px solid #e4e7ed;
        cursor: pointer;
        transition: all 0.3s ease;
        text-align: center;
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
          border-color: #667eea;
        }
        
        &.active {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          border-color: #4facfe;
          box-shadow: 0 8px 24px rgba(79, 172, 254, 0.3);
          
          .method-icon {
            transform: scale(1.2);
          }
          
          .method-label {
            color: white;
          }
          
          .method-desc {
            color: rgba(255, 255, 255, 0.9);
          }
        }
        
        .method-icon {
          font-size: 36px;
          margin-bottom: 12px;
          transition: all 0.3s ease;
        }
        
        .method-label {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 8px;
          transition: all 0.3s ease;
        }
        
        .method-desc {
          font-size: 13px;
          color: #909399;
          line-height: 1.4;
          transition: all 0.3s ease;
        }
      }
    }
  }
  
  .save-container {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin: 40px 0;
    
    .el-button {
      padding: 16px 48px;
      font-size: 16px;
      font-weight: 600;
      border-radius: 50px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
      }
    }
  }
  
  .info-card {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    border: 1px solid rgba(102, 126, 234, 0.1);
    
    .info-content {
      p {
        margin: 0 0 12px 0;
        color: #606266;
        line-height: 1.8;
        font-size: 14px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        strong {
          color: #667eea;
          font-weight: 600;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .user-tags {
    .tags-card {
      .preference-grid,
      .method-grid {
        grid-template-columns: repeat(3, 1fr);
      }
    }
  }
}

@media (max-width: 992px) {
  .user-tags {
    .page-header {
      padding: 24px;
      
      h2 {
        font-size: 28px;
      }
      
      .page-desc {
        font-size: 14px;
      }
    }
    
    .tags-card {
      .preference-grid,
      .method-grid {
        grid-template-columns: repeat(2, 1fr);
      }
      
      .budget-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}

@media (max-width: 768px) {
  .user-tags {
    .page-header {
      padding: 20px;
      margin-bottom: 20px;
      
      h2 {
        font-size: 24px;
      }
      
      .page-desc {
        font-size: 13px;
      }
    }
    
    .tags-card {
      .card-header {
        font-size: 14px;
      }
      
      .tags-container .tag-item {
        padding: 8px 16px;
        font-size: 14px;
      }
      
      .preference-grid,
      .method-grid,
      .budget-grid {
        grid-template-columns: 1fr;
        gap: 12px;
      }
      
      .preference-card,
      .method-card,
      .budget-card {
        padding: 16px;
      }
    }
    
    .save-container {
      flex-direction: column;
      margin: 30px 0;
      
      .el-button {
        width: 100%;
        padding: 14px 32px;
        font-size: 15px;
      }
    }
  }
}
</style>















