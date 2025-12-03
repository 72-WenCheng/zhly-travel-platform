<template>
  <div class="travel-plan-management">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="攻略标题">
          <el-input v-model="searchForm.title" placeholder="请输入攻略标题" clearable />
        </el-form-item>
        <el-form-item label="目的地">
          <el-input v-model="searchForm.destination" placeholder="请输入目的地" clearable />
        </el-form-item>
        <el-form-item label="旅游类型">
          <el-select v-model="searchForm.travelType" placeholder="请选择旅游类型" clearable>
            <el-option label="国内游" :value="1" />
            <el-option label="国外游" :value="2" />
            <el-option label="游轮游" :value="3" />
            <el-option label="节假日游" :value="4" />
            <el-option label="非节假日游" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>攻略列表</span>
          <el-button type="primary" @click="handleAdd">添加攻略</el-button>
        </div>
      </template>

      <el-table :data="planList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="攻略标题" width="200" show-overflow-tooltip />
        <el-table-column prop="destination" label="目的地" width="120" />
        <el-table-column prop="travelType" label="旅游类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTravelTypeTag(row.travelType)">
              {{ getTravelTypeName(row.travelType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="personCount" label="人数" width="80" />
        <el-table-column prop="perPersonBudget" label="人均预算" width="100">
          <template #default="{ row }">
            ¥{{ row.perPersonBudget }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="查看次数" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" size="small" @click="handleView(row)">查看</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              size="small" 
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="warning" 
              size="small" 
              @click="handleUnpublish(row)"
            >
              下架
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlanList, deletePlan, createPlan, updatePlan } from '@/api/travelPlan'
import type { TravelPlan } from '@/api/travelPlan'

// 搜索表单
const searchForm = reactive({
  title: '',
  destination: '',
  travelType: null,
  status: null
})

// 攻略列表
const planList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取旅游类型名称
const getTravelTypeName = (type: number) => {
  const typeMap = {
    1: '国内游',
    2: '国外游',
    3: '游轮游',
    4: '节假日游',
    5: '非节假日游'
  }
  return typeMap[type] || '未知'
}

// 获取旅游类型标签
const getTravelTypeTag = (type: number) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info',
    4: 'danger',
    5: ''
  }
  return tagMap[type] || ''
}

// 获取状态名称
const getStatusName = (status: number) => {
  const statusMap = {
    0: '草稿',
    1: '已发布',
    2: '已下架'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签
const getStatusTag = (status: number) => {
  const tagMap = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return tagMap[status] || ''
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadPlanList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    destination: '',
    travelType: null,
    status: null
  })
  handleSearch()
}

// 加载攻略列表
const loadPlanList = async () => {
  loading.value = true
  try {
    const result = await getPlanList({
      page: pagination.current,
      size: pagination.size,
      keyword: searchForm.title,
      destination: searchForm.destination,
      status: searchForm.status || undefined
    })
    
    if (result.code === 200 && result.data) {
      planList.value = result.data.list || []
      pagination.total = result.data.total || 0
    } else {
      ElMessage.error(result.message || '加载攻略列表失败')
    }
  } catch (error: any) {
    console.error('加载攻略列表失败:', error)
    ElMessage.error(error.message || '加载攻略列表失败')
  } finally {
    loading.value = false
  }
}

// 添加攻略
const handleAdd = () => {
  ElMessage.info('添加攻略功能开发中，请使用用户端创建攻略')
}

// 编辑攻略
const handleEdit = (row: any) => {
  ElMessage.info('编辑攻略功能开发中')
}

// 查看攻略
const handleView = (row: any) => {
  // 跳转到攻略详情页
  window.open(`/api/travel-plan/${row.id}`, '_blank')
}

// 发布攻略
const handlePublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发布这个攻略吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await updatePlan(row.id, { ...row, status: 1 })
    if (result.code === 200) {
      ElMessage.success('攻略发布成功')
      loadPlanList()
    } else {
      ElMessage.error(result.message || '发布失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '发布失败')
    }
  }
}

// 下架攻略
const handleUnpublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要下架这个攻略吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await updatePlan(row.id, { ...row, status: 2 })
    if (result.code === 200) {
      ElMessage.success('攻略下架成功')
      loadPlanList()
    } else {
      ElMessage.error(result.message || '下架失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '下架失败')
    }
  }
}

// 删除攻略
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个攻略吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deletePlan(row.id)
    if (result.code === 200) {
      ElMessage.success('攻略删除成功')
      loadPlanList()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadPlanList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadPlanList()
}

onMounted(() => {
  loadPlanList()
})
</script>

<style lang="scss" scoped>
.travel-plan-management {
  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>









