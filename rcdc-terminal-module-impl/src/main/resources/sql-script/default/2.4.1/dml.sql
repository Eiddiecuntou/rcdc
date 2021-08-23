
UPDATE t_sk_global_parameter SET param_value = '[]', default_value = '[]' WHERE param_key like '%terminal_license_num';

-- TODO 更新旧的升级包的升级支持cpu类型