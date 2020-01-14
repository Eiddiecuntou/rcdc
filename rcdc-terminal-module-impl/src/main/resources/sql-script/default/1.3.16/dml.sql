/** 初始化终端型号记录**/
INSERT INTO t_cbb_terminal_model_driver VALUES ('d838fe14-2b50-4325-8d4d-dfa9ca202383', 0, 'Rain200 V2', '80020071', 'Intel(R) Atom(TM) x5-Z8300 CPU @ 1.44GHz', 'VDI');
INSERT INTO t_cbb_terminal_model_driver VALUES ('4bfe59c9-aba8-47de-b58f-e612b3ef7158', 0, 'Rain100 V2', '80020061', 'Intel(R) Atom(TM) x5-Z8300 CPU @ 1.44GHz', 'VDI');
/** 将记录RCDC IP字段修改为记录集群虚拟IP **/
UPDATE t_sk_global_parameter
SET param_key = 'cluster_virtual_ip'
WHERE param_key = 'rcdc_server_ip';
