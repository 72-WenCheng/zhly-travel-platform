<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon :size="iconSize">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="empty-text">{{ text }}</div>
    <div v-if="description" class="empty-description">{{ description }}</div>
    <div v-if="showAction" class="empty-action">
      <slot name="action">
        <el-button type="primary" @click="handleAction">
          {{ actionText }}
        </el-button>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  text?: string
  description?: string
  icon?: string
  iconSize?: number
  showAction?: boolean
  actionText?: string
}

interface Emits {
  (e: 'action'): void
}

const props = withDefaults(defineProps<Props>(), {
  text: '暂无数据',
  description: '',
  icon: 'Box',
  iconSize: 64,
  showAction: false,
  actionText: '立即添加'
})

const emit = defineEmits<Emits>()

// 处理操作
const handleAction = () => {
  emit('action')
}
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  
  .empty-icon {
    margin-bottom: 16px;
    color: #c0c4cc;
  }
  
  .empty-text {
    font-size: 16px;
    color: #606266;
    margin-bottom: 8px;
  }
  
  .empty-description {
    font-size: 14px;
    color: #909399;
    margin-bottom: 24px;
    line-height: 1.5;
  }
  
  .empty-action {
    margin-top: 16px;
  }
}
</style>



















