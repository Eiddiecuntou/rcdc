package com.ruijie.rcos.rcdc.terminal.module.impl.spi.maintenance;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeServiceImpl;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: 进入维护模式前校验
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/04/02
 *
 * @author nt
 */
@Service
public class BeforeEnteringMaintenanceHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeEnteringMaintenanceHandler.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO systemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeServiceImpl terminalSystemUpgradeService;

    /**
     * 进入维护模式前校验
     *
     * @throws BusinessException 业务异常
     */
    public void handle() throws BusinessException {
        TerminalTypeArchType type = TerminalTypeArchType.VDI_LINUX_X86;
        List<TerminalSystemUpgradePackageEntity> upgradePackageList =
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(type.getTerminalType(), type.getArchType(), false);
        if (CollectionUtils.isEmpty(upgradePackageList)) {
            LOGGER.info("终端组件进入维护模式校验： 无可用的[{}]刷机包，校验通过", type.name());
            return;
        }
        for (TerminalSystemUpgradePackageEntity packageEntity : upgradePackageList) {
            boolean hasUpgradingTask = terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageEntity.getId());
            if (hasUpgradingTask) {
                LOGGER.info("终端组件进入维护模式校验： 存在进行中的[{}]刷机任务，校验未通过", type.name());
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_MAINTENANCE_PRE_VALIDATE_FAIL_FOR_LINUX_VDI_UPGRADING_TASK, type.name());
            }
        }
        LOGGER.info("终端组件进入维护模式校验： 不存在进行中的[{}]刷机任务，校验通过", type.name());
    }
}
