<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">健康数据管理</h3>
      <div style="display:flex;gap:12px;">
        <el-input v-model="query.elderlyName" placeholder="搜索老人姓名" clearable style="width:160px" @clear="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="elderlyName" label="老人姓名" />
      <el-table-column prop="heartRate" label="心率(次/分)" />
      <el-table-column label="血压(mmHg)"><template #default="{row}">{{ row.systolicPressure }}/{{ row.diastolicPressure }}</template></el-table-column>
      <el-table-column prop="bloodSugar" label="血糖(mmol/L)" />
      <el-table-column prop="bodyTemperature" label="体温(℃)" />
      <el-table-column prop="bloodOxygen" label="血氧(%)" />
      <el-table-column prop="recorderName" label="记录人" />
      <el-table-column prop="recordTime" label="记录时间" width="160" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{row}"><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { healthDataPage, healthDataDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ pageNum: 1, pageSize: 10, elderlyName: '' })
const tableData = ref([]); const total = ref(0)

const loadData = async () => { const res = await healthDataPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await healthDataDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
