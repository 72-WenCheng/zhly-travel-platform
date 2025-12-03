<template>
  <div class="map-display-container">
    <div :id="mapId" class="map-display"></div>
    <div v-if="loading" class="map-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>地图加载中...</span>
    </div>
    <div v-if="error" class="map-error">
      <el-icon><Warning /></el-icon>
      <span>{{ error }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Warning } from '@element-plus/icons-vue'

interface Props {
  longitude?: number
  latitude?: number
  address?: string
  name?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  longitude: undefined,
  latitude: undefined,
  address: '',
  name: '',
  height: '300px'
})

const loading = ref(false)
const error = ref('')
const mapId = ref(`map-display-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`)

let map: any = null
let marker: any = null
let amapKey = ''
let amapSecurityCode = ''

// 获取高德地图API Key和安全密钥
const getAmapKey = async () => {
  try {
    const envKey = import.meta.env.VITE_AMAP_KEY
    const envSecurityCode = import.meta.env.VITE_AMAP_SECURITY_CODE
    
    if (envKey && envKey !== 'your-amap-api-key') {
      amapKey = envKey
      if (envSecurityCode && envSecurityCode !== 'your-security-code') {
        amapSecurityCode = envSecurityCode
      }
      return
    }
    
    amapKey = envKey || 'your-amap-api-key'
    amapSecurityCode = envSecurityCode || ''
  } catch (error) {
    console.error('获取高德地图Key失败:', error)
    amapKey = import.meta.env.VITE_AMAP_KEY || 'your-amap-api-key'
    amapSecurityCode = import.meta.env.VITE_AMAP_SECURITY_CODE || ''
  }
}

// 加载高德地图JS API
const loadAmapScript = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    if ((window as any).AMap) {
      resolve()
      return
    }

    if (!amapKey || amapKey === 'your-amap-api-key') {
      error.value = '高德地图API Key未配置'
      reject(new Error('高德地图API Key未配置'))
      return
    }

    if (amapSecurityCode) {
      ;(window as any)._AMapSecurityConfig = {
        securityJsCode: amapSecurityCode
      }
    }

    const callbackName = `initAmapDisplay_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    
    ;(window as any)[callbackName] = () => {
      resolve()
      delete (window as any)[callbackName]
    }

    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}&callback=${callbackName}&log=0`
    script.async = true
    script.onerror = () => {
      delete (window as any)[callbackName]
      error.value = '加载高德地图API失败'
      reject(new Error('加载高德地图API失败'))
    }

    document.head.appendChild(script)
  })
}

// 初始化地图
const initMap = async () => {
  if (!props.longitude || !props.latitude) {
    error.value = '缺少位置信息'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await getAmapKey()
    await loadAmapScript()

    const AMap = (window as any).AMap
    if (!AMap) {
      error.value = '高德地图API加载失败'
      return
    }

    // 创建地图实例
    map = new AMap.Map(mapId.value, {
      zoom: 15,
      center: [props.longitude, props.latitude],
      mapStyle: 'amap://styles/normal',
      viewMode: '2D'
    })

    // 添加标记
    marker = new AMap.Marker({
      position: [props.longitude, props.latitude],
      title: props.name || '景点位置',
      icon: new AMap.Icon({
        size: new AMap.Size(32, 32),
        image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png'
      })
    })

    map.add(marker)

    // 添加信息窗口
    if (props.name || props.address) {
      const infoWindow = new AMap.InfoWindow({
        content: `<div style="padding: 8px;">
          <div style="font-weight: bold; margin-bottom: 4px;">${props.name || '景点位置'}</div>
          <div style="font-size: 12px; color: #666;">${props.address || ''}</div>
        </div>`,
        offset: new AMap.Pixel(0, -30)
      })
      
      marker.on('click', () => {
        infoWindow.open(map, marker.getPosition())
      })
    }

    loading.value = false
  } catch (err: any) {
    console.error('初始化地图失败:', err)
    error.value = err.message || '地图加载失败'
    loading.value = false
  }
}

// 监听位置变化
watch(() => [props.longitude, props.latitude], ([newLng, newLat], [oldLng, oldLat]) => {
  if (newLng !== oldLng || newLat !== oldLat) {
    if (map && marker && newLng && newLat) {
      const newPosition = [newLng, newLat]
      marker.setPosition(newPosition)
      map.setCenter(newPosition)
    } else if (newLng && newLat) {
      initMap()
    }
  }
}, { immediate: false })

onMounted(() => {
  if (props.longitude && props.latitude) {
    initMap()
  } else {
    error.value = '缺少位置信息'
  }
})

onUnmounted(() => {
  if (map) {
    map.destroy()
    map = null
    marker = null
  }
})
</script>

<style lang="scss" scoped>
.map-display-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 300px;

  .map-display {
    width: 100%;
    height: 100%;
    min-height: 300px;
    border-radius: 8px;
    overflow: hidden;
  }

  .map-loading,
  .map-error {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    color: #909399;
    font-size: 14px;

    .el-icon {
      font-size: 32px;
      
      &.is-loading {
        animation: rotate 1s linear infinite;
      }
    }
  }

  .map-error {
    color: #f56c6c;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>

