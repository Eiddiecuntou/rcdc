package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalComponentUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalVersionRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalVersionResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * Description: 终端检查升级，同时需要保存终端基本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@DispatcherImplemetion(ReceiveTerminalEvent.CHECK_UPGRADE)
public class CheckUpgradeHandlerImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;
    
    @Autowired
    private CbbTerminalComponentUpgradeAPI componentUpgradeAPI;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUpgradeHandlerImpl.class);

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        //保存终端基本信息
        saveBasicInfo(request);
        //检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        String reqData = (String) request.getData();
        CbbTerminalVersionRequest versionReq = JSON.parseObject(reqData, CbbTerminalVersionRequest.class);
        CbbTerminalVersionResponse versionResp = componentUpgradeAPI.getVersion(versionReq);
        
        CbbResponseShineMessage cbbShineMessageRequest = buildMessageResponse(request, versionResp);
        try {
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (Exception e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }

    private CbbResponseShineMessage buildMessageResponse(CbbDispatcherRequest request,
            CbbTerminalVersionResponse versionResp) {
        CbbResponseShineMessage cbbShineMessageRequest = CbbResponseShineMessage.create(request.getDispatcherKey(), request.getTerminalId(), request.getRequestId());
        cbbShineMessageRequest.setCode(0);
        cbbShineMessageRequest.setContent(versionResp);
        return cbbShineMessageRequest;
    }

    private void saveBasicInfo(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest 不能为null");
        Assert.notNull(request.getData(), "报文消息体不能为空");
        String terminalId = request.getTerminalId();
        TerminalEntity basicInfoEntity = basicInfoDAO.findFirstByTerminalId(terminalId);
        Date now = new Date();
        if (basicInfoEntity == null) {
            LOGGER.debug("新终端接入,terminalId:[{}]", terminalId);
            basicInfoEntity = new TerminalEntity();
            basicInfoEntity.setCreateTime(now);
        }
        String jsonData = String.valueOf(request.getData());
        ShineTerminalBasicInfo shineTerminalBasicInfo = JSON.parseObject(jsonData, ShineTerminalBasicInfo.class);
        BeanUtils.copyProperties(shineTerminalBasicInfo, basicInfoEntity);
        basicInfoEntity.setLastOnlineTime(now);
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        basicInfoDAO.save(basicInfoEntity);
    }

}
