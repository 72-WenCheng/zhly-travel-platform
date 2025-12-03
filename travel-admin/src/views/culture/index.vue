<template>
  <div class="culture-management">
    <!-- 文旅项目表单对话框 -->
    <CultureProjectForm
      ref="projectFormRef"
      v-model="formVisible"
      :project="currentProject"
      @success="handleFormSuccess"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="文旅名称">
          <el-input v-model="searchForm.name" placeholder="请输入文旅名称" clearable />
        </el-form-item>
        <el-form-item label="所在地">
          <el-input v-model="searchForm.location" placeholder="请输入所在地" clearable />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择业务类型" clearable>
            <el-option label="农产品" :value="1" />
            <el-option label="农家乐" :value="2" />
            <el-option label="电商" :value="3" />
            <el-option label="新农人" :value="4" />
            <el-option label="团建" :value="5" />
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
          <span>文旅对接列表</span>
          <el-button type="primary" @click="handleAdd">添加文旅</el-button>
        </div>
      </template>

      <el-table :data="cultureList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="文旅名称" width="200" show-overflow-tooltip />
        <el-table-column prop="location" label="所在地" width="150" />
        <el-table-column prop="businessType" label="业务类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getBusinessTypeTag(row.businessType)">
              {{ getBusinessTypeName(row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="contactEmail" label="联系邮箱" width="180" />
        <el-table-column prop="priceRange" label="价格范围" width="120" />
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
import { getProjectList, createProject, updateProject, deleteProject, toggleProjectStatus } from '@/api/cultureProject'
import type { CultureProject } from '@/api/cultureProject'
import CultureProjectForm from '@/components/CultureProjectForm.vue'

// 搜索表单
const searchForm = reactive({
  name: '',
  location: '',
  businessType: null,
  status: null
})

// 文旅列表
const cultureList = ref([])
const loading = ref(false)

// 表单相关
const formVisible = ref(false)
const currentProject = ref<CultureProject | null>(null)
const projectFormRef = ref()

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取业务类型名称
const getBusinessTypeName = (type: number) => {
  const typeMap = {
    1: '农产品',
    2: '农家乐',
    3: '电商',
    4: '新农人',
    5: '团建'
  }
  return typeMap[type] || '未知'
}

// 获取业务类型标签
const getBusinessTypeTag = (type: number) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info',
    4: 'danger',
    5: ''
  }
  return tagMap[type] || ''
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadCultureList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    location: '',
    businessType: null,
    status: null
  })
  handleSearch()
}

// 加载文旅列表
const loadCultureList = async () => {
  loading.value = true
  try {
    const result = await getProjectList({
      page: pagination.current,
      size: pagination.size,
      keyword: searchForm.name,
      region: searchForm.location,
      type: searchForm.businessType || undefined,
      status: searchForm.status || undefined
    })
    
    if (result.code === 200 && result.data) {
      cultureList.value = result.data.list || []
      pagination.total = result.data.total || 0
    } else {
      ElMessage.error(result.message || '加载文旅列表失败')
    }
  } catch (error: any) {
    console.error('加载文旅列表失败:', error)
    ElMessage.error(error.message || '加载文旅列表失败')
  } finally {
    loading.value = false
  }
}

// 添加文旅
const handleAdd = () => {
  currentProject.value = null
  formVisible.value = true
}

// 编辑文旅
const handleEdit = (row: any) => {
  currentProject.value = { ...row }
  formVisible.value = true
}

// 表单提交成功
const handleFormSuccess = async (formData: CultureProject) => {
  try {
    let result
    if (currentProject.value?.id) {
      // 编辑模式
      result = await updateProject(currentProject.value.id, formData)
    } else {
      // 新增模式
      result = await createProject(formData)
    }

    // 重置loading状态
    projectFormRef.value?.resetLoading()

    if (result.code === 200) {
      ElMessage.success(currentProject.value?.id ? '文旅项目更新成功' : '文旅项目创建成功')
      formVisible.value = false
      currentProject.value = null
      loadCultureList()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error: any) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
    // 重置loading状态
    projectFormRef.value?.resetLoading()
  }
}

// 查看文旅
const handleView = (row: any) => {
  window.open(`/api/culture-project/${row.id}`, '_blank')
}

// 切换状态
const handleToggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '下架' : '上架'}文旅 ${row.name} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === 1 ? 0 : 1
    const result = await toggleProjectStatus(row.id, newStatus)
    if (result.code === 200) {
      ElMessage.success('状态更新成功')
      loadCultureList()
    } else {
      ElMessage.error(result.message || '状态更新失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '状态更新失败')
    }
  }
}

// 删除文旅
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个文旅吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deleteProject(row.id)
    if (result.code === 200) {
      ElMessage.success('文旅删除成功')
      loadCultureList()
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
  loadCultureList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadCultureList()
}

onMounted(() => {
  loadCultureList()
})
</script>

<style lang="scss" scoped>
.culture-management {
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









