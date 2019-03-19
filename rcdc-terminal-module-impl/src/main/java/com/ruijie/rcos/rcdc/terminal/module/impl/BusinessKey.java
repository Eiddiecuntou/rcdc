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

    /**终端在线状态不允许删除*/
    String RCDC_TERMINAL_ONLINE_CANNOT_DELETE = "rcdc_terminal_online_cannot_delete";

    /** 终端离线状态不能进行关闭操作 */
    String RCDC_TERMINAL_OFFLINE_CANNOT_SHUTDOWN = "rcdc_terminal_offline_cannot_shutdown";

    /** 终端离线状态不能进行重启操作 */
    String RCDC_TERMINAL_OFFLINE_CANNOT_RESTART = "rcdc_terminal_offline_cannot_restart";

    /** 终端离线状态不能进行收集日志操作 */
    String RCDC_TERMINAL_OFFLINE_CANNOT_COLLECT_LOG = "rcdc_terminal_offline_cannot_collect_log";

    /** 终端离线状态不能进行检测操作 */
    String RCDC_TERMINAL_OFFLINE_CANNOT_DETECT = "rcdc_terminal_offline_cannot_detect";

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
     * 升级包类型不支持
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_UNSUPPORT = "rcdc_terminal_system_upgrade_upload_file_package_type_unsupport";

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
     * 系统升级包正在上传中
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING = "rcdc_terminal_system_upgrade_package_is_uploading";

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
     * 系统刷机包路径不存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_FILE_OPERATE_FAIL =
            "rcdc_terminal_system_upgrade_package_file_operate_fail";
    
    /**
     * 存在进行中的升级任务，不允许删除
     */
    String RCDC_TERMINAL_UPGRADE_PACKAGE_HAS_RUNNING_TASK_NOT_ALLOW_DELETE = "rcdc_terminal_upgrade_package_has_running_task_not_allow_delete";

    /**
     * 刷机任务不存在
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST = "rcdc_terminal_system_upgrade_task_not_exist";
    
    /**
     * 刷机任务已关闭
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED = "rcdc_terminal_system_upgrade_task_has_closed";
    
    /**
     *刷机任务终端已添加
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_EXIST = "rcdc_terminal_system_upgrade_terminal_exist";
    
    /**
     *刷机任务终端已添加
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST = "rcdc_terminal_system_upgrade_terminal_not_exist";

    /**
     * 系统升级任务正在进行中
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING = "rcdc_terminal_system_upgrade_task_is_running";

    /**
     * 终端系统升级状态文件目录不存在
     */
    String RCDC_TERMINAL_UPGRADE_SUCCESS_STATUS_DIRECTORY_NOT_EXIST = "rcdc_terminal_upgrade_success_status_directory_not_exist";

    /**
     * 刷机终端状态不可取消
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_CANCEL = "rcdc_terminal_system_upgrade_terminal_state_not_allow_cancel";
    
    /**
     * 刷机终端状态不可重试
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_RETRY = "rcdc_terminal_system_upgrade_terminal_state_not_allow_retry";
    
    /**
     * 终端升级信息发送失败
     */
    String RCDC_TERMINAL_UPGRADE_MESSAGE_SEND_FAIL = "rcdc_terminal_upgrade_message_send_fail";
    
    /**
     * 文件不存在
     */
    String RCDC_FILE_NOT_EXIST = "rcdc_file_not_exist";

    /**
     * 文件操作失败
     */
    String RCDC_FILE_OPERATE_FAIL = "rcdc_file_operate_fail";

    /**
     * 收集终端日志记录不存在
     */
    String RCDC_TERMINAL_COLLECT_LOG_CACHE_NOT_EXIST = "rcdc_terminal_collect_log_cache_not_exist";
    
    /**
     * 终端检测超时处理定时任务
     */
    String RCDC_TERMINAL_QUARTZ_DETECT_TIME_OUT = "rcdc_terminal_quartz_detect_time_out";
    
    /**
     * 终端检测状态-检测中
     */
    String RCDC_TERMINAL_DETECT_STATE_CHECKING = "rcdc_terminal_detect_state_checking";
    
    /**
     * 终端检测状态-检测成功
     */
    String RCDC_TERMINAL_DETECT_STATE_SUCCESS = "rcdc_terminal_detect_state_success";
    
    /**
     * 终端检测状态-检测失败
     */
    String RCDC_TERMINAL_DETECT_STATE_ERROR = "rcdc_terminal_detect_state_error";

    String RCDC_TERMINAL_SUCCESS_CONNECT_LOG = "rcdc_terminal_success_connect_log";

    String RCDC_TERMINAL_CONNECT_CLOSE_LOG = "rcdc_terminal_connect_close_log";


}
