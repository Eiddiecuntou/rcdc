
UPDATE t_sk_global_parameter SET param_value = '[]', default_value = '[]' WHERE param_key like '%terminal_license_num';

-- 更新旧终端的cpu架构为x86_64
UPDATE t_cbb_terminal set cpu_arch = 'X86_64';

-- 更新旧的升级包的升级支持cpu类型
UPDATE  t_cbb_sys_upgrade_package set cpu_arch = 'X86_64',support_cpu = 'ALL' where package_type = 'IDV_LINUX' or package_type = 'VDI_LINUX';


-- 更新数据库全局表终端组件包状态记录
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_android_arm', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_android';

UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_linux_x86', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_linux';

INSERT INTO t_sk_global_parameter VALUES('f7ca0970-df0e-48d1-881d-0e8416c47fd5','terminal_component_package_init_status_linux_arm','fail','fail','2021-08-26 10:51:33','2021-08-26 10:51:33',0);
