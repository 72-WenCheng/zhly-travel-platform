<template>
  <div class="profile-edit-page">
    <!-- 返回按钮 -->
    <BackButton />

    <!-- 个人资料表单 -->
    <el-card class="profile-form-card">
      <el-form
        ref="formRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="120px"
        label-position="right"
      >
        <!-- 头像上传 -->
        <el-form-item label="头像" prop="avatar">
          <div class="avatar-upload-wrapper">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <el-avatar 
                v-if="profileForm.avatar" 
                :src="profileForm.avatar" 
                :size="100"
                class="avatar-preview"
              >
                <el-icon><User /></el-icon>
              </el-avatar>
              <div v-else class="avatar-placeholder">
                <el-icon :size="40"><Plus /></el-icon>
                <div class="upload-text">上传头像</div>
              </div>
            </el-upload>
            <div class="avatar-tips">
              <el-icon><InfoFilled /></el-icon>
              <span>支持 JPG、PNG 格式，文件大小不超过 2MB</span>
            </div>
          </div>
        </el-form-item>

        <el-divider>
          <el-icon><User /></el-icon>
          <span>基本信息</span>
        </el-divider>

        <!-- 昵称 -->
        <el-form-item label="昵称" prop="nickname">
          <el-input 
            v-model="profileForm.nickname" 
            placeholder="请输入昵称"
            maxlength="20"
            show-word-limit
            clearable
          >
            <template #prefix>
              <el-icon><Edit /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 性别 -->
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="profileForm.gender">
            <el-radio :label="0">
              <el-icon><QuestionFilled /></el-icon>
              <span>保密</span>
            </el-radio>
            <el-radio :label="1">
              <span style="color: #409eff;">👨</span>
              <span>男</span>
            </el-radio>
            <el-radio :label="2">
              <span style="color: #f56c6c;">👩</span>
              <span>女</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 年龄 -->
        <el-form-item label="年龄" prop="age">
          <el-input-number
            v-model="profileForm.age"
            :min="1"
            :max="120"
            placeholder="请输入年龄"
            style="width: 100%"
          />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="profileForm.phone" 
            placeholder="请输入手机号"
            maxlength="11"
            clearable
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="profileForm.username" 
            placeholder="请输入用户名"
            clearable
            disabled
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
          <div class="field-tips">用户名作为登录凭证，不可修改</div>
        </el-form-item>


        <!-- 操作按钮 -->
        <el-form-item>
          <div class="form-actions">
            <el-button class="form-action-btn" @click="handleCancel" size="large">
              取消
            </el-button>
            <el-button 
              class="form-action-btn"
              @click="handleSubmit"
              size="large"
              :loading="submitLoading"
            >
              保存资料
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import BackButton from '@/components/BackButton.vue'
import request from '@/utils/request'
import { uploadAvatar, updateUserInfo } from '@/api/user'
import { getCurrentUserInfo } from '@/utils/user'
import {
  User, Edit, InfoFilled, Plus,
  QuestionFilled, Phone
} from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const submitLoading = ref(false)

// 表单数据
const profileForm = reactive({
  avatar: '',
  nickname: '',
  gender: 0,
  age: null,
  phone: '',
  username: ''
})


// 表单验证规则
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  age: [
    { type: 'number', min: 1, max: 120, message: '请输入正确的年龄', trigger: 'blur' }
  ]
}

// 头像上传前检查
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 自定义头像上传
const handleAvatarUpload = async (options) => {
  const file = options.file
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await uploadAvatar(formData)
    if (response.code === 200 && response.data) {
      const avatarUrl = response.data.avatar || response.data
      if (avatarUrl) {
        profileForm.avatar = avatarUrl
        ElMessage.success('头像上传成功')
      } else {
        ElMessage.error('头像上传失败：未返回图片地址')
      }
    } else {
      ElMessage.error(response.message || '头像上传失败')
    }
  } catch (error: unknown) {
    console.error('头像上传失败:', error)
    const errorMessage = error instanceof Error ? error.message : '头像上传失败，请稍后重试'
    ElMessage.error(errorMessage)
  }
}


// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 优先从当前标签页获取用户信息
    const userData = getCurrentUserInfo()
    if (userData) {
      fillFormFromUserData(userData)
    }
    
    // 从后端获取最新用户信息
    try {
      const response = await request.get('/user/info')
      if (response.code === 200 && response.data) {
        const userData = response.data
        fillFormFromUserData(userData)
        
        // 更新localStorage
        localStorage.setItem('travel_user_info', JSON.stringify(userData))
      }
    } catch (error) {
      console.warn('从后端获取用户信息失败，使用localStorage数据:', error)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// 从用户数据填充表单
const fillFormFromUserData = (userData) => {
  profileForm.avatar = userData.avatar || ''
  profileForm.nickname = userData.nickname || userData.username || ''
  profileForm.gender = userData.gender !== undefined ? userData.gender : 0
  profileForm.age = userData.age || null
  profileForm.phone = userData.phone || ''
  profileForm.username = userData.username || ''
}

// 取消
const handleCancel = () => {
  ElMessageBox.confirm('确定要取消吗？未保存的修改将丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.back()
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      
      try {
        // 在保存前检查是否是首次完善资料
        const userData = getCurrentUserInfo()
        const isFirstTime = !userData || (!userData.nickname && !userData.avatar)
        
        // 准备提交数据
        const updateData = {
          nickname: profileForm.nickname,
          avatar: profileForm.avatar,
          gender: profileForm.gender,
          age: profileForm.age,
          phone: profileForm.phone
        }
        
        // 调用后端API保存用户信息
        const response = await updateUserInfo(updateData)
        
        if (response.code === 200) {
          // 更新当前标签页的用户信息
          if (userData) {
            const updatedData = {
              ...userData,
              ...updateData
            }
            // 更新 sessionStorage（当前标签页）
            sessionStorage.setItem('travel_user_info', JSON.stringify(updatedData))
            // 更新 localStorage（向后兼容）
            localStorage.setItem('travel_user_info', JSON.stringify(updatedData))
          }
          
          if (isFirstTime) {
            ElMessage.success('保存成功！首次完善资料获得10积分奖励')
          } else {
            ElMessage.success('保存成功！')
          }
          
          // 延迟跳转回详情页
          setTimeout(() => {
            router.back()
          }, 1500)
        } else {
          ElMessage.error(response.message || '保存失败，请稍后重试')
        }
      } catch (error: unknown) {
        console.error('保存用户信息失败:', error)
        const errorMessage = error instanceof Error ? error.message : '保存失败，请稍后重试'
        ElMessage.error(errorMessage)
      } finally {
        submitLoading.value = false
      }
    } else {
      ElMessage.warning('请检查表单填写是否完整')
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-edit-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}


.profile-form-card {
  border-radius: 12px;
  
  :deep(.el-form) {
    max-width: 800px;
    margin: 0 auto;
  }
  
  :deep(.el-divider) {
    margin: 32px 0 24px;
    
    .el-divider__text {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.avatar-upload-wrapper {
  .avatar-uploader {
    :deep(.el-upload) {
      border: 2px dashed #dcdfe6;
      border-radius: 50%;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;
      
      &:hover {
        border-color: #409eff;
      }
    }
  }
  
  .avatar-preview {
    width: 100px;
    height: 100px;
    display: block;
  }
  
  .avatar-placeholder {
    width: 100px;
    height: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #8c939d;
    
    .upload-text {
      font-size: 12px;
    }
  }
  
  .avatar-tips {
    margin-top: 12px;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #909399;
  }
}

.field-tips {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  
  .form-action-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 6px;
    border: 1px solid #dcdfe6 !important;
    background: #fff !important;
    color: #606266 !important;
    font-weight: 400;
    font-size: 14px;
    box-shadow: none;
    transition: all 0.2s ease;
    min-width: 120px;

    &:hover {
      border-color: #c0c4cc !important;
      color: #606266 !important;
      background: #fff !important;
    }

    &:focus {
      border-color: #c0c4cc !important;
      color: #606266 !important;
      background: #fff !important;
    }

    &:active {
      border-color: #c0c4cc !important;
      color: #606266 !important;
      background: #fff !important;
    }

    &.is-loading {
      border-color: #dcdfe6 !important;
      background: #fff !important;
      color: #606266 !important;
    }
  }
}

@media (max-width: 768px) {
  :deep(.el-form) {
    .el-form-item__label {
      text-align: left !important;
    }
  }
}
</style>

