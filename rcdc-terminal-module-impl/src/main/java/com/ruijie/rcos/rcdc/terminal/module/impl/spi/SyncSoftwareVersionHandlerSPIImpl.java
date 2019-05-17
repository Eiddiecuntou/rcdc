package com.ruijie.rcos.rcdc.terminal.module.impl.spi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.base.sysmanage.module.def.api.SystemUpgradeAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.upgrade.BaseObtainSystemReleaseVersionRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.upgrade.BaseObtainSystemReleaseVersionResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SoftwareVersionResponseContent;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * Description: 终端同步系统版本
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019
 * /04/16
 *
 * @author nt
 */
@DispatcherImplemetion(ShineAction.REQUEST_SOFTWARE_VERSION)
public class SyncSoftwareVersionHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private SystemUpgradeAPI systemUpgradeAPI;

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSoftwareVersionHandlerSPIImpl.class);

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");

        // 获取rcdc版本信息
        BaseObtainSystemReleaseVersionRequest obtainVersionReq = new BaseObtainSystemReleaseVersionRequest();
        BaseObtainSystemReleaseVersionResponse versionResponse = null;
        try {
            versionResponse = systemUpgradeAPI.obtainSystemReleaseVersion(obtainVersionReq);
        } catch (BusinessException e) {
            LOGGER.error("获取系统版本号异常", e);
            responseError(request);
            return;
        }

        SoftwareVersionResponseContent softwareVersionResponseContent =
                new SoftwareVersionResponseContent(versionResponse.getSystemReleaseVersion());
        CbbResponseShineMessage responseMessage =
                MessageUtils.buildResponseMessage(request, softwareVersionResponseContent);
        doResponseMessage(responseMessage);
    }

    private void responseError(CbbDispatcherRequest request) {
        CbbResponseShineMessage responseMessage = MessageUtils.buildErrorResponseMessage(request);
        doResponseMessage(responseMessage);
    }

    private void doResponseMessage(CbbResponseShineMessage responseMessage) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("response get software version : {}", JSON.toJSONString(responseMessage));
            }
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("请求系统软件版本消息应答失败", e);
        }
    }

}
