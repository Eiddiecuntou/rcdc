package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;



/**
 * Description: 接收终端检测应答消息处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@DispatcherImplemetion(ShineAction.TERMINAL_DETECT)
public class TerminalDetectResponseHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private TerminalDetectService detectService;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        Assert.hasText(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");

        String data = request.getData();
        TerminalDetectResult result = JSON.parseObject(data, TerminalDetectResult.class);

        detectService.updateTerminalDetect(request.getTerminalId(), result);
    }
}
