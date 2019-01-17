package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalPassword;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

@DispatcherImplemetion(ReceiveTerminalEvent.SYNC_TERMINAL_PASSWORD)
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
