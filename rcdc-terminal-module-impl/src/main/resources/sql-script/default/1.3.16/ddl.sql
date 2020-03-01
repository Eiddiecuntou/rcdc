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

INSERT INTO t_sk_global_parameter
 VALUES ('c4a13dc8-3b26-444b-9419-214975d8182b', 'terminal_component_package_init_status_vdi_linux', 'fail', 'fail', now(), now(), 0);
INSERT INTO t_sk_global_parameter
 VALUES ('7c10207c-c7b3-4bf2-b2a3-ce99cf3f938d', 'terminal_component_package_init_status_vdi_android', 'fail', 'fail', now(), now(), 0);
INSERT INTO t_sk_global_parameter
 VALUES ('3a5b9432-c26e-4980-85ee-730f2f884d10', 'terminal_component_package_init_status_idv_linux', 'fail', 'fail', now(), now(), 0);