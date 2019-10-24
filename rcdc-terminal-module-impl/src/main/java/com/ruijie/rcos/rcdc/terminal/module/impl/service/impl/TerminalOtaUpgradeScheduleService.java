package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Override
    public void run() {
        LOGGER.debug("开始处理OTA升级定时任务");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = systemUpgradeService
                .getSystemUpgradeTaskByTerminalType(CbbTerminalTypeEnums.VDI_ANDROID);
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
                try {
                    systemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
                } catch (BusinessException e) {
                    LOGGER.error("同步终端状态失败", e);
                }
            }
        }
    }

}
