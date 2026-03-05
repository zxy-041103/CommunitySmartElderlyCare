<template>
  <div class="service-booking">
    <el-card class="booking-card">
      <template #header>
        <div class="card-header">
          <h2 class="page-title">服务预约</h2>
          <el-button size="large" class="refresh-btn" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <!-- 服务类型选择 -->
      <div class="service-type-section">
        <h3 class="section-title">选择服务类型</h3>
        <div class="service-type-grid">
          <div
            v-for="service in serviceTypes"
            :key="service.id"
            class="service-type-card"
            :class="{ active: selectedServiceId === service.id }"
            @click="selectService(service)"
          >
            <div
              class="service-icon"
              :style="{ backgroundColor: service.color }"
            >
              <el-icon :size="48"><component :is="service.icon" /></el-icon>
            </div>
            <h4 class="service-name">{{ service.name }}</h4>
            <p class="service-desc">{{ service.description }}</p>
            <div class="service-price">{{ service.price }}</div>
          </div>
        </div>
      </div>

      <!-- 预约表单 -->
      <div v-if="selectedService" class="booking-form-section">
        <h3 class="section-title">预约信息</h3>
        <el-form
          ref="bookingFormRef"
          :model="bookingForm"
          :rules="bookingRules"
          label-width="140px"
          class="elderly-form"
        >
          <el-form-item label="服务类型" prop="serviceType">
            <el-input v-model="bookingForm.serviceType" disabled size="large" />
          </el-form-item>
          <el-form-item label="服务名称" prop="serviceName">
            <el-input v-model="bookingForm.serviceName" disabled size="large" />
          </el-form-item>
          <el-form-item label="预约日期" prop="serviceDate">
            <el-date-picker
              v-model="bookingForm.serviceDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              size="large"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="预约时间" prop="serviceTime">
            <el-time-picker
              v-model="bookingForm.serviceTime"
              placeholder="选择时间"
              format="HH:mm"
              value-format="HH:mm"
              size="large"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="服务地址" prop="serviceAddress">
            <el-input
              v-model="bookingForm.serviceAddress"
              placeholder="请输入服务地址"
              size="large"
            />
          </el-form-item>
          <el-form-item label="服务描述" prop="serviceDescription">
            <el-input
              v-model="bookingForm.serviceDescription"
              type="textarea"
              :rows="3"
              placeholder="请描述您的服务需求"
              size="large"
            />
          </el-form-item>
        </el-form>
        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            class="form-action-btn primary"
            :loading="submitting"
            @click="handleBookingSubmit"
          >
            {{ submitting ? "提交中..." : "确认预约" }}
          </el-button>
          <el-button
            type="default"
            size="large"
            class="form-action-btn"
            @click="handleBookingReset"
          >
            重置
          </el-button>
        </div>
      </div>

      <!-- 预约记录 -->
      <div class="booking-record-section">
        <h3 class="section-title">预约记录</h3>
        <el-table
          v-loading="loading"
          :data="bookingRecords"
          stripe
          style="width: 100%"
          class="booking-table"
        >
          <el-table-column prop="serviceName" label="服务类型" width="180" />
          <el-table-column prop="serviceDate" label="预约日期" width="150" />
          <el-table-column prop="serviceTime" label="预约时间" width="120" />
          <el-table-column prop="orderStatus" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.orderStatus)" size="large">
                {{ getStatusLabel(scope.row.orderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="serviceDescription" label="服务描述" show-overflow-tooltip />
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="scope">
              <el-button
                v-if="scope.row.orderStatus === 'pending'"
                type="danger"
                size="large"
                @click="cancelBooking(scope.row.id)"
              >
                取消
              </el-button>
              <el-button
                v-else-if="scope.row.orderStatus === 'completed' && !scope.row.isEvaluated"
                type="primary"
                size="large"
                @click="reviewService(scope.row)"
              >
                评价
              </el-button>
              <el-button
                type="info"
                size="large"
                @click="viewDetails(scope.row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          :current-page="pagination.page"
          :page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          style="margin-top: 20px; justify-content: flex-end"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 服务评价弹窗 -->
    <el-dialog v-model="reviewDialogVisible" title="服务评价" width="600px">
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewRules"
        label-width="120px"
      >
        <el-form-item label="服务评分" prop="rating">
          <div class="rating-stars">
            <el-rate v-model="reviewForm.rating" :max="5" size="large" />
          </div>
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            placeholder="请输入您的评价"
            :rows="4"
            size="large"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="reviewDialogVisible = false"
            >取消</el-button
          >
          <el-button size="large" type="primary" :loading="reviewSubmitting" @click="submitReview"
            >提交评价</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 预约详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="600px">
      <div v-if="currentBooking" class="booking-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号" :span="2">{{ currentBooking.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="服务类型">{{ currentBooking.serviceType }}</el-descriptions-item>
          <el-descriptions-item label="服务名称">{{ currentBooking.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ currentBooking.serviceDate }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ currentBooking.serviceTime }}</el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag :type="getStatusType(currentBooking.orderStatus)" size="large">
              {{ getStatusLabel(currentBooking.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="currentBooking.isPaid ? 'success' : 'warning'" size="large">
              {{ currentBooking.isPaid ? '已支付' : '未支付' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="服务地址" :span="2">{{ currentBooking.serviceAddress || '无' }}</el-descriptions-item>
          <el-descriptions-item label="服务描述" :span="2">{{ currentBooking.serviceDescription || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ currentBooking.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="detailDialogVisible = false"
            >关闭</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, reactive, onMounted } from "vue";

// 页面名称
defineOptions({
  name: 'ElderlyServiceBooking'
});
import {
  House,
  User,
  BellFilled,
  Phone,
  Refresh,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { createServiceOrder, getServiceOrders, cancelServiceOrder } from "@/api/elderly/booking";
import { submitEvaluation } from "@/api/family/evaluate";

const serviceTypes = [
  {
    id: 1,
    name: "居家护理",
    description: "专业护理人员上门服务",
    price: "¥100/小时",
    color: "#409EFF",
    icon: "House",
  },
  {
    id: 2,
    name: "健康监测",
    description: "定期上门健康检查",
    price: "¥80/次",
    color: "#67C23A",
    icon: "User",
  },
  {
    id: 3,
    name: "接送服务",
    description: "老人出行接送服务",
    price: "¥50/次",
    color: "#E6A23C",
    icon: "BellFilled",
  },
  {
    id: 4,
    name: "紧急救援",
    description: "24小时紧急救援服务",
    price: "¥200/次",
    color: "#F56C6C",
    icon: "Phone",
  },
];

const selectedServiceId = ref(null);
const selectedService = ref(null);
const loading = ref(false);
const submitting = ref(false);
const reviewSubmitting = ref(false);

const bookingFormRef = ref(null);
const bookingForm = reactive({
  serviceType: "",
  serviceName: "",
  serviceDate: "",
  serviceTime: "",
  serviceAddress: "",
  serviceDescription: "",
});

const bookingRules = {
  serviceDate: [{ required: true, message: "请选择预约日期", trigger: "change" }],
  serviceTime: [{ required: true, message: "请选择预约时间", trigger: "change" }],
  serviceAddress: [{ required: true, message: "请输入服务地址", trigger: "blur" }],
};

const bookingRecords = ref([]);
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const reviewDialogVisible = ref(false);
const reviewFormRef = ref(null);
const reviewForm = reactive({
  orderId: null,
  rating: 5,
  content: "",
});

const reviewRules = {
  rating: [{ required: true, message: "请选择评分", trigger: "change" }],
  content: [{ required: true, message: "请输入评价内容", trigger: "blur" }],
};

const detailDialogVisible = ref(false);
const currentBooking = ref(null);

onMounted(() => {
  fetchBookingRecords();
});

const fetchBookingRecords = async () => {
  loading.value = true;
  try {
    const res = await getServiceOrders({
      page: pagination.page,
      size: pagination.size,
    });
    if (res.data) {
      bookingRecords.value = res.data.records || [];
      pagination.total = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取预约记录失败:", error);
    ElMessage.error("获取预约记录失败");
  } finally {
    loading.value = false;
  }
};

const selectService = (service) => {
  selectedServiceId.value = service.id;
  selectedService.value = service;
  bookingForm.serviceType = service.name;
  bookingForm.serviceName = service.name;
};

const refreshData = () => {
  fetchBookingRecords();
  ElMessage.success("数据已刷新");
};

const handleBookingSubmit = async () => {
  if (!bookingFormRef.value) return;

  try {
    await bookingFormRef.value.validate();
    submitting.value = true;

    const res = await createServiceOrder({
      serviceType: bookingForm.serviceType,
      serviceName: bookingForm.serviceName,
      serviceDate: bookingForm.serviceDate,
      serviceTime: bookingForm.serviceTime,
      serviceAddress: bookingForm.serviceAddress,
      serviceDescription: bookingForm.serviceDescription,
    });

    if (res.data) {
      ElMessage.success("预约提交成功，请等待确认");
      handleBookingReset();
      fetchBookingRecords();
    }
  } catch (error) {
    console.error("预约失败:", error);
    ElMessage.error(error.response?.data?.message || "预约失败");
  } finally {
    submitting.value = false;
  }
};

const handleBookingReset = () => {
  selectedServiceId.value = null;
  selectedService.value = null;
  if (bookingFormRef.value) {
    bookingFormRef.value.resetFields();
  }
};

const cancelBooking = (id) => {
  ElMessageBox.confirm("确定要取消该预约吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelServiceOrder(id);
        ElMessage.success("预约已取消");
        fetchBookingRecords();
      } catch (error) {
        console.error("取消预约失败:", error);
        ElMessage.error("取消预约失败");
      }
    })
    .catch(() => {});
};

const reviewService = (booking) => {
  reviewForm.orderId = booking.id;
  reviewForm.rating = 5;
  reviewForm.content = "";
  reviewDialogVisible.value = true;
};

const submitReview = async () => {
  if (!reviewFormRef.value) return;

  try {
    await reviewFormRef.value.validate();
    reviewSubmitting.value = true;

    await submitEvaluation({
      orderId: reviewForm.orderId,
      rating: reviewForm.rating,
      content: reviewForm.content,
    });

    ElMessage.success("评价提交成功");
    reviewDialogVisible.value = false;
    fetchBookingRecords();
  } catch (error) {
    console.error("评价失败:", error);
    ElMessage.error(error.response?.data?.message || "评价失败");
  } finally {
    reviewSubmitting.value = false;
  }
};

const viewDetails = (booking) => {
  currentBooking.value = booking;
  detailDialogVisible.value = true;
};

const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  fetchBookingRecords();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  fetchBookingRecords();
};

const getStatusType = (status) => {
  const statusMap = {
    pending: "warning",
    confirmed: "primary",
    in_progress: "warning",
    completed: "success",
    cancelled: "danger",
  };
  return statusMap[status] || "info";
};

const getStatusLabel = (status) => {
  const statusMap = {
    pending: "待确认",
    confirmed: "已确认",
    in_progress: "进行中",
    completed: "已完成",
    cancelled: "已取消",
  };
  return statusMap[status] || status;
};
</script>

<style lang="scss" scoped>
.service-booking {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.booking-card {
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.refresh-btn {
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 8px;
}

.service-type-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.service-type-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.service-type-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  &.active {
    border-color: #409eff;
    box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
  }
}

.service-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #ffffff;
}

.service-name {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
}

.service-desc {
  font-size: 14px;
  color: #909399;
  margin: 0 0 15px 0;
  line-height: 1.4;
}

.service-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.booking-form-section {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.elderly-form {
  max-width: 600px;
}

.form-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 30px;
}

.form-action-btn {
  min-width: 180px;
  height: 48px;
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 8px;

  &.primary {
    background-color: #409eff;
    border-color: #409eff;
    color: #ffffff;
  }
}

.booking-record-section {
  margin-top: 30px;
}

.booking-table {
  font-size: 16px;
}

.booking-details {
  padding: 10px;
}

.rating-stars {
  padding-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .service-booking {
    padding: 15px;
  }

  .page-title {
    font-size: 24px;
  }

  .service-type-grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-action-btn {
    width: 100%;
  }
}
</style>
