/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 80039
Source Host           : localhost:3306
Source Database       : smart_elderly_care

Target Server Type    : MYSQL
Target Server Version : 80039
File Encoding         : 65001

Date: 2026-03-06 00:40:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for activity_category
-- ----------------------------
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
-- Records of activity_category
-- ----------------------------
INSERT INTO `activity_category` VALUES ('1', '健身活动', '包括健身操、太极等体育活动', '1', '1', '2026-03-04 10:17:08', '2026-03-04 10:17:08');
INSERT INTO `activity_category` VALUES ('2', '文化活动', '包括书法、绘画、阅读等文化活动', '2', '1', '2026-03-04 10:17:08', '2026-03-04 10:17:08');
INSERT INTO `activity_category` VALUES ('3', '娱乐活动', '包括棋牌、唱歌、跳舞等娱乐活动', '3', '1', '2026-03-04 10:17:08', '2026-03-04 10:17:08');
INSERT INTO `activity_category` VALUES ('4', '教育活动', '包括健康讲座、技能培训等教育活动', '4', '1', '2026-03-04 10:17:08', '2026-03-04 10:17:08');
INSERT INTO `activity_category` VALUES ('5', '志愿服务', '包括社区服务、关爱老人等志愿活动', '5', '1', '2026-03-04 10:17:08', '2026-03-04 10:17:08');

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
-- Records of emergency_help
-- ----------------------------
INSERT INTO `emergency_help` VALUES ('11', '2', 'medical', 'high', '突然感到胸痛，需要紧急医疗救助', '13800138002', '重庆市沙坪坝区002号', 'completed', '9', '已联系120急救车，老人已送往医院', '2026-01-20 08:30:00', '1', '2026-01-24 11:09:32', '2026-01-24 11:09:32', '0');
INSERT INTO `emergency_help` VALUES ('12', '3', 'life', 'medium', '家中水管漏水，需要帮助维修', '13800138003', '重庆市沙坪坝区003号', 'handling', '10', '正在联系维修人员', '2026-01-20 10:00:00', '0', '2026-01-24 11:09:32', '2026-01-24 11:09:32', '0');
INSERT INTO `emergency_help` VALUES ('13', '4', 'security', 'high', '家中有陌生人敲门，感到害怕', '13800138004', '重庆市沙坪坝区004号', 'completed', '9', '已联系社区保安，确认是快递员', '2026-01-20 12:00:00', '1', '2026-01-24 11:09:32', '2026-01-24 11:09:32', '0');
INSERT INTO `emergency_help` VALUES ('14', '5', 'medical', 'medium', '血糖突然升高，需要帮助', '13800138005', '重庆市沙坪坝区005号', 'pending', null, null, null, '0', '2026-01-24 11:09:32', '2026-01-24 11:09:32', '0');
INSERT INTO `emergency_help` VALUES ('15', '6', 'other', 'low', '电视坏了，需要帮助修理', '13800138006', '重庆市沙坪坝区006号', 'pending', null, null, null, '0', '2026-01-24 11:09:32', '2026-01-24 11:09:32', '0');
INSERT INTO `emergency_help` VALUES ('16', '2', 'medical', 'medium', '感觉头晕，需要医生检查', '13800138002', '重庆市沙坪坝区002号', 'completed', '9', '已安排医生上门检查，血压正常，建议休息', '2026-02-01 09:30:00', '1', '2026-02-01 09:00:00', '2026-02-01 09:30:00', '0');
INSERT INTO `emergency_help` VALUES ('17', '3', 'life', 'low', '需要帮忙取快递', '13800138003', '重庆市沙坪坝区003号', 'completed', '10', '已帮忙取回快递', '2026-02-01 14:30:00', '1', '2026-02-01 14:00:00', '2026-02-01 14:30:00', '0');
INSERT INTO `emergency_help` VALUES ('18', '4', 'medical', 'high', '突然胸闷，呼吸困难', '13800138004', '重庆市沙坪坝区004号', 'completed', '9', '已联系120急救车，老人已送往医院，诊断为轻微哮喘', '2026-02-02 08:30:00', '1', '2026-02-02 08:00:00', '2026-02-02 08:30:00', '0');

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
-- Records of family_elderly_relation
-- ----------------------------
INSERT INTO `family_elderly_relation` VALUES ('1', '7', '2', 'child', '儿子', 'approved', '审核通过', '1', '2026-01-20 09:00:00', '0', '2026-01-24 11:08:10', '2026-03-05 22:23:00', '[{\"name\": \"户口本.jpg\", \"url\": \"/src/assets/images/relation/1.jpg\", \"size\": 1024000}]');

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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康数据表';

-- ----------------------------
-- Records of health_data
-- ----------------------------
INSERT INTO `health_data` VALUES ('41', '2', '75', '120', '80', '5.60', '65.5', '165.0', '36.5', '98.5', 'normal', '各项指标正常', '2026-01-20 08:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('42', '2', '78', '125', '82', '5.80', '65.5', '165.0', '36.6', '98.0', 'normal', '各项指标正常', '2026-01-20 20:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('43', '3', '82', '130', '85', '6.20', '58.0', '158.0', '36.5', '97.5', 'normal', '血压略高，注意监测', '2026-01-20 08:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('44', '3', '80', '128', '83', '6.00', '58.0', '158.0', '36.4', '97.8', 'normal', '血压略高，注意监测', '2026-01-20 20:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('45', '4', '70', '115', '75', '5.40', '70.0', '170.0', '36.5', '99.0', 'normal', '各项指标正常', '2026-01-20 09:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('46', '4', '72', '118', '78', '5.50', '70.0', '170.0', '36.6', '98.8', 'normal', '各项指标正常', '2026-01-20 21:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('47', '5', '90', '140', '90', '7.20', '62.0', '160.0', '36.7', '96.5', 'abnormal', '血压和血糖偏高', '2026-01-20 09:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('48', '5', '88', '138', '88', '7.00', '62.0', '160.0', '36.6', '96.8', 'abnormal', '血压和血糖偏高', '2026-01-20 21:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('49', '6', '76', '122', '81', '5.70', '68.0', '168.0', '36.5', '98.2', 'normal', '各项指标正常', '2026-01-20 10:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('50', '6', '74', '120', '80', '5.60', '68.0', '168.0', '36.4', '98.5', 'normal', '各项指标正常', '2026-01-20 22:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('51', '2', '77', '123', '81', '5.90', '65.5', '165.0', '36.5', '98.3', 'normal', '各项指标正常', '2026-01-21 08:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('52', '2', '79', '126', '83', '6.00', '65.5', '165.0', '36.6', '98.1', 'normal', '各项指标正常', '2026-01-21 20:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('53', '3', '83', '131', '86', '6.30', '58.0', '158.0', '36.5', '97.4', 'normal', '血压略高，注意监测', '2026-01-21 08:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('54', '3', '81', '129', '84', '6.10', '58.0', '158.0', '36.4', '97.7', 'normal', '血压略高，注意监测', '2026-01-21 20:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('55', '4', '71', '116', '76', '5.50', '70.0', '170.0', '36.5', '98.9', 'normal', '各项指标正常', '2026-01-21 09:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('56', '4', '73', '119', '79', '5.60', '70.0', '170.0', '36.6', '98.7', 'normal', '各项指标正常', '2026-01-21 21:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('57', '5', '91', '141', '91', '7.30', '62.0', '160.0', '36.7', '96.4', 'abnormal', '血压和血糖偏高', '2026-01-21 09:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('58', '5', '89', '139', '89', '7.10', '62.0', '160.0', '36.6', '96.7', 'abnormal', '血压和血糖偏高', '2026-01-21 21:30:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('59', '6', '77', '123', '82', '5.80', '68.0', '168.0', '36.5', '98.1', 'normal', '各项指标正常', '2026-01-21 10:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('60', '6', '75', '121', '81', '5.70', '68.0', '168.0', '36.4', '98.4', 'normal', '各项指标正常', '2026-01-21 22:00:00', '2026-01-24 11:08:56', '2026-01-24 11:08:56', '0');
INSERT INTO `health_data` VALUES ('61', '2', '76', '122', '80', '5.70', '65.5', '165.0', '36.5', '98.4', 'normal', '各项指标正常', '2026-02-01 08:00:00', '2026-02-01 08:00:00', '2026-02-01 08:00:00', '0');
INSERT INTO `health_data` VALUES ('62', '2', '78', '124', '82', '5.90', '65.5', '165.0', '36.6', '98.2', 'normal', '各项指标正常', '2026-02-01 20:00:00', '2026-02-01 20:00:00', '2026-02-01 20:00:00', '0');
INSERT INTO `health_data` VALUES ('63', '2', '75', '121', '79', '5.60', '65.5', '165.0', '36.5', '98.5', 'normal', '各项指标正常', '2026-02-02 08:00:00', '2026-02-02 08:00:00', '2026-02-02 08:00:00', '0');
INSERT INTO `health_data` VALUES ('64', '2', '77', '123', '81', '5.80', '65.5', '165.0', '36.6', '98.3', 'normal', '各项指标正常', '2026-02-02 20:00:00', '2026-02-02 20:00:00', '2026-02-02 20:00:00', '0');
INSERT INTO `health_data` VALUES ('65', '3', '81', '129', '84', '6.10', '58.0', '158.0', '36.5', '97.6', 'normal', '血压略高，注意监测', '2026-02-01 08:30:00', '2026-02-01 08:30:00', '2026-02-01 08:30:00', '0');
INSERT INTO `health_data` VALUES ('66', '3', '83', '131', '86', '6.30', '58.0', '158.0', '36.6', '97.4', 'normal', '血压略高，注意监测', '2026-02-01 20:30:00', '2026-02-01 20:30:00', '2026-02-01 20:30:00', '0');
INSERT INTO `health_data` VALUES ('67', '3', '80', '128', '83', '6.00', '58.0', '158.0', '36.5', '97.7', 'normal', '血压略高，注意监测', '2026-02-02 08:30:00', '2026-02-02 08:30:00', '2026-02-02 08:30:00', '0');
INSERT INTO `health_data` VALUES ('68', '3', '82', '130', '85', '6.20', '58.0', '158.0', '36.4', '97.5', 'normal', '血压略高，注意监测', '2026-02-02 20:30:00', '2026-02-02 20:30:00', '2026-02-02 20:30:00', '0');
INSERT INTO `health_data` VALUES ('69', '5', '89', '139', '89', '7.10', '62.0', '160.0', '36.6', '96.7', 'abnormal', '血压和血糖偏高', '2026-02-01 09:30:00', '2026-02-01 09:30:00', '2026-02-01 09:30:00', '0');
INSERT INTO `health_data` VALUES ('70', '5', '91', '142', '92', '7.40', '62.0', '160.0', '36.7', '96.3', 'abnormal', '血压和血糖偏高，建议就医', '2026-02-01 21:30:00', '2026-02-01 21:30:00', '2026-02-01 21:30:00', '0');
INSERT INTO `health_data` VALUES ('71', '5', '90', '140', '90', '7.20', '62.0', '160.0', '36.7', '96.5', 'abnormal', '血压和血糖偏高', '2026-02-02 09:30:00', '2026-02-02 09:30:00', '2026-02-02 09:30:00', '0');
INSERT INTO `health_data` VALUES ('72', '5', '88', '138', '88', '7.00', '62.0', '160.0', '36.6', '96.8', 'abnormal', '血压和血糖偏高', '2026-02-02 21:30:00', '2026-02-02 21:30:00', '2026-02-02 21:30:00', '0');
INSERT INTO `health_data` VALUES ('73', '1', '72', '120', '80', '5.20', '65.5', '170.0', '36.5', '98.0', 'normal', '3月1日监测', '2026-03-01 09:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('74', '1', '75', '125', '82', '5.50', '65.3', '170.0', '36.6', '97.5', 'normal', '3月5日监测', '2026-03-05 09:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('75', '1', '85', '140', '90', '6.80', '65.8', '170.0', '37.2', '96.0', 'abnormal', '3月10日监测（血压偏高）', '2026-03-10 09:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('76', '2', '68', '118', '78', '4.90', '58.2', '162.0', '36.3', '98.5', 'normal', '3月1日监测', '2026-03-01 10:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('77', '2', '90', '150', '95', '7.20', '58.5', '162.0', '37.5', '95.0', 'abnormal', '3月15日监测（多项异常）', '2026-03-15 10:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('78', '3', '70', '122', '81', '5.10', '70.1', '175.0', '36.4', '98.2', 'normal', '3月1日监测', '2026-03-01 11:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('79', '3', '73', '123', '83', '5.30', '70.0', '175.0', '36.7', '97.8', 'normal', '3月20日监测', '2026-03-20 11:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('80', '1', '74', '122', '81', '5.30', '65.6', '170.0', '36.6', '98.1', 'normal', '4月1日监测', '2026-04-01 09:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('81', '1', '76', '126', '83', '5.60', '65.4', '170.0', '36.7', '97.6', 'normal', '4月5日监测', '2026-04-05 09:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('82', '1', '88', '145', '92', '7.00', '65.9', '170.0', '37.3', '95.8', 'abnormal', '4月10日监测（血压偏高）', '2026-04-10 09:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('83', '2', '70', '120', '80', '5.00', '58.3', '162.0', '36.4', '98.6', 'normal', '4月1日监测', '2026-04-01 10:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('84', '2', '92', '155', '98', '7.50', '58.6', '162.0', '37.6', '94.8', 'abnormal', '4月15日监测（多项异常）', '2026-04-15 10:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('85', '3', '72', '124', '82', '5.20', '70.2', '175.0', '36.5', '98.3', 'normal', '4月1日监测', '2026-04-01 11:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('86', '3', '75', '125', '84', '5.40', '70.1', '175.0', '36.8', '97.7', 'normal', '4月20日监测', '2026-04-20 11:00:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `health_data` VALUES ('87', '1', '72', '120', '80', '5.20', '65.5', '170.0', '36.5', '98.0', 'normal', '3月1日监测', '2026-03-01 09:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('88', '1', '75', '125', '82', '5.50', '65.3', '170.0', '36.6', '97.5', 'normal', '3月5日监测', '2026-03-05 09:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('89', '1', '85', '140', '90', '6.80', '65.8', '170.0', '37.2', '96.0', 'abnormal', '3月10日监测（血压偏高）', '2026-03-10 09:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('90', '2', '68', '118', '78', '4.90', '58.2', '162.0', '36.3', '98.5', 'normal', '3月1日监测', '2026-03-01 10:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('91', '2', '90', '150', '95', '7.20', '58.5', '162.0', '37.5', '95.0', 'abnormal', '3月15日监测（多项异常）', '2026-03-15 10:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('92', '3', '70', '122', '81', '5.10', '70.1', '175.0', '36.4', '98.2', 'normal', '3月1日监测', '2026-03-01 11:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('93', '3', '73', '123', '83', '5.30', '70.0', '175.0', '36.7', '97.8', 'normal', '3月20日监测', '2026-03-20 11:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('94', '1', '74', '122', '81', '5.30', '65.6', '170.0', '36.6', '98.1', 'normal', '4月1日监测', '2026-04-01 09:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('95', '1', '76', '126', '83', '5.60', '65.4', '170.0', '36.7', '97.6', 'normal', '4月5日监测', '2026-04-05 09:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('96', '1', '88', '145', '92', '7.00', '65.9', '170.0', '37.3', '95.8', 'abnormal', '4月10日监测（血压偏高）', '2026-04-10 09:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('97', '2', '70', '120', '80', '5.00', '58.3', '162.0', '36.4', '98.6', 'normal', '4月1日监测', '2026-04-01 10:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('98', '2', '92', '155', '98', '7.50', '58.6', '162.0', '37.6', '94.8', 'abnormal', '4月15日监测（多项异常）', '2026-04-15 10:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('99', '3', '72', '124', '82', '5.20', '70.2', '175.0', '36.5', '98.3', 'normal', '4月1日监测', '2026-04-01 11:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');
INSERT INTO `health_data` VALUES ('100', '3', '75', '125', '84', '5.40', '70.1', '175.0', '36.8', '97.7', 'normal', '4月20日监测', '2026-04-20 11:00:00', '2026-03-06 00:09:41', '2026-03-06 00:09:41', '0');

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
-- Records of health_warning
-- ----------------------------

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
-- Records of health_warning_threshold
-- ----------------------------

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
-- Records of service_evaluation
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务预约表';

-- ----------------------------
-- Records of service_order
-- ----------------------------
INSERT INTO `service_order` VALUES ('11', 'SO202601200001', '2', 'home_care', '居家护理', '2026-01-22', '09:00-11:00', '9', '重庆市沙坪坝区002号', '需要上门测量血压和血糖', 'confirmed', '1', '2026-01-20 10:00:00', '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('12', 'SO202601200002', '3', 'medical_care', '医疗护理', '2026-01-22', '14:00-16:00', '10', '重庆市沙坪坝区003号', '需要换药和伤口护理', 'pending', '0', null, '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('13', 'SO202601200003', '4', 'life_assistance', '生活协助', '2026-01-23', '08:00-10:00', '9', '重庆市沙坪坝区004号', '需要帮助购物和打扫卫生', 'in_progress', '1', '2026-01-20 11:00:00', '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('14', 'SO202601200004', '5', 'home_care', '居家护理', '2026-01-23', '10:00-12:00', '10', '重庆市沙坪坝区005号', '需要上门测量血压和血糖', 'completed', '1', '2026-01-20 12:00:00', '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('15', 'SO202601200005', '6', 'medical_care', '医疗护理', '2026-01-24', '13:00-15:00', '9', '重庆市沙坪坝区006号', '需要换药和伤口护理', 'pending', '0', null, '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('16', 'SO202601200006', '2', 'life_assistance', '生活协助', '2026-01-24', '15:00-17:00', '10', '重庆市沙坪坝区002号', '需要帮助做饭和打扫卫生', 'confirmed', '1', '2026-01-20 13:00:00', '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('17', 'SO202601200007', '3', 'home_care', '居家护理', '2026-01-25', '09:00-11:00', '9', '重庆市沙坪坝区003号', '需要上门测量血压和血糖', 'pending', '0', null, '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('18', 'SO202601200008', '4', 'medical_care', '医疗护理', '2026-01-25', '14:00-16:00', '10', '重庆市沙坪坝区004号', '需要换药和伤口护理', 'confirmed', '1', '2026-01-20 14:00:00', '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('19', 'SO202601200009', '5', 'life_assistance', '生活协助', '2026-01-26', '08:00-10:00', '9', '重庆市沙坪坝区005号', '需要帮助购物和打扫卫生', 'pending', '0', null, '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('20', 'SO202601200010', '6', 'home_care', '居家护理', '2026-01-26', '10:00-12:00', '10', '重庆市沙坪坝区006号', '需要上门测量血压和血糖', 'confirmed', '1', '2026-01-20 15:00:00', '2026-01-24 11:09:55', '2026-01-24 11:09:55', '0');
INSERT INTO `service_order` VALUES ('21', 'SO202602010001', '2', 'home_care', '居家护理', '2026-02-01', '09:00-11:00', '9', '重庆市沙坪坝区002号', '需要上门测量血压和血糖', 'completed', '1', '2026-02-01 08:00:00', '2026-02-01 08:00:00', '2026-02-01 11:00:00', '0');
INSERT INTO `service_order` VALUES ('22', 'SO202602010002', '3', 'medical_care', '医疗护理', '2026-02-01', '14:00-16:00', '10', '重庆市沙坪坝区003号', '需要换药和伤口护理', 'completed', '1', '2026-02-01 13:00:00', '2026-02-01 13:00:00', '2026-02-01 16:00:00', '0');
INSERT INTO `service_order` VALUES ('23', 'SO202602020001', '4', 'life_assistance', '生活协助', '2026-02-02', '08:00-10:00', '9', '重庆市沙坪坝区004号', '需要帮助购物和打扫卫生', 'completed', '1', '2026-02-02 07:00:00', '2026-02-02 07:00:00', '2026-02-02 10:00:00', '0');
INSERT INTO `service_order` VALUES ('24', 'SO202602020002', '5', 'home_care', '居家护理', '2026-02-02', '10:00-12:00', '10', '重庆市沙坪坝区005号', '需要上门测量血压和血糖，重点关注血压', 'completed', '1', '2026-02-02 09:00:00', '2026-02-02 09:00:00', '2026-02-02 12:00:00', '0');
INSERT INTO `service_order` VALUES ('25', 'SO202602030001', '6', 'medical_care', '医疗护理', '2026-02-03', '13:00-15:00', '9', '重庆市沙坪坝区006号', '需要换药和伤口护理', 'completed', '1', '2026-02-03 12:00:00', '2026-02-03 12:00:00', '2026-02-03 15:00:00', '0');
INSERT INTO `service_order` VALUES ('26', 'SO202602030002', '2', 'life_assistance', '生活协助', '2026-02-03', '15:00-17:00', '10', '重庆市沙坪坝区002号', '需要帮助做饭和打扫卫生', 'completed', '1', '2026-02-03 14:00:00', '2026-02-03 14:00:00', '2026-02-03 17:00:00', '0');
INSERT INTO `service_order` VALUES ('27', 'SO202602040001', '3', 'home_care', '居家护理', '2026-02-04', '09:00-11:00', '9', '重庆市沙坪坝区003号', '需要上门测量血压和血糖', 'completed', '1', '2026-02-04 08:00:00', '2026-02-04 08:00:00', '2026-02-04 11:00:00', '0');
INSERT INTO `service_order` VALUES ('28', 'SO202602040002', '4', 'medical_care', '医疗护理', '2026-02-04', '14:00-16:00', '10', '重庆市沙坪坝区004号', '需要换药和伤口护理', 'completed', '1', '2026-02-04 13:00:00', '2026-02-04 13:00:00', '2026-02-04 16:00:00', '0');
INSERT INTO `service_order` VALUES ('29', 'SO202602050001', '5', 'life_assistance', '生活协助', '2026-02-05', '08:00-10:00', '9', '重庆市沙坪坝区005号', '需要帮助购物和打扫卫生', 'completed', '1', '2026-02-05 07:00:00', '2026-02-05 07:00:00', '2026-02-05 10:00:00', '0');
INSERT INTO `service_order` VALUES ('30', 'SO202602050002', '6', 'home_care', '居家护理', '2026-02-05', '10:00-12:00', '10', '重庆市沙坪坝区006号', '需要上门测量血压和血糖', 'completed', '1', '2026-02-05 09:00:00', '2026-02-05 09:00:00', '2026-02-05 12:00:00', '0');
INSERT INTO `service_order` VALUES ('31', 'ORD20260301001', '1', 'meal', '助餐服务', '2026-03-01', '09:00-10:00', '4', 'XX小区1栋1单元101', '午餐配送', 'completed', '1', '2026-03-01 08:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('32', 'ORD20260305001', '2', 'clean', '助洁服务', '2026-03-05', '14:00-15:00', '5', 'XX小区2栋2单元202', '家庭清洁', 'completed', '1', '2026-03-05 13:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('33', 'ORD20260310001', '1', 'health', '健康监测', '2026-03-10', '10:00-11:00', '4', 'XX小区1栋1单元101', '血压血糖监测', 'completed', '1', '2026-03-10 09:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('34', 'ORD20260315001', '3', 'meal', '助餐服务', '2026-03-15', '12:00-13:00', '5', 'XX小区3栋3单元303', '午餐配送', 'completed', '1', '2026-03-15 11:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('35', 'ORD20260320001', '2', 'health', '健康监测', '2026-03-20', '15:00-16:00', '4', 'XX小区2栋2单元202', '心率监测', 'completed', '1', '2026-03-20 14:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('36', 'ORD20260401001', '1', 'meal', '助餐服务', '2026-04-01', '09:00-10:00', '4', 'XX小区1栋1单元101', '午餐配送', 'completed', '1', '2026-04-01 08:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('37', 'ORD20260405001', '2', 'clean', '助洁服务', '2026-04-05', '14:00-15:00', '5', 'XX小区2栋2单元202', '家庭清洁', 'completed', '1', '2026-04-05 13:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('38', 'ORD20260410001', '1', 'health', '健康监测', '2026-04-10', '10:00-11:00', '4', 'XX小区1栋1单元101', '血压血糖监测', 'completed', '1', '2026-04-10 09:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('39', 'ORD20260415001', '3', 'meal', '助餐服务', '2026-04-15', '12:00-13:00', '5', 'XX小区3栋3单元303', '午餐配送', 'completed', '1', '2026-04-15 11:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');
INSERT INTO `service_order` VALUES ('40', 'ORD20260420001', '2', 'health', '健康监测', '2026-04-20', '15:00-16:00', '4', 'XX小区2栋2单元202', '心率监测', 'completed', '1', '2026-04-20 14:30:00', '2026-03-06 00:07:58', '2026-03-06 00:07:58', '0');

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
-- Records of service_verification
-- ----------------------------

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
-- Records of sys_backup_config
-- ----------------------------

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
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '管理员', 'B2B99FDB8252647747FF3154F2A3DA2FF648578061B9534168896A98BAACD681', 'gHF1qh44BJpkw9FyO+EM5Q==', 'admin', '1', '35', '重庆市沙坪坝区001号', '/avatars/admin.jpg', '1', '0', '2026-01-24 01:04:36', '2026-03-02 20:09:53');
INSERT INTO `sys_user` VALUES ('2', 'elderly001', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '张三', '0DB3826D38972752CF25F95432A818F7F648578061B9534168896A98BAACD681', 'ymdmiie88FYOoHprT0Vx8g==', 'elderly', '1', '75', '重庆市沙坪坝区002号', '/avatars/elderly001.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('3', 'elderly002', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '李四', '8333673CE9A4A329A76126B16A22058FF648578061B9534168896A98BAACD681', 'j+Fz9Qc39QaEDS/BlTXXHQ==', 'elderly', '0', '72', '重庆市沙坪坝区003号', '/avatars/elderly002.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('4', 'elderly003', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '王五', 'E0B42150D3E110E5B59AF5A471D43F8DF648578061B9534168896A98BAACD681', 'Rps29iTNzLYLstaeR35k1A==', 'elderly', '1', '78', '重庆市沙坪坝区004号', '/avatars/elderly003.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('5', 'elderly004', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '赵六', '21747AD1BE48553FFF71A92A80766A98F648578061B9534168896A98BAACD681', 'aQLd2RVI36IvWypiXGFsPg==', 'elderly', '0', '70', '重庆市沙坪坝区005号', '/avatars/elderly004.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('6', 'elderly005', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '孙七', '51E267909230A2881C189DB4F41677B5F648578061B9534168896A98BAACD681', 'rC96dPByFSMHOsnC9LI1jQ==', 'elderly', '1', '69', '重庆市沙坪坝区006号', '/avatars/elderly005.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('7', 'family001', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '张小明', '9F4018306E13479B7B6068F8CA6E975BF648578061B9534168896A98BAACD681', '5WCtGGJArCjd+fDu+aCZpw==', 'family', '1', '45', '重庆市沙坪坝区007号', '/avatars/family001.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('8', 'family002', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '李小红', '4C9012103266D2900C5A411F307DAA1CF648578061B9534168896A98BAACD681', 'xLMmgUZZCoaEf6ipeYqTzA==', 'family', '0', '42', '重庆市沙坪坝区008号', '/avatars/family002.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('9', 'caregiver001', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '陈护工', 'F43F305CB4F93EA261B626B4026D9AFAF648578061B9534168896A98BAACD681', 'nfe2/r4cfvNIzLwfWiBh/Q==', 'caregiver', '1', '30', '重庆市沙坪坝区009号', '/avatars/caregiver001.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('10', 'caregiver002', '$2a$10$L2geQ5o5JtgxVtgb4oKvleyytlaLpvJzltNMxJSQSFu5dX9fmwcU.', '刘护工', '6FC359547A82AB40E14D65C2BE6708B3F648578061B9534168896A98BAACD681', 'Lzv9TvsWDdbgEP4h66LNmA==', 'caregiver', '0', '28', '重庆市沙坪坝区010号', '/avatars/caregiver002.jpg', '1', '0', '2026-01-24 01:04:36', '2026-01-24 01:04:36');
INSERT INTO `sys_user` VALUES ('32', '吴仲华', '$2a$10$x4mlngw08Atjw9to82mJ6.xrNi1aLruAEM3OE10HUwm3Lti992bNS', '吴仲华', '511028197010119128', 'eP1YOXou5sO00yXqDl0GPQ==', 'elderly', '0', '56', '重庆市沙坪坝区99号', null, '1', '0', '2026-01-24 13:46:24', '2026-01-24 13:46:52');
