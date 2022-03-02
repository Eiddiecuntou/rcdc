--2.3.41
-- 修改all_disk_info字段长度
alter table t_cbb_terminal alter column all_disk_info type varchar(4096);

--2.3.42
alter table t_cbb_terminal add column if not EXISTS auth_mode varchar(64);

--2.4.1


CREATE TABLE if not EXISTS "public"."t_cbb_terminal_authorize" (
  "id" uuid NOT NULL,
  "terminal_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "license_type" text COLLATE "pg_catalog"."default" NOT NULL,
  "auth_mode" varchar(64) COLLATE "pg_catalog"."default",
  "authed" bool,
  "version" int4,
  CONSTRAINT "t_cbb_terminal_authorize_pkey" PRIMARY KEY ("id")
)
;


COMMENT ON COLUMN "public"."t_cbb_terminal_authorize"."terminal_id" IS '终端id';

COMMENT ON COLUMN "public"."t_cbb_terminal_authorize"."license_type" IS '证书类型';

COMMENT ON COLUMN "public"."t_cbb_terminal_authorize"."auth_mode" IS '授权模式';

COMMENT ON COLUMN "public"."t_cbb_terminal_authorize"."authed" IS '是否已授权';

-- 修改all_disk_info字段长度
alter table t_cbb_terminal alter column all_disk_info type text;
-- 修改all_disk_info字段长度
alter table t_cbb_terminal alter column product_type type text;

alter table t_cbb_terminal alter column hardware_version type text;

alter table t_cbb_terminal alter column serial_number type text;

alter table t_cbb_terminal alter column product_id type text;

alter table t_cbb_terminal alter column cpu_type type text;

alter table t_cbb_terminal_model_driver  alter column product_id type text;

alter table t_cbb_terminal_model_driver  alter column product_model type text;

alter table t_cbb_terminal_model_driver  alter column cpu_type type text;


-- 添加终端表升级支持的cpu类型、架构信息
alter table t_cbb_terminal add column IF NOT EXISTS upgrade_cpu_type text;

COMMENT ON COLUMN t_cbb_terminal.upgrade_cpu_type IS '终端升级cpu类型';

alter table t_cbb_terminal add column IF NOT EXISTS cpu_arch varchar(32);

COMMENT ON COLUMN t_cbb_terminal.cpu_arch IS '终端cpu架构类型';

ALTER TABLE "public"."t_cbb_sys_upgrade_package"
  ADD COLUMN IF NOT EXISTS "cpu_arch" varchar(32),
  ADD COLUMN IF NOT EXISTS "support_cpu" text;

COMMENT ON COLUMN "public"."t_cbb_sys_upgrade_package"."cpu_arch" IS 'cpu架构类型（x86_64、arm）';

COMMENT ON COLUMN "public"."t_cbb_sys_upgrade_package"."support_cpu" IS '支持的cpu';

ALTER TABLE "public"."t_cbb_sys_upgrade"
  ADD COLUMN IF NOT EXISTS "cpu_arch" varchar(32);

COMMENT ON COLUMN "public"."t_cbb_sys_upgrade"."cpu_arch" IS 'cpu架构';

--2.4.4

CREATE TABLE if not EXISTS "public"."t_cbb_terminal_authorization_whitelist" (
  "id" uuid NOT NULL,
  "product_type" text COLLATE "pg_catalog"."default" NOT NULL,
  "priority" int4 DEFAULT 1,
  "create_time" TIMESTAMP(6),
  "version" int4,
  CONSTRAINT "t_cbb_terminal_authorization_whitelist_pkey" PRIMARY KEY ("id")
)
;

COMMENT ON COLUMN "public"."t_cbb_terminal_authorization_whitelist"."product_type" IS '设备型号或者授权码';

COMMENT ON COLUMN "public"."t_cbb_terminal_authorization_whitelist"."priority" IS '授权优先级，值越大越先匹配';

COMMENT ON COLUMN "public"."t_cbb_terminal_authorization_whitelist"."create_time" IS '创建时间';

-- 增加ocs_sn字段
alter table t_cbb_terminal add column if not exists ocs_sn text;

COMMENT ON COLUMN "public"."t_cbb_terminal"."ocs_sn" IS 'OCS磁盘序列号';

--2.4.13

ALTER TABLE t_cbb_terminal_authorize ADD CONSTRAINT terminal_auth_id_unique UNIQUE ( terminal_id );

--2.4.21
-- 增加终端检测时，是否开启代理字段
ALTER TABLE t_cbb_terminal_detection ADD COLUMN IF NOT EXISTS enable_proxy BOOLEAN DEFAULT FALSE;
COMMENT ON COLUMN t_cbb_terminal_detection.enable_proxy IS '检测时，终端是否有开启代理';

-- 添加终端授权表是否为云应用授权字段
ALTER TABLE t_cbb_terminal_authorize add column IF NOT EXISTS cva_authed bool default false NOT NULL;
COMMENT ON COLUMN t_cbb_terminal_authorize.cva_authed IS '终端是否为云应用授权';