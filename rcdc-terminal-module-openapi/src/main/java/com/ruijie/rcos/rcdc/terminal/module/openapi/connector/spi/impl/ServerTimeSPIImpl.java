package com.ruijie.rcos.rcdc.terminal.module.openapi.connector.spi.impl;

import com.ruijie.rcos.rcdc.terminal.module.openapi.connector.spi.ServerTimeSPI;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/24
 *
 * @author hs
 */
public class ServerTimeSPIImpl implements ServerTimeSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerTimeSPIImpl.class);

    @Override
    public Long syncServerTime() {
        LOGGER.info("receive sync server time message");

        long currentTime = System.currentTimeMillis();
        LOGGER.info("currentTime is:{}", currentTime);
        return currentTime;
    }
}
