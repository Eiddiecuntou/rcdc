-- 更新数据库全局表终端组件初始化信息
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_android', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_vdi_android';
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_linux', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_vdi_linux';
DELETE FROM t_sk_global_parameter WHERE param_key = 'terminal_component_package_init_status_idv_linux';
-- 新增字段
ALTER table t_cbb_terminal ADD COLUMN all_net_card_mac_info varchar(512);
COMMENT ON COLUMN t_cbb_terminal.all_net_card_mac_info IS '终端所有网卡mac地址';
