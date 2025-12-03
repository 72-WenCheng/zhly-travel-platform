<template>
  <el-tag :type="tagType" :effect="effect">
    {{ statusText }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: number
  type?: 'user' | 'plan' | 'attraction' | 'project' | 'ai'
  effect?: 'dark' | 'light' | 'plain'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'user',
  effect: 'light'
})

// 状态文本映射
const statusTextMap: Record<string, Record<number, string>> = {
  user: {
    0: '禁用',
    1: '正常',
    2: 'VIP'
  },
  plan: {
    0: '草稿',
    1: '已发布',
    2: '已下架',
    3: '待审核',
    4: '已驳回'
  },
  attraction: {
    0: '已关闭',
    1: '正常',
    2: '维护中'
  },
  project: {
    0: '已关闭',
    1: '正常',
    2: '维护中'
  },
  ai: {
    0: '失败',
    1: '成功',
    2: '处理中'
  }
}

// 状态标签类型映射
const tagTypeMap: Record<string, Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'>> = {
  user: {
    0: 'danger',
    1: 'success',
    2: 'warning'
  },
  plan: {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'info',
    4: 'danger'
  },
  attraction: {
    0: 'danger',
    1: 'success',
    2: 'warning'
  },
  project: {
    0: 'danger',
    1: 'success',
    2: 'warning'
  },
  ai: {
    0: 'danger',
    1: 'success',
    2: 'warning'
  }
}

// 状态文本
const statusText = computed(() => {
  return statusTextMap[props.type]?.[props.status] || '未知'
})

// 标签类型
const tagType = computed((): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  return tagTypeMap[props.type]?.[props.status] || 'info'
})
</script>











