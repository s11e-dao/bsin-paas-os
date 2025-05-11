
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_app
-- ----------------------------
DROP TABLE IF EXISTS `sys_app`;
CREATE TABLE `sys_app` (
                           `app_id` varchar(100) NOT NULL DEFAULT '' COMMENT '应用id',
                           `app_code` varchar(100) NOT NULL DEFAULT '' COMMENT '应用编号',
                           `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '应用名称',
                           `logo` varchar(255) DEFAULT NULL COMMENT '应用图标',
                           `url` varchar(255) NOT NULL DEFAULT '' COMMENT '应用访问地址',
                           `type` int(11) NOT NULL DEFAULT '1' COMMENT '应用类型0、默认应用 1、普通应用',
                           `status` int(11) DEFAULT '0' COMMENT '应用状态 0、未发布 1、已发布',
                           `remark` varchar(255) DEFAULT NULL COMMENT '应用描述',
                           `app_language` int(11) DEFAULT '0' COMMENT '应用前端语言类型 0、vue 1、react',
                           `theme` text COMMENT '主题',
                           `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` int(11) DEFAULT '0' COMMENT '逻辑删除 0、未删除 1、已删除',
                           PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
