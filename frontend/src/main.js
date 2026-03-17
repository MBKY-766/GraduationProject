import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入Element Plus图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入路由
import router from './router'

// 引入Pinia（状态管理）
import { createPinia } from 'pinia'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
// 打印环境变量，验证是否读取成功
console.log('后端接口地址：', import.meta.env.VITE_API_BASE_URL)
// 挂载插件
app.use(ElementPlus)
app.use(router)
app.use(createPinia())

app.mount('#app')