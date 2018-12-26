package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

/**
 * Description: 定义接收终端消息事件接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public interface ReceiveTerminalEvent {
    /**
     * 检查升级
     */
    String CHECK_UPGRADE = "check_upgrade";

    /**
     * 通知日志已上传完成
     */
    String NOTICE_UPLOAD_LOG_FINISH = "upload_log_finish";

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
}
