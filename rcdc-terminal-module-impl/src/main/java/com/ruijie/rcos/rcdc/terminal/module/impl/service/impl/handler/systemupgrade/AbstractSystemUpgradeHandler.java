package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @param  <T> 返回的升级对象信息
 *
 * @author nt
 */
public abstract class AbstractSystemUpgradeHandler<T> implements TerminalSystemUpgradeHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSystemUpgradeHandler.class);

    @Override
    public SystemUpgradeCheckResult<T> checkSystemUpgrade(CbbTerminalTypeEnums terminalType, String terminalId) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.hasText(terminalId, "terminalId can not be blank");

        TerminalSystemUpgradePackageEntity upgradePackage = getTerminalSystemUpgradePackageDAO().findFirstByPackageType(terminalType);
        boolean enableUpgrade = isTerminalNeedUpgrade(terminalId, upgradePackage);
        if (!enableUpgrade) {
            return buildNoNeedCheckResult();
        }

        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        SystemUpgradeCheckResult<T> checkResult = getCheckResult(upgradePackage, upgradeTask);
        return checkResult;
    }

    private boolean isTerminalNeedUpgrade(String terminalId, TerminalSystemUpgradePackageEntity upgradePackage) {
        if (upgradePackage == null || upgradePackage.getIsDelete() == true) {
            LOGGER.info("终端类型[{}]的可用刷机包不存在", upgradePackage.getPackageType().name());
            return false;
        }

        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        if (upgradeTask == null) {
            LOGGER.info("终端类型[{}]无进行中的升级任务", upgradePackage.getPackageType().name());
            return false;
        }

        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity =
                getSystemUpgradeService().getSystemUpgradeTerminalByTaskId(terminalId, upgradeTask.getId());
        if (upgradeTerminalEntity == null) {
            LOGGER.info("终端[{}]未在升级任务中", terminalId);
            return false;
        }

        if (upgradeTerminalEntity.getState() != CbbSystemUpgradeStateEnums.WAIT) {
            LOGGER.info("终端[{}]未处于准备升级状态", terminalId);
            return false;
        }

        if (upgradingNumLimit()) {
            LOGGER.info("正在升级中的终端数量超出限制", terminalId);
            return false;
        }

        return true;
    }

    private SystemUpgradeCheckResult<T> buildNoNeedCheckResult() {
        SystemUpgradeCheckResult<T> noNeedResult = new SystemUpgradeCheckResult();
        noNeedResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE.getResult());
        noNeedResult.setContent(null);
        return noNeedResult;
    }

    @Override
    public void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        // 不处理，仅为子类提供默认实现
    }

    @Override
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        // 不处理，仅为子类提供默认实现
    }

    @Override
    public boolean checkAndHoldUpgradeQuota(String terminalId) {
        Assert.notNull(terminalId, "terminalId can not be blank");
        return true;
    }

    protected abstract SystemUpgradeCheckResult<T> getCheckResult(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalSystemUpgradeEntity upgradeTask);

    protected abstract TerminalSystemUpgradeService getSystemUpgradeService();

    protected abstract TerminalSystemUpgradePackageDAO getTerminalSystemUpgradePackageDAO();

    protected abstract boolean upgradingNumLimit();
}
