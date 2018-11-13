package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbNoticeEvent;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalBasicInfoEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Date;

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
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    private static final BeanCopier BEAN_COPIER = BeanCopier.create(ShineTerminalBasicInfo.class,
            TerminalBasicInfoEntity.class, false);

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUpgradeHandlerImpl.class);

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        //保存终端基本信息
        saveBasicInfo(request);
        //通知上层组件当前终端为在线状态
        CbbNoticeRequest cbbNoticeRequest = new CbbNoticeRequest(CbbNoticeEvent.ONLINE, request.getTerminalId());
        cbbTerminalEventNoticeSPI.notify(cbbNoticeRequest);
        //TODO 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        CbbShineMessageRequest cbbShineMessageRequest = new CbbShineMessageRequest();
        try {
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (BusinessException e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }

    private void saveBasicInfo(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest 不能为null");
        Assert.hasLength(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");
        String terminalId = request.getTerminalId();
        TerminalBasicInfoEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            basicInfoEntity = new TerminalBasicInfoEntity();
        }

        String jsonData = String.valueOf(request.getData());
        ShineTerminalBasicInfo shineTerminalBasicInfo = JSON.parseObject(jsonData, ShineTerminalBasicInfo.class);
        BEAN_COPIER.copy(shineTerminalBasicInfo, basicInfoEntity, null);
        Date now = new Date();
        basicInfoEntity.setCreateTime(now);
        basicInfoEntity.setUpdateTime(now);
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        basicInfoDAO.save(basicInfoEntity);
    }
}
