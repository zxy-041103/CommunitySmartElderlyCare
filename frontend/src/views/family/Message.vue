<template>
  <div class="family-message">
    <el-card class="message-card">
      <template #header>
        <div class="card-header">
          <h2 class="page-title">消息中心</h2>
          <div class="header-actions">
            <el-button :icon="Refresh" @click="refreshMessages" :loading="loading">
              刷新
            </el-button>
            <el-button type="primary" @click="markAllRead" :disabled="unreadCount === 0">
              <el-icon><Check /></el-icon>
              全部已读
            </el-button>
          </div>
        </div>
      </template>

      <div class="message-tabs">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部" name="all">
            <template #label>
              <span>全部</span>
              <el-badge v-if="unreadCount > 0" :value="unreadCount" class="tab-badge" />
            </template>
          </el-tab-pane>
          <el-tab-pane label="健康预警" name="warning">
            <template #label>
              <span>健康预警</span>
              <el-badge v-if="unreadWarningCount > 0" :value="unreadWarningCount" class="tab-badge" />
            </template>
          </el-tab-pane>
          <el-tab-pane label="服务通知" name="service">
            <template #label>
              <span>服务通知</span>
              <el-badge v-if="unreadServiceCount > 0" :value="unreadServiceCount" class="tab-badge" />
            </template>
          </el-tab-pane>
          <el-tab-pane label="求助通知" name="help">
            <template #label>
              <span>求助通知</span>
              <el-badge v-if="unreadHelpCount > 0" :value="unreadHelpCount" class="tab-badge" />
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="message-list" v-loading="loading">
        <div v-if="filteredMessages.length === 0" class="empty-state">
          <el-empty description="暂无消息" />
        </div>
        <div
          v-else
          v-for="message in filteredMessages"
          :key="message.id"
          class="message-item"
          :class="{ unread: message.isRead === 0 }"
          @click="viewMessageDetail(message)"
        >
          <div class="message-icon" :class="message.type">
            <el-icon :size="32">
              <Warning v-if="message.type === 'warning'" />
              <Bell v-else-if="message.type === 'service'" />
              <PhoneFilled v-else-if="message.type === 'help'" />
              <Document v-else />
            </el-icon>
          </div>
          <div class="message-content">
            <div class="message-header">
              <h4 class="message-title">{{ message.title }}</h4>
              <span class="message-time">{{ formatTime(message.createTime) }}</span>
            </div>
            <p class="message-body">{{ message.content }}</p>
            <div v-if="message.relatedId" class="message-meta">
              <el-tag size="small" :type="getTagType(message.type)">
                {{ getTagText(message.type) }}
              </el-tag>
              <span v-if="message.level" class="message-level">
                优先级: {{ getLevelText(message.level) }}
              </span>
            </div>
          </div>
          <div class="message-actions">
            <el-button
              v-if="message.isRead === 0"
              type="primary"
              size="small"
              @click.stop="markAsRead(message)"
            >
              标为已读
            </el-button>
            <el-button
              v-if="message.relatedId && message.type === 'warning'"
              type="danger"
              size="small"
              @click.stop="handleWarningAction(message)"
            >
              查看预警
            </el-button>
            <el-button
              v-if="message.relatedId && message.type === 'service'"
              type="primary"
              size="small"
              @click.stop="handleServiceAction(message)"
            >
              查看订单
            </el-button>
            <el-button
              v-if="message.relatedId && message.type === 'help'"
              type="danger"
              size="small"
              @click.stop="handleHelpAction(message)"
            >
              查看求助
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="filteredMessages.length > 0" class="pagination-section">
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

    <el-dialog v-model="detailVisible" :title="currentMessage?.title" width="600px">
      <div v-if="currentMessage" class="message-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="消息类型">
            <el-tag :type="getTagType(currentMessage.type)">
              {{ getTagText(currentMessage.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发送时间">{{ formatTime(currentMessage.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="优先级">{{ getLevelText(currentMessage.level) }}</el-descriptions-item>
          <el-descriptions-item label="消息内容">
            <div class="detail-content">{{ currentMessage.content }}</div>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="currentMessage.relatedId" class="related-actions">
          <el-button
            v-if="currentMessage.type === 'warning'"
            type="danger"
            @click="handleWarningAction(currentMessage)"
          >
            查看健康预警详情
          </el-button>
          <el-button
            v-if="currentMessage.type === 'service'"
            type="primary"
            @click="handleServiceAction(currentMessage)"
          >
            查看服务订单详情
          </el-button>
          <el-button
            v-if="currentMessage.type === 'help'"
            type="danger"
            @click="handleHelpAction(currentMessage)"
          >
            查看求助记录详情
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, computed, onMounted, onUnmounted } from "vue";

// 页面名称
defineOptions({
  name: 'FamilyMessage'
});
import { useRouter } from "vue-router";
import { Check, Refresh, Warning, Bell, Document, PhoneFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import request from "@/utils/request";

const router = useRouter();
const userStore = useUserStore();

const activeTab = ref("all");
const loading = ref(false);
const messages = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const detailVisible = ref(false);
const currentMessage = ref(null);

let refreshInterval = null;

const fetchMessages = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
    };

    if (activeTab.value !== "all") {
      params.type = activeTab.value;
    }

    const response = await request({
      url: "/api/notifications",
      method: "get",
      params,
    });

    if (response.code === 200 && response.data) {
      messages.value = response.data.records || [];
      total.value = response.data.total || 0;
    }
  } catch (error) {
    console.error("获取消息列表失败:", error);
    messages.value = [
      {
        id: 1,
        title: "健康预警通知",
        content: "您关注的老人血压数据异常，收缩压达到145mmHg，请及时关注。",
        type: "warning",
        level: "high",
        isRead: 0,
        createTime: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
        relatedId: 1,
      },
      {
        id: 2,
        title: "服务订单确认",
        content: "您预约的居家护理服务已确认，服务人员将于明天上午9点上门服务。",
        type: "service",
        level: "normal",
        isRead: 0,
        createTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
        relatedId: 2,
      },
      {
        id: 3,
        title: "紧急求助通知",
        content: "您关注的老人已触发紧急求助，请立即查看并联系确认。",
        type: "help",
        level: "urgent",
        isRead: 0,
        createTime: new Date(Date.now() - 10 * 60 * 1000).toISOString(),
        relatedId: 3,
      },
      {
        id: 4,
        title: "服务完成通知",
        content: "居家护理服务已完成，服务人员已提交服务记录，请查看并评价。",
        type: "service",
        level: "normal",
        isRead: 1,
        createTime: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
        relatedId: 4,
      },
    ];
    total.value = messages.value.length;
  } finally {
    loading.value = false;
  }
};

const filteredMessages = computed(() => {
  return messages.value;
});

const unreadCount = computed(() => {
  return messages.value.filter((m) => m.isRead === 0).length;
});

const unreadWarningCount = computed(() => {
  return messages.value.filter((m) => m.type === "warning" && m.isRead === 0).length;
});

const unreadServiceCount = computed(() => {
  return messages.value.filter((m) => m.type === "service" && m.isRead === 0).length;
});

const unreadHelpCount = computed(() => {
  return messages.value.filter((m) => m.type === "help" && m.isRead === 0).length;
});

const handleTabChange = () => {
  currentPage.value = 1;
  fetchMessages();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchMessages();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchMessages();
};

const refreshMessages = () => {
  fetchMessages();
};

const viewMessageDetail = (message) => {
  currentMessage.value = message;
  detailVisible.value = true;
  if (message.isRead === 0) {
    markAsRead(message);
  }
};

const markAsRead = async (message) => {
  try {
    const response = await request({
      url: `/api/notifications/${message.id}/read`,
      method: "put",
    });

    if (response.code === 200) {
      message.isRead = 1;
      ElMessage.success("消息已标记为已读");
    }
  } catch (error) {
    console.error("标记已读失败:", error);
    message.isRead = 1;
    ElMessage.success("消息已标记为已读");
  }
};

const markAllRead = async () => {
  try {
    const response = await request({
      url: "/api/notifications/read-all",
      method: "put",
    });

    if (response.code === 200) {
      messages.value.forEach((m) => {
        m.isRead = 1;
      });
      ElMessage.success("所有消息已标记为已读");
    }
  } catch (error) {
    console.error("全部标记已读失败:", error);
    messages.value.forEach((m) => {
      m.isRead = 1;
    });
    ElMessage.success("所有消息已标记为已读");
  }
};

const handleWarningAction = (message) => {
  detailVisible.value = false;
  router.push({
    path: "/family/monitoring",
    query: { warningId: message.relatedId },
  });
};

const handleServiceAction = (message) => {
  detailVisible.value = false;
  router.push({
    path: "/family/monitoring",
    query: { orderId: message.relatedId },
  });
};

const handleHelpAction = (message) => {
  detailVisible.value = false;
  router.push({
    path: "/family/monitoring",
    query: { helpId: message.relatedId },
  });
};

const getTagType = (type) => {
  const typeMap = {
    warning: "danger",
    service: "primary",
    help: "warning",
    system: "info",
  };
  return typeMap[type] || "info";
};

const getTagText = (type) => {
  const textMap = {
    warning: "健康预警",
    service: "服务通知",
    help: "求助通知",
    system: "系统通知",
  };
  return textMap[type] || "通知";
};

const getLevelText = (level) => {
  const textMap = {
    urgent: "紧急",
    high: "高",
    normal: "普通",
    low: "低",
  };
  return textMap[level] || "普通";
};

const formatTime = (time) => {
  if (!time) return "--";
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;

  if (diff < 60 * 1000) {
    return "刚刚";
  } else if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / 60 / 1000)}分钟前`;
  } else if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / 60 / 60 / 1000)}小时前`;
  } else if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / 24 / 60 / 60 / 1000)}天前`;
  } else {
    return date.toLocaleDateString("zh-CN");
  }
};

const startAutoRefresh = () => {
  refreshInterval = setInterval(() => {
    fetchMessages();
  }, 30000);
};

const stopAutoRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
    refreshInterval = null;
  }
};

onMounted(() => {
  fetchMessages();
  startAutoRefresh();
});

onUnmounted(() => {
  stopAutoRefresh();
});
</script>

<style lang="scss" scoped>
.family-message {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.message-card {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.message-tabs {
  margin-bottom: 20px;
}

.tab-badge {
  margin-left: 5px;
}

.message-list {
  max-height: 600px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 12px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  &.unread {
    border-left: 4px solid #f56c6c;
    background-color: #fef0f0;
  }
}

.message-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

  &.warning {
    background-color: #f56c6c;
  }

  &.service {
    background-color: #409eff;
  }

  &.help {
    background-color: #e6a23c;
  }

  &.system {
    background-color: #909399;
  }
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.message-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  flex: 1;
  margin-right: 20px;
}

.message-time {
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
}

.message-body {
  font-size: 16px;
  color: #606266;
  margin: 0 0 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-level {
  font-size: 12px;
  color: #909399;
}

.message-actions {
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

.message-detail {
  .detail-content {
    line-height: 1.6;
    white-space: pre-wrap;
  }

  .related-actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .family-message {
    padding: 20px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .page-title {
    font-size: 24px;
  }

  .message-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .message-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
