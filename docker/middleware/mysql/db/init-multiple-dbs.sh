#!/bin/bash

set -e

# 从环境变量中获取数据库列表
IFS=',' read -ra DATABASES <<< "$MYSQL_MULTIPLE_DATABASES"

echo "MYSQL_MULTIPLE_DATABASES: $MYSQL_MULTIPLE_DATABASES"
echo "MYSQL_ROOT_PASSWORD: $MYSQL_ROOT_PASSWORD"
echo "DATABASES: ${DATABASES[@]}"

# 创建每个数据库 - 使用正确的 socket 路径
for db in "${DATABASES[@]}"; do
  echo "Creating database $db..."
  mysql -u root -p"$MYSQL_ROOT_PASSWORD" --socket=/var/lib/mysql/mysql.sock -e "CREATE DATABASE IF NOT EXISTS \`$db\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
done

# 导入 SQL 文件
if [ -d "/docker-entrypoint-initdb.d/sqls" ]; then
  for sql_file in /docker-entrypoint-initdb.d/sqls/*.sql; do
    if [ -f "$sql_file" ]; then
      db_name=$(basename "$sql_file" .sql)
      echo "Importing $sql_file into $db_name..."
      mysql -u root -p"$MYSQL_ROOT_PASSWORD" --socket=/var/lib/mysql/mysql.sock "$db_name" < "$sql_file"
      echo "Successfully imported $sql_file"
    fi
  done
else
  echo "Warning: /docker-entrypoint-initdb.d/sqls directory not found"
fi

echo "All databases created and initialized successfully!"