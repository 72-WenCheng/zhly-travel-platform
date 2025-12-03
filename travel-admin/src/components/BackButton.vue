<template>
  <div class="back-navigation">
    <el-button @click="goBack" class="back-btn" text>
      <el-icon><ArrowLeft /></el-icon>
      <el-icon><ArrowLeft /></el-icon>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => {
  router.back()
}
</script>

<style lang="scss" scoped>
.back-navigation {
  margin-bottom: 16px;
  
  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: 2px;
    padding: 8px 14px;
    font-size: 14px;
    color: #667eea;
    background: white;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
    transition: all 0.2s ease;
    overflow: hidden;
    
    :deep(.el-icon) {
      font-size: 16px;
      line-height: 1;
      animation: conveyorBelt 1.5s ease-in-out infinite;
      
      &:nth-child(1) {
        animation-delay: 0s;
      }
      
      &:nth-child(2) {
        animation-delay: 0.2s;
      }
      
      &:nth-child(3) {
        animation-delay: 0.4s;
      }
    }
    
    @keyframes conveyorBelt {
      0%, 100% {
        transform: translateX(0);
        opacity: 0.6;
      }
      50% {
        transform: translateX(-3px);
        opacity: 1;
      }
    }
    
    &:hover {
      color: white;
      background: #667eea;
      border-color: #667eea;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.25);
      
      :deep(.el-icon) {
        animation: conveyorBeltFast 0.8s ease-in-out infinite;
      }
    }
    
    @keyframes conveyorBeltFast {
      0%, 100% {
        transform: translateX(0);
        opacity: 0.7;
      }
      50% {
        transform: translateX(-4px);
        opacity: 1;
      }
    }
  }
}

// 响应式设计
@media (max-width: 992px) {
  .back-navigation {
    .back-btn {
      font-size: 13px;
      padding: 7px 16px;
    }
  }
}

@media (max-width: 768px) {
  .back-navigation {
    .back-btn {
      font-size: 13px;
      padding: 6px 14px;
    }
  }
}
</style>







