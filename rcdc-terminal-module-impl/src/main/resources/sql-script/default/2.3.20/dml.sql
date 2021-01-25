/** voi终端授权数 */
INSERT INTO t_sk_global_parameter(id, param_key, param_value, default_value, create_time, update_time, version)
VALUES ('a718338d-7b0e-4bbb-ada0-c8040e9d111b', 'voi_terminal_license_num', '-1', '-1', now(), now(),
 0);
 
 /** voi升级终端授权数 */
INSERT INTO t_sk_global_parameter(id, param_key, param_value, default_value, create_time, update_time, version)
VALUES ('a718338d-7b0e-4bbb-ada0-c8040e9d122b', 'voi_upgrade_terminal_license_num', '-1', '-1', now(), now(),
 0);