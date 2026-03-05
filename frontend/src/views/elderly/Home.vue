<template>
  <div class="elderly-home">
    <!-- 顶部板块 -->
    <div class="top-section">
      <h1 class="welcome-text">您好，{{ userStore.realName || "老年朋友" }}</h1>
      <el-button class="voice-assistant-btn" @click="showVoiceAssistant">
        <el-icon :size="24"><Mic /></el-icon>
        <span>点击说话，唤醒服务</span>
      </el-button>
    </div>

    <!-- 核心功能区 -->
    <div class="core-functions">
      <div class="function-card" @click="goToHealthReport">
        <div class="function-icon health-icon">
          <el-icon :size="48"><Monitor /></el-icon>
        </div>
        <h3 class="function-title">健康概览</h3>
      </div>

      <div class="function-card emergency-card" @click="goToEmergency">
        <div class="function-icon emergency-icon">
          <el-icon :size="48"><Warning /></el-icon>
        </div>
        <h3 class="function-title">紧急求助</h3>
        <p class="emergency-hint">说"呼叫护工"可快速求助</p>
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
            <div class="announcement-time">{{ announcement.time }}</div>
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
            <div class="family-avatar">{{ family.name.charAt(0) }}</div>
            <div class="family-info">
              <div class="family-name">{{ family.name }}</div>
              <div class="family-relation">{{ family.relation }}</div>
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
      <VoiceRecognition
        :auto-start="false"
        :continuous="false"
        emergency-keyword="呼叫护工"
        @result="handleVoiceResult"
        @emergency="handleEmergencyVoice"
        @start="handleVoiceStart"
        @end="handleVoiceEnd"
      />
      <div class="voice-commands">
        <h4>常用语音指令：</h4>
        <ul>
          <li>打开健康报告</li>
          <li>我要紧急求助</li>
          <li>预约服务</li>
          <li>联系家属</li>
        </ul>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="voiceAssistantVisible = false"
            >关闭</el-button
          >
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
  Service,
  Refresh,
  Phone,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import VoiceRecognition from "@/components/elderly/VoiceRecognition.vue";

const userStore = useUserStore();
const router = useRouter();

const voiceAssistantVisible = ref(false);

const announcements = ref([
  {
    id: 1,
    content: "本周六上午9点将在社区活动中心举办健康讲座，欢迎参加",
    time: "2024-01-20",
  },
  {
    id: 2,
    content: "社区养老服务中心新增康复理疗服务，有需要的居民可前往咨询",
    time: "2024-01-18",
  },
  { id: 3, content: "近期气温较低，请注意保暖，预防感冒", time: "2024-01-15" },
]);

const familyMembers = ref([
  { id: 1, name: "张三", relation: "儿子", phone: "13800138001" },
  { id: 2, name: "李四", relation: "女儿", phone: "13900139001" },
]);

onMounted(() => {
  initData();
});

const initData = () => {
  console.log("初始化老人首页数据");
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

const goToHealthReport = () => {
  router.push("/elderly/health-report");
};

const goToEmergency = () => {
  router.push("/emergency");
};

const goToServiceBooking = () => {
  router.push("/elderly/service-booking");
};

const callFamily = (family) => {
  ElMessage.success(`正在拨打 ${family.name} 的电话: ${family.phone}`);
};

const refreshAnnouncements = () => {
  ElMessage.success("公告已刷新");
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
</style>
