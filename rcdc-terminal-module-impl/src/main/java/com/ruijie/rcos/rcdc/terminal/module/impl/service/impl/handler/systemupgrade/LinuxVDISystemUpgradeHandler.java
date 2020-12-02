package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.PackageObtainModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.SambaInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;
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

    @Autowired
    private TerminalSystemUpgradeTerminalDAO terminalSystemUpgradeTerminalDAO;

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
                buildLinuxVDICheckResultContent(upgradePackage, upgradeTask.getId(), pxeSambaInfo);

        SystemUpgradeCheckResult<LinuxVDICheckResultContent> checkResult = new SystemUpgradeCheckResult<>();
        checkResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());
        checkResult.setPackageObtainMode(PackageObtainModeEnums.SAMBA);
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
    public void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage, TerminalSystemUpgradeEntity upgradeEntity)
            throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeEntity, "upgradeEntity can not be null");
        Assert.notNull(upgradeEntity.getId(), "upgradeTask id can not be null");

        // 关闭刷机相关服务
        terminalSystemUpgradeSupportService.closeSystemUpgradeService();

        // 释放占用升级位置
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = terminalSystemUpgradeTerminalDAO.findBySysUpgradeId(upgradeEntity.getId());
        if (CollectionUtils.isEmpty(upgradeTerminalList)) {
            return;
        }

        upgradeTerminalList.forEach(upgradeTerminal -> releaseUpgradeQuota(upgradeTerminal.getTerminalId()));
    }

    @Override
    public Object getSystemUpgradeMsg(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId)
            throws BusinessException {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");

        SambaInfoDTO pxeSambaInfo = sambaInfoService.getPxeSambaInfo();

        LinuxVDICheckResultContent resultContent = buildLinuxVDICheckResultContent(upgradePackage, upgradeTaskId, pxeSambaInfo);

        return resultContent;
    }

    private LinuxVDICheckResultContent buildLinuxVDICheckResultContent(TerminalSystemUpgradePackageEntity upgradePackage,
                                                                       UUID upgradeTaskId, SambaInfoDTO pxeSambaInfo) {
        LinuxVDICheckResultContent resultContent = new LinuxVDICheckResultContent();
        resultContent.setImgName(upgradePackage.getImgName());
        resultContent.setIsoVersion(upgradePackage.getPackageVersion());
        resultContent.setPackageVersion(upgradePackage.getPackageVersion());
        resultContent.setUpgradeMode(upgradePackage.getUpgradeMode());
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

    @Override
    protected boolean enableUpgradeOnlyOnce(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        return true;
    }
}
