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

    /**
     * 开启终端检测成功日志
     */
    String RCDC_TERMINAL_START_DETECT_SUCCESS_LOG = "rcdc_terminal_start_detect_success_log";

    /**
     * 开启终端检测失败日志
     */

    String RCDC_TERMINAL_START_DETECT_FAIL_LOG = "rcdc_terminal_start_detect_fail_log";

    /**
     * 修改终端管理密码成功
     */
    String RCDC_TERMINAL_CHANGE_PWD_SUCCESS_LOG = "rcdc_terminal_change_pwd_success_log";

    /**
     * 修改终端管理密码成功
     */
    String RCDC_TERMINAL_CLOSE_FAIL_LOG = "rcdc_terminal_close_fail_log";

    /**
     * 批量修改终端管理员密码
     */
    String RCDC_TERMINAL_CLOSE_RESULT = "rcdc_terminal_close_result";

    /**关闭终端成功日志*/
    String RCDC_TERMINAL_CLOSE_SUCCESS_LOG = "rcdc_terminal_close_success_log";

    /**关闭终端成功结果*/
    String RCDC_TERMINAL_CLOSE_RESULT_SUCCESS = "rcdc_terminal_close_result_success";

    /**批量关闭终端任务名称*/
    String RCDC_TERMINAL_ClOSE_TASK_NAME = "rcdc_terminal_close_task_name";

    /**批量关闭终端任务描述*/
    String RCDC_TERMINAL_ClOSE_TASK_DESC = "rcdc_terminal_close_task_desc";
}
