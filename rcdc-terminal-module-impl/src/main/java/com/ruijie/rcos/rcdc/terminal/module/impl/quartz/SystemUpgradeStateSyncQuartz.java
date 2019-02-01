package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
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
@Quartz(cron = "0/10 * *  * * ?", msgKey = BusinessKey.RCDC_TERMINAL_QUARTZ_SYSTEM_UPGRADE_STATE_SYNC)
public class SystemUpgradeStateSyncQuartz implements QuartzTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeStateSyncQuartz.class);

    @Autowired
    private SystemUpgradeTaskManager taskManager;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;


    @Override
    public void execute() throws Exception {
        LOGGER.debug("start to synchronize system upgrade state...");
        try {
            syncState();
        } catch (BusinessException e) {
            LOGGER.error("synchronize system upgrade state error", e);
        }
        LOGGER.debug("finish synchronize system upgrade state");
    }

    private void syncState() throws BusinessException {
        // 获取缓存中的升级中的任务
        List<SystemUpgradeTask> upgradingTaskList = taskManager.getUpgradingTask();
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("no upgrading task in cache");
            return;
        }
        // 获取文件系统中的升级信息
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList =
                terminalSystemUpgradeService.readSystemUpgradeStateFromFile();
        if (systemUpgradeInfoList == null) {
            LOGGER.info("upgrade info in directory is null");
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
                // 无终端升级状态信息
                LOGGER.debug("no upgrade info, terminal id [{}]", task.getTerminalId());
                continue;
            }

            // 根据终端升级信息状态同步
            LOGGER.debug("start to synchonize task state with upgrade info, terminal id [{}]", task.getTerminalId());
            // 同步终端升级信息状态
            task.setState(matchInfo.getState());
        }
    }

}
