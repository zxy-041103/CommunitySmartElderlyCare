<template>
  <div class="service-record">
    <div class="page-header">
      <h2 class="page-title">服务记录</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="selectedDate"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
        />
        <el-button :icon="Refresh" @click="refreshRecords" :loading="loading">
          刷新
        </el-button>
      </div>
    </div>

    <el-card class="stats-card">
      <div class="stats-content">
        <div class="stat-item">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总服务数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value completed">{{ stats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
        <div class="stat-item">
          <div class="stat-value pending">{{ stats.pending }}</div>
          <div class="stat-label">待执行</div>
        </div>
        <div class="stat-item">
          <div class="stat-value in-progress">{{ stats.inProgress }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
    </el-card>

    <el-card class="filter-card">
      <div class="filter-content">
        <el-select v-model="filterStatus" placeholder="服务状态" clearable @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option label="待确认" value="pending" />
          <el-option label="已确认" value="confirmed" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索服务名称或老人姓名"
          clearable
          style="width: 300px"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </el-card>

    <el-card class="record-list-card">
      <div class="record-list" v-loading="loading">
        <div v-if="serviceRecords.length === 0" class="empty-state">
          <el-empty description="暂无服务记录" />
        </div>
        <div
          v-else
          v-for="record in serviceRecords"
          :key="record.id"
          class="record-item"
          :class="getRecordClass(record.orderStatus)"
          @click="viewRecordDetail(record)"
        >
          <div class="record-icon" :class="getIconClass(record.orderStatus)">
            <el-icon :size="24">
              <Check v-if="record.orderStatus === 'completed'" />
              <Clock v-else-if="record.orderStatus === 'in_progress'" />
              <Warning v-else-if="record.orderStatus === 'cancelled'" />
              <Document v-else />
            </el-icon>
          </div>
          <div class="record-info">
            <div class="record-header">
              <h4 class="service-name">{{ record.serviceName }}</h4>
              <el-tag :type="getStatusType(record.orderStatus)" size="small">
                {{ getStatusText(record.orderStatus) }}
              </el-tag>
            </div>
            <div class="record-details">
              <div class="detail-item">
                <el-icon><User /></el-icon>
                <span>{{ record.elderlyName || '待分配' }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ record.serviceDate }} {{ record.serviceTime }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Location /></el-icon>
                <span>{{ record.serviceAddress || '未设置' }}</span>
              </div>
            </div>
            <div class="record-meta">
              <span class="order-no">订单号: {{ record.orderNo }}</span>
              <span class="create-time">{{ formatDateTime(record.createTime) }}</span>
            </div>
          </div>
          <div class="record-actions">
            <el-button
              v-if="record.orderStatus === 'in_progress'"
              type="success"
              size="small"
              @click.stop="openScanDialog(record)"
            >
              扫码核销
            </el-button>
            <el-button
              v-if="record.orderStatus === 'completed' && !record.evaluated"
              type="primary"
              size="small"
              @click.stop="viewEvaluation(record)"
            >
              查看评价
            </el-button>
            <el-button type="info" size="small" @click.stop="viewRecordDetail(record)">
              详情
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="serviceRecords.length > 0" class="pagination-section">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <ScanVerification v-model="scanDialogVisible" @verification-success="handleVerificationSuccess" @verification-failed="handleVerificationFailed" />

    <el-dialog v-model="detailVisible" title="服务记录详情" width="700px">
      <div v-if="currentRecord" class="record-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号" :span="2">{{ currentRecord.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="服务名称">{{ currentRecord.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="服务类型">{{ currentRecord.serviceType }}</el-descriptions-item>
          <el-descriptions-item label="服务对象">{{ currentRecord.elderlyName || '待分配' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentRecord.contactPhone || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ currentRecord.serviceDate }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ currentRecord.serviceTime }}</el-descriptions-item>
          <el-descriptions-item label="服务地址" :span="2">{{ currentRecord.serviceAddress || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentRecord.orderStatus)">
              {{ getStatusText(currentRecord.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="核销状态">
            {{ currentRecord.verificationCode ? '已生成核销码' : '未生成' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务描述" :span="2">{{ currentRecord.serviceDescription || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentRecord.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentRecord.updateTime) }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentRecord.evaluation" class="evaluation-section">
          <h4 class="section-title">服务评价</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="评分">
              <el-rate v-model="currentRecord.evaluation.rating" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="评价内容">{{ currentRecord.evaluation.comment || '无评价内容' }}</el-descriptions-item>
            <el-descriptions-item label="评价时间">{{ formatDateTime(currentRecord.evaluation.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-actions">
          <el-button
            v-if="currentRecord.orderStatus === 'in_progress'"
            type="success"
            @click="openScanDialog(currentRecord)"
          >
            扫码核销
          </el-button>
          <el-button @click="detailVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, reactive, computed, onMounted } from "vue";

// 页面名称
defineOptions({
  name: 'CaregiverServiceRecord'
});
import {
  Check,
  Clock,
  Warning,
  Document,
  User,
  Calendar,
  Location,
  Search,
  Refresh,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getServiceRecords } from "@/api/caregiver/record";
import ScanVerification from "@/components/caregiver/ScanVerification.vue";

const loading = ref(false);
const selectedDate = ref([]);
const filterStatus = ref("");
const searchKeyword = ref("");
const serviceRecords = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const scanDialogVisible = ref(false);
const detailVisible = ref(false);
const currentRecord = ref(null);

const stats = reactive({
  total: 0,
  completed: 0,
  pending: 0,
  inProgress: 0,
});

const fetchServiceRecords = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
    };

    if (filterStatus.value) {
      params.status = filterStatus.value;
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }

    const response = await getServiceRecords(params);

    if (response.code === 200 && response.data) {
      serviceRecords.value = response.data.records || [];
      total.value = response.data.total || 0;
      updateStats(serviceRecords.value);
    }
  } catch (error) {
    console.error("获取服务记录失败:", error);
    serviceRecords.value = [
      {
        id: 1,
        orderNo: "SO202401240001",
        serviceName: "居家护理",
        serviceType: "日常护理",
        elderlyName: "张三",
        contactPhone: "13800138000",
        serviceDate: "2024-01-24",
        serviceTime: "09:00-11:00",
        serviceAddress: "幸福小区3栋2单元501室",
        orderStatus: "completed",
        serviceDescription: "协助老人进行日常护理",
        verificationCode: "VC202401240001",
        createTime: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
        updateTime: new Date(Date.now() - 20 * 60 * 60 * 1000).toISOString(),
        evaluated: true,
        evaluation: {
          rating: 5,
          comment: "服务态度很好，非常满意",
          createTime: new Date(Date.now() - 18 * 60 * 60 * 1000).toISOString(),
        },
      },
      {
        id: 2,
        orderNo: "SO202401240002",
        serviceName: "健康监测",
        serviceType: "健康服务",
        elderlyName: "李四",
        contactPhone: "13900139000",
        serviceDate: "2024-01-24",
        serviceTime: "14:00-15:00",
        serviceAddress: "阳光花园5栋1单元302室",
        orderStatus: "in_progress",
        serviceDescription: "测量血压、血糖等健康指标",
        verificationCode: "VC202401240002",
        createTime: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString(),
        updateTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
        evaluated: false,
      },
      {
        id: 3,
        orderNo: "SO202401240003",
        serviceName: "陪伴服务",
        serviceType: "生活服务",
        elderlyName: "王五",
        contactPhone: "13700137000",
        serviceDate: "2024-01-25",
        serviceTime: "10:00-12:00",
        serviceAddress: "和谐社区2栋3单元401室",
        orderStatus: "confirmed",
        serviceDescription: "陪伴老人聊天、散步",
        verificationCode: null,
        createTime: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(),
        updateTime: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(),
        evaluated: false,
      },
      {
        id: 4,
        orderNo: "SO202401240004",
        serviceName: "家政服务",
        serviceType: "生活服务",
        elderlyName: "赵六",
        contactPhone: "13600136000",
        serviceDate: "2024-01-25",
        serviceTime: "15:00-17:00",
        serviceAddress: "平安小区1栋1单元201室",
        orderStatus: "pending",
        serviceDescription: "打扫卫生、整理房间",
        verificationCode: null,
        createTime: new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString(),
        updateTime: new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString(),
        evaluated: false,
      },
    ];
    total.value = serviceRecords.value.length;
    updateStats(serviceRecords.value);
  } finally {
    loading.value = false;
  }
};

const updateStats = (records) => {
  stats.total = records.length;
  stats.completed = records.filter((r) => r.orderStatus === "completed").length;
  stats.pending = records.filter((r) => r.orderStatus === "pending" || r.orderStatus === "confirmed").length;
  stats.inProgress = records.filter((r) => r.orderStatus === "in_progress").length;
};

const refreshRecords = () => {
  fetchServiceRecords();
};

const handleDateChange = () => {
  currentPage.value = 1;
  fetchServiceRecords();
};

const handleFilterChange = () => {
  currentPage.value = 1;
  fetchServiceRecords();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchServiceRecords();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchServiceRecords();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchServiceRecords();
};

const viewRecordDetail = (record) => {
  currentRecord.value = record;
  detailVisible.value = true;
};

const openScanDialog = (record) => {
  currentRecord.value = record;
  detailVisible.value = false;
  scanDialogVisible.value = true;
};

const handleVerificationSuccess = () => {
  ElMessage.success("服务核销成功");
  scanDialogVisible.value = false;
  fetchServiceRecords();
};

const handleVerificationFailed = (message) => {
  ElMessage.error(message || "核销失败");
};

const viewEvaluation = (record) => {
  currentRecord.value = record;
  detailVisible.value = true;
};

const getStatusType = (status) => {
  const typeMap = {
    pending: "warning",
    confirmed: "primary",
    in_progress: "primary",
    completed: "success",
    cancelled: "danger",
  };
  return typeMap[status] || "info";
};

const getStatusText = (status) => {
  const textMap = {
    pending: "待确认",
    confirmed: "已确认",
    in_progress: "进行中",
    completed: "已完成",
    cancelled: "已取消",
  };
  return textMap[status] || status;
};

const getRecordClass = (status) => {
  const classMap = {
    completed: "record-completed",
    cancelled: "record-cancelled",
    in_progress: "record-in-progress",
  };
  return classMap[status] || "";
};

const getIconClass = (status) => {
  const classMap = {
    completed: "icon-success",
    cancelled: "icon-danger",
    in_progress: "icon-primary",
    pending: "icon-warning",
    confirmed: "icon-primary",
  };
  return classMap[status] || "icon-info";
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return "--";
  return new Date(dateTime).toLocaleString("zh-CN");
};

onMounted(() => {
  fetchServiceRecords();
});
</script>

<style lang="scss" scoped>
.service-record {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
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
  gap: 15px;
}

.stats-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.stats-content {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;

  &.completed {
    color: #67c23a;
  }

  &.pending {
    color: #e6a23c;
  }

  &.in-progress {
    color: #409eff;
  }
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.filter-content {
  display: flex;
  gap: 15px;
}

.record-list-card {
  border-radius: 12px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.record-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 12px;
  border-left: 4px solid #409eff;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.record-completed {
    border-left-color: #67c23a;
  }

  &.record-cancelled {
    border-left-color: #f56c6c;
  }

  &.record-in-progress {
    border-left-color: #409eff;
  }
}

.record-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;

  &.icon-success {
    background-color: #67c23a;
  }

  &.icon-danger {
    background-color: #f56c6c;
  }

  &.icon-primary {
    background-color: #409eff;
  }

  &.icon-warning {
    background-color: #e6a23c;
  }

  &.icon-info {
    background-color: #909399;
  }
}

.record-info {
  flex: 1;
  min-width: 0;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.service-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.record-details {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 10px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
}

.record-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.order-no {
  font-family: monospace;
}

.record-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.empty-state {
  padding: 40px 0;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.record-detail {
  .evaluation-section {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;

    .section-title {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin: 0 0 15px;
    }
  }

  .detail-actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .service-record {
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .filter-content {
    flex-direction: column;
  }

  .record-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .record-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .record-details {
    flex-direction: column;
    gap: 8px;
  }

  .record-meta {
    flex-direction: column;
    gap: 5px;
  }

  .record-actions {
    width: 100%;
    flex-direction: row;
    justify-content: flex-end;
  }

  .stats-content {
    flex-wrap: wrap;
    gap: 20px;
  }

  .stat-item {
    flex: 1;
    min-width: 80px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>
