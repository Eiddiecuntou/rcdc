package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.DispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.DispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: 终端检测消息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@DispatcherImplemetion(ReceiveTerminalEvent.TERMINAL_DETECT)
public class TerminalDetectHandlerImpl implements DispatcherHandlerSPI {

    @Autowired
    private TerminalDetectService detectService;

    @Override
    public void dispatch(DispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        Assert.hasLength(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");
        String data = (String) request.getData();
        TerminalDetectResult result = JSON.parseObject(data, TerminalDetectResult.class);

        detectService.updateBasicInfoAndDetect(request.getTerminalId(), result);
    }
}
