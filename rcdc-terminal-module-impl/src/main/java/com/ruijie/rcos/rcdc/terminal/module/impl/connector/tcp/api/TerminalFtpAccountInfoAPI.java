package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalFtpConfigInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.connectkit.api.annotation.ApiAction;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.SessionAlias;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.Tcp;

/**
 * Description: 终端ftp信息API
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/22
 *
 * @author hs
 */
@Tcp
public interface TerminalFtpAccountInfoAPI {

    /**
     * 同步ftp账号信息
     *
     * @param terminalId    terminalId
     * @param terminalFtpConfigInfo 账号信息
     * @throws BusinessException 业务异常
     */
    @ApiAction(ShineAction.SYNC_FTP_ACCOUNT_INFO)
    void syncFtpAccountInfo(@SessionAlias String terminalId, TerminalFtpConfigInfo terminalFtpConfigInfo) throws BusinessException;
}
