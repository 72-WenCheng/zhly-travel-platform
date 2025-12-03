<template>
  <div class="user-attractions-new">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="48"><Star /></el-icon>
        </div>
        <div class="header-text">
          <h2>我的收藏</h2>
          <p class="page-desc">管理你收藏的景点、攻略和文旅项目</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" size="large" @click="goToRecommendations">
          <el-icon><Search /></el-icon>
          发现更多
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.total }}</div>
              <div class="stats-label">总收藏数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.attractionCount }}</div>
              <div class="stats-label">景点收藏</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.planCount }}</div>
              <div class="stats-label">攻略收藏</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.cultureCount }}</div>
              <div class="stats-label">文旅项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline class="filter-form">
        <el-form-item>
          <el-input 
            v-model="searchForm.title" 
            placeholder="搜索收藏内容..." 
            clearable 
            size="large"
            style="width: 300px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select 
            v-model="searchForm.status" 
            placeholder="收藏类型" 
            clearable
            size="large"
            style="width: 200px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="景点" :value="1">
              <el-icon><Location /></el-icon> 景点
            </el-option>
            <el-option label="攻略" :value="2">
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
    <el-row :gutter="20" class="plans-grid" v-loading="loading">
      <el-col :span="6" v-for="item in collectionList" :key="item.id">
        <el-card class="plan-card" shadow="hover" @click="handleView(item)">
          <div class="card-cover">
            <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" />
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
            <div 
              class="type-badge"
              :class="{
                'badge-attraction': item.type === 1,
                'badge-plan': item.type === 2,
                'badge-culture': item.type === 3
              }"
            >
              <el-icon v-if="item.type === 1"><Location /></el-icon>
              <el-icon v-else-if="item.type === 2"><Document /></el-icon>
              <el-icon v-else><OfficeBuilding /></el-icon>
              {{ getTypeText(item.type) }}
            </div>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <div class="card-meta">
              <span class="meta-item" v-if="item.location">
                <el-icon><Location /></el-icon>
                {{ item.location }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(item.collectTime) }}
              </span>
            </div>
            <div class="plan-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ item.viewCount || 0 }}
              </span>
            </div>
            <div class="plan-actions">
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
      v-if="!loading && collectionList.length === 0" 
      description="暂无收藏"
      :image-size="200"
    >
      <el-button type="primary" @click="goToRecommendations">去发现景点</el-button>
    </el-empty>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="collectionList.length > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[8, 12, 16, 20]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadCollections"
        @current-change="loadCollections"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Star, 
  Delete, 
  Collection, 
  Location, 
  Document, 
  Search, 
  View, 
  Clock,
  OfficeBuilding,
  Picture,
  Plus
} from '@element-plus/icons-vue'
import BackButton from '@/components/BackButton.vue'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  title: '',
  status: null as number | null
})

// 收藏列表
const collectionList = ref<any[]>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 8,
  total: 0
})

// 统计数据
const stats = ref({
  total: 4,
  attractionCount: 1,
  planCount: 3,
  cultureCount: 0
})

// 获取类型文本
const getTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '景点',
    2: '攻略',
    3: '文旅项目'
  }
  return typeMap[type] || '未知'
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

// 加载收藏列表
const loadCollections = async () => {
  loading.value = true
  try {
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const allCollections = [
      {
        id: 1,
        type: 1,
        title: '黄山风景区',
        location: '安徽黄山',
        coverImage: 'https://picsum.photos/300/200?random=1',
        viewCount: 1580,
        collectTime: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        type: 2,
        title: '西藏·虔诚',
        location: '西藏拉萨',
        coverImage: 'https://picsum.photos/300/200?random=2',
        viewCount: 2340,
        collectTime: '2024-01-14 14:20:00'
      },
      {
        id: 3,
        type: 2,
        title: '成都·美食之旅',
        location: '四川成都',
        coverImage: 'https://picsum.photos/300/200?random=3',
        viewCount: 1892,
        collectTime: '2024-01-13 09:15:00'
      },
      {
        id: 4,
        type: 2,
        title: '云南·花海',
        location: '云南大理',
        coverImage: 'https://picsum.photos/300/200?random=4',
        viewCount: 3156,
        collectTime: '2024-01-12 16:45:00'
      }
    ]
    
    // 筛选
    let filtered = allCollections
    if (searchForm.status !== null) {
      filtered = filtered.filter(item => item.type === searchForm.status)
    }
    if (searchForm.title) {
      filtered = filtered.filter(item => 
        item.title.includes(searchForm.title)
      )
    }
    
    collectionList.value = filtered
    pagination.total = filtered.length
    
  } catch (error) {
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleView = (item: any) => {
  const routeMap: Record<number, string> = {
    1: '/home/user/attractions/detail',
    2: '/home/user/plans/detail',
    3: '/home/user/culture/detail'
  }
  
  const route = routeMap[item.type]
  if (route) {
    router.push(`${route}/${item.id}`)
  }
}

// 取消收藏
const handleDelete = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('取消收藏成功')
    loadCollections()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败')
    }
  }
}

// 去发现页面
const goToRecommendations = () => {
  router.push('/home/user/recommendations')
}

onMounted(() => {
  loadCollections()
})
</script>

<style lang="scss" scoped>
.user-attractions-new {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    background: white;
    border-radius: 16px;
    padding: 32px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;

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
        border: 1px solid #e4e7ed;
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
          font-size: 16px;
          color: #909399;
        }
      }
    }

    .header-right {
      :deep(.el-button) {
        background-color: #409eff;
        border: none;
        color: white;
        border-radius: 8px;
        padding: 12px 24px;

        &:hover {
          background-color: #66b1ff;
        }
      }
    }
  }

  .stats-row {
    margin-bottom: 24px;

    .stats-card {
      border-radius: 12px;
      border: none;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: all 0.3s;
      background: #fff;
      position: relative;
      overflow: visible;

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
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
      }

      :deep(.el-card__body) {
        padding: 20px;
      }

      .stats-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stats-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 28px;
          flex-shrink: 0;
        }

        .stats-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;

          .stats-value {
            font-size: 32px;
            font-weight: 600;
            color: #303133;
            line-height: 1.2;
            margin-bottom: 6px;
          }

          .stats-label {
            font-size: 14px;
            color: #909399;
            line-height: 1;
          }
        }
      }
    }
  }

  .filter-card {
    margin-bottom: 24px;
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    :deep(.el-card__body) {
      padding: 20px;
    }
    
    // 全局覆盖输入框和选择框的focus样式
    :deep(.el-input__wrapper.is-focus) {
      border-color: #909399 !important;
      box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2) inset !important;
    }
    
    :deep(.el-select .el-input__wrapper.is-focus),
    :deep(.el-select.is-focus .el-input__wrapper),
    :deep(.el-select .el-input.is-focus .el-input__wrapper) {
      border-color: #303133 !important;
      box-shadow: 0 0 0 1px rgba(48, 49, 51, 0.2) inset !important;
    }

    .filter-form {
      margin: 0;

      :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 16px;

        &:last-child {
          margin-right: 0;
        }
      }

      // 输入框focus样式 - 灰色
      :deep(.el-input__wrapper) {
        border-radius: 8px;
        
        &.is-focus,
        &.is-focus:hover {
          border-color: #909399 !important;
          box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2) inset !important;
        }
      }
      
      // 选择框focus样式 - 黑色
      :deep(.el-select) {
        .el-input__wrapper {
          border-radius: 8px;
        }
        
        .el-input__wrapper.is-focus,
        .el-input__wrapper.is-focus:hover {
          border-color: #303133 !important;
          box-shadow: 0 0 0 1px rgba(48, 49, 51, 0.2) inset !important;
        }
        
        &.is-focus .el-input__wrapper,
        &.is-focus .el-input__wrapper:hover {
          border-color: #303133 !important;
          box-shadow: 0 0 0 1px rgba(48, 49, 51, 0.2) inset !important;
        }
      }
      
      // 更具体的选择器确保覆盖Element Plus默认样式
      :deep(.el-select.is-focus .el-input__wrapper),
      :deep(.el-select .el-input.is-focus .el-input__wrapper),
      :deep(.el-select.is-focus .el-input .el-input__wrapper) {
        border-color: #303133 !important;
        box-shadow: 0 0 0 1px rgba(48, 49, 51, 0.2) inset !important;
      }
      
      // 覆盖Element Plus的默认蓝色focus样式
      :deep(.el-input__wrapper.is-focus) {
        border-color: #909399 !important;
        box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2) inset !important;
      }
      
      :deep(.el-select .el-input__wrapper.is-focus) {
        border-color: #303133 !important;
        box-shadow: 0 0 0 1px rgba(48, 49, 51, 0.2) inset !important;
      }
    }
  }

  .plans-grid {
    margin-bottom: 24px;

    .plan-card {
      margin-bottom: 20px;
      border-radius: 12px;
      border: none;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        transform: translateY(-4px);
      }

      :deep(.el-card__body) {
        padding: 0;
      }

      .card-cover {
        position: relative;
        width: 100%;
        height: 180px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.3s;
        }

        &:hover img {
          transform: scale(1.05);
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
          padding: 8px 16px;
          border-radius: 20px;
          font-size: 13px;
          font-weight: 600;
          color: white;
          display: flex;
          align-items: center;
          gap: 6px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          backdrop-filter: blur(10px);
          transition: all 0.3s;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
          }

          .el-icon {
            font-size: 16px;
          }

          &.badge-attraction {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }

          &.badge-plan {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }

          &.badge-culture {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

          &:hover {
            color: #409eff;
          }
        }

        .card-meta {
          display: flex;
          flex-direction: column;
          gap: 8px;
          margin-bottom: 12px;
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

        .plan-meta {
          display: flex;
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

        .plan-actions {
          display: flex;
          gap: 8px;

          .action-btn {
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
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 20px 0;

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
</style>
