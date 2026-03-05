<template>
  <div class="scan-verification">
    <el-dialog v-model="dialogVisible" title="扫码核销" width="500px" @close="handleClose">
      <div class="scan-content">
        <div v-if="!scannedData" class="scan-input-section">
          <el-input
            v-model="verificationCode"
            placeholder="请输入核销码或扫描二维码"
            size="large"
            clearable
            class="code-input"
          >
            <template #append>
              <el-button :icon="Camera" @click="openCamera" :loading="cameraLoading">
                扫码
              </el-button>
            </template>
          </el-input>

          <div v-if="cameraActive" class="camera-section">
            <video ref="videoRef" class="camera-preview" autoplay></video>
            <canvas ref="canvasRef" style="display: none"></canvas>
            <div class="camera-overlay">
              <div class="scan-line"></div>
            </div>
            <el-button type="danger" @click="closeCamera" class="close-camera-btn">
              关闭摄像头
            </el-button>
          </div>

          <el-button
            type="primary"
            size="large"
            class="verify-btn"
            @click="handleVerify"
            :loading="verifying"
            :disabled="!verificationCode"
          >
            验证核销码
          </el-button>
        </div>

        <div v-else class="verification-result">
          <el-result
            :icon="verificationSuccess ? 'success' : 'error'"
            :title="verificationSuccess ? '核销成功' : '核销失败'"
            :sub-title="resultMessage"
          >
            <template #extra>
              <el-descriptions v-if="verificationSuccess" :column="1" border>
                <el-descriptions-item label="订单编号">{{ scannedData.orderNo }}</el-descriptions-item>
                <el-descriptions-item label="服务类型">{{ scannedData.serviceType }}</el-descriptions-item>
                <el-descriptions-item label="服务名称">{{ scannedData.serviceName }}</el-descriptions-item>
                <el-descriptions-item label="预约日期">{{ scannedData.serviceDate }}</el-descriptions-item>
                <el-descriptions-item label="预约时间">{{ scannedData.serviceTime }}</el-descriptions-item>
                <el-descriptions-item label="用户姓名">{{ scannedData.userName }}</el-descriptions-item>
                <el-descriptions-item label="服务地址">{{ scannedData.serviceAddress }}</el-descriptions-item>
              </el-descriptions>
              <el-button type="primary" @click="resetScan">
                {{ verificationSuccess ? "继续核销" : "重新核销" }}
              </el-button>
            </template>
          </el-result>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from "vue";
import { Camera } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { verifyService } from "@/api/serviceOrder";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue", "verification-success", "verification-failed"]);

const dialogVisible = ref(props.modelValue);
const verificationCode = ref("");
const cameraActive = ref(false);
const cameraLoading = ref(false);
const verifying = ref(false);
const scannedData = ref(null);
const verificationSuccess = ref(false);
const resultMessage = ref("");
const videoRef = ref(null);
const canvasRef = ref(null);

let mediaStream = null;
let scanInterval = null;

const openCamera = async () => {
  cameraLoading.value = true;
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: "environment" },
    });
    if (videoRef.value) {
      videoRef.value.srcObject = mediaStream;
      cameraActive.value = true;
      startScanning();
    }
  } catch (error) {
    console.error("无法访问摄像头:", error);
    ElMessage.error("无法访问摄像头，请检查权限设置");
  } finally {
    cameraLoading.value = false;
  }
};

const closeCamera = () => {
  if (mediaStream) {
    mediaStream.getTracks().forEach((track) => track.stop());
    mediaStream = null;
  }
  if (scanInterval) {
    clearInterval(scanInterval);
    scanInterval = null;
  }
  cameraActive.value = false;
};

const startScanning = () => {
  scanInterval = setInterval(() => {
    if (videoRef.value && canvasRef.value) {
      const video = videoRef.value;
      const canvas = canvasRef.value;
      const context = canvas.getContext("2d");

      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      context.drawImage(video, 0, 0, canvas.width, canvas.height);

      const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
      const code = jsQR(imageData.data, imageData.width, imageData.height);

      if (code) {
        verificationCode.value = code.data;
        closeCamera();
        ElMessage.success("扫码成功");
        handleVerify();
      }
    }
  }, 500);
};

const handleVerify = async () => {
  if (!verificationCode.value) {
    ElMessage.warning("请输入核销码");
    return;
  }

  verifying.value = true;
  try {
    const response = await verifyService({
      verificationCode: verificationCode.value,
    });

    if (response.code === 200) {
      scannedData.value = response.data;
      verificationSuccess.value = true;
      resultMessage.value = "服务已成功核销";
      emit("verification-success", response.data);
      ElMessage.success("核销成功");
    } else {
      verificationSuccess.value = false;
      resultMessage.value = response.message || "核销码无效或已过期";
      emit("verification-failed", response.message);
    }
  } catch (error) {
    console.error("核销失败:", error);
    verificationSuccess.value = false;
    resultMessage.value = error.message || "核销失败，请重试";
    emit("verification-failed", error.message);
  } finally {
    verifying.value = false;
  }
};

const resetScan = () => {
  verificationCode.value = "";
  scannedData.value = null;
  verificationSuccess.value = false;
  resultMessage.value = "";
};

const handleClose = () => {
  closeCamera();
  resetScan();
  emit("update:modelValue", false);
};

onUnmounted(() => {
  closeCamera();
});
</script>

<style lang="scss" scoped>
.scan-verification {
  .scan-content {
    padding: 20px 0;
  }

  .scan-input-section {
    .code-input {
      margin-bottom: 20px;
    }

    .verify-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
    }
  }

  .camera-section {
    position: relative;
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: hidden;

    .camera-preview {
      width: 100%;
      height: 300px;
      object-fit: cover;
    }

    .camera-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      pointer-events: none;

      .scan-line {
        width: 80%;
        height: 2px;
        background: linear-gradient(90deg, transparent, #409eff, transparent);
        animation: scan 2s linear infinite;
      }
    }

    .close-camera-btn {
      position: absolute;
      bottom: 10px;
      left: 50%;
      transform: translateX(-50%);
    }
  }

  .verification-result {
    .el-descriptions {
      margin-bottom: 20px;
    }
  }
}

@keyframes scan {
  0% {
    transform: translateY(-150px);
  }
  100% {
    transform: translateY(150px);
  }
}
</style>
