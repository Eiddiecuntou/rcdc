-- 更新数据库全局表终端组件初始化信息
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_android', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_vdi_android';
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_linux', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_vdi_linux';
DELETE FROM t_sk_global_parameter WHERE param_key = 'terminal_component_package_init_status_idv_linux';
-- 新增字段
ALTER table t_cbb_terminal ADD COLUMN all_net_card_mac_info varchar(512);
COMMENT ON COLUMN t_cbb_terminal.all_net_card_mac_info IS '终端所有网卡mac地址';
-- 新增字段
ALTER TABLE t_cbb_terminal ADD COLUMN authed bool default true ;
COMMENT ON COLUMN t_cbb_terminal.authed IS '终端是否授权';
-- 新增字段
ALTER table t_cbb_terminal ADD COLUMN data_disk_size int8;
COMMENT ON COLUMN t_cbb_terminal.data_disk_size IS '数据分区大小';

-- 新增字段
ALTER table t_cbb_terminal ADD COLUMN support_work_mode varchar(128);
COMMENT ON COLUMN t_cbb_terminal.support_work_mode IS '终端支持模式';