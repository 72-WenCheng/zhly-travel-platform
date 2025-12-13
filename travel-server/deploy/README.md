# 部署脚本使用说明

## 脚本列表

- `start.sh` - 启动应用
- `stop.sh` - 停止应用
- `restart.sh` - 重启应用
- `status.sh` - 查看应用状态

## 使用方法

### 1. 赋予执行权限

```bash
chmod +x start.sh stop.sh restart.sh status.sh
```

### 2. 启动应用

```bash
# 生产环境（默认）
./start.sh prod

# 开发环境
./start.sh dev

# 测试环境
./start.sh test
```

### 3. 停止应用

```bash
./stop.sh
```

### 4. 重启应用

```bash
# 生产环境
./restart.sh prod

# 开发环境
./restart.sh dev
```

### 5. 查看状态

```bash
./status.sh
```

### 6. 查看日志

```bash
# 实时查看日志
tail -f ../logs/server.log

# 查看最后100行
tail -n 100 ../logs/server.log
```

## 注意事项

1. 确保 `travel-server.jar` 文件在项目根目录
2. 确保已安装 Java 17
3. 确保端口 8070 未被占用
4. 首次运行前，请配置好 `application-prod.yml`

## 系统服务配置（可选）

如果需要设置为系统服务，可以使用 `systemd`：

```bash
# 创建服务文件
sudo vim /etc/systemd/system/travel-server.service
```

内容：

```ini
[Unit]
Description=Travel Server Application
After=network.target mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/travel-server
ExecStart=/opt/travel-server/deploy/start.sh prod
ExecStop=/opt/travel-server/deploy/stop.sh
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用服务：

```bash
sudo systemctl daemon-reload
sudo systemctl enable travel-server
sudo systemctl start travel-server
sudo systemctl status travel-server
```



















































