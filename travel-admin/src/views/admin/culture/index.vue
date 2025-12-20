<template>
  <div class="culture-management">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon experience">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalExperience }}</div>
            <div class="stat-label">文化体验</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon service">
            <el-icon><House /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalService }}</div>
            <div class="stat-label">服务项目</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon project">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalProject }}</div>
            <div class="stat-label">政府项目</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon product">
            <el-icon><ShoppingBag /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalProduct }}</div>
            <div class="stat-label">特色产品</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-tabs v-model="activeTab" type="border-card" class="culture-tabs">
      <!-- 文化体验管理 -->
      <el-tab-pane label="文化体验管理" name="experience">
        <div class="tab-header">
          <div class="header-left">
            <el-button type="primary" @click="handleAdd('experience')">
              <el-icon><Plus /></el-icon>
              添加文化体验
            </el-button>
            <el-button @click="loadData('experience')">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
          <div class="header-right">
            <el-input
              v-model="filters.experience.keyword"
              placeholder="搜索体验名称、位置"
              style="width: 250px"
              clearable
              @clear="handleSearch('experience')"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch('experience')">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>

        <el-table :data="experienceList" style="width: 100%" v-loading="loading.experience">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="体验名称" width="200" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="location" label="位置" width="150" />
          <el-table-column prop="price" label="价格（元）" width="120">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="时长" width="120" />
          <el-table-column prop="rating" label="评分" width="100">
            <template #default="{ row }">
              <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit('experience', row)">
                编辑
              </el-button>
              <el-button 
                link 
                :type="row.status === 'active' ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus('experience', row)"
              >
                {{ row.status === 'active' ? '下架' : '上架' }}
              </el-button>
              <el-button link type="danger" size="small" @click="handleDelete('experience', row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.experience.page"
          v-model:page-size="pagination.experience.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.experience.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange('experience')"
          @current-change="handlePageChange('experience')"
          class="pagination"
        />
      </el-tab-pane>

      <!-- 服务管理（农家乐/民宿） -->
      <el-tab-pane label="服务管理" name="service">
        <div class="tab-header">
          <div class="header-left">
            <el-button type="primary" @click="handleAdd('service')">
              <el-icon><Plus /></el-icon>
              添加服务
            </el-button>
            <el-button @click="loadData('service')">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
          <div class="header-right">
            <el-input
              v-model="filters.service.keyword"
              placeholder="搜索服务名称、位置"
              style="width: 250px"
              clearable
              @clear="handleSearch('service')"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch('service')">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>

        <el-table :data="serviceList" style="width: 100%" v-loading="loading.service">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="服务名称" width="200" />
          <el-table-column prop="badge" label="标签" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ row.badge }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="位置" width="150" />
          <el-table-column label="套餐价格" width="200">
            <template #default="{ row }">
              <span v-if="row.packages && row.packages.length">
                ¥{{ row.packages[0].price }} - ¥{{ row.packages[row.packages.length - 1].price }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="100">
            <template #default="{ row }">
              <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
            </template>
          </el-table-column>
          <el-table-column prop="views" label="浏览量" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit('service', row)">
                编辑
              </el-button>
              <el-button 
                link 
                :type="row.status === 'active' ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus('service', row)"
              >
                {{ row.status === 'active' ? '下架' : '上架' }}
              </el-button>
              <el-button link type="danger" size="small" @click="handleDelete('service', row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.service.page"
          v-model:page-size="pagination.service.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.service.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange('service')"
          @current-change="handlePageChange('service')"
          class="pagination"
        />
      </el-tab-pane>

      <!-- 政府项目管理 -->
      <el-tab-pane label="政府项目管理" name="project">
        <div class="tab-header">
          <div class="header-left">
            <el-button type="primary" @click="handleAdd('project')">
              <el-icon><Plus /></el-icon>
              添加政府项目
            </el-button>
            <el-button @click="loadData('project')">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
          <div class="header-right">
            <el-input
              v-model="filters.project.keyword"
              placeholder="搜索项目名称、地区"
              style="width: 250px"
              clearable
              @clear="handleSearch('project')"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch('project')">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>

        <el-table :data="projectList" style="width: 100%" v-loading="loading.project">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="项目名称" width="250" />
          <el-table-column prop="location" label="项目地区" width="150" />
          <el-table-column prop="investment" label="投资规模（万元）" width="150">
            <template #default="{ row }">
              ¥{{ row.investment }}万
            </template>
          </el-table-column>
          <el-table-column prop="beneficiaries" label="惠及农户" width="120">
            <template #default="{ row }">
              {{ row.beneficiaries }}户
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始时间" width="120" />
          <el-table-column prop="statusText" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.statusType">{{ row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit('project', row)">
                编辑
              </el-button>
              <el-button link type="info" size="small" @click="handleViewApplications(row)">
                查看申请
              </el-button>
              <el-button link type="danger" size="small" @click="handleDelete('project', row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.project.page"
          v-model:page-size="pagination.project.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.project.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange('project')"
          @current-change="handlePageChange('project')"
          class="pagination"
        />
      </el-tab-pane>

      <!-- 特色产品管理 -->
      <el-tab-pane label="特色产品管理" name="product">
        <div class="tab-header">
          <div class="header-left">
            <el-button type="primary" @click="handleAdd('product')">
              <el-icon><Plus /></el-icon>
              添加特色产品
            </el-button>
            <el-button @click="loadData('product')">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
          <div class="header-right">
            <el-input
              v-model="filters.product.keyword"
              placeholder="搜索产品名称、产地"
              style="width: 250px"
              clearable
              @clear="handleSearch('product')"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch('product')">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>

        <el-table :data="productList" style="width: 100%" v-loading="loading.product">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="产品名称" width="200" />
          <el-table-column prop="badge" label="标签" width="120">
            <template #default="{ row }">
              <el-tag size="small" type="danger">{{ row.badge }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="origin" label="产地" width="150" />
          <el-table-column prop="price" label="价格（元）" width="120">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="sales" label="销量" width="100" />
          <el-table-column prop="rating" label="评分" width="100">
            <template #default="{ row }">
              <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit('product', row)">
                编辑
              </el-button>
              <el-button 
                link 
                :type="row.status === 'active' ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus('product', row)"
              >
                {{ row.status === 'active' ? '下架' : '上架' }}
              </el-button>
              <el-button link type="danger" size="small" @click="handleDelete('product', row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.product.page"
          v-model:page-size="pagination.product.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.product.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange('product')"
          @current-change="handlePageChange('product')"
          class="pagination"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称" />
        </el-form-item>
        
        <el-form-item v-if="currentType === 'experience'" label="分类" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类，如：精选推荐" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在地区" prop="region">
              <el-input v-model="formData.region" placeholder="请输入地区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细位置" prop="location">
              <el-input v-model="formData.location" placeholder="请输入详细位置" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="formData.price"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入价格"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分" prop="rating">
              <el-input-number
                v-model="formData.rating"
                :min="0"
                :max="5"
                :precision="1"
                style="width: 100%"
                placeholder="请输入评分"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>
        
        <el-form-item v-if="currentType === 'experience'" label="简介" prop="slogan">
          <el-input
            v-model="formData.slogan"
            type="textarea"
            :rows="2"
            placeholder="请输入简介，如：跟着非遗老师亲手完成一件蜀绣作品，收藏一段东方美学"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="formData.image" placeholder="请输入图片URL" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import BackButton from '@/components/BackButton.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, Star, House, Document, ShoppingBag } from '@element-plus/icons-vue'
import request from '@/utils/request'
import type { FormInstance, FormRules } from 'element-plus'
import * as cultureExperienceApi from '@/api/cultureExperience'

const router = useRouter()
const activeTab = ref('experience')

// 统计信息
const stats = reactive({
  totalExperience: 0,
  totalService: 0,
  totalProject: 0,
  totalProduct: 0
})

// 加载状态
const loading = reactive({
  experience: false,
  service: false,
  project: false,
  product: false
})

// 筛选条件
const filters = reactive({
  experience: { keyword: '' },
  service: { keyword: '' },
  project: { keyword: '' },
  product: { keyword: '' }
})

// 分页
const pagination = reactive({
  experience: { page: 1, size: 10, total: 0 },
  service: { page: 1, size: 10, total: 0 },
  project: { page: 1, size: 10, total: 0 },
  product: { page: 1, size: 10, total: 0 }
})

// 文化体验数据
const experienceList = ref([])

// 服务数据
const serviceList = ref([])

// 政府项目数据
const projectList = ref([])

// 特色产品数据
const productList = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => {
  if (!formData.id) {
    const typeMap: Record<string, string> = {
      experience: '添加文化体验',
      service: '添加服务',
      project: '添加政府项目',
      product: '添加特色产品'
    }
    return typeMap[currentType.value] || '添加项目'
  } else {
    const typeMap: Record<string, string> = {
      experience: '编辑文化体验',
      service: '编辑服务',
      project: '编辑政府项目',
      product: '编辑特色产品'
    }
    return typeMap[currentType.value] || '编辑项目'
  }
})
const currentType = ref('experience')
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive({
  id: null as number | null,
  name: '',
  categoryName: '',
  type: 0,
  region: '',
  location: '',
  slogan: '',
  description: '',
  image: '',
  price: 0,
  rating: 0,
  contactPerson: '',
  contactPhone: '',
  address: '',
  status: 1
})

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 获取文化体验统计
    const experienceStatsRes = await cultureExperienceApi.getExperienceStatistics()
    if (experienceStatsRes.code === 200) {
      stats.totalExperience = experienceStatsRes.data.totalExperiences || 0
    }
    
    // 获取其他统计
    const res = await request.get('/admin/culture/project/stats')
    if (res.code === 200) {
      const data = res.data
      stats.totalService = data.serviceCount || 0
      stats.totalProject = data.projectCount || 0
      stats.totalProduct = data.productCount || 0
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 加载数据
const loadData = async (type?: string) => {
  const loadType = type || activeTab.value
  
  try {
    if (loadType === 'experience' || !type) {
      loading.experience = true
      const params: any = {
        page: pagination.experience.page,
        size: pagination.experience.size
      }
      if (filters.experience.keyword) params.keyword = filters.experience.keyword
      
      const experienceRes = await cultureExperienceApi.getExperienceList(params)
      if (experienceRes.code === 200) {
        experienceList.value = (experienceRes.data.list || []).map((item: any) => ({
          id: item.id,
          name: item.name,
          categoryName: item.categoryName || '文化体验',
          location: item.location,
          price: item.price ? Number(item.price) : 0,
          duration: item.duration || '约2小时',
          rating: item.rating ? Number(item.rating) : 0,
          status: item.status || 'active'
        }))
        pagination.experience.total = experienceRes.data.total || 0
      }
      loading.experience = false
    }
    
    if (loadType === 'service' || !type) {
      loading.service = true
      const params: any = {
        page: pagination.service.page,
        size: pagination.service.size,
        type: 0
      }
      if (filters.service.keyword) params.keyword = filters.service.keyword
      
      const serviceRes = await request.get('/admin/culture/project/page', { params })
      if (serviceRes.code === 200) {
        serviceList.value = (serviceRes.data.list || []).map((item: any) => ({
          id: item.id,
          title: item.name,
          badge: '政府推荐',
          location: item.location,
          packages: [{ price: item.price }],
          rating: item.rating || 0,
          views: item.viewCount || 0,
          status: item.status === 1 ? 'active' : 'inactive'
        }))
        pagination.service.total = serviceRes.data.total || 0
      }
      loading.service = false
    }
    
    if (loadType === 'project' || !type) {
      loading.project = true
      const params: any = {
        page: pagination.project.page,
        size: pagination.project.size,
        type: 3
      }
      if (filters.project.keyword) params.keyword = filters.project.keyword
      
      const projectRes = await request.get('/admin/culture/project/page', { params })
      if (projectRes.code === 200) {
        projectList.value = (projectRes.data.list || []).map((item: any) => ({
          id: item.id,
          title: item.name,
          location: item.location,
          investment: item.price ? Number(item.price) / 10000 : 0,
          beneficiaries: 0,
          startDate: item.createTime ? item.createTime.substring(0, 7) : '',
          statusText: item.status === 1 ? '进行中' : '招募中',
          statusType: item.status === 1 ? 'success' : 'warning'
        }))
        pagination.project.total = projectRes.data.total || 0
      }
      loading.project = false
    }
    
    if (loadType === 'product' || !type) {
      loading.product = true
      const params: any = {
        page: pagination.product.page,
        size: pagination.product.size,
        type: 1
      }
      if (filters.product.keyword) params.keyword = filters.product.keyword
      
      const productRes = await request.get('/admin/culture/project/page', { params })
      if (productRes.code === 200) {
        productList.value = (productRes.data.list || []).map((item: any) => ({
          id: item.id,
          name: item.name,
          badge: '特色产品',
          origin: item.location,
          price: item.price,
          sales: item.orderCount || 0,
          rating: item.rating || 0,
          status: item.status === 1 ? 'active' : 'inactive'
        }))
        pagination.product.total = productRes.data.total || 0
      }
      loading.product = false
    }
    
    // 加载统计
    if (!type) {
      loadStats()
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    loading.experience = false
    loading.service = false
    loading.project = false
    loading.product = false
  }
}

// 搜索
const handleSearch = (type: string) => {
  const paginationMap: Record<string, any> = {
    experience: pagination.experience,
    service: pagination.service,
    project: pagination.project,
    product: pagination.product
  }
  paginationMap[type].page = 1
  loadData(type)
}

// 分页变化
const handleSizeChange = (type: string) => {
  loadData(type)
}

const handlePageChange = (type: string) => {
  loadData(type)
}

// 添加
const handleAdd = (type: string) => {
  currentType.value = type
  resetForm()
  const typeMap: Record<string, number> = {
    experience: 2,
    service: 0,
    project: 3,
    product: 1
  }
  formData.type = typeMap[type]
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (type: string, row: any) => {
  currentType.value = type
  try {
    let res: any
    if (type === 'experience') {
      res = await cultureExperienceApi.getExperienceById(row.id)
      if (res.code === 200) {
        const data = res.data
        formData.id = data.id
        formData.name = data.name || ''
        formData.categoryName = data.categoryName || ''
        formData.location = data.location || ''
        formData.slogan = data.slogan || ''
        formData.description = data.description || ''
        
        // 安全地解析图片字段
        try {
          if (data.images) {
            if (typeof data.images === 'string') {
              try {
                const parsed = JSON.parse(data.images)
                formData.image = Array.isArray(parsed) && parsed.length > 0 ? parsed[0] : parsed
              } catch {
                // 如果不是有效的 JSON，直接使用原值
                formData.image = data.images
              }
            } else {
              formData.image = data.images
            }
          } else {
            formData.image = ''
          }
        } catch {
          formData.image = ''
        }
        
        formData.price = data.price ? Number(data.price) : 0
        formData.rating = data.rating ? Number(data.rating) : 0
        formData.status = data.status === 'active' ? 1 : 0
        dialogVisible.value = true
      } else {
        ElMessage.error(res.message || '获取体验详情失败')
      }
    } else {
      res = await request.get(`/admin/culture/project/${row.id}`)
      if (res.code === 200) {
        const data = res.data
        formData.id = data.id
        formData.name = data.name || ''
        formData.type = data.type || 0
        formData.region = data.region || ''
        formData.location = data.location || ''
        formData.description = data.description || ''
        formData.image = data.image || ''
        formData.price = data.price ? Number(data.price) : 0
        formData.rating = data.rating ? Number(data.rating) : 0
        formData.contactPerson = data.contactPerson || ''
        formData.contactPhone = data.contactPhone || ''
        formData.address = data.address || ''
        formData.status = data.status !== undefined ? data.status : 1
        dialogVisible.value = true
      } else {
        ElMessage.error(res.message || '获取项目详情失败')
      }
    }
  } catch (error: any) {
    console.error('获取详情失败:', error)
    ElMessage.error(error?.message || '获取详情失败，请稍后重试')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (currentType.value === 'experience') {
      // 文化体验的提交逻辑
      // 处理图片字段：确保格式正确
      let imagesValue: string | null = null
      if (formData.image) {
        try {
          // 如果已经是 JSON 字符串，先解析再重新序列化
          const parsed = JSON.parse(formData.image)
          imagesValue = JSON.stringify(Array.isArray(parsed) ? parsed : [formData.image])
        } catch {
          // 如果不是 JSON，直接作为字符串数组的第一个元素
          imagesValue = JSON.stringify([formData.image])
        }
      }
      
      const submitData: any = {
        name: formData.name,
        categoryName: formData.categoryName || '文化体验',
        location: formData.location,
        price: formData.price || 0,
        duration: '约2小时',
        rating: formData.rating || 5.0,
        status: formData.status === 1 ? 'active' : 'inactive',
        slogan: formData.slogan || '',
        description: formData.description || '',
        images: imagesValue
      }
      
      if (formData.id) {
        // 编辑
        try {
          const res = await cultureExperienceApi.updateExperience(formData.id, submitData)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            loadData(currentType.value)
            loadStats()
          } else {
            ElMessage.error(res.message || '更新失败')
          }
        } catch (error: any) {
          console.error('更新文化体验失败:', error)
          ElMessage.error(error?.message || '更新失败，请稍后重试')
        }
      } else {
        // 新增
        try {
          const res = await cultureExperienceApi.createExperience(submitData)
          if (res.code === 200) {
            ElMessage.success('添加成功')
            dialogVisible.value = false
            loadData(currentType.value)
            loadStats()
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        } catch (error: any) {
          console.error('创建文化体验失败:', error)
          ElMessage.error(error?.message || '添加失败，请稍后重试')
        }
      }
    } else {
      // 其他类型的提交逻辑
      const submitData = { ...formData }
      delete submitData.id // 移除 id，因为 id 在 URL 路径中
      
      if (formData.id) {
        // 编辑
        try {
          const res = await request.put(`/admin/culture/project/${formData.id}`, submitData)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            loadData(currentType.value)
            loadStats()
          } else {
            ElMessage.error(res.message || '更新失败')
          }
        } catch (error: any) {
          console.error('更新项目失败:', error)
          ElMessage.error(error?.message || '更新失败，请稍后重试')
        }
      } else {
        // 新增
        try {
          const res = await request.post('/admin/culture/project', submitData)
          if (res.code === 200) {
            ElMessage.success('添加成功')
            dialogVisible.value = false
            loadData(currentType.value)
            loadStats()
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        } catch (error: any) {
          console.error('创建项目失败:', error)
          ElMessage.error(error?.message || '添加失败，请稍后重试')
        }
      }
    }
  } catch (error: any) {
    console.error('表单验证失败:', error)
    if (error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.id = null
  formData.name = ''
  formData.categoryName = ''
  formData.type = 0
  formData.region = ''
  formData.location = ''
  formData.slogan = ''
  formData.description = ''
  formData.image = ''
  formData.price = 0
  formData.rating = 0
  formData.contactPerson = ''
  formData.contactPhone = ''
  formData.address = ''
  formData.status = 1
}

// 切换状态（上架/下架）
const handleToggleStatus = async (type, row) => {
  const action = row.status === 'active' ? '下架' : '上架'
  const newStatus = row.status === 'active' ? 'inactive' : 'active'
  
  try {
    await ElMessageBox.confirm(
      `确认${action}该${type === 'experience' ? '文化体验' : type === 'service' ? '服务' : type === 'project' ? '政府项目' : '产品'}？`,
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    let res: any
    if (type === 'experience') {
      res = await cultureExperienceApi.toggleExperienceStatus(row.id, newStatus)
    } else {
      const statusNum = newStatus === 'active' ? 1 : 0
      res = await request.put(`/admin/culture/project/${row.id}/status`, {
        status: statusNum
      })
    }
    
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 删除
const handleDelete = async (type, row) => {
  const typeMap = {
    experience: '文化体验',
    service: '服务',
    project: '政府项目',
    product: '特色产品'
  }
  
  try {
    await ElMessageBox.confirm(
      `确认删除该${typeMap[type]}？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    let res: any
    if (type === 'experience') {
      res = await cultureExperienceApi.deleteExperience(row.id)
    } else {
      res = await request.delete(`/admin/culture/project/${row.id}`)
    }
    
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 查看项目申请
const handleViewApplications = (row: any) => {
  // 跳转到申请管理页面，并传递项目ID用于筛选
  router.push({
    path: '/home/admin/culture/applications',
    query: { projectId: row.id }
  })
}

// 页面加载
onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style scoped lang="scss">
.culture-management {
  padding: 20px;
}

.culture-tabs {
  :deep(.el-tabs__content) {
    padding: 20px;
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
  
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        
        &.experience {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
        }
        
        &.service {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          color: white;
        }
        
        &.project {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          color: white;
        }
        
        &.product {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          color: white;
        }
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
}

.tab-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-left {
    display: flex;
    gap: 10px;
  }
  
  .header-right {
    display: flex;
    gap: 10px;
    align-items: center;
  }
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-table) {
  .el-button--small {
    padding: 4px 8px;
  }
}
</style>
