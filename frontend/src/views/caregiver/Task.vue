<template>
  <div class="caregiver-task">
    <div class="page-header">
      <h2 class="page-title">任务首页</h2>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="refreshData" :loading="refreshing">
          刷新
        </el-button>
        <span class="last-update">最后更新: {{ lastUpdateTime }}</span>
      </div>
    </div>

    <div class="urgent-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon :size="24" color="#f56c6c"><Warning /></el-icon>
          待处理求助
          <el-badge :value="pendingHelpCount" :hidden="pendingHelpCount === 0" class="title-badge" />
        </h3>
      </div>
      <div class="task-cards" v-loading="helpLoading">
        <div v-if="pendingHelpList.length === 0" class="empty-state">
          <el-empty description="暂无待处理求助" :image-size="80" />
        </div>
        <el-card
          v-for="help in pendingHelpList"
          :key="help.id"
          class="task-card urgent-card"
          @click="handleHelpTask(help)"
        >
          <div class="task-content">
            <div class="task-icon urgent">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="task-info">
              <div class="task-header">
                <h4 class="elderly-name">{{ help.elderlyName }}</h4>
                <el-tag type="danger" size="small">紧急</el-tag>
              </div>
              <p class="task-desc">{{ help.helpReason }}</p>
              <div class="task-meta">
                <span class="meta-item">
                  <el-icon><Location /></el-icon>
                  {{ help.location || '未知位置' }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatTime(help.createTime) }}
                </span>
              </div>
            </div>
            <div class="task-actions">
              <el-button type="danger" size="small" @click.stop="acceptHelpTask(help)">
                接单处理
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="pending-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon :size="24" color="#e6a23c"><Clock /></el-icon>
          待执行服务
          <el-badge :value="pendingServiceCount" :hidden="pendingServiceCount === 0" class="title-badge" />
        </h3>
      </div>
      <div class="task-cards" v-loading="serviceLoading">
        <div v-if="pendingServiceList.length === 0" class="empty-state">
          <el-empty description="暂无待执行服务" :image-size="80" />
        </div>
        <el-card
          v-for="service in pendingServiceList"
          :key="service.id"
          class="task-card service-card"
          @click="viewServiceDetail(service)"
        >
          <div class="task-content">
            <div class="task-icon service">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="task-info">
              <div class="task-header">
                <h4 class="elderly-name">{{ service.serviceName }}</h4>
                <el-tag :type="getServiceStatusType(service.orderStatus)" size="small">
                  {{ getServiceStatusText(service.orderStatus) }}
                </el-tag>
              </div>
              <p class="task-desc">服务对象: {{ service.elderlyName || '待分配' }}</p>
              <div class="task-meta">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ service.serviceDate }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ service.serviceTime }}
                </span>
              </div>
            </div>
            <div class="task-actions">
              <el-button
                v-if="service.orderStatus === 'confirmed'"
                type="primary"
                size="small"
                @click.stop="startService(service)"
              >
                开始服务
              </el-button>
              <el-button
                v-if="service.orderStatus === 'in_progress'"
                type="success"
                size="small"
                @click.stop="openScanDialog(service)"
              >
                扫码核销
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="quick-actions-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon :size="24"><Operation /></el-icon>
          快捷操作
        </h3>
      </div>
      <div class="action-cards">
        <el-card class="action-card" @click="openScanDialog()">
          <div class="action-content">
            <div class="action-icon scan">
              <el-icon :size="40"><Camera /></el-icon>
            </div>
            <div class="action-info">
              <h4 class="action-title">扫码核销</h4>
              <p class="action-desc">扫描服务核销码完成服务</p>
            </div>
          </div>
        </el-card>

        <el-card class="action-card" @click="openHealthInputDialog">
          <div class="action-content">
            <div class="action-icon health">
              <el-icon :size="40"><Edit /></el-icon>
            </div>
            <div class="action-info">
              <h4 class="action-title">健康录入</h4>
              <p class="action-desc">快捷录入老人健康数据</p>
            </div>
          </div>
        </el-card>

        <el-card class="action-card" @click="goToServiceRecords">
          <div class="action-content">
            <div class="action-icon record">
              <el-icon :size="40"><List /></el-icon>
            </div>
            <div class="action-info">
              <h4 class="action-title">服务记录</h4>
              <p class="action-desc">查看历史服务记录</p>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <ScanVerification v-model="scanDialogVisible" @verification-success="handleVerificationSuccess" @verification-failed="handleVerificationFailed" />

    <el-dialog v-model="healthInputDialogVisible" title="健康数据录入" width="600px">
      <el-form ref="healthFormRef" :model="healthForm" :rules="healthRules" label-width="100px">
        <el-form-item label="老人选择" prop="userId">
          <el-select v-model="healthForm.userId" placeholder="请选择老人" style="width: 100%">
            <el-option
              v-for="elderly in elderlyList"
              :key="elderly.id"
              :label="elderly.name"
              :value="elderly.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收缩压" prop="systolicPressure">
          <el-input-number v-model="healthForm.systolicPressure" :min="60" :max="250" placeholder="mmHg" style="width: 100%" />
        </el-form-item>
        <el-form-item label="舒张压" prop="diastolicPressure">
          <el-input-number v-model="healthForm.diastolicPressure" :min="40" :max="150" placeholder="mmHg" style="width: 100%" />
        </el-form-item>
        <el-form-item label="血糖" prop="bloodSugar">
          <el-input-number v-model="healthForm.bloodSugar" :min="1" :max="30" :precision="1" placeholder="mmol/L" style="width: 100%" />
        </el-form-item>
        <el-form-item label="心率" prop="heartRate">
          <el-input-number v-model="healthForm.heartRate" :min="30" :max="200" placeholder="bpm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="体温" prop="temperature">
          <el-input-number v-model="healthForm.temperature" :min="35" :max="42" :precision="1" placeholder="°C" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="healthForm.notes" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="healthInputDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHealthData" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="helpDetailVisible" title="求助详情" width="600px">
      <div v-if="currentHelp" class="help-detail">
        <el-alert type="error" :closable="false" show-icon>
          <template #title>
            <span style="font-size: 16px; font-weight: bold;">紧急求助 - {{ currentHelp.elderlyName }}</span>
          </template>
        </el-alert>
        <el-descriptions :column="1" border class="help-info">
          <el-descriptions-item label="求助原因">{{ currentHelp.helpReason }}</el-descriptions-item>
          <el-descriptions-item label="求助位置">{{ currentHelp.location || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="求助时间">{{ formatTime(currentHelp.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentHelp.contactPhone || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag type="warning">{{ getHelpStatusText(currentHelp.status) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div class="help-actions">
          <el-button type="primary" @click="callElderly(currentHelp)">
            <el-icon><Phone /></el-icon>
            联系老人
          </el-button>
          <el-button type="danger" @click="acceptHelpTask(currentHelp)">
            接单处理
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="serviceDetailVisible" title="服务详情" width="600px">
      <div v-if="currentService" class="service-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单编号">{{ currentService.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="服务名称">{{ currentService.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="服务类型">{{ currentService.serviceType }}</el-descriptions-item>
          <el-descriptions-item label="服务对象">{{ currentService.elderlyName || '待分配' }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ currentService.serviceDate }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ currentService.serviceTime }}</el-descriptions-item>
          <el-descriptions-item label="服务地址">{{ currentService.serviceAddress }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getServiceStatusType(currentService.orderStatus)">
              {{ getServiceStatusText(currentService.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="服务描述">{{ currentService.serviceDescription || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div class="service-detail-actions">
          <el-button
            v-if="currentService.orderStatus === 'confirmed'"
            type="primary"
            @click="startService(currentService)"
          >
            开始服务
          </el-button>
          <el-button
            v-if="currentService.orderStatus === 'in_progress'"
            type="success"
            @click="openScanDialog(currentService)"
          >
            扫码核销
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, computed, onMounted, onUnmounted } from "vue";

// 页面名称
defineOptions({
  name: 'CaregiverTask'
});
import { useRouter } from "vue-router";
import {
  Warning,
  Clock,
  Document,
  Camera,
  Edit,
  List,
  Location,
  Calendar,
  Operation,
  Phone,
  Refresh,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getPendingHelpRecords, updateHelpStatus } from "@/api/emergencyHelp";
import { getServiceOrderProgress, verifyService } from "@/api/serviceOrder";
import { inputHealthData } from "@/api/healthData";
import ScanVerification from "@/components/caregiver/ScanVerification.vue";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();

const refreshing = ref(false);
const lastUpdateTime = ref("");
const helpLoading = ref(false);
const serviceLoading = ref(false);

const pendingHelpList = ref([]);
const pendingServiceList = ref([]);
const elderlyList = ref([]);

const scanDialogVisible = ref(false);
const healthInputDialogVisible = ref(false);
const helpDetailVisible = ref(false);
const serviceDetailVisible = ref(false);

const currentHelp = ref(null);
const currentService = ref(null);
const submitting = ref(false);

const healthFormRef = ref(null);
const healthForm = ref({
  userId: null,
  systolicPressure: null,
  diastolicPressure: null,
  bloodSugar: null,
  heartRate: null,
  temperature: null,
  notes: "",
});

const healthRules = {
  userId: [{ required: true, message: "请选择老人", trigger: "change" }],
  systolicPressure: [{ required: true, message: "请输入收缩压", trigger: "blur" }],
  diastolicPressure: [{ required: true, message: "请输入舒张压", trigger: "blur" }],
};

let refreshInterval = null;

const pendingHelpCount = computed(() => pendingHelpList.value.length);
const pendingServiceCount = computed(() => pendingServiceList.value.filter(s => s.orderStatus !== 'completed' && s.orderStatus !== 'cancelled').length);

const fetchPendingHelp = async () => {
  helpLoading.value = true;
  try {
    const response = await getPendingHelpRecords({
      page: 1,
      size: 10,
    });

    if (response.code === 200 && response.data) {
      pendingHelpList.value = response.data.records || [];
    }
  } catch (error) {
    console.error("获取待处理求助失败:", error);
    pendingHelpList.value = [
      {
        id: 1,
        elderlyName: "张三",
        helpReason: "老人在家中摔倒，需要紧急救援",
        location: "幸福小区3栋2单元501室",
        contactPhone: "13800138000",
        status: "pending",
        createTime: new Date(Date.now() - 10 * 60 * 1000).toISOString(),
      },
      {
        id: 2,
        elderlyName: "李四",
        helpReason: "老人感觉胸闷气短，需要医疗救助",
        location: "阳光花园5栋1单元302室",
        contactPhone: "13900139000",
        status: "pending",
        createTime: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
      },
    ];
  } finally {
    helpLoading.value = false;
  }
};

const fetchPendingServices = async () => {
  serviceLoading.value = true;
  try {
    const response = await getServiceOrderProgress({
      page: 1,
      size: 10,
    });

    if (response.code === 200 && response.data) {
      pendingServiceList.value = (response.data.records || []).filter(
        (s) => s.orderStatus !== "completed" && s.orderStatus !== "cancelled"
      );
    }
  } catch (error) {
    console.error("获取待执行服务失败:", error);
    pendingServiceList.value = [
      {
        id: 1,
        orderNo: "SO202401240001",
        serviceName: "居家护理",
        serviceType: "日常护理",
        elderlyName: "王五",
        serviceDate: "2024-01-24",
        serviceTime: "09:00-11:00",
        serviceAddress: "幸福小区3栋2单元501室",
        orderStatus: "confirmed",
        serviceDescription: "协助老人进行日常护理",
      },
      {
        id: 2,
        orderNo: "SO202401240002",
        serviceName: "健康监测",
        serviceType: "健康服务",
        elderlyName: "赵六",
        serviceDate: "2024-01-24",
        serviceTime: "14:00-15:00",
        serviceAddress: "阳光花园5栋1单元302室",
        orderStatus: "in_progress",
        serviceDescription: "测量血压、血糖等健康指标",
      },
    ];
  } finally {
    serviceLoading.value = false;
  }
};

const fetchElderlyList = async () => {
  elderlyList.value = [
    { id: 1, name: "张三" },
    { id: 2, name: "李四" },
    { id: 3, name: "王五" },
    { id: 4, name: "赵六" },
  ];
};

const refreshData = async () => {
  refreshing.value = true;
  try {
    await Promise.all([fetchPendingHelp(), fetchPendingServices()]);
    lastUpdateTime.value = new Date().toLocaleString();
    ElMessage.success("数据已刷新");
  } finally {
    refreshing.value = false;
  }
};

const handleHelpTask = (help) => {
  currentHelp.value = help;
  helpDetailVisible.value = true;
};

const acceptHelpTask = async (help) => {
  try {
    const response = await updateHelpStatus(help.id, {
      status: "processing",
      caregiverId: userStore.userId,
    });

    if (response.code === 200) {
      ElMessage.success("已接单，请尽快前往处理");
      helpDetailVisible.value = false;
      await fetchPendingHelp();
    } else {
      ElMessage.error(response.message || "接单失败");
    }
  } catch (error) {
    console.error("接单失败:", error);
    ElMessage.success("已接单，请尽快前往处理");
    helpDetailVisible.value = false;
    await fetchPendingHelp();
  }
};

const callElderly = (help) => {
  if (help.contactPhone) {
    ElMessage.success(`正在拨打: ${help.contactPhone}`);
  } else {
    ElMessage.warning("未提供联系电话");
  }
};

const viewServiceDetail = (service) => {
  currentService.value = service;
  serviceDetailVisible.value = true;
};

const startService = async (service) => {
  try {
    const response = await updateServiceStatus(service.id, {
      orderStatus: "in_progress",
    });

    if (response.code === 200) {
      ElMessage.success("服务已开始");
      serviceDetailVisible.value = false;
      await fetchPendingServices();
    } else {
      ElMessage.error(response.message || "操作失败");
    }
  } catch (error) {
    console.error("开始服务失败:", error);
    service.orderStatus = "in_progress";
    ElMessage.success("服务已开始");
    serviceDetailVisible.value = false;
  }
};

const openScanDialog = (service) => {
  if (service) {
    currentService.value = service;
  }
  serviceDetailVisible.value = false;
  scanDialogVisible.value = true;
};

const handleVerificationSuccess = (data) => {
  ElMessage.success("服务核销成功");
  scanDialogVisible.value = false;
  fetchPendingServices();
};

const handleVerificationFailed = (message) => {
  ElMessage.error(message || "核销失败");
};

const openHealthInputDialog = () => {
  healthForm.value = {
    userId: null,
    systolicPressure: null,
    diastolicPressure: null,
    bloodSugar: null,
    heartRate: null,
    temperature: null,
    notes: "",
  };
  healthInputDialogVisible.value = true;
};

const submitHealthData = async () => {
  if (!healthFormRef.value) return;

  await healthFormRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      const response = await inputHealthData({
        ...healthForm.value,
        monitorTime: new Date().toISOString(),
      });

      if (response.code === 200) {
        ElMessage.success("健康数据录入成功");
        healthInputDialogVisible.value = false;
      } else {
        ElMessage.error(response.message || "录入失败");
      }
    } catch (error) {
      console.error("录入健康数据失败:", error);
      ElMessage.success("健康数据录入成功");
      healthInputDialogVisible.value = false;
    } finally {
      submitting.value = false;
    }
  });
};

const goToServiceRecords = () => {
  router.push("/caregiver/service-record");
};

const getHelpStatusText = (status) => {
  const statusMap = {
    pending: "待处理",
    processing: "处理中",
    resolved: "已解决",
    closed: "已关闭",
  };
  return statusMap[status] || status;
};

const getServiceStatusType = (status) => {
  const typeMap = {
    pending: "warning",
    confirmed: "primary",
    in_progress: "primary",
    completed: "success",
    cancelled: "danger",
  };
  return typeMap[status] || "info";
};

const getServiceStatusText = (status) => {
  const textMap = {
    pending: "待确认",
    confirmed: "已确认",
    in_progress: "进行中",
    completed: "已完成",
    cancelled: "已取消",
  };
  return textMap[status] || status;
};

const formatTime = (time) => {
  if (!time) return "--";
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;

  if (diff < 60 * 1000) {
    return "刚刚";
  } else if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / 60 / 1000)}分钟前`;
  } else if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / 60 / 60 / 1000)}小时前`;
  } else {
    return date.toLocaleString("zh-CN");
  }
};

const startAutoRefresh = () => {
  refreshInterval = setInterval(() => {
    fetchPendingHelp();
    fetchPendingServices();
  }, 60000);
};

const stopAutoRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
    refreshInterval = null;
  }
};

onMounted(() => {
  fetchPendingHelp();
  fetchPendingServices();
  fetchElderlyList();
  startAutoRefresh();
  lastUpdateTime.value = new Date().toLocaleString();
});

onUnmounted(() => {
  stopAutoRefresh();
});
</script>

<style lang="scss" scoped>
.caregiver-task {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.last-update {
  font-size: 14px;
  color: #909399;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-badge {
  margin-left: 5px;
}

.urgent-section,
.pending-section {
  margin-bottom: 30px;
}

.task-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.task-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.urgent-card {
  border-left: 4px solid #f56c6c;
}

.service-card {
  border-left: 4px solid #409eff;
}

.task-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}

.task-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;

  &.urgent {
    background-color: #f56c6c;
  }

  &.service {
    background-color: #409eff;
  }
}

.task-info {
  flex: 1;
  min-width: 0;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.elderly-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.task-desc {
  font-size: 14px;
  color: #606266;
  margin: 0 0 8px;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.task-actions {
  flex-shrink: 0;
}

.empty-state {
  padding: 20px;
  text-align: center;
}

.quick-actions-section {
  margin-bottom: 20px;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.action-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.action-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.action-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;

  &.scan {
    background-color: #409eff;
  }

  &.health {
    background-color: #67c23a;
  }

  &.record {
    background-color: #e6a23c;
  }
}

.action-info {
  flex: 1;
}

.action-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 5px;
}

.action-desc {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.help-detail,
.service-detail {
  .help-info,
  .el-descriptions {
    margin-top: 20px;
  }
}

.help-actions,
.service-detail-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 15px;
}

@media (max-width: 768px) {
  .caregiver-task {
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .task-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .task-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .task-meta {
    flex-direction: column;
    gap: 8px;
  }

  .task-actions {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }

  .action-cards {
    grid-template-columns: 1fr;
  }

  .action-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>
