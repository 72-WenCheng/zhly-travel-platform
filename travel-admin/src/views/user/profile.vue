<template>
  <div class="profile-container">
    <!-- 个性化用户信息卡片 -->
    <div class="user-info-card">
      <div class="user-avatar-section">
        <el-avatar :size="120" :src="userForm.avatar" class="user-avatar">
          {{ userForm.nickname?.charAt(0) || '旅' }}
        </el-avatar>
      </div>
      <div class="user-info-section">
        <div class="user-name">{{ userForm.nickname || '旅游达人' }}</div>
        <div class="user-email">{{ userForm.email || 'user@example.com' }}</div>
        <div class="user-stats">
          <div class="stat-item">
            <el-icon><Star /></el-icon>
            <span>{{ userForm.points || 1250 }} 积分</span>
          </div>
          <div class="stat-item">
            <el-icon><Trophy /></el-icon>
            <span>等级 {{ userForm.level || 3 }}</span>
          </div>
        </div>
        <div class="quick-links">
          <el-button type="default" text @click="goToAddresses">
            <el-icon><Location /></el-icon>
            我的地址
          </el-button>
        </div>
      </div>
      <div class="user-actions">
        <el-button type="primary" round @click="activeTab = 'basic'">
          <el-icon><Edit /></el-icon>
          编辑资料
        </el-button>
      </div>
    </div>

    <!-- 个性化标签展示 - 全新设计 -->
    <div class="personalized-tags-container">
      <!-- 标签统计概览 -->
      <el-card class="tags-overview-card">
        <div class="overview-header">
          <div class="header-left">
            <el-icon class="header-icon"><DataAnalysis /></el-icon>
            <div class="header-info">
              <h3>个性化标签设置</h3>
              <p>设置你的个性化标签，获取精准的景点推荐</p>
            </div>
          </div>
          <div class="header-stats">
            <div class="stat-item">
              <span class="stat-label">已选标签</span>
              <span class="stat-value">{{ totalSelectedTags }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">完整度</span>
              <span class="stat-value">{{ completionRate }}%</span>
            </div>
          </div>
        </div>
        
        <!-- 完成度进度条 -->
        <div class="completion-progress">
          <el-progress :percentage="completionRate" :color="progressColor" :stroke-width="8">
            <template #default="{ percentage }">
              <span class="percentage-text">{{ percentage }}%</span>
            </template>
          </el-progress>
          <p class="progress-tip">
            <el-icon><InfoFilled /></el-icon>
            {{ completionTip }}
          </p>
        </div>
      </el-card>

      <!-- 用户画像卡片 - 新增 - 始终显示 -->
      <el-card class="user-portrait-card">
        <div class="portrait-header">
          <div class="portrait-icon">
            <el-icon><Avatar /></el-icon>
          </div>
          <div class="portrait-title">
            <h3>🎨 你的旅游画像</h3>
            <p v-if="totalSelectedTags > 0">基于 {{ totalSelectedTags }} 个标签智能生成</p>
            <p v-else>选择标签后自动生成个性化画像</p>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="portrait-empty" v-if="totalSelectedTags === 0">
          <div class="empty-icon">
            <el-icon><PictureFilled /></el-icon>
          </div>
          <h3>还没有设置标签</h3>
          <p>开始选择你的兴趣标签，我们将为你生成专属的旅游画像</p>
          <el-button type="primary" @click="activeTagTab = 'basic'" round>
            <el-icon><Edit /></el-icon>
            开始设置标签
          </el-button>
        </div>

        <!-- 画像内容 -->
        <div class="portrait-content" v-else>
          <!-- 画像标签云 -->
          <div class="portrait-tags">
            <el-tag 
              v-for="tag in portraitTagsList" 
              :key="tag.label"
              :type="tag.type"
              effect="dark"
              size="large"
              class="portrait-tag"
            >
              <span class="tag-emoji">{{ tag.icon }}</span>
              {{ tag.label }}
            </el-tag>
          </div>

          <!-- 画像描述 -->
          <div class="portrait-description">
            <div class="description-item" v-if="portraitDescription.intro">
              <el-icon class="item-icon"><User /></el-icon>
              <div class="item-content">
                <h4>画像描述</h4>
                <p>{{ portraitDescription.intro }}</p>
              </div>
            </div>

            <div class="description-item" v-if="portraitDescription.style">
              <el-icon class="item-icon"><TrendCharts /></el-icon>
              <div class="item-content">
                <h4>旅游风格</h4>
                <p>{{ portraitDescription.style }}</p>
              </div>
            </div>

            <div class="description-item" v-if="portraitDescription.recommendation">
              <el-icon class="item-icon"><Location /></el-icon>
              <div class="item-content">
                <h4>推荐目的地</h4>
                <p>{{ portraitDescription.recommendation }}</p>
              </div>
            </div>
          </div>

          <!-- 画像雷达图 -->
          <div class="portrait-radar">
            <div class="radar-title">
              <el-icon><DataAnalysis /></el-icon>
              <span>兴趣偏好分析</span>
            </div>
            <div class="radar-items">
              <div 
                v-for="dimension in radarDimensions" 
                :key="dimension.name"
                class="radar-item"
              >
                <div class="dimension-name">{{ dimension.icon }} {{ dimension.name }}</div>
                <el-progress 
                  :percentage="dimension.value" 
                  :color="dimension.color"
                  :stroke-width="12"
                />
              </div>
            </div>
          </div>

          <!-- 匹配度展示 -->
          <div class="portrait-match">
            <div class="match-header">
              <el-icon><Finished /></el-icon>
              <span>您可能喜欢的旅游类型</span>
            </div>
            <div class="match-items">
              <div 
                v-for="match in matchedTypes" 
                :key="match.name"
                class="match-item"
              >
                <div class="match-icon">{{ match.icon }}</div>
                <div class="match-info">
                  <div class="match-name">{{ match.name }}</div>
                  <div class="match-score">
                    <span class="score-value">{{ match.score }}%</span>
                    <span class="score-label">匹配度</span>
                  </div>
                </div>
                <el-progress 
                  type="circle" 
                  :percentage="match.score" 
                  :width="60"
                  :color="getMatchColor(match.score)"
                />
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 详细标签设置 -->
      <el-card class="tags-detail-card">
        <el-tabs v-model="activeTagTab" class="tags-tabs">
          <!-- 基础偏好 -->
          <el-tab-pane label="基础偏好" name="basic">
            <div class="tab-content">
              <!-- 兴趣标签 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Star /></el-icon>
                    <span>兴趣标签</span>
                    <el-tag type="info" size="small" effect="plain">多选</el-tag>
                  </div>
                  <span class="selected-count">已选 {{ selectedTags.length }}/10</span>
                </div>
                <p class="group-description">选择你感兴趣的旅游类型，系统会根据这些标签为你推荐相关景点</p>
                <div class="tag-grid">
                  <div
                    v-for="tag in interestTags"
                    :key="tag.value"
                    class="tag-item"
                    :class="{ 'is-selected': selectedTags.includes(tag.value) }"
                    @click="toggleTag(tag.value)"
                  >
                    <div class="tag-icon">{{ tag.icon }}</div>
                    <div class="tag-name">{{ tag.label }}</div>
                    <div class="tag-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 旅游偏好 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Compass /></el-icon>
                    <span>旅游偏好</span>
                    <el-tag type="warning" size="small" effect="plain">单选</el-tag>
                  </div>
                </div>
                <p class="group-description">你最喜欢的旅游方式是什么？</p>
                <div class="tag-grid">
                  <div
                    v-for="pref in travelPreferences"
                    :key="pref.value"
                    class="tag-item preference-item"
                    :class="{ 'is-selected': preferenceForm.travelPreference === pref.value }"
                    @click="preferenceForm.travelPreference = pref.value"
                  >
                    <div class="tag-icon">{{ pref.icon }}</div>
                    <div class="tag-name">{{ pref.label }}</div>
                    <div class="tag-description">{{ pref.description }}</div>
                    <div class="tag-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 消费水平 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Wallet /></el-icon>
                    <span>消费水平</span>
                    <el-tag type="warning" size="small" effect="plain">单选</el-tag>
                  </div>
                </div>
                <p class="group-description">你的旅游预算水平</p>
                <div class="consumption-selector">
                  <div
                    v-for="level in consumptionLevels"
                    :key="level.value"
                    class="consumption-item"
                    :class="{ 'is-selected': userForm.consumptionLevel === level.value }"
                    @click="userForm.consumptionLevel = level.value"
                  >
                    <div class="consumption-icon">{{ level.icon }}</div>
                    <div class="consumption-info">
                      <div class="consumption-name">{{ level.label }}</div>
                      <div class="consumption-range">{{ level.range }}</div>
                      <div class="consumption-desc">{{ level.description }}</div>
                    </div>
                    <div class="consumption-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 出行方式 -->
          <el-tab-pane label="出行方式" name="travel">
            <div class="tab-content">
              <!-- 出行方式 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Position /></el-icon>
                    <span>出行方式</span>
                    <el-tag type="info" size="small" effect="plain">多选</el-tag>
                  </div>
                  <span class="selected-count">已选 {{ (preferenceForm.travelMethods || []).length }}/5</span>
                </div>
                <p class="group-description">你常用的出行方式</p>
                <div class="tag-grid">
                  <div
                    v-for="method in travelMethods"
                    :key="method.value"
                    class="tag-item"
                    :class="{ 'is-selected': (preferenceForm.travelMethods || []).includes(method.value) }"
                    @click="toggleTravelMethod(method.value)"
                  >
                    <div class="tag-icon">{{ method.icon }}</div>
                    <div class="tag-name">{{ method.label }}</div>
                    <div class="tag-description">{{ method.description }}</div>
                    <div class="tag-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 旅游季节 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Sunny /></el-icon>
                    <span>旅游季节偏好</span>
                    <el-tag type="info" size="small" effect="plain">多选</el-tag>
                  </div>
                </div>
                <p class="group-description">你喜欢在哪个季节旅游？</p>
                <div class="season-grid">
                  <div
                    v-for="season in seasons"
                    :key="season.value"
                    class="season-item"
                    :class="{ 'is-selected': (preferenceForm.seasons || []).includes(season.value) }"
                    @click="toggleSeason(season.value)"
                  >
                    <div class="season-icon" :style="{ background: season.color }">
                      {{ season.icon }}
                    </div>
                    <div class="season-name">{{ season.label }}</div>
                    <div class="season-months">{{ season.months }}</div>
                    <div class="season-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 旅游时长 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Clock /></el-icon>
                    <span>旅游时长偏好</span>
                    <el-tag type="warning" size="small" effect="plain">单选</el-tag>
                  </div>
                </div>
                <p class="group-description">你通常的旅游时长</p>
                <div class="duration-selector">
                  <div
                    v-for="duration in durations"
                    :key="duration.value"
                    class="duration-item"
                    :class="{ 'is-selected': preferenceForm.duration === duration.value }"
                    @click="preferenceForm.duration = duration.value"
                  >
                    <div class="duration-icon">{{ duration.icon }}</div>
                    <div class="duration-label">{{ duration.label }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 地区偏好 -->
          <el-tab-pane label="地区偏好" name="region">
            <div class="tab-content">
              <!-- 常去城市 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Location /></el-icon>
                    <span>常去城市</span>
                  </div>
                  <span class="selected-count">已选 {{ (preferenceForm.frequentCities || []).length }} 个城市</span>
                </div>
                <p class="group-description">选择你常去或想去的城市</p>
                <div class="city-selector">
                  <el-select
                    v-model="preferenceForm.frequentCities"
                    multiple
                    filterable
                    placeholder="搜索并选择城市"
                    class="city-select"
                    :max-collapse-tags="3"
                  >
                    <el-option-group
                      v-for="group in cityGroups"
                      :key="group.label"
                      :label="group.label"
                    >
                      <el-option
                        v-for="city in group.cities"
                        :key="city"
                        :label="city"
                        :value="city"
                      />
                    </el-option-group>
                  </el-select>
                </div>
                
                <!-- 已选城市展示 -->
                <div class="selected-cities" v-if="(preferenceForm.frequentCities || []).length > 0">
                  <el-tag
                    v-for="city in preferenceForm.frequentCities"
                    :key="city"
                    closable
                    @close="removeCity(city)"
                    class="city-tag"
                  >
                    {{ city }}
                  </el-tag>
                </div>
              </div>

              <!-- 地区类型偏好 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><MapLocation /></el-icon>
                    <span>地区类型偏好</span>
                    <el-tag type="info" size="small" effect="plain">多选</el-tag>
                  </div>
                </div>
                <p class="group-description">你更喜欢哪种类型的旅游目的地？</p>
                <div class="tag-grid">
                  <div
                    v-for="region in regionTypes"
                    :key="region.value"
                    class="tag-item"
                    :class="{ 'is-selected': (preferenceForm.regionTypes || []).includes(region.value) }"
                    @click="toggleRegionType(region.value)"
                  >
                    <div class="tag-icon">{{ region.icon }}</div>
                    <div class="tag-name">{{ region.label }}</div>
                    <div class="tag-description">{{ region.description }}</div>
                    <div class="tag-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 个人信息 -->
          <el-tab-pane label="个人信息" name="personal">
            <div class="tab-content">
              <!-- 年龄段 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><User /></el-icon>
                    <span>年龄段</span>
                    <el-tag type="warning" size="small" effect="plain">单选</el-tag>
                  </div>
                </div>
                <p class="group-description">选择你的年龄段，获取适合的推荐</p>
                <div class="age-selector">
                  <div
                    v-for="age in ageRanges"
                    :key="age.value"
                    class="age-item"
                    :class="{ 'is-selected': preferenceForm.ageRange === age.value }"
                    @click="preferenceForm.ageRange = age.value"
                  >
                    <div class="age-icon">{{ age.icon }}</div>
                    <div class="age-label">{{ age.label }}</div>
                    <div class="age-range">{{ age.range }}</div>
                  </div>
                </div>
              </div>

              <!-- 职业类型 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Briefcase /></el-icon>
                    <span>职业类型</span>
                    <el-tag type="warning" size="small" effect="plain">单选</el-tag>
                  </div>
                </div>
                <p class="group-description">你的职业类型（有助于推荐适合的旅游时间）</p>
                <div class="tag-grid">
                  <div
                    v-for="job in jobTypes"
                    :key="job.value"
                    class="tag-item"
                    :class="{ 'is-selected': preferenceForm.jobType === job.value }"
                    @click="preferenceForm.jobType = job.value"
                  >
                    <div class="tag-icon">{{ job.icon }}</div>
                    <div class="tag-name">{{ job.label }}</div>
                    <div class="tag-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 旅游经验 -->
              <div class="tag-group">
                <div class="group-header">
                  <div class="group-title">
                    <el-icon class="title-icon"><Medal /></el-icon>
                    <span>旅游经验</span>
                    <el-tag type="warning" size="small" effect="plain">单选</el-tag>
                  </div>
                </div>
                <p class="group-description">你的旅游经验水平</p>
                <div class="experience-selector">
                  <div
                    v-for="exp in experienceLevels"
                    :key="exp.value"
                    class="experience-item"
                    :class="{ 'is-selected': preferenceForm.experienceLevel === exp.value }"
                    @click="preferenceForm.experienceLevel = exp.value"
                  >
                    <div class="experience-icon" :style="{ background: exp.color }">
                      {{ exp.icon }}
                    </div>
                    <div class="experience-info">
                      <div class="experience-name">{{ exp.label }}</div>
                      <div class="experience-desc">{{ exp.description }}</div>
                    </div>
                    <div class="experience-check">
                      <el-icon><Check /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>

        <!-- 操作按钮 -->
        <div class="tags-actions">
          <el-button @click="resetTags" icon="Refresh">
            重置所有标签
          </el-button>
          <el-button type="primary" @click="saveTags" :loading="saving" icon="Check">
            保存并获取推荐
          </el-button>
        </div>
      </el-card>

      <!-- 智能推荐提示 -->
      <el-card class="recommendation-tip-card" v-if="showRecommendationTip">
        <div class="tip-content">
          <el-icon class="tip-icon"><MagicStick /></el-icon>
          <div class="tip-text">
            <h4>🎉 标签设置完成！</h4>
            <p>系统已根据你的标签偏好生成个性化推荐，快去首页查看吧！</p>
          </div>
          <el-button type="primary" @click="goToDashboard" icon="ArrowRight">
            查看推荐
          </el-button>
        </div>
      </el-card>
    </div>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" label-width="120px" class="profile-form">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input v-model="userForm.username" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="userForm.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="userForm.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-radio-group v-model="userForm.gender">
                    <el-radio :label="0">女</el-radio>
                    <el-radio :label="1">男</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="userForm.age" :min="1" :max="150" placeholder="请输入年龄" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                :action="uploadAvatarAction"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveBasicInfo" :loading="saving">
                保存
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 偏好设置 -->
        <el-tab-pane label="偏好设置" name="preference">
          <el-form ref="preferenceFormRef" :model="preferenceForm" :rules="preferenceFormRules" label-width="120px" class="profile-form">
            <el-form-item label="旅游偏好" prop="travelPreference">
              <el-select v-model="preferenceForm.travelPreference" placeholder="请选择">
                <el-option label="自由行" :value="1" />
                <el-option label="跟团游" :value="2" />
                <el-option label="休闲度假" :value="3" />
                <el-option label="户外探险" :value="4" />
              </el-select>
            </el-form-item>

            <el-form-item label="兴趣标签">
              <el-tag
                v-for="tag in selectedTags"
                :key="tag"
                closable
                @close="handleTagClose(tag)"
                style="margin-right: 8px;"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="tagInputRef"
                v-model="tagInputValue"
                size="small"
                style="width: 90px"
                @keyup.enter="handleTagInputConfirm"
                @blur="handleTagInputConfirm"
              />
              <el-button v-else size="small" @click="showTagInput">+ 添加标签</el-button>
            </el-form-item>

            <el-form-item label="常去城市">
              <el-select
                v-model="preferenceForm.frequentCities"
                multiple
                filterable
                placeholder="请选择常去城市"
                style="width: 100%"
              >
                <el-option
                  v-for="city in cities"
                  :key="city"
                  :label="city"
                  :value="city"
                />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="savePreference" :loading="saving">
                保存
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 安全设置 -->
        <el-tab-pane label="安全设置" name="security">
          <div class="security-panel">
            <div class="security-panel__header">
              <div class="security-panel__title">
                <span>账户安全</span>
                <h3>修改登录密码</h3>
              </div>
              <div class="security-panel__badge">
                <div class="badge-dot"></div>
                <span>White·Grey Minimal</span>
              </div>
            </div>
            <p class="security-panel__desc">
              使用 9 位以内的新密码，保持纯净的白灰界面节奏。建议定期更新，避免与其他平台复用。
            </p>
            <el-form
              ref="securityFormRef"
              :model="securityForm"
              :rules="securityFormRules"
              label-width="120px"
              label-position="top"
              class="profile-form security-form"
            >
              <div class="security-form__fields">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="securityForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入原密码"
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="securityForm.newPassword"
                    type="password"
                    show-password
                    placeholder="请输入新密码（至少9位）"
                  />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="securityForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                  />
                </el-form-item>
              </div>
              <div class="security-form__actions">
                <el-button type="primary" @click="changePassword" :loading="saving">
                  保存新密码
                </el-button>
              </div>
            </el-form>
            <div class="security-panel__note">
              <span>提示</span>
              <p>密码更新后将在 30 秒内同步至所有端，确保在安静环境下完成操作。</p>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { uploadAvatar, updateUserInfo, changePassword as changePwd } from '@/api/user'
import { 
  Star, Trophy, Edit, PriceTag, Check, Refresh, User, 
  DataAnalysis, InfoFilled, Compass, Wallet, Position, 
  Sunny, Clock, Location, MapLocation, Briefcase, Medal,
  MagicStick, ArrowRight, Avatar, TrendCharts, Finished, PictureFilled
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const activeTab = ref('basic')
const activeTagTab = ref('basic')
const saving = ref(false)
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref()
const selectedTags = ref<string[]>([])
const showRecommendationTip = ref(false)

// 跳转到我的地址
const goToAddresses = () => {
  router.push('/home/user/addresses')
}

// 表单引用
const userFormRef = ref()
const preferenceFormRef = ref()
const securityFormRef = ref()

// 用户表单
const userForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: 0,
  age: 0,
  avatar: '',
  points: 1250,
  level: 3,
  consumptionLevel: 2
})

// 用户表单验证规则
const userFormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  age: [
    { type: 'number', min: 1, max: 150, message: '年龄必须在 1 到 150 之间', trigger: 'blur' }
  ]
}

// 偏好设置表单 - 扩展字段
const preferenceForm = reactive({
  travelPreference: 1,
  frequentCities: [] as string[],
  travelMethods: [] as number[],
  seasons: [] as number[],
  duration: 1,
  regionTypes: [] as number[],
  ageRange: 1,
  jobType: 1,
  experienceLevel: 1
})

// 兴趣标签列表 - 添加图标
const interestTags = ref([
  { label: '美食', value: '美食', icon: '🍜' },
  { label: '文化', value: '文化', icon: '🎭' },
  { label: '自然', value: '自然', icon: '🏔️' },
  { label: '历史', value: '历史', icon: '🏛️' },
  { label: '艺术', value: '艺术', icon: '🎨' },
  { label: '运动', value: '运动', icon: '⚽' },
  { label: '购物', value: '购物', icon: '🛍️' },
  { label: '娱乐', value: '娱乐', icon: '🎪' },
  { label: '摄影', value: '摄影', icon: '📷' },
  { label: '音乐', value: '音乐', icon: '🎵' }
])

// 旅游偏好列表 - 添加图标和描述
const travelPreferences = ref([
  { label: '自然风光', value: 1, icon: '🏞️', description: '山水景色、自然保护区' },
  { label: '人文历史', value: 2, icon: '🏛️', description: '历史古迹、文化遗产' },
  { label: '美食体验', value: 3, icon: '🍜', description: '特色美食、地方小吃' },
  { label: '购物娱乐', value: 4, icon: '🛍️', description: '购物中心、娱乐场所' },
  { label: '休闲度假', value: 5, icon: '🏖️', description: '海滩度假、温泉放松' },
  { label: '冒险探索', value: 6, icon: '🏕️', description: '户外探险、极限运动' }
])

// 消费水平列表 - 添加图标和详细描述
const consumptionLevels = ref([
  { label: '经济型', value: 1, icon: '💰', range: '￥500-1000/天', description: '适合预算有限的旅行者，注重性价比' },
  { label: '舒适型', value: 2, icon: '💎', range: '￥1000-3000/天', description: '追求舒适体验，品质与价格平衡' },
  { label: '豪华型', value: 3, icon: '👑', range: '￥3000+/天', description: '追求高端品质，享受尊贵服务' }
])

// 出行方式列表 - 添加图标和描述
const travelMethods = ref([
  { label: '自由行', value: 1, icon: '🚶', description: '自由灵活，按自己节奏' },
  { label: '跟团游', value: 2, icon: '👥', description: '省心省力，有人安排' },
  { label: '自驾游', value: 3, icon: '🚗', description: '自由驰骋，想停就停' },
  { label: '背包客', value: 4, icon: '🎒', description: '经济实惠，深度体验' },
  { label: '商务出行', value: 5, icon: '💼', description: '公务为主，兼顾旅游' }
])

// 旅游季节偏好
const seasons = ref([
  { label: '春季', value: 1, icon: '🌸', months: '3-5月', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '夏季', value: 2, icon: '☀️', months: '6-8月', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '秋季', value: 3, icon: '🍂', months: '9-11月', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { label: '冬季', value: 4, icon: '❄️', months: '12-2月', color: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)' }
])

// 旅游时长偏好
const durations = ref([
  { label: '1-2天', value: 1, icon: '⚡' },
  { label: '3-5天', value: 2, icon: '📅' },
  { label: '6-7天', value: 3, icon: '📆' },
  { label: '8天以上', value: 4, icon: '📋' }
])

// 地区类型偏好
const regionTypes = ref([
  { label: '海滨城市', value: 1, icon: '🌊', description: '海滩、海岛、港口' },
  { label: '山区', value: 2, icon: '⛰️', description: '高山、峡谷、森林' },
  { label: '古镇古村', value: 3, icon: '🏘️', description: '古镇、古村、民俗' },
  { label: '现代都市', value: 4, icon: '🏙️', description: '摩天大楼、繁华商圈' },
  { label: '草原沙漠', value: 5, icon: '🏜️', description: '草原、沙漠、戈壁' },
  { label: '湖泊湿地', value: 6, icon: '🦆', description: '湖泊、湿地、水乡' }
])

// 年龄段
const ageRanges = ref([
  { label: '18岁以下', value: 1, icon: '👶', range: '<18岁' },
  { label: '18-25岁', value: 2, icon: '🧑', range: '18-25岁' },
  { label: '26-35岁', value: 3, icon: '👨', range: '26-35岁' },
  { label: '36-50岁', value: 4, icon: '👴', range: '36-50岁' },
  { label: '50岁以上', value: 5, icon: '🧓', range: '>50岁' }
])

// 职业类型
const jobTypes = ref([
  { label: '学生', value: 1, icon: '🎓' },
  { label: '上班族', value: 2, icon: '💼' },
  { label: '自由职业', value: 3, icon: '💻' },
  { label: '企业主', value: 4, icon: '👔' },
  { label: '退休人员', value: 5, icon: '🌺' },
  { label: '其他', value: 6, icon: '👤' }
])

// 旅游经验
const experienceLevels = ref([
  { label: '新手', value: 1, icon: '🌱', description: '很少出游，需要详细指引', color: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)' },
  { label: '进阶', value: 2, icon: '🌿', description: '有一定经验，能独立规划', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '达人', value: 3, icon: '🌳', description: '经常旅游，经验丰富', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '专家', value: 4, icon: '🏆', description: '资深旅行家，精通各地', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
])

// 偏好设置表单验证规则
const preferenceFormRules = {
  travelPreference: [
    { required: true, message: '请选择旅游偏好', trigger: 'change' }
  ]
}

// 安全设置表单
const securityForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 安全设置表单验证规则
const securityFormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== securityForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 城市列表 - 按地区分组
const cityGroups = ref([
  {
    label: '华北地区',
    cities: ['北京', '天津', '石家庄', '太原', '呼和浩特']
  },
  {
    label: '华东地区',
    cities: ['上海', '南京', '杭州', '苏州', '宁波', '合肥', '南昌', '济南', '青岛']
  },
  {
    label: '华南地区',
    cities: ['广州', '深圳', '珠海', '厦门', '福州', '海口', '三亚', '桂林', '南宁']
  },
  {
    label: '华中地区',
    cities: ['武汉', '长沙', '郑州', '南昌']
  },
  {
    label: '西南地区',
    cities: ['成都', '重庆', '昆明', '贵阳', '拉萨', '西宁']
  },
  {
    label: '西北地区',
    cities: ['西安', '兰州', '银川', '乌鲁木齐']
  },
  {
    label: '东北地区',
    cities: ['沈阳', '大连', '哈尔滨', '长春']
  }
])

const cities = ref([
  '北京', '上海', '广州', '深圳', '杭州', '成都', '重庆', '西安',
  '武汉', '南京', '天津', '苏州', '长沙', '郑州', '青岛', '厦门'
])

// 计算属性：总选中标签数
const totalSelectedTags = computed(() => {
  let count = 0
  count += selectedTags.value.length
  count += preferenceForm.travelPreference ? 1 : 0
  count += userForm.consumptionLevel ? 1 : 0
  count += (preferenceForm.travelMethods || []).length
  count += (preferenceForm.seasons || []).length
  count += preferenceForm.duration ? 1 : 0
  count += (preferenceForm.frequentCities || []).length
  count += (preferenceForm.regionTypes || []).length
  count += preferenceForm.ageRange ? 1 : 0
  count += preferenceForm.jobType ? 1 : 0
  count += preferenceForm.experienceLevel ? 1 : 0
  return count
})

// 计算属性：完成度
const completionRate = computed(() => {
  const totalFields = 11 // 总共11个字段类别
  let completedFields = 0
  
  if (selectedTags.value.length > 0) completedFields++
  if (preferenceForm.travelPreference) completedFields++
  if (userForm.consumptionLevel) completedFields++
  if ((preferenceForm.travelMethods || []).length > 0) completedFields++
  if ((preferenceForm.seasons || []).length > 0) completedFields++
  if (preferenceForm.duration) completedFields++
  if ((preferenceForm.frequentCities || []).length > 0) completedFields++
  if ((preferenceForm.regionTypes || []).length > 0) completedFields++
  if (preferenceForm.ageRange) completedFields++
  if (preferenceForm.jobType) completedFields++
  if (preferenceForm.experienceLevel) completedFields++
  
  return Math.round((completedFields / totalFields) * 100)
})

// 计算属性：进度条颜色
const progressColor = computed(() => {
  const rate = completionRate.value
  if (rate < 30) return '#f56c6c'
  if (rate < 60) return '#e6a23c'
  if (rate < 90) return '#409eff'
  return '#67c23a'
})

// 计算属性：完成度提示
const completionTip = computed(() => {
  const rate = completionRate.value
  if (rate < 30) return '标签信息较少，建议完善更多信息以获取精准推荐'
  if (rate < 60) return '标签信息一般，继续完善可以获得更好的推荐效果'
  if (rate < 90) return '标签信息较完整，推荐效果不错！'
  return '标签信息非常完整，系统将为你提供最精准的个性化推荐！'
})

// 计算属性：用户画像标签列表
const portraitTagsList = computed(() => {
  const tags: Array<{ icon: string; label: string; type: string }> = []
  
  // 兴趣标签
  selectedTags.value.forEach(tag => {
    const found = interestTags.value.find(t => t.value === tag)
    if (found) {
      tags.push({ icon: found.icon, label: found.label, type: 'success' })
    }
  })
  
  // 旅游偏好
  if (preferenceForm.travelPreference) {
    const pref = travelPreferences.value.find(p => p.value === preferenceForm.travelPreference)
    if (pref) {
      tags.push({ icon: pref.icon, label: pref.label, type: 'warning' })
    }
  }
  
  // 消费水平
  if (userForm.consumptionLevel) {
    const level = consumptionLevels.value.find(l => l.value === userForm.consumptionLevel)
    if (level) {
      tags.push({ icon: level.icon, label: level.label, type: 'danger' })
    }
  }
  
  // 出行方式
  (preferenceForm.travelMethods || []).forEach(method => {
    const found = travelMethods.value.find(m => m.value === method)
    if (found) {
      tags.push({ icon: found.icon, label: found.label, type: 'info' })
    }
  })
  
  // 旅游经验
  if (preferenceForm.experienceLevel) {
    const exp = experienceLevels.value.find(e => e.value === preferenceForm.experienceLevel)
    if (exp) {
      tags.push({ icon: exp.icon, label: exp.label, type: '' })
    }
  }
  
  return tags
})

// 计算属性：用户画像描述
const portraitDescription = computed(() => {
  const desc: any = {}
  
  // 生成画像描述
  const interests = selectedTags.value.join('、')
  const pref = travelPreferences.value.find(p => p.value === preferenceForm.travelPreference)
  const level = consumptionLevels.value.find(l => l.value === userForm.consumptionLevel)
  const exp = experienceLevels.value.find(e => e.value === preferenceForm.experienceLevel)
  const age = ageRanges.value.find(a => a.value === preferenceForm.ageRange)
  const job = jobTypes.value.find(j => j.value === preferenceForm.jobType)
  
  // 画像描述
  if (selectedTags.value.length > 0 || pref || level) {
    let intro = '你是一位'
    if (exp) intro += `${exp.label}级别的`
    if (age) intro += `${age.label.replace('岁', '')}的`
    if (job) intro += `${job.label}，`
    else intro += '旅行者，'
    
    if (selectedTags.value.length > 0) {
      intro += `对${interests}等方面有浓厚兴趣，`
    }
    if (pref) {
      intro += `偏好${pref.label}类型的旅游，`
    }
    if (level) {
      intro += `旅游预算为${level.label}。`
    }
    desc.intro = intro
  }
  
  // 旅游风格
  const methods = (preferenceForm.travelMethods || []).map(m => {
    const found = travelMethods.value.find(tm => tm.value === m)
    return found ? found.label : ''
  }).filter(Boolean)
  
  if (methods.length > 0 || pref) {
    let style = '你的旅游风格为：'
    if (methods.length > 0) {
      style += methods.join('、') + '，'
    }
    if (pref) {
      style += `喜欢${pref.description}。`
    }
    if (exp) {
      style += exp.description
    }
    desc.style = style
  }
  
  // 推荐目的地
  const cities = preferenceForm.frequentCities || []
  const regions = (preferenceForm.regionTypes || []).map(r => {
    const found = regionTypes.value.find(rt => rt.value === r)
    return found ? found.label : ''
  }).filter(Boolean)
  
  if (cities.length > 0 || regions.length > 0) {
    let rec = '根据您的偏好，推荐您前往：'
    if (regions.length > 0) {
      rec += regions.join('、') + '等地区，'
    }
    if (cities.length > 0) {
      rec += `您常去的城市有${cities.slice(0, 5).join('、')}等。`
    }
    desc.recommendation = rec
  }
  
  return desc
})

// 计算属性：雷达图数据
const radarDimensions = computed(() => {
  const dimensions: Array<{ name: string; icon: string; value: number; color: string }> = []
  
  // 自然风光
  let nature = 0
  if (selectedTags.value.includes('自然')) nature += 40
  if (preferenceForm.travelPreference === 1) nature += 40
  if ((preferenceForm.regionTypes || []).includes(2)) nature += 20
  dimensions.push({ name: '自然风光', icon: '🏔️', value: Math.min(nature, 100), color: '#67c23a' })
  
  // 人文历史
  let culture = 0
  if (selectedTags.value.includes('历史')) culture += 30
  if (selectedTags.value.includes('文化')) culture += 30
  if (preferenceForm.travelPreference === 2) culture += 40
  dimensions.push({ name: '人文历史', icon: '🏛️', value: Math.min(culture, 100), color: '#e6a23c' })
  
  // 美食体验
  let food = 0
  if (selectedTags.value.includes('美食')) food += 50
  if (preferenceForm.travelPreference === 3) food += 50
  dimensions.push({ name: '美食体验', icon: '🍜', value: Math.min(food, 100), color: '#f56c6c' })
  
  // 休闲娱乐
  let leisure = 0
  if (selectedTags.value.includes('娱乐')) leisure += 30
  if (selectedTags.value.includes('购物')) leisure += 30
  if (preferenceForm.travelPreference === 4) leisure += 40
  dimensions.push({ name: '休闲娱乐', icon: '🎪', value: Math.min(leisure, 100), color: '#409eff' })
  
  // 冒险探索
  let adventure = 0
  if (selectedTags.value.includes('运动')) adventure += 30
  if (preferenceForm.travelPreference === 6) adventure += 40
  if (preferenceForm.experienceLevel >= 3) adventure += 30
  dimensions.push({ name: '冒险探索', icon: '🏕️', value: Math.min(adventure, 100), color: '#9c27b0' })
  
  return dimensions
})

// 计算属性：匹配的旅游类型
const matchedTypes = computed(() => {
  const types: Array<{ name: string; icon: string; score: number }> = []
  
  // 海滨度假
  let beachScore = 0
  if ((preferenceForm.regionTypes || []).includes(1)) beachScore += 40
  if (preferenceForm.travelPreference === 5) beachScore += 30
  if ((preferenceForm.seasons || []).includes(2)) beachScore += 20
  if (selectedTags.value.includes('自然')) beachScore += 10
  if (beachScore > 0) {
    types.push({ name: '海滨度假', icon: '🏖️', score: Math.min(beachScore, 100) })
  }
  
  // 古城古镇
  let ancientScore = 0
  if ((preferenceForm.regionTypes || []).includes(3)) ancientScore += 40
  if (selectedTags.value.includes('历史')) ancientScore += 30
  if (selectedTags.value.includes('文化')) ancientScore += 20
  if (preferenceForm.travelPreference === 2) ancientScore += 10
  if (ancientScore > 0) {
    types.push({ name: '古城古镇', icon: '🏘️', score: Math.min(ancientScore, 100) })
  }
  
  // 山地徒步
  let mountainScore = 0
  if ((preferenceForm.regionTypes || []).includes(2)) mountainScore += 40
  if (selectedTags.value.includes('运动')) mountainScore += 30
  if (preferenceForm.travelPreference === 6) mountainScore += 20
  if (preferenceForm.experienceLevel >= 3) mountainScore += 10
  if (mountainScore > 0) {
    types.push({ name: '山地徒步', icon: '⛰️', score: Math.min(mountainScore, 100) })
  }
  
  // 都市观光
  let cityScore = 0
  if ((preferenceForm.regionTypes || []).includes(4)) cityScore += 40
  if (selectedTags.value.includes('购物')) cityScore += 20
  if (selectedTags.value.includes('娱乐')) cityScore += 20
  if (preferenceForm.travelPreference === 4) cityScore += 20
  if (cityScore > 0) {
    types.push({ name: '都市观光', icon: '🏙️', score: Math.min(cityScore, 100) })
  }
  
  // 美食之旅
  let foodScore = 0
  if (selectedTags.value.includes('美食')) foodScore += 50
  if (preferenceForm.travelPreference === 3) foodScore += 40
  if ((preferenceForm.frequentCities || []).length > 0) foodScore += 10
  if (foodScore > 0) {
    types.push({ name: '美食之旅', icon: '🍜', score: Math.min(foodScore, 100) })
  }
  
  // 按匹配度排序，取前4个
  return types.sort((a, b) => b.score - a.score).slice(0, 4)
})

// 匹配度颜色
const getMatchColor = (score: number) => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#409eff'
  if (score >= 40) return '#e6a23c'
  return '#f56c6c'
}

// 加载用户信息
const loadUserInfo = () => {
  if (userStore.userInfo) {
    Object.assign(userForm, userStore.userInfo)
    if (userStore.userInfo.interestTags) {
      selectedTags.value = userStore.userInfo.interestTags.split(',').filter(Boolean)
    }
    if (userStore.userInfo.frequentCities) {
      preferenceForm.frequentCities = userStore.userInfo.frequentCities.split(',').filter(Boolean)
    }
    preferenceForm.travelPreference = userStore.userInfo.travelPreference || 1
    
    // 初始化缺失的字段
    if (!userForm.points) userForm.points = 1250
    if (!userForm.level) userForm.level = 3
    if (!userForm.consumptionLevel) userForm.consumptionLevel = 2
  }
}

// 头像上传相关的配置
const uploadAvatarAction = `${import.meta.env.VITE_API_BASE_URL || '/api'}/user/upload-avatar`
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('travel_token')}`
}

// 头像上传成功
const handleAvatarSuccess = (response: any) => {
  console.log('头像上传响应:', response)
  
  if (response.code === 200) {
    // 后端返回的data是Map结构，包含avatar字段
    const avatarUrl = response.data?.avatar || response.data
    if (avatarUrl) {
      userForm.avatar = avatarUrl
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error('头像上传失败：未返回图片地址')
    }
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 保存基本信息
const saveBasicInfo = async () => {
  if (!userFormRef.value) return
  
  try {
    // 表单验证
    await userFormRef.value.validate()
    
    saving.value = true
    const result = await updateUserInfo({
      nickname: userForm.nickname,
      email: userForm.email,
      phone: userForm.phone,
      gender: userForm.gender,
      age: userForm.age,
      avatar: userForm.avatar
    })
    
    if (result.success) {
      ElMessage.success('保存成功')
      userStore.setUserInfo({ ...userStore.userInfo!, ...userForm })
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error: any) {
    if (error.message) {
      // 表单验证失败
      console.log('表单验证失败:', error)
    } else {
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 保存偏好设置
const savePreference = async () => {
  if (!preferenceFormRef.value) return
  
  try {
    // 表单验证
    await preferenceFormRef.value.validate()
    
    saving.value = true
    const result = await updateUserInfo({
      travelPreference: preferenceForm.travelPreference,
      interestTags: selectedTags.value.join(','),
      frequentCities: preferenceForm.frequentCities.join(',')
    })
    
    if (result.success) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error: any) {
    if (error.message) {
      // 表单验证失败
      console.log('表单验证失败:', error)
    } else {
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!securityFormRef.value) return
  
  try {
    // 表单验证
    await securityFormRef.value.validate()
    
    saving.value = true
    const result = await changePwd({
      oldPassword: securityForm.oldPassword,
      newPassword: securityForm.newPassword
    })
    
    if (result.success) {
      ElMessage.success('密码修改成功，请重新登录')
      setTimeout(async () => {
        await userStore.logout()
        window.location.href = '/'
      }, 1500)
    } else {
      ElMessage.error(result.message || '密码修改失败')
    }
  } catch (error: any) {
    if (error.message) {
      // 表单验证失败
      console.log('表单验证失败:', error)
    } else {
      ElMessage.error('密码修改失败')
    }
  } finally {
    saving.value = false
  }
}

// 重置表单
const resetForm = () => {
  loadUserInfo()
}

// 标签相关
const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value.input.focus()
  })
}

const handleTagInputConfirm = () => {
  if (tagInputValue.value && !selectedTags.value.includes(tagInputValue.value)) {
    selectedTags.value.push(tagInputValue.value)
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

const handleTagClose = (tag: string) => {
  selectedTags.value = selectedTags.value.filter(t => t !== tag)
}

// 切换标签选择
const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

// 切换出行方式
const toggleTravelMethod = (method: number) => {
  if (!preferenceForm.travelMethods) {
    preferenceForm.travelMethods = []
  }
  const index = preferenceForm.travelMethods.indexOf(method)
  if (index > -1) {
    preferenceForm.travelMethods.splice(index, 1)
  } else {
    preferenceForm.travelMethods.push(method)
  }
}

// 切换季节
const toggleSeason = (season: number) => {
  if (!preferenceForm.seasons) {
    preferenceForm.seasons = []
  }
  const index = preferenceForm.seasons.indexOf(season)
  if (index > -1) {
    preferenceForm.seasons.splice(index, 1)
  } else {
    preferenceForm.seasons.push(season)
  }
}

// 切换地区类型
const toggleRegionType = (region: number) => {
  if (!preferenceForm.regionTypes) {
    preferenceForm.regionTypes = []
  }
  const index = preferenceForm.regionTypes.indexOf(region)
  if (index > -1) {
    preferenceForm.regionTypes.splice(index, 1)
  } else {
    preferenceForm.regionTypes.push(region)
  }
}

// 移除城市
const removeCity = (city: string) => {
  if (!preferenceForm.frequentCities) return
  const index = preferenceForm.frequentCities.indexOf(city)
  if (index > -1) {
    preferenceForm.frequentCities.splice(index, 1)
  }
}

// 重置所有标签
const resetTags = () => {
  ElMessageBox.confirm('确定要重置所有标签设置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    selectedTags.value = []
    preferenceForm.travelPreference = 1
    userForm.consumptionLevel = 2
    preferenceForm.travelMethods = []
    preferenceForm.seasons = []
    preferenceForm.duration = 1
    preferenceForm.frequentCities = []
    preferenceForm.regionTypes = []
    preferenceForm.ageRange = 1
    preferenceForm.jobType = 1
    preferenceForm.experienceLevel = 1
    ElMessage.success('已重置所有标签')
  }).catch(() => {
    // 取消操作
  })
}

// 跳转到首页
const goToDashboard = () => {
  router.push('/home/user/dashboard')
  showRecommendationTip.value = false
}

// 保存标签设置
const saveTags = async () => {
  try {
    saving.value = true
    
    // 准备所有标签数据
    const tagData = {
      interestTags: selectedTags.value.join(','),
      travelPreference: preferenceForm.travelPreference,
      consumptionLevel: userForm.consumptionLevel,
      travelMethods: (preferenceForm.travelMethods || []).join(','),
      seasons: (preferenceForm.seasons || []).join(','),
      duration: preferenceForm.duration,
      frequentCities: (preferenceForm.frequentCities || []).join(','),
      regionTypes: (preferenceForm.regionTypes || []).join(','),
      ageRange: preferenceForm.ageRange,
      jobType: preferenceForm.jobType,
      experienceLevel: preferenceForm.experienceLevel
    }
    
    const result = await updateUserInfo(tagData)
    
    if (result.success) {
      ElMessage.success('标签保存成功！系统正在为你生成个性化推荐...')
      
      // 显示推荐提示
      setTimeout(() => {
        showRecommendationTip.value = true
      }, 1000)
      
      // 3秒后自动隐藏提示
      setTimeout(() => {
        showRecommendationTip.value = false
      }, 8000)
    } else {
      ElMessage.error(result.message || '标签保存失败')
    }
  } catch (error) {
    console.error('保存标签失败:', error)
    ElMessage.error('标签保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}

// 个性化用户信息卡片
.user-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 32px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
  
  // 添加装饰效果
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
    pointer-events: none;
  }
  
  .user-avatar-section {
    position: relative;
    z-index: 1;
    
    .user-avatar {
      border: 4px solid rgba(255, 255, 255, 0.3);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%);
      font-size: 48px;
      font-weight: 700;
      color: white;
      transition: all 0.3s ease;
      
      &:hover {
        transform: scale(1.05);
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
      }
    }
  }
  
  .user-info-section {
    flex: 1;
    position: relative;
    z-index: 1;
    
    .user-name {
      font-size: 28px;
      font-weight: 700;
      color: white;
      margin-bottom: 8px;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    }
    
    .user-email {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.85);
      margin-bottom: 16px;
    }
    
    .user-stats {
      display: flex;
      gap: 16px;
      
      .stat-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 16px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        font-size: 14px;
        color: white;
        font-weight: 600;
        transition: all 0.2s ease;
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
        }
        
        .el-icon {
          font-size: 16px;
        }
      }
    }
  }
  
  .user-actions {
    position: relative;
    z-index: 1;
    
    :deep(.el-button) {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      backdrop-filter: blur(10px);
      padding: 10px 24px;
      font-weight: 600;
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      
      .el-icon {
        margin-right: 6px;
      }
    }
  }
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: none;
  
  :deep(.el-card__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px 12px 0 0;
    padding: 20px 24px;
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 20px;
    font-weight: 700;
    color: white;
    
    .el-icon {
      color: white;
      font-size: 24px;
    }
  }
  
  // 美化tabs
  :deep(.el-tabs__header) {
    margin: 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  :deep(.el-tabs__nav-wrap::after) {
    background-color: #f0f0f0;
  }
  
  :deep(.el-tabs__item) {
    height: 50px;
    line-height: 50px;
    font-size: 15px;
    font-weight: 500;
    color: #606266;
    transition: all 0.2s ease;
    
    &:hover {
      color: #667eea;
    }
    
    &.is-active {
      color: #667eea;
      font-weight: 600;
    }
  }
  
  :deep(.el-tabs__active-bar) {
    background-color: #667eea;
    height: 3px;
  }
  
  :deep(.el-tabs__content) {
    padding: 24px;
  }
}

.profile-form {
  padding: 0;
  
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
  }
  
  :deep(.el-input) {
    transition: all 0.2s ease;
    
    &:hover {
      border-color: #c0c4cc;
    }
    
    &.is-focus {
      border-color: #667eea;
    }
  }
  
  :deep(.el-button--primary) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    padding: 10px 28px;
    font-weight: 600;
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }
  
  :deep(.el-button) {
    border-radius: 8px;
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-1px);
    }
  }
}

.security-panel {
  background: #ffffff;
  border: 1px solid #edf0f4;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  gap: 24px;
  color: #3c4048;
}

.security-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.security-panel__title {
  span {
    display: block;
    font-size: 12px;
    letter-spacing: 0.32em;
    text-transform: uppercase;
    color: #b5b9c2;
    margin-bottom: 6px;
  }
  
  h3 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #2f3135;
  }
}

.security-panel__badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid #eaecf2;
  background: #fbfbfd;
  color: #7b818b;
  font-size: 13px;
  
  .badge-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #b6bcc7;
  }
}

.security-panel__desc {
  margin: 0;
  color: #7d818b;
  font-size: 14px;
  line-height: 1.6;
}

.security-form {
  padding: 0;
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
  
  :deep(.el-input__wrapper) {
    background: #f8f9fb;
    border-radius: 12px;
    border: 1px solid transparent;
    transition: border-color 0.2s ease;
    
    &:hover {
      border-color: #d6dbe5;
    }
    
    &.is-focus {
      border-color: #b8beca;
      box-shadow: none;
    }
  }
  
  :deep(.el-form-item__label) {
    color: #9a9fab;
    font-size: 13px;
  }
}

.security-form__fields {
  display: grid;
  grid-template-columns: 1fr;
  gap: 18px;
}

.security-form__actions {
  display: flex;
  justify-content: flex-end;
  
  :deep(.el-button--primary) {
    background: #2f3135;
    border-color: #2f3135;
    padding: 12px 32px;
    font-weight: 600;
    letter-spacing: 0.05em;
    
    &:hover {
      background: #1f2023;
      border-color: #1f2023;
    }
  }
}

.security-panel__note {
  border-radius: 14px;
  border: 1px dashed #e0e2e8;
  padding: 18px 20px;
  background: #f9fafc;
  color: #7b818b;
  font-size: 13px;
  line-height: 1.6;
  
  span {
    display: inline-block;
    font-weight: 600;
    color: #5a5f68;
    margin-bottom: 4px;
  }
  
  p {
    margin: 0;
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 2px dashed #d9d9d9;
    border-radius: 12px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s ease;
    background: #fafafa;
    
    &:hover {
      border-color: #667eea;
      background: #f5f7fa;
      transform: scale(1.02);
    }
  }
  
  .avatar-uploader-icon {
    font-size: 32px;
    color: #c0c4cc;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
    transition: all 0.3s ease;
  }
  
  :deep(.el-upload:hover .avatar-uploader-icon) {
    color: #667eea;
    transform: scale(1.1);
  }
  
  .avatar {
    width: 120px;
    height: 120px;
    display: block;
    border-radius: 12px;
    object-fit: cover;
  }
}

// 美化标签
:deep(.el-tag) {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  font-weight: 500;
  padding: 4px 12px;
  
  .el-tag__close {
    color: #667eea;
    
    &:hover {
      background-color: rgba(102, 126, 234, 0.2);
    }
  }
}

// 美化单选按钮
:deep(.el-radio) {
  margin-right: 20px;
  
  .el-radio__label {
    color: #606266;
    font-weight: 500;
  }
  
  .el-radio__input.is-checked .el-radio__inner {
    background-color: #667eea;
    border-color: #667eea;
  }
}

// 美化下拉框
:deep(.el-select) {
  .el-input__inner:focus {
    border-color: #667eea;
  }
}

// 个性化标签容器 - 全新设计
.personalized-tags-container {
  margin-bottom: 24px;
}

// 用户画像卡片 - 新增
.user-portrait-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  
  :deep(.el-card__body) {
    padding: 0;
  }
  
  .portrait-header {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 24px 32px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    .portrait-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .portrait-title {
      h3 {
        margin: 0 0 4px 0;
        font-size: 22px;
        font-weight: 700;
      }
      
      p {
        margin: 0;
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }
  
  .portrait-empty {
    padding: 80px 32px;
    background: white;
    text-align: center;
    
    .empty-icon {
      font-size: 80px;
      color: #c0c4cc;
      margin-bottom: 20px;
      
      .el-icon {
        animation: float 3s ease-in-out infinite;
      }
    }
    
    h3 {
      margin: 0 0 12px 0;
      font-size: 20px;
      font-weight: 700;
      color: #303133;
    }
    
    p {
      margin: 0 0 24px 0;
      font-size: 14px;
      color: #909399;
      line-height: 1.6;
    }
    
    .el-button {
      padding: 12px 32px;
      font-size: 15px;
      font-weight: 600;
      
      .el-icon {
        margin-right: 6px;
      }
    }
  }
  
  .portrait-content {
    padding: 32px;
    background: white;
  }
  
  // 画像标签云
  .portrait-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 32px;
    padding: 24px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
    border-radius: 12px;
    
    .portrait-tag {
      font-size: 14px;
      padding: 8px 16px;
      border-radius: 20px;
      font-weight: 600;
      transition: all 0.3s ease;
      
      .tag-emoji {
        margin-right: 4px;
        font-size: 16px;
      }
      
      &:hover {
        transform: translateY(-2px) scale(1.05);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
  }
  
  // 画像描述
  .portrait-description {
    margin-bottom: 32px;
    
    .description-item {
      display: flex;
      gap: 16px;
      padding: 20px;
      margin-bottom: 16px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
      border-radius: 12px;
      border-left: 4px solid #667eea;
      transition: all 0.3s ease;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      &:hover {
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        transform: translateX(4px);
        box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
      }
      
      .item-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        flex-shrink: 0;
      }
      
      .item-content {
        flex: 1;
        
        h4 {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 700;
          color: #303133;
        }
        
        p {
          margin: 0;
          font-size: 14px;
          color: #606266;
          line-height: 1.8;
        }
      }
    }
  }
  
  // 雷达图
  .portrait-radar {
    margin-bottom: 32px;
    padding: 24px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
    border-radius: 12px;
    
    .radar-title {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: 700;
      color: #303133;
      
      .el-icon {
        font-size: 20px;
        color: #667eea;
      }
    }
    
    .radar-items {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    
    .radar-item {
      .dimension-name {
        font-size: 14px;
        font-weight: 600;
        color: #606266;
        margin-bottom: 8px;
      }
    }
  }
  
  // 匹配度展示
  .portrait-match {
    .match-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: 700;
      color: #303133;
      
      .el-icon {
        font-size: 20px;
        color: #67c23a;
      }
    }
    
    .match-items {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }
    
    .match-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 12px;
      border: 2px solid #e4e7ed;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: #667eea;
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
      }
      
      .match-icon {
        font-size: 40px;
        flex-shrink: 0;
      }
      
      .match-info {
        flex: 1;
        
        .match-name {
          font-size: 16px;
          font-weight: 700;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .match-score {
          display: flex;
          align-items: baseline;
          gap: 4px;
          
          .score-value {
            font-size: 20px;
            font-weight: 700;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }
          
          .score-label {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }
}

// 标签统计概览卡片
.tags-overview-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  overflow: hidden;
  
  .overview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .header-icon {
        font-size: 48px;
        opacity: 0.9;
      }
      
      .header-info {
        h3 {
          margin: 0 0 4px 0;
          font-size: 20px;
          font-weight: 700;
        }
        
        p {
          margin: 0;
          font-size: 14px;
          opacity: 0.9;
        }
      }
    }
    
    .header-stats {
      display: flex;
      gap: 32px;
      
      .stat-item {
        text-align: center;
        
        .stat-label {
          display: block;
          font-size: 12px;
          opacity: 0.8;
          margin-bottom: 4px;
        }
        
        .stat-value {
          display: block;
          font-size: 32px;
          font-weight: 700;
        }
      }
    }
  }
  
  .completion-progress {
    padding: 24px;
    background: white;
    
    .percentage-text {
      font-size: 14px;
      font-weight: 600;
      color: #667eea;
    }
    
    .progress-tip {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 12px 0 0 0;
      font-size: 14px;
      color: #909399;
      
      .el-icon {
        font-size: 16px;
        color: #409eff;
      }
    }
  }
}

// 标签详情卡片
.tags-detail-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  
  :deep(.el-card__body) {
    padding: 0;
  }
  
  .tags-tabs {
    :deep(.el-tabs__header) {
      margin: 0;
      padding: 0 24px;
      background: #fafafa;
    }
    
    :deep(.el-tabs__nav-wrap::after) {
      height: 1px;
      background: #e4e7ed;
    }
    
    :deep(.el-tabs__item) {
      height: 56px;
      line-height: 56px;
      font-size: 15px;
      font-weight: 500;
      color: #606266;
      
      &:hover {
        color: #667eea;
      }
      
      &.is-active {
        color: #667eea;
        font-weight: 600;
      }
    }
    
    :deep(.el-tabs__active-bar) {
      height: 3px;
      background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    }
    
    :deep(.el-tabs__content) {
      padding: 0;
    }
  }
  
  .tab-content {
    padding: 32px 24px;
    min-height: 400px;
    background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  }
  
  .tag-group {
    margin-bottom: 48px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .group-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
      
      .group-title {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .title-icon {
          font-size: 20px;
          color: #667eea;
        }
        
        span {
          font-size: 17px;
          font-weight: 700;
          color: #303133;
        }
        
        .el-tag {
          margin-left: 8px;
        }
      }
      
      .selected-count {
        font-size: 14px;
        color: #909399;
        font-weight: 500;
      }
    }
    
    .group-description {
      margin: 0 0 20px 0;
      font-size: 13px;
      color: #909399;
      line-height: 1.6;
    }
  }
  
  .tags-actions {
    padding: 20px 24px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    gap: 12px;
    background: #fafafa;
    
    :deep(.el-button) {
      padding: 12px 32px;
      font-weight: 600;
      font-size: 15px;
      border-radius: 8px;
      
      &:not(.el-button--primary) {
        border-color: #dcdfe6;
        
        &:hover {
          color: #667eea;
          border-color: #667eea;
          background: #f5f7fa;
        }
      }
      
      &.el-button--primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
        }
      }
    }
  }
}

// 标签网格
.tag-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
  
  .tag-item {
    position: relative;
    padding: 24px 20px;
    border: 2px solid #e4e7ed;
    border-radius: 16px;
    background: white;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    
    &:hover {
      transform: translateY(-6px) scale(1.02);
      border-color: #667eea;
      box-shadow: 0 12px 32px rgba(102, 126, 234, 0.2);
      
      .tag-icon {
        transform: scale(1.15) rotate(5deg);
      }
    }
    
    &.is-selected {
      border-color: #667eea;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
      
      .tag-check {
        opacity: 1;
        transform: scale(1) rotate(0deg);
      }
      
      .tag-icon {
        animation: bounce 0.6s ease;
      }
    }
    
    .tag-icon {
      font-size: 40px;
      margin-bottom: 12px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      display: inline-block;
    }
    
    .tag-name {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .tag-description {
      font-size: 12px;
      color: #909399;
      line-height: 1.4;
    }
    
    .tag-check {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 14px;
      opacity: 0;
      transform: scale(0);
      transition: all 0.3s ease;
    }
  }
  
  .preference-item {
    grid-column: span 2;
    
    .tag-description {
      margin-top: 4px;
    }
  }
}

// 消费水平选择器
.consumption-selector {
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .consumption-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    border: 2px solid #e4e7ed;
    border-radius: 12px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    
    &:hover {
      transform: translateX(4px);
      border-color: #667eea;
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
    }
    
    &.is-selected {
      border-color: #667eea;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
      
      .consumption-check {
        opacity: 1;
        transform: scale(1);
      }
    }
    
    .consumption-icon {
      font-size: 40px;
      flex-shrink: 0;
    }
    
    .consumption-info {
      flex: 1;
      
      .consumption-name {
        font-size: 16px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .consumption-range {
        font-size: 14px;
        color: #667eea;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .consumption-desc {
        font-size: 13px;
        color: #909399;
        line-height: 1.5;
      }
    }
    
    .consumption-check {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 16px;
      opacity: 0;
      transform: scale(0);
      transition: all 0.3s ease;
      flex-shrink: 0;
    }
  }
}

// 季节网格
.season-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  
  .season-item {
    position: relative;
    padding: 24px 16px;
    border: 2px solid #e4e7ed;
    border-radius: 12px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }
    
    &.is-selected {
      border-color: transparent;
      
      .season-check {
        opacity: 1;
        transform: scale(1);
      }
    }
    
    .season-icon {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
      margin: 0 auto 12px;
    }
    
    .season-name {
      font-size: 16px;
      font-weight: 700;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .season-months {
      font-size: 13px;
      color: #909399;
    }
    
    .season-check {
      position: absolute;
      top: 12px;
      right: 12px;
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: white;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #67c23a;
      font-size: 14px;
      font-weight: 700;
      opacity: 0;
      transform: scale(0);
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    }
  }
}

// 时长选择器
.duration-selector {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  
  .duration-item {
    padding: 20px;
    border: 2px solid #e4e7ed;
    border-radius: 12px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
    
    &:hover {
      transform: translateY(-4px);
      border-color: #667eea;
      box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
    }
    
    &.is-selected {
      border-color: #667eea;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    }
    
    .duration-icon {
      font-size: 32px;
      margin-bottom: 8px;
    }
    
    .duration-label {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
    }
  }
}

// 城市选择器
.city-selector {
  margin-bottom: 16px;
  
  .city-select {
    width: 100%;
    
    :deep(.el-input__wrapper) {
      padding: 12px 16px;
      border-radius: 8px;
      
      &:hover,
      &.is-focus {
        border-color: #667eea;
      }
    }
  }
}

.selected-cities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  
  .city-tag {
    font-size: 14px;
    padding: 6px 12px;
    border-radius: 16px;
    background: white;
    border: 1px solid #e4e7ed;
    
    &:hover {
      border-color: #667eea;
      background: rgba(102, 126, 234, 0.1);
    }
  }
}

// 年龄选择器
.age-selector {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  
  .age-item {
    padding: 20px;
    border: 2px solid #e4e7ed;
    border-radius: 12px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
    
    &:hover {
      transform: translateY(-4px);
      border-color: #667eea;
      box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
    }
    
    &.is-selected {
      border-color: #667eea;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    }
    
    .age-icon {
      font-size: 32px;
      margin-bottom: 8px;
    }
    
    .age-label {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .age-range {
      font-size: 12px;
      color: #909399;
    }
  }
}

// 经验选择器
.experience-selector {
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .experience-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    border: 2px solid #e4e7ed;
    border-radius: 12px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    
    &:hover {
      transform: translateX(4px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }
    
    &.is-selected {
      border-color: transparent;
      
      .experience-check {
        opacity: 1;
        transform: scale(1);
      }
    }
    
    .experience-icon {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      flex-shrink: 0;
    }
    
    .experience-info {
      flex: 1;
      
      .experience-name {
        font-size: 16px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .experience-desc {
        font-size: 13px;
        color: #909399;
        line-height: 1.5;
      }
    }
    
    .experience-check {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: #67c23a;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 16px;
      opacity: 0;
      transform: scale(0);
      transition: all 0.3s ease;
      flex-shrink: 0;
    }
  }
}

// 推荐提示卡片
.recommendation-tip-card {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  margin-bottom: 24px;
  
  :deep(.el-card__body) {
    padding: 0;
  }
  
  .tip-content {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 24px;
    color: white;
    
    .tip-icon {
      font-size: 48px;
      opacity: 0.9;
      animation: float 2s ease-in-out infinite;
    }
    
    .tip-text {
      flex: 1;
      
      h4 {
        margin: 0 0 8px 0;
        font-size: 18px;
        font-weight: 700;
      }
      
      p {
        margin: 0;
        font-size: 14px;
        opacity: 0.9;
        line-height: 1.6;
      }
    }
    
    :deep(.el-button) {
      background: white;
      color: #667eea;
      border: none;
      font-weight: 600;
      padding: 12px 24px;
      
      &:hover {
        transform: translateX(4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.15);
  }
  50% {
    transform: scale(0.95);
  }
  75% {
    transform: scale(1.05);
  }
}
</style>

