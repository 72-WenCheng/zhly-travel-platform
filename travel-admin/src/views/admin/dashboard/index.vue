<template>
  <div class="admin-dashboard">
    <!-- 顶部控制栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h2 class="dashboard-title">
          <el-icon><DataAnalysis /></el-icon>
          系统监测面板
        </h2>
        <div class="status-info">
          <div class="status-text">
            <el-icon class="status-icon"><Refresh /></el-icon>
            <span>数据每30秒自动刷新</span>
          </div>
          <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-button type="success" class="action-btn export-btn" @click="exportData">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 核心指标 - 更详细版本 -->
    <el-row :gutter="20" class="overview-stats">
      <el-col :span="6">
        <div class="stat-card-enhanced stat-users" @click="handleCardClick('users')">
          <div class="card-header-mini">
            <span class="card-title">用户统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ stats.totalUsers }}</div>
            <div class="main-label">总用户数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日新增</span>
                <span class="value highlight">+{{ stats.todayUsers }}</span>
              </div>
              <div class="detail-item">
                <span class="label">本月新增</span>
                <span class="value">+{{ stats.monthlyUsers }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">活跃用户</span>
                <span class="value success">{{ stats.activeUsers }}</span>
              </div>
              <div class="detail-item">
                <span class="label">在线</span>
                <span class="value online">{{ stats.onlineUsers }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              较昨日 <span :class="stats.userGrowthRate >= 0 ? 'up' : 'down'">{{ stats.userGrowthRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card-enhanced stat-plans" @click="handleCardClick('plans')">
          <div class="card-header-mini">
            <span class="card-title">攻略统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ stats.totalPlans }}</div>
            <div class="main-label">总攻略数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日发布</span>
                <span class="value highlight">+{{ stats.todayPlans }}</span>
              </div>
              <div class="detail-item">
                <span class="label">本月发布</span>
                <span class="value">+{{ stats.monthlyPlans }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">待审核</span>
                <span class="value warning">{{ stats.pendingPlans }}</span>
              </div>
              <div class="detail-item">
                <span class="label">平均浏览</span>
                <span class="value">{{ stats.avgPlanViews }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              较昨日 <span :class="stats.planGrowthRate >= 0 ? 'up' : 'down'">{{ stats.planGrowthRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card-enhanced stat-attractions" @click="handleCardClick('attractions')">
          <div class="card-header-mini">
            <span class="card-title">景点统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ stats.totalAttractions }}</div>
            <div class="main-label">总景点数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">已上架</span>
                <span class="value success">{{ stats.publishedAttractions }}</span>
              </div>
              <div class="detail-item">
                <span class="label">已下架</span>
                <span class="value">{{ stats.unpublishedAttractions }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">平均评分</span>
                <span class="value">{{ stats.avgRating }}⭐</span>
              </div>
              <div class="detail-item">
                <span class="label">总浏览</span>
                <span class="value">{{ formatNumber(stats.totalAttractionViews) }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              上架率 <span class="up">{{ stats.publishRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card-enhanced stat-orders" @click="handleCardClick('orders')">
          <div class="card-header-mini">
            <span class="card-title">订单统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ stats.totalOrders }}</div>
            <div class="main-label">总订单数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日订单</span>
                <span class="value highlight">+{{ stats.todayOrders }}</span>
              </div>
              <div class="detail-item">
                <span class="label">本月订单</span>
                <span class="value">+{{ stats.monthlyOrders }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">待支付</span>
                <span class="value warning">{{ stats.pendingOrders }}</span>
              </div>
              <div class="detail-item">
                <span class="label">总金额</span>
                <span class="value amount">¥{{ formatNumber(stats.totalAmount) }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              转化率 <span class="up">{{ stats.conversionRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 互动数据统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <div class="stat-card-enhanced stat-comments" @click="handleCardClick('comments')">
          <div class="card-header-mini">
            <span class="card-title">评论统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ contentStats.totalComments }}</div>
            <div class="main-label">总评论数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日评论</span>
                <span class="value highlight">{{ contentStats.todayComments }}</span>
              </div>
              <div class="detail-item">
                <span class="label">待审核</span>
                <span class="value warning">{{ contentStats.pendingComments }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">审核率</span>
                <span class="value">{{ contentStats.commentApprovalRate }}%</span>
              </div>
              <div class="detail-item">
                <span class="label">平均评论</span>
                <div class="value value-multi">
                  <div>攻略 {{ contentStats.avgCommentsPerPlan }}</div>
                  <div>景点 {{ contentStats.avgCommentsPerAttraction }}</div>
                </div>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">攻略评论</span>
                <span class="value value-with-sub">
                  {{ contentStats.planComments }}
                  <span class="sub-value">今日 +{{ contentStats.planCommentsToday }}</span>
                </span>
              </div>
              <div class="detail-item">
                <span class="label">景点评论</span>
                <span class="value value-with-sub">
                  {{ contentStats.attractionComments }}
                  <span class="sub-value">今日 +{{ contentStats.attractionCommentsToday }}</span>
                </span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              较昨日 <span :class="contentStats.commentGrowthRate >= 0 ? 'up' : 'down'">{{ contentStats.commentGrowthRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>

      <!-- 收藏统计 -->
      <el-col :span="6">
        <div class="stat-card-enhanced stat-favorites" @click="handleCardClick('favorites')">
          <div class="card-header-mini">
            <span class="card-title">收藏统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ interactionStats.totalFavorites }}</div>
            <div class="main-label">总收藏数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日收藏</span>
                <span class="value highlight">{{ interactionStats.todayFavorites }}</span>
              </div>
              <div class="detail-item">
                <span class="label">攻略收藏</span>
                <span class="value">{{ interactionStats.planFavorites }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">景点收藏</span>
                <span class="value">{{ interactionStats.attractionFavorites }}</span>
              </div>
              <div class="detail-item">
                <span class="label">转化率</span>
                <span class="value success">{{ interactionStats.favoriteRate }}%</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              较昨日 <span :class="interactionStats.favoriteGrowthRate >= 0 ? 'up' : 'down'">{{ interactionStats.favoriteGrowthRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>

      <!-- 收藏 & 浏览 -->
      <el-col :span="6">
        <div class="stat-card-enhanced stat-browse" @click="handleCardClick('views')">
          <div class="card-header-mini">
            <span class="card-title">收藏 & 浏览</span>
          </div>
          <div class="card-main">
            <div class="main-metrics">
              <div class="metric-block">
                <div class="metric-value">{{ interactionStats.totalViews }}</div>
                <div class="metric-label">总浏览数</div>
              </div>
              <div class="metric-block">
                <div class="metric-value">{{ interactionStats.totalFavorites }}</div>
                <div class="metric-label">总收藏数</div>
              </div>
            </div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日浏览</span>
                <span class="value highlight">{{ interactionStats.todayViews }}</span>
              </div>
              <div class="detail-item">
                <span class="label">今日收藏</span>
                <span class="value highlight">{{ interactionStats.todayFavorites }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">攻略浏览</span>
                <span class="value value-with-sub">
                  {{ interactionStats.planViews }}
                  <span class="sub-value">今日 +{{ interactionStats.todayPlanViews }}</span>
                </span>
              </div>
              <div class="detail-item">
                <span class="label">景点浏览</span>
                <span class="value value-with-sub">
                  {{ interactionStats.attractionViews }}
                  <span class="sub-value">今日 +{{ interactionStats.todayAttractionViews }}</span>
                </span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">攻略收藏</span>
                <span class="value">{{ interactionStats.planFavorites }}</span>
              </div>
              <div class="detail-item">
                <span class="label">景点收藏</span>
                <span class="value">{{ interactionStats.attractionFavorites }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              浏览较昨日 <span :class="interactionStats.viewGrowthRate >= 0 ? 'up' : 'down'">{{ interactionStats.viewGrowthRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>

      <!-- AI使用统计 -->
      <el-col :span="6">
        <div class="stat-card-enhanced stat-ai" @click="handleCardClick('ai')">
          <div class="card-header-mini">
            <span class="card-title">AI使用统计</span>
          </div>
          <div class="card-main">
            <div class="main-value">{{ aiUsageStats.totalRequests }}</div>
            <div class="main-label">总请求数</div>
          </div>
          <div class="card-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">今日请求</span>
                <span class="value highlight">{{ aiUsageStats.todayRequests }}</span>
              </div>
              <div class="detail-item">
                <span class="label">成功率</span>
                <span class="value success">{{ aiUsageStats.successRate }}%</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">响应时间</span>
                <span class="value">{{ aiUsageStats.avgResponseTime }}ms</span>
              </div>
              <div class="detail-item">
                <span class="label">使用率</span>
                <span class="value">{{ aiUsageStats.usageRate }}%</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend-text">
              <el-icon><TrendCharts /></el-icon>
              较昨日 <span :class="aiUsageStats.requestGrowthRate >= 0 ? 'up' : 'down'">{{ aiUsageStats.requestGrowthRate }}%</span>
            </span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主要监测区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 左侧：用户活跃度 -->
      <el-col :span="8">
        <el-card class="monitor-card">
          <template #header>
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span>用户活跃度</span>
            </div>
          </template>
          <div class="activity-stats-enhanced">
            <div class="activity-card">
              <div class="activity-label">在线用户</div>
              <div class="activity-value">{{ activeStats.onlineUsers }}<span class="unit">人</span></div>
            </div>
            <div class="activity-card">
              <div class="activity-label">今日访问</div>
              <div class="activity-value">{{ activeStats.todayVisits }}<span class="unit">人</span></div>
            </div>
            <div class="activity-card">
              <div class="activity-label">今日新增</div>
              <div class="activity-value">{{ activeStats.todayNew }}<span class="unit">人</span></div>
            </div>
          </div>
          <div class="chart-container">
            <v-chart :option="userTrendOption" style="height: 240px;" />
          </div>
          <div class="user-distribution">
            <h4>用户分布</h4>
            <div class="distribution-item">
              <div class="distribution-header">
                <span class="label">活跃用户</span>
                <span class="percentage">{{ activeStats.activeRate }}%</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: activeStats.activeRate + '%', backgroundColor: '#67c23a' }"></div>
              </div>
            </div>
            <div class="distribution-item">
              <div class="distribution-header">
                <span class="label">新用户</span>
                <span class="percentage">{{ activeStats.newRate }}%</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: activeStats.newRate + '%', backgroundColor: '#409eff' }"></div>
              </div>
            </div>
            <div class="distribution-item">
              <div class="distribution-header">
                <span class="label">流失用户</span>
                <span class="percentage">{{ activeStats.lostRate }}%</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: activeStats.lostRate + '%', backgroundColor: '#f56c6c' }"></div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 中间：文旅业务监测 -->
      <el-col :span="8">
        <el-card class="monitor-card">
          <template #header>
            <div class="card-header">
              <el-icon><Shop /></el-icon>
              <span>文旅业务</span>
            </div>
          </template>
          <div class="business-stats-enhanced">
            <!-- 订单统计 -->
            <div class="stats-group">
              <div class="group-header">
                <span class="group-title">订单统计</span>
              </div>
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">总订单</div>
                  <div class="stat-value">{{ businessStats.totalOrders }}<span class="unit">单</span></div>
                </div>
                <div class="stat-item warning-item">
                  <div class="stat-label">待支付</div>
                  <div class="stat-value warning">{{ businessStats.pendingOrders }}<span class="unit">单</span></div>
                </div>
                <div class="stat-item success-item">
                  <div class="stat-label">已支付</div>
                  <div class="stat-value success">{{ businessStats.paidOrders }}<span class="unit">单</span></div>
                </div>
                <div class="stat-item highlight-item">
                  <div class="stat-label">订单金额</div>
                  <div class="stat-value amount">¥{{ formatNumber(businessStats.orderAmount) }}</div>
                </div>
              </div>
            </div>

            <!-- 优惠券统计 -->
            <div class="stats-group">
              <div class="group-header">
                <span class="group-title">优惠券使用</span>
              </div>
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">已发放</div>
                  <div class="stat-value">{{ businessStats.issuedCoupons }}<span class="unit">张</span></div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">已使用</div>
                  <div class="stat-value">{{ businessStats.usedCoupons }}<span class="unit">张</span></div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">使用率</div>
                  <div class="stat-value">{{ businessStats.couponRate }}<span class="unit">%</span></div>
                </div>
              </div>
            </div>

            <!-- 预订申请 -->
            <div class="stats-group">
              <div class="group-header">
                <span class="group-title">预订申请</span>
              </div>
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">文化体验</div>
                  <div class="stat-value">{{ businessStats.cultureBookings }}<span class="unit">次</span></div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">项目申请</div>
                  <div class="stat-value">{{ businessStats.projectApplications }}<span class="unit">个</span></div>
                </div>
                <div class="stat-item warning-item">
                  <div class="stat-label">待审核</div>
                  <div class="stat-value warning">{{ businessStats.pendingReview }}<span class="unit">个</span></div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：快捷操作 -->
      <el-col :span="8">
        <el-card class="monitor-card quick-actions-card">
          <template #header>
            <div class="card-header">
              <el-icon><Operation /></el-icon>
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <!-- 待处理事项（优先级高） -->
            <div class="actions-section priority-section">
              <div class="section-title">
                <el-icon><Warning /></el-icon>
                <span>待处理事项</span>
              </div>
              <div class="action-grid">
                <div class="action-item" @click="handleQuickAction('/home/admin/plans')">
                  <div class="action-icon warning">
                    <el-icon><Document /></el-icon>
            </div>
                  <div class="action-content">
                    <div class="action-label">审核攻略</div>
                    <div class="action-count" v-if="pendingActions.plans > 0">
                      {{ pendingActions.plans }}条待审核
                </div>
                    <div class="action-count" v-else>暂无待审核</div>
              </div>
                  <div class="action-badge" v-if="pendingActions.plans > 0">
                    <el-badge :value="pendingActions.plans" :max="99" />
                </div>
                  <el-icon class="action-arrow"><ArrowRight /></el-icon>
              </div>

                <div class="action-item" @click="handleQuickAction('/home/admin/report')">
                  <div class="action-icon danger">
                    <el-icon><Warning /></el-icon>
                </div>
                  <div class="action-content">
                    <div class="action-label">处理举报</div>
                    <div class="action-count" v-if="pendingActions.reports > 0">
                      {{ pendingActions.reports }}条待处理
              </div>
                    <div class="action-count" v-else>暂无举报</div>
                  </div>
                  <div class="action-badge" v-if="pendingActions.reports > 0">
                    <el-badge :value="pendingActions.reports" :max="99" type="danger" />
                  </div>
                  <el-icon class="action-arrow"><ArrowRight /></el-icon>
                </div>

                <div class="action-item" @click="handleQuickAction('/home/admin/culture/applications')">
                  <div class="action-icon primary">
                    <el-icon><Files /></el-icon>
                  </div>
                  <div class="action-content">
                    <div class="action-label">审核申请</div>
                    <div class="action-count" v-if="pendingActions.applications > 0">
                      {{ pendingActions.applications }}条待审核
                    </div>
                    <div class="action-count" v-else>暂无申请</div>
                  </div>
                  <div class="action-badge" v-if="pendingActions.applications > 0">
                    <el-badge :value="pendingActions.applications" :max="99" type="primary" />
                  </div>
                  <el-icon class="action-arrow"><ArrowRight /></el-icon>
                </div>
              </div>
            </div>

            <!-- 常用管理（日常操作） -->
            <div class="actions-section normal-section">
              <div class="section-title">
                <el-icon><Operation /></el-icon>
                <span>常用管理</span>
              </div>
              <div class="action-grid">
                <div
                  v-for="action in commonActions"
                  :key="action.code"
                  class="action-item"
                  @click="handleQuickAction(action)"
                >
                  <div class="action-icon info">
                    <el-icon>
                      <component :is="iconComponents[action.icon] || Operation" />
                    </el-icon>
                  </div>
                  <div class="action-content">
                    <div class="action-label">{{ action.label }}</div>
                    <div class="action-desc">{{ action.desc }}</div>
                  </div>
                  <el-icon class="action-arrow"><ArrowRight /></el-icon>
                </div>
                <el-empty
                  v-if="commonActions.length === 0"
                  description="暂无常用操作"
                  :image-size="70"
                />
              </div>
            </div>
          </div>

          <el-divider>
            <el-icon><Bell /></el-icon>
            <span style="margin-left: 8px;">实时通知</span>
          </el-divider>

          <div class="realtime-notifications">
            <div 
              v-for="notification in realtimeNotifications" 
              :key="notification.id" 
              class="notification-item"
            >
              <div class="notification-icon" :class="`type-${notification.type}`">
                <el-icon>
                  <component :is="notification.icon" />
                </el-icon>
              </div>
              <div class="notification-content">
                <div class="notification-text">{{ notification.message }}</div>
                <div class="notification-time">{{ notification.time }}</div>
              </div>
            </div>
            <el-empty v-if="realtimeNotifications.length === 0" description="暂无新通知" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 全球访问地图 -->
    <div class="world-map-wrapper">
      <el-card class="world-map-card">
        <template #header>
          <div class="world-map-header">
            <div class="header-left">
              <div>
                <div class="header-title">全球访问地图</div>
                <div class="header-subtitle">查看各区域访问与检索热度，支持快速查询</div>
              </div>
            </div>
          </div>
        </template>
        <div class="world-map-section">
          <div class="world-map-canvas">
            <template v-if="worldMapReady && worldMapData.length">
              <v-chart
                class="world-map-chart"
                :option="worldMapOption"
                autoresize
                @click="handleWorldCountryClick"
              />
            </template>
            <div class="world-map-placeholder" v-else>
              <el-empty
                :image-size="120"
                :description="worldMapLoadError ? '地图资源加载失败' : '地图加载中...'"
              />
              <el-button
                v-if="worldMapLoadError"
                type="primary"
                size="small"
                @click="reloadWorldMapAsset"
              >
                重试加载
              </el-button>
            </div>
          </div>
          <div class="world-map-sidebar">
            <div class="world-map-search">
              <el-input
                v-model="worldSearch"
                placeholder="输入国家或地区名称快速查询"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
            <div class="world-map-summary">
              <div class="summary-item">
                <span class="label">覆盖地区</span>
                <span class="value">{{ worldMapData.length }}</span>
              </div>
              <div class="summary-item">
                <span class="label">总访问量</span>
                <span class="value">{{ totalWorldVisits }}</span>
              </div>
              <div class="summary-item">
                <span class="label">总检索量</span>
                <span class="value">{{ totalWorldQueries }}</span>
              </div>
            </div>
            <el-scrollbar class="world-map-list">
              <div
                v-for="item in filteredWorldStats"
                :key="item.code"
                class="world-map-list-item"
                :class="{ active: worldSelectedCountry && worldSelectedCountry.code === item.code }"
                @click="selectWorldCountry(item)"
              >
                <div class="item-header">
                  <span class="country-name">{{ item.cnName }} · {{ item.name }}</span>
                  <span class="country-value">{{ getWorldMetricValue(item) }}</span>
                </div>
                <div class="item-footer">
                  <span>访问 {{ item.visits }}</span>
                  <span>检索 {{ item.queries }}</span>
                </div>
              </div>
              <div v-if="!filteredWorldStats.length" class="world-map-empty">
                暂无匹配结果
              </div>
            </el-scrollbar>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 系统功能使用分析 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>系统功能使用分析</span>
              <div class="header-subtitle">
                核心模块使用情况 · 数据驱动决策
              </div>
            </div>
          </template>
          
          <!-- 图表展示区域 -->
          <div class="function-charts-section">
            <el-row :gutter="20">
              <!-- 横向条形图 -->
              <el-col :span="16">
                <div class="chart-card">
                  <div class="chart-header">
                    <h4 class="chart-title">功能使用排名对比</h4>
                    <p class="chart-subtitle">实时更新 · {{ functionUsageRangeLabel }}</p>
                  </div>
                  <v-chart 
                    class="function-bar-chart" 
                    :option="functionUsageBarOption" 
                    autoresize
                  />
                </div>
              </el-col>
              <!-- 饼图 -->
              <el-col :span="8">
                <div class="chart-card">
                  <div class="chart-header">
                    <h4 class="chart-title">功能使用占比</h4>
                    <p class="chart-subtitle">实时更新 · {{ functionUsageRangeLabel }}</p>
                  </div>
                  <v-chart 
                    class="function-pie-chart" 
                    :option="functionUsagePieOption" 
                    autoresize
                  />
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 功能卡片网格 -->
          <div class="function-cards-section">
            <div class="section-header">
              <h3 class="section-title">功能使用详情</h3>
              <p class="section-description">
                根据核心模块的实际使用次数进行排序，快速识别高频与低频功能
              </p>
            </div>
            <div class="function-cards-grid" v-if="functionStatsData.length">
              <div 
                v-for="(item, index) in functionStatsData" 
                :key="index" 
                class="function-card"
              >
                <div class="card-content">
                  <h4 class="card-name">{{ item.name }}</h4>
                  <p class="card-desc">{{ item.description }}</p>
                  <div class="card-stats">
                    <div class="stat-item">
                      <span class="stat-label">使用次数</span>
                      <span class="stat-value">{{ item.value }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">占比</span>
                      <span class="stat-percent">{{ item.percent }}%</span>
                    </div>
                  </div>
                  <div class="card-trend">
                    <span class="trend-badge" :class="item.trend >= 0 ? 'trend-up' : 'trend-down'">
                      <el-icon><Top v-if="item.trend >= 0" /><Bottom v-else /></el-icon>
                      <span>{{ Math.abs(item.trend) }}%</span>
                    </span>
                  </div>
                </div>
                <div class="card-progress">
                  <div class="progress-bar">
                    <div
                      class="progress-fill"
                      :style="{ width: item.percent + '%', background: item.color }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
            <el-empty
              v-else
              description="暂无功能数据"
              :image-size="90"
              class="function-empty"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
// @ts-ignore: Vite 别名在编译时解析
import request from '@/utils/request'
import { use, registerMap } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart, MapChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  VisualMapComponent,
  GeoComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { 
  Document, CircleCheck, Timer, Money, Refresh, User, Location, MagicStick,
  TrendCharts, Shop, ShoppingCart, Ticket, Calendar, Operation, Warning, Files,
  Bell, DataAnalysis, ChatDotRound, Top, Download, ArrowRight, View, UserFilled,
  Star, Promotion, Bottom, Edit, Search, Connection, Picture, Coin, Clock,
  Setting, Grid, Odometer
} from '@element-plus/icons-vue'

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  MapChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  VisualMapComponent,
  GeoComponent
])

// 使用本地静态文件提供世界地图 GeoJSON（放在 travel-admin/public/world.json）
const WORLD_MAP_REMOTE_URL = '/world.json'

let worldMapRegisterPromise: Promise<void> | null = null
const worldMapReady = ref(false)
const worldMapLoadError = ref(false)

const ensureWorldMapRegistered = () => {
  if (!worldMapRegisterPromise) {
    worldMapRegisterPromise = fetch(WORLD_MAP_REMOTE_URL)
      .then(res => {
        if (!res.ok) {
          throw new Error('世界地图数据获取失败')
        }
        return res.json()
      })
      .then((geoJson: any) => {
        registerMap('world', geoJson)
        worldMapReady.value = true
        worldMapLoadError.value = false
      })
      .catch(error => {
        console.error('加载世界地图失败:', error)
        worldMapRegisterPromise = null
        worldMapReady.value = false
        worldMapLoadError.value = true
      })
  }
  return worldMapRegisterPromise
}

const reloadWorldMapAsset = async () => {
  worldMapReady.value = false
  worldMapLoadError.value = false
  worldMapRegisterPromise = null
  await ensureWorldMapRegistered()
  updateWorldMapOption()
}

// 自动刷新配置
const refreshInterval = ref(30000) // 30秒刷新一次
const autoRefreshTimer = ref<ReturnType<typeof setInterval> | null>(null)
const lastUpdateTime = ref<string>('')
const loading = ref(false)
const timeRange = ref('week') // 时间范围：today, week, month, all

// 根据时间范围计算天数
const getDaysByTimeRange = (range: string): number => {
  switch (range) {
    case 'today':
      return 1
    case 'week':
      return 7
    case 'month':
      return 30
    case 'all':
    default:
      return 90
  }
}

// 核心统计数据（更详细版本）
const stats = ref({
  // 用户相关
  totalUsers: 0,
  todayUsers: 0,
  monthlyUsers: 0,
  activeUsers: 0,
  onlineUsers: 0,
  userGrowthRate: 0,
  
  // 攻略相关
  totalPlans: 0,
  todayPlans: 0,
  monthlyPlans: 0,
  pendingPlans: 0,
  avgPlanViews: 0,
  planGrowthRate: 0,
  
  // 景点相关
  totalAttractions: 0,
  publishedAttractions: 0,
  unpublishedAttractions: 0,
  avgRating: 0,
  totalAttractionViews: 0,
  publishRate: 0,
  
  // 订单相关
  totalOrders: 0,
  todayOrders: 0,
  monthlyOrders: 0,
  pendingOrders: 0,
  totalAmount: 0,
  conversionRate: 0
})

// 用户活跃度数据
const activeStats = ref({
  onlineUsers: 0,
  todayVisits: 0,
  todayNew: 0,
  activeRate: 0,
  newRate: 0,
  lostRate: 0,
  lostUsers: 0
})

// 文旅业务数据
const businessStats = ref({
  totalOrders: 0,
  pendingOrders: 0,
  paidOrders: 0,
  orderAmount: 0,
  issuedCoupons: 0,
  usedCoupons: 0,
  couponRate: 0,
  cultureBookings: 0,
  projectApplications: 0,
  pendingReview: 0
})

// 互动数据（评论统计）
const contentStats = ref({
  totalComments: 0,
  todayComments: 0,
  pendingComments: 0,
  commentApprovalRate: 0,  // 审核率
  avgCommentsPerPlan: 0,    // 平均每篇攻略的评论数
  avgCommentsPerAttraction: 0, // 平均每个景点的评论数
  commentGrowthRate: 0,      // 评论增长率
  planComments: 0,
  attractionComments: 0,
  planCommentsToday: 0,
  attractionCommentsToday: 0
})

// 互动数据（收藏 & 浏览统计）
const interactionStats = ref({
  // 收藏数据
  totalFavorites: 0,         // 总收藏数
  todayFavorites: 0,         // 今日收藏
  planFavorites: 0,          // 攻略收藏
  attractionFavorites: 0,    // 景点收藏
  favoriteRate: 0,           // 收藏转化率
  favoriteGrowthRate: 0,     // 收藏增长率
  
  // 浏览数据
  totalViews: 0,             // 总浏览数
  todayViews: 0,             // 今日浏览
  planViews: 0,              // 攻略浏览
  attractionViews: 0,        // 景点浏览
  todayPlanViews: 0,         // 今日攻略浏览
  todayAttractionViews: 0,   // 今日景点浏览
  viewGrowthRate: 0          // 浏览增长率
})

// AI使用统计
const aiUsageStats = ref({
  totalRequests: 0,          // 总请求数
  todayRequests: 0,          // 今日请求
  successRate: 0,            // 成功率
  avgResponseTime: 0,        // 平均响应时间(ms)
  usageRate: 0,              // 使用率
  requestGrowthRate: 0       // 请求增长率
})

// 待处理事项
const pendingActions = ref({
  plans: 0,
  reports: 0,
  applications: 0
})

type QuickAction = {
  code: string
  label: string
  desc: string
  path: string
  icon: string
}

const defaultCommonActions: QuickAction[] = [
  {
    code: 'cultureOrders',
    label: '查看订单',
    desc: '文旅订单管理',
    path: '/home/admin/culture/orders',
    icon: 'ShoppingCart'
  },
  {
    code: 'adminUsers',
    label: '用户管理',
    desc: '用户信息管理',
    path: '/home/admin/users',
    icon: 'User'
  },
  {
    code: 'adminAttractions',
    label: '景点管理',
    desc: '景点内容管理',
    path: '/home/admin/attractions',
    icon: 'Location'
  }
]

const commonActions = ref<QuickAction[]>([...defaultCommonActions])

const iconComponents: Record<string, any> = {
  ShoppingCart,
  User,
  Location,
  Document,
  MagicStick,
  Files,
  Ticket,
  Calendar,
  Shop,
  Connection,
  Picture,
  TrendCharts,
  Operation,
  Star,
  Coin,
  Clock,
  Bell,
  Grid,
  Setting,
  Warning,
  Odometer,
  ChatDotRound
}

type WorldCountryStat = {
  code: string
  name: string
  cnName: string
  visits: number
  queries: number
  avgDuration: number
}

const defaultWorldMapData: WorldCountryStat[] = [
  { code: 'CN', name: 'China', cnName: '中国', visits: 15230, queries: 3240, avgDuration: 186 },
  { code: 'US', name: 'United States', cnName: '美国', visits: 12340, queries: 2980, avgDuration: 162 },
  { code: 'JP', name: 'Japan', cnName: '日本', visits: 8450, queries: 2100, avgDuration: 148 },
  { code: 'KR', name: 'South Korea', cnName: '韩国', visits: 6120, queries: 1502, avgDuration: 132 },
  { code: 'FR', name: 'France', cnName: '法国', visits: 4880, queries: 1105, avgDuration: 141 },
  { code: 'DE', name: 'Germany', cnName: '德国', visits: 5020, queries: 1188, avgDuration: 139 },
  { code: 'GB', name: 'United Kingdom', cnName: '英国', visits: 4650, queries: 990, avgDuration: 136 },
  { code: 'AU', name: 'Australia', cnName: '澳大利亚', visits: 3520, queries: 802, avgDuration: 129 },
  { code: 'TH', name: 'Thailand', cnName: '泰国', visits: 2980, queries: 650, avgDuration: 118 },
  { code: 'BR', name: 'Brazil', cnName: '巴西', visits: 2760, queries: 610, avgDuration: 120 },
  { code: 'AE', name: 'United Arab Emirates', cnName: '阿联酋', visits: 2050, queries: 480, avgDuration: 126 },
  { code: 'ZA', name: 'South Africa', cnName: '南非', visits: 1880, queries: 420, avgDuration: 117 }
]

const aggregateWorldStats = (data: WorldCountryStat[]): WorldCountryStat[] => {
  const countryMap = new Map<string, WorldCountryStat>()

  data.forEach(item => {
    if (!item) return
    const code = (item.code || item.name || '').toUpperCase()
    if (!code) return

    if (!countryMap.has(code)) {
      countryMap.set(code, {
        ...item,
        code,
        name: item.name || item.cnName || code,
        cnName: item.cnName || item.name || code
      })
      return
    }

    const target = countryMap.get(code)!
    target.visits = (target.visits || 0) + (item.visits || 0)
    target.queries = (target.queries || 0) + (item.queries || 0)
    target.avgDuration = item.avgDuration || target.avgDuration || 0
  })

  return Array.from(countryMap.values())
}

const worldMapData = ref<WorldCountryStat[]>(aggregateWorldStats(defaultWorldMapData))
const worldSelectedCountry = ref<WorldCountryStat | null>(worldMapData.value[0] || null)
const worldSearch = ref('')
const worldMapOption = ref<any>({
  tooltip: {
    trigger: 'item',
    formatter: (params: any) => {
      // 通过地图名称查找对应的数据
      const target = worldMapData.value.find(item => {
        const mapName = getMapCountryName(item)
        return mapName === params.name || item.name === params.name || item.cnName === params.name
      })
      if (!target) {
        return `${params.name}<br/>访问量：${params.value || 0}<br/>检索量：0`
      }
      return `${target.cnName} (${target.name})<br/>访问量：${target.visits || 0}<br/>检索量：${target.queries || 0}`
    }
  },
  visualMap: {
    min: 0,
    max: 15000,
    text: ['高', '低'],
    realtime: true,
    calculable: true,
    left: 'left',
    bottom: 'bottom',
    inRange: {
      // 从浅到深的蓝色渐变，体现热度
      color: ['#e8eef9', '#c5d9f0', '#9bc4e5', '#6ba8d9', '#3f72af', '#2a4d7a']
    },
    textStyle: {
      color: '#333'
    }
  },
  series: [
    {
      type: 'map',
      map: 'world',
      roam: true,
      zoom: 1.15,
      itemStyle: {
        borderColor: '#ffffff',
        borderWidth: 0.6,
        areaColor: '#e8eef9'
      },
      emphasis: {
        label: {
          show: false
        },
        itemStyle: {
          areaColor: '#fbbf24'
        }
      },
      data: []
    }
  ]
})

const getWorldMetricValue = (item: WorldCountryStat): number => {
  if (!item) return 0
  // 使用访问量 + 检索量作为热度指标，确保有检索量的国家也能显示颜色
  return (item.visits || 0) + (item.queries || 0)
}

const filteredWorldStats = computed(() => {
  const keyword = worldSearch.value.trim().toLowerCase()
  const list = worldMapData.value
    .filter(item => {
      if (!keyword) return true
      return (
        item.name.toLowerCase().includes(keyword) ||
        item.cnName.includes(keyword) ||
        item.code.toLowerCase().includes(keyword)
      )
    })
    .sort((a, b) => getWorldMetricValue(b) - getWorldMetricValue(a))
  return list
})

const totalWorldVisits = computed(() => {
  const total = worldMapData.value.reduce((sum, item) => sum + (item.visits || 0), 0)
  return total.toLocaleString()
})

const totalWorldQueries = computed(() => {
  const total = worldMapData.value.reduce((sum, item) => sum + (item.queries || 0), 0)
  return total.toLocaleString()
})

// 国家代码到地图名称的映射（ECharts 世界地图使用的名称）
// 注意：不同的地图数据可能使用不同的名称，这里提供多个可能的名称
const countryCodeToMapName: Record<string, string> = {
  'CN': 'China',
  'US': 'United States of America',
  'JP': 'Japan',
  'KR': 'Korea',
  'FR': 'France',
  'DE': 'Germany', // 也可能是 'Federal Republic of Germany' 或 'Deutschland'
  'GB': 'United Kingdom',
  'AU': 'Australia',
  'CA': 'Canada',
  'IT': 'Italy',
  'ES': 'Spain',
  'RU': 'Russia',
  'IN': 'India',
  'BR': 'Brazil',
  'MX': 'Mexico',
  'TH': 'Thailand',
  'SG': 'Singapore',
  'MY': 'Malaysia',
  'ID': 'Indonesia',
  'PH': 'Philippines',
  'VN': 'Vietnam',
  'AE': 'United Arab Emirates',
  'SA': 'Saudi Arabia',
  'TR': 'Turkey',
  'EG': 'Egypt',
  'ZA': 'South Africa',
  'AR': 'Argentina',
  'CL': 'Chile',
  'CO': 'Colombia',
  'PE': 'Peru',
  'NL': 'Netherlands',
  'BE': 'Belgium',
  'CH': 'Switzerland',
  'AT': 'Austria',
  'SE': 'Sweden',
  'NO': 'Norway',
  'DK': 'Denmark',
  'FI': 'Finland',
  'PL': 'Poland',
  'GR': 'Greece',
  'PT': 'Portugal',
  'IE': 'Ireland',
  'NZ': 'New Zealand',
  'PK': 'Pakistan',
  'BD': 'Bangladesh',
  'MM': 'Myanmar',
  'KH': 'Cambodia',
  'LA': 'Laos'
}

// 国家代码到多个可能地图名称的映射（用于匹配不同的地图数据格式）
const countryCodeToMapNames: Record<string, string[]> = {
  'DE': ['Germany', 'Federal Republic of Germany', 'Deutschland', 'DE'],
  'US': ['United States of America', 'United States', 'USA', 'US'],
  'GB': ['United Kingdom', 'UK', 'Great Britain', 'GB'],
  'KR': ['Korea', 'South Korea', 'Republic of Korea', 'KR'],
  'VN': ['Vietnam', 'Viet Nam', 'VN']
}

// 获取地图使用的国家名称
const getMapCountryName = (item: WorldCountryStat): string => {
  if (!item || !item.code) return item?.name || ''
  
  const code = item.code.toUpperCase()
  
  // 优先使用映射表中的名称
  if (countryCodeToMapName[code]) {
    return countryCodeToMapName[code]
  }
  
  // 如果没有映射，尝试使用英文名称
  if (item.name) {
    return item.name
  }
  
  // 最后使用代码
  return code
}

const updateWorldMapOption = () => {
  const values = worldMapData.value.map(item => getWorldMetricValue(item))
  const maxValue = values.length ? Math.max(...values) : 100
  const minValue = values.length ? Math.min(...values.filter(v => v > 0)) : 0
  
  // 构建地图数据，使用正确的国家名称
  // 确保所有有数据的国家（访问量或检索量 > 0）都包含在地图数据中
  const mapData: Array<{ name: string; value: number }> = []
  
  worldMapData.value
    .filter(item => getWorldMetricValue(item) > 0) // 只包含有数据的国家
    .forEach(item => {
      const mapName = getMapCountryName(item)
      let metricValue = getWorldMetricValue(item)
      
      // 如果值很小（<= 10），给它一个基础值，确保能显示明显的颜色
      // 这样即使只有1次检索，也能在地图上看到颜色
      // 将小值映射到至少 10，这样在分段映射中能显示明显的颜色
      if (metricValue > 0 && metricValue <= 10) {
        metricValue = Math.max(metricValue, 10)
      }
      
      // 调试日志（始终输出，方便排查问题）
      console.log(`[地图数据] ${item.cnName} (${item.code}) -> 地图名称: "${mapName}", 原始值: ${getWorldMetricValue(item)}, 映射值: ${metricValue}, 访问量: ${item.visits}, 检索量: ${item.queries}`)
      
      // 直接使用映射后的名称，不添加多个变体（避免数据重复）
      // 如果地图名称不匹配，ECharts 会忽略该数据点
      // 我们通过调试日志来查看实际需要使用的名称
      if (!mapData.find(d => d.name === mapName)) {
        mapData.push({
          name: mapName,
          value: metricValue
        })
      }
      
      // 对于德国，额外添加可能的名称变体（因为名称匹配可能有问题）
      if (item.code === 'DE') {
        const deNames = ['Federal Republic of Germany', 'Deutschland', 'DE', 'Germany']
        deNames.forEach(name => {
          if (name !== mapName && !mapData.find(d => d.name === name)) {
            mapData.push({
              name: name,
              value: metricValue
            })
          }
        })
      }
    })
  
  // 调试：输出所有地图数据
  console.log('[地图数据列表]', mapData)
  console.log('[世界地图数据]', worldMapData.value)
  
  // 重新计算映射后的最大值和最小值
  const mappedValues = mapData.map(d => d.value)
  const mappedMaxValue = mappedValues.length > 0 ? Math.max(...mappedValues) : maxValue
  const mappedMinValue = mappedValues.length > 0 ? Math.min(...mappedValues.filter(v => v > 0)) : minValue
  
  // 使用分段型 visualMap，确保小值也能显示明显颜色
  let visualMax = Math.max(mappedMaxValue, 100)
  
  // 如果有数据但最大值和最小值差距很大，或者有小值（<=10），使用分段映射
  const usePiecewise = (maxValue > 0 && minValue > 0 && maxValue / minValue > 20) || 
                       (mappedValues.some(v => v > 0 && v <= 10))
  
  if (usePiecewise) {
    // 使用分段型 visualMap，让小值显示更明显的颜色
    worldMapOption.value = {
      ...worldMapOption.value,
      visualMap: {
        type: 'piecewise', // 分段型映射
        min: 0,
        max: visualMax,
        text: ['高', '低'],
        realtime: true,
        calculable: true,
        left: 'left',
        bottom: 'bottom',
        pieces: [
          { min: 0, max: 0, color: '#e8eef9', label: '无数据' },
          { min: 1, max: 10, color: '#c5d9f0', label: '1-10' },
          { min: 11, max: 50, color: '#9bc4e5', label: '11-50' },
          { min: 51, max: 100, color: '#6ba8d9', label: '51-100' },
          { min: 101, max: 200, color: '#3f72af', label: '101-200' },
          { min: 201, color: '#2a4d7a', label: '200+' }
        ],
        textStyle: {
          color: '#333'
        },
        formatter: (value: number) => {
          return value.toLocaleString()
        }
      },
    }
  } else {
    // 使用连续型映射
    worldMapOption.value = {
      ...worldMapOption.value,
      visualMap: {
        type: 'continuous', // 连续型映射
        min: 0,
        max: visualMax,
        text: ['高', '低'],
        realtime: true,
        calculable: true,
        left: 'left',
        bottom: 'bottom',
        inRange: {
          // 从浅到深的蓝色渐变，体现热度
          // 浅色（低值）-> 深色（高值）
          // 使用更多颜色级别，确保小值也能显示明显颜色
          color: ['#e8eef9', '#d0e0f0', '#b8d1e7', '#9bc4e5', '#7db3d8', '#5fa0cb', '#3f72af', '#2a4d7a']
        },
        textStyle: {
          color: '#333'
        },
        formatter: (value: number) => {
          return value.toLocaleString()
        }
      },
    }
  }
  
  // 更新 series 配置
  worldMapOption.value = {
    ...worldMapOption.value,
    series: [
      {
        ...(worldMapOption.value.series?.[0] || {}),
        type: 'map',
        map: 'world',
        data: mapData,
        // 使用 nameMap 确保国家名称正确匹配
        nameMap: (() => {
          const nameMap: Record<string, string> = {}
          worldMapData.value.forEach(item => {
            if (item.code && countryCodeToMapName[item.code.toUpperCase()]) {
              // 如果地图使用不同的名称，可以通过 nameMap 映射
              // 这里先不设置，让 ECharts 自动匹配
            }
          })
          return nameMap
        })(),
        // 确保没有数据的国家显示为默认浅色
        itemStyle: {
          borderColor: '#ffffff',
          borderWidth: 0.6,
          areaColor: '#e8eef9' // 默认浅色，与 visualMap 最低值颜色一致
        },
        // 添加标签显示，方便调试
        label: {
          show: false, // 默认不显示，可以通过配置开启
          formatter: (params: any) => {
            return params.name
          }
        }
      }
    ]
  }
}

const selectWorldCountry = (country: WorldCountryStat) => {
  worldSelectedCountry.value = country
}

const handleWorldCountryClick = (params: any) => {
  if (!params || !params.name) return
  
  // 调试：输出点击的国家信息
  console.log('[地图点击事件]', {
    name: params.name,
    value: params.value,
    seriesName: params.seriesName
  })
  
  // 尝试多种方式匹配国家
  const target = worldMapData.value.find(item => {
    const mapName = getMapCountryName(item)
    return mapName === params.name || 
           item.name === params.name || 
           item.cnName === params.name ||
           item.code === params.name
  })
  
  if (target) {
    console.log('[找到匹配的国家]', target)
    worldSelectedCountry.value = target
  } else {
    console.warn('[未找到匹配的国家]', params.name, '可用国家:', worldMapData.value.map(i => `${i.cnName}(${i.code})`))
  }
}

const loadGlobalTraffic = async (options: { silent?: boolean } = {}) => {
  const { silent = true } = options
  let success = false
  try {
    await ensureWorldMapRegistered()
    const result = await request.get('/statistics/world-traffic')
    console.log('[API响应]', result)
    if (result.code === 200 && Array.isArray(result.data)) {
      const mapped = result.data.map((item: any) => {
        const mappedItem = {
          code: item.code || item.countryCode || item.isoCode || item.name,
          name: item.name || item.country || item.countryEn || item.code,
          cnName: item.cnName || item.countryCn || item.nameCn || item.name,
          visits: item.visits || item.traffic || item.value || 0,
          queries: item.queries || item.search || item.query || 0,
          avgDuration: item.avgDuration || item.duration || 0
        }
        console.log('[映射数据]', mappedItem)
        return mappedItem
      })
      worldMapData.value = aggregateWorldStats(mapped)
      console.log('[聚合后的数据]', worldMapData.value)
      success = true
    } else if (!silent) {
      ElMessage.warning('未获取到全球访问数据，使用示例数据')
    }
  } catch (error) {
    console.error('[加载全球访问数据失败]', error)
    if (!silent) {
      ElMessage.warning('全球访问数据加载失败，已使用示例数据')
    }
  } finally {
    if (!worldMapData.value.length) {
      worldMapData.value = [...defaultWorldMapData]
    }
    if (!worldSelectedCountry.value && worldMapData.value.length) {
      worldSelectedCountry.value = worldMapData.value[0]
    }
    updateWorldMapOption()
    return success
  }
}

watch(
  () => worldMapData.value,
  () => {
    updateWorldMapOption()
  },
  { deep: true }
)

watch(filteredWorldStats, list => {
  if (!list.length) {
    worldSelectedCountry.value = null
    return
  }
  if (!worldSelectedCountry.value) {
    worldSelectedCountry.value = list[0]
    return
  }
  const exists = list.some(item => item.code === worldSelectedCountry.value?.code)
  if (!exists) {
    worldSelectedCountry.value = list[0]
  }
})

updateWorldMapOption()

const functionUsageRangeTextMap: Record<string, string> = {
  today: '今日实时',
  week: '近7天',
  month: '近30天',
  quarter: '近90天'
}

const getFunctionUsageRangeParam = () => {
  if (timeRange.value === 'all') {
    return 'quarter'
  }
  return timeRange.value || 'week'
}

const getFunctionUsageFallbackLabel = () => {
  const key = getFunctionUsageRangeParam()
  return functionUsageRangeTextMap[key] || '近7天'
}

// 实时通知
const realtimeNotifications = ref<any[]>([])

// 快捷操作路由跳转
const router = useRouter()


// 用户增长趋势图表配置
const userTrendOption = ref({
  title: {
    text: '用户增长趋势',
    show: true
  },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#667eea',
    borderWidth: 2,
    textStyle: {
      color: '#303133',
      fontSize: 14
    },
    axisPointer: {
      type: 'cross',
      crossStyle: {
        color: '#667eea'
      }
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    axisLine: {
      lineStyle: {
        color: '#e4e7ed'
      }
    },
    axisLabel: {
      color: '#606266',
      fontSize: 12,
      fontWeight: 500
    },
    axisTick: {
      alignWithLabel: true
    }
  },
  yAxis: {
    type: 'value',
    axisLine: {
      show: false
    },
    axisLabel: {
      color: '#909399',
      fontSize: 12
    },
    splitLine: {
      lineStyle: {
        color: '#f0f0f0',
        type: 'dashed'
      }
    }
  },
  series: [{
    name: '用户增长',
    data: [0, 0, 0, 0, 0, 0, 0],
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 8,
    lineStyle: {
      width: 3,
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 1,
        y2: 0,
        colorStops: [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ]
      }
    },
    itemStyle: {
      color: '#667eea',
      borderColor: '#fff',
      borderWidth: 2,
      shadowBlur: 10,
      shadowColor: 'rgba(102, 126, 234, 0.5)'
    },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
        ]
      }
    },
    emphasis: {
      itemStyle: {
        color: '#764ba2',
        borderColor: '#fff',
        borderWidth: 3,
        shadowBlur: 15,
        shadowColor: 'rgba(118, 75, 162, 0.6)'
      },
      lineStyle: {
        width: 4
      }
    },
    animationDuration: 1000,
    animationEasing: 'cubicOut'
  }]
})

// 功能使用统计详细数据
const functionStatsData = ref([
  {
    name: '内容创作',
    description: '攻略发布、编辑',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'Edit',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    name: 'AI智能服务',
    description: 'AI生成、智能推荐',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'MagicStick',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  },
  {
    name: '景点浏览',
    description: '景点查看、搜索',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'Search',
    color: 'linear-gradient(135deg, #faa307 0%, #ffba08 100%)'
  },
  {
    name: '社区互动',
    description: '评论、点赞、收藏',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'ChatDotRound',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    name: '文旅对接',
    description: '项目查看、预订',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'Connection',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    name: '订单交易',
    description: '订单创建、支付',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'ShoppingCart',
    color: 'linear-gradient(135deg, #a78bfa 0%, #c084fc 100%)'
  },
  {
    name: '用户画像',
    description: '行为分析、推荐',
    value: 0,
    percent: 0,
    trend: 0,
    icon: 'Picture',
    color: 'linear-gradient(135deg, #fb7185 0%, #f472b6 100%)'
  }
])

const functionUsageRangeLabel = ref('近7天')

// 功能使用统计图表配置（横向条形图）
const functionUsageOption = ref({
  grid: {
    left: '12%',
    right: '8%',
    top: '12%',
    bottom: '3%',
    containLabel: true
  },
  tooltip: {},
  xAxis: {},
  yAxis: {},
  series: [{
    name: '功能使用',
    type: 'bar',
    barWidth: '50%',
    barGap: '30%',
    data: [
      { 
        value: 0, 
        name: '用户画像',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(251, 113, 133, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#fb7185' },
              { offset: 0.5, color: '#f472b6' },
              { offset: 1, color: '#ec4899' }
            ]
          }
        }
      },
      { 
        value: 0, 
        name: '订单交易',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(167, 139, 250, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#a78bfa' },
              { offset: 0.5, color: '#c084fc' },
              { offset: 1, color: '#d946ef' }
            ]
          }
        }
      },
      { 
        value: 0, 
        name: '文旅对接',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(79, 172, 254, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#4facfe' },
              { offset: 0.5, color: '#00f2fe' },
              { offset: 1, color: '#06b6d4' }
            ]
          }
        }
      },
      { 
        value: 0, 
        name: '社区互动',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(240, 147, 251, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#f093fb' },
              { offset: 0.5, color: '#f5576c' },
              { offset: 1, color: '#ef4444' }
            ]
          }
        }
      },
      { 
        value: 0, 
        name: '景点浏览',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(250, 163, 7, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#faa307' },
              { offset: 0.5, color: '#ffba08' },
              { offset: 1, color: '#f59e0b' }
            ]
          }
        }
      },
      { 
        value: 0, 
        name: 'AI智能服务',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(67, 233, 123, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#43e97b' },
              { offset: 0.5, color: '#38f9d7' },
              { offset: 1, color: '#10b981' }
            ]
          }
        }
      },
      { 
        value: 0, 
        name: '内容创作',
        itemStyle: {
          borderRadius: [0, 12, 12, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(102, 126, 234, 0.4)',
          shadowOffsetX: 0,
          shadowOffsetY: 2,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#667eea' },
              { offset: 0.5, color: '#764ba2' },
              { offset: 1, color: '#8b5cf6' }
            ]
          }
        }
      }
    ],
    label: {
      show: true,
      position: 'right',
      distance: 8,
      color: '#303133',
      fontSize: 14,
      fontWeight: 700,
      fontFamily: 'Arial, sans-serif',
      formatter: '{c}',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderRadius: 6,
      padding: [4, 10],
      shadowBlur: 4,
      shadowColor: 'rgba(0, 0, 0, 0.1)',
      shadowOffsetY: 2
    },
    emphasis: {
      focus: 'series',
      itemStyle: {
        shadowBlur: 20,
        shadowColor: 'rgba(0, 0, 0, 0.4)',
        shadowOffsetY: 4,
        borderColor: 'rgba(255, 255, 255, 0.5)',
        borderWidth: 2
      },
      label: {
        fontSize: 16,
        fontWeight: 900,
        shadowBlur: 8
      }
    },
    animationDuration: 1200,
    animationEasing: 'cubicOut',
    animationDelay: function (idx: number) {
      return idx * 120
    }
  }]
})

const functionUsageRanking = computed(() => {
  const items = functionStatsData.value
  if (!items.length) return []

  // 按值降序排序
  const sorted = items
    .map(item => ({ ...item }))
    .sort((a, b) => (b.value || 0) - (a.value || 0))

  // 计算最大值（用于进度条显示）
  const maxValue = Math.max(...sorted.map(item => item.value || 0), 1)

  // 返回排序后的数据，使用已计算的百分比
  return sorted.map((item, index) => {
    const value = item.value || 0
    const progress = maxValue > 0 ? (value / maxValue) * 100 : 0
    return {
      ...item,
      rank: index + 1,
      // 使用已计算的百分比，确保数据一致性
      percent: typeof item.percent === 'number' ? item.percent : 0,
      // 进度条至少显示8%的最小宽度（如果有值）
      progress: value > 0 ? Math.max(progress, 8) : 0,
      // 确保趋势值是有效的数字
      trend: typeof item.trend === 'number' && isFinite(item.trend) ? item.trend : 0
    }
  })
})

// 功能使用横向条形图配置
const functionUsageBarOption = computed(() => {
  const ranking = functionUsageRanking.value
  if (!ranking.length) {
    return {
      title: {
        text: '暂无数据',
        left: 'center',
        top: 'middle',
        textStyle: { color: '#909399', fontSize: 16 }
      }
    }
  }

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const param = params[0]
        return `${param.name}<br/>使用次数: ${param.value}次<br/>占比: ${param.data.percent}%`
      }
    },
    grid: {
      left: '15%',
      right: '8%',
      top: '10%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        color: '#606266',
        formatter: '{value}次'
      },
      axisLine: { lineStyle: { color: '#e4e7ed' } },
      splitLine: { lineStyle: { color: '#f0f2f5', type: 'dashed' } }
    },
    yAxis: {
      type: 'category',
      data: ranking.map(item => item.name),
      axisLabel: {
        color: '#606266',
        fontSize: 12
      },
      axisLine: { lineStyle: { color: '#e4e7ed' } }
    },
    series: [{
      name: '使用次数',
      type: 'bar',
      data: ranking.map(item => ({
        value: item.value,
        percent: item.percent,
        itemStyle: {
          color: (() => {
            if (item.color && item.color.includes('gradient')) {
              const colors = item.color.match(/#[0-9a-fA-F]{6}/g) || []
              if (colors.length >= 2) {
                return {
                  type: 'linear',
                  x: 0, y: 0, x2: 1, y2: 0,
                  colorStops: [
                    { offset: 0, color: colors[0] },
                    { offset: 1, color: colors[1] }
                  ]
                }
              }
            }
            // 默认渐变
            return {
              type: 'linear',
              x: 0, y: 0, x2: 1, y2: 0,
              colorStops: [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ]
            }
          })(),
          borderRadius: [0, 8, 8, 0],
          shadowBlur: 8,
          shadowColor: 'rgba(102, 126, 234, 0.3)',
          shadowOffsetY: 2
        }
      })),
      barWidth: '60%',
      label: {
        show: true,
        position: 'right',
        formatter: '{c}次',
        color: '#303133',
        fontSize: 12,
        fontWeight: 600
      },
      animationDuration: 1000,
      animationEasing: 'cubicOut'
    }]
  }
})

// 功能使用饼图配置
const functionUsagePieOption = computed(() => {
  const ranking = functionUsageRanking.value
  if (!ranking.length) {
    return {
      title: {
        text: '暂无数据',
        left: 'center',
        top: 'middle',
        textStyle: { color: '#909399', fontSize: 16 }
      }
    }
  }

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}次 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        color: '#606266',
        fontSize: 12
      },
      formatter: (name: string) => {
        const item = ranking.find(r => r.name === name)
        return item ? `${name} (${item.value}次)` : name
      }
    },
    series: [{
      name: '功能使用',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{d}%',
        fontSize: 12,
        fontWeight: 600,
        color: '#303133'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 700
        },
        itemStyle: {
          shadowBlur: 20,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      },
      data: ranking.map(item => {
        let color = '#667eea'
        if (item.color && item.color.includes('gradient')) {
          const colors = item.color.match(/#[0-9a-fA-F]{6}/g) || []
          color = colors[0] || '#667eea'
        } else if (item.color && item.color.startsWith('#')) {
          color = item.color
        }
        return {
          value: item.value,
          name: item.name,
          itemStyle: {
            color: color
          }
        }
      }),
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: (idx: number) => idx * 100
    }]
  }
})

// 格式化当前时间
const formatCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

const loadDashboardData = async () => {
  try {
    loading.value = true
    console.log('开始加载监测面板数据...')
    
    // 1. 加载核心统计（更详细版本）
    const overviewResult = await request.get('/statistics/overview')
    console.log('概览统计结果:', overviewResult)
    if (overviewResult.code === 200 && overviewResult.data) {
      const data = overviewResult.data
      stats.value = {
        // 用户相关
        totalUsers: data.totalUsers || 0,
        todayUsers: data.todayUsers || 0,
        monthlyUsers: data.monthlyUsers || 0,
        activeUsers: data.activeUsers || 0,
        onlineUsers: data.onlineUsers || 0,
        userGrowthRate: data.userGrowthRate || 0,
        
        // 攻略相关
        totalPlans: data.totalPlans || 0,
        todayPlans: data.todayPlans || 0,
        monthlyPlans: data.monthlyPlans || 0,
        pendingPlans: data.pendingPlans || 0,
        avgPlanViews: data.avgPlanViews || 0,
        planGrowthRate: data.planGrowthRate || 0,
        
        // 景点相关
        totalAttractions: data.totalAttractions || 0,
        publishedAttractions: data.publishedAttractions || 0,
        unpublishedAttractions: data.unpublishedAttractions || 0,
        avgRating: data.avgRating || 0,
        totalAttractionViews: data.totalAttractionViews || 0,
        publishRate: data.publishRate || 0,
        
        // 订单相关
        totalOrders: data.totalOrders || 0,
        todayOrders: data.todayOrders || 0,
        monthlyOrders: data.monthlyOrders || 0,
        pendingOrders: data.pendingOrders || 0,
        totalAmount: data.totalAmount || 0,
        conversionRate: data.conversionRate || 0
      }
      
      // 用户活跃度
      const totalUsers = data.totalUsers || 0
      const activeUsers = data.activeUsers || 0
      const todayNewValue = data.todayNew || data.todayUsers || 0
      const calcRate = (value: number, total: number) => {
        if (!total || total <= 0) return 0
        return Math.round((value / total) * 10000) / 100
      }
      const calcLostRate = (value: number, total: number) => {
        const base = total + value
        if (!base || base <= 0) return 0
        return Math.round((value / base) * 10000) / 100
      }
      const activeRate = typeof data.activeRate === 'number' ? data.activeRate : calcRate(activeUsers, totalUsers)
      const newRate = typeof data.newRate === 'number' ? data.newRate : calcRate(todayNewValue, totalUsers)
      const lostRate = typeof data.lostRate === 'number'
        ? data.lostRate
        : calcLostRate(data.lostUsers || 0, totalUsers)

      activeStats.value.onlineUsers = data.onlineUsers || 0
      activeStats.value.todayVisits = data.todayVisits || 0
      activeStats.value.todayNew = todayNewValue
      activeStats.value.activeRate = activeRate
      activeStats.value.newRate = newRate
      activeStats.value.lostRate = lostRate
      activeStats.value.lostUsers = data.lostUsers || 0
    }
    
    // 2. 加载文旅业务统计
    try {
      const businessResult = await request.get('/statistics/business')
      if (businessResult.code === 200 && businessResult.data) {
        businessStats.value = {
          totalOrders: businessResult.data.totalOrders || 0,
          pendingOrders: businessResult.data.pendingOrders || 0,
          paidOrders: businessResult.data.paidOrders || 0,
          orderAmount: businessResult.data.orderAmount || 0,
          issuedCoupons: businessResult.data.issuedCoupons || 0,
          usedCoupons: businessResult.data.usedCoupons || 0,
          couponRate: businessResult.data.couponRate || 0,
          cultureBookings: businessResult.data.cultureBookings || 0,
          projectApplications: businessResult.data.projectApplications || 0,
          pendingReview: businessResult.data.pendingReview || 0
        }
      }
    } catch (error) {
      console.warn('文旅业务统计加载失败，使用默认值')
    }
    
    // 3. 加载互动数据（评论统计）
    try {
      const contentResult = await request.get('/statistics/content')
      if (contentResult.code === 200 && contentResult.data) {
        contentStats.value = {
          totalComments: contentResult.data.totalComments || 0,
          todayComments: contentResult.data.todayComments || 0,
          pendingComments: contentResult.data.pendingComments || 0,
          commentApprovalRate: contentResult.data.commentApprovalRate || 0,
          avgCommentsPerPlan: contentResult.data.avgCommentsPerPlan || 0,
          avgCommentsPerAttraction: contentResult.data.avgCommentsPerAttraction || 0,
          commentGrowthRate: contentResult.data.commentGrowthRate || 0,
          planComments: contentResult.data.planComments || 0,
          attractionComments: contentResult.data.attractionComments || 0,
          planCommentsToday: contentResult.data.planCommentsToday || 0,
          attractionCommentsToday: contentResult.data.attractionCommentsToday || 0
        }
      }
    } catch (error) {
      console.warn('互动数据加载失败，使用默认值')
    }
    
    // 3.1 加载收藏和点赞统计
    try {
      const interactionResult = await request.get('/statistics/interactions')
      if (interactionResult.code === 200 && interactionResult.data) {
        interactionStats.value = {
          // 收藏数据
          totalFavorites: interactionResult.data.totalFavorites || 0,
          todayFavorites: interactionResult.data.todayFavorites || 0,
          planFavorites: interactionResult.data.planFavorites || 0,
          attractionFavorites: interactionResult.data.attractionFavorites || 0,
          favoriteRate: interactionResult.data.favoriteRate || 0,
          favoriteGrowthRate: interactionResult.data.favoriteGrowthRate || 0,
          // 浏览数据
          totalViews: interactionResult.data.totalViews || 0,
          todayViews: interactionResult.data.todayViews || 0,
          planViews: interactionResult.data.planViews || 0,
          attractionViews: interactionResult.data.attractionViews || 0,
          todayPlanViews: interactionResult.data.todayPlanViews || 0,
          todayAttractionViews: interactionResult.data.todayAttractionViews || 0,
          viewGrowthRate: interactionResult.data.viewGrowthRate || 0
        }
      }
    } catch (error) {
      console.warn('收藏点赞统计加载失败，使用默认值')
    }
    
    // 3.2 加载AI使用统计
    try {
      const aiResult = await request.get('/statistics/ai-usage')
      if (aiResult.code === 200 && aiResult.data) {
        aiUsageStats.value = {
          totalRequests: aiResult.data.totalRequests || 0,
          todayRequests: aiResult.data.todayRequests || 0,
          successRate: aiResult.data.successRate || 0,
          avgResponseTime: aiResult.data.avgResponseTime || 0,
          usageRate: aiResult.data.usageRate || 0,
          requestGrowthRate: aiResult.data.requestGrowthRate || 0
        }
      }
    } catch (error) {
      console.warn('AI使用统计加载失败，使用默认值')
    }
    
    // 4. 加载待处理事项
    try {
      const pendingResult = await request.get('/statistics/pending')
      if (pendingResult.code === 200 && pendingResult.data) {
        pendingActions.value = {
          plans: pendingResult.data.plans || 0,
          reports: pendingResult.data.reports || 0,
          applications: pendingResult.data.applications || 0
        }
      }
    } catch (error) {
      console.warn('待处理事项加载失败，使用默认值')
    }
    
    // 5. 加载实时通知（最近3条）
    try {
      const notificationsResult = await request.get('/statistics/notifications?limit=3')
      if (notificationsResult.code === 200 && notificationsResult.data) {
        realtimeNotifications.value = notificationsResult.data
      }
    } catch (error) {
      console.warn('实时通知加载失败')
      realtimeNotifications.value = []
    }
    
    await loadFunctionUsage(true)
    await loadGlobalTraffic({ silent: true })
    
    // 5. 加载用户增长趋势（根据时间范围）
    const days = getDaysByTimeRange(timeRange.value)
    const trendResult = await request.get(`/statistics/analytics/users?days=${days}`)
    console.log('用户增长趋势结果:', trendResult)
    if (trendResult.code === 200 && trendResult.data && trendResult.data.values) {
      // 更新用户增长趋势图数据
      userTrendOption.value.series[0].data = trendResult.data.values
      // 如果有日期数据，更新X轴
      if (trendResult.data.dates && trendResult.data.dates.length > 0) {
        userTrendOption.value.xAxis.data = trendResult.data.dates
      }
    }
    
    // 更新最后刷新时间
    lastUpdateTime.value = formatCurrentTime()
    console.log('监测面板数据加载完成，最后更新时间:', lastUpdateTime.value)
  } catch (error: any) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载监测面板数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 快捷操作跳转
const handleQuickAction = (target: string | QuickAction) => {
  if (!target) return
  if (typeof target === 'string') {
    router.push(target)
    return
  }
  recordCommonAction(target)
  router.push(target.path)
}

// 核心指标卡片点击
const handleCardClick = (type: string) => {
  const routes: Record<string, string> = {
    users: '/home/admin/users',
    plans: '/home/admin/plans',
    attractions: '/home/admin/attractions',
    orders: '/home/admin/culture/orders',
    comments: '/home/admin/plans',        // 跳转到攻略管理，可以在那里管理评论
    favorites: '/home/admin/plans',       // 跳转到攻略管理，查看收藏数据
    views: '/home/admin/plans',           // 跳转到攻略管理，查看浏览数据
    ai: '/home/admin/plans'               // 跳转到攻略管理，AI功能相关
  }
  if (routes[type]) {
    router.push(routes[type])
  }
}

// 格式化数字（千分位）
const formatNumber = (num: number): string => {
  if (num === undefined || num === null) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const normalizeAction = (action: any): QuickAction | null => {
  if (!action) {
    return null
  }
  const path = action.actionPath || action.path
  if (!path) {
    return null
  }
  const code = action.actionCode || action.code || path
  return {
    code,
    label: action.actionName || action.label || '常用操作',
    desc: action.actionDesc || action.desc || '',
    path,
    icon: action.actionIcon || action.icon || 'Operation'
  }
}

const loadCommonActions = async () => {
  try {
    const result = await request.get('/admin/usage/common-actions', {
      params: { limit: 3 }
    })
    if (result.code === 200 && Array.isArray(result.data)) {
      const parsed = result.data
        .map((item: any) => normalizeAction(item))
        .filter((item: QuickAction | null): item is QuickAction => !!item)
      if (parsed.length > 0) {
        commonActions.value = parsed
        return
      }
    }
  } catch (error) {
    console.error('加载常用操作失败:', error)
  }
  commonActions.value = [...defaultCommonActions]
}

const recordCommonAction = async (action: QuickAction) => {
  try {
    await request.post('/admin/usage/common-actions', {
      actionCode: action.code,
      actionName: action.label,
      actionPath: action.path,
      actionIcon: action.icon,
      actionDesc: action.desc
    })
    loadCommonActions()
  } catch (error) {
    console.error('记录常用操作失败:', error)
  }
}

// 导出数据
const exportData = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要导出当前监测数据报表吗？',
      '导出确认',
      {
        confirmButtonText: '确定导出',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 使用当前时间范围作为导出参数（预留扩展）
    const params = new URLSearchParams({
      type: 'overview',
      format: 'csv',
      timeRange: timeRange.value
    })

    // 通过新窗口触发浏览器下载
    window.open(`/api/statistics/export?${params.toString()}`, '_blank')
    ElMessage.success('数据报表导出任务已启动，请稍候...')
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('导出报表失败:', error)
      ElMessage.error('导出报表失败: ' + (error.message || '未知错误'))
    }
  }
}

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }
  autoRefreshTimer.value = setInterval(() => {
    console.log('自动刷新数据...')
    loadDashboardData()
  }, refreshInterval.value)
  console.log(`自动刷新已启动，间隔: ${refreshInterval.value / 1000}秒`)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
    autoRefreshTimer.value = null
    console.log('自动刷新已停止')
  }
}

onMounted(() => {
  loadDashboardData()
  startAutoRefresh()
  loadCommonActions()
})

onUnmounted(() => {
  stopAutoRefresh()
})

// 监听时间范围变化，重新加载监测面板数据（包含用户增长趋势）
watch(
  () => timeRange.value,
  () => {
    loadDashboardData()
  }
)

// 刷新用户增长趋势
const refreshUserTrend = async () => {
  try {
    ElMessage.info('正在刷新用户增长趋势...')
    const days = getDaysByTimeRange(timeRange.value)
    const trendResult = await request.get(`/statistics/analytics/users?days=${days}`)
    if (trendResult.code === 200 && trendResult.data && trendResult.data.values) {
      userTrendOption.value.series[0].data = trendResult.data.values
      if (trendResult.data.dates && trendResult.data.dates.length > 0) {
        userTrendOption.value.xAxis.data = trendResult.data.dates
      }
      ElMessage.success('用户增长趋势已更新')
    }
  } catch (error: any) {
    ElMessage.error('刷新失败: ' + error.message)
  }
}

// 功能使用统计加载
const loadFunctionUsage = async (suppressError = true) => {
  try {
    const functionResult = await request.get('/statistics/function-usage', {
      params: { range: getFunctionUsageRangeParam() }
    })
    
    // 数据验证
    if (!functionResult || functionResult.code !== 200) {
      throw new Error(`API返回错误: ${functionResult?.message || '未知错误'}`)
    }
    
    if (!functionResult.data || typeof functionResult.data !== 'object') {
      throw new Error('API返回数据格式错误')
    }
    
    const data = functionResult.data
    functionUsageRangeLabel.value = getFunctionUsageFallbackLabel()

    // 数据验证和清理
    const content = Math.max(0, Number(data.content) || 0)
    const aiService = Math.max(0, Number(data.aiService) || 0)
    const attraction = Math.max(0, Number(data.attraction) || 0)
    const interaction = Math.max(0, Number(data.interaction) || 0)
    const culture = Math.max(0, Number(data.culture) || 0)
    const order = Math.max(0, Number(data.order) || 0)
    const portrait = Math.max(0, Number(data.portrait) || 0)
    
    // 计算总量
    const total = content + aiService + attraction + interaction + culture + order + portrait
    
    // 计算百分比（使用更精确的算法，确保总和为100%）
    const calculatePercentages = (values: number[], total: number): number[] => {
      if (total === 0) {
        return values.map(() => 0)
      }
      // 先计算精确百分比
      const precise = values.map(v => (v / total) * 100)
      // 四舍五入到整数
      const rounded = precise.map(p => Math.round(p))
      // 计算总和
      const sum = rounded.reduce((s, r) => s + r, 0)
      // 如果总和不为100，调整最大的值
      if (sum !== 100 && rounded.length > 0) {
        const diff = 100 - sum
        const maxIndex = rounded.reduce((maxIdx, val, idx) => 
          val > rounded[maxIdx] ? idx : maxIdx, 0
        )
        rounded[maxIndex] += diff
      }
      return rounded
    }
    
    const values = [content, aiService, attraction, interaction, culture, order, portrait]
    const percentages = calculatePercentages(values, total)
    
    // 处理趋势值（确保是数字，处理NaN和Infinity）
    const safeTrend = (trend: any): number => {
      const num = Number(trend)
      if (isNaN(num) || !isFinite(num)) return 0
      // 限制趋势值在合理范围内（-1000% 到 1000%）
      return Math.max(-1000, Math.min(1000, num))
    }
    
    // 更新详细统计数据
    functionStatsData.value = [
      {
        name: '内容创作',
        description: '攻略发布、编辑',
        value: content,
        percent: percentages[0],
        trend: safeTrend(data.contentTrend),
        icon: 'Edit',
        color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      },
      {
        name: 'AI智能服务',
        description: 'AI生成、智能推荐',
        value: aiService,
        percent: percentages[1],
        trend: safeTrend(data.aiServiceTrend),
        icon: 'MagicStick',
        color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
      },
      {
        name: '景点浏览',
        description: '景点查看、搜索',
        value: attraction,
        percent: percentages[2],
        trend: safeTrend(data.attractionTrend),
        icon: 'Search',
        color: 'linear-gradient(135deg, #faa307 0%, #ffba08 100%)'
      },
      {
        name: '社区互动',
        description: '评论、点赞、收藏',
        value: interaction,
        percent: percentages[3],
        trend: safeTrend(data.interactionTrend),
        icon: 'ChatDotRound',
        color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
      },
      {
        name: '文旅对接',
        description: '项目查看、预订',
        value: culture,
        percent: percentages[4],
        trend: safeTrend(data.cultureTrend),
        icon: 'Connection',
        color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
      },
      {
        name: '订单交易',
        description: '订单创建、支付',
        value: order,
        percent: percentages[5],
        trend: safeTrend(data.orderTrend),
        icon: 'ShoppingCart',
        color: 'linear-gradient(135deg, #a78bfa 0%, #c084fc 100%)'
      },
      {
        name: '用户画像',
        description: '行为分析、推荐',
        value: portrait,
        percent: percentages[6],
        trend: safeTrend(data.portraitTrend),
        icon: 'Picture',
        color: 'linear-gradient(135deg, #fb7185 0%, #f472b6 100%)'
      }
    ]
    
    // 调试日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log('[功能使用统计]', {
        total,
        percentages: percentages.reduce((sum, p) => sum + p, 0),
        data: functionStatsData.value.map(item => ({
          name: item.name,
          value: item.value,
          percent: item.percent,
          trend: item.trend
        }))
      })
    }
  } catch (error) {
    console.error('功能使用统计加载失败:', error)
    // 发生错误时，重置数据避免显示错误数据
    functionStatsData.value = []
    if (!suppressError) {
      ElMessage.error('加载功能使用统计失败，请稍后重试')
      throw error
    }
  }
}

</script>

<style lang="scss" scoped>
.admin-dashboard {
  // 顶部控制栏样式
  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    margin-bottom: 24px;
    background: #ffffff;
    border-radius: 16px;
    border: 1px solid #e4e7ed;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    position: relative;
    
    .header-left {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
      flex: 0 0 auto;
      
      .dashboard-title {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        margin: 0;
        
        .el-icon {
          font-size: 28px;
          color: #606266;
        }
      }
      
      .status-info {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
        margin-left: 40px;
        
        .status-icon {
          font-size: 14px;
          color: #909399;
          animation: rotate 2s linear infinite;
        }
        
        .status-text {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          color: #909399;
          font-weight: 400;
        }
        
        .update-time {
          font-size: 14px;
          color: #909399;
          font-weight: 400;
          margin-left: 0;
        }
      }
    }
    
    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 0 0 auto;
      margin-left: auto;

      .el-select {
        // 覆盖 CSS 变量，移除蓝色
        --el-input-focus-border-color: #dcdfe6 !important;
        --el-border-color: #dcdfe6 !important;
        --el-color-primary: #606266 !important;
        
        :deep(.el-input__wrapper) {
          border: none !important;
          
          &:hover {
            box-shadow: 0 0 0 1px #dcdfe6 inset !important;
            border-color: #dcdfe6 !important;
          }
          
          &.is-focus,
          &:focus,
          &:focus-visible {
            --el-input-focus-border-color: #dcdfe6 !important;
            --el-border-color: #dcdfe6 !important;
            box-shadow: 0 0 0 1px #dcdfe6 inset !important;
            border-color: #dcdfe6 !important;
            outline: none !important;
          }
        }
        
        :deep(.el-input__inner) {
          &:focus {
            color: #606266 !important;
          }
        }
        
        // 当 el-select 获得焦点时
        &.is-focus,
        &:focus,
        &:focus-within {
          :deep(.el-input__wrapper) {
            --el-input-focus-border-color: #dcdfe6 !important;
            --el-border-color: #dcdfe6 !important;
            box-shadow: 0 0 0 1px #dcdfe6 inset !important;
            border-color: #dcdfe6 !important;
            outline: none !important;
          }
        }
      }

      .el-button {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        border-radius: 6px;
        border: 1px solid #dcdfe6;
        background: #fff;
        color: #606266;
        font-weight: 400;
        font-size: 14px;
        box-shadow: none;
        transition: all 0.2s ease;

        .el-icon {
          font-size: 16px;
        }

        &:hover {
          border-color: #909399;
          color: #606266;
          background: #fff;
        }

        &:active,
        &:focus:active {
          border-color: #303133 !important;
          box-shadow: 0 0 0 2px #303133 !important;
          outline: none !important;
        }

        &.el-button--primary {
          border-color: #dcdfe6;
          color: #606266;
          background: #fff;

          &:hover {
            background: #fff;
            border-color: #909399;
            color: #606266;
          }

          &:active,
          &:focus:active {
            border-color: #303133 !important;
            box-shadow: 0 0 0 2px #303133 !important;
            outline: none !important;
          }
        }

        &.el-button--success {
          border-color: #67c23a;
          color: #67c23a;
          background: #fff;

          &:hover {
            background: #f0f9ff;
            border-color: #67c23a;
          }

          &:active,
          &:focus:active {
            border-color: #303133 !important;
            box-shadow: 0 0 0 2px #303133 !important;
            outline: none !important;
          }
        }
      }

      .action-btn {
        border-color: #dcdfe6 !important;
        color: #606266 !important;
        background: #fff !important;
        font-weight: 500;
        box-shadow: none;

        .el-icon {
          color: #606266 !important;
        }

        &:hover {
          border-color: #c0c4cc !important;
          color: #606266 !important;
          background: #fff !important;
        }

        &.el-button--success {
          border-color: #dcdfe6 !important;
          color: #606266 !important;
          background: #fff !important;
        }
      }
    }
  }

  // 全局覆盖本页面内 el-select 的聚焦样式，去掉默认蓝色边框
  :deep(.el-select .el-input__wrapper.is-focus),
  :deep(.el-select .el-input__wrapper:focus),
  :deep(.el-select .el-input__wrapper:focus-visible),
  :deep(.el-select .el-input.is-focus .el-input__wrapper) {
    box-shadow: 0 0 0 1px #dcdfe6 inset !important;
    border-color: #dcdfe6 !important;
    outline: none !important;
  }
  
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  // 核心统计卡片 - 详细增强版
  .overview-stats {
    margin-bottom: 24px;
  }

  .stat-card-enhanced {
    background: white;
    border-radius: 16px;
    padding: 0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;

    &::before {
      content: '';
      display: none;
    }

    &:hover {
      transform: translateY(-6px);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
      border-color: rgba(102, 126, 234, 0.2);
      
      .arrow-icon {
        transform: translateX(4px);
      }
    }

    .card-header-mini {
      display: flex;
      align-items: center;
      gap: 0;
      padding: 16px 20px 12px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);

      .card-title {
        font-size: 14px;
        font-weight: 600;
        color: #606266;
      }
    }

    .card-main {
      padding: 16px 20px;
      text-align: center;

      .main-value {
        font-size: 36px;
        font-weight: 800;
        color: #303133;
        line-height: 1;
        margin-bottom: 8px;
        letter-spacing: -1px;
      }

      .main-label {
        font-size: 13px;
        color: #909399;
        font-weight: 500;
      }

      .sub-label {
        margin-top: 4px;
        font-size: 12px;
        color: #a0aec0;
        font-weight: 500;
      }

      .main-metrics {
        display: flex;
        gap: 16px;
        justify-content: center;

        .metric-block {
          flex: 1;
          border-radius: 12px;
          padding: 12px;
          display: flex;
          flex-direction: column;
          align-items: center;
          border: none;

          .metric-value {
            font-size: 30px;
            font-weight: 800;
            color: #1f2933;
            line-height: 1;
          }

          .metric-label {
            margin-top: 6px;
            font-size: 13px;
            color: #6b7280;
            font-weight: 600;
          }
        }
      }
    }

    .card-details {
      padding: 12px 20px;
      background: rgba(0, 0, 0, 0.02);

      .detail-row {
        display: flex;
        gap: 12px;
        margin-bottom: 10px;

        &:last-child {
          margin-bottom: 0;
        }

        .detail-item {
          flex: 1;
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 8px;
          background: white;
          border-radius: 8px;
          border: 1px solid rgba(0, 0, 0, 0.06);

          .label {
            font-size: 11px;
            color: #909399;
            margin-bottom: 4px;
            font-weight: 500;
          }

          .value {
            font-size: 16px;
            font-weight: 700;
          color: #303133;

            &.highlight {
              color: #409eff;
            }

            &.success {
              color: #67c23a;
            }

            &.warning {
              color: #e6a23c;
            }

            &.online {
              color: #67c23a;
              animation: pulse 2s ease-in-out infinite;
            }

            &.amount {
              color: #f56c6c;
          font-size: 14px;
            }
            }

            .value-multi {
              display: flex;
              flex-direction: column;
              gap: 4px;
              font-size: 14px;
              color: #303133;

              div {
                font-weight: 600;
              }
            }

            .value-with-sub {
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 2px;

              .sub-value {
                font-size: 11px;
                font-weight: 500;
                color: #909399;
              }
        }
      }
    }
  }
  
    .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
      padding: 12px 20px;
      background: rgba(0, 0, 0, 0.02);
      border-top: 1px solid rgba(0, 0, 0, 0.05);

      .trend-text {
      display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: #606266;
        font-weight: 500;

        .el-icon {
          font-size: 14px;
          color: #667eea;
        }

        .up {
          color: #67c23a;
          font-weight: 700;
        }

        .down {
          color: #f56c6c;
          font-weight: 700;
        }
      }

      .arrow-icon {
        font-size: 16px;
        color: #909399;
        transition: all 0.3s ease;
      }
    }

    &.stat-users {
      &::before {
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
      }
      .card-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }

    &.stat-plans {
      &::before {
        background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
      }
      .card-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }

    &.stat-attractions {
      &::before {
        background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
      }
      .card-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }

    &.stat-orders {
      &::before {
        background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
      }
      .card-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
      }
      
    &.stat-comments {
      &::before {
        background: linear-gradient(90deg, #faa307 0%, #ffba08 100%);
      }
      .card-icon {
        background: linear-gradient(135deg, #faa307 0%, #ffba08 100%);
      }
    }

    &.stat-favorites {
      &::before {
        background: linear-gradient(90deg, #a78bfa 0%, #c084fc 100%);
      }
      .card-icon {
        background: linear-gradient(135deg, #a78bfa 0%, #c084fc 100%);
      }
    }

    &.stat-browse {
      &::before {
        background: linear-gradient(90deg, #fb7185 0%, #f472b6 100%);
      }
      .card-icon {
        background: linear-gradient(135deg, #fb7185 0%, #f472b6 100%);
      }
    }

    &.stat-ai {
      &::before {
        background: linear-gradient(90deg, #06b6d4 0%, #22d3ee 100%);
      }
      .card-icon {
        background: linear-gradient(135deg, #06b6d4 0%, #22d3ee 100%);
      }
    }
  }

  @keyframes pulse {
    0%, 100% {
      opacity: 1;
    }
    50% {
      opacity: 0.6;
    }
  }
  
  // 监测卡片通用样式
  .monitor-card {
    height: 100%;
  
  .card-header {
    display: flex;
    align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
          color: #303133;

      .el-icon {
        font-size: 18px;
        color: #667eea;
      }
    }
  }

  // 用户活跃度卡片（与攻略统计一致的样式）
  .activity-stats-enhanced {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    flex-wrap: wrap;

    .activity-card {
      flex: 1;
      min-width: 140px;
      background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
      border-radius: 12px;
      padding: 18px 12px;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      border: 2px solid transparent;
      position: relative;
      overflow: hidden;

      &::after {
        content: '';
        position: absolute;
        top: -40%;
        right: -40%;
        width: 80%;
        height: 80%;
        border-radius: 50%;
        opacity: 0.05;
        pointer-events: none;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &:nth-child(1) {
        border-color: rgba(103, 194, 58, 0.3);

        &::after {
          background: linear-gradient(135deg, #a0f0a0 0%, #67c23a 100%);
        }

        .activity-value {
          color: #2f855a;
        }
      }

      &:nth-child(2) {
        border-color: rgba(64, 158, 255, 0.3);

        &::after {
          background: linear-gradient(135deg, #b1e3ff 0%, #409eff 100%);
        }
      }

      &:nth-child(3) {
        border-color: rgba(240, 147, 251, 0.3);

        &::after {
          background: linear-gradient(135deg, #ffd3f5 0%, #f093fb 100%);
        }

        .activity-value {
          color: #a855f7;
        }
      }

      .activity-label {
        font-size: 12px;
        color: #606266;
        margin-bottom: 10px;
        font-weight: 600;
        letter-spacing: 0.5px;
        position: relative;
        z-index: 1;
      }

      .activity-value {
        font-size: 28px;
        font-weight: 800;
        color: #303133;
        line-height: 1;
        position: relative;
        z-index: 1;

        .unit {
          font-size: 14px;
          font-weight: 500;
          color: #909399;
          margin-left: 4px;
        }
      }
    }
  }

  .user-distribution {
    margin-top: 20px;

    h4 {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
    }

    .distribution-item {
      margin-bottom: 16px;

      .distribution-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;

        .label {
          font-size: 13px;
          color: #606266;
        }

        .percentage {
          font-size: 13px;
          color: #909399;
        }
      }

      .progress-bar {
        width: 100%;
        height: 6px;
        background-color: #f0f0f0;
        border-radius: 3px;
        overflow: hidden;
        position: relative;

        .progress-fill {
          height: 100%;
          min-width: 0;
          border-radius: 3px;
          transition: width 0.3s ease;
          display: block;
        }
      }
    }
  }

  // 文旅业务样式 - 重新设计
  .business-stats-enhanced {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .stats-group {
      .group-header {
    display: flex;
    align-items: center;
        gap: 10px;
        margin-bottom: 12px;

        .group-icon {
          width: 32px;
          height: 32px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: white;

          &.orders {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }

          &.coupons {
            background: linear-gradient(135deg, #faa307 0%, #ffba08 100%);
          }

          &.bookings {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }
        }

        .group-title {
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }
      }

      .stats-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 8px;

        .stat-item {
          background: white;
          border: 1px solid rgba(0, 0, 0, 0.06);
          border-radius: 10px;
          padding: 12px;
          text-align: center;

          &.warning-item {
            background: white;
            border-color: rgba(0, 0, 0, 0.06);
          }

          &.success-item {
            background: white;
            border-color: rgba(0, 0, 0, 0.06);
          }

          &.highlight-item {
            background: white;
            border-color: rgba(0, 0, 0, 0.06);
          }

          .stat-label {
            font-size: 12px;
          color: #909399;
            margin-bottom: 6px;
          }

          .stat-value {
            font-size: 20px;
            font-weight: 700;
            color: #303133;

            &.warning {
              color: #e6a23c;
            }

            &.success {
              color: #67c23a;
            }

            &.amount {
              color: #f56c6c;
              font-size: 18px;
            }

            .unit {
              font-size: 12px;
        font-weight: 500;
              color: #909399;
              margin-left: 2px;
            }
          }
        }
      }
    }
  }

  // 快捷操作样式 - 重新设计
  .quick-actions {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .actions-section {
      .section-title {
    display: flex;
    align-items: center;
        gap: 8px;
        font-size: 13px;
        font-weight: 600;
        color: #606266;
        margin-bottom: 12px;
        padding: 0 8px;

        .el-icon {
          font-size: 16px;
          color: #909399;
        }
      }

      .action-grid {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }

      .action-item {
        display: flex;
        align-items: center;
        padding: 14px 16px;
        background: white;
        border: 1px solid rgba(0, 0, 0, 0.06);
        border-radius: 12px;
        cursor: pointer;
        transition: background-color 0.2s ease;
        position: relative;
    
    &:hover {
          background: rgba(0, 0, 0, 0.02);
        }

        .action-icon {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          color: white;
          margin-right: 12px;
          flex-shrink: 0;

          &.warning {
            background: linear-gradient(135deg, #e6a23c 0%, #f5c06b 100%);
            box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
          }

          &.danger {
            background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
            box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
          }

          &.primary {
            background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
          }

          &.info {
            background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
            box-shadow: 0 2px 8px rgba(144, 147, 153, 0.3);
          }
        }

        .action-content {
      flex: 1;
          display: flex;
          flex-direction: column;
          gap: 4px;

          .action-label {
            font-size: 14px;
        font-weight: 600;
            color: #303133;
            line-height: 1;
      }

          .action-count {
            font-size: 12px;
        color: #909399;
            line-height: 1;
          }

          .action-desc {
            font-size: 12px;
            color: #909399;
            line-height: 1;
          }
        }

        .action-badge {
          margin-right: 8px;
          flex-shrink: 0;

          :deep(.el-badge__content) {
            font-size: 11px;
            padding: 0 6px;
            height: 18px;
            line-height: 18px;
          }
        }

        .action-arrow {
          font-size: 16px;
          color: #c0c4cc;
          flex-shrink: 0;
        }
      }
    }

    // 待处理事项区域特殊样式
    .priority-section {
      .action-item {
        border-left: 3px solid transparent;

        &:nth-child(1) {
          border-left-color: #e6a23c;
        }

        &:nth-child(2) {
          border-left-color: #f56c6c;
        }

        &:nth-child(3) {
          border-left-color: #409eff;
        }
      }
    }

    // 常用管理区域
    .normal-section {
      .action-item {
        background: rgba(0, 0, 0, 0.02);
        border: 1px solid rgba(0, 0, 0, 0.04);

        &:hover {
          background: rgba(0, 0, 0, 0.04);
        }
      }
    }
  }

  // 实时通知样式
  .realtime-notifications {
    max-height: 300px;
    overflow-y: auto;
    padding: 8px 0;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: rgba(0, 0, 0, 0.05);
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 3px;
    }

    .notification-item {
      display: flex;
      align-items: flex-start;
      padding: 12px;
      margin-bottom: 8px;
      background: rgba(0, 0, 0, 0.02);
      border-radius: 12px;
      transition: all 0.2s;

      &:hover {
        background: rgba(102, 126, 234, 0.05);
      }

      .notification-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
        margin-right: 12px;
        color: white;
        flex-shrink: 0;

        &.type-order {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.type-comment {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.type-application {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .notification-content {
        flex: 1;

        .notification-text {
          font-size: 13px;
          color: #303133;
          margin-bottom: 4px;
          line-height: 1.4;
        }

        .notification-time {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    letter-spacing: 0.5px;
    
    .refresh-btn-header {
      margin-left: auto;
      flex-shrink: 0;
    }
    
    .header-actions {
      display: flex;
      gap: 10px;
      
      .el-button {
        border-radius: 8px;
        font-weight: 700 !important;
        font-size: 14px !important;
        padding: 8px 20px !important;
        transition: all 0.2s ease;
        border: none !important;
        
        // 刷新按钮美化
        &.el-button--primary {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
          color: #fff !important;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3) !important;
          
          &:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%) !important;
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4) !important;
          }
        }
        
        // 导出按钮美化
        &.el-button--success {
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%) !important;
          color: #fff !important;
          box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3) !important;
          
          &:hover {
            background: linear-gradient(135deg, #85ce61 0%, #67c23a 100%) !important;
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(103, 194, 58, 0.4) !important;
          }
        }
      }
    }
    
    // 美化所有card-header中的按钮
    :deep(.el-button--text) {
      color: #667eea !important;
      font-weight: 700 !important;
      font-size: 14px !important;
      padding: 6px 12px !important;
      border-radius: 6px !important;
      transition: all 0.2s ease !important;
      
      &:hover {
        background: rgba(102, 126, 234, 0.1) !important;
        color: #764ba2 !important;
        transform: translateY(-1px);
      }
    }
  }
  
  // 卡片美化 - 高级玻璃态风格
  :deep(.el-card) {
    border-radius: 24px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.06),
      0 2px 8px rgba(0, 0, 0, 0.04),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: hidden;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, 
        transparent 0%, 
        rgba(255, 255, 255, 0.8) 50%, 
        transparent 100%);
      opacity: 0.6;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 
        0 16px 48px rgba(0, 0, 0, 0.08),
        0 4px 16px rgba(0, 0, 0, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      border-color: rgba(255, 255, 255, 0.3);
    }
    
    .el-card__header {
      padding: 28px 32px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: linear-gradient(135deg, 
        rgba(255, 255, 255, 0.4) 0%, 
        rgba(255, 255, 255, 0.2) 100%);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 1px;
        background: linear-gradient(90deg, 
          transparent 0%, 
          rgba(0, 0, 0, 0.08) 50%, 
          transparent 100%);
      }
    }
  }
  
  .chart-container {
    margin-top: 20px;
    padding: 10px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.02), rgba(118, 75, 162, 0.02));
    border-radius: 12px;
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    }
  }

  // 图表展示区域 - 高级玻璃态设计
  .function-charts-section {
    margin-bottom: 40px;
    
    .chart-card {
      background: linear-gradient(135deg, 
        rgba(255, 255, 255, 0.9) 0%, 
        rgba(255, 255, 255, 0.7) 100%);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 20px;
      padding: 24px;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      box-shadow: 
        0 8px 32px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04),
        inset 0 1px 0 rgba(255, 255, 255, 0.6);
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 2px;
        background: linear-gradient(90deg, 
          transparent 0%, 
          rgba(64, 158, 255, 0.3) 50%, 
          transparent 100%);
        opacity: 0;
        transition: opacity 0.4s ease;
      }
      
      &:hover {
        transform: translateY(-4px);
        border-color: rgba(64, 158, 255, 0.4);
        box-shadow: 
          0 16px 48px rgba(64, 158, 255, 0.12),
          0 4px 16px rgba(0, 0, 0, 0.08),
          inset 0 1px 0 rgba(255, 255, 255, 0.8);
        
        &::before {
          opacity: 1;
        }
      }
      
      .chart-header {
        margin-bottom: 20px;
        padding-bottom: 16px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 0;
          width: 40px;
          height: 2px;
          background: linear-gradient(90deg, 
            rgba(64, 158, 255, 0.6) 0%, 
            transparent 100%);
          border-radius: 2px;
        }
        
        .chart-title {
          font-size: 17px;
          font-weight: 700;
          background: linear-gradient(135deg, #1a1a1a 0%, #4a4a4a 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          margin: 0 0 6px 0;
          line-height: 1.4;
          letter-spacing: -0.3px;
        }
        
        .chart-subtitle {
          font-size: 12px;
          color: #6b7280;
          margin: 0;
          line-height: 1.5;
          font-weight: 500;
          letter-spacing: 0.2px;
        }
      }
      
      .function-bar-chart {
        width: 100%;
        height: 420px;
        border-radius: 12px;
      }
      
      .function-pie-chart {
        width: 100%;
        height: 420px;
        border-radius: 12px;
      }
    }
  }

  // 全球访问地图样式
  .world-map-wrapper {
    margin-top: 20px;
  }

  .world-map-card {
    .world-map-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;

        .header-left {
          .header-title {
            font-size: 18px;
            font-weight: 700;
            color: #1f2937;
          }

          .header-subtitle {
            font-size: 13px;
            color: #6b7280;
            margin-top: 4px;
          }
        }

    }

    .world-map-section {
      display: flex;
      gap: 24px;
      flex-wrap: wrap;
    }

    .world-map-canvas {
      flex: 2;
      min-width: 420px;
      min-height: 420px;
      border-radius: 18px;
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.08), rgba(0, 242, 254, 0.08));
      padding: 16px;
    }

    .world-map-chart {
      width: 100%;
      height: 100%;
    }
      
      .world-map-placeholder {
        width: 100%;
        height: 100%;
        min-height: 360px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 12px;
      }

      .world-map-sidebar {
      flex: 1;
      min-width: 280px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .world-map-search {
      :deep(.el-input__wrapper) {
        border-radius: 12px;
        // 添加默认外边框样式
        border: 1px solid #dcdfe6 !important;
        // 固定阴影
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
        min-height: 44px;
      }
      
      :deep(.el-input__wrapper:hover) {
        border-color: #dcdfe6 !important;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
      }
      
      :deep(.el-input__wrapper.is-focus),
      :deep(.el-input__wrapper:focus),
      :deep(.el-input__wrapper:focus-visible) {
        border-color: #dcdfe6 !important;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
        outline: none !important;
      }
      
      :deep(.el-input__inner) {
        min-height: 44px;
        padding: 0 12px;
      }
    }

    .world-map-summary {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 12px;

      @media (max-width: 1200px) {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }

      .summary-item {
        background: #ffffff;
        border-radius: 16px;
        padding: 16px 18px;
        display: flex;
        flex-direction: column;
        gap: 6px;
        border: 1px solid #edf2f7;
        box-shadow: 0 4px 12px rgba(15, 23, 42, 0.05);

        .label {
          font-size: 12px;
          color: #94a3b8;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }

        .value {
          font-size: 24px;
          font-weight: 700;
          color: #1f2937;
        }
      }
    }

    .world-map-list {
      max-height: 300px;

      .world-map-list-item {
        padding: 12px 14px;
        border-radius: 12px;
        border: 1px solid rgba(15, 23, 42, 0.06);
        margin-bottom: 10px;
        background: #ffffff;
        cursor: pointer;
        transition: all 0.2s ease;

        .item-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-weight: 600;
          color: #1f2937;
          margin-bottom: 4px;
        }

        .item-footer {
          display: flex;
          gap: 12px;
          font-size: 12px;
          color: #6b7280;
        }

        &.active {
          border-color: rgba(15, 23, 42, 0.06);
          background: #ffffff;
        }

        &:hover {
          border-color: rgba(15, 23, 42, 0.06);
          background: #ffffff;
        }
      }

      .world-map-empty {
        text-align: center;
        color: #94a3b8;
        padding: 12px 0;
      }
    }

    .world-map-detail {
      border-radius: 14px;
      padding: 16px;
      background: #ffffff;
      display: flex;
      flex-direction: column;
      gap: 10px;
      border: 1px solid rgba(15, 23, 42, 0.08);

      .detail-title {
        font-size: 15px;
        font-weight: 700;
        color: #111827;
      }

      .detail-row {
        display: flex;
        justify-content: space-between;
        font-size: 13px;
        color: #4b5563;

        strong {
          color: #0f172a;
          font-size: 15px;
        }
      }
    }
  }

  // 功能卡片区域 - 高级现代设计
  .function-cards-section {
    .section-header {
      margin-bottom: 32px;
      padding-bottom: 20px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -1px;
        left: 0;
        width: 60px;
        height: 2px;
        background: linear-gradient(90deg, 
          rgba(64, 158, 255, 0.6) 0%, 
          transparent 100%);
        border-radius: 2px;
      }
      
      .section-title {
        font-size: 20px;
        font-weight: 700;
        background: linear-gradient(135deg, #1a1a1a 0%, #4a4a4a 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        margin: 0 0 10px 0;
        line-height: 1.4;
        letter-spacing: -0.5px;
      }
      
      .section-description {
        font-size: 14px;
        color: #6b7280;
        margin: 0;
        line-height: 1.6;
        font-weight: 500;
        letter-spacing: 0.1px;
      }
    }
    .function-cards-grid {
      display: grid;
      gap: 24px;
      // 桌面端两行铺满
      grid-template-columns: repeat(4, minmax(0, 1fr));
      
      // 响应式调整
      @media (max-width: 1400px) {
        grid-template-columns: repeat(3, minmax(0, 1fr));
      }
      
      @media (max-width: 1024px) {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
      
      @media (max-width: 640px) {
        grid-template-columns: 1fr;
      }
      
      .function-card {
        background: linear-gradient(135deg, 
          rgba(255, 255, 255, 0.95) 0%, 
          rgba(255, 255, 255, 0.85) 100%);
        backdrop-filter: blur(20px) saturate(180%);
        -webkit-backdrop-filter: blur(20px) saturate(180%);
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 20px;
        padding: 24px;
        position: relative;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        overflow: hidden;
        box-shadow: 
          0 8px 32px rgba(0, 0, 0, 0.06),
          0 2px 8px rgba(0, 0, 0, 0.04),
          inset 0 1px 0 rgba(255, 255, 255, 0.6);
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, 
            transparent 0%, 
            rgba(64, 158, 255, 0.3) 50%, 
            transparent 100%);
          opacity: 0;
          transition: opacity 0.4s ease;
        }
        
        &::after {
          content: '';
          position: absolute;
          top: -50%;
          right: -50%;
          width: 200%;
          height: 200%;
          background: radial-gradient(circle, 
            rgba(64, 158, 255, 0.05) 0%, 
            transparent 70%);
          opacity: 0;
          transition: opacity 0.4s ease;
        }
        
        &:hover {
          transform: none;
          border-color: rgba(64, 158, 255, 0.25);
          box-shadow: 
            0 10px 30px rgba(64, 158, 255, 0.08),
            0 4px 12px rgba(0, 0, 0, 0.06),
            inset 0 1px 0 rgba(255, 255, 255, 0.7);
          &::before,
          &::after {
            opacity: 0;
          }
          
          .card-icon,
          .card-rank {
            transform: none;
            box-shadow: none;
          }
        }
        
        .card-content {
          .card-name {
            font-size: 17px;
            font-weight: 700;
            background: linear-gradient(135deg, #1a1a1a 0%, #4a4a4a 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin: 0 0 8px 0;
            line-height: 1.4;
            letter-spacing: -0.3px;
          }
          
          .card-desc {
            font-size: 13px;
            color: #6b7280;
            margin: 0 0 20px 0;
            line-height: 1.6;
            font-weight: 500;
          }
          
          .card-stats {
            display: flex;
            gap: 20px;
            margin-bottom: 16px;
            padding: 16px;
            background: rgba(0, 0, 0, 0.02);
            border-radius: 12px;
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            
            .stat-item {
              display: flex;
              flex-direction: column;
              gap: 6px;
              flex: 1;
              
              .stat-label {
                font-size: 11px;
                color: #9ca3af;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
              }
              
              .stat-value {
                font-size: 24px;
                font-weight: 700;
                background: linear-gradient(135deg, #1a1a1a 0%, #4a4a4a 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                font-family: 'SF Mono', 'Monaco', 'Courier New', monospace;
                letter-spacing: -0.5px;
              }
              
              .stat-percent {
                font-size: 22px;
                font-weight: 700;
                background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                font-family: 'SF Mono', 'Monaco', 'Courier New', monospace;
                letter-spacing: -0.5px;
              }
            }
          }
          
          .card-trend {
            margin-bottom: 16px;
            
            .trend-badge {
              display: inline-flex;
              align-items: center;
              gap: 6px;
              padding: 8px 14px;
              border-radius: 10px;
              font-size: 13px;
              font-weight: 600;
              backdrop-filter: blur(10px);
              -webkit-backdrop-filter: blur(10px);
              transition: all 0.3s ease;
              
              .el-icon {
                font-size: 14px;
              }
              
              &.trend-up {
                color: #10b981;
                background: linear-gradient(135deg, 
                  rgba(16, 185, 129, 0.15) 0%, 
                  rgba(16, 185, 129, 0.08) 100%);
                border: 1px solid rgba(16, 185, 129, 0.2);
              }
              
              &.trend-down {
                color: #ef4444;
                background: linear-gradient(135deg, 
                  rgba(239, 68, 68, 0.15) 0%, 
                  rgba(239, 68, 68, 0.08) 100%);
                border: 1px solid rgba(239, 68, 68, 0.2);
              }
            }
          }
        }
        
        .card-progress {
          margin-top: 20px;
          
          .progress-bar {
            height: 10px;
            background: rgba(0, 0, 0, 0.04);
            border-radius: 10px;
            overflow: hidden;
            position: relative;
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            
            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              bottom: 0;
              background: linear-gradient(90deg, 
                transparent 0%, 
                rgba(255, 255, 255, 0.3) 50%, 
                transparent 100%);
              animation: shimmer 2s infinite;
            }
            
            .progress-fill {
              height: 100%;
              border-radius: 10px;
              transition: width 1s cubic-bezier(0.4, 0, 0.2, 1);
              position: relative;
              overflow: hidden;
              
              &::after {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: linear-gradient(90deg, 
                  transparent 0%, 
                  rgba(255, 255, 255, 0.4) 50%, 
                  transparent 100%);
                animation: shimmer 2s infinite;
              }
            }
          }
        }
      }
    }
    
    .function-empty {
      padding: 60px 0;
    }
  }
  
  @keyframes shimmer {
    0% {
      transform: translateX(-100%);
    }
    100% {
      transform: translateX(100%);
    }
  }
  
  // 卡片头部副标题 - 高级样式
  .header-subtitle {
    font-size: 13px;
    color: #6b7280;
    font-weight: 500;
    margin-left: 16px;
    letter-spacing: 0.2px;

    .range-chip {
      display: inline-flex;
      align-items: center;
      padding: 4px 12px;
      margin-left: 12px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: 600;
      color: #3b82f6;
      background: linear-gradient(135deg, 
        rgba(59, 130, 246, 0.12) 0%, 
        rgba(59, 130, 246, 0.08) 100%);
      border: 1px solid rgba(59, 130, 246, 0.2);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      box-shadow: 
        0 2px 8px rgba(59, 130, 246, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.5);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 
          0 4px 12px rgba(59, 130, 246, 0.2),
          inset 0 1px 0 rgba(255, 255, 255, 0.6);
      }

      &.simple {
        padding: 4px 10px;
        color: #666;
        border-radius: 8px;
        background: rgba(0, 0, 0, 0.04);
        border: none;
        box-shadow: none;

        &:hover {
          transform: none;
          box-shadow: none;
        }
      }
    }
  }

}
</style>

<style lang="scss">
// 下拉选项样式 - 移除蓝色（因为下拉菜单挂载在body上，需要非scoped样式）
// 在下拉菜单容器上覆盖 CSS 变量
.el-select-dropdown.el-popper[data-popper-placement],
.el-select-dropdown {
  --el-color-primary: #606266 !important;
  --el-text-color-primary: #303133 !important;
  --el-fill-color-light: #f5f5f5 !important;
}

// 使用最高优先级覆盖所有可能的蓝色样式
.el-select-dropdown.el-popper[data-popper-placement] .el-select-dropdown__item,
.el-select-dropdown .el-select-dropdown__item,
.el-popper .el-select-dropdown__item,
.el-select-dropdown__item {
  color: #606266 !important;
  background-color: transparent !important;
  
  &:hover {
    background-color: #f5f7fa !important;
    color: #303133 !important;
  }
  
  &.selected,
  &.is-selected,
  &[aria-selected="true"] {
    color: #303133 !important;
    background-color: #f5f5f5 !important;
    font-weight: 600 !important;
  }
  
  // 移除选中时的蓝色（所有可能的组合）
  &.selected:hover,
  &.is-selected:hover,
  &[aria-selected="true"]:hover,
  &.selected.is-selected,
  &.selected.is-selected:hover {
    background-color: #f5f5f5 !important;
    color: #303133 !important;
  }
}

// 更具体的选择器，针对 admin-dashboard 页面的下拉菜单
.admin-dashboard .header-right .el-select + .el-select-dropdown .el-select-dropdown__item,
.admin-dashboard .el-select-dropdown .el-select-dropdown__item {
  color: #606266 !important;
  background-color: transparent !important;
  
  &:hover {
    background-color: #f5f7fa !important;
    color: #303133 !important;
  }
  
  &.selected,
  &.is-selected,
  &[aria-selected="true"] {
    color: #303133 !important;
    background-color: #f5f5f5 !important;
    font-weight: 600 !important;
  }
  
  &.selected:hover,
  &.is-selected:hover,
  &[aria-selected="true"]:hover {
    background-color: #f5f5f5 !important;
    color: #303133 !important;
  }
}
</style>




