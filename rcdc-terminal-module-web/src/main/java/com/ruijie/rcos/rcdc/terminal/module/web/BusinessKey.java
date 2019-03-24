package com.ruijie.rcos.rcdc.terminal.module.web;

/**
 * Description: 国际化key
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/15
 *
 * @author Jarman
 */
public interface BusinessKey {

    /** 操作成功 */
    String RCDC_TERMINAL_MODULE_OPERATE_SUCCESS = "rcdc_terminal_module_operate_success";
    /** 操作失败 */
    String RCDC_TERMINAL_MODULE_OPERATE_FAIL = "rcdc_terminal_module_operate_fail";

    /**
     * 请求参数异常
     */
    String RCDC_COMMON_REQUEST_PARAM_ERROR = "rcdc_common_request_param_error";
    
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
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG =
            "rcdc_terminal_system_upgrade_package_upload_success_log";

    /**
     * 上传终端系统升级包失败日志
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG =
            "rcdc_terminal_system_upgrade_package_upload_fail_log";

    /**
     * 上传终端系统升级包文件名称长度超出限制
     */
    String RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FILE_NAME_LENGTH_EXCEED =
            "rcdc_terminal_system_upgrade_package_upload_file_name_length_exceed";


    /**
     * 创建终端刷机任务成功日志
     */
    String RCDC_CREATE_UPGRADE_TERMINAL_TASK_SUCCESS_LOG = "rcdc_create_upgrade_terminal_task_success_log";

    /**
     * 创建终端刷机任务失败日志
     */
    String RCDC_CREATE_UPGRADE_TERMINAL_TASK_FAIL_LOG = "rcdc_create_upgrade_terminal_task_fail_log";

    /**
     * 批量追加终端升级任务项名称
     */
    String RCDC_ADD_UPGRADE_TERMINAL_ITEM_NAME = "rcdc_add_upgrade_terminal_item_name";

    /**
     * 批量追加终端升级任务名称
     */
    String RCDC_ADD_UPGRADE_TERMINAL_TASK_NAME = "rcdc_add_upgrade_terminal_task_name";

    /**
     * 批量追加终端升级任务描述
     */
    String RCDC_ADD_UPGRADE_TERMINAL_TASK_DESC = "rcdc_add_upgrade_terminal_task_desc";

    /**
     * 批量追加终端系统升级任务成功日志
     */
    String RCDC_ADD_UPGRADE_TERMINAL_SUCCESS_LOG = "rcdc_add_upgrade_terminal_success_log";

    /**
     * 批量追加终端系统升级任务失败日志
     */
    String RCDC_ADD_UPGRADE_TERMINAL_FAIL_LOG = "rcdc_add_upgrade_terminal_fail_log";

    /**
     * 批量追加终端系统升级任务结果
     */
    String RCDC_ADD_UPGRADE_TERMINAL_RESULT = "rcdc_add_upgrade_terminal_result";

    /**
     * 批量追加终端系统升级任务成功
     */
    String RCDC_ADD_UPGRADE_TERMINAL_RESULT_SUCCESS = "rcdc_add_upgrade_terminal_result_success";
    
    /**
     * 批量追加终端系统升级任务项失败
     */
    String RCDC_ADD_UPGRADE_TERMINAL_RESULT_FAIL = "rcdc_add_upgrade_terminal_result_fail";
    
    
    
    /**
     * 批量取消终端升级任务项名称
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_ITEM_NAME = "rcdc_cancel_upgrade_terminal_item_name";

    /**
     * 批量取消终端升级任务名称
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_TASK_NAME = "rcdc_cancel_upgrade_terminal_task_name";

    /**
     * 批量取消终端升级任务描述
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_TASK_DESC = "rcdc_cancel_upgrade_terminal_task_desc";

    /**
     * 批量取消终端系统升级任务成功日志
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_SUCCESS_LOG = "rcdc_cancel_upgrade_terminal_success_log";

    /**
     * 批量取消终端系统升级任务失败日志
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_FAIL_LOG = "rcdc_cancel_upgrade_terminal_fail_log";

    /**
     * 批量取消终端系统升级任务结果
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_RESULT = "rcdc_cancel_upgrade_terminal_result";

    /**
     * 批量取消终端系统升级任务项成功
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_RESULT_SUCCESS = "rcdc_cancel_upgrade_terminal_result_success";
    
    /**
     * 批量取消终端系统升级任务项失败
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_RESULT_FAIL = "rcdc_cancel_upgrade_terminal_result_fail";
    
    /**
     * 取消终端系统升级成功（单条）
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_SUCCESS = "rcdc_cancel_upgrade_terminal_success";
    
    /**
     * 取消终端系统升级失败（单条）
     */
    String RCDC_CANCEL_UPGRADE_TERMINAL_FAIL = "rcdc_cancel_upgrade_terminal_fail";
    
    /**
     * 批量重试终端升级任务项名称
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_ITEM_NAME = "rcdc_retry_upgrade_terminal_item_name";

    /**
     * 批量重试终端升级任务名称
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_TASK_NAME = "rcdc_retry_upgrade_terminal_task_name";

    /**
     * 批量重试终端升级任务描述
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_TASK_DESC = "rcdc_retry_upgrade_terminal_task_desc";

    /**
     * 批量重试终端系统升级任务成功日志
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_SUCCESS_LOG = "rcdc_retry_upgrade_terminal_success_log";

    /**
     * 批量重试终端系统升级任务失败日志
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_FAIL_LOG = "rcdc_retry_upgrade_terminal_fail_log";

    /**
     * 批量重试终端系统升级任务结果
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_RESULT = "rcdc_retry_upgrade_terminal_result";

    /**
     * 批量重试终端系统升级任务项成功
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_RESULT_SUCCESS = "rcdc_retry_upgrade_terminal_result_success";
    
    /**
     * 批量重试终端系统升级任务项失败
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_RESULT_FAIL = "rcdc_retry_upgrade_terminal_result_fail";
    
    
    /**
     * 重试终端系统升级成功（单条）
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_SUCCESS = "rcdc_retry_upgrade_terminal_success";
    
    /**
     * 重试终端系统升级失败（单条）
     */
    String RCDC_RETRY_UPGRADE_TERMINAL_FAIL = "rcdc_retry_upgrade_terminal_fail";
    
    /** 关闭终端升级包[{0}]升级任务成功 */
    String RCDC_UPGRADE_TERMINAL_TASK_CLOSE_SUCCESS = "rcdc_upgrade_terminal_task_close_success";

    /** 关闭终端升级包[{0}]升级任务失败，失败原因：{1} */
    String RCDC_UPGRADE_TERMINAL_TASK_CLOSE_FAIL = "rcdc_upgrade_terminal_task_close_fail";
    
    
    /**
     * 批量删除终端升级包任务项名称
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_ITEM_NAME = "rcdc_delete_terminal_upgrade_package_item_name";

    /**
     * 批量删除终端升级包任务名称
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_TASK_NAME = "rcdc_delete_terminal_upgrade_package_task_name";

    /**
     * 批量删除终端升级包任务描述
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_TASK_DESC = "rcdc_delete_terminal_upgrade_package_task_desc";

    /**
     * 批量删除终端系统升级包任务成功日志
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_SUCCESS_LOG = "rcdc_delete_terminal_upgrade_package_success_log";

    /**
     * 批量删除终端系统升级包任务失败日志
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_FAIL_LOG = "rcdc_delete_terminal_upgrade_package_fail_log";

    /**
     * 批量删除终端系统升级包任务结果
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_RESULT = "rcdc_delete_terminal_upgrade_package_result";

    /**
     * 批量删除终端系统升级包任务项成功
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_RESULT_SUCCESS = "rcdc_delete_terminal_upgrade_package_result_success";
    
    /**
     * 批量删除终端系统升级包任务项失败
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_RESULT_FAIL = "rcdc_delete_terminal_upgrade_package_result_fail";
    
    /**
     * 删除单条终端系统升级包成功
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_SUCCESS = "rcdc_delete_terminal_upgrade_package_success";
    
    /**
     * 删除单条终端系统升级包失败
     */
    String RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_FAIL = "rcdc_delete_terminal_upgrade_package_fail";
    

    /**
     * 终端检测名称
     */
    String RCDC_TERMINAL_DETECT_ITEM_NAME = "rcdc_terminal_detect_item_name";

    /**
     * 终端检测批量任务名
     */
    String RCDC_TERMINAL_DETECT_BATCH_TASK_NAME = "rcdc_terminal_detect_batch_task_name";

    /**
     * 终端检测批量任务描述
     */
    String RCDC_TERMINAL_DETECT_BATCH_TASK_DESC = "rcdc_terminal_detect_batch_task_desc";

    /**
     * 终端检测批量任务结果
     */
    String RCDC_TERMINAL_DETECT_BATCH_TASK_RESULT = "rcdc_terminal_detect_batch_task_result";

    /**
     * 开启终端检测成功日志
     */
    String RCDC_TERMINAL_START_DETECT_SUCCESS_LOG = "rcdc_terminal_start_detect_success_log";

    /**
     * 发送检测命令成功
     */
    String RCDC_TERMINAL_DETECT_SEND_SUCCESS = "rcdc_terminal_detect_send_success";

    /**
     * 发送检测命令失败
     */
    String RCDC_TERMINAL_DETECT_SEND_FAIL = "rcdc_terminal_detect_send_fail";

    /**
     * 开启终端检测失败日志
     */
    String RCDC_TERMINAL_START_DETECT_FAIL_LOG = "rcdc_terminal_start_detect_fail_log";

    /**
     * 终端列表日期参数错误
     */
    String RCDC_TERMINAL_DETECT_LIST_DATE_ERROR = "rcdc_terminal_detect_list_date_error";

    /** 修改终端管理密码成功日志 */
    String RCDC_TERMINAL_CHANGE_PWD_SUCCESS_LOG = "rcdc_terminal_change_pwd_success_log";
    /** 修改终端管理密码失败日志 */
    String RCDC_TERMINAL_CHANGE_PWD_FAIL_LOG = "rcdc_terminal_change_pwd_fail_log";


    /** 关闭终端失败日志 */
    String RCDC_TERMINAL_CLOSE_FAIL_LOG = "rcdc_terminal_close_fail_log";

    /** 批量关闭终端结果 */
    String RCDC_TERMINAL_CLOSE_RESULT = "rcdc_terminal_close_result";

    /** 关闭终端成功日志 */
    String RCDC_TERMINAL_CLOSE_SUCCESS_LOG = "rcdc_terminal_close_success_log";

    /** 批量关闭终端任务名称 */
    String RCDC_TERMINAL_CLOSE_TASK_NAME = "rcdc_terminal_close_task_name";

    /** 批量关闭终端任务描述 */
    String RCDC_TERMINAL_CLOSE_TASK_DESC = "rcdc_terminal_close_task_desc";

    /** 发送关闭终端命令 */
    String RCDC_TERMINAL_CLOSE_ITEM_NAME = "rcdc_terminal_close_item_name";

    /** 关闭命令发送成功 */
    String RCDC_TERMINAL_CLOSE_SEND_SUCCESS = "rcdc_terminal_close_send_success";

    /** 关闭命令发送失败 */
    String RCDC_TERMINAL_CLOSE_SEND_FAIL = "rcdc_terminal_close_send_fail";

    /** 批量重启终端任务结果 */
    String RCDC_TERMINAL_RESTART_RESULT = "rcdc_terminal_restart_result";

    /** 重启终端失败日志 */
    String RCDC_TERMINAL_RESTART_FAIL_LOG = "rcdc_terminal_restart_fail_log";

    /** 重启终端成功日志 */
    String RCDC_TERMINAL_RESTART_SUCCESS_LOG = "rcdc_terminal_restart_success_log";

    /** 批量重启终端任务名称 */
    String RCDC_TERMINAL_RESTART_TASK_NAME = "rcdc_terminal_restart_task_name";

    /** 批量重启终端任务描述 */
    String RCDC_TERMINAL_RESTART_TASK_DESC = "rcdc_terminal_restart_task_desc";

    /** 发送重启命令给终端 */
    String RCDC_TERMINAL_RESTART_ITEM_NAME = "rcdc_terminal_restart_item_name";

    /** 重启命令发送成功 */
    String RCDC_TERMINAL_RESTART_SEND_SUCCESS = "rcdc_terminal_restart_send_success";

    /** 重启命令发送失败 */
    String RCDC_TERMINAL_RESTART_SEND_FAIL = "rcdc_terminal_restart_send_fail";
}
