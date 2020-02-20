/** 终端表新增idv模式、无线网络ssid、无线认证方式、网络信息字段 */
ALTER table t_cbb_terminal ADD COLUMN ssid varchar(256);
ALTER table t_cbb_terminal ADD COLUMN wireless_auth_mode varchar(256);
ALTER table t_cbb_terminal ADD COLUMN network_infos varchar(1024);


/** 终端系统升级包表新增ota脚本路径、ota脚本MD5值字段 */
ALTER table t_cbb_sys_upgrade_package ADD COLUMN ota_script_path varchar(256);
ALTER table t_cbb_sys_upgrade_package ADD COLUMN ota_script_md5 varchar(64);