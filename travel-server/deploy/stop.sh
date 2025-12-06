#!/bin/bash

# 旅游系统后端停止脚本

APP_NAME="travel-server"
APP_DIR=$(cd "$(dirname "$0")/.." && pwd)
PID_FILE="$APP_DIR/$APP_NAME.pid"

if [ ! -f "$PID_FILE" ]; then
    echo "应用未运行"
    exit 0
fi

PID=$(cat "$PID_FILE")

if ! ps -p "$PID" > /dev/null 2>&1; then
    echo "应用未运行 (PID 文件存在但进程不存在)"
    rm -f "$PID_FILE"
    exit 0
fi

echo "正在停止 $APP_NAME (PID: $PID)..."

# 优雅停止
kill "$PID"

# 等待进程结束
for i in {1..30}; do
    if ! ps -p "$PID" > /dev/null 2>&1; then
        echo "✅ $APP_NAME 已停止"
        rm -f "$PID_FILE"
        exit 0
    fi
    sleep 1
done

# 如果还在运行，强制杀死
if ps -p "$PID" > /dev/null 2>&1; then
    echo "强制停止..."
    kill -9 "$PID"
    sleep 1
    rm -f "$PID_FILE"
    echo "✅ $APP_NAME 已强制停止"
else
    rm -f "$PID_FILE"
    echo "✅ $APP_NAME 已停止"
fi

