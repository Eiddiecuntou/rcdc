-- 增加终端检测时，是否开启代理字段
ALTER TABLE t_cbb_terminal_detection ADD COLUMN IF NOT EXISTS enable_proxy BOOLEAN DEFAULT FALSE;
COMMENT ON COLUMN t_cbb_terminal_detection.enable_proxy IS '检测时，终端是否有开启代理';