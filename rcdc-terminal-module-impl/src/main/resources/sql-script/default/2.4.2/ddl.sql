
CREATE TABLE if not EXISTS "public"."t_cbb_terminal_authorization_whitelist" (
  "id" uuid NOT NULL,
  "product_type" text COLLATE "pg_catalog"."default" NOT NULL,
  "priority" int4 COLLATE "pg_catalog"."default" 1,
  "create_time" TIMESTAMP(6),
  "version" int4,
  CONSTRAINT "t_cbb_terminal_authorization_whitelist" PRIMARY KEY ("id")
)
;

COMMENT ON COLUMN "public"."t_cbb_terminal_authorization_whitelist"."product_type" IS '设备型号或者授权码'

COMMENT ON COLUMN "public"."t_cbb_terminal_authorization_whitelist"."priority" IS '授权优先级，值越大越先匹配'

COMMENT ON COLUMN "public"."t_cbb_terminal_authorization_whitelist"."create_time" IS '创建时间'

-- 增加ocs_sn字段
alter table t_cbb_terminal add column if not exists ocs_sn type text;

COMMENT ON COLUMN "public"."t_cbb_terminal"."ocs_sn" IS 'OCS磁盘序列号'
