<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">公告管理</h3>
      <div style="display:flex;gap:12px;">
        <el-input v-model="query.title" placeholder="搜索标题" clearable style="width:160px" @clear="loadData" />
        <el-select v-model="query.type" placeholder="类型" clearable style="width:120px" @change="loadData"><el-option label="通知" value="NOTICE" /><el-option label="政策" value="POLICY" /><el-option label="健康贴士" value="HEALTH_TIP" /></el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="openDialog(null)">发布公告</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="80"><template #default="{row}"><el-image :src="row.image" class="table-image" fit="cover" v-if="row.image" /></template></el-table-column>
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100"><template #default="{row}"><el-tag>{{ {NOTICE:'通知',POLICY:'政策',HEALTH_TIP:'健康贴士'}[row.type] }}</el-tag></template></el-table-column>
      <el-table-column prop="publisherName" label="发布人" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{ row.status===1?'已发布':'草稿' }}</el-tag></template></el-table-column>
      <el-table-column prop="top" label="置顶" width="70"><template #default="{row}"><el-tag type="danger" v-if="row.top">置顶</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑公告':'发布公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.type" style="width:100%"><el-option label="通知" value="NOTICE" /><el-option label="政策" value="POLICY" /><el-option label="健康贴士" value="HEALTH_TIP" /></el-select></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="图片"><el-upload action="/api/file/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="(r)=>form.image=r.data"><el-button size="small">上传图片</el-button></el-upload><el-image v-if="form.image" :src="form.image" style="width:120px;height:80px;margin-top:8px;" fit="cover" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="发布" inactive-text="草稿" /></el-form-item>
        <el-form-item label="置顶"><el-switch v-model="form.top" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { announcementPage, announcementAdd, announcementUpdate, announcementDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, title: '', type: '' }); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({})
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))
const loadData = async () => { const res = await announcementPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1, top: 0, type: 'NOTICE' }; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await announcementUpdate(form.value); else await announcementAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await announcementDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
