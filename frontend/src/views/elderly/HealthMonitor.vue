<template>
  <div class="elderly-health-monitor">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2 class="page-title">我的健康数据</h2>
          <el-button type="primary" size="large" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </div>
      </template>

      <el-row :gutter="20" class="statistics-row">
        <el-col :span="6">
          <el-statistic title="总记录数" :value="statistics.total" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="正常记录" :value="statistics.normal">
            <template #suffix>
              <el-icon color="#67C23A"><CircleCheck /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="异常记录" :value="statistics.abnormal">
            <template #suffix>
              <el-icon color="#E6A23C"><Warning /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="今日记录" :value="statistics.today" />
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>健康数据趋势</span>
          <div style="display: flex; gap: 10px; align-items: center;">
            <el-date-picker
              v-model="trendDateRange"
              type="daterange"
              size="small"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 220px"
              @change="initTrendChart"
            />
            <el-button type="primary" size="small" @click="initTrendChart">查询</el-button>
          </div>
        </div>
      </template>
      <div ref="trendChartRef" style="width: 100%; height: 350px"></div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>健康记录列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="健康状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="正常" value="normal" />
            <el-option label="异常" value="abnormal" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column
          prop="monitorTime"
          label="监测时间"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            {{ formatTime(row.monitorTime) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="heartRate"
          label="心率(次/分)"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getHeartRateType(row.heartRate)">
              {{ row.heartRate }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="systolicPressure"
          label="收缩压(mmHg)"
          width="120"
          align="center"
        />
        <el-table-column
          prop="diastolicPressure"
          label="舒张压(mmHg)"
          width="120"
          align="center"
        />
        <el-table-column
          prop="bloodSugar"
          label="血糖(mmol/L)"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getBloodSugarType(row.bloodSugar)">
              {{ row.bloodSugar }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="temperature"
          label="体温(℃)"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getTemperatureType(row.temperature)">
              {{ row.temperature }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="weight"
          label="体重(kg)"
          width="100"
          align="center"
        />
        <el-table-column
          prop="healthStatus"
          label="健康状态"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="row.healthStatus === 'abnormal' ? 'danger' : 'success'">
              {{ row.healthStatus === 'abnormal' ? '异常' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="备注"
          min-width="150"
          align="center"
        />
        <el-table-column
          label="操作"
          width="120"
          align="center"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="pagination.page"
        :page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog
      v-model="viewDialogVisible"
      title="健康数据详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="监测时间">
          {{ formatTime(currentRow.monitorTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="心率">
          {{ currentRow.heartRate }} 次/分
        </el-descriptions-item>
        <el-descriptions-item label="收缩压">
          {{ currentRow.systolicPressure }} mmHg
        </el-descriptions-item>
        <el-descriptions-item label="舒张压">
          {{ currentRow.diastolicPressure }} mmHg
        </el-descriptions-item>
        <el-descriptions-item label="血糖">
          {{ currentRow.bloodSugar }} mmol/L
        </el-descriptions-item>
        <el-descriptions-item label="体温">
          {{ currentRow.temperature }} ℃
        </el-descriptions-item>
        <el-descriptions-item label="体重">
          {{ currentRow.weight }} kg
        </el-descriptions-item>
        <el-descriptions-item label="健康状态">
          <el-tag :type="currentRow.healthStatus === 'abnormal' ? 'danger' : 'success'">
            {{ currentRow.healthStatus === 'abnormal' ? '异常' : '正常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentRow.description }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="心率" prop="heartRate">
          <el-input-number
            v-model="form.heartRate"
            :min="0"
            :max="200"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="收缩压" prop="systolicPressure">
          <el-input-number
            v-model="form.systolicPressure"
            :min="0"
            :max="300"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="舒张压" prop="diastolicPressure">
          <el-input-number
            v-model="form.diastolicPressure"
            :min="0"
            :max="200"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="血糖" prop="bloodSugar">
          <el-input-number
            v-model="form.bloodSugar"
            :min="0"
            :max="30"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="体温" prop="temperature">
          <el-input-number
            v-model="form.temperature"
            :min="0"
            :max="50"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="体重" prop="weight">
          <el-input-number
            v-model="form.weight"
            :min="0"
            :max="300"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as echarts from "echarts";
import {
  Plus,
  Search,
  Refresh,
  CircleCheck,
  Warning,
} from "@element-plus/icons-vue";
import { queryHealthData, inputHealthData } from "@/api/elderly/health";

defineOptions({
  name: 'ElderlyHealthMonitor'
});

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const dialogTitle = ref("");
const currentRow = ref(null);
const formRef = ref(null);
const trendChartRef = ref(null);
let trendChart = null;

const searchForm = reactive({
  status: "",
  dateRange: [],
});

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const trendDateRange = ref([]);

const tableData = ref([]);
const statistics = reactive({
  total: 0,
  normal: 0,
  abnormal: 0,
  today: 0,
});

const form = reactive({
  heartRate: 0,
  systolicPressure: 0,
  diastolicPressure: 0,
  bloodSugar: 0,
  temperature: 0,
  weight: 0,
  description: "",
});

const rules = {
  heartRate: [{ required: true, message: "请输入心率", trigger: "blur" }],
  systolicPressure: [{ required: true, message: "请输入收缩压", trigger: "blur" }],
  diastolicPressure: [{ required: true, message: "请输入舒张压", trigger: "blur" }],
  bloodSugar: [{ required: true, message: "请输入血糖", trigger: "blur" }],
  temperature: [{ required: true, message: "请输入体温", trigger: "blur" }],
  weight: [{ required: true, message: "请输入体重", trigger: "blur" }],
};

const getHeartRateType = (value) => {
  if (value === null || value === undefined) return "info";
  return value > 80 ? "danger" : "success";
};

const getBloodSugarType = (value) => {
  if (value === null || value === undefined) return "info";
  return value > 6.1 ? "danger" : "success";
};

const getTemperatureType = (value) => {
  if (value === null || value === undefined) return "info";
  return value > 37.5 ? "danger" : value < 36 ? "warning" : "success";
};

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const fetchList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
    };

    if (searchForm.status) {
      params.healthStatus = searchForm.status;
    }

    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startTime = searchForm.dateRange[0];
      params.endTime = searchForm.dateRange[1];
    }

    const res = await queryHealthData(params);
    if (res.data) {
      tableData.value = res.data.list || [];
      pagination.total = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取健康数据失败", error);
    ElMessage.error("获取健康数据失败");
  } finally {
    loading.value = false;
  }
};

const fetchStatistics = async () => {
  try {
    const res = await queryHealthData({ pageNum: 1, pageSize: 1000 });
    if (res.data && res.data.list) {
      const list = res.data.list;
      statistics.total = list.length;
      statistics.normal = list.filter(item => item.healthStatus === 'normal').length;
      statistics.abnormal = list.filter(item => item.healthStatus === 'abnormal').length;
      
      const today = new Date().toDateString();
      statistics.today = list.filter(item => {
        const itemDate = new Date(item.monitorTime).toDateString();
        return itemDate === today;
      }).length;
    }
  } catch (error) {
    console.error("获取统计数据失败", error);
  }
};

const initTrendChart = async () => {
  try {
    let params = {};
    if (trendDateRange.value && trendDateRange.value.length === 2) {
      params.startTime = trendDateRange.value[0];
      params.endTime = trendDateRange.value[1];
    }

    const res = await queryHealthData({ ...params, pageNum: 1, pageSize: 1000 });
    if (res.data && res.data.list) {
      const list = res.data.list;
      const dates = list.map(item => {
        const date = new Date(item.monitorTime);
        return `${date.getMonth() + 1}-${date.getDate()}`;
      });
      const heartRates = list.map(item => item.heartRate);
      const bloodPressures = list.map(item => item.systolicPressure);
      const bloodSugars = list.map(item => item.bloodSugar);

      if (trendChartRef.value) {
        if (!trendChart) {
          trendChart = echarts.init(trendChartRef.value);
        }

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
            data: ["心率", "收缩压", "血糖"],
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
            data: dates,
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
              name: "心率",
              type: "line",
              data: heartRates,
              smooth: true,
              itemStyle: {
                color: "#409EFF",
              },
            },
            {
              name: "收缩压",
              type: "line",
              data: bloodPressures,
              smooth: true,
              itemStyle: {
                color: "#67C23A",
              },
            },
            {
              name: "血糖",
              type: "line",
              data: bloodSugars,
              smooth: true,
              itemStyle: {
                color: "#E6A23C",
              },
            },
          ],
        };

        trendChart.setOption(option);
      }
    }
  } catch (error) {
    console.error("初始化趋势图失败", error);
    ElMessage.error("初始化趋势图失败");
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchList();
};

const handleReset = () => {
  searchForm.status = "";
  searchForm.dateRange = [];
  pagination.page = 1;
  fetchList();
};

const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  fetchList();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  fetchList();
};

const handleAdd = () => {
  dialogTitle.value = "新增健康数据";
  form.heartRate = 0;
  form.systolicPressure = 0;
  form.diastolicPressure = 0;
  form.bloodSugar = 0;
  form.temperature = 0;
  form.weight = 0;
  form.description = "";
  dialogVisible.value = true;
};

const handleView = (row) => {
  currentRow.value = row;
  viewDialogVisible.value = true;
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除这条记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("删除成功");
      fetchList();
      fetchStatistics();
    })
    .catch(() => {});
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    const data = {
      heartRate: form.heartRate,
      bloodPressureSystolic: form.systolicPressure,
      bloodPressureDiastolic: form.diastolicPressure,
      bloodSugar: form.bloodSugar,
      bodyTemperature: form.temperature,
      weight: form.weight,
      note: form.description,
    };

    await inputHealthData(data);
    ElMessage.success("添加成功");
    dialogVisible.value = false;
    fetchList();
    fetchStatistics();
  } catch (error) {
    console.error("提交失败", error);
    ElMessage.error("提交失败");
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  fetchList();
  fetchStatistics();
  nextTick(() => {
    initTrendChart();
  });
});

window.addEventListener("resize", () => {
  if (trendChart) {
    trendChart.resize();
  }
});
</script>

<style lang="scss" scoped>
.elderly-health-monitor {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.statistics-row {
  margin-bottom: 0;
}

.search-form {
  margin-bottom: 20px;
}
</style>