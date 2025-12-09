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
            <div class="service-header">
              <div class="service-title-wrap">
                <h3>{{ service.title }}</h3>
                <el-tag v-if="activeTab === 'products' && service.badge" size="small" type="success">{{ service.badge }}</el-tag>
              </div>
              <div class="service-price-block">
                <span class="service-price">¥{{ service.price }}</span>
                <span class="service-price-unit">
                  <template v-if="activeTab === 'products'">{{ service.unit || '/盒' }}</template>
                  <template v-else-if="activeTab === 'homestay'">/晚</template>
                  <template v-else>{{ service.unit || '/人' }}</template>
                </span>
              </div>
            </div>

            <div class="service-meta" :class="{ 'product-meta': activeTab === 'products' }">
              <span class="service-location">📍 {{ service.location }}</span>
              <span class="service-rating">⭐ {{ service.rating }}</span>
              <span v-if="(activeTab === 'products' && service.viewCount) || (activeTab !== 'products' && service.views)" class="service-views">
                👁️ {{ activeTab === 'products' ? service.viewCount : service.views }} 浏览
              </span>
            </div>

            <p v-if="service.summary" class="service-summary">{{ service.summary }}</p>

            <div v-if="activeTab === 'products'" class="product-extra">
              <span v-if="service.sales">🔥 月销 {{ service.sales }}</span>
              <span v-if="service.origin">📦 产地直发：{{ service.origin }}</span>
              <span v-if="service.shelfLife">⏱️ 保质期 {{ service.shelfLife }}</span>
            </div>
            <div v-else-if="activeTab === 'homestay'" class="product-extra">
              <span v-if="service.roomType">🏠 {{ service.roomType }}</span>
              <span v-if="service.capacity">👥 可住 {{ service.capacity }} 人</span>
              <span v-if="service.amenities?.length">🛎️ 配套：{{ service.amenities.slice(0, 3).join(' / ') }}</span>
            </div>
            <div v-else-if="activeTab === 'farmstay'" class="product-extra">
              <span v-if="service.contactPhone">📞 {{ service.contactPhone }}</span>
              <span v-if="service.views">🔥 热度 {{ service.views }}</span>
            </div>

            <div v-if="service.features?.length" class="service-features">
              <span v-for="feature in getFeatures(service)" :key="feature" class="feature-tag">
                {{ feature }}
              </span>
            </div>
            <div v-if="getHighlights(service).length" class="service-highlights">
              <span v-for="item in getHighlights(service)" :key="item" class="highlight-tag">#{{ item }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 政府合作项目 -->
    <div id="projects" class="gov-cooperation">
      <h2 class="section-title">
        <span class="title-icon">🤝</span>
        政府合作项目
      </h2>
      <div class="gov-projects-grid">
        <div v-for="project in govProjects" :key="project.id" class="gov-project-card" @click="handleProjectClick(project)">
          <div class="project-header">
            <h3>{{ project.title }}</h3>
            <span :class="['project-status', project.statusClass]">{{ project.statusText }}</span>
          </div>
          <p class="project-desc">{{ project.description }}</p>

          <div class="gov-meta">
            <div class="meta-chip">
              <el-icon><Location /></el-icon>
              <span>{{ project.location }}</span>
            </div>
            <div class="meta-chip">
              <el-icon><Calendar /></el-icon>
              <span>{{ project.startDate }}</span>
            </div>
            <div class="meta-chip">
              <el-icon><Coin /></el-icon>
              <span>投资 {{ project.investment }} 万</span>
            </div>
            <div class="meta-chip">
              <el-icon><UserFilled /></el-icon>
              <span>惠及 {{ project.beneficiaries }} 户</span>
            </div>
          </div>

          <div class="gov-tags">
            <span v-for="tag in project.tags" :key="tag" class="gov-tag">#{{ tag }}</span>
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
                <span class="price-value">¥{{ experience.price }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 特色周边 -->
    <div id="products" class="local-products">
      <h2 class="section-title">
        <span class="title-icon">🛍️</span>
        特色周边
      </h2>
      <div class="products-grid">
        <div v-for="product in localProducts" :key="product.id" class="product-card" @click="handleProductClick(product)">
          <div class="product-badge">{{ product.badge }}</div>
          <div class="product-header">
            <h4>{{ product.name }}</h4>
          </div>
          <div class="product-meta">
            <span class="meta-item">
              <el-icon><Location /></el-icon>
              {{ product.origin }}
            </span>
            <span class="meta-item">
              <el-icon><StarFilled /></el-icon>
              {{ product.rating }}
            </span>
          </div>
          <div class="product-footer">
            <span class="product-sales">已售 {{ product.sales }}</span>
            <span class="product-price">¥{{ product.price }}</span>
          </div>
        </div>
      </div>
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
        :class="{ 'active': activeSection === 'carousel' }"
        @click="scrollToSection('carousel')"
      >
        <el-icon><Picture /></el-icon>
        <span class="nav-text">精选推荐</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'modules' }"
        @click="scrollToSection('modules')"
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
        <span class="nav-text">合作项目</span>
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
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'cases' }"
        @click="scrollToSection('cases')"
      >
        <el-icon><Trophy /></el-icon>
        <span class="nav-text">成功案例</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ 'active': activeSection === 'partners' }"
        @click="scrollToSection('partners')"
      >
        <el-icon><UserFilled /></el-icon>
        <span class="nav-text">合作伙伴</span>
      </div>
    </div>

    <!-- 底部信息 -->
    <footer class="medium-footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#" target="_blank">关于我们</a>
          <span class="divider">|</span>
          <a href="#" target="_blank">服务条款</a>
          <span class="divider">|</span>
          <a href="#" target="_blank">隐私保护声明</a>
          <span class="divider">|</span>
          <a href="#" target="_blank">联系我们</a>
          <span class="divider">|</span>
          <a href="#" target="_blank">用户反馈</a>
        </div>
        <div class="footer-info">
          <p>{{ footerDescription }}</p>
          <p>本网站内容适合18岁以上用户使用，为了您的健康，请合理安排时间</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Star, Grid, Service, OfficeBuilding, Tickets, ShoppingBag, Trophy, UserFilled, Picture, Top,
  ArrowLeft, ArrowRight, Location, Calendar, Coin, Clock, StarFilled
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'
import * as cultureExperienceApi from '@/api/cultureExperience'

const router = useRouter()
const systemStore = useSystemStore()
const { footerDescription } = storeToRefs(systemStore)

console.log('✅ 中等版本文旅页面开始加载')

// 当前激活的区块
const activeSection = ref('banner')

// 是否显示返回顶部按钮
const showBackToTop = ref(false)

// 顶部数据展示
const heroStats = ref([
  { label: '合作文旅项目', value: '320+' },
  { label: '年度活跃对接', value: '1.2亿' },
  { label: '政府协同', value: '48个地区' }
])

const heroSuccess = ref([
  { title: '政策对接', desc: '申报直连与合规指导', value: '320+', unit: '项目/年' },
  { title: '产业振兴', desc: '规划落地与运营陪跑', value: '48', unit: '产业带' },
  { title: '文化体验', desc: '非遗体验与精品路线', value: '560+', unit: '体验' },
  { title: '特产上行', desc: '供应链与全渠道上架', value: '1.2K', unit: 'SKU' }
])
const heroPartners = computed(() => {
  return partners.value.slice(0, 4).map(p => p.name)
})

// 轮播图数据
const carouselBanners = ref([])

// 四大核心功能（与详情页对应）
const coreNavCards = ref([
  { title: '政策对接', desc: '政府窗口直连，政策解读、项目申报一站协同', meta: '立即对接', icon: '🏛️', accent: 'linear-gradient(135deg, #e3e8ff, #f6f7ff)', path: '/home/admin/culture', tags: ['政策解读', '项目申报', '合规指导'] },
  { title: '产业振兴', desc: '产业规划、运营陪跑、培训落地，帮你把路走稳', meta: '查看方案', icon: '📈', accent: 'linear-gradient(135deg, #e9fff7, #f8fffb)', path: '/home/user/culture/project', tags: ['规划落地', '运营陪跑', '培训赋能'] },
  { title: '文化体验', desc: '非遗工坊、沉浸演艺与精品路线，一键预约体验', meta: '去体验', icon: '🎭', accent: 'linear-gradient(135deg, #e8f5ff, #f7fbff)', path: '/home/user/culture/experience', tags: ['非遗工坊', '沉浸演艺', '精品路线'] },
  { title: '特产上行', desc: '产地直采+供应链，上架渠道与履约一体化', meta: '上架商品', icon: '🛍️', accent: 'linear-gradient(135deg, #fff4e8, #fffaf3)', path: '/home/user/culture/product', tags: ['供应链', '多渠道上架', '履约售后'] }
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
    title: '特产上行',
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
  farmstay: [
    {
      id: 1,
      title: '云雾山庄农家乐',
      location: '重庆市巴南区云雾山',
      origin: '云雾山脚下·自营农场',
      badge: '政府推荐',
      unit: '/人',
      features: ['采摘体验', '农家餐饮', '垂钓', '烧烤', '儿童乐园'],
      highlights: ['免费停车', '免费WiFi', '农家餐厅'],
      summary: '云雾山脚田园度假，农家菜+采摘+垂钓，亲子休闲好去处。',
      rating: 4.8,
      views: 1234,
      price: 88,
      contactPhone: '023-6688-1122'
    },
    {
      id: 2,
      title: '桃花源生态农庄',
      location: '重庆市江津区',
      origin: '江津·生态果园',
      badge: '热门',
      unit: '/人',
      features: ['有机蔬菜', '户外烧烤', '儿童乐园'],
      highlights: ['果园采摘', '烧烤场地', '亲子游乐'],
      summary: '有机果蔬+户外烧烤，家庭周末亲子游的热门选择。',
      rating: 4.9,
      views: 980,
      price: 128,
      contactPhone: '023-6688-1133'
    },
    {
      id: 3,
      title: '翠竹山居',
      location: '重庆市北碚区',
      origin: '北碚·竹林小院',
      badge: '新上架',
      unit: '/人',
      features: ['竹林漫步', '茶艺体验', '农耕体验'],
      highlights: ['茶室小憩', '竹林氧吧', '乡野露营'],
      summary: '竹林环绕的小院，主打茶艺体验与竹林徒步，清新安静。',
      rating: 4.7,
      views: 660,
      price: 98,
      contactPhone: '023-6688-1144'
    },
    {
      id: 4,
      title: '金色田园',
      location: '重庆市璧山区',
      origin: '璧山·稻田农庄',
      badge: '政府扶持',
      unit: '/人',
      features: ['稻田观光', '农事体验', '土鸡美食'],
      highlights: ['稻田露营', '土鸡柴火饭', '亲子插秧'],
      summary: '稻田景观和农事体验为亮点，土鸡柴火饭和乡野露营广受好评。',
      rating: 4.6,
      views: 520,
      price: 76,
      contactPhone: '023-6688-1155'
    }
  ],
  homestay: [
    {
      id: 1,
      title: '山水间·云舍',
      location: '重庆市武隆区',
      badge: '精品民宿',
      roomType: '景观大床房 · 1室1厅1卫',
      capacity: 2,
      amenities: ['空调', 'WiFi', '观景阳台', '早餐', '停车位'],
      highlightTags: ['峡谷观景', '管家服务', '私密小院'],
      highlights: '位于武隆峡谷旁，观景露台与私密小院，管家式服务与双早餐。',
      features: ['景观房', '管家服务', '双早套餐', '私人影院'],
      summary: '峡谷景观房与贴心管家服务，适合度假小憩。',
      rating: 4.9,
      views: 236,
      price: 368,
      cover: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80'
    },
    {
      id: 2,
      title: '老街记忆客栈',
      location: '重庆市磁器口',
      badge: '文化特色',
      roomType: '双人房 · 1室1卫',
      capacity: 2,
      amenities: ['早餐', 'WiFi', '老街步行可达'],
      highlightTags: ['古镇风情', '传统装饰'],
      highlights: '置身古镇的慢生活，木质小院与地道早餐相得益彰。',
      features: ['古镇风情', '传统装饰', '特色早餐'],
      summary: '靠近磁器口老街，传统风情与特色早餐。',
      rating: 4.8,
      views: 310,
      price: 228,
      cover: 'https://images.unsplash.com/photo-1496417263034-38ec4f0b665a?auto=format&fit=crop&w=1200&q=80'
    },
    {
      id: 3,
      title: '竹海深处',
      location: '重庆市永川区',
      badge: '生态体验',
      roomType: '庭院房 · 1室1厅',
      capacity: 3,
      amenities: ['早餐', '停车位', '竹林步道'],
      highlightTags: ['竹林氧吧', '静谧环境'],
      highlights: '竹海氧吧里的安静民宿，主打林间漫步与清新空气。',
      features: ['竹海环绕', '天然氧吧', '静谧环境'],
      summary: '竹林环绕，空气清新，适合放松发呆。',
      rating: 4.7,
      views: 280,
      price: 198,
      cover: 'https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=1200&q=80'
    },
    {
      id: 4,
      title: '江畔小筑',
      location: '重庆市长寿区',
      badge: '江景房',
      roomType: '江景大床房 · 1室1卫',
      capacity: 2,
      amenities: ['观景露台', '早餐', 'WiFi'],
      highlightTags: ['江景露台', '江鲜美食'],
      highlights: '江景露台与江鲜美食的组合，适合周末放松。',
      features: ['临江观景', '江鲜美食', '垂钓平台'],
      summary: '临江观景，江鲜美食，周末度假的自在选择。',
      rating: 4.8,
      views: 360,
      price: 268,
      cover: 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1200&q=80'
    }
  ],
  products: [
    {
      id: 1,
      title: '云雾绿茶礼盒',
      location: '重庆市忠县',
      origin: '重庆忠县·高山茶园',
      badge: '当季新茶',
      unit: '/盒',
      features: ['春茶新采', '源头直发', '可定制'],
      highlights: ['单芽茶', '溯源质检', '礼盒装'],
      summary: '900米云雾茶园春采单芽，礼盒装支持定制，产地直发。',
      rating: 4.8,
      price: 168,
      sales: 1560,
      viewCount: 3086,
      shelfLife: '18个月',
      cover: 'https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?auto=format&fit=crop&w=1200&q=80'
    },
    {
      id: 2,
      title: '竹编工艺套装',
      location: '重庆市江津区',
      origin: '江津·非遗工坊',
      badge: '非遗好物',
      unit: '/套',
      features: ['纯手工', '环保材质', '礼赠优选'],
      highlights: ['非遗师傅', '竹料可溯', '可定制'],
      summary: '手工竹编茶席与收纳盒组合，兼具实用与非遗美学，支持定制。',
      rating: 4.7,
      price: 198,
      sales: 980,
      viewCount: 2230,
      shelfLife: '可长期使用',
      cover: 'https://images.unsplash.com/photo-1505764706515-aa95265c5abc?auto=format&fit=crop&w=1200&q=80'
    },
    {
      id: 3,
      title: '山地蜂蜜',
      location: '重庆市酉阳县',
      origin: '酉阳·高山林场',
      badge: '生态养殖',
      unit: '/瓶',
      features: ['原生态', '溯源可查', '营养丰富'],
      highlights: ['高山放养', '低温灌装', '花源可溯'],
      summary: '高山放养蜂场直供，花源清晰可溯，低温灌装保留活性，口感清甜。',
      rating: 4.9,
      price: 128,
      sales: 1860,
      viewCount: 4120,
      shelfLife: '24个月',
      cover: 'https://images.unsplash.com/photo-1505253758473-96b7015fcd40?auto=format&fit=crop&w=1200&q=80'
    },
    {
      id: 4,
      title: '土鸡蛋礼盒',
      location: '重庆市武隆区',
      origin: '武隆·山地农场',
      badge: '农场直供',
      unit: '/盒',
      features: ['散养土鸡', '每日新鲜', '蛋香浓郁'],
      highlights: ['日捡日发', '粗粮喂养', '礼盒装'],
      summary: '散养土鸡每日新鲜收集，蛋香浓郁，礼盒装家庭早餐优选。',
      rating: 4.6,
      price: 76,
      sales: 2320,
      viewCount: 3650,
      shelfLife: '35天',
      cover: 'https://images.unsplash.com/photo-1502741338009-cac2772e18bc?auto=format&fit=crop&w=1200&q=80'
    }
  ]
})

const currentServices = computed(() => {
  return services.value[activeTab.value] || services.value.farmstay
})

// 政府合作项目
const govProjects = ref([
  {
    id: 1,
    title: '巴南区乡村振兴示范项目',
    location: '重庆市巴南区',
    description: '以农旅融合为核心，打造集观光、体验、度假为一体的乡村旅游综合体，带动当地农民增收致富。',
    tags: ['乡村振兴', '产业融合', '就业扶持'],
    startDate: '2024-03',
    investment: 5000,
    beneficiaries: 320,
    statusText: '进行中',
    statusClass: 'status-active',
    contactName: '张经理',
    contactPhone: '023-6688-3344',
    goalBrief: '目标：产业链年产值8000万+，就业500人+，示范乡村样板。'
  },
  {
    id: 2,
    title: '武隆区文化遗产保护与开发',
    location: '重庆市武隆区',
    description: '保护和传承当地非物质文化遗产，结合旅游开发，实现文化传承与经济发展双赢。',
    tags: ['文化传承', '非遗保护', '旅游开发'],
    startDate: '2024-01',
    investment: 3200,
    beneficiaries: 180,
    statusText: '招募中',
    statusClass: 'status-recruiting',
    contactName: '李主任',
    contactPhone: '023-6688-5566',
    goalBrief: '目标：完成非遗场馆建设与运营，年接待游客30万+。'
  },
  {
    id: 3,
    title: '江津区特色农产品推广计划',
    location: '重庆市江津区',
    description: '建立线上线下一体化销售平台，帮助当地农产品拓展销售渠道，提升品牌影响力。',
    tags: ['农产品', '电商扶持', '品牌打造'],
    startDate: '2024-02',
    investment: 1500,
    beneficiaries: 450,
    statusText: '进行中',
    statusClass: 'status-active',
    contactName: '王老师',
    contactPhone: '023-6688-7788',
    goalBrief: '目标：打造区域公用品牌，新增线上销售额3000万+。'
  }
])

// 文化体验项目（与管理端数据结构对应）
const cultureExperiences = ref([])

const fallbackExperiences = [
  {
    id: 1,
    name: '蜀绣体验工坊',
    categoryName: '非遗体验',
    location: '成都·锦里',
    price: 168,
    duration: '约2小时',
    rating: 4.8,
    status: 'hot',
    image: 'https://images.unsplash.com/photo-1611780876184-c2c63e8f51c6?w=800&h=600&fit=crop'
  },
  {
    id: 2,
    name: '川渝火锅体验馆',
    categoryName: '美食文化',
    location: '重庆·解放碑',
    price: 128,
    duration: '约3小时',
    rating: 4.9,
    status: 'hot',
    image: 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800&h=600&fit=crop'
  },
  {
    id: 3,
    name: '民俗山峡走读',
    categoryName: '历史探访',
    location: '重庆·磁器口',
    price: 88,
    duration: '半天',
    rating: 4.7,
    status: 'featured',
    image: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=600&fit=crop'
  },
  {
    id: 4,
    name: '竹编手工课',
    categoryName: '手工制作',
    location: '重庆·梁平',
    price: 128,
    duration: '约3小时',
    rating: 4.7,
    status: 'featured',
    image: 'https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=800&h=600&fit=crop'
  },
  {
    id: 5,
    name: '古镇文化游',
    categoryName: '历史探访',
    location: '重庆·磁器口',
    price: 88,
    duration: '半天',
    rating: 4.6,
    status: 'featured',
    image: 'https://images.unsplash.com/photo-1548013146-72479768bada?w=800&h=600&fit=crop'
  },
  {
    id: 6,
    name: '土家织锦技艺',
    categoryName: '非遗体验',
    location: '重庆·酉阳',
    price: 158,
    duration: '约2.5小时',
    rating: 4.8,
    status: 'hot',
    image: 'https://images.unsplash.com/photo-1515377905703-c4788e51af15?w=800&h=600&fit=crop'
  }
]

const normalizeImages = (images) => {
  if (!images) return []
  try {
    const parsed = Array.isArray(images) ? images : JSON.parse(images)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return typeof images === 'string' ? [images] : []
  }
}

const loadCultureExperiences = async () => {
  try {
    const res = await cultureExperienceApi.getUserExperienceList({ page: 1, size: 9 })
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
      console.warn('文化体验列表为空，使用本地示例数据')
      cultureExperiences.value = fallbackExperiences
    }
  } catch (error) {
    console.error('加载文化体验列表失败', error)
    cultureExperiences.value = fallbackExperiences
  }
}

// 特色周边
const localProducts = ref([
  { id: 1, name: '巴南银针茶', origin: '重庆巴南', badge: '地理标志', price: 128, sales: 2356, rating: 4.9 },
  { id: 2, name: '江津花椒', origin: '重庆江津', badge: '原产地直供', price: 68, sales: 5678, rating: 4.8 },
  { id: 3, name: '手工竹编', origin: '重庆梁平', badge: '非遗工艺', price: 158, sales: 892, rating: 4.7 },
  { id: 4, name: '土家织锦', origin: '重庆酉阳', badge: '文创精品', price: 368, sales: 456, rating: 4.9 },
  { id: 5, name: '涪陵榨菜', origin: '重庆涪陵', badge: '老字号', price: 45, sales: 12356, rating: 4.6 },
  { id: 6, name: '石柱蜂蜜', origin: '重庆石柱', badge: '天然有机', price: 98, sales: 3456, rating: 4.8 }
])

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

const getHighlights = (service) => {
  if (activeTab.value === 'homestay') {
    return Array.isArray(service.highlightTags) ? service.highlightTags : []
  }
  const list = Array.isArray(service.highlights) ? service.highlights : []
  return list.slice(0, 4)
}

const getFeatures = (service) => {
  const list = Array.isArray(service.features) ? service.features : []
  return list.slice(0, 4)
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
  const element = document.getElementById(sectionId)
  const scrollContainer = getScrollContainer()
  
  if (element && scrollContainer) {
    const headerOffset = 24 // 调整偏移量
    const elementPosition = element.offsetTop
    const offsetPosition = elementPosition - headerOffset
    
    scrollContainer.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
    
    // 手动设置激活状态
    activeSection.value = sectionId
  } else {
    console.warn('未找到元素或滚动容器:', sectionId)
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
  
  // 控制返回顶部按钮显示（滚动超过300px时显示）
  showBackToTop.value = scrollTop > 300
  
  const sections = ['banner', 'carousel', 'modules', 'services', 'projects', 'experience', 'products', 'cases', 'partners']
  const scrollPosition = scrollTop + 150 // 偏移量，提前触发
  
  for (let i = sections.length - 1; i >= 0; i--) {
    const section = document.getElementById(sections[i])
    if (section) {
      const offsetTop = section.offsetTop
      if (scrollPosition >= offsetTop) {
        activeSection.value = sections[i]
        break
      }
    }
  }
}

// 页面挂载时添加滚动监听
onMounted(() => {
  loadBanners() // 加载轮播图数据
  loadCultureExperiences() // 加载文化体验列表（含分类标签）
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
  console.log('跳转到产品详情:', product.name)
  // TODO: 跳转到产品详情页
  router.push(`/home/user/culture/product/${product.id}`)
}

const handleCaseClick = (caseItem) => {
  console.log('跳转到成功案例详情:', caseItem.title)
  // TODO: 跳转到案例详情页
  router.push(`/home/user/culture/case/${caseItem.id}`)
}

console.log('✅ 中等版本文旅页面数据初始化完成')
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

.gov-project-card,
.experience-card,
.product-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

.gov-project-card:hover,
.experience-card:hover,
.product-card:hover {
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
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
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
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
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

.service-location {
  font-size: 13px;
  color: #475569;
}

.service-summary {
  font-size: 13px;
  color: #334155;
  line-height: 1.4;
  margin: 0;
}

.service-features,
.service-highlights {
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
}

.feature-tag {
  padding: 4px 10px;
  background: #f1f5f9;
  color: #0f172a;
  border-radius: 10px;
  font-size: 12px;
  border: 1px solid #e2e8f0;
}

.highlight-tag {
  padding: 0;
  background: transparent;
  color: #0891b2;
  border-radius: 0;
  font-size: 12px;
  border: none;
}

.service-rating {
  font-size: 13px;
  color: #334155;
}

/* 政府合作项目 */
.gov-projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
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
  gap: 8px;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background: #f8fafc;
  color: #334155;
  border-radius: 6px;
  font-size: 12px;
  border: 1px solid #e5e7eb;
}

.gov-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.gov-tag {
  font-size: 12px;
  color: #475569;
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
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.experience-card {
  background: #ffffff;
  border-radius: 0;
  overflow: hidden;
  border: 1px solid #edf0f5;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.experience-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.08);
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
  transition: transform 0.4s;
}

.experience-card:hover .experience-image img {
  transform: scale(1.1);
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
  grid-template-columns: repeat(5, 1fr);
  gap: 24px;
}

.product-card {
  background: #ffffff;
  border-radius: 0;
  border: 1px solid #eef0f3;
  padding: 20px;
  text-align: left;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
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

/* 底部信息 */
.medium-footer {
  background: linear-gradient(180deg, #f5f7fa 0%, #e8eef5 100%);
  color: #5a5e66;
  padding: 40px 40px 30px;
  margin-top: 60px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(to right, transparent 0%, #d3dce6 20%, #d3dce6 80%, transparent 100%);
  }
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .footer-links {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    gap: 8px;
  margin-bottom: 28px;
    font-size: 12px;
    
    a {
      color: #5a5e66;
      text-decoration: none;
      transition: color 0.3s;
      
      &:hover {
        color: #667eea;
      }
    }
    
    .divider {
      color: #b4bccc;
      margin: 0 4px;
    }
  }
  
  .footer-info {
    text-align: center;
    font-size: 11px;
    line-height: 1.8;
    color: #878d99;
    
    p {
      margin: 4px 0;
    }
  }
}

@media (max-width: 768px) {
  .medium-footer {
    padding: 30px 20px 20px;
    
    .footer-links {
      font-size: 11px;
      gap: 6px;
    }
    
    .footer-info {
      font-size: 10px;
    }
  }
}

/* 右侧固定导航条 */
.side-nav {
  position: fixed;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1000;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
  padding: 12px 0;
  overflow-y: auto;
  max-height: 90vh;
  
  /* 美化滚动条 */
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 2px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
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
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 0 4px 4px 0;
      transition: height 0.3s ease;
    }
    
    .el-icon {
      font-size: 26px;
      color: #5a9fd4;
      margin-bottom: 6px;
      transition: all 0.3s ease;
    }
    
    .nav-text {
      font-size: 12px;
      color: #666;
      transition: all 0.3s ease;
      white-space: nowrap;
    }
    
    &:hover {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
      
      .el-icon {
        color: #667eea;
        animation: icon-bounce 0.6s ease;
      }
      
      .nav-text {
        color: #667eea;
        font-weight: 600;
      }
    }
    
    &:active {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
    }
    
    /* 激活状态 */
    &.active {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.12) 100%);
      
      &::before {
        height: 60%;
      }
      
      .el-icon {
        color: #667eea;
        transform: scale(1.1);
      }
      
      .nav-text {
        color: #667eea;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  color: white;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(102, 126, 234, 0.6);
    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
    
    .el-icon {
      animation: bounce-up 0.6s ease;
    }
  }
  
  &:active {
    transform: translateY(-2px);
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

