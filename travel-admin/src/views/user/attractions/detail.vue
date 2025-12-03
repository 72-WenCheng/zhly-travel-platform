<template>
  <div class="attraction-detail-page">
    <!-- 返回按钮 -->
    <BackButton />

    <div v-loading="loading" class="detail-container">
      <!-- 景点头部 -->
      <div class="attraction-header">
        <el-carousel :interval="5000" arrow="never" height="500px" class="header-carousel">
          <el-carousel-item v-for="(image, index) in attractionImages" :key="index">
            <img 
              :src="image" 
              :alt="`${attractionDetail.name || '景点'} - ${index + 1}`" 
              @error="handleImageError"
              style="width: 100%; height: 100%; object-fit: cover;"
            />
          </el-carousel-item>
        </el-carousel>

        <div class="header-info">
          <h1 class="attraction-name">{{ attractionDetail.name }}</h1>
          <div class="attraction-rating">
            <el-rate :model-value="ratingDisplayValue" disabled :max="5" size="large" allow-half />
            <span class="rating-text">{{ averageScoreText }}分</span>
          </div>
          <div class="attraction-tags">
            <el-tag v-if="getTypeName(attractionDetail.type)" type="primary">
              {{ getTypeName(attractionDetail.type) }}
            </el-tag>
            <el-tag v-if="attractionDetail.rating" type="warning">
              {{ attractionDetail.rating }}级景区
            </el-tag>
            <el-tag v-for="tag in attractionDetail.tags" :key="tag" type="info">
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 核心信息 -->
      <el-row :gutter="20" class="info-cards">
        <el-col :xs="12" :sm="6">
          <div class="info-card">
            <div class="card-icon time">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-label">建议游玩</div>
              <div class="card-value">{{ attractionDetail.suggestedDuration ? attractionDetail.suggestedDuration + '小时' : '2-3小时' }}</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="info-card">
            <div class="card-icon views">
              <el-icon><View /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-label">浏览量</div>
              <div class="card-value">{{ attractionDetail.viewCount || 0 }}</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="info-card">
            <div class="card-icon comments">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-label">评论数</div>
              <div class="card-value">{{ attractionDetail.commentCount || 0 }}</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="info-card">
            <div class="card-icon star">
              <el-icon><Star /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-label">收藏数</div>
              <div class="card-value">{{ attractionDetail.collectCount || 0 }}</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 主内容区 -->
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <!-- 操作按钮 -->
          <div class="action-bar">
            <el-button
              class="action-button"
              :class="{ 'is-active': attractionDetail.isCollected }"
              size="large"
              @click="handleCollect">
              <el-icon><Star /></el-icon>
              {{ attractionDetail.isCollected ? '已收藏' : '收藏景点' }}
            </el-button>
            <el-button class="action-button" size="large" @click="handleShare">
              <el-icon><Share /></el-icon>
              分享
            </el-button>
            <el-button class="action-button" size="large" @click="handleNavigation">
              <el-icon><Location /></el-icon>
              导航
            </el-button>
          </div>

          <!-- 评分组件 -->
          <el-card class="content-card rating-interactive-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Star /></el-icon>
                <span>景点评分</span>
              </div>
            </template>
            <div class="rating-body">
              <div class="rating-overview">
                <div class="score">{{ averageScoreText }}<span class="unit">分</span></div>
                <div class="count">{{ ratingSummary.ratingCount || 0 }} 人已评分</div>
              </div>
              <div class="rating-action">
                <el-rate
                  v-model="userRating"
                  :max="5"
                  allow-half
                  @change="handleRatingChange"
                  :disabled="ratingSubmitting"
                />
                <div class="rating-hint">点击星星即可评分，可随时调整</div>
              </div>
            </div>
          </el-card>

          <!-- 景点介绍 -->
          <el-card class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>景点介绍</span>
              </div>
            </template>
            <div class="description">
              {{ attractionDetail.description }}
            </div>
          </el-card>

          <!-- 开放时间 -->
          <el-card class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Clock /></el-icon>
                <span>开放时间</span>
              </div>
            </template>
            <div class="opening-hours">
              <div class="hours-item">
                <span class="label">开放时间：</span>
                <span class="value">{{ attractionDetail.openTime || '08:00 - 18:00' }}</span>
              </div>
            </div>
          </el-card>

          <!-- 交通指南 -->
          <el-card class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Guide /></el-icon>
                <span>交通指南</span>
              </div>
            </template>
            <div class="transportation">
              <div v-if="attractionDetail.address" class="trans-item">
                <div class="trans-icon">
                  <el-icon><Location /></el-icon>
                </div>
                <div class="trans-content">
                  <div class="trans-label">详细地址</div>
                  <div class="trans-value">{{ attractionDetail.address }}</div>
                </div>
              </div>
              <div v-if="attractionDetail.transportation" class="trans-item">
                <div class="trans-icon">
                  <el-icon><Van /></el-icon>
                </div>
                <div class="trans-content">
                  <div class="trans-label">交通信息</div>
                  <div class="trans-value">{{ attractionDetail.transportation }}</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 门票信息 -->
          <el-card v-if="attractionDetail.ticketPrice !== null && attractionDetail.ticketPrice !== undefined" class="content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Ticket /></el-icon>
                <span>门票信息</span>
              </div>
            </template>
            <div class="ticket-info">
              <div class="ticket-item">
                <span class="ticket-type">门票价格</span>
                <span class="ticket-price">
                  {{ attractionDetail.ticketPrice === 0 || attractionDetail.ticketPrice === '0' ? '免费' : `¥${attractionDetail.ticketPrice}` }}
                </span>
              </div>
            </div>
          </el-card>


          <!-- 评论区 -->
          <CommentSection content-type="ATTRACTION" :content-id="attractionId" />
        </el-col>

        <!-- 右侧边栏 -->
        <el-col :xs="24" :lg="8">
          <!-- 地图位置 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Location /></el-icon>
                <span>地图位置</span>
              </div>
            </template>
            <div class="map-container">
              <MapDisplay
                v-if="attractionDetail.longitude && attractionDetail.latitude"
                :longitude="attractionDetail.longitude"
                :latitude="attractionDetail.latitude"
                :address="attractionDetail.address"
                :name="attractionDetail.name"
                height="300px"
              />
              <div v-else class="map-placeholder">
                <el-icon><Location /></el-icon>
                <p>暂无位置信息</p>
                <p v-if="attractionDetail.address" class="address">{{ attractionDetail.address }}</p>
              </div>
            </div>
          </el-card>

          <!-- 信息栏 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><InfoFilled /></el-icon>
                <span>景点信息</span>
              </div>
            </template>
            <div class="info-list">
              <div v-if="attractionDetail.phone" class="info-item">
                <el-icon class="info-icon"><Phone /></el-icon>
                <div class="info-content">
                  <div class="info-label">联系电话</div>
                  <div class="info-value">{{ attractionDetail.phone }}</div>
              </div>
              </div>
              <div v-if="formatWebsiteUrl" class="info-item">
                <el-icon class="info-icon"><Link /></el-icon>
                <div class="info-content">
                  <div class="info-label">官网</div>
                  <div class="info-value">
                    <a :href="formatWebsiteUrl" target="_blank" rel="noopener noreferrer">
                      {{ attractionDetail.website }}
                    </a>
                  </div>
                </div>
              </div>
              <div v-if="attractionDetail.features" class="info-item">
                <el-icon class="info-icon"><Warning /></el-icon>
                <div class="info-content">
                  <div class="info-label">特色介绍</div>
                  <div class="info-value">{{ attractionDetail.features }}</div>
                </div>
              </div>
              <div v-if="attractionDetail.bestSeason" class="info-item">
                <el-icon class="info-icon"><Sunny /></el-icon>
                <div class="info-content">
                  <div class="info-label">最佳游览季节</div>
                  <div class="info-value">{{ attractionDetail.bestSeason }}</div>
                </div>
              </div>
              <div v-if="attractionDetail.notes" class="info-item">
                <el-icon class="info-icon"><CircleCheck /></el-icon>
                <div class="info-content">
                  <div class="info-label">注意事项</div>
                  <div class="info-value">{{ attractionDetail.notes }}</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 周边信息 -->
          <el-card v-if="attractionDetail.nearbyFood || attractionDetail.nearbyHotel" class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><MapLocation /></el-icon>
                <span>周边信息</span>
              </div>
            </template>
            <div class="nearby-info">
              <div v-if="attractionDetail.nearbyFood" class="nearby-item">
                <el-icon><TakeawayBox /></el-icon>
                <div class="nearby-content">
                  <div class="nearby-label">周边美食</div>
                  <div class="nearby-value">{{ attractionDetail.nearbyFood }}</div>
                </div>
              </div>
              <div v-if="attractionDetail.nearbyHotel" class="nearby-item">
                <el-icon><Guide /></el-icon>
                <div class="nearby-content">
                  <div class="nearby-label">周边住宿</div>
                  <div class="nearby-value">{{ attractionDetail.nearbyHotel }}</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 周边景点 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Location /></el-icon>
                <span>周边景点</span>
              </div>
            </template>
            <div class="nearby-attractions">
              <div
                v-for="attraction in nearbyAttractions"
                :key="attraction.id"
                class="nearby-item"
                @click="goToAttraction(attraction.id)">
                <img :src="attraction.image" :alt="attraction.name" />
                <div class="nearby-info">
                  <div class="nearby-name">{{ attraction.name }}</div>
                  <div class="nearby-distance">{{ attraction.distance }}</div>
                  <div class="nearby-meta">
                    <span>
                      <el-icon><Star /></el-icon>
                      {{ attraction.collectCount || 0 }}
                    </span>
                    <span>
                      <el-icon><ChatDotRound /></el-icon>
                      {{ attraction.commentCount || 0 }}
                    </span>
                  </div>
                </div>
              </div>
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
            <div class="related-plans">
              <div
                v-for="plan in relatedPlans"
                :key="plan.id"
                class="plan-item"
                @click="goToPlan(plan.id)">
                <img :src="plan.coverImage" :alt="plan.title" />
                <div class="plan-info">
                  <div class="plan-title">{{ plan.title }}</div>
                  <div class="plan-meta">
                    <span>
                      <el-icon><Star /></el-icon>
                      {{ plan.likeCount || 0 }}
                    </span>
                    <span>
                      <el-icon><View /></el-icon>
                      {{ plan.viewCount || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Location,
  Clock,
  View,
  ChatDotRound,
  Star,
  Share,
  Document,
  Guide,
  Van,
  TakeawayBox,
  Ticket,
  InfoFilled,
  Warning,
  Check,
  Phone,
  Link,
  Sunny,
  CircleCheck,
  MapLocation
} from '@element-plus/icons-vue';
import CommentSection from '@/components/CommentSection.vue';
import BackButton from '@/components/BackButton.vue';
import MapDisplay from '@/components/MapDisplay.vue';
import request from '@/utils/request';
import { getCurrentUserInfo, getCurrentUserId } from '@/utils/user';
import { recordShare } from '@/api/share';
import { useSystemStore } from '@/stores/system';
import { storeToRefs } from 'pinia';

const route = useRoute();
const router = useRouter();
const systemStore = useSystemStore();
const { userPlatformName } = storeToRefs(systemStore);

const attractionId = ref(parseInt(route.params.id));
const loading = ref(false);
const attractionDetail = ref({});
const nearbyAttractions = ref([]);
const relatedPlans = ref([]);
const ratingSummary = ref({
  averageScore: 0,
  ratingCount: 0,
  userScore: 0
});
const userRating = ref(0);
const ratingSubmitting = ref(false);

// 景点图片列表
const attractionImages = computed(() => {
  if (!attractionDetail.value || Object.keys(attractionDetail.value).length === 0) {
    return ['/default-attraction.jpg'];
  }
  
  // 优先使用images数组
  if (attractionDetail.value.images && Array.isArray(attractionDetail.value.images)) {
    const images = attractionDetail.value.images.filter(img => img && img.trim() !== '');
    if (images.length > 0) {
      return images;
    }
  }
  
  // 其次使用coverImage
  if (attractionDetail.value.coverImage) {
    return [attractionDetail.value.coverImage];
  }
  
  // 再次使用image
  if (attractionDetail.value.image) {
    return [attractionDetail.value.image];
  }
  
  // 最后使用默认图片
  return ['/default-attraction.jpg'];
});

const formatWebsiteUrl = computed(() => {
  const website = attractionDetail.value?.website;
  if (!website) return '';
  const trimmed = website.trim();
  if (/^https?:\/\//i.test(trimmed)) {
    return trimmed;
  }
  return `https://${trimmed}`;
});

const averageScoreDisplay = computed(() => {
  const avg = ratingSummary.value.averageScore ||
    (attractionDetail.value && Number(attractionDetail.value.score)) ||
    (attractionDetail.value && Number(attractionDetail.value.rating)) ||
    0;
  const num = Number(avg);
  if (Number.isNaN(num)) {
    return 0;
  }
  const rounded = Math.round(num * 10) / 10;
  if (rounded > 5) return 5;
  if (rounded < 0) return 0;
  return rounded;
});

const averageScoreText = computed(() => averageScoreDisplay.value.toFixed(1));
const ratingDisplayValue = computed(() => averageScoreDisplay.value);

// 加载景点详情
const loadAttractionDetail = async () => {
  loading.value = true;
  try {
    const response = await request.get(`/user/attraction/detail/${attractionId.value}`);
    if (response.code === 200) {
      attractionDetail.value = response.data || {};
      
      // 确保基本字段有默认值
      if (!attractionDetail.value.name) {
        attractionDetail.value.name = '景点名称';
      }
      
      // 后端已经解析了tags为数组，无需再次解析
      if (!Array.isArray(attractionDetail.value.tags)) {
      if (typeof attractionDetail.value.tags === 'string') {
          attractionDetail.value.tags = attractionDetail.value.tags.split(',').filter(Boolean).map(t => t.trim());
        } else {
          attractionDetail.value.tags = [];
        }
      }
      
      // 确保图片字段存在
      if (!attractionDetail.value.images || (Array.isArray(attractionDetail.value.images) && attractionDetail.value.images.length === 0)) {
        if (attractionDetail.value.coverImage) {
          attractionDetail.value.images = [attractionDetail.value.coverImage];
        } else if (attractionDetail.value.image) {
          attractionDetail.value.images = [attractionDetail.value.image];
        }
      }
      
      // 增加浏览量
      increaseViewCount();
      
      // 记录浏览历史（在数据加载完成后）
      recordBrowseHistory();
      
      // 加载周边和推荐
      loadRecommendations();
      loadRatingSummary();
    } else {
      ElMessage.error(response.message || '加载失败');
    }
  } catch (error) {
    console.error('加载景点详情失败:', error);
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
    // 数据加载完成后，确保滚动到顶部
    nextTick(() => {
      scrollToTop();
    });
  }
};

const loadRatingSummary = async () => {
  if (!attractionId.value) return;
  try {
    const response = await request.get(`/user/attraction/${attractionId.value}/rating`);
    if (response.code === 200) {
      ratingSummary.value = {
        averageScore: response.data?.averageScore || 0,
        ratingCount: response.data?.ratingCount || 0,
        userScore: response.data?.userScore || 0
      };
      userRating.value = ratingSummary.value.userScore || 0;
      if (ratingSummary.value.averageScore) {
        attractionDetail.value.score = ratingSummary.value.averageScore;
        attractionDetail.value.rating = Math.round(ratingSummary.value.averageScore);
      }
    }
  } catch (error) {
    console.warn('获取评分信息失败:', error);
  }
};

const handleRatingChange = async (value: number) => {
  if (!attractionId.value) return;
  const currentUserId = getCurrentUserId();
  if (!currentUserId) {
    userRating.value = ratingSummary.value.userScore || 0;
    ElMessage.warning('请先登录后再评分');
    router.push('/login');
    return;
  }

  if (ratingSubmitting.value) return;
  ratingSubmitting.value = true;

  try {
    const response = await request.post(`/user/attraction/${attractionId.value}/rating`, { score: value });
    if (response.code === 200) {
      ratingSummary.value = {
        averageScore: response.data?.averageScore || value,
        ratingCount: response.data?.ratingCount || ratingSummary.value.ratingCount || 1,
        userScore: response.data?.userScore || value
      };
      userRating.value = ratingSummary.value.userScore || value;
      attractionDetail.value.score = ratingSummary.value.averageScore;
      attractionDetail.value.rating = Math.round(ratingSummary.value.averageScore);
      ElMessage.success('评分成功，感谢您的反馈');
    }
  } catch (error: any) {
    console.error('提交评分失败:', error);
    userRating.value = ratingSummary.value.userScore || 0;
    ElMessage.error(error?.message || '评分失败，请稍后重试');
  } finally {
    ratingSubmitting.value = false;
  }
};

// 增加浏览量（使用防抖，避免重复调用）
let viewCountIncreased = false;
const increaseViewCount = async () => {
  // 防止重复调用
  if (viewCountIncreased) {
    console.log('⚠️ 浏览量已增加，跳过重复调用');
    return;
  }
  
  try {
    viewCountIncreased = true; // 标记已调用
    await request.post(`/user/attraction/view/${attractionId.value}`);
    console.log('✅ 浏览量增加成功');
  } catch (error) {
    console.error('增加浏览量失败:', error);
    viewCountIncreased = false; // 失败时重置标记
  }
};

// 记录浏览历史
const recordBrowseHistory = async () => {
  try {
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo();
    if (!userInfo) {
      console.warn('未登录，跳过记录浏览历史');
      return;
    }
    
    const userId = userInfo.id || userInfo.userId;
    
    if (!userId) {
      console.warn('用户ID不存在，跳过记录浏览历史');
      return;
    }
    
    // 获取景点信息（确保已经加载完成）
    const attraction = attractionDetail.value;
    if (!attraction || Object.keys(attraction).length === 0) {
      console.warn('景点信息未加载完成，稍后重试');
      // 延迟重试
      setTimeout(() => recordBrowseHistory(), 500);
      return;
    }
    
    const browseTitle = attraction.name || '景点';
    const browseImage = attraction.coverImage || attraction.image || (attraction.images && Array.isArray(attraction.images) && attraction.images[0]) || '';
    
    // 调用浏览历史API（使用表单数据方式）
    const response = await request.post('/user/history', null, {
      params: {
        userId: userId,
        browseType: 1, // 1-景点
        browseId: attractionId.value,
        browseTitle: browseTitle,
        browseImage: browseImage
      }
    });
    
    if (response && response.code === 200) {
      console.log('✅ 浏览历史记录成功');
      
      // 记录推荐反馈（点击查看）
      recordRecommendationFeedback(1, 1); // feedbackType=1表示点击查看
    } else {
      console.warn('⚠️ 浏览历史记录失败:', response?.message || '未知错误');
    }
  } catch (error) {
    console.error('❌ 记录浏览历史异常:', error);
    // 不显示错误提示，避免影响用户体验
  }
};

// 记录推荐反馈（持续学习优化）
const recordRecommendationFeedback = async (feedbackType: number, browseDuration?: number) => {
  try {
    const userInfo = getCurrentUserInfo();
    if (!userInfo) {
      return;
    }
    
    const userId = userInfo.id || userInfo.userId;
    if (!userId) {
      return;
    }
    
    // 调用推荐反馈API
    await request.post('/recommendation/feedback', null, {
      params: {
        recommendType: 1, // 1-景点
        recommendId: attractionId.value,
        feedbackType: feedbackType,
        browseDuration: browseDuration
      }
    });
    
    console.log('✅ 推荐反馈记录成功');
  } catch (error) {
    // 静默失败，不影响用户体验
    console.warn('推荐反馈记录失败:', error);
  }
};

// 记录浏览时长（用于持续学习优化和用户画像统计）
let browseStartTime = Date.now();
let browseDurationRecorded = false; // 防止重复记录
const recordBrowseDuration = async () => {
  if (browseDurationRecorded) {
    return; // 已经记录过，避免重复记录
  }
  
  const browseDuration = Math.floor((Date.now() - browseStartTime) / 1000); // 转换为秒
  
  // 如果浏览时长小于5秒，不记录（可能是误点或快速离开）
  if (browseDuration < 5) {
    return;
  }
  
  browseDurationRecorded = true; // 标记已记录
  
  try {
    const userInfo = getCurrentUserInfo();
    if (!userInfo) {
      return;
    }
    
    const userId = userInfo.id || userInfo.userId;
    if (!userId) {
      return;
    }
    
    // 更新浏览历史的停留时长
    try {
      await request.put('/user/history/duration', null, {
        params: {
          userId: userId,
          browseType: 1, // 1-景点
          browseId: attractionId.value,
          duration: browseDuration
        }
      });
      console.log(`✅ 浏览时长已更新: ${browseDuration}秒`);
    } catch (error) {
      console.warn('更新浏览时长失败:', error);
    }
    
    // 如果浏览时长超过30秒，视为积极反馈
    if (browseDuration > 30) {
      recordRecommendationFeedback(3, browseDuration); // feedbackType=3表示浏览时长>30s
    }
  } catch (error) {
    console.warn('记录浏览时长失败:', error);
  }
};

// 加载推荐内容
const loadRecommendations = async () => {
  try {
    // 加载周边景点
    const nearbyResponse = await request.get('/user/attraction/nearby', {
      params: {
        attractionId: attractionId.value,
        limit: 5
      }
    });
    if (nearbyResponse.code === 200) {
      const list = Array.isArray(nearbyResponse.data)
        ? nearbyResponse.data.slice(0, 5)
        : [];
      nearbyAttractions.value = list;
    }
    
    // 加载相关攻略
    const plansResponse = await request.get('/user/attraction/plan/by-attraction', {
      params: {
        attractionId: attractionId.value,
        limit: 5
      }
    });
    if (plansResponse.code === 200) {
      const planList = Array.isArray(plansResponse.data)
        ? plansResponse.data.slice(0, 5)
        : [];
      relatedPlans.value = planList;
    }
  } catch (error) {
    console.error('加载推荐内容失败:', error);
  }
};

// 收藏/取消收藏
const handleCollect = async () => {
  try {
    const response = await request.post(`/user/attraction/collect/${attractionId.value}`);
    if (response.code === 200) {
      attractionDetail.value.isCollected = response.data;
      if (attractionDetail.value.isCollected) {
        attractionDetail.value.collectCount = (attractionDetail.value.collectCount || 0) + 1;
        
        // 记录推荐反馈（收藏）
        recordRecommendationFeedback(2); // feedbackType=2表示收藏
        
        ElMessage.success('收藏成功');
      } else {
        attractionDetail.value.collectCount = Math.max(0, (attractionDetail.value.collectCount || 1) - 1);
        ElMessage.success('已取消收藏');
      }
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
    ElMessage.error('操作失败');
  }
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/default-attraction.jpg';
};

// 分享 - 生成分享图片
const handleShare = async () => {
  try {
    ElMessage.info('正在生成分享图片...');
    
    // 创建Canvas
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      throw new Error('无法创建画布');
    }
    
    // 设置画布尺寸（优化为更适合分享的尺寸）
    const width = 1080;
    const height = 1920;
    canvas.width = width;
    canvas.height = height;
    
    // 绘制新背景（更优雅的渐变）
    const bgGradient = ctx.createLinearGradient(0, 0, 0, height);
    bgGradient.addColorStop(0, '#a8edea'); // 浅青色
    bgGradient.addColorStop(0.25, '#fed6e3'); // 浅粉色
    bgGradient.addColorStop(0.5, '#d299c2'); // 淡紫色
    bgGradient.addColorStop(0.75, '#fef9d7'); // 浅黄色
    bgGradient.addColorStop(1, '#89f7fe'); // 天蓝色
    ctx.fillStyle = bgGradient;
    ctx.fillRect(0, 0, width, height);
    
    // 添加背景装饰图案（圆形光晕效果）
    ctx.fillStyle = 'rgba(255, 255, 255, 0.2)';
    for (let i = 0; i < 8; i++) {
      const x = Math.random() * width;
      const y = Math.random() * height;
      const radius = Math.random() * 250 + 150;
      const gradient = ctx.createRadialGradient(x, y, 0, x, y, radius);
      gradient.addColorStop(0, 'rgba(255, 255, 255, 0.3)');
      gradient.addColorStop(1, 'rgba(255, 255, 255, 0)');
      ctx.fillStyle = gradient;
      ctx.beginPath();
      ctx.arc(x, y, radius, 0, Math.PI * 2);
      ctx.fill();
    }
    
    // 添加额外的装饰点
    ctx.fillStyle = 'rgba(255, 255, 255, 0.15)';
    for (let i = 0; i < 20; i++) {
      const x = Math.random() * width;
      const y = Math.random() * height;
      const radius = Math.random() * 4 + 2;
      ctx.beginPath();
      ctx.arc(x, y, radius, 0, Math.PI * 2);
      ctx.fill();
    }
    
    // 绘制白色内容区域（带毛玻璃效果，确保底部有足够空间）
    const contentPadding = 25;
    const contentWidth = width - contentPadding * 2;
    const contentHeight = height - contentPadding * 2 - 140; // 增加底部预留空间
    const contentX = contentPadding;
    const contentY = contentPadding + 30;
    
    // 绘制多层阴影（增加立体感）
    for (let i = 0; i < 3; i++) {
      ctx.fillStyle = `rgba(0, 0, 0, ${0.08 - i * 0.02})`;
      drawRoundRect(ctx, contentX + (i + 1) * 3, contentY + (i + 1) * 3, contentWidth, contentHeight, 35);
      ctx.fill();
    }
    
    // 绘制半透明白色卡片（毛玻璃效果）
    ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
    drawRoundRect(ctx, contentX, contentY, contentWidth, contentHeight, 35);
    ctx.fill();
    
    // 添加顶部光泽效果
    const topGradient = ctx.createLinearGradient(contentX, contentY, contentX, contentY + 100);
    topGradient.addColorStop(0, 'rgba(255, 255, 255, 0.3)');
    topGradient.addColorStop(1, 'rgba(255, 255, 255, 0)');
    ctx.fillStyle = topGradient;
    drawRoundRect(ctx, contentX, contentY, contentWidth, 100, 35);
    ctx.fill();
    
    let currentY = contentY + 40;
    
    // 加载封面图片
    let coverImageLoaded = false;
    const coverImage = new Image();
    coverImage.crossOrigin = 'anonymous';
    
    const loadCoverImage = new Promise<void>((resolve) => {
      if (!attractionDetail.value.coverImage && !attractionImages.value?.[0]) {
        resolve();
        return;
      }
      
      const imageUrl = attractionDetail.value.coverImage || attractionImages.value?.[0] || '';
      if (!imageUrl) {
        resolve();
        return;
      }
      
      coverImage.onload = () => {
        coverImageLoaded = true;
        // 绘制封面图（占据超过一半屏幕，约55%）
        const imageHeight = Math.floor(height * 0.55); // 占屏幕55%
        const imageWidth = contentWidth - 40;
        const imageX = contentX + 20;
        const imageY = currentY;
        
        // 绘制多层阴影（模糊效果）
        for (let i = 0; i < 5; i++) {
          ctx.fillStyle = `rgba(0, 0, 0, ${0.15 - i * 0.025})`;
          drawRoundRect(ctx, imageX + i * 2, imageY + i * 2, imageWidth, imageHeight, 30);
          ctx.fill();
        }
        
        // 绘制图片背景
        ctx.fillStyle = '#f8f9fa';
        drawRoundRect(ctx, imageX, imageY, imageWidth, imageHeight, 30);
        ctx.fill();
        
        // 绘制圆角矩形遮罩
        ctx.save();
        ctx.beginPath();
        drawRoundRect(ctx, imageX, imageY, imageWidth, imageHeight, 30);
        ctx.clip();
        
        // 计算图片裁剪（保持宽高比）
        const imgAspect = coverImage.width / coverImage.height;
        const targetAspect = imageWidth / imageHeight;
        let drawWidth = imageWidth;
        let drawHeight = imageHeight;
        let drawX = imageX;
        let drawY = imageY;
        
        if (imgAspect > targetAspect) {
          drawHeight = imageWidth / imgAspect;
          drawY = imageY + (imageHeight - drawHeight) / 2;
        } else {
          drawWidth = imageHeight * imgAspect;
          drawX = imageX + (imageWidth - drawWidth) / 2;
        }
        
        // 绘制图片
        ctx.drawImage(coverImage, drawX, drawY, drawWidth, drawHeight);
        
        // 添加图片顶部渐变遮罩（柔化顶部边缘）
        const topGradient = ctx.createLinearGradient(imageX, imageY, imageX, imageY + 100);
        topGradient.addColorStop(0, 'rgba(255, 255, 255, 0.3)');
        topGradient.addColorStop(1, 'rgba(255, 255, 255, 0)');
        ctx.fillStyle = topGradient;
        ctx.fillRect(imageX, imageY, imageWidth, 100);
        
        // 添加图片底部高斯模糊遮罩效果（渐变模拟）
        const bottomBlurHeight = 150; // 模糊区域高度
        const bottomGradient = ctx.createLinearGradient(
          imageX, 
          imageY + imageHeight - bottomBlurHeight, 
          imageX, 
          imageY + imageHeight
        );
        bottomGradient.addColorStop(0, 'rgba(255, 255, 255, 0)');
        bottomGradient.addColorStop(0.3, 'rgba(255, 255, 255, 0.4)');
        bottomGradient.addColorStop(0.6, 'rgba(255, 255, 255, 0.7)');
        bottomGradient.addColorStop(1, 'rgba(255, 255, 255, 0.95)');
        ctx.fillStyle = bottomGradient;
        ctx.fillRect(imageX, imageY + imageHeight - bottomBlurHeight, imageWidth, bottomBlurHeight);
        
        ctx.restore();
        
        currentY += imageHeight + 35;
        resolve();
      };
      
      coverImage.onerror = () => {
        resolve();
      };
      
      coverImage.src = imageUrl;
      
      // 超时处理
      setTimeout(() => resolve(), 3000);
    });
    
    await loadCoverImage;
    
    // 如果没有封面图，绘制精美的占位符
    if (!coverImageLoaded) {
      const imageHeight = Math.floor(height * 0.55); // 占屏幕55%
      const imageWidth = contentWidth - 40;
      const imageX = contentX + 20;
      const imageY = currentY;
      
      // 绘制渐变占位符
      const placeholderGradient = ctx.createLinearGradient(imageX, imageY, imageX, imageY + imageHeight);
      placeholderGradient.addColorStop(0, '#f3f4f6');
      placeholderGradient.addColorStop(1, '#e5e7eb');
      ctx.fillStyle = placeholderGradient;
      drawRoundRect(ctx, imageX, imageY, imageWidth, imageHeight, 30);
      ctx.fill();
      
      // 绘制占位符图标和文字
      ctx.fillStyle = '#9ca3af';
      ctx.font = 'bold 56px Arial';
      ctx.textAlign = 'center';
      ctx.fillText('📸', width / 2, currentY + imageHeight / 2 - 25);
      ctx.font = '36px "Microsoft YaHei", Arial, sans-serif';
      ctx.fillText('景点图片', width / 2, currentY + imageHeight / 2 + 35);
      
      currentY += imageHeight + 35;
    }
    
    // 绘制景点名称（字体缩小，更精致）
    const name = attractionDetail.value.name || '景点名称';
    
    // 名称渐变效果
    const nameGradient = ctx.createLinearGradient(width / 2 - 150, currentY - 30, width / 2 + 150, currentY + 30);
    nameGradient.addColorStop(0, '#1a1a1a');
    nameGradient.addColorStop(0.5, '#333333');
    nameGradient.addColorStop(1, '#1a1a1a');
    
    // 绘制文字阴影（更轻微）
    ctx.shadowColor = 'rgba(0, 0, 0, 0.1)';
    ctx.shadowBlur = 10;
    ctx.shadowOffsetX = 0;
    ctx.shadowOffsetY = 3;
    
    ctx.fillStyle = nameGradient;
    ctx.font = 'bold 58px "Microsoft YaHei", Arial, sans-serif';
    ctx.textAlign = 'center';
    
    // 如果名称太长，分两行显示
    const nameMetrics = ctx.measureText(name);
    if (nameMetrics.width > contentWidth - 80) {
      const midPoint = Math.floor(name.length / 2);
      ctx.fillText(name.substring(0, midPoint), width / 2, currentY);
      currentY += 70;
      ctx.fillText(name.substring(midPoint), width / 2, currentY);
    } else {
      ctx.fillText(name, width / 2, currentY);
    }
    
    // 重置阴影
    ctx.shadowColor = 'transparent';
    ctx.shadowBlur = 0;
    ctx.shadowOffsetX = 0;
    ctx.shadowOffsetY = 0;
    
    currentY += 40;
    
    // 绘制评分和类型（字体缩小）
    const rating = attractionDetail.value.score || attractionDetail.value.rating || '0';
    const typeName = getTypeName(attractionDetail.value.type) || '';
    
    // 评分（左侧，带背景卡片）
    const ratingBgX = width / 2 - 110;
    const ratingBgY = currentY - 24;
    const ratingBgWidth = 100;
    const ratingBgHeight = 38;
    
    // 绘制评分背景（渐变）
    const ratingGradient = ctx.createLinearGradient(ratingBgX, ratingBgY, ratingBgX, ratingBgY + ratingBgHeight);
    ratingGradient.addColorStop(0, '#fbbf24');
    ratingGradient.addColorStop(1, '#f59e0b');
    ctx.fillStyle = ratingGradient;
    drawRoundRect(ctx, ratingBgX, ratingBgY, ratingBgWidth, ratingBgHeight, 19);
    ctx.fill();
    
    ctx.font = 'bold 30px "Microsoft YaHei", Arial, sans-serif';
    ctx.fillStyle = '#ffffff';
    const ratingText = `\u2B50 ${rating}分`;
    ctx.fillText(ratingText, width / 2 - 60, currentY);
    
    // 类型标签（右侧，带渐变背景）
    if (typeName) {
      ctx.font = 'bold 26px "Microsoft YaHei", Arial, sans-serif';
      const typeTextWidth = ctx.measureText(typeName).width + 28;
      const typeX = width / 2 + 110;
      const typeY = currentY - 22;
      const typeBgHeight = 36;
      
      // 绘制类型标签渐变背景
      const typeGradient = ctx.createLinearGradient(typeX - typeTextWidth / 2, typeY, typeX + typeTextWidth / 2, typeY + typeBgHeight);
      typeGradient.addColorStop(0, '#60a5fa');
      typeGradient.addColorStop(1, '#3b82f6');
      ctx.fillStyle = typeGradient;
      drawRoundRect(ctx, typeX - typeTextWidth / 2, typeY, typeTextWidth, typeBgHeight, 18);
      ctx.fill();
      
      // 绘制标签文字
      ctx.fillStyle = '#ffffff';
      ctx.fillText(typeName, typeX, currentY);
    }
    
    currentY += 50;
    
    // 绘制装饰性分隔线（带渐变，更细）
    const dividerGradient = ctx.createLinearGradient(contentX + 50, currentY, contentX + contentWidth - 50, currentY);
    dividerGradient.addColorStop(0, 'rgba(229, 231, 235, 0)');
    dividerGradient.addColorStop(0.5, 'rgba(229, 231, 235, 0.8)');
    dividerGradient.addColorStop(1, 'rgba(229, 231, 235, 0)');
    ctx.strokeStyle = dividerGradient;
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.moveTo(contentX + 50, currentY);
    ctx.lineTo(contentX + contentWidth - 50, currentY);
    ctx.stroke();
    
    // 添加分隔线装饰点（更小）
    ctx.fillStyle = '#667eea';
    ctx.beginPath();
    ctx.arc(width / 2, currentY, 3, 0, Math.PI * 2);
    ctx.fill();
    
    currentY += 40;
    
    // 绘制信息卡片区域（2列布局，更紧凑美观）
    const infoCardStartY = currentY;
    const cardWidth = (contentWidth - 90) / 2;
    const cardHeight = 140; // 缩小卡片高度
    const cardSpacing = 18;
    
    // 第一行信息卡片
    let cardX = contentX + 45;
    let cardRowY = infoCardStartY;
    
    // 卡片1: 位置信息
    if (attractionDetail.value.city || attractionDetail.value.address) {
      const locationText = attractionDetail.value.city 
        ? (attractionDetail.value.address ? `${attractionDetail.value.city}·${attractionDetail.value.address.substring(0, 6)}` : attractionDetail.value.city)
        : (attractionDetail.value.address?.substring(0, 8) || '未知');
      drawInfoCard(ctx, cardX, cardRowY, cardWidth, cardHeight, 
        '\u{1F4CD}', '位置', locationText);
      cardX += cardWidth + cardSpacing;
    }
    
    // 卡片2: 开放时间
    if (attractionDetail.value.openTime) {
      drawInfoCard(ctx, cardX, cardRowY, cardWidth, cardHeight, 
        '\u23F0', '开放时间', attractionDetail.value.openTime);
      cardX = contentX + 45;
      cardRowY += cardHeight + 15;
    }
    
    // 第二行信息卡片
    // 卡片3: 游玩时长
    if (attractionDetail.value.suggestedDuration) {
      drawInfoCard(ctx, cardX, cardRowY, cardWidth, cardHeight, 
        '\u{1F550}', '游玩时长', `${attractionDetail.value.suggestedDuration}小时`);
      cardX += cardWidth + cardSpacing;
    }
    
    // 卡片4: 门票价格
    if (attractionDetail.value.ticketPrice !== null && attractionDetail.value.ticketPrice !== undefined) {
      const price = attractionDetail.value.ticketPrice === 0 ? '免费' : `¥${attractionDetail.value.ticketPrice}`;
      drawInfoCard(ctx, cardX, cardRowY, cardWidth, cardHeight, 
        '\u{1F4B0}', '门票价格', price);
      cardX = contentX + 45;
      cardRowY += cardHeight + 15;
    }
    
    // 第三行信息卡片
    // 卡片5: 最佳季节
    if (attractionDetail.value.bestSeason) {
      drawInfoCard(ctx, cardX, cardRowY, cardWidth, cardHeight, 
        '\u{2600}\uFE0F', '最佳季节', attractionDetail.value.bestSeason);
      cardX += cardWidth + cardSpacing;
    }
    
    // 卡片6: 统计信息（简化文字）
    const statsText = `${attractionDetail.value.viewCount || 0} | ${attractionDetail.value.collectCount || 0} | ${attractionDetail.value.commentCount || 0}`;
    drawInfoCard(ctx, cardX, cardRowY, cardWidth, cardHeight, 
      '\u{1F4CA}', '热度', statsText);
    
    currentY = cardRowY + cardHeight + 30;
    
    // 绘制联系信息（如果有，字体缩小，确保不溢出）
    if ((attractionDetail.value.phone || attractionDetail.value.website) && currentY < contentY + contentHeight - 120) {
      ctx.strokeStyle = '#e5e7eb';
      ctx.lineWidth = 1;
      ctx.beginPath();
      ctx.moveTo(contentX + 50, currentY);
      ctx.lineTo(contentX + contentWidth - 50, currentY);
      ctx.stroke();
      currentY += 25;
      
      ctx.fillStyle = '#374151';
      ctx.font = 'bold 26px "Microsoft YaHei", Arial, sans-serif';
      ctx.textAlign = 'left';
      ctx.fillText('联系方式', contentX + 50, currentY);
      currentY += 35;
      
      if (attractionDetail.value.phone && currentY < contentY + contentHeight - 80) {
        ctx.fillStyle = '#6b7280';
        ctx.font = '24px "Microsoft YaHei", Arial, sans-serif';
        const phoneText = `\u{1F4DE} ${attractionDetail.value.phone}`;
        // 确保电话号不超过画布宽度
        const phoneMetrics = ctx.measureText(phoneText);
        if (phoneMetrics.width > contentWidth - 100) {
          const maxPhoneLength = Math.floor((attractionDetail.value.phone.length * (contentWidth - 150)) / phoneMetrics.width);
          const phone = attractionDetail.value.phone.substring(0, maxPhoneLength) + '...';
          ctx.fillText(`\u{1F4DE} ${phone}`, contentX + 50, currentY);
        } else {
          ctx.fillText(phoneText, contentX + 50, currentY);
        }
        currentY += 32;
      }
      
      if (attractionDetail.value.website && currentY < contentY + contentHeight - 80) {
        ctx.fillStyle = '#3b82f6';
        ctx.font = '24px "Microsoft YaHei", Arial, sans-serif';
        // 网址太长就截取，确保不溢出
        let website = attractionDetail.value.website;
        const websiteText = `\u{1F517} ${website}`;
        const websiteMetrics = ctx.measureText(websiteText);
        if (websiteMetrics.width > contentWidth - 100) {
          const maxLength = Math.floor((website.length * (contentWidth - 150)) / websiteMetrics.width);
          website = website.substring(0, maxLength) + '...';
        }
        ctx.fillText(`\u{1F517} ${website}`, contentX + 50, currentY);
        currentY += 32;
      }
      
      currentY += 15;
    }
    
    // 绘制特色介绍（如果有，字体缩小，确保不溢出）
    if (attractionDetail.value.features && currentY < contentY + contentHeight - 150) {
      ctx.strokeStyle = '#e5e7eb';
      ctx.lineWidth = 1;
      ctx.beginPath();
      ctx.moveTo(contentX + 50, currentY);
      ctx.lineTo(contentX + contentWidth - 50, currentY);
      ctx.stroke();
      currentY += 25;
      
      if (currentY < contentY + contentHeight - 120) {
        ctx.fillStyle = '#374151';
        ctx.font = 'bold 26px "Microsoft YaHei", Arial, sans-serif';
        ctx.textAlign = 'left';
        ctx.fillText('特色介绍', contentX + 50, currentY);
        currentY += 35;
        
        ctx.fillStyle = '#4b5563';
        ctx.font = '24px "Microsoft YaHei", Arial, sans-serif';
        const maxWidth = contentWidth - 100;
        let features = attractionDetail.value.features;
        // 根据可用高度限制字符数
        const availableHeight = contentY + contentHeight - currentY - 100;
        const maxLines = Math.floor(availableHeight / 35);
        const maxChars = maxLines * 25; // 估算每行字符数
        if (features.length > maxChars) {
          features = features.substring(0, maxChars - 3) + '...';
        }
        const featureLines = wrapText(ctx, features, maxWidth);
        featureLines.forEach((line: string, index: number) => {
          if (currentY > contentY + contentHeight - 100 || index >= maxLines) return;
          ctx.fillText(line, contentX + 50, currentY);
          currentY += 35;
        });
        currentY += 15;
      }
    }
    
    // 绘制描述（如果有空间，字体缩小，确保不溢出）
    if (attractionDetail.value.description && currentY < contentY + contentHeight - 150) {
      ctx.strokeStyle = '#e5e7eb';
      ctx.lineWidth = 1;
      ctx.beginPath();
      ctx.moveTo(contentX + 50, currentY);
      ctx.lineTo(contentX + contentWidth - 50, currentY);
      ctx.stroke();
      currentY += 25;
      
      if (currentY < contentY + contentHeight - 120) {
        ctx.fillStyle = '#374151';
        ctx.font = 'bold 26px "Microsoft YaHei", Arial, sans-serif';
        ctx.textAlign = 'left';
        ctx.fillText('景点介绍', contentX + 50, currentY);
        currentY += 35;
        
        ctx.fillStyle = '#4b5563';
        ctx.font = '24px "Microsoft YaHei", Arial, sans-serif';
        const maxWidth = contentWidth - 100;
        // 根据可用高度限制字符数
        const availableHeight = contentY + contentHeight - currentY - 100;
        const maxLines = Math.floor(availableHeight / 35);
        const maxChars = maxLines * 25; // 估算每行字符数
        let description = attractionDetail.value.description;
        if (description.length > maxChars) {
          description = description.substring(0, maxChars - 3) + '...';
        }
        const lines = wrapText(ctx, description, maxWidth);
        
        lines.forEach((line: string, index: number) => {
          if (currentY > contentY + contentHeight - 100 || index >= maxLines) return;
          ctx.fillText(line, contentX + 50, currentY);
          currentY += 35;
        });
      }
    }
    
    // 绘制底部信息（确保在画布内）
    // 计算底部位置，确保不超出画布
    const footerY = Math.min(contentY + contentHeight + 25, height - 80);
    
    // 绘制底部装饰线
    ctx.strokeStyle = '#e5e7eb';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(contentX + 80, footerY - 8);
    ctx.lineTo(contentX + contentWidth - 80, footerY - 8);
    ctx.stroke();
    
    // 绘制系统名称（渐变文字，字体缩小）
    const footerGradient = ctx.createLinearGradient(width / 2 - 100, footerY, width / 2 + 100, footerY);
    footerGradient.addColorStop(0, '#6366f1');
    footerGradient.addColorStop(1, '#ec4899');
    ctx.fillStyle = footerGradient;
    ctx.font = 'bold 24px "Microsoft YaHei", Arial, sans-serif';
    ctx.textAlign = 'center';
    
    // 确保系统名称不超出画布
    const systemName = userPlatformName.value || '智慧生态旅游系统';
    const systemNameMetrics = ctx.measureText(systemName);
    if (systemNameMetrics.width <= width - 160) {
      ctx.fillText(systemName, width / 2, footerY);
    } else {
      // 如果太长，缩小字体
      ctx.font = 'bold 22px "Microsoft YaHei", Arial, sans-serif';
      ctx.fillText(systemName, width / 2, footerY);
    }
    
    // 绘制提示文字（字体缩小，确保不超出）
    if (footerY + 30 < height - 20) {
      ctx.fillStyle = '#9ca3af';
      ctx.font = '20px "Microsoft YaHei", Arial, sans-serif';
      const tipText = '扫码了解更多精彩内容';
      const tipMetrics = ctx.measureText(tipText);
      if (tipMetrics.width <= width - 160) {
        ctx.fillText(tipText, width / 2, footerY + 30);
      } else {
        // 如果太长，缩小字体
        ctx.font = '18px "Microsoft YaHei", Arial, sans-serif';
        ctx.fillText(tipText, width / 2, footerY + 30);
      }
    }
    
    // 转换为图片并下载
    canvas.toBlob(async (blob) => {
      if (!blob) {
        ElMessage.error('生成图片失败');
        return;
      }
      
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${name}_分享图_${new Date().getTime()}.png`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      
      ElMessage.success('分享图片已生成并下载');
      
      // 调用后端API记录分享并奖励积分
      try {
        const response = await recordShare('attraction', attractionId.value);
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
    }, 'image/png');
    
  } catch (error: any) {
    console.error('生成分享图片失败:', error);
    ElMessage.error('生成分享图片失败: ' + (error.message || '未知错误'));
    
    // 降级方案：复制链接
    const url = window.location.href;
    if (navigator.clipboard) {
      navigator.clipboard.writeText(url).then(async () => {
        ElMessage.info('已降级为复制链接到剪贴板');
        
        // 即使降级，也记录分享
        try {
          await recordShare('attraction', attractionId.value);
        } catch (error: any) {
          console.warn('记录分享失败:', error);
        }
      });
    } else {
      ElMessage.info('分享链接：' + url);
      
      // 即使无法复制，也记录分享
      try {
        await recordShare('attraction', attractionId.value);
      } catch (error: any) {
        console.warn('记录分享失败:', error);
      }
    }
  }
};

// 文本换行辅助函数
const wrapText = (ctx: CanvasRenderingContext2D, text: string, maxWidth: number): string[] => {
  const words = text.split('');
  const lines: string[] = [];
  let currentLine = '';
  
  for (let i = 0; i < words.length; i++) {
    const testLine = currentLine + words[i];
    const metrics = ctx.measureText(testLine);
    
    if (metrics.width > maxWidth && currentLine !== '') {
      lines.push(currentLine);
      currentLine = words[i];
    } else {
      currentLine = testLine;
    }
  }
  
  if (currentLine) {
    lines.push(currentLine);
  }
  
  return lines;
};

// 绘制圆角矩形辅助函数
const drawRoundRect = (ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number) => {
  ctx.beginPath();
  ctx.moveTo(x + radius, y);
  ctx.lineTo(x + width - radius, y);
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
  ctx.lineTo(x + width, y + height - radius);
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
  ctx.lineTo(x + radius, y + height);
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
  ctx.lineTo(x, y + radius);
  ctx.quadraticCurveTo(x, y, x + radius, y);
  ctx.closePath();
};

// 绘制信息卡片辅助函数（带模糊阴影和渐变效果，字体缩小）
const drawInfoCard = (ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, 
  icon: string, label: string, value: string) => {
  // 绘制卡片阴影（多层模糊效果，更轻微）
  for (let i = 0; i < 3; i++) {
    ctx.fillStyle = `rgba(0, 0, 0, ${0.05 - i * 0.015})`;
    drawRoundRect(ctx, x + (i + 1) * 1.5, y + (i + 1) * 1.5, width, height, 16);
    ctx.fill();
  }
  
  // 绘制卡片背景（更精致的渐变）
  const cardGradient = ctx.createLinearGradient(x, y, x, y + height);
  cardGradient.addColorStop(0, 'rgba(255, 255, 255, 0.98)');
  cardGradient.addColorStop(0.5, 'rgba(250, 250, 250, 0.95)');
  cardGradient.addColorStop(1, 'rgba(245, 245, 245, 0.98)');
  ctx.fillStyle = cardGradient;
  drawRoundRect(ctx, x, y, width, height, 16);
  ctx.fill();
  
  // 绘制边框（渐变边框）
  const borderGradient = ctx.createLinearGradient(x, y, x + width, y + height);
  borderGradient.addColorStop(0, 'rgba(229, 231, 235, 0.8)');
  borderGradient.addColorStop(1, 'rgba(229, 231, 235, 0.4)');
  ctx.strokeStyle = borderGradient;
  ctx.lineWidth = 1.2;
  drawRoundRect(ctx, x, y, width, height, 16);
  ctx.stroke();
  
  // 添加顶部光泽（更小）
  const shineGradient = ctx.createLinearGradient(x, y, x, y + 30);
  shineGradient.addColorStop(0, 'rgba(255, 255, 255, 0.4)');
  shineGradient.addColorStop(1, 'rgba(255, 255, 255, 0)');
  ctx.fillStyle = shineGradient;
  drawRoundRect(ctx, x, y, width, 30, 16);
  ctx.fill();
  
  // 绘制图标（emoji，缩小）
  ctx.shadowColor = 'rgba(99, 102, 241, 0.15)';
  ctx.shadowBlur = 6;
  ctx.shadowOffsetX = 0;
  ctx.shadowOffsetY = 1.5;
  ctx.font = 'bold 36px Arial';
  ctx.textAlign = 'left';
  ctx.fillText(icon, x + 18, y + 48);
  ctx.shadowColor = 'transparent';
  ctx.shadowBlur = 0;
  ctx.shadowOffsetX = 0;
  ctx.shadowOffsetY = 0;
  
  // 绘制标签（字体缩小）
  ctx.fillStyle = '#9ca3af';
  ctx.font = '24px "Microsoft YaHei", Arial, sans-serif';
  ctx.fillText(label, x + 18, y + 80);
  
  // 绘制值（加粗，带渐变效果，字体缩小）
  const valueGradient = ctx.createLinearGradient(x + 18, y + 85, x + 18, y + 125);
  valueGradient.addColorStop(0, '#1f2937');
  valueGradient.addColorStop(1, '#374151');
  ctx.fillStyle = valueGradient;
  ctx.font = 'bold 28px "Microsoft YaHei", Arial, sans-serif';
  
  // 如果值太长，缩小字体或截取（更严格的限制）
  const originalFont = ctx.font;
  let displayValue = value;
  let valueMetrics = ctx.measureText(displayValue);
  const maxTextWidth = width - 50; // 增加边距确保不溢出
  
  if (valueMetrics.width > maxTextWidth) {
    // 先尝试缩小字体
    ctx.font = 'bold 24px "Microsoft YaHei", Arial, sans-serif';
    valueMetrics = ctx.measureText(displayValue);
    
    if (valueMetrics.width > maxTextWidth) {
      // 缩小到22px
      ctx.font = 'bold 22px "Microsoft YaHei", Arial, sans-serif';
      valueMetrics = ctx.measureText(displayValue);
      
      if (valueMetrics.width > maxTextWidth) {
        // 还是太长，截取
        while (ctx.measureText(displayValue).width > maxTextWidth - 10 && displayValue.length > 0) {
          displayValue = displayValue.substring(0, displayValue.length - 1);
        }
        displayValue = displayValue + '...';
      }
    }
  }
  
  ctx.fillText(displayValue, x + 18, y + 118);
  
  // 恢复字体
  ctx.font = originalFont;
};

// 导航
const handleNavigation = () => {
  if (attractionDetail.value.address) {
    // 这里可以集成高德地图或百度地图的导航功能
    const address = encodeURIComponent(attractionDetail.value.address);
    window.open(`https://uri.amap.com/navigation?to=${address}&mode=car&policy=1&src=myapp&coordinate=gaode`, '_blank');
  } else {
    ElMessage.warning('暂无地址信息');
  }
};

// 跳转到其他景点
const goToAttraction = (id) => {
  // 重置浏览量增加标记
  viewCountIncreased = false;
  router.push(`/home/user/attractions/detail/${id}`);
  attractionId.value = id;
  loadAttractionDetail();
};

// 跳转到攻略
const goToPlan = (id) => {
  router.push(`/home/user/plans/detail/${id}`);
};

// 获取景点类型名称（与管理平台创建景点的类型对应）
const getTypeName = (type) => {
  const typeMap = {
    1: '自然风光',
    2: '人文历史',
    3: '主题公园',
    4: '美食街',
    5: '古镇古村',
    6: '温泉度假',
    7: '宗教场所',
    8: '购物娱乐'
  };
  return typeMap[type] || '';
};

// 监听路由变化，切换景点时重置标记
watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    // 景点ID变化，先记录当前页面的浏览时长
    if (oldId) {
      recordBrowseDuration();
    }
    // 重置标记和开始时间
    viewCountIncreased = false;
    browseDurationRecorded = false;
    browseStartTime = Date.now();
    attractionId.value = parseInt(newId);
    // 滚动到顶部
    scrollToTop();
    loadAttractionDetail();
  }
}, { immediate: false });

// 页面卸载时记录浏览时长
onUnmounted(() => {
  recordBrowseDuration();
});

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

// 返回
onMounted(() => {
  // 先滚动到顶部
  scrollToTop();
  browseStartTime = Date.now(); // 记录开始浏览时间
  browseDurationRecorded = false; // 重置记录标记
  loadAttractionDetail();
  
  // 页面可见性变化时记录浏览时长
  const handleVisibilityChange = () => {
    if (document.hidden) {
      // 页面隐藏时记录浏览时长
      recordBrowseDuration();
    } else {
      // 页面重新可见时，重新开始计时（但不清除已记录标记，避免重复记录）
      browseStartTime = Date.now();
    }
  };
  
  document.addEventListener('visibilitychange', handleVisibilityChange);
  
  // 页面卸载时移除事件监听器
  onUnmounted(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange);
  });
});
</script>

<style scoped lang="scss">
.attraction-detail-page {
  min-height: 100vh;
  background: #f5f7fa;

  .detail-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 24px 24px 24px;
  }

  // 景点头部
  .attraction-header {
    position: relative;
    margin: 0 -24px 24px -24px;
    border-radius: 0;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);

    .header-carousel {
      :deep(.el-carousel__item) {
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }

    .header-info {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 40px;
      background: linear-gradient(to top, rgba(0, 0, 0, 0.9), transparent);
      color: white;

      .attraction-name {
        font-size: 36px;
        font-weight: 700;
        margin: 0 0 16px 0;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
      }

      .attraction-rating {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;

        .rating-text {
          font-size: 20px;
          font-weight: 600;
        }
      }

      .attraction-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }
    }
  }

  // 信息卡片
  .info-cards {
    margin-bottom: 24px;

    // 给每一列添加底部间距，避免上下卡片黏在一起
    :deep(.el-col) {
      margin-bottom: 20px;
    }

    .info-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 24px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: all 0.3s;
      height: 100%;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .card-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: white;
        flex-shrink: 0;

        &.price {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.time {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.views {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.comments {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }

        &.star {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
      }

      .card-content {
        flex: 1;

        .card-label {
          font-size: 14px;
          color: #999;
          margin-bottom: 8px;
        }

        .card-value {
          font-size: 22px;
          font-weight: 700;
          color: #333;
        }
      }
    }
  }

  // 操作栏
  .action-bar {
    display: flex;
    gap: 12px;
    padding: 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    margin-bottom: 24px;

    .action-button {
      flex: 1;
      background: #f7f8fa;
      border-color: #e2e5ec;
      color: #555;
      font-weight: 600;

      &:hover,
      &:focus {
        background: #ffffff;
        border-color: #d5d9e2;
        color: #222;
      }

      &.is-active {
        background: #eceffd;
        border-color: #d6dcf7;
        color: #3c5ee3;
      }

      :deep(.el-icon) {
        margin-right: 6px;
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

    .opening-hours {
      .hours-item {
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .label {
          font-size: 14px;
          color: #666;
          margin-right: 12px;
        }

        .value {
          font-size: 15px;
          font-weight: 600;
          color: #333;
        }
      }
    }

    .transportation {
      .trans-item {
        display: flex;
        gap: 16px;
        padding: 16px;
        background: #fafafa;
        border-radius: 8px;
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .trans-icon {
          width: 40px;
          height: 40px;
          background: #f0f2f5;
          color: #666;
          border: 1px solid #e1e4eb;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;

          .el-icon {
            font-size: 20px;
          }
        }

        .trans-content {
          flex: 1;

          .trans-label {
            font-size: 14px;
            color: #999;
            margin-bottom: 8px;
          }

          .trans-value {
            font-size: 15px;
            line-height: 1.6;
            color: #333;
          }
        }
      }
    }

    .ticket-info {
      .ticket-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px;
        background: #fafafa;
        border-radius: 8px;
        margin-bottom: 12px;

        .ticket-type {
          font-size: 15px;
          color: #666;
        }

        .ticket-price {
          font-family: 'KaiTi', 'STKaiti', 'Kaiti SC', 'Songti SC', serif;
          font-size: 24px;
          font-weight: 700;
          letter-spacing: -0.8px;
          color: #f5576c;
        }
      }

      .ticket-note {
        display: flex;
        align-items: flex-start;
        gap: 8px;
        padding: 12px;
        background: #fff7e6;
        border-radius: 8px;
        font-size: 14px;
        color: #d48806;

        .el-icon {
          font-size: 16px;
          margin-top: 2px;
        }
      }
    }

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
  }

  .rating-interactive-card {
    .rating-body {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 24px;
    }

    .rating-overview {
      display: flex;
      flex-direction: column;
      min-width: 220px;

      .score {
        font-size: 48px;
        font-weight: 700;
        color: #f59e0b;

        .unit {
          font-size: 20px;
          margin-left: 6px;
        }
      }

      .count {
        margin-top: 4px;
        font-size: 14px;
        color: #94a3b8;
      }
    }

    .rating-action {
      flex: 1;

      :deep(.el-rate__icon) {
        font-size: 32px;
      }

      .rating-hint {
        margin-top: 8px;
        font-size: 13px;
        color: #94a3b8;
      }
    }
  }

  // 侧边栏
  .sidebar-card {
    margin-bottom: 24px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .map-container {
      height: 300px;
      width: 100%;
      
      .map-placeholder {
        height: 100%;
        background: #f5f7fa;
        border-radius: 8px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #999;

        .el-icon {
          font-size: 48px;
          margin-bottom: 12px;
        }

        p {
          margin: 4px 0;
          font-size: 14px;
        }

        .address {
          font-size: 12px;
          color: #666;
          padding: 0 20px;
          text-align: center;
        }
      }
    }

    .contact-info {
      .contact-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        font-size: 14px;
        color: #666;

        .el-icon {
          font-size: 18px;
          color: #409eff;
        }

        a {
          color: #409eff;
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }
      }
    }

    .info-list {
      .info-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 16px;
        background: #fafafa;
        border-radius: 8px;
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .info-icon {
          font-size: 20px;
          color: #409eff;
          margin-top: 2px;
          flex-shrink: 0;
        }
        
        .info-content {
          flex: 1;
          min-width: 0;
          
          .info-label {
            font-size: 13px;
            color: #909399;
            margin-bottom: 6px;
            font-weight: 500;
          }
          
          .info-value {
            font-size: 14px;
            color: #303133;
            line-height: 1.6;
            word-break: break-word;
            
            a {
              color: #6d717c;
              text-decoration: none;
              
              &:hover {
                color: #3c4150;
                text-decoration: underline;
              }
            }
          }
        }
      }
    }
    
    .nearby-info {
      .nearby-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 16px;
        background: #fafafa;
        border-radius: 8px;
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .el-icon {
          font-size: 20px;
          color: #409eff;
          margin-top: 2px;
        }
        
        .nearby-content {
          flex: 1;
          
          .nearby-label {
            font-size: 13px;
            color: #909399;
            margin-bottom: 6px;
          }
          
          .nearby-value {
            font-size: 14px;
            color: #303133;
            line-height: 1.6;
          }
        }
        
      }
    }

    .nearby-attractions,
    .related-plans {
      .nearby-item,
      .plan-item {
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
        }

        .nearby-info,
        .plan-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;

          .nearby-name,
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

          .nearby-distance {
            font-size: 12px;
            color: #999;
          }

          .plan-meta,
          .nearby-meta {
            display: flex;
            gap: 12px;
            margin-top: 6px;
            font-size: 12px;
            color: #909399;

            span {
              display: inline-flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .attraction-detail-page {
    .detail-container {
      padding: 0 16px 16px 16px;
    }

    .attraction-header {
      margin: 0 -16px 16px -16px;

      .header-carousel {
        height: 300px;
      }

      .header-info {
        padding: 24px;

        .attraction-name {
          font-size: 24px;
        }

        .attraction-rating {
          font-size: 16px;
        }
      }
    }

    .action-bar {
      flex-wrap: wrap;

      button {
        flex: 1 1 auto;
        min-width: calc(50% - 6px);
      }
    }

    .info-cards {
      .el-col {
        margin-bottom: 12px;
      }
    }
  }
}
</style>




