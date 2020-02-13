/** 终端系统升级包表新增ota脚本路径、ota脚本MD5值字段 */
ALTER table t_cbb_sys_upgrade_package ADD COLUMN ota_script_path varchar(256);
ALTER table t_cbb_sys_upgrade_package ADD COLUMN ota_script_md5 varchar(64);