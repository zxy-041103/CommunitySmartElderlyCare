<template>
  <div class="login-page">
    <div style="position:absolute;top:40px;left:50%;transform:translateX(-50%);text-align:center;color:rgba(255,255,255,0.9);">
      <h1 style="font-size:32px;margin-bottom:8px;">🏠 社区智慧养老监护管理平台</h1>
      <p style="font-size:14px;color:rgba(255,255,255,0.7);">Smart Community Elderly Care Monitoring Platform</p>
    </div>
    <div class="login-card">
      <div class="login-title">
        <h1>用户登录</h1>
        <p>欢迎使用社区智慧养老监护管理平台</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" placeholder="请输入密码" type="password" show-password size="large" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" size="large" style="width:100%;font-size:16px;height:44px;">登 录</el-button>
        </el-form-item>
      </el-form>
      <div style="display:flex;justify-content:center;gap:16px;margin-top:16px;">
        <el-tag @click="fillAccount('admin','123456')" style="cursor:pointer">管理员</el-tag>
        <el-tag @click="fillAccount('test1','123456')" type="success" style="cursor:pointer">测试用户</el-tag>
        <el-tag @click="fillAccount('test2','123456')" type="warning" style="cursor:pointer">护工</el-tag>
        <el-tag @click="fillAccount('test3','123456')" type="danger" style="cursor:pointer">社区服务</el-tag>
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
