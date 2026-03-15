<template>
  <div class="voice-assistant-dialog">
    <!-- 语音识别组件 -->
    <div class="voice-section">
      <el-button
        :type="isListening ? 'danger' : 'primary'"
        size="large"
        class="voice-btn"
        :class="{ listening: isListening }"
        @click="toggleVoiceRecognition"
      >
        <el-icon :size="32">
          <Microphone v-if="!isListening" />
          <Loading v-else />
        </el-icon>
        <span class="voice-btn-text">{{ isListening ? '语音识别中...' : '点击开始语音识别' }}</span>
      </el-button>

      <div v-if="lastResult" class="voice-result">
        <p class="result-label">识别结果:</p>
        <p class="result-text">{{ lastResult }}</p>
      </div>

      <div v-if="!isSupported" class="voice-warning">
        <el-alert
          title="您的浏览器不支持语音识别功能"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
    </div>

    <!-- 功能指令说明 -->
    <div class="voice-commands">
      <h4 class="commands-title">常用语音指令：</h4>
      <div class="commands-grid">
        <div class="command-item">
          <el-tag type="primary" size="large">健康报告</el-tag>
          <span>"打开健康报告"、"查看健康数据"</span>
        </div>
        <div class="command-item">
          <el-tag type="danger" size="large">紧急求助</el-tag>
          <span>"紧急求助"、"救命"、"我不舒服"</span>
        </div>
        <div class="command-item">
          <el-tag type="success" size="large">预约服务</el-tag>
          <span>"预约服务"、"我要预约"、"家政预约"</span>
        </div>
        <div class="command-item">
          <el-tag type="warning" size="large">联系家属</el-tag>
          <span>"联系家属"、"给孩子打电话"、"和家人聊天"</span>
        </div>
      </div>
    </div>

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
        <h3 class="emergency-title">正在发起紧急求助</h3>
        <div class="countdown-wrapper">
          <div class="countdown-number">{{ countdown }}</div>
          <div class="countdown-text">秒后将联系急救中心和家属</div>
        </div>
        <p class="emergency-warning">点击取消可终止求助流程</p>
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

    <!-- 服务预约弹窗 -->
    <el-dialog
      v-model="bookingVisible"
      title="服务预约"
      width="700px"
      center
      class="booking-dialog"
    >
      <div class="booking-form-container">
        <!-- 步骤1：选择服务类型 -->
        <div v-if="bookingStep === 1" class="booking-step">
          <h3 class="step-title">请选择服务类型</h3>
          <div class="service-type-grid">
            <div
              v-for="service in serviceTypes"
              :key="service.id"
              class="service-type-card"
              :class="{ active: selectedService?.id === service.id }"
              @click="selectService(service)"
            >
              <div class="service-icon" :style="{ backgroundColor: service.color }">
                <el-icon :size="32"><component :is="service.icon" /></el-icon>
              </div>
              <h4 class="service-name">{{ service.name }}</h4>
              <p class="service-desc">{{ service.description }}</p>
            </div>
          </div>
        </div>

        <!-- 步骤2：填写预约信息 -->
        <div v-if="bookingStep === 2" class="booking-step">
          <h3 class="step-title">填写预约信息</h3>
          <el-form
            ref="bookingFormRef"
            :model="bookingForm"
            :rules="bookingRules"
            label-width="120px"
            class="elderly-form"
          >
            <el-form-item label="服务类型">
              <el-input v-model="bookingForm.serviceType" disabled size="large" />
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
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="bookingForm.contactPhone"
                placeholder="请输入联系电话"
                size="large"
              />
            </el-form-item>
            <el-form-item label="备注说明" prop="remark">
              <el-input
                v-model="bookingForm.remark"
                type="textarea"
                :rows="3"
                placeholder="请描述您的服务需求"
                size="large"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-if="bookingStep === 2" size="large" @click="bookingStep = 1">
            返回
          </el-button>
          <el-button size="large" @click="resetBookingForm">重置</el-button>
          <el-button
            v-if="bookingStep === 1"
            type="primary"
            size="large"
            @click="goToStep2"
            :disabled="!selectedService"
          >
            下一步
          </el-button>
          <el-button
            v-if="bookingStep === 2"
            type="primary"
            size="large"
            @click="submitBooking"
            :loading="submitting"
          >
            {{ submitting ? '提交中...' : '提交预约' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { Microphone, Loading, WarningFilled, House, User, Food, Scissor } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import voiceAssistant from "@/utils/voiceAssistant";
import { createServiceOrder } from "@/api/elderly/booking";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'navigate']);

// 语音识别状态
const isListening = ref(false);
const isSupported = ref(true);
const lastResult = ref("");

// 紧急求助状态
const emergencyConfirmVisible = ref(false);
const countdown = ref(10);
let countdownTimer = null;

// 服务预约状态
const bookingVisible = ref(false);
const bookingStep = ref(1);
const selectedService = ref(null);
const submitting = ref(false);
const bookingFormRef = ref(null);

const bookingForm = ref({
  serviceType: "",
  serviceName: "",
  serviceDate: "",
  serviceTime: "",
  serviceAddress: "",
  contactPhone: "",
  remark: ""
});

const bookingRules = {
  serviceDate: [{ required: true, message: "请选择预约日期", trigger: "change" }],
  serviceTime: [{ required: true, message: "请选择预约时间", trigger: "change" }],
  serviceAddress: [{ required: true, message: "请输入服务地址", trigger: "blur" }],
  contactPhone: [{ required: true, message: "请输入联系电话", trigger: "blur" }]
};

// 服务类型列表
const serviceTypes = [
  {
    id: 1,
    name: "家政服务",
    description: "清洁、洗衣、做饭等",
    icon: House,
    color: "#67c23a"
  },
  {
    id: 2,
    name: "医疗服务",
    description: "陪同就医、送药上门等",
    icon: User,
    color: "#f56c6c"
  },
  {
    id: 3,
    name: "送餐服务",
    description: "营养餐、特殊饮食配送",
    icon: Food,
    color: "#e6a23c"
  },
  {
    id: 4,
    name: "理发服务",
    description: "上门理发、修面等",
    icon: Scissor,
    color: "#409eff"
  }
];

onMounted(() => {
  isSupported.value = voiceAssistant.isSupported();

  // 设置语音助手回调
  voiceAssistant.on("result", handleVoiceResult);
  voiceAssistant.on("error", handleVoiceError);
  voiceAssistant.on("start", handleVoiceStart);
  voiceAssistant.on("end", handleVoiceEnd);
  voiceAssistant.on("emergency", handleEmergencyCommand);
  voiceAssistant.on("booking", handleBookingCommand);
  voiceAssistant.on("contactFamily", handleContactFamilyCommand);
});

onBeforeUnmount(() => {
  voiceAssistant.off("result");
  voiceAssistant.off("error");
  voiceAssistant.off("start");
  voiceAssistant.off("end");
  voiceAssistant.off("emergency");
  voiceAssistant.off("booking");
  voiceAssistant.off("contactFamily");
  voiceAssistant.destroy();

  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});

// 切换语音识别
const toggleVoiceRecognition = () => {
  if (isListening.value) {
    voiceAssistant.stop();
  } else {
    if (!voiceAssistant.isSupported()) {
      isSupported.value = false;
      ElMessage.error("您的浏览器不支持语音识别功能，请使用Chrome浏览器");
      return;
    }
    voiceAssistant.start();
  }
};

// 语音识别结果处理
const handleVoiceResult = (text) => {
  lastResult.value = text;
  console.log("语音识别结果:", text);

  // 检查健康报告指令
  if (text.includes("健康") || text.includes("报告")) {
    emit('navigate', '/elderly/health-report');
    emit('close');
    return;
  }
};

// 语音识别错误处理
const handleVoiceError = (error) => {
  isListening.value = false;
  console.error("语音识别错误:", error);
};

// 语音识别开始
const handleVoiceStart = () => {
  isListening.value = true;
  ElMessage.success("语音识别已启动，请说话");
};

// 语音识别结束
const handleVoiceEnd = () => {
  isListening.value = false;
  console.log("语音识别已停止");
};

// 紧急求助指令处理
const handleEmergencyCommand = (text) => {
  console.log("检测到紧急求助指令:", text);
  emit('close');
  showEmergencyConfirm();
};

// 显示紧急求助确认弹窗
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

// 取消紧急求助
const cancelEmergency = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
  emergencyConfirmVisible.value = false;
  countdown.value = 10;
  ElMessage.info("已取消紧急求助");
};

// 确认紧急求助
const confirmEmergency = async () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }

  try {
    // 调用紧急求助API
    const { createEmergency } = await import("@/api/elderly/emergency");
    await createEmergency({
      type: "security",
      description: "语音助手触发紧急求助"
    });
    ElMessage.success("紧急求助已发送，护工和家属将尽快联系您");
    emergencyConfirmVisible.value = false;
    countdown.value = 10;
    emit('navigate', '/elderly/emergency');
  } catch (error) {
    console.error("发送紧急求助失败", error);
    ElMessage.error("发送紧急求助失败，请稍后重试或直接拨打120");
  }
};

// 预约服务指令处理
const handleBookingCommand = (text) => {
  console.log("检测到预约服务指令:", text);
  emit('close');
  showBookingDialog();
};

// 显示预约弹窗
const showBookingDialog = () => {
  bookingVisible.value = true;
  bookingStep.value = 1;
  selectedService.value = null;
  resetBookingForm();
};

// 选择服务类型
const selectService = (service) => {
  selectedService.value = service;
  bookingForm.value.serviceType = service.name;
  bookingForm.value.serviceName = service.name;
};

// 进入步骤2
const goToStep2 = () => {
  if (!selectedService.value) {
    ElMessage.warning("请先选择服务类型");
    return;
  }
  bookingStep.value = 2;
};

// 重置预约表单
const resetBookingForm = () => {
  bookingForm.value = {
    serviceType: selectedService.value?.name || "",
    serviceName: selectedService.value?.name || "",
    serviceDate: "",
    serviceTime: "",
    serviceAddress: "",
    contactPhone: "",
    remark: ""
  };
  if (bookingFormRef.value) {
    bookingFormRef.value.resetFields();
  }
};

// 提交预约
const submitBooking = async () => {
  if (!bookingFormRef.value) return;

  await bookingFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        await createServiceOrder(bookingForm.value);
        ElMessage.success("预约已提交，工作人员将尽快联系您");
        bookingVisible.value = false;
        emit('navigate', '/elderly/service-booking');
      } catch (error) {
        console.error("提交预约失败", error);
        ElMessage.error("提交预约失败，请稍后重试");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 联系家属指令处理
const handleContactFamilyCommand = (text) => {
  console.log("检测到联系家属指令:", text);
  emit('close');
  emit('navigate', '/elderly/family-contact');
};
</script>

<style lang="scss" scoped>
.voice-assistant-dialog {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.voice-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 30px;
  background-color: #f5f7fa;
  border-radius: 16px;
}

.voice-btn {
  min-width: 280px;
  height: 64px;
  font-size: 20px;
  padding: 16px 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  transition: all 0.3s ease;

  &.listening {
    animation: pulse 1.5s infinite;
    background-color: #f56c6c;
    border-color: #f56c6c;
    color: #ffffff;

    &:hover {
      background-color: #f78989;
      border-color: #f78989;
    }
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  }
}

.voice-btn-text {
  font-size: 20px;
  font-weight: 500;
}

.voice-result {
  width: 100%;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 12px;
  text-align: center;
}

.result-label {
  font-size: 16px;
  color: #909399;
  margin-bottom: 8px;
}

.result-text {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.voice-warning {
  width: 100%;
}

.voice-commands {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 24px;
}

.commands-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  text-align: center;
}

.commands-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

.command-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 12px;

  span {
    font-size: 16px;
    color: #606266;
  }
}

// 紧急求助弹窗样式
.emergency-confirm-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
  text-align: center;
}

.emergency-icon-wrapper {
  margin-bottom: 20px;
}

.emergency-icon {
  color: #f56c6c;
  animation: pulse 1.5s infinite;
}

.emergency-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.countdown-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.countdown-number {
  font-size: 64px;
  font-weight: bold;
  color: #f56c6c;
  line-height: 1;
}

.countdown-text {
  font-size: 18px;
  color: #606266;
  margin-top: 8px;
}

.emergency-warning {
  font-size: 16px;
  color: #909399;
}

.cancel-btn,
.confirm-btn {
  font-size: 18px;
  padding: 16px 32px;
  height: auto;
}

// 预约弹窗样式
.booking-form-container {
  min-height: 400px;
}

.booking-step {
  padding: 20px;
}

.step-title {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 24px;
  text-align: center;
}

.service-type-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.service-type-card {
  padding: 24px;
  background-color: #f5f7fa;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;

  &:hover {
    background-color: #e6f7ff;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.active {
    border-color: #409eff;
    background-color: #e6f7ff;
  }
}

.service-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
}

.service-name {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.service-desc {
  font-size: 14px;
  color: #909399;
}

.elderly-form {
  :deep(.el-form-item__label) {
    font-size: 16px;
  }

  :deep(.el-input__inner),
  :deep(.el-textarea__inner) {
    font-size: 16px;
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>