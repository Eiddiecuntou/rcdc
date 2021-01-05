package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server;

import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.connectkit.api.annotation.ApiAction;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.SessionAlias;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.Tcp;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/5
 *
 * @author jarman
 */
@Tcp
public interface SyncTerminalStartModeTcpServer {

    /**
     * 同步终端启动方式
     *
     * @param terminalId 终端id
     * @return 启动方式
     * @throws BusinessException BusinessException
     */
    @ApiAction("sync_terminal_start_mode")
    String handle(@SessionAlias String terminalId) throws BusinessException;

}
