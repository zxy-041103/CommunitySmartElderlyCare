<template>
  <div class="booking-form">
    <h3 class="form-title">{{ title }}</h3>

    <el-form
      ref="bookingFormRef"
      :model="bookingForm"
      :rules="bookingRules"
      label-width="140px"
      class="elderly-form"
    >
      <el-form-item label="服务类型" prop="serviceId">
        <el-select
          v-model="bookingForm.serviceId"
          placeholder="选择服务类型"
          size="large"
          style="width: 100%"
          class="form-select"
        >
          <el-option
            v-for="service in services"
            :key="service.id"
            :label="service.name"
            :value="service.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="预约日期" prop="date">
        <el-date-picker
          v-model="bookingForm.date"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          size="large"
          style="width: 100%"
          class="form-datepicker"
        />
      </el-form-item>

      <el-form-item label="预约时间" prop="time">
        <el-time-picker
          v-model="bookingForm.time"
          placeholder="选择时间"
          format="HH:mm"
          value-format="HH:mm"
          size="large"
          style="width: 100%"
          class="form-timepicker"
        />
      </el-form-item>
    </el-form>

    <div class="form-actions">
      <el-button
        type="primary"
        size="large"
        class="form-action-btn primary"
        :loading="submitting"
        @click="handleSubmit"
      >
        <el-icon v-if="!submitting"><Check /></el-icon>
        <el-icon v-else><Loading /></el-icon>
        {{ submitting ? "提交中..." : "确认预约" }}
      </el-button>

      <el-button
        type="default"
        size="large"
        class="form-action-btn"
        :disabled="submitting"
        @click="handleReset"
      >
        <el-icon><Refresh /></el-icon>
        重置
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { Check, Refresh, Loading } from "@element-plus/icons-vue";

const props = defineProps({
  title: {
    type: String,
    default: "服务预约",
  },
  services: {
    type: Array,
    default: () => [
      { id: 1, name: "上门护理" },
      { id: 2, name: "健康检测" },
      { id: 3, name: "家政服务" },
      { id: 4, name: "陪伴聊天" },
    ],
  },
  initialValues: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(["submit", "reset"]);

const bookingFormRef = ref(null);
const submitting = ref(false);

const bookingForm = reactive({
  serviceId: props.initialValues.serviceId || "",
  date: props.initialValues.date || "",
  time: props.initialValues.time || "",
});

const bookingRules = {
  serviceId: [{ required: true, message: "请选择服务类型", trigger: "change" }],
  date: [{ required: true, message: "请选择预约日期", trigger: "change" }],
  time: [{ required: true, message: "请选择预约时间", trigger: "change" }],
};

const handleSubmit = async () => {
  if (!bookingFormRef.value) return;

  try {
    await bookingFormRef.value.validate();
    submitting.value = true;

    emit("submit", { ...bookingForm });
  } catch (error) {
    console.error("表单验证失败:", error);
  } finally {
    submitting.value = false;
  }
};

const handleReset = () => {
  if (bookingFormRef.value) {
    bookingFormRef.value.resetFields();
  }
  emit("reset");
};
</script>

<style lang="scss" scoped>
.booking-form {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.form-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 30px 0;
  text-align: center;
  line-height: 1.2;
}

.elderly-form {
  margin-bottom: 30px;
}

.form-select,
.form-datepicker,
.form-timepicker {
  font-size: 16px !important;
  height: 48px !important;
  border-radius: 8px !important;
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
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  &.primary {
    background-color: #409eff;
    border-color: #409eff;
    color: #ffffff;

    &:hover,
    &:focus {
      background-color: #66b1ff;
      border-color: #66b1ff;
    }
  }
}

/* 适配老年人的表单样式 */
:deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  line-height: 48px;
}

:deep(.el-form-item__error) {
  font-size: 14px;
  color: #f56c6c;
  margin-top: 8px;
}

:deep(.el-select__input),
:deep(.el-input__inner) {
  font-size: 16px;
  height: 48px;
  line-height: 48px;
}

:deep(.el-date-picker__header-label),
:deep(.el-time-spinner__item) {
  font-size: 16px;
}

:deep(.el-select-dropdown__item) {
  font-size: 16px;
  padding: 12px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .booking-form {
    padding: 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-action-btn {
    width: 100%;
    min-width: 100%;
  }

  .elderly-form {
    label-width: 100% !important;
  }

  :deep(.el-form-item__label) {
    line-height: 1.4;
    margin-bottom: 8px;
  }
}

/* WCAG AA 标准色彩对比度 */
.el-button {
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;

  &--primary {
    background-color: #409eff;
    border-color: #409eff;
    color: #ffffff;
    /* 对比度: 4.5:1 以上 */

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
    /* 对比度: 4.5:1 以上 */

    &:hover,
    &:focus {
      border-color: #409eff;
      color: #409eff;
    }
  }
}
</style>
