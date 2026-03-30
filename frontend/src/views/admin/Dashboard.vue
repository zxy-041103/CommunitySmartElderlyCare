<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px;">
      <el-col :span="4" v-for="item in statCards" :key="item.label">
        <div class="stat-card">
          <div class="stat-icon"><el-icon :size="36"><component :is="item.icon" /></el-icon></div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="page-card"><h3 class="page-title">预约状态分布</h3><div ref="appointmentChartRef" style="height:350px;"></div></div>
      </el-col>
      <el-col :span="12">
        <div class="page-card"><h3 class="page-title">健康预警级别分布</h3><div ref="alertChartRef" style="height:350px;"></div></div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { dashboardStats } from '../../api'

const stats = ref({})
const appointmentChartRef = ref()
const alertChartRef = ref()
const statCards = ref([])

onMounted(async () => {
  const res = await dashboardStats()
  stats.value = res.data
  statCards.value = [
    { label: '老年用户', value: res.data.totalUsers, icon: 'User' },
    { label: '护工人数', value: res.data.totalCaregivers, icon: 'Avatar' },
    { label: '待处理预警', value: res.data.pendingAlerts, icon: 'Warning' },
    { label: '待处理求助', value: res.data.pendingEmergencies, icon: 'AlarmClock' },
    { label: '待确认预约', value: res.data.pendingAppointments, icon: 'Tickets' },
    { label: '已完成服务', value: res.data.completedAppointments, icon: 'CircleCheck' }
  ]
  await nextTick()
  initAppointmentChart(res.data.appointmentStats)
  initAlertChart(res.data.alertStats)
})

const initAppointmentChart = (data) => {
  const chart = echarts.init(appointmentChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%' },
    color: ['#79BBFF', '#409EFF', '#E6A23C', '#67C23A', '#909399'],
    series: [{ type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false, itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false }, emphasis: { label: { show: true, fontSize: 16 } },
      data: Object.entries(data).map(([name, value]) => ({ name, value }))
    }]
  })
}
const initAlertChart = (data) => {
  const chart = echarts.init(alertChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    color: ['#409EFF', '#E6A23C', '#F56C6C'],
    xAxis: { type: 'category', data: Object.keys(data) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: Object.values(data), barWidth: '40%', itemStyle: { borderRadius: [8, 8, 0, 0] },
      colorBy: 'data' }]
  })
}
</script>
