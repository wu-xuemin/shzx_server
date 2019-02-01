/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50553
Source Host           : localhost:3306
Source Database       : shangzhong_db

Target Server Type    : MYSQL
Target Server Version : 50553
File Encoding         : 65001

Date: 2019-02-01 18:01:32
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
  `charge_teacher` int(10) unsigned DEFAULT NULL COMMENT '班主任',
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
  `plate_number` varchar(255) DEFAULT NULL COMMENT '车牌号',
  `plate_number_pic` varchar(255) DEFAULT NULL COMMENT '牌照照片存放路径',
  `bus_supplier` varchar(255) DEFAULT NULL COMMENT '供应商，外键',
  `bus_mom` int(10) unsigned DEFAULT NULL COMMENT '巴士妈妈，外键',
  `bus_driver` int(10) unsigned DEFAULT NULL COMMENT '司机',
  `school_partition` varchar(255) DEFAULT NULL COMMENT '浦东校区；浦西校区',
  `ipad_meid` varchar(255) DEFAULT NULL COMMENT 'ipad绑定的设备号',
  `valid` tinyint(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bus_mom` (`bus_mom`),
  KEY `fk_bus_driver` (`bus_driver`),
  CONSTRAINT `bus_base_info_ibfk_1` FOREIGN KEY (`bus_driver`) REFERENCES `user` (`id`),
  CONSTRAINT `bus_base_info_ibfk_2` FOREIGN KEY (`bus_mom`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_base_info
-- ----------------------------
INSERT INTO `bus_base_info` VALUES ('1', 'XC001', '沪A1111', '', '港湾校车有限公司', '2', '21', '浦东', 'meid111', '1');
INSERT INTO `bus_base_info` VALUES ('35', 'XC002', '沪A22222', '', '港湾校车有限公司', '3', '22', '浦东', 'meid222', '1');
INSERT INTO `bus_base_info` VALUES ('36', 'XC003', '沪A33333', '', '港湾校车有限公司', '4', '23', '浦东', 'meid333', '1');
INSERT INTO `bus_base_info` VALUES ('37', 'XC004', '沪A4444C', '', '小卫校车有限公司', '5', '24', '浦西', 'meid4444', '1');
INSERT INTO `bus_base_info` VALUES ('38', 'XC005', '沪A55555', '', '小卫校车有限公司', '6', '25', '浦西', 'mdid5555', '1');
INSERT INTO `bus_base_info` VALUES ('62', '1.0', '沪DA5953', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('63', '2.0', '沪BT7607', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('64', '3.0', '沪D42545', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('65', '4.0', '沪D45333', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('66', '5.0', '沪BT1548', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('67', '6.0', '沪D42540', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('68', '7.0', '沪AZ0007', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('69', '8.0', '沪D59950', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('70', '9.0', '沪D59969', null, null, null, null, '浦西', null, null);
INSERT INTO `bus_base_info` VALUES ('71', '10.0', '沪EQ7737', null, null, null, null, '浦西', null, null);

-- ----------------------------
-- Table structure for `bus_line`
-- ----------------------------
DROP TABLE IF EXISTS `bus_line`;
CREATE TABLE `bus_line` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bus_base_info` int(10) unsigned NOT NULL COMMENT '校车基本信息',
  `mode` varchar(255) NOT NULL COMMENT '"早班“、”午班“、”晚班“',
  `stations` text,
  `name` varchar(160) NOT NULL DEFAULT '',
  `valid` int(10) unsigned DEFAULT '1' COMMENT '1表示有效，0表示无效',
  PRIMARY KEY (`id`,`name`),
  KEY `fk_bus_base_info` (`bus_base_info`),
  CONSTRAINT `fk_bus_base_info` FOREIGN KEY (`bus_base_info`) REFERENCES `bus_base_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_line
-- ----------------------------
INSERT INTO `bus_line` VALUES ('34', '1', '早班', null, '', '1');
INSERT INTO `bus_line` VALUES ('38', '35', '早班', null, '', '1');
INSERT INTO `bus_line` VALUES ('39', '36', '早班', null, '', '1');
INSERT INTO `bus_line` VALUES ('40', '37', '早班', null, '', '1');
INSERT INTO `bus_line` VALUES ('41', '38', '早班', null, '', '1');
INSERT INTO `bus_line` VALUES ('42', '1', '午班', null, '', '1');
INSERT INTO `bus_line` VALUES ('43', '35', '午班', null, '', '1');
INSERT INTO `bus_line` VALUES ('46', '36', '午班', null, '', '1');
INSERT INTO `bus_line` VALUES ('47', '38', '午班', null, '', '1');
INSERT INTO `bus_line` VALUES ('48', '37', '午班', null, '', '1');
INSERT INTO `bus_line` VALUES ('135', '62', '早班', '中山南一路500弄', '1号车_早班', '1');
INSERT INTO `bus_line` VALUES ('136', '63', '早班', '爱建园田川路门', '2号车_早班', '1');
INSERT INTO `bus_line` VALUES ('137', '64', '早班', '虹桥路2419号', '3号车_早班', '1');
INSERT INTO `bus_line` VALUES ('138', '65', '早班', '普杰路69弄（锦梅路普杰路口，临时）', '4号车_早班', '1');
INSERT INTO `bus_line` VALUES ('139', '66', '早班', '东大名路591号（白玉兰广场）', '5号车_早班', '1');
INSERT INTO `bus_line` VALUES ('140', '67', '早班', '华山路1038弄嘉里华庭一期', '6号车_早班', '1');
INSERT INTO `bus_line` VALUES ('141', '68', '早班', '商城路99号仁恒滨江园', '7号车_早班', '1');
INSERT INTO `bus_line` VALUES ('142', '69', '早班', '古北瑞仕花园（红宝石路玛瑙路口）', '8号车_早班', '1');
INSERT INTO `bus_line` VALUES ('143', '70', '早班', '黄桦路369弄（临时）', '9号车_早班', '1');
INSERT INTO `bus_line` VALUES ('144', '71', '早班', '红松东路1099弄古北壹号', '10号车_早班', '1');

-- ----------------------------
-- Table structure for `bus_stations`
-- ----------------------------
DROP TABLE IF EXISTS `bus_stations`;
CREATE TABLE `bus_stations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `station_name` varchar(255) NOT NULL COMMENT '站点名称',
  `gps_info` varchar(255) DEFAULT NULL,
  `fare_rate` varchar(255) DEFAULT NULL COMMENT '收费信息',
  `remark` time DEFAULT NULL COMMENT '站点的时间',
  `valid` int(11) DEFAULT NULL COMMENT '1表示有效，0表示无效',
  PRIMARY KEY (`id`),
  KEY `station_name` (`station_name`(191))
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bus_stations
-- ----------------------------
INSERT INTO `bus_stations` VALUES ('1', '1号路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('11', '11路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('12', '22路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('13', '33路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('14', 'AA路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('15', 'bb路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('16', '人民路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('17', '晚霞路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('18', '人民广场', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('19', '44路口修改了', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('20', '秋色路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('21', '新胜路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('22', '10路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('23', 'CC口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('24', '阿德路口', '', null, null, null);
INSERT INTO `bus_stations` VALUES ('25', '康桥半岛五期恒和中路门', null, '4800.0', '07:00:00', null);
INSERT INTO `bus_stations` VALUES ('26', '秀沿路1155弄', null, '4800.0', '07:02:00', null);
INSERT INTO `bus_stations` VALUES ('27', '秀沿路1177弄恒和中路门', null, '4800.0', '07:04:00', null);
INSERT INTO `bus_stations` VALUES ('28', '沪南路2731弄康桥半岛', null, '4800.0', '07:06:00', null);
INSERT INTO `bus_stations` VALUES ('29', '康桥路1111弄（临时）', null, '4800.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('30', '御水路仁和都市花园（临时）', null, '4800.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('31', '御桥路1679号', null, '4800.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('32', '胶州路1118弄', null, '4800.0', '07:00:00', null);
INSERT INTO `bus_stations` VALUES ('33', '长寿路800弄苏堤春晓', null, '4800.0', '07:03:00', null);
INSERT INTO `bus_stations` VALUES ('34', '长寿路1028弄上海知音苑 ', null, '4800.0', '07:07:00', null);
INSERT INTO `bus_stations` VALUES ('35', '延平路85号', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('36', '万航渡路239弄（临时）', null, '4200.0', '07:19:00', null);
INSERT INTO `bus_stations` VALUES ('37', '闵行体育公园公交车站（临时）', null, '4200.0', '06:55:00', null);
INSERT INTO `bus_stations` VALUES ('38', '水清路1028弄', null, '4200.0', '07:05:00', null);
INSERT INTO `bus_stations` VALUES ('39', '报春路山花路口', null, '4200.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('40', '报春路虹莘路公交车站', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('41', '报春路龙茗路公交车站', null, '4200.0', '07:23:00', null);
INSERT INTO `bus_stations` VALUES ('42', '碧云路1188号（临时）', null, '4800.0', '07:04:00', null);
INSERT INTO `bus_stations` VALUES ('43', '碧云路199弄', null, '4800.0', '07:08:00', null);
INSERT INTO `bus_stations` VALUES ('44', '明月路188弄世茂湖滨花园', null, '4800.0', '07:12:00', null);
INSERT INTO `bus_stations` VALUES ('45', '云山路2000弄', null, '4800.0', '07:14:00', null);
INSERT INTO `bus_stations` VALUES ('46', '锦绣东路白桦路口（临时）', null, '4800.0', '07:16:00', null);
INSERT INTO `bus_stations` VALUES ('47', '羽山路100弄', null, '4800.0', '07:03:00', null);
INSERT INTO `bus_stations` VALUES ('48', '羽山路308弄陆家嘴花园二期', null, '4800.0', '07:05:00', null);
INSERT INTO `bus_stations` VALUES ('49', '羽山路383弄陆家嘴美丽苑(山水国际)', null, '4800.0', '07:06:00', null);
INSERT INTO `bus_stations` VALUES ('50', '万源杰座锦和路门', null, '4800.0', '07:19:00', null);
INSERT INTO `bus_stations` VALUES ('51', '锦和路99弄', null, '4800.0', '07:21:00', null);
INSERT INTO `bus_stations` VALUES ('52', '丁香路1399弄仁恒河滨城二期', null, '4800.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('53', '当代清水园丁香路门', null, '4800.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('54', '丁香路1089弄', null, '4800.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('55', '金汇南路301弄锦绣江南四期', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('56', '宜山路2328弄九歌上君 ', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('57', '银冬路568号', null, '4800.0', '06:58:00', null);
INSERT INTO `bus_stations` VALUES ('58', '青桐路618弄', null, '4800.0', '07:05:00', null);
INSERT INTO `bus_stations` VALUES ('59', '青桐路409弄', null, '4800.0', '07:06:00', null);
INSERT INTO `bus_stations` VALUES ('60', '青桐路333弄', null, '4800.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('61', '青桐路168弄', null, '4800.0', '07:12:00', null);
INSERT INTO `bus_stations` VALUES ('62', '汤臣豪庭香楠路门', null, '4800.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('63', '香楠路408弄', null, '4800.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('64', '广兰路地铁站', null, '4800.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('65', '虹莘路3799弄', null, '4200.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('66', '虹莘路3800弄风度国际', null, '4200.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('67', '虹莘路3333号天安豪园', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('68', '龙茗路2121弄金汇豪庭', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('69', '虹桥路1778弄（临时）', null, '4200.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('70', '虹桥路1720号美丽华花园(水城路门)', null, '4200.0', '07:11:00', null);
INSERT INTO `bus_stations` VALUES ('71', '虹梅路3887号', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('72', '虹梅路3297号华光花苑', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('73', '虹梅路程家桥支路口', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('74', '黄金城道881号浦东发展银行', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('75', '古北新苑翠玉路门', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('76', '红松东路699号', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('77', '名都城（古羊路门外接送）', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('78', '蓝宝石81号', null, '4200.0', '07:16:00', null);
INSERT INTO `bus_stations` VALUES ('79', '伊梨南路500弄', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('80', '黄金城道259弄', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('81', '姚虹路299弄恒盛苑', null, '4200.0', '07:24:00', null);
INSERT INTO `bus_stations` VALUES ('82', '碧波路49弄（临时）', null, '4800.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('83', '松涛路200号', null, '4800.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('84', '晨晖路377弄', null, '4800.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('85', '晨晖路820弄汤臣豪园四期', null, '4800.0', '07:19:00', null);
INSERT INTO `bus_stations` VALUES ('86', '晨晖路825弄汤臣豪园二期', null, '4800.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('87', '金科路中科路公交车站', null, '4800.0', '07:26:00', null);
INSERT INTO `bus_stations` VALUES ('88', '天山路288弄', null, '4200.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('89', '威宁路511弄长宁路门', null, '4800.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('90', '水城路883弄天山河畔花苑', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('91', '安西路500弄鸿凯湾绿苑', null, '4200.0', '07:03:00', null);
INSERT INTO `bus_stations` VALUES ('92', '愚园公馆凤冈路门', null, '4200.0', '07:06:00', null);
INSERT INTO `bus_stations` VALUES ('93', '安西路23弄', null, '4200.0', '07:08:00', null);
INSERT INTO `bus_stations` VALUES ('94', '中山公园公交车站', null, '4200.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('95', '安化路凯旋路口', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('96', '方家塘路33弄紫郡公馆（临时）', null, '4800.0', '06:50:00', null);
INSERT INTO `bus_stations` VALUES ('97', '谢卫路168号久事西郊花园网球场  ', null, '4800.0', '07:00:00', null);
INSERT INTO `bus_stations` VALUES ('98', '诸光路900弄东方明珠花园               ', null, '4800.0', '07:05:00', null);
INSERT INTO `bus_stations` VALUES ('99', '金汇南路91弄锦绣江南二期 ', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('100', '金汇南路60弄锦绣江南一期', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('101', '环镇南路200弄', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('102', '花园石桥路28号', null, '4800.0', '07:08:00', null);
INSERT INTO `bus_stations` VALUES ('103', '仁恒滨江园（东昌路门）', null, '4800.0', '07:12:00', null);
INSERT INTO `bus_stations` VALUES ('104', '浦城路377弄江临天下', null, '4800.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('105', '浦城路605号世茂滨江花园一期', null, '4800.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('106', '世茂滨江花园二期南门（临时）', null, '4800.0', '07:24:00', null);
INSERT INTO `bus_stations` VALUES ('107', '浦建路729号（临时）', null, '4800.0', '07:06:00', null);
INSERT INTO `bus_stations` VALUES ('108', '峨山路111号（临时）', null, '4800.0', '07:11:00', null);
INSERT INTO `bus_stations` VALUES ('109', '花木路916弄', null, '4800.0', '07:16:00', null);
INSERT INTO `bus_stations` VALUES ('110', '银霄路100弄', null, '4800.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('111', '银霄路230号大唐盛世一期', null, '4800.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('112', '银霄路280号大唐国际公寓西门', null, '4800.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('113', '打浦路38弄海华花园', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('114', '大同花园丽园路门', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('115', '斜土路638弄海悦花园', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('116', '斜土路810弄', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('117', '零陵路29弄丝庐花语', null, '4200.0', '07:26:00', null);
INSERT INTO `bus_stations` VALUES ('118', '未来域城永泰路门', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('119', '浦发博园西泰林路门', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('120', '环林东路491弄美林别墅  ', null, '4200.0', '07:23:00', null);
INSERT INTO `bus_stations` VALUES ('121', '环林西路永泰路口（临时）', null, '4200.0', '07:26:00', null);
INSERT INTO `bus_stations` VALUES ('122', '永泰路595弄（临时）', null, '4200.0', '07:27:00', null);
INSERT INTO `bus_stations` VALUES ('123', '上南路华夏西路口（上南路地铁6号线）', null, '4200.0', '07:30:00', null);
INSERT INTO `bus_stations` VALUES ('124', '福泉路550弄（临时）', null, '4200.0', '07:00:00', null);
INSERT INTO `bus_stations` VALUES ('125', '福泉路复地御西郊东门', null, '4200.0', '07:02:00', null);
INSERT INTO `bus_stations` VALUES ('126', '金浜路100号西郊宝成花苑', null, '4200.0', '07:05:00', null);
INSERT INTO `bus_stations` VALUES ('127', '新律花园清溪路门', null, '4200.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('128', '绿谷别墅剑河路门', null, '4200.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('129', '斜土路1622号（临时）', null, '4200.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('130', '斜土路四季园南门', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('131', '宛平南路255弄徐家汇花园', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('132', '辛耕路100弄永新花园 ', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('133', '南丹东路223号莱诗邸花园', null, '4200.0', '07:24:00', null);
INSERT INTO `bus_stations` VALUES ('134', '漕溪北路99弄尊园', null, '4200.0', '07:26:00', null);
INSERT INTO `bus_stations` VALUES ('135', '明中路月台路公交站', null, '4800.0', '06:50:00', null);
INSERT INTO `bus_stations` VALUES ('136', '明中路明华路口', null, '4800.0', '06:52:00', null);
INSERT INTO `bus_stations` VALUES ('137', '明华路366弄比华利花园(小区干道接送)', null, '4800.0', '06:54:00', null);
INSERT INTO `bus_stations` VALUES ('138', '新南路场西路口', null, '4200.0', '06:58:00', null);
INSERT INTO `bus_stations` VALUES ('139', '莘松路1288弄绿洲香岛', null, '4200.0', '07:01:00', null);
INSERT INTO `bus_stations` VALUES ('140', '春九路新南路口', null, '4200.0', '07:04:00', null);
INSERT INTO `bus_stations` VALUES ('141', '莘松路999弄浅水湾  ', null, '4200.0', '07:07:00', null);
INSERT INTO `bus_stations` VALUES ('142', '浅水湾北门口', null, '4200.0', '07:08:00', null);
INSERT INTO `bus_stations` VALUES ('143', '莘松路莘西路公交站', null, '4200.0', '07:12:00', null);
INSERT INTO `bus_stations` VALUES ('144', '番禺路虹桥乐庭东门', null, '4200.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('145', '南丹路377弄中凯城市之光    ', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('146', '宜山路55号', null, '4200.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('147', '虹桥路168弄东方曼哈顿', null, '4200.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('148', '田东路258弄宏润花园 ', null, '4200.0', '07:30:00', null);
INSERT INTO `bus_stations` VALUES ('149', '三江路88弄西雅图', null, '4200.0', '07:33:00', null);
INSERT INTO `bus_stations` VALUES ('150', '宾阳路50弄', null, '4200.0', '07:35:00', null);
INSERT INTO `bus_stations` VALUES ('151', '乳山路505弄', null, '4800.0', '07:00:00', null);
INSERT INTO `bus_stations` VALUES ('152', '国际华城商城路门', null, '4800.0', '07:03:00', null);
INSERT INTO `bus_stations` VALUES ('153', '钦殿路源深路口（临时）', null, '4800.0', '07:05:00', null);
INSERT INTO `bus_stations` VALUES ('154', '香榭丽花园源深路门', null, '4800.0', '07:08:00', null);
INSERT INTO `bus_stations` VALUES ('155', '东绣路99号陆家嘴中央公寓', null, '4800.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('156', '东绣路266弄', null, '4800.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('157', '梅花路50弄（临时）', null, '4800.0', '07:21:00', null);
INSERT INTO `bus_stations` VALUES ('158', '芙蓉江路388弄仁恒河滨花园（水城路门接送）', null, '4200.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('159', '虹古路11弄虹景花苑', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('160', '名都城翠钰路门', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('161', '九亭大街851弄（临时）', null, '4800.0', '06:46:00', null);
INSERT INTO `bus_stations` VALUES ('162', '九亭大街虬泾路口（临时）', null, '4800.0', '06:51:00', null);
INSERT INTO `bus_stations` VALUES ('163', '九亭大街涞亭南路口（临时）', null, '4200.0', '06:54:00', null);
INSERT INTO `bus_stations` VALUES ('164', '顾戴路3459弄', null, '4200.0', '06:59:00', null);
INSERT INTO `bus_stations` VALUES ('165', '顾戴路2000号皇都花园(会所接送）', null, '4200.0', '07:12:00', null);
INSERT INTO `bus_stations` VALUES ('166', '顾戴路1801弄（临时）', null, '4200.0', '07:19:00', null);
INSERT INTO `bus_stations` VALUES ('167', '涞寅路106弄', null, '4800.0', '06:47:00', null);
INSERT INTO `bus_stations` VALUES ('168', '贝尚湾涞寅路门', null, '4800.0', '06:48:00', null);
INSERT INTO `bus_stations` VALUES ('169', '涞坊路1033弄英伦风尚（临时）', null, '4800.0', '06:53:00', null);
INSERT INTO `bus_stations` VALUES ('170', '高泾路258弄', null, '4800.0', '06:57:00', null);
INSERT INTO `bus_stations` VALUES ('171', '龙东大道415弄汤臣', null, '4800.0', '07:00:00', null);
INSERT INTO `bus_stations` VALUES ('172', '罗山路2255号（临时）', null, '4800.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('173', '花木路1983弄四季雅苑', null, '4800.0', '07:16:00', null);
INSERT INTO `bus_stations` VALUES ('174', '金樽花园柳杉路门', null, '4800.0', '07:18:00', null);
INSERT INTO `bus_stations` VALUES ('175', '虹桥路2388弄中华园', null, '4200.0', '07:02:00', null);
INSERT INTO `bus_stations` VALUES ('176', '虹井路888弄', null, '4200.0', '07:04:00', null);
INSERT INTO `bus_stations` VALUES ('177', '金汇路588弄华光城', null, '4200.0', '07:08:00', null);
INSERT INTO `bus_stations` VALUES ('178', '金汇路515号', null, '4200.0', '07:10:00', null);
INSERT INTO `bus_stations` VALUES ('179', '金汇路先锋街口（临时）', null, '4200.0', '07:12:00', null);
INSERT INTO `bus_stations` VALUES ('180', '虹桥镇环镇南路88弄苹果园', null, '4200.0', '07:20:00', null);
INSERT INTO `bus_stations` VALUES ('181', '虹梅路2899弄东苑怡和园（环镇西路门）', null, '4200.0', '07:22:00', null);
INSERT INTO `bus_stations` VALUES ('182', '天安花园迎春路门', null, '4800.0', '07:13:00', null);
INSERT INTO `bus_stations` VALUES ('183', '华丽家族迎春路门', null, '4800.0', '07:15:00', null);
INSERT INTO `bus_stations` VALUES ('184', '水清木华迎春路门', null, '4800.0', '07:17:00', null);
INSERT INTO `bus_stations` VALUES ('185', '虹桥花园迎春路门', null, '4800.0', '07:18:00', null);

-- ----------------------------
-- Table structure for `device`
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
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4;

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
INSERT INTO `picked_students_info` VALUES ('40', '46', '2019-01-22 00:30:42', '25');
INSERT INTO `picked_students_info` VALUES ('41', '46', '2019-01-22 00:31:48', '25');
INSERT INTO `picked_students_info` VALUES ('42', '47', '2019-01-22 00:33:09', '25');
INSERT INTO `picked_students_info` VALUES ('43', '47', '2019-01-22 00:33:43', '25');
INSERT INTO `picked_students_info` VALUES ('44', '48', '2019-01-22 00:39:42', '25');
INSERT INTO `picked_students_info` VALUES ('45', '48', '2019-01-22 00:40:02', '25');
INSERT INTO `picked_students_info` VALUES ('46', '48', '2019-01-22 00:42:20', '25');
INSERT INTO `picked_students_info` VALUES ('47', '49', '2019-01-22 00:45:02', '25');
INSERT INTO `picked_students_info` VALUES ('48', '51', '2019-01-22 01:05:15', '25');
INSERT INTO `picked_students_info` VALUES ('49', '52', '2019-01-22 01:49:48', '1');
INSERT INTO `picked_students_info` VALUES ('50', '52', '2019-01-22 01:51:17', '1');
INSERT INTO `picked_students_info` VALUES ('51', '52', '2019-01-22 01:51:40', '1');
INSERT INTO `picked_students_info` VALUES ('52', '52', '2019-01-22 01:52:04', '1');
INSERT INTO `picked_students_info` VALUES ('53', '52', '2019-01-22 01:52:10', '1');
INSERT INTO `picked_students_info` VALUES ('54', '53', '2019-01-22 01:53:24', '25');
INSERT INTO `picked_students_info` VALUES ('55', '53', '2019-01-22 01:53:32', '26');
INSERT INTO `picked_students_info` VALUES ('56', '54', '2019-01-22 08:11:15', '25');
INSERT INTO `picked_students_info` VALUES ('57', '54', '2019-01-22 08:12:23', '25');
INSERT INTO `picked_students_info` VALUES ('58', '55', '2019-01-22 08:17:28', '25');
INSERT INTO `picked_students_info` VALUES ('59', '55', '2019-01-22 08:21:16', '25');
INSERT INTO `picked_students_info` VALUES ('60', '55', '2019-01-22 08:21:59', '25');
INSERT INTO `picked_students_info` VALUES ('61', '56', '2019-01-22 08:25:54', '25');
INSERT INTO `picked_students_info` VALUES ('62', '57', '2019-01-22 08:30:07', '25');
INSERT INTO `picked_students_info` VALUES ('63', '58', '2019-01-22 11:22:53', '1');
INSERT INTO `picked_students_info` VALUES ('64', '59', '2019-01-22 11:28:52', '1');
INSERT INTO `picked_students_info` VALUES ('65', '59', '2019-01-22 11:29:21', '25');
INSERT INTO `picked_students_info` VALUES ('66', '59', '2019-01-22 11:32:08', '1');
INSERT INTO `picked_students_info` VALUES ('67', '59', '2019-01-22 11:35:20', '25');
INSERT INTO `picked_students_info` VALUES ('68', '60', '2019-01-22 11:40:49', '1');
INSERT INTO `picked_students_info` VALUES ('69', '60', '2019-01-22 11:41:11', '25');
INSERT INTO `picked_students_info` VALUES ('70', '60', '2019-01-22 20:23:11', '16');
INSERT INTO `picked_students_info` VALUES ('71', '60', '2019-01-22 20:41:17', '17');
INSERT INTO `picked_students_info` VALUES ('72', '60', '2019-01-22 20:43:45', '18');
INSERT INTO `picked_students_info` VALUES ('73', '57', '2019-01-22 20:46:01', '18');
INSERT INTO `picked_students_info` VALUES ('74', '57', '2019-01-22 20:49:14', '16');
INSERT INTO `picked_students_info` VALUES ('75', '57', '2019-01-22 20:49:44', '17');
INSERT INTO `picked_students_info` VALUES ('76', '57', '2019-01-22 20:52:13', '19');
INSERT INTO `picked_students_info` VALUES ('77', '61', '2019-01-22 21:01:12', '21');
INSERT INTO `picked_students_info` VALUES ('78', '58', '2019-01-22 21:20:13', '23');

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
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `banji` int(10) unsigned DEFAULT NULL COMMENT '班级,外键',
  `bus_line_morning` int(10) unsigned DEFAULT NULL COMMENT '早班乘坐车的线路ID，外键',
  `bus_line_afternoon` int(10) unsigned DEFAULT NULL COMMENT '午班乘坐车的线路ID，外键',
  `board_station_morning` int(10) unsigned DEFAULT NULL COMMENT '上午班车上车站点',
  `board_station_afternoon` int(10) unsigned DEFAULT NULL COMMENT '下午班车下车站点',
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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4;

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
INSERT INTO `student` VALUES ('23', '4321', '/shzx/xh022.png', '刘亦菲', '2', '34', '42', '23', '23', '');
INSERT INTO `student` VALUES ('24', 'xh0023', '/shzx/xh000023.png', '张成', '3', '38', '43', '13', '13', '');
INSERT INTO `student` VALUES ('25', 'xh222', '/shzx/xh222.png', '高圆圆', '1', '34', '42', '22', '22', '');
INSERT INTO `student` VALUES ('26', 'xh444', '/shzx/xh444.png', '吴彦祖', '1', '34', '42', '23', '23', '');
INSERT INTO `student` VALUES ('28', 'xh-img1', null, '桃名', '1', '34', '42', '1', '1', null);
INSERT INTO `student` VALUES ('29', 'xh-img2', 'C:/images/shzxBusImages/studentImg/xh-img2_桃名2__2019-01-23-20-41-30_0.bmp', '桃名2', '1', '34', '42', '1', '1', null);
INSERT INTO `student` VALUES ('31', 'xh-yaomin111', 'C:/images/shzxBusImages/studentImg/xh-yaomin111_yaomin1_2019-01-24-13-15-46_0.jpg', 'yaomin1', '1', '34', '42', '1', '1', null);
INSERT INTO `student` VALUES ('32', 'xh-yaomin111', 'C:/images/shzxBusImages/studentImg/xh-yaomin111_yaomin1_2019-01-24-14-40-31_0.jpg', 'yaomin1', '1', '34', '42', '1', '1', null);

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
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4;

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
INSERT INTO `transport_record` VALUES ('43', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('44', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('45', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('46', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('47', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('48', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('49', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('50', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('51', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('52', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('53', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('54', '2019-01-22', '34', '21');
INSERT INTO `transport_record` VALUES ('55', '2019-01-22', '34', '21');
INSERT INTO `transport_record` VALUES ('56', '2019-01-22', '34', '21');
INSERT INTO `transport_record` VALUES ('57', '2019-01-22', '34', '21');
INSERT INTO `transport_record` VALUES ('58', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('59', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('60', '2019-01-22', '42', '21');
INSERT INTO `transport_record` VALUES ('61', '2019-01-22', '41', null);

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
INSERT INTO `user` VALUES ('2', 'busMom1', 'xiaozhang', null, '3', '1', null, '13588027825', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('3', 'busMom2', 'lily', null, '3', '1', null, '13033332222', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('4', 'busMom4', 'busmm4', null, '3', '1', null, '13800009999', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('5', 'busMom5', 'busMom5', null, '3', '1', null, '13066663333', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('6', 'busMom6', 'busMom6', null, '3', '1', null, '13022223333', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('11', 'bzr1', 'bzr_tom', null, '4', '1', null, '13600003333', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('12', 'bzr2', 'bzr_tim', null, '4', '1', null, '13600004444', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('13', 'bzr3', 'bzr_jack', null, '4', '1', null, '13033332211', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('14', 'bzr4', 'bzr_peter', null, '4', '1', null, '13622223333', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('15', 'bzr5', 'bzr5', null, '4', '1', null, '12300006666', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('21', 'sj1', 'sj_liusan', null, '5', '1', null, '13699990000', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('22', 'sj2', 'sj2_zhangsan', null, '5', '1', null, '13055550000', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('23', 'sj3', 'sj3_xiaowu', null, '5', '1', null, '13600022222', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('24', 'sj4', 'sj_4', null, '5', '1', null, '13699990000', '1970-01-01 00:00:01', '1');
INSERT INTO `user` VALUES ('25', 'sj5', 'sj5小张', null, '5', '1', null, '13699995566', '1970-01-01 00:00:01', '1');
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
