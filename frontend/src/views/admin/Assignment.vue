<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">护工分配管理</h3>
      <el-button type="success" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增分配</el-button>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="caregiverName" label="护工姓名" />
      <el-table-column prop="caregiverPhone" label="护工电话" />
      <el-table-column prop="elderlyName" label="老人姓名" />
      <el-table-column prop="elderlyPhone" label="老人电话" />
      <el-table-column prop="startDate" label="开始日期" />
      <el-table-column prop="endDate" label="结束日期" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'有效':'无效' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑分配':'新增分配'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="护工"><el-select v-model="form.caregiverId" filterable style="width:100%"><el-option v-for="u in caregivers" :key="u.id" :label="u.name" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="老人"><el-select v-model="form.elderlyId" filterable style="width:100%"><el-option v-for="u in elderly" :key="u.id" :label="u.name" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { assignmentPage, assignmentAdd, assignmentUpdate, assignmentDelete, userList } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10 }); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({})
const caregivers = ref([]); const elderly = ref([])
const loadData = async () => { const res = await assignmentPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1 }; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await assignmentUpdate(form.value); else await assignmentAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await assignmentDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(async () => {
  loadData()
  const r1 = await userList({ role: 'CAREGIVER' }); caregivers.value = r1.data
  const r2 = await userList({ role: 'USER' }); elderly.value = r2.data
})
</script>
