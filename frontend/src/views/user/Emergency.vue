<template>
  <div>
    <div class="page-card" style="text-align:center;">
      <h3 class="page-title">紧急求助</h3>
      <p style="color:#909399;margin-bottom:24px;">遇到紧急情况，请点击下方按钮一键求助</p>
      <div style="display:flex;justify-content:center;margin-bottom:24px;">
        <button class="sos-btn" @click="openSos"><span style="font-size:48px;">SOS</span><span style="font-size:14px;margin-top:4px;">一键求助</span></button>
      </div>
      <div style="display:flex;justify-content:center;gap:16px;">
        <el-tag size="large" type="danger">跌倒求助</el-tag>
        <el-tag size="large" type="warning">突发疾病</el-tag>
        <el-tag size="large">其他紧急情况</el-tag>
      </div>
    </div>
    <div class="page-card">
      <h3 class="page-title">求助记录</h3>
      <el-table :data="records" border stripe>
        <el-table-column prop="emergencyType" label="紧急类型" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="{PENDING:'danger',PROCESSING:'warning',RESOLVED:'success'}[row.status]">{{ {PENDING:'待处理',PROCESSING:'处理中',RESOLVED:'已解决'}[row.status] }}</el-tag></template></el-table-column>
        <el-table-column prop="responderName" label="响应人" />
        <el-table-column prop="resolveNote" label="解决备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="求助时间" width="160" />
      </el-table>
    </div>
    <el-dialog v-model="sosDialogVisible" title="紧急求助" width="450px">
      <el-form :model="sosForm" label-width="80px">
        <el-form-item label="紧急类型"><el-select v-model="sosForm.emergencyType" style="width:100%"><el-option label="跌倒" value="跌倒" /><el-option label="突发疾病" value="突发疾病" /><el-option label="其他" value="其他" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="sosForm.description" type="textarea" :rows="3" placeholder="请描述紧急情况" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="sosForm.location" placeholder="请输入当前位置" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="sosDialogVisible=false">取消</el-button><el-button type="danger" @click="submitSos">立即求助</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { emergencyMy, emergencyAdd } from '../../api'
import { ElMessage } from 'element-plus'
import { useVoiceAction } from '../../utils/voiceAction'

const records = ref([]); const sosDialogVisible = ref(false)
const sosForm = ref({ emergencyType: '跌倒', description: '', location: '' })

const openSos = (type = '跌倒') => {
  sosForm.value = { emergencyType: type, description: '', location: '' }
  sosDialogVisible.value = true
}
const submitSos = async () => {
  if (!sosForm.value.description) return ElMessage.warning('请描述紧急情况')
  await emergencyAdd(sosForm.value); ElMessage.success('求助已发送，请保持电话畅通'); sosDialogVisible.value = false; loadData()
}
const loadData = async () => { const res = await emergencyMy(); records.value = res.data }

// 语音指令监听
useVoiceAction('openSOS', () => openSos('跌倒'))
useVoiceAction('openSOSFall', () => openSos('跌倒'))
useVoiceAction('openSOSIllness', () => openSos('突发疾病'))

onMounted(loadData)
</script>
