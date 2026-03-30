<template>
  <div>
    <div class="page-card">
      <h3 class="page-title">服务中心</h3>
      <el-row :gutter="12" style="margin-bottom:20px;">
        <el-col :span="4" v-for="cat in categories" :key="cat.id">
          <div :class="['stat-card',{active:activeCat===cat.id}]" @click="filterByCat(cat.id)" style="cursor:pointer;padding:16px;">
            <el-icon :size="28"><component :is="cat.icon" /></el-icon>
            <div style="margin-top:8px;font-size:14px;">{{ cat.name }}</div>
          </div>
        </el-col>
      </el-row>
    </div>
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in services" :key="item.id">
        <el-card shadow="hover" style="border-radius:12px;margin-bottom:16px;">
          <el-image :src="item.image" style="width:100%;height:160px;border-radius:8px;" fit="cover"><template #error><div style="height:160px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;border-radius:8px;"><el-icon :size="40"><Picture /></el-icon></div></template></el-image>
          <div style="padding:12px 0 0;">
            <h4>{{ item.name }}</h4>
            <p style="color:#909399;font-size:13px;margin:8px 0;">{{ item.description?.substring(0,40) }}...</p>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span style="color:#F56C6C;font-size:18px;font-weight:600;">¥{{ item.price }}</span>
              <el-button type="primary" size="small" @click="openBooking(item)">立即预约</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog v-model="bookingVisible" title="预约服务" width="450px">
      <el-form :model="bookingForm" label-width="80px">
        <el-form-item label="服务"><el-input :value="bookingForm.serviceName" disabled /></el-form-item>
        <el-form-item label="价格"><el-input :value="'¥'+bookingForm.price" disabled /></el-form-item>
        <el-form-item label="预约时间"><el-date-picker v-model="bookingForm.appointmentTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" :disabled-date="(d) => d.getTime() < Date.now() - 86400000" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="bookingForm.note" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="bookingVisible=false">取消</el-button><el-button type="primary" @click="submitBooking">确认预约</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { serviceCategoryList, serviceItemList, appointmentAdd } from '../../api'
import { ElMessage } from 'element-plus'
const categories = ref([]); const services = ref([]); const allServices = ref([]); const activeCat = ref(null)
const bookingVisible = ref(false); const bookingForm = ref({})

const filterByCat = (catId) => {
  activeCat.value = activeCat.value === catId ? null : catId
  services.value = activeCat.value ? allServices.value.filter(s => s.categoryId === activeCat.value) : allServices.value
}
const openBooking = (item) => { bookingForm.value = { serviceItemId: item.id, serviceName: item.name, price: item.price, appointmentTime: '', note: '' }; bookingVisible.value = true }
const submitBooking = async () => {
  if (!bookingForm.value.appointmentTime) return ElMessage.warning('请选择预约时间')
  if (new Date(bookingForm.value.appointmentTime).getTime() < Date.now()) return ElMessage.warning('预约时间不能是过去的时间')
  await appointmentAdd(bookingForm.value); ElMessage.success('预约成功'); bookingVisible.value = false
}
onMounted(async () => {
  const r1 = await serviceCategoryList(); categories.value = r1.data
  const r2 = await serviceItemList({}); allServices.value = r2.data; services.value = r2.data
})
</script>
