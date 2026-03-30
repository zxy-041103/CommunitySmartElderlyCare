<template>
  <div class="page-card">
    <h3 class="page-title">紧急响应</h3>
    <el-table :data="emergencies" border stripe>
      <el-table-column prop="elderlyName" label="老人" />
      <el-table-column prop="elderlyPhone" label="电话" />
      <el-table-column prop="emergencyType" label="类型" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="{PENDING:'danger',PROCESSING:'warning',RESOLVED:'success'}[row.status]">{{ {PENDING:'待处理',PROCESSING:'处理中',RESOLVED:'已解决'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="求助时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button size="small" type="warning" @click="respond(row.id)" v-if="row.status==='PENDING'">响应</el-button>
          <el-button size="small" type="success" @click="openResolve(row)" v-if="row.status==='PROCESSING'">解决</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="resolveVisible" title="解决求助" width="400px">
      <el-form :model="resolveForm" label-width="80px">
        <el-form-item label="解决备注"><el-input v-model="resolveForm.resolveNote" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="resolveVisible=false">取消</el-button><el-button type="success" @click="submitResolve">确认解决</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { emergencyPending, emergencyRespond, emergencyResolve } from '../../api'
import { ElMessage } from 'element-plus'
const emergencies = ref([]); const resolveVisible = ref(false); const resolveForm = ref({})
const loadData = async () => { const res = await emergencyPending(); emergencies.value = res.data }
const respond = async (id) => { await emergencyRespond(id); ElMessage.success('已响应'); loadData() }
const openResolve = (row) => { resolveForm.value = { id: row.id, resolveNote: '' }; resolveVisible.value = true }
const submitResolve = async () => { await emergencyResolve(resolveForm.value.id, resolveForm.value); ElMessage.success('已解决'); resolveVisible.value = false; loadData() }
onMounted(loadData)
</script>
