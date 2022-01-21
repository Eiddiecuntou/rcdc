/** cva-idv终端授权数 */
INSERT INTO t_sk_global_parameter(id, param_key, param_value, default_value, create_time, update_time, version)
VALUES ('5696a1dd-d6f9-4636-949e-c0ede1434d3a', 'cva_terminal_license_num', '0', '0', now(), now(), 0);

--更新终端表中已授权的IDV终端授权证书类型
UPDATE t_cbb_terminal set license_type = 'IDV' where platform = 'IDV' and authed is TRUE;

UPDATE t_rco_user_desk_strategy_recommend set cpu = 0, memory = 0 where id = 'bedb7ed7-7754-4ce4-8baf-585a23dcff79';

UPDATE t_rco_user_desk_strategy_recommend set cpu = 0, memory = 0 where id = '76fa1d15-71f2-4915-98e3-5a68990087da';