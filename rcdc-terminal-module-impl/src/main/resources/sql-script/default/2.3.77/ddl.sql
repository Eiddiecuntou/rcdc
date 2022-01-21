-- 新增IDV终端授权证书类型
ALTER table t_cbb_terminal ADD COLUMN license_type varchar(64);
COMMENT ON COLUMN t_cbb_terminal.license_type IS 'IDV终端授权证书类型';