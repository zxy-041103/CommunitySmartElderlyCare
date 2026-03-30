<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">活动管理</h3>
      <div style="display:flex;gap:12px;">
        <el-input v-model="query.title" placeholder="搜索标题" clearable style="width:160px" @clear="loadData" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadData"><el-option label="即将开始" value="UPCOMING" /><el-option label="进行中" value="ONGOING" /><el-option label="已结束" value="ENDED" /></el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="openDialog(null)">新增活动</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="80"><template #default="{row}"><el-image :src="row.image" class="table-image" fit="cover" v-if="row.image" /></template></el-table-column>
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="location" label="地点" />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column label="报名" width="90"><template #default="{row}">{{ row.currentParticipants }}/{{ row.maxParticipants }}</template></el-table-column>
      <el-table-column prop="organizerName" label="组织者" />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="{UPCOMING:'primary',ONGOING:'success',ENDED:'info',CANCELLED:'danger'}[row.status]">{{ {UPCOMING:'即将开始',ONGOING:'进行中',ENDED:'已结束',CANCELLED:'已取消'}[row.status] }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑活动':'新增活动'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="最大人数"><el-input-number v-model="form.maxParticipants" :min="1" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="即将开始" value="UPCOMING" /><el-option label="进行中" value="ONGOING" /><el-option label="已结束" value="ENDED" /><el-option label="已取消" value="CANCELLED" /></el-select></el-form-item>
        <el-form-item label="图片"><el-upload action="/api/file/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="(r)=>form.image=r.data"><el-button size="small">上传图片</el-button></el-upload><el-image v-if="form.image" :src="form.image" style="width:120px;height:80px;margin-top:8px;" fit="cover" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { activityPage, activityAdd, activityUpdate, activityDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, title: '', status: '' }); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({})
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))
const loadData = async () => { const res = await activityPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 'UPCOMING', maxParticipants: 30, currentParticipants: 0 }; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await activityUpdate(form.value); else await activityAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await activityDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
