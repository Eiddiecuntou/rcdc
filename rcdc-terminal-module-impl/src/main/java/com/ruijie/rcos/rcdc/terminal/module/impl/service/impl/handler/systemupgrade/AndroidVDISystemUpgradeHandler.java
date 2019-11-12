package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
@Service
public class AndroidVDISystemUpgradeHandler extends AbstractSystemUpgradeHandler<AndroidVDICheckResultContent> {

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private BtService btService;

    @Override
    protected TerminalSystemUpgradeService getSystemUpgradeService() {
        return systemUpgradeService;
    }

    @Override
    protected TerminalSystemUpgradePackageDAO getTerminalSystemUpgradePackageDAO() {
        return terminalSystemUpgradePackageDAO;
    }

    @Override
    protected SystemUpgradeCheckResult<AndroidVDICheckResultContent> getCheckResult(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalSystemUpgradeEntity upgradeTask) {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTask, "upgradeTask can not be null");

        AndroidVDICheckResultContent resultContent = new AndroidVDICheckResultContent();
        resultContent.setOtaMD5(upgradePackage.getFileMd5());
        resultContent.setOtaSeedLink(upgradePackage.getSeedPath());
        resultContent.setOtaSeedMD5(upgradePackage.getSeedMd5());
        resultContent.setUpgradeMode(upgradeTask.getUpgradeMode());
        resultContent.setOtaVersion(upgradeTask.getPackageVersion());
        resultContent.setTaskId(upgradeTask.getId());

        SystemUpgradeCheckResult<AndroidVDICheckResultContent> checkResult = new SystemUpgradeCheckResult<>();
        checkResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());
        checkResult.setContent(resultContent);
        return checkResult;
    }

    @Override
    public Object getSystemUpgradeMsg(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId, CbbSystemUpgradeModeEnums upgradeMode) {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        Assert.notNull(upgradeMode, "upgradeMode can not be null");

        AndroidVDICheckResultContent resultContent = new AndroidVDICheckResultContent();
        resultContent.setOtaMD5(upgradePackage.getFileMd5());
        resultContent.setOtaSeedLink(upgradePackage.getSeedPath());
        resultContent.setOtaSeedMD5(upgradePackage.getSeedMd5());
        resultContent.setUpgradeMode(upgradeMode);
        resultContent.setOtaVersion(upgradePackage.getPackageVersion());
        resultContent.setTaskId(upgradeTaskId);

        return resultContent;
    }

    @Override
    public void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        //开启BT分享
        btService.startBtShare(upgradePackage.getSeedPath());
    }

    @Override
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        //关闭BT分享
        btService.stopBtShare(upgradePackage.getSeedPath());
    }

    @Override
    protected boolean upgradingNumLimit() {
        // 不限制升级终端数量
        return false;
    }
}
