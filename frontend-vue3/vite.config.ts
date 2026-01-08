import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0', // 允许局域网访问（方便以后用手机测试）
    port: 5173,      // 前端端口
    proxy: {
      // 匹配所有以 /api 开头的请求
      '/api': {
        target: 'http://localhost:8080', // 你的后端 Spring Boot 地址
        changeOrigin: true,              // 允许跨域
        // 如果后端接口本身就带有 /api 前缀，则不需要 rewrite
        // 如果后端接口没有 /api，请取消下面这一行的注释：
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  }
})