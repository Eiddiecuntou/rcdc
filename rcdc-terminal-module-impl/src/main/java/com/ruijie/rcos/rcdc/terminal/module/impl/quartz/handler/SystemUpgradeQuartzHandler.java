package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 系统升级状态同步定时器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeQuartzHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeQuartzHandler.class);

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private SystemUpgradeStateSynctHandler stateSyncHandler;

    @Autowired
    private SystemUpgradeStartConfirmHandler confirmHandler;

    @Override
    public void run() {
        LOGGER.debug("开始处理终端系统刷机定时任务...");
        try {
            dealAllUpgradingTask();
        } catch (Exception e) {
            LOGGER.error("处理终端系统刷机定时任务异常", e);
        }
        LOGGER.debug("完成处理终端系统刷机定时任务");
    }

    /**
     * 处理刷机任务
     * 
     */
    private void dealAllUpgradingTask() {
        List<CbbSystemUpgradeTaskStateEnums> stateList =
                Arrays.asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradeTaskList = systemUpgradeDAO
                .findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, stateList);
        if (CollectionUtils.isEmpty(upgradeTaskList)) {
            LOGGER.info("无正在进行中的刷机任务");
            return;
        }

        for (TerminalSystemUpgradeEntity upgradeTask : upgradeTaskList) {
            List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
            if (CollectionUtils.isEmpty(upgradeTerminalList)) {
                LOGGER.debug("刷机任务无刷机终端");
                return;
            }

            dealUpgradingTask(upgradeTerminalList);
        }
    }

    private void dealUpgradingTask(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) {
        // 执行刷机终端处理
        confirmHandler.execute(upgradeTerminalList);
        stateSyncHandler.execute(upgradeTerminalList);
    }

}
