-- 用户表结构升级SQL (SQL Server版本)

-- 1. 添加用户角色字段
ALTER TABLE user_info ADD user_role NVARCHAR(20) DEFAULT 'OPERATOR';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户角色（ADMIN-管理员，OPERATOR-操作员）', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'user_info', @level2type=N'COLUMN', @level2name=N'user_role';

-- 2. 添加登录失败次数字段
ALTER TABLE user_info ADD login_fail_count INT DEFAULT 0;
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'登录失败次数', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'user_info', @level2type=N'COLUMN', @level2name=N'login_fail_count';

-- 3. 添加是否锁定字段
ALTER TABLE user_info ADD is_locked INT DEFAULT 0;
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否锁定（0-否，1-是）', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'user_info', @level2type=N'COLUMN', @level2name=N'is_locked';

-- 5. 添加更新时间字段
ALTER TABLE user_info ADD update_time DATETIME DEFAULT GETDATE();
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'更新时间', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'user_info', @level2type=N'COLUMN', @level2name=N'update_time';

-- 6. 添加user_code唯一性约束（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'UK_user_code' AND object_id = OBJECT_ID('user_info'))
BEGIN
ALTER TABLE user_info ADD CONSTRAINT UK_user_code UNIQUE (user_code);
END

-- 7. 插入默认管理员账号（如果不存在）
IF NOT EXISTS (SELECT 1 FROM user_info WHERE user_code = 'admin')
BEGIN
    INSERT INTO user_info (user_id, user_code, user_password, user_name, user_role, login_fail_count, is_locked, create_time, update_time)
    VALUES (1, 'admin', 'wcs-admin', N'系统管理员', 'ADMIN', 0, 0, GETDATE(), GETDATE());
END

-- 8. 更新现有用户为操作员角色
UPDATE user_info SET user_role = 'OPERATOR' WHERE user_code != 'admin' AND user_role IS NULL;
