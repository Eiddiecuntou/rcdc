package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description: 与shine的action定义
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/19
 *
 * @author Jarman
 */
public interface ShineAction {

    /**
     * 检查升级
     */
    String CHECK_UPGRADE = "check_upgrade";

    /**
     * 通知日志已上传完成
     */
    String COLLECT_TERMINAL_LOG_FINISH = "collect_terminal_log_finish";

    /**
     * 上传终端检测消息
     */
    String TERMINAL_DETECT = "detect";

    /**
     * 上传USB 信息
     */
    String USB_INFO = "usb_info";

    /**
     * 心跳报文
     */
    String HEARTBEAT = "heartBeat";

    /**
     * 同步终端管理员密码
     */
    String SYNC_TERMINAL_PASSWORD = "sync_terminal_password";

    /**
     * 获取国际化语言
     */
    String GET_I18N_LANG = "get_i18n_lang";

    /**
     * 连接关闭
     */
    String CONNECT_CLOSE = "connect_close";

    /**
     * 连接关闭
     */
    String SYSTEM_UPGRADE = "upgrade_system";

    /**
     * 同步服务器时间
     */
    String SYNC_SERVER_TIME = "sync_server_time";

    /**
     * 请求软件版本信息
     */
    String REQUEST_SOFTWARE_VERSION = "request_software_version";

    /**
     * 同步LOGO
     */
    String SYNC_TERMINAL_LOGO = "sync_terminal_logo";


    /**
     * 检查终端系统升级
     */
    String CHECK_SYSTEM_UPGRADE = "check_system_upgrade";


    /**
     *  终端上报升级状态
     */
    String REPORT_SYSTEM_UPGRADE_RESULT = "report_system_upgrade_result";

    /**
     *  上报IDV终端确认清空数据盘响应
     */
    String CLEAR_DATA_DISK_CHOICE = "clear_data_disk_choice";

    /**
     *  上报IDV终端清空数据盘结果
     */
    String CLEAR_DATA_DISK_RESULT = "clear_data_disk_result";

    /**
     * 同步背景图
     */
    String SYNC_TERMINAL_BACKGROUND = "sync_terminal_background";

    /**
     *  终端请求同步离线登录设置
     */
    String REQUEST_OFFLINE_CONFIG = "request_offline_config";

    /**
     *  终端请求终端组列表
     */
    String GET_TERMINAL_GROUP_LIST = "get_terminal_ group_list";

    /**
     * 终端请求云主机id
     */
    String SYNC_RCOS_GLOBAL_PLATFORM_ID = "request_server_id";
}
