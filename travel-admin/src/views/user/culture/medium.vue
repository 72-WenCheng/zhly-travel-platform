<template>
  <div class="culture-platform-medium">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 顶部英雄区 -->
    <section id="banner" class="hero-shell">
      <div class="hero-copy">
        <h1>帮助当地文旅、政府、企业与文化资产高效对接</h1>
        <p class="hero-subtitle">成功案例</p>
        <div class="hero-success" v-if="heroSuccess.length">
          <div class="success-item" v-for="item in heroSuccess" :key="item.title">
            <div class="success-stat">
              <span class="stat-value">{{ item.value }}</span>
              <span class="stat-unit">{{ item.unit }}</span>
            </div>
            <div class="success-title">{{ item.title }}</div>
            <div class="success-desc">{{ item.desc }}</div>
          </div>
        </div>
        <div class="hero-partners" v-if="heroPartners.length">
          <span class="partners-label">合作伙伴</span>
          <div class="partners-tags">
            <span v-for="name in heroPartners" :key="name" class="partner-tag">{{ name }}</span>
          </div>
        </div>
      </div>
      <div class="hero-carousel" id="carousel" v-if="carouselBanners.length > 0">
        <el-carousel
          :interval="5200"
          arrow="never"
          height="360px"
          indicator-position="outside"
          :autoplay="true"
          :pause-on-hover="false"
          trigger="click"
          :loop="carouselBanners.length > 1"
        >
          <el-carousel-item v-for="(banner, index) in carouselBanners" :key="banner.id || index">
            <div
              class="hero-slide simple"
              :style="{ backgroundImage: `url(${banner.image})` }"
              @click="handleBannerClick(banner)"
            >
              <div class="hero-slide-mask"></div>
              <div class="hero-slide-content simple">
                <h2>{{ banner.title }}</h2>
                <p>{{ banner.subtitle }}</p>
                <div v-if="banner.tags?.length" class="slide-tags">
                  <span v-for="tag in banner.tags" :key="tag">{{ tag }}</span>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 核心菜单卡片轮播 -->
    <section id="nav-cards" class="nav-rotator">
      <div class="section-header minimal"></div>
      <div class="nav-card-track" @mouseenter="stopNavAutoplay" @mouseleave="startNavAutoplay">
        <div
          v-for="(card, index) in displayedNavCards"
          :key="card.title + index"
          :class="['nav-card', { active: index === 0 }]"
          :style="getNavCardStyle(index)"
          @click="handleNavCardClick(index)"
        >
          <div class="nav-card-body">
            <h3>{{ card.title }}</h3>
            <p>{{ card.desc }}</p>
            <div class="nav-card-tags">
              <span v-for="tag in card.tags" :key="tag" class="nav-card-tag">{{ tag }}</span>
            </div>
            <div class="nav-card-footer">
              <span class="nav-card-cta" @click.stop="handleNavLinkClick(card)">{{ card.meta }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>


    <!-- 产业振兴 -->
    <div id="services" class="service-categories">
      <h2 class="section-title">
        <span class="title-icon">🎨</span>
        产业振兴
      </h2>
      <div class="categories-tabs">
        <div 
          v-for="tab in serviceTabs" 
          :key="tab.value"
          :class="['category-tab', { 'active': activeTab === tab.value }]"
          @click="activeTab = tab.value"
        >
          <span class="tab-text">{{ tab.label }}</span>
        </div>
      </div>
      
      <div class="service-content">
        <div class="service-grid">
          <div v-for="service in currentServices" :key="service.id" class="service-card" @click="handleServiceClick(service)">
            <div v-if="service.cover" class="service-image">
              <el-image
                :src="service.cover"
                fit="cover"
                class="service-cover-img"
                :preview-src-list="service.cover ? [service.cover] : []"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>加载失败</span>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="service-header">
              <div class="service-title-wrap">
                <h3>{{ service.title }}</h3>
                <el-tag v-if="activeTab === 'products' && service.badge" size="small" type="success">{{ service.badge }}</el-tag>
              </div>
              <div class="service-price-block">
                <span class="service-price">¥{{ service.price }}</span>
                <span class="service-price-unit">
                  <template v-if="activeTab === 'products'">
                    {{ service.unit && service.unit.startsWith('/') ? service.unit : '/' + (service.unit || '盒') }}
                  </template>
                  <template v-else-if="activeTab === 'homestay'">/晚</template>
                  <template v-else>
                    {{ service.unit && service.unit.startsWith('/') ? service.unit : '/' + (service.unit || '人') }}
                  </template>
                </span>
              </div>
            </div>

            <div class="service-meta" :class="{ 'product-meta': activeTab === 'products' }">
              <span class="service-location">
                <el-icon><Location /></el-icon>
                {{ service.location }}
              </span>
              <span class="service-rating">
                <el-icon><Star /></el-icon>
                {{ service.rating }}
              </span>
              <span v-if="(activeTab === 'products' && service.viewCount) || (activeTab !== 'products' && service.views)" class="service-views">
                <el-icon><View /></el-icon>
                {{ activeTab === 'products' ? service.viewCount : service.views }} 浏览
              </span>
            </div>

            <p v-if="service.summary" class="service-summary">{{ service.summary }}</p>

            <div v-if="activeTab === 'products'" class="product-extra">
              <span v-if="service.sales">
                <el-icon><TrendCharts /></el-icon>
                月销 {{ service.sales }}
              </span>
              <span v-if="service.origin">
                <el-icon><Box /></el-icon>
                产地直发：{{ service.origin }}
              </span>
              <span v-if="service.shelfLife">
                <el-icon><Clock /></el-icon>
                保质期 {{ service.shelfLife }}
              </span>
            </div>
            <div v-else-if="activeTab === 'homestay'" class="product-extra">
              <span v-if="service.roomType">
                <el-icon><House /></el-icon>
                {{ service.roomType }}
              </span>
              <span v-if="service.capacity">
                <el-icon><User /></el-icon>
                可住 {{ service.capacity }} 人
              </span>
              <span v-if="service.amenities?.length">
                <el-icon><Setting /></el-icon>
                配套：{{ service.amenities.slice(0, 3).join(' / ') }}
              </span>
            </div>
            <div v-else-if="activeTab === 'farmstay'" class="product-extra">
              <span v-if="service.contactPhone">
                <el-icon><Phone /></el-icon>
                {{ service.contactPhone }}
              </span>
              <span v-if="service.views">
                <el-icon><TrendCharts /></el-icon>
                热度 {{ service.views }}
              </span>
            </div>

            <div v-if="getAllTags(service).length" class="service-tags">
              <span v-for="tag in getAllTags(service)" :key="tag" class="tag-item">#{{ tag }}</span>
            </div>
          </div>
        </div>
        <div v-if="!currentServices.length" class="empty-hint">暂无产业振兴数据</div>
      </div>
    </div>

    <!-- 政策对接 -->
    <div id="projects" class="gov-cooperation">
      <h2 class="section-title">
        <span class="title-icon">🤝</span>
        政策对接
      </h2>
      <div class="gov-projects-grid">
        <div v-for="project in govProjects" :key="project.id" class="gov-project-card" @click="handleProjectClick(project)">
          <div class="project-header">
            <h3>{{ project.title }}</h3>
            <span :class="['project-status', project.statusClass]">{{ project.statusText }}</span>
          </div>
          <p class="project-desc">{{ project.description }}</p>

          <div class="gov-meta">
            <span class="meta-item">
              <el-icon><Location /></el-icon>
              <span class="meta-text">{{ project.location }}</span>
            </span>
            <span class="meta-divider">|</span>
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span class="meta-text">{{ project.startDate }}</span>
            </span>
            <span class="meta-divider">|</span>
            <span class="meta-item">
              <el-icon><Coin /></el-icon>
              <span class="meta-text">投资 {{ project.investment }} 万</span>
            </span>
            <span class="meta-divider">|</span>
            <span class="meta-item">
              <el-icon><UserFilled /></el-icon>
              <span class="meta-text">惠及 {{ project.beneficiaries }} 户</span>
            </span>
          </div>

          <div class="gov-tags">
            <span v-for="(tag, index) in project.tags" :key="tag" class="gov-tag">
              <span class="tag-text">{{ tag }}</span>
              <span v-if="index < project.tags.length - 1" class="tag-divider">|</span>
            </span>
          </div>

          <div class="gov-contact">
            <span v-if="project.contactName">负责人：{{ project.contactName }}</span>
            <span v-if="project.contactPhone">咨询：{{ project.contactPhone }}</span>
          </div>

          <div class="gov-goal-brief" v-if="project.goalBrief">
            {{ project.goalBrief }}
          </div>
        </div>
      </div>
      <div v-if="!govProjects.length" class="empty-hint">暂无政策对接项目</div>
    </div>

    <!-- 文化体验 -->
    <div id="experience" class="culture-experience">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">🎭</span>
          文化体验
        </h2>
      </div>
      <div class="experience-grid">
        <div v-for="experience in cultureExperiences" :key="experience.id" class="experience-card" @click="handleCategoryClick(experience)">
          <div class="experience-image">
            <img :src="experience.image" :alt="experience.name" />
          </div>
          <div class="experience-content">
            <h3 class="experience-title">{{ experience.name }}</h3>
            <p class="experience-location">
              <el-icon><Location /></el-icon>
              <span>{{ experience.location }}</span>
            </p>
            <div class="experience-features">
              <span class="feature-item">
                <el-icon><Clock /></el-icon>
                <span>{{ experience.duration }}</span>
              </span>
              <span class="feature-item">
                <el-icon><StarFilled /></el-icon>
                <span>{{ experience.rating }}</span>
              </span>
            </div>
            <div class="experience-footer">
              <span class="experience-price">
                <span class="price-label">体验价</span>
                <span class="price-value">¥{{ experience.price || 0 }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!cultureExperiences.length" class="empty-hint">暂无文化体验项目</div>
    </div>

    <!-- 特色周边 -->
    <div id="products" class="local-products">
      <h2 class="section-title">
        <span class="title-icon">🛍️</span>
        特色周边
      </h2>
      <div class="products-grid">
        <div v-for="product in attractions" :key="product.id" class="product-card" @click="handleProductClick(product)">
          <div v-if="product.cover" class="product-image">
            <el-image
              :src="product.cover"
              fit="cover"
              class="product-cover-img"
              :preview-src-list="product.cover ? [product.cover] : []"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
          <div class="product-badge">{{ product.badge }}</div>
          <div class="product-header">
            <h4>{{ product.name || product.title }}</h4>
          </div>
          <div class="product-meta">
            <span class="meta-item">
              <el-icon><Location /></el-icon>
              {{ product.origin || product.location }}
            </span>
            <span class="meta-item">
              <el-icon><StarFilled /></el-icon>
              {{ product.rating }}
            </span>
          </div>
          <div class="product-footer">
            <span class="product-sales">已售 {{ product.sales }}</span>
            <span class="product-price">
              ¥{{ product.price }}
              <span class="product-price-unit">
                {{ product.unit && product.unit.startsWith('/') ? product.unit : '/' + (product.unit || '件') }}
              </span>
            </span>
          </div>
        </div>
      </div>
      <div v-if="!attractions.length" class="empty-hint">暂无特色周边</div>
    </div>



    <!-- 返回顶部按钮 -->
    <transition name="fade">
      <div 
        v-show="showBackToTop" 
        class="back-to-top" 
        @click="scrollToTop"
        title="返回顶部"
      >
        <el-icon :size="24">
          <Top />
        </el-icon>
      </div>
    </transition>

    <!-- 右侧固定导航条 -->
    <div class="side-nav">
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'banner' }"
        @click="scrollToSection('banner')"
      >
        <el-icon><Star /></el-icon>
        <span class="nav-text">顶部横幅</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'nav-cards' }"
        @click="scrollToSection('nav-cards')"
      >
        <el-icon><Grid /></el-icon>
        <span class="nav-text">核心板块</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'services' }"
        @click="scrollToSection('services')"
      >
        <el-icon><Service /></el-icon>
        <span class="nav-text">产业振兴</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'projects' }"
        @click="scrollToSection('projects')"
      >
        <el-icon><OfficeBuilding /></el-icon>
        <span class="nav-text">政策对接</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'experience' }"
        @click="scrollToSection('experience')"
      >
        <el-icon><Tickets /></el-icon>
        <span class="nav-text">文化体验</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'products' }"
        @click="scrollToSection('products')"
      >
        <el-icon><ShoppingBag /></el-icon>
        <span class="nav-text">特色周边</span>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from "vue-router";
import request from '@/utils/request';
import { processImageUrl, processImageUrls } from '@/utils/image';
import { 
  Star, Grid, Service, OfficeBuilding, Tickets, ShoppingBag, Trophy, UserFilled, Top,
  ArrowLeft, ArrowRight, Location, Calendar, Coin, Clock, StarFilled, View, TrendCharts, Box,
  House, User, Setting, Phone, Picture
} from '@element-plus/icons-vue';
import BackButton from '@/components/BackButton.vue';
import { useSystemStore } from '@/stores/system';
import { storeToRefs } from 'pinia';
import * as cultureExperienceApi from '@/api/cultureExperience';

const router = useRouter();
const systemStore = useSystemStore();
const { footerDescription } = storeToRefs(systemStore);

console.log('中等版本文旅页面开始加载');

// 当前激活的区块
const activeSection = ref('banner');

// 是否显示返回顶部按钮
const showBackToTop = ref(false);

// 顶部数据展示
const heroStats = ref([
  { label: '合作文旅项目', value: '320+' },
  { label: '年度活跃对接', value: '1.2亿' },
  { label: '政府协同', value: '48个地区' }
]);

// 合作伙伴
const partners = ref([
  { id: 1, emoji: '🏛️', name: '重庆市文化和旅游发展委员会' },
  { id: 2, emoji: '🏢', name: '重庆市农业农村委员会' },
  { id: 3, emoji: '🏦', name: '中国农业银行重庆分行' },
  { id: 4, emoji: '🏭', name: '重庆旅游投资集团' },
  { id: 5, emoji: '🎓', name: '重庆大学旅游学院' },
  { id: 6, emoji: '📱', name: '携程旅游' },
  { id: 7, emoji: '🌐', name: '美团点评' },
  { id: 8, emoji: '🚗', name: '滴滴出行' },
  { id: 9, emoji: '🤝', name: '本地文旅合作联盟' }
]);

const heroSuccess = ref([
  { title: '政策对接', desc: '申报直连与合规指导', value: '320+', unit: '项目/年' },
  { title: '产业振兴', desc: '规划落地与运营陪跑', value: '48', unit: '产业带' },
  { title: '文化体验', desc: '非遗体验与精品路线', value: '560+', unit: '体验' },
  { title: '特色周边', desc: '供应链与全渠道上架', value: '1.2K', unit: 'SKU' }
]);
const heroPartners = computed(() => {
  return partners.value.slice(0, 4).map(p => p.name);
});

// 轮播图数据
const carouselBanners = ref([]);

// 四大核心功能（与详情页对应）
const coreNavCards = ref([
  { title: '政策对接', desc: '政府窗口直连,政策解读项目申报一站协同', meta: '立即对接', icon: '', accent: 'linear-gradient(135deg, #e3e8ff, #f6f7ff)', path: '/home/admin/culture', tags: ['政策解读', '项目申报', '合规指导'] },
  { title: '产业振兴', desc: '产业规划运营陪跑培训落地,帮你把路走稳', meta: '查看方案', icon: '', accent: 'linear-gradient(135deg, #e9fff7, #f8fffb)', path: '/home/user/culture/project', tags: ['规划落地', '运营陪跑', '培训赋能'] },
  { title: '文化体验', desc: '非遗工坊沉浸演艺与精品路线，一键预约体验', meta: '去体验', icon: '', accent: 'linear-gradient(135deg, #e8f5ff, #f7fbff)', path: '/home/user/culture/experience', tags: ['非遗工坊', '沉浸演艺', '精品路线'] },
  { title: '特色周边', desc: '产地直采+供应链,上架渠道与履约一体化', meta: '上架商品', icon: '', accent: 'linear-gradient(135deg, #fff4e8, #fffaf3)', path: '/home/user/culture/product', tags: ['供应链', '多渠道上架', '履约售后'] }
])

const activeNavIndex = ref(0)
const navAutoplayTimer = ref(null)

const displayedNavCards = computed(() => {
  if (!coreNavCards.value.length) return []
  const result = []
  for (let i = 0; i < Math.min(4, coreNavCards.value.length); i++) {
    result.push(coreNavCards.value[(activeNavIndex.value + i) % coreNavCards.value.length])
  }
  return result
})

const getNavCardStyle = (index) => {
  // 叠层效果：当前大、等待小，后排更小更淡
  const presets = [
    { offset: 0, scale: 1.05, opacity: 1, zIndex: 4, blur: 'none' },
    { offset: 260, scale: 0.9, opacity: 0.78, zIndex: 3, blur: 'none' },
    { offset: -260, scale: 0.9, opacity: 0.78, zIndex: 3, blur: 'none' },
    { offset: 520, scale: 0.85, opacity: 0.45, zIndex: 2, blur: 'blur(0.6px)' }
  ]
  const preset = presets[index] || presets[presets.length - 1]
  return {
    transform: `translateX(-50%) translateX(${preset.offset}px) scale(${preset.scale})`,
    opacity: preset.opacity,
    zIndex: preset.zIndex,
    filter: preset.blur
  }
}

const rotateNavCard = (step = 1) => {
  const total = coreNavCards.value.length
  if (!total) return
  activeNavIndex.value = (activeNavIndex.value + step + total) % total
}

const nextNavCard = () => rotateNavCard(1)
const prevNavCard = () => rotateNavCard(-1)

const startNavAutoplay = () => {
  if (navAutoplayTimer.value || coreNavCards.value.length <= 1) return
  navAutoplayTimer.value = setInterval(() => {
    nextNavCard()
  }, 3000)
}

const stopNavAutoplay = () => {
  if (navAutoplayTimer.value) {
    clearInterval(navAutoplayTimer.value)
    navAutoplayTimer.value = null
  }
}

const handleNavCardClick = (index) => {
  if (index === 0) return
  // 左边等待卡
  if (index === 2) {
    prevNavCard()
    return
  }
  // 右边等待卡
  nextNavCard()
}

const handleNavLinkClick = (card) => {
  if (card?.path) {
    router.push(card.path)
  } else {
    scrollToSection('modules')
  }
}

const goModule = (module) => {
  if (module?.path) {
    router.push(module.path)
  }
}

// 加载轮播图数据（从后端获取）
const loadBanners = async () => {
  try {
    const response = await request.get('/user/home/banners', {
      params: { position: 'CULTURE' }
    })
    if (response.code === 200 && response.data && response.data.length > 0) {
      // 转换后端数据格式为前端需要的格式
      carouselBanners.value = response.data.map(banner => {
        const rawTags =
          banner.tags ||
          banner.tag ||
          banner.tagList ||
          banner.tagNames ||
          banner.tagsText ||
          banner.label ||
          ''
        let tagList = Array.isArray(rawTags)
          ? rawTags
          : (rawTags || '').split(/[,，、\s]+/).filter(Boolean)
        // 如果仍然没有标签，尝试从副标题拆分
        if (!tagList.length && banner.subtitle) {
          tagList = banner.subtitle.split(/[,，、\s]+/).filter(Boolean)
        }
        return {
          id: banner.id,
          title: banner.title || '',
          subtitle: banner.subtitle || '',
          tags: tagList.length ? tagList.slice(0, 4) : ['文旅项目'],
          image: banner.imageUrl || banner.image,
          link: getBannerLink(banner.linkType, banner.linkValue),
          linkType: banner.linkType || 0,
          linkValue: banner.linkValue || ''
        }
      })
    } else {
      // 没有数据时显示空列表
      carouselBanners.value = []
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
    // 加载失败时显示空列表
    carouselBanners.value = []
  }
}

// 根据链接类型和值生成跳转链接
const getBannerLink = (linkType, linkValue) => {
  if (!linkValue) return ''
  
  switch (linkType) {
    case 0: // 无链接
      return ''
    case 1: // 内部页面
      return linkValue
    case 2: // 外部链接
      return linkValue
    case 3: // 景点详情
      return `/home/user/attractions/detail/${linkValue}`
    case 4: // 攻略详情
      return `/home/user/plans/detail/${linkValue}`
    case 5: // 活动详情
      return `/home/user/activity/detail/${linkValue}`
    default:
      return linkValue
  }
}

// 核心板块
const coreModules = ref([
  {
    icon: OfficeBuilding,
    title: '政策对接',
    description: '政府窗口直连，政策申报与合规指导',
    count: 28,
    path: '/home/admin/culture'
  },
  {
    icon: Grid,
    title: '产业振兴',
    description: '规划落地、运营陪跑、培训赋能',
    count: 45,
    path: '/home/user/culture/project'
  },
  {
    icon: Tickets,
    title: '文化体验',
    description: '精品体验、非遗工坊、沉浸演艺',
    count: 63,
    path: '/home/user/culture/experience'
  },
  {
    icon: ShoppingBag,
    title: '特色周边',
    description: '产地直采、供应链与全渠道上架',
    count: 156,
    path: '/home/user/culture/product'
  }
])

// 服务标签页
const serviceTabs = ref([
  { value: 'farmstay', label: '农家乐' },
  { value: 'homestay', label: '特色民宿' },
  { value: 'products', label: '农特产品' }
])

const activeTab = ref('farmstay')

// 服务数据
const services = ref({
  farmstay: [],
  homestay: [],
  products: []
})

const currentServices = computed(() => {
  return services.value[activeTab.value] || services.value.farmstay
})

// 政策对接
const govProjects = ref([])

// 文化体验项目（与管理端数据结构对应）
const cultureExperiences = ref([])

// 特色周边（独立数据源，与农特产品区分）
const attractions = ref([])

const normalizeImages = (images) => {
  return processImageUrls(images)
}

// 通用：把字符串 / JSON 字符串 / 数组统一转成数组
const normalizeArray = (val) => {
  if (!val) return []
  if (Array.isArray(val)) return val
  try {
    const parsed = JSON.parse(val)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

// 去重并过滤空值
const dedupeTags = (arr = []) => {
  return [...new Set((arr || []).filter(Boolean))]
}

const loadCultureExperiences = async () => {
  try {
    const res = await cultureExperienceApi.getUserExperienceList({ page: 1, size: 6 })
    if (res.code === 200 && (res.data?.list?.length || 0) > 0) {
      cultureExperiences.value = res.data.list.map((item, index) => {
        const imgs = normalizeImages(item.images)
        return {
          id: item.id,
          name: item.name,
          categoryName: item.categoryName || '文化体验',
          location: item.location || '待定',
          price: item.price ? Number(item.price) : 0,
          duration: item.duration || '约2小时',
          rating: item.rating ? Number(item.rating) : 4.8,
          status: item.status || 'featured',
          image: imgs[0] || `https://picsum.photos/800/600?random=${880 + index}`
        }
      })
    } else {
      cultureExperiences.value = []
    }
  } catch (error) {
    console.error('加载文化体验列表失败', error)
    cultureExperiences.value = []
  }
}

// 加载农家乐
const loadFarmstay = async () => {
  try {
    const res = await request.get('/culture/services/page', { params: { page: 1, size: 4 } })
    const list = res?.data?.records || res?.data?.list || []
    if (Array.isArray(list) && list.length) {
      const baseList = list.map((item, idx) => {
        const featuresRaw = normalizeArray(item.features)
        const features = dedupeTags(featuresRaw) // 只用特色字段

        // 处理图片：优先使用 images，其次使用 cover
        let imagesArray: string[] = [];
        if (item.images) {
          if (Array.isArray(item.images)) {
            imagesArray = item.images;
          } else if (typeof item.images === 'string') {
            try {
              const parsed = JSON.parse(item.images);
              imagesArray = Array.isArray(parsed) ? parsed : [item.images];
            } catch {
              imagesArray = item.images.split(',').map((url: string) => url.trim()).filter(Boolean);
            }
          }
        } else if (item.cover) {
          imagesArray = [item.cover];
        }
        
        const coverUrl = imagesArray.length > 0 ? processImageUrl(imagesArray[0]) : '';

        return {
          id: item.id || idx,
          title: item.title || item.name || `农家乐 ${idx + 1}`,
          location: item.location || '待定',
          // 先用后端直接给的价格字段占位，后面再用详情里的套餐价格覆盖
          price: item.price || 0,
          unit: item.unit || '/人',
          summary: item.description || item.summary || '',
          rating: item.rating || 4.8,
          views: item.views || item.viewCount || 0,
          contactPhone: item.contactPhone || '',
          features,
          highlights: [], // 卡片不再使用 highlights
          cover: coverUrl // 添加封面图片
        }
      })

      services.value.farmstay = baseList

      // 再补充每个农家乐的套餐价格信息（取所有套餐里最低价）
      await Promise.all(
        services.value.farmstay.map(async (svc) => {
          try {
            const detailRes = await request.get(`/culture/services/${svc.id}`)
            if (detailRes.code === 200 && detailRes.data) {
              const pkgs = Array.isArray(detailRes.data.packages)
                ? detailRes.data.packages
                : normalizeArray(detailRes.data.packages)
              if (pkgs.length) {
                const minPkg = pkgs.reduce((min, p) => {
                  if (!p || p.price == null) return min
                  if (!min || Number(p.price) < Number(min.price)) return p
                  return min
                }, null)
                if (minPkg && minPkg.price != null) {
                  svc.price = Number(minPkg.price)
                  svc.unit = minPkg.unit || '/人'
                }
              }
            }
          } catch (e) {
            // 单个失败不影响整体
            console.warn('加载农家乐套餐价格失败', svc.id, e)
          }
        })
      )
    } else {
      services.value.farmstay = []
    }
  } catch (error) {
    console.warn('加载农家乐失败，保持空数据', error)
    services.value.farmstay = []
  }
}

// 加载民宿
const loadHomestay = async () => {
  try {
    // 注意：已使用全局 baseURL '/api'，这里不要再重复 '/api'
    const res = await request.get('/culture/homestays/page', { params: { page: 1, size: 4 } })
    const list = res?.data?.records || res?.data?.list || []
    console.log('首页民宿列表数据:', list)
    if (Array.isArray(list) && list.length) {
      services.value.homestay = list.map((item, idx) => {
        console.log(`民宿${idx}原始数据:`, item)
        console.log(`民宿${idx} cover:`, item.cover)
        console.log(`民宿${idx} images:`, item.images)
        
        // 先解析图片数组
        let imagesArray: string[] = [];
        if (item.images) {
          if (Array.isArray(item.images)) {
            imagesArray = item.images;
          } else if (typeof item.images === 'string') {
            try {
              const parsed = JSON.parse(item.images);
              imagesArray = Array.isArray(parsed) ? parsed : [item.images];
            } catch {
              imagesArray = item.images.split(',').map((url: string) => url.trim()).filter(Boolean);
            }
          }
        } else if (item.cover) {
          imagesArray = [item.cover];
        }
        
        const coverUrl = imagesArray.length > 0 ? processImageUrl(imagesArray[0]) : '';
        console.log(`民宿${idx}处理后的cover:`, coverUrl);
        
        return {
          id: item.id || idx,
          title: item.title || item.name || `民宿 ${idx + 1}`,
          location: item.location || '待定',
          price: item.price || 0,
          rating: item.rating || 4.7,
          views: item.views || 0,
          roomType: item.roomType,
          capacity: item.capacity,
          features: normalizeArray(item.features) || normalizeArray(item.amenities),
          highlights: normalizeArray(item.highlightTags) || normalizeArray(item.amenities),
          summary: item.description || item.summary || '',
          contactPhone: item.contactPhone || '',
          amenities: normalizeArray(item.amenities),
          cover: coverUrl
        }
      })
    } else {
      services.value.homestay = []
    }
  } catch (error) {
    console.warn('加载民宿失败，保持空数据', error)
    services.value.homestay = []
  }
}

// 加载农特产品（从culture/products获取，productType=4）
const loadAgriProducts = async () => {
  try {
    console.log('开始加载农特产品...')
    // 使用 /culture/products/page 接口，productType=4 表示农特产品
    const res = await request.get('/culture/products/page', {
      params: { page: 1, size: 20, type: 4 }
    })
    console.log('农特产品API响应:', res)
    
    // 接口返回的是 Page 对象，数据在 records 字段中
    const list = res?.data?.records || res?.data?.list || []
    console.log('农特产品列表:', list, '数量:', list?.length || 0)
    
    if (Array.isArray(list) && list.length) {
      // 按销量和评分排序
      const sorted = [...list].sort((a, b) => {
        const aHot = (a.salesCount || 0) + (a.reviewCount || 0) * 10
        const bHot = (b.salesCount || 0) + (b.reviewCount || 0) * 10
        return bHot - aHot
      })
      
      services.value.products = sorted.slice(0, 6).map((item, idx) => {
        const tags = normalizeArray(item.features || item.highlights)
        const price = item.price ? Number(item.price) : 0
        const images = normalizeImages(item.images || item.coverImage)
        
        return {
          id: item.id || idx,
          title: item.productName || `农特产品 ${idx + 1}`,
          name: item.productName || `农特产品 ${idx + 1}`,
          location: item.origin || '待定',
          origin: item.origin || '本地',
          badge: item.badge || tags[0] || '农特产品',
          price,
          sales: item.salesCount || 0,
          rating: item.rating ? Number(item.rating) : 4.5,
          unit: item.unit || '/件',
          viewCount: item.reviewCount || 0,
          summary: item.summary || item.description || '',
          cover: images[0] || ''
        }
      })
      console.log('农特产品加载成功，数量:', services.value.products.length)
    } else {
      services.value.products = []
      console.warn('农特产品列表为空')
    }
  } catch (error) {
    console.error('加载农特产品失败:', error)
    services.value.products = []
  }
}

// 加载特色周边（从attractions获取，type=8）
const loadAttractions = async () => {
  try {
    console.log('开始加载特色周边...')
    // type = 8 为特色周边，status 在服务端固定为已上架
    const res = await request.get('/culture/attractions/page', {
      params: { page: 1, size: 6, type: 8 }
    })
    console.log('特色周边API响应:', res)
    
    const list = res?.data?.records || res?.data?.list || []
    console.log('特色周边列表:', list, '数量:', list.length)
    
    if (Array.isArray(list) && list.length) {
      attractions.value = list.map((item, idx) => {
        const price = item.ticketPrice != null ? Number(item.ticketPrice) : 0
        const rating = item.rating != null ? Number(item.rating) : 4.6
        const locationParts = [item.province, item.city].filter(Boolean)
        const tags = Array.isArray(item.tags)
          ? item.tags
          : (typeof item.tags === 'string'
              ? item.tags.split(/[,，、\s]+/).filter(Boolean)
              : [])

        // 处理图片：优先使用 images，其次使用 coverImage
        let imagesArray: string[] = [];
        if (item.images) {
          if (Array.isArray(item.images)) {
            imagesArray = item.images;
          } else if (typeof item.images === 'string') {
            try {
              const parsed = JSON.parse(item.images);
              imagesArray = Array.isArray(parsed) ? parsed : [item.images];
            } catch {
              imagesArray = item.images.split(',').map((url: string) => url.trim()).filter(Boolean);
            }
          }
        } else if (item.coverImage) {
          imagesArray = [item.coverImage];
        } else if (item.image) {
          imagesArray = [item.image];
        }
        
        const coverUrl = imagesArray.length > 0 ? processImageUrl(imagesArray[0]) : '';

        return {
          id: item.id || idx,
          title: item.name || `特色周边 ${idx + 1}`,
          name: item.name || `特色周边 ${idx + 1}`,
          location: locationParts.join(' · ') || item.address || '待定',
          origin: item.city || item.province || '本地',
          // 角标优先使用后台单独的 badge 字段，其次用 tags 的第一个
          badge: item.badge || tags[0] || '特色周边',
          price,
          sales: item.viewCount || 0,
          rating,
          unit: '/件',
          viewCount: item.viewCount || 0,
          cover: coverUrl // 添加封面图片
        }
      })
      console.log('特色周边加载成功，数量:', attractions.value.length)
    } else {
      attractions.value = []
      console.warn('特色周边列表为空')
    }
  } catch (error) {
    console.error('加载特色周边失败:', error)
    attractions.value = []
  }
}

// 加载政策对接（只获取政府项目 type=3）
const loadGovProjects = async () => {
  try {
    console.log('开始加载政策对接项目...')
    const res = await request.get('/culture-project/list', { 
      params: { 
        page: 1, 
        size: 6,
        type: 3  // 只获取政府项目
        // 不限制status，显示所有状态的政府项目
      } 
    })
    console.log('📦 API响应:', res)
    console.log('📦 res.data:', res?.data)
    
    // 后端返回格式：Result.success("获取文旅项目列表成功", result)
    // result 是 Map<String, Object>，包含 list, total, page, size
    const list = res?.data?.list || res?.data?.records || (Array.isArray(res?.data) ? res.data : [])
    console.log('📋 解析后的列表:', list, '长度:', list?.length)
    
    if (Array.isArray(list) && list.length) {
      console.log('找到', list.length, '个政府项目')
      govProjects.value = list.slice(0, 6).map((item, idx) => {
        // 处理状态文本和样式
        let statusText = '进行中'
        let statusClass = 'status-active'
        if (item.status === 1) {
          statusText = '正常'
          statusClass = 'status-active'
        } else if (item.status === 2) {
          statusText = '维护中'
          statusClass = 'status-recruiting'
        } else if (item.status === 0) {
          statusText = '已关闭'
          statusClass = ''
        }
        
        // 处理创建时间
        let startDate = ''
        if (item.createTime) {
          if (typeof item.createTime === 'string') {
            startDate = item.createTime.slice(0, 10)
          } else {
            startDate = new Date(item.createTime).toISOString().slice(0, 10)
          }
        }
        
        return {
          id: item.id || idx,
          title: item.name || `项目 ${idx + 1}`,  // 使用 name 字段
          location: item.location || item.region || item.address || '待定',
          description: item.description || '',
          tags: normalizeArray(item.features) || [],
          startDate: startDate,
          investment: item.price ? Number(item.price) : 0,  // 使用 price 字段作为投资规模
          beneficiaries: 0,  // 如果没有这个字段，默认为0
          statusText: statusText,
          statusClass: statusClass,
          contactName: item.contactPerson || '',
          contactPhone: item.contactPhone || '',
          goalBrief: item.description ? (item.description.length > 50 ? item.description.slice(0, 50) + '...' : item.description) : ''
        }
      })
      console.log('处理后的项目列表:', govProjects.value)
    } else {
      console.warn('⚠️ 没有找到政府项目数据，列表为空或不是数组')
      govProjects.value = []
    }
  } catch (error) {
    console.error('❌ 加载政策对接失败:', error)
    console.error('错误详情:', error?.response || error?.message || error)
    govProjects.value = []
  }
}

// 成功案例
const successCases = ref([
  {
    id: 1,
    title: '巴南区木洞古镇文旅振兴项目',
    location: '重庆市巴南区',
    description: '通过文化挖掘、旅游开发、产业融合，木洞古镇从落后小镇蜕变为年游客量超50万的文旅名镇，带动当地就业300余人，年收入突破3000万。',
    revenue: 3000,
    jobs: 300,
    visitors: 50
  },
  {
    id: 2,
    title: '武隆仙女山民宿集群发展计划',
    location: '重庆市武隆区',
    description: '引入专业团队打造高端民宿集群，结合当地自然景观和土家文化，形成特色民宿品牌，年收入达5000万，创造就业岗位500余个。',
    revenue: 5000,
    jobs: 500,
    visitors: 80
  },
  {
    id: 3,
    title: '江津区花椒产业链升级项目',
    location: '重庆市江津区',
    description: '建立花椒文化体验园，整合种植、加工、销售、旅游全产业链，年产值超2000万，带动农户增收，吸引游客30万人次。',
    revenue: 2000,
    jobs: 180,
    visitors: 30
  }
])

// 交互函数
// 轮播图点击
const handleBannerClick = async (banner) => {
  // 记录点击统计
  if (banner.id) {
    try {
      await request.post(`/user/home/banner/${banner.id}/click`)
    } catch (error) {
      console.error('记录点击统计失败:', error)
    }
  }
  
  // 跳转逻辑
  if (banner.link) {
    if (banner.linkType === 2 || banner.link?.startsWith?.('http')) {
      window.open(banner.link, '_blank')
    } else {
      router.push(banner.link)
    }
  } else if (banner.linkType && banner.linkValue) {
    // 根据链接类型跳转
    const link = getBannerLink(banner.linkType, banner.linkValue)
    if (link) {
      if (banner.linkType === 2 || link.startsWith('http')) {
        window.open(link, '_blank')
      } else {
        router.push(link)
      }
    }
  }
}

const handleServiceClick = (service) => {
  console.log('跳转到服务详情:', service.title)
  if (activeTab.value === 'homestay') {
    router.push(`/home/user/culture/homestay/${service.id}`)
  } else if (activeTab.value === 'products') {
    router.push(`/home/user/culture/agri-product/${service.id}`)
  } else {
    router.push(`/home/user/culture/service/${service.id}`)
  }
}

// 只展示“特色字段”的标签，最多 8 个
const getFeatures = (service) => {
  const list = Array.isArray(service.features) ? service.features : []
  return dedupeTags(list).slice(0, 8)
}

// 高亮标签暂时不参与卡片展示
const getHighlights = (_service) => {
  return []
}

// 卡片标签 = 特色字段（已去重）且最多 8 个
const getAllTags = (service) => {
  const features = getFeatures(service)
  return dedupeTags(features).slice(0, 8)
}

const handleProjectClick = (project) => {
  console.log('跳转到政府项目详情:', project.title)
  // TODO: 跳转到政府项目详情页
  router.push(`/home/user/culture/project/${project.id}`)
}

const handleCategoryClick = (category) => {
  console.log('跳转到文化体验详情:', category.name)
  // 跳转到文化体验详情页（文化体验专属详情）
  router.push(`/home/user/culture/experience/${category.id}`)
}

// 获取滚动容器（el-main元素）
const getScrollContainer = () => {
  // 查找 el-main 容器
  return document.querySelector('.el-main') || document.querySelector('.main-content')
}

// 滚动到指定区域
const scrollToSection = (sectionId) => {
  console.log('🎯 点击导航:', sectionId)
  const element = document.getElementById(sectionId)
  
  if (!element) {
    console.error('❌ 未找到元素:', sectionId)
    return
  }
  
  console.log('找到元素:', element)
  
  // 查找滚动容器（优先查找 .main-content，然后是 .el-main）
  const scrollContainer = document.querySelector('.main-content') || 
                          document.querySelector('.el-main') || 
                          document.documentElement
  
  if (scrollContainer) {
    console.log('📦 找到滚动容器:', scrollContainer)
    
    // 获取元素相对于滚动容器的位置
    const containerRect = scrollContainer.getBoundingClientRect()
    const elementRect = element.getBoundingClientRect()
    const currentScrollTop = scrollContainer.scrollTop || window.pageYOffset || document.documentElement.scrollTop
    
    // 计算元素相对于滚动容器的实际位置
    // elementRect.top 是元素相对于视口的位置
    // containerRect.top 是滚动容器相对于视口的位置
    // 两者相减得到元素相对于滚动容器的位置，再加上当前滚动位置
    const elementOffsetTop = elementRect.top - containerRect.top + currentScrollTop
    
    // 顶部导航栏高度（header高度 + 可能的padding）
    const headerOffset = 100
    
    // 计算目标滚动位置
    const targetPosition = Math.max(0, elementOffsetTop - headerOffset)
    
    console.log('📍 滚动位置计算:', {
      sectionId,
      currentScrollTop,
      elementRectTop: elementRect.top,
      containerRectTop: containerRect.top,
      elementOffsetTop,
      headerOffset,
      targetPosition
    })
    
    // 执行滚动
    scrollContainer.scrollTo({
      top: targetPosition,
      behavior: 'smooth'
    })
    
    // 手动设置激活状态
    activeSection.value = sectionId
  } else {
    // 兜底方案：使用浏览器原生滚动
    console.log('⚠️ 未找到滚动容器，使用默认滚动')
    element.scrollIntoView({ 
      behavior: 'smooth', 
      block: 'start',
      inline: 'nearest'
    })
    activeSection.value = sectionId
  }
}

// 返回顶部
const scrollToTop = () => {
  const scrollContainer = getScrollContainer()
  if (scrollContainer) {
    scrollContainer.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }
}

// 滚动监听，自动更新当前激活的区块
const handleScroll = (event) => {
  const scrollContainer = event.target
  const scrollTop = scrollContainer.scrollTop
  const scrollHeight = scrollContainer.scrollHeight
  const clientHeight = scrollContainer.clientHeight
  
  // 控制返回顶部按钮显示（滚动超过300px时显示）
  showBackToTop.value = scrollTop > 300
  
  const sections = ['banner', 'nav-cards', 'services', 'projects', 'experience', 'products']
  const headerOffset = 150 // 偏移量，提前触发
  
  // 检查是否滚动到最底部
  const isAtBottom = scrollTop + clientHeight >= scrollHeight - 10 // 允许10px的误差
  
  if (isAtBottom) {
    // 滚动到底部时，激活最后一个section
    activeSection.value = sections[sections.length - 1]
    return
  }
  
  // 检查是否在顶部附近
  if (scrollTop <= headerOffset) {
    // 滚动到顶部时，激活第一个section
    activeSection.value = sections[0]
    return
  }
  
  // 正常滚动时，查找当前应该激活的section
  const scrollPosition = scrollTop + headerOffset
  
  for (let i = sections.length - 1; i >= 0; i--) {
    const section = document.getElementById(sections[i])
    if (section) {
      const containerRect = scrollContainer.getBoundingClientRect()
      const sectionRect = section.getBoundingClientRect()
      const sectionOffsetTop = scrollTop + (sectionRect.top - containerRect.top)
      
      if (scrollPosition >= sectionOffsetTop) {
        activeSection.value = sections[i]
        break
      }
    }
  }
}

// 页面挂载时添加滚动监听
onMounted(() => {
  loadBanners() // 加载轮播图数据
  loadCultureExperiences() // 文化体验
  loadFarmstay() // 农家乐
  loadHomestay() // 民宿
  loadAgriProducts() // 农特产品
  loadAttractions() // 特色周边
  loadGovProjects() // 政策对接
  startNavAutoplay()
  const scrollContainer = getScrollContainer()
  if (scrollContainer) {
    scrollContainer.addEventListener('scroll', handleScroll)
    // 初始化当前区块
    const event = { target: scrollContainer }
    handleScroll(event)
  }
})

// 页面卸载时移除滚动监听
onUnmounted(() => {
  const scrollContainer = getScrollContainer()
  if (scrollContainer) {
    scrollContainer.removeEventListener('scroll', handleScroll)
  }
  stopNavAutoplay()
})

const handleProductClick = (product) => {
  console.log('跳转到特色周边详情:', product.name, product.id)
  // 特色周边是从attractions接口获取的，应该跳转到景点详情页
  router.push(`/home/user/attractions/detail/${product.id}`)
}

const handleCaseClick = (caseItem) => {
  console.log('跳转到成功案例详情:', caseItem.title)
  // TODO: 跳转到案例详情页
  router.push(`/home/user/culture/case/${caseItem.id}`)
}

console.log('中等版本文旅页面数据初始化完成')
</script>

<style scoped>
.culture-platform-medium {
  position: relative;
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px 120px 60px 24px; /* 留出右侧导航空间并减少白边 */
  
  /* 添加装饰性背景圆圈 */
  &::before,
  &::after {
    content: none;
  }
}

/* 覆盖主页默认白色/渐变背景，去掉外层白框感 */
:deep(.home-layout .main-content) {
  background: #f5f7fa !important;
  padding: 0 !important;
}

/* 白色系英雄区 */
.hero-shell {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 28px;
  padding: 56px 80px;
  margin: 24px 80px 16px;
  background: #ffffff;
  border-radius: 32px;
  box-shadow: 0 18px 60px rgba(15, 23, 42, 0.08);
  position: relative;
  z-index: 2;
  overflow: hidden;
}

.hero-shell::after {
  content: '';
  position: absolute;
  right: -60px;
  top: -40px;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(98, 115, 255, 0.12) 0%, transparent 60%);
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 999px;
  background: #f3f6fb;
  color: #52607a;
  font-size: 13px;
  font-weight: 600;
  width: fit-content;
}

.hero-copy h1 {
  font-size: 34px;
  line-height: 1.3;
  color: #111827;
  margin: 0;
  letter-spacing: -0.5px;
}

.hero-subtitle {
  color: #1f2937;
  font-size: 14px;
  font-weight: 700;
  margin: 0;
}

.hero-success {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 10px;
  max-width: 720px;
}

.success-item {
  padding: 10px 12px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc, #eef2ff);
  border: 1px solid #e5e7eb;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.success-stat {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.stat-unit {
  font-size: 12px;
  color: #6b7280;
}

.success-title {
  font-size: 14px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 2px 0;
}

.success-desc {
  font-size: 12px;
  color: #4b5563;
  margin: 0;
  line-height: 1.4;
}

.hero-success {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  width: 100%;
}

.hero-partners {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  color: #374151;
  font-size: 13px;
  margin-top: 12px;
}

.partners-label {
  font-weight: 700;
  color: #111827;
}

.partners-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.partner-tag {
  padding: 6px 10px;
  border-radius: 999px;
  background: linear-gradient(135deg, #ecfdf3, #e0f2fe);
  color: #0f172a;
  border: 1px solid #d1fae5;
  font-size: 12px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.success-item {
  padding: 12px 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc, #eef2ff);
  border: 1px solid #e5e7eb;
  min-height: 120px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
}

.success-title {
  font-size: 14px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.success-desc {
  font-size: 12px;
  color: #4b5563;
  line-height: 1.5;
}

.success-stat {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.stat-unit {
  font-size: 12px;
  color: #6b7280;
}

.hero-carousel {
  position: relative;
}

.hero-slide {
  position: relative;
  border-radius: 0;
  overflow: hidden;
  height: 360px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-slide-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(17, 24, 39, 0.05) 0%, rgba(17, 24, 39, 0.7) 100%);
}

.hero-slide-content {
  position: relative;
  padding: 24px;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  text-align: center;
  opacity: 0.78;
}

.hero-slide-content h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.hero-slide-content p {
  margin: 0;
  color: rgba(255, 255, 255, 0.84);
  line-height: 1.6;
}

.slide-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.16);
  border-radius: 12px;
  width: fit-content;
  font-size: 12px;
}

.slide-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 10px;
  justify-content: center;
  opacity: 0.78;
}

.slide-tags span {
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.14);
  border-radius: 999px;
  font-size: 12px;
  color: #f8fafc;
  border: 1px solid rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.14);
}

/* 核心菜单轮播 */
.nav-rotator {
  padding: 8px 80px 32px;
}

.nav-card-track {
  position: relative;
  margin-top: 20px;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: visible;
}

.nav-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: pointer;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.06);
  transition: transform 0.35s ease, opacity 0.3s ease;
  width: 620px;
  max-width: 96vw;
  min-height: 240px;
  will-change: transform, opacity;
  align-items: center;
  position: absolute;
  top: 0;
  left: 50%;
}

.nav-card.active {
  border-color: #d1d5db;
}

.nav-card-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.nav-card-body h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #111827;
  text-align: center;
}

.nav-card-body p {
  margin: 0 0 14px 0;
  color: #4b5563;
  line-height: 1.6;
  font-size: 14px;
  text-align: center;
}

.nav-card-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
  text-align: center;
}

.nav-card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.nav-card-tag {
  padding: 6px 10px;
  background: #f3f4f6;
  color: #475569;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.nav-card-footer {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #16a34a;
  font-weight: 700;
  justify-content: center;
  margin-top: 10px;
  padding: 0 6px;
  width: auto;
  align-self: flex-end;
}

.nav-card-cta {
  background: linear-gradient(90deg, #16a34a 0%, #22c55e 50%, #16a34a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 14px;
  position: relative;
  display: inline-block;
}

/* 统一下方板块的白色卡片风格（默认），按需单独覆盖 */
.core-modules,
.service-categories,
.gov-cooperation,
.culture-experience,
.local-products {
  background: #ffffff;
  border-radius: 28px;
  padding: 40px 48px;
  margin: 28px 60px;
  box-shadow: 0 18px 48px rgba(15, 23, 42, 0.06);
  position: relative;
  z-index: 1;
}

/* 产业振兴区、政府合作、文化体验、特色周边模块取消卡片底色与阴影，仅保留内容 */
.service-categories,
.gov-cooperation,
.culture-experience,
.local-products {
  background: transparent;
  box-shadow: none;
  border-radius: 0;
  margin: 16px 0 24px;
  padding: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  margin: 0 0 20px 0;
  color: #0f172a;
}

.title-icon {
  font-size: 20px;
}


.service-categories .categories-tabs {
  background: transparent;
  border: none;
  border-radius: 0;
  padding: 0;
}

.service-grid {
  margin-top: 16px;
}

.service-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
}

.empty-hint {
  padding: 16px 0;
  color: #909399;
  text-align: center;
  font-size: 14px;
}

.gov-project-card,
.experience-card,
.product-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

.gov-project-card:hover {
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

/* 顶部横幅 */
.platform-banner {
  position: relative;
  padding: 80px 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  overflow: hidden;
  
  /* 添加斜切角效果（去掉方框感觉） */
  clip-path: polygon(0 0, 100% 0, 100% 85%, 0 100%);
  
  /* 添加动态背景效果 */
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
    background-size: 50px 50px;
    animation: backgroundMove 20s linear infinite;
  }
  
  /* 添加光晕效果 */
  &::after {
    content: '';
    position: absolute;
    top: -100px;
    right: -100px;
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    animation: floatGlow 8s ease-in-out infinite;
  }
}

/* 轮播图区域 */
.culture-carousel-section {
  padding: 0;
  margin: 0;
  position: relative;
  z-index: 1;
}

.culture-carousel {
  :deep(.el-carousel__container) {
    height: 480px;
  }
  
  :deep(.el-carousel__arrow) {
    width: 50px;
    height: 50px;
    font-size: 20px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    color: #667eea;
    border-radius: 50%;
    transition: all 0.3s;
    
    &:hover {
      background: #667eea;
      color: white;
      transform: scale(1.1);
    }
  }
  
  :deep(.el-carousel__indicators) {
    bottom: 30px;
    
    .el-carousel__indicator {
      .el-carousel__button {
        width: 40px;
        height: 4px;
        border-radius: 2px;
        background: rgba(255, 255, 255, 0.5);
        transition: all 0.3s;
      }
      
      &.is-active .el-carousel__button {
        width: 60px;
        background: white;
      }
    }
  }
}

.carousel-item-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.75) 0%, rgba(118, 75, 162, 0.75) 100%);
  z-index: 1;
}

.carousel-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  max-width: 900px;
  padding: 0 40px;
  animation: carouselContentFadeIn 0.8s ease-out;
}

.carousel-title {
  font-size: 56px;
  font-weight: 900;
  margin: 0 0 24px 0;
  line-height: 1.2;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  letter-spacing: 2px;
}

.carousel-subtitle {
  font-size: 20px;
  line-height: 1.8;
  margin: 0 0 32px 0;
  opacity: 0.95;
  font-weight: 400;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.carousel-tags {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.carousel-tag {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.35);
    border-color: rgba(255, 255, 255, 0.5);
    transform: translateY(-2px);
  }
}

.carousel-btn {
  font-size: 18px;
  padding: 16px 48px;
  background: white;
  color: #667eea;
  border: none;
  border-radius: 30px;
  font-weight: 700;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
  
  &:hover {
    background: #f0f2ff;
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
  }
  
  &:active {
    transform: translateY(-2px);
  }
}

@keyframes carouselContentFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.banner-content {
  position: relative;
  z-index: 1;
  max-width: 1600px;
  margin: 0 auto;
  text-align: center;
}

.banner-title {
  font-size: 56px;
  font-weight: 900;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
  animation: titleFadeIn 1s ease-out;
}

.title-icon {
  font-size: 64px;
  filter: drop-shadow(0 4px 12px rgba(255, 255, 255, 0.3));
}

.banner-subtitle {
  font-size: 22px;
  opacity: 0.95;
  margin: 0 0 32px 0;
  letter-spacing: 3px;
  font-weight: 300;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  animation: subtitleFadeIn 1s ease-out 0.3s both;
}

.banner-tags {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  animation: tagsFadeIn 1s ease-out 0.6s both;
}

.tag-item {
  padding: 10px 24px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border-radius: 25px;
  font-size: 15px;
  font-weight: 600;
  border: 2px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    background: rgba(255, 255, 255, 0.35);
    border-color: rgba(255, 255, 255, 0.6);
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  }
}

/* 核心板块 */
.core-modules,
.service-categories,
.gov-cooperation,
.local-products {
  position: relative;
  z-index: 1;
  padding: 60px 80px;
  max-width: 1600px;
  margin: 0 auto;
}

.section-title {
  position: relative;
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 40px 0;
  padding-bottom: 16px;
  
  /* 添加底部装饰线 */
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #cbd5e1 0%, #94a3b8 100%);
    border-radius: 2px;
  }
  
  .title-icon {
    font-size: 36px;
    filter: drop-shadow(0 2px 8px rgba(148, 163, 184, 0.3));
  }
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.module-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  transition: box-shadow 0.25s ease;
}

.module-card:hover {
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.module-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: #f5f5f5;
  color: #3b82f6;
}

.module-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px 0;
}

.module-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.module-count {
  font-size: 13px;
  color: #667eea;
  font-weight: 600;
}

/* 服务分类 */
.categories-tabs {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding: 0 8px;
  background: transparent;
  border-radius: 0;
  box-shadow: none;
  overflow-x: auto;
}

.category-tab {
  flex-shrink: 0;
  padding: 10px 4px 12px;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 16px;
  font-weight: 600;
  color: #94a3b8;
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  border-bottom: 2px solid transparent;
}

.category-tab:hover {
  color: #0f172a;
}

.category-tab.active {
  color: #0f172a;
  border-bottom-color: #0ea5e9;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.service-card {
  background: #ffffff;
  border-radius: 6px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.04);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.service-image {
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  background: #f5f7fa;
}

.service-cover-img {
  width: 100%;
  height: 100%;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: #f5f7fa;
  
  .el-icon {
    font-size: 32px;
    margin-bottom: 8px;
  }
  
  span {
    font-size: 12px;
  }
}

.service-card > * + * {
  padding-top: 8px;
  margin-top: 8px;
  border-top: 1px dashed #e2e8f0;
}

.service-card:hover {
  transform: none;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.service-title-wrap {
  display: flex;
  gap: 8px;
  align-items: center;
  flex: 1;
}

.service-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  flex: 1;
  line-height: 1.3;
}

.service-price-block {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.service-price {
  font-size: 22px;
  font-weight: 700;
  color: #f56c6c;
  letter-spacing: -0.5px;
}

.service-price-unit {
  font-size: 12px;
  color: #94a3b8;
}

.service-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  font-size: 13px;
  color: #475569;
  margin-bottom: 8px;
}

.service-location,
.service-rating,
.service-views {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #475569;
  
  .el-icon {
    font-size: 14px;
    color: #64748b;
  }
}

.service-rating {
  color: #334155;
  
  .el-icon {
    color: #f59e0b;
  }
}

.service-views {
  .el-icon {
    color: #64748b;
  }
}

.service-summary {
  font-size: 13px;
  color: #334155;
  line-height: 1.4;
  margin: 0;
}

.service-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin: 0;
}

.product-extra {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 6px;
  font-size: 12px;
  color: #475569;
  
  span {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    
    .el-icon {
      font-size: 14px;
      color: #64748b;
      flex-shrink: 0;
    }
  }
}

.tag-item {
  padding: 0;
  background: transparent;
  color: #0891b2;
  border-radius: 0;
  font-size: 12px;
  border: none;
  font-weight: 500;
  cursor: default;
  transition: none;
  
  &:hover {
    background: transparent;
    color: #0891b2;
    transform: none;
    box-shadow: none;
  }
}

.service-rating {
  font-size: 13px;
  color: #334155;
}

/* 政策对接 */
.gov-projects-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.gov-project-card {
  background: white;
  border-radius: 6px;
  padding: 20px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
  transition: box-shadow 0.25s ease;
  cursor: pointer;
}

.gov-project-card:hover {
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.project-header h3 {
  font-size: 17px;
  font-weight: 700;
  color: #303133;
  margin: 0;
  flex: 1;
}

.project-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  color: #475569;
}

.status-active {
  border-color: #67c23a;
  color: #3e8e1f;
  background: #f0f9eb;
}

.status-recruiting {
  border-color: #e6a23c;
  color: #a76505;
  background: #fdf3e6;
}

.project-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.gov-project-card > * + * {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e2e8f0;
}

.gov-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
  align-items: center;
  font-size: 12px;
  color: #334155;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0 8px;
  
  .el-icon {
    font-size: 14px;
    color: #64748b;
  }
  
  .meta-text {
    color: #334155;
  }
}

.meta-divider {
  color: #cbd5e1;
  padding: 0 4px;
}

.gov-tags {
  display: flex;
  gap: 0;
  flex-wrap: wrap;
  align-items: center;
}

.gov-tag {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  color: #475569;
  
  .tag-text {
    padding: 0 8px;
  }
  
  .tag-divider {
    color: #cbd5e1;
    padding: 0 4px;
  }
}

.gov-contact {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #475569;
}

.gov-goal-brief {
  font-size: 13px;
  color: #334155;
  line-height: 1.5;
}

/* 文化体验 */
.culture-experience {
  position: relative;
  z-index: 1;
  padding: 60px 80px;
  max-width: 1600px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 32px;
}

.section-subtitle {
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

.experience-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 32px;
}

.experience-card {
  background: #ffffff;
  border-radius: 0;
  overflow: hidden;
  border: 1px solid #edf0f5;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s ease;
  cursor: pointer;
}

.experience-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.06);
}

.experience-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.experience-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.experience-content {
  padding: 20px 20px 18px;
}

.experience-title {
  font-size: 17px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.experience-location {
  font-size: 14px;
  color: #606266;
  margin: 0 0 16px 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;

  .el-icon {
    color: #909399;
  }
}

.experience-features {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.feature-item {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;

  .el-icon {
    color: #f5a623;
  }
}

.experience-footer {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.experience-price {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.price-label {
  font-size: 12px;
  color: #909399;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #f56c6c;
}

.experience-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.experience-status.status-hot {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
}

.experience-status.status-featured {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

@media (max-width: 1200px) {
  .experience-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .experience-grid {
    grid-template-columns: 1fr;
  }
}

/* 特色周边 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 24px;
}

.product-card {
  background: #ffffff;
  border-radius: 0;
  border: 1px solid #eef0f3;
  padding: 20px;
  text-align: left;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.product-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.06);
}

.product-image {
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  background: #f5f7fa;
}

.product-cover-img {
  width: 100%;
  height: 100%;
}

.product-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 12px;
  background: #ffffff;
  color: #606266;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  max-width: 110px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.product-header h4 {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 10px 0;
  padding-right: 90px;
  min-height: 40px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}

.product-meta .meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.product-sales {
  font-size: 11px;
  color: #909399;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.product-price-unit {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 400;
}

/* 成功案例 */
.success-cases {
  position: relative;
  z-index: 1;
  padding: 60px 80px;
  max-width: 1600px;
  margin: 0 auto;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.case-card {
  background: white;
  border-radius: 32px;
  padding: 28px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s;
  cursor: pointer;
}

.case-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.case-card h3 {
  font-size: 17px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px 0;
}

.case-location {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px 0;
}

.case-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin: 0 0 20px 0;
}

.case-achievements {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.achievement-item {
  text-align: center;
}

.achievement-value {
  font-size: 24px;
  font-weight: 800;
  color: #667eea;
  margin-bottom: 4px;
}

.achievement-label {
  font-size: 12px;
  color: #909399;
}

/* 合作伙伴 */
.partners {
  position: relative;
  z-index: 1;
  padding: 60px 80px;
  max-width: 1600px;
  margin: 0 auto;
}

.partners-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.partner-card {
  background: white;
  border-radius: 28px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s;
}

.partner-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.partner-emoji {
  font-size: 48px;
  margin-bottom: 12px;
}

.partner-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .modules-grid,
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .experience-categories,
  .gov-projects-grid,
  .cases-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .partners-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .platform-banner {
    padding: 40px 20px;
  }
  
  .banner-title {
    font-size: 32px;
  }
  
  .core-modules,
  .service-categories,
  .culture-experience,
  .gov-cooperation,
  .success-cases,
  .local-products,
  .partners {
    padding: 40px 20px;
  }
  
  .modules-grid,
  .service-grid,
  .experience-categories,
  .gov-projects-grid,
  .cases-grid,
  .products-grid,
  .partners-grid {
    grid-template-columns: 1fr;
  }
}

/* 动画关键帧 */
@keyframes backgroundMove {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  100% {
    transform: translate(50px, 50px) rotate(360deg);
  }
}

@keyframes floatGlow {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-30px, -30px) scale(1.2);
    opacity: 0.8;
  }
}

@keyframes floatDecoration {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(20px, -20px) scale(1.1);
  }
  50% {
    transform: translate(-10px, -30px) scale(0.9);
  }
  75% {
    transform: translate(-20px, 10px) scale(1.05);
  }
}

@keyframes titleFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes subtitleFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-20px);
  }
  100% {
    opacity: 0.95;
    transform: translateY(0);
  }
}

@keyframes tagsFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-15px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-5px) rotate(5deg);
  }
  50% {
    transform: translateY(0) rotate(0deg);
  }
  75% {
    transform: translateY(-3px) rotate(-5deg);
  }
}


/* 右侧固定导航条 */
.side-nav {
  position: fixed;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1000;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  padding: 12px 0;
  overflow-y: auto;
  max-height: 90vh;
  border: 1px solid rgba(0, 0, 0, 0.06);
  
  /* 美化滚动条 */
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.03);
    border-radius: 2px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(0, 0, 0, 0.2);
    border-radius: 2px;
    
    &:hover {
      background: rgba(0, 0, 0, 0.3);
    }
  }
  
  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 70px;
    height: 70px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    
    /* 激活指示器 */
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 0;
      background: #333333;
      border-radius: 0 4px 4px 0;
      transition: height 0.3s ease;
    }
    
    .el-icon {
      font-size: 26px;
      color: #666666;
      margin-bottom: 6px;
      transition: all 0.3s ease;
    }
    
    .nav-text {
      font-size: 12px;
      color: #666666;
      transition: all 0.3s ease;
      white-space: nowrap;
    }
    
    &:hover {
      background: rgba(0, 0, 0, 0.04);
      
      .el-icon {
        color: #333333;
        animation: icon-bounce 0.6s ease;
      }
      
      .nav-text {
        color: #333333;
        font-weight: 600;
      }
    }
    
    &:active {
      background: rgba(0, 0, 0, 0.06);
    }
    
    /* 激活状态 */
    &.active {
      background: rgba(0, 0, 0, 0.05);
      
      &::before {
        height: 60%;
      }
      
      .el-icon {
        color: #000000;
        transform: scale(1.1);
      }
      
      .nav-text {
        color: #000000;
        font-weight: 700;
      }
    }
  }
}

@keyframes icon-bounce {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-5px) rotate(5deg);
  }
  50% {
    transform: translateY(0) rotate(0deg);
  }
  75% {
    transform: translateY(-3px) rotate(-5deg);
  }
}

/* 返回顶部按钮 */
.back-to-top {
  position: fixed;
  right: 30px;
  bottom: 100px;
  z-index: 999;
  width: 50px;
  height: 50px;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  color: #333333;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    background: #f5f5f5;
    border-color: rgba(0, 0, 0, 0.15);
    color: #000000;
    
    .el-icon {
      animation: bounce-up 0.6s ease;
    }
  }
  
  &:active {
    transform: translateY(-2px);
    background: #eeeeee;
  }
}

@keyframes bounce-up {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

/* 淡入淡出过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: scale(0.8);
}

.fade-leave-to {
  opacity: 0;
  transform: scale(0.8);
}

@media (max-width: 1200px) {
  .side-nav {
    display: none;
  }
  
  .back-to-top {
    right: 20px;
    bottom: 80px;
    width: 45px;
    height: 45px;
    
    .el-icon {
      font-size: 20px;
    }
  }
  
  .culture-platform-medium {
    padding-right: 0;
  }
}
</style>

