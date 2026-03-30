<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px;">
      <el-col :span="8"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Tickets /></el-icon></div><div class="stat-value">{{ stats.totalAppointments }}</div><div class="stat-label">总预约数</div></div></el-col>
      <el-col :span="8"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><CircleCheck /></el-icon></div><div class="stat-value" style="color:#67C23A;">{{ stats.completedAppointments }}</div><div class="stat-label">已完成</div></div></el-col>
      <el-col :span="8"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Flag /></el-icon></div><div class="stat-value">{{ stats.totalActivities }}</div><div class="stat-label">活动数量</div></div></el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="page-card"><h3 class="page-title">预约状态分布</h3><div ref="chartRef" style="height:350px;"></div></div>
      </el-col>
      <el-col :span="12">
        <div class="page-card"><h3 class="page-title">反馈类型分布</h3><div ref="fbChartRef" style="height:350px;"></div></div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { dashboardStats, feedbackPage } from '../../api'

const chartRef = ref(); const fbChartRef = ref()
const stats = ref({ totalAppointments: 0, completedAppointments: 0, totalActivities: 0 })

onMounted(async () => {
  // Use dashboard stats API for aggregated data (avoids N+1 queries)
  const res = await dashboardStats()
  const data = res.data
  stats.value.totalAppointments = (data.pendingAppointments || 0) + (data.completedAppointments || 0)
  stats.value.completedAppointments = data.completedAppointments || 0
  stats.value.totalActivities = data.totalActivities || 0

  await nextTick()

  // Appointment status distribution from dashboard stats
  const appointmentStats = data.appointmentStats || {}
  echarts.init(chartRef.value).setOption({
    tooltip: { trigger: 'item' }, legend: { bottom: '5%' }, color: ['#79BBFF','#409EFF','#E6A23C','#67C23A','#909399'],
    series: [{ type: 'pie', radius: ['40%','70%'], avoidLabelOverlap: false, itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false }, emphasis: { label: { show: true, fontSize: 14 } },
      data: Object.entries(appointmentStats).map(([name, value]) => ({ name, value }))
    }]
  })

  // Feedback type distribution
  const types = ['SUGGESTION','COMPLAINT','PRAISE']
  const typeLabels = ['建议','投诉','表扬']
  const typeCounts = []
  for (const t of types) { const r = await feedbackPage({ pageNum: 1, pageSize: 1, type: t }); typeCounts.push(r.data.total) }
  echarts.init(fbChartRef.value).setOption({
    tooltip: { trigger: 'axis' }, color: ['#409EFF','#F56C6C','#67C23A'],
    xAxis: { type: 'category', data: typeLabels }, yAxis: { type: 'value' },
    series: [{ type: 'bar', data: typeCounts, barWidth: '40%', itemStyle: { borderRadius: [8,8,0,0] }, colorBy: 'data' }]
  })
})
</script>
