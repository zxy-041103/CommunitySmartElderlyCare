import { defineStore } from "pinia";
import { login as loginApi } from "@/api/common/login";
import { getElderlyDetail } from "@/api/elderly/profile";

// 登录状态管理策略：
// 1. 首次访问（新标签页/新窗口）：显示登录页，不自动登录
// 2. 登录成功后：保存到 localStorage，标记登录状态
// 3. 刷新页面：保持登录状态（从 localStorage 恢复）
// 4. 关闭页面再打开：需要重新登录（清除自动登录标记）

// 检查是否是新会话（通过 sessionStorage）
const SESSION_KEY = 'app_session_active';
const isNewSession = !sessionStorage.getItem(SESSION_KEY);

// 如果是新会话，清除 localStorage 中的登录状态（强制重新登录）
if (isNewSession) {
  // 标记会话已激活
  sessionStorage.setItem(SESSION_KEY, 'true');
  // 清除持久化的登录状态
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('roleType');
  localStorage.removeItem('userId');
}

export const useUserStore = defineStore("user", {
  state: () => ({
    // 从 localStorage 恢复状态（刷新页面时）
    token: localStorage.getItem("token") || "",
    userInfo: null,
    role: localStorage.getItem("role") || "",
    roleType: localStorage.getItem("roleType") && localStorage.getItem("roleType") !== "" && localStorage.getItem("roleType") !== "null" ? parseInt(localStorage.getItem("roleType")) : null,
    userId: localStorage.getItem("userId") || "",
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userName: (state) => state.userInfo?.realName || state.userInfo?.username || "",
    currentUserId: (state) => state.userId,
  },

  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm);
      if (res.code === 200 && res.data) {
        this.token = res.data.token;
        this.role = res.data.userInfo.roleType;
        // 映射字符串角色类型为数字
        console.log('登录时后端返回的 roleType:', res.data.userInfo.roleType);
        console.log('登录时后端返回的 roleType 类型:', typeof res.data.userInfo.roleType);
        const roleTypeMap = {
          'elderly': 1,
          'family': 2,
          'caregiver': 3,
          'admin': 4
        };
        // 转换为小写以确保匹配
        const roleTypeLower = res.data.userInfo.roleType?.toLowerCase();
        this.roleType = roleTypeMap[roleTypeLower] || null;
        console.log('转换为小写后的 roleType:', roleTypeLower);
        console.log('映射后的 roleType:', this.roleType);
        this.userInfo = res.data.userInfo;
        this.userId = res.data.userInfo.id;
        // 保存到 localStorage 用于刷新恢复
        localStorage.setItem("token", res.data.token);
        localStorage.setItem("role", res.data.userInfo.roleType);
        if (this.roleType !== null) {
          localStorage.setItem("roleType", this.roleType.toString());
        } else {
          localStorage.removeItem("roleType");
        }
        localStorage.setItem("userId", res.data.userInfo.id);
      }
      return res;
    },

    async getUserInfo() {
      if (this.userId) {
        try {
          // 根据角色类型获取用户信息
          let res;
          if (this.role === 'elderly') {
            res = await getElderlyDetail(this.userId);
          } else {
            // 对于其他角色，暂时使用相同的API
            res = await getElderlyDetail(this.userId);
          }
          if (res.code === 200 && res.data) {
            this.userInfo = res.data;
            // 映射字符串角色类型为数字
            console.log('获取用户信息时后端返回的 roleType:', res.data.roleType);
            console.log('获取用户信息时后端返回的 roleType 类型:', typeof res.data.roleType);
            const roleTypeMap = {
              'elderly': 1,
              'family': 2,
              'caregiver': 3,
              'admin': 4
            };
            // 转换为小写以确保匹配
            const roleTypeLower = res.data.roleType?.toLowerCase();
            this.roleType = roleTypeMap[roleTypeLower] || null;
            console.log('转换为小写后的 roleType:', roleTypeLower);
            console.log('映射后的 roleType:', this.roleType);
            // 更新 localStorage 中的 roleType
            if (this.roleType !== null) {
              localStorage.setItem("roleType", this.roleType.toString());
            } else {
              localStorage.removeItem("roleType");
            }
          }
          return res;
        } catch (error) {
          console.error("获取用户信息失败:", error);
          // 如果获取失败，可能是 token 过期，清除登录状态
          this.logout();
          throw error;
        }
      }
    },

    // 初始化用户状态（页面加载时调用）
    async initUserState() {
      if (this.token && this.userId) {
        try {
          // 尝试获取用户信息验证 token 是否有效
          await this.getUserInfo();
        } catch (error) {
          // token 无效，已自动 logout
          console.log("登录状态已过期，请重新登录");
        }
      }
    },

    logout() {
      this.token = "";
      this.userInfo = null;
      this.role = "";
      this.roleType = null;
      this.userId = "";
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      localStorage.removeItem("roleType");
      localStorage.removeItem("userId");
    },
  },
});
