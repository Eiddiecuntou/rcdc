package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.Arrays;
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
    private SystemUpgradeStartConfirmHandler confirmHandler;

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
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays.asList(new CbbSystemUpgradeTaskStateEnums[] {
            CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradeTaskList =
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc(stateList);
        if (CollectionUtils.isEmpty(upgradeTaskList)) {
            LOGGER.info("无正在进行中的刷机任务");
            return;
        }

        for (TerminalSystemUpgradeEntity upgradeTask : upgradeTaskList) {
            List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList =
                    systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
            if (CollectionUtils.isEmpty(upgradeTerminalList)) {
                LOGGER.debug("刷机任务无刷机终端，关闭刷机任务");
                // 设置刷机任务为关闭状态
                upgradeTask.setState(CbbSystemUpgradeTaskStateEnums.CLOSING);
                systemUpgradeService.modifySystemUpgradeState(upgradeTask);
                continue;
            }

            dealUpgradingTask(upgradeTask, upgradeTerminalList);
        }
    }

    private void dealUpgradingTask(TerminalSystemUpgradeEntity upgradeTask,
            List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) throws BusinessException {
        // 执行刷机终端处理
        confirmHandler.execute(upgradeTerminalList);
        stateSyncHandler.execute(upgradeTerminalList);
        startWaitingHandler.execute(upgradeTerminalList, upgradeTask.getUpgradePackageId());

        // 判断刷机终端是否全部为成功状态，是则将刷机任务设为完成状态
        checkUpgradeTaskSuccessFinish(upgradeTask);
    }

    /**
     * 判断刷机任务是否全部处于成功状态，是则将刷机任务设为完成状态
     * 
     * @param upgradeTask 刷机任务
     * @param upgradeTerminalList 刷机终端列表
     * @throws BusinessException 业务异常
     */
    private void checkUpgradeTaskSuccessFinish(TerminalSystemUpgradeEntity upgradeTask) throws BusinessException {
        // 重新获取刷机终端列表，防止在定时任务执行过程中有刷机终端追加进入任务
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList =
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());

        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : upgradeTerminalList) {
            if (isUnsuccessState(upgradeTerminal.getState())) {
                LOGGER.debug("存在非成功状态的刷机终端，刷机任务继续进行");
                return;
            }
        }
        LOGGER.debug("刷机终端的状态均为成功状态，刷机任务自动完成");
        upgradeTask.setState(CbbSystemUpgradeTaskStateEnums.CLOSING);
        systemUpgradeService.modifySystemUpgradeState(upgradeTask);
    }

    private boolean isUnsuccessState(CbbSystemUpgradeStateEnums state) {
        return state != CbbSystemUpgradeStateEnums.SUCCESS;
    }


}
