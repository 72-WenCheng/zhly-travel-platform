<template>
  <div class="maintenance-page">
    <div class="maintenance-card">
      <el-icon class="maintenance-icon"><WarningFilled /></el-icon>
      <h1>{{ userPlatformName }}</h1>
      <p class="maintenance-title">系统维护中</p>
      <p class="maintenance-message">{{ maintenanceMessage }}</p>
      <p class="maintenance-contact" v-if="contactMessage">{{ contactMessage }}</p>
      <el-button type="primary" class="back-btn" @click="handleBack">返回登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { WarningFilled } from '@element-plus/icons-vue'
import { useSystemStore } from '@/stores/system'
import { storeToRefs } from 'pinia'

const router = useRouter()
const systemStore = useSystemStore()
const { userPlatformName, maintenanceMessage, contactMessage } = storeToRefs(systemStore)

const handleBack = () => {
  router.push('/')
}
</script>

<style scoped lang="scss">
.maintenance-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef2ff 0%, #f8fafc 100%);
  padding: 24px;
}

.maintenance-card {
  max-width: 520px;
  width: 100%;
  background: #fff;
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(79, 70, 229, 0.15);
  text-align: center;
  border: 1px solid rgba(99, 102, 241, 0.12);
}

.maintenance-icon {
  font-size: 48px;
  color: #f97316;
  margin-bottom: 12px;
}

h1 {
  margin: 0;
  font-size: 26px;
  font-weight: 600;
  color: #1f2937;
}

.maintenance-title {
  font-size: 20px;
  font-weight: 500;
  color: #4b5563;
  margin: 16px 0 8px;
}

.maintenance-message {
  color: #6b7280;
  line-height: 1.7;
  margin-bottom: 16px;
}

.maintenance-contact {
  color: #4c1d95;
  background: rgba(99, 102, 241, 0.08);
  padding: 12px 20px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.back-btn {
  padding: 12px 32px;
}
</style>
















































