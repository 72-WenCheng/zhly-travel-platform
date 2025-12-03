<template>
  <div class="admin-list-modern">
    <!-- 页面头部 -->
    <div class="page-header-modern">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="32"><Trophy /></el-icon>
        </div>
        <div class="header-title">
          <h1>用户等级管理</h1>
          <p>配置用户等级体系和权限控制</p>
        </div>
      </div>
    </div>

    <!-- 等级统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #CD7F32 0%, #8B4513 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">青铜旅行者</div>
          <div class="stat-value">{{ levelStats.bronze || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #C0C0C0 0%, #808080 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">白银探索者</div>
          <div class="stat-value">{{ levelStats.silver || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">黄金游侠</div>
          <div class="stat-value">{{ levelStats.gold || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #E5E4E2 0%, #A9A9A9 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">铂金旅者</div>
          <div class="stat-value">{{ levelStats.platinum || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #B9F2FF 0%, #00BFFF 100%)">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">钻石达人</div>
          <div class="stat-value">{{ levelStats.diamond || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #FF4500 0%, #DC143C 100%)">
          <el-icon :size="24"><Crown /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">王者导师</div>
          <div class="stat-value">{{ levelStats.king || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 等级列表 -->
    <el-card class="table-card-modern">
      <el-table :data="levels" class="modern-table">
        <el-table-column label="等级" width="200">
          <template #default="{ row }">
            <div class="level-cell">
              <div class="level-icon" :style="{ background: row.levelColor }">
                <el-icon><Trophy /></el-icon>
              </div>
              <div>
                <div class="level-name" :style="{ color: row.levelColor }">{{ row.levelName }}</div>
                <div class="level-code">LV.{{ row.levelCode }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所需积分" prop="requiredPoints" width="120" />
        <el-table-column label="发布权限" width="120">
          <template #default="{ row }">
            <el-tag :type="row.canPostPlan ? 'success' : 'info'">
              {{ row.canPostPlan ? '可发布' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="每日发布限制" prop="dailyPostLimit" width="120" />
        <el-table-column label="评论权限" width="120">
          <template #default="{ row }">
            <el-tag :type="row.canComment ? 'success' : 'info'">
              {{ row.canComment ? '可评论' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="每日评论限制" prop="dailyCommentLimit" width="120" />
        <el-table-column label="精品内容" width="120">
          <template #default="{ row }">
            <el-tag :type="row.canViewPremium ? 'success' : 'info'">
              {{ row.canViewPremium ? '可查看' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="editLevel(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="'编辑等级 - ' + currentLevel.levelName"
      width="600px"
      class="modern-edit-dialog"
    >
      <div class="dialog-header-decoration"></div>
      <el-form :model="currentLevel" label-width="130px" class="edit-form">
        <el-form-item label="等级名称">
          <el-input v-model="currentLevel.levelName" />
        </el-form-item>
        <el-form-item label="所需积分">
          <el-input-number v-model="currentLevel.requiredPoints" :min="0" />
        </el-form-item>
        <el-form-item label="等级颜色">
          <el-color-picker v-model="currentLevel.levelColor" />
        </el-form-item>
        <el-form-item label="发布权限">
          <el-switch v-model="currentLevel.canPostPlan" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="每日发布限制">
          <el-input-number v-model="currentLevel.dailyPostLimit" :min="0" :max="999" />
          <span class="form-tip">设置为999表示无限制</span>
        </el-form-item>
        <el-form-item label="评论权限">
          <el-switch v-model="currentLevel.canComment" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="每日评论限制">
          <el-input-number v-model="currentLevel.dailyCommentLimit" :min="0" :max="999" />
          <span class="form-tip">设置为999表示无限制</span>
        </el-form-item>
        <el-form-item label="精品内容权限">
          <el-switch v-model="currentLevel.canViewPremium" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" size="large">取消</el-button>
        <el-button type="primary" @click="saveLevel" size="large">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Trophy, User, Crown, Edit, Check } from '@element-plus/icons-vue'
import request from '@/utils/request'

const levels = ref([])
const levelStats = ref({})
const dialogVisible = ref(false)
const currentLevel = ref({})

const loadLevels = async () => {
  try {
    const res = await request.get('/admin/user-level/list')
    if (res.code === 200) {
      levels.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载等级列表失败')
  }
}

const loadLevelStats = async () => {
  try {
    const res = await request.get('/admin/user-level/stats')
    if (res.code === 200) {
      levelStats.value = res.data || {}
    }
  } catch (error) {
    console.error('加载等级统计失败', error)
  }
}

const editLevel = (row) => {
  currentLevel.value = { ...row }
  dialogVisible.value = true
}

const saveLevel = async () => {
  try {
    const res = await request.put('/admin/user-level/update', currentLevel.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadLevels()
      loadLevelStats()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadLevels()
  loadLevelStats()
})
</script>

<style lang="scss">
@import '@/styles/admin-list.scss';
</style>

<style lang="scss" scoped>
.level-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .level-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;
  }
  
  .level-name {
    font-size: 16px;
    font-weight: 600;
  }
  
  .level-code {
    font-size: 12px;
    color: #909399;
  }
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

.dialog-header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  margin-bottom: 20px;
}

.edit-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
  }
}
</style>







