<template>
  <div class="recommendations-page">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><TrendCharts /></el-icon>
        </div>
        <div class="header-text">
          <h2>景点社区</h2>
          <p class="page-desc">基于您的用户画像和浏览偏好，为您智能推荐心仪的旅游景点</p>
        </div>
      </div>
      <div class="last-update">
        <el-icon><Star /></el-icon>
        <span>个性化推荐，发现更多精彩目的地</span>
      </div>
    </div>
    
    <!-- 顶部搜索栏 -->
    <div class="search-bar">
      <div class="search-container">
        <el-input
          v-model="searchKeyword"
          placeholder="在所有景点中搜索...（支持搜索名称、位置、描述、标签、价格等）"
          size="large"
          @input="handleAutoSearch"
          @keyup.enter="handleCustomSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #suffix>
            <el-button 
              type="primary" 
              :icon="Search" 
              circle 
              @click="handleCustomSearch"
            />
          </template>
        </el-input>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filters-section">
      <!-- 分类切换 -->
      <div class="category-tabs">
        <div 
          v-for="category in categories" 
          :key="category.value"
          :class="['category-tab', { 'active': activeCategory === category.value }]"
          @click="switchCategory(category.value)"
        >
          <span class="category-icon">{{ category.icon }}</span>
          <span class="category-name">{{ category.label }}</span>
        </div>
      </div>
      
      <!-- 地区选择器 -->
      <div class="location-filter">
        <span class="location-label">请选择城市</span>
        <el-select 
          ref="locationSelectRef"
          v-model="selectedLocation" 
          placeholder="请选择" 
          clearable
          filterable
          @change="handleLocationChange"
          @visible-change="handleLocationSelectVisibleChange"
          size="large"
          class="location-select-black-focus"
        >
          <el-option-group label="🇨🇳 国内城市">
            <el-option
              v-for="location in domesticLocations"
              :key="location.value"
              :label="location.label"
              :value="location.value"
            >
              <span class="location-icon">{{ location.icon }}</span>
              <span>{{ location.label }}</span>
            </el-option>
          </el-option-group>
          <el-option-group label="🌏 国际城市">
            <el-option
              v-for="location in internationalLocations"
              :key="location.value"
              :label="location.label"
              :value="location.value"
            >
              <span class="location-icon">{{ location.icon }}</span>
              <span>{{ location.label }}</span>
            </el-option>
          </el-option-group>
        </el-select>
      </div>
    </div>

    <!-- 内容状态提示 -->
    <div v-if="selectedLocation && activeCategory === 'recommend'" class="content-tip location-tip">
      <el-icon class="tip-icon"><LocationFilled /></el-icon>
      <span>{{ selectedLocationLabel }} · 基于用户画像推荐</span>
    </div>
    <div v-else-if="selectedLocation && activeCategory === 'all'" class="content-tip location-tip">
      <el-icon class="tip-icon"><LocationFilled /></el-icon>
      <span>{{ selectedLocationLabel }} · 按热度推荐</span>
    </div>
    <div v-else-if="activeCategory === 'recommend'" class="content-tip recommend-tip">
      <el-icon class="tip-icon"><DataAnalysis /></el-icon>
      <span>基于您的用户画像智能推荐</span>
    </div>
    <div v-else-if="activeCategory === 'all'" class="content-tip all-tip">
      <el-icon class="tip-icon"><TrendCharts /></el-icon>
      <span>全部景点 · 按热度排序</span>
    </div>
    <div v-else-if="searchKeyword" class="content-tip search-tip">
      <el-icon class="tip-icon"><Search /></el-icon>
      <span>在所有景点中搜索"{{ searchKeyword }}"</span>
    </div>

    <!-- 景点卡片瀑布流 -->
    <div 
      v-infinite-scroll="loadMore"
      :infinite-scroll-disabled="loading || noMore"
      :infinite-scroll-distance="100"
      class="attractions-waterfall"
    >
      <div 
        v-for="attraction in attractions" 
        :key="attraction.id" 
        class="attraction-card"
        @click="viewDetail(attraction)"
      >
        <!-- 封面图 -->
        <div class="card-cover">
          <img :src="attraction.image" :alt="attraction.name" />
          <div class="card-gradient"></div>
          
          <!-- 类型标签 -->
          <div class="type-badge" :class="`badge-${attraction.type}`">
            {{ attraction.typeName }}
          </div>
        </div>
        
        <!-- 信息层 -->
        <div class="card-info">
          <h3 class="card-title">{{ attraction.name }}</h3>
          
          <div class="card-location">
            <el-icon><Location /></el-icon>
            <span>{{ attraction.location }}</span>
          </div>
          
          <!-- 评分和景区级别 -->
          <div class="card-rating-info" v-if="(attraction.rating && attraction.rating >= 1 && attraction.rating <= 5) || attraction.score">
            <div class="rating-item" v-if="attraction.rating && attraction.rating >= 1 && attraction.rating <= 5">
              <span class="rating-label">级别</span>
              <span class="rating-value">{{ Math.round(attraction.rating) }}A景区</span>
            </div>
            <div class="rating-item" v-if="attraction.score && attraction.score > 0">
              <span class="rating-label">评分</span>
              <span class="rating-value">{{ attraction.score.toFixed(1) }}</span>
              <el-icon class="star-icon"><StarFilled /></el-icon>
            </div>
          </div>
          
          <p class="card-desc">{{ attraction.description }}</p>
          
          <!-- 额外信息 -->
          <div class="card-extra-info" v-if="attraction.openTime || attraction.bestSeason || attraction.avgStayTime > 0">
            <div class="extra-info-item" v-if="attraction.openTime">
              <el-icon><Clock /></el-icon>
              <span>{{ attraction.openTime }}</span>
            </div>
            <div class="extra-info-item" v-if="attraction.bestSeason">
              <el-icon><Sunny /></el-icon>
              <span>最佳：{{ attraction.bestSeason }}</span>
            </div>
            <div class="extra-info-item" v-if="attraction.avgStayTime > 0">
              <el-icon><Timer /></el-icon>
              <span>建议游玩：{{ Math.round(attraction.avgStayTime / 60) }}分钟</span>
            </div>
          </div>
          
          <div class="card-meta">
            <div class="meta-tags">
              <span 
                v-for="tag in attraction.tags.slice(0, 5)" 
                :key="tag" 
                class="meta-tag"
              >
                #{{ tag }}
              </span>
            </div>
            
            <div class="card-actions">
              <div class="action-item">
                <el-icon><View /></el-icon>
                <span>{{ formatNumber(attraction.views) }}</span>
              </div>
              <div class="action-item" v-if="attraction.commentCount > 0">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ formatNumber(attraction.commentCount) }}</span>
              </div>
              <div 
                class="action-item favorite-action"
                :class="{ 'is-favorite': attraction.isFavorite }"
                @click.stop="toggleFavorite(attraction)"
              >
                <el-icon v-if="attraction.isFavorite"><StarFilled /></el-icon>
                <el-icon v-else><Star /></el-icon>
                <span>{{ formatNumber(attraction.favorites) }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-price-section">
            <div class="price-info">
              <span class="price-label">门票</span>
              <span class="price-value">
                {{ attraction.price === 0 ? '免费' : `¥${attraction.price}` }}
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
    
    <div v-if="noMore && attractions.length > 0" class="no-more">
      <el-divider>已经到底了</el-divider>
    </div>

    <el-empty v-if="attractions.length === 0 && !loading" description="暂无推荐景点" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { getCurrentUserId } from '@/utils/user'
import { 
  Location, DataAnalysis, Search,
  Star, StarFilled, Loading, View, ArrowRight, LocationFilled, TrendCharts
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')

// 活动分类
const activeCategory = ref('recommend')  // 默认显示推荐（基于用户画像）

// 选中的地区
const selectedLocation = ref<string | null>(null)

// 城市选择框的引用
const locationSelectRef = ref(null)

// 分类列表（与管理平台创建景点的类型对应）
const categories = ref([
  { value: 'recommend', label: '推荐', icon: '✨' },  // 基于用户画像推荐
  { value: 'all', label: '全部', icon: '📋' },        // 显示所有景点
  { value: '1', label: '自然风光', icon: '🏞️' },  // type=1
  { value: '2', label: '人文历史', icon: '🏛️' },  // type=2
  { value: '3', label: '主题公园', icon: '🎢' },  // type=3
  { value: '4', label: '美食街', icon: '🍜' },    // type=4
  { value: '5', label: '古镇古村', icon: '🏘️' },  // type=5
  { value: '6', label: '温泉度假', icon: '♨️' },  // type=6
  { value: '7', label: '宗教场所', icon: '⛩️' },  // type=7
  { value: '8', label: '购物娱乐', icon: '🛍️' }    // type=8
])

// 地区列表（国内外所有主要城市）
const locations = ref([
  // === 国内城市 ===
  // 直辖市
  { value: 'beijing', label: '北京', icon: '🏛️', region: 'domestic' },
  { value: 'shanghai', label: '上海', icon: '🏙️', region: 'domestic' },
  { value: 'chongqing', label: '重庆', icon: '🌉', region: 'domestic' },
  { value: 'tianjin', label: '天津', icon: '🌃', region: 'domestic' },
  
  // 华东地区
  { value: 'hangzhou', label: '杭州', icon: '🌸', region: 'domestic' },
  { value: 'suzhou', label: '苏州', icon: '🏡', region: 'domestic' },
  { value: 'nanjing', label: '南京', icon: '🏯', region: 'domestic' },
  { value: 'xiamen', label: '厦门', icon: '🏖️', region: 'domestic' },
  { value: 'qingdao', label: '青岛', icon: '🍺', region: 'domestic' },
  { value: 'ningbo', label: '宁波', icon: '🚢', region: 'domestic' },
  { value: 'wuxi', label: '无锡', icon: '🌊', region: 'domestic' },
  { value: 'yangzhou', label: '扬州', icon: '🏮', region: 'domestic' },
  { value: 'shaoxing', label: '绍兴', icon: '🍶', region: 'domestic' },
  { value: 'jinan', label: '济南', icon: '⛲', region: 'domestic' },
  { value: 'yantai', label: '烟台', icon: '🍎', region: 'domestic' },
  { value: 'weihai', label: '威海', icon: '🏖️', region: 'domestic' },
  { value: 'hefei', label: '合肥', icon: '🌳', region: 'domestic' },
  { value: 'huangshan', label: '黄山', icon: '⛰️', region: 'domestic' },
  { value: 'fuzhou', label: '福州', icon: '🌺', region: 'domestic' },
  { value: 'quanzhou', label: '泉州', icon: '🏯', region: 'domestic' },
  
  // 华南地区
  { value: 'guangzhou', label: '广州', icon: '🌺', region: 'domestic' },
  { value: 'shenzhen', label: '深圳', icon: '🏢', region: 'domestic' },
  { value: 'sanya', label: '三亚', icon: '🏝️', region: 'domestic' },
  { value: 'guilin', label: '桂林', icon: '⛰️', region: 'domestic' },
  { value: 'zhuhai', label: '珠海', icon: '🌊', region: 'domestic' },
  { value: 'dongguan', label: '东莞', icon: '🏭', region: 'domestic' },
  { value: 'foshan', label: '佛山', icon: '🥋', region: 'domestic' },
  { value: 'zhongshan', label: '中山', icon: '🏙️', region: 'domestic' },
  { value: 'nanning', label: '南宁', icon: '🌴', region: 'domestic' },
  { value: 'haikou', label: '海口', icon: '🥥', region: 'domestic' },
  { value: 'beihai', label: '北海', icon: '🏖️', region: 'domestic' },
  { value: 'yangshuo', label: '阳朔', icon: '🚣', region: 'domestic' },
  
  // 西南地区
  { value: 'chengdu', label: '成都', icon: '🐼', region: 'domestic' },
  { value: 'kunming', label: '昆明', icon: '🌷', region: 'domestic' },
  { value: 'lijiang', label: '丽江', icon: '🏔️', region: 'domestic' },
  { value: 'dali', label: '大理', icon: '🌅', region: 'domestic' },
  { value: 'lhasa', label: '拉萨', icon: '🏔️', region: 'domestic' },
  { value: 'guiyang', label: '贵阳', icon: '🌄', region: 'domestic' },
  { value: 'jiuzhaigou', label: '九寨沟', icon: '💧', region: 'domestic' },
  { value: 'leshan', label: '乐山', icon: '🗿', region: 'domestic' },
  { value: 'emeishan', label: '峨眉山', icon: '⛰️', region: 'domestic' },
  { value: 'shangri-la', label: '香格里拉', icon: '🏔️', region: 'domestic' },
  { value: 'xishuangbanna', label: '西双版纳', icon: '🦚', region: 'domestic' },
  
  // 西北地区
  { value: 'xian', label: '西安', icon: '🗿', region: 'domestic' },
  { value: 'lanzhou', label: '兰州', icon: '🍜', region: 'domestic' },
  { value: 'yinchuan', label: '银川', icon: '🏜️', region: 'domestic' },
  { value: 'urumqi', label: '乌鲁木齐', icon: '🎿', region: 'domestic' },
  { value: 'xining', label: '西宁', icon: '🏔️', region: 'domestic' },
  { value: 'dunhuang', label: '敦煌', icon: '🐫', region: 'domestic' },
  { value: 'turpan', label: '吐鲁番', icon: '🍇', region: 'domestic' },
  { value: 'kashgar', label: '喀什', icon: '🕌', region: 'domestic' },
  
  // 华北地区
  { value: 'zhengzhou', label: '郑州', icon: '🏛️', region: 'domestic' },
  { value: 'taiyuan', label: '太原', icon: '⛰️', region: 'domestic' },
  { value: 'shijiazhuang', label: '石家庄', icon: '🌾', region: 'domestic' },
  { value: 'baoding', label: '保定', icon: '🏰', region: 'domestic' },
  { value: 'datong', label: '大同', icon: '🗿', region: 'domestic' },
  { value: 'luoyang', label: '洛阳', icon: '🌺', region: 'domestic' },
  { value: 'kaifeng', label: '开封', icon: '🏛️', region: 'domestic' },
  { value: 'hohhot', label: '呼和浩特', icon: '🐎', region: 'domestic' },
  
  // 东北地区
  { value: 'harbin', label: '哈尔滨', icon: '❄️', region: 'domestic' },
  { value: 'dalian', label: '大连', icon: '🌊', region: 'domestic' },
  { value: 'shenyang', label: '沈阳', icon: '🏰', region: 'domestic' },
  { value: 'changchun', label: '长春', icon: '🎬', region: 'domestic' },
  { value: 'jilin', label: '吉林', icon: '❄️', region: 'domestic' },
  { value: 'mudanjiang', label: '牡丹江', icon: '🌸', region: 'domestic' },
  { value: 'qiqihar', label: '齐齐哈尔', icon: '🦌', region: 'domestic' },
  
  // 华中地区
  { value: 'wuhan', label: '武汉', icon: '🌸', region: 'domestic' },
  { value: 'changsha', label: '长沙', icon: '🌶️', region: 'domestic' },
  { value: 'zhangjiajie', label: '张家界', icon: '🏔️', region: 'domestic' },
  { value: 'nanchang', label: '南昌', icon: '🏞️', region: 'domestic' },
  { value: 'yichang', label: '宜昌', icon: '🚢', region: 'domestic' },
  { value: 'jingdezhen', label: '景德镇', icon: '🏺', region: 'domestic' },
  { value: 'fenghuang', label: '凤凰', icon: '🏮', region: 'domestic' },
  
  // 港澳台地区
  { value: 'hongkong', label: '香港', icon: '🏙️', region: 'domestic' },
  { value: 'macau', label: '澳门', icon: '🎰', region: 'domestic' },
  { value: 'taipei', label: '台北', icon: '🗼', region: 'domestic' },
  { value: 'kaohsiung', label: '高雄', icon: '⚓', region: 'domestic' },
  { value: 'taichung', label: '台中', icon: '🌆', region: 'domestic' },
  { value: 'tainan', label: '台南', icon: '🏯', region: 'domestic' },
  { value: 'hualien', label: '花莲', icon: '🏔️', region: 'domestic' },
  
  // === 国际城市 ===
  // 东亚
  { value: 'tokyo', label: '东京', icon: '🗼', region: 'international' },
  { value: 'osaka', label: '大阪', icon: '🏯', region: 'international' },
  { value: 'kyoto', label: '京都', icon: '⛩️', region: 'international' },
  { value: 'yokohama', label: '横滨', icon: '🌉', region: 'international' },
  { value: 'nagoya', label: '名古屋', icon: '🏯', region: 'international' },
  { value: 'fukuoka', label: '福冈', icon: '🏙️', region: 'international' },
  { value: 'sapporo', label: '札幌', icon: '❄️', region: 'international' },
  { value: 'nara', label: '奈良', icon: '🦌', region: 'international' },
  { value: 'seoul', label: '首尔', icon: '🏙️', region: 'international' },
  { value: 'busan', label: '釜山', icon: '🏖️', region: 'international' },
  { value: 'jeju', label: '济州岛', icon: '🌺', region: 'international' },
  { value: 'pyongyang', label: '平壤', icon: '🏛️', region: 'international' },
  
  // 东南亚
  { value: 'bangkok', label: '曼谷', icon: '🛕', region: 'international' },
  { value: 'phuket', label: '普吉岛', icon: '🏝️', region: 'international' },
  { value: 'chiangmai', label: '清迈', icon: '🏮', region: 'international' },
  { value: 'pattaya', label: '芭提雅', icon: '🏖️', region: 'international' },
  { value: 'singapore', label: '新加坡', icon: '🦁', region: 'international' },
  { value: 'bali', label: '巴厘岛', icon: '🌴', region: 'international' },
  { value: 'jakarta', label: '雅加达', icon: '🏙️', region: 'international' },
  { value: 'hanoi', label: '河内', icon: '🏞️', region: 'international' },
  { value: 'hochiminh', label: '胡志明市', icon: '🏙️', region: 'international' },
  { value: 'danang', label: '岘港', icon: '🏖️', region: 'international' },
  { value: 'kualalumpur', label: '吉隆坡', icon: '🗼', region: 'international' },
  { value: 'penang', label: '槟城', icon: '🏝️', region: 'international' },
  { value: 'manila', label: '马尼拉', icon: '🏙️', region: 'international' },
  { value: 'boracay', label: '长滩岛', icon: '🏖️', region: 'international' },
  { value: 'yangon', label: '仰光', icon: '🛕', region: 'international' },
  { value: 'phnompenh', label: '金边', icon: '🏛️', region: 'international' },
  { value: 'angkorwat', label: '吴哥窟', icon: '🏯', region: 'international' },
  
  // 南亚
  { value: 'dubai', label: '迪拜', icon: '🏜️', region: 'international' },
  { value: 'abudhabi', label: '阿布扎比', icon: '🕌', region: 'international' },
  { value: 'maldives', label: '马尔代夫', icon: '🏝️', region: 'international' },
  { value: 'mumbai', label: '孟买', icon: '🕌', region: 'international' },
  { value: 'delhi', label: '新德里', icon: '🏛️', region: 'international' },
  { value: 'agra', label: '阿格拉', icon: '🕌', region: 'international' },
  { value: 'jaipur', label: '斋浦尔', icon: '🏰', region: 'international' },
  { value: 'kathmandu', label: '加德满都', icon: '🏔️', region: 'international' },
  { value: 'colombo', label: '科伦坡', icon: '🏖️', region: 'international' },
  
  // 欧洲
  { value: 'paris', label: '巴黎', icon: '🗼', region: 'international' },
  { value: 'london', label: '伦敦', icon: '🎡', region: 'international' },
  { value: 'rome', label: '罗马', icon: '🏛️', region: 'international' },
  { value: 'barcelona', label: '巴塞罗那', icon: '🏰', region: 'international' },
  { value: 'madrid', label: '马德里', icon: '🏛️', region: 'international' },
  { value: 'amsterdam', label: '阿姆斯特丹', icon: '🌷', region: 'international' },
  { value: 'venice', label: '威尼斯', icon: '🚣', region: 'international' },
  { value: 'florence', label: '佛罗伦萨', icon: '🎨', region: 'international' },
  { value: 'milan', label: '米兰', icon: '👗', region: 'international' },
  { value: 'prague', label: '布拉格', icon: '🏰', region: 'international' },
  { value: 'vienna', label: '维也纳', icon: '🎻', region: 'international' },
  { value: 'berlin', label: '柏林', icon: '🏛️', region: 'international' },
  { value: 'munich', label: '慕尼黑', icon: '🍺', region: 'international' },
  { value: 'zurich', label: '苏黎世', icon: '🏔️', region: 'international' },
  { value: 'geneva', label: '日内瓦', icon: '⌚', region: 'international' },
  { value: 'athens', label: '雅典', icon: '🏛️', region: 'international' },
  { value: 'santorini', label: '圣托里尼', icon: '🏖️', region: 'international' },
  { value: 'istanbul', label: '伊斯坦布尔', icon: '🕌', region: 'international' },
  { value: 'moscow', label: '莫斯科', icon: '🏛️', region: 'international' },
  { value: 'stpetersburg', label: '圣彼得堡', icon: '❄️', region: 'international' },
  { value: 'copenhagen', label: '哥本哈根', icon: '🧜', region: 'international' },
  { value: 'stockholm', label: '斯德哥尔摩', icon: '🏰', region: 'international' },
  { value: 'oslo', label: '奥斯陆', icon: '🏔️', region: 'international' },
  { value: 'helsinki', label: '赫尔辛基', icon: '❄️', region: 'international' },
  { value: 'reykjavik', label: '雷克雅未克', icon: '🌋', region: 'international' },
  { value: 'dublin', label: '都柏林', icon: '🍀', region: 'international' },
  { value: 'edinburgh', label: '爱丁堡', icon: '🏰', region: 'international' },
  { value: 'lisbon', label: '里斯本', icon: '🚃', region: 'international' },
  { value: 'budapest', label: '布达佩斯', icon: '🌉', region: 'international' },
  { value: 'warsaw', label: '华沙', icon: '🏛️', region: 'international' },
  { value: 'krakow', label: '克拉科夫', icon: '🏰', region: 'international' },
  
  // 北美洲
  { value: 'newyork', label: '纽约', icon: '🗽', region: 'international' },
  { value: 'losangeles', label: '洛杉矶', icon: '🎬', region: 'international' },
  { value: 'sanfrancisco', label: '旧金山', icon: '🌉', region: 'international' },
  { value: 'lasvegas', label: '拉斯维加斯', icon: '🎰', region: 'international' },
  { value: 'miami', label: '迈阿密', icon: '🏖️', region: 'international' },
  { value: 'chicago', label: '芝加哥', icon: '🏙️', region: 'international' },
  { value: 'boston', label: '波士顿', icon: '🏛️', region: 'international' },
  { value: 'washington', label: '华盛顿', icon: '🏛️', region: 'international' },
  { value: 'seattle', label: '西雅图', icon: '☕', region: 'international' },
  { value: 'orlando', label: '奥兰多', icon: '🏰', region: 'international' },
  { value: 'hawaii', label: '夏威夷', icon: '🌺', region: 'international' },
  { value: 'vancouver', label: '温哥华', icon: '🍁', region: 'international' },
  { value: 'toronto', label: '多伦多', icon: '🏙️', region: 'international' },
  { value: 'montreal', label: '蒙特利尔', icon: '🏰', region: 'international' },
  { value: 'quebec', label: '魁北克', icon: '🏰', region: 'international' },
  { value: 'mexicocity', label: '墨西哥城', icon: '🏛️', region: 'international' },
  { value: 'cancun', label: '坎昆', icon: '🏖️', region: 'international' },
  
  // 大洋洲
  { value: 'sydney', label: '悉尼', icon: '🏛️', region: 'international' },
  { value: 'melbourne', label: '墨尔本', icon: '🏙️', region: 'international' },
  { value: 'brisbane', label: '布里斯班', icon: '☀️', region: 'international' },
  { value: 'perth', label: '珀斯', icon: '🏖️', region: 'international' },
  { value: 'goldcoast', label: '黄金海岸', icon: '🏄', region: 'international' },
  { value: 'cairns', label: '凯恩斯', icon: '🐠', region: 'international' },
  { value: 'auckland', label: '奥克兰', icon: '🐑', region: 'international' },
  { value: 'queenstown', label: '皇后镇', icon: '🏔️', region: 'international' },
  { value: 'wellington', label: '惠灵顿', icon: '🌊', region: 'international' },
  { value: 'christchurch', label: '基督城', icon: '🏔️', region: 'international' },
  { value: 'fiji', label: '斐济', icon: '🏝️', region: 'international' },
  { value: 'tahiti', label: '大溪地', icon: '🌺', region: 'international' },
  
  // 南美洲
  { value: 'riodejaneiro', label: '里约热内卢', icon: '🏖️', region: 'international' },
  { value: 'buenosaires', label: '布宜诺斯艾利斯', icon: '💃', region: 'international' },
  { value: 'saopaulo', label: '圣保罗', icon: '🏙️', region: 'international' },
  { value: 'lima', label: '利马', icon: '🏛️', region: 'international' },
  { value: 'machupicchu', label: '马丘比丘', icon: '🏔️', region: 'international' },
  { value: 'santiago', label: '圣地亚哥', icon: '🏔️', region: 'international' },
  { value: 'bogota', label: '波哥大', icon: '🏙️', region: 'international' },
  { value: 'cartagena', label: '卡塔赫纳', icon: '🏖️', region: 'international' },
  
  // 非洲
  { value: 'cairo', label: '开罗', icon: '🏜️', region: 'international' },
  { value: 'marrakech', label: '马拉喀什', icon: '🕌', region: 'international' },
  { value: 'capetown', label: '开普敦', icon: '🏔️', region: 'international' },
  { value: 'johannesburg', label: '约翰内斯堡', icon: '🦁', region: 'international' },
  { value: 'nairobi', label: '内罗毕', icon: '🦒', region: 'international' },
  { value: 'zanzibar', label: '桑给巴尔', icon: '🏖️', region: 'international' },
  { value: 'victoria', label: '维多利亚', icon: '🏝️', region: 'international' },
  { value: 'mauritius', label: '毛里求斯', icon: '🏝️', region: 'international' }
])

// 国内城市列表
const domesticLocations = computed(() => {
  return locations.value.filter(l => l.region === 'domestic')
})

// 国际城市列表
const internationalLocations = computed(() => {
  return locations.value.filter(l => l.region === 'international')
})

// 选中地区的label
const selectedLocationLabel = computed(() => {
  const location = locations.value.find(l => l.value === selectedLocation.value)
  return location ? location.label : ''
})

// 景点列表
const attractions = ref([])
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = ref(12)

// 切换分类
const switchCategory = (category) => {
  activeCategory.value = category
  searchKeyword.value = '' // 清空搜索
  // 注意：切换分类时不清空地区，保持地区筛选
  // selectedLocation.value = '' // 不清空地区
  resetAndLoad()
}

// 地区变化
const handleLocationChange = () => {
  searchKeyword.value = '' // 清空搜索
  activeCategory.value = 'all' // 重置分类
  resetAndLoad()
  if (selectedLocation.value) {
    ElMessage.success(`已切换到${selectedLocationLabel.value}，按热度推荐`)
  }
}

// 诊断函数：检查所有应用的样式
const diagnoseSelectStyles = (element: HTMLElement) => {
  const styles = window.getComputedStyle(element)
  const allStyles: Record<string, string> = {}
  
  // 检查所有相关样式属性
  const styleProps = [
    'border-color', 'border-width', 'border-style', 'border',
    'outline', 'outline-color', 'outline-width', 'outline-style',
    'transform', 'scale', 'width', 'min-width', 'max-width',
    'box-shadow', 'background', 'transition'
  ]
  
  styleProps.forEach(prop => {
    allStyles[prop] = styles.getPropertyValue(prop)
  })
  
  // 检查 CSS 变量
  const cssVars = [
    '--el-input-focus-border-color',
    '--el-border-color',
    '--el-color-primary',
    '--el-color-primary-light-3',
    '--el-color-primary-light-5',
    '--el-color-primary-light-7',
    '--el-color-primary-light-8',
    '--el-color-primary-light-9',
    '--el-color-primary-dark-2'
  ]
  
  cssVars.forEach(varName => {
    allStyles[varName] = styles.getPropertyValue(varName)
  })
  
  // 检查内联样式
  allStyles['inline-style'] = element.getAttribute('style') || 'none'
  
  // 检查类名
  allStyles['class-list'] = Array.from(element.classList).join(' ')
  
  // 检查是否有双重边框（检查父元素和子元素）
  const parent = element.parentElement
  const children = Array.from(element.children) as HTMLElement[]
  
  console.group('🔍 城市选择框样式诊断')
  console.log('元素:', element)
  console.log('父元素:', parent)
  console.log('子元素数量:', children.length)
  console.table(allStyles)
  
  // 检查父元素的边框
  if (parent) {
    const parentStyles = window.getComputedStyle(parent)
    const parentBorder = parentStyles.border || parentStyles.getPropertyValue('border')
    const parentOutline = parentStyles.outline || parentStyles.getPropertyValue('outline')
    if (parentBorder && parentBorder !== 'none' && parentBorder !== '0px none rgb(0, 0, 0)') {
      console.warn('⚠️ 父元素有边框:', parentBorder)
    }
    if (parentOutline && parentOutline !== 'none' && parentOutline !== '0px none rgb(0, 0, 0)') {
      console.warn('⚠️ 父元素有 outline:', parentOutline)
    }
  }
  
  // 检查子元素的边框
  children.forEach((child, index) => {
    const childStyles = window.getComputedStyle(child)
    const childBorder = childStyles.border || childStyles.getPropertyValue('border')
    const childOutline = childStyles.outline || childStyles.getPropertyValue('outline')
    if (childBorder && childBorder !== 'none' && childBorder !== '0px none rgb(0, 0, 0)') {
      console.warn(`⚠️ 子元素 ${index} 有边框:`, childBorder, child)
    }
    if (childOutline && childOutline !== 'none' && childOutline !== '0px none rgb(0, 0, 0)') {
      console.warn(`⚠️ 子元素 ${index} 有 outline:`, childOutline, child)
    }
  })
  
  // 检查是否有蓝色边框
  const borderColor = styles.borderColor || styles.getPropertyValue('border-color')
  if (borderColor.includes('rgb') && !borderColor.includes('48, 49, 51') && !borderColor.includes('228, 231, 237')) {
    console.warn('⚠️ 检测到非预期边框颜色:', borderColor)
  }
  
  // 检查是否有透明边框（可能导致双重边框视觉效果）
  if (borderColor.includes('rgba') && borderColor.includes('0, 0, 0, 0')) {
    console.warn('⚠️ 检测到透明边框，可能导致双重边框视觉效果')
  }
  
  // 检查是否有 transform/scale 变化
  const transform = styles.transform
  if (transform && transform !== 'none' && transform !== 'matrix(1, 0, 0, 1, 0, 0)') {
    console.warn('⚠️ 检测到 transform 变化:', transform)
  }
  
  // 检查 outline（双重边框的常见原因）
  const outline = styles.outline || styles.getPropertyValue('outline')
  if (outline && outline !== 'none' && outline !== '0px none rgb(0, 0, 0)') {
    console.warn('⚠️ 检测到 outline（可能导致双重边框）:', outline)
  }
  
  console.groupEnd()
  
  return allStyles
}

// 强制应用黑色边框样式
const applyLocationSelectBlackBorder = () => {
  nextTick(() => {
    if (locationSelectRef.value) {
      const selectEl = (locationSelectRef.value as any).$el || locationSelectRef.value
      if (selectEl) {
        const inputWrapper = selectEl.querySelector?.('.el-input__wrapper') as HTMLElement
        if (inputWrapper) {
          // 诊断：首次检查样式
          console.log('📋 初始化样式诊断')
          diagnoseSelectStyles(inputWrapper)
          
          // 直接应用样式 - 优化：移除收缩效果和蓝色
          const applyStyle = () => {
            // 移除所有蓝色相关的 CSS 变量
            inputWrapper.style.setProperty('--el-input-focus-border-color', '#303133', 'important')
            inputWrapper.style.setProperty('--el-border-color', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary-light-3', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary-light-5', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary-light-7', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary-light-8', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary-light-9', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary-dark-2', '#303133', 'important')
            // 设置边框和样式 - 简化样式，减少变化
            // 确保只有一个边框，移除所有可能的双重边框
            inputWrapper.style.setProperty('border-color', '#909399', 'important')
            inputWrapper.style.setProperty('border', '1px solid #909399', 'important')
            inputWrapper.style.setProperty('border-width', '1px', 'important')
            inputWrapper.style.setProperty('border-style', 'solid', 'important')
            inputWrapper.style.setProperty('box-shadow', '0 1px 4px rgba(0, 0, 0, 0.08)', 'important')
            inputWrapper.style.setProperty('transform', 'none', 'important')
            inputWrapper.style.setProperty('scale', '1', 'important')
            inputWrapper.style.setProperty('transition', 'border-color 0.15s ease', 'important')
            inputWrapper.style.setProperty('background', 'white', 'important')
            // 移除所有可能的双重边框（outline）
            inputWrapper.style.setProperty('outline', 'none', 'important')
            inputWrapper.style.setProperty('outline-color', 'transparent', 'important')
            inputWrapper.style.setProperty('outline-width', '0', 'important')
            inputWrapper.style.setProperty('outline-style', 'none', 'important')
            inputWrapper.style.setProperty('background', 'white', 'important')
            // 检查是否有 box-shadow 造成的视觉边框
            const computedStyle = window.getComputedStyle(inputWrapper)
            const boxShadow = computedStyle.boxShadow
            if (boxShadow && boxShadow !== 'none' && !boxShadow.includes('rgba(48, 49, 51')) {
              // 如果 box-shadow 不是我们设置的，可能需要调整
              console.log('检测到 box-shadow:', boxShadow)
            }
            // 防止宽度变化
            const currentWidth = inputWrapper.offsetWidth || 180
            inputWrapper.style.setProperty('width', `${currentWidth}px`, 'important')
            inputWrapper.style.setProperty('min-width', `${currentWidth}px`, 'important')
            inputWrapper.style.setProperty('max-width', `${currentWidth}px`, 'important')
          }
          
          // 立即应用一次
          applyStyle()
          
          // 使用MutationObserver监听类变化 - 更频繁地检查
          const observer = new MutationObserver((mutations) => {
            applyStyle() // 任何变化都重新应用
            
            // 如果检测到 is-focus 类变化，进行诊断
            mutations.forEach(mutation => {
              if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
                const target = mutation.target as HTMLElement
                if (target.classList.contains('is-focus')) {
                  console.log('🔍 Focus 状态变化，诊断样式:')
                  diagnoseSelectStyles(target)
                }
              }
            })
          })
          
          observer.observe(inputWrapper, {
            attributes: true,
            attributeFilter: ['class', 'style']
          })
          
          // 监听所有可能的事件
          const eventHandler = (event: Event) => {
            applyStyle()
            // 诊断点击/聚焦时的样式
            setTimeout(() => {
              console.log(`🔍 ${event.type} 事件后样式诊断:`)
              diagnoseSelectStyles(inputWrapper)
            }, 10)
          }
          
          inputWrapper.addEventListener('focus', eventHandler, true)
          inputWrapper.addEventListener('focusin', eventHandler, true)
          inputWrapper.addEventListener('click', eventHandler, true)
          inputWrapper.addEventListener('mousedown', eventHandler, true)
          
          // 使用 requestAnimationFrame 持续检查（仅在 focus 时）
          let rafId: number | null = null
          const checkAndApply = () => {
            if (inputWrapper.classList.contains('is-focus')) {
              applyStyle()
              rafId = requestAnimationFrame(checkAndApply)
            } else {
              if (rafId) {
                cancelAnimationFrame(rafId)
                rafId = null
              }
            }
          }
          
          // 监听 focus 状态变化
          const focusObserver = new MutationObserver(() => {
            if (inputWrapper.classList.contains('is-focus')) {
              checkAndApply()
            } else {
              if (rafId) {
                cancelAnimationFrame(rafId)
                rafId = null
              }
            }
          })
          
          focusObserver.observe(inputWrapper, {
            attributes: true,
            attributeFilter: ['class']
          })
        }
      }
    }
  })
}

// 选择框显示/隐藏变化
const handleLocationSelectVisibleChange = (visible: boolean) => {
  if (visible) {
    applyLocationSelectBlackBorder()
  }
}

// 自动搜索（带防抖）
let searchTimer = null
const handleAutoSearch = () => {
  // 清除之前的定时器
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  // 设置新的定时器，500ms后执行搜索
  searchTimer = setTimeout(() => {
    activeCategory.value = 'all' // 重置分类
    selectedLocation.value = '' // 清空地区
    resetAndLoad()
  }, 500) // 500ms防抖延迟
}

// 自定义搜索（点击按钮或按回车）
const handleCustomSearch = () => {
  // 清除防抖定时器
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  activeCategory.value = 'all' // 重置分类
  selectedLocation.value = '' // 清空地区
  resetAndLoad()
}


// 重置并加载
const resetAndLoad = () => {
  page.value = 1
  attractions.value = []
  noMore.value = false
  loadAttractions()
}

// 类型映射（后端type数字 -> 前端type字符串，与管理平台创建景点的类型对应）
const typeMap = {
  1: 'nature',      // 自然风光
  2: 'culture',     // 人文历史
  3: 'theme',       // 主题公园
  4: 'food',        // 美食街
  5: 'ancient',     // 古镇古村
  6: 'spa',         // 温泉度假
  7: 'religious',   // 宗教场所
  8: 'shopping'     // 购物娱乐
}

// 加载景点数据
const loadAttractions = async () => {
  loading.value = true
  
  try {
    // 构建请求参数
    const params = {
      page: page.value,
      size: pageSize.value
    }
    
    // 如果有搜索关键词，添加到参数
    if (searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim()
    }
    
    // 如果有选中地区，添加到参数（需要将地区转换为城市）
    if (selectedLocation.value) {
      const location = locations.value.find(l => l.value === selectedLocation.value)
      if (location) {
        // 保留原始城市名称，同时支持带"市"和不带"市"的匹配
        let cityName = location.label.replace(/市|省|自治区|特别行政区/g, '') // 移除后缀
        params.city = cityName
        // 如果没有搜索关键词，将城市名称作为关键词，用于记录搜索日志
        if (!params.keyword) {
          params.keyword = cityName
        }
        console.log('选择地点:', location.label, '转换为城市参数:', params.city)
        console.log('将尝试匹配:', cityName, '或', cityName + '市', '或', cityName + '省')
      }
    }
    
    // 如果是"推荐"分类，调用用户画像推荐API
    if (activeCategory.value === 'recommend') {
      // 获取用户ID
      const userId = getCurrentUserId()
      if (!userId) {
        ElMessage.warning('请先登录以获取个性化推荐')
        // 未登录时降级为显示所有景点
        activeCategory.value = 'all'
        resetAndLoad()
        return
      }
      
      // 构建推荐API参数
      const recommendParams = {
        limit: pageSize.value * page.value  // 获取足够的数据用于分页
      }
      
      // 如果有选中地区，添加到推荐参数
      if (selectedLocation.value) {
        const location = locations.value.find(l => l.value === selectedLocation.value)
        if (location) {
          recommendParams.city = location.label.replace(/市|省|自治区|特别行政区/g, '')
        }
      }
      
      console.log('调用推荐API，参数:', recommendParams)
      const recommendResponse = await request.get('/recommendation/attractions', { params: recommendParams })
      console.log('推荐API响应:', recommendResponse)
      
      if (recommendResponse.code === 200 && recommendResponse.data) {
        const recommendList = recommendResponse.data || []
        console.log('推荐景点列表:', recommendList, '数量:', recommendList.length)
        
        // 先按推荐分数排序（score高的在前）
        const sortedList = recommendList.sort((a, b) => {
          const scoreA = a.score || 0
          const scoreB = b.score || 0
          return scoreB - scoreA // 降序排列
        })
        console.log('排序后的推荐列表:', sortedList.map(item => ({
          name: item.name,
          type: item.type,
          score: item.score,
          reason: item.reason
        })))
        
        // 转换推荐数据格式
        const formattedList = sortedList.map(item => {
          // 处理图片
          let imageUrl = item.coverImage || item.image || '/default-attraction.jpg'
          if (!imageUrl && item.images) {
            if (typeof item.images === 'string') {
              const imageArray = item.images.split(',').filter(Boolean)
              if (imageArray.length > 0) {
                imageUrl = imageArray[0].trim()
              }
            } else if (Array.isArray(item.images) && item.images.length > 0) {
              imageUrl = item.images[0]
            }
          }
          
          // 处理标签
          let tags = item.tags || []
          if (typeof tags === 'string') {
            tags = tags.split(',').filter(Boolean).map(t => t.trim())
          }
          
          // 处理类型名称
          const typeNameMap = {
            1: '自然风光',
            2: '人文历史',
            3: '主题公园',
            4: '美食街',
            5: '古镇古村',
            6: '温泉度假',
            7: '宗教场所',
            8: '购物娱乐'
          }
          
          // 处理类型映射（推荐API返回的type可能是数字）
          let attractionType = 'culture'
          if (item.type !== null && item.type !== undefined) {
            attractionType = typeMap[item.type] || 'culture'
          }
          
          // 处理景区级别：优先使用rating字段，确保是1-5的整数
          let rating = 0
          if (item.rating !== null && item.rating !== undefined) {
            // 如果rating是数字，确保是1-5的整数
            const ratingNum = typeof item.rating === 'number' ? item.rating : Number(item.rating)
            if (!isNaN(ratingNum) && ratingNum >= 1 && ratingNum <= 5) {
              rating = Math.round(ratingNum) // 四舍五入确保是整数
            }
          }
          // 注意：不使用score作为rating，因为score是推荐分数（可能是小数），不是景区级别
          
          return {
            id: item.id,
            name: item.name || '',
            location: item.location || `${item.city || ''}${item.province ? (item.city ? ' · ' : '') + item.province : ''}`.trim() || '未知',
            city: item.city || '',
            description: item.description || '',
            image: imageUrl,
            type: attractionType,
            typeName: typeNameMap[item.type] || '其他',
            tags: tags,
            price: item.ticketPrice || item.price || 0,
            views: item.viewCount || item.views || 0,
            favorites: item.collectCount || item.favorites || 0,
            isFavorite: item.isFavorite || false,
            rating: rating, // 景区级别（1-5的整数）
            score: item.score || 0 // 推荐分数（可能是小数）
          }
        })
        
        // 分页处理
        const startIndex = (page.value - 1) * pageSize.value
        const endIndex = startIndex + pageSize.value
        const pageData = formattedList.slice(startIndex, endIndex)
        
        if (page.value === 1) {
          attractions.value = pageData
        } else {
          attractions.value.push(...pageData)
        }
        
        // 检查是否还有更多数据
        if (formattedList.length <= endIndex) {
          noMore.value = true
        } else {
          page.value++
        }
        
        loading.value = false
        return
      } else {
        // 推荐API失败或返回空，显示空状态（不降级）
        console.warn('推荐API返回空结果')
        attractions.value = []
        noMore.value = true
        loading.value = false
        return
      }
    }
    
    // 如果选择了分类，转换为后端type（排除'recommend'和'all'）
    if (activeCategory.value !== 'all' && activeCategory.value !== 'recommend') {
      // 直接使用分类值作为type（已统一为数字字符串）
      const typeValue = parseInt(activeCategory.value)
      if (!isNaN(typeValue) && typeValue >= 1 && typeValue <= 8) {
        params.type = typeValue
      }
    }
    
    // 调用API获取景点列表
    console.log('请求参数:', params)
    const response = await request.get('/user/attraction/list', { params })
    console.log('API响应:', response)
    
    if (response.code === 200 && response.data) {
      const { list, total } = response.data
      console.log('景点列表数据:', list, '总数:', total)
      console.log('响应数据结构:', JSON.stringify(response.data, null, 2))
      
      if (list && list.length > 0) {
        // 转换数据格式以匹配前端显示
        const formattedList = list.map(item => {
          // 处理图片：优先使用coverImage，其次images第一张，最后默认图片
          let imageUrl = item.coverImage || item.image || '/default-attraction.jpg'
          if (!imageUrl && item.images) {
            if (typeof item.images === 'string') {
              const imageArray = item.images.split(',').filter(Boolean)
              if (imageArray.length > 0) {
                imageUrl = imageArray[0].trim()
              }
            } else if (Array.isArray(item.images) && item.images.length > 0) {
              imageUrl = item.images[0]
            }
          }
          
          // 处理标签：如果是字符串则转换为数组
          let tags = item.tags || []
          if (typeof tags === 'string') {
            tags = tags.split(',').filter(Boolean).map(t => t.trim())
          }
          
          // 处理类型名称（与管理平台创建景点的类型对应）
          const typeNameMap = {
            1: '自然风光',
            2: '人文历史',
            3: '主题公园',
            4: '美食街',
            5: '古镇古村',
            6: '温泉度假',
            7: '宗教场所',
            8: '购物娱乐'
          }
          
          // 类型到筛选分类的映射（用于显示类型标签）
          const typeToCategoryMap = {
            1: '自然风光',
            2: '人文历史',
            3: '主题公园',
            4: '美食街',
            5: '古镇古村',
            6: '温泉度假',
            7: '宗教场所',
            8: '购物娱乐'
          }
          
          return {
            id: item.id,
            name: item.name || '',
            location: item.location || `${item.city || ''}${item.province ? (item.city ? ' · ' : '') + item.province : ''}`.trim() || '未知',
            city: item.city || '',
            description: item.description || '',
            image: imageUrl,
            type: typeMap[item.type] || 'culture',  // 用于卡片样式分类
            typeName: typeNameMap[item.type] || '其他',
            categoryType: typeToCategoryMap[item.type] || '其他',  // 用于筛选的分类类型
            tags: tags,
            price: item.ticketPrice || item.price || 0,
            views: item.viewCount || item.views || 0,
            favorites: item.collectCount || item.favorites || 0,
            isFavorite: item.isFavorite || false,
            // 处理景区级别：确保是1-5的整数
            rating: (() => {
              if (item.rating !== null && item.rating !== undefined) {
                const ratingNum = typeof item.rating === 'number' ? item.rating : Number(item.rating)
                if (!isNaN(ratingNum) && ratingNum >= 1 && ratingNum <= 5) {
                  return Math.round(ratingNum) // 四舍五入确保是整数
                }
              }
              return 0 // 如果没有有效的rating，返回0（不显示）
            })(), // 景区级别（1-5的整数）
            score: item.score || 0, // 评分
            commentCount: item.commentCount || item.comments || 0, // 评论数
            avgStayTime: item.avgStayTime || 0, // 平均停留时间（秒），如果没有则默认为0
            openTime: item.openTime || item.openingHours || '', // 开放时间
            bestSeason: item.bestSeason || '' // 最佳季节
          }
        })
        
        // 如果是"全部景点"，按热度排序（从高到低）
        if (activeCategory.value === 'all') {
          formattedList.sort((a, b) => {
            // 热度计算公式：浏览量 * 0.4 + 收藏量 * 0.4 + 平均停留时间（秒）* 0.2
            const viewsA = a.views || 0
            const viewsB = b.views || 0
            const favoritesA = a.favorites || 0
            const favoritesB = b.favorites || 0
            const stayTimeA = a.avgStayTime || 0 // 平均停留时间（秒），如果没有则默认为0
            const stayTimeB = b.avgStayTime || 0
            
            // 计算热度分数
            // 浏览量权重0.4，收藏量权重0.4，停留时间权重0.2
            // 停留时间以秒为单位，除以60转换为分钟，再除以10进行归一化（假设平均停留时间在0-100分钟之间）
            const heatScoreA = viewsA * 0.4 + favoritesA * 0.4 + (stayTimeA / 60 / 10) * 0.2
            const heatScoreB = viewsB * 0.4 + favoritesB * 0.4 + (stayTimeB / 60 / 10) * 0.2
            
            // 降序排列（热度高的在前）
            return heatScoreB - heatScoreA
          })
        }
        
        attractions.value.push(...formattedList)
        page.value++
        
        // 检查是否还有更多数据
        if (attractions.value.length >= total || formattedList.length < pageSize.value) {
          noMore.value = true
        }
      } else {
        // 即使没有数据，也要标记为没有更多数据
        noMore.value = true
        if (selectedLocation.value && page.value === 1) {
          console.warn('该地点没有找到景点，请检查城市名称匹配')
        }
      }
    } else {
      ElMessage.error(response.message || '加载景点失败')
      noMore.value = true
    }
  } catch (error) {
    console.error('加载景点失败:', error)
    ElMessage.error('加载景点失败，请稍后重试')
    noMore.value = true
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => {
  if (!loading.value && !noMore.value) {
    loadAttractions()
  }
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

// 切换收藏
const toggleFavorite = async (attraction) => {
  try {
    const response = await request.post(`/user/attraction/collect/${attraction.id}`)
    
    if (response.code === 200) {
      // 使用后端返回的新状态，而不是简单地反转当前状态
      // 后端返回的 data 是操作后的新状态（true=已收藏，false=未收藏）
      const newFavoriteStatus = response.data !== undefined ? response.data : !attraction.isFavorite
      
      // 更新收藏状态
      attraction.isFavorite = newFavoriteStatus
      
      // 更新收藏数量
      if (newFavoriteStatus) {
        attraction.favorites++
        ElMessage.success('已添加到收藏')
      } else {
        if (attraction.favorites > 0) {
          attraction.favorites--
        }
        ElMessage.info('已取消收藏')
      }
      
      console.log('收藏状态更新:', {
        id: attraction.id,
        name: attraction.name,
        isFavorite: attraction.isFavorite,
        favorites: attraction.favorites
      })
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 查看详情
const viewDetail = (attraction) => {
  router.push(`/home/user/attractions/detail/${attraction.id}`)
}

onMounted(() => {
  loadAttractions()
  // 应用黑色边框样式
  applyLocationSelectBlackBorder()
  
  // 延迟再次应用，确保DOM已渲染
  setTimeout(() => {
    applyLocationSelectBlackBorder()
  }, 100)
})
</script>

<style lang="scss" scoped>
.recommendations-page {
  min-height: 100vh;
  background: #f5f7fa;
  position: relative;
  padding-bottom: 40px;
  overflow: hidden;
  
  .page-header {
    margin-bottom: 24px;
    padding: 32px;
    background: white;
    border-radius: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
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
        background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #606266;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        border: 1px solid #e4e7ed;
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
      border-radius: 30px;
      color: #606266;
      font-size: 14px;
      border: 1px solid #e4e7ed;
      
      .el-icon {
        font-size: 16px;
        color: #909399;
      }
    }
  }
}

// 顶部搜索栏
.search-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px) saturate(180%);
  padding: 20px 20px 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  border-bottom: 1px solid rgba(255, 255, 255, 0.18);
  animation: slideDown 0.5s ease-out;
  
  .search-container {
    max-width: 1200px;
    margin: 0 auto;
    
    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: 8px;
        padding: 0 16px;
        background: white;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        border: 1px solid #e4e7ed;
        min-height: 44px;
        transition: none;
        
        .el-input__inner {
          height: 44px;
          line-height: 44px;
          font-size: 15px;
        }
        
        // 移除所有 hover 和 focus 效果
        &:hover,
        &:focus,
        &:focus-visible,
        &.is-focus,
        &.is-focus:hover,
        &:hover.is-focus {
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
          border-color: #e4e7ed;
          border: 1px solid #e4e7ed;
          outline: none;
          background: white;
        }
      }
      
      .el-input__inner {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
        
        &:focus {
          outline: none;
        }
      }
      
      .el-input__prefix {
        font-size: 20px;
        color: #909399;
      }
      
      .el-button {
        background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
        border: 1px solid #dcdfe6;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        color: #606266;
        outline: none;
        
        &:hover {
          background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
          border-color: #dcdfe6;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
          outline: none;
        }
        
        &:focus {
          outline: none;
        }
        
        &:active {
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
          outline: none;
        }
      }
    }
  }
  
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 筛选区域
.filters-section {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(31, 38, 135, 0.08);
  animation: slideDown 0.6s ease-out 0.1s both;
  
  .category-tabs {
    flex: 1;
    display: flex;
    gap: 12px;
    overflow-x: auto;
    white-space: nowrap;
    padding: 4px 0;
    
    &::-webkit-scrollbar {
      height: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: rgba(0, 0, 0, 0.05);
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #dcdfe6;
      border-radius: 3px;
      
      &:hover {
        background: #c0c4cc;
      }
    }
    
    .category-tab {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      background: white;
      border-radius: 4px;
      cursor: pointer;
      flex-shrink: 0;
      border: 1px solid #e4e7ed;
      
      .category-icon {
        font-size: 16px;
      }
      
      .category-name {
        font-size: 14px;
        font-weight: 400;
        color: #606266;
      }
      
      &:hover {
        border-color: #c0c4cc;
        background: #f5f7fa;
      }
      
      &.active {
        background: #f5f5f5;
        border-color: #909399;
        
        .category-name {
          color: #303133;
          font-weight: 600;
        }
      }
    }
  }
  
  .location-filter {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    gap: 8px;
    
    .location-label {
      font-size: 14px;
      color: #606266;
      font-weight: 500;
      white-space: nowrap;
    }
    
    :deep(.el-select) {
      width: 180px;
      
      .el-input__wrapper {
        border-radius: 25px;
        background: white !important;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08) !important;
        // 默认状态：统一的浅灰色边框
        border: 1px solid #e4e7ed !important;
        // 简化过渡，只保留边框颜色变化
        transition: border-color 0.15s ease !important;
        transform: none !important;
        width: 180px !important;
        min-width: 180px !important;
        max-width: 180px !important;
        // 确保没有双重边框
        outline: none !important;
        outline-color: transparent !important;
        outline-width: 0 !important;
        outline-style: none !important;
        
        // Hover 状态：保持完全相同的样式，不变化
        &:hover:not(.is-focus) {
          background: white !important;
          border: 1px solid #e4e7ed !important;
          border-color: #e4e7ed !important;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08) !important;
          transform: none !important;
          outline: none !important;
        }
        
        // Focus 状态：只改变边框颜色，其他保持不变
        &.is-focus,
        &:focus,
        &:focus-visible,
        &:focus-within {
          --el-input-focus-border-color: #909399 !important;
          --el-border-color: #909399 !important;
          --el-color-primary: #909399 !important;
          background: white !important;
          border-color: #909399 !important;
          border: 1px solid #909399 !important;
          border-width: 1px !important;
          transform: none !important;
          scale: 1 !important;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08) !important;
          outline: none !important;
          outline-color: transparent !important;
          outline-width: 0 !important;
          width: 180px !important;
          min-width: 180px !important;
          max-width: 180px !important;
        }
        
        // Hover + Focus 组合：保持与 focus 相同的样式
        &:hover.is-focus,
        &.is-focus:hover,
        &.is-focus:focus,
        &.is-focus:focus-visible {
          --el-input-focus-border-color: #909399 !important;
          --el-border-color: #909399 !important;
          background: white !important;
          border-color: #909399 !important;
          border: 1px solid #909399 !important;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08) !important;
          transform: none !important;
          scale: 1 !important;
          outline: none !important;
          width: 180px !important;
          min-width: 180px !important;
          max-width: 180px !important;
        }
      }
      
      &.is-focus .el-input__wrapper,
      &.is-focus .el-input .el-input__wrapper,
      .el-input.is-focus .el-input__wrapper,
      &:focus .el-input__wrapper,
      &:focus-within .el-input__wrapper {
        --el-input-focus-border-color: #303133 !important;
        --el-border-color: #303133 !important;
        --el-color-primary: #303133 !important;
        --el-color-primary-light-3: #303133 !important;
        --el-color-primary-light-5: #303133 !important;
        --el-color-primary-light-7: #303133 !important;
        --el-color-primary-light-8: #303133 !important;
        --el-color-primary-light-9: #303133 !important;
        --el-color-primary-dark-2: #303133 !important;
        background: white !important;
        border-color: #303133 !important;
        border: 2px solid #303133 !important;
        border-width: 2px !important;
        transform: none !important;
        scale: 1 !important;
        box-shadow: 0 2px 10px rgba(48, 49, 51, 0.15) !important;
        outline: none !important;
        outline-color: transparent !important;
        width: 180px !important;
        min-width: 180px !important;
        max-width: 180px !important;
      }
      
      .el-input__inner {
        font-weight: 600;
        color: #606266;
        
        &::placeholder {
          color: #909399 !important;
          opacity: 1 !important;
          font-weight: 500;
        }
      }
      
      // 确保 placeholder 在未选择时显示 - 使用 :deep() 穿透
      :deep(.el-select__placeholder) {
        color: #909399 !important;
        opacity: 1 !important;
        font-weight: 500 !important;
        display: block !important;
      }
      
      // 当没有选中值时，确保 placeholder 可见
      &:not(.is-focus) {
        :deep(.el-select__placeholder) {
          color: #909399 !important;
          opacity: 1 !important;
        }
      }
      
      // 确保输入框为空时显示 placeholder
      .el-input__wrapper:not(.has-value) {
        :deep(.el-select__placeholder) {
          color: #909399 !important;
          opacity: 1 !important;
        }
      }
      
      .el-input__prefix {
        font-size: 18px;
        color: #667eea;
      }
    }
  }
}

// 地区选择器下拉面板样式
:deep(.el-select-dropdown) {
  .el-select-group__title {
    font-weight: 700;
    font-size: 13px;
    color: #667eea;
    padding: 10px 12px 6px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  }
  
  .el-select-dropdown__item {
    padding: 8px 16px;
    
    &:hover {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    }
    
    &.selected {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
      color: #667eea;
      font-weight: 600;
    }
  }
}

.location-icon {
  margin-right: 8px;
  font-size: 16px;
}

// 内容提示
.content-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 0;
  margin: 20px 20px 16px;
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  
  .tip-icon {
    font-size: 16px;
    color: #909399;
  }
  
  &.search-tip {
    color: #909399;
  }
  
  &.location-tip {
    color: #909399;
  }
  
  &.recommend-tip {
    color: #909399;
  }
  
  &.all-tip {
    color: #909399;
  }
}

@keyframes tipFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

// 景点卡片网格布局
.attractions-waterfall {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 0 20px;
  margin-top: 20px;
  
  .attraction-card {
    position: relative;
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
    border: 1px solid #f0f0f0;
    
    &:hover {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      border-color: #dcdfe6;
    }
    
    // 封面图
    .card-cover {
      position: relative;
      width: 100%;
      height: 200px;
      overflow: hidden;
      background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .card-gradient {
        display: none;
      }
      
      .type-badge {
        position: absolute;
        top: 10px;
        left: 10px;
        padding: 4px 10px;
        border-radius: 2px;
        font-size: 11px;
        font-weight: 500;
        color: white;
        text-align: center;
        line-height: 1.4;
        white-space: nowrap;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
        
        &.badge-nature {
          background: #13c2c2;
        }
        
        &.badge-culture {
          background: #2f54eb;
        }
        
        &.badge-theme {
          background: #eb2f96;
        }
        
        &.badge-food {
          background: #fa8c16;
        }
        
        &.badge-ancient {
          background: #722ed1;
        }
        
        &.badge-spa {
          background: #f5222d;
        }
        
        &.badge-religious {
          background: #595959;
        }
        
        &.badge-shopping {
          background: #fa541c;
        }
        
        // 兼容旧类型
        &.badge-city {
          background: #2f54eb;
        }
        
        &.badge-relax {
          background: #f5222d;
        }
      }
    }
    
    // 信息层
    .card-info {
      padding: 14px;
      
      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 8px 0;
        line-height: 1.4;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
      }
      
      .card-location {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #909399;
        font-size: 12px;
        margin-bottom: 8px;
        
        .el-icon {
          font-size: 13px;
        }
      }
      
      .card-desc {
        font-size: 13px;
        color: #606266;
        line-height: 1.5;
        margin: 0 0 10px 0;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .card-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 14px;
        padding-bottom: 14px;
        border-bottom: 2px solid #f5f5f5;
        
        .meta-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          flex: 1;
          
          .meta-tag {
            position: relative;
            font-size: 12px;
            color: #667eea;
            font-weight: 600;
            white-space: nowrap;
            padding: 2px 0;
            transition: all 0.3s;
            
            &::after {
              content: '';
              position: absolute;
              bottom: 0;
              left: 0;
              width: 0;
              height: 2px;
              background: linear-gradient(90deg, #667eea, #764ba2);
              transition: width 0.3s;
            }
            
            &:hover {
              color: #5568d3;
              
              &::after {
                width: 100%;
              }
            }
          }
        }
        
        .card-actions {
          display: flex;
          gap: 12px;
          align-items: center;
          
          .action-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 13px;
            color: #909399;
            cursor: pointer;
            transition: all 0.2s ease;
            padding: 4px 8px;
            border-radius: 8px;
            
            .el-icon {
              font-size: 16px;
              transition: all 0.2s ease;
            }
            
            &:hover {
              color: #667eea;
              background: rgba(102, 126, 234, 0.1);
              
              .el-icon {
                transform: scale(1.1);
              }
            }
            
            &.favorite-action {
              &.is-favorite {
                color: #f56c6c;
                background: rgba(245, 108, 108, 0.1);
              }
              
              &:hover {
                color: #f56c6c;
                background: rgba(245, 108, 108, 0.15);
                
                .el-icon {
                  transform: scale(1.15);
                }
              }
            }
          }
        }
      }
      
      .card-price-section {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 14px;
        padding-top: 14px;
        border-top: 1px solid #f0f0f0;
        
        .price-info {
          display: flex;
          flex-direction: column;
          gap: 2px;
          
          .price-label {
            font-size: 12px;
            color: #606266;
            font-weight: 600;
            letter-spacing: 0.5px;
          }
          
          .price-value {
            font-family: 'KaiTi', 'STKaiti', 'Kaiti SC', 'Songti SC', serif;
            font-size: 24px;
            font-weight: 700;
            color: #ff4d4f;
            letter-spacing: -0.8px;
            line-height: 1.2;
          }
        }
        
      }
    }
  }
}

@keyframes heartBeat {
  0%, 100% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.3);
  }
  50% {
    transform: scale(1.1);
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 60px 40px;
  
  .el-icon {
    font-size: 48px;
    color: white;
    filter: drop-shadow(0 4px 15px rgba(102, 126, 234, 0.6));
    animation: loadingSpin 1s linear infinite;
  }
  
  span {
    color: white;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 2px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  }
}

@keyframes loadingSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
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
  padding: 80px 20px;
  
  .el-empty__image {
    width: 200px;
    height: 200px;
    
    svg {
      fill: rgba(255, 255, 255, 0.6);
      filter: drop-shadow(0 4px 20px rgba(0, 0, 0, 0.2));
    }
  }
  
  .el-empty__description {
    margin-top: 20px;
    
    p {
      color: white;
      font-size: 18px;
      font-weight: 600;
      letter-spacing: 1px;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }
  }
}

// 响应式设计
@media (max-width: 1400px) {
  .attractions-waterfall {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .attractions-waterfall {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .recommendations-page {
    padding-bottom: 20px;
  }
  
  .search-bar {
    padding: 12px 15px 10px;
  }
  
  .filters-section {
    flex-direction: column;
    align-items: stretch;
    padding: 10px 15px;
    gap: 10px;
    
    .category-tabs {
      overflow-x: auto;
    }
    
    .location-filter {
      :deep(.el-select) {
        width: 100%;
      }
    }
  }
  
  .content-tip {
    margin: 12px 15px 10px;
  }
  
  .attractions-waterfall {
    grid-template-columns: 1fr;
    padding: 0 15px;
    gap: 12px;
    
    .attraction-card {
      
      .card-info {
        padding: 14px;
        
        .card-meta {
          flex-direction: column;
          align-items: flex-start;
          gap: 10px;
          
          .card-actions {
            width: 100%;
            justify-content: flex-end;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 992px) {
  .recommendations-page {
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
  .recommendations-page {
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

<style lang="scss">
// 选择目的地输入框 focus 样式 - 只改颜色，不再放大或产生强烈阴影
.recommendations-page .location-filter .el-select.location-select-black-focus,
.recommendations-page .location-filter .location-select-black-focus {
  .el-select__wrapper,
  .el-input__wrapper,
  .el-input .el-input__wrapper {
    // 禁止任何缩放或大小动画，避免点击时"抖动"
    transition: border-color 0.2s ease, background 0.2s ease !important;
    transform: none !important;

    &:focus,
    &:focus-visible,
    &.is-focus,
    &.is-focus:hover {
      --el-input-focus-border-color: #303133 !important;
      --el-border-color: #303133 !important;
      --el-color-primary: #303133 !important;
      border-width: 2px !important; // 保持与未聚焦时一致，避免"放大抖动"
      border-style: solid !important;
      border-color: #303133 !important;
      box-shadow: 0 2px 10px rgba(48, 49, 51, 0.15) !important;  // 轻微阴影，不变化
      transform: none !important;
      outline: none !important;
    }
  }
  
  // 当 el-select 获得焦点时
  &.is-focus,
  &:focus,
  &:focus-within {
    .el-select__wrapper,
    .el-input__wrapper,
    .el-input .el-input__wrapper {
      --el-input-focus-border-color: #303133 !important;
      --el-border-color: #303133 !important;
      --el-color-primary: #303133 !important;
      border-width: 2px !important;
      border-style: solid !important;
      border-color: #303133 !important;
      box-shadow: 0 2px 10px rgba(48, 49, 51, 0.15) !important;
      transform: none !important;
      outline: none !important;
    }
  }
  
  // 所有可能的选择器组合（最高优先级）
  .el-input__wrapper.is-focus,
  .el-input.is-focus .el-input__wrapper,
  .el-input .el-input__wrapper.is-focus,
  &.is-focus .el-input__wrapper,
  &.is-focus .el-input .el-input__wrapper,
  .el-input__wrapper.is-focus:hover,
  &.is-focus:hover .el-input__wrapper {
    --el-input-focus-border-color: #303133 !important;
    --el-border-color: #303133 !important;
    --el-color-primary: #303133 !important;
    border-width: 2px !important;
    border-style: solid !important;
    border-color: #303133 !important;
    box-shadow: 0 2px 10px rgba(48, 49, 51, 0.15) !important;
    transform: none !important;
    outline: none !important;
  }
}

// 使用属性选择器确保最高优先级，移除所有蓝色
.recommendations-page .location-filter .el-select.location-select-black-focus .el-input__wrapper[class*="is-focus"],
.recommendations-page .location-filter .location-select-black-focus .el-input__wrapper[class*="is-focus"],
.recommendations-page .location-filter .el-select.location-select-black-focus .el-input__wrapper,
.recommendations-page .location-filter .location-select-black-focus .el-input__wrapper {
  --el-input-focus-border-color: #303133 !important;
  --el-border-color: #303133 !important;
  --el-color-primary: #303133 !important;
  --el-color-primary-light-3: #303133 !important;
  --el-color-primary-light-5: #303133 !important;
  --el-color-primary-light-7: #303133 !important;
  --el-color-primary-light-8: #303133 !important;
  --el-color-primary-light-9: #303133 !important;
  --el-color-primary-dark-2: #303133 !important;
  border-width: 2px !important;
  border-style: solid !important;
  border-color: #303133 !important;
  box-shadow: 0 2px 10px rgba(48, 49, 51, 0.15) !important;
  transform: none !important;
  outline: none !important;
  transition: border-color 0.2s ease, background 0.2s ease !important;
}

// 确保城市选择框的 placeholder 显示
.recommendations-page .location-filter .el-select.location-select-black-focus {
  :deep(.el-select__placeholder) {
    color: #909399 !important;
    opacity: 1 !important;
    font-weight: 500 !important;
    visibility: visible !important;
    display: inline-block !important;
  }
  
  // 当没有选中值时
  &:not(.has-value) {
    :deep(.el-select__placeholder) {
      color: #909399 !important;
      opacity: 1 !important;
    }
  }
  
  // 确保输入框为空时显示
  .el-input__wrapper:not(:has(.el-select__selected-item)) {
    :deep(.el-select__placeholder) {
      color: #909399 !important;
      opacity: 1 !important;
    }
  }
}

// 最高优先级：强制覆盖所有可能的蓝色和收缩效果
body .recommendations-page .location-filter .el-select.location-select-black-focus .el-input__wrapper,
body .recommendations-page .location-filter .location-select-black-focus .el-input__wrapper,
.recommendations-page .location-filter .el-select.location-select-black-focus .el-input__wrapper.is-focus,
.recommendations-page .location-filter .location-select-black-focus .el-input__wrapper.is-focus,
// 覆盖 Element Plus 默认样式
.el-select.location-select-black-focus .el-input__wrapper,
.el-select.location-select-black-focus .el-input.is-focus .el-input__wrapper,
.el-select.location-select-black-focus .el-input__wrapper.is-focus,
// 覆盖所有可能的组合
.recommendations-page .location-filter .el-select.location-select-black-focus .el-input .el-input__wrapper,
.recommendations-page .location-filter .el-select.location-select-black-focus .el-input.is-focus .el-input__wrapper,
.recommendations-page .location-filter .el-select.location-select-black-focus .el-input__wrapper.is-focus:hover,
.recommendations-page .location-filter .el-select.location-select-black-focus.is-focus .el-input__wrapper,
.recommendations-page .location-filter .el-select.location-select-black-focus:focus .el-input__wrapper,
.recommendations-page .location-filter .el-select.location-select-black-focus:focus-within .el-input__wrapper {
  --el-input-focus-border-color: #303133 !important;
  --el-border-color: #303133 !important;
  --el-color-primary: #303133 !important;
  --el-color-primary-light-3: #303133 !important;
  --el-color-primary-light-5: #303133 !important;
  --el-color-primary-light-7: #303133 !important;
  --el-color-primary-light-8: #303133 !important;
  --el-color-primary-light-9: #303133 !important;
  --el-color-primary-dark-2: #303133 !important;
  border: 2px solid #303133 !important;
  border-color: #303133 !important;
  border-width: 2px !important;
  border-style: solid !important;
  box-shadow: 0 2px 10px rgba(48, 49, 51, 0.15) !important;
  transform: none !important;
  scale: 1 !important;
  transition: border-color 0.2s ease, background 0.2s ease !important;
  outline: none !important;
  outline-color: transparent !important;
  outline-width: 0 !important;
  outline-style: none !important;
  width: 180px !important;
  min-width: 180px !important;
  max-width: 180px !important;
}
</style>

