/** 添加刷机任务升级包名类型字段 */
ALTER TABLE t_cbb_sys_upgrade ADD COLUMN package_type varchar(32);
ALTER TABLE t_cbb_sys_upgrade ADD COLUMN upgrade_mode varchar(32);

/** 添加刷机包表文件MD5、种子文件路径、种子文件MD5、升级类型字段*/
ALTER TABLE t_cbb_sys_upgrade_package ADD COLUMN file_md5 varchar(128),
ADD COLUMN seed_path varchar(128),
ADD COLUMN seed_md5 varchar(128);

/** 添加终端内部系统版本号字段 */
ALTER TABLE t_cbb_terminal ADD COLUMN os_inner_version varchar(32);
COMMENT ON COLUMN t_cbb_terminal.os_inner_version IS '终端内部系统版本号';


