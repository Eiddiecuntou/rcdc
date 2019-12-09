package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.OfflineLoginConfig;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: 终端请求当前离线登录设置
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 16:11
 *
 * @author conghaifeng
 */
@DispatcherImplemetion(ShineAction.REQUEST_OFFLINE_CONFIG)
public class CheckOfflineLoginHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    public static final Logger LOGGER = LoggerFactory.getLogger(CheckOfflineLoginHandlerSPIImpl.class);

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");
        String offlineAutoLocked = globalParameterAPI.findParameter(Constants.OFFLINE_LOGIN_TIME_KEY);
        if (offlineAutoLocked == null) {
            LOGGER.debug("离线登录设置参数不存在");
            return;
        }
        try {
            OfflineLoginConfig offlineLoginConfig = new OfflineLoginConfig(Integer.valueOf(offlineAutoLocked));
            CbbResponseShineMessage<OfflineLoginConfig> responseMessage = MessageUtils.buildResponseMessage(request, offlineLoginConfig);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端请求离线登录设置消息应答失败", e);
        }
    }
}
