<template>
  <div class="data-table">
    <!-- 表格工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-if="showAdd">
            <el-icon><Plus /></el-icon>
            添加
          </el-button>
          <el-button type="danger" @click="handleBatchDelete" v-if="showBatchDelete && selectedRows.length > 0">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </slot>
      </div>
      <div class="toolbar-right">
        <slot name="toolbar-right">
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </slot>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <el-table
      :data="data"
      :loading="loading"
      :stripe="stripe"
      :border="border"
      :height="height"
      :max-height="maxHeight"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @filter-change="handleFilterChange"
      v-bind="$attrs"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="55"
        align="center"
      />
      
      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="80"
        align="center"
        :index="getIndex"
      />
      
      <!-- 动态列 -->
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :align="column.align || 'left'"
        :sortable="column.sortable"
        :filters="column.filters"
        :filter-method="column.filterMethod"
        :show-overflow-tooltip="column.showOverflowTooltip !== false"
      >
        <template #default="{ row, $index }">
          <slot
            :name="column.prop"
            :row="row"
            :index="$index"
            :column="column"
          >
            <!-- 默认显示内容 -->
            <span v-if="!column.render">{{ getCellValue(row, column.prop) }}</span>
            
            <!-- 自定义渲染 -->
            <component
              v-else
              :is="column.render"
              :row="row"
              :value="getCellValue(row, column.prop)"
              :index="$index"
            />
          </slot>
        </template>
      </el-table-column>
      
      <!-- 操作列 -->
      <el-table-column
        v-if="showActions"
        label="操作"
        :width="actionWidth"
        :min-width="actionMinWidth"
        align="center"
        fixed="right"
      >
        <template #default="{ row, $index }">
          <slot name="actions" :row="row" :index="$index">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="info" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </slot>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        :layout="paginationLayout"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Refresh } from '@element-plus/icons-vue'

interface Column {
  prop: string
  label: string
  width?: number
  minWidth?: number
  align?: 'left' | 'center' | 'right'
  sortable?: boolean | 'custom'
  filters?: Array<{ text: string; value: any }>
  filterMethod?: (value: any, row: any, column: any) => boolean
  showOverflowTooltip?: boolean
  render?: any
}

interface Props {
  data: any[]
  columns: Column[]
  loading?: boolean
  total?: number
  currentPage?: number
  pageSize?: number
  pageSizes?: number[]
  showToolbar?: boolean
  showAdd?: boolean
  showBatchDelete?: boolean
  showSelection?: boolean
  showIndex?: boolean
  showActions?: boolean
  showPagination?: boolean
  actionWidth?: number
  actionMinWidth?: number
  stripe?: boolean
  border?: boolean
  height?: number | string
  maxHeight?: number | string
  paginationLayout?: string
}

interface Emits {
  (e: 'add'): void
  (e: 'edit', row: any): void
  (e: 'delete', row: any): void
  (e: 'view', row: any): void
  (e: 'batch-delete', rows: any[]): void
  (e: 'refresh'): void
  (e: 'page-change', page: number, size: number): void
  (e: 'sort-change', sort: any): void
  (e: 'filter-change', filters: any): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  total: 0,
  currentPage: 1,
  pageSize: 10,
  pageSizes: () => [10, 20, 50, 100],
  showToolbar: true,
  showAdd: true,
  showBatchDelete: true,
  showSelection: false,
  showIndex: false,
  showActions: true,
  showPagination: true,
  actionWidth: 200,
  actionMinWidth: 150,
  stripe: true,
  border: true,
  paginationLayout: 'total, sizes, prev, pager, next, jumper'
})

const emit = defineEmits<Emits>()

const selectedRows = ref<any[]>([])

// 获取单元格值
const getCellValue = (row: any, prop: string) => {
  const keys = prop.split('.')
  let value = row
  for (const key of keys) {
    value = value?.[key]
  }
  return value
}

// 获取序号
const getIndex = (index: number) => {
  return (props.currentPage - 1) * props.pageSize + index + 1
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

// 排序变化
const handleSortChange = (sort: any) => {
  emit('sort-change', sort)
}

// 筛选变化
const handleFilterChange = (filters: any) => {
  emit('filter-change', filters)
}

// 添加
const handleAdd = () => {
  emit('add')
}

// 编辑
const handleEdit = (row: any) => {
  emit('edit', row)
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    emit('delete', row)
  } catch {
    // 用户取消
  }
}

// 查看
const handleView = (row: any) => {
  emit('view', row)
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    emit('batch-delete', selectedRows.value)
  } catch {
    // 用户取消
  }
}

// 刷新
const handleRefresh = () => {
  emit('refresh')
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  emit('page-change', props.currentPage, size)
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  emit('page-change', page, props.pageSize)
}
</script>

<style lang="scss" scoped>
.data-table {
  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 12px 0;
    
    .toolbar-left {
      display: flex;
      gap: 12px;
    }
    
    .toolbar-right {
      display: flex;
      gap: 12px;
    }
  }
  
  .table-pagination {
    margin-top: 16px;
    display: flex;
    justify-content: center;
  }
}
</style>











