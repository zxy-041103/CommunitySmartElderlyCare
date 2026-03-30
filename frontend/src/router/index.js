import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  // ========== 管理员后台 ==========
  {
    path: '/admin', component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '数据统计', role: 'ADMIN' } },
      { path: 'user', name: 'AdminUser', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理', role: 'ADMIN' } },
      { path: 'elderlyProfile', name: 'AdminElderlyProfile', component: () => import('../views/admin/ElderlyProfile.vue'), meta: { title: '老人档案', role: 'ADMIN' } },
      { path: 'healthData', name: 'AdminHealthData', component: () => import('../views/admin/HealthData.vue'), meta: { title: '健康数据', role: 'ADMIN' } },
      { path: 'healthAlert', name: 'AdminHealthAlert', component: () => import('../views/admin/HealthAlert.vue'), meta: { title: '健康预警', role: 'ADMIN' } },
      { path: 'emergency', name: 'AdminEmergency', component: () => import('../views/admin/EmergencyRecord.vue'), meta: { title: '紧急救助', role: 'ADMIN' } },
      { path: 'serviceCategory', name: 'AdminServiceCategory', component: () => import('../views/admin/ServiceCategory.vue'), meta: { title: '服务类别', role: 'ADMIN' } },
      { path: 'serviceItem', name: 'AdminServiceItem', component: () => import('../views/admin/ServiceItem.vue'), meta: { title: '服务项目', role: 'ADMIN' } },
      { path: 'appointment', name: 'AdminAppointment', component: () => import('../views/admin/Appointment.vue'), meta: { title: '预约管理', role: 'ADMIN' } },
      { path: 'assignment', name: 'AdminAssignment', component: () => import('../views/admin/Assignment.vue'), meta: { title: '护工分配', role: 'ADMIN' } },
      { path: 'announcement', name: 'AdminAnnouncement', component: () => import('../views/admin/Announcement.vue'), meta: { title: '公告管理', role: 'ADMIN' } },
      { path: 'activity', name: 'AdminActivity', component: () => import('../views/admin/Activity.vue'), meta: { title: '活动管理', role: 'ADMIN' } },
      { path: 'feedback', name: 'AdminFeedback', component: () => import('../views/admin/Feedback.vue'), meta: { title: '意见反馈', role: 'ADMIN' } },
      { path: 'workSchedule', name: 'AdminWorkSchedule', component: () => import('../views/admin/WorkSchedule.vue'), meta: { title: '排班管理', role: 'ADMIN' } }
    ]
  },
  // ========== 用户前台 ==========
  {
    path: '/user', component: () => import('../layouts/UserLayout.vue'),
    children: [
      { path: '', redirect: '/user/home' },
      { path: 'home', name: 'UserHome', component: () => import('../views/user/Home.vue'), meta: { title: '首页', role: 'USER' } },
      { path: 'health', name: 'UserHealth', component: () => import('../views/user/Health.vue'), meta: { title: '健康中心', role: 'USER' } },
      { path: 'service', name: 'UserService', component: () => import('../views/user/ServiceCenter.vue'), meta: { title: '服务中心', role: 'USER' } },
      { path: 'appointment', name: 'UserAppointment', component: () => import('../views/user/MyAppointment.vue'), meta: { title: '我的预约', role: 'USER' } },
      { path: 'emergency', name: 'UserEmergency', component: () => import('../views/user/Emergency.vue'), meta: { title: '紧急求助', role: 'USER' } },
      { path: 'activity', name: 'UserActivity', component: () => import('../views/user/ActivityCenter.vue'), meta: { title: '活动中心', role: 'USER' } },
      { path: 'profile', name: 'UserProfile', component: () => import('../views/user/Profile.vue'), meta: { title: '个人中心', role: 'USER' } },
      { path: 'feedback', name: 'UserFeedback', component: () => import('../views/user/MyFeedback.vue'), meta: { title: '意见反馈', role: 'USER' } }
    ]
  },
  // ========== 护工端 ==========
  {
    path: '/caregiver', component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/caregiver/dashboard' },
      { path: 'dashboard', name: 'CaregiverDashboard', component: () => import('../views/caregiver/Dashboard.vue'), meta: { title: '工作台', role: 'CAREGIVER' } },
      { path: 'myElderly', name: 'CaregiverMyElderly', component: () => import('../views/caregiver/MyElderly.vue'), meta: { title: '我的老人', role: 'CAREGIVER' } },
      { path: 'healthMonitor', name: 'CaregiverHealthMonitor', component: () => import('../views/caregiver/HealthMonitor.vue'), meta: { title: '健康监测', role: 'CAREGIVER' } },
      { path: 'serviceRecord', name: 'CaregiverServiceRecord', component: () => import('../views/caregiver/ServiceRecord.vue'), meta: { title: '服务记录', role: 'CAREGIVER' } },
      { path: 'mySchedule', name: 'CaregiverMySchedule', component: () => import('../views/caregiver/MySchedule.vue'), meta: { title: '我的排班', role: 'CAREGIVER' } },
      { path: 'emergencyResponse', name: 'CaregiverEmergencyResponse', component: () => import('../views/caregiver/EmergencyResponse.vue'), meta: { title: '紧急响应', role: 'CAREGIVER' } }
    ]
  },
  // ========== 社区服务人员端 ==========
  {
    path: '/community', component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/community/dashboard' },
      { path: 'dashboard', name: 'CommunityDashboard', component: () => import('../views/community/Dashboard.vue'), meta: { title: '工作台', role: 'COMMUNITY' } },
      { path: 'appointmentProcess', name: 'CommunityAppointment', component: () => import('../views/community/AppointmentProcess.vue'), meta: { title: '预约处理', role: 'COMMUNITY' } },
      { path: 'activity', name: 'CommunityActivity', component: () => import('../views/community/ActivityManage.vue'), meta: { title: '活动管理', role: 'COMMUNITY' } },
      { path: 'announcement', name: 'CommunityAnnouncement', component: () => import('../views/community/AnnouncementManage.vue'), meta: { title: '公告管理', role: 'COMMUNITY' } },
      { path: 'feedback', name: 'CommunityFeedback', component: () => import('../views/community/FeedbackHandle.vue'), meta: { title: '反馈处理', role: 'COMMUNITY' } },
      { path: 'serviceStats', name: 'CommunityServiceStats', component: () => import('../views/community/ServiceStats.vue'), meta: { title: '服务统计', role: 'COMMUNITY' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
