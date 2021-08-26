package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server.HeartBeatTcpServer;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Description: ShineRequestPartTypeSPIImpl
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/26
 *
 * @author chenyunjin
 */
public class HeartBeatTcpServerImpl implements HeartBeatTcpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatTcpServerImpl.class);

    @Override
    public Object heartBeat() {

        LOGGER.debug("收到业务心跳报文");
        return new Object();
    }
}
