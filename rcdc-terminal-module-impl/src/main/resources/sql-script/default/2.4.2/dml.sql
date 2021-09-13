-- 更新旧的安卓升级包的升级支持cpu类型
UPDATE  t_cbb_sys_upgrade_package set cpu_arch = 'ARM',support_cpu = 'ALL' where package_type = 'VDI_ANDROID';

