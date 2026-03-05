<template>
  <div class="family-monitoring">
    <div v-if="hasWarning" class="warning-section">
      <div class="warning-content">
        <el-icon :size="32" class="warning-icon"><Warning /></el-icon>
        <div class="warning-info">
          <h3 class="warning-title">健康异常预警</h3>
          <p class="warning-message">{{ warningMessage }}</p>
        </div>
        <el-button type="danger" size="large" @click="handleWarning">
          查看详情
        </el-button>
      </div>
    </div>

    <div class="health-dashboard">
      <div class="page-header">
        <h2 class="page-title">监护中心</h2>
        <div class="header-actions">
          <el-button type="primary" @click="goToAddRelation">
            <el-icon><Plus /></el-icon>
            添加关联关系
          </el-button>
          <el-button :icon="Refresh" @click="refreshData" :loading="refreshing">
            刷新数据
          </el-button>
          <span class="last-update">最后更新: {{ lastUpdateTime }}</span>
        </div>
      </div>

      <div class="elderly-selector">
        <el-select
          v-model="selectedElderlyId"
          placeholder="选择老人"
          size="large"
          @change="selectElderly"
        >
          <el-option
            v-for="elderly in elderlyList"
            :key="elderly.id"
            :label="elderly.name"
            :value="elderly.id"
          />
        </el-select>
      </div>

      <div class="health-cards">
        <el-card
          v-for="item in healthDataList"
          :key="item.type"
          class="health-card"
          :class="{ abnormal: item.status === 'abnormal' }"
        >
          <div class="health-card-content">
            <div class="health-info">
              <h3 class="health-label">{{ item.label }}</h3>
              <div class="health-value">{{ item.value }}</div>
              <div class="health-status" :class="item.status">
                {{ item.statusText }}
              </div>
              <div class="health-time">{{ item.monitorTime }}</div>
            </div>
            <div class="health-trend">
              <div :ref="(el) => setTrendChartRef(el, item.type)" class="trend-chart"></div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="health-trend-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3 class="section-title">健康趋势</h3>
              <el-radio-group v-model="trendPeriod" size="small" @change="updateTrendChart">
                <el-radio-button label="7">近7天</el-radio-button>
                <el-radio-button label="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="healthTrendChartRef" class="trend-chart-large"></div>
        </el-card>
      </div>
    </div>

    <div class="service-tracking">
      <div class="section-header">
        <h3 class="section-title">服务进度跟踪</h3>
        <el-button type="primary" link @click="goToBooking">预约新服务</el-button>
      </div>
      <div class="service-list" v-loading="serviceLoading">
        <div v-if="serviceList.length === 0" class="empty-state">
          <el-empty description="暂无服务记录" />
        </div>
        <el-card
          v-for="service in serviceList"
          :key="service.id"
          class="service-card"
          :class="{ urgent: service.isUrgent }"
        >
          <div class="service-content">
            <div class="service-header">
              <h4 class="service-name">{{ service.serviceName }}</h4>
              <el-tag :type="getStatusType(service.orderStatus)" size="large">
                {{ getStatusText(service.orderStatus) }}
              </el-tag>
            </div>
            <div class="service-info">
              <div class="info-item">
                <span class="info-label">订单编号：</span>
                <span class="info-value">{{ service.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务类型：</span>
                <span class="info-value">{{ service.serviceType }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">预约日期：</span>
                <span class="info-value">{{ service.serviceDate }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">预约时间：</span>
                <span class="info-value">{{ service.serviceTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务地址：</span>
                <span class="info-value">{{ service.serviceAddress }}</span>
              </div>
              <div v-if="service.serviceDescription" class="info-item">
                <span class="info-label">服务描述：</span>
                <span class="info-value">{{ service.serviceDescription }}</span>
              </div>
            </div>
            <div class="service-actions">
              <el-button
                v-if="service.orderStatus === 'pending'"
                type="danger"
                size="small"
                @click="cancelServiceOrder(service.id)"
              >
                取消订单
              </el-button>
              <el-button
                v-if="service.orderStatus === 'completed'"
                type="primary"
                size="small"
                @click="goToEvaluation(service.id)"
              >
                评价服务
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="viewServiceDetails(service)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="serviceDetailVisible" title="服务详情" width="600px">
      <el-descriptions v-if="currentService" :column="1" border>
        <el-descriptions-item label="订单编号">{{ currentService.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ currentService.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ currentService.serviceType }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentService.serviceDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentService.serviceTime }}</el-descriptions-item>
        <el-descriptions-item label="服务地址">{{ currentService.serviceAddress }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentService.orderStatus)">
            {{ getStatusText(currentService.orderStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务描述">{{ currentService.serviceDescription || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentService.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="warningDetailVisible" title="健康预警详情" width="600px">
      <div class="warning-detail">
        <el-alert
          :title="currentWarning?.warningType"
          :description="currentWarning?.warningMessage"
          :type="getWarningType(currentWarning?.warningLevel)"
          show-icon
          :closable="false"
        />
        <el-descriptions v-if="currentWarning" :column="1" border class="warning-info">
          <el-descriptions-item label="预警级别">
            <el-tag :type="getWarningTagType(currentWarning.warningLevel)">
              {{ getWarningLevelText(currentWarning.warningLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预警时间">{{ currentWarning.createTime }}</el-descriptions-item>
          <el-descriptions-item label="处理状态">
            {{ currentWarning.notified === 1 ? '已通知' : '未通知' }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="warning-advice">
          <h4>建议措施：</h4>
          <ul>
            <li>请及时关注老人身体状况</li>
            <li>如有异常请立即联系医生或护工</li>
            <li>建议近期增加健康监测频率</li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";

// 页面名称
defineOptions({
  name: 'FamilyMonitoring'
});
import { useRouter } from "vue-router";
import * as echarts from "echarts";
import { Warning, Refresh, Plus } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getServiceOrderProgress, cancelOrder } from "@/api/serviceOrder";
import { queryHealthDataByTimeRange, getLatestHealthData } from "@/api/healthData";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();

const hasWarning = ref(false);
const warningMessage = ref("");
const warnings = ref([]);
const currentWarning = ref(null);
const warningDetailVisible = ref(false);

const elderlyList = ref([]);
const selectedElderlyId = ref(null);

const healthDataList = ref([]);
const serviceList = ref([]);
const serviceLoading = ref(false);
const currentService = ref(null);
const serviceDetailVisible = ref(false);

const refreshing = ref(false);
const lastUpdateTime = ref("");
const trendPeriod = ref("7");

const healthTrendChartRef = ref(null);
const trendChartRefs = ref({});
let healthTrendChart = null;
let refreshInterval = null;

const setTrendChartRef = (el, type) => {
  if (el) {
    trendChartRefs.value[type] = el;
  }
};

const fetchFamilyElderly = async () => {
  try {
    const familyId = userStore.userId;
    elderlyList.value = [
      { id: 1, name: "父亲 - 张三" },
      { id: 2, name: "母亲 - 李四" },
    ];
    if (elderlyList.value.length > 0) {
      selectedElderlyId.value = elderlyList.value[0].id;
      await fetchHealthData();
      await fetchServiceProgress();
    }
  } catch (error) {
    console.error("获取关联老人失败:", error);
  }
};

const fetchHealthData = async () => {
  if (!selectedElderlyId.value) return;

  try {
    const response = await queryHealthDataByTimeRange({
      userId: selectedElderlyId.value,
      startTime: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
      endTime: new Date().toISOString(),
    });

    if (response.code === 200 && response.data) {
      const latestData = response.data.latest || response.data;
      healthDataList.value = [
        {
          type: "bloodPressure",
          label: "血压",
          value: latestData.systolicPressure && latestData.diastolicPressure
            ? `${latestData.systolicPressure}/${latestData.diastolicPressure} mmHg`
            : "--/-- mmHg",
          status: getBloodPressureStatus(latestData.systolicPressure, latestData.diastolicPressure),
          statusText: getBloodPressureStatusText(latestData.systolicPressure, latestData.diastolicPressure),
          monitorTime: latestData.monitorTime ? formatDateTime(latestData.monitorTime) : "--",
          rawData: latestData,
        },
        {
          type: "bloodSugar",
          label: "血糖",
          value: latestData.bloodSugar ? `${latestData.bloodSugar} mmol/L` : "-- mmol/L",
          status: getBloodSugarStatus(latestData.bloodSugar),
          statusText: getBloodSugarStatusText(latestData.bloodSugar),
          monitorTime: latestData.monitorTime ? formatDateTime(latestData.monitorTime) : "--",
          rawData: latestData,
        },
        {
          type: "heartRate",
          label: "心率",
          value: latestData.heartRate ? `${latestData.heartRate} bpm` : "-- bpm",
          status: getHeartRateStatus(latestData.heartRate),
          statusText: getHeartRateStatusText(latestData.heartRate),
          monitorTime: latestData.monitorTime ? formatDateTime(latestData.monitorTime) : "--",
          rawData: latestData,
        },
        {
          type: "temperature",
          label: "体温",
          value: latestData.temperature ? `${latestData.temperature} °C` : "-- °C",
          status: getTemperatureStatus(latestData.temperature),
          statusText: getTemperatureStatusText(latestData.temperature),
          monitorTime: latestData.monitorTime ? formatDateTime(latestData.monitorTime) : "--",
          rawData: latestData,
        },
      ];

      checkHealthWarnings(latestData);

      await nextTick();
      initTrendCharts();
    }
  } catch (error) {
    console.error("获取健康数据失败:", error);
    ElMessage.error("获取健康数据失败");
  }
};

const fetchServiceProgress = async () => {
  if (!selectedElderlyId.value) return;

  serviceLoading.value = true;
  try {
    const response = await getServiceOrderProgress({
      page: 1,
      size: 10,
    });

    if (response.code === 200 && response.data) {
      serviceList.value = response.data.records || [];
    }
  } catch (error) {
    console.error("获取服务进度失败:", error);
  } finally {
    serviceLoading.value = false;
  }
};

const checkHealthWarnings = (data) => {
  warnings.value = [];

  if (data.systolicPressure > 140 || data.diastolicPressure > 90) {
    warnings.value.push({
      warningType: "血压预警",
      warningMessage: `血压偏高 (${data.systolicPressure}/${data.diastolicPressure} mmHg)，请注意观察`,
      warningLevel: data.systolicPressure > 160 ? "high" : "medium",
      createTime: new Date().toLocaleString(),
      notified: 0,
    });
  }

  if (data.bloodSugar > 7.0) {
    warnings.value.push({
      warningType: "血糖预警",
      warningMessage: `血糖偏高 (${data.bloodSugar} mmol/L)，建议控制饮食`,
      warningLevel: data.bloodSugar > 10 ? "high" : "medium",
      createTime: new Date().toLocaleString(),
      notified: 0,
    });
  }

  if (data.heartRate > 100 || data.heartRate < 60) {
    warnings.value.push({
      warningType: "心率预警",
      warningMessage: `心率异常 (${data.heartRate} bpm)，请及时关注`,
      warningLevel: data.heartRate > 120 || data.heartRate < 50 ? "high" : "medium",
      createTime: new Date().toLocaleString(),
      notified: 0,
    });
  }

  if (warnings.value.length > 0) {
    hasWarning.value = true;
    warningMessage.value = warnings.value[0].warningMessage;
  } else {
    hasWarning.value = false;
    warningMessage.value = "";
  }
};

const selectElderly = async (id) => {
  selectedElderlyId.value = id;
  await fetchHealthData();
  await fetchServiceProgress();
};

const refreshData = async () => {
  refreshing.value = true;
  try {
    await fetchHealthData();
    await fetchServiceProgress();
    lastUpdateTime.value = new Date().toLocaleString();
    ElMessage.success("数据已刷新");
  } finally {
    refreshing.value = false;
  }
};

const handleWarning = () => {
  if (warnings.value.length > 0) {
    currentWarning.value = warnings.value[0];
    warningDetailVisible.value = true;
  }
};

const cancelServiceOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm("确定要取消该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const response = await cancelOrder(orderId);
    if (response.code === 200) {
      ElMessage.success("订单已取消");
      await fetchServiceProgress();
    } else {
      ElMessage.error(response.message || "取消失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败");
    }
  }
};

const viewServiceDetails = (service) => {
  currentService.value = service;
  serviceDetailVisible.value = true;
};

const goToBooking = () => {
  router.push("/elderly/service-booking");
};

const goToEvaluation = (orderId) => {
  router.push(`/elderly/service-booking?evaluate=${orderId}`);
};

const goToAddRelation = () => {
  router.push("/family/add-relation");
};

const initTrendCharts = () => {
  Object.keys(trendChartRefs.value).forEach((type) => {
    const el = trendChartRefs.value[type];
    if (el) {
      const chart = echarts.init(el);
      updateMiniTrendChart(chart, type);
    }
  });
};

const initHealthTrendChart = () => {
  if (healthTrendChartRef.value) {
    healthTrendChart = echarts.init(healthTrendChartRef.value);
    updateTrendChart();
  }
};

const updateTrendChart = () => {
  if (!healthTrendChart) return;

  const days = parseInt(trendPeriod.value);
  const dates = [];
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    dates.push(date.toLocaleDateString("zh-CN", { month: "short", day: "numeric" }));
  }

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "cross" },
    },
    legend: {
      data: ["收缩压", "舒张压", "血糖", "心率"],
      textStyle: { fontSize: 14 },
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
      data: dates,
      axisLabel: { fontSize: 12 },
    },
    yAxis: [
      {
        type: "value",
        name: "血压/心率",
        axisLabel: { fontSize: 12 },
      },
      {
        type: "value",
        name: "血糖",
        axisLabel: { fontSize: 12 },
      },
    ],
    series: [
      {
        name: "收缩压",
        type: "line",
        data: generateMockData(days, 120, 145),
        lineStyle: { width: 2 },
        itemStyle: { color: "#F56C6C" },
      },
      {
        name: "舒张压",
        type: "line",
        data: generateMockData(days, 75, 95),
        lineStyle: { width: 2 },
        itemStyle: { color: "#E6A23C" },
      },
      {
        name: "血糖",
        type: "line",
        yAxisIndex: 1,
        data: generateMockData(days, 5.0, 7.0, true),
        lineStyle: { width: 2 },
        itemStyle: { color: "#409EFF" },
      },
      {
        name: "心率",
        type: "line",
        data: generateMockData(days, 70, 85),
        lineStyle: { width: 2 },
        itemStyle: { color: "#67C23A" },
      },
    ],
  };

  healthTrendChart.setOption(option);
};

const updateMiniTrendChart = (chart, type) => {
  const config = {
    bloodPressure: { data: generateMockData(7, 120, 145), color: "#F56C6C" },
    bloodSugar: { data: generateMockData(7, 5.0, 7.0, true), color: "#E6A23C" },
    heartRate: { data: generateMockData(7, 70, 85), color: "#409EFF" },
    temperature: { data: generateMockData(7, 36.0, 37.0, true), color: "#67C23A" },
  };

  const { data, color } = config[type] || { data: [], color: "#909399" };

  const option = {
    tooltip: { trigger: "axis" },
    grid: { left: "0%", right: "0%", top: "0%", bottom: "0%", containLabel: false },
    xAxis: { type: "category", show: false, data: ["", "", "", "", "", "", ""] },
    yAxis: { type: "value", show: false },
    series: [
      {
        type: "line",
        data: data,
        smooth: true,
        lineStyle: { width: 2, color: color },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: color + "80" },
            { offset: 1, color: color + "10" },
          ]),
        },
        symbol: "none",
      },
    ],
  };

  chart.setOption(option);
};

const generateMockData = (count, min, max, isFloat = false) => {
  const data = [];
  for (let i = 0; i < count; i++) {
    const value = Math.random() * (max - min) + min;
    data.push(isFloat ? value.toFixed(1) : Math.round(value));
  }
  return data;
};

const getBloodPressureStatus = (systolic, diastolic) => {
  if (!systolic || !diastolic) return "normal";
  if (systolic > 140 || diastolic > 90) return "abnormal";
  if (systolic > 130 || diastolic > 85) return "warning";
  return "normal";
};

const getBloodPressureStatusText = (systolic, diastolic) => {
  if (!systolic || !diastolic) return "未知";
  if (systolic > 160 || diastolic > 100) return "偏高";
  if (systolic > 140 || diastolic > 90) return "略高";
  if (systolic > 130 || diastolic > 85) return "正常偏高";
  return "正常";
};

const getBloodSugarStatus = (value) => {
  if (!value) return "normal";
  if (value > 7.0) return "abnormal";
  if (value > 6.1) return "warning";
  return "normal";
};

const getBloodSugarStatusText = (value) => {
  if (!value) return "未知";
  if (value > 10.0) return "偏高";
  if (value > 7.0) return "略高";
  if (value > 6.1) return "正常偏高";
  return "正常";
};

const getHeartRateStatus = (value) => {
  if (!value) return "normal";
  if (value > 100 || value < 60) return "abnormal";
  if (value > 90 || value < 65) return "warning";
  return "normal";
};

const getHeartRateStatusText = (value) => {
  if (!value) return "未知";
  if (value > 120) return "过快";
  if (value > 100) return "略快";
  if (value < 50) return "过慢";
  if (value < 60) return "略慢";
  return "正常";
};

const getTemperatureStatus = (value) => {
  if (!value) return "normal";
  if (value > 37.5 || value < 36.0) return "abnormal";
  if (value > 37.3 || value < 36.2) return "warning";
  return "normal";
};

const getTemperatureStatusText = (value) => {
  if (!value) return "未知";
  if (value > 38.0) return "发热";
  if (value > 37.3) return "低热";
  if (value < 36.0) return "偏低";
  return "正常";
};

const getStatusType = (status) => {
  const statusMap = {
    pending: "warning",
    confirmed: "primary",
    in_progress: "primary",
    completed: "success",
    cancelled: "danger",
  };
  return statusMap[status] || "info";
};

const getStatusText = (status) => {
  const statusMap = {
    pending: "待确认",
    confirmed: "已确认",
    in_progress: "进行中",
    completed: "已完成",
    cancelled: "已取消",
  };
  return statusMap[status] || status;
};

const getWarningType = (level) => {
  const typeMap = {
    high: "error",
    medium: "warning",
    low: "info",
  };
  return typeMap[level] || "warning";
};

const getWarningTagType = (level) => {
  const typeMap = {
    high: "danger",
    medium: "warning",
    low: "info",
  };
  return typeMap[level] || "info";
};

const getWarningLevelText = (level) => {
  const textMap = {
    high: "紧急",
    medium: "重要",
    low: "一般",
  };
  return textMap[level] || "一般";
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return "--";
  return new Date(dateTime).toLocaleString("zh-CN");
};

const startAutoRefresh = () => {
  refreshInterval = setInterval(() => {
    refreshData();
  }, 60000);
};

const stopAutoRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
    refreshInterval = null;
  }
};

onMounted(() => {
  fetchFamilyElderly();
  initHealthTrendChart();
  startAutoRefresh();
  lastUpdateTime.value = new Date().toLocaleString();
});

onUnmounted(() => {
  stopAutoRefresh();
  if (healthTrendChart) {
    healthTrendChart.dispose();
  }
});

window.addEventListener("resize", () => {
  if (healthTrendChart) {
    healthTrendChart.resize();
  }
});
</script>

<style lang="scss" scoped>
.family-monitoring {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.warning-section {
  margin-bottom: 30px;
}

.warning-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  background-color: #fef0f0;
  border: 2px solid #fbc4c4;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
  animation: warningFlash 2s infinite;
}

.warning-icon {
  color: #f56c6c;
  flex-shrink: 0;
}

.warning-info {
  flex: 1;
}

.warning-title {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
  margin: 0 0 10px;
}

.warning-message {
  font-size: 16px;
  color: #606266;
  margin: 0;
  line-height: 1.4;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.last-update {
  font-size: 14px;
  color: #909399;
}

.elderly-selector {
  margin-bottom: 30px;
  max-width: 300px;
}

.health-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.health-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  }

  &.abnormal {
    border-left: 4px solid #f56c6c;
  }
}

.health-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.health-info {
  flex: 1;
}

.health-label {
  font-size: 16px;
  color: #606266;
  margin: 0 0 10px;
}

.health-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px;
}

.health-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 5px;

  &.normal {
    background-color: #f0f9eb;
    color: #67c23a;
  }

  &.warning {
    background-color: #fdf6ec;
    color: #e6a23c;
  }

  &.abnormal {
    background-color: #fef0f0;
    color: #f56c6c;
  }
}

.health-time {
  font-size: 12px;
  color: #909399;
}

.health-trend {
  width: 120px;
  height: 80px;
}

.health-trend-section {
  margin-bottom: 40px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.trend-chart-large {
  width: 100%;
  height: 400px;
}

.service-tracking {
  margin-bottom: 20px;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.service-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  }

  &.urgent {
    border-left: 4px solid #f56c6c;
    box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
  }
}

.service-content {
  padding: 20px;
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.service-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.service-info {
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.info-label {
  font-weight: 500;
  color: #606266;
  min-width: 100px;
  margin-right: 10px;
}

.info-value {
  color: #303133;
}

.service-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.empty-state {
  padding: 40px 0;
}

.warning-detail {
  .warning-info {
    margin-top: 20px;
  }

  .warning-advice {
    margin-top: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 8px;

    h4 {
      margin: 0 0 10px;
      color: #303133;
    }

    ul {
      margin: 0;
      padding-left: 20px;

      li {
        margin-bottom: 5px;
        color: #606266;
      }
    }
  }
}

@keyframes warningFlash {
  0% {
    box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
  }
  50% {
    box-shadow: 0 6px 20px rgba(245, 108, 108, 0.4);
  }
  100% {
    box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
  }
}

@media (max-width: 768px) {
  .family-monitoring {
    padding: 20px;
  }

  .warning-content {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .health-card-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }

  .health-cards {
    grid-template-columns: 1fr;
  }

  .trend-chart-large {
    height: 300px;
  }

  .service-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
