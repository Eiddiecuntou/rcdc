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
     * 检查OTA升级
     */
    String CHECK_UPGRADE_OTA = "check_upgrade_ota";

    /**
     * OTA升级结果
     */
    String OTA_UPGRADE_RESULT = "ota_upgrade_result";

    /**
     * 同步是否需要OTA升级
     */
    String OTA_UPGRADE_JUDGE = "ota_upgrade_judge";
}
