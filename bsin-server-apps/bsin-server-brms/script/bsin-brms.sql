/*
 Navicat Premium Data Transfer

 Source Server         : yue17
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : rm-2zeq9708apq292r36ko.mysql.rds.aliyuncs.com:3306
 Source Schema         : bsin-intelligent-decision

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 20/06/2025 00:23:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for brms_decision_rule
-- ----------------------------
DROP TABLE IF EXISTS `brms_decision_rule`;
CREATE TABLE `brms_decision_rule` (
  `serial_no` varchar(255) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `kie_package_name` varchar(255) DEFAULT 'rules' COMMENT '规则包名：默认',
  `kie_base_name` varchar(255) DEFAULT NULL COMMENT '规则编号',
  `rule_name` varchar(255) DEFAULT NULL COMMENT '规则名称',
  `content` text COMMENT '规则内容',
  `content_json` text COMMENT '规则内容JSON',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL COMMENT '1 规则集 2 规则树 3 规则表',
  `version` varchar(255) DEFAULT NULL COMMENT '版本号',
  `description` varchar(255) DEFAULT NULL COMMENT '规则描述',
  PRIMARY KEY (`serial_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规则表';

-- ----------------------------
-- Records of brms_decision_rule
-- ----------------------------
BEGIN;
INSERT INTO `brms_decision_rule` VALUES ('1814518950009237505', '6345824413764157440', 'rules', 'chat', 'chat_test', 'package rules\n\nimport java.util.HashMap;\nglobal java.util.Map globalMap\n\nrule \"decision_rule_1_11\"\n    lock-on-active true\nwhen\n    $map : HashMap()\n    eval(((1 + 5) * 3) > 10 && $map.get(\'userAge\').equals(\"18\"))\nthen\n    globalMap.put(\"thenFlag\", \"1\");\n    globalMap.put(\"userScore\", \"100\");\n    update($map);\n    System.out.println(\"触发规则：decision_rule_1_11\");\nend\n', '{\"rules\":[{\"name\":\"decision_rule_1_11\",\"attributes\":{\"lock-on-active\":true},\"before\":{},\"conditions\":[{\"logic\":\"\",\"expression\":{\"left\":{\"type\":\"bracket\",\"expression\":{\"left\":{\"type\":\"bracket\",\"expression\":{\"left\":{\"type\":\"value\",\"value\":\"1\"},\"operator\":\"+\",\"right\":{\"type\":\"value\",\"value\":\"5\"}}},\"operator\":\"*\",\"right\":{\"type\":\"value\",\"value\":\"3\"}}},\"operator\":\">\",\"right\":{\"type\":\"value\",\"value\":\"10\"}}},{\"logic\":\"&&\",\"expression\":{\"left\":{\"type\":\"value\",\"value\":\"$map.get(\'userAge\')\"},\"operator\":\">\",\"right\":{\"type\":\"value\",\"value\":18}}}],\"actions\":[{\"type\":\"globalMap.put\",\"key\":\"userScore\",\"value\":\"100\"},{\"type\":\"update\",\"map\":\"$map\"},{\"type\":\"print\",\"message\":\"触发规则：decision_rule_1_11\"}]}]}', NULL, NULL, NULL, NULL, '测试规则');
COMMIT;

-- ----------------------------
-- Table structure for brms_event
-- ----------------------------
DROP TABLE IF EXISTS `brms_event`;
CREATE TABLE `brms_event` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `tenant_id` varchar(64) DEFAULT NULL COMMENT '租户id',
  `event_name` varchar(64) DEFAULT NULL COMMENT '事件名称',
  `event_code` varchar(32) DEFAULT NULL COMMENT '事件编码',
  `event_level` varchar(255) DEFAULT NULL COMMENT '1、平台级动作  2、商户级动作',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `del_flag` int(11) DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='等级事件规则表';

-- ----------------------------
-- Records of brms_event
-- ----------------------------
BEGIN;
INSERT INTO `brms_event` VALUES ('1', '6345824413764157440', '聊天', 'chat', '1', NULL, 0, '2025-01-25 10:14:49', NULL, '2025-01-25 10:07:09', NULL);
COMMIT;

-- ----------------------------
-- Table structure for brms_event_model
-- ----------------------------
DROP TABLE IF EXISTS `brms_event_model`;
CREATE TABLE `brms_event_model` (
  `serial_no` varchar(32) NOT NULL,
  `tenant_id` varchar(32) NOT NULL,
  `event_code` varchar(255) DEFAULT NULL COMMENT '事件编码',
  `model_no` varchar(32) DEFAULT NULL COMMENT '事件模型',
  `model_type` varchar(255) DEFAULT NULL COMMENT '1、流程模型 2、表单模型 3、规则模型 4、推理模型',
  PRIMARY KEY (`serial_no`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of brms_event_model
-- ----------------------------
BEGIN;
INSERT INTO `brms_event_model` VALUES ('1', '6345824413764157440', 'chat', '1814518950009237505', '3');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
