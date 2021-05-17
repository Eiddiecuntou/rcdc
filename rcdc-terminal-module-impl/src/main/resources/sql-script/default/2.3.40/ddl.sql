-- 修改all_disk_info字段长度
alter table t_cbb_terminal alter column all_disk_info type varchar(4096);

alter table t_cbb_terminal add column auth_mode varchar(64);

update t_cbb_terminal set auth_mode=platform;