<template>
  <div class="emergency-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>紧急求助</span>
              <el-button type="danger" @click="handleAdd">
                <el-icon><Warning /></el-icon>
                发起求助
              </el-button>
            </div>
          </template>

          <el-row :gutter="20" class="statistics-row">
            <el-col :span="6">
              <el-statistic title="总求助数" :value="statistics.total" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="待处理" :value="statistics.pending">
                <template #suffix>
                  <el-icon color="#F56C6C"><WarningFilled /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="处理中" :value="statistics.handling">
                <template #suffix>
                  <el-icon color="#E6A23C"><Loading /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="已完成" :value="statistics.completed">
                <template #suffix>
                  <el-icon color="#67C23A"><CircleCheckFilled /></el-icon>
                </template>
              </el-statistic>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>求助趋势</span>
          </template>
          <div ref="trendChartRef" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>求助类型分布</span>
          </template>
          <div ref="pieChartRef" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>求助记录列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="老人姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="求助类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择类型"
            clearable
          >
            <el-option label="医疗急救" value="medical" />
            <el-option label="生活求助" value="life" />
            <el-option label="安全报警" value="security" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="handling" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="求助时间">
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
          prop="elderlyName"
          label="老人姓名"
          width="100"
          align="center"
        />
        <el-table-column
          prop="type"
          label="求助类型"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="求助描述"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="status"
          label="处理状态"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="handlerName"
          label="处理人"
          width="100"
          align="center"
        />
        <el-table-column
          prop="emergencyTime"
          label="求助时间"
          width="160"
          align="center"
        />
        <el-table-column
          prop="handleTime"
          label="处理时间"
          width="160"
          align="center"
        />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending' || row.status === 'handling'"
              type="success"
              link
              @click="handleProcess(row)"
            >
              <el-icon><Check /></el-icon>
              处理
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
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
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="老人姓名" prop="elderlyId">
          <el-select
            v-model="form.elderlyId"
            placeholder="请选择老人"
            style="width: 100%"
          >
            <el-option
              v-for="item in elderlyList"
              :key="item.id"
              :label="item.realName || item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="求助类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="medical">医疗急救</el-radio>
            <el-radio value="life">生活求助</el-radio>
            <el-radio value="security">安全报警</el-radio>
            <el-radio value="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgency">
          <el-radio-group v-model="form.urgency">
            <el-radio value="high">紧急</el-radio>
            <el-radio value="medium">一般</el-radio>
            <el-radio value="low">不紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="求助描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述求助内容"
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="位置信息" prop="location">
          <el-input v-model="form.location" placeholder="请输入当前位置" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit"
          >确定</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="求助详情" width="700px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="老人姓名">{{
          currentRow.elderlyName
        }}</el-descriptions-item>
        <el-descriptions-item label="求助类型">
          <el-tag :type="getTypeTagType(currentRow.type)">
            {{ getTypeLabel(currentRow.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyTagType(currentRow.urgency)">
            {{ getUrgencyLabel(currentRow.urgency) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusTagType(currentRow.status)">
            {{ getStatusLabel(currentRow.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="求助描述" :span="2">{{
          currentRow.description
        }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{
          currentRow.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="位置信息">{{
          currentRow.location
        }}</el-descriptions-item>
        <el-descriptions-item label="求助时间">{{
          currentRow.emergencyTime
        }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{
          currentRow.handleTime || "未处理"
        }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{
          currentRow.handlerName || "未分配"
        }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{
          currentRow.handleResult || "未处理"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="processDialogVisible" title="处理求助" width="600px">
      <el-form
        ref="processFormRef"
        :model="processForm"
        :rules="processRules"
        label-width="120px"
      >
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio value="handling">处理中</el-radio>
            <el-radio value="completed">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input
            v-model="processForm.handleResult"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="processLoading"
          @click="handleProcessSubmit"
          >确定</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as echarts from "echarts";
import {
  getEmergencyList,
  createEmergency,
  updateEmergency,
  deleteEmergency,
  handleEmergency,
  getEmergencyStatistics,
  getEmergencyTrend,
} from "@/api/emergency";
import { getElderlyList } from "@/api/elderly";
import {
  Warning,
  WarningFilled,
  Loading,
  CircleCheckFilled,
  Search,
  Refresh,
  View,
  Check,
  Edit,
  Delete,
} from "@element-plus/icons-vue";

const loading = ref(false);
const submitLoading = ref(false);
const processLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const processDialogVisible = ref(false);
const dialogTitle = ref("");
const currentRow = ref(null);
const formRef = ref(null);
const processFormRef = ref(null);
const trendChartRef = ref(null);
const pieChartRef = ref(null);
let trendChart = null;
let pieChart = null;

const searchForm = reactive({
  name: "",
  type: "",
  status: "",
  dateRange: [],
});

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const tableData = ref([]);
const elderlyList = ref([]);

const statistics = reactive({
  total: 0,
  pending: 0,
  handling: 0,
  completed: 0,
});

const form = reactive({
  id: null,
  elderlyId: null,
  type: "medical",
  urgency: "high",
  description: "",
  phone: "",
  location: "",
});

const processForm = reactive({
  id: null,
  status: "handling",
  handleResult: "",
});

const rules = {
  elderlyId: [{ required: true, message: "请选择老人", trigger: "change" }],
  type: [{ required: true, message: "请选择求助类型", trigger: "change" }],
  urgency: [{ required: true, message: "请选择紧急程度", trigger: "change" }],
  description: [{ required: true, message: "请输入求助描述", trigger: "blur" }],
  phone: [{ required: true, message: "请输入联系电话", trigger: "blur" }],
  location: [{ required: true, message: "请输入位置信息", trigger: "blur" }],
};

const processRules = {
  status: [{ required: true, message: "请选择处理状态", trigger: "change" }],
  handleResult: [
    { required: true, message: "请输入处理结果", trigger: "blur" },
  ],
};

const getTypeLabel = (type) => {
  const map = {
    medical: "医疗急救",
    life: "生活求助",
    security: "安全报警",
    other: "其他",
  };
  return map[type] || type;
};

const getTypeTagType = (type) => {
  const map = {
    medical: "danger",
    life: "warning",
    security: "danger",
    other: "info",
  };
  return map[type] || "info";
};

const getStatusLabel = (status) => {
  const map = {
    pending: "待处理",
    handling: "处理中",
    completed: "已完成",
  };
  return map[status] || status;
};

const getStatusTagType = (status) => {
  const map = {
    pending: "danger",
    handling: "warning",
    completed: "success",
  };
  return map[status] || "info";
};

const getUrgencyLabel = (urgency) => {
  const map = {
    high: "紧急",
    medium: "一般",
    low: "不紧急",
  };
  return map[urgency] || urgency;
};

const getUrgencyTagType = (urgency) => {
  const map = {
    high: "danger",
    medium: "warning",
    low: "info",
  };
  return map[urgency] || "info";
};

const fetchList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm,
    };
    const res = await getEmergencyList(params);
    // 处理数据，映射字段名
    tableData.value = (res.data.records || []).map(item => {
      return {
        ...item,
        // 后端返回的字段名映射到前端显示的字段名
        elderlyName: item.elderlyName || '-',
        type: item.helpType || item.type || '-',
        status: item.helpStatus || item.status || '-',
        emergencyTime: item.createTime || item.emergencyTime || '-',
        handleTime: item.handleTime || '-',
        handlerName: item.handlerName || '-',
        handleResult: item.handleResult || '-'
      };
    });
    pagination.total = res.data.total || 0;
  } catch (error) {
    ElMessage.error("获取求助记录列表失败");
  } finally {
    loading.value = false;
  }
};

const fetchStatistics = async () => {
  try {
    const res = await getEmergencyStatistics();
    if (res && res.data) {
      Object.assign(statistics, {
        total: res.data.total || 0,
        pending: res.data.pending || 0,
        handling: res.data.handling || 0,
        completed: res.data.completed || 0,
      });
    }
  } catch (error) {
    console.error("获取统计数据失败", error);
    // 保持默认值
    Object.assign(statistics, {
      total: 0,
      pending: 0,
      handling: 0,
      completed: 0,
    });
  }
};

const fetchElderlyList = async () => {
  try {
    const res = await getElderlyList({ page: 1, size: 1000 });
    elderlyList.value = res.data.records || [];
  } catch (error) {
    console.error("获取老人列表失败", error);
  }
};

const initTrendChart = async () => {
  await nextTick();
  if (!trendChartRef.value) return;
  trendChart = echarts.init(trendChartRef.value);

  try {
    const res = await getEmergencyTrend({ days: 7 });
    const data = res.data || {};
    const dates = data.dates || [];
    const counts = data.counts || [];

    const option = {
      tooltip: {
        trigger: "axis",
      },
      xAxis: {
        type: "category",
        data: dates,
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          name: "求助数量",
          type: "line",
          data: counts,
          smooth: true,
          areaStyle: {
            color: {
              type: "linear",
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: "rgba(245, 108, 108, 0.3)" },
                { offset: 1, color: "rgba(245, 108, 108, 0.05)" },
              ],
            },
          },
        },
      ],
    };
    trendChart.setOption(option);
  } catch (error) {
    console.error("获取趋势数据失败", error);
  }
};

const initPieChart = async () => {
  await nextTick();
  if (!pieChartRef.value) return;
  pieChart = echarts.init(pieChartRef.value);

  const typeData = [
    {
      value: tableData.value.filter((item) => item.type === "medical").length,
      name: "医疗急救",
    },
    {
      value: tableData.value.filter((item) => item.type === "life").length,
      name: "生活求助",
    },
    {
      value: tableData.value.filter((item) => item.type === "security").length,
      name: "安全报警",
    },
    {
      value: tableData.value.filter((item) => item.type === "other").length,
      name: "其他",
    },
  ];

  const option = {
    tooltip: {
      trigger: "item",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "求助类型",
        type: "pie",
        radius: "50%",
        data: typeData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
  pieChart.setOption(option);
};

const handleSearch = () => {
  pagination.page = 1;
  fetchList();
};

const handleReset = () => {
  searchForm.name = "";
  searchForm.type = "";
  searchForm.status = "";
  searchForm.dateRange = [];
  pagination.page = 1;
  fetchList();
};

const handleSizeChange = (size) => {
  pagination.size = size;
  fetchList();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  fetchList();
};

const handleAdd = () => {
  dialogTitle.value = "发起求助";
  Object.assign(form, {
    id: null,
    elderlyId: null,
    type: "medical",
    urgency: "high",
    description: "",
    phone: "",
    location: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  dialogTitle.value = "编辑求助";
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handleView = (row) => {
  currentRow.value = row;
  viewDialogVisible.value = true;
};

const handleProcess = (row) => {
  processForm.id = row.id;
  processForm.status = row.status === "pending" ? "handling" : "completed";
  processForm.handleResult = row.handleResult || "";
  processDialogVisible.value = true;
};

const handleProcessSubmit = async () => {
  if (!processFormRef.value) return;
  await processFormRef.value.validate(async (valid) => {
    if (valid) {
      processLoading.value = true;
      try {
        await handleEmergency(processForm.id, processForm);
        ElMessage.success("处理成功");
        processDialogVisible.value = false;
        fetchList();
        fetchStatistics();
        initPieChart();
      } catch (error) {
        ElMessage.error("处理失败");
      } finally {
        processLoading.value = false;
      }
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该求助记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteEmergency(row.id);
        ElMessage.success("删除成功");
        fetchList();
        fetchStatistics();
        initPieChart();
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {});
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        if (form.id) {
          await updateEmergency(form.id, form);
          ElMessage.success("更新成功");
        } else {
          await createEmergency(form);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        fetchList();
        fetchStatistics();
        initPieChart();
      } catch (error) {
        ElMessage.error(form.id ? "更新失败" : "创建失败");
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

onMounted(async () => {
  // 先获取老人列表，再获取求助记录
  await fetchElderlyList();
  fetchList();
  fetchStatistics();
  initTrendChart();
  initPieChart();
});
</script>

<style lang="scss" scoped>
.emergency-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .statistics-row {
    margin-bottom: 0;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
