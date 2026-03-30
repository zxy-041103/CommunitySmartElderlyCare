USE elder_care;

-- ========== 测试数据 ==========

-- 用户数据 (密码均为123456, MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO sys_user (username, password, name, avatar, phone, email, role, gender, age, address, id_card, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', '13800000001', 'admin@eldercare.com', 'ADMIN', '男', 35, '北京市朝阳区社区服务中心', '110101199001011234', 1),
('test1', 'e10adc3949ba59abbe56e057f20f883e', '张大爷', 'https://api.dicebear.com/7.x/avataaars/svg?seed=elderly1', '13800000002', 'test1@eldercare.com', 'USER', '男', 72, '北京市朝阳区幸福小区1号楼301', '110101195301051234', 1),
('test2', 'e10adc3949ba59abbe56e057f20f883e', '李护士', 'https://api.dicebear.com/7.x/avataaars/svg?seed=caregiver1', '13800000003', 'test2@eldercare.com', 'CAREGIVER', '女', 28, '北京市朝阳区社区卫生中心', '110101199801031234', 1),
('test3', 'e10adc3949ba59abbe56e057f20f883e', '王社工', 'https://api.dicebear.com/7.x/avataaars/svg?seed=community1', '13800000004', 'test3@eldercare.com', 'COMMUNITY', '女', 32, '北京市朝阳区社区服务站', '110101199401071234', 1),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '王奶奶', 'https://api.dicebear.com/7.x/avataaars/svg?seed=elderly2', '13800000005', 'user2@eldercare.com', 'USER', '女', 68, '北京市朝阳区幸福小区2号楼502', '110101195801121234', 1),
('user3', 'e10adc3949ba59abbe56e057f20f883e', '刘大爷', 'https://api.dicebear.com/7.x/avataaars/svg?seed=elderly3', '13800000006', 'user3@eldercare.com', 'USER', '男', 75, '北京市朝阳区和平小区3号楼102', '110101195101081234', 1),
('caregiver2', 'e10adc3949ba59abbe56e057f20f883e', '陈护工', 'https://api.dicebear.com/7.x/avataaars/svg?seed=caregiver2', '13800000007', 'cg2@eldercare.com', 'CAREGIVER', '男', 35, '北京市朝阳区社区卫生中心', '110101199101091234', 1),
('community2', 'e10adc3949ba59abbe56e057f20f883e', '赵社工', 'https://api.dicebear.com/7.x/avataaars/svg?seed=community2', '13800000008', 'cm2@eldercare.com', 'COMMUNITY', '男', 30, '北京市朝阳区社区服务站', '110101199601101234', 1);

-- 老人档案数据
INSERT INTO elderly_profile (user_id, emergency_contact, emergency_phone, blood_type, medical_history, allergy_history, living_condition, disability_level, photo) VALUES
(2, '张小明', '13900000001', 'A型', '高血压病史10年，2型糖尿病5年', '青霉素过敏', '独居', '轻度', 'https://picsum.photos/seed/elder1/500/500'),
(5, '王小红', '13900000002', 'B型', '冠心病3年，骨质疏松', '磺胺类药物过敏', '与家人同住', '正常', 'https://picsum.photos/seed/elder2/500/500'),
(6, '刘小刚', '13900000003', 'O型', '慢性支气管炎8年，前列腺增生', '无', '独居', '中度', 'https://picsum.photos/seed/elder3/500/500');

-- 健康数据 (张大爷-最近7天)
INSERT INTO health_data (elderly_id, heart_rate, systolic_pressure, diastolic_pressure, blood_sugar, body_temperature, blood_oxygen, record_time, recorder_id) VALUES
(2, 72, 135, 85, 6.2, 36.5, 97, '2026-03-21 08:00:00', 3),
(2, 75, 140, 88, 6.5, 36.6, 96, '2026-03-22 08:00:00', 3),
(2, 68, 130, 82, 5.8, 36.4, 98, '2026-03-23 08:00:00', 3),
(2, 78, 145, 92, 7.1, 36.7, 95, '2026-03-24 08:00:00', 3),
(2, 70, 132, 84, 6.0, 36.5, 97, '2026-03-25 08:00:00', 3),
(2, 73, 138, 86, 6.3, 36.6, 96, '2026-03-26 08:00:00', 3),
(2, 76, 142, 90, 6.8, 36.8, 95, '2026-03-27 08:00:00', 3),
(5, 65, 125, 78, 5.5, 36.3, 98, '2026-03-21 09:00:00', 3),
(5, 68, 128, 80, 5.7, 36.4, 97, '2026-03-22 09:00:00', 3),
(5, 63, 122, 76, 5.3, 36.2, 99, '2026-03-23 09:00:00', 3),
(5, 70, 130, 82, 5.9, 36.5, 97, '2026-03-24 09:00:00', 7),
(5, 66, 126, 79, 5.6, 36.4, 98, '2026-03-25 09:00:00', 7),
(6, 80, 155, 95, 8.2, 36.9, 93, '2026-03-21 08:30:00', 7),
(6, 82, 158, 98, 8.5, 37.0, 92, '2026-03-22 08:30:00', 7),
(6, 78, 150, 92, 7.8, 36.8, 94, '2026-03-23 08:30:00', 7),
(6, 85, 162, 100, 9.0, 37.1, 91, '2026-03-24 08:30:00', 7),
(6, 79, 152, 94, 8.0, 36.9, 93, '2026-03-25 08:30:00', 7);

-- 健康预警数据
INSERT INTO health_alert (elderly_id, alert_type, alert_level, alert_content, status, processor_id, process_time, process_note) VALUES
(2, '血压异常', 'WARNING', '张大爷收缩压145mmHg，超过正常范围(90-140)', 'PROCESSED', 3, '2026-03-24 09:30:00', '已通知家属，建议调整用药'),
(6, '血糖异常', 'DANGER', '刘大爷血糖9.0mmol/L，严重偏高(正常3.9-6.1)', 'PROCESSED', 7, '2026-03-24 10:00:00', '已安排就医检查'),
(6, '血压异常', 'DANGER', '刘大爷收缩压162mmHg，严重偏高', 'PENDING', NULL, NULL, NULL),
(2, '心率异常', 'INFO', '张大爷心率78次/分，略高于日常水平', 'IGNORED', 3, '2026-03-24 12:00:00', '属正常波动范围'),
(6, '血氧异常', 'WARNING', '刘大爷血氧91%，低于正常值(95%以上)', 'PENDING', NULL, NULL, NULL);

-- 紧急救助记录
INSERT INTO emergency_record (elderly_id, emergency_type, description, location, status, responder_id, response_time, resolve_time, resolve_note, create_time) VALUES
(2, '跌倒', '在卫生间滑倒，右手臂疼痛', '家中卫生间', 'RESOLVED', 3, '2026-03-20 15:05:00', '2026-03-20 15:30:00', '护工到达现场，检查无骨折，进行了冰敷处理', '2026-03-20 15:00:00'),
(6, '突发疾病', '感觉胸闷气短，呼吸困难', '家中客厅', 'RESOLVED', 7, '2026-03-22 10:05:00', '2026-03-22 11:00:00', '已送往医院急诊，诊断为支气管炎急性发作', '2026-03-22 10:00:00'),
(5, '其他', '家中燃气报警器响，感觉头晕', '家中厨房', 'RESOLVED', 3, '2026-03-25 18:10:00', '2026-03-25 18:40:00', '已关闭燃气，开窗通风，老人无大碍', '2026-03-25 18:05:00');

-- 服务类别
INSERT INTO service_category (name, icon, description, sort_order) VALUES
('生活照料', 'House', '日常生活起居照料服务', 1),
('医疗护理', 'FirstAidKit', '基础医疗和护理服务', 2),
('康复理疗', 'Monitor', '康复训练和理疗服务', 3),
('精神慰藉', 'ChatDotRound', '心理关怀和精神陪伴', 4),
('家政服务', 'Brush', '家庭清洁维修等服务', 5),
('助餐服务', 'Bowl', '营养配餐和送餐服务', 6);

-- 服务项目
INSERT INTO service_item (category_id, name, description, image, price, duration) VALUES
(1, '日常起居照料', '协助老人洗漱、穿衣、如厕等日常生活照料', 'https://picsum.photos/seed/care1/800/500', 80.00, 120),
(1, '陪伴散步', '陪伴老人户外散步锻炼，确保安全', 'https://picsum.photos/seed/walk1/800/500', 50.00, 60),
(2, '血压血糖监测', '定期测量血压、血糖等基础健康指标', 'https://picsum.photos/seed/health1/800/500', 30.00, 30),
(2, '伤口护理', '伤口清洁、换药等基础护理服务', 'https://picsum.photos/seed/nurse1/800/500', 60.00, 45),
(2, '用药提醒', '按时提醒老人服药，记录用药情况', 'https://picsum.photos/seed/med1/800/500', 20.00, 15),
(3, '康复按摩', '专业康复按摩，缓解肌肉疲劳', 'https://picsum.photos/seed/massage1/800/500', 100.00, 60),
(3, '关节活动训练', '关节活动度训练，预防关节僵硬', 'https://picsum.photos/seed/rehab1/800/500', 80.00, 45),
(4, '心理疏导', '专业心理咨询师提供心理疏导服务', 'https://picsum.photos/seed/mind1/800/500', 120.00, 60),
(4, '陪伴聊天', '陪老人聊天、下棋、看电视等', 'https://picsum.photos/seed/chat1/800/500', 40.00, 60),
(5, '家庭清洁', '房间打扫、整理、消毒等', 'https://picsum.photos/seed/clean1/800/500', 60.00, 90),
(5, '衣物洗涤', '衣物清洗、晾晒、整理', 'https://picsum.photos/seed/laundry1/800/500', 40.00, 60),
(6, '营养配餐', '根据老人身体状况定制营养餐', 'https://picsum.photos/seed/food1/800/500', 35.00, 30),
(6, '送餐上门', '按时将餐食送至老人家中', 'https://picsum.photos/seed/delivery1/800/500', 15.00, 15);

-- 服务预约
INSERT INTO service_appointment (elderly_id, service_item_id, caregiver_id, appointment_time, status, note, rating, rating_content) VALUES
(2, 1, 3, '2026-03-20 09:00:00', 'COMPLETED', '需要帮助洗漱和整理房间', 5, '李护士非常细心耐心，服务很好'),
(2, 3, 3, '2026-03-22 10:00:00', 'COMPLETED', '定期血压血糖检测', 4, '很专业，检测仔细'),
(5, 6, 7, '2026-03-23 14:00:00', 'COMPLETED', '腰部酸痛需要按摩', 5, '按摩手法很好，腰痛缓解了'),
(6, 3, 7, '2026-03-24 09:00:00', 'COMPLETED', '检测血压和血糖', 4, '服务及时'),
(2, 9, 3, '2026-03-28 15:00:00', 'CONFIRMED', '想找人下棋聊天', NULL, NULL),
(5, 10, 7, '2026-03-29 09:00:00', 'PENDING', '家里需要打扫一下', NULL, NULL),
(6, 12, 7, '2026-03-29 11:00:00', 'PENDING', '需要低盐低糖餐', NULL, NULL),
(2, 5, 3, '2026-03-30 08:00:00', 'PENDING', '提醒吃降压药和降糖药', NULL, NULL);

-- 护工分配
INSERT INTO caregiver_assignment (caregiver_id, elderly_id, start_date, end_date, status) VALUES
(3, 2, '2026-01-01', '2026-12-31', 1),
(3, 5, '2026-01-01', '2026-12-31', 1),
(7, 5, '2026-01-01', '2026-06-30', 1),
(7, 6, '2026-01-01', '2026-12-31', 1);

-- 公告
INSERT INTO announcement (title, content, image, publisher_id, type, status, top) VALUES
('春季老年人健康养生指南', '春季是养生的好时节，老年朋友们要注意以下几点：\n1. 适当增加户外活动，每天散步30分钟\n2. 饮食清淡，多吃蔬菜水果\n3. 注意保暖，预防感冒\n4. 保持充足睡眠，每天7-8小时', 'https://picsum.photos/seed/spring1/800/400', 1, 'HEALTH_TIP', 1, 1),
('社区养老服务中心开放时间调整通知', '尊敬的居民朋友们：\n因设施升级改造，社区养老服务中心将于4月1日起调整开放时间为每天8:00-20:00，周末照常开放。给您带来不便，敬请谅解。', 'https://picsum.photos/seed/notice1/800/400', 4, 'NOTICE', 1, 0),
('免费体检活动通知', '朝阳区社区卫生服务中心将于4月5日-4月10日开展60岁以上老年人免费健康体检活动，包括血常规、尿常规、B超、心电图等项目。请携带身份证和医保卡前来。', 'https://picsum.photos/seed/checkup1/800/400', 1, 'NOTICE', 1, 1),
('关于加强老年人防诈骗意识的通知', '近期社区内出现多起针对老年人的电信诈骗案件，请大家提高警惕：\n1. 不要轻信陌生来电\n2. 不要随意转账汇款\n3. 遇到可疑情况及时报警\n4. 有疑问可咨询社区工作人员', NULL, 4, 'POLICY', 1, 0);

-- 活动
INSERT INTO activity (title, description, image, location, start_time, end_time, max_participants, current_participants, organizer_id, status) VALUES
('太极拳晨练活动', '每周一三五早上7:00-8:00，由专业教练带领大家练习太极拳，强身健体。适合所有年龄段的老年朋友参加。', 'https://picsum.photos/seed/taichi1/800/500', '社区活动广场', '2026-04-01 07:00:00', '2026-04-30 08:00:00', 30, 12, 4, 'UPCOMING'),
('老年书画班', '每周二四下午2:00-4:00，邀请书画老师教授国画和书法基础，感受传统文化魅力。', 'https://picsum.photos/seed/painting1/800/500', '社区文化中心', '2026-04-01 14:00:00', '2026-06-30 16:00:00', 20, 8, 4, 'UPCOMING'),
('春季健康讲座', '邀请三甲医院专家讲解春季常见疾病预防、合理用药等健康知识。', 'https://picsum.photos/seed/lecture1/800/500', '社区多功能厅', '2026-04-05 09:00:00', '2026-04-05 11:00:00', 50, 25, 8, 'UPCOMING'),
('重阳节文艺汇演', '社区老年文艺队表演节目，包括合唱、舞蹈、小品等，欢迎观看。', 'https://picsum.photos/seed/show1/800/500', '社区大礼堂', '2026-03-15 14:00:00', '2026-03-15 17:00:00', 100, 68, 4, 'ENDED');

-- 活动报名
INSERT INTO activity_registration (activity_id, user_id, status) VALUES
(1, 2, 'REGISTERED'), (1, 5, 'REGISTERED'), (1, 6, 'REGISTERED'),
(2, 5, 'REGISTERED'), (2, 2, 'REGISTERED'),
(3, 2, 'REGISTERED'), (3, 5, 'REGISTERED'), (3, 6, 'REGISTERED'),
(4, 2, 'ATTENDED'), (4, 5, 'ATTENDED');

-- 意见反馈
INSERT INTO feedback (user_id, type, content, image, reply, reply_by, reply_time, status) VALUES
(2, 'SUGGESTION', '建议增加中医理疗服务项目，很多老年人有这方面需求', NULL, '感谢您的建议，我们正在对接中医理疗资源，预计下月上线', 4, '2026-03-26 10:00:00', 'REPLIED'),
(5, 'PRAISE', '社区的护工服务非常专业，特别是陈护工，每次都很耐心', NULL, '感谢您的认可，我们会继续努力提供优质服务', 4, '2026-03-25 14:00:00', 'REPLIED'),
(6, 'COMPLAINT', '送餐时间经常不准时，希望能改善', NULL, NULL, NULL, NULL, 'PENDING'),
(2, 'SUGGESTION', '希望能增加线上视频问诊功能', NULL, NULL, NULL, NULL, 'PENDING');

-- 排班
INSERT INTO work_schedule (caregiver_id, work_date, shift_type, status, note) VALUES
(3, '2026-03-28', 'MORNING', 'SCHEDULED', '负责幸福小区片区'),
(3, '2026-03-28', 'AFTERNOON', 'SCHEDULED', NULL),
(7, '2026-03-28', 'MORNING', 'SCHEDULED', '负责和平小区片区'),
(7, '2026-03-28', 'NIGHT', 'SCHEDULED', '值班'),
(3, '2026-03-29', 'MORNING', 'SCHEDULED', NULL),
(3, '2026-03-29', 'AFTERNOON', 'SCHEDULED', NULL),
(7, '2026-03-29', 'AFTERNOON', 'SCHEDULED', NULL),
(7, '2026-03-30', 'MORNING', 'SCHEDULED', NULL),
(3, '2026-03-30', 'NIGHT', 'SCHEDULED', '值班');

-- 服务记录
INSERT INTO service_record (appointment_id, caregiver_id, elderly_id, service_content, service_time, duration, note) VALUES
(1, 3, 2, '协助张大爷洗漱、整理卧室和客厅', '2026-03-20 09:00:00', 120, '老人精神状态良好'),
(2, 3, 2, '测量血压140/88mmHg，血糖6.5mmol/L', '2026-03-22 10:00:00', 30, '血压略偏高，已记录'),
(3, 7, 5, '为王奶奶进行腰部按摩和热敷', '2026-03-23 14:00:00', 60, '按摩后明显缓解'),
(4, 7, 6, '测量血压162/100mmHg，血糖9.0mmol/L', '2026-03-24 09:00:00', 30, '指标偏高，已上报预警');
