/**将终端类型VDI修改为VDI_LINUX**/
UPDATE t_cbb_sys_upgrade_package
SET package_type = "VDI_LINUX"
WHERE package_type = "VDI";
