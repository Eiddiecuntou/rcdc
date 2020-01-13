/** 创建终端型号表 */
CREATE TABLE t_cbb_terminal_model_driver (
  "id" uuid NOT NULL,
  "version" int4 NOT NULL,
  "product_model" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "product_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "cpu_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "platform" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  CONSTRAINT "t_cbb_terminal_model_driver_pkey" PRIMARY KEY ("id")
);

CREATE INDEX t_cbb_terminal_model_driver_product_id_index ON t_cbb_terminal_model_driver (product_id);

/** 添加终端型号id字段 */
ALTER TABLE t_cbb_terminal ADD COLUMN product_id varchar(32);



/** 创建终端系统升级任务终端分组表*/
CREATE TABLE t_cbb_sys_upgrade_terminal_group (
  "id" uuid NOT NULL,
  "sys_upgrade_id" uuid NOT NULL,
  "terminal_group_id" uuid NOT NULL,
  "create_time" timestamp(0),
  "version" int4 DEFAULT 1,
  CONSTRAINT "t_cbb_sys_upgrade_terminal_group_pkey" PRIMARY KEY ("id")
);

COMMENT ON COLUMN t_cbb_sys_upgrade_terminal_group.sys_upgrade_id IS '刷机任务id';

COMMENT ON COLUMN t_cbb_sys_upgrade_terminal_group.terminal_group_id IS '终端分组id';

COMMENT ON COLUMN t_cbb_sys_upgrade_terminal_group.create_time IS '创建时间';

COMMENT ON COLUMN t_cbb_sys_upgrade_terminal_group.version IS '版本号，实现乐观锁';

