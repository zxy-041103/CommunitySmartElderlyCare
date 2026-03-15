<template>
  <div class="health-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>健康监测</span>
              <el-button type="primary" @click="handleAdd">
                <el-icon><Plus /></el-icon>
                新增记录
              </el-button>
            </div>
          </template>

          <el-row :gutter="20" class="statistics-row">
            <el-col :span="6">
              <el-statistic title="监测次数" :value="statistics.total" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="正常次数" :value="statistics.normal">
                <template #suffix>
                  <el-icon color="#67C23A"><CircleCheck /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="异常次数" :value="statistics.abnormal">
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
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>健康数据趋势</span>
              <div style="display: flex; gap: 10px; align-items: center;">
                <el-radio-group v-model="trendQueryType" size="small" @change="handleTrendQueryTypeChange">
                  <el-radio-button label="all">显示所有</el-radio-button>
                  <el-radio-button label="single">指定老人</el-radio-button>
                </el-radio-group>
                <el-select 
                  v-if="trendQueryType === 'single'" 
                  v-model="trendSelectedElderly" 
                  size="small" 
                  style="width: 120px"
                  placeholder="选择老人"
                  @change="initTrendChart"
                >
                  <el-option 
                    v-for="item in elderlyList" 
                    :key="item.id" 
                    :label="item.realName || item.name" 
                    :value="item.id" 
                  />
                </el-select>
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
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>健康状态分布</span>
          </template>
          <div ref="pieChartRef" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>健康记录列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="老人姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
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
          prop="elderlyName"
          label="老人姓名"
          width="100"
          align="center"
        />
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
          prop="bloodPressure"
          label="血压(mmHg)"
          width="130"
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
          prop="status"
          label="健康状态"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
              {{ row.status === "normal" ? "正常" : "异常" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="recordTime"
          label="记录时间"
          width="160"
          align="center"
        />
        <el-table-column
          prop="remark"
          label="备注"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
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
        <el-form-item label="心率(次/分)" prop="heartRate">
          <el-input-number v-model="form.heartRate" :min="0" :max="200" />
        </el-form-item>
        <el-form-item label="收缩压(mmHg)" prop="systolicPressure">
          <el-input-number
            v-model="form.systolicPressure"
            :min="0"
            :max="300"
          />
        </el-form-item>
        <el-form-item label="舒张压(mmHg)" prop="diastolicPressure">
          <el-input-number
            v-model="form.diastolicPressure"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="血糖(mmol/L)" prop="bloodSugar">
          <el-input-number
            v-model="form.bloodSugar"
            :min="0"
            :max="30"
            :step="0.1"
          />
        </el-form-item>
        <el-form-item label="体温(℃)" prop="temperature">
          <el-input-number
            v-model="form.temperature"
            :min="30"
            :max="45"
            :step="0.1"
          />
        </el-form-item>
        <el-form-item label="体重(kg)" prop="weight">
          <el-input-number
            v-model="form.weight"
            :min="0"
            :max="200"
            :step="0.1"
          />
        </el-form-item>
        <el-form-item label="健康状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="normal">正常</el-radio>
            <el-radio value="abnormal">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit"
          >确定</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="健康记录详情" width="700px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="老人姓名">{{
          currentRow.elderlyName
        }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{
          currentRow.recordTime
        }}</el-descriptions-item>
        <el-descriptions-item label="心率(次/分)">
          <el-tag :type="getHeartRateType(currentRow.heartRate)">
            {{ currentRow.heartRate }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="血压(mmHg)">{{
          currentRow.bloodPressure
        }}</el-descriptions-item>
        <el-descriptions-item label="血糖(mmol/L)">
          <el-tag :type="getBloodSugarType(currentRow.bloodSugar)">
            {{ currentRow.bloodSugar }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="体温(℃)">
          <el-tag :type="getTemperatureType(currentRow.temperature)">
            {{ currentRow.temperature }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="体重(kg)">{{
          currentRow.weight
        }}</el-descriptions-item>
        <el-descriptions-item label="健康状态">
          <el-tag :type="currentRow.status === 'normal' ? 'success' : 'danger'">
            {{ currentRow.status === "normal" ? "正常" : "异常" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentRow.remark || "无"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as echarts from "echarts";
import {
  getHealthList,
  createHealth,
  updateHealth,
  deleteHealth,
  getHealthStatistics,
  getHealthTrend,
  getAllHealthTrend,
} from "@/api/health";
import { getElderlyList } from "@/api/elderly";
import {
  Plus,
  Search,
  Refresh,
  CircleCheck,
  Warning,
  View,
  Edit,
  Delete,
} from "@element-plus/icons-vue";

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const dialogTitle = ref("");
const currentRow = ref(null);
const formRef = ref(null);
const trendChartRef = ref(null);
const pieChartRef = ref(null);
let trendChart = null;
let pieChart = null;

const searchForm = reactive({
  name: "",
  status: "",
  dateRange: [],
});

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

// 趋势图表查询参数
const trendQueryType = ref('all'); // 'all' 或 'single'
const trendSelectedElderly = ref('');
const trendDateRange = ref([]);

// 处理趋势查询类型切换
const handleTrendQueryTypeChange = () => {
  if (trendQueryType.value === 'single' && elderlyList.value.length > 0) {
    trendSelectedElderly.value = elderlyList.value[0].id;
  }
  initTrendChart();
};

const tableData = ref([]);
const elderlyList = ref([]);

const statistics = reactive({
  total: 0,
  normal: 0,
  abnormal: 0,
  today: 0,
});

const form = reactive({
  id: null,
  elderlyId: null,
  heartRate: 75,
  systolicPressure: 120,
  diastolicPressure: 80,
  bloodSugar: 5.5,
  temperature: 36.5,
  weight: 65,
  status: "normal",
  remark: "",
});

const rules = {
  elderlyId: [{ required: true, message: "请选择老人", trigger: "change" }],
  heartRate: [{ required: true, message: "请输入心率", trigger: "blur" }],
  systolicPressure: [
    { required: true, message: "请输入收缩压", trigger: "blur" },
  ],
  diastolicPressure: [
    { required: true, message: "请输入舒张压", trigger: "blur" },
  ],
  bloodSugar: [{ required: true, message: "请输入血糖", trigger: "blur" }],
  temperature: [{ required: true, message: "请输入体温", trigger: "blur" }],
  weight: [{ required: true, message: "请输入体重", trigger: "blur" }],
  status: [{ required: true, message: "请选择健康状态", trigger: "change" }],
};

const getHeartRateType = (value) => {
  if (value < 60 || value > 100) return "warning";
  return "success";
};

const getBloodSugarType = (value) => {
  if (value < 3.9 || value > 6.1) return "warning";
  return "success";
};

const getTemperatureType = (value) => {
  if (value < 36 || value > 37.3) return "warning";
  return "success";
};

const fetchList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      name: searchForm.name,
      status: searchForm.status,
      startDate: searchForm.dateRange && searchForm.dateRange.length > 0 ? searchForm.dateRange[0] : null,
      endDate: searchForm.dateRange && searchForm.dateRange.length > 1 ? searchForm.dateRange[1] : null
    };
    console.log('健康记录列表请求参数:', params);
    const res = await getHealthList(params);
    console.log('健康记录列表API响应:', res);
    console.log('健康记录列表数据:', res.data);
    console.log('健康记录列表:', res.data?.list);
    console.log('健康记录总数:', res.data?.total);
    // 处理数据，确保字段存在且格式正确
    const list = res.data?.list || [];
    console.log('原始列表数据:', list);
    tableData.value = list.map(item => {
      console.log('处理单项数据:', item);
      return {
        ...item,
        id: item.id,
        elderlyName: item.elderlyName || '-',
        bloodPressure: item.systolicPressure && item.diastolicPressure 
          ? `${item.systolicPressure}/${item.diastolicPressure}` 
          : '-',
        heartRate: item.heartRate !== null && item.heartRate !== undefined ? item.heartRate : '-',
        bloodSugar: item.bloodSugar !== null && item.bloodSugar !== undefined ? item.bloodSugar : '-',
        temperature: item.temperature !== null && item.temperature !== undefined ? item.temperature : '-',
        weight: item.weight !== null && item.weight !== undefined ? item.weight : '-',
        recordTime: item.monitorTime || item.createTime || '-',
        status: item.healthStatus || item.status || '-',
        remark: item.description || '-'
      };
    });
    console.log('处理后的表格数据:', tableData.value);
    pagination.total = res.data?.total || 0;
  } catch (error) {
    console.error('获取健康记录列表失败:', error);
    ElMessage.error("获取健康记录列表失败");
  } finally {
    loading.value = false;
  }
};

const fetchStatistics = async () => {
  try {
    const res = await getHealthStatistics();
    const data = res.data || {};
    statistics.total = data.totalCount || 0;
    statistics.normal = data.normalCount || 0;
    statistics.abnormal = data.abnormalCount || 0;
    statistics.today = data.todayCount || 0;
  } catch (error) {
    console.error("获取统计数据失败", error);
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
  
  // 如果图表已存在，先销毁
  if (trendChart) {
    trendChart.dispose();
  }
  trendChart = echarts.init(trendChartRef.value);

  try {
    let res;
    const params = {};
    
    // 处理日期范围参数
    if (trendDateRange.value && trendDateRange.value.length === 2) {
      params.startDate = trendDateRange.value[0];
      params.endDate = trendDateRange.value[1];
    } else {
      // 默认查询最近30天
      params.days = 30;
    }
    
    // 根据查询类型调用不同API
    if (trendQueryType.value === 'all') {
      // 查询所有用户的数据
      res = await getAllHealthTrend(params);
    } else {
      // 查询指定用户的数据
      let targetUserId = trendSelectedElderly.value;
      if (!targetUserId && elderlyList.value.length > 0) {
        targetUserId = elderlyList.value[0].id;
        trendSelectedElderly.value = targetUserId;
      }
      
      if (!targetUserId) {
        // 没有用户ID，显示空图表
        const option = {
          title: { text: '暂无健康数据', left: 'center', top: 'center', textStyle: { color: '#909399' } },
          xAxis: { type: 'category', data: [], axisLine: { lineStyle: { color: '#dcdfe6' } } },
          yAxis: { type: 'value', axisLine: { lineStyle: { color: '#dcdfe6' } }, splitLine: { lineStyle: { color: '#e4e7ed' } } },
          series: []
        };
        trendChart.setOption(option);
        return;
      }
      
      res = await getHealthTrend(targetUserId, params);
    }
    
    const data = res.data || {};
    
    // 后端返回的数据格式：{ dates: [], heartRates: [], systolicPressures: [], diastolicPressures: [], bloodSugars: [] }
    const dates = data.dates || [];
    const heartRates = data.heartRates || [];
    const systolicPressures = data.systolicPressures || [];
    const bloodSugars = data.bloodSugars || [];

    // 如果没有数据，显示提示
    if (dates.length === 0) {
      const option = {
        title: { 
          text: '该时间段暂无健康数据', 
          left: 'center', 
          top: 'center',
          textStyle: { color: '#909399', fontSize: 14 }
        },
        xAxis: { 
          type: 'category', 
          data: [],
          axisLine: { lineStyle: { color: '#dcdfe6' } }
        },
        yAxis: [
          { 
            type: 'value', 
            name: '心率/血压',
            axisLine: { lineStyle: { color: '#dcdfe6' } },
            splitLine: { lineStyle: { color: '#e4e7ed' } }
          },
          { 
            type: 'value', 
            name: '血糖',
            axisLine: { lineStyle: { color: '#dcdfe6' } },
            splitLine: { lineStyle: { color: '#e4e7ed' } }
          }
        ],
        series: []
      };
      trendChart.setOption(option);
      return;
    }

    // 获取老人姓名
    let elderlyName = '';
    if (trendQueryType.value === 'single' && trendSelectedElderly.value) {
      const elderly = elderlyList.value.find(item => item.id === trendSelectedElderly.value);
      if (elderly) {
        elderlyName = elderly.realName || elderly.name;
      }
    }

    const option = {
      title: {
        text: trendQueryType.value === 'single' ? `${elderlyName}的健康数据趋势` : '所有老人健康数据趋势',
        left: 'center',
        textStyle: { fontSize: 14, fontWeight: 'normal' }
      },
      tooltip: {
        trigger: "axis",
      },
      legend: {
        data: ["平均心率", "收缩压", "平均血糖"],
        top: 30,
        left: 'center',
        itemGap: 20,
        textStyle: {
          fontSize: 12
        }
      },
      xAxis: {
        type: "category",
        data: dates,
        axisLabel: {
          rotate: dates.length > 10 ? 45 : 0
        }
      },
      yAxis: [
        {
          type: "value",
          name: "心率/血压",
        },
        {
          type: "value",
          name: "血糖",
        },
      ],
      series: [
        {
          name: "平均心率",
          type: "line",
          data: heartRates,
          smooth: true,
        },
        {
          name: "收缩压",
          type: "line",
          data: systolicPressures,
          smooth: true,
        },
        {
          name: "平均血糖",
          type: "line",
          yAxisIndex: 1,
          data: bloodSugars,
          smooth: true,
        },
      ],
    };
    trendChart.setOption(option);
  } catch (error) {
    console.error("获取趋势数据失败", error);
    // 显示错误提示
    const option = {
      title: { 
        text: '数据加载失败', 
        left: 'center', 
        top: 'center',
        textStyle: { color: '#f56c6c' }
      },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value' },
      series: []
    };
    trendChart.setOption(option);
  }
};

const initPieChart = async () => {
  await nextTick();
  if (!pieChartRef.value) return;
  pieChart = echarts.init(pieChartRef.value);

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
        name: "健康状态",
        type: "pie",
        radius: "50%",
        data: [
          { value: statistics.normal, name: "正常" },
          { value: statistics.abnormal, name: "异常" },
        ],
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
  dialogTitle.value = "新增健康记录";
  Object.assign(form, {
    id: null,
    elderlyId: null,
    heartRate: 75,
    systolicPressure: 120,
    diastolicPressure: 80,
    bloodSugar: 5.5,
    temperature: 36.5,
    weight: 65,
    status: "normal",
    remark: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  dialogTitle.value = "编辑健康记录";
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handleView = (row) => {
  currentRow.value = row;
  viewDialogVisible.value = true;
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该健康记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteHealth(row.id);
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
        const data = {
          userId: form.elderlyId,
          bloodPressureSystolic: form.systolicPressure,
          bloodPressureDiastolic: form.diastolicPressure,
          heartRate: form.heartRate,
          bloodSugar: form.bloodSugar,
          bodyTemperature: form.temperature,
          weight: form.weight,
          note: form.remark
        };
        if (form.id) {
          await updateHealth(form.id, data);
          ElMessage.success("更新成功");
        } else {
          await createHealth(data);
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
  fetchList();
  fetchStatistics();
  await fetchElderlyList();
  initTrendChart();
  initPieChart();
});
</script>

<style lang="scss" scoped>
.health-container {
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
