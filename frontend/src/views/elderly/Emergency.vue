<template>
  <div class="elderly-emergency">
    <el-card class="emergency-sos-card">
      <div class="emergency-sos-content">
        <div class="emergency-sos-icon">
          <el-icon :size="80"><WarningFilled /></el-icon>
        </div>
        <h2 class="emergency-sos-title">紧急求助</h2>
        <p class="emergency-sos-desc">遇到紧急情况，点击下方按钮立即求助</p>
        <el-button type="danger" size="large" class="emergency-sos-btn" @click="showEmergencyConfirm">
          <el-icon :size="32"><WarningFilled /></el-icon>
          <span>一键求助</span>
        </el-button>
      </div>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <h2 class="page-title">我的紧急求助</h2>
          <el-button type="primary" size="large" @click="handleAdd">
            <el-icon><Plus /></el-icon>
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

    <el-card style="margin-top: 20px">
      <template #header>
        <span>求助记录列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
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
          prop="createTime"
          label="求助时间"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="helpType"
          label="求助类型"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.helpType)">
              {{ getTypeText(row.helpType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="helpStatus"
          label="处理状态"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.helpStatus)">
              {{ getStatusText(row.helpStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="求助内容"
          min-width="200"
          align="center"
        />
        <el-table-column
          prop="handleTime"
          label="处理时间"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            {{ row.handleTime ? formatTime(row.handleTime) : '-' }}
          </template>
        </el-table-column>
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
            <el-button
              v-if="row.helpStatus === 'pending'"
              type="danger"
              link
              size="small"
              @click="handleCancel(row)"
            >
              取消
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
      title="求助详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="求助时间">
          {{ formatTime(currentRow.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="求助类型">
          <el-tag :type="getTypeTagType(currentRow.helpType)">
            {{ getTypeText(currentRow.helpType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusTagType(currentRow.helpStatus)">
            {{ getStatusText(currentRow.helpStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理时间">
          {{ currentRow.handleTime ? formatTime(currentRow.handleTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人">
          {{ currentRow.handlerId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="求助内容" :span="2">
          {{ currentRow.description }}
        </el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">
          {{ currentRow.handleResult || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="dialogVisible"
      title="发起紧急求助"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="求助类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择求助类型" style="width: 100%">
            <el-option label="医疗急救" value="medical" />
            <el-option label="生活求助" value="life" />
            <el-option label="安全报警" value="security" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="求助内容" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="请详细描述您遇到的问题"
          />
        </el-form-item>
        <el-alert
          title="温馨提示"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <template #default>
            <p>• 医疗急救：突发疾病、身体不适等紧急医疗情况</p>
            <p>• 生活求助：日常生活困难、需要帮助等</p>
            <p>• 安全报警：发现安全隐患、遇到危险等</p>
            <p>• 其他：其他需要帮助的情况</p>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitLoading" @click="handleSubmit">
          确认求助
        </el-button>
      </template>
    </el-dialog>

    <!-- 紧急求助确认弹窗 -->
    <el-dialog
      v-model="emergencyConfirmVisible"
      title="紧急求助确认"
      width="500px"
      center
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      class="emergency-confirm-dialog"
    >
      <div class="emergency-confirm-content">
        <div class="emergency-icon-wrapper">
          <el-icon :size="80" class="emergency-icon"><WarningFilled /></el-icon>
        </div>
        <h3 class="emergency-title">确认发起紧急求助？</h3>
        <div class="countdown-wrapper">
          <div class="countdown-number">{{ countdown }}</div>
          <div class="countdown-text">秒后自动求助</div>
        </div>
        <p class="emergency-warning">系统将自动联系护工和家属，请确认您确实需要紧急帮助</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="cancelEmergencyConfirm" class="cancel-btn">
            取消求助
          </el-button>
          <el-button size="large" type="danger" @click="confirmEmergency" class="confirm-btn">
            立即求助
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Warning,
  Search,
  Refresh,
  WarningFilled,
  Loading,
  CircleCheckFilled,
} from "@element-plus/icons-vue";
import { getEmergencyList, createEmergency, cancelEmergency } from "@/api/elderly/emergency";

defineOptions({
  name: 'ElderlyEmergency'
});

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const emergencyConfirmVisible = ref(false);
const countdown = ref(10);
let countdownTimer = null;
const currentRow = ref(null);
const formRef = ref(null);

const searchForm = reactive({
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
const statistics = reactive({
  total: 0,
  pending: 0,
  handling: 0,
  completed: 0,
});

const form = reactive({
  type: "",
  description: "",
});

const rules = {
  type: [{ required: true, message: "请选择求助类型", trigger: "change" }],
  description: [
    { required: true, message: "请输入求助内容", trigger: "blur" },
    { min: 5, message: "求助内容至少5个字", trigger: "blur" },
  ],
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

const getTypeText = (type) => {
  const typeMap = {
    medical: "医疗急救",
    life: "生活求助",
    security: "安全报警",
    other: "其他",
  };
  return typeMap[type] || type;
};

const getTypeTagType = (type) => {
  const typeMap = {
    medical: "danger",
    life: "warning",
    security: "danger",
    other: "info",
  };
  return typeMap[type] || "info";
};

const getStatusText = (status) => {
  const statusMap = {
    pending: "待处理",
    handling: "处理中",
    completed: "已完成",
  };
  return statusMap[status] || status;
};

const getStatusTagType = (status) => {
  const statusMap = {
    pending: "danger",
    handling: "warning",
    completed: "success",
  };
  return statusMap[status] || "info";
};

const fetchList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
    };

    if (searchForm.type) {
      params.type = searchForm.type;
    }

    if (searchForm.status) {
      params.status = searchForm.status;
    }

    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startTime = searchForm.dateRange[0];
      params.endTime = searchForm.dateRange[1];
    }

    const res = await getEmergencyList(params);
    if (res.data) {
      tableData.value = res.data.list || [];
      pagination.total = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取求助记录失败", error);
    ElMessage.error("获取求助记录失败");
  } finally {
    loading.value = false;
  }
};

const fetchStatistics = async () => {
  try {
    const res = await getEmergencyList({ pageNum: 1, pageSize: 1000 });
    if (res.data && res.data.list) {
      const list = res.data.list;
      statistics.total = list.length;
      statistics.pending = list.filter(item => item.helpStatus === 'pending').length;
      statistics.handling = list.filter(item => item.helpStatus === 'handling').length;
      statistics.completed = list.filter(item => item.helpStatus === 'completed').length;
    }
  } catch (error) {
    console.error("获取统计数据失败", error);
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchList();
};

const handleReset = () => {
  searchForm.type = "";
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
  form.type = "";
  form.description = "";
  dialogVisible.value = true;
};

const handleView = (row) => {
  currentRow.value = row;
  viewDialogVisible.value = true;
};

const handleCancel = (row) => {
  ElMessageBox.confirm("确定要取消这条求助记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelEmergency(row.id);
        ElMessage.success("取消成功");
        fetchList();
        fetchStatistics();
      } catch (error) {
        console.error("取消失败", error);
        ElMessage.error("取消失败");
      }
    })
    .catch(() => {});
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    await createEmergency({
      type: form.type,
      description: form.description,
    });

    ElMessage.success("求助已发送，我们会尽快处理");
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

const showEmergencyConfirm = () => {
  emergencyConfirmVisible.value = true;
  countdown.value = 10;
  
  countdownTimer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(countdownTimer);
      confirmEmergency();
    }
  }, 1000);
};

const cancelEmergencyConfirm = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
  emergencyConfirmVisible.value = false;
  countdown.value = 10;
  ElMessage.info("已取消紧急求助");
};

const confirmEmergency = async () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
  
  try {
    await createEmergency({
      type: "medical",
      description: "紧急求助 - 一键求助"
    });
    ElMessage.success("紧急求助已发送，护工和家属将尽快联系您");
    emergencyConfirmVisible.value = false;
    countdown.value = 10;
    fetchList();
    fetchStatistics();
  } catch (error) {
    console.error("发送紧急求助失败", error);
    ElMessage.error("发送紧急求助失败，请稍后重试或直接拨打120");
  }
};

onMounted(() => {
  fetchList();
  fetchStatistics();
});
</script>

<style lang="scss" scoped>
.elderly-emergency {
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

.emergency-sos-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(245, 87, 108, 0.3);
}

.emergency-sos-content {
  text-align: center;
  padding: 40px 20px;
  color: white;
}

.emergency-sos-icon {
  margin-bottom: 20px;
  animation: pulse 2s infinite;
}

.emergency-sos-title {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.emergency-sos-desc {
  font-size: 18px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.emergency-sos-btn {
  padding: 20px 40px;
  font-size: 24px;
  border-radius: 12px;
  background: white;
  color: #f5576c;
  border: none;
  display: flex;
  align-items: center;
  gap: 15px;
  margin: 0 auto;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  animation: emergency-pulse 1s infinite;
}

.emergency-sos-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  background: #f8f9fa;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes emergency-pulse {
  0%, 100% {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  50% {
    box-shadow: 0 4px 20px rgba(245, 87, 108, 0.4);
  }
}

.emergency-confirm-dialog {
  .el-dialog__header {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 20px;
    border-radius: 8px 8px 0 0;
  }
  
  .el-dialog__title {
    color: white;
    font-size: 20px;
    font-weight: bold;
  }
}

.emergency-confirm-content {
  text-align: center;
  padding: 20px 0;
}

.emergency-icon-wrapper {
  margin-bottom: 20px;
}

.emergency-icon {
  color: #f5576c;
  animation: emergency-pulse 1s infinite;
}

.emergency-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
  font-weight: bold;
}

.countdown-wrapper {
  margin: 30px 0;
}

.countdown-number {
  font-size: 72px;
  font-weight: bold;
  color: #f5576c;
  line-height: 1;
  margin-bottom: 10px;
}

.countdown-text {
  font-size: 18px;
  color: #909399;
}

.emergency-warning {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin-top: 20px;
  padding: 15px;
  background-color: #fef0f0;
  border-radius: 8px;
  border-left: 4px solid #f56c6c;
}

.dialog-footer {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.cancel-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
}

.confirm-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
}
</style>