<template>
  <div class="elderly-home">
    <!-- 顶部板块 -->
    <div class="top-section">
      <h1 class="welcome-text">您好，{{ userStore.realName || "老年朋友" }}</h1>
      <div class="top-buttons">
        <el-button class="voice-assistant-btn" @click="showVoiceAssistant">
          <el-icon :size="24"><Mic /></el-icon>
          <span>点击说话，唤醒服务</span>
        </el-button>
        <el-button class="emergency-sos-btn" @click="goToEmergency">
          <el-icon :size="28"><WarningFilled /></el-icon>
          <span>发起求助</span>
        </el-button>
      </div>
    </div>

    <!-- 核心功能区 -->
    <div class="core-functions">
      <div class="function-card" @click="goToHealthReport">
        <div class="function-icon health-icon">
          <el-icon :size="48"><Monitor /></el-icon>
        </div>
        <h3 class="function-title">健康概览</h3>
      </div>

      <div class="function-card emergency-card" @click="showEmergencyConfirm">
        <div class="function-icon emergency-icon">
          <el-icon :size="48"><Warning /></el-icon>
        </div>
        <h3 class="function-title">紧急求助</h3>
        <p class="emergency-hint">点击卡片触发紧急求助</p>
      </div>

      <div class="function-card" @click="goToServiceBooking">
        <div class="function-icon service-icon">
          <el-icon :size="48"><Service /></el-icon>
        </div>
        <h3 class="function-title">我的服务</h3>
      </div>
    </div>

    <!-- 底部板块 -->
    <div class="bottom-section">
      <!-- 社区公告 -->
      <el-card class="announcement-card">
        <template #header>
          <div class="card-header">
            <h3 class="card-title">社区公告</h3>
            <el-button
              size="large"
              class="refresh-btn"
              @click="refreshAnnouncements"
            >
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>
        <div class="announcement-list">
          <div
            v-for="announcement in announcements"
            :key="announcement.id"
            class="announcement-item"
          >
            <div class="announcement-content">{{ announcement.content }}</div>
            <div class="announcement-time">{{ formatTime(announcement.publishTime) }}</div>
          </div>
        </div>
      </el-card>

      <!-- 联系家属 -->
      <div class="contact-family">
        <h3 class="section-title">联系家属</h3>
        <div class="family-list">
          <div
            v-for="family in familyMembers"
            :key="family.id"
            class="family-item"
            @click="callFamily(family)"
          >
            <div class="family-avatar">{{ family.familyName ? family.familyName.charAt(0) : '家' }}</div>
            <div class="family-info">
              <div class="family-name">{{ family.familyName }}</div>
              <div class="family-relation">{{ family.relationName }}</div>
            </div>
            <el-button type="primary" size="large" class="call-btn">
              <el-icon><Phone /></el-icon>
              呼叫
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 语音助手弹窗 -->
    <el-dialog
      v-model="voiceAssistantVisible"
      title="语音助手"
      width="80%"
      center
    >
      <VoiceAssistantDialog
        @close="voiceAssistantVisible = false"
        @navigate="handleVoiceNavigate"
      />
    </el-dialog>

    <!-- 紧急求助确认弹窗 -->
    <el-dialog
      v-model="emergencyConfirmVisible"
      title="紧急求助确认"
      width="500px"
      center
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      class="emergency-confirm-dialog"
    >
      <div class="emergency-confirm-content">
        <div class="emergency-icon-wrapper">
          <el-icon :size="80" class="emergency-icon"><WarningFilled /></el-icon>
        </div>
        <h3 class="emergency-title">确认发起紧急求助？</h3>
        <div class="countdown-wrapper">
          <div class="countdown-number">{{ countdown }}</div>
          <div class="countdown-text">秒后自动求助</div>
        </div>
        <p class="emergency-warning">系统将自动联系护工和家属，请确认您确实需要紧急帮助</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="cancelEmergency" class="cancel-btn">
            取消求助
          </el-button>
          <el-button size="large" type="danger" @click="confirmEmergency" class="confirm-btn">
            立即求助
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, onMounted } from "vue";

// 页面名称
defineOptions({
  name: 'ElderlyHome'
});
import { useUserStore } from "@/store/user";
import { useRouter } from "vue-router";
import {
  Mic,
  Monitor,
  Warning,
  WarningFilled,
  Service,
  Refresh,
  Phone,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import VoiceAssistantDialog from "@/components/elderly/VoiceAssistantDialog.vue";
import { getAnnouncements, getFamilyMembers, getHealthOverview } from "@/api/elderly/home";
import { createEmergency } from "@/api/elderly/emergency";

const userStore = useUserStore();
const router = useRouter();

const voiceAssistantVisible = ref(false);
const emergencyConfirmVisible = ref(false);
const countdown = ref(10);
let countdownTimer = null;
const loading = ref(false);

const announcements = ref([]);
const familyMembers = ref([]);
const healthOverview = ref({
  bloodPressure: "-",
  heartRate: "-",
  bloodSugar: "-",
  temperature: "-",
  weight: "-",
  monitorTime: "-"
});

onMounted(() => {
  initData();
});

const initData = async () => {
  loading.value = true;
  try {
    await Promise.all([
      fetchAnnouncements(),
      fetchFamilyMembers(),
      fetchHealthOverview()
    ]);
  } catch (error) {
    console.error("初始化老人首页数据失败", error);
    ElMessage.error("加载数据失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};

const fetchAnnouncements = async () => {
  try {
    const res = await getAnnouncements();
    announcements.value = res.data || [];
  } catch (error) {
    console.error("获取公告失败", error);
  }
};

const fetchFamilyMembers = async () => {
  try {
    const res = await getFamilyMembers();
    familyMembers.value = res.data || [];
  } catch (error) {
    console.error("获取家属信息失败", error);
  }
};

const fetchHealthOverview = async () => {
  try {
    const res = await getHealthOverview();
    healthOverview.value = res.data || {};
  } catch (error) {
    console.error("获取健康概览失败", error);
  }
};

const showVoiceAssistant = () => {
  voiceAssistantVisible.value = true;
};

const handleVoiceResult = (text) => {
  console.log("语音识别结果:", text);

  if (text.includes("健康") || text.includes("报告")) {
    goToHealthReport();
    voiceAssistantVisible.value = false;
  } else if (text.includes("服务") || text.includes("预约")) {
    goToServiceBooking();
    voiceAssistantVisible.value = false;
  } else if (text.includes("家属") || text.includes("联系")) {
    ElMessage.success("请在下方选择要联系的家属");
  }
};

const handleEmergencyVoice = (text) => {
  console.log("检测到紧急求助:", text);
  voiceAssistantVisible.value = false;
  goToEmergency();
};

const handleVoiceStart = () => {
  console.log("语音识别已启动");
};

const handleVoiceEnd = () => {
  console.log("语音识别已停止");
};

const handleVoiceNavigate = (path) => {
  router.push(path);
};

const goToHealthReport = () => {
  router.push("/elderly/health-report");
};

const goToEmergency = () => {
  router.push("/elderly/emergency");
};

const goToServiceBooking = () => {
  router.push("/elderly/service-booking");
};

const callFamily = (family) => {
  ElMessage.success(`正在拨打 ${family.name} 的电话: ${family.phone}`);
};

const refreshAnnouncements = async () => {
  await fetchAnnouncements();
  ElMessage.success("公告已刷新");
};

const showEmergencyConfirm = () => {
  emergencyConfirmVisible.value = true;
  countdown.value = 10;
  
  countdownTimer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(countdownTimer);
      confirmEmergency();
    }
  }, 1000);
};

const cancelEmergency = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
  emergencyConfirmVisible.value = false;
  countdown.value = 10;
  ElMessage.info("已取消紧急求助");
};

const confirmEmergency = async () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
  
  try {
    await createEmergency({
      type: "security",
      description: "紧急求助 - 首页一键求助"
    });
    ElMessage.success("紧急求助已发送，护工和家属将尽快联系您");
    emergencyConfirmVisible.value = false;
    countdown.value = 10;
  } catch (error) {
    console.error("发送紧急求助失败", error);
    ElMessage.error("发送紧急求助失败，请稍后重试或直接拨打120");
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};
</script>

<style lang="scss" scoped>
.elderly-home {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.top-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.top-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.voice-assistant-btn {
  padding: 15px 30px;
  font-size: 18px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.voice-assistant-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.emergency-sos-btn {
  padding: 15px 30px;
  font-size: 18px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
  animation: pulse 2s infinite;
}

.emergency-sos-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 87, 108, 0.4);
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
  }
  50% {
    box-shadow: 0 4px 20px rgba(245, 87, 108, 0.6);
  }
}

.welcome-text {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 30px;
  line-height: 1.2;
}

.voice-assistant-btn {
  font-size: 18px;
  padding: 15px 30px;
  background-color: #409eff;
  border: none;
  border-radius: 50px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
  height: 56px;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  }
}

.core-functions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.function-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px 15px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }
}

.emergency-card {
  border: 3px solid #f56c6c;
  background: linear-gradient(135deg, #ffffff 0%, #fef0f0 100%);
}

.emergency-hint {
  font-size: 14px;
  color: #f56c6c;
  margin: 8px 0 0 0;
  font-weight: 500;
}

.function-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.health-icon {
  background-color: #67c23a;
}

.emergency-icon {
  background-color: #f56c6c;
  animation: pulse 2s infinite;
}

.service-icon {
  background-color: #e6a23c;
}

.function-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.announcement-card {
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.refresh-btn {
  height: 48px;
  font-size: 16px;
  padding: 12px 24px;
}

.announcement-list {
  max-height: 300px;
  overflow-y: auto;
}

.announcement-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f2f5;

  &:last-child {
    border-bottom: none;
  }
}

.announcement-content {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.4;
}

.announcement-time {
  font-size: 14px;
  color: #909399;
}

.contact-family {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.family-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.family-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    background-color: #ecf5ff;
    transform: translateX(10px);
  }
}

.family-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #409eff;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-right: 20px;
}

.family-info {
  flex: 1;
}

.family-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.family-relation {
  font-size: 16px;
  color: #606266;
}

.call-btn {
  height: 48px;
  font-size: 16px;
  padding: 12px 24px;
}

.voice-commands {
  text-align: left;
  margin-top: 20px;
}

.voice-commands h4 {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.voice-commands ul {
  padding-left: 20px;
}

.voice-commands li {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.5;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .elderly-home {
    padding: 20px;
  }

  .welcome-text {
    font-size: 28px;
  }

  .core-functions {
    grid-template-columns: 1fr;
  }

  .bottom-section {
    grid-template-columns: 1fr;
  }

  .function-card {
    padding: 30px 20px;
  }
}

.el-button {
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;

  &--primary {
    background-color: #409eff;
    border-color: #409eff;
    color: #ffffff;

    &:hover,
    &:focus {
      background-color: #66b1ff;
      border-color: #66b1ff;
    }
  }

  &--default {
    background-color: #ffffff;
    border-color: #dcdfe6;
    color: #303133;

    &:hover,
    &:focus {
      border-color: #409eff;
      color: #409eff;
    }
  }
}

:deep(.el-card__header) {
  padding: 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.emergency-confirm-dialog {
  .el-dialog__header {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 20px;
    border-radius: 8px 8px 0 0;
  }
  
  .el-dialog__title {
    color: white;
    font-size: 20px;
    font-weight: bold;
  }
}

.emergency-confirm-content {
  text-align: center;
  padding: 20px 0;
}

.emergency-icon-wrapper {
  margin-bottom: 20px;
}

.emergency-icon {
  color: #f5576c;
  animation: emergency-pulse 1s infinite;
}

@keyframes emergency-pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.emergency-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
  font-weight: bold;
}

.countdown-wrapper {
  margin: 30px 0;
}

.countdown-number {
  font-size: 72px;
  font-weight: bold;
  color: #f5576c;
  line-height: 1;
  margin-bottom: 10px;
}

.countdown-text {
  font-size: 18px;
  color: #909399;
}

.emergency-warning {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin-top: 20px;
  padding: 15px;
  background-color: #fef0f0;
  border-radius: 8px;
  border-left: 4px solid #f56c6c;
}

.dialog-footer {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.cancel-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
}

.confirm-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
}
</style>

