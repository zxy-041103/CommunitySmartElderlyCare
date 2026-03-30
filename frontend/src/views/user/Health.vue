<template>
  <div>
    <div class="page-card">
      <h3 class="page-title">我的健康数据</h3>
      <el-radio-group v-model="days" @change="loadData" style="margin-bottom:16px;">
        <el-radio-button :label="7">近7天</el-radio-button><el-radio-button :label="14">近14天</el-radio-button><el-radio-button :label="30">近30天</el-radio-button>
      </el-radio-group>
      <el-row :gutter="20">
        <el-col :span="12"><div ref="bpChartRef" style="height:300px;"></div></el-col>
        <el-col :span="12"><div ref="hrChartRef" style="height:300px;"></div></el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top:16px;">
        <el-col :span="12"><div ref="bsChartRef" style="height:300px;"></div></el-col>
        <el-col :span="12"><div ref="boChartRef" style="height:300px;"></div></el-col>
      </el-row>
    </div>
    <div class="page-card">
      <h3 class="page-title">健康预警记录</h3>
      <el-table :data="alerts" border stripe>
        <el-table-column prop="alertType" label="预警类型" />
        <el-table-column prop="alertLevel" label="预警级别"><template #default="{row}"><el-tag :type="{INFO:'info',WARNING:'warning',DANGER:'danger'}[row.alertLevel]">{{ {INFO:'提示',WARNING:'警告',DANGER:'危险'}[row.alertLevel] }}</el-tag></template></el-table-column>
        <el-table-column prop="alertContent" label="内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态"><template #default="{row}"><el-tag :type="row.status==='PROCESSED'?'success':'warning'">{{ {PENDING:'待处理',PROCESSED:'已处理',IGNORED:'已忽略'}[row.status] }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { healthDataMy, healthAlertMy } from '../../api'
import { useVoiceAction } from '../../utils/voiceAction'

const days = ref(7); const alerts = ref([])
const bpChartRef = ref(); const hrChartRef = ref(); const bsChartRef = ref(); const boChartRef = ref()
let bpChart, hrChart, bsChart, boChart

const loadData = async () => {
  const res = await healthDataMy(days.value)
  const data = res.data
  const times = data.map(d => d.recordTime?.substring(5, 10))
  await nextTick()
  const opt = (title, series) => ({ tooltip: { trigger: 'axis' }, title: { text: title, left: 'center', textStyle: { fontSize: 14 } }, xAxis: { type: 'category', data: times }, yAxis: { type: 'value' }, grid: { top: 40, bottom: 30, left: 50, right: 20 }, series })
  if (!bpChart) bpChart = echarts.init(bpChartRef.value)
  bpChart.setOption(opt('血压趋势', [{ name: '收缩压', type: 'line', data: data.map(d => d.systolicPressure), smooth: true, itemStyle: { color: '#F56C6C' } }, { name: '舒张压', type: 'line', data: data.map(d => d.diastolicPressure), smooth: true, itemStyle: { color: '#409EFF' } }]))
  if (!hrChart) hrChart = echarts.init(hrChartRef.value)
  hrChart.setOption(opt('心率趋势', [{ name: '心率', type: 'line', data: data.map(d => d.heartRate), smooth: true, areaStyle: { color: 'rgba(245,108,108,0.1)' }, itemStyle: { color: '#F56C6C' } }]))
  if (!bsChart) bsChart = echarts.init(bsChartRef.value)
  bsChart.setOption(opt('血糖趋势', [{ name: '血糖', type: 'bar', data: data.map(d => d.bloodSugar), itemStyle: { color: '#67C23A', borderRadius: [4,4,0,0] } }]))
  if (!boChart) boChart = echarts.init(boChartRef.value)
  boChart.setOption(opt('血氧趋势', [{ name: '血氧', type: 'line', data: data.map(d => d.bloodOxygen), smooth: true, areaStyle: { color: 'rgba(64,158,255,0.1)' }, itemStyle: { color: '#409EFF' } }]))
}

// 语音指令监听 - 切换时间段
useVoiceAction('healthPeriod7', () => { days.value = 7; loadData() })
useVoiceAction('healthPeriod14', () => { days.value = 14; loadData() })
useVoiceAction('healthPeriod30', () => { days.value = 30; loadData() })

onMounted(async () => { await loadData(); const r = await healthAlertMy(); alerts.value = r.data })
</script>
