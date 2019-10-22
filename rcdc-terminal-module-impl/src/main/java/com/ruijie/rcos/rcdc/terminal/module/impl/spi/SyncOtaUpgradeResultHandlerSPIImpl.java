package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.OtaUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");
        // 保存终端基本信息
        String terminalId = request.getTerminalId();
        OtaUpgradeResultInfo otaUpgradeResultInfo = convertJsondata(request);
        basicInfoService.saveBasicInfo(terminalId, otaUpgradeResultInfo.getBasicInfo());
        updateTerminalUpgradeStatus(otaUpgradeResultInfo);
    }

    private OtaUpgradeResultInfo convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        OtaUpgradeResultInfo otaUpgradeResultInfo = JSON.parseObject(jsonData, OtaUpgradeResultInfo.class);
        return otaUpgradeResultInfo;
    }

    private void updateTerminalUpgradeStatus(OtaUpgradeResultInfo otaUpgradeResultInfo) {
        Assert.notNull(otaUpgradeResultInfo, "otaUpgradeResultInfo can not be null");
        Assert.notNull(otaUpgradeResultInfo.getOtaVersion(), "otaUpgradeResultInfo.getOtaVersion() can not be null");
        Assert.notNull(otaUpgradeResultInfo.getBasicInfo(), "otaUpgradeResultInfo.getBasicInfo() can not be null");
        String terminalId = otaUpgradeResultInfo.getBasicInfo().getTerminalId();
        TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        if (upgradePackage.getIsDelete() == true) {
            LOGGER.info("OTA升级包被删除");
            return;
        }
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList = systemUpgradeDAO
                .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId(), stateList);
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("没有OTA升级任务");
            return;
        }
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = systemUpgradeTerminalDAO
                .findFirstBySysUpgradeIdAndTerminalId(upgradingTaskList.get(0).getId(), terminalId);
        if (upgradeTerminal == null) {
            upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
            upgradeTerminal.setSysUpgradeId(upgradePackage.getId());
            upgradeTerminal.setTerminalId(terminalId);
            upgradeTerminal.setCreateTime(new Date());
        }
        upgradeTerminal.setState(otaUpgradeResultInfo.getUpgradeResult());
        systemUpgradeTerminalDAO.save(upgradeTerminal);
    }
}
