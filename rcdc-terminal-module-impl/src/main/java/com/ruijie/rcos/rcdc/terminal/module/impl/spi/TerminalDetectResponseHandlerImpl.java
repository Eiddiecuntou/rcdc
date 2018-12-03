package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: 接收终端检测应答消息处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@DispatcherImplemetion(ReceiveTerminalEvent.TERMINAL_DETECT)
public class TerminalDetectResponseHandlerImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private TerminalDetectService detectService;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        Assert.hasText(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");
        String data = (String) request.getData();
        TerminalDetectResponse result = JSON.parseObject(data, TerminalDetectResponse.class);

        detectService.updateBasicInfoAndDetect(request.getTerminalId(), result);
    }
}
