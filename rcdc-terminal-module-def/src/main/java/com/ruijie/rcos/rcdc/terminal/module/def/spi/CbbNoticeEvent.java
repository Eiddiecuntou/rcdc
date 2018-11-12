package com.ruijie.rcos.rcdc.terminal.module.def.spi;

/**
 * Description: 终端通知事件定义
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public interface CbbNoticeEvent {
    /**
     * 连接成功，在线状态
     */
    String ONLINE = "online";

    /**
     * 连接关闭，离线状态
     */
    String OFFLINE = "offline";
}
