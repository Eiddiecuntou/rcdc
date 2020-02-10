package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseStartBtShareRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseStopBtShareRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
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
 * @author nt
 */
@Service
public class AndroidVDISystemUpgradeHandler extends AbstractSystemUpgradeHandler<AndroidVDICheckResultContent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradeHandler.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private BtClientAPI btClientAPI;

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
            TerminalSystemUpgradeEntity upgradeTask) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTask, "upgradeTask can not be null");

        AndroidVDICheckResultContent resultContent = new AndroidVDICheckResultContent();
        resultContent.setPackageMD5(upgradePackage.getFileMd5());
        resultContent.setSeedLink(upgradePackage.getSeedPath());
        resultContent.setSeedMD5(upgradePackage.getSeedMd5());
        resultContent.setUpgradeMode(upgradeTask.getUpgradeMode());
        resultContent.setPackageVersion(upgradeTask.getPackageVersion());
        resultContent.setTaskId(upgradeTask.getId());
        resultContent.setSeedName(new File(upgradePackage.getSeedPath()).getName());
        resultContent.setPackageName(new File(upgradePackage.getFilePath()).getName());

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
        resultContent.setPackageMD5(upgradePackage.getFileMd5());
        resultContent.setSeedLink(upgradePackage.getSeedPath());
        resultContent.setSeedMD5(upgradePackage.getSeedMd5());
        resultContent.setUpgradeMode(upgradeMode);
        resultContent.setPackageVersion(upgradePackage.getPackageVersion());
        resultContent.setTaskId(upgradeTaskId);
        resultContent.setSeedName(new File(upgradePackage.getSeedPath()).getName());
        resultContent.setPackageName(new File(upgradePackage.getFilePath()).getName());

        return resultContent;
    }

    @Override
    public void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        // 开启BT分享
        LOGGER.info("开启安卓系统升级bt分享");
        BaseStartBtShareRequest apiRequest = new BaseStartBtShareRequest();
        apiRequest.setSeedFilePath(upgradePackage.getSeedPath());
        apiRequest.setFilePath(upgradePackage.getFilePath());
        btClientAPI.startBtShare(apiRequest);
    }

    @Override
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage, TerminalSystemUpgradeEntity upgradeEntity)
            throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeEntity, "upgradeEntity can not be null");

        // 关闭BT分享
        LOGGER.info("关闭安卓系统升级bt分享");
        BaseStopBtShareRequest apiRequest = new BaseStopBtShareRequest();
        apiRequest.setSeedFilePath(upgradePackage.getSeedPath());
        btClientAPI.stopBtShare(apiRequest);
    }

    @Override
    protected boolean upgradingNumLimit() {
        // 不限制升级终端数量
        return false;
    }
}
