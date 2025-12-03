<template>
  <div class="admin-list-modern">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><ChatDotRound /></el-icon>
        </div>
        <div class="header-title">
          <h1>评论管理</h1>
          <p>管理和审核用户评论</p>
          <div class="status-info">
            <div class="status-text">
              <el-icon class="status-icon"><Refresh /></el-icon>
              <span>数据每30秒自动刷新</span>
            </div>
            <span v-if="lastUpdateTime" class="update-time">{{ lastUpdateTime }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon :size="24"><ChatDotRound /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总评论数</div>
          <div class="stat-value">{{ totalCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon :size="24"><Select /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已发布</div>
          <div class="stat-value">{{ publishedCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon :size="24"><Hide /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已隐藏</div>
          <div class="stat-value">{{ hiddenCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
          <el-icon :size="24"><Delete /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已删除</div>
          <div class="stat-value">{{ deletedCount }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card-modern" shadow="never">
      <div class="filter-header">
        <el-icon><Search /></el-icon>
        <span>筛选条件</span>
      </div>
      <el-form :model="filters" class="filter-form">
        <div class="filter-row">
          <el-form-item label="内容类型">
            <el-select v-model="filters.contentType" placeholder="请选择内容类型" clearable>
              <el-option label="全部类型" value="ALL" />
              <el-option label="攻略" value="PLAN" />
              <el-option label="景点" value="ATTRACTION" />
              <el-option label="文旅项目" value="CULTURE" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="请选择状态" clearable>
              <el-option label="全部状态" value="ALL" />
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="已隐藏" value="HIDDEN" />
              <el-option label="已删除" value="DELETED" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词" style="flex: 1;">
            <el-input v-model="filters.keyword" placeholder="搜索评论内容或用户名" clearable />
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button class="reset-btn" @click="handleReset">
              重置筛选
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <transition name="slide-down">
      <el-card class="batch-card-modern" v-if="selectedComments.length > 0" shadow="never">
        <div class="batch-actions-modern">
          <div class="batch-info-modern">
            <el-icon :size="20"><Select /></el-icon>
            <span>已选择 <strong>{{ selectedComments.length }}</strong> 条评论</span>
          </div>
          <div class="batch-buttons">
            <el-button type="warning" size="small" @click="handleBatchHide">
              <el-icon><Hide /></el-icon>
              批量隐藏
            </el-button>
            <el-button type="danger" size="small" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button size="small" @click="clearSelection">
              <el-icon><CloseBold /></el-icon>
              取消选择
            </el-button>
          </div>
        </div>
      </el-card>
    </transition>

    <!-- 评论列表 -->
    <el-card class="table-card-modern" shadow="never">
      <div class="table-wrapper">
        <el-table 
          :data="commentList" 
          v-loading="loading" 
          class="modern-table"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          
          <el-table-column label="评论用户" width="150" align="center">
            <template #default="{ row }">
              <div class="user-info">
                <el-avatar :src="row.avatar" :size="32">{{ row.username?.charAt(0) || 'U' }}</el-avatar>
                <span>{{ row.username || '未知用户' }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="评论内容" min-width="300" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="comment-content">
                <div class="content-text">{{ row.content }}</div>
                <div class="content-meta">
                  <el-tag size="small" :type="getContentTypeColor(row.contentType)">
                    {{ getContentTypeLabel(row.contentType) }}
                  </el-tag>
                  <a 
                    v-if="row.contentId"
                    :href="getContentUrl(row.contentType, row.contentId)" 
                    target="_blank" 
                    style="color: #409eff; text-decoration: none;"
                    @click.stop
                    class="content-title"
                  >
                    {{ row.contentTitle || row.contentId }}
                  </a>
                  <span v-else style="color: #c0c4cc;">{{ row.contentTitle || '内容已删除' }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="互动数据" width="150" align="center">
            <template #default="{ row }">
              <div class="interaction-stats">
                <div class="stat-item">
                  <el-icon><CaretTop /></el-icon>
                  <span>{{ row.likeCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>{{ row.replyCount || 0 }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="标签" width="120" align="center">
            <template #default="{ row }">
              <div style="display: flex; gap: 4px; justify-content: center; flex-wrap: wrap;">
                <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="row.isHot" type="warning" size="small">热评</el-tag>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdTime" label="发布时间" width="180" align="center" />
          
          <el-table-column label="操作" width="320" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  v-if="row.status === 'PUBLISHED'"
                  type="info"
                  size="small"
                  text
                  @click="handleToggleTop(row)">
                  <el-icon><Top /></el-icon>
                  {{ row.isTop ? '取消置顶' : '置顶' }}
                </el-button>
                <el-button
                  v-if="row.status === 'PUBLISHED'"
                  type="warning"
                  size="small"
                  text
                  @click="handleHide(row)">
                  <el-icon><Hide /></el-icon>
                  隐藏
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  text
                  @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container-modern simple-pagination">
        <el-button
          class="page-btn"
          :disabled="pagination.page <= 1"
          @click="handleCurrentChange(pagination.page - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="page-info">
          {{ pagination.page }} / {{ Math.max(1, Math.ceil((pagination.total || 1) / (pagination.pageSize || 10))) }}
        </span>
        <el-button
          class="page-btn"
          :disabled="pagination.page >= Math.ceil((pagination.total || 1) / (pagination.pageSize || 10))"
          @click="handleCurrentChange(pagination.page + 1)"
        >
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <div class="page-jump">
          <span>前往</span>
          <el-input
            v-model.number="pageJump"
            size="small"
            class="page-jump-input"
            @input="handlePageJump"
          />
          <span>页</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  ChatDotRound,
  Delete,
  Hide,
  Refresh,
  Select,
  Search,
  CaretTop,
  Top,
  CloseBold
} from '@element-plus/icons-vue';
import request from '@/utils/request';
import BackButton from '@/components/BackButton.vue';
import { resolvePaginationTotal } from '@/utils/pagination';

// 数据
const loading = ref(false);
const commentList = ref([]);
const selectedComments = ref([]);
const lastUpdateTime = ref('');

// 统计数据
const totalCount = ref(0);
const publishedCount = ref(0);
const hiddenCount = ref(0);
const deletedCount = ref(0);

// 筛选条件
const filters = reactive({
  contentType: 'ALL',
  status: 'ALL',
  keyword: ''
});

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
});

// 翻页跳转
const pageJump = ref(null);

const handlePageJump = () => {
  const totalPages = Math.max(1, Math.ceil((pagination.total || 1) / (pagination.pageSize || 10)));
  let target = Number(pageJump.value || 1);
  if (!Number.isFinite(target)) return;
  if (target < 1) target = 1;
  if (target > totalPages) target = totalPages;
  if (target === pagination.page) return;
  handleCurrentChange(target);
};

// 格式化时间
const formatTime = () => {
  const now = new Date();
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  return `${hours}:${minutes}:${seconds}`;
};

// 加载评论列表
const loadComments = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      contentType: filters.contentType !== 'ALL' ? filters.contentType : undefined,
      status: filters.status !== 'ALL' ? filters.status : undefined,
      keyword: filters.keyword || undefined
    };

    const response = await request.get('/admin/comment/list', { params });
    
    if (response.code === 200) {
      const data = response.data || {};
      const list =
        Array.isArray(data.list)
          ? data.list
          : Array.isArray(data.records)
            ? data.records
            : Array.isArray(data.items)
              ? data.items
              : Array.isArray(data)
                ? data
                : [];
      const resolvedTotal = resolvePaginationTotal(data, list);
      const shouldSlice =
        list.length > pagination.pageSize &&
        resolvedTotal <= list.length;

      if (shouldSlice) {
        const start = (pagination.page - 1) * pagination.pageSize;
        commentList.value = list.slice(start, start + pagination.pageSize);
        pagination.total = list.length;
      } else {
        commentList.value = list;
        pagination.total = resolvedTotal;
      }
      lastUpdateTime.value = formatTime();
    } else {
      ElMessage.error(response.message || '加载失败');
    }
  } catch (error) {
    console.error('加载评论列表失败:', error);
    ElMessage.error('加载失败，请检查网络连接');
  } finally {
    loading.value = false;
  }
};

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await request.get('/admin/comment/stats');
    if (response.code === 200) {
      const stats = response.data;
      totalCount.value = stats.totalCount || 0;
      publishedCount.value = stats.publishedCount || 0;
      hiddenCount.value = stats.hiddenCount || 0;
      deletedCount.value = stats.deletedCount || 0;
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
    totalCount.value = 0;
    publishedCount.value = 0;
    hiddenCount.value = 0;
    deletedCount.value = 0;
  }
};

// 搜索
const handleSearch = () => {
  pagination.page = 1;
  loadComments();
};

// 重置
const handleReset = () => {
  filters.contentType = 'ALL';
  filters.status = 'ALL';
  filters.keyword = '';
  pagination.page = 1;
  loadComments();
};

// 监听筛选条件变化，自动触发搜索（使用防抖）
let searchTimeout = null;
watch(
  () => [filters.contentType, filters.status, filters.keyword],
  () => {
    // 清除之前的定时器
    if (searchTimeout) {
      clearTimeout(searchTimeout);
    }
    // 设置新的定时器（防抖：500ms）
    searchTimeout = setTimeout(() => {
      pagination.page = 1;
      loadComments();
    }, 500);
  },
  { deep: true }
);

// 选择变化
const handleSelectionChange = (selection) => {
  selectedComments.value = selection;
};

// 清除选择
const clearSelection = () => {
  selectedComments.value = [];
};

// 获取行类名
const getRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row';
};

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size;
  pagination.page = 1;
  loadComments();
};

// 当前页改变
const handleCurrentChange = (current) => {
  pagination.page = current;
  loadComments();
};

// 置顶/取消置顶
const handleToggleTop = async (row) => {
  try {
    const response = await request.put(`/admin/comment/toggle-top/${row.id}`);
    if (response.code === 200) {
      ElMessage.success(row.isTop ? '已取消置顶' : '已置顶');
      loadComments();
      loadStats();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('置顶操作失败:', error);
    ElMessage.error('操作失败');
  }
};

// 隐藏评论
const handleHide = async (row) => {
  try {
    await ElMessageBox.confirm('确定要隐藏这条评论吗？隐藏后用户将无法查看。', '确认隐藏', {
      type: 'warning'
    });
    
    const response = await request.put(`/admin/comment/hide/${row.id}`);
    if (response.code === 200) {
      ElMessage.success('已隐藏');
      loadComments();
      loadStats();
    } else {
      ElMessage.error(response.message || '隐藏失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('隐藏失败:', error);
      ElMessage.error('隐藏失败');
    }
  }
};

// 删除评论
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？删除后无法恢复。', '确认删除', {
      type: 'warning'
    });
    
    const response = await request.delete(`/admin/comment/delete/${row.id}`);
    if (response.code === 200) {
      ElMessage.success('删除成功');
      loadComments();
      loadStats();
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedComments.value.length} 条评论吗？`,
      '批量删除',
      { type: 'warning' }
    );
    
    const ids = selectedComments.value.map(c => c.id);
    const response = await request.delete('/admin/comment/batch-delete', { data: ids });
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功');
      clearSelection();
      loadComments();
      loadStats();
    } else {
      ElMessage.error(response.message || '批量删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error);
      ElMessage.error('批量删除失败');
    }
  }
};

// 批量隐藏
const handleBatchHide = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要隐藏选中的 ${selectedComments.value.length} 条评论吗？`,
      '批量隐藏',
      { type: 'warning' }
    );
    
    const ids = selectedComments.value.map(c => c.id);
    const response = await request.put('/admin/comment/batch-hide', ids);
    
    if (response.code === 200) {
      ElMessage.success('批量隐藏成功');
      clearSelection();
      loadComments();
      loadStats();
    } else {
      ElMessage.error(response.message || '批量隐藏失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量隐藏失败:', error);
      ElMessage.error('批量隐藏失败');
    }
  }
};

// 辅助函数
const getContentTypeLabel = (type) => {
  const labels = {
    'PLAN': '攻略',
    'ATTRACTION': '景点',
    'CULTURE': '文旅项目'
  };
  return labels[type] || type;
};

const getContentTypeColor = (type) => {
  const colors = {
    'PLAN': 'success',
    'ATTRACTION': 'primary',
    'CULTURE': 'warning'
  };
  return colors[type] || 'info';
};

const getStatusLabel = (status) => {
  const labels = {
    'PUBLISHED': '已发布',
    'HIDDEN': '已隐藏',
    'DELETED': '已删除'
  };
  return labels[status] || status;
};

const getStatusType = (status) => {
  const types = {
    'PUBLISHED': 'success',
    'HIDDEN': 'warning',
    'DELETED': 'danger'
  };
  return types[status] || 'info';
};

// 获取内容链接URL
const getContentUrl = (contentType, contentId) => {
  if (!contentType || !contentId) return '#';
  
  const urlMap = {
    'PLAN': `/home/user/plans/${contentId}`,
    'ATTRACTION': `/home/user/attractions/${contentId}`,
    'CULTURE': `/home/user/culture/${contentId}`
  };
  
  return urlMap[contentType] || '#';
};

// 自动刷新配置
const refreshInterval = ref(30000); // 30秒刷新一次
const autoRefreshTimer = ref(null);

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value);
  }
  autoRefreshTimer.value = setInterval(() => {
    loadComments();
    loadStats();
  }, refreshInterval.value);
};

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value);
    autoRefreshTimer.value = null;
  }
};

// 初始化
onMounted(() => {
  loadStats();
  loadComments();
  startAutoRefresh();
});

onUnmounted(() => {
  stopAutoRefresh();
});
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style lang="scss" scoped>
// 评论特定样式 - 保持与景点管理一致的样式
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;

  span {
    font-size: 14px;
    color: #303133;
  }
}

.comment-content {
  .content-text {
    font-size: 14px;
    color: #303133;
    line-height: 1.6;
    margin-bottom: 8px;
  }

  .content-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #909399;

    .content-title {
      max-width: 200px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: #409eff;
      
      &:hover {
        text-decoration: underline;
        color: #66b1ff;
      }
    }
  }
}

.interaction-stats {
  display: flex;
  gap: 16px;
  justify-content: center;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    color: #606266;

    .el-icon {
      font-size: 16px;
      color: #909399;
    }
  }
}
</style>
