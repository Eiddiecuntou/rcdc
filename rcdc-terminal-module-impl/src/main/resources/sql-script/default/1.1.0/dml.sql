
-- 修改终端管理员初始化密码为rcos_2019
UPDATE t_sk_global_parameter
SET param_value = '785uZuUromm4vYeXYo0fSOevwJ/72lkAScNViK5VOXQ=',
default_value = '785uZuUromm4vYeXYo0fSOevwJ/72lkAScNViK5VOXQ='
WHERE
	param_key = 'terminal_pwd';