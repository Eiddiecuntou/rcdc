package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Objects;

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

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        String terminalId = request.getTerminalId();
        LOGGER.debug("连接关闭事件处理，terminal={}", terminalId);

        // 终端状态更新处理
        modifyTerminalStateHandle(terminalId);

        // 终端连接关闭事件通知处理
        notifyTerminalConnectCloseHandle(terminalId);
    }

    private void modifyTerminalStateHandle(String terminalId) {
        TerminalEntity terminalEntity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (Objects.isNull(terminalEntity)) {
            LOGGER.warn("不存在terminalId=[{}]的终端记录，无需进行终端离线状态更新处理", terminalId);
            return;
        }
        basicInfoService.modifyTerminalStateToOffline(terminalId);
    }

    private void notifyTerminalConnectCloseHandle(String terminalId) {
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(CbbNoticeEventEnums.OFFLINE);
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        noticeRequest.setTerminalBasicInfo(basicInfo);
        terminalEventNoticeSPI.notify(noticeRequest);
    }
}
