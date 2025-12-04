<template>
  <div class="modern-profile-page">
    <!-- 个人资料卡片 -->
    <div class="profile-header-card">
      <div class="header-background"></div>
      <div class="profile-content">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.nickname?.charAt(0) }}
            </el-avatar>
            <el-button circle class="edit-avatar-btn" @click="editAvatar">
              <el-icon><Camera /></el-icon>
            </el-button>
          </div>
          <div class="user-info">
            <h2 class="user-name">{{ userInfo.nickname }}</h2>
            <p class="user-id">ID: {{ userInfo.id }}</p>
          </div>
        </div>
        
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.followers }}</div>
            <div class="stat-label">粉丝</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.following }}</div>
            <div class="stat-label">关注</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.plans }}</div>
            <div class="stat-label">攻略</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 等级与积分卡片 -->
    <el-row :gutter="20" class="cards-row">
      <el-col :span="12">
        <div class="level-card premium-card">
          <div class="card-header">
            <div class="card-title">
              <el-icon class="title-icon"><Trophy /></el-icon>
              <span>我的等级</span>
            </div>
            <el-button text type="primary" @click="viewLevelInfo">升级攻略</el-button>
          </div>
          
          <div class="level-main">
            <div class="level-badge-large" :style="{ 
              borderColor: pointsInfo.currentLevel?.levelColor,
              background: pointsInfo.currentLevel?.levelGradient || `linear-gradient(135deg, ${pointsInfo.currentLevel?.levelColor} 0%, ${pointsInfo.currentLevel?.levelColor} 100%)`
            }">
              <div class="level-icon-large">
                <el-icon :size="48"><Trophy /></el-icon>
              </div>
              <div class="level-name-large">
                {{ pointsInfo.currentLevel?.levelName }}
              </div>
              <div class="level-code">LV.{{ pointsInfo.userPoints?.levelCode }}</div>
            </div>
            
            <div class="level-progress-section">
              <div class="progress-header">
                <span class="current-points">{{ pointsInfo.userPoints?.totalPoints }} 积分</span>
                <span class="target-points" v-if="!pointsInfo.isMaxLevel">
                  / {{ pointsInfo.nextLevel?.requiredPoints }} 积分
                </span>
                <span class="max-level-badge" v-else>
                  <el-icon><Star /></el-icon>
                  已达最高等级
                </span>
              </div>
              <el-progress 
                v-if="!pointsInfo.isMaxLevel"
                :percentage="levelProgress" 
                :color="pointsInfo.currentLevel?.levelColor"
                :stroke-width="12"
                class="level-progress-bar"
              />
              <div class="needed-points" v-if="!pointsInfo.isMaxLevel">
                还需 <strong>{{ pointsInfo.neededPoints }}</strong> 积分升级
              </div>
            </div>
          </div>
          
          <div class="level-benefits">
            <h4 class="benefits-title">当前等级权益</h4>
            <div class="benefits-grid">
              <div class="benefit-item">
                <el-icon class="benefit-icon" :class="{ active: pointsInfo.currentLevel?.canPostPlan }">
                  <Document />
                </el-icon>
                <span>发布攻略</span>
                <span class="benefit-value">{{ pointsInfo.currentLevel?.canPostPlan ? pointsInfo.dailyPostLimit + '篇/天' : '不可用' }}</span>
              </div>
              <div class="benefit-item">
                <el-icon class="benefit-icon" :class="{ active: pointsInfo.currentLevel?.canComment }">
                  <ChatDotRound />
                </el-icon>
                <span>发表评论</span>
                <span class="benefit-value">{{ pointsInfo.currentLevel?.canComment ? pointsInfo.dailyCommentLimit + '条/天' : '不可用' }}</span>
              </div>
              <div class="benefit-item">
                <el-icon class="benefit-icon" :class="{ active: pointsInfo.currentLevel?.canViewPremium }">
                  <View />
                </el-icon>
                <span>精品内容</span>
                <span class="benefit-value">{{ pointsInfo.currentLevel?.canViewPremium ? '可查看' : '不可用' }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="points-card premium-card">
          <div class="card-header">
            <div class="card-title">
              <el-icon class="title-icon"><Coin /></el-icon>
              <span>我的积分</span>
            </div>
            <el-button text type="primary" @click="viewPointsLog">查看明细</el-button>
          </div>
          
          <div class="points-main">
            <div class="points-balance">
              <div class="balance-label">可用积分</div>
              <div class="balance-value">{{ pointsInfo.userPoints?.availablePoints || 0 }}</div>
              <div class="balance-total">总积分 {{ pointsInfo.userPoints?.totalPoints || 0 }}</div>
            </div>
            
            <div class="quick-actions">
              <el-button class="action-btn" @click="dailyCheckin">
                <el-icon><Clock /></el-icon>
                每日签到
              </el-button>
              <el-button class="action-btn" @click="inviteFriends">
                <el-icon><UserFilled /></el-icon>
                邀请好友
              </el-button>
            </div>
          </div>
          
          <div class="points-ways">
            <h4 class="ways-title">获取积分</h4>
            <div class="ways-list">
              <div class="way-item">
                <span class="way-name">每日登录</span>
                <span class="way-points">+5</span>
              </div>
              <div class="way-item">
                <span class="way-name">发布攻略</span>
                <span class="way-points">+20</span>
              </div>
              <div class="way-item">
                <span class="way-name">发表评论</span>
                <span class="way-points">+2</span>
              </div>
              <div class="way-item">
                <span class="way-name">获得点赞</span>
                <span class="way-points">+1</span>
              </div>
              <div class="way-item">
                <span class="way-name">完善资料</span>
                <span class="way-points">+10</span>
              </div>
              <div class="way-item">
                <span class="way-name">邀请好友</span>
                <span class="way-points">+50</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 今日行为统计 -->
    <div class="today-stats-card premium-card">
      <div class="card-header">
        <div class="card-title">
          <el-icon class="title-icon"><DataAnalysis /></el-icon>
          <span>今日行为</span>
        </div>
        <span class="today-date">{{ todayDate }}</span>
      </div>
      
      <div class="today-stats-grid">
        <div class="today-stat-item">
          <div class="stat-circle" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon :size="24"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日发布</div>
            <div class="stat-value">
              {{ pointsInfo.todayPosts || 0 }}
              <span class="stat-limit">/ {{ pointsInfo.dailyPostLimit }}</span>
            </div>
          </div>
          <el-progress 
            :percentage="getProgressPercentage(pointsInfo.todayPosts, pointsInfo.dailyPostLimit)" 
            :show-text="false"
            :stroke-width="4"
            color="#667eea"
          />
        </div>
        
        <div class="today-stat-item">
          <div class="stat-circle" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon :size="24"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日评论</div>
            <div class="stat-value">
              {{ pointsInfo.todayComments || 0 }}
              <span class="stat-limit">/ {{ pointsInfo.dailyCommentLimit }}</span>
            </div>
          </div>
          <el-progress 
            :percentage="getProgressPercentage(pointsInfo.todayComments, pointsInfo.dailyCommentLimit)" 
            :show-text="false"
            :stroke-width="4"
            color="#f5576c"
          />
        </div>
        
        <div class="today-stat-item">
          <div class="stat-circle" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon :size="24"><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日点赞</div>
            <div class="stat-value">{{ todayStats.likes }}</div>
          </div>
          <el-progress 
            :percentage="Math.min(100, (todayStats.likes / 50) * 100)" 
            :show-text="false"
            :stroke-width="4"
            color="#00f2fe"
          />
        </div>
        
        <div class="today-stat-item">
          <div class="stat-circle" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon :size="24"><Coin /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日获得积分</div>
            <div class="stat-value">+{{ todayStats.pointsEarned }}</div>
          </div>
          <el-progress 
            :percentage="Math.min(100, (todayStats.pointsEarned / 50) * 100)" 
            :show-text="false"
            :stroke-width="4"
            color="#43e97b"
          />
        </div>
      </div>
    </div>

    <!-- 等级路线图 -->
    <div class="level-roadmap-card premium-card">
      <div class="card-header">
        <div class="card-title">
          <el-icon class="title-icon"><Map /></el-icon>
          <span>升级路线</span>
        </div>
      </div>
      
      <div class="roadmap">
        <div 
          v-for="level in allLevels" 
          :key="level.levelCode" 
          class="roadmap-item"
          :class="{ 
            'current': level.levelCode === pointsInfo.userPoints?.levelCode,
            'completed': level.levelCode < pointsInfo.userPoints?.levelCode,
            'locked': level.levelCode > pointsInfo.userPoints?.levelCode
          }"
        >
          <div class="roadmap-icon" :style="{ background: level.levelGradient || level.levelColor }">
            <el-icon v-if="level.levelCode < pointsInfo.userPoints?.levelCode"><Check /></el-icon>
            <el-icon v-else-if="level.levelCode === pointsInfo.userPoints?.levelCode"><Star /></el-icon>
            <el-icon v-else><Lock /></el-icon>
          </div>
          <div class="roadmap-content">
            <div class="roadmap-level" :style="{ color: level.levelColor }">{{ level.levelName }}</div>
            <div class="roadmap-points">{{ level.requiredPoints }} 积分</div>
          </div>
          <div class="roadmap-line" v-if="level.levelCode < 6"></div>
        </div>
      </div>
    </div>

    <!-- 个人资料编辑 -->
    <div class="profile-edit-card premium-card">
      <div class="card-header">
        <div class="card-title">
          <el-icon class="title-icon"><User /></el-icon>
          <span>个人资料</span>
        </div>
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
      </div>
      
      <el-form :model="userInfo" label-width="100px" class="profile-form">
        <el-row :gutter="40">
          <el-col :span="12">
            <el-form-item label="用户昵称">
              <el-input v-model="userInfo.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="userInfo.gender">
                <el-radio :label="0">保密</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input-number v-model="userInfo.age" :min="0" :max="120" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱">
              <el-input v-model="userInfo.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Trophy, Coin, Camera, ArrowRight, Star, Document, ChatDotRound,
  View, Clock, UserFilled, DataAnalysis, Map, Check, Lock, User
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 用户信息
const userInfo = ref({
  id: 1,
  nickname: '旅行达人',
  avatar: '',
  gender: 0,
  age: 25,
  email: '',
  phone: ''
})

// 用户统计
const userStats = ref({
  followers: 128,
  following: 56,
  plans: 12
})

// 积分等级信息
const pointsInfo = ref({
  userPoints: {
    totalPoints: 1250,
    availablePoints: 1250,
    levelCode: 3
  },
  currentLevel: {
    levelName: '黄金游侠',
    levelColor: '#FFD700',
    canPostPlan: 1,
    canComment: 1,
    canViewPremium: 0
  },
  nextLevel: {
    levelName: '铂金旅者',
    requiredPoints: 2000
  },
  neededPoints: 750,
  todayPosts: 2,
  todayComments: 8,
  dailyPostLimit: 5,
  dailyCommentLimit: 30,
  isMaxLevel: false
})

// 今日统计
const todayStats = ref({
  likes: 15,
  pointsEarned: 35
})

// 所有等级（与升级指南保持一致）
const allLevels = ref([
  { levelCode: 1, levelName: '青铜旅行者', requiredPoints: 0, levelColor: '#8B7355', levelGradient: 'linear-gradient(135deg, #8B7355 0%, #6B5B4F 100%)' },
  { levelCode: 2, levelName: '白银探索者', requiredPoints: 100, levelColor: '#9CA3AF', levelGradient: 'linear-gradient(135deg, #9CA3AF 0%, #6B7280 100%)' },
  { levelCode: 3, levelName: '黄金游侠', requiredPoints: 500, levelColor: '#F59E0B', levelGradient: 'linear-gradient(135deg, #F59E0B 0%, #D97706 100%)' },
  { levelCode: 4, levelName: '铂金旅者', requiredPoints: 2000, levelColor: '#6366F1', levelGradient: 'linear-gradient(135deg, #6366F1 0%, #4F46E5 100%)' },
  { levelCode: 5, levelName: '钻石达人', requiredPoints: 5000, levelColor: '#EC4899', levelGradient: 'linear-gradient(135deg, #EC4899 0%, #DB2777 100%)' },
  { levelCode: 6, levelName: '王者导师', requiredPoints: 10000, levelColor: '#F97316', levelGradient: 'linear-gradient(135deg, #F97316 0%, #EA580C 100%)' }
])

// 今日日期
const todayDate = computed(() => {
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
})

// 等级进度
const levelProgress = computed(() => {
  if (pointsInfo.value.isMaxLevel) return 100
  const current = pointsInfo.value.userPoints?.totalPoints || 0
  const target = pointsInfo.value.nextLevel?.requiredPoints || 1
  return Math.min(100, (current / target) * 100)
})

// 方法
const getProgressPercentage = (current, limit) => {
  if (limit === 999) return 100 // 无限制
  return Math.min(100, (current / limit) * 100)
}

const editAvatar = () => {
  ElMessage.info('头像上传功能开发中')
}

const viewLevelInfo = () => {
  ElMessage.info('查看等级详情')
}

const viewPointsLog = () => {
  ElMessage.info('查看积分明细')
}

const dailyCheckin = () => {
  // TODO: 调用签到API
  ElMessage.success('签到成功，获得5积分！')
}

const inviteFriends = () => {
  ElMessage.info('邀请好友功能开发中')
}

const saveProfile = () => {
  ElMessage.success('保存成功')
}

onMounted(async () => {
  try {
    // 加载用户积分信息
    const userId = userInfo.value.id
    const res = await request.get('/user/points/my', { params: { userId } })
    if (res.code === 200) {
      pointsInfo.value = res.data
      // 确保使用正确的等级颜色和渐变（与升级指南一致）
      if (pointsInfo.value.userPoints?.levelCode) {
        const level = allLevels.value.find(l => l.levelCode === pointsInfo.value.userPoints.levelCode)
        if (level) {
          pointsInfo.value.currentLevel = {
            ...pointsInfo.value.currentLevel,
            levelColor: level.levelColor,
            levelGradient: level.levelGradient
          }
        }
      }
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
  }
})
</script>

<style lang="scss" scoped>
.modern-profile-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

// 个人资料卡片
.profile-header-card {
  position: relative;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  
  .header-background {
    height: 180px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  .profile-content {
    padding: 0 40px 30px;
    margin-top: -80px;
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    
    .avatar-section {
      display: flex;
      align-items: flex-end;
      gap: 24px;
      
      .avatar-wrapper {
        position: relative;
        
        .user-avatar {
          border: 5px solid white;
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
          font-size: 48px;
          font-weight: 700;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
        }
        
        .edit-avatar-btn {
          position: absolute;
          bottom: 5px;
          right: 5px;
          background: white;
          border: 2px solid #667eea;
          color: #667eea;
          
          &:hover {
            background: #667eea;
            color: white;
          }
        }
      }
      
      .user-info {
        padding-bottom: 10px;
        
        .user-name {
          font-size: 28px;
          font-weight: 700;
          margin: 0 0 5px 0;
          color: #303133;
        }
        
        .user-id {
          font-size: 14px;
          color: #909399;
          margin: 0;
        }
      }
    }
    
    .stats-section {
      display: flex;
      align-items: center;
      gap: 30px;
      padding: 20px 0;
      
      .stat-item {
        text-align: center;
        
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
      
      .stat-divider {
        width: 1px;
        height: 40px;
        background: #e4e7ed;
      }
    }
  }
}

// 卡片行
.cards-row {
  margin-bottom: 20px;
}

// 高级卡片
.premium-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 2px solid #f0f0f0;
    
    .card-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 20px;
      font-weight: 700;
      color: #303133;
      
      .title-icon {
        font-size: 24px;
        color: #667eea;
      }
    }
  }
}

// 等级卡片
.level-card {
  .level-main {
    display: flex;
    gap: 30px;
    margin-bottom: 30px;
    
    .level-badge-large {
      flex-shrink: 0;
      text-align: center;
      padding: 24px 20px;
      border: 3px solid;
      border-radius: 16px;
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: -50%;
        right: -50%;
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
        pointer-events: none;
      }
      
      .level-icon-large {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 15px;
        color: white;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
        position: relative;
        z-index: 1;
      }
      
      .level-name-large {
        font-size: 20px;
        font-weight: 700;
        margin-bottom: 5px;
        color: white;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        position: relative;
        z-index: 1;
      }
      
      .level-code {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.9);
        position: relative;
        z-index: 1;
      }
    }
    
    .level-progress-section {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .progress-header {
        display: flex;
        align-items: center;
        gap: 5px;
        margin-bottom: 12px;
        
        .current-points {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
        }
        
        .target-points {
          font-size: 16px;
          color: #909399;
        }
        
        .max-level-badge {
          display: flex;
          align-items: center;
          gap: 5px;
          color: #f56c6c;
          font-weight: 600;
        }
      }
      
      .level-progress-bar {
        margin-bottom: 10px;
      }
      
      .needed-points {
        font-size: 14px;
        color: #606266;
        
        strong {
          color: #667eea;
          font-size: 16px;
        }
      }
    }
  }
  
  .level-benefits {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    padding: 20px;
    border-radius: 12px;
    
    .benefits-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 15px 0;
    }
    
    .benefits-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 15px;
      
      .benefit-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        gap: 8px;
        
        .benefit-icon {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          background: #f0f0f0;
          color: #c0c4cc;
          
          &.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }
        }
        
        span {
          font-size: 13px;
          color: #606266;
        }
        
        .benefit-value {
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }
}

// 积分卡片
.points-card {
  .points-main {
    margin-bottom: 30px;
    
    .points-balance {
      text-align: center;
      padding: 30px 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 16px;
      color: white;
      margin-bottom: 20px;
      
      .balance-label {
        font-size: 14px;
        opacity: 0.9;
        margin-bottom: 10px;
      }
      
      .balance-value {
        font-size: 48px;
        font-weight: 700;
        margin-bottom: 5px;
      }
      
      .balance-total {
        font-size: 14px;
        opacity: 0.9;
      }
    }
    
    .quick-actions {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      
      .action-btn {
        height: 50px;
        font-size: 15px;
        font-weight: 600;
        border-radius: 12px;
      }
    }
  }
  
  .points-ways {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 12px;
    
    .ways-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 15px 0;
    }
    
    .ways-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      
      .way-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 15px;
        background: white;
        border-radius: 8px;
        
        .way-name {
          font-size: 14px;
          color: #606266;
        }
        
        .way-points {
          font-size: 16px;
          font-weight: 700;
          color: #67c23a;
        }
      }
    }
  }
}

// 今日统计
.today-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  
  .today-stat-item {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .stat-circle {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .stat-info {
      .stat-label {
        font-size: 13px;
        color: #909399;
        margin-bottom: 5px;
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        
        .stat-limit {
          font-size: 16px;
          color: #909399;
          font-weight: 400;
        }
      }
    }
  }
}

// 等级路线图
.roadmap {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  padding: 20px 0;
  
  .roadmap-item {
    position: relative;
    text-align: center;
    flex: 1;
    
    &.completed .roadmap-icon {
      background: #67c23a !important;
    }
    
    &.current .roadmap-icon {
      box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
      animation: pulse 2s infinite;
    }
    
    &.locked .roadmap-icon {
      background: #c0c4cc !important;
      opacity: 0.5;
    }
    
    .roadmap-icon {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 12px;
      color: white;
      font-size: 24px;
      position: relative;
      z-index: 2;
    }
    
    .roadmap-content {
      .roadmap-level {
        font-size: 16px;
        font-weight: 700;
        margin-bottom: 5px;
      }
      
      .roadmap-points {
        font-size: 13px;
        color: #909399;
      }
    }
    
    .roadmap-line {
      position: absolute;
      top: 28px;
      left: 50%;
      right: -50%;
      height: 2px;
      background: linear-gradient(90deg, currentColor 50%, #e4e7ed 50%);
      z-index: 1;
    }
    
    &.completed .roadmap-line {
      background: #67c23a;
    }
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(102, 126, 234, 0.1);
  }
}

// 个人资料表单
.profile-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 10px;
  }
}

// 响应式
@media (max-width: 1200px) {
  .benefits-grid,
  .ways-list {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .today-stats-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 20px;
  }
  
  .level-main {
    flex-direction: column;
  }
  
  .benefits-grid,
  .ways-list,
  .today-stats-grid {
    grid-template-columns: 1fr !important;
  }
  
  .roadmap {
    flex-direction: column;
    gap: 30px;
    
    .roadmap-line {
      display: none;
    }
  }
}
</style>







