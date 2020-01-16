package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
@RunWith(SkyEngineRunner.class)
public class LinuxVDISystemUpgradeHandlerTest {

    @Tested
    private LinuxVDISystemUpgradeHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService systemUpgradeService;

    @Injectable
    private LinuxVDISystemUpgradeFileClearHandler upgradeFileClearHandler;

    @Injectable
    private TerminalSystemUpgradeSupportService terminalSystemUpgradeSupportService;

    @Injectable
    private SambaInfoService sambaInfoService;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO terminalSystemUpgradeTerminalDAO;

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
        TerminalSystemUpgradePackageEntity upgradePackage = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        SambaInfoDTO pxeSambaInfo = buildPxeSambaInfo();

        new MockUp<AesUtil>() {
            @Mock
            public String encrypt(String str, String key) {
                return "111";
            }
        };

        new Expectations() {
            {
                sambaInfoService.getPxeSambaInfo();
                result = pxeSambaInfo;
            }
        };

        SystemUpgradeCheckResult<LinuxVDICheckResultContent> checkResult = handler.getCheckResult(upgradePackage, upgradeEntity);

        SystemUpgradeCheckResult<LinuxVDICheckResultContent> expectedResult = new SystemUpgradeCheckResult<>();
        LinuxVDICheckResultContent content = buildExpectedLinuxVDICheckResultContent(upgradePackage, upgradeEntity, pxeSambaInfo);

        expectedResult.setContent(content);
        expectedResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NEED_UPGRADE.getResult());

        assertEquals(expectedResult, checkResult);
    }



    /**
     * 测试获取系统升级消息
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetSystemUpgradeMsg() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();
        UUID upgradeTaskId = upgradeEntity.getId();
        CbbSystemUpgradeModeEnums mode = upgradeEntity.getUpgradeMode();

        new MockUp<AesUtil>() {
            @Mock
            public String encrypt(String str, String key) {
                return "111";
            }
        };

        SambaInfoDTO pxeSambaInfo = buildPxeSambaInfo();
        new Expectations() {
            {
                sambaInfoService.getPxeSambaInfo();
                result = pxeSambaInfo;

            }
        };

        LinuxVDICheckResultContent result = (LinuxVDICheckResultContent) handler.getSystemUpgradeMsg(packageEntity, upgradeTaskId, mode);

        LinuxVDICheckResultContent expectContent = buildExpectedLinuxVDICheckResultContent(packageEntity, upgradeEntity, pxeSambaInfo);

        assertEquals(expectContent, result);
    }

    /**
     * 测试 开启升级任务后
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAfterAddSystemUpgrade() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();

        handler.afterAddSystemUpgrade(packageEntity);

        new Verifications() {
            {
                upgradeFileClearHandler.clear();
                times = 1;

                terminalSystemUpgradeSupportService.openSystemUpgradeService(packageEntity);
                times = 1;
            }
        };
    }

    /**
     * 测试 关闭升级任务后
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAfterCloseSystemUpgrade() throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();
        upgradeEntity.setId(UUID.randomUUID());

        handler.afterCloseSystemUpgrade(packageEntity, upgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeSupportService.closeSystemUpgradeService();
                times = 1;

                terminalSystemUpgradeTerminalDAO.findBySysUpgradeId(upgradeEntity.getId());
                times = 1;
            }
        };
    }

    /**
     * 测试 检测并且获取升级位置
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckAndHoldUpgradeQuota() throws BusinessException {
        String terminalId = "123";

        handler.checkAndHoldUpgradeQuota(terminalId);

        new Verifications() {
            {
                SystemUpgradeGlobal.checkAndHoldUpgradeQuota(terminalId);
                times = 1;
            }
        };
    }

    /**
     * 测试 判断升级是否超出限制
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUpgradingNumLimit() throws BusinessException {

        handler.upgradingNumLimit();

        new Verifications() {
            {
                SystemUpgradeGlobal.isUpgradingNumExceedLimit();
                times = 1;
            }
        };
    }

    /**
     * 测试释放升级位置
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testReleaseUpgradeQuota() throws BusinessException {
        String terminalId = "123";

        handler.releaseUpgradeQuota(terminalId);

        new Verifications() {
            {
                SystemUpgradeGlobal.releaseUpgradeQuota(terminalId);
                times = 1;
            }
        };
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        upgradeEntity.setId(UUID.randomUUID());
        upgradeEntity.setPackageVersion("1.1.1");
        upgradeEntity.setPackageName("aa.zip");

        return upgradeEntity;
    }

    private TerminalSystemUpgradePackageEntity buildPackageEntity() {
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setId(UUID.randomUUID());
        packageEntity.setFilePath("/aaa/aa.zip");
        packageEntity.setFileMd5("abc");
        packageEntity.setSeedPath("/bbb/bb.torrent");
        packageEntity.setSeedMd5("cbd");

        return packageEntity;
    }

    private LinuxVDICheckResultContent buildExpectedLinuxVDICheckResultContent(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalSystemUpgradeEntity upgradeEntity, SambaInfoDTO pxeSambaInfo) {

        LinuxVDICheckResultContent resultContent = new LinuxVDICheckResultContent();
        resultContent.setImgName(upgradePackage.getImgName());
        resultContent.setIsoVersion(upgradePackage.getPackageVersion());
        resultContent.setPackageVersion(upgradePackage.getPackageVersion());
        resultContent.setUpgradeMode(upgradeEntity.getUpgradeMode());
        resultContent.setTaskId(upgradeEntity.getId());
        resultContent.setSambaIp(pxeSambaInfo.getIp());
        resultContent.setSambaPassword(AesUtil.encrypt(pxeSambaInfo.getPassword(), Constants.TERMINAL_PXE_SAMBA_PASSWORD_AES_KEY));
        resultContent.setSambaPort(pxeSambaInfo.getPort());
        resultContent.setSambaUserName(pxeSambaInfo.getUserName());
        resultContent.setSambaFilePath(File.separator + pxeSambaInfo.getFilePath() + Constants.PXE_ISO_SAMBA_LINUX_VDI_RELATE_PATH);
        resultContent.setUpgradePackageName(new File(upgradePackage.getFilePath()).getName());
        return resultContent;
    }

    private SambaInfoDTO buildPxeSambaInfo() {
        SambaInfoDTO sambaInfoDTO = new SambaInfoDTO();
        sambaInfoDTO.setFilePath("pxe");
        sambaInfoDTO.setPort("123");
        sambaInfoDTO.setIp("172.1.1.1");
        sambaInfoDTO.setPassword("abc");
        sambaInfoDTO.setUserName("userName");

        return sambaInfoDTO;
    }

}
