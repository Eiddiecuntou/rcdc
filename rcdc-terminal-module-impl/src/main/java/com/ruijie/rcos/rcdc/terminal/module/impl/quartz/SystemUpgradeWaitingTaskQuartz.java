package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.quartz.Quartz;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;

/**
 * 
 * Description: 定时处理系统升级队列中的任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月24日
 * 
 * @author nt
 */
@GlobalUniqueBean(value = "systemUpgradeQuartz")
@Quartz(cron = "0/15 * *  * * ?", name = BusinessKey.RCDC_TERMINAL_QUARTZ_DEAL_SYSTEM_UPGRADE_WAITING_TASK)
public class SystemUpgradeWaitingTaskQuartz implements QuartzTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeWaitingTaskQuartz.class);

    @Autowired
    private SystemUpgradeTaskManager taskManager;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Override
    public void execute() throws Exception {
        LOGGER.debug("start to deal with waiting system upgrade task...");
        dealWaitingTask();
        LOGGER.debug("finish deal with waiting system upgrade task");
    }

    /**
     * 每15秒检查一次队列中升级中数量是否有空闲，如果有将等待中的任务加入升级中
     */
    private void dealWaitingTask() {
        List<SystemUpgradeTask> startTaskList = taskManager.startWaitTask();
        if (CollectionUtils.isEmpty(startTaskList)) {
            LOGGER.debug("no tasks need to be processed");
            return;
        }

        // 发送升级指令
        for (SystemUpgradeTask task : startTaskList) {
            // 下发系统刷机指令
            TerminalSystemUpgradePackageEntity upgradePackage =
                    termianlSystemUpgradePackageDAO.findFirstByPackageType(task.getPlatform());
            if (upgradePackage == null) {
                LOGGER.info("终端类型[" + task.getPlatform() + "]升级包不存在");
                taskManager.modifyTaskState(task.getTerminalId(), CbbSystemUpgradeStateEnums.WAIT);
                continue;
            }
            TerminalSystemUpgradeMsg upgradeMsg =
                    new TerminalSystemUpgradeMsg(upgradePackage.getImgName(), upgradePackage.getPackageVersion());
            try {
                LOGGER.debug("终端[" + task.getTerminalId() + "]开始发送升级指令");
                terminalSystemUpgradeService.systemUpgrade(task.getTerminalId(), upgradeMsg);
                LOGGER.debug("终端[" + task.getTerminalId() + "]发送升级指令成功");
                task.setIsSend(true);
            } catch (Exception e) {
                LOGGER.info("终端[" + task.getTerminalId() + "]升级指令发送失败");
                // 系统刷机指令发送失败，将任务重置为等待中
                taskManager.modifyTaskState(task.getTerminalId(), CbbSystemUpgradeStateEnums.WAIT);
            }
        }
    }


}

