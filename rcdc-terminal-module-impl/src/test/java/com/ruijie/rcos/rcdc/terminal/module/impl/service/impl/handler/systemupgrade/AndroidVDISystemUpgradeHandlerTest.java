package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Injectable;
import mockit.Tested;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AndroidVDISystemUpgradeHandlerTest {

    @Tested
    private AndroidVDISystemUpgradeHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService systemUpgradeService;

    @Injectable
    private BtService btService;

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

        SystemUpgradeCheckResult<AndroidVDICheckResultContent> checkResult = handler.getCheckResult(packageEntity, upgradeEntity);

        SystemUpgradeCheckResult<AndroidVDICheckResultContent> expectedResult = new SystemUpgradeCheckResult<>();
        AndroidVDICheckResultContent content = buildExpectedAndroidVDICheckResultContent(upgradeEntity);
        expectedResult.setContent(content);
        expectedResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());

        assertEquals(expectedResult, checkResult);
    }

    /**
     * 测试获取系统升级消息
     */
    @Test
    public void testGetSystemUpgradeMsg() {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        AndroidVDICheckResultContent result =
                (AndroidVDICheckResultContent) handler.getSystemUpgradeMsg(packageEntity, upgradeEntity.getId(), upgradeEntity.getUpgradeMode());

        AndroidVDICheckResultContent expectContent = buildExpectedAndroidVDICheckResultContent(upgradeEntity);

        assertEquals(expectContent, result);
    }


    /**
     *  测试开启升级任务后
     *
     * @throws BusinessException
     */
    @Test
    public void testAfterAddSystemUpgrade() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();

        handler.afterAddSystemUpgrade(packageEntity);

        new Verifications() {
            {
                btService.startBtShare(packageEntity.getSeedPath(), packageEntity.getFilePath());
                times = 1;
            }
        };
    }

    /**
     *  测试关闭升级任务后
     *
     * @throws BusinessException
     */
    @Test
    public void testAfterCloseSystemUpgrade() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();

        handler.afterCloseSystemUpgrade(packageEntity);

        new Verifications() {
            {
                btService.stopBtShare(packageEntity.getSeedPath());
                times = 1;
            }
        };
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

    private AndroidVDICheckResultContent buildExpectedAndroidVDICheckResultContent(TerminalSystemUpgradeEntity upgradeEntity) {
        AndroidVDICheckResultContent content = new AndroidVDICheckResultContent();
        content.setPackageMD5("abc");
        content.setPackageName("aa.zip");
        content.setPackageVersion("1.1.1");
        content.setSeedLink("/bbb/bb.torrent");
        content.setSeedName("bb.torrent");
        content.setSeedMD5("cbd");
        content.setTaskId(upgradeEntity.getId());
        content.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        return content;
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
