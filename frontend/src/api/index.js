import request from './request'

// ========== 认证相关 ==========
export const login = (data) => request.post('/api/auth/login', data)
export const getUserInfo = () => request.get('/api/auth/info')
export const updatePassword = (data) => request.put('/api/auth/password', data)
export const updateProfile = (data) => request.put('/api/auth/profile', data)

// ========== 用户管理 ==========
export const userPage = (params) => request.get('/api/user/page', { params })
export const userList = (params) => request.get('/api/user/list', { params })
export const userGet = (id) => request.get(`/api/user/${id}`)
export const userAdd = (data) => request.post('/api/user', data)
export const userUpdate = (data) => request.put('/api/user', data)
export const userDelete = (id) => request.delete(`/api/user/${id}`)
export const userResetPassword = (id) => request.put(`/api/user/resetPassword/${id}`)

// ========== 老人档案 ==========
export const elderlyProfilePage = (params) => request.get('/api/elderlyProfile/page', { params })
export const elderlyProfileGet = (id) => request.get(`/api/elderlyProfile/${id}`)
export const elderlyProfileByUser = (userId) => request.get(`/api/elderlyProfile/byUserId/${userId}`)
export const elderlyProfileAdd = (data) => request.post('/api/elderlyProfile', data)
export const elderlyProfileUpdate = (data) => request.put('/api/elderlyProfile', data)
export const elderlyProfileDelete = (id) => request.delete(`/api/elderlyProfile/${id}`)
export const elderlyProfileMy = () => request.get('/api/elderlyProfile/my')
export const elderlyProfileSaveMy = (data) => request.post('/api/elderlyProfile/my', data)

// ========== 健康数据 ==========
export const healthDataPage = (params) => request.get('/api/healthData/page', { params })
export const healthDataList = (elderlyId, days) => request.get(`/api/healthData/list/${elderlyId}`, { params: { days } })
export const healthDataLatest = (elderlyId) => request.get(`/api/healthData/latest/${elderlyId}`)
export const healthDataMy = (days) => request.get('/api/healthData/my', { params: { days } })
export const healthDataAdd = (data) => request.post('/api/healthData', data)
export const healthDataUpdate = (data) => request.put('/api/healthData', data)
export const healthDataDelete = (id) => request.delete(`/api/healthData/${id}`)

// ========== 健康预警 ==========
export const healthAlertPage = (params) => request.get('/api/healthAlert/page', { params })
export const healthAlertMy = () => request.get('/api/healthAlert/my')
export const healthAlertPendingCount = () => request.get('/api/healthAlert/pending/count')
export const healthAlertAdd = (data) => request.post('/api/healthAlert', data)
export const healthAlertProcess = (id, data) => request.put(`/api/healthAlert/process/${id}`, data)
export const healthAlertDelete = (id) => request.delete(`/api/healthAlert/${id}`)

// ========== 紧急救助 ==========
export const emergencyPage = (params) => request.get('/api/emergency/page', { params })
export const emergencyMy = () => request.get('/api/emergency/my')
export const emergencyPending = () => request.get('/api/emergency/pending')
export const emergencyPendingCount = () => request.get('/api/emergency/pending/count')
export const emergencyAdd = (data) => request.post('/api/emergency', data)
export const emergencyRespond = (id) => request.put(`/api/emergency/respond/${id}`)
export const emergencyResolve = (id, data) => request.put(`/api/emergency/resolve/${id}`, data)
export const emergencyDelete = (id) => request.delete(`/api/emergency/${id}`)

// ========== 服务类别 ==========
export const serviceCategoryPage = (params) => request.get('/api/serviceCategory/page', { params })
export const serviceCategoryList = () => request.get('/api/serviceCategory/list')
export const serviceCategoryAdd = (data) => request.post('/api/serviceCategory', data)
export const serviceCategoryUpdate = (data) => request.put('/api/serviceCategory', data)
export const serviceCategoryDelete = (id) => request.delete(`/api/serviceCategory/${id}`)

// ========== 服务项目 ==========
export const serviceItemPage = (params) => request.get('/api/serviceItem/page', { params })
export const serviceItemList = (params) => request.get('/api/serviceItem/list', { params })
export const serviceItemGet = (id) => request.get(`/api/serviceItem/${id}`)
export const serviceItemAdd = (data) => request.post('/api/serviceItem', data)
export const serviceItemUpdate = (data) => request.put('/api/serviceItem', data)
export const serviceItemDelete = (id) => request.delete(`/api/serviceItem/${id}`)

// ========== 服务预约 ==========
export const appointmentPage = (params) => request.get('/api/appointment/page', { params })
export const appointmentMy = (params) => request.get('/api/appointment/my', { params })
export const appointmentAdd = (data) => request.post('/api/appointment', data)
export const appointmentUpdate = (data) => request.put('/api/appointment', data)
export const appointmentUpdateStatus = (id, data) => request.put(`/api/appointment/status/${id}`, data)
export const appointmentRate = (id, data) => request.put(`/api/appointment/rate/${id}`, data)
export const appointmentCancel = (id) => request.put(`/api/appointment/cancel/${id}`)
export const appointmentDelete = (id) => request.delete(`/api/appointment/${id}`)

// ========== 护工分配 ==========
export const assignmentPage = (params) => request.get('/api/assignment/page', { params })
export const assignmentMy = () => request.get('/api/assignment/my')
export const assignmentMyElderly = () => request.get('/api/assignment/myElderly')
export const assignmentAdd = (data) => request.post('/api/assignment', data)
export const assignmentUpdate = (data) => request.put('/api/assignment', data)
export const assignmentDelete = (id) => request.delete(`/api/assignment/${id}`)

// ========== 公告 ==========
export const announcementPage = (params) => request.get('/api/announcement/page', { params })
export const announcementPublished = (params) => request.get('/api/announcement/published', { params })
export const announcementGet = (id) => request.get(`/api/announcement/${id}`)
export const announcementAdd = (data) => request.post('/api/announcement', data)
export const announcementUpdate = (data) => request.put('/api/announcement', data)
export const announcementDelete = (id) => request.delete(`/api/announcement/${id}`)

// ========== 活动 ==========
export const activityPage = (params) => request.get('/api/activity/page', { params })
export const activityPublished = (params) => request.get('/api/activity/published', { params })
export const activityGet = (id) => request.get(`/api/activity/${id}`)
export const activityAdd = (data) => request.post('/api/activity', data)
export const activityUpdate = (data) => request.put('/api/activity', data)
export const activityDelete = (id) => request.delete(`/api/activity/${id}`)
export const activityRegister = (id) => request.post(`/api/activity/register/${id}`)
export const activityCancelReg = (id) => request.post(`/api/activity/cancel/${id}`)
export const activityRegistrations = (id) => request.get(`/api/activity/registrations/${id}`)
export const activityMyRegistrations = () => request.get('/api/activity/myRegistrations')

// ========== 意见反馈 ==========
export const feedbackPage = (params) => request.get('/api/feedback/page', { params })
export const feedbackMy = () => request.get('/api/feedback/my')
export const feedbackAdd = (data) => request.post('/api/feedback', data)
export const feedbackReply = (id, data) => request.put(`/api/feedback/reply/${id}`, data)
export const feedbackDelete = (id) => request.delete(`/api/feedback/${id}`)

// ========== 排班 ==========
export const workSchedulePage = (params) => request.get('/api/workSchedule/page', { params })
export const workScheduleMy = (params) => request.get('/api/workSchedule/my', { params })
export const workScheduleAdd = (data) => request.post('/api/workSchedule', data)
export const workScheduleUpdate = (data) => request.put('/api/workSchedule', data)
export const workScheduleDelete = (id) => request.delete(`/api/workSchedule/${id}`)

// ========== 服务记录 ==========
export const serviceRecordPage = (params) => request.get('/api/serviceRecord/page', { params })
export const serviceRecordMy = () => request.get('/api/serviceRecord/my')
export const serviceRecordAdd = (data) => request.post('/api/serviceRecord', data)
export const serviceRecordUpdate = (data) => request.put('/api/serviceRecord', data)
export const serviceRecordDelete = (id) => request.delete(`/api/serviceRecord/${id}`)

// ========== 仪表盘 ==========
export const dashboardStats = () => request.get('/api/dashboard/stats')

// ========== 文件上传 ==========
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ========== 语音识别 ==========
export const voiceRecognize = (data) => request.post('/api/voice/recognize', data)
export const voiceStatus = () => request.get('/api/voice/status')
