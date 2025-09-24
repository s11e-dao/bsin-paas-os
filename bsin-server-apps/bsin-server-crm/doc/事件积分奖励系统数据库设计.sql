-- =============================================
-- 事件积分奖励系统数据库设计
-- =============================================

-- 创建数据库（如果不存在）
-- CREATE DATABASE IF NOT EXISTS brms_reward_system DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE brms_reward_system;

-- =============================================
-- 1. 事件表（brms_event）
-- 存储系统中所有可触发奖励的事件定义
-- =============================================
DROP TABLE IF EXISTS `brms_event`;
CREATE TABLE `brms_event` (
    `serial_no`   VARCHAR(32)  NOT NULL COMMENT '主键，事件唯一标识',
    `tenant_id`   VARCHAR(64)  NOT NULL COMMENT '租户ID，支持多租户',
    `event_name`  VARCHAR(64)  NOT NULL COMMENT '事件名称',
    `event_code`  VARCHAR(32)  NOT NULL COMMENT '事件编码，业务系统调用标识',
    `event_level` VARCHAR(255) NOT NULL COMMENT '事件级别：1-平台级 2-商户级',
    `event_type`  VARCHAR(32)  NOT NULL COMMENT '事件类型：USER-用户事件 ORDER-订单事件 SYSTEM-系统事件',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '事件描述',
    `status`      TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    `update_by`   VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`serial_no`),
    UNIQUE KEY `uk_tenant_event_code` (`tenant_id`, `event_code`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_event_code` (`event_code`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_event_level` (`event_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事件表：存储系统中所有可触发奖励的事件定义';

-- =============================================
-- 2. 事件模型表（brms_event_model）
-- 存储事件对应的奖励规则配置
-- =============================================
DROP TABLE IF EXISTS `brms_event_model`;
CREATE TABLE `brms_event_model` (
    `serial_no`    VARCHAR(32)  NOT NULL COMMENT '主键，模型唯一标识',
    `event_code`   VARCHAR(255) NOT NULL COMMENT '关联事件编码',
    `tenant_id`    VARCHAR(64)  NOT NULL COMMENT '租户ID，支持多租户',
    `model_name`   VARCHAR(128) NOT NULL COMMENT '模型名称',
    `model_type`   VARCHAR(255) NOT NULL COMMENT '模型类型：3-规则模型（奖励规则）',
    `model_config` JSON         NOT NULL COMMENT '规则配置（JSON格式）',
    `priority`     INT(11)      NOT NULL DEFAULT 1 COMMENT '优先级，支持同一事件多个规则，数字越大优先级越高',
    `status`       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `start_time`   DATETIME     DEFAULT NULL COMMENT '生效开始时间',
    `end_time`     DATETIME     DEFAULT NULL COMMENT '生效结束时间',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`    VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    `update_by`    VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`serial_no`),
    KEY `idx_event_code` (`event_code`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_priority` (`priority`),
    KEY `idx_status_time` (`status`, `start_time`, `end_time`),
    KEY `idx_tenant_event` (`tenant_id`, `event_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事件模型表：存储事件对应的奖励规则配置';


-- =============================================
-- 示例数据插入
-- =============================================

-- 1. 插入事件定义示例数据
INSERT INTO `brms_event` (`serial_no`, `tenant_id`, `event_name`, `event_code`, `event_level`, `event_type`, `description`, `create_by`) VALUES
('EVT001', 'tenant_001', '用户登录', 'USER_LOGIN', '1', 'USER', '用户每日登录事件', 'admin'),
('EVT002', 'tenant_001', '分享内容', 'USER_SHARE', '1', 'USER', '用户分享内容到社交平台', 'admin'),
('EVT003', 'tenant_001', '连续签到', 'USER_CHECKIN', '1', 'USER', '用户连续签到事件', 'admin'),
('EVT004', 'tenant_001', '完善资料', 'USER_PROFILE', '1', 'USER', '用户完善个人资料', 'admin'),
('EVT005', 'tenant_001', '首次下单', 'ORDER_FIRST', '2', 'ORDER', '用户首次下单事件', 'admin'),
('EVT006', 'tenant_001', '完成订单', 'ORDER_COMPLETE', '2', 'ORDER', '用户完成订单支付', 'admin'),
('EVT007', 'tenant_001', '好评晒图', 'ORDER_REVIEW', '2', 'ORDER', '用户对订单进行好评并晒图', 'admin'),
('EVT008', 'tenant_001', '邀请好友', 'USER_INVITE', '1', 'USER', '用户邀请好友注册', 'admin'),
('EVT009', 'tenant_001', '好友下单', 'FRIEND_ORDER', '1', 'USER', '被邀请好友首次下单', 'admin'),
('EVT010', 'tenant_001', '发布动态', 'USER_POST', '1', 'USER', '用户发布动态内容', 'admin');

-- 2. 插入奖励规则配置示例数据
INSERT INTO `brms_event_model` (`serial_no`, `event_code`, `tenant_id`, `model_name`, `model_type`, `model_config`, `priority`, `create_by`) VALUES

-- 每日登录奖励
('MDL001', 'USER_LOGIN', 'tenant_001', '每日登录积分奖励', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'FIXED',
            'amount', 10,
            'description', '每日登录积分奖励'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 1,
        'totalLimit', 0
    ),
    'conditions', JSON_ARRAY(),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin'),

-- 分享内容随机奖励
('MDL002', 'USER_SHARE', 'tenant_001', '分享内容随机奖励', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'RANDOM',
            'minAmount', 5,
            'maxAmount', 20,
            'description', '分享内容随机积分奖励'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 3,
        'totalLimit', 0
    ),
    'conditions', JSON_ARRAY(),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin'),

-- 连续签到公式奖励
('MDL003', 'USER_CHECKIN', 'tenant_001', '连续签到递增奖励', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'FORMULA',
            'formula', 'days * 5',
            'description', '连续签到天数递增奖励'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 1,
        'totalLimit', 0
    ),
    'conditions', JSON_ARRAY(
        JSON_OBJECT(
            'field', 'days',
            'operator', 'lte',
            'value', 7,
            'description', '最多7天连续签到'
        )
    ),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin'),

-- 完善资料一次性奖励
('MDL004', 'USER_PROFILE', 'tenant_001', '完善资料奖励', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'FIXED',
            'amount', 50,
            'description', '完善个人资料奖励'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 0,
        'totalLimit', 1
    ),
    'conditions', JSON_ARRAY(),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin'),

-- 首次下单奖励
('MDL005', 'ORDER_FIRST', 'tenant_001', '首次下单奖励', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'FIXED',
            'amount', 100,
            'description', '首次下单奖励'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 0,
        'totalLimit', 1
    ),
    'conditions', JSON_ARRAY(
        JSON_OBJECT(
            'field', 'isFirstOrder',
            'operator', 'eq',
            'value', true,
            'description', '用户首单'
        )
    ),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin'),

-- 订单返积分（比例奖励）
('MDL006', 'ORDER_COMPLETE', 'tenant_001', '订单完成返积分', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'PERCENT',
            'amount', 1,
            'field', 'orderAmount',
            'description', '订单金额1%返积分'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 0,
        'totalLimit', 0
    ),
    'conditions', JSON_ARRAY(
        JSON_OBJECT(
            'field', 'orderAmount',
            'operator', 'gte',
            'value', 100,
            'description', '订单金额≥100元'
        )
    ),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin'),

-- 邀请好友奖励
('MDL007', 'USER_INVITE', 'tenant_001', '邀请好友注册奖励', '3', JSON_OBJECT(
    'rewards', JSON_ARRAY(
        JSON_OBJECT(
            'assetType', 'POINTS',
            'amountType', 'FIXED',
            'amount', 200,
            'description', '邀请好友注册奖励'
        )
    ),
    'limits', JSON_OBJECT(
        'dailyLimit', 0,
        'totalLimit', 0
    ),
    'conditions', JSON_ARRAY(
        JSON_OBJECT(
            'field', 'inviteStatus',
            'operator', 'eq',
            'value', 'SUCCESS',
            'description', '好友成功注册'
        )
    ),
    'effectTime', JSON_OBJECT(
        'startTime', NULL,
        'endTime', NULL
    )
), 1, 'admin');

-- =============================================
-- 常用查询语句示例
-- =============================================

-- 查询某租户下的所有事件
-- SELECT * FROM brms_event WHERE tenant_id = 'tenant_001' AND status = 1;

-- 查询某事件的所有激活规则
-- SELECT * FROM brms_event_model 
-- WHERE tenant_id = 'tenant_001' 
--   AND event_code = 'USER_LOGIN' 
--   AND status = 1 
--   AND (start_time IS NULL OR start_time <= NOW()) 
--   AND (end_time IS NULL OR end_time >= NOW())
-- ORDER BY priority DESC;

-- 查询特定事件和模型配置
-- SELECT 
--     e.event_name,
--     e.event_type,
--     m.model_name,
--     m.model_config,
--     m.priority
-- FROM brms_event e
-- JOIN brms_event_model m ON e.event_code = m.event_code AND e.tenant_id = m.tenant_id
-- WHERE e.tenant_id = 'tenant_001'
--   AND e.status = 1
--   AND m.status = 1
-- ORDER BY e.event_code, m.priority DESC;

-- 查询按优先级排序的奖励规则
-- SELECT 
--     event_code,
--     model_name,
--     JSON_EXTRACT(model_config, '$.rewards[0].amountType') as amount_type,
--     JSON_EXTRACT(model_config, '$.rewards[0].amount') as amount,
--     JSON_EXTRACT(model_config, '$.limits.dailyLimit') as daily_limit,
--     priority
-- FROM brms_event_model
-- WHERE tenant_id = 'tenant_001' 
--   AND status = 1
-- ORDER BY event_code, priority DESC;
