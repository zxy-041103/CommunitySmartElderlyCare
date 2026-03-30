<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">排班管理</h3>
      <div style="display:flex;gap:12px;">
        <el-select v-model="query.caregiverId" placeholder="选择护工" clearable style="width:140px" @change="loadData"><el-option v-for="u in caregivers" :key="u.id" :label="u.name" :value="u.id" /></el-select>
        <el-date-picker v-model="query.workDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" clearable @change="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="openDialog(null)">新增排班</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="caregiverName" label="护工" />
      <el-table-column prop="workDate" label="工作日期" />
      <el-table-column prop="shiftType" label="班次" width="90"><template #default="{row}"><el-tag :type="{MORNING:'success',AFTERNOON:'warning',NIGHT:'danger'}[row.shiftType]">{{ {MORNING:'早班',AFTERNOON:'午班',NIGHT:'晚班'}[row.shiftType] }}</el-tag></template></el-table-column>
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag>{{ {SCHEDULED:'已排班',ON_DUTY:'值班中',OFF_DUTY:'已下班'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column prop="note" label="备注" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑排班':'新增排班'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="护工"><el-select v-model="form.caregiverId" filterable style="width:100%"><el-option v-for="u in caregivers" :key="u.id" :label="u.name" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="日期"><el-date-picker v-model="form.workDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="班次"><el-select v-model="form.shiftType" style="width:100%"><el-option label="早班" value="MORNING" /><el-option label="午班" value="AFTERNOON" /><el-option label="晚班" value="NIGHT" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.note" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { workSchedulePage, workScheduleAdd, workScheduleUpdate, workScheduleDelete, userList } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, caregiverId: null, workDate: '' }); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({}); const caregivers = ref([])
const loadData = async () => { const res = await workSchedulePage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { shiftType: 'MORNING', status: 'SCHEDULED' }; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await workScheduleUpdate(form.value); else await workScheduleAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await workScheduleDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(async () => { loadData(); const r = await userList({ role: 'CAREGIVER' }); caregivers.value = r.data })
</script>
