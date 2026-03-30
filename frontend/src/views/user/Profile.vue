<template>
  <div>
    <div class="page-card">
      <h3 class="page-title">个人中心</h3>
      <el-form :model="form" label-width="100px" style="max-width:600px;">
        <el-form-item label="头像">
          <el-upload action="/api/file/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="(r)=>{form.avatar=r.data}">
            <el-avatar :size="80" :src="form.avatar">{{ form.name?.charAt(0) }}</el-avatar>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio label="男" /><el-radio label="女" /></el-radio-group></el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="150" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item><el-button type="primary" @click="saveProfile">保存信息</el-button></el-form-item>
      </el-form>
    </div>
    <div class="page-card">
      <h3 class="page-title">修改密码</h3>
      <el-form :model="pwdForm" label-width="100px" style="max-width:400px;">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="primary" @click="changePwd">修改密码</el-button></el-form-item>
      </el-form>
    </div>
    <div class="page-card">
      <h3 class="page-title">适老化设置</h3>
      <el-form label-width="100px" style="max-width:400px;">
        <el-form-item label="字体大小">
          <el-radio-group :model-value="fontSize" @change="changeFontSize">
            <el-radio-button label="normal">标准</el-radio-button><el-radio-button label="large">大号</el-radio-button><el-radio-button label="xlarge">超大号</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { updateProfile, updatePassword } from '../../api'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'
const userStore = useUserStore()
const form = ref({ ...userStore.userInfo })
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const fontSize = computed(() => userStore.fontSize)
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const saveProfile = async () => { const res = await updateProfile(form.value); userStore.setUserInfo(res.data); ElMessage.success('保存成功') }
const changePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) return ElMessage.warning('请填写完整')
  await updatePassword(pwdForm.value); ElMessage.success('密码修改成功'); pwdForm.value = { oldPassword: '', newPassword: '' }
}
const changeFontSize = (size) => { userStore.setFontSize(size) }
onMounted(() => { userStore.initFontSize() })
</script>
