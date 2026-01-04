<template>
  <div class="modern-user-dashboard">
    <!-- 
      轮播图区域：展示平台精选的旅游主题和活动信息，支持自动轮播和手动切换
      核心功能：
      1. 使用Element Plus的el-carousel组件实现自动轮播（5秒间隔）
      2. 通过v-for遍历轮播图数据，动态绑定背景图片和内容
      3. 点击轮播图可跳转到对应链接
      4. 展示标题、副标题、徽章等信息
    -->
    <div id="carousel" class="carousel-section" v-if="banners.length > 0">
      <el-carousel :interval="5000" arrow="never" height="580px" class="premium-carousel">
        <el-carousel-item v-for="(banner, index) in banners" :key="banner.id || index">
          <div 
            class="carousel-item" 
            :style="{ backgroundImage: `url(${banner.image})` }"
            :class="{ 'carousel-item-clickable': isBannerClickable(banner) }"
            @click="handleBannerClick(banner)"
          >
            <div class="carousel-overlay"></div>
            <div class="carousel-content">
              <div class="carousel-badge">
                <span class="badge-windmill">🌀</span>
                {{ banner.badge }}
              </div>
              <h2 class="carousel-title">{{ banner.title }}</h2>
              <p class="carousel-subtitle">{{ banner.subtitle }}</p>
              <div 
                v-if="isBannerClickable(banner)" 
                class="carousel-link" 
                @click.stop="handleBannerClick(banner)"
              >
                <span class="link-text">立即探索</span>
                <el-icon class="link-arrow"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 顶部区域：欢迎区 + 新闻/公告（并排显示） -->
    <div class="top-section-container">
      <!-- 左侧：欢迎区（天气 + 用户画像） -->
      <div id="welcome" class="welcome-section">
        <div class="welcome-content">
        <!-- 左侧：用户信息 -->
        <div class="user-info-section">
          <div class="user-header">
            <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
              <el-icon :size="40"><User /></el-icon>
            </el-avatar>
            <div class="user-greeting">
              <h1 class="welcome-title">
                <span class="greeting">{{ greeting }}，</span>
                <span class="user-name">{{ userInfo.nickname || '旅行者' }}</span>
              </h1>
              <p class="welcome-subtitle">开启你的智慧旅游之旅</p>
            </div>
          </div>
          
          <!-- 天气信息 -->
          <div class="weather-info-card">
            <div class="weather-main">
              <div class="weather-icon-wrapper">
                <span class="weather-icon">{{ weatherIcon }}</span>
                <div class="weather-temp">{{ weatherTemp }}</div>
              </div>
              <div class="weather-details">
                <div class="weather-location">
                  <el-icon><Location /></el-icon>
                  <span class="location-text">{{ weather.location || (weatherLoading ? '正在获取位置...' : '位置信息加载中') }}</span>
                  <el-button 
                    type="text" 
                    class="location-select-btn"
                    @click="showLocationSelector = true"
                    :icon="Edit"
                    title="选择位置"
                  >
                  </el-button>
                </div>
                <div class="weather-condition">{{ weather.weather || (weatherLoading ? '查询中...' : '') }}</div>
              </div>
            </div>
            <div class="weather-metrics">
              <div class="metric-item">
                <span class="metric-icon">💧</span>
                <div class="metric-info">
                  <span class="metric-label">湿度</span>
                  <span class="metric-value">{{ weather.humidity }}%</span>
                </div>
              </div>
              <div class="metric-item">
                <span class="metric-icon">💨</span>
                <div class="metric-info">
                  <span class="metric-label">风力</span>
                  <span class="metric-value">{{ weather.windSpeed }}级</span>
                </div>
              </div>
              <div class="metric-item">
                <span class="metric-icon">🌫️</span>
                <div class="metric-info">
                  <span class="metric-label">空气质量</span>
                  <span class="metric-value" :class="aqiClass">{{ weather.aqi }}</span>
                </div>
              </div>
              <div class="metric-item">
                <span class="metric-icon">☀️</span>
                <div class="metric-info">
                  <span class="metric-label">紫外线</span>
                  <span class="metric-value">{{ weather.uvIndex }}</span>
                </div>
              </div>
            </div>
            <div class="weather-advice">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ weatherAdvice }}</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧：用户画像分析 -->
        <div class="profile-guide-card" @click="goToPortrait">
          <!-- 装饰性元素 -->
          <div class="card-decoration"></div>
          
          <div class="guide-icon-wrapper">
            <div class="guide-icon-bg"></div>
            <el-icon class="guide-icon" :size="50"><DataAnalysis /></el-icon>
          </div>
          <div class="guide-content">
            <h3 class="guide-title">您的用户画像</h3>
            <p class="guide-desc">基于您的浏览行为和偏好，AI为您智能推荐</p>
            <div class="guide-stats">
              <div class="stat-badge">
                <el-icon><TrendCharts /></el-icon>
                <span>浏览 {{ userPortrait.browseCount || 0 }} 次</span>
              </div>
              <div class="stat-badge">
                <el-icon><Star /></el-icon>
                <span>收藏 {{ userPortrait.favoriteCount || 0 }} 次</span>
              </div>
              <div class="stat-badge">
                <el-icon><Search /></el-icon>
                <span>搜索 {{ userPortrait.searchCount || 0 }} 次</span>
              </div>
              <div class="stat-badge">
                <el-icon><LocationFilled /></el-icon>
                <span>{{ userPortrait.favoriteType || '自然风光' }}</span>
              </div>
            </div>
            <div class="guide-stats-secondary">
              <div class="stat-item" v-if="userPortrait.consumptionLevel">
                <span class="stat-label">消费水平：</span>
                <span class="stat-value">{{ userPortrait.consumptionLevel }}</span>
              </div>
              <div class="stat-item" v-if="userPortrait.travelMode">
                <span class="stat-label">出行方式：</span>
                <span class="stat-value">{{ userPortrait.travelMode }}</span>
              </div>
              <div class="stat-item" v-if="userPortrait.avgStayTime">
                <span class="stat-label">平均停留：</span>
                <span class="stat-value">{{ userPortrait.avgStayTime }}分钟</span>
              </div>
            </div>
            <div class="portrait-tags">
              <span v-for="tag in userPortrait.tags" :key="tag" class="portrait-tag">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
        </div>
        
        <!-- 装饰性元素 -->
        <div class="decoration-circles">
          <div class="circle circle-1"></div>
          <div class="circle circle-2"></div>
          <div class="circle circle-3"></div>
        </div>
      </div>

      <!-- 右侧：新闻/公告区域 -->
      <div class="news-section">
        <div class="news-container">
          <div class="news-header">
            <div>
              <div class="news-kicker">公告中心</div>
            </div>
          </div>
          <div class="news-tabs">
            <button
              v-for="tab in newsTabs"
              :key="tab.key"
              type="button"
              class="news-tab"
              :class="['news-tab', tabStyleMap[tab.key], { active: activeNewsTab === tab.key }]"
              @click="activeNewsTab = tab.key"
            >
              {{ tab.label }}
            </button>
          </div>
          <div class="news-content">
            <transition name="news-fade" mode="out-in">
              <div v-if="getNewsList().length" class="news-list" :key="activeNewsTab">
                <button
                  v-for="(item, index) in getNewsList()"
                  :key="`${activeNewsTab}-${item.id || index}`"
                  class="news-item"
                  type="button"
                  @click="handleNewsClick(item)"
                >
                  <div class="news-item-label" :class="getAnnouncementTypeClass(item.type)">{{ item.label }}</div>
                  <div class="news-item-title">{{ item.title }}</div>
                  <div class="news-item-date">{{ item.date }}</div>
                  <el-icon class="news-item-arrow"><ArrowRight /></el-icon>
                </button>
              </div>
              <div class="news-empty" :key="`${activeNewsTab}-empty`" v-else>
                <div class="news-empty-icon">📭</div>
                <div class="news-empty-title">暂无公告</div>
                <p class="news-empty-desc">稍后再来看看，新的公告会第一时间展示在这里。</p>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog 
      v-model="announcementDialogVisible" 
      width="750px"
      :close-on-click-modal="true"
      class="announcement-detail-dialog"
      :show-close="true"
    >
      <template #header>
        <div class="dialog-header-custom">
          <div class="dialog-title">{{ currentAnnouncement?.title || '公告详情' }}</div>
          <div v-if="currentAnnouncement" class="dialog-subtitle">
            <span class="type-tag" :class="getAnnouncementTypeClass(currentAnnouncement.type)">
              {{ getAnnouncementTypeName(currentAnnouncement.type) }}
            </span>
            <span class="announcement-date">
              <el-icon class="date-icon"><Clock /></el-icon>
              <span class="date-text">{{ formatAnnouncementDate(currentAnnouncement.publishTime || currentAnnouncement.createTime) }}</span>
            </span>
          </div>
        </div>
      </template>
          <div v-if="currentAnnouncement" class="announcement-detail">
        <div class="announcement-content">
          <div class="content-text" v-html="formatContent(currentAnnouncement.content)"></div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="close-btn" plain @click="announcementDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 核心功能和个人中心（并排显示） -->
    <div class="features-myspace-container">
      <!-- 核心功能导航 -->
      <div id="features" class="features-section">
        <h2 class="section-title">
          <span class="title-icon">🎯</span>
          核心功能
        </h2>
        <p class="section-subtitle">
          探索AI智能规划、景点社区、攻略社区、文旅体验、用户画像、升级指南等核心功能
        </p>
        <div class="features-grid">
          <div 
            v-for="(feature, index) in features" 
            :key="index" 
            class="feature-card"
            :class="`feature-${index + 1}`"
            @click="navigateTo(feature.path)"
          >
            <div class="feature-background" :style="{ background: feature.gradient }"></div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.desc }}</p>
            <div class="feature-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 个人中心 -->
      <div id="myspace" class="my-space-section">
        <h2 class="section-title">
          <span class="title-icon">💼</span>
          个人中心
        </h2>
        <p class="section-subtitle">
          管理你的攻略、收藏和个人信息
        </p>
        <div class="my-space-grid">
          <div 
            v-for="(item, index) in mySpace" 
            :key="index" 
            class="space-card"
            @click="navigateTo(item.path)"
          >
            <div class="space-content">
              <div class="space-header">
                <h3 class="space-title">{{ item.title }}</h3>
                <div class="space-badge" v-if="item.count > 0">{{ item.count }}</div>
              </div>
              <p class="space-desc">{{ item.desc }}</p>
              <div class="space-stats" v-if="item.title !== '个人资料'">
                <span class="stat-item">
                  <el-icon><Document /></el-icon>
                  <span>{{ item.stats.total }}</span>
                </span>
                <span v-if="item.title === '我的攻略'" class="stat-item">
                  <el-icon><View /></el-icon>
                  <span>{{ item.stats.views }}</span>
                </span>
                <span v-if="item.title === '我的攻略'" class="stat-item">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>{{ item.stats.comments || 0 }}</span>
                </span>
              </div>
            </div>
            <div class="space-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 升级指南和文旅体验（并排显示） -->
    <div class="level-guide-culture-container">
      <!-- 升级指南区域（集成到首页） -->
      <div id="level-guide" class="level-guide-section">
        <div class="section-header">
          <div>
            <h2 class="section-title">
              <span class="title-icon">🏆</span>
              升级指南
            </h2>
            <p class="section-subtitle">了解等级体系，快速提升你的旅行者等级</p>
          </div>
        </div>
        <div class="level-guide-content">
          <!-- 当前等级卡片 -->
          <el-card class="current-level-mini-card">
            <div class="level-status-mini">
              <div class="level-icon-mini" :style="{ background: currentLevelGradient }">
                <el-icon :size="36">
                  <User v-if="currentLevelIcon === 'User'" />
                  <Aim v-else-if="currentLevelIcon === 'Aim'" />
                  <Medal v-else-if="currentLevelIcon === 'Medal'" />
                  <Trophy v-else-if="currentLevelIcon === 'Trophy'" />
                  <StarFilled v-else-if="currentLevelIcon === 'StarFilled'" />
                  <TrophyBase v-else-if="currentLevelIcon === 'TrophyBase'" />
                  <Medal v-else />
                </el-icon>
              </div>
              <div class="level-info-mini">
                <div class="level-name-mini" :style="{ color: currentLevelColor }">{{ currentLevelName }}</div>
                <div class="level-desc-mini">当前等级</div>
              </div>
              <div class="level-progress-mini" v-if="nextLevel">
                <el-progress 
                  :percentage="levelProgress" 
                  :color="progressColor"
                  :stroke-width="10"
                  :show-text="false"
                />
                <div class="progress-text-mini">距离{{ nextLevel.name }}还需 {{ nextLevel.points - currentPoints }} 积分</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 文旅商业化 -->
      <div id="culture" class="culture-section">
        <div class="section-header">
          <div>
            <h2 class="section-title">
              <span class="title-icon">🏛️</span>
              文旅体验
            </h2>
            <p class="section-subtitle">特色文旅项目，体验当地文化</p>
          </div>
        </div>
        
        <el-row :gutter="20" class="culture-grid">
          <el-col :span="12" v-for="item in cultureProjects" :key="item.id">
            <div 
              class="culture-card"
              :class="{ 'is-placeholder': item.isPlaceholder }"
              @click="!item.isPlaceholder && viewCulture(item)"
            >
              <div class="culture-image">
                <img 
                  v-if="item.image" 
                  :src="item.image" 
                  :alt="item.name" 
                />
                <div v-else class="no-image">
                  <el-icon><Picture /></el-icon>
                  <span>暂无图片</span>
                </div>
                <div v-if="item.typeName" class="culture-type">{{ item.typeName }}</div>
              </div>
              <div class="culture-content">
                <h3 class="culture-title">{{ item.name }}</h3>
                <p v-if="item.location" class="culture-location">
                  <el-icon><Location /></el-icon>
                  {{ item.location }}
                </p>
                <div v-if="item.features && item.features.length > 0" class="culture-features">
                  <span v-for="feature in item.features" :key="feature" class="feature-tag">
                    {{ feature }}
                  </span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 为你推荐和热门攻略（并排显示） -->
    <div class="recommendations-plans-container">
      <!-- 智能推荐 -->
      <div id="recommendations" class="recommendations-section">
        <div class="section-header">
          <div>
            <h2 class="section-title">
              <span class="title-icon">✨</span>
              为你推荐
            </h2>
            <p class="section-subtitle">
              基于您的用户画像大数据智能推荐
            </p>
          </div>
        </div>
        
        <div class="recommendations-grid">
          <div 
            v-for="item in recommendations" 
            :key="item.id" 
            class="recommendation-card"
            @click="viewDetail(item)"
          >
            <div class="recommendation-image">
              <img :src="item.image" :alt="item.title" />
              <div class="recommendation-overlay">
                <el-button circle size="large" class="like-btn">
                  <el-icon><Star /></el-icon>
                </el-button>
              </div>
              <div class="recommendation-badge">{{ item.badge }}</div>
            </div>
            <div class="recommendation-content">
              <div class="recommendation-header">
                <h3 class="recommendation-title">{{ item.title }}</h3>
              </div>
              <p class="recommendation-location">
                <el-icon><Location /></el-icon>
                {{ item.location }}
              </p>
              <div class="recommendation-tags">
                <span v-for="tag in item.tags" :key="tag" class="tag">
                  {{ tag }}
                </span>
              </div>
              <div class="recommendation-footer">
                <div class="stats-group">
                  <span class="favorites">
                    <el-icon><Star /></el-icon>
                    {{ formatNumber(item.favorites || item.collectCount || 0) }}
                  </span>
                  <span class="views">
                    <el-icon><View /></el-icon>
                    {{ formatNumber(item.views) }}
                  </span>
                  <span class="comments">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ formatNumber(item.comments || item.commentCount || 0) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门攻略 -->
      <div id="plans" class="plans-section">
      <div class="section-header">
        <div>
          <h2 class="section-title">
            <span class="title-icon">📖</span>
            热门攻略
          </h2>
          <p class="section-subtitle">达人分享，实用干货</p>
        </div>
      </div>
      
      <div class="plans-list">
        <div v-for="plan in hotPlans" :key="plan.id" class="plan-card" @click="viewPlan(plan)">
          <div class="plan-image">
            <img :src="plan.cover" :alt="plan.title" />
          </div>
          <div class="plan-content">
            <div class="plan-header">
              <h3 class="plan-title">{{ plan.title }}</h3>
              <el-tag :type="plan.statusType" size="small">{{ plan.statusText }}</el-tag>
            </div>
            <div v-if="plan.destination" class="plan-destination">
              <el-icon><Location /></el-icon>
              <span>{{ plan.destination }}</span>
            </div>
            <p class="plan-desc">{{ plan.description }}</p>
            <div class="plan-meta">
              <div class="author-info">
                <el-avatar 
                  :size="32" 
                  :src="plan.authorAvatar"
                  class="author-avatar"
                  @click.stop="viewUserProfile(plan.authorId)"
                  style="cursor: pointer;"
                >{{ plan.author?.charAt(0) }}</el-avatar>
                <span 
                  class="author-name"
                  @click.stop="viewUserProfile(plan.authorId)"
                  style="cursor: pointer;"
                >{{ plan.author }}</span>
                <span class="author-level" :style="{ 
                  '--level-color-start': plan.levelGradient?.start || plan.levelColor, 
                  '--level-color-end': plan.levelGradient?.end || plan.levelColor 
                }">{{ plan.levelName }}</span>
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
              <div class="plan-stats">
                <span><el-icon><View /></el-icon> {{ formatNumber(plan.viewCount) }}</span>
                <span><el-icon><Star /></el-icon> {{ formatNumber(plan.likeCount) }}</span>
                <span><el-icon><ChatDotRound /></el-icon> {{ formatNumber(plan.commentCount) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      </div>
    </div>

    <!-- 底部信息 -->
    <footer class="dashboard-footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="javascript:void(0)" @click="handleAboutUs">关于我们</a>
          <span class="divider">|</span>
          <a href="javascript:void(0)" @click="handleTerms">服务条款</a>
          <span class="divider">|</span>
          <a href="javascript:void(0)" @click="handlePrivacy">隐私保护声明</a>
          <span class="divider">|</span>
          <a href="javascript:void(0)" @click="handleContact">联系我们</a>
          <span class="divider">|</span>
          <a href="javascript:void(0)" @click="handleFeedback">用户反馈</a>
        </div>
        <div class="footer-info">
          <p>{{ footerDescription }}</p>
          <p>本网站内容适合18岁以上用户使用，为了您的健康，请合理安排时间</p>
        </div>
      </div>
    </footer>

    <!-- 右侧固定导航条 -->
    <div class="side-nav">
      <div class="nav-item" @click="scrollToSection('carousel')">
        <el-icon><Picture /></el-icon>
        <span class="nav-text">精选推荐</span>
      </div>
      <div class="nav-item" @click="scrollToSection('welcome')">
        <el-icon><UserFilled /></el-icon>
        <span class="nav-text">欢迎区</span>
      </div>
      <div class="nav-item" @click="scrollToSection('features')">
        <el-icon><Grid /></el-icon>
        <span class="nav-text">核心功能</span>
      </div>
      <div class="nav-item" @click="scrollToSection('myspace')">
        <el-icon><User /></el-icon>
        <span class="nav-text">个人中心</span>
      </div>
      <div class="nav-item" @click="scrollToSection('level-guide')">
        <el-icon><TrophyBase /></el-icon>
        <span class="nav-text">升级指南</span>
      </div>
      <div class="nav-item" @click="scrollToSection('culture')">
        <el-icon><Shop /></el-icon>
        <span class="nav-text">文旅体验</span>
      </div>
      <div class="nav-item" @click="scrollToSection('recommendations')">
        <el-icon><LocationFilled /></el-icon>
        <span class="nav-text">为你推荐</span>
      </div>
      <div class="nav-item" @click="scrollToSection('plans')">
        <el-icon><Document /></el-icon>
        <span class="nav-text">热门攻略</span>
      </div>
    </div>

    <!-- 位置选择对话框 -->
    <el-dialog
      v-model="showLocationSelector"
      title="选择位置"
      width="640px"
      :close-on-click-modal="false"
      class="location-selector-dialog"
    >
      <div class="location-warning">
        <p class="location-warning-title">选择位置前请了解：</p>
        <ul class="location-warning-list">
          <li>请输入准确的城市名称，如：南宁、北京、上海等，系统将自动获取该城市的天气信息。</li>
          <li>区/县为可选信息，填写后可以获取更精确的位置信息。</li>
          <li>如果输入的城市名称无法识别，请尝试使用城市全称（如"南宁市"）或联系管理员。</li>
        </ul>
      </div>
      <div class="location-divider"></div>
      <div class="location-selector-content">
        <el-form :model="locationForm" label-width="80px" label-position="top" :hide-required-asterisk="true">
          <el-form-item label="市">
            <el-input
              v-model="locationForm.city"
              placeholder="请输入城市名称，如：南宁、北京、上海"
              clearable
              size="large"
              @blur="validateCityInput"
            />
          </el-form-item>
          
          <el-form-item label="区">
            <el-input
              v-model="locationForm.district"
              placeholder="请输入区/县名称，如：青秀区、西乡塘区（可选）"
              clearable
              size="large"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="location-dialog-footer">
          <el-button class="location-dialog-btn" @click="showLocationSelector = false">取消</el-button>
          <el-button class="location-dialog-btn" @click="confirmLocationSelection" :loading="locationLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户协议对话框 -->
    <AgreementDialog v-model="showUserAgreementDialog" type="user" />
    
    <!-- 隐私政策对话框 -->
    <AgreementDialog v-model="showPrivacyDialog" type="privacy" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Trophy, ArrowRight, Star, Location, View, ChatDotRound, 
  Document, User, Shop, UserFilled, InfoFilled, DataAnalysis,
  TrendCharts, LocationFilled, MagicStick, Top, TrophyBase, Picture, Grid,
  Edit, Search, Clock, Medal, Aim, StarFilled
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElLoading } from 'element-plus'
import { getLevelByPoints } from '@/utils/level'
import { getUserPortrait } from '@/api/user'
import { getCurrentUserInfo, getCurrentUserId } from '@/utils/user'
import AgreementDialog from '@/components/AgreementDialog.vue'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'
import { getHotProjects, getProjectList } from '@/api/cultureProject'
import { getHotExperiences } from '@/api/cultureExperience'
import { processImageUrl, processImageUrls } from '@/utils/image'

const router = useRouter()
const systemStore = useSystemStore()
const { footerDescription, contactMessage, contactEmail, aboutMessage } = storeToRefs(systemStore)

// 用户信息
const userInfo = ref({
  nickname: '旅行达人',
  avatar: '',
  level: 3,
  points: 1250
})

// 用户画像数据
const userPortrait = ref({
  browseCount: 0,
  favoriteCount: 0,
  searchCount: 0,
  favoriteType: '自然风光',
  consumptionLevel: '',
  travelMode: '',
  avgStayTime: 0,
  tags: ['美食爱好者', '文化旅游', '摄影达人']
})

const totalPoints = ref(1250)
const levelName = ref('黄金游侠')
const levelColor = ref('#FFD700')

// 问候语（根据当前时间准确获取）
const greeting = computed(() => {
  const hour = new Date().getHours()
  // 凌晨：0:00-5:59
  if (hour >= 0 && hour < 6) {
    return '凌晨好'
  }
  // 早上：6:00-8:59
  if (hour >= 6 && hour < 9) {
    return '早上好'
  }
  // 上午：9:00-11:59
  if (hour >= 9 && hour < 12) {
    return '上午好'
  }
  // 中午：12:00-13:59
  if (hour >= 12 && hour < 14) {
    return '中午好'
  }
  // 下午：14:00-17:59
  if (hour >= 14 && hour < 18) {
    return '下午好'
  }
  // 晚上：18:00-21:59
  if (hour >= 18 && hour < 22) {
    return '晚上好'
  }
  // 深夜：22:00-23:59
  if (hour >= 22 && hour < 24) {
    return '夜深了'
  }
  // 默认（理论上不会执行到这里）
  return '你好'
})


// 天气信息（初始为空，等待加载）
const weather = ref({
  temperature: null,
  weather: '',
  location: '',
  humidity: null,
  windSpeed: null,
  aqi: '',
  aqiLevel: '',
  uvIndex: ''
})

// 天气加载状态
const weatherLoading = ref(false)

// 位置选择相关
const showLocationSelector = ref(false)
const locationLoading = ref(false)
const locationForm = ref({
  city: '',
  district: ''
})

// 验证城市输入
const validateCityInput = (): boolean => {
  if (!locationForm.value.city || !locationForm.value.city.trim()) {
    ElMessage.warning('请输入城市名称')
    return false
  }
  return true
}

// 确认位置选择
const confirmLocationSelection = async () => {
  // 使用自定义验证函数
  if (!validateCityInput()) {
    return
  }
  
  // 构建位置字符串：如果有区就组合成"城市·区"，否则只用城市
  const city = locationForm.value.city.trim()
  const district = locationForm.value.district?.trim() || ''
  const locationText = district ? `${city}·${district}` : city
  
  locationLoading.value = true
  try {
    // 保存选择的位置
    localStorage.setItem('selected_weather_city', locationText)
    localStorage.setItem('last_location_update', Date.now().toString())
    
    // 根据选择的位置获取天气
    const success = await getWeatherByCity(locationText)
    
    if (success) {
      ElMessage.success(`已切换到${locationText}的天气`)
      showLocationSelector.value = false
      // 清空表单
      locationForm.value = {
        city: '',
        district: ''
      }
    } else {
      // getWeatherByCity内部已经显示了错误信息，这里不需要再显示
      // 但可以提示用户检查输入是否正确
      ElMessage.warning('请检查城市和区名称是否正确')
    }
  } catch (error) {
    console.error('切换位置失败:', error)
    ElMessage.error('切换位置失败，请稍后重试')
  } finally {
    locationLoading.value = false
  }
}


const weatherIcon = computed(() => {
  const weatherStr = weather.value.weather
  if (weatherStr.includes('晴')) return '☀️'
  if (weatherStr.includes('多云')) return '⛅'
  if (weatherStr.includes('阴')) return '☁️'
  if (weatherStr.includes('雨')) return '🌧️'
  if (weatherStr.includes('雪')) return '❄️'
  if (weatherStr.includes('雾')) return '🌫️'
  return '🌤️'
})

const weatherTemp = computed(() => {
  if (weather.value.temperature !== null) {
    return weather.value.temperature + '°C'
  }
  if (weatherLoading.value) {
    return '加载中...'
  }
  return '--'
})

const aqiClass = computed(() => {
  return `aqi-${weather.value.aqiLevel}`
})

const weatherAdvice = computed(() => {
  if (!weather.value.weather && !weather.value.temperature) {
    return '正在为您获取天气信息...'
  }
  
  const temp = weather.value.temperature
  const weatherStr = weather.value.weather || ''
  const aqiLevel = weather.value.aqiLevel
  
  // 优先处理特殊天气情况
  if (weatherStr.includes('暴雨') || weatherStr.includes('大暴雨')) {
    return '暴雨天气，建议减少出行，如需外出请带好雨具，注意安全哦 ☔'
  }
  
  if (weatherStr.includes('雷') || weatherStr.includes('雷电')) {
    return '雷雨天气，请避免户外活动，如在户外请寻找安全避雷场所 ⚡'
  }
  
  if (weatherStr.includes('雪')) {
    if (weatherStr.includes('大雪') || weatherStr.includes('暴雪')) {
      return '大雪纷飞，银装素裹很美，但出行要注意保暖防滑，慢行小心 ❄️'
    }
    return '雪花飘落，浪漫唯美，记得添衣保暖，小心路滑 ⛄'
  }
  
  if (weatherStr.includes('雨')) {
    if (weatherStr.includes('小雨')) {
      return '绵绵细雨，诗意朦胧，记得带把小伞，享受雨中漫步的浪漫 🌧️'
    }
    if (weatherStr.includes('中雨')) {
      return '中雨天气，出门记得带伞，小心地滑，注意安全 🌧️'
    }
    return '雨天路滑，出行请带好雨具，注意交通安全 🌂'
  }
  
  if (weatherStr.includes('雾') || weatherStr.includes('霾')) {
    if (aqiLevel === 'bad') {
      return '雾霾天气，空气质量不佳，建议减少户外活动，出门记得佩戴口罩 😷'
    }
    return '大雾天气，能见度较低，出行请小心驾驶，注意安全 🌫️'
  }
  
  if (weatherStr.includes('沙') || weatherStr.includes('尘')) {
    return '沙尘天气，空气质量较差，建议减少外出，如需出门请做好防护 🌪️'
  }
  
  if (weatherStr.includes('阴') || weatherStr.includes('多云转阴')) {
    return '阴云密布，虽然少了阳光，但也别有一番宁静之美，适合室内活动 ☁️'
  }
  
  if (weatherStr.includes('多云') || weatherStr.includes('少云')) {
    if (temp > 28) {
      return '多云天气，温度较高，出门记得防晒和补水，享受舒适的云层遮阳 ☁️'
    }
    if (temp < 15) {
      return '多云天气，温度适宜，微风轻拂，正是出游的好时光 🌤️'
    }
    return '多云天气，云卷云舒，温度适中，适合各种户外活动 ⛅'
  }
  
  // 根据温度处理晴天情况
  if (weatherStr.includes('晴')) {
    if (temp >= 35) {
      return '烈日当空，天气炎热，注意防暑降温，多喝水，尽量避免正午外出 🌞'
    }
    if (temp > 30) {
      return '阳光明媚，温度较高，出门记得防晒，适当补水，享受夏日美好 ☀️'
    }
    if (temp >= 25) {
      return '晴空万里，温度宜人，正是外出游玩的好天气，享受美好时光 🌤️'
    }
    if (temp >= 20) {
      return '天气晴朗，温度舒适，微风和煦，适合各种户外活动，心情也会格外愉悦 ☀️'
    }
    if (temp >= 15) {
      return '阳光正好，温度适中，不冷不热，正是出门散步的好时机 ✨'
    }
    if (temp >= 10) {
      return '晴空万里，略有凉意，记得适当添衣，享受清新的空气和温暖的阳光 🌞'
    }
    if (temp >= 5) {
      return '天气晴朗，但温度较低，注意保暖，享受冬日的暖阳 ☀️'
    }
    return '阳光明媚，但天气较冷，记得多穿衣物，注意防寒保暖 🧥'
  }
  
  // 根据温度的一般性建议
  if (temp >= 35) {
    return '高温天气，请注意防暑，多补充水分，合理安排户外活动时间 🌡️'
  }
  
  if (temp >= 30) {
    return '天气较热，出门记得做好防晒，多喝水，享受夏日的热情 ☀️'
  }
  
  if (temp < 0) {
    return '严寒天气，记得添衣保暖，注意防寒，室内外温差大，小心感冒 🧊'
  }
  
  if (temp < 10) {
    return '天气较冷，记得多穿衣物，注意保暖，一杯热茶会让您更温暖 🧥'
  }
  
  // 空气质量提示
  if (aqiLevel === 'bad') {
    return '空气质量不佳，建议减少户外活动，在室内享受安静时光，注意健康 😷'
  }
  
  if (aqiLevel === 'moderate') {
    return '空气质量一般，敏感人群建议减少户外活动，普通人群正常活动即可 🌬️'
  }
  
  // 默认提示
  return '天气宜人，温度适中，祝您有美好的一天，心情愉悦 ✨'
})

// 轮播图数据
const banners = ref([])

// 加载轮播图数据（从后端获取）
const loadBanners = async () => {
  try {
    const response = await request.get('/user/home/banners', {
      params: { position: 'HOME' }
    })
    if (response.code === 200 && response.data && response.data.length > 0) {
      // 转换后端数据格式为前端需要的格式
      banners.value = response.data.map(banner => ({
        id: banner.id,
        image: banner.imageUrl || banner.image,
        title: banner.title || '',
        subtitle: banner.subtitle || '',
        badge: banner.label || banner.tag || banner.badge || '热门推荐',
        linkType: banner.linkType || 0,
        linkValue: banner.linkValue || '',
        // 兼容旧格式
        type: banner.linkType === 3 ? 'attraction' : (banner.linkType === 4 ? 'plan' : (banner.linkType === 5 ? 'culture' : '')),
        link: getBannerLink(banner.linkType, banner.linkValue)
      }))
    } else {
      // 没有数据时显示空列表
      banners.value = []
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
    // 加载失败时显示空列表
    banners.value = []
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
    default:
      return linkValue
  }
}

// 核心功能
const features = ref([
  {
    icon: MagicStick,
    title: 'AI智能规划',
    desc: '一键生成专属旅行计划',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    path: '/home/user/ai'
  },
  {
    icon: Location,
    title: '景点社区',
    desc: '探索精彩景点社区',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    path: '/home/user/recommendations'
  },
  {
    icon: ChatDotRound,
    title: '攻略社区',
    desc: '探索精彩旅行攻略',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    path: '/home/user/community'
  },
  {
    icon: Shop,
    title: '文旅体验',
    desc: '预订特色文旅项目',
    gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    path: '/home/user/culture'
  },
  {
    icon: DataAnalysis,
    title: '用户画像',
    desc: '查看您的用户画像分析',
    gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
    path: '/home/user/portrait'
  },
  {
    icon: TrophyBase,
    title: '升级指南',
    desc: '了解等级升级规则',
    gradient: 'linear-gradient(135deg, #ffd89b 0%, #19547b 100%)',
    path: '/home/user/level-guide'
  }
])

// 个人中心
const mySpace = ref([
  {
    icon: Document,
    title: '我的攻略',
    desc: '管理你创建的旅行攻略',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    path: '/home/user/plans',
    count: 0, // 待审核攻略数量
    stats: {
      total: 0, // 总攻略数
      views: 0,  // 已发布攻略的总浏览量
      comments: 0 // 已发布攻略的总评论数
    }
  },
  {
    icon: Star,
    title: '我的收藏',
    desc: '收藏的景点和攻略',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    path: '/home/user/collect',
    count: 0,  // 不显示红色圆点，设为0
    stats: {
      total: 0,  // 收藏总数
      views: 0   // 不显示浏览量
    }
  },
  {
    icon: UserFilled,
    title: '个人资料',
    desc: '管理个人信息和设置',
    gradient: 'linear-gradient(135deg, #a8c0ff 0%, #3f2b96 100%)',
    path: '/home/user/profile-edit',
    count: 0,
    stats: {
      total: 0,
      views: 0
    }
  }
])

// 推荐数据
const recommendations = ref([])

// 热门攻略
const hotPlans = ref([])

// 新闻/公告标签页（按公告类型分类）
const newsTabs = ref([
  { key: '1', label: '系统公告', type: 1 },
  { key: '2', label: '活动公告', type: 2 },
  { key: '3', label: '功能更新', type: 3 },
  { key: '4', label: '维护通知', type: 4 }
])
const tabStyleMap = {
  '1': 'tab-system',
  '2': 'tab-activity',
  '3': 'tab-feature',
  '4': 'tab-maintain'
}
const activeNewsTab = ref('1') // 默认选中系统公告

// 新闻列表数据（按类型存储）
const newsList = ref({
  '1': [], // 系统公告
  '2': [], // 活动公告
  '3': [], // 功能更新
  '4': []  // 维护通知
})

// 加载公告数据（按类型加载）
const loadAnnouncements = async () => {
  try {
    // 并行加载所有类型的公告
    const promises = newsTabs.value.map(tab => 
      request.get('/user/announcement/list', {
        params: { type: tab.type, limit: 5 }
      })
    )
    
    const responses = await Promise.all(promises)
    
    responses.forEach((response, index) => {
      const tabKey = newsTabs.value[index].key
      const typeName = newsTabs.value[index].label
      
      if (response.code === 200 && response.data) {
        newsList.value[tabKey] = response.data.map((item) => {
          const date = new Date(item.publishTime || item.createTime)
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          return {
            label: typeName,
            title: item.title,
            date: `${month}-${day}`,
            id: item.id,
            type: item.type
          }
        })
      } else {
        newsList.value[tabKey] = []
      }
    })
  } catch (error) {
    console.error('加载公告失败:', error)
    // 如果加载失败，清空所有数据
    newsTabs.value.forEach(tab => {
      newsList.value[tab.key] = []
    })
  }
}

// 升级指南相关数据
const currentPoints = ref(0)
const currentLevelCode = ref(1)
const currentLevelInfo = ref(null) // 存储当前等级完整信息

const upgradeTips = ref([
  { icon: '📝', title: '完善个人资料', desc: '首次完善获得10积分', points: 10, action: 'profile' },
  { icon: '📅', title: '每日登录签到', desc: '每天可获得5积分', points: 5, action: 'checkin' },
  { icon: '✍️', title: '发布优质攻略', desc: '每篇通过审核获得20积分', points: 20, action: 'create-plan' },
  { icon: '💬', title: '积极互动交流', desc: '每条有效评论获得2积分', points: 2, action: 'community' }
])

// 等级列表（与升级指南保持一致）
const levels = ref([
  { code: 1, name: '青铜旅行者', points: 0, gradient: 'linear-gradient(135deg, #8B7355 0%, #6B5B4F 100%)', color: '#8B7355', icon: 'User' },
  { code: 2, name: '白银探索者', points: 100, gradient: 'linear-gradient(135deg, #9CA3AF 0%, #6B7280 100%)', color: '#9CA3AF', icon: 'Aim' },
  { code: 3, name: '黄金游侠', points: 500, gradient: 'linear-gradient(135deg, #F59E0B 0%, #D97706 100%)', color: '#F59E0B', icon: 'Medal' },
  { code: 4, name: '铂金旅者', points: 2000, gradient: 'linear-gradient(135deg, #6366F1 0%, #4F46E5 100%)', color: '#6366F1', icon: 'Trophy' },
  { code: 5, name: '钻石达人', points: 5000, gradient: 'linear-gradient(135deg, #EC4899 0%, #DB2777 100%)', color: '#EC4899', icon: 'StarFilled' },
  { code: 6, name: '王者导师', points: 10000, gradient: 'linear-gradient(135deg, #F97316 0%, #EA580C 100%)', color: '#F97316', icon: 'TrophyBase' }
])

// 计算当前等级信息
const currentLevelName = computed(() => {
  return currentLevelInfo.value?.name || '青铜旅行者'
})

const currentLevelColor = computed(() => {
  return currentLevelInfo.value?.color || '#8B7355'
})

const currentLevelGradient = computed(() => {
  return currentLevelInfo.value?.gradient || 'linear-gradient(135deg, #8B7355 0%, #6B5B4F 100%)'
})

const currentLevelIcon = computed(() => {
  const level = levels.value.find(l => l.code === currentLevelCode.value)
  return level?.icon || 'User'
})

const nextLevel = computed(() => {
  const current = levels.value.find(l => l.code === currentLevelCode.value)
  if (!current) return null
  const nextIndex = levels.value.findIndex(l => l.code === current.code) + 1
  return nextIndex < levels.value.length ? levels.value[nextIndex] : null
})

const levelProgress = computed(() => {
  if (!nextLevel.value) return 100
  const current = levels.value.find(l => l.code === currentLevelCode.value)
  if (!current || !nextLevel.value) return 0
  const range = nextLevel.value.points - current.points
  const progress = currentPoints.value - current.points
  return Math.min(100, Math.max(0, Math.round((progress / range) * 100)))
})

const progressColor = computed(() => {
  return currentLevelColor.value
})

// 获取新闻列表
const getNewsList = () => {
  return newsList.value[activeNewsTab.value] || []
}

// 公告详情对话框
const announcementDialogVisible = ref(false)
const currentAnnouncement = ref(null)

// 处理新闻点击
const handleNewsClick = async (item) => {
  if (!item.id) {
    return
  }
  
  try {
    // 加载公告详情
    const response = await request.get(`/user/announcement/${item.id}`)
    
    if (response.code === 200 && response.data) {
      currentAnnouncement.value = response.data
      announcementDialogVisible.value = true
    } else {
      ElMessage.error('获取公告详情失败')
    }
  } catch (error) {
    console.error('加载公告详情失败:', error)
    ElMessage.error('加载公告详情失败')
  }
}

// 格式化公告内容（支持换行，段落之间两个字的间距）
const formatContent = (content) => {
  if (!content) return ''
  // 按换行符分割段落，用 <p> 标签包裹，每个段落首行缩进两个字的间距
  const paragraphs = content.split('\n').filter(p => p.trim())
  return paragraphs.map(p => `<p>${p.trim()}</p>`).join('')
}

// 获取公告类型名称
const getAnnouncementTypeName = (type) => {
  const typeMap = {
    1: '系统公告',
    2: '活动公告',
    3: '功能更新',
    4: '维护通知'
  }
  return typeMap[type] || '公告'
}

// 获取公告类型标签样式
const announcementTypeClassMap = {
  1: 'badge-system',
  2: 'badge-activity',
  3: 'badge-feature',
  4: 'badge-maintain'
}

const getAnnouncementTypeClass = (type) => {
  return announcementTypeClassMap[type] || 'badge-system'
}

// 格式化公告日期
const formatAnnouncementDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 处理升级技巧点击
const handleTipAction = (tip) => {
  switch (tip.action) {
    case 'profile':
      navigateTo('/home/user/profile')
      break
    case 'checkin':
      ElMessage.info('签到功能开发中')
      break
    case 'create-plan':
      navigateTo('/home/user/plans/create')
      break
    case 'community':
      navigateTo('/home/user/community')
      break
  }
}

// 景点类型映射（与管理平台创建景点的类型对应）
const getTypeName = function(type) {
  const typeMap = {
    1: '自然风光',
    2: '人文历史',
    3: '主题公园',
    4: '美食街',
    5: '古镇古村',
    6: '温泉度假',
    7: '宗教场所',
    8: '购物娱乐'
  }
  const result = typeMap[type]
  return result || '其他'
}

// 格式化景点数据
const formatAttraction = function(attraction) {
  let image = attraction.image || attraction.coverImage || '/default-attraction.jpg'
  if (!image && attraction.images && Array.isArray(attraction.images) && attraction.images.length > 0) {
            image = attraction.images[0]
          }
  const tags = Array.isArray(attraction.tags) ? attraction.tags : []
  const location = attraction.location || attraction.city || attraction.address || attraction.province || ''
  
  // 直接使用景区级别字段（rating）显示，和详情页保持一致
  const badge = attraction.rating ? attraction.rating + 'A景区' : '热门'
  
  // 处理价格：确保转换为数字，处理BigDecimal类型
  let price = 0
  if (attraction.price !== null && attraction.price !== undefined) {
    price = typeof attraction.price === 'number' ? attraction.price : Number(attraction.price) || 0
  } else if (attraction.ticketPrice !== null && attraction.ticketPrice !== undefined) {
    price = typeof attraction.ticketPrice === 'number' ? attraction.ticketPrice : Number(attraction.ticketPrice) || 0
  }
        
        return {
          id: attraction.id,
          type: 'attraction',
          image: image,
          title: attraction.name || '景点名称',
          location: location,
    rating: attraction.rating || null, // 景区级别（1-5的整数），score是评分（可能是小数）
    price: price,
    views: attraction.views || attraction.viewCount || 0,
    favorites: attraction.favorites || attraction.collectCount || 0,
    collectCount: attraction.favorites || attraction.collectCount || 0,
    comments: attraction.comments || attraction.commentCount || 0,
          tags: tags.length > 0 ? tags : ['自然风光'],
          badge: badge
  }
}

// 加载推荐景点（基于用户画像的智能推荐）
const loadRecommendations = async () => {
  try {
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    if (!userInfo) {
      // 未登录，使用默认推荐
      await loadDefaultRecommendations()
      return
    }
    
    const userId = userInfo.id || userInfo.userId
    
    if (!userId) {
      await loadDefaultRecommendations()
      return
    }
    
    // 使用推荐API（基于用户画像大数据推荐）
    try {
      const recommendResponse = await request.get('/recommendation/attractions', {
        params: {
          limit: 10 // 获取更多数据用于排序
        }
      })
      
      if (recommendResponse.code === 200 && recommendResponse.data) {
        const recommendList = recommendResponse.data || []
        console.log('推荐API返回数据:', recommendList, '数量:', recommendList.length)
        
        // 转换推荐数据格式
        const formattedList = recommendList.map(item => {
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
          
          // 处理价格：确保转换为数字，处理BigDecimal类型
          let price = 0
          if (item.ticketPrice !== null && item.ticketPrice !== undefined) {
            price = typeof item.ticketPrice === 'number' ? item.ticketPrice : Number(item.ticketPrice) || 0
          } else if (item.price !== null && item.price !== undefined) {
            price = typeof item.price === 'number' ? item.price : Number(item.price) || 0
          }
          
          return {
            id: item.id,
            name: item.name || '',
            location: item.location || `${item.city || ''}${item.province ? (item.city ? ' · ' : '') + item.province : ''}`.trim() || '未知',
            city: item.city || '',
            description: item.description || '',
            image: imageUrl,
            typeName: typeNameMap[item.type] || '其他',
            tags: tags,
            price: price,
            views: item.viewCount || item.views || 0,
            favorites: item.collectCount || item.favorites || 0,
            comments: item.commentCount || item.comments || 0,
            isFavorite: item.isFavorite || false,
            rating: item.rating || null // 使用景区级别字段（1-5的整数），不是score
          }
        })
        
        // 如果有多条数据，根据收藏量和浏览量排序，然后只取前6个
        if (formattedList.length > 0) {
          // 按收藏量和浏览量排序（哪个多就优先显示）
          const sortedList = formattedList.sort((a, b) => {
            const aViews = a.views || 0
            const bViews = b.views || 0
            const aFavorites = a.favorites || 0
            const bFavorites = b.favorites || 0
            
            // 计算总热度（浏览量 + 收藏量）
            const aTotal = aViews + aFavorites
            const bTotal = bViews + bFavorites
            
            // 先按总热度排序（降序）
            if (bTotal !== aTotal) {
              return bTotal - aTotal
            }
            
            // 总热度相同，优先显示收藏量多的
            const favoriteDiff = bFavorites - aFavorites
            if (favoriteDiff !== 0) {
              return favoriteDiff
            }
            
            // 收藏量也相同，按浏览量排序（降序）
            return bViews - aViews
          })
          
          // 只取前6个
          recommendations.value = sortedList.slice(0, 6).map(item => formatAttraction(item))
          console.log('✅ 基于用户画像的智能推荐景点（已排序，显示前6个）:', recommendations.value)
        } else {
          // 没有推荐数据，使用默认推荐
          await loadDefaultRecommendations()
        }
        return
      }
    } catch (recommendError) {
      console.warn('推荐API调用失败，降级为默认推荐:', recommendError)
    }
    
    // 降级方案：使用默认推荐
    await loadDefaultRecommendations()
  } catch (error) {
    console.error('加载推荐景点失败:', error)
    await loadDefaultRecommendations()
  }
}

// 默认推荐（无用户画像时使用）
const loadDefaultRecommendations = async () => {
  try {
    const response = await request.get('/user/attraction/list', {
      params: {
        page: 1,
        size: 10, // 获取更多数据用于排序
        status: 1
      }
    })
    
    if (response.code === 200 && response.data?.list) {
      const list = response.data.list || []
      
        // 如果有多条数据，根据收藏量和浏览量排序，然后只取前6个
      if (list.length > 0) {
        // 按收藏量和浏览量排序（哪个多就优先显示）
        const sortedList = list.sort((a, b) => {
          const aViews = a.viewCount || a.views || 0
          const bViews = b.viewCount || b.views || 0
          const aFavorites = a.collectCount || a.favorites || 0
          const bFavorites = b.collectCount || b.favorites || 0
          
          // 计算总热度（浏览量 + 收藏量）
          const aTotal = aViews + aFavorites
          const bTotal = bViews + bFavorites
          
          // 先按总热度排序（降序）
          if (bTotal !== aTotal) {
            return bTotal - aTotal
          }
          
          // 总热度相同，优先显示收藏量多的
          const favoriteDiff = bFavorites - aFavorites
          if (favoriteDiff !== 0) {
            return favoriteDiff
          }
          
          // 收藏量也相同，按浏览量排序（降序）
          return bViews - aViews
        })
        
        // 只取前6个
        recommendations.value = sortedList.slice(0, 6).map(formatAttraction)
        console.log('默认推荐景点数据（已排序，显示前6个）:', recommendations.value)
      } else {
        recommendations.value = []
      }
    }
  } catch (error) {
    console.error('加载默认推荐景点失败:', error)
    recommendations.value = []
  }
}

// 加载热门攻略（使用推荐算法）
const loadHotPlans = async () => {
  try {
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    
    // 使用推荐API（基于用户画像大数据推荐）
    if (userInfo && (userInfo.id || userInfo.userId)) {
      try {
        const recommendResponse = await request.get('/recommendation/plans', {
          params: {
            limit: 10 // 获取更多数据用于排序
          }
        })
        
        if (recommendResponse.code === 200 && recommendResponse.data) {
          let plans = recommendResponse.data || []
          console.log('推荐攻略API返回数据:', plans, '数量:', plans.length)
          
          // 如果推荐算法返回的数据大于6个，按浏览量和收藏量排序（哪个多就优先显示）
          if (plans.length > 6) {
            plans = plans.sort((a, b) => {
              const aViews = a.viewCount || a.views || 0
              const bViews = b.viewCount || b.views || 0
              const aLikes = a.likeCount || a.likes || 0
              const bLikes = b.likeCount || b.likes || 0
              
              // 计算总热度（浏览量 + 收藏量）
              const aTotal = aViews + aLikes
              const bTotal = bViews + bLikes
              
              // 先按总热度排序（降序）
              if (bTotal !== aTotal) {
                return bTotal - aTotal
              }
              
              // 总热度相同，优先显示收藏量多的
              const likeDiff = bLikes - aLikes
              if (likeDiff !== 0) {
                return likeDiff
              }
              
              // 收藏量也相同，按浏览量排序（降序）
              return bViews - aViews
            })
          }
          
          // 只取前6个
          plans = plans.slice(0, 6)
          
          hotPlans.value = plans.map((plan) => {
            // 处理封面图
            let cover = plan.coverImage || ''
            if (!cover && plan.images) {
              if (typeof plan.images === 'string') {
                const imageArray = plan.images.split(',').filter(Boolean)
                if (imageArray.length > 0) {
                  cover = imageArray[0].trim().replace(/["']/g, '')
                }
              } else if (Array.isArray(plan.images) && plan.images.length > 0) {
                cover = plan.images[0]
              }
            }
            if (!cover) {
              cover = '/default-cover.jpg'
            }
            
            // 处理作者信息
            const author = plan.author || plan.username || plan.userName || '匿名用户'
            const authorAvatar = plan.authorAvatar || plan.userAvatar || ''
            
            // 根据作者积分计算等级（统一使用升级指南的计算方式）
            const authorPoints = plan.authorPoints !== undefined && plan.authorPoints !== null ? plan.authorPoints : 0
            const authorLevelInfo = getLevelByPoints(authorPoints)
            
            const levelName = authorLevelInfo.name
            const levelColor = authorLevelInfo.color
            const levelGradient = authorLevelInfo.gradient || { start: authorLevelInfo.color, end: authorLevelInfo.color }
            
            // 处理标签（从tags字段解析）
            let tags = []
            if (plan.tags) {
              if (Array.isArray(plan.tags)) {
                tags = plan.tags
              } else if (typeof plan.tags === 'string' && plan.tags.trim()) {
                tags = plan.tags.split(',').filter(Boolean).map(t => t.trim())
              }
            }
            
            // 根据点赞数和浏览量确定状态标签
            const likeCount = plan.likeCount || 0
            const viewCount = plan.viewCount || 0
            let statusText = '推荐'
            let statusType = 'info'
            
            if (likeCount > 1000 || viewCount > 10000) {
              statusText = '精品'
              statusType = 'danger'
            } else if (likeCount > 500 || viewCount > 5000) {
              statusText = '推荐'
              statusType = 'warning'
            }
            
            return {
              id: plan.id,
              cover: cover,
              title: plan.title || '攻略标题',
              destination: plan.destination || '',
              description: plan.description 
                ? (plan.description.length > 80 ? plan.description.substring(0, 80) + '...' : plan.description)
                : '暂无描述',
              authorId: plan.authorId || plan.userId || plan.author?.id,
              author: author,
              authorAvatar: authorAvatar,
              levelName: levelName,
              levelColor: levelColor,
              levelGradient: levelGradient,
              statusText: statusText,
              statusType: statusType,
              viewCount: plan.viewCount || 0,
              likeCount: likeCount,
              commentCount: plan.commentCount || 0,
              tags: tags
            }
          })
          
          console.log('✅ 基于用户画像的智能推荐攻略（已排序，显示前6个）:', hotPlans.value)
          return
        }
      } catch (recommendError) {
        console.warn('推荐攻略API调用失败，降级为默认推荐:', recommendError)
      }
    }
    
    // 降级方案：使用默认推荐（按浏览量排序）
    const response = await request.get('/travel-plan/list', {
      params: {
        page: 1,
        size: 10, // 获取更多以便排序筛选
        status: 1 // 只获取已发布状态的攻略
      }
    })
    
    if (response.code === 200 && response.data?.list) {
      let plans = response.data.list || []
      
      // 过滤已发布状态并排序（按浏览量和收藏量排序，哪个多就优先显示）
      plans = plans
        .filter((plan) => plan.status === 1)
        .sort((a, b) => {
          const aViews = a.viewCount || 0
          const bViews = b.viewCount || 0
          const aLikes = a.likeCount || 0
          const bLikes = b.likeCount || 0
          
          // 计算总热度（浏览量 + 收藏量）
          const aTotal = aViews + aLikes
          const bTotal = bViews + bLikes
          
          // 先按总热度排序（降序）
          if (bTotal !== aTotal) {
            return bTotal - aTotal
          }
          
          // 总热度相同，优先显示收藏量多的
          const likeDiff = bLikes - aLikes
          if (likeDiff !== 0) {
            return likeDiff
          }
          
          // 收藏量也相同，按浏览量排序（降序）
          return bViews - aViews
        })
        .slice(0, 6) // 只取前6个
      
      hotPlans.value = plans.map((plan) => {
        // 处理封面图
        let cover = plan.coverImage || ''
        if (!cover && plan.images) {
          if (typeof plan.images === 'string') {
            const imageArray = plan.images.split(',').filter(Boolean)
            if (imageArray.length > 0) {
              cover = imageArray[0].trim().replace(/["']/g, '')
            }
          } else if (Array.isArray(plan.images) && plan.images.length > 0) {
            cover = plan.images[0]
          }
        }
        if (!cover) {
          cover = '/default-cover.jpg'
        }
        
        // 处理作者信息
        const author = plan.author || plan.username || plan.userName || '匿名用户'
        const authorAvatar = plan.authorAvatar || plan.userAvatar || ''
        
        // 根据作者积分计算等级（统一使用升级指南的计算方式）
        const authorPoints = plan.authorPoints !== undefined && plan.authorPoints !== null ? plan.authorPoints : 0
        const authorLevelInfo = getLevelByPoints(authorPoints)
        
        const levelName = authorLevelInfo.name
        const levelColor = authorLevelInfo.color
        const levelGradient = authorLevelInfo.gradient || { start: authorLevelInfo.color, end: authorLevelInfo.color }
        
        // 处理标签（从tags字段解析）
        let tags = []
        if (plan.tags) {
          if (Array.isArray(plan.tags)) {
            tags = plan.tags
          } else if (typeof plan.tags === 'string' && plan.tags.trim()) {
            tags = plan.tags.split(',').filter(Boolean).map(t => t.trim())
          }
        }
        
        // 根据点赞数和浏览量确定状态标签
        const likeCount = plan.likeCount || 0
        const viewCount = plan.viewCount || 0
        let statusText = '推荐'
        let statusType = 'info'
        
        if (likeCount > 1000 || viewCount > 10000) {
          statusText = '精品'
          statusType = 'danger'
        } else if (likeCount > 500 || viewCount > 5000) {
          statusText = '推荐'
          statusType = 'warning'
        }
        
        return {
          id: plan.id,
          cover: cover,
          title: plan.title || '攻略标题',
          destination: plan.destination || '',
          description: plan.description 
            ? (plan.description.length > 80 ? plan.description.substring(0, 80) + '...' : plan.description)
            : '暂无描述',
          authorId: plan.authorId || plan.userId || plan.author?.id,
          author: author,
          authorAvatar: authorAvatar,
          levelName: levelName,
          levelColor: levelColor,
          levelGradient: levelGradient,
          statusText: statusText,
          statusType: statusType,
          viewCount: plan.viewCount || 0,
          likeCount: likeCount,
          commentCount: plan.commentCount || 0,
          tags: tags
        }
      })
    }
  } catch (error) {
    console.error('加载热门攻略失败:', error)
    // 如果加载失败，保持空数组或使用默认数据
  }
}

// 文旅项目
const cultureProjects = ref([])

// Footer 链接相关
const showUserAgreementDialog = ref(false)
const showPrivacyDialog = ref(false)

// Footer 链接处理函数
const handleAboutUs = () => {
  ElMessage.info(aboutMessage.value)
}

const handleTerms = () => {
  // 打开用户协议对话框
  showUserAgreementDialog.value = true
}

const handlePrivacy = () => {
  // 打开隐私政策对话框
  showPrivacyDialog.value = true
}

const handleContact = () => {
  ElMessage.info(contactMessage.value)
}

const handleFeedback = () => {
  ElMessage.info(`感谢您的反馈！您可以通过邮箱 ${contactEmail.value} 向我们提出宝贵的意见和建议。`)
}

// 方法
const navigateTo = (path) => {
  console.log('======================')
  console.log('🎯 navigateTo 被调用')
  console.log('📍 目标路径:', path)
  console.log('🔧 当前路由:', router.currentRoute.value.path)
  console.log('======================')
  
  try {
    router.push(path)
    console.log('✅ router.push 执行成功')
  } catch (error) {
    console.error('❌ router.push 执行失败:', error)
  }
}

// 滚动到指定区块
const scrollToSection = (sectionId) => {
  console.log('🎯 点击导航:', sectionId)
  const element = document.getElementById(sectionId)
  
  if (!element) {
    console.error('❌ 未找到元素:', sectionId)
    return
  }
  
  console.log('✅ 找到元素:', element)
  
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
  } else {
    // 兜底方案：使用浏览器原生滚动
    console.log('⚠️ 未找到滚动容器，使用默认滚动')
    element.scrollIntoView({ 
      behavior: 'smooth', 
      block: 'start',
      inline: 'nearest'
    })
  }
}

// 跳转到用户画像页面
const goToPortrait = () => {
  router.push('/home/user/portrait')
}

// 判断轮播图是否可点击
const isBannerClickable = (banner) => {
  return banner.linkType !== 0 && banner.linkType !== undefined && banner.linkType !== null
}

// 处理轮播图点击
const handleBannerClick = async (banner) => {
  // 如果无链接，不处理点击
  if (!isBannerClickable(banner)) {
    return
  }
  
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
  } else if (banner.type && banner.id) {
    // 兼容旧格式
    if (banner.type === 'attraction') {
      router.push(`/home/user/attractions/detail/${banner.id}`)
    } else if (banner.type === 'plan') {
      router.push(`/home/user/plans/detail/${banner.id}`)
    } else if (banner.type === 'culture') {
      router.push(`/home/user/culture/detail/${banner.id}`)
    }
  }
}

const viewDetail = (item) => {
  // 判断类型跳转到对应详情页
  if (item.type === 'attraction' || item.badge === '景点推荐') {
    router.push(`/home/user/attractions/detail/${item.id}`)
  } else if (item.type === 'plan') {
    router.push(`/home/user/plans/detail/${item.id}`)
  } else if (item.type === 'culture') {
    router.push(`/home/user/culture/detail/${item.id}`)
  } else {
    // 默认跳转到景点详情
    router.push(`/home/user/attractions/detail/${item.id}`)
  }
}

const viewPlan = (plan) => {
  router.push(`/home/user/plans/detail/${plan.id}`)
}

// 查看用户主页
const viewUserProfile = (userId) => {
  if (!userId) {
    ElMessage.warning('用户ID不存在')
    return
  }
  router.push(`/home/user/profile/${userId}`)
}

const viewCulture = (item) => {
  // 根据不同类型跳转到不同页面
  if (item.itemType === 'homestay') {
    router.push(`/home/user/culture/homestay/${item.id}`)
  } else if (item.itemType === 'service') {
    router.push(`/home/user/culture/service/${item.id}`)
  } else if (item.itemType === 'experience') {
    router.push(`/home/user/culture/experience/${item.id}`)
  } else if (item.itemType === 'attraction') {
    // 特色周边跳转到产品详情页
    if (item.typeName === '特色周边') {
      router.push(`/home/user/culture/product/${item.id}`)
    } else {
      router.push(`/home/user/culture/attraction/${item.id}`)
    }
  } else if (item.itemType === 'product') {
    // 农特产品跳转到农特产品详情页，其他产品跳转到产品详情页
    if (item.typeName === '农特产品') {
      router.push(`/home/user/culture/agri-product/${item.id}`)
    } else {
      router.push(`/home/user/culture/product/${item.id}`)
    }
  } else if (item.itemType === 'project') {
    router.push(`/home/user/culture/project/${item.id}`)
  } else {
    router.push(`/home/user/culture/detail/${item.id}`)
  }
}

// 解析图片字段
const parseImage = (item: any) => {
  if (item.image) return processImageUrl(item.image)
  if (item.cover) return processImageUrl(item.cover)
  if (item.coverImage) return processImageUrl(item.coverImage)
  
  if (item.images) {
    const processedImages = processImageUrls(item.images)
    if (processedImages.length > 0) {
      return processedImages[0]
    }
  }
  
  return ''
}

// 解析标签/特性字段
const parseFeatures = (item: any, fieldNames: string[] = ['features', 'highlightTags', 'tags']) => {
  for (const fieldName of fieldNames) {
    if (!item[fieldName]) continue
    
    if (Array.isArray(item[fieldName])) {
      // 如果是对象数组，提取title或直接使用字符串
      return item[fieldName].slice(0, 3).map((f: any) => {
        if (typeof f === 'string') return f
        if (f && typeof f === 'object' && f.title) return f.title
        return String(f)
      })
    }
    
    if (typeof item[fieldName] === 'string') {
      try {
        const parsed = JSON.parse(item[fieldName])
        if (Array.isArray(parsed)) {
          // 如果是对象数组，提取title或直接使用字符串
          return parsed.slice(0, 3).map((f: any) => {
            if (typeof f === 'string') return f
            if (f && typeof f === 'object' && f.title) return f.title
            return String(f)
          })
        }
      } catch (e) {
        // 如果不是JSON，可能是逗号分隔的字符串
        const features = item[fieldName].split(',').filter((f: string) => f.trim())
        return features.slice(0, 3)
      }
    }
  }
  
  return []
}

// 解析文化体验的标签（特殊处理，features可能是包含emoji和title的对象数组）
const parseExperienceFeatures = (item: any) => {
  if (!item.features) return []
  
  try {
    let features = item.features
    if (typeof features === 'string') {
      features = JSON.parse(features)
    }
    
    if (Array.isArray(features)) {
      // 提取title字段，如果没有title则使用整个对象转字符串
      return features.slice(0, 3).map((f: any) => {
        if (typeof f === 'string') return f
        if (f && typeof f === 'object') {
          // 优先使用title，其次使用description，最后使用整个对象
          return f.title || f.description || (f.emoji ? `${f.emoji} ${f.title || ''}` : String(f))
        }
        return String(f)
      }).filter((f: string) => f && f.trim())
    }
  } catch (e) {
    // 如果解析失败，尝试作为逗号分隔的字符串处理
    if (typeof item.features === 'string') {
      const features = item.features.split(',').filter((f: string) => f.trim())
      return features.slice(0, 3)
    }
  }
  
  return []
}

// 加载文旅体验数据
const loadCultureProjects = async () => {
  try {
    const projects = []
    
    // 1. 农家乐（热度最高 - 按浏览量排序）
    try {
      const serviceRes = await request.get('/culture/services/page', {
        params: { page: 1, size: 10 }
      })
      console.log('农家乐API响应:', serviceRes)
      if (serviceRes.code === 200) {
        // Page对象可能包含records或list字段
        const list = serviceRes.data?.records || serviceRes.data?.list || []
        if (list.length > 0) {
          // 按热度排序（浏览量+订单量）
          const sorted = [...list].sort((a: any, b: any) => {
            const aHot = (a.views || a.viewCount || 0) + (a.orderCount || 0)
            const bHot = (b.views || b.viewCount || 0) + (b.orderCount || 0)
            return bHot - aHot
          })
          const item = sorted[0]
          console.log('农家乐数据:', item)
          projects.push({
            id: item.id,
            name: item.title || item.name || '农家乐服务',
            location: item.location || '',
            image: parseImage(item),
            typeName: '农家乐',
            features: parseFeatures(item, ['features', 'highlightTags']),
            itemType: 'service'
          })
        }
      }
    } catch (e) {
      console.error('加载农家乐失败:', e)
    }
    
    // 2. 民宿（热度最高 - 按浏览量排序）
    try {
      const homestayRes = await request.get('/culture/homestays/page', {
        params: { page: 1, size: 10 }
      })
      console.log('民宿API响应:', homestayRes)
      if (homestayRes.code === 200) {
        // Page对象可能包含records或list字段
        const list = homestayRes.data?.records || homestayRes.data?.list || []
        if (list.length > 0) {
          // 按热度排序
          const sorted = [...list].sort((a: any, b: any) => {
            const aHot = (a.views || a.viewCount || 0) + (a.orderCount || 0)
            const bHot = (b.views || b.viewCount || 0) + (b.orderCount || 0)
            return bHot - aHot
          })
          const item = sorted[0]
          console.log('民宿数据:', item)
          projects.push({
            id: item.id,
            name: item.title || item.name || '特色民宿',
            location: item.location || '',
            image: parseImage(item),
            typeName: '民宿',
            features: parseFeatures(item, ['features', 'highlightTags']),
            itemType: 'homestay'
          })
        }
      }
    } catch (e) {
      console.error('加载民宿失败:', e)
    }
    
    // 3. 农特产品（从culture/products获取，productType=4）
    try {
      const productRes = await request.get('/culture/products/page', {
        params: { page: 1, size: 20, type: 4 }
      })
      console.log('农特产品API响应:', productRes)
      
      if (productRes.code === 200) {
        // 接口返回的是 Page 对象，数据在 records 字段中
        const list = productRes.data?.records || productRes.data?.list || []
        console.log('农特产品列表:', list, '数量:', list.length)
        if (list.length > 0) {
          // 按销量和评分排序
          const sorted = [...list].sort((a: any, b: any) => {
            const aHot = (a.salesCount || 0) + (a.reviewCount || 0) * 10
            const bHot = (b.salesCount || 0) + (b.reviewCount || 0) * 10
            return bHot - aHot
          })
          const item = sorted[0]
          console.log('农特产品数据:', item)
          projects.push({
            id: item.id,
            name: item.productName || '农特产品',
            location: item.origin || '',
            image: parseImage(item),
            typeName: '农特产品',
            features: parseFeatures(item, ['features', 'highlights', 'badge']),
            itemType: 'product'
          })
        } else {
          console.warn('农特产品列表为空')
        }
      } else {
        console.warn('农特产品API返回错误:', productRes)
      }
    } catch (e) {
      console.error('加载农特产品失败:', e)
      console.error('错误详情:', e.response || e.message)
    }
    
    // 4. 政策对接（type=3，热度最高 - 按浏览量排序）
    try {
      // 先尝试获取status=1的数据，如果没有再获取所有状态的数据
      let projectRes = await getProjectList({ page: 1, size: 20, type: 3, status: 1 })
      console.log('政策对接API响应(status=1):', projectRes)
      
      // 如果status=1没有数据，尝试获取所有状态的数据
      if (projectRes.code === 200 && (!projectRes.data?.list || projectRes.data.list.length === 0)) {
        projectRes = await getProjectList({ page: 1, size: 20, type: 3 })
        console.log('政策对接API响应(所有状态):', projectRes)
      }
      
      if (projectRes.code === 200) {
        const list = projectRes.data?.list || []
        console.log('政策对接列表:', list, '数量:', list.length)
        if (list.length > 0) {
          // 按热度排序，优先显示status=1的
          const sorted = [...list].sort((a: any, b: any) => {
            // 优先显示status=1的
            if (a.status === 1 && b.status !== 1) return -1
            if (a.status !== 1 && b.status === 1) return 1
            // 然后按热度排序
            const aHot = (a.viewCount || 0) + (a.orderCount || 0)
            const bHot = (b.viewCount || 0) + (b.orderCount || 0)
            return bHot - aHot
          })
          const item = sorted[0]
          console.log('政策对接数据:', item)
          projects.push({
            id: item.id,
            name: item.name || '政策对接项目',
            location: item.location || item.region || '',
            image: parseImage(item),
            typeName: '政策对接',
            features: parseFeatures(item, ['tags']),
            itemType: 'project'
          })
        } else {
          console.warn('政策对接列表为空')
        }
      } else {
        console.warn('政策对接API返回错误:', projectRes)
      }
    } catch (e) {
      console.error('加载政策对接失败:', e)
    }
    
    // 5. 文化体验（热度最高）
    try {
      const experienceRes = await getHotExperiences(10)
      if (experienceRes.code === 200 && experienceRes.data?.length > 0) {
        // 按热度排序（如果API没有排序）
        const sorted = [...experienceRes.data].sort((a: any, b: any) => {
          const aHot = (a.viewCount || 0) + (a.orderCount || 0)
          const bHot = (b.viewCount || 0) + (b.orderCount || 0)
          return bHot - aHot
        })
        const item = sorted[0]
        // 解析文化体验的标签，features可能是对象数组，需要提取title
        const experienceFeatures = parseExperienceFeatures(item)
        projects.push({
          id: item.id,
          name: item.name || '文化体验',
          location: item.location || '',
          image: parseImage(item),
          typeName: '文化体验',
          features: experienceFeatures,
          itemType: 'experience'
        })
      }
    } catch (e) {
      console.warn('加载文化体验失败:', e)
    }
    
    // 6. 特色周边（热度最高 - 按浏览量排序）
    try {
      const attractionRes = await request.get('/culture/attractions/page', {
        params: { page: 1, size: 10 }
      })
      console.log('特色周边API响应:', attractionRes)
      if (attractionRes.code === 200) {
        // Page对象可能包含records或list字段
        const list = attractionRes.data?.records || attractionRes.data?.list || []
        if (list.length > 0) {
          // 按热度排序
          const sorted = [...list].sort((a: any, b: any) => {
            const aHot = (a.viewCount || 0) + (a.collectCount || 0)
            const bHot = (b.viewCount || 0) + (b.collectCount || 0)
            return bHot - aHot
          })
          const item = sorted[0]
          console.log('特色周边数据:', item)
          projects.push({
            id: item.id,
            name: item.name || '特色周边',
            location: item.city || item.location || '',
            image: parseImage(item),
            typeName: '特色周边',
            features: parseFeatures(item, ['tags']),
            itemType: 'attraction'
          })
        }
      }
    } catch (e) {
      console.error('加载特色周边失败:', e)
    }
    
    console.log('✅ 文旅体验数据加载完成，共', projects.length, '条')
    console.log('数据详情:', projects)
    
    // 如果数据不足6个，用空数据填充
    while (projects.length < 6) {
      projects.push({
        id: `placeholder-${projects.length}`,
        name: '暂无数据',
        location: '',
        image: '',
        typeName: '',
        features: [],
        itemType: '',
        isPlaceholder: true
      })
    }
    
    cultureProjects.value = projects.slice(0, 6)
  } catch (error) {
    console.error('加载文旅体验数据失败:', error)
    ElMessage.error('加载文旅体验数据失败')
  }
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 优先从当前标签页获取用户信息
    const userData = getCurrentUserInfo()
    if (userData) {
      userInfo.value = {
        nickname: userData.nickname || userData.username || '旅行者',
        avatar: userData.avatar || '',
        level: userData.level || 1,
        points: userData.points || 0
      }
    }
    
    // 从后端获取真实的用户信息和等级
    try {
      const response = await request.get('/user/info')
      if (response.code === 200 && response.data) {
        const backendUser = response.data
        userInfo.value = {
          nickname: backendUser.nickname || backendUser.username || '旅行者',
          avatar: backendUser.avatar || '',
          level: backendUser.levelId || backendUser.level || 1,
          points: backendUser.points || 0
        }
        
        // 尝试获取积分和等级信息
        const userId = backendUser.id
        if (userId) {
          try {
            const pointsResponse = await request.get('/user/points/my', {
              params: { userId }
            })
            if (pointsResponse.code === 200 && pointsResponse.data) {
              const pointsData = pointsResponse.data
              const currentLevel = pointsData.currentLevel
              const userPoints = pointsData.userPoints
              
              // 统一使用积分计算等级（根据升级指南）
                totalPoints.value = userPoints?.totalPoints || 0
              const levelInfo = getLevelByPoints(totalPoints.value)
                levelName.value = levelInfo.name
                levelColor.value = levelInfo.color
            }
          } catch (error) {
            console.warn('获取积分信息失败，使用默认等级:', error)
            // 降级方案：根据积分计算等级（如果没有积分，默认0积分=青铜旅行者）
            const points = backendUser.points || 0
            const levelInfo = getLevelByPoints(points)
            levelName.value = levelInfo.name
            levelColor.value = levelInfo.color
            totalPoints.value = points
          }
        }
      }
    } catch (error) {
      console.warn('从后端获取用户信息失败，使用localStorage数据:', error)
      // 如果后端获取失败，使用localStorage中的数据，根据积分计算等级
      if (savedUserInfo) {
        const userData = JSON.parse(savedUserInfo)
        const points = userData.points || 0
        const levelInfo = getLevelByPoints(points)
        levelName.value = levelInfo.name
        levelColor.value = levelInfo.color
        totalPoints.value = points
      }
    }
    
    // 加载用户画像数据（基于浏览行为分析）
    loadUserPortrait()
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 默认设置为青铜旅行者（0积分）
    const levelInfo = getLevelByPoints(0)
    levelName.value = levelInfo.name
    levelColor.value = levelInfo.color
    totalPoints.value = 0
  }
}

// 获取用户当前位置（优化版：检查精度，避免低精度定位导致错误）
const getCurrentLocation = () => {
  return new Promise(function(resolve, reject) {
    // 检查浏览器是否支持定位
    if (!navigator.geolocation) {
      const error = new Error('浏览器不支持定位功能')
      error.code = -1
      console.error('❌ 浏览器不支持定位功能')
      reject(error)
      return
    }
    
    // 检查是否在安全上下文（HTTPS或localhost）
    const isSecureContext = location.protocol === 'https:' || 
                           location.hostname === 'localhost' || 
                           location.hostname === '127.0.0.1' ||
                           location.hostname.includes('localhost')
    
    if (!isSecureContext) {
      const error = new Error('定位功能需要HTTPS环境（localhost除外）')
      error.code = -2
      console.error('❌ 定位功能需要HTTPS环境:', {
        当前协议: location.protocol,
        当前主机: location.hostname,
        解决方法: '请使用HTTPS或localhost访问'
      })
      reject(error)
      return
    }
    
    // 尝试定位，使用多级降级策略
    let attemptCount = 0
    const maxAttempts = 3  // 增加到3次尝试
    const ACCURACY_THRESHOLD = 3000  // 精度阈值：3000米，超过此值认为精度不足
    
    const tryGetPosition = (useHighAccuracy, previousAccuracy = null) => {
      attemptCount++
      
      const options = {
        enableHighAccuracy: useHighAccuracy,
        // 高精度定位：延长超时时间到30秒，给GPS更多时间
        timeout: useHighAccuracy ? 30000 : 20000,  // 高精度30秒，低精度20秒
        maximumAge: useHighAccuracy ? 0 : 30000  // 低精度时允许使用30秒内的缓存
      }
      
      console.log(`🌐 定位尝试 ${attemptCount}/${maxAttempts}:`, {
        高精度: useHighAccuracy,
        超时: options.timeout + 'ms',
        选项: options,
        协议: location.protocol,
        主机: location.hostname,
        上次精度: previousAccuracy ? previousAccuracy + '米' : '无'
      })
      
      navigator.geolocation.getCurrentPosition(
        function(position) {
          const accuracy = position.coords.accuracy
          const isHighAccuracyResult = accuracy <= ACCURACY_THRESHOLD
          
          console.log('✅ 定位成功:', {
            尝试次数: attemptCount,
            精度类型: useHighAccuracy ? '高精度' : '低精度',
            实际精度: accuracy + '米',
            精度评估: isHighAccuracyResult ? '✅ 精度良好' : '⚠️ 精度较低（可能是网络定位）',
            纬度: position.coords.latitude,
            经度: position.coords.longitude,
            海拔: position.coords.altitude || '未知',
            海拔精度: position.coords.altitudeAccuracy || '未知',
            方向: position.coords.heading || '未知',
            速度: position.coords.speed || '未知'
          })
          
          // 如果精度太低（可能是网络定位导致不准确），且还有重试机会，尝试重新获取高精度定位
          if (!isHighAccuracyResult && attemptCount < maxAttempts && !useHighAccuracy) {
            console.warn(`⚠️ 定位精度较低（${accuracy}米），可能是网络定位，尝试重新获取高精度定位...`)
            setTimeout(() => {
              tryGetPosition(true, accuracy)
            }, 1000)
            return
          }
          
          // 如果精度仍然很低，提示用户
          if (!isHighAccuracyResult) {
            console.warn(`⚠️ 定位精度较低（${accuracy}米），位置可能不准确，建议手动选择城市`)
            ElMessage.warning({
              message: `定位精度较低（${Math.round(accuracy / 1000)}公里），位置可能不准确，建议手动选择城市`,
              duration: 5000
            })
          }
          
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: accuracy
          })
        },
        function(error) {
          // 增强错误信息
          const enhancedError = {
            code: error.code,
            message: error.message,
            timestamp: new Date().toISOString(),
            attempt: attemptCount,
            useHighAccuracy: useHighAccuracy
          }
          
          // 添加详细的错误描述
          const errorMessages = {
            1: '定位权限被拒绝 - 用户拒绝了定位请求',
            2: '位置信息不可用 - 无法获取位置信息',
            3: '定位请求超时 - 在指定时间内无法获取位置'
          }
          
          enhancedError.description = errorMessages[error.code] || '未知错误'
          
          console.error(`❌ 定位请求失败 (尝试 ${attemptCount}/${maxAttempts}):`, enhancedError)
          
          // 如果是超时错误，根据尝试次数决定策略
          if (error.code === 3) {
            if (attemptCount < maxAttempts && useHighAccuracy) {
              // 高精度超时，尝试低精度（但这是最后手段）
              console.log('🔄 高精度定位超时，尝试使用低精度定位（网络定位）...')
              setTimeout(() => {
                tryGetPosition(false)
              }, 1000)
              return
            } else if (attemptCount < maxAttempts && !useHighAccuracy) {
              // 低精度也超时，再试一次高精度（可能GPS信号恢复了）
              console.log('🔄 低精度定位也超时，再次尝试高精度定位...')
              setTimeout(() => {
                tryGetPosition(true)
              }, 2000)
              return
            }
          }
          
          // 如果是权限问题，提供更详细的帮助信息
          if (error.code === 1) {
            console.log('💡 解决权限问题的步骤:')
            console.log('1. 点击Chrome地址栏左侧的锁图标（或信息图标）')
            console.log('2. 点击"网站设置"')
            console.log('3. 找到"位置"权限')
            console.log('4. 将"阻止"改为"询问"或"允许"')
            console.log('5. 刷新页面')
          }
          
          // 如果是超时且已经尝试了所有次数，尝试使用watchPosition作为最后手段
          if (error.code === 3 && attemptCount >= maxAttempts) {
            console.log('💡 定位超时解决建议:')
            console.log('1. 检查网络连接是否正常')
            console.log('2. 检查系统定位服务是否开启')
            console.log('3. Chrome定位依赖Google服务，如果无法访问外网，定位会失败')
            console.log('4. 如果使用VPN，尝试关闭VPN（可能影响Google服务）')
            console.log('5. 刷新页面重试')
            console.log('6. 如果问题持续，可能是GPS信号弱或定位服务不可用')
            console.log('7. 建议使用IP定位或手动选择城市')
            
            // 尝试使用watchPosition作为最后手段（持续监听位置，可能在某些情况下更可靠）
            console.log('🔄 getCurrentPosition失败，尝试使用watchPosition持续监听位置...')
            tryWatchPosition(resolve, reject)
            return
          }
          
          reject(enhancedError)
        },
        options
      )
    }
    
    // 开始第一次尝试（高精度，给GPS更多时间）
    tryGetPosition(true)
  })
}

// 使用watchPosition持续监听位置（作为getCurrentPosition失败后的备选方案）
const tryWatchPosition = (resolve, reject) => {
  let watchId = null
  let positionCount = 0
  const maxPositions = 3  // 最多接收3个位置更新
  
  const options = {
    enableHighAccuracy: true,
    timeout: 20000,  // 20秒超时
    maximumAge: 0
  }
  
  console.log('📡 使用watchPosition持续监听位置变化...')
  
  watchId = navigator.geolocation.watchPosition(
    function(position) {
      positionCount++
      const accuracy = position.coords.accuracy
      
      console.log(`📡 watchPosition收到位置更新 ${positionCount}:`, {
        精度: accuracy + '米',
        纬度: position.coords.latitude,
        经度: position.coords.longitude
      })
      
      // 如果精度足够好，或者已经收到3个位置更新，使用第一个精度最好的
      if (accuracy <= 1000 || positionCount >= maxPositions) {
        // 停止监听
        if (watchId !== null) {
          navigator.geolocation.clearWatch(watchId)
          watchId = null
        }
        
        console.log('✅ watchPosition定位成功:', {
          精度: accuracy + '米',
          位置更新次数: positionCount
        })
        
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          accuracy: accuracy
        })
      }
    },
    function(error) {
      // 停止监听
      if (watchId !== null) {
        navigator.geolocation.clearWatch(watchId)
        watchId = null
      }
      
      console.error('❌ watchPosition也失败:', error)
      reject({
        code: error.code,
        message: error.message,
        description: 'Chrome定位服务可能无法访问Google服务（需要外网），建议使用IP定位或手动选择城市'
      })
    },
    options
  )
  
  // 设置总超时（30秒后如果还没成功，停止监听）
  setTimeout(() => {
    if (watchId !== null) {
      navigator.geolocation.clearWatch(watchId)
      watchId = null
      console.error('❌ watchPosition超时（30秒）')
      reject({
        code: 3,
        message: '定位超时',
        description: 'Chrome定位服务可能无法访问Google服务（需要外网）。建议使用IP定位或手动选择城市'
      })
    }
  }, 30000)
}

// 优化的IP定位方案（尝试多个IP服务，选择最准确的结果）
const tryIpLocationOptimized = async () => {
  try {
    console.log('🌐 尝试使用优化的IP定位获取位置信息...')
    ElMessage.info({
      message: '正在使用IP定位获取位置...',
      duration: 2000
    })
    
    // 尝试多个IP定位服务，选择最准确的结果
    const ipLocationServices = [
      { name: '高德自动检测', url: '/third-party/location/ip', useIp: false },
      { name: '高德IP定位', url: '/third-party/location/ip', useIp: true }
    ]
    
    let bestResult = null
    let bestCity = null
    
    // 先获取用户真实IP
    let userIp = null
    const ipServices = [
      { url: 'https://api.ipify.org?format=json', v4: true },
      { url: 'https://api4.ipify.org?format=json', v4: true },
      { url: 'https://ipapi.co/json/', v4: false }
    ]
    
    for (const service of ipServices) {
      try {
        const ipResponse = await fetch(service.url, { 
          method: 'GET',
          headers: { 'Accept': 'application/json' },
          signal: AbortSignal.timeout(5000)
        })
        
        if (ipResponse.ok) {
          const ipData = await ipResponse.json()
          let ip = ipData.ip || ipData.query || ipData.origin
          
          if (ip && !ip.includes(':') && ip.includes('.')) {
            userIp = ip
            console.log('✅ 获取到用户真实IP (IPv4):', userIp)
            break
          }
        }
      } catch (ipError) {
        console.warn(`⚠️ 从 ${service.url} 获取IP失败:`, ipError)
        continue
      }
    }
    
    // 尝试不同的IP定位服务
    for (const service of ipLocationServices) {
      try {
        const params = service.useIp && userIp ? { ip: userIp } : {}
        console.log(`📡 尝试 ${service.name}，参数:`, params)
        
        const response = await request.get(service.url, { params })
        
        if (response && response.code === 200 && response.data) {
          const data = response.data
          const locationInfo = data.location || {}
          let cityName = data.city || locationInfo.city || ''
          
          if (cityName) {
            cityName = cityName.replace('市', '').replace('省', '').replace('自治区', '').trim()
            
            // 优先选择非"崇左"的城市（如果定位到崇左，可能是IP不准确）
            if (cityName && cityName !== '崇左') {
              console.log(`✅ ${service.name} 获取到城市:`, cityName)
              bestResult = { data, locationInfo, cityName, service: service.name }
              bestCity = cityName
              break // 找到准确的城市，直接使用
            } else if (!bestResult) {
              // 如果定位到崇左，先保存作为备选
              bestResult = { data, locationInfo, cityName, service: service.name }
              bestCity = cityName
            }
          }
        }
      } catch (error) {
        console.warn(`⚠️ ${service.name} 失败:`, error)
        continue
      }
    }
    
    // 如果所有服务都返回崇左，可能是IP确实在崇左，或者IP定位不准确
    if (bestResult && bestCity) {
      const { data, locationInfo, cityName } = bestResult
      const district = locationInfo.district || getCityDistrict(cityName)
      const locationText = district ? `${cityName}·${district}` : cityName
      
      // 保存位置信息
      localStorage.setItem('selected_weather_city', locationText)
      localStorage.setItem('last_location_update', Date.now().toString())
      
      // 转换数据格式以适配前端显示
      weather.value = {
        temperature: Math.round(data.temperature || 25),
        weather: convertWeatherDescription(data.description || '晴天'),
        location: locationText + ' (IP定位)',
        humidity: data.humidity || 60,
        windSpeed: convertWindSpeed(data.windSpeed || 3.5),
        aqi: getAirQuality(data),
        aqiLevel: getAirQualityLevel(data),
        uvIndex: getUVIndex(data)
      }
      
      console.log('✅ 优化的IP定位成功:', locationText)
      
      // 如果定位到崇左，提示用户手动选择
      if (cityName === '崇左') {
        ElMessage.warning({
          message: 'IP定位可能不准确，如果您的实际位置不是崇左，请点击位置旁的编辑按钮手动选择城市',
          duration: 6000,
          showClose: true
        })
      } else {
        ElMessage.success({
          message: `已通过IP定位获取到 ${locationText} 的天气信息`,
          duration: 3000
        })
      }
      
      weatherLoading.value = false
      return true
    }
    
    return false
  } catch (error) {
    console.error('❌ 优化的IP定位失败:', error)
    return false
  }
}

// IP定位备用方案（不依赖浏览器定位，适合网络受限环境）
const tryIpLocation = async () => {
  try {
    console.log('🌐 尝试使用IP定位获取位置信息...')
    ElMessage.info({
      message: '浏览器定位失败，正在使用IP定位...',
      duration: 2000
    })
    
    // 先在前端获取用户真实公网IP（优先获取IPv4）
    let userIp = null
    const ipServices = [
      { url: 'https://api.ipify.org?format=json', v4: true },
      { url: 'https://api4.ipify.org?format=json', v4: true }, // 强制IPv4
      { url: 'https://ipapi.co/json/', v4: false },
      { url: 'https://api.ip.sb/ip?format=json', v4: false }
    ]
    
    // 优先尝试IPv4服务
    for (const service of ipServices) {
      try {
        console.log(`🌐 尝试从 ${service.url} 获取用户真实IP地址...`)
        const ipResponse = await fetch(service.url, { 
          method: 'GET',
          headers: { 'Accept': 'application/json' },
          signal: AbortSignal.timeout(5000) // 5秒超时
        })
        
        if (ipResponse.ok) {
          const ipData = await ipResponse.json()
          // 不同服务返回格式不同
          let ip = ipData.ip || ipData.query || ipData.origin || ipData.data?.ip
          
          // 检查是否是IPv4（IPv6地址包含冒号）
          const isIPv4 = ip && !ip.includes(':') && ip.includes('.')
          
          if (ip && !ip.includes('127.0.0.1') && !ip.includes('localhost')) {
            // 优先使用IPv4，如果是IPv4或者服务明确支持IPv4
            if (isIPv4 || (service.v4 && ip)) {
              userIp = ip
              console.log('✅ 获取到用户真实IP (IPv4):', userIp)
              break // 获取到IPv4，跳出循环
            } else if (!userIp) {
              // 如果没有IPv4，先保存IPv6作为备用
              userIp = ip
              console.log('⚠️ 获取到IPv6地址，将继续尝试获取IPv4:', userIp)
            }
          }
        }
      } catch (ipError) {
        console.warn(`⚠️ 从 ${service.url} 获取IP失败:`, ipError)
        continue // 尝试下一个服务
      }
    }
    
    // 如果获取到的是IPv6，尝试获取IPv4，或者使用自动检测
    if (userIp && userIp.includes(':')) {
      console.warn('⚠️ 获取到的是IPv6地址，高德API可能不支持，将使用自动检测')
      userIp = null // 不传IP，让高德API自动检测
    }
    
    if (!userIp) {
      console.log('ℹ️ 使用后端自动检测IP（不传递IP参数，让高德API自动检测）')
    }
    
    // 调用后端IP定位接口（如果获取到IPv4，传递IP参数；否则让后端自动检测）
    const params = userIp ? { ip: userIp } : {}
    console.log('📡 调用IP定位接口，参数:', params)
    const response = await request.get('/third-party/location/ip', { params })
    
    console.log('📦 IP定位完整响应:', JSON.stringify(response, null, 2))
    
    if (response && response.code === 200 && response.data) {
      const data = response.data
      const locationInfo = data.location || {}
      
      // 提取城市信息（从多个可能的位置）
      let cityName = data.city || locationInfo.city || ''
      
      // 如果还是没有，尝试从locationInfo的其他字段获取
      if (!cityName && locationInfo) {
        cityName = locationInfo.city || locationInfo.province || ''
      }
      
      // 处理城市名格式（可能包含"市"、"省"后缀）
      if (cityName) {
        cityName = cityName.replace('市', '').replace('省', '').replace('自治区', '').replace('特别行政区', '').trim()
      }
      
      console.log('🔍 提取的城市名:', {
        cityName,
        dataCity: data.city,
        locationCity: locationInfo.city,
        locationProvince: locationInfo.province,
        locationAdcode: locationInfo.adcode,
        fullData: data,
        fullLocation: locationInfo
      })
      
      // 如果提取到的城市名是"重庆"，可能是IP定位不准确，尝试重新获取
      if (cityName === '重庆' && userIp) {
        console.warn('⚠️ IP定位结果为重庆，可能是IP不准确，尝试使用高德API自动检测')
        // 不传递IP参数，让高德API自动检测
        const autoResponse = await request.get('/third-party/location/ip')
        if (autoResponse && autoResponse.code === 200 && autoResponse.data) {
          const autoData = autoResponse.data
          const autoLocationInfo = autoData.location || {}
          const autoCityName = autoData.city || autoLocationInfo.city || autoLocationInfo.province || ''
          if (autoCityName && autoCityName !== '重庆') {
            console.log('✅ 使用自动检测获取到更准确的城市:', autoCityName)
            cityName = autoCityName.replace('市', '').replace('省', '').trim()
            // 更新数据
            Object.assign(data, autoData)
            Object.assign(locationInfo, autoLocationInfo)
          }
        }
      }
      
      if (cityName) {
        // 如果IP定位到崇左，可能是精度问题，尝试再次使用浏览器定位获取更准确位置
        if (cityName === '崇左' || cityName === '崇左市') {
          console.log('⚠️ IP定位到崇左，可能是精度问题，尝试使用浏览器定位获取更准确位置...')
          try {
            const location = await getCurrentLocation()
            console.log('✅ 浏览器定位成功，使用高德逆地理编码获取准确城市...')
            
            // 使用高德逆地理编码API获取准确城市
            const geoResponse = await request.get('/third-party/weather/location', {
              params: {
                longitude: location.longitude,
                latitude: location.latitude
              }
            })
            
            if (geoResponse && geoResponse.code === 200 && geoResponse.data) {
              const geoData = geoResponse.data
              const geoLocationInfo = geoData.location || {}
              const geoCityName = geoData.city || geoLocationInfo.city || ''
              
              if (geoCityName && geoCityName !== '崇左' && geoCityName !== '崇左市') {
                console.log('✅ 使用高德逆地理编码获取到更准确的城市:', geoCityName)
                
                // 使用高德逆地理编码的结果
                const district = geoLocationInfo.district || getCityDistrict(geoCityName)
                const locationText = district ? `${geoCityName}·${district}` : geoCityName
                
                localStorage.setItem('selected_weather_city', locationText)
                localStorage.setItem('last_location_update', Date.now().toString())
                
                weather.value = {
                  temperature: Math.round(geoData.temperature || 25),
                  weather: convertWeatherDescription(geoData.description || '晴天'),
                  location: locationText,
                  humidity: geoData.humidity || 60,
                  windSpeed: convertWindSpeed(geoData.windSpeed || 3.5),
                  aqi: getAirQuality(geoData),
                  aqiLevel: getAirQualityLevel(geoData),
                  uvIndex: getUVIndex(geoData)
                }
                
                console.log('✅ 使用高德逆地理编码成功:', locationText)
                ElMessage.success({
                  message: `已通过高德定位获取到 ${locationText} 的天气信息`,
                  duration: 3000
                })
                weatherLoading.value = false
                return true
              }
            }
          } catch (geoError) {
            console.warn('⚠️ 浏览器定位失败，继续使用IP定位结果:', geoError)
          }
        }
        
        const district = locationInfo.district || getCityDistrict(cityName)
        const locationText = district ? `${cityName}·${district}` : cityName
        
        // 保存位置信息
        localStorage.setItem('selected_weather_city', locationText)
        localStorage.setItem('last_location_update', Date.now().toString())
        
        // 转换数据格式以适配前端显示
        weather.value = {
          temperature: Math.round(data.temperature || 25),
          weather: convertWeatherDescription(data.description || '晴天'),
          location: locationText + ' (IP定位)',
          humidity: data.humidity || 60,
          windSpeed: convertWindSpeed(data.windSpeed || 3.5),
          aqi: getAirQuality(data),
          aqiLevel: getAirQualityLevel(data),
          uvIndex: getUVIndex(data)
        }
        
        console.log('✅ IP定位成功:', locationText)
        ElMessage.success({
          message: `已通过IP定位获取到 ${locationText} 的天气信息`,
          duration: 3000
        })
        weatherLoading.value = false
        return true
      } else {
        console.warn('⚠️ IP定位返回成功，但无法提取城市名:', {
          data: data,
          locationInfo: locationInfo,
          可能原因: 'IP定位API返回空数据，可能是IPv6地址或IP不准确'
        })
        
        // 如果IP定位返回空数据，尝试使用自动检测（不传IP）
        if (userIp) {
          console.log('🔄 IP定位返回空数据，尝试使用自动检测（不传IP参数）...')
          try {
            const autoResponse = await request.get('/third-party/location/ip')
            if (autoResponse && autoResponse.code === 200 && autoResponse.data) {
              const autoData = autoResponse.data
              const autoLocationInfo = autoData.location || {}
              const autoCityName = autoData.city || autoLocationInfo.city || autoLocationInfo.province || ''
              
              if (autoCityName) {
                const processedCityName = autoCityName.replace('市', '').replace('省', '').trim()
                if (processedCityName) {
                  console.log('✅ 使用自动检测获取到城市:', processedCityName)
                  // 使用自动检测的结果
                  Object.assign(data, autoData)
                  Object.assign(locationInfo, autoLocationInfo)
                  cityName = processedCityName
                  
                  // 继续处理显示逻辑
                  const district = locationInfo.district || getCityDistrict(cityName)
                  const locationText = district ? `${cityName}·${district}` : cityName
                  
                  localStorage.setItem('selected_weather_city', locationText)
                  localStorage.setItem('last_location_update', Date.now().toString())
                  
                  weather.value = {
                    temperature: Math.round(data.temperature || 25),
                    weather: convertWeatherDescription(data.description || '晴天'),
                    location: locationText + ' (IP定位)',
                    humidity: data.humidity || 60,
                    windSpeed: convertWindSpeed(data.windSpeed || 3.5),
                    aqi: getAirQuality(data),
                    aqiLevel: getAirQualityLevel(data),
                    uvIndex: getUVIndex(data)
                  }
                  
                  console.log('✅ IP定位（自动检测）成功:', locationText)
                  ElMessage.success({
                    message: `已通过IP定位获取到 ${locationText} 的天气信息`,
                    duration: 3000
                  })
                  weatherLoading.value = false
                  return true
                }
              }
            }
          } catch (autoError) {
            console.error('自动检测也失败:', autoError)
          }
        }
      }
    }
    
    console.warn('⚠️ IP定位失败，响应:', response)
    return false
  } catch (error) {
    console.error('❌ IP定位失败:', error)
    ElMessage.error({
      message: 'IP定位失败: ' + (error.message || '未知错误'),
      duration: 3000
    })
    return false
  }
}

// 根据城市获取天气信息（自动调用，不对外暴露）
const getWeatherByCity = async (cityLocation) => {
  if (!cityLocation) {
    console.warn('城市名称不能为空')
    weatherLoading.value = false
    return false
  }
  
  weatherLoading.value = true
  
  try {
    // 解析城市和区县信息
    // 格式可能是: "北京" 或 "北京·朝阳区"
    // 注意：高德天气API主要支持城市名称，区县级别可能不支持，所以优先使用城市名
    let cityName = cityLocation
    let districtName = ''
    
    if (cityLocation.includes('·')) {
      const parts = cityLocation.split('·')
      cityName = parts[0]  // 使用城市名称（高德API对城市名的支持更好）
      districtName = parts[1] || ''
    }
    
    console.log('正在获取天气信息:', { 
      originalLocation: cityLocation,
      city: cityName, 
      district: districtName,
      queryCity: cityName
    })
    
    ElMessage.info({
      message: `正在获取${cityName}的天气信息...`,
      duration: 2000
    })
    
    // 调用后端天气API（使用城市名称，高德API对城市名的支持更好）
    const queryCity = cityName
    const response = await request.get('/third-party/weather', {
      params: { city: queryCity }
    })
    
    console.log('天气API完整响应:', JSON.stringify(response, null, 2))
    
    if (response && response.code === 200 && response.data) {
      const data = response.data
      console.log('天气数据详情:', data)
      
      // 检查是否有错误信息（后端返回的error或errorMessage字段）
      if (data.error || data.errorMessage || data.success === false) {
        console.error('天气API返回错误信息:', data.error, data.errorMessage)
        const errorMsg = data.errorMessage || data.error || '天气API返回异常'
        
        // 更精确地判断是否为API Key配置错误
        // 只有当错误信息明确提到"API Key未配置"或"INVALID_USER_KEY"时，才显示API Key配置错误
        // 如果只是说"请检查API Key是否有效，或城市名称是否正确"，这可能是城市名称错误，不显示API Key错误
        const isApiKeyError = (
          errorMsg.includes('API Key未配置') || 
          errorMsg.includes('INVALID_USER_KEY') || 
          errorMsg.includes('请在application.yml或环境变量中配置AMAP_API_KEY') ||
          (errorMsg.includes('API Key') && errorMsg.includes('未配置'))
        )
        
        if (isApiKeyError) {
          ElMessage.error({
            message: `高德API Key配置错误：${errorMsg}。请检查后端配置文件 application-dev.yml 中的 third-party.amap.api.key 配置。`,
            duration: 8000,
            showClose: true
          })
        } else {
          // 城市名称错误或其他错误，只显示普通错误提示
          ElMessage.warning({
            message: `获取${cityLocation}的天气信息失败：${errorMsg}`,
            duration: 5000,
            showClose: true
          })
        }
        
        return false
      }
      
      // 正常数据，提取城市和区县信息
      let cityName = ''
      let district = ''
      let locationText = ''
      
      // 使用API返回的城市名
      cityName = data.city || cityLocation
      district = getCityDistrict(cityName)
      locationText = district ? `${cityName}·${district}` : cityName
      
      // 转换数据格式以适配前端显示
      const newWeather = {
        temperature: Math.round(data.temperature || 25),
        weather: convertWeatherDescription(data.description || '晴天'),
        location: locationText,
        humidity: data.humidity || 60,
        windSpeed: convertWindSpeed(data.windSpeed || 3.5),
        aqi: getAirQuality(data),
        aqiLevel: getAirQualityLevel(data),
        uvIndex: getUVIndex(data)
      }
      
      console.log('新天气信息:', newWeather)
      
      // 更新天气信息
      weather.value = newWeather
      console.log('天气信息已更新到weather.value:', weather.value)
      
      // 保存位置信息
      localStorage.setItem('selected_weather_city', locationText)
      console.log('位置已保存到localStorage:', locationText)
      
      return true
    } else {
      console.error('天气API返回错误:', response)
      const errorMsg = response?.message || '天气API返回异常'
      console.error('错误详情:', errorMsg)
      
      ElMessage.error({
        message: `天气服务暂时不可用：${errorMsg}。请检查后端配置和API Key。`,
        duration: 6000,
        showClose: true
      })
      
      return false
    }
  } catch (error) {
    console.error('获取天气信息异常:', error)
    console.error('错误堆栈:', error.stack)
    
    // 出错时不使用模拟数据，直接返回错误
    ElMessage.error({
      message: `获取天气信息失败：${error.message || '未知错误'}。请检查网络连接和后端服务。`,
      duration: 6000,
      showClose: true
    })
    
    return false
  }
}


// 检测是否为Chrome浏览器
const isChromeBrowser = () => {
  const userAgent = navigator.userAgent.toLowerCase()
  // Chrome特征：包含Chrome且不包含Edge、Opera等
  const isChrome = userAgent.includes('chrome') && 
                   !userAgent.includes('edge') && 
                   !userAgent.includes('opr') &&
                   !userAgent.includes('opera')
  return isChrome
}

// 获取真实天气信息（优先使用浏览器定位+高德逆地理编码，失败时使用IP定位）
const getWeatherInfo = async () => {
  weatherLoading.value = true
  
  try {
    // 优先尝试浏览器定位（经纬度）+ 高德逆地理编码（最准确）
    try {
      console.log('🌐 优先尝试浏览器定位（经纬度）+ 高德逆地理编码...')
      ElMessage.info({
        message: '正在获取您的位置和天气信息...',
        duration: 2000
      })
      
      const location = await getCurrentLocation()
      console.log('✅ 浏览器定位成功，经纬度:', location.longitude, location.latitude)
      
      // 调用后端API根据经纬度获取城市和天气（使用高德逆地理编码）
      const response = await request.get('/third-party/weather/location', {
        params: {
          longitude: location.longitude,
          latitude: location.latitude
        }
      })
      
      if (response && response.code === 200 && response.data && !response.data.error) {
        const data = response.data
        const locationInfo = data.location || {}
        
        // 提取城市和区县信息
        let cityName = data.city || locationInfo.city || ''
        
        if (cityName) {
          // 处理城市名格式（去掉"市"后缀）
          cityName = cityName.replace('市', '').replace('省', '').trim()
          
          const district = locationInfo.district || getCityDistrict(cityName)
          const locationText = district ? `${cityName}·${district}` : cityName
          
          // 保存位置信息
          localStorage.setItem('selected_weather_city', locationText)
          localStorage.setItem('last_location_update', Date.now().toString())
          
          // 转换数据格式以适配前端显示
          weather.value = {
            temperature: Math.round(data.temperature || 25),
            weather: convertWeatherDescription(data.description || '晴天'),
            location: locationText,
            humidity: data.humidity || 60,
            windSpeed: convertWindSpeed(data.windSpeed || 3.5),
            aqi: getAirQuality(data),
            aqiLevel: getAirQualityLevel(data),
            uvIndex: getUVIndex(data)
          }
          
          console.log('✅ 浏览器定位+高德逆地理编码成功:', locationText)
          weatherLoading.value = false
          return // 成功获取，直接返回
        }
      }
    } catch (locationError) {
      console.warn('⚠️ 浏览器定位失败，使用高德API IP定位作为备选:', locationError)
    }
    
    // 浏览器定位失败，使用高德API IP定位
    // 先在前端获取用户真实公网IP，然后传递给后端，确保定位准确
    console.log('🌐 浏览器定位失败，使用高德API IP定位...')
    ElMessage.info({
      message: '浏览器定位失败，正在使用高德API定位...',
      duration: 2000
    })
    
    // 先在前端获取用户真实公网IP（优先获取IPv4）
    let userRealIp = null
    const ipServices = [
      { url: 'https://api.ipify.org?format=json', v4: true },
      { url: 'https://api4.ipify.org?format=json', v4: true }, // 强制IPv4
      { url: 'https://ipapi.co/json/', v4: false }
    ]
    
    for (const service of ipServices) {
      try {
        const ipResponse = await fetch(service.url, { 
          method: 'GET',
          headers: { 'Accept': 'application/json' },
          signal: AbortSignal.timeout(5000)
        })
        
        if (ipResponse.ok) {
          const ipData = await ipResponse.json()
          let ip = ipData.ip || ipData.query || ipData.origin
          
          // 优先使用IPv4
          const isIPv4 = ip && !ip.includes(':') && ip.includes('.')
          if (ip && isIPv4 && !ip.includes('127.0.0.1') && !ip.includes('localhost')) {
            userRealIp = ip
            console.log('✅ 获取到用户真实公网IP (IPv4):', userRealIp)
            break
          }
        }
      } catch (ipError) {
        console.warn(`⚠️ 从 ${service.url} 获取IP失败:`, ipError)
        continue
      }
    }
    
    // 调用高德API IP定位接口
    // 如果获取到用户真实IP，传递给后端；否则让后端自动检测
    const params = userRealIp ? { ip: userRealIp } : {}
    console.log('📡 调用高德API IP定位，参数:', params)
    const response = await request.get('/third-party/location/ip', { params })
    
    if (response && response.code === 200 && response.data) {
      const data = response.data
      const locationInfo = data.location || {}
      
      // 提取城市信息
      let cityName = data.city || locationInfo.city || ''
      
      if (cityName) {
        // 处理城市名格式（去掉"市"、"省"后缀）
        cityName = cityName.replace('市', '').replace('省', '').replace('自治区', '').trim()
        
        // 提取区县信息（优先从locationInfo，其次从data顶层）
        const district = locationInfo.district || data.district || getCityDistrict(cityName)
        const locationText = district ? `${cityName}·${district}` : cityName
        
        console.log('📍 IP定位提取的位置信息:', {
          city: cityName,
          district: district,
          locationText: locationText,
          locationInfo: locationInfo,
          dataDistrict: data.district
        })
        
        // 保存位置信息
        localStorage.setItem('selected_weather_city', locationText)
        localStorage.setItem('last_location_update', Date.now().toString())
        
        // 转换数据格式以适配前端显示
        weather.value = {
          temperature: Math.round(data.temperature || 25),
          weather: convertWeatherDescription(data.description || '晴天'),
          location: locationText,
          humidity: data.humidity || 60,
          windSpeed: convertWindSpeed(data.windSpeed || 3.5),
          aqi: getAirQuality(data),
          aqiLevel: getAirQualityLevel(data),
          uvIndex: getUVIndex(data)
        }
        
        console.log('✅ 高德API IP定位成功:', locationText)
        weatherLoading.value = false
        return
      }
    }
    
    // 如果定位失败，尝试使用缓存的城市
    const savedCity = localStorage.getItem('selected_weather_city')
    if (savedCity) {
      console.log('📌 使用缓存的城市:', savedCity)
      await getWeatherByCity(savedCity)
      return
    }
    
    // 如果都没有，使用默认城市
    console.log('⚠️ 无法获取位置信息，使用默认城市')
    await getWeatherByCity('南宁')
    
  } catch (error) {
    console.error('获取天气信息失败:', error)
    
    // 尝试使用缓存的城市
    const savedCity = localStorage.getItem('selected_weather_city')
    if (savedCity) {
      await getWeatherByCity(savedCity)
    } else {
      ElMessage.error({
        message: '获取天气信息失败，请稍后重试',
        duration: 3000
      })
      weatherLoading.value = false
    }
  }
}

// 转换天气描述为中文
const convertWeatherDescription = (description) => {
  if (!description) return '晴天'
  const desc = description.toLowerCase()
  if (desc.includes('clear') || desc.includes('晴')) return '晴天'
  if (desc.includes('cloud')) return '多云'
  if (desc.includes('rain')) return '小雨'
  if (desc.includes('drizzle')) return '小雨'
  if (desc.includes('snow')) return '雪'
  if (desc.includes('fog') || desc.includes('mist')) return '雾'
  if (desc.includes('overcast')) return '阴天'
  return description
}

// 转换风速为级别（后端返回的可能是风力等级数字，也可能是m/s速度）
const convertWindSpeed = (speed) => {
  // 如果speed已经是整数且小于10，可能是风力等级，直接返回
  if (typeof speed === 'number' && speed === Math.floor(speed) && speed < 10) {
    return speed
  }
  
  // 否则按m/s转换为级别
  // 0-0.2: 0级, 0.3-1.5: 1级, 1.6-3.3: 2级, 3.4-5.4: 3级, 5.5-7.9: 4级
  if (speed <= 0.2) return 0
  if (speed <= 1.5) return 1
  if (speed <= 3.3) return 2
  if (speed <= 5.4) return 3
  if (speed <= 7.9) return 4
  if (speed <= 10.7) return 5
  if (speed <= 13.8) return 6
  return 7
}

// 获取空气质量（OpenWeatherMap不直接提供AQI，需要根据数据计算或使用默认值）
const getAirQuality = (data) => {
  // 如果有AQI数据直接使用
  if (data.aqi) return data.aqi
  if (data.airQuality) return data.airQuality
  
  // 根据能见度和湿度估算
  if (data.visibility) {
    const visibility = data.visibility
    if (visibility > 10000) return '优'
    if (visibility > 5000) return '良'
    if (visibility > 2000) return '轻度污染'
    return '中度污染'
  }
  
  return '良'
}

// 获取空气质量等级
const getAirQualityLevel = (data) => {
  const aqi = getAirQuality(data)
  if (aqi === '优') return 'good'
  if (aqi === '良') return 'moderate'
  return 'bad'
}

// 获取紫外线指数（根据时间和天气估算）
const getUVIndex = (data) => {
  // 如果有UV数据直接使用
  if (data.uvIndex) return data.uvIndex
  
  // 根据天气和时间估算
  const hour = new Date().getHours()
  const description = (data.description || '').toLowerCase()
  
  if (description.includes('clear') || description.includes('晴')) {
    if (hour >= 10 && hour <= 16) return '强'
    return '中等'
  }
  if (description.includes('cloud')) return '中等'
  return '弱'
}

// 获取城市区县（简化处理，使用主要区县）
const getCityDistrict = (city) => {
  const cityDistricts = {
    '北京': '朝阳区',
    '上海': '黄浦区',
    '广州': '天河区',
    '深圳': '南山区',
    '成都': '锦江区',
    '杭州': '西湖区',
    '南京': '鼓楼区',
    '西安': '碑林区',
    '武汉': '江汉区',
    '重庆': '渝中区',
    '天津': '和平区',
    '苏州': '姑苏区',
    '郑州': '金水区',
    '长沙': '芙蓉区',
    '青岛': '市南区',
    '大连': '中山区',
    '厦门': '思明区',
    '福州': '鼓楼区',
    '昆明': '五华区',
    '哈尔滨': '道里区',
    '沈阳': '和平区',
    '济南': '历下区',
    '石家庄': '长安区',
    '长春': '朝阳区',
    '南昌': '东湖区',
    '合肥': '庐阳区',
    '南宁': '青秀区',
    '海口': '龙华区',
    '太原': '迎泽区',
    '贵阳': '云岩区'
  }
  return cityDistricts[city] || '中心区'
}

// 加载用户画像数据（从后端API获取真实数据）
const loadUserPortrait = async () => {
  try {
    // 从后端API获取用户画像数据
    const response = await getUserPortrait()
    
    if (response.code === 200 && response.data) {
      const data = response.data
      
      // 提取兴趣标签名称列表
      const tags = (data.interestTags || []).map(tag => (tag && tag.name) ? tag.name : tag).slice(0, 5)
      
      // 获取消费水平文本
      const consumptionLevelText = data.consumptionLevel 
        ? (typeof data.consumptionLevel === 'string' 
            ? data.consumptionLevel 
            : (data.consumptionLevel.level || ''))
        : ''
      
      // 更新用户画像数据
      userPortrait.value = {
        browseCount: data.browseCount || 0,
        favoriteCount: data.favoriteCount || 0,
        searchCount: data.searchCount || 0,
        favoriteType: data.primaryPreference || '自然风光',
        consumptionLevel: consumptionLevelText,
        travelMode: data.travelMode || '',
        avgStayTime: data.avgStayTime || 0,
        tags: tags.length > 0 ? tags : ['文化旅游', '自然风光', '美食体验'] // 默认标签
      }
      
      console.log('✅ 用户画像数据已加载（真实数据）:', userPortrait.value)
    } else {
      console.warn('⚠️ 获取用户画像数据失败，使用默认数据')
      userPortrait.value = {
        browseCount: 0,
        favoriteCount: 0,
        searchCount: 0,
        favoriteType: '自然风光',
        consumptionLevel: '',
        travelMode: '',
        avgStayTime: 0,
        tags: ['文化旅游', '自然风光', '美食体验']
      }
    }
  } catch (error) {
    console.error('❌ 加载用户画像失败:', error)
    // 加载失败时使用默认数据
    userPortrait.value = {
      browseCount: 0,
      favoriteCount: 0,
      searchCount: 0,
      favoriteType: '自然风光',
      consumptionLevel: '',
      travelMode: '',
      avgStayTime: 0,
      tags: ['文化旅游', '自然风光', '美食体验']
    }
  }
}

// 加载"我的攻略"统计数据
const loadMyPlansStats = async () => {
  try {
    const userInfo = getCurrentUserInfo()
    if (!userInfo) {
      return
    }
    const userId = userInfo.id
    
    if (!userId) {
      return
    }
    
    // 获取用户的所有攻略
    const response = await request.get('/travel-plan/list', {
      params: {
        page: 1,
        size: 9999,
        authorId: userId
      }
    })
    
    if (response.code === 200 && response.data?.list) {
      const allPlans = response.data.list || []
      
      // 统计总攻略数
      const totalPlans = allPlans.length
      
      // 统计待审核数量（auditStatus === 0）
      const pendingCount = allPlans.filter(function(plan) {
        return plan.auditStatus === 0
      }).length
      
      // 只统计已发布攻略（auditStatus === 1）的浏览量和评论数
      const publishedPlans = allPlans.filter(function(plan) {
        return plan.auditStatus === 1
      })
      
      // 统计已发布攻略的总浏览量
      const totalViews = publishedPlans.reduce(function(sum, plan) {
        return sum + (plan.viewCount || 0)
      }, 0)
      
      // 统计已发布攻略的总评论数
      const totalComments = publishedPlans.reduce(function(sum, plan) {
        return sum + (plan.commentCount || 0)
      }, 0)
      
      // 更新"我的攻略"卡片数据
      const myPlansCard = mySpace.value.find(function(item) {
        return item.title === '我的攻略'
      })
      if (myPlansCard) {
        myPlansCard.count = pendingCount // 右上角红色数字：待审核数量
        myPlansCard.stats.total = totalPlans // 总攻略数
        myPlansCard.stats.views = totalViews // 已发布攻略的总浏览量
        myPlansCard.stats.comments = totalComments // 已发布攻略的总评论数
      }
    }
  } catch (error) {
    console.error('加载我的攻略统计失败:', error)
  }
}

// 加载我的收藏统计数据
const loadMyCollectStats = async () => {
  try {
    console.log('🔍 开始加载我的收藏统计数据...')
    
    // 获取当前标签页的用户信息
    const userData = getCurrentUserInfo()
    if (!userData) {
      console.warn('⚠️ 未获取到用户信息，无法加载收藏统计')
      return
    }
    
    const userId = userData.id || userData.userId
    if (!userId) {
      console.warn('⚠️ 未获取到用户ID，无法加载收藏统计')
      return
    }
    
    console.log('✅ 用户ID:', userId)
    
    // 先获取收藏列表（这个API更可靠）
    const listResponse = await request.get('/user-collect/list', {
      params: {
        userId: userId,
        page: 1,
        size: 1000  // 获取所有收藏
      }
    })
    
    console.log('📋 收藏列表API响应:', listResponse)
    
    // 找到"我的收藏"卡片
    const collectCard = mySpace.value.find(function(card) {
      return card.title === '我的收藏'
    })
    
    if (!collectCard) {
      console.error('❌ 未找到"我的收藏"卡片')
      return
    }
    
    // 从收藏列表获取总数
    if (listResponse.code === 200 && listResponse.data) {
      const totalCount = listResponse.data.total || 0
      const collects = listResponse.data.list || []
      
      console.log('✅ 收藏总数（从列表API）:', totalCount)
      console.log('📋 收藏列表数据:', collects)
      
      // 更新收藏总数（不显示红色圆点，count设为0）
      collectCard.count = 0
      collectCard.stats.total = totalCount
      console.log('✅ 已更新收藏卡片数据 - count:', collectCard.count, 'total:', collectCard.stats.total)
    } else {
      console.warn('⚠️ 收藏列表API返回错误，尝试使用统计API')
      
      // 如果列表API失败，尝试使用统计API
      try {
        const statsResponse = await request.get(`/user-collect/stats/${userId}`)
        console.log('📊 收藏统计API响应:', statsResponse)
        
        if (statsResponse.code === 200 && statsResponse.data) {
          const statsData = statsResponse.data
          const totalCount = statsData.totalCount || 0
          
          console.log('✅ 收藏总数（从统计API）:', totalCount)
          
          collectCard.count = 0 // 不显示红色圆点
          collectCard.stats.total = totalCount
          console.log('✅ 已更新收藏卡片数据 - count:', collectCard.count, 'total:', collectCard.stats.total)
        }
      } catch (statsError) {
        console.error('❌ 统计API也失败:', statsError)
      }
    }
  } catch (error) {
    console.error('❌ 加载我的收藏统计失败:', error)
    console.error('错误详情:', error.response || error)
  }
}

// 加载用户积分和等级信息
const loadUserLevelInfo = async () => {
  try {
    const userId = getCurrentUserId()
    if (!userId) return
    
    // 获取用户积分及等级信息
    const pointsResponse = await request.get('/user/points/my', {
      params: { userId }
    })
    
    if (pointsResponse.code === 200 && pointsResponse.data) {
      const userPoints = pointsResponse.data.userPoints || {}
      currentPoints.value = userPoints.totalPoints || 0
      
      // 根据积分计算等级（使用升级指南的等级体系）
      const levelInfo = getLevelByPoints(currentPoints.value)
      currentLevelCode.value = levelInfo.code || 1
      
      // 从 levels 数组中获取完整的等级信息（包括颜色和渐变）
      const levelData = levels.value.find(l => l.code === levelInfo.code)
      if (levelData) {
        currentLevelInfo.value = {
          name: levelData.name,
          color: levelData.color,
          gradient: levelData.gradient,
          icon: levelData.icon
        }
      } else {
        // 降级方案：使用 levelInfo 的数据
        currentLevelInfo.value = {
          name: levelInfo.name,
          color: levelInfo.color,
          gradient: levelInfo.gradient ? `linear-gradient(135deg, ${levelInfo.gradient.start}, ${levelInfo.gradient.end})` : `linear-gradient(135deg, ${levelInfo.color}, ${levelInfo.color})`,
          icon: 'User'
        }
      }
    }
  } catch (error) {
    console.error('加载用户等级信息失败:', error)
    // 默认设置为青铜旅行者
    currentLevelCode.value = 1
    currentLevelInfo.value = {
      name: '青铜旅行者',
      color: '#8B7355',
      gradient: 'linear-gradient(135deg, #8B7355 0%, #6B5B4F 100%)',
      icon: 'User'
    }
  }
}

onMounted(() => {
  // 加载数据
  console.log('用户首页加载')
  loadUserInfo()
  getWeatherInfo()
  loadBanners()
  loadRecommendations()
  loadHotPlans()
  loadMyPlansStats() // 加载我的攻略统计数据
  loadMyCollectStats() // 加载我的收藏统计数据
  loadUserLevelInfo() // 加载用户积分和等级信息
  loadAnnouncements() // 加载公告数据
  loadCultureProjects() // 加载文旅体验数据
})
</script>

<style lang="scss" scoped>
.modern-user-dashboard {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  padding-bottom: 60px;
  padding-right: 130px; /* 为右侧导航条留出空间 */
}

// 顶部区域容器（欢迎区 + 新闻/公告）
.top-section-container {
  display: flex;
  gap: 24px;
  margin: 0 40px 32px;
  align-items: flex-start;
}

// 新闻/公告区域
.news-section {
  flex: 1;
  min-width: 0;

  .news-container {
    background: #fff;
    border-radius: 16px;
    border: 1px solid #ebeef5;
    padding: 24px 28px;
    box-shadow: 0 10px 30px rgba(15, 39, 95, 0.04);
  }

  .news-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 16px;

    h3 {
      margin: 8px 0 6px;
      font-size: 20px;
      color: #1f2a37;
    }

    p {
      margin: 0;
      color: #909399;
      font-size: 13px;
    }
  }

  .news-kicker {
    font-size: 24px;
    font-weight: 700;
    color: #1f2a37;
    letter-spacing: 0;
    text-transform: none;
  }

  .news-view-all {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-weight: 500;
  }

  .news-tabs {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
    margin: 20px 0 12px;
  }

  @media (min-width: 1280px) {
    .news-tabs {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
  }

  .news-tab {
    border: none;
    border-radius: 0;
    padding: 8px 12px;
    background: transparent;
    color: #9ca3af;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow: none;
    position: relative;
    border-bottom: 2px solid transparent;

    &:focus,
    &:active {
      outline: none;
      box-shadow: none;
    }

    &.tab-system {
      color: #9ca3af;

      &.active {
        color: #3c5ed4;
        border-bottom-color: #3c5ed4;
      }
    }

    &.tab-activity {
      color: #9ca3af;

      &.active {
        color: #d26a18;
        border-bottom-color: #d26a18;
      }
    }

    &.tab-feature {
      color: #9ca3af;

      &.active {
        color: #1c9150;
        border-bottom-color: #1c9150;
      }
    }

    &.tab-maintain {
      color: #9ca3af;

      &.active {
        color: #c3473c;
        border-bottom-color: #c3473c;
      }
    }
  }

  .news-content {
    margin-top: 12px;
    min-height: 220px;
  }

  .news-list {
    display: flex;
    flex-direction: column;
  }

  .news-item {
    display: grid;
    grid-template-columns: 110px 1fr 70px 24px;
    align-items: center;
    padding: 14px 0;
    column-gap: 12px;
    border-bottom: 1px solid #f2f3f5;
    cursor: pointer;
    transition: color 0.15s ease;
    background: transparent !important;
    background-color: transparent !important;
    border: none;
    width: 100%;
    text-align: left;
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    outline: none;

    &:last-child {
      border-bottom: none;
    }

    &:hover .news-item-title {
      color: #3a7bff;
    }

    &:hover,
    &:focus,
    &:focus-visible,
    &:active {
      background: transparent !important;
      background-color: transparent !important;
      outline: none;
      box-shadow: none !important;
    }
  }

  .news-item-label {
    justify-self: start;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 500;
    border: 1px solid transparent;
  }

  .news-item-title {
    font-size: 15px;
    font-weight: 500;
    color: #1f2a37;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .news-item-date {
    font-size: 13px;
    color: #9ca3af;
    text-align: right;
    pointer-events: none;
  }

  .news-item-arrow {
    color: #c0c4cc;
    font-size: 16px;
    pointer-events: none;
  }

  .news-empty {
    text-align: center;
    padding: 40px 0;
    color: #909399;
  }

  .news-empty-icon {
    font-size: 30px;
    margin-bottom: 10px;
  }

  .news-empty-title {
    font-weight: 600;
    color: #303133;
    margin-bottom: 6px;
  }

  .news-empty-desc {
    margin: 0;
    font-size: 13px;
    color: #a0a7b3;
  }

  .news-fade-enter-active,
  .news-fade-leave-active {
    transition: opacity 0.18s ease-out, transform 0.18s ease-out;
  }

  .news-fade-enter-from,
  .news-fade-leave-to {
    opacity: 0;
    transform: translateY(2px);
  }
}

.badge-system {
  background: rgba(76, 125, 255, 0.12);
  color: #3c5ed4;
  border-color: rgba(76, 125, 255, 0.3);
}

.badge-activity {
  background: rgba(255, 173, 102, 0.15);
  color: #d26a18;
  border-color: rgba(255, 173, 102, 0.35);
}

.badge-feature {
  background: rgba(57, 199, 135, 0.15);
  color: #1c9150;
  border-color: rgba(57, 199, 135, 0.35);
}

.badge-maintain {
  background: rgba(242, 126, 126, 0.15);
  color: #c3473c;
  border-color: rgba(242, 126, 126, 0.35);
}

// 升级指南和文旅体验容器
.level-guide-culture-container {
  display: flex;
  gap: 24px;
  margin: 0 40px;
  align-items: flex-start;
}

// 升级指南区域
.level-guide-section {
  flex: 1;
  min-width: 0;
  padding: 40px 40px 20px;

  .level-guide-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 20px;

    .current-level-mini-card {
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      
      :deep(.el-card__body) {
        padding: 28px 24px;
      }
      
      .level-status-mini {
        display: flex;
        flex-direction: column;
        gap: 20px;

        .level-icon-mini {
          width: 90px;
          height: 90px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto;
          color: white;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
          filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
          position: relative;
          
          &::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
            pointer-events: none;
          }
        }

        .level-info-mini {
          text-align: center;

          .level-name-mini {
            font-size: 22px;
            font-weight: 700;
            margin-bottom: 6px;
            letter-spacing: 0.5px;
          }

          .level-desc-mini {
            font-size: 13px;
            color: #909399;
          }
        }

        .level-progress-mini {
          margin-top: 4px;
          
          :deep(.el-progress-bar__outer) {
            border-radius: 10px;
            background-color: #f3f4f6;
          }
          
          :deep(.el-progress-bar__inner) {
            border-radius: 10px;
          }
          
          .progress-text-mini {
            font-size: 13px;
            color: #6b7280;
            text-align: center;
            margin-top: 10px;
            font-weight: 500;
          }
        }
      }
    }

        .upgrade-tips-grid {
          display: flex;
          flex-direction: column;
          gap: 16px;

      .tip-card {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: white;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .tip-icon {
          font-size: 32px;
        }

        .tip-content-mini {
          flex: 1;

          .tip-title-mini {
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
          }

          .tip-desc-mini {
            font-size: 12px;
            color: #909399;
          }
        }

        .tip-points {
          font-size: 16px;
          font-weight: 600;
          color: #409eff;
        }
      }
    }
  }
}

// 欢迎区域
.welcome-section {
  flex: 2;
  min-width: 0;
  position: relative;
  padding: 48px 40px;
  background: linear-gradient(145deg, 
    #f5f5f5 0%, 
    #e8e8e8 40%,
    #f0f0f0 80%,
    #e8e8e8 100%);
  background-size: 200% 200%;
  overflow: hidden;
  border-radius: 32px;
  box-shadow: 
    0 16px 48px rgba(150, 150, 150, 0.15),
    0 0 0 1px rgba(200, 200, 200, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  animation: gradient-shift 15s ease infinite;
  
  // 顶部装饰性高光
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, 
      transparent 0%,
      rgba(255, 255, 255, 0.6) 50%,
      transparent 100%);
    opacity: 0.8;
  }
  
  // 底部装饰性阴影
  &::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 20px;
    right: 20px;
    height: 8px;
    background: radial-gradient(ellipse at center, 
      rgba(180, 180, 180, 0.3) 0%, 
      transparent 70%);
    filter: blur(8px);
    z-index: -1;
  }
  
  .welcome-content {
    position: relative;
    z-index: 2;
    display: flex;
    gap: 24px;
    align-items: flex-start;
    
    .user-info-section {
      flex: 2;
      min-width: 0;
    }
    
    .profile-guide-card {
      flex: 1;
      min-width: 0;
    }
  }
  
  // 左侧用户信息区域
  .user-info-section {
    display: flex;
    flex-direction: column;
    gap: 24px;
    
    .user-header {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .user-avatar {
        border: 4px solid rgba(200, 200, 200, 0.4);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 240, 240, 0.8));
        
        :deep(.el-icon) {
          color: #2c3e50;
        }
      }
      
      .user-greeting {
        flex: 1;
        
        .welcome-title {
          font-size: 36px;
          font-weight: 700;
          margin: 0 0 8px 0;
          color: #2c3e50;
          
          .greeting {
            opacity: 0.9;
            font-weight: 600;
            color: #34495e;
          }
          
          .user-name {
            background: linear-gradient(90deg, #2c3e50 0%, #34495e 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            text-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          }
        }
        
        .welcome-subtitle {
          font-size: 16px;
          color: rgba(52, 73, 94, 0.8);
          margin: 0;
        }
      }
    }
    
    // 天气信息卡片
    .weather-info-card {
      background: linear-gradient(135deg, 
        rgba(255, 255, 255, 0.8) 0%,
        rgba(248, 248, 248, 0.7) 100%);
      backdrop-filter: blur(30px);
      border-radius: 24px;
      padding: 28px;
      border: 1.5px solid rgba(220, 220, 220, 0.5);
      box-shadow: 
        0 12px 40px rgba(150, 150, 150, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 
          0 16px 48px rgba(150, 150, 150, 0.15),
          inset 0 1px 0 rgba(255, 255, 255, 0.9);
        border-color: rgba(200, 200, 200, 0.6);
      }
      
      .weather-main {
        display: flex;
        align-items: center;
        gap: 20px;
        margin-bottom: 20px;
        padding-bottom: 20px;
        border-bottom: 1px solid rgba(220, 220, 220, 0.4);
        
        .weather-icon-wrapper {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .weather-icon {
            font-size: 48px;
            filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
          }
          
          .weather-temp {
            font-size: 42px;
            font-weight: 700;
            color: #2c3e50;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          }
        }
        
        .weather-details {
          flex: 1;
          
          .weather-location {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #2c3e50;
            font-size: 16px;
            margin-bottom: 6px;
            
            .el-icon {
              font-size: 18px;
              flex-shrink: 0;
              color: #34495e;
            }
            
            .city-select {
              flex: 1;
              max-width: 200px;
              
              :deep(.el-input__wrapper) {
                background: rgba(255, 255, 255, 0.8);
                border: 1px solid rgba(200, 200, 200, 0.5);
                border-radius: 8px;
                box-shadow: none;
                
                &:hover {
                  background: rgba(255, 255, 255, 0.9);
                  border-color: rgba(180, 180, 180, 0.6);
                }
                
                &.is-focus {
                  background: rgba(255, 255, 255, 1);
                  border-color: rgba(150, 150, 150, 0.7);
                  box-shadow: 0 0 0 2px rgba(200, 200, 200, 0.2);
                }
              }
              
              :deep(.el-input__inner) {
                color: #2c3e50;
                font-size: 16px;
                
                &::placeholder {
                  color: rgba(100, 100, 100, 0.6);
                }
              }
              
              :deep(.el-select__caret) {
                color: rgba(100, 100, 100, 0.8);
              }
            }
          }
          
          .weather-condition {
            font-size: 18px;
            color: #34495e;
            font-weight: 500;
          }
        }
      }
      
      .weather-metrics {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 16px;
        margin-bottom: 16px;
        
        .metric-item {
          display: flex;
          align-items: center;
          gap: 10px;
          background: rgba(255, 255, 255, 0.6);
          padding: 12px;
          border-radius: 12px;
          border: 1px solid rgba(220, 220, 220, 0.4);
          
          .metric-icon {
            font-size: 24px;
          }
          
          .metric-info {
            display: flex;
            flex-direction: column;
            
            .metric-label {
              font-size: 12px;
              color: rgba(100, 100, 100, 0.8);
              margin-bottom: 2px;
            }
            
            .metric-value {
              font-size: 16px;
              font-weight: 600;
              color: #2c3e50;
              
              &.aqi-good {
                color: #67C23A;
              }
              
              &.aqi-moderate {
                color: #E6A23C;
              }
              
              &.aqi-bad {
                color: #F56C6C;
              }
            }
          }
        }
      }
      
      .weather-advice {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px 16px;
        background: rgba(255, 255, 255, 0.6);
        border-radius: 12px;
        border: 1px solid rgba(220, 220, 220, 0.4);
        color: #34495e;
        font-size: 14px;
        
        .el-icon {
          font-size: 18px;
          flex-shrink: 0;
          color: #34495e;
        }
      }
    }
  }
  
  // 右侧个人画像引导卡片
  .profile-guide-card {
    flex: 1;
    min-width: 0;
    background: linear-gradient(145deg, 
      #faf5e6 0%, 
      #fef9e7 50%,
      #faf5e6 100%);
    backdrop-filter: blur(30px);
    border-radius: 32px;
    padding: 40px 36px;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    box-shadow: 
      0 12px 40px rgba(0, 0, 0, 0.08),
      0 0 0 1px rgba(0, 0, 0, 0.05),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
    border: 2.5px solid transparent;
    background-clip: padding-box;
    position: relative;
    overflow: hidden;
    
    // 外层光晕边框（奶白色调）
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      border-radius: 32px;
      padding: 2.5px;
      background: linear-gradient(145deg, 
        rgba(0, 0, 0, 0.05) 0%, 
        rgba(0, 0, 0, 0.03) 50%,
        rgba(0, 0, 0, 0.05) 100%);
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      opacity: 0.7;
      z-index: -1;
    }
    
    // 装饰性渐变光效（奶白色调）
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: linear-gradient(45deg, 
        transparent 30%,
        rgba(255, 255, 255, 0.3) 50%,
        transparent 70%
      );
      transform: rotate(45deg);
      pointer-events: none;
    }
    
    // 装饰性圆点
    .card-decoration {
      position: absolute;
      width: 100%;
      height: 100%;
      top: 0;
      left: 0;
      pointer-events: none;
      
      &::before,
      &::after {
        content: '';
        position: absolute;
        border-radius: 50%;
        background: radial-gradient(circle, 
          rgba(0, 0, 0, 0.03) 0%, 
          transparent 70%);
        animation: float-decoration 8s ease-in-out infinite;
      }
      
      &::before {
        width: 120px;
        height: 120px;
        top: -20px;
        right: -20px;
        animation-delay: 0s;
      }
      
      &::after {
        width: 90px;
        height: 90px;
        bottom: 20px;
        left: -10px;
        animation-delay: 2s;
      }
    }
    
    .guide-icon-wrapper {
      position: relative;
      margin-bottom: 28px;
      transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
      
      .guide-icon-bg {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 110px;
        height: 110px;
        background: linear-gradient(135deg, 
          rgba(0, 0, 0, 0.03) 0%, 
          rgba(0, 0, 0, 0.02) 50%,
          rgba(0, 0, 0, 0.03) 100%);
        border-radius: 50%;
        opacity: 1;
        transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
        
        &::before {
          content: '';
          position: absolute;
          inset: -10px;
          border-radius: 50%;
          background: linear-gradient(135deg, 
            rgba(255, 255, 255, 0.2) 0%, 
            rgba(255, 255, 255, 0.1) 100%);
          filter: blur(15px);
        }
      }
      
      .guide-icon {
        position: relative;
        z-index: 2;
        color: #374151;
        filter: drop-shadow(0 4px 16px rgba(0, 0, 0, 0.1));
        transition: all 0.4s ease;
      }
    }
    
    .guide-content {
      margin-bottom: 28px;
      position: relative;
      z-index: 2;
      
      .guide-title {
        font-size: 26px;
        font-weight: 800;
        color: #374151;
        margin: 0 0 16px 0;
        line-height: 1.3;
        letter-spacing: -0.5px;
        text-shadow: none;
      }
      
      .guide-desc {
        font-size: 14px;
        color: #606266;
        margin: 0 0 20px 0;
        line-height: 1.7;
        font-weight: 500;
        opacity: 0.9;
      }
      
      .guide-stats {
        display: flex;
        gap: 10px;
        justify-content: center;
        margin-bottom: 20px;
        flex-wrap: wrap;
        
        .stat-badge {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 18px;
          background: rgba(255, 255, 255, 0.6);
          border-radius: 24px;
          font-size: 13px;
          color: #374151;
          font-weight: 700;
          border: 1.5px solid rgba(0, 0, 0, 0.08);
          backdrop-filter: blur(10px);
          letter-spacing: 0.3px;
          
          .el-icon {
            font-size: 17px;
            color: #374151;
          }
        }
      }
      
      .guide-stats-secondary {
        display: flex;
        flex-direction: column;
        gap: 8px;
        margin-bottom: 16px;
        padding: 12px;
        background: rgba(255, 255, 255, 0.4);
        border-radius: 12px;
        border: 1px solid rgba(0, 0, 0, 0.05);
        
        .stat-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          font-size: 13px;
          
          .stat-label {
            color: #606266;
            font-weight: 500;
          }
          
          .stat-value {
            color: #374151;
            font-weight: 600;
          }
        }
      }
      
      .portrait-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        justify-content: center;
        
        .portrait-tag {
          padding: 8px 16px;
          background: rgba(255, 255, 255, 0.6);
          border: 1.5px solid rgba(0, 0, 0, 0.08);
          border-radius: 20px;
          font-size: 13px;
          color: #374151;
          font-weight: 600;
          backdrop-filter: blur(10px);
          letter-spacing: 0.3px;
        }
      }
    }
    
    .guide-btn {
      width: 100%;
      height: 56px;
      position: relative;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 28px;
      overflow: visible;
      
      // 背景层
      .btn-bg {
        position: absolute;
        inset: 0;
        background: linear-gradient(135deg, #374151 0%, #1f2937 100%);
        border-radius: 28px;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 
          0 4px 20px rgba(0, 0, 0, 0.15),
          inset 0 1px 0 rgba(255, 255, 255, 0.1);
      }
      
      // 内容层
      .btn-content {
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;
        gap: 10px;
        color: white;
        font-size: 15px;
        font-weight: 600;
        letter-spacing: 0.5px;
        transition: all 0.3s ease;
        
        .btn-icon {
          font-size: 20px;
          transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        }
        
        .btn-arrow {
          font-size: 18px;
          transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        }
      }
      
      // 波纹层
      .btn-ripple {
        position: absolute;
        inset: -4px;
        border-radius: 32px;
        border: 2px solid #374151;
        opacity: 0;
        transition: all 0.6s ease;
      }
      
      // 悬停效果
      &:hover {
        .btn-bg {
          transform: scale(1.02);
          box-shadow: 
            0 8px 30px rgba(0, 0, 0, 0.2),
            0 0 0 4px rgba(55, 65, 81, 0.1),
            inset 0 1px 0 rgba(255, 255, 255, 0.1);
        }
        
        .btn-content {
          gap: 14px;
          
          .btn-icon {
            transform: rotate(-15deg) scale(1.15);
          }
          
          .btn-arrow {
            transform: translateX(6px) rotate(-10deg);
          }
        }
        
        .btn-ripple {
          opacity: 1;
          transform: scale(1.1);
          border-color: rgba(55, 65, 81, 0.4);
        }
      }
      
      // 点击效果
      &:active {
        .btn-bg {
          transform: scale(0.98);
          box-shadow: 
            0 2px 10px rgba(0, 0, 0, 0.2),
            inset 0 2px 4px rgba(0, 0, 0, 0.3);
        }
        
        .btn-content {
          transform: scale(0.95);
        }
      }
    }
  }
  
  // 装饰性浮动动画
  @keyframes float-decoration {
    0%, 100% {
      transform: translate(0, 0) scale(1);
      opacity: 0.6;
    }
    33% {
      transform: translate(10px, -15px) scale(1.1);
      opacity: 0.8;
    }
    66% {
      transform: translate(-10px, 15px) scale(0.9);
      opacity: 0.7;
    }
  }
  
  .decoration-circles {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: 1;
    pointer-events: none;
    
    .circle {
      position: absolute;
      border-radius: 50%;
      filter: blur(40px);
      opacity: 0.6;
      
      &.circle-1 {
        width: 450px;
        height: 450px;
        top: -220px;
        right: -120px;
        background: radial-gradient(circle at center,
          rgba(255, 255, 255, 0.15) 0%,
          rgba(255, 255, 255, 0.08) 40%,
          transparent 70%);
        animation: float-scale 22s ease-in-out infinite;
      }
      
      &.circle-2 {
        width: 300px;
        height: 300px;
        bottom: -140px;
        left: 8%;
        background: radial-gradient(circle at center,
          rgba(255, 255, 255, 0.12) 0%,
          rgba(255, 255, 255, 0.06) 40%,
          transparent 70%);
        animation: float-scale 18s ease-in-out infinite reverse;
      }
      
      &.circle-3 {
        width: 220px;
        height: 220px;
        top: 35%;
        right: 12%;
        background: radial-gradient(circle at center,
          rgba(255, 255, 255, 0.1) 0%,
          rgba(255, 255, 255, 0.05) 40%,
          transparent 70%);
        animation: float-rotate 20s ease-in-out infinite;
      }
    }
  }
  
  // 浮动动画
  @keyframes float {
    0%, 100% {
      transform: translateY(0px);
    }
    50% {
      transform: translateY(-20px);
    }
  }
  
  // 浮动+缩放动画
  @keyframes float-scale {
    0%, 100% {
      transform: translateY(0px) scale(1);
    }
    33% {
      transform: translateY(-25px) scale(1.05);
    }
    66% {
      transform: translateY(15px) scale(0.95);
    }
  }
  
  // 浮动+旋转动画
  @keyframes float-rotate {
    0%, 100% {
      transform: translateY(0px) rotate(0deg);
    }
    50% {
      transform: translateY(-30px) rotate(180deg);
    }
  }
  
  // 渐变背景动画
  @keyframes gradient-shift {
    0%, 100% {
      background-position: 0% 50%;
    }
    50% {
      background-position: 100% 50%;
    }
  }
}

// 轮播图
.carousel-section {
  padding: 30px 40px;
  
  .premium-carousel {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    position: relative;
    
    // 指示器样式
    :deep(.el-carousel__indicators) {
      .el-carousel__indicator {
        .el-carousel__button {
          width: 24px;
          height: 3px;
          border-radius: 2px;
          background: rgba(255, 255, 255, 0.5);
          transition: all 0.3s;
        }
        
        &.is-active .el-carousel__button {
          width: 40px;
          background: white;
        }
      }
    }
    
    .carousel-item {
      position: relative;
      width: 100%;
      height: 100%;
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      display: flex;
      align-items: center;
      
      &.carousel-item-clickable {
        cursor: pointer;
      }
      
      // 移除所有悬停高亮效果
      
      .carousel-overlay {
        position: absolute;
        width: 100%;
        height: 100%;
        // 底部渐变遮罩，让文字更清晰，同时突出图片
        background: linear-gradient(
          to top,
          rgba(0, 0, 0, 0.5) 0%,
          rgba(0, 0, 0, 0.2) 30%,
          transparent 60%
        );
        transition: all 0.4s ease;
      }
      
      .carousel-content {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        z-index: 2;
        padding: 50px 60px;
        color: white;
        // 文字靠左对齐，不遮挡图片中心
        
        .carousel-badge {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 4px 12px;
          background: transparent;
          border-radius: 16px;
          font-size: 14px;
          font-weight: 500;
          margin-bottom: 16px;
          opacity: 0.9;
          letter-spacing: 0.5px;
          
          .badge-windmill {
            display: inline-block;
            font-size: 14px;
            animation: windmill-spin 3s linear infinite;
          }
        }
        
        .carousel-title {
          font-size: 52px;
          font-weight: 700;
          margin: 0 0 12px 0;
          text-shadow: 0 2px 12px rgba(0, 0, 0, 0.5);
          line-height: 1.3;
          letter-spacing: -0.5px;
          // 限制最大宽度，避免遮挡图片
          max-width: 70%;
        }
        
        .carousel-subtitle {
          font-size: 20px;
          opacity: 0.95;
          margin: 0 0 24px 0;
          line-height: 1.6;
          text-shadow: 0 1px 6px rgba(0, 0, 0, 0.4);
          font-weight: 400;
          // 限制最大宽度
          max-width: 60%;
        }
        
        .carousel-link {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          color: white;
          font-size: 15px;
          font-weight: 500;
          cursor: pointer;
          padding: 10px 20px;
          background: rgba(255, 255, 255, 0.15);
          backdrop-filter: blur(8px);
          border-radius: 24px;
          border: 1px solid rgba(255, 255, 255, 0.2);
          transition: all 0.3s ease;
          
          .link-text {
            text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
          }
          
          .link-arrow {
            font-size: 16px;
            transition: transform 0.3s ease;
          }
          
          &:hover {
            background: rgba(255, 255, 255, 0.25);
            border-color: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
            
            .link-arrow {
              transform: translateX(3px);
            }
          }
        }
      }
    }
  }
  
  // 响应式优化：小屏幕设备
  @media (max-width: 768px) {
    .carousel-content {
      padding: 30px 24px !important;
      
      .carousel-badge {
        font-size: 13px;
        padding: 3px 10px;
        margin-bottom: 12px;
      }
      
      .carousel-title {
        font-size: 32px !important;
        max-width: 90% !important;
        margin-bottom: 10px;
      }
      
      .carousel-subtitle {
        font-size: 16px !important;
        max-width: 85% !important;
        margin-bottom: 20px;
      }
      
      .carousel-link {
        font-size: 14px;
        padding: 8px 16px;
      }
    }
  }
  
  // 中等屏幕优化
  @media (max-width: 1200px) {
    .carousel-content {
      padding: 40px 40px;
      
      .carousel-title {
        font-size: 44px;
        max-width: 75%;
      }
      
      .carousel-subtitle {
        font-size: 18px;
        max-width: 65%;
      }
    }
  }
}

// 呼吸灯动画
@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.2);
  }
}

// 风车旋转动画
@keyframes windmill-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 右侧固定导航条
.side-nav {
  position: fixed;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1000;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.1);
  padding: 12px 0;
  
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
      background: #f5f7fa;
      
      .el-icon {
        color: #409eff;
        animation: icon-bounce 0.6s ease;
      }
      
      .nav-text {
        color: #409eff;
      }
    }
    
    &:active {
      background: #e8edf3;
      
      .el-icon {
        transform: scale(0.9);
      }
    }
    
    &:not(:last-child) {
      border-bottom: 1px solid #f0f0f0;
    }
  }
}

// 图标弹跳动画
@keyframes icon-bounce {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  25% {
    transform: translateY(-8px) scale(1.1);
  }
  50% {
    transform: translateY(0) scale(1);
  }
  75% {
    transform: translateY(-4px) scale(1.05);
  }
}

// 核心功能
// 核心功能和个人中心容器
.features-myspace-container {
  display: flex;
  gap: 24px;
  margin: 0 40px;
  align-items: flex-start;
}

.features-section {
  flex: 1;
  min-width: 0;
  padding: 40px 40px 20px;
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-top: 20px;
    
    .feature-card {
      position: relative;
      background: white;
      border-radius: 20px;
      padding: 30px;
      cursor: pointer;
      overflow: hidden;
      transition: all 0.3s ease;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
        
        .feature-background {
          opacity: 0.1;
        }
        
        .feature-arrow {
          transform: translateX(5px);
        }
      }
      
      .feature-background {
        position: absolute;
        width: 200px;
        height: 200px;
        border-radius: 50%;
        top: -100px;
        right: -100px;
        opacity: 0.05;
        transition: all 0.3s ease;
        pointer-events: none;
      }
      
      .feature-title {
        font-size: 20px;
        font-weight: 700;
        margin: 0 0 10px 0;
        color: #303133;
        pointer-events: none;
      }
      
      .feature-desc {
        font-size: 14px;
        color: #909399;
        margin: 0;
        line-height: 1.6;
        pointer-events: none;
      }
      
      .feature-arrow {
        position: absolute;
        bottom: 20px;
        right: 20px;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: rgba(102, 126, 234, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #667eea;
        transition: all 0.3s ease;
        pointer-events: none;
      }
    }
  }
  
  // 响应式设计
  @media (max-width: 1200px) {
    .features-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
  
  @media (max-width: 768px) {
    padding: 30px 20px 20px;
    
    .features-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }
  }
}

// 个人中心
.my-space-section {
  flex: 1;
  min-width: 0;
  padding: 40px 40px 20px;
  
  .my-space-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 24px;
    margin-top: 20px;
    
    .space-card {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: #ffffff;
      border-radius: 12px;
      padding: 24px 28px;
      cursor: pointer;
      overflow: hidden;
      transition: all 0.25s ease;
      border: 1px solid #e8eaed;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background: linear-gradient(180deg, #409EFF 0%, #337ECC 100%);
        transform: scaleY(0);
        transform-origin: top;
        transition: transform 0.3s ease;
      }
      
      &:hover {
        border-color: #409EFF;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
        
        &::before {
          transform: scaleY(1);
        }
        
        .space-arrow {
          color: #409EFF;
          transform: translateX(3px);
        }
      }
      
      .space-content {
        flex: 1;
        position: relative;
        z-index: 1;
        pointer-events: none;
        
        .space-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 10px;
          
          .space-title {
            font-size: 18px;
            font-weight: 600;
            margin: 0;
            color: #1a1a1a;
            letter-spacing: 0;
          }
          
          .space-badge {
            min-width: 20px;
            height: 20px;
            padding: 0 6px;
            background: #ff4757;
            color: white;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 11px;
            font-weight: 600;
            line-height: 1;
          }
        }
        
        .space-desc {
          font-size: 13px;
          color: #8e8e93;
          margin: 0 0 16px 0;
          line-height: 1.5;
        }
        
        .space-stats {
          display: flex;
          gap: 28px;
          
          .stat-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: #3a3a3c;
            font-weight: 500;
            
            .el-icon {
              font-size: 16px;
              color: #8e8e93;
            }
          }
        }
      }
      
      .space-arrow {
        position: relative;
        z-index: 1;
        color: #8e8e93;
        font-size: 18px;
        transition: all 0.25s ease;
        pointer-events: none;
        flex-shrink: 0;
        margin-left: 16px;
      }
    }
  }
  
  // 响应式设计
  @media (max-width: 1024px) {
    .my-space-grid {
      grid-template-columns: 1fr;
    }
  }
  
  @media (max-width: 768px) {
    padding: 20px;
    
    .my-space-grid {
      gap: 16px;
      
      .space-card {
        padding: 20px 24px;
        
        .space-content {
          .space-header {
            .space-title {
              font-size: 17px;
            }
          }
          
          .space-desc {
            font-size: 12px;
            margin-bottom: 14px;
          }
          
          .space-stats {
            gap: 20px;
            
            .stat-item {
              font-size: 13px;
              
              .el-icon {
                font-size: 15px;
              }
            }
          }
        }
        
        .space-arrow {
          font-size: 16px;
          margin-left: 12px;
        }
      }
    }
  }
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

// 通用节标题
.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  
  .title-icon {
    font-size: 32px;
  }
}

.section-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 8px 0 0 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

// 修改查看全部/查看详情按钮颜色为灰色系（使用更高优先级）
.section-header :deep(.el-button.el-button--text.el-button--primary) {
  color: #606266 !important;
  
  &:hover {
    color: #909399 !important;
    background-color: transparent !important;
  }
  
  &:focus {
    color: #909399 !important;
    background-color: transparent !important;
  }
  
  .el-icon {
    color: inherit !important;
  }
}

// 推荐区域
// 为你推荐和热门攻略容器
.recommendations-plans-container {
  display: flex;
  gap: 24px;
  margin: 0 40px;
  align-items: flex-start;
}

.recommendations-section {
  flex: 1;
  min-width: 0;
  padding: 40px 40px 20px;
  
  .recommendations-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 24px;
    
    .recommendation-card {
      background: white;
      border-radius: 20px;
      overflow: hidden;
      cursor: pointer;
      transition: box-shadow 0.3s ease;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      display: flex;
      flex-direction: row;
      
      &:hover {
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
      }
      
      .recommendation-image {
        position: relative;
        flex: 2;
        height: 280px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .recommendation-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.3);
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: all 0.3s ease;
          
          .like-btn {
            background: white;
            color: #f5576c;
            
            &:hover {
              background: #f5576c;
              color: white;
            }
          }
        }
        
        .recommendation-badge {
          position: absolute;
          top: 12px;
          left: 12px;
          padding: 6px 14px;
          background: linear-gradient(135deg, rgba(245, 87, 108, 0.95) 0%, rgba(240, 147, 251, 0.95) 100%);
          backdrop-filter: blur(10px);
          border-radius: 8px;
          font-size: 12px;
          font-weight: 600;
          color: white;
          box-shadow: 0 2px 8px rgba(245, 87, 108, 0.3);
          border: 1px solid rgba(255, 255, 255, 0.3);
        }
      }
      
      .recommendation-content {
        flex: 1;
        padding: 24px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-width: 0;
        
        .recommendation-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 12px;
          
          .recommendation-title {
            flex: 1;
            font-size: 20px;
            font-weight: 700;
            margin: 0;
            color: #303133;
            line-height: 1.5;
          }
          
          .recommendation-rating {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            white-space: nowrap;
            margin-left: 8px;
          }
        }
        
        .recommendation-location {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 15px;
          color: #909399;
          
          .el-icon {
            display: flex;
            align-items: center;
          }
          margin-bottom: 16px;
        }
        
        .recommendation-tags {
          display: flex;
          gap: 8px;
          margin-bottom: 16px;
          flex-wrap: wrap;
          
          .tag {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 6px 14px;
            background: #f5f5f5;
            color: #606266;
            border: 1px solid #e4e7ed;
            border-radius: 6px;
            font-size: 13px;
            transition: all 0.2s ease;
            
            .tag-windmill {
              display: none; // 移除风车图标
            }
            
            &:hover {
              background: #f0f0f0;
              border-color: #d3d4d6;
            }
          }
        }
        
        .recommendation-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 12px;
          border-top: 1px solid #f0f0f0;
          
          .price {
            font-size: 20px;
            font-weight: 700;
            background: linear-gradient(135deg, #f5576c 0%, #ff6b81 100%);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-shadow: 0 2px 8px rgba(245, 87, 108, 0.2);
            letter-spacing: 0.5px;
          }
          
          .stats-group {
            display: flex;
            align-items: center;
            gap: 16px;
            
            .favorites {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 13px;
              color: #909399;
              
              .el-icon {
                color: #FFD700;
                display: flex;
                align-items: center;
              }
            }
            
            .views {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 13px;
              color: #909399;
              
              .el-icon {
                display: flex;
                align-items: center;
              }
            }
            
            .comments {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 13px;
              color: #909399;
              
              .el-icon {
                display: flex;
                align-items: center;
              }
            }
          }
        }
      }
    }
  }
}

// 攻略区域
.plans-section {
  flex: 1;
  min-width: 0;
  padding: 40px 40px 20px;
  
  .plans-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
    
    .plan-card {
      display: flex;
      background: white;
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      
      &:hover {
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
      }
      
      .plan-image {
        width: 280px;
        height: 180px;
        flex-shrink: 0;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.3s ease;
        }
      }
      
      .plan-content {
        flex: 1;
        padding: 20px 24px;
        display: flex;
        flex-direction: column;
        
        .plan-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 12px;
          
          .plan-title {
            flex: 1;
            font-size: 18px;
            font-weight: 700;
            margin: 0;
            color: #303133;
            line-height: 1.4;
          }
        }
        
        .plan-destination {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 10px;
          font-size: 13px;
          color: #909399;
          
          .el-icon {
            color: #409eff;
            font-size: 14px;
          }
        }
        
        .plan-desc {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          margin: 0 0 auto 0;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .plan-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 16px;
          padding-top: 16px;
          border-top: 1px solid #f0f0f0;
          
          .author-info {
            display: flex;
            align-items: center;
            gap: 10px;
            
            .author-name {
              font-size: 14px;
              color: #303133;
              font-weight: 500;
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
          }
          
          .plan-stats {
            display: flex;
            gap: 16px;
            font-size: 13px;
            color: #909399;
            
            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }
}

// 文旅区域
.culture-section {
  flex: 1;
  min-width: 0;
  padding: 40px 40px 20px;
  
  .culture-grid {
    margin-bottom: 20px;
    
    .el-col {
      margin-bottom: 20px;
    }
    
    .culture-card {
      background: white;
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      transition: box-shadow 0.3s ease;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      height: 100%;
      
      &:hover {
        transform: none;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        
        img {
          transform: none;
        }
      }
      
      // 占位符样式
      &.is-placeholder {
        opacity: 0.5;
        cursor: default;
        
        &:hover {
          transform: none;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
        }
      }
      
      .culture-image {
        position: relative;
        height: 200px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: none;
        }
        
        .culture-type {
          position: absolute;
          top: 12px;
          right: 12px;
          padding: 8px 16px;
          background: #ffffff;
          border: 1px solid #e0e0e0;
          border-radius: 8px;
          font-size: 13px;
          font-weight: 700;
          color: #333333;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
          letter-spacing: 0.5px;
        }
      }
      
      .culture-content {
        padding: 18px;
        
        .culture-title {
          font-size: 16px;
          font-weight: 700;
          margin: 0 0 10px 0;
          color: #303133;
        }
        
        .culture-location {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 13px;
          color: #909399;
          margin-bottom: 12px;
        }
        
        .culture-features {
          display: flex;
          gap: 6px;
          flex-wrap: wrap;
          
          .feature-tag {
            padding: 4px 10px;
            background: rgba(67, 233, 123, 0.08);
            color: #43e97b;
            border-radius: 8px;
            font-size: 12px;
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 1400px) {
  .welcome-section {
    .welcome-content {
      grid-template-columns: 1fr;
      gap: 24px;
    }
    
    .weather-metrics {
      grid-template-columns: repeat(2, 1fr) !important;
    }
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .recommendations-grid {
    grid-template-columns: 1fr !important;
    
    .recommendation-card {
      .recommendation-image {
        flex: 2;
        height: 260px;
      }
    }
  }
}

@media (max-width: 992px) {
  .welcome-section {
    padding: 30px 40px;
    
    .user-header {
      flex-direction: column;
      text-align: center;
      
      .user-greeting {
        .welcome-title {
          font-size: 28px;
        }
      }
    }
    
    .weather-metrics {
      grid-template-columns: repeat(2, 1fr) !important;
    }
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .recommendations-grid {
    grid-template-columns: 1fr !important;
    
    .recommendation-card {
      .recommendation-image {
        flex: 2;
        height: 240px;
      }
      
      .recommendation-content {
        padding: 20px;
        
        .recommendation-header .recommendation-title {
          font-size: 18px;
        }
      }
    }
  }
  
  .culture-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .welcome-section {
    padding: 20px 40px;
    
    .welcome-content {
      grid-template-columns: 1fr;
    }
    
    .user-header {
      .user-avatar {
        width: 60px !important;
        height: 60px !important;
      }
      
      .user-greeting {
        .welcome-title {
          font-size: 24px;
        }
        
        .welcome-subtitle {
          font-size: 14px;
        }
      }
    }
    
    
    .weather-info-card {
      padding: 20px;
      
      .weather-main {
        flex-direction: column;
        align-items: flex-start;
        
        .weather-icon-wrapper {
          .weather-icon {
            font-size: 36px;
          }
          
          .weather-temp {
            font-size: 32px;
          }
        }
      }
      
      .weather-metrics {
        grid-template-columns: repeat(2, 1fr) !important;
        gap: 12px;
        
        .metric-item {
          padding: 10px;
        }
      }
    }
    
    .profile-guide-card {
      padding: 24px;
      
      .guide-content {
        .guide-title {
          font-size: 20px;
        }
        
        .guide-desc {
          font-size: 13px;
        }
        
        .guide-stats {
          flex-direction: column;
          gap: 8px;
        }
      }
    }
  }
  
  .features-grid,
  .recommendations-grid {
    grid-template-columns: 1fr !important;
    
    .recommendation-card {
      flex-direction: column;
      
      .recommendation-image {
        width: 100%;
        min-width: 100%;
        height: 220px;
      }
      
      .recommendation-content {
        padding: 20px;
        
        .recommendation-header .recommendation-title {
          font-size: 18px;
        }
        
        .recommendation-location {
          font-size: 14px;
        }
      }
    }
  }
  
  .culture-grid {
    grid-template-columns: 1fr !important;
  }
  
  .plan-card {
    flex-direction: column;
    
    .plan-image {
      width: 100%;
      height: 200px;
    }
  }
}

/* 底部信息 */
.dashboard-footer {
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
    font-size: 14px;
    
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
    font-size: 13px;
    line-height: 1.8;
    color: #909399;
    
    p {
      margin: 4px 0;
    }
  }
}

@media (max-width: 768px) {
  .dashboard-footer {
    padding: 30px 20px 20px;
    
    .footer-links {
      font-size: 13px;
      gap: 6px;
    }
    
    .footer-info {
      font-size: 12px;
    }
  }
}

// 位置选择对话框样式
.location-selector-dialog {
  :deep(.el-dialog) {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  }
  
  :deep(.el-dialog__header) {
    padding: 16px 20px !important;
    background: #ffffff !important;
    border-bottom: 1px solid #f0f0f0 !important;
    margin: 0 !important;
    
    .el-dialog__title {
      font-size: 22px;
      font-weight: 600;
      color: #1f2937;
    }
    
    .el-dialog__headerbtn {
      top: 20px;
      right: 20px;
      
      .el-dialog__close {
        color: #909399;
        font-size: 18px;
        transition: color 0.2s ease;
        
        &:hover {
          color: #606266;
        }
      }
    }
  }
  
  :deep(.el-dialog__body) {
    padding: 20px 24px 16px !important;
    background: #ffffff !important;
  }
  
  :deep(.el-dialog__footer) {
    padding: 10px 20px 16px !important;
    background: #ffffff !important;
    border-top: 1px solid #f0f0f0 !important;
    margin: 0 !important;
  }
  
  // 位置选择提示框样式
  .location-warning {
    background: #fff5f5;
    border: 1px solid #fecaca;
    color: #c2410c;
    padding: 16px 20px;
    border-radius: 8px;
    font-size: 15px;
    line-height: 1.7;
    margin-bottom: 20px;

    .location-warning-title {
      font-weight: 600;
      font-size: 16px;
      margin: 0 0 8px 0;
    }

    .location-warning-list {
      margin: 0;
      padding-left: 20px;

      li {
        margin: 4px 0;
      }
    }
  }
  
  // 分隔线样式
  .location-divider {
    height: 1px;
    background: #f0f0f0;
    margin: 0 0 20px 0;
  }
  
  .location-selector-content {
    :deep(.el-form-item) {
      margin-bottom: 24px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      // 隐藏错误提示（使用 ElMessage 弹出提示代替）
      .el-form-item__error {
        display: none !important;
      }
      
      .el-form-item__label {
        font-size: 16px !important;
        font-weight: 500 !important;
        color: #606266 !important;
        padding-bottom: 10px !important;
        line-height: 1.5;
      }
      
      .el-form-item__content {
        .el-input {
          --el-input-focus-border-color: #e4e7ed !important;
          --el-border-color: #e4e7ed !important;
          --el-color-primary: #e4e7ed !important;
          
          .el-input__wrapper {
            border-radius: 8px !important;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
            transition: none !important;
            background: white !important;
            border: 1px solid #e4e7ed !important;
            border-color: #e4e7ed !important;
            padding: 10px 16px !important;
            min-height: 42px !important;
            outline: none !important;
            
            &:hover {
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              border-color: #e4e7ed !important;
              border: 1px solid #e4e7ed !important;
              background: white !important;
              --el-input-focus-border-color: #e4e7ed !important;
              --el-border-color: #e4e7ed !important;
            }
            
            &.is-focus,
            &:focus,
            &:focus-visible,
            &:focus-within {
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
              border-color: #e4e7ed !important;
              border: 1px solid #e4e7ed !important;
              background: white !important;
              --el-input-focus-border-color: #e4e7ed !important;
              --el-border-color: #e4e7ed !important;
              --el-color-primary: #e4e7ed !important;
              outline: none !important;
            }
          }
          
          .el-input__inner {
            font-size: 15px !important;
            color: #303133 !important;
            line-height: 1.6;
            
            &::placeholder {
              font-size: 15px !important;
              color: #909399 !important;
            }
          }
        }
      }
    }
  }
  
  .location-dialog-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 14px;
    
    .location-dialog-btn {
      min-width: 100px;
      min-height: 36px;
      padding: 8px 20px;
      border-radius: 6px;
      font-weight: 500;
      font-size: 14px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      transition: none;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid #dcdfe6;
      background-color: white;
      color: #606266;
      
      &:hover {
        border-color: #c0c4cc;
        color: #303133;
        background-color: white;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      }
      
      &:active {
        border-color: #c0c4cc;
        color: #303133;
        background-color: white;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      }
      
      &:focus {
        border-color: #c0c4cc;
        color: #606266;
        background-color: #f5f7fa;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      }
      
      &.is-loading {
        border-color: #dcdfe6;
        background: #f5f7fa;
        color: #909399;
      }
    }
  }
}

// 天气位置选择按钮样式
.weather-location {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  
  .location-select-btn {
    padding: 4px;
    margin-left: 4px;
    color: rgba(100, 100, 100, 0.8);
    font-size: 14px;
    opacity: 0.7;
    transition: all 0.3s;
    
    &:hover {
      opacity: 1;
      color: white;
      background: rgba(255, 255, 255, 0.15);
    }
    
    :deep(.el-icon) {
      font-size: 16px;
    }
  }
  
  // 公告详情对话框样式
  .announcement-detail-dialog {
    :deep(.el-dialog) {
      border-radius: 12px !important;
      overflow: hidden;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;
      max-height: 75vh !important;
      display: flex !important;
      flex-direction: column !important;
    }
    
    :deep(.el-dialog__header) {
      padding: 24px 28px 20px !important;
      border-bottom: none !important;
      margin-right: 0 !important;
      margin: 0 !important;
      flex-shrink: 0 !important;
      
      .dialog-header-custom {
        width: 100%;
        
        .dialog-title {
          font-size: 28px !important;
          font-weight: 600 !important;
          color: #303133 !important;
          line-height: 1.4;
        }
      }
    }
    
    :deep(.el-dialog__body) {
      padding: 0 28px 28px !important;
      background: #fff !important;
      flex: 1 !important;
      overflow-y: auto !important;
      min-height: 300px !important;
    }
    
    :deep(.el-dialog__footer) {
      padding: 20px 28px !important;
      border-top: 1px solid #ebeef5 !important;
      margin: 0 !important;
      flex-shrink: 0 !important;
    }
  }
  
  .announcement-detail {
    .announcement-header {
      margin-bottom: 20px;
      
      .announcement-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        flex-wrap: wrap;
        
        .type-tag {
          font-size: 15px;
          padding: 8px 18px;
          border-radius: 6px;
          font-weight: 500;
          height: auto;
          line-height: 1.5;
          display: inline-flex;
          align-items: center;
          border: 1px solid transparent;
          background: transparent;
        }
        
        .announcement-date {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #606266;
          font-size: 16px;
          line-height: 1.5;
          
          .date-icon {
            font-size: 16px;
            color: #909399;
            display: inline-flex;
            align-items: center;
          }
          
          .date-text {
            display: inline-block;
            vertical-align: middle;
          }
        }
      }
    }
    
    .announcement-content {
      min-height: 250px;
      padding-top: 8px;
      
      .content-text {
        line-height: 2;
        color: #303133;
        font-size: 18px;
        word-wrap: break-word;
        letter-spacing: 0.3px;
        
        :deep(p) {
          margin: 0 0 0 0;
          padding: 0;
          text-indent: 2em; /* 段落首行缩进两个字的间距 */
          line-height: 2;
        }
        
        :deep(p + p) {
          margin-top: 1em; /* 段落之间的间距 */
        }
        
        :deep(br) {
          line-height: 2;
        }
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    
    .close-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      padding: 12px 28px;
      font-size: 16px;
      border-radius: 4px;
      border: 1px solid #dcdfe6 !important;
      background: #fff !important;
      background-color: #fff !important;
      color: #606266 !important;
      font-weight: 400;
      font-size: 14px;
      box-shadow: none !important;
      transition: border-color 0.2s ease, background-color 0.2s ease;
      min-width: 80px;
      cursor: pointer;

      &,
      &:focus,
      &:focus-visible {
        border: 1px solid #dcdfe6 !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        outline: none !important;
      }

      :deep(.el-button__inner) {
        color: #606266 !important;
      }

      &:hover {
        border-color: #c0c4cc !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        
        :deep(.el-button__inner) {
          color: #606266 !important;
        }
      }

      &:active,
      &:focus {
        border-color: #c0c4cc !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        
        :deep(.el-button__inner) {
          color: #606266 !important;
        }
      }
    }
  }
}
</style>

<!-- 公告详情对话框全局样式（对话框渲染在body下，需要使用全局样式） -->
<style lang="scss">
// 覆盖公告对话框关闭按钮的样式
.announcement-detail-dialog {
  .dialog-footer {
    .close-btn {
      border: 1px solid #dcdfe6 !important;
      border-color: #dcdfe6 !important;
      background: #fff !important;
      background-color: #fff !important;
      color: #606266 !important;
      font-size: 16px !important;
      padding: 12px 28px !important;
      
      &,
      &:focus,
      &:focus-visible {
        border: 1px solid #dcdfe6 !important;
        border-color: #dcdfe6 !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        outline: none !important;
        font-size: 16px !important;
        padding: 12px 28px !important;
      }
      
      &:hover {
        border-color: #c0c4cc !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        font-size: 16px !important;
        padding: 12px 28px !important;
      }
      
      &:focus,
      &:active {
        border-color: #c0c4cc !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        box-shadow: none !important;
        font-size: 16px !important;
        padding: 12px 28px !important;
      }
    }
  }
}
</style>

<style lang="scss">
.el-overlay .announcement-detail-dialog {
  .el-dialog {
    border-radius: 12px !important;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;
    max-height: 75vh !important;
    display: flex !important;
    flex-direction: column !important;
  }
  
  .el-dialog__header {
    padding: 24px 28px 20px !important;
    border-bottom: none !important;
    margin-right: 0 !important;
    margin: 0 !important;
    flex-shrink: 0 !important;
    
    .dialog-header-custom {
      width: 100%;
      
      .dialog-title {
        font-size: 28px !important;
        font-weight: 600 !important;
        color: #303133 !important;
        line-height: 1.4;
        margin-bottom: 8px;
      }

      .dialog-subtitle {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-top: 8px;

        .type-tag {
          font-size: 15px;
          padding: 8px 18px;
          border: 1px solid transparent;
          border-radius: 999px;
          background: transparent;
        }

        .announcement-date {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          font-size: 16px;
          color: #909399;

          .date-icon {
            font-size: 16px;
          }
        }
      }
    }
  }
  
  .el-dialog__body {
    padding: 0 28px 28px !important;
    background: #fff !important;
    flex: 1 !important;
    overflow-y: auto !important;
    min-height: 300px !important;
  }
  
  .el-dialog__footer {
    padding: 20px 28px !important;
    border-top: 1px solid #ebeef5 !important;
    margin: 0 !important;
    flex-shrink: 0 !important;
    
    .dialog-footer {
      .close-btn {
        border: 1px solid #dcdfe6 !important;
        border-color: #dcdfe6 !important;
        background: #fff !important;
        background-color: #fff !important;
        color: #606266 !important;
        font-size: 16px !important;
        padding: 12px 28px !important;
        
        // 覆盖 Element Plus 按钮的所有可能样式
        &,
        &.el-button--default,
        &.el-button--primary,
        &.el-button--text,
        &.el-button--plain,
        &:focus,
        &:focus-visible {
          border: 1px solid #dcdfe6 !important;
          border-color: #dcdfe6 !important;
          background: #fff !important;
          background-color: #fff !important;
          color: #606266 !important;
          box-shadow: none !important;
          outline: none !important;
          font-size: 16px !important;
          padding: 12px 28px !important;
        }
        
        :deep(.el-button__inner) {
          color: #606266 !important;
          font-size: 16px !important;
        }
        
        &:hover {
          border-color: #c0c4cc !important;
          background: #fff !important;
          background-color: #fff !important;
          color: #606266 !important;
          box-shadow: none !important;
          
          :deep(.el-button__inner) {
            color: #606266 !important;
          }
        }
        
        &:active,
        &:focus {
          border-color: #c0c4cc !important;
          background: #fff !important;
          background-color: #fff !important;
          color: #606266 !important;
          box-shadow: none !important;
          
          :deep(.el-button__inner) {
            color: #606266 !important;
          }
        }
      }
    }
  }
  
  // 全局覆盖查看全部/查看详情按钮颜色
  .section-header .el-button.el-button--text.el-button--primary,
  .view-all-btn {
    color: #606266 !important;
    
    &:hover {
      color: #909399 !important;
      background-color: transparent !important;
    }
    
    &:focus {
      color: #909399 !important;
      background-color: transparent !important;
    }
    
    .el-icon {
      color: inherit !important;
    }
  }
  
  .announcement-detail {
    .announcement-content {
      .content-text {
        p {
          margin: 0 0 0 0;
          padding: 0;
          text-indent: 2em !important; /* 段落首行缩进两个字的间距 */
          line-height: 2;
        }
        
        p + p {
          margin-top: 1em !important; /* 段落之间的间距 */
        }
      }
    }
  }
}
</style>

