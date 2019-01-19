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
}
