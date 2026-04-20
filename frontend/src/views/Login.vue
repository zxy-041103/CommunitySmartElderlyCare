<template>
  <div class="login-page">
    <!-- 背景遮罩层 -->
    <div class="login-overlay"></div>

    <!-- 顶部标题 -->
    <div class="login-header">
      <h1 class="main-title">🏠 社区智慧养老监护管理平台</h1>
      <p class="sub-title">Smart Community Elderly Care Monitoring Platform</p>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-card-inner">
        <div class="login-title">
          <h2>用户登录</h2>
          <p>欢迎使用社区智慧养老监护管理平台</p>
        </div>

        <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="0">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="请输入密码"
              type="password"
              show-password
              size="large"
              prefix-icon="Lock"
              class="custom-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleLogin"
              :loading="loading"
              size="large"
              class="login-button"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 角色快速登录 -->
        <div class="quick-login">
          <p class="quick-login-title">快速登录</p>
          <div class="role-buttons">
            <span class="role-btn" @click="fillAccount('admin', '123456')">管理员</span>
            <span class="role-btn" @click="fillAccount('test1', '123456')">测试用户</span>
            <span class="role-btn" @click="fillAccount('test2', '123456')">护工</span>
            <span class="role-btn" @click="fillAccount('test3', '123456')">社区服务</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { login } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const loginForm = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const fillAccount = (u, p) => { loginForm.value = { username: u, password: p } }

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(loginForm.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success('登录成功')
    const role = res.data.user.role
    if (role === 'ADMIN') router.push('/admin')
    else if (role === 'USER') router.push('/user')
    else if (role === 'CAREGIVER') router.push('/caregiver')
    else if (role === 'COMMUNITY') router.push('/community')
  } finally { loading.value = false }
}
</script>