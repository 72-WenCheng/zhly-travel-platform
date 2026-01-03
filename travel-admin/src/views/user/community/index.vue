 <template>
  <div class="community-page">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header" ref="pageHeaderRef">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><ChatDotRound /></el-icon>
        </div>
        <div class="header-text">
          <h2>攻略社区</h2>
          <p class="page-desc">分享旅行经验，发现精彩攻略，与旅行达人交流心得</p>
        </div>
      </div>
      <div class="last-update">
        <el-icon><TrendCharts /></el-icon>
        <span>汇聚优质旅行攻略，助您规划完美行程</span>
      </div>
    </div>

    <!-- 主要内容区域：三栏布局 -->
    <div class="main-content-layout">
      <!-- 左侧导航栏 -->
      <div class="left-nav-sidebar">
        <el-card class="nav-card" shadow="never">
          <div class="nav-menu">
            <div 
              class="nav-item"
              :class="{ 'active': activeCategory === 'hot' }"
              @click="switchCategory('hot')"
            >
              <el-icon><Star /></el-icon>
              <span>推荐</span>
            </div>
            <div 
              class="nav-item"
              :class="{ 'active': activeCategory === 'latest' }"
              @click="switchCategory('latest')"
            >
              <el-icon><Compass /></el-icon>
              <span>发现</span>
            </div>
            <div 
              class="nav-item"
              :class="{ 'active': activeCategory === 'featured' }"
              @click="switchCategory('featured')"
            >
              <el-icon><Medal /></el-icon>
              <span>精华</span>
            </div>
            <div 
              class="nav-item"
              :class="{ 'active': activeCategory === 'followed' }"
              @click="switchCategory('followed')"
            >
              <el-icon><StarFilled /></el-icon>
              <span>关注</span>
            </div>
            <div class="nav-divider"></div>
            <div class="nav-item" @click="router.push('/home/user/plans')">
              <el-icon><Document /></el-icon>
              <span>我的攻略</span>
            </div>
            <div class="nav-item" @click="router.push('/home/user/plans/create')">
              <el-icon><Edit /></el-icon>
              <span>写攻略</span>
            </div>
          </div>
        </el-card>

        <!-- 热门标签 -->
        <el-card class="tags-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><PriceTag /></el-icon>
              <span>热门标签</span>
            </div>
          </template>
          <div class="hot-tags-list">
            <div 
              v-for="(tag, index) in hotTopics.slice(0, 12)" 
              :key="tag.id || index"
              class="tag-item"
              @click="searchByTopic(tag.name)"
            >
              <span class="tag-prefix">#</span>
              <span class="tag-name">{{ tag.name }}</span>
              <span class="tag-count">{{ tag.count || 0 }}</span>
            </div>
            <div v-if="hotTopics.length === 0" class="empty-tags">
              <el-empty description="暂无标签" :image-size="60" />
            </div>
          </div>
        </el-card>

        <!-- 热门目的地 -->
        <el-card class="destinations-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Location /></el-icon>
              <span>热门目的地</span>
            </div>
          </template>
          <div class="destinations-list">
            <div 
              v-for="(dest, index) in hotDestinations" 
              :key="index"
              class="destination-item"
              @click="searchByDestination(dest.name)"
            >
              <el-icon><LocationFilled /></el-icon>
              <span>{{ dest.name }}</span>
              <span class="dest-count">{{ dest.count }}篇</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 中间：主内容区 -->
      <div class="content-center">
        <!-- 热门话题链接 -->
        <div class="hot-topic-links" ref="hotTopicLinksRef">
          <div class="topics-left">
            <span class="link-label">热门话题：</span>
            <div class="topic-row">
              <a
                v-for="(link, index) in hotTopicLinks"
                :key="index"
                class="topic-link"
                :style="getTopicStyle(link.topic)"
                @click.prevent="searchByTopic(link.topic)"
              >
                <span class="topic-label">{{ link.label }}</span>
              </a>
            </div>
          </div>
          <div class="search-right">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索标题 / 目的地 / 标签 / 作者..."
              size="default"
              @input="handleSearchInput"
              class="inline-search"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>

        <!-- 资讯头条（热门攻略大卡片） -->
        <div v-if="featuredPlans.length > 0 && activeCategory === 'hot'" class="featured-plans-section">
          <div class="featured-plans-grid">
            <div 
              v-for="(plan, index) in featuredPlans.slice(0, 4)" 
              :key="plan.id"
              class="featured-plan-card"
              :class="`featured-${index + 1}`"
              @click="viewDetail(plan)"
            >
              <!-- 图片区域 -->
              <div class="featured-image">
                <img :src="plan.coverImage || '/default-cover.jpg'" :alt="plan.title" />
                <div class="featured-badge" v-if="index === 0">
                  <el-icon><Trophy /></el-icon>
                  <span>最热</span>
                </div>
              </div>
              
              <!-- 内容区域 -->
              <div class="featured-content">
                <div class="featured-header">
                  <h3 class="featured-title">{{ plan.title }}</h3>
                  <div class="featured-destination" v-if="plan.destination">
                    <el-icon v-if="plan.destination"><Location /></el-icon>
                    <span v-if="plan.destination">{{ plan.destination }}</span>
                  </div>
                </div>
                
                <!-- 作者和统计 -->
                <div class="featured-meta">
                  <div class="featured-author">
                    <el-avatar :size="28" :src="plan.authorAvatar">{{ plan.authorName?.charAt(0) }}</el-avatar>
                    <span class="author-name">{{ plan.authorName }}</span>
                  </div>
                  <div class="featured-stats-group">
                    <span class="featured-stats" title="浏览量">
                      <el-icon><View /></el-icon>
                      {{ formatNumber(plan.viewCount) }}
                    </span>
                    <span class="featured-stats" title="评论数">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ formatNumber(plan.commentCount) }}
                    </span>
                    <span class="featured-stats" v-if="plan.likeCount > 0" title="点赞数">
                      <el-icon><Star /></el-icon>
                      {{ formatNumber(plan.likeCount) }}
                    </span>
                    <span class="featured-stats" v-if="plan.collectCount > 0" title="收藏数">
                      <el-icon><Collection /></el-icon>
                      {{ formatNumber(plan.collectCount) }}
                    </span>
                  </div>
                </div>
                
                <!-- 底部信息 -->
                <div class="featured-footer">
                  <span class="featured-budget" v-if="plan.budget !== undefined">
                    <el-icon><Money /></el-icon>
                    ¥{{ plan.budget || 0 }}
                  </span>
                  <span class="featured-time" v-if="plan.createTime">
                    <el-icon><Clock /></el-icon>
                    {{ formatTime(plan.createTime) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 攻略列表 -->
        <div 
          v-infinite-scroll="loadMore"
          :infinite-scroll-disabled="loading || noMore"
          :infinite-scroll-distance="100"
          class="plans-list"
        >
      <div 
        v-for="plan in plans" 
        :key="plan.id" 
        class="plan-card"
        @click="viewDetail(plan)"
      >
        <!-- 封面图 -->
        <div class="card-cover" :style="{ height: plan.imageHeight + 'px' }">
          <img :src="plan.coverImage" :alt="plan.title" />
          <div class="card-gradient"></div>
          
          <!-- 精华标签 -->
          <div v-if="plan.isFeatured" class="featured-badge">
            <el-icon><Medal /></el-icon>
            <span>精华</span>
          </div>
        </div>
        
        <!-- 信息层 -->
        <div class="card-info">
          <h3 class="card-title">{{ plan.title }}</h3>
          
          <div class="card-destination">
            <el-icon><Location /></el-icon>
            <span>{{ plan.destination }}</span>
          </div>
          
          <p class="card-desc">{{ plan.description }}</p>
        
        <div class="card-quick-info">
          <div class="info-chip" v-if="plan.days">
            <el-icon><Calendar /></el-icon>
            <span>{{ plan.days }}天行程</span>
          </div>
          <div class="info-chip" v-if="plan.bestSeason">
            <el-icon><Sunny /></el-icon>
            <span>{{ plan.bestSeason }}</span>
          </div>
          <div class="info-chip" v-if="plan.travelTypeLabel">
            <el-icon><Compass /></el-icon>
            <span>{{ plan.travelTypeLabel }}</span>
          </div>
          <div class="info-chip" v-if="plan.suitableFor">
            <el-icon><Connection /></el-icon>
            <span>{{ plan.suitableFor }}</span>
          </div>
          <div class="info-chip" v-if="plan.people">
            <el-icon><UserFilled /></el-icon>
            <span>{{ plan.people }}人同行</span>
          </div>
          <div class="info-chip" v-if="plan.difficultyLabel">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ plan.difficultyLabel }}</span>
          </div>
        </div>
          
          <!-- 作者信息 -->
          <div class="author-info">
            <el-avatar 
              :size="32" 
              :src="plan.authorAvatar"
              class="author-avatar"
              @click.stop="viewUserProfile(plan.authorId)"
              style="cursor: pointer;"
            >
              {{ plan.authorName?.charAt(0) }}
            </el-avatar>
            <div class="author-details">
              <div class="author-name-row">
                <span 
                  class="author-name"
                  @click.stop="viewUserProfile(plan.authorId)"
                  style="cursor: pointer;"
                >{{ plan.authorName }}</span>
                <span 
                  v-if="plan.levelName"
                  class="author-level" 
                  :style="{ 
                    '--level-color-start': plan.levelGradient?.start || plan.levelColor, 
                    '--level-color-end': plan.levelGradient?.end || plan.levelColor 
                  }"
                >
                  {{ plan.levelName }}
                </span>
                <span 
                  v-for="(tag, tagIndex) in plan.tags" 
                  :key="tag" 
                  class="plan-tag"
                  :class="`tag-color-${tagIndex % 6}`"
                  style="margin-left: 8px;"
                >
                  #{{ tag }}
                </span>
              </div>
              <span class="publish-time">{{ formatTime(plan.createTime) }}</span>
            </div>
          </div>
          
          <div class="card-meta">
            <div class="meta-stats">
              <div class="stat-item" title="浏览量">
                <el-icon><View /></el-icon>
                <span>{{ formatNumber(plan.viewCount) }}</span>
              </div>
              <div class="stat-item" title="评论数">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ formatNumber(plan.commentCount) }}</span>
              </div>
              <div 
                class="stat-item like-action"
                :class="{ 'is-liked': plan.isLiked }"
                @click.stop="toggleLike(plan)"
                title="点赞数"
              >
                <el-icon v-if="plan.isLiked"><StarFilled /></el-icon>
                <el-icon v-else><Star /></el-icon>
                <span>{{ formatNumber(plan.likeCount) }}</span>
              </div>
              <div class="stat-item" v-if="plan.collectCount > 0" title="收藏数">
                <el-icon><Collection /></el-icon>
                <span>{{ formatNumber(plan.collectCount) }}</span>
              </div>
            </div>
            
            <div class="card-budget">
              <span class="budget-label">预算</span>
              <span class="budget-value">
                ¥{{ plan.budget || 0 }}
              </span>
            </div>
          </div>
          
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div v-if="noMore && plans.length > 0" class="no-more">
      <el-divider>已经到底了</el-divider>
    </div>

        <el-empty v-if="plans.length === 0 && !loading" description="暂无攻略" />
      </div>

      <!-- 右侧：社区侧边栏 -->
      <div class="sidebar-right">
        <!-- 社区数据统计 -->
        <el-card class="sidebar-card stats-card" shadow="never">
          <div class="stats-content">
            <div class="stats-tabs">
              <div 
                class="stats-tab"
                :class="{ 'active': statsPeriod === 'week' }"
                @click="statsPeriod = 'week'"
              >
                最近7天
              </div>
              <div 
                class="stats-tab"
                :class="{ 'active': statsPeriod === 'all' }"
                @click="statsPeriod = 'all'"
              >
                全部
              </div>
            </div>
            <div class="stats-items">
              <div class="stat-item">
                <div class="stat-label">攻略总数</div>
                <div class="stat-value">{{ communityStats.totalPlans || '--' }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">活跃用户</div>
                <div class="stat-value">{{ communityStats.activeUsers || '--' }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">总浏览量</div>
                <div class="stat-value">{{ formatNumber(communityStats.totalViews || 0) }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">总评论数</div>
                <div class="stat-value">{{ formatNumber(communityStats.totalComments || 0) }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 热门文章 -->
        <el-card class="sidebar-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Trophy /></el-icon>
              <span>热门文章</span>
            </div>
          </template>
          <div class="hot-articles-tabs">
            <div 
              class="article-tab"
              :class="{ 'active': hotArticlePeriod === 'today' }"
              @click="hotArticlePeriod = 'today'"
            >
              今天
            </div>
            <div 
              class="article-tab"
              :class="{ 'active': hotArticlePeriod === 'week' }"
              @click="hotArticlePeriod = 'week'"
            >
              近一周
            </div>
            <div 
              class="article-tab"
              :class="{ 'active': hotArticlePeriod === 'month' }"
              @click="hotArticlePeriod = 'month'"
            >
              近一月
            </div>
          </div>
          <div class="hot-articles-list">
            <div 
              v-for="(article, index) in hotArticles" 
              :key="article.id"
              class="article-item"
              @click="viewDetail(article)"
            >
              <div class="article-rank">{{ index + 1 }}</div>
              <div class="article-content">
                <div class="article-title">{{ article.title }}</div>
                <div class="article-author">{{ article.authorName }}</div>
              </div>
            </div>
            <div v-if="hotArticles.length === 0" class="empty-articles">
              <el-empty description="暂无文章" :image-size="80" />
            </div>
          </div>
        </el-card>

        <!-- 热门作者排行榜 -->
        <el-card class="sidebar-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><UserFilled /></el-icon>
              <span>热门作者</span>
            </div>
          </template>
          <div class="author-ranking">
            <div 
              v-for="(author, index) in topAuthors.slice(0, 8)" 
              :key="author.id"
              class="ranking-item"
              @click="viewUserProfile(author.id)"
            >
              <div class="rank-number" :class="`rank-${index + 1}`">
                {{ index + 1 }}
              </div>
              <el-avatar :size="36" :src="author.avatar" class="author-avatar">
                {{ author.name?.charAt(0) }}
              </el-avatar>
              <div class="author-info">
                <div class="author-name">{{ author.name }}</div>
                <div class="author-stats">
                  <span>{{ author.planCount || 0 }}篇</span>
                  <span>·</span>
                  <span>{{ formatNumber(author.totalLikes || 0) }}赞</span>
                </div>
              </div>
            </div>
            <div v-if="topAuthors.length === 0" class="empty-ranking">
              <el-empty description="暂无数据" :image-size="80" />
            </div>
          </div>
        </el-card>

        <!-- 最新评论 -->
        <el-card class="sidebar-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>最新互动</span>
            </div>
          </template>
          <div class="latest-comments">
            <div 
              v-for="comment in latestComments" 
              :key="comment.id"
              class="comment-item"
            >
              <el-avatar :size="36" :src="comment.avatar" class="comment-avatar">
                {{ comment.username?.charAt(0) }}
              </el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-user">{{ comment.username }}</span>
                  <span class="comment-time">{{ formatTime(comment.createdTime) }}</span>
                </div>
                <div class="comment-text" :title="comment.content">{{ comment.content }}</div>
                <div class="comment-footer">
                  <span 
                    class="comment-plan-link" 
                    @click="viewPlanDetail(comment.contentId)"
                  >
                    {{ comment.planTitle }}
                  </span>
                </div>
              </div>
            </div>
            <div v-if="latestComments.length === 0" class="empty-comments">
              <el-empty description="暂无评论" :image-size="80" />
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onActivated, watch, nextTick, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { getLevelByPoints } from '@/utils/level'
import { getCurrentUserId, getCurrentUserInfo } from '@/utils/user'
import { planTagOptions, planTagPalette } from '@/constants/tags'
import {
  Search, Location, Calendar, View, ChatDotRound, Star, StarFilled,
  Loading, TrendCharts, Medal, Money, Trophy, ArrowUp, ArrowDown,
  UserFilled, Plus, Document, Compass, Edit, PriceTag, LocationFilled,
  Clock, DataAnalysis, Connection, Close, Sunny, Collection
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 页面头部引用（保留，后续如需扩展可用）
const pageHeaderRef = ref<HTMLElement | null>(null)
const hotTopicLinksRef = ref<HTMLElement | null>(null)

// 搜索和筛选
const searchKeyword = ref('')
const activeCategory = ref('hot')
const selectedDestination = ref('')
const selectedDays = ref('')
const selectedBudget = ref('')

// 分类
const categories = ref([
  { value: 'hot', label: '热门', icon: '🔥' },
  { value: 'latest', label: '最新', icon: '🆕' },
  { value: 'featured', label: '精华', icon: '⭐' },
  { value: 'followed', label: '关注', icon: '❤️' }
])

// 目的地列表
// 目的地城市列表（国内外所有主要城市）
const destinations = ref([
  // 国内城市
  '北京', '上海', '重庆', '天津',
  '杭州', '苏州', '南京', '厦门', '青岛', '宁波', '无锡', '扬州', '绍兴', '济南', '烟台', '威海', '合肥', '黄山', '福州', '泉州',
  '广州', '深圳', '三亚', '桂林', '珠海', '东莞', '佛山', '中山', '南宁', '海口', '北海', '阳朔',
  '成都', '昆明', '丽江', '大理', '拉萨', '贵阳', '九寨沟', '乐山', '峨眉山', '香格里拉', '西双版纳',
  '西安', '兰州', '银川', '乌鲁木齐', '西宁', '敦煌', '吐鲁番', '喀什',
  '郑州', '太原', '石家庄', '保定', '大同', '洛阳', '开封', '呼和浩特',
  '哈尔滨', '大连', '沈阳', '长春', '吉林', '牡丹江', '齐齐哈尔',
  '武汉', '长沙', '张家界', '南昌', '宜昌', '景德镇', '凤凰',
  '香港', '澳门', '台北', '高雄', '台中', '台南', '花莲',
  
  // 国际城市
  // 东亚
  '东京', '大阪', '京都', '横滨', '名古屋', '福冈', '札幌', '奈良', '首尔', '釜山', '济州岛', '平壤',
  
  // 东南亚
  '曼谷', '普吉岛', '清迈', '芭提雅', '新加坡', '巴厘岛', '雅加达', '河内', '胡志明市', '岘港', '吉隆坡', '槟城', '马尼拉', '长滩岛', '仰光', '金边', '吴哥窟',
  
  // 南亚
  '迪拜', '阿布扎比', '马尔代夫', '孟买', '新德里', '阿格拉', '斋浦尔', '加德满都', '科伦坡',
  
  // 欧洲
  '巴黎', '伦敦', '罗马', '巴塞罗那', '马德里', '阿姆斯特丹', '威尼斯', '佛罗伦萨', '米兰', '布拉格', '维也纳', '柏林', '慕尼黑', '苏黎世', '日内瓦', '雅典', '圣托里尼', '伊斯坦布尔', '莫斯科', '圣彼得堡', '哥本哈根', '斯德哥尔摩', '奥斯陆', '赫尔辛基', '雷克雅未克', '都柏林', '爱丁堡', '里斯本', '布达佩斯', '华沙', '克拉科夫',
  
  // 北美洲
  '纽约', '洛杉矶', '旧金山', '拉斯维加斯', '迈阿密', '芝加哥', '波士顿', '华盛顿', '西雅图', '奥兰多', '夏威夷', '温哥华', '多伦多', '蒙特利尔', '魁北克', '墨西哥城', '坎昆',
  
  // 大洋洲
  '悉尼', '墨尔本', '布里斯班', '珀斯', '黄金海岸', '凯恩斯', '奥克兰', '皇后镇', '惠灵顿', '基督城', '斐济', '大溪地',
  
  // 南美洲
  '里约热内卢', '布宜诺斯艾利斯', '圣保罗', '利马', '马丘比丘', '圣地亚哥', '波哥大', '卡塔赫纳',
  
  // 非洲
  '开罗', '马拉喀什', '开普敦', '约翰内斯堡', '内罗毕', '桑给巴尔', '维多利亚', '毛里求斯'
])

// 攻略列表
const plans = ref<any[]>([])
const loading = ref(false)
const noMore = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)

// 社区侧边栏数据
const topAuthors = ref<any[]>([])
const hotTopics = ref<any[]>([])
const latestComments = ref<any[]>([])
const featuredPlans = ref<any[]>([])
const hotArticles = ref<any[]>([])
const hotDestinations = ref<any[]>([])
const communityStats = ref({
  totalPlans: 0,
  activeUsers: 0,
  totalViews: 0,
  totalComments: 0
})
const statsPeriod = ref('week')
const hotArticlePeriod = ref('today')

// 热门话题链接：与攻略标签保持一致
const hotTopicLinks = ref(
  planTagOptions.map(tag => ({
    label: tag,
    topic: tag
  }))
)

const defaultTopicPalette = {
  text: '#606266',
  bg: '#f5f7fa',
  hoverBg: '#e4e7ed',
  border: '#e4e7ed',
  shadow: '0 2px 8px rgba(0, 0, 0, 0.06)',
  hoverShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
}

const getTopicStyle = (tag: string) => {
  // 统一使用白灰色系
  return {
    '--topic-color': defaultTopicPalette.text,
    '--topic-bg': defaultTopicPalette.bg,
    '--topic-hover-bg': defaultTopicPalette.hoverBg,
    '--topic-border-color': defaultTopicPalette.border,
    '--topic-shadow': defaultTopicPalette.shadow,
    '--topic-hover-shadow': defaultTopicPalette.hoverShadow
  }
}

// ======== 推荐算法辅助函数 ========
const getHoursSince = (time) => {
  if (!time) {
    return 9999
  }
  const date = new Date(time)
  const now = new Date()
  return Math.max(1, (now.getTime() - date.getTime()) / 3600000)
}

const calculateFreshnessScore = (time) => {
  const hours = getHoursSince(time)
  // 0-1之间，越新越接近1
  return 1 / Math.pow(hours + 2, 0.35)
}

const calculateEngagementScore = (plan) => {
  const views = plan.viewCount || 0
  const likes = plan.likeCount || 0
  const comments = plan.commentCount || 0
  const collects = plan.collectCount || 0
  return views * 0.001 + likes * 1.2 + comments * 1.5 + collects * 0.8
}

const calculateDiscoveryScore = (plan) => {
  const freshness = calculateFreshnessScore(plan.createTime || plan.publishTime)
  const engagement = calculateEngagementScore(plan)
  // 强调最新，同时兼顾互动
  return freshness * 80 + engagement
}

const meetsFeaturedCriteria = (plan) => {
  const likes = plan.likeCount || 0
  const views = plan.viewCount || 0
  const comments = plan.commentCount || 0
  // 优先尊重后端标记，其次使用阈值判定
  return !!plan.isFeatured ||
    (likes >= 200 && comments >= 15) ||
    (likes >= 120 && views >= 8000) ||
    (comments >= 30 && views >= 5000)
}

const calculateFeaturedScore = (plan) => {
  const engagement = calculateEngagementScore(plan)
  const freshness = calculateFreshnessScore(plan.createTime || plan.publishTime)
  return engagement * 0.85 + freshness * 40
}

const difficultyLabels = ['非常简单', '简单', '一般', '有挑战', '困难']
const getDifficultyLabel = (level) => {
  if (!level || level <= 0) {
    return ''
  }
  const index = Math.min(level, difficultyLabels.length) - 1
  return difficultyLabels[index] || ''
}

const travelTypeMap = {
  1: '自由行',
  2: '跟团游',
  3: '自驾游',
  4: '背包客',
  5: '定制游'
}

const getTravelTypeLabel = (type) => {
  if (type == null) {
    return ''
  }
  return travelTypeMap[type] || ''
}

// 切换分类
const switchCategory = function(category) {
  activeCategory.value = category
  resetAndLoad()
}

// 筛选变化
const handleFilterChange = () => {
  resetAndLoad()
}

// 搜索关键字与话题
const activeTopic = ref('')
let searchDebounceTimer: any = null

// 输入框搜索（自动检索，带简单防抖）
const handleSearchInput = () => {
  // 手动输入时，清除话题筛选，仅按关键字搜索
  activeTopic.value = ''
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer)
  }
  searchDebounceTimer = setTimeout(() => {
    resetAndLoad()
  }, 400)
}

const clearSearchKeyword = () => {
  searchKeyword.value = ''
  activeTopic.value = ''
  resetAndLoad()
}

// 重置并加载
const resetAndLoad = () => {
  currentPage.value = 1
  plans.value = []
  noMore.value = false
  loadPlans()
}

// 加载更多
const loadMore = () => {
  if (!loading.value && !noMore.value) {
    currentPage.value++
    loadPlans()
  }
}

// 记录搜索日志（攻略社区）
const recordSearchLog = async (keyword: string) => {
  if (!keyword || !keyword.trim()) {
    return
  }
  try {
    const userInfo = getCurrentUserInfo()
    const userId = userInfo?.id || userInfo?.userId || null
    
    // 调用搜索接口记录日志（使用travel-plans类型）
    await request.get('/search/travel-plans', {
      params: {
        keyword: keyword.trim(),
        page: 1,
        size: 1
      }
    })
  } catch (error) {
    // 记录搜索日志失败不应该影响搜索功能
    console.warn('记录搜索日志失败:', error)
  }
}

// 加载攻略列表
const loadPlans = async () => {
  loading.value = true
  try {
    let response
    
    // 根据不同分类调用不同的接口
    // 重要：只查询已发布状态(status=1)的攻略，不判断审核状态
    const keywordParam = (() => {
      const k = (activeTopic.value || searchKeyword.value || '').trim()
      return k ? k : undefined
    })()
    
    // 如果有关键词，记录搜索日志
    if (keywordParam) {
      recordSearchLog(keywordParam)
    } else if (selectedDestination.value) {
      // 如果只有目的地选择而没有关键词，也将目的地作为关键词记录搜索日志
      recordSearchLog(selectedDestination.value)
    }

    if (activeCategory.value === 'hot') {
      // 推荐攻略：按浏览量和收藏量综合排序
      const params = {
        page: currentPage.value,
        size: pageSize.value,
        status: 1, // 严格要求：只查询已发布状态
        keyword: keywordParam,
        destination: selectedDestination.value || undefined
      }
      response = await request.get('/travel-plan/list', { params })
      
      // 前端排序：按浏览量和收藏量综合得分降序
      // 综合得分 = 浏览量 * 0.6 + 收藏量 * 0.4
      if (response.code === 200 && response.data?.list) {
        response.data.list = response.data.list.sort(function(a, b) {
          const aViewCount = a.viewCount || 0
          const aCollectCount = a.collectCount || 0
          const bViewCount = b.viewCount || 0
          const bCollectCount = b.collectCount || 0
          
          // 计算综合得分
          const aScore = aViewCount * 0.6 + aCollectCount * 0.4
          const bScore = bViewCount * 0.6 + bCollectCount * 0.4
          
          return bScore - aScore
        })
      }
    } else if (activeCategory.value === 'latest') {
      // 最新攻略：按创建时间排序
      const params = {
        page: currentPage.value,
        size: pageSize.value,
        status: 1, // 严格要求：只查询已发布状态
        keyword: keywordParam,
        destination: selectedDestination.value || undefined
      }
      response = await request.get('/travel-plan/list', { params })
      
      if (response.code === 200 && response.data?.list) {
        response.data.list = response.data.list.sort(function(a, b) {
          return calculateDiscoveryScore(b) - calculateDiscoveryScore(a)
        })
      }
    } else if (activeCategory.value === 'featured') {
      // 精华攻略：按点赞数排序
      const params = {
        page: currentPage.value,
        size: pageSize.value,
        status: 1, // 严格要求：只查询已发布状态
        keyword: keywordParam,
        destination: selectedDestination.value || undefined
      }
      response = await request.get('/travel-plan/list', { params })
      
      // 前端筛选：优先显示符合加精标准的攻略
      if (response.code === 200 && response.data?.list) {
        const fetchedList = response.data.list
        const featuredList = fetchedList
          .filter(function(plan) {
            return meetsFeaturedCriteria(plan)
          })
          .sort(function(a, b) {
            return calculateFeaturedScore(b) - calculateFeaturedScore(a)
          })
        
        if (featuredList.length < pageSize.value) {
          const remainingPlans = fetchedList
            .filter(function(plan) {
              return !meetsFeaturedCriteria(plan)
            })
            .sort(function(a, b) {
              return calculateFeaturedScore(b) - calculateFeaturedScore(a)
            })
          featuredList.push(...remainingPlans.slice(0, pageSize.value - featuredList.length))
        }
        
        response.data.list = featuredList
      }
    } else if (activeCategory.value === 'followed') {
      // 关注动态：只显示关注的作者的攻略，排除自己的攻略
      const currentUserId = getCurrentUserId()
      
      if (!currentUserId) {
        // 未登录，返回空列表
        response = { code: 200, data: { list: [], total: 0 } }
      } else {
        // 获取关注的用户列表（静默处理，不显示错误）
        let followedUserIds: number[] = []
        try {
          // 使用 axios 直接调用，避免触发全局错误处理
          const axios = (await import('axios')).default
          const token = sessionStorage.getItem('travel_token') || localStorage.getItem('travel_token')
          const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
          
          try {
            const followResponse = await axios.get(`${baseURL}/user/follow/following-list`, {
              params: {
                userId: currentUserId,
                page: 1,
                limit: 1000
              },
              headers: token ? { Authorization: `Bearer ${token}` } : {},
              validateStatus: () => true // 不抛出任何HTTP状态码错误
            })
            
            // 检查响应数据
            if (followResponse.data && followResponse.data.code === 200 && Array.isArray(followResponse.data.data)) {
              followedUserIds = followResponse.data.data
                .map((item: any) => item.followeeId)
                .filter((id: any) => id && typeof id === 'number')
            }
          } catch (err) {
            // 完全静默，不输出任何错误
            followedUserIds = []
          }
        } catch (error) {
          // 完全静默，不输出任何错误
          followedUserIds = []
        }
        
        if (followedUserIds.length === 0) {
          // 没有关注任何用户，返回空列表（静默处理，不报错）
          response = { code: 200, data: { list: [], total: 0 } }
        } else {
          // 获取攻略列表
          try {
            const params = {
              page: currentPage.value,
              size: pageSize.value * 2, // 获取更多数据，因为需要过滤
              status: 1, // 严格要求：只查询已发布状态
              keyword: keywordParam,
              destination: selectedDestination.value || undefined
            }
            response = await request.get('/travel-plan/list', { params }).catch(() => {
              // 获取攻略列表失败，返回空列表
              return { code: 200, data: { list: [], total: 0 } }
            })
            
            // 前端过滤：只显示关注的用户的攻略，排除自己的攻略
            if (response && response.code === 200 && response.data?.list) {
              response.data.list = response.data.list.filter((plan: any) => {
                const planAuthorId = plan.authorId || plan.userId
                // 只显示关注的用户的攻略，且不是自己的攻略
                return planAuthorId && 
                       followedUserIds.includes(planAuthorId) && 
                       planAuthorId !== currentUserId
              })
              // 更新总数
              if (response.data.total !== undefined) {
                response.data.total = response.data.list.length
              }
            } else {
              // 响应格式不正确，返回空列表
              response = { code: 200, data: { list: [], total: 0 } }
            }
          } catch (error) {
            // 获取攻略列表出错，静默处理
            response = { code: 200, data: { list: [], total: 0 } }
          }
        }
      }
    }
    
    if (response && response.code === 200 && response.data) {
      const planList = response.data.list || []
      
      // 严格过滤：只显示已发布状态(status=1)的攻略，不判断审核状态
      const filteredPlans = planList.filter(function(plan) {
        // 只判断发布状态：status === 1
        return plan.status === 1
      })
      
      // 前端筛选：出行天数和预算范围
      let finalFilteredPlans = filteredPlans
      
      // 筛选出行天数
      if (selectedDays.value) {
        const daysNum = parseInt(selectedDays.value)
        if (!isNaN(daysNum) && daysNum > 0) {
          finalFilteredPlans = finalFilteredPlans.filter(function(plan) {
            const planDays = plan.days || 1
            // 允许±1天的误差
            return planDays >= daysNum - 1 && planDays <= daysNum + 1
          })
        }
      }
      
      // 筛选预算范围
      if (selectedBudget.value) {
        const budgetStr = selectedBudget.value.trim()
        // 支持格式：纯数字、"1000-5000"、"1000+"、"<5000"等
        if (budgetStr.includes('-')) {
          // 范围格式：1000-5000
          const parts = budgetStr.split('-')
          const minStr = parts[0].trim()
          const maxStr = parts[1] ? parts[1].trim() : ''
          const min = parseInt(minStr) || 0
          const max = maxStr ? parseInt(maxStr) || Infinity : Infinity
          finalFilteredPlans = finalFilteredPlans.filter(function(plan) {
            const planBudget = plan.budget || 0
            return planBudget >= min && planBudget <= max
          })
        } else if (budgetStr.endsWith('+')) {
          // 最小值格式：1000+
          const min = parseInt(budgetStr.replace('+', '')) || 0
          finalFilteredPlans = finalFilteredPlans.filter(function(plan) {
            const planBudget = plan.budget || 0
            return planBudget >= min
          })
        } else if (budgetStr.startsWith('<')) {
          // 最大值格式：<5000
          const max = parseInt(budgetStr.replace('<', '')) || Infinity
          finalFilteredPlans = finalFilteredPlans.filter(function(plan) {
            const planBudget = plan.budget || 0
            return planBudget <= max
          })
        } else {
          // 纯数字：精确匹配（允许±20%误差）
          const target = parseInt(budgetStr) || 0
          if (target > 0) {
            finalFilteredPlans = finalFilteredPlans.filter(function(plan) {
              const planBudget = plan.budget || 0
              const tolerance = target * 0.2
              return planBudget >= target - tolerance && planBudget <= target + tolerance
            })
          }
        }
      }
      
      // 处理数据格式，确保coverImage正确显示
      const formattedPlans = finalFilteredPlans.map(function(plan) {
        // 如果没有coverImage，从images字段提取第一张
        let coverImage = plan.coverImage
        if (!coverImage && plan.images) {
          const imageList = typeof plan.images === 'string' 
            ? plan.images.split(',').filter(function(img) { return img.trim() })
            : plan.images
          if (imageList && imageList.length > 0) {
            coverImage = imageList[0]
          }
        }
        
        // 处理标签（从tags字段解析）
        let tags = []
        if (plan.tags) {
          if (Array.isArray(plan.tags)) {
            tags = plan.tags
          } else if (typeof plan.tags === 'string' && plan.tags.trim()) {
            tags = plan.tags.split(',').filter(Boolean).map(function(t) { return t.trim() })
          }
        }
        
        // 根据作者积分计算等级（统一使用升级指南的计算方式）
        const authorPoints = plan.authorPoints !== undefined && plan.authorPoints !== null ? plan.authorPoints : 0
        const authorLevelInfo = getLevelByPoints(authorPoints)
        
        const levelName = authorLevelInfo.name
        const levelColor = authorLevelInfo.color
        const levelGradient = authorLevelInfo.gradient || { start: authorLevelInfo.color, end: authorLevelInfo.color }
        const derivedFeatured = meetsFeaturedCriteria(plan)
        const difficultyLabel = getDifficultyLabel(plan.difficultyLevel)
        const travelTypeLabel = getTravelTypeLabel(plan.type ?? plan.travelType)
        
        return {
          id: plan.id,
          title: plan.title,
          destination: plan.destination,
          description: plan.description || '',
          coverImage: coverImage || '',
          days: plan.days || 1,
          budget: plan.budget || 0,
          authorId: plan.authorId || plan.userId || plan.author?.id,
          authorName: plan.author || plan.authorName || '匿名用户',
          authorAvatar: plan.authorAvatar || '',
          viewCount: plan.viewCount || 0,
          commentCount: plan.commentCount || 0,
          likeCount: plan.likeCount || 0,
          collectCount: plan.collectCount || 0,
          isLiked: false, // 需要后端支持
          isFeatured: derivedFeatured,
          createTime: plan.createTime || plan.publishTime || new Date().toISOString(),
          imageHeight: 200 + Math.floor(Math.random() * 60), // 卡片更紧凑
          tags: tags,
          levelName: levelName,
          levelColor: levelColor,
          levelGradient: levelGradient,
          bestSeason: plan.bestSeason || '',
          suitableFor: plan.suitableFor || '',
          difficultyLevel: plan.difficultyLevel || null,
          difficultyLabel: difficultyLabel,
          people: plan.people || null,
          travelType: plan.type ?? plan.travelType ?? null,
          travelTypeLabel: travelTypeLabel
        }
      })
      
      if (currentPage.value === 1) {
        plans.value = formattedPlans
      } else {
        plans.value.push(...formattedPlans)
      }
      
      // 判断是否还有更多数据
      if (formattedPlans.length < pageSize.value) {
        noMore.value = true
      } else {
        noMore.value = false
      }
    } else {
      throw new Error(response?.message || '加载失败')
    }
  } catch (error) {
    console.error('加载攻略失败:', error)
    ElMessage.error(error?.message || '加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = function(plan) {
  router.push(`/home/user/plans/detail/${plan.id}`)
}

// 查看用户主页
const viewUserProfile = function(userId) {
  if (!userId) {
    ElMessage.warning('用户ID不存在')
    return
  }
  router.push(`/home/user/profile/${userId}`)
}

// 点赞
const toggleLike = async function(plan) {
  try {
    // TODO: 调用API
    plan.isLiked = !plan.isLiked
    plan.likeCount += plan.isLiked ? 1 : -1
    ElMessage.success(plan.isLiked ? '点赞成功' : '已取消点赞')
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  }
}

// 格式化数字
const formatNumber = function(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 格式化时间
const formatTime = function(time) {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 加载热门作者
const loadTopAuthors = async () => {
  try {
    const response = await request.get('/community/top-authors', {
      params: {
        limit: 10
      }
    })
    if (response.code === 200 && response.data) {
      topAuthors.value = (response.data || []).map((author: any) => ({
        id: author.id,
        name: author.name || '匿名用户',
        avatar: author.avatar || '',
        planCount: author.planCount || 0,
        totalLikes: author.totalLikes || 0,
        points: 0
      }))
    }
  } catch (error) {
    console.error('加载热门作者失败:', error)
    topAuthors.value = []
  }
}

// 热门标签 / 目的地的统计阈值
const MIN_HOT_TAG_COUNT = 2
const MAX_HOT_TAGS = 10
const MIN_HOT_DEST_COUNT = 2
const MAX_HOT_DEST = 10

// 加载热门话题（从攻略标签中提取）
const loadHotTopics = async () => {
  try {
    // 获取攻略列表，提取标签统计
    const response = await request.get('/travel-plan/list', {
      params: {
        page: 1,
        size: 300, // 采样更多数据做统计
        status: 1
      }
    })
    if (response.code === 200 && response.data?.list) {
      const tagMap = new Map<string, number>()
      const plans = response.data.list || []
      
      plans.forEach((plan: any) => {
        let tags: string[] = []
        if (plan.tags) {
          if (Array.isArray(plan.tags)) {
            tags = plan.tags
          } else if (typeof plan.tags === 'string') {
            tags = plan.tags.split(',').filter(Boolean).map((t: string) => t.trim())
          }
        }
        tags.forEach((tag: string) => {
          tagMap.set(tag, (tagMap.get(tag) || 0) + 1)
        })
      })
      
      hotTopics.value = Array.from(tagMap.entries())
        // 过滤掉出现次数太少或名称太奇怪的标签
        .filter(([name, count]) => {
          const c = count as number
          return !!name && name.length <= 8 && c >= MIN_HOT_TAG_COUNT
        })
        .map(([name, count]) => ({
          id: name,
          name,
          count,
          trend: Math.random() > 0.5 ? 'up' : 'down'
        }))
        .sort((a, b) => (b.count || 0) - (a.count || 0))
        .slice(0, MAX_HOT_TAGS)
    }
  } catch (error) {
    console.error('加载热门话题失败:', error)
    hotTopics.value = []
  }
}

// 加载最新评论
const loadLatestComments = async () => {
  try {
    const response = await request.get('/community/latest-comments', {
      params: {
        limit: 5
      }
    })
    if (response.code === 200 && response.data) {
      latestComments.value = (response.data || []).map((comment: any) => ({
        id: comment.id,
        username: comment.username || '匿名用户',
        avatar: comment.avatar || '',
        content: comment.content || '', // 显示完整内容，由CSS处理溢出
        createdTime: comment.createdTime,
        contentId: comment.contentId,
        planTitle: comment.planTitle || '攻略'
      }))
    }
  } catch (error) {
    console.error('加载最新评论失败:', error)
    latestComments.value = []
  }
}


// 加载精选攻略（用于资讯头条）
const loadFeaturedPlans = async () => {
  try {
    const response = await request.get('/travel-plan/list', {
      params: {
        page: 1,
        size: 20,
        status: 1
      }
    })
    if (response.code === 200 && response.data?.list) {
      featuredPlans.value = (response.data.list || [])
        .sort((a: any, b: any) => {
          // 按浏览量和收藏量综合排序（与推荐排序逻辑一致）
          const aViewCount = a.viewCount || 0
          const aCollectCount = a.collectCount || 0
          const bViewCount = b.viewCount || 0
          const bCollectCount = b.collectCount || 0
          
          // 计算综合得分
          const aScore = aViewCount * 0.6 + aCollectCount * 0.4
          const bScore = bViewCount * 0.6 + bCollectCount * 0.4
          
          return bScore - aScore
        })
        .slice(0, 4)
        .map((plan: any) => {
          let coverImage = plan.coverImage
          if (!coverImage && plan.images) {
            const imageList = typeof plan.images === 'string' 
              ? plan.images.split(',').filter((img: string) => img.trim())
              : plan.images
            if (imageList && imageList.length > 0) {
              coverImage = imageList[0]
            }
          }
          // 处理标签
          let tags = plan.tags || []
          if (typeof tags === 'string') {
            tags = tags.split(',').filter(Boolean).map((t: string) => t.trim())
          } else if (!Array.isArray(tags)) {
            tags = []
          }
          
          return {
            id: plan.id,
            title: plan.title,
            coverImage: coverImage || '',
            authorName: plan.author || plan.authorName || '匿名用户',
            authorAvatar: plan.authorAvatar || '',
            viewCount: plan.viewCount || 0,
            commentCount: plan.commentCount || 0,
            likeCount: plan.likeCount || 0,
            collectCount: plan.collectCount || 0,
            destination: plan.destination || '',
            days: plan.days || 0,
            budget: plan.budget || 0,
            tags: tags,
            createTime: plan.createTime || plan.publishTime || ''
          }
        })
    }
  } catch (error) {
    console.error('加载精选攻略失败:', error)
    featuredPlans.value = []
  }
}

// 加载热门文章
const loadHotArticles = async () => {
  try {
    // 根据时间段映射
    let period = 'today'
    if (hotArticlePeriod.value === 'week') {
      period = 'week'
    } else if (hotArticlePeriod.value === 'month') {
      period = 'month'
    }
    
    const response = await request.get('/community/hot-articles', {
      params: {
        period: period,
        limit: 10
      }
    })
    if (response.code === 200 && response.data) {
      hotArticles.value = (response.data || []).map((article: any) => ({
        id: article.id,
        title: article.title?.substring(0, 40) + (article.title?.length > 40 ? '...' : ''),
        authorName: article.authorName || '匿名用户',
        viewCount: article.viewCount || 0
      }))
    }
  } catch (error) {
    console.error('加载热门文章失败:', error)
    hotArticles.value = []
  }
}

// 加载热门目的地
const loadHotDestinations = async () => {
  try {
    const response = await request.get('/travel-plan/list', {
      params: {
        page: 1,
        size: 300,
        status: 1
      }
    })
    if (response.code === 200 && response.data?.list) {
      const destMap = new Map<string, number>()
      const plans = response.data.list || []
      
      plans.forEach((plan: any) => {
        const dest = plan.destination
        if (dest) {
          destMap.set(dest, (destMap.get(dest) || 0) + 1)
        }
      })
      
      hotDestinations.value = Array.from(destMap.entries())
        .filter(([name, count]) => {
          const c = count as number
          return !!name && c >= MIN_HOT_DEST_COUNT
        })
        .map(([name, count]) => ({ name, count }))
        .sort((a, b) => (b.count || 0) - (a.count || 0))
        .slice(0, MAX_HOT_DEST)
    }
  } catch (error) {
    console.error('加载热门目的地失败:', error)
    hotDestinations.value = []
  }
}

// 加载社区统计数据
const loadCommunityStats = async () => {
  try {
    const period = statsPeriod.value === 'week' ? 'week' : 'all'
    const response = await request.get('/statistics/community', {
      params: {
        period: period
      }
    })
    if (response.code === 200 && response.data) {
      communityStats.value = {
        totalPlans: response.data.totalPlans || 0,
        activeUsers: response.data.activeUsers || 0,
        totalViews: response.data.totalViews || 0,
        totalComments: response.data.totalComments || 0
      }
    }
  } catch (error) {
    console.error('加载社区统计失败:', error)
    // 失败时保持默认值
    communityStats.value = {
      totalPlans: 0,
      activeUsers: 0,
      totalViews: 0,
      totalComments: 0
    }
  }
}

// 按目的地搜索
const searchByDestination = (destName: string) => {
  // 再次点击同一目的地时取消筛选
  if (selectedDestination.value === destName) {
    selectedDestination.value = ''
  } else {
    selectedDestination.value = destName
    // 记录搜索日志（将目的地作为关键词）
    recordSearchLog(destName)
  }
  // 点击热门目的地时清空关键字和话题，只按目的地筛选
  searchKeyword.value = ''
  activeTopic.value = ''
  resetAndLoad()
}


// 按话题搜索
const searchByTopic = (topicName: string) => {
  // 再次点击同一话题时取消筛选
  if (activeTopic.value === topicName) {
    activeTopic.value = ''
  } else {
    activeTopic.value = topicName
  }
  // 点击热门标签时清空关键字和目的地，只按话题筛选
  searchKeyword.value = ''
  selectedDestination.value = ''
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer)
  }
  resetAndLoad()
}

// 查看攻略详情
const viewPlanDetail = (planId: number) => {
  router.push(`/home/user/plans/detail/${planId}`)
}


// 初始化函数
const initializePage = () => {
  loadPlans()
  loadTopAuthors()
  loadHotTopics()
  loadLatestComments()
  loadFeaturedPlans()
  loadHotArticles()
  loadHotDestinations()
  loadCommunityStats()
}

onMounted(() => {
  initializePage()
})

// 如果使用了 keep-alive，组件激活时也需要处理
onActivated(() => {
  initializePage()
})

onBeforeUnmount(() => {
})

// 监听路由变化，重新计算位置
watch(() => route.path, (newPath, oldPath) => {
  if (newPath === oldPath) return
  // 可根据需要在此重新加载数据
}, { immediate: false })

// 监听热门文章周期变化
watch(hotArticlePeriod, () => {
  loadHotArticles()
})

// 监听统计周期变化
watch(statsPeriod, () => {
  loadCommunityStats()
})
</script>

<style lang="scss" scoped>
.community-page {
  min-height: 100vh;
  background: #f5f7fa;
  position: relative;
  padding: 24px;
  padding-bottom: 40px;
  overflow: hidden;

  // 全局卡片标题样式
  :deep(.card-header) {
    display: flex !important;
    align-items: center !important;
    gap: 4px !important;
    font-weight: 600;
    font-size: 15px;
    color: #303133;

    .el-icon {
      color: #909399;
      font-size: 16px;
    }
  }

  // 页面头部
  .page-header {
    margin-bottom: 24px;
    padding: 32px;
    background: white;
    border-radius: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    }
    
    .header-content {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .header-icon {
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #606266;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      }
      
      .header-text {
        h2 {
          margin: 0 0 8px 0;
          font-size: 32px;
          font-weight: 700;
          color: #303133;
        }
        
        .page-desc {
          margin: 0;
          color: #909399;
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
      background: #f5f5f5;
      border: 1px solid #e4e7ed;
      border-radius: 30px;
      color: #606266;
      font-size: 14px;
      
      .el-icon {
        font-size: 16px;
        color: #909399;
      }
    }
  }
  
  // 响应式设计

  @media (max-width: 768px) {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      padding: 24px;
      
      .header-content {
        .header-icon {
          width: 60px;
          height: 60px;
          
          :deep(.el-icon) {
            font-size: 36px;
          }
        }
        
        .header-text {
          h2 {
            font-size: 24px;
          }
          
          .page-desc {
            font-size: 14px;
          }
        }
      }
      
      .last-update {
        width: 100%;
        justify-content: center;
      }
    }
    // 两列卡片在小屏幕自动切换为一列
    .plans-list {
      grid-template-columns: 1fr;
    }

    .content-center {
      .hot-topic-links {
        .search-right {
          max-width: 100%;
        }
      }
    }
  }



  // 内容提示
  .content-tip {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    margin-bottom: 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border-left: 3px solid #667eea;
    font-size: 14px;
    font-weight: 600;
    color: #667eea;
    
    .tip-icon {
      font-size: 16px;
    }
  }

  // 攻略列表（改为列表布局）
  .plans-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;

    .plan-card {
      margin-bottom: 0;
      background: white;
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s;
      border: 1px solid #f0f0f0;
      
      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        
        .card-cover img {
          transform: none;
          scale: 1;
        }
      }
      
      .card-cover {
        position: relative;
        overflow: hidden;
        background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: none;
          transform: none;
        }
        
        &:hover img {
          transform: none;
          scale: 1;
        }
        
        .featured-badge {
          position: absolute;
          top: 12px;
          left: 12px;
          display: flex;
          align-items: center;
          gap: 4px;
          padding: 6px 12px;
          background: #ffd700;
          color: #8B4513;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 600;
        }
      }
      
      .card-info {
        padding: 20px;
        
        .card-title {
          margin: 0 0 12px;
          font-size: 18px;
          font-weight: 700;
          color: #303133;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .card-destination {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 12px;
          font-size: 14px;
          color: #667eea;
          font-weight: 600;
          
          .el-icon {
            font-size: 16px;
          }
        }
        
        .card-desc {
          margin: 0 0 16px;
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .card-quick-info {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          margin-bottom: 16px;
          
          .info-chip {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 12px;
            border-radius: 10px;
            background: #f5f7ff;
            color: #606266;
            font-size: 12px;
            font-weight: 500;
            
            .el-icon {
              font-size: 14px;
              color: inherit;
            }
          }
        }
        
        .author-info {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 0;
          border-top: 2px solid #f5f5f5;
          border-bottom: 2px solid #f5f5f5;
          margin-bottom: 12px;
          
          .author-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 4px;
            
            .author-name-row {
              display: flex;
              align-items: center;
              flex-wrap: wrap;
              gap: 8px;
            }
            
            .author-name {
              font-size: 14px;
              font-weight: 600;
              color: #303133;
            }
            
            .author-level {
              font-size: 11px;
              font-weight: 600;
              padding: 3px 10px;
              border-radius: 12px;
              background: linear-gradient(135deg, var(--level-color-start, #409eff), var(--level-color-end, #66b1ff));
              color: white;
              box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
              white-space: nowrap;
            }
            
            .plan-tag {
              font-size: 12px;
              font-weight: 500;
              padding: 2px 6px;
              border-radius: 4px;
              
              &.tag-color-0 {
                color: #667eea;
                background: rgba(102, 126, 234, 0.1);
              }
              
              &.tag-color-1 {
                color: #f093fb;
                background: rgba(240, 147, 251, 0.1);
              }
              
              &.tag-color-2 {
                color: #4facfe;
                background: rgba(79, 172, 254, 0.1);
              }
              
              &.tag-color-3 {
                color: #43e97b;
                background: rgba(67, 233, 123, 0.1);
              }
              
              &.tag-color-4 {
                color: #fa709a;
                background: rgba(250, 112, 154, 0.1);
              }
              
              &.tag-color-5 {
                color: #f57c00;
                background: rgba(254, 225, 64, 0.15);
              }
            }
            
            .publish-time {
              font-size: 12px;
              color: #909399;
            }
          }
        }
        
        .card-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .meta-stats {
            display: flex;
            gap: 16px;
            
            .stat-item {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 13px;
              color: #909399;
              cursor: pointer;
              transition: all 0.3s;
              padding: 4px 8px;
              border-radius: 8px;
              
              .el-icon {
                font-size: 16px;
              }
              
              &:hover {
                background: rgba(102, 126, 234, 0.1);
                color: #667eea;
              }
              
              &.like-action.is-liked {
                color: #f56c6c;
                
                .el-icon {
                  animation: heartBeat 0.6s ease-out;
                }
              }
            }
          }
          
          .card-budget {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 16px;
            margin-top: 12px;
            border-top: 1px dashed rgba(0, 0, 0, 0.08);
            
            .budget-label {
              font-size: 14px;
              font-weight: 600;
              color: #909399;
              letter-spacing: 0.5px;
            }
            
            .budget-value {
              font-family: 'KaiTi', 'STKaiti', 'Kaiti SC', 'Songti SC', serif;
              font-size: 22px;
              font-weight: 700;
              color: #f5576c;
              letter-spacing: -0.5px;
              text-transform: uppercase;
            }
          }
        }
      }
    }
  }

  // 加载状态
  .loading-container {
    position: relative;
    z-index: 10;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px;
    padding: 60px 20px;
    
    .el-icon {
      font-size: 48px;
      color: white;
      filter: drop-shadow(0 4px 20px rgba(255, 255, 255, 0.3));
    }
    
    span {
      color: white;
      font-size: 16px;
      font-weight: 600;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }
  }

  .no-more {
    padding: 30px 20px;
    
    :deep(.el-divider) {
      .el-divider__text {
        background: transparent;
        color: #909399;
        font-weight: 600;
        font-size: 14px;
        letter-spacing: 1px;
        padding: 0 20px;
      }
      
      .el-divider__line {
        background: #e4e7ed;
      }
    }
  }

  :deep(.el-empty) {
    padding: 100px 20px;
    
    .el-empty__image {
      width: 200px;
      height: 200px;
      
      svg {
        fill: rgba(255, 255, 255, 0.6);
      }
    }
    
    .el-empty__description {
      p {
        color: white;
        font-size: 18px;
        font-weight: 600;
      }
    }
  }

  // 主要内容布局 - 三栏布局
  .main-content-layout {
    display: block;
    position: relative;

    .left-nav-sidebar {
      width: 200px;
      position: fixed;
      left: 24px;
      top: 343px; // 继续下移 3px
      max-height: calc(100vh - 343px - 24px);
      overflow-y: auto;
      z-index: 100;
    }

    .content-center {
      // 左侧栏: left 24px + width 200px = 224px, gap 20px, 所以从 244px 开始
      // 页面 padding 24px，所以 margin-left: 244 - 24 = 220px
      margin-left: 220px;
      // 右侧栏: right 24px + width 300px = 324px, gap 20px, 所以右边需要 344px
      margin-right: 344px;
      min-width: 0;
      width: calc(100% - 220px - 344px); // 确保宽度正确
    }

    .sidebar-right {
      width: 300px;
      position: fixed;
      right: 24px;
      top: 343px; // 与左侧保持对齐
      max-height: calc(100vh - 343px - 24px);
      overflow-y: auto;
      z-index: 100;
    }
  }

  // 左侧导航栏
  .left-nav-sidebar {
    .nav-card, .tags-card, .destinations-card {
      margin-bottom: 16px;
      border-radius: 12px;
      border: 1px solid #f0f0f0;

      :deep(.el-card__body) {
        padding: 16px;
      }

      .card-header {
        display: flex;
        align-items: center;
        gap: 4px;
        font-weight: 600;
        font-size: 15px;
        color: #303133;

        .el-icon {
          color: #909399;
          font-size: 16px;
        }
      }
    }

    .nav-menu {
      .nav-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        margin-bottom: 4px;
        color: #606266;
        font-size: 14px;
        border: 1px solid transparent;
        box-sizing: border-box;

        .el-icon {
          font-size: 18px;
        }

        &.active {
          background: #f5f5f5;
          color: #303133;
          font-weight: 600;
          border-color: #909399;
        }
        
        &:hover {
          background: #f5f7fa;
        }
      }

      .nav-divider {
        height: 1px;
        background: #f0f0f0;
        margin: 12px 0;
      }
    }

    .hot-tags-list {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .tag-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 12px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 13px;

        .tag-prefix {
          color: #606266;
          font-weight: 600;
        }
        
        &:hover {
          background: #f5f7fa;
        }

        .tag-name {
          flex: 1;
          color: #303133;
        }

        .tag-count {
          color: #909399;
          font-size: 12px;
        }
      }
    }

    .destinations-list {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .destination-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 13px;

        .el-icon {
          color: #909399;
        }
        
        &:hover {
          background: #f5f7fa;
        }

        span {
          flex: 1;
          color: #303133;
        }

        .dest-count {
          color: #909399;
          font-size: 12px;
        }
      }
    }
  }

  // 中间内容区
  .content-center {
    .hot-topic-links {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 24px;
      padding: 16px 20px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

      .topics-left {
        display: flex;
        align-items: center;
        gap: 12px;
        flex: 1;
        min-width: 0;

        .link-label {
          font-weight: 600;
          color: #303133;
          font-size: 15px;
          flex-shrink: 0;
        }

        .topic-row {
          display: flex;
          gap: 8px;
          overflow-x: auto;
          flex: 1;
          padding-bottom: 2px;

          &::-webkit-scrollbar {
            height: 4px;
          }
        }

        .topic-link {
          font-size: 13px;
          cursor: pointer;
          padding: 6px 14px;
          border-radius: 10px;
          text-decoration: none;
          white-space: nowrap;
          display: inline-flex;
          align-items: center;
          font-weight: 500;
          color: var(--topic-color, #606266);
          background: var(--topic-bg, #f5f7fa);
          border: 1px solid var(--topic-border-color, #e4e7ed);
          box-shadow: var(--topic-shadow, 0 2px 8px rgba(0, 0, 0, 0.06));
          transition: all 0.2s;

          &:hover {
            background: var(--topic-hover-bg, #e4e7ed);
            box-shadow: var(--topic-hover-shadow, 0 4px 12px rgba(0, 0, 0, 0.1));
            border-color: #d3d6db;
          }

          .topic-label {
            letter-spacing: 0.5px;
          }
        }
      }

      .search-right {
        width: 100%;
        max-width: 420px;
        align-self: flex-start;

          .inline-search {
            max-width: 100%;
            
            :deep(.el-input__wrapper) {
              border-radius: 8px;
              border: 1px solid #e4e7ed !important;
              background-color: white !important;
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              padding: 0 16px !important;
              min-height: 44px !important;
              transition: none !important;

              .el-input__inner {
                height: 44px !important;
                line-height: 44px !important;
                font-size: 15px !important;
              }
              
              // 移除所有 hover 和 focus 效果
              &:hover,
              &:focus,
              &:focus-visible,
              &.is-focus,
              &.is-focus:hover,
              &:hover.is-focus {
                border: 1px solid #e4e7ed !important;
                border-color: #e4e7ed !important;
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
                outline: none !important;
                background: white !important;
              }
            }

          :deep(.el-input__inner) {
            color: #303133;
            font-size: 15px !important;
            line-height: 44px !important;
            height: 44px !important;
          
            &::placeholder {
              color: #c0c4cc;
            }
          }

          :deep(.el-input__prefix) {
            color: #909399;
            padding-left: 12px;
          }

          :deep(.el-input__suffix) {
            padding-right: 8px;
            min-width: 80px;
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 4px;
          }

          .suffix-actions {
            display: flex;
            align-items: center;
            gap: 4px;
            
            .clear-btn {
              width: 24px;
              height: 24px;
              padding: 0;
              opacity: 0;
              pointer-events: none;
              transition: opacity 0.3s;
              
              &.is-visible {
                opacity: 1;
                pointer-events: auto;
              }
            }
          }

          :deep(.el-button) {
            background-color: #909399;
            border: none;
            color: white;
            transition: background-color 0.3s;
            flex-shrink: 0;

            &:hover {
              background-color: #a6a9ad;
            }

            &:active {
              background-color: #82848a;
            }
          }
        }
      }

      @media (max-width: 1200px) {
        flex-direction: column;
        align-items: stretch;

        .topics-left {
          width: 100%;
        }

        .search-right {
          width: 100%;
          max-width: 100%;
          min-width: 0;
        }
      }
    }

    .featured-plans-section {
      margin-bottom: 24px;

      .featured-plans-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;

        .featured-plan-card {
          background: white;
          border-radius: 12px;
          overflow: hidden;
          cursor: pointer;
          transition: all 0.3s;
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
          display: flex;
          flex-direction: column;
          border: 1px solid #f0f0f0;

          &:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
            border-color: #e4e7ed;
            
            .featured-image img {
              transform: none;
              scale: 1;
            }
          }

          &.featured-1 {
            grid-column: 1 / -1;
          }

          .featured-image {
            position: relative;
            width: 100%;
            height: 200px;
            overflow: hidden;
            background: linear-gradient(135deg, #f0f0f0, #e8e8e8);

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
              transition: none;
              transform: none;
            }
            
            &:hover img {
              transform: none;
              scale: 1;
            }

            .featured-badge {
              position: absolute;
              top: 12px;
              left: 12px;
              display: flex;
              align-items: center;
              gap: 4px;
              padding: 6px 12px;
              background: linear-gradient(135deg, #ffd700, #ffed4e);
              color: #8B4513;
              border-radius: 20px;
              font-size: 12px;
              font-weight: 600;
              z-index: 2;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
            }
          }

          .featured-content {
            padding: 16px;
            display: flex;
            flex-direction: column;
            gap: 12px;
            flex: 1;

            .featured-header {
              .featured-title {
                margin: 0 0 8px 0;
                font-size: 18px;
                font-weight: 700;
                color: #1f2937;
                line-height: 1.4;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .featured-destination {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 13px;
                color: #667eea;
                font-weight: 500;
                
                .el-icon {
                  font-size: 14px;
                }
                
                .featured-days {
                  margin-left: 4px;
                  padding: 2px 8px;
                  background: rgba(102, 126, 234, 0.1);
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 600;
                  color: #667eea;
                }
              }
            }
            
            .featured-tags {
              display: flex;
              align-items: center;
              gap: 6px;
              flex-wrap: wrap;
              
              .featured-tag {
                padding: 3px 10px;
                background: #f5f7fa;
                border-radius: 12px;
                font-size: 11px;
                color: #4b5563;
                font-weight: 500;
                border: 1px solid #e4e7ed;
                transition: all 0.2s;
                
                &:hover {
                  background: #e4e7ed;
                  border-color: #cbd5e1;
                }
              }
            }

            .featured-meta {
              display: flex;
              align-items: center;
              justify-content: space-between;
              gap: 12px;
              padding-top: 8px;
              border-top: 1px solid #f0f0f0;

              .featured-author {
                display: flex;
                align-items: center;
                gap: 8px;
                
                .author-name {
                  font-size: 13px;
                  color: #4b5563;
                  font-weight: 500;
                }
              }
              
              .featured-stats-group {
                display: flex;
                align-items: center;
                gap: 12px;
              }

              .featured-stats {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: #6b7280;
                
                .el-icon {
                  font-size: 14px;
                  color: #9ca3af;
                }
              }
            }
            
            .featured-footer {
              display: flex;
              align-items: center;
              justify-content: space-between;
              gap: 12px;
              padding-top: 8px;
              border-top: 1px solid #f0f0f0;
              font-size: 12px;
              
              .featured-budget {
                display: flex;
                align-items: center;
                gap: 4px;
                font-weight: 600;
                color: #1f2937;
                
                .el-icon {
                  font-size: 14px;
                  color: #f59e0b;
                }
              }
              
              .featured-time {
                display: flex;
                align-items: center;
                gap: 4px;
                color: #9ca3af;
                
                .el-icon {
                  font-size: 12px;
                }
              }
            }
          }
        }
      }
    }
  }

  // 侧边栏卡片
  .sidebar-card {
    margin-bottom: 16px;
    border-radius: 12px;
    overflow: hidden;
    border: 1px solid #f0f0f0;
    transition: all 0.3s ease;

    // 移除 Element Plus 默认的蓝色外框
    :deep(.el-card) {
      border: none;
      box-shadow: none;
      
      &:hover {
        border: none;
        box-shadow: none;
      }
    }

    // 移除蓝色外框，添加新的悬停效果
    :deep(.el-card__body) {
      transition: background-color 0.3s ease;
    }

    &:hover {
      border-color: #e4e7ed;
      background-color: #fafafa;
      
      :deep(.el-card__body) {
        background-color: #fafafa;
      }
      
      // 确保没有蓝色外框
      :deep(.el-card) {
        border: none !important;
        box-shadow: none !important;
        outline: none !important;
      }
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 4px;
      font-weight: 600;
      font-size: 15px;
      color: #303133;

      .el-icon {
        color: #909399;
        font-size: 16px;
      }
    }

    &.stats-card {
      // 禁用整个卡片的 hover 放大效果，但保留背景色变化
      transform: none !important;
      background: white !important;
      background-image: none !important;
      background-color: white !important;
      overflow: visible !important;

      // 移除全局样式中定义的紫色渐变顶部边框
      &::before {
        display: none !important;
        content: none !important;
        height: 0 !important;
        width: 0 !important;
        background: none !important;
        background-color: transparent !important;
        background-image: none !important;
        position: static !important;
        top: auto !important;
        left: auto !important;
        right: auto !important;
        z-index: -1 !important;
        animation: none !important;
        background-size: auto !important;
      }

      // 隐藏header区域并移除所有背景
      :deep(.el-card__header) {
        display: none !important;
        padding: 0 !important;
        background: none !important;
        background-color: transparent !important;
        border: none !important;
        height: 0 !important;
        min-height: 0 !important;
        margin: 0 !important;
      }

      &:hover {
        transform: none !important;
        scale: 1 !important;
        border-color: #e4e7ed;
        background-color: #fafafa !important;
      }

      :deep(.el-card__body) {
        transform: none !important;
        transition: background-color 0.3s ease;
        padding-top: 16px !important;
        background: white !important;
        background-color: white !important;

        &:hover {
          background-color: #fafafa !important;
        }
      }

      // 确保卡片本身没有渐变背景
      :deep(.el-card) {
        background: white !important;
        background-color: white !important;
        background-image: none !important;
      }

      .stats-content {
        .stats-tabs {
          display: flex;
          gap: 8px;
          margin-bottom: 16px;
          padding-bottom: 12px;
          border-bottom: 1px solid #f0f0f0;

          .stats-tab {
            flex: 1;
            text-align: center;
            padding: 6px 12px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 13px;
            color: #606266;
            transition: all 0.3s;
            border: 1px solid transparent;
            box-sizing: border-box;

            &:hover {
              background: #f5f7fa;
            }

            &.active {
              background: #f5f5f5;
              color: #303133;
              font-weight: 600;
              border-color: #909399;
            }
          }
        }

        .stats-items {
          .stat-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f5f5f5;

            &:last-child {
              border-bottom: none;
            }

            .stat-label {
              font-size: 13px;
              color: #909399;
            }

            .stat-value {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
            }
          }
        }
      }
    }

    .hot-articles-tabs {
      display: flex;
      gap: 4px;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px solid #f0f0f0;

      .article-tab {
        flex: 1;
        text-align: center;
        padding: 6px 8px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 12px;
        color: #606266;
        transition: all 0.3s;
        border: 1px solid transparent;
        box-sizing: border-box;

        &:hover {
          background: #f5f7fa;
        }

        &.active {
          background: #f5f5f5;
          color: #303133;
          font-weight: 600;
          border-color: #909399;
        }
      }
    }

    .hot-articles-list {
      .article-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid #f5f5f5;
        cursor: pointer;
        transition: all 0.3s;

        &:last-child {
          border-bottom: none;
        }

        .article-rank {
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #f5f5f5;
          border: 1px solid #e4e7ed;
          border-radius: 4px;
          font-size: 12px;
          font-weight: 600;
          color: #606266;
          flex-shrink: 0;
        }
        
        &:hover {
          background: #f5f7fa;
        }

        .article-content {
          flex: 1;
          min-width: 0;

          .article-title {
            font-size: 13px;
            font-weight: 500;
            color: #303133;
            line-height: 1.5;
            margin-bottom: 6px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .article-author {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }

  }

  // 热门作者排行榜
  .author-ranking {
    .ranking-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      margin-bottom: 8px;
      
      &:hover {
        background: #f5f7fa;
      }

      .rank-number {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        font-weight: 700;
        font-size: 12px;
        flex-shrink: 0;

        &.rank-1 {
          background: linear-gradient(135deg, #ffd700, #ffed4e);
          color: #8B4513;
        }

        &.rank-2 {
          background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
          color: #606266;
        }

        &.rank-3 {
          background: linear-gradient(135deg, #cd7f32, #e6a857);
          color: white;
        }

        &:not(.rank-1):not(.rank-2):not(.rank-3) {
          background: #f0f0f0;
          color: #909399;
        }
      }

      .author-avatar {
        flex-shrink: 0;
      }

      .author-info {
        flex: 1;
        min-width: 0;

        .author-name {
          font-weight: 600;
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .author-stats {
          display: flex;
          gap: 12px;
          font-size: 12px;
          color: #909399;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 4px;

            .el-icon {
              font-size: 12px;
            }
          }
        }
      }
    }

    .empty-ranking {
      padding: 20px;
    }
  }

  // 热门话题
  .hot-topics {
    .topic-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      margin-bottom: 8px;

      &:hover {
        background: #f5f7fa;
      }

      .topic-rank {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        border-radius: 50%;
        font-weight: 700;
        font-size: 12px;
        flex-shrink: 0;
      }

      .topic-content {
        flex: 1;
        min-width: 0;

        .topic-name {
          font-weight: 600;
          font-size: 14px;
          color: #667eea;
          margin-bottom: 4px;
        }

        .topic-count {
          font-size: 12px;
          color: #909399;
        }
      }

      .trend-up {
        color: #f56c6c;
        font-size: 16px;
      }

      .trend-down {
        color: #67c23a;
        font-size: 16px;
      }
    }

    .empty-topics {
      padding: 20px;
    }
  }

  // 最新评论
  .latest-comments {
    .comment-item {
      display: flex;
      gap: 12px;
      padding: 12px 0;
      border-bottom: 1px solid #f5f5f5;
      transition: background-color 0.2s;
      min-width: 0; // 防止flex子元素溢出

      &:last-child {
        border-bottom: none;
      }

      .comment-avatar {
        flex-shrink: 0;
      }

      .comment-content {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        gap: 6px;

        .comment-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 8px;

          .comment-user {
            font-weight: 600;
            font-size: 13px;
            color: #303133;
            flex-shrink: 0;
          }

          .comment-time {
            font-size: 12px;
            color: #909399;
            flex-shrink: 0;
            white-space: nowrap;
          }
        }

        .comment-text {
          font-size: 13px;
          color: #606266;
          line-height: 1.5;
          word-break: break-word;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          line-clamp: 2;
        }

        .comment-footer {
          display: flex;
          align-items: center;
          gap: 8px;

          .comment-plan-link {
            font-size: 12px;
            color: #606266;
            cursor: pointer;
            transition: color 0.2s;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 100%;
            text-decoration: none;

            &:hover {
              color: #303133;
              text-decoration: underline;
            }
          }
        }
      }
    }

    .empty-comments {
      padding: 20px;
    }
  }


  // 响应式设计
  @media (max-width: 1400px) {
    .main-content-layout {
      .left-nav-sidebar {
        width: 180px;
        left: 20px;
        top: 343px;
        max-height: calc(100vh - 343px - 20px);
      }
      .sidebar-right {
        width: 280px;
        right: 20px;
        top: 343px;
        max-height: calc(100vh - 343px - 20px);
      }
      .content-center {
        // 左侧栏: left 20px + width 180px = 200px, gap 20px, 所以从 220px 开始
        // 中间内容区在 padding 20px 内，所以需要 margin-left: 220 - 20 = 200px
        margin-left: 200px;
        // 右侧栏: right 20px + width 280px, gap 20px
        // 中间内容区需要 margin-right: 20 + 280 + 20 = 320px
        margin-right: 320px;
      }
    }
  }

  @media (max-width: 1200px) {
    .main-content-layout {
      flex-wrap: wrap;

      .left-nav-sidebar {
        width: 100%;
        position: static;
        left: auto;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 16px;
        max-height: none;
      }

      .content-center {
        width: 100%;
        margin-left: 0;
        margin-right: 0;
      }

      .sidebar-right {
        width: 100%;
        position: static;
        right: auto;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 16px;
        max-height: none;
      }
    }
  }

  @media (max-width: 768px) {
    .main-content-layout {
      .left-nav-sidebar {
        grid-template-columns: 1fr;
      }
      .sidebar-right {
        grid-template-columns: 1fr;
      }
      .content-center {
        .featured-plans-grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>

<style lang="scss">
// 全局覆盖社区页面搜索框样式 - 确保优先级最高
.community-page .search-right .inline-search .el-input__wrapper,
.community-page .search-right .inline-search .el-input .el-input__wrapper,
body .community-page .search-right .inline-search .el-input__wrapper,
body .community-page .search-right .inline-search .el-input .el-input__wrapper {
  border-radius: 8px !important;
  border: 1px solid #e4e7ed !important;
  background-color: white !important;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
  padding: 0 16px !important;
  min-height: 44px !important;
  transition: none !important;
  
  .el-input__inner {
    height: 44px !important;
    line-height: 44px !important;
    font-size: 15px !important;
  }
  
  // 移除所有 hover 和 focus 效果
  &:hover,
  &:focus,
  &:focus-visible,
  &.is-focus,
  &.is-focus:hover,
  &:hover.is-focus {
    border: 1px solid #e4e7ed !important;
    border-color: #e4e7ed !important;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
    outline: none !important;
    background: white !important;
  }
}

// 覆盖所有可能的选择器组合
.community-page .search-right .inline-search .el-input__wrapper.is-focus,
.community-page .search-right .inline-search .el-input.is-focus .el-input__wrapper,
.community-page .search-right .inline-search .el-input .el-input__wrapper.is-focus,
body .community-page .search-right .inline-search .el-input__wrapper.is-focus,
body .community-page .search-right .inline-search .el-input.is-focus .el-input__wrapper {
  border: 1px solid #e4e7ed !important;
  border-color: #e4e7ed !important;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
  outline: none !important;
  background: white !important;
}
</style>


