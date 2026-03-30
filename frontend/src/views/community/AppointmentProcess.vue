<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">预约处理</h3>
      <el-select v-model="query.status" placeholder="状态" clearable style="width:140px" @change="loadData">
        <el-option label="待确认" value="PENDING" /><el-option label="已确认" value="CONFIRMED" /><el-option label="进行中" value="IN_PROGRESS" /><el-option label="已完成" value="COMPLETED" />
      </el-select>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="elderlyName" label="老人" />
      <el-table-column prop="serviceItemName" label="服务项目" />
      <el-table-column prop="caregiverName" label="护工" />
      <el-table-column prop="appointmentTime" label="预约时间" width="160" />
      <el-table-column prop="status" label="状态" width="100"><template #default="{row}"><el-tag :type="{PENDING:'warning',CONFIRMED:'primary',IN_PROGRESS:'',COMPLETED:'success',CANCELLED:'info'}[row.status]">{{ {PENDING:'待确认',CONFIRMED:'已确认',IN_PROGRESS:'进行中',COMPLETED:'已完成',CANCELLED:'已取消'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column prop="note" label="备注" show-overflow-tooltip />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{row}">
          <el-button size="small" type="primary" @click="updateStatus(row.id,'CONFIRMED')" v-if="row.status==='PENDING'">确认</el-button>
          <el-button size="small" type="" @click="updateStatus(row.id,'IN_PROGRESS')" v-if="row.status==='CONFIRMED'">开始服务</el-button>
          <el-button size="small" type="success" @click="updateStatus(row.id,'COMPLETED')" v-if="row.status==='IN_PROGRESS'">完成</el-button>
          <el-button size="small" type="danger" @click="updateStatus(row.id,'CANCELLED')" v-if="row.status==='PENDING'||row.status==='CONFIRMED'">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { appointmentPage, appointmentUpdateStatus } from '../../api'
import { ElMessage } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, status: '' }); const tableData = ref([]); const total = ref(0)
const loadData = async () => { const res = await appointmentPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const updateStatus = async (id, status) => { await appointmentUpdateStatus(id, { status }); ElMessage.success('操作成功'); loadData() }
onMounted(loadData)
</script>
