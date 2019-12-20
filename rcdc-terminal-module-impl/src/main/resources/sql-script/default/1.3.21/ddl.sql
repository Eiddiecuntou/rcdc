/** 终端表新增idv模式、无线网络ssid、无线认证方式字段 */
ALTER table t_cbb_terminal ADD COLUMN ssid varchar(256);
ALTER table t_cbb_terminal ADD COLUMN wireless_auth_mode varchar(256);

CREATE TABLE t_cbb_terminal_network_info (
  "id" uuid NOT NULL
  CONSTRAINT "t_cbb_terminal_network_info_pkey" PRIMARY KEY ,
  "terminal_id" varchar(32)  NOT NULL,
  "mac_addr" varchar(64),
  "ip" varchar(64),
  "subnet_mask" varchar(64),
  "gateway" varchar(64),
  "main_dns" varchar(64),
  "second_dns" varchar(64),
  "get_ip_mode" varchar(32),
  "get_dns_mode" varchar(32),
  "network_access_mode" varchar(32),
  "ssid" varchar(256),
  "wireless_auth_mode" varchar(256),
  "version" int4 DEFAULT 1
)
;

COMMENT ON COLUMN t_cbb_terminal_network_info.terminal_id IS '终端唯一标识,由终端主动上报，约定为第一张网卡的mac地址';

COMMENT ON COLUMN t_cbb_terminal_network_info.mac_addr IS '终端mac地址，多个以逗号分隔';

COMMENT ON COLUMN t_cbb_terminal_network_info.ip IS '终端ip地址';

COMMENT ON COLUMN t_cbb_terminal_network_info.subnet_mask IS '子网掩码';

COMMENT ON COLUMN t_cbb_terminal_network_info.gateway IS '网关';

COMMENT ON COLUMN t_cbb_terminal_network_info.main_dns IS '首先DNS';

COMMENT ON COLUMN t_cbb_terminal_network_info.second_dns IS '备选dns';

COMMENT ON COLUMN t_cbb_terminal_network_info.get_ip_mode IS '获取ip方式，0自动获取，1手动填入';

COMMENT ON COLUMN t_cbb_terminal_network_info.get_dns_mode IS '获取dns方式，0自动获取，1手动填写';

COMMENT ON COLUMN t_cbb_terminal_network_info.network_access_mode IS '网络接入方式:无线接入，有线接入';

COMMENT ON COLUMN t_cbb_terminal_network_info.version IS '版本号，实现乐观锁';

COMMENT ON COLUMN t_cbb_terminal_network_info.ssid IS '无线SSID';

COMMENT ON COLUMN t_cbb_terminal_network_info.wireless_auth_mode IS '无线认证模式';

COMMENT ON TABLE t_cbb_terminal_network_info IS '终端网络信息表';

