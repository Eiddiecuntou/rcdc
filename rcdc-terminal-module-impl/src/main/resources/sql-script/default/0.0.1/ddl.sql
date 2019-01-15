/**终端表*/
create table t_cbb_terminal
(
  id                   uuid        not null
    constraint t_rcdc_terminal_basic_info_pkey
    primary key,
  terminal_name        varchar(32),
  terminal_id          varchar(32) not null,
  mac_addr             varchar(64),
  ip                   varchar(64),
  subnet_mask          varchar(64),
  gateway              varchar(64),
  main_dns             varchar(64),
  second_dns           varchar(64),
  get_ip_mode          varchar(32),
  get_dns_mode         varchar(32),
  product_type         varchar(32),
  terminal_type        varchar(32),
  serial_number        varchar(64),
  cpu_type             varchar(64),
  memory_size          integer,
  disk_size            integer,
  terminal_os_type     varchar(32),
  terminal_os_version  varchar(32),
  rain_os_version      varchar(32),
  rain_upgrade_version varchar(32),
  hardware_version     varchar(32),
  network_access_mode  varchar(32),
  create_time          timestamp,
  last_online_time     timestamp,
  last_offline_time    timestamp,
  version              integer default 1,
  state                varchar(64) not null,
  platform             varchar(64)
);

comment on table t_cbb_terminal
is '终端基本信息表';

comment on column t_cbb_terminal.terminal_name
is '终端名称';

comment on column t_cbb_terminal.terminal_id
is '终端唯一标识,由终端主动上报，约定为第一张网卡的mac地址';

comment on column t_cbb_terminal.mac_addr
is '终端mac地址，多个以逗号分隔';

comment on column t_cbb_terminal.ip
is '终端ip地址';

comment on column t_cbb_terminal.subnet_mask
is '子网掩码';

comment on column t_cbb_terminal.gateway
is '网关';

comment on column t_cbb_terminal.main_dns
is '首先DNS';

comment on column t_cbb_terminal.second_dns
is '备选dns';

comment on column t_cbb_terminal.get_ip_mode
is '获取ip方式，0自动获取，1手动填入';

comment on column t_cbb_terminal.get_dns_mode
is '获取dns方式，0自动获取，1手动填写';

comment on column t_cbb_terminal.product_type
is '产品型号:Rain100S,Rain200V2...';

comment on column t_cbb_terminal.terminal_type
is '终端类型：idv，vdi';

comment on column t_cbb_terminal.serial_number
is '产品序列号';

comment on column t_cbb_terminal.cpu_type
is 'cup型号';

comment on column t_cbb_terminal.memory_size
is '内存大小，单位M';

comment on column t_cbb_terminal.disk_size
is '磁盘大小，单位M';

comment on column t_cbb_terminal.terminal_os_type
is '终端操作系统类型，Android、Linux。。。';

comment on column t_cbb_terminal.terminal_os_version
is '终端操作系统版本';

comment on column t_cbb_terminal.rain_os_version
is '终端系统版本号';

comment on column t_cbb_terminal.rain_upgrade_version
is '软件版本号，指组件升级包的版本号';

comment on column t_cbb_terminal.hardware_version
is '硬件版本号';

comment on column t_cbb_terminal.network_access_mode
is '网络接入方式:无线接入，有线接入';

comment on column t_cbb_terminal.create_time
is '数据创建时间';

comment on column t_cbb_terminal.last_online_time
is '终端最近接入的时间点';

comment on column t_cbb_terminal.last_offline_time
is '终端最近一次离线的时间点';

comment on column t_cbb_terminal.version
is '版本号，实现乐观锁';

comment on column t_cbb_terminal.state
is '终端状态：离线，在线 升级中';

comment on column t_cbb_terminal.platform
is '平台';

alter table t_cbb_terminal
  owner to postgres;

create unique index t_rcdc_terminal_basic_info_terminal_id_uindex
  on t_cbb_terminal (terminal_id);

/**终端检测表*/
create table t_cbb_terminal_detection
(
  id               uuid        not null
    constraint t_terminal_detection_pkey
    primary key,
  ip_conflict      smallint,
  ip_conflict_mac  varchar(32),
  access_internet  smallint,
  bandwidth        varchar(32),
  packet_loss_rate varchar(32),
  network_delay    integer,
  detect_time      timestamp,
  terminal_id      varchar(32) not null,
  detect_state     varchar(64) not null,
  detect_fail_msg  varchar(128),
  version          integer default 1
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

alter table t_cbb_terminal_detection
  owner to postgres;

/** 终端系统升级包*/
CREATE TABLE t_cbb_sys_upgrade_package (
  "id" uuid NOT NULL,
  "img_name" varchar(64) COLLATE "pg_catalog"."default",
  "package_type" varchar(32) COLLATE "pg_catalog"."default",
  "upload_time" timestamp(6),
  "package_version" varchar(64) COLLATE "pg_catalog"."default",
  "version" int4 NOT NULL DEFAULT 0
);

COMMENT ON COLUMN t_cbb_sys_upgrade_package.img_name IS '升级包名称';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.package_type IS '升级包类型，包括android升级包、Linux vdi升级包、Linux IDV升级包';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.upload_time IS '上传时间';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.package_version IS '升级包版本号';
COMMENT ON COLUMN t_cbb_sys_upgrade_package.version IS '版本号';
COMMENT ON TABLE t_cbb_sys_upgrade_package IS '终端系统升级包表';

ALTER TABLE "public"."t_cbb_sys_upgrade_package" ADD CONSTRAINT "t_termianl_system_upgrade_package_pkey" PRIMARY KEY ("id");
