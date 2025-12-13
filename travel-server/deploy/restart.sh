#!/bin/bash

# 旅游系统后端重启脚本
# 使用方法: ./restart.sh [dev|test|prod]

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
ENV=${1:-prod}

echo "正在重启应用 (环境: $ENV)..."

# 停止
"$SCRIPT_DIR/stop.sh"

# 等待
sleep 2

# 启动
"$SCRIPT_DIR/start.sh" "$ENV"



















































