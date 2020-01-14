package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
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
 * @param <T> 返回的升级对象信息
 *
 * @author nt
 */
public abstract class AbstractSystemUpgradeHandler<T> implements TerminalSystemUpgradeHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSystemUpgradeHandler.class);

    @Override
    public SystemUpgradeCheckResult<T> checkSystemUpgrade(CbbTerminalTypeEnums terminalType, TerminalEntity terminalEntity) throws BusinessException {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(terminalEntity, "terminalEntity can not be blank");
        Assert.hasText(terminalEntity.getTerminalId(), "terminalId can not be blank");
        Assert.notNull(terminalEntity.getGroupId(), "terminal group id can not be null");

        boolean enableUpgrade = isTerminalNeedUpgrade(terminalEntity, terminalType);
        if (!enableUpgrade) {
            return buildNoNeedCheckResult();
        }

        TerminalSystemUpgradePackageEntity upgradePackage = getTerminalSystemUpgradePackageDAO().findFirstByPackageType(terminalType);
        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        SystemUpgradeCheckResult<T> checkResult = getCheckResult(upgradePackage, upgradeTask);
        return checkResult;
    }

    private boolean isTerminalNeedUpgrade(TerminalEntity terminalEntity, CbbTerminalTypeEnums terminalType) {

        boolean enableUpgrade = isTerminalEnableUpgrade(terminalEntity, terminalType);
        if (!enableUpgrade) {
            LOGGER.info("终端[{}]不升级");
            return false;
        }

        if (upgradingNumLimit()) {
            LOGGER.info("正在升级中的终端数量超出限制");
            return false;
        }

        // TODO 需优化
        TerminalSystemUpgradePackageEntity upgradePackage = getTerminalSystemUpgradePackageDAO().findFirstByPackageType(terminalType);
        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity =
                getSystemUpgradeService().getSystemUpgradeTerminalByTaskId(terminalEntity.getTerminalId(), upgradeTask.getId());
        if (upgradeTerminalEntity.getState() == CbbSystemUpgradeStateEnums.WAIT) {
            LOGGER.info("终端[{}]处于准备升级状态", upgradeTerminalEntity.getTerminalId());
            return true;
        }

        return false;
    }

    @Override
    public boolean isTerminalEnableUpgrade(TerminalEntity terminalEntity, CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        Assert.notNull(terminalType, "terminalType can not be null");

        String terminalId = terminalEntity.getTerminalId();

        TerminalSystemUpgradePackageEntity upgradePackage = getTerminalSystemUpgradePackageDAO().findFirstByPackageType(terminalType);
        if (upgradePackage == null || upgradePackage.getIsDelete() == true) {
            LOGGER.info("终端[{}]的可用刷机包不存在", terminalId);
            return false;
        }

        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        if (upgradeTask == null) {
            LOGGER.info("终端[{}]无进行中的升级任务", terminalId);
            return false;
        }

        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity =
                getSystemUpgradeService().getSystemUpgradeTerminalByTaskId(terminalId, upgradeTask.getId());
        boolean isGroupInUpgrade = getSystemUpgradeService().isGroupInUpgradeTask(upgradeTask.getId(), terminalEntity.getGroupId());

        if (notInTask(upgradeTerminalEntity, isGroupInUpgrade)) {
            LOGGER.info("终端[{}]不在任务中", terminalId);
            return false;
        }

        return true;
    }

    private boolean notInTask(TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity, boolean isGroupInUpgrade) {
        if (upgradeTerminalEntity == null) {
            return !isGroupInUpgrade;
        }

        return false;
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
        Assert.hasText(terminalId, "terminalId can not be blank");
        return true;
    }

    @Override
    public void releaseUpgradeQuota(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");
    }

    protected abstract SystemUpgradeCheckResult<T> getCheckResult(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalSystemUpgradeEntity upgradeTask) throws BusinessException;

    protected abstract TerminalSystemUpgradeService getSystemUpgradeService();

    protected abstract TerminalSystemUpgradePackageDAO getTerminalSystemUpgradePackageDAO();

    protected abstract boolean upgradingNumLimit();

}
