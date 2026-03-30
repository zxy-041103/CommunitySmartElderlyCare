<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">服务类别管理</h3>
      <el-button type="success" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增类别</el-button>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="类别名称" />
      <el-table-column prop="icon" label="图标" width="80"><template #default="{row}"><el-icon :size="24"><component :is="row.icon" /></el-icon></template></el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'禁用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑类别':'新增类别'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="Element Plus图标名称" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { serviceCategoryPage, serviceCategoryAdd, serviceCategoryUpdate, serviceCategoryDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10 }); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({})
const loadData = async () => { const res = await serviceCategoryPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1, sortOrder: 0 }; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await serviceCategoryUpdate(form.value); else await serviceCategoryAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await serviceCategoryDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
