package com.ruijie.rcos.rcdc.terminal.module.impl.spi;


import java.util.Date;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
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
public class CheckUpgradeHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;
    
    @Autowired
    private TerminalComponentUpgradeService componentUpgradeService;

    @Autowired
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUpgradeHandlerSPIImpl.class);

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");
        LOGGER.debug("=====终端升级报文===={}", request.getData());
        Assert.notNull(request, "DispatcherRequest不能为null");
        //保存终端基本信息
        saveBasicInfo(request);
        // 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        
        TerminalVersionResultDTO versionResult = getCheckVersionResult(request);
        CbbResponseShineMessage cbbShineMessageRequest = buildMessageResponse(request, versionResult);
        try {
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (Exception e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }
    
    private TerminalVersionResultDTO getCheckVersionResult(CbbDispatcherRequest request) {
        ShineTerminalBasicInfo basicInfo = convertJsondata(request);
        CbbTerminalTypeEnums terminalType = convertTerminalType(basicInfo.getTerminalType());
        return componentUpgradeService.getVersion(basicInfo.getRainUpgradeVersion(), terminalType);
    }
    
    private CbbTerminalTypeEnums convertTerminalType(TerminalTypeEnums terminalType) {
        // TODO 终端类型转换
        return null;
    }

    private CbbResponseShineMessage buildMessageResponse(CbbDispatcherRequest request,
            TerminalVersionResultDTO versionResult) {
        CbbResponseShineMessage cbbShineMessageRequest = CbbResponseShineMessage.create(request.getDispatcherKey(), request.getTerminalId(), request.getRequestId());
        cbbShineMessageRequest.setCode(0);
        cbbShineMessageRequest.setContent(versionResult);
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
        ShineTerminalBasicInfo shineTerminalBasicInfo = convertJsondata(request);
        BeanUtils.copyProperties(shineTerminalBasicInfo, basicInfoEntity);
        basicInfoEntity.setLastOnlineTime(now);
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        basicInfoDAO.save(basicInfoEntity);
        //通知其他组件终端为在线状态
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(NoticeEventEnums.ONLINE, request.getTerminalId());
        terminalEventNoticeSPI.notify(noticeRequest);
    }

    private ShineTerminalBasicInfo convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        ShineTerminalBasicInfo shineTerminalBasicInfo = JSON.parseObject(jsonData, ShineTerminalBasicInfo.class);
        return shineTerminalBasicInfo;
    }
}
