<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">健康预警管理</h3>
      <div style="display:flex;gap:12px;">
        <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadData"><el-option label="待处理" value="PENDING" /><el-option label="已处理" value="PROCESSED" /><el-option label="已忽略" value="IGNORED" /></el-select>
        <el-select v-model="query.alertLevel" placeholder="级别" clearable style="width:120px" @change="loadData"><el-option label="提示" value="INFO" /><el-option label="警告" value="WARNING" /><el-option label="危险" value="DANGER" /></el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="elderlyName" label="老人姓名" />
      <el-table-column prop="alertType" label="预警类型" />
      <el-table-column prop="alertLevel" label="预警级别" width="90"><template #default="{row}"><el-tag :type="levelType(row.alertLevel)">{{ levelLabel(row.alertLevel) }}</el-tag></template></el-table-column>
      <el-table-column prop="alertContent" label="预警内容" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag></template></el-table-column>
      <el-table-column prop="processorName" label="处理人" />
      <el-table-column prop="processNote" label="处理备注" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button size="small" type="success" @click="openProcess(row)" v-if="row.status==='PENDING'">处理</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />

    <el-dialog v-model="processDialogVisible" title="处理预警" width="450px">
      <el-form :model="processForm" label-width="80px">
        <el-form-item label="处理状态"><el-select v-model="processForm.status" style="width:100%"><el-option label="已处理" value="PROCESSED" /><el-option label="已忽略" value="IGNORED" /></el-select></el-form-item>
        <el-form-item label="处理备注"><el-input v-model="processForm.processNote" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="processDialogVisible=false">取消</el-button><el-button type="primary" @click="submitProcess">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { healthAlertPage, healthAlertProcess, healthAlertDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ pageNum: 1, pageSize: 10, status: '', alertLevel: '' })
const tableData = ref([]); const total = ref(0); const processDialogVisible = ref(false)
const processForm = ref({ id: null, status: 'PROCESSED', processNote: '' })

const levelLabel = (l) => ({ INFO: '提示', WARNING: '警告', DANGER: '危险' }[l] || l)
const levelType = (l) => ({ INFO: 'info', WARNING: 'warning', DANGER: 'danger' }[l] || '')
const statusLabel = (s) => ({ PENDING: '待处理', PROCESSED: '已处理', IGNORED: '已忽略' }[s] || s)
const statusType = (s) => ({ PENDING: 'warning', PROCESSED: 'success', IGNORED: 'info' }[s] || '')

const loadData = async () => { const res = await healthAlertPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openProcess = (row) => { processForm.value = { id: row.id, status: 'PROCESSED', processNote: '' }; processDialogVisible.value = true }
const submitProcess = async () => { await healthAlertProcess(processForm.value.id, processForm.value); ElMessage.success('处理成功'); processDialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await healthAlertDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
