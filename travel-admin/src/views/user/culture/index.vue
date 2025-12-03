<template>
  <div class="culture-platform">
    <!-- 顶部横幅 -->
    <div id="banner" class="platform-banner">
            <div class="banner-content">
        <div class="banner-text">
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
        <div class="banner-illustration">
          <div class="illustration-circle circle-1"></div>
          <div class="illustration-circle circle-2"></div>
          <div class="illustration-circle circle-3"></div>
        </div>
      </div>
    </div>

    <!-- 核心板块 -->
    <div id="modules" class="core-modules">
      <h2 class="section-title">
        <span class="title-icon">🎯</span>
        核心板块
      </h2>
      <div class="modules-grid">
        <div 
          v-for="(module, index) in coreModules" 
          :key="index" 
          class="module-card"
          :class="`module-${index + 1}`"
          @click="navigateToModule(module)"
        >
          <div class="module-background" :style="{ background: module.gradient }"></div>
          <div class="module-icon">
            <el-icon :size="48">
              <component :is="module.icon" />
            </el-icon>
          </div>
          <h3 class="module-title">{{ module.title }}</h3>
          <p class="module-desc">{{ module.description }}</p>
          <div class="module-stats">
            <span class="stat-item">
              <el-icon><TrendCharts /></el-icon>
              <span>{{ module.count }}+项目</span>
            </span>
        </div>
          <div class="module-arrow">
            <el-icon><ArrowRight /></el-icon>
      </div>
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
          @click="switchTab(tab.value)"
        >
          <el-icon :size="20">
            <component :is="tab.icon" />
          </el-icon>
          <span>{{ tab.label }}</span>
          <div class="tab-badge" v-if="tab.count">{{ tab.count }}</div>
        </div>
      </div>
      
      <!-- 服务内容 -->
      <div class="service-content">
        <div class="content-grid">
          <div 
            v-for="service in currentServices" 
            :key="service.id" 
            class="service-card"
            @click="viewServiceDetail(service)"
          >
            <div class="service-image">
              <img :src="service.image" :alt="service.title" />
              <div class="service-badge">{{ service.badge }}</div>
              <div class="service-overlay">
                <el-button type="primary" circle size="large">
                  <el-icon><View /></el-icon>
                </el-button>
          </div>
            </div>
            <div class="service-info">
              <h3 class="service-title">{{ service.title }}</h3>
              <p class="service-location">
                <el-icon><Location /></el-icon>
                {{ service.location }}
              </p>
              <div class="service-features">
                <span v-for="feature in service.features" :key="feature" class="feature-tag">
                  {{ feature }}
              </span>
            </div>
              <div class="service-footer">
                <div class="service-stats">
                  <span class="stat">
                    <el-icon><User /></el-icon>
                    {{ service.participants }}人参与
                  </span>
                  <span class="stat">
                    <el-icon><Star /></el-icon>
                    {{ service.rating }}
                  </span>
          </div>
                <div class="service-price">
                  <span v-if="service.price === 0" class="free-tag">免费</span>
                  <span v-else class="price-value">¥{{ service.price }}<span class="price-unit">/人</span></span>
        </div>
      </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 政府合作项目 -->
    <div id="projects" class="gov-cooperation">
      <div class="section-header">
        <div>
          <h2 class="section-title">
            <span class="title-icon">🤝</span>
            政府合作项目
          </h2>
          <p class="section-subtitle">政府主导 · 精准扶持 · 产业升级</p>
        </div>
        <el-button type="primary" @click="viewAllGovProjects">
          查看全部项目
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      
      <div class="gov-projects-grid">
        <div 
          v-for="project in govProjects" 
          :key="project.id" 
          class="gov-project-card"
          @click="viewGovProject(project)"
        >
          <div class="project-header">
            <div class="project-logo">
              <img :src="project.govLogo" :alt="project.govName" />
            </div>
            <div class="project-meta">
              <h3 class="project-title">{{ project.title }}</h3>
              <p class="project-gov">{{ project.govName }}</p>
            </div>
            <div class="project-status">
              <el-tag :type="project.statusType" size="large">{{ project.statusText }}</el-tag>
            </div>
          </div>
          <p class="project-desc">{{ project.description }}</p>
          <div class="project-tags">
            <span v-for="tag in project.tags" :key="tag" class="project-tag">
              #{{ tag }}
            </span>
          </div>
          <div class="project-footer">
            <div class="project-info">
              <span class="info-item">
                <el-icon><Calendar /></el-icon>
                {{ project.startDate }}
              </span>
              <span class="info-item">
                <el-icon><Coin /></el-icon>
                投资 {{ project.investment }}万
              </span>
              <span class="info-item">
                <el-icon><User /></el-icon>
                惠及 {{ project.beneficiaries }}户
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 文化体验 -->
    <div id="experience" class="culture-experience">
      <h2 class="section-title">
        <span class="title-icon">🎭</span>
        文化体验
      </h2>
      <div class="experience-categories">
        <div 
          v-for="category in cultureCategories" 
          :key="category.id"
          class="experience-category"
          @click="viewCategoryDetail(category)"
        >
          <div class="category-cover" :style="{ backgroundImage: `url(${category.cover})` }">
            <div class="category-overlay"></div>
            <div class="category-content">
              <h3 class="category-name">{{ category.name }}</h3>
              <p class="category-desc">{{ category.description }}</p>
              <div class="category-count">{{ category.itemCount }}个体验项目</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 特色周边 -->
    <div id="products" class="local-products">
      <div class="section-header">
        <div>
          <h2 class="section-title">
            <span class="title-icon">🛍️</span>
            特色周边
          </h2>
          <p class="section-subtitle">地道特产 · 手工艺品 · 文创产品</p>
        </div>
        <el-button type="primary" plain @click="viewAllProducts">
          进入商城
          <el-icon><ArrowRight /></el-icon>
            </el-button>
      </div>
      
      <div class="products-grid">
        <div 
          v-for="product in localProducts" 
          :key="product.id" 
          class="product-card"
          @click="viewProduct(product)"
        >
          <div class="product-image">
            <img :src="product.image" :alt="product.name" />
            <div class="product-badge" v-if="product.badge">{{ product.badge }}</div>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-origin">
              <el-icon><MapLocation /></el-icon>
              {{ product.origin }}
            </p>
            <div class="product-footer">
              <div class="product-sales">已售{{ product.sales }}</div>
              <div class="product-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
            </div>
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
      <div class="cases-slider">
        <el-carousel :interval="5000" arrow="hover" height="350px">
          <el-carousel-item v-for="caseItem in successCases" :key="caseItem.id">
            <div class="case-card">
              <div class="case-image">
                <img :src="caseItem.image" :alt="caseItem.title" />
              </div>
              <div class="case-content">
                <h3 class="case-title">{{ caseItem.title }}</h3>
                <p class="case-location">
                  <el-icon><Location /></el-icon>
                  {{ caseItem.location }}
                </p>
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
          </el-carousel-item>
        </el-carousel>
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
          <div class="partner-logo">
            <img :src="partner.logo" :alt="partner.name" />
          </div>
          <p class="partner-name">{{ partner.name }}</p>
        </div>
      </div>
    </div>

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
    <footer class="culture-footer">
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
import { ElMessage } from 'element-plus'
import {
  Location, ArrowRight, TrendCharts, View, User, Star,
  Calendar, Coin, MapLocation, Office, ShoppingBag,
  Food, HomeFilled, Tickets, Coffee, Picture, Grid,
  Service, OfficeBuilding, Trophy, UserFilled
} from '@element-plus/icons-vue'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'

console.log('✅ 文旅对接页面开始加载...')

const router = useRouter()
const systemStore = useSystemStore()
const { footerDescription } = storeToRefs(systemStore)

// 当前激活的区块
const activeSection = ref('banner')

console.log('✅ Router初始化成功')

// 核心板块
const coreModules = ref([
  {
    icon: Office,
    title: '政府对接',
    description: '对接当地政府，政策支持，资源整合',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    count: 28,
    path: '/home/user/culture/government'
  },
  {
    icon: TrendCharts,
    title: '产业振兴',
    description: '旅游+农业，助力乡村产业升级',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    count: 45,
    path: '/home/user/culture/industry'
  },
  {
    icon: Picture,
    title: '文化传承',
    description: '保护传统文化，活化非遗项目',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    count: 63,
    path: '/home/user/culture/heritage'
  },
  {
    icon: ShoppingBag,
    title: '特产推广',
    description: '地方特产上线，拓展销售渠道',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    count: 156,
    path: '/home/user/culture/products'
  }
])

// 服务标签页
const serviceTabs = ref([
  { value: 'farmstay', label: '农家乐', icon: HomeFilled, count: 48 },
  { value: 'homestay', label: '特色民宿', icon: Coffee, count: 32 },
  { value: 'products', label: '农特产品', icon: ShoppingBag, count: 156 },
  { value: 'experience', label: '文化体验', icon: Tickets, count: 24 },
  { value: 'food', label: '地道美食', icon: Food, count: 67 }
])

const activeTab = ref('farmstay')

// 农家乐服务
const farmstayServices = ref([
  {
    id: 1,
    title: '云雾山庄农家乐',
    location: '重庆市巴南区',
    image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400',
    badge: '政府推荐',
    features: ['采摘体验', '农家餐饮', '垂钓'],
    participants: 1280,
    rating: 4.8,
    price: 88
  },
  {
    id: 2,
    title: '桃花源生态农庄',
    location: '重庆市江津区',
    image: 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=400',
    badge: '热门',
    features: ['有机蔬菜', '户外烧烤', '儿童乐园'],
    participants: 956,
    rating: 4.9,
    price: 128
  },
  {
    id: 3,
    title: '翠竹山居',
    location: '重庆市北碚区',
    image: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=400',
    badge: '新上架',
    features: ['竹林漫步', '茶艺体验', '农耕体验'],
    participants: 532,
    rating: 4.7,
    price: 98
  },
  {
    id: 4,
    title: '金色田园',
    location: '重庆市璧山区',
    image: 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=400',
    badge: '政府扶持',
    features: ['稻田观光', '农事体验', '土鸡美食'],
    participants: 768,
    rating: 4.6,
    price: 76
  }
])

// 民宿服务
const homestayServices = ref([
  {
    id: 1,
    title: '山水间·云舍',
    location: '重庆市武隆区',
    image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400',
    badge: '精品民宿',
    features: ['景观房', '管家服务', '私人影院'],
    participants: 423,
    rating: 4.9,
    price: 368
  },
  {
    id: 2,
    title: '老街记忆客栈',
    location: '重庆市磁器口',
    image: 'https://images.unsplash.com/photo-1586611292717-f828b167408c?w=400',
    badge: '文化特色',
    features: ['古镇风情', '传统装饰', '特色早餐'],
    participants: 892,
    rating: 4.8,
    price: 228
  },
  {
    id: 3,
    title: '竹海深处',
    location: '重庆市永川区',
    image: 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400',
    badge: '生态体验',
    features: ['竹海环绕', '天然氧吧', '静谧环境'],
    participants: 356,
    rating: 4.7,
    price: 198
  },
  {
    id: 4,
    title: '江畔小筑',
    location: '重庆市长寿区',
    image: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400',
    badge: '江景房',
    features: ['临江观景', '江鲜美食', '垂钓平台'],
    participants: 634,
    rating: 4.8,
    price: 268
  }
])

// 当前显示的服务
const currentServices = computed(() => {
  const servicesMap = {
    farmstay: farmstayServices.value,
    homestay: homestayServices.value,
    products: farmstayServices.value, // 暂用农家乐数据
    experience: homestayServices.value, // 暂用民宿数据
    food: farmstayServices.value // 暂用农家乐数据
  }
  return servicesMap[activeTab.value] || []
})

// 政府合作项目
const govProjects = ref([
  {
    id: 1,
    title: '巴南区乡村振兴示范项目',
    govName: '重庆市巴南区人民政府',
    govLogo: 'https://images.unsplash.com/photo-1541873676-a18131494184?w=100',
    description: '以农旅融合为核心，打造集观光、体验、度假为一体的乡村旅游综合体，带动当地农民增收致富。',
    tags: ['乡村振兴', '产业融合', '就业扶持'],
    startDate: '2024-03',
    investment: 5000,
    beneficiaries: 320,
    statusText: '进行中',
    statusType: 'success'
  },
  {
    id: 2,
    title: '武隆区文化遗产保护与开发',
    govName: '重庆市武隆区文化旅游委员会',
    govLogo: 'https://images.unsplash.com/photo-1541873676-a18131494184?w=100',
    description: '保护和传承当地非物质文化遗产，结合旅游开发，实现文化传承与经济发展双赢。',
    tags: ['文化传承', '非遗保护', '旅游开发'],
    startDate: '2024-01',
    investment: 3200,
    beneficiaries: 180,
    statusText: '招募中',
    statusType: 'warning'
  },
  {
    id: 3,
    title: '江津区特色农产品推广计划',
    govName: '重庆市江津区农业农村委员会',
    govLogo: 'https://images.unsplash.com/photo-1541873676-a18131494184?w=100',
    description: '建立线上线下一体化销售平台，帮助当地农产品拓展销售渠道，提升品牌影响力。',
    tags: ['农产品', '电商扶持', '品牌打造'],
    startDate: '2024-02',
    investment: 1500,
    beneficiaries: 450,
    statusText: '进行中',
    statusType: 'success'
  }
])

// 文化体验分类
const cultureCategories = ref([
  {
    id: 1,
    name: '非遗传承',
    description: '体验传统手工艺，感受非遗魅力',
    cover: 'https://images.unsplash.com/photo-1582719471384-894fbb16e074?w=400',
    itemCount: 28
  },
  {
    id: 2,
    name: '民俗体验',
    description: '参与地方民俗活动，了解风土人情',
    cover: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400',
    itemCount: 34
  },
  {
    id: 3,
    name: '传统美食',
    description: '品尝地道美食，传承饮食文化',
    cover: 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400',
    itemCount: 56
  },
  {
    id: 4,
    name: '古建筑游',
    description: '探访古建筑，感受历史韵味',
    cover: 'https://images.unsplash.com/photo-1596436889106-be35e843f974?w=400',
    itemCount: 18
  }
])

// 特色周边产品
const localProducts = ref([
  {
    id: 1,
    name: '巴南银针茶',
    origin: '重庆巴南',
    image: 'https://images.unsplash.com/photo-1563822249366-88b34e9b8d98?w=300',
    badge: '地理标志',
    price: 128,
    sales: 2356
  },
  {
    id: 2,
    name: '江津花椒',
    origin: '重庆江津',
    image: 'https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=300',
    badge: '原产地直供',
    price: 68,
    sales: 5678
  },
  {
    id: 3,
    name: '手工竹编',
    origin: '重庆梁平',
    image: 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=300',
    badge: '非遗工艺',
    price: 158,
    sales: 892
  },
  {
    id: 4,
    name: '土家织锦',
    origin: '重庆酉阳',
    image: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=300',
    badge: '文创精品',
    price: 368,
    sales: 456
  },
  {
    id: 5,
    name: '涪陵榨菜',
    origin: '重庆涪陵',
    image: 'https://images.unsplash.com/photo-1589621316382-008455b857cd?w=300',
    badge: '老字号',
    price: 45,
    sales: 12356
  },
  {
    id: 6,
    name: '石柱蜂蜜',
    origin: '重庆石柱',
    image: 'https://images.unsplash.com/photo-1587049352846-4a222e784l94?w=300',
    badge: '天然有机',
    price: 98,
    sales: 3456
  }
])

// 成功案例
const successCases = ref([
  {
    id: 1,
    title: '巴南区丰盛古镇文旅振兴',
    location: '重庆市巴南区',
    image: 'https://images.unsplash.com/photo-1596436889106-be35e843f974?w=600',
    description: '通过政府引导、企业运营、村民参与的模式，将古镇打造成集文化体验、休闲度假、特色美食于一体的旅游目的地。',
    revenue: 8000,
    jobs: 320,
    visitors: 150
  },
  {
    id: 2,
    title: '武隆仙女山民宿集群',
    location: '重庆市武隆区',
    image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600',
    description: '依托自然风光，发展高端民宿产业，带动当地农民就业，实现年收入翻倍增长。',
    revenue: 6500,
    jobs: 180,
    visitors: 85
  },
  {
    id: 3,
    title: '江津四面山农旅融合',
    location: '重庆市江津区',
    image: 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600',
    description: '结合景区优势，发展观光农业、农事体验、农产品销售，形成完整产业链。',
    revenue: 5200,
    jobs: 256,
    visitors: 120
  }
])

// 合作伙伴
const partners = ref([
  { id: 1, name: '重庆市文化旅游委员会', logo: 'https://via.placeholder.com/100' },
  { id: 2, name: '重庆市农业农村委员会', logo: 'https://via.placeholder.com/100' },
  { id: 3, name: '中国旅游集团', logo: 'https://via.placeholder.com/100' },
  { id: 4, name: '携程旅行网', logo: 'https://via.placeholder.com/100' },
  { id: 5, name: '美团点评', logo: 'https://via.placeholder.com/100' },
  { id: 6, name: '阿里巴巴乡村振兴基金', logo: 'https://via.placeholder.com/100' }
])

// 滚动到指定区域
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    const headerOffset = 80
    const elementPosition = element.getBoundingClientRect().top
    const offsetPosition = elementPosition + window.pageYOffset - headerOffset
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
    
    // 手动设置激活状态
    activeSection.value = sectionId
  }
}

// 滚动监听，自动更新当前激活的区块
const handleScroll = () => {
  const sections = ['banner', 'modules', 'services', 'projects', 'experience', 'products', 'cases', 'partners']
  const scrollPosition = window.scrollY + 200 // 偏移量，提前触发
  
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
  window.addEventListener('scroll', handleScroll)
  handleScroll() // 初始化当前区块
})

// 页面卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 方法
const navigateToModule = (module) => {
  ElMessage.info(`跳转到：${module.title}`)
  // router.push(module.path)
}

const switchTab = (tab) => {
  activeTab.value = tab
}

const viewServiceDetail = (service) => {
  ElMessage.success(`查看服务详情：${service.title}`)
}

const viewAllGovProjects = () => {
  ElMessage.info('查看全部政府合作项目')
}

const viewGovProject = (project) => {
  ElMessage.success(`查看项目详情：${project.title}`)
}

const viewCategoryDetail = (category) => {
  ElMessage.success(`查看分类：${category.name}`)
}

const viewAllProducts = () => {
  ElMessage.info('进入特产商城')
}

const viewProduct = (product) => {
  ElMessage.success(`查看产品：${product.name}`)
}
</script>

<style lang="scss" scoped>
.culture-platform {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #e8eef5 100%);
  position: relative;
  padding-right: 120px; // 为右侧导航条留出空间
  
  // 背景装饰
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 600px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    pointer-events: none;
    z-index: 0;
  }
  
  > * {
    position: relative;
    z-index: 1;
  }
}

// 顶部横幅
.platform-banner {
  padding: 60px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  
  .banner-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;
    z-index: 2;
  }
  
  .banner-text {
    flex: 1;
  }
  
  .banner-title {
    font-size: 48px;
    font-weight: 800;
    color: white;
    margin: 0 0 16px 0;
    display: flex;
    align-items: center;
    gap: 16px;
    
    .title-icon {
      font-size: 56px;
    }
  }
  
  .banner-subtitle {
    font-size: 20px;
    color: rgba(255, 255, 255, 0.9);
    margin: 0 0 24px 0;
    letter-spacing: 1px;
  }
  
  .banner-tags {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    
    .tag-item {
      padding: 8px 20px;
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      color: white;
      font-size: 14px;
      font-weight: 500;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
  }
  
  .banner-illustration {
    position: relative;
    width: 300px;
    height: 300px;
    
    .illustration-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      
      &.circle-1 {
        width: 200px;
        height: 200px;
        top: 0;
        right: 0;
        animation: float 6s ease-in-out infinite;
      }
      
      &.circle-2 {
        width: 150px;
        height: 150px;
        bottom: 50px;
        right: 100px;
        animation: float 8s ease-in-out infinite reverse;
      }
      
      &.circle-3 {
        width: 100px;
        height: 100px;
        top: 100px;
        right: 50px;
        animation: float 10s ease-in-out infinite;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

// 核心板块
.core-modules {
  padding: 60px 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  .modules-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
    margin-top: 32px;
    
    .module-card {
      position: relative;
      background: white;
      border-radius: 24px;
      padding: 32px;
      cursor: pointer;
      overflow: hidden;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      
      &::before {
        content: '';
        position: absolute;
        inset: -2px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
        border-radius: 24px;
        opacity: 0;
        z-index: 0;
        transition: opacity 0.4s;
      }
      
      &:hover {
        transform: translateY(-12px);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        
        &::before {
          opacity: 1;
        }
        
        .module-icon {
          transform: scale(1.1) rotate(5deg);
        }
        
        .module-arrow {
          transform: translateX(8px);
          opacity: 1;
        }
      }
      
      > * {
        position: relative;
        z-index: 1;
      }
      
      .module-background {
        position: absolute;
        inset: 0;
        opacity: 0.05;
        z-index: 0;
      }
      
      .module-icon {
        width: 80px;
        height: 80px;
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    margin-bottom: 20px;
        transition: all 0.4s;
        box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
      }
      
      &.module-2 .module-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        box-shadow: 0 8px 32px rgba(240, 147, 251, 0.3);
      }
      
      &.module-3 .module-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        box-shadow: 0 8px 32px rgba(79, 172, 254, 0.3);
      }
      
      &.module-4 .module-icon {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        box-shadow: 0 8px 32px rgba(67, 233, 123, 0.3);
      }
      
      .module-title {
        font-size: 20px;
        font-weight: 700;
      color: #303133;
        margin: 0 0 12px 0;
    }
    
      .module-desc {
      font-size: 14px;
        color: #909399;
        line-height: 1.6;
        margin: 0 0 16px 0;
      }
      
      .module-stats {
        display: flex;
        gap: 16px;
        margin-bottom: 12px;
        
        .stat-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: #667eea;
          font-weight: 600;
          
          .el-icon {
            font-size: 16px;
          }
        }
      }
      
      .module-arrow {
        position: absolute;
        right: 24px;
        bottom: 24px;
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: rgba(102, 126, 234, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #667eea;
        font-size: 18px;
        opacity: 0.6;
        transition: all 0.3s;
      }
    }
  }
}

// 服务分类
.service-categories {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  .categories-tabs {
    display: flex;
    gap: 16px;
    margin: 32px 0;
    padding: 8px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
    overflow-x: auto;
    
    &::-webkit-scrollbar {
      height: 4px;
    }
    
    &::-webkit-scrollbar-track {
      background: rgba(0, 0, 0, 0.05);
      border-radius: 2px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 2px;
    }
    
    .category-tab {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s;
      font-size: 15px;
      font-weight: 500;
      color: #606266;
      position: relative;
      background: transparent;
      border: 2px solid transparent;
      
      &::before {
        content: '';
        position: absolute;
        inset: -2px;
        border-radius: 14px;
        padding: 2px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
        -webkit-mask-composite: xor;
        mask-composite: exclude;
        opacity: 0;
        transition: opacity 0.3s;
      }
      
      &:hover {
        color: #667eea;
        background: rgba(102, 126, 234, 0.05);
      }
      
      &.active {
        color: white;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
        
        .tab-badge {
          background: rgba(255, 255, 255, 0.3);
          color: white;
        }
      }
      
      .tab-badge {
        min-width: 20px;
        height: 20px;
        padding: 0 6px;
        background: rgba(102, 126, 234, 0.1);
        color: #667eea;
        border-radius: 10px;
        font-size: 12px;
        font-weight: 600;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s;
      }
    }
  }
  
  .service-content {
    margin-top: 32px;
  }
  
  .content-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
    
    .service-card {
      background: white;
      border-radius: 20px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.4s;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        
        .service-image {
          img {
            transform: scale(1.1);
          }
          
          .service-overlay {
            opacity: 1;
          }
        }
      }
      
      .service-image {
        position: relative;
        height: 200px;
        overflow: hidden;
        background: #f5f7fa;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
          transition: transform 0.4s;
      }
      
        .service-badge {
        position: absolute;
          top: 12px;
          left: 12px;
          padding: 6px 16px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 600;
          box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
        }
        
        .service-overlay {
          position: absolute;
          inset: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: opacity 0.3s;
        }
      }
      
      .service-info {
        padding: 20px;
        
        .service-title {
          font-size: 16px;
          font-weight: 700;
          color: #303133;
          margin: 0 0 8px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .service-location {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: #909399;
          margin-bottom: 12px;
          
          .el-icon {
            font-size: 14px;
          }
        }
        
        .service-features {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
          margin-bottom: 16px;
          
          .feature-tag {
            padding: 4px 12px;
            background: rgba(102, 126, 234, 0.08);
            color: #667eea;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
          }
        }
        
        .service-footer {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 16px;
          border-top: 1px solid #f0f0f0;
          
          .service-stats {
            display: flex;
            gap: 12px;
            
            .stat {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              color: #909399;
              
              .el-icon {
                font-size: 14px;
                color: #ffb800;
              }
            }
          }
          
          .service-price {
            .free-tag {
              font-size: 14px;
              font-weight: 600;
              color: #67c23a;
            }
            
            .price-value {
              font-size: 20px;
              font-weight: 700;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              -webkit-background-clip: text;
              -webkit-text-fill-color: transparent;
              background-clip: text;
              
              .price-unit {
                font-size: 12px;
                font-weight: 500;
              }
            }
          }
        }
      }
    }
  }
}

// 政府合作项目
.gov-cooperation {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  .section-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    margin-bottom: 32px;
  }
  
  .gov-projects-grid {
      display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    
    .gov-project-card {
      background: white;
      border-radius: 20px;
      padding: 28px;
        cursor: pointer;
      transition: all 0.4s;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      border: 2px solid transparent;
        
        &:hover {
        transform: translateY(-8px);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        border-color: #667eea;
      }
      
      .project-header {
        display: flex;
        align-items: flex-start;
        gap: 16px;
        margin-bottom: 16px;
        
        .project-logo {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          overflow: hidden;
          flex-shrink: 0;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
        
        .project-meta {
          flex: 1;
          
          .project-title {
            font-size: 17px;
            font-weight: 700;
          color: #303133;
            margin: 0 0 6px 0;
            line-height: 1.4;
        }
        
          .project-gov {
            font-size: 13px;
          color: #909399;
          }
        }
        
        .project-status {
          flex-shrink: 0;
        }
      }
      
      .project-desc {
        font-size: 14px;
        color: #606266;
        line-height: 1.8;
        margin-bottom: 16px;
      }
      
      .project-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
    margin-bottom: 20px;
    
        .project-tag {
          padding: 6px 14px;
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
          color: #667eea;
          border-radius: 16px;
          font-size: 13px;
          font-weight: 500;
        }
      }
      
      .project-footer {
        padding-top: 20px;
        border-top: 1px solid #f0f0f0;
        
        .project-info {
      display: flex;
          gap: 20px;
          flex-wrap: wrap;
          
          .info-item {
            display: flex;
      align-items: center;
            gap: 6px;
            font-size: 13px;
            color: #606266;
            font-weight: 500;
            
            .el-icon {
              font-size: 16px;
              color: #667eea;
            }
          }
        }
      }
    }
  }
}

// 文化体验
.culture-experience {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  .experience-categories {
      display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
    margin-top: 32px;
    
    .experience-category {
      position: relative;
      height: 280px;
      border-radius: 20px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.4s;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
        
        .category-cover {
          transform: scale(1.1);
        }
        
        .category-overlay {
          background: rgba(0, 0, 0, 0.6);
        }
      }
      
      .category-cover {
        position: absolute;
        inset: 0;
        background-size: cover;
        background-position: center;
        transition: transform 0.4s;
      }
      
      .category-overlay {
        position: absolute;
        inset: 0;
        background: linear-gradient(180deg, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.7) 100%);
        transition: all 0.3s;
      }
      
      .category-content {
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
        justify-content: flex-end;
        padding: 24px;
        color: white;
        
        .category-name {
          font-size: 22px;
          font-weight: 700;
          margin: 0 0 8px 0;
        }
        
        .category-desc {
          font-size: 14px;
          opacity: 0.9;
          margin: 0 0 12px 0;
          line-height: 1.6;
        }
        
        .category-count {
          font-size: 13px;
          opacity: 0.8;
        }
      }
    }
  }
}

// 特色周边
.local-products {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  .section-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    margin-bottom: 32px;
  }
  
  .products-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
      gap: 24px;
      
    .product-card {
      background: white;
      border-radius: 16px;
        overflow: hidden;
        cursor: pointer;
        transition: all 0.3s;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
        
        &:hover {
        transform: translateY(-6px);
        box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
        
        .product-image img {
          transform: scale(1.1);
        }
      }
      
      .product-image {
          position: relative;
        height: 160px;
          overflow: hidden;
        background: #f5f7fa;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          transition: transform 0.4s;
          }
          
        .product-badge {
            position: absolute;
            top: 12px;
            right: 12px;
          padding: 6px 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          border-radius: 12px;
          font-size: 11px;
          font-weight: 600;
          box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
          max-width: 90px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
      
      .product-info {
        padding: 14px;
        
        .product-name {
          font-size: 14px;
          font-weight: 600;
            color: #303133;
          margin: 0 0 6px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .product-origin {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          color: #909399;
          margin-bottom: 10px;
          
          .el-icon {
            font-size: 13px;
          }
        }
        
        .product-footer {
          display: flex;
          align-items: center;
          justify-content: space-between;
          
          .product-sales {
            font-size: 11px;
            color: #909399;
          }
          
          .product-price {
            display: flex;
            align-items: baseline;
            
            .price-symbol {
              font-size: 12px;
              font-weight: 600;
              color: #f56c6c;
            }
            
            .price-value {
              font-size: 18px;
              font-weight: 700;
              color: #f56c6c;
            }
          }
        }
      }
    }
  }
}

// 成功案例
.success-cases {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  .cases-slider {
    margin-top: 32px;
    
    .case-card {
      display: flex;
      gap: 32px;
      background: white;
      border-radius: 24px;
      padding: 32px;
      height: 100%;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
      
      .case-image {
        flex-shrink: 0;
        width: 400px;
        height: 286px;
        border-radius: 16px;
        overflow: hidden;
        background: #f5f7fa;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .case-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        
        .case-title {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
            margin: 0 0 12px 0;
        }
        
        .case-location {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 15px;
          color: #909399;
          margin-bottom: 20px;
          
          .el-icon {
            font-size: 16px;
          }
        }
        
        .case-description {
          font-size: 15px;
            color: #606266;
          line-height: 1.8;
          margin-bottom: auto;
          }
          
        .case-achievements {
            display: flex;
          gap: 40px;
          margin-top: 32px;
          padding-top: 24px;
          border-top: 1px solid #f0f0f0;
          
          .achievement-item {
            text-align: center;
            
            .achievement-value {
              font-size: 32px;
              font-weight: 700;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              -webkit-background-clip: text;
              -webkit-text-fill-color: transparent;
              background-clip: text;
              margin-bottom: 8px;
            }
            
            .achievement-label {
              font-size: 13px;
              color: #909399;
            }
          }
        }
      }
    }
  }
}

// 合作伙伴
.partners {
  padding: 40px 40px 80px;
  max-width: 1200px;
  margin: 0 auto;
  
  .partners-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 24px;
    margin-top: 32px;
    
    .partner-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      text-align: center;
      transition: all 0.3s;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      
      &:hover {
        transform: translateY(-6px);
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
      }
      
      .partner-logo {
        width: 80px;
        height: 80px;
        margin: 0 auto 12px;
        border-radius: 12px;
        overflow: hidden;
        background: #f5f7fa;
        display: flex;
            align-items: center;
        justify-content: center;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
        }
      }
      
      .partner-name {
        font-size: 13px;
        color: #606266;
        line-height: 1.4;
      }
    }
  }
}

// 通用样式
.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
              display: flex;
              align-items: center;
  gap: 12px;
  margin: 0 0 8px 0;
  
  .title-icon {
    font-size: 36px;
  }
}

.section-subtitle {
  font-size: 15px;
              color: #909399;
  margin: 0;
  letter-spacing: 0.5px;
}

// 响应式
@media (max-width: 1400px) {
  .modules-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .content-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }
  
  .gov-projects-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .experience-categories {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .products-grid {
    grid-template-columns: repeat(4, 1fr) !important;
  }
  
  .partners-grid {
    grid-template-columns: repeat(4, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .platform-banner {
    padding: 40px 20px;
    
    .banner-content {
      flex-direction: column;
    }
    
    .banner-title {
      font-size: 32px;
    }
    
    .banner-illustration {
      display: none;
    }
  }
  
  .core-modules,
  .service-categories,
  .gov-cooperation,
  .culture-experience,
  .local-products,
  .success-cases,
  .partners {
    padding: 30px 20px;
  }
  
  .modules-grid,
  .content-grid,
  .gov-projects-grid,
  .experience-categories,
  .products-grid,
  .partners-grid {
    grid-template-columns: 1fr !important;
  }
  
  .case-card {
    flex-direction: column;
    
    .case-image {
      width: 100%;
      height: 200px;
    }
  }
}

/* 底部信息 */
.culture-footer {
  background: #ffffff;
  color: #606266;
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
    background: linear-gradient(to right, transparent 0%, #e4e7ed 20%, #e4e7ed 80%, transparent 100%);
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
      color: #606266;
      text-decoration: none;
      transition: color 0.3s;
      
      &:hover {
        color: #667eea;
      }
    }
    
    .divider {
      color: #c0c4cc;
      margin: 0 4px;
    }
  }
  
  .footer-info {
    text-align: center;
    font-size: 11px;
    line-height: 1.8;
    color: #909399;
    
    p {
      margin: 4px 0;
    }
  }
}

@media (max-width: 768px) {
  .culture-footer {
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

@media (max-width: 1200px) {
  .side-nav {
    display: none;
  }
}
</style>
