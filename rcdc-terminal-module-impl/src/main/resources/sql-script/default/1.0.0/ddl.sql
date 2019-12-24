/**终端表*/
CREATE TABLE t_cbb_terminal
(
  id                   UUID        NOT NULL
    CONSTRAINT t_cbb_terminal_pkey
    PRIMARY KEY,
  terminal_name        VARCHAR(32),
  terminal_id          VARCHAR(32) NOT NULL,
  mac_addr             VARCHAR(64),
  ip                   VARCHAR(64),
  subnet_mask          VARCHAR(64),
  gateway              VARCHAR(64),
  main_dns             VARCHAR(64),
  second_dns           VARCHAR(64),
  get_ip_mode          VARCHAR(32),
  get_dns_mode         VARCHAR(32),
  product_type         VARCHAR(32),
  terminal_type        VARCHAR(32),
  serial_number        VARCHAR(64),
  cpu_type             VARCHAR(64),
  memory_size          BIGINT,
  disk_size            BIGINT,
  terminal_os_type     VARCHAR(32),
  terminal_os_version  VARCHAR(32),
  rain_os_version      VARCHAR(32),
  rain_upgrade_version VARCHAR(32),
  hardware_version     VARCHAR(32),
  network_access_mode  VARCHAR(32),
  create_time          TIMESTAMP(6),
  last_online_time     TIMESTAMP(6),
  last_offline_time    TIMESTAMP(6),
  version              INTEGER DEFAULT 1,
  state                VARCHAR(64) NOT NULL,
  platform             VARCHAR(64)
);

CREATE UNIQUE INDEX t_rcdc_terminal_basic_info_terminal_id_uindex
  ON t_cbb_terminal (terminal_id);

COMMENT ON TABLE t_cbb_terminal IS '终端基本信息表';

COMMENT ON COLUMN t_cbb_terminal.terminal_name IS '终端名称';

COMMENT ON COLUMN t_cbb_terminal.terminal_id IS '终端唯一标识,由终端主动上报，约定为第一张网卡的mac地址';

COMMENT ON COLUMN t_cbb_terminal.mac_addr IS '终端mac地址，多个以逗号分隔';

COMMENT ON COLUMN t_cbb_terminal.ip IS '终端ip地址';

COMMENT ON COLUMN t_cbb_terminal.subnet_mask IS '子网掩码';

COMMENT ON COLUMN t_cbb_terminal.gateway IS '网关';

COMMENT ON COLUMN t_cbb_terminal.main_dns IS '首先DNS';

COMMENT ON COLUMN t_cbb_terminal.second_dns IS '备选dns';

COMMENT ON COLUMN t_cbb_terminal.get_ip_mode IS '获取ip方式，0自动获取，1手动填入';

COMMENT ON COLUMN t_cbb_terminal.get_dns_mode IS '获取dns方式，0自动获取，1手动填写';

COMMENT ON COLUMN t_cbb_terminal.product_type IS '产品型号:Rain100S,Rain200V2...';

COMMENT ON COLUMN t_cbb_terminal.terminal_type IS '终端类型：idv，vdi';

COMMENT ON COLUMN t_cbb_terminal.serial_number IS '产品序列号';

COMMENT ON COLUMN t_cbb_terminal.cpu_type IS 'cup型号';

COMMENT ON COLUMN t_cbb_terminal.memory_size IS '内存大小，单位B';

COMMENT ON COLUMN t_cbb_terminal.disk_size IS '磁盘大小，单位B';

COMMENT ON COLUMN t_cbb_terminal.terminal_os_type IS '终端操作系统类型，Android、Linux。。。';

COMMENT ON COLUMN t_cbb_terminal.terminal_os_version IS '终端操作系统版本';

COMMENT ON COLUMN t_cbb_terminal.rain_os_version IS '终端系统版本号';

COMMENT ON COLUMN t_cbb_terminal.rain_upgrade_version IS '软件版本号，指组件升级包的版本号';

COMMENT ON COLUMN t_cbb_terminal.hardware_version IS '硬件版本号';

COMMENT ON COLUMN t_cbb_terminal.network_access_mode IS '网络接入方式:无线接入，有线接入';

COMMENT ON COLUMN t_cbb_terminal.create_time IS '数据创建时间';

COMMENT ON COLUMN t_cbb_terminal.last_online_time IS '终端最近接入的时间点';

COMMENT ON COLUMN t_cbb_terminal.last_offline_time IS '终端最近一次离线的时间点';

COMMENT ON COLUMN t_cbb_terminal.version IS '版本号，实现乐观锁';

COMMENT ON COLUMN t_cbb_terminal.state IS '终端状态：离线，在线 升级中';

COMMENT ON COLUMN t_cbb_terminal.platform IS '平台';



/**终端检测表*/
create table t_cbb_terminal_detection
(
  "id" uuid NOT NULL,
  "ip_conflict" int2,
  "ip_conflict_mac" varchar(32) ,
  "access_internet" int2,
  "bandwidth" float4,
  "packet_loss_rate" int2,
  "network_delay" int4,
  "detect_time" timestamp(6),
  "terminal_id" varchar(32)  NOT NULL,
  "detect_state" varchar(64)  NOT NULL,
  "detect_fail_msg" varchar(128) ,
  "version" int4 DEFAULT 1
);

comment on column t_cbb_terminal_detection.ip_conflict
is 'ip是否冲突，0不冲突，1冲突';

comment on column t_cbb_terminal_detection.ip_conflict_mac
is '当有ip冲突的时候，此字段保存冲突的mac地址';

comment on column t_cbb_terminal_detection.access_internet
is '是否可访问互联网，0不可访问，1可访问';

comment on column t_cbb_terminal_detection.bandwidth
is '带宽';

comment on column t_cbb_terminal_detection.packet_loss_rate
is '丢包率';

comment on column t_cbb_terminal_detection.network_delay
is '时延';

comment on column t_cbb_terminal_detection.detect_time
is '检测时间';

comment on column t_cbb_terminal_detection.terminal_id
is '终端id';

comment on column t_cbb_terminal_detection.detect_state
is '终端检测状态';

comment on column t_cbb_terminal_detection.detect_fail_msg
is '检测失败原因';

comment on column t_cbb_terminal_detection.version
is '版本号，实现乐观锁';

ALTER TABLE t_cbb_terminal_detection ADD CONSTRAINT "t_terminal_detection_pkey" PRIMARY KEY ("id");

/** 终端系统升级包*/
CREATE TABLE t_cbb_sys_upgrade_package (
  "id" uuid NOT NULL,
  "img_name" varchar(64) ,
  "package_type" varchar(32) ,
  "upload_time" timestamp(6),
  "package_version" varchar(64) ,
  "version" int4 NOT NULL DEFAULT 0,
  "origin" varchar(32) ,
  "distribution_mode" varchar(32) ,
  "package_name" varchar(128) ,
  "file_path" varchar(128) 
);

COMMENT ON COLUMN t_cbb_sys_upgrade_package.img_name IS '升级包名称';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.package_type IS '升级包类型，包括android升级包、Linux vdi升级包、Linux IDV升级包';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.upload_time IS '上传时间';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.package_version IS '升级包版本号';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.version IS '版本号';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.origin IS '系统刷机包来源';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.distribution_mode IS '分发方式';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.package_name IS '升级包名称';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.file_path IS '刷机包存放路径';
COMMENT ON TABLE t_cbb_sys_upgrade_package IS '终端系统升级包表';

ALTER TABLE t_cbb_sys_upgrade_package ADD CONSTRAINT "t_termianl_system_upgrade_package_pkey" PRIMARY KEY ("id");


/** 刷机表 */
CREATE TABLE t_cbb_sys_upgrade (
  "id" uuid NOT NULL,
  "upgrade_package_id" uuid NOT NULL,
  "package_version" varchar(32)  NOT NULL,
  "package_name" varchar(64) ,
  "create_time" timestamp(0) NOT NULL,
  "state" varchar(32) ,
  "version" int4 DEFAULT 1
)
;
COMMENT ON COLUMN t_cbb_sys_upgrade.upgrade_package_id IS '系统刷机包id';
COMMENT ON COLUMN t_cbb_sys_upgrade.package_version IS '刷机包版本号';
COMMENT ON COLUMN t_cbb_sys_upgrade.package_name IS '刷机包镜像名称';
COMMENT ON COLUMN t_cbb_sys_upgrade.create_time IS '生成时间';
COMMENT ON COLUMN t_cbb_sys_upgrade.state IS '任务状态';
COMMENT ON COLUMN t_cbb_sys_upgrade.version IS '版本号，实现乐观锁';

ALTER TABLE t_cbb_sys_upgrade ADD CONSTRAINT "t_cbb_sys_upgrade_pkey" PRIMARY KEY ("id");

CREATE TABLE t_cbb_sys_upgrade_terminal (
  "id" uuid NOT NULL,
  "sys_upgrade_id" uuid NOT NULL,
  "terminal_id" varchar(64)  NOT NULL,
  "start_time" timestamp(0),
  "state" varchar(32) ,
  "create_time" timestamp(0),
  "version" int4 DEFAULT 1
)
;
COMMENT ON COLUMN t_cbb_sys_upgrade_terminal.sys_upgrade_id IS '刷机任务id';
COMMENT ON COLUMN t_cbb_sys_upgrade_terminal.terminal_id IS '刷机终端id';
COMMENT ON COLUMN t_cbb_sys_upgrade_terminal.start_time IS '开始刷机时间';
COMMENT ON COLUMN t_cbb_sys_upgrade_terminal.state IS '刷机状态';
COMMENT ON COLUMN t_cbb_sys_upgrade_terminal.create_time IS '创建时间';
COMMENT ON COLUMN t_cbb_sys_upgrade_terminal.version IS '版本号，实现乐观锁';

ALTER TABLE t_cbb_sys_upgrade_terminal ADD CONSTRAINT "t_cbb_sys_upgrade_terminal_pkey" PRIMARY KEY ("id");




