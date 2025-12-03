import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath } from 'url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: [
        'vue',
        'vue-router',
        'pinia'
      ],
      dts: true
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: true
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    open: true,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8070',
        changeOrigin: true,
        ws: true,
        secure: false,
        rewrite: (path) => path,
        configure: (proxy, _options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('代理错误:', err.message)
          })
          proxy.on('proxyReqWs', (proxyReq, req, _socket) => {
            console.log('WebSocket 代理请求:', req.url)
          })
        }
      },
      '/upload': {
        target: 'http://127.0.0.1:8070',
        changeOrigin: true,
        ws: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    // 启用代码压缩
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: false, // 保留 console，方便调试
        drop_debugger: true,
        pure_funcs: ['console.debug'] // 移除 debug 日志
      }
    },
    // 优化代码分割
    rollupOptions: {
      output: {
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: '[ext]/[name]-[hash].[ext]',
        // 手动代码分割，优化加载性能
        manualChunks: (id) => {
          // node_modules 中的包单独打包
          if (id.includes('node_modules')) {
            // Element Plus 单独打包
            if (id.includes('element-plus')) {
              return 'element-plus'
            }
            // Element Plus 图标单独打包（延迟加载）
            if (id.includes('@element-plus/icons-vue')) {
              return 'element-icons'
            }
            // ECharts 单独打包
            if (id.includes('echarts')) {
              return 'echarts'
            }
            // Vue 相关库单独打包
            if (id.includes('vue') || id.includes('pinia') || id.includes('vue-router')) {
              return 'vue-vendor'
            }
            // Axios 单独打包
            if (id.includes('axios')) {
              return 'axios'
            }
            // 其他第三方库
            return 'vendor'
          }
        }
      }
    },
    // 提高构建性能
    chunkSizeWarningLimit: 1000,
    // 启用 CSS 代码分割
    cssCodeSplit: true
  },
  // 优化依赖预构建
  optimizeDeps: {
    include: [
      'vue',
      'vue-router',
      'pinia',
      'element-plus',
      '@element-plus/icons-vue'
    ],
    exclude: ['echarts']
  },
  // 性能优化：启用更快的构建
  esbuild: {
    target: 'es2015',
    // 移除 console.log（生产环境）
    drop: process.env.NODE_ENV === 'production' ? ['console', 'debugger'] : []
  },
  // CSS 预处理器配置，抑制 Sass 弃用警告
  css: {
    preprocessorOptions: {
      scss: {
        // 抑制 Sass 弃用警告
        // 注意：Vue SFC 中使用 @import 是标准用法，这些警告可以安全忽略
        silenceDeprecations: ['import', 'legacy-js-api'],
        // 使用静默模式，不显示弃用警告
        quietDeps: true
      }
    }
  }
})

