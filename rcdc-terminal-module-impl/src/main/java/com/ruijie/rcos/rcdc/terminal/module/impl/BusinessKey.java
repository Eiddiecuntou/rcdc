package com.ruijie.rcos.rcdc.terminal.module.impl;

/**
 * Description: 国际化key全局变量接口，与国际化文件中的key对应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public interface BusinessKey {

    /**
     * 没有找到对应的终端
     */
    String RCDC_TERMINAL_NOT_FOUND_TERMINAL = "rcdc_terminal_not_found_terminal";

    /**
     * 终端断开连接，处于离线状态
     */
    String RCDC_TERMINAL_OFFLINE = "rcdc_terminal_offline";

    /**
     * 正在收集终端日志中
     */
    String RCDC_TERMINAL_COLLECT_LOG_DOING = "rcdc_terminal_collect_log_doing";

    /**
     * 不存在日志文件
     */
    String RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST = "rcdc_terminal_collect_log_not_exist";
    
    /**
     * 终端管理员密码记录不存在
     */
    String RCDC_TERMINAL_ADMIN_PWD_RECORD_NOT_EXIST = "rcdc_terminal_admin_pwd_record_not_exist";

    /**
     * 上传系统升级文件类型错误
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR = "rcdc_terminal_system_upgrade_upload_file_type_error";

    /**
     * 上传系统升级文件失败
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL = "rcdc_terminal_system_upgrade_upload_file_fail";

    /**
     * 上传系统升级文件完整性校验失败
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_COMPLETE_CHECK_FAIL =
            "rcdc_terminal_system_upgrade_upload_file_complete_check_fail";

    /**
     * 系统指令执行失败
     */
    String RCDC_SYSTEM_CMD_EXECUTE_FAIL = "rcdc_system_cmd_execute_fail";

    /**
     * 上传系统升级文件不正确
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_INCORRECT = "rcdc_terminal_system_upgrade_upload_file_incorrect";

    /**
     * 系统升级包不存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST = "rcdc_terminal_system_upgrade_package_not_exist";


    /**
     * 系统升级包已存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_HAS_EXIST = "rcdc_terminal_system_upgrade_package_has_exist";

    /**
     * 系统升级包路径不存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_PATH_NOT_EXIST = "rcdc_terminal_system_upgrade_package_path_not_exist";

    /**
     * 系统升级包版本文件不正确
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT =
            "rcdc_terminal_system_upgrade_package_version_file_incorrect";

    /**
     * 系统升级包路径不存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_FILE_OPERATE_FAIL =
            "rcdc_terminal_system_upgrade_package_file_operate_fail";

    /**
     * 系统升级任务不存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST = "rcdc_terminal_system_upgrade_task_not_exist";

    /**
     * 系统升级任务正在进行中
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING = "rcdc_terminal_system_upgrade_task_is_running";

    /**
     * 系统升级任务超出数量限制
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT = "rcdc_terminal_system_upgrade_num_exceed_limit";

    /**
     * 系统升级中任务超出数量限制
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADING_NUM_EXCEED_LIMIT = "rcdc_terminal_system_upgrading_num_exceed_limit";

    /**
     * 系统升级任务状态异常
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_STATE_INCORRECT = "rcdc_terminal_system_upgrade_task_state_incorrect";

    /**
     * 文件不存在
     */
    String RCDC_FILE_NOT_EXIST = "rcdc_file_not_exist";

    /**
     * 文件操作失败
     */
    String RCDC_FILE_OPERATE_FAIL = "rcdc_file_operate_fail";

    /**
     * 终端正在运行虚机
     */
    String RCDC_TERMINAL_IS_RUNNING_VIRTHAL_MACHINE = "rcdc_terminal_is_running_virthal_machine";
}
