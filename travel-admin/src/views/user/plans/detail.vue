<template>
  <div class="plan-detail-page">
    <!-- 返回按钮 -->
    <BackButton />

    <div v-loading="loading" class="detail-container">
      <!-- 攻略头部信息 -->
      <div class="plan-header">
        <div class="header-cover">
          <img :src="planDetail.coverImage || '/default-cover.jpg'" alt="封面图" />
          <div class="header-overlay">
            <h1 class="plan-title">{{ planDetail.title }}</h1>
            <div class="plan-meta">
              <div class="meta-item">
                <el-icon><Location /></el-icon>
                <span>{{ planDetail.destination }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ planDetail.days }}天{{ planDetail.nights }}晚</span>
              </div>
              <div class="meta-item">
                <el-icon><Wallet /></el-icon>
                <span>¥{{ planDetail.estimatedCost || '待定' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 核心信息卡片 -->
      <el-row :gutter="20" class="info-cards">
        <el-col :xs="24" :sm="8">
          <div class="info-card views">
            <div class="card-content">
              <div class="card-value">{{ planDetail.viewCount || 0 }}</div>
              <div class="card-label">浏览量</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="info-card likes">
            <div class="card-content">
              <div class="card-value">{{ collectCount }}</div>
              <div class="card-label">收藏数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="info-card comments">
            <div class="card-content">
              <div class="card-value">{{ planDetail.commentCount || 0 }}</div>
              <div class="card-label">评论数</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 主内容区 -->
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <!-- 作者信息 -->
          <div class="author-card">
            <div class="author-info">
              <el-avatar 
                :src="planDetail.authorAvatar" 
                :size="50"
                @click.stop="viewUserProfile(planDetail.authorId)"
                style="cursor: pointer;"
              >
                {{ planDetail.authorName?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div 
                  class="author-name"
                  @click.stop="viewUserProfile(planDetail.authorId)"
                  style="cursor: pointer;"
                >
                  {{ planDetail.authorName }}
                  <el-tag 
                    v-if="authorLevelInfo" 
                    size="small" 
                    :style="{ 
                      background: authorLevelInfo.gradient 
                        ? `linear-gradient(135deg, ${authorLevelInfo.gradient.start}, ${authorLevelInfo.gradient.end})`
                        : authorLevelInfo.color,
                      color: 'white',
                      border: 'none'
                    }"
                  >
                    {{ authorLevelInfo.name }}
                  </el-tag>
                </div>
                <div class="publish-time">发布于 {{ formatDateTime(planDetail.createdTime) }}</div>
              </div>
            </div>
            <div class="action-buttons">
              <el-button
                class="light-button"
                :class="{ 'is-active': isCollected }"
                :disabled="!isPublished"
                @click="handleCollect">
                <el-icon><Star /></el-icon>
                {{ isCollected ? '已收藏' : '收藏' }}
              </el-button>
              <el-button 
                class="light-button" 
                :disabled="!isPublished"
                @click="handleShare">
                <el-icon><Share /></el-icon>
                分享
              </el-button>
            </div>
          </div>

          <!-- 攻略简介 -->
          <el-card class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>攻略简介</span>
              </div>
            </template>
            <div class="description">
              {{ planDetail.description }}
            </div>
          </el-card>

          <!-- 行程安排 -->
          <el-card class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Calendar /></el-icon>
                <span>行程安排</span>
              </div>
            </template>
            <div v-if="planDetail.itinerary && planDetail.itinerary.length > 0" class="itinerary-list">
              <div
                v-for="(day, index) in planDetail.itinerary"
                :key="index"
                class="itinerary-day">
                <div class="day-header">
                  <div class="day-number">Day {{ index + 1 }}</div>
                  <div class="day-title">{{ day.title || day.day || `第${index + 1}天` }}</div>
                </div>
                <div class="day-content">
                  <!-- 支持 items 格式（创建/编辑页面使用的格式） -->
                  <div v-if="day.items && Array.isArray(day.items) && day.items.length > 0" class="activities-list">
                    <div v-for="(item, idx) in day.items" :key="idx" class="activity-item">
                      <div class="activity-time">{{ formatTime(item.time) }}</div>
                      <div class="activity-content">
                        <div class="activity-name">{{ item.activity || item.name || '未命名活动' }}</div>
                        <div v-if="item.location" class="activity-location">
                          <el-icon><Location /></el-icon>
                          {{ item.location }}
                        </div>
                        <div v-if="item.description" class="activity-desc">{{ item.description }}</div>
                      </div>
                    </div>
                  </div>
                  <!-- 支持 activities 格式（旧数据格式） -->
                  <div v-else-if="day.activities && Array.isArray(day.activities) && day.activities.length > 0" class="activities-list">
                    <div v-for="(activity, idx) in day.activities" :key="idx" class="activity-item">
                      <div class="activity-time">{{ formatTime(activity.time) }}</div>
                      <div class="activity-content">
                        <div class="activity-name">{{ activity.activity || activity.name || '未命名活动' }}</div>
                        <div v-if="activity.location" class="activity-location">
                          <el-icon><Location /></el-icon>
                          {{ activity.location }}
                        </div>
                        <div v-if="activity.description" class="activity-desc">{{ activity.description }}</div>
                      </div>
                    </div>
                  </div>
                  <!-- 支持 spots 格式 -->
                  <div v-else-if="day.spots && day.spots.length > 0" class="spots-list">
                    <div v-for="(spot, idx) in day.spots" :key="idx" class="spot-item">
                      <div class="spot-icon">
                        <el-icon><Location /></el-icon>
                      </div>
                      <div class="spot-info">
                        <div class="spot-name">{{ spot.name }}</div>
                        <div v-if="spot.description" class="spot-desc">{{ spot.description }}</div>
                        <div class="spot-meta">
                          <span v-if="spot.duration">
                            <el-icon><Clock /></el-icon>
                            {{ spot.duration }}
                          </span>
                          <span v-if="spot.cost">
                            <el-icon><Wallet /></el-icon>
                            ¥{{ spot.cost }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-else class="day-description">
                    {{ day.description || day.content || '暂无行程安排' }}
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-content">
              <el-icon><InfoFilled /></el-icon>
              <span>暂无行程安排</span>
            </div>
          </el-card>

          <!-- 费用预算 -->
          <el-card class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Wallet /></el-icon>
                <span>费用预算</span>
              </div>
            </template>
            <div class="budget-section">
              <div class="budget-summary">
                <div class="budget-total">
                  <span class="label">预计总费用：</span>
                  <span class="value">¥{{ totalBudget }}</span>
                </div>
                <div class="budget-per-person">
                  <span class="label">人均费用：</span>
                  <span class="value">¥{{ perPersonBudget }}</span>
                  <span class="person-count">（{{ planDetail.personCount || 1 }}人）</span>
                </div>
              </div>
              <div v-if="planDetail.costBreakdown && planDetail.costBreakdown.length > 0" class="cost-breakdown">
                <div
                  v-for="(item, index) in planDetail.costBreakdown"
                  :key="index"
                  class="cost-item">
                  <span class="cost-name">{{ item.name || item.category }}</span>
                  <span class="cost-value">¥{{ item.amount || item.value || 0 }}</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 旅行贴士 -->
          <el-card v-if="planDetail.tips && planDetail.tips.length > 0" class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Warning /></el-icon>
                <span>旅行贴士</span>
              </div>
            </template>
            <div class="tips-content">
              <div v-for="(tip, index) in planDetail.tips" :key="index" class="tip-item">
                <el-icon><Check /></el-icon>
                <span>{{ tip }}</span>
              </div>
            </div>
          </el-card>

          <!-- 评论区 -->
          <CommentSection 
            v-if="isPublished"
            content-type="PLAN" 
            :content-id="planId"
            @comment-count-changed="handleCommentCountChanged" />
          <el-card v-else class="content-card" shadow="never">
            <div class="disabled-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>该攻略尚未发布，暂不支持评论</span>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧边栏 -->
        <el-col :xs="24" :lg="8">
          <!-- 快速信息 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><InfoFilled /></el-icon>
                <span>快速信息</span>
              </div>
            </template>
            <div class="quick-info">
              <div class="info-row">
                <span class="label">最佳季节：</span>
                <span class="value">{{ planDetail.bestSeason || '全年' }}</span>
              </div>
              <div class="info-row">
                <span class="label">旅行方式：</span>
                <span class="value">{{ getTravelTypeText(planDetail.travelMode) }}</span>
              </div>
              <div class="info-row">
                <span class="label">适合人群：</span>
                <span class="value">{{ planDetail.suitableFor || '所有人' }}</span>
              </div>
              <div class="info-row">
                <span class="label">难度等级：</span>
                <el-rate
                  v-model="planDetail.difficulty"
                  disabled
                  :max="5"
                  text-color="#ff9900"
                />
              </div>
            </div>
          </el-card>

          <!-- 推荐景点 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Location /></el-icon>
                <span>推荐景点</span>
              </div>
            </template>
            <div v-loading="loadingAttractions" class="recommended-attractions">
              <div
                v-if="recommendedAttractions.length > 0"
                v-for="attraction in recommendedAttractions"
                :key="attraction.id"
                class="attraction-item"
                @click="goToAttraction(attraction.id)">
                <img 
                  :src="getImageUrl(attraction.image, 'attraction')" 
                  :alt="attraction.name"
                  loading="lazy"
                  @error="handleImageError($event, 'attraction')" />
                <div class="attraction-info">
                  <div class="attraction-name" :title="attraction.name">{{ attraction.name }}</div>
                  <div class="attraction-rating">
                    <el-rate
                      :model-value="Math.round((attraction.rating || 0) * 2) / 2"
                      disabled
                      :max="5"
                      size="small"
                      allow-half
                    />
                  </div>
                  <div v-if="attraction.city" class="attraction-city">{{ attraction.city }}</div>
                </div>
              </div>
              <el-empty 
                v-else 
                description="暂无推荐景点"
                :image-size="80">
                <el-button size="small" @click="loadRecommendations">刷新</el-button>
              </el-empty>
            </div>
          </el-card>

          <!-- 相关攻略 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>相关攻略</span>
              </div>
            </template>
            <div v-loading="loadingPlans" class="related-plans">
              <div
                v-if="relatedPlans.length > 0"
                v-for="plan in relatedPlans"
                :key="plan.id"
                class="related-plan-item"
                @click="goToPlan(plan.id)">
                <img 
                  :src="getImageUrl(plan.coverImage, 'plan')" 
                  :alt="plan.title"
                  loading="lazy"
                  @error="handleImageError($event, 'plan')" />
                <div class="plan-info">
                  <div class="plan-title" :title="plan.title">{{ plan.title }}</div>
                  <div class="plan-meta">
                    <span class="meta-item">
                      <el-icon><View /></el-icon>
                      <span class="meta-text">{{ plan.viewCount || 0 }}</span>
                    </span>
                    <span v-if="plan.collectCount !== undefined" class="meta-item">
                      <el-icon><Star /></el-icon>
                      <span class="meta-text">{{ plan.collectCount || 0 }}</span>
                    </span>
                    <span v-if="plan.destination" class="meta-item destination">
                      <el-icon><Location /></el-icon>
                      <span class="meta-text" :title="plan.destination">{{ plan.destination }}</span>
                    </span>
                  </div>
                </div>
              </div>
              <el-empty 
                v-else 
                description="暂无相关攻略"
                :image-size="80">
                <el-button size="small" @click="loadRecommendations">刷新</el-button>
              </el-empty>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import BackButton from '@/components/BackButton.vue';
import {
  Location,
  Calendar,
  Wallet,
  View,
  Star,
  ChatDotRound,
  Document,
  Clock,
  Warning,
  Check,
  InfoFilled,
  Share
} from '@element-plus/icons-vue';
import CommentSection from '@/components/CommentSection.vue';
import request from '@/utils/request';
import { formatDateTime } from '@/utils';
import { addCollect, removeCollect, checkCollected } from '@/api/userCollect';
import { recordShare } from '@/api/share';
import { getCurrentUserInfo } from '@/utils/user';
import { getLevelByPoints } from '@/utils/level';

const route = useRoute();
const router = useRouter();

const planId = ref(parseInt(route.params.id));
const loading = ref(false);
const planDetail = ref({});
const isCollected = ref(false);
const collectCount = ref(0);
const recommendedAttractions = ref([]);
const relatedPlans = ref([]);
const loadingAttractions = ref(false);
const loadingPlans = ref(false);

// 计算总预算（从费用明细）
const totalBudget = computed(() => {
  if (!planDetail.value.costBreakdown || planDetail.value.costBreakdown.length === 0) {
    return planDetail.value.estimatedCost || 0;
  }
  return planDetail.value.costBreakdown.reduce((sum: number, item: any) => {
    return sum + (Number(item.amount || item.value || 0));
  }, 0);
});

// 计算人均费用
const perPersonBudget = computed(() => {
  const total = totalBudget.value;
  const personCount = planDetail.value.personCount || 1;
  return personCount > 0 ? Math.round(total / personCount) : total;
});

// 判断攻略是否已发布（只有已发布状态才能评论、收藏、分享）
const isPublished = computed(() => {
  const status = planDetail.value.status;
  const auditStatus = planDetail.value.auditStatus;
  
  // 已发布：status = 1 且 auditStatus = 1
  return status === 1 && auditStatus === 1;
});

// 作者等级信息
const authorLevelInfo = computed(() => {
  // 优先使用作者积分计算等级
  if (planDetail.value.authorPoints !== undefined && planDetail.value.authorPoints !== null) {
    return getLevelByPoints(planDetail.value.authorPoints);
  }
  // 如果后端返回了等级名称，尝试根据名称获取等级信息
  if (planDetail.value.authorLevel) {
    const levelMap: Record<string, any> = {
      '青铜旅行者': { name: '青铜旅行者', color: '#8B7355', gradient: { start: '#8B7355', end: '#6B5B4F' } },
      '白银探索者': { name: '白银探索者', color: '#9CA3AF', gradient: { start: '#9CA3AF', end: '#6B7280' } },
      '黄金游侠': { name: '黄金游侠', color: '#F59E0B', gradient: { start: '#F59E0B', end: '#D97706' } },
      '铂金旅者': { name: '铂金旅者', color: '#6366F1', gradient: { start: '#6366F1', end: '#4F46E5' } },
      '钻石达人': { name: '钻石达人', color: '#EC4899', gradient: { start: '#EC4899', end: '#DB2777' } },
      '王者导师': { name: '王者导师', color: '#F97316', gradient: { start: '#F97316', end: '#EA580C' } }
    };
    return levelMap[planDetail.value.authorLevel] || null;
  }
  return null;
});

// 格式化时间显示（行程安排中的时间）
const formatTime = (time: any) => {
  if (!time) return '待定'
  
  // 如果是时间选择器返回的Date对象
  if (time instanceof Date) {
    const hours = time.getHours().toString().padStart(2, '0');
    const minutes = time.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
  
  // 如果是字符串，使用 formatDateTime 处理（移除 T 字母等）
  if (typeof time === 'string') {
    // 如果是完整的日期时间格式（包含日期和时间），格式化后提取时间部分
    const formatted = formatDateTime(time);
    // 如果包含空格，说明是日期时间格式，提取时间部分（HH:mm:ss）
    if (formatted.includes(' ')) {
      const timePart = formatted.split(' ')[1];
      // 如果时间部分包含秒，只取时分，否则返回完整时间部分
      if (timePart && timePart.includes(':')) {
        const parts = timePart.split(':');
        // 返回 HH:mm 格式（不包含秒）
        return `${parts[0]}:${parts[1]}`;
      }
      return timePart;
    }
    // 如果已经是时间格式（HH:mm），直接返回
    return formatted;
  }
  
  return '';
};

// 加载攻略详情
const loadPlanDetail = async () => {
  loading.value = true;
  try {
    const response = await request.get(`/travel-plan/${planId.value}`);
    if (response.code === 200) {
      const data = response.data;
      
      // 基础数据映射
      planDetail.value = {
        id: data.id,
        title: data.title || '未命名攻略',
        description: data.description || data.content || '暂无描述',
        destination: data.destination || '未知目的地',
        days: data.days || 1,
        nights: (data.days && data.days > 1) ? data.days - 1 : 0,
        estimatedCost: data.budget || 0,
        viewCount: data.viewCount || 0,
        likeCount: data.likeCount || 0,
        collectCount: data.likeCount || 0, // 收藏数使用likeCount字段
        commentCount: data.commentCount || 0,
        authorId: data.authorId,
        authorName: data.authorName || data.author || '匿名用户', // 支持author和authorName两种字段名
        authorAvatar: data.authorAvatar || '',
        authorLevel: data.authorLevel || '',
        authorPoints: data.authorPoints || data.authorPoints || null, // 作者积分
        createdTime: data.createTime || '',
        publishTime: data.publishTime || '',
        bestSeason: data.bestSeason || '全年',
        travelMode: data.travelType || '自由行',
        suitableFor: data.suitableFor || '所有人',
        difficulty: data.difficultyLevel || 3,
        personCount: data.personCount || 1,
        status: data.status, // 发布状态：0-草稿，1-已发布，2-已下架
        auditStatus: data.auditStatus // 审核状态：NULL-草稿，0-待审核，1-通过，2-拒绝
      };
      
      // 解析图片（支持images和coverImage两种字段）
      if (data.images) {
        const imageList = typeof data.images === 'string' 
          ? data.images.split(',').filter(img => img.trim())
          : data.images;
        planDetail.value.coverImage = imageList[0] || data.coverImage || '/default-cover.jpg';
        planDetail.value.images = imageList;
      } else if (data.coverImage) {
        planDetail.value.coverImage = data.coverImage;
        planDetail.value.images = [data.coverImage];
      } else {
        planDetail.value.coverImage = '/default-cover.jpg';
        planDetail.value.images = [];
      }
      
      // 解析行程数据 (itinerary 字段)
      if (data.itinerary) {
        try {
          const itineraryData = typeof data.itinerary === 'string' 
            ? JSON.parse(data.itinerary) 
            : data.itinerary;
          
          // 确保是数组格式
          if (Array.isArray(itineraryData)) {
            planDetail.value.itinerary = itineraryData;
          } else {
            // 如果是单个对象，转为数组
            planDetail.value.itinerary = [itineraryData];
          }
          
          // 调试日志
          console.log('解析后的行程数据:', planDetail.value.itinerary);
        } catch (e) {
          console.error('解析行程数据失败:', e, data.itinerary);
          planDetail.value.itinerary = [];
        }
      } else {
        planDetail.value.itinerary = [];
      }
      
      // 解析费用明细 (costDetail 字段)
      if (data.costDetail) {
        try {
          const costData = typeof data.costDetail === 'string' 
            ? JSON.parse(data.costDetail) 
            : data.costDetail;
          // 确保costData是数组格式
          planDetail.value.costBreakdown = Array.isArray(costData) ? costData : [costData];
        } catch (e) {
          console.error('解析费用明细失败:', e);
          planDetail.value.costBreakdown = [];
        }
      } else {
        planDetail.value.costBreakdown = [];
      }
      
      // 解析注意事项 (notice 字段)
      if (data.notice) {
        // 如果 notice 是字符串，按行分割
        if (typeof data.notice === 'string') {
          const tips = data.notice.split('\n').filter(tip => tip.trim());
          planDetail.value.tips = tips.length > 0 ? tips : [data.notice]; // 如果没有换行，整个字符串作为一条
        } else if (Array.isArray(data.notice)) {
          planDetail.value.tips = data.notice;
        } else {
          planDetail.value.tips = [];
        }
      } else {
        planDetail.value.tips = [];
      }
      
      // 解析标签
      if (data.tags) {
        planDetail.value.tags = typeof data.tags === 'string' 
          ? data.tags.split(',').filter(tag => tag.trim())
          : data.tags;
      } else {
        planDetail.value.tags = [];
      }
      
      // 更新收藏数
      collectCount.value = planDetail.value.collectCount || 0;
      
      // 如果后端没有返回作者积分，尝试获取
      if (!planDetail.value.authorPoints && planDetail.value.authorId) {
        try {
          const pointsResponse = await request.get('/user/points/my', {
            params: { userId: planDetail.value.authorId }
          });
          if (pointsResponse.code === 200 && pointsResponse.data?.userPoints) {
            planDetail.value.authorPoints = pointsResponse.data.userPoints.totalPoints || 0;
          }
        } catch (error) {
          console.warn('获取作者积分失败:', error);
        }
      }
      
      // 检查收藏状态
      checkCollectStatus();
      
      // 记录浏览行为
      await recordPlanBrowseHistory();
      
      // 加载推荐内容
      loadRecommendations();
    } else {
      ElMessage.error(response.message || '加载失败');
    }
  } catch (error) {
    console.error('加载攻略详情失败:', error);
    ElMessage.error('加载失败，请稍后重试');
  } finally {
    loading.value = false;
    // 数据加载完成后，确保滚动到顶部
    nextTick(() => {
      scrollToTop();
    });
  }
};

// 记录攻略浏览历史
let planBrowseHistoryRecorded = false;
const recordPlanBrowseHistory = async () => {
  if (planBrowseHistoryRecorded) {
    return;
  }
  const userInfo = getCurrentUserInfo();
  if (!userInfo) {
    return;
  }
  const userId = userInfo.id || userInfo.userId;
  if (!userId) {
    return;
  }
  const plan = planDetail.value;
  if (!plan || Object.keys(plan).length === 0) {
    return;
  }
  const browseTitle = plan.title || '攻略';
  const browseImage = plan.coverImage || (plan.images && plan.images[0]) || '';
  try {
    const response = await request.post('/user/history', null, {
      params: {
        userId,
        browseType: 2, // 2-攻略
        browseId: planId.value,
        browseTitle,
        browseImage
      }
    });
    if (response?.code === 200) {
      planBrowseHistoryRecorded = true;
    }
  } catch (error) {
    console.warn('记录攻略浏览历史失败:', error);
  }
};

// 记录攻略浏览时长
let planBrowseStartTime = Date.now();
let planBrowseDurationRecorded = false;
const recordPlanBrowseDuration = async () => {
  if (planBrowseDurationRecorded) {
    return;
  }
  const browseDuration = Math.floor((Date.now() - planBrowseStartTime) / 1000);
  if (browseDuration < 5) {
    return;
  }
  const userInfo = getCurrentUserInfo();
  if (!userInfo) {
    return;
  }
  const userId = userInfo.id || userInfo.userId;
  if (!userId) {
    return;
  }
  planBrowseDurationRecorded = true;
  try {
    await request.put('/user/history/duration', null, {
      params: {
        userId,
        browseType: 2,
        browseId: planId.value,
        duration: browseDuration
      }
    });
  } catch (error) {
    console.warn('更新攻略浏览时长失败:', error);
  }
};

const handleVisibilityChange = () => {
  if (document.hidden) {
    recordPlanBrowseDuration();
  } else {
    planBrowseStartTime = Date.now();
  }
};

const handleBeforeUnload = () => {
  recordPlanBrowseDuration();
};

// 加载推荐内容
const loadRecommendations = async () => {
  const destination = planDetail.value.destination;
  if (!destination) {
    console.warn('目的地为空，无法加载推荐内容');
    recommendedAttractions.value = [];
    relatedPlans.value = [];
    return;
  }
  
  console.log('开始加载推荐内容，目的地:', destination);
  
  // 并行加载推荐景点和相关攻略
  await Promise.all([
    loadRecommendedAttractions(destination),
    loadRelatedPlans(destination)
  ]);
  
  console.log('推荐内容加载完成，景点数:', recommendedAttractions.value.length, '攻略数:', relatedPlans.value.length);
};

// 加载推荐景点
const loadRecommendedAttractions = async (destination: string) => {
  loadingAttractions.value = true;
  try {
    console.log('加载推荐景点，目的地参数:', destination, '类型:', typeof destination);
    // 优先使用新的相关景点API（根据地点推荐，按热度排序）
    let attractionsResponse;
    try {
      attractionsResponse = await request.get('/recommendation/attractions/related', {
        params: {
          destination: destination,
          limit: 5 // 最多显示5个
        }
      });
      console.log('推荐景点API响应:', attractionsResponse);
    } catch (e) {
      // 如果新API不可用，尝试使用旧的推荐API
      try {
        attractionsResponse = await request.get('/recommendation/attractions', {
          params: {
            city: destination,
            limit: 6
          }
        });
      } catch (e2) {
        // 如果推荐API也不可用，使用普通列表API（用户端接口）
        attractionsResponse = await request.get('/user/attraction/list', {
          params: {
            page: 1,
            size: 10, // 多取一些以便筛选
            city: destination,
            type: null, // 用户端接口不需要status参数，只返回正常状态的景点
            keyword: '' // 可以根据目的地搜索
          }
        });
      }
    }
    
    if (attractionsResponse.code === 200) {
      const attractions = attractionsResponse.data.list || attractionsResponse.data || [];
      console.log('获取到景点数据:', attractions.length, '条，目的地:', destination);
      
      // 去重：根据id去重
      const uniqueAttractionsMap = new Map();
      attractions.forEach(item => {
        if (item && item.id && !uniqueAttractionsMap.has(item.id)) {
          uniqueAttractionsMap.set(item.id, item);
        }
      });
      const uniqueAttractions = Array.from(uniqueAttractionsMap.values());
      console.log('去重后景点数:', uniqueAttractions.length);
      
      // 按热度排序（如果后端没有提供热度分数，前端计算）
      const sortedAttractions = uniqueAttractions
        .map(item => {
          // 如果后端没有提供热度分数，前端计算
          if (!item.popularityScore) {
            let popularityScore = 0;
            // 浏览量权重：每1000次浏览 +1分
            popularityScore += (item.viewCount || 0) / 1000;
            // 收藏数权重：每个收藏 +2分
            popularityScore += (item.collectCount || 0) * 2;
            // 评分权重：评分 * 10分
            popularityScore += (item.score || item.rating || 0) * 10;
            // 景区级别权重
            if (item.rating) {
              if (item.rating === 5) popularityScore += 5;
              else if (item.rating === 4) popularityScore += 3;
              else if (item.rating === 3) popularityScore += 1;
            }
            item.popularityScore = popularityScore;
          }
          return item;
        })
        .sort((a, b) => {
          const scoreA = a.popularityScore || 0;
          const scoreB = b.popularityScore || 0;
          return scoreB - scoreA;
        })
        .slice(0, 5); // 只取前5个
      
      // 只显示实际有数据的项，并且过滤掉不是目标城市的景点
      // 统一处理城市名称格式（移除"市"、"省"等后缀）
      const normalizeCityName = (city: string) => {
        if (!city) return '';
        return city.replace(/市|省|自治区|特别行政区/g, '').trim();
      };
      const normalizedDestination = normalizeCityName(destination);
      
      recommendedAttractions.value = sortedAttractions
        .filter(item => {
          if (!item || !item.id) return false;
          // 过滤掉不是目标城市的景点
          const itemCity = normalizeCityName(item.city || item.location || '');
          return itemCity === normalizedDestination || itemCity.includes(normalizedDestination) || normalizedDestination.includes(itemCity);
        })
        .map(item => ({
        id: item.id,
        name: item.name || item.title || '未知景点',
        image: item.images 
          ? (typeof item.images === 'string' ? item.images.split(',')[0] : item.images[0])
          : item.image || item.coverImage || '/default-attraction.jpg',
        rating: item.rating || item.score || 4.0,
        city: item.city || item.location || destination,
        viewCount: item.viewCount || 0
      }));
    } else {
      recommendedAttractions.value = [];
    }
  } catch (error) {
    console.error('加载推荐景点失败:', error);
    recommendedAttractions.value = [];
  } finally {
    loadingAttractions.value = false;
  }
};

// 加载相关攻略
const loadRelatedPlans = async (destination: string) => {
  loadingPlans.value = true;
  try {
    console.log('加载相关攻略，目的地参数:', destination, '类型:', typeof destination, '当前攻略ID:', planId.value);
    // 优先使用新的相关攻略API
    let plansResponse;
    try {
      plansResponse = await request.get('/recommendation/plans/related', {
        params: {
          planId: planId.value, // 排除当前攻略
          destination: destination,
          limit: 5 // 最多显示5个
        }
      });
      console.log('相关攻略API响应:', plansResponse);
    } catch (e) {
      // 如果新API不可用，尝试使用旧的推荐API
      try {
        plansResponse = await request.get('/recommendation/plans', {
          params: {
            destination: destination,
            limit: 6
          }
        });
      } catch (e2) {
        // 如果推荐API也不可用，使用普通列表API
        plansResponse = await request.get('/travel-plan/list', {
          params: {
            page: 1,
            size: 10, // 多取一些以便筛选
            destination: destination,
            status: 1, // 已发布
            auditStatus: 1 // 已审核通过
          }
        });
      }
    }
    
    if (plansResponse.code === 200) {
      const plans = plansResponse.data.list || plansResponse.data || [];
      console.log('获取到攻略数据:', plans.length, '条，目的地:', destination);
      
      // 去重：根据id去重
      const uniquePlansMap = new Map();
      plans.forEach(item => {
        if (item && item.id && !uniquePlansMap.has(item.id)) {
          uniquePlansMap.set(item.id, item);
        }
      });
      const uniquePlans = Array.from(uniquePlansMap.values());
      console.log('去重后攻略数:', uniquePlans.length);
      
      // 统一处理城市名称格式（移除"市"、"省"等后缀）
      const normalizeCityName = (city: string) => {
        if (!city) return '';
        return city.replace(/市|省|自治区|特别行政区/g, '').trim();
      };
      const normalizedDestination = normalizeCityName(destination);
      
      // 过滤当前攻略，并按相关度排序，同时过滤掉不是目标城市的攻略
      const filteredPlans = uniquePlans
        .filter(item => {
          if (!item || !item.id || item.id === planId.value) return false;
          // 过滤掉不是目标城市的攻略
          const itemDestination = normalizeCityName(item.destination || '');
          return itemDestination === normalizedDestination || 
                 itemDestination.includes(normalizedDestination) || 
                 normalizedDestination.includes(itemDestination);
        })
        .map(item => {
          // 计算相关度分数（基于目的地匹配、标签匹配、浏览量等）
          let relevanceScore = item.relevanceScore || 0;
          
          // 如果后端没有提供相关度分数，前端计算
          if (!item.relevanceScore) {
            // 目的地完全匹配 +10分
            const itemDestination = normalizeCityName(item.destination || '');
            if (itemDestination === normalizedDestination) {
              relevanceScore += 10;
            } else if (itemDestination.includes(normalizedDestination) || normalizedDestination.includes(itemDestination)) {
              // 模糊匹配 +5分
              relevanceScore += 5;
            }
            
            // 标签匹配 +5分（如果有标签）
            if (planDetail.value.tags && item.tags) {
              const currentTags = Array.isArray(planDetail.value.tags) 
                ? planDetail.value.tags 
                : planDetail.value.tags.split(',');
              const itemTags = Array.isArray(item.tags) 
                ? item.tags 
                : item.tags.split(',');
              const commonTags = currentTags.filter(tag => itemTags.includes(tag));
              relevanceScore += commonTags.length * 5;
            }
            
            // 浏览量 +1分（每1000次浏览）
            relevanceScore += (item.viewCount || 0) / 1000;
            
            // 收藏数 +2分（每个收藏）
            relevanceScore += (item.likeCount || item.collectCount || 0) * 2;
          }
          
          return {
            ...item,
            relevanceScore
          };
        })
        .sort((a, b) => b.relevanceScore - a.relevanceScore)
        .slice(0, 5); // 只取前5个
      
      // 只显示实际有数据的项
      relatedPlans.value = filteredPlans
        .filter(item => item && item.id) // 再次过滤确保没有空数据
        .map(item => ({
        id: item.id,
        title: item.title || '未命名攻略',
        coverImage: item.images 
          ? (typeof item.images === 'string' ? item.images.split(',')[0] : item.images[0])
          : item.coverImage || '/default-cover.jpg',
        viewCount: item.viewCount || 0,
        collectCount: item.likeCount || item.collectCount || 0,
        destination: item.destination || ''
      }));
    } else {
      relatedPlans.value = [];
    }
  } catch (error) {
    console.error('加载相关攻略失败:', error);
    relatedPlans.value = [];
  } finally {
    loadingPlans.value = false;
  }
};

// 获取图片URL，防止闪烁
const getImageUrl = (url: string | undefined, type: 'attraction' | 'plan'): string => {
  if (!url || url === 'null' || url === 'undefined') {
    return type === 'attraction' ? '/default-attraction.jpg' : '/default-cover.jpg';
  }
  // 如果已经是默认图片，直接返回，避免循环
  if (url.includes('default-attraction.jpg') || url.includes('default-cover.jpg')) {
    return url;
  }
  return url;
};

// 处理图片加载错误（只处理一次，防止循环）
const imageErrorHandled = new Set<string>();
const handleImageError = (event: Event, type: string) => {
  const img = event.target as HTMLImageElement;
  const originalSrc = img.src;
  
  // 如果已经处理过这个图片的错误，就不再处理
  if (imageErrorHandled.has(originalSrc)) {
    return;
  }
  
  // 标记为已处理
  imageErrorHandled.add(originalSrc);
  
  // 设置默认图片
  const defaultImg = type === 'attraction' ? '/default-attraction.jpg' : '/default-cover.jpg';
  
  // 避免无限循环：如果目标已经是默认图片，就不再设置
  if (!img.src.includes('default-attraction.jpg') && !img.src.includes('default-cover.jpg')) {
    img.src = defaultImg;
  }
};

// 获取旅行方式文本
const getTravelTypeText = (travelType: any) => {
  if (!travelType && travelType !== 0) return '自由行';
  
  // 如果是字符串，直接返回
  if (typeof travelType === 'string') {
    const typeMap: Record<string, string> = {
      '1': '自由行', '2': '跟团游', '3': '自驾游', '4': '背包客',
      '自由行': '自由行', '跟团游': '跟团游', '自驾游': '自驾游', '背包客': '背包客'
    };
    return typeMap[travelType] || travelType;
  }
  
  // 如果是数字，转换为文本
  if (typeof travelType === 'number') {
    const typeMap: Record<number, string> = {
      1: '自由行',
      2: '跟团游',
      3: '自驾游',
      4: '背包客'
    };
    return typeMap[travelType] || '自由行';
  }
  
  return '自由行';
};

// 检查收藏状态
const checkCollectStatus = async () => {
  try {
    const response = await checkCollected(1, planId.value); // collectType=1 表示攻略
    if (response.code === 200) {
      isCollected.value = response.data || false;
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error);
    // 如果未登录或检查失败，默认未收藏
    isCollected.value = false;
  }
};

// 收藏/取消收藏
const handleCollect = async () => {
  // 如果未发布，不允许收藏
  if (!isPublished.value) {
    ElMessage.warning('该攻略尚未发布，无法收藏');
    return;
  }
  
  try {
    if (isCollected.value) {
      // 取消收藏
      const response = await removeCollect(1, planId.value); // collectType=1 表示攻略
      if (response.code === 200) {
        isCollected.value = false;
        collectCount.value = Math.max(0, collectCount.value - 1);
        planDetail.value.collectCount = collectCount.value;
        ElMessage.success('已取消收藏');
      } else {
        ElMessage.error(response.message || '取消收藏失败');
      }
    } else {
      // 添加收藏
      const response = await addCollect(1, planId.value); // collectType=1 表示攻略
      if (response.code === 200) {
        isCollected.value = true;
        collectCount.value += 1;
        planDetail.value.collectCount = collectCount.value;
        ElMessage.success('收藏成功');
      } else {
        ElMessage.error(response.message || '收藏失败');
      }
    }
  } catch (error: any) {
    console.error('收藏操作失败:', error);
    if (error.response?.status === 401 || error.message?.includes('未登录')) {
      ElMessage.error('请先登录');
    } else {
      ElMessage.error(error.message || '操作失败');
    }
  }
};

// 处理评论数变化
const handleCommentCountChanged = (count: number) => {
  planDetail.value.commentCount = count;
};

// 分享
const handleShare = async () => {
  // 如果未发布，不允许分享
  if (!isPublished.value) {
    ElMessage.warning('该攻略尚未发布，无法分享');
    return;
  }
  
  const url = window.location.href;
  
  // 复制到剪贴板
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(async () => {
      ElMessage.success('链接已复制到剪贴板');
      
      // 调用后端API记录分享并奖励积分
      try {
        const response = await recordShare('plan', planId.value);
        if (response.code === 200) {
          // 如果返回消息包含积分信息，显示给用户
          if (response.message && response.message.includes('积分')) {
            ElMessage.success(response.message);
          }
        }
      } catch (error: any) {
        // 分享记录失败不影响分享功能，只记录错误
        console.warn('记录分享失败:', error);
      }
    });
  } else {
    ElMessage.info('分享链接：' + url);
    
    // 即使无法复制，也记录分享
    try {
      await recordShare('plan', planId.value);
    } catch (error: any) {
      console.warn('记录分享失败:', error);
    }
  }
};

// 跳转到景点详情
const goToAttraction = (id) => {
  router.push(`/home/user/attractions/detail/${id}`);
};

// 跳转到其他攻略
const goToPlan = (id) => {
  router.push(`/home/user/plans/detail/${id}`);
  planId.value = id;
  scrollToTop();
  loadPlanDetail();
};

// 查看用户详情
const viewUserProfile = (userId: number) => {
  if (userId) {
    router.push(`/home/user/profile/${userId}`);
  }
};

// 滚动到顶部
const scrollToTop = () => {
  nextTick(() => {
    // 查找滚动容器
    const scrollContainer = document.querySelector('.el-main') || document.querySelector('.main-content') || document.querySelector('.el-scrollbar__wrap');
    
    if (scrollContainer) {
      (scrollContainer as HTMLElement).scrollTo({ top: 0, behavior: 'instant' });
      (scrollContainer as HTMLElement).scrollTop = 0;
    } else {
      // 如果没有找到滚动容器，使用 window
      window.scrollTo({ top: 0, left: 0, behavior: 'instant' });
      document.documentElement.scrollTop = 0;
      document.body.scrollTop = 0;
    }
  });
};

watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    recordPlanBrowseDuration();
    planBrowseStartTime = Date.now();
    planBrowseDurationRecorded = false;
    planBrowseHistoryRecorded = false;
    const parsedId = Number(newId);
    if (!Number.isNaN(parsedId)) {
      planId.value = parsedId;
    }
    scrollToTop();
    loadPlanDetail();
  }
});

onMounted(() => {
  scrollToTop();
  planBrowseStartTime = Date.now();
  planBrowseDurationRecorded = false;
  planBrowseHistoryRecorded = false;
  loadPlanDetail();
  document.addEventListener('visibilitychange', handleVisibilityChange);
  window.addEventListener('beforeunload', handleBeforeUnload);
});

onUnmounted(() => {
  recordPlanBrowseDuration();
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  window.removeEventListener('beforeunload', handleBeforeUnload);
});
</script>

<style scoped lang="scss">
.plan-detail-page {
  min-height: 100vh;
  background: #f5f7fa;

  .detail-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 24px 24px 24px;
  }

  // 攻略头部
  .plan-header {
    margin: 0 -24px 24px -24px;

    .header-cover {
      position: relative;
      height: 400px;
      border-radius: 0;
      overflow: hidden;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .header-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 40px;
        background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
        color: white;

        .plan-title {
          font-size: 36px;
          font-weight: 700;
          margin: 0 0 16px 0;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
        }

        .plan-meta {
          display: flex;
          gap: 32px;
          font-size: 16px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;

            .el-icon {
              font-size: 20px;
            }
          }
        }
      }
    }
  }

  // 信息卡片
  .info-cards {
    margin-bottom: 24px;

    .info-card {
      position: relative;
      padding: 20px 16px;
      background: #ffffff;
      border-radius: 12px;
      border: 1px solid #e8eaed;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;
      overflow: hidden;

      &::after {
        content: '';
        position: absolute;
        top: -40px;
        right: -40px;
        width: 100px;
        height: 100px;
        border-radius: 50%;
        opacity: 0.08;
        pointer-events: none;
      }

      &.views {
        border-color: #409EFF;
        background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
        
        &::after {
          background: radial-gradient(circle, #409EFF 0%, transparent 70%);
        }
      }

      &.likes {
        border-color: #f5576c;
        background: linear-gradient(135deg, #ffffff 0%, #fff0f5 100%);
        
        &::after {
          background: radial-gradient(circle, #f5576c 0%, transparent 70%);
        }
      }

      &.comments {
        border-color: #4facfe;
        background: linear-gradient(135deg, #ffffff 0%, #f0f9ff 100%);
        
        &::after {
          background: radial-gradient(circle, #4facfe 0%, transparent 70%);
        }
      }

      .card-content {
        position: relative;
        z-index: 1;
        text-align: center;

        .card-value {
          font-size: 28px;
          font-weight: 700;
          color: #1a1a1a;
          line-height: 1.2;
          margin-bottom: 8px;
          letter-spacing: -0.5px;
        }

        .card-label {
          font-size: 13px;
          color: #6b7280;
          font-weight: 500;
        }
      }
    }
  }

  // 作者卡片
  .author-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    margin-bottom: 24px;

    .author-info {
      display: flex;
      gap: 16px;
      align-items: center;

      .author-details {
        .author-name {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .publish-time {
          font-size: 14px;
          color: #999;
        }
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;
      
      .light-button {
        background: #fff;
        border: 1px solid #e0e0e0;
        color: #4a4a4a;
        border-radius: 8px;
        padding: 0 18px;
        height: 40px;
        display: inline-flex;
        align-items: center;
        gap: 6px;
        transition: all 0.2s ease;

        .el-icon {
          font-size: 16px;
        }

        &:hover {
          background: #f5f7fa;
          border-color: #cdd4de;
          color: #1f2d3d;
        }

        &.is-active {
          background: #f0f5ff;
          border-color: #bfcde0;
          color: #1f5c9a;
        }
      }
    }
  }

  // 内容卡片
  .content-card {
    margin-bottom: 24px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
      color: #333;

      .el-icon {
        font-size: 20px;
        color: #409eff;
      }
    }

    .description {
      font-size: 15px;
      line-height: 1.8;
      color: #666;
      white-space: pre-wrap;
    }
  }

  // 行程列表 - 白灰色系
  .itinerary-list {
    .itinerary-day {
      margin-bottom: 24px;
      padding: 20px;
      background: #ffffff;
      border-radius: 8px;
      border: 1px solid #e5e7eb;

      &:last-child {
        margin-bottom: 0;
      }

      .day-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 20px;
        padding-bottom: 12px;
        border-bottom: 1px solid #f0f0f0;

        .day-number {
          padding: 6px 14px;
          background: #f5f5f5;
          color: #666666;
          border-radius: 6px;
          font-weight: 500;
          font-size: 12px;
          border: 1px solid #e0e0e0;
        }

        .day-title {
          font-size: 16px;
          font-weight: 500;
          color: #333333;
        }
      }

      .day-content {
        .day-description {
          font-size: 14px;
          line-height: 1.8;
          color: #666666;
          padding: 16px;
          background: #fafafa;
          border-radius: 6px;
          border-left: 3px solid #d0d0d0;
        }

        .spots-list {
          .spot-item {
            display: flex;
            gap: 14px;
            padding: 16px;
            background: #fafafa;
            border-radius: 6px;
            margin-bottom: 12px;
            border: 1px solid #e5e5e5;

            &:last-child {
              margin-bottom: 0;
            }

            .spot-icon {
              width: 40px;
              height: 40px;
              background: #f0f0f0;
              color: #666666;
              border-radius: 6px;
              display: flex;
              align-items: center;
              justify-content: center;
              flex-shrink: 0;
              border: 1px solid #e0e0e0;

              .el-icon {
                font-size: 18px;
              }
            }

            .spot-info {
              flex: 1;

              .spot-name {
                font-size: 15px;
                font-weight: 500;
                color: #333333;
                margin-bottom: 8px;
              }

              .spot-desc {
                font-size: 13px;
                color: #666666;
                line-height: 1.6;
                margin-bottom: 10px;
              }

              .spot-meta {
                display: flex;
                gap: 12px;
                font-size: 12px;
                color: #999999;

                span {
                  display: flex;
                  align-items: center;
                  gap: 4px;
                  padding: 4px 10px;
                  background: #f5f5f5;
                  border-radius: 4px;
                  border: 1px solid #e5e5e5;

                  .el-icon {
                    font-size: 13px;
                    color: #999999;
                  }
                }
              }
            }
          }
        }

        .activities-list {
          position: relative;
          padding-left: 32px;

          // 时间线
          &::before {
            content: '';
            position: absolute;
            left: 12px;
            top: 0;
            bottom: 0;
            width: 1px;
            background: #e0e0e0;
          }

          .activity-item {
            position: relative;
            display: flex;
            gap: 14px;
            padding: 16px;
            background: #fafafa;
            border: 1px solid #e5e5e5;
            border-radius: 6px;
            margin-bottom: 14px;

            &:last-child {
              margin-bottom: 0;
            }

            // 时间线节点
            &::before {
              content: '';
              position: absolute;
              left: -26px;
              top: 20px;
              width: 10px;
              height: 10px;
              background: #d0d0d0;
              border: 2px solid #ffffff;
              border-radius: 50%;
              z-index: 1;
            }

            .activity-time {
              min-width: 70px;
              font-size: 12px;
              font-weight: 500;
              color: #666666;
              padding: 6px 12px;
              background: #f0f0f0;
              border-radius: 4px;
              text-align: center;
              flex-shrink: 0;
              height: fit-content;
              border: 1px solid #e0e0e0;
            }

            .activity-content {
              flex: 1;

              .activity-name {
                font-size: 15px;
                font-weight: 500;
                color: #333333;
                margin-bottom: 8px;
                line-height: 1.5;
              }

              .activity-location {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                font-size: 13px;
                color: #666666;
                padding: 5px 10px;
                background: #f5f5f5;
                border-radius: 4px;
                margin-bottom: 8px;
                border: 1px solid #e5e5e5;

                .el-icon {
                  font-size: 13px;
                  color: #999999;
                }
              }

              .activity-desc {
                font-size: 13px;
                color: #666666;
                line-height: 1.6;
                padding-top: 10px;
                border-top: 1px solid #f0f0f0;
                margin-top: 8px;
              }
            }
          }
        }
      }
    }
  }

  // 费用预算 - 白灰色系
  .budget-section {
    .budget-summary {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
      margin-bottom: 24px;

      @media (max-width: 768px) {
        grid-template-columns: 1fr;
        gap: 12px;
      }

      .budget-total,
      .budget-per-person {
        padding: 20px;
        background: #ffffff;
        border-radius: 6px;
        text-align: center;
        border: 1px solid #e5e5e5;

        .label {
          display: block;
          font-size: 13px;
          color: #999999;
          margin-bottom: 12px;
          font-weight: 400;
        }

        .value {
          display: block;
          font-size: 32px;
          font-weight: 600;
          margin-bottom: 8px;
          letter-spacing: -0.5px;
          color: #333333;
        }

        .person-count {
          font-size: 12px;
          color: #999999;
          font-weight: 400;
        }
      }

      .budget-total {
        border-top: 3px solid #d0d0d0;
      }

      .budget-per-person {
        border-top: 3px solid #d0d0d0;
      }
    }

    .cost-breakdown {
      background: #ffffff;
      border-radius: 6px;
      border: 1px solid #e5e5e5;
      overflow: hidden;

      .cost-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 14px 18px;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .cost-name {
          font-size: 14px;
          font-weight: 400;
          color: #666666;
          display: flex;
          align-items: center;
          gap: 10px;

          &::before {
            content: '';
            width: 6px;
            height: 6px;
            background: #d0d0d0;
            border-radius: 50%;
          }
        }

        .cost-value {
          font-size: 15px;
          font-weight: 500;
          color: #333333;
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }
      }
    }
  }

  // 旅行贴士
  .tips-content {
    .tip-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 12px 0;
      font-size: 14px;
      line-height: 1.8;
      color: #666;

      .el-icon {
        color: #67c23a;
        font-size: 16px;
        margin-top: 3px;
        flex-shrink: 0;
      }
    }
  }

  // 空内容提示
  .empty-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: #909399;

    .el-icon {
      font-size: 48px;
      margin-bottom: 16px;
      opacity: 0.5;
    }

    span {
      font-size: 14px;
    }
  }

  // 侧边栏
  .sidebar-card {
    margin-bottom: 24px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .quick-info {
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .label {
          font-size: 14px;
          color: #666;
        }

        .value {
          font-size: 14px;
          font-weight: 600;
          color: #333;
        }
      }
    }

    .recommended-attractions,
    .related-plans {
      .attraction-item,
      .related-plan-item {
        display: flex;
        gap: 12px;
        padding: 12px;
        border-radius: 8px;
        margin-bottom: 12px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #f5f7fa;
        }

        &:last-child {
          margin-bottom: 0;
        }

        img {
          width: 80px;
          height: 80px;
          object-fit: cover;
          border-radius: 8px;
          flex-shrink: 0;
          background: #f5f7fa;
          display: block;
        }

        .attraction-info,
        .plan-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;

          .attraction-name,
          .plan-title {
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .attraction-rating {
            :deep(.el-rate) {
              height: auto;
            }
          }

          .plan-meta {
            display: flex;
            align-items: center;
            gap: 16px;
            font-size: 12px;
            color: #999;
            margin-top: 4px;
            flex-wrap: wrap;

            .meta-item {
              display: inline-flex;
              align-items: center;
              gap: 4px;
              white-space: nowrap;
              flex-shrink: 0;

              .el-icon {
                font-size: 14px;
                color: #909399;
                flex-shrink: 0;
              }

              .meta-text {
                line-height: 1;
                max-width: 120px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }

              &.destination {
                flex: 1;
                min-width: 0;

                .meta-text {
                  max-width: 100%;
                }
              }
            }
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .plan-detail-page {
    .detail-container {
      padding: 0 16px 16px 16px;
    }

    .plan-header {
      margin: 0 -16px 16px -16px;
    }

    .plan-header .header-cover {
      height: 250px;

      .header-overlay {
        padding: 24px;

        .plan-title {
          font-size: 24px;
        }

        .plan-meta {
          flex-wrap: wrap;
          gap: 16px;
          font-size: 14px;
        }
      }
    }

    .author-card {
      flex-direction: column;
      gap: 16px;

      .action-buttons {
        width: 100%;

        button {
          flex: 1;
        }
      }
    }

    .info-cards {
      .el-col {
        margin-bottom: 12px;
      }
    }
  }
}

.disabled-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
  
  .el-icon {
    font-size: 20px;
    color: #c0c4cc;
  }
}
</style>










