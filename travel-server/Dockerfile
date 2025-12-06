# 多阶段构建 - 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

# 安装必要的工具
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

WORKDIR /app

# 从构建阶段复制 jar 文件
COPY --from=builder /build/target/*.jar app.jar

# 创建必要的目录
RUN mkdir -p /app/upload /app/logs

# 暴露端口
EXPOSE 8070

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8070/api/health/check || exit 1

# 启动应用
ENTRYPOINT ["java", \
  "-Xms512m", \
  "-Xmx1024m", \
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=200", \
  "-XX:+HeapDumpOnOutOfMemoryError", \
  "-XX:HeapDumpPath=/app/logs/heapdump.hprof", \
  "-jar", \
  "app.jar", \
  "--spring.profiles.active=prod"]
