--2.3.42
update t_cbb_terminal set auth_mode=platform;

--2.4.1

UPDATE t_sk_global_parameter SET param_value = '[]', default_value = '[]' WHERE param_key like '%terminal_license_num';

-- 更新旧终端的cpu架构为x86_64
UPDATE t_cbb_terminal set cpu_arch = 'X86_64';

-- 更新旧的升级包的升级支持cpu类型
UPDATE  t_cbb_sys_upgrade_package set cpu_arch = 'X86_64',support_cpu = 'ALL' where package_type = 'IDV_LINUX' or package_type = 'VDI_LINUX';


-- 更新数据库全局表终端组件包状态记录
UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_android_arm', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_android';

UPDATE t_sk_global_parameter SET param_key = 'terminal_component_package_init_status_linux_x86', param_value = 'fail' WHERE param_key = 'terminal_component_package_init_status_linux';

INSERT INTO t_sk_global_parameter VALUES('f7ca0970-df0e-48d1-881d-0e8416c47fd5','terminal_component_package_init_status_linux_arm','fail','fail','2021-08-26 10:51:33','2021-08-26 10:51:33',0);


--2.4.2
-- 内置的ocs型号sn的第4、6、7位 RG-OCS-256 对应的PA9  | RG-OCS-128对应的PAA
INSERT INTO "public"."t_cbb_terminal_authorization_whitelist" VALUES ('9b5eca97-1ad2-43ed-938d-e77abea3d694','RG-OCS-128_PAA',10, now(),0);
INSERT INTO "public"."t_cbb_terminal_authorization_whitelist" VALUES ('745f2751-cc85-4f66-b265-505ac44acdaf','RG-OCS-256_PA9',10, now(),0);


