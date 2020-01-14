package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.SambaInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
@Service
public class LinuxVDISystemUpgradeHandler extends AbstractSystemUpgradeHandler<LinuxVDICheckResultContent> {

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private LinuxVDISystemUpgradeFileClearHandler upgradeFileClearHandler;

    @Autowired
    private TerminalSystemUpgradeSupportService terminalSystemUpgradeSupportService;

    @Autowired
    private SambaInfoService sambaInfoService;

    @Override
    protected TerminalSystemUpgradeService getSystemUpgradeService() {
        return systemUpgradeService;
    }

    @Override
    protected TerminalSystemUpgradePackageDAO getTerminalSystemUpgradePackageDAO() {
        return terminalSystemUpgradePackageDAO;
    }

    @Override
    protected SystemUpgradeCheckResult<LinuxVDICheckResultContent> getCheckResult(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalSystemUpgradeEntity upgradeTask) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTask, "upgradeTask can not be null");

        SambaInfoDTO pxeSambaInfo = sambaInfoService.getPxeSambaInfo();

        LinuxVDICheckResultContent resultContent =
                buildLinuxVDICheckResultContent(upgradePackage, upgradeTask.getId(), upgradeTask.getUpgradeMode(), pxeSambaInfo);

        SystemUpgradeCheckResult<LinuxVDICheckResultContent> checkResult = new SystemUpgradeCheckResult<>();
        checkResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());
        checkResult.setContent(resultContent);
        return checkResult;
    }

    @Override
    public void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradePackage.getId(), "packageId can not be null");

        // 清理终端升级相关文件
        upgradeFileClearHandler.clear();

        // 开启刷机相关服务
        terminalSystemUpgradeSupportService.openSystemUpgradeService(upgradePackage);
    }

    @Override
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");

        // 开启刷机相关服务
        terminalSystemUpgradeSupportService.closeSystemUpgradeService();
    }

    @Override
    public Object getSystemUpgradeMsg(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId, CbbSystemUpgradeModeEnums upgradeMode)
            throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        Assert.notNull(upgradeMode, "upgradeMode can not be null");

        SambaInfoDTO pxeSambaInfo = sambaInfoService.getPxeSambaInfo();

        LinuxVDICheckResultContent resultContent = buildLinuxVDICheckResultContent(upgradePackage, upgradeTaskId, upgradeMode, pxeSambaInfo);

        return resultContent;
    }

    private LinuxVDICheckResultContent buildLinuxVDICheckResultContent(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId,
            CbbSystemUpgradeModeEnums upgradeMode, SambaInfoDTO pxeSambaInfo) {
        LinuxVDICheckResultContent resultContent = new LinuxVDICheckResultContent();
        resultContent.setImgName(upgradePackage.getImgName());
        resultContent.setIsoVersion(upgradePackage.getPackageVersion());
        resultContent.setPackageVersion(upgradePackage.getPackageVersion());
        resultContent.setUpgradeMode(upgradeMode);
        resultContent.setTaskId(upgradeTaskId);
        resultContent.setSambaIp(pxeSambaInfo.getIp());
        resultContent.setSambaPassword(AesUtil.encrypt(pxeSambaInfo.getPassword(), Constants.TERMINAL_PXE_SAMBA_PASSWORD_AES_KEY));
        resultContent.setSambaPort(pxeSambaInfo.getPort());
        resultContent.setSambaUserName(pxeSambaInfo.getUserName());
        resultContent.setSambaFilePath(File.separator + pxeSambaInfo.getFilePath() + Constants.PXE_ISO_SAMBA_LINUX_VDI_RELATE_PATH);
        resultContent.setUpgradePackageName(new File(upgradePackage.getFilePath()).getName());
        return resultContent;
    }

    @Override
    public boolean checkAndHoldUpgradeQuota(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");

        return SystemUpgradeGlobal.checkAndHoldUpgradeQuota(terminalId);
    }

    @Override
    protected boolean upgradingNumLimit() {
        return SystemUpgradeGlobal.isUpgradingNumExceedLimit();
    }

    @Override
    public void releaseUpgradeQuota(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");

        SystemUpgradeGlobal.releaseUpgradeQuota(terminalId);
    }
}
