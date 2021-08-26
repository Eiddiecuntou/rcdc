

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
alter table t_cbb_terminal add column upgrade_cpu_type text;

COMMENT ON COLUMN t_cbb_terminal.upgrade_cpu_type IS '终端升级cpu类型';

alter table t_cbb_terminal add column cpu_arch varchar(32);

COMMENT ON COLUMN t_cbb_terminal.cpu_arch IS '终端cpu架构类型';

ALTER TABLE "public"."t_cbb_sys_upgrade_package"
  ADD COLUMN "cpu_arch" varchar(32),
  ADD COLUMN "support_cpu" text;

COMMENT ON COLUMN "public"."t_cbb_sys_upgrade_package"."cpu_arch" IS 'cpu架构类型（x86_64、arm）';

COMMENT ON COLUMN "public"."t_cbb_sys_upgrade_package"."support_cpu" IS '支持的cpu';

ALTER TABLE "public"."t_cbb_sys_upgrade"
  ADD COLUMN "cpu_arch" varchar(32);

COMMENT ON COLUMN "public"."t_cbb_sys_upgrade"."cpu_arch" IS 'cpu架构';