<template>
  <div class="notification-center">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
      <el-button :icon="Bell" circle @click="toggleNotificationPanel" />
    </el-badge>

    <el-drawer v-model="panelVisible" title="消息通知" direction="rtl" size="400px" class="notification-drawer">
      <div class="notification-header">
        <el-radio-group v-model="activeTab" size="small">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="warning">预警</el-radio-button>
          <el-radio-button value="task">任务</el-radio-button>
        </el-radio-group>
        <el-button type="primary" link size="small" @click="markAllAsRead" :disabled="unreadCount === 0">
          全部已读
        </el-button>
      </div>

      <div class="notification-list" v-loading="loading">
        <div v-if="filteredNotifications.length === 0" class="empty-state">
          <el-empty description="暂无消息" />
        </div>
        <div v-else>
          <div
            v-for="notification in filteredNotifications"
            :key="notification.id"
            class="notification-item"
            :class="{ unread: !notification.isRead }"
            @click="handleNotificationClick(notification)"
          >
            <div class="notification-icon">
              <el-icon :size="24" :class="getNotificationIconClass(notification.type)">
                <Warning v-if="notification.type === 'warning'" />
                <Bell v-else-if="notification.type === 'task'" />
                <Document v-else />
              </el-icon>
            </div>
            <div class="notification-content">
              <div class="notification-title">{{ notification.title }}</div>
              <div class="notification-message">{{ notification.message }}</div>
              <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
            </div>
            <el-tag :type="getNotificationTagType(notification.level)" size="small">
              {{ getNotificationLevelText(notification.level) }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="detailVisible" :title="currentNotification?.title" width="500px">
      <div class="notification-detail" v-if="currentNotification">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="消息类型">
            <el-tag :type="getNotificationTagType(currentNotification.level)">
              {{ getNotificationTypeText(currentNotification.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发送时间">{{ formatTime(currentNotification.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="消息内容">{{ currentNotification.message }}</el-descriptions-item>
        </el-descriptions>
        <div class="notification-actions" v-if="currentNotification.actionUrl">
          <el-button type="primary" @click="handleAction(currentNotification)">查看详情</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Bell, Warning, Document } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  userId: {
    type: Number,
    required: true,
  },
  roleType: {
    type: String,
    default: "family",
  },
});

const emit = defineEmits(["notification-click", "new-notification"]);

const router = useRouter();
const panelVisible = ref(false);
const detailVisible = ref(false);
const loading = ref(false);
const activeTab = ref("all");
const notifications = ref([]);
const currentNotification = ref(null);
const unreadCount = computed(() => notifications.value.filter((n) => !n.isRead).length);

let pollingInterval = null;

const filteredNotifications = computed(() => {
  if (activeTab.value === "all") {
    return notifications.value;
  }
  return notifications.value.filter((n) => n.type === activeTab.value);
});

const toggleNotificationPanel = () => {
  panelVisible.value = !panelVisible.value;
};

const getNotificationIconClass = (type) => {
  const classMap = {
    warning: "icon-warning",
    task: "icon-task",
    system: "icon-system",
  };
  return classMap[type] || "icon-system";
};

const getNotificationTagType = (level) => {
  const typeMap = {
    high: "danger",
    medium: "warning",
    low: "info",
  };
  return typeMap[level] || "info";
};

const getNotificationLevelText = (level) => {
  const textMap = {
    high: "紧急",
    medium: "重要",
    low: "普通",
  };
  return textMap[level] || "普通";
};

const getNotificationTypeText = (type) => {
  const textMap = {
    warning: "健康预警",
    task: "任务通知",
    system: "系统消息",
  };
  return textMap[type] || "系统消息";
};

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return "刚刚";
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

const handleNotificationClick = (notification) => {
  notification.isRead = true;
  currentNotification.value = notification;
  detailVisible.value = true;
  emit("notification-click", notification);
};

const handleAction = (notification) => {
  if (notification.actionUrl) {
    router.push(notification.actionUrl);
    detailVisible.value = false;
    panelVisible.value = false;
  }
};

const markAllAsRead = () => {
  notifications.value.forEach((n) => (n.isRead = true));
  ElMessage.success("已全部标记为已读");
};

const fetchNotifications = async () => {
  loading.value = true;
  try {
    const mockNotifications = [
      {
        id: 1,
        type: "warning",
        level: "high",
        title: "健康预警",
        message: "您的父亲血压偏高，收缩压145mmHg，请注意观察",
        createTime: new Date(Date.now() - 300000),
        isRead: false,
        actionUrl: "/family/monitoring",
      },
      {
        id: 2,
        type: "task",
        level: "medium",
        title: "服务进度更新",
        message: "居家护理服务已完成，请确认并评价",
        createTime: new Date(Date.now() - 3600000),
        isRead: false,
        actionUrl: "/family/monitoring",
      },
      {
        id: 3,
        type: "warning",
        level: "medium",
        title: "血糖预警",
        message: "您的母亲血糖略高，餐后血糖7.2mmol/L",
        createTime: new Date(Date.now() - 7200000),
        isRead: true,
        actionUrl: "/family/monitoring",
      },
    ];
    notifications.value = mockNotifications;
  } catch (error) {
    console.error("获取通知失败:", error);
  } finally {
    loading.value = false;
  }
};

const startPolling = () => {
  pollingInterval = setInterval(() => {
    fetchNotifications();
  }, 30000);
};

const stopPolling = () => {
  if (pollingInterval) {
    clearInterval(pollingInterval);
    pollingInterval = null;
  }
};

onMounted(() => {
  fetchNotifications();
  startPolling();
});

onUnmounted(() => {
  stopPolling();
});

defineExpose({
  fetchNotifications,
  addNotification: (notification) => {
    notifications.value.unshift({
      ...notification,
      id: Date.now(),
      createTime: new Date(),
      isRead: false,
    });
    emit("new-notification", notification);
  },
});
</script>

<style lang="scss" scoped>
.notification-center {
  display: inline-block;
}

.notification-badge {
  :deep(.el-badge__content) {
    font-size: 12px;
  }
}

.notification-drawer {
  :deep(.el-drawer__header) {
    margin-bottom: 0;
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
  }

  :deep(.el-drawer__body) {
    padding: 0;
  }
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.notification-list {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: #f5f7fa;
  }

  &.unread {
    background-color: #ecf5ff;
  }
}

.notification-icon {
  margin-right: 15px;
  flex-shrink: 0;

  .icon-warning {
    color: #f56c6c;
  }

  .icon-task {
    color: #409eff;
  }

  .icon-system {
    color: #909399;
  }
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}

.notification-message {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-detail {
  .notification-actions {
    margin-top: 20px;
    text-align: center;
  }
}

.empty-state {
  padding: 40px 0;
}
</style>
