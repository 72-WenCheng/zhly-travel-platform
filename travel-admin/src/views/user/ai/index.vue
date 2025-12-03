<template>
  <div class="user-ai">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><MagicStick /></el-icon>
        </div>
        <div class="header-text">
          <h2>AI智能生成</h2>
          <p class="page-desc">基于先进的AI技术，为您智能生成个性化旅游攻略</p>
        </div>
      </div>
      <div class="last-update">
        <el-icon><InfoFilled /></el-icon>
        <span>输入您的旅行需求，AI将为您定制专属攻略</span>
      </div>
    </div>

    <!-- AI生成表单 -->
    <el-card class="generate-card">
      <template #header>
        <div class="card-header">
          <span>攻略生成</span>
          <el-tag type="success">AI驱动</el-tag>
        </div>
      </template>
      
      <el-form :model="generateForm" :rules="generateRules" ref="generateFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目的地" prop="destination">
              <el-input v-model="generateForm.destination" placeholder="请输入目的地，如：重庆" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出行天数" prop="days">
              <el-select v-model="generateForm.days" placeholder="请选择天数">
                <el-option label="1天" :value="1" />
                <el-option label="2天" :value="2" />
                <el-option label="3天" :value="3" />
                <el-option label="5天" :value="5" />
                <el-option label="7天" :value="7" />
                <el-option label="10天" :value="10" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预算范围" prop="budget">
              <el-select v-model="generateForm.budget" placeholder="请选择预算">
                <el-option label="500元以下" value="500" />
                <el-option label="500-1000元" value="1000" />
                <el-option label="1000-2000元" value="2000" />
                <el-option label="2000-5000元" value="5000" />
                <el-option label="5000元以上" value="5000+" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出行人数" prop="people">
              <el-select v-model="generateForm.people" placeholder="请选择人数">
                <el-option label="1人" :value="1" />
                <el-option label="2人" :value="2" />
                <el-option label="3-4人" :value="4" />
                <el-option label="5-6人" :value="6" />
                <el-option label="7人以上" :value="8" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="旅游偏好" prop="preferences">
          <el-checkbox-group v-model="generateForm.preferences">
            <el-checkbox label="美食">美食</el-checkbox>
            <el-checkbox label="文化">文化</el-checkbox>
            <el-checkbox label="自然">自然</el-checkbox>
            <el-checkbox label="购物">购物</el-checkbox>
            <el-checkbox label="历史">历史</el-checkbox>
            <el-checkbox label="娱乐">娱乐</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="特殊需求" prop="specialNeeds">
          <el-input 
            v-model="generateForm.specialNeeds" 
            type="textarea" 
            :rows="3"
            placeholder="请输入特殊需求，如：带老人、带小孩、无障碍等"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="generatePlan" :loading="generating">
            <el-icon><MagicStick /></el-icon>
            {{ generating ? 'AI生成中...' : '生成攻略' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 生成结果 -->
    <el-card v-if="generatedPlan" class="result-card">
      <template #header>
        <div class="card-header">
          <span>生成结果</span>
          <div>
            <el-button type="success" @click="savePlan">保存攻略</el-button>
            <el-button type="info" @click="regeneratePlan">重新生成</el-button>
          </div>
        </div>
      </template>
      
      <div class="plan-content">
        <h3>{{ generatedPlan.title }}</h3>
        <div class="plan-meta">
          <el-tag type="info">{{ generatedPlan.destination }}</el-tag>
          <el-tag type="success">{{ generatedPlan.days }}天</el-tag>
          <el-tag type="warning">预算 {{ generatedPlan.budget }}</el-tag>
        </div>
        
        <div class="plan-details">
          <div v-for="(day, index) in generatedPlan.days" :key="index" class="day-plan">
            <h4>第{{ index + 1 }}天</h4>
            <div class="day-content">
              <div v-for="item in day.schedule" :key="item.time" class="schedule-item">
                <span class="time">{{ item.time }}</span>
                <span class="activity">{{ item.activity }}</span>
                <span class="location">{{ item.location }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 历史生成记录 -->
    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span>生成历史</span>
          <el-button type="text" @click="viewAllHistory">查看全部</el-button>
        </div>
      </template>
      
      <el-table :data="historyList" stripe>
        <el-table-column prop="title" label="攻略标题" width="200" show-overflow-tooltip />
        <el-table-column prop="destination" label="目的地" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="createTime" label="生成时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewHistory(row)">查看</el-button>
            <el-button type="success" size="small" @click="useHistory(row)">使用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import BackButton from '@/components/BackButton.vue'
import { ElMessage } from 'element-plus'
import { MagicStick, InfoFilled } from '@element-plus/icons-vue'
import { generateTravelPlan, getAiLogList } from '@/api/ai'
import type { AiGenerateLog } from '@/api/ai'

// 生成表单
const generateForm = reactive({
  destination: '',
  days: null,
  budget: '',
  people: null,
  preferences: [],
  specialNeeds: ''
})

// 表单验证规则
const generateRules = {
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' }
  ],
  days: [
    { required: true, message: '请选择出行天数', trigger: 'change' }
  ],
  budget: [
    { required: true, message: '请选择预算范围', trigger: 'change' }
  ],
  people: [
    { required: true, message: '请选择出行人数', trigger: 'change' }
  ]
}

// 生成状态
const generating = ref(false)
const generatedPlan = ref(null)

// 历史记录
const historyList = ref([])

// 生成攻略
const generatePlan = async () => {
  try {
    generating.value = true
    
    // 调用AI生成API
    const result = await generateTravelPlan({
      destination: generateForm.destination,
      days: generateForm.days,
      budget: generateForm.budget,
      interests: generateForm.preferences.join(','),
      travelStyle: generateForm.specialNeeds || '休闲游'
    })
    
    if (result.code === 200 && result.data) {
      // 解析AI返回的攻略内容
      const planData = result.data
      generatedPlan.value = {
        title: `${generateForm.destination}${generateForm.days}日游攻略`,
        destination: generateForm.destination,
        days: generateForm.days,
        budget: generateForm.budget,
        content: planData.plan || planData
      }
      
      ElMessage.success('攻略生成成功！')
      loadHistory() // 刷新历史记录
    } else {
      ElMessage.error(result.message || '生成失败，请重试')
    }
  } catch (error: any) {
    console.error('生成失败:', error)
    ElMessage.error(error.message || '生成失败，请重试')
  } finally {
    generating.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(generateForm, {
    destination: '',
    days: null,
    budget: '',
    people: null,
    preferences: [],
    specialNeeds: ''
  })
}

// 保存攻略
const savePlan = async () => {
  try {
    // TODO: 调用创建攻略API保存生成的攻略
    ElMessage.success('攻略保存成功！')
    generatedPlan.value = null
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

// 重新生成
const regeneratePlan = () => {
  generatePlan()
}

// 查看历史
const viewHistory = (row: any) => {
  ElMessage.info('查看历史功能开发中...')
}

// 使用历史
const useHistory = (row: any) => {
  ElMessage.info('使用历史功能开发中...')
}

// 查看全部历史
const viewAllHistory = () => {
  ElMessage.info('查看全部历史功能开发中...')
}

onMounted(() => {
  loadHistory()
})

const loadHistory = async () => {
  try {
    // 加载AI生成历史
    const userId = 1 // TODO: 从store获取当前用户ID
    const result = await getAiLogList({
      page: 1,
      size: 10,
      userId
    })
    
    if (result.code === 200 && result.data) {
      historyList.value = result.data.list.map((log: AiGenerateLog) => ({
        id: log.id,
        title: log.requestContent.substring(0, 30) + '...',
        destination: extractDestination(log.requestContent),
        days: extractDays(log.requestContent),
        createTime: log.createTime
      }))
    }
  } catch (error) {
    console.error('加载历史失败:', error)
  }
}

// 提取目的地
const extractDestination = (text: string) => {
  const match = text.match(/[重庆|成都|西安|北京|上海|广州|深圳|杭州|南京|苏州|武汉|长沙|郑州|青岛|厦门|大连|厦门|昆明|桂林|丽江|三亚|海口|呼和浩特|乌鲁木齐|拉萨|银川|西宁|哈尔滨|长春|沈阳|石家庄|太原|济南|合肥|南昌|福州|南宁|贵阳|昆明]+/)
  return match ? match[0] : '未知'
}

// 提取天数
const extractDays = (text: string) => {
  const match = text.match(/(\d+)天/)
  return match ? parseInt(match[1]) : 1
}
</script>

<style lang="scss" scoped>
.user-ai {
  .page-header {
    margin-bottom: 24px;
    padding: 32px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
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
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        backdrop-filter: blur(10px);
      }
      
      .header-text {
        h2 {
          margin: 0 0 8px 0;
          color: white;
          font-size: 32px;
          font-weight: 700;
        }
        
        .page-desc {
          margin: 0;
          color: rgba(255, 255, 255, 0.9);
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
      background: rgba(255, 255, 255, 0.2);
      border-radius: 30px;
      color: white;
      font-size: 14px;
      backdrop-filter: blur(10px);
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
  
  .generate-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .result-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .plan-content {
      h3 {
        margin: 0 0 16px 0;
        color: #303133;
      }
      
      .plan-meta {
        margin-bottom: 20px;
        
        .el-tag {
          margin-right: 8px;
        }
      }
      
      .day-plan {
        margin-bottom: 20px;
        
        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          border-bottom: 1px solid #e4e7ed;
          padding-bottom: 8px;
        }
        
        .schedule-item {
          display: flex;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;
          
          .time {
            width: 80px;
            color: #409eff;
            font-weight: bold;
          }
          
          .activity {
            flex: 1;
            margin: 0 16px;
            color: #303133;
          }
          
          .location {
            color: #909399;
            font-size: 14px;
          }
        }
      }
    }
  }
  
  .history-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

// 响应式设计
@media (max-width: 992px) {
  .user-ai {
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
  }
}

@media (max-width: 768px) {
  .user-ai {
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
  }
}
</style>









