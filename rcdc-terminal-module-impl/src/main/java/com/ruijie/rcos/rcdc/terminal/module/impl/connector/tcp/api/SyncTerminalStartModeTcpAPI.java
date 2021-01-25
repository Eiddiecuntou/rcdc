package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.api;

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
public interface SyncTerminalStartModeTcpAPI {

    /**
     * 下发终端启动方式给shine
     *
     * @param terminalId 终端id
     * @param startMode  启动方式
     * @throws BusinessException BusinessException
     */
    @ApiAction("set_terminal_start_mode")
    void handle(@SessionAlias String terminalId, String startMode) throws BusinessException;

}
