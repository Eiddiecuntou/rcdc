package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.OtaUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/12
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.OTA_UPGRADE_RESULT)
public class SyncOtaUpgradeResultHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncOtaUpgradeResultHandlerSPIImpl.class);

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalComponentUpgradeService componentUpgradeService;

    @Autowired
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");
        // 保存终端基本信息
        String terminalId = request.getTerminalId();
        OtaUpgradeResultInfo otaUpgradeResultInfo = convertJsondata(request);
        saveBasicInfo(terminalId, otaUpgradeResultInfo.getBasicInfo());
        updateTerminalUpgradeStatus(otaUpgradeResultInfo);
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
            basicInfoEntity.setGroupId(Constants.DEFAULT_TERMINAL_GROUP_UUID);
        }
        BeanUtils.copyProperties(shineTerminalBasicInfo, basicInfoEntity);
        basicInfoEntity.setLastOnlineTime(now);
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        basicInfoDAO.save(basicInfoEntity);
        // 通知其他组件终端为在线状态
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(CbbNoticeEventEnums.ONLINE, terminalId);
        terminalEventNoticeSPI.notify(noticeRequest);
    }

    private OtaUpgradeResultInfo convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        OtaUpgradeResultInfo otaUpgradeResultInfo = JSON.parseObject(jsonData, OtaUpgradeResultInfo.class);
        return otaUpgradeResultInfo;
    }

    private void updateTerminalUpgradeStatus(OtaUpgradeResultInfo otaUpgradeResultInfo) {
        Assert.notNull(otaUpgradeResultInfo, "otaUpgradeResultInfo can not be null");
        TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO.findFirstByPackageType(TerminalPlatformEnums.RK3188);
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = systemUpgradeTerminalDAO
                .findFirstBySysUpgradeIdAndTerminalId(upgradePackage.getId(), otaUpgradeResultInfo.getBasicInfo().getTerminalId());
        if (upgradeTerminal != null) {
            upgradeTerminal.setState(otaUpgradeResultInfo.getUpgradeResult());
            systemUpgradeTerminalDAO.save(upgradeTerminal);
        }
    }
}
