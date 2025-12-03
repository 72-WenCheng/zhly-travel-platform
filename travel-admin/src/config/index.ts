// 应用配置
export const config = {
  // API基础URL
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8070/api',
  
  // 应用信息
  appTitle: import.meta.env.VITE_APP_TITLE || '旅游系统',
  appVersion: '1.0.0',
  
  // 分页配置
  pagination: {
    defaultPageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  
  // 上传配置
  upload: {
    maxSize: 10 * 1024 * 1024, // 10MB
    allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  },
  
  // 地图配置
  map: {
    amapKey: 'your-amap-key',
    defaultCenter: [116.397428, 39.90923], // 北京
    defaultZoom: 10
  }
}



















