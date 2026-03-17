import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'

// 导入页面组件
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import Detection from '@/views/Detection.vue'
import DataAnalysis from '@/views/DataAnalysis.vue'

// 定义路由规则
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    children: [
      {
        path: 'detection',
        name: 'Detection',
        component: Detection
      },
      {
        path: 'data-analysis',
        name: 'DataAnalysis',
        component: DataAnalysis
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes
})


// 修复路由守卫逻辑，确保所有路径都有适当的处理
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 有token的情况
  if (token) {
    if (to.path === '/login') {
      // 已登录用户访问登录页，跳转到检测页
      next({ path: '/home/detection' })
    } else {
      // 正常访问其他页面
      next()
    }
  } 
  // 没有token的情况
  else {
    if (to.path === '/login') {
      // 访问登录页，允许通过
      next()
    } else {
      // 未登录访问其他页面，跳转到登录页
      next('/login')
    }
  }
})



export default router