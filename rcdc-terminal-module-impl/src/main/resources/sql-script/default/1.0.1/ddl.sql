/** 修改刷机任务升级包名称长度为128 */
ALTER TABLE t_cbb_sys_upgrade alter COLUMN package_name type varchar(128);

/** 添加刷机包表逻辑删除字段 */
ALTER TABLE t_cbb_sys_upgrade_package ADD COLUMN is_delete bool DEFAULT FALSE;