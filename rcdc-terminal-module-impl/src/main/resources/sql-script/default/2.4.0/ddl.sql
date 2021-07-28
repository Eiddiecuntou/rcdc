

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
