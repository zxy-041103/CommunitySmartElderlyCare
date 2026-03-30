<template>
  <div>
    <div class="page-card">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
        <h3 class="page-title" style="margin-bottom:0">健康监测</h3>
        <div style="display:flex;gap:12px;">
          <el-select v-model="selectedElderly" placeholder="选择老人" style="width:160px" @change="loadData"><el-option v-for="u in elderlyList" :key="u.id" :label="u.name" :value="u.id" /></el-select>
          <el-button type="success" @click="openRecord">录入数据</el-button>
        </div>
      </div>
      <el-row :gutter="20" v-if="healthList.length">
        <el-col :span="12"><div ref="bpChartRef" style="height:280px;"></div></el-col>
        <el-col :span="12"><div ref="hrChartRef" style="height:280px;"></div></el-col>
      </el-row>
    </div>
    <div class="page-card">
      <h3 class="page-title">健康记录</h3>
      <el-table :data="healthList" border stripe>
        <el-table-column prop="heartRate" label="心率" /><el-table-column label="血压"><template #default="{row}">{{ row.systolicPressure }}/{{ row.diastolicPressure }}</template></el-table-column>
        <el-table-column prop="bloodSugar" label="血糖" /><el-table-column prop="bodyTemperature" label="体温" /><el-table-column prop="bloodOxygen" label="血氧" /><el-table-column prop="recordTime" label="记录时间" width="160" />
      </el-table>
    </div>
    <el-dialog v-model="recordVisible" title="录入健康数据" width="500px">
      <el-form :model="recordForm" label-width="100px">
        <el-form-item label="老人"><el-select v-model="recordForm.elderlyId" style="width:100%"><el-option v-for="u in elderlyList" :key="u.id" :label="u.name" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="心率(次/分)"><el-input-number v-model="recordForm.heartRate" :min="30" :max="200" /></el-form-item>
        <el-form-item label="收缩压"><el-input-number v-model="recordForm.systolicPressure" :min="60" :max="250" /></el-form-item>
        <el-form-item label="舒张压"><el-input-number v-model="recordForm.diastolicPressure" :min="40" :max="150" /></el-form-item>
        <el-form-item label="血糖"><el-input-number v-model="recordForm.bloodSugar" :min="1" :max="30" :precision="1" :step="0.1" /></el-form-item>
        <el-form-item label="体温(℃)"><el-input-number v-model="recordForm.bodyTemperature" :min="34" :max="42" :precision="1" :step="0.1" /></el-form-item>
        <el-form-item label="血氧(%)"><el-input-number v-model="recordForm.bloodOxygen" :min="50" :max="100" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="recordVisible=false">取消</el-button><el-button type="primary" @click="submitRecord">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { assignmentMyElderly, healthDataList, healthDataAdd } from '../../api'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const elderlyList = ref([]); const selectedElderly = ref(null); const healthList = ref([])
const recordVisible = ref(false); const recordForm = ref({})
const bpChartRef = ref(); const hrChartRef = ref()

const loadData = async () => {
  if (!selectedElderly.value) return
  const res = await healthDataList(selectedElderly.value, 14); healthList.value = res.data
  await nextTick()
  const times = res.data.map(d => d.recordTime?.substring(5,10))
  if (bpChartRef.value) { echarts.init(bpChartRef.value).setOption({ tooltip:{trigger:'axis'}, title:{text:'血压趋势',left:'center',textStyle:{fontSize:14}}, xAxis:{type:'category',data:times}, yAxis:{type:'value'}, grid:{top:40,bottom:30,left:50,right:20}, series:[{name:'收缩压',type:'line',data:res.data.map(d=>d.systolicPressure),smooth:true,itemStyle:{color:'#F56C6C'}},{name:'舒张压',type:'line',data:res.data.map(d=>d.diastolicPressure),smooth:true,itemStyle:{color:'#409EFF'}}]}) }
  if (hrChartRef.value) { echarts.init(hrChartRef.value).setOption({ tooltip:{trigger:'axis'}, title:{text:'心率趋势',left:'center',textStyle:{fontSize:14}}, xAxis:{type:'category',data:times}, yAxis:{type:'value'}, grid:{top:40,bottom:30,left:50,right:20}, series:[{name:'心率',type:'line',data:res.data.map(d=>d.heartRate),smooth:true,areaStyle:{color:'rgba(245,108,108,0.1)'},itemStyle:{color:'#F56C6C'}}]}) }
}

const openRecord = () => { recordForm.value = { elderlyId: selectedElderly.value, heartRate: 72, systolicPressure: 120, diastolicPressure: 80, bloodSugar: 5.5, bodyTemperature: 36.5, bloodOxygen: 98 }; recordVisible.value = true }
const submitRecord = async () => { if (!recordForm.value.elderlyId) return ElMessage.warning('请选择老人'); await healthDataAdd(recordForm.value); ElMessage.success('录入成功'); recordVisible.value = false; loadData() }

onMounted(async () => {
  const res = await assignmentMyElderly(); elderlyList.value = res.data
  if (route.query.elderlyId) { selectedElderly.value = Number(route.query.elderlyId); loadData() }
  else if (elderlyList.value.length) { selectedElderly.value = elderlyList.value[0].id; loadData() }
})
</script>
