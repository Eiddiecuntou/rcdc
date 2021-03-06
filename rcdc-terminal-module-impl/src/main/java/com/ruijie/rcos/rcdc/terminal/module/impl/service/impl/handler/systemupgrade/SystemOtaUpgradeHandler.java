package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.PackageObtainModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
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
public class SystemOtaUpgradeHandler extends AbstractSystemUpgradeHandler<OtaCheckResultContent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemOtaUpgradeHandler.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private BtClientService btClientService;

    @Override
    protected TerminalSystemUpgradeService getSystemUpgradeService() {
        return systemUpgradeService;
    }

    @Override
    protected TerminalSystemUpgradePackageDAO getTerminalSystemUpgradePackageDAO() {
        return terminalSystemUpgradePackageDAO;
    }

    @Override
    protected SystemUpgradeCheckResult<OtaCheckResultContent> getCheckResult(TerminalSystemUpgradePackageEntity upgradePackage,
                                                                             TerminalSystemUpgradeEntity upgradeTask) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTask, "upgradeTask can not be null");

        LOGGER.info("?????????ota????????????");
        OtaCheckResultContent resultContent = new OtaCheckResultContent();
        resultContent.setPackageMD5(upgradePackage.getFileMd5());
        resultContent.setSeedLink(upgradePackage.getSeedPath());
        resultContent.setSeedMD5(upgradePackage.getSeedMd5());
        resultContent.setUpgradeMode(upgradePackage.getUpgradeMode());
        resultContent.setPackageVersion(upgradeTask.getPackageVersion());
        resultContent.setTaskId(upgradeTask.getId());
        resultContent.setFlashMode(upgradeTask.getFlashMode());
        resultContent.setSeedName(new File(upgradePackage.getSeedPath()).getName());
        resultContent.setPackageName(new File(upgradePackage.getFilePath()).getName());
        resultContent.setOtaScriptPath(upgradePackage.getOtaScriptPath());
        resultContent.setOtaScriptMD5(upgradePackage.getOtaScriptMd5());
        resultContent.setPackageObtainMode(PackageObtainModeEnums.OTA);

        SystemUpgradeCheckResult<OtaCheckResultContent> checkResult = new SystemUpgradeCheckResult<>();
        checkResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());
        checkResult.setPackageObtainMode(PackageObtainModeEnums.OTA);
        checkResult.setContent(resultContent);
        return checkResult;
    }

    @Override
    public Object getSystemUpgradeMsg(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");

        TerminalSystemUpgradeEntity systemUpgradeTask = terminalSystemUpgradeService.getSystemUpgradeTask(upgradeTaskId);

        OtaCheckResultContent resultContent = new OtaCheckResultContent();
        resultContent.setPackageMD5(upgradePackage.getFileMd5());
        resultContent.setSeedLink(upgradePackage.getSeedPath());
        resultContent.setSeedMD5(upgradePackage.getSeedMd5());
        resultContent.setUpgradeMode(upgradePackage.getUpgradeMode());
        resultContent.setPackageVersion(upgradePackage.getPackageVersion());
        resultContent.setTaskId(upgradeTaskId);
        resultContent.setFlashMode(systemUpgradeTask.getFlashMode());
        resultContent.setSeedName(new File(upgradePackage.getSeedPath()).getName());
        resultContent.setPackageName(new File(upgradePackage.getFilePath()).getName());
        resultContent.setOtaScriptPath(upgradePackage.getOtaScriptPath());
        resultContent.setOtaScriptMD5(upgradePackage.getOtaScriptMd5());

        return resultContent;
    }

    @Override
    public void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        // ??????BT??????
        LOGGER.info("????????????????????????bt??????");
        btClientService.startBtShare(upgradePackage.getFilePath(), upgradePackage.getSeedPath());
    }

    @Override
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage, TerminalSystemUpgradeEntity upgradeEntity)
            throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeEntity, "upgradeEntity can not be null");

        // ??????BT??????
        LOGGER.info("????????????????????????bt??????");
        btClientService.stopBtShare(upgradePackage.getSeedPath());
    }

    @Override
    protected boolean upgradingNumLimit() {
        // ???????????????????????????
        return false;
    }

    @Override
    protected boolean enableUpgradeOnlyOnce(CbbTerminalTypeEnums terminalType) {
        if (terminalType == CbbTerminalTypeEnums.VDI_ANDROID) {
            // ??????ota????????????????????????????????????????????????
            return false;
        }

        return true;
    }
}
