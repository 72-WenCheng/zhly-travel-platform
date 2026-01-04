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
          <span class="label">详细地址：</span>
          <span class="value">{{ selectedAddress || '请在地图上点击选择位置' }}</span>
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
    // 如果已经加载过，直接返回
    if ((window as any).AMap) {
      resolve()
      return
    }

    // 检查API Key
    if (!amapKey || amapKey === 'your-amap-api-key') {
      const errorMsg = '高德地图API Key未配置，请在前端.env文件中配置VITE_AMAP_KEY或在后端系统配置中添加amap.api.key'
      console.error('❌', errorMsg)
      ElMessage.error('高德地图API Key未配置，请在环境变量中设置VITE_AMAP_KEY')
      reject(new Error(errorMsg))
      return
    }
    
    console.log('✅ 使用高德地图API Key:', amapKey.substring(0, 10) + '...')

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

// 逆地理编码（根据坐标获取地址）- 使用后端API
const reverseGeocode = async (lng: number, lat: number) => {
  try {
    // 优先使用后端API获取详细地址（使用Web服务API Key，更准确）
    const response = await request.get('/third-party/reverse-geocode', {
      params: {
        longitude: lng,
        latitude: lat
      }
    })
    
    if (response.code === 200 && response.data) {
      const data = response.data
      
      // 获取格式化地址
      if (data.formattedAddress) {
        selectedAddress.value = data.formattedAddress
      } else {
        // 如果没有格式化地址，手动拼接
        const parts = []
        if (data.province) parts.push(data.province)
        if (data.city) parts.push(data.city)
        if (data.district) parts.push(data.district)
        selectedAddress.value = parts.length > 0 ? parts.join('') : '地址解析中...'
      }
      
      // 提取省市区信息
      selectedProvince.value = data.province || ''
      selectedCity.value = data.city || data.district || props.defaultCity || ''
      
      return
    }
  } catch (error) {
    console.warn('后端逆地理编码失败，尝试使用前端API:', error)
  }
  
  // 降级方案：使用前端高德地图API
  const AMap = (window as any).AMap
  if (!AMap) {
    selectedAddress.value = '无法获取地址信息，请重新选择位置'
    ElMessage.warning('地图API未加载，请刷新页面重试')
    return
  }

  if (!geocoder) {
    try {
      await loadAmapPlugins()
      geocoder = new AMap.Geocoder({
        city: props.defaultCity || '全国'
      })
    } catch (error) {
      console.error('加载地理编码插件失败:', error)
      selectedAddress.value = '无法获取地址信息，请重新选择位置'
      return
    }
  }

  geocoder.getAddress([lng, lat], (status: string, result: any) => {
    if (status === 'complete' && result.info === 'OK') {
      const regeocode = result.regeocode
      const addressComponent = regeocode.addressComponent
      
      // 构建完整的详细地址
      let fullAddress = ''
      if (addressComponent) {
        // 拼接完整地址：省 + 市 + 区/县 + 街道 + 门牌号
        const parts = []
        if (addressComponent.province) parts.push(addressComponent.province)
        if (addressComponent.city) parts.push(addressComponent.city)
        if (addressComponent.district) parts.push(addressComponent.district)
        if (addressComponent.township) parts.push(addressComponent.township)
        if (addressComponent.street) parts.push(addressComponent.street)
        if (addressComponent.streetNumber) parts.push(addressComponent.streetNumber)
        
        // 如果有完整地址组件，使用拼接的地址
        if (parts.length > 0) {
          fullAddress = parts.join('')
        }
        
        // 如果有格式化地址，优先使用（通常更准确）
        if (regeocode.formattedAddress) {
          fullAddress = regeocode.formattedAddress
        }
        
        // 提取省市区信息
        selectedProvince.value = addressComponent.province || ''
        selectedCity.value = addressComponent.city || addressComponent.district || props.defaultCity || ''
        
        // 如果没有省份但有城市，从城市名称中提取省份
        if (!selectedProvince.value && selectedCity.value) {
          if (selectedCity.value.includes('市')) {
            selectedProvince.value = selectedCity.value.split('市')[0] + '市'
          } else if (selectedCity.value.includes('省')) {
            selectedProvince.value = selectedCity.value.split('省')[0] + '省'
          }
        }
      } else if (regeocode.formattedAddress) {
        // 如果没有地址组件，使用格式化地址
        fullAddress = regeocode.formattedAddress
      }
      
      selectedAddress.value = fullAddress || '地址解析中...'
    } else {
      console.warn('逆地理编码失败:', status, result)
      // 如果逆地理编码失败，提示用户重新选择
      selectedAddress.value = '无法获取地址信息，请重新选择位置'
      ElMessage.warning('无法获取该位置的详细地址，请尝试选择其他位置')
    }
  })
}

// 搜索地点
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  const keyword = searchKeyword.value.trim()

  try {
    // 优先使用后端API进行地理编码（适用于地址、城市、省份等）
    try {
      const geocodeResponse = await request.get('/third-party/amap/geocode', {
        params: { address: keyword }
      })
      
      console.log('地理编码响应:', geocodeResponse)
      
      if (geocodeResponse.code === 200 && geocodeResponse.data) {
        const data = geocodeResponse.data
        if (data.longitude && data.latitude) {
          const lng = Number(data.longitude)
          const lat = Number(data.latitude)
          
          if (!isNaN(lng) && !isNaN(lat)) {
            addMarker(lng, lat)
            // 使用逆地理编码获取详细地址
            await reverseGeocode(lng, lat)
            return
          }
        }
      }
      // 如果地理编码失败，继续尝试其他方法（不抛出错误）
      console.warn('地理编码未找到结果，尝试输入提示API')
    } catch (geocodeError: any) {
      console.warn('地理编码请求失败，尝试输入提示API:', geocodeError)
      
      // 尝试使用输入提示API
      try {
        const tipsResponse = await request.get('/third-party/amap/inputtips', {
          params: { 
            keywords: keyword,
            city: props.defaultCity || '',
            citylimit: false
          }
        })
        
        console.log('输入提示响应:', tipsResponse)
        
        if (tipsResponse.code === 200 && tipsResponse.data && Array.isArray(tipsResponse.data) && tipsResponse.data.length > 0) {
          const tip = tipsResponse.data[0]
          if (tip.location && typeof tip.location === 'string') {
            const locationParts = tip.location.split(',')
            if (locationParts.length === 2) {
              const lng = Number(locationParts[0].trim())
              const lat = Number(locationParts[1].trim())
              if (!isNaN(lng) && !isNaN(lat)) {
                addMarker(lng, lat)
                // 构建地址信息
                let address = tip.name || ''
                if (tip.address) {
                  address += (address ? ' - ' : '') + tip.address
                }
                if (tip.district) {
                  address += (address ? '，' : '') + tip.district
                }
                selectedAddress.value = address || keyword
                await reverseGeocode(lng, lat)
                return
              }
            }
          }
        } else {
          console.warn('输入提示API未返回有效数据')
        }
      } catch (tipsError: any) {
        console.warn('输入提示API也失败，尝试POI搜索:', tipsError)
      }
    }

    // 降级方案：使用后端POI搜索API
    try {
      const poiResponse = await request.get('/third-party/amap/poi', {
        params: {
          keyword: keyword,
          city: props.defaultCity || ''
        }
      })
      
      console.log('POI搜索响应:', poiResponse)
      
      if (poiResponse.code === 200 && poiResponse.data) {
        const data = poiResponse.data
        if (data.longitude && data.latitude) {
          const lng = Number(data.longitude)
          const lat = Number(data.latitude)
          
          if (!isNaN(lng) && !isNaN(lat)) {
            addMarker(lng, lat)
            
            // 使用返回的地址信息
            if (data.formattedAddress) {
              selectedAddress.value = data.formattedAddress
            } else {
              let address = data.name || ''
              if (data.address) {
                address += (address ? ' - ' : '') + data.address
              }
              if (data.adname) {
                address += (address ? '，' : '') + data.adname
              }
              selectedAddress.value = address || keyword
            }
            
            // 提取省市区信息
            if (data.pname) {
              selectedProvince.value = data.pname
            }
            if (data.cityname) {
              selectedCity.value = data.cityname
            } else if (data.adname) {
              selectedCity.value = data.adname
            }
            
            // 使用逆地理编码获取更详细的地址
            await reverseGeocode(lng, lat)
            return
          }
        }
      } else {
        console.warn('POI搜索返回错误:', poiResponse.message || '未知错误')
      }
    } catch (poiError: any) {
      console.warn('后端POI搜索失败:', poiError)
    }
    
    // 如果所有方法都失败，提示用户
    ElMessage.warning('未找到相关地点，请尝试输入更具体的地点名称或地址')
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

