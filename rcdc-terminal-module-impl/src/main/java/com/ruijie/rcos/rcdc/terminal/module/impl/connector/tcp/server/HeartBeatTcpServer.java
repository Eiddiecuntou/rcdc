package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server;

import com.ruijie.rcos.sk.connectkit.api.annotation.ApiAction;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.Tcp;

/**
 * Description: 接收shine上报的虚拟化策略信息
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 14:17 2020/9/28
 *
 * @author yxd
 */
@Tcp
public interface HeartBeatTcpServer {

    /**
     * 获取IDV终端虚拟化策略信息
     *
     * @return Object obj
     */
    @ApiAction("heartBeat")
    Object heartBeat();
}
