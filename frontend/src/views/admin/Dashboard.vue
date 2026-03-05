<template>
  <div class="dashboard">
    <el-row :gutter="12">
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ elderlyCount }}</div>
              <div class="stat-label">老人总数</div>
              <div class="stat-hint" v-if="elderlyCount === 0">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="24"><Monitor /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ healthMonitorCount }}</div>
              <div class="stat-label">今日健康监测</div>
              <div class="stat-hint" v-if="healthMonitorCount === 0">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="24"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ emergencyCount }}</div>
              <div class="stat-label">紧急求助</div>
              <div class="stat-hint" v-if="emergencyCount === 0">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="24"><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ activityCount }}</div>
              <div class="stat-label">总活动</div>
              <div class="stat-hint" v-if="activityCount === 0">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #909399">
              <el-icon :size="24"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ abnormalCount }}</div>
              <div class="stat-label">健康异常</div>
              <div class="stat-hint" v-if="abnormalCount === 0">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #9254de">
              <el-icon :size="24"><FirstAidKit /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ serviceCount }}</div>
              <div class="stat-label">服务订单</div>
              <div class="stat-hint" v-if="serviceCount === 0">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdmin">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>家属-老人关联关系审核</span>
              <el-button
                type="primary"
                size="small"
                plain
                @click="refreshPendingRelations"
              >
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <el-table :data="pendingRelations" style="width: 100%">
            <el-table-column prop="familyName" label="家属姓名" width="100" />
            <el-table-column prop="familyPhone" label="家属手机号" width="120">
              <template #default="scope">
                {{ formatPhone(scope.row.familyPhone) }}
              </template>
            </el-table-column>
            <el-table-column prop="elderlyName" label="老人姓名" width="100" />
            <el-table-column prop="elderlyPhone" label="老人手机号" width="120">
              <template #default="scope">
                {{ formatPhone(scope.row.elderlyPhone) }}
              </template>
            </el-table-column>
            <el-table-column prop="relationType" label="关系" width="80">
              <template #default="scope">
                {{ relationTypeMap[scope.row.relationType] }}
              </template>
            </el-table-column>
            <el-table-column prop="relationName" label="关系名称" width="90" />
            <el-table-column prop="applyTime" label="申请时间" width="160">
              <template #default="scope">
                {{ formatDateTime(scope.row.applyTime) }}
              </template>
            </el-table-column>
            <el-table-column label="审核材料" width="90">
              <template #default="scope">
                <el-button 
                  type="info" 
                  size="small" 
                  @click="viewProofMaterials(scope.row)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
                  type="success"
                  size="small"
                  @click="handleAudit(scope.row, 'approved')"
                  >通过</el-button
                >
                <el-button
                  type="danger"
                  size="small"
                  @click="handleAudit(scope.row, 'rejected')"
                  >拒绝</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动分类统计 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdmin">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>活动分类统计</span>
              <el-button
                type="primary"
                size="small"
                plain
                @click="refreshActivityStats"
              >
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <!-- 饼状图 -->
          <div ref="pieChartRef" style="width: 100%; height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 审核材料查看弹窗 -->
    <el-dialog
      v-model="proofMaterialsVisible"
      title="审核材料"
      width="800px"
    >
      <div v-if="parsedProofMaterials && parsedProofMaterials.length > 0" class="proof-materials">
        <div class="materials-list">
          <div
            v-for="(material, index) in parsedProofMaterials"
            :key="index"
            class="material-item"
          >
            <el-image
              :src="getFullImageUrl(material.url)"
              fit="cover"
              style="width: 200px; height: 200px; cursor: pointer"
              @click="previewImage(getFullImageUrl(material.url))"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
            <div class="material-name">{{ material.name }}</div>
          </div>
        </div>
      </div>
      <div v-else class="no-materials">
        <el-empty description="暂无审核材料" :image-size="80" />
      </div>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="`审核关联关系 - ${currentRelation?.familyName} 与 ${currentRelation?.elderlyName}`"
      width="600px"
    >
      <!-- 审核材料查看 -->
      <div v-if="currentRelation?.proofMaterials" class="proof-materials">
        <h4>审核材料</h4>
        <div class="materials-list">
          <el-image
            v-for="(material, index) in parsedProofMaterials"
            :key="index"
            :src="getFullImageUrl(material.url)"
            fit="cover"
            style="width: 100px; height: 100px; margin: 5px; cursor: pointer"
            @click="previewImage(getFullImageUrl(material.url))"
          >
            <template #error>
              <div class="image-error-small">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
      </div>
      <div v-else class="proof-materials">
        <h4>审核材料</h4>
        <p>无审核材料</p>
      </div>
      
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" style="margin-top: 20px">
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :label="'approved'">通过</el-radio>
            <el-radio :label="'rejected'">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAuditForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewDialogVisible" title="预览图片" width="800px">
      <el-image :src="previewImageUrl" fit="contain" style="width: 100%; height: 500px" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import request from "@/utils/request";
import {
  User,
  Monitor,
  Warning,
  Calendar,
  Refresh,
  Picture,
  FirstAidKit,
} from "@element-plus/icons-vue";

// 页面名称
defineOptions({
  name: 'AdminDashboard'
});

// 导入ECharts
import * as echarts from 'echarts';

const userStore = useUserStore();
const isAdmin = computed(() => userStore.roleType === 4);

// 统计数据
const elderlyCount = ref(0);
const healthMonitorCount = ref(0);
const emergencyCount = ref(0);
const activityCount = ref(0);
const abnormalCount = ref(0);
const serviceCount = ref(0);

// 家属-老人关联关系审核
const pendingRelations = ref([]);
const auditDialogVisible = ref(false);
const currentRelation = ref(null);
const auditForm = ref({});
const auditFormRef = ref(null);
const auditRules = {
  auditStatus: [
    { required: true, message: "请选择审核状态", trigger: "change" },
  ],
};

// 格式化手机号
const formatPhone = (phone) => {
  if (!phone) return '';
  const phoneStr = phone.toString();
  if (phoneStr.length === 11) {
    return phoneStr.replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3');
  }
  return phone;
};

// 审核材料查看
const proofMaterialsVisible = ref(false);

// 活动分类统计
const activityStats = ref([]);
// 饼状图实例
const pieChartRef = ref(null);
let pieChart = null;

// 解析审核材料
const parsedProofMaterials = computed(() => {
  if (!currentRelation.value?.proofMaterials) return [];
  try {
    return JSON.parse(currentRelation.value.proofMaterials);
  } catch (error) {
    console.error('解析审核材料失败:', error);
    return [];
  }
});

// 获取完整的图片URL
const getFullImageUrl = (url) => {
  if (!url) return '';
  // 如果URL已经是完整的http地址，直接返回
  if (url.startsWith('http')) {
    return url;
  }
  // 如果URL已经是相对于前端assets目录的路径（以/src/assets开头）
  if (url.startsWith('/src/assets')) {
    // 在开发环境中，Vite会自动处理这些路径
    return url;
  }
  // 如果URL是相对路径但不以/src/assets开头，尝试转换
  if (url.startsWith('/images/')) {
    return `/src/assets${url}`;
  }
  // 否则添加后端基础URL（兼容旧数据）
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  // 移除/api后缀，因为图片路径是相对于根目录的
  const serverUrl = baseUrl.replace('/api', '');
  return `${serverUrl}${url}`;
};

// 查看审核材料
const viewProofMaterials = (relation) => {
  currentRelation.value = relation;
  proofMaterialsVisible.value = true;
};

// 图片预览
const previewDialogVisible = ref(false);
const previewImageUrl = ref('');

const relationTypeMap = {
  spouse: "配偶",
  child: "子女",
  grandchild: "孙子女",
  sibling: "兄弟姐妹",
  other: "其他",
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--';
  try {
    // 处理不同格式的时间数据
    let date;
    if (typeof dateTime === 'string') {
      // 处理 ISO 格式的时间字符串
      if (dateTime.includes('T')) {
        date = new Date(dateTime.replace('T', ' '));
      } else {
        date = new Date(dateTime);
      }
    } else {
      date = new Date(dateTime);
    }
    
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return dateTime;
    }
    
    return date.toLocaleString('zh-CN');
  } catch (error) {
    console.error('时间格式化错误:', error);
    return dateTime;
  }
};

onMounted(async () => {
  await initData();
  // 初始化饼状图
  setTimeout(() => {
    initPieChart();
  }, 100);
});

// 初始化数据
const initData = async () => {
  // 获取统计数据
  await fetchStatistics();
  // 只有管理员才获取待审核的关联关系和活动分类统计
  if (isAdmin.value) {
    await getPendingRelations();
    await getActivityStats();
  }
};

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await request({
      url: '/admin/dashboard/statistics',
      method: 'get'
    });
    if (res.code === 200 && res.data) {
      elderlyCount.value = res.data.totalElderly || 0;
      // 健康监测数量用健康数据覆盖率计算（老人数 * 覆盖率 / 100）
      const coverage = res.data.healthCoverage || 0;
      healthMonitorCount.value = Math.round(elderlyCount.value * coverage / 100);
      emergencyCount.value = res.data.monthlyEmergencies || 0;
      activityCount.value = res.data.totalActivities || 0;
      abnormalCount.value = res.data.monthlyAbnormalities || 0;
      serviceCount.value = res.data.monthlyServices || 0;
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

// 获取待审核的关联关系
const getPendingRelations = async () => {
  try {
    const res = await request({
      url: '/admin/family-elderly-relations/pending',
      method: 'get'
    });
    if (res.code === 200 && res.data) {
      pendingRelations.value = res.data;
    }
  } catch (error) {
    console.error("获取待审核关联关系失败:", error);
  }
};

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url;
  previewDialogVisible.value = true;
};

// 刷新待审核的关联关系
const refreshPendingRelations = async () => {
  await getPendingRelations();
  ElMessage.success("刷新成功");
};

// 获取活动分类统计数据
const getActivityStats = async () => {
  try {
    const res = await request({
      url: "/admin/dashboard/activity-stats",
      method: "get",
    });
    if (res.code === 200) {
      activityStats.value = res.data;
      // 更新饼状图
      updatePieChart();
    }
  } catch (error) {
    console.error("获取活动分类统计失败:", error);
  }
};

// 刷新活动分类统计数据
const refreshActivityStats = async () => {
  await getActivityStats();
  ElMessage.success("刷新成功");
};

// 初始化饼状图
const initPieChart = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value);
    updatePieChart();
  }
};

// 更新饼状图数据
const updatePieChart = () => {
  if (!pieChart) return;
  
  const data = activityStats.value.map(item => ({
    name: item.categoryName,
    value: item.activityCount
  }));
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: activityStats.value.map(item => item.categoryName)
    },
    series: [
      {
        name: '活动分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  };
  
  pieChart.setOption(option);
};

// 监听窗口大小变化，调整饼状图大小
window.addEventListener('resize', () => {
  if (pieChart) {
    pieChart.resize();
  }
});

// 个人信息编辑相关方法已移至 Layout.vue 中的头像下拉框

// 处理审核
const handleAudit = (relation, status) => {
  currentRelation.value = relation;
  auditForm.value = {
    auditStatus: status,
    auditRemark: "",
  };
  auditDialogVisible.value = true;
};

// 提交审核
const submitAuditForm = async () => {
  if (!auditFormRef.value || !currentRelation.value) return;

  try {
    await auditFormRef.value.validate();
    const res = await import("@/api/user").then((m) =>
      m.auditFamilyRelation(currentRelation.value.id, auditForm.value),
    );
    if (res.data) {
      await getPendingRelations();
      auditDialogVisible.value = false;
      ElMessage.success("审核成功");
    }
  } catch (error) {
    console.error("审核失败:", error);
    ElMessage.error("审核失败");
  }
};
</script>

<style lang="scss" scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  height: 90px;

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-right: 12px;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
    transition: all 0.3s ease;
    flex-shrink: 0;

    &:hover {
      transform: scale(1.05);
      box-shadow: 0 5px 14px rgba(0, 0, 0, 0.2);
    }
  }

  .stat-content {
    flex: 1;
    min-width: 0;

    .stat-value {
      font-size: 22px;
      font-weight: bold;
      margin-bottom: 4px;
      color: #2c3e50;
    }

    .stat-label {
      font-size: 13px;
      color: #7f8c8d;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .stat-hint {
      font-size: 11px;
      color: #909399;
      margin-top: 2px;
      font-style: italic;
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.user-info {
  display: flex;
  align-items: flex-start;
  padding: 20px 0;

  .user-avatar {
    margin-right: 20px;

    img {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
      border: 3px solid #f0f2f5;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }

  .user-details {
    flex: 1;

    .info-item {
      margin-bottom: 12px;
      display: flex;
      align-items: center;

      .label {
        font-weight: 500;
        color: #606266;
        min-width: 60px;
        margin-right: 10px;
      }

      .value {
        color: #303133;
        word-break: break-all;
      }
    }
  }
}

:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;

  &:hover {
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }

  .el-card__header {
    font-size: 18px;
    font-weight: bold;
    color: #2c3e50;
    padding: 20px;
    border-bottom: 1px solid #f0f2f5;
  }

  .el-card__body {
    padding: 20px;
  }
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;

  .el-table__header-wrapper {
    background-color: #f5f7fa;
  }

  .el-table__row {
    transition: all 0.3s ease;

    &:hover {
      background-color: #f5f7fa;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.proof-materials {
  padding: 20px 0;
}

.materials-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.material-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.material-name {
  font-size: 14px;
  color: #606266;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-error {
  width: 200px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
  gap: 8px;
}

.image-error-small {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
}

.no-materials {
  padding: 40px 0;
  text-align: center;
}
</style>







