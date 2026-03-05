<template>
  <div class="font-resizer">
    <el-button-group>
      <el-button
        :size="size"
        :disabled="currentSize <= minSize"
        @click="decreaseFont"
      >
        <el-icon><ZoomOut /></el-icon>
      </el-button>
      <el-button :size="size">{{ currentSize }}px</el-button>
      <el-button
        :size="size"
        :disabled="currentSize >= maxSize"
        @click="increaseFont"
      >
        <el-icon><ZoomIn /></el-icon>
      </el-button>
    </el-button-group>
    <el-button :size="size" style="margin-left: 10px" @click="resetFont">
      重置
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  size: {
    type: String,
    default: "default",
  },
  minSize: {
    type: Number,
    default: 12,
  },
  maxSize: {
    type: Number,
    default: 24,
  },
  defaultSize: {
    type: Number,
    default: 14,
  },
});

const emit = defineEmits(["change"]);

const currentSize = ref(props.defaultSize);

onMounted(() => {
  applyFontSize(currentSize.value);
});

const increaseFont = () => {
  if (currentSize.value < props.maxSize) {
    currentSize.value += 2;
    applyFontSize(currentSize.value);
    emit("change", currentSize.value);
  }
};

const decreaseFont = () => {
  if (currentSize.value > props.minSize) {
    currentSize.value -= 2;
    applyFontSize(currentSize.value);
    emit("change", currentSize.value);
  }
};

const resetFont = () => {
  currentSize.value = props.defaultSize;
  applyFontSize(currentSize.value);
  emit("change", currentSize.value);
  ElMessage.success("字体已重置");
};

const applyFontSize = (size) => {
  document.documentElement.style.fontSize = size + "px";
};

defineExpose({
  increaseFont,
  decreaseFont,
  resetFont,
  currentSize,
});
</script>

<style lang="scss" scoped>
.font-resizer {
  display: inline-block;
}
</style>
