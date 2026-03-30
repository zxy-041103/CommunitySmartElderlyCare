<template>
  <div>
    <div class="page-card">
      <h3 class="page-title">活动中心</h3>
      <el-row :gutter="16">
        <el-col :span="8" v-for="act in activities" :key="act.id">
          <el-card shadow="hover" style="border-radius:12px;margin-bottom:16px;">
            <el-image :src="act.image" style="width:100%;height:180px;border-radius:8px;" fit="cover"><template #error><div style="height:180px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;border-radius:8px;"><el-icon :size="40"><Picture /></el-icon></div></template></el-image>
            <div style="padding:12px 0 0;">
              <h3>{{ act.title }}</h3>
              <p style="color:#909399;font-size:13px;margin:8px 0;line-height:1.6;">{{ act.description?.substring(0,60) }}...</p>
              <p style="font-size:13px;color:#606266;"><el-icon><Location /></el-icon> {{ act.location }}</p>
              <p style="font-size:13px;color:#606266;"><el-icon><Clock /></el-icon> {{ act.startTime }} ~ {{ act.endTime }}</p>
              <div style="display:flex;justify-content:space-between;align-items:center;margin-top:12px;">
                <span style="font-size:13px;color:#909399;">{{ act.currentParticipants }}/{{ act.maxParticipants }}人已报名</span>
                <template v-if="act.status !== 'ENDED' && act.status !== 'CANCELLED'">
                  <el-button type="primary" size="small" @click="register(act.id)" v-if="!act.registered" :disabled="act.currentParticipants >= act.maxParticipants">{{ act.currentParticipants >= act.maxParticipants ? '已满' : '报名' }}</el-button>
                  <el-button type="info" size="small" @click="cancelReg(act.id)" v-else>取消报名</el-button>
                </template>
                <el-tag v-else type="info" size="small">已结束</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { activityPublished, activityGet, activityRegister, activityCancelReg } from '../../api'
import { ElMessage } from 'element-plus'
const activities = ref([])

const loadData = async () => {
  const res = await activityPublished({ pageNum: 1, pageSize: 20 })
  const list = res.data.records
  for (let a of list) { try { const r = await activityGet(a.id); a.registered = r.data.registered } catch(e) {} }
  activities.value = list
}
const register = async (id) => { await activityRegister(id); ElMessage.success('报名成功'); loadData() }
const cancelReg = async (id) => { await activityCancelReg(id); ElMessage.success('已取消报名'); loadData() }
onMounted(loadData)
</script>
