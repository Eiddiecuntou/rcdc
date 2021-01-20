-- 新增VOI终端启动方式字段
ALTER table t_cbb_terminal ADD COLUMN start_mode varchar(32) default 'auto';
COMMENT ON COLUMN t_cbb_terminal.start_mode IS 'VOI终端启动方式';