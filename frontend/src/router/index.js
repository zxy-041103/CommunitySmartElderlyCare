import { createRouter, createWebHistory } from "vue-router";
import Layout from "@/views/common/Layout.vue";
import Login from "@/views/common/Login.vue";
import { useUserStore } from "@/store/user";

const routes = [
  // 登录页 - 独立路由
  {
    path: "/login",
    name: "Login",
    component: Login,
    meta: { title: "登录", requiresAuth: false },
  },
  // 403页 - 独立路由
  {
    path: "/403",
    name: "403",
    component: () => import("@/views/common/403.vue"),
    meta: { title: "无权限访问", requiresAuth: false },
  },
  // 主布局路由
  {
    path: "/",
    name: "Layout",
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      // 老人角色路由 (role_type=1)
      {
        path: "elderly/home",
        name: "ElderlyHome",
        component: () => import("@/views/elderly/Home.vue"),
        meta: { title: "老人首页", roles: [1] },
      },
      {
        path: "elderly/health-report",
        name: "ElderlyHealthReport",
        component: () => import("@/views/elderly/HealthReport.vue"),
        meta: { title: "健康报告", roles: [1] },
      },
      {
        path: "elderly/service-booking",
        name: "ElderlyServiceBooking",
        component: () => import("@/views/elderly/ServiceBooking.vue"),
        meta: { title: "服务预约", roles: [1] },
      },
      {
        path: "elderly/health-monitor",
        name: "ElderlyHealthMonitor",
        component: () => import("@/views/elderly/HealthMonitor.vue"),
        meta: { title: "健康监测", roles: [1] },
      },
      {
        path: "elderly/emergency",
        name: "ElderlyEmergency",
        component: () => import("@/views/elderly/Emergency.vue"),
        meta: { title: "紧急求助", roles: [1] },
      },
      {
        path: "elderly/family-contact",
        name: "ElderlyFamilyContact",
        component: () => import("@/views/elderly/FamilyContact.vue"),
        meta: { title: "联系家属", roles: [1] },
      },

      // 家属角色路由 (role_type=2)
      {
        path: "family/monitoring",
        name: "FamilyMonitoring",
        component: () => import("@/views/family/Monitoring.vue"),
        meta: { title: "监护中心", roles: [2] },
      },
      {
        path: "family/message",
        name: "FamilyMessage",
        component: () => import("@/views/family/Message.vue"),
        meta: { title: "消息中心", roles: [2] },
      },
      {
        path: "family/add-relation",
        name: "FamilyAddRelation",
        component: () => import("@/views/family/AddRelation.vue"),
        meta: { title: "添加关联", roles: [2] },
      },

      // 护工角色路由 (role_type=3)
      {
        path: "caregiver/task",
        name: "CaregiverTask",
        component: () => import("@/views/caregiver/Task.vue"),
        meta: { title: "任务首页", roles: [3] },
      },
      {
        path: "caregiver/service-record",
        name: "CaregiverServiceRecord",
        component: () => import("@/views/caregiver/ServiceRecord.vue"),
        meta: { title: "服务记录", roles: [3] },
      },

      // 管理员角色路由 (role_type=4)
      {
        path: "admin/dashboard",
        name: "AdminDashboard",
        component: () => import("@/views/admin/Dashboard.vue"),
        meta: { title: "首页", roles: [4] },
      },
      {
        path: "admin/data-count",
        name: "AdminDataCount",
        component: () => import("@/views/admin/DataCount.vue"),
        meta: { title: "数据统计详情", roles: [4] },
      },
      {
        path: "admin/user-manage",
        name: "AdminUserManage",
        component: () => import("@/views/admin/UserManage.vue"),
        meta: { title: "用户管理", roles: [4] },
      },
      {
        path: "admin/activity-manage",
        name: "AdminActivityManage",
        component: () => import("@/views/admin/ActivityManage.vue"),
        meta: { title: "活动管理", roles: [4] },
      },
      {
        path: "admin/system-config",
        name: "AdminSystemConfig",
        component: () => import("@/views/admin/SystemConfig.vue"),
        meta: { title: "系统配置", roles: [4] },
      },

      // 公共路由（管理员、家属、护工可访问）
      {
        path: "health",
        name: "Health",
        component: () => import("@/views/health/Index.vue"),
        meta: { title: "健康监测", roles: [2, 3, 4] },
      },
      {
        path: "emergency",
        name: "Emergency",
        component: () => import("@/views/emergency/Index.vue"),
        meta: { title: "紧急求助", roles: [2, 3, 4] },
      },
    ],
  },
  // 404页
  {
    path: "/:pathMatch(.*)*",
    redirect: "/403",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  strict: false,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log('路由守卫开始执行');
  console.log('路由守卫执行时的 to:', to);
  console.log('路由守卫执行时的 to.path:', to.path);
  console.log('路由守卫执行时的 to.fullPath:', to.fullPath);
  console.log('路由守卫执行时的 to.name:', to.name);
  
  const userStore = useUserStore();
  const token = userStore.token;
  console.log('路由守卫执行时的 token:', token);

  document.title = to.meta.title || "智慧养老平台";

  console.log('路由守卫执行时的 to.matched:', to.matched);
  console.log('路由守卫执行时的 to.matched.map(r => r.meta):', to.matched.map(r => r.meta));
  
  const requiresAuth = to.meta.requiresAuth !== undefined ? to.meta.requiresAuth : to.matched.some(record => record.meta.requiresAuth === true);
  console.log('路由守卫执行时的 requiresAuth:', requiresAuth);

  if (!requiresAuth) {
    console.log('不需要认证的路由，直接通过');
    next();
    return;
  }

  if (!token) {
    console.log('需要认证但未登录，跳转到登录页');
    next("/login");
    return;
  }

  const userRole = userStore.roleType;
  console.log('路由守卫执行时的 userRole:', userRole);
  console.log('路由守卫执行时的 to.meta.roles:', to.meta.roles);

  if (to.meta.roles) {
    const hasPermission = to.meta.roles.includes(userRole);
    console.log('路由守卫执行时的 hasPermission:', hasPermission);
    if (!hasPermission) {
      console.log('无权限访问，跳转到 403');
      next("/403");
      return;
    }
  }

  console.log('路由守卫执行完毕，允许访问');
  next();
});

export default router;