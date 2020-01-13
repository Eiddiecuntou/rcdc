package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
@Service
public class AndroidVDISystemUpgradePackageHandler extends AbstractSystemUpgradePackageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradePackageHandler.class);

    private static final String OTA_SUFFIX = ".zip";

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private AndroidVDISystemUpgradePackageHelper systemUpgradePackageHelper;

    @Override
    protected TerminalSystemUpgradePackageService getSystemUpgradePackageService() {
        return terminalSystemUpgradePackageService;
    }

    @Override
    protected TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");
        String savePackageName = UUID.randomUUID() + OTA_SUFFIX;
        try {
            // 解压zip文件
            String packagePath = systemUpgradePackageHelper.unZipPackage(filePath, savePackageName);
            // 校验version信息
            TerminalUpgradeVersionFileInfo upgradeInfo =
                    systemUpgradePackageHelper.checkVersionInfo(packagePath, Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
            // 制作Bt种子
            SeedFileInfo seedFileInfo = systemUpgradePackageHelper.makeBtSeed(packagePath);

            upgradeInfo.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
            upgradeInfo.setPackageName(fileName);
            upgradeInfo.setFilePath(packagePath);
            upgradeInfo.setSeedLink(seedFileInfo.getSeedFilePath());
            upgradeInfo.setSeedMD5(seedFileInfo.getSeedFileMD5());
            upgradeInfo.setFileSaveDir(Constants.TERMINAL_UPGRADE_OTA_PACKAGE);
            upgradeInfo.setRealFileName(savePackageName);
            return upgradeInfo;
        } catch (Exception e) {
            FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName);
            throw e;
        } finally {
            FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
            FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
        }
    }

}
