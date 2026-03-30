-- 社区智慧养老监护管理平台数据库初始化脚本
CREATE DATABASE IF NOT EXISTS elder_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE elder_care;

-- 1. 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  name VARCHAR(50) COMMENT '姓名',
  avatar VARCHAR(500) COMMENT '头像',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  role VARCHAR(20) NOT NULL COMMENT '角色: ADMIN/USER/CAREGIVER/COMMUNITY',
  gender VARCHAR(10) COMMENT '性别',
  age INT COMMENT '年龄',
  address VARCHAR(200) COMMENT '地址',
  id_card VARCHAR(20) COMMENT '身份证号',
  status INT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- 2. 老人档案表
DROP TABLE IF EXISTS elderly_profile;
CREATE TABLE elderly_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '关联用户ID',
  emergency_contact VARCHAR(50) COMMENT '紧急联系人',
  emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
  blood_type VARCHAR(10) COMMENT '血型',
  medical_history TEXT COMMENT '病史',
  allergy_history VARCHAR(500) COMMENT '过敏史',
  living_condition VARCHAR(50) COMMENT '居住情况: 独居/与家人同住/养老院',
  disability_level VARCHAR(20) COMMENT '失能等级: 正常/轻度/中度/重度',
  photo VARCHAR(500) COMMENT '照片',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '老人档案表';

-- 3. 健康数据表
DROP TABLE IF EXISTS health_data;
CREATE TABLE health_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  elderly_id BIGINT NOT NULL COMMENT '老人用户ID',
  heart_rate INT COMMENT '心率(次/分)',
  systolic_pressure INT COMMENT '收缩压(mmHg)',
  diastolic_pressure INT COMMENT '舒张压(mmHg)',
  blood_sugar DECIMAL(4,1) COMMENT '血糖(mmol/L)',
  body_temperature DECIMAL(3,1) COMMENT '体温(℃)',
  blood_oxygen INT COMMENT '血氧(%)',
  record_time DATETIME COMMENT '记录时间',
  recorder_id BIGINT COMMENT '记录人ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '健康数据表';

-- 4. 健康预警表
DROP TABLE IF EXISTS health_alert;
CREATE TABLE health_alert (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  elderly_id BIGINT NOT NULL COMMENT '老人用户ID',
  alert_type VARCHAR(50) COMMENT '预警类型: 心率异常/血压异常/血糖异常/体温异常/血氧异常',
  alert_level VARCHAR(20) COMMENT '预警级别: INFO/WARNING/DANGER',
  alert_content VARCHAR(500) COMMENT '预警内容',
  status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/PROCESSED/IGNORED',
  processor_id BIGINT COMMENT '处理人ID',
  process_time DATETIME COMMENT '处理时间',
  process_note VARCHAR(500) COMMENT '处理备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '健康预警表';

-- 5. 紧急救助记录表
DROP TABLE IF EXISTS emergency_record;
CREATE TABLE emergency_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  elderly_id BIGINT NOT NULL COMMENT '老人用户ID',
  emergency_type VARCHAR(50) COMMENT '紧急类型: 跌倒/突发疾病/其他',
  description VARCHAR(500) COMMENT '描述',
  location VARCHAR(200) COMMENT '位置',
  status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/PROCESSING/RESOLVED',
  responder_id BIGINT COMMENT '响应人ID',
  response_time DATETIME COMMENT '响应时间',
  resolve_time DATETIME COMMENT '解决时间',
  resolve_note VARCHAR(500) COMMENT '解决备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '紧急救助记录表';

-- 6. 服务类别表
DROP TABLE IF EXISTS service_category;
CREATE TABLE service_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '类别名称',
  icon VARCHAR(100) COMMENT '图标',
  description VARCHAR(200) COMMENT '描述',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status INT DEFAULT 1 COMMENT '状态',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '服务类别表';

-- 7. 服务项目表
DROP TABLE IF EXISTS service_item;
CREATE TABLE service_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL COMMENT '类别ID',
  name VARCHAR(100) NOT NULL COMMENT '服务名称',
  description TEXT COMMENT '服务描述',
  image VARCHAR(500) COMMENT '服务图片',
  price DECIMAL(10,2) COMMENT '价格',
  duration INT COMMENT '时长(分钟)',
  status INT DEFAULT 1 COMMENT '状态',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '服务项目表';

-- 8. 服务预约表
DROP TABLE IF EXISTS service_appointment;
CREATE TABLE service_appointment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  elderly_id BIGINT NOT NULL COMMENT '老人用户ID',
  service_item_id BIGINT NOT NULL COMMENT '服务项目ID',
  caregiver_id BIGINT COMMENT '护工ID',
  appointment_time DATETIME COMMENT '预约时间',
  status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/CONFIRMED/IN_PROGRESS/COMPLETED/CANCELLED',
  note VARCHAR(500) COMMENT '备注',
  rating INT COMMENT '评分1-5',
  rating_content VARCHAR(500) COMMENT '评价内容',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '服务预约表';

-- 9. 护工分配表
DROP TABLE IF EXISTS caregiver_assignment;
CREATE TABLE caregiver_assignment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  caregiver_id BIGINT NOT NULL COMMENT '护工ID',
  elderly_id BIGINT NOT NULL COMMENT '老人用户ID',
  start_date DATE COMMENT '开始日期',
  end_date DATE COMMENT '结束日期',
  status INT DEFAULT 1 COMMENT '状态: 1有效 0无效',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '护工分配表';

-- 10. 公告表
DROP TABLE IF EXISTS announcement;
CREATE TABLE announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT COMMENT '内容',
  image VARCHAR(500) COMMENT '图片',
  publisher_id BIGINT COMMENT '发布人ID',
  type VARCHAR(20) COMMENT '类型: NOTICE/POLICY/HEALTH_TIP',
  status INT DEFAULT 1 COMMENT '状态: 1发布 0草稿',
  top INT DEFAULT 0 COMMENT '是否置顶',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '公告表';

-- 11. 活动表
DROP TABLE IF EXISTS activity;
CREATE TABLE activity (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL COMMENT '标题',
  description TEXT COMMENT '描述',
  image VARCHAR(500) COMMENT '图片',
  location VARCHAR(200) COMMENT '地点',
  start_time DATETIME COMMENT '开始时间',
  end_time DATETIME COMMENT '结束时间',
  max_participants INT COMMENT '最大人数',
  current_participants INT DEFAULT 0 COMMENT '当前报名人数',
  organizer_id BIGINT COMMENT '组织者ID',
  status VARCHAR(20) DEFAULT 'UPCOMING' COMMENT '状态: UPCOMING/ONGOING/ENDED/CANCELLED',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '活动表';

-- 12. 活动报名表
DROP TABLE IF EXISTS activity_registration;
CREATE TABLE activity_registration (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL COMMENT '活动ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  status VARCHAR(20) DEFAULT 'REGISTERED' COMMENT '状态: REGISTERED/CANCELLED/ATTENDED',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '活动报名表';

-- 13. 意见反馈表
DROP TABLE IF EXISTS feedback;
CREATE TABLE feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  type VARCHAR(20) COMMENT '类型: SUGGESTION/COMPLAINT/PRAISE',
  content TEXT COMMENT '内容',
  image VARCHAR(500) COMMENT '图片',
  reply TEXT COMMENT '回复',
  reply_by BIGINT COMMENT '回复人ID',
  reply_time DATETIME COMMENT '回复时间',
  status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/REPLIED',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '意见反馈表';

-- 14. 排班表
DROP TABLE IF EXISTS work_schedule;
CREATE TABLE work_schedule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  caregiver_id BIGINT NOT NULL COMMENT '护工ID',
  work_date DATE NOT NULL COMMENT '工作日期',
  shift_type VARCHAR(20) COMMENT '班次: MORNING/AFTERNOON/NIGHT',
  status VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '状态: SCHEDULED/ON_DUTY/OFF_DUTY',
  note VARCHAR(200) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '排班表';

-- 15. 服务记录表
DROP TABLE IF EXISTS service_record;
CREATE TABLE service_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  appointment_id BIGINT COMMENT '关联预约ID',
  caregiver_id BIGINT NOT NULL COMMENT '护工ID',
  elderly_id BIGINT NOT NULL COMMENT '老人ID',
  service_content VARCHAR(500) COMMENT '服务内容',
  service_time DATETIME COMMENT '服务时间',
  duration INT COMMENT '时长(分钟)',
  note VARCHAR(500) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '服务记录表';

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
