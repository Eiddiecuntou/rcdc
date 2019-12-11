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

