package com.ruijie.rcos.rcdc.terminal.module.web;

public interface BusinessKey {

    /**
     * 下载终端日志成功日志
     */
    String RCDC_TERMINAL_DOWNLOAD_TERMINAL_LOG_SUCCESS_LOG = "rcdc_terminal_download_terminal_log_success_log";
    
    /**
     * 下载终端日志失败日志
     */
    String RCDC_TERMINAL_DOWNLOAD_TERMINAL_LOG_FAIL_LOG = "rcdc_terminal_download_terminal_log_fail_log";

    /**
     * 上传终端系统升级包成功日志
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG = "rcdc_terminal_system_upgrade_package_upload_success_log";
    
    /**
     * 上传终端系统升级包失败日志
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG = "rcdc_terminal_system_upgrade_package_upload_fail_log";

    /**
     * 创建终端系统升级任务成功日志
     */
    String RCDC_TERMINAL_CREATE_SYSTEM_UPGRADE_TASK_SUCCESS_LOG = "rcdc_terminal_create_system_upgrade_task_success_log";
    
    /**
     * 创建终端系统升级任务失败日志
     */
    String RCDC_TERMINAL_CREATE_SYSTEM_UPGRADE_TASK_FAIL_LOG = "rcdc_terminal_create_system_upgrade_task_fail_log";

    /**
     * 移除终端升级任务失败日志
     */
    String RCDC_TERMINAL_DELETE_SYSTEM_UPGRADE_FAIL_LOG = "rcdc_terminal_delete_system_upgrade_fail_log";

    /**
     * 移除终端升级任务成功日志
     */
    String RCDC_TERMINAL_DELETE_SYSTEM_UPGRADE_SUCCESS_LOG = "rcdc_terminal_delete_system_upgrade_success_log";

}
