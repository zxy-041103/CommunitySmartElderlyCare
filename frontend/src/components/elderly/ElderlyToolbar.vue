<template>
  <div class="elderly-toolbar">
    <div class="toolbar-item">
      <span class="toolbar-label">语音识别：</span>
      <VoiceRecognition @result="handleVoiceResult" />
    </div>
    <div class="toolbar-item">
      <span class="toolbar-label">字体大小：</span>
      <FontResizer @change="handleFontChange" />
    </div>
    <div class="toolbar-item">
      <span class="toolbar-label">高对比度：</span>
      <el-switch v-model="highContrast" @change="toggleHighContrast" />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import VoiceRecognition from "./VoiceRecognition.vue";
import FontResizer from "./FontResizer.vue";
import { ElMessage } from "element-plus";

const emit = defineEmits(["voiceResult", "fontChange", "highContrastChange"]);

const highContrast = ref(false);

const handleVoiceResult = (result) => {
  emit("voiceResult", result);
  ElMessage.success("识别结果: " + result);
};

const handleFontChange = (size) => {
  emit("fontChange", size);
};

const toggleHighContrast = (value) => {
  if (value) {
    document.body.classList.add("high-contrast");
  } else {
    document.body.classList.remove("high-contrast");
  }
  emit("highContrastChange", value);
  ElMessage.success(value ? "已开启高对比度" : "已关闭高对比度");
};
</script>

<style lang="scss" scoped>
.elderly-toolbar {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .toolbar-item {
    display: flex;
    align-items: center;

    .toolbar-label {
      font-size: 16px;
      font-weight: bold;
      margin-right: 10px;
    }
  }
}

:global(.high-contrast) {
  background-color: #000 !important;
  color: #fff !important;

  .el-card,
  .el-button,
  .el-input__inner {
    background-color: #000 !important;
    color: #fff !important;
    border-color: #fff !important;
  }
}
</style>
