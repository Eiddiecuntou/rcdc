package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/17
 *
 * @author hs
 */
public class TerminalOtaUpgradeScheduleService implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOtaUpgradeScheduleService.class);

    private static final int TIME_OUT = 10 * 60;

    private UUID upgradeTaskId;

    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    public TerminalOtaUpgradeScheduleService(UUID upgradeTaskId, TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO) {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        Assert.notNull(systemUpgradeTerminalDAO, "systemUpgradeTerminalDAO can not be null");
        this.upgradeTaskId = upgradeTaskId;
        this.systemUpgradeTerminalDAO = systemUpgradeTerminalDAO;
    }

    @Override
    public void run() {
        LOGGER.debug("开始处理OTA升级定时任务");
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
