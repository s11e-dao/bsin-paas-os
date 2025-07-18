/*
 Navicat Premium Data Transfer

 Source Server         : aliMysql
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : rm-2zeq9708apq292r36ko.mysql.rds.aliyuncs.com:3306
 Source Schema         : yue17-crm

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 17/07/2025 22:59:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for crm_account
-- ----------------------------
DROP TABLE IF EXISTS `crm_account`;
CREATE TABLE `crm_account` (
  `serial_no` varchar(32) NOT NULL COMMENT '账户编号',
  `name` varchar(255) DEFAULT NULL COMMENT '账户名称',
  `biz_role_type_no` varchar(32) NOT NULL COMMENT '业务角色类型编号',
  `biz_role_type` varchar(255) DEFAULT NULL COMMENT '角色类型，1.运营平台 2.租户平台 4.代理商 5.租户客户 6.门店 99.无',
  `category` varchar(255) DEFAULT NULL COMMENT '账户类别： 1:余额账户 2:累计收入 3:累计支出 4:在途账户 ',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  `cumulative_amount` decimal(10,2) DEFAULT '0.00' COMMENT '累计金额',
  `balance` decimal(28,2) DEFAULT '0.00' COMMENT '账户余额',
  `freeze_amount` decimal(10,2) DEFAULT '0.00' COMMENT '冻结金额',
  `type` varchar(11) NOT NULL DEFAULT '0' COMMENT '账户类型 0、个人账户 1、企业账户 2 租户(dao)账户',
  `ccy` varchar(32) NOT NULL COMMENT '币种 用币种英文代替',
  `status` varchar(11) DEFAULT '0' COMMENT '账户状态 0、正常 1、冻结',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` int(11) DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  `check_code` varchar(255) DEFAULT NULL COMMENT '避免余额手动修改',
  `decimals` decimal(2,0) unsigned NOT NULL DEFAULT '2' COMMENT '小数点位数',
  PRIMARY KEY (`serial_no`) USING BTREE,
  UNIQUE KEY `biz_role_type_no` (`biz_role_type_no`,`category`,`ccy`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_account
-- ----------------------------
BEGIN;
INSERT INTO `crm_account` VALUES ('11', NULL, '1738934400126685184', NULL, '1', '1737841274272223232', 0.00, 10958.10, 0.00, '0', 'cny', '0', '2024-02-11 10:44:48', '2024-02-11 17:34:21', 0, '9bbd1be9b99a8962fb0f05ee1747cb19', 2);
INSERT INTO `crm_account` VALUES ('1737868783969873921', NULL, '1737841352147968001', NULL, '1', '1737841274272223232', 0.00, 0.00, 0.00, '0', 'cny', '0', '2023-12-22 00:13:03', '2023-12-22 00:13:03', 0, NULL, 2);
INSERT INTO `crm_account` VALUES ('1739222113207865346', NULL, '1737853500064509954', NULL, '1', '1737841274272223232', 0.00, 0.00, 0.00, '0', 'cny', '0', '2023-12-25 17:50:39', '2023-12-25 17:50:39', 0, NULL, 2);
INSERT INTO `crm_account` VALUES ('1739269317662494721', NULL, '1738934400126685184', NULL, '1', '1737841274272223232', 0.00, 0.00, 0.00, '0', 'HJM-BC', '0', '2023-12-25 20:58:14', '2023-12-25 20:58:14', 0, NULL, 2);
INSERT INTO `crm_account` VALUES ('1844712082344468482', NULL, '1738521059952562176', NULL, '1', '1801458632269893632', 0.00, 0.00, 0.00, '0', 'HJM-BC', '0', '2024-10-11 20:10:08', '2024-10-11 20:10:08', 0, NULL, 2);
INSERT INTO `crm_account` VALUES ('1844716168431230977', NULL, '1827579279643381760', NULL, '1', '1801458632269893632', 0.00, 0.00, 0.00, '0', 'HJM-BC', '0', '2024-10-11 20:26:22', '2024-10-11 20:26:22', 0, NULL, 2);
INSERT INTO `crm_account` VALUES ('1859106319169191937', '待结算账户', '6345824413764157440', '2', '5', '6345824413764157440', 0.00, 0.17, 0.00, '0', 'cny', '0', '2024-11-20 13:27:41', '2024-11-20 13:27:41', 0, '87bcdee408f67dd05da31df7b1cc4ec6', 2);
INSERT INTO `crm_account` VALUES ('1859106333022978050', '待结算账户', '1846190237240397824', '2', '5', '6345824413764157440', 0.00, 0.17, 0.00, '0', 'cny', '0', '2024-11-20 13:27:45', '2024-11-20 13:27:45', 0, 'e0cb5233d5332a89dedd212264993656', 2);
INSERT INTO `crm_account` VALUES ('1859106347556241409', '待结算账户', '1737853500064509954', '4', '5', '1846190237240397824', 0.00, 1.06, 0.00, '0', 'cny', '0', '2024-11-20 13:27:48', '2024-11-20 13:27:48', 0, '0c8c32ffb1fbc296ffdaa0ccd529871a', 2);
INSERT INTO `crm_account` VALUES ('1859106352778149889', '待结算账户', '1847267145117995008', '4', '5', '1846190237240397824', 0.00, 1.25, 0.00, '0', 'cny', '0', '2024-11-20 13:27:49', '2024-11-20 13:27:49', 0, '7a846217a7272a86a6c7d9aac255f04e', 2);
INSERT INTO `crm_account` VALUES ('1859106357744205825', '待结算账户', '1737853502828482561', '5', '5', '6345824413764157440', 0.00, 0.66, 0.00, '0', 'cny', '0', '2024-11-20 13:27:50', '2024-11-20 13:27:50', 0, '3b51c11166b3b4924598a932fd8b8067', 2);
COMMIT;

-- ----------------------------
-- Table structure for crm_account_freeze_journal
-- ----------------------------
DROP TABLE IF EXISTS `crm_account_freeze_journal`;
CREATE TABLE `crm_account_freeze_journal` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `account_no` varchar(32) DEFAULT NULL COMMENT '客户或者商户的账户编号',
  `type` varchar(32) DEFAULT NULL COMMENT '冻结事件类型：（1.社区提案、2.订单 3.数字资产 4.任务质押)',
  `type_no` varchar(32) DEFAULT NULL COMMENT '冻结的事件类型编号（提案、订单等编号）',
  `freeze_amount` int(32) DEFAULT NULL COMMENT '冻结金额',
  `status` varchar(100) DEFAULT NULL COMMENT '冻结状态：（1：已冻结  2：部分解冻  3：已解冻）',
  `biz_role_type_no` varchar(32) DEFAULT NULL COMMENT '业务角色类型',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户号',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数字资产铸造流水';

-- ----------------------------
-- Records of crm_account_freeze_journal
-- ----------------------------
BEGIN;
INSERT INTO `crm_account_freeze_journal` VALUES ('1739269318870437888', '1739269317662494721', '4', '1739268377794461697', 0, '1', NULL, '1737853502828482561', '1737841274272223232', '2023-12-25 20:58:14', NULL);
INSERT INTO `crm_account_freeze_journal` VALUES ('1747550656127963136', '1739269317662494721', '4', '1739887363263430657', 0, '1', NULL, '1737853502828482561', '1737841274272223232', '2024-01-17 17:25:19', NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_account_journal
-- ----------------------------
DROP TABLE IF EXISTS `crm_account_journal`;
CREATE TABLE `crm_account_journal` (
  `serial_no` varchar(32) NOT NULL COMMENT '账户流水编号',
  `biz_role_type_no` varchar(32) NOT NULL COMMENT '客户编号',
  `account_no` varchar(32) NOT NULL COMMENT '账户编号',
  `account_type` int(11) NOT NULL COMMENT '账户类型 0、个人账户 1、企业账户',
  `order_type` int(11) DEFAULT NULL COMMENT '业务类型 0、支付 1、退款 2、出售 3、充值 4、转账 5、提现',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `in_out_flag` int(11) NOT NULL COMMENT '出账入账标志 0、出账 1、入账',
  `amount` decimal(28,2) NOT NULL COMMENT '金额',
  `ccy` varchar(32) DEFAULT NULL COMMENT '币种',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户号',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_account_journal
-- ----------------------------
BEGIN;
INSERT INTO `crm_account_journal` VALUES ('1756612684549328896', '1738934400126685184', '11', 0, NULL, NULL, 1, 99.00, 'cny', '2024-02-11 17:34:34', NULL, '1737841274272223232', NULL);
INSERT INTO `crm_account_journal` VALUES ('1756615866449006592', '1738934400126685184', '11', 0, NULL, NULL, 1, 99.00, 'cny', '2024-02-11 17:47:13', NULL, '1737841274272223232', NULL);
INSERT INTO `crm_account_journal` VALUES ('1756620398151208960', '1738934400126685184', '11', 0, NULL, NULL, 1, 9.90, 'cny', '2024-02-11 18:05:14', NULL, '1737841274272223232', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857665729332121600', 'string', '1857665729021829121', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-16 14:03:18', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857667132507164672', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-16 14:08:52', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857667167252779008', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 196.42, 'cny', '2024-11-16 14:09:01', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857669216610684928', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-16 14:17:09', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857669217655066624', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 196.42, 'cny', '2024-11-16 14:17:10', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857669534358573056', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-16 14:18:25', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857669535390371840', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 196.42, 'cny', '2024-11-16 14:18:25', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857702999753887744', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-16 16:31:24', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857703000756326400', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 196.42, 'cny', '2024-11-16 16:31:24', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857705936286781440', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-16 16:43:04', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1857705937343746048', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 196.42, 'cny', '2024-11-16 16:43:04', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1858801564748222464', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 97.15, 'cny', '2024-11-19 17:16:42', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1858801565838741504', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 196.42, 'cny', '2024-11-19 17:16:42', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1858804477742682112', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 0.97, 'cny', '2024-11-19 17:28:17', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1858804478820618240', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 1.96, 'cny', '2024-11-19 17:28:17', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1858804543211573248', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 0.97, 'cny', '2024-11-19 17:28:32', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1858804544272732160', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 1.96, 'cny', '2024-11-19 17:28:32', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859047594706210816', '6345824413764157440', '1859047594400026626', 0, NULL, NULL, 2, 4.36, 'cny', '2024-11-20 09:34:20', NULL, NULL, NULL);
INSERT INTO `crm_account_journal` VALUES ('1859047652176564224', '1846190237240397824', '1859047651832631297', 0, NULL, NULL, 2, 3.26, 'cny', '2024-11-20 09:34:34', NULL, NULL, NULL);
INSERT INTO `crm_account_journal` VALUES ('1859047708715782144', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 0.97, 'cny', '2024-11-20 09:34:47', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859047716202614784', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 1.14, 'cny', '2024-11-20 09:34:49', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859047726436716544', '1737853502828482561', '1859047726155698178', 0, NULL, NULL, 2, 2.86, 'cny', '2024-11-20 09:34:52', NULL, NULL, NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051342220365824', '6345824413764157440', '1859051341956132865', 0, NULL, NULL, 2, 4.36, 'cny', '2024-11-20 09:49:14', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051343407353856', '1846190237240397824', '1859051343151509506', 0, NULL, NULL, 2, 3.26, 'cny', '2024-11-20 09:49:14', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051344602730496', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 0.97, 'cny', '2024-11-20 09:49:14', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051345676472320', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 1.14, 'cny', '2024-11-20 09:49:15', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051346871848960', '1737853502828482561', '1859051346607616002', 0, NULL, NULL, 2, 2.86, 'cny', '2024-11-20 09:49:15', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051415385804800', '6345824413764157440', '1859051341956132865', 0, NULL, NULL, 2, 4.36, 'cny', '2024-11-20 09:49:31', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051416333717504', '1846190237240397824', '1859051343151509506', 0, NULL, NULL, 2, 3.26, 'cny', '2024-11-20 09:49:31', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051417256464384', '1737853500064509954', '1857667132217843713', 0, NULL, NULL, 2, 0.97, 'cny', '2024-11-20 09:49:32', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051418305040384', '1847267145117995008', '1857667166971846658', 0, NULL, NULL, 2, 1.14, 'cny', '2024-11-20 09:49:32', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859051419211010048', '1737853502828482561', '1859051346607616002', 0, NULL, NULL, 2, 2.86, 'cny', '2024-11-20 09:49:32', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859106319462699008', '6345824413764157440', '1859106319169191937', 0, NULL, NULL, 2, 0.17, 'cny', '2024-11-20 13:27:41', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859106333291319296', '1846190237240397824', '1859106333022978050', 0, NULL, NULL, 2, 0.17, 'cny', '2024-11-20 13:27:45', NULL, '6345824413764157440', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859106347824582656', '1737853500064509954', '1859106347556241409', 0, NULL, NULL, 2, 1.06, 'cny', '2024-11-20 13:27:48', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859106353054879744', '1847267145117995008', '1859106352778149889', 0, NULL, NULL, 2, 1.25, 'cny', '2024-11-20 13:27:49', NULL, '1846190237240397824', NULL);
INSERT INTO `crm_account_journal` VALUES ('1859106358008352768', '1737853502828482561', '1859106357744205825', 0, NULL, NULL, 2, 0.66, 'cny', '2024-11-20 13:27:50', NULL, '6345824413764157440', NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_biz_role_app
-- ----------------------------
DROP TABLE IF EXISTS `crm_biz_role_app`;
CREATE TABLE `crm_biz_role_app` (
  `serial_no` varchar(255) NOT NULL COMMENT '序号',
  `tenant_id` varchar(255) NOT NULL COMMENT '租户号',
  `biz_role_type` varchar(255) DEFAULT NULL COMMENT '业务角色类型：角色类型，1.运营平台 2.租户平台 4.代理商 5.租户客户 6.门店 99.无',
  `biz_role_type_no` varchar(32) DEFAULT NULL COMMENT '业务角色类型编号',
  `app_name` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `app_channel` varchar(255) DEFAULT NULL COMMENT '1: 应用 2：接口 3:mp(公众号服务订阅号)、4.miniapp(小程序)、 5.cp(企业号|企业微信)、6.pay(微信支付)、7.open(微信开放平台) 8wechat(个人微信) 9.menu(菜单模版)',
  `app_id` varchar(255) NOT NULL COMMENT '应用ID：自动生成 微信公众号的appID：公众号通过此ID检索公众号参数 设置企业微信的corpId：企业微信通过此ID检索公众号参数',
  `app_secret` varchar(255) NOT NULL COMMENT '应用密钥：自动生成',
  `app_description` varchar(255) DEFAULT NULL COMMENT '应用描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `del_flag` int(255) DEFAULT '0' COMMENT '删除标识',
  `status` varchar(255) DEFAULT '0' COMMENT '应用状态: 0 待审核 1 审核通过',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '回调地址',
  `aes_key` varchar(255) DEFAULT NULL COMMENT '企业微信/微信公众号的EncodingAESKey',
  `token` varchar(255) DEFAULT NULL COMMENT '微信公众号/企业微信/小程序的token',
  `corp_id` varchar(255) DEFAULT NULL COMMENT '企业微信应用ID',
  `agent_id` varchar(255) DEFAULT NULL COMMENT 'agent ID',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统接入的产品应用渠道信息表：平台和商户接入的应用';

-- ----------------------------
-- Records of crm_biz_role_app
-- ----------------------------
BEGIN;
INSERT INTO `crm_biz_role_app` VALUES ('1846190237240123222', '6345824413764157440', '4', '1846556049375875073', '太美你鸡小程序', '4', 'wxb93d0918b7826a69', '77699e63efc85159934651d5c33e3b9b', NULL, '2024-10-15 00:25:06', '2024-10-15 00:25:06', 0, '0', 'https://域名/wxpay/wechat', NULL, NULL, NULL, NULL);
INSERT INTO `crm_biz_role_app` VALUES ('1846190237240123333', '6345824413764157440', '4', '1846556049375875073', 'leonard测试小程序', '4', 'wx3b5b626a1a90ade1', '979b4c0d1ed7e680f94d3081a5bb69dd', NULL, '2024-10-28 20:39:42', '2024-10-28 20:39:42', 0, '0', 'https://域名/wxpay/wechat', NULL, NULL, NULL, NULL);
INSERT INTO `crm_biz_role_app` VALUES ('1846190237240123456', '6345824413764157440', '4', '1846556049375875073', '分销商城', '4', 'wxfa99fb352d815a26', 'ec29c82e7b8e8689e8e0f59ef84de1f4', '正式线上小程序', '2024-11-07 00:20:12', '2024-11-07 00:20:12', 0, '0', 'https://域名/wxpay/wechat', NULL, NULL, NULL, NULL);
INSERT INTO `crm_biz_role_app` VALUES ('1868937468317929472', '6345824413764157440', '2', '1846190237240397824', '小家好物', '4', 'wx5d1ac3ef86ce21d3', '22', '小家好物', '2024-12-17 16:33:10', '2024-12-17 16:33:10', 0, '0', 'https://gateway.iittii.com', '22', '22', NULL, NULL);
INSERT INTO `crm_biz_role_app` VALUES ('1893315829769048064', '6345824413764157440', '99', '1801458632269893632', '粤17', '4', 'wx029e37aae5feec1b', '5a6b75a5f2a3be85b77dc4cbd5bbae96', '1', '2025-02-22 23:04:04', '2025-02-22 23:04:04', 0, '0', '1', '1', '1', NULL, NULL);
INSERT INTO `crm_biz_role_app` VALUES ('1894059574646935552', '1801458632269893632', '4', '1801458632269893632', 'yue17', '4', 'wx029e37aae5feec1b', '5a6b75a5f2a3be85b77dc4cbd5bbae96', 'yue17小程序', '2025-02-25 00:19:27', '2025-02-25 00:19:27', 0, '0', 'https://gateway.iittii.com', '1', '1', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_biz_role_app_api_fee
-- ----------------------------
DROP TABLE IF EXISTS `crm_biz_role_app_api_fee`;
CREATE TABLE `crm_biz_role_app_api_fee` (
  `serial_no` varchar(255) NOT NULL COMMENT '序号',
  `tenant_id` varchar(255) DEFAULT NULL COMMENT '租户号',
  `customer_no` varchar(255) DEFAULT NULL COMMENT '客户号',
  `fee` varchar(255) DEFAULT NULL COMMENT '单次调用费用：单位分',
  `free_times` int(11) DEFAULT NULL COMMENT '可免费调用次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(255) NOT NULL DEFAULT '0' COMMENT '配置生效状态：0待审核 1已生效',
  `product_id` varchar(255) DEFAULT NULL COMMENT '应用ID',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户号',
  `api_code` varchar(255) DEFAULT NULL COMMENT 'api编号',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户消费参数配置';

-- ----------------------------
-- Records of crm_biz_role_app_api_fee
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_biz_role_app_api_journal
-- ----------------------------
DROP TABLE IF EXISTS `crm_biz_role_app_api_journal`;
CREATE TABLE `crm_biz_role_app_api_journal` (
  `serial_no` varchar(255) NOT NULL COMMENT '序号',
  `tenant_id` varchar(255) NOT NULL COMMENT '租户ID',
  `merchant_no` varchar(11) DEFAULT NULL COMMENT '商户号',
  `product_id` varchar(255) NOT NULL COMMENT '调用应用ID',
  `api_name` varchar(255) DEFAULT NULL,
  `fee` varchar(255) DEFAULT NULL COMMENT '消费费用: 单位分',
  `customer_no` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_biz_role_app_api_journal
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_biz_role_bill
-- ----------------------------
DROP TABLE IF EXISTS `crm_biz_role_bill`;
CREATE TABLE `crm_biz_role_bill` (
  `serial_no` int(11) NOT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `bill_type` varchar(255) DEFAULT NULL COMMENT '交易场景：线上支付、线下扫码、转账、红包、AA收款等',
  `bill_type_no` varchar(255) DEFAULT NULL COMMENT '订单信息',
  `pay_channel` varchar(255) DEFAULT NULL COMMENT '支付渠道：wechat_pay,alipay,bank_card,balance等',
  `discount_info` json DEFAULT NULL COMMENT '优惠券信息',
  `create_time` datetime DEFAULT NULL COMMENT '优惠信息',
  `payer_type` varchar(255) DEFAULT NULL COMMENT '付款方类型：业务角色类型',
  `payer_no` int(11) DEFAULT NULL COMMENT '付款方ID',
  `payee_type` varchar(255) DEFAULT NULL COMMENT '收款方类型：业务角色类型',
  `payee_no` int(11) DEFAULT NULL COMMENT '收款方ID',
  `amount` varchar(255) DEFAULT NULL COMMENT '交易金额',
  PRIMARY KEY (`serial_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='不同业务角色的帐单表';

-- ----------------------------
-- Records of crm_biz_role_bill
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_biz_role_subscribe_journal
-- ----------------------------
DROP TABLE IF EXISTS `crm_biz_role_subscribe_journal`;
CREATE TABLE `crm_biz_role_subscribe_journal` (
  `serial_no` varchar(32) NOT NULL,
  `merchant_no` varchar(32) DEFAULT NULL,
  `pay_status` varchar(255) DEFAULT NULL COMMENT '支付状态',
  `amout` varchar(255) DEFAULT NULL COMMENT '支付金额',
  `product_id` varchar(32) DEFAULT NULL COMMENT '租户的产品ID',
  `app_id` varchar(32) DEFAULT NULL COMMENT '订阅的应用ID',
  `app_function_id` varchar(32) DEFAULT NULL COMMENT '订阅的应用功能ID',
  `create_time` datetime DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_biz_role_subscribe_journal
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_condition
-- ----------------------------
DROP TABLE IF EXISTS `crm_condition`;
CREATE TABLE `crm_condition` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户',
  `value` int(11) DEFAULT NULL COMMENT '条件值',
  `name` varchar(255) DEFAULT NULL COMMENT '条件名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户编码',
  `type_no` varchar(32) DEFAULT NULL COMMENT '条件类型资产编号：',
  `type` varchar(11) DEFAULT NULL COMMENT '条件类型：1:数字资产 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡   6：账户-联合曲线(BC)  ) 7：满减 8：权限',
  `type_protocol` varchar(11) DEFAULT NULL COMMENT '条件资产类型协议： ERC20 ERC721 ERC1155 ERC3525 ',
  `type_token_id` varchar(32) DEFAULT NULL COMMENT '条件资产tokenId： ERC1155专属',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级达成条件';

-- ----------------------------
-- Records of crm_condition
-- ----------------------------
BEGIN;
INSERT INTO `crm_condition` VALUES ('1739269119217389570', NULL, '2023-12-25 20:57:26', NULL, '2023-12-25 20:57:26', 0, '1737841274272223232', 10, '持有花尖墨劳动价值余额>10', '花尖墨劳动价值余额>10', '1737853502828482561', '1739222480528232449', '6', NULL, NULL);
INSERT INTO `crm_condition` VALUES ('1745061204193333249', NULL, '2024-01-10 20:33:07', NULL, '2024-01-10 20:33:07', 0, '1737841274272223232', 1, '花间默徽章1个', '11', '1737853502828482561', '1739251040504647680', '1', NULL, NULL);
INSERT INTO `crm_condition` VALUES ('1848749732309266433', NULL, '2024-10-22 23:34:19', NULL, '2024-10-22 23:34:19', 0, '1846190237240397824', NULL, '条件名称', '备注', '1846556049375875073', '条件类型编号', '9', NULL, NULL);
INSERT INTO `crm_condition` VALUES ('1848750114682990593', NULL, '2024-10-22 23:35:50', NULL, '2024-10-22 23:35:50', 0, '1846190237240397824', 1, '花尖墨水果PFP', '1', '1846556049375875073', '0', '1', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_condition_relation
-- ----------------------------
DROP TABLE IF EXISTS `crm_condition_relation`;
CREATE TABLE `crm_condition_relation` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `category` varchar(32) DEFAULT NULL COMMENT '条件分类：1、会员等级 2 数字资产 3 任务 4 活动',
  `category_no` varchar(32) DEFAULT NULL COMMENT '条件分类编号',
  `type` varchar(255) DEFAULT NULL COMMENT '条件类型：1:数字资产 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡   6：账户-联合曲线(BC)  ) 7：满减 8：权限',
  `type_no` varchar(32) DEFAULT NULL COMMENT '条件类型编号（事件或收益）',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销任务参与条件配置表';

-- ----------------------------
-- Records of crm_condition_relation
-- ----------------------------
BEGIN;
INSERT INTO `crm_condition_relation` VALUES ('1745061271239282689', '3', '1740357981305860097', '1', '1745061204193333249');
COMMIT;

-- ----------------------------
-- Table structure for crm_customer_base
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer_base`;
CREATE TABLE `crm_customer_base` (
  `customer_no` varchar(32) NOT NULL COMMENT '客户号',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户id',
  `auth_method` varchar(50) DEFAULT NULL COMMENT '0、手机号 1、邮箱 2、QQ 3、微信4、用户名 5、微博  第三方登录获取',
  `credential` varchar(50) DEFAULT NULL COMMENT '凭据，第三方标识 微信平台中的openId：登录用户与微信平台唯一标识',
  `username` varchar(255) NOT NULL COMMENT '登录用户名：注册设置 ',
  `password` varchar(255) DEFAULT NULL COMMENT '登录密码：注册设置',
  `real_name` varchar(255) DEFAULT NULL COMMENT '真实姓名：实名认证设置',
  `id_number` varchar(32) DEFAULT NULL COMMENT '身份证号：实名认证设置',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称：小程序设置',
  `tx_password` varchar(32) DEFAULT NULL COMMENT '支付密码：实名认证设置',
  `tx_password_status` varchar(1) DEFAULT '1' COMMENT '支付密码状态 0、未设置 1、已经设置 2、锁定',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号：实名认证设置|小程序设置',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱：小程序设置',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像：小程序设置',
  `type` varchar(11) DEFAULT NULL COMMENT '客户类型 0、个人客户 1、企业客户',
  `vip_flag` bit(1) DEFAULT b'0' COMMENT '是否是会员标识： 0、否 1、是',
  `certification_status` bit(1) DEFAULT b'0' COMMENT '实名认证标识 0：未认证 1：已认证',
  `google_token` varchar(255) DEFAULT NULL COMMENT '谷歌验证器token',
  `wallet_address` varchar(255) DEFAULT NULL COMMENT '链上钱包地址:实名认证后生成',
  `wallet_private_key` mediumtext COMMENT '链上钱包密码:实名认证设置',
  `evm_wallet_address` varchar(255) DEFAULT NULL COMMENT 'evm预留链上钱包地址:实名认证后生成',
  `evm_wallet_private_key` mediumtext COMMENT 'evm预留链上钱包密码:实名认证设置',
  `description` longtext COMMENT '介绍',
  `birthday` varchar(32) DEFAULT NULL COMMENT '生日',
  `sex` varchar(11) DEFAULT NULL COMMENT '性别：1：男 2：女',
  `info` varchar(255) DEFAULT NULL COMMENT '简介',
  `invite_code` varchar(32) DEFAULT NULL COMMENT '邀请码',
  `profile_address` varchar(255) DEFAULT NULL COMMENT 'Profile合约地址:创建数字分身时设置',
  `evm_profile_address` varchar(255) DEFAULT NULL COMMENT 'evm预留链上Profile合约地址:创建数字分身时设置',
  `copilot_no` varchar(32) DEFAULT NULL COMMENT '创建profile时创建的数字分身(用户)|品牌馆(商户)ID',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  `session_key` varchar(255) DEFAULT NULL COMMENT '微信平台：登录请求会话key',
  `grade_no` varchar(255) DEFAULT NULL COMMENT '等级序列号（关联crm_grade）',
  PRIMARY KEY (`customer_no`) USING BTREE,
  UNIQUE KEY `username` (`tenant_id`,`username`,`credential`,`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户信息';

-- ----------------------------
-- Records of crm_customer_base
-- ----------------------------
BEGIN;
INSERT INTO `crm_customer_base` VALUES ('1737841352147968001', '1737841274272223232', NULL, NULL, 'jiujiu', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, 'jiujiu根节点', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-21 22:24:01', NULL, '2023-12-21 22:24:01', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1737853500064509954', '1737841274272223232', NULL, NULL, '花尖墨', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/ai/1737841274272223232/1737853502828482561/1737853500064509954/65afcc0f56e2f0d434a9340a.png', '2', b'0', b'0', NULL, 'cfxtest:aarpncc5f4s3fxrr4vf4s4ye253t18tc825p0dg9u0', 'c8c60038e2b084495e5e72518279631f479af25410e3636c898520aab70e8412c487b0ccbd5229cb06cc726ac2ad28e0ad59585abdb923312346b29cc35b9015f6ee07011c776ddfb8fe23d7a7989cf1', '0x886Be6d58c8d73CB1a76bBcc425Ca7253261e36b', 'b19025a4b57631b31541caba21747016399900c81079802cc82124232fec9d1443de1f34a303b0920d80383cc022eec915044aa36116cbf195b9668d85a4a592f6ee07011c776ddfb8fe23d7a7989cf1', 'web3水果品牌', NULL, NULL, 'ddg ', NULL, 'cfxtest:acduh8f9u2fwsmnau4zzvps0nzh56bnxz6nj4xx6kz', NULL, '1747666622665068544', NULL, '2023-12-21 23:12:17', NULL, '2024-01-23 22:24:21', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738153137618866177', '1738153132644503552', NULL, NULL, '云南大理', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '云南大理', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 19:02:57', NULL, '2023-12-22 19:02:57', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738161557751324673', '1738153132644503552', NULL, NULL, '大理商户bl', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '水果', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 19:36:24', NULL, '2023-12-22 19:36:24', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738161853810466818', '1738153132644503552', NULL, NULL, '大理商户bolei', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 19:37:35', NULL, '2025-03-11 11:36:26', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738174098984525825', '1738174093884264448', NULL, NULL, '云南丽江', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '云南丽江', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 20:26:14', NULL, '2023-12-22 20:26:14', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738177261376495618', '1738177256720830464', NULL, NULL, '云南保山', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '云南保山', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 20:38:48', NULL, '2023-12-22 20:38:48', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738178976007970818', '1738178971067092992', NULL, NULL, '云南蒙自', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '云南蒙自', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 20:45:37', NULL, '2023-12-22 20:45:37', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738182184784748545', '1738182179072118784', NULL, NULL, '云南玉溪', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '云南玉溪', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 20:58:22', NULL, '2023-12-22 20:58:22', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738187085979967489', '1738178971067092992', NULL, NULL, '蒙自商户', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-22 21:17:50', NULL, '2025-03-11 11:36:29', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738199949004304386', '1737841274272223232', NULL, NULL, '13232521022', '123456', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'DUTV0X', NULL, NULL, NULL, NULL, '2023-12-22 22:08:58', NULL, '2023-12-22 22:08:58', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738445698803769346', '1539479385919328256', NULL, NULL, 'yatta', 'b35dc9c7749418655b1d250f2dc680d4', NULL, NULL, NULL, NULL, '1', '15077851132', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-23 14:25:28', NULL, '2023-12-23 14:25:28', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738521059952562176', '1801458632269893632', NULL, NULL, '13632521024', 'e10adc3949ba59abbe56e057f20f883e', '博羸', NULL, '博羸', '111111', '1', '13632521021', NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/bigan/jiujiu/1737841274272223232/1738521059952562176/65a3585024708307100c07ae.png', '0', b'0', b'1', NULL, 'cfxtest:aakk63146mekvzxkce6002bh7bgvh1aan22u5bhv4b', '094adda88d783bb426b2f570697bd1c7d60a98a2b9ae5c9da5a6b9cd47a835197c88bc4ea8e54041bb5f16e85b9f603e1cca4a903514478562f774510238cbeaf6ee07011c776ddfb8fe23d7a7989cf1', NULL, NULL, NULL, NULL, '2', NULL, 'LLUJXI', NULL, NULL, NULL, NULL, '2023-12-23 19:24:57', NULL, '2025-03-11 11:36:30', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1738934400126685184', '1737841274272223232', NULL, NULL, '16675588381', 'e10adc3949ba59abbe56e057f20f883e', '颤三', NULL, 'leonard', '737062', '1', '16676688761', '1287279970@qq.com', 'https://s11edao.oss-cn-beijing.aliyuncs.com/jiujiu/1737841274272223232/1738934400126685184/65a7e3e640a89ace34b9ff58.png', '0', b'0', b'0', NULL, 'cfxtest:aatjvpugh82ugr9msnduzthn668gjm92pafks6fvk6', 'bcf105b3eb1f26475235f5ceee2adf8b15125a05465c087332915f1d36dea977b8cdc6298f618ab5eaa51e21cfbf0e32aadb5bfedb6d8a970ba197ec1ebe2817f6ee07011c776ddfb8fe23d7a7989cf1', NULL, NULL, '数字分身11', '1996-01-14', '1', '无事此静坐，一日当两日！', '5JSHAH', 'cfxtest:aca53cj5baasrfufs1wxtpc116x00exrspkx626k1r', NULL, NULL, NULL, '2023-12-24 22:47:23', NULL, '2024-04-13 14:47:01', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1739224452182462466', '1737841274272223232', NULL, NULL, 's11eDao', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '2', b'0', b'1', NULL, 'cfxtest:aarpncc5f4s3fxrr4vf4s4ye253t18tc825p0dg9u0', 'c8c60038e2b084495e5e72518279631f479af25410e3636c898520aab70e8412c487b0ccbd5229cb06cc726ac2ad28e0ad59585abdb923312346b29cc35b9015f6ee07011c776ddfb8fe23d7a7989cf1', '1', 'a7cfddd0a007cc1b400f5b47fa44ff8d', '火源社区，web3社区', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-25 17:59:57', NULL, '2023-12-29 12:20:15', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1739895955806822402', '1737841274272223232', NULL, NULL, 'admin001', '4eef1e1ea34879a2ae60c60815927ed9', NULL, NULL, NULL, NULL, '1', '15314216721', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-27 14:28:16', NULL, '2023-12-27 14:28:16', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1739946069988548610', '1543960566824046592', NULL, NULL, 'admin', '21232f297a57a5a743894a0e4a801fc3', NULL, NULL, NULL, NULL, '1', '17800000000', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-12-27 17:47:24', NULL, '2023-12-27 17:47:24', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747149122130952194', '1747149114505826304', NULL, NULL, 'byt-test1', '123456', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-16 14:49:46', NULL, '2024-01-16 14:49:46', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747166540232409090', '1747149114505826304', NULL, NULL, 'byt-sh', '123456', NULL, NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-16 15:58:58', NULL, '2025-03-11 11:36:30', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747171844198248449', '1747149114505826304', NULL, NULL, 'byt-sh1@qq.com', '123456', NULL, NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-16 16:20:02', NULL, '2025-03-11 11:36:31', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747172543497777154', '1747149114505826304', NULL, NULL, 'byt-sh3@qq.com', '123456', 'byt-sh3', NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-16 16:22:49', NULL, '2025-03-11 11:36:32', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747173052497539073', '1747149114505826304', NULL, NULL, 'byt-sh4@qq.com', '123456', 'byt-sh4', NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-16 16:24:51', NULL, '2025-03-11 11:36:32', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747526341072392192', '1737841274272223232', NULL, NULL, '13248799877', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'IQ7XN4', NULL, NULL, NULL, NULL, '2024-01-17 15:48:41', NULL, '2024-01-17 15:48:41', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747600558728024064', '1737841274272223232', NULL, NULL, '16676688761', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '4T29R4', NULL, NULL, NULL, NULL, '2024-01-17 20:43:36', NULL, '2024-01-17 20:43:36', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747853988159680514', '1747853982593847296', NULL, NULL, '火源', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '123456', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-18 13:30:38', NULL, '2024-01-18 13:30:38', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1747854784477655042', '1747853982593847296', NULL, NULL, '火源商户', 'e10adc3949ba59abbe56e057f20f883e', '火源商户', NULL, NULL, NULL, '1', NULL, NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '11', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-18 13:33:48', NULL, '2024-01-18 13:33:48', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1749783719914377216', '1737841274272223232', NULL, NULL, '16657788381', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'RCL5R7', NULL, NULL, NULL, NULL, '2024-01-23 21:18:43', NULL, '2024-01-23 21:18:43', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1750137997661270018', '1750137977289445376', NULL, NULL, 'AI超级助手', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, 'wechat试运营节点', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-24 20:46:31', NULL, '2024-01-24 20:46:31', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1750138015323484162', '1750137989528424448', NULL, NULL, 'AI超级助手', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, 'wechat试运营节点', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-24 20:46:33', NULL, '2024-01-24 20:46:33', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1750139178030690306', '1750137989528424448', NULL, NULL, '花尖墨', 'e10adc3949ba59abbe56e057f20f883e', '花尖墨', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-24 20:51:10', NULL, '2024-01-24 20:51:10', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1751873304026935298', '1737841274272223232', NULL, NULL, '验证码测试', 'e10adc3949ba59abbe56e057f20f883e', '验证码测试', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-29 15:41:58', NULL, '2024-01-29 15:41:58', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1751874028714532865', '1737841274272223232', NULL, NULL, '验证码测试1', 'e10adc3949ba59abbe56e057f20f883e', '验证码测试1', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-29 15:44:51', NULL, '2024-01-29 15:44:51', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1751874497063100417', '1737841274272223232', NULL, NULL, '验证码测试2', 'e10adc3949ba59abbe56e057f20f883e', '验证码测试2', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-29 15:46:43', NULL, '2024-01-29 15:46:43', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1751874710754500609', '1737841274272223232', NULL, NULL, '验证码测试3', 'e10adc3949ba59abbe56e057f20f883e', '验证码测试3', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-01-29 15:47:34', NULL, '2024-01-29 15:47:34', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755074359044636674', '1737841274272223232', NULL, NULL, 'leonard', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 11:41:49', NULL, '2024-02-07 11:41:49', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755074467857465346', '1737841274272223232', NULL, NULL, 'leonard1', 'e10adc3949ba59abbe56e057f20f883e', 'leonard1', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 11:42:15', NULL, '2024-02-07 11:42:15', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755134698847404034', '1737841274272223232', NULL, NULL, 'test1', 'e10adc3949ba59abbe56e057f20f883e', 'test1', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 15:41:35', NULL, '2024-02-07 15:41:35', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755160163502981121', '1755160153394647040', NULL, NULL, 'copilot', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '微信分身产品试运营节点', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:22:47', NULL, '2024-02-07 17:22:47', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755160962538864642', '1755160153394647040', NULL, NULL, 'leonard', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:25:57', NULL, '2024-02-07 17:25:57', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755161191484948481', '1755160153394647040', NULL, NULL, 'leonard1', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:26:52', NULL, '2024-02-07 17:26:52', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755161682868633601', '1755160153394647040', NULL, NULL, 'leonard2', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:28:49', NULL, '2024-02-07 17:28:49', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755162844372086786', '1755160153394647040', NULL, NULL, 'leonard3', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:33:26', NULL, '2024-02-07 17:33:26', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755164249958842370', '1755160153394647040', NULL, NULL, 'leonard4', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:39:01', NULL, '2024-02-07 17:39:01', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755164394255482882', '1755160153394647040', NULL, NULL, 'leonard5', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:39:35', NULL, '2024-02-07 17:39:35', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755164913074098177', '1755160153394647040', NULL, NULL, 'leonard6', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:41:39', NULL, '2024-02-07 17:41:39', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755165065348304897', '1755160153394647040', NULL, NULL, 'leonard8', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:42:15', NULL, '2024-02-07 17:42:15', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755165701435478017', '1755160153394647040', NULL, NULL, 'leonard9', 'e10adc3949ba59abbe56e057f20f883e', 'leonard', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 17:44:47', NULL, '2024-02-07 17:44:47', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755190917830254594', '1755190905624727552', NULL, NULL, 'copilot-plus', '3e3dedfafba4d62e63f0d94610092d9f', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '3', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '添加了jiujiu的copilo应用', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 19:25:00', NULL, '2024-02-07 19:25:00', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755191459612696577', '1755190905624727552', NULL, NULL, 'leonard10', 'e10adc3949ba59abbe56e057f20f883e', 'leonard10', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 19:27:08', NULL, '2024-02-07 19:27:08', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755227734629654529', '1755190905624727552', NULL, NULL, 'leonard11', 'e10adc3949ba59abbe56e057f20f883e', 'leonard11', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 21:51:17', NULL, '2024-02-07 21:51:17', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755228770081677313', '1755190905624727552', NULL, NULL, 'leonard12', 'e10adc3949ba59abbe56e057f20f883e', 'leonard12', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 21:55:24', NULL, '2024-02-07 21:55:24', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755257289776013313', '1755190905624727552', NULL, NULL, 'leonard13', 'e10adc3949ba59abbe56e057f20f883e', 'leonard13', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 23:48:43', NULL, '2024-02-07 23:48:43', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755257688247476226', '1755190905624727552', NULL, NULL, 'leonard14', 'e10adc3949ba59abbe56e057f20f883e', 'leonard14', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 23:50:18', NULL, '2024-02-07 23:50:18', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755259190018023425', '1755190905624727552', NULL, NULL, 'leonard15', 'e10adc3949ba59abbe56e057f20f883e', 'leonard15', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-07 23:56:16', NULL, '2024-02-07 23:56:16', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1755785834528813058', '1755190905624727552', NULL, NULL, 'bolei', 'e10adc3949ba59abbe56e057f20f883e', 'bolei', NULL, NULL, NULL, '1', '13632521021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-09 10:48:58', NULL, '2025-03-11 11:39:12', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1758101265591611393', '1755190905624727552', NULL, NULL, '江少', 'e10adc3949ba59abbe56e057f20f883e', '江少', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-15 20:09:40', NULL, '2024-02-15 20:09:40', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1761954362965405698', '1755190905624727552', NULL, NULL, 'leonard-test', 'e10adc3949ba59abbe56e057f20f883e', 'leonard-test', NULL, NULL, NULL, '1', '[16675588381]', NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/ai/1755190905624727552/1761954365884731392/1761954362965405698/66249fb38f089407a1548e1b.png', '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '测试号', NULL, NULL, NULL, NULL, NULL, '2024-02-26 11:20:30', NULL, '2024-04-21 13:10:22', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1762001848832872449', '1755190905624727552', NULL, NULL, 'leonard-test1', 'e10adc3949ba59abbe56e057f20f883e', 'leonard-test1', NULL, NULL, NULL, '1', '[16675588381]', NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/ai/1755190905624727552/1762001850552422400/1762001848832872449/65dc2fd85ee230a50c6b80d8.png', '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '111', NULL, NULL, NULL, NULL, NULL, '2024-02-26 14:29:11', NULL, '2024-02-26 14:29:52', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763174440083959809', '1747853982593847296', NULL, NULL, 'DDMa', '1c63129ae9db9c60c3e8aa94d3e00495', '登登澄澄', NULL, NULL, NULL, '1', '13771988050', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-02-29 20:08:39', NULL, '2024-02-29 20:08:39', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763455354097676290', '1747137575044386816', NULL, NULL, 'hua', '1f4b75b0e685ecd7fd26cd6ec7841570', '华', NULL, NULL, NULL, '1', '17375649752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-01 14:44:54', NULL, '2024-03-01 14:44:54', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763822232896843778', '1750137977289445376', NULL, NULL, 'UMJ0036', 'c4963081d169695ec89f288c86d6dd9a', 'UMJ0036', NULL, NULL, NULL, '1', '18294431150', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 15:02:45', NULL, '2024-03-02 15:02:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763822794421874690', '1755190905624727552', NULL, NULL, 'UMJ0036', 'c4963081d169695ec89f288c86d6dd9a', 'UMJ0036', NULL, NULL, NULL, '1', '18294431150', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 15:04:59', NULL, '2024-03-02 15:04:59', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763823217312575489', '1738182179072118784', NULL, NULL, 'lqh', 'e2b620620d2913dd4fb7e8ea509b1f87', 'lqh', NULL, NULL, NULL, '1', '17375639752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 15:06:40', NULL, '2024-03-02 15:06:40', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763825097203822593', '1737841274272223232', NULL, NULL, 'feidi', '58d20f9e2f822fe51862fef86a39ecea', '飞地总部', NULL, NULL, NULL, '1', '18091873216', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 15:14:08', NULL, '2024-03-02 15:14:08', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763842772411920385', '1737841274272223232', NULL, NULL, 'cq1998', '90284f275b2016f9a91526f4905167d0', '车友嘉', NULL, NULL, NULL, '1', '13883850988', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 16:24:22', NULL, '2024-03-02 16:24:22', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763844807450472449', '1755190905624727552', NULL, NULL, 'cq1998', '90284f275b2016f9a91526f4905167d0', '车友嘉', NULL, NULL, NULL, '1', '13368410772', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 16:32:27', NULL, '2024-03-02 16:32:27', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763850902260723713', '1755190905624727552', NULL, NULL, 'leonard-test11', 'e10adc3949ba59abbe56e057f20f883e', 'leonard-test11', NULL, NULL, NULL, '1', '16675588381', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 16:56:40', NULL, '2024-03-02 16:56:40', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763880310287675393', '1737841274272223232', NULL, NULL, 'lqh', 'e2b620620d2913dd4fb7e8ea509b1f87', 'lqh', NULL, NULL, NULL, '1', '17375649752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 18:53:32', NULL, '2024-03-02 18:53:32', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763881617106972673', '1755190905624727552', NULL, NULL, 'lqh', 'e2b620620d2913dd4fb7e8ea509b1f87', 'lqh', NULL, NULL, NULL, '1', '17375649752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 18:58:43', NULL, '2024-03-02 18:58:43', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763898412413005826', '1755190905624727552', NULL, NULL, 'qing', 'e2b620620d2913dd4fb7e8ea509b1f87', 'qing', NULL, NULL, NULL, '1', '13874358297', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 20:05:28', NULL, '2024-03-02 20:05:28', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1763898511402774530', '1755190905624727552', NULL, NULL, 'qin', 'e2b620620d2913dd4fb7e8ea509b1f87', 'qin', NULL, NULL, NULL, '1', '13874358297', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-02 20:05:51', NULL, '2024-03-02 20:05:51', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764123200825896961', '1755190905624727552', NULL, NULL, 'AI财税', '405890ae44d98e4c1a5e926ba61ea67a', 'AI财税', NULL, NULL, NULL, '1', '13731085881', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 10:58:41', NULL, '2024-03-03 10:58:41', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764135697452675073', '1755190905624727552', NULL, NULL, 'saiboxin', '3b6c09b22d52854a253c5be64e9c0365', 'saiboxin', NULL, NULL, NULL, '1', '15195125898', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 11:48:21', NULL, '2024-03-03 11:48:21', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764135785134600193', '1755190905624727552', NULL, NULL, '赛博芯', '3b6c09b22d52854a253c5be64e9c0365', '赛博芯', NULL, NULL, NULL, '1', '15195125898', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 11:48:42', NULL, '2024-03-03 11:48:42', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764186021987135489', '1737841274272223232', NULL, NULL, 'Nirvana', '9d7f3524c0c7a0762bb1bcd245e98c03', 'Al', NULL, NULL, NULL, '1', '15188727966', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 15:08:19', NULL, '2024-03-03 15:08:19', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764192288390557697', '1755190905624727552', NULL, NULL, 'Nirvana', '9d7f3524c0c7a0762bb1bcd245e98c03', 'Al', NULL, NULL, NULL, '1', '15188727966', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 15:33:13', NULL, '2024-03-03 15:33:13', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764205500670455810', '1755190905624727552', NULL, NULL, 'cfx', '9d7f3524c0c7a0762bb1bcd245e98c03', 'cfx', NULL, NULL, NULL, '1', '15188727966', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 16:25:43', NULL, '2024-03-03 16:25:43', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764265196597063681', '6345824413764157440', NULL, NULL, 'wang945', '0811a79e98425d69b8aa9ee9b06dc5c2', '18807489993', NULL, NULL, NULL, '1', '18807489993', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 20:22:56', NULL, '2024-03-03 20:22:56', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764265793345859585', '6345824413764157440', NULL, NULL, 'wang0732', '0811a79e98425d69b8aa9ee9b06dc5c2', 'wang945', NULL, NULL, NULL, '1', '18807489993', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 20:25:18', NULL, '2024-03-03 20:25:18', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764265947830464514', '6345824413764157440', NULL, NULL, 'jackwang', '0811a79e98425d69b8aa9ee9b06dc5c2', 'wang945', NULL, NULL, NULL, '1', '18807489993', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 20:25:55', NULL, '2024-03-03 20:25:55', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764266248230711297', '1737841274272223232', NULL, NULL, 'jackwang', '0811a79e98425d69b8aa9ee9b06dc5c2', 'wang945', NULL, NULL, NULL, '1', '18807489993', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 20:27:06', NULL, '2024-03-03 20:27:06', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764269502737985538', '1747853982593847296', NULL, NULL, 'sw666888', 'ce1da40804f498ca022bdc4db82090a9', '小小卖店', NULL, NULL, NULL, '1', '18213742227', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 20:40:02', NULL, '2024-03-03 20:40:02', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764300037803782145', '1755190905624727552', NULL, NULL, 'symbol', '001e7a998a1ed6e70becd4c697f38157', '符号', NULL, NULL, NULL, '1', '13032340172', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-03 22:41:22', NULL, '2024-03-03 22:41:22', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764451159025692674', '1747853982593847296', NULL, NULL, 'han', '6630ba1c6e34ccbd10748a93078edab7', 'andy', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 08:41:53', NULL, '2024-03-04 08:41:53', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764451250646069249', '1747853982593847296', NULL, NULL, 'hanyu2022', '6630ba1c6e34ccbd10748a93078edab7', 'andy2022', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 08:42:14', NULL, '2024-03-04 08:42:14', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764452907794608130', '1750137977289445376', NULL, NULL, 'q9831155', '6630ba1c6e34ccbd10748a93078edab7', 'andy2022', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 08:48:49', NULL, '2024-03-04 08:48:49', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764454683256074242', '1755190905624727552', NULL, NULL, 'hanyu2023', '6630ba1c6e34ccbd10748a93078edab7', 'andy2023', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 08:55:53', NULL, '2024-03-04 08:55:53', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764463305075765250', '1755190905624727552', NULL, NULL, 'Mr. Xu', '39441ae52ca32eab23a98a218a61610d', 'Cuering', NULL, NULL, NULL, '1', '13835172650', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 09:30:08', NULL, '2024-03-04 10:42:19', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764481326787899393', '1755190905624727552', NULL, NULL, 'Cuering', '39441ae52ca32eab23a98a218a61610d', 'Cuering', NULL, NULL, NULL, '1', '13835172650', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:41:45', NULL, '2024-03-04 10:41:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764481471113900034', '1755190905624727552', NULL, NULL, 'Test 00', '39441ae52ca32eab23a98a218a61610d', 'Cuering', NULL, NULL, NULL, '1', '13835172650', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:42:19', NULL, '2024-03-04 10:42:19', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764482842269626369', '1755190905624727552', NULL, NULL, 'q9831155', '6630ba1c6e34ccbd10748a93078edab7', 'andy2024', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:47:46', NULL, '2024-03-04 10:47:46', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764483782372536321', '1755190905624727552', NULL, NULL, 'one', '39441ae52ca32eab23a98a218a61610d', 'onlyone', NULL, NULL, NULL, '1', '13835172650', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:51:31', NULL, '2024-03-04 10:51:31', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764484740775194626', '1755190905624727552', NULL, NULL, 'Andy2024', '6630ba1c6e34ccbd10748a93078edab7', 'Andy2024', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:55:19', NULL, '2024-03-04 10:55:19', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764484916537503745', '1755190905624727552', NULL, NULL, 'Andy202403', '6630ba1c6e34ccbd10748a93078edab7', 'Andy202403', NULL, NULL, NULL, '1', '13589592796', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:56:01', NULL, '2024-03-04 10:56:01', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764485247044464641', '1755190905624727552', NULL, NULL, 'Tyxytsg', '39441ae52ca32eab23a98a218a61610d', 'Tyxytsg', NULL, NULL, NULL, '1', '13835172650', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:57:20', NULL, '2024-03-04 10:57:20', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764485575106146305', '1755190905624727552', NULL, NULL, 'bolei-x', 'e10adc3949ba59abbe56e057f20f883e', 'bolei-x', NULL, NULL, NULL, '1', '13632521024', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 10:58:38', NULL, '2024-03-04 10:58:38', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764662733623615490', '1755190905624727552', NULL, NULL, 'Yanwu', '4e8467e1d731f6171dcea209dd17c3c4', '心誠則靈', NULL, NULL, NULL, '1', '18907536279', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 22:42:36', NULL, '2024-03-04 22:42:36', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764669781916606465', '1738153132644503552', NULL, NULL, '乖乖兔', 'aff9150c898b4d921539b34e707b7980', '乖乖兔', NULL, NULL, NULL, '1', '18566266752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 23:10:36', NULL, '2024-03-04 23:10:36', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764670571276230657', '1755190905624727552', NULL, NULL, '乖乖兔', 'aff9150c898b4d921539b34e707b7980', '乖乖兔', NULL, NULL, NULL, '1', '18566266752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 23:13:45', NULL, '2024-03-04 23:13:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764670830714904578', '1755190905624727552', NULL, NULL, '林乖乖兔', 'e33aceaffa01628c1aebcb3cc0fe3ea5', '林乖乖兔', NULL, NULL, NULL, '1', '18566266752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 23:14:46', NULL, '2024-03-04 23:14:46', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764671139667337217', '1755190905624727552', NULL, NULL, '陈乖乖兔', '324d1907d9ca6733d399b87affe48c74', '陈乖乖兔', NULL, NULL, NULL, '1', '18566266752', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-04 23:16:00', NULL, '2024-03-04 23:16:00', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764683907552337921', '1755190905624727552', NULL, NULL, '心誠則靈', '45f65f607de7701c8a375ffb35bccc82', '心誠則靈', NULL, NULL, NULL, '1', '18907536279', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 00:06:44', NULL, '2024-03-05 00:06:44', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764691436789391362', '1755190905624727552', NULL, NULL, '心誠則靈x', '4e8467e1d731f6171dcea209dd17c3c4', '心誠則靈x', NULL, NULL, NULL, '1', '18907536279', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 00:36:39', NULL, '2024-03-05 00:36:39', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764774142009237506', '1755190905624727552', NULL, NULL, 'JINGLI', '5d69dd41949cdbe99818910ccb6c4656', 'JINGLI', NULL, NULL, NULL, '1', '15021135838', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 06:05:18', NULL, '2024-03-05 06:05:18', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764777071608946690', '1738153132644503552', NULL, NULL, 'xiaofei216', '678b3228889b93ac041e9aa6676c817c', 'xiaofei216', NULL, NULL, NULL, '1', '15933469087', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 06:16:56', NULL, '2024-03-05 06:16:56', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764820954602627073', '1755160153394647040', NULL, NULL, 'xuan200922702', '6b2973b952fb416e68d5dcae8b28d467', '小王子', NULL, NULL, NULL, '1', '18310659662', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 09:11:19', NULL, '2024-03-05 09:11:19', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764821538709790722', '1738153132644503552', NULL, NULL, 'zrmo', '0ad6461b54a317192cb78d3cc1832c55', 'zrmo', NULL, NULL, NULL, '1', '18012776988', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 09:13:38', NULL, '2024-03-05 09:13:38', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764831851903541249', '1737841274272223232', NULL, NULL, 'zrmo', '1e385729abd71844340f792065e88b67', 'zrmo', NULL, NULL, NULL, '1', '18012776988', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 09:54:37', NULL, '2024-03-05 09:54:37', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764889231144083458', '1755190905624727552', NULL, NULL, 'wuchen', '622a3dae9c72972e36fc2c5edf6706f3', '无尘', NULL, NULL, NULL, '1', '18796171279', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 13:42:37', NULL, '2024-03-05 13:42:37', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764921410276384770', '1755160153394647040', NULL, NULL, 'vcommon', 'fdba0ada6bf6e94f0873c077797393c4', 'vcommon', NULL, NULL, NULL, '1', '13817048334', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 15:50:29', NULL, '2024-03-05 15:50:29', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764922039942078465', '1755190905624727552', NULL, NULL, 'vcommon', 'd32a8d17b8163a96202257c72c726bfe', 'vcommon', NULL, NULL, NULL, '1', '13817048334', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 15:52:59', NULL, '2024-03-05 15:52:59', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764922618969939970', '1755190905624727552', NULL, NULL, '13817048334', 'fdba0ada6bf6e94f0873c077797393c4', '13817048334', NULL, NULL, NULL, '1', '13817048334', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 15:55:17', NULL, '2024-03-05 15:55:17', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764922882271567874', '1755190905624727552', NULL, NULL, '138170483341', 'fdba0ada6bf6e94f0873c077797393c4', '138170483341', NULL, NULL, NULL, '1', '13817048334', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 15:56:20', NULL, '2024-03-05 15:56:20', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1764924436978425857', '1755190905624727552', NULL, NULL, '星际', '9e29613b61995f8c0bea1d50cab801f4', '星际', NULL, NULL, NULL, '1', '18388248964', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 16:02:31', NULL, '2024-03-05 16:02:31', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1765038745632989185', '1755190905624727552', NULL, NULL, 'ninetest', '78094fec22c67a6b2a36035d51c98bd7', 'ninetest', NULL, NULL, NULL, '1', '13263311186', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-05 23:36:45', NULL, '2024-03-05 23:36:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1765537726112817153', '1738153132644503552', NULL, NULL, '18251815099', 'b8f24ad49fe22b4f47d6e18113dcad52', '18251815099', NULL, NULL, NULL, '1', '18251815099', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-07 08:39:30', NULL, '2024-03-07 08:39:30', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1765560253195313154', '6345824413764157440', NULL, NULL, 'xifanz', 'af79cc9df76e4a33ef87d14ccae0ef3c', 'xifanz', NULL, NULL, NULL, '1', '18940272720', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-07 10:09:01', NULL, '2024-03-07 10:09:01', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1765656589391454210', '1755190905624727552', NULL, NULL, 'smart', 'e10adc3949ba59abbe56e057f20f883e', 'smart', NULL, NULL, NULL, '1', '13316801311', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-07 16:31:50', NULL, '2024-03-07 16:31:50', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1765782008555986946', '1737841274272223232', NULL, NULL, '自变量', '9ba8c7ee802a842d14cf42f846c65555', '自变量', NULL, NULL, NULL, '1', '16625155721', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-08 00:50:12', NULL, '2024-03-08 00:50:12', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1765782797965942785', '1755190905624727552', NULL, NULL, '自变量', '9ba8c7ee802a842d14cf42f846c65555', 'uu', NULL, NULL, NULL, '1', '16625155721', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-08 00:53:20', NULL, '2024-03-08 00:53:20', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1766665080939831298', '1738153132644503552', NULL, NULL, '涵仔', '88273109ceb5a1e2d3e351cdee7bc367', '涵仔', NULL, NULL, NULL, '1', '13767868333', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-10 11:19:13', NULL, '2024-03-10 11:19:13', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767175438792220673', '1738153132644503552', NULL, NULL, 'tyh2024', '499b5878d2c9b550c16b8eaedecc1448', 'tlmty', NULL, NULL, NULL, '1', '18352905524', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-11 21:07:11', NULL, '2024-03-11 21:07:11', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767377106460827650', '1755190905624727552', NULL, NULL, 'csallen', '708a9c84b47404c5524405e5cbd910b8', '麦子', NULL, NULL, NULL, '1', '15674975284', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-12 10:28:33', NULL, '2024-03-12 10:28:33', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767438147794329601', '1755190905624727552', NULL, NULL, 'huangkh', '34c0200ecd6bfc8505b54ceeadf448b8', 'huangkh', NULL, NULL, NULL, '1', '18006127325', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-12 14:31:06', NULL, '2024-03-12 14:31:06', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767507432940068866', '1738153132644503552', NULL, NULL, 'sophia', '77196abb2dc1ac953261aa87d1895b08', '紫风铃', NULL, NULL, NULL, '1', '13759195727', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-12 19:06:25', NULL, '2024-03-12 19:06:25', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767782557602111490', '6345824413764157440', NULL, NULL, 'zj', 'e10adc3949ba59abbe56e057f20f883e', 'zj', NULL, NULL, NULL, '1', '15811095820', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-13 13:19:40', NULL, '2024-03-13 13:19:40', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767782785772249090', '6345824413764157440', NULL, NULL, 'zj31', 'e10adc3949ba59abbe56e057f20f883e', 'zj31', NULL, NULL, NULL, '1', '15811095820', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-13 13:20:34', NULL, '2024-03-13 13:20:34', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767783430252224513', '6345824413764157440', NULL, NULL, 'zj313', 'e10adc3949ba59abbe56e057f20f883e', 'zj313', NULL, NULL, NULL, '1', '15811095820', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-13 13:23:08', NULL, '2024-03-13 13:23:08', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767785850843783170', '1755190905624727552', NULL, NULL, 'zj313', 'e10adc3949ba59abbe56e057f20f883e', 'zj313', NULL, NULL, NULL, '1', '15811095820', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-13 13:32:45', NULL, '2024-03-13 13:32:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767788895551971329', '1737841274272223232', NULL, NULL, 'boatfly', 'a038b442d38c5f9638d0cb808109af42', 'boat', NULL, NULL, NULL, '1', '15138207219', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-13 13:44:51', NULL, '2024-03-13 13:44:51', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767789186569560065', '1755190905624727552', NULL, NULL, 'acbcqz123', 'b8f24ad49fe22b4f47d6e18113dcad52', '18251815099', NULL, NULL, NULL, '1', '18251815099', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-13 13:46:01', NULL, '2024-03-13 13:46:01', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767944908670287874', '1737841274272223232', NULL, NULL, 'ravel', '0665805284efb3f8e9b2d50569242dca', 'Ravel', NULL, NULL, NULL, '1', '13919152379', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-14 00:04:47', NULL, '2024-03-14 00:04:47', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1767946387074367490', '1755190905624727552', NULL, NULL, 'Ravel', '0665805284efb3f8e9b2d50569242dca', 'Ravel', NULL, NULL, NULL, '1', '13919152379', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-14 00:10:40', NULL, '2024-03-14 00:10:40', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1768192880637165569', '1750137977289445376', NULL, NULL, 'lizexi486', '54dc402f881a7349e72718490ba0200c', '沐子安', NULL, NULL, NULL, '1', '13631527932', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-14 16:30:09', NULL, '2024-03-14 16:30:09', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1768204519683964929', '1755190905624727552', NULL, NULL, 'ghostrin', 'e668865ca058b061fc5c4e0c1e4c4822', 'ghostrin', NULL, NULL, NULL, '1', '18823663355', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-14 17:16:23', NULL, '2024-03-14 17:16:23', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769407310909718529', '1755190905624727552', NULL, NULL, 'chenbo', 'e10adc3949ba59abbe56e057f20f883e', 'chenbo', NULL, NULL, NULL, '1', '15675837862', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 00:55:51', NULL, '2024-03-18 00:55:51', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769564799634554881', '6345824413764157440', NULL, NULL, 'bdww', 'eebd003b8d808811d4f304192180992d', 'bdsi', NULL, NULL, NULL, '1', '17364506846', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 11:21:39', NULL, '2024-03-18 11:21:39', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769571928311382017', '1737841274272223232', NULL, NULL, 'bdww2025', 'eebd003b8d808811d4f304192180992d', 'bdww2025', NULL, NULL, NULL, '1', '17364506846', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 11:49:59', NULL, '2024-03-18 11:49:59', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769572250438123522', '1755160153394647040', NULL, NULL, 'bdww2025', 'eebd003b8d808811d4f304192180992d', 'bdww2025', NULL, NULL, NULL, '1', '17364506846', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 11:51:16', NULL, '2024-03-18 11:51:16', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769572376892194817', '1755160153394647040', NULL, NULL, 'bdww2023', 'eebd003b8d808811d4f304192180992d', 'bdww2023', NULL, NULL, NULL, '1', '17364506846', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 11:51:46', NULL, '2024-03-18 11:51:46', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769572912710336513', '1755190905624727552', NULL, NULL, 'bdww2021', 'eebd003b8d808811d4f304192180992d', 'bdww2021', NULL, NULL, NULL, '1', '17364506846', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 11:53:54', NULL, '2024-03-18 11:53:54', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769626724418502658', '1755190905624727552', NULL, NULL, 'bdww1', 'eebd003b8d808811d4f304192180992d', 'bdww1', NULL, NULL, NULL, '1', '17364506846', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-18 15:27:44', NULL, '2024-03-18 15:27:44', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769750430629281793', '1755190905624727552', NULL, NULL, '萱悦', '77196abb2dc1ac953261aa87d1895b08', '萱悦', NULL, NULL, NULL, '1', '[13759195727]', NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/ai/1755190905624727552/1769750434152583168/1769750430629281793/6600bc5d8f089407a1548e13.jpg', '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '专业的银行客服小助手', NULL, NULL, NULL, NULL, NULL, '2024-03-18 23:39:17', NULL, '2024-03-25 07:51:23', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769962199234691073', '1755190905624727552', NULL, NULL, 'justice', 'cbe5af8898019a0241fe79c6de16b0fa', '个人商户', NULL, NULL, NULL, '1', '17778391021', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 13:40:47', NULL, '2024-03-19 13:40:47', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769973607733182465', '1750137977289445376', NULL, NULL, 'ornage', '015e20bdd093034e97363ef1439a72dc', 'li', NULL, NULL, NULL, '1', '18565648075', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 14:26:07', NULL, '2024-03-19 14:26:07', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769976028924526593', '1755190905624727552', NULL, NULL, 'lucien', 'b6366d345f26a0ba5b155423ba4e72a2', 'lucien', NULL, NULL, NULL, '1', '17688995261', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 14:35:44', NULL, '2024-03-19 14:35:44', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769982359286890498', '1755190905624727552', NULL, NULL, '子淡', 'a95e004804a6f1d08fe6d7712da7c86d', '子淡', NULL, NULL, NULL, '1', '18199056807', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 15:00:53', NULL, '2024-03-19 15:00:53', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769991822593015809', '1747853982593847296', NULL, NULL, 'orange', '015e20bdd093034e97363ef1439a72dc', 'orange', NULL, NULL, NULL, '1', '18565648075', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 15:38:30', NULL, '2024-03-19 15:38:30', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1769992503022370818', '1750137977289445376', NULL, NULL, 'orange', '015e20bdd093034e97363ef1439a72dc', 'orange', NULL, NULL, NULL, '1', '18565648075', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 15:41:12', NULL, '2024-03-19 15:41:12', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1770004029959618561', '1755190905624727552', NULL, NULL, 'sss', '9f6e6800cfae7749eb6c486619254b9c', 'sss', NULL, NULL, NULL, '1', '13194559231', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-19 16:27:00', NULL, '2024-03-19 16:27:00', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1770278303291195394', '1750137989528424448', NULL, NULL, 'thdqn', '25f9e794323b453885f5181f1b624d0b', 'thdqn', NULL, NULL, NULL, '1', '18487199921', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-20 10:36:52', NULL, '2024-03-20 10:36:52', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1770328347931361281', '1737841274272223232', NULL, NULL, 'ornage_', '015e20bdd093034e97363ef1439a72dc', 'orange_', NULL, NULL, NULL, '1', '18565648075', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-20 13:55:44', NULL, '2024-03-20 13:55:44', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1770634465492647938', '1755190905624727552', NULL, NULL, 'ferfi', 'd77b521ece70c79060b74c0caee9fc80', 'ferfi', NULL, NULL, NULL, '1', '15773560538', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-21 10:12:08', NULL, '2024-03-21 10:12:08', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1771003676966567938', '1755190905624727552', NULL, NULL, 'orange', '015e20bdd093034e97363ef1439a72dc', 'orange', NULL, NULL, NULL, '1', '18565648075', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-22 10:39:15', NULL, '2024-03-22 10:39:15', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1771004005040832513', '1755190905624727552', NULL, NULL, 'orange_1', '015e20bdd093034e97363ef1439a72dc', 'orange_1', NULL, NULL, NULL, '1', '18565648075', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-22 10:40:33', NULL, '2024-03-22 10:40:33', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1771186335940395010', '1750137977289445376', NULL, NULL, '东方', '8607cc2312cc1f0b301bee4dd8429208', '13298327230', NULL, NULL, NULL, '1', '13298327230', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-22 22:45:04', NULL, '2024-03-22 22:45:04', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1772145374862032898', '1747853982593847296', NULL, NULL, '1111111111', '3e6c7d141e32189c917761138b026b74', '测试商户', NULL, NULL, NULL, '1', '15244843246', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-25 14:15:57', NULL, '2024-03-25 14:15:57', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1772284156202500097', '1737841274272223232', NULL, NULL, 'yang', '3aa246e91d4ec10013f6fe2f741b0514', 'scott', NULL, NULL, NULL, '1', '13530702131', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-25 23:27:25', NULL, '2024-03-25 23:27:25', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1772932173561643010', '1737841274272223232', NULL, NULL, 'wayhvi', '9bda0c1f5b5c251b7659e2377f21cba3', 'helperbot', NULL, NULL, NULL, '1', '18024967988', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-27 18:22:24', NULL, '2024-03-27 18:22:24', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1772933210171293697', '1750137977289445376', NULL, NULL, 'wayhvi', '9bda0c1f5b5c251b7659e2377f21cba3', 'helperbot', NULL, NULL, NULL, '1', '18024967988', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-27 18:26:31', NULL, '2024-03-27 18:26:31', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1773226734972354561', '1755190905624727552', NULL, NULL, 'ruxyeah', 'fabd2393b0405b24495bb6e366e5c53c', 'ruxyeah', NULL, NULL, NULL, '1', '15000723484', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-28 13:52:53', NULL, '2024-03-28 13:52:53', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1773227212770689025', '1755190905624727552', NULL, NULL, 'ruxiang', 'e10adc3949ba59abbe56e057f20f883e', 'ruxyeah', NULL, NULL, NULL, '1', '15000723484', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-28 13:54:47', NULL, '2024-03-28 13:54:47', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1773227425270906881', '1755190905624727552', NULL, NULL, 'ruxiang123', 'e10adc3949ba59abbe56e057f20f883e', 'ruxyeah123', NULL, NULL, NULL, '1', '15000723484', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-28 13:55:37', NULL, '2024-03-28 13:55:37', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1774691304014397442', '1750137977289445376', NULL, NULL, '靓伊', 'f90b271f13d94243799a88aa37d0eaaf', '靓伊', NULL, NULL, NULL, '1', '18879842622', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-01 14:52:33', NULL, '2024-04-01 14:52:33', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1776987012243439617', '1737841274272223232', NULL, NULL, 'xiang2811', '670b14728ad9902aecba32e22fa4f6bd', '益增', NULL, NULL, NULL, '1', '18755333392', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-07 22:54:53', NULL, '2024-04-07 22:54:53', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1777004967656210433', '1755190905624727552', NULL, NULL, 'xiang2822', '670b14728ad9902aecba32e22fa4f6bd', 'xiang2822', NULL, NULL, NULL, '1', '18755333392', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-08 00:06:14', NULL, '2024-04-08 00:06:14', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1777219260225994753', '1755190905624727552', NULL, NULL, 'Shirly', 'b0edf25ee1e0e96f81ad75933945433d', 'Shirly', NULL, NULL, NULL, '1', '18123818620', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-08 14:17:45', NULL, '2024-04-08 14:17:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1777253943206789121', '6345824413764157440', NULL, NULL, 'acktui', 'e120ea280aa50693d5568d0071456460', 'acktui', NULL, NULL, NULL, '1', '13438263506', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-08 16:35:34', NULL, '2024-04-08 16:35:34', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1777254003403440129', '1747853982593847296', NULL, NULL, 'acktui', 'e120ea280aa50693d5568d0071456460', 'acktui', NULL, NULL, NULL, '1', '13438263506', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-08 16:35:49', NULL, '2024-04-08 16:35:49', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1779731624498348033', '1737841274272223232', NULL, NULL, 'linbao', 'faaa461295b836dc8e5887effd280f6c', '大树', NULL, NULL, NULL, '1', '13196231102', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-15 12:40:59', NULL, '2024-04-15 12:40:59', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1779732038958497793', '1737841274272223232', NULL, NULL, 'jack', 'faaa461295b836dc8e5887effd280f6c', '大树', NULL, NULL, NULL, '1', '13196231102', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-15 12:42:38', NULL, '2024-04-15 12:42:38', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1782970668748460034', '123456789', '0', NULL, '18856904203', '123456', NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, '3', b'0', b'0', '01b4bf34721da635867f1110f02f5aad35427af67d6ac34e5083681c4f66bae3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-24 11:11:48', NULL, '2024-04-24 11:11:45', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1783027298152255490', '1010338399', '0', NULL, '18856904203', '123456', NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, '3', b'0', b'0', 'b99732f85594d22a45df04dc4f9a0d9a35427af67d6ac34e5083681c4f66bae3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-24 14:56:50', NULL, '2024-04-24 14:56:46', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1783106409147240449', '1010338399', '0', NULL, '18856904272', '123456', NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, '3', b'0', b'0', 'eb745c3fdba56c700cd510e68c031acd35427af67d6ac34e5083681c4f66bae3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-24 20:11:11', NULL, '2024-04-24 20:11:08', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1785207606567026689', '6345824413764157440', NULL, NULL, 'liuhx', '4d5008f1025732e14d7b032b7a599d79', 'liuhx', NULL, NULL, NULL, '1', '18953173980', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-30 15:20:35', NULL, '2024-04-30 15:20:35', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1785208069047762946', '1738153132644503552', NULL, NULL, 'liuhx', '4d5008f1025732e14d7b032b7a599d79', 'liuhx', NULL, NULL, NULL, '1', '18953173980', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-04-30 15:22:26', NULL, '2024-04-30 15:22:26', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1791043590420480002', '1755190905624727552', NULL, NULL, '测试', 'c889a498381487e0306dde9b63e881af', '测试', NULL, NULL, NULL, '1', '13266565504', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-16 17:50:42', NULL, '2024-05-16 17:50:42', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1791061300114145282', '1755190905624727552', NULL, NULL, 'yesan', '0f0b186bad4cbf082b04a243ce345295', '测试1', NULL, NULL, NULL, '1', '13266565504', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-16 19:01:05', NULL, '2024-05-16 19:01:05', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1791061382582550529', '1755190905624727552', NULL, NULL, 'yesan1', '0f0b186bad4cbf082b04a243ce345295', '测试1', NULL, NULL, NULL, '1', '13266565504', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-16 19:01:24', NULL, '2024-05-16 19:01:24', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1791061572093788162', '1755190905624727552', NULL, NULL, 'yesingle', '0f0b186bad4cbf082b04a243ce345295', '测试1', NULL, NULL, NULL, '1', '13266565504', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-16 19:02:09', NULL, '2024-05-16 19:02:09', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1791275323157954561', '1755160153394647040', NULL, NULL, 'yesingle', '82e9b66046547bf3f136c2766d5d06f8', 'yesingle', NULL, NULL, NULL, '1', '13266565504', NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-17 09:11:32', NULL, '2024-05-17 09:11:32', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1796753213919293442', '1796753195226238976', NULL, NULL, 'hy1', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-06-01 11:58:42', NULL, '2024-06-01 11:58:42', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1796759931768238081', '1796759813896671232', NULL, NULL, 'hy2', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '2', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-06-01 12:25:28', NULL, '2024-06-01 12:25:28', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1796777428806471681', '1796759813896671232', NULL, NULL, 'hy2-m1', 'e10adc3949ba59abbe56e057f20f883e', 'hy2', NULL, NULL, NULL, '1', NULL, NULL, NULL, '1', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '11', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-06-01 13:34:56', NULL, '2024-06-01 13:34:56', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1827579279643381760', '1801458632269893633', NULL, NULL, '13632521021', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'GAEC1N', NULL, NULL, NULL, NULL, '2024-08-25 13:30:29', NULL, '2025-03-11 11:38:54', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1840358006076870656', '1801458632269893632', NULL, NULL, '18213742227', '511c6fd9e44bc92f9083a8a56fe7a593', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'AUWSJF', NULL, NULL, NULL, NULL, '2024-09-29 19:48:35', NULL, '2024-09-29 19:48:35', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1840373230326648832', '1801458632269893632', NULL, NULL, '18961216871', 'a37f6e6255e0e94cf0292fc4f4a138e7', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'RAGDKF', NULL, NULL, NULL, NULL, '2024-09-29 20:49:05', NULL, '2024-09-29 20:49:05', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1840412006461607936', '1801458632269893632', NULL, NULL, '13662992256', '72b4bbfc33ecbc37a010fbeedacbd8a2', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7PW26K', NULL, NULL, NULL, NULL, '2024-09-29 23:23:10', NULL, '2024-09-29 23:23:10', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1840512209545990144', '1801458632269893632', NULL, NULL, '18868577710', 'fd6e3123f6d29a2c4f65fe02c0a3a75e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Q2E65I', NULL, NULL, NULL, NULL, '2024-09-30 06:01:20', NULL, '2025-03-09 13:49:39', 1, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1840555906924941312', '1801458632269893632', NULL, NULL, '18987177035', '1458d80fadc902ba97b3e5c5af478dc5', NULL, NULL, '星际', NULL, '1', '18987177035', 'bo.qian@live.com', NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, '1981-11-11 ', '1', 'Now', 'NMP2YV', NULL, NULL, NULL, NULL, '2024-09-30 08:54:58', NULL, '2024-10-12 09:42:18', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1840712685256314880', '1801458632269893632', NULL, NULL, '13632521025', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'EUAP3V', NULL, NULL, NULL, NULL, '2024-09-30 19:17:57', NULL, '2025-03-09 13:49:29', 1, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1841096490085388288', '1801458632269893632', NULL, NULL, '16675588381', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'JJGKPN', NULL, NULL, NULL, NULL, '2024-10-01 20:43:03', NULL, '2025-03-09 13:49:25', 1, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1844773482802581504', '1801458632269893632', NULL, NULL, '13919152379', 'a1621873f2964ce8618c9ae855c1d55f', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'L59IE6', NULL, NULL, NULL, NULL, '2024-10-12 00:14:07', NULL, '2025-03-09 13:49:21', 1, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1850896443318079488', '1846190237240397824', '3', 'ooT1L6nFsaTU_Z62unl8NgegYm00', 'ooT1L6nFsaTU_Z62unl8NgegYm00', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '小红', NULL, '1', '16688899871', '123@qq.com', '头像地址', '0', b'0', b'0', NULL, 'cfxtest:aakn4cg4nuf77s41p6414mwcmh2s92n9a2x31eyj9a', '6adb9aedad938e66a75911db1afc74423f4cc21e77261e656989a11a6ccef145', NULL, NULL, NULL, NULL, '2', NULL, 'K4XOM2', NULL, NULL, NULL, NULL, '2024-10-28 21:44:34', NULL, '2024-10-29 01:09:19', 0, 'JVNjQM3h95fHg2DJd0aljA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1860209334991065088', '1846190237240397824', '3', 'oqASg7VBjqmskvK7uBZRsfZwgTT4', 'oqASg7VBjqmskvK7uBZRsfZwgTT4', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, 'leonard', NULL, '1', '13249059981', NULL, NULL, '0', b'1', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1PIIV5', NULL, NULL, NULL, NULL, '2024-11-23 14:30:41', NULL, '2024-11-24 17:29:14', 0, 'hJCJVsFDufyeEjJYWJx59w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1860365500937277440', '1846190237240397824', '3', '', '', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7608QZ', NULL, NULL, NULL, NULL, '2024-11-24 00:51:14', NULL, '2024-11-24 00:51:14', 0, '', NULL);
INSERT INTO `crm_customer_base` VALUES ('1860638624907923456', '1846190237240397824', '3', 'oqASg7fBAXfWv0L9Qe75ohQ7U4Gs', 'oqASg7fBAXfWv0L9Qe75ohQ7U4Gs', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', '17679181864', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'EUSJ4Y', NULL, NULL, NULL, NULL, '2024-11-24 18:56:31', NULL, '2024-12-15 22:53:55', 0, '55p+Wh0K29EcClNPwInMFA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1865652644459188224', '1846190237240397824', '3', 'oqASg7S66ZFo-G9fsgJWnYSvvZAk', 'oqASg7S66ZFo-G9fsgJWnYSvvZAk', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', '13361784696', NULL, NULL, '0', b'1', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'X41SY7', NULL, NULL, NULL, NULL, '2024-12-08 15:00:27', NULL, '2024-12-15 22:53:50', 0, 'aPldjWdHO7eKLDWBuP7R4A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1869566390399275008', '1846190237240397824', '3', 'oqASg7Q-eVuRqOr6OhDoa7Y8nMvg', 'oqASg7Q-eVuRqOr6OhDoa7Y8nMvg', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, NULL, '1', '13632521022', NULL, NULL, '0', b'1', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '22VW1T', NULL, NULL, NULL, NULL, '2024-12-19 10:12:17', NULL, '2025-03-11 11:36:56', 0, 'M7CfJXsYw/DXPp+F97VA0w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1894252987572948992', '1801458632269893632', '3', 'oJTwA7dVZBvHwWoi4qRtmmGkW8m8', 'oJTwA7dVZBvHwWoi4qRtmmGkW8m8', NULL, NULL, NULL, 'leonard', NULL, '1', '13249059981', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'C9A501', NULL, NULL, NULL, NULL, '2025-02-25 13:08:00', NULL, '2025-02-25 13:08:00', 0, 'r6z6SBIeIOjj0XBVR4kIYQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1894339016384450560', '1801458632269893632', '3', 'oJTwA7T0BAikBkE9Q7ATSmufiv78', 'oJTwA7T0BAikBkE9Q7ATSmufiv78', NULL, NULL, NULL, '博羸兄弟', NULL, '1', '13632521021', NULL, 'https://yue17-miniapp.oss-cn-beijing.aliyuncs.com/admin/67d2e2d5e4b0711c92cc06bc.jpeg', '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, '简介', '1981-11-11 ', NULL, NULL, 'T8DQ1U', NULL, NULL, NULL, NULL, '2025-02-25 18:49:51', NULL, '2025-03-11 11:36:15', 0, 'PKp0/1/G1KIDgkuGIl6xOA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1894958621305999360', '1801458632269893632', '3', 'oJTwA7a1CUmvLrPVnmp4yKpJXhcQ', 'oJTwA7a1CUmvLrPVnmp4yKpJXhcQ', NULL, NULL, NULL, '粤十七｜Mark@Xiong', NULL, '1', '18123838895', NULL, 'https://yue17-miniapp.oss-cn-beijing.aliyuncs.com/admin/67e3f7efe4b05dfbc30a30dc.jpeg', '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HGAAWD', NULL, NULL, NULL, NULL, '2025-02-27 11:51:56', NULL, '2025-02-27 11:51:56', 0, 'THOxcbH+DeBtHQWbd/ding==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1895111719949307904', '1801458632269893632', '3', 'oJTwA7UQOVo9xNL8Uq4dkjJsTC4Q', 'oJTwA7UQOVo9xNL8Uq4dkjJsTC4Q', NULL, NULL, NULL, '火源学院-小助手', NULL, '1', '13630303030', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NDZHXN', NULL, NULL, NULL, NULL, '2025-02-27 22:00:18', NULL, '2025-02-27 22:00:18', 0, '792R9WWwtrrBZ76lRam28g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1895670429851455488', '1801458632269893632', '3', 'oJTwA7fYuvDKTAFGt63hmnDrcyFA', 'oJTwA7fYuvDKTAFGt63hmnDrcyFA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'WVFBGZ', NULL, NULL, NULL, NULL, '2025-03-01 11:00:25', NULL, '2025-03-09 13:49:15', 1, 'a5J7SkydsGrO1Mcrtic0dg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1895670456736944128', '1801458632269893632', '3', 'oJTwA7Qv1Ao_4ixfitxY3gDsxnpY', 'oJTwA7Qv1Ao_4ixfitxY3gDsxnpY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2A4ZVL', NULL, NULL, NULL, NULL, '2025-03-01 11:00:31', NULL, '2025-03-09 13:49:13', 1, 'cWS6Ng3Tf9d2uZ1BesT8Ag==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1895754348491837440', '1801458632269893632', '3', 'oJTwA7a31OtyOEcmVt1kpyMI5T9A', 'oJTwA7a31OtyOEcmVt1kpyMI5T9A', NULL, NULL, NULL, '方柏婷', NULL, '1', '13048944327', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FDTFFC', NULL, NULL, NULL, NULL, '2025-03-01 16:33:52', NULL, '2025-03-01 16:33:52', 0, 'YJpslEREit0lBW3P/86Mrg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1895777251895676928', '1801458632269893632', '3', 'oJTwA7VLGpgg8aEJStsWocEmhINA', 'oJTwA7VLGpgg8aEJStsWocEmhINA', NULL, NULL, NULL, 'SQQ-AE｜Mark@xiong', NULL, '1', '18566696475', NULL, 'https://yue17-miniapp.oss-cn-beijing.aliyuncs.com/admin/67e3f95ce4b05dfbc30a30dd.jpeg', '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FEBJ8Z', NULL, NULL, NULL, NULL, '2025-03-01 18:04:53', NULL, '2025-03-01 18:04:53', 0, 'IhtTnR7oOOYr+3zOaUM+Sg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897271318773108736', '1801458632269893632', '3', 'oJTwA7YCSWSlxO5ouY9eMJY8Kyb8', 'oJTwA7YCSWSlxO5ouY9eMJY8Kyb8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'H42G0F', NULL, NULL, NULL, NULL, '2025-03-05 21:01:46', NULL, '2025-03-09 13:49:09', 1, 'XGs8MAjtoBbiljvW+vjiRw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897271321709121536', '1801458632269893632', '3', 'oJTwA7fXrMaLLa800PKNXpaSfces', 'oJTwA7fXrMaLLa800PKNXpaSfces', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'GZK1NH', NULL, NULL, NULL, NULL, '2025-03-05 21:01:47', NULL, '2025-03-09 13:49:06', 1, '/53ny3Nf2ts5Ue/RzM6llg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897282638629179392', '1801458632269893632', '3', 'oJTwA7V-HYjTEbKpt5PrcxJYxEiE', 'oJTwA7V-HYjTEbKpt5PrcxJYxEiE', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7QTYCD', NULL, NULL, NULL, NULL, '2025-03-05 21:46:45', NULL, '2025-03-09 13:49:04', 1, '9IMZVjBj5YeJPnUcqY5i7A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897282653841920000', '1801458632269893632', '3', 'oJTwA7bvybdKkLLCU2xBr_WLRrLo', 'oJTwA7bvybdKkLLCU2xBr_WLRrLo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'WBKMFG', NULL, NULL, NULL, NULL, '2025-03-05 21:46:49', NULL, '2025-03-09 13:49:02', 1, 'iRv9mA9gx5ZCiEg1VMeyCA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897651748298952704', '1801458632269893632', '3', 'oJTwA7esri1vWUwZw8HsZ0ViPSv8', 'oJTwA7esri1vWUwZw8HsZ0ViPSv8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NRK0SB', NULL, NULL, NULL, NULL, '2025-03-06 22:13:28', NULL, '2025-03-09 13:48:59', 1, 'WXxvey9G6n/UN5LucqsITA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897845906947051520', '1801458632269893632', '3', 'oJTwA7cYaJftOFSboJ8bBGFAeago', 'oJTwA7cYaJftOFSboJ8bBGFAeago', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '829NPD', NULL, NULL, NULL, NULL, '2025-03-07 11:04:59', NULL, '2025-03-09 13:48:53', 1, 'pYTuJ4a6xaZJFe0wjnDQ7Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1897845908008210432', '1801458632269893632', '3', 'oJTwA7a6A7hGO-20iioiqz6D9rRg', 'oJTwA7a6A7hGO-20iioiqz6D9rRg', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2F5URG', NULL, NULL, NULL, NULL, '2025-03-07 11:04:59', NULL, '2025-03-09 13:48:56', 1, '5xmiX8N3o0N7in6YtHzf1A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1898318636230250496', '1801458632269893632', '3', 'oJTwA7QNQ03F1paUPsjh01XbNvqw', 'oJTwA7QNQ03F1paUPsjh01XbNvqw', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '28HOZT', NULL, NULL, NULL, NULL, '2025-03-08 18:23:26', NULL, '2025-03-09 13:48:45', 1, 'tRkbm5FxfmemoZd3/B3T/g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1898620923444072448', '1801458632269893632', '3', 'oJTwA7dAdYa4HhU4SAqaMKv2oSBA', 'oJTwA7dAdYa4HhU4SAqaMKv2oSBA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'V36BWK', NULL, NULL, NULL, NULL, '2025-03-09 14:24:37', NULL, '2025-03-09 14:24:37', 0, 'scA1LeuIQ6nwUDOSR5Va8A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1898950034335076352', '1801458632269893632', '3', 'oJTwA7bvybdKkLLCU2xBr_WLRrLo', 'oJTwA7bvybdKkLLCU2xBr_WLRrLo', NULL, NULL, NULL, NULL, NULL, '1', '13147323662', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'B47OPY', NULL, NULL, NULL, NULL, '2025-03-10 12:12:23', NULL, '2025-03-10 12:12:23', 0, 'wjXT7NvQUy9Hw62IHVxJ6w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899013164964646912', '1801458632269893632', '3', 'oJTwA7bCUj-zoqJQe4uHDo9AwoUA', 'oJTwA7bCUj-zoqJQe4uHDo9AwoUA', NULL, NULL, NULL, NULL, NULL, '1', '13413563192', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CRJ5LG', NULL, NULL, NULL, NULL, '2025-03-10 16:23:15', NULL, '2025-03-10 16:23:15', 0, 'ETNgscuaHUGm6lGZEOJAjw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899013438898835456', '1801458632269893632', '3', 'oJTwA7eNkoKGklDscWOUPR-BEFfU', 'oJTwA7eNkoKGklDscWOUPR-BEFfU', NULL, NULL, NULL, NULL, NULL, '1', '13602279669', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZHEPP8', NULL, NULL, NULL, NULL, '2025-03-10 16:24:20', NULL, '2025-03-10 16:24:20', 0, 'lux2LEootqSq+tCrry+2ag==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899014350237208576', '1801458632269893632', '3', 'oJTwA7fqDabXGlczKkhgSpY7ZU6A', 'oJTwA7fqDabXGlczKkhgSpY7ZU6A', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8ACNSP', NULL, NULL, NULL, NULL, '2025-03-10 16:27:57', NULL, '2025-03-10 16:27:57', 0, 'iK5qZ/+uqSIi1Tvjj3ocIw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899021131621470208', '1801458632269893632', '3', 'oJTwA7ejLxdlw8_enccKwiSZuxRM', 'oJTwA7ejLxdlw8_enccKwiSZuxRM', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'L3QZP4', NULL, NULL, NULL, NULL, '2025-03-10 16:54:54', NULL, '2025-03-10 16:54:54', 0, 'WDYc1taVP5yZXw6t8gqZPA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899027150011502592', '1801458632269893632', '3', 'oJTwA7bRkxoFwxCh6x6HabNzeomg', 'oJTwA7bRkxoFwxCh6x6HabNzeomg', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PY1XO6', NULL, NULL, NULL, NULL, '2025-03-10 17:18:49', NULL, '2025-03-10 17:18:49', 0, 'mdIw/1b/CBWQ23qeI6zqaw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899309161842348032', '1801458632269893632', '4', NULL, '13798353468', '131486', '媛宝', NULL, NULL, '131486', '1', '13798353468', NULL, NULL, '0', b'0', b'1', NULL, 'cfxtest:aasfdwx12e8yu39z7zwbwypuc71jetu1wyham0191x', '0c45f011b45919f5cbc24ac13ddf74d04e417b704b0314dd729642573fb0daea8e533eb4f6ee330e6e53874296d5255b34459c390ca2eba0a83fe3fc5f8e1ebdf6ee07011c776ddfb8fe23d7a7989cf1', NULL, NULL, NULL, NULL, NULL, NULL, '13HRTI', NULL, NULL, NULL, NULL, '2025-03-11 11:59:26', NULL, '2025-03-11 12:13:20', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1899371683479949312', '1801458632269893632', '3', 'oJTwA7TFdE7jTf8348ffKGXNOhRE', 'oJTwA7TFdE7jTf8348ffKGXNOhRE', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3BSZ2B', NULL, NULL, NULL, NULL, '2025-03-11 16:07:52', NULL, '2025-03-11 16:07:52', 0, 'Jb78ijDmt4rb+kp4eTEHqg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899414286959251456', '1801458632269893632', '3', 'oJTwA7cCZO1ougM2Fd2bcK7A0a7c', 'oJTwA7cCZO1ougM2Fd2bcK7A0a7c', NULL, NULL, NULL, NULL, NULL, '1', '13361784696', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HA42R4', NULL, NULL, NULL, NULL, '2025-03-11 18:57:10', NULL, '2025-03-11 18:57:10', 0, 'hKoily7giR+VI1B/5dadew==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899415466225569792', '1801458632269893632', '3', 'oJTwA7QOv4IbEIn0SiabxoaBChcY', 'oJTwA7QOv4IbEIn0SiabxoaBChcY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'XHZ55V', NULL, NULL, NULL, NULL, '2025-03-11 19:01:51', NULL, '2025-03-11 19:01:51', 0, 'MadxagFXIHx5BscXldxjtg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899504351576199168', '1801458632269893632', '3', 'oJTwA7V-HYjTEbKpt5PrcxJYxEiE', 'oJTwA7V-HYjTEbKpt5PrcxJYxEiE', NULL, NULL, NULL, NULL, NULL, '1', '13510638097', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '5A9JFD', NULL, NULL, NULL, NULL, '2025-03-12 00:55:03', NULL, '2025-03-12 00:55:03', 0, '6KwNdPsHYw2bDYb4KhblVg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899685929535279104', '1801458632269893632', '3', 'oJTwA7ZkD0BT5Tu02U8js-keXNbk', 'oJTwA7ZkD0BT5Tu02U8js-keXNbk', NULL, NULL, NULL, NULL, NULL, '1', '13902944639', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'BXITS8', NULL, NULL, NULL, NULL, '2025-03-12 12:56:34', NULL, '2025-03-12 12:56:34', 0, '9sKwqye03r9o/tCeEaudrQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899696506584305664', '1801458632269893632', '4', NULL, '18548124999', '111111', '杨永江', NULL, NULL, '111111', '1', '18548124999', NULL, NULL, '0', b'0', b'1', NULL, 'cfxtest:aak39wrbzw958ddkdk28yc7t4dvzutajeum9yp4bdc', '3754a37e5b1b5ece74e47eaa6bc6371fd9a9f8d7b00f92f063fecf6601269f1af136154996af3a0b2a0319f83f45ba9e9a771a540d2c4a017bf7ad04094befacf6ee07011c776ddfb8fe23d7a7989cf1', NULL, NULL, NULL, NULL, NULL, NULL, '4CQQ3B', NULL, NULL, NULL, NULL, '2025-03-12 13:38:36', NULL, '2025-03-12 13:42:51', 0, NULL, NULL);
INSERT INTO `crm_customer_base` VALUES ('1899997913032036352', '1801458632269893632', '3', 'oJTwA7cYaJftOFSboJ8bBGFAeago', 'oJTwA7cYaJftOFSboJ8bBGFAeago', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8XL879', NULL, NULL, NULL, NULL, '2025-03-13 09:36:17', NULL, '2025-03-13 09:36:17', 0, '5IbxinfnqBPk/39/+eRQBQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899997913153671168', '1801458632269893632', '3', 'oJTwA7Qv1Ao_4ixfitxY3gDsxnpY', 'oJTwA7Qv1Ao_4ixfitxY3gDsxnpY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8QJ2YD', NULL, NULL, NULL, NULL, '2025-03-13 09:36:17', NULL, '2025-03-13 09:36:17', 0, '/2aumIhspBf8UY1jH8uQgw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1899998418307256320', '1801458632269893632', '3', 'oJTwA7bpdFpB13NTgVGtVoMiHvl4', 'oJTwA7bpdFpB13NTgVGtVoMiHvl4', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HJS387', NULL, NULL, NULL, NULL, '2025-03-13 09:38:17', NULL, '2025-03-13 09:38:17', 0, 'J7UhHutDnxpV5XQkSdNwKQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900002524908359680', '1801458632269893632', '3', 'oJTwA7fNdqAEWlgwM6qGmmD7UPWo', 'oJTwA7fNdqAEWlgwM6qGmmD7UPWo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8Z8FMP', NULL, NULL, NULL, NULL, '2025-03-13 09:54:36', NULL, '2025-03-13 09:54:36', 0, 'eJAKoGIWt/0iNY41cKSTWg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900005915831177216', '1801458632269893632', '3', 'oJTwA7Zr5f2uL9cC7qvcOcAH36KU', 'oJTwA7Zr5f2uL9cC7qvcOcAH36KU', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '5B648Y', NULL, NULL, NULL, NULL, '2025-03-13 10:08:05', NULL, '2025-03-13 10:08:05', 0, 'o4FQha97IIKM0S6zh21o9w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900008903136120832', '1801458632269893632', '3', 'oJTwA7YCSWSlxO5ouY9eMJY8Kyb8', 'oJTwA7YCSWSlxO5ouY9eMJY8Kyb8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'JCY4G5', NULL, NULL, NULL, NULL, '2025-03-13 10:19:57', NULL, '2025-03-13 10:19:57', 0, '4FiMlrN4DwRrE3jzf+3PxA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900008904130170880', '1801458632269893632', '3', 'oJTwA7a4Vx-uelKLXkb2brdQCY_M', 'oJTwA7a4Vx-uelKLXkb2brdQCY_M', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ODCX10', NULL, NULL, NULL, NULL, '2025-03-13 10:19:57', NULL, '2025-03-13 10:19:57', 0, 'p/9oiIWimr56OqfaAkB/Gw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900011084362944512', '1801458632269893632', '3', 'oJTwA7c9T1-vm-oemcqhjI2bKqjE', 'oJTwA7c9T1-vm-oemcqhjI2bKqjE', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HLFD70', NULL, NULL, NULL, NULL, '2025-03-13 10:28:37', NULL, '2025-03-13 10:28:37', 0, '03UyWU5/er+xh2HAg2hk/Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900015891031134208', '1801458632269893632', '3', 'oJTwA7e46S8LlkkqWDAa4PyYWMys', 'oJTwA7e46S8LlkkqWDAa4PyYWMys', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'XQBCWY', NULL, NULL, NULL, NULL, '2025-03-13 10:47:43', NULL, '2025-03-13 10:47:43', 0, '32oOVq8S38U9gOhH7z8sHw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900089644872241152', '1801458632269893632', '3', 'oJTwA7XQgg5IosAhytwMzV9L2llU', 'oJTwA7XQgg5IosAhytwMzV9L2llU', NULL, NULL, NULL, NULL, NULL, '1', '14758900983', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PX17MD', NULL, NULL, NULL, NULL, '2025-03-13 15:40:48', NULL, '2025-03-13 15:40:48', 0, '0SXHbmo82bJAPeBgFQoMEg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900150827201466368', '1801458632269893632', '3', 'oJTwA7ZP60_XAo4yqr2gji0Z2b8Q', 'oJTwA7ZP60_XAo4yqr2gji0Z2b8Q', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '61M7DL', NULL, NULL, NULL, NULL, '2025-03-13 19:43:55', NULL, '2025-03-13 19:43:55', 0, 'tN/1CCPAGVXjptZAplLOxw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900164559797358592', '1801458632269893632', '3', 'oJTwA7Vn3KUOu7prsFDBrdU8HZeo', 'oJTwA7Vn3KUOu7prsFDBrdU8HZeo', NULL, NULL, NULL, NULL, NULL, '1', '15914149133', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7REYUD', NULL, NULL, NULL, NULL, '2025-03-13 20:38:29', NULL, '2025-03-13 20:38:29', 0, 'gs+lnMYjL6o9GnoVApwjyw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900165286036901888', '1801458632269893632', '3', 'oJTwA7d-CF_bpKBZusT_egS16hTs', 'oJTwA7d-CF_bpKBZusT_egS16hTs', NULL, NULL, NULL, NULL, NULL, '1', '19065298615', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZUSUHJ', NULL, NULL, NULL, NULL, '2025-03-13 20:41:22', NULL, '2025-03-13 20:41:22', 0, 'ijy65uXdgx4eUP3jXhTRQg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900172862816391168', '1801458632269893632', '3', 'oJTwA7Qt6hYjSLFAmbSV3Z0xZPpA', 'oJTwA7Qt6hYjSLFAmbSV3Z0xZPpA', NULL, NULL, NULL, NULL, NULL, '1', '13810794967', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'XAHVOR', NULL, NULL, NULL, NULL, '2025-03-13 21:11:28', NULL, '2025-03-13 21:11:28', 0, 'XX4A6Qxqsq2S5NRUz5Pi9A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900176469775224832', '1801458632269893632', '3', 'oJTwA7UzralOB00mO2jVNxsM4l_E', 'oJTwA7UzralOB00mO2jVNxsM4l_E', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '01FJUT', NULL, NULL, NULL, NULL, '2025-03-13 21:25:48', NULL, '2025-03-13 21:25:48', 0, 'MBycrOpHZgaPe+9CTq7qxQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900429244618641408', '1801458632269893632', '3', 'oJTwA7QNQ03F1paUPsjh01XbNvqw', 'oJTwA7QNQ03F1paUPsjh01XbNvqw', NULL, NULL, NULL, NULL, NULL, '1', '13590303507', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'JGO833', NULL, NULL, NULL, NULL, '2025-03-14 14:10:14', NULL, '2025-03-14 14:10:14', 0, '0G9g0DuUwqcMiw6W4vbvLA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900437342775283712', '1801458632269893632', '3', 'oJTwA7SW_7Ur4CsVncQU_TAKc5lk', 'oJTwA7SW_7Ur4CsVncQU_TAKc5lk', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'U2JFWE', NULL, NULL, NULL, NULL, '2025-03-14 14:42:25', NULL, '2025-03-14 14:42:25', 0, 'cow88X+rdT3KgtoNHcpqrg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900783796916195328', '1801458632269893632', '3', 'oJTwA7StCEfC_Ce2kAm4o-zDxo9w', 'oJTwA7StCEfC_Ce2kAm4o-zDxo9w', NULL, NULL, NULL, NULL, NULL, '1', '15367880888', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'S96BB6', NULL, NULL, NULL, NULL, '2025-03-15 13:39:06', NULL, '2025-03-15 13:39:06', 0, 'MUM4PfhISeyFePEaqHGBYg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900788178655252480', '1801458632269893632', '3', 'oJTwA7YmSUdVJ0iNDUo_e_NQHbIA', 'oJTwA7YmSUdVJ0iNDUo_e_NQHbIA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8HQYD1', NULL, NULL, NULL, NULL, '2025-03-15 13:56:31', NULL, '2025-03-15 13:56:31', 0, 'lcnmeQcPxK3sFnMWFkTbqA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900795565348884480', '1801458632269893632', '3', 'oJTwA7WsqyiXNcAC4PByKc7PPiLc', 'oJTwA7WsqyiXNcAC4PByKc7PPiLc', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2ODM5Z', NULL, NULL, NULL, NULL, '2025-03-15 14:25:52', NULL, '2025-03-15 14:25:52', 0, 'BpKwa8xHNk3b2WSI48cY+A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900848842157461504', '1801458632269893632', '3', 'oJTwA7dfYnWE_Okxle2acbEPWSFc', 'oJTwA7dfYnWE_Okxle2acbEPWSFc', NULL, NULL, NULL, NULL, NULL, '1', '13416472917', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'C4OPIU', NULL, NULL, NULL, NULL, '2025-03-15 17:57:34', NULL, '2025-03-15 17:57:34', 0, '0r5wZGHQ73hte2fZP2f/Dw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1900944778892283904', '1801458632269893632', '3', 'oJTwA7d0pkP9bX-IA39Vz8idEJFU', 'oJTwA7d0pkP9bX-IA39Vz8idEJFU', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'G93YX3', NULL, NULL, NULL, NULL, '2025-03-16 00:18:47', NULL, '2025-03-16 00:18:47', 0, '1NUecefsl9Ixor6lDsGB2A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1901540556186718208', '1801458632269893632', '3', 'oJTwA7SSTppRBF5dc4PMJ55WhRBY', 'oJTwA7SSTppRBF5dc4PMJ55WhRBY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '6WRI7O', NULL, NULL, NULL, NULL, '2025-03-17 15:46:12', NULL, '2025-03-17 15:46:12', 0, 'EMLX4bZ7Bvq/AvvZcmRu1g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1901552156947910656', '1801458632269893632', '3', 'oJTwA7fBUyxPJHbbRwTmdurBCNRU', 'oJTwA7fBUyxPJHbbRwTmdurBCNRU', NULL, NULL, NULL, NULL, NULL, '1', '19273937795', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7PKENV', NULL, NULL, NULL, NULL, '2025-03-17 16:32:18', NULL, '2025-03-17 16:32:18', 0, '6xDJOaxlnslSrmxphbC3KQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1902207089061793792', '1801458632269893632', '3', 'oJTwA7V5fyXB0zV3jpSmtHdFRbbY', 'oJTwA7V5fyXB0zV3jpSmtHdFRbbY', NULL, NULL, NULL, NULL, NULL, '1', '18588801605', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FBD935', NULL, NULL, NULL, NULL, '2025-03-19 11:54:46', NULL, '2025-03-19 11:54:46', 0, 'aW++4NVWgpVhCzHlrbuMhw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1902334797766856704', '1801458632269893632', '3', 'oJTwA7aqzhG7ke3k71MH85oRe8ms', 'oJTwA7aqzhG7ke3k71MH85oRe8ms', NULL, NULL, NULL, NULL, NULL, '1', '13169280008', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'J4T6FO', NULL, NULL, NULL, NULL, '2025-03-19 20:22:14', NULL, '2025-03-19 20:22:14', 0, 'fUgiF4E7dCHnosWsLLPDVw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1902334827940679680', '1801458632269893632', '3', 'oJTwA7bXgafA-ywauKBN96SvZn6A', 'oJTwA7bXgafA-ywauKBN96SvZn6A', NULL, NULL, NULL, NULL, NULL, '1', '19925683745', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '71URMP', NULL, NULL, NULL, NULL, '2025-03-19 20:22:21', NULL, '2025-03-19 20:22:21', 0, 'uOLfg/e1+/93WiQLxTciYA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1902609821220343808', '1801458632269893632', '3', 'oJTwA7eE51atItC4A2NWvMTWZGxE', 'oJTwA7eE51atItC4A2NWvMTWZGxE', NULL, NULL, NULL, NULL, NULL, '1', '18998931125', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8FG7F5', NULL, NULL, NULL, NULL, '2025-03-20 14:35:04', NULL, '2025-03-20 14:35:04', 0, '6kk3PYhzQkf6bdL1Rs0HnA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1902609884675969024', '1801458632269893632', '3', 'oJTwA7Z0t36IaXFIQdar02QBPPoY', 'oJTwA7Z0t36IaXFIQdar02QBPPoY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'XRYZE0', NULL, NULL, NULL, NULL, '2025-03-20 14:35:20', NULL, '2025-03-20 14:35:20', 0, 'LlQVHOdV9yu2fFjViZimVw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1902695184425160704', '1801458632269893632', '3', 'oJTwA7WYzuRghJ57F4jGODvaFi5c', 'oJTwA7WYzuRghJ57F4jGODvaFi5c', NULL, NULL, NULL, NULL, NULL, '1', '13172831665', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Z1U1B4', NULL, NULL, NULL, NULL, '2025-03-20 20:14:17', NULL, '2025-03-20 20:14:17', 0, '6ToRaxpgpP7IRjqlc63aQQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1903015348328861696', '1801458632269893632', '3', 'oJTwA7fhOja3LNkIvrn_SgCy7oRw', 'oJTwA7fhOja3LNkIvrn_SgCy7oRw', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'O9B9HG', NULL, NULL, NULL, NULL, '2025-03-21 17:26:30', NULL, '2025-03-21 17:26:30', 0, 'H4Vb0kjtqjbLgl5Cx5Kv/A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1903761407229431808', '1801458632269893632', '3', 'oJTwA7Vpp-3c9nhc9KQJNjtbQ060', 'oJTwA7Vpp-3c9nhc9KQJNjtbQ060', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CXLB19', NULL, NULL, NULL, NULL, '2025-03-23 18:51:04', NULL, '2025-03-23 18:51:04', 0, 'soVTi5bkdoCdCRJ1VNM7Mg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1903828614454054912', '1801458632269893632', '3', 'oJTwA7UCB6x-8oqr145z0OKfHWAE', 'oJTwA7UCB6x-8oqr145z0OKfHWAE', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'GM2V76', NULL, NULL, NULL, NULL, '2025-03-23 23:18:07', NULL, '2025-03-23 23:18:07', 0, 'GczmjmrZgwHQvRpmbimckw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1903929072870887424', '1801458632269893632', '3', 'oJTwA7ZexwdICUMlVoPRFtYDFIWc', 'oJTwA7ZexwdICUMlVoPRFtYDFIWc', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7DWS7X', NULL, NULL, NULL, NULL, '2025-03-24 05:57:18', NULL, '2025-03-24 05:57:18', 0, 'ENUC+Ip1MyMPkb80UWzBkA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1903951167357063168', '1801458632269893632', '3', 'oJTwA7blBtfjtGhx7EssQEiIkjms', 'oJTwA7blBtfjtGhx7EssQEiIkjms', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '91TBYA', NULL, NULL, NULL, NULL, '2025-03-24 07:25:06', NULL, '2025-03-24 07:25:06', 0, 'PnX17VnD3McGKZr3N1GoIg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904070772515803136', '1801458632269893632', '3', 'oJTwA7bMlVDkd3IeEFY_jsUD6jWQ', 'oJTwA7bMlVDkd3IeEFY_jsUD6jWQ', NULL, NULL, NULL, NULL, NULL, '1', '18369088789', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'U0MCP3', NULL, NULL, NULL, NULL, '2025-03-24 15:20:22', NULL, '2025-03-24 15:20:22', 0, 'ku32C01K7J6PJa5ZhGvENQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904436687090618368', '1801458632269893632', '3', 'oJTwA7QWqW2AZ8vrDQOYA2sZPbCM', 'oJTwA7QWqW2AZ8vrDQOYA2sZPbCM', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'OQP0ZW', NULL, NULL, NULL, NULL, '2025-03-25 15:34:23', NULL, '2025-03-25 15:34:23', 0, '13mKZ0goxEk+7tw9wOBIsA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904746152948011008', '1801458632269893632', '3', 'oJTwA7X6LMWTTxkMbWumyGD_o65s', 'oJTwA7X6LMWTTxkMbWumyGD_o65s', NULL, NULL, NULL, NULL, NULL, '1', '13138179966', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'RSJYMG', NULL, NULL, NULL, NULL, '2025-03-26 12:04:06', NULL, '2025-03-26 12:04:06', 0, 'YoRrWiUgELDfmk7OBJrF/A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904783794787979264', '1801458632269893632', '3', 'oJTwA7ZasmWQyFWajeo8WdqRgqGY', 'oJTwA7ZasmWQyFWajeo8WdqRgqGY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZRSG18', NULL, NULL, NULL, NULL, '2025-03-26 14:33:40', NULL, '2025-03-26 14:33:40', 0, 'FKicwH9blGFLNKwwXI9FxQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904790071148679168', '1801458632269893632', '3', 'oJTwA7coIURVQE1Fcb0gdxhU9Aqw', 'oJTwA7coIURVQE1Fcb0gdxhU9Aqw', NULL, NULL, NULL, NULL, NULL, '1', '13699790419', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'KJFVNT', NULL, NULL, NULL, NULL, '2025-03-26 14:58:36', NULL, '2025-03-26 14:58:36', 0, 'XY3WaoFPO6yJtyF1LxbD1Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904790208986091520', '1801458632269893632', '3', 'oJTwA7YdRn6GTupWsqD15toxMO48', 'oJTwA7YdRn6GTupWsqD15toxMO48', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2YIZJL', NULL, NULL, NULL, NULL, '2025-03-26 14:59:09', NULL, '2025-03-26 14:59:09', 0, 'tZ593wYu+uIn0THMaUCyJQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904790743604662272', '1801458632269893632', '3', 'oJTwA7cO-EkU9A1WYsdbQVLBrLNA', 'oJTwA7cO-EkU9A1WYsdbQVLBrLNA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3PP6YZ', NULL, NULL, NULL, NULL, '2025-03-26 15:01:17', NULL, '2025-03-26 15:01:17', 0, 'A1SHZBuV5HD8UeN0n1Jn6w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904805916537131008', '1801458632269893632', '3', 'oJTwA7epdA0qHgZWcAB4AV0pBlqc', 'oJTwA7epdA0qHgZWcAB4AV0pBlqc', NULL, NULL, NULL, NULL, NULL, '1', '13728621243', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'U3R9S5', NULL, NULL, NULL, NULL, '2025-03-26 16:01:34', NULL, '2025-03-26 16:01:34', 0, 'L39E3SNVC0dlP1p7SxUshA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904823926022868992', '1801458632269893632', '3', 'oJTwA7SA3NgC10cD897oryQbtWEo', 'oJTwA7SA3NgC10cD897oryQbtWEo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'XIQ2IZ', NULL, NULL, NULL, NULL, '2025-03-26 17:13:08', NULL, '2025-03-26 17:13:08', 0, '+pz6fRSE8FEFqQxd9znqJA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904825279080173568', '1801458632269893632', '3', 'oJTwA7fFZl-jQldQ20z5i7vMpN9A', 'oJTwA7fFZl-jQldQ20z5i7vMpN9A', NULL, NULL, NULL, NULL, NULL, '1', '18923769597', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3TI6PT', NULL, NULL, NULL, NULL, '2025-03-26 17:18:31', NULL, '2025-03-26 17:18:31', 0, 'DqaJr7MzER2JdU+8eKT1RQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1904877516204675072', '1801458632269893632', '3', 'oJTwA7TpAT9eXM34D9EisK3ZQt_c', 'oJTwA7TpAT9eXM34D9EisK3ZQt_c', NULL, NULL, NULL, NULL, NULL, '1', '15818799587', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'QM0GKD', NULL, NULL, NULL, NULL, '2025-03-26 20:46:05', NULL, '2025-03-26 20:46:05', 0, 'GPdqle9jXQOcUnkOD/JVwQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1905224228559523840', '1801458632269893632', '3', 'oJTwA7U_3WFy18rn-VK6Ti_S6eAI', 'oJTwA7U_3WFy18rn-VK6Ti_S6eAI', NULL, NULL, NULL, NULL, NULL, '1', '13138179966', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'J8E6XR', NULL, NULL, NULL, NULL, '2025-03-27 19:43:48', NULL, '2025-03-27 19:43:48', 0, 'vkv33qkza1zAlGL3r+ehEg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1905262975258857472', '1801458632269893632', '3', 'oJTwA7XrztvZZUWO254AFHWSJMfE', 'oJTwA7XrztvZZUWO254AFHWSJMfE', NULL, NULL, NULL, NULL, NULL, '1', '13480865139', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3J02YZ', NULL, NULL, NULL, NULL, '2025-03-27 22:17:46', NULL, '2025-03-27 22:17:46', 0, 'oKNZiu0bnNX6wXsbNV5a8Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906147688320208896', '1801458632269893632', '3', 'oJTwA7WSKaXhIJxmtf37_odEHm8M', 'oJTwA7WSKaXhIJxmtf37_odEHm8M', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'WD1FDV', NULL, NULL, NULL, NULL, '2025-03-30 08:53:18', NULL, '2025-03-30 08:53:18', 0, 'MwB13ybIQHPdlJFyObKYNQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906147804703756288', '1801458632269893632', '3', 'oJTwA7YFhE-xS7RiYLVV7PCO49c8', 'oJTwA7YFhE-xS7RiYLVV7PCO49c8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'YJAXT9', NULL, NULL, NULL, NULL, '2025-03-30 08:53:45', NULL, '2025-03-30 08:53:45', 0, 'vz8Qg3F9HyBf55QuvAhgCg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906277548468342784', '1801458632269893632', '3', 'oJTwA7Q54uVWqOaIahL1luyQQm9U', 'oJTwA7Q54uVWqOaIahL1luyQQm9U', NULL, NULL, NULL, NULL, NULL, '1', '15270614679', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZKDOOO', NULL, NULL, NULL, NULL, '2025-03-30 17:29:19', NULL, '2025-03-30 17:29:19', 0, 'fcX1O7Z3vTf7G8/vOL5f4A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906292008843087872', '1801458632269893632', '3', 'oJTwA7SekTcyKy4lz3lyEQmY-yP0', 'oJTwA7SekTcyKy4lz3lyEQmY-yP0', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NEKNI3', NULL, NULL, NULL, NULL, '2025-03-30 18:26:46', NULL, '2025-03-30 18:26:46', 0, 'KQRQtUR2vOIIs+UyWslhLg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906304715856154624', '1801458632269893632', '3', 'oJTwA7fwNpCM1iC1ZAyMCEHpO72c', 'oJTwA7fwNpCM1iC1ZAyMCEHpO72c', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'AR0VT5', NULL, NULL, NULL, NULL, '2025-03-30 19:17:16', NULL, '2025-03-30 19:17:16', 0, 'z73+zQGQOlTZadz0F5TKiA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906336550233444352', '1801458632269893632', '3', 'oJTwA7evrXATAQW9z401nkWAk9q4', 'oJTwA7evrXATAQW9z401nkWAk9q4', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'EF9PSF', NULL, NULL, NULL, NULL, '2025-03-30 21:23:46', NULL, '2025-03-30 21:23:46', 0, '659ihEThzOMNrieD25Oueg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906536332541431808', '1801458632269893632', '3', 'oJTwA7YHbkIXBINIpc5CUzSC1uac', 'oJTwA7YHbkIXBINIpc5CUzSC1uac', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HC9SUH', NULL, NULL, NULL, NULL, '2025-03-31 10:37:38', NULL, '2025-03-31 10:37:38', 0, '3Lu2La82Ajz0tCOgimTHrQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906541099435036672', '1801458632269893632', '3', 'oJTwA7fvCKI36gHt0IEpB6-TGw10', 'oJTwA7fvCKI36gHt0IEpB6-TGw10', NULL, NULL, NULL, NULL, NULL, '1', '15323803070', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NYZ685', NULL, NULL, NULL, NULL, '2025-03-31 10:56:34', NULL, '2025-03-31 10:56:34', 0, 'Tmk/Jkm00dp7u1GK3Lvwjw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906601661254733824', '1801458632269893632', '3', 'oJTwA7bwqs5N5Crmz7f58U4tthyA', 'oJTwA7bwqs5N5Crmz7f58U4tthyA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'K6A66Q', NULL, NULL, NULL, NULL, '2025-03-31 14:57:13', NULL, '2025-03-31 14:57:13', 0, 'CJUI2pw2GEgF2u0m3k0jxA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906602133575307264', '1801458632269893632', '3', 'oJTwA7aQspl3MfsRTVXq-iMTkvPE', 'oJTwA7aQspl3MfsRTVXq-iMTkvPE', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '5P0S5S', NULL, NULL, NULL, NULL, '2025-03-31 14:59:06', NULL, '2025-03-31 14:59:06', 0, 'yqIvS0DHpEPqVp9NhArGkQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906619568734998528', '1801458632269893632', '3', 'oJTwA7WB95l-ONklBZ4VWBt-ymyk', 'oJTwA7WB95l-ONklBZ4VWBt-ymyk', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZES0E0', NULL, NULL, NULL, NULL, '2025-03-31 16:08:23', NULL, '2025-03-31 16:08:23', 0, 'hV+AEyhu6dflokn++k7lEA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906926688915296256', '1801458632269893632', '3', 'oJTwA7U4NSBnWSja-vXX08dEoqBE', 'oJTwA7U4NSBnWSja-vXX08dEoqBE', NULL, NULL, NULL, NULL, NULL, '1', '18098994711', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'J6SZ4X', NULL, NULL, NULL, NULL, '2025-04-01 12:28:46', NULL, '2025-04-01 12:28:46', 0, 'XyaHjDDYDaB9WdYInVJlow==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906926839050407936', '1801458632269893632', '3', 'oJTwA7eSCaCxdxkYd2K0AtlrNBmg', 'oJTwA7eSCaCxdxkYd2K0AtlrNBmg', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'DE7GY8', NULL, NULL, NULL, NULL, '2025-04-01 12:29:22', NULL, '2025-04-01 12:29:22', 0, 'yfgrT6KZ97/2kwf0heqO6w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906949417555070976', '1801458632269893632', '3', 'oJTwA7Snrh3XqqPAXNDpcojmynCA', 'oJTwA7Snrh3XqqPAXNDpcojmynCA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'VRODO5', NULL, NULL, NULL, NULL, '2025-04-01 13:59:05', NULL, '2025-04-01 13:59:05', 0, '4xsmQbzPFhvbJfczu2kC7w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1906958724497543168', '1801458632269893632', '3', 'oJTwA7dYYQmV1xhTTTnyawBgDXzk', 'oJTwA7dYYQmV1xhTTTnyawBgDXzk', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0B6RHL', NULL, NULL, NULL, NULL, '2025-04-01 14:36:04', NULL, '2025-04-01 14:36:04', 0, 'TQVClQA5opCx21HsfiCLUA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907016176089829376', '1801458632269893632', '3', 'oJTwA7c2SPQl0parwM5lweWE05WE', 'oJTwA7c2SPQl0parwM5lweWE05WE', NULL, NULL, NULL, NULL, NULL, '1', '15670001266', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'F0MHAJ', NULL, NULL, NULL, NULL, '2025-04-01 18:24:21', NULL, '2025-04-01 18:24:21', 0, 'kmfo8b+mwyUXkiZ4cfV40A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907305229372755968', '1801458632269893632', '3', 'oJTwA7RselJrlpUCwipnrot198PE', 'oJTwA7RselJrlpUCwipnrot198PE', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7CVCNI', NULL, NULL, NULL, NULL, '2025-04-02 13:32:57', NULL, '2025-04-02 13:32:57', 0, 'PM7vbI88Fy1weDnjXg2H2w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907360299464396800', '1801458632269893632', '3', 'oJTwA7cdem0EfTTAr1KQCn0-bveY', 'oJTwA7cdem0EfTTAr1KQCn0-bveY', NULL, NULL, NULL, NULL, NULL, '1', '13926227431', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'G1Z7RZ', NULL, NULL, NULL, NULL, '2025-04-02 17:11:47', NULL, '2025-04-02 17:11:47', 0, '65aFidxpuIuRTcRryio0zw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907372524501602304', '1801458632269893632', '3', 'oJTwA7YeWJnAc09OckB5a9wqyBFM', 'oJTwA7YeWJnAc09OckB5a9wqyBFM', NULL, NULL, NULL, NULL, NULL, '1', '18023147621', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZUQ5ZO', NULL, NULL, NULL, NULL, '2025-04-02 18:00:21', NULL, '2025-04-02 18:00:21', 0, 'M0sy1hiznDLDDZgTFEmnvA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907426973576204288', '1801458632269893632', '3', 'oJTwA7fWdHXvE-ANBNgOas3uSYIw', 'oJTwA7fWdHXvE-ANBNgOas3uSYIw', NULL, NULL, NULL, NULL, NULL, '1', '19529822260', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CQU9PD', NULL, NULL, NULL, NULL, '2025-04-02 21:36:43', NULL, '2025-04-02 21:36:43', 0, 'eq9LyMrqCIXoA1XDfsMVBQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907428498784522240', '1801458632269893632', '3', 'oJTwA7WKT-dhbTNFK1KWgI9I1hFg', 'oJTwA7WKT-dhbTNFK1KWgI9I1hFg', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SKCPVU', NULL, NULL, NULL, NULL, '2025-04-02 21:42:47', NULL, '2025-04-02 21:42:47', 0, '+S8BUGQjzbKHhD3yS/6w1g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907630072815292416', '1801458632269893632', '3', 'oJTwA7e_UgClVgTrdMyuPRdxkAyk', 'oJTwA7e_UgClVgTrdMyuPRdxkAyk', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'RYPQ0P', NULL, NULL, NULL, NULL, '2025-04-03 11:03:46', NULL, '2025-04-03 11:03:46', 0, 'BQrSdUU0Kzsrf6frGD/KuQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907683129217388544', '1801458632269893632', '3', 'oJTwA7S5QO2sESwq18H_oiwWEKWE', 'oJTwA7S5QO2sESwq18H_oiwWEKWE', NULL, NULL, NULL, NULL, NULL, '1', '18942523898', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SPGIVF', NULL, NULL, NULL, NULL, '2025-04-03 14:34:35', NULL, '2025-04-03 14:34:35', 0, 't5YEQY2Otvv3ry9W8vuA1g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907694822777556992', '1801458632269893632', '3', 'oJTwA7dNge0e8wahoGyTj3ON_hEo', 'oJTwA7dNge0e8wahoGyTj3ON_hEo', NULL, NULL, NULL, NULL, NULL, '1', '15105883709', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'RQ2891', NULL, NULL, NULL, NULL, '2025-04-03 15:21:03', NULL, '2025-04-03 15:21:03', 0, 'myePE4zj2m9sVgXvfKcivQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907708062509174784', '1801458632269893632', '3', 'oJTwA7ahLR24ZaACoS6Nq9yNiPK0', 'oJTwA7ahLR24ZaACoS6Nq9yNiPK0', NULL, NULL, NULL, NULL, NULL, '1', '13724351743', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'OLI1FA', NULL, NULL, NULL, NULL, '2025-04-03 16:13:40', NULL, '2025-04-03 16:13:40', 0, 'OpEyINxOkxgt5fUCRbCCbQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907776509444231168', '1801458632269893632', '3', 'oJTwA7SPY3_1BsqYnXfKLaBPYcFc', 'oJTwA7SPY3_1BsqYnXfKLaBPYcFc', NULL, NULL, NULL, NULL, NULL, '1', '17373101142', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NEPZD3', NULL, NULL, NULL, NULL, '2025-04-03 20:45:39', NULL, '2025-04-03 20:45:39', 0, 'PIiy5rfITUbhYhCC4vBC6Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907797374340829184', '1801458632269893632', '3', 'oJTwA7VMwaW_OiurfIPKmO_XRqgg', 'oJTwA7VMwaW_OiurfIPKmO_XRqgg', NULL, NULL, NULL, NULL, NULL, '1', '18665667603', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'IC7JR4', NULL, NULL, NULL, NULL, '2025-04-03 22:08:33', NULL, '2025-04-03 22:08:33', 0, 'v4xy754IMGM6c0kdhj7ftw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1907959910172332032', '1801458632269893632', '3', 'oJTwA7SxH3gA9hmySQsvh5wqwmeQ', 'oJTwA7SxH3gA9hmySQsvh5wqwmeQ', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'XKOUNH', NULL, NULL, NULL, NULL, '2025-04-04 08:54:25', NULL, '2025-04-04 08:54:25', 0, 'XY5HfkMXGbA1T6MMrCsC2Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1908478058793406464', '1801458632269893632', '3', 'oJTwA7VWQV7ddSuTIbE55D8BaFrk', 'oJTwA7VWQV7ddSuTIbE55D8BaFrk', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'DM8D8X', NULL, NULL, NULL, NULL, '2025-04-05 19:13:21', NULL, '2025-04-05 19:13:21', 0, '0EaHZvNTlfUitRN5Uio2dQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1908772661740310528', '1801458632269893632', '3', 'oJTwA7TVmUsGFObHlOBJrNG2Kh4I', 'oJTwA7TVmUsGFObHlOBJrNG2Kh4I', NULL, NULL, NULL, NULL, NULL, '1', '13826551143', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'RX0FRA', NULL, NULL, NULL, NULL, '2025-04-06 14:44:00', NULL, '2025-04-06 14:44:00', 0, 'hngDlf6A13Mu5ScfJ6tPXA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1908776061483749376', '1801458632269893632', '3', 'oJTwA7VdZnQhQNJVdzl5oH0l2n_E', 'oJTwA7VdZnQhQNJVdzl5oH0l2n_E', NULL, NULL, NULL, NULL, NULL, '1', '18823768947', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '96SLTA', NULL, NULL, NULL, NULL, '2025-04-06 14:57:31', NULL, '2025-04-06 14:57:31', 0, 'eVsgck0iyrsfsFMGKCbpZw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909078623009247232', '1801458632269893632', '3', 'oJTwA7cIZo6DdzssKWIaGNH3vEb0', 'oJTwA7cIZo6DdzssKWIaGNH3vEb0', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'QXP9TG', NULL, NULL, NULL, NULL, '2025-04-07 10:59:47', NULL, '2025-04-07 10:59:47', 0, 'v0qOr+3/ex+eHd1Nsc8QXA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909163833335877632', '1801458632269893632', '3', 'oJTwA7cMb2Y6KJ4qwiOQoO3ygyWQ', 'oJTwA7cMb2Y6KJ4qwiOQoO3ygyWQ', NULL, NULL, NULL, NULL, NULL, '1', '13530647383', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2WFSEW', NULL, NULL, NULL, NULL, '2025-04-07 16:38:23', NULL, '2025-04-07 16:38:23', 0, '/hsyyvG8IhClZgcpt+Y+Vw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909174972467777536', '1801458632269893632', '3', 'oJTwA7ezVlMrwMAUeTuB6X2Hc4ZQ', 'oJTwA7ezVlMrwMAUeTuB6X2Hc4ZQ', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Z61FEM', NULL, NULL, NULL, NULL, '2025-04-07 17:22:38', NULL, '2025-04-07 17:22:38', 0, 'SALkKjRShFI02XLonXLwyQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909447249335816192', '1801458632269893632', '3', 'oJTwA7ego2l2xUPhhx_Kyh-agP8E', 'oJTwA7ego2l2xUPhhx_Kyh-agP8E', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'UKA9KT', NULL, NULL, NULL, NULL, '2025-04-08 11:24:34', NULL, '2025-04-08 11:24:34', 0, '3y8YdKaTcEE8RcoGt/+V1A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909447394106413056', '1801458632269893632', '3', 'oJTwA7e4YF01kOCahvI3JArJBJm8', 'oJTwA7e4YF01kOCahvI3JArJBJm8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'LC461O', NULL, NULL, NULL, NULL, '2025-04-08 11:25:09', NULL, '2025-04-08 11:25:09', 0, 'XfDDIgtLILdDcErD/pDM6Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909451317710032896', '1801458632269893632', '3', 'oJTwA7eWRChMHcSm_6rGpxO8CvKU', 'oJTwA7eWRChMHcSm_6rGpxO8CvKU', NULL, NULL, NULL, NULL, NULL, '1', '13066930041', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'EHJKKS', NULL, NULL, NULL, NULL, '2025-04-08 11:40:44', NULL, '2025-04-08 11:40:44', 0, 'F4v4ZbEcZlclhnK7EutWUA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909474166365622272', '1801458632269893632', '3', 'oJTwA7ZEznyrwIi5ISu78N2y1inY', 'oJTwA7ZEznyrwIi5ISu78N2y1inY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'WE0FP1', NULL, NULL, NULL, NULL, '2025-04-08 13:11:32', NULL, '2025-04-08 13:11:32', 0, 'XHeZIVu0dSniGfNtr74CzQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909474289623633920', '1801458632269893632', '3', 'oJTwA7YC5_eg0Nlzc468tTOarlvM', 'oJTwA7YC5_eg0Nlzc468tTOarlvM', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PIL34H', NULL, NULL, NULL, NULL, '2025-04-08 13:12:01', NULL, '2025-04-08 13:12:01', 0, '2QTbtuS90UORqVBdbDiOWQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909474735109050368', '1801458632269893632', '3', 'oJTwA7UFE6WRmdIC5KwuDO5xgPdo', 'oJTwA7UFE6WRmdIC5KwuDO5xgPdo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SYLDW3', NULL, NULL, NULL, NULL, '2025-04-08 13:13:47', NULL, '2025-04-08 13:13:47', 0, 'pY00GdLujrv29O0lFUnlcQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909519067128139776', '1801458632269893632', '3', 'oJTwA7dj5r2mnI0jZ4rKS2DekZK4', 'oJTwA7dj5r2mnI0jZ4rKS2DekZK4', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'OY22KC', NULL, NULL, NULL, NULL, '2025-04-08 16:09:57', NULL, '2025-04-08 16:09:57', 0, '0ZRIv6g9UyzZlJ8dtATctg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909524342677573632', '1801458632269893632', '3', 'oJTwA7RaA9lCJFlCoyVtEPotBVIE', 'oJTwA7RaA9lCJFlCoyVtEPotBVIE', NULL, NULL, NULL, NULL, NULL, '1', '15827649102', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'F29CV5', NULL, NULL, NULL, NULL, '2025-04-08 16:30:55', NULL, '2025-04-08 16:30:55', 0, 'zek5ktOsbprmdzbGRBsPxQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909533328743206912', '1801458632269893632', '3', 'oJTwA7a_Z50RIOXfrqV6ciZW6rtg', 'oJTwA7a_Z50RIOXfrqV6ciZW6rtg', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'X35PCU', NULL, NULL, NULL, NULL, '2025-04-08 17:06:37', NULL, '2025-04-08 17:06:37', 0, 'gTxZwiJytmo/4ZOUyvOq8Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909804815215628288', '1801458632269893632', '3', 'oJTwA7brhXvm4SIbq6RllK-eGWb4', 'oJTwA7brhXvm4SIbq6RllK-eGWb4', NULL, NULL, NULL, NULL, NULL, '1', '13684938653', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PM8JNZ', NULL, NULL, NULL, NULL, '2025-04-09 11:05:25', NULL, '2025-04-09 11:05:25', 0, '6uQLTA/uroj97gw0ndkymQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909804877870141440', '1801458632269893632', '3', 'oJTwA7QNJNNTNlxmkqaMBsthFtYs', 'oJTwA7QNJNNTNlxmkqaMBsthFtYs', NULL, NULL, NULL, NULL, NULL, '1', '13028843357', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZG8IQ4', NULL, NULL, NULL, NULL, '2025-04-09 11:05:40', NULL, '2025-04-09 11:05:40', 0, 'eXlcEZXxKSnzkITy3CSOkw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909804879849852928', '1801458632269893632', '3', 'oJTwA7TK1zYQZMJEVuTh0uOv8cwo', 'oJTwA7TK1zYQZMJEVuTh0uOv8cwo', NULL, NULL, NULL, NULL, NULL, '1', '13828892610', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'MGR7DP', NULL, NULL, NULL, NULL, '2025-04-09 11:05:40', NULL, '2025-04-09 11:05:40', 0, '+0mQAKpMjywUgoCW7aklLw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909804917976076288', '1801458632269893632', '3', 'oJTwA7XW5QDt2vKljne2YfVWD0Sk', 'oJTwA7XW5QDt2vKljne2YfVWD0Sk', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'WI11Z6', NULL, NULL, NULL, NULL, '2025-04-09 11:05:49', NULL, '2025-04-09 11:05:49', 0, 'w6q8J54bMhdUHYRICqRjoA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909804938591080448', '1801458632269893632', '3', 'oJTwA7bsHGXyQ5R6cvGvb-Xfldko', 'oJTwA7bsHGXyQ5R6cvGvb-Xfldko', NULL, NULL, NULL, NULL, NULL, '1', '13352915361', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ONSOON', NULL, NULL, NULL, NULL, '2025-04-09 11:05:54', NULL, '2025-04-09 11:05:54', 0, 'goJGG92F+UrF/C2AuZSyIA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909804951098494976', '1801458632269893632', '3', 'oJTwA7bTouRnpCeqPDVVt0O5IHE8', 'oJTwA7bTouRnpCeqPDVVt0O5IHE8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CEK6X1', NULL, NULL, NULL, NULL, '2025-04-09 11:05:57', NULL, '2025-04-09 11:05:57', 0, 'vfy6HYQvwiTHpPGB7fS31A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909805212437188608', '1801458632269893632', '3', 'oJTwA7fF_Nr408RX0pVmaw7wxK3U', 'oJTwA7fF_Nr408RX0pVmaw7wxK3U', NULL, NULL, NULL, NULL, NULL, '1', '14776166891', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HGAZSA', NULL, NULL, NULL, NULL, '2025-04-09 11:06:59', NULL, '2025-04-09 11:06:59', 0, 'tj61aq6vwWPOfkaR/qhUKw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909805218229522432', '1801458632269893632', '3', 'oJTwA7XOdHyLNEOmV6Uy_lVcQrlM', 'oJTwA7XOdHyLNEOmV6Uy_lVcQrlM', NULL, NULL, NULL, NULL, NULL, '1', '15013556076', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'UX510X', NULL, NULL, NULL, NULL, '2025-04-09 11:07:01', NULL, '2025-04-09 11:07:01', 0, 'zBWcudQSxcArinqxsA7wNg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909805251985281024', '1801458632269893632', '3', 'oJTwA7YfOZlTCZBfZCEGRtGD00ZQ', 'oJTwA7YfOZlTCZBfZCEGRtGD00ZQ', NULL, NULL, NULL, '有生命力的段玉婷', NULL, '1', '18942254445', NULL, 'https://yue17-miniapp.oss-cn-beijing.aliyuncs.com/admin/67f5e478e4b02a617d5edcc3.jpg', '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'MFH02B', NULL, NULL, NULL, NULL, '2025-04-09 11:07:09', NULL, '2025-04-09 11:07:09', 0, 'n1qVnIDzdi9/AZz3dMs5HQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909805365927743488', '1801458632269893632', '3', 'oJTwA7Vd9FMNllLqJDxsAGqXuIjc', 'oJTwA7Vd9FMNllLqJDxsAGqXuIjc', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'W4DI6F', NULL, NULL, NULL, NULL, '2025-04-09 11:07:36', NULL, '2025-04-09 11:07:36', 0, 'EtHP6hsgYZvLN6s8mQktUg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909866696504643584', '1801458632269893632', '3', 'oJTwA7WGqcmeSHjuCRMo8-h1l96A', 'oJTwA7WGqcmeSHjuCRMo8-h1l96A', NULL, NULL, NULL, NULL, NULL, '1', '19902366868', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'OLWGZC', NULL, NULL, NULL, NULL, '2025-04-09 15:11:18', NULL, '2025-04-09 15:11:18', 0, 'EmYrhAhDx04FuD19eeSW4g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909908407008235520', '1801458632269893632', '3', 'oJTwA7cQYpuMkiKJQQOKo593snII', 'oJTwA7cQYpuMkiKJQQOKo593snII', NULL, NULL, NULL, NULL, NULL, '1', '13530656703', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3AMLIP', NULL, NULL, NULL, NULL, '2025-04-09 17:57:03', NULL, '2025-04-09 17:57:03', 0, 'OUChrWGjYaivgqDGNRFIWg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909918057489371136', '1801458632269893632', '3', 'oJTwA7SIXlEyV-nL9cZnmOX0gkx8', 'oJTwA7SIXlEyV-nL9cZnmOX0gkx8', NULL, NULL, NULL, NULL, NULL, '1', '13960603606', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '6DHLNA', NULL, NULL, NULL, NULL, '2025-04-09 18:35:24', NULL, '2025-04-09 18:35:24', 0, '0nndgVbrstlHT0kWN1xEVQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1909964167952076800', '1801458632269893632', '3', 'oJTwA7f-uSm-Cu13qBISUnla1Avc', 'oJTwA7f-uSm-Cu13qBISUnla1Avc', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7HUR8O', NULL, NULL, NULL, NULL, '2025-04-09 21:38:37', NULL, '2025-04-09 21:38:37', 0, 'wzcmgzvcf/X9HC066wMhtA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910222440819200000', '1801458632269893632', '3', 'oJTwA7V7NsG9bi7YjFSWGRy1utls', 'oJTwA7V7NsG9bi7YjFSWGRy1utls', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'AGDG2F', NULL, NULL, NULL, NULL, '2025-04-10 14:44:54', NULL, '2025-04-10 14:44:54', 0, 'YpjxMcmLm/SkzC2IqYnnmA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910547740488765440', '1801458632269893632', '3', 'oJTwA7VBWUimpbMVtEh2yJn9_oJk', 'oJTwA7VBWUimpbMVtEh2yJn9_oJk', NULL, NULL, NULL, NULL, NULL, '1', '13781581784', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'VO5WA3', NULL, NULL, NULL, NULL, '2025-04-11 12:17:32', NULL, '2025-04-11 12:17:32', 0, 'XpYMHYwHGNb3EvVriCvg/w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910596915809095680', '1801458632269893632', '3', 'oJTwA7X9EoS-om0c7-XdBZiYRbu8', 'oJTwA7X9EoS-om0c7-XdBZiYRbu8', NULL, NULL, NULL, NULL, NULL, '1', '18566714268', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8ZLBIS', NULL, NULL, NULL, NULL, '2025-04-11 15:32:56', NULL, '2025-04-11 15:32:56', 0, 'PDBB6Q32XI9PLDdVnkSO5g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910600419571273728', '1801458632269893632', '3', 'oJTwA7baC04G2N8E9Ojx6fn7oxKU', 'oJTwA7baC04G2N8E9Ojx6fn7oxKU', NULL, NULL, NULL, NULL, NULL, '1', '13691983026', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ONY9I7', NULL, NULL, NULL, NULL, '2025-04-11 15:46:51', NULL, '2025-04-11 15:46:51', 0, 'Dow4lBoskJyjBUPTnuIGUQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910621275886325760', '1801458632269893632', '3', 'oJTwA7Z2iSSsGrldhon34rnOEOYk', 'oJTwA7Z2iSSsGrldhon34rnOEOYk', NULL, NULL, NULL, NULL, NULL, '1', '19830803919', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'LVO8MM', NULL, NULL, NULL, NULL, '2025-04-11 17:09:44', NULL, '2025-04-11 17:09:44', 0, 'tEOdm7QXpa2DdKHp/Cds9Q==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910634404707962880', '1801458632269893632', '3', 'oJTwA7WjjnZlcttcC-RnPv4NCNEc', 'oJTwA7WjjnZlcttcC-RnPv4NCNEc', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FU9UW3', NULL, NULL, NULL, NULL, '2025-04-11 18:01:54', NULL, '2025-04-11 18:01:54', 0, 'XyDRcdVgYUrkgrvUd/4JZg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910910619125682176', '1801458632269893632', '3', 'oJTwA7csAbMUzcXuK1o5swjVDJak', 'oJTwA7csAbMUzcXuK1o5swjVDJak', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NTS5WQ', NULL, NULL, NULL, NULL, '2025-04-12 12:19:29', NULL, '2025-04-12 12:19:29', 0, '3A2RhuWBuVB2frUApIrNmw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910919163946340352', '1801458632269893632', '3', 'oJTwA7eWO_6surqdby7aNco-1IUo', 'oJTwA7eWO_6surqdby7aNco-1IUo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZLK8GV', NULL, NULL, NULL, NULL, '2025-04-12 12:53:26', NULL, '2025-04-12 12:53:26', 0, 'Vnifioc4Mdcy9E80cUzaFQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910952205746311168', '1801458632269893632', '3', 'oJTwA7VgSSEX2Yy0u510KEDpCHPY', 'oJTwA7VgSSEX2Yy0u510KEDpCHPY', NULL, NULL, NULL, NULL, NULL, '1', '18926043018', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'LY1UT2', NULL, NULL, NULL, NULL, '2025-04-12 15:04:44', NULL, '2025-04-12 15:04:44', 0, '37DYBhsPb2UGW2QnHAWqRQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1910968171649699840', '1801458632269893632', '3', 'oJTwA7dnMXDq1fgvNpSMSfr1mhpo', 'oJTwA7dnMXDq1fgvNpSMSfr1mhpo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PCCEMU', NULL, NULL, NULL, NULL, '2025-04-12 16:08:10', NULL, '2025-04-12 16:08:10', 0, 'Et0ZGntYTryu7FjgodG+4A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911052376706519040', '1801458632269893632', '3', 'oJTwA7Q8qvN8GWNJd2i2iVL5px-4', 'oJTwA7Q8qvN8GWNJd2i2iVL5px-4', NULL, NULL, NULL, 'oJTwA7Q8qvN8GWNJd2i2iVL5px-4', NULL, '1', '18825030788', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'VXMD22', NULL, NULL, NULL, NULL, '2025-04-12 21:42:46', NULL, '2025-04-12 21:42:46', 0, 'Tp5e4dITKVOykOHuHtwxdQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911368631862824960', '1801458632269893632', '3', 'oJTwA7W00faaMwBAtlw86t-AwKJI', 'oJTwA7W00faaMwBAtlw86t-AwKJI', NULL, NULL, NULL, NULL, NULL, '1', '13427456402', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'QCAJP5', NULL, NULL, NULL, NULL, '2025-04-13 18:39:28', NULL, '2025-04-13 18:39:28', 0, 'tC+s/fH7YMmmI9oI3USAlg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911444799437803520', '1801458632269893632', '3', 'oJTwA7eR6H2K0vzMZmnkRdkI5mA4', 'oJTwA7eR6H2K0vzMZmnkRdkI5mA4', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'WRQHWU', NULL, NULL, NULL, NULL, '2025-04-13 23:42:07', NULL, '2025-04-13 23:42:07', 0, '/UCuOoM4qnWXvzPPy6xZeA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911686789370875904', '1801458632269893632', '3', 'oJTwA7cTwSWF8-0s1BljLNPhpdYo', 'oJTwA7cTwSWF8-0s1BljLNPhpdYo', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'UPAB0N', NULL, NULL, NULL, NULL, '2025-04-14 15:43:42', NULL, '2025-04-14 15:43:42', 0, '0cp8+HkuimO2Qlwc68xLkA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911734388350128128', '1801458632269893632', '3', 'oJTwA7U1Oh7g7UelrsUbmjWUn4KY', 'oJTwA7U1Oh7g7UelrsUbmjWUn4KY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '9M08UX', NULL, NULL, NULL, NULL, '2025-04-14 18:52:51', NULL, '2025-04-14 18:52:51', 0, '06FNaxQ76F9Mstx5dobVXQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911807113273937920', '1801458632269893632', '3', 'oJTwA7R2v005v4NyfZ6j2mw80JxA', 'oJTwA7R2v005v4NyfZ6j2mw80JxA', NULL, NULL, NULL, NULL, NULL, '1', '13877759531', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0GR0PD', NULL, NULL, NULL, NULL, '2025-04-14 23:41:50', NULL, '2025-04-14 23:41:50', 0, 's+Rj3bvqrT5Q7AdtVINQNg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911819501209522176', '1801458632269893632', '3', 'oJTwA7TtWTqWsf46-k8CtJt91we4', 'oJTwA7TtWTqWsf46-k8CtJt91we4', NULL, NULL, NULL, NULL, NULL, '1', '13418685116', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0LAME2', NULL, NULL, NULL, NULL, '2025-04-15 00:31:03', NULL, '2025-04-15 00:31:03', 0, 'E1lEGW/NIMulSJeA36BPLQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1911820044334141440', '1801458632269893632', '3', 'oJTwA7V46uwHdJKeotjMa3NUYMsM', 'oJTwA7V46uwHdJKeotjMa3NUYMsM', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CRIZAH', NULL, NULL, NULL, NULL, '2025-04-15 00:33:13', NULL, '2025-04-15 00:33:13', 0, 'YxJ/499oxG/lu+ZteokPSg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912043355425083392', '1801458632269893632', '3', 'oJTwA7TaIZFdhWJK0hJCVGOdl5Dc', 'oJTwA7TaIZFdhWJK0hJCVGOdl5Dc', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NP4KB9', NULL, NULL, NULL, NULL, '2025-04-15 15:20:34', NULL, '2025-04-15 15:20:34', 0, 'uSDRVgaIrZw5G6ObeCWY9w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912054264604266496', '1801458632269893632', '3', 'oJTwA7b_srlkpgSyIWpUxWnwB8lQ', 'oJTwA7b_srlkpgSyIWpUxWnwB8lQ', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'QJRAU3', NULL, NULL, NULL, NULL, '2025-04-15 16:03:55', NULL, '2025-04-15 16:03:55', 0, 'ltXH3NGaL1D2UnGx6MVKwQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912084686520324096', '1801458632269893632', '3', 'oJTwA7W4zPAWBHFgLcevxjVhIy5M', 'oJTwA7W4zPAWBHFgLcevxjVhIy5M', NULL, NULL, NULL, NULL, NULL, '1', '13557131411', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'AVKXGW', NULL, NULL, NULL, NULL, '2025-04-15 18:04:48', NULL, '2025-04-15 18:04:48', 0, 'UAk7hrPR7kORBCOMOt6Vxw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912085388420321280', '1801458632269893632', '3', 'oJTwA7doyuV0Qfgc4hEdn9zwXgGY', 'oJTwA7doyuV0Qfgc4hEdn9zwXgGY', NULL, NULL, NULL, NULL, NULL, '1', '17873087363', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZW7Y4L', NULL, NULL, NULL, NULL, '2025-04-15 18:07:36', NULL, '2025-04-15 18:07:36', 0, 'D6XEipZfjBcYcOtnCs9/ww==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912087531038576640', '1801458632269893632', '3', 'oJTwA7f3nx9wfXnLtJ-hnezvo77M', 'oJTwA7f3nx9wfXnLtJ-hnezvo77M', NULL, NULL, NULL, NULL, NULL, '1', '13087995992', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1U9EEY', NULL, NULL, NULL, NULL, '2025-04-15 18:16:07', NULL, '2025-04-15 18:16:07', 0, '9JSWKlKHweEAg/jHL+LnNg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912101339849166848', '1801458632269893632', '3', 'oJTwA7UnW-LGadijy9yC0xen_kdg', 'oJTwA7UnW-LGadijy9yC0xen_kdg', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SOUEG8', NULL, NULL, NULL, NULL, '2025-04-15 19:10:59', NULL, '2025-04-15 19:10:59', 0, 'IJkdGG75wraaFHcCwQ97uA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912157406918479872', '1801458632269893632', '3', 'oJTwA7VgFcvQbuqbQulnwetDfVJI', 'oJTwA7VgFcvQbuqbQulnwetDfVJI', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TS5Y9G', NULL, NULL, NULL, NULL, '2025-04-15 22:53:46', NULL, '2025-04-15 22:53:46', 0, 'FtHkd6Uy3UmSS9SojSJwxg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912363050325905408', '1801458632269893632', '3', 'oJTwA7ZBt5BAdnx_K3JVAFCjzqSw', 'oJTwA7ZBt5BAdnx_K3JVAFCjzqSw', NULL, NULL, NULL, NULL, NULL, '1', '13517157456', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FM7N6A', NULL, NULL, NULL, NULL, '2025-04-16 12:30:55', NULL, '2025-04-16 12:30:55', 0, 'QhccbJgsKddyk0WEXNBnYA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912366043649675264', '1801458632269893632', '3', 'oJTwA7SS_eRQi9GT-o_s7LHzNBB8', 'oJTwA7SS_eRQi9GT-o_s7LHzNBB8', NULL, NULL, NULL, NULL, NULL, '1', '13597795666', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'HTMOZA', NULL, NULL, NULL, NULL, '2025-04-16 12:42:49', NULL, '2025-04-16 12:42:49', 0, 'SiC2023+EHnodbFIcHb1AA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912392054328135680', '1801458632269893632', '3', 'oJTwA7VNqW1BTzOPqf7Rs1SA1r4c', 'oJTwA7VNqW1BTzOPqf7Rs1SA1r4c', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'NA81IB', NULL, NULL, NULL, NULL, '2025-04-16 14:26:10', NULL, '2025-04-16 14:26:10', 0, '9F0qATED1bKvdtw8BxN1Ww==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912488306189406208', '1801458632269893632', '3', 'oJTwA7QbOMiMyTZpgK9kxP37UGu8', 'oJTwA7QbOMiMyTZpgK9kxP37UGu8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FKU71A', NULL, NULL, NULL, NULL, '2025-04-16 20:48:39', NULL, '2025-04-16 20:48:39', 0, 'XE8qqIZW9qilbsvaRsbP/A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912527780780838912', '1801458632269893632', '3', 'oJTwA7SoILkOaEIP0bDGNtbvJ4x0', 'oJTwA7SoILkOaEIP0bDGNtbvJ4x0', NULL, NULL, NULL, NULL, NULL, '1', '15285422601', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '6DX7R6', NULL, NULL, NULL, NULL, '2025-04-16 23:25:30', NULL, '2025-04-16 23:25:30', 0, 'dRa3dgCXto41jcaZqNgpQg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912705713004744704', '1801458632269893632', '3', 'oJTwA7SDBuGXN_PqznxFNoDRtMUU', 'oJTwA7SDBuGXN_PqznxFNoDRtMUU', NULL, NULL, NULL, NULL, NULL, '1', '19206022344', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SKZ89X', NULL, NULL, NULL, NULL, '2025-04-17 11:12:33', NULL, '2025-04-17 11:12:33', 0, 'uEdYoKkUuUABHHDbhCefDQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912709869585174528', '1801458632269893632', '3', 'oJTwA7cHXymX0kgKw6uL8mBGHwMU', 'oJTwA7cHXymX0kgKw6uL8mBGHwMU', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'GQJI84', NULL, NULL, NULL, NULL, '2025-04-17 11:29:04', NULL, '2025-04-17 11:29:04', 0, 'n00KNAk1NowEoBoK3N5pDA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912720395597910016', '1801458632269893632', '3', 'oJTwA7ZGq_hwjzACk7MesYCP99tg', 'oJTwA7ZGq_hwjzACk7MesYCP99tg', NULL, NULL, NULL, NULL, NULL, '1', '18877258977', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '73W01H', NULL, NULL, NULL, NULL, '2025-04-17 12:10:53', NULL, '2025-04-17 12:10:53', 0, '+GHDIWY10G6aVPLgKCMSOQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912722811370213376', '1801458632269893632', '3', 'oJTwA7TrHpcAIU4xhR9zBMdrTpK0', 'oJTwA7TrHpcAIU4xhR9zBMdrTpK0', NULL, NULL, NULL, NULL, NULL, '1', '18579313413', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'KZZB0S', NULL, NULL, NULL, NULL, '2025-04-17 12:20:29', NULL, '2025-04-17 12:20:29', 0, 'MWQbpeetEJpt2bpVYvxSEQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912735573836173312', '1801458632269893632', '3', 'oJTwA7Q955lUkkANthA2MaY-7tjA', 'oJTwA7Q955lUkkANthA2MaY-7tjA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TEH6FB', NULL, NULL, NULL, NULL, '2025-04-17 13:11:12', NULL, '2025-04-17 13:11:12', 0, 'HW+FhdbY5Ym8wXXTA2gZcw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912759361319604224', '1801458632269893632', '3', 'oJTwA7UAo7O8AHc5sDyWAvKysPt8', 'oJTwA7UAo7O8AHc5sDyWAvKysPt8', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8SQKFG', NULL, NULL, NULL, NULL, '2025-04-17 14:45:43', NULL, '2025-04-17 14:45:43', 0, 'MqunSXjua9JTSy18C2xa2w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912771498163179520', '1801458632269893632', '3', 'oJTwA7WAuyA4Kf1SIET410vRgrXU', 'oJTwA7WAuyA4Kf1SIET410vRgrXU', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'B20HEC', NULL, NULL, NULL, NULL, '2025-04-17 15:33:57', NULL, '2025-04-17 15:33:57', 0, 'MifF66lE6IzJHc7USE8tmA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912868286215884800', '1801458632269893632', '3', 'oJTwA7ehcueQrjXu_LFyBprZOh4A', 'oJTwA7ehcueQrjXu_LFyBprZOh4A', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'BTDOAI', NULL, NULL, NULL, NULL, '2025-04-17 21:58:33', NULL, '2025-04-17 21:58:33', 0, 'pU0AIiYEhm+GAdt4RVhNqA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1912870259434590208', '1801458632269893632', '3', 'oJTwA7c1rlxMK508SO5zHPS0EaPA', 'oJTwA7c1rlxMK508SO5zHPS0EaPA', NULL, NULL, NULL, NULL, NULL, '1', '18005091298', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'N17OQI', NULL, NULL, NULL, NULL, '2025-04-17 22:06:23', NULL, '2025-04-17 22:06:23', 0, 'TGt7XMgQMQvcxVDo9vFFpQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1913796215951200256', '1801458632269893632', '3', 'oJTwA7QhgvJA24xTR1xxWKn8uy1U', 'oJTwA7QhgvJA24xTR1xxWKn8uy1U', NULL, NULL, NULL, NULL, NULL, '1', '17589749542', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2P9BAM', NULL, NULL, NULL, NULL, '2025-04-20 11:25:49', NULL, '2025-04-20 11:25:49', 0, 'VWFhm4uVaBoFKYzJsF/oYw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1913835430453841920', '1801458632269893632', '3', 'oJTwA7R2dBJW4wlvSYiDZas0uZog', 'oJTwA7R2dBJW4wlvSYiDZas0uZog', NULL, NULL, NULL, NULL, NULL, '1', '13403583297', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1OA9TG', NULL, NULL, NULL, NULL, '2025-04-20 14:01:38', NULL, '2025-04-20 14:01:38', 0, '44nAJC0ZdpJv/KyX8LaRNg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1913839695968735232', '1801458632269893632', '3', 'oJTwA7QI8ifKlgW-vnh9tAy6w_lo', 'oJTwA7QI8ifKlgW-vnh9tAy6w_lo', NULL, NULL, NULL, NULL, NULL, '1', '13977325964', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'MWMTNI', NULL, NULL, NULL, NULL, '2025-04-20 14:18:35', NULL, '2025-04-20 14:18:35', 0, 'bTko+Q+VwPdXATexrKZznA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1913873850697912320', '1801458632269893632', '3', 'oJTwA7WQMWzhwq8hWLDI5mQKyJls', 'oJTwA7WQMWzhwq8hWLDI5mQKyJls', NULL, NULL, NULL, NULL, NULL, '1', '13152581280', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0AX6DH', NULL, NULL, NULL, NULL, '2025-04-20 16:34:18', NULL, '2025-04-20 16:34:18', 0, 'BYOCNiF7E2GQvmYaoPfEyA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1913885589392986112', '1801458632269893632', '3', 'oJTwA7QhoOc2z1rzDaZXo_HSkI14', 'oJTwA7QhoOc2z1rzDaZXo_HSkI14', NULL, NULL, NULL, NULL, NULL, '1', '13823768167', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2V3YUL', NULL, NULL, NULL, NULL, '2025-04-20 17:20:57', NULL, '2025-04-20 17:20:57', 0, 'aoxNUUwcOSxpCXvfNRKHQw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914170142410018816', '1801458632269893632', '3', 'oJTwA7Tc2PIo704I967d_8_osrec', 'oJTwA7Tc2PIo704I967d_8_osrec', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'IIF57C', NULL, NULL, NULL, NULL, '2025-04-21 12:11:40', NULL, '2025-04-21 12:11:40', 0, 'C5vuStK9lk4nvDTngx7Z6A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914171707275481088', '1801458632269893632', '3', 'oJTwA7bT9COX0Xff9XPcuxWbxMls', 'oJTwA7bT9COX0Xff9XPcuxWbxMls', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SMMQUP', NULL, NULL, NULL, NULL, '2025-04-21 12:17:53', NULL, '2025-04-21 12:17:53', 0, 'UbVkI0OGe//dbMfqnUax3g==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914567969413926912', '1801458632269893632', '3', 'oJTwA7Uh3uBsh9Ff80_SmsXkVPDI', 'oJTwA7Uh3uBsh9Ff80_SmsXkVPDI', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TDLQCZ', NULL, NULL, NULL, NULL, '2025-04-22 14:32:29', NULL, '2025-04-22 14:32:29', 0, 'oUKuUhLzSnOatM8kyJKVsA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914568998016651264', '1801458632269893632', '3', 'oJTwA7eFpdx3BejoYnBxAgE8q1nY', 'oJTwA7eFpdx3BejoYnBxAgE8q1nY', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'I0XQUW', NULL, NULL, NULL, NULL, '2025-04-22 14:36:34', NULL, '2025-04-22 14:36:34', 0, 'ojAQmw7c6QATA9NcTXwKGw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914573446587748352', '1801458632269893632', '3', 'oJTwA7SSRke6g4aLgbIMyeIj6AJA', 'oJTwA7SSRke6g4aLgbIMyeIj6AJA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1GWTJN', NULL, NULL, NULL, NULL, '2025-04-22 14:54:15', NULL, '2025-04-22 14:54:15', 0, 'Vg01/zy6u6/WEECoSpmPuA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914579129496375296', '1801458632269893632', '3', 'oJTwA7V0hjdBY0N0su4M1KUTw-xw', 'oJTwA7V0hjdBY0N0su4M1KUTw-xw', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '68NB6A', NULL, NULL, NULL, NULL, '2025-04-22 15:16:50', NULL, '2025-04-22 15:16:50', 0, '54RRf75NcgcVCGJz0C2ejA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1914899787300343808', '1801458632269893632', '3', 'oJTwA7dj-v3huFT54GPLNX9LF07w', 'oJTwA7dj-v3huFT54GPLNX9LF07w', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PBOQ4T', NULL, NULL, NULL, NULL, '2025-04-23 12:31:01', NULL, '2025-04-23 12:31:01', 0, 'Z+DlyvZbuqelOUBJXtq7ag==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1915055041014796288', '1801458632269893632', '3', 'oJTwA7cJDVo0KCNy9ZVfeWbVa34g', 'oJTwA7cJDVo0KCNy9ZVfeWbVa34g', NULL, NULL, NULL, NULL, NULL, '1', '18361585881', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '846R5B', NULL, NULL, NULL, NULL, '2025-04-23 22:47:56', NULL, '2025-04-23 22:47:56', 0, 'Ea7LgLmKm3YZ5n4J1yZlwQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1915582188326359040', '1801458632269893632', '3', 'oJTwA7W-R7eCaL6gAmc-Y8rJIyLA', 'oJTwA7W-R7eCaL6gAmc-Y8rJIyLA', NULL, NULL, NULL, NULL, NULL, '1', '15172494410', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'AROEGD', NULL, NULL, NULL, NULL, '2025-04-25 09:42:38', NULL, '2025-04-25 09:42:38', 0, 'F8FqMlHPz7XSG4goTDAn+A==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1915588509389950976', '1801458632269893632', '3', 'oJTwA7TUDgH_kN-VTsWwaxLoypus', 'oJTwA7TUDgH_kN-VTsWwaxLoypus', NULL, NULL, NULL, NULL, NULL, '1', '13360605358', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'BR2JDR', NULL, NULL, NULL, NULL, '2025-04-25 10:07:45', NULL, '2025-04-25 10:07:45', 0, 'BAmAu+PNjdXQpSrZIxqUDg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1915628459166666752', '1801458632269893632', '3', 'oJTwA7Y-S0a1VY4YO7ZSDOtfs41k', 'oJTwA7Y-S0a1VY4YO7ZSDOtfs41k', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PNDSEJ', NULL, NULL, NULL, NULL, '2025-04-25 12:46:30', NULL, '2025-04-25 12:46:30', 0, 'ttqpivDCmbjY3sLH+yzlVQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916013403336478720', '1801458632269893632', '3', 'oJTwA7c_GYxQbi_EP5Fys8X8_Rms', 'oJTwA7c_GYxQbi_EP5Fys8X8_Rms', NULL, NULL, NULL, NULL, NULL, '1', '13707833370', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'W6LVNI', NULL, NULL, NULL, NULL, '2025-04-26 14:16:07', NULL, '2025-04-26 14:16:07', 0, 'W6ujHtLVhnxKJ+IbdEwJqw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916021791428579328', '1801458632269893632', '3', 'oJTwA7YC2bsv2LfYIPjDxHUYIqKA', 'oJTwA7YC2bsv2LfYIPjDxHUYIqKA', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'U5445R', NULL, NULL, NULL, NULL, '2025-04-26 14:49:27', NULL, '2025-04-26 14:49:27', 0, 'qaLBMZrfSxNO3/wO4n5g/w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916031785213497344', '1801458632269893632', '3', 'oJTwA7TE2UtxBIqE-IZUGfs8lqus', 'oJTwA7TE2UtxBIqE-IZUGfs8lqus', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '8T6MSW', NULL, NULL, NULL, NULL, '2025-04-26 15:29:10', NULL, '2025-04-26 15:29:10', 0, 'bOL1LlTuRqe5qmt+M+chzQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916085333095747584', '1801458632269893632', '3', 'oJTwA7QZFH4qmHk1Pxxjc8RtlG5o', 'oJTwA7QZFH4qmHk1Pxxjc8RtlG5o', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '78N8E0', NULL, NULL, NULL, NULL, '2025-04-26 19:01:57', NULL, '2025-04-26 19:01:57', 0, 'r3vYYlR4UTgkYYot3ECyNA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916128300732911616', '1801458632269893632', '3', 'oJTwA7VFbKXMs0Zh03ly0zW4BPlw', 'oJTwA7VFbKXMs0Zh03ly0zW4BPlw', NULL, NULL, NULL, NULL, NULL, '1', '13911088823', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZJQXLL', NULL, NULL, NULL, NULL, '2025-04-26 21:52:41', NULL, '2025-04-26 21:52:41', 0, '/fa7kPxhf8YEX2l/T2l1BQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916275640609411072', '1801458632269893632', '3', 'oJTwA7eyYIMD1q9d9UHjaeBB_IwY', 'oJTwA7eyYIMD1q9d9UHjaeBB_IwY', NULL, NULL, NULL, NULL, NULL, '1', '18858802316', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'T07YZH', NULL, NULL, NULL, NULL, '2025-04-27 07:38:10', NULL, '2025-04-27 07:38:10', 0, 'ZHnyw6fNPjHTHxE2zYaj7w==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916302023842729984', '1801458632269893632', '3', 'oJTwA7fLjidDInlC0lgEkGUkHjDk', 'oJTwA7fLjidDInlC0lgEkGUkHjDk', NULL, NULL, NULL, NULL, NULL, '1', '13480751665', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'YVLSLI', NULL, NULL, NULL, NULL, '2025-04-27 09:23:00', NULL, '2025-04-27 09:23:00', 0, 'Kk40vwaRL16S/LBzWuJIVA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916321462449475584', '1801458632269893632', '3', 'oJTwA7R49HYsY2mZ-66lGaVifXJI', 'oJTwA7R49HYsY2mZ-66lGaVifXJI', NULL, NULL, NULL, NULL, NULL, '1', '13302206656', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'K7FM53', NULL, NULL, NULL, NULL, '2025-04-27 10:40:14', NULL, '2025-04-27 10:40:14', 0, 'jSP8cmi7BvGzZ5Z/U3OagA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916400427407118336', '1801458632269893632', '3', 'oJTwA7eg1NJlMdK-SLXgmGZ1rb3s', 'oJTwA7eg1NJlMdK-SLXgmGZ1rb3s', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3T7SS3', NULL, NULL, NULL, NULL, '2025-04-27 15:54:01', NULL, '2025-04-27 15:54:01', 0, 'G0i1poImMVUw1Ffml2TZTw==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916410483628445696', '1801458632269893632', '3', 'oJTwA7SQhe16IvWbDrsWAclxQPT8', 'oJTwA7SQhe16IvWbDrsWAclxQPT8', NULL, NULL, NULL, NULL, NULL, '1', '15315308722', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'OPEGOS', NULL, NULL, NULL, NULL, '2025-04-27 16:33:59', NULL, '2025-04-27 16:33:59', 0, 'XTZ07nhHbw5Uli4+ybOGsg==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916473011544920064', '1801458632269893632', '3', 'oJTwA7X__agfdU2DUvCr_5GKlxcY', 'oJTwA7X__agfdU2DUvCr_5GKlxcY', NULL, NULL, NULL, NULL, NULL, '1', '13760381973', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '05N7ZI', NULL, NULL, NULL, NULL, '2025-04-27 20:42:27', NULL, '2025-04-27 20:42:27', 0, '4JdGmR81okFlTWjthLkKPA==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916664889905123328', '1801458632269893632', '3', 'oJTwA7f-P-SXAedctxBH2vARRnZU', 'oJTwA7f-P-SXAedctxBH2vARRnZU', NULL, NULL, NULL, NULL, NULL, '1', '18518558447', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3YE7G2', NULL, NULL, NULL, NULL, '2025-04-28 09:24:54', NULL, '2025-04-28 09:24:54', 0, 'z6SP1pWZQCR+5LQvtxJKog==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916665006481608704', '1801458632269893632', '3', 'oJTwA7Y2RO4PMS1_wr2dHfk1kCV0', 'oJTwA7Y2RO4PMS1_wr2dHfk1kCV0', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'OMZTUW', NULL, NULL, NULL, NULL, '2025-04-28 09:25:22', NULL, '2025-04-28 09:25:22', 0, 'NCWyWaNpStI9o3qohEGXpQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916665032276578304', '1801458632269893632', '3', 'oJTwA7dz9etv09EbozyJdCD1f7to', 'oJTwA7dz9etv09EbozyJdCD1f7to', NULL, NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TGSSD9', NULL, NULL, NULL, NULL, '2025-04-28 09:25:28', NULL, '2025-04-28 09:25:28', 0, 'a059OXsXTM4mAdWWqg9FKQ==', NULL);
INSERT INTO `crm_customer_base` VALUES ('1916667241085145088', '1801458632269893632', '3', 'oJTwA7atDrkE0IZJ1CIQWwBUQmnE', 'oJTwA7atDrkE0IZJ1CIQWwBUQmnE', NULL, NULL, NULL, NULL, NULL, '1', '15779085808', NULL, NULL, '0', b'0', b'0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'JJACP1', NULL, NULL, NULL, NULL, '2025-04-28 09:34:14', NULL, '2025-04-28 09:34:14', 0, 'RCDPyjqVbRD6LHUtn40rIQ==', NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_customer_identity
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer_identity`;
CREATE TABLE `crm_customer_identity` (
  `tenant_id` varchar(255) NOT NULL,
  `merchant_no` varchar(255) DEFAULT NULL COMMENT '商户号',
  `customer_no` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL COMMENT '身份登录名称',
  `name` varchar(255) DEFAULT NULL COMMENT '身份名称：商户(3) 代理商(4) 客户(5)',
  `biz_role_type` varchar(255) NOT NULL COMMENT '身份类型：商户|客户|代理商  见业务角色类型枚举',
  `biz_role_type_no` varchar(255) NOT NULL COMMENT '身份类型编号：商户号|代理商号|客户号',
  `status` varchar(255) NOT NULL DEFAULT '1' COMMENT '状态： 0：禁用 1:启用',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  PRIMARY KEY (`biz_role_type_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_customer_identity
-- ----------------------------
BEGIN;
INSERT INTO `crm_customer_identity` VALUES ('1846190237240397824', '1846556049375875074', '1869566390399275008', 'oqASg7Q-eVuRqOr6OhDoa7Y8nMvg', '客户', '5', '1869566390399275008', '1', '2024-12-19 15:41:58', '2024-12-19 10:12:17', 0);
INSERT INTO `crm_customer_identity` VALUES ('1846190237240397824', '1869637386607136768', '1869566390399275001', '商户', '商户', '3', '1869637386607136768', '1', '2024-12-25 20:42:22', '2024-12-19 14:54:25', 0);
INSERT INTO `crm_customer_identity` VALUES ('1846190237240397824', '1846556049375875074', '1869566390399275001', '代理商', '代理商', '4', '1869649134244466689', '1', '2024-12-25 20:42:26', '2024-12-19 15:41:04', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1894252987572948992', 'oJTwA7dVZBvHwWoi4qRtmmGkW8m8', '客户', '5', '1894252987572948992', '1', '2025-02-25 13:08:00', '2025-02-25 13:08:00', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1894339016384450560', 'oJTwA7T0BAikBkE9Q7ATSmufiv78', '客户', '5', '1894339016384450560', '1', '2025-02-25 18:49:51', '2025-02-25 18:49:51', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1894958621305999360', 'oJTwA7a1CUmvLrPVnmp4yKpJXhcQ', '客户', '5', '1894958621305999360', '1', '2025-02-27 11:51:56', '2025-02-27 11:51:56', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1895111719949307904', 'oJTwA7UQOVo9xNL8Uq4dkjJsTC4Q', '客户', '5', '1895111719949307904', '1', '2025-02-27 22:00:18', '2025-02-27 22:00:18', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1895670429851455488', 'oJTwA7fYuvDKTAFGt63hmnDrcyFA', '客户', '5', '1895670429851455488', '1', '2025-03-01 11:00:25', '2025-03-01 11:00:25', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1895670456736944128', 'oJTwA7Qv1Ao_4ixfitxY3gDsxnpY', '客户', '5', '1895670456736944128', '1', '2025-03-01 11:00:31', '2025-03-01 11:00:31', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1895754348491837440', 'oJTwA7a31OtyOEcmVt1kpyMI5T9A', '客户', '5', '1895754348491837440', '1', '2025-03-01 16:33:52', '2025-03-01 16:33:52', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1895777251895676928', 'oJTwA7VLGpgg8aEJStsWocEmhINA', '客户', '5', '1895777251895676928', '1', '2025-03-01 18:04:53', '2025-03-01 18:04:53', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897271318773108736', 'oJTwA7YCSWSlxO5ouY9eMJY8Kyb8', '客户', '5', '1897271318773108736', '1', '2025-03-05 21:01:46', '2025-03-05 21:01:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897271321709121536', 'oJTwA7fXrMaLLa800PKNXpaSfces', '客户', '5', '1897271321709121536', '1', '2025-03-05 21:01:47', '2025-03-05 21:01:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897282638629179392', 'oJTwA7V-HYjTEbKpt5PrcxJYxEiE', '客户', '5', '1897282638629179392', '1', '2025-03-05 21:46:45', '2025-03-05 21:46:45', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897282653841920000', 'oJTwA7bvybdKkLLCU2xBr_WLRrLo', '客户', '5', '1897282653841920000', '1', '2025-03-05 21:46:49', '2025-03-05 21:46:49', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897651748298952704', 'oJTwA7esri1vWUwZw8HsZ0ViPSv8', '客户', '5', '1897651748298952704', '1', '2025-03-06 22:13:28', '2025-03-06 22:13:28', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897845906947051520', 'oJTwA7cYaJftOFSboJ8bBGFAeago', '客户', '5', '1897845906947051520', '1', '2025-03-07 11:04:59', '2025-03-07 11:04:59', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1897845908008210432', 'oJTwA7a6A7hGO-20iioiqz6D9rRg', '客户', '5', '1897845908008210432', '1', '2025-03-07 11:04:59', '2025-03-07 11:04:59', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1898318636230250496', 'oJTwA7QNQ03F1paUPsjh01XbNvqw', '客户', '5', '1898318636230250496', '1', '2025-03-08 18:23:26', '2025-03-08 18:23:26', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1898620923444072448', 'oJTwA7dAdYa4HhU4SAqaMKv2oSBA', '客户', '5', '1898620923444072448', '1', '2025-03-09 14:24:37', '2025-03-09 14:24:37', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1898715340075044864', 'oJTwA7T0BAikBkE9Q7ATSmufiv78', '客户', '5', '1898715340075044864', '1', '2025-03-09 20:39:48', '2025-03-09 20:39:48', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1898950034335076352', 'oJTwA7bvybdKkLLCU2xBr_WLRrLo', '客户', '5', '1898950034335076352', '1', '2025-03-10 12:12:23', '2025-03-10 12:12:23', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899013164964646912', 'oJTwA7bCUj-zoqJQe4uHDo9AwoUA', '客户', '5', '1899013164964646912', '1', '2025-03-10 16:23:15', '2025-03-10 16:23:15', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899013438898835456', 'oJTwA7eNkoKGklDscWOUPR-BEFfU', '客户', '5', '1899013438898835456', '1', '2025-03-10 16:24:20', '2025-03-10 16:24:20', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899014350237208576', 'oJTwA7fqDabXGlczKkhgSpY7ZU6A', '客户', '5', '1899014350237208576', '1', '2025-03-10 16:27:57', '2025-03-10 16:27:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899021131621470208', 'oJTwA7ejLxdlw8_enccKwiSZuxRM', '客户', '5', '1899021131621470208', '1', '2025-03-10 16:54:54', '2025-03-10 16:54:54', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899027150011502592', 'oJTwA7bRkxoFwxCh6x6HabNzeomg', '客户', '5', '1899027150011502592', '1', '2025-03-10 17:18:49', '2025-03-10 17:18:49', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899309161842348032', '13798353468', '客户', '5', '1899309161842348032', '1', '2025-03-11 11:59:26', '2025-03-11 11:59:26', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899371683479949312', 'oJTwA7TFdE7jTf8348ffKGXNOhRE', '客户', '5', '1899371683479949312', '1', '2025-03-11 16:07:52', '2025-03-11 16:07:52', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899414286959251456', 'oJTwA7cCZO1ougM2Fd2bcK7A0a7c', '客户', '5', '1899414286959251456', '1', '2025-03-11 18:57:10', '2025-03-11 18:57:10', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899415466225569792', 'oJTwA7QOv4IbEIn0SiabxoaBChcY', '客户', '5', '1899415466225569792', '1', '2025-03-11 19:01:51', '2025-03-11 19:01:51', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899504351576199168', 'oJTwA7V-HYjTEbKpt5PrcxJYxEiE', '客户', '5', '1899504351576199168', '1', '2025-03-12 00:55:03', '2025-03-12 00:55:03', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899685929535279104', 'oJTwA7ZkD0BT5Tu02U8js-keXNbk', '客户', '5', '1899685929535279104', '1', '2025-03-12 12:56:34', '2025-03-12 12:56:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899696506584305664', '18548124999', '客户', '5', '1899696506584305664', '1', '2025-03-12 13:38:36', '2025-03-12 13:38:36', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899997913032036352', 'oJTwA7cYaJftOFSboJ8bBGFAeago', '客户', '5', '1899997913032036352', '1', '2025-03-13 09:36:17', '2025-03-13 09:36:17', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899997913153671168', 'oJTwA7Qv1Ao_4ixfitxY3gDsxnpY', '客户', '5', '1899997913153671168', '1', '2025-03-13 09:36:17', '2025-03-13 09:36:17', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1899998418307256320', 'oJTwA7bpdFpB13NTgVGtVoMiHvl4', '客户', '5', '1899998418307256320', '1', '2025-03-13 09:38:18', '2025-03-13 09:38:18', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900002524908359680', 'oJTwA7fNdqAEWlgwM6qGmmD7UPWo', '客户', '5', '1900002524908359680', '1', '2025-03-13 09:54:37', '2025-03-13 09:54:37', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900005915831177216', 'oJTwA7Zr5f2uL9cC7qvcOcAH36KU', '客户', '5', '1900005915831177216', '1', '2025-03-13 10:08:05', '2025-03-13 10:08:05', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900008903136120832', 'oJTwA7YCSWSlxO5ouY9eMJY8Kyb8', '客户', '5', '1900008903136120832', '1', '2025-03-13 10:19:57', '2025-03-13 10:19:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900008904130170880', 'oJTwA7a4Vx-uelKLXkb2brdQCY_M', '客户', '5', '1900008904130170880', '1', '2025-03-13 10:19:58', '2025-03-13 10:19:58', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900011084362944512', 'oJTwA7c9T1-vm-oemcqhjI2bKqjE', '客户', '5', '1900011084362944512', '1', '2025-03-13 10:28:37', '2025-03-13 10:28:37', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900015891031134208', 'oJTwA7e46S8LlkkqWDAa4PyYWMys', '客户', '5', '1900015891031134208', '1', '2025-03-13 10:47:43', '2025-03-13 10:47:43', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900089644872241152', 'oJTwA7XQgg5IosAhytwMzV9L2llU', '客户', '5', '1900089644872241152', '1', '2025-03-13 15:40:48', '2025-03-13 15:40:48', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900150827201466368', 'oJTwA7ZP60_XAo4yqr2gji0Z2b8Q', '客户', '5', '1900150827201466368', '1', '2025-03-13 19:43:55', '2025-03-13 19:43:55', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900164559797358592', 'oJTwA7Vn3KUOu7prsFDBrdU8HZeo', '客户', '5', '1900164559797358592', '1', '2025-03-13 20:38:29', '2025-03-13 20:38:29', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900165286036901888', 'oJTwA7d-CF_bpKBZusT_egS16hTs', '客户', '5', '1900165286036901888', '1', '2025-03-13 20:41:22', '2025-03-13 20:41:22', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900172862816391168', 'oJTwA7Qt6hYjSLFAmbSV3Z0xZPpA', '客户', '5', '1900172862816391168', '1', '2025-03-13 21:11:28', '2025-03-13 21:11:28', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900176469775224832', 'oJTwA7UzralOB00mO2jVNxsM4l_E', '客户', '5', '1900176469775224832', '1', '2025-03-13 21:25:48', '2025-03-13 21:25:48', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900429244618641408', 'oJTwA7QNQ03F1paUPsjh01XbNvqw', '客户', '5', '1900429244618641408', '1', '2025-03-14 14:10:14', '2025-03-14 14:10:14', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900437342775283712', 'oJTwA7SW_7Ur4CsVncQU_TAKc5lk', '客户', '5', '1900437342775283712', '1', '2025-03-14 14:42:25', '2025-03-14 14:42:25', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900783796916195328', 'oJTwA7StCEfC_Ce2kAm4o-zDxo9w', '客户', '5', '1900783796916195328', '1', '2025-03-15 13:39:06', '2025-03-15 13:39:06', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900788178655252480', 'oJTwA7YmSUdVJ0iNDUo_e_NQHbIA', '客户', '5', '1900788178655252480', '1', '2025-03-15 13:56:31', '2025-03-15 13:56:31', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900795565348884480', 'oJTwA7WsqyiXNcAC4PByKc7PPiLc', '客户', '5', '1900795565348884480', '1', '2025-03-15 14:25:52', '2025-03-15 14:25:52', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900848842157461504', 'oJTwA7dfYnWE_Okxle2acbEPWSFc', '客户', '5', '1900848842157461504', '1', '2025-03-15 17:57:34', '2025-03-15 17:57:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1900944778892283904', 'oJTwA7d0pkP9bX-IA39Vz8idEJFU', '客户', '5', '1900944778892283904', '1', '2025-03-16 00:18:47', '2025-03-16 00:18:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1901540556186718208', 'oJTwA7SSTppRBF5dc4PMJ55WhRBY', '客户', '5', '1901540556186718208', '1', '2025-03-17 15:46:12', '2025-03-17 15:46:12', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1901552156947910656', 'oJTwA7fBUyxPJHbbRwTmdurBCNRU', '客户', '5', '1901552156947910656', '1', '2025-03-17 16:32:18', '2025-03-17 16:32:18', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1902207089061793792', 'oJTwA7V5fyXB0zV3jpSmtHdFRbbY', '客户', '5', '1902207089061793792', '1', '2025-03-19 11:54:46', '2025-03-19 11:54:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1902334797766856704', 'oJTwA7aqzhG7ke3k71MH85oRe8ms', '客户', '5', '1902334797766856704', '1', '2025-03-19 20:22:14', '2025-03-19 20:22:14', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1902334827940679680', 'oJTwA7bXgafA-ywauKBN96SvZn6A', '客户', '5', '1902334827940679680', '1', '2025-03-19 20:22:21', '2025-03-19 20:22:21', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1902609821220343808', 'oJTwA7eE51atItC4A2NWvMTWZGxE', '客户', '5', '1902609821220343808', '1', '2025-03-20 14:35:04', '2025-03-20 14:35:04', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1902609884675969024', 'oJTwA7Z0t36IaXFIQdar02QBPPoY', '客户', '5', '1902609884675969024', '1', '2025-03-20 14:35:20', '2025-03-20 14:35:20', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1902695184425160704', 'oJTwA7WYzuRghJ57F4jGODvaFi5c', '客户', '5', '1902695184425160704', '1', '2025-03-20 20:14:17', '2025-03-20 20:14:17', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1903015348328861696', 'oJTwA7fhOja3LNkIvrn_SgCy7oRw', '客户', '5', '1903015348328861696', '1', '2025-03-21 17:26:30', '2025-03-21 17:26:30', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1903761407229431808', 'oJTwA7Vpp-3c9nhc9KQJNjtbQ060', '客户', '5', '1903761407229431808', '1', '2025-03-23 18:51:04', '2025-03-23 18:51:04', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1903828614454054912', 'oJTwA7UCB6x-8oqr145z0OKfHWAE', '客户', '5', '1903828614454054912', '1', '2025-03-23 23:18:07', '2025-03-23 23:18:07', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1903929072870887424', 'oJTwA7ZexwdICUMlVoPRFtYDFIWc', '客户', '5', '1903929072870887424', '1', '2025-03-24 05:57:19', '2025-03-24 05:57:19', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1903951167357063168', 'oJTwA7blBtfjtGhx7EssQEiIkjms', '客户', '5', '1903951167357063168', '1', '2025-03-24 07:25:06', '2025-03-24 07:25:06', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904070772515803136', 'oJTwA7bMlVDkd3IeEFY_jsUD6jWQ', '客户', '5', '1904070772515803136', '1', '2025-03-24 15:20:22', '2025-03-24 15:20:22', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904436687090618368', 'oJTwA7QWqW2AZ8vrDQOYA2sZPbCM', '客户', '5', '1904436687090618368', '1', '2025-03-25 15:34:23', '2025-03-25 15:34:23', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904746152948011008', 'oJTwA7X6LMWTTxkMbWumyGD_o65s', '客户', '5', '1904746152948011008', '1', '2025-03-26 12:04:06', '2025-03-26 12:04:06', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904783794787979264', 'oJTwA7ZasmWQyFWajeo8WdqRgqGY', '客户', '5', '1904783794787979264', '1', '2025-03-26 14:33:40', '2025-03-26 14:33:40', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904790071148679168', 'oJTwA7coIURVQE1Fcb0gdxhU9Aqw', '客户', '5', '1904790071148679168', '1', '2025-03-26 14:58:37', '2025-03-26 14:58:37', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904790208986091520', 'oJTwA7YdRn6GTupWsqD15toxMO48', '客户', '5', '1904790208986091520', '1', '2025-03-26 14:59:09', '2025-03-26 14:59:09', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904790743604662272', 'oJTwA7cO-EkU9A1WYsdbQVLBrLNA', '客户', '5', '1904790743604662272', '1', '2025-03-26 15:01:17', '2025-03-26 15:01:17', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904805916537131008', 'oJTwA7epdA0qHgZWcAB4AV0pBlqc', '客户', '5', '1904805916537131008', '1', '2025-03-26 16:01:34', '2025-03-26 16:01:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904823926022868992', 'oJTwA7SA3NgC10cD897oryQbtWEo', '客户', '5', '1904823926022868992', '1', '2025-03-26 17:13:08', '2025-03-26 17:13:08', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904825279080173568', 'oJTwA7fFZl-jQldQ20z5i7vMpN9A', '客户', '5', '1904825279080173568', '1', '2025-03-26 17:18:31', '2025-03-26 17:18:31', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1904877516204675072', 'oJTwA7TpAT9eXM34D9EisK3ZQt_c', '客户', '5', '1904877516204675072', '1', '2025-03-26 20:46:05', '2025-03-26 20:46:05', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1905224228559523840', 'oJTwA7U_3WFy18rn-VK6Ti_S6eAI', '客户', '5', '1905224228559523840', '1', '2025-03-27 19:43:48', '2025-03-27 19:43:48', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1905262975258857472', 'oJTwA7XrztvZZUWO254AFHWSJMfE', '客户', '5', '1905262975258857472', '1', '2025-03-27 22:17:46', '2025-03-27 22:17:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906147688320208896', 'oJTwA7WSKaXhIJxmtf37_odEHm8M', '客户', '5', '1906147688320208896', '1', '2025-03-30 08:53:18', '2025-03-30 08:53:18', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906147804703756288', 'oJTwA7YFhE-xS7RiYLVV7PCO49c8', '客户', '5', '1906147804703756288', '1', '2025-03-30 08:53:45', '2025-03-30 08:53:45', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906277548468342784', 'oJTwA7Q54uVWqOaIahL1luyQQm9U', '客户', '5', '1906277548468342784', '1', '2025-03-30 17:29:19', '2025-03-30 17:29:19', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906292008843087872', 'oJTwA7SekTcyKy4lz3lyEQmY-yP0', '客户', '5', '1906292008843087872', '1', '2025-03-30 18:26:46', '2025-03-30 18:26:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906304715856154624', 'oJTwA7fwNpCM1iC1ZAyMCEHpO72c', '客户', '5', '1906304715856154624', '1', '2025-03-30 19:17:16', '2025-03-30 19:17:16', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906336550233444352', 'oJTwA7evrXATAQW9z401nkWAk9q4', '客户', '5', '1906336550233444352', '1', '2025-03-30 21:23:46', '2025-03-30 21:23:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906536332541431808', 'oJTwA7YHbkIXBINIpc5CUzSC1uac', '客户', '5', '1906536332541431808', '1', '2025-03-31 10:37:38', '2025-03-31 10:37:38', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906541099435036672', 'oJTwA7fvCKI36gHt0IEpB6-TGw10', '客户', '5', '1906541099435036672', '1', '2025-03-31 10:56:34', '2025-03-31 10:56:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906601661254733824', 'oJTwA7bwqs5N5Crmz7f58U4tthyA', '客户', '5', '1906601661254733824', '1', '2025-03-31 14:57:13', '2025-03-31 14:57:13', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906602133575307264', 'oJTwA7aQspl3MfsRTVXq-iMTkvPE', '客户', '5', '1906602133575307264', '1', '2025-03-31 14:59:06', '2025-03-31 14:59:06', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906619568734998528', 'oJTwA7WB95l-ONklBZ4VWBt-ymyk', '客户', '5', '1906619568734998528', '1', '2025-03-31 16:08:23', '2025-03-31 16:08:23', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906926688915296256', 'oJTwA7U4NSBnWSja-vXX08dEoqBE', '客户', '5', '1906926688915296256', '1', '2025-04-01 12:28:46', '2025-04-01 12:28:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906926839050407936', 'oJTwA7eSCaCxdxkYd2K0AtlrNBmg', '客户', '5', '1906926839050407936', '1', '2025-04-01 12:29:22', '2025-04-01 12:29:22', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906949417555070976', 'oJTwA7Snrh3XqqPAXNDpcojmynCA', '客户', '5', '1906949417555070976', '1', '2025-04-01 13:59:05', '2025-04-01 13:59:05', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1906958724497543168', 'oJTwA7dYYQmV1xhTTTnyawBgDXzk', '客户', '5', '1906958724497543168', '1', '2025-04-01 14:36:04', '2025-04-01 14:36:04', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907016176089829376', 'oJTwA7c2SPQl0parwM5lweWE05WE', '客户', '5', '1907016176089829376', '1', '2025-04-01 18:24:21', '2025-04-01 18:24:21', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907305229372755968', 'oJTwA7RselJrlpUCwipnrot198PE', '客户', '5', '1907305229372755968', '1', '2025-04-02 13:32:57', '2025-04-02 13:32:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907360299464396800', 'oJTwA7cdem0EfTTAr1KQCn0-bveY', '客户', '5', '1907360299464396800', '1', '2025-04-02 17:11:47', '2025-04-02 17:11:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907372524501602304', 'oJTwA7YeWJnAc09OckB5a9wqyBFM', '客户', '5', '1907372524501602304', '1', '2025-04-02 18:00:21', '2025-04-02 18:00:21', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907426973576204288', 'oJTwA7fWdHXvE-ANBNgOas3uSYIw', '客户', '5', '1907426973576204288', '1', '2025-04-02 21:36:43', '2025-04-02 21:36:43', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907428498784522240', 'oJTwA7WKT-dhbTNFK1KWgI9I1hFg', '客户', '5', '1907428498784522240', '1', '2025-04-02 21:42:47', '2025-04-02 21:42:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907630072815292416', 'oJTwA7e_UgClVgTrdMyuPRdxkAyk', '客户', '5', '1907630072815292416', '1', '2025-04-03 11:03:46', '2025-04-03 11:03:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907683129217388544', 'oJTwA7S5QO2sESwq18H_oiwWEKWE', '客户', '5', '1907683129217388544', '1', '2025-04-03 14:34:35', '2025-04-03 14:34:35', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907694822777556992', 'oJTwA7dNge0e8wahoGyTj3ON_hEo', '客户', '5', '1907694822777556992', '1', '2025-04-03 15:21:03', '2025-04-03 15:21:03', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907708062509174784', 'oJTwA7ahLR24ZaACoS6Nq9yNiPK0', '客户', '5', '1907708062509174784', '1', '2025-04-03 16:13:40', '2025-04-03 16:13:40', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907776509444231168', 'oJTwA7SPY3_1BsqYnXfKLaBPYcFc', '客户', '5', '1907776509444231168', '1', '2025-04-03 20:45:39', '2025-04-03 20:45:39', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907797374340829184', 'oJTwA7VMwaW_OiurfIPKmO_XRqgg', '客户', '5', '1907797374340829184', '1', '2025-04-03 22:08:34', '2025-04-03 22:08:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1907959910172332032', 'oJTwA7SxH3gA9hmySQsvh5wqwmeQ', '客户', '5', '1907959910172332032', '1', '2025-04-04 08:54:25', '2025-04-04 08:54:25', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1908478058793406464', 'oJTwA7VWQV7ddSuTIbE55D8BaFrk', '客户', '5', '1908478058793406464', '1', '2025-04-05 19:13:21', '2025-04-05 19:13:21', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1908772661740310528', 'oJTwA7TVmUsGFObHlOBJrNG2Kh4I', '客户', '5', '1908772661740310528', '1', '2025-04-06 14:44:00', '2025-04-06 14:44:00', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1908776061483749376', 'oJTwA7VdZnQhQNJVdzl5oH0l2n_E', '客户', '5', '1908776061483749376', '1', '2025-04-06 14:57:31', '2025-04-06 14:57:31', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909078623009247232', 'oJTwA7cIZo6DdzssKWIaGNH3vEb0', '客户', '5', '1909078623009247232', '1', '2025-04-07 10:59:47', '2025-04-07 10:59:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909163833335877632', 'oJTwA7cMb2Y6KJ4qwiOQoO3ygyWQ', '客户', '5', '1909163833335877632', '1', '2025-04-07 16:38:23', '2025-04-07 16:38:23', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909174972467777536', 'oJTwA7ezVlMrwMAUeTuB6X2Hc4ZQ', '客户', '5', '1909174972467777536', '1', '2025-04-07 17:22:38', '2025-04-07 17:22:38', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909447249335816192', 'oJTwA7ego2l2xUPhhx_Kyh-agP8E', '客户', '5', '1909447249335816192', '1', '2025-04-08 11:24:34', '2025-04-08 11:24:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909447394106413056', 'oJTwA7e4YF01kOCahvI3JArJBJm8', '客户', '5', '1909447394106413056', '1', '2025-04-08 11:25:09', '2025-04-08 11:25:09', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909451317710032896', 'oJTwA7eWRChMHcSm_6rGpxO8CvKU', '客户', '5', '1909451317710032896', '1', '2025-04-08 11:40:44', '2025-04-08 11:40:44', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909474166365622272', 'oJTwA7ZEznyrwIi5ISu78N2y1inY', '客户', '5', '1909474166365622272', '1', '2025-04-08 13:11:32', '2025-04-08 13:11:32', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909474289623633920', 'oJTwA7YC5_eg0Nlzc468tTOarlvM', '客户', '5', '1909474289623633920', '1', '2025-04-08 13:12:01', '2025-04-08 13:12:01', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909474735109050368', 'oJTwA7UFE6WRmdIC5KwuDO5xgPdo', '客户', '5', '1909474735109050368', '1', '2025-04-08 13:13:47', '2025-04-08 13:13:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909519067128139776', 'oJTwA7dj5r2mnI0jZ4rKS2DekZK4', '客户', '5', '1909519067128139776', '1', '2025-04-08 16:09:57', '2025-04-08 16:09:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909524342677573632', 'oJTwA7RaA9lCJFlCoyVtEPotBVIE', '客户', '5', '1909524342677573632', '1', '2025-04-08 16:30:55', '2025-04-08 16:30:55', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909533328743206912', 'oJTwA7a_Z50RIOXfrqV6ciZW6rtg', '客户', '5', '1909533328743206912', '1', '2025-04-08 17:06:37', '2025-04-08 17:06:37', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909804815215628288', 'oJTwA7brhXvm4SIbq6RllK-eGWb4', '客户', '5', '1909804815215628288', '1', '2025-04-09 11:05:25', '2025-04-09 11:05:25', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909804877870141440', 'oJTwA7QNJNNTNlxmkqaMBsthFtYs', '客户', '5', '1909804877870141440', '1', '2025-04-09 11:05:40', '2025-04-09 11:05:40', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909804879849852928', 'oJTwA7TK1zYQZMJEVuTh0uOv8cwo', '客户', '5', '1909804879849852928', '1', '2025-04-09 11:05:40', '2025-04-09 11:05:40', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909804917976076288', 'oJTwA7XW5QDt2vKljne2YfVWD0Sk', '客户', '5', '1909804917976076288', '1', '2025-04-09 11:05:49', '2025-04-09 11:05:49', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909804938591080448', 'oJTwA7bsHGXyQ5R6cvGvb-Xfldko', '客户', '5', '1909804938591080448', '1', '2025-04-09 11:05:54', '2025-04-09 11:05:54', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909804951098494976', 'oJTwA7bTouRnpCeqPDVVt0O5IHE8', '客户', '5', '1909804951098494976', '1', '2025-04-09 11:05:57', '2025-04-09 11:05:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909805212437188608', 'oJTwA7fF_Nr408RX0pVmaw7wxK3U', '客户', '5', '1909805212437188608', '1', '2025-04-09 11:06:59', '2025-04-09 11:06:59', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909805218229522432', 'oJTwA7XOdHyLNEOmV6Uy_lVcQrlM', '客户', '5', '1909805218229522432', '1', '2025-04-09 11:07:01', '2025-04-09 11:07:01', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909805251985281024', 'oJTwA7YfOZlTCZBfZCEGRtGD00ZQ', '客户', '5', '1909805251985281024', '1', '2025-04-09 11:07:09', '2025-04-09 11:07:09', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909805365927743488', 'oJTwA7Vd9FMNllLqJDxsAGqXuIjc', '客户', '5', '1909805365927743488', '1', '2025-04-09 11:07:36', '2025-04-09 11:07:36', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909866696504643584', 'oJTwA7WGqcmeSHjuCRMo8-h1l96A', '客户', '5', '1909866696504643584', '1', '2025-04-09 15:11:18', '2025-04-09 15:11:18', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909908407008235520', 'oJTwA7cQYpuMkiKJQQOKo593snII', '客户', '5', '1909908407008235520', '1', '2025-04-09 17:57:03', '2025-04-09 17:57:03', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909918057489371136', 'oJTwA7SIXlEyV-nL9cZnmOX0gkx8', '客户', '5', '1909918057489371136', '1', '2025-04-09 18:35:24', '2025-04-09 18:35:24', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1909964167952076800', 'oJTwA7f-uSm-Cu13qBISUnla1Avc', '客户', '5', '1909964167952076800', '1', '2025-04-09 21:38:37', '2025-04-09 21:38:37', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910222440819200000', 'oJTwA7V7NsG9bi7YjFSWGRy1utls', '客户', '5', '1910222440819200000', '1', '2025-04-10 14:44:54', '2025-04-10 14:44:54', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910547740488765440', 'oJTwA7VBWUimpbMVtEh2yJn9_oJk', '客户', '5', '1910547740488765440', '1', '2025-04-11 12:17:32', '2025-04-11 12:17:32', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910596915809095680', 'oJTwA7X9EoS-om0c7-XdBZiYRbu8', '客户', '5', '1910596915809095680', '1', '2025-04-11 15:32:56', '2025-04-11 15:32:56', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910600419571273728', 'oJTwA7baC04G2N8E9Ojx6fn7oxKU', '客户', '5', '1910600419571273728', '1', '2025-04-11 15:46:52', '2025-04-11 15:46:52', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910621275886325760', 'oJTwA7Z2iSSsGrldhon34rnOEOYk', '客户', '5', '1910621275886325760', '1', '2025-04-11 17:09:44', '2025-04-11 17:09:44', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910634404707962880', 'oJTwA7WjjnZlcttcC-RnPv4NCNEc', '客户', '5', '1910634404707962880', '1', '2025-04-11 18:01:54', '2025-04-11 18:01:54', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910910619125682176', 'oJTwA7csAbMUzcXuK1o5swjVDJak', '客户', '5', '1910910619125682176', '1', '2025-04-12 12:19:29', '2025-04-12 12:19:29', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910919163946340352', 'oJTwA7eWO_6surqdby7aNco-1IUo', '客户', '5', '1910919163946340352', '1', '2025-04-12 12:53:26', '2025-04-12 12:53:26', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910952205746311168', 'oJTwA7VgSSEX2Yy0u510KEDpCHPY', '客户', '5', '1910952205746311168', '1', '2025-04-12 15:04:44', '2025-04-12 15:04:44', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1910968171649699840', 'oJTwA7dnMXDq1fgvNpSMSfr1mhpo', '客户', '5', '1910968171649699840', '1', '2025-04-12 16:08:11', '2025-04-12 16:08:11', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911052376706519040', 'oJTwA7Q8qvN8GWNJd2i2iVL5px-4', '客户', '5', '1911052376706519040', '1', '2025-04-12 21:42:47', '2025-04-12 21:42:47', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911368631862824960', 'oJTwA7W00faaMwBAtlw86t-AwKJI', '客户', '5', '1911368631862824960', '1', '2025-04-13 18:39:28', '2025-04-13 18:39:28', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911444799437803520', 'oJTwA7eR6H2K0vzMZmnkRdkI5mA4', '客户', '5', '1911444799437803520', '1', '2025-04-13 23:42:07', '2025-04-13 23:42:07', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911686789370875904', 'oJTwA7cTwSWF8-0s1BljLNPhpdYo', '客户', '5', '1911686789370875904', '1', '2025-04-14 15:43:42', '2025-04-14 15:43:42', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911734388350128128', 'oJTwA7U1Oh7g7UelrsUbmjWUn4KY', '客户', '5', '1911734388350128128', '1', '2025-04-14 18:52:51', '2025-04-14 18:52:51', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911807113273937920', 'oJTwA7R2v005v4NyfZ6j2mw80JxA', '客户', '5', '1911807113273937920', '1', '2025-04-14 23:41:50', '2025-04-14 23:41:50', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911819501209522176', 'oJTwA7TtWTqWsf46-k8CtJt91we4', '客户', '5', '1911819501209522176', '1', '2025-04-15 00:31:03', '2025-04-15 00:31:03', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1911820044334141440', 'oJTwA7V46uwHdJKeotjMa3NUYMsM', '客户', '5', '1911820044334141440', '1', '2025-04-15 00:33:13', '2025-04-15 00:33:13', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912043355425083392', 'oJTwA7TaIZFdhWJK0hJCVGOdl5Dc', '客户', '5', '1912043355425083392', '1', '2025-04-15 15:20:34', '2025-04-15 15:20:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912054264604266496', 'oJTwA7b_srlkpgSyIWpUxWnwB8lQ', '客户', '5', '1912054264604266496', '1', '2025-04-15 16:03:55', '2025-04-15 16:03:55', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912084686520324096', 'oJTwA7W4zPAWBHFgLcevxjVhIy5M', '客户', '5', '1912084686520324096', '1', '2025-04-15 18:04:48', '2025-04-15 18:04:48', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912085388420321280', 'oJTwA7doyuV0Qfgc4hEdn9zwXgGY', '客户', '5', '1912085388420321280', '1', '2025-04-15 18:07:36', '2025-04-15 18:07:36', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912087531038576640', 'oJTwA7f3nx9wfXnLtJ-hnezvo77M', '客户', '5', '1912087531038576640', '1', '2025-04-15 18:16:07', '2025-04-15 18:16:07', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912101339849166848', 'oJTwA7UnW-LGadijy9yC0xen_kdg', '客户', '5', '1912101339849166848', '1', '2025-04-15 19:10:59', '2025-04-15 19:10:59', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912157406918479872', 'oJTwA7VgFcvQbuqbQulnwetDfVJI', '客户', '5', '1912157406918479872', '1', '2025-04-15 22:53:46', '2025-04-15 22:53:46', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912363050325905408', 'oJTwA7ZBt5BAdnx_K3JVAFCjzqSw', '客户', '5', '1912363050325905408', '1', '2025-04-16 12:30:56', '2025-04-16 12:30:56', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912366043649675264', 'oJTwA7SS_eRQi9GT-o_s7LHzNBB8', '客户', '5', '1912366043649675264', '1', '2025-04-16 12:42:49', '2025-04-16 12:42:49', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912392054328135680', 'oJTwA7VNqW1BTzOPqf7Rs1SA1r4c', '客户', '5', '1912392054328135680', '1', '2025-04-16 14:26:11', '2025-04-16 14:26:11', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912488306189406208', 'oJTwA7QbOMiMyTZpgK9kxP37UGu8', '客户', '5', '1912488306189406208', '1', '2025-04-16 20:48:39', '2025-04-16 20:48:39', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912527780780838912', 'oJTwA7SoILkOaEIP0bDGNtbvJ4x0', '客户', '5', '1912527780780838912', '1', '2025-04-16 23:25:30', '2025-04-16 23:25:30', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912705713004744704', 'oJTwA7SDBuGXN_PqznxFNoDRtMUU', '客户', '5', '1912705713004744704', '1', '2025-04-17 11:12:33', '2025-04-17 11:12:33', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912709869585174528', 'oJTwA7cHXymX0kgKw6uL8mBGHwMU', '客户', '5', '1912709869585174528', '1', '2025-04-17 11:29:04', '2025-04-17 11:29:04', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912720395597910016', 'oJTwA7ZGq_hwjzACk7MesYCP99tg', '客户', '5', '1912720395597910016', '1', '2025-04-17 12:10:53', '2025-04-17 12:10:53', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912722811370213376', 'oJTwA7TrHpcAIU4xhR9zBMdrTpK0', '客户', '5', '1912722811370213376', '1', '2025-04-17 12:20:29', '2025-04-17 12:20:29', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912735573836173312', 'oJTwA7Q955lUkkANthA2MaY-7tjA', '客户', '5', '1912735573836173312', '1', '2025-04-17 13:11:12', '2025-04-17 13:11:12', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912759361319604224', 'oJTwA7UAo7O8AHc5sDyWAvKysPt8', '客户', '5', '1912759361319604224', '1', '2025-04-17 14:45:43', '2025-04-17 14:45:43', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912771498163179520', 'oJTwA7WAuyA4Kf1SIET410vRgrXU', '客户', '5', '1912771498163179520', '1', '2025-04-17 15:33:57', '2025-04-17 15:33:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912868286215884800', 'oJTwA7ehcueQrjXu_LFyBprZOh4A', '客户', '5', '1912868286215884800', '1', '2025-04-17 21:58:33', '2025-04-17 21:58:33', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1912870259434590208', 'oJTwA7c1rlxMK508SO5zHPS0EaPA', '客户', '5', '1912870259434590208', '1', '2025-04-17 22:06:24', '2025-04-17 22:06:24', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1913796215951200256', 'oJTwA7QhgvJA24xTR1xxWKn8uy1U', '客户', '5', '1913796215951200256', '1', '2025-04-20 11:25:49', '2025-04-20 11:25:49', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1913835430453841920', 'oJTwA7R2dBJW4wlvSYiDZas0uZog', '客户', '5', '1913835430453841920', '1', '2025-04-20 14:01:38', '2025-04-20 14:01:38', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1913839695968735232', 'oJTwA7QI8ifKlgW-vnh9tAy6w_lo', '客户', '5', '1913839695968735232', '1', '2025-04-20 14:18:35', '2025-04-20 14:18:35', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1913873850697912320', 'oJTwA7WQMWzhwq8hWLDI5mQKyJls', '客户', '5', '1913873850697912320', '1', '2025-04-20 16:34:18', '2025-04-20 16:34:18', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1913885589392986112', 'oJTwA7QhoOc2z1rzDaZXo_HSkI14', '客户', '5', '1913885589392986112', '1', '2025-04-20 17:20:57', '2025-04-20 17:20:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914170142410018816', 'oJTwA7Tc2PIo704I967d_8_osrec', '客户', '5', '1914170142410018816', '1', '2025-04-21 12:11:40', '2025-04-21 12:11:40', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914171707275481088', 'oJTwA7bT9COX0Xff9XPcuxWbxMls', '客户', '5', '1914171707275481088', '1', '2025-04-21 12:17:53', '2025-04-21 12:17:53', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914567969413926912', 'oJTwA7Uh3uBsh9Ff80_SmsXkVPDI', '客户', '5', '1914567969413926912', '1', '2025-04-22 14:32:29', '2025-04-22 14:32:29', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914568998016651264', 'oJTwA7eFpdx3BejoYnBxAgE8q1nY', '客户', '5', '1914568998016651264', '1', '2025-04-22 14:36:34', '2025-04-22 14:36:34', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914573446587748352', 'oJTwA7SSRke6g4aLgbIMyeIj6AJA', '客户', '5', '1914573446587748352', '1', '2025-04-22 14:54:15', '2025-04-22 14:54:15', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914579129496375296', 'oJTwA7V0hjdBY0N0su4M1KUTw-xw', '客户', '5', '1914579129496375296', '1', '2025-04-22 15:16:50', '2025-04-22 15:16:50', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1914899787300343808', 'oJTwA7dj-v3huFT54GPLNX9LF07w', '客户', '5', '1914899787300343808', '1', '2025-04-23 12:31:01', '2025-04-23 12:31:01', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1915055041014796288', 'oJTwA7cJDVo0KCNy9ZVfeWbVa34g', '客户', '5', '1915055041014796288', '1', '2025-04-23 22:47:56', '2025-04-23 22:47:56', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1915582188326359040', 'oJTwA7W-R7eCaL6gAmc-Y8rJIyLA', '客户', '5', '1915582188326359040', '1', '2025-04-25 09:42:38', '2025-04-25 09:42:38', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1915588509389950976', 'oJTwA7TUDgH_kN-VTsWwaxLoypus', '客户', '5', '1915588509389950976', '1', '2025-04-25 10:07:45', '2025-04-25 10:07:45', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1915628459166666752', 'oJTwA7Y-S0a1VY4YO7ZSDOtfs41k', '客户', '5', '1915628459166666752', '1', '2025-04-25 12:46:30', '2025-04-25 12:46:30', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916013403336478720', 'oJTwA7c_GYxQbi_EP5Fys8X8_Rms', '客户', '5', '1916013403336478720', '1', '2025-04-26 14:16:07', '2025-04-26 14:16:07', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916021791428579328', 'oJTwA7YC2bsv2LfYIPjDxHUYIqKA', '客户', '5', '1916021791428579328', '1', '2025-04-26 14:49:27', '2025-04-26 14:49:27', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916031785213497344', 'oJTwA7TE2UtxBIqE-IZUGfs8lqus', '客户', '5', '1916031785213497344', '1', '2025-04-26 15:29:10', '2025-04-26 15:29:10', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916085333095747584', 'oJTwA7QZFH4qmHk1Pxxjc8RtlG5o', '客户', '5', '1916085333095747584', '1', '2025-04-26 19:01:57', '2025-04-26 19:01:57', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916128300732911616', 'oJTwA7VFbKXMs0Zh03ly0zW4BPlw', '客户', '5', '1916128300732911616', '1', '2025-04-26 21:52:41', '2025-04-26 21:52:41', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916275640609411072', 'oJTwA7eyYIMD1q9d9UHjaeBB_IwY', '客户', '5', '1916275640609411072', '1', '2025-04-27 07:38:10', '2025-04-27 07:38:10', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916302023842729984', 'oJTwA7fLjidDInlC0lgEkGUkHjDk', '客户', '5', '1916302023842729984', '1', '2025-04-27 09:23:00', '2025-04-27 09:23:00', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916321462449475584', 'oJTwA7R49HYsY2mZ-66lGaVifXJI', '客户', '5', '1916321462449475584', '1', '2025-04-27 10:40:14', '2025-04-27 10:40:14', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916400427407118336', 'oJTwA7eg1NJlMdK-SLXgmGZ1rb3s', '客户', '5', '1916400427407118336', '1', '2025-04-27 15:54:01', '2025-04-27 15:54:01', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916410483628445696', 'oJTwA7SQhe16IvWbDrsWAclxQPT8', '客户', '5', '1916410483628445696', '1', '2025-04-27 16:33:59', '2025-04-27 16:33:59', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916473011544920064', 'oJTwA7X__agfdU2DUvCr_5GKlxcY', '客户', '5', '1916473011544920064', '1', '2025-04-27 20:42:27', '2025-04-27 20:42:27', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916664889905123328', 'oJTwA7f-P-SXAedctxBH2vARRnZU', '客户', '5', '1916664889905123328', '1', '2025-04-28 09:24:54', '2025-04-28 09:24:54', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916665006481608704', 'oJTwA7Y2RO4PMS1_wr2dHfk1kCV0', '客户', '5', '1916665006481608704', '1', '2025-04-28 09:25:22', '2025-04-28 09:25:22', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916665032276578304', 'oJTwA7dz9etv09EbozyJdCD1f7to', '客户', '5', '1916665032276578304', '1', '2025-04-28 09:25:28', '2025-04-28 09:25:28', 0);
INSERT INTO `crm_customer_identity` VALUES ('1801458632269893632', NULL, '1916667241085145088', 'oJTwA7atDrkE0IZJ1CIQWwBUQmnE', '客户', '5', '1916667241085145088', '1', '2025-04-28 09:34:15', '2025-04-28 09:34:15', 0);
INSERT INTO `crm_customer_identity` VALUES ('6345824413764157440', 'null', 'null', 'nan', '代理商', '4', '1945653500549664769', '1', '2025-07-17 09:15:17', '2025-07-17 09:15:17', 0);
INSERT INTO `crm_customer_identity` VALUES ('6345824413764157440', 'null', 'null', 'nan', '代理商', '4', '1945654328639492097', '1', '2025-07-17 09:18:35', '2025-07-17 09:18:35', 0);
INSERT INTO `crm_customer_identity` VALUES ('6345824413764157440', 'null', 'null', 'nan', '代理商', '4', '1945667691113222145', '1', '2025-07-17 10:11:41', '2025-07-17 10:11:41', 0);
INSERT INTO `crm_customer_identity` VALUES ('6345824413764157440', 'null', 'null', 'nan', '代理商', '4', '1945735231181033473', '1', '2025-07-17 14:40:03', '2025-07-17 14:40:03', 0);
INSERT INTO `crm_customer_identity` VALUES ('6345824413764157440', 'null', 'null', 'nanb', '代理商', '4', '1945759112415547393', '1', '2025-07-17 16:14:57', '2025-07-17 16:14:57', 0);
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_commission_config
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_commission_config`;
CREATE TABLE `crm_dis_commission_config` (
  `serial_no` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(255) NOT NULL COMMENT '设置规则的租户',
  `super_tenant_rate` decimal(10,2) DEFAULT '0.00' COMMENT '运营平台分佣比例',
  `tenant_rate` decimal(10,2) DEFAULT '0.00' COMMENT '租户平台分佣比例',
  `sys_agent_rate` decimal(10,2) DEFAULT '0.00' COMMENT '代理商分佣比例',
  `customer_rate` decimal(10,2) DEFAULT NULL COMMENT '消费者返利比例',
  `merchant_no` varchar(255) DEFAULT NULL COMMENT '设置规则的商户',
  `merchant_sharing_rate` decimal(10,2) DEFAULT '0.00' COMMENT '商户让利比例',
  PRIMARY KEY (`tenant_id`) USING BTREE,
  UNIQUE KEY `tenant_id_UNIQUE` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='参与分佣设置表';

-- ----------------------------
-- Records of crm_dis_commission_config
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_commission_config` VALUES ('1850132129279774720', '1846190237240397821', 99.00, 74.00, 48.00, 65.00, NULL, 40.00);
INSERT INTO `crm_dis_commission_config` VALUES ('1860579819910205440', '1846190237240397824', 5.00, 5.00, 20.00, 70.00, '1846556049375875073', 30.00);
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_commission_goods
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_commission_goods`;
CREATE TABLE `crm_dis_commission_goods` (
  `serial_no` int(11) NOT NULL,
  `goods_category_no` varchar(45) DEFAULT NULL COMMENT '商品类型',
  `merchant_no` varchar(45) DEFAULT NULL COMMENT '商户ID',
  `brokerage_rule_no` varchar(45) DEFAULT NULL COMMENT '分佣规则ID',
  `del_flag` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_dis_commission_goods
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_commission_goods` VALUES (0, '1857008750297300993', '1737853502828482561', '1850379926298038272', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_commission_journal
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_commission_journal`;
CREATE TABLE `crm_dis_commission_journal` (
  `serial_no` varchar(255) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL COMMENT '租户号',
  `order_no` varchar(45) DEFAULT NULL COMMENT '订单ID',
  `merchant_no` varchar(45) DEFAULT NULL COMMENT '门店ID',
  `good_no` varchar(45) DEFAULT NULL COMMENT '商品ID',
  `policy_no` varchar(45) DEFAULT NULL COMMENT '政策ID',
  `rule_no` varchar(45) DEFAULT NULL COMMENT '规则ID\n',
  `dis_amount` decimal(10,2) DEFAULT NULL COMMENT '分佣金额\n',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分佣时间',
  `trigger_event_code` varchar(45) DEFAULT NULL COMMENT '触发分佣的事件编码, 收款: PAY_SUCCESS, 发货: TRANSFER',
  `exclude_fee_type` varchar(45) DEFAULT NULL COMMENT '不进行分佣的资金: 运费: carriage, 自定义: custom',
  `exclude_custom_per` decimal(10,0) DEFAULT NULL COMMENT '自定义扣除费用比例',
  `dis_level` int(11) DEFAULT NULL COMMENT '几级分佣一级分佣:1, 二级分佣:2',
  `sys_agent_no` varchar(45) DEFAULT NULL COMMENT '分销员ID\\n',
  `status` int(11) DEFAULT NULL COMMENT '佣金是否打入分销员账户打入:1/未打入:0',
  `pay_amount` decimal(10,0) DEFAULT NULL COMMENT '订单金额',
  `rate` decimal(10,0) DEFAULT NULL COMMENT '分佣比例',
  `point_days` int(11) DEFAULT NULL COMMENT '分佣条件达成后隔几天结算佣金',
  `good_sku_no` varchar(45) DEFAULT NULL,
  `goods_sku_amount` decimal(10,0) DEFAULT NULL,
  `goods_category_no` varchar(45) DEFAULT NULL,
  `is_preview` int(11) DEFAULT NULL COMMENT '是否是预分佣1:0',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分佣金明细';

-- ----------------------------
-- Records of crm_dis_commission_journal
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_commission_journal` VALUES ('1850382729984741376', NULL, NULL, '1737853502828482561', '', '1850142522513428480', '1850379926298038272', 100.00, NULL, 'reprehenderit fugiat veniam laborum in', 'nisi', 50, 1, '1848765426037821441', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1850402465288163328', NULL, '11111', '1737853502828482561', '1111111', '1850142522513428480', '1850379926298038272', 74520.00, NULL, 'reprehenderit fugiat veniam laborum in', 'nisi', 50, 1, '1848765426037821441', NULL, NULL, NULL, NULL, NULL, 2000, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1850403791690665984', NULL, '11111', '1737853502828482561', '1111111', '1850142522513428480', '1850379926298038272', 745.20, NULL, 'reprehenderit fugiat veniam laborum in', 'nisi', 50, 1, '1848765426037821441', NULL, NULL, NULL, NULL, '2222', 2000, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1850514160065056768', NULL, '1111222', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 2.43, NULL, 'reprehenderit fugiat veniam laborum in', 'nisi', 50, 1, '1849479857050882048', NULL, NULL, 48, NULL, NULL, 11, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1850517919918854144', NULL, '1111222', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 2.43, NULL, 'reprehenderit fugiat veniam laborum in', 'nisi', 50, 1, '1849479857050882048', NULL, NULL, 48, NULL, NULL, 11, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1850519431231442944', NULL, '1111222', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 2.43, NULL, 'reprehenderit fugiat veniam laborum in', 'nisi', 50, 1, '1849479857050882048', NULL, NULL, 48, NULL, NULL, 11, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857634878980493312', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857653910118993920', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857654131821514752', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857654277904928768', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857654384561885184', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857654506024734720', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857654827593633792', NULL, 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857662430977593344', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857662846851223552', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857663132877590528', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857663159096184832', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857663189706215424', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857665118159114240', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857665184022269952', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857665247075241984', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.00, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857665681206677504', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, 'string', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857667109102948352', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, NULL, 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857667149934497792', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 196.42, NULL, 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857667882176090112', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-16 14:11:51', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857668335907508224', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-16 14:13:39', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857669215520165888', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-16 14:17:09', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857669217277579264', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 196.42, '2024-11-16 14:17:09', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857669533964308480', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-16 14:18:25', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857669535008690176', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 196.42, '2024-11-16 14:18:25', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857702999368011776', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-16 16:31:24', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857703000378839040', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 196.42, '2024-11-16 16:31:24', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857705934579699712', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-16 16:43:04', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1857705936962064384', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 196.42, '2024-11-16 16:43:04', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1858801563544457216', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 97.15, '2024-11-19 17:16:42', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1858801565440282624', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 196.42, '2024-11-19 17:16:42', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1858804476652163072', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.97, '2024-11-19 17:28:16', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1858804478426353664', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.96, '2024-11-19 17:28:17', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1858804542808920064', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.97, '2024-11-19 17:28:32', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1858804543878467584', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.96, '2024-11-19 17:28:32', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859047561403437056', NULL, NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 4.36, '2024-11-20 09:34:12', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859047651287371776', NULL, NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 3.26, '2024-11-20 09:34:34', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859047688931250176', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.97, '2024-11-20 09:34:43', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859047715674132480', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.14, '2024-11-20 09:34:49', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859047725581078528', NULL, NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 2.86, '2024-11-20 09:34:51', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859050376955826176', NULL, NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 4.36, '2024-11-20 09:45:24', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051340702027776', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 4.36, '2024-11-20 09:49:13', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051342748848128', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 3.26, '2024-11-20 09:49:14', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051344204271616', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.97, '2024-11-20 09:49:14', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051345286402048', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.14, '2024-11-20 09:49:14', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051346204954624', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 2.86, '2024-11-20 09:49:15', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051414983151616', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 4.36, '2024-11-20 09:49:31', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051415935258624', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 3.26, '2024-11-20 09:49:31', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051416853811200', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 0.97, '2024-11-20 09:49:32', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051417914970112', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.14, '2024-11-20 09:49:32', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 48, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859051418820939776', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 2.86, '2024-11-20 09:49:32', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 99, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859106285421727744', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 0.17, '2024-11-20 13:27:33', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859106332410515456', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 0.17, '2024-11-20 13:27:44', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859106346994110464', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.06, '2024-11-20 13:27:48', 'PAY_SUCCESS', 'nisi', 50, 1, '1737853500064509954', NULL, NULL, 70, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859106352207630336', '1846190237240397824', 'string', '1737853502828482561', NULL, '1850142522513428480', '1850379926298038272', 1.25, '2024-11-20 13:27:49', 'PAY_SUCCESS', 'nisi', 50, 2, '1847267145117995008', NULL, NULL, 70, NULL, NULL, NULL, '1746556310373142529', 0);
INSERT INTO `crm_dis_commission_journal` VALUES ('1859106357240795136', '6345824413764157440', NULL, NULL, NULL, '1850142522513428480', '1850379926298038272', 0.66, '2024-11-20 13:27:50', 'PAY_SUCCESS', 'nisi', 50, NULL, NULL, NULL, NULL, 5, NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_commission_policy
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_commission_policy`;
CREATE TABLE `crm_dis_commission_policy` (
  `serial_no` varchar(45) NOT NULL,
  `tenant_id` varchar(45) DEFAULT NULL COMMENT '租户CODE',
  `policy_name` varchar(45) DEFAULT NULL COMMENT '分佣政策名称',
  `start_time` datetime DEFAULT NULL COMMENT '政策开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '政策结束时间',
  `exclude_fee_type` varchar(45) DEFAULT NULL COMMENT '不进行分佣的资金: 运费: carriage, 自定义: custom',
  `exclude_custom_per` decimal(5,2) DEFAULT NULL COMMENT '自定义扣除费用比例',
  `trigger_event_code` varchar(45) DEFAULT NULL COMMENT '触发分佣的事件编码, 收款: PAY_SUCCESS, 发货: TRANSFER',
  `trigger_event_after_date` int(11) DEFAULT '0' COMMENT '触发事件后几天',
  `remark` varchar(45) DEFAULT NULL COMMENT '备注',
  `status` int(11) DEFAULT '0' COMMENT '是否启用 1,0',
  `del_flag` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(45) DEFAULT NULL COMMENT '创建用户',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_dis_commission_policy
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_commission_policy` VALUES ('1850142522513428480', '6345824413764157440', '周资做品整全干', '2018-10-20 04:11:01', '2026-03-30 17:43:27', 'nisi', 50.00, 'PAY_SUCCESS', 0, 'Duis reprehenderit est ullamco', 1, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_policy` VALUES ('1850148191815733248', '6345824413764157440', '周资做品整全干', '2018-10-20 04:11:01', '2006-03-30 17:43:27', 'nisi', 50.00, 'PAY_SUCCESS', 0, 'Duis reprehenderit est ullamco', 0, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_policy` VALUES ('1850157134013140992', '6345824413764157440', '周资做品整全干', '2018-10-20 04:11:01', '2006-03-30 17:43:27', 'nisi', 50.00, 'PAY_SUCCESS', 0, 'Duis reprehenderit est ullamco', 0, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_policy` VALUES ('1850157417816526848', '6345824413764157440', '周资做品整全干', '2018-10-20 04:11:01', '2006-03-30 17:43:27', 'nisi', 50.00, 'PAY_SUCCESS', 0, 'Duis reprehenderit est ullamco', 0, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_policy` VALUES ('1850158169460969472', '1846190237240397824', '周资做品整全干', '2018-10-20 04:11:01', '2006-03-30 17:43:27', 'nisi', 50.00, 'PAY_SUCCESS', 0, 'Duis reprehenderit est ullamco', 0, NULL, NULL, NULL, '1535984304528691200');
INSERT INTO `crm_dis_commission_policy` VALUES ('1850176876069392384', '1846190237240397824', '周资做品整全干', '2018-10-20 04:11:01', '2006-03-30 17:43:27', 'nisi', 50.00, 'PAY_SUCCESS', 0, 'Duis reprehenderit est ullamco', 0, NULL, NULL, NULL, '1535984304528691200');
INSERT INTO `crm_dis_commission_policy` VALUES ('1859884030217031680', '1846190237240397824', 'wew镇村务', '2024-11-22 00:00:00', '2024-12-24 00:00:00', NULL, NULL, 'consignee_success', 1, '1232', 0, NULL, NULL, NULL, '1846190238037315584');
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_commission_rule
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_commission_rule`;
CREATE TABLE `crm_dis_commission_rule` (
  `serial_no` varchar(45) NOT NULL COMMENT '商品分佣比例ID',
  `tenant_id` varchar(45) DEFAULT NULL,
  `brokerage_policy_no` varchar(45) DEFAULT NULL COMMENT '分佣政策ID',
  `first_sale_per` int(11) DEFAULT NULL COMMENT '一级分佣比例',
  `second_sale_per` int(11) DEFAULT NULL COMMENT '二级分佣比例',
  `goods_category_no` varchar(45) DEFAULT NULL COMMENT '商品类型',
  `merchant_no` varchar(45) DEFAULT NULL COMMENT '门店ID',
  `del_flag` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分佣规则配置表';

-- ----------------------------
-- Records of crm_dis_commission_rule
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_commission_rule` VALUES ('1850379926298038272', '6345824413764157440', '1850142522513428480', 46, 54, '1746556310373142529', '1737853502828482561', NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_rule` VALUES ('1860247138026524672', '1846190237240397824', '1850158169460969472', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_rule` VALUES ('1862704541141700608', '1846190237240397824', '1850158169460969472', 1, 1, '1857008750297300993', '1862689121860980736', NULL, NULL, NULL);
INSERT INTO `crm_dis_commission_rule` VALUES ('1863845014422032384', '1846190237240397824', '1850158169460969472', 1, 1, '0', '1850310256933081088', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_invite_relation
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_invite_relation`;
CREATE TABLE `crm_dis_invite_relation` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `tenant_id` varchar(255) DEFAULT NULL,
  `customer_no` varchar(32) NOT NULL COMMENT '被邀请人序列号',
  `sys_agent_no` varchar(255) DEFAULT '-1' COMMENT '被邀请人所属代理商： -1 说明没有代理商',
  `parent_no` varchar(32) NOT NULL COMMENT '被邀请人的父级序列号',
  `invite_level` int(11) NOT NULL DEFAULT '1' COMMENT '邀请等级 1、2、3级',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请关系表';

-- ----------------------------
-- Records of crm_dis_invite_relation
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_invite_relation` VALUES ('', '1846190237240397824', '1845863547842727936', NULL, '1849479857050882048', 1, '2024-10-27 10:10:55');
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_model
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_model`;
CREATE TABLE `crm_dis_model` (
  `tenant_id` varchar(32) NOT NULL COMMENT '租户CODE',
  `merchant_no` varchar(255) DEFAULT NULL COMMENT '商户号',
  `model` varchar(45) DEFAULT NULL COMMENT '模型类型:一级分销: level1, 二级分销: level2, 链路2+1: level2_1',
  `quit_current_limit` int(11) DEFAULT NULL COMMENT '退出团队机制',
  `first_invite_brokerage` int(11) DEFAULT '0' COMMENT '一级推荐奖励',
  `second_invite_brokerage` int(11) DEFAULT '0' COMMENT '二级推荐奖励',
  UNIQUE KEY `tenant_id_UNIQUE` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分销模型表';

-- ----------------------------
-- Records of crm_dis_model
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_model` VALUES ('1846190237240397824', NULL, 'level2_1', 3, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_policy_merchant
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_policy_merchant`;
CREATE TABLE `crm_dis_policy_merchant` (
  `serial_no` varchar(45) NOT NULL,
  `brokerage_policy_no` varchar(45) DEFAULT NULL COMMENT '分佣政策ID',
  `tenant_id` varchar(45) DEFAULT NULL COMMENT '租户id',
  `merchant_no` varchar(45) DEFAULT NULL COMMENT '商户ID',
  PRIMARY KEY (`serial_no`) USING BTREE,
  UNIQUE KEY `merchant_no_UNIQUE` (`merchant_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_dis_policy_merchant
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_policy_merchant` VALUES ('1850310256022917120', 'do magna', '6345824413764157440', 'deserunt');
INSERT INTO `crm_dis_policy_merchant` VALUES ('1850310256253603840', 'do magna', '6345824413764157440', '2');
INSERT INTO `crm_dis_policy_merchant` VALUES ('1850310256484290560', 'do magna', '6345824413764157440', '3');
INSERT INTO `crm_dis_policy_merchant` VALUES ('1850310256706588672', 'do magna', '6345824413764157440', '4');
INSERT INTO `crm_dis_policy_merchant` VALUES ('1850310256933081088', '1850158169460969472', '6345824413764157440', '1846556049375875073');
INSERT INTO `crm_dis_policy_merchant` VALUES ('1862689121860980736', '1850158169460969472', '1846190237240397824', '1846556049375875074');
COMMIT;

-- ----------------------------
-- Table structure for crm_dis_team_relation
-- ----------------------------
DROP TABLE IF EXISTS `crm_dis_team_relation`;
CREATE TABLE `crm_dis_team_relation` (
  `serial_no` varchar(255) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `prarent_sys_agent_no` varchar(45) DEFAULT '-1' COMMENT '上级分销员ID',
  `sys_agent_no` varchar(45) DEFAULT NULL COMMENT '分销员ID(代理商ID)',
  `dis_agent_type` int(11) DEFAULT '0' COMMENT '分销商类型 1(老板), 0(分销员)',
  `type` varchar(255) DEFAULT '1' COMMENT '成员类型：1 自己成员 2 贡献成员',
  PRIMARY KEY (`serial_no`) USING BTREE,
  UNIQUE KEY `sys_agent_no_UNIQUE` (`sys_agent_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理/分销团队关系表';

-- ----------------------------
-- Records of crm_dis_team_relation
-- ----------------------------
BEGIN;
INSERT INTO `crm_dis_team_relation` VALUES ('1850496253327183872', '1846190237240397824', '1847267145117995008', '1737853500064509954', 0, NULL);
INSERT INTO `crm_dis_team_relation` VALUES ('1859437025229410304', '1846190237240397824', '-1', '1859437024113725441', 1, NULL);
INSERT INTO `crm_dis_team_relation` VALUES ('1865794233533861888', '1846190237240397824', '-1', '1865794231566733313', 1, '1');
INSERT INTO `crm_dis_team_relation` VALUES ('1869649135171407872', '1846190237240397824', '-1', '1869649134244466689', 1, '1');
INSERT INTO `crm_dis_team_relation` VALUES ('1869676940944871424', '1846190237240397824', '-1', '1869676939992764416', 1, '1');
INSERT INTO `crm_dis_team_relation` VALUES ('1869978138621120512', '1846190237240397824', '-1', '1869978137677402113', 1, '1');
COMMIT;

-- ----------------------------
-- Table structure for crm_equity
-- ----------------------------
DROP TABLE IF EXISTS `crm_equity`;
CREATE TABLE `crm_equity` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  `tenant_id` varchar(64) DEFAULT NULL COMMENT '租户',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户编码',
  `name` varchar(64) DEFAULT NULL COMMENT '权益名称',
  `type` varchar(11) DEFAULT NULL COMMENT '条件类型：1:数字资产 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡   6：账户-联合曲线(BC)  ) 7：满减 8：权限 9：会员等级',
  `type_no` varchar(32) DEFAULT NULL COMMENT '资产类型编号',
  `total_amount` decimal(12,2) DEFAULT NULL COMMENT '满减总金额',
  `value` int(11) DEFAULT NULL COMMENT '值：满减金额、赠送金额、赠送数量 折扣值',
  `over_flag` varchar(4) DEFAULT NULL COMMENT '叠加使用标识(1:是,2:否)',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级权益利益表';

-- ----------------------------
-- Records of crm_equity
-- ----------------------------
BEGIN;
INSERT INTO `crm_equity` VALUES ('1739268685081759745', NULL, '2023-12-25 20:55:43', NULL, '2023-12-25 20:55:43', 0, '1737841274272223232', '1737853502828482561', '花尖墨-10个劳动价值', '6', '1739222480528232449', NULL, 10, NULL, '奖励10个劳动价值');
INSERT INTO `crm_equity` VALUES ('1739268884390891521', NULL, '2023-12-25 20:56:30', NULL, '2023-12-25 20:56:30', 0, '1737841274272223232', '1737853502828482561', '1枚花尖墨徽章#1', '1', '1739251040504647680', NULL, 1, NULL, '花尖墨1号徽章1个');
INSERT INTO `crm_equity` VALUES ('1740030114009411586', NULL, '2023-12-27 23:21:22', NULL, '2023-12-27 23:21:22', 0, '1737841274272223232', '1739224452920684545', '1枚s11eDao徽章#1', '1', '1739990744309043200', NULL, 1, NULL, '测试');
INSERT INTO `crm_equity` VALUES ('1848749450510757890', NULL, '2024-10-22 23:33:12', NULL, '2024-10-22 23:33:12', 0, '1846190237240397824', '1846556049375875073', '权益名称', '9', '资产类型编号', 100.00, 100, '2', '备注');
INSERT INTO `crm_equity` VALUES ('1848750182601355266', NULL, '2024-10-22 23:36:06', NULL, '2024-10-22 23:36:06', 0, '1846190237240397824', '1846556049375875073', '1', '2', '0', NULL, 1, NULL, '1');
COMMIT;

-- ----------------------------
-- Table structure for crm_equity_relation
-- ----------------------------
DROP TABLE IF EXISTS `crm_equity_relation`;
CREATE TABLE `crm_equity_relation` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `category` varchar(255) DEFAULT NULL COMMENT '权益分类：1、会员等级 2 数字资产 3 任务 4 活动',
  `category_no` varchar(32) DEFAULT NULL COMMENT '权益分类编号：关联等级 任务 活动',
  `type` tinyint(4) DEFAULT NULL COMMENT '条件类型：1:数字资产 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡   6：账户-联合曲线(BC)  ) 7：满减 8：权限',
  `type_no` varchar(32) DEFAULT NULL COMMENT '关联权益编号',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级权益';

-- ----------------------------
-- Records of crm_equity_relation
-- ----------------------------
BEGIN;
INSERT INTO `crm_equity_relation` VALUES ('1739269197562793985', '3', '1739268377794461697', 6, '1739268685081759745');
INSERT INTO `crm_equity_relation` VALUES ('1739887464824430594', '3', '1739887363263430657', 6, '1739268685081759745');
INSERT INTO `crm_equity_relation` VALUES ('1741734154103672833', '3', '1739268377794461697', 1, '1739268884390891521');
INSERT INTO `crm_equity_relation` VALUES ('1745061372087128065', '3', '1740357981305860097', 1, '1739268884390891521');
COMMIT;

-- ----------------------------
-- Table structure for crm_fee_config
-- ----------------------------
DROP TABLE IF EXISTS `crm_fee_config`;
CREATE TABLE `crm_fee_config` (
  `fee_config_id` varchar(32) NOT NULL COMMENT '费率配置id',
  `fee_type` int(255) NOT NULL COMMENT '费率类型',
  `fee_method` varchar(255) NOT NULL COMMENT '费率计算方法',
  `fee_rate` int(255) DEFAULT NULL COMMENT '费率',
  `min_fee` int(255) DEFAULT NULL COMMENT '最低费用',
  `max_fee` int(255) DEFAULT NULL COMMENT '最高费用',
  `fee_fixed` int(255) DEFAULT NULL COMMENT '固定费用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `merchant_id` int(255) NOT NULL COMMENT '商户id',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` int(11) NOT NULL COMMENT '逻辑删除;0、未删除 1、已删除',
  PRIMARY KEY (`fee_config_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='费率配置;';

-- ----------------------------
-- Records of crm_fee_config
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_grade
-- ----------------------------
DROP TABLE IF EXISTS `crm_grade`;
CREATE TABLE `crm_grade` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `biz_role_type` varchar(255) DEFAULT NULL COMMENT '等级的业务角色类型',
  `tenant_id` varchar(64) DEFAULT NULL COMMENT '租户',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户编号',
  `name` varchar(64) DEFAULT NULL COMMENT '等级名称',
  `description` varchar(255) DEFAULT NULL COMMENT '等级描述',
  `grade_num` varchar(11) DEFAULT NULL COMMENT '等级级数',
  `grade_image` varchar(256) DEFAULT NULL COMMENT '等级图标',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` int(11) DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级划分配置';

-- ----------------------------
-- Records of crm_grade
-- ----------------------------
BEGIN;
INSERT INTO `crm_grade` VALUES ('1739228225185386496', '3', '1737841274272223232', '1737853502828482561', 'traveler', '旅行者', '1', '1', '2023-12-25 18:14:57', NULL, NULL, '2024-12-03 14:32:25', 0);
INSERT INTO `crm_grade` VALUES ('1739228225185386497', '5', '1737841274272223232', '1737853502828482561', 'traveler', '旅行者', '1', '1', '2023-12-25 18:14:57', NULL, NULL, '2024-12-03 14:33:17', 0);
INSERT INTO `crm_grade` VALUES ('1739228456794853376', '3', '1737841274272223232', '1737853502828482561', 'citizen', '常驻民', '2', '2', '2023-12-25 18:15:52', NULL, NULL, '2024-12-03 14:32:26', 0);
INSERT INTO `crm_grade` VALUES ('1739228456794853398', '5', '1737841274272223232', '1737853502828482561', 'citizen', '常驻民', '2', '2', '2023-12-25 18:15:52', NULL, NULL, '2024-12-03 14:33:16', 0);
INSERT INTO `crm_grade` VALUES ('1739228550155866112', '3', '1846190237240397824', '1737853502828482561', 'contributor', '贡献者', '3', '3', '2023-12-25 18:16:14', NULL, NULL, '2024-12-03 14:32:44', 0);
INSERT INTO `crm_grade` VALUES ('1739228550155866199', '5', '1846190237240397824', '1737853502828482561', 'contributor', '贡献者', '3', '3', '2023-12-25 18:16:14', NULL, NULL, '2024-12-03 14:33:15', 0);
INSERT INTO `crm_grade` VALUES ('1739228923818020800', '3', '1846190237240397824', '1737853502828482561', 'builder', '建设者', '4', '4', '2023-12-25 18:17:43', NULL, NULL, '2024-12-03 14:32:47', 0);
INSERT INTO `crm_grade` VALUES ('1739228923818020864', '5', '1846190237240397824', '1737853502828482561', 'builder', '建设者', '4', '4', '2023-12-25 18:17:43', NULL, NULL, '2024-12-03 14:33:14', 0);
INSERT INTO `crm_grade` VALUES ('1863835491913306112', '3', '1846190237240397824', NULL, '火源', '1', '1', '1', '2024-12-03 14:39:44', NULL, NULL, '2024-12-03 14:39:44', 0);
COMMIT;

-- ----------------------------
-- Table structure for crm_member
-- ----------------------------
DROP TABLE IF EXISTS `crm_member`;
CREATE TABLE `crm_member` (
  `serial_no` varchar(32) NOT NULL,
  `tenant_id` varchar(32) NOT NULL,
  `customer_no` varchar(32) NOT NULL COMMENT '客户号',
  `merchant_no` varchar(32) NOT NULL COMMENT '商户号',
  `store_no` varchar(32) DEFAULT NULL COMMENT '门店id，门店会员则有值',
  `status` varchar(255) NOT NULL DEFAULT '1' COMMENT '会员状态: 0：禁用 1:正常 2: 待支付(创建会员订单时创建会员，支付成功后更新状态)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `avatar` varchar(255) DEFAULT NULL COMMENT '会员头像',
  PRIMARY KEY (`serial_no`) USING BTREE,
  KEY `customer_no` (`customer_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_member
-- ----------------------------
BEGIN;
INSERT INTO `crm_member` VALUES ('1746520651596021761', '1737841274272223232', '1738934400126685184', '1739224452920684545', NULL, '1', '2024-01-14 21:12:26', 'leonard', '2024-11-01 00:04:03', NULL);
INSERT INTO `crm_member` VALUES ('1746534129970954241', '1737841274272223232', '1738934400126685184', '1737853502828482561', NULL, '1', '2024-01-14 22:06:00', 'leonard', '2024-11-01 00:04:03', NULL);
INSERT INTO `crm_member` VALUES ('1846204151426805762', '1846190237240397824', '1845851786632630272', '1846556049375875074', NULL, '1', '2024-11-01 00:02:53', 'leonard', '2024-11-01 00:04:37', NULL);
INSERT INTO `crm_member` VALUES ('1859430659626520577', '1846190237240397824', '1855542525268463616', '1846556049375875074', NULL, '1', '2024-11-21 10:56:30', '靳波', '2024-11-21 11:08:30', NULL);
INSERT INTO `crm_member` VALUES ('1859436797952684034', '1846190237240397824', '1855988298938454016', '1846556049375875074', NULL, '1', '2024-11-21 11:20:53', '靳波', '2024-11-21 11:20:53', NULL);
INSERT INTO `crm_member` VALUES ('1863254330998833154', '1846190237240397824', '1860209334991065089', '1846556049375875074', NULL, '1', '2024-12-02 00:10:24', '888888888', '2024-12-08 19:40:11', '');
INSERT INTO `crm_member` VALUES ('1868308933880582146', '1846190237240397824', '1860638624907923456', '1846556049375875074', NULL, '2', '2024-12-15 22:55:36', NULL, '2024-12-15 22:55:36', NULL);
INSERT INTO `crm_member` VALUES ('1869618768796323840', '1846190237240397824', '1860209334991065088', '1737853502828482561', '111', '2', '2024-12-19 13:41:48', '崇国芳', '2024-12-19 13:41:48', 'https://avatars.githubusercontent.com/u/2307262');
INSERT INTO `crm_member` VALUES ('1869636150491222016', '1846190237240397824', '1860209334991065088', '1846556049375875074', NULL, '2', '2024-12-19 15:16:38', '会元11', '2024-12-19 15:16:38', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6763c1f73eca0579e5371d60.jpeg');
INSERT INTO `crm_member` VALUES ('1869644917681369089', '1846190237240397824', '1860209334991065088', '1846556049375875074', NULL, '2', '2024-12-19 15:24:19', '会元11', '2024-12-19 15:24:19', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6763c1f73eca0579e5371d60.jpeg');
INSERT INTO `crm_member` VALUES ('1869645287828738049', '1846190237240397824', '1860209334991065088', '1846556049375875074', NULL, '2', '2024-12-19 15:25:47', '会元11', '2024-12-19 15:25:47', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6763c1f73eca0579e5371d60.jpeg');
INSERT INTO `crm_member` VALUES ('1869645290840248321', '1846190237240397824', '1860209334991065088', '1846556049375875074', NULL, '2', '2024-12-19 15:25:48', '会元11', '2024-12-19 15:25:48', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6763c1f73eca0579e5371d60.jpeg');
INSERT INTO `crm_member` VALUES ('1869645290852831234', '1846190237240397824', '1860209334991065088', '1846556049375875074', NULL, '2', '2024-12-19 15:25:48', '会元11', '2024-12-19 15:25:48', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6763c1f73eca0579e5371d60.jpeg');
INSERT INTO `crm_member` VALUES ('1869646184809934849', '1846190237240397824', '1865652644459188224', '1846556049375875074', NULL, '2', '2024-12-19 15:29:21', '会元11', '2024-12-19 15:29:21', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6763c1f73eca0579e5371d60.jpeg');
INSERT INTO `crm_member` VALUES ('1869648347804119042', '1846190237240397824', '1869566390399275008', '1737853502828482561', '开通所属店铺的店铺D', '2', '2024-12-19 15:37:57', '会员昵称', '2024-12-19 15:37:57', '头像');
COMMIT;

-- ----------------------------
-- Table structure for crm_member_grade
-- ----------------------------
DROP TABLE IF EXISTS `crm_member_grade`;
CREATE TABLE `crm_member_grade` (
  `serial_no` varchar(32) NOT NULL COMMENT '序列号',
  `mermber_no` varchar(32) DEFAULT NULL COMMENT '会员ID',
  `grade_no` varchar(64) DEFAULT NULL COMMENT '等级编号',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级';

-- ----------------------------
-- Records of crm_member_grade
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_merchant
-- ----------------------------
DROP TABLE IF EXISTS `crm_merchant`;
CREATE TABLE `crm_merchant` (
  `serial_no` varchar(32) NOT NULL,
  `tenant_id` varchar(32) NOT NULL,
  `username` varchar(32) NOT NULL COMMENT '商户登录名称',
  `password` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `merchant_name` varchar(255) DEFAULT NULL COMMENT '企业名称',
  `business_no` varchar(255) DEFAULT NULL COMMENT '企业工商号',
  `legal_person_name` varchar(255) DEFAULT NULL COMMENT '法人姓名',
  `legal_person_cred_type` varchar(255) DEFAULT NULL COMMENT '法人证件类型',
  `legal_person_cred_no` varchar(255) DEFAULT NULL COMMENT '法人证件号',
  `business_licence_img` varchar(255) DEFAULT NULL COMMENT '营业执照图片',
  `logo_url` varchar(255) DEFAULT NULL COMMENT '商户logo',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `net_address` varchar(255) DEFAULT NULL COMMENT '公司网址',
  `merchant_address` varchar(255) DEFAULT NULL COMMENT '企业地址',
  `status` varchar(1) DEFAULT '1' COMMENT '状态：0 正常 1 冻结 2 待审核 3、驳回 4、拒绝',
  `category` varchar(255) DEFAULT '1' COMMENT '商户类别：1、普通商户 2、web3商户',
  `type` varchar(4) DEFAULT '1' COMMENT '商户类型：1、企业商户 2、个人商户  99、平台直属商户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `authentication_status` varchar(1) DEFAULT '1' COMMENT '认证状态   1: 待认证  2：认证成功  3：认证失败',
  `business_type` varchar(255) DEFAULT NULL COMMENT '业态',
  `business_scope` text COMMENT '经营范围',
  `del_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `description` text COMMENT '描述',
  `grade_no` varchar(255) DEFAULT NULL COMMENT '等级序列号（关联crm_grade）',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_merchant
-- ----------------------------
BEGIN;
INSERT INTO `crm_merchant` VALUES ('1737853502828482561', '6345824413764157440', '花尖墨', 'e10adc3949ba59abbe56e057f20f883e', '花尖墨', NULL, NULL, NULL, NULL, NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/jiujiu/1737841274272223232/1737841352147968001/64.jpeg', NULL, NULL, NULL, '1', NULL, '1', '2023-12-22 09:56:24', '2023-12-21 23:12:17', '2', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1737864333385445377', '6345824413764157440', '1737864332806696961', NULL, '南诏南', NULL, NULL, NULL, NULL, NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/jiujiu/1737841274272223232/1737841352147968001/%E5%8D%97%E8%AF%8F%E5%8D%97O.png', NULL, NULL, NULL, '1', NULL, '1', '2023-12-21 23:55:19', '2023-12-21 23:55:19', '2', '6', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1738161558573408258', '6345824413764157440', '1738161557751324673', NULL, '大理商户bl', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, '1', NULL, '1', '2023-12-22 19:36:24', '2023-12-22 19:36:24', '1', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1738161854393475074', '6345824413764157440', '1738161853810466818', NULL, '大理商户bolei', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '1', NULL, '1', '2023-12-22 19:37:35', '2023-12-22 19:37:35', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1738187087053602818', '6345824413764157440', '1738187085979967489', NULL, '蒙自商户', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '1', NULL, '1', '2023-12-22 21:18:31', '2023-12-22 21:17:51', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1738445702360469506', '1801458632269893631', '1738445698803769346', NULL, 'yatta', NULL, NULL, NULL, NULL, NULL, NULL, '15077851132', NULL, NULL, '1', NULL, '1', '2023-12-23 14:25:29', '2023-12-23 14:25:29', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1739224452920684545', '1801458632269893632', 's11eDao', 'e10adc3949ba59abbe56e057f20f883e', 's11eDao', NULL, NULL, NULL, NULL, NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/jiujiu/1737841274272223232/1737841352147968001/s11eDao-logo.png', NULL, NULL, NULL, '0', NULL, '99', '2023-12-25 18:00:57', '2023-12-25 17:59:57', '2', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1739895959560654850', '1737841274272223232', '1739895955806822402', NULL, 'admin001', NULL, NULL, NULL, NULL, NULL, NULL, '15314216721', NULL, NULL, '1', NULL, '1', '2023-12-27 14:28:17', '2023-12-27 14:28:17', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1739946071527788546', '1543960566824046592', '1739946069988548610', NULL, 'admin', NULL, NULL, NULL, NULL, NULL, NULL, '17800000000', NULL, NULL, '1', NULL, '1', '2023-12-27 17:47:24', '2023-12-27 17:47:24', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1747166542195363841', '1747149114505826304', '1747166540232409090', NULL, 'byt-sh', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '1', NULL, '1', '2024-01-16 15:58:59', '2024-01-16 15:58:59', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1747171844852584450', '1747149114505826304', '1747171844198248449', NULL, 'byt-sh1@qq.com', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '1', NULL, '1', '2024-01-16 16:20:03', '2024-01-16 16:20:03', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1747172544932216833', '1747149114505826304', '1747172543497777154', NULL, 'byt-sh3@qq.com', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '1', NULL, '1', '2024-01-16 16:22:50', '2024-01-16 16:22:50', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1747173053571354626', '1747149114505826304', '1747173052497539073', NULL, 'byt-sh4', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '1', NULL, '1', '2024-01-16 16:24:51', '2024-01-16 16:24:51', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1747854785157120002', '1747853982593847296', '1747854784477655042', NULL, '火源商户', NULL, NULL, NULL, NULL, NULL, 'https://s11edao.oss-cn-beijing.aliyuncs.com/bigan/jiujiu/1747853982593847296/1747853988159680514/65a8b8321eca46c5c1d11965.jpg', NULL, NULL, NULL, '2', NULL, '1', '2024-01-18 13:34:55', '2024-01-18 13:33:48', '2', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1750139179330830337', '1750137989528424448', '1750139178030690306', NULL, '花尖墨', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '1', NULL, '1', '2024-01-24 20:51:11', '2024-01-24 20:51:11', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1751874905076654081', '1737841274272223232', '1751874710754500609', NULL, '验证码测试3', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '1', NULL, '1', '2024-01-29 15:48:20', '2024-01-29 15:48:20', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755134700709715969', '1737841274272223232', '1755134698847404034', NULL, 'test1', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '1', NULL, '1', '2024-02-07 15:41:36', '2024-02-07 15:41:36', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755165702635024385', '1755160153394647040', '1755165701435478017', NULL, 'leonard', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '2', NULL, '1', '2024-02-07 17:45:49', '2024-02-07 17:44:47', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755191461479063553', '1755190905624727552', '1755191459612696577', NULL, 'leonard10', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '2', NULL, '1', '2024-02-07 19:27:37', '2024-02-07 19:27:09', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755227735883653121', '1755190905624727552', '1755227734629654529', NULL, 'leonard11', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '2', NULL, '1', '2024-02-07 21:51:36', '2024-02-07 21:51:17', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755228771964821506', '1755190905624727552', '1755228770081677313', NULL, 'leonard12', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '2', NULL, '1', '2024-02-07 21:55:40', '2024-02-07 21:55:24', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755259191825666048', '1755190905624727552', '1755259190018023425', NULL, 'leonard15', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '0', NULL, '1', '2024-02-07 23:56:19', '2024-02-07 23:56:19', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1755785841118154752', '1755190905624727552', '1755785834528813058', NULL, 'bolei', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '0', NULL, '1', '2024-02-09 10:49:03', '2024-02-09 10:49:03', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1758101270348042240', '1755190905624727552', '1758101265591611393', NULL, '江少', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '0', NULL, '1', '2024-02-15 20:09:45', '2024-02-15 20:09:45', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1761954365884731392', '1755190905624727552', '1761954362965405698', NULL, 'leonard-test', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '0', NULL, '1', '2024-02-26 11:20:33', '2024-02-26 11:20:33', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1762001850552422400', '1755190905624727552', '1762001848832872449', NULL, 'leonard-test1', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '0', NULL, '1', '2024-02-26 14:29:13', '2024-02-26 14:29:13', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763455356702429184', '1747137575044386816', '1763455354097676290', NULL, '华', NULL, NULL, NULL, NULL, NULL, NULL, '17375649752', NULL, NULL, '0', NULL, '1', '2024-03-01 14:44:57', '2024-03-01 14:44:57', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763822796414259200', '1755190905624727552', '1763822794421874690', NULL, 'UMJ0036', NULL, NULL, NULL, NULL, NULL, NULL, '18294431150', NULL, NULL, '0', NULL, '1', '2024-03-02 15:05:02', '2024-03-02 15:05:02', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763823219200102400', '1738182179072118784', '1763823217312575489', NULL, 'lqh', NULL, NULL, NULL, NULL, NULL, NULL, '17375639752', NULL, NULL, '0', NULL, '1', '2024-03-02 15:06:43', '2024-03-02 15:06:43', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763825099108126720', '1737841274272223232', '1763825097203822593', NULL, '飞地总部', NULL, NULL, NULL, NULL, NULL, NULL, '18091873216', NULL, NULL, '0', NULL, '1', '2024-03-02 15:14:12', '2024-03-02 15:14:12', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763842775712927744', '1737841274272223232', '1763842772411920385', NULL, '车友嘉', NULL, NULL, NULL, NULL, NULL, NULL, '13883850988', NULL, NULL, '0', NULL, '1', '2024-03-02 16:24:27', '2024-03-02 16:24:27', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763844809480605696', '1755190905624727552', '1763844807450472449', NULL, '车友嘉', NULL, NULL, NULL, NULL, NULL, NULL, '13368410772', NULL, NULL, '0', NULL, '1', '2024-03-02 16:32:31', '2024-03-02 16:32:31', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763850903825289216', '1755190905624727552', '1763850902260723713', NULL, 'leonard-test11', NULL, NULL, NULL, NULL, NULL, NULL, '16675588381', NULL, NULL, '0', NULL, '1', '2024-03-02 16:56:44', '2024-03-02 16:56:44', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763880312393306112', '1737841274272223232', '1763880310287675393', NULL, 'lqh', NULL, NULL, NULL, NULL, NULL, NULL, '17375649752', NULL, NULL, '0', NULL, '1', '2024-03-02 18:53:35', '2024-03-02 18:53:35', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763881618583457792', '1755190905624727552', '1763881617106972673', NULL, 'lqh', NULL, NULL, NULL, NULL, NULL, NULL, '17375649752', NULL, NULL, '0', NULL, '1', '2024-03-02 18:58:47', '2024-03-02 18:58:47', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1763898513739091968', '1755190905624727552', '1763898511402774530', NULL, 'qin', NULL, NULL, NULL, NULL, NULL, NULL, '13874358297', NULL, NULL, '0', NULL, '1', '2024-03-02 20:05:57', '2024-03-02 20:05:57', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764123204068184064', '1755190905624727552', '1764123200825896961', NULL, 'AI财税', NULL, NULL, NULL, NULL, NULL, NULL, '13731085881', NULL, NULL, '0', NULL, '1', '2024-03-03 10:58:46', '2024-03-03 10:58:46', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764135786594308096', '1755190905624727552', '1764135785134600193', NULL, '赛博芯', NULL, NULL, NULL, NULL, NULL, NULL, '15195125898', NULL, NULL, '0', NULL, '1', '2024-03-03 11:48:47', '2024-03-03 11:48:47', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764186023916605440', '1737841274272223232', '1764186021987135489', NULL, 'Al', NULL, NULL, NULL, NULL, NULL, NULL, '15188727966', NULL, NULL, '0', NULL, '1', '2024-03-03 15:08:23', '2024-03-03 15:08:23', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764192289950928896', '1755190905624727552', '1764192288390557697', NULL, 'Al', NULL, NULL, NULL, NULL, NULL, NULL, '15188727966', NULL, NULL, '0', NULL, '1', '2024-03-03 15:33:17', '2024-03-03 15:33:17', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764205502222438400', '1755190905624727552', '1764205500670455810', NULL, 'cfx', NULL, NULL, NULL, NULL, NULL, NULL, '15188727966', NULL, NULL, '0', NULL, '1', '2024-03-03 16:25:48', '2024-03-03 16:25:48', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764266250206318592', '1737841274272223232', '1764266248230711297', NULL, 'wang945', NULL, NULL, NULL, NULL, NULL, NULL, '18807489993', NULL, NULL, '0', NULL, '1', '2024-03-03 20:27:09', '2024-03-03 20:27:09', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764269504646483968', '1747853982593847296', '1764269502737985538', NULL, '小小卖店', NULL, NULL, NULL, NULL, NULL, NULL, '18213742227', NULL, NULL, '0', NULL, '1', '2024-03-03 20:40:06', '2024-03-03 20:40:06', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764300039284461568', '1755190905624727552', '1764300037803782145', NULL, '符号', NULL, NULL, NULL, NULL, NULL, NULL, '13032340172', NULL, NULL, '0', NULL, '1', '2024-03-03 22:41:27', '2024-03-03 22:41:27', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764451252101582848', '1747853982593847296', '1764451250646069249', NULL, 'andy2022', NULL, NULL, NULL, NULL, NULL, NULL, '13589592796', NULL, NULL, '0', NULL, '1', '2024-03-04 08:42:19', '2024-03-04 08:42:19', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764454685198127104', '1755190905624727552', '1764454683256074242', NULL, 'andy2023', NULL, NULL, NULL, NULL, NULL, NULL, '13589592796', NULL, NULL, '0', NULL, '1', '2024-03-04 08:55:56', '2024-03-04 08:55:56', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764463306975875072', '1755190905624727552', '1764463305075765250', NULL, 'Cuering', NULL, NULL, NULL, NULL, NULL, NULL, '13835172650', NULL, NULL, '0', NULL, '1', '2024-03-04 09:30:12', '2024-03-04 09:30:12', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764482843838386176', '1755190905624727552', '1764482842269626369', NULL, 'andy2024', NULL, NULL, NULL, NULL, NULL, NULL, '13589592796', NULL, NULL, '0', NULL, '1', '2024-03-04 10:47:50', '2024-03-04 10:47:50', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764483783869992960', '1755190905624727552', '1764483782372536321', NULL, 'onlyone', NULL, NULL, NULL, NULL, NULL, NULL, '13835172650', NULL, NULL, '0', NULL, '1', '2024-03-04 10:51:33', '2024-03-04 10:51:33', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764484918139817984', '1755190905624727552', '1764484916537503745', NULL, 'Andy202403', NULL, NULL, NULL, NULL, NULL, NULL, '13589592796', NULL, NULL, '0', NULL, '1', '2024-03-04 10:56:04', '2024-03-04 10:56:04', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764485248479006720', '1755190905624727552', '1764485247044464641', NULL, 'Tyxytsg', NULL, NULL, NULL, NULL, NULL, NULL, '13835172650', NULL, NULL, '0', NULL, '1', '2024-03-04 10:57:24', '2024-03-04 10:57:24', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764485576624574464', '1755190905624727552', '1764485575106146305', NULL, 'bolei-x', NULL, NULL, NULL, NULL, NULL, NULL, '13632521024', NULL, NULL, '0', NULL, '1', '2024-03-04 10:58:41', '2024-03-04 10:58:41', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764662753492078592', '1755190905624727552', '1764662733623615490', NULL, '心誠則靈', NULL, NULL, NULL, NULL, NULL, NULL, '18907536279', NULL, NULL, '0', NULL, '1', '2024-03-04 22:42:46', '2024-03-04 22:42:46', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764669783476932608', '1738153132644503552', '1764669781916606465', NULL, '乖乖兔', NULL, NULL, NULL, NULL, NULL, NULL, '18566266752', NULL, NULL, '0', NULL, '1', '2024-03-04 23:10:40', '2024-03-04 23:10:40', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764670832656912384', '1755190905624727552', '1764670830714904578', NULL, '林乖乖兔', NULL, NULL, NULL, NULL, NULL, NULL, '18566266752', NULL, NULL, '0', NULL, '1', '2024-03-04 23:14:49', '2024-03-04 23:14:49', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764671141638705152', '1755190905624727552', '1764671139667337217', NULL, '陈乖乖兔', NULL, NULL, NULL, NULL, NULL, NULL, '18566266752', NULL, NULL, '0', NULL, '1', '2024-03-04 23:16:04', '2024-03-04 23:16:04', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764683909053943808', '1755190905624727552', '1764683907552337921', NULL, '心誠則靈', NULL, NULL, NULL, NULL, NULL, NULL, '18907536279', NULL, NULL, '0', NULL, '1', '2024-03-05 00:06:49', '2024-03-05 00:06:49', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764691438672678912', '1755190905624727552', '1764691436789391362', NULL, '心誠則靈x', NULL, NULL, NULL, NULL, NULL, NULL, '18907536279', NULL, NULL, '0', NULL, '1', '2024-03-05 00:36:44', '2024-03-05 00:36:44', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764777073139912704', '1738153132644503552', '1764777071608946690', NULL, 'xiaofei216', NULL, NULL, NULL, NULL, NULL, NULL, '15933469087', NULL, NULL, '0', NULL, '1', '2024-03-05 06:17:00', '2024-03-05 06:17:00', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764820956066484224', '1755160153394647040', '1764820954602627073', NULL, '小王子', NULL, NULL, NULL, NULL, NULL, NULL, '18310659662', NULL, NULL, '0', NULL, '1', '2024-03-05 09:11:23', '2024-03-05 09:11:23', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764831853774245888', '1737841274272223232', '1764831851903541249', NULL, 'zrmo', NULL, NULL, NULL, NULL, NULL, NULL, '18012776988', NULL, NULL, '0', NULL, '1', '2024-03-05 09:54:40', '2024-03-05 09:54:40', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764889233052536832', '1755190905624727552', '1764889231144083458', NULL, '无尘', NULL, NULL, NULL, NULL, NULL, NULL, '18796171279', NULL, NULL, '0', NULL, '1', '2024-03-05 13:42:41', '2024-03-05 13:42:41', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764922883768979456', '1755190905624727552', '1764922882271567874', NULL, '138170483341', NULL, NULL, NULL, NULL, NULL, NULL, '13817048334', NULL, NULL, '0', NULL, '1', '2024-03-05 15:56:23', '2024-03-05 15:56:23', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1764924438832353280', '1755190905624727552', '1764924436978425857', NULL, '星际', NULL, NULL, NULL, NULL, NULL, NULL, '18388248964', NULL, NULL, '0', NULL, '1', '2024-03-05 16:02:35', '2024-03-05 16:02:35', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1765038752704630784', '1755190905624727552', '1765038745632989185', NULL, 'ninetest', NULL, NULL, NULL, NULL, NULL, NULL, '13263311186', NULL, NULL, '0', NULL, '1', '2024-03-05 23:37:03', '2024-03-05 23:37:03', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1765537728671387648', '1738153132644503552', '1765537726112817153', NULL, '18251815099', NULL, NULL, NULL, NULL, NULL, NULL, '18251815099', NULL, NULL, '0', NULL, '1', '2024-03-07 08:39:35', '2024-03-07 08:39:35', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1765656594001039360', '1755190905624727552', '1765656589391454210', NULL, 'smart', NULL, NULL, NULL, NULL, NULL, NULL, '13316801311', NULL, NULL, '0', NULL, '1', '2024-03-07 16:31:55', '2024-03-07 16:31:55', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1765782012523843584', '1737841274272223232', '1765782008555986946', NULL, '自变量', NULL, NULL, NULL, NULL, NULL, NULL, '16625155721', NULL, NULL, '0', NULL, '1', '2024-03-08 00:50:17', '2024-03-08 00:50:17', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1765782799501103104', '1755190905624727552', '1765782797965942785', NULL, 'uu', NULL, NULL, NULL, NULL, NULL, NULL, '16625155721', NULL, NULL, '0', NULL, '1', '2024-03-08 00:53:24', '2024-03-08 00:53:24', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1766665084232404992', '1738153132644503552', '1766665080939831298', NULL, '涵仔', NULL, NULL, NULL, NULL, NULL, NULL, '13767868333', NULL, NULL, '0', NULL, '1', '2024-03-10 11:19:16', '2024-03-10 11:19:16', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767175442554556416', '1738153132644503552', '1767175438792220673', NULL, 'tlmty', NULL, NULL, NULL, NULL, NULL, NULL, '18352905524', NULL, NULL, '0', NULL, '1', '2024-03-11 21:07:18', '2024-03-11 21:07:18', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767377110093139968', '1755190905624727552', '1767377106460827650', NULL, '麦子', NULL, NULL, NULL, NULL, NULL, NULL, '15674975284', NULL, NULL, '0', NULL, '1', '2024-03-12 10:28:39', '2024-03-12 10:28:39', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767438151426641920', '1755190905624727552', '1767438147794329601', NULL, 'huangkh', NULL, NULL, NULL, NULL, NULL, NULL, '18006127325', NULL, NULL, '0', NULL, '1', '2024-03-12 14:31:11', '2024-03-12 14:31:11', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767507435431530496', '1738153132644503552', '1767507432940068866', NULL, '紫风铃', NULL, NULL, NULL, NULL, NULL, NULL, '13759195727', NULL, NULL, '0', NULL, '1', '2024-03-12 19:06:29', '2024-03-12 19:06:29', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767785853012283392', '1755190905624727552', '1767785850843783170', NULL, 'zj313', NULL, NULL, NULL, NULL, NULL, NULL, '15811095820', NULL, NULL, '0', NULL, '1', '2024-03-13 13:32:51', '2024-03-13 13:32:51', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767788898341228544', '1737841274272223232', '1767788895551971329', NULL, 'boat', NULL, NULL, NULL, NULL, NULL, NULL, '15138207219', NULL, NULL, '0', NULL, '1', '2024-03-13 13:45:06', '2024-03-13 13:45:06', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767789192504545280', '1755190905624727552', '1767789186569560065', NULL, '18251815099', NULL, NULL, NULL, NULL, NULL, NULL, '18251815099', NULL, NULL, '0', NULL, '1', '2024-03-13 13:46:09', '2024-03-13 13:46:09', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767944918111752192', '1737841274272223232', '1767944908670287874', NULL, 'Ravel', NULL, NULL, NULL, NULL, NULL, NULL, '13919152379', NULL, NULL, '0', NULL, '1', '2024-03-14 00:04:54', '2024-03-14 00:04:54', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1767946389469401088', '1755190905624727552', '1767946387074367490', NULL, 'Ravel', NULL, NULL, NULL, NULL, NULL, NULL, '13919152379', NULL, NULL, '0', NULL, '1', '2024-03-14 00:10:45', '2024-03-14 00:10:45', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1768192882998644736', '1750137977289445376', '1768192880637165569', NULL, '沐子安', NULL, NULL, NULL, NULL, NULL, NULL, '13631527932', NULL, NULL, '0', NULL, '1', '2024-03-14 16:30:14', '2024-03-14 16:30:14', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1768204521638596608', '1755190905624727552', '1768204519683964929', NULL, 'ghostrin', NULL, NULL, NULL, NULL, NULL, NULL, '18823663355', NULL, NULL, '0', NULL, '1', '2024-03-14 17:16:28', '2024-03-14 17:16:28', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769407313820651520', '1755190905624727552', '1769407310909718529', NULL, 'chenbo', NULL, NULL, NULL, NULL, NULL, NULL, '15675837862', NULL, NULL, '0', NULL, '1', '2024-03-18 00:55:56', '2024-03-18 00:55:56', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769626726356357120', '1755190905624727552', '1769626724418502658', NULL, 'bdww1', NULL, NULL, NULL, NULL, NULL, NULL, '17364506846', NULL, NULL, '0', NULL, '1', '2024-03-18 15:27:49', '2024-03-18 15:27:49', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769750434152583168', '1755190905624727552', '1769750430629281793', NULL, '萱悦', NULL, NULL, NULL, NULL, NULL, NULL, '13759195727', NULL, NULL, '0', NULL, '1', '2024-03-18 23:39:22', '2024-03-18 23:39:22', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769962202976096256', '1755190905624727552', '1769962199234691073', NULL, '个人商户', NULL, NULL, NULL, NULL, NULL, NULL, '17778391021', NULL, NULL, '0', NULL, '1', '2024-03-19 13:40:53', '2024-03-19 13:40:53', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769973609394212864', '1750137977289445376', '1769973607733182465', NULL, 'li', NULL, NULL, NULL, NULL, NULL, NULL, '18565648075', NULL, NULL, '0', NULL, '1', '2024-03-19 14:26:12', '2024-03-19 14:26:12', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769976030925295616', '1755190905624727552', '1769976028924526593', NULL, 'lucien', NULL, NULL, NULL, NULL, NULL, NULL, '17688995261', NULL, NULL, '0', NULL, '1', '2024-03-19 14:35:48', '2024-03-19 14:35:48', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769982360918560768', '1755190905624727552', '1769982359286890498', NULL, '子淡', NULL, NULL, NULL, NULL, NULL, NULL, '18199056807', NULL, NULL, '0', NULL, '1', '2024-03-19 15:00:58', '2024-03-19 15:00:58', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769991824497315840', '1747853982593847296', '1769991822593015809', NULL, 'orange', NULL, NULL, NULL, NULL, NULL, NULL, '18565648075', NULL, NULL, '0', NULL, '1', '2024-03-19 15:38:34', '2024-03-19 15:38:34', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1769992504603709440', '1750137977289445376', '1769992503022370818', NULL, 'orange', NULL, NULL, NULL, NULL, NULL, NULL, '18565648075', NULL, NULL, '0', NULL, '1', '2024-03-19 15:41:17', '2024-03-19 15:41:17', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1770004031465459712', '1755190905624727552', '1770004029959618561', NULL, 'sss', NULL, NULL, NULL, NULL, NULL, NULL, '13194559231', NULL, NULL, '0', NULL, '1', '2024-03-19 16:27:05', '2024-03-19 16:27:05', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1770278306684473344', '1750137989528424448', '1770278303291195394', NULL, 'thdqn', NULL, NULL, NULL, NULL, NULL, NULL, '18487199921', NULL, NULL, '0', NULL, '1', '2024-03-20 10:36:58', '2024-03-20 10:36:58', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1770328350729048064', '1737841274272223232', '1770328347931361281', NULL, 'orange_', NULL, NULL, NULL, NULL, NULL, NULL, '18565648075', NULL, NULL, '0', NULL, '1', '2024-03-20 13:55:47', '2024-03-20 13:55:47', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1770634468739125248', '1755190905624727552', '1770634465492647938', NULL, 'ferfi', NULL, NULL, NULL, NULL, NULL, NULL, '15773560538', NULL, NULL, '0', NULL, '1', '2024-03-21 10:12:14', '2024-03-21 10:12:14', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1771004006878023680', '1755190905624727552', '1771004005040832513', NULL, 'orange_1', NULL, NULL, NULL, NULL, NULL, NULL, '18565648075', NULL, NULL, '0', NULL, '1', '2024-03-22 10:40:38', '2024-03-22 10:40:38', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1771186339048460288', '1750137977289445376', '1771186335940395010', NULL, '13298327230', NULL, NULL, NULL, NULL, NULL, NULL, '13298327230', NULL, NULL, '0', NULL, '1', '2024-03-22 22:45:09', '2024-03-22 22:45:09', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1772145378527940608', '1747853982593847296', '1772145374862032898', NULL, '测试商户', NULL, NULL, NULL, NULL, NULL, NULL, '15244843246', NULL, NULL, '0', NULL, '1', '2024-03-25 14:16:02', '2024-03-25 14:16:02', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1772284160107483136', '1737841274272223232', '1772284156202500097', NULL, 'scott', NULL, NULL, NULL, NULL, NULL, NULL, '13530702131', NULL, NULL, '0', NULL, '1', '2024-03-25 23:27:30', '2024-03-25 23:27:30', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1772932177315631104', '1737841274272223232', '1772932173561643010', NULL, 'helperbot', NULL, NULL, NULL, NULL, NULL, NULL, '18024967988', NULL, NULL, '0', NULL, '1', '2024-03-27 18:22:30', '2024-03-27 18:22:30', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1772933212121731072', '1750137977289445376', '1772933210171293697', NULL, 'helperbot', NULL, NULL, NULL, NULL, NULL, NULL, '18024967988', NULL, NULL, '0', NULL, '1', '2024-03-27 18:26:35', '2024-03-27 18:26:35', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1773227426910965760', '1755190905624727552', '1773227425270906881', NULL, 'ruxyeah123', NULL, NULL, NULL, NULL, NULL, NULL, '15000723484', NULL, NULL, '0', NULL, '1', '2024-03-28 13:55:40', '2024-03-28 13:55:40', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1774691307617390592', '1750137977289445376', '1774691304014397442', NULL, '靓伊', NULL, NULL, NULL, NULL, NULL, NULL, '18879842622', NULL, NULL, '0', NULL, '1', '2024-04-01 14:52:39', '2024-04-01 14:52:39', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1776987015703826432', '1737841274272223232', '1776987012243439617', NULL, '益增', NULL, NULL, NULL, NULL, NULL, NULL, '18755333392', NULL, NULL, '0', NULL, '1', '2024-04-07 22:54:58', '2024-04-07 22:54:58', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1777004969594064896', '1755190905624727552', '1777004967656210433', NULL, 'xiang2822', NULL, NULL, NULL, NULL, NULL, NULL, '18755333392', NULL, NULL, '0', NULL, '1', '2024-04-08 00:06:18', '2024-04-08 00:06:18', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1777219263644438528', '1755190905624727552', '1777219260225994753', NULL, 'Shirly', NULL, NULL, NULL, NULL, NULL, NULL, '18123818620', NULL, NULL, '0', NULL, '1', '2024-04-08 14:17:51', '2024-04-08 14:17:51', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1777254005773307904', '1747853982593847296', '1777254003403440129', NULL, 'acktui', NULL, NULL, NULL, NULL, NULL, NULL, '13438263506', NULL, NULL, '0', NULL, '1', '2024-04-08 16:35:51', '2024-04-08 16:35:51', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1779732040644694016', '1737841274272223232', '1779732038958497793', NULL, '大树', NULL, NULL, NULL, NULL, NULL, NULL, '13196231102', NULL, NULL, '0', NULL, '1', '2024-04-15 12:42:43', '2024-04-15 12:42:43', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1785208071014977536', '1738153132644503552', '1785208069047762946', NULL, 'liuhx', NULL, NULL, NULL, NULL, NULL, NULL, '18953173980', NULL, NULL, '0', NULL, '1', '2024-04-30 15:22:30', '2024-04-30 15:22:30', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1796777429313982466', '1796759813896671232', '1796777428806471681', NULL, 'hy2', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, '1', NULL, '1', '2024-06-01 13:34:56', '2024-06-01 13:34:56', '1', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1796816978891165697', '1796759813896671232', 'hy2-m2', NULL, 'hy2-m2', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, '1', NULL, '1', '2024-06-01 16:12:05', '2024-06-01 16:12:05', '1', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1796855729617031169', '1796759813896671232', 'hy2-m3', NULL, 'hy2-m3', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, '1', NULL, '1', '2024-06-01 18:46:04', '2024-06-01 18:46:04', '1', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1846556049375875073', '1846190237240397824', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '链动2+1超级管理员', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, '1', NULL, '1', '2024-10-16 22:17:24', '2024-10-16 22:17:24', '1', '1', NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1846556049375875074', '1846190237240397824', 'under_merchant', 'e10adc3949ba59abbe56e057f20f883e', '链动2+1直属商户', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', NULL, '99', '2024-10-30 22:47:11', '2024-10-30 22:47:11', '1', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1865700988548157440', '1846190237240397824', '高玉梅', 'qBatz1s4yrd_GPf', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, '0', '2024-12-08 18:12:37', '2024-12-08 18:12:37', '2', NULL, NULL, '0', NULL, '');
INSERT INTO `crm_merchant` VALUES ('1869637386607136768', '1846190237240397824', '乾勇', 'WjOqPXikM7lh22M', '局慧', NULL, NULL, NULL, 'ut aliqua dolor', 'https://loremflickr.com/400/400?lock=5606449974398848', 'https://avatars.githubusercontent.com/u/6752614', '91771015478', NULL, NULL, '0', NULL, '0', '2024-12-19 14:54:25', '2024-12-19 14:54:25', '2', 'sit deserunt', NULL, '0', '即来非次实拉近量。拉术南根什。即如人。', '');
COMMIT;

-- ----------------------------
-- Table structure for crm_merchant_api_secret
-- ----------------------------
DROP TABLE IF EXISTS `crm_merchant_api_secret`;
CREATE TABLE `crm_merchant_api_secret` (
  `secret_id` varchar(32) NOT NULL COMMENT '秘钥id',
  `name` varchar(100) NOT NULL COMMENT 'api秘钥名称',
  `public_key` varchar(255) NOT NULL COMMENT '公钥',
  `status` int(11) NOT NULL COMMENT '状态;1.启用 2.禁用',
  `api_type` int(11) NOT NULL COMMENT 'API 类型;0.全部 1.查询 2.充值 3.提现',
  `ip_whit_list` varchar(255) DEFAULT NULL COMMENT 'ip白名单',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `merchant_id` varchar(32) NOT NULL COMMENT '商户id',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '删除逻辑;0.未删除 1.已删除',
  PRIMARY KEY (`secret_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='api秘钥;';

-- ----------------------------
-- Records of crm_merchant_api_secret
-- ----------------------------
BEGIN;
INSERT INTO `crm_merchant_api_secret` VALUES ('1783102032589230080', '秘钥2', '55f5s55f5sh5d55j55afgd', 1, 0, '196.170.16.168;196.0.0.157', '备注嘻嘻嘻嘻嘻嘻', '1781671150867320832', NULL, '2024-04-24 19:53:48', NULL, '2024-04-24 19:53:44', 0);
INSERT INTO `crm_merchant_api_secret` VALUES ('1783103494379671552', '秘钥1', '55f5s55f5sh5d55j55afgd', 1, 0, '196.170.16.168;196.0.0.157', '备注嘻嘻嘻嘻嘻嘻', '1781671150867320832', NULL, '2024-04-24 19:59:36', NULL, '2024-04-24 19:59:33', 1);
COMMIT;

-- ----------------------------
-- Table structure for crm_merchant_auth
-- ----------------------------
DROP TABLE IF EXISTS `crm_merchant_auth`;
CREATE TABLE `crm_merchant_auth` (
  `serial_no` varchar(32) NOT NULL COMMENT '主键流水号',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户ID',
  `username` varchar(32) NOT NULL COMMENT '商户登录名',
  `password` varchar(255) NOT NULL COMMENT '登录密码(加密存储)',
  `merchant_name` varchar(100) NOT NULL COMMENT '企业名称',
  `base_info_auth_status` varchar(255) DEFAULT NULL COMMENT '基础信息认证状态',
  `legal_person_name` varchar(50) DEFAULT NULL COMMENT '法人姓名',
  `legal_person_cred_type` varchar(20) DEFAULT 'ID_CARD' COMMENT '法人证件类型(ID_CARD/PASSPORT)',
  `legal_person_id_card_national` varchar(255) DEFAULT NULL COMMENT '法人证件国徽面',
  `legal_person_id_card_copy` varchar(255) DEFAULT NULL COMMENT '法人证件正面',
  `legal_person_cred_no` varchar(50) DEFAULT NULL COMMENT '法人证件号码',
  `legal_person_info_auth_status` varchar(255) DEFAULT NULL COMMENT '法人信息认证状态',
  `business_licence_img` varchar(255) DEFAULT NULL COMMENT '营业执照URL',
  `business_no` varchar(30) DEFAULT NULL COMMENT '统一社会信用代码',
  `business_scope` text COMMENT '经营范围',
  `business_type` varchar(50) DEFAULT NULL COMMENT '所属业态',
  `business_info_auth_status` varchar(255) DEFAULT NULL COMMENT '营业信息认证状态',
  `logo_url` varchar(255) DEFAULT NULL COMMENT '商户LOGO URL',
  `contact_name` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(20) NOT NULL COMMENT '联系手机号',
  `website` varchar(100) DEFAULT NULL COMMENT '企业官网',
  `merchant_address` varchar(200) NOT NULL COMMENT '注册地址',
  `status` enum('0','1','2','3','4') NOT NULL DEFAULT '2' COMMENT '状态:0=正常,1=冻结,2=待审核,3=驳回,4=拒绝',
  `category` enum('1','2') NOT NULL DEFAULT '1' COMMENT '商户类别:1=普通商户,2=WEB3商户',
  `merchant_type` enum('1','2','99') NOT NULL DEFAULT '1' COMMENT '类型:1=企业,2=个人,99=平台直属',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `auth_status` enum('1','2','3') NOT NULL DEFAULT '1' COMMENT '认证状态:1=待认证, 2=待完善 ,3=成功 4 =失败',
  `description` text COMMENT '商户描述',
  `version` int(11) DEFAULT '1' COMMENT '版本号（乐观锁）',
  `del_flag` varchar(255) DEFAULT '0' COMMENT '删除标记:0=正常,1=删除',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`serial_no`),
  UNIQUE KEY `idx_tenant_username` (`tenant_id`,`username`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_business_no` (`business_no`(10))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户认证信息表';

-- ----------------------------
-- Records of crm_merchant_auth
-- ----------------------------
BEGIN;
INSERT INTO `crm_merchant_auth` VALUES ('1942875007904800769', 'tenant_123456', 'merchant_test', 'password123', '某某科技有限公司', '2', '张三', 'ID_CARD', NULL, NULL, '110101199001010001', '1', 'https://example.com/business_license.jpg', '91110000123456789X', NULL, NULL, '1', 'https://example.com/logo.jpg', NULL, '13800138000', 'https://www.example.com', '北京市朝阳区某某街道123号', '2', '1', '1', '2025-07-09 17:14:33', '2025-07-09 17:32:58', '1', '专业的科技服务公司', 1, '0', '');
COMMIT;

-- ----------------------------
-- Table structure for crm_merchant_business_type
-- ----------------------------
DROP TABLE IF EXISTS `crm_merchant_business_type`;
CREATE TABLE `crm_merchant_business_type` (
  `serial_no` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '业态名称：如美食',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标URL',
  `parent_no` varchar(255) DEFAULT NULL COMMENT '父级ID',
  `sort` varchar(255) DEFAULT NULL COMMENT '排序值，越小越靠前',
  `status` varchar(255) DEFAULT NULL COMMENT '状态：1启用，0禁用',
  PRIMARY KEY (`serial_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户分类业态表（业态+分类+标签）';

-- ----------------------------
-- Records of crm_merchant_business_type
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_merchant_config
-- ----------------------------
DROP TABLE IF EXISTS `crm_merchant_config`;
CREATE TABLE `crm_merchant_config` (
  `serial_no` varchar(32) DEFAULT NULL COMMENT '商户配置表id',
  `tenant_id` int(11) DEFAULT NULL COMMENT '所属租户',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '商户id',
  `member_model` varchar(255) DEFAULT NULL COMMENT '商户会员模型： 1平台会员 2商户会员 3店铺会员',
  `callback_address` varchar(255) DEFAULT NULL COMMENT '回调地址',
  `profit_sharing_rate` varchar(255) DEFAULT NULL COMMENT '商户让利配置',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(4) DEFAULT '0' COMMENT '逻辑删除;0、未删除 1、已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户配置表;';

-- ----------------------------
-- Records of crm_merchant_config
-- ----------------------------
BEGIN;
INSERT INTO `crm_merchant_config` VALUES ('1782970670586662912', NULL, '1782970669089296384', NULL, NULL, NULL, NULL, '2024-04-24 11:11:49', NULL, NULL, 0);
INSERT INTO `crm_merchant_config` VALUES ('1783027298858045440', NULL, '1783027298518306816', NULL, 'http://xxxxxx:8088/getxxinfo/jjjjj', NULL, NULL, '2024-04-24 14:56:50', NULL, NULL, 0);
INSERT INTO `crm_merchant_config` VALUES ('1783106409806827520', NULL, '1783106409441923072', NULL, NULL, NULL, NULL, '2024-04-24 20:11:11', NULL, '2024-04-24 20:11:08', 0);
COMMIT;

-- ----------------------------
-- Table structure for crm_merchant_hyperledger_param
-- ----------------------------
DROP TABLE IF EXISTS `crm_merchant_hyperledger_param`;
CREATE TABLE `crm_merchant_hyperledger_param` (
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  `merchant_no` varchar(32) NOT NULL COMMENT '品牌商户编号',
  `benefit_allocation_ratio` varchar(32) NOT NULL COMMENT '单笔交易社区收益比例',
  `treasury_allocation_ratio` varchar(32) NOT NULL COMMENT '社区金库直接分配比例',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`merchant_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_merchant_hyperledger_param
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for crm_platform
-- ----------------------------
DROP TABLE IF EXISTS `crm_platform`;
CREATE TABLE `crm_platform` (
  `serial_no` varchar(32) NOT NULL,
  `tenant_id` varchar(32) NOT NULL,
  `username` varchar(32) NOT NULL COMMENT '商户登录名称',
  `platform_name` varchar(255) DEFAULT NULL COMMENT '企业名称',
  `business_no` varchar(255) DEFAULT NULL COMMENT '企业工商号',
  `legal_person_name` varchar(255) DEFAULT NULL COMMENT '法人姓名',
  `legal_person_cred_type` varchar(255) DEFAULT NULL COMMENT '法人证件类型',
  `legal_person_cred_no` varchar(255) DEFAULT NULL COMMENT '法人证件号',
  `business_licence_img` varchar(255) DEFAULT NULL COMMENT '营业执照图片',
  `logo_url` varchar(255) DEFAULT NULL COMMENT '商户logo',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `net_address` varchar(255) DEFAULT NULL COMMENT '公司网址',
  `platform_address` varchar(255) DEFAULT NULL COMMENT '企业地址',
  `status` varchar(1) DEFAULT '1' COMMENT '状态：0 正常 1 冻结 2 待审核',
  `type` varchar(1) DEFAULT '1' COMMENT '平台类型：1 个人 2 企业',
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `authentication_status` varchar(1) DEFAULT '1' COMMENT '认证状态   1: 待认证  2：认证成功  3：认证失败',
  `business_type` varchar(255) DEFAULT NULL COMMENT '业态',
  `business_scope` text COMMENT '经营范围',
  `del_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `description` varchar(255) DEFAULT NULL COMMENT '平台描述',
  `tx_password` varchar(255) DEFAULT NULL,
  `tx_password_status` varchar(255) DEFAULT NULL,
  `google_secret_key` varchar(255) DEFAULT NULL,
  `eco_value_allocation_model` varchar(255) DEFAULT NULL COMMENT '生态价值分配模型',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_platform
-- ----------------------------
BEGIN;
INSERT INTO `crm_platform` VALUES ('1799273766543560704', '1799273767436947456', '大理节点', '大理节点', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '1', '1535984304528691200', '2024-06-08 10:54:32', NULL, '2024-06-08 10:54:31', '1', NULL, NULL, '0', NULL, NULL, '0', NULL, NULL);
INSERT INTO `crm_platform` VALUES ('1801458630923522048', '1801458632269893632', 'huoyuanshequ', '火源社区', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '1', '1535984304528691200', '2024-06-14 11:36:25', NULL, '2024-06-14 11:36:24', '1', NULL, NULL, '0', '火源社区节点', NULL, '0', NULL, NULL);
INSERT INTO `crm_platform` VALUES ('1846190236313456640', '6345824413764157440', 'admin', '链动2+1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '1', '1535984304528691200', '2024-10-15 22:03:50', NULL, '2024-10-15 22:03:50', '1', NULL, NULL, '0', '链动2+1小程序节点', NULL, '0', NULL, 'CURVE_BASED');
COMMIT;

-- ----------------------------
-- Table structure for crm_settlement_account
-- ----------------------------
DROP TABLE IF EXISTS `crm_settlement_account`;
CREATE TABLE `crm_settlement_account` (
  `serial_no` varchar(32) NOT NULL COMMENT '结算账户id',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  `biz_role_type` varchar(10) DEFAULT NULL COMMENT '角色类型，1.运营平台 2.租户平台 4.代理商 5.租户客户 6.门店 99.无''',
  `biz_role_type_no` varchar(32) DEFAULT NULL COMMENT '业务角色类型编号',
  `account_num` varchar(100) NOT NULL COMMENT '账户号',
  `account_name` varchar(100) NOT NULL COMMENT '账户名',
  `account_type` int(11) NOT NULL COMMENT '账户类型：1.IBAN账户 2.电子账户 ',
  `bank_name` varchar(255) NOT NULL COMMENT '银行名称',
  `bank_branch` varchar(255) DEFAULT NULL COMMENT '银行支行',
  `swift_code` varchar(100) DEFAULT NULL COMMENT '银行识别码;（电子账户也会制定相应的识别码）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除;0、未删除 1、已删除',
  `category` varchar(255) DEFAULT NULL COMMENT '分类：1 支付结算  2体现结算',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='结算账户;';

-- ----------------------------
-- Records of crm_settlement_account
-- ----------------------------
BEGIN;
INSERT INTO `crm_settlement_account` VALUES ('1783076456629407744', NULL, NULL, NULL, 'LT773120023810029964', 'Yang Qianjin', 1, 'UAB Paytend Europe', NULL, 'UAPULT22', NULL, NULL, '2024-04-24 18:12:10', NULL, '2024-04-24 18:12:06', 0, NULL);
INSERT INTO `crm_settlement_account` VALUES ('1783076935740559360', NULL, NULL, NULL, 'LT773120023810029964', 'Yang Qianjin', 1, 'UAB Paytend Europe', NULL, 'UAPULT22', NULL, NULL, '2024-04-24 18:14:04', NULL, '2024-04-24 18:14:01', 1, NULL);
INSERT INTO `crm_settlement_account` VALUES ('1783083869868789760', NULL, NULL, NULL, 'LT773120023810029964', 'Li Si', 1, 'UAB Paytend Europe', NULL, 'UAPULT22', '结算账户', NULL, '2024-04-24 18:41:37', NULL, '2024-04-24 18:41:34', 0, NULL);
INSERT INTO `crm_settlement_account` VALUES ('1869297509462052864', '1846190237240397824', '5', '1860209334991065088', '1234567891020', '王五的账户', 1, '工商银行', NULL, 'ICBKCNBJ', '这是一个示例账户', 'oqASg7VBjqmskvK7uBZRsfZwgTT4', '2024-12-18 16:23:51', 'oqASg7VBjqmskvK7uBZRsfZwgTT4', '2024-12-18 16:34:28', 0, NULL);
INSERT INTO `crm_settlement_account` VALUES ('1870021908276318208', '1846190237240397824', '5', '1869566390399275008', '123456789', '张三的账户', 1, '工商银行', NULL, 'ICBKCNBJ', '这是一个示例账户', '1869566390399275008', '2024-12-20 16:22:21', '1869566390399275008', '2024-12-20 16:22:21', 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_store
-- ----------------------------
DROP TABLE IF EXISTS `crm_store`;
CREATE TABLE `crm_store` (
  `serial_no` varchar(32) NOT NULL COMMENT '店铺ID',
  `store_name` varchar(50) NOT NULL DEFAULT '' COMMENT '店铺名称',
  `password` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `longitude` varchar(255) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(255) DEFAULT NULL COMMENT '纬度',
  `business_hours` varchar(255) DEFAULT NULL COMMENT '营业时间',
  `description` text NOT NULL COMMENT '店铺介绍',
  `logo` varchar(255) DEFAULT '0' COMMENT '店铺logo',
  `merchant_no` varchar(32) DEFAULT NULL COMMENT '所属商户编号',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  `del_flag` int(11) DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `type` varchar(255) DEFAULT '1' COMMENT '类型：0：总店 1：非总店',
  `business_model` varchar(255) DEFAULT NULL COMMENT '经营模式  1、直营，2、加盟',
  `region_code` varchar(255) DEFAULT NULL COMMENT '区域编码',
  `grade_no` varchar(255) DEFAULT NULL COMMENT '等级序列号（关联crm_grade）',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_store
-- ----------------------------
BEGIN;
INSERT INTO `crm_store` VALUES ('1', '店铺名称', NULL, '地址', '1', '2', '1', '描述', '店铺logo', '1846556049375875073', '1846190237240397824', 0, '2024-10-21 21:02:38', '2024-10-21 21:02:38', '1', NULL, NULL, NULL);
INSERT INTO `crm_store` VALUES ('178577410', '1', NULL, '1', '1', '1', '1', '1', '1', '1846556049375875073', '1846190237240397824', 0, '2024-10-22 22:02:54', '2024-10-22 22:02:54', '1', NULL, NULL, NULL);
INSERT INTO `crm_store` VALUES ('1869641501047394304', '', NULL, NULL, NULL, NULL, NULL, '即来非次实拉近量。拉术南根什。即如人。', '0', '1869637386607136768', '1846190237240397824', 0, '2024-12-19 15:10:44', '2024-12-19 15:10:44', '0', '1', NULL, NULL);
INSERT INTO `crm_store` VALUES ('2', '商户总店', NULL, '深圳宝安', NULL, NULL, NULL, '店铺介绍', 'logo', '1846556049375875073', NULL, 0, '2024-10-21 22:07:33', '2024-10-21 22:07:33', '0', '1', NULL, NULL);
INSERT INTO `crm_store` VALUES ('3', '商户总店', NULL, '深圳宝安', NULL, NULL, NULL, '店铺介绍', 'logo', '1846556049375875073', NULL, 0, '2024-10-21 22:15:54', '2024-10-21 22:15:54', '1', '1', NULL, NULL);
INSERT INTO `crm_store` VALUES ('4', '店铺名称', NULL, '地址', '经度', '维度', '8-8', '描述', 'logo', '1846556049375875073', '1846190237240397824', 0, '2024-10-22 21:50:50', '2024-10-22 21:50:50', '1', NULL, NULL, NULL);
INSERT INTO `crm_store` VALUES ('5', '店铺名称', NULL, '地址', '经度', '维度', '8-8', '描述', 'logo', '1846556049375875073', '1846190237240397824', 0, '2024-10-22 21:51:04', '2024-10-22 21:51:04', '1', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for crm_sys_agent
-- ----------------------------
DROP TABLE IF EXISTS `crm_sys_agent`;
CREATE TABLE `crm_sys_agent` (
  `serial_no` varchar(32) NOT NULL,
  `tenant_id` varchar(32) NOT NULL,
  `username` varchar(32) NOT NULL COMMENT '登录名称',
  `password` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `agent_name` varchar(255) DEFAULT NULL COMMENT '代理商名称',
  `type` varchar(1) DEFAULT '1' COMMENT '代理商类型：1、个人客户  2、企业客户',
  `business_no` varchar(255) DEFAULT NULL COMMENT '企业工商号',
  `legal_person_name` varchar(255) DEFAULT NULL COMMENT '法人姓名',
  `legal_person_cred_type` varchar(255) DEFAULT NULL COMMENT '法人证件类型',
  `legal_person_cred_no` varchar(255) DEFAULT NULL COMMENT '法人证件号',
  `business_licence_img` varchar(255) DEFAULT NULL COMMENT '营业执照图片',
  `logo_url` varchar(255) DEFAULT NULL COMMENT '商户logo',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `net_address` varchar(255) DEFAULT NULL COMMENT '公司网址',
  `agent_address` varchar(255) DEFAULT NULL COMMENT '企业地址',
  `status` varchar(1) DEFAULT '1' COMMENT '状态：0 正常 1 冻结 2 待审核',
  `category` varchar(255) DEFAULT NULL COMMENT '代理商类别：1、平台代理 2、分销代理',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `authentication_status` varchar(1) DEFAULT '1' COMMENT '认证状态   1: 待认证  2：认证成功  3：认证失败',
  `business_type` varchar(255) DEFAULT NULL COMMENT '业态',
  `business_scope` text COMMENT '经营范围',
  `del_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `description` text COMMENT '代理商介绍',
  `agent_level` varchar(255) DEFAULT NULL COMMENT '代理商级别：PROVINCE-省代理 CITY-市代理 COUNTY-县代理 DISTRICT-区代理 TOWN-镇代理 NONE-无',
  `grade_no` varchar(255) DEFAULT NULL COMMENT '等级序列号（关联crm_grade）',
  `parent_agent_no` varchar(255) DEFAULT NULL COMMENT '上级代理商编号',
  `region_code` varchar(255) DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商表';

-- ----------------------------
-- Records of crm_sys_agent
-- ----------------------------
BEGIN;
INSERT INTO `crm_sys_agent` VALUES ('1737853500064509954', '6345824413764157440', 'bolei', 'e10adc3949ba59abbe56e057f20f883e', '1号代理商', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', NULL, '2024-10-18 21:23:02', '2024-10-18 21:23:02', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1800849143061680128', '6345824413764157440', 'jiujiu-agent', NULL, 'jiujiu产品代理商', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', NULL, '2024-06-12 19:14:28', '2024-06-12 19:14:28', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1847267145117995008', '1846190237240397824', 'admin', NULL, '代理商1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', NULL, '2024-10-23 00:36:40', '2024-10-23 00:36:40', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1859437024113725441', '1846190237240397824', 'admin', NULL, '汲国强', '0', NULL, NULL, NULL, NULL, NULL, NULL, 'https://avatars.githubusercontent.com/u/48413943', '13632521024', NULL, '上海市 南林市 黑山县 律中心1813号 11层', '1', NULL, '2024-11-21 11:21:47', '2024-11-21 11:21:47', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1865794231566733313', '1846190237240397824', 'admin', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', NULL, '2024-12-09 00:23:04', '2024-12-09 00:23:04', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1869649134244466689', '1846190237240397824', 'admin', NULL, '嬴三锋', '0', NULL, NULL, NULL, NULL, NULL, 'https://avatars.githubusercontent.com/u/35239891', 'https://avatars.githubusercontent.com/u/74002918', '52945202296', NULL, '黑龙江省 太汉市 斗门区 程侬9号 2号房间', '1', NULL, '2024-12-19 15:41:04', '2024-12-19 15:41:04', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1869676939992764416', '1846190237240397824', 'oqASg7S66ZFo-G9fsgJWnYSvvZAk', NULL, '团长', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '13361784696', NULL, '南昌', '1', NULL, '2024-12-19 17:31:34', '2024-12-19 17:31:34', '1', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1869978137677402113', '1846190237240397824', 'oqASg7S66ZFo-G9fsgJWnYSvvZAk', NULL, '', '0', NULL, NULL, NULL, NULL, 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/6765006f3aaa3ed5ac11688c.png', 'https://bsin-jinan.oss-cn-beijing.aliyuncs.com/test/676500773aaa3ed5ac11688d.png', NULL, '13361784696', NULL, '南昌市', '1', NULL, '2024-12-20 13:28:25', '2024-12-20 13:28:25', '1', '1', NULL, '0', '商户介绍', NULL, NULL, NULL, NULL);
INSERT INTO `crm_sys_agent` VALUES ('1945759112415547393', '6345824413764157440', 'nanb', 'e10adc3949ba59abbe56e057f20f883e', '南昌合伙公司', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '1', '2025-07-17 16:14:57', '2025-07-17 16:14:57', '1', NULL, NULL, '0', NULL, 'PROVINCE', NULL, NULL, '1');
COMMIT;

-- ----------------------------
-- Table structure for crm_withdrawal_record
-- ----------------------------
DROP TABLE IF EXISTS `crm_withdrawal_record`;
CREATE TABLE `crm_withdrawal_record` (
  `serial_no` varchar(255) NOT NULL,
  `tenant_id` varchar(32) NOT NULL COMMENT '租户ID',
  `biz_role_type` varchar(255) DEFAULT NULL COMMENT '业务角色类型',
  `biz_role_type_no` varchar(32) DEFAULT NULL COMMENT '业务角色类型编号',
  `account_no` varchar(32) NOT NULL COMMENT '提现账号',
  `withdrawal_type` varchar(255) DEFAULT NULL COMMENT '提现方式：1-银行卡，2-支付宝，3-微信，4-其他',
  `amount` varchar(255) DEFAULT NULL COMMENT '提现金额',
  `fee` varchar(255) DEFAULT NULL COMMENT '手续费',
  `settlement_account_no` varchar(255) NOT NULL COMMENT '结算账户编号',
  `settlement_account_json` json DEFAULT NULL COMMENT '结算账户信息快照',
  `create_time` datetime DEFAULT NULL COMMENT '申请时间',
  `audit_status` varchar(255) DEFAULT NULL COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `status` varchar(255) DEFAULT NULL COMMENT '处理状态：0-待处理，1-处理中，2-已完成，3-已取消',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`serial_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crm_withdrawal_record
-- ----------------------------
BEGIN;
INSERT INTO `crm_withdrawal_record` VALUES ('1942780507832324098', '6345824413764157440', NULL, NULL, '1', '1', '1', NULL, '1', NULL, NULL, NULL, NULL, NULL, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
