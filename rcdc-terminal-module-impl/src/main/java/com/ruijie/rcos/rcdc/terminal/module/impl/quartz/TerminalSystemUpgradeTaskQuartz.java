package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TermianlSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TermianlSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 定时同步终端系统升级任务状态
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月24日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeTaskQuartz {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeTaskQuartz.class);

    /**
     * 终端系统升级任务管理器
     */
    @Autowired
    private SystemUpgradeTaskManager taskManager;

    /**
     * 终端系统升级服务
     */
    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    /**
     * 终端系统升级包DAO
     */
    @Autowired
    private TermianlSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    /**
     * 
     * 每10秒与状态文件同步一次状态
     */
    @Scheduled(cron = "0/10 * *  * * ?")
    public void stateSynchronize() {
        // 获取缓存中的升级中的任务
        List<SystemUpgradeTask> upgradingTaskList = taskManager.getUpgradingTask();
        // 获取文件系统中的升级信息
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList =
                terminalSystemUpgradeService.readSystemUpgradeStateFromFile();
        if (systemUpgradeInfoList == null) {
            LOGGER.info("upgrade info in file is null");
            systemUpgradeInfoList = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("no upgrading task in cache");
            return;
        }

        for (SystemUpgradeTask task : upgradingTaskList) {
            if (StringUtils.isBlank(task.getTerminalId())) {
                LOGGER.debug("upgrade  task is invalid, remove it");
                taskManager.removeTask(task);
                continue;
            }
            // 查找匹配的升级信息
            TerminalSystemUpgradeInfo matchInfo = null;
            for (Iterator<TerminalSystemUpgradeInfo> iterator = systemUpgradeInfoList.iterator(); iterator.hasNext();) {
                TerminalSystemUpgradeInfo upgradeInfo = (TerminalSystemUpgradeInfo) iterator.next();
                if (task.getTerminalId().equals(upgradeInfo.getTerminalId())) {
                    LOGGER.debug("upgrade task find the match upgrade info, terminal id [{}]", task.getTerminalId());
                    matchInfo = upgradeInfo;
                    iterator.remove();
                    break;
                }

            }
            if (matchInfo == null) {
                // 无终端升级信息时状态同步
                LOGGER.debug("start to synchonize task state without upgrade info, terminal id [{}]",
                        task.getTerminalId());
                synchronizeStateWithoutMatchUpgradeInfo(task);
            } else {
                // 根据终端升级信息状态同步
                LOGGER.debug("start to synchonize task state with upgrade info, terminal id [{}]",
                        task.getTerminalId());
                synchronizeStateWithMatchUpgradeInfo(task, matchInfo);
            }
        }

    }

    /**
     * 每15秒检查一次队列中升级中数量是否有空闲，如果有将等待中的任务加入升级中
     */
    @Scheduled(cron = "0/15 * *  * * ?")
    public void dealWaitingTask() {
        List<SystemUpgradeTask> startTaskList = taskManager.startWaitTask();
        if (CollectionUtils.isEmpty(startTaskList)) {
            LOGGER.debug("no task to be started");
            return;
        }

        // 发送升级指令
        for (SystemUpgradeTask task : startTaskList) {
            // 下发系统刷机指令
            TermianlSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO
                    .findTermianlSystemUpgradePackageByPackageType(task.getTerminalType());
            if (upgradePackage == null) {
                LOGGER.info("终端类型[" + task.getTerminalType() + "]升级包不存在");
                taskManager.modifyTaskState(task.getTerminalId(), CbbSystemUpgradeStateEnums.WAIT);
                continue;
            }
            TerminalSystemUpgradeMsg upgradeMsg =
                    new TerminalSystemUpgradeMsg(upgradePackage.getName(), upgradePackage.getStorePath(),
                            upgradePackage.getInternalVersion(), upgradePackage.getExternalVersion());
            try {
                LOGGER.debug("终端[" + task.getTerminalId() + "]开始发送升级指令");
                terminalSystemUpgradeService.systemUpgrade(task.getTerminalId(), upgradeMsg);
                LOGGER.debug("终端[" + task.getTerminalId() + "]发送升级指令成功");
                task.setIsSend(true);
            } catch (Exception e) {
                LOGGER.info("终端[" + task.getTerminalId() + "]升级指令发送失败");
                // 系统刷机指令发送失败，将失败任务移除队列
                taskManager.removeTaskByTerminalId(task.getTerminalId());
            }
        }
    }


    private void synchronizeStateWithMatchUpgradeInfo(SystemUpgradeTask task, TerminalSystemUpgradeInfo matchInfo) {
        // TODO FIXME 根据终端升级信息状态同步,还需同数据库中的终端状态进行比对
        task.setState(matchInfo.getState());

    }

    private void synchronizeStateWithoutMatchUpgradeInfo(SystemUpgradeTask task) {
        // TODO FIXME 无终端升级信息时状态同步,判断是否第一次心跳超时,还需同数据库中的终端状态进行比对

    }
}

