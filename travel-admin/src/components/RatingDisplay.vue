<template>
  <div class="rating-display">
    <el-rate
      v-model="ratingValue"
      :max="max"
      :disabled="disabled"
      :allow-half="allowHalf"
      :show-score="showScore"
      :show-text="showText"
      :texts="texts"
      :score-template="scoreTemplate"
      @change="handleChange"
    />
    <span v-if="showCount" class="rating-count">({{ count }})</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: number
  max?: number
  disabled?: boolean
  allowHalf?: boolean
  showScore?: boolean
  showText?: boolean
  showCount?: boolean
  count?: number
  texts?: string[]
  scoreTemplate?: string
}

interface Emits {
  (e: 'update:modelValue', value: number): void
  (e: 'change', value: number): void
}

const props = withDefaults(defineProps<Props>(), {
  max: 5,
  disabled: true,
  allowHalf: true,
  showScore: true,
  showText: false,
  showCount: false,
  count: 0,
  texts: () => ['极差', '失望', '一般', '满意', '惊喜'],
  scoreTemplate: '{value}'
})

const emit = defineEmits<Emits>()

// 评分值
const ratingValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 处理评分变化
const handleChange = (value: number) => {
  emit('change', value)
}
</script>

<style lang="scss" scoped>
.rating-display {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .rating-count {
    font-size: 14px;
    color: #909399;
  }
}
</style>



















