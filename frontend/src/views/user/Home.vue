<template>
  <div>
    <!-- 公告轮播 -->
    <el-carousel height="200px" style="border-radius:12px;overflow:hidden;margin-bottom:20px;">
      <el-carousel-item v-for="ann in announcements" :key="ann.id">
        <div style="height:100%;background:linear-gradient(135deg,#1a3a5c,#4a90d9);display:flex;align-items:center;padding:0 40px;color:#fff;">
          <div style="flex:1"><h2 style="margin-bottom:8px;">{{ ann.title }}</h2><p style="opacity:0.8;font-size:14px;line-height:1.6;">{{ ann.content?.substring(0,100) }}...</p></div>
          <el-image v-if="ann.image" :src="ann.image" style="width:180px;height:140px;border-radius:8px;" fit="cover"><template #error><div style="width:180px;height:140px;display:flex;align-items:center;justify-content:center;background:rgba(255,255,255,0.1);border-radius:8px;"><el-icon :size="32" color="rgba(255,255,255,0.5)"><Picture /></el-icon></div></template></el-image>
        </div>
      </el-carousel-item>
    </el-carousel>
    <!-- 健康概览 -->
    <div class="page-card">
      <h3 class="page-title">我的健康概览</h3>
      <el-row :gutter="16" v-if="latestHealth">
        <el-col :span="4" v-for="item in healthItems" :key="item.label">
          <div class="stat-card"><div class="stat-icon"><el-icon :size="28"><component :is="item.icon" /></el-icon></div><div class="stat-value" :style="{color:item.color}">{{ item.value }}</div><div class="stat-label">{{ item.label }}</div></div>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无健康数据" />
    </div>
    <!-- 推荐服务 -->
    <div class="page-card">
      <h3 class="page-title">推荐服务</h3>
      <el-row :gutter="16">
        <el-col :span="6" v-for="item in services" :key="item.id">
          <el-card shadow="hover" style="border-radius:12px;cursor:pointer;" @click="$router.push('/user/service')">
            <el-image :src="item.image" style="width:100%;height:140px;border-radius:8px;" fit="cover"><template #error><div style="height:140px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;border-radius:8px;"><el-icon :size="40"><Picture /></el-icon></div></template></el-image>
            <div style="padding:12px 0 0;">
              <h4>{{ item.name }}</h4>
              <div style="display:flex;justify-content:space-between;margin-top:8px;color:#909399;font-size:13px;">
                <span>{{ item.categoryName }}</span><span style="color:#F56C6C;font-weight:600;">¥{{ item.price }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <!-- 近期活动 -->
    <div class="page-card">
      <h3 class="page-title">近期活动</h3>
      <el-row :gutter="16">
        <el-col :span="8" v-for="act in activities" :key="act.id">
          <el-card shadow="hover" style="border-radius:12px;cursor:pointer;" @click="$router.push('/user/activity')">
            <el-image :src="act.image" style="width:100%;height:140px;border-radius:8px;" fit="cover"><template #error><div style="height:140px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;border-radius:8px;"><el-icon :size="40"><Picture /></el-icon></div></template></el-image>
            <div style="padding:12px 0 0;">
              <h4>{{ act.title }}</h4>
              <p style="color:#909399;font-size:13px;margin-top:6px;"><el-icon><Location /></el-icon> {{ act.location }}</p>
              <p style="color:#909399;font-size:13px;"><el-icon><Clock /></el-icon> {{ act.startTime }}</p>
              <div style="display:flex;justify-content:space-between;margin-top:8px;">
                <el-tag size="small" :type="{UPCOMING:'primary',ONGOING:'success'}[act.status]">{{ {UPCOMING:'即将开始',ONGOING:'进行中'}[act.status] }}</el-tag>
                <span style="font-size:12px;color:#909399;">{{ act.currentParticipants }}/{{ act.maxParticipants }}人</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { announcementPublished, healthDataLatest, serviceItemList, activityPublished } from '../../api'
import { useUserStore } from '../../store/user'
const userStore = useUserStore()
const announcements = ref([]); const latestHealth = ref(null); const services = ref([]); const activities = ref([])

const healthItems = computed(() => {
  if (!latestHealth.value) return []
  const h = latestHealth.value
  return [
    { label: '心率(次/分)', value: h.heartRate, icon: 'Monitor', color: '#F56C6C' },
    { label: '收缩压(mmHg)', value: h.systolicPressure, icon: 'TrendCharts', color: '#E6A23C' },
    { label: '舒张压(mmHg)', value: h.diastolicPressure, icon: 'TrendCharts', color: '#409EFF' },
    { label: '血糖(mmol/L)', value: h.bloodSugar, icon: 'Coin', color: '#67C23A' },
    { label: '体温(℃)', value: h.bodyTemperature, icon: 'Sunny', color: '#E6A23C' },
    { label: '血氧(%)', value: h.bloodOxygen, icon: 'UploadFilled', color: '#409EFF' }
  ]
})

onMounted(async () => {
  const r1 = await announcementPublished({ pageNum: 1, pageSize: 5 }); announcements.value = r1.data.records
  try { const r2 = await healthDataLatest(userStore.userInfo.id); latestHealth.value = r2.data } catch(e) {}
  const r3 = await serviceItemList({}); services.value = r3.data.slice(0, 4)
  const r4 = await activityPublished({ pageNum: 1, pageSize: 3 }); activities.value = r4.data.records
})
</script>
