<template>
  <div class="data-analysis">
    <el-card class="stat-card">
      <template #header>
        <div class="card-header">
          <span>检测统计概览</span>
        </div>
      </template>
      <div class="stat-container">
        <div class="stat-item">
          <el-icon class="stat-icon"><Camera /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ totalTests }}</div>
            <div class="stat-label">总检测次数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Warning /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ totalDefects }}</div>
            <div class="stat-label">总缺陷数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Check /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ passRate }}%</div>
            <div class="stat-label">合格率</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Timer /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ avgTime }}s</div>
            <div class="stat-label">平均检测时间</div>
          </div>
        </div>
      </div>
    </el-card>

    <div class="chart-container">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>缺陷类型分布</span>
          </div>
        </template>
        <div ref="defectTypeChart" class="chart"></div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>检测结果统计</span>
          </div>
        </template>
        <div ref="detectionResultChart" class="chart"></div>
      </el-card>

      <el-card class="chart-card full-width">
        <template #header>
          <div class="card-header">
            <span>检测趋势</span>
          </div>
        </template>
        <div ref="detectionTrendChart" class="chart"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { Camera, Warning, Check, Timer } from '@element-plus/icons-vue'

// 统计数据
const totalTests = 120
const totalDefects = 45
const passRate = 62.5
const avgTime = 2.3

// 图表引用
const defectTypeChart = ref(null)
const detectionResultChart = ref(null)
const detectionTrendChart = ref(null)

// 图表实例
let defectTypeChartInstance = null
let detectionResultChartInstance = null
let detectionTrendChartInstance = null

// 初始化缺陷类型分布图表
const initDefectTypeChart = () => {
  if (defectTypeChart.value) {
    defectTypeChartInstance = echarts.init(defectTypeChart.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: ['裂纹', '切割缺陷', '挤压缺陷', '侧边压痕']
      },
      series: [
        {
          name: '缺陷类型',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 15, name: '裂纹' },
            { value: 10, name: '切割缺陷' },
            { value: 8, name: '挤压缺陷' },
            { value: 7, name: '侧边压痕' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    defectTypeChartInstance.setOption(option)
  }
}

// 初始化检测结果统计图表
const initDetectionResultChart = () => {
  if (detectionResultChart.value) {
    detectionResultChartInstance = echarts.init(detectionResultChart.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '检测次数',
          type: 'bar',
          data: [12, 19, 15, 17, 20, 14, 18]
        },
        {
          name: '缺陷数量',
          type: 'bar',
          data: [5, 8, 6, 7, 9, 5, 7]
        }
      ]
    }
    detectionResultChartInstance.setOption(option)
  }
}

// 初始化检测趋势图表
const initDetectionTrendChart = () => {
  if (detectionTrendChart.value) {
    detectionTrendChartInstance = echarts.init(detectionTrendChart.value)
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['检测次数', '缺陷数量', '合格率']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: [
        {
          type: 'value',
          name: '数量',
          min: 0,
          max: 100
        },
        {
          type: 'value',
          name: '合格率',
          min: 0,
          max: 100,
          axisLabel: {
            formatter: '{value}%'
          }
        }
      ],
      series: [
        {
          name: '检测次数',
          type: 'line',
          data: [60, 70, 80, 90, 100, 120]
        },
        {
          name: '缺陷数量',
          type: 'line',
          data: [25, 28, 30, 32, 35, 45]
        },
        {
          name: '合格率',
          type: 'line',
          yAxisIndex: 1,
          data: [58.3, 60, 62.5, 64.4, 65, 62.5]
        }
      ]
    }
    detectionTrendChartInstance.setOption(option)
  }
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  defectTypeChartInstance?.resize()
  detectionResultChartInstance?.resize()
  detectionTrendChartInstance?.resize()
}

onMounted(() => {
  initDefectTypeChart()
  initDetectionResultChart()
  initDetectionTrendChart()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.data-analysis {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.stat-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #2e3b4e;
}

.stat-container {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  margin: 10px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  min-width: 200px;
  flex: 1;
  max-width: 300px;
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 32px;
  color: #409eff;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #2e3b4e;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.chart-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  height: 400px;
}

.full-width {
  grid-column: 1 / -1;
}

.chart {
  width: 100%;
  height: 350px;
}

@media (max-width: 768px) {
  .chart-container {
    grid-template-columns: 1fr;
  }
  
  .stat-container {
    flex-direction: column;
    align-items: center;
  }
  
  .stat-item {
    width: 100%;
    max-width: none;
  }
}
</style>