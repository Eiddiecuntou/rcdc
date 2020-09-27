-- 新增字段
ALTER TABLE t_cbb_sys_upgrade ADD COLUMN flash_mode varchar(32);

-- 字段增加说明
COMMENT ON COLUMN t_cbb_sys_upgrade.flash_mode IS '刷机方式';

-- 旧数据缺省值
UPDATE t_cbb_sys_upgrade set flash_mode = 'FAST';

-- flash_mode 字段新增非空校验
ALTER TABLE t_cbb_sys_upgrade ALTER COLUMN flash_mode SET NOT NULL;

