#!/bin/bash

# Docker 部署脚本

set -e

echo "=========================================="
echo "旅游系统 Docker 部署脚本"
echo "=========================================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ 错误: 未安装 Docker，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ 错误: 未安装 Docker Compose，请先安装 Docker Compose"
    exit 1
fi

# 进入项目目录
SCRIPT_DIR=$(cd "$(dirname "$0")/.." && pwd)
cd "$SCRIPT_DIR"

# 检查环境变量文件
if [ ! -f .env ]; then
    echo "⚠️  未找到 .env 文件，使用默认配置"
    echo "建议创建 .env 文件配置数据库密码等敏感信息"
fi

# 选择部署模式
MODE=${1:-dev}

case $MODE in
    prod)
        echo "🚀 生产环境部署模式"
        COMPOSE_FILE="-f docker-compose.yml -f docker-compose.prod.yml"
        ;;
    dev)
        echo "🔧 开发环境部署模式"
        COMPOSE_FILE="-f docker-compose.yml"
        ;;
    *)
        echo "❌ 未知模式: $MODE"
        echo "使用方法: $0 [dev|prod]"
        exit 1
        ;;
esac

# 停止旧容器
echo ""
echo "停止旧容器..."
docker-compose $COMPOSE_FILE down

# 构建镜像
echo ""
echo "构建镜像..."
docker-compose $COMPOSE_FILE build --no-cache

# 启动服务
echo ""
echo "启动服务..."
docker-compose $COMPOSE_FILE up -d

# 等待服务启动
echo ""
echo "等待服务启动..."
sleep 10

# 检查服务状态
echo ""
echo "服务状态:"
docker-compose $COMPOSE_FILE ps

# 显示日志
echo ""
echo "=========================================="
echo "部署完成！"
echo "=========================================="
echo ""
echo "查看日志: docker-compose $COMPOSE_FILE logs -f"
echo "停止服务: docker-compose $COMPOSE_FILE down"
echo "重启服务: docker-compose $COMPOSE_FILE restart"
echo ""
echo "服务地址:"
echo "  前端: http://localhost"
echo "  后端: http://localhost:8070"
echo "  API: http://localhost/api"
echo ""



















