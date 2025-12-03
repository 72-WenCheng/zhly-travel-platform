<template>
  <div class="attraction-management">
    <!-- 景点表单对话框 -->
    <AttractionForm
      ref="attractionFormRef"
      v-model="formVisible"
      :attraction="currentAttraction"
      @success="handleFormSuccess"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="景点名称">
          <el-input v-model="searchForm.name" placeholder="请输入景点名称" clearable />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="请输入城市" clearable />
        </el-form-item>
        <el-form-item label="景点类型">
          <el-select v-model="searchForm.type" placeholder="请选择景点类型" clearable>
            <el-option label="自然景观" :value="1" />
            <el-option label="人文景观" :value="2" />
            <el-option label="主题公园" :value="3" />
            <el-option label="博物馆" :value="4" />
            <el-option label="历史遗迹" :value="5" />
            <el-option label="美食街" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="下架" :value="0" />
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
          <span>景点列表</span>
          <el-button type="primary" @click="handleAdd">添加景点</el-button>
        </div>
      </template>

      <el-table :data="attractionList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="景点名称" width="200" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="type" label="景点类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ticketPrice" label="门票价格" width="100">
          <template #default="{ row }">
            ¥{{ row.ticketPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="等级" width="80">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column prop="viewCount" label="查看次数" width="100" />
        <el-table-column prop="collectCount" label="收藏次数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" size="small" @click="handleView(row)">查看</el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
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
import { getAttractionList, createAttraction, updateAttraction, deleteAttraction, toggleAttractionStatus } from '@/api/attraction'
import type { Attraction } from '@/api/attraction'
import AttractionForm from '@/components/AttractionForm.vue'

// 搜索表单
const searchForm = reactive({
  name: '',
  city: '',
  type: null,
  status: null
})

// 景点列表
const attractionList = ref([])
const loading = ref(false)

// 表单相关
const formVisible = ref(false)
const currentAttraction = ref<Attraction | null>(null)
const attractionFormRef = ref()

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取景点类型名称
const getTypeName = (type: number) => {
  const typeMap = {
    1: '自然景观',
    2: '人文景观',
    3: '主题公园',
    4: '博物馆',
    5: '历史遗迹',
    6: '美食街'
  }
  return typeMap[type] || '未知'
}

// 获取景点类型标签
const getTypeTag = (type: number) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info',
    4: 'danger',
    5: '',
    6: 'success'
  }
  return tagMap[type] || ''
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadAttractionList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    city: '',
    type: null,
    status: null
  })
  handleSearch()
}

// 加载景点列表
const loadAttractionList = async () => {
  loading.value = true
  try {
    const result = await getAttractionList({
      page: pagination.current,
      size: pagination.size,
      keyword: searchForm.name,
      city: searchForm.city,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined
    })
    
    if (result.code === 200 && result.data) {
      attractionList.value = result.data.list || []
      pagination.total = result.data.total || 0
    } else {
      ElMessage.error(result.message || '加载景点列表失败')
    }
  } catch (error: any) {
    console.error('加载景点列表失败:', error)
    ElMessage.error(error.message || '加载景点列表失败')
  } finally {
    loading.value = false
    }
}

// 添加景点
const handleAdd = () => {
  currentAttraction.value = null
  formVisible.value = true
}

// 编辑景点
const handleEdit = (row: any) => {
  currentAttraction.value = { ...row }
  formVisible.value = true
}

// 表单提交成功
const handleFormSuccess = async (formData: Attraction) => {
  try {
    let result
    if (currentAttraction.value?.id) {
      // 编辑模式
      result = await updateAttraction(currentAttraction.value.id, formData)
    } else {
      // 新增模式
      result = await createAttraction(formData)
    }

    // 重置loading状态
    attractionFormRef.value?.resetLoading()

    if (result.code === 200) {
      ElMessage.success(currentAttraction.value?.id ? '景点更新成功' : '景点创建成功')
      formVisible.value = false
      currentAttraction.value = null
      loadAttractionList()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error: any) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
    // 重置loading状态
    attractionFormRef.value?.resetLoading()
  }
}

// 查看景点
const handleView = (row: any) => {
  // 跳转到用户端景点详情页
  window.open(`/home/user/attractions/detail/${row.id}`, '_blank')
}

// 切换状态
const handleToggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '下架' : '上架'}景点 ${row.name} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === 1 ? 0 : 1
    const result = await toggleAttractionStatus(row.id, newStatus)
    if (result.code === 200) {
      ElMessage.success('状态更新成功')
      loadAttractionList()
    } else {
      ElMessage.error(result.message || '状态更新失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '状态更新失败')
    }
  }
}

// 删除景点
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个景点吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deleteAttraction(row.id)
    if (result.code === 200) {
      ElMessage.success('景点删除成功')
      loadAttractionList()
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
  loadAttractionList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadAttractionList()
}

onMounted(() => {
  loadAttractionList()
})
</script>

<style lang="scss" scoped>
.attraction-management {
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









