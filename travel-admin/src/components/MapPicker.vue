<template>
  <el-dialog
    v-model="visible"
    title="地图选择位置"
    width="80%"
    :close-on-click-modal="false"
    @closed="handleDialogClosed"
  >
    <div class="map-picker-container">
      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索地点..."
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 地图容器 -->
      <div id="map-container" class="map-container"></div>

      <!-- 底部信息 -->
      <div class="map-info">
        <div class="info-item">
          <span class="label">地址：</span>
          <span class="value">{{ selectedAddress || '请在地图上点击选择位置' }}</span>
        </div>
        <div v-if="selectedLocation" class="info-item">
          <span class="label">坐标：</span>
          <span class="value">经度: {{ selectedLocation.lng.toFixed(6) }}, 纬度: {{ selectedLocation.lat.toFixed(6) }}</span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :disabled="!selectedLocation" @click="handleConfirm">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

interface Location {
  lng: number
  lat: number
}

interface Props {
  modelValue: boolean
  defaultCity?: string
  defaultLocation?: Location
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm', data: { location: Location; address: string; province?: string; city?: string }): void
}

const props = withDefaults(defineProps<Props>(), {
  defaultCity: '',
  defaultLocation: undefined
})

const emit = defineEmits<Emits>()

const visible = ref(false)
const searchKeyword = ref('')
const selectedLocation = ref<Location | null>(null)
const selectedAddress = ref('')
const selectedProvince = ref('')
const selectedCity = ref('')

let map: any = null
let marker: any = null
let geocoder: any = null
let amapKey = ''
let amapSecurityCode = '' // 安全密钥

// 监听dialog显示
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    nextTick(() => {
      initMap()
    })
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

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
      
      if (amapKey) {
        return
      }
    } catch (e) {
      // 如果后端接口不存在，继续使用环境变量或默认值
    }
    
    // 使用环境变量或提示用户配置
    amapKey = envKey || 'your-amap-api-key'
    if (amapKey === 'your-amap-api-key') {
      console.warn('请配置高德地图API Key: VITE_AMAP_KEY环境变量或系统配置')
      ElMessage.warning('地图功能需要配置高德地图API Key')
    }
  } catch (error) {
    console.error('获取高德地图Key失败:', error)
    amapKey = import.meta.env.VITE_AMAP_KEY || 'your-amap-api-key'
    amapSecurityCode = import.meta.env.VITE_AMAP_SECURITY_CODE || ''
  }
}

// 加载高德地图JS API
const loadAmapScript = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    // 如果已经加载过，直接返回
    if ((window as any).AMap) {
      resolve()
      return
    }

    // 检查API Key
    if (!amapKey || amapKey === 'your-amap-api-key') {
      ElMessage.error('高德地图API Key未配置，请在环境变量中设置VITE_AMAP_KEY')
      reject(new Error('高德地图API Key未配置'))
      return
    }

    // 如果配置了安全密钥，需要在加载脚本前设置全局配置
    if (amapSecurityCode) {
      ;(window as any)._AMapSecurityConfig = {
        securityJsCode: amapSecurityCode
      }
    }

    // 创建唯一回调函数名
    const callbackName = `initAmap_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    
    // 设置全局回调
    ;(window as any)[callbackName] = () => {
      resolve()
      delete (window as any)[callbackName]
    }

    // 创建并添加script标签
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}&callback=${callbackName}&log=0`
    script.async = true
    script.onerror = () => {
      delete (window as any)[callbackName]
      reject(new Error('加载高德地图API失败，请检查网络连接和API Key'))
    }

    document.head.appendChild(script)
  })
}

// 加载高德地图插件
const loadAmapPlugins = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    const AMap = (window as any).AMap
    if (!AMap) {
      reject(new Error('高德地图API未加载'))
      return
    }

    // 加载需要的插件
    AMap.plugin(['AMap.Geocoder', 'AMap.PlaceSearch'], () => {
      resolve()
    })
  })
}

// 初始化地图
const initMap = async () => {
  try {
    await getAmapKey()
    await loadAmapScript()
    await loadAmapPlugins()

    const AMap = (window as any).AMap
    if (!AMap) {
      ElMessage.error('高德地图API加载失败')
      return
    }

    // 创建地图实例
    const center = props.defaultLocation 
      ? [props.defaultLocation.lng, props.defaultLocation.lat]
      : props.defaultCity 
        ? await getCityCenter(props.defaultCity)
        : [116.397428, 39.90923] // 默认北京

    map = new AMap.Map('map-container', {
      zoom: props.defaultLocation ? 15 : 10,
      center: center,
      mapStyle: 'amap://styles/normal'
    })

    // 创建地理编码实例（需要先加载插件）
    geocoder = new AMap.Geocoder({
      city: props.defaultCity || '全国'
    })

    // 如果有默认位置，显示标记
    if (props.defaultLocation) {
      addMarker(props.defaultLocation.lng, props.defaultLocation.lat)
      reverseGeocode(props.defaultLocation.lng, props.defaultLocation.lat)
    }

    // 地图点击事件
    map.on('click', (e: any) => {
      const { lng, lat } = e.lnglat
      addMarker(lng, lat)
      reverseGeocode(lng, lat)
    })
  } catch (error) {
    console.error('初始化地图失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接和API Key配置')
  }
}

// 获取城市中心点
const getCityCenter = async (city: string): Promise<number[]> => {
  try {
    const response = await request.get('/third-party/amap/geocode', {
      params: { address: city }
    })
    
    if (response.code === 200 && response.data) {
      const lng = Number(response.data.longitude)
      const lat = Number(response.data.latitude)
      if (!isNaN(lng) && !isNaN(lat)) {
        return [lng, lat]
      }
    }
  } catch (error) {
    console.error('获取城市中心点失败:', error)
  }
  
  // 默认返回北京坐标
  return [116.397428, 39.90923]
}

// 添加标记
const addMarker = (lng: number, lat: number) => {
  const AMap = (window as any).AMap
  
  // 移除旧标记
  if (marker) {
    map.remove(marker)
  }

  // 创建新标记
  marker = new AMap.Marker({
    position: [lng, lat],
    draggable: true,
    cursor: 'move'
  })

  map.add(marker)
  map.setCenter([lng, lat])

  // 标记拖拽事件
  marker.on('dragend', (e: any) => {
    const { lng, lat } = e.target.getPosition()
    reverseGeocode(lng, lat)
  })

  selectedLocation.value = { lng, lat }
}

// 逆地理编码（根据坐标获取地址）
const reverseGeocode = async (lng: number, lat: number) => {
  const AMap = (window as any).AMap
  if (!AMap) {
    return
  }

  if (!geocoder) {
    // 确保插件已加载，然后创建geocoder
    try {
      await loadAmapPlugins()
      geocoder = new AMap.Geocoder({
        city: props.defaultCity || '全国'
      })
    } catch (error) {
      console.error('加载地理编码插件失败:', error)
      return
    }
  }

  geocoder.getAddress([lng, lat], (status: string, result: any) => {
    if (status === 'complete' && result.info === 'OK') {
      const regeocode = result.regeocode
      selectedAddress.value = regeocode.formattedAddress || ''
      
      // 提取省市区信息
      const addressComponent = regeocode.addressComponent
      if (addressComponent) {
        selectedProvince.value = addressComponent.province || ''
        // 优先使用city，如果没有city则使用district
        selectedCity.value = addressComponent.city || addressComponent.district || props.defaultCity || ''
        
        // 如果没有省份但有城市，从城市名称中提取省份（例如：北京市 -> 北京市）
        if (!selectedProvince.value && selectedCity.value) {
          if (selectedCity.value.includes('市')) {
            selectedProvince.value = selectedCity.value.split('市')[0] + '市'
          } else if (selectedCity.value.includes('省')) {
            selectedProvince.value = selectedCity.value.split('省')[0] + '省'
          }
        }
      }
    } else {
      console.warn('逆地理编码失败:', status, result)
      // 如果逆地理编码失败，至少设置坐标
      selectedAddress.value = `经度: ${lng.toFixed(6)}, 纬度: ${lat.toFixed(6)}`
    }
  })
}

// 搜索地点
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  try {
    const AMap = (window as any).AMap
    if (!AMap) {
      ElMessage.error('地图未初始化')
      return
    }

    // 确保插件已加载
    await loadAmapPlugins()

    // 使用高德地图POI搜索
    const placeSearch = new AMap.PlaceSearch({
      city: props.defaultCity || '全国',
      pageSize: 10,
      // 如果配置了安全密钥，会在全局设置中生效
    })

    placeSearch.search(searchKeyword.value, (status: string, result: any) => {
      if (status === 'complete' && result.poiList && result.poiList.pois.length > 0) {
        const poi = result.poiList.pois[0]
        const location = poi.location
        addMarker(location.lng, location.lat)
        selectedAddress.value = poi.name + (poi.address ? ` - ${poi.address}` : '')
        
        // 提取省市区信息
        if (poi.adname) {
          const parts = poi.adname.split(/\s+/)
          if (parts.length >= 2) {
            selectedProvince.value = parts[0]
            selectedCity.value = parts[1] || parts[0]
          } else if (parts.length === 1) {
            // 如果只有一个部分，尝试解析
            const fullName = parts[0]
            if (fullName.includes('市')) {
              selectedCity.value = fullName
              selectedProvince.value = fullName.split('市')[0] + '市'
            } else if (fullName.includes('省')) {
              selectedProvince.value = fullName.split('省')[0] + '省'
            }
          }
        }
      } else {
        ElMessage.warning('未找到相关地点')
      }
    })
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  }
}

// 确认选择
const handleConfirm = () => {
  if (!selectedLocation.value) {
    ElMessage.warning('请先在地图上选择位置')
    return
  }

  emit('confirm', {
    location: selectedLocation.value,
    address: selectedAddress.value,
    province: selectedProvince.value,
    city: selectedCity.value || props.defaultCity
  })
  
  visible.value = false
}

// 取消
const handleCancel = () => {
  visible.value = false
}

// 对话框关闭时清理
const handleDialogClosed = () => {
  if (map) {
    map.destroy()
    map = null
    marker = null
    geocoder = null
  }
  searchKeyword.value = ''
  selectedLocation.value = null
  selectedAddress.value = ''
  selectedProvince.value = ''
  selectedCity.value = ''
}
</script>

<style lang="scss" scoped>
.map-picker-container {
  .search-box {
    margin-bottom: 16px;
  }

  .map-container {
    width: 100%;
    height: 500px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
  }

  .map-info {
    margin-top: 16px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 4px;

    .info-item {
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        font-weight: 600;
        color: #606266;
        margin-right: 8px;
      }

      .value {
        color: #303133;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

