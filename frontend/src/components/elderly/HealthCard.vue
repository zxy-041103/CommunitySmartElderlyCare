<template>
  <div
    class="health-card"
    :class="{ abnormal: healthData.status === 'abnormal' }"
  >
    <div class="health-card-header">
      <h3 class="health-card-title">{{ healthData.title }}</h3>
      <div class="health-card-status">
        <el-tag :type="getStatusType(healthData.status)">
          {{ getStatusText(healthData.status) }}
        </el-tag>
      </div>
    </div>

    <div class="health-card-body">
      <div class="health-data-value">{{ healthData.value }}</div>
      <div class="health-data-unit">{{ healthData.unit }}</div>

      <div v-if="healthData.time" class="health-data-time">
        {{ healthData.time }}
      </div>
    </div>

    <div v-if="healthData.advice" class="health-card-footer">
      <p class="health-advice">{{ healthData.advice }}</p>
    </div>

    <div v-if="showActions" class="health-card-actions">
      <el-button
        type="primary"
        size="large"
        class="health-action-btn"
        @click="handleAction('detail')"
      >
        <el-icon><View /></el-icon>
        查看详情
      </el-button>
      <el-button
        type="default"
        size="large"
        class="health-action-btn"
        @click="handleAction('record')"
      >
        <el-icon><Document /></el-icon>
        记录数据
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { View, Document } from "@element-plus/icons-vue";

const props = defineProps({
  healthData: {
    type: Object,
    required: true,
    default: () => ({
      title: "血压",
      value: "120/80",
      unit: "mmHg",
      status: "normal",
      time: "2024-01-24 08:30",
      advice: "",
    }),
  },
  showActions: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["action"]);

const getStatusType = (status) => {
  const statusMap = {
    normal: "success",
    abnormal: "danger",
    warning: "warning",
  };
  return statusMap[status] || "info";
};

const getStatusText = (status) => {
  const statusMap = {
    normal: "正常",
    abnormal: "异常",
    warning: "警告",
  };
  return statusMap[status] || status;
};

const handleAction = (action) => {
  emit("action", {
    action,
    healthData: props.healthData,
  });
};
</script>

<style lang="scss" scoped>
.health-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border-left: 4px solid #67c23a;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  &.abnormal {
    border-left-color: #f56c6c;
    background-color: #fef0f0;
  }
}

.health-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.health-card-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  line-height: 1.2;
}

.health-card-body {
  text-align: center;
  margin-bottom: 20px;
}

.health-data-value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 10px;
}

.health-data-unit {
  font-size: 18px;
  color: #606266;
  margin-bottom: 15px;
}

.health-data-time {
  font-size: 16px;
  color: #909399;
}

.health-card-footer {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  border-left: 3px solid #409eff;
}

.health-advice {
  font-size: 16px;
  color: #606266;
  margin: 0;
  line-height: 1.4;
}

.health-card-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.health-action-btn {
  min-width: 160px;
  height: 48px;
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-card {
    padding: 20px;
  }

  .health-data-value {
    font-size: 28px;
  }

  .health-card-actions {
    flex-direction: column;
  }

  .health-action-btn {
    width: 100%;
    min-width: 100%;
  }
}

/* WCAG AA 标准色彩对比度 */
:deep(.el-tag) {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 4px;
}

.el-button {
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;

  &--primary {
    background-color: #409eff;
    border-color: #409eff;

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

/* 动画效果 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

.health-card.abnormal {
  animation: pulse 2s infinite;
}
</style>
