<template>
  <div class="page-card">
    <h3 class="page-title">我的预约</h3>
    <el-radio-group v-model="statusFilter" @change="loadData" style="margin-bottom:16px;">
      <el-radio-button label="">全部</el-radio-button><el-radio-button label="PENDING">待确认</el-radio-button><el-radio-button label="CONFIRMED">已确认</el-radio-button><el-radio-button label="COMPLETED">已完成</el-radio-button><el-radio-button label="CANCELLED">已取消</el-radio-button>
    </el-radio-group>
    <el-table :data="appointments" border stripe>
      <el-table-column prop="serviceItemName" label="服务项目" />
      <el-table-column prop="caregiverName" label="护工" />
      <el-table-column prop="appointmentTime" label="预约时间" width="160" />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="{PENDING:'warning',CONFIRMED:'primary',IN_PROGRESS:'',COMPLETED:'success',CANCELLED:'info'}[row.status]">{{ {PENDING:'待确认',CONFIRMED:'已确认',IN_PROGRESS:'进行中',COMPLETED:'已完成',CANCELLED:'已取消'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column prop="note" label="备注" show-overflow-tooltip />
      <el-table-column prop="rating" label="评分" width="70" />
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" type="warning" @click="cancel(row.id)" v-if="row.status==='PENDING'||row.status==='CONFIRMED'">取消</el-button>
          <el-button size="small" type="primary" @click="openRate(row)" v-if="row.status==='COMPLETED'&&!row.rating">评价</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="rateDialogVisible" title="服务评价" width="400px">
      <el-form :model="rateForm" label-width="60px">
        <el-form-item label="评分"><el-rate v-model="rateForm.rating" /></el-form-item>
        <el-form-item label="评价"><el-input v-model="rateForm.ratingContent" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="rateDialogVisible=false">取消</el-button><el-button type="primary" @click="submitRate">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { appointmentMy, appointmentCancel, appointmentRate } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useVoiceAction } from '../../utils/voiceAction'

const statusFilter = ref(''); const appointments = ref([]); const rateDialogVisible = ref(false); const rateForm = ref({})
const loadData = async () => { const res = await appointmentMy({ status: statusFilter.value }); appointments.value = res.data }
const cancel = (id) => { ElMessageBox.confirm('确认取消？').then(async () => { await appointmentCancel(id); ElMessage.success('已取消'); loadData() }) }
const openRate = (row) => { rateForm.value = { id: row.id, rating: 5, ratingContent: '' }; rateDialogVisible.value = true }
const submitRate = async () => { await appointmentRate(rateForm.value.id, rateForm.value); ElMessage.success('评价成功'); rateDialogVisible.value = false; loadData() }

// 语音指令监听 - 筛选预约状态
const filterByStatus = (status) => { statusFilter.value = status; loadData() }
useVoiceAction('filterAll', () => filterByStatus(''))
useVoiceAction('filterPending', () => filterByStatus('PENDING'))
useVoiceAction('filterConfirmed', () => filterByStatus('CONFIRMED'))
useVoiceAction('filterCompleted', () => filterByStatus('COMPLETED'))
useVoiceAction('filterCancelled', () => filterByStatus('CANCELLED'))

onMounted(loadData)
</script>
