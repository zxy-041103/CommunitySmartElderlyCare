<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">老人档案管理</h3>
      <div style="display:flex;gap:12px;">
        <el-input v-model="query.name" placeholder="搜索老人姓名" clearable style="width:180px" @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="openDialog(null)">新增档案</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="照片" width="80"><template #default="{row}"><el-image :src="row.photo" class="table-image" fit="cover" v-if="row.photo"><template #error><el-icon :size="30"><Picture /></el-icon></template></el-image></template></el-table-column>
      <el-table-column prop="userName" label="老人姓名" />
      <el-table-column prop="userGender" label="性别" width="70" />
      <el-table-column prop="userAge" label="年龄" width="70" />
      <el-table-column prop="userPhone" label="联系电话" />
      <el-table-column prop="emergencyContact" label="紧急联系人" />
      <el-table-column prop="emergencyPhone" label="紧急电话" />
      <el-table-column prop="bloodType" label="血型" width="70" />
      <el-table-column prop="livingCondition" label="居住情况" />
      <el-table-column prop="disabilityLevel" label="失能等级" />
      <el-table-column prop="medicalHistory" label="病史" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}"><el-button size="small" @click="openDialog(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="form.id?'编辑档案':'新增档案'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="关联用户"><el-select v-model="form.userId" filterable placeholder="选择老人用户" style="width:100%"><el-option v-for="u in userList" :key="u.id" :label="u.name" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="紧急联系人"><el-input v-model="form.emergencyContact" /></el-form-item>
        <el-form-item label="紧急电话"><el-input v-model="form.emergencyPhone" /></el-form-item>
        <el-form-item label="血型"><el-select v-model="form.bloodType" style="width:100%"><el-option v-for="t in ['A型','B型','O型','AB型']" :key="t" :label="t" :value="t" /></el-select></el-form-item>
        <el-form-item label="居住情况"><el-select v-model="form.livingCondition" style="width:100%"><el-option v-for="t in ['独居','与家人同住','养老院']" :key="t" :label="t" :value="t" /></el-select></el-form-item>
        <el-form-item label="失能等级"><el-select v-model="form.disabilityLevel" style="width:100%"><el-option v-for="t in ['正常','轻度','中度','重度']" :key="t" :label="t" :value="t" /></el-select></el-form-item>
        <el-form-item label="病史"><el-input v-model="form.medicalHistory" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="过敏史"><el-input v-model="form.allergyHistory" /></el-form-item>
        <el-form-item label="照片"><el-upload action="/api/file/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="(r)=>form.photo=r.data"><el-button size="small">上传照片</el-button></el-upload><el-image v-if="form.photo" :src="form.photo" style="width:80px;height:80px;margin-top:8px;" fit="cover" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { elderlyProfilePage, elderlyProfileAdd, elderlyProfileUpdate, elderlyProfileDelete, userList as fetchUserList } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ pageNum: 1, pageSize: 10, name: '' })
const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const form = ref({})
const userList = ref([])
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const loadData = async () => { const res = await elderlyProfilePage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const loadUsers = async () => { const res = await fetchUserList({ role: 'USER' }); userList.value = res.data }
const openDialog = (row) => { form.value = row ? { ...row } : {}; dialogVisible.value = true }
const submit = async () => { if (form.value.id) await elderlyProfileUpdate(form.value); else await elderlyProfileAdd(form.value); ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await elderlyProfileDelete(id); ElMessage.success('删除成功'); loadData() }) }

onMounted(() => { loadData(); loadUsers() })
</script>
