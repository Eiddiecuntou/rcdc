-- 更新数据库全局表终端组件初始化信息
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_android', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_vdi_android';
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_linux', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_vdi_linux';
DELETE FROM t_sk_global_parameter WHERE param_key = 'terminal_component_package_init_status_idv_linux';
