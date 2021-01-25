-- 新增VOI终端启动方式字段
ALTER table t_cbb_terminal ADD COLUMN start_mode varchar(32) default 'AUTO';
COMMENT ON COLUMN t_cbb_terminal.start_mode IS 'VOI终端启动方式';

ALTER table t_cbb_terminal ADD COLUMN support_tc_start  bool DEFAULT FALSE;;
COMMENT ON COLUMN t_cbb_terminal.support_tc_start IS '终端是否支持TC启动';
