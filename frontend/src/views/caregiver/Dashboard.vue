<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px;">
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><User /></el-icon></div><div class="stat-value">{{ elderlyCount }}</div><div class="stat-label">负责老人</div></div></el-col>
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Warning /></el-icon></div><div class="stat-value" style="color:#F56C6C;">{{ pendingEmergency }}</div><div class="stat-label">待响应求助</div></div></el-col>
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Tickets /></el-icon></div><div class="stat-value">{{ pendingAppointment }}</div><div class="stat-label">待服务预约</div></div></el-col>
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Calendar /></el-icon></div><div class="stat-value">{{ todaySchedule }}</div><div class="stat-label">今日班次</div></div></el-col>
    </el-row>
    <div class="page-card">
      <h3 class="page-title">待响应紧急求助</h3>
      <el-table :data="emergencies" border stripe>
        <el-table-column prop="elderlyName" label="老人" />
        <el-table-column prop="elderlyPhone" label="电话" />
        <el-table-column prop="emergencyType" label="类型" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="求助时间" width="160" />
        <el-table-column label="操作" width="120"><template #default="{row}"><el-button size="small" type="danger" @click="respond(row.id)" v-if="row.status==='PENDING'">响应</el-button></template></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { assignmentMyElderly, emergencyPending, emergencyRespond, appointmentMy, workScheduleMy } from '../../api'
import { ElMessage } from 'element-plus'
const elderlyCount = ref(0); const pendingEmergency = ref(0); const pendingAppointment = ref(0); const todaySchedule = ref(0); const emergencies = ref([])
const respond = async (id) => { await emergencyRespond(id); ElMessage.success('已响应'); loadData() }
const loadData = async () => {
  try { const r1 = await assignmentMyElderly(); elderlyCount.value = r1.data.length } catch(e){}
  const r2 = await emergencyPending(); emergencies.value = r2.data; pendingEmergency.value = r2.data.filter(e=>e.status==='PENDING').length
  const r3 = await appointmentMy({ status: 'CONFIRMED' }); pendingAppointment.value = r3.data.length
  const today = new Date().toISOString().substring(0,10)
  const r4 = await workScheduleMy({ startDate: today, endDate: today }); todaySchedule.value = r4.data.length
}
onMounted(loadData)
</script>
