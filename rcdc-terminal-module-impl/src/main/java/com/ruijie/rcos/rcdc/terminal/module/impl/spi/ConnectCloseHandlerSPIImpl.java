package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * Description: 连接关闭事件处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
@DispatcherImplemetion(ShineAction.CONNECT_CLOSE)
public class ConnectCloseHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectCloseHandlerSPIImpl.class);

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        String terminalId = request.getTerminalId();
        LOGGER.debug("连接关闭事件处理，terminal={}",terminalId);
        basicInfoService.modifyTerminalStateToOffline(terminalId);
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(NoticeEventEnums.OFFLINE, terminalId);
        terminalEventNoticeSPI.notify(noticeRequest);
    }
}
