import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 新增：导入path模块

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    // 新增：配置路径别名
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src') // 把@指向src目录
        }
    },
    // 保留之前的代理配置
    server: {
        host: '0.0.0.0', // 新增：让局域网能访问前端（解决Network提示use --host to expose）
        port: 5173,       // 指定端口
        open: 'http://10.194.181.247:5173/', // 启动时自动打开指定IP地址
        proxy: {
            '/api': {
                target: 'http://10.194.181.247:5000',
                changeOrigin: true
            },
            '/results': {
                target: 'http://10.194.181.247:5000',
                changeOrigin: true
            }
        }
    }
})