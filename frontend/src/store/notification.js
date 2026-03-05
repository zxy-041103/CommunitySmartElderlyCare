import { defineStore } from "pinia";
import { getMessages, markMessageAsRead, markAllMessagesAsRead } from "@/api/family/message";

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    messages: [],
    unreadCount: 0,
    loading: false,
  }),

  getters: {
    hasUnreadMessages: (state) => state.unreadCount > 0,
    recentMessages: (state) => state.messages.slice(0, 5),
  },

  actions: {
    async fetchMessages(params) {
      this.loading = true;
      try {
        const res = await getMessages(params);
        if (res.code === 200 && res.data) {
          this.messages = res.data.records || [];
          this.updateUnreadCount();
        }
        return res;
      } catch (error) {
        console.error("获取消息失败:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async markAsRead(messageId) {
      try {
        const res = await markMessageAsRead(messageId);
        if (res.code === 200) {
          // 更新本地消息状态
          const message = this.messages.find(m => m.id === messageId);
          if (message) {
            message.status = 'read';
            this.updateUnreadCount();
          }
        }
        return res;
      } catch (error) {
        console.error("标记消息已读失败:", error);
        throw error;
      }
    },

    async markAllAsRead() {
      try {
        const res = await markAllMessagesAsRead();
        if (res.code === 200) {
          // 更新本地所有消息状态
          this.messages.forEach(message => {
            message.status = 'read';
          });
          this.unreadCount = 0;
        }
        return res;
      } catch (error) {
        console.error("标记所有消息已读失败:", error);
        throw error;
      }
    },

    updateUnreadCount() {
      this.unreadCount = this.messages.filter(m => m.status === 'unread').length;
    },

    clearMessages() {
      this.messages = [];
      this.unreadCount = 0;
    },
  },
});