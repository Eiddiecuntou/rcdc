package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/17
 *
 * @author hs
 */
@Service
public class TerminalOtaUpgradeScheduleService implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOtaUpgradeScheduleService.class);

    private static final int TIME_OUT = 10 * 60;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Override
    public void run() {
        LOGGER.debug("开始处理OTA升级定时任务");
        TerminalSystemUpgradePackageEntity upgradePackage = terminalSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList = terminalSystemUpgradeDAO
                .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId(), stateList);
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("没有OTA升级任务，不查询终端状态");
            return;
        }

        UUID upgradeTaskId = upgradingTaskList.get(0).getId();
        List<TerminalSystemUpgradeTerminalEntity> terminalList =
            systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(upgradeTaskId, CbbSystemUpgradeStateEnums.UPGRADING);
        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : terminalList) {
            boolean isTimeout = TerminalDateUtil.isTimeout(upgradeTerminal.getCreateTime(), TIME_OUT);
            if (isTimeout) {
                upgradeTerminal.setState(CbbSystemUpgradeStateEnums.TIMEOUT);
                systemUpgradeTerminalDAO.save(upgradeTerminal);
            }
        }
    }

}
