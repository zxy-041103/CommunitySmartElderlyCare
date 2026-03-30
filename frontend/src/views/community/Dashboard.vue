<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px;">
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Tickets /></el-icon></div><div class="stat-value">{{ stats.pendingAppointments }}</div><div class="stat-label">待处理预约</div></div></el-col>
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Flag /></el-icon></div><div class="stat-value">{{ stats.upcomingActivities }}</div><div class="stat-label">近期活动</div></div></el-col>
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><ChatDotRound /></el-icon></div><div class="stat-value" style="color:#E6A23C;">{{ stats.pendingFeedback }}</div><div class="stat-label">待回复反馈</div></div></el-col>
      <el-col :span="6"><div class="stat-card"><div class="stat-icon"><el-icon :size="36"><Bell /></el-icon></div><div class="stat-value">{{ stats.totalAnnouncements }}</div><div class="stat-label">公告数量</div></div></el-col>
    </el-row>
    <div class="page-card">
      <h3 class="page-title">最近待处理预约</h3>
      <el-table :data="appointments" border stripe>
        <el-table-column prop="elderlyName" label="老人" />
        <el-table-column prop="serviceItemName" label="服务" />
        <el-table-column prop="appointmentTime" label="预约时间" width="160" />
        <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag type="warning">{{ {PENDING:'待确认',CONFIRMED:'已确认'}[row.status] }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="confirm(row.id)" v-if="row.status==='PENDING'">确认</el-button>
            <el-button size="small" type="success" @click="complete(row.id)" v-if="row.status==='CONFIRMED'">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { appointmentPage, appointmentUpdateStatus, activityPage, feedbackPage, announcementPage } from '../../api'
import { ElMessage } from 'element-plus'
const stats = ref({ pendingAppointments: 0, upcomingActivities: 0, pendingFeedback: 0, totalAnnouncements: 0 })
const appointments = ref([])
const loadData = async () => {
  const r1 = await appointmentPage({ pageNum: 1, pageSize: 5, status: 'PENDING' }); appointments.value = r1.data.records; stats.value.pendingAppointments = r1.data.total
  const r2 = await activityPage({ pageNum: 1, pageSize: 1, status: 'UPCOMING' }); stats.value.upcomingActivities = r2.data.total
  const r3 = await feedbackPage({ pageNum: 1, pageSize: 1, status: 'PENDING' }); stats.value.pendingFeedback = r3.data.total
  const r4 = await announcementPage({ pageNum: 1, pageSize: 1 }); stats.value.totalAnnouncements = r4.data.total
}
const confirm = async (id) => { await appointmentUpdateStatus(id, { status: 'CONFIRMED' }); ElMessage.success('已确认'); loadData() }
const complete = async (id) => { await appointmentUpdateStatus(id, { status: 'COMPLETED' }); ElMessage.success('已完成'); loadData() }
onMounted(loadData)
</script>
