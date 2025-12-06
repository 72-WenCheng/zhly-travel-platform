#!/bin/bash

# 旅游系统后端启动脚本
# 使用方法: ./start.sh [dev|test|prod]

# 获取环境参数，默认为 prod
ENV=${1:-prod}

# 应用名称
APP_NAME="travel-server"
APP_JAR="travel-server.jar"
APP_DIR=$(cd "$(dirname "$0")/.." && pwd)
LOG_DIR="$APP_DIR/logs"
PID_FILE="$APP_DIR/$APP_NAME.pid"

# 创建日志目录
mkdir -p "$LOG_DIR"

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
    echo "错误: 未找到 Java，请先安装 Java 17"
    exit 1
fi

# 检查 jar 文件是否存在
if [ ! -f "$APP_DIR/$APP_JAR" ]; then
    echo "错误: 未找到 $APP_JAR 文件"
    exit 1
fi

# 检查是否已经运行
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "应用已经在运行中 (PID: $PID)"
        exit 1
    else
        rm -f "$PID_FILE"
    fi
fi

# JVM 参数配置
JVM_OPTS="-Xms512m -Xmx1024m"
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="$JVM_OPTS -XX:HeapDumpPath=$LOG_DIR/heapdump.hprof"

# 启动应用
echo "正在启动 $APP_NAME (环境: $ENV)..."
cd "$APP_DIR"

nohup java $JVM_OPTS -jar "$APP_JAR" \
    --spring.profiles.active=$ENV \
    > "$LOG_DIR/server.log" 2>&1 &

# 保存 PID
echo $! > "$PID_FILE"

# 等待启动
sleep 3

# 检查是否启动成功
if ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
    echo "✅ $APP_NAME 启动成功!"
    echo "PID: $(cat $PID_FILE)"
    echo "日志文件: $LOG_DIR/server.log"
    echo "查看日志: tail -f $LOG_DIR/server.log"
else
    echo "❌ $APP_NAME 启动失败，请查看日志: $LOG_DIR/server.log"
    rm -f "$PID_FILE"
    exit 1
fi

