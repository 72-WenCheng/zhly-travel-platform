#!/bin/bash

# 旅游系统后端状态检查脚本

APP_NAME="travel-server"
APP_DIR=$(cd "$(dirname "$0")/.." && pwd)
PID_FILE="$APP_DIR/$APP_NAME.pid"

if [ ! -f "$PID_FILE" ]; then
    echo "❌ 应用未运行"
    exit 1
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" > /dev/null 2>&1; then
    echo "✅ 应用正在运行"
    echo "PID: $PID"
    
    # 显示进程信息
    ps -p "$PID" -o pid,ppid,cmd,%mem,%cpu,etime
    
    # 显示端口占用
    echo ""
    echo "端口占用:"
    netstat -tlnp 2>/dev/null | grep "$PID" || ss -tlnp 2>/dev/null | grep "$PID"
else
    echo "❌ 应用未运行 (PID 文件存在但进程不存在)"
    rm -f "$PID_FILE"
    exit 1
fi









