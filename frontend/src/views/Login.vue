<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <!-- 头部 Logo 和标题 -->
        <div class="login-header">
          <div class="logo">
            <svg viewBox="0 0 100 100" class="logo-icon">
              <circle cx="50" cy="50" r="40" fill="none" stroke="#409EFF" stroke-width="4"/>
              <circle cx="50" cy="50" r="25" fill="#409EFF"/>
              <circle cx="50" cy="50" r="15" fill="#fff"/>
            </svg>
          </div>
          <h1 class="title">O型密封圈检测系统</h1>
          <p class="subtitle">O-ring Seal Inspection System</p>
        </div>
        
        <el-form label-width="0" :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
          <el-form-item prop="username">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#909399" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </span>
              <el-input v-model="loginForm.username" placeholder="请输入用户名" class="custom-input"></el-input>
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#909399" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </span>
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" class="custom-input"></el-input>
            </div>
          </el-form-item>
          <el-form-item class="button-item">
            <el-button type="primary" @click="login" :loading="loading" class="login-btn">
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
        
        <!-- 底部信息 -->
        <div class="login-footer">
          <span class="copyright">© 2026 O-ring Inspection System</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'root',
  password: '123456'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const login = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request({
          url: '/login',
          method: 'post',
          data: loginForm
        })
        if (res.code === 200) {
          sessionStorage.setItem('token', res.data.token)
          ElMessage.success('登录成功')
          router.push('/home')
        } else {
          ElMessage.error(res.msg || '登录失败')
        }
      } catch (err) {
        ElMessage.error(err.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.2);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.15);
  bottom: -50px;
  right: -50px;
  animation-delay: -2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.1);
  top: 50%;
  right: 10%;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.login-container {
  width: 420px;
  z-index: 10;
}

.login-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.logo {
  margin-bottom: 20px;
}

.logo-icon {
  width: 60px;
  height: 60px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 12px;
  padding: 0 16px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.input-wrapper:focus-within {
  background: #fff;
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.input-icon {
  margin-right: 12px;
  flex-shrink: 0;
}

.custom-input {
  flex: 1;
  border: none;
  background: transparent;
  height: 48px;
}

.custom-input :deep(.el-input__wrapper) {
  border: none;
  box-shadow: none;
  background: transparent;
}

.custom-input :deep(.el-input__inner) {
  height: 48px;
  line-height: 48px;
  font-size: 15px;
}

.button-item {
  margin-bottom: 0;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
}

.copyright {
  font-size: 12px;
  color: #c0c4cc;
}
</style>