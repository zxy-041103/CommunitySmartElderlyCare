<template>
  <div class="admin-layout">
    <div class="sidebar">
      <div class="logo-area">
        <h2>🏠 智慧养老平台</h2>
      </div>
      <el-menu :default-active="activeMenu" :router="true" text-color="rgba(255,255,255,0.75)" active-text-color="#fff" background-color="transparent">
        <template v-if="role === 'ADMIN'">
          <el-menu-item index="/admin/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据统计</span></el-menu-item>
          <el-menu-item index="/admin/user"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
          <el-menu-item index="/admin/elderlyProfile"><el-icon><Notebook /></el-icon><span>老人档案</span></el-menu-item>
          <el-sub-menu index="health-sub">
            <template #title><el-icon><Monitor /></el-icon><span>健康管理</span></template>
            <el-menu-item index="/admin/healthData">健康数据</el-menu-item>
            <el-menu-item index="/admin/healthAlert">健康预警</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/admin/emergency"><el-icon><AlarmClock /></el-icon><span>紧急救助</span></el-menu-item>
          <el-sub-menu index="service-sub">
            <template #title><el-icon><Service /></el-icon><span>服务管理</span></template>
            <el-menu-item index="/admin/serviceCategory">服务类别</el-menu-item>
            <el-menu-item index="/admin/serviceItem">服务项目</el-menu-item>
            <el-menu-item index="/admin/appointment">预约管理</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/admin/assignment"><el-icon><Connection /></el-icon><span>护工分配</span></el-menu-item>
          <el-menu-item index="/admin/announcement"><el-icon><Bell /></el-icon><span>公告管理</span></el-menu-item>
          <el-menu-item index="/admin/activity"><el-icon><Flag /></el-icon><span>活动管理</span></el-menu-item>
          <el-menu-item index="/admin/feedback"><el-icon><ChatDotRound /></el-icon><span>意见反馈</span></el-menu-item>
          <el-menu-item index="/admin/workSchedule"><el-icon><Calendar /></el-icon><span>排班管理</span></el-menu-item>
        </template>
        <template v-if="role === 'CAREGIVER'">
          <el-menu-item index="/caregiver/dashboard"><el-icon><DataAnalysis /></el-icon><span>工作台</span></el-menu-item>
          <el-menu-item index="/caregiver/myElderly"><el-icon><User /></el-icon><span>我的老人</span></el-menu-item>
          <el-menu-item index="/caregiver/healthMonitor"><el-icon><Monitor /></el-icon><span>健康监测</span></el-menu-item>
          <el-menu-item index="/caregiver/serviceRecord"><el-icon><Document /></el-icon><span>服务记录</span></el-menu-item>
          <el-menu-item index="/caregiver/mySchedule"><el-icon><Calendar /></el-icon><span>我的排班</span></el-menu-item>
          <el-menu-item index="/caregiver/emergencyResponse"><el-icon><AlarmClock /></el-icon><span>紧急响应</span></el-menu-item>
        </template>
        <template v-if="role === 'COMMUNITY'">
          <el-menu-item index="/community/dashboard"><el-icon><DataAnalysis /></el-icon><span>工作台</span></el-menu-item>
          <el-menu-item index="/community/appointmentProcess"><el-icon><Tickets /></el-icon><span>预约处理</span></el-menu-item>
          <el-menu-item index="/community/activity"><el-icon><Flag /></el-icon><span>活动管理</span></el-menu-item>
          <el-menu-item index="/community/announcement"><el-icon><Bell /></el-icon><span>公告管理</span></el-menu-item>
          <el-menu-item index="/community/feedback"><el-icon><ChatDotRound /></el-icon><span>反馈处理</span></el-menu-item>
          <el-menu-item index="/community/serviceStats"><el-icon><TrendCharts /></el-icon><span>服务统计</span></el-menu-item>
        </template>
      </el-menu>
    </div>
    <div class="main-area">
      <div class="header">
        <div style="display:flex;align-items:center;gap:12px;">
          <span style="font-size:16px;font-weight:500;">{{ $route.meta.title }}</span>
        </div>
        <div style="display:flex;align-items:center;gap:16px;">
          <el-dropdown @command="handleCommand">
            <span style="cursor:pointer;display:flex;align-items:center;gap:8px;">
              <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.name?.charAt(0) }}</el-avatar>
              <span>{{ userInfo.name }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div class="content-area">
        <router-view />
      </div>
    </div>
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { updatePassword } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const role = computed(() => userStore.userInfo.role)
const activeMenu = computed(() => route.path)
const pwdDialogVisible = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '' })

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (cmd === 'password') {
    pwdDialogVisible.value = true
    pwdForm.value = { oldPassword: '', newPassword: '' }
  }
}
const submitPassword = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    return ElMessage.warning('请填写完整')
  }
  await updatePassword(pwdForm.value)
  ElMessage.success('密码修改成功')
  pwdDialogVisible.value = false
}
</script>
