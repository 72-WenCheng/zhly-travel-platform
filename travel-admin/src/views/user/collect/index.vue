<template>
  <div class="user-collect">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="icon-wrapper">
          <el-icon :size="48"><Star /></el-icon>
        </div>
        <div class="header-text">
          <h2>我的收藏</h2>
          <p>管理你收藏的景点和攻略</p>
        </div>
      </div>
    </div>

    <!-- 收藏统计 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><StarFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.totalCount }}</div>
              <div class="stats-label">总收藏数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><LocationFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.typeCounts.type2 }}</div>
              <div class="stats-label">景点收藏</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><Files /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.typeCounts.type1 }}</div>
              <div class="stats-label">攻略收藏</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="32" style="color: #374151;"><Shop /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.typeCounts.type3 }}</div>
              <div class="stats-label">文旅项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline class="filter-form">
        <el-form-item>
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索收藏内容" 
            clearable 
            style="width: 300px;"
            @change="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select 
            ref="collectTypeSelectRef"
            v-model="searchForm.collectType" 
            placeholder="收藏类型" 
            clearable
            style="width: 200px;"
            class="collect-type-select"
            @change="handleSearch"
            @visible-change="handleSelectVisibleChange"
          >
            <el-option label="景点" :value="2">
              <el-icon><Location /></el-icon> 景点
            </el-option>
            <el-option label="攻略" :value="1">
              <el-icon><Document /></el-icon> 攻略
            </el-option>
            <el-option label="文旅项目" :value="3">
              <el-icon><OfficeBuilding /></el-icon> 文旅项目
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 收藏列表 -->
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="item in collectList" :key="item.id">
        <el-card class="collect-card" shadow="never">
          <div class="card-cover" @click="handleView(item)">
            <img 
              v-if="item.collectImage" 
              :src="item.collectImage" 
              :alt="item.collectName"
            />
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
            <div 
              class="type-badge"
              :style="getBadgeStyle(item.collectType)"
            >
              <el-icon v-if="item.collectType === 1"><Document /></el-icon>
              <el-icon v-else-if="item.collectType === 2"><Location /></el-icon>
              <el-icon v-else><OfficeBuilding /></el-icon>
              {{ getTypeText(item.collectType) }}
            </div>
          </div>
          <div class="card-body">
            <h3 class="card-title" @click="handleView(item)">{{ item.collectName }}</h3>
            <div class="card-meta">
              <span class="meta-item" v-if="item.city">
                <el-icon><Location /></el-icon>
                {{ item.city }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(item.createTime) }}
              </span>
              <span class="meta-item" v-if="item.viewCount !== undefined">
                <el-icon><View /></el-icon>
                {{ item.viewCount }}
              </span>
            </div>
            <div class="card-actions">
              <el-button class="action-btn" type="primary" @click.stop="handleView(item)">
                查看
              </el-button>
              <el-button class="action-btn" type="danger" @click.stop="handleDelete(item)">
                取消收藏
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty 
      v-if="!loading && collectList.length === 0" 
      description="暂无收藏"
      :image-size="120"
    >
      <el-button type="primary" @click="$router.push('/home/user/recommendations')">
        去发现景点
      </el-button>
    </el-empty>

    <!-- 加载更多提示 -->
    <div class="load-more-wrapper" v-if="collectList.length > 0">
      <div v-if="loadingMore" class="loading-more">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <div v-else-if="hasMore" class="load-more-tip">
        <el-icon><ArrowDown /></el-icon>
        <span>继续下拉加载更多</span>
      </div>
      <div v-else-if="collectList.length > 0" class="no-more">
        <el-icon><Check /></el-icon>
        <span>已加载全部内容</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Delete, Star, StarFilled, LocationFilled, 
  Files, Shop, Search, Picture, Clock, View, Loading, ArrowDown, Check
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { getCurrentUserInfo, getCurrentUserId } from '@/utils/user'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  collectType: undefined as number | undefined,
  keyword: ''
})

// el-select ref
const collectTypeSelectRef = ref()

// 强制应用黑色边框样式
const applyBlackBorderStyle = () => {
  nextTick(() => {
    if (collectTypeSelectRef.value) {
      const selectEl = (collectTypeSelectRef.value as any).$el || collectTypeSelectRef.value
      if (selectEl) {
        const inputWrapper = selectEl.querySelector?.('.el-input__wrapper') as HTMLElement
        if (inputWrapper) {
          // 添加自定义类
          inputWrapper.classList.add('custom-black-focus')
          
          // 直接应用样式
          const applyStyle = () => {
            inputWrapper.style.setProperty('--el-input-focus-border-color', '#303133', 'important')
            inputWrapper.style.setProperty('--el-border-color', '#303133', 'important')
            inputWrapper.style.setProperty('--el-color-primary', '#303133', 'important')
            inputWrapper.style.setProperty('border-color', '#303133', 'important')
            inputWrapper.style.setProperty('box-shadow', '0 0 0 1px #303133 inset', 'important')
            inputWrapper.style.setProperty('outline', 'none', 'important')
          }
          
          // 立即应用一次
          applyStyle()
          
          // 使用MutationObserver监听类变化
          const observer = new MutationObserver(() => {
            if (inputWrapper.classList.contains('is-focus')) {
              applyStyle()
            }
          })
          
          observer.observe(inputWrapper, {
            attributes: true,
            attributeFilter: ['class']
          })
          
          // 监听focus事件
          inputWrapper.addEventListener('focus', applyStyle, true)
          inputWrapper.addEventListener('focusin', applyStyle, true)
          
          // 定期检查并应用（作为后备方案）
          const checkInterval = setInterval(() => {
            if (inputWrapper.classList.contains('is-focus')) {
              applyStyle()
            }
          }, 100)
          
          // 5秒后清除定期检查（避免内存泄漏）
          setTimeout(() => {
            clearInterval(checkInterval)
          }, 5000)
        }
      }
    }
  })
}

// 选择框显示/隐藏变化
const handleSelectVisibleChange = (visible: boolean) => {
  if (visible) {
    applyBlackBorderStyle()
  }
}

// 收藏列表
const collectList = ref<any[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const pageSize = ref(6) // 每次加载的数量（减小以便验证滚动加载）
const currentPage = ref(1)

// 统计信息（初始值为0，等待API加载）
const stats = ref({
  totalCount: 0,
  typeCounts: {
    type1: 0,
    type2: 0,
    type3: 0
  }
})

// 获取类型文本（收藏类型）
const getTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '攻略',
    2: '景点',
    3: '文旅项目'
  }
  return typeMap[type] || '未知'
}

// 获取标签样式（柔和色块，简单又有区分度）
const getBadgeStyle = (type: number) => {
  const styleMap: Record<number, string> = {
    // 攻略：蓝色系
    1: 'background: rgba(59, 130, 246, 0.12); border: 1px solid rgba(59, 130, 246, 0.35); color: #1d4ed8;',
    // 景点：橙色系
    2: 'background: rgba(249, 115, 22, 0.12); border: 1px solid rgba(249, 115, 22, 0.35); color: #c05621;',
    // 文旅项目：绿色系
    3: 'background: rgba(16, 185, 129, 0.12); border: 1px solid rgba(16, 185, 129, 0.35); color: #047857;'
  }
  // 默认使用蓝色
  return styleMap[type] || styleMap[1]
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  collectList.value = []
  hasMore.value = true
  loadCollectList(true)
}

// 加载收藏列表
const loadCollectList = async (reset: boolean = false) => {
  if (reset) {
    loading.value = true
    currentPage.value = 1
    collectList.value = []
    hasMore.value = true
  } else {
    if (loadingMore.value || !hasMore.value) return
    loadingMore.value = true
  }
  
  try {
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    if (!userInfo) {
      ElMessage.warning('请先登录')
      if (reset) loading.value = false
      else loadingMore.value = false
      return
    }
    
    const userId = userInfo.id || userInfo.userId
    
    if (!userId) {
      ElMessage.warning('用户信息获取失败')
      if (reset) loading.value = false
      else loadingMore.value = false
      return
    }
    
    // 调用API获取收藏列表
    console.log('🔍 开始加载收藏列表，用户ID:', userId, '页码:', currentPage.value, '每页数量:', pageSize.value, '重置:', reset)
    const requestParams: any = {
      userId: userId,
      page: currentPage.value,
      size: pageSize.value
    }
    if (searchForm.collectType !== undefined) {
      requestParams.collectType = searchForm.collectType
    }
    console.log('🔍 请求参数:', requestParams)
    
    const response = await request.get('/user-collect/list', {
      params: requestParams
    })
    
    console.log('📡 API响应:', response)
    
    if (response && response.code === 200 && response.data) {
      let collects = response.data.list || []
      
      console.log('✅ 收藏列表API返回数据（原始）:', JSON.stringify(collects, null, 2))
      console.log('✅ 收藏总数:', response.data.total)
      
      // 确保数据格式正确，并获取图片
      collects = await Promise.all(collects.map(async (item: any) => {
        // 确保createTime是字符串格式
        if (item.createTime && typeof item.createTime === 'object') {
          item.createTime = item.createTime.toString()
        }
        
        // 如果没有图片，根据收藏类型获取图片
        if (!item.collectImage && item.collectId) {
          try {
            let imageUrl = ''
            
            if (item.collectType === 1) {
              // 攻略：获取攻略封面图
              const planResponse = await request.get(`/travel-plan/${item.collectId}`)
              if (planResponse.code === 200 && planResponse.data) {
                const plan = planResponse.data
                // 优先使用coverImage，其次使用images的第一张
                if (plan.coverImage) {
                  imageUrl = plan.coverImage
                } else if (plan.images) {
                  if (Array.isArray(plan.images) && plan.images.length > 0) {
                    imageUrl = plan.images[0]
                  } else if (typeof plan.images === 'string') {
                    const images = plan.images.split(',').filter((img: string) => img.trim())
                    if (images.length > 0) {
                      imageUrl = images[0].trim().replace(/["']/g, '')
                    }
                  }
                }
              }
            } else if (item.collectType === 2) {
              // 景点：获取景点图片
              const attractionResponse = await request.get(`/user/attraction/detail/${item.collectId}`)
              if (attractionResponse.code === 200 && attractionResponse.data) {
                const attraction = attractionResponse.data
                // 优先使用images数组的第一张，其次使用coverImage，最后使用image
                if (attraction.images) {
                  if (Array.isArray(attraction.images) && attraction.images.length > 0) {
                    imageUrl = attraction.images[0]
                  } else if (typeof attraction.images === 'string') {
                    const images = attraction.images.split(',').filter((img: string) => img.trim())
                    if (images.length > 0) {
                      imageUrl = images[0].trim().replace(/["']/g, '')
                    }
                  }
                }
                if (!imageUrl && attraction.coverImage) {
                  imageUrl = attraction.coverImage
                }
                if (!imageUrl && attraction.image) {
                  imageUrl = attraction.image
                }
              }
            } else if (item.collectType === 3) {
              // 文旅项目：获取文旅项目图片
              const cultureResponse = await request.get(`/culture-project/${item.collectId}`)
              if (cultureResponse.code === 200 && cultureResponse.data) {
                const culture = cultureResponse.data
                if (culture.image) {
                  imageUrl = culture.image
                } else if (culture.coverImage) {
                  imageUrl = culture.coverImage
                } else if (culture.images) {
                  if (Array.isArray(culture.images) && culture.images.length > 0) {
                    imageUrl = culture.images[0]
                  }
                }
              }
            }
            
            if (imageUrl) {
              item.collectImage = imageUrl
            }
          } catch (error) {
            console.warn(`获取收藏项 ${item.id} 的图片失败:`, error)
          }
        }
        
        return item
      }))
      
      // 关键词筛选
      if (searchForm.keyword) {
        collects = collects.filter((item: any) => 
          item.collectName && item.collectName.includes(searchForm.keyword)
        )
      }
      
      // 追加或替换数据
      if (reset) {
        collectList.value = collects
        // 重置时，如果加载的数据等于pageSize，说明可能还有更多
        hasMore.value = collects.length >= pageSize.value
        // 更新页码（只有在还有更多数据时才更新）
        if (hasMore.value && collects.length > 0) {
          currentPage.value = 2 // 下次加载第2页
        } else {
          currentPage.value = 1
        }
      } else {
        collectList.value = [...collectList.value, ...collects]
        // 判断是否还有更多数据
        const total = response.data.total || 0
        hasMore.value = collectList.value.length < total
        
        // 如果本次加载的数据少于pageSize，说明没有更多了
        if (collects.length < pageSize.value) {
          hasMore.value = false
        }
        
        // 更新页码（只有在还有更多数据时才更新）
        if (hasMore.value && collects.length > 0) {
          currentPage.value++
        }
      }
      
      const total = response.data.total || 0
      console.log('✅ 收藏列表加载成功，当前', collectList.value.length, '条，总共', total, '条，还有更多:', hasMore.value, '当前页码:', currentPage.value)
      
      if (collects.length === 0 && reset) {
        console.log('ℹ️ 当前用户没有收藏记录')
      }
    } else {
      console.error('❌ 收藏列表API返回错误:', response)
      ElMessage.error(response?.message || '加载收藏列表失败')
      // 清空列表，不显示任何数据
      if (reset) {
        collectList.value = []
      }
      hasMore.value = false
    }
    
  } catch (error: any) {
    console.error('❌ 加载收藏列表异常:', error)
    console.error('错误详情:', error.response || error)
    
    // 如果是网络错误或API错误，显示更详细的提示
    if (error.response) {
      console.error('API响应:', error.response.data)
      if (reset) {
        ElMessage.error('加载收藏列表失败: ' + (error.response.data?.message || error.message || '未知错误'))
      }
    } else {
      if (reset) {
        ElMessage.error('加载收藏列表失败: ' + (error.message || '网络错误，请检查后端服务是否正常运行'))
      }
    }
    
    if (reset) {
      collectList.value = []
    }
    hasMore.value = false
  } finally {
    if (reset) {
      loading.value = false
    } else {
      loadingMore.value = false
    }
  }
}

// 获取滚动容器
const getScrollContainer = () => {
  // 查找 el-main 容器（Element Plus 布局的滚动容器）
  return document.querySelector('.el-main') || document.querySelector('.main-content') || window
}

// 滚动监听
const handleScroll = (event?: Event) => {
  // 获取滚动容器
  const scrollContainer = getScrollContainer()
  
  let scrollTop = 0
  let containerHeight = 0
  let scrollHeight = 0
  
  if (scrollContainer === window) {
    // window 滚动
    scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0
    containerHeight = window.innerHeight || document.documentElement.clientHeight
    scrollHeight = Math.max(
      document.body.scrollHeight,
      document.body.offsetHeight,
      document.documentElement.clientHeight,
      document.documentElement.scrollHeight,
      document.documentElement.offsetHeight
    )
  } else {
    // 容器滚动
    const container = scrollContainer as HTMLElement
    scrollTop = container.scrollTop || 0
    containerHeight = container.clientHeight || 0
    scrollHeight = container.scrollHeight || 0
  }
  
  // 当滚动到距离底部50px时加载更多（减小阈值，更容易触发）
  const threshold = 50
  const isNearBottom = scrollTop + containerHeight >= scrollHeight - threshold
  
  console.log('滚动监听:', {
    scrollTop,
    containerHeight,
    scrollHeight,
    isNearBottom,
    hasMore: hasMore.value,
    loadingMore: loadingMore.value,
    loading: loading.value,
    currentCount: collectList.value.length
  })
  
  if (isNearBottom) {
    if (hasMore.value && !loadingMore.value && !loading.value) {
      console.log('✅ 触发加载更多')
      loadCollectList(false)
    } else {
      console.log('❌ 不满足加载条件:', {
        hasMore: hasMore.value,
        loadingMore: loadingMore.value,
        loading: loading.value
      })
    }
  }
}

// 样式应用定时器
let styleInterval: NodeJS.Timeout | null = null

// 组件挂载时添加滚动监听
onMounted(() => {
  loadCollectList(true)
  loadStats()
  
  // 应用黑色边框样式
  applyBlackBorderStyle()
  
  // 延迟添加滚动监听，确保 DOM 已渲染
  setTimeout(() => {
    const scrollContainer = getScrollContainer()
    if (scrollContainer === window) {
      window.addEventListener('scroll', handleScroll, { passive: true })
    } else {
      (scrollContainer as HTMLElement).addEventListener('scroll', handleScroll, { passive: true })
    }
    
    // 再次应用样式，确保生效
    applyBlackBorderStyle()
  }, 100)
  
  // 定期检查并应用样式（防止动态渲染导致失效）
  styleInterval = setInterval(() => {
    applyBlackBorderStyle()
  }, 500)
})

// 组件卸载时移除滚动监听
onUnmounted(() => {
  const scrollContainer = getScrollContainer()
  if (scrollContainer === window) {
    window.removeEventListener('scroll', handleScroll)
  } else {
    (scrollContainer as HTMLElement).removeEventListener('scroll', handleScroll)
  }
  
  // 清除样式应用定时器
  if (styleInterval) {
    clearInterval(styleInterval)
    styleInterval = null
  }
})

// 加载统计信息
const loadStats = async () => {
  try {
    console.log('🔍 开始加载收藏统计数据...')
    
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    if (!userInfo) {
      console.warn('⚠️ 未获取到用户信息，无法加载统计')
      return
    }
    
    const userId = userInfo.id || userInfo.userId
    
    if (!userId) {
      console.warn('⚠️ 未获取到用户ID，无法加载统计')
      return
    }
    
    console.log('✅ 用户ID:', userId)
    
    // 先尝试从收藏列表计算统计数据（更可靠）
    try {
      const listResponse = await request.get('/user-collect/list', {
        params: {
          userId: userId,
          page: 1,
          size: 10000  // 获取所有收藏以计算统计
        }
      })
      
      if (listResponse.code === 200 && listResponse.data) {
        const collects = listResponse.data.list || []
        const totalCount = listResponse.data.total || collects.length
        
        // 按类型统计
        const typeCounts = {
          type1: 0,  // 攻略
          type2: 0,  // 景点
          type3: 0   // 文旅项目
        }
        
        collects.forEach((item: any) => {
          if (item.collectType === 1) {
            typeCounts.type1++
          } else if (item.collectType === 2) {
            typeCounts.type2++
          } else if (item.collectType === 3) {
            typeCounts.type3++
          }
        })
        
        // 更新统计数据
        stats.value = {
          totalCount: totalCount,
          typeCounts: typeCounts
        }
        
        console.log('✅ 从收藏列表计算统计成功:', stats.value)
        return
      }
    } catch (listError) {
      console.warn('⚠️ 从收藏列表计算统计失败，尝试使用统计API:', listError)
    }
    
    // 如果列表API失败，尝试使用统计API
    const response = await request.get(`/user-collect/stats/${userId}`)
    
    console.log('📊 收藏统计API返回:', response)
    
    if (response.code === 200 && response.data) {
      const data = response.data
      stats.value = {
        totalCount: data.totalCount || 0,
        typeCounts: {
          type1: data.typeCounts?.type1 || 0,  // 攻略
          type2: data.typeCounts?.type2 || 0,  // 景点
          type3: data.typeCounts?.type3 || 0   // 文旅项目
        }
      }
      console.log('✅ 收藏统计加载成功（从统计API）:', stats.value)
    } else {
      console.warn('⚠️ 收藏统计API返回错误:', response)
    }
  } catch (error: any) {
    console.error('❌ 加载统计信息失败:', error)
    console.error('错误详情:', error.response || error)
  }
}

// 查看收藏
const handleView = (item: any) => {
  if (item.collectType === 1 && item.collectUrl) {
    window.open(item.collectUrl, '_blank', 'noopener,noreferrer')
    return
  }

  const routeMap: Record<number, string> = {
    1: '/home/user/plans/detail',
    2: '/home/user/attractions/detail',
    3: '/home/user/culture/detail'
  }
  
  const route = routeMap[item.collectType]
  if (route) {
    router.push(`${route}/${item.collectId}`)
  }
}

// 删除收藏
const handleDelete = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 获取当前标签页的用户信息
    const userInfo = getCurrentUserInfo()
    if (!userInfo) {
      ElMessage.warning('请先登录')
      return
    }
    
    const userId = userInfo.id || userInfo.userId
    
    if (!userId) {
      ElMessage.warning('用户信息获取失败')
      return
    }
    
    // 调用API取消收藏
    const response = await request.post('/user-collect/remove', null, {
      params: {
        userId: userId,
        collectType: item.collectType,
        collectId: item.collectId
      }
    })
    
    if (response.code === 200) {
      ElMessage.success('取消收藏成功')
      await loadCollectList(true)
      await loadStats()
    } else {
      ElMessage.error(response.message || '取消收藏失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败: ' + (error.message || '未知错误'))
    }
  }
}

</script>

<style lang="scss" scoped>
.user-collect {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 32px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 20px;

      .icon-wrapper {
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #606266;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #e4e7ed;
      }

      .header-text {
        h2 {
          margin: 0 0 8px 0;
          font-size: 32px;
          font-weight: 700;
          color: #303133;
        }

        p {
          margin: 0;
          font-size: 16px;
          color: #909399;
        }
      }
    }

  }

  .stats-row {
    margin-bottom: 20px;

    .stats-card {
      border-radius: 12px !important;
      border: 1px solid #ebeef5 !important;
      background: white !important;
      background-color: white !important;
      background-image: none !important;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06) !important;
      transition: background-color 0.3s, border-color 0.3s, box-shadow 0.3s !important;
      transform: none !important;
      position: relative;
      overflow: visible;
      cursor: default !important;
      height: 96px !important;
      box-sizing: border-box !important;
      display: flex !important;
      align-items: center !important;

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

      &:hover {
        background: #fafafa !important;
        background-color: #fafafa !important;
        background-image: none !important;
        border-color: #dcdfe6 !important;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
        transform: none !important;
        
        .stats-icon {
          transform: none !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          
          :deep(.el-icon) {
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            text-shadow: none !important;
            opacity: 1 !important;
          }
        }
      }

      :deep(.el-card__header) {
        display: none !important;
        padding: 0 !important;
        height: 0 !important;
        margin: 0 !important;
        border-bottom: none !important;
      }

      :deep(.el-card__body) {
        padding: 20px !important;
        background: transparent !important;
        background-color: transparent !important;
        transition: background-color 0.3s !important;
        height: 100% !important;
        box-sizing: border-box !important;
        display: flex !important;
        align-items: center !important;
      }

      &:hover :deep(.el-card__body) {
        background-color: transparent !important;
      }

      .stats-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stats-icon {
          width: auto;
          height: auto;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          transition: none !important;
          transform: none !important;
          pointer-events: none !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          background: transparent !important;
          border-radius: 0 !important;
          
          :deep(.el-icon) {
            transition: none !important;
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            text-shadow: none !important;
            opacity: 1 !important;
          }
          
          &:hover {
            transform: none !important;
            box-shadow: none !important;
            filter: none !important;
            opacity: 1 !important;
            
            :deep(.el-icon) {
              transform: none !important;
              box-shadow: none !important;
              filter: none !important;
              text-shadow: none !important;
              opacity: 1 !important;
            }
          }
        }

        .stats-info {
          flex: 1;

          .stats-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
            transition: all 0.3s;
          }

          .stats-label {
            font-size: 14px;
            color: #909399;
            margin-bottom: 2px;
          }
        }
      }
    }
  }

  .filter-card {
    margin-bottom: 20px;
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .filter-form {
      margin-bottom: 0;

      :deep(.el-form-item) {
        margin-bottom: 0;
      }

      // 输入框focus样式 - 黑色边框
      :deep(.el-input) {
        .el-input__wrapper {
          border-radius: 8px;
          
          &.is-focus,
          &.is-focus:hover {
            border-color: #303133 !important;
            box-shadow: 0 0 0 1px #303133 inset !important;
          }
        }
      }
      
      // 选择框focus样式 - 黑色边框（覆盖所有可能的状态）
      :deep(.el-select) {
        // 禁用所有默认的 focus 效果，使用CSS变量覆盖
        .el-select__wrapper,
        .el-input__wrapper {
          border-radius: 8px;
          border: none !important;
          
          &:focus,
          &:focus-visible,
          &.is-focus,
          &.is-focus:hover {
            --el-input-focus-border-color: #303133 !important;
            --el-border-color: #303133 !important;
            border: none !important;
            box-shadow: 0 0 0 1px #303133 inset !important;
            outline: none !important;
          }
        }
        
        &.is-focus .el-input__wrapper,
        &.is-focus .el-input__wrapper:hover,
        &.is-focus .el-input .el-input__wrapper,
        &.is-focus .el-input .el-input__wrapper:hover {
          --el-input-focus-border-color: #303133 !important;
          --el-border-color: #303133 !important;
          border: none !important;
          box-shadow: 0 0 0 1px #303133 inset !important;
          outline: none !important;
        }
        
        .el-input.is-focus .el-input__wrapper,
        .el-input.is-focus .el-input__wrapper:hover {
          --el-input-focus-border-color: #303133 !important;
          --el-border-color: #303133 !important;
          border: none !important;
          box-shadow: 0 0 0 1px #303133 inset !important;
          outline: none !important;
        }
        
        // 覆盖所有可能的组合
        .el-input .el-input__wrapper.is-focus,
        .el-input .el-input__wrapper.is-focus:hover {
          --el-input-focus-border-color: #303133 !important;
          --el-border-color: #303133 !important;
          border: none !important;
          box-shadow: 0 0 0 1px #303133 inset !important;
          outline: none !important;
        }
        
        // 当 el-select 获得焦点时
        &.is-focus {
          .el-select__wrapper,
          .el-input__wrapper {
            --el-input-focus-border-color: #303133 !important;
            --el-border-color: #303133 !important;
            border: none !important;
            box-shadow: 0 0 0 1px #303133 inset !important;
            outline: none !important;
          }
        }
      }
    }
    
    // 全局覆盖 - 使用更具体的选择器（覆盖所有可能的状态）
    :deep(.el-input__wrapper.is-focus) {
      border-color: #303133 !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
    }
    
    // el-select的所有可能组合
    :deep(.el-select .el-input__wrapper.is-focus),
    :deep(.el-select.is-focus .el-input__wrapper),
    :deep(.el-select .el-input.is-focus .el-input__wrapper),
    :deep(.el-select.is-focus .el-input .el-input__wrapper),
    :deep(.el-select .el-input .el-input__wrapper.is-focus),
    :deep(.el-select.is-focus .el-input .el-input__wrapper.is-focus),
    :deep(.el-select:hover .el-input__wrapper.is-focus),
    :deep(.el-select.is-focus:hover .el-input__wrapper) {
      border-color: #303133 !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
    }
  }

  .collect-card {
    margin-bottom: 20px;
    border-radius: 12px;
    border: none;
    overflow: hidden;
    transition: transform 0.3s, box-shadow 0.3s;

    &:hover {
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      transform: translateY(-4px);
      
      .card-cover {
        transform: none !important;
        isolation: isolate !important;
        contain: layout style paint !important;
        
        img {
          transition: none !important;
          transform: translate3d(0, 0, 0) !important;
          transform-origin: center center !important;
          transform-style: flat !important;
          scale: 1 !important;
          width: 100% !important;
          height: 100% !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          backface-visibility: hidden !important;
          perspective: none !important;
          isolation: isolate !important;
          position: relative !important;
          top: 0 !important;
          left: 0 !important;
        }
      }
    }

    // 禁用 el-card 的默认 hover 效果
    :deep(.el-card__body) {
      img {
        transition: none !important;
        transform: none !important;
        scale: 1 !important;
        box-shadow: none !important;
        filter: none !important;
        opacity: 1 !important;
      }
    }

    &:hover :deep(.el-card__body) {
      img {
        transition: none !important;
        transform: none !important;
        scale: 1 !important;
        box-shadow: none !important;
        filter: none !important;
        opacity: 1 !important;
      }
    }

    .card-cover {
      position: relative;
      width: 100%;
      height: 180px;
      overflow: hidden;
      cursor: pointer;
      transition: none !important;
      transform: none !important;
      isolation: isolate !important;
      contain: layout style paint !important;
      backface-visibility: hidden !important;
      perspective: none !important;

      img {
        width: 100% !important;
        height: 100% !important;
        object-fit: cover;
        transition: none !important;
        transform: translate3d(0, 0, 0) !important;
        transform-origin: center center !important;
        transform-style: flat !important;
        scale: 1 !important;
        will-change: auto !important;
        max-width: 100% !important;
        max-height: 100% !important;
        box-shadow: none !important;
        filter: none !important;
        opacity: 1 !important;
        backface-visibility: hidden !important;
        perspective: none !important;
        isolation: isolate !important;
        position: relative !important;
        top: 0 !important;
        left: 0 !important;
      }

      &:hover {
        transition: none !important;
        transform: none !important;
        
        img {
          transition: none !important;
          transform: translate3d(0, 0, 0) !important;
          transform-origin: center center !important;
          transform-style: flat !important;
          scale: 1 !important;
          width: 100% !important;
          height: 100% !important;
          box-shadow: none !important;
          filter: none !important;
          opacity: 1 !important;
          backface-visibility: hidden !important;
          perspective: none !important;
          position: relative !important;
          top: 0 !important;
          left: 0 !important;
        }
      }

      .no-image {
        width: 100%;
        height: 100%;
        background: #f5f7fa;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 8px;
        color: #c0c4cc;

        .el-icon {
          font-size: 48px;
        }

        span {
          font-size: 14px;
        }
      }

      .type-badge {
        position: absolute;
        top: 12px;
        right: 12px;
        padding: 4px 12px;
        border-radius: 999px;
        font-size: 12px;
        font-weight: 500;
        display: inline-flex;
        align-items: center;
        gap: 6px;
        box-shadow: none;
        transition: background-color 0.15s ease, border-color 0.15s ease, color 0.15s ease;
        z-index: 2;
        
        &:hover {
          filter: brightness(0.97);
        }
        
        .el-icon {
          font-size: 14px;
          opacity: 0.9;
        }
      }
    }

    .card-body {
      padding: 16px;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 12px 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;

        &:hover {
          color: #409eff;
        }
      }

      .card-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 16px;
        font-size: 13px;
        color: #909399;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 4px;

          .el-icon {
            font-size: 14px;
          }
        }
      }

      .card-actions {
        display: flex;
        gap: 8px;

        .action-btn {
          flex: 1;
          border-radius: 4px;
          padding: 6px 16px;
          font-weight: 500;
          transition: none;

          &:hover {
            transform: none !important;
            box-shadow: none !important;
          }
        }
      }
    }
  }

  .load-more-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 30px;
    padding: 20px 0;
    min-height: 60px;

    .loading-more {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #409eff;
      font-size: 14px;

      .loading-icon {
        font-size: 18px;
        animation: rotate 1s linear infinite;
      }
    }

    .load-more-tip {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #909399;
      font-size: 14px;

      .el-icon {
        font-size: 16px;
        animation: bounce 2s ease-in-out infinite;
      }
    }

    .no-more {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #c0c4cc;
      font-size: 13px;

      .el-icon {
        font-size: 16px;
        color: #67c23a;
      }
    }
  }

  // 旋转动画
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  // 上下跳动动画
  @keyframes bounce {
    0%, 100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-4px);
    }
  }

  // 旧的分页样式（已移除，保留以防需要）
  .pagination-wrapper {
    display: none; // 隐藏分页组件

    // 翻页组件样式
    :deep(.el-pagination) {
      .el-pagination__total {
        color: #606266;
        font-weight: 400;
      }

      // 每页条数选择器
      .el-pagination__sizes {
        .el-select {
          .el-input__wrapper {
            border-radius: 6px;
            border-color: #e4e7ed;
            background-color: white;

            &.is-focus {
              border-color: #909399 !important;
              box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2) inset !important;
            }
          }
        }
      }
      
      // 下拉菜单选项样式 - 移除蓝色
      :deep(.el-select-dropdown__item) {
        color: #606266 !important;
        
        &:hover {
          background-color: #f5f7fa !important;
          color: #303133 !important;
        }
        
        &.selected {
          color: #303133 !important;
          background-color: #f5f5f5 !important;
          font-weight: 600;
        }
      }

      // 上一页/下一页按钮
      .btn-prev,
      .btn-next {
        background-color: white;
        border: 1px solid #e4e7ed;
        border-radius: 6px;
        color: #606266;
        min-width: 32px;
        height: 32px;

        &:hover {
          color: #303133;
          border-color: #c0c4cc;
          background-color: #fafafa;
        }

        &:disabled {
          color: #c0c4cc;
          background-color: #f5f7fa;
          border-color: #e4e7ed;
          cursor: not-allowed;
        }
      }

      // 页码按钮
      .el-pager {
        li {
          background-color: white;
          border: 1px solid #e4e7ed;
          border-radius: 6px;
          color: #606266;
          min-width: 32px;
          height: 32px;
          margin: 0 4px;

          &:hover {
            color: #303133;
            border-color: #c0c4cc;
            background-color: #fafafa;
          }

          &.is-active {
            background-color: #f5f5f5;
            border-color: #909399;
            color: #303133 !important;
            font-weight: 600;
          }
          
          // 确保没有蓝色
          &.number {
            color: #606266 !important;
            
            &.is-active {
              color: #303133 !important;
            }
          }
        }
      }

      // 跳转输入框
      .el-pagination__jump {
        color: #606266;

        .el-input {
          .el-input__wrapper {
            border-radius: 6px;
            border-color: #e4e7ed;
            background-color: white;
            width: 50px;

            &.is-focus {
              border-color: #909399 !important;
              box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2) inset !important;
            }
          }
        }
      }
    }
  }


  :deep(.el-empty) {
    padding: 60px 20px;

    .el-empty__description p {
      color: #909399;
      font-size: 14px;
    }
  }
}
</style>

<style lang="scss">
// 全局禁用收藏卡片图片的所有效果
.collect-card .card-cover img {
  transition: none !important;
  transform: translate3d(0, 0, 0) !important;
  transform-origin: center center !important;
  transform-style: flat !important;
  scale: 1 !important;
  box-shadow: none !important;
  filter: none !important;
  opacity: 1 !important;
  backface-visibility: hidden !important;
  perspective: none !important;
  isolation: isolate !important;
}

.collect-card:hover .card-cover img {
  transition: none !important;
  transform: translate3d(0, 0, 0) !important;
  transform-origin: center center !important;
  transform-style: flat !important;
  scale: 1 !important;
  box-shadow: none !important;
  filter: none !important;
  opacity: 1 !important;
}

// 翻页组件下拉菜单样式 - 移除蓝色（因为下拉菜单挂载在body上，需要非scoped样式）
.el-pagination .el-select-dropdown__item,
.el-select-dropdown.el-popper[data-popper-placement] .el-select-dropdown__item {
  color: #606266 !important;
  
  &:hover {
    background-color: #f5f7fa !important;
    color: #303133 !important;
  }
  
  &.selected {
    color: #303133 !important;
    background-color: #f5f5f5 !important;
    font-weight: 600;
  }
}

// 收藏页面输入框和选择框focus样式 - 黑色边框（全局样式，确保优先级）
.user-collect .filter-card .el-input__wrapper.is-focus,
.user-collect .filter-card .el-input__wrapper.is-focus:hover {
  border-color: #303133 !important;
  box-shadow: 0 0 0 1px #303133 inset !important;
}

// el-select的所有可能组合（全局样式，使用CSS变量和最高优先级）
.user-collect .filter-card .el-select.collect-type-select,
.user-collect .filter-card .collect-type-select {
  // 禁用所有默认的 focus 效果
  .el-select__wrapper,
  .el-input__wrapper,
  .el-input .el-input__wrapper {
    &:focus,
    &:focus-visible,
    &.is-focus,
    &.custom-black-focus.is-focus {
      --el-input-focus-border-color: #303133 !important;
      --el-border-color: #303133 !important;
      --el-color-primary: #303133 !important;
      border: none !important;
      border-color: #303133 !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
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
      border: none !important;
      border-color: #303133 !important;
      box-shadow: 0 0 0 1px #303133 inset !important;
      outline: none !important;
    }
  }
  
  // 所有可能的选择器组合（最高优先级）
  .el-input__wrapper.is-focus,
  .el-input.is-focus .el-input__wrapper,
  .el-input .el-input__wrapper.is-focus,
  &.is-focus .el-input__wrapper,
  &.is-focus .el-input .el-input__wrapper,
  .custom-black-focus.is-focus {
    --el-input-focus-border-color: #303133 !important;
    --el-border-color: #303133 !important;
    --el-color-primary: #303133 !important;
    border: none !important;
    border-color: #303133 !important;
    box-shadow: 0 0 0 1px #303133 inset !important;
    outline: none !important;
  }
}

// 使用属性选择器确保最高优先级
.user-collect .filter-card .el-select.collect-type-select .el-input__wrapper[class*="is-focus"],
.user-collect .filter-card .collect-type-select .el-input__wrapper[class*="is-focus"] {
  --el-input-focus-border-color: #303133 !important;
  --el-border-color: #303133 !important;
  --el-color-primary: #303133 !important;
  border: none !important;
  border-color: #303133 !important;
  box-shadow: 0 0 0 1px #303133 inset !important;
  outline: none !important;
}
</style>
