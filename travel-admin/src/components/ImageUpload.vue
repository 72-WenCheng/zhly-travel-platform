<template>
  <div class="image-upload">
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :accept="accept"
      :class="['upload-dragger', { 'has-image': !!imageUrl }]"
      drag
    >
      <div v-if="!imageUrl" class="upload-content">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <div class="upload-text">点击或拖拽上传图片</div>
        <div class="upload-hint">支持 JPG、PNG、GIF 格式，大小不超过 10MB</div>
      </div>
      <div v-else class="image-preview">
        <div class="image-wrapper">
          <img :src="imageUrl" :alt="alt" />
        </div>
        <div class="image-actions">
          <el-button type="primary" size="small" @click.stop="previewImage">
            <el-icon><ZoomIn /></el-icon>
            预览
          </el-button>
          <el-button type="danger" size="small" @click.stop="removeImage">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>
    </el-upload>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="80%">
      <div class="preview-container">
        <img :src="imageUrl" alt="预览图片" class="preview-image" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

interface Props {
  modelValue?: string
  alt?: string
  accept?: string
  maxSize?: number
}

interface Emits {
  (e: 'update:modelValue', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  alt: '上传图片',
  accept: 'image/jpeg,image/png,image/gif,image/webp',
  maxSize: 10 * 1024 * 1024 // 10MB
})

const emit = defineEmits<Emits>()

const userStore = useUserStore()
const previewVisible = ref(false)

// 上传配置
const uploadUrl = computed(() => '/api/upload/image')
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}))

// 图片URL
const imageUrl = computed({
  get: () => props.modelValue || '',
  set: (value) => emit('update:modelValue', value)
})

// 上传前验证
const beforeUpload = (file: File) => {
  // 检查文件类型
  if (!props.accept.split(',').some(type => file.type === type.trim())) {
    ElMessage.error('请上传正确格式的图片文件')
    return false
  }
  
  // 检查文件大小
  if (file.size > props.maxSize) {
    ElMessage.error(`图片大小不能超过 ${formatFileSize(props.maxSize)}`)
    return false
  }
  
  return true
}

// 上传成功
const handleSuccess = (response: any) => {
  if (response.code === 200) {
    // 后端返回的字段是 fileUrl，兼容 url 字段
    imageUrl.value = response.data.fileUrl || response.data.url || ''
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 上传失败
const handleError = (error: any) => {
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败')
}

// 预览图片
const previewImage = () => {
  previewVisible.value = true
}

// 删除图片
const removeImage = () => {
  imageUrl.value = ''
  ElMessage.success('图片已删除')
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style lang="scss" scoped>
.image-upload {
  .upload-dragger {
    width: 100%;
    min-height: 200px;
    border: 2px dashed #d9d9d9;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    transition: border-color 0.3s;
    background: #fff;
    padding: 0;
    
    &:hover {
      border-color: #409eff;
    }
    
    &.has-image {
      padding: 12px;
      min-height: auto;
    }
    
    .upload-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: #8c939d;
      
      .upload-icon {
        font-size: 48px;
        margin-bottom: 16px;
      }
      
      .upload-text {
        font-size: 16px;
        margin-bottom: 8px;
      }
      
      .upload-hint {
        font-size: 14px;
        color: #c0c4cc;
      }
    }
    
    .image-preview {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 12px;
      
      .image-wrapper {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 6px;
        background: #fff;
        padding: 8px;
        border: 1px solid #ecf0f3;
        min-height: 180px;
        
        img {
          width: 100%;
          max-height: 220px;
          object-fit: contain;
          border-radius: 4px;
        }
      }
      
      .image-actions {
        display: flex;
        justify-content: center;
        gap: 12px;
      }
    }
  }
  
  .preview-container {
    text-align: center;
    
    .preview-image {
      max-width: 100%;
      max-height: 70vh;
      object-fit: contain;
    }
  }
}
</style>



















