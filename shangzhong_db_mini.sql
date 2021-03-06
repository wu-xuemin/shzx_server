/*
Navicat MySQL Data Transfer

Source Server         : Localhost
Source Server Version : 50553
Source Host           : localhost:3306
Source Database       : shangzhong_db

Target Server Type    : MYSQL
Target Server Version : 50553
File Encoding         : 65001

Date: 2019-02-11 14:45:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for banji
-- ----------------------------
DROP TABLE IF EXISTS `banji`;
CREATE TABLE `banji` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `grade` varchar(255) NOT NULL COMMENT '年级',
  `class_name` varchar(255) NOT NULL COMMENT '班级名称',
  `charge_teacher` int(10) unsigned DEFAULT NULL COMMENT '班主任',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `class_id_from_url` varchar(255) DEFAULT NULL COMMENT '上中url里解析到的classId',
  PRIMARY KEY (`id`),
  KEY `fk_charge_teacher` (`charge_teacher`),
  CONSTRAINT `fk_charge_teacher` FOREIGN KEY (`charge_teacher`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of banji
-- ----------------------------

-- ----------------------------
-- Table structure for `booking_record`
-- ----------------------------
DROP TABLE IF EXISTS `booking_record`;
CREATE TABLE `booking_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '该表只是记录变更历史，实际的变更在确认后，应该更新到实际的表',
  `change_date` date NOT NULL COMMENT '变更线路的日期',
  `student` int(10) unsigned NOT NULL COMMENT '要变更线路的学生',
  `old_bus_line` int(10) unsigned NOT NULL COMMENT '原校车',
  `new_bus_line` int(10) unsigned NOT NULL COMMENT '新校车',
  `new_station` int(10) unsigned NOT NULL COMMENT '新站点',
  `change_content` varchar(1000) NOT NULL COMMENT '变更内容',
  `confirm_status` varchar(255) NOT NULL COMMENT '是否确认',
  `create_time` datetime NOT NULL COMMENT '该条记录的创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '该条记录的更新时间',
  PRIMARY KEY (`id`,`change_date`),
  KEY `fk_student` (`student`),
  KEY `fk_old_bus` (`old_bus_line`),
  KEY `fk_new_bus` (`new_bus_line`),
  KEY `fk_new_station` (`new_station`),
  CONSTRAINT `fk_new_bus_line` FOREIGN KEY (`new_bus_line`) REFERENCES `bus_line` (`id`),
  CONSTRAINT `fk_new_station` FOREIGN KEY (`new_station`) REFERENCES `bus_stations` (`id`),
  CONSTRAINT `fk_old_bus_line` FOREIGN KEY (`old_bus_line`) REFERENCES `bus_line` (`id`),
  CONSTRAINT `fk_student` FOREIGN KEY (`student`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of booking_record
-- ----------------------------

-- ----------------------------
-- Table structure for `bus_base_info`
-- ----------------------------
DROP TABLE IF EXISTS `bus_base_info`;
CREATE TABLE `bus_base_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `number` varchar(255) NOT NULL COMMENT '校车编号',
  `plate_number` varchar(255) DEFAULT NULL COMMENT '车牌号',
  `plate_number_pic` varchar(255) DEFAULT NULL COMMENT '牌照照片存放路径',
  `bus_supplier` varchar(255) DEFAULT NULL COMMENT '供应商，外键',
  `bus_mom` int(10) unsigned DEFAULT NULL COMMENT '巴士妈妈，外键',
  `bus_driver` int(10) unsigned DEFAULT NULL COMMENT '司机',
  `school_partition` varchar(255) DEFAULT NULL COMMENT '浦东校区；浦西校区',
  `ipad_meid` varchar(255) DEFAULT NULL COMMENT 'ipad绑定的设备号',
  `valid` int(10) unsigned DEFAULT '1' COMMENT '1表示有效，0表示无效',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bus_mom` (`bus_mom`),
  KEY `fk_bus_driver` (`bus_driver`),
  CONSTRAINT `bus_base_info_ibfk_1` FOREIGN KEY (`bus_driver`) REFERENCES `user` (`id`),
  CONSTRAINT `bus_base_info_ibfk_2` FOREIGN KEY (`bus_mom`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_base_info
-- ----------------------------

-- ----------------------------
-- Table structure for `bus_line`
-- ----------------------------
DROP TABLE IF EXISTS `bus_line`;
CREATE TABLE `bus_line` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bus_base_info` int(10) unsigned DEFAULT NULL COMMENT '校车基本信息；晚班和校车不绑定',
  `mode` varchar(255) NOT NULL COMMENT '"上学“、”放学“、”晚班“',
  `stations` text,
  `name` varchar(160) NOT NULL DEFAULT '',
  `valid` int(10) unsigned DEFAULT '1' COMMENT '1表示有效，0表示无效',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`name`),
  KEY `fk_bus_base_info` (`bus_base_info`),
  CONSTRAINT `fk_bus_base_info` FOREIGN KEY (`bus_base_info`) REFERENCES `bus_base_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_line
-- ----------------------------

-- ----------------------------
-- Table structure for `bus_stations`
-- ----------------------------
DROP TABLE IF EXISTS `bus_stations`;
CREATE TABLE `bus_stations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `station_name` varchar(255) NOT NULL COMMENT '站点名称',
  `gps_info` varchar(255) DEFAULT NULL,
  `fare_rate` varchar(255) DEFAULT NULL COMMENT '收费信息',
  `remark` varchar(255) DEFAULT NULL COMMENT '站点的时间',
  `valid` int(10) DEFAULT '1' COMMENT '1表示有效，0表示无效',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `station_name` (`station_name`(191))
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_stations
-- ----------------------------

-- ----------------------------
-- Table structure for `device`
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '设备名称',
  `meid` varchar(255) NOT NULL COMMENT 'MEID地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES ('1', 'wxm', '111222');
INSERT INTO `device` VALUES ('2', 'ipad2', '222333');
INSERT INTO `device` VALUES ('3', 'ipad3', '3333aaa');

-- ----------------------------
-- Table structure for `messages`
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `send_time` datetime NOT NULL COMMENT '发布时间',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `publisher` varchar(255) NOT NULL COMMENT '发布人',
  `read_count` int(10) unsigned NOT NULL COMMENT '阅读次数',
  `content` text NOT NULL COMMENT '消息的内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `picked_students_info`
-- ----------------------------
DROP TABLE IF EXISTS `picked_students_info`;
CREATE TABLE `picked_students_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transport_record_id` int(10) unsigned NOT NULL COMMENT 'transport_record的id，也可以据此查到线路信息，再进一步可以查到该学生是否乘坐该线路',
  `board_time` datetime NOT NULL COMMENT '上下车时间',
  `student_id` int(10) unsigned NOT NULL COMMENT '学生(用ID表示）',
  PRIMARY KEY (`id`),
  KEY `fk_student_id` (`student_id`),
  KEY `fk_transport_record_id` (`transport_record_id`),
  CONSTRAINT `fk_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `fk_transport_record_id` FOREIGN KEY (`transport_record_id`) REFERENCES `transport_record` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  `role_des` varchar(255) DEFAULT NULL COMMENT '角色说明',
  `role_scope` text COMMENT '角色权限列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '超级管理员', '全局管理', '{\"datamanage\":[\"/home/datamanage/databus\",\"/home/datamanage/classdata\",\"/home/datamanage/recordquery\"],\"schoolbus\":[\"/home/schoolbus/busmanage\",\"/home/schoolbus/sitelib\",\"/home/schoolbus/nightway\",\"/home/schoolbus/linemamage\"],\"student\":[\"/home/student/studentmange\",\"/home/student/changemange\"],\"system\":[\"/home/system/user_manage\",\"/home/system/role_manage\",\"/home/system/data_sync\"]}');
INSERT INTO `role` VALUES ('2', '管理员', '管理', null);
INSERT INTO `role` VALUES ('3', '巴士妈妈', '巴士妈妈', null);
INSERT INTO `role` VALUES ('4', '班主任', '班主任', null);
INSERT INTO `role` VALUES ('5', '司机', '司机', null);

-- ----------------------------
-- Table structure for `student`
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `student_number` varchar(150) NOT NULL COMMENT '学号',
  `head_img` varchar(255) DEFAULT NULL COMMENT '保存头像的URL',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `banji` int(10) unsigned DEFAULT NULL COMMENT '班级,外键',
  `bus_line_morning` int(10) unsigned DEFAULT NULL COMMENT '上学乘坐车的线路ID，外键',
  `bus_line_afternoon` int(10) unsigned DEFAULT NULL COMMENT '放学乘坐车的线路ID，外键',
  `board_station_morning` int(10) unsigned DEFAULT NULL COMMENT '上午 班车上车站点',
  `board_station_afternoon` int(10) unsigned DEFAULT NULL COMMENT '下午 班车下车站点',
  `family_info` text COMMENT '家庭信息 JSON',
  `valid` int(10) DEFAULT '1' COMMENT '1表示有效，0表示无效',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`student_number`),
  KEY `fk_bus` (`bus_line_morning`),
  KEY `fk_banji` (`banji`),
  KEY `fk_board_station_morning` (`board_station_morning`),
  KEY `fk_board_station_afternoon` (`board_station_afternoon`),
  KEY `fk_bus_afternoon` (`bus_line_afternoon`),
  KEY `student_number` (`student_number`),
  CONSTRAINT `fk_banji` FOREIGN KEY (`banji`) REFERENCES `banji` (`id`),
  CONSTRAINT `fk_board_station_afternoon` FOREIGN KEY (`board_station_afternoon`) REFERENCES `bus_stations` (`id`),
  CONSTRAINT `fk_board_station_morning` FOREIGN KEY (`board_station_morning`) REFERENCES `bus_stations` (`id`),
  CONSTRAINT `fk_bus_line_afternoon` FOREIGN KEY (`bus_line_afternoon`) REFERENCES `bus_line` (`id`),
  CONSTRAINT `fk_bus_line_morning` FOREIGN KEY (`bus_line_morning`) REFERENCES `bus_line` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of student
-- ----------------------------

-- ----------------------------
-- Table structure for `transport_range`
-- ----------------------------

-- ----------------------------
-- Records of transport_range
-- ----------------------------

-- ----------------------------
-- Table structure for `transport_record`
-- ----------------------------
DROP TABLE IF EXISTS `transport_record`;
CREATE TABLE `transport_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '接送班次信息，一趟对应一条记录',
  `date` date NOT NULL COMMENT '接送日期',
  `bus_line` int(10) unsigned NOT NULL COMMENT '校车线路，外键，',
  `current_station` int(10) unsigned DEFAULT NULL COMMENT '校车所处的当前站点',
  `flag` varchar(255) NOT NULL COMMENT '早上上车、放学上车、放学下车、晚班上车',
  `bus_number_in_tr` varchar(255) NOT NULL COMMENT '校车编号，因为晚班的线路和校车不绑定，所以需要记录校车. 命名后缀in_tr是为了不影响其他地方的bus_number',
  `status` varchar(255) NOT NULL COMMENT '上学待开始/上学进行中/上学已结束 /放学进行中/放学已结束/晚班进行中/晚班已结束',
  `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_tr_bus` (`bus_line`),
  KEY `fk_current_station` (`current_station`),
  CONSTRAINT `fk_bus_line` FOREIGN KEY (`bus_line`) REFERENCES `bus_line` (`id`),
  CONSTRAINT `fk_current_station` FOREIGN KEY (`current_station`) REFERENCES `bus_stations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of transport_record
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account` varchar(255) CHARACTER SET utf8 NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `wechatUnionId` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '微信unionId，在没授权前是空的。',
  `role_id` int(10) unsigned NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 NOT NULL,
  `head_image` varchar(255) DEFAULT NULL COMMENT '头像保存位置',
  `phone` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '电话',
  `create_time` datetime NOT NULL,
  `valid` int(10) DEFAULT '1' COMMENT '是否在职 ， “1”:在职 “0”:离职',
  `school_staff_code` varchar(255) DEFAULT NULL COMMENT '学校的登录账户，比如教师工号',
  PRIMARY KEY (`id`),
  KEY `fk_u_role_id` (`role_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'admin', 'wechat222', '1', '1', '', '13188888888', '2018-07-11 10:03:43', '1', null);

-- ----------------------------
-- Table structure for `user_msg_status_info`
-- ----------------------------
DROP TABLE IF EXISTS `user_msg_status_info`;
CREATE TABLE `user_msg_status_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `message_id` int(10) unsigned NOT NULL COMMENT '消息ID，',
  `status` varchar(255) NOT NULL COMMENT '消息状态，比如”未读“、“已读”、“删除"',
  `user` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_msg` (`message_id`),
  KEY `fk_msg_user` (`user`),
  CONSTRAINT `fk_msg` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`),
  CONSTRAINT `fk_msg_user` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_msg_status_info
-- ----------------------------
