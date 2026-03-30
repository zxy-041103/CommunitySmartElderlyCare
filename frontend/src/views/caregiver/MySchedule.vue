<template>
  <div class="page-card">
    <h3 class="page-title">我的排班</h3>
    <el-table :data="schedules" border stripe>
      <el-table-column prop="workDate" label="日期" />
      <el-table-column prop="shiftType" label="班次" width="90"><template #default="{row}"><el-tag :type="{MORNING:'success',AFTERNOON:'warning',NIGHT:'danger'}[row.shiftType]">{{ {MORNING:'早班',AFTERNOON:'午班',NIGHT:'晚班'}[row.shiftType] }}</el-tag></template></el-table-column>
      <el-table-column prop="status" label="状态"><template #default="{row}"><el-tag>{{ {SCHEDULED:'已排班',ON_DUTY:'值班中',OFF_DUTY:'已下班'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column prop="note" label="备注" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { workScheduleMy } from '../../api'
const schedules = ref([])
onMounted(async () => {
  const today = new Date()
  const start = new Date(today.getTime() - 7 * 86400000).toISOString().substring(0,10)
  const end = new Date(today.getTime() + 30 * 86400000).toISOString().substring(0,10)
  const res = await workScheduleMy({ startDate: start, endDate: end }); schedules.value = res.data
})
</script>
