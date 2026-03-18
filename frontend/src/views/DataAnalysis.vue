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
            <div class="stat-value">{{ overview.totalTests || 0 }}</div>
            <div class="stat-label">总检测次数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Warning /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ overview.totalDefects || 0 }}</div>
            <div class="stat-label">总缺陷数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Check /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ overview.passRate ? overview.passRate.toFixed(1) : 0 }}%</div>
            <div class="stat-label">合格率</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Timer /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ overview.avgTime || 0 }}s</div>
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
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { Camera, Warning, Check, Timer } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 统计数据
const overview = reactive({
  totalTests: 0,
  totalDefects: 0,
  passRate: 0,
  avgTime: 0
})

// 图表数据
const defectTypeData = ref([])
const detectionResultData = ref({
  dates: [],
  detectionCounts: [],
  defectCounts: []
})
const detectionTrendData = ref({
  months: [],
  detectionCounts: [],
  defectCounts: [],
  passRates: []
})

// 图表引用
const defectTypeChart = ref(null)
const detectionResultChart = ref(null)
const detectionTrendChart = ref(null)

// 图表实例
let defectTypeChartInstance = null
let detectionResultChartInstance = null
let detectionTrendChartInstance = null

// 获取统计概览
const getOverview = async () => {
  try {
    const res = await request({
      url: '/statistics/overview',
      method: 'get'
    })
    if (res.code === 200) {
      Object.assign(overview, res.data)
    }
  } catch (err) {
    console.error('获取统计概览失败:', err)
  }
}

// 获取缺陷类型分布
const getDefectTypeDistribution = async () => {
  try {
    const res = await request({
      url: '/statistics/defect-type',
      method: 'get'
    })
    if (res.code === 200) {
      defectTypeData.value = res.data
      initDefectTypeChart()
    }
  } catch (err) {
    console.error('获取缺陷类型分布失败:', err)
  }
}

// 获取检测结果统计
const getDetectionResult = async () => {
  try {
    const res = await request({
      url: '/statistics/detection-result',
      method: 'get'
    })
    if (res.code === 200) {
      detectionResultData.value = res.data
      initDetectionResultChart()
    }
  } catch (err) {
    console.error('获取检测结果统计失败:', err)
  }
}

// 获取检测趋势
const getDetectionTrend = async () => {
  try {
    const res = await request({
      url: '/statistics/detection-trend',
      method: 'get'
    })
    if (res.code === 200) {
      detectionTrendData.value = res.data
      initDetectionTrendChart()
    }
  } catch (err) {
    console.error('获取检测趋势失败:', err)
  }
}

// 初始化缺陷类型分布图表
const initDefectTypeChart = () => {
  if (defectTypeChart.value) {
    defectTypeChartInstance = echarts.init(defectTypeChart.value)
    const names = defectTypeData.value.map(item => item.name)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: names
      },
      series: [
        {
          name: '缺陷类型',
          type: 'pie',
          radius: '50%',
          data: defectTypeData.value,
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
        data: detectionResultData.value.dates
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '检测次数',
          type: 'bar',
          data: detectionResultData.value.detectionCounts
        },
        {
          name: '缺陷数量',
          type: 'bar',
          data: detectionResultData.value.defectCounts
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
        data: detectionTrendData.value.months
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
          data: detectionTrendData.value.detectionCounts
        },
        {
          name: '缺陷数量',
          type: 'line',
          data: detectionTrendData.value.defectCounts
        },
        {
          name: '合格率',
          type: 'line',
          yAxisIndex: 1,
          data: detectionTrendData.value.passRates
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

onMounted(async () => {
  // 获取统计数据
  await getOverview()
  await getDefectTypeDistribution()
  await getDetectionResult()
  await getDetectionTrend()

  // 监听窗口大小变化
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