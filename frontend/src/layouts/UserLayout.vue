<template>
  <div class="user-layout">
    <div class="top-header">
      <div class="logo">🏠 智慧养老平台</div>
      <el-menu mode="horizontal" :default-active="activeMenu" :router="true" background-color="transparent" text-color="rgba(255,255,255,0.8)" active-text-color="#fff">
        <el-menu-item index="/user/home"><el-icon><HomeFilled /></el-icon>首页</el-menu-item>
        <el-menu-item index="/user/health"><el-icon><Monitor /></el-icon>健康中心</el-menu-item>
        <el-menu-item index="/user/service"><el-icon><Service /></el-icon>服务中心</el-menu-item>
        <el-menu-item index="/user/appointment"><el-icon><Tickets /></el-icon>我的预约</el-menu-item>
        <el-menu-item index="/user/emergency"><el-icon><AlarmClock /></el-icon>紧急求助</el-menu-item>
        <el-menu-item index="/user/activity"><el-icon><Flag /></el-icon>活动中心</el-menu-item>
        <el-menu-item index="/user/feedback"><el-icon><ChatDotRound /></el-icon>意见反馈</el-menu-item>
        <el-menu-item index="/user/profile"><el-icon><User /></el-icon>个人中心</el-menu-item>
      </el-menu>
      <div style="display:flex;align-items:center;gap:12px;">
        <el-dropdown>
          <span style="color:#fff;cursor:pointer;display:flex;align-items:center;gap:6px;">
            <span>字体大小</span><el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="setFontSize('normal')">标准</el-dropdown-item>
              <el-dropdown-item @click="setFontSize('large')">大号</el-dropdown-item>
              <el-dropdown-item @click="setFontSize('xlarge')">超大号</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-dropdown @command="handleCommand">
          <span style="color:#fff;cursor:pointer;display:flex;align-items:center;gap:6px;">
            <el-avatar :size="28" :src="userInfo.avatar">{{ userInfo.name?.charAt(0) }}</el-avatar>
            {{ userInfo.name }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div class="main-content">
      <router-view />
    </div>
    <VoiceButton />
    <div class="user-footer">
      © 2026 社区智慧养老监护管理平台 - 智慧养老，温暖守护
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import VoiceButton from '../components/VoiceButton.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const activeMenu = computed(() => route.path)

onMounted(() => { userStore.initFontSize() })

const setFontSize = (size) => { userStore.setFontSize(size) }
const handleCommand = (cmd) => {
  if (cmd === 'logout') { userStore.logout(); router.push('/login') }
  else if (cmd === 'profile') { router.push('/user/profile') }
}
</script>
