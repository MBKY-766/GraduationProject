<template>
  <div class="login-container">
    <el-card title="O型密封圈检测系统-登录">
      <el-form label-width="80px" :model="loginForm">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})

const login = () => {
  // if (!loginForm.value.username || !loginForm.value.password) {
  //   ElMessage.warning('请输入用户名和密码')
  //   return
  // }
  localStorage.setItem('token', 'test-token-123456')
  ElMessage.success('登录成功')
  // 仅执行一次跳转，无需setTimeout
  router.push('/home/detection')
}
</script>

<style scoped>
.login-container {
  width: 400px;
  margin: 100px auto;
}
</style>