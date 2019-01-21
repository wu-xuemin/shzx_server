/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50553
Source Host           : localhost:3306
Source Database       : shangzhong_db

Target Server Type    : MYSQL
Target Server Version : 50553
File Encoding         : 65001

Date: 2019-01-20 20:11:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `banji`
-- ----------------------------
DROP TABLE IF EXISTS `banji`;
CREATE TABLE `banji` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `grade` varchar(255) NOT NULL COMMENT '年级',
  `class_name` varchar(255) NOT NULL COMMENT '班级名称',
  `charge_teacher` int(10) unsigned NOT NULL COMMENT '班主任',
  PRIMARY KEY (`id`),
  KEY `fk_charge_teacher` (`charge_teacher`),
  CONSTRAINT `fk_charge_teacher` FOREIGN KEY (`charge_teacher`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of banji
-- ----------------------------
INSERT INTO `banji` VALUES ('1', '1', '1年级1班', '11');
INSERT INTO `banji` VALUES ('2', '1', '1年级2班', '12');
INSERT INTO `banji` VALUES ('3', '1', '1年级3班', '13');
INSERT INTO `banji` VALUES ('9', '2', '2年级1班', '14');
INSERT INTO `banji` VALUES ('10', '', '2年级2班', '15');

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
  `plate_number` varchar(255) NOT NULL COMMENT '车牌号',
  `plate_number_pic` varchar(255) NOT NULL COMMENT '牌照照片存放路径',
  `bus_supplier` varchar(255) NOT NULL COMMENT '供应商，外键',
  `bus_mom` int(10) unsigned NOT NULL COMMENT '巴士妈妈，外键',
  `bus_driver` int(10) unsigned NOT NULL COMMENT '司机',
  `school_partition` varchar(255) NOT NULL COMMENT '浦东校区；浦西校区',
  `ipad_meid` varchar(255) NOT NULL COMMENT 'ipad绑定的设备号',
  `valid` tinyint(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bus_mom` (`bus_mom`),
  KEY `fk_bus_driver` (`bus_driver`),
  CONSTRAINT `bus_base_info_ibfk_1` FOREIGN KEY (`bus_driver`) REFERENCES `user` (`id`),
  CONSTRAINT `bus_base_info_ibfk_2` FOREIGN KEY (`bus_mom`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_base_info
-- ----------------------------
INSERT INTO `bus_base_info` VALUES ('1', 'XC001', '沪A1111', '', '港湾校车有限公司', '2', '21', '浦东', 'meid111', '1');
INSERT INTO `bus_base_info` VALUES ('35', 'XC002', '沪A22222', '', '港湾校车有限公司', '3', '22', '浦东', 'meid222', '1');
INSERT INTO `bus_base_info` VALUES ('36', 'XC003', '沪A33333', '', '港湾校车有限公司', '4', '23', '浦东', 'meid333', '1');
INSERT INTO `bus_base_info` VALUES ('37', 'XC004', '沪A4444C', '', '小卫校车有限公司', '5', '24', '浦西', 'meid4444', '1');
INSERT INTO `bus_base_info` VALUES ('38', 'XC005', '沪A55555', '', '小卫校车有限公司', '6', '25', '浦西', 'mdid5555', '1');

-- ----------------------------
-- Table structure for `bus_line`
-- ----------------------------
DROP TABLE IF EXISTS `bus_line`;
CREATE TABLE `bus_line` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bus_base_info` int(10) unsigned NOT NULL COMMENT '校车基本信息',
  `transport_range` int(10) unsigned NOT NULL COMMENT '班车区间，包含了夜班路线',
  `mode` varchar(255) NOT NULL COMMENT '"早班“、”午班“、”晚班“',
  PRIMARY KEY (`id`),
  KEY `fk_bus_base_info` (`bus_base_info`),
  KEY `fk_transport_range` (`transport_range`),
  CONSTRAINT `fk_bus_base_info` FOREIGN KEY (`bus_base_info`) REFERENCES `bus_base_info` (`id`),
  CONSTRAINT `fk_transport_range` FOREIGN KEY (`transport_range`) REFERENCES `transport_range` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_line
-- ----------------------------
INSERT INTO `bus_line` VALUES ('34', '1', '1', '早班');
INSERT INTO `bus_line` VALUES ('38', '35', '5', '早班');
INSERT INTO `bus_line` VALUES ('39', '36', '6', '早班');
INSERT INTO `bus_line` VALUES ('40', '37', '7', '早班');
INSERT INTO `bus_line` VALUES ('41', '38', '8', '早班');
INSERT INTO `bus_line` VALUES ('42', '1', '1', '午班');
INSERT INTO `bus_line` VALUES ('43', '35', '5', '午班');
INSERT INTO `bus_line` VALUES ('46', '36', '6', '午班');
INSERT INTO `bus_line` VALUES ('47', '38', '8', '午班');
INSERT INTO `bus_line` VALUES ('48', '37', '7', '午班');
INSERT INTO `bus_line` VALUES ('49', '1', '10', '晚班');
INSERT INTO `bus_line` VALUES ('50', '35', '11', '晚班');

-- ----------------------------
-- Table structure for `bus_stations`
-- ----------------------------
DROP TABLE IF EXISTS `bus_stations`;
CREATE TABLE `bus_stations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `station_name` varchar(255) NOT NULL COMMENT '站点名称',
  `gps_info` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `station_name` (`station_name`(191))
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_stations
-- ----------------------------
INSERT INTO `bus_stations` VALUES ('1', '1号路口', '');
INSERT INTO `bus_stations` VALUES ('11', '11路口', '');
INSERT INTO `bus_stations` VALUES ('12', '22路口', '');
INSERT INTO `bus_stations` VALUES ('13', '33路口', '');
INSERT INTO `bus_stations` VALUES ('14', 'AA路口', '');
INSERT INTO `bus_stations` VALUES ('15', 'bb路口', '');
INSERT INTO `bus_stations` VALUES ('16', '人民路口', '');
INSERT INTO `bus_stations` VALUES ('17', '晚霞路口', '');
INSERT INTO `bus_stations` VALUES ('18', '人民广场', '');
INSERT INTO `bus_stations` VALUES ('19', '44路口', '');
INSERT INTO `bus_stations` VALUES ('20', '秋色路口', '');
INSERT INTO `bus_stations` VALUES ('21', '新胜路口', '');
INSERT INTO `bus_stations` VALUES ('22', '10路口', '');
INSERT INTO `bus_stations` VALUES ('23', 'CC口', '');

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '设备名称',
  `meid` varchar(255) NOT NULL COMMENT 'MEID地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES ('1', 'wxm', '111222');
INSERT INTO `device` VALUES ('2', 'ipad2', '222333');
INSERT INTO `device` VALUES ('3', 'ipad3', '3333aaa');
INSERT INTO `device` VALUES ('4', 'Dvname', 'abc111');

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES ('1', '2019-01-11 11:51:48', 'title---放假通知111', '校办', '0', '涛涛涛涛通知内容1111');
INSERT INTO `messages` VALUES ('2', '2019-01-12 11:52:56', 'title---开会通知2222', '校车管理办公室', '0', '开关开会通知内容0000');
INSERT INTO `messages` VALUES ('4', '2019-01-11 13:53:36', '学习通知', '校车办', '0', '学西嘻嘻嘻嘻嘻嘻嘻嘻');

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of picked_students_info
-- ----------------------------
INSERT INTO `picked_students_info` VALUES ('23', '11', '2019-01-01 14:10:03', '16');
INSERT INTO `picked_students_info` VALUES ('24', '11', '2019-01-01 14:14:10', '17');
INSERT INTO `picked_students_info` VALUES ('25', '12', '2019-01-01 14:14:40', '18');
INSERT INTO `picked_students_info` VALUES ('26', '14', '2019-01-11 14:16:38', '16');
INSERT INTO `picked_students_info` VALUES ('27', '14', '2019-01-11 14:16:50', '17');
INSERT INTO `picked_students_info` VALUES ('28', '14', '2019-01-11 15:17:00', '18');
INSERT INTO `picked_students_info` VALUES ('29', '21', '2019-01-12 11:06:03', '16');
INSERT INTO `picked_students_info` VALUES ('30', '21', '2019-01-12 11:06:24', '24');
INSERT INTO `picked_students_info` VALUES ('31', '22', '2019-01-12 11:06:43', '16');
INSERT INTO `picked_students_info` VALUES ('33', '22', '2019-01-12 11:07:00', '17');
INSERT INTO `picked_students_info` VALUES ('34', '23', '2019-01-12 11:07:36', '24');
INSERT INTO `picked_students_info` VALUES ('35', '23', '2019-01-12 11:07:54', '20');
INSERT INTO `picked_students_info` VALUES ('37', '22', '2019-01-14 10:28:54', '1');
INSERT INTO `picked_students_info` VALUES ('39', '19', '2019-01-12 08:37:26', '16');

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  `role_des` varchar(255) DEFAULT NULL COMMENT '角色说明',
  `role_scope` text COMMENT '角色权限列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '超级管理员', '全局管理', '');
INSERT INTO `role` VALUES ('2', '管理员', '管理', null);
INSERT INTO `role` VALUES ('3', '巴士妈妈', '巴士妈妈', null);
INSERT INTO `role` VALUES ('4', '班主任', '班主任', null);
INSERT INTO `role` VALUES ('5', '司机', '司机', null);
INSERT INTO `role` VALUES ('6', 'test11', 'test11des', null);

-- ----------------------------
-- Table structure for `student`
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `student_number` varchar(150) NOT NULL COMMENT '学号',
  `head_img` varchar(255) DEFAULT NULL COMMENT '保存头像的URL',
  `name` varchar(255) NOT NULL COMMENT '姓名',
  `banji` int(10) unsigned NOT NULL COMMENT '班级,外键',
  `bus_line_morning` int(10) unsigned NOT NULL COMMENT '早班乘坐车的线路ID，外键',
  `bus_line_afternoon` int(10) unsigned NOT NULL COMMENT '午班乘坐车的线路ID，外键',
  `board_station_morning` int(10) unsigned NOT NULL COMMENT '上午班车上车站点',
  `board_station_afternoon` int(10) unsigned NOT NULL COMMENT '下午班车下车站点',
  `family_info` text COMMENT '家庭信息 JSON',
  PRIMARY KEY (`id`,`student_number`),
  KEY `fk_bus` (`bus_line_morning`),
  KEY `fk_banji` (`banji`),
  KEY `fk_board_station_morning` (`board_station_morning`),
  KEY `fk_board_station_afternoon` (`board_station_afternoon`),
  KEY `fk_bus_afternoon` (`bus_line_afternoon`),
  CONSTRAINT `fk_banji` FOREIGN KEY (`banji`) REFERENCES `banji` (`id`),
  CONSTRAINT `fk_board_station_afternoon` FOREIGN KEY (`board_station_afternoon`) REFERENCES `bus_stations` (`id`),
  CONSTRAINT `fk_board_station_morning` FOREIGN KEY (`board_station_morning`) REFERENCES `bus_stations` (`id`),
  CONSTRAINT `fk_bus_line_afternoon` FOREIGN KEY (`bus_line_afternoon`) REFERENCES `bus_line` (`id`),
  CONSTRAINT `fk_bus_line_morning` FOREIGN KEY (`bus_line_morning`) REFERENCES `bus_line` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('1', 'xh888', '/shzx/xh888.png', '武都头', '9', '34', '42', '13', '13', '');
INSERT INTO `student` VALUES ('16', 'xh001', '/shzx/xh001.png', '张小名', '1', '34', '42', '21', '21', '');
INSERT INTO `student` VALUES ('17', 'xh002', '/shzx/xh002.png', '王小明', '1', '34', '42', '12', '12', '');
INSERT INTO `student` VALUES ('18', 'XH003', '/shzx/xh003.png', '王小丫', '1', '38', '43', '14', '14', '');
INSERT INTO `student` VALUES ('19', 'xh021', '/shzx/xh021.png', '张晓婷', '9', '39', '46', '16', '16', '');
INSERT INTO `student` VALUES ('20', 'xh020', '/shzx/xh020.png', '黄丽', '10', '40', '48', '19', '19', '');
INSERT INTO `student` VALUES ('21', 'xh023', '/shzx/xh023.png', '王语嫣', '10', '41', '47', '11', '19', '');
INSERT INTO `student` VALUES ('23', 'XH022', '/shzx/xh022.png', '王笑笑', '2', '34', '42', '23', '23', '');
INSERT INTO `student` VALUES ('24', 'xh0023', '/shzx/xh000023.png', '张成', '3', '38', '43', '13', '13', '');
INSERT INTO `student` VALUES ('25', 'xh222', '/shzx/xh222.png', '高圆圆', '1', '34', '42', '22', '22', '');
INSERT INTO `student` VALUES ('26', 'xh444', '/shzx/xh444.png', '吴彦祖', '1', '34', '42', '23', '23', '');

-- ----------------------------
-- Table structure for `transport_range`
-- ----------------------------
DROP TABLE IF EXISTS `transport_range`;
CREATE TABLE `transport_range` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `stations` text NOT NULL COMMENT '区间的站点名称，JSON\r\n\r\n[{"station_name": "AA路口"} ,\r\n{ "station_name": "BB口"},\r\n{ "station_name": "CC口"}\r\n]\r\n',
  `range_name` varchar(255) NOT NULL COMMENT '区间名字（通常是首尾站点名称）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of transport_range
-- ----------------------------
INSERT INTO `transport_range` VALUES ('1', '[{\"station_name\": \"新胜路口\"} ,{ \"station_name\": \"10路口\"},{ \"station_name\": \"CC口\"}]', '区间1线');
INSERT INTO `transport_range` VALUES ('5', '[{\"station_name\": \"AA路口\"} ,{ \"station_name\": \"BB口\"},{ \"station_name\": \"CC口\"}]', '区间111');
INSERT INTO `transport_range` VALUES ('6', '[{\"station_name\": \"11路口\"} ,{ \"station_name\": \"22口\"},{ \"station_name\": \"33路口\"}]', '区间2线');
INSERT INTO `transport_range` VALUES ('7', '[{\"station_name\": \"33路口\"} ,{ \"station_name\": \"44口\"},{ \"station_name\": \"ff口\"}]', '区间3线');
INSERT INTO `transport_range` VALUES ('8', '[{\"station_name\": \"44路口\"} ,{ \"station_name\": \"33B口\"},{ \"station_name\": \"xx路口\"},{ \"station_name\": \"CC口\"}]', '区间4线');
INSERT INTO `transport_range` VALUES ('9', '[{\"station_name\": \"人民路口\"} ,{ \"station_name\": \"胜利路口\"},{ \"station_name\": \"xx路口\"},{ \"station_name\": \"CC口\"}]', '区间5线');
INSERT INTO `transport_range` VALUES ('10', '[{\"station_name\": \"晚霞路口\"} ,{ \"station_name\": \"秋色路口\"},{ \"station_name\": \"枫林路口\"},{ \"station_name\": \"CC口\"}]', '区间6线');
INSERT INTO `transport_range` VALUES ('11', '[{\"station_name\": \"云上路口\"} ,{ \"station_name\": \"小港路口\"},{ \"station_name\": \"xx路口\"},{ \"station_name\": \"CC口\"}]', '区间7线');

-- ----------------------------
-- Table structure for `transport_record`
-- ----------------------------
DROP TABLE IF EXISTS `transport_record`;
CREATE TABLE `transport_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '接送班次信息，一趟对应一条记录',
  `date` date NOT NULL COMMENT '接送日期',
  `bus_line` int(10) unsigned NOT NULL COMMENT '校车线路，外键，',
  `current_station` int(10) unsigned DEFAULT NULL COMMENT '校车所处的当前站点',
  PRIMARY KEY (`id`),
  KEY `fk_tr_bus` (`bus_line`),
  KEY `fk_current_station` (`current_station`),
  CONSTRAINT `fk_bus_line` FOREIGN KEY (`bus_line`) REFERENCES `bus_line` (`id`),
  CONSTRAINT `fk_current_station` FOREIGN KEY (`current_station`) REFERENCES `bus_stations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of transport_record
-- ----------------------------
INSERT INTO `transport_record` VALUES ('11', '2019-01-01', '34', null);
INSERT INTO `transport_record` VALUES ('12', '2019-01-01', '38', null);
INSERT INTO `transport_record` VALUES ('13', '2019-01-01', '39', null);
INSERT INTO `transport_record` VALUES ('14', '2019-01-11', '34', null);
INSERT INTO `transport_record` VALUES ('15', '2019-01-11', '38', null);
INSERT INTO `transport_record` VALUES ('16', '2019-01-11', '39', null);
INSERT INTO `transport_record` VALUES ('17', '2019-01-11', '40', null);
INSERT INTO `transport_record` VALUES ('18', '2019-01-11', '41', null);
INSERT INTO `transport_record` VALUES ('19', '2019-01-12', '34', null);
INSERT INTO `transport_record` VALUES ('20', '2019-01-12', '38', null);
INSERT INTO `transport_record` VALUES ('21', '2019-01-12', '42', null);
INSERT INTO `transport_record` VALUES ('22', '2019-01-12', '43', null);
INSERT INTO `transport_record` VALUES ('23', '2019-01-12', '43', null);
INSERT INTO `transport_record` VALUES ('25', '2019-01-14', '38', '11');
INSERT INTO `transport_record` VALUES ('26', '2019-01-14', '38', '12');
INSERT INTO `transport_record` VALUES ('27', '2019-01-14', '38', '12');
INSERT INTO `transport_record` VALUES ('28', '2019-01-14', '38', '12');

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
  `phone` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '电话',
  `create_time` datetime NOT NULL,
  `valid` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '是否在职 ， “1”:在职 “0”:离职',
  PRIMARY KEY (`id`),
  KEY `fk_u_role_id` (`role_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'admin', 'wechat222', '1', '1', '', '13188888888', '2018-07-11 10:03:43', '1');
INSERT INTO `user` VALUES ('2', 'busMom1', 'xiaozhang', null, '3', '1', null, '13588027825', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('3', 'busMom2', 'lily', null, '3', '1', null, '13033332222', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('4', 'busMom4', 'busmm4', null, '3', '1', null, '13800009999', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('5', 'busMom5', 'busMom5', null, '3', '1', null, '13066663333', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('6', 'busMom6', 'busMom6', null, '3', '1', null, '13022223333', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('11', 'bzr1', 'bzr_tom', null, '4', '1', null, '13600003333', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('12', 'bzr2', 'bzr_tim', null, '4', '1', null, '13600004444', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('13', 'bzr3', 'bzr_jack', null, '4', '1', null, '13033332211', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('14', 'bzr4', 'bzr_peter', null, '4', '1', null, '13622223333', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('15', 'bzr5', 'bzr5', null, '4', '1', null, '12300006666', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('21', 'sj1', 'sj_liusan', null, '5', '1', null, '13699990000', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('22', 'sj2', 'sj2_zhangsan', null, '5', '1', null, '13055550000', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('23', 'sj3', 'sj3_xiaowu', null, '5', '1', null, '13600022222', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('24', 'sj4', 'sj_4', null, '5', '1', null, '13699990000', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('25', 'sj5', 'sj5小张', null, '5', '1', null, '13699995566', '0000-00-00 00:00:00', '1');
INSERT INTO `user` VALUES ('26', 'busMom-swg', 'busmm-swg', '', '3', 'password', '', '13800009123', '2019-01-12 15:13:14', '1');

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
INSERT INTO `user_msg_status_info` VALUES ('1', '1', '未读', '2');
INSERT INTO `user_msg_status_info` VALUES ('2', '1', '未读', '3');
INSERT INTO `user_msg_status_info` VALUES ('3', '1', '未读', '4');
INSERT INTO `user_msg_status_info` VALUES ('4', '2', '未读', '2');
INSERT INTO `user_msg_status_info` VALUES ('5', '2', '已读', '3');
INSERT INTO `user_msg_status_info` VALUES ('6', '2', '已读', '4');
INSERT INTO `user_msg_status_info` VALUES ('7', '4', '已读', '2');
INSERT INTO `user_msg_status_info` VALUES ('8', '4', '未读', '3');
