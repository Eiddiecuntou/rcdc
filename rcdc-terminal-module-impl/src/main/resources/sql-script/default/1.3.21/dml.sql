INSERT INTO t_sk_global_parameter
VALUES ('cdfb2d7b-e5de-4d4a-9d6f-4571d68fd7ab', 'terminal_background', NULL, NULL, '2019-11-07', '2019-11-07', 0);
INSERT INTO t_sk_global_parameter
VALUES ('8b1c0f81-7272-47c0-9a7a-f81a16c8e798', 'offline_time', '15', '15', '2019-12-15', '2019-12-15', 0);

 /** 终端初始 ftp账号信息  */
INSERT INTO t_sk_global_parameter(id, param_key, param_value, default_value, create_time, update_time, version)
VALUES ('e6a1075a-3cbf-4e8d-bf76-72cdd9db3f70', 'terminal_ftp_config', '{"ftpPort": 2021,"ftpUserName": "shine","ftpUserPassword": "21Wq_Er","ftpPath": "/","fileDir": "/"}', NULL, now(), now(), 0);
