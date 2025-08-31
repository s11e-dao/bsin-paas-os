#!/bin/bash

# Seata 数据库初始化脚本
echo "正在初始化 Seata 数据库..."

# 等待 MySQL 启动
echo "等待 MySQL 启动..."
until mysql -h bsin-mysql-3.0 -u root -p123456 -e "SELECT 1" >/dev/null 2>&1; do
    echo "MySQL 尚未就绪，等待 5 秒..."
    sleep 5
done

echo "MySQL 已就绪，开始创建 Seata 数据库和表..."

# 创建数据库
mysql -h bsin-mysql-3.0 -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS \`bsin-seata\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 创建表
mysql -h bsin-mysql-3.0 -u root -p123456 bsin-seata <<EOF
-- -------------------------------- The script used when storeMode is 'db' --------------------------------
-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS \`global_table\`
(
    \`xid\`                       VARCHAR(128) NOT NULL,
    \`transaction_id\`            BIGINT,
    \`status\`                    TINYINT      NOT NULL,
    \`application_id\`            VARCHAR(32),
    \`transaction_service_group\` VARCHAR(32),
    \`transaction_name\`          VARCHAR(128),
    \`timeout\`                   INT,
    \`begin_time\`                BIGINT,
    \`application_data\`          VARCHAR(2000),
    \`gmt_create\`                DATETIME,
    \`gmt_modified\`              DATETIME,
    PRIMARY KEY (\`xid\`),
    KEY \`idx_gmt_modified_status\` (\`gmt_modified\`, \`status\`),
    KEY \`idx_transaction_id\` (\`transaction_id\`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS \`branch_table\`
(
    \`branch_id\`         BIGINT       NOT NULL,
    \`xid\`               VARCHAR(128) NOT NULL,
    \`transaction_id\`    BIGINT,
    \`resource_group_id\` VARCHAR(32),
    \`resource_id\`       VARCHAR(256),
    \`branch_type\`       VARCHAR(8),
    \`status\`            TINYINT,
    \`client_id\`         VARCHAR(64),
    \`application_data\`  VARCHAR(2000),
    \`gmt_create\`        DATETIME(6),
    \`gmt_modified\`      DATETIME(6),
    PRIMARY KEY (\`branch_id\`),
    KEY \`idx_xid\` (\`xid\`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS \`lock_table\`
(
    \`row_key\`        VARCHAR(128) NOT NULL,
    \`xid\`            VARCHAR(128),
    \`transaction_id\` BIGINT,
    \`branch_id\`      BIGINT       NOT NULL,
    \`resource_id\`    VARCHAR(256),
    \`table_name\`     VARCHAR(32),
    \`pk\`             VARCHAR(36),
    \`gmt_create\`     DATETIME,
    \`gmt_modified\`   DATETIME,
    PRIMARY KEY (\`row_key\`),
    KEY \`idx_branch_id\` (\`branch_id\`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- for AT mode you must to init this sql for you business database. the seata server not need it.
CREATE TABLE IF NOT EXISTS \`undo_log\`
(
    \`branch_id\`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    \`xid\`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    \`context\`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    \`rollback_info\` LONGBLOB     NOT NULL COMMENT 'rollback info',
    \`log_status\`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    \`log_created\`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    \`log_modified\`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY \`ux_undo_log\` (\`xid\`, \`branch_id\`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
EOF

echo "Seata 数据库初始化完成！"
