package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalPassword;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * 
 * Description: 终端同步管理员密码SPI
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月21日
 * 
 * @author nt
 */
@DispatcherImplemetion(ShineAction.SYNC_TERMINAL_PASSWORD)
public class SyncTerminalPasswordHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTerminalPasswordHandlerSPIImpl.class);

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalOperatorService terminalOperatorService;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");

        LOGGER.debug("=====终端同步管理员密码报文===={}", request.getData());
        try {
            String password = terminalOperatorService.getTerminalPassword();
            TerminalPassword terminalPassword = new TerminalPassword();
            terminalPassword.setPassword(password);
            CbbResponseShineMessage<TerminalPassword> responseMessage = MessageUtils.buildResponseMessage(request, terminalPassword);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端同步管理员密码消息应答失败", e);
        }

    }

}
