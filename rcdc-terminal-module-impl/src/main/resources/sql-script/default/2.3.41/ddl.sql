-- 修改all_disk_info字段长度
alter table t_cbb_terminal alter column all_disk_info type varchar(4096);
