package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalLogo;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Description:同步終端logo
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/22
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.SYNC_TERMINAL_LOGO)
public class SyncTerminalLogoHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    public static final Logger LOGGER = LoggerFactory.getLogger(SyncTerminalLogoHandlerSPIImpl.class);

    @Autowired
    private TerminalLogoService terminalLogoService;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");

        LOGGER.debug("=====终端同步Logo报文===={}", request.getData());
        try {
            String logoName = terminalLogoService.getTerminalLogoName();
            TerminalLogo terminalLogo = new TerminalLogo();
            terminalLogo.setLogoName(logoName);
            CbbResponseShineMessage<TerminalLogo> responseMessage = MessageUtils.buildResponseMessage(request, terminalLogo);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端同步Logo消息应答失败", e);
        }

    }
}
