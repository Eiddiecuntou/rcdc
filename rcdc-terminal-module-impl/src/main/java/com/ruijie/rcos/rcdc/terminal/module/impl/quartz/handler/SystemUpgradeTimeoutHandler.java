package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 刷机中终端超时处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeTimeoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeTimeoutHandler.class);

    /**
     * 终端刷机超时时间2h
     */
    private static final int UPGRADING_TIME_OUT = 2 * 60 * 60;

    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    /**
     * 开始处理刷机终端超时
     * 
     * @param upgradeTerminalList 刷机终端列表
     */
    public void execute(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) {
        Assert.notNull(upgradeTerminalList, "upgradeTerminalList can not be null");

        try {
            timeout(upgradeTerminalList);
        } catch (Exception e) {
            LOGGER.error("处理刷机终端超时异常", e);
        }
    }

    /**
     * 处理刷机超时的终端
     * 
     * @param upgradeTerminalList 刷机终端列表
     * @throws BusinessException 业务异常
     */
    private void timeout(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) throws BusinessException {
        LOGGER.info("开始刷机终端超时处理...");
        int count = 0;
        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : upgradeTerminalList) {
            if (upgradeTerminal.getState() != CbbSystemUpgradeStateEnums.UPGRADING) {
                LOGGER.debug("刷机终端状态非进行中状态，不进行超时处理");
                continue;
            }
            Date startTime = upgradeTerminal.getStartTime();
            if (TerminalDateUtil.isTimeout(startTime, UPGRADING_TIME_OUT)) {
                LOGGER.debug("超时，设置刷机状态为失败");
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal.getSysUpgradeId(),
                        upgradeTerminal.getTerminalId(), CbbSystemUpgradeStateEnums.FAIL);
                count++;
            }
        }
        LOGGER.info("完成刷机终端超时处理，处理刷机超时终端{}台", count);
    }

}
