package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.message.CommonMessageCode;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalLogName;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 收集日志应答消息处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
@DispatcherImplemetion(ShineAction.COLLECT_TERMINAL_LOG_FINISH)
public class CollectLogResponseSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectLogResponseSPIImpl.class);

    @Autowired
    private CollectLogCacheManager collectLogCacheManager;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");
        Assert.hasText(request.getData(), "data不能为null");
        CbbShineMessageResponse<TerminalLogName> response = MessageUtils.parse(request.getData(), TerminalLogName.class);
        if (CommonMessageCode.SUCCESS == response.getCode()) {
            collectLogCacheManager.updateState(request.getTerminalId(), CbbCollectLogStateEnums.DONE, response.getContent().getLogName());
            LOGGER.debug("日志上传成功；terminalId:{};logName:{}", request.getTerminalId(), response.getContent().getLogName());
            return;
        }
        LOGGER.error("日志收集失败；terminalId:{}", request.getTerminalId());
        collectLogCacheManager.updateState(request.getTerminalId(), CbbCollectLogStateEnums.FAULT);
    }
}
