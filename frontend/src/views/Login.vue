<template>
  <div class="login-container">
    <el-card title="O型密封圈检测系统-登录">
      <el-form label-width="80px" :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
.login-container {
  width: 400px;
  margin: 100px auto;
}
</style>