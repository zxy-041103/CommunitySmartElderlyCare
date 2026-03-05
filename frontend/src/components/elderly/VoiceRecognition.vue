<template>
  <div class="voice-recognition">
    <el-button
      :type="isActive ? 'danger' : 'primary'"
      size="large"
      class="voice-btn"
      :class="{ listening: isActive }"
      @click="toggleVoiceRecognition"
    >
      <el-icon :size="24">
        <Microphone v-if="!isActive" />
        <Loading v-else />
      </el-icon>
      <span class="voice-btn-text">{{
        isActive ? "语音识别中..." : "点击开始语音识别"
      }}</span>
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
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { Microphone, Loading } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import voiceRecognition from "@/utils/voice";

const props = defineProps({
  autoStart: {
    type: Boolean,
    default: false,
  },
  continuous: {
    type: Boolean,
    default: false,
  },
  emergencyKeyword: {
    type: String,
    default: "呼叫护工",
  },
});

const emit = defineEmits(["result", "error", "start", "end", "emergency"]);

const isActive = ref(false);
const isSupported = ref(true);
const lastResult = ref("");

const toggleVoiceRecognition = () => {
  if (isActive.value) {
    stopVoiceRecognition();
  } else {
    startVoiceRecognition();
  }
};

const startVoiceRecognition = () => {
  if (!voiceRecognition.isSupported()) {
    isSupported.value = false;
    ElMessage.error("您的浏览器不支持语音识别功能，请使用Chrome浏览器");
    return;
  }

  const started = props.continuous
    ? voiceRecognition.startContinuous()
    : voiceRecognition.start();

  if (started) {
    isActive.value = true;
    ElMessage.success("语音识别已启动，请说话");
  }
};

const stopVoiceRecognition = () => {
  if (props.continuous) {
    voiceRecognition.stopContinuous();
  } else {
    voiceRecognition.stop();
  }

  isActive.value = false;
  ElMessage.info("语音识别已停止");
};

const handleResult = (text) => {
  lastResult.value = text;
  emit("result", text);
};

const handleError = (error) => {
  isActive.value = false;
  emit("error", error);
};

const handleStart = () => {
  isActive.value = true;
  emit("start");
};

const handleEnd = () => {
  isActive.value = false;
  emit("end");
};

const handleEmergency = (text) => {
  emit("emergency", text);
};

onMounted(() => {
  isSupported.value = voiceRecognition.isSupported();

  voiceRecognition.setEmergencyKeyword(props.emergencyKeyword);

  voiceRecognition.on("result", handleResult);
  voiceRecognition.on("error", handleError);
  voiceRecognition.on("start", handleStart);
  voiceRecognition.on("end", handleEnd);
  voiceRecognition.on("emergency", handleEmergency);

  if (props.autoStart) {
    startVoiceRecognition();
  }
});

onBeforeUnmount(() => {
  voiceRecognition.off("result", handleResult);
  voiceRecognition.off("error", handleError);
  voiceRecognition.off("start", handleStart);
  voiceRecognition.off("end", handleEnd);
  voiceRecognition.off("emergency", handleEmergency);

  if (isActive.value) {
    stopVoiceRecognition();
  }
});
</script>

<style lang="scss" scoped>
.voice-recognition {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.voice-btn {
  min-width: 240px;
  height: 56px;
  font-size: 18px;
  padding: 16px 32px;
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
  font-size: 18px;
  font-weight: 500;
}

.voice-result {
  width: 100%;
  max-width: 500px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 12px;
  border-left: 4px solid #409eff;
}

.result-label {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
  margin: 0 0 10px 0;
}

.result-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  line-height: 1.4;
}

.voice-warning {
  width: 100%;
  max-width: 500px;
}

/* 动画效果 */
@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.4);
  }
  50% {
    transform: scale(1.02);
    box-shadow: 0 0 0 10px rgba(245, 108, 108, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0);
  }
}

/* WCAG AA 标准色彩对比度 */
.el-button {
  font-size: 18px;
  font-weight: 500;
  border-radius: 12px;

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

  &--danger {
    background-color: #f56c6c;
    border-color: #f56c6c;
    color: #ffffff;

    &:hover,
    &:focus {
      background-color: #f78989;
      border-color: #f78989;
    }
  }
}

:deep(.el-alert) {
  font-size: 16px;
  padding: 16px 20px;
  border-radius: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .voice-btn {
    min-width: 100%;
    font-size: 16px;
    height: 48px;
  }

  .voice-btn-text {
    font-size: 16px;
  }

  .voice-result {
    padding: 16px;
  }

  .result-text {
    font-size: 16px;
  }
}
</style>
