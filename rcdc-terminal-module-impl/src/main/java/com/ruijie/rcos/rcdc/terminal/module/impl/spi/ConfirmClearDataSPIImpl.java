package com.ruijie.rcos.rcdc.terminal.module.impl.spi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * Description: 确认数据盘清空应答消息处理
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/7 16:16
 *
 * @author conghaifeng
 */
@DispatcherImplemetion(ShineAction.CONFIRM_CLEAR_DATA)
public class ConfirmClearDataSPIImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    public static final Logger LOGGER = LoggerFactory.getLogger(ConfirmClearDataSPIImpl.class);

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request,"CbbDispatcherRequest can not be null");
        Assert.hasText(request.getTerminalId(), "terminalId 不能为空");
        //记录shine选择进行数据盘清空
        LOGGER.warn("终端开始进行数据盘清空,terminalId = [{}]", request.getTerminalId());
        //记录系统日志
        baseSystemLogMgmtAPI.createSystemLog(new BaseCreateSystemLogRequest(BusinessKey.RCDC_TERMINAL_CONFIRM_TO_CLEAR_DISK,
                request.getTerminalId()));
    }
}
