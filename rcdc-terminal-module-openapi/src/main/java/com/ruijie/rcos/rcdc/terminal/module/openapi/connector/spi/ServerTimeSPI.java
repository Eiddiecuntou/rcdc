package com.ruijie.rcos.rcdc.terminal.module.openapi.connector.spi;

import com.ruijie.rcos.sk.connectkit.api.annotation.ApiAction;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.Tcp;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/24
 *
 * @author hs
 */
@Tcp
public interface ServerTimeSPI {

    /**
     * 同步系统时间
     * @return 时间
     */
    @ApiAction("syncServerTime")
    Long syncServerTime();
}
