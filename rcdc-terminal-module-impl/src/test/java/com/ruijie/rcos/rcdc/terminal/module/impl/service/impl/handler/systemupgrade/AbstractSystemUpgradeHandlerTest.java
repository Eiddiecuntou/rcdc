package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUpgradeTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.PackageObtainModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AbstractSystemUpgradeHandlerTest {

    @Injectable
    private TerminalSystemUpgradePackageDAO systemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService systemUpgradeService;

    @Injectable
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;


    /**
     * 测试CheckSystemUpgrade - 不能升级
     */
    @Test
    public void testCheckSystemUpgradeCannotUpgrade() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();

        TerminalEntity terminalEntity = buildTerminalEntity();

        new MockUp<TestedSystemUpgradeHandler>() {
            @Mock
            public boolean isTerminalEnableUpgrade(TerminalEntity terminal, CbbTerminalTypeEnums terminalType) {
                return false;
            }
        };

        SystemUpgradeCheckResult checkResult = handler.checkSystemUpgrade(CbbTerminalTypeEnums.VDI_LINUX, terminalEntity);

        SystemUpgradeCheckResult expectedResult = new SystemUpgradeCheckResult();
        expectedResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE.getResult());
        expectedResult.setContent(null);
        expectedResult.setPackageObtainMode(PackageObtainModeEnums.SAMBA);
        assertEquals(expectedResult, checkResult);

        new Verifications() {
            {
                systemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 0;

                systemUpgradeService.getUpgradingSystemUpgradeTaskByPackageId((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试testCheckSystemUpgradeCannotUpgrade2 - 不能升级
     */
    @Test
    public void testCheckSystemUpgradeCannotUpgrade2() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();

        TerminalEntity terminalEntity = buildTerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);

        new MockUp<TestedSystemUpgradeHandler>() {
            @Mock
            public boolean isTerminalEnableUpgrade(TerminalEntity terminal, CbbTerminalTypeEnums terminalType) {
                return false;
            }
        };

        SystemUpgradeCheckResult checkResult = handler.checkSystemUpgrade(CbbTerminalTypeEnums.IDV_LINUX, terminalEntity);

        SystemUpgradeCheckResult expectedResult = new SystemUpgradeCheckResult();
        expectedResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE.getResult());
        expectedResult.setContent(null);
        expectedResult.setPackageObtainMode(PackageObtainModeEnums.OTA);
        assertEquals(expectedResult, checkResult);

        new Verifications() {
            {
                systemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 0;

                systemUpgradeService.getUpgradingSystemUpgradeTaskByPackageId((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试CheckSystemUpgrade
     */
    @Test
    public void testCheckSystemUpgrade() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();

        TerminalEntity terminalEntity = buildTerminalEntity();

        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();

        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = buildUpgradeTerminalEntity(terminalEntity, upgradeEntity);
        upgradeTerminalEntity.setState(CbbSystemUpgradeStateEnums.WAIT);

        new MockUp<TestedSystemUpgradeHandler>() {
            @Mock
            public boolean isTerminalEnableUpgrade(TerminalEntity terminal, CbbTerminalTypeEnums terminalType) {
                return true;
            }

            @Mock
            public boolean upgradingNumLimit() {
                return false;
            }
        };


        new Expectations() {
            {

                systemUpgradeService.getUpgradingSystemUpgradeTaskByPackageId(packageEntity.getId());
                result = upgradeEntity;

                systemUpgradeService.getSystemUpgradeTerminalByTaskId(terminalEntity.getTerminalId(), upgradeEntity.getId());
                result = upgradeTerminalEntity;

            }
        };

        SystemUpgradeCheckResult checkResult = handler.checkSystemUpgrade(CbbTerminalTypeEnums.VDI_LINUX, terminalEntity);

        SystemUpgradeCheckResult  expectedResult = new SystemUpgradeCheckResult();
        expectedResult.setSystemUpgradeCode(1);

        assertEquals(expectedResult, checkResult);

        new Verifications() {
            {
                systemUpgradeService.getUpgradingSystemUpgradeTaskByPackageId(packageEntity.getId());
                times = 2;

                systemUpgradeService.getSystemUpgradeTerminalByTaskId(terminalEntity.getTerminalId(), upgradeEntity.getId());
                times = 1;
            }
        };
    }

    /**
     * 测试添加升级任务后
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAfterAddSystemUpgrade() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        handler.afterAddSystemUpgrade(packageEntity);

        // 空实现，直接校验通过
        assertTrue(true);
    }

    /**
     * 测试关闭升级任务后
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAfterCloseystemUpgrade() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();
        TerminalSystemUpgradePackageEntity packageEntity = buildPackageEntity();
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();
        handler.afterCloseSystemUpgrade(packageEntity, upgradeEntity);

        // 空实现，直接校验通过
        assertTrue(true);
    }

    /**
     * 测试关闭升级任务后
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testReleaseUpgradeQuota() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();
        handler.releaseUpgradeQuota("123");

        // 空实现，直接校验通过
        assertTrue(true);
    }

    /**
     * 测试判断是否有升级位置
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckAndHoldUpgradeQuota() throws BusinessException {
        TestedSystemUpgradeHandler handler = new TestedSystemUpgradeHandler();
        boolean canUpgrade = handler.checkAndHoldUpgradeQuota("123");

        assertTrue(canUpgrade);
    }

    private TerminalSystemUpgradeTerminalEntity buildUpgradeTerminalEntity(TerminalEntity terminalEntity, TerminalSystemUpgradeEntity upgradeEntity) {
        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalEntity.setTerminalId(terminalEntity.getTerminalId());
        upgradeTerminalEntity.setSysUpgradeId(upgradeEntity.getId());
        upgradeTerminalEntity.setState(CbbSystemUpgradeStateEnums.WAIT);
        return upgradeTerminalEntity;
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setId(UUID.randomUUID());
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.FINISH);

        return upgradeEntity;
    }

    private TerminalSystemUpgradePackageEntity buildPackageEntity() {
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setId(UUID.randomUUID());

        return packageEntity;
    }

    private TerminalEntity buildTerminalEntity() {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setTerminalId("123");
        terminalEntity.setGroupId(UUID.randomUUID());
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");

        return terminalEntity;
    }

    /**
     * Description: 测试类
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019/11/7
     *
     * @author nt
     */
    private class TestedSystemUpgradeHandler extends AbstractSystemUpgradeHandler {

        @Override
        protected SystemUpgradeCheckResult getCheckResult(TerminalSystemUpgradePackageEntity upgradePackage, TerminalSystemUpgradeEntity upgradeTask)
                throws BusinessException {
            SystemUpgradeCheckResult checkResult = new SystemUpgradeCheckResult();
            checkResult.setSystemUpgradeCode(1);
            return checkResult;
        }

        @Override
        protected TerminalSystemUpgradeService getSystemUpgradeService() {
            return systemUpgradeService;
        }

        @Override
        protected TerminalSystemUpgradePackageDAO getTerminalSystemUpgradePackageDAO() {
            return systemUpgradePackageDAO;
        }

        @Override
        protected boolean upgradingNumLimit() {
            return false;
        }

        @Override
        public Object getSystemUpgradeMsg(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId) throws BusinessException {
            // for test
            return null;
        }

        @Override
        protected boolean enableUpgradeOnlyOnce(CbbTerminalTypeEnums terminalType) {
            return terminalType != CbbTerminalTypeEnums.VDI_ANDROID;
        }
    }

}
