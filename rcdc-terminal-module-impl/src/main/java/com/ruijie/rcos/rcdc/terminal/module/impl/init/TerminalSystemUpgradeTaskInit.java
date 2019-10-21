package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;

/**
 * 
 * Description: 终端刷机任务初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月14日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeTaskInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeTaskInit.class);

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    @Autowired
    private TerminalSystemUpgradeSupportService systemUpgradeSupportService;

    @Override
    public void safeInit() {
        LOGGER.info("开始终端刷机服务初始化...");
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING, CbbSystemUpgradeTaskStateEnums.CLOSING});
        final List<TerminalSystemUpgradeEntity> upgradingTaskList = systemUpgradeDAO.
                findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, stateList);
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("无进行中的刷机任务");
            return;
        }

        for (TerminalSystemUpgradeEntity upgradingTask : upgradingTaskList) {
            startSystemUpgradeSupportService(upgradingTask.getUpgradePackageId());
        }

        LOGGER.info("完成终端刷机服务初始化...");
    }

    private void startSystemUpgradeSupportService(UUID packageId) {
        try {
            final TerminalSystemUpgradePackageEntity systemUpgradePackage = systemUpgradePackageService.getSystemUpgradePackage(packageId);
            LOGGER.info("存在进行中的终端刷机任务，开启相关刷机服务...");
            systemUpgradeSupportService.openSystemUpgradeService(systemUpgradePackage);
        } catch (BusinessException e) {
            LOGGER.error("开启终端刷机相关服务失败", e);
        }
    }

}
