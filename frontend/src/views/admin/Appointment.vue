<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">预约管理</h3>
      <el-select v-model="query.status" placeholder="状态筛选" clearable style="width:140px" @change="loadData">
        <el-option label="待确认" value="PENDING" /><el-option label="已确认" value="CONFIRMED" /><el-option label="进行中" value="IN_PROGRESS" /><el-option label="已完成" value="COMPLETED" /><el-option label="已取消" value="CANCELLED" />
      </el-select>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="elderlyName" label="老人" />
      <el-table-column prop="serviceItemName" label="服务项目" />
      <el-table-column prop="caregiverName" label="护工" />
      <el-table-column prop="appointmentTime" label="预约时间" width="160" />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="aptStatusType(row.status)">{{ aptStatusLabel(row.status) }}</el-tag></template></el-table-column>
      <el-table-column prop="note" label="备注" show-overflow-tooltip />
      <el-table-column prop="rating" label="评分" width="70" />
      <el-table-column prop="ratingContent" label="评价" show-overflow-tooltip />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{row}"><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { appointmentPage, appointmentDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, status: '' }); const tableData = ref([]); const total = ref(0)
const aptStatusLabel = (s) => ({ PENDING:'待确认', CONFIRMED:'已确认', IN_PROGRESS:'进行中', COMPLETED:'已完成', CANCELLED:'已取消' }[s] || s)
const aptStatusType = (s) => ({ PENDING:'warning', CONFIRMED:'primary', IN_PROGRESS:'', COMPLETED:'success', CANCELLED:'info' }[s] || '')
const loadData = async () => { const res = await appointmentPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await appointmentDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
