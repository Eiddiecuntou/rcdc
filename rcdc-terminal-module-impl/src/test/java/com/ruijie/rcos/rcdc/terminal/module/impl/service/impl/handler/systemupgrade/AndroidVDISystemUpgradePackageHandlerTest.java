package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.BtClientServiceImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AndroidVDISystemUpgradePackageHandlerTest {

    @Tested
    private AndroidVDISystemUpgradePackageHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private AndroidVDISystemUpgradePackageHelper systemUpgradePackageHelper;

    @Injectable
    private BtClientAPI btClientAPI;

    @Injectable
    private NetworkAPI networkAPI;

    @Injectable
    private BtClientService btClientService;

    /**
     * 测试获取系统升级包service
     */
    @Test
    public void testGetSystemUpgradePackageService() {
        TerminalSystemUpgradePackageService getService = handler.getSystemUpgradePackageService();
        assertEquals(terminalSystemUpgradePackageService, getService);
    }

    /**
     * 测试获取系统升级包信息
     */
    @Test
    public void testGetPackageInfo() throws BusinessException {

        new MockUp<FileOperateUtil>() {
            @Mock
            public void deleteFileByPath(String filePath) {
                return;
            }
        };

        UUID id = UUID.randomUUID();
        new MockUp<UUID>() {
            @Mock
            public UUID randomUUID() {
                return id;
            }
        };

        String filePath = "/aa/123.zip";
        String savePackageName = id.toString() + ".zip";
        String savePackagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName;

        TerminalUpgradeVersionFileInfo upgradeInfo = new TerminalUpgradeVersionFileInfo();
        upgradeInfo.setFileMD5("fileMd5");
        upgradeInfo.setVersion("version");

        SeedFileInfo seedFileInfo = new SeedFileInfo("/abc/seed.torrent", "123aaa");
        SeedFileInfoDTO seedFileInfoDTO = new SeedFileInfoDTO("/abc/seed.torrent", "123aaa");
        new Expectations(BtClientServiceImpl.class) {
            {
                systemUpgradePackageHelper.unZipPackage(filePath, savePackageName);
                result = savePackagePath;

                systemUpgradePackageHelper.checkVersionInfo(savePackagePath, Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
                result = upgradeInfo;

                btClientService.makeBtSeed(anyString, anyString);
                result = seedFileInfoDTO;
                
                
            }
        };

        TerminalUpgradeVersionFileInfo packageInfo = handler.getPackageInfo("123.zip", filePath);

        TerminalUpgradeVersionFileInfo expectedPackageInfo = new TerminalUpgradeVersionFileInfo();
        expectedPackageInfo.setFileMD5("fileMd5");
        expectedPackageInfo.setVersion("version");

        expectedPackageInfo.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        expectedPackageInfo.setPackageName("123.zip");
        expectedPackageInfo.setFilePath(savePackagePath);
        expectedPackageInfo.setSeedLink(seedFileInfo.getSeedFilePath());
        expectedPackageInfo.setSeedMD5(seedFileInfo.getSeedFileMD5());
        expectedPackageInfo.setFileSaveDir(Constants.TERMINAL_UPGRADE_OTA_PACKAGE);
        expectedPackageInfo.setRealFileName(id.toString() + ".zip");

        assertEquals(expectedPackageInfo, packageInfo);

        new Verifications() {
            {
                systemUpgradePackageHelper.unZipPackage(filePath, savePackageName);
                times = 1;

                systemUpgradePackageHelper.checkVersionInfo(savePackagePath, Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
                times = 1;

                FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
                times = 1;

                FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
                times = 1;
            }
        };
    }

    /**
     * 测试获取系统升级包信息 - 制作种子出现异常
     */
    @Test
    public void testGetPackageInfoMakeBtSeedHasException() throws BusinessException {

        new MockUp<FileOperateUtil>() {
            @Mock
            public void deleteFileByPath(String filePath) {
                return;
            }
        };

        UUID id = UUID.randomUUID();
        new MockUp<UUID>() {
            @Mock
            public UUID randomUUID() {
                return id;
            }
        };

        String filePath = "/aa/123.zip";
        String savePackageName = id.toString() + ".zip";
        String savePackagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName;

        TerminalUpgradeVersionFileInfo upgradeInfo = new TerminalUpgradeVersionFileInfo();
        upgradeInfo.setFileMD5("fileMd5");
        upgradeInfo.setVersion("version");

        new Expectations() {
            {
                systemUpgradePackageHelper.unZipPackage(filePath, savePackageName);
                result = savePackagePath;

                systemUpgradePackageHelper.checkVersionInfo(savePackagePath, Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
                result = upgradeInfo;

                btClientService.makeBtSeed(anyString, anyString);
                result = new BusinessException(BusinessKey.RCDC_TERMINAL_BT_MAKE_SEED_FILE_FAIL);

            }
        };

        try {
            handler.getPackageInfo("123.zip", filePath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_BT_MAKE_SEED_FILE_FAIL, e.getKey());
        }

        new Verifications() {
            {
                systemUpgradePackageHelper.unZipPackage(filePath, savePackageName);
                times = 1;

                systemUpgradePackageHelper.checkVersionInfo(savePackagePath, Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
                times = 1;

                FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName);
                times = 1;

                FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
                times = 1;

                FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
                times = 1;
            }
        };
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        upgradeEntity.setId(UUID.randomUUID());
        upgradeEntity.setPackageVersion("1.1.1");
        return upgradeEntity;
    }

    private TerminalSystemUpgradePackageEntity buildPackageEntity() {
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setFilePath("/aaa/aa.zip");
        packageEntity.setFileMd5("abc");
        packageEntity.setSeedPath("/bbb/bb.torrent");
        packageEntity.setSeedMd5("cbd");
        packageEntity.setPackageVersion("1.1.1");
        return packageEntity;
    }

}
