-- 新增字段
ALTER table t_cbb_terminal ADD COLUMN all_net_card_mac_info varchar(512);
COMMENT ON COLUMN t_cbb_terminal.all_net_card_mac_info IS '终端所有网卡mac地址';
-- 新增字段
ALTER TABLE t_cbb_terminal ADD COLUMN authed bool default true ;
COMMENT ON COLUMN t_cbb_terminal.authed IS '终端是否授权';