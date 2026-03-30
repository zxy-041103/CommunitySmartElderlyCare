<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">意见反馈</h3>
      <el-button type="primary" @click="openDialog">提交反馈</el-button>
    </div>
    <el-table :data="feedbacks" border stripe>
      <el-table-column prop="type" label="类型" width="90"><template #default="{row}"><el-tag :type="{SUGGESTION:'primary',COMPLAINT:'danger',PRAISE:'success'}[row.type]">{{ {SUGGESTION:'建议',COMPLAINT:'投诉',PRAISE:'表扬'}[row.type] }}</el-tag></template></el-table-column>
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="图片" width="80"><template #default="{row}"><el-image :src="row.image" class="table-image" fit="cover" v-if="row.image" /></template></el-table-column>
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='REPLIED'?'success':'warning'">{{ row.status==='REPLIED'?'已回复':'待回复' }}</el-tag></template></el-table-column>
      <el-table-column prop="reply" label="回复" show-overflow-tooltip />
      <el-table-column prop="createTime" label="提交时间" width="160" />
    </el-table>
    <el-dialog v-model="dialogVisible" title="提交反馈" width="500px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="类型"><el-select v-model="form.type" style="width:100%"><el-option label="建议" value="SUGGESTION" /><el-option label="投诉" value="COMPLAINT" /><el-option label="表扬" value="PRAISE" /></el-select></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="图片"><el-upload action="/api/file/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="(r)=>form.image=r.data"><el-button size="small">上传图片</el-button></el-upload><el-image v-if="form.image" :src="form.image" style="width:80px;height:80px;margin-top:8px;" fit="cover" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { feedbackMy, feedbackAdd } from '../../api'
import { ElMessage } from 'element-plus'
import { useVoiceAction } from '../../utils/voiceAction'

const feedbacks = ref([]); const dialogVisible = ref(false)
const form = ref({ type: 'SUGGESTION', content: '', image: '' })
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const openDialog = (type = 'SUGGESTION') => {
  form.value = { type, content: '', image: '' }
  dialogVisible.value = true
}
const submit = async () => {
  if (!form.value.content) return ElMessage.warning('请填写内容')
  await feedbackAdd(form.value); ElMessage.success('提交成功'); dialogVisible.value = false; loadData()
}
const loadData = async () => { const res = await feedbackMy(); feedbacks.value = res.data }

// 语音指令监听
useVoiceAction('openFeedbackSuggestion', () => openDialog('SUGGESTION'))
useVoiceAction('openFeedbackComplaint', () => openDialog('COMPLAINT'))
useVoiceAction('openFeedbackPraise', () => openDialog('PRAISE'))

onMounted(loadData)
</script>
