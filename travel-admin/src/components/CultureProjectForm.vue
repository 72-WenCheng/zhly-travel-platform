<template>
  <el-dialog
    v-model="visible"
    :title="formTitle"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
    class="modern-edit-dialog"
  >
    <div class="dialog-header-decoration"></div>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      label-position="right"
      class="edit-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="项目名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入项目名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择业务类型" style="width: 100%">
              <el-option label="农产品" :value="1" />
              <el-option label="农家乐" :value="2" />
              <el-option label="电商" :value="3" />
              <el-option label="新农人" :value="4" />
              <el-option label="团建" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="所在地区" prop="region">
            <el-input v-model="formData.region" placeholder="请输入所在地区" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格" prop="price">
            <el-input-number
              v-model="formData.price"
              :min="0"
              :precision="2"
              placeholder="请输入价格"
              style="width: 100%"
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

      <el-form-item label="项目封面" prop="image">
        <el-upload
          class="avatar-uploader"
          :action="uploadUrl"
          :show-file-list="false"
          :on-success="handleImageSuccess"
          :before-upload="beforeImageUpload"
          :headers="uploadHeaders"
        >
          <img v-if="formData.image" :src="formData.image" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">支持jpg/png格式，建议尺寸800x600</div>
      </el-form-item>

      <el-form-item label="项目描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入项目描述"
        />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="0">下架</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose" :disabled="loading" size="large">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading" size="large">
          <el-icon v-if="!loading"><Check /></el-icon>
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Check } from '@element-plus/icons-vue'
import { uploadAvatar } from '@/api/user'
import type { CultureProject } from '@/api/cultureProject'
import type { FormInstance, FormRules } from 'element-plus'

interface Props {
  modelValue: boolean
  project?: CultureProject | null
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  project: null
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'success': [formData: CultureProject]
  'loading': [loading: boolean]
}>()

const formRef = ref<FormInstance>()
const loading = ref(false)
const visible = ref(props.modelValue)

const formData = reactive<CultureProject>({
  name: '',
  type: 1,
  region: '',
  price: 0,
  contactPerson: '',
  contactPhone: '',
  address: '',
  image: '',
  description: '',
  status: 1
})

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  region: [{ required: true, message: '请输入所在地区', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
})

const formTitle = computed(() => {
  return props.project ? '编辑文旅项目' : '添加文旅项目'
})

const uploadUrl = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL}/api/upload/avatar`
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 监听 dialog 显示/隐藏
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.project) {
    // 编辑模式，填充数据
    Object.assign(formData, props.project)
  } else if (val) {
    // 新增模式，重置表单
    resetForm()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 图片上传成功
const handleImageSuccess = (response: any) => {
  if (response.code === 200 && response.data) {
    formData.image = response.data.url || response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 图片上传前校验
const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    name: '',
    type: 1,
    region: '',
    price: 0,
    contactPerson: '',
    contactPhone: '',
    address: '',
    image: '',
    description: '',
    status: 1
  })
  formRef.value?.clearValidate()
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      emit('loading', true)
      const formDataCopy = { ...formData }
      emit('success', formDataCopy)
    }
  })
}

// 重置loading状态
const resetLoading = () => {
  loading.value = false
  emit('loading', false)
}

// 暴露方法供父组件调用
defineExpose({
  resetForm,
  handleSubmit,
  resetLoading
})
</script>

<style lang="scss" scoped>
:deep(.modern-edit-dialog) {
  .el-dialog {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    
    .el-dialog__header {
      padding: 20px 24px;
      background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
      border-bottom: 1px solid #f0f0f0;
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
      
      .el-dialog__headerbtn {
        top: 18px;
        right: 18px;
        
        .el-dialog__close {
          font-size: 20px;
          color: #909399;
          
          &:hover {
            color: #667eea;
          }
        }
      }
    }
    
    .el-dialog__body {
      padding: 24px;
      max-height: 70vh;
      overflow-y: auto;
    }
    
    .el-dialog__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #f0f0f0;
      background: #fafafa;
    }
  }
}

.dialog-header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  margin-bottom: 20px;
}

.edit-form {
  .el-form-item {
    margin-bottom: 22px;
    
    .el-form-item__label {
      font-weight: 500;
      color: #606266;
    }
    
    .el-input,
    .el-select,
    .el-textarea,
    .el-input-number {
      .el-input__wrapper,
      .el-select__wrapper {
        border-radius: 8px;
        transition: all 0.3s;
        
        &:hover {
          box-shadow: 0 0 0 1px #c0c4cc inset;
        }
      }
    }
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 2px dashed #dcdfe6;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: #667eea;
      background: rgba(102, 126, 234, 0.05);
    }
  }

  .avatar {
    width: 178px;
    height: 178px;
    display: block;
    border-radius: 6px;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: color 0.3s;
  }
  
  &:hover .avatar-uploader-icon {
    color: #667eea;
  }
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .el-button {
    padding: 12px 32px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s;
    
    &.el-button--primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      
      &:hover {
        background: linear-gradient(135deg, #5568d3 0%, #6a4192 100%);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
    
    &:not(.el-button--primary) {
      &:hover {
        border-color: #667eea;
        color: #667eea;
      }
    }
  }
}
</style>





