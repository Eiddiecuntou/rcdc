/** 修改终端检测时延数据类型 */
ALTER TABLE t_cbb_terminal_detection ALTER COLUMN network_delay TYPE numeric(24,2);

/** 新增终端检测记录创建时间 */
ALTER TABLE t_cbb_terminal_detection ADD column create_time timestamp(6);