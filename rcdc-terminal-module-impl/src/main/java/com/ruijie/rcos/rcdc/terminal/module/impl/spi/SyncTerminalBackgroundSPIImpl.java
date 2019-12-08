package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalBackgroundInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: 同步终端背景图SPI
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author songxiang
 */
@DispatcherImplemetion(ShineAction.SYNC_TERMINAL_BACKGROUND)
public class SyncTerminalBackgroundSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTerminalLogoHandlerSPIImpl.class);

    private static final Integer NO_NEED_SYNC = 0;

    private static final Integer NEED_SYNC = 1;

    @Autowired
    GlobalParameterAPI globalParameterAPI;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "cbbDispatcherRequest must not be null");
        LOGGER.debug("=====终端同步背景界面报文===={}", request.getData());

        TerminalBackgroundInfo terminalSyncBackgroundResponse = buildBackgroundResponse();
        CbbResponseShineMessage cbbResponseShineMessage = MessageUtils.buildResponseMessage(request, terminalSyncBackgroundResponse);
        messageHandlerAPI.response(cbbResponseShineMessage);
    }

    private TerminalBackgroundInfo buildBackgroundResponse() {
        String parameter = globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
        if (StringUtils.isEmpty(parameter)) {
            LOGGER.info("终端同步背景界面:检测到数据库中的值为空，需要初始化终端背景图片");
            TerminalBackgroundInfo terminalBackgroundInfo = new TerminalBackgroundInfo();
            terminalBackgroundInfo.setIsDefaultImage(true);
            return terminalBackgroundInfo;
        }
        return JSON.parseObject(parameter, TerminalBackgroundInfo.class);
    }
}
