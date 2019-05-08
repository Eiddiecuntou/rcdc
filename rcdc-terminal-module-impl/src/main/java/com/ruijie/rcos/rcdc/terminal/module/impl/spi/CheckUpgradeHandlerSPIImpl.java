package com.ruijie.rcos.rcdc.terminal.module.impl.spi;


import java.util.Date;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
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
@DispatcherImplemetion(ShineAction.CHECK_UPGRADE)
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

        // 保存终端基本信息
        String terminalId = request.getTerminalId();
        ShineTerminalBasicInfo basicInfo = convertJsondata(request);
        saveBasicInfo(terminalId, basicInfo);

        // 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        TerminalVersionResultDTO versionResult = componentUpgradeService.getVersion(basicInfo.getRainUpgradeVersion(), basicInfo.getPlatform());
        CbbResponseShineMessage cbbShineMessageRequest = MessageUtils.buildResponseMessage(request, versionResult);
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("response check upgrade : {}", JSON.toJSONString(cbbShineMessageRequest));
            }
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (Exception e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }

    private void saveBasicInfo(String terminalId, ShineTerminalBasicInfo shineTerminalBasicInfo) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(shineTerminalBasicInfo, "终端信息不能为空");

        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        Date now = new Date();
        if (basicInfoEntity == null) {
            LOGGER.info("新终端接入,terminalId:[{}]", terminalId);
            basicInfoEntity = new TerminalEntity();
            basicInfoEntity.setCreateTime(now);
        }
        BeanUtils.copyProperties(shineTerminalBasicInfo, basicInfoEntity);
        basicInfoEntity.setLastOnlineTime(now);
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        basicInfoDAO.save(basicInfoEntity);
        // 通知其他组件终端为在线状态
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(NoticeEventEnums.ONLINE, terminalId);
        terminalEventNoticeSPI.notify(noticeRequest);
    }

    private ShineTerminalBasicInfo convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        ShineTerminalBasicInfo basicInfo = JSON.parseObject(jsonData, ShineTerminalBasicInfo.class);
        return basicInfo;
    }
}
