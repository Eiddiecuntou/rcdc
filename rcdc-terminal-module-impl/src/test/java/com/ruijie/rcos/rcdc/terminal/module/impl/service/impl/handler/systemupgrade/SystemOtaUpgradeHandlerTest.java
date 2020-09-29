package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbFlashModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.BtClientServiceImpl;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class SystemOtaUpgradeHandlerTest {

    @Tested
    private SystemOtaUpgradeHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService systemUpgradeService;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private BtClientAPI btClientAPI;

    @Injectable
    private BtClientService btClientService;

    @Injectable
    private CbbTerminalSystemUpgradeAPI cbbTerminalSystemUpgradeAPI;

    /**
     * 测试获取系统升级service
     */
    @Test
    public void testGetSystemUpgradeService() {
        TerminalSystemUpgradeService getService = handler.getSystemUpgradeService();
        assertEquals(systemUpgradeService, getService);
    }

    /**
     * 测试获取系统升级包DAO
     */
    @Test
    public void testGetTerminalSystemUpgradePackageDAO() {
        TerminalSystemUpgradePackageDAO getDAO = handler.getTerminalSystemUpgradePackageDAO();
        assertEquals(terminalSystemUpgradePackageDAO, getDAO);
    }

    /**
     * 测试获取检测结果
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetCheckResult() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        SystemUpgradeCheckResult<OtaCheckResultContent> checkResult = handler.getCheckResult(packageEntity, upgradeEntity);

        SystemUpgradeCheckResult<OtaCheckResultContent> expectedResult = new SystemUpgradeCheckResult<>();
        OtaCheckResultContent content = buildExpectedAndroidVDICheckResultContent(upgradeEntity);
        expectedResult.setContent(content);
        expectedResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());

        assertEquals(expectedResult, checkResult);
    }

    /**
     * 测试获取系统升级消息
     */
    @Test
    public void testGetSystemUpgradeMsg() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        TerminalSystemUpgradeEntity terminalSystemUpgradeEntity = new TerminalSystemUpgradeEntity();
        terminalSystemUpgradeEntity.setId(upgradeEntity.getId());
        terminalSystemUpgradeEntity.setFlashMode(CbbFlashModeEnums.FAST);
        new Expectations() {
            {
                terminalSystemUpgradeService.getSystemUpgradeTask(upgradeEntity.getId());
                result = terminalSystemUpgradeEntity;
            }
        };
        OtaCheckResultContent result =
                (OtaCheckResultContent) handler.getSystemUpgradeMsg(packageEntity, upgradeEntity.getId());

        OtaCheckResultContent expectContent = buildExpectedAndroidVDICheckResultContent(upgradeEntity);

        assertEquals(expectContent, result);
    }


    /**
     *  测试关闭升级任务后
     *
     */
    @Test
    public void testUpgradingNumLimit() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();

        boolean isExceedLimit = handler.upgradingNumLimit();

        assertTrue(!isExceedLimit);
    }

    private OtaCheckResultContent buildExpectedAndroidVDICheckResultContent(TerminalSystemUpgradeEntity upgradeEntity) {
        OtaCheckResultContent content = new OtaCheckResultContent();
        content.setPackageMD5("abc");
        content.setPackageName("aa.zip");
        content.setPackageVersion("1.1.1");
        content.setSeedLink("/bbb/bb.torrent");
        content.setSeedName("bb.torrent");
        content.setSeedMD5("cbd");
        content.setTaskId(upgradeEntity.getId());
        content.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        content.setFlashMode(CbbFlashModeEnums.FAST);
        return content;
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setId(UUID.randomUUID());
        upgradeEntity.setPackageVersion("1.1.1");
        upgradeEntity.setFlashMode(CbbFlashModeEnums.FAST);
        return upgradeEntity;
    }

    private TerminalSystemUpgradePackageEntity buildPackageEntity() {
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setFilePath("/aaa/aa.zip");
        packageEntity.setFileMd5("abc");
        packageEntity.setSeedPath("/bbb/bb.torrent");
        packageEntity.setSeedMd5("cbd");
        packageEntity.setPackageVersion("1.1.1");
        packageEntity.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        return packageEntity;
    }

    /**
     * 添加系统升级任务后
     * @throws BusinessException 异常
     */
    @Test
    public void testAfterAddSystemUpgrade() throws BusinessException {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setFilePath("filePath");
        upgradePackage.setSeedPath("seedPath.torrent");

        new Expectations() {
            {
                btClientService.startBtShare(anyString, anyString);
            }
        };

        try {
            handler.afterAddSystemUpgrade(upgradePackage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * 关闭系统升级任务后
     * @throws BusinessException 异常
     */
    @Test
    public void testAfterCloseSystemUpgrade() throws BusinessException {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setSeedPath("seedPath.torrent");
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();

        new Expectations(BtClientServiceImpl.class) {
            {
                btClientService.stopBtShare(anyString);
            }
        };

        try {
            handler.afterCloseSystemUpgrade(upgradePackage, upgradeEntity);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * testEnableUpgradeOnlyOnce
     */
    @Test
    public void testEnableUpgradeOnlyOnce() {

        boolean enableUpgradeOnlyOnce = handler.enableUpgradeOnlyOnce(CbbTerminalTypeEnums.IDV_LINUX);
        assertEquals(true, enableUpgradeOnlyOnce);

        boolean enableUpgradeOnlyOnce2 = handler.enableUpgradeOnlyOnce(CbbTerminalTypeEnums.VDI_ANDROID);
        assertEquals(false, enableUpgradeOnlyOnce2);
    }
}
