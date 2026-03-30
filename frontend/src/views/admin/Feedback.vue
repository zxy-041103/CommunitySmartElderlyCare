<template>
  <div class="page-card">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3 class="page-title" style="margin-bottom:0">意见反馈管理</h3>
      <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadData"><el-option label="待回复" value="PENDING" /><el-option label="已回复" value="REPLIED" /></el-select>
    </div>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="userName" label="用户" />
      <el-table-column prop="type" label="类型" width="90"><template #default="{row}"><el-tag :type="{SUGGESTION:'primary',COMPLAINT:'danger',PRAISE:'success'}[row.type]">{{ {SUGGESTION:'建议',COMPLAINT:'投诉',PRAISE:'表扬'}[row.type] }}</el-tag></template></el-table-column>
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="图片" width="80"><template #default="{row}"><el-image :src="row.image" class="table-image" fit="cover" v-if="row.image" /></template></el-table-column>
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='REPLIED'?'success':'warning'">{{ row.status==='REPLIED'?'已回复':'待回复' }}</el-tag></template></el-table-column>
      <el-table-column prop="reply" label="回复" show-overflow-tooltip />
      <el-table-column prop="replyByName" label="回复人" />
      <el-table-column prop="createTime" label="提交时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}">
          <el-button size="small" type="primary" @click="openReply(row)" v-if="row.status==='PENDING'">回复</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:end;" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData" />
    <el-dialog v-model="replyDialogVisible" title="回复反馈" width="450px">
      <el-form :model="replyForm" label-width="60px">
        <el-form-item label="内容"><p>{{ replyForm.content }}</p></el-form-item>
        <el-form-item label="回复"><el-input v-model="replyForm.reply" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="replyDialogVisible=false">取消</el-button><el-button type="primary" @click="submitReply">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { feedbackPage, feedbackReply, feedbackDelete } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const query = ref({ pageNum: 1, pageSize: 10, status: '' }); const tableData = ref([]); const total = ref(0); const replyDialogVisible = ref(false); const replyForm = ref({})
const loadData = async () => { const res = await feedbackPage(query.value); tableData.value = res.data.records; total.value = res.data.total }
const openReply = (row) => { replyForm.value = { id: row.id, content: row.content, reply: '' }; replyDialogVisible.value = true }
const submitReply = async () => { await feedbackReply(replyForm.value.id, replyForm.value); ElMessage.success('回复成功'); replyDialogVisible.value = false; loadData() }
const del = (id) => { ElMessageBox.confirm('确认删除？').then(async () => { await feedbackDelete(id); ElMessage.success('删除成功'); loadData() }) }
onMounted(loadData)
</script>
