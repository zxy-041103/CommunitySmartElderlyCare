<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <h2 class="page-title">数据统计</h2>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="refreshData" :loading="loading">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="metric-card" shadow="hover">
            <div class="metric-content">
              <div class="metric-info">
                <div class="metric-label">健康监测覆盖率</div>
                <div class="metric-value">{{ metrics.healthCoverage }}%</div>
                <div class="metric-trend" :class="metrics.healthCoverageTrend >= 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="metrics.healthCoverageTrend >= 0" /><ArrowDown v-else /></el-icon>
                  <span>{{ Math.abs(metrics.healthCoverageTrend) }}%</span>
                  <span class="trend-text">较上期</span>
                </div>
              </div>
              <div class="metric-icon health">
                <el-icon :size="40"><Monitor /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="metric-card" shadow="hover">
            <div class="metric-content">
              <div class="metric-info">
                <div class="metric-label">服务核销率</div>
                <div class="metric-value">{{ metrics.serviceVerification }}%</div>
                <div class="metric-trend" :class="metrics.serviceVerificationTrend >= 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="metrics.serviceVerificationTrend >= 0" /><ArrowDown v-else /></el-icon>
                  <span>{{ Math.abs(metrics.serviceVerificationTrend) }}%</span>
                  <span class="trend-text">较上期</span>
                </div>
              </div>
              <div class="metric-icon service">
                <el-icon :size="40"><Check /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="metric-card" shadow="hover">
            <div class="metric-content">
              <div class="metric-info">
                <div class="metric-label">紧急求助响应率</div>
                <div class="metric-value">{{ metrics.emergencyResponse }}%</div>
                <div class="metric-trend" :class="metrics.emergencyResponseTrend >= 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="metrics.emergencyResponseTrend >= 0" /><ArrowDown v-else /></el-icon>
                  <span>{{ Math.abs(metrics.emergencyResponseTrend) }}%</span>
                  <span class="trend-text">较上期</span>
                </div>
              </div>
              <div class="metric-icon emergency">
                <el-icon :size="40"><Warning /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="metric-card" shadow="hover">
            <div class="metric-content">
              <div class="metric-info">
                <div class="metric-label">老人满意度</div>
                <div class="metric-value">{{ metrics.satisfaction }}%</div>
                <div class="metric-trend" :class="metrics.satisfactionTrend >= 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="metrics.satisfactionTrend >= 0" /><ArrowDown v-else /></el-icon>
                  <span>{{ Math.abs(metrics.satisfactionTrend) }}%</span>
                  <span class="trend-text">较上期</span>
                </div>
              </div>
              <div class="metric-icon satisfaction">
                <el-icon :size="40"><Star /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 健康异常率趋势 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">健康异常率趋势</span>
                <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
                  <el-radio-button label="week">本周</el-radio-button>
                  <el-radio-button label="month">本月</el-radio-button>
                  <el-radio-button label="year">本年</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="healthChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 护工工作量分布 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">护工工作量分布</span>
              </div>
            </template>
            <div ref="workloadChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-20">
        <!-- 服务类型占比 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">服务类型占比</span>
              </div>
            </template>
            <div ref="serviceTypeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 老人健康状态分布 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">老人健康状态分布</span>
              </div>
            </template>
            <div ref="healthStatusChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据概览 -->
    <el-card class="overview-card" shadow="hover">
      <template #header>
        <div class="overview-header">
          <span class="overview-title">数据概览</span>
          <el-tag type="info">实时数据</el-tag>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="4" v-for="item in overviewData" :key="item.label">
          <div class="overview-item">
            <div class="overview-icon" :class="item.type">
              <el-icon :size="24"><component :is="item.icon" /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ item.value }}</div>
              <div class="overview-label">{{ item.label }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';
import * as echarts from 'echarts';
import {
  Monitor,
  Check,
  Warning,
  Star,
  ArrowUp,
  ArrowDown,
  Refresh,
  User,
  UserFilled,
  FirstAidKit,
  Document,
  Bell
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

// 页面名称
defineOptions({
  name: 'AdminDataCount'
});

// 加载状态
const loading = ref(false);

// 时间范围 - 默认本月
const timeRange = ref('month');

// 核心指标 - 初始化为0，从接口获取真实数据
const metrics = ref({
  healthCoverage: 0,
  healthCoverageTrend: 0,
  serviceVerification: 0,
  serviceVerificationTrend: 0,
  emergencyResponse: 0,
  emergencyResponseTrend: 0,
  satisfaction: 0,
  satisfactionTrend: 0
});

// 数据概览 - 初始化为0
const overviewData = ref([
  { label: '总老人数', value: 0, type: 'elderly', icon: 'User' },
  { label: '总家属数', value: 0, type: 'family', icon: 'UserFilled' },
  { label: '总护工数', value: 0, type: 'caregiver', icon: 'FirstAidKit' },
  { label: '本月服务', value: 0, type: 'service', icon: 'Document' },
  { label: '本月求助', value: 0, type: 'emergency', icon: 'Bell' },
  { label: '健康异常', value: 0, type: 'warning', icon: 'Warning' }
]);

// 图表数据缓存
const chartData = ref({
  healthTrend: { labels: [], values: [] },
  workloadDistribution: { names: [], values: [] },
  serviceTypeRatio: [],
  healthStatusDistribution: []
});

// 图表引用
const healthChartRef = ref(null);
const workloadChartRef = ref(null);
const serviceTypeChartRef = ref(null);
const healthStatusChartRef = ref(null);

let healthChart = null;
let workloadChart = null;
let serviceTypeChart = null;
let healthStatusChart = null;

// 获取统计数据 - 调用真实后端接口
const fetchStatisticsData = async () => {
  loading.value = true;
  try {
    const response = await request({
      url: '/admin/data-count/statistics',
      method: 'get',
      params: { timeRange: timeRange.value }
    });

    if (response.code === 200 && response.data) {
      const data = response.data;
      
      // 更新核心指标
      metrics.value = {
        healthCoverage: data.healthCoverage || 0,
        healthCoverageTrend: data.healthCoverageTrend || 0,
        serviceVerification: data.serviceVerification || 0,
        serviceVerificationTrend: data.serviceVerificationTrend || 0,
        emergencyResponse: data.emergencyResponse || 0,
        emergencyResponseTrend: data.emergencyResponseTrend || 0,
        satisfaction: data.satisfaction || 0,
        satisfactionTrend: data.satisfactionTrend || 0
      };

      // 更新数据概览
      overviewData.value = [
        { label: '总老人数', value: data.totalElderly || 0, type: 'elderly', icon: 'User' },
        { label: '总家属数', value: data.totalFamily || 0, type: 'family', icon: 'UserFilled' },
        { label: '总护工数', value: data.totalCaregiver || 0, type: 'caregiver', icon: 'FirstAidKit' },
        { label: '本月服务', value: data.monthlyServices || 0, type: 'service', icon: 'Document' },
        { label: '本月求助', value: data.monthlyEmergencies || 0, type: 'emergency', icon: 'Bell' },
        { label: '健康异常', value: data.monthlyAbnormalities || 0, type: 'warning', icon: 'Warning' }
      ];

      // 缓存图表数据
      chartData.value = {
        healthTrend: data.healthTrend || { labels: [], values: [] },
        workloadDistribution: data.workloadDistribution || { names: [], values: [] },
        serviceTypeRatio: data.serviceTypeRatio || [],
        healthStatusDistribution: data.healthStatusDistribution || []
      };

      // 更新图表
      updateAllCharts();
      
      console.log('统计数据加载成功:', data);
    } else {
      ElMessage.error(response.message || '获取统计数据失败');
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
    ElMessage.error('获取统计数据失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 时间范围切换处理
const handleTimeRangeChange = () => {
  console.log('时间范围切换为:', timeRange.value);
  fetchStatisticsData();
};

// 刷新数据
const refreshData = () => {
  fetchStatisticsData();
};

// 初始化所有图表
const initCharts = () => {
  nextTick(() => {
    initHealthChart();
    initWorkloadChart();
    initServiceTypeChart();
    initHealthStatusChart();
    // 初始化后更新图表数据
    updateAllCharts();
  });
};

// 健康异常率趋势图
const initHealthChart = () => {
  if (!healthChartRef.value) return;
  
  if (healthChart) {
    healthChart.dispose();
  }
  
  healthChart = echarts.init(healthChartRef.value);
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}%'
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
      data: []
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      name: '健康异常率',
      type: 'line',
      smooth: true,
      data: [],
      lineStyle: {
        width: 3,
        color: '#F56C6C'
      },
      itemStyle: {
        color: '#F56C6C'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
          { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
        ])
      }
    }]
  };
  healthChart.setOption(option);
};

// 护工工作量分布图
const initWorkloadChart = () => {
  if (!workloadChartRef.value) return;
  
  if (workloadChart) {
    workloadChart.dispose();
  }
  
  workloadChart = echarts.init(workloadChartRef.value);
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: [],
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '服务次数'
    },
    series: [{
      name: '服务次数',
      type: 'bar',
      data: [],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#67C23A' }
        ])
      },
      barWidth: '60%'
    }]
  };
  workloadChart.setOption(option);
};

// 服务类型占比图
const initServiceTypeChart = () => {
  if (!serviceTypeChartRef.value) return;
  
  if (serviceTypeChart) {
    serviceTypeChart.dispose();
  }
  
  serviceTypeChart = echarts.init(serviceTypeChartRef.value);
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [{
      name: '服务类型',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: []
    }]
  };
  serviceTypeChart.setOption(option);
};

// 老人健康状态分布图
const initHealthStatusChart = () => {
  if (!healthStatusChartRef.value) return;
  
  if (healthStatusChart) {
    healthStatusChart.dispose();
  }
  
  healthStatusChart = echarts.init(healthStatusChartRef.value);
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}人 ({d}%)'
    },
    legend: {
      bottom: '5%',
      left: 'center'
    },
    series: [{
      name: '健康状态',
      type: 'pie',
      radius: '65%',
      center: ['50%', '45%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  };
  healthStatusChart.setOption(option);
};

// 更新所有图表
const updateAllCharts = () => {
  if (!chartData.value) return;
  
  // 更新健康异常率趋势图
  if (healthChart && chartData.value.healthTrend) {
    const { labels, values } = chartData.value.healthTrend;
    healthChart.setOption({
      xAxis: { data: labels || [] },
      series: [{ data: values || [] }]
    });
  }
  
  // 更新护工工作量分布图
  if (workloadChart && chartData.value.workloadDistribution) {
    const { names, values } = chartData.value.workloadDistribution;
    workloadChart.setOption({
      xAxis: { data: names || [] },
      series: [{ data: values || [] }]
    });
  }
  
  // 更新服务类型占比图
  if (serviceTypeChart && chartData.value.serviceTypeRatio) {
    const data = chartData.value.serviceTypeRatio.map((item, index) => {
      const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#9B59B6'];
      return {
        name: item.name,
        value: item.value,
        itemStyle: { color: colors[index % colors.length] }
      };
    });
    serviceTypeChart.setOption({
      series: [{ data: data }]
    });
  }
  
  // 更新老人健康状态分布图
  if (healthStatusChart && chartData.value.healthStatusDistribution) {
    const statusColors = {
      '健康': '#67C23A',
      '正常': '#67C23A',
      '一般': '#E6A23C',
      '需关注': '#F56C6C',
      '异常': '#F56C6C',
      '紧急': '#909399'
    };
    const data = chartData.value.healthStatusDistribution.map(item => ({
      name: item.name,
      value: item.value,
      itemStyle: { color: statusColors[item.name] || '#409EFF' }
    }));
    healthStatusChart.setOption({
      series: [{ data: data }]
    });
  }
};

// 窗口大小变化时调整图表
const handleResize = () => {
  healthChart?.resize();
  workloadChart?.resize();
  serviceTypeChart?.resize();
  healthStatusChart?.resize();
};

onMounted(() => {
  // 页面加载时获取数据（默认本月）
  fetchStatisticsData();
  initCharts();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  healthChart?.dispose();
  workloadChart?.dispose();
  serviceTypeChart?.dispose();
  healthStatusChart?.dispose();
});
</script>

<style lang="scss" scoped>
.admin-dashboard {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 15px;
}

// 指标卡片
.metrics-section {
  margin-bottom: 30px;
}

.metric-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
}

.metric-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}

.metric-info {
  flex: 1;
}

.metric-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  font-weight: 500;
  
  &.up {
    color: #67c23a;
  }
  
  &.down {
    color: #f56c6c;
  }
  
  .trend-text {
    color: #909399;
    font-size: 12px;
    margin-left: 5px;
  }
}

.metric-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  
  &.health {
    background: linear-gradient(135deg, #409eff, #64b5f6);
  }
  
  &.service {
    background: linear-gradient(135deg, #67c23a, #81c784);
  }
  
  &.emergency {
    background: linear-gradient(135deg, #e6a23c, #ffb74d);
  }
  
  &.satisfaction {
    background: linear-gradient(135deg, #f56c6c, #e57373);
  }
}

// 图表区域
.charts-section {
  margin-bottom: 30px;
}

.chart-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.mt-20 {
  margin-top: 20px;
}

// 数据概览
.overview-card {
  border-radius: 12px;
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background-color: #f9fafc;
  border-radius: 8px;
  margin-bottom: 10px;
}

.overview-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  
  &.elderly {
    background-color: #409eff;
  }
  
  &.family {
    background-color: #67c23a;
  }
  
  &.caregiver {
    background-color: #e6a23c;
  }
  
  &.service {
    background-color: #409eff;
  }
  
  &.emergency {
    background-color: #f56c6c;
  }
  
  &.warning {
    background-color: #e6a23c;
  }
}

.overview-info {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.overview-label {
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: 20px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .metric-content {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .chart-container {
    height: 250px;
  }
}
</style>
