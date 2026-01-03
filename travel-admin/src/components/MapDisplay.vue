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
import { Loading, Warning } from '@element-plus/icons-vue'
import request from '@/utils/request'

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

// 获取高德地图API Key和安全密钥（从后端获取）
const getAmapKey = async () => {
  try {
    // 优先从环境变量获取
    const envKey = import.meta.env.VITE_AMAP_KEY
    const envSecurityCode = import.meta.env.VITE_AMAP_SECURITY_CODE
    
    if (envKey && envKey !== 'your-amap-api-key') {
      amapKey = envKey
      // 如果配置了安全密钥，也加载
      if (envSecurityCode && envSecurityCode !== 'your-security-code') {
        amapSecurityCode = envSecurityCode
      }
      return
    }
    
    // 如果环境变量没有，尝试从后端获取配置
    try {
      const keyResponse = await request.get('/system-config/value/amap.api.key')
      if (keyResponse.code === 200 && keyResponse.data) {
        amapKey = keyResponse.data
      }
      
      // 尝试获取安全密钥
      try {
        const securityResponse = await request.get('/system-config/value/amap.security.code')
        if (securityResponse.code === 200 && securityResponse.data) {
          amapSecurityCode = securityResponse.data
        }
      } catch (e) {
        // 安全密钥可选
      }
      
      if (amapKey && amapKey !== 'your-amap-api-key') {
        return
      }
    } catch (e) {
      // 如果后端接口不存在，继续使用环境变量或默认值
      console.warn('从后端获取API Key失败:', e)
    }
    
    // 如果都没有配置，使用一个默认的Web端(JS API) Key（用于前端地图显示）
    // 注意：这是Web端Key，不是Web服务Key
    if (!amapKey || amapKey === 'your-amap-api-key') {
      // 使用一个可能可用的默认Key（如果之前配置过）
      amapKey = '4c1487f8f9b1c39bb406fdf78c214c76' // 默认Web端Key，如果不可用需要替换
      console.warn('⚠️ 使用默认高德地图API Key，如果地图无法显示，请：')
      console.warn('   1. 在前端.env文件中配置 VITE_AMAP_KEY=你的Web端Key')
      console.warn('   2. 或在后端系统配置中添加 amap.api.key 配置项')
      console.warn('   3. 确保使用的是Web端(JS API)类型的Key，不是Web服务Key')
    }
  } catch (error) {
    console.error('获取高德地图Key失败:', error)
    // 降级方案：使用默认Key
    amapKey = import.meta.env.VITE_AMAP_KEY || '4c1487f8f9b1c39bb406fdf78c214c76'
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
      const errorMsg = '高德地图API Key未配置，请在前端.env文件中配置VITE_AMAP_KEY或在后端系统配置中添加amap.api.key'
      error.value = errorMsg
      console.error('❌', errorMsg)
      console.error('当前amapKey值:', amapKey)
      reject(new Error(errorMsg))
      return
    }
    
    console.log('✅ 使用高德地图API Key:', amapKey.substring(0, 10) + '...')

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

