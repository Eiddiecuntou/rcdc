package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUpgradeTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.PackageObtainModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

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

    private static final String SYSTEM_OS_TYPE_ANDROID = "Android";

    @Autowired
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    @Override
    public SystemUpgradeCheckResult<T> checkSystemUpgrade(CbbTerminalTypeEnums terminalType, TerminalEntity terminalEntity) throws BusinessException {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(terminalEntity, "terminalEntity can not be blank");
        Assert.hasText(terminalEntity.getTerminalId(), "terminalId can not be blank");
        Assert.notNull(terminalEntity.getGroupId(), "terminal group id can not be null");

        CheckSystemUpgradeResultEnums checkSystemUpgradeResult = isTerminalNeedUpgrade(terminalEntity, terminalType);
        if (checkSystemUpgradeResult == CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE
                || checkSystemUpgradeResult == CheckSystemUpgradeResultEnums.UNSUPPORT) {
            return buildNoNeedCheckResult(checkSystemUpgradeResult, terminalEntity);
        }

        TerminalSystemUpgradePackageEntity upgradePackage = getTerminalSystemUpgradePackageDAO().findFirstByPackageType(terminalType);
        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        SystemUpgradeCheckResult<T> checkResult = getCheckResult(upgradePackage, upgradeTask);
        checkResult.setSystemUpgradeCode(checkSystemUpgradeResult.getResult());
        
        return checkResult;
    }

    private CheckSystemUpgradeResultEnums isTerminalNeedUpgrade(TerminalEntity terminalEntity, CbbTerminalTypeEnums terminalType) {

        boolean enableUpgrade = isTerminalEnableUpgrade(terminalEntity, terminalType);
        if (!enableUpgrade) {
            LOGGER.info("终端[{}]不升级", terminalEntity.getTerminalId());
            return CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE;
        }

        // TODO 需优化
        TerminalSystemUpgradePackageEntity upgradePackage = getTerminalSystemUpgradePackageDAO().findFirstByPackageType(terminalType);
        TerminalSystemUpgradeEntity upgradeTask = getSystemUpgradeService().getUpgradingSystemUpgradeTaskByPackageId(upgradePackage.getId());
        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity =
                getSystemUpgradeService().getSystemUpgradeTerminalByTaskId(terminalEntity.getTerminalId(), upgradeTask.getId());
        if (upgradeTerminalEntity == null) {
            // 终端是在升级任务开启之后接入的
            return CheckSystemUpgradeResultEnums.NEED_UPGRADE;
        }

        CbbSystemUpgradeStateEnums state = upgradeTerminalEntity.getState();
        if (isNoNeedUpgrade(state, terminalType)) {
            LOGGER.info("终端[{}]处于[{}]升级状态，无需升级", upgradeTerminalEntity.getTerminalId(), state.name());
            return CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE;
        }


        if (state == CbbSystemUpgradeStateEnums.UPGRADING && upgradePackage.getPackageType() == CbbTerminalTypeEnums.IDV_LINUX) {
            LOGGER.info("终端[{}]正在升级中", upgradeTerminalEntity.getTerminalId());
            return CheckSystemUpgradeResultEnums.UPGRADING;
        }

        LOGGER.info("终端[{}]需要升级", upgradeTerminalEntity.getTerminalId());
        return CheckSystemUpgradeResultEnums.NEED_UPGRADE;
    }

    private boolean isNoNeedUpgrade(CbbSystemUpgradeStateEnums state, CbbTerminalTypeEnums terminalType) {
        if (!enableUpgradeOnlyOnce(terminalType)) {
            LOGGER.info("终端类型[{}]的升级任务可重复升级", terminalType.name());
            return false;
        }

        return state == CbbSystemUpgradeStateEnums.SUCCESS || state == CbbSystemUpgradeStateEnums.UNDO;
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
            // 判断是否需要加入升级任务
            return shouldAddToUpgradeList(terminalEntity, terminalId, upgradeTask);
        }

        return true;
    }

    private boolean shouldAddToUpgradeList(TerminalEntity terminalEntity, String terminalId, TerminalSystemUpgradeEntity upgradeTask) {
        if (SYSTEM_OS_TYPE_ANDROID.equals(terminalEntity.getTerminalOsType())) {
            CbbUpgradeTerminalDTO addRequest = new CbbUpgradeTerminalDTO();
            addRequest.setTerminalId(terminalId);
            addRequest.setUpgradeTaskId(upgradeTask.getId());
            try {
                cbbTerminalUpgradeAPI.addSystemUpgradeTerminal(addRequest);
                LOGGER.info("终端[{}]加入系统升级任务成功", terminalId);
                return true;
            } catch (BusinessException e) {
                LOGGER.error("终端[" + terminalId + "]加入系统升级任务失败", e);
            }
        }
        return false;
    }

    private boolean notInTask(TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity, boolean isGroupInUpgrade) {
        if (upgradeTerminalEntity == null) {
            return !isGroupInUpgrade;
        }

        return false;
    }

    private SystemUpgradeCheckResult<T> buildNoNeedCheckResult(CheckSystemUpgradeResultEnums result, TerminalEntity terminalEntity) {
        SystemUpgradeCheckResult<T> noNeedResult = new SystemUpgradeCheckResult();
        noNeedResult.setSystemUpgradeCode(result.getResult());
        noNeedResult.setContent(null);

        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(terminalEntity.getPlatform().name(), terminalEntity.getTerminalOsType());
        if (terminalType == CbbTerminalTypeEnums.VDI_LINUX) {
            noNeedResult.setPackageObtainMode(PackageObtainModeEnums.SAMBA);
        } else {
            noNeedResult.setPackageObtainMode(PackageObtainModeEnums.OTA);
        }

        return noNeedResult;
    }

    @Override
    public void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        // 不处理，仅为子类提供默认实现
    }

    @Override
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage, TerminalSystemUpgradeEntity upgradeEntity)
            throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeEntity, "upgradeEntity can not be null");

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

    protected abstract boolean enableUpgradeOnlyOnce(CbbTerminalTypeEnums terminalType);

}
