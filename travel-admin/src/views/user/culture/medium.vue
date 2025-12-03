<template>
  <div class="culture-platform-medium">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 顶部横幅 -->
    <div id="banner" class="platform-banner">
      <div class="banner-content">
        <h1 class="banner-title">
          <span class="title-icon">🏛️</span>
          智慧文旅对接平台
        </h1>
        <p class="banner-subtitle">助力乡村振兴 · 传承文化精髓 · 共创美好未来</p>
        <div class="banner-tags">
          <span class="tag-item">政府支持</span>
          <span class="tag-item">文化传承</span>
          <span class="tag-item">产业振兴</span>
          <span class="tag-item">乡村发展</span>
        </div>
      </div>
    </div>

    <!-- 轮播图 -->
    <div id="carousel" class="culture-carousel-section" v-if="carouselBanners.length > 0">
      <el-carousel :interval="5000" arrow="always" height="480px" class="culture-carousel">
        <el-carousel-item v-for="(banner, index) in carouselBanners" :key="banner.id || index">
          <div class="carousel-item-wrapper" :style="{ backgroundImage: `url(${banner.image})` }">
            <div class="carousel-overlay"></div>
            <div class="carousel-content">
              <h2 class="carousel-title">{{ banner.title }}</h2>
              <p class="carousel-subtitle">{{ banner.subtitle }}</p>
              <div class="carousel-tags">
                <span v-for="tag in banner.tags" :key="tag" class="carousel-tag">{{ tag }}</span>
              </div>
              <el-button type="primary" size="large" class="carousel-btn" @click="handleBannerClick(banner)">
                {{ banner.btnText }}
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 核心板块 -->
    <div id="modules" class="core-modules">
      <h2 class="section-title">
        <span class="title-icon">🎯</span>
        核心板块
      </h2>
      <div class="modules-grid">
        <div v-for="(module, index) in coreModules" :key="index" class="module-card">
          <div class="module-icon">{{ module.emoji }}</div>
          <h3 class="module-title">{{ module.title }}</h3>
          <p class="module-desc">{{ module.description }}</p>
          <div class="module-count">{{ module.count }}+项目</div>
        </div>
      </div>
    </div>

    <!-- 服务分类 -->
    <div id="services" class="service-categories">
      <h2 class="section-title">
        <span class="title-icon">🎨</span>
        服务分类
      </h2>
      <div class="categories-tabs">
        <div 
          v-for="tab in serviceTabs" 
          :key="tab.value"
          :class="['category-tab', { 'active': activeTab === tab.value }]"
          @click="activeTab = tab.value"
        >
          <span>{{ tab.emoji }} {{ tab.label }}</span>
          <span class="tab-count">{{ tab.count }}</span>
        </div>
      </div>
      
      <div class="service-content">
        <div class="service-grid">
          <div v-for="service in currentServices" :key="service.id" class="service-card" @click="handleServiceClick(service)">
            <div class="service-header">
              <h3>{{ service.title }}</h3>
              <span class="service-badge">{{ service.badge }}</span>
            </div>
            <p class="service-location">📍 {{ service.location }}</p>
            <div class="service-features">
              <span v-for="feature in service.features" :key="feature" class="feature-tag">
                {{ feature }}
              </span>
            </div>
            <div class="service-footer">
              <span class="service-rating">⭐ {{ service.rating }}</span>
              <span class="service-price">¥{{ service.price }}</span>
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
          <div class="project-tags">
            <span v-for="tag in project.tags" :key="tag" class="project-tag">#{{ tag }}</span>
          </div>
          <div class="project-info">
            <span>📅 {{ project.startDate }}</span>
            <span>💰 投资{{ project.investment }}万</span>
            <span>👥 惠及{{ project.beneficiaries }}户</span>
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
            <div class="experience-badge">{{ experience.categoryName }}</div>
          </div>
          <div class="experience-content">
            <h3 class="experience-title">{{ experience.name }}</h3>
            <p class="experience-location">📍 {{ experience.location }}</p>
            <div class="experience-features">
              <span class="feature-item">⏱️ {{ experience.duration }}</span>
              <span class="feature-item">⭐ {{ experience.rating }}</span>
            </div>
            <div class="experience-footer">
              <span class="experience-price">
                <span class="price-label">体验价</span>
                <span class="price-value">¥{{ experience.price }}</span>
              </span>
              <span class="experience-status" :class="`status-${experience.status}`">
                {{ experience.status === 'hot' ? '🔥 热门' : '✨ 精选' }}
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
          <h4>{{ product.name }}</h4>
          <p class="product-origin">📍 {{ product.origin }}</p>
          <div class="product-footer">
            <span class="product-sales">已售{{ product.sales }}</span>
            <span class="product-price">¥{{ product.price }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 成功案例 -->
    <div id="cases" class="success-cases">
      <h2 class="section-title">
        <span class="title-icon">🏆</span>
        成功案例
      </h2>
      <div class="cases-grid">
        <div v-for="caseItem in successCases" :key="caseItem.id" class="case-card" @click="handleCaseClick(caseItem)">
          <h3>{{ caseItem.title }}</h3>
          <p class="case-location">📍 {{ caseItem.location }}</p>
          <p class="case-description">{{ caseItem.description }}</p>
          <div class="case-achievements">
            <div class="achievement-item">
              <div class="achievement-value">{{ caseItem.revenue }}万</div>
              <div class="achievement-label">年收入</div>
            </div>
            <div class="achievement-item">
              <div class="achievement-value">{{ caseItem.jobs }}+</div>
              <div class="achievement-label">就业岗位</div>
            </div>
            <div class="achievement-item">
              <div class="achievement-value">{{ caseItem.visitors }}万</div>
              <div class="achievement-label">年游客量</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 合作伙伴 -->
    <div id="partners" class="partners">
      <h2 class="section-title">
        <span class="title-icon">👥</span>
        合作伙伴
      </h2>
      <div class="partners-grid">
        <div v-for="partner in partners" :key="partner.id" class="partner-card">
          <div class="partner-emoji">{{ partner.emoji }}</div>
          <p class="partner-name">{{ partner.name }}</p>
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
        <span class="nav-text">服务分类</span>
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
  Star, Grid, Service, OfficeBuilding, Tickets, ShoppingBag, Trophy, UserFilled, Picture, Top
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'

const router = useRouter()
const systemStore = useSystemStore()
const { footerDescription } = storeToRefs(systemStore)

console.log('✅ 中等版本文旅页面开始加载')

// 当前激活的区块
const activeSection = ref('banner')

// 是否显示返回顶部按钮
const showBackToTop = ref(false)

// 轮播图数据
const carouselBanners = ref([])

// 加载轮播图数据（从后端获取）
const loadBanners = async () => {
  try {
    const response = await request.get('/user/home/banners', {
      params: { position: 'CULTURE' }
    })
    if (response.code === 200 && response.data && response.data.length > 0) {
      // 转换后端数据格式为前端需要的格式
      carouselBanners.value = response.data.map(banner => ({
        id: banner.id,
        title: banner.title || '',
        subtitle: banner.subtitle || '',
        tags: banner.subtitle ? banner.subtitle.split('、').slice(0, 3) : ['文旅项目'],
        btnText: '了解详情',
        image: banner.imageUrl || banner.image,
        link: getBannerLink(banner.linkType, banner.linkValue),
        linkType: banner.linkType || 0,
        linkValue: banner.linkValue || ''
      }))
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
    emoji: '🏛️',
    title: '政府对接',
    description: '对接当地政府，政策支持，资源整合',
    count: 28
  },
  {
    emoji: '📈',
    title: '产业振兴',
    description: '旅游+农业，助力乡村产业升级',
    count: 45
  },
  {
    emoji: '🎨',
    title: '文化传承',
    description: '保护传统文化，活化非遗项目',
    count: 63
  },
  {
    emoji: '🛍️',
    title: '特产推广',
    description: '地方特产上线，拓展销售渠道',
    count: 156
  }
])

// 服务标签页
const serviceTabs = ref([
  { value: 'farmstay', label: '农家乐', emoji: '🏠', count: 48 },
  { value: 'homestay', label: '特色民宿', emoji: '☕', count: 32 },
  { value: 'products', label: '农特产品', emoji: '🛍️', count: 156 },
  { value: 'experience', label: '文化体验', emoji: '🎭', count: 24 },
  { value: 'food', label: '地道美食', emoji: '🍜', count: 67 }
])

const activeTab = ref('farmstay')

// 服务数据
const services = ref({
  farmstay: [
    { id: 1, title: '云雾山庄农家乐', location: '重庆市巴南区', badge: '政府推荐', features: ['采摘体验', '农家餐饮', '垂钓'], rating: 4.8, price: 88 },
    { id: 2, title: '桃花源生态农庄', location: '重庆市江津区', badge: '热门', features: ['有机蔬菜', '户外烧烤', '儿童乐园'], rating: 4.9, price: 128 },
    { id: 3, title: '翠竹山居', location: '重庆市北碚区', badge: '新上架', features: ['竹林漫步', '茶艺体验', '农耕体验'], rating: 4.7, price: 98 },
    { id: 4, title: '金色田园', location: '重庆市璧山区', badge: '政府扶持', features: ['稻田观光', '农事体验', '土鸡美食'], rating: 4.6, price: 76 }
  ],
  homestay: [
    { id: 1, title: '山水间·云舍', location: '重庆市武隆区', badge: '精品民宿', features: ['景观房', '管家服务', '私人影院'], rating: 4.9, price: 368 },
    { id: 2, title: '老街记忆客栈', location: '重庆市磁器口', badge: '文化特色', features: ['古镇风情', '传统装饰', '特色早餐'], rating: 4.8, price: 228 },
    { id: 3, title: '竹海深处', location: '重庆市永川区', badge: '生态体验', features: ['竹海环绕', '天然氧吧', '静谧环境'], rating: 4.7, price: 198 },
    { id: 4, title: '江畔小筑', location: '重庆市长寿区', badge: '江景房', features: ['临江观景', '江鲜美食', '垂钓平台'], rating: 4.8, price: 268 }
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
    description: '以农旅融合为核心，打造集观光、体验、度假为一体的乡村旅游综合体，带动当地农民增收致富。',
    tags: ['乡村振兴', '产业融合', '就业扶持'],
    startDate: '2024-03',
    investment: 5000,
    beneficiaries: 320,
    statusText: '进行中',
    statusClass: 'status-active'
  },
  {
    id: 2,
    title: '武隆区文化遗产保护与开发',
    description: '保护和传承当地非物质文化遗产，结合旅游开发，实现文化传承与经济发展双赢。',
    tags: ['文化传承', '非遗保护', '旅游开发'],
    startDate: '2024-01',
    investment: 3200,
    beneficiaries: 180,
    statusText: '招募中',
    statusClass: 'status-recruiting'
  },
  {
    id: 3,
    title: '江津区特色农产品推广计划',
    description: '建立线上线下一体化销售平台，帮助当地农产品拓展销售渠道，提升品牌影响力。',
    tags: ['农产品', '电商扶持', '品牌打造'],
    startDate: '2024-02',
    investment: 1500,
    beneficiaries: 450,
    statusText: '进行中',
    statusClass: 'status-active'
  }
])

// 文化体验项目（与管理端数据结构对应）
const cultureExperiences = ref([
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
    name: '重庆民俗山峡美誉',
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
])

// 特色周边
const localProducts = ref([
  { id: 1, name: '巴南银针茶', origin: '重庆巴南', badge: '地理标志', price: 128, sales: 2356 },
  { id: 2, name: '江津花椒', origin: '重庆江津', badge: '原产地直供', price: 68, sales: 5678 },
  { id: 3, name: '手工竹编', origin: '重庆梁平', badge: '非遗工艺', price: 158, sales: 892 },
  { id: 4, name: '土家织锦', origin: '重庆酉阳', badge: '文创精品', price: 368, sales: 456 },
  { id: 5, name: '涪陵榨菜', origin: '重庆涪陵', badge: '老字号', price: 45, sales: 12356 },
  { id: 6, name: '石柱蜂蜜', origin: '重庆石柱', badge: '天然有机', price: 98, sales: 3456 }
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
  { id: 8, emoji: '🚗', name: '滴滴出行' }
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
    if (banner.linkType === 2) {
      // 外部链接，新窗口打开
      window.open(banner.link, '_blank')
    } else {
      // 内部链接
      router.push(banner.link)
    }
  } else if (banner.linkType && banner.linkValue) {
    // 根据链接类型跳转
    const link = getBannerLink(banner.linkType, banner.linkValue)
    if (link) {
      if (banner.linkType === 2) {
        window.open(link, '_blank')
      } else {
        router.push(link)
      }
    }
  }
}

const handleServiceClick = (service) => {
  console.log('跳转到服务详情:', service.title)
  // TODO: 跳转到服务详情页
  router.push(`/home/user/culture/service/${service.id}`)
}

const handleProjectClick = (project) => {
  console.log('跳转到政府项目详情:', project.title)
  // TODO: 跳转到政府项目详情页
  router.push(`/home/user/culture/project/${project.id}`)
}

const handleCategoryClick = (category) => {
  console.log('跳转到文化体验详情:', category.name)
  // 跳转到文化体验详情页
  router.push(`/home/user/culture/detail/${category.id}`)
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
  background: linear-gradient(180deg, #f5f7fa 0%, #e8eef5 100%);
  padding-bottom: 60px;
  padding-right: 120px; /* 为右侧导航条留出空间 */
  
  /* 添加装饰性背景圆圈 */
  &::before {
    content: '';
    position: fixed;
    top: 10%;
    left: 5%;
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(102, 126, 234, 0.08) 0%, transparent 70%);
    border-radius: 50%;
    animation: floatDecoration 15s ease-in-out infinite;
    pointer-events: none;
    z-index: 0;
  }
  
  &::after {
    content: '';
    position: fixed;
    bottom: 15%;
    right: 8%;
    width: 250px;
    height: 250px;
    background: radial-gradient(circle, rgba(118, 75, 162, 0.08) 0%, transparent 70%);
    border-radius: 50%;
    animation: floatDecoration 18s ease-in-out infinite reverse;
    pointer-events: none;
    z-index: 0;
  }
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
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
  }
  
  .title-icon {
    font-size: 36px;
    filter: drop-shadow(0 2px 8px rgba(102, 126, 234, 0.3));
  }
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.module-card {
  background: white;
  border-radius: 32px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s;
}

.module-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.module-icon {
  font-size: 64px;
  margin-bottom: 16px;
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
  gap: 16px;
  margin-bottom: 32px;
  padding: 8px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow-x: auto;
}

.category-tab {
  flex-shrink: 0;
  padding: 12px 24px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  background: transparent;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-tab:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.category-tab.active {
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}

.tab-count {
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  font-size: 12px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.service-card {
  background: white;
  border-radius: 28px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s;
  cursor: pointer;
}

.service-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.service-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
  margin: 0;
  flex: 1;
}

.service-badge {
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.service-location {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px 0;
}

.service-features {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.feature-tag {
  padding: 4px 12px;
  background: rgba(102, 126, 234, 0.08);
  color: #667eea;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.service-rating {
  font-size: 13px;
  color: #606266;
}

.service-price {
  font-size: 18px;
  font-weight: 700;
  color: #667eea;
}

/* 政府合作项目 */
.gov-projects-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.gov-project-card {
  background: white;
  border-radius: 32px;
  padding: 28px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s;
  cursor: pointer;
}

.gov-project-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
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
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-active {
  background: #67c23a;
  color: white;
}

.status-recruiting {
  background: #e6a23c;
  color: white;
}

.project-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.project-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.project-tag {
  padding: 6px 14px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.project-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #606266;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
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
  background: white;
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.experience-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.experience-image {
  position: relative;
  width: 100%;
  height: 220px;
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

.experience-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: #667eea;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.experience-content {
  padding: 24px;
}

.experience-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.experience-location {
  font-size: 14px;
  color: #606266;
  margin: 0 0 16px 0;
}

.experience-features {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.feature-item {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.experience-footer {
  display: flex;
  justify-content: space-between;
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
  background: white;
  border-radius: 28px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.product-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  max-width: 90px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.product-card h4 {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px 0;
  padding-right: 80px;
  min-height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-origin {
  font-size: 12px;
  color: #909399;
  margin: 0 0 12px 0;
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
  font-size: 16px;
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
    margin-bottom: 20px;
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

