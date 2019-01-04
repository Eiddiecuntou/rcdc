package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.quartz.Quartz;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;

/**
 * 
 * Description: 系统升级状态同步定时器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
@GlobalUniqueBean("upgradeStateSyncQuartz")
@Quartz(cron = "0/10 * *  * * ?")
public class SystemUpgradeStateSyncQuartz implements QuartzTask {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeStateSyncQuartz.class);

    @Autowired
    private SystemUpgradeTaskManager taskManager;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Override
    public void execute() throws Exception {
        LOGGER.debug("start to synchronize system upgrade state...");
        syncState();
        LOGGER.debug("finish synchronize system upgrade state");
    }

    private void syncState() {
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
    
    private void synchronizeStateWithMatchUpgradeInfo(SystemUpgradeTask task, TerminalSystemUpgradeInfo matchInfo) {
        // TODO FIXME 根据终端升级信息状态同步,还需同数据库中的终端状态进行比对
        task.setState(matchInfo.getState());

    }

    private void synchronizeStateWithoutMatchUpgradeInfo(SystemUpgradeTask task) {
        // TODO FIXME 无终端升级信息时状态同步,判断是否第一次心跳超时,还需同数据库中的终端状态进行比对
        long lastActiveTime = task.getTimeStamp();
        
    }

}
