<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">用户管理</h3>
      <div style="display:flex;gap:12px;">
        <el-input v-model="query.name" placeholder="搜索姓名" clearable style="width:160px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="query.role" placeholder="角色筛选" clearable style="width:140px" @change="loadData">
          <el-option label="用户" value="USER" /><el-option label="护工" value="CAREGIVER" /><el-option label="社区服务" value="COMMUNITY" /><el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button type="success" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增用户</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="头像" width="80"><template #default="{row}"><el-avatar :size="40" :src="row.avatar">{{ row.name?.charAt(0) }}</el-avatar></template></el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="role" label="角色"><template #default="{row}"><el-tag :type="roleType(row.role)">{{ roleLabel(row.role) }}</el-tag></template></el-table-column>
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="age" label="年龄" width="70" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'禁用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="resetPwd(row.id)">重置密码</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="form.id?'编辑用户':'新增用户'" width="550px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role" style="width:100%"><el-option label="用户" value="USER" /><el-option label="护工" value="CAREGIVER" /><el-option label="社区服务" value="COMMUNITY" /><el-option label="管理员" value="ADMIN" /></el-select></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio value="男" /><el-radio value="女" /></el-radio-group></el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="150" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userPage, userAdd, userUpdate, userDelete, userResetPassword } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ pageNum: 1, pageSize: 10, name: '', role: '' })
const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false)
const form = ref({})

const roleLabel = (r) => ({ ADMIN: '管理员', USER: '用户', CAREGIVER: '护工', COMMUNITY: '社区服务' }[r] || r)
const roleType = (r) => ({ ADMIN: 'danger', USER: 'info', CAREGIVER: 'warning', COMMUNITY: 'success' }[r] || 'info')

const loadData = async () => {
  const res = await userPage(query.value)
  tableData.value = res.data.records; total.value = res.data.total
}
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1, role: 'USER' }; dialogVisible.value = true }
const submit = async () => {
  if (form.value.id) await userUpdate(form.value); else await userAdd(form.value)
  ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
}
const del = (id) => { ElMessageBox.confirm('确认删除？', '提示').then(async () => { await userDelete(id); ElMessage.success('删除成功'); loadData() }) }
const resetPwd = (id) => { ElMessageBox.confirm('确认重置密码为123456？', '提示').then(async () => { await userResetPassword(id); ElMessage.success('密码已重置') }) }

onMounted(loadData)
</script>
