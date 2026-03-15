import { defineStore } from "pinia";
import { getElderlyDetail } from "@/api/elderly/profile";
import { queryHealthData } from "@/api/elderly/health";
import { getServiceOrders } from "@/api/elderly/booking";

export const useElderlyStore = defineStore("elderly", {
  state: () => ({
    elderlyInfo: null,
    healthData: [],
    serviceOrders: [],
    loading: false,
  }),

  getters: {
    hasElderlyInfo: (state) => !!state.elderlyInfo,
    healthDataCount: (state) => state.healthData.length,
    pendingOrdersCount: (state) => state.serviceOrders.filter(order => order.orderStatus === 'pending' || order.orderStatus === 'confirmed').length,
  },

  actions: {
    async getElderlyInfo(elderlyId) {
      this.loading = true;
      try {
        const res = await getElderlyDetail(elderlyId);
        if (res.code === 200 && res.data) {
          this.elderlyInfo = res.data;
        }
        return res;
      } catch (error) {
        console.error("获取老人信息失败:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async getElderlyHealthData(elderlyId, params) {
      this.loading = true;
      try {
        const res = await queryHealthData({
          ...params,
          elderlyId,
        });
        if (res.code === 200 && res.data) {
          this.healthData = res.data.records || [];
        }
        return res;
      } catch (error) {
        console.error("获取老人健康数据失败:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async getElderlyServiceOrders(elderlyId, params) {
      this.loading = true;
      try {
        const res = await getServiceOrders({
          ...params,
          elderlyId,
        });
        if (res.code === 200 && res.data) {
          this.serviceOrders = res.data.records || [];
        }
        return res;
      } catch (error) {
        console.error("获取老人服务订单失败:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    clearElderlyData() {
      this.elderlyInfo = null;
      this.healthData = [];
      this.serviceOrders = [];
    },
  },
});