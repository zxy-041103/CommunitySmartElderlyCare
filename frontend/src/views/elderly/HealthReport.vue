<template>
  <div class="health-report">
    <el-card class="report-card">
      <template #header>
        <div class="card-header">
          <h2 class="page-title">健康报告</h2>
          <el-button size="large" class="refresh-btn" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <!-- 时间筛选 -->
      <div class="time-filter">
        <el-button
          v-for="period in timePeriods"
          :key="period.value"
          :type="selectedPeriod === period.value ? 'primary' : 'default'"
          size="large"
          class="filter-btn"
          @click="selectTimePeriod(period.value)"
        >
          {{ period.label }}
        </el-button>
      </div>

      <!-- 健康数据卡片 -->
      <div class="health-cards">
        <HealthCard
          :health-data="bloodPressureData"
          :show-actions="true"
          @action="handleHealthAction"
        />
        <HealthCard
          :health-data="bloodSugarData"
          :show-actions="true"
          @action="handleHealthAction"
        />
        <HealthCard
          :health-data="heartRateData"
          :show-actions="true"
          @action="handleHealthAction"
        />
      </div>

      <!-- 健康数据图表 -->
      <div class="chart-section">
        <h3 class="section-title">健康数据趋势</h3>
        <div ref="healthChartRef" class="health-chart"></div>
      </div>

      <!-- 异常记录 -->
      <div class="abnormal-section">
        <h3 class="section-title">
          异常记录
          <el-tag type="danger" size="large" effect="dark">
            {{ abnormalRecords.length }} 条
          </el-tag>
        </h3>
        <el-table
          :data="abnormalRecords"
          stripe
          style="width: 100%"
          class="abnormal-table"
        >
          <el-table-column prop="date" label="日期" width="180" />
          <el-table-column prop="time" label="时间" width="120" />
          <el-table-column prop="indicator" label="指标" width="120" />
          <el-table-column prop="value" label="数值" width="100" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag type="danger">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="advice" label="建议" />
        </el-table>
      </div>

      <!-- 健康建议 -->
      <div class="advice-section">
        <h3 class="section-title">健康建议</h3>
        <el-card class="advice-card">
          <div class="advice-content">
            <el-icon :size="24" class="advice-icon"><Warning /></el-icon>
            <div class="advice-text">
              <p>
                1. 您的血压在{{
                  selectedPeriod === "7" ? "近7天" : "近30天"
                }}内有{{
                  abnormalRecords.filter((r) => r.indicator === "血压").length
                }}次异常，请按时服药并定期监测。
              </p>
              <p>2. 建议每天保持30分钟的适度运动，如散步、太极拳等。</p>
              <p>3. 饮食宜清淡，减少盐分和油脂的摄入。</p>
              <p>4. 保持充足的睡眠，建议每晚睡眠时间不少于7小时。</p>
              <p>5. 如有不适，请及时联系医生或家属。</p>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentIndicator + '详情'"
      width="700px"
    >
      <div v-if="currentDetailData" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="当前数值">{{ currentDetailData.currentValue }}</el-descriptions-item>
          <el-descriptions-item label="单位">{{ currentDetailData.unit }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentDetailData.status === 'abnormal' ? 'danger' : 'success'">
              {{ currentDetailData.status === 'abnormal' ? '异常' : '正常' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="测量时间">{{ currentDetailData.time }}</el-descriptions-item>
          <el-descriptions-item label="正常范围" :span="2">{{ currentDetailData.normalRange }}</el-descriptions-item>
          <el-descriptions-item label="健康建议" :span="2">{{ currentDetailData.advice }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="detail-subtitle">历史记录（{{ selectedPeriod === '7' ? '近7天' : '近30天' }}）</h4>
        <el-table :data="currentDetailData.history" stripe style="width: 100%">
          <el-table-column prop="date" label="日期" width="150" />
          <el-table-column prop="value" label="数值" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'abnormal' ? 'danger' : 'success'" size="small">
                {{ scope.row.status === 'abnormal' ? '异常' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="note" label="备注" />
        </el-table>
      </div>
      <template #footer>
        <el-button size="large" @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 记录数据弹窗 -->
    <el-dialog
      v-model="recordDialogVisible"
      :title="'记录' + currentIndicator + '数据'"
      width="500px"
    >
      <el-form
        ref="recordFormRef"
        :model="recordForm"
        :rules="recordRules"
        label-width="120px"
        class="record-form"
      >
        <el-form-item label="测量数值" prop="value">
          <el-input-number
            v-model="recordForm.value"
            :precision="currentIndicator === '血糖' ? 1 : 0"
            :min="0"
            :max="currentIndicator === '血压' ? 300 : currentIndicator === '血糖' ? 30 : 200"
            size="large"
            style="width: 200px"
          />
          <span class="unit-text">{{ currentUnit }}</span>
        </el-form-item>
        <el-form-item label="测量时间" prop="measureTime">
          <el-date-picker
            v-model="recordForm.measureTime"
            type="datetime"
            placeholder="选择测量时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            size="large"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="note">
          <el-input
            v-model="recordForm.note"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
            size="large"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="large" @click="recordDialogVisible = false">取消</el-button>
        <el-button size="large" type="primary" :loading="submitting" @click="submitRecord">
          {{ submitting ? '提交中...' : '确认记录' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, onMounted, watch, computed } from "vue";

// 页面名称
defineOptions({
  name: 'ElderlyHealthReport'
});
import * as echarts from "echarts";
import { Warning, Refresh } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import HealthCard from "@/components/elderly/HealthCard.vue";

const selectedPeriod = ref("7");
const timePeriods = [
  { label: "近7天", value: "7" },
  { label: "近30天", value: "30" },
];

const healthChartRef = ref(null);
let healthChart = null;

// 弹窗相关
const detailDialogVisible = ref(false);
const recordDialogVisible = ref(false);
const currentIndicator = ref("");
const currentUnit = ref("");
const currentDetailData = ref(null);
const recordFormRef = ref(null);
const submitting = ref(false);

const recordForm = ref({
  value: 0,
  measureTime: "",
  note: "",
});

const recordRules = {
  value: [{ required: true, message: "请输入测量数值", trigger: "blur" }],
  measureTime: [{ required: true, message: "请选择测量时间", trigger: "change" }],
};

const healthData = {
  7: {
    dates: [
      "1月18日",
      "1月19日",
      "1月20日",
      "1月21日",
      "1月22日",
      "1月23日",
      "1月24日",
    ],
    bloodPressure: [120, 135, 128, 140, 125, 130, 122],
    bloodSugar: [5.5, 6.2, 5.8, 6.5, 5.9, 6.1, 5.7],
    heartRate: [72, 78, 75, 82, 76, 74, 73],
  },
  30: {
    dates: [
      "12月25日",
      "12月30日",
      "1月5日",
      "1月10日",
      "1月15日",
      "1月20日",
      "1月24日",
    ],
    bloodPressure: [125, 130, 128, 135, 132, 128, 122],
    bloodSugar: [5.8, 6.0, 5.9, 6.2, 6.1, 5.9, 5.7],
    heartRate: [75, 76, 74, 78, 77, 75, 73],
  },
};

const abnormalRecords = ref([
  {
    date: "2024-01-21",
    time: "08:30",
    indicator: "血压",
    value: "140/90",
    status: "偏高",
    advice: "建议休息15分钟后重新测量",
  },
  {
    date: "2024-01-20",
    time: "14:00",
    indicator: "血糖",
    value: "6.5",
    status: "偏高",
    advice: "减少碳水化合物摄入",
  },
  {
    date: "2024-01-19",
    time: "10:00",
    indicator: "心率",
    value: "82",
    status: "偏快",
    advice: "建议适当休息",
  },
]);

const bloodPressureData = computed(() => {
  const currentData = healthData[selectedPeriod.value];
  const lastValue =
    currentData.bloodPressure[currentData.bloodPressure.length - 1];
  const isAbnormal = lastValue > 140;

  return {
    title: "血压",
    value: lastValue.toString(),
    unit: "mmHg",
    status: isAbnormal ? "abnormal" : "normal",
    time: "2024-01-24 08:30",
    advice: isAbnormal
      ? "血压偏高，请注意休息并按时服药"
      : "血压正常，继续保持",
  };
});

const bloodSugarData = computed(() => {
  const currentData = healthData[selectedPeriod.value];
  const lastValue = currentData.bloodSugar[currentData.bloodSugar.length - 1];
  const isAbnormal = lastValue > 6.1;

  return {
    title: "血糖",
    value: lastValue.toFixed(1),
    unit: "mmol/L",
    status: isAbnormal ? "abnormal" : "normal",
    time: "2024-01-24 08:30",
    advice: isAbnormal ? "血糖偏高，请控制饮食" : "血糖正常，继续保持",
  };
});

const heartRateData = computed(() => {
  const currentData = healthData[selectedPeriod.value];
  const lastValue = currentData.heartRate[currentData.heartRate.length - 1];
  const isAbnormal = lastValue > 80;

  return {
    title: "心率",
    value: lastValue.toString(),
    unit: "次/分",
    status: isAbnormal ? "abnormal" : "normal",
    time: "2024-01-24 08:30",
    advice: isAbnormal ? "心率偏快，建议适当休息" : "心率正常，继续保持",
  };
});

onMounted(() => {
  initHealthChart();
  // 设置默认测量时间为当前时间
  const now = new Date();
  recordForm.value.measureTime = now.toISOString().slice(0, 19).replace('T', ' ');
});

watch(selectedPeriod, (newPeriod) => {
  updateHealthChart(newPeriod);
});

const selectTimePeriod = (period) => {
  selectedPeriod.value = period;
};

const refreshData = () => {
  ElMessage.success("数据已刷新");
};

const handleHealthAction = (data) => {
  const { action, healthData } = data;
  currentIndicator.value = healthData.title;
  currentUnit.value = healthData.unit;

  if (action === "detail") {
    showDetailDialog(healthData);
  } else if (action === "record") {
    showRecordDialog(healthData);
  }
};

const showDetailDialog = (cardData) => {
  const periodData = healthData[selectedPeriod.value];
  let historyData = [];
  let normalRange = "";

  if (cardData.title === "血压") {
    historyData = periodData.dates.map((date, index) => ({
      date,
      value: periodData.bloodPressure[index],
      status: periodData.bloodPressure[index] > 140 ? "abnormal" : "normal",
      note: periodData.bloodPressure[index] > 140 ? "血压偏高" : "正常",
    }));
    normalRange = "90-140 mmHg";
  } else if (cardData.title === "血糖") {
    historyData = periodData.dates.map((date, index) => ({
      date,
      value: periodData.bloodSugar[index],
      status: periodData.bloodSugar[index] > 6.1 ? "abnormal" : "normal",
      note: periodData.bloodSugar[index] > 6.1 ? "血糖偏高" : "正常",
    }));
    normalRange = "3.9-6.1 mmol/L";
  } else if (cardData.title === "心率") {
    historyData = periodData.dates.map((date, index) => ({
      date,
      value: periodData.heartRate[index],
      status: periodData.heartRate[index] > 80 ? "abnormal" : "normal",
      note: periodData.heartRate[index] > 80 ? "心率偏快" : "正常",
    }));
    normalRange = "60-80 次/分";
  }

  currentDetailData.value = {
    currentValue: cardData.value,
    unit: cardData.unit,
    status: cardData.status,
    time: cardData.time,
    normalRange,
    advice: cardData.advice,
    history: historyData,
  };

  detailDialogVisible.value = true;
};

const showRecordDialog = (healthData) => {
  recordForm.value = {
    value: parseFloat(healthData.value) || 0,
    measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    note: "",
  };
  recordDialogVisible.value = true;
};

const submitRecord = async () => {
  if (!recordFormRef.value) return;

  try {
    await recordFormRef.value.validate();
    submitting.value = true;

    // 模拟提交数据到后端
    setTimeout(() => {
      ElMessage.success(`${currentIndicator.value}数据记录成功`);
      recordDialogVisible.value = false;
      submitting.value = false;

      // 添加新记录到异常记录（如果是异常值）
      const value = recordForm.value.value;
      let isAbnormal = false;
      let status = "";
      let advice = "";

      if (currentIndicator.value === "血压") {
        isAbnormal = value > 140;
        status = isAbnormal ? "偏高" : "正常";
        advice = isAbnormal ? "建议休息15分钟后重新测量" : "血压正常";
      } else if (currentIndicator.value === "血糖") {
        isAbnormal = value > 6.1;
        status = isAbnormal ? "偏高" : "正常";
        advice = isAbnormal ? "减少碳水化合物摄入" : "血糖正常";
      } else if (currentIndicator.value === "心率") {
        isAbnormal = value > 80;
        status = isAbnormal ? "偏快" : "正常";
        advice = isAbnormal ? "建议适当休息" : "心率正常";
      }

      if (isAbnormal) {
        const now = new Date();
        abnormalRecords.value.unshift({
          date: now.toISOString().slice(0, 10),
          time: now.toTimeString().slice(0, 5),
          indicator: currentIndicator.value,
          value: value.toString(),
          status,
          advice,
        });
      }
    }, 500);
  } catch (error) {
    console.error("表单验证失败:", error);
  }
};

const initHealthChart = () => {
  if (healthChartRef.value) {
    healthChart = echarts.init(healthChartRef.value);
    updateHealthChart(selectedPeriod.value);
  }
};

const updateHealthChart = (period) => {
  if (!healthChart) return;

  const data = healthData[period];
  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        label: {
          backgroundColor: "#6a7985",
        },
      },
    },
    legend: {
      data: ["血压", "血糖", "心率"],
      textStyle: {
        fontSize: 16,
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: data.dates,
      axisLabel: {
        fontSize: 14,
      },
    },
    yAxis: {
      type: "value",
      axisLabel: {
        fontSize: 14,
      },
    },
    series: [
      {
        name: "血压",
        type: "line",
        stack: "Total",
        data: data.bloodPressure,
        lineStyle: {
          width: 4,
        },
        itemStyle: {
          color: "#F56C6C",
        },
        markLine: {
          silent: true,
          data: [
            { type: "average", name: "平均值" },
            {
              yAxis: 140,
              name: "高压阈值",
              lineStyle: { color: "#F56C6C" },
              label: { color: "#F56C6C" },
            },
          ],
        },
      },
      {
        name: "血糖",
        type: "line",
        stack: "Total",
        data: data.bloodSugar,
        lineStyle: {
          width: 4,
        },
        itemStyle: {
          color: "#E6A23C",
        },
        markLine: {
          silent: true,
          data: [
            { type: "average", name: "平均值" },
            {
              yAxis: 6.1,
              name: "阈值",
              lineStyle: { color: "#E6A23C" },
              label: { color: "#E6A23C" },
            },
          ],
        },
      },
      {
        name: "心率",
        type: "line",
        stack: "Total",
        data: data.heartRate,
        lineStyle: {
          width: 4,
        },
        itemStyle: {
          color: "#409EFF",
        },
        markLine: {
          silent: true,
          data: [
            { type: "average", name: "平均值" },
            {
              yAxis: 80,
              name: "上限",
              lineStyle: { color: "#409EFF" },
              label: { color: "#409EFF" },
            },
          ],
        },
      },
    ],
  };

  healthChart.setOption(option);
};

window.addEventListener("resize", () => {
  if (healthChart) {
    healthChart.resize();
  }
});
</script>

<style lang="scss" scoped>
.health-report {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.report-card {
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.refresh-btn {
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 8px;
}

.time-filter {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.filter-btn {
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 8px;
  min-width: 120px;
}

.health-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.health-chart {
  width: 100%;
  height: 400px;
}

.abnormal-section {
  margin-bottom: 30px;
}

.abnormal-table {
  font-size: 16px;
}

.advice-section {
  margin-bottom: 20px;
}

.advice-card {
  background-color: #f5f7fa;
  border-radius: 12px;
}

.advice-content {
  display: flex;
  gap: 15px;
  align-items: flex-start;
}

.advice-icon {
  color: #e6a23c;
  flex-shrink: 0;
  margin-top: 4px;
}

.advice-text {
  flex: 1;
}

.advice-text p {
  font-size: 16px;
  color: #606266;
  line-height: 1.8;
  margin: 0 0 10px 0;
}

.advice-text p:last-child {
  margin-bottom: 0;
}

.detail-content {
  padding: 10px;
}

.detail-subtitle {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 30px 0 15px 0;
}

.record-form {
  padding: 20px;
}

.unit-text {
  margin-left: 10px;
  font-size: 16px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-report {
    padding: 15px;
  }

  .page-title {
    font-size: 24px;
  }

  .health-cards {
    grid-template-columns: 1fr;
  }

  .health-chart {
    height: 300px;
  }
}
</style>
