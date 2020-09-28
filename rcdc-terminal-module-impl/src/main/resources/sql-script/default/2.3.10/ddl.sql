/** 终端表增加无线网卡数量、以太网卡数量、所有磁盘信息3个字段 */
ALTER table t_cbb_terminal ADD COLUMN wireless_net_card_num int4 default 0;
ALTER table t_cbb_terminal ADD COLUMN ethernet_net_card_num int4 default 0;
ALTER table t_cbb_terminal ADD COLUMN all_disk_info varchar(2048);

COMMENT ON COLUMN t_cbb_terminal.wireless_net_card_num IS '无线网卡数量';
COMMENT ON COLUMN t_cbb_terminal.ethernet_net_card_num IS '以太网卡数量';
COMMENT ON COLUMN t_cbb_terminal.all_disk_info IS '所有磁盘信息';