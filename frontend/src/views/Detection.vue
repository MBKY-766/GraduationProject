<template>
  <div class="detection-container">
    <el-card title="O型密封圈缺陷检测" class="detect-card">
      <!-- 上传区域 -->
      <div class="upload-section">
        <div class="upload-content">
          <el-upload
              drag
              :auto-upload="false"
              :on-change="handleFileChange"
              :on-remove="handleRemove"
              :file-list="fileList"
              :limit="1"
              accept="image/*"
              class="upload-dragger"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">点击或拖拽上传图片（仅限1张）</div>
          </el-upload>
          <div class="upload-tip">支持 jpg / png / jpeg，建议高清原图</div>
        </div>

        <div class="button-container">
          <el-button type="primary" :loading="loading" @click="startDetect" :disabled="fileList.length === 0" size="large">
            开始检测
          </el-button>
        </div>
      </div>

      <!-- 检测结果 -->
      <div v-if="result" class="result-area">
        <el-divider content-position="left">
          <span class="result-title">检测结果（共 {{ result.defect_count || 0 }} 个缺陷）</span>
        </el-divider>
        <div class="result-content">
          <!--直接返回检测结果-->

          <div class="image-container" v-if="result.result_image">
            <h3 class="image-title">检测结果图像：</h3>
            <el-image
                :src="result.result_image"
                fit="contain"
                :preview-src-list="[result.result_image]"
                class="result-image"
            />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const fileList = ref([])
const loading = ref(false)
const result = ref(null)


const handleFileChange = (file) => { fileList.value = [file] }
const handleRemove = () => { fileList.value = []; result.value = null }

const startDetect = async () => {
  if (!fileList.value.length) return
  const formData = new FormData()
  formData.append('file', fileList.value[0].raw)

  loading.value = true
  try {
    const res = await request({ url: '/detect', method: 'post', data: formData })

    if (res.code === 200) {
      // 确保res.data存在
      if (res.data) {
        if (res.data && typeof res.data === 'object') {
          console.log('=== 后端返回的data ===', res.data)
          console.log('=== 后端返回的图片路径 ===', res.data.data.result_image)
          // 确保defects属性存在且是数组
          if (!res.data.defects || !Array.isArray(res.data.defects)) {
            res.data.defects = []
            result.value = {
              defect_count: res.data.data.defect_count ?? 0,
              result_image: res.data.data.result_image || ''  // 直接使用后端返回的完整URL
            }
            // 确保defect_count属性存在
            if (res.data.defect_count === undefined) {
              res.data.defect_count = res.data.defects.length
            }
            // 确保result_image属性存在
            if (!res.data.data.result_image) {
              res.data.data.result_image = ''
            }
            result.value = res.data.data
            console.log('=== 最终使用的图片URL ===', result.value.result_image)
            ElMessage.success('检测完成！')
          } else {
            // 如果res.data不是对象，可能是Flask直接返回了结果
            console.log('=== 后端返回的data不是对象 ===', res.data)
            ElMessage.error('检测结果数据格式错误')
            result.value = {defects: [], defect_count: 0, result_image: ''}
          }
        } else {
          console.error('=== 请求失败 ===', err)
          ElMessage.error(res.msg || '检测失败')
          result.value = {defects: [], defect_count: 0, result_image: ''}
        }
      }
    }
  } catch (err) {
    ElMessage.error('请求失败，请检查后端是否运行')
    result.value = { defects: [], defect_count: 0, result_image: '' }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ==================== 关键：加大宽度（解决太窄问题） ==================== */
.detection-container {
  width: 100%;
  height: 100%;
  padding: 24px;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.detect-card {
  width: 100%;
  max-width: 1600px;          /* ← 核心调整：从1280px改成1400px，增加宽度 */
  height: fit-content;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

/* 上传区域 */
.upload-section {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  margin: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

/* 强制上传拖拽区调整大小和位置 */
.upload-dragger :deep(.el-upload-dragger) {
  width: 250px !important;  /* 固定宽度，使其缩小 */
  height: 250px !important; /* 固定高度，使其缩小 */
  margin: 0 0 0 0 !important; /* 靠左对齐 */
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.upload-dragger :deep(.el-upload-dragger:hover) {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.upload-dragger :deep(.el-upload__text) {
  white-space: nowrap;
  font-size: 16px;
  color: #606266;
}

/* 上传提示文字 */
.upload-tip {
  margin-top: 12px;
  font-size: 14px;
  color: #909399;
  align-self: flex-start;
}

/* 按钮容器 */
.button-container {
  margin-top: 20px;
  align-self: flex-start; /* 按钮居中 */
}

/* 结果区域美化 */
.result-area {
  margin: 20px;
  padding: 24px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.result-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.result-content {
  margin-top: 20px;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

/* 图像容器 */
.image-container {
  margin-top: 30px;
  text-align: center;
}

.image-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16px;
  text-align: left;
}

.result-image {
  max-width: 100%;
  max-height: 70vh;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.result-image:hover {
  transform: scale(1.02);
}

/* 响应式设计 */
@media screen and (max-width: 1440px) {
  .detect-card {
    max-width: 1200px;
  }

  .upload-dragger :deep(.el-upload-dragger) {
    min-width: 600px !important;
  }
}

@media screen and (max-width: 1200px) {
  .detect-card {
    max-width: 95%;
  }

  .upload-dragger :deep(.el-upload-dragger) {
    min-width: 100% !important;
  }
}
</style>