-- ============================================
-- 手机号登录功能 - 数据库初始化SQL
-- 执行前请备份数据库
-- ============================================

-- 1. 添加phone字段（如果不存在）
-- MySQL 8.0+ 支持 IF NOT EXISTS
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT '手机号' AFTER email;

-- MySQL 5.7 及以下版本，请先检查字段是否存在，如果不存在再执行：
-- ALTER TABLE sys_user ADD COLUMN phone VARCHAR(20) COMMENT '手机号' AFTER email;

-- 2. 删除可能存在的旧索引（如果存在）
ALTER TABLE sys_user DROP INDEX IF EXISTS uk_phone;
ALTER TABLE sys_user DROP INDEX IF EXISTS idx_phone;

-- 3. 添加唯一索引（确保手机号唯一）
ALTER TABLE sys_user ADD UNIQUE INDEX uk_phone (phone);

-- 4. 添加普通索引（提高查询性能）
ALTER TABLE sys_user ADD INDEX idx_phone (phone);

-- ============================================
-- 验证SQL执行结果
-- ============================================

-- 查看表结构
DESC sys_user;

-- 查看phone字段的索引
SHOW INDEX FROM sys_user WHERE Column_name = 'phone';
