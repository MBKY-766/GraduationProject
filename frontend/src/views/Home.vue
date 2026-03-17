<template>
  <el-container style="height: 100vh;">
    <!-- 侧边栏 -->
    <el-aside width="200px" style="background-color: #2e3b4e;">
      <el-menu
        default-active="1"
        class="el-menu-vertical-demo"
        background-color="#2e3b4e"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="1">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="2" @click="$router.push('/home/detection')">
          <el-icon><Camera /></el-icon>
          <template #title>缺陷检测</template>
        </el-menu-item>
        <el-menu-item index="3" @click="$router.push('/home/data-analysis')">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>
        <el-menu-item index="4" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <template #title>退出登录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <el-dropdown>
          <i class="el-icon-setting" style="margin-right: 15px"></i>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>查看</el-dropdown-item>
              <el-dropdown-item>新增</el-dropdown-item>
              <el-dropdown-item>删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <span>管理员</span>
      </el-header>
      <el-main>
        <!-- 嵌套路由出口 -->
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 修复：补充缺失的图标导入
import { House, Camera, DataAnalysis, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()

const logout = () => {
  // 修复1：移除重复路由跳转，仅保留一次replace（replace不会留下历史记录，更适合退出登录）
  localStorage.removeItem('token')
  ElMessage.success('退出成功')
  // 修复2：仅执行一次跳转，且无需setTimeout（避免延迟触发窗口激活）
  router.replace('/login')
}
</script>

<style scoped>
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>