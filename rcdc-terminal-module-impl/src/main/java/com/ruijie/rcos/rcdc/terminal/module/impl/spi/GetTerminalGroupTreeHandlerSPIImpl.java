package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbGetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 获取终端组树型结构列表
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/9 14:07
 *
 * @author zhangyichi
 */
@DispatcherImplemetion(ShineAction.GET_TERMINAL_GROUP_LIST)
public class GetTerminalGroupTreeHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTerminalGroupTreeHandlerSPIImpl.class);

    @Autowired
    private CbbTerminalGroupMgmtAPI cbbTerminalGroupMgmtAPI;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request cannot be null!");
        Assert.hasText(request.getData(), "data in request cannot be null!");

        try {
            CbbGetTerminalGroupCompleteTreeRequest apiRequest =
                    JSONObject.parseObject(request.getData(), CbbGetTerminalGroupCompleteTreeRequest.class);
            Assert.notNull(apiRequest, "parse data in request fail! data:[" + request.getData() + "]");

            CbbGetTerminalGroupTreeResponse apiResponse = cbbTerminalGroupMgmtAPI.loadTerminalGroupCompleteTree(apiRequest);
            Assert.notNull(apiResponse, "apiResponse is null!");

            CbbResponseShineMessage shineMessage = MessageUtils.buildResponseMessage(request, apiResponse);
            messageHandlerAPI.response(shineMessage);
        } catch (Exception e) {
            LOGGER.error("获取终端组列表失败", e);
            CbbResponseShineMessage shineMessage = MessageUtils.buildErrorResponseMessage(request);
            messageHandlerAPI.response(shineMessage);
        }
    }
}
