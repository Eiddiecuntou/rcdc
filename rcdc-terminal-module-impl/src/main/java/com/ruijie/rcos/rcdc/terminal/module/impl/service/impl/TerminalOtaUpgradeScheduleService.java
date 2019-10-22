package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static final int TIME_OUT = 5 * 60;

    private UUID upgradeTaskId;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    public TerminalOtaUpgradeScheduleService(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

    @Override
    public void run() {
        LOGGER.debug("开始处理OTA升级定时任务");
        List<TerminalSystemUpgradeTerminalEntity> terminalList = systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(upgradeTaskId, CbbSystemUpgradeStateEnums.UPGRADING);
        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : terminalList) {
            boolean isTimeout = TerminalDateUtil.isTimeout(upgradeTerminal.getCreateTime(), TIME_OUT);
            if (isTimeout) {
                upgradeTerminal.setState(CbbSystemUpgradeStateEnums.TIMEOUT);
                systemUpgradeTerminalDAO.save(upgradeTerminal);
            }
        }
    }

}
