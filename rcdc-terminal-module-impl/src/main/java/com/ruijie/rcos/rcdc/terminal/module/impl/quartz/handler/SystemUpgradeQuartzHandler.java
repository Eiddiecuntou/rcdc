package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
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
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private SystemUpgradeStartWaitingHandler startWaitingHandler;

    @Autowired
    private SystemUpgradeStateSynctHandler stateSyncHandler;

    @Autowired
    private SystemUpgradeTimeoutHandler timeoutHandler;

    @Override
    public void run() {
        LOGGER.debug("开始处理终端系统刷机定时任务...");
        try {
            
            dealAllUpgradingTask();
        } catch (BusinessException e) {
            LOGGER.error("处理终端系统刷机定时任务异常", e);
        }
        LOGGER.debug("完成处理终端系统刷机定时任务");
    }

    /**
     * 处理刷机任务
     * 
     * @throws BusinessException 业务异常
     */
    private void dealAllUpgradingTask() throws BusinessException {

        List<TerminalSystemUpgradeEntity> upgradeTaskList =
                systemUpgradeDAO.findByStateOrderByCreateTimeAsc(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        if (CollectionUtils.isEmpty(upgradeTaskList)) {
            LOGGER.info("无正在进行中的刷机任务");
            return;
        }

        for (TerminalSystemUpgradeEntity upgradeTask : upgradeTaskList) {
            List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList =
                    systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
            if (CollectionUtils.isEmpty(upgradeTerminalList)) {
                LOGGER.debug("刷机任务无刷机终端，关闭刷机任务");
                // 设置刷机任务为完成状态
                systemUpgradeService.modifySystemUpgradeState(upgradeTask.getId(),
                        CbbSystemUpgradeTaskStateEnums.FINISH);
                continue;
            }

            dealUpgradingTask(upgradeTask, upgradeTerminalList);
        }
    }

    private void dealUpgradingTask(TerminalSystemUpgradeEntity upgradeTask,
            List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) throws BusinessException {
        // 执行刷机终端处理
        stateSyncHandler.execute(upgradeTerminalList);
        timeoutHandler.execute(upgradeTerminalList);
        startWaitingHandler.execute(upgradeTerminalList, upgradeTask.getUpgradePackageId());

        // 判断刷机终端是否全部处于最终态，是则将刷机任务设为完成状态
        checkUpgradeTaskFinish(upgradeTask, upgradeTerminalList);
    }

    /**
     * 判断刷机任务是否全部处于最终态，是则将刷机任务设为完成状态
     * 
     * @param upgradeTask 刷机任务
     * @param upgradeTerminalList 刷机终端列表
     * @throws BusinessException 业务异常
     */
    private void checkUpgradeTaskFinish(TerminalSystemUpgradeEntity upgradeTask,
            List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) throws BusinessException {
        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : upgradeTerminalList) {
            if (isProgressState(upgradeTerminal.getState())) {
                LOGGER.debug("存在非最终态的刷机终端，刷机任务未完成");
                return;
            }
        }

        systemUpgradeService.modifySystemUpgradeState(upgradeTask.getId(), CbbSystemUpgradeTaskStateEnums.FINISH);
    }

    private boolean isProgressState(CbbSystemUpgradeStateEnums state) {
        return state == CbbSystemUpgradeStateEnums.WAIT || state == CbbSystemUpgradeStateEnums.UPGRADING;
    }


}
