<template>
  <div class="culture-center">
    <!-- 页面标题 -->
    <el-card class="page-header">
      <h1>
        <el-icon><Shop /></el-icon>
        文旅体验中心
      </h1>
    </el-card>

    <el-card class="page-card">
      <el-row :gutter="20" class="entry-row">
        <el-col :xs="24" :sm="12" :md="6">
          <!-- 我的预约：文化体验 -->
          <el-card class="entry-card" shadow="never" @click="go('/home/user/culture/bookings?mode=experience')">
            <div class="entry-icon primary">
              <span class="icon-base icon-calendar" aria-hidden="true"></span>
            </div>
            <div class="entry-title">我的预约</div>
            <div class="entry-desc">文化体验预约记录，随时查看进度</div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="entry-card" shadow="never" @click="go('/home/user/culture/orders')">
            <div class="entry-icon success">
              <span class="icon-base icon-list" aria-hidden="true"></span>
            </div>
            <div class="entry-title">我的订单</div>
            <div class="entry-desc">农特产品 / 特色周边订单，支付物流一目了然</div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <!-- 我的预定：农家乐 / 民宿 -->
          <el-card class="entry-card" shadow="never" @click="go('/home/user/culture/bookings?mode=stay')">
            <div class="entry-icon warning">
              <span class="icon-base icon-ticket" aria-hidden="true"></span>
            </div>
            <div class="entry-title">我的预定</div>
            <div class="entry-desc">农家乐 / 民宿预定行程，入住信息一键查看</div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="entry-card" shadow="never" @click="go('/home/user/culture/applications')">
            <div class="entry-icon info">
              <span class="icon-base icon-doc" aria-hidden="true"></span>
            </div>
            <div class="entry-title">我的申请</div>
            <div class="entry-desc">政策对接 / 合作申请进度，实时跟进</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="page-card sponsor-card" shadow="never">
      <div class="sponsor-header">合作品牌</div>
      <div class="sponsor-subtitle">与多家文旅品牌与机构深度合作</div>
      <div class="sponsor-list">
        <div
          v-for="item in sponsors"
          :key="item.name"
          class="sponsor-item"
        >
          <div class="sponsor-logo">{{ item.short }}</div>
          <div class="sponsor-name">{{ item.name }}</div>
        </div>
      </div>
    </el-card>

  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Shop } from '@element-plus/icons-vue'

const router = useRouter()
const go = (path: string) => {
  router.push(path)
}

const sponsors = [
  { name: '山城文旅集团', short: 'SC' },
  { name: '乡村度假联合会', short: 'RV' },
  { name: '非遗体验工坊', short: 'FH' },
  { name: '农特产品联盟', short: 'NT' },
  { name: '民宿合作伙伴', short: 'INN' },
  { name: '政策服务中心', short: 'POL' }
]

</script>

<style scoped lang="scss">
.culture-center {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;

  h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin: 0;

    .el-icon {
      font-size: 28px;
      color: #409eff;
    }
  }
}

.page-card {
  border-radius: 12px;
  padding: 32px 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 28px;
  color: #667eea;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
}

.subtitle {
  color: #6b7280;
  font-size: 13px;
}

.entry-row {
  margin-top: 0;
}

.entry-card {
  border-radius: 12px;
  cursor: pointer;
  transition: none;
  padding: 28px 24px;
  height: 100%;
  min-height: 180px;
  display: flex;
  flex-direction: column;
  border: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transform: none;
    border-color: #f0f0f0;
  }
}

.entry-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  background: #f5f6f8;
  color: #5f6368;
  font-size: 28px;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.entry-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.entry-desc {
  margin-top: 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
  flex: 1;
}

.icon-base {
  display: inline-block;
  width: 26px;
  height: 26px;
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.icon-calendar {
  background-image: url("data:image/svg+xml,%3Csvg width='32' height='32' viewBox='0 0 32 32' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Crect x='5' y='8' width='22' height='18' rx='2' stroke='%235f6368' stroke-width='2'/%3E%3Cpath d='M11 6V10' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3Cpath d='M21 6V10' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3Cpath d='M5 14H27' stroke='%235f6368' stroke-width='2'/%3E%3C/svg%3E");
}

.icon-list {
  background-image: url("data:image/svg+xml,%3Csvg width='32' height='32' viewBox='0 0 32 32' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Crect x='7' y='8' width='18' height='16' rx='2' stroke='%235f6368' stroke-width='2'/%3E%3Cpath d='M11 12H21' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3Cpath d='M11 16H21' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3Cpath d='M11 20H17' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3C/svg%3E");
}

.icon-ticket {
  background-image: url("data:image/svg+xml,%3Csvg width='32' height='32' viewBox='0 0 32 32' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Crect x='6' y='9' width='20' height='14' rx='2' stroke='%235f6368' stroke-width='2'/%3E%3Cpath d='M12 9V23' stroke='%235f6368' stroke-width='2' stroke-linecap='round' stroke-dasharray='3 2'/%3E%3Cpath d='M20 13H24' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3Cpath d='M20 17H24' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3C/svg%3E");
}

.icon-doc {
  background-image: url("data:image/svg+xml,%3Csvg width='32' height='32' viewBox='0 0 32 32' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M9 6C7.89543 6 7 6.89543 7 8V24C7 25.1046 7.89543 26 9 26H23C24.1046 26 25 25.1046 25 24V12.8284C25 12.2979 24.7893 11.7893 24.4142 11.4142L18.5858 5.58579C18.2107 5.21071 17.7021 5 17.1716 5H9Z' stroke='%235f6368' stroke-width='2'/%3E%3Cpath d='M17 6V12C17 12.5523 17.4477 13 18 13H24' stroke='%235f6368' stroke-width='2'/%3E%3Cpath d='M11 17H21' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3Cpath d='M11 21H17' stroke='%235f6368' stroke-width='2' stroke-linecap='round'/%3E%3C/svg%3E");
}

.sponsor-card {
  margin-top: 24px;
  text-align: center;
  padding: 32px 24px;
}

.sponsor-header {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.sponsor-subtitle {
  margin-top: 0;
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 24px;
}

.sponsor-list {
  margin-top: 0;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 24px;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.sponsor-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  transition: none;

  &:hover {
    background: #fafafa;
    border-color: #f0f0f0;
    transform: none;
    box-shadow: none;
  }
}

.sponsor-logo {
  width: 80px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #ffffff, #f3f4f6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
  border: 1px solid #e5e7eb;
}

.sponsor-name {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

</style>

