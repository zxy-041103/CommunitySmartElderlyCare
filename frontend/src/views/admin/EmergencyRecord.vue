<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">紧急救助管理</h3>
      <el-select v-model="query.status" placeholder="状态筛选" clearable style="width:130px" @change="loadData"><el-option label="待处理" value="PENDING" /><el-option label="处理中" value="PROCESSING" /><el-option label="已解决" value="RESOLVED" /></el-select>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="elderlyName" label="老人姓名" />
      <el-table-column prop="elderlyPhone" label="联系电话" />
      <el-table-column prop="emergencyType" label="紧急类型" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="row.status==='RESOLVED'?'success':row.status==='PROCESSING'?'warning':'danger'">{{ {PENDING:'待处理',PROCESSING:'处理中',RESOLVED:'已解决'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column prop="responderName" label="响应人" />
      <el-table-column prop="resolveNote" label="解决备注" show-overflow-tooltip />
      <el-table-column prop="createTime" label="求助时间" width="160" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{row}"><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { emergencyPage, emergencyDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, status: '' })
const tableData = ref([]); const total = ref(0)
const loadData = async () => { const res = await emergencyPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await emergencyDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
