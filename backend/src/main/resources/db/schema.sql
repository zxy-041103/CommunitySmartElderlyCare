DROP TABLE IF EXISTS `activity_category`;
CREATE TABLE `activity_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `sort` int unsigned DEFAULT '0' COMMENT '排序',
  `is_enabled` tinyint unsigned DEFAULT '1' COMMENT '是否启用：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`category_name`),
  KEY `idx_is_enabled` (`is_enabled`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动分类表';

-- ----------------------------
-- Table structure for emergency_help
-- ----------------------------
DROP TABLE IF EXISTS `emergency_help`;
CREATE TABLE `emergency_help` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '求助ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（老人）',
  `help_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '求助类型：medical-医疗急救，life-生活求助，security-安全报警，other-其他',
  `urgency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '紧急程度：high-紧急，medium-一般，low-不紧急',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '求助描述',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '位置信息',
  `help_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '求助状态：pending-待处理，handling-处理中，completed-已完成',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handle_result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理结果',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `is_processed` tinyint unsigned DEFAULT '0' COMMENT '是否已处理：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_help_type` (`help_type`),
  KEY `idx_urgency` (`urgency`),
  KEY `idx_help_status` (`help_status`),
  KEY `idx_handler_id` (`handler_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_emergency_handler` FOREIGN KEY (`handler_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_emergency_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='紧急求助表';

-- ----------------------------
-- Table structure for family_elderly_relation
-- ----------------------------
DROP TABLE IF EXISTS `family_elderly_relation`;
CREATE TABLE `family_elderly_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `family_id` bigint NOT NULL COMMENT '家属用户ID',
  `elderly_id` bigint NOT NULL COMMENT '老人用户ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关系类型：spouse-配偶，child-子女，grandchild-孙子女，sibling-兄弟姐妹，other-其他',
  `relation_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关系名称（如：儿子、女儿等）',
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '审核状态：pending-待审核，approved-已通过，rejected-已拒绝',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核备注',
  `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `proof_materials` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '审核材料（JSON格式存储文件路径）',
  PRIMARY KEY (`id`),
  KEY `idx_family_id` (`family_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `fk_relation_auditor` (`auditor_id`),
  CONSTRAINT `fk_relation_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_relation_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_relation_family` FOREIGN KEY (`family_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家属-老人关联关系表';

-- ----------------------------
-- Table structure for health_data
-- ----------------------------
DROP TABLE IF EXISTS `health_data`;
CREATE TABLE `health_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '健康数据ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `heart_rate` int unsigned DEFAULT NULL COMMENT '心率（次/分）',
  `systolic_pressure` int unsigned DEFAULT NULL COMMENT '收缩压（mmHg）',
  `diastolic_pressure` int unsigned DEFAULT NULL COMMENT '舒张压（mmHg）',
  `blood_sugar` decimal(5,2) DEFAULT NULL COMMENT '血糖（mmol/L）',
  `weight` decimal(5,1) DEFAULT NULL COMMENT '体重（kg）',
  `height` decimal(5,1) DEFAULT NULL COMMENT '身高（cm）',
  `temperature` decimal(4,1) DEFAULT NULL COMMENT '体温（℃）',
  `oxygen_saturation` decimal(4,1) DEFAULT NULL COMMENT '血氧饱和度（%）',
  `health_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'normal' COMMENT '健康状态：normal-正常，abnormal-异常',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '健康描述',
  `monitor_time` datetime NOT NULL COMMENT '监测时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_monitor_time` (`monitor_time`),
  KEY `idx_health_status` (`health_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_health_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康数据表';

-- ----------------------------
-- Table structure for health_warning
-- ----------------------------
DROP TABLE IF EXISTS `health_warning`;
CREATE TABLE `health_warning` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `health_data_id` bigint NOT NULL COMMENT '健康数据ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `warning_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警类型：bloodPressure, heartRate, bloodSugar, temperature',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警级别：low, medium, high',
  `warning_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警信息',
  `is_notified` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否已通知：0-未通知，1-已通知',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_health_data_id` (`health_data_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_warning_health_data` FOREIGN KEY (`health_data_id`) REFERENCES `health_data` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_warning_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康预警表';

-- ----------------------------
-- Table structure for health_warning_threshold
-- ----------------------------
DROP TABLE IF EXISTS `health_warning_threshold`;
CREATE TABLE `health_warning_threshold` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阈值配置ID',
  `indicator_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '指标类型：bloodPressureSystolic, bloodPressureDiastolic, heartRate, bloodSugar, bodyTemperature',
  `min_value` double NOT NULL COMMENT '最小值阈值',
  `max_value` double NOT NULL COMMENT '最大值阈值',
  `is_active` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否激活：0-未激活，1-已激活',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_indicator_type` (`indicator_type`)
) ENGINE=InnoDB AUTO_INCREMENT=232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康预警阈值配置表';

-- ----------------------------
-- Table structure for service_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `service_evaluation`;
CREATE TABLE `service_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '服务订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（老人）',
  `caregiver_id` bigint NOT NULL COMMENT '护工ID',
  `rating` tinyint unsigned NOT NULL COMMENT '评分：1-5分',
  `evaluation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识，0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_caregiver_id` (`caregiver_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_evaluation_caregiver` FOREIGN KEY (`caregiver_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluation_order` FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluation_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- ----------------------------
-- Table structure for service_order
-- ----------------------------
DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID（老人）',
  `service_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务类型',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
  `service_date` date NOT NULL COMMENT '服务日期',
  `service_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务时间段',
  `caregiver_id` bigint DEFAULT NULL COMMENT '护工ID',
  `service_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务地址',
  `service_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务描述',
  `order_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '订单状态：pending-待确认，confirmed-已确认，in_progress-进行中，completed-已完成，cancelled-已取消',
  `is_paid` tinyint unsigned DEFAULT '0' COMMENT '是否已支付：1-是，0-否',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_caregiver_id` (`caregiver_id`),
  KEY `idx_service_date` (`service_date`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_order_caregiver` FOREIGN KEY (`caregiver_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务预约表';

-- ----------------------------
-- Table structure for service_verification
-- ----------------------------
DROP TABLE IF EXISTS `service_verification`;
CREATE TABLE `service_verification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '核销ID',
  `order_id` bigint NOT NULL COMMENT '服务订单ID',
  `verification_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '核销码',
  `verification_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '核销状态：pending-待核销，verified-已核销',
  `verification_time` datetime DEFAULT NULL COMMENT '核销时间',
  `verifier_id` bigint DEFAULT NULL COMMENT '核销人ID（护工）',
  `service_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '服务内容',
  `elderly_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '老人反馈',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_verification_code` (`verification_code`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_verification_status` (`verification_status`),
  KEY `idx_verifier_id` (`verifier_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_verification_order` FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_verification_verifier` FOREIGN KEY (`verifier_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务核销表';

-- ----------------------------
-- Table structure for sys_announcement
-- ----------------------------
DROP TABLE IF EXISTS `sys_announcement`;
CREATE TABLE `sys_announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `announcement_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'notice' COMMENT '公告类型：notice-通知，activity-活动，emergency-紧急',
  `target_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标角色：elderly-老人，family-家属，caregiver-护工，admin-管理员，多个用逗号分隔',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `is_top` tinyint unsigned DEFAULT '0' COMMENT '是否置顶：1-是，0-否',
  `is_published` tinyint unsigned DEFAULT '1' COMMENT '是否发布：1-是，0-否',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `view_count` int unsigned DEFAULT '0' COMMENT '浏览次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_category_id` bigint DEFAULT NULL COMMENT '活动分类ID',
  `activity_time` datetime DEFAULT NULL COMMENT '活动时间',
  `activity_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动地点',
  `participant_count` int DEFAULT '0' COMMENT '参与人数',
  PRIMARY KEY (`id`),
  KEY `idx_announcement_type` (`announcement_type`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_is_published` (`is_published`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `fk_announcement_activity_category` (`activity_category_id`),
  CONSTRAINT `fk_announcement_activity_category` FOREIGN KEY (`activity_category_id`) REFERENCES `activity_category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ----------------------------
-- Table structure for sys_backup_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_backup_config`;
CREATE TABLE `sys_backup_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `backup_frequency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备份频率：daily-每天, weekly-每周, monthly-每月',
  `backup_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备份时间：HH:mm格式',
  `backup_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '/backup' COMMENT '备份文件存储路径',
  `retention_days` int unsigned DEFAULT '30' COMMENT '备份保留天数',
  `is_enabled` tinyint unsigned DEFAULT '1' COMMENT '是否启用：0-未启用，1-已启用',
  `last_backup_time` datetime DEFAULT NULL COMMENT '最后备份时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据备份配置表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号（AES加密）',
  `phone` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号（AES加密）',
  `role_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色类型：elderly-老人，family-家属，caregiver-护工，admin-管理员',
  `gender` tinyint unsigned DEFAULT '1' COMMENT '性别：1-男，0-女',
  `age` int unsigned DEFAULT '0' COMMENT '年龄',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '住址',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `is_enabled` tinyint unsigned DEFAULT '1' COMMENT '是否启用：1-是，0-否',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_type` (`role_type`),
  KEY `idx_phone` (`phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Table structure for sys_voice_keyword
-- ----------------------------
DROP TABLE IF EXISTS `sys_voice_keyword`;
CREATE TABLE `sys_voice_keyword` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关键词ID',
  `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语音关键词',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发的动作类型：health_report-健康报告, emergency-紧急求助, booking-预约服务, contact_family-联系家属等',
  `action_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '动作参数（JSON格式）',
  `sort` int unsigned DEFAULT '0' COMMENT '排序',
  `is_enabled` tinyint unsigned DEFAULT '1' COMMENT '是否启用：0-未启用，1-已启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '是否删除：1-是，0-否',
  PRIMARY KEY (`id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_action_type` (`action_type`),
  KEY `idx_sort` (`sort`),
  KEY `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='语音关键词配置表';
