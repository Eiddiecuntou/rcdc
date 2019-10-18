package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Override
    public void run() {
        LOGGER.debug("开始处理OTA升级定时任务");
        List<TerminalSystemUpgradeTerminalEntity> terminalList = systemUpgradeTerminalDAO
                .findByTerminalTypeAndState(CbbTerminalTypeEnums.VDI_ANDROID, CbbSystemUpgradeStateEnums.UPGRADING);
        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : terminalList) {
            boolean isTimeout = TerminalDateUtil.isTimeout(upgradeTerminal.getCreateTime(), TIME_OUT);
            if (isTimeout) {
                // FIXME 超时设置成失败就好
                upgradeTerminal.setState(CbbSystemUpgradeStateEnums.TIMEOUT);
                systemUpgradeTerminalDAO.save(upgradeTerminal);
            }
        }
    }

}
