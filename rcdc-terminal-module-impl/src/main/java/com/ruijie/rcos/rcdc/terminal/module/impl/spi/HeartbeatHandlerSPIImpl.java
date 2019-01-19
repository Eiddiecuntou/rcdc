package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineResponseCode;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 心跳处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/17
 *
 * @author Jarman
 */
@DispatcherImplemetion(ShineAction.HEARTBEAT)
public class HeartbeatHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatHandlerSPIImpl.class);

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为null");
        LOGGER.debug("收到心跳报文，terminalId={}", request.getTerminalId());
        CbbResponseShineMessage shineMessage = new CbbResponseShineMessage();
        shineMessage.setAction(ShineAction.HEARTBEAT);
        shineMessage.setTerminalId(request.getTerminalId());
        shineMessage.setRequestId(request.getRequestId());
        shineMessage.setCode(ShineResponseCode.SUCCESS);
        messageHandlerAPI.response(shineMessage);
    }
}
