<template>
  <div class="home-page">
    <div class="welcome-container">
      <h1>欢迎来到缺陷检测系统</h1>
      <p>本系统提供O型密封圈的缺陷检测功能，支持上传图片进行检测，并展示检测结果。</p>
      <div class="features">
        <div class="feature-item">
          <el-icon class="feature-icon"><Camera /></el-icon>
          <h3>缺陷检测</h3>
          <p>上传O型密封圈图片，系统会自动检测并标记缺陷。</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><DataAnalysis /></el-icon>
          <h3>数据统计</h3>
          <p>查看检测结果的统计数据，了解缺陷分布情况。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Camera, DataAnalysis } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-page {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.welcome-container {
  text-align: center;
  max-width: 800px;
  padding: 40px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-container h1 {
  font-size: 28px;
  color: #2e3b4e;
  margin-bottom: 20px;
}

.welcome-container p {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.features {
  display: flex;
  justify-content: space-around;
  margin-top: 40px;
}

.feature-item {
  flex: 1;
  padding: 20px;
  margin: 0 10px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 20px;
}

.feature-item h3 {
  font-size: 18px;
  color: #2e3b4e;
  margin-bottom: 10px;
}

.feature-item p {
  font-size: 14px;
  color: #666;
  margin-bottom: 0;
}
</style>