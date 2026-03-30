<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">服务记录</h3>
      <el-button type="success" @click="openDialog"><el-icon><Plus /></el-icon>添加记录</el-button>
    </div>
    <el-table :data="records" border stripe>
      <el-table-column prop="elderlyName" label="老人" />
      <el-table-column prop="serviceContent" label="服务内容" show-overflow-tooltip />
      <el-table-column prop="serviceTime" label="服务时间" width="160" />
      <el-table-column prop="duration" label="时长(分)" width="80" />
      <el-table-column prop="note" label="备注" show-overflow-tooltip />
      <el-table-column label="操作" width="100"><template #default="{row}"><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template></el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="添加服务记录" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="老人"><el-select v-model="form.elderlyId" filterable style="width:100%"><el-option v-for="u in elderlyList" :key="u.id" :label="u.name" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="服务内容"><el-input v-model="form.serviceContent" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="时长(分)"><el-input-number v-model="form.duration" :min="1" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.note" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { serviceRecordMy, serviceRecordAdd, serviceRecordDelete, assignmentMyElderly } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const records = ref([]); const dialogVisible = ref(false); const form = ref({}); const elderlyList = ref([])
const loadData = async () => { const res = await serviceRecordMy(); records.value = res.data }
const openDialog = () => { form.value = { duration: 30 }; dialogVisible.value = true }
const submit = async () => { await serviceRecordAdd(form.value); ElMessage.success('记录成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await serviceRecordDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(async () => { loadData(); const r = await assignmentMyElderly(); elderlyList.value = r.data })
</script>
