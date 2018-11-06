package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.TranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.ShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.DispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.NoticeEvent;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.TerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.DispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.NoticeRequest;
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
public class CheckUpgradeHandlerImpl implements DispatcherHandlerSPI {

    @Autowired
    private TranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalEventNoticeSPI terminalEventNoticeSPI;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUpgradeHandlerImpl.class);

    @Override
    public void dispatch(DispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        //保存终端基本信息
        saveBasicInfo(request);
        //通知上层组件当前终端为在线状态
        NoticeRequest noticeRequest = new NoticeRequest(NoticeEvent.ONLINE, request.getTerminalId());
        terminalEventNoticeSPI.notify(noticeRequest);
        //TODO 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        ShineMessageRequest shineMessageRequest = new ShineMessageRequest();
        try {
            messageHandlerAPI.response(shineMessageRequest);
        } catch (BusinessException e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }

    private void saveBasicInfo(DispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest 不能为null");
        Assert.hasLength(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");
        String terminalId = request.getTerminalId();
        TerminalBasicInfoEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            basicInfoEntity = new TerminalBasicInfoEntity();
        }

        String jsonData = String.valueOf(request.getData());
        ShineTerminalBasicInfo shineTerminalBasicInfo = JSON.parseObject(jsonData, ShineTerminalBasicInfo.class);
        BeanCopier beanCopier = BeanCopier.create(shineTerminalBasicInfo.getClass(), basicInfoEntity.getClass(), false);
        beanCopier.copy(shineTerminalBasicInfo, basicInfoEntity, null);
        Date now = new Date();
        basicInfoEntity.setCreateTime(now);
        basicInfoEntity.setUpdateTime(now);
        basicInfoDAO.save(basicInfoEntity);
    }
}
