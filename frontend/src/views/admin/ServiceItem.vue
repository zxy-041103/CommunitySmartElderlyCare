<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">服务项目管理</h3>
      <div style="display:flex;gap:12px;">
        <el-input v-model="query.name" placeholder="搜索服务名称" clearable style="width:160px" @clear="loadData" />
        <el-select v-model="query.categoryId" placeholder="选择类别" clearable style="width:140px" @change="loadData"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="openDialog(null)">新增项目</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="80"><template #default="{row}"><el-image :src="row.image" class="table-image" fit="cover" v-if="row.image"><template #error><el-icon :size="30"><Picture /></el-icon></template></el-image></template></el-table-column>
      <el-table-column prop="name" label="服务名称" />
      <el-table-column prop="categoryName" label="所属类别" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="price" label="价格(元)" width="100" />
      <el-table-column prop="duration" label="时长(分钟)" width="100" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'上架':'下架' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑项目':'新增项目'" width="550px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类别"><el-select v-model="form.categoryId" style="width:100%"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="时长"><el-input-number v-model="form.duration" :min="1" /> 分钟</el-form-item>
        <el-form-item label="图片"><el-upload action="/api/file/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="(r)=>form.image=r.data"><el-button size="small">上传图片</el-button></el-upload><el-image v-if="form.image" :src="form.image" style="width:120px;height:80px;margin-top:8px;" fit="cover" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { serviceItemPage, serviceItemAdd, serviceItemUpdate, serviceItemDelete, serviceCategoryList } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, name: '', categoryId: null }); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({}); const categories = ref([])
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))
const loadData = async () => { const res = await serviceItemPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const loadCategories = async () => { const res = await serviceCategoryList(); categories.value = res.data }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1 }; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await serviceItemUpdate(form.value); else await serviceItemAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await serviceItemDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(() => { loadData(); loadCategories() })
</script>
