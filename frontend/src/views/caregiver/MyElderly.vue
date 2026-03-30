<template>
  <div class="page-card">
    <h3 class="page-title">我的老人</h3>
    <el-table :data="elderlyList" border stripe>
      <el-table-column prop="name" label="姓名" />
      <el-table-column label="头像" width="80"><template #default="{row}"><el-avatar :size="40" :src="row.avatar">{{ row.name?.charAt(0) }}</el-avatar></template></el-table-column>
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="age" label="年龄" width="70" />
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button size="small" type="primary" @click="viewHealth(row.id)">查看健康</el-button>
          <el-button size="small" @click="viewProfile(row.id)">查看档案</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="profileVisible" title="老人档案" width="500px">
      <el-descriptions :column="2" border v-if="profile">
        <el-descriptions-item label="姓名">{{ profile.userName }}</el-descriptions-item>
        <el-descriptions-item label="血型">{{ profile.bloodType }}</el-descriptions-item>
        <el-descriptions-item label="居住情况">{{ profile.livingCondition }}</el-descriptions-item>
        <el-descriptions-item label="失能等级">{{ profile.disabilityLevel }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ profile.emergencyContact }}</el-descriptions-item>
        <el-descriptions-item label="紧急电话">{{ profile.emergencyPhone }}</el-descriptions-item>
        <el-descriptions-item label="病史" :span="2">{{ profile.medicalHistory }}</el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">{{ profile.allergyHistory }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { assignmentMyElderly, elderlyProfileByUser } from '../../api'
import { useRouter } from 'vue-router'
const router = useRouter()
const elderlyList = ref([]); const profileVisible = ref(false); const profile = ref(null)
const viewHealth = (id) => { router.push({ path: '/caregiver/healthMonitor', query: { elderlyId: id } }) }
const viewProfile = async (userId) => { const res = await elderlyProfileByUser(userId); profile.value = res.data; profileVisible.value = true }
onMounted(async () => { const res = await assignmentMyElderly(); elderlyList.value = res.data })
</script>
